package com.sap.jms.util.compat.rmi_p4.interfaces;

	public interface P4RemoteLoadingExt {

	  /**
	   * indicate special list of jars that the remote object wich to be provided remotely
	   * possilbe paths: -> j2ee/cluster/....
	   * @return jars array
	   */
	  public String[] getResources();
	}
