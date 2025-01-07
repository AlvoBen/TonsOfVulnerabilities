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
package com.sap.engine.services.deploy;

import com.sap.engine.services.deploy.container.ContainerInfo;


/**
 *@author Luchesar Cekov
 */
public class Test {
 void p() {
   ContainerInfo containerInfo = new ContainerInfo() {
     public boolean isSoftwareTypeSupported(String softType, String softSubType) {
       return "MyPreferedSoftwaresubtype".equals(softSubType);
     }
   };
   containerInfo.setSoftwareTypes(new String[] {"FS"});
   containerInfo.setFileNames(new String[]{"configuration/config.xml", "META-INF/MANIFEST.MF", "a/b/c/d.e", "anymodule.mod"});
   
   containerInfo.setFileExtensions(new String[] {".mp3", ".sda", ".jar", ".war"});
   
 }
}
