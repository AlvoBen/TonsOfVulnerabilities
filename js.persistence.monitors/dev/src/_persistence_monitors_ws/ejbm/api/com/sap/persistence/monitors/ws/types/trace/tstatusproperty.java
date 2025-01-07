
package com.sap.persistence.monitors.ws.types.trace;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TStatusProperty complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TStatusProperty">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="StatusPropertyList" type="{http://sap.com/persistence/monitors/ws/types/trace}TNodeStatusProperty" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TStatusProperty", propOrder = {
    "statusPropertyList"
})
public class TStatusProperty {

    @XmlElement(name = "StatusPropertyList", required = true)
    protected List<TNodeStatusProperty> statusPropertyList;

    /**
     * Gets the value of the statusPropertyList property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the statusPropertyList property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getStatusPropertyList().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TNodeStatusProperty }
     * 
     * 
     */
    public List<TNodeStatusProperty> getStatusPropertyList() {
        if (statusPropertyList == null) {
            statusPropertyList = new ArrayList<TNodeStatusProperty>();
        }
        return this.statusPropertyList;
    }

}
