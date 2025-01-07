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
/**
 *@author Luchesar Cekov
 */
package com.sap.engine.services.deploy.ear.jar.initcontainer;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import com.sap.engine.services.deploy.container.rtgen.GenerationException;
import com.sap.engine.services.deploy.container.rtgen.ModuleDetector;
import com.sap.engine.services.deploy.ear.Module;
import com.sap.engine.services.deploy.ear.modules.Java;
import com.sap.lib.javalang.tool.ReadResult;

/**
 * This class is used to identify Application client modules in an 
 * application which does not contain application.xml deployment descriptor.
 *
 * @author i031222
 * @version 7.20
 */

public class AppClientModuleDetector implements ModuleDetector {
    private static final long serialVersionUID = 5325409662941721974L;

    /**
     * Checks whether the given archive contains any Application clients. 
     * If so - generates the corresponding J2EEModule descriptor
     * 
     * @param tempDir the temporary folder where the .ear file is extracted
     * @param moduleRelativeFileUri the name of the module file
     */
    public Module detectModule(File tempDir, String moduleRelativeFileUri) throws GenerationException {
      if (!moduleRelativeFileUri.endsWith(".jar")) return null;
        try {
            // check for Main-Class attribute in MANIFEST.MF
            JarFile jar = new JarFile(tempDir + File.separator + moduleRelativeFileUri);
              
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                String mainClassName = manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS);
                if (mainClassName != null) {
                    return  new Java(tempDir, moduleRelativeFileUri);
                }
            }
        } catch (IOException ioexc) {
            throw new GenerationException("Cannot get " + moduleRelativeFileUri + " module.", ioexc);
        } catch (IllegalStateException isx) {
            throw new GenerationException("A problem occurred. Maybe the .jar file is closed.", isx); 
        }
        return null;
    }
}
