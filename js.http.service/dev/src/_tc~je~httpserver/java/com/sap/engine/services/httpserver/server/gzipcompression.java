/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.httpserver.server;

import java.util.zip.*;
import java.io.*;

import com.sap.engine.lib.util.HashMapObjectObject;
import com.sap.engine.services.httpserver.exceptions.HttpIOException;
import com.sap.engine.services.httpserver.interfaces.properties.HttpCompressedProperties;
import com.sap.engine.services.httpserver.lib.ParseUtils;
import com.sap.engine.services.httpserver.lib.headers.MimeHeaders;
import com.sap.engine.services.httpserver.lib.protocol.HeaderNames;
import com.sap.engine.services.httpserver.lib.protocol.HeaderValues;

public class GzipCompression {

  private String workDir = null;
  private String zipFileName = null;
  private HttpCompressedProperties compressedProperties = null;

  public GzipCompression(String workDir, HttpCompressedProperties props) {
    this.workDir = workDir;
    this.compressedProperties = props;
  }

  public void init() {
    zipFileName = null;
  }

  public String getFileName() {
    return zipFileName;
  }

  public RandomAccessFile compressFile(String host, String fileName, File sourceFile, MimeHeaders headers, HashMapObjectObject translationTable) throws IOException {
    String infoFileName = null;
    String realZipFileName = (workDir + File.separator + host + File.separator + fileName).replace(ParseUtils.separatorChar, File.separatorChar);
    if (realZipFileName.endsWith(File.separator)) {
      realZipFileName = realZipFileName + sourceFile.getName();
    }
    realZipFileName += ".gzip";
    zipFileName = (String)translationTable.get(realZipFileName);
    if (zipFileName == null) {
      zipFileName = fileName;
      if (zipFileName.endsWith(File.separator)) {
        zipFileName = zipFileName + sourceFile.getName();
      }
      zipFileName = zipFileName.hashCode() + "";// + ".gzip";
      zipFileName = (workDir + File.separator + host + File.separator + zipFileName).replace(ParseUtils.separatorChar, File.separatorChar);
      zipFileName = zipFileName + ".gzip";
    }
    infoFileName = zipFileName + ".txt";

    synchronized (this) {
      File compressedFile = new File(zipFileName);
      File infoFile = new File(infoFileName);

      boolean gzipIsUpToDate = compressedFile.exists()
          && sourceFile.lastModified() == compressedFile.lastModified()
          && infoFile.exists();

      if (gzipIsUpToDate) {
        // Check the info file
        BufferedReader br = new BufferedReader(new FileReader(infoFile));
        String info = br.readLine();
        br.close();
        gzipIsUpToDate = realZipFileName.equals(info);
      }

      if (!gzipIsUpToDate) {
        // does not exist or has been modified => will have to re-compress it
        compress(sourceFile, zipFileName);
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(infoFileName);
          fos.write(realZipFileName.getBytes());
          fos.flush();
          translationTable.put(realZipFileName, zipFileName);
        } catch (IOException ioe) {
          Log.logWarning("ASJ.http.000072", 
            "Cannot write info file [{0}] for compressed resource [{1}].", 
            new Object[]{infoFileName, realZipFileName}, ioe, null, null, null);
        } finally {
          if (fos != null) {
            try {
              fos.close();
            } catch (IOException e) {
              Log.logWarning("ASJ.http.000073", 
                "Cannot close info file [{0}] for compressed resource [{1}].", 
                new Object[]{infoFileName, realZipFileName}, e, null, null, null);
            }
          }
        }
      }
    }

    RandomAccessFile r;
    try {
      r = new RandomAccessFile(zipFileName, "r");
      headers.putIntHeader(HeaderNames.entity_header_content_length_, (int) r.length());
      headers.addHeader(HeaderNames.entity_header_content_encoding_, HeaderValues.gzip_);
    } catch (IOException ex) {
      throw new HttpIOException(HttpIOException.CANNOT_READ_COMPRESSED_FILE, new Object[]{zipFileName}, ex);
    }

    return r;
  }
 
  // ------------------------ PRIVATE ------------------------

  private void compress(File f, String newFileName) throws IOException {
    String fileName = f.getAbsolutePath().replace('/', ParseUtils.separatorChar).replace('\\', ParseUtils.separatorChar);
    FileInputStream fInStream = new FileInputStream(fileName);
    try {
      (new File(newFileName.substring(0, newFileName.lastIndexOf(File.separator)))).mkdirs();
      FilterOutputStream gzipOutStream = null;
      try {
        gzipOutStream = getZipStream(newFileName);
        byte[] buffer = new byte[1024];
        int readed = fInStream.read(buffer, 0, buffer.length);
        while (readed != -1) {
          gzipOutStream.write(buffer, 0, readed);
          readed = fInStream.read(buffer, 0, buffer.length);
        }
        gzipOutStream.flush();
      } finally {
        if (gzipOutStream != null) {
          try {
            gzipOutStream.close();
          } catch (IOException io) {
            Log.logError("ASJ.http.000165", "Cannot close file [{0}].", new Object[]{newFileName}, io, null, null, null);
          }
        }
      }
    } finally {
      try {
        fInStream.close();
      } catch (IOException io) {
        Log.logError("ASJ.http.000166", "Cannot close file [{0}].", new Object[]{fileName}, io, null, null, null);
      }
    }
    // For every zipped file will set lastModified() time of File f.
    new File(newFileName).setLastModified(f.lastModified());
  }

  private FilterOutputStream getZipStream(String newFileName) throws IOException {
    FilterOutputStream gzipstream = null;
    FileOutputStream outputStream = new FileOutputStream(newFileName, false);
    if (compressedProperties.getGZipImpConstructor() == null) {
      gzipstream = new GZIPOutputStream(outputStream);
    } else {
      try {
        gzipstream = (FilterOutputStream) compressedProperties.getGZipImpConstructor().newInstance(new Object[]{outputStream});
      } catch (Exception e) {
        Log.logWarning("ASJ.http.000074", 
          "Cannot instantiate GZIP implementation class [{0}]. Will use default one from JDK.", 
          new Object[]{compressedProperties.getGZipImplementation()}, e, null, null, null);
        gzipstream = new GZIPOutputStream(outputStream);
      }
    }
    return gzipstream;
  }

}
