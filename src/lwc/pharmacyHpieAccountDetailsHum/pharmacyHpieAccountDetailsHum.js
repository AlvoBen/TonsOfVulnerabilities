/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    						 07/18/2022                user story 4861950, 4861945
* Atul Patil                    						 07/28/2023                user story 4861950, 4861945
* Jagadeesh Kureti                                       06/SEP/2023               User Story 5012565: T1PRJ0870026 MF27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Details tab - Account details Edit fields
*****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { getFormatDate } from 'c/crmUtilityHum';
import { getLocaleDate, sortTable } from 'c/crmUtilityHum';
import { getAllMemberPlans } from 'c/genericMemberPlanDetails';
import { toastMsge } from "c/crmUtilityHum";
import updateCapTypeHpieService from '@salesforce/apex/Pharmacy_HPIE_LC_HUM.updateCapType';
import { updateEmailAddress, updatePrimaryPhone, updateAlternatePhone } from "c/pharmacyHPIEIntegrationHum"
import { RefreshEvent } from "lightning/refresh";
import { CurrentPageReference } from 'lightning/navigation';
export default class PharmacyHpieAccountDetailsHum extends LightningElement {
    @api financeDetails;
    @api profileDetails;
    @api demographicDetails;
    @api preferenceDetails;
    @api accountId;
    @api recordId;
    @api enterpriseId;
    @api userId;
    @api userProfile;
    @track memberProfileDeatils;
    @track isEditable = false;
    @track memberFinanceDetails;
    @track financeDetailsLoading = true;
    @track bgColor = '';
    @track memberPrimayPhone = '';
    @track memberAlternatePhone;
    @track memberPermanentAddress = '';
    @track memberCapType = '';
    @track memberConsent = '';
    @track isEmailReadyOnly;
    @track effectiveDate;
    @track teminationDate;
    @track planType;
    @track policyList
    @track capType;
    @track capTypes = [{ label: 'S - Safety', value: 'S' }, { label: 'E - Easy', value: 'E' }]
    @track updateTransactions = [];
    @track declinedDisabled = true;
    @track declinedEmail;
    @track profileLoaded = false;
    @track addressLoaded = false;
    @track financeLoaded = false;
    @track preferenceLoaded = false;
    @track accountBalance;
    @track accountLimit;
    @track isMemberConsentRequired = false;

    connectedCallback() {
        this.getPolicyDetails();
        this.processMemberProfileData();
    }

    processMemberProfileData() {
        this.memberProfileDeatils = {
            firstName: this.profileDetails?.FirstName ?? '',
            lastName: this.profileDetails?.LastName ?? '',
            email: this.profileDetails?.Email ?? '',
            middleInitial: this.profileDetails?.MiddleInitial ?? '',
            dateOfBirth: this.profileDetails?.DateofBirth ? getFormatDate(this.profileDetails?.DateofBirth) : ''
        }
        this.addOldValues('memberEmail', this.memberProfileDeatils.email);
    }

    addOldValues(type, value) {
        this.updateTransactions.push({
            name: type,
            oldValue: value
        })
    }

    @api setProfileDetails(data) {
        this.profileDetails = data;
        this.processMemberProfileData();
        this.profileLoaded = true;
    }

    @api setFinanceDetails(data) {
        this.financeDetails = data;
        this.accountBalance = this.financeDetails?.accountOutstandingBalance ?? '';
        this.accountLimit = this.financeDetails?.accountCreditLimit ?? ''
        this.setBgColor();
        this.financeLoaded = false;
    }


    setBgColor() {
        this.bgColor = parseFloat(this.financeDetails?.accountOutstandingBalance) > 0 ? 'slds-box style-red' :
            parseFloat(this.financeDetails?.accountOutstandingBalance) < 0 ? 'slds-box style-green'
                : parseFloat(this.financeDetails?.accountOutstandingBalance) === 0.0 ? 'slds-box style-grey' : '';
    }

    @api setDemographicDetails(data) {
        this.demographicDetails = data;
        this.setMemberDemographicsData();
        this.addressLoaded = true;
    }

    setMemberDemographicsData() {
        this.memberPrimaryPhone = this.demographicDetails && this.demographicDetails?.PrimaryPhone
            && Array.isArray(this.demographicDetails?.PrimaryPhone)
            && this.demographicDetails?.PrimaryPhone?.length > 0 ?
            this.demographicDetails?.PrimaryPhone[0]?.z0number ?? '' : '';
        this.memberAlternatePhone = this.demographicDetails && this.demographicDetails?.AlternatePhone
            && Array.isArray(this.demographicDetails?.AlternatePhone)
            && this.demographicDetails?.AlternatePhone?.length > 0
            ? this.demographicDetails?.AlternatePhone[0]?.z0number ?? '' : '';
        this.memberPermanentAddress = this.demographicDetails && this.demographicDetails?.Addresses
            && Array.isArray(this.demographicDetails?.Addresses)
            && this.demographicDetails?.Addresses?.length > 0 ?
            this.demographicDetails?.Addresses.find(k => k?.z0type?.description?.toLowerCase() === 'home') : null;
        this.addOldValues('memberPrimaryPhone', this.memberPrimaryPhone);
        this.addOldValues('memberAlternatePhone', this.memberAlternatePhone);
    }

    get permanentHomeStreet() {
        return `${this.memberPermanentAddress?.addressLine1 ?? ''} ${this.memberPermanentAddress?.addressLine2 ?? ''}`
    }

    @api setPreferenceDetails(data) {
        this.preferenceDetails = data;
        this.capType = this.preferenceDetails?.preference?.capType?.code ?? '';
        this.isMemberConsentRequired = this.preferenceDetails?.preference?.consents?.memberConsentRequired;
        this.memberConsent = this.preferenceDetails?.preference?.consents?.memberConsentRequired === true
            && this.preferenceDetails?.preference?.consents?.consentBeginDate
            && this.preferenceDetails?.preference?.consents?.consentEndDate
            && new Date(this.preferenceDetails?.preference?.consents?.consentBeginDate).getTime() <= new Date().getTime()
            && new Date(this.preferenceDetails?.preference?.consents?.consentEndDate) >= new Date().getTime()
            ? `Approved ${getFormatDate(this.preferenceDetails?.preference?.consents?.consentBeginDate)}`
            : 'Request Consent for each Order';
        this.addOldValues('capType', this.capType);
        this.declinedDisabled = true;
        this.declinedEmail = this.preferenceDetails?.preference?.emailDeclined?.status
            && this.preferenceDetails?.preference?.emailDeclined?.status === true ? true : false;
        this.isEmailReadyOnly = !this.declinedEmail
        this.preferenceLoaded = true;
    }

    editMemberDetails() {
        this.isEditable = true;
        this.declinedDisabled = false;
    }

    displayStatus(event) {
        if (event.target.checked === true) {
            this.isEmailReadyOnly = false;
            this.declinedEmail = true;
        }
        if (event.target.checked === false) {
            this.isEmailReadyOnly = true;
            this.declinedEmail = false;
        }
    }

    getPolicyDetails() {
        Promise.all([this.getPolicyData()]).then(result => {
            console.log(result);
        }).catch(error => {
            console.log(error);
        });
    }

    getPolicyData() {
        return new Promise((resolve, reject) => {
            getAllMemberPlans(this.accountId).then(result => {
                if (result && Array.isArray(result) && result?.length > 0) {
                    this.effectiveDate = result[0]?.EffectiveFrom ? getLocaleDate(result[0].EffectiveFrom) : '';
                    this.teminationDate = result[0]?.EffectiveTo ? getLocaleDate(result[0].EffectiveTo) : '';
                    this.planType = result[0]?.Product_Type__c ?? '';
                }
                resolve(true);
            }).catch(error => {
                console.log(error);
                reject(error);
            })
        });
    }

    handleChange(event) {
        switch (event?.target?.name) {            
            case 'capType':
                this.addNewValues(event?.target?.name, event?.detail?.value);
                break;
            case 'memberPrimaryPhone':
                this.addNewValues(event?.target?.name, event?.detail?.value);
                break;
            case 'memberAlternatePhone':
                this.addNewValues(event?.target?.name, event?.detail?.value);
                break;
            case 'memberEmail':
                this.addNewValues(event?.target?.name, event?.detail?.value);
                break;
        }
    }

    addNewValues(type, newValue) {
        this.updateTransactions.forEach(k => {
            if (k?.name?.toLowerCase() === type?.toLowerCase()) {
                k.newValue = newValue;
                k.changed = k?.oldValue != newValue && newValue && newValue?.length > 0 ? true : false;
            }
        })
        console.log(this.updateTransactions);
    }

    addAfterSaveValues(type, sameOldAndNewValue) {
        this.updateTransactions.forEach(k => {
            if (k?.name?.toLowerCase() === type?.toLowerCase()) {
                k.newValue = sameOldAndNewValue;
                k.oldValue = sameOldAndNewValue;
                k.changed = false;
            }
        })
    }

    handleCancel() {
        this.isEditable = false;
    }

    handleSaveClick() {
        this.updateTransactions.forEach(k => {
            if (k?.changed) {
                switch (k?.name?.toLowerCase()) {
                    case 'captype':
                        this.callUpdateCapType(k.newValue);
                        break;
                    case 'memberemail':
                        this.callUpdateEmailAddress(k.newValue);
                        break;
                    case 'memberprimaryphone':
                        this.callUpdatePrimaryPhone(k.newValue);
                        break;
                    case 'memberalternatephone':
                        this.callUpdateAlternatePhone(k.newValue);
                        break;
                }
            }
        })
        this.dispatchEvent(new RefreshEvent());
        this.isEditable = false;
    }

    callUpdatePrimaryPhone(primaryPhoneValue) {
        this.memberPrimaryPhone = primaryPhoneValue;
        updatePrimaryPhone(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', primaryPhoneValue);
        this.addAfterSaveValues('memberPrimaryPhone', primaryPhoneValue);   
        toastMsge('Success', 'Primary Phone Updated', 'info', 'dismissible');
    }

    callUpdateAlternatePhone(alternatePhoneValue) {
        this.memberAlternatePhone = alternatePhoneValue;
        this.memberMobile = alternatePhoneValue;
        updateAlternatePhone(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', alternatePhoneValue);
        this.addAfterSaveValues('memberAlternatePhone', alternatePhoneValue);  
        toastMsge('Success', 'Alternate Phone Updated', 'info', 'dismissible');
    }

    callUpdateCapType(capTypeValue) {
        this.capType = capTypeValue;        
        new Promise((resolve, reject) => {
            this.preferenceLoaded = false;
            updateCapTypeHpieService({ personId: this.enterpriseId, userId: this.userId, requestedTime: new Date().toISOString(), organization: this.organization ?? 'HUMANA', capType: capTypeValue })            
                .then(result => {
                    resolve(result);
                    const capTypeEvent = new CustomEvent('captypeupdate', { detail: capTypeValue, bubbles: true, composed: true });
                    this.dispatchEvent(capTypeEvent);   
                    this.preferenceLoaded = true;  
                    this.addAfterSaveValues('capType', capTypeValue);            
                    toastMsge('Success', 'Cap Type Updated', 'info', 'dismissible');                    
                }).catch(error => {
                    reject(error);
                    toastMsge('Failed', 'Failed to Update Cap Type', 'error', 'dismissible');
                    this.preferenceLoaded = true;
                })
        });
    }

    callUpdateEmailAddress(emailIdValue) {
        this.memberProfileDeatils.email = emailIdValue;
        updateEmailAddress(this.enterpriseId, this.userId, this.organization ?? 'HUMANA', emailIdValue);
        this.addAfterSaveValues('memberEmail', emailIdValue); 
        toastMsge('Success', 'Email Updated', 'info', 'dismissible');
    }
}