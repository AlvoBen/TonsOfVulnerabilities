/*******************************************************************************************************************************
LWC JS Name : memIdCardHighlightsPanelHum.js
Function    : This JS serves as controller to memberIdCardsContainerHUM.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                  US #2888581 User Story 2888581: Lightning-ID Cards-Realignment of ‘Product’ & ‘Policy’ fields on member ID Card page
* Vishal Shinde                                         19/09/2022                  3236839 - Additional Capabilities on Claims Line Items: Hover Over Claim LIST/Summary     
* G Sagar                                               11/07/2022                  3771949 - CRM Service Billing Systems Integration: Lightning - ID Cards- Logging on Lightning & Classic (Surge) 
* Abhishek Mangutkar                                    03/01/2023                  US 4286520 Remove logic for assign member plan id for logging cases                                           
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { getRTLayout, detailConstants } from './layoutConfig';
import retrieveTaxIds from '@salesforce/apex/AccountConsumerIdComponent_LC_HUM.retrieveConsumerIds';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import memberIdCardsIcons from '@salesforce/resourceUrl/memberIdCardsServiceCall_SR_HUM';
import { getLabels, getUserGroup,ageCalculator } from 'c/crmUtilityHum';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import hasCRMS_111_StridesAccess from '@salesforce/customPermission/CRMS_111_StridesAccess';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import pubSubHum from 'c/pubSubHum';
import {
    publish,
    MessageContext,
    subscribe,
    unsubscribe
} from 'lightning/messageService';
import { CurrentPageReference } from 'lightning/navigation';
import loggingLMSChannel from '@salesforce/messageChannel/loggingLMSChannel__c';
import {
    performLogging,
    setEventListener,
    checkloggingstatus,
    clearLoggedValues,
    getLoggingKey
} from 'c/loggingUtilityHum';

export default class MemIdCardHighlightsPanelHum extends LightningElement {
    @track oCustomDetails;
    @track oDetails;
    @api sRecordId;
    @api sRecordTypeName;
    @api oUserGroup;
    @api sAction;
    @api accLegacyDelete;
    @api sAccountPageName;
    @api sMemIdCardsTemplate;
    @api bMemberIdcards =false;
    @track bShowDemographics;
    @track bShowVerifyButton;
    @track showInteractions;
    @track showRccAlert;
    @track showGboAlert;
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
	@track profileName;
    @track netWorkId;
    @track workQueue;
    @track uPharmacy;
	@track sGroupNumber;
	@track bShowMemberSearch;
    @track showLoggingIcon = true;
    @track startLogging = false;
    collectedLoggedData = [];
    autoLogging = true;
    sPlanMemId;
	loggingRecordId;

    @wire(MessageContext)
    messageContext;

 @wire(CurrentPageReference)
      wiredPageRef(currentPageReference) {
          this.pageRef = currentPageReference;          
          this.loggingkey = getLoggingKey(this.pageRef);
          let memberPlanId = this.pageRef?.state?.C__MemberPlanId ?? null;
		  this.loggingRecordId = memberPlanId ? memberPlanId : this.sRecordId;
      }
  
      @wire(getRecord, {
          recordId: USER_ID,
          fields: [PROFILE_NAME_FIELD, NETWORK_ID_FIELD, CURRENT_QUEUE]
      })
      wireuser({ error, data }) {
          if (error) {
              this.error = error;
          } else if (data) {
              this.profilename = data.fields.Profile.value.fields.Name.value;
              
              this.netWorkId = data.fields.Network_User_Id__c.value;
              
              this.workQueue = data.fields.Current_Queue__c.value;
             
          }
      }//001Z000001XtHWAIA3
      @wire(getRecord, {
          recordId: '$recordId',
          fields: ['Account.Name', 'Account.Enterprise_ID__c']
      })
      wiredAccount({ error, data }) {
          
          if (data) {
              this.personid = data.fields.Enterprise_ID__c.value;
      
          } else if (error) {
              console.log('error in wire--', error)
          }
      }
      
      handleLogging(event) {
        console.log('in claimsummaryhighlightspanelhum handleLogging');
        if (this.startLogging) {
            performLogging(
                event,
                this.createRelatedField(),
                'IdCardsloggingHum',
                this.loggingkey ? this.loggingkey : getLoggingKey(),
                this.pageRef
            );
        }
    }

    

    loggingEventHandler(data) {
        if (data.MessageName === 'StartLogging') {
            this.startLogging = true;
        }
        if (data.MessageName === 'StopLogging') {
            this.startLogging = false;
            clearLoggedValues(this.loggingkey);
     
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
  
    loadCommonCss() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }
	//This method checks whether user is pharmacy user or not
    checkPharamcyUser(){
        if( hasCRMS_111_StridesAccess && (this.profilename === 'Humana Pharmacy Specialist' || ((this.profilename === 'Customer Care Specialist' || this.profilename === 'Customer Care Supervisor') && ((hasCRMS_205_CCSPDPPharmacyPilot && (this.workQueue === 'PDP Pilot Humana Pharmacy Calls' || this.workQueue === 'PDP Pilot Plan Calls'))|| (hasCRMS_206_CCSHumanaPharmacyAccess && this.workQueue === 'Louisville RSO Calls Support') )))){

            this.uPharmacy = true;
        }
        else{this.uPharmacy = false;}
       }
    connectedCallback() {
        this.loadCommonCss();
        if(!this.oUserGroup){
            this.oUserGroup = getUserGroup();
        }
		if(this.isMemberAccount()){
            this.sAccountPageName= 'Member Account';
        }else if(this.isGroupAccount()){
            this.sAccountPageName= 'Business Account';
        }
            this.oDetails = getRTLayout(this.sRecordTypeName, this.oUserGroup);  
        this.oCustomDetails = [];
        let oTaxIds = [];
        let sTaxIds = '';
        let sTaxIdHover = '';
            this.checkPharamcyUser();
            this.bShowVerifyButton = this.isMemberAccount();
			this.showInteractions = this.isMemberAccount();
            this.showRccAlert = (this.isGroupAccount() || this.isMemberAccount()) && (this.oUserGroup.bRcc) && (!this.accLegacyDelete) ? true : false;
			this.showGboAlert = (this.isGroupAccount() || this.isMemberAccount()) && (this.oUserGroup.bGbo || this.uPharmacy || this.oUserGroup.bGeneral) && (!this.accLegacyDelete) ? true : false;
		if(this.oUserGroup.bProvider || this.oUserGroup.bGbo)
        {
            this.bShowVerifyButton = false;
        }
            let bIsTaxIdRequired = JSON.stringify(this.oDetails).indexOf('sTaxIds') > -1;
            if (this.oDetails && bIsTaxIdRequired) {
                retrieveTaxIds({ AccountId: this.sRecordId }).then((result) => {
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
            if(this.sMemIdCardsTemplate == 'true')
            {
                this.bMemberIdcards = true;
            }
this.sPlanMemId=this.sRecordId;
            
            this.loggingkey = getLoggingKey();
         console.log('this.loggingkey===>'+JSON.stringify(this.loggingkey));
        this.startLogging = checkloggingstatus(
            this.loggingkey ? this.loggingkey : getLoggingKey()
        );
        pubSubHum.registerListener(
            'loggingevent',
            this.loggingEventHandler.bind(this),
            this
        );
}

        renderedCallback() {
            Promise.all([
                loadStyle(this, customcss + '/CRM_Assets/styles/logging.css')
            ]).catch((error) => {});
        
        }

    isMemberAccount() {
        return this.sRecordTypeName === detailConstants.rtMem || this.sRecordTypeName === detailConstants.rtUnMem;
    }

    isGroupAccount () {
        return this.sRecordTypeName === detailConstants.rtGrp || this.sRecordTypeName === detailConstants.rtUnGrp;
    }
    
    handleLoad(event) {
        let oPayload = event.detail;
        let oDetail = oPayload.records[this.sRecordId];
        this.sWorkPhonePlusExt = "";
        if (this.sRecordTypeName == 'Member' && oDetail.fields.Birthdate__c.value) {
            this.iAge = ageCalculator(oDetail.fields.Birthdate__c.value);
            const phExtention = oDetail.fields.Work_Phone_Ext__c;
            const workPhone = oDetail.fields.PersonOtherPhone;
            if(workPhone && workPhone.value){
                this.sWorkPhonePlusExt = workPhone.value;
            }
            if(phExtention && phExtention.value){
                this.sWorkPhonePlusExt += `^${phExtention.value}`;
            }
        }
        if (this.sRecordTypeName == 'Group' || this.sRecordTypeName == 'Unknown Group' || this.sRecordTypeName == 'Provider' ||
            this.sRecordTypeName == 'Unknown Provider' || this.sRecordTypeName == 'Agent/Broker' || this.sRecordTypeName == 'Unknown Agent/Broker') {
                this.sName = (oDetail.fields.Name && oDetail.fields.Name.value)  ? oDetail.fields.Name.value : this.sName;
            this.sphone = oDetail.fields.Phone ? oDetail.fields.Phone.value : '';
			if (this.sRecordTypeName == 'Group'|| this.sRecordTypeName == 'Unknown Group')
            {
                this.bShowMemberSearch = true;
                this.sGroupNumber= oDetail.fields.Group_Number__c ? oDetail.fields.Group_Number__c.value : '';
            }   
        }
        else {
            let me = this;
            me.sName = (oDetail.fields.FirstName &&  oDetail.fields.FirstName.value)? oDetail.fields.FirstName.value : this.sName;
            me.sName = (oDetail.fields.MiddleName && oDetail.fields.MiddleName.value) ? this.sName +' '+ oDetail.fields.MiddleName.value : this.sName;
            me.sName = (oDetail.fields.LastName && oDetail.fields.LastName.value) ? this.sName +' '+ oDetail.fields.LastName.value : this.sName;
            this.oDetails.recodDetail.fields.forEach(function (item) {
                if (item.label == 'Home Phone') {
                    me.sHomephone = oDetail.fields.PersonHomePhone ? oDetail.fields.PersonHomePhone.value : '';
                }
                if (item.label == 'Work Phone') {
                    me.sOtherphone = oDetail.fields.PersonOtherPhone ? oDetail.fields.PersonOtherPhone.value : '';
                }
            });
        }
    }
 handleLogging(event) {
        console.log('call come in handlelogging');
		if(this.loggingkey && checkloggingstatus(this.loggingkey)){
            console.log('call come in handlelogging if condition');
            performLogging(event,this.createRelatedField(),'Id Card Highlights',this.loggingkey,this.pageRef);
        }else{
            getLoggingKey(this.pageRef).then(result =>{
                this.loggingkey = result;
                if(this.loggingkey && checkloggingstatus(this.loggingkey)){
                    console.log('call come in handlelogging elseif condition');
                    performLogging(event,this.createRelatedField(),'ID Card Highlights',this.loggingkey,this.pageRef);
                }
            })
        }
	}

    createRelatedField(){
        console.log('call come in creatrelated field');
        return [{
            label : 'MemberPlan Name',
            value :  this.sPlanMemId

        }];
    }
}