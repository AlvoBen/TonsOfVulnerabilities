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

import java.io.*;
import java.util.Date;
import java.util.Iterator;

import com.sap.tc.logging.*;

import com.sap.engine.frame.ServiceContext;
import com.sap.engine.frame.core.thread.ThreadSystem;

/**
 * @author Nikola Marchev
 * @version 6.30
 * @deprecated
 */

public class OverwriteLogfileListenerImpl implements OverwriteLogfileListener {

  private static final Location CLASS_LOCATION = Location.getLocation(OverwriteLogfileListenerImpl.class);

  private static final String PROPERTY_NAME_ENABLED = "ArchiveOldLogFiles";
  private static final String PROPERTY_DIR_NAME     = "ArchivesDirectory";

  private String dirName;
  private boolean enabled;
  private ServiceContext sc;

  public OverwriteLogfileListenerImpl( ServiceContext sc ) {
	this.sc = sc;
	dirName = sc.getServiceState().getProperty( PROPERTY_DIR_NAME, "./log/archive" );
	new File( dirName ).mkdirs();
	if ( !dirName.endsWith( "/" ) && !dirName.endsWith( "\\" ) ) {
	  dirName += '/';
	}
	String enabledStr = sc.getServiceState().getProperty( PROPERTY_NAME_ENABLED, "ON" );
	enabled = enabledStr.equalsIgnoreCase( "ON" ) || enabledStr.equalsIgnoreCase( "YES" ) || enabledStr.equalsIgnoreCase( "TRUE" );
  }

  public void setEnabled( boolean enabled ) {
	this.enabled = enabled;
  }

  public void handleEvent( OverwriteLogfileEvent evt ) {
	if ( !enabled ) {
      // $JL-SEVERITY_TEST$
	  CLASS_LOCATION.logT( Severity.WARNING, "The information from file log '" + evt.getFileName() + "' will be lost - archiving old file logs is disabled." );
	  return;
	}

	String[] fileNames = new String[ evt.getFileCnt() ];
	Iterator fileNamesIterator = evt.getFileLog().calculateFileNames().iterator();
	String time = "." + new Date( System.currentTimeMillis() ).toString().replace( ':', '.' ).replace( ' ', '_' );
	long[] fileTimes = new long[ evt.getFileCnt() ];
	for ( int i = 0; i < fileNames.length; i ++ ) {
	  fileNames[ i ] = (String) fileNamesIterator.next();
	  File currentFile = new File( fileNames[ i ] );
	  fileTimes[ i ] = currentFile.lastModified();
	  fileNames[ i ] = patternToFileName( fileNames[ i ] );
	  String newLogFileName = fileNames[ i ] + '.' + fileTimes[ i ];
	  fileNames[ i ] = currentFile.renameTo( new File( newLogFileName ) ) ? newLogFileName : null;
	}
	ArchiveThread at = new ArchiveThread( fileNames, dirName + evt.getFileLog().getPattern().replace( ':', '_' ).replace( '/', '_' ) + time + ".zip", fileTimes );
	ThreadSystem threadSystem = sc.getCoreContext().getThreadSystem();
	threadSystem.startThread( at, true );
  }

  private String patternToFileName( String pattern ) {
	return pattern.replace( ':', '_' ).replace( '/', '_' ).replace( '\\', '_' );
  }

  public String[] getAllArchives( String pattern ) {
	String[] result = new File( dirName ).list( new ArchivesFilter( patternToFileName( pattern ) ) );
	for ( int i = 0; i < result.length; i ++ ) {
	  result[ i ] = dirName + result[ i ];
	}
	return result;
  }

  class ArchivesFilter implements FilenameFilter {
	String pattern;

	public ArchivesFilter( String pattern ) {
	  this.pattern = pattern;
	}

	public boolean accept( File dir, String name ) {
	  return name.indexOf( pattern ) != -1 && name.endsWith( ".zip" );
	}
  }

}