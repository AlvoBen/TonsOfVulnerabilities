/*
LWC Name
: PharmacyPrescriptionsFilersHum.js
Function        : LWC to display pharmacyPrescriptionFilter deatails.

Modification Log:
* Developer Name                  Date                         Description
*
* Swapnali Sonawane               12/05/2022                   US- 3969790 Migration of the order queue detail capability
* Swapnali Sonawane               01/20/2023                   US- 4146078 Mail Order Pharmacy- Address/Close the gaps identified during user testing. - 1
* Nirmal Garg					  07/27/2023	                US4902305
* Vishal Shinde                   10/12/2023                    DF- 8215
****************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import getPrescriptions from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeEpostMemberPrescription';
import { getPickListValues, hcConstants, getLocaleDate } from 'c/crmUtilityHum';
import updateCapType from '@salesforce/apexContinuation/Pharmacy_LC_HUM.updateCaptype';
import updateMemberConsentBeginDate from '@salesforce/apexContinuation/Pharmacy_LC_HUM.updateConsentBeginDate';
import updateMemberConsentEndDate from '@salesforce/apexContinuation/Pharmacy_LC_HUM.updateConsentEndDate';
import getMemberRequest from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService';
import { getPrescriptionIcons } from './layoutConfig';
import { publish, MessageContext, subscribe } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import noPresData from '@salesforce/label/c.PHARMACY_PRESCRIPTION_NODATA';
import prescriptionServiceError from '@salesforce/label/c.PHARMACY_PRESCRIPTION_ERROR';
import { CurrentPageReference } from 'lightning/navigation';
const CARDS_PER_ROW = 4;
const INITIAL_LOAD_CARDS = 8;
const PRIOR_MONTH = 18;

export default class PharmacyPrescriptionsFiltersHum extends LightningElement {

    openCapTypeModal = false;
    openMemberConsentModal = false;
    capTypeFlag = false;
    memberConsentFlag = false;

    sScriptKey = "";
    @api enterpriseId;
    @api networkId;
    @api recordId;
    availablePrescriptions = [];
    @track sSortBy = [];
    @track serveresult;
    @track filterStatusValues = {};


    @track prscriptionList;
    @api var1;
    @api pharmacydemographicdetails;
    @track isCardClick = false;
    @track selectedCardIndex;
    memberConsentApprovedDate;
    @track isMorePrescription = true;
    @track finalPrescriptionList = [];//used for Prescription card and detail logic
    @track addCartPrescriptionList = [];
    @track prescriptions;
    @track filterPrescriptionObj;
    @track filterObj = {};
    @track pillFilterValues = [];
    @track filterInactivePrescription;
    @track filterAvailablePrescription;
    @track iconsModel;
    @track allPrescriptionRecords;
    @track filterRemainingPrescription;
    @track counter = 1;
    @track showMorePrescription;
    @track totalcount = 0;
    @track filteredcount = 0;
    @track totalfilteredresults = [];
    @track layoutsection;
    @track isInActiveSelected = false;
    @track loaded = false;
    @wire(MessageContext)
    messageContext;
    @track orderDetailPrescriptions = [];
    @track createOrderDisable = true;
    @track addPresciptionList;
    @track orderItemPrescriptions = [];
    @track addedprescriptions = [];
    @track isCreateOrderClicked = false;
    @track dataFound = false;
    @track serviceerror = false;
    @track memberPlanId='';
    @track keyword = '';
    labels = {
        noPresData,
        prescriptionServiceError
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.state;
            this.setParametersBasedOnUrl();
        }
    }

    setParametersBasedOnUrl() {
        this.memberPlanId = this.urlStateParameters.c__PlanMemberId || null;
    }


    connectedCallback() {
        this.getSortByValue();
        if (this.pharmacydemographicdetails) {
            this.setCapTypeMemberConsent(this.pharmacydemographicdetails);
        }
        this.iconsModel = getPrescriptionIcons("icons");
    }

    @api setDemographicsDetails(data) {
        this.pharmacydemographicdetails = data;
        if (this.template.querySelector('c-pharmacy-create-order-hum') != null) {
            this.template.querySelector('c-pharmacy-create-order-hum').pharmacydata(this.pharmacydemographicdetails);
        }
    }

    @api
    setAutofill(data) {
        if (data) {
            let layoutsectionid = Math.floor(this.layoutid / CARDS_PER_ROW + 1);
            this.layoutsection = this.template.querySelector('[data-id=detail' + Math.round(layoutsectionid) + ']');
            if (this.layoutsection != null && this.layoutsection != undefined) {
                let detaildata = this.finalPrescriptionList[this.layoutid];
                this.template.querySelectorAll(".detailsection").forEach(k => {
                    k.style.display = 'none';
                })
                if (this.template.querySelector("c-pharmacy-prescription-detail-hum") != null) {
                    this.template.querySelectorAll("c-pharmacy-prescription-detail-hum").forEach(t => {
                        t.getPriscriptionDetails(detaildata);
                    })
                }
                this.layoutsection.style.display = "block";
            }
        }
    }

    @api
    setPrescriptions(data) {
        let result = data;
        if (result) {
            this.filterPrescriptionObj = result; // Use for Filter 
            this.addPresciptionList = result; // Use to pass presciption to highlight panel 
            if (this.addPresciptionList && this.addPresciptionList.length > 0) {
                this.addIcon(this.addPresciptionList);
            }
            this.prepareMemberPrescriptionArray(this.filterPrescriptionObj);
            this.prscriptionList = this.filterPrescriptionObj;   //Original data
            if (this.prscriptionList && this.prscriptionList.length > 0) {
                this.removeInActiveStatus(this.filterPrescriptionObj);
                this.filterStatusValues = getPickListValues(['Status'], this.filterPrescriptionObj);
            }

            this.totalcount = this.filterAvailablePrescription ? this.filterAvailablePrescription.length : 0;
            this.totalfilteredresults = this.filterAvailablePrescription;
            if (this.totalcount != 0) {
                this.dataFound = true;
                if (this.totalcount > INITIAL_LOAD_CARDS) {
                    this.filterPrescriptionObj = this.totalfilteredresults.slice(0, INITIAL_LOAD_CARDS);
                    this.filteredcount = INITIAL_LOAD_CARDS;
                    this.isMorePrescription = false;
                }
                else {
                    this.filterPrescriptionObj = this.totalfilteredresults;
                    this.filteredcount = this.totalcount;
                    this.isMorePrescription = true;
                }
                this.createFinalPrscriptionList(this.filterAvailablePrescription);
            } else {

            }

        }
        this.loaded = true;
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

    goToSummary() {
        this.isCreateOrderClicked = false;
        this.addedprescriptions = [];
        this.addCartPrescriptionList = [];
        this.finalPrescriptionList.forEach(k => {
            if (k.prescriptions.Status == 'INACTIVE' || k.prescriptions.Status == 'WORKRX') {
                k.OrderEligible = false;
            }
            else {
                k.OrderEligible = true;
            }
            k.addToCart = false;
        });

        this.publishMemberData('ClosePrescriptionWindow', true);
        this.createOrderDisable = true;
    }

    addPrescription() {

    }

    handleCreateOrderClick() {
        this.isCreateOrderClicked = true;
        if (this.template.querySelector('c-pharmacy-create-order-hum') != null) {
            this.template.querySelector('c-pharmacy-create-order-hum').pharmacydata(this.pharmacydemographicdetails);
        }
    }

    handleAddPrescription(event) {
        if (event && event.detail) {
            let presdata = {
                AddedPrescription: event.detail.addeddprescriptions,
                TotalPrescriptions: this.addPresciptionList
            }
            this.publishMemberData('AddPrescriptions', presdata);
        }
    }



    isEmptyObj(obj) {
        return Object.keys(obj).length === 0;
    }

    publishMemberData(msgType, msgData) {
        let message = { messageDetails: msgData, MessageName: msgType };
        publish(this.messageContext, messageChannel, message);
    }

    compareValues(key, order = 'asc') {
        let sKey = key;
        return function innerSort(a, b) {
            if (!a.hasOwnProperty(key) || !b.hasOwnProperty(key)) {
                return 0;
            }
            let comparison = 0;
            if (sKey == "ExpirationDate") {
                let da, db;
                if (a[key] != undefined) { da = new Date(a[key]); }
                if (b[key] != undefined) { db = new Date(b[key]); }
                comparison = da - db;
            }
            else {
                const varA = (typeof a[key] === 'string')
                    ? a[key].toUpperCase() : a[key];
                const varB = (typeof b[key] === 'string')
                    ? b[key].toUpperCase() : b[key];
                if (varA > varB) {
                    comparison = 1;
                } else if (varA < varB) {
                    comparison = -1;
                }
            }
            return (
                (order === 'desc') ? (comparison * -1) : comparison
            );
        };
    }

    getSortedValue(event) {
        if (event) {  //on selection of filters by user
            const element = event.target.name;
            const filterValue = event.detail.value;
            if (this.pillFilterValues.length == 0) {
                this.filterAvailablePrescription.sort(this.compareValues(filterValue, 'desc'));
                this.totalfilteredresults.sort(this.compareValues(filterValue, 'desc'));
                this.createFinalPrscriptionList(this.filterAvailablePrescription);
            }
            else {
                this.prscriptionList.sort(this.compareValues(filterValue, 'desc'));
                this.totalfilteredresults.sort(this.compareValues(filterValue, 'desc'));
                this.createFinalPrscriptionList(this.filterPrescriptionObj);
            }
            if (this.pillFilterValues.some(arg => arg["key"] === element)) {
                let ind = this.pillFilterValues.indexOf(this.pillFilterValues.find(element => element.key === 'sSortBy'));
                if (ind >= 0) {
                    this.pillFilterValues.splice(ind, 1);
                }
                this.pillFilterValues.push({
                    "key": event.target.name,
                    "value": event.target.value
                });
            }
            else {
                this.pillFilterValues.push({
                    "key": event.target.name,
                    "value": event.target.value
                })
            }
        }
    }

    handleKeywordSearch(event) {
        let name = event.detail.name;
        let value = event.detail.value;
        this.keyword = value;
        if (value.length >= 3) {
            this.filterObj[name] = value;
            this.filterObj = {
                ...this.filterObj,
                searchByWord: value,
            };
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
            this.pillFilterValues.push({
                key: name,
                value: value,
            });
        } else {
            if (this.filterObj.hasOwnProperty(name)) {
                delete this.filterObj[name];
            }
            this.generateFilterData();
            this.pillFilterValues = this.pillFilterValues.filter(k => k.key != name);
        }
    }


    performfilter(keyname, keyvalues) {
        let tmp = [];
        if (this.filterPrescriptionObj.length > 0) {
            if (keyname === 'searchByWord') {
                this.filterPrescriptionObj.forEach(a => {
                    Object.values(a).forEach(h => {
                        if (null != h && h.toString().toLowerCase().includes(keyvalues.toLowerCase())
                            && !tmp.includes(a)) {
                            tmp.push(a);
                        }
                    })
                })
            } else {
                this.filterPrescriptionObj.forEach(f => {
                    if (f.hasOwnProperty(keyname) && keyvalues.includes(f[keyname])) {
                        if (!tmp.includes(f))
                            tmp.push(f);
                    }
                })
            }
        }
        this.filterPrescriptionObj = tmp.length > 0 ? tmp : [];
        tmp = [];
    }



    generateFilterData() {
        this.filterPrescriptionObj = this.prscriptionList.length > 0 ? this.prscriptionList : [];
        if (this.filterObj && Object.keys(this.filterObj).length > 0) {
            Object.keys(this.filterObj).forEach(k => {
                if (this.filterObj[k].length > 0) {
                    this.performfilter(k, this.filterObj[k])
                } else {
                    this.filterObj[k];
                }
            })
        } else {
            this.filterPrescriptionObj = this.filterAvailablePrescription;
        }
        this.totalfilteredresults = this.filterPrescriptionObj;
        this.totalcount = this.totalfilteredresults.length;
        if (this.totalcount > INITIAL_LOAD_CARDS) {
            this.filteredcount = INITIAL_LOAD_CARDS;
            this.filterPrescriptionObj = this.totalfilteredresults.slice(0, INITIAL_LOAD_CARDS);
            this.isMorePrescription = false;
        } else {
            this.filteredcount = this.totalcount;
            this.isMorePrescription = true;
        }
        this.createFinalPrscriptionList(this.filterPrescriptionObj);
    }



    getFilterValue(event) {
        const me = this;
        if (event) {  //on selection of filters by user            
            const filterValue = event.detail.value;
            let filterStatus = this.prscriptionList.filter(prscription => {
                return prscription.Status === filterValue;
            })
            let tempObj = JSON.parse(JSON.stringify(filterStatus));
            this.createFinalPrscriptionList(tempObj);
        }
    }



    handlePillRemove(event) {
        this.pillFilterValues.splice(event.target.dataset.index, 1);
        Object.keys(this.filterObj).forEach(t => {
            if (t === event.target.dataset.key) {
                if (event.target.dataset.key === 'searchByWord') {
                    delete this.filterObj['searchByWord'];
                    if (this.template.querySelector('c-generic-keyword-search-hum') != null) {
                        this.template.querySelector('c-generic-keyword-search-hum').clearSearchData();
                    }
                } else {
                    const ind = this.filterObj[t].indexOf(event.target.dataset.value);
                    this.filterObj[t].splice(ind, 1);
                    if (this.filterObj[t].length <= 0) {
                        delete this.filterObj[t];
                    }
                }
            }
        });
        this.generateFilterData();
        if (this.template.querySelector('c-generic-multiselect-picklist-hum') != null) {
            let payload = { keyname: event.target.dataset.key, value: event.target.dataset.value };
            if (event.target.dataset.value == "INACTIVE") { this.isInActiveSelected = false; }
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
        let filterdata = event.detail;
        if (filterdata != null && Object.values(filterdata.selectedvalues).length > 0) {
            if (this.filterObj.hasOwnProperty(filterdata.keyname)) {
                this.filterObj[filterdata.keyname] = Object.values(filterdata.selectedvalues);
            } else {
                this.filterObj[filterdata.keyname] = Object.values(filterdata.selectedvalues);
            }
        }
        if (Object.values(filterdata.selectedvalues).length > 0) {
            this.pillFilterValues = this.pillFilterValues.filter(k => k["key"] != filterdata.keyname);
            Object.values(filterdata.selectedvalues).forEach(k => {
                this.pillFilterValues.push({
                    key: filterdata.keyname,
                    value: k
                });
            })
        }
        else {
            this.pillFilterValues = this.pillFilterValues.filter(option => option["key"] != filterdata.keyname);
            delete this.filterObj[filterdata.keyname];
        }
        this.generateFilterData();
    }

    async callservice() {
        let response = await this.getPrescriptionsDetails();
        return response;
    }

    handleAutoRefill(event) {
        this.callservice().then((response) => {
            if (response === 'success' || response === undefined) {
                this.selectedCardIndex = event.detail.selectedCard;
                let layoutid = event.detail.selectedKey;
                let layoutsectionid = Math.floor(layoutid / CARDS_PER_ROW + 1);
                this.layoutsection = this.template.querySelector('[data-id=detail' + Math.round(layoutsectionid) + ']');
                if (this.layoutsection != null && this.layoutsection != undefined) {
                    let detaildata = this.finalPrescriptionList[layoutid];
                    this.template.querySelectorAll(".detailsection").forEach(k => {
                        k.style.display = 'none';
                    })
                    if (this.template.querySelector("c-pharmacy-prescription-detail-hum") != null) {
                        this.template.querySelectorAll("c-pharmacy-prescription-detail-hum").forEach(t => {
                            t.getPriscriptionDetails(detaildata);
                        })
                    }
                    this.layoutsection.style.display = "block";
                }
            }
        })
    }

    handleAddCart(event) {
        if (event) {
            let selPrescriptioncolor = event.detail.prescriptioncolor;
            let selrxnumber = event.detail.prescriptionnumber;
            let newprescription = event.detail.item;
            const selPrescription = this.finalPrescriptionList.find(k => k.prescriptions.RXNumber === selrxnumber);
            if (selPrescription && !this.addedprescriptions.includes(selPrescription)) {
                this.addedprescriptions.push(selPrescription);
            }
            switch (selPrescriptioncolor) {
                case "green": {
                    this.finalPrescriptionList.forEach(k => {
                        if (k.icon.icontype === 'blue' || k.prescriptions.Status === "WORKRX" || k.prescriptions.Status === 'INACTIVE') {
                            k.OrderEligible = false;
                        }

                        if (k.prescriptions.RXNumber == selrxnumber) {
                            k.addToCart = true;
                        }
                    });
                    this.createOrderDisable = false;
                    break;
                }
                case "blue": {
                    this.finalPrescriptionList.forEach(k => {
                        if (k.icon.icontype === 'green' || k.prescriptions.Status === "WORKRX" || k.prescriptions.Status === 'INACTIVE') {
                            k.OrderEligible = false;
                        }
                        if (k.icon.icontype === 'black' && k.prescriptions.Status != "WORKRX") {
                            k.OrderEligible = true;
                        }
                        if (k.prescriptions.RXNumber == selrxnumber) {
                            k.addToCart = true;
                        }
                    })
                    this.createOrderDisable = false;
                    break;
                }
                case "black": {
                    this.finalPrescriptionList.forEach(k => {
                        if (k.prescriptions.Status === "WORKRX" || k.prescriptions.Status === 'INACTIVE') {
                            k.OrderEligible = false;
                        }
                        if ((k.icon.icontype === 'green' || k.icon.icontype === 'blue') && k.prescriptions.Status != "WORKRX") {
                            k.OrderEligible = true;
                        }
                        if (k.prescriptions.RXNumber == selrxnumber) {
                            k.addToCart = true;
                        }
                    })
                    this.createOrderDisable = false;
                    break;
                }
                default: {
                    this.finalPrescriptionList.forEach(k => {
                        if (k.prescriptions.Status === 'INACTIVE' || k.prescriptions.Status === "WORKRX") {
                            k.OrderEligible = false;
                        }
                    })
                    break;
                }
            }
        }
        this.addCartPrescriptionList = this.finalPrescriptionList;
    }

    handleCardClickSelect(event) {
        this.handleCardClick(event)
            .then(() => this.layoutsection.scrollIntoView())
            .catch((error) => console.log('handleCardClickSelect ' + error));
    }


    handleCardClick(event) {
        return new Promise((resolve) => {
            this.isCardClick = event.detail.isCardClick;
            this.selectedCardIndex = event.detail.selectedCard;
            let layoutid = event.detail.selectedKey;
            let selectedRXNumber = event.detail.RXNumber;
            let layoutsectionid = Math.floor(layoutid / CARDS_PER_ROW + 1);
            this.layoutsection = this.template.querySelector('[data-id=detail' + Math.round(layoutsectionid) + ']');
            if (this.layoutsection != null && this.layoutsection != undefined) {
                let detaildata = this.finalPrescriptionList[layoutid];
                this.template.querySelectorAll(".detailsection").forEach(k => {
                    k.style.display = 'none';
                })
                if (this.template.querySelector("c-pharmacy-prescription-detail-hum") != null) {
                    this.template.querySelectorAll("c-pharmacy-prescription-detail-hum").forEach(t => {
                        t.getPriscriptionDetails(detaildata);
                    })
                }
                if (this.template.querySelector("c-pharmacy-prescription-card-hum") != null) {
                    this.template.querySelectorAll("c-pharmacy-prescription-card-hum").forEach(t => {
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


    getSortByValue() {
        const items = [
            { value: 'DispensedDrug', label: 'Dispensed drug name' },
            { value: 'ExpirationDate', label: 'Expiration date' },
            { value: 'NextFillDate', label: 'Next fill date' },
            { value: 'PrescriberInformation', label: 'Prescriber' },
            { value: 'RefillsRemaining', label: 'Refills remaining' },
            { value: 'RXNumber', label: 'RX number' }

        ];
        this.sSortBy = items;
    }

    ClearFilter() {
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

    // to move inactive status at last 
    arraymove(arr, fromIndex, toIndex) {
        var element = arr[fromIndex];
        arr.splice(fromIndex, 1);
        arr.splice(toIndex, 0, element);
    }


    async getPrescriptionsDetails() {
        let startdate = new Date();
        startdate.setMonth(startdate.getMonth() - PRIOR_MONTH);
        let enddate = new Date();
        let sStartDate = (startdate.getMonth() + 1) + '/' + startdate.getDate() + '/' + startdate.getFullYear();
        let sEndDate = (enddate.getMonth() + 1) + '/' + enddate.getDate() + '/' + enddate.getFullYear();
        await getPrescriptions({ memID: this.enterpriseId, scriptKey: this.sScriptKey, startDate: sStartDate, endDate: sEndDate, networkId: this.networkId, sRecordId: this.recordId })
            .then(result => {
                this.loaded = true;
                if (result) {
                    this.filterPrescriptionObj = JSON.parse(result); // Use for Filter 
                    this.addPresciptionList = JSON.parse(result); // Use to pass presciption to highlight panel 
                    if (this.addPresciptionList && this.addPresciptionList.length > 0) {
                        this.addIcon(this.addPresciptionList);
                    }
                    this.prepareMemberPrescriptionArray(this.filterPrescriptionObj);
                    this.prscriptionList = this.filterPrescriptionObj;   //Original data
                    if (this.prscriptionList && this.prscriptionList.length > 0) {
                        this.removeInActiveStatus(this.filterPrescriptionObj);
                        this.filterStatusValues = getPickListValues(['Status'], this.filterPrescriptionObj);
                    }
                    this.totalcount = this.filterAvailablePrescription.length;
                    this.totalfilteredresults = this.filterAvailablePrescription;
                    if (this.totalcount > INITIAL_LOAD_CARDS) {
                        this.filterPrescriptionObj = this.totalfilteredresults.slice(0, INITIAL_LOAD_CARDS);
                        this.filteredcount = INITIAL_LOAD_CARDS;
                        this.isMorePrescription = false;
                    }
                    else {
                        this.filterPrescriptionObj = this.totalfilteredresults;
                        this.filteredcount = this.totalcount;
                        this.isMorePrescription = true;
                    }
                    this.createFinalPrscriptionList(this.filterAvailablePrescription);
                }
                return "success";
            }).catch(err => {
                this.loaded = true;
                console.log("Error Occured", err);
                return "failed";
            });
    }


    addIcon(result) {
        let tmp = [];
        result.forEach(f => {
            f.icon = this.getIcon(f);
            tmp.push(f);
        })
        this.addPresciptionList = tmp.length > 0 ? tmp : [];
        tmp = [];
    }


    removeInActiveStatus(result) {
        let PrsList = result;
        this.filterAvailablePrescription = PrsList.filter((item) => item.Status !== 'INACTIVE');
        this.filterInactivePrescription = PrsList.filter((item) => item.Status === 'INACTIVE');
    }

    ShowMoreCards() {

        if ((this.filteredcount + INITIAL_LOAD_CARDS) < this.totalcount) {
            this.filterPrescriptionObj = this.totalfilteredresults.slice(0, (this.filteredcount + INITIAL_LOAD_CARDS))
            this.filteredcount = this.filteredcount + INITIAL_LOAD_CARDS
        } else {
            this.filteredcount = this.totalcount;
            this.filterPrescriptionObj = this.totalfilteredresults.slice(0, this.totalcount);
            this.isMorePrescription = true;
        }
        this.createFinalPrscriptionList(this.filterPrescriptionObj)
    }

    createFinalPrscriptionList(result) {
        this.finalPrescriptionList = [];
        this.prescriptions = result;
        let prescData;
        let addCartVal = false;
        let orderEligVal;
        if (this.prescriptions) {
            for (let j = 1; j <= this.prescriptions.length; j++) {
                prescData = this.prescriptions[j - 1];
                if (prescData.Status == 'INACTIVE' || prescData.Status == 'WORKRX') {
                    orderEligVal = false;
                }
                else {
                    orderEligVal = true;
                }
                if (j % CARDS_PER_ROW == 0) {
                    this.finalPrescriptionList.push({
                        key: j - 1,
                        detailSection: true,
                        prescriptions: this.prescriptions[j - 1],
                        counter: 'detail' + Math.round(j / CARDS_PER_ROW),
                        icon: this.getIcon(this.prescriptions[j - 1]),
                        addToCart: addCartVal,
                        OrderEligible: orderEligVal
                    });
                }
                else {
                    if (j == this.prescriptions.length) {
                        this.finalPrescriptionList.push({
                            key: j - 1,
                            detailSection: true,
                            prescriptions: this.prescriptions[j - 1],
                            counter: 'detail' + Math.round((j + 1) / CARDS_PER_ROW),
                            icon: this.getIcon(this.prescriptions[j - 1]),
                            addToCart: addCartVal,
                            OrderEligible: orderEligVal
                        });
                    }
                    else {
                        this.finalPrescriptionList.push({
                            key: j - 1,
                            detailSection: false,
                            prescriptions: this.prescriptions[j - 1],
                            counter: 'detail' + Math.round(j / CARDS_PER_ROW),
                            icon: this.getIcon(this.prescriptions[j - 1]),
                            addToCart: addCartVal,
                            OrderEligible: orderEligVal
                        });
                    }
                }
            }
            this.updateAddCartValue();
            return this.finalPrescriptionList;
        }
    }

    updateAddCartValue() {
        let cartFilter = [];
        if (this.addCartPrescriptionList.length > 0) {
            this.finalPrescriptionList.forEach(k => {
                cartFilter = this.addCartPrescriptionList.filter(cartval => cartval.prescriptions.RXNumber == k.prescriptions.RXNumber);
                let cartObj = JSON.parse(JSON.stringify(cartFilter));
                if (cartObj != undefined && cartObj.length > 0) {
                    k.OrderEligible = cartObj[0].OrderEligible;
                    k.addToCart = cartObj[0].addToCart;
                }
            })

        }
    }

    getIcon(iconname) {
        let Today = Date.parse(getLocaleDate(new Date()));
        let expdate = Date.parse(iconname.ExpirationDate);
        let nextfilldate = Date.parse(iconname.NextFillDate);
        let nextfillmindate = Date.parse(iconname.NextFillMinDate);
        let lastfilldate = Date.parse(iconname.LastFillDate);
        if (iconname.Status === 'INACTIVE') {
            return this.iconsModel.find(x => x.iconname === "INACTIVE");
        }
        else if (iconname.Status === 'EXPIRED' || iconname.RefillsRemaining <= 0 || expdate < Today) {
            if (nextfilldate <= Today) {
                return this.iconsModel.find(x => x.iconname === "greenclock");
            }
            else if (nextfilldate >= Today && nextfillmindate < Today) {
                return this.iconsModel.find(x => x.iconname === "blueclock");
            } else {
                return this.iconsModel.find(x => x.iconname === "clock");
            }
        }
        else {
            if (expdate > Today && nextfilldate < Today) {
                return this.iconsModel.find(x => x.iconname === "refillsremaining");
            } else if (nextfilldate > Today && nextfillmindate <= Today) {
                return this.iconsModel.find(x => x.iconname === "bluecalendar");
            } else {
                return this.iconsModel.find(x => x.iconname === "event");
            }
        }
    }

    toggleCapType() {
        this.openCapTypeModal = true;
    }

    closeModal() {
        this.openCapTypeModal = false;
        this.openMemberConsentModal = false;
    }

    /**
    * Display toast message 
    * when an exception is
    * thrown
    */
    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }

    updateCapTypeValue() {
        let capTypeValue = '';
        this.openCapTypeModal = false;
        let isUpdateCapTypeSuccess = false;
        if (this.capTypeFlag) {
            this.capTypeFlag = false;
            capTypeValue = 'E';
        }
        else {
            this.capTypeFlag = true;
            capTypeValue = 'S';
        }
        updateCapType({ enterprise: this.enterpriseId, captype: capTypeValue, networkID: this.networkId, sRecordId: this.recordId })
            .then(result => {
                if (result == "success") {
                    isUpdateCapTypeSuccess = true;
                    this.publishMemberData("CapType", capTypeValue);
                    this.showToast("Success", "Cap Type Updated", "info");
                    //get member details
                    this.getUpdatedMemberDetails("CapType");
                }
            }).catch(err => {
                this.showToast("Failed", "Failed to Update Cap Type", "error");
                console.log("update cap type service error - " + err);
            });
    }

    getUpdatedMemberDetails(calledFrom) {
        getMemberRequest({ memID: this.enterpriseId, networkId: this.networkId, sRecordId: this.recordId })
            .then(result => {

                let responseData = JSON.parse(result);
                this.pharmacydemographicdetails = responseData.objPharDemographicDetails;
                if (calledFrom == "CapType") {
                    this.setCapType(this.pharmacydemographicdetails.CapType);
                }
                if (calledFrom == "MemberConsent") {
                    this.setMemberConsent(this.pharmacydemographicdetails.NeedsMemberConsent, this.pharmacydemographicdetails.MemberConsent, this.pharmacydemographicdetails.MemberConsentApprovedDate);
                }
            }).catch(err => {
                console.log('Error occurred' + err);
            });
    }

    toggleMemberConsent() {
        this.openMemberConsentModal = true;
    }

    updateMemberConsent() {
        let isUpdatememberConsentSuccess = false;
        this.openMemberConsentModal = false;
        if (this.memberConsentFlag) {
            this.memberConsentFlag = false;
            //call update member consent end date service
            updateMemberConsentEndDate({ enterprise: this.enterpriseId, networkID: this.networkId, sRecordId: this.recordId })
                .then(result => {
                    if (result == 'success') {
                        this.showToast("Success", "Member Consent Updated", "info");
                        isUpdatememberConsentSuccess = true;
                        this.publishMemberData("MemberConsent", "Member Consent Disabled");
                        //get member details
                        this.getUpdatedMemberDetails("MemberConsent");
                    }
                }).catch(err => {
                    this.showToast("Failed", "Failed to Update Member Consent", "error");
                    console.log("update consent end date service call error - " + err);

                });
        }
        else {
            this.memberConsentFlag = true;
            //call update member consent begin date service
            updateMemberConsentBeginDate({ enterprise: this.enterpriseId, networkID: this.networkId, sRecordId: this.recordId })
                .then(result => {
                    if (result == 'success') {
                        this.showToast("Success", "Member Consent Updated", "info");
                        isUpdatememberConsentSuccess = true;
                        this.publishMemberData("MemberConsent", this.memberConsentApprovedDate);
                        //get member details
                        this.getUpdatedMemberDetails("MemberConsent");
                    }
                }).catch(err => {
                    this.showToast("Failed", "Failed to Update Member Consent", "error");
                    console.log("update consent begin date service call error - " + err);
                });
        }
    }

    prepareMemberPrescriptionArray(result) {
        if (result && result.length > 0) {
            result.forEach(function (item) {
                if (item.Consent != '' && item.Consent != null) {
                    if (item.Consent.toUpperCase() == "CA") {
                        item.Consent = "Approved";
                    }
                    else if (item.Consent.toUpperCase() == "CPC" || item.Consent.toUpperCase() == "CR") {
                        item.Consent = "Consent Needed";
                    }
                    else {
                        item.Consent = "";
                    }
                }
                if (item.AutoRefillEligible.toUpperCase() == "TRUE") {
                    item.AutoRefillEligible = "Yes";
                }
                else {
                    item.AutoRefillEligible = "No"
                }
                // Convert phone number into format (###-###-####)
                if (item.Phone != undefined || item.Phone != '') {
                    if (item.Phone.length == 10) {
                        item.Phone = item.Phone.substr(0, 3) + '-' + item.Phone.substr(3, 3) + '-' + item.Phone.substr(6, 4);
                    }
                }
                if (item.Fax != undefined || item.Fax != '') {
                    if (item.Fax.length == 10) {
                        item.Fax = item.Fax.substr(0, 3) + '-' + item.Fax.substr(3, 3) + '-' + item.Fax.substr(6, 4);
                    }
                }
            })
            this.filterPrescriptionObj = result;
        }
    }


    //set cap type value
    setCapType(capValue) {
        if (capValue == "S") {
            this.capTypeFlag = true;
        }
        else {
            this.capTypeFlag = false;
        }
    }

    setCapTypeMemberConsent(pharmacyDemographics) {
        this.setCapType(pharmacyDemographics.CapType);
        this.setMemberConsent(pharmacyDemographics.NeedsMemberConsent, pharmacyDemographics.MemberConsent, pharmacyDemographics.MemberConsentApprovedDate);
    }

    setMemberConsent(needsMemberConsent, memberConsent, memberConsentApprovedDate) {
        if (needsMemberConsent == "" || needsMemberConsent == null || needsMemberConsent == undefined || needsMemberConsent == 'false') {
            this.memberConsentFlag = false;
        }
        if (memberConsent != "" && memberConsent != null && memberConsent != undefined) {
            if (memberConsent == "true") {
                this.memberConsentFlag = true;
            }
            else if (memberConsent == "false") {
                this.memberConsentFlag = false;
            }
            if (memberConsentApprovedDate != null && memberConsentApprovedDate != undefined)
                this.memberConsentApprovedDate = "Approved " + memberConsentApprovedDate;
        }
    }

    handlescroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if ((this.filteredcount + INITIAL_LOAD_CARDS) < this.totalcount) {
                this.filterPrescriptionObj = this.totalfilteredresults.slice(0, (this.filteredcount + INITIAL_LOAD_CARDS))
                this.filteredcount = this.filteredcount + INITIAL_LOAD_CARDS
            } else {
                this.filteredcount = this.totalcount;
                this.filterPrescriptionObj = this.totalfilteredresults.slice(0, this.totalcount);
                this.isMorePrescription = true;
            }
        }
        this.createFinalPrscriptionList(this.filterPrescriptionObj);
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
}