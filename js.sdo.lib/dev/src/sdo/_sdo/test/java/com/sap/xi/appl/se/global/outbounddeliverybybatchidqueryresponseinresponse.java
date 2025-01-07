
package com.sap.xi.appl.se.global;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for outboundDeliveryByBatchIDQueryResponseInResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="outboundDeliveryByBatchIDQueryResponseInResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="return" type="{http://sap.com/xi/APPL/SE/Global}OutboundDeliveryByBatchIDQueryMessage_sync" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "outboundDeliveryByBatchIDQueryResponseInResponse", namespace = "http://sap.com/xi/appl/se/global/", propOrder = {
    "_return"
})
public class OutboundDeliveryByBatchIDQueryResponseInResponse {

    @XmlElement(name = "return")
    protected OutboundDeliveryByBatchIDQueryMessageSync _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link OutboundDeliveryByBatchIDQueryMessageSync }
     *     
     */
    public OutboundDeliveryByBatchIDQueryMessageSync getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutboundDeliveryByBatchIDQueryMessageSync }
     *     
     */
    public void setReturn(OutboundDeliveryByBatchIDQueryMessageSync value) {
        this._return = value;
    }

}
