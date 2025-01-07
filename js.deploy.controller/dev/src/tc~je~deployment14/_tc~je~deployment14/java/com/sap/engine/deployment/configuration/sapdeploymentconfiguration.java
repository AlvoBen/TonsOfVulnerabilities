package com.sap.engine.deployment.configuration;

import java.io.InputStream;
import java.io.OutputStream;
import javax.enterprise.deploy.model.*;
import javax.enterprise.deploy.spi.DConfigBeanRoot;
import javax.enterprise.deploy.spi.DeploymentConfiguration;

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;
import com.sap.engine.deployment.SAPDeploymentManager;
import com.sap.engine.deployment.Logger;
import com.sap.engine.deployment.exceptions.SAPBeanNotFoundException;
import com.sap.engine.deployment.exceptions.SAPConfigurationException;

/**
 * An interface that defines a container for all the server-specific
 * configuration information for a single top-level J2EE module. The
 * DeploymentConfiguration object could represent a single stand alone module or
 * an EAR file that contains several sub-modules.
 * 
 * @author Mariela Todorova
 */
public class SAPDeploymentConfiguration implements DeploymentConfiguration {
	private static final Location location = Location
			.getLocation(SAPDeploymentConfiguration.class);
	private DeployableObject deployableObject = null;
	private SAPDeploymentManager deploymentManager = null;

	public SAPDeploymentConfiguration(DeployableObject dObject,
			SAPDeploymentManager manager) {
		this.deployableObject = dObject;
		this.deploymentManager = manager;
		Logger
				.trace(location, Severity.DEBUG,
						"SAP Deployment Configuration for deployable object "
								+ dObject);
	}

	/**
	 * Returns an object that provides access to the deployment descriptor data
	 * and classes of a J2EE module.
	 * 
	 * @return DeployableObject
	 */
	public DeployableObject getDeployableObject() {
		return this.deployableObject;
	}

	/**
	 * Returns the top level configuration bean, DConfigBeanRoot, associated
	 * with the deployment descriptor represented by the designated DDBeanRoot
	 * bean.
	 * 
	 * @param bean
	 *            The top level bean that represents the associated deployment
	 *            descriptor.
	 * @return the DConfigBeanRoot for editing the server-specific properties
	 *         required by the module.
	 * @throws ConfigurationException
	 *             reports errors in generating a configuration bean
	 */
	public DConfigBeanRoot getDConfigBeanRoot(DDBeanRoot bean)
			throws SAPConfigurationException {
		return null;
	}

	/**
	 * Remove the root DConfigBean and all its children.
	 * 
	 * @param bean
	 *            the top leve DConfigBean to remove.
	 * @throws BeanNotFoundException
	 *             the bean provides is not in this beans child list.
	 */
	public void removeDConfigBean(DConfigBeanRoot bean)
			throws SAPBeanNotFoundException {
	}

	/**
	 * Restore from disk to instantated objects all the DConfigBeans associated
	 * with a specific deployment descriptor. The beans may be fully or
	 * partially configured.
	 * 
	 * @param inputArchive
	 *            The input stream for the file from which the DConfigBeans
	 *            should be restored.
	 * @param bean
	 *            The DDBeanRoot bean associated with the deployment descriptor
	 *            file.
	 * @return The top most parent configuration bean, DConfigBeanRoot
	 * @throws ConfigurationException
	 *             reports errors in generating a configuration bean
	 */
	public DConfigBeanRoot restoreDConfigBean(InputStream inputArchive,
			DDBeanRoot bean) throws SAPConfigurationException {
		return null;
	}

	/**
	 * Save to disk all the configuration beans associated with a particular
	 * deployment descriptor file. The saved data may be fully or partially
	 * configured DConfigBeans. The output file format is recommended to be XML.
	 * 
	 * @param outputArchive
	 *            The output stream to which the DConfigBeans should be saved.
	 * @param bean
	 *            The top level bean, DConfigBeanRoot, from which to be save.
	 * @throws ConfigurationException
	 *             reports errors in generating a configuration bean
	 */
	public void saveDConfigBean(OutputStream outputArchive, DConfigBeanRoot bean)
			throws SAPConfigurationException {
	}

	/**
	 * Restore from disk to a full set of configuration beans previously stored.
	 * 
	 * @param inputArchive
	 *            The input stream from which to restore the Configuration.
	 * @throws ConfigurationException
	 *             reports errors in generating a configuration bean
	 */
	public void restore(InputStream inputArchive)
			throws SAPConfigurationException {
	}

	/**
	 * Save to disk the current set configuration beans created for this
	 * deployable module. It is recommended the file format be XML.
	 * 
	 * @param outputArchive
	 *            The output stream to which to save the Configuration.
	 * @throws ConfigurationException
	 */
	public void save(OutputStream outputArchive)
			throws SAPConfigurationException {
	}

	public SAPDeploymentManager getDeploymentManager() {
		return this.deploymentManager;
	}

}
