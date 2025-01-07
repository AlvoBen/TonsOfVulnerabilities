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
package noNamespace;

import java.math.BigDecimal;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;

/**
 * @author D042774
 *
 */
public interface USAddress {
    String getName();

    void setName(String value);

    String getStreet();

    void setStreet(String value);

    String getCity();

    void setCity(String value);

    String getState();

    void setState(String value);

    BigDecimal getZip();

    void setZip(BigDecimal value);

    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData(xmlElement=false))
    String getCountry();

    void setCountry(String value);
}
