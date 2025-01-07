/*******************************************************************************************************************************
LWC JS Name          : memIdCardsHistoryContainer.js
Version              : 1.0
Created On           : 02/09/2022
Function             : This JS serves as controller to memIdCardsHistoryContainer.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Vamshi Krishna Pemberthi                              02/09/2022                   Initial Version
* Apurva Urkude										    02/03/2023                   UserStory 4100199: Lightning- MCD- Not able to order ID Card for OH Medicaid Pending Plan Group 325240
* Raj Paliwal                                           04/04/2023                   User Story 4369943: Lightning - Contact Servicing-ID Cards- Add PCP Link on Member ID Card Page.
* Raj Paliwal                                           04/06/2023                   Defect Fix: 7493.
* Anuradha Gajbhe                                       07/28/2023                   US: 4826980: INC2411079- MF 4743214 - Go Live Incident resolve Quicklink data bleeding RAID 87.
*********************************************************************************************************************************/

import { api, LightningElement, track,  wire  } from 'lwc';
import { loadStyle } from 'lightning/platformResourceLoader';
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { CurrentPageReference } from 'lightning/navigation';
import { getUserGroup } from "c/crmUtilityHum";
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import memberIdCardsInfoMessageHistory from '@salesforce/label/c.INFOMESSAGE_MEMBERIDCARDS_HUM';

const tabMedical = 'Medical';
const tabDental = 'Dental'; 

export default class MemIdCardsHistoryContainer extends LightningElement {
    label ={
        memberIdCardsInfoMessageHistory,
    };
    @api medicalProduct='MED';
    @api dentalProduct='DEN';
    @api recordId;
    @api productType;
    @track memId;
    @api tabId;
    
    @track oTabs = {
        medicalIds: tabMedical,
        dentalIds: tabDental
    };
    @wire(CurrentPageReference)
    currentPageReference(pageRef){
        this.pageRef = pageRef;
    }
    constructor(){
        super();
        // loading css
        loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css'); 
        this.sMemIdCardsTemplate = 'true';
    }

    handleActive(event){
        this.productType =  event.target.value;
    }

    connectedCallback() {
        const me = this;
        this.oUserGroup = getUserGroup();
        this.isInitDone = true;
        this.recordId = this.pageRef.state.C__Id;
        this.getClickTabId();
    }

    getClickTabId(){
        return new Promise((resolve,reject)=>{
            try{
                invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                    if (isConsole) {
                        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => { 
                        if (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab){
                            this.tabId = focusedTab.parentTabId;
                            resolve(focusedTab.parentTabId);
                        }else {
                            this.tabId = focusedTab.tabId;
                            resolve(focusedTab.tabId);
                        }
                        });
                    }
                });   
            }catch(error){
                reject(error);
            }
        })
    }

}