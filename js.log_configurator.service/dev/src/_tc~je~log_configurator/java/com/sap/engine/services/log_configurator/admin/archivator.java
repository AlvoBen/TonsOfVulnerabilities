package com.sap.engine.services.log_configurator.admin;

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

import com.sap.engine.lib.logging.descriptors.LogDestinationDescriptor;
import com.sap.tc.logging.Location;
import com.sap.tc.logging.Severity;
import com.sap.tc.logging.FileLog;
import com.sap.tc.logging.Log;

import java.util.zip.ZipOutputStream;
import java.util.zip.ZipEntry;
import java.util.Date;
import java.util.List;
import java.io.*;

/**
 * @author Nikola Marchev
 * @version 6.30
 */
public class Archivator {//$JL-EXC$

  private static final Location TRACER = Location.getLocation(Archivator.class);
  public static final String DEFAULT_ARCHIVE_DIR = "./log/archive/";

  private boolean dumps;
  private PrintStream out;
  private PrintStream err;
  private StringBuffer output;
  private LogConfigurator logConfigurator;


  public Archivator(LogConfigurator logConfigurator) {
	this.logConfigurator = logConfigurator;
  }


  public String archive( LogDestinationDescriptor[] logDestinationDescriptors, boolean defaultTracesSelected, String archiveDir, boolean dumps, PrintStream out, PrintStream err ) {
	if ( dumps && err != null && out != null ) {
	  this.dumps = true;
	  this.err = err;
	  this.out = out;
	} else {
	  this.dumps = false;
	}
	output = new StringBuffer();
    
	archiveDir = archiveDir.replace('\\', '/');
	if ( !archiveDir.endsWith("/") ) {
	  archiveDir += "/";
	}
    
	ZipOutputStream zout = null;
	File dir = new File( archiveDir );
	if ( !dir.exists() ) {
	  if ( !dir.mkdirs() ) {
		printError( "Can't create archive directory!" );
		return output.toString();
	  }
	}
    
	String name = archiveDir + "LogArchive_" + new Date( System.currentTimeMillis() ).toString().replace( ':', '.' ).replace( ' ', '_' ) + ".zip";
	try {
	  zout = new ZipOutputStream( new FileOutputStream( name ) );
	} catch ( FileNotFoundException e ) {
      // $JL-EXC$ 		
	  printError( "Can't create archive file!" );
	  return output.toString();
	}
	addMessage( "Creating archive '" + name + "':\r\n" );
    
	try {
	  if ( defaultTracesSelected ) {
		addMessage( "Adding default traces\r\n" );
		FileLog defaultTraceFile = logConfigurator.getDefaultTraceFile();
		if (defaultTraceFile != null) {
		  List fileNames = defaultTraceFile.calculateFileNames();
		  for (int i = fileNames.size(); --i >= 0; ) {
			archive((String) fileNames.get(i), zout, true);
		  }
		}
	  }
      
	  for ( int i = logDestinationDescriptors.length; --i >= 0; ) {
		Log dst = logDestinationDescriptors[ i ] == null ? null : logDestinationDescriptors[ i ].getRealLog();
		if ( dst instanceof FileLog ) {
		  List fileNames = ((FileLog) dst).calculateFileNames();
		  for (int j = fileNames.size(); --j >= 0; ) {
			archive((String) fileNames.get(j), zout, true);
		  }
		}
	  }
      
	  return output.toString();
	} catch ( IOException e ) {
	  // $JL-EXC$		
	  printError( "Can't write to zip file!" );
	  return output.toString();
	} finally {
	  try {
		zout.close();
	  } catch ( Exception e ) {
		TRACER.traceThrowableT(Severity.ERROR, "Failed to close archive file \"" + name + "\"!", e);
	  }
	}
  }

  private void archive( String fileName, ZipOutputStream zout, boolean dump ) throws IOException {
	try {
	  File f = new File( fileName );
	  if ( f.length() == 0 ) {
		return;
	  }
      
	  FileInputStream in = new FileInputStream( f );
	  if ( dump ) {
		addMessage( "Adding \"" + fileName + "\"\r\n" );
	  }
      
	  zout.putNextEntry( new ZipEntry( f.getPath() ) );
	  byte[] buffer = new byte[ 1024 ];
	  int bytesRead = 0;
      
	  while ( (bytesRead = in.read(buffer)) != -1 ) {
		zout.write(buffer, 0, bytesRead);
	  }
      
	  in.close();
	} catch ( Exception e ) {
	  TRACER.traceThrowableT(Severity.ERROR, "Failed to archive \"" + fileName + "\" file!", e);
	  printError("Failed to archive \"" + fileName + "\" file due to: " + e + "\n");
	}
  }

  public void addMessage( String message ) {
	if ( dumps ) {
	  out.print( message );
	} else {
	  output.append( message );
	}
  }

  public void printError( String error ) {
	if ( dumps ) {
	  err.println( "[Shell -> LOG_ARCHIVE] " + error );
	} else {
	  output.append( error );
	}
  }

}
