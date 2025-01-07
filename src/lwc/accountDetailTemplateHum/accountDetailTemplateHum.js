/*******************************************************************************************************************************
LWC JS Name : accountDetailTemplateHum.js
Function    : This JS serves as controller to accountDetailTemplateHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pradeep Dani                                          12/18/2020                    initial version
* Joel George                                           02/26/2021                    REQ 1892718
* Mohan Kumar N			                                03/11/2021	                 US:1864079 : Account sections overlap issue
* Mohan Kumar N                                         03/12/2021                    US: US:1942036: Password popup
* Joel George			                                    03/16/2021		              REQ 1895067 
* Joel George                                           03/31/2021                    Made chat transcript display as false for provider
* Mohan Kumar N			               				    03/08/2021		           	  US-1749564
* Ritik Agarwal                                         03/05/2021                    Add a legacy Contact history tab
* Mohan                                                 04/28/2021                   Jump link scroll issue fix
* Mohan                                                  06/02/2021                    US-2176313 Apply fileter on open cases click
* Ranadheer Alwal                                       7/25/2021                  US: 2322406 Added DynamicCustomLinksLcHum Lightning web component  
* Ritik Agarwal                                         08/07/2021                 Added condition to hide Alerts icons from account highlights panel if  account is legacy delete
* Ankima Srivastava                                     08/17/2021                US : 2233885 - Label Change
* Ajay Chakradhar                                       10/07/2021                US : 2260341 - Account Management - Additional Member ID Fields 
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { getRecord } from 'lightning/uiRecordApi';
import {  hcConstants, getUserGroup, getSessionItem, setSessionItem, getLabels, compareDate,getLocaleDate } from "c/crmUtilityHum";
import isSandboxOrg from '@salesforce/apex/SearchUtilty_H_HUM.isSandboxOrgInfo';
import { loadStyle } from 'lightning/platformResourceLoader';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';

const arrFields = ['Account.Account_Security_Answer__c','Account.Account_Security_Question__c','Account.Account_Security_EndDate__c','Account.Account_Security_Access__c','Account.Name','Account.ETL_Record_Deleted__c'];
const rtMem = 'Member';
const rtUnMem = 'Unknown Member';
const rtGrp = 'Group';
const rtUnGrp = 'Unknown Group';
const rtPr = 'Provider';
const rtUnPr = 'Unknown Provider';
const rtAg = 'Agent/Broker';
const rtunAg = 'Unknown Agent/Broker';
const tabPolicyCaseHistryInts = 'Plan & Interaction History';
const tabInts = 'Interaction History';
const tabCaseHistoryInts = 'Case History & Interactions';
const tabDetails = 'Details';
const tabVerifyDemographics = 'Verify Demographics';
const tabDivisionsSubgroups = 'Divisions/Subgroups';
const tabLegacyHistoryTable = hcConstants.LEGACY_TABLE;
const openCasesStatus = {
   sStatus:["In Progress", "Pending - Response", "Pending - Transfer", "External Transfer in Progress"]
};

export default class AccountDetailTemplateHum extends LightningElement {
   @api recordId;
   @api objectApiName;
   @track vPOAData = '';
   @track bShowAssociatedForm = false;
   @track bShowCaseRelatedList = false;
   @track bShowContactHandlingAlerts = false;
   @track bShowDemographics = false;
   @track bShowDetails = false;
   @track bShowHighlights = false;
   @track bShowInteractionsRelatedList = false;
   @track bShowTranscriptRelatedList = false;
   @track bShowPOA = false;
   @track bShowPolicyRelatedList = false;
   @track bShowQuickFind = false;
   @track bShowConsumerList = false;
   @track recordType = '';
   @track divisionresults;
   @track oUserGroup;
   @track sDemographicResult;
   @track bShowAccountMemberIdsRelatedList = false;
   @track sMainRegionClass = 'slds-size_9-of-12 slds-medium-size_9-of-12';
   @track bShowSidebar = true;
   @track bShowGroupSubDivision = false;
   @track oDivisionSubgroupResult = '';
   @track bDivisionSubgroupFlag = false;
   @track bLegacyHistoryTable = false;
   @track bIntialWireCall = true;
   displayHeader = true;
   @track oTabs = {
      policyCaseHistoryInts: tabPolicyCaseHistryInts,
      details: tabDetails,
      verifyDemographics: tabVerifyDemographics,
      divisionsSubgroups: tabDivisionsSubgroups,
      legacyHistoryTab: tabLegacyHistoryTable
   };
   @track stickyHeaderCss = "slds-m-bottom_x-small ";
   @track buttonsConfig = [{
      text: getLabels().HUMAlertsAcknowledge,
      isTypeBrand: true,
      eventName: hcConstants.CLOSE
   }];
   @track questionDetails= {
      question: '',
      answer:'',
      pValue:''
    };
   @track bShowModal = false;
   @track labels = getLabels();
   @track accountName;
   hasSystemToolBar = false;
   isInitDone;
   dataWire;
   @track isLegacyDelete= false;

   constructor() {
      super();
      // loading css
      loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css'); 
   }

   @wire(getRecord, {
      recordId: '$recordId',
      fields: arrFields
   })
   wiredAccount({
      error,
      data
   }) {
      if (data) {
         this.recordType = data.recordTypeInfo.name;
         this.dataWire=data;
         this.accountName = data.fields.Name.value;
         this.isLegacyDelete = data.fields.ETL_Record_Deleted__c.value;
      } else if (error) {
         console.log('Error Occured', error);
      }

      if (this.isInitDone) {
         let oUG = this.oUserGroup;
         switch (this.recordType) {
            case rtMem:
            case rtUnMem:
               this.bLegacyHistoryTable = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
               this.bShowAssociatedForm = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bGbo);
               this.bShowContactHandlingAlerts = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bGbo);
               this.bShowDemographics = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral);
               this.bShowDetails = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
               this.bShowHighlights = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
               this.bShowInteractionsRelatedList = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
			      this.bShowTranscriptRelatedList = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);		   
               this.bShowAccordianPolicyRelatedList = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
               this.bShowPOA = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bGbo);
               this.bShowAccountMemberIdsRelatedList = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
               this.bShowCaseRelatedList = true;
               break;
            case rtGrp:
            case rtUnGrp:
               this.bLegacyHistoryTable = (oUG.bPharmacy || oUG.bGeneral || oUG.bGbo);
               this.bShowDetails = true;
               this.bShowHighlights = true;
               this.bShowInteractionsRelatedList = true;
			      this.bShowTranscriptRelatedList = true;
               this.bShowPolicyRelatedList = true;
               this.bShowCaseRelatedList = true;
               this.bShowGroupSubDivision = true;
               break;
            case rtPr:
            case rtUnPr:
               this.bLegacyHistoryTable = (oUG.bRcc || oUG.bProvider || oUG.bPharmacy || oUG.bGeneral || oUG.bGbo);
               this.oTabs.policyCaseHistoryInts = tabInts;
               this.bShowConsumerList = true;
               this.bShowDetails = true;
               this.bShowHighlights = true;
               this.bShowInteractionsRelatedList = true;
			      this.bShowTranscriptRelatedList = false;
               this.bShowCaseRelatedList = true;
               break;
            case rtAg:
            case rtunAg:
               this.bLegacyHistoryTable = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bGbo);
               this.oTabs.policyCaseHistoryInts = tabCaseHistoryInts;
               this.bShowDetails = true;
               this.bShowHighlights = true;
               this.bShowInteractionsRelatedList = true;
			      this.bShowTranscriptRelatedList = true;
               this.bShowConsumerList = true;
               this.bShowCaseRelatedList = true;
               break;
         }
         if (!(this.bShowPOA || this.bShowContactHandlingAlerts || this.bShowAssociatedForm)) {
            this.sMainRegionClass = 'slds-size_12-of-12 slds-medium-size_12-of-12';
            this.bShowSidebar = false;
         }

            if(this.bShowAssociatedForm && this.bIntialWireCall){
               this.popupOperation(this.dataWire);
            }
            this.bIntialWireCall= false;
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
        this.selMemberAccId = this.recordId;
        this.openModal(this.recordId);
        }
   }

   connectedCallback() {
      const me = this;
      this.oUserGroup = getUserGroup();
      this.isInitDone = true;
      // Sandbox orgs has extra toolbar, so this check is needed to add appropriate css
      isSandboxOrg()
         .then(hasSystemToolBar => {
               me.hasSystemToolBar = hasSystemToolBar;
               me.stickyHeaderCss += hasSystemToolBar ? 'sticky-header-system-bar': 'sticky-header';
         })
         .catch(err => {
            console.error('Error', err);
         });
   }

   handleCustomEvent(event) {
      this.vPOAData = event.detail;
      this.template.querySelector('c-account-detail-p-o-a-hum').processResponseFromAssociatedUser(event.detail);
   }

   handleDemographicClickCustomEvent(event) {
	  this.sDemographicResult = event.detail;
      if(this.template.querySelector('c-account-detail-demographics-hum')){
        this.template.querySelector('c-account-detail-demographics-hum').processResponseFromHighlightsPanel(JSON.parse(this.sDemographicResult));
      }
   }

   handleCustomEventGroupSubdivison(event) {
      this.oDivisionSubgroupResult = event.detail;
      this.bDivisionSubgroupFlag = true;
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
      setSessionItem(this.selMemberAccId, true);
   }

   /**
    * update scrollMarginTop dynamically based on highlightspanel height
    * @param {*} offsetHeight 
    */
   updateScrollMargin(offsetHeight) {
      const jumpLinkViews = this.template.querySelectorAll('.jump-views');
      const sfHeaderHeight = this.hasSystemToolBar ? 125: 98;
      const marginTop  = sfHeaderHeight + offsetHeight;
      jumpLinkViews.forEach(item => {
         item.style.scrollMarginTop = `${marginTop}px`;
      });
   }

   /**
    * Handle scroll to view on click of jump links
    * @param {*} evnt 
    */
   onScrollToView(evnt) {
      const { dataId, offsetHeight, bApplyFilters} = evnt.detail;
      const me = this;
      let sSelector = "";
      if(!me.hasMarginUpdated) {
         me.updateScrollMargin(offsetHeight);
         me.hasMarginUpdated = true;
      }
      switch(dataId){
         case 'policies': 
            sSelector = me.bShowAccordianPolicyRelatedList ? 'c-account-detail-accordian-policy-hum': 'c-account-detail-policy-hum';
         break;
         case 'casehistory':
            sSelector = 'c-account-detail-case-history-hum';
            this.applyFilters(bApplyFilters, openCasesStatus , sSelector);
         break;
         case 'interactions':
            sSelector = 'c-account-detail-interactions-hum';
         break;
         case 'transcripts':
            sSelector = 'c-account-detail-transcripts-hum';
         break;
         default:
      }
      me.scrollTo(sSelector);
   }

   /**
    * Scroll to the element provided
    * @param {*} selector 
    */
   scrollTo(selector){
      const cmpElement = this.template.querySelector(selector);
      cmpElement && cmpElement.scrollIntoView();
   }

   applyFilters(bApplyFilters, oFilters, sSelector) {
      if(bApplyFilters){
         this.template.querySelector(sSelector).applyFilters(oFilters);
      }
   }
}