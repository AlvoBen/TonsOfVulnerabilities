/*
Function        : LWC to display health history .

Modification Log:
* Developer Name                  Date                         Description
****************************************************************************************************************************
* Atul Patil                    						 08/21/2023                User Story US4889468
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { getHealthHistoryData } from 'c/pharmacyHPIEIntegrationHum';
export default class PharmacyHpieHealthHistoryHum extends LightningElement {

    @track profileDetails = '';
    @track allergies = '';
    @api enterpriseId;
    @api userId;
    @api organization;
    @track loaded = false;

    connectedCallback() {
        Promise.all([this.getHealthHistoryData()]).then(result => {
            console.log(result);
        }).catch(error => {
            console.log(error);
        })
    }

    getHealthHistoryData() {
        return new Promise((resolve, reject) => {
            getHealthHistoryData(this.enterpriseId, this.userId, this.organization ?? 'HUMANA')
                .then(result => {
                    this.profileDetails = result;
                    this.passDataToChildComponents();
                }).then(() => {
                    this.loaded = true;
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    this.loaded = true;
                    reject(false);
                })
        });
    }


    passDataToChildComponents() {
        if (this.template.querySelector('c-pharmacy-health-conditions-hum') != null) {
            this.template.querySelector('c-pharmacy-health-conditions-hum').setProfileDetails(this.profileDetails);
        }
        if (this.template.querySelector('c-pharmacy-allergies-hum') != null) {
            this.template.querySelector('c-pharmacy-allergies-hum').setProfileDetails(this.profileDetails);
        }
    }
}