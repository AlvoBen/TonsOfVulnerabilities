/*--
File Name        : genericExternalLinkLauncherHum.js
Created Date     : 05/27/2022
Modification Log :
* Developer                          Date                  Description
*************************************************************************************************
* Kalyani Pachpol                   05/27/2022           User Story 3150124: Benefits External link
* Kalyani Pachpol                   07/28/2022           US- 3613352
* Kalyani Pachpol                   08/10/2022           US- 3613352
* Aishwarya Pawar                   09/27/2022           US- 3552709
* Nirmal Garg                       07/26/2023           US-4910752
* Anuradha Gajbhe                   10/18/2023           US-5066619 - Benefit and Accumulator page- ability to pass data elements to Debut from CRM links  
* Raj Paliwal                       10/18/2023           US-5066625 - Auth/referral page- ability to pass data elements to Debut from CRM links 
* Dimple Sharma                     10/18/2023           US-4934019 - Plan Member page- ability to pass data elements to Debut from CRM links 
**************************************************************************************************/
import { LightningElement, track, wire, api } from 'lwc';
import { CurrentPageReference } from 'lightning/navigation';
import { invokeWorkspaceAPI } from "c/workSpaceUtilityComponentHum";
import launchBenefitMatrix from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.generateBenefitMatrixURL';
import launchDebutTool from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.generateDebutToolURL';
import launchBenefitGrid from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.generateBenefitGridURL';
import launchCertificateOfCoverage from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.generateCertificateURL';
import getRxConnectProURL from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.generateRXConnectProURL';
import invokeEncryptService from '@salesforce/apexContinuation/ExternalLinkLauncher_LC_Hum.invokeEncryptService';
import getInteractionAndCaseDetail from '@salesforce/apex/ExternalLinkLauncher_LC_Hum.getInteractionAndCaseDetail';
import { getMemberPlanDetails } from 'c/genericMemberPlanDetails';
import { getFormatDate } from 'c/crmUtilityHum';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
const MAX_NEW_DATE = '4000-12-31';
const MAX_OLD_DATE = '9999-12-31';
export default class GenericExternalLinkLauncherHum extends LightningElement {
    currentPageReference = null;
    urlStateParameters = null;
    @api pageName;
    @api linkName;
    @api recordId;
    @api Id;
    @track url;
    @api source;
    @api EnterpriseId;
    @api ContractNumber;
    @api PBPCode;
    @api MedicareSegmentId;
    @api EffectiveTo;
    @api EffectiveFrom;
    @track encodedEnterpriseId;
    @api previousTabId;
    @track bInteractionCaseSwitch = false;


    @wire(CurrentPageReference)
    wiredPageRef(currentPageReference) {
        this.interactionIdObj = currentPageReference?.state?.ws ? (currentPageReference.state.ws).split('?')[1] : '';
        this.interactionId = this.interactionIdObj ? this.interactionIdObj.split('=')[1] : '';
    }

    connectedCallback() {
        if (this.EffectiveTo) {
            this.EffectiveTo = this.changeDateformat(this.EffectiveTo) === MAX_NEW_DATE ? MAX_OLD_DATE : this.EffectiveTo;
        }
        this.recordId = this.recordId ?? this.Id;
        this.launchExternalLink();
    }

    launchExternalLink() {
        this.url = '';
        switch (this.pageName) {
            case "Benefits":
                switch (this.linkName) {
                    case "BenefitMatrix":
                        this.launchBenefitMatrix();
                        break;
                    case "DebutTool":
                        this.getInteractionAndCaseDetails();
                        this.launchDebutTool();
                        break;
                    case "BenefitGrid":
                        this.launchBenefitGrid();
                        break;
                    case "CertificateOfCoverage":
                        this.launchCertificateOfCoverage();
                        break;
                    case "RxConnectPro":
                        this.launchRXConnectPro();
                        break;
                }
                break;
            case "Auth/Referral Summary":
                switch (this.linkName) {
                    case "DebutTool":
                        this.getInteractionAndCaseDetails();
                        this.launchDebutTool();
                        break;
                }
                break;
        }
    }
    launchRXConnectPro() {
        getRxConnectProURL({ memplanid: this.recordId, source: this.source })
            .then(result => {
                if (result && result?.IdForMember) {
                    invokeEncryptService({ MemberId: result.IdForMember })
                        .then(encrypteddata => {
                            let encoded = encodeURIComponent(encrypteddata);
                            encoded = encodeURIComponent(encoded);
                            let finalURL = result.sURL + encoded;
                            if (result.sRxConnectFlag === true)
                                finalURL = finalURL + 'source=scrm';
                            this.url = result.HSS_ONECLICK_URL + '?' + result.HSS_ONECLICK_TARGET + '=' + finalURL;
                            this.launchlink('width=1000');
                        }).catch(error => {
                            console.log(error);
                        })
                }
            }).catch(error => {
                console.log(error);
            })
    }


    launchCertificateOfCoverage() {
        getCustomSettingValue('Certificate', 'CertificateURL')
            .then(result => {
                let baseUrl = result?.CertificateListURL__c ?? '';
                if (baseUrl && baseUrl?.length > 0) {
                    getMemberPlanDetails(this.recordId).then(result => {
                        this.memberPlanDetails = result && Array.isArray(result) && result?.length > 0 ? result[0] : null;
                        if (this.memberPlanDetails && Object.keys(this.memberPlanDetails)?.length > 0) {
                            baseUrl += `?pers=%20${this.memberPlanDetails?.Member?.Mbr_Gen_Key__c ?? ''}`;
                            baseUrl += `&ptfm=${this.memberPlanDetails?.Plan?.Platform__c ?? ''}`;
                            baseUrl += `&prod=${this.memberPlanDetails?.Plan.Product__r?.Sold_Product_Key_Value__c ?? ''}`;
                            baseUrl += `&lob=${this.memberPlanDetails?.Policy__r?.Major_LOB_Frm__c ?? ''}`;
                            baseUrl += `&seq=${this.memberPlanDetails?.Plan?.Product__r?.Segment_Type__c ?? ''}`;
                            baseUrl += `&cust=${this.memberPlanDetails?.Plan?.Payer?.Enterprise_ID__c ?? ''}`;
                            baseUrl += `&ccnbr=${this.memberPlanDetails?.Plan?.Purchaser_Plan_External_ID__c?.split('|')[3] ?? ''}`;
                            baseUrl += `&sub=${this.memberPlanDetails?.SubscriberPlanId__r?.Member.Mbr_Gen_Key__c ?? ''}`;
                            baseUrl += `&asof=${this.memberPlanDetails?.EffectiveFrom ? getFormatDate(this.memberPlanDetails?.EffectiveFrom) : this.memberPlanDetails?.EffectiveTo
                                ? getFormatDate(this.memberPlanDetails?.EffectiveTo) : getFormatDate(new Date())}`;
                            baseUrl += `&port=22`;
                            baseUrl = baseUrl.includes('HIDDENTARGET=') ? `${baseUrl.split('HIDDENTARGET=')[0]}HIDDENTARGET=${btoa(baseUrl.split('HIDDENTARGET=')[1])}` : baseUrl;
                        }
                    }).then(() => {
                        this.url = baseUrl;
                        this.launchlink('width=1000');
                    }).catch(error => {
						baseUrl = baseUrl.includes('HIDDENTARGET=') ? `${baseUrl.split('HIDDENTARGET=')[0]}HIDDENTARGET=${btoa(baseUrl.split('HIDDENTARGET=')[1])}` : baseUrl;
                        this.url = baseUrl;
                        this.launchlink('width=1000');
                    })
                }
            }).catch(error => {
                console.log(error);
            })
    }

    launchBenefitGrid() {
        launchBenefitGrid({ memplanid: this.recordId })
            .then(result => {
                if (result) {
                    this.url = result;
                    this.launchlink('width=1000');
                }
            }).catch(error => {
                console.log(error);
            })
    }

    changeDateformat(sDate) {
        let formattedDate;
        if (sDate !== MAX_OLD_DATE) {
            sDate = new Date(sDate).toLocaleDateString('en-US');
            if (sDate != '') {
                if (sDate.includes('/')) {
                    let eDate = sDate.split('/');
                    if (eDate.length > 0) {
                        formattedDate = `${eDate[2] ?? ''}-${eDate[0].padStart(2, '0') ?? ''}-${eDate[1].padStart(2, '0') ?? ''}`
                        return formattedDate;
                    }
                }
                else {
                    return sDate;
                }
            }
        } else {
            return sDate
        }
    }

    getInteractionAndCaseDetails(){
        getInteractionAndCaseDetail({interactionId : this.interactionId})
        .then(result => {
            if (result) {
                if(result.interactionCaseSwitch){
                    this.interactionId = result ? (result.InteractionId ? result.InteractionId : '') : '';
                    this.case = result ? (result.IntCase ? result.IntCase : '') : '';
                    this.bInteractionCaseSwitch = result ? result.interactionCaseSwitch: false;
                }  
            }
        })
    }

    launchDebutTool() {
        launchDebutTool({})
            .then(result => {
                if (result) {
                    this.url = result;
                    if (this.EnterpriseId) {
                        this.encodedEnterpriseId = this.EnterpriseId ? btoa(this.EnterpriseId) : '';
                        this.url += this.encodedEnterpriseId ? `enterpriseId=${this.encodedEnterpriseId}` : '';
                        this.url += (this.ContractNumber && this.ContractNumber != 'null') && (this.PBPCode && this.PBPCode != 'null') && (this.MedicareSegmentId && this.MedicareSegmentId != 'null') ? `&planId=${this.ContractNumber}-${this.PBPCode}-${this.MedicareSegmentId}` : '';
                        this.url += this.EffectiveFrom ? `&coverageEffectiveDate=${this.changeDateformat(this.EffectiveFrom)}` : '&coverageEffectiveDate=';
                        this.url += this.EffectiveTo ? `&coverageEndDate=${this.changeDateformat(this.EffectiveTo)}` : '&coverageEndDate=';
			            if (this.bInteractionCaseSwitch){
                        	this.url += this.case ? `&case=${this.case}` : '';
                            this.url += this.interactionId ? `&interactionId=${this.interactionId}` : '';
                        }
                    }
                    this.launchlink('top=0,left=0,width=1280,height=1024');
                }
            }).catch(error => {
                console.log(error);
            })
    }


    launchBenefitMatrix() {
        launchBenefitMatrix({ planid: this.recordId })
            .then(result => {
                if (result) {
                    this.url = result;
                    this.launchlink('width=1000');
                }
            }).catch(error => {
                console.log(error);
            })
    }

    launchlink(windowatt) {
        window.open(this.url, "_blank", "toolbar=yes, scrollbars=yes, resizable=yes," + windowatt);
        this.closesubtab();
    }

    async toFocusTab() {
        try {
            await invokeWorkspaceAPI("focusTab", {
                tabId: this.previousTabId
            });

        } catch (error) {

        }
    }

    async closesubtab() {
        const focusedTab = await invokeWorkspaceAPI("getFocusedTabInfo");
        this.toFocusTab();
        await invokeWorkspaceAPI("closeTab", {
            tabId: focusedTab.tabId
        });
    }
}