/*******************************************************************************************************************************
LWC JS Name : CaseInformationComponentHum.js
Function    : This JS serves as helper to caseInformationComponentHum.html

Modification Log:
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                                     09/14/2023            US - 4900963 - T1PRJ0865978 - C06; Case Mgt- Lightning - Disable Save and Transfer buttons for Complaint cases that auto-route
* Pooja Kumbhar                                     09/14/2023            US - 4932577 - T1PRJ0865978 - C06; Case Mgt Lightning - Update Toast Error Message on click of buttons Auto Routed Templates- CASE DETAIL PAGE
* Vani Shrivastava                                   09/15/2023             US 4918290: T1PRJ0865978 - C06- Case Management- Close Case button should have correct toast message verbiage
* Jasmeen Shangari								   10/5/2023			  DF-8188 on Click of save And trasfer button And close case button, toast message Is Not seen rather case Is getting closed on case edit page And User without 400 PS also able to edit the case.
* Prasuna Pattabhi                                   10/05/2023             US 4828071: T1PRJ0865978 - Disable Case Editing for Legacy Delete Members and Policies
* Prasuna Pattabhi                                   10/09/2023             US 4828071: T1PRJ0865978 - Added swicth on JS file
* Prasuna Pattabhi                                   10/10/2023             US 4828071: Switch Changes
* Apurva Urkude                                     10/27/2023             US 4932577: T1PRJ0865978 - Added swicth on JS file
* Prasuna Pattabhi                                   10/13/2023             US 4828071: Switch Changes Revisisted
* Nilesh Gadkar                                     10/13/2023             US 4918290: T1PRJ0865978 - Added Switch 
* Santhi Mandava                                    10/13/2023            US4884468 : Account custom lookup
* Prasuna Pattabhi                                   10/16/2023             US 4828071: Re adding Switch Changes overridden with Account lookup
* Jasmeen Shangari								     10/16/2023			    DF-8220 Mentor section not changing for edit case on Intent change.
* Prasuna Pattabhi                                   3/1/2024              US 5373580 :  Find Case related JS files which might run into size limitation
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import {
    getCaseLayout
} from './layoutConfig';
import {
    getUnknownCaseLayout
} from './unknownLayoutConfig';
import { doInitialSetup,showAccSearchPopUpModal,showAccAboutSearchPopUpModal,handleAccountSelection,handleAccSearchPopupClose,handleAboutChange,
    handleAccAboutSearchPopupClose,handleInteractingWithClear,handleInteractingAboutClear,handleInteractingWithTypeChange,handleInteractingAboutTypeChange } from './caseAccountSearchHelper';
import sendRequestLogNotes from "@salesforce/apexContinuation/CaseDetails_LC_Hum.sendRequestLogNotes";
import populateCaseData from '@salesforce/apex/CaseDetails_LC_Hum.populateCaseData';
import populateAccountRecordType from "@salesforce/apex/CaseDetails_LC_Hum.populateAccountRecordTypeName";
import saveCase from '@salesforce/apex/PharmacyCaseSave_LC_HUM.saveCaseDetails';
import inputsForValidation from "@salesforce/apex/CaseCriteriaValidations_LH_HUM.inputsForValidation";
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { deleteObjProperties, toastMsge, getUserGroup } from "c/crmUtilityHum";
import customcss from '@salesforce/resourceUrl/LightningCRMAssets_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { getCaseLabels, getLabels } from 'c/customLabelsHum';
import { CurrentPageReference } from 'lightning/navigation';
import caseValidations from "c/caseValidationsHum";
import pubsub from 'c/pubSubHum';
import { publish, MessageContext, subscribe, unsubscribe } from 'lightning/messageService';
import COMMENTS_CHANNEL_REQ from '@salesforce/messageChannel/caseCommentsLMSReqChannel__c';
import COMMENTS_CHANNEL from '@salesforce/messageChannel/caseCommentsLMSChannel__c';
import getCallBenefitCategoryValues from '@salesforce/apex/CaseDetails_LC_Hum.getCallBenefitCategoryValues';
import callBenifitCategoryView from '@salesforce/apex/CaseDetails_LC_Hum.callBenifitCategoryView';
import validateCase from '@salesforce/apex/CaseDetails_LC_Hum.validateCase';
import hasCRMS_CSERT_Complaints from '@salesforce/customPermission/CRMS_CSERT_Complaints'; 
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import getRecordTypeChange from '@salesforce/apex/CaseDetails_LC_Hum.getRecordTypeChange';  // for RT change update in UI
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import getlaunchEmmeURL from '@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.getlaunchEmmeURL';
import NETWORK_ID_FIELD from '@salesforce/schema/User.Network_User_Id__c';
import CURRENT_QUEUE from '@salesforce/schema/User.Current_Queue__c';
import USER_ID from '@salesforce/user/Id';
import { getRecord } from 'lightning/uiRecordApi';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import CASECOMMENT_FAULTERROR_HUM from '@salesforce/label/c.CASECOMMENT_FAULTERROR_HUM';
import CASECOMMENT_MEMBERNOTFOUND_HUM from '@salesforce/label/c.CASECOMMENT_MEMBERNOTFOUND_HUM';
import { createCaseCommentRecord } from 'c/genericCaseActionHum';
import CASECOMMENT_PHARMACY_FAULTERROR_HUM from '@salesforce/label/c.CASECOMMENT_PHARMACY_FAULTERROR_HUM';
import CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM from '@salesforce/label/c.CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM';
import getProcessDetails from '@salesforce/apex/CaseDetails_LC_Hum.getProcessDetails';
import CheckCaseTemplates from '@salesforce/apex/CaseDetails_LC_Hum.CheckCaseTemplates';
import isNewbornProcessAttached from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.isNewbornProcessAttached';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
import getCaseNumber from '@salesforce/apex/CaseDetails_LC_Hum.getCaseNumber';
import { callHpieService, callRSService,MHKAutoRouteButtonDisable,getCaseFieldValues,enableRelatedSection,getSwitchData } from './caseInformationComponentHelper';
import IS_LEGACY_DELETE_MEMBER from '@salesforce/label/c.IS_LEGACY_DELETE_MEMBER';
import isLegacyDeleteMemberOrPolicy from '@salesforce/apex/CaseDetails_LC_Hum.isLegacyDeleteMemberOrPolicy';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
const USER_FIELDS = [NETWORK_ID_FIELD,CURRENT_QUEUE,PROFILE_NAME_FIELD];
const MEMBER_PLAN_ID = ['Member_Plan_Id__c'];
import { showEsclIndicator, showDCNUnknown, setVisibilities,preFillValues } from './caseInformationComponentHelper';
import { handleTypeChange, handleSubTypeChange, handleClassificationChange , handleIntentChange,handlePharmacyLogCode,
    handleMedicareChange, setTabLabelOnEdit, checkTemplates, showToastMessage, handleLogCodeVisibility,
    navigateToCaseDetailPage, createCase,fetchTabInfo,toFocusTab} from './caseInformationCaseDataHelper';
export default class CaseInformationComponentHum extends caseValidations {
    @api recordId;// Case Id
    @api encodedData;//It'll store the account id when case is created from HP tab.
    @api pageRefData; //this variable is used for getting URL attributes that coming from other pages
    @api interactionID; //to store the interaction id that is passed thru url
    @api showPopupModal = false; // to store whether to show memberplan popup
    @track profileName;
    @track resultData;
    @track caseForm;
    @track case = {};
    @track pharmacyLogCode;
    @track caseComment = {};
    @track itemSize = "slds-col slds-size_1-of-1 slds-p-around_small";
    @track showActionModal = false;
    @track labels = getCaseLabels();
    @track prevLabels = getLabels();
    @track caseAccountId;
    @track buttonsConfig = [
        {
            text: "Continue",
            isTypeBrand: true,
            eventName: "continue"
        },
        {
            text: getLabels().HUMCancel,
            isTypeBrand: false,
            eventName: 'closeoverlay'
        }
    ];
    @track overlayModalTitle = 'Unsaved Changes';
    @track isEdit = false;
    @track tabLabel = this.labels.HUMNewCase;
    @track caseNumber;
    @track pickListVisible = true; //This is used to control the visibility of HP log code field.
    @track error;
    isCase = false;
    isModified = false;
    @track ClassificationIntentMap = {};
    @track urlCaseComment;
    @track comments;
    @track commentLimit; // this will used to hold the comment limit based on LOBs
    @track maxLimitMsg;
    lstHpCases = ['Closed HP Agent/Broker Case', 'Closed HP Group Case', 'Closed HP Member Case', 'Closed HP Provider Case', 'Closed HP Unknown Case', 'HP Agent/Broker Case', 'HP Group Case', 'HP Member Case', 'HP Provider Case', 'HP Unknown Case'];
    @track bDisplayLimitMsg = true;
    @track forRccTemplate = false;
    @track templatename;
    @track pcpservicefundedit = false;
    @track autoroutestatus;
    @track prevSubType;
    @track caseType;
    @track caseSubtype;
    iscaseSave = 'false_' + Date.now();
    @track METValidations = '';
    @track METTasklist = '';
    @track isSaveTransfer = false;
    @track ClickedTransfer = false;
    @track isCloseCase = false;
    @track ClickedClose = false;
    @track clickedCloseCase = false;
    
    @track showAccSearch = false;
    @track showAccAboutSearch = false;
    @track showWithClear = false;
    @track showAboutClear = false;
    @track interactingWithObj = {};
    @track interactingAboutObj = {};
    //@track bIntWithMember = false;
    @track accSearchHistory = {};
    @track providerAccSearchHistory = {};
    @track bInteractingWithMember =false;
    @track bInteractingWithProvider =false;
    @track bNonMemberOrProvider =false;
    @track bAccountNameCustomLookUp = false;
    @track bInteractingAboutMember = false;
    @track bInteractingAboutProvider = false;
    @track bMemberSearch = false;
    @track bProviderSearch = false;
    @track sInteractingAboutVal = '';
    @track selectedField ='';
    @track switch_4884468 = false;
    cancelCaseDisabled = false;
    closeCaseDisabled = false;
    saveTransferDisabled = false;
    SaveCaseDisabled = false;
    isCreateCase = true;
    tempcaseid;
    lstComplaints = ['Yes - Medicaid', 'Yes - Medicare Part C', 'Yes - Medicare Part D'];
    lstAutoRouteStatus = ['Evaluate Good Cause', 'Evaluate Expedited Appeal', 'PCP Reassignment', 'Termed in Error', 'Evaluate Part D'];
    lstInteractingwithType = ['Member', 'Member Representative or Caregiver', 'Internal', 'Government', 'Care Manager', 'Unknown-Member'];
    
    layoutData = {};
    userAccess = {};
    requiredFields = {};
    isCallBenefitCategoryView = false;
    lstInteractingAboutType = ['Agent', 'Group', 'Member', 'Provider', 'Unknown-Agent', 'Unknown-Group', 'Unknown-Member', 'Unknown-Provider'];
    lstInteractionOrigin = ['Service Inquiry', 'NINA Web Chat', 'Internal Process', 'Watson Voice', 'Watson Web Chat', 'Vantage', 'Cancelled', 'Correspondence'];
    isEscIndicatorDisplay = false;
    isDCNDisplayUnknown = false;
    isDCNLink = false;
    isEscIndicatorDateDisplay = false;
    isDCNDisplay = false;
    isFromQS = false;
    medicarePartCPartDQS = '';
    caseCommentIss;
    caseCommentRes;
    bDisplayLimitMsgRes = true;
    bDisplayLimitMsgIss = true;
    maxLimitMsgIss;
    maxLimitMsgRes;
    bMediCCUpdate = false;
    showMedicareCallsComments = false;
    issue;
    flowName = '';
    processResultDetails;
    swtichComment;
    isHPCaseRecType;
    bIsMedicareCalls;
    @track caseRecType;
    @track comments;
    @track userRecord;
    @track netId;
    @track enterpriseId;
    enableClaimModal = false;
    claimList = '';
    enableClaimButton = false;
    @track bShowProcessTemp = true;
    switch_5231359 =false;

    bIntWithType = false;
    bIsIVRcase = false;
    bIntWithTypeOrigin = false;
    bEditAccName = false;
    bEditMemberPlan = false;
    selectedAccountId;
    pageName;
    isNewCase = true;
    @track hasNewbornAttached = false;
    @track newbornClassification;
    @track newbornIntent;
    @track isMCDPolicy;
    @track currentStatus

    @track caseId;
    @track idClassification;
    @track classificationLabel;
    @track intentLabel;
    @track bShowMentorDocs = false;
    @track ciMentorlinkdata = {};
    @track lstReferenceDocuments = []; 
    @track lstMentorDocuments = []; 
    @track IsMentorlink;
    @track bIsShowDocumentSec = false;
    @track isLegacyDeleteMember = IS_LEGACY_DELETE_MEMBER;
    @track b4874911Switch = false;
    CCSup = "Customer Care Supervisor";
	CCSpec = "Customer Care Specialist";
	HPSpe = "Humana Pharmacy Specialist";

    Commentlabels = {
        CASECOMMENT_FAULTERROR_HUM,
        CASECOMMENT_MEMBERNOTFOUND_HUM,
        CASECOMMENT_PHARMACY_FAULTERROR_HUM,
        CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM

    }

    memPlanDisabled = false;
    memPlanName;
    @track showPill = false;
    @track showIcon = false;
    selectedRecord = {};
    @track isCredentialing = false;
    @track bisSwitchOn4828071 = false;    
     @track bisSwitchOn4932577 = false;
    @track bisSwitchOn4918290 = false;
    sAttachedTemplateName = '';

    @wire
        (getRecord, { recordId: USER_ID, fields: USER_FIELDS })
    wireUserRecord({ error, data }) {
        if (data) {
            try {
                this.userRecord = data;
                this.netId = data.fields.Network_User_Id__c.value;
				this.userCurrentQueue = data.fields.Current_Queue__c.value;
                this.profileName = data.fields.Profile.value.fields.Name.value;
            } catch (e) {
                console.log('An error occured when handling the retrieved user record data');
            }
        } else if (error) {
            console.log('An error occured when retrieving the user record data: ' + JSON.stringify(error));
        }
    }

    @wire(getRecord, {
        recordId: '$caseAccountId',
        fields: ['Account.Enterprise_ID__c']
    })
    wiredAccount({ error, data }) {
        if (data) {
            this.enterpriseId = data.fields.Enterprise_ID__c.value;
        } else if (error) {
            console.log('error in wire--', error);
        }
    }
    @wire(MessageContext)
    messageContext;

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference && currentPageReference?.attributes && currentPageReference?.attributes?.attributes) {
            let pageAttributes = currentPageReference.attributes.attributes;
            let urlStateParameters = currentPageReference.state.ws;
            let urlstate = urlStateParameters.split('c__interactionId=')[1];

            this.interactionID = urlstate != null && urlstate != '' && urlstate != undefined ? urlstate.substring(0, 18) : null;

            if (pageAttributes.encodedData.Id != null && pageAttributes.encodedData.Id != undefined) {
                this.encodedData = pageAttributes.encodedData.Id;
            } else if (pageAttributes?.encodedData) {
                this.encodedData = pageAttributes.encodedData;
            }
            if (pageAttributes?.encodedData?.caseComment) {
                this.urlCaseComment = pageAttributes?.encodedData?.caseComment ?? '';
                this.caseCommentRes = pageAttributes?.encodedData?.caseComment ?? '';
            }
            if (pageAttributes?.encodedData?.objApiName && pageAttributes.encodedData.objApiName === 'Case' && pageAttributes?.encodedData?.Id) {
                this.recordId = pageAttributes.encodedData.Id;
            }
            if (pageAttributes?.encodedData?.templateName) {
                this.templatename = pageAttributes.encodedData.templateName;
            }
            if (pageAttributes?.encodedData?.serviceFundEdit) {
                this.pcpservicefundedit = pageAttributes.encodedData.serviceFundEdit;
            }
            if (pageAttributes?.encodedData?.isFromUpdatePlanDemographic && pageAttributes.encodedData.isFromUpdatePlanDemographic) {
                this.forRccTemplate = true;
            }
            if (pageAttributes?.encodedData?.flowname) {
                this.flowName = pageAttributes.encodedData.flowname;
            }
        } else if (currentPageReference && currentPageReference?.attributes && currentPageReference?.attributes?.state &&
            currentPageReference?.attributes?.state?.c__recordId) {
            this.recordId = currentPageReference.attributes.state.c__recordId;
            this.encodedData = currentPageReference.attributes.state.c__recordId;
            this.flowName = currentPageReference.attributes.state.c__flowname;
        }
    }


    connectedCallback() {
        if (this.recordId) {
            this.isEdit = true;
            this.tabLabel = "Edit Case";
            this.isNewCase = false;
            this.pageName = 'Case Edit';
            this.tempcaseid = this.recordId;
            this.bShowMentorDocs = true;
        }
        else {
            this.isCreateCase = true;
        }
        getSwitchData.call(this).then(result => {
            if (result && result === true) {
        if (this.isNewCase) {
            this.pageName = 'New Case';
            this.bShowProcessTemp = false;
        }
        this.qsCommentsSubscriptionEvent();
        this.caseDetaildata();
        this.loadCommonCss();
         }
        })
    }

    renderedCallback() {
        //Below method will run only on edit case to set the visibility of HP log case and seting default value for log code if applicable.
        if (this.ClassificationIntentMap && Object.keys(this.ClassificationIntentMap).length > 0 && this.isEdit) {
            handleLogCodeVisibility.call(this,this.ClassificationIntentMap.classification, this.ClassificationIntentMap.intent, true);
        }
        this.ClassificationIntentMap = null;
        if (this.template.querySelector("lightning-textarea") != null && this.comments != '' && this.comments != undefined) {
            if (this.template.querySelector("[data-id='commentsiss']") != null || this.template.querySelector("lightning-textarea") != null) {
                if (this.showMedicareCallsComments == true && this.template.querySelector("[data-id='commentsiss']") != null) {
                    const commentsiss = this.template.querySelector("[data-id='commentsiss']");
                    const commentsres = this.template.querySelector("[data-id='commentsres']");
                    if (this.issue != '' && this.issue.length > (this.commentLimit + 2)) {
                        this.issue = this.issue.substring(0, this.commentLimit);
                    }
                    if (this.comments != '' && this.comments.length > (this.commentLimit + 2)) {
                        this.comments = this.comments.substring(0, this.commentLimit);
                    }
                    if (commentsres != null) {
                        commentsres.value = this.comments;
                        commentsres.focus();
                        this.checkCharLimitResolution();
                    }
                    if (commentsiss != null) {
                        commentsiss.value = this.issue;
                        commentsiss.focus();
                        this.checkCharLimitIssue();
                    }
                    this.comments = '';
                    this.issue = '';
                } else if (this.template.querySelector("lightning-textarea") != null) {
                    let cmt = this.issue + this.comments;
                    if (cmt != '' && cmt.length > (this.commentLimit + 2)) {
                        cmt = cmt.substring(0, this.commentLimit);
                    }
                    this.template.querySelector("lightning-textarea").value = cmt;
                    this.template.querySelector("lightning-textarea").focus();
                    this.checkCharLimit();
                    this.comments = '';
                    this.issue = '';
                }
            }
        }

    }

    loadCommonCss() {
        Promise.all([
            loadStyle(this, customcss + '/CRM_Assets/styles/commonStyles.css')
        ]).catch(error => {
            this.error = error;
        });
    }
    

    /**
     * Method Name : caseDetaildata
     * Function: get data from apex call and prefill default fields
     */
    async caseDetaildata() {
        try {           
            const objSwitch = await isCRMFunctionalityONJS({sStoryNumber: ['4884468','5231359']});
            if(objSwitch) {
                this.switch_4884468 = objSwitch['4884468'];
                this.switch_5231359 = objSwitch['5231359'];
            }
            
            const pageName = (this.pageRefData && this.pageRefData.hasOwnProperty('pageName')) ? this.pageRefData.pageName : '';
            let inputParams = { callerPageName: pageName, flowName: this.flowName, interactionId: this.interactionID };
            if(this.bisSwitchOn4828071 == true && this.isEdit == true){
                const sresult = await isLegacyDeleteMemberOrPolicy({ Id: this.recordId });
                if (sresult == true){
                    this.dispatchEvent(new ShowToastEvent({title: "",message: this.isLegacyDeleteMember,variant: 'error',mode: "dismissible"}));
                    const focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
                    this.closeTab(focusedTab,this.recordId);
                    return;
                } 
            }
            const result = await populateCaseData({ objectID: this.encodedData, params: JSON.stringify(inputParams) }); //, isPharmacyLogging: true
            this.resultData = result;
            this.caseAccountId = result?.objCase?.AccountId ?? null;
            this.memPlanId = result?.objCase?.Member_Plan_Id__c ?? null;
            this.memPlanName = result?.additionalInfo?.memberPlanName ?? null;
            doInitialSetup(result,this);
            if (this.resultData.objCase.Interacting_About_Type__c != 'Member') {
                this.memPlanDisabled = true;
            }
            if (this.memPlanName != null && !this.memPlanDisabled) {
                this.showPill = true;
                this.showIcon = false;
            } else if (this.memPlanDisabled) {
                this.showPill = false;
                this.showIcon = false;
            }
            else {
                this.showPill = false;
                this.showIcon = true;
            }
            showEsclIndicator.call(this,this.resultData.objCase.Interacting_About_Type__c, this.resultData.objCase.Origin, this.resultData.objCase.Classification_Type__c);
            enableRelatedSection(this);
            if (result) {
                this.processResultDetails = result.processResultDetails;
                this.bIsMedicareCalls = result.additionalInfo.bMedicareCalls;
                if(this.switch_5231359) this.sAttachedTemplateName = result.additionalInfo.sAttachedTemplateName;
                this.caseNumber = result.prefillValues.caseNumber;
                this.autoroutestatus = result?.prefillValues?.AutoRouteStatus ?? '';
                this.currentStatus = result.objCase.Status;
                this.isMCDPolicy = result.additionalInfo.bHasMCDPolicy;
                this.isCredentialing = result.prefillValues.isCredentialing;
                if (this.isEdit == true) {
                    this.classificationLabel = result?.prefillValues?.classification_Id;
                    if(this.switch_5231359) this.intentLabel = result?.prefillValues?.intent_Name;
                    this.newbornClassification = result.objCase.Classification__c;
                    this.newbornIntent = result.prefillValues.intent_Name;
                     if(this.bisSwitchOn4932577 == true) {
                    if((result.objCase.Case_Owner__c == 'LV G and A and Correspondence Screening' || result.objCase.Case_Owner__c == 'Green Bay Grievance and Appeals') && (result.objCase.Owner_Queue__c == 'LV G and A and Correspondence Screening' || result.objCase.Owner_Queue__c == 'Green Bay Grievance and Appeals') && result.additionalInfo.userAccess.CRMS_400_Grievance_Appeals == undefined)
				  {
					  showToastMessage.call(this,this.recordId);
					   }
                }
                }
                setTabLabelOnEdit.call(this);
                preFillValues.call(this,result);
                if (result.objCase.Classification__c == 'Claims') {
                    this.enableClaimButton = true;
                }
                if (result.objCase.Status == 'Closed') {
                    this.closeCaseDisabled = true;
                    this.saveTransferDisabled = true;
                }
            }
            showDCNUnknown.call(this);

            if (this.recordId && this.processResultDetails == undefined) {
                checkTemplates.call(this,this.recordId);
            }
            if (this.isEdit == true) {
                let submissionId = await isNewbornProcessAttached({ Id: this.recordId });
                this.hasNewbornAttached = submissionId != '' && submissionId != null ? true : false;
                if (this.hasNewbornAttached && this.currentStatus == 'In Progress') {
                    const classification = this.template.querySelector('[data-id="case-classification"]');
                    const intent = this.template.querySelector('[data-id="case-intent"]');
                    classification.disabled = true;
                    intent.disabled = true;
                    const memberplan = this.template.querySelector('[data-id="case-memberplan"]');
                    memberplan.disabled = true;
                    this.memPlanDisabled = true;

                }
                if (this.isCredentialing) {
                    const classification = this.template.querySelector('[data-id="case-classification"]');
                    const intent = this.template.querySelector('[data-id="case-intent"]');
                    classification.disabled = true;
                    intent.disabled = true;
                }
            }

        } catch (error) {
            this.error = error;
            let message = error.body.message
                .replace(/&amp;/g, "&")
                .replace(/&quot;/g, '"');
            toastMsge("", message, "error", "pester");
        }
    }   

    handleStandardChange(event) {
        this.isModified = true;
        if (event && event.target) {
            setVisibilities.call(this,event.target.fieldName, event.target.value);
            if (event.target.fieldName == 'AccountId') {
                this.selectedAccountId = event.target.value;
                const memplan = this.template.querySelector('[data-id="case-memberplan"]');
                if (memplan != null) {
                    memplan.value = null;
                    this.selectedRecord.Id = '';
                    this.showPill = false;
                    this.showIcon = true;
                }
                const medid = this.template.querySelector('[data-id="case-medicareid"]');
                if (medid != null) {
                    medid.value = null;
                }
                this.case.AccountId = event.target.value;
            } else if (event.target.fieldName == 'Type') {
                handleTypeChange.call(this,event);
                const dcnId = this.template.querySelector('[data-id="case-dcnid"]');
                if (dcnId != null) {
                    if (event.target.value == 'Correspondence Inquiry' || event.target.value == 'MHK Dispute Task') {
                        dcnId.disabled = false;
                    } else {
                        dcnId.disabled = true;
                        dcnId.value = null;
                    }
                }
            } else if (event.target.fieldName === 'Subtype__c') {
                handleSubTypeChange.call(this,event);
            }
            else if (event.target.fieldName === 'Interacting_About_Type__c') {
                const memplan = this.template.querySelector('[data-id="case-memberplan"]');
                const accname = this.template.querySelector('[data-id="case-accname"]');
                if (accname.value != null) {
                    accname.value = null;
                }
                if (event.target.value == 'Member' && !this.hasNewbornAttached) {
                    memplan.disabled = false;
                    this.memPlanDisabled = false;
                    this.showPill = false;
                    this.showIcon = true;
                }
                else {
                    memplan.disabled = true;
                    this.memPlanDisabled = true;
                    this.showPopupModal = false;
                    memplan.value = null;
                    this.selectedRecord.Id = '';
                    this.showPill = false;
                    this.showIcon = false;
                }
                handleInteractingAboutTypeChange(event,this);
                }else if (event.target.fieldName === 'Interacting_With_Type__c'){
                handleInteractingWithTypeChange(event,this);
            }
        }
    }

    
    /**
     * Method: handleChange
     * @param {*} event
     * Function: this method is used to manupulate picklist value and called from onchage attribut in combobox.
     */
    handleChange(event) {
        this.isModified = true;
        if (event.target.name === 'classificationToIntentValues') {
            handleClassificationChange.call(this,event);
        } else if (event.target.name === 'CTCI_List__c') {
            handleIntentChange.call(this,event);
        } else if (event.target.name === 'pharmacyLogCode') {
            handlePharmacyLogCode.call(this,event);
        } else if (event.target.name === 'medicarePartcPartD') {
            handleMedicareChange.call(this,event);
        }
    }

    
    
    
    

    /**
     * Method Name : handleRadioChange
     * @param {*} event
     * Function : this function is being called from handlechange function on the change of Humana Pharmacy Log Code value
     */
    handleRadioChange(event) {
        if (event.target.checked) {
            this.case[event.target.name] = event.currentTarget.dataset.label;
        }
        this.isModified = true;
    }

    /**
    Method Name:handleMETData
    function : get MET Enrollment section data OR validation error message
    */

    handleMETData(event) {
        let errMsg = ''
        if (event.detail.error != '' && event.detail.error != undefined && event.detail.error != null) {
            if (event.detail.msgOnsave == true) {
                this.METValidations = event.detail.error;
            } else {
                this.METValidations = '';
                errMsg = event.detail.error;
                this.showPageMsg(false, errMsg);
            }
        } else if (event.detail.data != '' && event.detail.data != undefined && event.detail.data != null) {
            this.METValidations = '';
            this.METTasklist = event.detail.data;
        } else if (event.detail.error == '') {
            if (event.detail.msgOnsave == true) {
                this.METValidations = '';
            } else {
                errMsg = '';
                this.showPageMsg(true, errMsg);
            }
        }

    }

    /**
     * Method Name : onSave
     * Function : this function is use to send the case data in apex to create the case record
     * and interactionid that is captured from URL
     */
    async onSave() {
        try {
        		let isValid = true;
            this.iscaseSave = 'true_' + Date.now();
            const recordData = this.template.querySelectorAll('lightning-input-field');
            const lightFields = this.template.querySelectorAll('lightning-input');
            const caseComment = this.template.querySelector('lightning-textarea');

            if (caseComment != null) {
                this.caseComment.CommentBody = this.pharmacyLogCode ? '(' + this.pharmacyLogCode + ') ' + caseComment.value : caseComment.value;
                this.comment = caseComment.value;
            }
            if (recordData) {
                recordData.forEach(field => {
                    this.case[field.fieldName] = field.value;
                });
                if (this.clickedCloseCase == true) {
                    this.case.Status = 'Closed';
                }
            }
            if (lightFields) {
                lightFields.forEach(field => {
                    this.case[field.name] = field.value;
                });
            }
            if (this.isEdit) {
                this.case['Member_Plan_Id__c'] = this.selectedRecord.Id;
                if(this.switch_4884468){
                    if(this.caseForm[1]?.fields[1]?.hasOwnProperty('accLookup') && this.caseForm[1].fields[1]['accLookup']){
                        if(!this.bNonMemberOrProvider) this.case['Interacting_With__c'] = this.interactingWithObj.sInteractingWithId;
                    }
                    if(this.caseForm[1]?.fields[4]?.hasOwnProperty('accNameLookup') && this.caseForm[1].fields[4]['accNameLookup']){
                        if(this.bAccountNameCustomLookUp) this.case['AccountId'] = this.interactingAboutObj.sInteractingAboutId;
                        isValid = this.handleRequiredFieldsValidations({ "lightning-input": "name" }, ["AccountId"],
                        "Complete this field", "Required fields are highlighted in red", true);
                    }
                }
            }

            this.case['interactionid'] = this.interactionID;
            if(this.switch_5231359){
                if(this.classificationLabel == 'Complaints or Compliments' && this.intentLabel == 'QAA'){
                    if((this.processResultDetails == undefined && !this.sAttachedTemplateName) && this.case['Status'] == 'Closed'){
                        isValid =false;
                        this.showPageMsg(false, 'The case cannot be closed because the QAA Process Template has not been attached');
                    }
                }
            }
            if (this.resultData.prefillValues.caseRecordTypeName == 'Closed Medicare Case') {
                isValid = this.handleRequiredFieldsValidations({ "lightning-combobox": "label", "lightning-input-field": "fieldName" }, ["Status"],
                    "Complete this field", "Required fields are highlighted in red", true);
            } else {
            if(isValid){
                isValid = this.handleRequiredFieldsValidations({ "lightning-combobox": "label", "lightning-input-field": "fieldName" }, [
                    "Classification",
                    "Intent",
                    "Priority",
                    "Interacting_With_Type__c",
                    "Origin",
                    "Status",
                    this.requiredFields.compliantDate,
                    this.requiredFields.compliantOrigin,
                    this.requiredFields.compliantCategory,
                    this.requiredFields.compliantReason,
                    this.requiredFields.accountId
                ], "Complete this field", "Required fields are highlighted in red", true);
            }
            }
            if (!isValid) {
                return;
            }
            try {
                ////////////////////////criteria based validation Starts here////////////////////////////////////////////////
                let isValidationNotOccur = await this.handleCriteriaBaseValidation({ "lightning-input-field": "fieldName" }, [
                    "Interacting_With__c",
                    "Member_Plan_Id__c"
                ]);
                let isRespStatusSucceeded = this.handleResponseStautsValidation(this.resultData.prefillValues, this.case);

                if (isValidationNotOccur && isRespStatusSucceeded) {
                    this.recordId ? this.case.Id = this.recordId : '';
                    if (this.pcpservicefundedit && this.templatename && this.templatename.toLowerCase() === 'pcp update'
                        && this.autoroutestatus != 'Completed') {
                        this.case['Autoroute_Status__c'] = 'PCP Reassignment';
                    } else if (this.templatename && this.templatename.toLowerCase() === 'invoice') {
                        this.case['Autoroute_Status__c'] = 'Invoice Requests';
                    }
                    let errMsg = '';
                    let regEx = /^[0-9a-zA-Z]+$/;
                    if (this.isDCNDisplay == true && this.case.DCN__c != '' && this.case.DCN__c != null && this.case.DCN__c != undefined && !this.case.DCN__c.match(regEx)) {
                        errMsg = 'Warning: Please enter only alpha-numeric values for the DCN field';
                        this.showPageMsg(false, errMsg);
                    }
                    if (this.bMediCCUpdate != true && this.showMedicareCallsComments == true && this.case.Status == 'Closed'
                        && this.resultData.prefillValues.caseRecordTypeName.indexOf('Closed') == -1) {
                        if (this.caseCommentIss == null || this.caseCommentIss == '' || this.caseCommentRes == null || this.caseCommentRes == '') {
                            errMsg = 'Enter Medicare Calls Case Comments';
                        } else if (this.caseCommentIss.length < 10 || this.caseCommentRes.length < 10) {
                            errMsg = 'A minimum of 10 characters are required, with a maximum of 2000';
                        }
                    }
                    if (this.lstComplaints.includes(this.case.Complaint__c) &&
                        ((this.processResultDetails != undefined && this.processResultDetails.sAutoRouteStatus != undefined && this.lstAutoRouteStatus.includes(this.processResultDetails.sAutoRouteStatus)) ||
                            (this.processResultDetails != undefined && this.processResultDetails.isCreditableCoverageATVF007Routed != undefined && this.processResultDetails.isCreditableCoverageATVF007Routed == 'true'))) {
                        errMsg = this.labels.Complaint_must_be_selected_No;
                    }
                    if (this.processResultDetails != undefined &&
                        this.processResultDetails.bIsMedicaidPARProcessAttached == 'true' &&
                        this.case.Complaint__c != null &&
                        this.case.Complaint__c == 'Yes - Medicaid') {
                        errMsg = this.labels.Complaint_Cant_be_marked_Medicaid_PAR_Provider;
                    }
                    if (errMsg == '' && this.recordId) {
                        errMsg = await validateCase({ status: this.case.Status, origin: this.case.Origin, memplanId: this.case.Member_Plan_Id__c, validateDCN: this.isDCNDisplay, DCN: this.case.DCN__c, caseId: this.recordId, type: this.case.Type });
                        if (errMsg != '' && this.clickedCloseCase == true && errMsg.includes('The open Work Task(s) listed below must be closed before closing or canceling the case')) {
                            errMsg = 'This case cannot be Closed because there is an open Work Task';
                        }
                    }
                    if (errMsg == '') {
                        if (this.case.Origin == 'Web Chat' && this.case.Member_Plan_Id__c == ''
                            && this.case.Status != 'Closed' && this.case.Status != 'Cancelled' && this.case.Interacting_With_Type__c == 'Member') {
                            errMsg = 'Please enter a value for Policy Member.';
                        }
                    }
                    if (errMsg == '' && this.hasNewbornAttached == false && this.isEdit == true) {
                        if (this.newbornClassification == 'Enrollment' && this.newbornIntent == 'Newborn' && this.isMCDPolicy && this.currentStatus == 'In Progress') {
                            errMsg = 'Medicaid Newborn Notification Template is required before saving the case.';
                            this.ClickedTransfer = false;
                            this.clickedCloseCase = false;
                        }
                    }
					 if(this.bisSwitchOn4932577 == true){
					if(errMsg == '' && (this.isEdit || this.isNewCase) && (this.clickedCloseCase == true || this.ClickedTransfer == true)){
						errMsg = await MHKAutoRouteButtonDisable.call(this,this.case);
						if(errMsg != '')
						{
							 this.showPageMsg(false, errMsg);
							 this.clickedCloseCase = false;
							 this.ClickedTransfer = false;
						}
						}
					}
                    if (errMsg == '') {
                        if (this.METValidations == '') {
                            createCase.call(this);
                        } else {

                            this.showPageMsg(false, this.METValidations);
                        }
                    } else {
                        this.showPageMsg(false, errMsg);
                    }
                }
            } catch (error) {
                console.log('error in validationcallback--', error);
            }
            //////////////////////////////////////////criteria based validaion end here/////////////////////////////////
        } catch (error) {
            this.error = error;
        }
    }

    
    /**
         * Method Name: sendRequestToEpost
         * Function: this is used for sending comment and logocedes to EPOST once hit save and then save casecomment to DB
         */
    async sendRequestToEpost(caseId) {
        let caseid = caseId
        const switchVal = getCustomSettingValue('Switch', 'HPIE Switch');
        switchVal.then(result => {
            if (result && result?.IsON__c && result?.IsON__c === true) {
                callHpieService.call(this, caseid);
            } else {
                callRSService.call(this, caseid);
            }
        });
    }
    sendCaseCommentsToEpost(sCaseId) {
        return new Promise((resolve, reject) => {
            sendRequestLogNotes({
                sComment: this.comment,
                sCode: this.pharmacyLogCode,
                sCaseId: sCaseId,
                sEntId: this.enterpriseId,
                sNetworkId: this.netId,
                sAccId: this.caseAccountId
            })
                .then(result => {
                    resolve(result);
                })
                .catch(err => {
                    console.log("Error occured in sendCaseCommentsToEpost- " + JSON.stringify(err));
                    reject(err);
                });
        })
    }
    addCaseComment(caseID, caseComment) {
        let caseCommentResponse;
        createCaseCommentRecord(caseID, caseComment).then(result => {
            if (result) {
                caseCommentResponse = result;
            }
        }).catch(error => {
            console.log('Error Occured in addCaseComment', error);
        });
    }

    

    async closeCaseDetailPage() {
        const focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        this.closeTab(focusedTab.tabId);
        setTimeout(() => {
            invokeWorkspaceAPI('openSubtab', {
                parentTabId: focusedTab.parentTabId,
                focus: true,
                recordId: focusedTab.recordId
            }).then(res => {
                console.log('res ', JSON.stringify(res));
            }).catch(error => {
                console.log('error in opensub tab', error);
            });
        }, 300);


    }
    /**
  *
  * @param {*} tabId of case detail page that needs to refresh once case dit lwc close after save
  */
    async refreshSubtab(tabId) {
        try {
            await invokeWorkspaceAPI("refreshTab", {
                tabId: tabId
            });
            // pubsub to refresh case comment custom LWC component
            pubsub.fireCrossEvent(this.resultData.objCase.Id, 'case-transfer-container-hum', this.resultData.objCase.Id);
            if (this.forRccTemplate) {
                setTimeout(() => { pubsub.fireCrossEvent(this.resultData.objCase.Id, 'refreshCaseProcessSec', { 'isFinished': true }); }, 2000);
            }
        } catch (error) {
            console.log('error in refresh tab function--', error);
        }
    }

    

    /**
   * Method Name : closeTab
   * @param {*} focusTab, caseId, source
   * Function : This method will close the create/edit page and navigate user to case detail page.
   */

    async closeTab(focusTab, caseId, source = "subTab") {
        try {
            await invokeWorkspaceAPI("closeTab", {
                tabId: focusTab.tabId
            });
            if (source === "primaryTab") {
                // this if needed to confirm that now case is edit from HP tab that will show edit form as a parentTab
                const accId = this.resultData.objCase.AccountId;
                invokeWorkspaceAPI("getAllTabInfo")
                    .then((res) => {
                        const self = this;
                        let pharmacyTab = res.filter((item) => {
                            return (
                                item.pageReference.type === "standard__navItemPage" &&
                                item.pageReference.attributes.apiName === "Humana_Pharmacy" &&
                                accId === self.matchAccId(item.pageReference.state)
                            );
                        });
                        let subTabToNavigate = pharmacyTab[0].subtabs.filter((item) => {
                            return item.recordId === caseId;
                        });
                        toFocusTab.call(this,subTabToNavigate[0].tabId);
                    })
                    .catch((error) => {
                        console.log("error in focusedtab", error);
                    });
            } else {
                fetchTabInfo.call(this,focusTab, caseId);
            }
        } catch (error) {
            console.log("error in closedtab--", error);
        }
    }

    


    /**
     * Method Name : matchAccId
     * @param {*} stateParam - it will contain array that has accId or any other value
     * Function : This method is used to verify account id that comes from HP tab and case that create from accId are same so that we can focus on that subtab.
     */
    matchAccId(stateParam) {
        let tab = Object.values(stateParam).filter((item) => {
            return item === this.resultData.objCase.AccountId;
        });
        return tab[0];
    }


    async onContinueClick() {
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        this.closeTab(focusedTab);

    }

    onCancelClick() {
        this.showActionModal = false;
    }

    onCancel() {
        if (this.isModified) {
            this.showActionModal = true;
        } else {
            this.onContinueClick();
        }
    }

    async onSaveTransfer() {
        this.ClickedTransfer = true;
        if (this.isModified || this.recordId == null || this.recordId == undefined) {
            await this.onSave();
        } else if (this.hasNewbornAttached == false && this.isEdit == true) {
            if (this.newbornClassification == 'Enrollment' && this.newbornIntent == 'Newborn' && this.isMCDPolicy && this.currentStatus == 'In Progress') {
                this.showPageMsg(false, 'Medicaid Newborn Notification Template is required before saving the case.');
                this.ClickedTransfer = false;
            }
        }
        else {
            this.isSaveTransfer = true;
        }
    }

    handlesaveandnavigatecase(event) {
        navigateToCaseDetailPage.call(this,event.detail);
    }

    closeTransferPopUp(event) {
        this.isSaveTransfer = false;
        this.ClickedTransfer = false
    }


    async onClose() {
        this.clickedCloseCase = true;
        this.isModified = true;
        if (this.isModified || this.recordId == null || this.recordId == undefined) {
            await this.onSave();
        } else if (this.hasNewbornAttached == false && this.isEdit == true) {
            if (this.newbornClassification == 'Enrollment' && this.newbornIntent == 'Newborn' && this.isMCDPolicy && this.currentStatus == 'In Progress') {
                this.showPageMsg(false, 'Medicaid Newborn Notification Template is required before saving the case.');
                this.clickedCloseCase = false;
            }
        }
        else {
            this.isCloseCase = true;
        }

    }

    /**
   * Method Name : handleRequiredFieldsValidations
   * Function : This method is used to handle requird field validations
   */
    handleRequiredFieldsValidations(selectorCheckNull, fldstoCheckNull, fldErrorMsg, pageMsg, isHeaderMsg) {
        let selectorToValidate = this.checkNullValues(selectorCheckNull, fldstoCheckNull);
        if (this.requiredFields.medicarePartcPartD == "true") {
            selectorToValidate.push(this.template.querySelector('[data-id="case-medicare-part-c-d"]'));
        }
        return this.updateFieldValidation(selectorToValidate, fldErrorMsg, pageMsg, isHeaderMsg);
    }

    /**
     * Method Name : handleCriteriaBaseValidation
     * Function : This method is used to handle criteria based validations using apex.
     */
    async handleCriteriaBaseValidation(selectors, fieldsName) {
        let dBresult;
        let isFromValid = false;
        let fieldSelectors = this.checkNullValues(selectors, fieldsName);
        let inputMap = {};
         if(fieldSelectors.length == 0){
            if(this.caseForm[1]?.fields[1]?.hasOwnProperty('accLookup') && this.caseForm[1].fields[1]['accLookup']){
                inputMap = { accountId: this.interactingWithObj.sInteractingWithId};
            }
        }
        else if (fieldSelectors.length == 1) {
            inputMap = { accountId: fieldSelectors[0].value };
        }
        else {
            inputMap = { accountId: fieldSelectors[0].value, memPlanId: fieldSelectors[1].value };
        }
        try {
            dBresult = await inputsForValidation({ inputParams: JSON.stringify(inputMap) });
            isFromValid = this.performValidations(dBresult);
        } catch (error) {
            console.log('errorDBValidation--', error);
        }
        return new Promise((resolve, reject) => {
            if (isFromValid) {
                return resolve(isFromValid);
            } else {
                return reject(false);
            }
        });
    }
    qsCommentsSubscriptionEvent() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                COMMENTS_CHANNEL,
                (message) => this.assignComments(message)
            );
        }
        const payload = { eventName: 'requestComments' };
        publish(this.messageContext, COMMENTS_CHANNEL_REQ, payload);
    }
    /**
       * Method Name: assignComments
       * Function: used to assign the commetns to the comments text area when case created or comments update from quickstart
    */
    assignComments(message) {
        if (message.source == 'QuickStart' && message.caseId == this.recordId) {
            this.displayComments(message.comments, true, 'commentsTransferred');
            this.isFromQS = true;
            this.medicarePartCPartDQS = message.comments.medicarePartCOrPartD;
        } else if (message.caseId == this.recordId && message.source == 'UpdateComments') {
            this.validateCIData(message.comments);
        }
    }
    /**
       * Method Name: displayComments
       * Function: used to display the commetns in the comments text area when case created or comments update from quickstart
    */
    displayComments(comments, success, eventName) {
        if (success) {
            let issue = comments.issue != '' && comments.issue != null && comments.issue != undefined ? comments.issue.replace(/\n\n/g, ' \n') : '';
            let resolution = comments.resolution != '' && comments.resolution != null && comments.resolution != undefined ? comments.resolution.replace(/\n\n/g, ' \n') : '';
            if (eventName == 'UpdateCommentsTrasnferred') {
                if (this.showMedicareCallsComments == true) {
                    const commentsiss = this.template.querySelector("[data-id='commentsiss']");
                    const commentsres = this.template.querySelector("[data-id='commentsres']");
                    if (issue != '' && issue.length > (this.commentLimit + 2)) {
                        issue = issue.substring(0, this.commentLimit);
                    }
                    if (resolution != '' && resolution.length > (this.commentLimit + 2)) {
                        resolution = resolution.substring(0, this.commentLimit);
                    }
                    if (commentsres != null) {
                        commentsres.value = resolution;
                        commentsres.focus();
                        this.checkCharLimitResolution();
                    }
                    if (commentsiss != null) {
                        commentsiss.value = issue;
                        commentsiss.focus();
                        this.checkCharLimitIssue();
                    }
                } else if (this.template.querySelector("lightning-textarea") != null) {
                    let cmt = issue + resolution;
                    if (cmt != '' && cmt.length > (this.commentLimit + 2)) {
                        cmt = cmt.substring(0, this.commentLimit);
                    }
                    this.template.querySelector("lightning-textarea").value = cmt;
                    this.template.querySelector("lightning-textarea").focus();
                    this.checkCharLimit();
                }
            } else if (eventName == 'commentsTransferred') {
                this.issue = issue;
                this.comments = resolution;
            }
        }
        const payload = { eventName: eventName, success: success };
        publish(this.messageContext, COMMENTS_CHANNEL_REQ, payload);

    }
    /**
     * Method Name: checkCharLimit
     * Function: used to check character limit onchange on comment box
     */
    checkCharLimit() {
        this.handleStandardChange();
        let sCaseComment = this.template.querySelector(`[data-id='comments']`);
        this.maxLimitMsg = (this.commentLimit - sCaseComment.value.length) + ' characters remaining';
        if (this.commentLimit === sCaseComment.value.length) {
            sCaseComment.setCustomValidity('Warning: Exceeded character limit');
            this.bDisplayLimitMsg = false;
        } else {
            sCaseComment.setCustomValidity("");
            this.bDisplayLimitMsg = true;
        }
        sCaseComment.reportValidity();
    }

    /**
       * Method Name: validateCIData
       * Function: used to check the classification and intent with the edit page when update comments triggered from quickstart
    */
    validateCIData(comments) {
        let isValid = true;
        let clssficationIdQS = comments.classificationIdQS ? comments.classificationIdQS : '';
        let intentIdQS = comments.intentIdQS ? comments.intentIdQS : '';
        let ceClassificationID = '';
        let ceIntentId = '';
        const CIData = this.template.querySelectorAll("lightning-combobox");
        if (CIData) {
            CIData.forEach((field) => {
                if (field.name == 'classificationToIntentValues') {
                    ceClassificationID = field.value;
                } else if (field.name == 'CTCI_List__c') {
                    ceIntentId = field.value;
                }
            });
        }
        if (intentIdQS && ceIntentId != 'None' && intentIdQS != ceIntentId) {
            isValid = false;
        }
        if (clssficationIdQS && ceClassificationID != 'None' && clssficationIdQS != ceClassificationID) {
            isValid = false;
        }
        this.displayComments(comments, isValid, 'UpdateCommentsTrasnferred');
    }
    

    


    

    /**
      * Method Name : handleLinkEvent
      * @param {*} event
      * Function : this function is being called when user click on DCN link
    */
    handleLinkEvent(event) {
        const urlLink = event.currentTarget.getAttribute('data-action');
        let label = event.currentTarget.getAttribute('data-link-label');

        if (label == 'DCN') {
            window.open(urlLink, '_blank', "toolbar=yes, scrollbars=yes, resizable=yes, width=1000");
        }
    }

    /**
       * Method Name: checkCharLimitIssue
       * Function: used to check character limit onchange on comment box
       */
    checkCharLimitIssue() {
        this.handleStandardChange();
        let sCaseComment = this.template.querySelector("[data-id='commentsiss']");
        this.caseCommentIss = sCaseComment.value;
        this.maxLimitMsgIss = (this.commentLimit - sCaseComment.value.length) + ' characters remaining';
        if (this.commentLimit === sCaseComment.value.length) {
            sCaseComment.setCustomValidity('Warning: Exceeded character limit');
            this.bDisplayLimitMsgIss = false;
        } else {
            sCaseComment.setCustomValidity("");
            this.bDisplayLimitMsgIss = true;
        }
        sCaseComment.reportValidity();
    }

    /**
       * Method Name: checkCharLimitResolution
       * Function: used to check character limit onchange on comment box
       */
    checkCharLimitResolution() {
        this.handleStandardChange();
        let sCaseComment = this.template.querySelector("[data-id='commentsres']");
        this.caseCommentRes = sCaseComment.value;
        this.maxLimitMsgRes = (this.commentLimit - sCaseComment.value.length) + ' characters remaining';
        if (this.commentLimit === sCaseComment.value.length) {
            sCaseComment.setCustomValidity('Warning: Exceeded character limit');
            this.bDisplayLimitMsgRes = false;
        } else {
            sCaseComment.setCustomValidity("");
            this.bDisplayLimitMsgRes = true;
        }
        sCaseComment.reportValidity();
    }
    onClmNumberAdd() {
        this.enableClaimModal = true;
    }
    handleAddClaims(event) {
        this.claimList = this.claimList.length > 0 ? this.claimList + ',' + event.detail.toString() : event.detail.toString();
        this.enableClaimModal = false;
    }
    handleClaimpopup(event) {
        this.enableClaimModal = event.detail;
    }
    handleData(event) {
        this.templatename = event.detail.showtemplate;
        this.pcpservicefundedit = event.detail.showservice;
    }
    async getProcessDetails(event) {
        this.flowName = event.detail.flowName;
        let updatedData;
        if (this.flowName == 'Medicaid Newborn Notification') {
            if (this.hasNewbornAttached == true) {
                updatedData = {};
            } else {
                updatedData = await getProcessDetails({ Id: this.recordId, flowName: this.flowName });
                this.hasNewbornAttached == true
            }
        } else {
            updatedData = await getProcessDetails({ Id: this.recordId, flowName: this.flowName });
        }
        this.processResultDetails = updatedData;
        if (this.processResultDetails.bdisablecloseCancel == 'true') {
            this.closeCaseDisabled = true;
            this.cancelCaseDisabled = true;
            this.SaveCaseDisabled = false;
        }
        if (this.processResultDetails.bTransferBtnDisabled == 'true') {
            this.saveTransferDisabled = true;
        }
        const status = this.template.querySelector('[data-id="cstatus"]');
        status.value = updatedData.status;
        status.disabled = updatedData.bDisableCaseStatus == "true" ? true : false;

        const topic = this.template.querySelector('[data-id="ctopic"]');
        if (updatedData.topic != undefined && updatedData.topic != null && updatedData.topic != "") {
            this.case.Topic__c = updatedData.topic;
            topic.value = updatedData.topic;
        }

        if (updatedData.sClassification != undefined && updatedData.sIntent != undefined) {

            const classification = this.template.querySelector('[data-id="case-classification"]');
            const intent = this.template.querySelector('[data-id="case-intent"]');

            classification.value = updatedData.sClassification;
            this.case.Classification_Id__c = updatedData.sClassification;
            const classificatioToIntent = this.resultData.ctciModel.classificationToIntent;
            if (classificatioToIntent[updatedData.sClassification]) {
                intent.options = [...[{ label: '--None--', value: '--None--' }], ...classificatioToIntent[updatedData.sClassification]];
            }

            intent.value = updatedData.sIntent;
            this.case.Intent_Id__c = updatedData.sIntent;

            if (updatedData.bHasCTCI == "true") {
                classification.disabled = true;
                intent.disabled = true;
            }
        }

        if (this.flowName == 'Medicaid Newborn Notification') {
            const memberplan = this.template.querySelector('[data-id="case-memberplan"]');
            memberplan.disabled = true;
            this.hasNewbornAttached = true;
            this.case.CTCI_List__c = updatedData.CTCILIstId;
        }
        if (updatedData.isCreditableCoverageATVF007Routed == "true") {

            const type = this.template.querySelector('[data-id="ctype"]');
            const subtype = this.template.querySelector('[data-id="csubtype"]');
            type.disabled = updatedData.bDisableType == "true" ? true : false;
            subtype.disabled = updatedData.bDisableSubtype == "true" ? true : false;
            const classificationType = this.template.querySelector('[data-id="clstype"]');
            classificationType.value = updatedData.classificationtypes;
            type.value = updatedData.ctype;
            subtype.value = updatedData.subtype;
            this.case.Classification_Type__c = updatedData.classificationtypes;
            this.case.Type = updatedData.ctype;
            this.case.Subtype__c = updatedData.subtype;
            const recType = this.template.querySelector('[data-id="caseRecordTypeName"]');
            const caseowner = this.template.querySelector('[data-id="caseowner"]');
            const ownerqueue = this.template.querySelector('[data-id="ownerqueue"]');
            const wqv = this.template.querySelector('[data-id="cwqviewname"]');

            recType.value = updatedData.rectype;
            caseowner.value = updatedData.caseowner;
            ownerqueue.value = updatedData.ownerqueue;
            wqv.value = updatedData.workqueueview;
            this.case.Case_Owner__c = updatedData.caseowner;
            this.case.Owner_Queue__c = updatedData.ownerqueue;
            this.case.Work_Queue_View_Name__c = updatedData.workqueueview;
        }
    }
    /* method called to decide when memberplan pop should open*/
    handlePopUpModal() {
        if (this.memPlanDisabled || this.showPill) {
            this.showPopupModal = false;
        } else
            this.showPopupModal = true;
    }
    /* to close the modal when click on close */
    modalCloseHandler() {
        this.showPopupModal = false;
    }
    /* to get selected record from memberPlanLookupHum */
    handleRadioSelect(event) {
        if (event.detail) {
            this.selectedRecord = event.detail.record[0];
            this.memPlanName = this.selectedRecord.Name;
            this.showPill = this.selectedRecord != undefined ? true : false;
            this.showIcon = this.selectedRecord != undefined ? false : true;
        }
        setVisibilities.call(this,MEMBER_PLAN_ID, this.selectedRecord.Id)
    }
    /* to remove pill when onclick*/
    handleRemove() {
        this.memPlanName = '';
        this.selectedRecord.Id = '';
        this.showPill = false;
        this.showIcon = true;
        setVisibilities.call(this,MEMBER_PLAN_ID, this.selectedRecord.Id);
    }
    
        showAccSearchPopUp(){
        showAccSearchPopUpModal(this);
    }
    
    showAccAboutSearchPopUp(){
        showAccAboutSearchPopUpModal(this);
    }
    
    handleCustomAccountSelection(event){
        handleAccountSelection(event,this);
    } 

    handleAccountSearchPopupClose(){
        handleAccSearchPopupClose(this);
    }

    handleAboutSearchPopupClose(){
        handleAccAboutSearchPopupClose(this);
    }

    handleIntWithClear(){
        handleInteractingWithClear(this);
    }
    
    handleIntAboutClear(){
        handleInteractingAboutClear(this);
    }
    
    handleAboutFocusChange(event){
        handleAboutChange(event,this);
    }

    handleAccountNameChange(event) {
        if(event.target.name == 'Interacting_With__c'){
            if(!event.target.value){
                handleInteractingWithClear(this);
            }
            
        }else if(event.target.name == 'AccountId'){
            if(!event.target.value){
                handleInteractingAboutClear(this);
            }
        }
    }
}