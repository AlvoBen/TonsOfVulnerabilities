package com.sap.engine.services.dc.gd.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.naming.NamingException;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.lib.jar.JarExtractor;
import com.sap.engine.services.accounting.Accounting;
import com.sap.engine.services.dc.cm.deploy.DeploymentItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.undeploy.UndeployItem;
import com.sap.engine.services.dc.cm.undeploy.UndeployItemStatus;
import com.sap.engine.services.dc.cm.utils.statistics.TimeStatisticsEntry;
import com.sap.engine.services.dc.gd.DeliveryException;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.util.exception.DCExceptionConstants;
import com.sap.engine.services.deploy.DeployService;
import com.sap.engine.services.deploy.container.ComponentNotDeployedException;
import com.sap.engine.services.deploy.container.WarningException;
import com.sap.tc.logging.Location;

/**
 * @author Rumiana Angelova
 * @version 7.0
 */
class LibraryDeployer extends Deployer {
	
	private Location location = getLocation(this.getClass());

	private static LibraryDeployer INSTANCE;

	private final DeployService deployService;

	static synchronized LibraryDeployer getInstance() throws DeliveryException {
		if (INSTANCE == null) {
			INSTANCE = new LibraryDeployer();
		}

		return INSTANCE;
	}

	private LibraryDeployer() throws DeliveryException {
		try {
			this.deployService = ServiceConfigurer.getInstance()
					.getDeployService();
		} catch (NamingException nex) {
			throw new DeliveryException(DCExceptionConstants.GET_DS, nex);
		}
	}

	void perform620Deployment(DeploymentItem deploymentItem)
			throws DeliveryException {
		try {

			/* DC name and vendor are used as library name and provider */
			String dcName = null;
			String dcVendor = null;
			File newSdaFile = null;
			File jarRoot = null;
			String[] jarPathList = null;
			newSdaFile = new File(deploymentItem.getSduFilePath());
			/* determine info on DC of SDA */
			dcName = deploymentItem.getSda().getName();
			dcVendor = deploymentItem.getSda().getVendor();

			if ((dcName == null) || (dcName.equals(""))) {
				throw new DeliveryException(DCExceptionConstants.DC_EMPTY,
						new String[] { deploymentItem.toString() });
			}
			if ((dcVendor == null) || (dcVendor.equals(""))) {
				throw new DeliveryException(DCExceptionConstants.VENDOR_EMPTY,
						new String[] { deploymentItem.toString() });
			}

			/* Determine extraction dir for jars and create it, if not existent */
			File sdaParent = newSdaFile.getParentFile();
			jarRoot = new File(sdaParent, "work");
			if (jarRoot.exists()) {
				FileUtils.deleteDirectory(jarRoot);
			}
			jarRoot.mkdirs();
			/*
			 * get JarSL instance for SDA, unpack SDA and get list of contained
			 * files. First unpack, then get list of files. Otherwise JarSL may
			 * read archive twice
			 */
			JarExtractor extractor = new JarExtractor();
			try {
				extractor.extractJar(newSdaFile.getPath(), jarRoot.getPath());
				String[] sdaFileList = this.getJarList(newSdaFile);
				/*
				 * build list of absolute path names of jars in the extraction
				 * directory
				 */
				/* count the jars */
				if (sdaFileList != null) {
					for (int i = 0; i < sdaFileList.length; i++) {
						sdaFileList[i] = jarRoot.getPath()
								+ File.separator
								+ sdaFileList[i].replace('/',
										File.separatorChar);
					}
				}

				// String[] references =
				// this.getNamesOfReferencedLibs(deploymentItem.getSda());
				/* now perform action */
				// String lib630 = makeLibraryJar(dcName, jarPathList,
				// references, dcVendor);
				// deployService.deployLibrary();
			} catch (IOException ioex) {// $JL-EXC$
			} catch (Exception we) {// $JL-EXC$
			} finally {
				FileUtils.deleteDirectory(jarRoot);
			}

			// TODO clean extraction directory

		} finally {
		}

	}

	// private String makeLibraryJar(String libName, String[] jars, String[]
	// references, String providerName) throws Exception {
	// Vector entries = new Vector();
	//
	// String[] mappings = new String[jars.length];
	// InfoObject info = new InfoObject("");
	// for (int i = 0; i < jars.length; i++) {
	// if (mappings[i] == null || mappings[i].trim().equals("")) {
	// mappings[i] = (new File(jars[i])).getName();
	// }
	// info = new InfoObject(mappings[i], jars[i]);
	// entries.add(info);
	// }
	// //make provider xml
	// String libJarName = null;
	// if (libName.toLowerCase().startsWith("library:")) {
	// libJarName = libName.substring("library:".length());
	// } else {
	// libJarName = libName;
	// }
	// String tempProviderXml = null;
	// tempProviderXml = new File(getTempLibDir(false),
	// "TempProvider.xml").getAbsolutePath();
	// libJarName = (new File(getTempLibDir(false), libJarName +
	// ".jar")).getAbsolutePath();
	// LibProviderDocument provider = new LibProviderDocument();
	// provider.setDisplayName(libName);
	// provider.setComponentName(libName);
	// provider.setJars(mappings);
	// provider.setProviderName(providerName);
	// Hashtable refs = new Hashtable();
	// Hashtable strength = new Hashtable();
	// String refName = null;
	// if (references != null) {
	// for (int i = 0; i < references.length; i++) {
	// if (references[i].equals("j2eestandard") ||
	// references[i].equals("library:j2eestandard")) {
	// refName = "library:J2EEStandard";
	// } else if (references[i].equals("inqmyxml") ||
	// references[i].equals("library:inqmyxml")) {
	// refName = "library:sapxmltoolkit";
	// } else if (references[i].equals("SAPXMLToolkit") ||
	// references[i].equals("library:SAPXMLToolkit")) {
	// refName = "library:sapxmltoolkit";
	// } else if (references[i].equals("loggingStandard") ||
	// references[i].equals("library:loggingStandard")) {
	// refName = "library:com.sap.tc.Logging";
	// } else if (references[i].equals("j2eeCA") ||
	// references[i].equals("library:j2eeCA")) {
	// refName = "library:j2eeca";
	// } else if (references[i].equals("ejb") ||
	// references[i].equals("library:ejb")) {
	// refName = "library:ejb20";
	// } else if (references[i].equals("server-xml") ||
	// references[i].equals("library:server-xml")) {
	// refName = "library:sapxmltoolkit";
	// } else if (references[i].equals("iaiksecurity") ||
	// references[i].equals("library:iaiksecurity")) {
	// refName = "library:IAIKSecurity";
	// } else {
	// refName = references[i];
	// }
	// if (refName.startsWith("library:")) {
	// refs.put(refName.substring(8), "library");
	// strength.put(refName.substring(8), "weak");
	// } else if (refName.startsWith("service:")) {
	// refs.put(refName.substring(8), "service");
	// strength.put(refName.substring(8), "weak");
	// } else if (refName.startsWith("application:")) {
	// refs.put(refName.substring(12), "application");
	// strength.put(refName.substring(12), "weak");
	// } else {
	// refs.put(refName, "library");
	// strength.put(refName, "weak");
	// }
	// }
	// }
	// provider.setReferences(refs);
	// provider.setRefStrength(strength);
	// provider.loadDocumentFromFields();
	// // StandardDOMWriter writer = new StandardDOMWriter();
	// writer.write(provider.getMainDocument(), tempProviderXml, provider.dtd,
	// "UTF-8");
	// entries.add(new InfoObject("server/provider.xml", tempProviderXml));
	// JarUtils jarUtil = new JarUtils();
	// jarUtil.makeJarFromFiles(libJarName, entries);
	// (new File(tempProviderXml)).delete();
	// return libJarName;
	// }

	private String[] getJarList(File file) throws IOException {
		JarFile jarFile = null;
		ArrayList list = new ArrayList();
		String[] result = null;
		try {
			jarFile = new JarFile(file);
			Enumeration jarFileEntriesEnum = jarFile.entries();
			ZipEntry entry = null;
			while (jarFileEntriesEnum.hasMoreElements()) {
				entry = (ZipEntry) jarFileEntriesEnum.nextElement();
				if (!entry.isDirectory()
						&& (entry.getName().toLowerCase().endsWith(".zip")
								|| entry.getName().toLowerCase().endsWith(
										".jar") || entry.getName()
								.toLowerCase().endsWith(".rar"))) {
					list.add(entry.getName());
				}
			}
			result = new String[list.size()];
			list.toArray(result);
			return result;
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException ioex) {// $JL-EXC$
				}
			}
		}
	}

	void performDeployment(DeploymentItem deploymentItem)
			throws DeliveryException {
		final String tagName = "Library Deployer";
		deploymentItem.startTimeStatEntry(tagName,
				TimeStatisticsEntry.ENTRY_TYPE_OTHER);
		Accounting.beginMeasure(tagName, DeployService.class);
		try {
			// String[] references =
			// this.getNamesOfReferencedLibs(deploymentItem.getSda());
			try {
				long beginTime = System.currentTimeMillis();
				if (location.bePath()) {
					tracePath(location, 
							"Invoking Deploy Service's deployLibrary operation ...");
				}
				this.deployService.deployLibrary(deploymentItem
						.getSduFilePath());
				if (location.bePath()) {
					tracePath(location, 
							"Deploy Service's deployLibrary operation has finished. Time: [{0}] ms.",
							new Object[] { String.valueOf(System
									.currentTimeMillis()
									- beginTime) });
				}
			} catch (WarningException we) {
				deploymentItem.setDeploymentStatus(DeploymentStatus.WARNING);
				deploymentItem.addDescription("Warnings:\n"
						+ this.concatStrings(we.getWarnings()));
			} catch (RemoteException re) {
				throw new DeliveryException(DCExceptionConstants.DEPLOY,
						new String[] { deploymentItem.getSda().getId()
								.toString() }, re);
			} catch (Exception e) {
				throw new DeliveryException(DCExceptionConstants.DEPLOY,
						new String[] { deploymentItem.getSda().getId()
								.toString() }, e);
			}
		} finally {
			Accounting.endMeasure(tagName);
			deploymentItem.finishTimeStatEntry();
		}
	}

	private void checkReferences(String[] references) {
		if (references != null) {
			for (int i = 0; i < references.length; i++) {
				if (references[i].equals("j2eestandard")
						|| references[i].equals("library:j2eestandard")) {
					references[i] = "library:J2EEStandard";
				} else if (references[i].equals("inqmyxml")
						|| references[i].equals("library:inqmyxml")) {
					references[i] = "library:sapxmltoolkit";
				} else if (references[i].equals("SAPXMLToolkit")
						|| references[i].equals("library:SAPXMLToolkit")) {
					references[i] = "library:sapxmltoolkit";
				} else if (references[i].equals("loggingStandard")
						|| references[i].equals("library:loggingStandard")) {
					references[i] = "library:com.sap.tc.Logging";
				} else if (references[i].equals("j2eeCA")
						|| references[i].equals("library:j2eeCA")) {
					references[i] = "library:j2eeca";
				} else if (references[i].equals("ejb")
						|| references[i].equals("library:ejb")) {
					references[i] = "library:ejb20";
				} else if (references[i].equals("server-xml")
						|| references[i].equals("library:server-xml")) {
					references[i] = "library:sapxmltoolkit";
				} else if (references[i].equals("iaiksecurity")
						|| references[i].equals("library:iaiksecurity")) {
					references[i] = "library:IAIKSecurity";
				}
			}
		}

	}

	void performUndeployment(UndeployItem item) throws DeliveryException {
		final String dcName = item.getSda().getName();
		final String dcVendor = item.getSda().getVendor();
		try {
			if (location.bePath()) {
				tracePath(location, 
						"Invoking Deploy Service's removeLibrary operation ...");
			}

			long beginTime = System.currentTimeMillis();
			final String tagName = "Remove Library";
			Accounting.beginMeasure(tagName, DeployService.class);
			try {
				this.deployService.removeLibrary(dcVendor, dcName);
			} catch (ComponentNotDeployedException cnfe) {
				item.setUndeployItemStatus(UndeployItemStatus.WARNING);
				item
						.setDescription("Warnings:\n"
								+ "\tThe component is removed from the repository despite it was not deployed."
								+ "\n\tReason:" + cnfe.getLocalizedMessage());
			} finally {
				Accounting.endMeasure(tagName);
			}
			long delay = beginTime - System.currentTimeMillis();
			if (location.bePath()) {
				tracePath(location, 
						"Deploy Service's removeLibrary operation has finished. Time: [{0}] ms.",
						new Object[] { new Long(delay) });
			}
		} catch (WarningException we) {
			item.setUndeployItemStatus(UndeployItemStatus.WARNING);
			item.setDescription("Warnings:\n"
					+ this.concatStrings(we.getWarnings()));
		} catch (RemoteException rex) {
			if (rex.getMessage() != null) {
				item.setDescription("Error: \n" + rex.getMessage());
			}

			throw new DeliveryException(DCExceptionConstants.REMOVE,
					new String[] { item.toString() }, rex);
		} catch (Exception ex) {
			if (ex.getMessage() != null) {
				item.setDescription("Error: \n" + ex.getMessage());
			}

			throw new DeliveryException(DCExceptionConstants.REMOVE,
					new String[] { item.toString() }, ex);
		}
	}

}
