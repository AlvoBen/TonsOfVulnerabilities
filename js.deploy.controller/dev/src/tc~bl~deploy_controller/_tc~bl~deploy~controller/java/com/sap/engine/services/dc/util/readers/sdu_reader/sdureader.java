package com.sap.engine.services.dc.util.readers.sdu_reader;

import java.io.File;
import java.io.InputStream;

import com.sap.engine.services.dc.cm.ErrorStrategy;
import com.sap.engine.services.dc.repo.SduLocation;

/**
 * 
 * Title: Software Deployment Manager Description:
 * 
 * Copyright: Copyright (c) 2003 Company: SAP AG Date: 2004-3-25
 * 
 * @author dimitar
 * @version 1.0
 * @since 6.40
 * 
 */
public interface SduReader {

	public SduLocation read(String archiveFilePathName)
			throws SduReaderException;

	public SduLocation read(String archiveFilePathName,
			ErrorStrategy errorStrategy) throws SduReaderException;

	public void setTempExtractingDir(File tempExtractingDir)
			throws SduReaderException;

	public File getTempExtractingDir();

	public InputStream getSapManifestInputStream(String archiveFilePathName)
			throws SduReaderException;

}
