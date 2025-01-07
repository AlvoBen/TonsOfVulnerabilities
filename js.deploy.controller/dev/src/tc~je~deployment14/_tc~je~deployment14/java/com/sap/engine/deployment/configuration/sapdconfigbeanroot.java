package com.sap.engine.deployment.configuration;

import javax.enterprise.deploy.model.DDBeanRoot;
import javax.enterprise.deploy.spi.DConfigBean;
import javax.enterprise.deploy.spi.DConfigBeanRoot;

/**
 * A J2EE component module consists of one or more deployment descriptor files
 * and zero or more non-deployment descriptor XML instance documents. A module
 * must contain a component-specific deployment descriptor file (see the
 * component specification for details). It may contain one or more secondary
 * deployment descriptor files that define extra functionality on the component
 * and zero or more non-deployment descriptor XML instance documents (see the
 * Web Services specification).
 * 
 * <p>
 * The DConfigBeanRoot object is a deployment configuration bean (DConfigBean)
 * that is associated with the root of the component's deployment descriptor. It
 * must be created by calling the
 * DeploymentConfiguration.getDConfigBean(DDBeanRoot) method, where DDBeanRoot
 * represents the component's deployment descriptor.
 * 
 * <p>
 * A DConfigBean object is associated with a deployment descriptor that extends
 * a component's functionality. It must be created by calling the
 * DConfigBeanRoot.getDConfigBean(DDBeanRoot) method. This DConfigBean object is
 * a child of the compontent's DConfigBeanRoot object. The DDBeanRoot argument
 * represents the secondary deployment descriptor. Deployment descriptor files
 * such as webservice.xml and webserviceclient.xml are examples of secondary
 * deployment descriptor files.
 * 
 * <p>
 * The server plugin must request a DDBeanRoot object for any non-deployment
 * descriptor XML instance document data it requires. The plugin must call
 * method DeployableObject.getDDBeanRoot(String) where String is the full path
 * name from the root of the module to the file to be represented. A WSDL file
 * is an example of a non-deployment descriptor XML instance document.
 * 
 * @author Mariela Todorova
 */
public class SAPDConfigBeanRoot extends SAPDConfigBean implements
		DConfigBeanRoot {
	private DDBeanRoot ddBeanRoot = null;

	/**
	 * Return a DConfigBean for a deployment descriptor that is not the module's
	 * primary deployment descriptor. Web services provides a deployment
	 * descriptor in addition to the module's primary deployment descriptor.
	 * Only the DDBeanRoot for this catagory of secondary deployment descriptors
	 * are to be passed as arguments through this method.
	 * 
	 * Web service has two deployment descriptor files, one that defines the web
	 * service and one that defines a client of a web service. See the Web
	 * Service specificiation for the details.
	 * 
	 * @param ddBeanRoot
	 *            represents the root element of a deployment descriptor file.
	 * 
	 * @return a DConfigBean to be used for processing this deployment
	 *         descriptor data. Null may be returned if no DConfigBean is
	 *         required for this deployment descriptor.
	 */
	public DConfigBean getDConfigBean(DDBeanRoot ddBeanRoot) {
		SAPDConfigBean configBean = new SAPDConfigBean();
		configBean.setDDBean(ddBeanRoot);
		return configBean;
	}

}
