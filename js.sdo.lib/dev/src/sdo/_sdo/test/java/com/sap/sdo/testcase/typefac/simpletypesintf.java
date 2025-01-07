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
package com.sap.sdo.testcase.typefac;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;

public interface SimpleTypesIntf {


    Boolean getBooleanObject();
    void setBooleanObject(Boolean value);

    boolean getBoolean();
    void setBoolean(boolean value);

    Byte getByteObject();
    void setByteObject(Byte value);

    byte getByte();
    void setByte(byte value);

    byte[] getBytes();
    void setBytes(byte[] value);

    char getCharacter();
    void setCharacter(char value);

    Character getCharacterObject();
    void setCharacterObject(Character value);

    Date getDate();
    void setDate(Date value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#Day")
    String getDay();
    void setDay(String value);

    BigDecimal getDecimal();
    void setDecimal(BigDecimal value);

    Double getDoubleObject();
    void setDoubleObject(Double value);

    double getDouble();
    void setDouble(double value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#Duration")
    String getDuration();
    void setDuration(String value);

    Float getFloatObject();
    void setFloatObject(Float value);

    float getFloat();
    void setFloat(float value);

    Integer getIntObject();
    void setIntObject(Integer value);

    int getInt();
    void setInt(int value);

    BigInteger getInteger();
    void setInteger(BigInteger value);

    Long getLongObject();
    void setLongObject(Long value);

    long getLong();
    void setLong(long value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#MonthDay")
    String getMonthDay();
    void setMonthDay(String value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#Month")
    String getMonth();
    void setMonth(String value);

    Object getObject();
    void setObject(Object value);

    Short getShortObject();
    void setShortObject(Short value);

    short getShort();
    void setShort(short value);

    String getString();
    void setString(String value);

    List<String> getStringMany();
    void setStringMany(List<String> value);

    @SdoPropertyMetaData(xmlInfo = @com.sap.sdo.api.XmlPropertyMetaData(xmlElement = false))
    List<String> getStringsAttr();
    void setStringsAttr(List<String> value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#Strings")
    List<String> getStrings();
    void setStrings(List<String> value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#Time")
    String getTime();
    void setTime(String value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#URI")
    String getURI();
    void setURI(String value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#Year")
    String getYear();
    void setYear(String value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#YearMonth")
    String getYearMonth();
    void setYearMonth(String value);

    @SdoPropertyMetaData(sdoType="commonj.sdo#YearMonthDay")
    String getYearMonthDay();
    void setYearMonthDay(String value);
}
