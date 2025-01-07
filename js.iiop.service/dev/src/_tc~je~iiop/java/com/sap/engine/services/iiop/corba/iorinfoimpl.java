package com.sap.engine.services.iiop.CORBA;

import org.omg.CORBA.*;
import org.omg.IOP.TaggedComponent;
import org.omg.PortableInterceptor.IORInfo;
import org.omg.PortableInterceptor.ObjectReferenceFactory;
import org.omg.PortableInterceptor.ObjectReferenceTemplate;

import java.util.HashMap;

/**
 * IORInfoImpl is the implementation of the IORInfo class, as described
 * in orbos/99-12-02, section 7.
 *
 * @author Mladen Droshev
 */
public class IORInfoImpl extends LocalObject implements IORInfo {

  //store for Profile
  HashMap<Integer, Profile> store = null;

  /**
   * Creates a new IORInfo implementation.
   */
  IORInfoImpl() {
    store = new HashMap<Integer, Profile>();
  }

  /**
   * An ORB service implementation may determine what server side policy
   * of a particular type is in effect for an IOR being constructed by
   * calling the get_effective_policy operation.  When the IOR being
   * constructed is for an object implemented using a POA, all Policy
   * objects passed to the PortableServer::POA::create_POA call that
   * created that POA are accessible via get_effective_policy.
   * <p>
   * If a policy for the given type is not known to the ORB, then this
   * operation will raise INV_POLICY with a standard minor code of 2.
   *
   * @param type The CORBA::PolicyType specifying the type of policy to
   *   return.
   * @return The effective CORBA::Policy object of the requested type.
   *   If the given policy type is known, but no policy of that tpye is
   *   in effect, then this operation will return a nil object reference.
   */
  public Policy get_effective_policy(int type) {
    // !!!! we return null because it don't have implementation od POA
    return null;
  }

  /**
   * A portable ORB service implementation calls this method from its
   * implementation of establish_components to add a tagged component to
   * the set which will be included when constructing IORs.  The
   * components in this set will be included in all profiles.
   * <p>
   * Any number of components may exist with the same component ID.
   *
   * @param tagged_component The IOP::TaggedComponent to add
   */
  public void add_ior_component(TaggedComponent tagged_component) {
    if (tagged_component == null) {
      nullParam();
    } else {
      //for all - after create new Profile must to copy this List in List for private Profile and tp add new compo-s
      for (Profile profileVar:store.values()) {
        profileVar.addSimpleProfile(new SimpleProfile(tagged_component.tag, tagged_component.component_data));
      }
    }
  }

  /**
   * A portable ORB service implementation calls this method from its
   * implementation of establish_components to add a tagged component to
   * the set which will be included when constructing IORs.  The
   * components in this set will be included in the specified profile.
   * <p>
   * Any number of components may exist with the same component ID.
   * <p>
   * If the given profile ID does not define a known profile or it is
   * impossible to add components to thgat profile, BAD_PARAM is raised
   * with a minor code of TBD_BP + 3.
   *
   * @param tagged_component The IOP::TaggedComponent to add.
   * @param profile_id The IOP::ProfileId tof the profile to which this
   *     component will be added.
   */
  public void add_ior_component_to_profile(TaggedComponent tagged_component, int profile_id) {
    if ((tagged_component == null) || (!store.containsKey(profile_id))) {
      nullParam();
    } else {
      store.get(profile_id).addSimpleProfile(new SimpleProfile(tagged_component.tag, tagged_component.component_data));
    }
  }


  /** Return the adapter manager id of the object adapter
     * that was just created and is running IOR interceptors.
     */
  public int manager_id () {
    throw new NO_IMPLEMENT(); //TODO
  }

  /** Return the adapter state of the object adapter
     * that was just created and is running IOR interceptors.
     */
  public short state () {
    throw new NO_IMPLEMENT(); //TODO
  }

  /** Return the object reference template of the object adapter
     * that was just created and is running IOR interceptors.
     */
  public ObjectReferenceTemplate adapter_template () {
    throw new NO_IMPLEMENT(); //TODO
  }

  /** On read, returns the current factory that will be used to create
     * object references for the object adapter that was just created
     * and is running IOR interceptors.  By default, this factory is the same
     * as the value of the adapter_template attribute.  The current_factory
     * may also be set to another object reference template inside an
     * IORInterceptor_3_0.
     */
  public ObjectReferenceFactory current_factory () {
    throw new NO_IMPLEMENT(); //TODO
  }

  /** On read, returns the current factory that will be used to create
     * object references for the object adapter that was just created
     * and is running IOR interceptors.  By default, this factory is the same
     * as the value of the adapter_template attribute.  The current_factory
     * may also be set to another object reference template inside an
     * IORInterceptor_3_0.
     */
  public void current_factory (org.omg.PortableInterceptor.ObjectReferenceFactory newCurrent_factory) {
    throw new NO_IMPLEMENT(); //TODO
  }


  /**
   * Called when an invalid null parameter was passed.  Throws a
   * BAD_PARAM with a minor code of 1
   */
  private void nullParam() throws BAD_PARAM {
    throw new BAD_PARAM(0x53550000 + 200, CompletionStatus.COMPLETED_NO);
  }

}

