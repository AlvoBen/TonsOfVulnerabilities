/*
JS Controller        : caseProcessLaunchHum
Version              : 1.0
Created On           : 2/24/2022
Function             : Component to display to case processes templates.

Modification Log:
* Developer Name                    Date                         Description
* Isha Gupta                       2/24/2022                     Original Version
* Manohar Billa                    7/27/2022                     Added more permision sets for showing medicare OI form process template
* Santhi Mandava                   07/28/2022                    US3268999 - Generic case templates implementation
* Muthukumar					             08/16/2022                    Df-5852 fix
* Abhishek Mangutkar			         08/22/2022                    DF-5919 fix
* Santhi Mandava                   09/01/2022                    US3279633 QAA Template changes
* Bhumika Dodiya                   09/02/2022                    US3751656 : Case Resolution - Action Component - Recommended Process Template Restriction based on Status of Case
* Muthukumar                       09/08/2022                    US-3279519 update plan demographic
* Muthukumar                       09/14/2022                    DF-6166 Fix
* Visweswararao J                  09/15/2022                    DF-6149 Fix
* Nirmal Garg                      09/26/2022                    US2961209-PCP Update/Change Screen
* Abhishek Mangutkar               10/12/2022                    US-3837985 RTI - attach to case upon resend  functionality
* Santhi Mandava                   11/01/2022                    US:3813238 Displaying Update plan demographics button
* visweswararao j                  11/01/2022                    User Story 3862542: T1PRJ0170850 - MF19080 - Lightning- Templates/Update Plan Demographics Changes
* visweswararao j                  11/01/2022                    User Story 3698842: T1PRJ0170850 - MF 19080 - Lightning - Templates/QAA & Limitations of Template
* Prasuna Pattabhi                  12/05/2022                    User Stories US_4016910,US_4020206,US_4020208,US_4020207
* Prasuna Pattabhi                  12/05/2022                    BuildFix
* Jasmeen Shangari                  12/05/2022                   US-4016899, Creditable coverage Process changes to launch Case Edit
* Prasuna Pattabhi                  12/21/2022                    US 4020210  QAA Changes
* Prasuna Pattabhi                  12/27/2022                    US 4020210  QAA Missing Changes
* Nirmal Garg                 		12/29/2022                   DF-6877 fix
* Prasuna Pattabhi             02/01/2023                      US-4178418 : TRR Process Template on Case Details Page
* Aishwarya Pawar                   02/02/2023                   REQ 4211868 - Ability to view multiple processes in an invoice request
* Jasmeen Shangari					02/15/2023				     US-4226309: Integrate Process Section on Case Edit
* Santhi Mandava                  02/20/2023                     US4222937_CaseProcessChanges
* Nirmal Garg					   03/02/2022				 	DF-7300
* Kalyani Pachpol                 03/17/2023                     DF-7399
* Prasuna Pattabhi             03/31/2023                      US 4429882 : Case Edit Page to retain Quick Start info
* Jasmeen Shangari					03/31/2023				     US-4414691: Dynamic View for All Processes Templates by default on page load
* Prasuna Pattabhi             03/31/2023                      US 4429882 : Case Edit Page to retain Quick Start info
* Jasmeen Shangari                  04/13/2023                 US-4420520 : Design & Implementation of a fix to ensure the TRR template does not override the Good Cause reinstatement template*
* Jasmeen Shangari                  04/20/2023                 DF-7559 Recommended Process is not visile if process is available
* Prasuna Pattabhi                  06/14/2023                 US 4467570 : Launching and opening Newborn Notification Template (baby bot) for Medicaid (Lightning)
* Pooja Kumbhar	                    06/29/2023                 US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
* Prasuna Pattabhi                  07/13/2023                 US 4752577 - Quick Start Update Comments Button not populating in CRM Case
* Prasuna Pattabhi                  07/20/2023                 US 4752577 - Null check Added
* Muthukumar 						08/01/2023				   US 4749205 
* Jasmeen Shangari                  10/06/2023                 US 5116621 - No Templates to show for Cancelled Case
* Jasmeen Shangari                  10/13/2023                 US 5116621 - Added Switch
* Vardhman Jain                      10/20/2023                   US-5009031 update Commercial demographic
* Isaac Chung                       12/12/2023                 US 5107579 Mail Order Management; Pharmacy- Guided Flow- Inactivate Rx- Implementation (Lightning)
* Pinky Vijur                       03/01/2024                 User Story 5203789: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch button /link and existing flow
*------------------------------------------------------------------------------------------------------------------------------
*/

import { LightningElement, track, api, wire } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { invokeWorkspaceAPI, openLWCSubtab } from "c/workSpaceUtilityComponentHum";
import { getUserGroup, hcConstants, getLabels } from "c/crmUtilityHum";
import currentUserId from '@salesforce/user/Id';
import removeTemplateSubmission from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.removeTemplateSubmission';
import getTemplateDetails from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getTemplateDetails';
import showOIMEdicareForm from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.showOIMEdicareForm';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import hasCRMS_111_StridesAccess from '@salesforce/customPermission/CRMS_111_StridesAccess';
import pubSubHum from 'c/pubSubHum';
import { recordIdNames, submissionIdNames, templateNames, newProcesstemplateLabels } from './inputs'
import getCaseStatus from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getCaseStatus';
import { uConstants } from 'c/updatePlanDemographicConstants';
import isProcessAttached from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.isProcessAttached';
import hasTRRProcess from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.hasTRRProcess';
import { getProcess } from 'c/caseProcessEvaluationHum';
import isCRMFunctionalityONJS from '@salesforce/apex/HUMUtilityHelper_LWC.isCRMFunctionalityONJS';
const PCPPCDQuestions = 'PCP/PCD Questions';
const PCPUpdateChange = 'PCP Update/Change';
const PharmacyWebIssue = 'Web Issues';
const SendPrintItemMember = 'Send Print Item: Member';
const SendClaimStatementMember = 'Send Claim Statement: Member';
const SendClaimStatementProvider = 'Send Claim Statement: Provider';
const AuthReferralHistory = 'Auth / Referral History';
const InvoiceRequest = "Invoice Request";
const InactivateRx = "Inactivate Rx";
const noLinksRecommendedMessage = 'No recommended processes available';
const noLinksAvailableMessage = 'No processes available';
const sRecommended = 'Recommended';
const sAvailable = 'Available';
import { attachProcessToCase} from 'c/genericCaseActionHum';
import { getRecord } from 'lightning/uiRecordApi';
import USER_ID from '@salesforce/user/Id';
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import { getTemplateSubmissionOwnerIds } from 'c/genericTemplateDataCreationHum';
const MedicaidNewbornNotification = 'Medicaid Newborn Notification';
import getNewbornTemplateDetails from '@salesforce/apex/CaseProcessesLaunch_LC_Hum.getNewbornTemplateDetails';
import refreshCaseProcessLMS from "@salesforce/messageChannel/refreshCaseProcessFromTemplateLMS__c";
import { APPLICATION_SCOPE, subscribe, messageContext, createMessageContext } from "lightning/messageService";



export default class CaseProcessLaunchHum extends LightningElement {
    @wire(CurrentPageReference) pageRef;
    @track error;
    @api recordId;
    @api casedata;
    @api pageName;
    @api memPlanId;
    @api accountId;
    @track executedProcessesList = [];
    @track selectedProcessName;
    @api objectApiName;
    @track userGroup = getUserGroup();
    @track pharmacyProfile = hcConstants.PHARMACY_SPECIALIST_PROFILE_NAME;
    @track ProfileList = [hcConstants.Customer_Care_Specialist, hcConstants.CUSTOMER_CARE_SUPERVISOR, hcConstants.System_Admin];
    @track Recordtypes = [hcConstants.CASE_AGENT_API, hcConstants.Closed_Member_Case_API, hcConstants.Medicare_Case_API, hcConstants.CASE_MEMBER_API, hcConstants.CASE_PROVIDER_API, hcConstants.Closed_Medicare_Case_API];
    @track templateList = [];
    @track isTemplateAttached = false;
    @track confiuredTemplateNames = [];
    @track showViewAll = false;
    @track viewAllLinkLabel = 'View All';
    @track isTemplatesVisible = false;
    @track openMed_Modal;
    @track templateId;
    @track lstVisibleTemplates = [];
    @track linkStyle = 'spancls';
    @track sAttachedTemplateName = '';
    @track bSubtabSwitch;
    @track bmemberId; 
    @track bmemberName;
    @track bmemberBithday; 
    @track bTNvalue;

    @track labels = getLabels();
    @track showOIFlow;
    @track memberPlanId;
    @track isCloseCase = false;
    @track isCancelledCase = false;
    @track isCancelBtnClicked = false;
    @track isCaseEditPageOpened = false;
    @track isFinished = false;
    @track iconName = 'standard:account';
    @track showRecommendedActions = true;
    @track headingName = 'Processes';
    @track trrHeadingName = 'TRR Process';
    @track trrCount = '';
    @track showTRR = false;
    @track hasTRRprocesses = false;
    @track trrData = [];
    @track viewAll = false;
    @track caseNumber;
    @track noOfRecords = 3;
    @track showAttachedTemplates = false;
    @track evaluatedProcess = [];
    @track AvailableProcess = [];
    @track RecommendedProcess = [];
    @track AvailableProcessLabels = [];
    @track RecommendedProcessLabels = [];
    @track mapConfiguredTemplates = new Map();
    @track noTemplateLinksMessage = '';
    templateLabels = ['Medicare Part D Redetermination', 'Medicare/Medicaid Expedited Appeal', 'Medicare Good Cause Reinstatement Form', 'Medicaid PAR Provider Not Accepting', 'QAA Complaint', 'Medicare Creditable Coverage Attestation', 'Medicare and Medicaid Other Insurance Form','Mail Order Pharmacy Web Issues'];
    processData;
    caseRecordType;
    @track uniqueCaseId = this.getUniqueId();
    @api isEdit = false;
    @track isNewbornLaunch = true;
    @track templateSubmissionOwnerId = null;
    @track subscription = null;
    @track isCancelCaseSwitchON = false;
	bSwitch5009031 = false;

    getUniqueId() {
        let uniqueId = `50000${Math.random().toString(16).slice(2)}`;
        uniqueId = uniqueId && uniqueId?.length >= 18 ? uniqueId.substring(0, 18) : uniqueId.padEnd(18, '0');
        return uniqueId;
    }

    @wire(getRecord, {
        recordId: USER_ID,
        fields: [PROFILE_NAME_FIELD]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
        }
    }
	
	@wire(isCRMFunctionalityONJS,{sStoryNumber:'5009031'})
    switchFuntion({error,data}){
        if(data){
            this.bSwitch5009031 = data['5009031'];
        }
        if(error){
            console.log('error---',error)
        }
    }
	

    @api
    async attachProcess(caseId) {
        let templateOwnerId;
        await getTemplateSubmissionOwnerIds(this.uniqueCaseId).then(result => {
            if (result) {
                templateOwnerId = result;
            }
        }).catch(error => {
            console.log(error);
        })
        //if yes then update who id
        if (templateOwnerId) {
            attachProcessToCase(templateOwnerId, caseId)
                .then(result => {
                    if (result) {
                        console.log('Template attached to Case');
                    }
                }).catch(error => [
                    console.log(error)
                ])
        }
    }

    getCaseProcessData() {
        return new Promise((resolve, reject) => {
            try {
                if (this.recordId && this.recordId.startsWith('500')) {
                    let caseobj = {
                        caseId: this.recordId
                    }
                    resolve(caseobj);
                } else {
                    let tempobj = {
                        ClassificationType: this.casedata?.prefillValues?.classificationType ?? '',
                        InteractingAbout: this.casedata?.objCase?.Interacting_About_Type__c ?? '',
                        CaseRecordType: this.casedata?.prefillValues?.caseRecordTypeName?.replace(' ', '_') ?? '',
                        AccountName: this.casedata?.objCase?.AccountId ?? '',
                        OwnerQueue: this.casedata?.objCase?.Owner_Queue__c ?? '',
                        Status: 'In Progress',
                        AccountRecordType: this.casedata?.objCase?.Interacting_About_Type__c ?? '',
                        MemberPlanId: this.casedata?.objCase?.Member_Plan_Id__c ?? '',
                        AccountId: this.casedata?.objCase?.AccountId ?? ''
                    }
                    this.caseRecordType = this.casedata?.prefillValues?.caseRecordTypeName?.replace(' ', '_') ?? '';
                    resolve(tempobj);
                }
            } catch (error) {
                reject(error);
            }
        })
    }


    connectedCallback() {
        pubSubHum.registerListener('refreshCaseProcessSec', this.refreshComponent, this);
        this.subscribeToLMSChannel();

        this.getSwitchData().then(switchResult => {
            if (switchResult && switchResult === true) 
            {
                        //get case status
                getCaseStatus({ sCaseId: this.recordId }).then(result => {
                    this.isCloseCase = (result == 'Closed') ? true : false;
                    this.isCancelledCase = (result == 'Cancelled') ? true : false;
                }).catch(error => {
                    this.error = error;
                });

                hasTRRProcess({ sCaseId: this.recordId, showAll: false, noOfRecords: this.noOfRecords }).then(result => {
                    this.showTRR = result.hasTRR == 'true' ? true : false;
                    this.hasTRRprocesses = false;
                    this.caseNumber = result.caseNumber;
                    if (this.showTRR) {
                        this.viewAll = result.showViewAll == 'true' ? true : false;
                        if (this.viewAll) {
                            this.trrCount = this.noOfRecords + '+';
                        } else {
                            this.trrCount = '' + result.totalNoOfProcesses;
                        }
                        if (result.data != undefined) {
                            this.trrData = JSON.parse(result.data);
                        }
                        this.hasTRRprocesses = result.totalNoOfProcesses == '0' ? false : true;
                        this.trrHeadingName = 'TRR Process (' + this.trrCount + ')';
                        this.showRecommendedActions = false;
                        this.iconName = 'standard:call_history';
                    }
                }).catch(error => {
                    this.error = error;
                    console.log(this.error);
                });

                this.processFunction();

            }
        });

        showOIMEdicareForm({ sCaseId: this.recordId })
        .then(result => {
            this.showOIFlow = result;
        })
        .catch(error => {
            console.log('Error : ', error);
        });
    }

    processFunction() {
        this.getCaseProcessData().then(result => {
            if (result) {
                getProcess(result).then(result => {
                    this.evaluatedProcess = result;
                    this.isCancelBtnClicked=false; 
                    this.getTemplateSubmissionData(this.recordId);
                }).catch(error => {
                    console.error(error);
                })

            }
        }).catch(error => {
            console.error(error);
        });
    }

    getTemplateSubmissionData(caseId) {
        removeTemplateSubmission({ caseId: caseId, isCancel: this.isCancelBtnClicked })
            .then(result => {
                this.memberPlanId = result.MemberPlanId;
                this.bSubtabSwitch = result.bSubtabCloseSwitch;
                this.bmemberName = result.MemberName; 
                this.bmemberBirthday = result.MemberBirthday;
                this.bTNValue = result.TempValue; 
                this.bmemberId =result.MemberId; 

                if (result && Array.isArray(result) && result.length > 0) {
                    result.forEach(k => {
                        if (k && k?.ProcessType.toLowerCase() === PCPPCDQuestions.toLowerCase()) {
                            k.ProcessType = PCPUpdateChange;
                        }
                    })
                }
                this.getTemplatesData(result);
            })
            .catch(error => {
                this.error = error;
                console.log('Error : ', this.error);
            });
    }

    refreshComponent(event) {
        if(event.flowName==='PCP Update/Change')
        {
            let template='PCP Update';
            let servicefundedits=event.servicefundedit;
            const selectedEvent = new CustomEvent("pcpupdate", {
                detail: {
                    showtemplate: template,
                    showservice: servicefundedits
                }
            });
            this.dispatchEvent(selectedEvent);
        }
        this.isCancelBtnClicked = !event.isFinished;
        this.isFinished = event.isFinished;
        this.getTemplateSubmissionData(this.recordId);
    }

    getTemplatesData(caseData) {
        getTemplateDetails()
            .then(result => {
                this.resetDetails();
                result.forEach(ele => {
                    let tempObj = {};
                    tempObj.templateName = ele.Template_Name__c;
                    tempObj.templateLabel = ele.Template_Label__c;
                    tempObj.hasAccess = false;
                    tempObj.showInAction = ele.IsVisible__c;
                    tempObj.params = ele.Params__c;
                    tempObj.TemplateType = ele.Template_Type__c;
                    tempObj.description = ele.Description__c;
                    tempObj.flowParamsJSON = this.getFlowParameters(ele.Params__c, this.objectApiName, '');
                    this.templateList.push(tempObj);
                    this.mapConfiguredTemplates.set(tempObj.templateLabel, tempObj);
                    if (ele.Template_Name__c === uConstants.Update_Plan_Demographics_API || ele.Template_Name__c === uConstants.Update_Commercial_Demographics_API) {
                        var temp = ele.Description__c.split(',');
                        temp.forEach(v => {
                            this.confiuredTemplateNames.push(v);
                        })
                    } else {
                        this.confiuredTemplateNames.push(ele.Description__c);

                    }
                });
                this.visibilityCheck(caseData);
                this.showViewAll = true;
                if (this.isTemplateAttached) this.linkStyle = 'spandisabled';
            })
            .catch(Error => {
                console.log('Error : ', this.error);
            })
    }

    resetDetails() {
        this.templateList = [];
        this.lstVisibleTemplates = [];
        this.isTemplatesVisible = false;
        this.confiuredTemplateNames = [];
        this.sAttachedTemplateName = '';
        this.viewAllLinkLabel = 'View All';
        this.isTemplateAttached = false;
        this.isCancelBtnClicked = false;
    }

    visibilityCheck(caseData) {
        if (caseData != null) {
            this.checkProcessEvaluationLogic(this.evaluatedProcess);
            if (caseData.CaseProcessTemp_DTO_HUMList != null && caseData.CaseProcessTemp_DTO_HUMList.length > 0) {
                caseData.CaseProcessTemp_DTO_HUMList.forEach(item => {
                    if (item && item?.ProcessType != PCPPCDQuestions) {
                        if (this.confiuredTemplateNames.includes(item.ProcessType)) {
                            this.sAttachedTemplateName = item.ProcessType;
                            item.ProcessType = templateNames[item.ProcessType];
                            this.executedProcessesList = this.checkAccessibility(caseData.CaseProcessTemp_DTO_HUMList, caseData);
                            var executedProcesses = this.executedProcessesList.length > 0 ? this.executedProcessesList.filter(a => a.hasAccess == true) : [];
                            this.isTemplateAttached = executedProcesses.length > 0 ? true : false;
                            this.isTemplatesVisible = false;
                        }
                    } else {
                        if (this.confiuredTemplateNames.includes(PCPUpdateChange)) {
                            this.sAttachedTemplateName = PCPUpdateChange;
                            item.ProcessType = templateNames[PCPUpdateChange];
                            this.executedProcessesList = this.checkAccessibility(caseData.CaseProcessTemp_DTO_HUMList, caseData);
                            this.isTemplateAttached = true;
                            this.isTemplatesVisible = false;
                        }
                    }
                });
            } else {
                if (this.RecommendedProcess?.length > 0)
                    this.setRecommndedAvailableProcessVisibility(sRecommended);
                else {
                    this.setRecommndedAvailableProcessVisibility(sAvailable);
                    this.viewAllLinkLabel = 'View Less';
                }
            }
            if (this.selectedProcessObj && this.isCaseEditPageOpened == false && this.isFinished == true &&
                this.templateLabels.includes(this.selectedProcessObj.templateLabel)) {
                this.openEditTab();
            }else if(this.selectedProcessObj && this.isCaseEditPageOpened == false && this.selectedProcessObj.templateLabel=='Medicaid Newborn Notification' && this.isFinished == true && this.isNewbornLaunch==true){
                this.openEditTab();
            }
        }
        if (this.isCancelCaseSwitchON) {
            if (this.isTemplateAttached == true || this.showTRR || this.isCancelledCase) {
                this.showRecommendedActions = false;
                this.showAttachedTemplates = true;
                this.iconName = 'standard:call_history';
                this.headingName = 'Process History (' + this.executedProcessesList.length + ')';
            }
        }
        else {
            if (this.isTemplateAttached == true || this.showTRR) {
                this.showRecommendedActions = false;
                this.showAttachedTemplates = true;
                this.iconName = 'standard:call_history';
                this.headingName = 'Process History (' + this.executedProcessesList.length + ')';
            }
        }
    }

    checkAccessibility(lstLinks, caseData) {
        lstLinks.forEach((element) => {
            if (newProcesstemplateLabels.includes(element.ProcessType)) {
                this.setTemplateVisibility(element);
            } else {
                if ((element?.templateName && (element.templateName === SendPrintItemMember || element.templateName === SendClaimStatementMember || element.templateName === SendClaimStatementProvider))
                    || (element?.ProcessType && (element.ProcessType === SendPrintItemMember || element.ProcessType === SendClaimStatementMember || element.ProcessType === SendClaimStatementProvider))) {
                    if (hasCRMS_111_StridesAccess && this.ProfileList.includes(caseData.ProfileName) && this.Recordtypes.includes(caseData.CaseRecordType)) {
                        this.setTemplateVisibility(element);
                    }
                } else if (element.ProcessType == 'Invoice Request') {
                    if (hasCRMS_111_StridesAccess && (caseData.ProfileName === this.pharmacyProfile
                        || (this.ProfileList.includes(caseData.ProfileName) && (hasCRMS_206_CCSHumanaPharmacyAccess || hasCRMS_205_CCSPDPPharmacyPilot)))) {
                        element.ProcessType = 'Invoice Request - ' + element.CaseTemplateName;
                        this.setTemplateVisibility(element);
                    }
                } else if (element.ProcessType == 'Inactivate Rx') {
                    if (hasCRMS_111_StridesAccess && (caseData.ProfileName === this.pharmacyProfile
                        || (this.ProfileList.includes(caseData.ProfileName) && (hasCRMS_206_CCSHumanaPharmacyAccess || hasCRMS_205_CCSPDPPharmacyPilot)))) {
                        element.ProcessType = 'Inactivate Rx - ' + element.CaseTemplateName;
                        this.setTemplateVisibility(element);
                    }
                } else if (element.ProcessType == 'Mail Order Pharmacy Web Issues' || element.ProcessType == 'Centerwell Pharmacy Web Issues') { 

                    if (hasCRMS_111_StridesAccess && (caseData.ProfileName === this.pharmacyProfile
                        || (this.ProfileList.includes(caseData.ProfileName) && (hasCRMS_206_CCSHumanaPharmacyAccess || hasCRMS_205_CCSPDPPharmacyPilot)))) {
                        element.ProcessType = 'Mail Order Pharmacy Web Issues - ' + element.CaseTemplateName;
                        this.setTemplateVisibility(element);
                    }
                } else {
                    if (this.userGroup.bRcc && this.ProfileList.includes(caseData.ProfileName) && this.Recordtypes.includes(caseData.CaseRecordType)) {
                        this.setTemplateVisibility(element);
                    }
                }
            }
        });
        return lstLinks;
    }

    setTemplateVisibility(element) {
        element.hasAccess = true;
        if(element.templateName != uConstants.Update_Commercial_Demographics_API){
            this.lstVisibleTemplates.push(element);
        }
        else if(element.templateName == uConstants.Update_Commercial_Demographics_API && this.bSwitch5009031 == true){
            this.lstVisibleTemplates.push(element);
        }
        this.isTemplatesVisible = true;
    }

    launchProcess(event) {
        if (this.isTemplateAttached) return;
        this.selectedProcessName = event.target.dataset.name;
        this.selectedProcessObj = {};
        for (let i = 0; i <= this.templateList.length - 1; i++) {
            if (this.templateList[i].templateName == event.target.dataset.name) {
                this.selectedProcessObj = this.templateList[i];
                break;
            }
        }
        let caseId;
        if (this.pageName === 'New Case' || this.isnew) {
            caseId = this.uniqueCaseId;
        }
        else {
            caseId = this.recordId;
        }
        if (this.selectedProcessObj) {
            let sData = JSON.stringify(this.selectedProcessObj);
            invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                if (isConsole) {
                    invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                        invokeWorkspaceAPI('openSubtab', {
                            parentTabId: focusedTab.parentTabId,
                            pageReference: {
                                "type": "standard__component",
                                "attributes": {
                                    "componentName": "c__CaseProcess_LCMP_HUM"
                                },
                                "state": {
                                    c__caseNo: caseId,
                                    c__flowName: this.selectedProcessObj.templateLabel,
                                    c__flowData: sData,
                                    c__tabCloseSwitch: this.bSubtabSwitch,
                                    c__pageRef: this.pageRef,
                                    c__pageName: this.pageName,
                                    c__planMemberId: this.memPlanId,
                                    c__accountId: this.accountId
                                }
                            },
                            focus: true
                        }).
                            catch(function (error) {
                                console.log('error: ' + JSON.stringify(error));
                            });
                    });
                }
            });
        }
    }

    getFlowParameters(praramsString, objectName, templateSubmittionId) {
        let tempflowParams = [];
        if (this.pageName === 'New Case') { this.recordId = this.uniqueCaseId; }
        if (praramsString) {
            let params = praramsString.split(',');
            params.forEach(sParam => {
                let objParam = {};
                objParam.type = 'String';
                objParam.name = sParam;
                let sParamName = sParam;

                if (recordIdNames.includes(sParamName)) {
                    sParamName = 'CaseId';
                } else if (submissionIdNames.includes(sParamName)) {
                    sParamName = 'TemplateSubmissionId';
                }
                else if (sParamName == 'UserID') {
                    sParamName = 'UserId';
                }
                switch (sParamName) {
                    case 'CaseId':
                        objParam.value = this.recordId;
                        break;
                    case 'WhatType':
                        objParam.value = objectName;
                        break;
                    case 'TemplateSubmissionId':
                        objParam.value = templateSubmittionId;
                        break;
                    case 'UserId':
                        objParam.value = currentUserId;
                        break;
                    case 'EditSequenceNumber':
                        objParam.value = '1';
                        break;
                    case 'AccountId':
                        objParam.value = this.getParamvalue('Account');
                        break;
                    case 'PolicyMemberId':
                        objParam.value = this.getParamvalue('Plan');
                        break;
                    case 'MemberPlanId':
                        objParam.value = this.getParamvalue('Plan');
                        break;
                    case 'AccountID':
                        objParam.value = this.getParamvalue('Account');
                        break;
                    case 'policyMemberId':
                        objParam.value = this.getParamvalue('Plan');
                        break;
                    case 'Case_MemberPlanId':
                        objParam.value = this.getParamvalue('Plan');
                        break;
                    case 'Members_Name':
                        objParam.value = this.getParamvalue('bmemberName11');
                        break;
                    case 'Members_ID':
                        objParam.value = this.getParamvalue('Account11');
                        break;
                    case 'Members_DateOf_Birth':
                        objParam.value = this.getParamvalue('bmemberBirthday11');
                        break;
                     case 'Case_ID':
                        objParam.value = this.recordId;
                        break;
                     case 'TN':
                         objParam.value = this.getParamvalue('bTNValue11');
                         break;
                      case 'ParamInputSID':
                         objParam.value =templateSubmittionId;
                         break;
                    case 'isPharmacy':
                       objParam.type = 'Boolean';
                       objParam.value =false;
                     break;                       
                }
                tempflowParams.push(objParam);
            });
        }
        let paramsJSON = (tempflowParams) ? JSON.stringify(tempflowParams) : '';
        return paramsJSON;
    }

    getParamvalue(type) {
        let value;
        switch (type) {
            case 'Account':
                value = this.pageName === 'New Case' ? this.casedata?.objCase?.AccountId ?? '' : '';
                break;
            case 'Plan':
                value = this.pageName === 'New Case' ? this.casedata?.objCase?.Member_Plan_Id__c ?? '' : '';
                break;
                case 'Account11':
                    value = this.bmemberId;
                    break;
                case 'bmemberName11':
                   value = this.bmemberName ;
                    break;
                case 'bmemberBirthday11':               
                   value = this.bmemberBirthday ;
                   break;
                case 'bTNValue11':
                    value = this.bTNValue ;
                    break;
        }
        return value;
    }

    setRecommndedAvailableProcessVisibility(sType) {
        this.isTemplatesVisible = false;
        this.lstVisibleTemplates = [];
        if (sType === sRecommended) {
            this.RecommendedProcess.forEach(ele => {
                this.setTemplateVisibility(ele);
            });
        } else if (sType === sAvailable) {
            this.AvailableProcess.forEach(ele => {
                this.setTemplateVisibility(ele);
            })
        }
        this.isTemplatesVisible = this.lstVisibleTemplates.length ? true : false;
        this.noTemplateLinksMessage = (sType == sRecommended) ? noLinksRecommendedMessage : noLinksAvailableMessage;
    }

    handleViewAllClick(event) {

        if (event.target.dataset.name === 'View All') {
            this.setRecommndedAvailableProcessVisibility(sAvailable);
            this.viewAllLinkLabel = 'View Less';
        } else {
            this.setRecommndedAvailableProcessVisibility(sRecommended);
            this.viewAllLinkLabel = 'View All';
        }
    }

    sortArray(element) {
        try {
            element.sort((a, b) => {
                const nameA = a.templateLabel ? a.templateLabel.toUpperCase() : '';
                const nameB = b.templateLabel ? b.templateLabel.toUpperCase() : '';
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                return 0;
            });
            return element;
        } catch (e) {
            console.log("in catch>> " + e);
            return null;
        }
    }

    launchExecutedProcess(event) {
        this.templateId = event.target.dataset.targetId;
        this.selectedProcessName = event.target.dataset.name;
        this.templateSubmissionOwnerId =  event.target.dataset.townerid;
        if (this.selectedProcessName == 'Medicare and Medicaid Other Insurance Form') {
            const flowParams = { "flowParams": [{ name: "recordId", type: "String", value: this.recordId }] };
            var compDefinition = {
                componentDef: "c:callExecutedProcessHum",
                attributes:
                {
                    flowParams: flowParams,
                    flowName: 'Call_Medicare_OI_Flow'
                }
            };
            var encodedCompDef = btoa(JSON.stringify(compDefinition));
            invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                if (isConsole) {
                    invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                        let parenttabId;
                        if (focusedTab.parentTabId != null) {
                            parenttabId = focusedTab.parentTabId;
                        }
                        else {
                            parenttabId = focusedTab.tabId;
                        }
                        invokeWorkspaceAPI('openSubtab', {
                            parentTabId: parenttabId,
                            url: '/one/one.app#' + encodedCompDef,
                            focus: true
                        }).then(tabId => {
                            invokeWorkspaceAPI('setTabLabel', {
                                tabId: tabId,
                                label: 'Summary OI Flow'
                            });
                        });
                    });
                }
            });
        } else if (this.selectedProcessName && this.selectedProcessName.toLowerCase() === PCPUpdateChange.toLowerCase()) {
            let tempdata = {};
            tempdata.tempSubmissionId = event.target.dataset.id;
            tempdata.templateName = event.target.dataset.name;
            openLWCSubtab('pcpSummaryScreenHum', tempdata, { label: 'PCP Update/Change', icon: 'standard:case' });
        } else if (this.selectedProcessName && (this.selectedProcessName.toLowerCase() === SendPrintItemMember.toLowerCase() || this.selectedProcessName.toLowerCase() === SendClaimStatementMember.toLowerCase() || this.selectedProcessName.toLowerCase() === SendClaimStatementProvider.toLowerCase())) {
            let templatedata = {};
            templatedata.Id = this.templateId;
            templatedata.templatename = this.selectedProcessName;
            openLWCSubtab('sendPrintItemContainerHum', templatedata, { label: this.selectedProcessName, icon: 'standard:actions_and_buttons' });
        } else if (this.selectedProcessName && this.selectedProcessName.toLowerCase() === AuthReferralHistory.toLowerCase()) {
            let templatedata = {};
            templatedata.Id = this.templateId;
            templatedata.templatename = this.selectedProcessName;
            openLWCSubtab('authFlowSummary', templatedata, { label: this.selectedProcessName, icon: 'standard:actions_and_buttons' });
        } else if (this.selectedProcessName && this.selectedProcessName.toLowerCase().includes(InvoiceRequest.toLowerCase())) {
            let templatedata = {};
            templatedata.Id = this.templateId;
            templatedata.templatename = this.selectedProcessName;
            openLWCSubtab('invoiceSummary', templatedata, { label: this.selectedProcessName, icon: 'standard:actions_and_buttons' });
        } else if (this.selectedProcessName && this.selectedProcessName.toLowerCase().includes(InactivateRx.toLowerCase())) {
            let templatedata = {};
            templatedata.Id = this.templateId;
            templatedata.templatename = this.selectedProcessName;
            openLWCSubtab('invoiceSummary', templatedata, { label: this.selectedProcessName, icon: 'standard:actions_and_buttons' });
        }   else if (this.selectedProcessName && this.selectedProcessName.toLowerCase().includes(PharmacyWebIssue.toLowerCase())) { 
                let templatedata = {};
                templatedata.Id = this.templateId;
                templatedata.templatename = this.selectedProcessName;
                openLWCSubtab('invoiceSummary', templatedata, { label: this.selectedProcessName, icon: 'standard:actions_and_buttons' });
        } 
        else if (this.selectedProcessName && this.selectedProcessName.toLowerCase() === MedicaidNewbornNotification.toLowerCase()) {
            this.isNewbornLaunch = false;
            getNewbornTemplateDetails()
                .then(result => {
                    let tempObj = {};
                    tempObj.templateName = result.Template_Name__c;
                    tempObj.templateLabel = result.Template_Label__c;
                    tempObj.hasAccess = false;
                    tempObj.showInAction = result.IsVisible__c;
                    tempObj.params = result.Params__c;
                    tempObj.TemplateType = result.Template_Type__c;
                    tempObj.description = result.Description__c;
                    tempObj.flowParamsJSON = this.getFlowParameters(result.Params__c, this.objectApiName, this.templateSubmissionOwnerId);
                    tempObj.newbornSubmissionId = this.templateId;
                    this.selectedProcessObj = tempObj;
                    let sData = JSON.stringify(this.selectedProcessObj);
                    invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                        if(isConsole) {
                            invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                                invokeWorkspaceAPI('openSubtab', {
                                    parentTabId: focusedTab.parentTabId,
                                    pageReference: {
                                        "type": "standard__component",
                                        "attributes": {
                                            "componentName": "c__CaseProcess_LCMP_HUM"
                                        },
                                        "state": {
                                            c__caseNo: this.recordId,
                                            c__flowName: this.selectedProcessObj.templateLabel,
                                            c__flowData: sData,
                                            c__tabCloseSwitch: this.bSubtabSwitch,
                                            c__pageRef: this.pageRef,
                                            c__pageName: this.pageName,
                                            c__planMemberId: this.memPlanId,
                                            c__accountId: this.accountId
                                        }
                                    },
                                    focus: true
                                })
                                    .catch(function (error) {
                                        console.log('error: ' + JSON.stringify(error));
                                    });
                            });
                        }
                    });
                })
                .catch(Error => {
                    console.log('Error : ', this.error);
                });
        } else {
            this.openMed_Modal = true;
        }
    }

    childCloseEvent(event) {
        this.openMed_Modal = event.detail;
    }
    openEditTab() {
        isProcessAttached({ Id: this.recordId, uniqueuTempName: this.selectedProcessObj.templateLabel }).then(result => {
            let tabOpen = false;
            if (result) {
                invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    let parentTab = focusedTab.parentTabId;
                    invokeWorkspaceAPI('getAllTabInfo').then(atabsInfo => {
                        for (let i = 0; i < atabsInfo.length; i++) {
                            if (atabsInfo[i].tabId == parentTab && atabsInfo[i].pageReference) {
                                let stabsInfo = atabsInfo[i].subtabs;
                                for (let l = 0; l < stabsInfo.length; l++) {
                                    if (stabsInfo[l].pageReference && stabsInfo[l].pageReference.attributes) {
                                        let tabCmpName ='';
                                        let recId ='';
                                        let id15 = '';
                                        let cmpData = this.getTabNameAndRecordId(stabsInfo[l].pageReference);
                                        tabCmpName = cmpData.tabCmpName;
                                        recId = cmpData.recId;
                                        id15 = this.recordId.slice(0, 15);
                                        if(cmpData.isCaseTab && (recId == this.recordId || recId == id15)) {
                                            if(this.isEdit==true){
                                                if(this.selectedProcessObj.templateLabel=='Medicaid Newborn Notification'){
                                                    pubSubHum.fireCrossEvent(this.recordId, 'refreshCaseProcessSec', { 'flowName':'Medicaid Newborn Notification', 'isFinished':true });
                                                }
                                                tabOpen = true;
                                                this.dispatchEvent(new CustomEvent('refreshcase',{detail :{flowName:this.selectedProcessObj.templateLabel}}));
                                                break;
                                            }else{
                                                invokeWorkspaceAPI('closeTab', { tabId: stabsInfo[l].tabId });
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(tabOpen==false){
                            let casedata = {};
                            casedata.Id = this.recordId;
                            casedata.objApiName='Case';
                            casedata.flowname = this.selectedProcessObj.templateLabel;
                            casedata.caseComment = this.selectedProcessObj.templateLabel === 'Mail Order Pharmacy Web Issues'?'Mail Order Pharmacy Web Issues process attached to the case and transferred to the Web Ticket team.':'';
                            openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
                        }
                    });
                });
                this.isCaseEditPageOpened = true;
            }
        });
    }

    onViewAllClick(event) {
        let hasTabOpened = false;
        const data = { recordId: this.recordId, relatedListName: 'Process History', showViewAll: true, caseNumber: this.caseNumber };
        let componentDef = { componentDef: "c:caseTRRProcessDetails", attributes: { encodedData: data } };
        let encodedComponentDef = btoa(JSON.stringify(componentDef));
        invokeWorkspaceAPI("getFocusedTabInfo").then(focusedTab => {
            invokeWorkspaceAPI('getTabInfo', { tabId: focusedTab.parentTabId != null ? focusedTab.parentTabId : focusedTab.tabId }).then(tabDetails => {
                if (tabDetails && tabDetails.subtabs && tabDetails.subtabs.length > 0) {
                    tabDetails.subtabs.forEach((item) => {
                        if (item.customTitle == 'Process History' && item.pageReference &&
                            item.pageReference?.attributes && item.pageReference?.attributes?.attributes &&
                            item.pageReference?.attributes?.attributes?.encodedData &&
                            item.pageReference.attributes.attributes.encodedData.recordId == this.recordId) {
                            invokeWorkspaceAPI("openTab", { url: item.url });
                            invokeWorkspaceAPI("refreshTab", { tabId: item.tabId, includeAllSubtabs: false });
                            hasTabOpened = true;
                        }
                    });
                    if (hasTabOpened == false) {
                        invokeWorkspaceAPI("openSubtab", {
                            parentTabId: focusedTab.parentTabId != null ? focusedTab.parentTabId : focusedTab.tabId,
                            url: "/one/one.app#" + encodedComponentDef,
                            focus: true
                        }).then(newTabId => {
                            let tabLabel = 'Process History';
                            invokeWorkspaceAPI("setTabLabel", { tabId: newTabId, label: tabLabel });
                            invokeWorkspaceAPI("setTabIcon", { tabId: newTabId, icon: 'standard:process', iconAlt: '' });
                        });
                    }
                } else {
                    invokeWorkspaceAPI("openSubtab", {
                        parentTabId: focusedTab.parentTabId != null ? focusedTab.parentTabId : focusedTab.tabId,
                        url: "/one/one.app#" + encodedComponentDef,
                        focus: true
                    }).then(newTabId => {
                        let tabLabel = 'Process History';
                        invokeWorkspaceAPI("setTabLabel", { tabId: newTabId, label: tabLabel });
                        invokeWorkspaceAPI("setTabIcon", { tabId: newTabId, icon: 'standard:process', iconAlt: '' });
                    });
                }
            });
        });
    }
    launchExecutedTRRProcess(event) {
        let selectedProcessName = event.target.dataset.name;
        invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
            if (isConsole) {
                let tempData = {
                    templateName: selectedProcessName,
                    templateId: this.templateId,
                    recordId: this.recordId
                };
                let componentDef = {
                    componentDef: "c:displayTRRDetailsLC",
                    attributes: { encodedData: tempData }
                };
                let encodedComponentDef = btoa(JSON.stringify(componentDef));
                let url = "/one/one.app#" + encodedComponentDef;
                invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    invokeWorkspaceAPI('openSubtab', {
                        parentTabId: focusedTab.parentTabId != null ? focusedTab.parentTabId : focusedTab.tabId,
                        url: url,
                        focus: true
                    }).then(newTabId => {
                        let tabLabel = selectedProcessName;
                        invokeWorkspaceAPI("setTabLabel", { tabId: newTabId, label: tabLabel });
                        invokeWorkspaceAPI("setTabIcon", { tabId: newTabId, icon: 'standard:process', iconAlt: '' });
                    });
                });
            }
        });
    }
    checkProcessEvaluationLogic(evaluatedProcess) {
        try {
            this.lstVisibleTemplates = [];
            let tempFinalList = [];
            this.AvailableProcess = [];
            this.RecommendedProcess = [];
            this.AvailableProcessLabels = [];
            this.RecommendedProcessLabels = [];
            console.log('evaluatedProcess------', evaluatedProcess);
            Object.entries(evaluatedProcess).forEach(objProcess => {
                if (objProcess[0].toLocaleLowerCase() === sRecommended.toLocaleLowerCase()) {
                    objProcess[1].forEach(ele => {
                        this.RecommendedProcess.push(this.mapConfiguredTemplates.get(ele.templateName));
                        this.RecommendedProcessLabels.push(ele.templateName);
                        if (!this.AvailableProcessLabels.includes(ele.templateName)) {
                            this.AvailableProcess.push(this.mapConfiguredTemplates.get(ele.templateName));
                            this.AvailableProcessLabels.push(ele.templateName);
                        }
                    })
                }
                else if (objProcess[0].toLocaleLowerCase() === sAvailable.toLocaleLowerCase()) {
                    objProcess[1].forEach(ele => {
                        if (!this.AvailableProcessLabels.includes(ele.templateName)) {
                            this.AvailableProcess.push(this.mapConfiguredTemplates.get(ele.templateName));
                            this.AvailableProcessLabels.push(ele.templateName);
                        }
                    })
                }
            });
            if (this.AvailableProcess?.length > 1) {
                this.AvailableProcess = this.sortArray(this.AvailableProcess);
            }
            if (this.RecommendedProcess?.length > 1) {
                this.RecommendedProcess = this.sortArray(this.RecommendedProcess);
            }

        } catch (err) {
            console.log('123---error', err);
        }
    }
    getTabNameAndRecordId(pageReference){
        let cmpData = {};
        if(pageReference.attributes.componentName !=undefined){
            cmpData.tabCmpName = pageReference.attributes.componentName;
            cmpData.recId = pageReference.state ? pageReference.state.c__recordId : '';
        }else if(pageReference.attributes.name!=undefined){
            cmpData.tabCmpName = pageReference.attributes.name;
            cmpData.recId = pageReference.attributes.attributes && pageReference.attributes.attributes.encodedData?pageReference.attributes.attributes.encodedData.Id:'';
        }else if(cmpData.recId==undefined && pageReference.state && pageReference.state.c__recordId){
            cmpData.recId = pageReference.state.c__recordId;
        }
        if('c__HumEditButtonOverrideLightning' == cmpData.tabCmpName || cmpData.tabCmpName == 'c:caseInformationComponentHum') {
            cmpData.isCaseTab = true;
        }
        return cmpData;
    }

    subscribeToLMSChannel() {
        if (!this.subscription) {
            this.messageContext = createMessageContext();
            this.subscription = subscribe(
                this.messageContext,
                refreshCaseProcessLMS,
                (message) => this.handleRefreshLMSMessage(message),
                { scope: APPLICATION_SCOPE }
            );
        }
    }

    handleRefreshLMSMessage(message){
        this.isCancelBtnClicked = !message.isFinished;
        this.isFinished = message.isFinished;
        this.getTemplateSubmissionData(message.recordId);
    }

    getSwitchData(){
        return new Promise((resolve, reject) => {
            isCRMFunctionalityONJS({ sStoryNumber: ['5116621'] })
                .then(result => {
                    this.isCancelCaseSwitchON = result['5116621'];
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })
    }     
}