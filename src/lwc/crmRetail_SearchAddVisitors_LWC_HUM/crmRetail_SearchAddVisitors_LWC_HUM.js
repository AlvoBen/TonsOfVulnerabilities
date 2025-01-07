/*
File Name        : crmRetail_SearchAddVisitors_LWC_HUM.js
Version          : 1.0 
Created Date     : 03/10/2022
Function         : Lightning Web Component used Notification functionality for CRM Retail.
Modification Log :
* Developer                 Code review         Date                  Description
*************************************************************************************************
* Vinoth L       	                	        07/01/2022            Original Version
* Sahil Verma       	                	    09/23/2022            User Story 3850860 - T1PRJ0154546 / SF / MF9 Storefront Modernization - Navigation upon check-in
* Vinoth L                                      12/13/2022            User Story 4046005: T1PRJ0154546 / SF / MF11 Storefront Interaction Duplicate Logic Update
* Mohamed Thameem      	               	        06/12/2023            User Story 4695921: T1PRJ0154546 / SF/ MF9 Storefront - Allow Search By Phone Number Only

**************************************************************************************************
*/
import { LightningElement, api,wire,track } from 'lwc';
import { NavigationMixin } from 'lightning/navigation';
import fetchStateValue from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchStateValue';
import SearchVisitor_PolicyIdInfo_HUM from '@salesforce/label/c.SearchVisitor_PolicyIdInfo_HUM';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import processMemberIdSearch from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.processMemberIdSearch';
import runProcessAccountSearchLogic from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.runProcessAccountSearchLogic';
import processInactiveMemberCheckIn from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.processInactiveMemberCheckin';
import fetchLocationValue from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchLocationValue';
import initiateVisitorCheckIn from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.initiateVisitorCheckIn';
import fetchAccountDetails from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchAccountDetails';
import {subscribe, MessageContext, APPLICATION_SCOPE, unsubscribe} from 'lightning/messageService';
import CRMRetailMessageChannel from "@salesforce/messageChannel/CRMRetailMessageChannel__c";
import { loadStyle } from 'lightning/platformResourceLoader';
import multiLineCSS from '@salesforce/resourceUrl/CRMRetail_multiLineToast_SR_HUM';
import acknowledgeNotifications from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.acknowledgeNotifications';
import crmRetailStylesheet from '@salesforce/resourceUrl/CRMRetailSearchAddVisitors_SR_HUM';

import CRMRetail_CheckInSuccessMsg from '@salesforce/label/c.CRMRetail_CheckInSuccessMsg_ModernizedHome';
import CRM_Retail_Waiver_Success_Text from '@salesforce/label/c.CRM_Retail_Waiver_Success_Text';
import CRM_Retail_Signed_Error_text from '@salesforce/label/c.CRM_Retail_Signed_Error_text';
import CRM_Retail_Waiver_Failure_Text from '@salesforce/label/c.CRM_Retail_Waiver_Failure_Text';
import CRM_Retail_Waiver_Functionality_Expiration_Success from '@salesforce/label/c.CRM_Retail_Waiver_Functionality_Expiration_Success';
import CRMRetail_ISSUCCESS_HUM from '@salesforce/label/c.CRMRetail_ISSUCCESS_HUM';
import CRMRETAIL_CHECKIN_ERROR_MSGR from '@salesforce/label/c.CRMRETAIL_CHECKIN_ERROR_MSGR';
import CRMRetail_Inactive_CheckIn_Error from '@salesforce/label/c.CRMRetail_Inactive_CheckIn_Error';
import CRMRetail_BlockVisitor_Icon_Name from '@salesforce/label/c.CRMRetail_BlockVisitor_Icon_Name';
import CRMRetail_InactiveMember_Label from '@salesforce/label/c.CRMRetail_InactiveMember_Label';
import CRMRetail_Check_Icon_Name from '@salesforce/label/c.CRMRetail_Check_Icon_Name';
import CRMRetail_Member from '@salesforce/label/c.CRMRetail_Member';

import cancelButtonLabel from '@salesforce/label/c.CRMRetail_Cancel_ButtonLabel';
import CRMRetail_Future_Modal_Title from '@salesforce/label/c.CRMRetail_Future_Modal_Title';
import CRMRetail_Future_Modal_Message from '@salesforce/label/c.CRMRetail_Future_Modal_Message';

import CRMRetail_Future_Interaction_Date from '@salesforce/label/c.CRMRetail_Future_Interaction_Date';
import CRMRetail_New_Visitor_Page from '@salesforce/label/c.CRMRetail_New_Visitor_Page';
import CRMRetail_Future_Error from '@salesforce/label/c.CRMRetail_Future_Error';
import CRMRetail_Duplicate_Message from '@salesforce/label/c.CRMRetail_Duplicate_Message';
import CRMRetail_Virtual_Error from '@salesforce/label/c.CRMRetail_Virtual_Error';
import CRMRetail_Onsite_Error from '@salesforce/label/c.CRMRetail_Onsite_Error';
import CRMRetail_Checkin_Error_Message from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import CRMRetail_No_Records_Error from '@salesforce/label/c.CRMRetail_No_Records_Error';
import CRMRetail_No_Records_Text from '@salesforce/label/c.CRMRetail_No_Records_Text';
import CRMRetail_Minimum_Search_Name_Entry from '@salesforce/label/c.CRMRetail_Minimum_Search_Name_Entry';
import CRMRetail_Minimum_Search_No_Entry from '@salesforce/label/c.CRMRetail_Minimum_Search_No_Entry';
import CRMRetail_Person_Birthdate_Key from '@salesforce/label/c.CRMRetail_Person_Birthdate_Key';
import CRMRetail_Person_HomePhone_Key from '@salesforce/label/c.CRMRetail_Person_HomePhone_Key';
import CRMRetail_PeronsMailingState_Key from '@salesforce/label/c.CRMRetail_PeronsMailingState_Key';
import CRMRetail_Zipcode_Key from '@salesforce/label/c.CRMRetail_Zipcode_Key';
import CRMRetail_FirstName_Key from '@salesforce/label/c.CRMRetail_FirstName_Key';
import CRMRetail_LastName_Key from '@salesforce/label/c.CRMRetail_LastName_Key';
import CRMRetail_MemberID_Key from '@salesforce/label/c.CRMRetail_MemberID_Key';
import CRMRetail_CheckIn_event from '@salesforce/label/c.CRMRetail_CheckIn_event';
import CRMRetail_Virtual_CheckIn_Event from '@salesforce/label/c.CRMRetail_Virtual_CheckIn_Event';
import CRMRetail_Onsite_checkIn from '@salesforce/label/c.CRMRetail_Onsite_checkIn';
import CRMRetail_Virtual_CheckIn from '@salesforce/label/c.CRMRetail_Virtual_CheckIn';
import CRMRetail_Replace_Card from '@salesforce/label/c.CRMRetail_Replace_Card';
import CRMRetail_EditVisitor from '@salesforce/label/c.CRMRetail_EditVisitor';
import DATE_VALIDATION_TEXT from '@salesforce/label/c.Valid_Format_For_Calendar_Date';
import CRMRetail_OCheckIn_Text from '@salesforce/label/c.CRMRetail_OCheckIn_Text';
import CRMRetail_VCheckIn_Text from '@salesforce/label/c.CRMRetail_VCheckIn_Text';
import CRMRetail_ReplaceCard_Key from '@salesforce/label/c.CRMRetail_ReplaceCard_Key';
import CRMRetail_DupIntList_Key from '@salesforce/label/c.CRMRetail_DupIntList_Key';
import CRMRetail_Duplicate_Interaction from '@salesforce/label/c.CRMRetail_Duplicate_Interaction';
import CRMRetail_NotificationList_Key from '@salesforce/label/c.CRMRetail_NotificationList_Key';
import CRMRetail_InteractionDate_Key from '@salesforce/label/c.CRMRetail_InteractionDate_Key';
import CRMRetail_NotificationRecords_Key from '@salesforce/label/c.CRMRetail_NotificationRecords_Key';
import CRMRetail_CheckInSuccess_Text from '@salesforce/label/c.CRMRetail_CheckInSuccess_Text';
import CRMRetail_DuplicateInteraction_Key from '@salesforce/label/c.CRMRetail_DuplicateInteraction_Key';
import CRMRetail_MaxReached_Key from '@salesforce/label/c.CRMRetail_MaxReached_Key';
import CRMRetail_EditVisitor_Key from '@salesforce/label/c.CRMRetail_EditVisitor_Key';
import CRMRetail_Location_Occupancy_Title from '@salesforce/label/c.CRMRetail_Location_Occupancy_Title';
import CRMRetail_Location_Occupancy_Exceeded from '@salesforce/label/c.CRMRetail_Location_Occupancy_Exceeded';
import CRMRetail_IsMaxReached_HUM from '@salesforce/label/c.CRMRetail_IsMaxReached_HUM';
import CRM_Retail_Visitor_Check_In from '@salesforce/label/c.CRM_Retail_Visitor_Check_In';
import SearchVisitor_SearchBtnLabel_HUM from '@salesforce/label/c.SearchVisitor_SearchBtnLabel_HUM';
import CRMRetail_Scan_Card_Button from '@salesforce/label/c.CRMRetail_Scan_Card_Button';
import CRMRetail_Search_Results_Section from '@salesforce/label/c.CRMRetail_Search_Results_Section';
import SearchVisitor_LastNameMsg_HUM from '@salesforce/label/c.SearchVisitor_LastNameMsg_HUM';
import SearchVisitor_FirstNameMsg_HUM from '@salesforce/label/c.SearchVisitor_FirstNameMsg_HUM';
import SearchVisitor_FirstNameLabel_HUM from '@salesforce/label/c.SearchVisitor_FirstNameLabel_HUM';
import SearchVisitor_LastNameLabel_HUM from '@salesforce/label/c.SearchVisitor_LastNameLabel_HUM';
import CRMRetail_New_Text from '@salesforce/label/c.CRMRetail_New_Text';
import SearchVisitor_PhoneMsg_HUM from '@salesforce/label/c.SearchVisitor_PhoneMsg_HUM';
import SearchVisitor_ZipCodeInvalidMsg_HUM from '@salesforce/label/c.SearchVisitor_ZipCodeInvalidMsg_HUM';
import CRMRetail_Home_text from '@salesforce/label/c.CRMRetail_Home_text';
import SearchVisitor_ResetLabel_HUM from '@salesforce/label/c.SearchVisitor_ResetLabel_HUM';
import CRMRetail_Error_Label from '@salesforce/label/c.CRMRetail_Error_Label';
import CRMRetail_Error_variant from '@salesforce/label/c.CRMRetail_Error_variant';
import CRMRetail_Success_Variant from '@salesforce/label/c.CRMRetail_Success_Variant';
import CRMRetail_HomePage_URL_Part from '@salesforce/label/c.CRMRetail_HomePage_URL_Part';
import CRMRetail_Modernized_cmp from '@salesforce/label/c.CRMRetail_Modernized_cmp';
import CRMRetail_NAMEURL_Key from '@salesforce/label/c.CRMRetail_NAMEURL_Key';
import CRMRetail_Name_Key from '@salesforce/label/c.CRMRetail_Name_Key';
import CRMREtail_HomeLocation_Error from '@salesforce/label/c.CRMREtail_HomeLocation_Error';
import CRMRetail_LocationError from '@salesforce/label/c.CRMRetail_LocationError';
import CRMRetail_Location_Key from '@salesforce/label/c.CRMRetail_Location_Key';
import standard__navItemPage from '@salesforce/label/c.CRMRetail_TypeOfPage_HUM';
import CRMRetail_InteractionsEventsTab from '@salesforce/label/c.CRMRetail_InteractionEventsTab_HUM';
import INACTIVE_POLICY from '@salesforce/label/c.CRMRetail_InactiveMem_Exists';
import NONMEMBERACCOUNT_EXISTS from '@salesforce/label/c.CRMRetail_NonMemberAcc_Exists';
import UNEXPECTED_ERROR from '@salesforce/label/c.CRMRetail_Unexpected_Error';
import getSwitchMap from '@salesforce/apex/CRMRetail_HomePage_LC_HUM.fetchSwitchResults';
import timezone from '@salesforce/i18n/timeZone';
import CRMRetail_Future_member from '@salesforce/label/c.CRMRetail_Future_member';

export default class CrmRetail_SearchAddVisitors_LWC_HUM extends NavigationMixin(LightningElement) {
    customCSS = multiLineCSS + '/CRMRetail_multiLineToastCss.css';
    label = {
        DATE_VALIDATION_TEXT
    };
    recievedMessage
    transferSubscription = null;
    listOfStates = [];
    value = 'None';
    selectedStateValue;
    idHelpText = SearchVisitor_PolicyIdInfo_HUM;
    ifSearchCriteriaValid = false;
    searchResultsTitle = CRMRetail_Search_Results_Section;
    firstName;
    lastName;
    firstNameRequired = false;
    lastNameRequired = false;
    lastNameRequiredError = SearchVisitor_LastNameMsg_HUM;
    firstNameRequiredError = SearchVisitor_FirstNameMsg_HUM;
    NoEntryError = CRMRetail_Minimum_Search_No_Entry;
    NameEntryError = CRMRetail_Minimum_Search_Name_Entry;
    lastNameLabel = SearchVisitor_LastNameLabel_HUM;
    firstNameLabel = SearchVisitor_FirstNameLabel_HUM;
    memberId = '';
    crmRetailZipCodePattern = '^[0-9]{5,9}$';
    zipCodePatternMismatch =  SearchVisitor_ZipCodeInvalidMsg_HUM;
    crmRetailPhonePattern = '^[0-9]{10,10}$';
    phonePatternMismatch = SearchVisitor_PhoneMsg_HUM;
    personBirthdate;
    personHomePhone;
    personMailingState;
    zipCode;
    inputList;
    data;
    error;
    isLoaded;
    isCheckInDisabled = false;
    currentLocation;
    currentInteractionDate;
    cacheValue;
    todayDate;
    isInactiveMemberCheckIn;
    isSpinnerVisible;
    showBarcodeModal;
    isModalActive;
    @api recordId;
    rows;
    inactiveValidity;
    inactiveModalOpen;
    inactiveMemberData;
    selectedRecords;
    isNotificationModalActive= false;
    isDuplicateModalOpen;
    duplicateIntList=[];
    genericModalList=[];
    notificationData;
    lstSelectedRecords = [];
    pageName = CRMRetail_Home_text;
    replaceAccount;
    columns;
    notificationDetails;
    isWaiverAcknowledged=false;
    isCssLoaded = false;
    newButtonLabel = CRMRetail_New_Text;
    virtualButtonLabel = CRMRetail_Virtual_CheckIn;
    onsiteButtonLabel = CRMRetail_Onsite_checkIn;
    scanCardButtonLabel = CRMRetail_Scan_Card_Button;
    resetButtonLabel = SearchVisitor_ResetLabel_HUM;
    searchButtonLabel = SearchVisitor_SearchBtnLabel_HUM;
    visitorCheckInPageTitle = CRM_Retail_Visitor_Check_In;
    genericModalHeader;
    @wire(MessageContext)
    context;
    accountURL;
    sourceComp = CRMRetail_Modernized_cmp;
    isVisitorCheckInDisabled = false;
    switchMap;
    groupedFields = [];
    standAloneFields = [];
    searchCriteria = new Map();
    isSwitchOn_4695921=true;
    prevfirstNameFieldErrorMessage = 'First Name is required when an ID is not provided.';
    prevlastNameFieldErrorMessage = 'Last Name is required when an ID is not provided.';
    prevminimumSearchCriteriaWhenNoField = 'Minimum search criteria is First Name and Last Name plus one additional field (Zip Code, State, Birthdate, or Phone) -OR- Medicare/Humana Member ID.';
    prevminimumSearchCriteriaWhenNameField = 'Minimum search criteria is First Name, Last Name and one additional field (Zip Code, State, Birthdate, or Phone).';
    showFutureCheckInModal;

    cancelButtonLabel = cancelButtonLabel;
    futureModalTitle = CRMRetail_Future_Modal_Title;
    futureModalMessage = CRMRetail_Future_Modal_Message;

    inactiveColumns = [
        { label: 'Name', fieldName: 'accountURL', type: 'url',typeAttributes: {label: { fieldName: 'Name' }, target: '_blank'}},
           { label: 'Birthdate', fieldName: 'Birthdate__c'},
           { label: 'State', fieldName: 'PersonMailingState'},
           { label: 'Zip Code', fieldName: 'PersonMailingPostalCode' },
           { label: 'Phone', fieldName: 'PersonHomePhone' }
       ];
       
    isInactiveMember;
    isExpiredMember;
    tableData;
    hasVisitorRecords;
    displayMsg;
    continuationMsg;
    inactiveMemberAccDetails;
    visitorData;



    @track searchInputList = [
        {
            id: 'firstname',
            label: this.firstNameLabel,
            fieldapi: CRMRetail_FirstName_Key,
            criteriaName: 'accountInfo',
            type: 'text',
            isStandAlone: false,
            onfocusEvent: this.handleOnInputFocus,
            onchangeEvent: this.handleOnInputChange,
            onblurEvent: this.handleOnInputBlur,
            name: 'FirstName',
            class: 'personHomePhoneFields',
            placeholder: 'Enter First Name...',
            priority: 3
        },
        {
            id: 'lastname',
            label: this.lastNameLabel,
            fieldapi: CRMRetail_LastName_Key,
            criteriaName: 'accountInfo',
            type: 'text',
            isStandAlone: false,
            onfocusEvent: this.handleOnInputFocus,
            onchangeEvent: this.handleOnInputChange,
            onblurEvent: this.handleOnInputBlur,
            name: 'LastName',
            class: 'personHomePhoneFields',
            placeholder: 'Enter Last Name...',
            minlength: 2,
            messageWhenTooShort: 'A minimum of 2 characters are required in the last name.',
            priority: 3
        },
        {
            id: 'zip',
            label: 'Zip Code',
            fieldapi: CRMRetail_Zipcode_Key,
            criteriaName: 'accountInfo',
            type: 'text',
            isStandAlone: false,
            onfocusEvent: this.handleOnInputFocus,
            onchangeEvent: this.handleOnInputChange,
            name: 'PersonMailingPostalCode',
            class: 'zipCodeFields',
            placeholder: 'Enter Zip Code...',
            pattern: this.crmRetailZipCodePattern,
            messageWhenPatternMismatch: this.zipCodePatternMismatch,
            priority: 3
        },
        {
            id: 'state',
            label: 'State',
            fieldapi: CRMRetail_PeronsMailingState_Key,
            criteriaName: 'accountInfo',
            type: 'text',
            isStandAlone: false,
            isCombobox: true,
            onchangeEvent: this.handleOnInputChange,
            name: 'PersonMailingState',
            class: 'personMailingStateFields',
            options: [],
            value: this.value,
            priority: 3
        },
        {
            id: 'birthdate',
            label: 'Birthdate',
            fieldapi: CRMRetail_Person_Birthdate_Key,
            criteriaName: 'accountInfo',
            type: 'date',
            isStandAlone: false,
            onchangeEvent: this.handleOnInputChange,
            name: 'PersonBirthdate',
            class: 'personBirthdateFields',
            datestyle: "short",
            messageWhenbadInput: this.label.DATE_VALIDATION_TEXT,
            priority: 3
        },
        {
            id: 'id',
            label: 'ID',
            type: 'text',
            isStandAlone: true,
            onfocusEvent: this.handleOnInputFocus,
            onchangeEvent: this.handleOnInputChange,
            name: CRMRetail_MemberID_Key,
            class: 'memberIdFields width_auto',
            placeholder: 'Enter Humana Member ID/Medicare ID',
            priority: 1,
            isCustomSearch: true,
            helptext: this.idHelpText,
            customSearchHandlermethod: this.handleMemberIdSearch
        },
        { showOR: true },
        {
            id: 'phone',
            label: 'Phone',
            fieldapi: CRMRetail_Person_HomePhone_Key,
            type: 'text',
            isStandAlone: true,
            onfocusEvent: this.handleOnInputFocus,
            onchangeEvent: this.handleOnInputChange,
            name: CRMRetail_Person_HomePhone_Key,
            class: 'personHomePhoneFields',
            placeholder: 'Enter Phone Number...',
            pattern: this.crmRetailPhonePattern,
            messageWhenPatternMismatch: this.phonePatternMismatch,
            priority: 2,
        }
    ];

    subscribeMessage() {
        if (!this.transferSubscription) {
            this.transferSubscription = subscribe(this.context, CRMRetailMessageChannel, (message) => { this.handleMessage(message) }, { scope: APPLICATION_SCOPE });
        }
    }

    handleMessage(message){
        if(this.notificationData)
        {
            this.isNotificationModalActive=true;
            this.notificationData=message.modalData;
           
        }
        if(message.selectedInteractionDate){
            this.currentInteractionDate = message.selectedInteractionDate;            
        }
        if(message.SelectedLocation){
            this.currentLocation = message.SelectedLocation;
        }
        if((message.SelectedLocation && message.SelectedLocation == 'None') || (this.currentLocation && this.currentLocation == 'None')){
            this.isVisitorCheckInDisabled = true;
            this.showToastMessage(CRMREtail_HomeLocation_Error,CRMRetail_LocationError,CRMRetail_Error_variant);
            this.ifSearchCriteriaValid = false;
        }
        else if(message.selectedInteractionDate && this.currentLocation && this.currentLocation != 'None'){
            this.isVisitorCheckInDisabled = false;
        }
        else{
            this.isVisitorCheckInDisabled = false;
        }        
    }
    renderedCallback() {            
        if(this.isCssLoaded) return            
            loadStyle(this,crmRetailStylesheet+'/CRMRetailSearchAddVisitors_SR_HUM.css').then(()=>{
                this.isCssLoaded = true;            
        })
        .catch(error=>{
            this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
        }); 
    }    

    @wire(fetchStateValue)     
    wiredFetchStateValue({error,data}){
        if(data){
            data = JSON.stringify(data);
            var lstOfStateNames = data.split(',');
            for(let i=0; i < lstOfStateNames.length; i++){
                lstOfStateNames[i] = lstOfStateNames[i].replaceAll('[','');  
                lstOfStateNames[i] = lstOfStateNames[i].replaceAll(']','');  
                lstOfStateNames[i] = lstOfStateNames[i].replaceAll('"','');                
                this.listOfStates = [...this.listOfStates ,{value: lstOfStateNames[i] , label: lstOfStateNames[i]} ];                                              
            }
            this.searchInputList.find(obj => obj.id == 'state').options = this.listOfStates;
            this.error = undefined;
        }
        else if (error){
            this.error = error;
            this.listOfStates = undefined;
        }
    }

    columns=[
        { label: 'Name', fieldName: 'nameURL',type: 'url', initialWidth: 185,sortable: 'true',
        typeAttributes: {label: { fieldName: 'Name' },target: '_blank'}},   
        { label: 'Birthdate', fieldName: 'Birthdate',type:'text', initialWidth: 120,sortable: 'true',
        typeAttributes : {day:"numeric", month:"numeric", year:"numeric"}},           
        { label: 'Visitor Id', fieldName: 'visitorId',sortable: 'true'},             
        { label: 'Zipcode', fieldName: 'PersonMailingPostalCode',sortable: 'true'},
        { label: 'Phone', fieldName: 'PersonHomePhone',sortable: 'true'},
        { label: 'Member', fieldName: 'Member',type: 'button-icon',
        typeAttributes: { 
            title: { fieldName: 'memberIconLabel' },
            variant:'bare', 
            class: { fieldName: 'iconStyle' },
			
            iconName: {fieldName:'isMember'},
            }
        },
        { type: 'action', typeAttributes: { rowActions: this.getRowActions }
        }
    ];
    crmFunctionalitySwitch(){
        getSwitchMap()
            .then((data) => {
                this.switchMap = data;
                this.error = undefined;
                this.onLoadSwitchHandler();
            })
            .catch((error) =>{
                this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);  
            })
    }

     getRowActions(row, doneCallback) {
        if(row.isReplaceEligible) {
            doneCallback([
                { label: CRMRetail_Onsite_checkIn, name: CRMRetail_OCheckIn_Text },
                { label: CRMRetail_Virtual_CheckIn, name: CRMRetail_VCheckIn_Text },
                { label: CRMRetail_Replace_Card, name: CRMRetail_ReplaceCard_Key,disabled:false},
                { label: CRMRetail_EditVisitor, name: CRMRetail_EditVisitor_Key},
            ]);
        }else{
            doneCallback([
                { label: CRMRetail_Onsite_checkIn, name: CRMRetail_OCheckIn_Text },
                { label: CRMRetail_Virtual_CheckIn, name: CRMRetail_VCheckIn_Text },
                { label: CRMRetail_Replace_Card, name: CRMRetail_ReplaceCard_Key,disabled:true},
                { label: CRMRetail_EditVisitor, name: CRMRetail_EditVisitor_Key},
            ]);
        }
    }

    handleRowAction( event ) {
        const actionName = event.detail.action.name;
        this.rows = event.detail.row;
        if(this.rows.RecordTypeName == CRMRetail_Member){
            this.rows.isReplaceEligible = false;
        }
        switch ( actionName ) {
            case CRMRetail_OCheckIn_Text:   
                this.toggleSpinner(true);
                this.newCheckin(CRMRetail_CheckIn_event);
                break;
            case CRMRetail_VCheckIn_Text:
                this.toggleSpinner(true);
                this.newCheckin(CRMRetail_Virtual_CheckIn_Event);                
                break;
            case CRMRetail_ReplaceCard_Key:                                
                this.showBarcodeModal = true;
                this.isModalActive = true;
                this.pageName = CRMRetail_Replace_Card;
                this.replaceAccount = this.rows;
                break;
            case CRMRetail_EditVisitor_Key:
                this[NavigationMixin.Navigate]({
                    type: 'standard__recordPage',
                    attributes: {
                        recordId: this.rows.Id,
                        objectApiName: 'account',
                        actionName: 'edit'
                    }
                });
            default:
        }
    }
    handleSortAccountData(event) {       
        this.sortBy = event.detail.fieldName;       
        this.sortDirection = event.detail.sortDirection;           
        this.sortAccountData(event.detail.fieldName, event.detail.sortDirection); 
    }
    sortAccountData(fieldname, direction) {        
        let parseData = JSON.parse(JSON.stringify(this.data));
        fieldname = (fieldname === CRMRetail_NAMEURL_Key) ? CRMRetail_Name_Key : fieldname;
        let keyValue = (a) => {            
            return a[fieldname];
        };
        let isReverse = direction === 'asc' ? 1: -1;
           parseData.sort((x, y) => {
            x = keyValue(x) ? keyValue(x) : ''; 
            y = keyValue(y) ? keyValue(y) : '';           
            return isReverse * ((x > y) - (y > x));
        });        
        this.data = parseData;
    }
    connectedCallback(){
        this.subscribeMessage();
        if(sessionStorage.getItem(CRMRetail_DupIntList_Key) != null && sessionStorage.getItem(CRMRetail_DupIntList_Key))
        {
            var value = sessionStorage.getItem(CRMRetail_DupIntList_Key);
            sessionStorage.clear();
            this.genericModalHeader = CRMRetail_Duplicate_Interaction;
            this.duplicateIntList.push(value);
            this.isDuplicateModalOpen=true;
        }
        if(sessionStorage.getItem(CRMRetail_NotificationList_Key) != null && sessionStorage.getItem(CRMRetail_NotificationList_Key)){            
            var value = JSON.parse(sessionStorage.getItem(CRMRetail_NotificationList_Key));
            sessionStorage.clear(); 
            var accId = value.accountRec?value.accountRec.Id : '';
            fetchAccountDetails({accountId:accId})
            .then((result)=>{
                value.visitorId = result.GCM_Visitor_Barcode__c ? result.GCM_Visitor_Barcode__c : '';
                value.accountRec.Name=result.Name ? result.Name : '';
                value.accountRec.Enterprise_ID__c=result.Enterprise_ID__c ? result.Enterprise_ID__c : null;
                this.notificationData = value;
                this.isNotificationModalActive = true;
            })
            .catch((error)=>{
                this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            });                      
        }
        fetchLocationValue({})
        .then((displayCriteria)=>{
            this.currentInteractionDate = this.formatDate(displayCriteria[CRMRetail_InteractionDate_Key])
            this.currentLocation =  displayCriteria[CRMRetail_Location_Key];
            this.displayCriteria = JSON.stringify(displayCriteria);
            if(!this.currentLocation || this.currentLocation == 'None'){
                this.isVisitorCheckInDisabled = true;
                this.ifSearchCriteriaValid = false;            
                this.showToastMessage(CRMREtail_HomeLocation_Error,CRMRetail_LocationError,CRMRetail_Error_variant);
            }else{
                this.isVisitorCheckInDisabled = false;
            }            
        })
        .catch((error)=>{
            this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
        }); 
        this.crmFunctionalitySwitch();
        this.searchCriteria.set('accountInfo', { requiredFields: ['firstname', 'lastname'], optionallyRequired: ['birthdate', 'state', 'zip'], noOfOptionalRequired: 1, error: this.NameEntryError, onchangeCriteriaFail: [{ error: this.firstNameRequiredError, fieldstodiplay: ['firstname'] }, { error: this.lastNameRequiredError, fieldstodiplay: ['lastname'] }] });
        this.groupedFields = this.searchInputList.filter(obj => obj?.criteriaName == 'accountInfo');
        this.standAloneFields = this.searchInputList.filter(obj => obj.isStandAlone || obj.showOR);
    }

    onLoadSwitchHandler() {
        if (!this.switchMap['Switch_4695921']) {
            this.isSwitchOn_4695921 = false;
            this.firstNameRequiredError = this.prevfirstNameFieldErrorMessage;
            this.lastNameRequiredError = this.prevlastNameFieldErrorMessage;
            this.NoEntryError = this.prevminimumSearchCriteriaWhenNoField;
            this.NameEntryError = this.prevminimumSearchCriteriaWhenNameField;
            this.searchInputList.find(obj => obj.id == 'phone').isStandAlone = false;
            this.searchInputList.find(obj => obj.id == 'phone').criteriaName = 'accountInfo';
            this.searchInputList.find(obj => obj.id == 'phone').priority = 3;
            this.searchInputList = this.searchInputList.filter(obj => !obj.showOR);
            this.searchCriteria.set('accountInfo', { requiredFields: ['firstname', 'lastname'], optionallyRequired: ['birthdate', 'state', 'zip', 'phone'], noOfOptionalRequired: 1, error: this.NameEntryError, onchangeCriteriaFail: [{ error: this.firstNameRequiredError, fieldstodiplay: ['firstname'] }, { error: this.lastNameRequiredError, fieldstodiplay: ['lastname'] }] });
            this.groupedFields = this.searchInputList.filter(obj => obj?.criteriaName == 'accountInfo');
            this.standAloneFields = this.searchInputList.filter(obj => obj.isStandAlone || obj.showOR);
        }
        else {
            this.isSwitchOn_4695921 = true;
        }
        
    }

    handleOnInputBlur(event) {
        let inputFieldIns = this.searchInputList.find(obj => obj.name == event.target.name);
        let errorMsg = "";
        if (event.target.value) {
            if (inputFieldIns.minlength) {
                if (inputFieldIns.minlength > event.target.value.length)
                {
                    errorMsg = inputFieldIns.messageWhenTooShort;
                    this.removeOrAddStar(inputFieldIns.name,'add');
                }
                else
                {
                    this.removeOrAddStar(inputFieldIns.name,'remove');
                }
            }
            if(!errorMsg)
            {
                this.removeOrAddStar(inputFieldIns.name,'remove');
            }
            event.target.setCustomValidity(errorMsg);
            event.target.reportValidity();
        }
    }
    
    handleStateChange(event) {
        const selectedOption = event.detail.value;
        this.selectedStateValue = selectedOption;
    }

    handleOnInputFocus(event) {
        let priorityNoAndFieldValues = this.getPriorityNoAndFieldValues();
        let elementId = event.target.dataset.item;
        let priotyNumber = priorityNoAndFieldValues.priorityToSearch != 0 ? priorityNoAndFieldValues.priorityToSearch : this.searchInputList.find(obj => obj.id == elementId).priority;
        let valueMap = priorityNoAndFieldValues.valueMap;
        let validationResult = this.validateCriteria(priotyNumber, valueMap);
        if (!validationResult.isValid) {
            validationResult?.criteria?.onchangeCriteriaFail?.forEach(entry => {
                entry.fieldstodiplay.forEach(field => {
                    let name = this.template.querySelector('[data-item="' + field + '"]').name;
                    if (!valueMap?.get(name)?.value) {
                        this.template.querySelector('[data-item="' + field + '"]').setCustomValidity(entry.error);
                        this.template.querySelector('[data-item="' + field + '"]').reportValidity();
                        this.removeOrAddStar(name,'add');
                    }
                })
            })
        }
        else if (validationResult.isStandAlone) {
            this.searchInputList.filter(obj => !obj.isStandAlone && !obj.showOR).forEach(entry => {
                this.template.querySelector('[data-item="' + entry.id + '"]')?.setCustomValidity("");
                this.template.querySelector('[data-item="' + entry.id + '"]')?.reportValidity();
                this.removeOrAddStar(entry.name,'remove');
            })
        }
    }

    handleOnInputChange(event) {
        this.handleOnInputFocus(event);
    }

    standardAccountSearchHandler(inputList, _this, singleFieldSearch) {
        runProcessAccountSearchLogic({ inputList: inputList, singleFieldSearch: singleFieldSearch })
            .then((data) => {
                _this.data = JSON.parse(JSON.stringify(data));
                if (_this.data.length > 0) {
                    _this.processSearchResultsOnSuccess(_this);
                }
                else {
                    _this.processSearchResultsOnNoData(_this);
                }
            })
            .catch((error) => {
                _this.showToastMessage(CRMRetail_Checkin_Error_Message, CRMRetail_Error_Label, CRMRetail_Error_variant);
                _this.toggleSpinner(false, _this);
                _this.processSearchResultsOnFailure(_this);
            });
    }

    handleMemberIdSearch(event, _this) {
        processMemberIdSearch({ memberId: event.value })
            .then((data) => {
                _this.data = JSON.parse(JSON.stringify(data));
                if (_this.data.length > 0) {
                    _this.processSearchResultsOnSuccess(_this);
                }
                else {
                    _this.processSearchResultsOnNoData(_this);
                }
            })
            .catch((error) => {
                _this.showToastMessage(CRMRetail_Checkin_Error_Message, CRMRetail_Error_Label, CRMRetail_Error_variant);
                _this.toggleSpinner(false, _this);
                _this.processSearchResultsOnFailure(_this);
            });
    }

    processSearchResultsOnNoData(_this)
    {
        _this.toggleSpinner(false, _this);
        _this.showToastMessage(CRMRetail_No_Records_Error, CRMRetail_No_Records_Text, 'warning');
        _this.isCheckInDisabled = true;
        _this.isLoaded = true;
    }

    validateInputOnSearchClick(event) {
        let priorityNoAndFieldValues = this.getPriorityNoAndFieldValues();
        this.processsearch(priorityNoAndFieldValues.priorityToSearch, priorityNoAndFieldValues.valueMap);
    }

    processsearch(priorityToSearch, valueMap) {
        let criteriaResult = this.validateCriteria(priorityToSearch, valueMap);
        let listOfFields = this.searchInputList.filter(obj => obj.priority === priorityToSearch);
        if (listOfFields.length != 0) {
            if (criteriaResult.isValid) {
                if (!this.isThereFieldValidationError(listOfFields)) {
                    return 0;
                }
                this.isCheckInDisabled = false;
                this.ifSearchCriteriaValid = true;
                this.toggleSpinner(true);
                if (criteriaResult.isStandAlone) {
                    if (listOfFields[0].isCustomSearch) {
                        listOfFields[0].customSearchHandlermethod(valueMap.get(listOfFields[0].name), this);
                    }
                    else {
                        let inputlist = {};
                        inputlist[listOfFields[0].fieldapi] = valueMap.get(listOfFields[0].name).value;
                        this.standardAccountSearchHandler(JSON.stringify(inputlist), this, true);
                    }
                }
                else {
                    this.standardAccountSearchHandler(JSON.stringify(criteriaResult.inputlist), this, false);
                }
            }
            else {
                this.showToastMessage(criteriaResult.criteria.error,CRMRetail_Error_Label, CRMRetail_Error_variant);
            }
        }
        else {
            this.showToastMessage(this.NoEntryError,CRMRetail_Error_Label, CRMRetail_Error_variant);
        }
    }

    getPriorityNoAndFieldValues() {
        let valueMap = new Map();
        let elementList = this.template.querySelectorAll('lightning-input, lightning-combobox');
        let priorityToSearch = 0;
        elementList.forEach(entry => {
            if (!(entry.tagName?.toLowerCase() == 'lightning-combobox' && entry.value == 'None') && entry.value) {
                valueMap.set(entry.name, entry);
                if (priorityToSearch == 0 || priorityToSearch > this.searchInputList.find(obj => { return obj.name === entry.name }).priority) {
                    priorityToSearch = this.searchInputList.find(obj => { return obj.name === entry.name }).priority;
                }
            }
        })
        return { priorityToSearch: priorityToSearch, valueMap: valueMap }
    }

    validateCriteria(priorityToSearch, valueMap) {
        let listOfFields = this.searchInputList.filter(obj => obj.priority === priorityToSearch);
        if (listOfFields.length != 0) {
            if (listOfFields.length == 1 && listOfFields[0].isStandAlone) {
                return { isValid: true, isStandAlone: true };
            }
            else {
                let requiredCount = 0;
                let optionallyRequiredCount = 0;
                let critria = this.searchCriteria.get(listOfFields[0]?.criteriaName);
                let inputlist = {};
                listOfFields.forEach(entry => {
                    if (critria.requiredFields.includes(entry.id) && valueMap.has(entry.name) && valueMap.get(entry.name).value) {
                        inputlist[entry.fieldapi] = valueMap.get(entry.name).value;
                        requiredCount++;
                    }
                    if (critria.optionallyRequired.includes(entry.id) && valueMap.has(entry.name) && valueMap.get(entry.name).value) {
                        inputlist[entry.fieldapi] = valueMap.get(entry.name).value;
                        optionallyRequiredCount++;
                    }
                })

                if (critria?.requiredFields?.length === requiredCount) {

                    if (optionallyRequiredCount >= critria.noOfOptionalRequired) {
                        return { isValid: true, criteria: critria, inputlist: inputlist };
                    }
                    else {
                        return { isValid: false, criteria: critria };
                    }
                }
                else {
                    return { isValid: false, criteria: critria };
                }
            }
        }
    }

    isThereFieldValidationError(listOfFields) {
        let isValid = true;
        listOfFields.forEach(entry => {
            let inputElement = this.template.querySelector('[data-item="' + entry.id + '"]');
            if (!inputElement.checkValidity()) {
                isValid = false;
            }
            if (entry.minlength && entry.minlength > inputElement.value?.length) {
                inputElement.setCustomValidity(entry.messageWhenTooShort);
                inputElement.reportValidity();
                isValid = false;
            }
        })
        return isValid;
    }

    resetFields() {
        let elementList = this.template.querySelectorAll('lightning-input, lightning-combobox');
        elementList.forEach(entry => {
            entry.value = entry.tagName.toLowerCase() === 'lightning-combobox' ? 'None' : null;
            entry.setCustomValidity("");
            entry.reportValidity();
            this.removeOrAddStar(entry.name,'remove');
        })
        this.ifSearchCriteriaValid = false;
        this.isCheckInDisabled = false;
    }

    removeOrAddStar(inputName,removeOrAdd)
    {
        let input = this.searchInputList.find(obj => obj.name == inputName);
        let requiredStar = '* ';
        if(removeOrAdd === 'remove' && input?.label?.startsWith(requiredStar))
        {
            input.label = input.label.substring(2);
        }
        else if(removeOrAdd === 'add' && !input?.label?.startsWith(requiredStar))
        {
            input.label = requiredStar+input.label;
        }
    }

    processSearchResultsOnSuccess(_this) {
        if (_this == undefined) _this = this;
        _this.data.forEach(Record => {
            Record.Id = Record.Id ? Record.Id : '';
            Record.Name = Record.Name ? Record.Name : '';
            Record.Birthdate = Record.Birthdate != undefined ? _this.formatDate(Record.Birthdate) : '';
            Record.visitorId = Record.visitorId ? Record.visitorId : '';
            Record.PersonMailingPostalCode = Record.PersonMailingPostalCode ? Record.PersonMailingPostalCode : '';
            Record.PersonHomePhone = Record.PersonHomePhone ? _this.formatPhoneNumber(Record.PersonHomePhone) : '';
            Record.iconStyle = 'memberIconStyle';
            if (Record.inactiveCheck) {
                Record.isMember = CRMRetail_BlockVisitor_Icon_Name;
                Record.memberIconLabel = CRMRetail_InactiveMember_Label;
                Record.isReplaceEligible = false;
            }










            else if (Record.RecordTypeName == CRMRetail_Member) {
                //Record.isMember = CRMRetail_Check_Icon_Name;
                Record.isReplaceEligible = false;
				if(Record.isFutureMember){
					Record.isMember = 'standard:assigned_resource';
					Record.memberIconLabel = CRMRetail_Future_member;
					Record.iconStyle = 'futureMemberStyling';
				}
				else{
					Record.isMember = CRMRetail_Check_Icon_Name;
				}



            }
            else {
                Record.isReplaceEligible = true;
            }
            //Record.memberIconStyle = 'memberIconStyle';
            _this.isLoaded = true;
            Record.nameURL = Record.Id ? window.location.origin + '/lightning/r/' + Record.Id + '/view' : '';
            _this.toggleSpinner(false, _this);
        });
    }

    processSearchResultsOnFailure(_this) {
        if (_this == undefined) _this = this;
        _this.data = undefined;
        _this.toggleSpinner(false, _this);
        _this.showToastMessage(CRMRetail_Checkin_Error_Message, CRMRetail_Error_Label, CRMRetail_Error_variant);
    }

    formatDate(inputDate){
        const today = new Date(inputDate);
        var dd = String(today.getDate()).padStart(2, '0');
        var mm = String(today.getMonth() + 1).padStart(2, '0'); 
        var yyyy = today.getFullYear();

        var finalDate = mm + '/' + dd + '/' + yyyy;        
        return finalDate;
    }
    formatPhoneNumber(phone){
        var cleaned = ('' + phone).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);                 
        if(match)
        {
            phone = '(' + match[1] + ') ' + match[2] + '-' + match[3];
        } 
        return phone;
    }
    clearPreviousSelections(){
        this.lstSelectedRecords = [];
        this.selectedRecords = undefined;
    } 
    onInactiveCheckIn(){
        this.toggleSpinner(true);
        this.isInactiveMemberCheckIn = true;
        this.processCheckIn(CRMRetail_CheckIn_event);
    }
    onVirtualInactiveCheckIn(){
        this.toggleSpinner(true);
        this.isInactiveMemberCheckIn = true;
        this.processCheckIn(CRMRetail_Virtual_CheckIn_Event);
    }      
    onOnsiteCheckIn(){
        this.toggleSpinner(true);
        this.rows = undefined;
        this.newCheckin(CRMRetail_CheckIn_event);
    }
    onVirtualCheckIn(){
        this.toggleSpinner(true);
        this.rows = undefined;
        this.newCheckin(CRMRetail_Virtual_CheckIn_Event);
    }
    newCheckin(newCheckinType){   
        this.clearPreviousSelections();         
        if(this.rows){              
            this.selectedRecords = this.rows;
        }else{
            this.selectedRecords = this.template.querySelector("lightning-datatable") ? this.template.querySelector("lightning-datatable").getSelectedRows()[0] : undefined;               
        }        
        if(this.selectedRecords){
            this.inactiveValidity = this.selectedRecords.inactiveCheck;            
            if(this.inactiveValidity && !this.isInactiveModalOpen){
                this.showToastMessage(CRMRetail_Inactive_CheckIn_Error,CRMRetail_Error_Label,CRMRetail_Error_variant);
                this.selectedRecords = undefined;                                       
                this.toggleSpinner(false);
            }
			else if(this.switchMap.Switch_5225414 && this.selectedRecords.isFutureMember){  
				this.showFutureCheckInModal = true;			
                this.selectedRecords = undefined; 				
                this.toggleSpinner(false);
            }
            else{                 
                this.isCheckInDisabled = true;                                            
                this.processCheckIn(newCheckinType); 
            }                        
        }  
        else{
            let errorType= newCheckinType == CRMRetail_CheckIn_event?CRMRetail_Onsite_Error:CRMRetail_Virtual_Error;
            this.toggleSpinner(false);
            this.showToastMessage(CRMRETAIL_CHECKIN_ERROR_MSGR,errorType,'warning');
        } 
    } 
    validateFutureDate(){
        this.todayDate = new Date((new Date().toLocaleString('en-US', {timeZone: timezone})));        
        var today = this.todayDate.setHours(0,0,0,0);
        var currentInteractionDate = new Date(this.currentInteractionDate.toLocaleString('en-US', {timeZone: timezone})).setHours(0,0,0,0);        
        if(currentInteractionDate > today){
            this.showToastMessage(CRMRetail_Future_Error, CRMRetail_Future_Interaction_Date,CRMRetail_Error_variant); 
            return false;         
        }
        return true;
    }
    processCheckIn(checkInType){  
        this.inactiveModalOpen = false;
        if(!this.validateFutureDate()){
            this.toggleSpinner(false);
            this.isCheckInDisabled = false;            
        }
        else{                        
            this.createCheckIn(checkInType);
        }                
    }    
    createCheckIn(newCheckInType){                 
        if(this.isInactiveMemberCheckIn){  
            var visitorAccountId = JSON.parse(this.selectedRecords.inactiveMemberVisitorAccount).Id; 
            var visitorAccountName = JSON.parse(this.selectedRecords.inactiveMemberVisitorAccount).Name; 
            var expiredMemId = JSON.parse(this.selectedRecords.inactiveMember).Id;
            var dateOrigin = 'displayDate';
            processInactiveMemberCheckIn({accountId : visitorAccountId, accName : visitorAccountName,expiredMemAccId : expiredMemId, checkInType:newCheckInType,dateOrigin: dateOrigin})
            .then((data) => {                
                this.data = data;                
                if(this.data[CRMRetail_ISSUCCESS_HUM] == 'true'){
                    this.notificationData = {};                           
                    this.notificationData = this.data[CRMRetail_NotificationRecords_Key];                               
                    if(this.switchMap && this.switchMap.Switch_3850860){                                                                                 
                        var parsedNotificationData =  JSON.parse(this.notificationData);                             
                        if(parsedNotificationData && parsedNotificationData != '[]' && parsedNotificationData.length >0){ 
                            this.isNotificationModalActive = true; 
                        }  
                        else{
                            this.navigateToInteractionPage(standard__navItemPage, CRMRetail_InteractionsEventsTab);
                        } 
                    }  
                    else if(this.notificationData){
                        this.isNotificationModalActive = true; 
                    }                                           
                    this.showToastMessage(CRMRetail_CheckInSuccessMsg,CRM_Retail_Waiver_Functionality_Expiration_Success,CRMRetail_Success_Variant);                    
                }
                else{ 
                    this.duplicateIntList = [];                 
                    if(this.data[CRMRetail_IsMaxReached_HUM] == 'true'){       
                        this.duplicateIntList.push(CRMRetail_Location_Occupancy_Exceeded); 
                        this.genericModalHeader = CRMRetail_Location_Occupancy_Title;
                    }
                    else{
                        this.duplicateIntList.push(visitorAccountName + ' '+CRMRetail_Duplicate_Message); 
                        this.genericModalHeader = CRMRetail_Duplicate_Interaction;
                    }                                            
                    this.isDuplicateModalOpen = true;
                } 
                this.toggleSpinner(false);                      
            })
            .catch((error) =>{
                this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            });            
            this.ifSearchCriteriaValid = false; 
            this.resetFields();
        }
        else{           
            this.isDuplicateModalOpen = false;
            let accId = this.selectedRecords['Id'];
            let accName = this.selectedRecords['Name'];
            var obj = {};
            obj[accId] = accName;            
            initiateVisitorCheckIn({mapOfAccIdName :obj, checkInType:newCheckInType})
            .then((data) => {
                this.data = data;
                if(this.data[CRMRetail_ISSUCCESS_HUM] == 'true'){
                    this.notificationData = {};                          
                    this.notificationData = this.data[CRMRetail_NotificationRecords_Key];
                    if(this.switchMap && this.switchMap.Switch_3850860){
                        var parsedNotificationData =  JSON.parse(this.notificationData);                              
                        if(parsedNotificationData && parsedNotificationData != '[]' && parsedNotificationData.length >0){ 
                            this.isNotificationModalActive = true; 
                        }  
                        else{
                            this.navigateToInteractionPage(standard__navItemPage, CRMRetail_InteractionsEventsTab);
                        } 
                    }  
                    else if(this.notificationData){
                        this.isNotificationModalActive = true; 
                    }                                 
                    this.showToastMessage(CRMRetail_CheckInSuccessMsg,CRM_Retail_Waiver_Functionality_Expiration_Success,CRMRetail_Success_Variant);                    
                }
                else{ 
                    this.duplicateIntList = [];                 
                    if(this.data[CRMRetail_IsMaxReached_HUM] == 'true'){       
                        this.duplicateIntList.push(CRMRetail_Location_Occupancy_Exceeded); 
                        this.genericModalHeader = CRMRetail_Location_Occupancy_Title;
                    }
                    else{             
                        this.duplicateIntList.push(accName + ' '+CRMRetail_Duplicate_Message);                         
                        this.genericModalHeader = CRMRetail_Duplicate_Interaction;
                    }                                            
                    this.isDuplicateModalOpen = true;
                } 
                this.toggleSpinner(false);                       
            })
            .catch((error) =>{                
                this.showToastMessage(CRMRetail_Checkin_Error_Message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            });           
            this.ifSearchCriteriaValid = false; 
            this.resetFields();
        }
    }
    onNewBtnClicked(){        
        this.clearPreviousSelections();
        this.toggleSpinner(true);                
        this.selectedRecords = this.template.querySelector("lightning-datatable") ? this.template.querySelector("lightning-datatable").getSelectedRows()[0] : undefined;               
        this.todayDate = new Date().toISOString(); 
        this.currentInteractionDate = new Date(this.currentInteractionDate).toISOString();        
        if(this.selectedRecords){
            this.inactiveValidity = this.selectedRecords.inactiveCheck;  
        }
        if(!this.validateFutureDate()){
            this.toggleSpinner(false);
            this.isCheckInDisabled = false;            
        } 
        else if(this.inactiveValidity){                                     
            var inactiveMem = [];                        
            inactiveMem.push({"isInactiveMember" : true});            
            inactiveMem.push({"isScanCard":false});
            inactiveMem.push({"inactiveMemberAcc":this.selectedRecords.inactiveMember});
            if(this.selectedRecords.inactiveMemberVisitorAccount){
                inactiveMem.push({"NonMemberAccount":this.selectedRecords.inactiveMemberVisitorAccount});
            }                         
            this.handleInactiveModalData(inactiveMem);

			
            this.inactiveMemberData = inactiveMem;  
            this.inactiveModalOpen = true;
            this.toggleSpinner(false); 
        }
        else{
            this.prepareNewClickDetails();
        }
    }
    prepareNewClickDetails(){
        this.toggleSpinner(true);        
        this.pageName = CRMRetail_New_Visitor_Page;
        let cmpDef = {
            componentDef: "c:crmRetail_newVisitorInformation_LWC_HUM",
        };    
        let encodedDef = btoa(JSON.stringify(cmpDef));
        this[NavigationMixin.Navigate]({
            type: "standard__webPage",
            attributes: {
                url: CRMRetail_HomePage_URL_Part + encodedDef
            }
        });
        this.toggleSpinner(false);        
    }      
    handleCloseModal(){
        this.inactiveModalOpen = false;
        this.toggleSpinner(false);





    } 
	    handleFutureCloseModal(){
		this.showFutureCheckInModal = false;
        this.toggleSpinner(false);
	}   
	
    showToastMessage(message,title,variant){
        var mode = (variant == 'success' || variant == 'warning') ? 'dismissable' : 'sticky';      
        const evt = new ShowToastEvent({
            title: title,
            message: message,
            variant: variant,
            mode: mode
        });
        this.dispatchEvent(evt);
    }
    
    toggleSpinner(toggleFactor, _this){
        if (_this == undefined) { _this = this; }
        _this.isSpinnerVisible = toggleFactor;
    }

    handleScanCardButtonClick(){
        if(this.validateFutureDate()){
            this.isModalActive=true;
        }      
    }
    handleScanCardModalClose(){
        this.isModalActive=false;        
    }    
    redirectToFilesTab(event){
        this[NavigationMixin.GenerateUrl]({    
            type: 'standard__objectPage',    
            attributes: {    
                objectApiName: 'ContentDocument',    
                actionName: 'home',    
            },    
        }).then(url => { window.open(url) });    
    }    
    handleScanCardEvt(event){                      
        var origin = event.detail.eventOrigin; 
        this.notificationData = {};                           
		switch (origin){
            case CRMRetail_CheckInSuccess_Text:
                    this.showToastMessage(CRMRetail_CheckInSuccessMsg,CRM_Retail_Waiver_Functionality_Expiration_Success,CRMRetail_Success_Variant);
                    this.notificationData = event.detail.notificationData != '[]' ? event.detail.notificationData : {};                    				
                    this.isModalActive = false;	
                    if(this.notificationData[0]){ 
                        this.isNotificationModalActive = true; 
                    }  
                    else if(this.switchMap && this.switchMap.Switch_3850860){
                        this.navigateToInteractionPage(standard__navItemPage, CRMRetail_InteractionsEventsTab);
                    }                  
                    break;
            case CRMRetail_DuplicateInteraction_Key: 
                    this.genericModalHeader = CRMRetail_Duplicate_Interaction;                  
                    this.duplicateIntList = event.detail.DupIntList;
                    this.isDuplicateModalOpen = true;
                    this.isNotificationModalActive = false;
                    this.isModalActive = false;                    
                    break;
            case CRMRetail_MaxReached_Key:
                    this.genericModalHeader = CRMRetail_Location_Occupancy_Title;                    
                    this.duplicateIntList = event.detail.expiredList;
                    this.isDuplicateModalOpen = true;
                    this.isNotificationModalActive = false;
                    this.isModalActive = false;
                    break;                                             
        }       
    }
    navigateToHome(){
        this[NavigationMixin.Navigate]({
            type: 'standard__namedPage',
            attributes: {
                pageName: 'home'
            }
        });
    }
    handleduplicatemodalclosedevt(){        
        this.isModalActive=false;
        this.isDuplicateModalOpen=false;
        this.genericModalHeader='';
        this.duplicateIntList=[];
    }
    handleNotificationModalClose(event){  
        this.isNotificationModalActive=false;
        this.isModalActive=false;
        if(this.switchMap && this.switchMap.Switch_3850860 && (this.pageName == CRMRetail_Home_text || this.pageName == CRMRetail_Replace_Card)){
            this.navigateToInteractionPage(standard__navItemPage, CRMRetail_InteractionsEventsTab);
        }          
    }
    handleinactivememModalCloase(){        
        this.isModalActive=false;
    }
    ackNotification(event){
        this.toggleSpinner(true);        
        this.isNotificationModalActive = false;
        var notificationData = event.detail.data;  
        this.isWaiverAcknowledged = false;
        if(notificationData.ack){
           
            if(notificationData.waiverDate){          
                this.isWaiverAcknowledged = true;
              
            }            
            var lstOfRecToUpdate=[];
            lstOfRecToUpdate.push(notificationData);
          
            var lstOfAccountIds =[];
            lstOfAccountIds.push(notificationData.accId);
     
            var currLocation = notificationData.currentLocation;
        }
        else{ 
            if(this.switchMap && this.switchMap.Switch_3850860 && (this.pageName == CRMRetail_Home_text || this.pageName == CRMRetail_Replace_Card)){
                this.navigateToInteractionPage(standard__navItemPage, CRMRetail_InteractionsEventsTab);           
            }           
            this.toggleSpinner(false);
            this.notificationData = null;
            this.isNotificationModalActive = false;
        }
        if(lstOfRecToUpdate!==null && lstOfRecToUpdate!==undefined && lstOfRecToUpdate.length>0){
            this.toggleSpinner(true);
            let data1 = acknowledgeNotifications({inputJSON: JSON.stringify(lstOfRecToUpdate),accIds : lstOfAccountIds,currentLocation : currLocation});
            acknowledgeNotifications({inputJSON: JSON.stringify(lstOfRecToUpdate),accIds : lstOfAccountIds,currentLocation : currLocation})
           .then((data) => {
                if(this.isWaiverAcknowledged){     
                    this.showToastMessage(CRM_Retail_Waiver_Success_Text,CRM_Retail_Waiver_Functionality_Expiration_Success,CRMRetail_Success_Variant);              
                }
                if(!data){
                    this.showToastMessage(CRM_Retail_Waiver_Failure_Text,CRMRetail_Error_Label,CRMRetail_Error_variant);
                }  
                if(this.switchMap && this.switchMap.Switch_3850860 && (this.pageName == CRMRetail_Home_text || this.pageName == CRMRetail_Replace_Card)){
                    this.navigateToInteractionPage(standard__navItemPage, CRMRetail_InteractionsEventsTab);           
                }               
                this.toggleSpinner(false);                
            })
            .catch((error) => {
                this.toggleSpinner(false);
                this.showToastMessage(CRM_Retail_Signed_Error_text,CRMRetail_Error_Label,CRMRetail_Error_variant);
            });
        }        
    }
    navigateToInteractionPage(type,apiName){
        this[NavigationMixin.Navigate]({
            type: type,
            attributes: {
                apiName: apiName
            }
        });
    }
      
    handleInactiveModalData(inactiveMemberData)
    {        
        this.showInactiveMem = true;

        var sMsg;
        try                
        {  
            this.isInactiveMember= (inactiveMemberData[0].isInactiveMember) ? true : false;
            this.isExpiredMember= (inactiveMemberData[0].isExpiredMember) ? true : false;
            this.inactiveMemberAccDetails = JSON.parse(inactiveMemberData[2].inactiveMemberAcc);                      
                     

            if(inactiveMemberData[3]){
                this.visitorData = JSON.parse(inactiveMemberData[3].NonMemberAccount);
                this.hasVisitorRecords=true;
                sMsg = NONMEMBERACCOUNT_EXISTS;
                this.displaymsgforExistingAccPrefix=sMsg.split('|')[0]+' ';
                this.displaymsgforExistingAccSuffix=sMsg.split('|')[1];
            }
            else{
                    this.displaymsgforInactiveAcc = INACTIVE_POLICY;
            }
            if(this.visitorData){
                this.dataObj = [{           
                    "Name":this.visitorData.Name,
                    "Birthdate__c":this.inactiveMemberAccDetails.Birthdate__c,
                    "PersonMailingState":this.visitorData.PersonMailingState,
                    "PersonMailingPostalCode":this.visitorData.PersonMailingPostalCode,
                    "PersonHomePhone": this.formatPhoneNumber(this.visitorData.PersonHomePhone),
                    "Id":this.visitorData.Id,
                    "accountURL":'/'+this.visitorData.Id
                }];       
                this.tableData=this.dataObj;
            }            
        }
        catch(err){
            if(err.message){
                this.showToastMessage(err.message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            }
            else{
                this.showToastMessage(UNEXPECTED_ERROR,CRMRetail_Error_Label,CRMRetail_Error_variant);
            }
        }    

    }

    formatPhoneNumber(phone) 
    {
        var cleaned = ('' + phone).replace(/\D/g, '');
        var match = cleaned.match(/^(\d{3})(\d{3})(\d{4})$/);                 
        if(match)
        {
            phone = '(' + match[1] + ') ' + match[2] + '-' + match[3];
        } 
        return phone;
    }

    handleNewButtonClick() {
        var accObj;
        try {
            accObj = {
                "FirstName": this.inactiveMemberAccDetails.FirstName,
                "LastName": this.inactiveMemberAccDetails.LastName,
                "Birthdate__c": this.inactiveMemberAccDetails.Birthdate__c,
                "Gender__c": this.inactiveMemberAccDetails.Gender__c,
                "PersonMailingStreet": this.inactiveMemberAccDetails.PersonMailingStreet,
                "PersonMailingCity": this.inactiveMemberAccDetails.PersonMailingCity,
                "PersonMailingState": this.inactiveMemberAccDetails.PersonMailingState,
                "PersonMailingPostalCode": this.inactiveMemberAccDetails.PersonMailingPostalCode,
                "ScanCardFlow": this.isScanCard
            };
            if ((this.isScanCard && this.isInactiveMember) || (!this.isScanCard)) {
                accObj.ParentId = this.inactiveMemberAccDetails.Id;
            }

            let navConfig;

            sessionStorage.setItem('c__insertAccountObj', JSON.stringify(accObj));

            let cmpDef = {
                componentDef: "c:crmRetail_newVisitorInformation_LWC_HUM",
            };

            let encodedDef = btoa(JSON.stringify(cmpDef));
            navConfig = {
                type: "standard__webPage",
                attributes: {
                    url: "/one/one.app#" + encodedDef
                }
            };

            if (navConfig) {
                this[NavigationMixin.Navigate](navConfig);
            }

        }
        catch (err) {
            if(err.message){
                this.showToastMessage(err.message,CRMRetail_Error_Label,CRMRetail_Error_variant);
            }
            else{
                this.showToastMessage(UNEXPECTED_ERROR,CRMRetail_Error_Label,CRMRetail_Error_variant);
            }
        }
    }

}