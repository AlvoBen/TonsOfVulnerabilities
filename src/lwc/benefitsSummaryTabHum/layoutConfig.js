export const BenefitSummaryModel = [[
    {
    label : 'Member Id',
    helpText : false,
    source : 'api',
    sourceObject : 'memberPlanData',
    relationshipData : false,
    mappingField : 'Name',
    value : '',
    class : ''
},{
    label : 'Benefits Period',
    helpText : true,
    helpTextObject : 'labels',
    helpTextMappingFiled: 'BenefitsPeriod',
    helpTextValue : '',
    source : 'api',
    sourceObject : 'packageinfo',
    mappingField : 'AccumulationPeriod',
    value : ''
},{
    label : 'Coverage Type',
    helpText : false,
    source : 'mbe',
    sourceObject : 'MBEData',
    mappingField : 'CoverageType',
    value : ''
},{
    label : 'Benefit Package ID',
    helpText : false,
    source : 'api',
    sourceObject : 'memberPlanData',
    relationshipData : true,
    relationProperty : 'Policy__r',
    mappingField : 'Benefit_Coverage__c',
    value : ''
},{
    label : 'Plan Options',
    helpText : false,
    source : 'api',
    sourceObject : 'planOption',
    mappingField : 'planOption',
    value : ''
}],[{
    label : 'HDHP',
    helpText : false,
    source : 'api',
    sourceObject : 'hdhp',
    mappingField : 'hdhp',
    value : ''
},{
    label : 'Deductible Type',
    helpText : false,
    source : 'api',
    sourceObject : 'deductible',
    mappingField : 'hdhp',
    value : ''
},{
    label : 'Out of Area Indicator',
    helpText : false,
    source : 'mbe',
    sourceObject : 'MBEData',
    mappingField : 'OutOfAreaIndicator',
    value : ''
},{
    label : 'Paid Thru Date',
    helpText : false,
    source : 'billing',
    sourceObject : 'BillingData',
    mappingField : 'PaidThroughDate',
    value : ''
},{
    label : 'Certificate',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'Certificate',
    value : ''
}],[{
    label : 'COBRA',
    helpText : false,
    source : '',
    sourceObject : 'GBEData',
    mappingField : 'MHVenderCode',
    value : 'False'
},{
    label : 'MH Vendor Code',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'MHVenderCode',
    value : ''
},{
    label : 'Retired',
    helpText : false,
    source : 'mbe',
    sourceObject : 'MBEData',
    mappingField : 'Retired',
    value : ''
},{
    label : 'Network Description Status',
    helpText : false,
    source : 'mbe',
    sourceObject : 'MBEData',
    mappingField : 'NetworkDesc',
    value : ''
},{
    label : 'Max Dependent Age',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'MaxDependentAge',
    value : ''
}],[{
    label : 'Max Student Age',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'MaxStudentAge',
    value : ''
},{
    label : 'Market',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'Market',
    value : ''
},{
    label : 'Selling Ledger Number',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'SellingLedgerNumber',
    value : ''
},{
    label : 'Selling Ledger Description',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'SellingLedgerDescription',
    value : ''
},{
    label : 'Last Renewal Date',
    helpText : false,
    source : 'gbe',
    sourceObject : 'GBEData',
    mappingField : 'LastRenewalDate',
    value : ''
}]];

export function getBenefitLayout(name) {
    if(name === 'summary'){
        return BenefitSummaryModel;
    }
}