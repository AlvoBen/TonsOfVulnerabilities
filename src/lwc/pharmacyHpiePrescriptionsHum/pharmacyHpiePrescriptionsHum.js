/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                   user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
* Atul Patil                    09/16/2023                  T1PRJ0870026 -MF27408 SF-TECH DF - 7995 - Lightning - Prescriptions data is not loading on Mail Order Pharmacy page
* Isaac Chung                    12/12/2023                 US 5107579 Mail Order Management; Pharmacy- Guided Flow- Inactivate Rx- Implementation (Lightning)
* vishal Shinde                   02/29/2024                  US - 5142800- Mail Order Management - Pharmacy - "Prescriptions & Order Summary" tab - Prescriptions â€“ Create Order
* Jonathan Dickinson             02/29/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
******************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import { getPickListValues } from 'c/crmUtilityHum';
import { getModel } from './layoutConfig';
import { MessageContext } from 'lightning/messageService';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import noPresData from '@salesforce/label/c.PHARMACY_PRESCRIPTION_NODATA';
import prescriptionServiceError from '@salesforce/label/c.PHARMACY_PRESCRIPTION_ERROR';
const CARDS_PER_ROW = 4;
const INITIAL_LOAD_CARDS = 8;
const PRIOR_MONTH = 18;

export default class PharmacyHpiePrescriptionsHum extends LightningElement {

    @api enterpriseId;
    @api userId;
    @api recordId;
    @api organization;
    @api preferenceDetails;
    @api demographicsDetails;
    @api financeDetails; 
    @api accData;  
    @track sSortBy = getModel('sortby');
    @track serveresult; 
    @track filterStatusValues = {};
    @track isCardClick = false;
    @track selectedCardIndex;
    @track isMorePrescription = true;
    @track filterPrescriptionObj;
    @track filterObj = {};
    @track pillFilterValues = [];
    @track filterAvailablePrescription;
    @track iconsModel = getModel("icons");
    @track totalCount = 0;
    @track filteredCount = 0;
    @track totalfilteredresults = [];
    @track layoutsection;
    @track isInActiveSelected = false;
    @track loaded = false;
    @track createOrderDisable = true;
    @track addedprescriptions = [];
    @track isCreateOrderClicked = false;
    @track dataFound = false;
    @track serviceerror = false;
    @track keyword = '';
    @track showAddressDescription = false;
    @track totalPrescriptions = [];
    @track totalActivePrescriptions = [];
    @track totalInactivePrescriptions = [];
    @track totalActiveCount;
    @track filteredActiveCount;
    @track totalInactiveCount;
    @track filteredInactiveCount;
    @track finalPrescriptions = [];
    @track finalFilteredPrescription = [];
    @track showMoreDisabled = true;
    @track inactivateRxRequest = false
    @track flag= false;
    @track prescArray =[];
    @track checkOk= false;
    @track isMemberConsentRequired = false;

    @wire(MessageContext)
    messageContext;

    labels = {
        noPresData,
        prescriptionServiceError
    }




    connectedCallback() {
        this.processPreferenceData();
    }

    @api setPreferenceDetails(data) {
        this.preferenceDetails = data;
        this.processPreferenceData();
    }

    @api setDemographicsDetails(data) {
        this.demographicsDetails = data;
    }

    @api setFinanceDetails(data) {
        this.financeDetails = data;
    }

    processPreferenceData() {
        this.isMemberConsentRequired = this.preferenceDetails?.preference?.consents?.memberConsentRequired;
        this.passDataToChildComponents([{
            name: 'cap',
            data: 'preference'
        }
        ])
    }

    passDataToChildComponents(components) {
        if (components && Array.isArray(components) && components?.length > 0) {
            components.forEach(k => {
                switch (k?.name?.toLowerCase()) {
                    case 'cap':
                        switch (k?.data?.toLowerCase()) {
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-captype-hum') != null) {
                                    this.template.querySelector('c-pharmacy-captype-hum').setPreferenceDetails(this.preferenceDetails);
                                }
                                break;
                        }
                        break;
                    case 'memberconsent':
                        switch (k?.data?.toLowerCase()) {
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-member-consent-hum') != null) {
                                    this.template.querySelector('c-pharmacy-member-consent-hum').setPreferenceDetails(this.preferenceDetails);
                                }
                                break;
                        }
                        break;
                }
            })
        }
    }


    @api
    setPrescriptions(data) {
        this.serviceerror = false;
        this.serviceerror = false;
        if (data && Object.keys(data)?.length > 0) {
            this.totalPrescriptions = data;
            this.prepareMemberPrescriptionArray(this.filterPrescriptionObj);
            this.setData();
        }
        this.loaded = true;
    }

    setData() {
        if (this.totalPrescriptions && this.totalPrescriptions?.length > 0) {
            this.removeInActiveStatus();
            this.filterStatusValues = getPickListValues(['status'], this.totalPrescriptions);
        }
        this.totalActiveCount = this.totalActivePrescriptions?.length ?? 0;
        this.filteredActiveCount = this.filterAvailablePrescription?.length ?? 0;
        this.createFinalPrscriptionList(this.totalActivePrescriptions);
        this.displayData();
    }

    displayData() {
        this.dataFound = this.finalPrescriptions?.length > 0 ? true : false;
        this.totalCount = this.finalPrescriptions?.length ?? 0;
        this.filterDetails();
    }

    filterDetails() {
        this.totalCount = this.finalPrescriptions?.length ?? 0;
        if (this.totalCount > INITIAL_LOAD_CARDS) {
            this.finalFilteredPrescription = this.finalPrescriptions.slice(0, INITIAL_LOAD_CARDS);
            this.filteredCount = INITIAL_LOAD_CARDS;
            this.showMoreDisabled = false;
        }
        else {
            this.finalFilteredPrescription = this.finalPrescriptions;
            this.filteredCount = this.totalCount;
            this.isMorePrescription = true;
            this.showMoreDisabled = true;
        }
    }

    refreshData() {
        this.loaded = false;
        this.dispatchEvent(new CustomEvent('refreshpresdata'));
    }

    @api
    displayError() {
        this.loaded = true;
        this.serviceerror = true;
    }

    @api
    displayNoPrescriptions() {
        this.loaded = true;
        this.serviceerror = false;
        this.dataFound = false;
    }

    goToSummary() { 
        this.isCreateOrderClicked = false; 
        this.addedprescriptions = [];
        this.addCartPrescriptionList = [];
        this.finalPrescriptions = this.finalPrescriptions.map(item => ({
            ...item,
            orderEligible: item?.status?.description?.toUpperCase()?.includes('INACTIVE') ||
                item?.status?.description?.toUpperCase()?.includes('WORKRX')  || (item.isReNewRx === false && item.isRefillable === false)? false : true,
                 addedToCart: false
        }));
        this.setData();
        this.createOrderDisable = true;
    }

    handleCreateOrderClick() {
        this.isCreateOrderClicked = true;
        if (this.template.querySelector('c-pharmacy-hpie-create-order-hum') != null) {
            this.template.querySelector('c-pharmacy-hpie-create-order-hum').pharmacydata();
        }
    }

    isEmptyObj(obj) {
        return obj && Object.keys(obj)?.length === 0 ? true : false;
    }

    getSortedValue(event) {
        if (this.filterObj.hasOwnProperty(event?.target?.name)) {
            delete this.filterObj[event?.target?.name];
            this.filterObj[event?.target?.name] = event?.detail?.value ?? null
        } else {
            this.filterObj[event?.target?.name] = event?.detail?.value ?? null;
        }
        this.generateFilterData();
    }

    handleKeywordSearch(event) {
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= 3) {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
                this.filterObj[name] = value ?? null
            } else {
                this.filterObj[name] = value ?? null;
            }
            this.generateFilterData();
        } else {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
            }
            this.generateFilterData();
        }
    }

    performfilter(data) {
        let tmp = [];
        if (data && Array.isArray(data) && data?.length > 0) {
            for (const [key, value] of Object.entries(this.filterObj)) {
                switch (key) {
                    case "searchByWord":
                        if (tmp && tmp?.length > 0) {
                            let cloneData = tmp;
                            tmp = [];
                            cloneData?.forEach(a => {
                                Object.values(a)?.forEach(h => {
                                    if (null != h && h?.toString()?.toLowerCase().includes(this.keyword?.toLowerCase())
                                        && !tmp.includes(a)) {
                                        tmp.push(a);
                                    }
                                })
                            });
                        } else {
                            data?.forEach(a => {
                                Object.values(a)?.forEach(h => {
                                    if (null != h && typeof (h) !== 'object' && h.toString()?.toLowerCase().includes(this.keyword?.toLowerCase())
                                        && !tmp.includes(a)) {
                                        tmp.push(a);
                                    }
                                })
                            });
                        }
                        break;
                    case "status":
                        tmp = tmp?.length > 0 ? tmp.filter(k => this.filterObj['status'].includes(k?.status)) :
                            data.filter(k => this.filterObj['status'].includes(k?.status));
                        break;
                    case "sSortBy":
                        let fieldName = this.filterObj['sSortBy'];
                        tmp = fieldName === 'expirationDate' ? data.sort(function (a, b) {
                            return new Date(a[fieldName]) > new Date(b[fieldName]) ? -1 : 1;
                        }) : data.sort(function (a, b) {
                            return a[fieldName] > b[fieldName] ? -1 : 1;
                        })

                }
            }
        }
        this.createFinalPrscriptionList(tmp);
        tmp = [];
    }

    addPillValues() {
        this.pillFilterValues = [];
        for (const [key, value] of Object.entries(this.filterObj)) {
            if (value && Array.isArray(value) && value?.length > 0) {
                value.forEach(k => {
                    this.pillFilterValues.push({
                        key: key,
                        value: k
                    });
                })
            } else {
                this.pillFilterValues.push({
                    key: key,
                    value: key === 'sSortBy' ? this.sSortBy.find(k => k?.value === value)?.label : value
                })
            }
        }
    }

    generateFilterData() {
        if (this.filterObj && Object.keys(this.filterObj)?.length > 0) {
            this.addPillValues();
            this.performfilter(this.filterObj.hasOwnProperty('status') ? this.filterObj['status']?.includes('Inactive')
                ? this.totalActivePrescriptions.concat(this.totalInactivePrescriptions) : this.totalActivePrescriptions
                : this.totalActivePrescriptions);
        } else {
            this.pillFilterValues = [];
            this.createFinalPrscriptionList(this.totalActivePrescriptions);
        }
    }

    handlePillRemove(event) {
        this.pillFilterValues = [];
        Object.keys(this.filterObj).forEach(t => {
            if (t === event.target.dataset.key) {
                switch (event?.target?.dataset?.key) {
                    case "searchByWord":
                        delete this.filterObj['searchByWord'];
                        this.keyword = '';
                        if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                            this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                        }
                        break;
                    case "sSortBy":
                        delete this.filterObj['sSortBy'];
                        break;
                    case "status":
                        if (this.filterObj['status']?.length > 1) {
                            this.filterObj['status'] = this.filterObj['status']?.filter(t => t !== event?.target.dataset?.value);
                        } else {
                            delete this.filterObj['status'];
                        }
                        break;
                }
            }
        });
        this.generateFilterData();
        if (this.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
            let payload = { keyname: event.target.dataset.key, value: event.target.dataset.value };
            if (event.target.dataset.value == "Inactive") { this.isInActiveSelected = false; }
            this.template.querySelectorAll('c-generic-multiselect-picklist-hum').forEach(k => {
                k.clearSelection(payload);
            })
        }
        let fields = this.template.querySelectorAll('lightning-combobox');
        fields.forEach(function (item) {
            item.value = '';
        });
    }


    handlePicklistFilter(event) {
        if (event && event?.detail && event?.detail?.selectedvalues && event?.detail?.keyname
            && Array.isArray(event?.detail?.selectedvalues) && event?.detail?.selectedvalues?.length > 0) {
            if (this.filterObj.hasOwnProperty(event?.detail?.keyname)) {
                delete this.filterObj[event?.detail?.keyname];
                this.filterObj[event?.detail?.keyname] = event?.detail?.selectedvalues ?? null
            } else {
                this.filterObj[event?.detail?.keyname] = event?.detail?.selectedvalues ?? null;
            }
            this.generateFilterData();
        }
    }

    handleAutoRefill(event) {
        if (event && event?.detail) {
            this.displayDetailSection(event);
        }
    }

    displayDetailSection(event) {
        this.isCardClick = event?.detail?.isCardClick ?? false;
        this.selectedCardIndex = event?.detail?.selectedCard ?? null;
        let layoutid = event?.detail?.selectedKey ?? null;
        let selectedRXNumber = event?.detail?.prescriptionKey ?? '';
        let layoutsectionid = Math.floor(layoutid / CARDS_PER_ROW + 1);
        this.layoutsection = this.template.querySelector('[data-id=detail' + Math.round(layoutsectionid) + ']');
        this.finalPrescriptions = this.finalPrescriptions.map((item) => ({
            ...item,
            autoRefillEnrolled: item?.key === event?.detail?.prescriptionKey ? event?.detail?.autoRefill === true ? 'Yes' : 'No' : item?.autoRefillEnrolled
        }));
        this.finalFilteredPrescription = this.finalFilteredPrescription.map((item) => ({
            ...item,
            autoRefillEnrolled: item?.key === event?.detail?.prescriptionKey ? event?.detail?.autoRefill === true ? 'Yes' : 'No' : item?.autoRefillEnrolled
        }));
        if (this.layoutsection) {
            this.template.querySelectorAll(".detailsection").forEach(k => {
                k.style.display = 'none';
            })
            if (this.template.querySelector("c-pharmacy-hpie-prescription-details-hum") != null) {
                this.template.querySelectorAll("c-pharmacy-hpie-prescription-details-hum").forEach(t => {
                    t.setPrescriptionDetails(this.finalPrescriptions[layoutid] ?? null);
                })
            }
            if (this.template.querySelector("c-pharmacy-hpie-prescription-card-hum") != null) {
                this.template.querySelectorAll("c-pharmacy-hpie-prescription-card-hum").forEach(t => {
                    t.displayBorder(selectedRXNumber);
                })
            }
            if (this.template.querySelector("c-pharmacy-hpie-prescription-details-hum") != null && event?.detail?.autoRefill) {
                this.template.querySelectorAll("c-pharmacy-hpie-prescription-details-hum").forEach(t => {
                    t.setPrescriptionAutoRefillDetails(event?.detail?.autoRefill ?? '');
                })
            }
            this.layoutsection.style.display = "block";
        }
    }



    handleAddCart(event) { 
        if (event) {
            let selPrescriptioncolor = event.detail.prescriptioncolor;
            let selrxnumber = event.detail.prescriptionnumber; 
            let newprescription = event.detail.item;
            const selPrescription = this.finalPrescriptions.find(k => k?.key === selrxnumber);
            if (selPrescription && !this.addedprescriptions.includes(selPrescription)) {
                this.addedprescriptions.push(selPrescription);
            }
            this.finalPrescriptions = this.finalPrescriptions.map((item) => ({
                ...item,
                addedToCart: item?.key === selrxnumber ? true : item?.addedToCart ?? false
            }))
            this.passDataToChildComponents([{
                name: 'createOrder',
                data: 'addedprescriptions'
            }])
            switch (selPrescriptioncolor) { 
                case "green": {
                    this.finalPrescriptions = this.finalPrescriptions.map((item) => ({ 
                        ...item,
                        orderEligible: item?.icon?.icontype === 'blue' || item?.status?.toUpperCase() === 'WORKRX'
                            || item?.status?.toUpperCase() === 'INACTIVE' || (item.isReNewRx === false && item.isRefillable === false) ? false : true
                    }))
                    this.createOrderDisable = false;
                    break;
                }
                case "blue": {
                    this.finalPrescriptions.forEach(k => {  
                        if (k.icon.icontype === 'green'
                            || k?.status?.toString().toUpperCase() === "WORKRX"
                            || k?.status?.toString().toUpperCase() === 'INACTIVE' || (k.isReNewRx === false && k.isRefillable === false)) {
                            k.orderEligible = false;
                        }
                        if (k.icon.icontype === 'black' && k?.status?.toString().toUpperCase() != "WORKRX" && (k.isReNewRx === true || k.isRefillable === true)) {
                            k.orderEligible = true;
                        }
                    })
                    this.createOrderDisable = false;
                    break;
                }
                case "black": {
                    this.finalPrescriptions.forEach(k => {
                        if (k?.status?.toString().toUpperCase() === "WORKRX" ||
                            k?.status?.toString().toUpperCase() === 'INACTIVE' || (k.isReNewRx === false && k.isRefillable === false)) {
                            k.orderEligible = false;
                        }
                        if ((k?.icon?.icontype === 'green'
                            || k?.icon?.icontype === 'blue')
                            && k?.toString().toUpperCase() != "WORKRX"
                            &&(k.isReNewRx === true || k.isRefillable === true)) {
                            k.orderEligible = true;
                        }
                    })
                    this.createOrderDisable = false;
                    break;
                }
                default: {
                    this.finalPrescriptions.forEach(k => {
                        if (k?.status?.toString().toUpperCase() === 'INACTIVE'
                            || k?.status?.toString().toUpperCase() === "WORKRX" || (k.isReNewRx === false && k.isRefillable === false)) {
                            k.orderEligible = false;
                        }
                    })
                    break;
                }
            }
        }
    }

    handleCardClickSelect(event) {
        this.handleCardClick(event)
            .then(() => this.layoutsection.scrollIntoView())
    }

    handleCardClick(event) {
        return new Promise((resolve) => {
            this.isCardClick = event?.detail?.isCardClick ?? false;
            this.selectedCardIndex = event?.detail?.selectedCard ?? null;
            let layoutid = event?.detail?.selectedKey ?? null;
            let selectedRXNumber = event?.detail?.prescriptionKey ?? '';
            let layoutsectionid = Math.floor(layoutid / CARDS_PER_ROW + 1);
            this.layoutsection = this.template.querySelector('[data-id=detail' + Math.round(layoutsectionid) + ']');
            if (this.layoutsection) {
                this.template.querySelectorAll(".detailsection").forEach(k => {
                    k.style.display = 'none';
                })
                if (this.template.querySelector("c-pharmacy-hpie-prescription-details-hum") != null) {
                    this.template.querySelectorAll("c-pharmacy-hpie-prescription-details-hum").forEach(t => {
                        t.setPrescriptionDetails(this.finalPrescriptions[layoutid] ?? null);
                    })
                }
                if (this.template.querySelector("c-pharmacy-hpie-prescription-card-hum") != null) {
                    this.template.querySelectorAll("c-pharmacy-hpie-prescription-card-hum").forEach(t => {
                        t.displayBorder(selectedRXNumber);
                    })
                }
                this.layoutsection.style.display = "block";
            }
            resolve('');
        });
    }

    handleCloseClick(event) {
        let layoutid = event.detail.selectedKey;
        let layoutsectionid = Math.floor(layoutid / CARDS_PER_ROW + 1);
        this.layoutsection = this.template.querySelector('[data-id=detail' + Math.round(layoutsectionid) + ']');
        if (this.layoutsection != null && this.layoutsection != undefined) {
            this.layoutsection.style.display = "none";
        }
    }

    clearFilter() {
        this.pillFilterValues = [];
        let fields = this.template.querySelectorAll('lightning-combobox');
        let inputfields = this.template.querySelectorAll('[data-name="searchByWord"');
        inputfields.forEach(function (item) {
            item.value = '';
        })
        fields.forEach(function (item) {
            item.value = '';
        })
        if (this.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
            this.template.querySelectorAll('c-generic-multiselect-picklist-hum').forEach(k => {
                k.clearDropDowns();
            })
        }
        this.filterObj = {};
        this.createFinalPrscriptionList(this.filterAvailablePrescription);
    }



    removeInActiveStatus() {
        this.totalActivePrescriptions = this.totalPrescriptions.filter((item) => !item?.status?.toUpperCase().includes('INACTIVE'));
        this.totalInactivePrescriptions = this.totalPrescriptions.filter((item) => item?.status?.toUpperCase()?.includes('INACTIVE'));
    }

    showMoreCards() {
        if ((this.filteredCount + INITIAL_LOAD_CARDS) < this.totalCount) {
            this.finalFilteredPrescription = this.finalPrescriptions.slice(0,
                (this.filteredCount + INITIAL_LOAD_CARDS))
            this.filteredCount = this.filteredCount + INITIAL_LOAD_CARDS
            this.showMoreDisabled = false;
        } else {
            this.filteredCount = this.totalCount;
            this.finalFilteredPrescription = this.finalPrescriptions.slice(0, this.totalCount);
            this.showMoreDisabled = true;
        }
    }

    addPrescriptionToFinal(prescription, counter, detailcounter, orderEligible, addToCart, detailSection) {
        this.finalPrescriptions.push({
            counter: counter,
            detailCounter: detailcounter,
            addToCart: addToCart,
            orderEligible: orderEligible,
            detailSection: detailSection,
            archived: prescription?.archived ?? null,
            daysSupply :prescription?.daysSupply ?? null,
            autoRefillEligible: prescription?.autoRefillEligible ?? null,
            autoRefillEnrolled: prescription?.autoRefillEnrolled ?? null,
            consent: prescription?.consent ?? null,
            controlled: prescription?.controlled ?? null,
            daysSupply: prescription?.daysSupply ?? null,
            dispensedDrug: prescription?.dispensedDrug ?? null,
            expirationDate: prescription?.expirationDate ?? null,
            icon: prescription?.icon ?? null,
            key: prescription?.key ?? null,
            lastFillDate: prescription?.lastFillDate ?? null,
            nextFillMaxDate: prescription?.nextFillMaxDate ?? null,
            nextFillMinDate: prescription?.nextFillMinDate ?? null,
            prescriberFax: prescription?.prescriberFax ?? null,
            prescriberName: prescription?.prescriberName ?? null,
            prescriberPhone: prescription?.prescriberPhone ?? null,
            quantity: prescription?.quantity ?? null,
            refillsRemaining: prescription?.refillsRemaining ?? null,
            status: prescription?.status ?? null,
            writtenDrug: prescription?.writtenDrug ?? null,
            writtenDate: prescription?.writtenDate ?? null,
            addedToCart: false,
            numberOfRefills :prescription?.numberOfRefills ?? null,
            autoRefill: prescription?.autoRefill ?? null,
            physicianFirstName :prescription?.physicianFirstName ?? null,
             physicianLastName :prescription?.physicianLastName ?? null,
                copayConsentGiven :prescription?.copayConsentGiven ?? null,
                copay : prescription?.copay ?? null,
                rxConsentData: prescription?.rxConsentData ?? null,
                rxConsent: prescription?.rxConsent ?? null,
                rxConsentDescription: prescription?.rxConsentDescription ?? null,
                isReNewRx: prescription?.isReNewRx ?? '',
                isRefillable: prescription?.isRefillable ?? ''

        })
    }

    createFinalPrscriptionList(prescriptions) {
        this.finalPrescriptions = [];
        if (prescriptions && Array.isArray(prescriptions) && prescriptions?.length > 0) {
            for (let i = 1; i <= prescriptions?.length; i++) {
                if (i % CARDS_PER_ROW === 0) {
                    this.addPrescriptionToFinal(prescriptions[i - 1], i - 1, `detail${Math.round(i / CARDS_PER_ROW)}`,
                    this.determineRefillEligibility(prescriptions[i - 1]), false, true);
                } else if (i == prescriptions.length) {
                    this.addPrescriptionToFinal(prescriptions[i - 1], i - 1, `detail${Math.round((i + 1) / CARDS_PER_ROW)}`, 
                    this.determineRefillEligibility(prescriptions[i - 1]), false, true);
                }
                else {
                    this.addPrescriptionToFinal(prescriptions[i - 1], i - 1, `detail${Math.round(i / CARDS_PER_ROW)}`,
                    this.determineRefillEligibility(prescriptions[i - 1]), false, false);
                }
            }
        }
        this.displayData(); 
    }



    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    } 


    prepareMemberPrescriptionArray() {
        if (this.totalPrescriptions && this.totalPrescriptions?.length > 0) {
            this.totalPrescriptions = this.totalPrescriptions.map(item => ({
                key: item?.key ?? '',
                daysSupply: item?.daysSupply ?? '',
                quantity: item?.quantity ?? '',
                lastFillDate: item?.lastFillDate ?? '',
                nextFillMinDate: item?.nextFillMinDate ?? '',
                nextFillMaxDate: item?.nextFillMaxDate ?? '',
                refillsRemaining: item?.refillsRemaining ?? 0,
                expirationDate: item?.expirationDate ?? '',
                status: item?.status?.description?.toUpperCase()?.includes('INACTIVE') ? 'Inactive' : (item?.status?.description ?? ''),
                dispensedDrug: this.getDispensedDrug(item),
                consent: this.setDisplayedRxConsent(item?.consent?.status?.code),
                autoRefillEligible: item?.autoRefillEligible && item?.autoRefillEligible === true ? 'Yes' : 'No',
                prescriberName: `${item?.physician?.firstName ?? ''} ${item?.physician?.lastName ?? ''}`,
                physicianFirstName :item?.physician?.firstName ?? '', 
                physicianLastName :item?.physician?.lastName ?? '', 
                prescriberPhone: item?.physician?.phones && Array.isArray(item?.physician?.phones) && item?.physician?.phones?.length > 0
                    ? item?.physician?.phones.find(k => k?.z0type?.toUpperCase() === 'WORK PHONE')?.value ?? '' : '',
                prescriberFax: item?.physician?.phones && Array.isArray(item?.physician?.phones) && item?.physician?.phones?.length > 0
                    ? item?.physician?.phones.find(k => k?.z0type?.toUpperCase() === 'FAX NUMBER')?.value ?? '' : '',
                writtenDrug: item?.product?.drug?.label ?? '',
                autoRefillEnrolled: item?.autoRefill && item?.autoRefill === true ? 'Yes' : 'No',
                autoRefill: item?.autoRefill ?? null, 
                controlled: item?.product?.drug?.z0type?.code === 'CS' || item?.product?.drug?.z0type?.code?.toUpperCase() === 'CONTROLLED' ? 'Yes' : 'No',
                icon: item?.icon ?? null,
                archived: item?.isArchived ?? false,
                writtenDate: item?.writtenDate ?? null,
                numberOfRefills :item?.numberOfRefills ?? null,
                copayConsentGiven : item?.copay?.copayConsentGiven ?? false,
                copay : item?.copay ?? false,
                rxConsentData: item?.consent ?? '',
                rxConsent: item?.consent?.status?.code ?? '',
                rxConsentDescription: item?.consent?.status?.description ?? '',
                isReNewRx: item?.isReNewRx ?? '',
                isRefillable: item?.isRefillable ?? ''

            }))
        }
    }

    getDispensedDrug(item) {
        return item && Array.isArray(item?.fills) && item?.fills?.length > 0
            ? (item.fills[0]?.itemLabel ?? '') : '';
    }
    
    setDisplayedRxConsent(consent) {
        let rxConsent;
        if (consent === 'A') {
            rxConsent = 'Approved';
        } else if (consent === 'P' || consent === 'R') {
            rxConsent = 'Consent Needed';
        } else {
            rxConsent = '';
        }
        return rxConsent;
    }


    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            this.showMoreCards();
        }
    }

    clearSearchData() {
        let count = 1;
        if (this.filterObj.hasOwnProperty("searchByWord")) {
            delete this.filterObj["searchByWord"];
            count = 0;
        }
        this.generateFilterData();
        let ind = this.pillFilterValues.indexOf(
            this.pillFilterValues.find((element) => element.key === "searchByWord")
        );
        if (ind >= 0) {
            this.pillFilterValues.splice(ind, 1);
        }
        this.searchEntered = false;
    }

    handleCloseAddPrescriptionPopover() {
            if(this.prescArray != null && this.prescArray != undefined && this.prescArray.length >0  && this.checkOk == false){
                this.finalPrescriptions.forEach(k => {
                    this.prescArray.forEach(j=>{
                        if(k.key == j){
                            k.addedToCart= false;
                        }
                    })
                })
                this.prescArray=[];
            }
            
        if (this.template.querySelector('c-pharmacy-add-prescription-popover-hum') != null) {  
            this.template.querySelector('c-pharmacy-add-prescription-popover-hum').addPrescription(this.finalPrescriptions);
        }
           
            this.flag= false;
    }

    handelAddPrescription(){ 
      if (this.template.querySelector('c-pharmacy-hpie-create-order-hum ') != null) {  
        this.template.querySelector('c-pharmacy-hpie-create-order-hum').addPrescription(this.addedprescriptions);
    }
       this.flag= false;
       this.checkOk = true;
    }

    handleUpdatePrescriptions(event) { 
        let index ='';
        let eventData = JSON.parse(JSON.stringify(event.detail));
        this.check = eventData.check;
        this.keypassed= eventData.msg;
        let pres = this.finalPrescriptions?.find(k => k?.key === eventData?.msg);

        if(this.check){
            this.prescArray.push(this.keypassed);
            this.checkOk = false;
            this.finalPrescriptions = this.finalPrescriptions.map((item) => ({ 
                ...item,
                    addedToCart: item?.key === eventData?.msg ? true : item?.addedToCart ?? false
            }))
            if (pres && Object.keys(pres)?.length > 0 && this.addedprescriptions?.findIndex(k => k?.key === eventData?.msg) < 0) {
            this.addedprescriptions.push(pres);
            }
        }
        else if (!this.check){
            if(this.prescArray != null && this.prescArray != undefined && this.prescArray.length >0){
                this.prescArray.forEach(k=>{
                    if(k == this.keypassed){
                       this.prescArray=this.prescArray?.filter(k => k?.key != this.keypassed)
                    }
                })
            }

            this.finalPrescriptions = this.finalPrescriptions.map((item) => ({ 
                ...item,
                addedToCart: item?.key === eventData?.msg ? false : item?.addedToCart ?? true
            }))

            if (this.addedprescriptions?.findIndex(k => k?.key === eventData?.msg) >= 0) {
                this.addedprescriptions.forEach(a => {
                    Object.values(a).forEach(b => {
                        let tempNode = JSON.stringify(b);
                        if (null != tempNode && tempNode.includes(eventData?.msg)) {
                            index = this.addedprescriptions.indexOf(a);
                            this.addedprescriptions.splice(index, 1);
                        }
                    })
                });
            }
        }
    }

    mycustomevent(event){
        const textVal = JSON.parse(JSON.stringify(event.detail));
        this.flag =textVal.msg;
    }

    handleAddPrescription(event) {
        let eventKeyData = JSON.parse(JSON.stringify(event.detail));
        let selrxnumber= eventKeyData?.msg;
        this.finalPrescriptions = this.finalPrescriptions.map((item) => ({
            ...item,
            addedToCart: item?.key === selrxnumber ? false : item?.addedToCart ?? true
        }))
        let pres = this.finalPrescriptions?.find(k => k?.key === selrxnumber);
         if (this.addedprescriptions?.findIndex(k => k?.key === selrxnumber) >= 0) {
            this.addedprescriptions = this.addedprescriptions?.filter(k => k?.key != selrxnumber); 
        }
        this.prescArray=[];
    }

    handleCreditCardSave(){
        this.dispatchEvent(new CustomEvent("ordercreditcardsave"));       
    }

    handleCallDemographics(){ 
        this.dispatchEvent(new CustomEvent('calldemographics'));
    } 

    handleCallPreference() {
        this.dispatchEvent(new CustomEvent('callpreference'));
    }

    handleLogNoteAdded() {  
        this.dispatchEvent(new CustomEvent('lognoteadded'));
    }

    determineRefillEligibility(item) {
        return (!(item?.status?.toUpperCase() === 'WORKRX') && !(item?.status?.toUpperCase() === 'INACTIVE')) && (item.isReNewRx === true || item.isRefillable === true) ? true : false;
    }
    handleInactivateRxClick(){
        this.inactivateRxRequest = true;
    }
    handleCloseInactivateRx() {
        this.inactivateRxRequest = false;
    }
}