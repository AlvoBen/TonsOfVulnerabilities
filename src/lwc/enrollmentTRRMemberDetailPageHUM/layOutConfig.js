export function getDetailFormLayout(){
    const detail = [
        {label: 'Medicare Claims number', mapping: 'sMedicareClaim', value: ''},
        {label: 'DOB', mapping: 'sDOB', value: ''},
        {label: 'First Name', mapping: 'sFirstName', value: ''},
        {label: 'Gender', mapping: 'sGender', value: ''},
        {label: 'Middle Name', mapping: 'sMiddleInitial', value: ''},
        {label: 'County Code', mapping: 'sCountyName', value: ''},
        {label: 'Last Name', mapping: 'sLastName', value: ''},
        {label: 'State Code', mapping: 'sState', value: ''},
        {label: 'Contract', mapping: 'sContract', value: ''},
        {label: 'Effective Date', mapping: 'sEffectiveDate', value: ''},
        {label: 'PBP', mapping: 'sPBP', value: ''},
        {label: 'End Date', mapping: 'sEndDate', value: ''},
        {label: 'Segment ID', mapping: 'sSegmentNumber', value: ''},
		{label: 'Race', mapping: 'sRace', value: ''},
        {label: 'Ethnicity', mapping: 'sEthinicity', value: ''}
    ];
    return detail;
}

export function getBenifitsInfoLayout(){
    const detail =[
                    {Title:'Reply Code',gridSize:'3',
                    data:[
                    {section:'Reply Code information',label: 'Reply Code', mapping: 'sReplyCode', value: ''},
                    {section:'Reply Code information',label: 'Application Date', mapping: 'sApplicationDate', value: ''},
                    {section:'Reply Code information',label: 'UI Flag', mapping: 'sUIInitialChangeFlag', value: ''},
                    {section:'Reply Code information',label: 'TRC Short Name', mapping: 'sTRCShortName', value: ''},
                    {section:'Reply Code information',label: 'Variable', mapping: 'sVariable', value: ''},
                    {section:'Reply Code information',label: 'Processing indicator', mapping: 'sDedupIndicator', value: ''},
                    {section:'Reply Code information',label: 'Humana Received Date', mapping: 'sHumanaReceivedDate', value: ''},
                    {section:'Reply Code information',label: 'Enroll Source', mapping: 'sEnrollmentSourceCode', value: ''},
                    {section:'Reply Code information',label: 'Election Code', mapping: 'sElectionTypeCode', value: ''},
                    {section:'Reply Code information',label: 'Prior PBP', mapping: 'sPriorPBP', value: ''},
                    {section:'Reply Code information',label: 'Error Message', mapping: 'sErrorMessage', value: ''},
                    {section:'Reply Code information',label: 'Application Date Indicator', mapping: 'sApplicationDateIndicator', value: ''},
                    {section:'Reply Code information',label: 'Source ID ', mapping: 'sSourceId', value: ''},
                    {section:'Reply Code information',label: 'SEP Reason code', mapping: 'sSEPReasCode', value: ''},
                    {section:'Reply Code information',label: 'Timestamp', mapping: 'sCMSProcessingTimestamp', value: ''},
                    {section:'Reply Code information',label: 'Transaction Code', mapping: 'sTransactionCode', value: ''},
                    {section:'Reply Code information',label: 'Transaction Date', mapping: 'sTransactionDate', value: ''},
                    {section:'Reply Code information',label: 'UI Organization', mapping: 'sUIOrganization', value: ''},
                    ]},
                    {Title:'Creditable Coverage / LEP & 4Rx',gridSize:'4',
                    data:[
                    {section:'Creditable Coverage information',label: 'Creditable Cover Flag', mapping: 'sCreditableCoverageFlag', value: ''},
                    {section:'Creditable Coverage information',label: 'Cumulative No. of Uncovered Months', mapping: 'sCumulativeNumberofUncoveredMonths', value: 'asd'},
                    {section:'Creditable Coverage information',label: 'Submitted No of Uncovered Months', mapping: 'sSubmittedNumberofUncoveredMonths', value: ''},
                    {section:'Creditable Coverage information',label: 'Part D Penalty Amount', mapping: 'sPartDPenaltyAmount', value: ''},
                    {section:'Creditable Coverage information',label: 'Part D Penalty Waived', mapping: 'sPartDPenaltyWaivedAmount', value: ''},
                    {section:'Creditable Coverage information',label: 'Part D Penalty Sub Amount', mapping: 'sPartDPenaltySubAmount', value: ''},

                    {section:'4Rx Information',label: 'Part D Rx Bin', mapping: 'sPartDRxBIN', value: ''},
                    {section:'4Rx Information',label: 'Part D Rx PCN', mapping: 'sPartDRxPCN', value: ''},
                    {section:'4Rx Information',label: 'Part D Rx ID', mapping: 'sPartDRxId', value: ''},
                    {section:'4Rx Information',label: 'Part D Rx Group', mapping: 'sPartDRxGroup', value: ''},
                    ]},
                    {Title:'Extra Help & Secondary Insurance',gridSize:'4',
                    data:[
                    {section:'Extra Help information',label: 'Extra Help Prem Level', mapping: 'sLISPremiumLevel', value: ''},
                    {section:'Extra Help information',label: 'Extra Help Copay Company', mapping: 'sLISCopayCategory', value: ''},
                    {section:'Extra Help information',label: 'Extra Help Period Effective Date', mapping: 'sLISPeriodEffectiveDate', value: ''},
                    {section:'Extra Help information',label: 'Extra Help Prem amount', mapping: 'sLISPremAmount', value: ''},
                    {section:'Extra Help information',label: 'Extra Help Period End date', mapping: 'sLISPeriodEndDate', value: ''},
                    {section:'Extra Help information',label: 'Extra Help Source Code', mapping: 'sLISSourceCode', value: ''},
                    {section:'Extra Help information',label: 'Extra Help Enroller Type', mapping: 'sLISEnrolleeTypeCode', value: ''},

                    {section:'Secondary Insurance Information',label: 'Secondary Drug Flag', mapping: 'sSecondaryInsDrugFlag', value: ''},
                    {section:'Secondary Insurance Information',label: 'Secondary Rx ID', mapping: 'sSecondaryRxId', value: ''},
                    {section:'Secondary Insurance Information',label: 'Secondary Rx Group', mapping: 'sSecondaryRxGroup', value: ''},
                    {section:'Secondary Insurance Information',label: 'Secondary Ins Bin', mapping: 'sSecondaryRxBIN', value: ''},
                    {section:'Secondary Insurance Information',label: 'Secondary Ins PCN', mapping: 'sSecondaryRxPCN', value: ''},
                    ]},
                    {Title:'Other Indicators',gridSize:'4',
                    data:[
                        {section:'Others Indicators section',label: 'Employ Sub Override Flag', mapping: 'sEmployerSubsidyOverrideFlag', value: ''},
                        {section:'Others Indicators section',label: 'EGHP', mapping: 'sEGHPIndicator', value: ''},
                        {section:'Others Indicators section',label: 'Plan Assigned Trans Tracking ID', mapping: 'sPlanAssignedTransTrackingId', value: ''},
                        {section:'Others Indicators section',label: 'District Off CD', mapping: 'sDistrictOfficeCode', value: ''},
                        {section:'Others Indicators section',label: 'Part D Opt out Flag', mapping: 'sPartDOptOutFlag', value: ''},
                        {section:'Others Indicators section',label: 'Out of Area Flag', mapping: 'sOutOfAreaFlag', value: ''},
                        {section:'Others Indicators section',label: 'Preferred Language', mapping: 'sPrefLangcode', value: ''},
                        {section:'Others Indicators section',label: 'Disenrollment Reason Code', mapping: 'sDisenrollmentReasonCode', value: ''},
                        {section:'Others Indicators section',label: 'Part C  Bene Prem', mapping: 'sPartCPremiumAmount', value: ''},
                        {section:'Others Indicators section',label: 'Part D Bene Prem', mapping: 'sPartDPremiumAmount', value: ''},
                        {section:'Others Indicators section',label: 'E SRD Ind', mapping: 'sESRDIndicator', value: ''},
                        {section:'Others Indicators section',label: 'Accessible Format', mapping: 'sAccessibleFmtCode', value: ''},
                        {section:'Others Indicators section',label: 'WA Indicator', mapping: 'sWAIndicator', value: ''},
                        {section:'Others Indicators section',label: 'Entitlement Type', mapping: 'sEntitlementTypeCode', value: ''},
                        {section:'Others Indicators section',label: 'Prem With Opt Part C/D', mapping: 'sPremiumWithholdOptionCode', value: ''},
                        {section:'Others Indicators section',label: 'Disability Ind', mapping: 'sDisabilityIndicator', value: ''},
                        {section:'Others Indicators section',label: 'Hospice Ind', mapping: 'sHospiceIndicator', value: ''},
                        {section:'Others Indicators section',label: 'MSP Status Flag', mapping: 'sMSPStatusFlag', value: ''},
                        {section:'Others Indicators section',label: 'De Minimis Diff Amt', mapping: 'sDeMinimisDifferentialAmount', value: ''},
                        {section:'Others Indicators section',label: 'Prev Pt D Cont - Troop', mapping: 'sPrevTROOPPBP', value: ''},
                        {section:'Others Indicators section',label: 'Instit / NHC ind', mapping: 'sInstitutionalIndicator', value: ''},


                    ]},
                    
            ];
    return detail;
}