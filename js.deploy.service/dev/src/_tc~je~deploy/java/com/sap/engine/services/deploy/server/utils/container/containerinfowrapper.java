package com.sap.engine.services.deploy.server.utils.container;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sap.engine.services.deploy.container.Component;
import com.sap.engine.services.deploy.container.ContainerInfo;
import com.sap.engine.services.deploy.container.rtgen.Generator;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.logging.DSLog;
import com.sap.engine.services.deploy.logging.DSLogConstants;
import com.sap.engine.services.deploy.server.utils.ServiceUtils;
import com.sap.tc.logging.Location;

/**
 * Implement parse of containers-info.xml and setting of all properties in base
 * class
 * 
 * @author I043963
 * 
 */
public class ContainerInfoWrapper extends ContainerInfo {
	
	private static final Location location = 
		Location.getLocation(ContainerInfoWrapper.class);

	private static final long serialVersionUID = 6647789669241958420L;
	private boolean forceServiceStart;
	private boolean hasGenerator;
	private boolean hasModuleDetector;

	// xml tag constants
	private final static String TAG_NAME = "name";
	private final static String TAG_IS_J2EE_CONTAINER = "isJ2EEContainer";
	private final static String TAG_J2EE_MODULE_NAME = "j2eeModuleName";
	private final static String TAG_MODULE_NAME = "moduleName";
	private final static String TAG_PRIORITY = "priority";
	private final static String TAG_CLASS_LOAD_PRIORITY = "classLoadPriority";
	private final static String TAG_FILE_NAMES = "fileNames";
	private final static String TAG_FILE_EXTENSIONS = "fileExtensions";
	private final static String TAG_RESOURCE_TYPES = "resourceTypes";
	private final static String TAG_SOFTWARE_TYPES = "softwareTypes";
	private final static String TAG_SUPPORT_SINGLE_FILE_UPDATE = "supportsSingleFileUpdate";
	private final static String TAG_SUPPORT_LAZY_START = "supportsLazyStart";
	private final static String TAG_SUPPORT_PARALLELISM = "supportingParallelism";
	private final static String TAG_NEED_START_INITIALLY = "needStartInitially";
	private final static String TAG_FORCE_SERVICE_START = "forceServiceStart";
	private final static String TAG_HAS_GENERATOR = "hasGenerator";
	private final static String TAG_HAS_MODULE_DESTECTOR = "hasModuleDetector";
	private final static String TAG_CONDITIONAL_FILE_NAMES = "conditionalFileNames";
	private final static String TAG_SOFTWARE_SUB_TYPES = "softwareSubTypes";
	private final static String TAG_IS_CONTENT_HANDLER = "isContentHandler";

	// The name of the WEB container
	private final static String WEB_CONTAINER_NAME = "servlet_jsp";
	// The name of the EJB container
	private final static String EJB_CONTAINER_NAME = "EJBContainer";
	// The name of the connector container
	private final static String CONNECTOR_CONTAINER_NAME = "connector";
	// The name of the appclient container(java type)
	private final static String APP_CONTAINER_NAME = "appclient";

	private String getXMLElemValue(final String tagName, final Element elem) {
		NodeList nodes = elem.getElementsByTagName(tagName);
		if (nodes != null && nodes.item(0) != null) {
			return nodes.item(0).getChildNodes().item(0).getNodeValue();
		}
		return null;
	}

	private String[] getStrings(final String tagName, final Element elem) {
		String[] result = null;
		NodeList nodes = elem.getElementsByTagName(tagName);
		if (nodes != null && nodes.item(0) != null) {
			Element element = (Element) (nodes.item(0));
			NodeList items = element.getElementsByTagName(TAG_NAME);
			result = new String[items.getLength()];
			for (int i = 0; i < items.getLength(); ++i) {
				result[i] = items.item(i).getChildNodes().item(0)
						.getNodeValue();
			}
		}
		return result;
	}

	/**
	 * Parse xml part of info parameter and set members.
	 * 
	 * @param info
	 *            - An XML element containing container specific info.
	 * @param comp
	 *            - Component that provides this container, either Service or
	 *            Application.
	 */
	public ContainerInfoWrapper(Element info, Component comp) {
		this
				.setName(info.getAttributes().getNamedItem(TAG_NAME)
						.getNodeValue());
		String value = getXMLElemValue(TAG_IS_J2EE_CONTAINER, info);
		if (value != null) {
			this.setJ2EEContainer(Boolean.parseBoolean(value));
		}
		setJ2EEModuleName();
		value = getXMLElemValue(TAG_MODULE_NAME, info);
		if (value != null) {
			this.setModuleName(value);
		}
		value = getXMLElemValue(TAG_PRIORITY, info);
		if (value != null) {
			this.setPriority(Integer.parseInt(value));
		}
		value = getXMLElemValue(TAG_CLASS_LOAD_PRIORITY, info);
		if (value != null) {
			this.setClassLoadPriority(Integer.parseInt(value));
		}
		this.setFileNames(getStrings(TAG_FILE_NAMES, info));
		this.setFileExtensions(getStrings(TAG_FILE_EXTENSIONS, info));
		this.setResourceTypes(getStrings(TAG_RESOURCE_TYPES, info));
		this.setSoftwareTypes(getStrings(TAG_SOFTWARE_TYPES, info));
		this.setSoftwareSubTypes(getStrings(TAG_SOFTWARE_SUB_TYPES, info));
		value = getXMLElemValue(TAG_SUPPORT_SINGLE_FILE_UPDATE, info);
		if (value != null) {
			this.setSupportingSingleFileUpdate(Boolean.parseBoolean(value));
		}
		value = getXMLElemValue(TAG_SUPPORT_LAZY_START, info);
		if (value != null) {
			this.setSupportingLazyStart(Boolean.parseBoolean(value));
		}
		value = getXMLElemValue(TAG_SUPPORT_PARALLELISM, info);
		if (value != null) {
			this.setSupportingParallelism(Boolean.parseBoolean(value));
		}
		value = getXMLElemValue(TAG_NEED_START_INITIALLY, info);
		if (value != null) {
			this.setNeedStartInitially(Boolean.parseBoolean(value));
		}
		value = getXMLElemValue(TAG_FORCE_SERVICE_START, info);
		if (location.beDebug()) {
			DSLog.traceDebug(
							location,
							"The containers-info.xml isForceServiceStart has value:[{0}].",
							value);
		}
		if (value != null) {
			this.forceServiceStart = Boolean.parseBoolean(value);
			if (location.beDebug()) {
				DSLog.traceDebug(
								location,
								"The containers-info.xml isForceServiceStart has non-null value:[{0}].",
								this.forceServiceStart);
			}
		}
		value = getXMLElemValue(TAG_HAS_MODULE_DESTECTOR, info);
		if (value != null) {
			this.hasModuleDetector = Boolean.parseBoolean(value);
		}
		value = getXMLElemValue(TAG_HAS_GENERATOR, info);
		if (value != null) {
			this.hasGenerator = Boolean.parseBoolean(value);
		}
		this.setComponent(comp);
		this.setConditionalFileNames(getStrings(TAG_CONDITIONAL_FILE_NAMES,
				info));
		value = getXMLElemValue(TAG_IS_CONTENT_HANDLER, info);
		if (value != null) {
			this.setContentHandler(Boolean.parseBoolean(value));
		}

	}

	private void setJ2EEModuleName() {
		if (this.getName().equals(WEB_CONTAINER_NAME)) {
			this.setJ2EEModuleName(J2EEModule.Type.web.name());
		} else if (this.getName().equals(EJB_CONTAINER_NAME)) {
			this.setJ2EEModuleName(J2EEModule.Type.ejb.name());
		} else if (this.getName().equals(CONNECTOR_CONTAINER_NAME)) {
			this.setJ2EEModuleName(J2EEModule.Type.connector.name());
		} else if (this.getName().equals(APP_CONTAINER_NAME)) {
			this.setJ2EEModuleName(J2EEModule.Type.java.name());
		}
	}

	/**
	 * @return if the service of container have to be started at call of
	 *         getContainerInfo() method
	 */
	public boolean isForceServiceStart() {
		return this.forceServiceStart;
	}

	/**
	 * Check if there is module detector provided by this container and if there
	 * is one try to start service and than return the provided object
	 * reference.
	 */
	@Override
	public ModuleDetector getModuleDetector() {
		if (this.hasModuleDetector) {
			ServiceUtils.startComponentAndWaitRE(this.getComponent(), this
					.getName());
		}
		return super.getModuleDetector();
	}

	/**
	 * Check if there is generator provided by this container and if there is
	 * one try to start service and than return the provided object reference.
	 */
	@Override
	public Generator getGenerator() {
		if (this.hasGenerator) {
			ServiceUtils.startComponentAndWaitRE(this.getComponent(), this
					.getName());
		}
		return super.getGenerator();
	}

	/**
	 * Test the compatibility of this container info and the one provide as
	 * parameter.
	 * 
	 * @param containerInfo
	 * @return True if the containerInfo parameter is compatible.
	 */
	public String checkCompatibility(ContainerInfo containerInfo) {
		StringBuffer result = new StringBuffer();
		if (!testString(this.getName(), containerInfo.getName())) {
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_NAME_DIFFERENT, new Object[] {
							this.getName(), containerInfo.getName() }));
		}
		if (!testString(this.getServiceName(), containerInfo.getServiceName())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_SERVICE_NAME_DIFFERENT, new Object[] {
							this.getServiceName(),
							containerInfo.getServiceName() }));
		}
		if (!testString(this.getModuleName(), containerInfo.getModuleName())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_MODULE_NAME_DIFFERENT,
					new Object[] { this.getModuleName(),
							containerInfo.getModuleName() }));
		}
		if (!testString(this.getJ2EEModuleName(), containerInfo
				.getJ2EEModuleName())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_J2EEMUDULE_NAME_DIFFERENT,
					new Object[] { this.getJ2EEModuleName(),
							containerInfo.getJ2EEModuleName() }));
		}
		if (this.getPriority() != containerInfo.getPriority()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_PRIORITY_DIFFERENT, new Object[] {
							this.getPriority(), containerInfo.getPriority() }));
		}
		if (this.getClassLoadPriority() != containerInfo.getClassLoadPriority()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_CLASS_LOAD_PRIORITY_DIFFERENT,
					new Object[] { this.getClassLoadPriority(),
							containerInfo.getClassLoadPriority() }));
		}
		if (this.isJ2EEContainer() != containerInfo.isJ2EEContainer()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_J2EE_CONTAINER_DIFFERENT, new Object[] {
							this.isJ2EEContainer(),
							containerInfo.isJ2EEContainer() }));
		}
		if (this.isSupportingSingleFileUpdate() != containerInfo
				.isSupportingSingleFileUpdate()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_SUPPORT_SINGLE_FILE_UPDATE_DIFFERENT,
					new Object[] { this.isSupportingSingleFileUpdate(),
							containerInfo.isSupportingSingleFileUpdate() }));
		}
		if (this.isSupportingLazyStart() != containerInfo
				.isSupportingLazyStart()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_SUPPORT_LAZY_START_DIFFERENT,
					new Object[] {this.isSupportingLazyStart(), containerInfo
							.isSupportingLazyStart()}));
		}
		if (this.isSupportingParallelism() != containerInfo
				.isSupportingParallelism()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_SUPPORT_PARALLELISM_DIFFERENT,
					new Object[] { this.isSupportingParallelism(),
							containerInfo.isSupportingParallelism() }));
		}
		if (this.isNeedStartInitially() != containerInfo.isNeedStartInitially()) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_NEED_START_INITIALLY_DIFFERENT,
					new Object[] { this.isNeedStartInitially(),
							containerInfo.isNeedStartInitially() }));
		}
		if (!checkExtendStrings(this.getFileExtensions(), containerInfo
				.getFileExtensions())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_FILE_EXTENSIONS_DIFFERENT,
					new Object[] { CAConvertor.toString(this.getFileExtensions(), ""),
							CAConvertor.toString(containerInfo.getFileExtensions(), "") }));
		}
		if (!checkExtendStrings(this.getFileNames(), containerInfo
				.getFileNames())) {
			result.append(CAConstants.EOL);
			result.append(DSLog
					.getLocalizedMessage(
							DSLogConstants.INFO_FILE_NAMES_DIFFERENT,
							new Object[] { CAConvertor.toString(this.getFileNames(), ""),
									CAConvertor.toString(containerInfo.getFileNames(), "") }));
		}
		if (!checkExtendStrings(this.getResourceTypes(), containerInfo
				.getResourceTypes())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_RESOURCE_TYPES_DIFFERENT, new Object[] {
							CAConvertor.toString(this.getResourceTypes(), ""),
							CAConvertor.toString(containerInfo.getResourceTypes(), "") }));
		}
		if (!checkExtendStrings(this.getSoftwareTypes(), containerInfo
				.getSoftwareTypes())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.INFO_SOFTWARE_TYPES_DIFFERENT, new Object[] {
							CAConvertor.toString(this.getSoftwareTypes(), ""),
									CAConvertor.toString(containerInfo.getSoftwareTypes(), "") }));
		}
		if (!checkExtendStrings(this.getConditionalFileNames(), containerInfo
				.getConditionalFileNames())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.CONDITIONAL_FILE_NAMES_DIFFERENT,
					new Object[] { CAConvertor.toString(this.getConditionalFileNames(), ""),
							CAConvertor.toString(containerInfo.getConditionalFileNames(), "")}));
		}
		if (!checkExtendStrings(this.getSoftwareSubTypes(), containerInfo
				.getSoftwareSubTypes())) {
			result.append(CAConstants.EOL);
			result.append(DSLog.getLocalizedMessage(
					DSLogConstants.SOFTWARE_SUB_TYPES_DIFFERENT, new Object[] {
							CAConvertor.toString(this.getSoftwareSubTypes(), ""),
							CAConvertor.toString(containerInfo.getSoftwareSubTypes(), "") }));
		}

		return result.length() > 0 ? result.toString() : null;
	}

	private boolean testString(String source, String target) {
		return (source == null) ? target == null : source.equals(target);
	}

	private boolean checkSinArray(String s, String[] list) {
		for (String sl : list) {
			if (s.equals(sl)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkExtendStrings(String[] source, String[] target) {
		if (source == null || target == null) {
			return true;
		}

		for (String s : source) {
			if (!checkSinArray(s, target)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();

		sb.append(toStringSimple());
		sb.append(CAConstants.EOL).append("Component: " + getComponent());
		sb.append("Generator: "
				+ (generator != null ? generator : hasGenerator));
		sb.append(CAConstants.EOL);
		sb
				.append("Module Detecter: "
						+ (moduleDetecter != null ? moduleDetecter
								: hasModuleDetector));
		sb.append(CAConstants.EOL);

		return sb.toString();
	}

}
