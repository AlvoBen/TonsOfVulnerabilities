
package com.sap.persistence.monitors.ws.types.common;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sap.persistence.monitors.ws.types.common package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _NodeSelection_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/common", "NodeSelection");
    private final static QName _ServiceInfo_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/common", "ServiceInfo");
    private final static QName _PingMBean_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/common", "PingMBean");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sap.persistence.monitors.ws.types.common
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TNode }
     * 
     */
    public TNode createTNode() {
        return new TNode();
    }

    /**
     * Create an instance of {@link TNodeSelection }
     * 
     */
    public TNodeSelection createTNodeSelection() {
        return new TNodeSelection();
    }

    /**
     * Create an instance of {@link TServiceInfo }
     * 
     */
    public TServiceInfo createTServiceInfo() {
        return new TServiceInfo();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TNodeSelection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/common", name = "NodeSelection")
    public JAXBElement<TNodeSelection> createNodeSelection(TNodeSelection value) {
        return new JAXBElement<TNodeSelection>(_NodeSelection_QNAME, TNodeSelection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TServiceInfo }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/common", name = "ServiceInfo")
    public JAXBElement<TServiceInfo> createServiceInfo(TServiceInfo value) {
        return new JAXBElement<TServiceInfo>(_ServiceInfo_QNAME, TServiceInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Boolean }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/common", name = "PingMBean")
    public JAXBElement<Boolean> createPingMBean(Boolean value) {
        return new JAXBElement<Boolean>(_PingMBean_QNAME, Boolean.class, null, value);
    }

}
