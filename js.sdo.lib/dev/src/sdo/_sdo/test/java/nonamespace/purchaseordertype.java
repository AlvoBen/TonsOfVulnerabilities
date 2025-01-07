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

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;

/**
 * @author D042774
 *
 */
public interface PurchaseOrderType {
    @SdoPropertyMetaData(containment=true)
    USAddress getShipTo();

    void setShipTo(USAddress value);

    @SdoPropertyMetaData(containment=true)
    USAddress getBillTo();

    void setBillTo(USAddress value);

    String getComment();

    void setComment(String value);

    @SdoPropertyMetaData(containment=true)
    Items getItems();

    void setItems(Items value);

    @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData(xmlElement=false))
    String getOrderDate();

    void setOrderDate(String value);
}
