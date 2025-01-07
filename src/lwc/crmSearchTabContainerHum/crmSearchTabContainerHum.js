/*******************************************************************************************************************************
LWC JS Name : crmSearchTabContainerHum.js
Function    : This JS serves as controller to crmSearchTabContainerHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Joel George                                          12/18/2020                    initial version
* Arpit/Navajit 				                       02/10/2021				  Modifications for Search and Integration
* Mohan Kumar N                                        02/19/2021                  Loading external style sheet
* Ritik Agarwal                                        28/02/2021                   SSN carry over
* Supriya Shastri                                      05/07/2021                   US-2199980 Added labels 
* Arpit jain                                           08/02/2021                   PubSub Change : pubSubComponent replaced by pubSubHum
* Arpit Jain                                           10/01/2022                   PubSub replaced by Lightning Message Channel
* Krishna Teja Samudrala                               03/18/2022                   Added interaction log component.
* Vardhman Jain                                        01/20/2023                   User story-4041468 Auto Search Legacy
* Ceasar Sabarre				                               01/31/2023					User Story - 4214872 Display Interaction Log on Enrollment Search
* Santhi Mandava                                       02/06/2013                 User Story 4082261,4084543: Display interaction information on search page and account detail page.
*********************************************************************************************************************************/
import { LightningElement, api, track, wire } from "lwc";
import pubSubHum from "c/pubSubHum";
import { CurrentPageReference } from "lightning/navigation";
import customcss from "@salesforce/resourceUrl/LightningCRMAssets_SR_HUM";
import { loadStyle } from "lightning/platformResourceLoader";
import { getLabels } from "c/crmUtilityHum";
import PHONEBOOKLMS from "@salesforce/messageChannel/phoneBookHum__c";
import USER_ID from '@salesforce/user/Id';
import CallCenter_FIELD from "@salesforce/schema/User.CallCenterId";
import { getRecord } from "lightning/uiRecordApi";
import {
  publish,
  subscribe,
  unsubscribe,
  APPLICATION_SCOPE,
  MessageContext,
} from "lightning/messageService";

export default class TabContainer extends LightningElement {
  @track sSSN = "";
  @track labels = getLabels();
  @track interactionLogCss = "container-sticky-ilp";
  @track genesysUser;


  @wire(getRecord, {
    recordId: USER_ID,
    fields: [CallCenter_FIELD]
  })
  wireuser({ error, data }) {
    if (error) {
		console.log('Error----',error);
    } else if (data) {
      this.genesysUser = data.fields.CallCenterId.value;
    }
  }

  handleActivetabSet(event) {
    var activeTabSet = event.target.value;
    if (activeTabSet === "mtvbenefit") {
      this.interactionLogCss = "container-sticky-ilp-none";
    } else {
      this.interactionLogCss = "container-sticky-ilp";
    }
  }

  handleTabSwitching(event) {
    const { tabVal, memberId } = event.detail;
    this.sSSN = memberId;
    this.template.querySelector("lightning-tabset").activeTabValue = tabVal;
  }

  constructor() {
    super();
    // loading css
    loadStyle(this, customcss + "/CRM_Assets/styles/commonStyles.css");
  }

  @track encodedData;
  callDataSubscription = null;
  @wire(MessageContext)
  messageContext;
  @wire(CurrentPageReference)
  wiredPageRef(pageRef) {
    this.pageRef = pageRef;
  }

  subscription() {
    if (!this.callDataSubscription) {
      this.callDataSubscription = subscribe(
        this.messageContext,
        PHONEBOOKLMS,
        (message) => {
          this.callDataValueCallback(message);
        },
        { scope: APPLICATION_SCOPE }
      );
    }

    publish(this.messageContext, PHONEBOOKLMS, {
      messageToSend: { ack: true },
    });
  }
  
   autoSearchHandler(event){
    let details =  JSON.stringify(event.detail);
    if(details){
      details = JSON.parse(details);
      if(details){
        this.callDataValueCallback(details.wrapper);
      }
    }
  }

  callDataValueCallback(result) {
    if (result.messageToSend.callData) {
     let sInteractionId = result.messageToSend.callData.sInteractionId;
      var callerData = result.messageToSend.callData;
      var callerTypeKeys = Object.keys(callerData);
      if (
        this.template.querySelector("lightning-tabset") != undefined &&
        this.template.querySelector("lightning-tabset") != null
      ) {
        this.template.querySelector("lightning-tabset").activeTabValue =
          callerTypeKeys[0];
      }
      var ref = this;
      this.resettabs();
      callerTypeKeys.forEach(function (item) {
        if (callerData && callerData != null) {
          switch (item) {
            case "member":
              if (
                ref.template.querySelector("c-member-search-form-hum") !=
                  undefined &&
                ref.template.querySelector("c-member-search-form-hum") != null
              ) {
                ref.template
                  .querySelector("c-member-search-form-hum")
                  .encodedValues(callerData);
              } else {
                ref.encodedData = callerData;
              }
              break;

            case "provider":
              if (
                ref.template.querySelector("c-provider-search-form-hum") !=
                  undefined &&
                ref.template.querySelector("c-provider-search-form-hum") != null
              ) {
                ref.template
                  .querySelector("c-provider-search-form-hum")
                  .encodedValues(callerData);
              } else {
                ref.encodedData = callerData;
              }
              break;

            case "group":
              if (
                ref.template.querySelector("c-group-search-form-hum") !=
                  undefined &&
                ref.template.querySelector("c-group-search-form-hum") != null
              ) {
                ref.template
                  .querySelector("c-group-search-form-hum")
                  .encodedValues(callerData);
              } else {
                ref.encodedData = callerData;
              }
              break;

            case "agent":
              if (
                ref.template.querySelector("c-agent-search-form-hum") !=
                  undefined &&
                ref.template.querySelector("c-agent-search-form-hum") != null
              ) {
                ref.template
                  .querySelector("c-agent-search-form-hum")
                  .encodedValues(callerData);
              } else {
                ref.encodedData = callerData;
              }
              break;
          }
        }
      });
      
      //Interaction panel will be prepopulated, if the logged in user is genesys user and interaction id is not null
      if(this.genesysUser && sInteractionId){
        let objInteraction = {};
        objInteraction.bOnActiveCall = true;
        objInteraction.sIntractionId = sInteractionId;
        if(ref.template.querySelector("c-Interaction-Cmp-Hum")){
          ref.template.querySelector("c-Interaction-Cmp-Hum").fetchInteractionDetails(objInteraction);
        }
      }
    }
  }

  resettabs() {
    var ref = this;
    if (
      ref.template.querySelector("c-member-search-form-hum") != undefined &&
      ref.template.querySelector("c-member-search-form-hum") != null
    ) {
      ref.template.querySelector("c-member-search-form-hum").handleReset();
    }

    if (
      ref.template.querySelector("c-provider-search-form-hum") != undefined &&
      ref.template.querySelector("c-provider-search-form-hum") != null
    ) {
      ref.template
        .querySelector("c-provider-search-form-hum")
        .handleResetProvider();
    }

    if (
      ref.template.querySelector("c-group-search-form-hum") != undefined &&
      ref.template.querySelector("c-group-search-form-hum") != null
    ) {
      ref.template.querySelector("c-group-search-form-hum").handleReset();
    }

    if (
      ref.template.querySelector("c-agent-search-form-hum") != undefined &&
      ref.template.querySelector("c-agent-search-form-hum") != null
    ) {
      ref.template.querySelector("c-agent-search-form-hum").handleResetAgent();
    }
  }

  unsubscribeToMessageChannel() {
    unsubscribe(this.callDataSubscription);
    this.callDataSubscription = null;
  }
  connectedCallback() {
    this.subscription();
  }

  disconnectedCallback() {
    this.unsubscribeToMessageChannel();
  }
}