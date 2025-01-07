/*
 * Copyright (c) 2003 by SAP AG, Walldorf.,
 * <<http://www.sap.com>>
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.server.validate.jlin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sap.engine.frame.core.configuration.CustomParameterMappings;
import com.sap.engine.jlinee.lib.ApplicationComponentInfo;
import com.sap.engine.jlinee.lib.IExtractedFileLocator;
import com.sap.engine.services.deploy.container.DeploymentException;
import com.sap.engine.services.deploy.container.ValidatedModelsCache;
import com.sap.engine.services.deploy.container.util.CAConstants;
import com.sap.engine.services.deploy.container.util.CAConvertor;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.jar.DplArchiveReader;
import com.sap.engine.services.deploy.ear.jar.EARReader;
import com.sap.engine.services.deploy.ear.modules.extract.IExtractable;
import com.sap.engine.services.deploy.server.dpl_info.DeploymentInfo;
import com.sap.engine.services.deploy.server.utils.ValidateUtils;
import com.sap.lib.javalang.tool.ReadResult;

/**
 * 
 * 
 * @author Anton Georgiev
 * @version 7.1
 */
public class AppJLinInfo {

	private final DeploymentInfo dInfo;
	private final String operation;
	private final File earFile;
	private final Map<ApplicationComponentInfo, String> appCompInfos;
	private final Object clResourcesOrCLs[];// File && ClassLoader
	private final CustomParameterMappings customParameterMappings;
	private ReadResult annotations;
	private ValidatedModelsCacheImpl validatedModulesCache;
	private DplArchiveReader reader;
	private IExtractedFileLocator fileLocator;

	/**
	 * @param _dInfo. Not null.
	 * @param _operation. Not null.
	 * @param _earFile
	 * @param _cName_Files
	 * @param _fPath_fPaths. Not null.
	 * @param _fName_fUriInEar
	 * @param _customParameterMappings
	 * @param uriInEar2AltDD
	 * @param aReader
	 * @throws IOException
	 * @throws DeploymentException
	 */
	public AppJLinInfo(final DeploymentInfo _dInfo, final String _operation, 
		final File _earFile, final Hashtable<String, File[]> _cName_Files, 
		final Hashtable<String, List<String>> _fPath_fPaths,
		Hashtable<String, String> _fName_fUriInEar,
		CustomParameterMappings _customParameterMappings,
		Map<String, String> uriInEar2AltDD, 
		DplArchiveReader aReader) throws IOException, DeploymentException {
		
		assert _dInfo != null;
		assert _fPath_fPaths != null;
		ValidateUtils.nullValidator(_operation, "operation");
		
		dInfo = _dInfo;
		operation = _operation;
		validatedModulesCache = new ValidatedModelsCacheImpl();
		{
			appCompInfos = getApplicationComponentInfos(_cName_Files,
					_fPath_fPaths, _fName_fUriInEar, uriInEar2AltDD);

			{// validate
				ValidateUtils.nullValidator(appCompInfos,
						"ApplicationComponentInfo");
			}
		}

		ValidateUtils.nullValidator(_customParameterMappings,
				"CustomParameterMappings");
		customParameterMappings = _customParameterMappings;

		reader = aReader;
		earFile = _earFile;
		Set<File> folderResource = reader.getClassFinder().getFolderResources();
		Set<? extends File> zipResource = reader.getClassFinder()
				.getZipResources();
		Set<ClassLoader> parents = reader.getClassFinder().getParents();

		ArrayList<Object> all = new ArrayList<Object>(folderResource.size()
				+ zipResource.size() + parents.size() + 2);
		all.addAll(folderResource);
		all.addAll(zipResource);
		all.addAll(parents);
		if (reader instanceof EARReader) {
			all.add(earFile);
		}
		all.add(aReader.getTempDir());
		clResourcesOrCLs = new Object[all.size()];
		all.toArray(clResourcesOrCLs);

		annotations = reader.getDescriptor().getAnnotations();
		fileLocator = new ExtractedFileLocator(reader);
	}

	private ApplicationComponentInfo getApplicationComponentInfo(String cName,
		File files[], String fUriInEar, String altDD) {
		return new ApplicationComponentInfo(
			cName, files, fUriInEar, cName + "~" + fUriInEar, altDD);
	}

	private Map<ApplicationComponentInfo, String> 
		getApplicationComponentInfos(Hashtable<String, File[]> _cName_Files,
		Hashtable<String, List<String>> _fPath_fPaths, 
		Hashtable<String, String> _fName_fUriInEar,
		final Map<String, String> uriInEar2AltDDr) {
		
		assert _cName_Files != null;
		assert _fPath_fPaths != null;
		assert _fName_fUriInEar != null;

		final Map<ApplicationComponentInfo, String> res = 
			new HashMap<ApplicationComponentInfo, String>();

		String fUriInEar;
		File[] files;
		List<String> fPathsLocal;
		List<File> filesForAppInfo;
		ApplicationComponentInfo appInfo;
		
		for(final String cName : _cName_Files.keySet()) {
			final File[] filesGlobal = _cName_Files.get(cName);
			if (filesGlobal != null) {
				for (final File file : filesGlobal) {
					if (file == null) {
						continue;
					}
					fUriInEar = _fName_fUriInEar.get(file.getAbsolutePath());
					if (fUriInEar == null) {
						// for stand alone modules
						fUriInEar = file.getName();
					}
					fPathsLocal = _fPath_fPaths.get(file.getAbsolutePath());

					filesForAppInfo = new ArrayList<File>();
					filesForAppInfo.add(file);
					if (fPathsLocal != null) {
						for (int k = 0; k < fPathsLocal.size(); k++) {
							filesForAppInfo.add(new File(fPathsLocal.get(k)));
						}
					}
					files = new File[filesForAppInfo.size()];
					filesForAppInfo.toArray(files);
					appInfo = getApplicationComponentInfo(cName, files,
						fUriInEar, uriInEar2AltDDr.get(fUriInEar));
					res.put(appInfo, fUriInEar);
					validatedModulesCache.addValidatedModelObjectMap(cName,
						file.getAbsolutePath(), appInfo.getCache());
				}
			}
		}
		return res;
	}

	public Object[] getCLResourcesOrCLs() {
		return clResourcesOrCLs;
	}

	public String getAppName() {
		return dInfo.getApplicationName();
	}

	public File getEarFile() {
		return earFile;
	}

	public Map<ApplicationComponentInfo, String> 
		getApplicationComponentInfos() {
		return appCompInfos;
	}

	public CustomParameterMappings getCustomParameterMappings() {
		return customParameterMappings;
	}

	public String getOperation() {
		return operation;
	}

	public ReadResult getAnnotations() {
		return annotations;
	}

	public DplArchiveReader getReader() {
		return reader;
	}

	public IExtractedFileLocator getFileLocator() {
		return fileLocator;
	}

	public ValidatedModelsCache getValidatedModeslCache() {
		return validatedModulesCache;
	}

	@Override
	public String toString() {
		final String shift = "   ";
		final StringBuffer sb = new StringBuffer(CAConstants.EOL
				+ this.getClass().getName() + CAConstants.EOL);

		sb.append(shift + "AppName = " + getAppName() + CAConstants.EOL);
		sb.append(shift + "EarFile = " + getEarFile() + CAConstants.EOL);
		sb.append(shift + "ApplicationComponentInfos = " + "TODO"
			+ CAConstants.EOL);
		sb.append(shift + "ClassLoaderResources = "
			+ CAConvertor.toString(getCLResourcesOrCLs(), "")
			+ CAConstants.EOL);
		sb.append(shift + "Annotations = " + annotations + CAConstants.EOL);

		return sb.toString();
	}

	private static class ExtractedFileLocator implements IExtractedFileLocator {
		private Map<String, File[]> fileMappings;

		public ExtractedFileLocator(DplArchiveReader aReader)
				throws DeploymentException, IOException {
			fileMappings = new HashMap<String, File[]>();
			for (Module m : aReader.getDescriptor().getAllModules()) {
				if (m instanceof IExtractable) {
					fileMappings.put(m.getUri(),
							new File[] { ((IExtractable) m).extracted() });
				} else {
					if (fileMappings.containsKey(m.getUri())) {
						continue;
					}
					fileMappings.put(m.getUri(), new File[] { m
							.getAbsoluteFile() });
				}
			}
		}

		public Map<String, File[]> getEARContentMap() {
			return fileMappings;
		}
	}
}
