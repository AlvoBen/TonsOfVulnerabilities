/*
 * Copyright (c) 2006 by SAP AG, Walldorf.,
 * http://www.sap.com
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of SAP AG, Walldorf. You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms
 * of the license agreement you entered into with SAP.
 */
package com.sap.sdo.impl.types.simple;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.sap.sdo.api.util.URINamePair;
import com.sap.sdo.impl.objects.DataFactoryImpl;
import com.sap.sdo.impl.types.TypeHelperImpl;
import com.sap.sdo.impl.types.builtin.MetaDataType;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Sequence;
import commonj.sdo.Type;

/**
 * simple type abstraction over java class
 *
 */
public abstract class JavaSimpleType<S> extends MetaDataType<S> implements DataObject
{
    public static final BooleanSimpleType BOOLEAN = new BooleanSimpleType();
    public static final BooleanObjectSimpleType BOOLEANOBJECT = new BooleanObjectSimpleType();
    public static final Boolean01SimpleType BOOLEAN_01 = new Boolean01SimpleType();
    public static final ByteSimpleType BYTE    = new ByteSimpleType();
    public static final ByteObjectSimpleType BYTEOBJECT  = new ByteObjectSimpleType();
	public static final BytesSimpleType BYTES = new BytesSimpleType();
    public static final CharSimpleType CHARACTER = new CharSimpleType();
    public static final CharObjectSimpleType CHARACTEROBJECT = new CharObjectSimpleType();
	public static final DateSimpleType DATE = new DateSimpleType();
	public static final DateTimeSimpleType DATETIME = new DateTimeSimpleType();
	public static final DaySimpleType DAY = new DaySimpleType();
	public static final DecimalSimpleType DECIMAL = new DecimalSimpleType();
	public static final DurationSimpleType DURATION = new DurationSimpleType();
    public static final DoubleSimpleType DOUBLE = new DoubleSimpleType();
    public static final DoubleObjectSimpleType DOUBLEOBJECT = new DoubleObjectSimpleType();
    public static final FloatSimpleType FLOAT = new FloatSimpleType();
    public static final FloatObjectSimpleType FLOATOBJECT = new FloatObjectSimpleType();
    public static final IntSimpleType INT = new IntSimpleType();
    public static final IntObjectSimpleType INTOBJECT = new IntObjectSimpleType();
	public static final IntegerSimpleType INTEGER = new IntegerSimpleType();
    public static final LongSimpleType LONG = new LongSimpleType();
    public static final LongObjectSimpleType LONGOBJECT = new LongObjectSimpleType();
	public static final MonthSimpleType MONTH = new MonthSimpleType();
	public static final MonthDaySimpleType MONTHDAY = new MonthDaySimpleType();
	public static final ObjectSimpleType OBJECT = new ObjectSimpleType();
    public static final ShortSimpleType SHORT = new ShortSimpleType();
    public static final ShortObjectSimpleType SHORTOBJECT = new ShortObjectSimpleType();
	public static final StringSimpleType STRING = new StringSimpleType();
	public static final StringsSimpleType STRINGS = new StringsSimpleType();
	public static final TimeSimpleType TIME = new TimeSimpleType();
    public static final URISimpleType URI = new URISimpleType();
    public static final YearSimpleType YEAR = new YearSimpleType();
    public static final YearMonthSimpleType YEARMONTH = new YearMonthSimpleType();
    public static final YearMonthDaySimpleType YEARMONTHDAY = new YearMonthDaySimpleType();
    public static final ClassType CLASS = new ClassType();
    /** @deprecated Use key-annotations */
    @Deprecated
    public static final IdSimpleType ID = new IdSimpleType();
    
    //default values for the primitive types, if the value would be null
    public static final Boolean DEFAULT_BOOLEAN = Boolean.FALSE;
    public static final Byte DEFAULT_BYTE = Byte.valueOf((byte)0);
    public static final Character DEFAULT_CHAR = Character.valueOf('\u0000');
    public static final Double DEFAULT_DOUBLE = Double.valueOf(0d);
    public static final Float DEFAULT_FLOAT = Float.valueOf(0f);
    public static final Integer DEFAULT_INT = Integer.valueOf(0);
    public static final Long DEFAULT_LONG = Long.valueOf(0L);
    public static final Short DEFAULT_SHORT = Short.valueOf((short)0);
    
    
    JavaSimpleType(URINamePair unp, Class<S> clz) {
        setUNP(unp);
        setInstanceClass(clz);
        setOpen(false);
        setAbstract(false);
        setDataType(true);
        setSequenced(false);
        useCache();
    }
    public boolean isOpen() {
    	return false;
    }
    public boolean isDataType() {
    	return true;
    }
    public boolean isAbstract() {
    	return false;
    }
    public boolean isSequenced() {
    	return false;
    }
    // helper: default conversions between basic number types.
    protected static <T> T convertToNumberType(Class<T> targetType, Number pData) {
        if (targetType==Double.class) {
            return (T)convertToDouble(pData);
        }
        if (targetType==Float.class) {
            return (T)convertToFloat(pData);
        }
        if (targetType==Integer.class) {
            return (T)convertToInteger(pData);
        }
        if (targetType==Long.class) {
            return (T)convertToLong(pData);
        }
        if (targetType==Short.class) {
            return (T)convertToShort(pData);
        }
        if (targetType==BigDecimal.class) {
            return (T)convertToBigDecimal(pData);
        }
        if (targetType==BigInteger.class) {
            return (T)convertToBigInteger(pData);
        }
        if (targetType==Byte.class) {
            return (T)convertToByte(pData);
        }
        throw new ClassCastException("Can not convert from " + pData.getClass().getName() +
            " to " + targetType.getName());
    }
    
    protected static Double convertToDouble(Number data) {
        return Double.valueOf(data.doubleValue());
    }
    
    protected static Float convertToFloat(Number data) {
        return Float.valueOf(data.floatValue());
    }

    protected static Integer convertToInteger(Number data) {
        return Integer.valueOf(data.intValue());
    }

    protected static Long convertToLong(Number data) {
        return Long.valueOf(data.longValue());
    }
    
    protected static Short convertToShort(Number data) {
        return Short.valueOf(data.shortValue());
    }
    
    protected static BigDecimal convertToBigDecimal(Number data) {
        // constructor provides better results than BigDecimal.getValue()
        // for whole numbers
        return new BigDecimal(data.doubleValue());
    }
    
    protected static BigInteger convertToBigInteger(Number data) {
        return BigInteger.valueOf(data.longValue());
    }
    
    protected static Byte convertToByte(Number data) {
        return Byte.valueOf(data.byteValue());
    }
    
    protected S convertFromWrapperOrEx(Object data) {
        if (data instanceof DataObject) {
            DataObject dataObject = (DataObject)data;
            Property simpleContentProperty = TypeHelperImpl.getSimpleContentProperty(dataObject);
            if (simpleContentProperty != null) {
                return convertFromJavaClass(dataObject.get(simpleContentProperty));
            }
            Sequence sequence = dataObject.getSequence();
            if (sequence != null) {
                StringBuilder text = null;
                for (int i = 0; i < sequence.size(); i++) {
                    if (sequence.getProperty(i) == null) {
                        if (text == null) {
                            text = new StringBuilder();
                        }
                        text.append(sequence.getValue(i));
                    } else {
                        text = null;
                        break;
                    }
                }
                if (text != null) {
                    return convertFromJavaClass(text.toString().trim());
                }
            }
        }
        throw new ClassCastException("Can not convert from " + data.getClass().getName() +
            " to " + getInstanceClass().getName());
    }

    protected <T> T convertToWrapperOrEx(S data, Class<T> targetType) {
        if (targetType==DataObject.class) {
            return (T)((DataFactoryImpl)getHelperContext().getDataFactory()).createWrapper(this, data);
        }
        throw new ClassCastException("Can not convert from " + getInstanceClass().getName() +
            " to " + targetType.getName());
    }

    @Override
    public boolean isAssignableType(Type assignableFrom) {
        if (assignableFrom == this) {
            return true;
        }
        List<Type> baseTypes = assignableFrom.getBaseTypes();
        for (int i = 0; i < baseTypes.size(); i++) {
            if (isAssignableType(baseTypes.get(i))) {
                return true;
            }
        }
        if (assignableFrom instanceof JavaSimpleType) {
            JavaSimpleType javaSimpleType = (JavaSimpleType)assignableFrom;
            if (getNillableType() == javaSimpleType.getNillableType()) {
                return true;
            }
            Type internalBaseType = javaSimpleType.getInternalBaseType();
            if (internalBaseType != null) {
                return isAssignableType(internalBaseType);
            }
        }
		return false;
	}

    /**
     * Returns the nillable version of the Type.
     * @return The same type but nillable.
     */
	public JavaSimpleType<S> getNillableType() {
		return this;
	}
    
    /**
     * Returnes an imaginary base type. The value range of this base type has to
     * cover the full value range of this type. In most cases this represents
     * the hierarchy of the related XSD types.
     * @return The base type or null.
     */
    public JavaSimpleType<?> getInternalBaseType() {
        return null;
    }
    
    @Override
    public S copy(S pO, boolean pShallow) {
        return pO;
    }
}
