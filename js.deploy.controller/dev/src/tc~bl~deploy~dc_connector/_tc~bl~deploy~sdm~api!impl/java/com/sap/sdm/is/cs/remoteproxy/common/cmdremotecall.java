package com.sap.sdm.is.cs.remoteproxy.common;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import com.sap.sdm.is.cs.cmd.CmdIF;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class CmdRemoteCall implements CmdIF {
	public final static String NAME = "RemoteCall";

	private String ifName = null;
	private String methodName = null;
	private String instanceID = null;

	// the sessionID is not needed anymore but kept for downward compatibility
	private String sessionID = null;
	private Object[] methodArgs = null;
	private RemoteCallArg[] rcArgs = null;
	private Class[] parameterTypeArr = null;
	private String[] sigClassNames = null;
	private List filesToBeTransferred = new ArrayList();
	private InterfaceID[] delArr = null;

	public CmdRemoteCall(String sessionID, String ifName, String methodName,
			String instanceID) {
		this(sessionID, ifName, methodName, instanceID, null, null, null);
	}

	public CmdRemoteCall(String sessionID, String ifName, String methodName,
			String instanceID, RemoteCallArg[] callArgs,
			String[] sigClassNames, InterfaceID[] delArr) {
		this.sessionID = sessionID;
		this.ifName = ifName;
		this.methodName = methodName;
		this.instanceID = instanceID;
		this.rcArgs = callArgs;
		this.sigClassNames = sigClassNames;
		this.delArr = delArr;
	}

	public CmdRemoteCall(String sessionID, String ifName, String methodName,
			String instanceID, Object[] args, String[] sigClassNames,
			InterfaceID[] delArr) throws Exception {
		this.sessionID = sessionID;
		this.ifName = ifName;
		this.methodName = methodName;
		this.instanceID = instanceID;
		this.methodArgs = args;
		this.sigClassNames = sigClassNames;
		this.delArr = delArr;
		if (args != null) {
			this.rcArgs = new RemoteCallArg[args.length];
			for (int i = 0; i < args.length; i++) {
				rcArgs[i] = CmdFactory.createRemoteCallArg(args[i]);
				if (null == args[i])
					continue;
				Class argClass = args[i].getClass();
				if (!argClass.isArray()) {
					String argClassName = argClass.getName();
					if (argClassName.equals(ClassNames.FILECLASSNAME)) {
						this.filesToBeTransferred.add((File) args[i]);
					}
				} else {
					int arrLength = Array.getLength(args[i]);
					String arrElemClassName = args[i].getClass()
							.getComponentType().getName();
					for (int j = 0; j < arrLength; j++) {
						Object arrElem = Array.get(args[i], j);
						if (arrElemClassName.equals(ClassNames.FILECLASSNAME)) {
							/*
							 * special handling for file objects within arrays
							 */
							File arrElemFile = (File) arrElem;
							if (!arrElemFile.exists()) {
								throw new Exception(
										"Given argument (java.io.File) within array \""
												+ arrElemFile.getAbsolutePath()
												+ "\" does not exist");
							}
							if (arrElemFile.isDirectory()) {
								throw new Exception(
										"Given argument (java.io.File) within array \""
												+ arrElemFile.getAbsolutePath()
												+ "\" is a directory");
							}
							this.filesToBeTransferred.add(arrElemFile);
						}
					}
				}
			}
		}
	}

	public String getMyName() {
		return NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdRemoteCall#getInterfaceName()
	 */
	public String getInterfaceName() {
		return this.ifName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdRemoteCall#getMethodName()
	 */
	public String getMethodName() {
		return this.methodName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdRemoteCall#getInstanceID()
	 */
	public String getInstanceID() {
		return this.instanceID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdRemoteCall#getSessionID()
	 */
	public String getSessionID() {
		return this.sessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.CmdRemoteCall#getMethodArgs()
	 */
	public Object[] getMethodArgs() {
		return this.methodArgs;
	}

	public RemoteCallArg[] getCallArgs() {
		return this.rcArgs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.CmdRemoteCall#getFileArrForTransfer
	 * ()
	 */
	public File[] getFileArrForTransfer() {
		File[] result = new File[this.filesToBeTransferred.size()];
		this.filesToBeTransferred.toArray(result);
		return result;
	}

	public String[] getSignatureClassNames() {
		return this.sigClassNames;
	}

	public InterfaceID[] getDelArr() {
		return this.delArr;
	}

	// public InterfaceID getInterfaceIDForProxyArg(Class proxyClass, Object
	// arg)
	// throws Exception
	// {
	//    
	// if (Proxy.isProxyClass(proxyClass)) {
	// InterfaceID argID =
	// ((APIClientInvocationHandler)Proxy.getInvocationHandler
	// (arg)).getClientInterfaceID();
	// if (argID == null) {
	// throw new Exception("ClientInterfaceID for \"" + proxyClass.getName() +
	// "\" not found");
	// } else {
	// return argID;
	// }
	// } else {
	// throw new Exception("Error: argument is no primitive and no proxy");
	// }
	// }
}
