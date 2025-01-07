/*
 * Created on 2005.2.9
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sap.engine.cache.util;

import java.util.HashMap;
import java.util.Map;

import com.sap.engine.cache.util.dump.DumpWriter;

/**
 * @author petio-p
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FastAccessList {
  
  private LinkedItem first = null;
  private LinkedItem last = null;
  
  private Map accelerator = null;
  
  public FastAccessList() {
    accelerator = new HashMap();
  }
  
  public boolean contains(Object value) {
    return accelerator.containsKey(value);
  }
  
  public Object addFirst(Object value) {
    Object result = null;
    // if we have repetition, remove it
    LinkedItem newItem = _removeItemFromListOnly(value);
    if (newItem == null) {
      newItem = new LinkedItem(value);
      // add the value to hash accelerator
      accelerator.put(value, newItem);
    } else {
      result = newItem.value;
    }
    LinkedItem found = first;
    first = newItem;
    if (found != null) {
      first.next = found;
      found.prev = first;
    } else {
      last = newItem;
    }
    return result;
  }

  public Object addLast(Object value) {
    Object result = null;
    // if we have repetition, remove it
    LinkedItem newItem = _removeItemFromListOnly(value);
    if (newItem == null) {
      newItem = new LinkedItem(value);
      // add the value to hash accelerator
      accelerator.put(value, newItem);
    } else {
      result = newItem.value;
    }
    LinkedItem found = last;
    last = newItem;
    if (found != null) {
      last.prev = found;
      found.next = last;
    } else {
      first = newItem;
    }
    return result;
  }
  
  public Object getFirst() {
    return first.value;
  }
  
  public Object getLast() {
    return last.value;
  }
  
  private LinkedItem _removeItemFromListOnly(Object value) {
    LinkedItem found = (LinkedItem) accelerator.get(value);
    LinkedItem result = null;
    if (found != null) {
      result = found;
      if (found == first) {
        first = found.next;
        if (found.next != null) {
          found.next.prev = null;
        } else {
          last = null;
        }
      } else if (found == last) {
        last = found.prev;
        if (found.prev != null) {
          found.prev.next = null;
        } else {
          first = null;
        }
      } else {
        found.prev.next = found.next;
        found.next.prev = found.prev;
      }
    }
    return result;
  }
  
  public Object remove(Object value) {
    LinkedItem found = (LinkedItem) accelerator.remove(value);
    Object result = null;
    if (found != null) {
      result = found.value;
      if (found == first) {
        first = found.next;
        if (found.next != null) {
          found.next.prev = null;
        } else {
          last = null;
        }
      } else if (found == last) {
        last = found.prev;
        if (found.prev != null) {
          found.prev.next = null;
        } else {
          first = null;
        }
      } else {
        found.prev.next = found.next;
        found.next.prev = found.prev;
      }
    }
    return result;
  }
  
  public Object removeFirst() {
    LinkedItem found = first;
    Object result = null;
    if (found != null) {
      accelerator.remove(found.value);
      first = found.next;
      if (found.next != null) {
        found.next.prev = null;
      } else {
        last = null;
      }
      result = found.value;
    }
    return result;
  }
  
  public Object removeLast() {
    LinkedItem found = last;
    Object result = null;
    if (found != null) {
      accelerator.remove(found.value);
      last = found.prev;
      if (found.prev != null) {
        found.prev.next = null;
      } else {
        first = null;
      }
      result = found.value;
    }
    return result;
  }
  
  public void printValues() {
    if (first != null) {
      LinkedItem current = first;
      while (current != null) {
        DumpWriter.dump("<" + current.value + ">");
        current = current.next;
      }
    } else {
      DumpWriter.dump("<EMPTY>");
    }
  }
  
  public int size() {
    return accelerator.size();
  }
  
  public static void main(String[] args) {
    FastAccessList list = new FastAccessList();
    list.printValues();
    DumpWriter.dump("- addLast(2) --------------");
    list.addLast("2");
    list.printValues();
    DumpWriter.dump("- addLast(3) --------------");
    list.addLast("3");
    list.printValues();
    DumpWriter.dump("- addFirst(1) -------------");
    list.addFirst("1");
    list.printValues();
    DumpWriter.dump("- remove(2) ---------------");
    list.remove("2");
    list.printValues();
    DumpWriter.dump("- addFirst(3) -------------");
    list.addFirst("3");
    list.printValues();
    DumpWriter.dump("- removeFirst() -----------");
    list.removeFirst();
    list.printValues();
    DumpWriter.dump("- removeLast() ------------");
    list.removeLast();
    list.printValues();
    DumpWriter.dump("- addFirst(3) -------------");
    list.addFirst("3");
    list.printValues();
    DumpWriter.dump("- addFirst(2) -------------");
    list.addFirst("2");
    list.printValues();
    DumpWriter.dump("- addFirst(1) -------------");
    list.addFirst("1");
    list.printValues();
    DumpWriter.dump("- removeFirst() -----------");
    list.removeFirst();
    list.printValues();
    DumpWriter.dump("- removeLast() ------------");
    list.removeLast();
    list.printValues();
    DumpWriter.dump("- remove(2) ---------------");
    list.remove("2");
    list.printValues();
    DumpWriter.dump("- removeFirst() -----------");
    list.removeFirst();
    list.printValues();
    DumpWriter.dump("- removeLast() ------------");
    list.removeLast();
    list.printValues();
    DumpWriter.dump("---------------------------");
  }

}
