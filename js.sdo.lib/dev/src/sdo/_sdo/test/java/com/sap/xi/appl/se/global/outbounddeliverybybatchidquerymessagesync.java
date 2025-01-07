
package com.sap.xi.appl.se.global;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for OutboundDeliveryByBatchIDQueryMessage_sync complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="OutboundDeliveryByBatchIDQueryMessage_sync">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="OutboundDeliverySelectionByBatchID">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SelectionByOutboundDeliveryShipFromLocationInternalID" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LowerBoundaryLocationInternalID" type="{http://sap.com/xi/APPL/SE/Global}LocationInternalID"/>
 *                             &lt;element name="UpperBoundaryLocationInternalID" type="{http://sap.com/xi/APPL/SE/Global}LocationInternalID" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="OutboundDeliveryShipFromLocationStandardID" type="{http://sap.com/xi/APPL/SE/Global}LocationStandardID" maxOccurs="unbounded" minOccurs="0"/>
 *                   &lt;element name="SelectionByOutboundDeliveryItemProductInternalID" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LowerBoundaryProductInternalID" type="{http://sap.com/xi/APPL/SE/Global}ProductInternalID"/>
 *                             &lt;element name="UpperBoundaryProductInternalID" type="{http://sap.com/xi/APPL/SE/Global}ProductInternalID" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SelectionByOutboundDeliveryItemProductStandardID" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LowerBoundaryProductStandardID" type="{http://sap.com/xi/APPL/SE/Global}ProductStandardID"/>
 *                             &lt;element name="UpperBoundaryProductStandardID" type="{http://sap.com/xi/APPL/SE/Global}ProductStandardID" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="SelectionByOutboundDeliveryItemBatchID" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="LowerBoundaryBatchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="UpperBoundaryBatchID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OutboundDeliveryByBatchIDQueryMessage_sync", propOrder = {
    "outboundDeliverySelectionByBatchID"
})
public class OutboundDeliveryByBatchIDQueryMessageSync {

    @XmlElement(name = "OutboundDeliverySelectionByBatchID", required = true)
    protected OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID outboundDeliverySelectionByBatchID;

    /**
     * Gets the value of the outboundDeliverySelectionByBatchID property.
     * 
     * @return
     *     possible object is
     *     {@link OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID }
     *     
     */
    public OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID getOutboundDeliverySelectionByBatchID() {
        return outboundDeliverySelectionByBatchID;
    }

    /**
     * Sets the value of the outboundDeliverySelectionByBatchID property.
     * 
     * @param value
     *     allowed object is
     *     {@link OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID }
     *     
     */
    public void setOutboundDeliverySelectionByBatchID(OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID value) {
        this.outboundDeliverySelectionByBatchID = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="SelectionByOutboundDeliveryShipFromLocationInternalID" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="LowerBoundaryLocationInternalID" type="{http://sap.com/xi/APPL/SE/Global}LocationInternalID"/>
     *                   &lt;element name="UpperBoundaryLocationInternalID" type="{http://sap.com/xi/APPL/SE/Global}LocationInternalID" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="OutboundDeliveryShipFromLocationStandardID" type="{http://sap.com/xi/APPL/SE/Global}LocationStandardID" maxOccurs="unbounded" minOccurs="0"/>
     *         &lt;element name="SelectionByOutboundDeliveryItemProductInternalID" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="LowerBoundaryProductInternalID" type="{http://sap.com/xi/APPL/SE/Global}ProductInternalID"/>
     *                   &lt;element name="UpperBoundaryProductInternalID" type="{http://sap.com/xi/APPL/SE/Global}ProductInternalID" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SelectionByOutboundDeliveryItemProductStandardID" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="LowerBoundaryProductStandardID" type="{http://sap.com/xi/APPL/SE/Global}ProductStandardID"/>
     *                   &lt;element name="UpperBoundaryProductStandardID" type="{http://sap.com/xi/APPL/SE/Global}ProductStandardID" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="SelectionByOutboundDeliveryItemBatchID" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="LowerBoundaryBatchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="UpperBoundaryBatchID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "selectionByOutboundDeliveryShipFromLocationInternalID",
        "outboundDeliveryShipFromLocationStandardID",
        "selectionByOutboundDeliveryItemProductInternalID",
        "selectionByOutboundDeliveryItemProductStandardID",
        "selectionByOutboundDeliveryItemBatchID"
    })
    public static class OutboundDeliverySelectionByBatchID {

        @XmlElement(name = "SelectionByOutboundDeliveryShipFromLocationInternalID")
        protected List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryShipFromLocationInternalID> selectionByOutboundDeliveryShipFromLocationInternalID;
        @XmlElement(name = "OutboundDeliveryShipFromLocationStandardID")
        protected List<LocationStandardID> outboundDeliveryShipFromLocationStandardID;
        @XmlElement(name = "SelectionByOutboundDeliveryItemProductInternalID")
        protected List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductInternalID> selectionByOutboundDeliveryItemProductInternalID;
        @XmlElement(name = "SelectionByOutboundDeliveryItemProductStandardID")
        protected List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductStandardID> selectionByOutboundDeliveryItemProductStandardID;
        @XmlElement(name = "SelectionByOutboundDeliveryItemBatchID")
        protected List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemBatchID> selectionByOutboundDeliveryItemBatchID;

        /**
         * Gets the value of the selectionByOutboundDeliveryShipFromLocationInternalID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the selectionByOutboundDeliveryShipFromLocationInternalID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSelectionByOutboundDeliveryShipFromLocationInternalID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryShipFromLocationInternalID }
         * 
         * 
         */
        public List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryShipFromLocationInternalID> getSelectionByOutboundDeliveryShipFromLocationInternalID() {
            if (selectionByOutboundDeliveryShipFromLocationInternalID == null) {
                selectionByOutboundDeliveryShipFromLocationInternalID = new ArrayList<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryShipFromLocationInternalID>();
            }
            return this.selectionByOutboundDeliveryShipFromLocationInternalID;
        }

        /**
         * Gets the value of the outboundDeliveryShipFromLocationStandardID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the outboundDeliveryShipFromLocationStandardID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOutboundDeliveryShipFromLocationStandardID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link LocationStandardID }
         * 
         * 
         */
        public List<LocationStandardID> getOutboundDeliveryShipFromLocationStandardID() {
            if (outboundDeliveryShipFromLocationStandardID == null) {
                outboundDeliveryShipFromLocationStandardID = new ArrayList<LocationStandardID>();
            }
            return this.outboundDeliveryShipFromLocationStandardID;
        }

        /**
         * Gets the value of the selectionByOutboundDeliveryItemProductInternalID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the selectionByOutboundDeliveryItemProductInternalID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSelectionByOutboundDeliveryItemProductInternalID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductInternalID }
         * 
         * 
         */
        public List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductInternalID> getSelectionByOutboundDeliveryItemProductInternalID() {
            if (selectionByOutboundDeliveryItemProductInternalID == null) {
                selectionByOutboundDeliveryItemProductInternalID = new ArrayList<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductInternalID>();
            }
            return this.selectionByOutboundDeliveryItemProductInternalID;
        }

        /**
         * Gets the value of the selectionByOutboundDeliveryItemProductStandardID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the selectionByOutboundDeliveryItemProductStandardID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSelectionByOutboundDeliveryItemProductStandardID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductStandardID }
         * 
         * 
         */
        public List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductStandardID> getSelectionByOutboundDeliveryItemProductStandardID() {
            if (selectionByOutboundDeliveryItemProductStandardID == null) {
                selectionByOutboundDeliveryItemProductStandardID = new ArrayList<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemProductStandardID>();
            }
            return this.selectionByOutboundDeliveryItemProductStandardID;
        }

        /**
         * Gets the value of the selectionByOutboundDeliveryItemBatchID property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the selectionByOutboundDeliveryItemBatchID property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSelectionByOutboundDeliveryItemBatchID().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemBatchID }
         * 
         * 
         */
        public List<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemBatchID> getSelectionByOutboundDeliveryItemBatchID() {
            if (selectionByOutboundDeliveryItemBatchID == null) {
                selectionByOutboundDeliveryItemBatchID = new ArrayList<OutboundDeliveryByBatchIDQueryMessageSync.OutboundDeliverySelectionByBatchID.SelectionByOutboundDeliveryItemBatchID>();
            }
            return this.selectionByOutboundDeliveryItemBatchID;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="LowerBoundaryBatchID" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="UpperBoundaryBatchID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "inclusionExclusionCode",
            "intervalBoundaryTypeCode",
            "lowerBoundaryBatchID",
            "upperBoundaryBatchID"
        })
        public static class SelectionByOutboundDeliveryItemBatchID {

            @XmlElement(name = "InclusionExclusionCode", required = true)
            protected String inclusionExclusionCode;
            @XmlElement(name = "IntervalBoundaryTypeCode", required = true)
            protected String intervalBoundaryTypeCode;
            @XmlElement(name = "LowerBoundaryBatchID", required = true)
            protected String lowerBoundaryBatchID;
            @XmlElement(name = "UpperBoundaryBatchID")
            protected String upperBoundaryBatchID;

            /**
             * Gets the value of the inclusionExclusionCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInclusionExclusionCode() {
                return inclusionExclusionCode;
            }

            /**
             * Sets the value of the inclusionExclusionCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInclusionExclusionCode(String value) {
                this.inclusionExclusionCode = value;
            }

            /**
             * Gets the value of the intervalBoundaryTypeCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIntervalBoundaryTypeCode() {
                return intervalBoundaryTypeCode;
            }

            /**
             * Sets the value of the intervalBoundaryTypeCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIntervalBoundaryTypeCode(String value) {
                this.intervalBoundaryTypeCode = value;
            }

            /**
             * Gets the value of the lowerBoundaryBatchID property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getLowerBoundaryBatchID() {
                return lowerBoundaryBatchID;
            }

            /**
             * Sets the value of the lowerBoundaryBatchID property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setLowerBoundaryBatchID(String value) {
                this.lowerBoundaryBatchID = value;
            }

            /**
             * Gets the value of the upperBoundaryBatchID property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getUpperBoundaryBatchID() {
                return upperBoundaryBatchID;
            }

            /**
             * Sets the value of the upperBoundaryBatchID property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setUpperBoundaryBatchID(String value) {
                this.upperBoundaryBatchID = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="LowerBoundaryProductInternalID" type="{http://sap.com/xi/APPL/SE/Global}ProductInternalID"/>
         *         &lt;element name="UpperBoundaryProductInternalID" type="{http://sap.com/xi/APPL/SE/Global}ProductInternalID" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "inclusionExclusionCode",
            "intervalBoundaryTypeCode",
            "lowerBoundaryProductInternalID",
            "upperBoundaryProductInternalID"
        })
        public static class SelectionByOutboundDeliveryItemProductInternalID {

            @XmlElement(name = "InclusionExclusionCode", required = true)
            protected String inclusionExclusionCode;
            @XmlElement(name = "IntervalBoundaryTypeCode", required = true)
            protected String intervalBoundaryTypeCode;
            @XmlElement(name = "LowerBoundaryProductInternalID", required = true)
            protected ProductInternalID lowerBoundaryProductInternalID;
            @XmlElement(name = "UpperBoundaryProductInternalID")
            protected ProductInternalID upperBoundaryProductInternalID;

            /**
             * Gets the value of the inclusionExclusionCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInclusionExclusionCode() {
                return inclusionExclusionCode;
            }

            /**
             * Sets the value of the inclusionExclusionCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInclusionExclusionCode(String value) {
                this.inclusionExclusionCode = value;
            }

            /**
             * Gets the value of the intervalBoundaryTypeCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIntervalBoundaryTypeCode() {
                return intervalBoundaryTypeCode;
            }

            /**
             * Sets the value of the intervalBoundaryTypeCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIntervalBoundaryTypeCode(String value) {
                this.intervalBoundaryTypeCode = value;
            }

            /**
             * Gets the value of the lowerBoundaryProductInternalID property.
             * 
             * @return
             *     possible object is
             *     {@link ProductInternalID }
             *     
             */
            public ProductInternalID getLowerBoundaryProductInternalID() {
                return lowerBoundaryProductInternalID;
            }

            /**
             * Sets the value of the lowerBoundaryProductInternalID property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProductInternalID }
             *     
             */
            public void setLowerBoundaryProductInternalID(ProductInternalID value) {
                this.lowerBoundaryProductInternalID = value;
            }

            /**
             * Gets the value of the upperBoundaryProductInternalID property.
             * 
             * @return
             *     possible object is
             *     {@link ProductInternalID }
             *     
             */
            public ProductInternalID getUpperBoundaryProductInternalID() {
                return upperBoundaryProductInternalID;
            }

            /**
             * Sets the value of the upperBoundaryProductInternalID property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProductInternalID }
             *     
             */
            public void setUpperBoundaryProductInternalID(ProductInternalID value) {
                this.upperBoundaryProductInternalID = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="LowerBoundaryProductStandardID" type="{http://sap.com/xi/APPL/SE/Global}ProductStandardID"/>
         *         &lt;element name="UpperBoundaryProductStandardID" type="{http://sap.com/xi/APPL/SE/Global}ProductStandardID" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "inclusionExclusionCode",
            "intervalBoundaryTypeCode",
            "lowerBoundaryProductStandardID",
            "upperBoundaryProductStandardID"
        })
        public static class SelectionByOutboundDeliveryItemProductStandardID {

            @XmlElement(name = "InclusionExclusionCode", required = true)
            protected String inclusionExclusionCode;
            @XmlElement(name = "IntervalBoundaryTypeCode", required = true)
            protected String intervalBoundaryTypeCode;
            @XmlElement(name = "LowerBoundaryProductStandardID", required = true)
            protected ProductStandardID lowerBoundaryProductStandardID;
            @XmlElement(name = "UpperBoundaryProductStandardID")
            protected ProductStandardID upperBoundaryProductStandardID;

            /**
             * Gets the value of the inclusionExclusionCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInclusionExclusionCode() {
                return inclusionExclusionCode;
            }

            /**
             * Sets the value of the inclusionExclusionCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInclusionExclusionCode(String value) {
                this.inclusionExclusionCode = value;
            }

            /**
             * Gets the value of the intervalBoundaryTypeCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIntervalBoundaryTypeCode() {
                return intervalBoundaryTypeCode;
            }

            /**
             * Sets the value of the intervalBoundaryTypeCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIntervalBoundaryTypeCode(String value) {
                this.intervalBoundaryTypeCode = value;
            }

            /**
             * Gets the value of the lowerBoundaryProductStandardID property.
             * 
             * @return
             *     possible object is
             *     {@link ProductStandardID }
             *     
             */
            public ProductStandardID getLowerBoundaryProductStandardID() {
                return lowerBoundaryProductStandardID;
            }

            /**
             * Sets the value of the lowerBoundaryProductStandardID property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProductStandardID }
             *     
             */
            public void setLowerBoundaryProductStandardID(ProductStandardID value) {
                this.lowerBoundaryProductStandardID = value;
            }

            /**
             * Gets the value of the upperBoundaryProductStandardID property.
             * 
             * @return
             *     possible object is
             *     {@link ProductStandardID }
             *     
             */
            public ProductStandardID getUpperBoundaryProductStandardID() {
                return upperBoundaryProductStandardID;
            }

            /**
             * Sets the value of the upperBoundaryProductStandardID property.
             * 
             * @param value
             *     allowed object is
             *     {@link ProductStandardID }
             *     
             */
            public void setUpperBoundaryProductStandardID(ProductStandardID value) {
                this.upperBoundaryProductStandardID = value;
            }

        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="InclusionExclusionCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="IntervalBoundaryTypeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="LowerBoundaryLocationInternalID" type="{http://sap.com/xi/APPL/SE/Global}LocationInternalID"/>
         *         &lt;element name="UpperBoundaryLocationInternalID" type="{http://sap.com/xi/APPL/SE/Global}LocationInternalID" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "inclusionExclusionCode",
            "intervalBoundaryTypeCode",
            "lowerBoundaryLocationInternalID",
            "upperBoundaryLocationInternalID"
        })
        public static class SelectionByOutboundDeliveryShipFromLocationInternalID {

            @XmlElement(name = "InclusionExclusionCode", required = true)
            protected String inclusionExclusionCode;
            @XmlElement(name = "IntervalBoundaryTypeCode", required = true)
            protected String intervalBoundaryTypeCode;
            @XmlElement(name = "LowerBoundaryLocationInternalID", required = true)
            protected LocationInternalID lowerBoundaryLocationInternalID;
            @XmlElement(name = "UpperBoundaryLocationInternalID")
            protected LocationInternalID upperBoundaryLocationInternalID;

            /**
             * Gets the value of the inclusionExclusionCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getInclusionExclusionCode() {
                return inclusionExclusionCode;
            }

            /**
             * Sets the value of the inclusionExclusionCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setInclusionExclusionCode(String value) {
                this.inclusionExclusionCode = value;
            }

            /**
             * Gets the value of the intervalBoundaryTypeCode property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getIntervalBoundaryTypeCode() {
                return intervalBoundaryTypeCode;
            }

            /**
             * Sets the value of the intervalBoundaryTypeCode property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setIntervalBoundaryTypeCode(String value) {
                this.intervalBoundaryTypeCode = value;
            }

            /**
             * Gets the value of the lowerBoundaryLocationInternalID property.
             * 
             * @return
             *     possible object is
             *     {@link LocationInternalID }
             *     
             */
            public LocationInternalID getLowerBoundaryLocationInternalID() {
                return lowerBoundaryLocationInternalID;
            }

            /**
             * Sets the value of the lowerBoundaryLocationInternalID property.
             * 
             * @param value
             *     allowed object is
             *     {@link LocationInternalID }
             *     
             */
            public void setLowerBoundaryLocationInternalID(LocationInternalID value) {
                this.lowerBoundaryLocationInternalID = value;
            }

            /**
             * Gets the value of the upperBoundaryLocationInternalID property.
             * 
             * @return
             *     possible object is
             *     {@link LocationInternalID }
             *     
             */
            public LocationInternalID getUpperBoundaryLocationInternalID() {
                return upperBoundaryLocationInternalID;
            }

            /**
             * Sets the value of the upperBoundaryLocationInternalID property.
             * 
             * @param value
             *     allowed object is
             *     {@link LocationInternalID }
             *     
             */
            public void setUpperBoundaryLocationInternalID(LocationInternalID value) {
                this.upperBoundaryLocationInternalID = value;
            }

        }

    }

}
