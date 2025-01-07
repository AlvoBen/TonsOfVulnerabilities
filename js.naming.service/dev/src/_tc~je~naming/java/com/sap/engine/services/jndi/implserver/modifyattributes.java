/*
 * Copyright (c) 2000 by SAP AG, Walldorf.,
 * url: http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.engine.services.jndi.implserver;

import javax.naming.*;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.ModificationItem;

import com.sap.engine.services.jndi.JNDIFrame;
import com.sap.tc.logging.Location;

import javax.naming.directory.AttributeModificationException;

//import com.inqmy.frame.container.log.LogContext;
/**
 * Class for modifying attributes
 *
 * @author Petio Petev, Panayot Dobrikov
 * @version 4.00
 */
public class ModifyAttributes {

  private final static Location LOG_LOCATION = Location.getLocation(ModifyAttributes.class);

  /**
   * Modifies attributes
   *
   * @param attr Attributes to be modified
   * @param mod_op Code of operation to be done
   * @param newattr New attributes
   * @throws javax.naming.directory.AttributeModificationException Thrown if a problem when modifying attributes occures.
   * @throws NamingException Thrown if a problem occurs.
   */
  public static Attributes modAttr(Attributes attr, int mod_op, Attributes newattr) throws javax.naming.directory.AttributeModificationException, NamingException {
    switch (mod_op) {
      case DirContext.ADD_ATTRIBUTE: {
        if (newattr == null) {
          return attr;
        }

        if (attr == null) {
          attr = new BasicAttributes();
        }

        for (NamingEnumeration ne = newattr.getAll(); ne.hasMore();) {
          Attribute newattribute = (Attribute) ne.next();
          String attrID = newattribute.getID();
          Attribute oldattr = attr.get(attrID);

          //////////////Agaist page 12 bottom
          if (oldattr == null) {
            oldattr = new BasicAttribute(attrID);
            attr.put(oldattr);
          } //replace it with continue

          ////////////////
          for (NamingEnumeration vals = newattribute.getAll(); vals.hasMore();) {
            oldattr.add(vals.next());
          }
        }

        return attr;
      } // DirContext.ADD_ATTRIBUTE
      case DirContext.REPLACE_ATTRIBUTE: {
        if (newattr == null) {
          return attr;
        }

        if (attr == null) {
          return newattr;
        }

        for (NamingEnumeration ne = newattr.getAll(); ne.hasMore();) {
          Attribute newattribute = (Attribute) ne.next();
          String attrID = newattribute.getID();
          Attribute oldattr = attr.get(attrID);

          if (oldattr == null) {
            attr.put(newattribute);
            continue;
          }

          attr.remove(attrID);
          attr.put(newattribute);
        }

        return attr;
      } // case 2
      case DirContext.REMOVE_ATTRIBUTE: {
        if (newattr == null) {
          return attr;
        }

        if (attr == null) {
          return attr;
        }

        for (NamingEnumeration ne = newattr.getAll(); ne.hasMore();) {
          Attribute newattribute = (Attribute) ne.next();
          String attrID = newattribute.getID();
          Attribute oldattr = attr.get(attrID);

          if (oldattr != null) {
            attr.remove(attrID);

            if (newattribute.size() == 0) {
              return attr;
            }

            Object value;

            for (NamingEnumeration vals = newattribute.getAll(); vals.hasMoreElements();) {
              value = vals.nextElement();
              if (!oldattr.remove(value)) {
              }
            }

            if (oldattr.size() != 0) {
              attr.put(oldattr);
            }
          }
        }

        return attr;
      } // case 3
      default: {
        if (LOG_LOCATION.beInfo()) {
          LOG_LOCATION.infoT("Incorrect mode " + mod_op + " in modifyAttributes operation.");
        }
        throw new AttributeModificationException("Incorrect mode " + new Integer(mod_op) + " in modifyAttributes operation.");
      }
    }
  }

  /**
   * Modifies attributes
   *
   * @param attr Attributes to be modified
   * @param mods Contains code of operation and new attributes
   * @throws javax.naming.directory.AttributeModificationException Thrown if a problem when modifying attributes occures.
   * @throws NamingException Thrown if a problem occurs.
   */
  public static Attributes modAttr(Attributes attr, ModificationItem[] mods) throws javax.naming.directory.AttributeModificationException, NamingException {
    if (attr == null) {
      attr = new BasicAttributes();
    }

    for (int i = 0; i < mods.length; i++) {
      String attrID;
      Attribute oldattr;

      if (mods[i] == null) {
        continue;
      }

      switch (mods[i].getModificationOp()) {
        case DirContext.ADD_ATTRIBUTE: {
          if (mods[i].getAttribute() == null) {
            break;
          }

          attrID = mods[i].getAttribute().getID();

          /////////////////////////////////////////////////////////////
          if ((attrID.equals("Persistent")) || (attrID.equals("Flat Context"))) {
            continue;
          }

          /////////////////////////////////////////////////////////////
          oldattr = attr.get(attrID);

          //////////////Agaist page 12 bottom
          if (oldattr == null) {
            oldattr = new BasicAttribute(attrID);
            attr.put(oldattr);
          } //replace it with continue

          ////////////////
          for (NamingEnumeration vals = mods[i].getAttribute().getAll(); vals.hasMore();) {
            oldattr.add(vals.next());
          }

          break;
        }
        case DirContext.REPLACE_ATTRIBUTE: {
          if (mods[i].getAttribute() == null) {
            break;
          }

          attrID = mods[i].getAttribute().getID();

          /////////////////////////////////////////////////////////////
          if ((attrID.equals("Persistent")) || (attrID.equals("Flat Context"))) {
            continue;
          }

          /////////////////////////////////////////////////////////////
          oldattr = attr.get(attrID);

          if (oldattr == null) {
            attr.put(mods[i].getAttribute());
            break;
          }

          attr.remove(attrID);
          attr.put(mods[i].getAttribute());
          break;
        }
        case DirContext.REMOVE_ATTRIBUTE: {
          if (mods[i].getAttribute() == null) {
            break;
          }

          attrID = mods[i].getAttribute().getID();

          /////////////////////////////////////////////////////////////
          if ((attrID.equals("Persistent")) || (attrID.equals("Flat Context")) || (attrID.equals("File Context"))) {
            continue;
          }

          /////////////////////////////////////////////////////////////
          oldattr = attr.get(attrID);

          if (oldattr == null) {
            break;
          }

          attr.remove(attrID);

          //   System.out.println("Now I am removing....................");
          //
          //                for (NamingEnumeration vals = oldattr.getAll();
          //                     vals.hasMoreElements();
          //                     System.out.println(attrID + ": " + vals.nextElement()))
          //                    ;
          if (mods[i].getAttribute().size() == 0) {
            //            System.out.println("Removing the whole: "+attrID);
            attr.remove(attrID);
            break;
          }

          //            System.out.println("Removing parts of: "+attrID);
          Object value;

          for (NamingEnumeration vals = mods[i].getAttribute().getAll(); vals.hasMoreElements();) {
            value = vals.nextElement();
            //                   System.out.println("Removing " + attrID + ": " + value);
            oldattr.remove(value);
          }

          //            System.out.println("Left parts of: "+attrID);
          //
          //                for (NamingEnumeration vals = oldattr.getAll();
          //                     vals.hasMoreElements();
          //                     System.out.println(attrID + ": " + vals.nextElement()))
          //                    ;
          attr.put(oldattr);
          break;
        }
        default: {
          if (LOG_LOCATION.beInfo()) {
            LOG_LOCATION.infoT("Incorrect mode " + mods[i].getModificationOp() + " in modifyAttributes operation.");
          }
          throw new AttributeModificationException("Incorrect mode " + new Integer(mods[i].getModificationOp()) + " in modifyAttributes operation.");
        }
      }
    }

    return attr;
  }

  /**
   * Checks if two attribute sets are equal
   *
   * @param attr Attributes to be scanned
   * @param attrToMatch Attributes to match
   * @return "true" if the attribute sets are equal
   * @throws NamingException Thrown if a problem occurs.
   */
  public static boolean matchingAttributes(Attributes attr, Attributes attrToMatch) throws NamingException {
    for (NamingEnumeration ne = attrToMatch.getAll(); ne.hasMore();) {
      Attribute matchAttribute = (Attribute) ne.next();
      String attrID = matchAttribute.getID();
      Attribute oldattr = attr.get(attrID);

      if (oldattr == null) {
        return false;
      }

      for (NamingEnumeration vals = matchAttribute.getAll(); vals.hasMore();) {
        if (!member(oldattr, vals.next())) {
          return false;
        }
      }
    }

    return true;
  }

  /**
   * Checks if an object is a memeber of an attribute
   *
   * @param attr Attribute to be scanned
   * @param obj Object to use
   * @return "true" if the attribute contains the object
   * @throws NamingException Thrown if a problem occurs.
   */
  private static boolean member(Attribute attr, Object obj) throws NamingException {
    for (NamingEnumeration vals = attr.getAll(); vals.hasMore();) {
      //////////////////////////////////////////////////////////////////////////
      if ((vals.next()).equals(obj)) {
        return true;
      }
    }

    //////////////////////////////////////////////////////////////////////////
    return false;
  }


}

