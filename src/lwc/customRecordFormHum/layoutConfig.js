/*******************************************************************************************************************************
LWC JS Name : layoutConfig.js
Function    : This JS serves as helper to customRecordFormHum.js. 

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya                                                 05/21/2021                   initial version
* Supriya                                                 06/04/2021                   US: 2117860 GBO additional plan details
* Supriya                                                 06/09/2021                   Added checkbox for legacy delete
* Mohan                                                   08/03/2021                  US: 2149972
* Kajal                                                   09/03/2021                   US: 2365014
* Joel							                          09/24/2021			        US 2585552
* Ankima							                      10/24/2021			        US 2581786
* Supriya Shastri				                          03/16/2021				      US-1985154
* Muthu Kumar                   						  09/02/2022                  US: 3043287 Member Plan Logging stories Changes.
* Vardhman Jain                                           20/09/2022                  DF-6218 fix
* Vardhman Jain                                           16/06/2022                  US:4525760 T1PRJ1097507- MF21369 - Consumer/CRM Service must display the Pre-Release Mbr Ind  on the Plan Member Page
* Swarnalina Laha										  30/06/2023				  US 4720802: T1PRJ0865978 - MF27252 - C01/Consumer Management/Plan Details Tab- Display "Platform" field for permission set 685
*********************************************************************************************************************************/

import CRMS_207_PBM_Iconology from '@salesforce/customPermission/CRMS_207_PBM_Iconology';
import { getUserGroup, hcConstants } from 'c/crmUtilityHum';

export function getFormLayout() {
    const { bPharmacy, bProvider, bRcc, bGeneral } = getUserGroup();
    if (CRMS_207_PBM_Iconology) {
        return addPBMDetail;
    }
    else if (bPharmacy || bRcc || bGeneral) {
        return addDetail;
    }
    else if (bProvider) {
        return providerDetail;
    }
    else {
        return;
    }
}

export function getGboForm(product) {
    switch (product) {
        case hcConstants.MED_POLICY:
            return gboMedDetail;
            break;
        case hcConstants.DEN_POLICY:
            return gboDenDetail;
            break;
        default:
            return gboGeneralDetail;
            break;
    }
}

export function getGboMESTypeForm(){
    return addDetail;
}

const addDetail = [
    { label: 'Product', mapping: 'Product__c', value: '', wrapper: '' },
    { label: 'Product Description', mapping: 'Product_Description__c', value: '', wrapper: '' },
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c', value: '', wrapper: '',  checkbox: 'true', donotlog: true },
    { label: 'Plan Name', mapping: 'iab_description__c', value: '', wrapper: 'Plan' },
    { label: 'Plan', mapping: 'Name', value: '', wrapper: 'Plan' },
    { label: 'Platform', mapping: 'Policy_Platform__c', value: '', wrapper: '' },
    { label: 'Market', mapping: 'SourceSystem', value: '', wrapper: 'Plan' },
    { label: 'Market Number', mapping: 'Selling_Market_Number__c', value: '', wrapper: 'Plan' },
    { label: 'Group Name', mapping: 'Display_Group_Name__c', value: '', wrapper: '', copyToClipBoard: 'true' },
    { label: 'Group Number', mapping: 'GroupNumber', value: '', wrapper: '', copyToClipBoard: 'true' },
    { label: 'Dual Status 12-Mo Flag', action:"dual-status", mapping: '', value: '', wrapper: '', hasHelp: 'true', hide: 'false' },
    { label: 'Issue State', mapping: 'Issue_State__c', value: '', wrapper: '' },
    { label: 'Other Insurance', action:"other-insurance", mapping: '', value: 'No', wrapper: '', hasHelp: 'true', hide: 'false'},
    { label: 'Benefit Package ID', mapping: 'Benefit_Coverage__c', value: '', wrapper: 'Plan', copyToClipBoard: 'true' },
	{ label: 'Pre-Release Member', mapping: 'BSN', value: '', wrapper: 'gbe',hide: false }
];

const addPBMDetail = [
    { label: 'Relationship', mapping: 'RelationshipToSubscriber', value: '', wrapper: '' },
    { label: 'Subscriber', mapping: 'Name', value: '', wrapper: 'SubscriberPlanId__r' },
    { label: 'Plan', mapping: 'Name', value: '', wrapper: 'Plan' },
    { label: 'Platform', mapping: 'Policy_Platform__c', value: '', wrapper: '' },
    { label: 'Product', mapping: 'Product__c', value: '', wrapper: '' },
    { label: 'Product Type Code', mapping: 'Product_Type_Code__c', value: '', wrapper: '' },
    { label: 'Other Insurance', action:"other-insurance", mapping: '', value: 'No', wrapper: '', hasHelp: 'true', hide: 'false' },
    { label: 'Issue State', mapping: 'Issue_State__c', value: '', wrapper: '' },
    { label: 'Plan name', mapping: 'iab_description__c', value: '', wrapper: 'Plan' },
    { label: 'Exchange', mapping: 'Exchange__c', value: '', wrapper: '' },
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c', value: '', wrapper: '',  checkbox: 'true', donotlog: true },
    { label: 'Benefit Package ID', mapping: 'Benefit_Coverage__c', value: '', wrapper: 'Plan', copyToClipBoard: 'true' },
    { label: 'Group Name', mapping: 'Display_Group_Name__c', value: '', wrapper: '', copyToClipBoard: 'true' },
	{ label: 'Pre-Release Member', mapping: 'BSN', value: '', wrapper: 'gbe',hide: false },
    { label: 'Group Number', mapping: 'GroupNumber', value: '', wrapper: '', copyToClipBoard: 'true' },
    { label: 'Market', mapping: 'SourceSystem', value: '', wrapper: 'Plan' },
    { label: 'Market Number', mapping: 'Selling_Market_Number__c', value: '', wrapper: 'Plan' },
    { label: 'Dual Status 12-Mo Flag', action:"dual-status", mapping: '', value: '', wrapper: '', hasHelp: 'true', hide: 'false' },
    { label: 'ASO Indicator', mapping: 'ASO__c', value: '', wrapper: '' }
];

const providerDetail = [
    { label: 'Product Description', mapping: 'Product_Description__c', value: '', wrapper: '' },
    { label: 'Plan', mapping: 'Name', value: '', wrapper: 'Plan' },
    { label: 'Group Name', mapping: 'GroupNumber', value: '', wrapper: '', copyToClipBoard: 'true' },
    { label: 'Dual Status 12-Mo Flag', action:"dual-status", mapping: '', value: '', wrapper: '', hasHelp: 'true', hide: 'false' },
    { label: 'Market', mapping: 'SourceSystem', value: '', wrapper: 'Plan' },
    { label: 'Issue State', mapping: 'Issue_State__c', value: '', wrapper: '' },
    { label: 'EHB Term Date', mapping: 'EHB_Term_Date__c', value: '', wrapper: '' },
    { label: 'Relationship', mapping: 'RelationshipToSubscriber', value: '', wrapper: '' },
    { label: 'ASO', mapping: 'ASO__c', value: '', wrapper: ''},
    { label: 'Product Type Code', mapping: 'Product_Type_Code__c', value: '', wrapper: '' },
    { label: 'Benefit Package ID', mapping: 'Benefit_Coverage__c', value: '', wrapper: 'Plan', copyToClipBoard: 'true' },
    { label: 'Market Number', mapping: 'Selling_Market_Number__c', value: '', wrapper: 'Plan' },
    { label: 'Other Insurance', action:"other-insurance", mapping: '', value: 'No', wrapper: '', hasHelp: 'true', hide: 'false' },
    { label: 'Pre-Release Member', mapping: 'BSN', value: '', wrapper: 'gbe',hide: false },
    { label: 'Platform', mapping: 'Policy_Platform__c', value: '', wrapper: '' }
];

const gboGeneralDetail = [
    { label: 'Status', mapping: 'Member_Coverage_Status__c', value: '', wrapper: '' },
    { label: 'Benefit Package ID', mapping: 'Benefit_Coverage__c', value: '', wrapper: 'Plan', copyToClipBoard: 'true' },
    { label: 'Product', mapping: 'Product__c', value: '', wrapper: '' },
	{ label: 'Pre-Release Member', mapping: 'BSN', value: '', wrapper: 'gbe',hide: false },
    { label: 'Product Type', mapping: 'Product_Type__c', value: '', wrapper: '' },
    { label: 'Relationship', mapping: 'RelationshipToSubscriber', value: '', wrapper: '' },
    { label: 'Subscriber', mapping: 'Name', value: '', wrapper: 'SubscriberPlanId__r' },
    { label: 'Platform', mapping: 'Policy_Platform__c', value: '', wrapper: '' },
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c', value: '', wrapper: '',  checkbox: 'true', donotlog: true },
    { label: 'Other Insurance', action:"other-insurance", mapping: '', value: 'No', wrapper: '', hasHelp: 'true', hide: 'false' },
    { label: 'PEO Indicator', mapping: 'peoIndicator', value: '', wrapper: 'gbe', hide: 'false' },
    { label: 'EDI', mapping: 'EDI', value: '', wrapper: 'edi', hide: 'false' } 
];

const gboDenDetail = [
    { label: 'Status', mapping: 'Member_Coverage_Status__c', value: '', wrapper: '' },
    { label: 'Benefit Package ID', mapping: 'Benefit_Coverage__c', value: '', wrapper: 'Plan', copyToClipBoard: 'true' },
    { label: 'Product', mapping: 'Product__c', value: '', wrapper: '' },
	{ label: 'Pre-Release Member', mapping: 'BSN', value: '', wrapper: 'gbe',hide: false },
    { label: 'Product Type', mapping: 'Product_Type__c', value: '', wrapper: '' },
    { label: 'Relationship', mapping: 'RelationshipToSubscriber', value: '', wrapper: '' },
    { label: 'Subscriber', mapping: 'Name', value: '', wrapper: 'SubscriberPlanId__r' },
    { label: 'Platform', mapping: 'Policy_Platform__c', value: '', wrapper: '' },
    { label: 'EHB Term Date', mapping: 'EHB_Term_Date__c', value: '', wrapper: '' },
    { label: 'PEO Indicator', mapping: 'peoIndicator', value: '', wrapper: 'gbe', hide: 'false' },
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c', value: '', wrapper: '',  checkbox: 'true', donotlog: true },
    { label: 'Exchange', mapping: 'Exchange__c', value: '', wrapper: '' },
    { label: 'Exchange Type', mapping: 'Exchange_Type__c', value: '', wrapper: '' },
    { label: 'EDI', mapping: 'EDI', value: '', wrapper: 'edi', hide: 'false' },
    { label: 'Other Insurance', action:"other-insurance", mapping: '', value: 'No', wrapper: '', hasHelp: 'true', hide: 'false' }
];

const gboMedDetail = [
    { label: 'Status', mapping: 'Member_Coverage_Status__c', value: '', wrapper: '' },
    { label: 'Benefit Package ID', mapping: 'Benefit_Coverage__c', value: '', wrapper: 'Plan', copyToClipBoard: 'true' },
    { label: 'Product', mapping: 'Product__c', value: '', wrapper: '' },
	{ label: 'Pre-Release Member', mapping: 'BSN', value: '', wrapper: 'gbe',hide: false },
    { label: 'Product Type', mapping: 'Product_Type__c', value: '', wrapper: '' },
    { label: 'Relationship', mapping: 'RelationshipToSubscriber', value: '', wrapper: '' },
    { label: 'Subscriber', mapping: 'Name', value: '', wrapper: 'SubscriberPlanId__r' },
    { label: 'Platform', mapping: 'Policy_Platform__c', value: '', wrapper: '' },
    { label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c', value: '', wrapper: '',  checkbox: 'true', donotlog: true},
    { label: 'PEO Indicator', mapping: 'peoIndicator', value: '', wrapper: 'gbe', hide: 'false' },
    { label: 'Metallic Tier', mapping: 'Metallic_Tier__c', value: '', wrapper: '' },
    { label: 'Exchange', mapping: 'Exchange__c', value: '', wrapper: '' },
    { label: 'Exchange Type', mapping: 'Exchange_Type__c', value: '', wrapper: '' },
    { label: 'EDI', mapping: 'EDI', value: '', wrapper: 'edi', hide: 'false' },
    { label: 'Other Insurance', action:"other-insurance", mapping: '', value: 'No', wrapper: '', hasHelp: 'true', hide: 'false' }
   
];

export const ediForm = [
    { label: 'Humana Can Change', value: '' },
    { label: 'Group Update Frequency', value: '' },
    { label: 'Frequency Details', value: '' }
];
export const additionalDateForm = [
    { label: 'Last Modified Date', value: '' }
   
];
export const validProdType = ["MES","MEF", "MGS", "MEP", "MPD","MER", "MRO",  "MRP","MGF", "MSL", "MGO", "MSP","MGP", "RSK", "MGR", "SUP"];
//changes for memPlanDetailPage start - muthu 
export function getMemPlanLayout(secTitle){
    const {MEMBER_PLAN_INFORMATION, POLICY_DETAILS,MEMBER_IDS} = hcConstants;//muthu 9/12  | added "MEMBER_IDS"
    if(secTitle==MEMBER_PLAN_INFORMATION){
        return memPlanDetail;
    }
    else if(secTitle==POLICY_DETAILS){
        return policyDetail;
    }

    else if(secTitle==MEMBER_IDS){
        return memberIdDetail;
    }
    else{
        return memPlanDetailCCS;
    }
}
const memPlanDetail =[
    {label: 'Policy', mapping: 'Policy__r', wrapper: '0', action: 'PolicyTab'},
    {label: 'Effective From', mapping: 'EffectiveFrom', wrapper: '0'},
    {label: 'Member ID', mapping: 'Name', wrapper: '0'},
    {label: 'Effective To', mapping: 'EffectiveTo', wrapper: '0'},
    {label: 'Member', mapping: 'Member', wrapper: '0', action: 'MemberTab', link: true},
    {label: 'Member Coverage Status', mapping: 'Member_Coverage_Status__c', wrapper: '0', hasHelp: 'true'},
    {label: 'Relationship to Subscriber', mapping: 'RelationshipToSubscriber', wrapper: '0'},
    {label: 'Term Reason Code', mapping: 'Term_Reason_Code__c', wrapper: '0'},
    {label: 'Subscriber', mapping: 'Subscriber', wrapper: '0', action: 'SubscriberTab',link: true},
    {label: 'EHB Term Date', mapping: 'EHB_Term_Date__c', wrapper: '0'},
    {label: 'Source System Identifier', mapping: 'SourceSystemIdentifier', wrapper: '0'},
    {label: 'Legacy Delete', mapping: 'ETL_Record_Deleted__c', wrapper: '0', checkbox: 'true', donotlog: true}
];
const policyDetail =[
    {label: 'Product',mapping: 'Product__c', wrapper: '0'},
    {label: 'Policy Platform',mapping: 'Policy_Platform__c', wrapper: '0'},
    {label: 'Product Type',mapping: 'Product_Type__c', wrapper: '0'},
    {label: 'Segment Indicator',mapping: 'Segment_Indicator__c', wrapper: '0'},
    {label: 'Product Type Code',mapping: 'Product_Type_Code__c', wrapper: '0'},
    {label: 'Issue State',mapping: 'Issue_State__c', wrapper: '0'},
    {label: 'Plan',mapping: 'Plan', wrapper: '0', action: 'PlanTab', link: true},
    {label: 'Exchange',mapping: 'Exchange__c', wrapper: '0'},
    {label: 'Product Description',mapping: 'Product_Description__c', wrapper: '0'},
    {label: 'Exchange Type',mapping: 'Exchange_Type__c', wrapper: '0'},
    {label: 'Alternative Description',mapping: 'Alternate_Description__c', wrapper: '0'},
    {label: 'Metallic Tier',mapping: 'Metallic_Tier__c', wrapper: '0'},
    {label: 'Display Group Name',mapping: 'Display_Group_Name__c', wrapper: '0'},
    {label: 'ASO',mapping: 'ASO__c', wrapper: '0'},
    {label: 'Group Number',mapping: 'GroupNumber', wrapper: '0'},
    {label: 'Dual Status Indicator',mapping: 'Dual_Status_Indicator__c', wrapper: '0'}
];
const memberIdDetail =[
    {label:'Medicaid Id',mapping: 'Medicaid_Id__c', wrapper: '0', hasHelp: 'true'},
    {label:'Vitality-Entity-ID',mapping: 'Vitality_Entity_ID__c', wrapper: '0'},
    {label:'Member-Id-Base',mapping: 'Member_Id_Base__c', wrapper: '0'},
    {label:'Member Dependent Code',mapping: 'Member_Dependent_Code__c', wrapper: '0'}
];
const memPlanDetailCCS =[
    {label: 'Member ID', mapping: 'Name', wrapper: '0'},
    {label: 'Alternative Description', mapping: 'Alternate_Description__c', wrapper: '0'},
    {label: 'Segment Indicator', mapping: 'Segment_Indicator__c', wrapper: '0'},
    {label: 'Term Reason Code', mapping: 'Term_Reason_Code__c', wrapper: '0'},
    {label: 'Term Description', mapping: 'Term_Reason_Description__c', wrapper: '0'}
];