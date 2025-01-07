package com.sap.engine.services.iiop.CORBA.interceptors;

import com.sap.engine.services.iiop.internal.interceptors.InterceptorsStorage;
import com.sap.engine.services.iiop.internal.interceptors.MinorCodes;
import com.sap.engine.services.iiop.internal.ClientORB;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.CORBA.portable.DelegateImpl;
import com.sap.engine.services.iiop.CORBA.IOR;
import org.omg.CORBA.*;
import org.omg.CORBA.portable.ObjectImpl;
import org.omg.IOP.CodecFactory;
import org.omg.PortableInterceptor.*;
import org.omg.PortableInterceptor.ORBInitInfoPackage.DuplicateName;
import org.omg.PortableInterceptor.ORBInitInfoPackage.InvalidName;

public final class ORBInitInfoImpl extends org.omg.CORBA.LocalObject implements ORBInitInfo {

  // The ORB we are initializing
  private transient ORB orb;
  // The arguments passed to ORB_init
  private String[] args;
  // The ID of the ORB being initialized
  private String orbId;
  // The CodecFactory
  private CodecFactory codecFactory;
  // The current stage of initialization
  private int stage = STAGE_PRE_INIT;

  private boolean staticPropeties;
  // The pre-initialization stage (pre_init() being called)
  public static final int STAGE_PRE_INIT = 0;
  public static final int STAGE_POST_INIT = 1;
  public static final int STAGE_CLOSED = 2; // Reject all calls - this object should no longer be around.
  // The description for the OBJECT_NOT_EXIST exception in STAGE_CLOSED
  private static final String MESSAGE_ORBINITINFO_INVALID = "ORBInitInfo object is only valid during ORB_init";

  /**
   * Creates a new ORBInitInfoImpl object (scoped to package)
   *
   * @param args The arguments passed to ORB_init.
   */
  public ORBInitInfoImpl(ORB orb, String[] args, String orbId, CodecFactory codecFactory, boolean staticPropeties) {
    this.orb = orb;
    this.args = args;
    this.orbId = orbId;
    this.codecFactory = codecFactory;
    this.staticPropeties = staticPropeties;
  }

  /**
   * Sets the current stage we are in.  This limits access to certain
   * functionality.
   */
  public void setStage(int stage) {
    this.stage = stage;
  }

  public String[] arguments() {
    checkStage();
    return args;
  }

  public String orb_id() {
    checkStage();
    return orbId;
  }

  /**
   * This attribute is the IOP::CodecFactory.  The CodecFactory is normally
   * obtained via a call to ORB::resolve_initial_references( "CodecFactory" )
   * but since the ORB is not yet available and Interceptors, particularly
   * when processing service contexts, will require a Codec, a means of
   * obtaining a Codec is necessary during ORB intialization.
   */
  public CodecFactory codec_factory() {
    checkStage();
    return codecFactory;
  }

  /**
   * See orbos/99-12-02, Chapter 11, Dynamic Initial References on page
   * 11-81.  This operation is identical to ORB::register_initial_reference
   * described there.  This same functionality exists here because the ORB,
   * not yet fully initialized, is not yet available but initial references
   * may need to be registered as part of Interceptor registration.
   * <p>
   * This method may not be called during post_init.
   */
  public void register_initial_reference(String id, org.omg.CORBA.Object obj) throws InvalidName {
    checkStage();
    if (id == null) {
      nullParam();
    }

    boolean isNil = false;
    if( obj == null ) {
      isNil = true;
    } else if( obj instanceof ObjectImpl ) {
      DelegateImpl delegate = (DelegateImpl) ((ObjectImpl) obj)._get_delegate();
      IOR ior = delegate.getIOR();
      isNil = ior.is_nil();
    }

    if(isNil) {
      throw new BAD_PARAM("register_initial_reference called with nil Object", MinorCodes.RIR_WITH_NULL_OBJECT, CompletionStatus.COMPLETED_NO);
    }

    try {
      ((ClientORB) orb).register_initial_reference(id, obj);
    } catch(org.omg.CORBA.ORBPackage.InvalidName e) {
      throw new InvalidName(e.getMessage());
    }
  }

  /**
   * This operation is only valid during post_init.  It is identical to
   * ORB::resolve_initial_references.  This same functionality exists here
   * because the ORB, not yet fully initialized, is not yet available,
   * but initial references may be required from the ORB as part
   * of Interceptor registration.
   * <p>
   * (incorporates changes from errata in orbos/00-01-01)
   * <p>
   * This method may not be called during pre_init.
   */
  public org.omg.CORBA.Object resolve_initial_references(String id) throws InvalidName {
    checkStage();

    if (id == null) {
      nullParam();
    }

    if (stage == STAGE_PRE_INIT) {
      throw new BAD_INV_ORDER("Resolve Initial References cannot be called in pre_init stage");
    }

    org.omg.CORBA.Object objRef = null;
    try {
      objRef = orb.resolve_initial_references(id);
    } catch (org.omg.CORBA.ORBPackage.InvalidName e) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("ORBInitInfoImpl.resolve_initial_references(String)", LoggerConfigurator.exceptionTrace(e));
      }
      throw new InvalidName(e.toString());
    }
    return objRef;
  }

  public void add_client_request_interceptor(ClientRequestInterceptor interceptor) throws DuplicateName {
    checkStage();

    if (interceptor == null) {
      nullParam();
    }

    if (staticPropeties) {
      InterceptorsStorage.register_client_interceptor(interceptor);
    } else {
      InterceptorsStorage.register_client_interceptor(orb, interceptor);
    }
  }

  public void add_server_request_interceptor(ServerRequestInterceptor interceptor) throws DuplicateName {
    checkStage();

    if (interceptor == null) {
      nullParam();
    }

    if (staticPropeties) {
      InterceptorsStorage.register_server_interceptor(interceptor);
    } else {
      InterceptorsStorage.register_server_interceptor(orb, interceptor);
    }
  }

  public void add_ior_interceptor(IORInterceptor interceptor) throws DuplicateName {
    checkStage();

    if (interceptor == null) {
      nullParam();
    }

    if (staticPropeties) {
      InterceptorsStorage.register_ior_interceptor(interceptor);
    } else {
      InterceptorsStorage.register_ior_interceptor(orb, interceptor);
    }
  }

  /**
   * A service calls allocate_slot_id to allocate a slot on
   * PortableInterceptor::Current.
   *
   * @return The index to the slot which has been allocated.
   */
  public int allocate_slot_id() {
    checkStage();
    return ((com.sap.engine.services.iiop.CORBA.ORB) orb).getPICurrent().allocateSlotId();
  }

  /**
   * Register a PolicyFactory for the given PolicyType.
   * <p>
   * If a PolicyFactory already exists for the given PolicyType,
   * BAD_INV_ORDER is raised with a minor code of TBD_BIO+2.
   */
  public void register_policy_factory(int type, PolicyFactory policy_factory) {
    checkStage();

    if (policy_factory == null) {
      nullParam();
    }

    //      orb.registerPolicyFactory( type, policy_factory ); Vancho
  }

  private void nullParam() throws BAD_PARAM {
    throw new BAD_PARAM("Input parameter is null", 1, CompletionStatus.COMPLETED_NO);
  }
  
//  private void nullParam(String method, String param_type, String param_name) throws BAD_PARAM {
//    throw new BAD_PARAM("Input parameter in method " + method + " of type " + param_type + " is null", 1, CompletionStatus.COMPLETED_NO);
//  }

  private void checkStage() {
    if (stage == STAGE_CLOSED) {
      throw new OBJECT_NOT_EXIST(MESSAGE_ORBINITINFO_INVALID);
    }
  }

}

