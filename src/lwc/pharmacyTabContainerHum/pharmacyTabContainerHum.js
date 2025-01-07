/*
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson              02/01/2023                   US-3939434
* Nirmal Garg					  07/27/2023	               US4902305
* Vishal Shinde                   08/28/2023                   US- 4833055-User Story 4833055 Mail Order Management: Pharmacy - Iconology- Authorization (Lightning)
* Jonathan Dickinson			  10/09/2023			       DF-8199
****************************************************************************************************************************/
import { LightningElement, wire, api,track } from 'lwc';
import { subscribe, unsubscribe, publish, createMessageContext } from 'lightning/messageService';
import pharmacyMembermessageChannel from '@salesforce/messageChannel/pharmacyMemberDetails__c';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { CurrentPageReference } from 'lightning/navigation';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import errorMessageChannel from '@salesforce/messageChannel/pharmacyErrors__c';
import pharmacyMemberNotFoundError from '@salesforce/label/c.pharmacyMemberNotFoundError';
import pharmacyHTTPError from '@salesforce/label/c.pharmacyGenericError';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import getAuthorizations from '@salesforce/apexContinuation/PharmacyBenefits_LC_HUM.invokeGetpAuthService';
const ERROR_404 = '404';
const ERROR_500 = '500';
const MEM_NOT_FOUND = 'Member not found';

export default class PharmacyTabContainerHum extends LightningElement {

    pharmacyMemberDetails;
    pharmacyOMSDetails;
    pharmcyAccountDetails;
    pharmacyDemographicDetails;
    payer;
    currentPageReference = null;
    urlStateParameters = null;
    accID;
    urlEnterpriceID;
    netWorkId;
    profilename;
    recordId;
    errorType;
    isError = false;
    errorMessage;
    @track pharmacyAuthorizations='';
    @track bPharmacyAuth =false;
    @track pharmAuthServiceError = false;

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.state;
            this.setParametersBasedOnUrl();
        }
    }


    setParametersBasedOnUrl() {
        this.accID = this.urlStateParameters.c__AccountID || null;
        this.recordId = this.urlStateParameters.c__AccountID || null;
        this.urlEnterpriceID = this.urlStateParameters.c__enterpriceID || null;
    }

    messageContext = createMessageContext();

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD, NETWORK_ID_FIELD]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
            this.netWorkId = data.fields.Network_User_Id__c.value;
        }
    }

    // Lightning message service subscribe and unsubsubscribe
    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                pharmacyMembermessageChannel,
                (message) => this.handleMessage(message)
            );
        }
    }
    // Handler for message received by component
    handleMessage(message) {
        const me = this;
        if (message != null && message.memberDetails != null) {
            me.pharmacyMemberDetails = JSON.parse(JSON.stringify(message.memberDetails));
            const healthAndAllergiesComponent = me.template.querySelector('c-pharmacy-health-and-allergy-details');
            if (healthAndAllergiesComponent) {
                healthAndAllergiesComponent.pharmacydata(me.pharmacyMemberDetails.objPharmacyAllergiesAndHConditions);
            }
            const pharmacyAccountDetailsComponent = me.template.querySelector('c-pharmacy-account-details');
            if (pharmacyAccountDetailsComponent) {
                pharmacyAccountDetailsComponent.pharmacydata(me.pharmacyMemberDetails.objPharDemographicDetails, this.urlEnterpriceID, this.networkId, this.recordId);
            }
            me.pharmacyDemographicDetails = me.pharmacyMemberDetails.objPharDemographicDetails
            me.pharmacyOMSDetails = me.pharmacyMemberDetails.objPharOMSDetails;
            me.pharmcyAccountDetails = me.pharmacyMemberDetails.objPharDemographicDetails;
            me.payer = me?.pharmacyMemberDetails?.objPharmacyPayer?.payer ?? '';
        }
        else {
            const evt = new ShowToastEvent({
                title: 'Error',
                message: 'Error occurred while retrieving member details.',
                variant: 'error',
                mode: 'dismissable'
            });
            this.dispatchEvent(evt);
        }
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    // Standard lifecycle hooks used to subscribe and unsubsubscribe to the message channel
    connectedCallback() {
        this.subscribeToMessageChannel();
        this.subscribeToErrorMessageChannel();
	    this.getPharmacyAuthorizations();
    }

    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
        this.unsubscribeToErrorMessageChannel();
    }

    subscribeToErrorMessageChannel() {
        if (!this.errorSubscription) {
            this.errorSubscription = subscribe(
                this.messageContext,
                errorMessageChannel,
                (message) => this.handleErrorMessage(message)
            );
        }
    }

    handleErrorMessage(message) {
        this.errorType = message.errorType;
        if (this.errorType === MEM_NOT_FOUND) {
            this.errorMessage = pharmacyMemberNotFoundError;
            this.isError = true;
        } else if (this.errorType === ERROR_404 || this.errorType === ERROR_500) {
            this.errorMessage = pharmacyHTTPError;
            this.isError = true;
        } else {
            this.isError = false;
        }
    }

    unsubscribeToErrorMessageChannel() {
        unsubscribe(this.errorSubscription);
        this.errorSubscription = null;
    }

    handleUpdateDemographics(event) {
        this.pharmacyDemographicDetails = event?.detail;
        if (this.template.querySelector('c-pharmacy-prescriptions-and-order-summary-hum') != null) {
            this.template.querySelector('c-pharmacy-prescriptions-and-order-summary-hum').setDemographicsDetails(this.pharmacyDemographicDetails);
        }
        this.fireEventToUpdateHLPane();
    }

    fireEventToUpdateHLPane() {
        let message = { messageDetails: this.pharmacyDemographicDetails, MessageName: "UpdateAddress" };
        publish(this.messageContext, humanaPharmacyLMS, message);
    }

    getPharmacyAuthorizations() {
        getAuthorizations({ sMemID: this.urlEnterpriceID })  
            .then(result => {
                if (result && result != 'null' && result?.length > 0) {
                    this.pharmacyAuthorizations = JSON.parse(result);
                   
                    this.checkForPharmacyAuth();
                }
            }).catch(error => {
                console.log(error);
                this.pharmacyAuthorizations = null;
                this.pharmAuthServiceError= true
            })
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
        this.fireEventToHighlightedPanel();
    }

    fireEventToHighlightedPanel(){
        let message = { messageDetails: this.bPharmacyAuth, MessageName: "authorization" };
        publish(this.messageContext, humanaPharmacyLMS, message);
    }
}