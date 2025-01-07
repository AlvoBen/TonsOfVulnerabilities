/*
LWC Name        : displayLoggedInfoHum.js
Function        : LWC to display logged cases.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
*Nirmal Garg                     03/17/2022                     initial version - US - 3035939
*Kalyani Pachpol	             06/09/2022		                3149503
*Kalyani Pachpol                 07/28/2022                     US- 3613352
*Divya Bhamre                    07/28/2022                     US- 3581984
*Jonathan Dickinson              08/15/2022		                US- 3699864
*Swapnali Sonawane               08/23/2022                     US#3631288 Use purchaser plan object
* Divya Bhamre                    01/20/2023                    US - 4119977
* Aishwarya Pawar                03/01/2023                     US - 4286514
* Vishal Shinde                  06/09/2023                     US - 4542629
* Suraj Patil                    05/09/2023                     US#4542585 Dental Plan - Tooth History
* Raj Paliwal                    05/19/2023                     US: 4542567 Dental Plan Information
* Vishal Shinde                   05/22/2023                   US: 4542608 Dental Plan - Reimbursement tool link
* Nirmal Garg		              06/01/2023                   US - 4556179 - Dental Plan - Dental Waiting Periods
* Apurva Urkude		              06/05/2023                   US - 4654062
****************************************************************************************************************************/
import { api, LightningElement, track, wire } from 'lwc';
import getPBEResponse from '@salesforce/apexContinuation/Benefits_LC_HUM.invokePBEService';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { CurrentPageReference } from 'lightning/navigation';
import serviceerrormessage from '@salesforce/label/c.Lightning_ErrorMessage';
import { toastMsge } from "c/crmUtilityHum";
import { openMentor } from 'c/genericMentorLaunch';
import pbeErrorMsg from '@salesforce/label/c.BenefitPBEServiceError_Msg';
import pbeErrorMsg1 from '@salesforce/label/c.BenefitPBEServiceError_Msg1';
import { getMemberPlanDetails } from 'c/genericMemberPlanDetails';
import getswitchvalue from "@salesforce/apex/Benefits_LC_HUM.getswitchvalue";
import getswitchvalueSnapShot from "@salesforce/apex/Benefits_LC_HUM.getswitchvalueSnapShot";
import getswitchvaluePlanInformation from "@salesforce/apex/Benefits_LC_HUM.getswitchvaluePlanInformation";
import getswitchvalueReimbursement from "@salesforce/apex/Benefits_LC_HUM.getswitchvalueReimbursement";
import { invokePBEService, invokeMBEService, invokeGBEService } from 'c/benefitsServiceHelperHum';
import { updateTabLabel } from 'c/genericUpdateTabLabel';

export default class BenefitsTabContainerHum extends LightningElement {

    @track benefitType;
    @track accountId;
    @track memberName;
    @api recordId;
    @track PlanMember;
    @track MemberPlanData;
    @track PBEResponse = {};
    @api benefitSnapshotlist = {};
    labels = {
        serviceerrormessage,
        pbeErrorMsg,
        pbeErrorMsg1

    }

    @track platformCode;

    @track isMedical;
    @track isDental;
    @track memberId;
    @track IsAutoLaunchMentor = false;
    @track bIsCasPolicy = false;
    @track loaded = false;
    @track producttype;
    @track pbeService = false;
    @track policyExternalId;
    @track autoLogging = true;
    @track showloggingicon = true;
    @track showalerticon = true;
    @track subtype = 'Benefits and Accumulators';
    @track attachdesc = 'Policy ExternalID';
    @track productId;
    @track headerfields = null;
    @track message = '';
    @track serviceerror = false;
    @track toothhistoryswitch = false;
    @track SnapShotswitch = false;
    @track planInformationSwitch = false;
    @track ReimbursementSwitch = false;	
	
	@track gberesponse;
    @track mberesponse;
    @track gbeserviceerror = false;
    @track gbeerrormessage;
    @track mbeserviceerror = false;
    @track mbeerrormessage;
      @track sGroupNumber;

    connectedCallback(){
        this.getSnapshotValue();
    }

    callPBEService() {
        let today = new Date().toISOString().slice(0, 10);
        this.loaded = false;
        invokePBEService('initialsearch', { memberplanid: this.recordId, asofdate: '' }).then(result => {
            if (result?.serviceError) {
                this.serviceerror = true;
                this.message = result?.errorCode === '111111' ? this.labels.pbeErrorMsg
                    : result?.errorCode === '000305' ? this.labels.pbeErrorMsg1 : this.labels.serviceerrormessage;
                this.loaded = true;
                this.displayErrorMessage("pbe");
            } else {
                this.serviceerror = false
                this.PBEResponse = result;
                this.pbeService = true;
                if (this.PBEResponse) {
                    this.platformCode = this.PBEResponse?.PlatformCode ?? null;
                    this.productId = this.PBEResponse?.PackageInfo?.PackageId ?? '';
                    this.passDataToChildComponents("pbe");
                } else {
                    this.serviceerror = false;
                    this.displayErrorMessage("pbe");
                }
                this.loaded = true;
            }
        }).catch(error => {
            console.log(error);
            this.loaded = true;
            this.serviceerror = true;
            this.message = this.labels.serviceerrormessage;
            this.displayErrorMessage("pbe");
        });
    }

    passDataToChildComponents(name) {
        if (name) {
            switch (name?.toLowerCase()) {
                case "pbe":
                    if (this.template.querySelector('c-benefit-dental-snapshot-hum') != null) {
                        this.template.querySelector('c-benefit-dental-snapshot-hum').setPBEData(this.PBEResponse,
                            this.producttype, this.memberName, this.recordId);
                    }
                    if (this.template.querySelector('c-benefit-snapshot-hum') != null) {
                        this.template.querySelector('c-benefit-snapshot-hum').setPBEData(this.PBEResponse,
                            this.producttype, this.memberName, this.recordId);
                    }
                    if (this.template.querySelector('c-benefits-policy-rider-hum') != null) {
                        this.template.querySelector('c-benefits-policy-rider-hum').setPBEData(this.PBEResponse, this.memberName, this.isDental);
                    }
                    if (this.template.querySelector('c-benefits-search-hum') != null) {
                        this.template.querySelector('c-benefits-search-hum').setPBEData(this.PBEResponse, this.isMedical, this.isDental,
                            this.platformCode, this.productId);
                    }
                    if (this.template.querySelector('c-benefits-summary-tab-hum') != null) {
                        this.template.querySelector('c-benefits-summary-tab-hum').setPBEData(this.PBEResponse, this.recordId,
                            this.MemberPlanData);
                    }
                    break;
                case "gbe":
                    if (this.template.querySelector('c-benefits-summary-tab-hum') != null) {
                        this.template.querySelector('c-benefits-summary-tab-hum').setGBEData(this.gberesponse, this.recordId,
                            this.MemberPlanData);
                    }
                    break;
                case "mbe":
                    if (this.template.querySelector('c-benefits-summary-tab-hum') != null) {
                        this.template.querySelector('c-benefits-summary-tab-hum').setMBEData(this.mberesponse, this.recordId,
                            this.MemberPlanData);
                    }
                    if (this.template.querySelector('c-benefits-waiting-period-hum') != null) {
                        this.template.querySelector('c-benefits-waiting-period-hum').setMBEData(this.mberesponse);
                    }
                    break;
                case "header":
                    if (this.template.querySelector('c-generic-component-header') != null) {
                        this.template.querySelector('c-generic-component-header').getmemberId(this.accountId);
                    }
                    break;
            }
        }

    }

    displayErrorMessage(servicename) {
        if (servicename) {
            switch (servicename?.toLowerCase()) {
                case "pbe":
                    if (this.template.querySelector('c-benefit-snapshot-hum') != null) {
                        this.template.querySelector('c-benefit-snapshot-hum').displayErrorMessage(this.serviceerror, this.message, this.producttype);
                    }
                    if (this.template.querySelector('c-benefits-policy-rider-hum') != null) {
                        this.template.querySelector('c-benefits-policy-rider-hum').displayErrorMessage(this.serviceerror, this.message);
                    }
                    if (this.template.querySelector('c-benefits-search-hum') != null) {
                        this.template.querySelector('c-benefits-search-hum').displayErrorMessage(this.serviceerror, this.message);
                    }
                    if (this.template.querySelector('c-benefits-summary-tab-hum') != null) {
                        this.template.querySelector('c-benefits-summary-tab-hum').displayErrorMessage(this.serviceerror, this.message);
                    }
                    break;
                case "gbe":
                    if (this.template.querySelector('c-benefits-summary-tab-hum') != null) {
                        this.template.querySelector('c-benefits-summary-tab-hum').displayErrorMessage(this.serviceerror, this.message);
                    }
                    break;
                case "mbe":
                    if (this.template.querySelector('c-benefits-summary-tab-hum') != null) {
                        this.template.querySelector('c-benefits-summary-tab-hum').displayErrorMessage(this.serviceerror, this.message);
                    }
                    if (this.template.querySelector('c-benefits-waiting-period-hum') != null) {
                        this.template.querySelector('c-benefits-waiting-period-hum').displayErrorMessage(this.serviceerror, this.message);
                    }
                    break;

            }
        }

    }

    addHeaderfields() {
        this.headerfields = [];
        this.headerfields.push({
            label: 'Date of Birth',
            value: this.MemberPlanData.Member.Birthdate__c,
            cssclass: 'slds-col slds-size_1-of-4',
            style: '',
        });
        let mailingAddressObj = this.MemberPlanData.Member.PersonMailingAddress;

        let sMailingAddress = mailingAddressObj ? `${mailingAddressObj?.street ?? ''},\n${mailingAddressObj?.city ?? ''}, ${mailingAddressObj?.stateCode ?? ''} ${mailingAddressObj?.postalCode ?? ''}\n${mailingAddressObj?.countryCode ?? ''}` : '';

        this.headerfields.push({
            label: 'Mailing Address',
            value: sMailingAddress,
            cssclass: 'slds-col slds-size_1-of-4',
            style: 'white-space: pre',
        });
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference && currentPageReference?.attributes &&
            currentPageReference?.attributes?.attributes && currentPageReference?.attributes?.attributes?.Id) {
            this.recordId = currentPageReference.attributes.attributes.Id;
            if (this.recordId)
                this.getMemberPlanData();
        }
    }

    getSnapshotValue(){
        getswitchvalueSnapShot()
        .then(result => {
            this.SnapShotswitch=result.IsON__c;
        })
        .catch(err => {
            console.log("Error occured - " + err);
            this.SnapShotswitch = false;
        });
    }

    @wire(getswitchvalue)
    getswitchvalue({ error, data }) {
        if (data) {
            this.toothhistoryswitch = data.IsON__c;
        }
        else if(error){
            this.toothhistoryswitch = false;
        }
    }

   

    @wire(getswitchvaluePlanInformation)
    getswitchvaluePlanInformation({ error, data }) {
        if (data) {
            this.planInformationSwitch = data.IsON__c;
        }
        else if(error){
            this.planInformationSwitch = false;
        }
    }

    @wire(getswitchvalueReimbursement)
    getswitchvalueReimbursement({ error, data }) {
        if (data) {
            this.ReimbursementSwitch = data.IsON__c;
        }
        else if(error){
            this.ReimbursementSwitch = false;
        }
    }

    getMemberPlanData() {
        getMemberPlanDetails(this.recordId)
            .then(result => {
                if (result && Array.isArray(result) && result.length > 0) {
                    this.MemberPlanData = result[0];
                    this.openMentor();
                    this.PlanProductType = this.MemberPlanData.Product__c;
                    this.setBenefitType();
                    this.addHeaderfields();
                    this.memberName = this.MemberPlanData.Name;
                    this.accountId = this.MemberPlanData.MemberId;
		     this.sGroupNumber= this.MemberPlanData.sGroupNumber;
                    this.policyExternalId = this.MemberPlanData?.Plan?.Purchaser_Plan_External_ID__c ?? '';
                    this.passDataToChildComponents('header');
                    this.updateTabLabel();
                }
                this.callPBEService();
            })
            .catch(err => {
                console.log("Error occured - " + err);
                this.loaded = true;
                toastMsge('', this.labels.serviceerrormessage, 'error', 'dismissible');
            });
    }

    updateTabLabel() {
        setTimeout(() => {
            updateTabLabel(this.benefitType ? this.benefitType : 'Benefits', 'standard:record', 'Benefits');
        }, 1000)
    }
	
	openMentor() {
        try {
            openMentor(this.MemberPlanData, 'data');
        } catch (error) {
            this.loaded = true;
            console.log(error);
            toastMsge('', this.labels.serviceerrormessage, 'error', 'dismissible');
        }
    }

    setBenefitType() {
        switch (this.PlanProductType) {
            case "MED":
                this.benefitType = 'Medical Benefits';
                this.producttype = 'Medical';
                this.isMedical = true;
                break;
            case "DEN":
                this.benefitType = "Dental Benefits";
                this.producttype = 'Dental';
                this.isDental = true;
                break;
        }
    }
	
	callGBEService() {
        invokeGBEService(this.recordId, '').then(result => {
            this.gbeservicecall = true;
            this.gberesponse = result;
            this.passDataToChildComponents("gbe")
        }).catch(error => {
            console.log(error);
            this.loaded = true;
            this.serviceerror = true;
            this.message = this.labels.serviceerrormessage;
            this.displayErrorMessage("gbe");
        })
    }

    callMBEService() {
        invokeMBEService(this.recordId, '').then(result => {
            this.mbeserviceall = true;
            this.mberesponse = result;
            this.passDataToChildComponents("mbe");
        }).catch(error => {
            console.log(error);
            this.loaded = true;
            this.serviceerror = true;
            this.message = this.labels.serviceerrormessage;
            this.displayErrorMessage("mbe");
        })
    }

}