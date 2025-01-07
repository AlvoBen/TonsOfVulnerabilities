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
import java.util.List;

import com.sap.sdo.api.SdoPropertyMetaData;
import com.sap.sdo.api.XmlPropertyMetaData;

/**
 * @author D042774
 *
 */
public interface Items {
    @SdoPropertyMetaData(containment=true)
    List<Item> getItem();
    void setItem(List<Item> pItem);

    interface Item {
        String getProductName();

        void setProductName(String value);

        int getQuantity();

        void setQuantity(int value);

        BigDecimal getUSPrice();

        void setUSPrice(BigDecimal value);

        String getComment();

        void setComment(String value);

        String getShipDate();

        void setShipDate(String value);

        @SdoPropertyMetaData(xmlInfo=@XmlPropertyMetaData(xmlElement=false))
        String getPartNum();

        void setPartNum(String value);
    }
}
