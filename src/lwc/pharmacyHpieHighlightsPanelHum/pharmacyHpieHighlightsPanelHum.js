/*
Function        : LWC to display demographics details .

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                     07/18/2022                user story 4861950, 4861945
* Atul Patil                      07/28/2023                user story 4861950, 4861945
* Swapnali Sonawane               09/01/2023                US: 5012557 Pharmacy - MTM Eligibility
* Jonathan Dickinson			  10/09/2023				DF-8195
* Pinky Vijur                     10/19/2023                DF-8227 Regression - Lightning - Warning message not displayed for demographics not verified  on MOP page
* Pinky Vijur                     03/01/2024                User Story 5428942: T1PRJ0865978- MF21712 Mail Order Management; Pharmacy- Guided Flow- Web Issues- Launch from pharmacy
*****************************************************************************************************************************/
import { LightningElement, track, api, wire } from 'lwc';
import { publish, subscribe, createMessageContext } from 'lightning/messageService';
import humanaPharmacyLMS from '@salesforce/messageChannel/pharmacyLMSChannel__c';
import customcss from '@salesforce/resourceUrl/PharmacyLightning_SR_HUM';
import { loadStyle } from 'lightning/platformResourceLoader';
import { getFormatDate } from 'c/crmUtilityHum';
import getMemberIcons from '@salesforce/apex/MemberIcons_LC_HUM.getMemberIconStatus';
import { openLWCSubtab,invokeWorkspaceAPI } from 'c/workSpaceUtilityComponentHum';
import { CurrentPageReference } from 'lightning/navigation';
import pharmacyDemographicsNoData from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_NODATA';
import pharmacyDemographicsError from '@salesforce/label/c.PHARMACY_DEMOGRAPHIC_FILTER_ERROR';
import { getMtmData } from 'c/mTMIntegrationHum';
import { getRecord } from 'lightning/uiRecordApi';
import { getAllMemberPlans,getMemberPlanDetails } from 'c/genericMemberPlanDetails';
import medicare_Set from '@salesforce/label/c.MEDICARE_SET';
import getTemplateNum from '@salesforce/apex/PharmacyWebIssues_LC_HUM.getWebIssuesTN'

export default class PharmacyHpieHighlightsPanelHum extends LightningElement {
    @track accountId;
    @track enterpriseId;
    @track userId;
    @track userProfile;
    @track memberName;
    @track memberDOB;
    @track accountBalance;
    @track accountLimit;
    @track shippingAddress;
    @track memberEmail;
    @track foreColor;
    @track bShowDemographicsWarning;
    @track memberIcons = [];
    @track accountNumber;
    @track profileLoaded = false;
    @track addressLoaded = false;
    @track financeLoaded = false;
    @track serviceError = false;
    @track noDataError = false;
    @track message = '';
    @track mtmElegibility;
    @track mebGenKey;
    @track productTypeLOB;
    @track isMTMCalled;
    @track memberPlanId;
    @track objectApiName = 'MemberPlan';
    @track memberId;
    @track templateNumber;
    

    @wire(getRecord, {
        recordId: '$accountId',
        fields: ['Account.Mbr_Gen_Key__c']
      })
      wiredAccount({ error, data }) {
        if (data) {
            this.memGenKey = data.fields.Mbr_Gen_Key__c.value;
            if (this.memGenKey && this.productTypeLOB && this.labels.medicare_Set.includes(this.productTypeLOB) && !this.isMTMCalled){this.getMTMData();}
        } else if (error) {
            console.log('error in wire--', error);
        }
      }
      @wire(getTemplateNum)
      templateNumResult({error, data}) {
          if (data) {
              this.templateNumber = data;
          } else if (error) {
              console.log(error, "error retrieving template number")
          }
      }
      getPolicyDetails(){
        if(this.memberPlanId){
            getMemberPlanDetails(this.memberPlanId)
                .then(result => {
                    this.productTypeLOB = result?.Product_Type__c??'';
                }).catch(error => {
                    console.log(error);
                })
        }else{
            return new Promise((resolve, reject) => {
                getAllMemberPlans(this.accountId).then(result => {
                    if (result && Array.isArray(result) && result?.length > 0) {
                        this.productTypeLOB = result[0]?.Product_Type__c ?? '';
                    }
                    resolve(true);
                }).catch(error => {
                    console.log(error);
                    reject(error);
                })
            });
        }
        if (this.memGenKey && this.productTypeLOB && this.labels.medicare_Set.includes(this.productTypeLOB) && !this.isMTMCalled)
            {this.getMTMData();}
    }
    
    connectedCallback() {
        this.subscribeToMessageChannel();
        this.loadCommonCSS();
        if (!this.productTypeLOB) this.getPolicyDetails();
    }
    
     getMTMData() {
        if (this.memGenKey){
             getMtmData(this.memGenKey)
                .then(result => {
                    if(result){
                        let mtmResult = result;
                        this.mtmElegibility = mtmResult?.Member?.MemberEligibility?.CmrEligibilityStatus
                                            && mtmResult?.Member?.MemberEligibility?.CmrEligibilityStatus?.toUpperCase() === 'Y'
                                            && mtmResult?.Member?.MemberEligibility?.MtmMember 
                                            && mtmResult?.Member?.MemberEligibility?.MtmMember?.toUpperCase() === 'Y'
                                            ? true : false;
                        this.isMTMCalled = true; 
                    }
                }).catch(error => {
                    console.log('getMTMData',error);
                    this.isMTMCalled = true;      
                })
        }
    }

    labels = {
        pharmacyDemographicsNoData,
        pharmacyDemographicsError,
        medicare_Set
    };
    
    messageContext = createMessageContext();

    subscribeToMessageChannel() {
        if (!this.subscription) {
            this.subscription = subscribe(
                this.messageContext,
                humanaPharmacyLMS,
                (message) => this.handleMessage(message)
            );
        }
    }

    @wire(CurrentPageReference)
    getStateParameters(currentPageReference) {
        if (currentPageReference) {
            this.urlStateParameters = currentPageReference.state;
            this.setParametersBasedOnUrl();
        }
    }

    setParametersBasedOnUrl() {
        this.accountId = this.urlStateParameters?.c__AccountID ?? null;
        this.enterpriseId = this.urlStateParameters?.c__enterpriceID ?? '';
        this.userId = this.urlStateParameters?.c__userId ?? '';
        this.userProfile = this.urlStateParameters?.c__userProfile ?? '';
        this.memGenKey = atob(this.urlStateParameters?.c__memGenKey ?? '');
        this.productTypeLOB =  this.urlStateParameters?.c__ProductTypeLOB?.value ?? '';
        this.memberPlanId = this.urlStateParameters?.c__PlanMemberId ?? '';
        this.MemberId = atob(this.urlStateParameters?.c__MemberId??'');
        if (this.memGenKey && this.productTypeLOB && this.labels.medicare_Set.includes(this.productTypeLOB) && !this.isMTMCalled){this.getMTMData();}
        this.loadMemberIcons();
    }

    loadCommonCSS() {
        Promise.all([
            loadStyle(this, customcss + '/PharmacyLightning_CSS_HUM.css')
        ]).catch(error => {
            console.log('Error Occured', error);
        });
    }

    handleMessage(message) {
        if (message && message?.MessageName) {
            if (message?.serviceError && message?.serviceError === true) {
                this.serviceError = true;
                this.displayErrorMessage();
            } else if (message?.noDataError && message?.noDataError === true) {
                this.noDataError = true;
                this.displayErrorMessage();
            }
            else {
                switch (message?.MessageName) {
                    case 'profile':
                        this.displayProfileData(message?.payload);
                        break;
                    case 'finance':
                        this.displayFinanceData(message?.payload);
                        break;
                    case 'address':
                        this.displayAddressData(message?.payload);
                        break;
                    case 'VerifyDemographics':
                        this.handleMessageVerifyDemographics(message);
                        break;
                    case 'authorizationIcon':
                        this.updateAuthrizationIcon(message?.payload);
                        break;
                }
            }
        }
    }

    displayErrorMessage() {
        this.message = this.serviceError ? this.labels.pharmacyDemographicsError : this.noDataError ?
            this.labels.pharmacyDemographicsNoData : '';
    }

    handleMessageVerifyDemographics(message) {
        if (message && Object.keys(message)?.length > 0) {
            this.bShowDemographicsWarning = message?.messageDetails && message?.messageDetails > 90 ? true : false;
        }
    }

    displayProfileData(payload) {
        this.memberEmail = payload?.email ?? '';
        this.accountNumber = payload?.accountNumber ?? '';
        this.memberName = payload?.memberName ?? '';
        this.memberDOB = getFormatDate(payload?.dob ?? '');
        this.profileLoaded = true;
    }

    displayAddressData(payload) {
        this.shippingAddress = payload?.shippingAddress ?? '';
        this.addressLoaded = true;
    }

    displayFinanceData(payload) {
        this.accountBalance = payload?.accountBalance ?? '';
        this.accountLimit = payload?.accountLimit ?? '';
        this.foreColor = parseFloat(payload?.accountBalance) > 0 ?
            'slds-truncate slds-text-color_error' : 'slds-truncate';
        this.financeLoaded = true;
        this.updateCreditCardIcons(payload);
    }

    get shippingHomeStreet() {
        return `${this.shippingAddress?.addressLine1 ?? ''} ${this.shippingAddress?.addressLine2 ?? ''}`
    }

    verifyDemographicsData(event) {
        let message = { messageDetails: 'onclickVerify', MessageName: "fireVerifyDemographicsService" };
        publish(this.messageContext, humanaPharmacyLMS, message);
    }

    loadMemberIcons() {
        const iconParams = {
            sPageName: 'Humana Pharmacy',
            sRecordId: this.accountId
        };
        getMemberIcons(iconParams).then((result) => {
            if (result && result.bIconsPresnt) {
                if (result && result?.lstMemberIcons && Array.isArray(result?.lstMemberIcons)
                    && result?.lstMemberIcons?.length > 0)
                    this.memberIcons = result?.lstMemberIcons.map(item => ({
                        ...item,
                        bIconVisible: item?.sIconName === "PharmacyAuthorization"
                            || item?.sIconName === "creditCardExpiredIcon"
                            || item?.sIconName === "creditCardExpiringSoonIcon"
                            ? false : true
                    }))
                console.log(this.memberIcons);
            }
        }).catch((error) => {
            console.log('Error Occured', error);
        });
    }

    newCaseClick() {
        openLWCSubtab('caseInformationComponentHum', this.accountId, { label: 'New Case', icon: 'standard:case' }, { pageName: 'Humana_Pharmacy_Tab' });
    }

    updateCreditCardIcons(payload) {
        if (payload && payload?.creditCardExpired && payload?.creditCardExpired === true) {
            if (this.memberIcons.find(k => k?.sIconName === 'creditCardExpiredIcon') != null) {
                this.memberIcons.find(k => k?.sIconName === 'creditCardExpiredIcon').bIconVisible = true;
            }
        }
        if (payload && payload?.creditCardExpired && payload?.creditCardExpiring === true) {
            if (this.memberIcons.find(k => k?.sIconName === 'creditCardExpiringSoonIcon') != null) {
                this.memberIcons.find(k => k?.sIconName === 'creditCardExpiringSoonIcon').bIconVisible = true;
            }
        }
    }

    updateAuthrizationIcon(payload) {
        if (payload && payload?.authorization && payload?.authorization === true) {
            if (this.memberIcons.find(k => k?.sIconName === 'PharmacyAuthorization') != null) {
                this.memberIcons.find(k => k?.sIconName === 'PharmacyAuthorization').bIconVisible = true;
            }
        }
    }
    navigateToFlow() {
        let tempObj = {};
        tempObj.templateName = 'pharmacyWebIssuesGuidedFlow';
        tempObj.templateLabel = 'Pharmacy Web Issues';
        tempObj.hasAccess = false;
        tempObj.showInAction = true;
        tempObj.params = 'Case_ID,Members_Name,Members_DateOf_Birth,TN,Members_ID';
        tempObj.TemplateType = 'Screen Flow';
        tempObj.description = 'This template is used to capture web issues related information.';
        tempObj.flowParamsJSON = this.getFlowParameters(tempObj.params, this.objectApiName, '');

    var compDefinition = {
        componentDef: "c:pharmacyWebIssueFlowHum",
        attributes:
        {
            flowParams: tempObj.flowParamsJSON,
            flowName: 'pharmacyWebIssuesGuidedFlow'
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
                            label: 'Web Issues'
                        });
                        invokeWorkspaceAPI('setTabIcon', { tabId: tabId, icon: 'standard:case', iconAlt: '' });
                    });
                });
            }
        });
    
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
            switch (sParamName) {
                case 'Case_ID':
                    objParam.value = this.accountId;
                    break;
                 case 'SID':
                    objParam.value = templateSubmittionId;
                    break;
                case 'Members_Name':
                    objParam.value = JSON.stringify(this.memberName).replace(/\\n\s+/g,'').replace(/\"/g,'')??'';
                    break;
                case 'Members_DateOf_Birth':
                    objParam.value = this.memberDOB??'';
                    break;
                case 'TN':
                    objParam.value = this.templateNumber;
                    break;
                case 'Members_ID':
                    objParam.value = this.MemberId??'';
                    break;
            }
            tempflowParams.push(objParam);
        });
        tempflowParams.push({
            type: 'Boolean',
            name: 'isPharmacy',
            value: true
        })
    }
  
    let paramsJSON = (tempflowParams) ? JSON.stringify(tempflowParams) : '';
    return paramsJSON;
}

}