/*

LWC Name        : benefitsPolicyRiderHum.js

Function        : LWC to display benefitsPolicyRiderHum.

* Developer Name                  Date                         Description

*------------------------------------------------------------------------------------------------------------------------------

* Divya Bhamre                   26/05/2022                      US - 3017471 
* Aishwarya Pawar               	 03/01/2023                     US - 4286514
* Kinal Mangukiya                    05/25/2023                     US-4588646
****************************************************************************************************************************/


import { LightningElement, track, api, wire } from 'lwc';
import HUMNoRecords from "@salesforce/label/c.HUMNo_records_to_display";
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { performTableLogging, getLoggingKey, checkloggingstatus } from 'c/loggingUtilityHum';
import { CurrentPageReference } from 'lightning/navigation';
import loggingcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
const INITIAL_LOAD = 10;

export default class BenefitsPolicyRiderHum extends LightningElement {

    @api serviceerror;
    @api message;
    @api pberesponse;
    @api memberplanname;
    @api isDental;
    @track data = [];
    @track initialload = true;
    @track isDataAvailable = false;
    @track filteredCount = 0;
    @track totalCount = 0;
    @track totalRiderList = [];
    @track filteredRiderList = [];
    @track screenName;
    @track loggingkey;
    @track pageRef;
    @track columns = [
        { label: 'Name', fieldName: 'name' },
        { label: 'Description', fieldName: 'Description', type: 'text' }
    ];


    labels = {
        HUMNoRecords
    };
	
	get formatTotalCount(){
		if(this.totalCount > 6){
			return '6+';
		}else{
			return this.totalCount;
		}
	}

    connectedCallback() {
        this.initialSetUp();
    }

    initialSetUp() {
        if (!this.serviceerror) {
            this.setRiderData();
        } else {
            this.loaded = true;
        }
        this.loadCommonCSS();
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
        if (this.relatedInputField[0].value == undefined) this.relatedInputField[0].value = this.memberplanname;
    }

    //load css
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    @track relatedInputField = [{
        label: "Plan Member Id",
        value: this.memberplanname
    }, {
        label: "Section",
        value: "Benefits Policy Riders"
    }, {
        label: "Name",
        mappingField: "name"
    }];

    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.pageRef = currentPageReference;
        getLoggingKey(this.pageRef).then(result => {
            this.loggingkey = result;
        });
    }


    @api
    setPBEData(pberesponse,memberplanname, isDental) {
        this.pberesponse = pberesponse;
        this.memberplanname = memberplanname;
        this.isDental = isDental;
        this.setRiderData();
    }

    @api
    displayErrorMessage(serviceError, message) {
        this.loaded = true;
        this.serviceerror = serviceError;
        this.message = message;
        this.dataFound = false;
    }


    getUniqueId() {
        return Math.random().toString(16).slice(2);
    }


    processRiderData() {
        this.riderList.Rider.forEach(k => {
            this.totalRiderList.push({
                index: this.getUniqueId(),
                name: this.platformcode === 'MTV' || this.platformcode === 'EM' ? k.Name : this.platformcode === 'CAS' || this.platformcode === 'LV' ? k.Id : '',
                Description: this.platformcode === 'MTV' || this.platformcode === 'EM' ? k.CertDefinition : this.platformcode === 'CAS' || this.platformcode === 'LV' ? k.Name : '',
            })
        })
    }


    setRiderData() {
        this.screenName= this.isDental ? "Dental Benefits Riders" : "Medical Benefits Riders" ;
        if (this.pberesponse && this.pberesponse?.RiderList &&
            this.pberesponse?.RiderList?.Rider && Array.isArray(this.pberesponse?.RiderList?.Rider)
            && this.pberesponse?.RiderList?.Rider?.length > 0) {
            this.riderList = this.pberesponse?.RiderList ?? null;
            this.platformcode = this.pberesponse?.PlatformCode ?? null;
            this.dataFound = true;
            this.processRiderData();
            this.getFilteredData();
        } else {
            this.riderList = null;
        }
        this.loaded = true;
        this.serviceerror = false;
    }


    getFilteredData() {
        if (this.totalRiderList.length <= INITIAL_LOAD) {
            this.totalCount = this.totalRiderList.length;
            this.filteredCount = this.totalCount;
            this.filteredRiderList = this.totalRiderList;
        } else {
            this.totalCount = this.totalRiderList.length;
            this.filteredCount = INITIAL_LOAD
            this.filteredRiderList = this.totalRiderList.slice(0, INITIAL_LOAD);
        }
    }


    handleScroll(event) {
        if ((event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight - 1)
            || (event.target.scrollHeight - Math.round(event.target.scrollTop) === event.target.clientHeight + 1)) {
            if (this.totalRiderList.length <= (this.filteredCount + INITIAL_LOAD)) {
                this.totalCount = this.totalRiderList.length;
                this.filteredCount = this.totalCount;
                this.filteredRiderList = this.totalRiderList;
            } else {
                this.totalCount = this.totalRiderList.length;
                this.filteredCount = (this.filteredCount + INITIAL_LOAD)
                this.filteredRiderList = this.totalRiderList.slice(0, (this.filteredCount + INITIAL_LOAD));
            }
        }
    }


    renderedCallback() {
        Promise.all([
            loadStyle(this, loggingcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    createRelatedField() {
        return [{
            label: 'Related Field',
            value: this.relatedInputField
        }];
    }


    handleLogging(event) {
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performTableLogging(event, this.filteredRiderList, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);

        } else {
            getLoggingKey(this.pageRef).then(result => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performTableLogging(event, this.filteredRiderList, this.relatedInputField, this.columns, this.screenName, this.pageRef, this.createRelatedField(), this.loggingkey);
                }
            });
        }
    }
}