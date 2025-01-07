/*
 * Copyright (c) 2004 by SAP Labs Bulgaria.,
 * url: http://www.saplabs.bg
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP Labs Bulgaria.
 */
package com.sap.engine.lib.xml.parser.binary.handlers;

import com.sap.engine.lib.util.*;
import com.sap.engine.lib.xml.parser.XMLParser;
import com.sap.engine.lib.xml.parser.binary.common.Constants;
import com.sap.engine.lib.xml.parser.binary.common.MappingData;
import com.sap.engine.lib.xml.parser.binary.pools.MappingDataPool;
import com.sap.engine.lib.xml.parser.helpers.CharArray;

import java.util.Vector;

/**
 * BXML Namespace Handler
 *
 * @author Vladimir Videlov
 * @version 7.10
 */
public final class BinaryNamespaceHandler {
	public final CharArray defaultPrefixName = new CharArray("</>");

	protected HashMapObjectObject hashPrefix;
  protected HashMapObjectObject hashURI;
  protected HashMapObjectIntPositive hashMapping;

  protected HashMapObjectIntPositive hashURItoID;

  protected Vector mapData = new Vector();

  //protected Vector prefixLevels;
  //protected Vector uriLevels;
  protected Vector mapLevels;

  protected ArrayBoolean openedNS;

  protected MappingDataPool mappingPool;

  protected int level;
  protected int mappingID;

  private MappingData mappingData;

 /**
	* Constructs the Binary Namespace Handler
	*/
	public BinaryNamespaceHandler() {
    init();
	}

  public void init() {
    level = 0;

    mappingID = Constants.START_ID_VALUE - 1;

    hashPrefix = new HashMapObjectObject();
    hashURI = new HashMapObjectObject();

    hashMapping = new HashMapObjectIntPositive();
    hashURItoID = new HashMapObjectIntPositive();

		//prefixLevels = new Vector();
    //uriLevels = new Vector();
    mapLevels = new Vector();

    mappingPool = new MappingDataPool(100, 100);
    mappingData = new MappingData();

    openedNS = new ArrayBoolean();

    //addInitial();
  }

  public void clear() {
    hashPrefix.clear();
    hashURI.clear();

    hashMapping.clear();
    hashURItoID.clear();

    //prefixLevels.clear();
    //uriLevels.clear();
    mapLevels.clear();

    mappingPool.release();

    openedNS.clear();
    mapData.clear();
  }

	public void reuse() {
		level = 0;

    mappingID = Constants.START_ID_VALUE - 1;

    hashPrefix.clear();
    hashURI.clear();

    hashMapping.clear();
    hashURItoID.clear();

    //prefixLevels.clear();
    //uriLevels.clear();
    mapLevels.clear();

    mappingPool.releaseAllObjects();

    mappingData.prefix = null;
    mappingData.uri = null;

    openedNS.clear();
    mapData.clear();

    //addInitial();
	}

  protected void addInitial() {
    add(XMLParser.crXML, XMLParser.caXMLNamespace);
    add(XMLParser.caXMLNS, XMLParser.crXMLNSNamespace);

    levelUp();
  }

  /**
   * Inserts the default namespace.
   * @param uri URI
   */
  public void addDefault(CharArray uri) {
    add(defaultPrefixName, uri);
  }

  /**
   * Returns the default namspace
   * @return default namespace
   */
  public CharArray getDefault() {
    return getURI(defaultPrefixName);
  }

  /**
   * Returns the prefix of the namespace pointed by uri.
   * @param uri URI
   * @return the prefix a namespace
   */
  public CharArray getPrefix(CharArray uri) {
	  CharArray result = CharArray.EMPTY;

    if (uri != null && uri.length() > 0) {
      Stack prefixes = (Stack) hashURI.get(uri);

      if (prefixes != null && prefixes.size() > 0) {
        result = (CharArray) prefixes.top();
      }
    }

	  return result;
  }

  /**
   * Returns the uri of the namespace pointed by prefix.
   * @param prefix Prefix
   * @return URI
   */
  public CharArray getURI(CharArray prefix) {
	  CharArray result = CharArray.EMPTY;

    if (prefix == null || prefix.length() == 0) {
      result = getDefault();
    } else {
      Stack uris = (Stack) hashPrefix.get(prefix);

	    if (uris != null && uris.size() > 0) {
        result = (CharArray) uris.top();
	    }
    }

	  return result;
  }

  /**
   * Same as getURI(String prefix), but for attribute namspapces,
   * there is no default namespacing.
   * @param prefix Prefix
   * @return URI
   */
  public CharArray getAttributeURI(CharArray prefix) {
    CharArray result = CharArray.EMPTY;

    Stack uris = (Stack) hashPrefix.get(prefix);

    if (uris != null && uris.size() > 0) {
      result = (CharArray) uris.top();
    }

  	return result;
  }

  /**
   * Checks if the prefix is mapped.
   * @param prefix Prefix
   * @return <tt>true</tt> if the prefix is mapped, <tt>false</tt> otherwise
   */
  public boolean isPrefixMapped(CharArray prefix) {
    boolean result = false;

    if (prefix != null && prefix.length() > 0) {
      Stack uris = (Stack) hashPrefix.get(prefix);
      result = (uris != null && uris.size() > 0);
    }

    return result;
  }

  /**
   * Checks if the URI is mapped.
   * @param uri URI
   * @return <tt>true</tt> if the uri is mapped, <tt>false</tt> otherwise
   */
  public boolean isUriMapped(CharArray uri) {
   boolean result = false;

    if (uri != null && uri.length() > 0) {
      Stack prefixes = (Stack) hashURI.get(uri);
      result = (prefixes != null && prefixes.size() > 0);
    }

    return result;
  }

  /**
   * Adds a namespace, with given prefix and uri.
   * @param prefix Prefix
   * @param uri URI
   * @return Mapping Data
   */
	public MappingData add(CharArray prefix, CharArray uri) {
    // auto increment the mapping id
    mappingID++;

    MappingData mapping = mappingPool.getObject(prefix, uri);

    // mapping from mapping ID -> mapping pair
    mapData.setSize(mappingID + 1);
    mapData.setElementAt(mapping, mappingID);

    // mapping from mapping pair -> mapping ID
    hashMapping.put(mapping, mappingID);

    // mapping from mapping ID -> URI
    hashURItoID.put(uri, mappingID);

    // flags the opening status of the mapping pair
    openedNS.setSize(mappingID + 1);
    openedNS.setElementAt(false, mappingID);

    return mapping;
  }

  /**
   * Opens a namespace, with given mapping ID.
   * @param mappingID Mapping ID
   */
  public void open(int mappingID) {
    open((MappingData) mapData.get(mappingID));
  }

  /**
   * Opens a namespace, with given prefix and uri.
   * @param mapping Mapping Data
   */
  public void open(MappingData mapping) {
    // hash the prefix and uri
    hashPrefixURI(mapping.prefix, mapping.uri);

    int mapID = getMappingID(mapping);

    //addLevelItem(prefixLevels, prefix);
    //addLevelItem(uriLevels, uri);
    addLevelInt(mapLevels, mapID);

    // flags the opened status of the mapping pair
    openedNS.setElementAt(true, mapID);
  }

  /**
   * Opens a namespace, with given prefix and uri.
   * @param prefix Prefix
   * @param uri URI
   */
  public void open(CharArray prefix, CharArray uri) {
    // hash the prefix and uri
    hashPrefixURI(prefix, uri);

    int mapID = getMappingID(prefix, uri);

    //addLevelItem(prefixLevels, prefix);
    //addLevelItem(uriLevels, uri);
    addLevelInt(mapLevels, mapID);

    // flags the opened status of the mapping pair
    openedNS.setElementAt(true, mapID);
  }

  /**
   * Called when the xml scanner goes a level deeper into the xml, and the
   * level counters have to be updated
   *
   */
  public void levelUp() {
	  level++;
  }

  /**
   * Called when the xml scanner goes a level higher in the xml, and checks
   * whether some of the Namespace enties have gone out of scope
   */
  public void levelDown() {
    if (level < 0) {
      return;
    }

    /***
    // removes prefix level
	  if (prefixLevels.size() > level) {
			LinkedList list = (LinkedList) prefixLevels.get(level);

      if (list != null) {
        RootIterator itor = list.elementsIterator();

        while (!itor.isAtEnd()) {
          removePrefixLast((CharArray)itor.next());
        }

        list.clear();
      }
	  }

    // removes uri level
	  if (uriLevels.size() > level) {
			LinkedList list = (LinkedList) uriLevels.get(level);

      if (list != null) {
        RootIterator itor = list.elementsIterator();

        while (!itor.isAtEnd()) {
          removeUriLast((CharArray)itor.next());
        }

        list.clear();
      }
	  }
    /***/

    // removes mapping level
    if (mapLevels.size() > level) {
 			ArrayInt array = (ArrayInt) mapLevels.get(level);

      if (array != null) {
        EnumerationInt enum1 = array.elements();

        int mapID;

        while (enum1.hasMoreElements()) {
          mapID = enum1.nextElement();

          mappingData = (MappingData) mapData.get(mapID);

          removePrefixLast(mappingData.prefix);
          removeUriLast(mappingData.uri);

          openedNS.set(mapID, false);
        }

        array.clear();
      }
    }

    level--;
  }

  public ArrayInt getLevelMappingIDs() {
    return (level > -1 && mapLevels.size() > level) ? (ArrayInt) mapLevels.get(level): null;
  }

  public int getLastMappingID() {
    return mappingID;
  }

  public MappingData getMappingData(int id) {
    return (MappingData) mapData.get(id);
  }

  public int getMappingID(CharArray uri) {
    return hashURItoID.get(uri);
  }

  public int getMappingID(CharArray prefix, CharArray uri) {
    mappingData.prefix = prefix;
    mappingData.uri = uri;

    return hashMapping.get(mappingData);
  }

  public int getMappingID(MappingData mapping) {
    return hashMapping.get(mapping);
  }

  public boolean isMappingOpened(int mappingID) {
    return openedNS.get(mappingID);
  }

  /**
   * Stores a prefix and uri tuple in the hash tables
   * @param prefix Prefix
   * @param uri URI
   */
  protected void hashPrefixURI(CharArray prefix, CharArray uri) {
    // mapping from URI -> prefix
    Stack prefixes = (Stack) hashURI.get(uri);

		if (prefixes != null) {
			prefixes.push(prefix);
		} else {
			prefixes = new Stack();
			prefixes.push(prefix);

			hashURI.put(uri, prefixes);
		}

    // mapping from prefix -> URI
    Stack uris = (Stack) hashPrefix.get(prefix);

		if (uris != null) {
			uris.push(uri);
		} else {
			uris = new Stack();
			uris.push(uri);

			hashPrefix.put(prefix, uris);
		}
  }

  /**
   * Adds an item to some vector holder for a current level.
   * @param levelItems Level items
   * @param item Object item
   */
  protected void addLevelItem(Vector levelItems, Object item) {
    if (levelItems.size() > level) {
		  LinkedList list = (LinkedList) levelItems.get(level);

		  if (list != null) {
			  list.add(item);
		  } else {
			  list = new LinkedList();
			  list.add(item);

		    levelItems.set(level, list);
		  }
	  } else {
			LinkedList list = new LinkedList();
			list.add(item);

		  levelItems.setSize(level + 1);
			levelItems.add(level, list);
	  }
  }

  /**
   * Adds an item to some vector holder for a current level.
   * @param levelItems Level items
   * @param value Level value
   */
  protected void addLevelInt(Vector levelItems, int value) {
    if (levelItems.size() > level) {
		  ArrayInt array = (ArrayInt) levelItems.get(level);

		  if (array != null) {
			  array.add(value);
		  } else {
			  array = new ArrayInt();
			  array.add(value);

		    levelItems.set(level, array);
		  }
	  } else {
			ArrayInt array = new ArrayInt();
			array.add(value);

		  levelItems.setSize(level + 1);
			levelItems.add(level, array);
	  }
  }

	protected CharArray removePrefixLast(CharArray prefix) {
		CharArray result = null;

		Stack uris = ((Stack) hashPrefix.get(prefix));

		if (uris != null && uris.size() > 0) {
			result = (CharArray) uris.pop();
		}

		return result;
	}

	protected CharArray removeUriLast(CharArray uri) {
		CharArray result = null;

    Stack prefixes = ((Stack) hashURI.get(uri));

    if (prefixes != null && prefixes.size() > 0) {
      result = (CharArray) prefixes.pop();
    }

		return result;
	}
}