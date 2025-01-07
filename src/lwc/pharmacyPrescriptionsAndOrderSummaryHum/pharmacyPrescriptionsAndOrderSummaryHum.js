/*
LWC Name        : PharmacyPrescriptionsAndOrderSummaryHum.js
Function        : LWC to display pharmacy order tabs.

Modification Log:
* Developer Name                  Date                         Description
*
* Swapnali Sonawane               12/05/2022                   US- 3969790 Migration of the order queue detail capability
* Nirmal Garg						  07/27/2023	                US4902305
****************************************************************************************************************************/
import { LightningElement, wire, track, api } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import getPrescriptions from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeEpostMemberPrescription';
const PRIOR_MONTH = 18;
export default class PharmacyPrescriptionsAndOrderSummaryHum extends LightningElement {
    @track recId;
    @track entId;
    @track currentPageReference = null;
    @track urlStateParameters = null;
    @track accID;
    @api pharmacydemographicdetails;
    @api recordid;
    @api networkid;
    @api enterpriseid;
    @api omsdetails;
    @api payer;


    sScriptKey = "";
    @track prescriptiondata = {};

    connectedCallback() {
        this.getPrescriptionsDetails();
    }

    getPrescriptions() {
        let startdate = new Date();
        const me = this;
        startdate.setMonth(startdate.getMonth() - PRIOR_MONTH);
        let enddate = new Date();
        let sStartDate = (startdate.getMonth() + 1) + '/' + startdate.getDate() + '/' + startdate.getFullYear();
        let sEndDate = (enddate.getMonth() + 1) + '/' + enddate.getDate() + '/' + enddate.getFullYear();
        return new Promise(function (resolve, reject) {
            getPrescriptions({ memID: me.enterpriseid, scriptKey: me.sScriptKey, startDate: sStartDate, endDate: sEndDate, networkId: me.networkid, sRecordId: me.recordid })
                .then((result) => {
                    resolve(result);
                }).catch((error) => {
                    reject(error);
                });
        });
    }


    handlePrescRefresh() {
        this.getPrescriptionsDetails();
    }

    @api setDemographicsDetails(data) {
        this.pharmacydemographicdetails = data;
        if (this.template.querySelector('c-pharmacy-prescriptions-filters-hum') != null) {
            this.template.querySelector('c-pharmacy-prescriptions-filters-hum').setDemographicsDetails(this.pharmacydemographicdetails);
        }
        if (this.template.querySelector('c-pharmacy-order-summary-filter-hum') != null) {
            this.template.querySelector('c-pharmacy-order-summary-filter-hum').setDemographicsDetails(this.pharmacydemographicdetails);
        }
    }

    async getPrescriptionsDetails() {
        let startdate = new Date();
        startdate.setMonth(startdate.getMonth() - PRIOR_MONTH);
        let enddate = new Date();
        let sStartDate = (startdate.getMonth() + 1) + '/' + startdate.getDate() + '/' + startdate.getFullYear();
        let sEndDate = (enddate.getMonth() + 1) + '/' + enddate.getDate() + '/' + enddate.getFullYear();
        await getPrescriptions({ memID: this.enterpriseid, scriptKey: this.sScriptKey, startDate: sStartDate, endDate: sEndDate, networkId: this.networkid, sRecordId: this.recordid })
            .then(result => {
                this.prescriptiondata = JSON.parse(result);
                if (this.template.querySelector('c-pharmacy-prescriptions-filters-hum') != null) {
                    this.template.querySelector('c-pharmacy-prescriptions-filters-hum').setPrescriptions(this.prescriptiondata);
                }
                if (this.template.querySelector('c-pharmacy-order-summary-filter-hum') != null) {
                    this.template.querySelector('c-pharmacy-order-summary-filter-hum').setPrescriptionData(this.prescriptiondata);
                }
            }).catch(error => {
                console.log(error);
                if (this.template.querySelector('c-pharmacy-prescriptions-filters-hum') != null) {
                    this.template.querySelector('c-pharmacy-prescriptions-filters-hum').displayError();
                }
            })
    }
}