import { LightningElement } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import handleNewVirtualVisitor from '@salesforce/apex/CRMRetail_VisitorInformation_LC_HUM.handleNewVirtualVisitor';
import getVisitorInfoPageData from '@salesforce/apex/CRMRetail_VisitorInformation_LC_HUM.getVisitorInfoPageData'
import logException from '@salesforce/apex/CRMRetail_VisitorInformation_LC_HUM.logUIException'
import Required_for_Birthdate from '@salesforce/label/c.Required_for_Birthdate';
import Valid_Format_for_Birthdate from '@salesforce/label/c.Valid_Format_for_Birthdate';
import Valid_Waiver_Date from '@salesforce/label/c.Valid_Waiver_Date';
import Help_Text_For_Waiver_Date from '@salesforce/label/c.Help_Text_For_Waiver_Date';
import Year_Restriction_for_Waiver_Date from '@salesforce/label/c.Year_Restriction_for_Waiver_Date';
import Format_For_Waiver_date from '@salesforce/label/c.Format_For_Waiver_date';
import ZipCodeInvalidMsg from '@salesforce/label/c.SearchVisitor_ZipCodeInvalidMsg_HUM'
import CRMRetail_CheckInSuccessMsg from '@salesforce/label/c.CRMRetail_CheckInSuccessMsg'
import PhoneInvalidMsg from '@salesforce/label/c.SearchVisitor_PhoneMsg_HUM'
import CRMRetail_Checkin_Error_Message from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import CRMRetail_Required_Fields_Error from '@salesforce/label/c.CRMRetail_Required_Fields_Error';
import CRMRetail_Field_Value_Validity from '@salesforce/label/c.CRMRetail_Field_Value_Validity';
import CRMRetail_Field_Format_Error from '@salesforce/label/c.CRMRetail_Field_Format_Error';
import CRMRetail_Required_Error_Title from '@salesforce/label/c.CRMRetail_Required_Error_Title';
import CRMRetail_Address_Information_Section from '@salesforce/label/c.CRMRetail_Address_Information_Section';
import CRMRetail_Additional_Information_Section from '@salesforce/label/c.CRMRetail_Additional_Information_Section';
import CRMRetail_Visitor_Information_Section from '@salesforce/label/c.CRMRetail_Visitor_Information_Section';
import standard__navItemPage from '@salesforce/label/c.CRMRetail_TypeOfPage_HUM';
import CRMRetail_InteractionsEventsTab from '@salesforce/label/c.CRMRetail_InteractionEventsTab_HUM'; 
import formFactorPropertyName from '@salesforce/client/formFactor'
import FORM_FACTOR from '@salesforce/client/formFactor';
import APINAME_For_Search_Add from '@salesforce/label/c.APINAME_For_Search_Add';

export default class CrmRetail_newVisitorInformation_LWC_HUM extends NavigationMixin(LightningElement) {

    birthDate;
    waiverDate;
    fieldDisabled;
    isVirtualDisabled;
    visitorViewMode;
    visitorPageHeader;
    accountRowId;
    continueLabel;
    isModalActive = false;
    showVirtualVisitor = true;
    account = {
        'sobjectType': 'Account', 'FirstName': '', 'LastName': '', 'PersonMailingStreet': '',
        'PersonMailingCity': '', 'PersonMailingPostalCode': '',
        'PersonMailingStateCode': '', 'Birthdate__c': '', 'PersonOtherPhone': '',
        'PersonHomePhone': '', 'PersonEmail': '',
        'Waiver_Date__c': '',
        'Product_Sales__c': '', 'Gender__c': '', 'Permission_to_Contact__c': false,
        'PersonMailingState': '', 'CRMRetailNotificationOptOut__c': ' ', 'ParentId': '',
        'RecordTypeId': '','CRMRetail_PreferredName__c': '',
    };

    genderOptions = [];
    switchMap=[];
    stateOptions = [];
    notificationOption = [];
    maxBirthDate;
    todayDate;
    ltngCardClass = 'sfront-ltng_card';
    loadSpinner = false;
    birthDateOverFlowError = Valid_Format_for_Birthdate;
    birthDateRequiredError = Required_for_Birthdate;
    waiverDateOverFlowError = Format_For_Waiver_date;
    waiverDateUnderFlowError = Year_Restriction_for_Waiver_Date;
    waiverDateBadError = Valid_Waiver_Date;
    waiverDateHelpText = Help_Text_For_Waiver_Date;
    zipPatternMismatchError = ZipCodeInvalidMsg;
    phonePatternError = PhoneInvalidMsg;
    pageName = "New Visitor";
    generricUIErrorMsg = CRMRetail_Checkin_Error_Message;
    Required_Fields_Error = CRMRetail_Required_Fields_Error;
    Field_Value_Validity = CRMRetail_Field_Value_Validity;
    Field_Format_Error = CRMRetail_Field_Format_Error;
    Required_Error_Title = CRMRetail_Required_Error_Title;
    additionalInformationSection = CRMRetail_Additional_Information_Section;
    visitorInformationSection = CRMRetail_Visitor_Information_Section;
    addressInformationSection = CRMRetail_Address_Information_Section;

    connectedCallback() {
        this.switchMap.Switch_5084370 = true; 
        this.loadDeafultData();
        this.loadPicklistValues();
        this.procureQueryParams();
        this.deduceViewMode('New');
    }

    handleFirstName(event) {
        if (event.target.checkValidity()) {
            this.account.FirstName = event.target.value;
            event.target.reportValidity();
        }
    }

    handleLastName(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.LastName = event.target.value;
        }
    }

    handleGender(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.Gender__c = event.target.value;
        }
    }

    handleBirthdate(event) {
        let datefield = this.template.querySelector('.bdayfield');
        if (event.target.value == '9999-12-31') {
            datefield.max='9999-12-31';
        }
        else {
            let today = new Date;
            today.setDate(today.getDate() - 1);
            datefield.max=this.formatDate(today, '-', 'yyyymmdd');
        }

        if (datefield.checkValidity()) {
            this.account.Birthdate__c = this.formateDateStr(event.target.value,'/','mmddyyyy');
            this.birthDate = event.target.value;
            event.target.reportValidity();
        }
    }

    handleStreet(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonMailingStreet = event.target.value;
        }
    }

    handleCity(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonMailingCity = event.target.value;
        }
    }

    handleState(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonMailingState = event.target.value;
        }
    }

    handleZip(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonMailingPostalCode = event.target.value;
        }
    }

    handleHomePhone(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonHomePhone = this.formatPhoneNumber(event.target.value);
        }
    }

    handleWorkPhone(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonOtherPhone = this.formatPhoneNumber(event.target.value);
        }
    }

    handleEmail(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.PersonEmail = event.target.value;
        }
    }

    handleWaiverDate(event) {
        if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.Waiver_Date__c = this.formateDateStr(event.target.value,'-','yyyymmdd');
            this.waiverDate = event.target.value;
        }
    }
	
	handlePrefName(event){
		if (event.target.checkValidity()) {
            event.target.reportValidity();
            this.account.CRMRetail_PreferredName__c = event.target.value;
        }		
	}

    handleNotification(event) {
        let _selected = event.detail.value;
        let selectLabels = _selected.map(option => this.notificationOption.find(o => o.value === option).label);
        this.account.CRMRetailNotificationOptOut__c = selectLabels.join(';');
    }

    handleScanCardModalClose() {
        this.isModalActive = false;
    }

    onVirtualVisitor() {
        this.validateAndProcess('virtual');
    }

    onContinue() {
        this.validateAndProcess('continue');
    }

    onCancel(event) {
        if(formFactorPropertyName =='Large')
        {
            this[NavigationMixin.Navigate]({
                type: 'standard__namedPage',
                attributes: {
                    pageName: 'home',
                }
            });
        }
        else{
            this[NavigationMixin.Navigate]({
                type: standard__navItemPage,
                attributes: {
                    apiName: CRMRetail_InteractionsEventsTab,
                }
            });
        }
        
    }

    validateAndProcess(from) {
        if (this.validateAllFields('.validationRequired')) {
            if (from == 'virtual') {
                this.onVirtualVisitorLogic();
            }
            else if (from == 'continue') {
                this.onContinueLogic();
            }
        }
    }


    onContinueLogic() {
        this.isModalActive = true;
        this.startSpinner(false);
    }

    onVirtualVisitorLogic() {		
        this.startSpinner(true);
        for (var key in this.account) {
            if (typeof this.account[key] === "undefined") {
                this.account[key] = null;
            }
            if ((this.switchMap && !this.switchMap.Switch_3782843) && key === "Waiver_Date__c") {
                if (typeof this.account[key] !== "undefined") {
                    if (this.account[key] === "Invalid Date") {
                        this.account[key] = null;
                    }
                }
            }
        }

        handleNewVirtualVisitor({ account: this.account })
            .then((response) => {
                this.startSpinner(false);
                var listOfNotifications = [];
                if (response.visitorType) {
                    listOfNotifications.push(response);
                }
                this.handleEventTraverse(listOfNotifications);
            })
            .catch((error) => {
                this.handleCatch(error);
                this.startSpinner(false);
            })
    }

    handleEventTraverse(notifications) {
        try {
            this.popToast('Success', CRMRetail_CheckInSuccessMsg, 'success', 'dismissible');
            this.account = {};
            this.navigateToURL(notifications);
        } catch (error) {
            this.handleCatch(error);
        }
    }

    navigateToURL(notificationList) {
        try {
            if( (this.switchMap && !this.switchMap.Switch_3850860) || ( notificationList != null && notificationList[0] != null && notificationList[0].listOfNotificationRec) )
            {                
if(FORM_FACTOR=='Small' || FORM_FACTOR=='Medium')
                {
                    this[NavigationMixin.Navigate]({
                        type: standard__navItemPage,
                        attributes: {
                            apiName: APINAME_For_Search_Add
                        }
                    });
                }
                else{
                if (notificationList != null && notificationList.length > 0) {
                    sessionStorage.setItem('notificationList', JSON.stringify(notificationList[0]))
                }
                this[NavigationMixin.Navigate]({
                    type: 'standard__namedPage',
                    attributes: {
                        pageName: 'home',
                    }
                });
}
            }
            else{
                this[NavigationMixin.Navigate]({
                    type: standard__navItemPage,
                    attributes: {
                        apiName: CRMRetail_InteractionsEventsTab
                    }
                });
            }
            
        } catch (error) {
            this.handleCatch(error);
        }
    }

    validateAllFields(className) {
        try {
            let formatValidationReqList = [];
            this.template.querySelectorAll('.formatValidationReq').forEach(function (field) {
                formatValidationReqList.push(field.label);
            });
            let allFields = this.template.querySelectorAll(className);
            let firstErrorField;
            allFields.forEach(function (field) {
                if (!field.checkValidity()) {
                    field.reportValidity();
                    if (firstErrorField == undefined) {
                        firstErrorField = field;
                    }
                }
            });
            if (firstErrorField != undefined) {
                firstErrorField.scrollIntoView({ behavior: "smooth", block: "center", inline: "nearest" });

                if (firstErrorField.value != null && firstErrorField.value != '' && firstErrorField.value != undefined && formatValidationReqList.includes(firstErrorField.label)) {
                    this.popToast(this.Field_Format_Error, this.Field_Value_Validity, 'error', 'pester');
                }
                else {
                    this.popToast(this.Required_Error_Title, this.Required_Fields_Error, 'error', 'pester');
                }
                return false;
            }
            return true;
        } catch (error) {
            this.handleCatch(error);
        }
    }

    formatDate(date, seprator, format) {
        try {
            const dtf = new Intl.DateTimeFormat('en', { year: 'numeric', month: '2-digit', day: '2-digit' })
            const [{ value: mo }, , { value: da }, , { value: ye }] = dtf.formatToParts(date);
            if (format.toLowerCase() == 'yyyymmdd')
                return `${ye}${seprator}${mo}${seprator}${da}`;
            else if (format.toLowerCase() == 'mmddyyyy')
                return `${mo}${seprator}${da}${seprator}${ye}`;
        } catch (error) {
            this.handleCatch(error);
        }
    }

    formateDateStr(dateStr,seperator,format){

        let dateArray = dateStr.split('-');

        if (format.toLowerCase() == 'yyyymmdd')
        {
            return dateArray[0]+seperator+dateArray[1]+seperator+dateArray[2];
        }
        else if (format.toLowerCase() == 'mmddyyyy')
        {
            return dateArray[1]+seperator+dateArray[2]+seperator+dateArray[0];
        }
        
    }

    startSpinner(flag) {
        this.loadSpinner = flag;
    }

    popToast(title, message, variant, mode) {
        const event = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: mode

        });
        this.dispatchEvent(event);
    }

    formatPhoneNumber(phoneNumberString) {
        try {
            var cleaned = ('' + phoneNumberString).replace(/\D/g, '');
            var match = cleaned.match(/^(1|)?(\d{3})(\d{3})(\d{4})$/)
            if (match) {
                var intlCode = (match[1] ? '+1 ' : '');
                var formattedPhoneNum = [intlCode, '(', match[2], ') ', match[3], '-', match[4]].join('');
                return formattedPhoneNum;
            }
            return null;
        } catch (error) {
            this.handleCatch(error);
        }
    }
    deduceViewMode(mode) {
        this.processPageHeader('New Visitor');
        this.continueLabel = 'Continue';
    }
    processPageHeader(headerValue) {
        try {
            if (this.visitorViewMode !== "New" && this.visitorViewMode !== undefined && this.visitorViewMode !== null && this.visitorViewMode !== "") {
                headerValue = this.visitorViewMode + " " + headerValue;
            }
            this.visitorPageHeader = headerValue;
        } catch (error) {
            this.handleCatch(error);
        }
    }
    loadDeafultData() {
        try {
            let today = new Date;
            today.setDate(today.getDate() - 1);
            this.maxBirthDate = this.formatDate(today, '-', 'yyyymmdd');
            
            if(this.switchMap && !this.switchMap.Switch_3782843){
                today = new Date();
                this.maxWaiverDate = this.formatDate(today, '-', 'yyyymmdd');
                today.setFullYear(today.getFullYear() - 3);
                this.minWaiverDate = this.formatDate(today, '-', 'yyyymmdd');
            }
            
        } catch (error) {
            this.handleCatch(error);
        }
    }
    loadPicklistValues() {
        try {
            this.genderOptions = [
                { label: 'None', value: '' },
                { label: 'Male', value: 'Male' },
                { label: 'Female', value: 'Female' },
                { label: 'Undisclosed', value: 'Undisclosed' }
            ];
        } catch (error) {
            this.handleCatch(error);
        }
        getVisitorInfoPageData()
            .then((result) => {
                if (result != null && result != undefined) {

                    if(result.cacheError){
                            this.popToast('Error', result.cacheError, 'error', 'sticky');
                    }

                    if (result.recordTypeId) {
                        this.account.RecordTypeId = result.recordTypeId;
                    }
                    var options = [];
                    result.stateList.forEach(function (state) {
                        if (state !== "Other" && state !== "US Virgin Islands") {
                            options.push({
                                label: state,
                                value: (state === "None") ? "" : state
                            });
                        }
                    });
                    this.stateOptions = options;
                    options = [];
                    result.notificationList.forEach(function (notification) {
                        options.push({
                            label: notification,
                            value: notification
                        });
                    });
                    this.notificationOption = options;
                    this.switchMap = result.switchMap;
                }
            })
            .catch((error) => {
                this.handleCatch(error);
            })
    }
    handleCatch(error) {
        if (!this.isApexError(error)) {
            logException({ msg: JSON.stringify(error.message) })
                .then((result) => {
                });
        }
        this.popToast('Error', this.generricUIErrorMsg, 'error', 'dismissible');
    }
    isApexError(error) {
        if (error.status == 500) {
            return true;
        }
        return false;
    }
    procureQueryParams() {
        try {
            let accountFromLocal = sessionStorage.getItem('c__insertAccountObj'); //Origin Line
           // let accountFromLocal = this.sanitizeHTMLAttribute(sessionStorage.getItem('c__insertAccountObj'));  //DK Cxone change
            sessionStorage.clear();
            if (accountFromLocal) {
                var accObj = JSON.parse(accountFromLocal);
                if (accObj != undefined && accObj != null) {
                    this.isVirtualDisabled = this.sanitizeHTMLAttribute(accObj.ScanCardFlow); //CxOne_FIX
                    if (accObj.Birthdate__c) {
                        this.birthDate =  this.sanitizeHTMLAttribute(this.formatDate(new Date(accObj.Birthdate__c), '-', 'yyyymmdd'));//CxOne_FIX
                    }	
					if(this.switchMap && !this.switchMap.Switch_3782843 && accObj.Waiver_Date__c) {
						this.waiverDate =  this.sanitizeHTMLAttribute(this.formatDate(new Date(accObj.Waiver_Date__c), '-', 'yyyymmdd'));//CxOne_FIX
   				}                   
                    this.account.FirstName = accObj.FirstName;
                    this.account.LastName = accObj.LastName;
                    this.account.Gender__c = accObj.Gender__c;
                    this.account.Birthdate__c = accObj.Birthdate__c;
                    this.account.PersonMailingStreet = accObj.PersonMailingStreet;
                    this.account.PersonMailingCity = accObj.PersonMailingCity;
                    this.account.PersonMailingPostalCode = accObj.PersonMailingPostalCode;
                    this.account.PersonMailingState = accObj.PersonMailingState;
                    this.account.ParentId = accObj.ParentId;
					this.account.CRMRetail_PreferredName__c = accObj.CRMRetail_PreferredName__c;
                }
            } 			
        } catch (error) {
            this.handleCatch(error);
         }
    }
    sanitizeHTMLAttribute(value)
    {
        return typeof value === 'string' ? value.replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#39;') : value;
    } 
}