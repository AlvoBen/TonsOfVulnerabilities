/*

Modification Log:
* Developer Name                  Date                         Description
*------------------------------------------------------------------------------------------------------------------------------
*Kalyani Pachpol                 07/27/2022                    initial version US-3613352
****************************************************************************************************************************/

import CAS_Product_Type from '@salesforce/label/c.CAS_Product_Type';
import invokementorGridService from '@salesforce/apexContinuation/Benefits_LC_HUM.mentorGridService';
import setBaseURL from '@salesforce/apex/MentorRedirect_LC_HUM.setBaseURL';
import getBusinessGroups from '@salesforce/apex/MentorRedirect_LC_HUM.getBusinessGroups';
import { getMemberPlanDetails } from 'c/genericMemberPlanDetails';
let PARAMTITLE_MENTORREDIRECT_HUM = '?queryText=';
let MAJOR_LOB_MES = 'MES';
let CONSTANTSUBGROUP_MENTORREDIRECT_HUM = '&benefitgrid=0&filterType=Documents';
let TARGET_MENTORREDIRECT_HUM = 'TARGET=';
let PARAMSUBGROUP_MENTORREDIRECT_HUM = '?queryText=subgroup:';
let PARAMFILTER_MENTORREDIRECT_HUM = '?filterType=Documents';

const openMentor = (memberplandata, type) => {
    switch (type) {
        case "data":
            checkplatform(memberplandata);
            break;
        case "Id":
            getMemberPlanDetails(memberplandata)
                .then(result => {
                    if (result && Array.isArray(result) && result.length > 0) {
                        checkplatform(result[0]);
                    }
                }).catch(error => {
                    console.log(error)
                })
    }
}

const checkplatform = (memberplandata) => {
    if (memberplandata) {
        var regex = /[a-zA-Z]/;
        if (memberplandata?.Plan?.Platform__c == 'EM' && memberplandata?.Plan?.Benefit_Coverage__c?.length >= 3) {
            if ((memberplandata?.Plan?.Benefit_Coverage__c?.substring(0, 2)?.includes('SF') &&
                regex.test(memberplandata?.Plan?.Benefit_Coverage__c?.substring(2, 3))) ||
                (memberplandata?.Plan?.Benefit_Coverage__c?.substring(0, 2)?.includes('FI') &&
                    regex.test(memberplandata?.Plan?.Benefit_Coverage__c?.substring(2, 3))) ||
                memberplandata?.Plan?.Benefit_Coverage__c?.substring(0, 2)?.includes('FE')) {
                launchMentor(memberplandata);
            }
        } else if (memberplandata?.Plan?.Platform__c === 'LV' && !CAS_Product_Type.includes(memberplandata?.Product_Type__c)) {
            invokementorGridService({ memberPlanId: memberplandata.Id })
                .then(result => {
                    let mentorGridServiceResponse = JSON.parse(result);
                    if (mentorGridServiceResponse === true || mentorGridServiceResponse === 'true')
                        launchMentor(memberplandata);
                })
                .catch(error => {
                    console.log(error);
                });
        }
    }
}

const launchMentor = (memberplandata) => {
    let sBaseURL;
    let sProduct = memberplandata.Product__c;
    setBaseURL().then(result => {
        sBaseURL = result;
        if (sProduct && sProduct === 'MED' && getBusinessProdGroups(memberplandata)) {
            sBaseURL = sBaseURL + isMedicareMedicaidPolicy(memberplandata);
        }
        else {
            sBaseURL = sBaseURL + isNonMedicareMedicaidPolicy(memberplandata);
        }
        sBaseURL = hssBaseEncode(sBaseURL);
        window.open(sBaseURL, "_blank", "toolbar=yes, scrollbars=yes, resizable=yes,width=1000");
    })
}

const getBusinessProdGroups = (memberplandata) => {
    getBusinessGroups({ sProduct: memberplandata.Product__c, sProductType: memberplandata.Product_Type__c })
        .then(result => {
            return result;
        })
        .catch(err => {
            console.log("Error occured - " + err);
        });
}

const isMedicareMedicaidPolicy = (memberplandata) => {
    let sMentorURL = '';
    let bIsMCDProduct = false;
    let sContractNum = memberplandata?.Plan?.Contract_Number__c ?? '';
    let sPBPCode = memberplandata?.Plan?.PBP_Code__c ?? '';
    let sMedicaresegId = memberplandata?.Plan?.Medicare_Segment_ID__c ?? '';
    let sProductDesc = memberplandata?.Product_Description__c ?? '';
    let sProductType = memberplandata?.Product_Type__c ?? '';
    let sStateCode = memberplandata?.Issue_State__c ?? '';
    bIsMCDProduct = sProductType && sProductType === 'MCD' ? true : false;
    if (memberplandata && memberplandata?.Policy_Platform__c &&
        memberplandata?.Policy_Platform__c === 'LV' && bIsMCDProduct) {
        let stateCode = '%22' + sStateCode + ' Medicaid Comprehensive Benefit Grid%22';
        sMentorURL = PARAMTITLE_MENTORREDIRECT_HUM + stateCode + '&filterType=Benefit Grids&filterset=FilterSet&filters={%22Function%22:%22none%22,%22SubFunction%22:%22none%22,%22Platform%22:%22none%22,%22BusinessSegment%22:%22none%22,%22LineOfCoverage%22:%22none%22,%22Market%22:%22none%22}';
    }
    else {
        let sQueryText = sContractNum + '-' + sPBPCode;
        if (sMedicaresegId) {
            sQueryText += `-${sMedicaresegId}`;
        }
        if (sProductDesc && sProductType && sProductType === MAJOR_LOB_MES) {
            sQueryText += `-${sProductDesc}`
        }
        sMentorURL = PARAMTITLE_MENTORREDIRECT_HUM + sQueryText + CONSTANTPARAMEND_MENTORREDIRECT_HUM;
    }
    return sMentorURL;
}

const isNonMedicareMedicaidPolicy = (memberplandata) => {
    let sMentorURL = '';
    let sPlatform = memberplandata?.Plan?.Platform__c ?? '';
    let sBenCoverage = memberplandata?.Plan?.Benefit_Coverage__c ?? '';
    let sGrpNumber = memberplandata?.Plan?.Policy_Group_Number__c ?? '';
    if (sPlatform && (sPlatform === 'EM' || sPlatform == 'MTV')) {
        sMentorURL = PARAMSUBGROUP_MENTORREDIRECT_HUM + sBenCoverage + CONSTANTSUBGROUP_MENTORREDIRECT_HUM;
    }
    else if (sPlatform && (sPlatform == 'LV' || sPlatform == 'CI' || sPlatform == 'CAS')) {
        if (sGrpNumber && sGrpNumber?.length > 0) {
            sMentorURL = PARAMSUBGROUP_MENTORREDIRECT_HUM + sGrpNumber + CONSTANTSUBGROUP_MENTORREDIRECT_HUM;
        }
    }
    else {
        sMentorURL = PARAMFILTER_MENTORREDIRECT_HUM;
    }
    return sMentorURL;
}

const hssBaseEncode = (sEndpoint) => {
    let url1;
    if (sEndpoint && sEndpoint.includes(TARGET_MENTORREDIRECT_HUM)) {
        url1 = sEndpoint.split(TARGET_MENTORREDIRECT_HUM);
        sEndpoint = url1[0] + TARGET_MENTORREDIRECT_HUM + btoa(url1[1]);
    }
    return sEndpoint;
}
export { openMentor }