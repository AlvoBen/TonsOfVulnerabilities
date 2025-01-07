/*LWC Name        : interactionCmp.js
Function        : LWC container to display Interaction log component in Lightning strides app.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Vardhman Jain                   01/20/2023                   User story-4041468 Auto Search Legacy
* Kalyani Pachpol                 02/01/2023                   US-4185604
* Santhi Mandava                  02/06/2013                  User Story 4082261,4084543: Display interaction information on search page and account detail page.
* Vardhman Jain                   02/06/2023                   DF7105 Fix
* Muthukumar 					            02/07/2023		         		   US-4141940 Authenticate incoming calls- IVR/Genesys
* Visweswara Rao J                02/07/2023                   User Story 4130775: T1PRJ0865978 -23998 / 4079568 C6, Lightning- Case management- Ability to create interactions for Home office policies- Jaguars
 * Nirmal Garg                    02/09/2023                    US-4185604 changes
 * Santhi Mandava                 02/07/2023                    US4304165: FirstName,LastName and Pharmacy id changes
 * Muthukumar 					          04/13/2023		          			US-4404843 Toggle button change
* Prasuna Pattabhi                04/14/2023                    US4470668:Interaction creation provider search - KNOWN Provider- Legacy Softphone - Interacting With and Interacting Button
 * Vardhman Jain                  04/13/2023                    US4363205: save and continue button changes
 * Abhishek Mangutkar              04/14/2023                    User Story 4465763 - Interaction creation for 'unknown' group accounts- with, about buttons
 * Anuradha Gajbhe                 04/17/2023                    User Story 4461361 - Interaction Creation on agency/broker search results- Interacting With and About Buttons (genesys).
 * Raj Paliwal                     04/17/2023                    User Story 4461416 - Interaction Log "Save & Continue" button points to Agency/Broker Business Account Page (genesys).
 * Raj Paliwal                     04/27/2023                    Defect Fix: 7580
 * Raj Paliwal                     05/02/2023                    User Story 4582301 - Interaction Creation on Agency/Broker Search Results- "With&About" button functionality.
 * Raj Paliwal                     05/04/2023                    Defect Fix: 7601
 * Sivaprakash Rajendran           05/04/2023                    US 4579434 - T1PRJ0036776: Enterprise - Caller Type Population with Phonebook - Lightning (Genesys)
 * Harshada Kamble/Anil Pavithran  05/05/2023                    US4461937- T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
 * Raj Paliwal                     05/16/2023                    Defect Fix: 7650, 7643, 7635
 * Muthukumar 					           05/22/2023				             US-4522776 & 4522916 warning message
 * Visweswara Rao J                07/021/2023                   User Story 4832620: T1PRJ1097507- NULL NULL Error when creating Interaction With Unknown Provider About Member
 * Nirmal Garg                      07/18/2022                user story 4861950, 4861945
 * Atul Patil                    07/28/2023                user story 4861950, 4861945
 * Harshada Kamble				         08/04/2023				             User Story 4890793 T1PRJ0036776: INC2447611, Consumer/Toggles button from interactions is not staying green (Genesys)
 * Harshada Kamble                 08/10/2023                    User Story 4954802: T1PRJ0307696: INC2463199 Watson VOC data error for AGENT_CALL_START_TIME (Genesys)
 * Robert Crispen                  09/06/2023                   User Story 4951430 T1PRJ0891339 - Feature 4837529 - Chat Assistant Panel (Default & On Load of Person Account & Plan Member)
 * Disha Dhole                      02/28/2024                  User Story 5452132-T1PRJ0865978: - INC2632156: CRM Service lightening : Creates double entry with one case
******************************************************************************************************************************/
import { LightningElement, track, wire, api } from "lwc";
import { createRecord } from "lightning/uiRecordApi";
import { updateRecord } from "lightning/uiRecordApi";
import { deleteRecord } from "lightning/uiRecordApi";
import { getRecord } from "lightning/uiRecordApi";
import { getObjectInfo } from "lightning/uiObjectInfoApi";
import { getPicklistValues } from "lightning/uiObjectInfoApi";
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import {
  getSessionItem,
  hcConstants,
  setSessionItem,
  getLabels,
  compareDate,
  getLocaleDate,
  getBaseUrl
} from "c/crmUtilityHum";
import { NavigationMixin } from "lightning/navigation";
import INTERACTION_OBJECT from "@salesforce/schema/Interaction__c";
import INTERACTION_MEMBER_OBJECT from "@salesforce/schema/Interaction_Member__c";
import CASE_INTERACTION_OBJECT from "@salesforce/schema/Case_Interaction__c";
import INTERACTING_WITH from "@salesforce/schema/Interaction__c.Interacting_With__c";
import INTERACTING_WITH_TPE from "@salesforce/schema/Interaction__c.Interacting_With_type__c";
import INTERACTION_ORIGIN from "@salesforce/schema/Interaction__c.Interaction_Origin__c";
import INTERACTION_AUTHENTICATED from "@salesforce/schema/Interaction__c.Authenticated__c";
import INTERACTION_NUMBER from "@salesforce/schema/Interaction__c.Id";
import INTERACTING_WITH_NAME from "@salesforce/schema/Interaction__c.Caller_Name__c";
import INTERACTING_ABOUT from "@salesforce/schema/Interaction_Member__c.Interacting_About__c";
import INTERACTING_ABOUT_TYPE from "@salesforce/schema/Interaction_Member__c.Interacting_About_Type__c";
import INTERACTING_INTERACTION from "@salesforce/schema/Interaction_Member__c.Interaction__c";
import INTERACTING_ABOUT_AUTHENTICATED from "@salesforce/schema/Interaction_Member__c.Authenticated__c";
import INTERACTING_MEMEBR_ID from "@salesforce/schema/Interaction_Member__c.Id";
import USER_ID from "@salesforce/user/Id";
import CallCenter_FIELD from "@salesforce/schema/User.CallCenterId";
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import CASEID_FIELD from "@salesforce/schema/Case_Interaction__c.Case__c";
import INTERACTIONID_FIELD from "@salesforce/schema/Case_Interaction__c.Interaction__c";
import getInteractionList from "@salesforce/apex/InteractionController_LC_HUM.getInteractionList";
import getInteractionMemberList from "@salesforce/apex/InteractionController_LC_HUM.getInteractionMemberList";
import getInteractingAbout from "@salesforce/apex/InteractionController_LC_HUM.showAckPopup";
import initializeVariables from "@salesforce/apex/InteractionController_LC_HUM.initializeVariables";
import authCheckFunction from "@salesforce/apex/InteractionController_LC_HUM.authCheckHandler";
import getSelectedIntMemberDetails from "@salesforce/apex/InteractionController_LC_HUM.getSelectedIntMemberDetails";
import getCaseInteraction from "@salesforce/apex/InteractionController_LC_HUM.getCaseInteraction";
import getIntWithRecType from "@salesforce/apex/InteractionController_LC_HUM.getIntWithRecType";
import insertProviderAccount from "@salesforce/apex/ProviderSearch_LC_HUM.insertProviderAccount";
import getAccountId from "@salesforce/apex/InteractionController_LC_HUM.getAccountId";
import maximizeWindowTemplate from './interactionCmpHumMaximize.html';
import minimizeWindowTemplate from './interactionCmpHumMinimize.html';
import handleRecordDml from "@salesforce/apex/InteractionController_LC_HUM.handleRecordDml";
import insertMemberAccount from '@salesforce/apex/MemberSearchEnrollmentHelper_LC_HUM.insertMemberAccount';
import refreshIntLogLMS from "@salesforce/messageChannel/RefreshInteractionLog__c";
import PHONEBOOKCALLERTYPELMS from "@salesforce/messageChannel/phoneBookCallerTypeHum__c"; //Added for US#4579434 - Caller type population
import MYAHLMS from "@salesforce/messageChannel/myAssistantAtHumana_CMP_LMS__c"; // Added for US#4951430 - AI Chat Assistant Panel
import isMyAHSwitchedON from '@salesforce/apex/MyAssistantAtHumana_LC_HUM.getMyAHOnOffSwitchBooleanValue'; //MyAH Feature Switch
import getCallMemberAccountDetails from '@salesforce/apex/PhoneBook_LC_HUM.getCallMemberAccountDetails'; // Added for US#4951430 - AI Chat Assistant Panel
import hasMyAHAccess from '@salesforce/customPermission/CRMS_900_My_Assistant_At_Humana'; // Added for US#4951430 - AI Chat Assistant Panel

import {
  APPLICATION_SCOPE,
  createMessageContext,
  MessageContext,
  publish,
  releaseMessageContext,
  subscribe,
  unsubscribe
} from "lightning/messageService";
import ICLMS from "@salesforce/messageChannel/Interaction_Cmp_Lms__c";
import { invokeWorkspaceAPI,opentab } from "c/workSpaceUtilityComponentHum";
import pubSubHum from "c/pubSubHum";
import { CurrentPageReference } from "lightning/navigation";
import HUMNoInteractionCreated from '@salesforce/label/c.HUMNoInteractionCreated';
import hasQuickStartUser_HUMAccess from '@salesforce/customPermission/QuickStartUser_HUM';
import HUMINTERACTIONVALIDATION from '@salesforce/label/c.HUM_INTERACTION_VALIDATION';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import MyAH_LWS_toMyAH_Label from '@salesforce/label/c.MyAssistantAtHumanaMessageChannelMyAH';
import MyAH_LMS_toInteractionLog_Label from '@salesforce/label/c.MyAssistantAtHumanaMessageChannelInteractionLog';

export default class InteractionCmpHum extends NavigationMixin(
  LightningElement
) {
  value = "";
  pageMessages = false;
  cardCss = "sld-card card-lip";
  pageUrl;
  genesysUser;
  isLocked=false;
  @api prop2;
  @api isAccountPage = false;
  @api isCasePage;
  @api caseRecordId;
  @track interactionrecordId;
  @track InteractingAboutTypevalue = "";
  @track InteractionOriginvalue = "";
  @track industryPicklistvalue = "";
  @track IntWithLkpValue = "";
  @track IntNumLkpValue = "";
  @track IntWithLkpValue1 = "";
  @track IntNumLkpValue1 = "";
  @track InteractionNoLabel = "Interaction #";
  @track InteractingWithLabel = "Interacting With";
  @track cssClass =
    "slds-utility-bar_container slds-utility-bar container-lip-2";
  @track iconName = "utility:minimize_window";
  @track cssClass1 = "container-lip-6";
  @track cssClass2 =
    "slds-m-around_medium slds-m-bottom_medium slds-clearfix card-buttons-lip";
  @track cssClass3 = "";
  @track firstName = "";
  @track intNumName = "";
  @track intNumRecordId = "";
  @track intWithName = "";
  @track intWithRecordId = "";
  @track labels = getLabels();
  @track isModalOpen = false;
  @track showIntsearch = false;
  @track errorMessage;
  @track Interaction;
  @track InteractionMemberrec;
  @track InteractingAboutvalue = "";
  @track InteractingAboutvalueTemp ="";
  @track error;
  @track memberCreated = false;
  @track InteractingAboutPicklistvalue = [{ label: "--None--", value: "none" }];
  @track interWithTypePikLst = [];
  @track originPikLst = [];
  @track interactingAboutTypePikLst = [];
  @track recordType;
  @track InteractingAboutRecordType = "";
  @track interactingAboutId = null; 
  @track featureSwitchMyAH = false; 
  @track bShowModal = false;
  @track cShowModal = false;
  @track sinteractionId = "";
  @track mf3Switch = true;
  @track mf3Switch2;
  @track Switch_2230000;
  @track validateCRMSwitch;
  @track multiMemberAuthSwitch;
  @track bWithAboutButtonSwitch = false;
  @track authCheck = false;
  @track authCheckLabel = "Unauthenticated";
  @track bShowUpdate = false;
  @track saveRender = false; //this.buttonRenderLogicSave();
  @track saveNContbtnRender;
  @track interactingAboutRadioValue = "";
  @track InteractionMemberList = [];
  @track radioErrorMessages;
  @track radioErrorMessage;
  @track passwordModalCss = "password-modal-css";
  //@track InteractionMemberList1 = [];
  @track intMemberExists = false;
  @track intMemberAuthUpdated =false;
  @track intaboutid;
  @track genesysUserBtn = false;
  @track interactionNameAcc = "";
  @track handleAssCaseFlag;
  @track caseInteractionrecordId;
  @track maximizeWindow=true;
  @track minimizeWindow=false;
  @track authMessage;
  @track authMsgStyle;
  @track isActionMsgShow = false;
  @track isShowAuthSol = false;
  @track authcheckValue = false;
  @track toggleBGColor = 'slds-checkbox_faux';
  @track actionMsg = 'Action Required : This caller is not authenticated';
  @track disabledtoggle = false;
  @track isDisabled = false;
  @track triggeredEvent = '';
  @track objAccData = {};
  @track intAboutAccName = '';
  @track searchvalue;
  @track searchCmp;
  @track searchIntWith;
  @track searchIntWithValue;
  @track intAboutCurSelValue;
    @track isLockedAcc;
    @track networkId;
    @track profileName;
  @track warningMessage = HUMNoInteractionCreated;
  @track questionDetails = {
    question: "",
    answer: "",
    pValue: ""
  };
  @track buttonsConfig = [
    {
      text: getLabels().HUMAlertsAcknowledge,
      isTypeBrand: true,
      eventName: hcConstants.CLOSE
    }
  ];
  @track updateButtonsConfig = [
    {
      text: "Delete",
      isTypeBrand: true,
      eventName: "delete"
    },
    {
      text: "Cancel",
      isTypeBrand: false,
      eventName: hcConstants.CLOSE
    }
  ];
  InitialObjsdata;
  fieldPikValues;
  error;
  dontshowmodal1;
  bIntCreationStatus = false;
  groupIsInteractingAbout;
  groupIsInteractingWith;
  calledFromMemberSearch = false; 
  callerTypePopulationSwitch;//Added for US#4579434
  hasError = false;//Added for US#4579434
  refreshIntLogLMSSwitch =false;//Added for US#4890793 

  handlechangeobjapi2;
  @api
  get handlechangeobjapi() {
    return this.handlechangeobjapi2;
  }
  @track buttonConfiguration = [{
    text: "No",
    isTypeBrand: false,
    eventName: "close"
},
{
  text: "Yes",
  isTypeBrand: true,
  eventName: "continue"
}];
  set handlechangeobjapi(value) {
    let tempList = [...value];
    this.handlechangeobjapi2 = tempList;
    this.handleInteractionFieldChanges(tempList);
  }

  constructor() {
    super();
  }

  async getEnclosingTabDetails() {
    let firingTabId;
    let firingtabParentid;
    let subtabs;
    await invokeWorkspaceAPI("getFocusedTabInfo").then((primaryFocusTab) => {
      if (
        primaryFocusTab.tabId != undefined &&
        primaryFocusTab.recordId != undefined
      ) {
        firingTabId = primaryFocusTab.tabId;
        firingtabParentid = primaryFocusTab.parentTabId;
        subtabs = primaryFocusTab.subtabs;
      }
      const message = {
        firingTabId: firingTabId,
        firingtabParentid: firingtabParentid,
        subtabs: subtabs
      };
      if (firingtabParentid) {
        publish(this.messageContext, ICLMS, message);
      }
    });
  }

  connectedCallback() {
	this.subscribeToLMSChannel();
    isMyAHSwitchedON()
    .then(switchState=>{
      if(switchState === true && hasMyAHAccess){
        this.featureSwitchMyAH = true;
        this.subscribeToMyAHMessageChannel();
      }
    })
    .catch(error=>{
      console.log('MyAH Switch Error ',error)
    })
    if (this.isAccountPage) {
      this.handleMinimizeBtn();
    }
    pubSubHum.registerListener(
      "CLEAR_MEMBER_POLICY_INT",
      this.handleClearPolicyData,
      this
    );
    pubSubHum.registerListener(
      "MEMBER_POLICY_INT",
      this.handlePolicyData,
      this
    );
    pubSubHum.registerListener(
      "MEMBER_SEARCH_FNLNPhId",
      this.nameAndPhIdclickHandler,
      this
    );
    pubSubHum.registerListener(
      "InteractingWithnAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "InteractingWith",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "InteractingAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "gInteractingWithnAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "gInteractingWith",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "gInteractingAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "pInteractingWithnAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "pInteractingWith",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "pInteractingAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "AGENCY_SEARCH_FNLNPhId",
      this.nameAndPhIdagencyclickHandler,
      this
    );
    pubSubHum.registerListener(
      "aInteractingWithnAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "aInteractingWith",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "aInteractingAbout",
      this.handleInteractionData,
      this
    );
    pubSubHum.registerListener(
      "intlogcomponentfieldchanges",
      this.handleInteractionFieldChanges,
      this
    );
    
     pubSubHum.registerListener(
      "ENROLLMENT_SEARCH_FNLN",
      this.enrollmentLinksHandler,
      this
    );
    
    pubSubHum.registerListener(
      "PROVIDER_SEARCH_NAMEPrId",
      this.nameProviderSearchChanges,
      this
    );
    
    pubSubHum.registerListener(
      "eInteractingWithnAbout",
      this.enrollmentLinksHandler,
      this
    );
    pubSubHum.registerListener(
      "eInteractingWith",
      this.enrollmentLinksHandler,
      this
    );
    pubSubHum.registerListener(
      "eInteractingAbout",
      this.enrollmentLinksHandler,
      this
    );
    
    pubSubHum.registerListener(
      "GROUP_SEARCH_FNLN",
      this.groupNameClickHandler,
      this
    );
    
    this.displayInteractionDetailsOnLoad();
  }

  fireEvent(eventName, detail) {
    this.dispatchEvent(
      new CustomEvent(eventName, {
        detail
      })
    );
  }

  @track intLmsObjList = [];

  @api
  handleInteractionFieldChanges1(message) {
    this.isAccountPage = true;
    let intLmsObjList1 = JSON.stringify(message);
    this.loopIntFeildsList(intLmsObjList1);
  }
  handleInteractionFieldChanges(message) {
    if (this.isAccountPage) {
      let inboundmessage = message;
      inboundmessage.forEach((el) => {
        if (el.key === "firstNameinput") {
          this.firstName = el.value;
        } else if (el.key === "InteractingAbout") {
          this.InteractingAboutvalue = el.value;
        } else if (el.key === "InteractionOrigin") {
          this.InteractionOriginvalue = el.value;
        } else if (el.key === "Interactingwithtype") {
          this.industryPicklistvalue = el.value;
        } else if (el.key === "InteractingAboutType") {
          this.InteractingAboutTypevalue = el.value;
        } else if (el.key === "IntWithLkp") {
          this.template
            .querySelector('[data-id="IntWithDID"]')
            .setIntNumLkpValue(el.label, el.value);
          this.intWithRecordId = el.value;
		  this.intWithName = el.label;
        } else if (el.key === "IntNumInp") {
          this.interactionNameAcc = el.value;
          this.intNumRecordId = this.interactionrecordId = el.recordId;
        } else if (el.key === "clear") {
          this.handleResetAll();
        } else if (el.key === "InteractingAboutPlkValues") {
          const tempArr = el.value;
          this.InteractingAboutPicklistvalue = [];
          tempArr.forEach((ele) => {
            this.InteractingAboutPicklistvalue = [
              ...this.InteractingAboutPicklistvalue,
              {
                label: ele.label,
                value: ele.value
              }
            ];
          });
		  if(this.template.querySelector(
            '[data-id="InteractingAbout"]'
          ) != null){
          const interactingAboutPik = this.template.querySelector(
            '[data-id="InteractingAbout"]'
          );
          interactingAboutPik.value = el.valueofrec;
		  }
          this.InteractingAboutRecordType = el.InteractingAboutRecordType;
        } else if (el.key.startsWith("001")) {
          let intLmsObjList1 = JSON.stringify(el.value);
          this.loopIntFeildsList(intLmsObjList1);
        }
      });
    }
  }

  loopIntFeildsList(intLmsObjList1) {
    if (this.isAccountPage) {
      let inboundmessage = JSON.parse(intLmsObjList1);
      inboundmessage.forEach((el) => {
        if (el.key === "firstNameinput") {
          this.firstName = el.value;
        } else if (el.key === "InteractingAbout") {
          this.InteractingAboutvalue = el.value;
        } else if (el.key === "InteractionOrigin") {
          this.InteractionOriginvalue = el.value;
        } else if (el.key === "Interactingwithtype") {
          this.industryPicklistvalue = el.value;
        } else if (el.key === "InteractingAboutType") {
          this.InteractingAboutTypevalue = el.value;
        } else if (el.key === "IntWithLkp") {
          this.template
            .querySelector('[data-id="IntWithDID"]')
            .setIntNumLkpValue(el.label, el.value);
          this.intWithRecordId = el.value;
          this.intWithName = el.label;
        } else if (el.key === "IntNumInp") {
          this.interactionNameAcc = el.label;
          this.intNumRecordId = this.interactionrecordId = el.value;
        } else if (el.key === "clear") {
          this.handleResetAll();
        } else if (el.key === "InteractingAboutPlkValues") {
          const tempArr = el.value;
          this.InteractingAboutPicklistvalue = [];
          tempArr.forEach((ele) => {
            this.InteractingAboutPicklistvalue = [
              ...this.InteractingAboutPicklistvalue,
              {
                label: ele.label,
                value: ele.value
              }
            ];
          });
		  if(this.template.querySelector(
            '[data-id="InteractingAbout"]'
          ) != null){
          const interactingAboutPik = this.template.querySelector(
            '[data-id="InteractingAbout"]'
          );
          interactingAboutPik.value = el.valueofrec;
		  }
          this.InteractingAboutRecordType = el.InteractingAboutRecordType;
        }
		else if(el.key === "authenticatorValue" && this.InteractionOriginvalue != null && this.InteractionOriginvalue ==='Inbound Call'){
          this.authcheckValue = el.value ? true:false;
          this.displayTogglebutton();
          this.disabledtoggle = el.value ? true:false;
        }
      });
    }
  }

  @track memberPlanId;
  handlePolicyData(event){
    this.memberPlanId = event.message;
  }

  handleClearPolicyData(event){
    this.memberPlanId = null;
  }

  nameAndPhIdclickHandler(objpayload){
    if (
      this.InteractionOriginvalue === "none" ||
      this.InteractionOriginvalue === null ||
      this.InteractionOriginvalue === undefined ||
      this.InteractionOriginvalue === ""
    ) {
      this.InteractionOriginvalue = "Inbound Call";
    }
    const eventName = objpayload?.message?.sourceSystem;
    const intwithandaboutobj = objpayload?.message?.messageToSend[0]?.rowData;
    this.isLocked = intwithandaboutobj?.isLocked;
    this.triggeredEvent = intwithandaboutobj?.originalEvent;
    this.objAccData = intwithandaboutobj;
    this.intAboutAccName = `${this.objAccData?.FirstName} ${this.objAccData?.LastName}`;
    this.intAboutAccName = this.intAboutAccName=='null null'?`${this.objAccData?.DBA__c}`:this.intAboutAccName;
     this.calledFromMemberSearch = true;
    if((this.industryPicklistvalue && this.intWithName && this.intWithRecordId) ||
    (this.industryPicklistvalue && this.firstName)){
      let tempintaboutvalue = intwithandaboutobj.Id;
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;
      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue
        ];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label:
              intwithandaboutobj.FirstName + " " + intwithandaboutobj.LastName,
            value: intwithandaboutobj.Id
          }
        ];
      }
      
      //US4723377:
      if(this.multiMemberAuthSwitch){
        this.InteractingAboutvalueTemp =this.InteractingAboutvalue;
      }

       if(this.template.querySelector(
        '[data-id="InteractingAbout"]'
      ) != null){
      let interactingAboutPik = this.template.querySelector(
        '[data-id="InteractingAbout"]'
      );
      interactingAboutPik.value = intwithandaboutobj.Id;
	  }
      this.InteractingAboutTypevalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
        let interactingAboutTypePik = this.template.querySelector(
          '[data-id="InteractingAboutType"]'
        );
        interactingAboutTypePik.value = this.InteractingAboutTypevalue;

      
      this.InteractingAboutRecordType = intwithandaboutobj.RecordType;
      this.InteractingAboutvalue = intwithandaboutobj.Id;

      //US4723377
      if(this.multiMemberAuthSwitch)
      {
        this.handleAuthChangeOnNameLinkClick(this.InteractingAboutvalueTemp,intwithandaboutobj.Id ) 
      }
      else
      {
        this.createIntRecOpenAcc();
      }
          
    }else{
      this.InteractingAboutvalue = intwithandaboutobj.Id;
      if(eventName === 'MEMBER_SEARCH_FNLNPhId' && hasQuickStartUser_HUMAccess){
        this.isModalOpen = true;
        this.isLockedAcc = intwithandaboutobj.isLocked;
        }else{
        if(intwithandaboutobj.isLocked){
        this.handleNavigatetoProtectedPage();
      }else{
        this.navigateToViewAccountPage();  
      }
      }
    }
  }
  
  enrollmentLinksHandler(objpayload){
	  this.isSearchBtnUsed = false;
	if(
      this.InteractionOriginvalue === "none" ||
      this.InteractionOriginvalue === null ||     
      this.InteractionOriginvalue === undefined ||      
      this.InteractionOriginvalue === ""      
      ) {
        this.InteractionOriginvalue = "Inbound Call";      
      }  
    let selectedrow = {};
    if (objpayload) {
      selectedrow = objpayload.message?.messageToSend[0]?.rowData?.sLegacyMemberJson ? JSON.parse(objpayload.message?.messageToSend[0]?.rowData?.sLegacyMemberJson) : objpayload;
    }
    let sExternalID = objpayload.message?.messageToSend[0]?.rowData?.sExternalID;      
    if(selectedrow) {
    insertMemberAccount({ accountJson: JSON.stringify(selectedrow), externalId: sExternalID }).then(result => {
      if(result){
       selectedrow.Id = result;
          selectedrow.originalEvent = objpayload.message?.messageToSend[0]?.rowData?.originalEvent ? objpayload.message?.messageToSend[0]?.rowData?.originalEvent : objpayload?.message?.sourceSystem;  
          selectedrow.RecordType = 'Unknown Member';
          let rowData = {...objpayload.message?.messageToSend[0]?.rowData};
          rowData['Id'] = result;
          rowData['FirstName'] = selectedrow.LastName;
          rowData['LastName'] = selectedrow.FirstName;
          rowData['RecordType'] = 'Unknown Member';
          let recordDataobj = {"rowData" : rowData};
          recordDataobj.rowData = rowData;
          const message = {
          sourceSystem: "ENROLLMENT_SEARCH_FNLN",
          message : {}
        };
        message.message.messageToSend = [];
        message.message.messageToSend.push(recordDataobj);

          let eventName = '';
          if(selectedrow.originalEvent === 'eInteractingWithnAbout') eventName = 'InteractingWithnAbout';
          if(selectedrow.originalEvent === 'eInteractingWith') eventName = 'InteractingWith';
          if(selectedrow.originalEvent === 'eInteractingAbout') eventName = 'InteractingAbout';
          if(eventName) {
            this.isSearchBtnUsed = true;
            this.parseMemberData(eventName,selectedrow);
          }
          else if(selectedrow.originalEvent == 'ENROLLMENT_SEARCH_FNLN') this.nameAndPhIdclickHandler(message);
      }
    }).catch(error => {
        console.log('Error Occured', error);
    });
  }
  }

  nameAndPhIdagencyclickHandler(objpayload){
    if (
      this.InteractionOriginvalue === "none" ||
      this.InteractionOriginvalue === null ||
      this.InteractionOriginvalue === undefined ||
      this.InteractionOriginvalue === ""
    ) {
      this.InteractionOriginvalue = "Inbound Call";
    }
    const eventName = objpayload?.message?.sourceSystem;
    const intwithandaboutobj = objpayload?.message?.messageToSend[0]?.rowData;
    this.isLocked = intwithandaboutobj?.isLocked;
    this.triggeredEvent = intwithandaboutobj?.originalEvent;
    this.objAccData = intwithandaboutobj;
    this.intAboutAccName = `${this.objAccData?.strFirst} ${this.objAccData?.urlLastName}`;
    this.intAboutAccName = this.intAboutAccName=='null null'?`${this.objAccData?.DBA__c}`:this.intAboutAccName;

    if((this.industryPicklistvalue && this.intWithName && this.intWithRecordId) || (this.industryPicklistvalue && this.firstName)){
      let tempintaboutvalue = this.ids?this.ids:intwithandaboutobj.sAgencyExtId;
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;
      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue
        ];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label:this.intAboutAccName,
              /*intwithandaboutobj.strFirst +
                    " " +
                    intwithandaboutobj.urlLastName,*/
            value: tempintaboutvalue
          }
        ];
      }

      //US4723377
      if(this.multiMemberAuthSwitch){
        this.InteractingAboutvalueTemp = this.InteractingAboutvalue;
      }

       if(this.template.querySelector(
        '[data-id="InteractingAbout"]'
      ) != null){
      let interactingAboutPik = this.template.querySelector(
        '[data-id="InteractingAbout"]'
      );
      interactingAboutPik.value = tempintaboutvalue;
	  }
      
      let interactingAboutTypePik = this.template.querySelector(
        '[data-id="InteractingAboutType"]'
      );
      if (eventName === "AGENCY_SEARCH_FNLNPhId" && !this.bWithAboutButtonSwitch)
      {
        if(intwithandaboutobj.salesforceAccount.RecordType != null && intwithandaboutobj.salesforceAccount.RecordType != undefined && intwithandaboutobj.salesforceAccount.RecordType != {}){
          if (intwithandaboutobj.salesforceAccount.RecordType.Name === "Agent/Broker") {
            this.recordTypeName = "Agent";
          } else if (intwithandaboutobj.salesforceAccount.RecordType.Name === "Unknown Agent/Broker") {
            this.recordTypeName = "Unknown Agent";
          }
        }
        else{
          if (intwithandaboutobj.recordType === "Agency" || intwithandaboutobj.recordType === "Broker" ) {
            this.recordTypeName = "Agent";
          } else if (intwithandaboutobj.recordType === "Unknown Agent" || intwithandaboutobj.recordType === "Unknown Broker") {
            this.recordTypeName = "Unknown Agent";
          }
        }
      }
	    this.InteractingAboutTypevalue =
        this.recordTypeName
              .split(" ")
              .join("-");
        interactingAboutTypePik.value = this.InteractingAboutTypevalue;
      
      this.InteractingAboutRecordType = this.recordTypeName;
      this.InteractingAboutvalue = tempintaboutvalue;
      
      if (eventName === "AGENCY_SEARCH_FNLNPhId") {
          if (this.recordTypeName === "Unknown Agent") {
            this.disableIntAbout = true
          }  
          //US4723377
          if(this.multiMemberAuthSwitch){
            this.handleAuthChangeOnNameLinkClick(this.InteractingAboutvalueTemp,tempintaboutvalue )
          }
          else{
            this.createIntRecOpenAcc();
          }
      }
    }else{
      this.InteractingAboutvalue = intwithandaboutobj.sAgencyExtId;
      if(eventName === 'AGENCY_SEARCH_FNLNPhId' && hasQuickStartUser_HUMAccess){
        this.isModalOpen = true;
        this.isLockedAcc = intwithandaboutobj.isLocked;
        }else{
          if(intwithandaboutobj.isLocked){
            this.handleNavigatetoProtectedPage();
          }else{
            this.navigateToViewAccountPage();  
          }
        }
    }
  }

 
  @track isSearchBtnUsed = false;
  handleInteractionData(objpayload) {
    this.disableIntAbout = false
    this.getInteractionBtnFlag = false;
    this.isSearchBtnUsed = true;
    const eventName = objpayload.message.sourceSystem;
    const intwithandaboutobj = objpayload.message.messageToSend[0].rowData;
    this.objAccData = intwithandaboutobj;
        this.intAboutAccName =`${this.objAccData?.FirstName} ${this.objAccData?.LastName}`;
        this.intAboutAccName = this.intAboutAccName=='null null'?`${this.objAccData?.DBA__c}`:this.intAboutAccName;

    if (
      this.InteractionOriginvalue === "none" ||
      this.InteractionOriginvalue === null ||
      this.InteractionOriginvalue === undefined ||
      this.InteractionOriginvalue === ""
    ) {
      this.InteractionOriginvalue = "Inbound Call";
    }

    if (
      eventName === "gInteractingAbout" ||
      eventName === "pInteractingAbout" ||
      eventName === "aInteractingAbout" ||
      eventName === "InteractingAbout"
    ) {
      this.bIntCreationStatus = false;
    }
    
    if (
      eventName === "InteractingWithnAbout" ||
      eventName === "InteractingWith" ||
      eventName === "InteractingAbout"
    ) {
      this.parseMemberData(eventName, intwithandaboutobj);
    }

    if (
      eventName === "gInteractingWithnAbout" ||
      eventName === "gInteractingWith" ||
      eventName === "gInteractingAbout"
    ) {
      this.parseGroupData(eventName, intwithandaboutobj);
    }

    if (
      eventName === "pInteractingWithnAbout" ||
      eventName === "pInteractingWith" ||
      eventName === "pInteractingAbout"
    ) {
      this.parseProviderData(eventName, intwithandaboutobj);
    }
    if (
      eventName === "aInteractingWithnAbout" ||
      eventName === "aInteractingWith" ||
      eventName === "aInteractingAbout"
    ) {
      this.bWithAboutButtonSwitch = true;
      this.parseAgentData(eventName, intwithandaboutobj);
    }
  }
  disableIntAbout;
  parseAgentData(eventName, intwithandaboutobj) {
    let ids;
    let recordTypeName;
    
    if (intwithandaboutobj.salesforceAccount.Account_External_ID__c){
      this.fetchrecordTypeExtId(eventName, intwithandaboutobj);
    }else{
      this.fetchrecordTypeId(eventName, intwithandaboutobj);
    }
  }

  fetchrecordTypeExtId(eventName, intwithandaboutobj){
    getAccountId({
      extaccId: intwithandaboutobj.salesforceAccount.Account_External_ID__c
    })
    .then((response) => {
      if (response) {
          let result = JSON.parse(response);
          this.ids = result.Id;
          this.recordTypeName = result.RecordType.Name;
          this.parseAgentDataDetail(eventName, intwithandaboutobj);
      }else {
        this.dispatchEvent(
          new ShowToastEvent({
            title: "Error",
            message:
              "Something went wrong, please contact your administrator.",
            variant: "error"
          })
        );
      }
    })
    .catch((error) => {
      console.log(error);
    });
  }

  fetchrecordTypeId(eventName, intwithandaboutobj){
    getIntWithRecType({
      accRecordId: intwithandaboutobj.salesforceAccount.Id
      })  
      .then((response) => {
        if (response) {
            this.ids = intwithandaboutobj.salesforceAccount.Id;
            this.recordTypeName = response;
            this.parseAgentDataDetail(eventName, intwithandaboutobj);
        }else {
          this.dispatchEvent(
            new ShowToastEvent({
              title: "Error",
              message:
                "Something went wrong, please contact your administrator.",
              variant: "error"
            })
          );
        }
      })
      .catch((error) => {
        console.log(error);
      });
  }

  parseAgentDataDetail(eventName, intwithandaboutobj){
    if (this.recordTypeName === "Agent/Broker") {
      this.recordTypeName = "Agent";
    } else if (this.recordTypeName === "Unknown Agent/Broker") {
      this.recordTypeName = "Unknown Agent";
    }

    if (eventName === "aInteractingWithnAbout" || eventName === "aInteractingWith")
    {
      this.IntWithLkpValue = intwithandaboutobj.strFirst + " " + intwithandaboutobj.urlLastName;
      this.IntWithLkpValue1 = this.ids;
      this.template.querySelector('[data-id="IntWithDID"]').setIntNumLkpValue(this.IntWithLkpValue, this.IntWithLkpValue1);
      this.intWithRecordId = this.IntWithLkpValue1;
      this.intWithName = this.IntWithLkpValue;
      this.industryPicklistvalue = this.recordTypeName.split(" ").join("-");
    }

    if (eventName === "aInteractingWithnAbout" || eventName === "aInteractingAbout")
    {
      let tempintaboutvalue = "";
      tempintaboutvalue = this.ids;
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;

      //US4723377
      if(this.multiMemberAuthSwitch){
        if(this.InteractingAboutvalue != tempintaboutvalue){
          this.InteractingAboutvalue = tempintaboutvalue;
          this.setAuthValue("");
        }
      }

      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [...this.InteractingAboutPicklistvalue];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label: intwithandaboutobj.strFirst + " " + intwithandaboutobj.urlLastName,
            value: tempintaboutvalue
          }
        ];
      }

      if(this.template.querySelector('[data-id="InteractingAbout"]') != null){
        let interactingAboutPik = this.template.querySelector('[data-id="InteractingAbout"]');
        interactingAboutPik.value = tempintaboutvalue;
      }
      let interactingAboutTypePik = this.template.querySelector('[data-id="InteractingAboutType"]');

      this.InteractingAboutTypevalue = null;
      this.InteractingAboutTypevalue = this.recordTypeName.split(" ").join("-");
       
      interactingAboutTypePik.value = this.InteractingAboutTypevalue;
      this.InteractingAboutRecordType = this.recordTypeName;
      this.InteractingAboutvalue = tempintaboutvalue;
    }
    
    if (eventName === "aInteractingWithnAbout") {
      this.InteractingAboutTypevalue = this.industryPicklistvalue = this.recordTypeName.split(" ").join("-");
      if (this.recordTypeName === "Unknown Agent") {
        this.disableIntAbout = true
      }
      this.createIntRecOpenAcc();
      //US4723377
      if(this.multiMemberAuthSwitch){
        this.setAuthValue("");
      }
    }     
  }

  parseMemberData(eventName, intwithandaboutobj) {
    if (
      eventName === "InteractingWithnAbout" ||
      eventName === "InteractingWith"
    ) {
      this.IntWithLkpValue1 = intwithandaboutobj.Id;
      this.isLocked = intwithandaboutobj.isLocked;
      this.IntWithLkpValue =
        intwithandaboutobj.FirstName + " " + intwithandaboutobj.LastName;

      this.template
        .querySelector('[data-id="IntWithDID"]')
        .setIntNumLkpValue(this.IntWithLkpValue, this.IntWithLkpValue1);
      this.intWithRecordId = this.IntWithLkpValue1;
	  this.intWithName = this.IntWithLkpValue;
      this.industryPicklistvalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
    }

    if (
      eventName === "InteractingWithnAbout" ||
      eventName === "InteractingAbout"
    ) {
      let tempintaboutvalue = intwithandaboutobj.Id;
      this.interactingAboutId = intwithandaboutobj.Id; //US#4951430
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;

      //US4723377
      if(this.multiMemberAuthSwitch){
        if(this.InteractingAboutvalue != tempintaboutvalue){
          this.InteractingAboutvalue = tempintaboutvalue;
          this.setAuthValue("");
        }
      }

      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue
        ];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label:
              intwithandaboutobj.FirstName + " " + intwithandaboutobj.LastName,
            value: intwithandaboutobj.Id
          }
        ];
      }
       if(this.template.querySelector(
        '[data-id="InteractingAbout"]'
      ) != null){
      let interactingAboutPik = this.template.querySelector(
        '[data-id="InteractingAbout"]'
      );
      interactingAboutPik.value = intwithandaboutobj.Id;
	  }
      this.InteractingAboutTypevalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
        let interactingAboutTypePik = this.template.querySelector(
          '[data-id="InteractingAboutType"]'
        );
        interactingAboutTypePik.value = this.InteractingAboutTypevalue;

      
      this.InteractingAboutRecordType = intwithandaboutobj.RecordType;
      this.InteractingAboutvalue = intwithandaboutobj.Id;
      
    }

    if (eventName === "InteractingWithnAbout") {
     this.calledFromMemberSearch = true;
      this.InteractingAboutTypevalue = this.industryPicklistvalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
      this.createIntRecOpenAcc();
      //US4723377
      if(this.multiMemberAuthSwitch){
        this.setAuthValue("");
      }
    }
  }

  parseProviderData(eventName, intwithandaboutobj) {
    let accExtId = intwithandaboutobj.sExtID;
    let selectedrow = {};
    let jsonRecord = this.jsonRecordData;
    const recordFound = true; 
    if (recordFound) {
      selectedrow = intwithandaboutobj;
    }
    let accountRecord = {
      BillingStreet: selectedrow.sAddress,
      Birthdate__c: selectedrow.sBirthdate,
      RecordTypeId: selectedrow.sPend,
      BillingCity: selectedrow.sCity,
      Provider_Classification__c: selectedrow.sClassification,
      DBA__c: selectedrow.sDBA,
      Degree__c: selectedrow.sDegree,
      Enterprise_ID__c: selectedrow.sEnterpriseID,
      Account_External_ID__c: selectedrow.sExtID,
      Individual_First_Name__c: selectedrow.sFirstName,
      Gender__c: selectedrow.sGender,
      Individual_Last_Name__c: selectedrow.sLastName,
      NPI_ID__c: selectedrow.sNPI,
      Phone: selectedrow.sPhone,
      Phone_Ext__c: selectedrow.sPhoneExtn,
      Source_Platform_Code__c: selectedrow.sPlatform,
      BillingPostalCode: selectedrow.sPostalCode,
      ShippingCity: selectedrow.sServiceCity,
      ShippingStatecode: selectedrow.sServiceState,
      ShippingStreet: selectedrow.sServiceaddress,
      shippingPostalCode: selectedrow.sServicezip,
      Description: selectedrow.sSpeciality,
      BillingStatecode: selectedrow.sState,
      Taxonomy_Code__c: selectedrow.sTaxmonycode
    };
    let ids;
    insertProviderAccount({
      consumerIds: selectedrow ? selectedrow.sTaxID : null,
      accountJson: JSON.stringify(accountRecord),
      externalId: accExtId
    })
      .then((result) => {
        ids = result;
        if (
          eventName === "pInteractingWithnAbout" ||
          eventName === "pInteractingWith"
        ) {
          var fName='';
          if(intwithandaboutobj.sFirstName !=null || intwithandaboutobj.sLastName!= null){
            fName = intwithandaboutobj.sFirstName?intwithandaboutobj.sFirstName:'';
            fName = fName!=''?fName+' '+intwithandaboutobj.sLastName:intwithandaboutobj.sLastName;
          }
          fName = fName == ''?intwithandaboutobj.sDBA:fName;
          console.log('fName >>>' +fName);
         
          this.IntWithLkpValue= fName;
          this.IntWithLkpValue1 = ids;
          this.template
            .querySelector('[data-id="IntWithDID"]')
            .setIntNumLkpValue(this.IntWithLkpValue, this.IntWithLkpValue1);
          this.intWithRecordId = this.IntWithLkpValue1;
		   this.intWithName = this.IntWithLkpValue;
          this.industryPicklistvalue = intwithandaboutobj.sPend
            .split(" ")
            .join("-");
        }
        if (
          eventName === "pInteractingWithnAbout" ||
          eventName === "pInteractingAbout"
        ) {
          let tempintaboutvalue = "";
          tempintaboutvalue = ids;
          let temparr = [];
          temparr = this.InteractingAboutPicklistvalue;
          var fName='';
          if(intwithandaboutobj.sFirstName !=null || intwithandaboutobj.sLastName!= null){
            fName = intwithandaboutobj.sFirstName?intwithandaboutobj.sFirstName:'';
            fName = fName!=''?fName+' '+intwithandaboutobj.sLastName:intwithandaboutobj.sLastName;
          }
          fName = fName == ''?intwithandaboutobj.sDBA:fName;
          //US4723377
          if(this.multiMemberAuthSwitch){
            if(this.InteractingAboutvalue != tempintaboutvalue){
              this.InteractingAboutvalue = tempintaboutvalue;
              this.setAuthValue("");
            }
          }

          if (temparr.find((el) => el.value === tempintaboutvalue)) {
            this.InteractingAboutPicklistvalue = [
              ...this.InteractingAboutPicklistvalue
            ];
          } else {
            this.InteractingAboutPicklistvalue = [
              ...this.InteractingAboutPicklistvalue,
              {
                label:
                fName,
                value: tempintaboutvalue
              }
            ];
          }
          if(this.template.querySelector(
            '[data-id="InteractingAbout"]'
          ) != null){
          let interactingAboutPik = this.template.querySelector(
            '[data-id="InteractingAbout"]'
          );
          interactingAboutPik.value = tempintaboutvalue;
		  }

          this.InteractingAboutTypevalue = intwithandaboutobj.sPend
            .split(" ")
            .join("-");
          this.InteractingAboutRecordType = intwithandaboutobj.sPend;
          this.InteractingAboutvalue = tempintaboutvalue;

          if (eventName === "pInteractingWithnAbout") {
            this.InteractingAboutTypevalue = this.industryPicklistvalue =
              intwithandaboutobj.sPend.split(" ").join("-");
            this.createIntRecOpenAcc();
            //US4723377
            if(this.multiMemberAuthSwitch){
              this.setAuthValue("");
            }
          }
        }
      })
      .catch((error) => {
        console.log("Error Occured", error);
      });
  }

  parseGroupData(eventName, intwithandaboutobj) {
    if (
      eventName === "gInteractingWithnAbout" ||
      eventName === "gInteractingWith"
    ) {
      this.IntWithLkpValue1 = intwithandaboutobj.Id;
      this.IntWithLkpValue = intwithandaboutobj.Name;
      this.groupIsInteractingWith = true;

      this.template
        .querySelector('[data-id="IntWithDID"]')
        .setIntNumLkpValue(this.IntWithLkpValue, this.IntWithLkpValue1);
      this.intWithRecordId = this.IntWithLkpValue1;
	  this.intWithName = this.IntWithLkpValue;
      this.industryPicklistvalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
    }

    if (
      eventName === "gInteractingWithnAbout" ||
      eventName === "gInteractingAbout"
    ) {
      let tempintaboutvalue = intwithandaboutobj.Id;
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;

      //US4723377
      if(this.multiMemberAuthSwitch){
        if(this.InteractingAboutvalue != tempintaboutvalue){
          this.InteractingAboutvalue = tempintaboutvalue;
          this.setAuthValue("");
        }
      }

      this.groupIsInteractingWith = true;
      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue
        ];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label: intwithandaboutobj.Name,
            value: intwithandaboutobj.Id
          }
        ];
      }
      if(this.template.querySelector(
        '[data-id="InteractingAbout"]'
      ) != null){
      let interactingAboutPik = this.template.querySelector(
        '[data-id="InteractingAbout"]'
      );
      interactingAboutPik.value = intwithandaboutobj.Id;
	  }

      this.InteractingAboutTypevalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
      this.InteractingAboutRecordType = intwithandaboutobj.RecordType;
      this.InteractingAboutvalue = intwithandaboutobj.Id;
    }

    if (eventName === "gInteractingWithnAbout") {
      this.InteractingAboutTypevalue = this.industryPicklistvalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
      this.createIntRecOpenAcc();

      //US4723377
      if(this.multiMemberAuthSwitch){
        this.setAuthValue("");
      }
    }
  }

  navigateToAccountBool = false;
  async createIntRecOpenAcc() {
    let result;
    try {
      result = await this.handleSaveButton();
      this.navigateToAccountBool = true;
    } catch (ex) {
      console.log("Error " + ex);
    }
  }

  renderedCallback() {
    this.saveRender = this.buttonRenderLogicSave();
	this.displayTogglebutton();
    if (this.isAccountPage) {
      this.saveNContbtnRender = false;
      this.genesysUserBtn = true;
      this.validateCRMSwitch = false;
      this.mf3Switch = true;
    } else if (!this.isAccountPage) {
      this.saveNContbtnRender = this.buttonRenderLogicsaveNCont();
      this.genesysUserBtn = this.genesysUser;
    }
    if(!this.isAccountPage)
    {
		if(this.template.querySelector(
        '[data-id="InteractingAboutType"]')!=null){
      const interactingAboutPik = this.template.querySelector(
        '[data-id="InteractingAboutType"]').value;
      if(interactingAboutPik !== "Unknown-Agent"){
        this.disableIntAbout = false
      }
		}
    }
  }

  @track c__interactionId;
  // pubsub
  @wire(CurrentPageReference) pageRef;

  subscription = null;
  @wire(MessageContext)
  messageContext;
  subscribeToMessageChannel() {
    if (!this.subscription) {
      this.subscription = subscribe(
        this.messageContext,
        ICLMS,
        (message) => this.handleIntLMSMessage(message),
        { scope: APPLICATION_SCOPE }
      );
    }
  }
   
   publishLMSChannel(){
    const msgPayload = {
      authcheckValue: this.authcheckValue,
      authMessage: this.authMessage,
      authMsgStyle: this.authMsgStyle,
      isActionMsgShow: this.isActionMsgShow,
      disabledtoggle: this.disabledtoggle
    };
    publish(this.messageContext, refreshIntLogLMS, msgPayload);   
 }
 subscribeToLMSChannel() {
  if (!this.subscription) {
    this.subscription = subscribe(
      this.messageContext,
      refreshIntLogLMS,
      (message) => this.handleRefreshIntLMSMessage(message),
      { scope: APPLICATION_SCOPE }
    );
  }
}
handleRefreshIntLMSMessage(message) {
  if(message){
      this.authcheckValue = message?.authcheckValue;
      this.authMessage=message?.authMessage;
      this.authMsgStyle=message?.authMsgStyle;
      this.isActionMsgShow= message?.isActionMsgShow;
      this.disabledtoggle=message?.disabledtoggle;
  }
}

  disconnectedCallback() {
    unsubscribe(this.subscription);
    this.subscription = null;
  }

  handleIntLMSMessage(message) {}

  @wire(getObjectInfo, { objectApiName: INTERACTION_OBJECT })
  accountMetadata;

  @wire(getObjectInfo, { objectApiName: INTERACTION_MEMBER_OBJECT })
  interactionMemberMetadata;

  @wire(getPicklistValues, {
    recordTypeId: "$accountMetadata.data.defaultRecordTypeId",
    fieldApiName: INTERACTING_WITH_TPE
  })
  industryPicklist({ error, data }) {
    let pikValParam = "industryPicklist";
    this.addNone(data, error, pikValParam);
  }

  @wire(getPicklistValues, {
    recordTypeId: "$accountMetadata.data.defaultRecordTypeId",
    fieldApiName: INTERACTION_ORIGIN
  })
  originPicklist({ error, data }) {
    let pikValParam = "originPicklist";
    this.addNone(data, error, pikValParam);
  }

  @wire(getPicklistValues, {
    recordTypeId: "$interactionMemberMetadata.data.defaultRecordTypeId",
    fieldApiName: INTERACTING_ABOUT_TYPE
  })
  interactingAboutPicklist({ error, data }) {
    let pikValParam = "interactingAboutPicklist";
    this.addNone(data, error, pikValParam);
  }

  @wire(getRecord, {
    recordId: USER_ID,
      fields: [CallCenter_FIELD, NETWORK_ID_FIELD, PROFILE_NAME_FIELD]
  })
  wireuser({ error, data }) {
    if (error) {
    } else if (data) {
        this.genesysUser = data.fields.CallCenterId.value;
        this.networkId = data.fields.Network_User_Id__c.value;
        this.profileName = data.fields.Profile.value.fields.Name.value;
    }
  }

  @wire(initializeVariables)
  getInitialObjs({ error, data }) {
    if (data) {
      this.validateCRMSwitch = data[0];
      // ad-hoc disabling mf3switch
      this.mf3Switch = data[1];
      this.Switch_2230000 = data[2];
      this.multiMemberAuthSwitch = data[3];
      this.callerTypePopulationSwitch = data[4];//Added for US#4579434
      this.refreshIntLogLMSSwitch = data[5];//Added for US#4890793
      this.error = undefined;
    } else if (error) {
      this.error = error;
      this.contacts = undefined;
    }
  }

  addNone(data, error, pikValParam) {
    if (data) {
      this.fieldPikValues = data.values;
      var listViewData = [];
      for (var i = 0; i < this.fieldPikValues.length; i++) {
        listViewData.push({
          attributes: null,
          label: this.fieldPikValues[i].label,
          validFor: Array(0),
          value: this.fieldPikValues[i].value
        });
      }
      listViewData.unshift({
        attributes: null,
        label: "--None--",
        validFor: Array(0),
        value: "none"
      });
      if (pikValParam === "industryPicklist") {
        this.interWithTypePikLst = listViewData;
      }
      if (pikValParam === "originPicklist") {
        this.originPikLst = listViewData;
      }
      if (pikValParam === "interactingAboutPicklist") {
        this.interactingAboutTypePikLst = listViewData;
      }
      this.error = undefined;
    } else if (error) {
      this.error = error;
      this.contacts = undefined;
    }
  }

  get interWithTypePikLst() {
    return this.interWithTypePikLst;
  }

  get interWithTypePikLst() {
    return this.originPikLst;
  }

  get interWithTypePikLst() {
    return this.interactingAboutTypePikLst;
  }

  // --> Handle UI Changes
  @api firingTabId;
  @api firingtabParentid;
  @api subtabs;
  @track handlechangeobj;

  async fireEventToAura() {
    let firingTabId;
    let firingtabParentid;
    let subtabs;
    await invokeWorkspaceAPI("getFocusedTabInfo").then((primaryFocusTab) => {
      if (
        primaryFocusTab.tabId != undefined &&
        primaryFocusTab.recordId != undefined
      ) {
        firingTabId = primaryFocusTab.tabId;
        firingtabParentid = primaryFocusTab.parentTabId;
        subtabs = primaryFocusTab.subtabs;
      }
      const message = {
        handlechangeobj: this.handlechangeobj,
        firingTabId: firingTabId,
        firingtabParentid: firingtabParentid,
        subtabs: subtabs
      };
      publish(this.messageContext, ICLMS, message);
    });
  }

  handleChange(event) {
    this.intAboutCurSelValue = event.detail.value;

    //US4723377:
    if(this.multiMemberAuthSwitch )
    {
      if(this.intAboutCurSelValue === 'none') {
        this.searchvalue = false;
        this.handlevalidateInteractionFields();
      }
    }

    if (this.isAccountPage) {
      this.handlechangeobj = [
        { key: event.target.name, value: event.detail.value }
      ];
      this.fireEventToAura();
    }
    if (event.target.name === "InteractionOrigin") {
      this.InteractionOriginvalue = event.detail.value;
      if (this.InteractionOriginvalue === "none") {
        this.InteractionOriginvalue = null;
      } else {
        return this.InteractionOriginvalue;
      }
    }
    if (event.target.name === "InteractingAboutType") {
      this.InteractingAboutTypevalue = event.detail.value;
      if (this.InteractingAboutTypevalue === "none") {
        this.InteractingAboutTypevalue = null;
      } else {
        return this.InteractingAboutTypevalue;
      }
    }
    if (event.target.name === "InteractingAbout") {
      this.InteractingAboutvalue = event.detail.value;
      if (this.InteractingAboutvalue === "none") {
        this.InteractingAboutvalue = null;
        this.InteractingAboutRecordType = null;
      } else {
        this.InteractingAboutvalue = this.InteractingAboutvalue;
        this.interactingAboutId = this.InteractingAboutvalue; // Added for US#4951430
        getIntWithRecType({
          accRecordId: this.InteractingAboutvalue
        })
          .then((result) => {
            this.InteractingAboutRecordType = result;
            let iabtv = result.split(" ").join("-");
            if (result === "Unknown Agent/Broker") {
              iabtv = "Unknown-Agent";
            }
            this.InteractingAboutTypevalue = iabtv;
          })
          .catch((error) => {
            this.error = error;
          });

          if(this.multiMemberAuthSwitch){
            this.setAuthValue(event.target.name);
          }
          else{
            if(this.bIntCreationStatus && !this.isAccountPage && this.InteractingAboutvalue) {
              this.navigateToViewAccountPage();
              this.navigateToAccountBool = false;
            }
        }
        return this.InteractingAboutvalue;
      }      
    }
    if (event.target.name === "Interactingwithtype") {
      this.industryPicklistvalue = event.detail.value;
      if (this.industryPicklistvalue === "none") {
        this.industryPicklistvalue = null;
      } else {
        return this.industryPicklistvalue;
      }
    }
    if (event.target.name === "firstNameinput") {
      this.firstName = event.detail.value;
      if (this.firstName.length > 149) {
        this.errorMessage =
          "The interaction name must not exceed 150 characters";
        this.pageMessages = true;
      } else {
        this.pageMessages = false;
      }
    }
    if (event.target.name === "radioGroup") {
      this.interactingAboutRadioValue = event.detail.value;
      if (
        !this.interactingAboutRadioValue ||
        this.interactingAboutRadioValue !== ""
      ) {
        this.radioErrorMessage = "";
        this.radioErrorMessages = false;
      }
    }
  }

  handleMaximizeBtn() {
	this.minimizeWindow = false;
    this.maximizeWindow = true;
    if (this.isAccountPage) {
      const passEventr = new CustomEvent("refreshcasevar", {
        detail: {}
      });
      this.dispatchEvent(passEventr);
      this.cssClass =
        "slds-utility-bar_container slds-utility-bar container-lip-2";
    } else if (!this.isAccountPage) {
      this.cssClass =
        "slds-utility-bar_container slds-utility-bar container-lip-2";
    }
    this.cssClass1 = "container-lip-6";
    this.cssClass2 =
      "slds-m-around_medium slds-m-bottom_medium slds-clearfix card-buttons-lip";
    this.cssClass3 = "";
	if(this.template
      .querySelector('[data-id="IntWithDID"]') != null){
        this.template
        .querySelector('[data-id="IntWithDID"]')
        .setIntNumLkpValue(this.intWithName, this.intWithRecordId);
      }
  }

  handleMinimizeBtn() {
	this.minimizeWindow = true;
    this.maximizeWindow = false;
    if (this.isAccountPage) {
      this.cssClass =
        "slds-utility-bar_container slds-utility-bar container-lip-3";
    } else if (!this.isAccountPage) {
      this.cssClass =
        "slds-utility-bar_container slds-utility-bar container-lip-3";
    }
    this.cssClass1 = "container-lip-4";
    this.cssClass2 = "container-lip-4";
    this.cssClass3 = "container-lip-5";
  }

  @track getInteractionBtnFlag = false;
  onIntNumSelection(event) {
    this.getInteractionBtnFlag = false;
    this.intNumName = event.detail.selectedValue;
    this.intNumRecordId = event.detail.selectedRecordId;
    let strCallerFn = "onIntNumSelection";
    if (!this.intNumRecordId || this.intNumRecordId == "") {
      this.intNumRecordId = null;
      this.interactionrecordId = null;
      this.intMemberExists = false;
	  this.isShowAuthSol = false;
    }
    if (this.intNumRecordId) {
      this.handleGetInteractionBtn(strCallerFn);
    }
    if (this.intNumRecordId || this.intNumName) {
      if (this.isAccountPage) {
        this.bShowUpdate = false;
      } else if (!this.isAccountPage) {
        this.bShowUpdate = true;
      }
    } else {
      this.bShowUpdate = false;
      this.bIntCreationStatus = false;
    }
  }

  onIntWithSelection(event) {
    this.intWithName = event.detail.selectedValue;
    this.intWithRecordId = event.detail.selectedRecordId;
    this.handlechangeobj = [
      {
        key: "IntWithLkp",
        value: this.intWithRecordId,
        label: this.intWithName
      }
    ];
    this.fireEventToAura();
  }

  closeModal() {
    this.isModalOpen = false;
  }
  closeModal2() {
    this.cShowModal = false;
    this.interactingAboutRadioValue = "";
    this.InteractionMemberList = [];
  }

  submitDetails() {
    this.isModalOpen = false;
  }

  handleLkp(event) {
    this.template
      .querySelector("c-humlookup-search")
      .handleLookupModal(
        event.detail.interactingWith,
        event.detail.objectName,
        event.detail.searchKey
      );
  }

  // --> Clear field values and variable nullification

  handleResetAll() {
    this.template.querySelectorAll("lightning-combobox").forEach((element) => {
      element.value = null;
    });
    this.template.querySelectorAll("lightning-input").forEach((element) => {
      element.value = null;
    });
    this.template
      .querySelectorAll("c-interaction-cmp-lookup")
      .forEach((element) => {
        element.changeMessage();
      });
    this.firstName = null;
    if (!this.isSearchBtnUsed) {
      this.InteractionOriginvalue = null;
    } else if (this.isSearchBtnUsed) {
      let intorigin = this.template.querySelector('[data-id="intOrigin"]');
      intorigin.value = this.InteractionOriginvalue;
    }
    this.industryPicklistvalue = null;
    this.intWithRecordId = null;
    this.intNumRecordId = null;
    this.InteractingAboutvalue = null;
    this.InteractingAboutTypevalue = null;
    this.IntNumLkpValue = null;
    this.IntWithLkpValue = null;
    this.IntNumLkpValue1 = null;
    this.IntWithLkpValue1 = null;
    this.memberCreated = false;
    this.triggeredEvent = '';
    this.objAccData = null;
    this.intAboutAccName = '';
    this.InteractingAboutRecordType = null;
    this.groupIsInteractingAbout = false;
    this.groupIsInteractingWith = false;
    this.calledFromMemberSearch = false;
	  this.isDisabled = false;
	if(this.template.querySelector(
      '[data-id="InteractingAbout"]'
    ) != null){
    const interactingAboutPik = this.template.querySelector(
      '[data-id="InteractingAbout"]'
    );
    interactingAboutPik.value = null;
	}
	
	const intNumbField = this.template
    .querySelector('[data-id="IntNumDID"]');
    if(intNumbField) intNumbField.enableDisableElement(false);
    this.bShowUpdate = false;
    this.InteractingAboutPicklistvalue = [{ label: "--None--", value: "none" }];
    this.InteractionMemberList = [];
    this.interactingAboutRadioValue = null;
    this.interactionrecordId = null;
    this.intMemberExists = false;
    this.caseInteractionrecordId = null;
    this.interactionNameAcc = null;
    this.counterIntAboutOpen = 0;
    this.bIntCreationStatus = false;
	this.isShowAuthSol = false;
	this.resetToggleVariables();
  }

  validateInput() {
    let isValid = false;
    let InteractingAboutTypevalue = this.InteractingAboutTypevalue;
    if (InteractingAboutTypevalue) {
      InteractingAboutTypevalue =
        this.InteractingAboutTypevalue.split("-").join(" ");
    } else {
      InteractingAboutTypevalue = this.InteractingAboutTypevalue;
    }
    if (
      this.isMemberFieldsFilled() &&
      this.InteractingAboutRecordType === InteractingAboutTypevalue
    ) {
      isValid = true;
    } else {
      isValid = false;
      this.dispatchEvent(
        new ShowToastEvent({
          title: "Error",
          message:
            "The account type entered into the Interacting About Type field does not match the person/entity selected in the Interacting About field. Please update one of the fields to resolve this error.",
          variant: "error",
          mode: "sticky"
        })
      );
    }
    return isValid;
  }

  isMemberFieldsFilled() {
    let isValid =
      this.InteractingAboutvalue || this.InteractingAboutTypevalue
        ? true
        : false;
    return isValid;
  }

  handleSavePreLogic() {
    if(this.multiMemberAuthSwitch){

      if(!this.authcheckValue){
        this.authcheckValue= false;
      }
      //User Story 4725390
      if(this.authcheckValue == false ||(this.authcheckValue == true
        && (this.intWithRecordId || this.firstName) && this.InteractingAboutvalue)){
          this.handleSavePreLogicDetail();
        }
        else{
          this.handlevalidateInteractionFields();
        }
    }
    else{
      this.handleSavePreLogicDetail();
    }
  }

  handlevalidateInteractionFields(){
    this.searchCmp = this.template.querySelector(".interaction-dropdown");
    this.searchvalue = this.searchCmp.value;
    this.searchIntWith = this.template.querySelector('[data-id="IntWithDID"]');
    this.searchIntWithValue = this.searchIntWith.value;
      if (((!this.searchvalue || this.searchvalue == null || this.intAboutCurSelValue ==='none') ||
      ((!this.searchIntWithValue|| this.searchIntWithValue == null)) || !this.firstName)
      && this.authcheckValue == true) {
        this.dispatchEvent(
          new ShowToastEvent({
            title: HUMINTERACTIONVALIDATION,
            message: "",
            variant: "error",
          })
        );
        this.resetToggleVariables();
      }
    }

  handleSavePreLogicDetail(){
    const interactionRecValue = this.intNumRecordId ? this.intNumRecordId : "";
    if (interactionRecValue || interactionRecValue !== "") {
      if (!this.isMemberFieldsFilled()) {
        this.updateInteractionRecord(this.memberCreated);
      } else if (this.isMemberFieldsFilled) {
        if (this.validateInput())
          this.updateInteractionRecord(this.memberCreated);
      }
    } else if (!interactionRecValue || interactionRecValue === "") {
      if (!this.isMemberFieldsFilled()) {
        this.handleCreateInteractionRecords(this.memberCreated);
      } else if (this.isMemberFieldsFilled && this.validateInput()) {
        this.handleCreateInteractionRecords(this.memberCreated);
      }
    }
    //Added for US#4579434
    setTimeout(()=>{
      if(this.callerTypePopulationSwitch && !this.hasError){
        this.sendInteractingWithValue();
      }
    },500);
    
  }

  handleSaveButton(strCallerFn) {
    const methodName = "save";
    this.memberCreated = false;
    this.showPasswordPopup(methodName);
    if(this.featureSwitchMyAH && hasMyAHAccess) {
      this.sendInteractionMyAH( {msgType: MyAH_LMS_toInteractionLog_Label} );
    }
  }
  
   handleSaveandContinueButton(strCallerFn) {
    const methodName = "saveAndContinue";
    this.memberCreated = false;
    this.showPasswordPopup(methodName);
    if(this.featureSwitchMyAH && hasMyAHAccess) {
      this.sendInteractionMyAH( {msgType: MyAH_LMS_toInteractionLog_Label} );
    }
  }

  handleSaveAndNewLogBtn() {
    const methodName = "saveUpdate";
    this.memberCreated = true;
    this.showPasswordPopup(methodName);
  }

  handleCreateInteractionRecords(strRecs) {
    this.hasError = false;//Added for US#4579434
    const fields = {};
    fields[INTERACTING_WITH_NAME.fieldApiName] = this.firstName;
    fields[INTERACTION_ORIGIN.fieldApiName] = this.InteractionOriginvalue;
    fields[INTERACTING_WITH.fieldApiName] = this.intWithRecordId;
    fields[INTERACTING_WITH_TPE.fieldApiName] = this.industryPicklistvalue;
    fields[INTERACTION_AUTHENTICATED.fieldApiName] = this.authcheckValue?this.authcheckValue:false;//US 4141940
    fields.sobjectType= INTERACTION_OBJECT.objectApiName;

    handleRecordDml({objData:fields,action:'insert',objApiName:INTERACTION_OBJECT.objectApiName})
      .then((interaction) => {
        this.interactionrecordId = this.intNumRecordId = interaction.Id;
        this.interactionNameAcc = this.intNumName = interaction.Name;
		if(this.authcheckValue){
          this.disabledtoggle = true;
        }else{
          this.disabledtoggle = false;
        }
        if (!this.isAccountPage) {
          this.template
            .querySelector('[data-id="IntNumDID"]')
            .setIntNumLkpValue(interaction.Name, interaction.Id);
        }
        this.dispatchEvent(
          new ShowToastEvent({
            title: "Success",
            message:
              "Interaction " + interaction.Name + " created.",
            variant: "success"
          })
        );
      })
      .then(() => {
        // ad-hoc 1 --> calling interaction member creation fnction only if InteractingAboutTypevalue is not null
        this.createInteractionMemberPreLogic(strRecs);
        if (this.handleAssCaseFlag) {
          this.createCaseInteractionRecord();
          this.handleAssCaseFlag = false;
        }
      })

      .then((interaction) => {
        if (!this.isAccountPage) {
          this.bShowUpdate = true;
          this.buttonRenderLogicsaveNCont();
        }
        this.buttonsRenderLogic();
        if (this.isAccountPage) {
          this.handlechangeobj = [
            {
              key: "IntNumInp",
              value: this.interactionNameAcc,
              recordId: this.interactionrecordId
            }
          ];
          this.fireEventToAura();
        }
      })

      .then((interaction) => { 
        this.bIntCreationStatus = true;
        if (this.navigateToAccountBool) {
          if(this.bIntCreationStatus && !this.isAccountPage) {
            if(!this.isLocked){
              this.navigateToViewAccountPage();
            }else{
              this.handleNavigatetoProtectedPage();
            }
            this.navigateToAccountBool = false;
          }
        }
      })

      .catch((error) => {
        this.hasError = true;//Added for US#4579434
        this.handleAssCaseFlag = false;
        let errors = error?.body?.pageErrors;
        if(this.multiMemberAuthSwitch){
          this.resetToggleVariables();
        }
        errors
          ? errors.map((each) => {
              this.dispatchEvent(
                new ShowToastEvent({
                  title: "Error creating record",
                  message: each.message,
                  variant: "error",
                  mode: "sticky"
                })
              );
            })
          : "";
      });
  }
   navigatetoProtectedPage(){
    let state= this.intNumRecordId ? {
      c__accountDetailId: this.intAboutAccName,
      c__dataList: this.InteractingAboutvalue,
      c__tabdetails: 'protectedAccount',
      c__interactionId : this.intNumRecordId
    } : 
    {
      c__accountDetailId: this.intAboutAccName,
      c__dataList: this.InteractingAboutvalue,
      c__tabdetails: 'protectedAccount'
    };
    opentab('c__SearchEnrollmentDetails_CMP_HUM',state,true);
  }
 handleNavigatetoProtectedPage() {
    let cmpName = 'c__SearchEnrollmentDetails_CMP_HUM';
    var tabdata;
    
    invokeWorkspaceAPI('getAllTabInfo').then(function (response) {
          for (let key in response) {
            if (response[key].url.includes(cmpName)) {
              tabdata = response[key];
              break;
            }
          }
          if(tabdata){
          invokeWorkspaceAPI('closeTab',{tabId :tabdata.tabId}).then(function (response) {
            tabdata={};
          });
          }
        }).then((response)=>{
            this.navigatetoProtectedPage();

        }).catch((error) => {
          console.log('##--recordInput in Navigation:error '+JSON.stringify(error));
        });
      
    }

    createInteractionMember(strRecs) {
      const fields = {};
      fields[INTERACTING_ABOUT.fieldApiName] = this.InteractingAboutvalue;
      fields[INTERACTING_ABOUT_TYPE.fieldApiName] =
        this.InteractingAboutTypevalue;
      fields[INTERACTING_INTERACTION.fieldApiName] = this.interactionrecordId;
      if(this.multiMemberAuthSwitch){
        fields[INTERACTING_ABOUT_AUTHENTICATED.fieldApiName] = this.authcheckValue?this.authcheckValue:false;
      }
      fields.sobjectType= INTERACTION_MEMBER_OBJECT.objectApiName;

      handleRecordDml({objData:fields,action:'insert',objApiName:INTERACTION_MEMBER_OBJECT.objectApiName})
        .then((interactionMember) => {
          this.interactionMemberrecordId = interactionMember.Id;
        })
        .then((interactionMember) => {
          if(this.InteractingAboutvalue && !this.isAccountPage) {
            this.bIntCreationStatus = true;
          }
          if (strRecs === true) {
            this.handleResetAll();
            if (this.isAccountPage) {
              this.handlechangeobj = [
                {
                  key: "clear",
                  value: "clear"
                }
              ];
              this.fireEventToAura();
            }
          }
          this.intMemberExists = false;
        })
        .catch((error) => {
          let errors = error?.body?.pageErrors;
          errors
            ? errors.map((each) => {
                this.dispatchEvent(
                  new ShowToastEvent({
                    title: "Error creating record",
                    message: each.message,
                    variant: "error",
                    mode: "sticky"
                  })
                );
              })
            : "";
        });
    }


  updateInteractionMember(strRecs ) {
    const fields = {};
    fields[INTERACTING_ABOUT.fieldApiName] = this.InteractingAboutvalue;
    fields[INTERACTING_ABOUT_TYPE.fieldApiName] = this.InteractingAboutTypevalue;
    fields[INTERACTING_INTERACTION.fieldApiName] = this.interactionrecordId;
    fields.sobjectType= INTERACTION_MEMBER_OBJECT.objectApiName;

    if(this.multiMemberAuthSwitch){
      fields[INTERACTING_ABOUT_AUTHENTICATED.fieldApiName] = this.intMemberAuthUpdated;
    }
      fields[INTERACTING_MEMEBR_ID.fieldApiName] = this.intaboutid;

    handleRecordDml({objData:fields,action:'update',objApiName:INTERACTION_MEMBER_OBJECT.objectApiName})
      .then((interactionMember) => {
        this.interactionMemberrecordId = interactionMember.Id;

        if(this.InteractingAboutvalue && !this.isAccountPage) {
          this.bIntCreationStatus = true;
        }
        if (strRecs === true) {
          this.handleResetAll();
          if (this.isAccountPage) {
            this.handlechangeobj = [
              {
                key: "clear",
                value: "clear"
              }
            ];
            this.fireEventToAura();
          }
        }
        this.intMemberExists = false;
      })
      .then((interactionMember) => {
        if(this.InteractingAboutvalue && !this.isAccountPage) {
          this.bIntCreationStatus = true;
        }
        if (strRecs === true) {
          this.handleResetAll();
          if (this.isAccountPage) {
            this.handlechangeobj = [
              {
                key: "clear",
                value: "clear"
              }
            ];
            this.fireEventToAura();
          }
        }
        this.intMemberExists = false;
      })
      .catch((error) => {
        let errors = error?.body?.pageErrors;
        errors
          ? errors.map((each) => {
              this.dispatchEvent(
                new ShowToastEvent({
                  title: "Error creating record",
                  message: each.message,
                  variant: "error",
                  mode: "sticky"
                })
              );
            })
          : "";
      });
  }

  updateInteractionRecord(strRecs) {
    this.hasError = false;//Added for US#4579434 
    const fields = {};
    fields[INTERACTION_NUMBER.fieldApiName] = this.intNumRecordId;
    fields[INTERACTING_WITH_NAME.fieldApiName] = this.firstName;
    fields[INTERACTION_ORIGIN.fieldApiName] = this.InteractionOriginvalue;
    fields[INTERACTING_WITH.fieldApiName] = this.intWithRecordId;
    fields[INTERACTING_WITH_TPE.fieldApiName] = this.industryPicklistvalue;
    if(!this.multiMemberAuthSwitch){
    fields[INTERACTION_AUTHENTICATED.fieldApiName] = this.authcheckValue;
    }
    else if(this.multiMemberAuthSwitch && this.authcheckValue){
      fields[INTERACTION_AUTHENTICATED.fieldApiName] = this.authcheckValue;
    }
    fields.sobjectType= INTERACTION_OBJECT.objectApiName;
    if (this.InteractionOriginvalue === "none") {
      this.InteractionOriginvalue = null;
    }
    
    handleRecordDml({objData:fields,action:'update',objApiName:INTERACTION_OBJECT.objectApiName})
      .then((interaction) => {
        this.interactionrecordId = interaction.Id;
		if(this.authcheckValue){
          this.disabledtoggle = true;
		  if(this.InteractionOriginvalue === "Inbound Call" && this.genesysUser){
            this.publishLMSChannel();
          }
        }else{
          this.disabledtoggle = false;
        }
        if (this.handleAssCaseFlag) {
          this.createCaseInteractionRecord();
          this.handleAssCaseFlag = false;
        }
        this.interactionNameAcc = interaction.Name;
        if (!this.isAccountPage) {
          this.template
            .querySelector('[data-id="IntNumDID"]')
            .setIntNumLkpValue(interaction.Name, interaction.Id);
        }
        this.dispatchEvent(
          new ShowToastEvent({
            title: "Success",
            message:
              "Interaction " + interaction.Name + " updated.",
            variant: "success"
          })
        );
      })
      .then(() => {
        if (!this.isAccountPage) {
          this.bShowUpdate = true;
          this.buttonRenderLogicsaveNCont();
        }
        this.createInteractionMemberPreLogic(strRecs);
        if (this.isAccountPage) {
          this.handlechangeobj = [
            {
              key: "IntNumInp",
              value: this.interactionNameAcc,
              recordId: this.interactionrecordId
            }
          ];
          this.fireEventToAura();
        }
      })

      .then(() => {
        this.bIntCreationStatus = true;
        if (this.navigateToAccountBool) {
          if(this.bIntCreationStatus && !this.isAccountPage) {
            if(!this.isLocked){
              this.navigateToViewAccountPage();
              }else{
                this.handleNavigatetoProtectedPage();
            }
          this.navigateToAccountBool = false; 
          }
        }
      })

      .catch((error) => {
        this.hasError = true;//Added for US#4579434
        this.handleAssCaseFlag = false;
        let errors = error?.body?.pageErrors;
        if(this.multiMemberAuthSwitch){
          this.resetToggleVariables();
        }
        errors
          ? errors.map((each) => {
              this.dispatchEvent(
                new ShowToastEvent({
                  title: "Error creating record",
                  message: each.message,
                  variant: "error",
                  mode: "sticky"
                })
              );
            })
          : "";
      });
  }

  createCaseInteractionRecord(strRecs) {
    const fields = {};
    fields[CASEID_FIELD.fieldApiName] = this.caseRecordId
      ? this.caseRecordId
      : "";
    fields[INTERACTIONID_FIELD.fieldApiName] = this.interactionrecordId;
    fields.sobjectType= CASE_INTERACTION_OBJECT.objectApiName;
    const caseInteractionrecordInput = {
      apiName: CASE_INTERACTION_OBJECT.objectApiName,
      fields
    };
    handleRecordDml({objData:fields,action:'insert',objApiName:CASE_INTERACTION_OBJECT.objectApiName})
      .then((caseInteraction) => {
        this.caseInteractionrecordId = caseInteraction.Id;
        this.dispatchEvent(
          new ShowToastEvent({
            title: "Success",
            message: "Case and Interaction Association successfull.",
            variant: "success"
          })
        );
      })
      .then((caseInteraction) => {
        if (strRecs === true) {
          this.handleResetAll();
          if (this.isAccountPage) {
            this.handlechangeobj = [
              {
                key: "clear",
                value: "clear"
              }
            ];
            this.fireEventToAura();
          }
        }
      })
      .catch((error) => {
        let errors = error?.body?.pageErrors;
        errors
          ? errors.map((each) => {
              this.dispatchEvent(
                new ShowToastEvent({
                  title: "Error creating record",
                  message: each.message,
                  variant: "error",
                  mode: "sticky"
                })
              );
            })
          : "";
      });
  }

  deleteInteractionMember(strRecs) {
    if (
      !this.interactingAboutRadioValue ||
      this.interactingAboutRadioValue === ""
    ) {
      this.radioErrorMessage = "Please select an Interacting about.";
      this.radioErrorMessages = true;
    } else if (this.interactingAboutRadioValue) {
      deleteRecord(this.interactingAboutRadioValue)
        .then(() => {
          this.dispatchEvent(
            new ShowToastEvent({
              title: "Success",
              message: "Record deleted",
              variant: "success"
            })
          );
          this.closeModal2();
          this.handleUpdateBtn("deleteCalled");
        })
        .catch((error) => {
          this.dispatchEvent(
            new ShowToastEvent({
              title: "Error deleting record",
              message: error.body.message,
              variant: "error"
            })
          );
        });
    }
  }
  
   @api
  fetchInteractionDetails(objInteraction){
    if(objInteraction.bOnActiveCall) this.handleMaximizeBtn();
    if(objInteraction.sIntractionId) this.getInteractionDetailsById(objInteraction.sIntractionId)
  }
  
   getInteractionDetailsById(sInteractionId){
    getInteractionList({ recordId: sInteractionId })
  .then((result) => {
    this.displayInteractionDetails(result);
  })
  .catch((error) => {
  console.log('error----',error);
    this.error = error;
    this.dispatchEvent(
      new ShowToastEvent({
        title: "Warning",
        message: "No records found",
        variant: "warning"
      })
    );
  });
  }
  
  displayInteractionDetails(result){
    if(result){
    this.Interaction = result;
    if (this.Interaction.Id && this.Interaction.Name) {
      this.IntNumLkpValue1 = this.Interaction.Id;
      this.intNumRecordId = this.IntNumLkpValue1;

      //Added for US#4890793
      if(this.refreshIntLogLMSSwitch){
        this.interactionrecordId =this.intNumRecordId;
      }
      
      this.IntNumLkpValue = this.Interaction.Name;
      this.intNumName = this.Interaction?.Name ?? '';
      this.isDisabled = (this.genesysUser && this.Interaction.Interaction_Origin__c == "Inbound Call")? true:false;

      if (this.isAccountPage) {
        this.interactionNameAcc = this.IntNumLkpValue;
      } else if (!this.isAccountPage) {
        const idField = this.template.querySelector('[data-id="IntNumDID"]');
        if(idField) {
          idField.setIntNumLkpValue(this.IntNumLkpValue, this.IntNumLkpValue1);
          idField.enableDisableElement(this.isDisabled);
        } 
      }
    }
    if (this.Interaction.Interaction_Origin__c)
      this.InteractionOriginvalue = this.Interaction.Interaction_Origin__c;
    
    this.firstName = this.Interaction.Caller_Name__c?this.Interaction.Caller_Name__c:'';

        this.IntWithLkpValue1 = this.Interaction?.Interacting_With__c ?? '';
        this.IntWithLkpValue = this.Interaction?.Interacting_With__r?.Name ?? '';

        if (this.template
            .querySelector('[data-id="IntWithDID"]')) {
            this.template
                .querySelector('[data-id="IntWithDID"]')
                .setIntNumLkpValue(this.IntWithLkpValue, this.IntWithLkpValue1);
        }
        this.intWithRecordId = this.IntWithLkpValue1;
        this.intWithName = this.IntWithLkpValue;
        this.industryPicklistvalue = this?.Interaction?.Interacting_With_type__c ?? '';
    let InteractionMemberrec = result.Interaction_Members__r;

    //Added for US#4890793
    if(this.refreshIntLogLMSSwitch){
      if(!InteractionMemberrec){
        this.authcheckValue= false;;
      }
    }

    InteractionMemberrec
      ? InteractionMemberrec.map((each) => {
          this.interactionMemberrecordId = each.Id;
          this.InteractingAboutTypevalue = each.Interacting_About_Type__c;
          if(this.multiMemberAuthSwitch){
            this.authcheckValue = each.Authenticated__c;
          }
          this.InteractingAboutPicklistvalue = [
            ...this.InteractingAboutPicklistvalue,
            {
              label: each.Interacting_About__r.Name,
              value: each.Interacting_About__r.Id
            }
          ];

          const interactingAboutPik = this.template.querySelector(
            '[data-id="InteractingAbout"]'
          );
          if(interactingAboutPik) interactingAboutPik.value = each.Interacting_About__r.Id;
          this.InteractingAboutRecordType =
            each.Interacting_About__r.RecordType.Name;
          this.InteractingAboutvalue = each.Interacting_About__r.Id;
          if(this.InteractingAboutvalue && !this.isAccountPage) {
            this.bIntCreationStatus = true;
          }
        })
            : "";
        if (InteractionMemberrec === null || InteractionMemberrec === undefined || InteractionMemberrec === '') {
            this.interactionMemberrecordId = '';
            this.InteractingAboutTypevalue = '';
            this.InteractingAboutPicklistvalue = [];
            this.InteractingAboutRecordType = '';
            this.InteractingAboutvalue = '';
            const interactingAboutPik = this.template.querySelector(
                '[data-id="InteractingAbout"]'
            );
            if (interactingAboutPik) interactingAboutPik.value = '';
        }
    if (this.isAccountPage) {
      this.bShowUpdate = false;
      this.mf3Switch = true;
    } else if (!this.isAccountPage) {
      this.bShowUpdate = true;
      this.mf3Switch = false;
    }
    if(!this.genesysUser){
      const wrapper = this.prepareSearchWrapperObj(this.Interaction.Call_Data__c);
      const autoSearchEvent = new CustomEvent("autosearch", {detail: {wrapper}});
      this.dispatchEvent(autoSearchEvent);
    }
    if(!this.multiMemberAuthSwitch){
      this.authcheckValue = this.Interaction?.Authenticated__c;
    }
    this.displayTogglebutton();
    this.disabledtoggle = this.authcheckValue ? true:false;
    }
}


  displayInteractionDetailsOnLoad(){
  let sUrl = window.location.href;
    let objInteraction = {};
    objInteraction.bOnActiveCall = false;
    if(sUrl.includes('c__interactionId')){
      let arrUrls = sUrl.split('?');     
      if(arrUrls && arrUrls.length>1){
        let lstQueryParams = arrUrls[1].split('&');
        if(lstQueryParams && lstQueryParams.length>0){
          lstQueryParams.forEach(ele=>{
            let arrInteractionUrls = ele.split('=');
            if(arrInteractionUrls && arrInteractionUrls.length>1){
              if(arrInteractionUrls[0] == 'c__interactionId'){
                objInteraction.sIntractionId = arrInteractionUrls[1];
              }
              else if(arrInteractionUrls[0] == 'c__bOnActiveCall'){
                if(arrInteractionUrls[1] == 'true') objInteraction.bOnActiveCall = true;
              }
            }
          });
        }
      }
      this.fetchInteractionDetails(objInteraction);
    }
    
  }

  // --> Get Interaction Button

  handleGetInteractionBtn(strCallerFn) {
    this.getInteractionBtnFlag = true;
    this.InteractingAboutPicklistvalue = [];
    let recordId2 = '';
    if (strCallerFn === "onIntNumSelection") {
      if (this.intNumRecordId.startsWith("a0U")) {
        recordId2 = this.intNumRecordId;
      } 
      this.getInteractionBtnFlag = false;
    } else {
      recordId2 = "";
    }
     this.getInteractionDetailsById(recordId2);
  }
  
   prepareSearchWrapperObj(objCallData){
    const SearchObject = {};
    const TypeToSearch = {};
    if(objCallData){
      let calldata = JSON.parse(objCallData);
    if (calldata.InteractingAboutType === 'Member') {
        SearchObject.sMemberid = calldata.InteractingAboutID;
        if (calldata.InteractingAboutID){
          SearchObject.sMemberid = calldata.InteractingAboutID;
          SearchObject.sFirstName = '';
          SearchObject.sLastName ='';
          SearchObject.sBirthdate = '';
          }
        else{
          SearchObject.sMemberid = '';
          SearchObject.sFirstName = calldata.InteractingAboutFirstName;
          SearchObject.sLastName =calldata.InteractingAboutLastName;
          SearchObject.sBirthdate = '';
          }
        SearchObject.sSuffix = '';
        SearchObject.sPhone = '';
        SearchObject.sGroupNumber = '';
        SearchObject.sPID = '';
        SearchObject.sState = '';
        SearchObject.sPostalCode = '';
        SearchObject.tabName = 'M';
        SearchObject.searchComponent = 'c-member-search-form-hum';
        TypeToSearch.member = SearchObject;
    }
    else if (calldata.InteractingAboutType === 'Provider') {
        SearchObject.sTaxID = calldata.InteractingAboutID;
        SearchObject.sFirstName = calldata.InteractingAboutFirstName;
        SearchObject.sLastName = calldata.InteractingAboutLastName;
        SearchObject.sNPI = calldata.InteractingAboutNPIID;
        SearchObject.sFacilityName = '';
        SearchObject.sState = '';
        SearchObject.sPostalCode = '';
        SearchObject.sSpeciality = '';
        
        SearchObject.tabName = 'P';
        SearchObject.searchComponent = 'c-provider-search-form-hum';
        TypeToSearch.provider = SearchObject;
    }
    else if (calldata.InteractingAboutType === 'Group') {
        SearchObject.sGroupName = calldata.InteractingAboutFirstName + ' ' +  calldata.InteractingAboutLastName;
        SearchObject.sState = '';
        SearchObject.sGroupNumber = calldata.InteractingAboutID;
        
        SearchObject.tabName = 'G';
        SearchObject.searchComponent = 'c-group-search-form-hum';
        TypeToSearch.group = SearchObject;
    }
    else if (calldata.InteractingAboutType === 'Agent') {
        SearchObject.sAgentType = 'Agency';
        SearchObject.sAgentId = calldata.InteractingAboutID;
        SearchObject.sState = '';
        SearchObject.sFirstName = '';
        SearchObject.sLastName = '';
        SearchObject.sTaxID = '';
        var tempId = '';
        
        SearchObject.tabName = 'A';
        SearchObject.searchComponent = 'c-agent-search-form-hum';
        TypeToSearch.agent = SearchObject;
    }
    else {
        SearchObject.sFirstName = '';
        SearchObject.sLastName = '';
        SearchObject.sMemberid = '';
        SearchObject.sSuffix = '';
        SearchObject.sBirthdate = '';
        SearchObject.sPhone = '';
        SearchObject.sGroupNumber = '';
        SearchObject.sPID = '';
        SearchObject.sState = '';
        SearchObject.sPostalCode = '';
        SearchObject.tabName = 'Member';
        SearchObject.searchComponent = 'c-member-search-form-hum';
        TypeToSearch.member = SearchObject;
    }
    }
    let objSearchData ={messageToSend:{callData: TypeToSearch }};
    return objSearchData;
}

  showPasswordPopup(btnFlag) {
    const interactingAboutPik = this.template.querySelector(
      '[data-id="InteractingAbout"]'
    );
    var sInteractingAboutTemp = "";
    var sInteractingWithAboutFlag = false;
    var sInteractingAbout = interactingAboutPik?.value ?? null;
    var sInteractingAboutName = this.InteractingAboutvalue;
    var sInteractingWith = this.industryPicklistvalue;
    var sinteractionWithId = this.intWithName;
    var sInteractingAboutType = this.InteractingAboutTypevalue;
    var sInteractionOrigin = this.InteractionOriginvalue;
    var sInteractionNumber = this.intNumRecordId;
    var intWithType = [
      "Member",
      "Unknown-Member",
      "Member Representative or Caregiver",
      "Other"
    ];

    if (
      sInteractionOrigin == "Inbound Call" &&
      sInteractingAboutType == "Member" &&
      intWithType.indexOf(sInteractingWith) > -1
    ) {
      if (btnFlag == "save" && !sInteractionNumber) {
        this.showAckPopUp(sInteractingAbout, btnFlag, null);
      } else if (btnFlag == "saveUpdate" && !sInteractionNumber) {
        this.showAckPopUp(sInteractingAbout, btnFlag, null);
      } else if (
        !sInteractionNumber &&
        (btnFlag == "save" || btnFlag == "saveUpdate") &&
        sInteractingAboutTemp != sInteractingAbout &&
        sInteractingWithAboutFlag == true
      ) {
        this.showAckPopUp(sInteractingAbout, btnFlag, null);
        if (btnFlag == "save") {
          this.handleSavePreLogic(this.memberCreated);
        } else if (btnFlag == "saveUpdate") {
          this.handleSavePreLogic(this.memberCreated);
        }
      } else {
        if (btnFlag == "save") {
          this.handleSavePreLogic(this.memberCreated);
        } else if (btnFlag == "saveUpdate") {
          this.handleSavePreLogic(this.memberCreated);
        } else if (btnFlag == "saveAndContinue") {
         this.navigateToAccountBool = true;
          this.handleSavePreLogic(this.memberCreated);
        }
      }
      sInteractingAboutTemp = sInteractingAbout;
      sInteractingWithAboutFlag = true;
    } else {
      sInteractingAboutTemp = sInteractingAbout;
      sInteractingWithAboutFlag = true;
      if (btnFlag == "save") {
        this.handleSavePreLogic(this.memberCreated);
      } else if (btnFlag == "saveUpdate") {
        this.handleSavePreLogic(this.memberCreated);
      } else if (btnFlag == "saveAndContinue") {
       this.navigateToAccountBool = true;        
       this.handleSavePreLogic(this.memberCreated);
      }
    }
  }

  showAckPopUp(sInteractionAbtId, interactionFlag, mapRecordHaveAccess) {
    this.sinteractionId = sInteractionAbtId;
    this.aShowAckPopup(sInteractionAbtId);
  }

  aShowAckPopup(sInteractionAbtId) {
    getInteractingAbout({ sInteractionAbout: sInteractionAbtId })
      .then((result) => {
        this.dataWire = result;
        this.popupOperation(this.dataWire);
      })
      .catch((error) => {
        this.error = error;
      });
  }

  closeModal1() {
    this.handleSavePreLogic(this.memberCreated);
    this.bShowModal = false;
    setSessionItem(this.selMemberAccId, true);
  }

  popupOperation(dataWire) {
    let termDate = "";
    if (dataWire.Account_Security_EndDate__c) {
      termDate = dataWire.Account_Security_EndDate__c
        ? getLocaleDate(dataWire.Account_Security_EndDate__c)
        : null;
    } else {
      termDate = null;
    }
    let todayDate = getLocaleDate(new Date());

    if (compareDate(termDate, todayDate) === 1) {
      this.questionDetails = {
        question: dataWire.Account_Security_Question__c,
        answer: dataWire.Account_Security_Answer__c,
        pValue: dataWire.Account_Security_Access__c
      };
      const dontshowmodal = false;
      this.selMemberAccId = this.sinteractionId;
      this.openModal(this.sinteractionId, dontshowmodal);
    } else {
      const dontshowmodal = true;
      this.openModal(this.sinteractionId, dontshowmodal);
    }
  }

  openModal(key, dontshowmodal) {
    var boolshowModal = getSessionItem(key) ? false : true;
    if (dontshowmodal === true) {
      if (boolshowModal === true) {
        this.bShowModal = false;
        this.handleSavePreLogic(this.memberCreated);
      } else if (boolshowModal === false) {
        this.bShowModal = false;
        this.handleSavePreLogic(this.memberCreated);
      }
    } else if (dontshowmodal === false) {
      if (boolshowModal === true) {
        this.bShowModal = true;
      } else if (boolshowModal === false) {
        this.bShowModal = false;
        this.handleSavePreLogic(this.memberCreated);
      }
    }
  }

  buttonRenderLogicSave() {
    let saveBtnBool = false;
    if (!this.genesysUser || this.isAccountPage) {
      saveBtnBool = true;
    } else if (
      ((this.InteractionOriginvalue !== "Inbound Call" && this.genesysUser) ||
        (!this.Switch_2230000 && this.genesysUser)) &&
      !this.isAccountPage
    ) {
      saveBtnBool = true;
    }
    return saveBtnBool;
  }

  buttonRenderLogicsaveNCont() {
    let saveBtnBool = false;
    if (
      this.InteractionOriginvalue == "Inbound Call" &&
      this.genesysUser &&
      this.Switch_2230000
    ) {
      if (!this.isAccountPage) {
        saveBtnBool = true;
        this.mf3Switch = false;
      }
    } else {
      this.mf3Switch = true;
    }
    return saveBtnBool;
  }

  handleAuthchanges(event) {
    const target = event.target.name;
    const value = event.target.checked;
    if (event.target.name === "authRadio") {
      this.authCheck = event.target.checked;
      if (this.authCheck) {
        this.authCheckLabel = "Authenticated";
      } else if (!this.authCheck) {
        this.authCheckLabel = "Unauthenticated";
      }
    }
  }

  buttonsRenderLogic(btnName) {
    authCheckFunction({ sInteractionAbout: this.interactionrecordId })
      .then((result) => {
        this.authCheck = result.authCheck;
        this.authCheckLabel = result.authCheckLabel;
      })
      .catch((error) => {
        this.error = error;
      });
  }

  handleUpdateBtn(strCallerFn) {
    this.radioErrorMessages = false;
    this.radioErrorMessage = "";
    this.cShowModal = true;
    this.getInteractionMemberListFn(strCallerFn);
  }

  setAuthValue(strCallerFn){
      if(!this.interactionrecordId){
        this.interactionrecordId =this.intNumRecordId;
      }
      if(this.InteractingAboutvalue && this.interactionrecordId){
        getSelectedIntMemberDetails({ sInteraction: this.interactionrecordId , sInteractionAbout:this.InteractingAboutvalue})
        .then((result) => {
          this.authcheckValue = result.Authenticated__c;
          if(result.Authenticated__c == true){
            this.authCheckLabel = 'Authenticated';
          }
          else {
              this.authCheckLabel = 'UnAuthenticated';
          }
          this.disabledtoggle = this.authcheckValue ? true:false;
          if (strCallerFn === "InteractingAbout"){
          if(this.multiMemberAuthSwitch ){
            if(this.InteractingAboutvalue ) {
              this.navigateToViewAccountPage();
              this.navigateToAccountBool = false;
            }
          } else{
          if(this.bIntCreationStatus && !this.isAccountPage && this.InteractingAboutvalue) {
            this.navigateToViewAccountPage();
            this.navigateToAccountBool = false;
          }
        }
          }
        })
        .catch((error) => {
          this.error = error;
        });
      }
  }

  getInteractionMemberListFn(strCallerFn) {
    let callerFnName = "";
    let recordId1 = this.interactionrecordId
      ? this.interactionrecordId
      : this.intNumRecordId;
    if (this.getInteractionBtnFlag) {
      callerFnName = "getInteractionMemberListFn";
    }
    if (strCallerFn === "deleteCalled") {
      this.InteractingAboutPicklistvalue = [];
      this.InteractingAboutvalue = null;
    }
    getInteractionMemberList({
      recordId: recordId1,
      callingFnName: callerFnName
    })
      .then((result) => {
        let InteractionMemberrec = result;
        InteractionMemberrec
          ? InteractionMemberrec.map((each) => {
              this.InteractionMemberList = [
                ...this.InteractionMemberList,
                { label: each.Interacting_About__r.Name, value: each.Id }
              ];
              if (strCallerFn === "deleteCalled") {
                this.InteractingAboutPicklistvalue = [
                  ...this.InteractingAboutPicklistvalue,
                  {
                    label: each.Interacting_About__r.Name,
                    value: each.Interacting_About__r.Id
                  }
                ];
				if(this.template.querySelector(
                  '[data-id="InteractingAbout"]'
                ) != null){
                const interactingAboutPik = this.template.querySelector(
                  '[data-id="InteractingAbout"]'
                );
                interactingAboutPik.value = each.Interacting_About__r.Id;
				}
                this.InteractingAboutvalue = each.Interacting_About__r.Id;

                const interactingAboutTypePik = this.template.querySelector(
                  '[data-id="InteractingAboutType"]'
                );
                interactingAboutTypePik.value = each.Interacting_About_Type__c.split(" ").join("-");
                this.InteractingAboutTypevalue = each.Interacting_About_Type__c.split(" ").join("-");
              }
            })
          : "";

          if (strCallerFn === "deleteCalled") {
            const InteractingAboutPicklistvalueLength 
            = this.InteractingAboutPicklistvalue ? this.InteractingAboutPicklistvalue.length : "";
            if(!InteractingAboutPicklistvalueLength){
              this.cShowModal = false
              this.template.querySelector(
                '[data-id="InteractingAboutType"]').value = 'none';
            }
          }
          if(this.multiMemberAuthSwitch){
            this.setAuthValue("deleteCalled");
          }
      })
      .catch((error) => {
        this.error = error;
      });
  }

  onIntRecordSearch(event) {
    if (event.detail.selectedObject === "intWith") {
      this.onIntWithSelectionSearch(event);
    } else if (event.detail.selectedObject === "intNum") {
      this.onIntNumSelectionSearch(event);
    }
  }

  onIntWithSelectionSearch(event) {
    this.intWithName = this.IntWithLkpValue = event.detail.selectedValue;
    this.intWithRecordId = this.IntWithLkpValue1 =
      event.detail.selectedRecordId;
    this.template
      .querySelector('[data-id="IntWithDID"]')
      .setIntNumLkpValue(this.IntWithLkpValue, this.IntWithLkpValue1);
  }

  onIntNumSelectionSearch(event) {
    this.IntNumLkpValue = event.detail.selectedValue;
    this.IntNumLkpValue1 = event.detail.selectedRecordId;
    this.template
      .querySelector('[data-id="IntNumDID"]')
      .setIntNumLkpValue(this.IntNumLkpValue, this.IntNumLkpValue1);
    this.onIntNumSelection(event);
  }

  createInteractionMemberPreLogic(strRecs) {
    let IMValue = this.InteractingAboutvalue ? this.InteractingAboutvalue : "";
    let recordId1 = this.interactionrecordId
      ? this.interactionrecordId
      : this.intNumRecordId;
    getInteractionMemberList({ recordId: recordId1, callingFnName: "" })
      .then((result) => {
        if (result) {
          result.forEach((el) => {
            if (el.Interacting_About__c === IMValue) {
              this.intMemberExists = true;
            }

            if(this.multiMemberAuthSwitch){
              if(this.authcheckValue === true){
                this.intMemberAuthUpdated = true;
              }
              else{
                this.intMemberAuthUpdated = false;
              }
            }
          });
        }

        if (
          this.InteractingAboutTypevalue &&
          this.InteractingAboutvalue &&
          !this.intMemberExists
        ) {
          this.createInteractionMember(strRecs);
        }
        else if(
          this.InteractingAboutTypevalue &&
          this.InteractingAboutvalue &&
          this.intMemberExists && this.intMemberAuthUpdated
        ){
          if(this.multiMemberAuthSwitch){
            if(this.InteractingAboutvalue && this.interactionrecordId){
              getSelectedIntMemberDetails({ sInteraction: this.interactionrecordId , sInteractionAbout:this.InteractingAboutvalue})
              .then((result) => {
                this.intaboutid = result.Id;
                this.InteractingAboutvalue = result.Interacting_About__c;
                this.InteractingAboutTypevalue = result.Interacting_About_Type__c;
                this.interactionrecordId = result.Interaction__c;
                this.updateInteractionMember(strRecs);
              })
              .catch((error) => {
                this.error = error;
              });
            }
          }

        }
        this.intMemberExists = false;
        if (strRecs) {
          this.handleResetAll();
          if (this.isAccountPage) {
            this.handlechangeobj = [
              {
                key: "clear",
                value: "clear"
              }
            ];
            this.fireEventToAura();
          }
        }
      })
      .catch((error) => {
        this.error = error;
      });
  }

  generatedUrl = "";
  async navigateToViewAccountPage() {
      if (this.triggeredEvent === 'HP_Link_MS') {
          const switchVal = getCustomSettingValue('Switch', 'HPIE Switch');
          switchVal.then(result => {
              if (result && result?.IsON__c && result?.IsON__c === true) {
                  this[NavigationMixin.Navigate]({
                      type: 'standard__navItemPage',
                      attributes: {
                          apiName: 'Pharmacy_HPIE',
                          recordId: this.objAccData?.Id
                      },
                      state: this.intNumRecordId ? {
                          c__AccountID: this.objAccData?.Id,
                          c__enterpriceID: this.objAccData?.enterpriseID,
                          c__fName: this.objAccData?.FirstName,
                          c__lName: this.objAccData?.LastName,
                          c__interactionId: this.intNumRecordId,
                          c__userId: btoa(this.networkId ?? ''),
                          c__memGenKey: btoa(this.objAccData?.Mbr_Gen_Key__c ?? ''),
                          c__PlanMemberId : this.memberPlanId,
                          c__userProfile: btoa(this.profileName?.replace(' ', '_') ?? '')
                      } : {
                          c__AccountID: this.objAccData?.Id,
                          c__enterpriceID: this.objAccData?.enterpriseID,
                          c__fName: this.objAccData?.FirstName,
                              c__lName: this.objAccData?.LastName,
                              c__userId: btoa(this.networkId ?? ''),
                              c__memGenKey: btoa(this.objAccData?.Mbr_Gen_Key__c ?? ''),
                              c__PlanMemberId : this.memberPlanId,
                              c__userProfile: btoa(this.profileName?.replace(' ', '_') ?? '')
                      }
                  });
              } else {
                  this[NavigationMixin.Navigate]({
                      type: 'standard__navItemPage',
                      attributes: {
                          apiName: 'Pharmacy_App_Page',
                          recordId: this.objAccData?.Id
                      },
                      state: this.intNumRecordId ? {
                          c__AccountID: this.objAccData?.Id,
                          c__enterpriceID: this.objAccData?.enterpriseID,
                          c__fName: this.objAccData?.FirstName,
                          c__lName: this.objAccData?.LastName,
                          c__interactionId: this.intNumRecordId,
                          c__userId: btoa(this.networkId ?? ''),
                          c__memGenKey: btoa(this.objAccData?.Mbr_Gen_Key__c ?? ''),
                          c__PlanMemberId : this.memberPlanId,
                          c__userProfile: btoa(this.profileName?.replace(' ', '_') ?? '')
                      } : {
                          c__AccountID: this.objAccData?.Id,
                          c__enterpriceID: this.objAccData?.enterpriseID,
                          c__fName: this.objAccData?.FirstName,
                          c__lName: this.objAccData?.LastName,
                          c__userId: btoa(this.networkId ?? ''),
                          c__memGenKey: btoa(this.objAccData?.Mbr_Gen_Key__c ?? ''),
                          c__PlanMemberId : this.memberPlanId,
                          c__userProfile: btoa(this.profileName?.replace(' ', '_') ?? '')
                      }
                  });
              }
              this.triggeredEvent = '';
              this.objAccData = {};
          });
        
    }else{
      this[NavigationMixin.GenerateUrl]({
        type: "standard__recordPage",
        attributes: {
          recordId: this.InteractingAboutvalue,
          objectApiName: "Account",
          actionName: "view"
        }
      }).then((generatedUrl) => {
        this.recordPageUrl = generatedUrl;
        this.createIntObj();
      });

      try {
        await invokeWorkspaceAPI("openTab", {
          pageReference: {
            type: "standard__recordPage",
            attributes: {
              recordId: this.InteractingAboutvalue,
              actionName: "view"
            },
            state: this.intNumRecordId ? {
              c__interactionId: this.intNumRecordId
            } : {}
          },
          focus: true
        }).then((result) => {          
          if(this.memberPlanId  && this.calledFromMemberSearch){
            setTimeout(() => {
              this.opensubtab(result);
            }, 2500);
          }
           this.calledFromMemberSearch = false;
          setTimeout(() => {
            this.handlechangeobj = [
              {
                key: this.InteractingAboutvalue,
                value: this.intLmsObjList
              }
            ];
            this.fireEventToAura();
          }, 2500);
        });
      } catch (error) {
        console.log("error " + error);
          }
          this.triggeredEvent = '';
          this.objAccData = {};
    }
    
  }

  async opensubtab(result){
    let selPolicyId =this.memberPlanId;
    let accountId = this.InteractingAboutvalue;
    try{
      await invokeWorkspaceAPI('openSubtab', {
        parentTabId: result,
        url: '/lightning/r/MemberPan/'+selPolicyId+'/view',
        focus: false
      });
    }
    catch(error){
      console.log(error + JSON.stringify(error));
    }    
  }

  @api
  createIntObj(message, firingtabParentid, subtabs) {
    const recName = this.intWithName ? this.intWithName : this.IntWithLkpValue;
    const recId = this.intWithRecordId
      ? this.intWithRecordId
      : this.IntWithLkpValue1;
    this.intLmsObjList = [];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      { key: "firstNameinput", value: this.firstName }
    ];
	this.intLmsObjList = [
      ...this.intLmsObjList,
      { key: "InteractingAbout", value: this.InteractingAboutvalue }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      { key: "InteractionOrigin", value: this.InteractionOriginvalue }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      { key: "Interactingwithtype", value: this.industryPicklistvalue }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      { key: "InteractingAboutType", value: this.InteractingAboutTypevalue }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      {
        key: "IntWithLkp",
        label: recName,
        value: recId
      }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      {
        key: "IntNumInp",
        label: this.interactionNameAcc ? this.interactionNameAcc : this.intNumName,
        value: this.interactionrecordId ? this.interactionrecordId : this.intNumRecordId
      }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      {
        key: "InteractingAboutPlkValues",
        value: this.InteractingAboutPicklistvalue,
        valueofrec: this.InteractingAboutvalue,
        InteractingAboutRecordType: this.InteractingAboutRecordType
      }
    ];
    this.intLmsObjList = [
      ...this.intLmsObjList,
      {
        key: "firingTabId",
        value: firingtabParentid
      }
    ];
	this.intLmsObjList = [
      ...this.intLmsObjList,
      { key: "authenticatorValue", value: this.authcheckValue }
    ];
    if (subtabs) {
      this.intLmsObjList = [
        ...this.intLmsObjList,
        {
          key: "subtabs",
          value: subtabs
        }
      ];
    }
    if (message) {
      this.handlechangeobj = [{ key: "intLmsObj", value: this.intLmsObjList }];
      const passEventr = new CustomEvent("senddatatochild", {
        detail: { key: "intLmsObj", value: this.intLmsObjList }
      });
      this.dispatchEvent(passEventr);
    }
  }

  handleAssCaseBtn() {
    this.handleAssCaseFlag = true;
    let caseinteractionexists = false;
    getCaseInteraction({
      caseRecordId: this.caseRecordId,
      intRecordId: this.interactionrecordId
    })
      .then((result) => {
        caseinteractionexists = result;
        if (caseinteractionexists) {
          this.dispatchEvent(
            new ShowToastEvent({
              title: "Warning",
              message: "This Case and Interaction are already associated.",
              variant: "warning"
            })
          );
        } else {
          this.handleSaveButton();
        }
      })
      .catch((error) => {
        this.error = error;
      });
  }

  @track counterIntAboutOpen = 0;
  handleIntAboutOpen(event) {
    if(this.bIntCreationStatus && !this.isAccountPage
       && (this.InteractingAboutvalue != null || this.InteractingAboutvalue !== "none")
       && this.counterIntAboutOpen <1) {
      this.navigateToViewAccountPage();
      this.navigateToAccountBool = false;
      this.counterIntAboutOpen = 1;
    }
  }
  
  render()
  {
      return this.minimizeWindow ? minimizeWindowTemplate: maximizeWindowTemplate;
  }
  setToggleValue(event){
    let isChecked = event?.target?.checked ? true:false;
    this.setToggleVariables(isChecked);
  }
  setToggleVariables(toggleValue){
     if(toggleValue){
           this.authcheckValue = true;
           this.authMessage = 'Caller Authenticated';
           this.authMsgStyle = 'slds-text-color_success';
           this.isActionMsgShow = false;   
       }
     else{
         this.authcheckValue = false;
         this.authMessage = 'Caller Unauthenticated';
         this.authMsgStyle = 'slds-text-color_error';
         this.isActionMsgShow = true;
         }     
   }
   displayTogglebutton(){
     this.isShowAuthSol =  this.InteractionOriginvalue === "Inbound Call" && this.genesysUser? true:false;
     if(this.isShowAuthSol){
       this.setToggleVariables(this.authcheckValue);
     } 
   }
   resetToggleVariables(){
     this.isShowAuthSol =  this.InteractionOriginvalue === "Inbound Call" && this.genesysUser? true:false;
     this.authcheckValue = false;
     this.authMessage = 'Caller Unauthenticated';
     this.authMsgStyle = 'slds-text-color_error';
     this.isActionMsgShow = true;
     this.disabledtoggle = false;
   }
   nameProviderSearchChanges(objpayload){
    if(this.InteractionOriginvalue === "none" || this.InteractionOriginvalue === null ||
      this.InteractionOriginvalue === undefined ||  this.InteractionOriginvalue === "" ){
        this.InteractionOriginvalue = "Inbound Call";
    }
    const eventName = objpayload?.message?.sourceSystem;
    const intwithandaboutobj = objpayload?.message?.messageToSend[0]?.rowData;
    this.isLocked = intwithandaboutobj?.isLocked;
    this.triggeredEvent = intwithandaboutobj?.originalEvent;
    this.objAccData = intwithandaboutobj;
    this.intAboutAccName = `${this.objAccData?.Individual_First_Name__c} ${this.objAccData?.Individual_Last_Name__c}`;
    this.intAboutAccName = this.intAboutAccName=='null null'?`${this.objAccData?.DBA__c}`:this.intAboutAccName;
    if((((this.intWithName && this.intWithRecordId) || this.firstName)
    || (this.industryPicklistvalue && ((this.intWithName  && this.intWithRecordId) || this.firstName)))){
      let tempintaboutvalue = intwithandaboutobj.Id;
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;
      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue
        ];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label:
            this.intAboutAccName,//intwithandaboutobj.Individual_First_Name__c + " " + intwithandaboutobj.Individual_Last_Name__c,
            value: intwithandaboutobj.Id
          }
        ];
      }

      //US4723377
      if(this.multiMemberAuthSwitch){
        this.InteractingAboutvalueTemp = this.InteractingAboutvalue;
      }

       if(this.template.querySelector(
        '[data-id="InteractingAbout"]'
      ) != null){
      let interactingAboutPik = this.template.querySelector(
        '[data-id="InteractingAbout"]'
      );
      interactingAboutPik.value = intwithandaboutobj.Id;
	  }
      this.InteractingAboutTypevalue =
        intwithandaboutobj.RecordTypeId.split(" ").join("-");
        let interactingAboutTypePik = this.template.querySelector(
          '[data-id="InteractingAboutType"]'
        );
        interactingAboutTypePik.value = this.InteractingAboutTypevalue;
      this.InteractingAboutRecordType = intwithandaboutobj.RecordTypeId;
      this.InteractingAboutvalue = intwithandaboutobj.Id;

      //US4723377
      if(this.multiMemberAuthSwitch){
        this.handleAuthChangeOnNameLinkClick(this.InteractingAboutvalueTemp,intwithandaboutobj.Id )
      }
      else{
        this.createIntRecOpenAcc();
      }
          
    }else{
      this.InteractingAboutvalue = intwithandaboutobj.Id;
      if(eventName === 'PROVIDER_SEARCH_NAMEPrId' && hasQuickStartUser_HUMAccess){
        this.isModalOpen = true;
        this.isLockedAcc = intwithandaboutobj.isLocked;
      }else{
        if(intwithandaboutobj.isLocked){
          this.handleNavigatetoProtectedPage();
        }else{
          this.navigateToViewAccountPage();  
        }
      }
    }
  }
  
  groupNameClickHandler(objpayload) {
    if (
      this.InteractionOriginvalue === "none" ||
      this.InteractionOriginvalue === null ||
      this.InteractionOriginvalue === undefined ||
      this.InteractionOriginvalue === ""
    ) {
      this.InteractionOriginvalue = "Inbound Call";
    }
    const eventName = objpayload?.message?.sourceSystem;
    const intwithandaboutobj = objpayload?.message?.messageToSend[0]?.rowData;
    this.isLocked = intwithandaboutobj?.isLocked;
    this.triggeredEvent = intwithandaboutobj?.originalEvent;
    this.objAccData = intwithandaboutobj;
    this.intAboutAccName = `${this.objAccData?.FirstName} ${this.objAccData?.LastName}`;
    this.intAboutAccName = this.intAboutAccName=='null null'?`${this.objAccData?.DBA__c}`:this.intAboutAccName;
    if ((this.groupIsInteractingAbout || (this.InteractingAboutTypevalue && this.InteractingAboutvalue)) && (this.groupIsInteractingWith || this.industryPicklistvalue)) {
      let tempintaboutvalue = intwithandaboutobj.Id;
      let temparr = [];
      temparr = this.InteractingAboutPicklistvalue;
      if (temparr.find((el) => el.value === tempintaboutvalue)) {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue
        ];
      } else {
        this.InteractingAboutPicklistvalue = [
          ...this.InteractingAboutPicklistvalue,
          {
            label:
            this.intAboutAccName,
            value: intwithandaboutobj.Id
          }
        ];
      }

      //US4723377
      if(this.multiMemberAuthSwitch){
        this.InteractingAboutvalueTemp =this.InteractingAboutvalue;
      }
      
      if (this.template.querySelector(
        '[data-id="InteractingAbout"]'
      ) != null) {
        let interactingAboutPik = this.template.querySelector(
          '[data-id="InteractingAbout"]'
        );
        interactingAboutPik.value = intwithandaboutobj.Id;
      }
      this.InteractingAboutTypevalue =
        intwithandaboutobj.RecordType.split(" ").join("-");
      let interactingAboutTypePik = this.template.querySelector(
        '[data-id="InteractingAboutType"]'
      );
      interactingAboutTypePik.value = this.InteractingAboutTypevalue;


      this.InteractingAboutRecordType = intwithandaboutobj.RecordType;
      this.InteractingAboutvalue = intwithandaboutobj.Id;

      //US4723377
      if(this.multiMemberAuthSwitch)
      {
        this.handleAuthChangeOnNameLinkClick(this.InteractingAboutvalueTemp,intwithandaboutobj.Id )
      }
      else
      {
        this.createIntRecOpenAcc();
      }
    }
    else {
      this.InteractingAboutvalue = intwithandaboutobj.Id;
      if(eventName === 'GROUP_SEARCH_FNLN' && hasQuickStartUser_HUMAccess){
        this.isModalOpen = true;
        this.isLockedAcc = intwithandaboutobj.isLocked;
        }else{
          if (intwithandaboutobj.isLocked) {
            this.handleNavigatetoProtectedPage();
          } else {
            this.navigateToViewAccountPage();
          }
        }
    }
  }

  /*
   * Added for US#4579434 - Caller type population
   * Params - {}
   * Description - this method is used to publish the selected 'Interacting with type' value to phoneBook.
   */
  sendInteractingWithValue(){
    const message = {
      interactingWithTypeValue: this.industryPicklistvalue,
    };
    publish(this.messageContext, PHONEBOOKCALLERTYPELMS, message);
  }
  
  closeModalPopUp() {
    this.isModalOpen = false;
}

  openAccPage() {
    this.isModalOpen = false;
    if(this.isLockedAcc){
      this.handleNavigatetoProtectedPage();
    }else{
      this.navigateToViewAccountPage();  
    }
  }

   /*
   * Added for US4723377
   * Params - {InteractingAboutvalue- previous selected interacting about value,
   * intwithandaboutobjId- previous selected interacting about value }
   * Description - US4723377: T1PRJ0036776: MMA ability to save authentication status by clicking on account name
   */
    handleAuthChangeOnNameLinkClick(InteractingAboutvalue, intwithandaboutobjId ){
        if(!this.interactionrecordId){
          this.interactionrecordId =this.intNumRecordId;
        }
        if(InteractingAboutvalue && intwithandaboutobjId && this.interactionrecordId && (InteractingAboutvalue != intwithandaboutobjId)){
          getSelectedIntMemberDetails({ sInteraction: this.interactionrecordId , sInteractionAbout:intwithandaboutobjId})
          .then((result) => {
            if(result.Authenticated__c == false || result.Authenticated__c == undefined)
            {
              this.authcheckValue = false;
            }
            else if(result.Authenticated__c == true)
            {
              this.authcheckValue = true;
            }
            this.disabledtoggle = this.authcheckValue ? true:false;
            this.createIntRecOpenAcc();
          })
          .catch((error) => {
            this.error = error;
          });
      }
      else{
        this.createIntRecOpenAcc();
      }
    }

  /*
   * Added for US#4951430 - Chat Assistant Panel
   * Params - {}
   * Description - Method publishes the Interacting With Account Id to the CRM AI Chat Assistant LWC when Interaction is created
  */
    sendInteractionMyAH(message){
      if(message.msgType == MyAH_LMS_toInteractionLog_Label) {
        const msg = {
          msginteractingAboutId: this.interactingAboutId,
          msgType: MyAH_LWS_toMyAH_Label}; 
        publish(this.messageContext, MYAHLMS, msg);
      }

    if(message.msgType == "PureCloudEvent") {
      var msg = {
        msginteractingAboutId: null,
        msgType: MyAH_LWS_toMyAH_Label}; 

      getCallMemberAccountDetails({searchModal: message.searchModal})
      .then(response => {
        msg.msginteractingAboutId = response;
        this.updateMyahInteractingAbout(response);
        publish(this.messageContext, MYAHLMS, msg);
      })
      .catch(error => {
        console.log("MYAH | Calling from interacitonCmpHum.js Erorr in getCallMemberAccountDetails" + JSON.stringify(error));
      })
      }
    }

    updateMyahInteractingAbout(value) {
      this.interactingAboutId = value;
    }

    subscribeToMyAHMessageChannel() {
      if (!this.myAHSubscription) {
        this.myAHSubscription = subscribe(
          this.messageContext,
          MYAHLMS,
          (message) => this.sendInteractionMyAH(message),
          { scope: APPLICATION_SCOPE }
        );
      }
    }
}