
package com.sap.persistence.monitors.ws.types.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TNodeSelection complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TNodeSelection">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ignoreNodeFilter" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="NodeFilter" type="{http://sap.com/persistence/monitors/ws/types/common}TNode" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TNodeSelection", propOrder = {
    "ignoreNodeFilter",
    "nodeFilter"
})
public class TNodeSelection {

    protected boolean ignoreNodeFilter;
    @XmlElement(name = "NodeFilter", required = true)
    protected List<TNode> nodeFilter;

    /**
     * Gets the value of the ignoreNodeFilter property.
     * 
     */
    public boolean isIgnoreNodeFilter() {
        return ignoreNodeFilter;
    }

    /**
     * Sets the value of the ignoreNodeFilter property.
     * 
     */
    public void setIgnoreNodeFilter(boolean value) {
        this.ignoreNodeFilter = value;
    }

    /**
     * Gets the value of the nodeFilter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeFilter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeFilter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TNode }
     * 
     * 
     */
    public List<TNode> getNodeFilter() {
        if (nodeFilter == null) {
            nodeFilter = new ArrayList<TNode>();
        }
        return this.nodeFilter;
    }
    
    public ArrayList<String> getNodeList(){
    	ArrayList<String> ar = new ArrayList<String>();
    	if (nodeFilter != null) {
           Iterator<TNode> it = nodeFilter.iterator();
           while (it.hasNext()){
        	   ar.add(it.next().node);
           }
    	}
        	   
        
        return ar;
    }

}
