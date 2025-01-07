
package com.sap.persistence.monitors.ws.types.trace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.sap.persistence.monitors.ws.types.common.TNodeSelection;


/**
 * <p>Java class for TDetailedStatusSelection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TDetailedStatusSelection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="nodeSelection" type="{http://sap.com/persistence/monitors/ws/types/common}TNodeSelection"/>
 *         &lt;element name="property" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TDetailedStatusSelection", propOrder = {
    "nodeSelection",
    "property"
})
public class TDetailedStatusSelection {

    @XmlElement(required = true)
    protected TNodeSelection nodeSelection;
    @XmlElement(required = true)
    protected String property;

    /**
     * Gets the value of the nodeSelection property.
     * 
     * @return
     *     possible object is
     *     {@link TNodeSelection }
     *     
     */
    public TNodeSelection getNodeSelection() {
        return nodeSelection;
    }

    /**
     * Sets the value of the nodeSelection property.
     * 
     * @param value
     *     allowed object is
     *     {@link TNodeSelection }
     *     
     */
    public void setNodeSelection(TNodeSelection value) {
        this.nodeSelection = value;
    }

    /**
     * Gets the value of the property property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProperty() {
        return property;
    }

    /**
     * Sets the value of the property property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProperty(String value) {
        this.property = value;
    }

}
