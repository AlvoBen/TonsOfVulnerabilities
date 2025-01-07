package com.sap.sdo.impl.types.builtin;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import commonj.sdo.ChangeSummary;
import commonj.sdo.DataGraph;
import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;

public abstract class DelegatingDataObject<D extends DataObject> implements DataObject {
	private static final long serialVersionUID = -8542411676818124513L;
	protected abstract D getDelegate();
	public DataObject createDataObject(int propertyIndex, String namespaceURI, String typeName) {
		return getDelegate().createDataObject(propertyIndex, namespaceURI, typeName);
	}

	public DataObject createDataObject(int propertyIndex) {
		return getDelegate().createDataObject(propertyIndex);
	}

	public DataObject createDataObject(Property property, Type type) {
		return getDelegate().createDataObject(property, type);
	}

	public DataObject createDataObject(Property property) {
		return getDelegate().createDataObject(property);
	}

	public DataObject createDataObject(String propertyName, String namespaceURI, String typeName) {
		return getDelegate().createDataObject(propertyName, namespaceURI, typeName);
	}

	public DataObject createDataObject(String propertyName) {
		return getDelegate().createDataObject(propertyName);
	}

	public void delete() {
		getDelegate().delete();
	}

	public void detach() {
		getDelegate().detach();
	}

	public Object get(int propertyIndex) {
		return getDelegate().get(propertyIndex);
	}

	public Object get(Property property) {
		return getDelegate().get(property);
	}

	public Object get(String path) {
		return getDelegate().get(path);
	}

	public BigDecimal getBigDecimal(int propertyIndex) {
		return getDelegate().getBigDecimal(propertyIndex);
	}

	public BigDecimal getBigDecimal(Property property) {
		return getDelegate().getBigDecimal(property);
	}

	public BigDecimal getBigDecimal(String path) {
		return getDelegate().getBigDecimal(path);
	}

	public BigInteger getBigInteger(int propertyIndex) {
		return getDelegate().getBigInteger(propertyIndex);
	}

	public BigInteger getBigInteger(Property property) {
		return getDelegate().getBigInteger(property);
	}

	public BigInteger getBigInteger(String path) {
		return getDelegate().getBigInteger(path);
	}

	public boolean getBoolean(int propertyIndex) {
		return getDelegate().getBoolean(propertyIndex);
	}

	public boolean getBoolean(Property property) {
		return getDelegate().getBoolean(property);
	}

	public boolean getBoolean(String path) {
		return getDelegate().getBoolean(path);
	}

	public byte getByte(int propertyIndex) {
		return getDelegate().getByte(propertyIndex);
	}

	public byte getByte(Property property) {
		return getDelegate().getByte(property);
	}

	public byte getByte(String path) {
		return getDelegate().getByte(path);
	}

	public byte[] getBytes(int propertyIndex) {
		return getDelegate().getBytes(propertyIndex);
	}

	public byte[] getBytes(Property property) {
		return getDelegate().getBytes(property);
	}

	public byte[] getBytes(String path) {
		return getDelegate().getBytes(path);
	}

	public ChangeSummary getChangeSummary() {
		return getDelegate().getChangeSummary();
	}

	public char getChar(int propertyIndex) {
		return getDelegate().getChar(propertyIndex);
	}

	public char getChar(Property property) {
		return getDelegate().getChar(property);
	}

	public char getChar(String path) {
		return getDelegate().getChar(path);
	}

	public DataObject getContainer() {
		return getDelegate().getContainer();
	}

	public Property getContainmentProperty() {
		return getDelegate().getContainmentProperty();
	}

	public DataGraph getDataGraph() {
		return getDelegate().getDataGraph();
	}

	public DataObject getDataObject(int propertyIndex) {
		return getDelegate().getDataObject(propertyIndex);
	}

	public DataObject getDataObject(Property property) {
		return getDelegate().getDataObject(property);
	}

	public DataObject getDataObject(String path) {
		return getDelegate().getDataObject(path);
	}

	public Date getDate(int propertyIndex) {
		return getDelegate().getDate(propertyIndex);
	}

	public Date getDate(Property property) {
		return getDelegate().getDate(property);
	}

	public Date getDate(String path) {
		return getDelegate().getDate(path);
	}

	public double getDouble(int propertyIndex) {
		return getDelegate().getDouble(propertyIndex);
	}

	public double getDouble(Property property) {
		return getDelegate().getDouble(property);
	}

	public double getDouble(String path) {
		return getDelegate().getDouble(path);
	}

	public float getFloat(int propertyIndex) {
		return getDelegate().getFloat(propertyIndex);
	}

	public float getFloat(Property property) {
		return getDelegate().getFloat(property);
	}

	public float getFloat(String path) {
		return getDelegate().getFloat(path);
	}

	public List getInstanceProperties() {
		return getDelegate().getInstanceProperties();
	}

	public Property getInstanceProperty(String propertyName) {
		return getDelegate().getInstanceProperty(propertyName);
	}

	public int getInt(int propertyIndex) {
		return getDelegate().getInt(propertyIndex);
	}

	public int getInt(Property property) {
		return getDelegate().getInt(property);
	}

	public int getInt(String path) {
		return getDelegate().getInt(path);
	}

	public List getList(int propertyIndex) {
		return getDelegate().getList(propertyIndex);
	}

	public List getList(Property property) {
		return getDelegate().getList(property);
	}

	public List getList(String path) {
		return getDelegate().getList(path);
	}

	public long getLong(int propertyIndex) {
		return getDelegate().getLong(propertyIndex);
	}

	public long getLong(Property property) {
		return getDelegate().getLong(property);
	}

	public long getLong(String path) {
		return getDelegate().getLong(path);
	}

	@Deprecated
    public Property getProperty(String propertyName) {
		return getDelegate().getProperty(propertyName);
	}

	public DataObject getRootObject() {
		return getDelegate().getRootObject();
	}

	public Sequence getSequence() {
		return getDelegate().getSequence();
	}

    @Deprecated
	public Sequence getSequence(int propertyIndex) {
		return getDelegate().getSequence(propertyIndex);
	}

    @Deprecated
	public Sequence getSequence(Property property) {
		return getDelegate().getSequence(property);
	}

    @Deprecated
	public Sequence getSequence(String path) {
		return getDelegate().getSequence(path);
	}

	public short getShort(int propertyIndex) {
		return getDelegate().getShort(propertyIndex);
	}

	public short getShort(Property property) {
		return getDelegate().getShort(property);
	}

	public short getShort(String path) {
		return getDelegate().getShort(path);
	}

	public String getString(int propertyIndex) {
		return getDelegate().getString(propertyIndex);
	}

	public String getString(Property property) {
		return getDelegate().getString(property);
	}

	public String getString(String path) {
		return getDelegate().getString(path);
	}

	public Type getType() {
		return getDelegate().getType();
	}

	public boolean isSet(int propertyIndex) {
		return getDelegate().isSet(propertyIndex);
	}

	public boolean isSet(Property property) {
		return getDelegate().isSet(property);
	}

	public boolean isSet(String path) {
		return getDelegate().isSet(path);
	}

	public void set(int propertyIndex, Object value) {
		getDelegate().set(propertyIndex, value);
	}

	public void set(Property property, Object value) {
		getDelegate().set(property, value);
	}

	public void set(String path, Object value) {
		getDelegate().set(path, value);
	}

	public void setBigDecimal(int propertyIndex, BigDecimal value) {
		getDelegate().setBigDecimal(propertyIndex, value);
	}

	public void setBigDecimal(Property property, BigDecimal value) {
		getDelegate().setBigDecimal(property, value);
	}

	public void setBigDecimal(String path, BigDecimal value) {
		getDelegate().setBigDecimal(path, value);
	}

	public void setBigInteger(int propertyIndex, BigInteger value) {
		getDelegate().setBigInteger(propertyIndex, value);
	}

	public void setBigInteger(Property property, BigInteger value) {
		getDelegate().setBigInteger(property, value);
	}

	public void setBigInteger(String path, BigInteger value) {
		getDelegate().setBigInteger(path, value);
	}

	public void setBoolean(int propertyIndex, boolean value) {
		getDelegate().setBoolean(propertyIndex, value);
	}

	public void setBoolean(Property property, boolean value) {
		getDelegate().setBoolean(property, value);
	}

	public void setBoolean(String path, boolean value) {
		getDelegate().setBoolean(path, value);
	}

	public void setByte(int propertyIndex, byte value) {
		getDelegate().setByte(propertyIndex, value);
	}

	public void setByte(Property property, byte value) {
		getDelegate().setByte(property, value);
	}

	public void setByte(String path, byte value) {
		getDelegate().setByte(path, value);
	}

	public void setBytes(int propertyIndex, byte[] value) {
		getDelegate().setBytes(propertyIndex, value);
	}

	public void setBytes(Property property, byte[] value) {
		getDelegate().setBytes(property, value);
	}

	public void setBytes(String path, byte[] value) {
		getDelegate().setBytes(path, value);
	}

	public void setChar(int propertyIndex, char value) {
		getDelegate().setChar(propertyIndex, value);
	}

	public void setChar(Property property, char value) {
		getDelegate().setChar(property, value);
	}

	public void setChar(String path, char value) {
		getDelegate().setChar(path, value);
	}

	public void setDataObject(int propertyIndex, DataObject value) {
		getDelegate().setDataObject(propertyIndex, value);
	}

	public void setDataObject(Property property, DataObject value) {
		getDelegate().setDataObject(property, value);
	}

	public void setDataObject(String path, DataObject value) {
		getDelegate().setDataObject(path, value);
	}

	public void setDate(int propertyIndex, Date value) {
		getDelegate().setDate(propertyIndex, value);
	}

	public void setDate(Property property, Date value) {
		getDelegate().setDate(property, value);
	}

	public void setDate(String path, Date value) {
		getDelegate().setDate(path, value);
	}

	public void setDouble(int propertyIndex, double value) {
		getDelegate().setDouble(propertyIndex, value);
	}

	public void setDouble(Property property, double value) {
		getDelegate().setDouble(property, value);
	}

	public void setDouble(String path, double value) {
		getDelegate().setDouble(path, value);
	}

	public void setFloat(int propertyIndex, float value) {
		getDelegate().setFloat(propertyIndex, value);
	}

	public void setFloat(Property property, float value) {
		getDelegate().setFloat(property, value);
	}

	public void setFloat(String path, float value) {
		getDelegate().setFloat(path, value);
	}

	public void setInt(int propertyIndex, int value) {
		getDelegate().setInt(propertyIndex, value);
	}

	public void setInt(Property property, int value) {
		getDelegate().setInt(property, value);
	}

	public void setInt(String path, int value) {
		getDelegate().setInt(path, value);
	}

	public void setList(int propertyIndex, List value) {
		getDelegate().setList(propertyIndex, value);
	}

	public void setList(Property property, List value) {
		getDelegate().setList(property, value);
	}

	public void setList(String path, List value) {
		getDelegate().setList(path, value);
	}

	public void setLong(int propertyIndex, long value) {
		getDelegate().setLong(propertyIndex, value);
	}

	public void setLong(Property property, long value) {
		getDelegate().setLong(property, value);
	}

	public void setLong(String path, long value) {
		getDelegate().setLong(path, value);
	}

	public void setShort(int propertyIndex, short value) {
		getDelegate().setShort(propertyIndex, value);
	}

	public void setShort(Property property, short value) {
		getDelegate().setShort(property, value);
	}

	public void setShort(String path, short value) {
		getDelegate().setShort(path, value);
	}

	public void setString(int propertyIndex, String value) {
		getDelegate().setString(propertyIndex, value);
	}

	public void setString(Property property, String value) {
		getDelegate().setString(property, value);
	}

	public void setString(String path, String value) {
		getDelegate().setString(path, value);
	}

	public void unset(int propertyIndex) {
		getDelegate().unset(propertyIndex);
	}

	public void unset(Property property) {
		getDelegate().unset(property);
	}

	public void unset(String path) {
		getDelegate().unset(path);
	}
    
    @Override
    public boolean equals(Object pObj) {
        if (pObj == this) {
            return true;
        }
        return getDelegate().equals(pObj);
    }
    
    @Override
    public int hashCode() {
        return getDelegate().hashCode();
    }
    
    @Override
    public String toString() {
        return getDelegate().toString();
    }
//    public DataObject project(HelperContext ctx) {
//    	return getDelegate().project(ctx);
//    }
//    public Object project() {
//    	return getDelegate().project();
//    }
    
}
