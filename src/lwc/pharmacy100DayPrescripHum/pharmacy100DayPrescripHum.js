/*
Function        : LWC to update the auto refill for prescription.

Modification Log:
* Developer Name                  Date                         Description
****************************************************************************************************************************
* Nirmal Garg                    07/18/2022                 	 Initial version US#5071365
* Vishal Shinde                  10/18/2023                      DF-8224
*****************************************************************************************************************************/
import { LightningElement, api, track } from 'lwc';
import { getAllMemberPlans, getMemberPlanDetails } from 'c/genericMemberPlanDetails';
import { searchRequestDTO } from './searchRequest';
import invokeMemElig100DayPrescripService from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeMemElig100DayPrescripService';
export default class Pharmacy100DayPrescripHum extends LightningElement {
    @api memberPlanId;
    @api accountId;
    @track memberPlan;
    @track prescripResult = '';
    @track serviceCall = false;
    @track loaded = false;

    handleClick() {
        this.serviceCall = true;
        this.loaded = false;
        this.getPlanDetails();
    }

    getPlanDetails() {
        if (this.memberPlanId) {
            getMemberPlanDetails(this.memberPlanId)
                .then(result => {
                    this.memberPlan = result;
                }).then(() => {
                    this.callService();
                }).catch(error => { 
                    console.log(error);
                    this.loaded = true;
                })
        } else {
            getAllMemberPlans(this.accountId)
                .then(result => {
                    this.memberPlan = result[0];
                }).then(() => {
                    this.callService();
                }).catch(error => {
                    console.log(error);
                    this.loaded = true;
                })
        }
    }

    callService() {
        let request = new searchRequestDTO(this.memberPlan);
        invokeMemElig100DayPrescripService({ request: JSON.stringify(request) })
            .then((result => {
                result=JSON.parse(result);
                if (result) {
                    this.prescripResult = result && result?.SearchResponse?.ResponseStatus?.Success ?
                        result?.SearchResponse?.Is100DaysPrescribed === true ? 'Y' : 'N' : 'No Data';
                } else {
                    this.prescripResult = 'No Data';
                }
                this.loaded = true;
            })).catch(error => {
                console.log(error);
                this.loaded = true;
                this.prescripResult = 'No Data';
            })
    }
}