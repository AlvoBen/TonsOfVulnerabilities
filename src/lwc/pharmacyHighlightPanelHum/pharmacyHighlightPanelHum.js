/*
Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Abhishek Mangutkar              05/12/2022                  Initial version
* Soniya Kunapareddy              08/24/2022                  US-3562135
* Swapnali Sonawane               12/05/2022                  US- 3969790 Migration of the order queue detail capability
* Abhishek Mangutkar              12/06/2022                   US3996413- Fixing Pharmacy Issues
* Jonathan Dickinson              02/01/2023                  US-3939434
* Nirmal Garg						  07/27/2023	                US4902305
* Vishal Shinde                   08/28/2023                  US- 4833055-User Story 4833055 Mail Order Management: Pharmacy - Iconology- Authorization (Lightning)
* Jonathan Dickinson			  10/09/2023			       DF-8199
* Isaac Chung                     11/06/2023                  US 5306769 - Next Best Action, Add Mail Order Page to NBA Engine - Interim Solution
****************************************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import getMemberRequest from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeGetMemberService';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import { publish, subscribe, unsubscribe, createMessageContext } from 'lightning/messageService';
import messageChannel from '@salesforce/messageChannel/pharmacyMemberDetails__c';
import { CurrentPageReference, NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import pharmacyDemographicsNoData from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_NODATA';
import pharmacyDemographicsError from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_ERROR';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getMemberIcons from '@salesforce/apex/MemberIcons_LC_HUM.getMemberIconStatus';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';

import { getLabels, getLocaleDate } from 'c/crmUtilityHum';
import getPrescriptions from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeEpostMemberPrescription';
import { getPrescriptionIcons } from './layoutConfig';
import { openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import memberNotFoundErrorMsg from '@salesforce/label/c.Pharmacy_Mem_Not_found';
import errorMessageChannel from '@salesforce/messageChannel/pharmacyErrors__c';
import pubSubHum from 'c/pubSubHum';
const MINIMUM_WORD_LENGTH = 3;
const PRIOR_MONTH = 18;
const MEM_NOT_FOUND = 'Member not found';
const ERROR_500 = '500';

export default class PharmacyHighlightPanelHum extends NavigationMixin(LightningElement) {
    label = {
        pharmacyDemographicsNoData,
        pharmacyDemographicsError,
        memberNotFoundErrorMsg
    };
    isHighlightDetailsLoaded = false;
    isHighlightDetailsLoading = true;


    memberName;
    accoutId;
    memberDOB;
    shippingAddress;
    shippingAddressStreet;
    shippingAddressCity;
    shippingAddressCountry;
    shippingAddressProvince;
    shippingAddressPostalCode;
    memberEmail;
    balanceDue;
    accountLimit;
    mtmElegibility;

    netWorkId;

    isBalanceDue = true;
    isBalanceZero = false;
    isBalanceCredit = false;

    currentPageReference = null;
    urlStateParameters = null;
    urlAccoutId = null;
    urlEnterpriceID = null;
    fName = null;
    lName = null;
    memberPlanId = null;
    @track loaded = false;
    sScriptKey = "";
    addedPrescriptions;
    @track addPresciptionList;
    @track memberIcons = [];
    @track prescriptions;
    @track showPrescriptionPopover = false;
    @track finalPrescriptionList = [];
    @track SearchPresciptionList = [];
    @track iconsModel;
    @track selectedPrescriptions = [];
    @track selectedPrescriptionCount = 0;
    @track prescriptionColorList = [];
    @track bCreatingCase = false;
    cardExpiringSoon = false;
    cardExpired = false;

    labels = getLabels();
    bShowDemographicsWarning;

    @wire(CurrentPageReference) pageRef;

    errAssistiveText = 'Error';
    iconname = 'utility:error';
    errorClass = 'slds-scoped-notification slds-media slds-media_center slds-theme_error';
    iconErrorClass = 'slds-icon_container slds-icon-utility-error';
    serviceFailure = false;
    errorMessage;

    connectedCallback() {
        this.subscribeToMessageChannel();
        this.loadCommonCSS();
        if (!this.isHighlightDetailsLoaded) {
            this.getMemberHighlights();

            this.getPrescriptionDetails();
            this.iconsModel = getPrescriptionIcons("icons");
        }
        pubSubHum.registerListener('CreditCardDetailsUpdate', this.loadData.bind(this), this);
    }

    loadData(eventData) {
        this.updateCreditCardIcons(eventData.messageDetails);
    }

    //set current page url ref.
    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.state;
            this.setParametersBasedOnUrl();
        }
    }

    //load css
    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    //get parameters from url.
    setParametersBasedOnUrl() {
        this.urlAccoutId = this.urlStateParameters.c__AccountID || null;
        this.urlEnterpriceID = this.urlStateParameters.c__enterpriceID || null;
        this.fName = this.urlStateParameters.c__fName || null;
        this.lName = this.urlStateParameters.c__lName || null;
        this.memberPlanId = this.urlStateParameters.c__PlanMemberId || null;
    }

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

    messageContext = createMessageContext();

    //demograpichs message channel start
    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                humanaPharmacyLMS,
                (message) => this.handleMessage(message)

            );
        }
    }



    // Handler for message received by component
    handleMessage(message) {
        if (!this.isEmptyObj(message)) {
            if (message.MessageName === 'UpdateAddress') {
                let receivedDemographicMessage = message.messageDetails;
                this.updateShippingAddress(receivedDemographicMessage.Address)
            }
            else if (message.MessageName === 'VerifyDemographics') {
                this.handleMessageVerifyDemographics(message)
            }
            else if (message.MessageName === 'AddPrescriptions') {
                this.addPrescription(message.messageDetails)
            }
            else if (message.MessageName === 'ClosePrescriptionWindow') {
                this.hidePrescriptionPopover();
            }
            else if (message.MessageName === 'CreditCardDetailsUpdate') {
                this.updateCreditCardIcons(message.messageDetails);
            }
 	         else if (message.MessageName === 'authorization') {
                this.updateAuthorizationIcon(message.messageDetails);
            }
        }
    }

    get getStyle() {
        let maxheight = window.innerHeight - 138;
        return 'background-color: #f3f2f2; border: 1px solid #dddbda ;width:380px;max-height: ' + maxheight + 'px;';
    }
    get getInnerDivStyle() {
        let maxheight = window.innerHeight - 318;
        return 'max-height:' + maxheight + 'px;';
    }
    // method to get prescriptions from service
    getPrescriptionDetails() {
        let startdate = new Date();
        startdate.setMonth(startdate.getMonth() - PRIOR_MONTH);
        let enddate = new Date();
        let sStartDate = (startdate.getMonth() + 1) + '/' + startdate.getDate() + '/' + startdate.getFullYear();
        let sEndDate = (enddate.getMonth() + 1) + '/' + enddate.getDate() + '/' + enddate.getFullYear();

        getPrescriptions({ memID: this.urlEnterpriceID, scriptKey: this.sScriptKey, startDate: sStartDate, endDate: sEndDate, networkId: this.networkId, sRecordId: this.recordId })
            .then(result => {
                this.loaded = true;
                if (result) {
                    this.addPresciptionList = JSON.parse(result);
                }
            })
            .catch(err => {
                this.loaded = true;
                console.log("Error Occured", err);
                console.log("Error -------->");
            });
    }
    //display the popover on Add prescription button click
    addPrescription(Prescriptions) {
        this.addedPrescriptions = Prescriptions.AddedPrescription;
        this.createFinalPrscriptionList(this.addPresciptionList);
        this.selectedPrescriptions = [];
        this.prescriptionColorList = [];
        for (let j of this.addedPrescriptions.values()) {
            this.selectedPrescriptions.push({
                addedP: j['prescriptions'],
                addedIcon: j['icon'],
            });
            this.prescriptionColorList.push({
                currentPrescriptioncolor: j['icon'].icontype,
            });
        }
        this.selectedPrescriptionCount = this.selectedPrescriptions.length;
        this.checkEligibility();
        this.showPrescriptionPopover = true;
    }
    //method to hide the Popover
    hidePrescriptionPopover() {
        this.selectedPrescriptions = [];
        this.selectedPrescriptionCount = 0;
        this.showPrescriptionPopover = false;
    }
    //create final prescription list
    createFinalPrscriptionList(result) {
        if (result != null && result != undefined) {
            this.finalPrescriptionList = [];
            this.prescriptions = result;
            this.prescriptions = this.prescriptions.filter((item) => item.Status !== 'INACTIVE');
            for (let j = 0; j < this.prescriptions.length; j++) {
                let addedtoCart = false;
                let checked;
                this.addedPrescriptions.forEach(a => {
                    let p = JSON.stringify(a);
                    if (p.includes(JSON.stringify(this.prescriptions[j].RXNumber))) {
                        addedtoCart = true;
                    }
                });

                let array = JSON.stringify(this.selectedPrescriptions);
                if (array.includes(JSON.stringify(this.prescriptions[j].RXNumber))) {
                    checked = true;
                } else { checked = false; }

                this.finalPrescriptionList.push({
                    key: j,
                    prescriptions: this.prescriptions[j],
                    icon: this.getIcon(this.prescriptions[j]),
                    AddedtoCart: addedtoCart,
                    OrderEligible: (this.prescriptions[j].Status == 'WORKRX') ? false : true,
                    Checked: checked,
                });
            }
            return this.finalPrescriptionList;
        }
    }
    //method to search the precription in popover
    searchPrescription(event) {
        if (event.target.value.length >= MINIMUM_WORD_LENGTH) {
            let filterByText = event.target.value;
            this.SearchPresciptionList = this.addPresciptionList;
            let tmp = [];
            this.SearchPresciptionList.forEach(a => {
                Object.values(a).forEach(b => {
                    let tempNode = JSON.stringify(b);
                    if (null != tempNode && tempNode.toLowerCase().includes(filterByText.toLowerCase()) && !tmp.includes(a)) {
                        tmp.push(a);
                    }
                })
            });
            this.SearchPresciptionList = tmp.length > 0 ? tmp : [];
            this.createFinalPrscriptionList(this.SearchPresciptionList);
        }
        else {
            this.createFinalPrscriptionList(this.addPresciptionList);
        }
        this.checkEligibility();
    }

    getIcon(iconname) {
        let Today = Date.parse(getLocaleDate(new Date()));
        let expdate = Date.parse(iconname.ExpirationDate);
        let nextfilldate = Date.parse(iconname.NextFillDate);
        let nextfillmindate = Date.parse(iconname.NextFillMinDate);
        let lastfilldate = Date.parse(iconname.LastFillDate);
        if (iconname.Status === 'INACTIVE') {
            return this.iconsModel.find(x => x.iconname === "INACTIVE");
        }
        else if (iconname.Status === 'EXPIRED' || iconname.RefillsRemaining <= 0 || expdate < Today) {
            if (nextfilldate <= Today) {
                return this.iconsModel.find(x => x.iconname === "greenclock");
            }
            else if (nextfilldate >= Today && nextfillmindate < Today) {
                return this.iconsModel.find(x => x.iconname === "blueclock");
            } else {
                return this.iconsModel.find(x => x.iconname === "clock");
            }
        }
        else {
            if (expdate > Today && nextfilldate < Today) {
                return this.iconsModel.find(x => x.iconname === "refillsremaining");
            } else if (nextfilldate > Today && nextfillmindate <= Today) {
                return this.iconsModel.find(x => x.iconname === "bluecalendar");
            } else {
                return this.iconsModel.find(x => x.iconname === "event");
            }
        }
    }
    // method to handle prescription select event
    handlePrescriptionSelect(event) {
        let currentPresciption = event.detail.selectedPrescription;
        let present = false;
        let index;
        this.selectedPrescriptions.forEach(a => {
            Object.values(a).forEach(b => {
                let tempNode = JSON.stringify(b);
                if (null != tempNode && tempNode.includes(event.detail.prescriptionnumber)) {
                    present = true;
                    index = this.selectedPrescriptions.indexOf(a);
                }
            })
        });

        if (present) {
            this.selectedPrescriptions.splice(index, 1);
            this.prescriptionColorList.splice(index, 1)
        }
        else if (!present) {
            let icon = this.getIcon(currentPresciption)
            this.selectedPrescriptions.push({
                currentPresciption,
                icon: icon,
            });
            this.prescriptionColorList.push({
                currentPrescriptioncolor: icon.icontype,
            });
        }
        this.selectedPrescriptionCount = this.selectedPrescriptions.length;
        this.checkEligibility();
    }
    //method to check eligiblity of prescriptions
    checkEligibility() {
        if (this.selectedPrescriptionCount === 0) {
            this.finalPrescriptionList.forEach(k => {
                if (k.prescriptions.Status === "WORKRX") {
                    k.OrderEligible = false;
                }
                else {
                    k.OrderEligible = true;
                }
            });
        }
        else {
            let icons = JSON.stringify(this.prescriptionColorList);
            if (icons.includes('green') || (icons.includes('green') && icons.includes('black'))) {
                this.finalPrescriptionList.forEach(k => {
                    if (k.icon.icontype === 'blue' || k.prescriptions.Status === "WORKRX") {
                        k.OrderEligible = false;
                    }
                    if ((k.icon.icontype === 'green' || k.icon.icontype === 'black') && k.prescriptions.Status != "WORKRX") {
                        k.OrderEligible = true;
                    }
                });
            }
            else if (icons.includes('blue') || (icons.includes('blue') && icons.includes('black'))) {

                this.finalPrescriptionList.forEach(k => {
                    if (k.icon.icontype === 'green' || k.prescriptions.Status === "WORKRX") {
                        k.OrderEligible = false;
                    }
                    if ((k.icon.icontype === 'black' || k.icon.icontype === 'blue') && k.prescriptions.Status != "WORKRX") {
                        k.OrderEligible = true;
                    }
                });
            }
            else if ((icons.includes("black")) && (!icons.includes("green")) && (!icons.includes("blue"))) {

                this.finalPrescriptionList.forEach(k => {
                    if (k.prescriptions.Status === "WORKRX") {
                        k.OrderEligible = false;
                    }
                    if ((k.icon.icontype === 'black' || k.icon.icontype === 'green' || k.icon.icontype === 'blue') && k.prescriptions.Status != "WORKRX") {
                        k.OrderEligible = true;
                    }
                });
            }
        }
    }
    // method to send selected prescriptions to the order detail component
    passToCreateOrder() {
        this.addedPrescriptions.forEach(a => { this.selectedPrescriptions.shift(); });
        let message = { messageDetails: this.selectedPrescriptions, MessageName: "UpdatePrescriptions" };
        publish(this.messageContext, humanaPharmacyLMS, message);
        this.hidePrescriptionPopover();
    }

    updateShippingAddress(addressDTO) {
        let tempShippingAddress = addressDTO
            && Array.isArray(addressDTO) && addressDTO?.length > 0
            ? addressDTO.find(k => k?.AddressType === 'S') : null;
        this.displayShippingAddress(tempShippingAddress);
    }

    handleMessageVerifyDemographics(message) {
        if (!this.isEmptyObj(message)) {
            let daysSinceLastModified = message.messageDetails;
            if (daysSinceLastModified > 90) {
                this.bShowDemographicsWarning = true;
            }
            else {
                this.bShowDemographicsWarning = false;
            }
        }
    }


    unsubscribeToMessageChannel() {
        unsubscribe(this.subscription);
        this.subscription = null;

    }

    disconnectedCallback() {
        this.unsubscribeToMessageChannel();
    }
    //demographics message channel end

    /**
     * Handles member highlight service call
     */
    getMemberHighlights() {

        if (this.urlEnterpriceID) {
            getMemberRequest({ memID: this.urlEnterpriceID, networkId: this.netWorkId, sRecordId: this.urlAccoutId })
                .then(result => {

                    //parse JSON string
                    if (result && result != 'null') {
                        let responseData = JSON.parse(result);


                        //get member demograpic details
                        if (responseData && (!this.isEmptyObj(responseData))) {
                            if (!responseData.showErrorMsgs) {
                                if (responseData.objPharDemographicDetails && (!this.isEmptyObj(responseData.objPharDemographicDetails))) {
                                    let demographicDetails = responseData.objPharDemographicDetails;

                                    //publish message channel to other components
                                    let message = { memberDetails: responseData };
                                    publish(this.messageContext, messageChannel, message);

                                    if ((demographicDetails.AccountNumber != null && demographicDetails.AccountNumber != "") ||
                                        (demographicDetails.FirstName != null && demographicDetails.FirstName != "") ||
                                        (demographicDetails.LastName != null && demographicDetails.LastName != "") ||
                                        (demographicDetails.DOB != null && demographicDetails.DOB != "") ||
                                        (demographicDetails.Email != null && demographicDetails.Email != "") ||
                                        (demographicDetails.BalanceDue != null && demographicDetails.BalanceDue != "") ||
                                        (demographicDetails.AccountLimit != null && demographicDetails.AccountLimit != "") ||
                                        (demographicDetails.MTMIndicator != null && demographicDetails.MTMIndicator != "") ||
                                        (demographicDetails.AccountLimit != null && demographicDetails.AccountLimit != "") ||
                                        (demographicDetails.MailingAddress != null && demographicDetails.MailingAddress != "")) {
                                        this.memberName = demographicDetails.FirstName + " " + demographicDetails.MiddleName + " " + demographicDetails.LastName;
                                        this.accoutId = demographicDetails.AccountNumber;
                                        this.memberDOB = demographicDetails.DOB;
                                        this.memberEmail = demographicDetails.Email;
                                        this.balanceDue = demographicDetails.BalanceDue;
                                        this.accountLimit = demographicDetails.AccountLimit;
                                        let mtmIndicator = demographicDetails.MTMIndicator;
                                        if (mtmIndicator) {
                                            if (mtmIndicator.toUpperCase() == 'UNKNOWN') {
                                                this.mtmElegibility = false;
                                            }
                                            else {
                                                this.mtmElegibility = true;
                                            }
                                        }
                                        else {
                                            this.mtmElegibility = false;
                                        }
                                        let tempShippingAddress = responseData?.objPharDemographicDetails
                                            && responseData?.objPharDemographicDetails?.Address
                                            && Array.isArray(responseData?.objPharDemographicDetails?.Address) && responseData?.objPharDemographicDetails?.Address > 0
                                            ? responseData?.objPharDemographicDetails?.Address.find(k => k?.AddressType === 'S') : null;
                                        this.displayShippingAddress(tempShippingAddress);

                                        this.isBalanceDue = parseFloat(this.balanceDue) > 0;
                                        this.isBalanceZero = parseFloat(this.balanceDue) == 0.00;
                                        this.isBalanceCredit = parseFloat(this.balanceDue) < 0;

                                        this.checkCreditCards(demographicDetails.CreditCardsDetail.CreditCard);
                                        this.loadMemberIcons();
                                        this.isHighlightDetailsLoaded = true;
                                        this.isHighlightDetailsLoading = false;
                                    }
                                    else {
                                        //show error
                                        this.isHighlightDetailsLoaded = true;
                                        this.isHighlightDetailsLoading = false;

                                        this.memberNotFound(MEM_NOT_FOUND);
                                    }
                                }
                                else {
                                    //show error
                                    this.isHighlightDetailsLoaded = true;
                                    this.isHighlightDetailsLoading = false;

                                    this.memberNotFound(MEM_NOT_FOUND);
                                }
                            }
                            else {
                                //object is empty
                                this.isHighlightDetailsLoaded = true;
                                this.isHighlightDetailsLoading = false;

                                this.memberNotFound(MEM_NOT_FOUND);
                            }
                        }
                        else {
                            //server error
                            this.isHighlightDetailsLoaded = true;
                            this.isHighlightDetailsLoading = false;

                            this.memberNotFound(ERROR_500);
                        }
                    }
                })
                .catch(err => {
                    console.log("Error Occured", err);
                    this.isHighlightDetailsLoaded = true;
                    this.isHighlightDetailsLoading = false;

                    this.memberNotFound(err.message);
                });
        }
    }

    displayShippingAddress(address) {
        if (address && Object.keys(address)?.length > 0) {
            this.shippingAddressStreet = `${address?.AddressLine1 ?? ''} ${address?.AddressLine2 ?? ''},`;
            this.shippingAddressCity = `${address?.City ?? ''}, ${address?.StateCode ?? ''} ${address?.ZipCode ?? ''}`;
            this.shippingAddress = `${this.shippingAddressStreet} ${this.shippingAddressCity}`;
        }
    }

    /*
    * Check for object is empty
    */
    isEmptyObj(obj) {
        return Object.keys(obj).length === 0;
    }

    /**
   * Display toast message
   * when an exception is
   * thrown
   */
    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }

    /*
    * Set toast message if member not found
    */
    memberNotFound(errorMsg) {
        if (errorMsg === MEM_NOT_FOUND) {
            this.errorMessage = this.label.memberNotFoundErrorMsg;
        }
        else {
            this.errorMessage = this.label.pharmacyDemographicsError;
        }
        this.serviceFailure = true;
        let message = { errorType: errorMsg };
        publish(this.messageContext, errorMessageChannel, message);
    }

    loadMemberIcons() {
        const me = this;
        const iconParams = {
            sPageName: 'Humana Pharmacy',
            sRecordId: me.urlAccoutId
        };
        getMemberIcons(iconParams).then((result) => {
            if (result && result.bIconsPresnt) {
                me.memberIcons = this.showHideCreditCardIcons(result.lstMemberIcons);
            }
        }).catch((error) => {
            console.log('Error Occured', error);
        });
    }

    showHideCreditCardIcons(lstMemberIcon) {
	if (lstMemberIcon && lstMemberIcon?.length > 0) {
            if (lstMemberIcon.find(k => k?.sIconName === 'PharmacyAuthorization') != null) {
                lstMemberIcon.find(k => k?.sIconName === 'PharmacyAuthorization').bIconVisible = false;
            }
        }
        lstMemberIcon.forEach((item, cIcon) => {
            if (item.sIconName == "creditCardExpiringSoonIcon") {
                if (this.cardExpiringSoon == false) {
                    lstMemberIcon.splice(cIcon, 1);
                }
            }
        });
        lstMemberIcon.forEach((item, cIcon) => {
            if (item.sIconName == "creditCardExpiredIcon") {
                if (this.cardExpired == false) {
                    lstMemberIcon.splice(cIcon, 1);
                }
            }
        });
        return lstMemberIcon;
    }

    checkCreditCards(cardDTO) {
        let currentdate = new Date();
        if (cardDTO != null) {
            for (let k = 0; k < cardDTO.length; k++) {
                let card = cardDTO[k];
                if (card.IsActive == 'true') {
                    let months;
                    let expiMonth = card.ExpirationMonth;
                    let expiYear = card.ExpirationYear;
                    let expidate = new Date(expiYear, expiMonth - 1, 1);
                    months = (expidate.getFullYear() - currentdate.getFullYear()) * 12;
                    months -= currentdate.getMonth() + 1;
                    months += expidate.getMonth();
                    if (months < 0) {
                        this.cardExpired = true;
                    }
                    if (months == 0) {
                        this.cardExpiringSoon = true;
                    }
                }
            }
        }
    }

    verifyDemographicsData(event) {

        //publish message channel to other components
        let message = { messageDetails: this.labels.HUM_OnClick, MessageName: "fireVerifyDemographicsService" };
        publish(this.messageContext, humanaPharmacyLMS, message);
    }


    newCaseClick() {
        openLWCSubtab('caseInformationComponentHum', this.urlAccoutId, { label: 'New Case', icon: 'standard:case' }, { pageName: 'Humana_Pharmacy_Tab' });
    }

    updateCreditCardIcons(creditCradData) {
        if (creditCradData != 'No Credit Card') {
            this.cardExpired = false;
            this.cardExpiringSoon = false;
            this.checkCreditCards(creditCradData);
            this.loadMemberIcons();
        }
    }
    
    updateAuthorizationIcon(payload) {
        if (payload === true) {
            if (this.memberIcons.find(k => k?.sIconName === 'PharmacyAuthorization') != null) {
                this.memberIcons.find(k => k?.sIconName === 'PharmacyAuthorization').bIconVisible = true;
            }
        }
    }
}