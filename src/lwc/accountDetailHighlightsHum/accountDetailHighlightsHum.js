/*******************************************************************************************************************************
LWC JS Name : accountDetailHighlightsHum.js
Function    : This JS serves as controller to accountDetailHighlightsHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                        12/18/2020                    initial version(azure # 1648022)
* Arpit Jain					                       02/12/2021		            Click To Dial	
* Mohan kumar N					                       04/02/2021		            Work phone plus extension	
* Joel George			        						03/16/2021		           REQ 1895067
* Joel George			        						03/22/2021		           DF 2717
* Supriya Shastri                                      03/23/2021                  US-1999420  
* Mohan Kumar N			        						03/08/2021		           US-1749564
* Supriya                                               04/26/2021                 US-1464401
* Mohan                                               04/28/2021                   Jump link scroll issue fix
* Joel                                               05/11/2021                   Account Name showing on highlights
* Supriya                                              06/04/2021                  US: 2176313 
* Ankima                                              08/17/2021                  US:2233885 - Label Change
* Ankima                                              08/18/2021                  US:2233885 - Label Change  Update
* Aishwarya Pawar                                      10/19/2021                  REQ-268408, REQ-2684091 and REQ- 2684061
* Mohan                                                10/27/2021                US: 2440592
* Vardhman Jain                                        10/21/2021                  US:2098890- Account Management - Group Account - Launch Member Search 
* Supriya Shastri				                       01/13/2021				   US-2954236
* Aishwarya Pawar				                       02/17/2022				   DF-4486 fix
* Abhishek Mangutkar								   05/09/2022				   US-2871585
* Supriya Shastri				                       03/16/2021				   US-1985154
* Aishwarya Pawar				                       06/01/2022				   DF-4973 fix
* Nilanjana Sanyal                                     03/09/2022			       US-2498215 - Saperating the icon display in a common component commonHighlighPanelIconHum
* Muthukumar 										   06/21/2022				   DF-5050
* Aishwarya Pawar				                       07/25/2022				   DF-5442 fix
* Vardhman Jain                                        04/22/2022                  US-3046016_3045989: Group Account logging stories changes.
* Muthukumar                                           09/08/2022                  US-3279519 update plan demographic 
* Santhi Mandava                                       11/01/2022                  US:3813238 Displaying Update plan demographics button
* visweswararao j                                      11/01/2022                  User Story 3862542: T1PRJ0170850 - MF19080 - Lightning- Templates/Update Plan Demographics Changes
* Manohar Billa                                        02/16/2023                  US:3272618 - Verify Demographics changes for QS
* Manohar Billa                                        02/20/2023                 REQ - US4179702 : Guidance Alert for QS
* Jonathan Dickinson                                   03/31/2023                User Story 4414983: T1PRJ0865978 - Next Best Action- Move the Existing alerts on Person Account & Plan Member to right hand side 
* Akshay Gulve                                         04/17/2023                 US: 4475037 - Case Management: Auto Fill "Case Origin" "Interacting with type" " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Jaguars)
* Deepakkumar Khandelwal							   06/30/2023				  US_4742449_ T1PRJ0865978 - INC2384724/Contact Handling Alert Icon is displayed even when there are no contact handling alerts for that member. 
* Pooja Kumbhar                                        07/13/2023                 US 4772880 - T1PRJ0865978 - INC2392465 - Lightning Command Center RAID #030: Quick Start overriding manually entered phone number with phone number from Account
 *Hima Bindu Ramayanam							       07/12/2023				  User Story 4802575: T1PRJ0865978 - INC2410933/Consumer/Toggling between Person Account tabs displays information from 'other' tab
* Muthukumar										   08/16/2023				  User Story 4879230: T1PRJ0865978 - INC2430109 / Missing Residential Address on Person Account Page
* Vardhman Jain                                        10/20/2023                   US-5009031 update Commercial demographic
 ****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { getRTLayout, detailConstants } from './layoutConfig';
import retrieveTaxIds from '@salesforce/apex/AccountConsumerIdComponent_LC_HUM.retrieveConsumerIds';
import startRequestDemographics from '@salesforce/apexContinuation/VerifyDemograhics_LC_HUM.APIService';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import getMemberIcons from '@salesforce/apex/MemberIcons_LC_HUM.getMemberIconStatus';
import { getUserGroup, ageCalculator, hcConstants, getSessionItem, setSessionItem, compareDate,getLocaleDate, copyToClipBoard  } from 'c/crmUtilityHum';
import { getLabels } from 'c/customLabelsHum';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import hasCRMS_111_StridesAccess from '@salesforce/customPermission/CRMS_111_StridesAccess';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import hasCRMS_1200_DemographicUpdateAccess from '@salesforce/customPermission/CRMS_1200_MedicareMedicaid_Demographic_Update';
import hasCRMS_1210_Commercial_Demographic_Update from '@salesforce/customPermission/CRMS_1210_Commercial_Demographic_Update';
import { invokeWorkspaceAPI,openLWCSubtab } from 'c/workSpaceUtilityComponentHum';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import loggingLMSChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import {performLogging,getLoggingKey,checkloggingstatus} from 'c/loggingUtilityHum';
import createCaseAndRedirect from '@salesforce/apex/SaveCaseDemographic_LC_HUM.createCaseAndRedirect';
import {uConstants} from 'c/updatePlanDemographicConstants'; 
import verDemGrapChannel from '@salesforce/messageChannel/VerifyDemographicsChannel__c';
import CallbackNumberMessageChannel from '@salesforce/messageChannel/CallbackNumberMessageChannel__c';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';

export default class AccountDetailHighlightsHum extends LightningElement {
    @track oCustomDetails;
    @track oDetails;
    @api recordId;
    @track sRecordTypeName;
    @track oUserGroup;
    @api sAction;
    @track accLegacyDelete;
	@api sAccountPageName;
    @track bShowDemographics;
    @track bShowVerifyButton;
    @track bShowUpdateDemographicsBtn = false;
    @track showInteractions;
    @track showRTISection = false;
    @track sphone;
    @track sHomephone;
    @track sOtherphone;
    @track iAge;
    @track sName;
    @track sTaxIds;
    @track sTaxIdHover;
    @track bTaxIdViewAll;
    @track sWorkPhonePlusExt = "";
    @track memberIcons = [];
    @track interactionPills = [];
    @track labels = getLabels();
    @track policyLabel;
    @track constants = hcConstants;
    @track panelDetails;
    @track profileName;
    @track netWorkId;
    @track workQueue;
    @track uPharmacy;
	@track sGroupNumber;
	@track bShowMemberSearch;
    @track bShowModal = false;
	@track showContactHandlingAlert =false;
    @track questionDetails= {
        question: '',
        answer:'',
        pValue:''
      };
	@track loggingkey;
    @track bShowVerDmoGrapMsg; //Verify Demographics Event
    @track demGrpRes; //Verify Demographics
    @track interactionId;
	@track mAddress;
	@track rAddress; 
    @track bSwitch5009031 = false;
	showLoggingIcon = false;       
    autoLogging = true;
	personid;
    selectedItemValue;
	
   dataWire;
    @track bIntialWireCall = true;
	@track bSpinner =false;
    @track buttonsConfig = [{
        text: getLabels().HUMAlertsAcknowledge,
        isTypeBrand: true,
        eventName: hcConstants.CLOSE
     }];
     @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD,NETWORK_ID_FIELD,CURRENT_QUEUE]
      }) wireuser({
        error,
        data
      }) {
        if (error) {
          this.error = error;
        } else if (data) {
          this.profilename = data.fields.Profile.value.fields.Name.value;
          this.netWorkId = data.fields.Network_User_Id__c.value;
          this.workQueue = data.fields.Current_Queue__c.value;
        }
      }
    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
        this.pageState = this.pageRef?.state ??  null;
        this.getInteractionId();
    }
	
	@wire(isCRMFunctionalityONJS,{sStoryNumber:'5009031'})
    switchFuntion({error,data}){
        if(data){
            this.bSwitch5009031 = data['5009031'];
        }
        if(error){
            console.log('error---',error)
        }
    }


    @wire(getRecord, {
        recordId: '$recordId', fields: ['Account.Account_Security_Answer__c','Account.Account_Security_Question__c','Account.Account_Security_EndDate__c','Account.Account_Security_Access__c','Account.Name','Account.ETL_Record_Deleted__c', 'Account.Enterprise_ID__c']
    })
    wiredAccount({
        error,
        data
    }) {
        if (data) {
            this.dataWire=data;
            this.sRecordTypeName = data.recordTypeInfo.name;
			if(this.sRecordTypeName == detailConstants.rtMem && hasCRMS_1200_DemographicUpdateAccess){
                this.bShowUpdateDemographicsBtn = true;
            }
			if((this.sRecordTypeName == detailConstants.rtMem) && hasCRMS_1210_Commercial_Demographic_Update){
                this.bShowUpdateCommercialDemographicsBtn = true;
            }
            this.accLegacyDelete = data.fields.ETL_Record_Deleted__c.value;
			this.personid = data.fields.Enterprise_ID__c.value;
            this.fetchPanelData(this.sRecordTypeName);
            if(this.bIntialWireCall){
                this.popupOperation(this.dataWire);
             }
             this.bIntialWireCall= false;
			 this.showLoggingIcon = this.isMemberAccount()||this.isGroupAccount();
        }
        else if (error) {
            console.log('error in wire--', error);
        }
    }
	
	handleOnselect(event) {
        this.selectedItemValue = event.detail.value;
        if(this.selectedItemValue == 'Retail'){
            this.updatePlanDemographic();
        }
        else if(this.selectedItemValue == 'Commercial'){
            this.updateCommercialDemographic();
        }
    }

    popupOperation(dataWire){
        let termDate= dataWire.fields.Account_Security_EndDate__c.value ? getLocaleDate(dataWire.fields.Account_Security_EndDate__c.value): null;
        let todayDate= getLocaleDate(new Date());
      
        if(compareDate(termDate,todayDate)===1){
          this.questionDetails = {
             question: dataWire.fields.Account_Security_Question__c.value,
             answer: dataWire.fields.Account_Security_Answer__c.value,
             pValue: dataWire.fields.Account_Security_Access__c.value
          }
          if(this.questionDetails.pValue !== null){
            this.openModal(this.recordId);
          }
        }
     }
     // start-US:2098890- Account Management - Group Account - Launch Member Search 
	@track encodedMemberData = {
        member :  {
            sFirstName: "",
            sLastName: "",
            sMemberid: "",
            sBirthdate: "",
            sPhone: "",
            sSuffix: "",
            sGroupNumber: "",
            sPID: "",
            sState: "",
          }
     };
     navitageToMemberSearch() {
         this.encodedMemberData['member'].sGroupNumber = this.sGroupNumber;
         let componentDef = {
             componentDef: "c:memberSearchFormHum",
             attributes: {
                encodedData: this.encodedMemberData,
                isNavigatedFrmGrp: true
           } 
         };
     // Encode the componentDefinition JS object to Base64 format to make it url addressable
         let encodedComponentDef = btoa(JSON.stringify(componentDef));
     
         invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
           if (isConsole) {
             invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
               invokeWorkspaceAPI('openSubtab', {
                 parentTabId: focusedTab.tabId,
                 url: '/one/one.app#' + encodedComponentDef,
                 focus: true
               }).then(tabId => {
                 invokeWorkspaceAPI('setTabLabel', {
                     tabId: tabId,
                     label: 'Member Search'
                     });
                 invokeWorkspaceAPI('setTabIcon', {
                     tabId: tabId,
                     icon: 'standard:search',
                     iconAlt: ''
                     });
               });
             });
           }
         });
     }
 //End- US:2098890- Account Management - Group Account - Launch Member Search 
 
 
    loadCommonCss() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css'),
			loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

	/* 2498215 Implementation for Icon display*/
    loadMemberIcons() {
        const me = this;
        const {hLimit} = hcConstants;
        console.log('Horizontal icon limit in Account Page', hLimit);
        if (me.isMemberAccount()) {
            const iconParams = {
                sPageName: 'Member Account',
                sRecordId: me.recordId
            };
            getMemberIcons(iconParams).then((result) => {
                if (result && result.bIconsPresnt) {
                if(this.showContactHandlingAlert ==false){
                    result.lstMemberIcons.forEach(ele=>{
                        if(ele.sIconName === 'ContactHandlingAlert') ele.bIconVisible = this.showContactHandlingAlert;
                    });
                }
                me.memberIcons = result.lstMemberIcons;
                this.template.querySelector("c-common-highlight-panel-icon-hum").loadMemberIcons(me.memberIcons,hLimit);
                }

            }).catch((error) => {
                console.log('Error Occured', error);
            });
        }
    }
	
    handlecontactevent(event){
        const myContactAlert =event.detail.ContactHandlingAlertValue;
        if (myContactAlert == true){
            this.showContactHandlingAlert =true ;
        }else{
            this.showContactHandlingAlert =false;
        }
        this.loadMemberIcons();
    }
	
      //This method checks whether user is pharmacy user or not
	checkPharamcyUser(){
        if(hasCRMS_111_StridesAccess && (this.profilename === 'Humana Pharmacy Specialist' || ((this.profilename === 'Customer Care Specialist' || this.profilename === 'Customer Care Supervisor') && (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_206_CCSHumanaPharmacyAccess)))) {

            this.uPharmacy = true;
        }
        else{this.uPharmacy = false;}
    }
    fetchPanelData(recType) {
        this.loadCommonCss();
        if (!this.oUserGroup) {
            this.oUserGroup = getUserGroup();
        }
        if(this.isMemberAccount()){
            this.sAccountPageName= 'Person Account';
        }else if(this.isGroupAccount()){
            this.sAccountPageName= 'Business Account';
        }
        this.oDetails = getRTLayout(recType, this.oUserGroup);

        this.oCustomDetails = [];
        let oTaxIds = [];
        let sTaxIds = '';
        let sTaxIdHover = '';
        this.loadMemberIcons();
		this.checkPharamcyUser();
        this.bShowVerifyButton = this.isMemberAccount();
        this.showInteractions = this.isMemberAccount();
        this.showRTISection = (this.oUserGroup.bRcc || this.oUserGroup.bGbo || this.uPharmacy || this.oUserGroup.bGeneral) ? true : false;
        if (this.oUserGroup.bProvider || this.oUserGroup.bGbo) {
            this.bShowVerifyButton = false;
        }
        if (this.isMemberAccount()) {
            this.processVerifyDemographicsData(this.labels.HUM_OnLoad);
        }
        let bIsTaxIdRequired = JSON.stringify(this.oDetails).indexOf('sTaxIds') > -1;
        if (this.oDetails && bIsTaxIdRequired) {
            retrieveTaxIds({ AccountId: this.recordId }).then((result) => {
                if (result && result.length > 0) {
                    result.forEach(rowEl => {
                        if (rowEl.ID_Type__c == 'TAXID') oTaxIds.push((rowEl.Consumer_ID__c ? rowEl.Consumer_ID__c : ''));
                    });
                }
                if (oTaxIds && oTaxIds.length > 0 && oTaxIds.length <= 2) {
                    oTaxIds.forEach(element => {
                        sTaxIds += element + '<br/>';
                    });
                } else if (oTaxIds && oTaxIds.length > 2) {
                    sTaxIds += oTaxIds[0] + '<br/>' + oTaxIds[1] + '<br/>';
                    oTaxIds.forEach(element => {
                        sTaxIdHover += element + '<br/>';
                    });
                    this.bTaxIdViewAll = true;
                    this.sTaxIdHover = sTaxIdHover;
                }
                this.sTaxIds = sTaxIds;
            }).catch((error) => {
                console.log('Error Occured', error);
            });
        }
        console.log('150');
    }


    isMemberAccount() {
        return this.sRecordTypeName === detailConstants.rtMem || this.sRecordTypeName === detailConstants.rtUnMem;
    }

    isGroupAccount() {
        return this.sRecordTypeName === detailConstants.rtGrp || this.sRecordTypeName === detailConstants.rtUnGrp;
    }

    handleLoad(event) {
        let oPayload = event.detail;
        let oDetail = oPayload.records[this.recordId];
        this.panelDetails = oDetail;
        this.sWorkPhonePlusExt = "";
        if (this.sRecordTypeName == 'Member' && oDetail.fields.Birthdate__c.value) {
            this.iAge = ageCalculator(oDetail.fields.Birthdate__c.value);
            const phExtention = oDetail.fields.Work_Phone_Ext__c;
            const workPhone = oDetail.fields.PersonOtherPhone;
            if (workPhone && workPhone.value) {
                this.sWorkPhonePlusExt = workPhone.value;
            }
            if (phExtention && phExtention.value) {
                this.sWorkPhonePlusExt += `^${phExtention.value}`;
            }
        }
        if (this.sRecordTypeName == 'Group' || this.sRecordTypeName == 'Unknown Group' || this.sRecordTypeName == 'Provider' ||
            this.sRecordTypeName == 'Unknown Provider' || this.sRecordTypeName == 'Agent/Broker' || this.sRecordTypeName == 'Unknown Agent/Broker') {
            this.sName = (oDetail.fields.Name && oDetail.fields.Name.value) ? oDetail.fields.Name.value : this.sName;
            this.sphone = oDetail.fields.Phone ? oDetail.fields.Phone.value : ''; 
            if (this.sRecordTypeName == 'Group'|| this.sRecordTypeName == 'Unknown Group')
            {
                this.bShowMemberSearch = true;
                this.sGroupNumber= oDetail.fields.Group_Number__c ? oDetail.fields.Group_Number__c.value : '';
            }
        }
        else {
            let me = this;
            me.sName = (oDetail.fields.FirstName && oDetail.fields.FirstName.value) ? oDetail.fields.FirstName.value : this.sName;
            me.sName = (oDetail.fields.MiddleName && oDetail.fields.MiddleName.value) ? this.sName + ' ' + oDetail.fields.MiddleName.value : this.sName;
            me.sName = (oDetail.fields.LastName && oDetail.fields.LastName.value) ? this.sName + ' ' + oDetail.fields.LastName.value : this.sName;
            this.oDetails.recodDetail.fields.forEach(function (item) {
                if(item.label === 'Mailing Address'){
                    let mailStreet = me.panelDetails.fields['PersonMailingStreet'] ? me.panelDetails.fields['PersonMailingStreet'].value : null;
                    let mailCity  = me.panelDetails.fields['PersonMailingCity'] ? me.panelDetails.fields['PersonMailingCity'].value : null;
                    let mailState = me.panelDetails.fields['PersonMailingStateCode'] ? me.panelDetails.fields['PersonMailingStateCode'].displayValue : null;
                    let mailPostal = me.panelDetails.fields['PersonMailingPostalCode'] ? me.panelDetails.fields['PersonMailingPostalCode'].value : null;
                    let countryCode = me.panelDetails.fields['PersonMailingCountryCode'] ? me.panelDetails.fields['PersonMailingCountryCode'].displayValue : null;
                    let mailAddress = mailStreet && mailCity && mailState && mailPostal && countryCode ? mailStreet+' '+mailCity+' '+mailState+' '+mailPostal+' '+countryCode : null;
                    me.mAddress = mailAddress;
                }
                if(item.label === 'Residential Address'){
                    let residentialStreet = me.panelDetails.fields['ShippingStreet'] ? me.panelDetails.fields['ShippingStreet'].value : null;
                    let residentialCity = me.panelDetails.fields['ShippingCity'] ? me.panelDetails.fields['ShippingCity'].value : null;
                    let residentialState = me.panelDetails.fields['ShippingStateCode'] ? me.panelDetails.fields['ShippingStateCode'].displayValue : null;
                    let residentialPostal = me.panelDetails.fields['ShippingPostalCode'] ? me.panelDetails.fields['ShippingPostalCode'].value : null;
                    let residentialCountry = me.panelDetails.fields['ShippingCountryCode'] ? me.panelDetails.fields['ShippingCountryCode'].displayValue : null;
                    let residentialAddress = residentialStreet && residentialCity && residentialState && residentialPostal && residentialCountry ? residentialStreet+' '+residentialCity+' '+residentialState+' '+residentialPostal+' '+residentialCountry : 'United States';
                    me.rAddress = residentialAddress;
                }
                if( item.hasOwnProperty('copyToClipBoard')  && (!item.hasOwnProperty('bAddress')) && (!oDetail.fields[item.mapping].value)){
                    console.log('home phone copy property',item.label);
					item.copyToClipBoard = false;
                  }
                if(item.hasOwnProperty('copyToClipBoard') && item.hasOwnProperty('bAddress') && (!item.value)){ // this if for hanlde only address fields for copy paste feature
                    item.copyToClipBoard = false;
                }
                if (item.label == 'Home Phone') {
                    me.sHomephone = oDetail.fields.PersonHomePhone ? oDetail.fields.PersonHomePhone.value : '';
                }
                if (item.label == 'Work Phone') {
                    me.sOtherphone = oDetail.fields.PersonOtherPhone ? oDetail.fields.PersonOtherPhone.value : '';
                }
            });
        }
    }

    /**
    * Open Password popup
    */
   openModal(key){
    this.bShowModal = getSessionItem(key) ? false: true;
 }

     /**
    * Handle close of passsword popup
    */
   closeModal(){
    this.bShowModal = false;
    setSessionItem(this.recordId, true); // selMemberAccId
 }

    verifyDemographicsData(event) {
        this.processVerifyDemographicsData(this.labels.HUM_OnClick);
    }
	
	updatePlanDemographic(event) {
    
        this.bSpinner=true;
        createCaseAndRedirect({ objID: this.recordId, newInteractionId: this.interactionId})
        .then(result => {
            if(result)
            {
                this.bSpinner=false;
                openLWCSubtab('caseInformationComponentHum',{objApiName:'Case',Id:result.newCaseId ,isFromUpdatePlanDemographic: true},{label:result.newCaseNumber,icon:'standard:case'});
                openLWCSubtab('demographicNonCommercialComponentLightning',result.newCaseId,{label:uConstants.Update_Plan_Demographics,icon:'standard:case',tabSwitch:result.SubtabCloseSwitch}); 
            }
    
        }).catch(error => {
            this.bSpinner=false;
            console.log("Error Occured", error);
        });}
		
	updateCommercialDemographic(event) {
        this.bSpinner=true;
        createCaseAndRedirect({ objID: this.recordId, newInteractionId: this.interactionId})
        .then(result => {
            if(result)
                {
                    this.bSpinner=false;
                    openLWCSubtab('caseInformationComponentHum',{objApiName:'Case',Id:result.newCaseId ,isFromUpdatePlanDemographic: true},{label:result.newCaseNumber,icon:'standard:case'});
                    openLWCSubtab('demographicCommercialComponentLightning',result.newCaseId,{label:uConstants.Update_Commercial_Demographics,icon:'standard:case',tabSwitch:result.SubtabCloseSwitch}); 
                }
        
            }).catch(error => {
                this.bSpinner=false;
                console.log("Error Occured", error);
    });}
    
    @wire(MessageContext)
    messageContext;

    processVerifyDemographicsData(sActionType) {

        let daysSinceVerified;
        let sDemographicResult;
        startRequestDemographics({ recId: this.recordId, action: sActionType })
        .then(result => {
            if (result) {
                daysSinceVerified = result[0].iDaysSinceLastVerified;
                if (daysSinceVerified) {
                    this.bShowDemographics = (daysSinceVerified > 90);
                }
                else {
                    this.bShowDemographics = false;
                }
                this.demGrpRes = result;
                this.publishMessage(result);
            }
        }).catch(error => {
            console.log("Error Occured", error);
        });
    }

    onInterPillClick(evnt) {
        const offsetHeight = this.template.querySelector('.highlights-panel').offsetHeight;
        this.fireEvent('scrolltoview', {
            dataId: evnt.detail.scrollTo,
            offsetHeight,
            bApplyFilters: true
        });
    }

    fireEvent(eventName, detail) {
        const oEvent = new CustomEvent(eventName, {
            detail
        });
        this.dispatchEvent(oEvent);
    }

    /**
    * Fire scrolltoview event on click of jump links
    * @param {*} evnt 
    */
    handleJumpLinkClick(evnt) {
        const offsetHeight = this.template.querySelector('.highlights-panel').offsetHeight; // Height of the Highlight panel
        this.fireEvent('scrolltoview', {
            dataId: evnt.currentTarget.getAttribute("data-id"),
            offsetHeight
        });
    }

    publishMessage(oData) {
        pubSubHum.fireEvent(this.pageRef, 'on-verify-demographics', oData);
        const daysSinceVerified = this.demGrpRes?.[0]?.iDaysSinceLastVerified;
        this.bShowDemographics = daysSinceVerified ? daysSinceVerified > 90 : false;
        var payload = {message : this.bShowDemographics, interId :this.interactionId};
        publish(this.messageContext, verDemGrapChannel, payload);
    }

	handleLogging(event) {
		if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            performLogging(event,this.createRelatedField(),'Account Highlights',this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    performLogging(event,this.createRelatedField(),'Account Highlights',this.loggingkey,this.pageRef);
                }
            })
        }
	}
	
	createRelatedField(){
        return [{
            label : 'Member Name',
            value : this.sName
        }];
    }	
	
	 copyToBoard(event){
        if(!event.currentTarget.dataset.address){
           copyToClipBoard(this.panelDetails.fields[ event.currentTarget.dataset.field].value);
        }else{
            copyToClipBoard(event.currentTarget.dataset.field);
        }
    }

    getInteractionId() {
        if(this.pageState && typeof(this.pageState) === 'object') {
            if(this.pageState.hasOwnProperty('c__interactionId')) {
              this.interactionId = `${this.pageState['c__interactionId']}`;
              this.publishCallbackNumber(this.interactionId);
            }
        }
    }

    publishCallbackNumber(interId)
   {
	   let message = {
         InteractionId: interId
      };
      
      publish(this.messageContext, CallbackNumberMessageChannel, message);
   }
}