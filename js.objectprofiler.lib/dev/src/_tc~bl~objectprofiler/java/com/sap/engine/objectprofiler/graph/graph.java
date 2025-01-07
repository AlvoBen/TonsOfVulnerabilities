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

package com.sap.engine.objectprofiler.graph;

import com.sap.engine.objectprofiler.controller.impl.ClassesFilter;

import java.lang.reflect.Field;
import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.rmi.RemoteException;
import java.awt.*;
import java.util.Properties;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Collection;

/**
 * @author Georgi Stanev, Mladen Droshev, Pavel Bonev
 * @version 7.10C
 */
public class Graph implements Serializable {
  public static String envFilesDir = "com/sap/engine/objectprofiler/graph/envFiles/";

  public static int MAX_NODES = 1000;

  private HashMap nodes = new HashMap();
  private ArrayList references = new ArrayList();

  // Key = node, Value = ArrayList with all references refered by the key node
  private HashMap kids = new HashMap();

  // Key = node, Value = ArrayList with all references refering to the key node
  private HashMap parents = new HashMap();

  private Node root = null;

  private boolean isShareable = false;
  private HashMap shareabilityMap = new HashMap();

  private static boolean debug = false;

  private Properties additionalInfo = new Properties();

  static final long serialVersionUID = -6707211165951057426L;

  public Graph() {
  }

  public Graph(Node root, Node[] _nodes, Reference[] refs) {
    this.root = root;

    for (int i=0;i<_nodes.length;i++) {
      nodes.put(new Key(_nodes[i]), _nodes[i]);
    }

    for (int i=0;i<refs.length;i++) {
      addReference(refs[i]);
    }
  }

  public void addReference(Reference ref) {
    references.add(ref);
    addParent(ref);
    addKid(ref);
  }

  public void addKid(Reference ref) {
    Node parent = ref.getParent();
    ArrayList list = (ArrayList)kids.get(parent);
    if (list == null) {
      list = new ArrayList();
      kids.put(parent, list);
    }
    list.add(ref);
  }

  public int getSize() {
    int size = 0;
    Iterator iterat = nodes.values().iterator();
    while (iterat.hasNext()) {
      Node node = (Node)iterat.next();
      size += node.getWeight();
    }

    return size;
  }

  public void addParent(Reference ref) {
    Node kid = ref.getChild();
    ArrayList list = (ArrayList)parents.get(kid);
    if (list == null) {
      list = new ArrayList();
      parents.put(kid, list);
    }
    list.add(ref);
  }

  public static void setMaxNodesThreshold(int threshold) {
    if (threshold > 0) {
      MAX_NODES = threshold;
    }
  }

  public boolean isShareable() {
    return isShareable;
  }

  public static Graph buildGraph(Object obj) throws RemoteException {
    try {
      return buildGraph(obj, -1);
    } catch (Exception e) {
      throw new RemoteException(e.getMessage());
    }
  }


  public static Graph buildGraph(Object obj, int level) throws Exception {
    return buildGraph(obj, level, null);
  }

  public static Graph buildGraph(Object obj, int level, ClassesFilter _filter) throws Exception {
    return buildGraph(obj, level, _filter, false, false);
  }

  public static Graph buildGraph(Object obj, int level, ClassesFilter _filter, boolean includeTransients, boolean onlyNonShareable) throws Exception {
    Graph result = new Graph();
    //result.buildGraphRecursively(obj, level, _filter, includeTransients, onlyNonShareable);
    result.buildGraphIteratively(obj, level, _filter, includeTransients, onlyNonShareable);

    return result;
  }

 private void buildGraphIteratively(Object _parent, int _level, ClassesFilter classesFilter, boolean includeTransients, boolean onlyNonshareable) throws Exception {
   LinkedList nodeCandidates = new LinkedList();
   NodeTriplet triplet = new NodeTriplet(null, _parent, null, _level);

   nodeCandidates.addLast(triplet);

   while (nodeCandidates.size() > 0) {
     NodeTriplet currentTriplet = (NodeTriplet)nodeCandidates.removeFirst();
     int currentLevel = currentTriplet.level;

     if (!onlyNonshareable && nodes.size() >= MAX_NODES) {
      if (currentLevel != -2) {
        throw new Exception("Too many nodes, please decrease the depth level and try again!");
      } else {
        return;
      }
     }

     if (currentLevel > 0) {
       currentLevel--;
     }

     Node currentNode = Node.buildNode(currentTriplet.child, includeTransients);
     currentNode.setID(nodes.values().size());
     Key parentKey = new Key(currentTriplet.child);
     nodes.put(parentKey, currentNode);
     //calcShareability(currentNode, currentTriplet.child);

     if (root == null) {
       root = currentNode;
     } else {
       Reference ref = new Reference(currentTriplet.refName, currentTriplet.parentNode, currentNode, false);
       addReference(ref);
     }

     // CASE 1: Array of objects
     if (currentTriplet.child.getClass().isArray()) {
       int arLength = Array.getLength(currentTriplet.child);
       if (!currentTriplet.child.getClass().getComponentType().isPrimitive()) {
         for (int i = 0; i < arLength; i++) {
           Object child = Array.get(currentTriplet.child, i);

           if (child != null) {
             if ((currentLevel == 0) ||
                 ((classesFilter != null) &&
                  (classesFilter.filter(child)))) {
               continue;
             }
             Key childKey = new Key(child);
             Node childNode = (Node)nodes.get(childKey);
             String refName = "["+i+"]";
             if (childNode == null) {
               NodeTriplet newTriplet = new NodeTriplet(currentNode, child, refName, currentLevel);
               nodeCandidates.addLast(newTriplet);
             } else {
               Reference ref = new Reference(refName, currentNode, childNode, false);
               addReference(ref);
             }
           }
         }
       }
     } else {
       // CASE 2: An Object
       Field[] nonPrimitiveFields = Node.getNonPrimitiveFields(currentTriplet.child.getClass(), includeTransients);
        if (nonPrimitiveFields != null) {
          for (int i = 0; i < nonPrimitiveFields.length; i++) {
            try {
              Object child = nonPrimitiveFields[i].get(currentTriplet.child);
              if (child != null) {
                if ((currentLevel == 0) ||
                    ((classesFilter != null) &&
                     (classesFilter.filter(child)))) {
                  continue;
                }
                Key childKey = new Key(child);
                Node childNode = (Node)nodes.get(childKey);
                String refName = nonPrimitiveFields[i].getName();
                if (childNode == null) {
                  NodeTriplet newTriplet = new NodeTriplet(currentNode, child, refName, currentLevel);
                  nodeCandidates.addLast(newTriplet);
                } else {
                  Reference ref = new Reference(refName, currentNode, childNode, false);
                  addReference(ref);
                }
              }
            } catch (IllegalAccessException e) {
              //System.out.println(">> Exception : " + e.getMessage());
              e.printStackTrace();
            }
          }
        }
      }
    }
  }


  private void buildGraphRecursively(Object parent, int level, ClassesFilter classesFilter, boolean includeTransients, boolean onlyNonshareable) throws Exception {
    // threshod check
    if (nodes.size() > MAX_NODES) {
      if (level != -2) {
        throw new Exception("Too many nodes, please decrease the depth level and try again!");
      } else {
        return;
      }
    }

    // filters check
    if (classesFilter != null) {
      if (classesFilter.filter(parent)) {
        return;
      }
    }

    Node parentNode = Node.buildNode(parent, includeTransients);
    parentNode.setID(nodes.values().size());
    Key parentKey = new Key(parent);
    nodes.put(parentKey, parentNode);
    //calcShareability(parentNode, parent);
    if (root == null) {
      root = parentNode;
    }

    // parsing depth check
    if (level == 0) {
      // put a dummy node in case of there are some anchestors
      Field[] nonPrimitiveFields = Node.getNonPrimitiveFields(parent.getClass(), includeTransients);
      if (nonPrimitiveFields != null && nonPrimitiveFields.length > 0) {
        Node dummyNode = new Node();
        Key dummyKey = new Key(dummyNode);
        nodes.put(dummyKey,dummyNode);

        Reference ref = new Reference("<dummy>", parentNode, dummyNode, false);
        addReference(ref);
      }

      return;
    } else if (level > 0) {
      level--;
    }

    // CASE 1: Array of objects
    if (parent.getClass().isArray()) {
      int arLength = Array.getLength(parent);
      if (!parent.getClass().getComponentType().isPrimitive()) {
        for (int i = 0; i < arLength; i++) {
          Object child = Array.get(parent, i);
          if (child != null) {
            Key childKey = new Key(child);
            Node childNode = (Node)nodes.get(childKey);
            if (childNode == null) {
              buildGraphRecursively(child, level, classesFilter, includeTransients, onlyNonshareable);
              childNode = (Node)nodes.get(childKey);
            }

            if (childNode != null) {
              if (!childNode.isShareable() || childNode.hasNonShareableKids()) {
                parentNode.setNonShareableKids(true);
              }
              String refName = "["+i+"]";
              Reference ref = new Reference(refName, parentNode, childNode, false);
              addReference(ref);
            }
          }
        }
      }
      if (onlyNonshareable &&
            parentNode.isShareable() &&
            !parentNode.hasNonShareableKids()) {
        nodes.remove(parentKey);
      }

      return;
    }

    // CASE 2: An Object
    Field[] nonPrimitiveFields = Node.getNonPrimitiveFields(parent.getClass(), includeTransients);
    if (nonPrimitiveFields != null) {
      for (int i = 0; i < nonPrimitiveFields.length; i++) {
        try {
          //System.out.println( " Parent class:"+parent.getClass().getName() + " child ref Name:"+nonPrimitiveFields[i].getName());
          Object child = nonPrimitiveFields[i].get(parent);
          if (child != null) {
            Key childKey = new Key(child);
            Node childNode = (Node)nodes.get(childKey);
            if (childNode == null) {
              buildGraphRecursively(child, level, classesFilter, includeTransients, onlyNonshareable);
              childNode = (Node)nodes.get(childKey);
            }

            if (childNode != null) {
              if (!childNode.isShareable() || childNode.hasNonShareableKids()) {
                parentNode.setNonShareableKids(true);
              }
              String refName = nonPrimitiveFields[i].getName();
              Reference ref = new Reference(refName, parentNode, childNode, Modifier.isTransient(nonPrimitiveFields[i].getModifiers()));
              addReference(ref);
            }
          }
        } catch (IllegalAccessException e) {
          //System.out.println(">> Exception : " + e.getMessage());
          e.printStackTrace();
        }
      }
    }
    if (onlyNonshareable &&
          parentNode.isShareable() &&
          !parentNode.hasNonShareableKids()) {
      nodes.remove(parentKey);
    }
  }

  public void setShareabilityMap(HashMap map) {
    shareabilityMap = map;
  }

  public static ShareabilityDescription buildShareabilityDescription(Object _props) {
    //com.sap.vmc.core.sharing.ShareabilityProperties props = (com.sap.vmc.core.sharing.ShareabilityProperties)_props;
    //ShareabilityDescription desc = null;

    //String className = props.getDescribedClass().getName();
    //boolean isShareable = props.isShareable();


    // compatibility with sharebility version
//    HashMap flags = new HashMap();
//    flags.put(Node.DEFINED_SHAREABLE, true);
//    flags.put(Node.NON_SERILIZABLE_BASE_CLASS, false);
//    flags.put(Node.NON_SHAREABLE_CLASSLOADER, false);
//    flags.put(Node.NON_TRIVIAL_FINALIZER, false);
//    flags.put(Node.READ_EXTERNAL, false);
//    flags.put(Node.READ_RESOLVE, false);
//    flags.put(Node.READ_OBJECT, false);
//    flags.put(Node.WRITE_EXTERNAL, false);
//    flags.put(Node.WRITE_REPLACE, false);
//    flags.put(Node.WRITE_OBJECT, false);
//    flags.put(Node.NOT_SERIALIZABLE, false);
//    flags.put(Node.NOT_SERIALIZABLE, false);
//    flags.put(Node.SERIAL_PERSISTENT_FIELD, false);
//    flags.put(Node.TRANSIENT_FIELD, false);
//
//    StringBuffer buf = new StringBuffer();
//    boolean commaFlag = false;

    // DEPRECATED SHAREABILITY
//    if (!isShareable) {
//      if (props.hasNonSerializableBaseClass()) {
//        buf.append("non serializable base class");
//        commaFlag = true;
//      }
//
//      if (props.hasNonShareableClassLoader()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has non shareable class loader");
//      }
//
//      if (props.hasNonTrivialFinalizer()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has non trivial finalizer");
//      }
//
//      if (props.hasReadExternal()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has readExternal()");
//      }
//
//      if (props.hasReadObject()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has readObject()");
//      }
//
//      if (props.hasReadResolve()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has readResolve()");
//      }
//
//      if (props.hasSerialPersistentFieldsField()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has serial persistent field");
//      }
//
//      if (props.hasTransientFields()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has transient field");
//      }
//
//      if (props.hasWriteExternal()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has writeExternal()");
//      }
//
//      if (props.hasWriteObject()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has writeObject()");
//      }
//
//      if (props.hasWriteReplace()) {
//        if (commaFlag) {
//          buf.append(", ");
//        } else {
//          commaFlag = true;
//        }
//        buf.append("has writeReplace()");
//      }
//
//      if (props.isNotSerializable()) {
//        if (commaFlag) {
//          buf.append(", ");
//        }
//        buf.append("not serializable");
//      }
//    }

//    desc = new ShareabilityDescription(className, true, buf.toString());
//    desc.setFlags(flags);
//
//    return desc;
    return null;
  }

  public void setRoot(Node node) {
    root = node;
  }

  public Node getRoot() {
    return root;
  }

  private void calcShareability(Node node, Object value) {
//    Class _class = value.getClass();
//    String className = _class.getName();
//    ShareabilityDescription desc = (ShareabilityDescription)shareabilityMap.get(className);

//    if (desc == null) {
//      try {
//        com.sap.vmc.core.sharing.ShareabilityAnalyzer analyzer = com.sap.vmc.core.sharing.ShareabilityAnalyzer.getInstance();
//        com.sap.vmc.core.sharing.ShareabilityProperties props = analyzer.examineClass(_class);
//        desc = buildShareabilityDescription(props);
//        shareabilityMap.put(className, desc);
//      } catch (Throwable t) {
//        desc = new ShareabilityDescription(className, false, getStackTrace(t));
//        shareabilityMap.put(className, desc);
//      }
//    }

//    if (desc != null) {
//      HashMap nodeProps = desc.getFlags();
//      node.setShareable(desc.getShareable());
//      node.setReasonForNonShareability(desc.getReason());
//      node.setProps(nodeProps);
//    }
  }



  public static String getStackTrace(Throwable t) {
    StringWriter writer = new StringWriter();
    PrintWriter pw = new PrintWriter(writer);
    t.printStackTrace(pw);
    writer.flush();

    return writer.toString();
  }

  public Reference getReference(Node parent, Node kid) {
    ArrayList list = (ArrayList)kids.get(parent);
    for (int i=0;i<list.size();i++) {
      Reference ref = (Reference)list.get(i);
      if (kid == ref.getChild()) {
        return ref;
      }
    }

    return null;
  }

  public Node[] getParents(Node current) {
    ArrayList list = (ArrayList)parents.get(current);

    if (list != null) {
      Node[] nodes = new Node[list.size()];

      for (int i=0;i<list.size();i++) {
        Reference ref = (Reference)list.get(i);
        nodes[i] = ref.getParent();
      }

      return nodes;
    } else {
      return null;
    }
  }

  public Node[] getChildren(Node current) {
    ArrayList list = (ArrayList)kids.get(current);

    if (list != null) {
      Node[] nodes = new Node[list.size()];

      for (int i=0;i<list.size();i++) {
        Reference ref = (Reference)list.get(i);
        nodes[i] = ref.getChild();
      }

      return nodes;
    } else {
      return null;
    }
  }

  public String[] getInfo() {
    String[] info = new String[3];

    info[0] = "Shareable            : "+isShareable;
    info[1] = "Number of nodes      : "+nodes.size();
    info[2] = "Number of references : "+references.size();

    return info;
  }

  public Collection getNodes() {
    return nodes.values();
  }

  public ArrayList getReferences() {
    return references;
  }

  public int getNodeCount() {
    return nodes.size();
  }


  public int delta(Node a, Node b) {
    int delta = calcSubgraphWeight(a, b, new ArrayList()) - calcSubgraphWeight(b, a, new ArrayList());

    return delta;
  }

  private int calcSubgraphWeight(Node a, Node b, ArrayList visited) {
    if (visited.contains(a) || a.equals(b)) {
      return 0;
    }

    int weight = a.getWeight();
    visited.add(a);
    Node[] kids = getChildren(a);
    if (kids != null) {
      for (int i = 0; i < kids.length; i++) {
        weight += calcSubgraphWeight(kids[i], b, visited);
      }
    }

    return weight;
  }

  public static void setDebug(boolean debug) {
    Graph.debug = debug;
  }

  public static boolean isDebug() {
    return debug;
  }


  private static final String[] letters = new String[]{"b", "K", "M", "G", "T"};

  public static String convertToShortSize(float size) {
    float threshold = 1024f;
    String res = null;

    int counter = 0;

    while (counter < letters.length - 1 && size > threshold) {
      counter++;
      size /= threshold;
    }

    DecimalFormat formater = new DecimalFormat("0.#" + letters[counter]);
    res = formater.format(size);

    return res;


  }

  public HashMap getClassStatistics() {
    HashMap result = new HashMap();
    Collection allnodes = getNodes();
    Iterator iterat = allnodes.iterator();
    while (iterat.hasNext()) {
      Node node = (Node)iterat.next();
      int size = node.getWeight();
      ClassData cd = node.getCurrentClassData();

      if  (cd != null) {
        String className = cd.getClassName();
        Dimension info = (Dimension)result.get(className);

        if (info == null) {
          result.put(className, new Dimension(1,size));
        } else {
          int counter = (int)info.getWidth()+1;
          int weight = (int)info.getHeight()+size;
          info.setSize(counter, weight);
        }
      }
    }
    return result;
  }

  public void setProperty(String key, String value) {
    additionalInfo.put(key, value);
  }

  public void setProperties(Properties p) {
    additionalInfo = p;
  }

  public Properties getProperties() {
    return additionalInfo;
  }


  public void writeInXML(String fileName) {

  }

  private class NodeTriplet {
    public Node parentNode;
    public Object child;
    public String refName;
    public int level;

    public NodeTriplet(Node parentNode, Object child, String refName, int level) {
      this.parentNode = parentNode;
      this.child = child;
      this.refName = refName;
      this.level = level;
    }
  }

}
