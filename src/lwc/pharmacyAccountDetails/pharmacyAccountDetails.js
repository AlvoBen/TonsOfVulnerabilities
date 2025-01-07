/*
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Kalyani Pachpol                 05/12/2022                  US-3188387
* Kalyani Pachpol                 05/24/2022                  US-3188387-Negative Value Change
* Soniya Kunapareddy              08/24/2022                  US-3562135
* Jonathan Dickinson              02/01/2023                  US-3939434
****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import updateGetMember from '@salesforce/apexContinuation/Pharmacy_LC_HUM.updateGetMember';
import getMemberDetails from "@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService";
import { publish, MessageContext, subscribe, unsubscribe, APPLICATION_SCOPE } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import getPolicyList from '@salesforce/apex/MemberSearchActiveFuturePolicies_LC_HUM.determinePolicyAccess';
import { getLocaleDate, sortTable, hcConstants, getLabels } from 'c/crmUtilityHum';
export default class PharmacyAccountDetails extends LightningElement {
    @track status;
    @track displaytext = '';
    @track isvisible;
    @track issave;
    @track iscancel;
    @track iseditable;
    @api recordId;
    @track displayEmail;
    @track isError;
    @track isprimaryPhone;
    @track isEmail;
    @track isAlternatePhone;
    @track isCapType;
    @track emailField;
    @track IsDeclined;
    @track declinedEmail;
    @track declinedDisabled = true;
    @track loaded = false;
    policyList;
    header = 'Account Details';
    phone;
    AltPhone;
    Email;
    memID;
    IsDeclined = 'YYY';
    enterpriseID;
    sRecordId;

    //MessageContext for publishig demographic data.
    @wire(MessageContext)
    messageContext;

    connectedCallback() {
        this.subscribeToMessageChannel();
    }

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                messageChannel,
                (message) => this.handleMessage(message),
                { scope: APPLICATION_SCOPE }
            );
        }
    }

    handleMessage(message) {
        if (message) {
            if (message.MessageName === 'CapType') {
                this.capType = message.messageDetails;
            } else if (message.MessageName === 'MemberConsent') {
                this.memberConsent = message.messageDetails;
            }
        }
    }

    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;
    }

    //to get data from Tab container
    pharmacyMemberDetails;
    pharmacyOMSDetails;
    pharmcyAccountDetails;
    recordId;
    pharmacyMemberDetails;
    pharmcyAccountDetails;
    recordId = '';
    @track memberPrimaryPhone;
    @track memberEmail;
    @track captype;
    @track memberAlternatePhone;
    @track error;
    @track accountDetailsRecord;
    @track isMemberPrimaryPhoneEmpty;
    @track isMemberEmailEmpty;
    @track isAlternatePhoneEmpty;
    @track isEditable = false;
    @track isEmailReadyOnly = false;
    @track isDeclinedCheck = false;
    memberFirstName;
    memberDOB;
    memberPlanType;
    memberMobile;
    memberMiddleName;
    memberPrimaryPhone;
    effectiveDate;
    memberEmail;
    memberLastName;
    capType;
    teminationDate;
    declinedEmail;
    memberConsent;
    accountLimit;
    receivedMessage = '';
    processedMessage = '';
    memberAccountData;
    pharmcyAccountDetails;
    memberaddress;
    memberphone;
    @track bValidData = false;
    @track planType;
    @api
    pharmacydata(memberdata, enterpriseid, networkid, recordid) {
        const me = this;
        me.enterpriseID = enterpriseid;
        me.networkId = networkid;
        me.recordId = recordid;
        me.pharmcyAccountDetails = memberdata;
        me.getMemberAccountDetails();
        me.getPolicyDetails();
    }

    get options() {
        return [{ label: 'S', value: 'S' }, { label: 'E', value: 'E' }]
    }

    getMemberAccountDetails() {
        const me = this;
        this.loaded = true;
        let memberAccountData = me.pharmcyAccountDetails;
        if (memberAccountData != undefined) {
            me.memberMiddleName = memberAccountData.MiddleName != null ? memberAccountData.MiddleName : '';
            me.memberFirstName = memberAccountData.FirstName != null ? memberAccountData.FirstName : '';
            me.memberLastName = memberAccountData.LastName != null ? memberAccountData.LastName : '';
            me.memberDOB = memberAccountData.DOB != null ? memberAccountData.DOB : '';
            me.memberEmail = memberAccountData.Email != null ? memberAccountData.Email : '';
            me.balanceDue = memberAccountData.BalanceDue != null ? memberAccountData.BalanceDue : 0;
            me.capType = memberAccountData.CapType != null ? memberAccountData.CapType : '';
            me.memberMobile = memberAccountData.AltPhone != null ? this.formatphonenumber(memberAccountData.AltPhone) : '';
            me.memberAlternatePhone = memberAccountData.AltPhone != null ? this.formatphonenumber(memberAccountData.AltPhone) : '';
            me.memberPrimaryPhone = memberAccountData.PhoneNumber != null ? this.formatphonenumber(memberAccountData.PhoneNumber) : '';
            me.memberphone = memberAccountData.PhoneNumber != null ? this.formatphonenumber(memberAccountData.PhoneNumber) : '';
            me.declinedEmail = memberAccountData.IsDeclined != null ? memberAccountData.IsDeclined : false;
            me.memberConsent = memberAccountData.MemberConsent != null ? memberAccountData.MemberConsent == "true" ? 'Approved ' + memberAccountData.MemberConsentApprovedDate : "Request Consent for each Order" : '';
            me.accountLimit = memberAccountData.AccountLimit != null ? memberAccountData.AccountLimit : 0;
            me.memberaddress = memberAccountData.MailingAddress != null ? memberAccountData.MailingAddress : '';
            me.memberaddress += memberAccountData.MailingCity != null ? ' ' + memberAccountData.MailingCity : '';
            me.declinedDisabled = true;
            me.isEmailReadyOnly = memberAccountData.IsDeclined != null ? !memberAccountData.IsDeclined : !false;
        }
    }
    displayStatus(event) {
        if (event.target.checked === true) {
            this.isEmailReadyOnly = false;
            //this.isDeclinedCheck = true;
            this.declinedEmail = true;
        } if (event.target.checked === false) {
            this.isEmailReadyOnly = true;
            //this.isDeclinedCheck = false;
            this.declinedEmail = false;
        }
    }
    editMemberDetails() {
        const me = this;
        me.isEditable = true;
        me.isvisible = true;
        me.declinedDisabled = false;
        me.isEmailReadyOnly = me.pharmcyAccountDetails.IsDeclined != null ? !me.pharmcyAccountDetails.IsDeclined : !false;
    }
    get getformattedprimaryphone() {
        if (this.memberPrimaryPhone != null) {
            if (this.memberPrimaryPhone.length == 10) {
                return '(' + this.memberPrimaryPhone.substring(0, 3) + ') ' + this.memberPrimaryPhone.substring(3, 6) + '-' + this.memberPrimaryPhone.substring(6, 10);
            }
        }
    }



    get getbackgroundcolor() {
        const me = this;
        if (parseFloat(me.balanceDue) === 0.0) {
            return 'slds-box style-grey';
        } else if (parseFloat(me.balanceDue) > 0) {
            return 'slds-box style-red';
        } else if (parseFloat(me.balanceDue) < 0) {
            return 'slds-box style-green';
        }

    }

    formatphonenumber(phonenumber) {
        if (phonenumber != null) {
            if (phonenumber.length == 10) {
                return '(' + phonenumber.substring(0, 3) + ') ' + phonenumber.substring(3, 6) + '-' + phonenumber.substring(6, 10);
            }
        }
    }

    get getformattedalternatephone() {
        if (this.memberMobile != null) {
            if (this.memberMobile.length == 10) {
                return '(' + this.memberMobile.substring(0, 3) + ') ' + this.memberMobile.substring(3, 6) + '-' + this.memberMobile.substring(6, 10);
            }
        }
    }

    validationcheck() {
        return !this.bValidData;
    }
    saveAndUpdateClick(event) {
        this.loaded = false;
        if (this.validationcheck()) {
            updateGetMember({ enterprise: this.enterpriseID, phone: this.formartphone(this.memberPrimaryPhone), AltPhone: this.formartphone(this.memberAlternatePhone), Email: this.memberEmail, captype: this.capType, networkId: this.networkId, sRecordId: this.recordId, addressDto: null, IsDeclined: this.declinedEmail })
                .then(result => {
                    let responseData = JSON.parse(result);
                    if (responseData != undefined && responseData != null) {
                        if (responseData.EditMemberResponse != null) {
                            if (responseData.EditMemberResponse.ErrorDescription != null &&
                                responseData.EditMemberResponse.ErrorDescription != "") {
                                this.dispatchEvent(new ShowToastEvent({
                                    title: 'Error!',
                                    message: 'Error Occurred while processing your request.',
                                    variant: 'error',
                                    mode: 'dismissable'
                                }));
                                this.getMemberAccountDetails();
                            } else {
                                if (responseData.ErrorDescription == null && responseData.Error == null) {
                                    this.dispatchEvent(new ShowToastEvent({
                                        title: 'Success!',
                                        message: 'Record has been updated successfully.',
                                        variant: 'success',
                                        mode: 'dismissable'
                                    }));
                                }
                            }
                        }
                        else {
                            if (responseData.Fault != null) {
                                this.dispatchEvent(new ShowToastEvent({
                                    title: 'Error!',
                                    message: 'Error Occurred while processing your request.',
                                    variant: 'error',
                                    mode: 'dismissable'
                                }));
                            }
                            this.getMemberAccountDetails();
                        }
                    }
                    this.refreshMemberData();
                }).catch(error => {
                    this.dispatchEvent(new ShowToastEvent({
                        title: 'Error!',
                        message: 'Error Occurred while processing your request.',
                        variant: 'error',
                        mode: 'dismissable'
                    }));
                    console.log('error :-' + error);
                    this.getMemberAccountDetails();
                })
            this.isEditable = false;
            this.isvisible = false;
        }
    }
    refreshMemberData() {
        this.loaded = false;
        const me = this;
        getMemberDetails({
            memID: this.enterpriseID,
            networkId: this.networkId,
            sRecordId: this.recordId
        }).
            then(data => {
                this.loaded = true;
                let omsdata = JSON.parse(data);
                if (omsdata && omsdata != {} && omsdata.objPharDemographicDetails != {}) {
                    me.pharmcyAccountDetails = omsdata.objPharDemographicDetails;
                    this.getMemberAccountDetails();
                    this.fireEvent();
                }
                this.loaded = true;
            }).catch(e => {
                this.loaded = true;
                console.log('Error : ' + JSON.stringify(e));
            })

    }
    fireEvent() {
        let message = { messageDetails: this.memberEmail, MessageName: "UpdateEmail" };
        publish(this.messageContext, messageChannel, message);
    }
    handleChange(event) {
        if (event.target.name === 'memberPrimaryPhone') {
            const y = event.target.value
                .replace(/\D+/g, '')
                .match(/(\d{0,3})(\d{0,3})(\d{0,4})/);
            event.target.value =
                !y[2] ? y[1] : `(${y[1]}) ${y[2]}` + (y[3] ? `-${y[3]}` : ``);
            this.memberPrimaryPhone = event.target.value;
            this.bValidData = this.formartphone(this.memberPrimaryPhone).toString().trim() != '' &&
                this.formartphone(this.memberPrimaryPhone).length != 10 ? true : false;
        } else if (event.target.name === 'memberEmail') {
            this.memberEmail = event.target.value;
            const re = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            this.bValidData = this.memberEmail.toString().trim() != ''
                && !re.test(String(this.memberEmail).toLowerCase()) ? true : false;
        } else if (event.target.name === 'memberAlternatePhone') {
            const x = event.target.value
                .replace(/\D+/g, '')
                .match(/(\d{0,3})(\d{0,3})(\d{0,4})/);
            event.target.value =
                !x[2] ? x[1] : `(${x[1]}) ${x[2]}` + (x[3] ? `-${x[3]}` : ``);
            this.memberAlternatePhone = event.target.value;
            this.bValidData = this.formartphone(this.memberAlternatePhone).toString().trim() != '' &&
                this.formartphone(this.memberAlternatePhone).length != 10 ? true : false;
        } else if (event.target.name === 'capType') {
            this.value = event.detail.value;
            this.capType = event.target.value;
        }
    }
    handleCancel() {
        const me = this;
        me.isvisible = false;
        me.isEditable = false;
        me.declinedDisabled = true;
        me.declinedEmail = me.pharmcyAccountDetails.IsDeclined != null ? me.pharmcyAccountDetails.IsDeclined : false;
        me.getMemberAccountDetails();
    }
    formartphone(phonenumber) {
        if (phonenumber) {
            return phonenumber.replace('(', '').replace(')', '').replace(' ', '').replace('-', '');
        }
        else {
            return '';
        }
    }

    getPolicyDetails() {
        getPolicyList({ sAccId: this.recordId })
            .then(result => {
                if (result) {
                    let policyrecords = JSON.parse(JSON.stringify(result));
                    policyrecords.forEach(function (item) {
                        item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
                        item.EffectiveTo = getLocaleDate(item.EffectiveTo);
                        item.PlanName = (item.Plan && item.Plan.iab_description__c) ? item.Plan.iab_description__c : '';
                        item.MedicareId__c = item.Member.MedicareId__c;
                        item.Name = (item.Member_Id_Base__c && item.Member_Dependent_Code__c) ? (item.Member_Id_Base__c + '-' + item.Member_Dependent_Code__c) : item.Name;
                        item.Product_Type__c = item.Product_Type__c
                    });
                    this.policyList = sortTable(policyrecords, 'Member_Coverage_Status__c', 'Product__c');
                    if (this.policyList.length > 0) {
                        this.effectiveDate = this.policyList[0].EffectiveFrom ? getLocaleDate(this.policyList[0].EffectiveFrom) : '';
                        this.teminationDate = this.policyList[0].EffectiveTo ? getLocaleDate(this.policyList[0].EffectiveTo) : '';
                        this.planType = this.policyList[0].Product_Type__c;
                    }

                }
            })
            .catch(error => {
                console.log("Error Occured", error);
            });
    }
}