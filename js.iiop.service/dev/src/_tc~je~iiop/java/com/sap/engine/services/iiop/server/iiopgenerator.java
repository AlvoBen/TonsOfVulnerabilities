/**
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.iiop.server;

import com.sap.engine.interfaces.cross.ProviderContainer;
import com.sap.engine.services.iiop.logging.LoggerConfigurator;
import com.sap.engine.services.iiop.server.generator.StubTieGenerator;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Public class IIOPGenerator is aimed to start the genarator
 * of Stubs and Ties and is a part of the implemenattion of the
 * ProtocolProvider interface for IIOP remote protocol
 *
 * @author Ralitsa Bozhkova
 * @version 4.0
 */
public class IIOPGenerator {

  /**
   * Constructor for the IIOPGenerator
   *
   */
  public IIOPGenerator() {
  }

  /**
   * This method generates all the necessary files, i.e.
   * Tie and Stub files an redistributes them according
   * to whether they are aimed to be client side or server side
   *
   * @param   objects    - objects for which Ties and Stubs are generated
   * @param   interfaces - interfaces for which Stubs are generated
   * @param   workDir    - working directory
   * @return  new ProviderContainer with all the information necessary
   */
  public ProviderContainer generateSupport(Class[] objects, Class[] interfaces, Hashtable access, String workDir) {
    Vector vAll = new Vector();
    Vector temp = null;
    Vector client = new Vector();

    for (int i = 0; i < objects.length; i++) {
      try {
        StubTieGenerator generator = new StubTieGenerator(objects[i], workDir, access, false);
        temp = generator.generate();
        int size = temp.size();

        for (int j = 0; j < size; j++) {
          String tmp = (String) temp.elementAt(j);
          String generated = tmp.substring(workDir.length() + 1, tmp.lastIndexOf('.'));
          vAll.addElement(generated);

          // Add only stub files
          if (generated.endsWith("_Stub")) {
            client.addElement(generated);
          }
        }
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beDebug()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).debugT("IIOPGenerator.generateSupport(Class[], Class[], Hashtable, String, boolean)", "Generate support finishted successfully");
        }
      } catch (Exception ex) {
        if (LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).beError()) {
          LoggerConfigurator.getLocation(LoggerConfigurator.DEST_GENERAL).errorT("IIOPGenerator.generateSupport(Class[], Class[], Hashtable, String, boolean)", "Error when generating support" + LoggerConfigurator.exceptionTrace(ex));
        }
      }
    }

    String[] serverSupport = new String[vAll.size()];
    vAll.copyInto(serverSupport);
    String[] clientSupport = new String[client.size()];
    client.copyInto(clientSupport);
    return new ProviderContainer(serverSupport, clientSupport, workDir);
  }

}

