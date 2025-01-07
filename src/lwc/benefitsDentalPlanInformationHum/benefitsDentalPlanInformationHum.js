/*******************************************************************************************************************************
LWC JS Name : benefitsDentalPlanInformationHum.js
Function    : This JS serves as Controller to benefitsDentalPlanInformationHum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Raj Paliwal                                           05/19/2023                    US: 4542567 Dental Plan Information
*********************************************************************************************************************************/
import { LightningElement , api, wire, track} from 'lwc';
import getGBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokeGBEService';
import getMBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokeMBEService';
import BenefitsServiceError from '@salesforce/label/c.BenefitsServiceError';
import BenefitMBEServiceError from '@salesforce/label/c.BenefitMBEServiceError';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { CurrentPageReference } from 'lightning/navigation';
import {performLogging, checkloggingstatus, getLoggingKey} from 'c/loggingUtilityHum';

export default class BenefitsDentalPlanInformationHum extends LightningElement {

    @api recordid;
    @api memberplanname;
    @track GBEResponse = {};
    @track MBEResponse = {};
    @track MBEData = {};
    @track GBEData = {};
    @track serviceerror;
    @track loaded=false;
    autoLogging = true;
    @track message;
    labels = {
        BenefitsServiceError,
        BenefitMBEServiceError
    };

    @wire(CurrentPageReference)
    pageRef;

    get generateLogId(){
        return Math.random().toString(16).slice(2);
    }

    connectedCallback() {
        this.getGBEData();
        this.asyncGetMBEData();
        if(this.autoLogging){
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
            });
        }
    }

    renderedCallback() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch((error) => {});
    }

    async getGBEData() {
        await getGBEResponse({ memberPlanId: this.recordid, asOfDate: '' })
            .then(result => {
                this.GBEResponse = JSON.parse(result);
                if (this.GBEResponse) {
                    this.GBEData.sOpenEnrolEnddate = this.GBEResponse?.sOpenEnrolEnddate ?? '';
                    this.GBEData.sOpenEnrolBegindate = this.GBEResponse?.sOpenEnrolBegindate ?? '';
                    this.GBEData.sGroupEnrollmentCount = this.GBEResponse?.sGroupEnrollmentCount ?? '';
                    this.GBEData.sHourlyReq = this.GBEResponse?.sHourlyReq ?? '';
                }
            })
            .catch(err => {
                console.log("Error occured in getGBEData- " + JSON.stringify(err));
            });
    }

    async asyncGetMBEData() {
        await this.getMBEData().then(result => {
            if (result != null) {
                try {
                    if (result && result === 'Code_83.1.1') {
                        this.serviceerror = true;
                        this.message = this.labels.BenefitMBEServiceError;
                    } else {
                        this.MBEResponse = JSON.parse(result);
                        if (this.MBEResponse) {
                            this.MBEData.sOriginalEffectiveDate = this.MBEResponse?.sOriginalEffectiveDate ?? '';
                            let sEnrollmentInfo = this.MBEResponse?.EnrollmentInfo ?? '';
                            if(sEnrollmentInfo && sEnrollmentInfo != ''){
                                this.MBEData.sEnrollmentType = sEnrollmentInfo?.EnrollmentType ?? '';
                                this.MBEData.sDateofHire = sEnrollmentInfo?.DateofHire ?? '';
                                this.MBEData.sTimelyIndicator = sEnrollmentInfo?.TimelyIndicator ?? '';
                            }
                            let sPriorCarrier = this.MBEResponse?.EnrollmentInfo?.PriorCarrier ?? '';
                            if(sPriorCarrier && sPriorCarrier != ''){
                                this.MBEData.sCarrierName = sPriorCarrier?.CarrierName ?? '';
                                this.MBEData.sEffectiveDate = sPriorCarrier?.EffectiveDate ?? '';
                                this.MBEData.sEndDate = sPriorCarrier?.EndDate ?? '';
                            }
                            let sIndicator = this.MBEResponse?.EnrollmentInfo?.PriorCarrier?.IndicatorList?.Indicator ?? '';
                            if(sIndicator && sIndicator != ''){
                                sIndicator.forEach(element => {
                                    if(element.Name === "PriorCoverageIndicator"){
                                        this.MBEData.sPriorCoverageIndicator = element.Value?element.Value:'';
                                    }
                                    else if(element.Name === "PriorOrthoIndicator"){
                                        this.MBEData.sPriorOrthoIndicator = element.Value?element.Value:'';
                                    }
                                    else if(element.Name === "IncompleteIndicator"){
                                        this.MBEData.sIncompleteIndicator = element.Value?element.Value:'';
                                    }
                                });
                            }
                        }
                    }
                } catch (error) {
                    this.serviceerror = true;
                    this.message = this.labels.BenefitsServiceError;
                }
                this.loaded = true;
            }
        }).catch(error => {
            this.loaded = true;
            this.serviceerror = true;
            this.message = this.labels.BenefitsServiceError;
            console.log('Error in getMBEData : '+JSON.stringify(error));
        })
    }

    async getMBEData() {
        return new Promise((resolve, reject) => {
            getMBEResponse({ memberPlanId: this.recordid, asOfDate: '' })
                .then(result => {
                    resolve(result);
                }).catch(error => {
                    reject(error);
                })
        })
    }

    handleLogging(event) {
        let sectionName= event.currentTarget.hasAttribute('data-section') ? event.currentTarget.getAttribute('data-section'):'';
        if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
            performLogging(
                event,
                this.createRelatedField(),
                'Dental Benefits Plan Information - '+sectionName,
                this.loggingkey,
                this.pageRef
            );
        } else {
            getLoggingKey(this.pageRef).then((result) => {
                this.loggingkey = result;
                if (this.loggingkey && checkloggingstatus(this.loggingkey)) {
                    performLogging(
                        event, 
                        this.createRelatedField(),
                        'Dental Benefits Plan Information - '+sectionName,
                        this.loggingkey,
                        this.pageRef 
                    );
		        }
            });
        }	
    }
	
	createRelatedField() {
        return [
            {   label: 'Plan Member ID',
                value: this.memberplanname
            }
        ]
    }
}