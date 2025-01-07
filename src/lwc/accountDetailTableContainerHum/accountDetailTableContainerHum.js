/*******************************************************************************************************************************
Function    : This JS serves as controller to accountDetailTableContainerHum.html. 
Modification Log: 
Developer Name             Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan Kumar N          03/22/2021                    US:1892975
* Supriya Shastri        04/23/2021                    Inquiry Detail page changes
* Ritik Agarwal          10/08/2021                     Added viewAll for Available and Enrolled Programs
* Pooja Kumbhar          11/02/2022                    US- 3863263 : T1PRJ0170850 - Lightning - RCC/Medicare (MET) Enrollment sections Case Details Page
* Ashish/Kajal Namdev    07/18/2022                    Added archived components logic for view all functionality
* Gowthami Thota         12/07/2022                    US - 3827038 : T1PRJ0170850 - Related Information Section on Case Details Page
* Gowthami Thota         02/28/2023                    User Story 4279664: T1PRJ0865978 - C06, Lightning- Implementation- Add Interaction Section on Case Detail Page
* Deepak khandelwal      09/27/2023                     US_5050647 -- chat transcript view all button
* Tharun Madishetti		 10/06/2023						US-5050647: Added Switch Functionality
 *********************************************************************************************************************************/
import { LightningElement, api, wire, track } from 'lwc';
import { getRecord } from 'lightning/uiRecordApi';
import { getLabels } from "c/crmUtilityHum";
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
const arrFields = ['Account.Name'];

export default class AccountDetailTableContainerHum extends LightningElement {    
    @api nameOfScreen;
    @api oParams;
    @api response;
    @track bShowPolicyList;
    @track bShowAccordianPolicy;
    @track recordType;
    @track recordId;
    @track bShowCasehistory;
    @track bShowInteractions;
    @track bshowTranscripts;
    @track bShowLegacyHistoryTable;
    @track bEnrollProgram;
    @track bAvailProgram;
    @track bshowMetEnroll;
    @track breadCrumbItems = [];
    @track subtabTitle;
    @track viewAllCaseHistory= false;
    @track viewAlltasks= false;
    @track viewAllComments = false;
    @track labels = getLabels();
    @track bshowRelatedInfo;
    @track bshowAttachmentInfo;
    @track bshowCaseInteractions;
	@track bSwitch5050647 = false;

    connectedCallback() {
		const switchVal = getCustomSettingValue('Switch', '5050647');			
		switchVal.then(result => {
			if (result && result?.IsON__c)  this.bSwitch5050647 = result?.IsON__c;
			const me = this;
			switch (me.nameOfScreen) {
				case 'Archived Case':
					me.bShowArchievedCases = true;
					break;
				case 'Case Search':
					me.bShowCasehistory = true;
					break;
				case 'groupAccountPolicies':
					me.bShowPolicyList = true;
					break;
				case 'accountdetailpolicy':
					me.bShowAccordianPolicy = true;
					break;
				case 'Interactions':
					me.bShowInteractions = true;
					break;
				case 'inquirynotes':
					me.subtabTitle = me.labels.inquiryNotesHum;
					me.bShowInquiryTaskDetails = true;
					break;
				case 'inquiryaudittrail':
					me.subtabTitle = me.labels.inquiryAuditTrailHum;
					me.bShowInquiryTaskDetails = true;
					break;
				case 'inquiryattachments':
					me.subtabTitle = me.labels.inquiryAttachmentsHum;
					me.bShowInquiryTaskDetails = true;
					break;
				case 'inquirytasklist':
					me.subtabTitle = me.labels.taskListHum;
					me.bShowInquiryTaskDetails = true;
					break;
				case 'tasknotes':
					me.subtabTitle = "Task Notes";
					me.bShowInquiryTaskDetails = true;
					break;
				case 'taskaudittrail':
					me.subtabTitle = "Task Audit Trail";
					me.bShowInquiryTaskDetails = true;
					break;
				case 'taskattachments':
					me.subtabTitle = "Task Attachments";
					me.bShowInquiryTaskDetails = true;
					break;
				case 'Legacy Contact History':
					me.bShowLegacyHistoryTable = true;
					break;
				case 'Enrolled Programs':
					me.bEnrollProgram = true;
					break;
				case 'Available Programs':
					me.bAvailProgram = true;
					break;
				case 'METEnrollment':
					me.bshowMetEnroll = true;
					break;
				case 'Archived Case History' : 
					me.bArchivedCaseHistory = true;  
					me.viewAllCaseHistory = true;
					break;
				 case 'Case History' : 
					me.bArchivedCaseHistory = true;  
					me.viewAllCaseHistory = true;
					break;   
				case 'Tasks' : 
					me.bArchivedCaseTasks = true;  
					me.viewAllTasks = true;
					break;  
				case 'Archived Case Comments' : 
					me.bArchivedCaseComment = true;  
					me.viewAllComments = true;
					break;
				case 'RelatedInformation':
					me.bshowRelatedInfo = true;
					break;
				case 'CaseAttachment':
					me.bshowAttachmentInfo = true;
					break;
				case 'CaseInteractions':
					me.bshowCaseInteractions = true;
					break;
				case 'chattranscript':
					if (this.bSwitch5050647) me.bshowTranscripts = true;
					else me.bshowTranscripts = false;
					break; 
				default:
			}
		});
    }

    @wire(getRecord, {
        recordId: '$oParams.sRecordId',
        fields: arrFields
    })
    wiredAccount({
        error,
        data
    }) {
        if (data) {
            this.breadCrumbItems = [{
                "label":'Accounts',
                "href":'',
                "eventname":''
            },{
                "label":  data.fields.Name.value,
                "href":'',
                "eventname":''
            }];
        } else if (error) {
            console.log('Error Occured', error);
        }
    }
}