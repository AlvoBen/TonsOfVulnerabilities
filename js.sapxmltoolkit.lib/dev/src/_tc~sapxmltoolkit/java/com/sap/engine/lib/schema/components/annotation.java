package com.sap.engine.lib.schema.components;

import org.w3c.dom.*;
import java.util.Vector;

/**
 * @author  Nick Nickolov, nick_nickolov@abv.bg
 * @version 14-Mar-02, 14:28:24
 */
public interface Annotation extends Base {

  Vector getAttributes();

  Vector getAppInformations();

  Vector getUserInformations();
  
  void getAttributes(Vector collector);

  void getAppInformations(Vector collector);

  void getUserInformations(Vector collector);

  Node[] getAttributesArray();

  Node[] getAppInformationsArray();

  Node[] getUserInformationsArray();
}

