/*
LWC Name: genericMemberPlanDetails.html
Function: Generic Component for getting member plan details.
Modification Log:
* Developer Name                  Date                         Description
* Nirmal Garg                  03 / 14 / 2023                 		Initial Version
* Nirmal Garg                    						 07/18/2022                user story 4861950, 4861945
* Atul Patil                    						 07/28/2023                user story 4861950, 4861945
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import getPlanDetails from '@salesforce/apex/GenericMemberPlanDetails_LC_HUM.getMemberPlanDetails';
import getPolicyList from '@salesforce/apex/MemberSearchActiveFuturePolicies_LC_HUM.determinePolicyAccess';
import { getLocaleDate, sortTable } from 'c/crmUtilityHum';

export function getMemberPlanDetails(memberPlanId){
    return new Promise((resolve,reject)=>{
        getPlanDetails({memberplanid : memberPlanId})
        .then(result =>{
            resolve(result);
        }).catch(error =>{
            reject(error);
        })
    })
}

export function getAllMemberPlans(accountId) {
    return new Promise((resolve, reject) => {
        getPolicyList({ sAccId: accountId })
            .then(result => {
                if (result) {
                    let policyrecords = JSON.parse(JSON.stringify(result));
                    policyrecords.forEach(function (item) {
                        item.EffectiveFrom = getLocaleDate(item.EffectiveFrom);
                        item.EffectiveTo = getLocaleDate(item.EffectiveTo);
                        item.PlanName = (item.Plan && item.Plan.iab_description__c) ? item.Plan.iab_description__c : '';
                        item.MedicareId__c = item.Member.MedicareId__c;
                        item.Name = (item.Member_Id_Base__c && item.Member_Dependent_Code__c) ? (item.Member_Id_Base__c + '-' + item.Member_Dependent_Code__c) : item.Name;
                        item.Product_Type__c = item.Product_Type__c
                    });
                    policyrecords = sortTable(policyrecords, 'Member_Coverage_Status__c', 'Product__c');
                    resolve(policyrecords)
                }
            })
            .catch(error => {
                console.log("Error Occured", error);
                reject(error);
            });
    });
}