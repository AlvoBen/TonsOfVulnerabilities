package com.sap.engine.services.iiop.CORBA;

import com.sap.engine.services.iiop.internal.portable.IIOPInputStream;
import com.sap.engine.services.iiop.internal.portable.IIOPOutputStream;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import org.omg.CORBA.*;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;

public final class CDRCodecImpl extends LocalObject implements Codec {

  private transient ORB orb;

  public CDRCodecImpl(ORB orb) {
    this.orb = orb;
  }

  public byte[] encode(Any any) throws InvalidTypeForEncoding {
    if (any == null) {
      nullParam();
    }

    return encodeImpl(any, true);
  }

  public Any decode(byte abyte0[]) throws FormatMismatch {
    if (abyte0 == null) {
      nullParam();
    }

    return decodeImpl(abyte0, null);
  }

  public byte[] encode_value(Any any) throws InvalidTypeForEncoding {
    if (any == null) {
      nullParam();
    }

    return encodeImpl(any, false);
  }

  public Any decode_value(byte abyte0[], TypeCode typecode) throws FormatMismatch, TypeMismatch {
    if (abyte0 == null) {
      nullParam();
    }

    if (typecode == null) {
      nullParam();
    }

    return decodeImpl(abyte0, typecode);
  }

  private byte[] encodeImpl(Any any, boolean flag) {
    if (any == null) {
      nullParam();
    }

    IIOPOutputStream outputStream = new IIOPOutputStream(orb);
    outputStream.write_boolean(false); //encoding endian

    if (flag) {
      outputStream.write_TypeCode(any.type());
    }

    any.write_value(outputStream);
    return outputStream.toByteArray();
  }

  private Any decodeImpl(byte abyte0[], TypeCode typecode) throws FormatMismatch {
    if (abyte0 == null) {
      nullParam();
    }

    AnyImpl anyimpl = null;
    try {
      IIOPInputStream inputStream = new IIOPInputStream(orb, abyte0);
      inputStream.read_boolean(); //encoding endian

      if (typecode == null) {
        typecode = inputStream.read_TypeCode();
      }

      anyimpl = new AnyImpl(orb);
      anyimpl.read_value(inputStream, typecode);
    } catch (RuntimeException runtimeexception) {
      if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
        LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("CDRCodecImpl.decodeImpl(byte[], TypeCode)", LoggerConfigurator.exceptionTrace(runtimeexception));
      }
      throw new FormatMismatch();
    }
    return anyimpl;
  }

  private void nullParam() throws BAD_PARAM {
    throw new BAD_PARAM(0x535500c9, CompletionStatus.COMPLETED_NO);
  }

}

