/*******************************************************************************************************************************
LWC JS Name : closeCaseHum.js
Function    : This JS serves as helper to closeCaseHum.html 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Gowthami Thota                                           06/20/2022                   initial version
* Prasuna Pattabhi                                        31/10/2022          US-3934433 Close Case Medicare Case Calls Cooments
* Prasuna Pattabhi                                        11/02/2022          US-3934433 Error Message Typo Fix
* Kalyani Pachpol                                         2/17/2022                     US-4256639
* Pooja Kumbhar                                           05/02/2023          US - 4501560  Case Management: MF 26340 - Toast Error Message on click of buttons Auto Routed Templates- CASE DETAIL PAGE
* Kalyani Pachpol                                         05/04/2023                  US-4486808
* Prasuna Pattabhi                                         07/27/2023                  US-4876326 Restrict the User to Close and Transfer the Case without attaching Babybot for Medicaid from Case Details page
* Pooja Kumbhar                                     09/14/2023            US - 4900963 - T1PRJ0865978 - C06; Case Mgt- Lightning - Disable Save and Transfer buttons for Complaint cases that auto-route
* Pooja Kumbhar                                     09/14/2023            US - 4932577 - T1PRJ0865978 - C06; Case Mgt Lightning - Update Toast Error Message on click of buttons Auto Routed Templates- CASE DETAIL PAGE
* Vani Shrivastava                                  09/15/2023         US 4918290: T1PRJ0865978 - C06- Case Management- Close Case button should have correct toast message verbiage 
* Jasmeen Shangari                                  09/27/2023         DF-8159:close case button is not working as expected detailed explanation mentioned in below descriptiion
* Apurva Urkude                                     10/27/2023         US 4932577: T1PRJ0865978 - Added swicth on JS file
* Nilesh Gadkar                                     10/13/2023             US 4918290: T1PRJ0865978 - Added Switch 
* Santhi Mandava                                    02/28/2024         User Story 5231359: T1PRJ1132745 - MF 28326 - C06; Case Management - Oklahoma MCD Lighting - Auto Routing Feature for State of Issue (MCD OK G&A 1.1)
*********************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import getCaseData from '@salesforce/apex/CaseClose_LC_HUM.fetchCaseData';
import getOpenTaskCount from '@salesforce/apex/CaseClose_LC_HUM.CheckForOpenTaskCount';
import closeCaseSave from '@salesforce/apex/CaseClose_LC_HUM.CaseCloseSave';
import saveCaseClose from '@salesforce/apex/CaseClose_LC_HUM.saveCaseClose';
import { getUserGroup, toastMsge } from 'c/crmUtilityHum';
import { getCaseLabels } from "c/customLabelsHum";
import pubsub from 'c/pubSubHum';
import { CurrentPageReference,NavigationMixin } from 'lightning/navigation';
import { ShowToastEvent } from "lightning/platformShowToastEvent";
import { getRecordNotifyChange } from 'lightning/uiRecordApi';
import { CloseActionScreenEvent } from 'lightning/actions';
import { loadStyle } from "lightning/platformResourceLoader";
import modal from "@salesforce/resourceUrl/custommodal";
import hasCRMS_300_HP_Supervisor_Custom from '@salesforce/customPermission/CRMS_300_HP_Supervisor_Custom';
import checkMedicareCalls from "@salesforce/apex/CaseDetails_LC_Hum.checkMedicareCalls";
import getCCMedicareCalls from "@salesforce/apex/CaseDetails_LC_Hum.getCCMedicareCalls";
import CheckCaseTemplates from '@salesforce/apex/CaseDetails_LC_Hum.CheckCaseTemplates';
import isNewbornReq from '@salesforce/apex/CaseDetails_LC_Hum.isNewbornReq';
import NEWBORN_ERROR_MSG from '@salesforce/label/c.NEWBORN_ERROR_MSG';
import hasCRMS_400_Grievance_Appeals from '@salesforce/customPermission/CRMS_400_Grievance_Appeals_Custom';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';

export default class CloseCaseHum extends NavigationMixin(LightningElement) {
    @wire(CurrentPageReference) pageRef;
    @api recordId; // this will be caseid

    @track dbResponse; // this will use to fetch Database data
    @track inputParams = {};   // this variable used to send input params to apex to fetch relevant data on transfer page
    @track oUserGroup = getUserGroup();
    @track caseLabels = getCaseLabels();
    @track profileName; // used to store current logged in user prfile name
    
    @track showInfoMsges = false; //this variable will take care of hiding and showing information message on UI
    @track spinnerVisible = true;
    restrictrendercalback = true;
    validationOccur = false;
    OpenTaskCount ;
    lstErrors = [];
    isSupervisor;
	lstHpCases =['Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'];
    validateMedicareCalls = false;
    hasMediCCUpdate = true;
    isButtonDisable = false;
    hasNewbornValMsgReq = false;
    newbornErrorMsg = NEWBORN_ERROR_MSG;
    @track bisSwitchOn4932577 = false;
    @track bisSwitchOn4918290 = false;
    isCloseButtonDisable = false;

    connectedCallback() {
        loadStyle(this, modal);       
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
            if (result.bdisablecloseCancel == 'true') {
                this.isButtonDisable = true;
            }
            const objSwitch = await isCRMFunctionalityONJS({sStoryNumber: ['5231359']});
            if(objSwitch && result.disableClose) this.isCloseButtonDisable = true;
        }
        return this.isButtonDisable;
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
            let response = await getCaseData({ pageInput: JSON.stringify(this.inputParams) });
            this.dbResponse = response;
            if(this.bisSwitchOn4932577 == true)
			{
            if ((this.dbResponse.caseData.Case_Owner__c == 'LV G and A and Correspondence Screening' || this.dbResponse.caseData.Case_Owner__c == 'Green Bay Grievance and Appeals') && (this.dbResponse.caseData.Owner_Queue__c == 'LV G and A and Correspondence Screening' || this.dbResponse.caseData.Owner_Queue__c == 'Green Bay Grievance and Appeals') && !hasCRMS_400_Grievance_Appeals) {
                this.isButtonDisable = true;
            }
            }
            this.spinnerVisible = !(Object.keys(response).length > 0);
            this.profileName = this.dbResponse.caseCommentModal.sProfileName;
            this.handleOnLoadPageErrorMsg();
        } catch (err) {
            console.error("Error", err);
        }
    }

    async handleOnLoadPageErrorMsg(){
        const {Classification_Id__c} = this.dbResponse.caseData.hasOwnProperty("Classification_Id__c") ? this.dbResponse.caseData : "";   
        const {Intent_Id__c} = this.dbResponse.caseData.hasOwnProperty("Intent_Id__c") ? this.dbResponse.caseData : "";
        const { OwnerId,Owner_Queue__c,Complaint__c,G_A_Rights_Given__c } = this.dbResponse.caseData;
        const { sCurrentUserId } = this.dbResponse;
        const { sCurrentUserQueue } = this.dbResponse;

        this.isSupervisor = (this.profileName ==='Humana Pharmacy Specialist' && hasCRMS_300_HP_Supervisor_Custom) || this.profileName === 'Customer Care Supervisor' ? true : false;
        
        const result =  await getOpenTaskCount({sCaseID: this.recordId});
		let medicareCCUpdateRequired = true;
        const bMedicareCalls = await checkMedicareCalls({ memplanId: this.dbResponse.caseData.Member_Plan_Id__c, sOrigin : this.dbResponse.caseData.Origin});
        if(bMedicareCalls == true){
            let recordTypeName = this.dbResponse.caseData.RecordType.Name;
            if(recordTypeName != 'Medicare Case' && recordTypeName != 'Closed Medicare Case' && !this.lstHpCases.includes(recordTypeName)
            && (this.profileName === "Customer Care Specialist" || this.profileName === "Customer Care Supervisor")){
                const mediCCUpdate = await getCCMedicareCalls({caseId:this.dbResponse.caseData.Id});
                medicareCCUpdateRequired = mediCCUpdate;
            }
        } 
        this.validateMedicareCalls = bMedicareCalls;
        this.hasMediCCUpdate = medicareCCUpdateRequired; 

            let onloadMsge = '';
            
            if (this.isButtonDisable == true) {
            this.closeForm();
            this.spinnerVisible = false;
            if(this.bisSwitchOn4932577 == true){
            onloadMsge += this.caseLabels.AutoRouteToastMessage;
			}
			else{
			onloadMsge += 'The case cannot be closed because the Process Template is being auto routed';
			}
            this.showToastMsg("", onloadMsge, "error", "dismissible");
            }else if(this.isCloseButtonDisable){
                this.closeForm();
                this.spinnerVisible = false;
                this.showToastMsg("", "The case cannot be closed because the QAA Process Template has not been attached" , "error", "dismissible");
            }else if(this.hasNewbornValMsgReq == true){
                this.closeForm();
                this.spinnerVisible = false;
                onloadMsge += this.newbornErrorMsg;
                this.showToastMsg("", onloadMsge, "error", "dismissible");
            }else if (!this.isSupervisor && OwnerId && OwnerId !== sCurrentUserId){
                    this.closeForm();
                    this.spinnerVisible = false;
                    onloadMsge += this.caseLabels.HumCaseCloseOwnerError;
                    this.showToastMsg("",onloadMsge,"error","dismissible");
            }
            else if(result === 'TRUE' ){
				if(this.dbResponse.caseData.RecordType.Name === 'HP Member Case'){
				this.validationOccur = true;
				}
				else{
					this.closeForm();
					this.spinnerVisible = false;
					onloadMsge += this.caseLabels.HumCaseCloseWorkTaskError;
					this.showToastMsg("", onloadMsge ,"error","dismissible");
				}                
            }
            else if(result == 'FALSE'  && Classification_Id__c != null && Intent_Id__c !=null && G_A_Rights_Given__c !=null && Complaint__c !=null && (
                    (bMedicareCalls == true && medicareCCUpdateRequired == true) || bMedicareCalls == false)) {
                if(this.dbResponse.caseData.RecordType.Name === 'HP Member Case'){
					this.validationOccur = true;
				}
				else{
					this.closeCase();                        
				}
            }
            else if(Classification_Id__c == null || Intent_Id__c ==null || G_A_Rights_Given__c ==null || Complaint__c == null || (bMedicareCalls == true && medicareCCUpdateRequired == false))
            this.validationOccur = true;
    }

    async closeCase(){
        try{
        this.spinnerVisible = true;
        const { CaseNumber } = this.dbResponse.caseData;
            let caseUpdated = await closeCaseSave({sCaseID: this.recordId});
            if (caseUpdated !== 'NOT_SUCCESS'){
                this.closeForm();
                this.spinnerVisible = false;
                getRecordNotifyChange([{ recordId: this.recordId }]);
                if(this.bisSwitchOn4918290 == true) {
                    this.showToastMsg("",'Case ' + `${CaseNumber}` + ' was successfully closed ',"success","dismissible");
                } else {
                    this.showToastMsg("",'Case ' + `${CaseNumber}` + ' was successfully saved',"success","dismissible");
                }
            }
        }
        catch(error){
            console.log('error in closeCase', error);
        }
    }

    /**
     * Generiic method to handle toast messages
     * @param {*} strTitle 
     * @param {*} strMessage 
     * @param {*} strStyle 
     */
     showToastMsg(strTitle, strMessage, strStyle, strMode) {
        this.dispatchEvent(
            new ShowToastEvent({
                title: strTitle,
                message: strMessage,
                variant: strStyle,
                mode: strMode
            })
        );
    }

    /**
     * Method Name: handleChildCloseForm
     * Function: used for handle child components on parent
     */
    handleChildCloseForm(event) {
        this.showToastErrorOnLoad(event.detail);
    }

    /**
   * Method Name: saveCaseForm
   * Function: used to save the case and will fire on click of save of modal popup
   */
    
     handleSave(){
        let CTCI = this.template.querySelector('c-classification-intent-modal-hum').saveCTCI();
        let gAndA = this.template.querySelector('c-case-gna-component-hum').saveGandA();
        let texas = this.template.querySelector('c-case-texas-letter-component-hum').saveTexas();
        let caseCommentbody = this.template.querySelector('c-case-comments-form-hum').fetchCommentValue(); // fetch comment field value
        let logCode = this.template.querySelector('c-case-comments-form-hum').fetchLogCode();
        let dbCase = this.dbResponse.caseData; // this is database that comes from on load of page
        //from here all criteria based kind validations
        
        if(!this.handleRequiredValidationsCTCI(CTCI)){
            return;
        }
        else if(!this.handleRequiredValidations(gAndA)){
            return;
        }

        else if (!this.handleGnAReasonValidations(gAndA)) {
            return;
        }

        else if (!this.handleComplaintTypeValidations(gAndA)) {
            return;
        }
		if (!this.template.querySelector('c-case-comments-form-hum').handleLogCodeValidation()) {  // this.showPageMsg(false, 'Humana Pharmacy Log Code: You must enter a value');
            return;
        } else {
            this.showPageMsg(true, '');
        }

        if (!this.template.querySelector('c-case-comments-form-hum').handleCommentValidation()) { 
            return;
        } else {
            this.showPageMsg(true, '');
        }
        let validationMSg = this.template.querySelector('c-case-comments-form-hum').handleCommentValidation();
        let valMsgs = validationMSg.split("::");
        if(valMsgs[1]!=''){
            return;
        }
		let commentsIssue = null;
        let commentsResolution = null;
        if(this.validateMedicareCalls == true && this.hasMediCCUpdate == false){
            commentsIssue = this.template.querySelector('c-case-comments-form-hum').fetchCommentIssue();
            commentsResolution = this.template.querySelector('c-case-comments-form-hum').fetchCommentResolution();
            let medicareCallsMsg = this.template.querySelector('c-case-comments-form-hum').checkMedicareCase();
            if(medicareCallsMsg.length>4){
                this.showPageMsg(false, medicareCallsMsg.substring(4,medicareCallsMsg.length));
                return;
            }            
        }
        let updatedCase = { ...dbCase, ...CTCI, ...gAndA, ...texas }; //collect case data field from all child componen and merge into one case variable
        this.saveAndCloseCase(updatedCase,caseCommentbody,logCode,commentsIssue,commentsResolution,this.validateMedicareCalls);
    }
    
    handleComplaintTypeValidations(gAndA) {
        if (gAndA=== 'COMPLAINT_TYPE_REASON_VALIDATION_FAILED') {
            
            this.showPageMsg(false, 'These required fields must be completed: Complaint Reason,Complaint Type.');
            return false;
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }
    handleGnAReasonValidations(gAndA) {
        if (gAndA === 'GnA_REASON_VALIDATION_FAILED') {
            this.showPageMsg(false, 'These required fields must be completed: G&A Reason.');
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
     handleRequiredValidationsCTCI(CTCI) {
        // this if to handle classification and intent validation
        if (CTCI === 'CTCI_REQUIRED_FIELD_VALIDATION_FAILED') {
            this.showPageMsg(false, 'These required fields must be completed: Classification,Intent.');
            return false; // means form is invalid and some required fields are missing
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }

    handleRequiredValidations(gAndA) {
        // this if to handle serivce center and department required feidls validation
        if (gAndA === 'REQUIRED_FIELD_VALIDATION_FAILED') {
            this.showPageMsg(false, 'These required fields must be completed: G&A Rights Given, Complaint.');
            return false; // means form is invalid and some required fields are missing
        } else {
            this.showPageMsg(true, '');
            return true;
        }
    }

    /**
   * Method Name: saveAndCloseCase
   * Function: used for save and close the case
   */
    async saveAndCloseCase(updatedCase,caseCommentbody,logCode,commentsIssue,commentsResolution,bMedicareCalls) {
        //database operation here
        try {
            this.spinnerVisible = true;
            let refinedCase = {};
            let fildsReqToUpdateCase = ['Id','Status', 'Intent_Id__c','Classification_Id__c','G_A_Rights_Given__c', 'G_A_Reason__c', 'Complaint__c', 'Complaint_Type__c',
                'Complaint_Reason__c','Language_Preference__c'];
            Object.keys(updatedCase).filter((item) => {
                if (fildsReqToUpdateCase.includes(item)) {
                    refinedCase[item] = updatedCase.hasOwnProperty(item) ? updatedCase[item] : '';
                }
            });
            let saveParams = {};
            saveParams.oCaseRec = refinedCase; // this is update params case that required to perform DML
            saveParams.caseComment = caseCommentbody!=null?caseCommentbody.value:''; // value from comment field
            saveParams.humLogCode = logCode;
            let caseClosed = await saveCaseClose({ caseCloseInput: JSON.stringify(saveParams) });
            if (caseClosed && caseClosed !== 'NOT_SUCCESS') { // if case successfully transfered then save case comment
                getRecordNotifyChange([{ recordId: this.recordId }]);
                if(this.bisSwitchOn4918290 == true) {
                toastMsge("", 'Case ' + `${updatedCase.CaseNumber}` + ' was successfully closed ', "success", "dismissible");
                } else {
                    toastMsge("", 'Case ' + `${updatedCase.CaseNumber}` + ' was successfully saved ', "success", "dismissible");
                }
                this.spinnerVisible = false;
                this.closeForm();

                if ((!this.oUserGroup.bPharmacy) && ((caseCommentbody && caseCommentbody.value) || (bMedicareCalls==true))) {// if to make sure if comment is inserted then only process the save comment functionality ofr non-hp
                    this.template.querySelector('c-case-comments-form-hum').sendRequestToEpost(); // save case comment after Epost if pharmacy
                } else if (this.oUserGroup.bPharmacy) {
                    this.template.querySelector('c-case-comments-form-hum').sendRequestToEpost();
                }
                else {
                    this.spinnerVisible = false;
                    this.closeForm();
                }
            }
            else {
                this.spinnerVisible = false;
                this.closeForm();
            }
        } catch (error) {
            this.spinnerVisible = false;
            this.closeForm();
            console.log('error in caseclose save', error);
            let message = error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"');
            //let errormsg = error.hasOwnProperty('body') && error.body.hasOwnProperty('message') ? error.body.message.replace(/&amp;/g, "&").replace(/&quot;/g, '"') : error;
            toastMsge("", message, "error", "dismissible");
        }
    }

    /**
   * Method Name: closeForm
   * Function: will fire when user click on cancel
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
    }
    refreshTimeline() {
        pubsub.fireEvent(this.pageRef, 'close-case-hum', this.recordId);
    }

    /**
    * show top page error msge
    * @param {*} isValid - true means there is no any kind of error present on page after verify all validations criteira
    *  @param {*} headerMsg - the top header error msge
    */
    showPageMsg(isValid, headerMsg) {
        const errHeader = this.template.querySelector(".page-error-message");
        if (!isValid) {
            errHeader.innerHTML =
                '<div class="slds-m-horizontal_small slds-p-around_small hc-error-header" style="color:white; font-size:20px; background: #c23934;border-radius:0.3rem">Review the error on this page.</div>' +
                '<p class="slds-p-horizontal_large slds-p-vertical_small" style="color:#c23934; font-size: 13px;">' +
                headerMsg +
                "</p>";
        } else {
            errHeader.innerHTML = " ";
        }
    }
handleHPCaseComment(event) {
        this.setHPCaseCommentView =event.detail.showhpcomment;
		let setHPLogCode= event.detail.showlogcode;
        let setChangeRecordType = event.detail.showrecordtype;
        if(this.template.querySelector('c-case-comments-form-hum') !=null)
        {
        this.template.querySelector('c-case-comments-form-hum').casecommentvisibility(this.setHPCaseCommentView,setHPLogCode,setChangeRecordType);
        }
    }
		handleHPLogCode(event)
    {
        this.setDefaultLogCode= event.detail.showlogcodes;
        this.template.querySelector('c-case-comments-form-hum').hplogcodeclassification(this.setDefaultLogCode);
    }	
	/*Method to fetch the switch details*/
    getSwitchData() {
        return new Promise((resolve, reject) => {
            isCRMFunctionalityONJS({ sStoryNumber: ['4932577','4918290'] })
                .then(result => {                  
                    this.bisSwitchOn4932577 = result['4932577'];
                    this.bisSwitchOn4918290 = result['4918290'];
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })
    }

}