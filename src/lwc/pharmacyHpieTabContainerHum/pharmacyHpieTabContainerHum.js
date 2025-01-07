/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                    07/18/2022                 user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient APIUS-3975339 Change - RTI Icon Display Logic
* Atul Patil                    07/28/2023                  user story 4861950: T1PRJ0870026 - HPIE/CRM - C12 Mail Order Management - Pharmacy - "Details" Tab - Lightning - Patient API
* Vishal Shinde                 08/28/2023                  US- 4833055-User Story 4833055 Mail Order Management: Pharmacy - Iconology- Authorization (Lightning)
* Jagadeesh Kureti              06/SEP/2023                 User Story 5012565: T1PRJ0870026 MF27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Details tab - Account details Edit fields
* Jonathan Dickinson			 09/04/2023				    User Story 4999697: T1PRJ0870026 MF27456 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy - Finance tab - Lightning - Edit Credit Card, One time payment
* Jonathan Dickinson			 09/22/2023				    User Story 5061288: T1PRJ0870026   MF 27406 HPIE/CRM - SF Tech- Mail Order Management - Pharmacy -  Details tab - Address section
* Jonathan Dickinson			 10/09/2023				    DF-8195
* Vishal Shinde                  10/10/2023                 User Story 5002422- Mail Order Management; Pharmacy - identify Error Messaging and parameters (Lightning)
* Pinky Vijur                    10/19/2023                 DF-8227 Regression - Lightning - Warning message not displayed for demographics not verified  on MOP page
* Swapnali Sonawane              10/23/2023                 US - 5058187 Pharmacy Edit Order
* Jonathan Dickinson             03/05/2024                 User Story 4835317: T1PRJ0865978 - MF 27481 - C12; Mail Order Management; Pharmacy - Mail Order Pharmacy page gap- Member Consent (Lightning)
*****************************************************************************************************************************/
import { LightningElement, wire, api, track } from 'lwc';
import { publish, createMessageContext } from 'lightning/messageService';
import { getFinanceDetails, getDemographicsDetails, getProfileDetails, getPreference } from 'c/pharmacyHPIEIntegrationHum';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import getAuthorizations from '@salesforce/apexContinuation/PharmacyBenefits_LC_HUM.invokeGetpAuthService';
import pharmacyMemberNotFoundError from '@salesforce/label/c.pharmacyMemberNotFoundError';
import pharmacyServiceError from '@salesforce/label/c.pharmacyGenericError';
import { CurrentPageReference } from 'lightning/navigation';
export default class PharmacyHpieTabContainerHum extends LightningElement {
    @api accData;
    @track financeDetails;
    @track demographicsDetails;
    @track profileDetails;
    @track preferenceDetails;
    @track accountId;
    @track enterpriseId;
    @track userId;
    @track userProfile;
    @track pharmacyAuthorizations ='';
    @track organization;
    @track serviceError = false;
    @track memberNotFoundError = false;
    @track errorMessage;
    @track bPharmacyAuth =false;
    @track pharmAuthServiceError = false;
    @track financeServiceError = false;

    messageContext = createMessageContext();

    callServices() {
        Promise.all([this.getProfileData(), this.getFinanceData(), this.getPrefereceData(), this.getPharmacyAuthorizations(), this.getDemographicsData()])
            .then(result => {
                console.log(result);
            }).catch(error => {
                console.log(error);
            })
    }


    getProfileData() {
        return new Promise((resolve, reject) => {
            getProfileDetails(this.enterpriseId, this.userId, this.organization ?? 'HUMANA').then(result => {
                if (result && Object.keys(result)?.length > 0) {
                    this.profileDetails = result;
                    this.fireEventToHighlightedPanel('profile');
                    this.accData = this.profileDetails?.AccountId;
                    this.passDataToChildComponents([{
                        name: 'details',
                        data: 'profile'
                    }]);
                }
                resolve(true);
            }).catch(error => {
                console.log(error);
                this.fireEventToHighlightedPanel('profile', true);
                reject(false);
            })
        });
    }

    getFinanceData() {
        return new Promise((resolve, reject) => {
            getFinanceDetails(this.enterpriseId, this.userId, this.organization ?? 'HUMANA')
                .then(result => {
                    this.financeDetails = result;
                    this.fireEventToHighlightedPanel('finance');
                    this.passDataToChildComponents([{
                        name: 'details',
                        data: 'finance'
                    }, {
                        name: 'prescriptions',
                        data: 'finance'
                    }, {
                        name: 'financetab',
                        data: 'finance' 
                    }]);
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    this.financeServiceError= true;
                    this.fireEventToHighlightedPanel('finance', true);
                    reject(false);
                })
        });
    }

    getDemographicsData(refreshHighlightPanel = true) {
        return new Promise((resolve, reject) => {
            getDemographicsDetails(this.enterpriseId, this.userId, this.organization ?? 'HUMANA')
                .then(result => {
                    this.demographicsDetails = result;
                    if (refreshHighlightPanel) {
                        this.fireEventToHighlightedPanel('address');
                    }
                    this.passDataToChildComponents([{
                        name: 'details',
                        data: 'demographics'
                    }, {
                        name: 'prescriptions',
                        data: 'demographics'
                    }]);
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    this.fireEventToHighlightedPanel('address', true);
                    reject(false);
                })
        });
    }


    getPharmacyAuthorizations() {
        return new Promise((resolve, reject) => {
            getAuthorizations({ sMemID: this.enterpriseId })
                .then(result => {
                    if (result && result != 'null' && result?.length > 0) {
                        this.pharmacyAuthorizations = JSON.parse(result);
                        this.passDataToChildComponents([{
                            name: 'authorizations',
                            data: 'authorizations'
                        }])
                        this.checkForPharmacyAuth();
                    }
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    this.pharmacyAuthorizations = null;
                    this.pharmAuthServiceError = true
                    reject(false);
                })
        });
    }


    getPrefereceData() {
        return new Promise((resolve, reject) => {
            getPreference(this.enterpriseId, this.userId, this.organization ?? 'HUMANA').then(result => {
                this.preferenceDetails = result;
                this.passDataToChildComponents([{
                    name: 'details',
                    data: 'preference'
                }, {
                    name: 'prescriptions',
                    data: 'preference'
                }]);
                resolve(true);
            }).catch(error => {
                console.log(error);
                reject(false);
            })
        });
    }

    displayErrorMessage() {
        this.errorMessage = this.memberNotFoundError ? pharmacyMemberNotFoundError
            : this.serviceError ? pharmacyServiceError
                : '';
    }

    passDataToChildComponents(components) {
        if (components && Array.isArray(components) && components?.length > 0) {
            components.forEach(k => {
                switch (k?.name?.toLowerCase()) {
                    case "details":
                        switch (k?.data?.toLowerCase()) {
                            case 'profile':
                                if (this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum').setProfileDetails(this.profileDetails);
                                }
                                break;
                            case 'finance':
                                if (this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum').setFinanceDetails(this.financeDetails);
                                }
                                break;
                            case 'demographics':
                                if (this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum').setDemographicDetails(this.demographicsDetails);
                                }
                                break;
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-details-tab-container-hum').setPreferenceDetails(this.preferenceDetails);
                                }
                                break;

                        }
                        break;
                    case 'prescriptions':
                        switch (k?.data?.toLowerCase()) {
                            case 'preference':
                                if (this.template.querySelector('c-pharmacy-hpie-prescription-order-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-prescription-order-tab-container-hum').setPreferenceDetails(this.preferenceDetails);
                                }
                                break;
                            case 'demographics':
                                if (this.template.querySelector('c-pharmacy-hpie-prescription-order-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-prescription-order-tab-container-hum').setDemographicsDetails(this.demographicsDetails);
                                }
                                break;
                            case 'finance':
                                if (this.template.querySelector('c-pharmacy-hpie-prescription-order-tab-container-hum') != null) {
                                    this.template.querySelector('c-pharmacy-hpie-prescription-order-tab-container-hum').setFinanceDetails(this.financeDetails);
                                }
                        }break;
                    case 'authorizations':
                        switch (k?.data?.toLowerCase()) {
                            case 'authorizations':
                                if (this.template.querySelector('c-pharmacy-benefits-and-authorizations-tab-hum') != null) {
                                    this.template.querySelector('c-pharmacy-benefits-and-authorizations-tab-hum').setAuthorizationDetails(this.pharmacyAuthorizations);
                                }
                                break;
                        }
                }
            })
        }
    }  

    

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.state;
            this.setParametersBasedOnUrl();
        }
    }

    setParametersBasedOnUrl() {
        this.accountId = this.urlStateParameters?.c__AccountID ?? null;
        this.recordId = this.urlStateParameters?.c__AccountID ?? null
        this.enterpriseId = this.urlStateParameters?.c__enterpriceID ?? '';
        this.userId = atob(this.urlStateParameters?.c__userId) ?? '';
        this.userProfile = atob(this.urlStateParameters?.c__userProfile).replace('_', ' ') ?? '';
        this.callServices();
    }

    

    checkForPharmacyAuth() {
        if (this.pharmacyAuthorizations && this.pharmacyAuthorizations?.GetPriorAuthStatusHistoryResponse
            && this.pharmacyAuthorizations?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails
            && Array.isArray(this.pharmacyAuthorizations?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails)
            && this.pharmacyAuthorizations?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails?.length > 0) {
            this.pharmacyAuthorizations?.GetPriorAuthStatusHistoryResponse?.PriorAuthDetails.forEach(k => {
                if (k && k?.AgadiaAuthDetails && Array.isArray(k?.AgadiaAuthDetails) && k?.AgadiaAuthDetails?.length > 0) {
                    let todayDate = new Date();
                    k?.AgadiaAuthDetails.forEach(t => {
                        let requestedDate = t?.EOCCreationDate ? new Date(t?.EOCCreationDate) : null;
                        let timeDifference = todayDate.getTime() - requestedDate.getTime();
                        let differenceInDays = Math.ceil(timeDifference / (1000 * 3600 * 24));
                        if (differenceInDays <= 30) {
                            this.bPharmacyAuth = true;
                            return;
                        }
                    })
                }
            })
        }
        this.fireEventToHighlightedPanel('authorizationIcon');
    }

    fireEventToHighlightedPanel(type, error = false) {
        let message;
        if (type) {
            switch (type?.toLowerCase()) {
                case 'profile':
                    message = {
                        MessageName: type,
                        payload: this.getMemberProfilePayload(),
                        serviceError: error
                    }
                    break;
                case 'finance':
                    message = {
                        MessageName: type,
                        payload: this.getMemberFinancePayload(),
                        serviceError: error
                    }
                    break;
                case 'address':
                    message = {
                        MessageName: type,
                        payload: this.getMemberAddressPayload(),
                        serviceError: error
                    }
                    break;
                case 'authorizationicon':
                    message = {
                        MessageName: type,
                        payload: this.authorizationPayload(),
                        serviceError: error
                    }
                    break;
            }
        }
        publish(this.messageContext, humanaPharmacyLMS, message);
    }

    getMemberAddressPayload() {
        return {
            shippingAddress: this.demographicsDetails && this.demographicsDetails?.Addresses
                && Array.isArray(this.demographicsDetails?.Addresses) && this.demographicsDetails?.Addresses?.length > 0
                ? this.demographicsDetails?.Addresses.find(k => k?.z0type?.code === '11') : null
        }
    }

    getMemberFinancePayload() {
        return {
            accountBalance: this.financeDetails?.accountOutstandingBalance ?? '',
            accountLimit: this.financeDetails?.accountCreditLimit ?? '',
            creditCardExpired: this.checkExpiredCreditCard(),
            creditCardExpiring: this.checkExpiringCreditCard()
        }
    }

    checkExpiringCreditCard() {
        let currentdate = new Date();
        let bCreditCardExpiring = false;
        if (this.financeDetails && this.financeDetails?.paymentCards && Array.isArray(this.financeDetails?.paymentCards)
            && this.financeDetails?.paymentCards?.length > 0) {
            let activeCards = this.financeDetails.paymentCards.filter(k => k?.active === true);
            if (activeCards && Array.isArray(activeCards) && activeCards?.length > 0) {
                activeCards.forEach(card => {
                    let months;
                    let expiMonth = card.expirationMonth;
                    let expiYear = card.expirationYear;
                    let expidate = new Date(expiYear, expiMonth - 1, 1);
                    months = (expidate.getFullYear() - currentdate.getFullYear()) * 12;
                    months -= currentdate.getMonth() + 1;
                    months += expidate.getMonth();
                    let temp = months === 0 ? true : false;
                    if (temp === true) {
                        bCreditCardExpiring = true;
                    }
                })
            }
        }
        return bCreditCardExpiring;
    }

    checkExpiredCreditCard() {
        let currentdate = new Date();
        let bCreditCardExpired = false;
        if (this.financeDetails && this.financeDetails?.paymentCards && Array.isArray(this.financeDetails?.paymentCards)
            && this.financeDetails?.paymentCards?.length > 0) {
            let activeCards = this.financeDetails.paymentCards.filter(k => k?.active === true);
            if (activeCards && Array.isArray(activeCards) && activeCards?.length > 0) {
                activeCards.forEach(card => {
                    let months;
                    let expiMonth = card.expirationMonth;
                    let expiYear = card.expirationYear;
                    let expidate = new Date(expiYear, expiMonth - 1, 1);
                    months = (expidate.getFullYear() - currentdate.getFullYear()) * 12;
                    months -= currentdate.getMonth() + 1;
                    months += expidate.getMonth();
                    let temp = months === 0 ? true : false;
                    if (temp === true) {
                        bCreditCardExpired = true;
                    }
                })
            }
        }
        return bCreditCardExpired;
    }

    getMemberProfilePayload() {
        return {
            accountNumber: this.profileDetails?.AccountId ?? '',
            memberName: `${this.profileDetails?.FirstName ?? ''} 
            ${this.profileDetails?.MiddleInitial ?? ''} 
            ${this.profileDetails?.LastName ?? ''}`,
            dob: this.profileDetails?.DateofBirth ?? '',
            email: this.profileDetails?.Email ?? '',
        }
    }
    

    authorizationPayload() {
        return {
            authorization: this.bPharmacyAuth
        }
    }

    handleCallPreference() {
        this.getPrefereceData();
    }

    handleCardSave() {
        this.getFinanceData();
    }

    handleAddressSaveSuccess(event) {
        this.getDemographicsData(event.detail.refreshHighlightPanel);
    }

    handleCallDemographics(){
        this.getDemographicsData();
    }

    handleLogNoteAdded() {
        if (this.template.querySelector('c-pharmacy-combined-history-data-hum') != null) {
            this.template.querySelector('c-pharmacy-combined-history-data-hum').updateHistory();
        }
    }
}