/*
LWC Name        : pcpUpdateContainer.js
Function        : LWC to update PCP change.

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     08/12/2022                 initial version
* Nirmal Garg                     09/26/2022                 US2961209
* Nirmal Garg                     10/12/2022                 US3856109
* Aishwarya Pawar                 12/05/2022                 REQ - 3959516
* Aishwarya Pawar                 12/19/2022                 DF - 6816 Fix
* Divya Bhamre                    01/19/2023                 REQ - 4167678 - T1PRJ0865978- MF 23749-   Lightning-PCP Change-  Additional changes
* Swapnali Sonawane	              02/23/2023                 US 4178421 Determine the logic to display templates on New case page and attach case to the template on Launch
* Abhishek Mangutkar           	  03/01/2023                 US 4286520 Remove logic for assign member plan id for logging cases
* KalyaniPachpol                  03/02/2023                 US-4305931
* Kalyani Pachpol                 03/13/2023                 US-4363678
* Kalyani Pachpol                 03/17/2023                 DF-7399
* Nirmal Garg                     04/12/2023                 US4460894
* Kalyani Pachpol                 05/29/2023                 DF-7688
* Abhishek Mangutkar           	  05/31/2023                 DF-7709
* Swapnali Sonawane               08/16/2023                 US-4938422 PCP Change Template
* Swapnali Sonawane               09/25/2023                 US-5073478 PCP Change- Auto update CI/CAS
*****************************************************************************************************************************/
import { LightningElement, api, track, wire } from 'lwc';
import { getModel } from './layoutConfig';
import pcpquestionstemplate from './templates/pcpquestions.html';
import pcpprovidersearchscreentemplate from './templates/pcpprovidersearch.html';
import pcpservicefundquestionstemplate from './templates/pcpservicefundquestions.html';
import pcpSummaryScreenemplate from './templates/pcpSummaryScreen.html';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import { getMemberPlanDetails } from 'c/genericMemberPlanDetails';
import getProviderSearchVisibilty from '@salesforce/apex/PCPUpdate_LC_HUM.getProviderSearchVisibilty';
import { generateTemplateData, updateTemplateSubmissionData, deleteUnsavedData } from 'c/genericTemplateDataCreationHum';
import { getPCPDetails } from 'c/pcpDetailsHum';
import pubSubHum from 'c/pubSubHum';
import { CurrentPageReference } from 'lightning/navigation';
import { invokeWorkspaceAPI, openLWCSubtab } from "c/workSpaceUtilityComponentHum";
import pcpserviceerror from '@salesforce/label/c.BenefitsServiceError';
import required_msg_HUM from '@salesforce/label/c.required_msg_HUM';
import LOG_NOTE from '@salesforce/label/c.PCPLogNoteMessage';
const SF_QUESTION = "Did you receive a Service Fund edit when attempting to change the member's PCP";
import pcpPlanTypes from '@salesforce/label/c.PCPPlanTypes';
import currentPCPName from '@salesforce/label/c.CurrentPCPName'

import genericCaseActionHum from 'c/genericCaseActionHum';
import { getCaseData, attachInteractionToCase, createNewCaseId, getInteractionId } from 'c/genericCaseActionHum';
import { updateAnswer, redirectToCasePages, getNetworkData, updateMemberPCP, validatePCPEligibilty } from './pcpUpdateHelper';
import { getFormatDate } from 'c/crmUtilityHum';

const PCP_QUESTION = 'PCP Question';
export default class PcpUpdateContainer extends genericCaseActionHum {
    @wire(CurrentPageReference) pageRef;
    attachCaseNote = LOG_NOTE;
    @track tempdatacreated = false;
    @api recordId;
    @api caseRecordId;
    @api objectApiName;
    @api currentpcpname;
    @api caspersonid;
    @api mtvpersonid;
    @track requestEffectiveDate;
    @track effectiveDate;
    @api flowdata;
    @track keyword = '';
    @api memid;
    @api pagename;
    isdefaultLoad = false;
    @track pcpquestionscreen = true;
    @track pcpprovidersearchscreen = false;
    @track PCPFundData = {};
    @track pcpservicefundscreen = false;
    @track pcpservicefund = false;
    @track pcpsummaryscreen = false;
    @track changeReasonOptions = getModel('ChangeReasons');
    @track pcpConstants = getModel('contants');
    @track otherInputEnabled = false;
    @track changeReason = '';
    @track disableFinishButton = true;
    @track disableNextSaveButton = true;
    @track pcpupdatequestion = [];
    @track providerSearchQuestions = [];
    @track serviceFundQuestions = [];
    @track inputMiles = '3';
    @track inputPhysicianName;
    @track inputZipCode;
    @track data = [];
    @track columns = getModel("providersearchcolumns");
    @track personid;
    @track memberplanid = '';
    @track pcpActiveFutureData = [];
    @track soldproductdetails;
    @track pcpServiceResponse;
    @track providerSearchList = [];
    @track memberdob;
    @track accountId;
    @track memberPlanId;
    @track platformpointerlist;
    @track displaylogging = false;
    @track displayFinishButton = false;
    @track filteredProviders = [];
    @track loaded = true;
    @track totalCount = 0;
    @track filteredcount;
    @track templateMasterData = [];
    @track templateFields = [];

    @track product;
    @track searchperformed = false;
    @track displayExistingCasePopover = false;

    @track newcase = true;
    @track existingcase = false;
    @track displayprevious = false;


    @track lstPhysicianName;
    @track selectedPhysicianData = {};
    @track physicianSearchTableModel;
    @track displayPCPQuestionScreen = true;
    @track physicanList = [];
    @track pcpTemplateDetails = [];
    @track pcpTemplateName = 'PCP/PCD Questions';
    @track tabId;
    @track labels = {
        pcpserviceerror,
        required_msg_HUM,
        pcpPlanTypes,
        currentPCPName
    }


    @track availableActions = [];
    @track pcpFooterButton = 'Save & Next';
    @track isDataAvailable = false;
    @track otherReason = '';
    @track bShowErrorMsg = false;
    @track tempSubmissionOwnerId = '';
    @track tempSubmissionId = '';
    @track tempSubmissionData = [];
    @track allvalid = false;
    @track servicefundedit = false;
    @track allvalid = false;
    @track showProviderSearch;
    @track PcpNumber = '';
    @track physicianName = '';
    @track contactInformation = '';
    @track statePageRef;
    @track flowname;
    @track sellingLedgerNumber;
    @track providernetworkId;
    @track memberplandata;
    @track mbeNetworkId;
    @track providerIds;
    @track majorlob;
    @track isPCPSerachYes = false;
    @track isValidatePCP = false;
    @track isPCPUpdated = false;
    @track disableUpdateCI = true;
    @track serviceFundData = [];




    handleDisableSubmitButton(event) {
        this.updateMasterData(event.target.dataset.question, event.target.value);
    }

    updateSelectedPhysicianInfo(event) {
        if (event && event?.detail && event?.detail?.data) {
            this.selectedPhysicianData = event?.detail?.data;
            this.updateMasterData(this.pcpConstants?.LocationAddressNewPhysician, this.selectedPhysicianData?.physicianAddress ?? '')
            this.updateMasterData(this.pcpConstants?.NameAndInfoNewProvider, this.selectedPhysicianData?.physicianName ?? '');
            if (this.selectedPhysicianData?.pcpNumber && this.selectedPhysicianData?.physicianName && (this.selectedPhysicianData?.physicianAddress || this.selectedPhysicianData?.phone)) {
                this.disableUpdateCI = false;
            }
            else { this.disableUpdateCI = true; }
        }
    }

    checkProviderSearchVisibility() {
        getProviderSearchVisibilty()
            .then(result => {
                this.showProviderSearch = result;
            })
            .catch(error => {
                console.log(error);
            })

    }

    @api deleteUnsavedData() {
        deleteUnsavedData(this.tempSubmissionOwnerId, this.tempSubmissionId);
    }


    async getCurrentPCPData() {
        let refdate = new Date().toISOString().split('T')[0];
        getPCPDetails(this.memberplandata?.Member?.Enterprise_ID__c ?? this.personid, refdate,
            this.memberplandata?.Plan?.Major_LOB__c ?? '', this.memberplandata?.Plan?.Source_Major_LOB__c ?? '',
            this.memberplandata?.EffectiveFrom ?? '', this.memberplandata?.EffectiveTo ?? '', this.memberplandata?.Product__c ?? '', this.memberplandata?.Policy__r?.Major_LOB_Frm__c ?? '', this.memberplandata?.Policy__r?.Major_LOB__c ?? '')
            .then(pcpdata => {
                if (pcpdata && typeof (pcpdata) === 'object') {
                    this.pcpActiveFutureData = pcpdata.has('pcpActiveFuture') ? pcpdata.get('pcpActiveFuture') : null;
                    this.networkDetails = pcpdata.has('Network') ? pcpdata.get('Network') : null;
                    this.platformpointerlist = pcpdata.has('platformpointerlist') ? pcpdata.get('platformpointerlist') : null;
                }
                this.currentpcpname = this.pcpActiveFutureData && Array.isArray(this.pcpActiveFutureData) && this.pcpActiveFutureData.length > 0
                    ? this.pcpActiveFutureData[0].Name : '';
                this.currentpcpname = this.currentpcpname && this.currentpcpname?.length > 0 ? this.currentpcpname : this.labels.currentPCPName;
                this.caspersonid = this.platformpointerlist?.PlatformProxyKey && Array.isArray(this.platformpointerlist?.PlatformProxyKey)
                    && this.platformpointerlist?.PlatformProxyKey?.length > 0 ? this.platformpointerlist?.PlatformProxyKey?.find(k => k?.Source?.toUpperCase() === 'LV'
                        && k?.KeyType?.toUpperCase() === 'PERSONID')?.KeyValue ?? '' : '';
                this.mtvpersonid = this.platformpointerlist?.PlatformProxyKey && Array.isArray(this.platformpointerlist?.PlatformProxyKey)
                    && this.platformpointerlist?.PlatformProxyKey?.length > 0 ? this.platformpointerlist?.PlatformProxyKey?.find(k => k?.Source?.toUpperCase() === 'EM'
                        && k?.KeyType?.toUpperCase() === 'PERSONID')?.KeyValue ?? '' : '';
                this.getNetworkId();
                this.updateMasterData(this.pcpConstants?.NameOfCurrentPhysician, this.currentpcpname);
            }).catch(error => {
                this.getNetworkId();
                console.log(error);
            })
    }


    render() {
        return this.pcpquestionscreen ? pcpquestionstemplate : this.pcpprovidersearchscreen ? pcpprovidersearchscreentemplate : this.pcpsummaryscreen ? pcpSummaryScreenemplate : pcpservicefundquestionstemplate;
    }



    handleReasonChange(e) {
        this.changeReason = e?.detail?.changeReason ?? '';
        this.bShowErrorMsg = false;
        this.updateMasterData(e.detail.question, this.changeReasonOptions.find(k => k.value === e.detail.changeReason).label);
        if (this.pcpprovidersearchscreen) {
            if (this.providerSearchQuestions.find(k => k.Options)?.Answer?.toLocaleLowerCase() === 'yes'
                || this.providerSearchQuestions.find(k => k.Options)?.Answer?.toLocaleLowerCase() === 'no') {
                this.navigateToNextScreen(this.providerSearchQuestions.find(k => k.Options)?.Answer?.toLocaleLowerCase());
            }
        }
    }

    navigateToNextScreen(value) {
        if (value) {
            if (value?.toLocaleLowerCase() === 'yes') {
                this.updateMasterData('Requested effective date?', this.requestEffectiveDate);
                this.displaylogging = false;
                this.displayFinishButton = true;
                this.pcpquestionscreen = false;
                this.pcpprovidersearchscreen = false;
                this.pcpsummaryscreen = false;
                this.pcpservicefundscreen = true;
                this.displayprevious = true;
                this.servicefundedit = true;
                this.isPCPSerachYes = true;
            } else {
                this.displaylogging = this.recordId && this.objectApiName
                    && this.objectApiName?.toLocaleLowerCase() === 'case' ? false :
                    this.recordId && this.objectApiName && this.objectApiName?.toLocaleLowerCase() === 'memberplan' ? true : false;
                this.displayFinishButton = this.recordId && this.objectApiName
                    && this.objectApiName?.toLocaleLowerCase() === 'case' ? true : false;
                this.pcpprovidersearchscreen = false;
                this.pcpquestionscreen = false;
                this.pcpservicefundscreen = false;
                this.servicefundedit = false;
                this.pcpsummaryscreen = true;
                this.disableFinishButton = false;
                this.isPCPSerachYes = false;
            }
        }
    }

    clearSearchData() {
        this.keyword = '';
        this.filterdata();
    }


    showErrorMessage(message) {
        this.dispatchEvent(new ShowToastEvent({
            title: '',
            message: message,
            variant: 'error',
            mode: 'pester'
        }));
    }


    handleAnswerChange(e) {
        if (e.target.label == 'Did you receive a PCP Assignment Research message in CI that prevented you from changing the PCP?') { this.isPCPSerachYes = e?.target?.value === 'Yes' ? true : false; }
        updateAnswer.call(this, e);
    }
    handleUpdateCI() {
        validatePCPEligibilty.call(this)
            .then(result => {
                if (result && result === true) {
                    console.log(result);
                    updateMemberPCP.call(this).then(result => {
                        console.log('PCP Updated successfully.');
                    }).catch(error => {
                        console.log('Update PCP failed' + error);
                    })
                }
            }).catch(error => {
                console.log(error);
            })
    }


    isInputValid() {
        let isValid = false;
        if (this.template.querySelector('c-pcp-update-current-pcp-hum') != null) {
            isValid = this.template.querySelector('c-pcp-update-current-pcp-hum').checkvalidity();
        }
        return isValid;
    }

    isProviderSearchValid() {
        let isValid = true;
        this.bShowErrorMsg = false;
        let inputFields = this.template.querySelectorAll('.validate');
        inputFields.forEach(inputField => {
            if (!inputField.checkValidity()) {
                inputField.reportValidity();
                isValid = false;
            }
        });
        if (this.changeReason && this.changeReason.toLocaleLowerCase() === 'none') {
            isValid = false;
            this.bShowErrorMsg = true;
        }
        return isValid;
    }

    isServiceFundValid() {
        let isValid = true;
        let inputFields = this.template.querySelectorAll('.sfvalidate');
        inputFields.forEach(inputField => {
            if (!inputField.checkValidity()) {
                inputField.reportValidity();
                isValid = false;
            }
        });
        return isValid;
    }

    handleOtherReasonDetails(event) {
        if (event) {
            this.otherReason = event?.detail?.otherReason ?? '';
            this.updateMasterData(event.detail.question, this.otherReason);
        }
    }

    checkButtons() {
        if (this.recordId && this.objectApiName) {
            switch (this.objectApiName?.toLocaleLowerCase()) {
                case "case":
                    this.displaylogging = false;
                    this.displayFinishButton = true;
                    this.disableFinishButton = false;
                    break;
                case "memberplan":
                    this.displaylogging = true;
                    this.displayFinishButton = false;
            }
        }
    }

    handleFinish(event) {
        if (event?.target?.name.toLocaleLowerCase() === 'saveandnext') {
            if (this.isInputValid()) {
                if (this.template.querySelector('c-pcp-update-current-pcp-hum') != null) {
                    if (this.template.querySelector('c-pcp-update-current-pcp-hum').checkvalidity()) {
                        this.pcpquestionscreen = false;
                        this.pcpprovidersearchscreen = true;
                    }
                }
            }
        } else if (event?.target?.name.toLocaleLowerCase() === 'finish') {
            if (this.pcpprovidersearchscreen) {
                if (this.isProviderSearchValid()) {
                    this.updateTemplateData();
                }
            } else if (this.pcpsummaryscreen) {
                this.updateTemplateData();
                this.attachInteraction(this.caseRecordId);
            }
        }
        else if (event?.target?.name.toLocaleLowerCase() === 'submit') {
            if (this.isServiceFundValid()) {
                this.isPCPSerachYes = true;
                this.checkButtons();
                this.pcpsummaryscreen = true;
            }
        }
    }

    updateTemplateData() {
        this.loaded = false;
        if (this.tempSubmissionData && Array.isArray(this.tempSubmissionData) && this.tempSubmissionData.length > 0) {
            this.tempSubmissionData.forEach(k => {
                if (k && k.fields?.Template_Field__r?.displayValue) {
                    let index = this.templateMasterData.findIndex(h => h?.TemplateField === k?.fields?.Template_Field__r?.displayValue);
                    if (index > -1) {
                        this.templateMasterData[index].TemplateSubmissionId = k.id;
                    }
                }
            })
        }
        if (this.templateMasterData && Array.isArray(this.templateMasterData) && this.templateMasterData.length > 0) {
            let counter = 0;

            Promise.all(
                this.templateMasterData.map(k => {
                    if (k && k?.TemplateSubmissionId && k?.Value) {
                        return updateTemplateSubmissionData(k?.TemplateSubmissionId, k?.Value);
                    }
                })
            ).then(result => {
                this.loaded = true;
                this.redirectToCasePages();
            }).catch(error => {
                console.error(error);
                this.loaded = true;
                this.redirectToCasePages();
            })
        }
    }



    closesubtab() {
        try {
            if (this.caseRecordId) {
                invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
                    if (isConsole) {
                        invokeWorkspaceAPI('disableTabClose', {
                            tabId: this.tabId,
                            disabled: false
                        });
                        invokeWorkspaceAPI('closeTab', {
                            tabId: this.tabId
                        });

                    }
                });
                setTimeout(() => { pubSubHum.fireEvent(this.statePageRef, 'refreshCaseProcessSec', { 'flowName': this.flowname, 'isFinished': true, 'servicefundedit': this.servicefundedit }); }, 2000);
            }

        }

        catch (error) {
            console.log('Error==', error);
        }
    }

    updateMasterData(question, value) {
        if (this.templateMasterData && Array.isArray(this.templateMasterData)
            && this.templateMasterData.length > 0) {
            this.templateMasterData.forEach(k => {
                if (k && k?.Question && question &&
                    k.Question.toLocaleLowerCase().includes(question.toLocaleLowerCase())) {
                    k.Value = value
                }
            })
        }
    }

    formatDate(date) {
        let d = new Date(date),
            month = '' + (d.getMonth() + 1),
            day = '' + d.getDate(),
            year = d.getFullYear();

        if (month.length < 2)
            month = '0' + month;
        if (day.length < 2)
            day = '0' + day;

        return [year, month, day].join('-');
    }

    renderedCallback() {
        this.getTabId();
        if (this.template.querySelector('c-pcp-provider-search-hum') != null) {
            this.template.querySelector('c-pcp-provider-search-hum').updateNetworkId(this.providernetworkId, this.providerIds);
        }
    }

    getModelElement(element) {
        const me = this;
        let elements = { ...element, Id: me.getUniqueId(element?.id) };
        return JSON.parse(JSON.stringify(element).replaceAll('__c', ''));
    }

    connectedCallback() {
        this.statePageRef = this.pageRef.state.c__pageRef;
        this.flowname = this.pageRef.state.c__flowName;
        this.requestEffectiveDate = getFormatDate(new Date(), 'yyyy-mm-dd');
        this.effectiveDate = getFormatDate(new Date(), 'yyyy-mm-dd');
        let modelData = getModel('questions').map(obj => ({ ...obj, Id: this.getUniqueId() }));
        this.mbeNetworkId = this.pageRef?.state?.c__networkId ?? null;
        this.templateMasterData = modelData.map(obj => {
            let elements = {
                ...obj, Options: obj?.Options__c &&
                    obj?.Options__c.length > 0 ? this.getOptions(obj.Options__c) : null,
                TemplateField: obj?.Template_Field_Name__c ?? ''
            };
            return JSON.parse(JSON.stringify(elements).replaceAll('__c', ''));
        });

        this.pcpupdatequestion = modelData.filter(obj => obj?.Screen__c === 'PCPQuestion' && obj?.Options__c &&
            obj?.Options__c.length > 0)?.map(obj => {
                let elements = {
                    ...obj, Options: obj?.Options__c &&
                        obj?.Options__c.length > 0 ? this.getOptions(obj.Options__c) : null
                };
                return JSON.parse(JSON.stringify(elements).replaceAll('__c', ''));
            });

        this.providerSearchQuestions = modelData.filter(obj => obj?.Screen__c === 'PCPProviderSearch' && obj?.Options__c &&
            obj?.Options__c.length > 0)?.map(obj => {
                let elements = {
                    ...obj, Options: obj?.Options__c &&
                        obj?.Options__c.length > 0 ? this.getOptions(obj.Options__c) : null
                };
                return JSON.parse(JSON.stringify(elements).replaceAll('__c', ''));
            });
        this.serviceFundQuestions = modelData.filter(obj => obj?.Screen__c === 'PCPServiceFund' && obj?.Options__c &&
            obj?.Options__c.length > 0)?.map(obj => {
                let elements = {
                    ...obj, Options: obj?.Options__c &&
                        obj?.Options__c.length > 0 ? this.getOptions(obj.Options__c) : null
                };
                return JSON.parse(JSON.stringify(elements).replaceAll('__c', ''));
            });


        this.pcpupdatequestion.sort((a, b) => {
            return a.Order > b.Order ? 1 : -1;
        })

        if (this.flowdata) {
            let data = JSON.parse(this.flowdata);
            if (data && Array.isArray(data) && data.length > 0) {
                data.forEach(k => {
                    if (k && k?.name && (k.name.toLocaleLowerCase() === 'caseid'
                        || k.name.toLocaleLowerCase() === 'memberplanid')) {
                        this.recordId = k?.value ?? '';
                    } else if (k && k?.name && k.name.toLocaleLowerCase() === 'whattype') {
                        this.objectApiName = k?.value ?? '';
                    } else if (k && k?.name && k.name.toLocaleLowerCase() === 'currentpcpname') {
                        this.currentpcpname = k?.value ?? '';
                    }
                })
            }
        }


        this.physicianSearchTableModel = getModel("providersearchtable");

        if (this.recordId && this.objectApiName) {
            if (this.pagename === 'New Case') {
                this.objectApiName = 'Case';
                this.caseRecordId = this.recordId;
                this.memplanId = this.memid;
                this.getMemberPlanData();
            } else {
                switch (this.objectApiName.toLocaleLowerCase()) {
                    case "case":
                        this.caseRecordId = this.recordId;
                        this.getCaseDetails();
                        break;
                    case "memberplan":
                        this.memberPlanId = this.recordId;
                        this.getMemberPlanData();
                        break;
                }
            }
        }


        this.updateMasterData(this.pcpConstants?.EffectiveDate, this.effectiveDate);
        if (this.currentpcpname && this.currentpcpname?.length > 0) {
            this.updateMasterData(this.pcpConstants?.NameOfCurrentPhysician, this.currentpcpname);
        }
        this.checkProviderSearchVisibility();
        this.getTemplateData().then(result => {
            console.log('template data created' + result);
            this.tempdatacreated = true;
        }).catch(error => {
            console.log(error);
        });
    }



    getTemplateData() {
        return new Promise((resolve, reject) => {
            generateTemplateData(this.pcpTemplateName, this.getCaseRecordId())
                .then(result => {
                    if (result && typeof (result) === 'object') {
                        this.pcpTemplateDetails = result?.has('template') ? result.get('template')[0] : null;
                        this.templateFields = result?.has('templateFields') ? result.get('templateFields') : null;
                        this.tempSubmissionOwnerId = result?.has('templateSubmissionOwner') ? result.get('templateSubmissionOwner') : null;
                        this.tempSubmissionId = result?.has('templateSubmission') ? result.get('templateSubmission') : null;
                        this.tempSubmissionData = result?.has('templateSubmissionData') ? result.get('templateSubmissionData') : null;
                        resolve(true);
                    }
                }).catch(error => {
                    console.log(error);
                    reject(false);
                })
        })

    }

    getCaseRecordId() {
        if (this.objectApiName) {
            switch (this.objectApiName?.toLocaleLowerCase()) {
                case "case":
                    return this.recordId;
                case "memberplan":
                    let uniqueId = `50000${Math.random().toString(16).slice(2)}`;
                    uniqueId = uniqueId && uniqueId?.length >= 18 ? uniqueId.substring(0, 18) : uniqueId.padEnd(18, '0');
                    return uniqueId;
            }
        } else {
            let uniqueId = `50000${Math.random().toString(16).slice(2)}`;
            uniqueId = uniqueId && uniqueId?.length >= 18 ? uniqueId.substring(0, 18) : uniqueId.padEnd(18, '0');
            return uniqueId;
        }
    }

    checkforplantype() {
        if (!this.labels.pcpPlanTypes.includes(this.majorlob)) {
            this.pcpquestionscreen = false;
            this.pcpprovidersearchscreen = true;
            this.displayPCPQuestionScreen = false;
        }
    }

    getMemberPlanData() {
        getMemberPlanDetails(this.recordId).then(result => {
            if (result && Array.isArray(result) && result.length > 0) {
                result.forEach(k => {
                    this.memberplandata = k;
                    this.memberdob = k?.Member?.Birthdate__c ?? '';
                    this.accountId = k?.Member?.Id ?? '';
                    this.inputZipCode = k?.Member?.PersonMailingPostalCode ?? '';
                    this.majorlob = k?.Plan?.Major_LOB__c ?? '';
                    this.checkforplantype();
                    if (!this.currentpcpname && !this.currentpcpname?.length > 0) {
                        this.getCurrentPCPData();
                    } else {
                        this.getNetworkId();
                    }
                })
            }
        }).catch(error => {
            console.log(error);
        })
    }

    getNetworkId() {
        getNetworkData.call(this).then(result => {
            if (result) {
                if (this.template.querySelector('c-pcp-provider-search-hum') != null) {
                    this.template.querySelector('c-pcp-provider-search-hum').updateNetworkId(this.providernetworkId, this.providerIds);
                }
            }
        }).catch(error => {
            console.log(error);
        })
    }



    getCaseDetails() {
        getCaseData(this.caseRecordId).then(result => {
            if (result && Array.isArray(result) && result.length > 0) {
                result.forEach(k => {
                    this.memberplandata = k?.Member_Plan_Id__r ?? null;
                    this.memberdob = k?.Account?.Birthdate__c ?? '';
                    this.personid = k?.Account?.Enterprise_ID__c ?? '';
                    this.accountId = k?.Account?.Id ?? '';
                    this.inputZipCode = k?.Account?.PersonMailingPostalCode ?? '';
                    this.majorlob = k?.Member_Plan_Id__r?.Plan?.Major_LOB__c ?? '';
                    this.checkforplantype();
                    if (!this.currentpcpname && !this.currentpcpname?.length > 0) {
                        this.getCurrentPCPData();
                    } else {
                        this.getNetworkId();
                    }
                })
            }
        })
    }



    handleEffectiveDateChange(event) {
        this.effectiveDate = event?.detail?.effectiveDate;
        this.updateMasterData(event.detail.question, this.effectiveDate);
    }

    handleRequestEffectiveDateChange(event) {
        this.requestEffectiveDate = event.target.value;
        this.updateMasterData(event.target.dataset.question, event.target.value);
    }



    getUniqueId() {
        return Math.random().toString(16).slice(2);
    }

    fireProcessSectionRefreshEvent() {
        setTimeout(() => {
            this.closesubtab();
        }, 3000)
    }
    handlePrevious(event) {
        if (!this.isPCPSerachYes || event?.target?.dataset?.name == 'PCP Fund') {
            this.isPCPSerachYes = false;
            this.pcpservicefundscreen = false;
            this.pcpprovidersearchscreen = true;
            this.displayFinishButton = false;
            this.displaylogging = false;
            this.displayprevious = false;
            this.displayExistingCasePopover = false;
            if (this.providerSearchQuestions && this.providerSearchQuestions.length > 0) {
                this.providerSearchQuestions.forEach(k => {
                    k.Answer = '';
                    this.updateMasterData(k.Question, '');
                })
            }
            this.updateMasterData(this.pcpConstants?.RequestEffectiveDate, '');
        }
        else if (this.isPCPSerachYes) {
            this.displaylogging = false;
            this.displayFinishButton = true;
            this.pcpquestionscreen = false;
            this.pcpprovidersearchscreen = false;
            this.pcpsummaryscreen = false;
            this.pcpservicefundscreen = true;
            this.displayprevious = true;
            this.servicefundedit = true;
            this.isPCPSerachYes = true;
            this.getFundData();
        }
    }
    getFundData() {
        this.serviceFundData.forEach(k => {
            switch (k.Question) {
                case "PCP number or Center number":
                    this.PCPFundData.pcpNumber = k.Value;
                    break;
                case "Group name, if applicable":
                    this.PCPFundData.groupName = k.Value;
                    break;
                case "CAS ID from PAAG, application":
                    this.PCPFundData.caseId = k.Value;
                    break;
                case "Is the member an established patient with the new PCP":
                    this.PCPFundData.option = k.Value;
                    break;
                case "Name of the individual doctor?":
                    this.PCPFundData.doctor = k.Value;
                    break;
                case "Frozen panel only: name of the office personel who confirmed established patient/appointment scheduled, as applicable":
                    this.PCPFundData.text5 = k.Value;
                    break;
                case "Who you spoke with that advised the patient is established. Included any additional details provided.":
                    this.PCPFundData.text6 = k.Value;
                    break;
                case "Requested effective date?":
                    this.PCPFundData.effDate = k.Value;
                    break;
                case "The error received in CI":
                    this.PCPFundData.error = k.Value;
                    break;
                case "Request details":
                    this.PCPFundData.requestDetail = k.Value;
                    break;
                case "Callback number, if different than the number in the system":
                    this.PCPFundData.callBackNo = k.Value;
                    break;
            }
        })
    }
    showToast(strTitle, strMessage, strStyle) {
        this.dispatchEvent(new ShowToastEvent({
            title: strTitle,
            message: strMessage,
            variant: strStyle
        }));
    }


    handlenewEffectiveDate(event) {
        this.effectiveDate = event.detail.effectiveDate;
    }

    redirectToCaseEditPage() {
        if (this.caseRecordId) {
            let casedata = {};
            casedata.Id = this.caseRecordId;
            casedata.objApiName = 'Case';
            casedata.templateName = 'PCP Update';
            casedata.serviceFundEdit = this.servicefundedit;
            openLWCSubtab('caseInformationComponentHum', casedata, { label: 'Edit Case', icon: 'standard:case' });
        }
    }

    getOptions(data) {
        let tmp = []
        if (data) {
            let options = data.split(';');
            if (options && Array.isArray(options) && options.length > 0) {
                options.forEach(k => {
                    if (k) {
                        tmp.push({
                            label: k.split(':')[0],
                            value: k.split(':')[1]
                        })
                    }

                })
            }
        }

        return tmp;
    }

    handleToggle(event) {
        this.allvalid = false;
        if (this.pcpsummaryscreen && !event.target.checked) {
            this.newcase = false;
            this.existingcase = true;
            this.displayExistingCasePopover = true;
        }
        event.target.checked = true;
    }

    handleProviderSelect(event) {
        if (event && event?.detail) {
            this.selectedPhysicianData = event.detail.checked ? event.detail.providerdetails : null;
            if (this.template.querySelector('c-pcp-selected-physician-hum') != null) {
                this.template.querySelector('c-pcp-selected-physician-hum').updateData(this.selectedPhysicianData);
            }
            this.updateMasterData(this.pcpConstants?.LocationAddressNewPhysician, this.selectedPhysicianData?.physicianAddress ?? '')
            this.updateMasterData(this.pcpConstants?.NameAndInfoNewProvider, this.selectedPhysicianData?.physicianName ?? '');
            if (this.selectedPhysicianData?.pcpNumber && this.selectedPhysicianData?.physicianName && this.selectedPhysicianData?.physicianAddress) {
                this.disableUpdateCI = false;
            }
            else { this.disableUpdateCI = true; }
        }
    }


    async handleAttachExisting(event) {
        this.displayExistingCasePopover = false;
        this.loaded = false;
        this.caseRecordId = event.detail.caseId;
        this.existingcase = true;
        this.newcase = false;
        if (this.tempdatacreated) {
            this.updateTemplateData();
            this.attachInteraction(this.caseRecordId);
        } else {
            this.getTemplateData().then(result => {
                if (result) {
                    this.updateTemplateData();
                    this.attachInteraction(this.caseRecordId);
                }
            }).catch(error => {
                if (!error) {
                    console.log('get template data failed');
                }
            })
        }

    }

    attachInteraction(logCaseId) {
        let attachInteractionResponse;
        attachInteractionToCase(logCaseId).then(result => {
            if (result) {
                attachInteractionResponse = result;
            }
        }).catch(error => {
            return error;
        });
    }



    handleLogFinish() {
        if (this.pcpprovidersearchscreen) {
            if (this.isProviderSearchValid()) {
                this.createCase();
            }
        } else if (this.pcpservicefundscreen) {
            if (this.isServiceFundValid()) {
                this.createCase();
            }
        } else if (this.pcpsummaryscreen) {
            this.createCase();
        }
    }



    handleCloseExistingCasePopover() {
        this.displayExistingCasePopover = false;
        this.newcase = true;
        this.existingcase = false;
    }


    createCase() {
        this.loaded = false;
        let interactionId = getInteractionId();
        if (this.newcase) {
            createNewCaseId(this.memberPlanId ?? this.accountId, 'Logging', interactionId)
                .then(result => {
                    this.caseRecordId = result.toString().split('-')[0];
                    if (this.tempdatacreated) {
                        this.updateTemplateData();
                        this.attachInteraction(this.caseRecordId);
                    } else {
                        this.getTemplateData().then(result => {
                            if (result) {
                                this.updateTemplateData();
                                this.attachInteraction(this.caseRecordId);
                            }
                        }).catch(error => {
                            if (!error) {
                                console.log('get template data failed');
                                this.loaded = true;
                                this.showErrorMessage('Error occurred while updating template data.')
                            }
                        })
                    }
                }).catch(error => {
                    console.log(error);
                    this.loaded = true;
                    this.showErrorMessage('Error occurred while creating new case.')
                })
        } else {
            this.displayExistingCasePopover = true;
        }
    }

    getTabId() {
        invokeWorkspaceAPI('isConsoleNavigation').then(isConsole => {
            if (isConsole) {
                invokeWorkspaceAPI('getFocusedTabInfo').then(focusedTab => {
                    this.tabId = focusedTab.tabId;
                });
            }
        });
    }

    async redirectToCaseDetailPage() {
        let pageref = {
            type: 'standard__recordPage',
            attributes: {
                recordId: this.caseRecordId,
                objectApiName: 'Case',
                actionName: 'view'
            },
        }
        let focusedTab = await invokeWorkspaceAPI('getFocusedTabInfo');
        if (await invokeWorkspaceAPI('isConsoleNavigation')) {
            await invokeWorkspaceAPI('openSubtab', {
                parentTabId: (focusedTab.hasOwnProperty('isSubtab') && focusedTab.isSubtab) ? focusedTab.parentTabId : focusedTab.tabId,
                pageReference: pageref
            });
        }
    }

    redirectToCasePages() {
        redirectToCasePages.call(this);
    }

    get PCPQuestionsData() {
        let data = this.getData('PCPQuestion')?.filter(k => k && k?.Display === true);
        data.sort((a, b) => {
            return a.Order > b.Order ? 1 : -1;
        })
        return data;
    }

    get PCPProviderSearchData() {
        let providerdata = this.getData('PCPProviderSearch')
        let data = providerdata?.filter(k => k && k?.Display === true);
        data.push({
            label: this.pcpConstants?.NameAddressNewPhysician,
            Value: `${providerdata.find(k => k && k?.JoinField && k?.JoinField?.toLocaleLowerCase() === 'name')?.Value ?? ''} ,
      ${providerdata.find(k => k && k?.JoinField && k?.JoinField?.toLocaleLowerCase() === 'address')?.Value ?? ''}`,
            Order: '4'
        });
        data.sort((a, b) => {
            return a.Order > b.Order ? 1 : -1;
        });
        let sfquestion = this.templateMasterData.find(k => k?.TemplateField?.toLocaleLowerCase() === SF_QUESTION?.toLocaleLowerCase());
        if (sfquestion && sfquestion?.Value && sfquestion?.Value.toLocaleLowerCase() === 'yes') {
            this.displayServiceFundSection = true;
        }
        else {
            this.displayServiceFundSection = false;
        }
        return data;
    }


    get PCPServiceFundData() {
        let data = this.getData('PCPServiceFund')?.filter(k => k && k?.Display === true);
        data.sort((a, b) => {
            return a.Order > b.Order ? 1 : -1;
        });
        this.serviceFundData = data;
        return data;
    }

    getData(screenname) {
        if (this.templateMasterData && Array.isArray(this.templateMasterData) && this.templateMasterData.length > 0) {
            return this.templateMasterData.filter(k => k?.Screen.toLocaleLowerCase() === screenname.toLocaleLowerCase());
        } else {
            return null;
        }
    }
}