package com.sap.jms.util.compat.concurrent;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;


public class ConcurrentHashMap extends java.util.Hashtable {
	
	 public synchronized Set entrySet() {
		 Hashtable copy = (Hashtable) super.clone();
		 Set result = ((ConcurrentHashMap) copy).getEntrySet();
		 return result;		 		 
	 }
	 
	 private Set getEntrySet() {
		 return super.entrySet();
	 }
	 
	 public synchronized Set keySet() {
		 Hashtable copy = (Hashtable) super.clone();
		 Set result = ((ConcurrentHashMap) copy).getKeySet();
		 return result;		 		 
	 }
	 
	 private Set getKeySet() {
		 return super.keySet();
	 }	 
	 
	 public synchronized Collection values() {
		 Hashtable copy = (Hashtable) super.clone();
		 Collection result = ((ConcurrentHashMap) copy).getValues();
		 return result;		 		 
	 }
	 
	 private Collection getValues() {
		 return super.values();
	 }	 	 
}
