package com.sap.engine.services.dc.cm.server.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sap.engine.services.dc.cm.server.ServerFactory;
import com.sap.engine.services.dc.cm.server.spi.OnlineOfflineSoftwareType;
import com.sap.engine.services.dc.cm.server.spi.SoftwareTypeService;
import com.sap.engine.services.dc.repo.SoftwareType;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-9-28
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SoftwareTypeServiceImpl implements SoftwareTypeService {

	// a software type must be either offline, online or post-online
	private Map<String, Set<SoftwareType>> slots2SoftwareTypes = new LinkedHashMap<String, Set<SoftwareType>>(
			3);

	private final Map<SoftwareType, Map<String, String>> softwareType2stAttributes = new HashMap<SoftwareType, Map<String, String>>(
			30);

	private final Map<String, Set<SoftwareType>> softwareTypeProperty2softwareTypes = new HashMap<String, Set<SoftwareType>>(
			30);

	/**
	 * This map holds the relationship between a software type and the types
	 * that it is allowed to depend on
	 */
	private static Map<SoftwareType, Set<SoftwareType>> allowedDependencies = new HashMap<SoftwareType, Set<SoftwareType>>(
			30);

	// tags' names and attributes
	private final String ALLOWED_REFERENCES_TAG = "allowed-references";
	private final String SOFTWARE_TYPES_TAG = "software-types";
	private final String SLOTS_TAG = "slots";

	private final String ID_ATT = "id";
	private final String SLOT_ID_ATT = "slotId";
	private final String NAME_ATT = "name";
	private final String SOFTWARE_TYPE_ID_ATT = "softwareTypeId";
	private final String SUB_TYPE_NAME_ATT = "subTypeName";

	private final String DESCRIPTION_ATT = "description";

	SoftwareTypeServiceImpl(Document configurationDocument) {
		parse(configurationDocument);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getOfflineSoftwareTypes()
	 */
	public Set<SoftwareType> getOfflineSoftwareTypes() {
		return getSoftwareTypesBySlot(OFFLINE_SLOT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getOnlineSoftwareTypes()
	 */
	public Set<SoftwareType> getOnlineSoftwareTypes() {
		return getSoftwareTypesBySlot(ONLINE_SLOT);
	}

	public Set<SoftwareType> getPostOnlineSoftwareTypes() {
		return getSoftwareTypesBySlot(POST_ONLINE_SLOT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getFirstDeployedSoftwareType()
	 */
	public OnlineOfflineSoftwareType getFirstDeployedSoftwareType() {
		return OnlineOfflineSoftwareType.OFFLINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getFirstUndeployedSoftwareType()
	 */
	public OnlineOfflineSoftwareType getFirstUndeployedSoftwareType() {
		return OnlineOfflineSoftwareType.ONLINE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getUnsupportedForUndeploySoftwareTypes()
	 */
	public Set<SoftwareType> getUnsupportedForUndeploySoftwareTypes() {
		Set<SoftwareType> result = new HashSet<SoftwareType>(
				softwareType2stAttributes.keySet());
		result.removeAll(getSoftwareTypesByAttribute(SUPPORTED_FOR_UNDEPLOY));
		return Collections.unmodifiableSet(result);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getApplicationSoftwareTypes()
	 */
	public Set<SoftwareType> getApplicationSoftwareTypes() {
		return getSoftwareTypesByAttribute(APPLICATION_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getFSSoftwareTypes()
	 */
	public Set<SoftwareType> getFSSoftwareTypes() {
		return getSoftwareTypesByAttribute(FS_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getDBSoftwareTypes()
	 */
	public Set<SoftwareType> getDBSoftwareTypes() {
		return getSoftwareTypesByAttribute(DB_TYPE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.sap.engine.services.dc.cm.server.spi.SoftwareTypeService#
	 * getJ2EESoftwareTypes()
	 */
	public Set<SoftwareType> getJ2EESoftwareTypes() {
		return getSoftwareTypesByAttribute(JEE_SOFTWARE_TYPE);
	}

	public Set<SoftwareType> getRollingSoftwareTypes() {
		return getSoftwareTypesByAttribute(ROLLING);
	}

	public Set<SoftwareType> getSafeModeSoftwareTypes() {
		return getSoftwareTypesByAttribute(SUPPORT_SAFE_MODE);
	}

	public Set<SoftwareType> getSoftwareTypesByAttribute(
			final String stAttribute) {
		return Collections
				.unmodifiableSet(this.softwareTypeProperty2softwareTypes
						.get(stAttribute));
	}

	public Map<String, String> getSoftwareTypeAttributes(
			final SoftwareType softwareType) {
		return Collections.unmodifiableMap(this.softwareType2stAttributes
				.get(softwareType));
	}

	public Set<SoftwareType> getSoftwareTypesBySlot(final String slot) {
		return Collections.unmodifiableSet(this.slots2SoftwareTypes.get(slot));
	}

	public Set<SoftwareType> getAllowedDependencies(final SoftwareType type) {

		if (type == null) {
			throw new IllegalArgumentException("The type cannot be null");
		}
		Set<SoftwareType> result = allowedDependencies.get(type);
		if (result == null) {
			throw new IllegalStateException("The software type '" + type
					+ "'is not mapped to a set of allowed dependencies");
		}
		return Collections.unmodifiableSet(result);

	}

	public boolean isDependencyAllowed(final SoftwareType a,
			final SoftwareType b) {
		Set<SoftwareType> allowed = allowedDependencies.get(a);
		if (allowed == null) {
			return false;
		}
		return allowed.contains(b);
	}

	private void parse(Document doc) {
		Element root = doc.getDocumentElement();

		// parse slots
		Node slotsNode = root.getElementsByTagName(SLOTS_TAG).item(0);
		NodeList slots = slotsNode.getChildNodes();

		this.slots2SoftwareTypes = new LinkedHashMap<String, Set<SoftwareType>>(
				slots.getLength());

		for (int i = 0; i < slots.getLength(); i++) {
			Node slot = slots.item(i);
			NamedNodeMap attributesMap = slot.getAttributes();

			if (attributesMap == null) {
				continue;
			}

			Properties attributes = getAttributesAsProperties(attributesMap);

			// ??? what about OnlineOfflineSoftwareType object
			this.slots2SoftwareTypes.put(attributes.getProperty(ID_ATT),
					new HashSet<SoftwareType>(20));

		}
		// --- parse slots

		// parse software types
		Node softwareTypesNode = root.getElementsByTagName(SOFTWARE_TYPES_TAG)
				.item(0);
		NodeList softwareTypeNodes = softwareTypesNode.getChildNodes();

		final Properties ids2SoftwareTypes = new Properties();
		final boolean createSotwareTypes = true;
		for (int i = 0; i < softwareTypeNodes.getLength(); i++) {
			Node softwareTypeNode = softwareTypeNodes.item(i);
			if (softwareTypeNode == null) {
				continue;
			}

			NamedNodeMap attributesMap = softwareTypeNode.getAttributes();
			if (attributesMap == null) {
				continue;
			}

			Properties softwareTypeAtributes = getAttributesAsProperties(attributesMap);

			SoftwareType softwareType = SoftwareType.getSoftwareType(
					new Integer(softwareTypeAtributes.getProperty(ID_ATT)),
					softwareTypeAtributes.getProperty(NAME_ATT),
					softwareTypeAtributes.getProperty(SUB_TYPE_NAME_ATT),
					softwareTypeAtributes.getProperty(DESCRIPTION_ATT),
					createSotwareTypes);

			// init dependencies
			allowedDependencies
					.put(softwareType, new HashSet<SoftwareType>(10));

			Map map = softwareTypeAtributes;

			this.softwareType2stAttributes.put(softwareType, map);
			ids2SoftwareTypes.put(softwareTypeAtributes.get(ID_ATT),
					softwareType);

			Iterator<Entry<Object, Object>> setSoftwareTypeAtributes = softwareTypeAtributes
					.entrySet().iterator();

			while (setSoftwareTypeAtributes.hasNext()) {
				Map.Entry property = setSoftwareTypeAtributes.next();

				String key = (String) property.getKey();
				if (ID_ATT.equals(key) || NAME_ATT.equals(key)
						|| SUB_TYPE_NAME_ATT.equals(key)) {
					continue;
				}
				String value = (String) property.getValue();
				// ignore "unreal" properties
				if ("false".equals(value)) {
					continue;
				}

				// add type to properties
				if (!this.softwareTypeProperty2softwareTypes.containsKey(key)) {
					this.softwareTypeProperty2softwareTypes.put(key,
							new HashSet<SoftwareType>());
				}

				this.softwareTypeProperty2softwareTypes.get(key).add(
						softwareType);

				// add type to slot
				if (SLOT_ID_ATT.equals(key)) {
					this.slots2SoftwareTypes.get(value).add(softwareType);
				}
			}
		}
		// --- parse software types

		// parse allowed references of each softare type
		Node allowedReferencesNode = root.getElementsByTagName(
				ALLOWED_REFERENCES_TAG).item(0);
		NodeList allowedReferenceNodes = allowedReferencesNode.getChildNodes();

		for (int i = 0; i < allowedReferenceNodes.getLength(); i++) {
			Node allowedReferenceNode = allowedReferenceNodes.item(i);
			NamedNodeMap attributesMap = allowedReferenceNode.getAttributes();

			if (attributesMap == null) {
				continue;
			}

			final Properties allowedReferenceAttribute = getAttributesAsProperties(attributesMap);

			final String softwareTypeId = allowedReferenceAttribute
					.getProperty(SOFTWARE_TYPE_ID_ATT);

			final SoftwareType softwareType = (SoftwareType) ids2SoftwareTypes
					.get(softwareTypeId);

			final NodeList seesNode = allowedReferenceNode.getChildNodes();

			// parse see references for each software type
			final Set<SoftwareType> allowedReferences = allowedDependencies
					.get(softwareType);
			for (int k = 0; k < seesNode.getLength(); k++) {
				Node seeNode = seesNode.item(k);
				NamedNodeMap seeAttributesMap = seeNode.getAttributes();
				if (seeAttributesMap == null) {
					continue;
				}
				Properties seeAttributes = getAttributesAsProperties(seeAttributesMap);
				allowedReferences.add((SoftwareType) ids2SoftwareTypes
						.get(seeAttributes.get(SOFTWARE_TYPE_ID_ATT)));

			}
			allowedDependencies.put(softwareType, allowedReferences);
		}
		// -- parse allowed references of each softare type

	}

	private Properties getAttributesAsProperties(final NamedNodeMap namedNodeMap) {
		if (namedNodeMap == null) {
			return null;
		}

		final Properties attributes = new Properties();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			attributes.put(namedNodeMap.item(i).getNodeName(), namedNodeMap
					.item(i).getNodeValue());
		}
		return attributes;
	}

	public String getFirstSlot() {
		if (slots2SoftwareTypes == null || slots2SoftwareTypes.isEmpty()) {
			return null;
		}
		return slots2SoftwareTypes.keySet().iterator().next();
	}

	public String getNextSlot(final String slot) {
		if (slots2SoftwareTypes == null || slots2SoftwareTypes.isEmpty()) {
			return null;
		}

		final Iterator<String> slots = slots2SoftwareTypes.keySet().iterator();
		while (slots.hasNext()) {
			// is current slot but not last
			if (slots.next().equals(slot) && slots.hasNext()) {
				return slots.next();
			}
		}

		return null;
	}

	public boolean isSupported(final SoftwareType type) {
		return softwareType2stAttributes.keySet().contains(type);
	}
}
