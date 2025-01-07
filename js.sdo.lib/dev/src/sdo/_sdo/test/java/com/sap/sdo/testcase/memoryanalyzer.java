package com.sap.sdo.testcase;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is a tool to calculate the memory footprint of object trees.
 * The MemoryAnalyzer is designed to be used for a single scan.
 * It can also be used to sum up the results of more than one scan. In that case
 * the object instances will not be scanned twice. Note that the objects should
 * not be modified between the several scans with the same MemoryAnalyzer.
 * @author D042807
 *
 */
public class MemoryAnalyzer {
    
    private Map _analyzedObjects = new IdentityHashMap();
    private Set<Class> _classFilter = new HashSet<Class>();
    private Set<Class> _analyzedClasses;
    private long _bytes32bit = 0;
    private long _bytes64bit = 0;
    private long _optimal32bit = 0;
    private long _optimal64bit = 0;
    
    public MemoryAnalyzer() {
        _classFilter.add(Class.class);
    }

    /**
     * Returns the allocated memory of the scanned Objects on a 32 bit JVM.
     * @return The number of bytes.
     */
    public long getBytes32bit() {
        return _bytes32bit;
    }

    /**
     * Returns the allocated memory of the scanned Objects on a 64 bit JVM.
     * @return The number of bytes.
     */
    public long getBytes64bit() {
        return _bytes64bit;
    }
    
    /**
     * Returns the theoretical allocated memory of the scanned Objects on a 
     * 32 bit JVM if there would be no allignment to 8 bytes.
     * @return The number of bytes.
     */
    public long getOptimal32bit() {
        return _optimal32bit;
    }

    /**
     * Returns the theoretical allocated memory of the scanned Objects on a 
     * 64 bit JVM if there would be no allignment to 8 bytes.
     * @return The number of bytes.
     */
    public long getOptimal64bit() {
        return _optimal64bit;
    }
    
    /**
     * A class filter means that objects with exactly this class will be skipped
     * while scanning. The size of these objects will not be added to the final
     * result.
     * @param pClass The class of the objects to skip.
     */
    public void addClassFilter(Class pClass) {
        _classFilter.add(pClass);
    }

    /**
     * An object filter means that these objects will be skipped
     * while scanning. The size of these objects will not be added to the final
     * result. Objects that are scanned once by this MemoryAnalyzer instance
     * are also in the object filter.
     * @param pObject The object to skip.
     */
    public void addObjectFilter(Object pObject) {
        _analyzedObjects.put(pObject, null);
    }

    /**
     * Analyses the memory footprint of this object and the retained objects.
     * Objects that match the {@link #addClassFilter(Class) class filter} or
     * {@link #addObjectFilter(Object) object filter} will be skipped.
     * Objects that are scanned once by this MemoryAnalyzer instance are also in
     * the object filter.
     * @param pObject The root object to analyze.
     */
    public void scanObject(Object pObject) {
        if (pObject == null || _analyzedObjects.containsKey(pObject)) {
            return;
        }
        _analyzedObjects.put(pObject, null);
        Class objectClass = pObject.getClass();
        if (_classFilter.contains(objectClass)) {
            return;
        }
        if (_analyzedClasses != null) {
            _analyzedClasses.add(objectClass);
        }
        if (objectClass.isArray()) {
            scanArray(pObject);
        } else {
            scanObjectLevel(objectClass, pObject);
        }
    }
    
    public void collectAnalyzedClasses(boolean bool) {
        if (_analyzedClasses == null && bool) {
            _analyzedClasses = new HashSet<Class>();
        } else if (!bool) {
            _analyzedClasses = null;
        }
    }
    
    public Set<String> getAnalyzedClasses() {
        Set<String> result = new TreeSet<String>();
        for(Class analyzedClass: _analyzedClasses) {
            result.add(analyzedClass.getName());
        }
        return result;
    }
    
    private void scanObjectLevel(Class pClass, Object pObject) {
        {
            Class superClass = pClass.getSuperclass();
            if (superClass != null) {
                scanObjectLevel(superClass, pObject);
            } else {
                addTo32bit(8);
                addTo64bit(16);
            }
            
        }
        Object[] retainedObjects;
        {
            List retainedObjectList = new ArrayList();
            Field[] fields = pClass.getDeclaredFields();
            int fields32bit = 0;
            int fields64bit = 0;
            for (Field field: fields) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    Class fieldClass = field.getType();
                    int refSize = 0;
                    if (fieldClass == boolean.class) {
                        refSize = 1;
                    } else if (fieldClass == byte.class) {
                        refSize = 1;
                    } else if (fieldClass == char.class) {
                        refSize = 2;
                    } else if (fieldClass == short.class) {
                        refSize = 2;
                    } else if (fieldClass == int.class) {
                        refSize = 4;
                    } else if (fieldClass == float.class) {
                        refSize = 4;
                    } else if (fieldClass == long.class) {
                        refSize = 8;
                    } else if (fieldClass == double.class) {
                        refSize = 8;
                    }
                    if (refSize == 0) {
                        fields32bit += 4;
                        fields64bit += 8;
                        field.setAccessible(true);
                        Object fieldValue = null;
                        try {
                            fieldValue = field.get(pObject);
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        if (fieldValue != null) {
                            retainedObjectList.add(fieldValue);
                        }
                    } else {
                        fields32bit += refSize;
                        fields64bit += refSize;
                    }
                }
            }
            addTo32bit(fields32bit);
            addTo64bit(fields64bit);
            retainedObjects = retainedObjectList.toArray();
        }
        for (int i = 0; i < retainedObjects.length; i++) {
            scanObject(retainedObjects[i]);
        }
    }
    
    private void scanArray(Object arrayObject) {
        Object[] retainedObjects = null;
        {
            Class arrayClass = arrayObject.getClass();
            int refSize = 0;
            int length = 0;
            if (arrayClass == boolean[].class) {
                refSize = 1;
                length = ((boolean[])arrayObject).length;
            } else if (arrayClass == byte[].class) {
                refSize = 1;
                length = ((byte[])arrayObject).length;
            } else if (arrayClass == char[].class) {
                refSize = 2;
                length = ((char[])arrayObject).length;
            } else if (arrayClass == short[].class) {
                refSize = 2;
                length = ((short[])arrayObject).length;
            } else if (arrayClass == int[].class) {
                refSize = 4;
                length = ((int[])arrayObject).length;
            } else if (arrayClass == float[].class) {
                refSize = 4;
                length = ((float[])arrayObject).length;
            } else if (arrayClass == long[].class) {
                refSize = 8;
                length = ((long[])arrayObject).length;
            } else if (arrayClass == double[].class) {
                refSize = 8;
                length = ((double[])arrayObject).length;
            }
            int refSize32bit;
            int refSize64bit;
            if (refSize == 0) {
                refSize32bit = 4;
                refSize64bit = 8;
                retainedObjects = ((Object[])arrayObject);
                length = retainedObjects.length;
            } else {
                refSize32bit = refSize;
                refSize64bit = refSize;
            }
            
            int bytes32bit = 12 + length * refSize32bit;
            int bytes64bit = 20 + length * refSize64bit;
            addTo32bit(bytes32bit);
            addTo64bit(bytes64bit);
        }
        if (retainedObjects != null) {
            for(int i = 0; i < retainedObjects.length; i++) {
                scanObject(retainedObjects[i]);
            }
        }
    }

    private void addTo32bit(int pBytes) {
        _bytes32bit += allignTo8(pBytes);
        _optimal32bit += pBytes;
    }
    
    private void addTo64bit(int pBytes) {
        _bytes64bit += allignTo8(pBytes);
        _optimal64bit += pBytes;
    }

    private int allignTo8(int pBytes) {
        int alligned = pBytes & 0xFFFFFFF8;
        if (alligned < pBytes) {
            alligned += 8;
        }
        return alligned;
    }
   
}
