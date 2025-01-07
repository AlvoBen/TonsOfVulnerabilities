/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http:////www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf.. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.deploy.ear.jar.initcontainer;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sap.engine.lib.converter.CombinedEntityResolver;
import com.sap.engine.services.deploy.container.rtgen.AnnotationsSupportingModuleDetector;
import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.modules.EJB;
import com.sap.lib.javalang.file.FileInfo;
import com.sap.lib.javalang.file.FolderInfo;
import com.sap.lib.javalang.tool.Filter;
import com.sap.lib.javalang.tool.ReadResult;

/**
 * @author Luchesar Cekov
 */
public class EjbModuleDetector extends AnnotationsSupportingModuleDetector {
  private static final String[] EJBJARXML = new String[] { "META-INF/ejb-jar.xml", "meta-inf/ejb-jar.xml" };

  /**
   * Checks whether the given archive contains any Enterprise Java Beans. If so -
   * generates the corresponding J2EEModule descriptor
   * 
   * @param tempDir
   *          the temporary folder where the .ear file is extracted
   * @param moduleRelativeFileUri
   *          the name of the module file
   */
  public Module detectModule(File tempDir, String moduleRelativeFileUri, ReadResult annotationsMetadata)
                      throws GenerationException {

    try {

      // check for ejb-jar.xml
      JarFile jar = new JarFile(tempDir + File.separator + moduleRelativeFileUri);
      for (String ejbjar : EJBJARXML) {
        ZipEntry entry = jar.getEntry(ejbjar);
        if (entry != null) {
          return new EJB(tempDir, moduleRelativeFileUri);
        }
      }

      // check for @Stateful, @Stateless, or @MessageDriven annotations
      // ClassInfoReader classInfoReader = new ReaderFactory().getReader();
      // ReadResult annotationsMetadata = classInfoReader.read(new File[] { new
      // File(tempDir, moduleRelativeFileUri) });

      FileInfo moduleFile = getModuleAnnotations(annotationsMetadata, tempDir, moduleRelativeFileUri);
      if (moduleFile == null)
        return null;

      if (moduleFile.getClassLevelAnnotations(AnnotationFilters.STATEFUL).values().size() > 0
          || moduleFile.getClassLevelAnnotations(AnnotationFilters.STATELESS).values().size() > 0
          || moduleFile.getClassLevelAnnotations(AnnotationFilters.MESSAGE_DRIVEN).values().size() > 0) {

        // workaround for CTS 5 because the application client jar also contains
        // classes with the same annotations
        String mainClass = null;
        try {
          mainClass = jar.getManifest().getMainAttributes().getValue("Main-Class");
        } catch (NullPointerException exc) {//$JL-EXC$
          // nothing to do, probably no manifest
        }

        if (mainClass == null) { // no app client
          return new EJB(tempDir, moduleRelativeFileUri);
        }
      }

      return null;
    } catch (IOException ioexc) {
      throw new GenerationException("Cannot get " + moduleRelativeFileUri + " module.", ioexc);
    }
  }

  /**
   * @deprecated use filters for module files within EAR file when they are
   *             implemented
   * @param tempDir
   * @param moduleRelativeFileUri
   * @param files
   * @return
   */
  private FileInfo getModuleAnnotations(ReadResult annotationsMetadata, File tempDir, String moduleRelativeFileUri) {
    FileInfo[] files = ((FolderInfo) annotationsMetadata.getProcessedFiles()[0]).getFiles();
    FileInfo moduleFile = null;
    String moduleFullPath = new File(tempDir, moduleRelativeFileUri).getAbsolutePath();
    for (FileInfo fileInfo : files) {
      if (moduleFullPath.equals(fileInfo.getFullPath())) {
        moduleFile = fileInfo;
        break;
      }
    }
    return moduleFile;
  }

  private static class AnnotationFilters {
    public static final Filter STATELESS = new Filter();
    public static final Filter STATEFUL = new Filter();
    public static final Filter APPLICATION_EXCEPTION = new Filter();
    public static final Filter MESSAGE_DRIVEN = new Filter();

    static {
      STATELESS.setAnnFilteringType(Filter.FilterType.POSITIVE);
      STATELESS.setAnnotationTypes(new String[] { "javax.ejb.Stateless" });
      STATEFUL.setAnnFilteringType(Filter.FilterType.POSITIVE);
      STATEFUL.setAnnotationTypes(new String[] { "javax.ejb.Stateful" });
      APPLICATION_EXCEPTION.setAnnFilteringType(Filter.FilterType.POSITIVE);
      APPLICATION_EXCEPTION.setAnnotationTypes(new String[] { "javax.ejb.ApplicationException" });
      MESSAGE_DRIVEN.setAnnFilteringType(Filter.FilterType.POSITIVE);
      MESSAGE_DRIVEN.setAnnotationTypes(new String[] { "javax.ejb.MessageDriven" });
    }
  }

  private static class SchemaResolver extends CombinedEntityResolver {

    private final HashMap schemas;

    public SchemaResolver() {
      this.schemas = new HashMap();
      this.schemas.put("ejb-j2ee-engine_3_0.xsd",
                          "com/sap/engine/services/ejb3/descriptors/schemas/ejb-j2ee-engine_3_0.xsd");
      this.schemas.put("ejb-jar_3_0.xsd", "com/sap/engine/services/ejb3/descriptors/schemas/ejb-jar_3_0.xsd");
      this.schemas.put("javaee_5.xsd", "com/sap/engine/services/ejb3/descriptors/schemas/javaee_5.xsd");
      this.schemas.put("javaee_web_services_1_2.xsd",
                          "com/sap/engine/services/ejb3/descriptors/schemas/javaee_web_services_1_2.xsd");
      this.schemas.put("javaee_web_services_client_1_2.xsd",
                          "com/sap/engine/services/ejb3/descriptors/schemas/javaee_web_services_client_1_2.xsd");
      this.schemas.put("xml.xsd", "com/sap/engine/services/ejb3/descriptors/schemas/xml.xsd");

    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
      if (this.schemas.containsKey(systemId)) {
        InputSource inputSource = new InputSource();
        inputSource.setByteStream(getClass().getClassLoader().getResourceAsStream((String) schemas.get(systemId)));
        inputSource.setSystemId(systemId);
        return (inputSource);
      } else {
        return super.resolveEntity(publicId, systemId);
      }
    }
  }

}
