/*
JS Controller        : PrimaryCarePhysicianHum
Version              : 1.0
Created On           : 04/05/2022
Function             : Component to display PCP/PCD info.

Modification Log:
* Developer Name                         Date                         Description
* Jonathan Dickinson                   04/04/2022                   US-3196414
* Swapnali Sonawane                    08/23/2022                   US#3631288 Use purchaser plan object
* Kalyani Pachpol                      03/17/2022                   DF-7399
* Nirmal Garg                          04/14/2023                   US4460894
* Aishwarya Pawar                     05/19/2023                   US - 4516245
* Atul Patil                           05/26/2023                    DF-7684
*------------------------------------------------------------------------------------------------------------------------------
*/
import { LightningElement, wire, track } from 'lwc';
import { invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import getProviderPhoneNumber from "@salesforce/apexContinuation/PrimaryCare_LC_HUM.getProviderPhoneNumber";
import { CurrentPageReference } from 'lightning/navigation';
import { getRecord } from 'lightning/uiRecordApi';
import { ShowToastEvent } from 'lightning/platformShowToastEvent';
import pcpserviceerror from '@salesforce/label/c.BenefitsServiceError';
import currentUserId from '@salesforce/user/Id';
import { recordIdNames, submissionIdNames, templateNames } from './input';
import { getUserGroup, hcConstants } from "c/crmUtilityHum";
import PROFILE_NAME_FIELD from '@salesforce/schema/User.Profile.Name';
import { getPCPDetails } from 'c/pcpDetailsHum';
import { getMemberPlanDetails } from 'c/genericMemberPlanDetails';
export default class PrimaryCarePhysicianHum extends LightningElement {

    recId;
    personId;
    pcpServiceResponse;
    currentPageReference;
    @track flowParams;
    flowOutput;
    memberPlan;
    memberId;
    effectiveTo;
    effectiveFrom;
    currentPCPName;
    @track
    pcpActiveFutureData = [];
    @track
    pcpPreviousData = [];
    isDataProcessed = false;
    hasActivePCP = false;
    hasPreviousPCP = false;
    retrievedAllPhoneNumbers = false;
    @track pageHeaderName = '';
    @track soldproductdetails;
    @track majorlob;
    @track majorlobfrom;
    @track serviceerror = false;
    showloggingicon = true;
    autoLogging = true;
    pageType = '';
    pageTypeDetail = '';
    @track objectApiName = 'MemberPlan';
    @track profilename = '';
    @track displayUpdateButton = false;
    @track userGroup = getUserGroup();
    @track ProfileList = [hcConstants.Customer_Care_Specialist, hcConstants.CUSTOMER_CARE_SUPERVISOR, hcConstants.System_Admin];
    pageRef;
    @track networkDetails;
    @track isDental;
    @track platformpointerlist;


    labels = {
        pcpserviceerror
    }

    async connectedCallback() {

    }

    @wire(getRecord, {
        recordId: currentUserId,
        fields: [PROFILE_NAME_FIELD]
    }) wireuser({
        error,
        data
    }) {
        if (error) {
            this.error = error;
        } else if (data) {
            this.profilename = data.fields.Profile.value.fields.Name.value;
            if (this.profilename && this.ProfileList.includes(this.profilename)) {
                this.displayUpdateButton = true;
            }

        }
    }

    @wire(CurrentPageReference)
    wiredPageRef(pageRef) {
        this.pageRef = pageRef;
    }

    get activePCPMessage() {
        return `This member has no active ${this.pageHeaderName}.`;
    }

    get previousPCPMessage() {
        return `This member has no ${this.pageHeaderName} history.`;
    }


    getMemberPlanData() {
        getMemberPlanDetails(this.recId).then(result => {
            if (result && Array.isArray(result) && result.length > 0) {
                result.forEach(k => {
                    this.memberPlan = k;
                    this.memberId = this.memberPlan?.Name ?? '';
                    this.pageHeaderName = this.pageType = this.memberPlan?.Product__c == 'MED' ? 'PCP' : this.memberPlan?.Product__c == 'DEN' ? 'PCD' : '';
                    this.pageTypeDetail = this.pageHeaderName == 'PCP' ? 'PCP Details' : 'PCD Details';
                    this.isDental = this.pageTypeDetail == 'PCP Details' ? false : true;
                    this.getPCPData().then(result => {
                        this.isDataProcessed = result;
                        this.retrievePhoneNumbers();
                    }).catch(error => {
                        this.isDataProcessed = true;
                        this.serviceerror = false;
                        this.showErrorMessage(this.labels.pcpserviceerror);
                        console.log(error);
                    })
                })
            }
        }).catch(error => {
            console.log(error);
        })
    }

    getPCPData() {
        this.pcpActiveFutureData = [];
        this.pcpPreviousData = [];
        return new Promise((resolve, reject) => {
            getPCPDetails(this.memberPlan?.Member?.Enterprise_ID__c ?? '', this.memberPlan?.EffectiveTo ?? '',
                this.memberPlan?.Plan?.Major_LOB__c ?? '', this.memberPlan?.Plan?.Source_Major_LOB__c ?? '',
                this.memberPlan?.EffectiveFrom ?? '', this.memberPlan?.EffectiveTo ?? '', this.memberPlan?.Product__c ?? '', this.memberPlan?.Policy__r?.Major_LOB_Frm__c ?? '', this.memberPlan?.Policy__r?.Major_LOB__c ?? '')
                .then(pcpdata => {
                    if (pcpdata && typeof (pcpdata) === 'object') {
                        this.pcpActiveFutureData = pcpdata.has('pcpActiveFuture') ? pcpdata.get('pcpActiveFuture') : null;
                        this.pcpPreviousData = pcpdata.has('pcpHistory') ? pcpdata.get('pcpHistory') : null;
                        this.networkDetails = pcpdata.has('Network') ? pcpdata.get('Network') : null;
                        this.platformpointerlist = pcpdata.has('platformpointerlist') ? pcpdata.get('platformpointerlist') : null;
                    }
                    this.getActivePCPPCD();
                    this.getPCPPCDHistory();
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    this.pcpActiveFutureData = null;
                    this.pcpPreviousData = null;
                    reject(false);
                })
        })
    }

    showErrorMessage(message) {
        this.dispatchEvent(new ShowToastEvent({
            title: '',
            message: message,
            variant: 'error',
            mode: 'pester'
        }));
    }

    getPhoneNumber(providerNum) {
        return new Promise((resolve, reject) => {
            getProviderPhoneNumber({ providerNumber: providerNum })
                .then(result => {
                    let phoneNumber = this.formatPhoneNumber(JSON.parse(result));
                    resolve(phoneNumber);
                })
                .catch(error => {
                    this.showErrorMessage('An error occured with retrieving the phone number.');
                    reject('An error occured with retrieving the phone number: ' + JSON.stringify(error));
                })
        })
    }

    async retrievePhoneNumbers() {
        if (Array.isArray(this.pcpActiveFutureData) && this.pcpActiveFutureData.length > 0) {
            this.pcpActiveFutureData.forEach(k => {
                if (k && k.hasOwnProperty('Npi') && k?.Npi) {
                    this.getPhoneNumber(k.Npi).then(result => {
                        k.phoneNumber = result;
                    }).catch(error => {
                        console.log(error);
                    })
                }
            })
        }
        if (Array.isArray(this.pcpPreviousData) && this.pcpPreviousData.length > 0) {
            this.pcpPreviousData.forEach(k => {
                if (k && k.hasOwnProperty('Npi') && k?.Npi) {
                    this.getPhoneNumber(k.Npi).then(result => {
                        k.phoneNumber = result;
                    }).catch(error => {
                        console.log(error);
                    })
                }
            })
        }
        this.retrievedAllPhoneNumbers = true;
    }

    formatPhoneNumber(num) {
        // match 4 chars if preceeded by exactly 6 chars, match 3 chars otherwise
        return num.match(/((?<=[\S]{6})[\S]{4})|[\S]{3}/g).join('-');
    }

    @wire(CurrentPageReference)
    getPageState(currentPageReference) {
        this.currentPageReference = currentPageReference;
        if (this.currentPageReference) {
            this.recId = currentPageReference.attributes.attributes.Id;
            this.getMemberPlanData();
        }
    }


    getActivePCPPCD() {
        if (this.pcpActiveFutureData?.length > 0) {
            this.pcpActiveFutureData.sort(this.compareDates);
            this.hasActivePCP = true;
        }
    }

    getPCPPCDHistory() {
        if (this.pcpPreviousData?.length > 0) {
            this.pcpPreviousData.sort(this.compareDates);
            this.hasPreviousPCP = true;
        }
    }


    getFlowParameters(praramsString, objectName, templateSubmittionId) {
        let tempflowParams = [];
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
                }
                tempflowParams.push(objParam);
            });
        }
        tempflowParams.push({
            type: 'String',
            name: 'MemberPlanId',
            value: this.recId
        })
        tempflowParams.push({
            type: 'String',
            name: 'CurrentPCPName',
            value: this.pcpActiveFutureData && Array.isArray(this.pcpActiveFutureData) && this.pcpActiveFutureData.length > 0
                ? this.pcpActiveFutureData[0].Name : ''
        })
        let paramsJSON = (tempflowParams) ? JSON.stringify(tempflowParams) : '';
        return paramsJSON;
    }
    navigateToFlow() {
        let flowdata = [];
        let tempObj = {};
        tempObj.templateName = 'PCP Update/Change';
        tempObj.templateLabel = 'PCP Update/Change';
        tempObj.hasAccess = false;
        tempObj.showInAction = true;
        tempObj.params = 'CaseId,WhatType,UserId,tempSubmissionOwnerId';
        tempObj.TemplateType = 'LWC';
        tempObj.description = 'This template is used to capture pcp pcd related information.';
        tempObj.flowParamsJSON = this.getFlowParameters(tempObj.params, this.objectApiName, '');
        tempObj.CASPersonId = this.platformpointerlist?.PlatformProxyKey && Array.isArray(this.platformpointerlist?.PlatformProxyKey)
            && this.platformpointerlist?.PlatformProxyKey?.length > 0 ? this.platformpointerlist?.PlatformProxyKey?.find(k => k?.Source?.toUpperCase() === 'LV'
                && k?.KeyType?.toUpperCase() === 'PERSONID')?.KeyValue ?? '' : '';
        tempObj.MTVPersonId = this.platformpointerlist?.PlatformProxyKey && Array.isArray(this.platformpointerlist?.PlatformProxyKey)
            && this.platformpointerlist?.PlatformProxyKey?.length > 0 ? this.platformpointerlist?.PlatformProxyKey?.find(k => k?.Source?.toUpperCase() === 'EM'
                && k?.KeyType?.toUpperCase() === 'PERSONID')?.KeyValue ?? '' : '';
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
                                c__caseNo: '',
                                c__flowName: tempObj.templateLabel,
                                c__flowData: JSON.stringify(tempObj),
                                c__tabCloseSwitch: false,
                                c__pageName: 'PCP',
                                c__pageRef: this.pageRef,
                                c__networkId: this.networkDetails?.NetworkDirectoryList?.NetworkDirectory?.find(k => k?.Primary === 'true')?.DirectoryId ?? null
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