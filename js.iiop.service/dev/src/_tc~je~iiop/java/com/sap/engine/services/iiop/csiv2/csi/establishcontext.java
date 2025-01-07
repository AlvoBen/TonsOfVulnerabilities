package com.sap.engine.services.iiop.csiv2.CSI;

import org.omg.CORBA.portable.IDLEntity;

public final class EstablishContext implements IDLEntity {

  public EstablishContext() {
    client_context_id = 0L;
    authorization_token = null;
    identity_token = null;
    client_authentication_token = null;
  }

  public EstablishContext(long l, AuthorizationElement aauthorizationelement[], IdentityToken identitytoken, byte abyte0[]) {
    client_context_id = 0L;
    authorization_token = null;
    identity_token = null;
    client_authentication_token = null;
    client_context_id = l;
    authorization_token = aauthorizationelement;
    identity_token = identitytoken;
    client_authentication_token = abyte0;
  }

  public long client_context_id;
  public AuthorizationElement authorization_token[];
  public IdentityToken identity_token;
  public byte client_authentication_token[];

}

