/*******************************************************************************************************************************
LWC JS Name : qsCreateCaseHum.js
Function    : This JS serves as controller to qsCreateCaseHum.html. 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                      07/12/2022                  initial version(azure #)
* Prasuna Pattabhi										08/16/2022					3708098 - Update Existing Case Comments
* Prasuna Pattabhi										08/17/2022					3272650 - Associate Interaction to Case
* Prasuna Pattabhi										08/22/2022					3272650 - Associate Interaction to Case - Missing Code Added
* Pooja Kumbhar											09/12/2022				    US:3705153 - Lightning - Quick Start - RCC Specific - Task Information Section 
* Pooja Kumbhar									        10/06/2022				    DF-6290: QA Lightning US3705153 The name of the Task tab is not displayed correctly
* Disha Dole 											20/01/2023 				    US:4085171 - T1PRJ0865978 - C06, Lightning-Case Management- Quick Start-Associate to Policy, alignment & display issue fixes
* Jasmeen Shangari                                      03/06/2023                  Fix for DF-7322-Auto routing failed when user create the case from quickstart
* Pooja Kumbhar	                                        06/01/2023                     US:4583426 - T1PRJ0865978 - C06- Case Management - MF 26447 - Provider QuickStart- Implement Callback Number, G&A, Duplicated C/I logic
* Pooja Kumbhar	                                        06/13/2023                     DF - 7751 - 4583426 - Lightning - T1PRJ0865978 - defect fix
* Pooja Kumbhar	                                        06/28/2023                     US4773013 - T1PRJ0865978 - INC2395527 - Lightning Command Center RAID#041: every time user  getting a call creating a task automatically.
* Prasuna Pattabhi              07/13/2023                          US 4752577 - Quick Start Update Comments Button not populating in CRM Case
* Prasuna Pattabhi              07/18/2023                          US 4752577 - Quick Start Update Comments Button not populating in CRM Case
* Prasuna Pattabhi              07/20/2023                          US 4752577 - Null check
* Jasmeen Shangari              07/28/2023                          US 4850274 - Addressed Interaction issue after creating process
*********************************************************************************************************************************/

import { LightningElement,track,wire,api } from 'lwc';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import {invokeWorkspaceAPI,openLWCSubtab} from 'c/workSpaceUtilityComponentHum';
import { publish, MessageContext,subscribe, unsubscribe } from 'lightning/messageService';
import COMMENTS_CHANNEL from '@salesforce/messageChannel/caseCommentsLMSChannel__c';
import COMMENTS_CHANNEL_REQ from '@salesforce/messageChannel/caseCommentsLMSReqChannel__c';
import validateCreateCase from '@salesforce/apex/QuickStart_CreateCase_LC_Hum.validateCreateCase';
import getCaseNumber from '@salesforce/apex/QuickStart_CreateCase_LC_Hum.getCaseNumber';
import insertCase from '@salesforce/apex/QuickStart_CreateCase_LC_Hum.insertCase';
import saveTask from '@salesforce/apex/QuickStart_LC_Hum.saveTask';
import { NavigationMixin } from 'lightning/navigation';

export default class QsCreateCaseHum extends NavigationMixin(LightningElement) {
    
    subscription;
    jsonCaseComment;    
    additionalInfo = {};
    oNewCase = {};
    updateCommentsData = {};
    btnName = 'Associate To Policy';
    successMsg = '';
    bIsAccount = false;
    isShowLoading = false;
    interactionId;
    primaryTabId;
    recordId;
    messageToUser;
    savedCaseId;
    caseID;
    eventTriggered = false;
    errorDispatchEvent = false;
    eventTriggeredName = '';
    bIsHPCase = 'false';
    strCaseData = '';
	serviceCenter;
    department;
    workqueuename;
    username;
    userId;
    taskType;
    taskDuedate;
    taskComment;
    iscreateTask;
    sTaskComment;
    sTaskInformation = [];

    @api disable = false;

    @api get casedetails(){
      return {};
    }
    set casedetails(data) {
      if(this.eventTriggered){

        this.oNewCase = {};
        this.additionalInfo = {};
		
		this.serviceCenter = data.sQueueServiceCenter;
        this.department = data.sQueueDepartment;
        this.workqueuename = data.sQueueName;
        this.username = data.sUserName;
        this.userId = data.userId;
		
        this.oNewCase.Owner_Queue__c = data.sQueueName;
        this.oNewCase.Case_Owner__c = data.sUserName;
        this.oNewCase.Classification_Type__c = data.sClassificationType;
        this.oNewCase.Service_Center__c = data.sQueueServiceCenter;
        this.oNewCase.Department__c = data.sQueueDepartment;
        this.oNewCase.Classification_Id__c = data.classificationId;
        this.oNewCase.Intent_Id__c = data.intentId;;
        this.oNewCase.G_A_Reason__c = data.sGnARightsReason;
        this.oNewCase.Complaint_Reason__c = data.sComplaintReason;
        this.oNewCase.Complaint_Type__c = data.sComplaintType;
        this.oNewCase.G_A_Rights_Given__c = data.sGnARights;
        this.oNewCase.Complaint__c = data.sComplaint;
        this.oNewCase.CTCI_List__c = data.ctciId;

        this.additionalInfo.userId = data.userId;
        this.additionalInfo.sProfileName = data.sProfileName;
        this.additionalInfo.classificationTypeId = data.classificationTypeId;
        this.additionalInfo.bAllowMultipleCase = ''+data.bAllowMultipleCase;
        this.additionalInfo.medicarePartCPartDValue = data.sMedicarePartCPartDValue;
        this.additionalInfo.bProviderUser = data.bProviderUser;  
        this.additionalInfo.classificationLabel = data.classificationName;
        this.additionalInfo.intentLabel = data.intentName; 
		this.additionalInfo.classificationId = data.classificationId;
        this.additionalInfo.intentId = data.intentId;
			
        this.jsonCaseComment = data.sCaseComment;
		this.taskType = data.tasktype;
        this.taskDuedate = data.duedate;
        this.iscreateTask = data.iscretetask;
        this.sTaskComment = data.staskcomment;
        
        if(this.eventTriggeredName=='associateCase'){
          this.associateCase();
        }else if(this.eventTriggeredName == 'updateComments') {
                this.updateComments();
        }

        this.eventTriggeredName = ''; 
        this.eventTriggered = false;
        this.errorDispatchEvent = false;  

      }      
    }
    @api get errorConfirmation(){
      return {};
    }
    set errorConfirmation(data){
      if(this.errorDispatchEvent){        
        if(this.eventTriggeredName=='policyConfirmation'){
          this.validatePolicyConfirmation(data.response);
        }else if(this.eventTriggeredName=='classificationConfirmation'){
          this.validateClassificationConfirmation(data.response);
        }else if(this.eventTriggeredName == 'caseConfirmation') {
          this.validateCaseConfirmation(data.response);
        }

        this.eventTriggeredName = '';
        this.errorDispatchEvent = false;
      }

    }

    @api
    get reset() {
        return
    }

    set reset(value) {
        const data = this.template.querySelectorAll("lightning-input[data-id='accountCheckbox']");
        if (data) {
            data.forEach((field) => {
                field.checked = false;
            });
        }
        this.bIsAccount = false;
        this.btnName = 'Associate To Policy';
        const gChbxId = this.template.querySelectorAll("lightning-input[data-id='gChbxId']");
        if (gChbxId) {
            gChbxId.forEach((field) => {
                field.checked = false;
            });
        }
    }

    @wire(MessageContext)
    messageContext;
   
    connectedCallback(){
      if (!this.subscription) {
        this.subscribeCommentsEvent();  
      }
    }
  
    disconnectedCallback(){
      unsubscribe(this.subscription);
      this.subscription =null;        
    }

	/* Handles the button name change if the user wants assocaite the case to member plan / member account */

    handlechangeAssociateText(event){

      if(event.target.checked){
        this.bIsAccount = true;
        this.btnName = 'Associate To Account';
      }else{
        this.bIsAccount = false;
        this.btnName = 'Associate To Policy';
      }   

    } 

	/* Triggers event to parent to get the child components data if all validations successful */

    getCaseDetails() {  
      this.eventTriggeredName = 'associateCase';
      this.dispatchEvent(new CustomEvent('datarequest',{detail:this.eventTriggeredName}));
      this.eventTriggered = true;
    }
    
	/* Assocaites to case logic starts */

    associateCase(){
      try{
        this.resetValues();
        this.verifyInteractionAndAssociate();  
      } catch(e) {
        this.isShowLoading = false;
        this.messageToUser = 'Quick Start could not associate to a record. Please try again or contact your administrator.';
        this.showError('Error','');        
      }
    }
    
	/* Reset the values to default */
    resetValues(){
      
      this.messageToUser = '';
      this.successMsg = '';
      this.eventTriggeredName = ''; 
      this.primaryTabId = '';
      this.recordId = '';
      this.interactionId = '';
      this.eventTriggered = false;       
      this.errorDispatchEvent = false;       

    }
    
	/**
		- verifies the user on correct objec tab
		- verfies the interaction is in place
		- verfies there are any multiple Member Plans showing and requests confirmation

	*/
    verifyInteractionAndAssociate(){  
      invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab=>{
	      if(focusedTab.tabId != undefined && focusedTab.recordId != undefined){ 
          let primaryObject = focusedTab.pageReference.attributes.objectApiName+'';
	        if(primaryObject == 'MemberPlan' || primaryObject=='Account'){
		        let tabId = focusedTab.tabId;
            let parentTabId = focusedTab.parentTabId
            let recordId = focusedTab.recordId; 
                this.interactionId = '';   
                this.pageState = focusedTab.pageReference?.state ?? null;
                if (this.pageState && typeof (this.pageState) === 'object') {
                    if (this.pageState.hasOwnProperty('ws')) {
                        this.stateValue = this.pageState && this.pageState.hasOwnProperty('ws') ? this.pageState['ws'] : null;
                        let tempvalues = this.stateValue && this.stateValue.includes('c__interactionId') ? this.stateValue.split('c__interactionId=') : null;
                        if (tempvalues && Array.isArray(tempvalues) && tempvalues?.length >= 2) {
                            this.interactionId = tempvalues[1]?.substring(0, 18) ?? null;
                        }
                    } else if (this.pageState.hasOwnProperty('c__interactionId')) {
                        this.interactionId = this.pageState['c__interactionId'];
                    }
                }

              if(this.bIsAccount){
                if(parentTabId == null){
                  this.primaryTabId = tabId;
                  this.recordId = recordId; 
                  if(this.interactionId!=''){
                    this.createCaseAndOpen();
                  }else{
                    this.messageToUser = 'Interaction needs to be created before creating a Case.';
                    this.showError('Error','');
                  }
                }else{
                  let isAccountFocused = false;
                  invokeWorkspaceAPI('getTabInfo', { tabId: parentTabId})
                  .then(tabInfo=>{
                    let subTabs = tabInfo.subtabs;                    
                    for(let i=0;i<subTabs.length;i++){
                      if(subTabs[i].pageReference){
                        let objectApiName = subTabs[i].pageReference.attributes.objectApiName+'';
                        if(objectApiName =='Account'){
                          if(subTabs[i].focused){
                            isAccountFocused = true;
                            this.primaryTabId = subTabs[i].parentTabId;
                            this.recordId = subTabs[i].recordId;
                            break;
                          }
                        }
                      }
                    }

                    if(isAccountFocused){
                      if(this.interactionId!=''){
                        this.createCaseAndOpen();
                      }else{ 
                        this.messageToUser = 'Interaction needs to be created before creating a Case.';
                        this.showError('Error','');
                      }
                    }else{
                      this.messageToUser = 'You must have an account page showing to associate to an account.';
                      this.showError('Error','');
                    }
                  })
                }
              }else{
                let isPolicyFocused = false;
                let policyTabCount = 0;
                if(parentTabId==null){
                  this.messageToUser = 'You must have a Policy Member page showing to associate to a policy.';
                  this.showError('Error','');
                }else{                            
                  invokeWorkspaceAPI('getTabInfo', { tabId: parentTabId})
                  .then(tabInfo=>{
                    let subTabs = tabInfo.subtabs;                    
                    for(let i=0;i<subTabs.length;i++){
                      if(subTabs[i].pageReference){
                        let objectApiName = subTabs[i].pageReference.attributes.objectApiName+'';
                        if(objectApiName =='MemberPlan'){
                          if(subTabs[i].focused){
                            isPolicyFocused = true;
                            this.primaryTabId = subTabs[i].parentTabId;
                            this.recordId = subTabs[i].recordId;
                          }
                          policyTabCount++;
                        }
                      }
                    }
                    if(!isPolicyFocused){
                      this.messageToUser = 'You must have a Policy Member page showing to associate to a policy.';
                      this.showError('Error','');
                    }else{
                      if(policyTabCount>1){                                        
                        this.handleMultipleTabs();
                      }else{
                        this.checkInteracion();
                      }
                    }
                  })                            
                }
              }          
          }else{
            let errorMsg = this.bIsAccount ==true?'You must have an account page showing to associate to an account.':'You must have a Policy Member page showing to associate to a policy.';
            this.messageToUser = errorMsg;
            this.showError('Error','');
          }
	      }else{
          let errorMsg = this.bIsAccount ==true?'You must have an account page showing to associate to an account.':'You must have a Policy Member page showing to associate to a policy.';
          this.messageToUser = errorMsg;
          this.showError('Error','');
        }
      });
    }
	
	/* In case multiple member plans open trigger error message to user */
    handleMultipleTabs(){     

      this.messageToUser = 'Multiple policies open. Do you want to associate to the policy showing?'; 
      this.showError('Warning','CreateCase');
      this.eventTriggeredName = 'policyConfirmation';

    }
  
	/* 
		* Check if Interaction associated with the Member account. 
		* Check if the complaint option selected matches the plan assocaited to the case 
	 */
    checkInteracion(){
      if(this.interactionId!=''){           
        this.createCaseAndOpen();       
      }else{
        this.messageToUser = 'Interaction needs to be created before creating a Case.';
        this.showError('Error','');
      }
    }

	/*  On multiple plans showing scenario check if the user response and proceed accordingly */

  validatePolicyConfirmation(bIsConfirmed){
      if(bIsConfirmed){
        if(this.interactionId!=''){ 
            this.createCaseAndOpen();         
        }else{
          this.messageToUser = 'Interaction needs to be created before creating a Case.';
          this.showError('Error','');
        }
      }
    }
  
	/* Calls the contraoller method to execute the save case and performs server side validations  */

    createCaseAndOpen(){     

      this.isShowLoading = true;
      this.successMsg ='New case creation in progress ...'; 
      
      this.additionalInfo.recordId = this.recordId;        
      this.additionalInfo.bIsAccount = ''+this.bIsAccount;
      this.additionalInfo.interactionId = this.interactionId;

      let caseData = JSON.stringify(this.oNewCase);
      let additionalInfo = JSON.stringify(this.additionalInfo);

      validateCreateCase({caseData:caseData,additionalInfo:additionalInfo})
      .then(result=>{
        if(result != undefined  && result.errorOnSave!=undefined){
          this.savedCaseId = result.caseId;
		      this.caseID = this.savedCaseId;
          this.bIsHPCase = result.bIsHPCase;             
          if(result.errorOnSave=='true'){
            this.messageToUser = result.messageDetails; 
            if(result.messageType=='Error'){
              this.showError('Error','');
            }else if(result.messageType=='Warning'){
              this.strCaseData = result.caseData;
              this.showError('Warning','CreateCase');
              this.eventTriggeredName = 'classificationConfirmation';
            }else if(result.messageType=='Confirmation'){
              this.strCaseData = result.caseData;
              this.showError('Warning','CreateCase');
              this.eventTriggeredName = 'classificationConfirmation';
            }                 
          }else{                   
            this.openCaseInNewTab();
          }
          this.isShowLoading = false;      
          this.successMsg ='';                     
        }else{
          this.isShowLoading = false;      
          this.successMsg ='';   
          this.messageToUser = 'Quick Start could not associate to a record. Please try again or contact your administrator.';
          this.showError('Error','');       
        }
      })
      .catch(error => {
        this.messageToUser = error.body.message;
        this.isShowLoading = false;
        this.dispatchEvent(
            new ShowToastEvent({
                title: 'Error!',message: this.messageToUser,variant: 'error'
            }),
        );
      })
    }

    /** Once user confirms on the duplicate classfication validation confirmation this method creates case */
    validateClassificationConfirmation(bIsConfirmed){
      if(bIsConfirmed){
        this.isShowLoading = true;
        this.successMsg ='New case creation in progress ...';
        insertCase({caseData:this.strCaseData,bIsHPCase:this.bIsHPCase})
        .then(result=>{          
          if(result != undefined && result.errorOnSave!=undefined){
            this.savedCaseId = result.caseId;
		        this.caseID = this.savedCaseId;
            this.bIsHPCase = result.bIsHPCase;                
            if(result.errorOnSave=='true'){
              this.messageToUser = result.messageDetails;
              this.showError('Error','');                 
            }else{                   
              this.openCaseInNewTab();
            }
            this.isShowLoading = false;      
            this.successMsg ='';                    
          }else{
            this.isShowLoading = false;      
            this.successMsg ='';   
            this.messageToUser = 'Quick Start could not associate to a record. Please try again or contact your administrator.';
            this.showError('Error','');       
          }
        })
        .catch(error => {
          this.messageToUser = error.body.message;
          this.isShowLoading = false;
          this.dispatchEvent(
              new ShowToastEvent({
                  title: 'Error!',message: this.messageToUser,variant: 'error'
              }),
          );
        })
      }else{
        //do nothing
      }
    }

	/* On successfull case creation the case is opened in new tab */    
     async openCaseInNewTab(){       
        if (this.savedCaseId.length == 18 || this.savedCaseId.length == 15){
            let casedata = {};
            casedata.Id = this.savedCaseId;
            casedata.objApiName = 'Case';
            openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
            this.dispatchEvent(new CustomEvent('casecreated', { detail: { data: true } }));
        }else{
        this.isShowLoading = false;
        this.successMsg ='';
      } 
		if (this.iscreateTask == true && (this.savedCaseId.length == 18 || this.savedCaseId.length == 15)) {
            let taskNumber;
            await saveTask({
                    sTasktype: this.taskType,
                    sTaskDueDate: this.taskDuedate,
                    sTaskComment: this.sTaskComment,
                    serviceCenter: this.serviceCenter,
                    department: this.department,
                    workqueuename: this.workqueuename,
                    username: this.username,
                    userId: this.userId,
                    caseId: this.caseID
                })
                .then(data => {
                    data = JSON.parse(data);
                    taskNumber = data.taskNumber;
                    this.taskId = data.taskId;
                })
                .catch(error => {

                    this.dispatchEvent(
                        new ShowToastEvent({
                            title: 'Error!',
                            message: error.message,
                            variant: 'error',
                        }),
                    );
                })

            if (this.taskId != '' && this.taskId != null && this.taskId != undefined) {
				let taskTabId;
                if (this.taskId.length == 18 || this.taskId.length == 15) {
                    await invokeWorkspaceAPI('openSubtab', {
                        parentTabId: this.primaryTabId,
                        pageReference: {
                            type: 'standard__recordPage',
                            attributes: {
                                recordId: this.taskId,
                                objectApiName: 'Task',
                                actionName: 'view'
                            }
                        },
                        focus: false
                    });
					await invokeWorkspaceAPI('getTabInfo', { tabId: this.primaryTabId })
                        .then(tabInfo => {
                            let subTabs = tabInfo.subtabs;
                            for (let i = 0; i < subTabs.length; i++) {
                                if (subTabs[i].pageReference) {
                                    let objectApiName = subTabs[i].pageReference.attributes.objectApiName + '';
                                    if (objectApiName == 'Task') {
                                        taskTabId = subTabs[i].tabId;
                                    }
                                }
                            }
                        });
                    await invokeWorkspaceAPI('setTabLabel', { tabId: taskTabId, label: taskNumber });
                }

            }
        }
    }

  /* Adds an event listener which is triggered once the case edit page is opened in New tab */
  subscribeCommentsEvent(){
    if (!this.subscription) {
      this.subscription = subscribe(
        this.messageContext,
        COMMENTS_CHANNEL_REQ,
        (message) => this.handleMessage(message)   
      );
    }
  }
  /* This is triggered from the Case Edit page requesting the comments */
  handleMessage(message){
    if(message.eventName == 'requestComments'){     
      if(this.savedCaseId!=undefined && this.savedCaseId!=''){
        let comments = JSON.parse(this.jsonCaseComment);
        comments['CaseId']=this.savedCaseId.length == 18?this.savedCaseId.slice(0, 15):this.savedCaseId;
        comments['classificationIdQS']=this.oNewCase.Classification_Id__c;
        comments['medicarePartCOrPartD']=this.additionalInfo.medicarePartCPartDValue!=''?this.additionalInfo.medicarePartCPartDValue:'';
        comments['intentIdQS']=this.oNewCase.Intent_Id__c;
        const payload = {
          caseId: this.savedCaseId,
          comments:comments,
          isHPCase:this.bIsHPCase, 
          source:'QuickStart'
        };
        publish(this.messageContext, COMMENTS_CHANNEL, payload);
        this.savedCaseId = '';                        
      }else{
        const payload = { source:'QuickStartUnsubscribeEvent'};
        publish(this.messageContext, COMMENTS_CHANNEL, payload);
      }
    }else if(message.eventName == 'UpdateCommentsTrasnferred'){
      this.isShowLoading = false;
      if(message.success==true){         
        this.messageToUser = '';
        this.showError('Success','');
        this.dispatchEvent(
          new ShowToastEvent({
              title: 'Success',message: 'Comments updated successfully.',variant: 'success'
          }),
        );
      }else{        
        this.successMsg ='';
        this.messageToUser = 'The Classification and Intent selected in quick start does not match the case in focus. Update the Classification and Intent in quick start to match with case to allow update comments.';
        this.showError('Error','');
      }
	 }else{  
      if(message.success==true){ 
        this.getSavedCaseNumber();  
      }else{
        this.isShowLoading = false;
        this.successMsg ='';
      }
    }
  }
  /* On successfull transfer of comments to Case Edit page the case number is retrived and displayed using toast message */
  getSavedCaseNumber(){
      getCaseNumber({caseId:this.caseID})
      .then(caseNumber=>{
        this.isShowLoading = false;
        this.successMsg = '';
        this.messageToUser = 'Case created successfully - case number '+caseNumber;
        this.showError('Success','');
        this.dispatchEvent(
          new ShowToastEvent({
              title: 'Success',message: this.messageToUser,variant: 'success'
          }),
        );
      })
    }

    /* Triggers event to message component and based on the errorType the payload is constructed.*/
  showError(errorType,Source){
    let error = [];
    let warning =[];   
    if(errorType=='Error'){
      this.messageToUser = this.messageToUser!=''?this.messageToUser:'Quick Start could not associate to a record. Please try again or contact your administrator.';
      error = [{MessageType: 'Error',Message: this.messageToUser,Source:'',DynamicValue: []}];
      this.dispatchEvent(new CustomEvent('casevalidation',{detail :{error:error}}));
      this.errorDispatchEvent = false;
    }else if(errorType=='Warning'){
      warning =[{MessageType:'Warning',Message:this.messageToUser,Source :Source,DynamicValue: []}];
      this.dispatchEvent(new CustomEvent('casevalidation',{detail :{error:warning}}));
      this.errorDispatchEvent = true;
    }else if(errorType=='Success'){      
      this.errorDispatchEvent = false;
    }  
  }

  getCaseComments() {

        this.eventTriggeredName = 'updateComments';
        this.dispatchEvent(new CustomEvent('datarequest',{detail:this.eventTriggeredName}));
        this.eventTriggered = true;

    }

    updateComments() {

        this.updateCommentsData = {};
        this.updateCommentsData.caseRecordId = '';
        this.updateCommentsData.caseAction = 'edit';
        this.validateCaseTabInfo();
    }

    validateCaseTabInfo() {
        invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
            if (focusedTab.tabId != undefined) {
                let cmpName = '';
                let focusedTabPg = {};
                if (focusedTab.pageReference != null && focusedTab.pageReference.attributes) {
                    focusedTabPg = this.getTabNameAndRecordId(focusedTab.pageReference);
                }
                let caseTabCount = 0;
                let actionName;
                if(focusedTabPg.isCaseTab) {
                    if (focusedTab.parentTabId == null) {
                        invokeWorkspaceAPI('getAllTabInfo').then(atabsInfo => {
                            for (let i = 0; i < atabsInfo.length; i++) {
                                if(atabsInfo[i].pageReference && atabsInfo[i].pageReference.attributes) {
                                    let atabsInfoPg = this.getTabNameAndRecordId(atabsInfo[i].pageReference);
                                    let recId = atabsInfoPg.recId;
                                    if(atabsInfoPg.isCaseTab) {
                                        if(atabsInfo[i].focused) {
                                            this.updateCommentsData.caseTabId = atabsInfo[i].parentTabId;
                                            this.updateCommentsData.caseRecordId = recId;
                                        }
                                        caseTabCount++;
                                    }
                                }
                            }
                            if (caseTabCount > 1) {
                                this.handleMultipleCases();
                            } else if (caseTabCount == 1) {
                                this.sendCommentsToUpdate();
                            } else {
                                this.messageToUser = 'You must be on the case edit page to update comments.';
                                this.showError('Error', '');
                            }
                        })
                    } else {
                        invokeWorkspaceAPI('getTabInfo', { tabId: focusedTab.parentTabId }).then(tabInfo => {
                            let tabInfoPg = {};
                            if (tabInfo && tabInfo.pageReference && tabInfo.pageReference.attributes) {
                                tabInfoPg = this.getTabNameAndRecordId(tabInfo.pageReference);
                            }
                            if (tabInfoPg.isObjCaseApi) {
								invokeWorkspaceAPI('getAllTabInfo').then(atabsInfo => {
									for(let i = 0; i < atabsInfo.length; i++){
										let atabsInfoPg = {};
                    if(atabsInfo[i].pageReference && atabsInfo[i].pageReference.attributes) {
                      atabsInfoPg = this.getTabNameAndRecordId(atabsInfo[i].pageReference);
                    }
                    if(atabsInfoPg.isObjCaseApi) {
											let stabsInfo = atabsInfo[i].subtabs;
											for(let l = 0; l < stabsInfo.length; l++){
													let stabsInfoPg = {};
                          if(stabsInfo[l].pageReference && stabsInfo[l].pageReference.attributes) {
                              stabsInfoPg = this.getTabNameAndRecordId(stabsInfo[l].pageReference);
                          let recId = stabsInfoPg.recId;
                          if(stabsInfoPg.isCaseTab) {
														if(atabsInfo[i].focused) {
															this.updateCommentsData.caseTabId = stabsInfo[l].parentTabId;
															this.updateCommentsData.caseRecordId = recId;
													  }
												    caseTabCount++;
											    }                          
												}
											}
										}
									}
									if (caseTabCount > 1) {
										this.handleMultipleCases();
									}else if(caseTabCount == 1) {
										this.sendCommentsToUpdate();
									}else {
										this.messageToUser = 'You must be on the case edit page to update comments.';
										this.showError('Error', '');
									}
								})
							}else{
								let stabsInfo = tabInfo.subtabs;
								for (let i = 0; i < stabsInfo.length; i++) {
									if (stabsInfo[i].pageReference && stabsInfo[i].pageReference.attributes) {
										let stabsInfoPg = this.getTabNameAndRecordId(stabsInfo[i].pageReference);
                    let recId = stabsInfoPg.recId;
                    if(stabsInfoPg.isCaseTab) {
											if (stabsInfo[i].focused) {
												if(recId == undefined && stabsInfo[i].pageReference.attributes.state){
													recId = stabsInfo[i].pageReference.attributes.state.c__recordId;
												}
												this.updateCommentsData.caseTabId = stabsInfo[i].parentTabId;
												this.updateCommentsData.caseRecordId = recId;
											}
											caseTabCount++;
										}
									}
								}
								if (caseTabCount > 1) {
									this.handleMultipleCases();
								} else if (caseTabCount == 1) {
									this.sendCommentsToUpdate();
								} else {
									this.messageToUser = 'You must be on the case edit page to update comments.';
									this.showError('Error', '');
								}
							}
                        })
                    }
                } else {
                    this.messageToUser = 'You must be on the case edit page to update comments.';
                    this.showError('Error', '');
                }
            } else {
                this.messageToUser = 'You must be on the case edit page to update comments.';
                this.showError('Error', '');
            }
        })
    }

    handleMultipleCases() {
        this.messageToUser = 'Multiple Cases open. Do you want to associate comments to the case showing?.';
        this.showError('Warning', 'CreateCase');
        this.eventTriggeredName = 'caseConfirmation';
    }

    validateCaseConfirmation(bIsConfirmed) {
        if (bIsConfirmed) {
            this.sendCommentsToUpdate();
        }
    }
    sendCommentsToUpdate() {
        let comments = JSON.parse(this.jsonCaseComment);
        comments['CaseId'] = this.updateCommentsData.caseRecordId;
        comments['classificationIdQS'] = this.oNewCase.Classification_Id__c;
        comments['medicarePartCOrPartD'] = this.additionalInfo.medicarePartCPartDValue != '' ? this.additionalInfo.medicarePartCPartDValue : '';
        comments['intentIdQS'] = this.oNewCase.Intent_Id__c;
        const payload = {
            caseId: this.updateCommentsData.caseRecordId,
            comments: comments,
            source: 'UpdateComments'
        };
        publish(this.messageContext, COMMENTS_CHANNEL, payload);
    } 
    getTabNameAndRecordId(pageReference) {
        let cmpData = {};
        if (pageReference.attributes.componentName != undefined) {
            cmpData.tabCmpName = pageReference.attributes.componentName;
            cmpData.recId = pageReference.state ? pageReference.state.c__recordId : '';
        } else if (pageReference.attributes.name != undefined) {
            cmpData.tabCmpName = pageReference.attributes.name;
            cmpData.recId = pageReference.attributes.attributes && pageReference.attributes.attributes.encodedData?pageReference.attributes.attributes.encodedData.Id:'';
        } else if (cmpData.recId == undefined && pageReference.state && pageReference.state.c__recordId) {
            cmpData.recId = pageReference.state.c__recordId;
        }
        if ('c__HumEditButtonOverrideLightning' == cmpData.tabCmpName || cmpData.tabCmpName == 'c:caseInformationComponentHum') {
            cmpData.isCaseTab = true;
        }
        if (pageReference.attributes && (
                pageReference.attributes.objectApiName == 'Case' ||
                (pageReference.attributes.attributes && pageReference.attributes.attributes.encodedData &&
                    pageReference.attributes.attributes.encodedData.objApiName == 'Case')
            )) {
            cmpData.isObjCaseApi = true;
        } else if ('c__HumCloneCaseButtonOverrideLightning' == cmpData.tabCmpName) {
            cmpData.isObjCaseApi = true;
        }
        return cmpData;
    }
}