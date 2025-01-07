package com.sap.engine.services.dc.cm.deploy.impl;

import static com.sap.engine.services.dc.util.logging.DCLog.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatch;
import com.sap.engine.services.dc.cm.deploy.DeploymentBatchItem;
import com.sap.engine.services.dc.cm.deploy.DeploymentStatus;
import com.sap.engine.services.dc.cm.deploy.NotEnoughDiskSpaceException;
import com.sap.engine.services.dc.cm.deploy.SduLoadingException;
import com.sap.engine.services.dc.cm.deploy.impl.SduLocationDeplBatchItemMapper.MappingException;
import com.sap.engine.services.dc.manage.PathsConfigurer;
import com.sap.engine.services.dc.manage.ServiceConfigurer;
import com.sap.engine.services.dc.repo.Sdu;
import com.sap.engine.services.dc.repo.SduLocation;
import com.sap.engine.services.dc.util.FileUtils;
import com.sap.engine.services.dc.util.JvmUtils;
import com.sap.engine.services.dc.util.logging.DCLog;
import com.sap.engine.services.dc.util.readers.sdu_reader.SduReader;
import com.sap.engine.services.dc.util.readers.sdu_reader.SduReaderException;
import com.sap.engine.services.dc.util.readers.sdu_reader.SduReaderFactory;
import com.sap.tc.logging.Location;

/**
 * 
 * Title: J2EE Deployment Team Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-8-18
 * 
 * @author Dimitar Dimitrov
 * @version 1.0
 * @since 7.0
 * 
 */
final class SduLoader {
	private  final Location location = DCLog.getLocation(this.getClass());

	private final ErrorStrategy errorStrategy;

	SduLoader(ErrorStrategy errorStrategy) {
		this.errorStrategy = errorStrategy;
	}

	DeploymentBatch load(final String sessionId, final String[] archives,
			final boolean timeStatEnabled) throws SduLoadingException {

		if (location.bePath()) {
			tracePath(location, "Start loading archives ...");
		}

		final File sessionDir = new File(PathsConfigurer.getInstance()
				.getUploadDirName(sessionId));
		FileUtils.mkdirs(sessionDir.getAbsolutePath());

		try {
			doCheckIsAvailableDiskSpace(archives, sessionDir);
		} catch (final NotEnoughDiskSpaceException nedse) {
			throw new SduLoadingException("Please free some more disk space.",
					nedse);
		}

		final Collection<DeploymentBatchItem> deplItems = new ArrayList<DeploymentBatchItem>(
				archives.length);
		final SduReader sduReader = SduReaderFactory.getInstance()
				.createSduReader();

		try {
			sduReader.setTempExtractingDir(sessionDir);
		} catch (SduReaderException e) {
			SduLoadingException sle = new SduLoadingException(
					"[An error occurred while creating temp dir: "
							+ sessionDir.getAbsolutePath(), e);
			sle.setMessageID("ASJ.dpl_dc.003093");
			throw sle;
		}

		for (int i = 0; i < archives.length; i++) {

			SduLocation sduLocation = null;
			SduReaderException srException = null;
			try {
				sduLocation = sduReader.read(archives[i], this.errorStrategy);

				String typeName;
				Sdu sduItem = sduLocation.getSdu();
				SduGetType sduType = new SduGetType();
				sduItem.accept(sduType);
				typeName = sduType.getDescription();

				logInfo(location, "ASJ.dpl_dc.001107",
						"{0} [{1}] was loaded from location [{2}].",
						new Object[] { typeName,
								sduItem.getId().toString(), archives[i] });

			} catch (SduReaderException sre) {

				final String errMsg = DCLog
						.buildExceptionMessage(
								"ASJ.dpl_dc.001108",
								"A reading error occurred while loading the SDU [{0}].",
								new Object[] { archives[i] });
				logErrorThrowable(location,  null, errMsg, sre);

				if (this.errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
					throw new SduLoadingException(errMsg, sre);
				}
				srException = sre;
				sduLocation = (SduLocation) sre.getUserObject();
			}
			try {
				if (sduLocation != null) {
					DeploymentBatchItem dplBatchItem = SduLocationDeplBatchItemMapper
							.getInstance().map(sduLocation, timeStatEnabled);

					// set the old sdu
					dplBatchItem.getProperties().put("SESSIONID", sessionId);

					if (srException != null) {
						dplBatchItem
								.setDeploymentStatus(DeploymentStatus.ABORTED);
						dplBatchItem.addDescription(srException
								.getStackTraceString());
					}
					deplItems.add(dplBatchItem);
				}
			} catch (MappingException me) {
				final String errMsg = DCLog
						.buildExceptionMessage(
								"",
								"ASJ.dpl_dc.003096 A mapping error occurred while loading the SDU [{0}]. The read SDU could not be mapped to a deployment batch item.",
								new Object[] { archives[i] });
				logErrorThrowable(location, null, errMsg, me);

				if (this.errorStrategy.equals(ErrorStrategy.ON_ERROR_STOP)) {
					throw new SduLoadingException(errMsg, me);
				}
			}
		}

		if (location.bePath()) {
			tracePath(location, "[{0}] archive(s) loaded.",
				new Object[] { new Integer(deplItems.size()) });
		}
		
		return new DeploymentBatchImpl(deplItems);
	}


	private void doCheckIsAvailableDiskSpace(
			final String[] archiveFilePathNames, final File tempExtractDir)
			throws NotEnoughDiskSpaceException {
		if (!JvmUtils.isSapJvm()) {
			logInfo(location, 
					"ASJ.dpl_dc.001167",
					"Will not perform check for available disc space as it can be performed only on SAP JVM.");
			return;
		}

		// 100 * 1024 * 1024; // 100 Mb
		final long minSpaceToOperate = ServiceConfigurer.getInstance()
				.getMinFreeBytesToDeploy();
		final long usableSpace = FileUtils.getUsableSpace(tempExtractDir
				.getAbsolutePath());

		// check for minimal needed
		if (usableSpace < minSpaceToOperate) {
			throw new NotEnoughDiskSpaceException(
					"ASJ.dpl_dc.003472 Usable free disk space ["
							+ usableSpace
							+ "] bytes is less than minimal number of bytes to operate ["
							+ minSpaceToOperate + "]."
							+ " Please free some space.");
		}

		File fileFromArchives = null;
		long totalArhiveSize = 0;
		for (final String archiveFilePathName : archiveFilePathNames) {
			fileFromArchives = new File(archiveFilePathName);
			totalArhiveSize = totalArhiveSize + fileFromArchives.length();
		}

		// check for needed 2*(size of archives)
		final long minSpaceForProcessing = 2 * totalArhiveSize;
		if (usableSpace < minSpaceForProcessing) {
			throw new NotEnoughDiskSpaceException(
					"ASJ.dpl_dc.003473 Usable free disk space ["
							+ usableSpace
							+ "] bytes is not enough for deployment of archives with size ["
							+ totalArhiveSize
							+ "] bytes."
							+ " It should be at least ["
							+ minSpaceForProcessing
							+ "] bytes. Please free some space or shrink the batch.");
		}
	}
}
