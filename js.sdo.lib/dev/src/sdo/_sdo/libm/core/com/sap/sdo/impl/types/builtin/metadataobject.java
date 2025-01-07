package com.sap.sdo.impl.types.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sap.sdo.impl.types.SdoProperty;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;
import commonj.sdo.helper.HelperContext;

public abstract class MetaDataObject implements DataObject {
	public void set(String path, Object value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");	
	}

	public boolean isSet(String path) {
		return get(path)!=null;
	}

	public void unset(String path) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public boolean getBoolean(String path) {
		return (Boolean)get(path);
	}

	public byte getByte(String path) {
		return (Byte)get(path);
	}

	public char getChar(String path) {
		return (Character)get(path);
	}

	public double getDouble(String path) {
		return (Double)get(path);
	}

	public float getFloat(String path) {
		return (Float)get(path);
	}

	public int getInt(String path) {
		return (Integer)get(path);
	}

	public long getLong(String path) {
		return (Long)get(path);
	}

	public short getShort(String path) {
		return (Short)get(path);
	}

	public byte[] getBytes(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigInteger getBigInteger(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject getDataObject(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getList(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public Sequence getSequence(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setBoolean(String path, boolean value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setByte(String path, byte value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setChar(String path, char value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setDouble(String path, double value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setFloat(String path, float value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setInt(String path, int value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setLong(String path, long value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setShort(String path, short value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setBytes(String path, byte[] value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setBigDecimal(String path, BigDecimal value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setBigInteger(String path, BigInteger value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setDataObject(String path, DataObject value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setDate(String path, Date value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setString(String path, String value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setList(String path, List value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void set(int propertyIndex, Object value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public boolean isSet(int propertyIndex) {
		return false;
	}

	public void unset(int propertyIndex) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public boolean getBoolean(int propertyIndex) {
		return (Boolean)get(propertyIndex);
	}

	public byte getByte(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public char getChar(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(int propertyIndex) {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte[] getBytes(int propertyIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(int propertyIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigInteger getBigInteger(int propertyIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject getDataObject(int propertyIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public Date getDate(int propertyIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getString(int propertyIndex) {
		return (String)get(propertyIndex);
	}

	public List getList(int propertyIndex) {
		final List list = (List)get(propertyIndex);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
	}

	public Sequence getSequence(int propertyIndex) {
		return null;
	}

	public void setBoolean(int propertyIndex, boolean value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setByte(int propertyIndex, byte value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setChar(int propertyIndex, char value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setDouble(int propertyIndex, double value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setFloat(int propertyIndex, float value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setInt(int propertyIndex, int value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setLong(int propertyIndex, long value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setShort(int propertyIndex, short value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setBytes(int propertyIndex, byte[] value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setBigDecimal(int propertyIndex, BigDecimal value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setBigInteger(int propertyIndex, BigInteger value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setDataObject(int propertyIndex, DataObject value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setDate(int propertyIndex, Date value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setString(int propertyIndex, String value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void setList(int propertyIndex, List value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public void set(Property property, Object value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public boolean isSet(Property property) {
		return isSet(getPropertyIndex(property));
	}

    /**
     * @param property
     * @return
     */
    protected int getPropertyIndex(Property property) {
        return ((SdoProperty)property).getIndex();
    }

	public void unset(Property property) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
		
	}

	public boolean getBoolean(Property property) {
		Boolean value = (Boolean)get(property);
        if (value == null) {
            return (Boolean)property.getDefault();
        }
        return value;
	}

	public byte getByte(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public char getChar(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getDouble(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFloat(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getInt(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public long getLong(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public short getShort(Property property) {
		// TODO Auto-generated method stub
		return 0;
	}

	public byte[] getBytes(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigDecimal getBigDecimal(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	public BigInteger getBigInteger(Property property) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject getDataObject(Property property) {
		return (DataObject)get(property);
	}

	public Date getDate(Property property) {
		return (Date)get(property);
	}

	public String getString(Property property) {
        Object value = get(property);
        if (value != null) {
            return value.toString();
        }
        return null;
	}

	public List getList(Property property) {
		final List list = (List)get(property);
        if (list == null) {
            return Collections.emptyList();
        }
        return list;
	}

	public Sequence getSequence(Property property) {
		return null;
	}

	public void setBoolean(Property property, boolean value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setByte(Property property, byte value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setChar(Property property, char value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setDouble(Property property, double value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setFloat(Property property, float value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setInt(Property property, int value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setLong(Property property, long value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setShort(Property property, short value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setBytes(Property property, byte[] value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setBigDecimal(Property property, BigDecimal value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setBigInteger(Property property, BigInteger value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setDataObject(Property property, DataObject value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setDate(Property property, Date value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setString(Property property, String value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void setList(Property property, List value) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public DataObject createDataObject(String propertyName) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public DataObject createDataObject(int propertyIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	public DataObject createDataObject(Property property) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public DataObject createDataObject(String propertyName, String namespaceURI, String typeName) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public DataObject createDataObject(int propertyIndex, String namespaceURI, String typeName) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public DataObject createDataObject(Property property, Type type) {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public void delete() {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}

	public DataObject getContainer() {
		return null;
	}

	public Property getContainmentProperty() {
		return null;
	}

	public DataGraph getDataGraph() {
		return null;
	}

	public Type getType() {
		return TypeType.getInstance();
	}

	public Sequence getSequence() {
		return null;
	}

	public DataObject getRootObject() {
		return null;
	}

	public ChangeSummary getChangeSummary() {
		return null;
	}

	public void detach() {
		throw new IllegalArgumentException("Attempt to modify predefined type");
	}
	public Object project() {
		throw new RuntimeException();
	}
	public DataObject project(HelperContext ctx) {
		return this;
	}
}
