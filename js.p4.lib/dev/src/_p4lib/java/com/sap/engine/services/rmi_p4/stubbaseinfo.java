package com.sap.engine.services.rmi_p4;

/**
 * This class contains information for creating remote call
 * Objects of this class are created by the broker and set
 * in the stub
 *
 * @author Ivo Simeonov, Georgy Stanev
 * @version 7.0
 */
public class StubBaseInfo extends RemoteObjectInfo {

  static final long serialVersionUID = 6846475421043266444L;

    transient String incomingProfile;

    public String getIncomingProfile() {
    return incomingProfile;
  }

  public void setIncomingProfile(String incomingProfile) {
    this.incomingProfile = incomingProfile;
  }

  public static StubBaseInfo makeStubBaseInfo(RemoteObjectInfo remoteInfo) {
    StubBaseInfo inf = new StubBaseInfo();
    inf.connectionProfiles = remoteInfo.connectionProfiles;
    inf.client_id = remoteInfo.client_id;
    inf.server_id = remoteInfo.server_id;
    inf.ownerId = remoteInfo.ownerId;
    inf.key = remoteInfo.key;
    inf.stubs = remoteInfo.stubs;
    inf.isRedirectable = remoteInfo.isRedirectable;
    inf.factoryName = remoteInfo.factoryName;
    inf.objIdentity = remoteInfo.objIdentity;
    inf.redirIdent = remoteInfo.redirIdent;
    inf.setUrls(remoteInfo.getUrls());
    inf.hosts = remoteInfo.hosts;
    inf.server_classLoaderName = remoteInfo.server_classLoaderName;
    inf.setOptimization(remoteInfo.supportOptimization());
    return inf;
  }

  public StubBaseInfo cloneStubBaseInfo() {
    //skip connected flag
    StubBaseInfo inf = new StubBaseInfo();
    inf.connectionProfiles = this.connectionProfiles;
    inf.client_id = this.client_id;
    inf.server_id = this.server_id;
    inf.ownerId = this.ownerId;
    inf.key = this.key;
    inf.stubs = this.stubs;
    inf.isRedirectable = this.isRedirectable;
    inf.factoryName = this.factoryName;
    inf.objIdentity = this.objIdentity;
    inf.redirIdent = this.redirIdent;
    inf.setUrls(this.getUrls());
    inf.hosts = this.hosts;
    inf.server_classLoaderName = this.server_classLoaderName;
    inf.incomingProfile = this.incomingProfile;
    inf.setOptimization(this.supportOptimization());
    return inf;
  }

  protected int checkConnectionType(String connectionType) {
    int count = 0;

    for (int i = 0; i < connectionProfiles.length; i++) {
      if (connectionProfiles[i].getType().equals(connectionType)) {
        count++;
      }
    }

    return count;
  }

}

