package com.sap.engine.services.iiop.CORBA;

import org.omg.CORBA.*;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.Encoding;
import org.omg.IOP.ENCODING_CDR_ENCAPS;

public final class CodecFactoryImpl extends LocalObject implements CodecFactory {

  private static final int MAX_MINOR_VERSION_SUPPORTED = 2;
  private Codec codec = null;

  public CodecFactoryImpl(ORB orb) {
    this.codec = new CDRCodecImpl(orb);
  }

  public Codec create_codec(Encoding encoding) throws UnknownEncoding {
    if (encoding == null) {
      nullParam();
    }

    Codec codec = null;

    if (encoding.format == ENCODING_CDR_ENCAPS.value && encoding.major_version == 1 && encoding.minor_version >= 0 && encoding.minor_version <= 2) {
      codec = this.codec;
    }

    if (codec == null) {
      throw new UnknownEncoding();
    } else {
      return codec;
    }
  }

  private void nullParam() throws BAD_PARAM {
    throw new BAD_PARAM(0x535500c9, CompletionStatus.COMPLETED_NO);
  }

}

