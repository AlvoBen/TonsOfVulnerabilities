import { LightningElement, track, api, wire } from 'lwc';
import getCaseData from '@salesforce/apex/CaseTransfer_LC_HUM.fetchCaseData';
import saveCaseTransfer from '@salesforce/apex/CaseTransferSave_LD_HUM.saveCaseTransfer';
import { getUserGroup, toastMsge } from 'c/crmUtilityHum';
import { getCaseLabels } from "c/customLabelsHum";
import pubsub from 'c/pubSubHum';
import hasCRMS_300_Humana_Pharmacy_Supervisor from '@salesforce/customPermission/CRMS_300_HP_Supervisor_Custom';
import { CurrentPageReference } from 'lightning/navigation';
import CheckCaseTemplates from '@salesforce/apex/CaseDetails_LC_Hum.CheckCaseTemplates';
import isNewbornReq from '@salesforce/apex/CaseDetails_LC_Hum.isNewbornReq';
import NEWBORN_ERROR_MSG from '@salesforce/label/c.NEWBORN_ERROR_MSG';
import hasCRMS_400_Grievance_Appeals from '@salesforce/customPermission/CRMS_400_Grievance_Appeals_Custom';
import IS_LEGACY_DELETE_MEMBER from '@salesforce/label/c.IS_LEGACY_DELETE_MEMBER';
import isLegacyDeleteMemberOrPolicy from '@salesforce/apex/CaseDetails_LC_Hum.isLegacyDeleteMemberOrPolicy';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';

export default class CaseTransferContainerHum extends LightningElement {
    @wire(CurrentPageReference) pageRef;
    @api recordId; // this will be caseid
    @api recordid; // this will be caseid
    @api bReassignToCreator = false; //variable used to identify that creator button is clicked and should be true here from aura component
    @api bReassignToSender = false; //variable used to identify that sender  button is clicked and should be true here from aura component

    @api iscreatecase =false;

    @track dbResponse; // this will use to fetch Database data
    @track inputParams = {};   // this variable used to send input params to apex to fetch relevant data on transfer page
    @track oUserGroup = getUserGroup();
    @track caseLabels = getCaseLabels();
    @track profileName; // used to store current logged in user prfile name
    @track userId; // this variable used to hold the selected user Id from autocomplete
    @track userName; // this variable used to hold the selected user name from autocomplete
    @track manualEnterUser; //this vairable used to store the value present in user field
    @track showInfoMsges = false; //this variable will take care of hiding and showing information message on UI
    @track showValidationMsge = false; // this variable is used for hide/show validation msges
    @track guidanceinformationalMsge = ''; // used to store guidance msge coming from casetransferservicedepthum
    @track topicinformationalMsge = ''; // used to store topic msge coming from casetransferservicedepthum
    @track spinnerVisible = true;
    displayLabel;
    @track showTransferAssistInfo = false;
    @track showCaseTransferAssistmsg = '';
    @track bisSwitchOn4932577 = false;
    isButtonDisable = false;
    hasNewbornValMsgReq = false;
    newbornErrorMsg = NEWBORN_ERROR_MSG;
    LVGScreening = 'LV G and A and Correspondence Screening';
    Grrenbayqueue = 'Green Bay Grievance and Appeals';
    @track isLegacyDeleteMemberErrorMsg = IS_LEGACY_DELETE_MEMBER;
    @track isLegacyDeleteMember = false;
    @track bisSwitchOn4828071 = false;

    connectedCallback() {
		
        if(this.recordId == '' || this.recordId == null || this.recordId == undefined){
		    this.recordId = this.recordid;
		}
        if(this.bReassignToCreator==true){
            this.displayLabel ='Reassign To Creator';   
        }
        else if(this.bReassignToSender==true){
            this.displayLabel ='Reassign To Sender';       
        }
        else{
            this.displayLabel ='Change Owner';     
        }
        this.getSwitchData.call(this).then(result => {
            if (result && result === true) {
        this.CheckTemplates(this.recordId);
        this.hasNewbornValMsg();
        this.callServer();
            }
        });
    }

    /* *
       * Method Name :- Check Templates
       * Function:- To shoe toast error message if templates are auot routed on case 
       * */

    async CheckTemplates(caseid) {
       const result = await CheckCaseTemplates({ caseId: caseid });
        if (result) {
            if (result.bTransferBtnDisabled == 'true') {
                this.isButtonDisable = true;
            }
        } 
    }

    /* *
       * Method Name :- hasNewbornValMsg
       * Function:- To show toast error message if babybot template is not attached  
    * */
    async hasNewbornValMsg() {
        const result = await isNewbornReq({ Id: this.recordId });
        this.hasNewbornValMsgReq = result;
    }

    /**
   * Method Name: callServer
   * Function: used for fetch all required dtails from apex that is being used fr transfer the case
   */
    async callServer() {
        this.inputParams.sCaseId = this.recordId;
        this.inputParams.bRSOFlag = false; // this will dynamic one 206 permission set comes in picture

        try {
            if (this.bisSwitchOn4828071 == true){
                this.isLegacyDeleteMember = await isLegacyDeleteMemberOrPolicy({ Id: this.recordId });
            }
            let response = await getCaseData({ pageInput: JSON.stringify(this.inputParams) });
            this.dbResponse = response;
            if(this.bisSwitchOn4932577 == true){
            if ((this.dbResponse.caseData.Case_Owner__c == this.LVGScreening || this.dbResponse.caseData.Case_Owner__c == this.Grrenbayqueue) && (this.dbResponse.caseData.Owner_Queue__c == this.LVGScreening || this.dbResponse.caseData.Owner_Queue__c == this.Grrenbayqueue) && !hasCRMS_400_Grievance_Appeals) {
                this.isButtonDisable = true;
            }
            }
            this.spinnerVisible = !(Object.keys(response).length > 0);
            this.profileName = this.dbResponse.caseCommentModal.sProfileName;
            if (this.isButtonDisable == true) {
                this.pageError = true;
                if(this.bisSwitchOn4932577 == true){
                this.showToastErrorOnLoad(this.caseLabels.AutoRouteToastMessage);
                }
                else{
                    if (this.bReassignToCreator == true) {
                    this.showToastErrorOnLoad('The case cannot be reassigned to creator because the Process Template is being auto routed');
                } else if (this.bReassignToSender == true) {
                    this.showToastErrorOnLoad('The case cannot be reassigned to sender because the Process Template is being auto routed');
                } else {
                    this.showToastErrorOnLoad('The owner cannot be changed because the Process Template is being auto routed');
                }
                }

                return;
            }else if(this.hasNewbornValMsgReq == true){
                this.pageError = true;
                this.showToastErrorOnLoad(this.newbornErrorMsg);
                return;
            }else if(this.isLegacyDeleteMember == true && this.bisSwitchOn4828071 == true){
                this.pageError = true;
                this.showToastErrorOnLoad(this.isLegacyDeleteMemberErrorMsg);
                return;
            } else {
                this.handleOnLoadPageErrorMsg();
            }
        } catch (err) {
            console.error("Error", err);
        }
    }

    /**
   * Method Name: handleOnLoadPageErrorMsg
   * Function: used for show all error msge on load of page to cover checkForErrors() method in classic
   */
    handleOnLoadPageErrorMsg() {
        const { CTCI_List__r = {} } = this.dbResponse.caseData;
        const { CTCI_List__r: { Classification__c = {} } = {} } = this.dbResponse.caseData;
        const { CTCI_List__r: { Intent__c = {} } = {} } = this.dbResponse.caseData;
        const { Interacting_With__c, Interacting_With_Name__c, OwnerId, Interacting_With_Type__c } = this.dbResponse.caseData;
        const { ownerObjName, currentUserID } = this.dbResponse.caseTransfer;
        const { bPharmacy } = this.oUserGroup;

        let onloadMsge = '';
        if (OwnerId && OwnerId !== currentUserID && ownerObjName && ownerObjName === 'User') {
            if (this.profileName === 'Customer Care Specialist' || (this.profileName === 'Humana Pharmacy Specialist' && hasCRMS_300_Humana_Pharmacy_Supervisor === undefined)) {
                onloadMsge += this.caseLabels.HUMCaseTransferError;
				this.pageError = true;
                this.showToastErrorOnLoad(onloadMsge);
                return;
            }
        }
        if (!(Object.keys(Classification__c).length > 0 && Classification__c !== null && Object.keys(Intent__c).length > 0 && Intent__c !== null)) {
            onloadMsge += this.caseLabels.CIError_ChangeCase_HUM;
			this.pageError = true;
            this.showToastErrorOnLoad(onloadMsge);
            return;
        }
        if ((Interacting_With_Type__c==null || Interacting_With_Type__c== undefined) || ((Interacting_With__c == null || Interacting_With__c == undefined) && (Interacting_With_Name__c == null || Interacting_With_Name__c == undefined ))) {
            onloadMsge += this.caseLabels.HUMSelectInteractionWith;
			this.pageError = true;
            this.showToastErrorOnLoad(onloadMsge);
        }
        if (this.dbResponse.caseTransfer.bcaseTransferAssistInfo && this.displayLabel == 'Change Owner') {
			this.showTransferAssistInfo = true;
            this.showCaseTransferAssistmsg = 'Please review Service Center, Department, Topic and User fields to ensure they are accurate prior to completing the transfer.';
        }
    }

    /**
   * Method Name: showToastErrorOnLoad
   * Function: used for show error in toast on load pf page
   */
    showToastErrorOnLoad(onloadMsge) {
        toastMsge("", onloadMsge, "error", "dismissable");
        this.closeForm();
    }

    /**
     * Method Name: handleChildCloseForm
     * Function: used for handle child components on parent
     */
    handleChildCloseForm(event) {
        this.showToastErrorOnLoad(event.detail);
    }

    /**
   * Method Name: saveTransferForm
   * Function: used for save and trnasfer the case and will fire on click of save of modal popup
   */
    saveTransferForm() {

        let dbCase = this.dbResponse.caseData; // this is database that comes from on load of page
        let caseCommentbody = this.template.querySelector('c-case-transfer-comments-form-hum').fetchDOMValues(); // fetch comment field value
        if(this.bReassignToCreator){// this if to make sure that creator is clicked and rtc value should be in comment
            caseCommentbody = {value: '\n#RTC'}; 
        }
        let caseTransferSection = this.template.querySelector('c-case-transfer-service-dept-hum').saveCaseTransferSection();
        let gAndA = this.template.querySelector('c-case-transfer-gna-hum').saveGandA();
        let texas = this.template.querySelector('c-case-transfer-texas-hum').saveTexas();
		let commentIssue = this.template.querySelector('c-case-transfer-comments-form-hum').fetchCommentIssue();
        let commntResolution = this.template.querySelector('c-case-transfer-comments-form-hum').fetchCommentResolution();

        if (!this.handleRequiredValidations(caseTransferSection)) { // this if to handle required field validation
            return;
        }

        if (!this.handleTopicFieldValidation(caseTransferSection)) { // this if to handle topic validation
            return;
        }

        if (this.userId && this.userName) { // this if to handle user validation if user is not valid in user field
            this.showPageMsg(true, '');
        } else if (this.manualEnterUser !== "") { // means there is some dummy value present
            this.showPageMsg(false, 'Please enter correct user');
            return;
        }
        // from here all criteria based kind validations
        if (!this.template.querySelector('c-case-transfer-comments-form-hum').handleLogCodeValidation()) { // this if to hadle log ocde validation for HP
            this.showPageMsg(false, 'Humana Pharmacy Log Code: You must enter a value');
            return;
        } else {
            this.showPageMsg(true, '');
        }

        if (!this.template.querySelector('c-case-transfer-comments-form-hum').handleCommentValidation()) { // this if to hadle comment validation for HP
            this.showPageMsg(false, 'Case Comment: You must enter a value');
            return;
        } else {
            this.showPageMsg(true, '');
        }
		let bMedicareCalls = false;
        let validationMSg = this.template.querySelector('c-case-transfer-comments-form-hum').handleCommentValidation();
        let valMsgs = validationMSg.split("::");
        if(valMsgs[1]!=''){
            this.showPageMsg(false,valMsgs[1]);
            return;
        }
        if(valMsgs[0]=="MCC"){
            bMedicareCalls = true;
        }
        if (!this.handleRequiredValidations(gAndA)) {
            return;
        } else {
        if (!this.handleComplaintReasonValidations(gAndA)) {
            return;
        }
        if (!this.handleComplaintAssociatedPlanValidations(gAndA)) {
            return;
        }
        if (!this.handleComplaintTypeValidations(gAndA)) {
            return;
        }
         if (!this.handleGnAReasonValidations(gAndA)) {
            return;
        }
        if (!this.handleGnARightsComplaintValidations(gAndA)) {
            return;
        }
        if (!this.handleGnARightsComplaintMHKValidations(gAndA)) {
            return;
            }
        }

        let updatedCase = { ...dbCase, ...caseTransferSection, ...gAndA, ...texas }; //collect case data field from all child componen and merge into one case variable
        this.saveAndTransferCase(updatedCase, caseCommentbody,bMedicareCalls,commentIssue,commntResolution);
    }

    handleComplaintReasonValidations(gAndA) {
        if (gAndA === 'COMPLAINT_REASON_VALIDATION_FAILED') {
            this.showPageMsg(false, 'Complaint Reason: You must enter a value');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }

    }
    handleComplaintAssociatedPlanValidations(gAndA) {
        if (gAndA === 'COMPLAINT_ASSOCIATED_PLAN_VALIDATION_FAILED') {
            this.showPageMsg(false, 'Complaint option selected does not match the policy associated to the case, please review the Help Hover Over for guidance');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }
    handleComplaintTypeValidations(gAndA) {
        if (gAndA === 'COMPLAINT_TYPE_VALIDATION_FAILED') {
            this.showPageMsg(false, 'Complaint Type drop-down is required for this policy. Product =MED. Value must be selected in order to save');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }
    handleGnAReasonValidations(gAndA) {
        if (gAndA === 'GnA_REASON_VALIDATION_FAILED') {
            this.showPageMsg(false, 'G&A Reason: You must enter a value');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    } 
    handleGnARightsComplaintValidations(gAndA) {
        if (gAndA === 'GnA_RIGHT_AND_COMPLAINT_VALIDATION_FAILED') {
            this.showPageMsg(false, 'The case cannot contain both Complaint and G&A Rights Given. Please choose the correct indicator or create 2 separate cases.');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }

    handleGnARightsComplaintMHKValidations(gAndA) {
        if (gAndA === 'GnA_RIGHT_AND_COMPLAINT_MHK_VALIDATION_FAILED') {
            this.showPageMsg(false, 'The G&A Rights and Complaint Given value can only be NO for MHK case.');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }

    /**
   * Method Name: handleRequiredValidations
   * Function: used for hadnle all required feilds validations
   */
    handleRequiredValidations(caseTransferSection) {
        // this if to handle serivce center and department required feidls validation
        if (caseTransferSection === 'VALIDATION_FAILED') {
            this.showPageMsg(false, 'Required fields are highlighted in red');
            return false; // means form is invalid and some required fields are missing
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }

    /**
   * Method Name: handleTopicFieldValidation
   * Function: used for hadnle topic validation if topic is enabled and left blank
   */
    handleTopicFieldValidation(caseTransferSection) {
        if (caseTransferSection === 'TOPIC_VALIDATION_FAILED') {
            this.showPageMsg(false, 'You must select a topic before transitioning');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }

    /**
   * Method Name: saveAndTransferCase
   * Function: used for save and trnasfer the case
   */
    async saveAndTransferCase(updatedCase, caseCommentbody,bMedicareCalls,commentIssue,commntResolution) {
        //database operation here
        try {
            this.spinnerVisible = true;
            let refinedCase = {};
            let fildsReqToUpdateCase = ['Id', 'Service_Center__c', 'Department__c', 'Topic__c', 'Priority', 'Transfer_Reason__c', 'Status', 'Escalation_Indicator__c','Escalation_Indicator_Date__c',
                'Case_Owner__c', 'Language_Preference__c', 'OwnerId', 'G_A_Rights_Given__c', 'G_A_Reason__c', 'Complaint__c', 'Complaint_Type__c',
                'Complaint_Reason__c', 'Type', 'User__c', 'Classification_Type__c', 'Owner_Queue__c', 'Transferred_From__c', 'Transferred_Date__c'];
            Object.keys(updatedCase).filter((item) => {
                if (fildsReqToUpdateCase.includes(item)) {
                    refinedCase[item] = updatedCase.hasOwnProperty(item) ? updatedCase[item] : '';
                }
            });
            let saveParams = {};
            saveParams.oCaseRec = refinedCase; // this is update params case that required to perform DML
            saveParams.caseComment = (bMedicareCalls==true)?commentIssue.value+commntResolution.value:caseCommentbody.value; // value from comment field
            saveParams.ownerId = this.userId;  // the id comes from user selection from user search box
            saveParams.ownerName = this.userName; // the name comes from user selection from user search box
            saveParams.mapServCenDeptWorkQueueSetup = this.dbResponse.caseTransfer.mapUniqueServCenDeptQueueSetup;

            let caseTransfered = await saveCaseTransfer({ caseTranferInput: JSON.stringify(saveParams) });
            if (caseTransfered && caseTransfered !== 'NOT_SUCCESS') { // if case successfully transfered then save case comment
                toastMsge("", 'Case ' + `${updatedCase.CaseNumber}` + ' was successfully transferred ', "success", "dismissable");

                if ((!this.oUserGroup.bPharmacy) && ((caseCommentbody && caseCommentbody.value) || 
                (commentIssue && commentIssue.value && commntResolution && commntResolution.value))) {// if to make sure if comment is inserted then only process the save comment functionality ofr non-hp
                    this.template.querySelector('c-case-transfer-comments-form-hum').sendRequestToEpost(); // save case comment after Epost if pharmacy
                } else if (this.oUserGroup.bPharmacy) {
                    this.template.querySelector('c-case-transfer-comments-form-hum').sendRequestToEpost();
                }
                else if(this.iscreatecase == true)
                {
                    const selectedEvent = new CustomEvent("handlesaveandnavigate", {
                        detail: this.recordid
                      });
                    this.dispatchEvent(selectedEvent);
                }
                else {
                    this.spinnerVisible = false;
                    this.closeForm();
                }
            }
        } catch (error) {
            this.spinnerVisible = false;
            console.log('error in casetrnasfercontainer save', error);
            let errormsg = error.hasOwnProperty('body') && error.body.hasOwnProperty('message') ? error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"') : error;
            toastMsge("", errormsg, "error", "dismissable");
        }
    }

    /**
   * Method Name: closeForm
   * Function: will fire when user click on cancel and the we are firing event on caseTransferchangeOwnerAura component
   */
    closeForm() {

        this.dispatchEvent(new CustomEvent("closeaction", {
            detail: ''
        }));
    }

    /**
   * Method Name: saveComments
   * Function: will fire when caseComment data is save o backend and now case comment timeline should show latest data
   */
    saveComments(event) {
        // this if is to make sure that case comment is saved in DB and now refresh the timemeline comment using pubsub
        if (event.detail.saveMsg === 'Comment inserted') {
            this.refreshTimeline();
        }
        this.spinnerVisible = false;
        this.closeForm();
		if(this.recordid != '')   
		{            
			const selectedEvent = new CustomEvent("handlesaveandnavigate", {   
			detail: this.recordid    
			});         
			this.dispatchEvent(selectedEvent);  
		}
    }
    refreshTimeline() {
        pubsub.fireEvent(this.pageRef, 'case-transfer-container-hum', this.recordId);
    }

    /**
 * Method Name: handlefetchuserfieldvalue
 * Function: will fire on click of save button from caseTransferServceDept component and gives user value present in user field
 */
    handlefetchuserfieldvalue(event) {
        this.manualEnterUser = event.detail;
    }

    /**
  * Method Name: handleSelectedUser
  * Function: will fire there is some value in user search box field
  */
    handleSelectedUser(event) {
        this.userId = event.detail.userId;
        this.userName = event.detail.userName;
    }

    /**
 * Method Name: removeInfoMsgeOnCross
 * Function:this method will run when user click on cross icon of info msge icon
 */
    removeInfoMsgeOnCross() {
        let event = { detail: false };
        this.removeInfoMessage(event);
    }

    /**
 * Method Name: removeInfoMessage
 * Function:remove infromational mseesage from UI 
 */
    removeInfoMessage(event) {
        this.showInfoMsges = event.detail;
        this.guidanceinformationalMsge = '';
        this.topicinformationalMsge = '';
    }

    /**
 * Method Name: showInfoMessage
 * Function: show infromational mseesage from UI 
 */
    showInfoMessage(event) {
        this.showInfoMsges = event.detail.showInforMsge;
        this.guidanceinformationalMsge = event.detail.guidanceInfo;
        this.topicinformationalMsge = event.detail.topicInfo;
    }

    /**
    * show top page error msge
    * @param {*} isValid - true means there is no any kind of error present on page after verify all validations criteira
    *  @param {*} headerMsg - the top header error msge
    */
    showPageMsg(isValid, headerMsg) {
        const errHeader = this.template.querySelector(".page-error-message");
        if (!isValid) {
            errHeader.scrollIntoView({
                block: 'center',
                behavior: 'smooth',
                inline: "center"
            });
            errHeader.innerHTML =
                '<div class="slds-m-horizontal_small slds-p-around_small hc-error-header" style="color:white; font-size:20px; background: #c23934;border-radius:0.3rem">Review the error on this page.</div>' +
                '<p class="slds-p-horizontal_large slds-p-vertical_small" style="color:#c23934; font-size: 13px;">' +
                headerMsg +
                "</p>";
        } else {
            errHeader.innerHTML = " ";
        }
    }
	/*Method to fetch the switch details*/
	getSwitchData() {
        return new Promise((resolve, reject) => {
            isCRMFunctionalityONJS({ sStoryNumber: ['4932577','4828071'] })
                .then(result => {
                    this.bisSwitchOn4932577 = result['4932577'];
                    this.bisSwitchOn4828071 = result['4828071'];
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })
    }
}