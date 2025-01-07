/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.cosnaming;

import org.omg.CosNaming.*;

/**
 * Copyright: Copyright (c) 2003
 * Company: SAP AG - Sap Labs Bulgaria
 *
 * @version 6.30
 */

public abstract class NamingContextExtBaseImpl extends org.omg.CORBA.portable.ObjectImpl implements org.omg.CosNaming.NamingContextExt, org.omg.CORBA.portable.InvokeHandler, org.omg.CosNaming.NamingContext {
  /**
   * serial version UID
   */
  static final long serialVersionUID = -9040873576864454626L;

  public NamingContextExtBaseImpl() {
    super();
  }

//  public NamingContextExtBaseImpl(String root){
//    super(root);
//  }

  private static java.util.Hashtable _methods = new java.util.Hashtable();

  static {
    _methods.put("to_string", new java.lang.Integer(0));
    _methods.put("to_name", new java.lang.Integer(1));
    _methods.put("to_url", new java.lang.Integer(2));
    _methods.put("resolve_str", new java.lang.Integer(3));
    _methods.put("bind", new java.lang.Integer(4));
    _methods.put("rebind", new java.lang.Integer(5));
    _methods.put("bind_context", new java.lang.Integer(6));
    _methods.put("rebind_context", new java.lang.Integer(7));
    _methods.put("resolve", new java.lang.Integer(8));
    _methods.put("unbind", new java.lang.Integer(9));
    _methods.put("new_context", new java.lang.Integer(10));
    _methods.put("bind_new_context", new java.lang.Integer(11));
    _methods.put("destroy", new java.lang.Integer(12));
    _methods.put("list", new java.lang.Integer(13));
  }

  public org.omg.CORBA.portable.OutputStream _invoke(String method, org.omg.CORBA.portable.InputStream in, org.omg.CORBA.portable.ResponseHandler rh) {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer) _methods.get(method);
    if (__method == null) {
      throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    switch (__method.intValue()) {
      case 0:  // CosNaming/NamingContextExt/to_string
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          String __result = null;
          __result = this.to_string(n);
          out = rh.createReply();
          out.write_string(__result);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 1:  // CosNaming/NamingContextExt/to_name
      {
        try {
          String sn = org.omg.CosNaming.NamingContextExtPackage.StringNameHelper.read(in);
          NameComponent __result[] = null;
          __result = this.to_name(sn);
          out = rh.createReply();
          NameHelper.write(out, __result);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 2:  // CosNaming/NamingContextExt/to_url
      {
        try {
          String addr = org.omg.CosNaming.NamingContextExtPackage.AddressHelper.read(in);
          String sn = org.omg.CosNaming.NamingContextExtPackage.StringNameHelper.read(in);
          String __result = null;
          __result = this.to_url(addr, sn);
          out = rh.createReply();
          out.write_string(__result);
        } catch (org.omg.CosNaming.NamingContextExtPackage.InvalidAddress __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextExtPackage.InvalidAddressHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 3:  // CosNaming/NamingContextExt/resolve_str
      {
        try {
          String n = org.omg.CosNaming.NamingContextExtPackage.StringNameHelper.read(in);
          org.omg.CORBA.Object __result = null;
          __result = this.resolve_str(n);
          out = rh.createReply();
          org.omg.CORBA.ObjectHelper.write(out, __result);
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 4:  // CosNaming/NamingContext/bind
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          org.omg.CORBA.Object obj = org.omg.CORBA.ObjectHelper.read(in);
          this.bind(n, obj);
          out = rh.createReply();
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write(out, __ex);
        }
        break;
      }

      case 5:  // CosNaming/NamingContext/rebind
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          org.omg.CORBA.Object obj = org.omg.CORBA.ObjectHelper.read(in);
          this.rebind(n, obj);
          out = rh.createReply();
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 6:  // CosNaming/NamingContext/bind_context
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          NamingContext nc = NamingContextHelper.read(in);
          this.bind_context(n, nc);
          out = rh.createReply();
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write(out, __ex);
        }
        break;
      }

      case 7:  // CosNaming/NamingContext/rebind_context
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          NamingContext nc = NamingContextHelper.read(in);
          this.rebind_context(n, nc);
          out = rh.createReply();
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 8:  // CosNaming/NamingContext/resolve
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          org.omg.CORBA.Object __result = null;
          __result = this.resolve(n);
          out = rh.createReply();
          org.omg.CORBA.ObjectHelper.write(out, __result);
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 9:  // CosNaming/NamingContext/unbind
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          this.unbind(n);
          out = rh.createReply();
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 10:  // CosNaming/NamingContext/new_context
      {
        NamingContext __result = null;
        __result = this.new_context();
        out = rh.createReply();
        NamingContextHelper.write(out, __result);
        break;
      }

      case 11:  // CosNaming/NamingContext/bind_new_context
      {
        try {
          NameComponent n[] = NameHelper.read(in);
          NamingContext __result = null;
          __result = this.bind_new_context(n);
          out = rh.createReply();
          NamingContextHelper.write(out, __result);
        } catch (org.omg.CosNaming.NamingContextPackage.NotFound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotFoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.AlreadyBound __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.AlreadyBoundHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.CannotProceed __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.CannotProceedHelper.write(out, __ex);
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.InvalidNameHelper.write(out, __ex);
        }
        break;
      }

      case 12:  // CosNaming/NamingContext/destroy
      {
        try {
          this.destroy();
          out = rh.createReply();
        } catch (org.omg.CosNaming.NamingContextPackage.NotEmpty __ex) {
          out = rh.createExceptionReply();
          org.omg.CosNaming.NamingContextPackage.NotEmptyHelper.write(out, __ex);
        }
        break;
      }

      case 13:  // CosNaming/NamingContext/list
      {
        int how_many = in.read_ulong();
        BindingListHolder bl = new BindingListHolder();
        BindingIteratorHolder bi = new BindingIteratorHolder();
        this.list(how_many, bl, bi);
        out = rh.createReply();
        BindingListHelper.write(out, bl.value);
        BindingIteratorHelper.write(out, bi.value);
        break;
      }

      default:
        throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {"IDL:omg.org/CosNaming/NamingContextExt:1.0", "IDL:omg.org/CosNaming/NamingContext:1.0"};

  public String[] _ids() {
    return __ids;
  }


}
