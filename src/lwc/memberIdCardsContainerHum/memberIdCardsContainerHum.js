/*******************************************************************************************************************************
LWC JS Name          : memIdCardsContainerHum.js
Version              : 1.0
Created On           : 02/09/2022
Function             : This JS serves as controller to memberIdCardsContainerHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
*********************************************************************************************************************************/

import { api, LightningElement, track,  wire  } from 'lwc';
import { getRecord } from 'lightning/uiRecordApi';
import { loadStyle } from 'lightning/platformResourceLoader';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { CurrentPageReference } from 'lightning/navigation';
import {  getUserGroup, getLabels } from "c/crmUtilityHum";
const arrFields = ['Account.Account_Security_Answer__c','Account.Account_Security_Question__c','Account.Account_Security_EndDate__c','Account.Account_Security_Access__c','Account.Name','Account.ETL_Record_Deleted__c'];
const rtMem = 'Member';
const rtUnMem = 'Unknown Member';
const tabMedical = 'Medical';
const tabDental = 'Dental';

export default class MemberIdCardsContainerHum extends LightningElement {
    @api recordId;
    @track sMemIdCardsTemplate = '';
    @track bShowDetails = false;
    @track bShowHighlights = false;
    @track recordType = '';
    @track oUserGroup;
    @track sMainRegionClass = 'slds-size_9-of-12 slds-medium-size_9-of-12';
    @track stickyHeaderCss = "slds-m-bottom_x-small ";
    @track labels = getLabels();
    @track accountName;
    isInitDone;
    dataWire;
    @track oTabs = {
        medicalIds: tabMedical,
        dentalIds: tabDental
    };

    @wire(CurrentPageReference)
        currentPageReference(pageRef){
            this.pageRef = pageRef;
        };
    
    constructor() {
        super();
            // loading css
        loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css'); 
        this.sMemIdCardsTemplate = 'true';
    }
    connectedCallback()
    {
        const me = this;
        this.oUserGroup = getUserGroup();
        this.isInitDone = true;
        this.recordId = this.pageRef.state.C__Id;
    }


    @api bLoadComponent = false;

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
        if(this.recordType == rtMem || this.recordType == rtUnMem)
        {
            this.bLoadComponent = true;
        }
  
        if (this.isInitDone) {
           let oUG = this.oUserGroup;
           switch (this.recordType) {
              case rtMem:
              case rtUnMem:
                  this.bShowDetails = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
                  this.bShowHighlights = (oUG.bRcc || oUG.bPharmacy || oUG.bGeneral || oUG.bProvider || oUG.bGbo);
                  break;
              
           }
        }
     }


}