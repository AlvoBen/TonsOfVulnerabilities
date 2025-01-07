package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.*;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public abstract class SASContextBodyHelper {

  public SASContextBodyHelper() {

  }

  public static void insert(Any any, SASContextBody sascontextbody) {
    OutputStream outputstream = any.create_output_stream();
    any.type(type());
    write(outputstream, sascontextbody);
    any.read_value(outputstream.create_input_stream(), type());
  }

  public static SASContextBody extract(Any any) {
    return read(any.create_input_stream());
  }

  public static synchronized TypeCode type() {
    if (__typeCode == null) {
      TypeCode typecode = ORB.init().get_primitive_tc(TCKind.tk_short);
      //          typecode = ORB.init().create_alias_tc(MsgTypeHelper.id(), "MsgType", typecode);
      UnionMember aunionmember[] = new UnionMember[4];
      Any any = ORB.init().create_any();
      any.insert_short((short) 0);
      TypeCode typecode1 = EstablishContextHelper.type();
      aunionmember[0] = new UnionMember("establish_msg", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_short((short) 1);
      typecode1 = CompleteEstablishContextHelper.type();
      aunionmember[1] = new UnionMember("complete_msg", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_short((short) 4);
      typecode1 = ContextErrorHelper.type();
      aunionmember[2] = new UnionMember("error_msg", any, typecode1, null);
      any = ORB.init().create_any();
      any.insert_short((short) 5);
      typecode1 = MessageInContextHelper.type();
      aunionmember[3] = new UnionMember("in_context_msg", any, typecode1, null);
      __typeCode = ORB.init().create_union_tc(id(), "SASContextBody", typecode, aunionmember);
    }

    return __typeCode;
  }

  public static String id() {
    return _id;
  }

  public static SASContextBody read(InputStream inputstream) {
    SASContextBody sascontextbody = new SASContextBody();
    short word0 = 0;
    word0 = inputstream.read_short();

    switch (word0) {
      case 0: // '\0'
      {
        EstablishContext establishcontext = null;
        establishcontext = EstablishContextHelper.read(inputstream);
        sascontextbody.establish_msg(establishcontext);
        break;
      }
      case 1: // '\001'
      {
        CompleteEstablishContext completeestablishcontext = null;
        completeestablishcontext = CompleteEstablishContextHelper.read(inputstream);
        sascontextbody.complete_msg(completeestablishcontext);
        break;
      }
      case 4: // '\004'
      {
        ContextError contexterror = null;
        contexterror = ContextErrorHelper.read(inputstream);
        sascontextbody.error_msg(contexterror);
        break;
      }
      case 5: // '\005'
      {
        MessageInContext messageincontext = null;
        messageincontext = MessageInContextHelper.read(inputstream);
        sascontextbody.in_context_msg(messageincontext);
        break;
      }
      case 2: // '\002'
      case 3: // '\003'
      default: {
        sascontextbody._default(word0);
        break;
      }
    }

    return sascontextbody;
  }

  public static void write(OutputStream outputstream, SASContextBody sascontextbody) {
    outputstream.write_short(sascontextbody.discriminator());

    switch (sascontextbody.discriminator()) {
      case 0: // '\0'
      {
        EstablishContextHelper.write(outputstream, sascontextbody.establish_msg());
        break;
      }
      case 1: // '\001'
      {
        CompleteEstablishContextHelper.write(outputstream, sascontextbody.complete_msg());
        break;
      }
      case 4: // '\004'
      {
        ContextErrorHelper.write(outputstream, sascontextbody.error_msg());
        break;
      }
      case 5: // '\005'
      {
        MessageInContextHelper.write(outputstream, sascontextbody.in_context_msg());
        break;
      }
    }
  }

  private static String _id = "IDL:omg.org/CSI/SASContextBody:1.0";
  private static TypeCode __typeCode = null;

}

