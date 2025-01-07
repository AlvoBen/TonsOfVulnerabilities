package com.sap.sdm.is.cs.remoteproxy.common;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Proxy;

import com.sap.sdm.is.cs.remoteproxy.client.impl.ClientInvocationHandler;

/**
 * 
 * @author <A HREF="mailto:DL_011000358700005701181999E">Change Management
 *         Tools</a> - Martin Stahl
 * @version 1.0
 */
public class RemoteCallArg {

	private String ifName = null;
	private String instanceID = null;
	private String value = null;
	private ArrayElem[] arrElemArr = null;
	private boolean isArray = false;
	private boolean isNull = false;

	public RemoteCallArg(String ifName, String instanceID, String value,
			ArrayElem[] arrElemArr, boolean isArray) {
		this.ifName = ifName;
		this.instanceID = instanceID;
		this.value = value;
		this.arrElemArr = arrElemArr;
		this.isArray = isArray;
	}

	public RemoteCallArg(Object arg) throws Exception {
		if (arg == null) {
			this.isNull = true;
			return;
		}
		Class argClass = arg.getClass();
		if (!argClass.isArray()) {
			String argClassName = argClass.getName();
			if ((argClassName.equals(ClassNames.BOOLEANCLASSNAME))
					|| (argClassName.equals(ClassNames.BYTECLASSNAME))
					|| (argClassName.equals(ClassNames.DOUBLECLASSNAME))
					|| (argClassName.equals(ClassNames.FLOATCLASSNAME))
					|| (argClassName.equals(ClassNames.INTEGERCLASSNAME))
					|| (argClassName.equals(ClassNames.LONGCLASSNAME))
					|| (argClassName.equals(ClassNames.SHORTCLASSNAME))) {
				this.ifName = argClassName;
				this.value = arg.toString();
			} else if (argClassName.equals(ClassNames.STRINGCLASSNAME)) {
				this.ifName = argClassName;
				this.value = (String) arg;
			} else if (argClassName.equals(ClassNames.FILECLASSNAME)) {
				/*
				 * special handling for arguments of class java.io.file
				 */
				this.ifName = argClassName;
				this.value = ((File) arg).getName();
			} else {
				// try to find the argument within ClientInstanceManager...
				InterfaceID argID = getInterfaceIDForProxyArg(argClass, arg);
				this.ifName = argID.getClientClassName();
				this.instanceID = argID.getInstanceID();
			}
		} else {
			// argument is an array
			this.ifName = argClass.getName();
			this.isArray = true;
			int arrLength = Array.getLength(arg);
			this.arrElemArr = new ArrayElem[arrLength];
			String arrElemClassName = arg.getClass().getComponentType()
					.getName();
			for (int i = 0; i < arrLength; i++) {
				Object arrElem = Array.get(arg, i);
				if (arrElem == null) {
					arrElemArr[i] = CmdFactory.createArrayElem(
							arrElemClassName, null, null, false);
				} else if (arrElemClassName.equals(ClassNames.BOOLEANCLASSNAME)
						|| arrElemClassName.equals(ClassNames.BYTECLASSNAME)
						|| arrElemClassName.equals(ClassNames.DOUBLECLASSNAME)
						|| arrElemClassName.equals(ClassNames.FLOATCLASSNAME)
						|| arrElemClassName.equals(ClassNames.INTEGERCLASSNAME)
						|| arrElemClassName.equals(ClassNames.LONGCLASSNAME)
						|| arrElemClassName.equals(ClassNames.SHORTCLASSNAME)
						|| arrElemClassName.equals(ClassNames.STRINGCLASSNAME)) {
					arrElemArr[i] = CmdFactory.createArrayElem(
							arrElemClassName, null, Array.get(arg, i)
									.toString(), false);
				} else if (arrElemClassName.equals(ClassNames.FILECLASSNAME)) {
					/*
					 * special handling for file objects within arrays
					 */
					File arrElemFile = (File) arrElem;
					arrElemArr[i] = CmdFactory.createArrayElem(
							arrElemClassName, null, arrElemFile.getName(),
							false);
				} else {
					// try to find the argument within ClientInstanceManager...
					InterfaceID argID = getInterfaceIDForProxyArg(Array.get(
							arg, i).getClass(), Array.get(arg, i));
					if (argID == null) {
						throw new Exception("ClientInterfaceID for \""
								+ Array.get(arg, i).getClass().getName()
								+ "\" not found");
					} else {
						arrElemArr[i] = CmdFactory.createArrayElem(argID
								.getClientClassName(), argID.getInstanceID(),
								null, false);
					}
				}
			} // end of for all array elements
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.sap.sdm.client_server.api.common.RemoteCallArg#getInterfaceName()
	 */
	public String getInterfaceName() {
		return this.ifName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.RemoteCallArg#getInstanceID()
	 */
	public String getInstanceID() {
		return this.instanceID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.RemoteCallArg#getValue()
	 */
	public String getValue() {
		return this.value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.RemoteCallArg#getArrElemArr()
	 */
	public ArrayElem[] getArrElemArr() {
		return this.arrElemArr;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sap.sdm.client_server.api.common.RemoteCallArg#isArray()
	 */
	public boolean isArray() {
		return this.isArray;
	}

	public boolean isNull() {
		return this.isNull;
	}

	public InterfaceID getInterfaceIDForProxyArg(Class proxyClass, Object arg)
			throws Exception {
		if (Proxy.isProxyClass(proxyClass)) {
			InterfaceID argID = ((ClientInvocationHandler) Proxy
					.getInvocationHandler(arg)).getClientInterfaceID();
			if (argID == null) {
				throw new Exception("ClientInterfaceID for \""
						+ proxyClass.getName() + "\" not found");
			} else {
				return argID;
			}
		} else {
			throw new Exception("Error: argument is no primitive and no proxy");
		}
	}
}
