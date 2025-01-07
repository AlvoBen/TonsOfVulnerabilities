
package com.sap.xi.appl.se.global;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for outboundDeliveryByBatchIDQueryResponseInMany complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="outboundDeliveryByBatchIDQueryResponseInMany">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="parameters" type="{http://sap.com/xi/APPL/SE/Global}OutboundDeliveryByBatchIDQueryMessage_sync" minOccurs="0"/>
 *         &lt;element name="count" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "outboundDeliveryByBatchIDQueryResponseInMany", namespace = "http://sap.com/xi/appl/se/global/", propOrder = {
    "parameters",
    "count"
})
public class OutboundDeliveryByBatchIDQueryResponseInMany {

    protected OutboundDeliveryByBatchIDQueryMessageSync parameters;
    protected int count;

    /**
     * Gets the value of the parameters property.
     * 
     * @return
     *     possible object is
     *     {@link OutboundDeliveryByBatchIDQueryMessageSync }
     *     
     */
    public OutboundDeliveryByBatchIDQueryMessageSync getParameters() {
        return parameters;
    }

    /**
     * Sets the value of the parameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutboundDeliveryByBatchIDQueryMessageSync }
     *     
     */
    public void setParameters(OutboundDeliveryByBatchIDQueryMessageSync value) {
        this.parameters = value;
    }

    /**
     * Gets the value of the count property.
     * 
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets the value of the count property.
     * 
     */
    public void setCount(int value) {
        this.count = value;
    }

}
