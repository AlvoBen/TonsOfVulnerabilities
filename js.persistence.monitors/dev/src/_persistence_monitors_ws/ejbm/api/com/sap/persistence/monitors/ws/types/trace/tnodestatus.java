
package com.sap.persistence.monitors.ws.types.trace;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.sap.persistence.monitors.sql.trace.NodeStatus;


/**
 * <p>Java class for TNodeStatus complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TNodeStatus">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="node" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="traceId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="isOn" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TNodeStatus", propOrder = {
    "node",
    "traceId",
    "isOn"
})
public class TNodeStatus {

    @XmlElement(required = true)
    protected String node;
    @XmlElement(required = true)
    protected String traceId;
    protected boolean isOn;

    /**
     * Gets the value of the node property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNode() {
        return node;
    }

    /**
     * Sets the value of the node property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNode(String value) {
        this.node = value;
    }

    /**
     * Gets the value of the traceId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTraceId() {
        return traceId;
    }

    /**
     * Sets the value of the traceId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTraceId(String value) {
        this.traceId = value;
    }

    /**
     * Gets the value of the isOn property.
     * 
     */
    public boolean isIsOn() {
        return isOn;
    }

    /**
     * Sets the value of the isOn property.
     * 
     */
    public void setIsOn(boolean value) {
        this.isOn = value;
    }
    
    public TNodeStatus(){
    	node = "";
    	traceId = "";
    	isOn = false;;
    	
    }
    
    public TNodeStatus(String jmxNode, NodeStatus jmxStatus){
    	node = jmxNode;
    	traceId = checkEntry(jmxStatus.getCurrentPrefix(), "");
    	isOn = jmxStatus.isOn();
    	
    }
    
    private String checkEntry(String input, String defVal){
    	String retour = defVal;
    	
    	if (input != null){
    		retour = input;
    	}
    	
    	return retour;
    }


}
