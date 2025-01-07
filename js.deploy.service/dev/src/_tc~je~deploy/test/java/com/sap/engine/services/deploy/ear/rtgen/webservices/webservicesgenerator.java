/*
 * Created on 2005-3-23 by Luchesar Cekov
 */
package com.sap.engine.services.deploy.ear.rtgen.webservices;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sap.engine.lib.io.FileUtils;
import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.Generator;
import com.sap.engine.services.deploy.ear.J2EEModule;
import com.sap.engine.services.deploy.ear.modules.Web;
import com.sap.lib.javalang.tool.ReadResult;

/**
 * @author Luchesar Cekov
 */
public class WebServicesGenerator implements Generator {
  private static final long serialVersionUID = -4311759946619547710L;
  private final String[] DEFAULT_WS_ENTRIES = new String[] { "META-INF/ws-deployment-descriptor.xml",
                                                            "meta-inf/ws-deployment-descriptor.xml" };

  public J2EEModule[] generate(File tempDir, String moduleRelativeFileUri) throws GenerationException {
    ArrayList result = new ArrayList(1);
    try {
      ZipFile file = new ZipFile(tempDir + File.separator + moduleRelativeFileUri);
      try {
        for (int i = 0; i < DEFAULT_WS_ENTRIES.length; i++) {
          ZipEntry entry = file.getEntry(DEFAULT_WS_ENTRIES[i]);
          if (entry != null) {
            createSingleModule(tempDir, file.getInputStream(entry), result);
          }
        }
      } finally {
        file.close();
      }
    } catch (GenerationException rtge) {
      throw rtge;
    } catch (Exception e) {
      throw new GenerationException(e.getMessage(), e);
    }

    return (J2EEModule[]) result.toArray(new J2EEModule[] {});

  }

  private void createSingleModule(File tempDir, InputStream entry, ArrayList result) throws GenerationException {
    String tempCreatorDir = new File(tempDir, "webService" + System.currentTimeMillis()).getAbsolutePath();
    try {
      WebInfoCreatorImpl creator = new WebInfoCreatorImpl();
      WebInfo[] webInfo = creator.createSingleWebInfo(tempCreatorDir, entry);
      File warFile = null;
      if (webInfo != null) {
        for (int i = 0; i < webInfo.length; i++) {
          warFile = new File(webInfo[i].getWarModulePath());
          FileUtils.copyFile(warFile, new File(tempDir, warFile.getName()));

          J2EEModule webModule = new Web(tempDir, new File(webInfo[i].getWarModulePath()).getName(), webInfo[i]
                              .getContextRoot());
          result.add(webModule);
        }
      }
    } catch (Exception e) {
      throw new GenerationException(e.getMessage(), e);
    } finally {
      FileUtils.deleteDirectory(new File(tempCreatorDir));
    }
  }

  public boolean supportsFile(String moduleRelativeFileUri) {
    return moduleRelativeFileUri.endsWith(".wsar") || moduleRelativeFileUri.endsWith(".jar");
  }

  public boolean removeModule(String moduleRelativeFileUri) {
    return false;
  }
}