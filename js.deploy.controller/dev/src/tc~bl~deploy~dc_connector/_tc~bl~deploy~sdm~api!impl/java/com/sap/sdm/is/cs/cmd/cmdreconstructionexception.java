package com.sap.sdm.is.cs.cmd;

/**
 * 
 * Title: Software Deployment Manager Description: An exception of this type is
 * thrown every time when a command could not be reconstructed. Copyright:
 * Copyright (c) 2003 Company: SAP AG Date 2003-9-12
 * 
 * @author dimitar-d
 * @version 1.0
 * 
 */
public class CmdReconstructionException extends Exception {

	public CmdReconstructionException() {
		super();
	}

	public CmdReconstructionException(String msg) {
		super(msg);
	}

}