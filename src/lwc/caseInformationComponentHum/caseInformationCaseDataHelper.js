/*******************************************************************************************************************************
LWC JS Name : caseInformationCaseDataHelper.js
Function    : This JS serves as helper to caseInformationComponentHum.js

Modification Log:
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Prasuna Pattabhi                                   3/1/2024              US 5373580 :  Find Case related JS files which might run into size limitation
*********************************************************************************************************************************/
import getProcessDetails from '@salesforce/apex/CaseDetails_LC_Hum.getProcessDetails';
import CheckCaseTemplates from '@salesforce/apex/CaseDetails_LC_Hum.CheckCaseTemplates';
import { preFillValues ,getCaseFieldValues} from './caseInformationComponentHelper';
import getRecordTypeChange from '@salesforce/apex/CaseDetails_LC_Hum.getRecordTypeChange';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { deleteObjProperties,toastMsge} from 'c/crmUtilityHum';
import saveCase from '@salesforce/apex/PharmacyCaseSave_LC_HUM.saveCaseDetails';
import getlaunchEmmeURL from '@salesforce/apex/METEnrollmentCaseEditTable_LC_HUM.getlaunchEmmeURL';
import getCaseNumber from '@salesforce/apex/CaseDetails_LC_Hum.getCaseNumber';

/**
      * Method Name : retainCaseCommentFor206PermSet
      * @param {*}
      * Function : this function is being called from handleRecordTypeChange method and trasfer the case comments
      *             entered when record type switches
      */
export function retainCaseCommentFor206PermSet() {
    if (!this.isHPCaseRecType && this.bIsMedicareCalls == true) {
        const commentsissValue = this.template.querySelector("[data-id='commentsiss']").value;
        const commentsresValue = this.template.querySelector("[data-id='commentsres']").value;
        if ((commentsresValue != '' && commentsresValue != undefined) && (commentsissValue != '' && commentsissValue != undefined)) {
            this.swtichComment = 'Medicare Calls Issue: ' + commentsissValue + '     ' + 'Medicare Calls Resolution: ' + commentsresValue;
        }
        else if ((commentsissValue != '' && commentsissValue != undefined) && (commentsresValue == '' || commentsresValue == undefined)) {
            this.swtichComment = 'Medicare Calls Issue: ' + commentsissValue;
        }
        else if ((commentsissValue == '' || commentsissValue == undefined) && (commentsresValue != '' && commentsresValue != undefined)) {
            this.swtichComment = 'Medicare Calls Resolution: ' + commentsresValue;
        }
        else if (commentsissValue == '' && commentsresValue == '') {
            this.swtichComment = '';
        }
    }
    else {
        const caseCommentValue = this.template.querySelector("lightning-textarea").value;
        this.caseCommentIss = '';
        this.swtichComment = caseCommentValue;
    }
    if (this.swtichComment != '' && this.swtichComment != undefined && this.swtichComment.length > 1900) {
        this.swtichComment = this.swtichComment.substring(0, 1900);
    }
}

/**
     * Method Name : handleRecordtypeChange
     * @param {*} event
     * Function : this function is being called for record type change
     */

export async function handleRecordtypeChange(classificationId, intentId) {
    const rtField = this.template.querySelector('[data-id="caseRecordTypeName"]');
    if (rtField != undefined) {
        const result = await getRecordTypeChange({ clasId: this.case.Classification_Id__c, intId: this.case.Intent_Id__c, caseRt: rtField.value, clasType: this.resultData.prefillValues.classificationType });
        if (result) {
            if (result.returnRT) {
                if (this.userAccess.CRMS_206_CCSHumanaPharmacyAccess == true) {
                    this.isHPCaseRecType = this.resultData.prefillValues.caseRecordTypeName.indexOf('HP') == -1 ? false : true;
                    retainCaseCommentFor206PermSet.call(this);
                    this.urlCaseComment = this.swtichComment;
                    this.caseCommentRes = this.swtichComment;
                }
                const rtName = result.returnRT.split('=')[0];
                const rtId = result.returnRT.split('=')[1];
                rtField.value = rtName;
                this.caseUpdateRT = rtId;
                this.resultData.objCase.RecordTypeId = rtId;
                this.resultData.prefillValues.caseRecordTypeName = rtName;
                this.resultData.ctciModel.classificationName = this.case.Classification_Id__c;
                this.resultData.ctciModel.intentName = this.case.Intent_Id__c;
                if (result.ctciObj) {
                    this.resultData.ctciModel.ctciObj = result.ctciObj;
                }
                await preFillValues.call(this,this.resultData);
            }
        }
    }
}

/**
     * Method: handleTypeChange
     * @param {*} event
     * Function: this method is used to get case type values on change of case Type.
     */
export function handleTypeChange(event) {
    this.caseType = event.detail.value + '_' + Date.now();
}

/**
 * Method: handleSubTypeChange
 * @param {*} event
 * Function: this method is used to get case Subtype values on change of case Subtype.
 */
export function handleSubTypeChange(event) {
    this.caseSubtype = event.detail.value + '_' + Date.now();
}
/**
     * Method Name : handleClassificationChange
     * @param {*} event
     * Function : this function is being called from handlechange function on the change of classification value
     */
export function handleClassificationChange(event) {
    // below line is used to assign classification id to case object (this.case)
    this.case.Classification_Id__c = event.detail.value;
    var selectedLabel = event.target.options.find(opt => opt.value === event.detail.value).label;
    this.newbornClassification = selectedLabel;
    this.classificationLabel = selectedLabel;
    if (selectedLabel == 'Claims') {
        this.enableClaimButton = true;
    } else {
        this.enableClaimButton = false;
    }
    const classificatioToIntent = this.resultData.ctciModel.classificationToIntent;
    const intentField = this.template.querySelector('[data-id="case-intent"]');
    if (classificatioToIntent[event.detail.value]) {
        intentField.options = [...[{ label: '--None--', value: '--None--' }], ...classificatioToIntent[event.detail.value]];
    }
    intentField.value = '--None--';
    intentField.disabled = classificatioToIntent[event.detail.value] ? false : true;
}

/**
 * Method Name : handleIntentChange
 * @param {*} event
 * Function : this function is being called from handlechange function on the change of Intent value
 */
export async function handleIntentChange(event) {
    const classWithIntentCTCI = this.resultData.ctciModel.mpOfclassificationIntentToCTCIId;
    // classWithIntentCTCI ==> this variable is to store map<classification Id, map<Intent Id, CTCI Id>>
    const classification = this.template.querySelector('[data-id="case-classification"]');
    //Below variable will give map of intent id and ctci id
    const intentWithCTCI = classWithIntentCTCI[classification.value];
    this.case[event.target.name] = intentWithCTCI[event.detail.value] ? intentWithCTCI[event.detail.value] : null;
    // below line is used to assign Intent id to case object (this.case)
    this.case.Intent_Id__c = event.detail.value;
    if (event.detail.value != null && event.detail.value != '') {
        var selectedLabel = event.target.options.find(opt => opt.value === event.detail.value).label;
        this.newbornIntent = selectedLabel;
        this.intentLabel = selectedLabel;
    }
    // to handle mentor documents
    if(this.profileName === 'Humana Pharmacy Specialist' || (this.profileName === 'Customer Care Specialist' || this.profileName === 'Customer Care Supervisor')) {
        this.bShowMentorDocs = true;
        if(this.classificationLabel != null && this.intentLabel != null){
            getCaseFieldValues.call(this,this.caseId,this.classificationLabel,this.intentLabel); 
        }                
    }
    // to handle Record type change
    await handleRecordtypeChange.call(this,classification.value, event.detail.value);
    handleLogCodeVisibility.call(this,classification.value, event.detail.value, false);
}
/**
     * Method Name : handlePharmacyLogCode
     * @param {*} event
     * Function : this function is being called from handlechange function on the change of Humana Pharmacy Log Code value
     */
export function handlePharmacyLogCode(event) {
    this.pharmacyLogCode = event.target.value === '--None--' ? null : event.target.value;
}
/**
       * Method Name : handleMedicareChange
       * @param {*} event
       * Function : this function is being called from handlechange function on the change of Medicare Part C Pard D value value
       */
export function handleMedicareChange(event) {
    // below line is used to assign classification id to case object (this.case)
    this.case.Call_Benefit_Category__c = event.detail.value;
}
/**
   * Method Name : setTabLabelOnEdit
   * Function: This method will set the tab label once user clicked on edit button from case detail page
   */
export async function  setTabLabelOnEdit() {
    if (this.isEdit) {
        try {
            if (this.forRccTemplate) {
                let focusedTab = await invokeWorkspaceAPI("getFocusedTabInfo");
                let tabDetails = await invokeWorkspaceAPI("getTabInfo", {
                    tabId: focusedTab.parentTabId
                });
                if (tabDetails && tabDetails.subtabs) {
                    tabDetails.subtabs.forEach((item) => {
                        if (item.customTitle === this.caseNumber) {
                            invokeWorkspaceAPI("setTabLabel", { tabId: item.tabId, label: "Edit " + this.caseNumber });
                            invokeWorkspaceAPI("setTabIcon", { tabId: item.tabId, icon: 'standard:case', iconAlt: "Case" });
                        }
                    });

                }
            } else {
                let focusedTab = await invokeWorkspaceAPI("getFocusedTabInfo");
                await invokeWorkspaceAPI("setTabLabel", { tabId: focusedTab.tabId, label: "Edit " + this.caseNumber });
                await invokeWorkspaceAPI("setTabIcon", { tabId: focusedTab.tabId, icon: 'standard:case', iconAlt: "Case" });
            }
        } catch (error) {
            this.error = error;
            console.log('error in setTabLabelOnEdit', error);
        }
    }
}
/* *
       * Method Name :- Check Templates
       * Function:- To shoe toast error message if templates are auot routed on case 
       * */

export async function checkTemplates(caseid) {
    const result = await CheckCaseTemplates({ caseId: caseid });
    if (result) {
        if (result.bdisablecloseCancel == 'true') {
         if(this.bisSwitchOn4932577 == true){
         showToastMessage.call(this,caseid);
         }
         else{
         this.dispatchEvent(
                new ShowToastEvent({
                    title: "",
                    message: 'The case cannot be edited because the Process Template is being auto routed',
                    variant: 'error',
                    mode: "dismissible"
                })
            );
            const focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
            this.closeTab(focusedTab, caseid);
            }
        } 
        if(this.switch_5231359){
            if (result.disableClose == 'true') this.closeCaseDisabled = true;
        }
    }	
    }
    export async function showToastMessage(caseid)
		{
			this.dispatchEvent(
					new ShowToastEvent({
                        title: "",
                        message: this.labels.AutoRouteToastMessage,
                        variant: 'error',
						mode: "dismissible"
                    })
				);
				const focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
				this.closeTab(focusedTab,caseid);
		}
    
        /**
     * Method Name : handleLogCodeVisibility
     * @param {*} classification, intentId
     * Function : this function is being called from renderedCallBack,handleIntentChange to check the visivility of HP log code field
     */
    export function handleLogCodeVisibility(classification, intentId, onLoadEdit) {
        try {
            const classWithIntentCTCI =
                this.resultData.ctciModel.mpOfclassificationIntentToCTCIId;
            const intentWithCTCI = classWithIntentCTCI[classification];
            // Below const is used to store values for Service Model Type field of CTCI object to show log code for HP member case
            const serviceModelTypes = ['Insurance/Plan', 'Humana Pharmacy'];
            const caseClassificationTypes = ['Calls (RSO)', 'HP Clinical Services', 'HP Finance Ops', 'HP RxE Calls', 'HP Specialty Calls', 'Humana Pharmacy Calls', 'Humana Pharmacy Web Chat'];
            //Below const is used to store map of CTCI id and CTCI object.
            const mapOfCTCIIdToCTCI = this.resultData.ctciModel.mapOfCTCIIdToCTCI;
            const ctciId = intentWithCTCI[intentId];
            let ctciObj = {};
            if (!onLoadEdit) {
                let ctciList = Object.values(mapOfCTCIIdToCTCI);
                let filteredCTCI = ctciList.filter((item) => {
                    return (
                        item.Classification__c === classification &&
                        item.Intent__c === intentId
                    );
                });
                ctciObj = filteredCTCI[0];
            } else {
                ctciObj = this.resultData.ctciModel.ctciObj;
            }

            let caseClassificationType = this.resultData.objCase.Classification_Type__c;
            //Below line is to store service Model Type value which is coming from apex.
            const serviceModelType = ctciObj && ctciObj.hasOwnProperty('Service_Model_Type__c') ? ctciObj.Service_Model_Type__c : null;
			this.serviceModel = serviceModelType;
            // This variable is used to set the true/false based on the case record type. if it is HP Member Case and serviceModel type has value.
            this.pickListVisible = (this.resultData.prefillValues.caseRecordTypeName.search("HP") !== -1) && (serviceModelTypes.includes(serviceModelType) && caseClassificationTypes.includes(caseClassificationType));

            setTimeout(() => {
                //Below if is added to check if the case is HP member case and Service Model Type has any of single value 'Insurance/Plan'/ 'Humana Specialty Pharmacy'/ 'Humana Pharmacy'. Display HP log code field else hide it.
                if (this.pickListVisible && (this.resultData.prefillValues.caseRecordTypeName.search("HP") !== -1) && ctciObj && ctciObj.hasOwnProperty('Humana_Pharmacy_Log_Code__c') && ctciObj.Humana_Pharmacy_Log_Code__c !== '') {
                    const logCoge = this.template.querySelector(
                        '[data-id="case-logcode"]'
                    );
                    if (logCoge != null && logCoge != undefined) {
                        logCoge.value = ctciObj.Humana_Pharmacy_Log_Code__c;
                    }
                    this.pharmacyLogCode = ctciObj.Humana_Pharmacy_Log_Code__c;
                } else if (!this.pickListVisible) {
                    this.pharmacyLogCode = null;
                }
            }, 100);

        } catch (error) {
            console.log('Error in handleLogCodeVisibility--- ', error);
        }
    }
/**
     * Method Name : navigateToCaseDetailPage
     * @param {*} caseId
     * Function : This method is to close the create case screen and open case detail screen in subtab.
     */
export async function navigateToCaseDetailPage(caseId) {
    try {
        const focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (focusedTab.parentTabId != null) {

            const tabInfo = await invokeWorkspaceAPI("getTabInfo", {
                tabId: focusedTab.parentTabId
            });
            let subTabs = tabInfo.subtabs;

            let subtabToRefresh = subTabs.filter((item) => {
                return (
                    item.hasOwnProperty("pageReference") &&
                    item.pageReference.attributes.actionName === "view" &&
                    item.pageReference.attributes.recordId === caseId
                );
            });
            // closing caseDetail tab before closing edit tab and reopening it again to refresh the tab details
            if (subtabToRefresh.length > 0) {
                await invokeWorkspaceAPI("closeTab", {
                    tabId: subtabToRefresh[0].tabId
                });
            }
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: focusedTab.parentTabId,
                focus: true,
                recordId: caseId
            });
            this.closeTab(focusedTab, caseId);
        } //this else will run when user is on Humana Pharmacy tab and edit the case (case edit screen will open as a parent tab).
        else if (focusedTab.parentTabId == null) {
            this.closeTab(focusedTab, caseId, "primaryTab");
        }
    } catch (error) {
        this.error = error;
    }
}
/**
     * Method Name : createCase
     * Function : this method is being called from onSave function to create the case and call the navigatToCaseDetailPage method.
     */
export async function createCase() {
    try {
        deleteObjProperties(this.case, ['caseRecordTypeName', 'accountName', 'medicareId', 'classificationType', 'LastModifiedby_Queue__c', 'Created_By_Queue__c', 'Work_Queue_View_Name__c', 'Re_Open_Case_Date__c', 'Doc_Type__c', 'Pend_Key__c']);
        let finalCaseData = Object.assign(this.resultData.objCase, this.case);
        // below line is used to remove Case_Owner__C in Edit case scenario so that case owner will remain same.
        finalCaseData.hasOwnProperty('Id') ? deleteObjProperties(finalCaseData, ['Case_Owner__c']) : '';
        finalCaseData.hasOwnProperty('Guided_Process_Flag__c') ? deleteObjProperties(finalCaseData, ['Guided_Process_Flag__c']) : '';
        if (finalCaseData.Status != 'Pending - Response') {
            finalCaseData.Response_Status__c = '';
        }
        if (finalCaseData.Member_Plan_Id__c == '') {
            finalCaseData.Member_Plan_Id__c = null;
        }
        let isClosedCase = this.resultData.prefillValues.caseRecordTypeName.indexOf('Closed') == -1 ? false : true;
        if (this.caseComment.CommentBody == null || this.caseComment.CommentBody == undefined) {
            this.caseComment.CommentBody = '';
        }

        if (this.flowName == 'Medicare and Medicaid Other Insurance Form' && this.processResultDetails != undefined && this.processResultDetails.isOItemplateAttached == 'true' && finalCaseData.Status == 'Closed') {
            finalCaseData.Status = 'Pending - Response';
        }

        if (this.flowName != '' && this.processResultDetails != undefined && this.processResultDetails.sAutoRouteStatus != undefined) {
            finalCaseData.Autoroute_Status__c = this.processResultDetails.sAutoRouteStatus;

        }

        const result = await saveCase({
            caseData: JSON.stringify(finalCaseData),
            caseCommentData: JSON.stringify(this.caseComment),
            recordId: this.recordId,
            profileName: this.resultData.prefillValues.profileName,
            isEdit: this.isEdit,
            isClosedCase: isClosedCase,
            showMedicareCallsComments: this.showMedicareCallsComments,
            caseCommentIss: this.caseCommentIss,
            caseCommentRes: this.caseCommentRes,
            bMedicareCalls: this.isCallBenefitCategoryView,
            sPrevSubType: this.prevSubType,
            METTasklist: this.METTasklist,
            sClaimList: this.claimList
        });

        if (result) {
            if ((this.comment != undefined && this.comment != '') && (this.pharmacyLogCode != undefined && this.pharmacyLogCode != '')) {
                //Call epost to update comments
                this.sendRequestToEpost(result);
            }

            // Attach template if created on New Case Page 
            if (this.isNewCase) {
                if (this.template.querySelector('c-case-process-launch-hum') != null) {
                    this.template.querySelector('c-case-process-launch-hum').attachProcess(result);
                }
            }

            if (this.METTasklist != '' && this.METTasklist != undefined && this.METTasklist != null) {
                await getlaunchEmmeURL({ lstTask: this.METTasklist, sCaseRecordId: this.recordId })
                    .then((data) => {
                        if (data != '') {
                            //Base64 encode launceeme
                            let launchEMMEURL = '';
                            var emmeSplit = data.split('HIDDENTARGET=');
                            launchEMMEURL = emmeSplit[0] + 'HIDDENTARGET=' + window.btoa(emmeSplit[1]);
                            window.open(launchEMMEURL, 'LaunchEMME');
                        }
                    }).catch(error => {
                        this.dispatchEvent(
                            new ShowToastEvent({
                                title: 'Error! ' + error.message,
                                message: error.message,
                                variant: 'error',
                            }),
                        );
                    });
            }
            if (result && this.ClickedTransfer == false && this.clickedCloseCase == false) {
                navigateToCaseDetailPage.call(this,result);
                toastMsge('', 'Case was successfully saved', 'success', 'pester');
            }
            else if (result && (this.ClickedTransfer == true || this.clickedCloseCase == true)) {
                this.tempcaseid = result;
                if (this.ClickedTransfer == true)
                    this.isSaveTransfer = true;
                if (this.ClickedClose == true)
                    this.isCloseCase = true;
                if (this.clickedCloseCase) {
                    this.closeCaseDisabled = true;
                    this.saveTransferDisabled = true;
                    if(this.bisSwitchOn4918290 == true){
                        if (this.isEdit) {
                            toastMsge('', 'Case '+`${this.caseNumber}`+' was successfully closed', 'success', 'pester');
                        } else {
                            getCaseNumber({CaseId:result}).then(data =>{ 
                            toastMsge('', 'Case '+data+ '  was successfully closed', 'success', 'pester');})
                        }
                    } else {
                    if (this.isEdit) {
                        toastMsge('', 'Case was successfully saved', 'success', 'pester');   
                    } else {
                        toastMsge('', 'Case was successfully created', 'success', 'pester');
                    }
                    }
                    navigateToCaseDetailPage.call(this,result);
                }
            }
        }
    } catch (error) {
        console.log("error in create case func---> ", error);
        let message = error?.body?.message?.replace(/&amp;/g, "&").replace(/&quot;/g, '"') ?? '';
        if (message.includes('FIELD_CUSTOM_VALIDATION_EXCEPTION,')) {
            /**  this.showPageMsg(false, message.split('FIELD_CUSTOM_VALIDATION_EXCEPTION,').pop().split('Please update one of the fields to resolve this error.:')[0]);*/
            var splitErrorMsg = message.split('FIELD_CUSTOM_VALIDATION_EXCEPTION,').pop().split('Please update one of the fields to resolve this error.:')[0];
            if (splitErrorMsg.includes('[G_A_Reason__c]')) {
                this.showPageMsg(false, splitErrorMsg.split('G_A_Reason__c')[0].slice(0, -3))
            }
            else if (splitErrorMsg.includes('[Complaint_Reason__c]')) {
                this.showPageMsg(false, splitErrorMsg.split('Complaint_Reason__c')[0].slice(0, -3))
            }
            else {
                this.showPageMsg(false, message.split('FIELD_CUSTOM_VALIDATION_EXCEPTION,').pop().split('Please update one of the fields to resolve this error.:')[0]);
            }
        } else {
            toastMsge("", message, "error", "pester");
        }
    }
}
export async function fetchTabInfo(focusedTab, caseId) {
    try {
        const tabInfo = await invokeWorkspaceAPI("getTabInfo", {
            tabId: focusedTab.parentTabId
        });
        let subTabs = tabInfo.subtabs;
        let subtabToRefresh = subTabs.filter((item) => {
            return (
                item.hasOwnProperty("pageReference") &&
                item.pageReference.attributes.actionName === "view" &&
                item.pageReference.attributes.recordId === caseId
            );
        });

        if (subtabToRefresh.length > 0 && subtabToRefresh[0].hasOwnProperty('tabId')) {
            this.refreshSubtab(subtabToRefresh[0].tabId);
        }
    } catch (error) {
        console.log("error in tabInfo--", error);
    }
}
export async function toFocusTab(tabId) {
    try {
        await invokeWorkspaceAPI("focusTab", {
            tabId: tabId
        });
        this.refreshSubtab(tabId);
    } catch (error) {
        console.log("error in toFocusTab--", error);
    }
}