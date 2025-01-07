const PCP_QUESTION = 'PCP Question';
const PCP_SERVICE_FUND = 'PCP ServiceFund';
const CASE = "case";
const MEMBER_PLAN = "memberplan";
const PROVIDER_SEARCH = "Provider Search";
import attachProcessToCase from '@salesforce/apex/Logging_LC_HUM.updateWhoId';
import { Network_DTO_HUM } from './providersearchrequest';
import getNetworkId from '@salesforce/apexContinuation/PCPUpdate_LC_HUM.getNetworkId';
import validatePCP from '@salesforce/apexContinuation/PCPUpdate_LC_HUM.validatePCP';
import updateMember from '@salesforce/apexContinuation/PCPUpdate_LC_HUM.updateMember';
import { toastMsge, getFormatDate } from 'c/crmUtilityHum';
import { ValidateEligibilityRequest_DTO_HUM } from './validatepcprequest';
import { UpdatePCP_DTO_HUM } from './updatepcprequest';

export function updateAnswer(event) {
    if (event?.target?.dataset?.name) {
        switch (event?.target?.dataset?.name) {
            case PCP_QUESTION:
                this.pcpupdatequestion.find(k => k?.Id === event?.target?.dataset?.id).Answer = event?.detail?.value;
                break;
            case PCP_SERVICE_FUND:
                this.serviceFundQuestions.find(k => k?.Id === event?.target?.dataset?.id).Answer = event?.detail?.value;
                break;
            case PROVIDER_SEARCH:
                console.log(this.templateMasterData);
                this.providerSearchQuestions.find(k => k?.Id === event?.target?.dataset?.id).Answer = event?.detail?.value;
                if (this.isInputValid()) {
                    this.navigateToNextScreen(event?.detail?.value?.toLocaleLowerCase());
                }
        }
    }
    if (this.templateMasterData && Array.isArray(this.templateMasterData) && this.templateMasterData.length > 0) {
        this.templateMasterData.forEach(obj => {
            if (obj?.Id === event?.target?.dataset?.id) {
                obj.Value = event?.detail?.value;
            }
        })
    }
    if (this.pcpupdatequestion.every(checkValue)) {
        this.disableNextSaveButton = false;
    }
}

function checkValue(element) {
    if (element && element?.Answer && element?.Answer?.length > 0) {
        return true;
    }
    return false;
}

export function redirectToCasePages() {
    switch (this.objectApiName?.toLocaleLowerCase()) {
         case CASE:
            if (this.pcpsummaryscreen && this.pagename != 'New Case') {
                switch (this.pagename?.toLocaleLowerCase()) {
                    case "case":
                        if (this.servicefundedit) {
                           this.redirectToCaseEditPage();

                        } else {
                           this.fireProcessSectionRefreshEvent();
                        }
                        break;
                    case "case edit":
                        this.fireProcessSectionRefreshEvent();
                        break;
                }
            }
            break;
        case MEMBER_PLAN:
            if (this.pcpsummaryscreen) {
                if (this.newcase) {
                    attachProcesstocase.call(this).then(result => {
                        if (result) {
                            this.redirectToCaseEditPage();
                        } else {
                            console.log('error attaching template');
                        }
                    }).catch(error => {
                        console.log(error);
                    })

                } else if (this.existingcase) {
                    if (this.servicefundedit) {
                        attachProcesstocase.call(this).then(result => {
                            if (result) {
                                this.redirectToCaseEditPage();
                            } else {
                                console.log('error attaching template');
                            }
                        }).catch(error => {
                            console.log(error);
                        })
                    } else {
                        attachProcesstocase.call(this).then(result => {
                            if (result) {
                                this.redirectToCaseDetailPage();
                            } else {
                                console.log('error attaching template');
                            }
                        }).catch(error => {
                            console.log(error);
                        })

                    }
                }
            }
            break;
    }
    setTimeout(() => {
        this.closesubtab();
    }, 3000)
}

function attachProcesstocase() {
    let processSids = [];
    processSids.push(this.tempSubmissionId);
    return new Promise((resolve, reject) => {
        attachProcessToCase({ processIds: processSids, caseid: this.caseRecordId }).then(result => {
            if (result && result === true) {
                resolve(true);
            }
            else {
                resolve(false)
            }
        }).catch(error => {
            console.log(error);
            reject(false);
        })
    })
}

export function getNetworkData() {
    return new Promise((resolve, reject) => {
        let request = {};
        this.providerIds = [];
        request.GetNetworks = new Network_DTO_HUM(1, this.memberplandata?.Plan?.Selling_Market_Number__c ?? '');
        getNetworkId({ requestbody: JSON.stringify(request) })
            .then(result => {
                if (result && result?.NetworksResponse?.Networks && Array.isArray(result?.NetworksResponse?.Networks)
                    && result?.NetworksResponse?.Networks.length > 0) {
                    this.providernetworkId = result?.NetworksResponse?.Networks[0]?.networkId ?? null;
                    result?.NetworksResponse?.Networks.forEach(k => {
                        if (k && k?.networkId) {
                            if (this.providerIds && this.providerIds?.length > 0) {
                                if (this.providerIds.findIndex(t => t?.networkId === k?.networkId) < 0) {
                                    this.providerIds.push({
                                        networkId: k?.networkId,
                                        networkName: k?.networkName
                                    });
                                }
                            } else {
                                this.providerIds.push({
                                    networkId: k?.networkId,
                                    networkName: k?.networkName
                                });
                            }
                        }
                    })
                } else {
                    this.providernetworkId = this.mbeNetworkId;
                    this.providerIds.push({
                        networkId: this.mbeNetworkId,
                        networkName: ''
                    });
                }
                resolve(true);
            }).catch(error => {
                console.log(error);
                this.providernetworkId = this.mbeNetworkId;
                this.providerIds.push({
                    networkId: this.mbeNetworkId,
                    networkName: ''
                });
                resolve(true);
            })
    })
}

export function validatePCPEligibilty() {
    this.loaded = false;
    return new Promise((resolve, reject) => {
        let request = {};
        request.ValidateEligibilityRequest = new ValidateEligibilityRequest_DTO_HUM(this.memberplandata?.Policy_Platform__c ?? '',
            this.memberplandata?.Issue_State__c ?? '', getGroupId(this.memberplandata))
        validatePCP({ requestbody: JSON.stringify(request) })
            .then(result => {
                if (result && result?.length > 0) {
                    let response = JSON.parse(result);
                    if (response && response?.ValidateEligibilityResponse &&
                        response?.ValidateEligibilityResponse?.result?.toUpperCase() === 'OK'
                        && response?.ValidateEligibilityResponse?.responseCode === '200') {
                        if (response && response?.ValidateEligibilityResponse?.members && Array.isArray(response?.ValidateEligibilityResponse?.members)
                            && response?.ValidateEligibilityResponse?.members.length > 0) {
                            let eligibilities = response?.ValidateEligibilityResponse?.members[0]?.eligibilities ?? null;
                            this.isValidatePCP = eligibilities && Array.isArray(eligibilities) && eligibilities?.length > 0 ?
                                eligibilities[0]?.pcpResponse?.eligible : 'false';
                            if (this.isValidatePCP?.toLocaleLowerCase() === 'true') {
                                resolve(true);
                            } else if (this.isValidatePCP?.toLocaleLowerCase() === 'false') {
                                this.loaded = true;
                                toastMsge('Validate PCP Eligibility', 'This plan does not requires to have a primary care physician.', 'warning', 'pester');
                                resolve(false);
                            }
                        }
                    } else if (response && response?.result?.toUpperCase() !== 'OK' && response?.responseCode !== '200') {
                        this.loaded = true;
                        toastMsge('Validate PCP Eligibility', 'Error', 'error', 'pester');
                        reject(false);
                    }
                } else {
                    this.loaded = true;
                }
            }).catch(error => {
                console.log(error);
                this.loaded = true;
                toastMsge('Validate PCP Eligibility', 'Error', 'error', 'pester');
                reject(false)
            })
    });
}

const getGroupId = (memberplandata) => {
    return ['LV', 'EM'].includes(memberplandata?.Policy_Platform__c) && memberplandata?.Plan?.Source_Cust_Cov_Key__c?.length > 5 ? memberplandata?.Plan?.Source_Cust_Cov_Key__c.substring(0, 6) ?? '' :
        memberplandata?.Policy_Platform__c?.toUpperCase() === 'CB' ? memberplandata?.Plan?.Source_Cust_Cov_Key__c?.length > 5 ? memberplandata?.Plan?.Source_Cust_Cov_Key__c.substring(0, 6) ?? '' :
            memberplandata?.Plan?.Source_Cust_Cov_Key__c ?? '' : '';
}


export function updateMemberPCP() {
    return new Promise((resolve, reject) => {
        let request = {};
        request.UpdateMemberRequest = new UpdatePCP_DTO_HUM(this.requestEffectiveDate, this.selectedPhysicianData?.pcpNumber ?? '',
            this.selectedPhysicianData?.physicianName ?? '', getFormatDate(this.memberdob, 'yyyy-mm-dd'),
            this.memberplandata?.Issue_State__c ?? '', this.memberplandata?.Policy_Platform__c?.toUpperCase() === 'LV' ? this.caspersonid ?? this.memberplandata?.Member?.Enterprise_ID__c ?? ''
            : this.memberplandata?.Policy_Platform__c?.toUpperCase() === 'EM' ? this.mtvpersonid ?? this.memberplandata?.Member?.Enterprise_ID__c ?? '' : ''
            , this.memberplandata?.Policy_Platform__c?.toUpperCase() === 'LV' ? this.caspersonid ?? this.memberplandata?.Member?.Enterprise_ID__c ?? ''
                : this.memberplandata?.Policy_Platform__c?.toUpperCase() === 'EM' ? this.mtvpersonid ?? this.memberplandata?.Member?.Enterprise_ID__c ?? '' : '',
            this.memberplandata?.Policy_Platform__c ?? '', getGroupId(this.memberplandata),
            this.memberplandata?.Policy_Platform__c?.toUpperCase() === 'LV' ? this.memberplandata?.Plan?.Source_Cust_Cov_Key__c?.length >= 12 ? this.memberplandata?.Plan?.Source_Cust_Cov_Key__c.substring(6, 9) ?? '000' : '000'
                : this.memberplandata?.Policy_Platform__c?.toUpperCase() === 'EM' ? this.memberplandata?.Plan?.Benefit_Coverage__c ?? '' : '');

        updateMember({ requestbody: JSON.stringify(request) })
            .then(result => {
                let response = JSON.parse(result);
                if (response && response?.UpdateMemberResponse && response?.UpdateMemberResponse?.responseCode === 'S') {
                    toastMsge('Update PCP', 'Selected PCP was updated successfully in CI.', 'success', 'pester');
                    resolve(true);
                } else if (response && response?.UpdateMemberResponse && response?.UpdateMemberResponse?.responseCode !== 'S'
                    && response?.UpdateMemberResponse?.errormessage && response?.UpdateMemberResponse?.errormessage?.length > 0) {
                    toastMsge('Update PCP', 'Selected PCP was NOT updated in CI. Please try again or contact System Administrator.', 'error', 'pester');
                    resolve(false);
                } else {
                    toastMsge('Update PCP', 'Selected PCP was NOT updated in CI. Please try again or contact System Administrator.', 'error', 'pester');
                    resolve(false);
                }
                this.loaded = true;
            }).catch(error => {
                console.log('Update PCP Error', error);
                toastMsge('Update PCP', 'Selected PCP was NOT updated in CI. Please try again or contact System Administrator.', 'error', 'pester');
                this.loaded = true;
                reject(false);
            })
    });
}