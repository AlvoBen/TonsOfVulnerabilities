
package com.sap.persistence.monitors.ws.types.trace;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;
import com.sap.persistence.monitors.ws.types.common.TNodeSelection;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the com.sap.persistence.monitors.ws.types.trace package. 
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

    private final static QName _DetailedStatus_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "DetailedStatus");
    private final static QName _HighLevelSelection_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "HighLevelSelection");
    private final static QName _Status_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "Status");
    private final static QName _SwitchOnSelection_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "SwitchOnSelection");
    private final static QName _SwitchOffSelection_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "SwitchOffSelection");
    private final static QName _DetailedStatusSelection_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "DetailedStatusSelection");
    private final static QName _StatusSelection_QNAME = new QName("http://sap.com/persistence/monitors/ws/types/trace", "StatusSelection");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.sap.persistence.monitors.ws.types.trace
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link TNodeStatus }
     * 
     */
    public TNodeStatus createTNodeStatus() {
        return new TNodeStatus();
    }

    /**
     * Create an instance of {@link TNodeStatusProperty }
     * 
     */
    public TNodeStatusProperty createTNodeStatusProperty() {
        return new TNodeStatusProperty();
    }

    /**
     * Create an instance of {@link TStatusProperty }
     * 
     */
    public TStatusProperty createTStatusProperty() {
        return new TStatusProperty();
    }

    /**
     * Create an instance of {@link TDetailedStatusSelection }
     * 
     */
    public TDetailedStatusSelection createTDetailedStatusSelection() {
        return new TDetailedStatusSelection();
    }

    /**
     * Create an instance of {@link TStatus }
     * 
     */
    public TStatus createTStatus() {
        return new TStatus();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TStatusProperty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "DetailedStatus")
    public JAXBElement<TStatusProperty> createDetailedStatus(TStatusProperty value) {
        return new JAXBElement<TStatusProperty>(_DetailedStatus_QNAME, TStatusProperty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TNodeSelection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "HighLevelSelection")
    public JAXBElement<TNodeSelection> createHighLevelSelection(TNodeSelection value) {
        return new JAXBElement<TNodeSelection>(_HighLevelSelection_QNAME, TNodeSelection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "Status")
    public JAXBElement<TStatus> createStatus(TStatus value) {
        return new JAXBElement<TStatus>(_Status_QNAME, TStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TNodeSelection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "SwitchOnSelection")
    public JAXBElement<TNodeSelection> createSwitchOnSelection(TNodeSelection value) {
        return new JAXBElement<TNodeSelection>(_SwitchOnSelection_QNAME, TNodeSelection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TNodeSelection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "SwitchOffSelection")
    public JAXBElement<TNodeSelection> createSwitchOffSelection(TNodeSelection value) {
        return new JAXBElement<TNodeSelection>(_SwitchOffSelection_QNAME, TNodeSelection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TDetailedStatusSelection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "DetailedStatusSelection")
    public JAXBElement<TDetailedStatusSelection> createDetailedStatusSelection(TDetailedStatusSelection value) {
        return new JAXBElement<TDetailedStatusSelection>(_DetailedStatusSelection_QNAME, TDetailedStatusSelection.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TNodeSelection }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://sap.com/persistence/monitors/ws/types/trace", name = "StatusSelection")
    public JAXBElement<TNodeSelection> createStatusSelection(TNodeSelection value) {
        return new JAXBElement<TNodeSelection>(_StatusSelection_QNAME, TNodeSelection.class, null, value);
    }

}
