/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.editor.impl.first;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import com.sap.engine.services.deploy.ReferenceObject;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.dpl_info.module.ContainerData;
import com.sap.engine.services.deploy.server.dpl_info.module.ResourceReference;
import com.sap.engine.services.deploy.server.properties.PropManager;
import com.sap.engine.services.deploy.server.utils.Convertor;
import com.sap.engine.services.deploy.server.utils.FSUtils;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.tc.logging.Location;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.0
 */
class DIAdapter1 {
	
	private static final Location location = Location.getLocation(DIAdapter1.class);
	private static final Character DELIMITER = new Character('\n');

	// ************* FILES FOR CLASS LOADER *************//

	protected static String[] getFilesForCL(DeploymentInfo dInfo) {
		ValidateUtils.nullValidator(dInfo, "deployment info");

		Set<String> files = new LinkedHashSet<String>();
		final Hashtable cName_cData = dInfo.getCNameAndCData();
		final Enumeration cnEnum = cName_cData.keys();
		String cName = null;
		ContainerData cData = null;
		while (cnEnum.hasMoreElements()) {
			cName = (String) cnEnum.nextElement();
			cData = (ContainerData) cName_cData.get(cName);
			files.addAll(cData.getFilesForCL());
		}

		// absolute -> relative
		files = FSUtils.relativePath(
				PropManager.getInstance().getAppsWorkDir(), files);

		String result[] = null;
		if (files != null && files.size() != 0) {
			result = new String[files.size()];
			files.toArray(result);
		}
		return result;
	}

	protected static void setFilesForCL(DeploymentInfo dInfo,
			String filesForCL[]) {
		ValidateUtils.nullValidator(dInfo, "deployment info");

		if (filesForCL == null || filesForCL.length == 0) {
			return;
		}
		boolean isSet = false;
		final Hashtable cName_cData = dInfo.getCNameAndCData();
		final Enumeration cnEnum = cName_cData.keys();
		String cName = null;
		ContainerData cData = null;
		while (cnEnum.hasMoreElements()) {
			cName = (String) cnEnum.nextElement();
			cData = (ContainerData) cName_cData.get(cName);
			if (!isSet) {
				if (!cData.isOptional() || !cnEnum.hasMoreElements()) {
					if (location.beDebug()) {
						DSLog
								.traceDebug(
										location,
										"The files for class loader [{0}] for application [{1}], are set to container [{2}].",
										Convertor.toString(filesForCL, ""),
										dInfo.getApplicationName(), cData
												.getContName());
					}

					// relative -> absolute
					cData.setFilesForCL(FSUtils.absolutePaths(PropManager
							.getInstance().getAppsWorkDir(), Convertor
							.cObject(filesForCL)));
					isSet = true;
				}
			} else {
				cData.setFilesForCL(null);
			}
		}
		if (!isSet) {
			throw new IllegalStateException(
					"ASJ.dpl_ds.006046 The files for class loader "
							+ Convertor.toString(filesForCL, "")
							+ "cannot be set to application '"
							+ dInfo.getApplicationName()
							+ "', becaue there are not containers on which the application is deployed.");
		}
	}

	// ************* FILES FOR CLASS LOADER *************//

	// ************* REFERENCE OBJECT *************//
	/**
	 * Decodes array of ReferenceObjects. The references, which
	 * getCharacteristic().isPersistent()==false will not be stored in DB
	 * 
	 * @param cfgPath
	 * @param refObjects
	 *            array of ReferenceObjects.
	 * @return decoded array of ReferenceObjects.
	 */
	public static String[] decodeReferenceObjectArray(String cfgPath,
			ReferenceObject[] refObjects) {
		ArrayList refStringsList = null;

		if (refObjects != null) {
			refStringsList = new ArrayList();
			for (int i = 0; i < refObjects.length; i++) {
				if (refObjects[i] != null
						&& refObjects[i].getCharacteristic().isPersistent()) {
					refStringsList.add(decode(refObjects[i]));
				} else {
					if (location.beDebug()) {
						DSLog.traceDebug(
								location,
								"The reference [{0}] won't be persisted.",
								refObjects[i].print(""));
					}
				}
			}
		}

		if (refStringsList == null) {
			return null;
		}
		String refStrings[] = new String[refStringsList.size()];
		refStringsList.toArray(refStrings);
		return refStrings;
	}

	/**
	 * Decodes this ReferenceObject and returns its String representation.
	 * 
	 * @return a String representing this ReferenceObject.
	 */
	private static String decode(ReferenceObject refObj) {
		String decode = "";

		// DO NOT CHANGE THIS ORDER!!!
		decode = decode
				+ (refObj.getReferenceProviderName() == null ? "" : refObj
						.getReferenceProviderName()) + DELIMITER.toString();
		decode = decode
				+ (refObj.getReferenceTarget() == null ? "" : refObj
						.getReferenceTarget()) + DELIMITER.toString();
		decode = decode
				+ (refObj.getReferenceTargetType() == null ? "" : refObj
						.getReferenceTargetType()) + DELIMITER.toString();
		decode = decode
				+ (refObj.getReferenceType() == null ? "" : refObj
						.getReferenceType()) + DELIMITER.toString();

		return decode;
	}

	/**
	 * Encodes array of ReferenceObjects.
	 * 
	 * @param refStrings
	 *            array of ReferenceObjects.
	 * @return encoded array of ReferenceObjects.
	 */
	public static ReferenceObject[] encodeReferenceObjectArray(
			String[] refStrings) {
		ReferenceObject refObject[] = null;

		if (refStrings != null) {
			refObject = new ReferenceObject[refStrings.length];
			for (int i = 0; i < refStrings.length; i++) {
				if (refStrings[i] != null) {
					refObject[i] = encodeReferenceObject(refStrings[i]);
				}
			}
		}

		return refObject;
	}

	/**
	 * Encodes this ReferenceObject using its String representation.
	 * 
	 * @param encode
	 *            a String representing the ReferenceObject.
	 * 
	 * @return the encoded ReferenceObject.
	 * 
	 * @throws IllegalArgumentException
	 *             if the String representation of the ReferenceObject is not
	 *             correct.
	 */
	private static ReferenceObject encodeReferenceObject(String encode)
			throws IllegalArgumentException {
		if (encode == null) {
			return null;
		}

		ReferenceObject refObject = new ReferenceObject();
		String token = null;

		// DO NOT CHANGE THIS ORDER!!!
		StringTokenizer tokenizer = new StringTokenizer(encode, DELIMITER
				.toString(), true);

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				refObject.setReferenceProviderName(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ReferenceProviderName",
							"ReferenceObject");
				}
			}
		} else {
			throwException(encode, "ReferenceProviderName", "ReferenceObject");
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				refObject.setReferenceTarget(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ReferenceTarget", "ReferenceObject");
				}
			}
		} else {
			throwException(encode, "ReferenceTarget", "ReferenceObject");
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				refObject.setReferenceTargetType(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ReferenceTargetType",
							"ReferenceObject");
				}
			}
		} else {
			throwException(encode, "ReferenceTargetType", "ReferenceObject");
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				refObject.setReferenceType(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ReferenceType", "ReferenceObject");
				}
			}
		} else {
			throwException(encode, "ReferenceType", "ReferenceObject");
		}

		return refObject;
	}

	// ************* REFERENCE OBJECT *************//

	// ************* RESOURCE REFERENCE *************//
	/**
	 * Decodes this ResourceObject and returns its String representation.
	 * 
	 * @return a String representing this ResourceObject.
	 */
	private static String decode(ResourceReference resRef) {
		String res = "";

		// DO NOT CHANGE THIS ORDER!!!
		res += (((resRef.getResRefName() == null) ? "" : resRef.getResRefName()) + DELIMITER);
		res += (((resRef.getResRefType() == null) ? "" : resRef.getResRefType()) + DELIMITER);
		res += (((resRef.getReferenceType() == null) ? "" : resRef
				.getReferenceType()) + DELIMITER);

		return res;
	}

	/**
	 * Encodes this ResourceObject using its String representation.
	 * 
	 * @param encode
	 *            a String representing the ReferenceObject.
	 * 
	 * @return the encoded ResourceObject.
	 * 
	 * @throws IllegalArgumentException
	 *             if the String representation of the ResourceObject is not
	 *             correct.
	 */
	private static ResourceReference encodeResourceReference(String encode)
			throws IllegalArgumentException {
		if (encode == null) {
			return null;
		}

		ResourceReference resRef = new ResourceReference();
		String token = null;

		// DO NOT CHANGE THIS ORDER!!!
		StringTokenizer tokenizer = new StringTokenizer(encode, DELIMITER
				.toString(), true);

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				resRef.setResRefName(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ResourceName", "ResourceReference");
				}
			}
		} else {
			throwException(encode, "ResourceName", "ResourceReference");
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				resRef.setResRefType(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ResourceType", "ResourceReference");
				}
			}
		} else {
			throwException(encode, "ResourceType", "ResourceReference");
		}

		if (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			if (!token.equals(DELIMITER.toString())) {
				resRef.setReferenceType(token);
				if (tokenizer.hasMoreTokens()) {
					tokenizer.nextToken();
				} else {
					throwException(encode, "ReferenceType", "ResourceReference");
				}
			}
		} else {
			throwException(encode, "ReferenceType", "ResourceReference");
		}

		return resRef;
	}

	public static String[] decode(Set resourceReferences) {
		String result[] = null;
		if (resourceReferences != null && resourceReferences.size() != 0) {
			result = new String[resourceReferences.size()];
			final Iterator rrIter = resourceReferences.iterator();
			int i = -1;
			while (rrIter.hasNext()) {
				result[++i] = decode((ResourceReference) rrIter.next());
			}
		}
		return result;
	}

	public static Set encode(String resourceReferences[])
			throws IllegalArgumentException {
		Set result = null;
		if (resourceReferences != null) {
			result = new LinkedHashSet();
			ResourceReference resRef = null;
			for (int i = 0; i < resourceReferences.length; i++) {
				resRef = encodeResourceReference(resourceReferences[i]);
				result.add(resRef);
			}
		}
		return result;
	}

	public static void setResourceReferences(DeploymentInfo dInfo, Set resRefs) {
		ValidateUtils.nullValidator(dInfo, "deployment info");

		if (resRefs == null || resRefs.size() == 0) {
			return;
		}
		boolean isSet = false;
		final Hashtable cName_cData = dInfo.getCNameAndCData();
		final Enumeration cnEnum = cName_cData.keys();
		String cName = null;
		ContainerData cData = null;
		while (cnEnum.hasMoreElements()) {
			cName = (String) cnEnum.nextElement();
			cData = (ContainerData) cName_cData.get(cName);
			if (!isSet) {
				if (!cData.isOptional() || !cnEnum.hasMoreElements()) {
					if (location.beDebug()) {
						DSLog.traceDebug(location, 
										"The resource references [{0}] for application [{1}] are set to container [{2}].",
										Convertor.toString(resRefs, ""), dInfo
												.getApplicationName(), cData
												.getContName());
					}
					cData.setResourceReferences(resRefs);
					isSet = true;
				}
			} else {
				cData.setFilesForCL(null);
			}
		}
		if (!isSet) {
			throw new IllegalStateException(
					"ASJ.dpl_ds.006047 The resource references "
							+ Convertor.toString(resRefs, "")
							+ "cannot be set to application '"
							+ dInfo.getApplicationName()
							+ "', becaue there are not containers on which the application is deployed.");
		}
	}

	// ************* RESOURCE REFERENCE *************//

	private static void throwException(String encode, String what, String type)
			throws IllegalArgumentException {
		final IllegalArgumentException iae = new IllegalArgumentException(
				"ASJ.dpl_ds.006048 Error in decoding [" + encode + "] into "
						+ type + ", because of wrong string."
						+ "Could not encode [" + what + "].");
		DSLog.logErrorThrowable(location, iae);
		throw iae;
	}
}
