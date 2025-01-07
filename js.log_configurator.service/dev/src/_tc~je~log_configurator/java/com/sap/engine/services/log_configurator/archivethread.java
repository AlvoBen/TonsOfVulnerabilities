package com.sap.engine.services.log_configurator;

/**
 * Title:        Logging
 * Description:  Logging API
 * Copyright:    Copyright (c) 2002
 * Company:      SAP Labs Bulgaria LTD., Sofia, Bulgaria.
 * Url:          Http://www.saplabs.bg
 *               All rights reserved.
 *
 *               This software is the confidential and proprietary information
 *               of SAP AG International ("Confidential Information").
 *               You shall not disclose such  Confidential Information
 *               and shall use it only in accordance with the terms of
 *               the license agreement you entered into with SAP AG.
 */

import com.sap.tc.logging.Severity;
import com.sap.tc.logging.Location;

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.io.*;

/**
 * @author Nikola Marchev
 * @version 6.30
 * @deprecated
 */

public class ArchiveThread implements Runnable {

  private static final int BUFFER_LENGTH = 10000;
  private static final Location CLASS_LOCATION = Location.getLocation(OverwriteLogfileListenerImpl.class);

  private long[] fileTimes;

  private String[] fileNames;
  private String zipFileName;

  public ArchiveThread( String[] fileNames, String zipFileName, long[] fileTimes ) {
	super();
	this.fileNames = fileNames;
	this.zipFileName = zipFileName;
	this.fileTimes = fileTimes;
  }

  public void run() {
	try {
	  Thread.currentThread().setPriority( Thread.NORM_PRIORITY - 1 );
	  byte[] buffer = new byte[ BUFFER_LENGTH ];
	  ZipOutputStream zout = null;
	  ZipEntry zipEntry = null;

	  try {
		zout = new ZipOutputStream( new FileOutputStream( zipFileName ) );
	  } catch (FileNotFoundException e) {
        // $JL-EXC$
		CLASS_LOCATION.logT( Severity.ERROR, "Can't create '" + zipFileName + "': " + e.getMessage() );
		deleteFiles();
		return;
	  }

	  for ( int i = 0; i < fileNames.length; i ++ ) {
		if ( fileNames[ i ] != null ) {
		  try {
			CLASS_LOCATION.logT( Severity.INFO, "Adding '" + fileNames[ i ] + "' to '" + zipFileName + "'." );
			int lastIndex = fileNames[ i ].lastIndexOf( '/' );

			if ( lastIndex > 0 ) {
			  zipEntry = new ZipEntry( fileNames[ i ].substring( lastIndex + 1 ) );
			} else {
			  zipEntry = new ZipEntry( fileNames[ i ] );
			}

			zipEntry.setTime(fileTimes[i]);
			zout.putNextEntry(zipEntry);

			FileInputStream in = new FileInputStream( fileNames[ i ] );
			while ( in.available() != 0 ) {
			  int readed = in.read( buffer );
			  if ( readed > 0 ) {
				zout.write( buffer, 0, readed );
			  }
			}

			in.close();
		  } catch ( IOException e ) {
			// $JL-EXC$
			CLASS_LOCATION.logT( Severity.ERROR, "I/O error occurs when adding'" + fileNames[ i ] + "':" + e.getMessage() );
		  } finally {
			if ( !new File( fileNames[ i ] ).delete() ) {
              // $JL-SEVERITY_TEST$			  
			  CLASS_LOCATION.logT( Severity.WARNING, "Can't delete '" + fileNames[ i ] + "'." );
			}
		  }
		}
	  }

	  try {
		zout.close();
	  } catch ( IOException e ) {
		// $JL-EXC$	$JL-SEVERITY_TEST$	
		CLASS_LOCATION.logT( Severity.WARNING, "Can't close zipfile '" + zipFileName + "'." );
	  }
	} finally {
	  Thread.currentThread().setPriority( Thread.NORM_PRIORITY );
	}
  }

  private void deleteFiles() {
	for ( int i = 0; i < fileNames.length; i ++ ) {
	  if ( fileNames[ i ] != null && !new File( fileNames[ i ] ).delete() ) {
		// $JL-SEVERITY_TEST$
		CLASS_LOCATION.logT( Severity.WARNING, "Can't delete '" + fileNames[ i ] + "'." );
	  }
	}
  }

}
