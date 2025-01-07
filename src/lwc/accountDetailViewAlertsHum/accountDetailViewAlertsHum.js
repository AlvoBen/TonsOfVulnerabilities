/*******************************************************************************************************************************
LWC JS Name : accountDetailViewAlertsHum.js
Function    : This JS serves as controller to accountDetailViewAlertsHum.html.

Modification Log:
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                           04/26/2021                 initial version US-1464401
* Supriya Shastri                                         04/26/2021                 added conditions to render icon for US-1464401
* Aishwarya Pawar                                         10/09/2021                Added Alert UI
* Aishwarya Pawar                                         05/12/2022                REQ - 3285223 Alert Configuration on Plan Member Page
* Aishwarya Pawar                                         05/12/2022                REQ - 3127063  Lightning - CRM90 Day Recertification Notification in Medicaid Details Section  for OH MCD
* Abhishek Mangutkar                            		  06/30/2022                REQ - 3246031
* Ceasar Sabarre                                            01/24/2023              REQ-4153700  Alerts - Interaction id logic fix for NBA
* Manohar Billa                                           02/20/2023                 US:3272618 - Guidance Alerts for QS
* Jonathan Dickinson                                      03/31/2023                User Story 4365030: T1PRJ0865978-MF 23786 -SF - TECH DF - 7268 - By Delivering or Terming an Alert, the number of alerts displayed in the bubble isn't auto updating causing a mism
*********************************************************************************************************************************/

import { LightningElement, api, track, wire } from 'lwc';
import init from '@salesforce/apex/AccountDetailViewAlerts_LC_HUM.init';
import { CurrentPageReference } from 'lightning/navigation';
import updateRecommnedationBasedOnAction from '@salesforce/apex/AccountDetailViewAlerts_LC_HUM.updateRecommnedationBasedOnAction';
import { publish, createMessageContext } from 'lightning/messageService';
import guidanceAlertMessageChannel from '@salesforce/messageChannel/guidanceAlerts__c';
import alertRefreshMessageChannel from '@salesforce/messageChannel/alertRefresh__c';

export default class AccountDetailViewAlertsHum extends LightningElement {
   @api recordId;
   @api memberPlanRecordId
   @api sRecordTypeName;
   @api urlString;
   @api isRcc;
   @api accountPageName;
   @api otherPageName;
   @track alertsDetails;
   @track alertCount;
   @track alertCountdisplay;
   @track alertsIconClass;
   @track alertContainerClass = 'alerts-container';
   @track flowParams;
   @track RccGboSection;
   @track displayAlertPopover = false;
   @track isCommandAlert;
   @track displayCommandAlertPopup = false;
   @track listRecommendationActionIds;
   @track sPageName;
  @track interactionId='';
  @track pageRef;
  @track pageState;
  @track stateValue='';

   //Guidance Alerts
   @track guidanceAlertCount = 0;
   @track guidanceAlertIds = [];
   @track hasGuidanceAlerts = false;
   @track interId;
   @track bRefreshAlerts = false;


  @wire(CurrentPageReference)
   wiredPageRef(pageRef) {
       this.pageRef = pageRef;
       this.pageState = this.pageRef?.state ??  null;
       this.getInteractionId();
       this.getAlerts();
   }

   getInteractionId(){
     if(this.pageState && typeof(this.pageState) === 'object'){
        if(this.pageState.hasOwnProperty('ws')){
           this.stateValue = this.pageState['ws'];
        }
        else if(this.pageState.hasOwnProperty('c__interactionId')){
            this.stateValue = `c__interactionId=${this.pageState['c__interactionId']}`;
            this.interId = `${this.pageState['c__interactionId']}`;
        }
     }
 }

 getAlerts(){
   const inputParams = { sAccountPageName: this.accountPageName, sOtherPageName: this.otherPageName, sAccountId: this.recordId, polId: this.memberPlanRecordId, URLString: this.stateValue };
   this.alertsIconClass = 'noalerticon';
   this.getAlertDetails(inputParams);
   this.alertContainerClass = this.isRcc ? 'rcc-alert alerts-container' : 'alerts-container';
   this.RccGboSection = this.isRcc ? 'rccSection slds-popover slds-popover_error slds-nubbin_top-left' : 'gboSection slds-popover slds-popover_error slds-nubbin_top-right';
 }

   get flowParamsJSON() {
      return JSON.stringify(this.flowParams);
   }
   get commandAlertFlowParamsJSON() {
      return JSON.stringify(this.flowParamsCommandAlert);
   }

   /**apex call to load and
    * iterate through alerts data
    */
   async getAlertDetails(inputParams) {
      try {
         let result = await init(inputParams);
         this.alertsDetails = JSON.parse(result);
         //added hasGuidanceAlerts assignment
         this.hasGuidanceAlerts = (this.alertsDetails.hasOwnProperty('isGuidanceAlert')) ? this.alertsDetails.isGuidanceAlert : false;
         this.loadRecommendationDetails();
         this.isCommandAlert = this.alertsDetails.isCommandAlert;
         if(this.isCommandAlert){
            let listcommandRecActionIds = this.alertsDetails.listcommandRecActionIds;
            this.flowParamsCommandAlert = [
               {
                  name: 'lstRecommendationActionIds',
                  type: 'String',
                  value: listcommandRecActionIds
               },
               {
                  name: 'sPageName',
                  type: 'String',
                  value: this.sPageName
               },
               {
                  name: 'InteractionId',
                  type: 'Boolean',
                  value: this.interactionId
               }
            ];
            this.displayCommandAlertPopup=true;
         }

      } catch (error) {
         console.log('Error in getAlertDetails() method', JSON.stringify(error));
      }

   }

   handleRefresh(){
      // bRefreshAlerts used here to make sure the component that sent message doesn't refresh
      if(!this.bRefreshAlerts) {
          this.getAlerts();
      } else {
          this.bRefreshAlerts = false;
      }
   }

   handleShowAlert() {
      this.displayAlertPopover = true;
   }

   hideAlert() {
      this.displayAlertPopover = false;
      this.updateRecommendationBasedOnAction();
   }
   /*This method handles the finish behaviour of NBA flow in lwc */
   finishcheck(event) {
      this.displayAlertPopover = event.detail.status;
      this.updateRecommendationBasedOnAction();
   }
   CloseCommandAlert(event){
      this.displayCommandAlertPopup = false;
   }
   loadRecommendationDetails(){
         this.alertCount = (this.alertsDetails.hasOwnProperty('sAlertCount')) ? this.alertsDetails.sAlertCount : 0;
         this.alertCountdisplay = (this.alertCount > 0) ? true : false;
         this.interactionId = (this.alertsDetails.hasOwnProperty('bInteractionId')) ? this.alertsDetails.bInteractionId : 'false';
         this.sPageName = (this.alertsDetails.hasOwnProperty('sPageName')) ? this.alertsDetails.sPageName : 'Person Account';
         this.listRecommendationActionIds = this.alertsDetails.listRecommendationActionIds;
         this.alertsIconClass = (this.alertCount > 0) ? 'alerticon' : 'noalerticon';
         //added this function call
         this.setGuidanceAlertCount();
         this.flowParams = [
            {
               name: 'lstRecommendationActionIds',
               type: 'String',
               value: this.listRecommendationActionIds
            },
            {
               name: 'sPageName',
               type: 'String',
               value: this.sPageName
            },
            {
               name: 'InteractionId',
               type: 'Boolean',
               value: this.interactionId
            }
         ];
   }

   updateRecommendationBasedOnAction(){

      let actionIds =JSON.stringify(this.listRecommendationActionIds);
      updateRecommnedationBasedOnAction({lstRecommendationActionIds:actionIds})
      .then(result =>{
      this.alertsDetails = JSON.parse(result);
      this.alertsDetails.sPageName = this.sPageName;
      let tempAlertCount = this.alertCount;
      this.loadRecommendationDetails();
      this.bRefreshAlerts = this.alertCount !== tempAlertCount;
      this.publishAlertRefreshMessage();
      }).catch(e=>{
         console.log('Error in updateRecommendationBasedOnAction',JSON.stringify(e));
      });
   }


   setGuidanceAlertCount() {
      if (this.hasGuidanceAlerts) {
         if (this.guidanceAlertIds && Array.isArray(this.guidanceAlertIds)) {
            if (this.guidanceAlertIds.length === 0) {
               if (this.alertsDetails && this.alertsDetails.hasOwnProperty('mapOfRecommendationActionsIdWithType') && this.alertsDetails.mapOfRecommendationActionsIdWithType) {
                  for (const [key, value] of Object.entries(this.alertsDetails.mapOfRecommendationActionsIdWithType)) {
                     if (value.toLowerCase().includes("guidance")) {
                        this.guidanceAlertIds.push(key);
                     }
                  }
               }
            }
         }
         this.guidanceAlertCount = this.listRecommendationActionIds.filter(id => this.guidanceAlertIds.includes(id)).length;
      }

      this.publishGuidanceAlertsMessage();
   }

   messageContext = createMessageContext();

   publishGuidanceAlertsMessage() {
      let message = {
         alertCount: this.guidanceAlertCount,
         pageName: this.accountPageName,
         accountId: this.recordId,
         interToday: this.interId,
      };
      
      publish(this.messageContext, guidanceAlertMessageChannel, message);
      
   }

   publishAlertRefreshMessage() {
      if(this.bRefreshAlerts) {

          let message = {
              accountId: this.recordId,
           };
        
           publish(this.messageContext, alertRefreshMessageChannel, message);
      }
   }

}