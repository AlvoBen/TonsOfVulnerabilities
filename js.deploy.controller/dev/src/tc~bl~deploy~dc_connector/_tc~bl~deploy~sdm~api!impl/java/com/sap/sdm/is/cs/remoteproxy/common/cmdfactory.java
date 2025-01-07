package com.sap.sdm.is.cs.remoteproxy.common;

/**
 * @author Christian Gabrisch 07.08.2003
 */
public final class CmdFactory {

	public static CmdRemoteCall createCmdRemoteCall(String sessionID,
			String ifName, String methodName, String instanceID) {
		return new CmdRemoteCall(sessionID, ifName, methodName, instanceID);
	}

	public static CmdRemoteCall createCmdRemoteCall(String sessionID,
			String ifName, String methodName, String instanceID,
			RemoteCallArg[] callArgs, String[] sigClassNames,
			InterfaceID[] delArr) {
		return new CmdRemoteCall(sessionID, ifName, methodName, instanceID,
				callArgs, sigClassNames, delArr);
	}

	public static CmdRemoteCall createCmdRemoteCall(String sessionID,
			String ifName, String methodName, String instanceID, Object[] args,
			Class[] parameterTypes, InterfaceID[] delArr) throws Exception {
		String[] sigClassNames = null;
		if (parameterTypes != null) {
			sigClassNames = new String[parameterTypes.length];
			for (int i = 0; i < parameterTypes.length; i++) {
				sigClassNames[i] = parameterTypes[i].getName();
			}
		}
		return new CmdRemoteCall(sessionID, ifName, methodName, instanceID,
				args, sigClassNames, delArr);
	}

	public static RemoteCallArg createRemoteCallArg(String ifName,
			String instanceID, String value, ArrayElem[] arrElemArr,
			boolean isArray) {
		return new RemoteCallArg(ifName, instanceID, value, arrElemArr, isArray);
	}

	public static RemoteCallArg createRemoteCallArg(Object arg)
			throws Exception {
		return new RemoteCallArg(arg);
	}

	public static ArrayElem createArrayElem(String ifName, String instanceID,
			String value, boolean hasCacheableNoArgMethods) {
		return new ArrayElem(ifName, instanceID, value,
				hasCacheableNoArgMethods);
	}

	public static CmdRemoteException createCmdRemoteException(String name,
			String msg) {
		return new CmdRemoteException(name, msg);
	}

	public static CmdRemoteReturn createCmdRemoteReturn(String ifName,
			String instanceID, String value, ArrayElem[] arrElemArr,
			boolean hasCacheableNoArgMethods) {
		return new CmdRemoteReturn(ifName, instanceID, value, arrElemArr,
				hasCacheableNoArgMethods);

	}

}
