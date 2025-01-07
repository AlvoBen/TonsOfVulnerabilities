package com.sap.engine.services.rmi_p4;

import com.sap.engine.services.rmi_p4.exception.P4Logger;

import java.io.DataInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * User: I024084
 * Date: 2006-1-25
 * Time: 11:27:22
 */
public class DataOptInputStream extends DataInputStream {

  private String connectionType;
  private ClassLoader classLoader;
  private ByteArrayInputStream arrayStream = null;
  private String underlyingProfile;
  private P4ObjectBroker broker = P4ObjectBroker.init();
  private int remoteBrokerId = -1;

  private boolean icm = false;


  public String getUnderlyingProfile() {
    return underlyingProfile;
  }

  public void setUnderlyingProfile(String underlyingProfile) {
    this.underlyingProfile = underlyingProfile;
  }

  public DataOptInputStream(ByteArrayInputStream bin) {
    this(bin, "None");
  }

  public DataOptInputStream(ByteArrayInputStream bin, boolean icm) {
    this(bin, "None");
    this.icm = icm;
  }

  public DataOptInputStream(ByteArrayInputStream bin, String type) {
    super(bin);
    this.connectionType = type;
    this.arrayStream = bin;
  }


  public void close() throws IOException {
    try {
      super.close();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("MarshalInputStream.close()", P4Logger.exceptionTrace(ioex));
      }
    }
    try {
      arrayStream.close();
    } catch (IOException ioex) {
      if (P4Logger.getLocation().beDebug()) {
        P4Logger.getLocation().debugT("MarshalInputStream.close()", P4Logger.exceptionTrace(ioex));
      }
    }
  }

  public void setConnectionType(String _type) {
    connectionType = _type;
  }

  public void setClassLoader(ClassLoader classloader) {
    this.classLoader = classloader;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public void setRemoteBrokerId(int remote_brokerId) {
    this.remoteBrokerId = remote_brokerId;
  }

  public int getRemoteBrokerId() {
    return this.remoteBrokerId;
  }
}
