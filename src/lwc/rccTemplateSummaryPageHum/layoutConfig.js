/*
Additional JS        : layoutConfig
Version              : 1.0
Created On           : 07/28/2022 
Function             : Js used for data mapping on the summary page

Modification Log: 
* Developer Name                    Date                         Description
* Viswa                             07/28/2022                   Original Version
* Bhakti Vispute                    09/01/2022                   US# 3279399 RCC Templates: Medicare Good Cause Reinstatement Templates Form Summary page
* Nilanjana Sanyal                  09/02/2022                   US# 3522901 RCC Templates: Medicaid PAR Provider Not Accepting Template  - Summary Page
* Vardhman Jain                     09/23/2022                   DF-6255-Fix
* Muthukumar 						09/27/2022                   DF-6261 fix 
* Vardhman Jain                      10/20/2023                   US-5009031 update Commercial demographic
*------------------------------------------------------------------------------------------------------------------------------
*/

import {hcConstants} from "c/crmUtilityHum";
import {uConstants} from 'c/updatePlanDemographicConstants';

export function getInfoLayout(templateName){
    if(templateName === hcConstants.MEDICARE_EXPEDITED_APPEAL){
        const detail=[
            {section:'Overview',label:'Members Name',mapping:'MemberFullName',value:''},
            {section:'Overview',label:'Member ID #',mapping:'MemberId',value:''},
            {section:'Overview',label:'Members Telephone Number:',mapping:'MemberHomePhone',value:''},
            {section:'Caller (Member) Information',label:'Appeal Date',mapping:'MemberAppealDate',value:''},
            {section:'Caller (Member) Information',label:'Appeal Time',mapping:'MemberAppealTime',value:''},
            {section:'Caller (Member) Information',label:'Callers Name',mapping:'MemberCallersName',value:''},
            {section:'Caller (Member) Information',label:'Relationship To Member',mapping:'MemberRelationshipToMember',value:''},
            {section:'Caller (Member) Information',label:'Callers Telephone Number',mapping:'MemberCallersTelephoneNumber',value:''},
            {section:'Caller (Member) Information',label:'Callers Street Address',mapping:'MemberCallersStreetAddress',value:''},
            {section:'Caller (Member) Information',label:'Callers City',mapping:'MemberCallersCity',value:''},
            {section:'Caller (Member) Information',label:'Callers State',mapping:'MemberCallersState',value:''},
            {section:'Caller (Member) Information',label:'Callers Zip Code',mapping:'MemberCallersZipCode',value:''},
            {section:'Caller (Other) Information',label:'Appeal Date',mapping:'CallerAppealDate',value:''},
            {section:'Caller (Other) Information',label:'Appeal Time',mapping:'CallerAppealTime',value:''},
            {section:'Caller (Other) Information',label:'Callers Name',mapping:'CallerName',value:''},
            {section:'Caller (Other) Information',label:'Relationship To Member',mapping:'CallerRelationshipToMember',value:''},
            {section:'Caller (Other) Information',label:'Callers Telephone Number',mapping:'CallerTelephoneNumber',value:''},
            {section:'Caller (Other) Information',label:'Callers Street Address',mapping:'CallerStreetAddress',value:''},
            {section:'Caller (Other) Information',label:'Callers City',mapping:'CallerCity',value:''},
            {section:'Caller (Other) Information',label:'Callers State',mapping:'CallerState',value:''},
            {section:'Caller (Other) Information',label:'Callers Zip Code',mapping:'CallerPostalCode',value:''},
            {section:'Drug Or Service Information 1',label:'Drug Name/Dosage OR Service',mapping:'DrugDosageService',value:''},
            {section:'Drug Or Service Information 1',label:'Is the Member requesting continuation of services?',mapping:'IsContinuationofServices',value:''},
            {section:'Drug Or Service Information 1',label:'EOC or CGX Number',mapping:'EOCCCANumber',value:''},
            {section:'Drug Or Service Information 1',label:'Denial Date',mapping:'DenialDate',value:''},
            {section:'Drug Or Service Information 2',label:'Drug Name/Dosage OR Service',mapping:'DrugDosageService2',value:''},
            {section:'Drug Or Service Information 2',label:'Is the Member requesting continuation of services?',mapping:'IsContinuationofServices2',value:''},
            {section:'Drug Or Service Information 2',label:'EOC or CGX Number',mapping:'EOCCCANumber2',value:''},
            {section:'Drug Or Service Information 2',label:'Denial Date',mapping:'DenialDate2',value:''},
            {section:'Drug Or Service Information 3',label:'Drug Name/Dosage OR Service',mapping:'DrugDosageService3',value:''},
            {section:'Drug Or Service Information 3',label:'Is the Member requesting continuation of services?',mapping:'IsContinuationofServices3',value:''},
            {section:'Drug Or Service Information 3',label:'EOC or CGX Number',mapping:'EOCCCANumber3',value:''},
            {section:'Drug Or Service Information 3',label:'Denial Date',mapping:'DenialDate3',value:''},
            {section:'Drug Or Service Information 4',label:'Drug Name/Dosage OR Service',mapping:'DrugDosageService4',value:''},
            {section:'Drug Or Service Information 4',label:'Is the Member requesting continuation of services?',mapping:'IsContinuationofServices4',value:''},
            {section:'Drug Or Service Information 4',label:'EOC or CGX Number',mapping:'EOCCCANumber4',value:''},
            {section:'Drug Or Service Information 4',label:'Denial Date',mapping:'DenialDate4',value:''},
            {section:'Physician Information',label:'Physician Name',mapping:'PhysicianName',value:''},
            {section:'Physician Information',label:'Telephone Number',mapping:'PhysicianNumber',value:''},
            {section:'Physician Information',label:'Fax Number',mapping:'PhysicianFax',value:''},
            {section:'Physician Information',label:'Street Address',mapping:'PhysicianStreetAddress',value:''},
            {section:'Physician Information',label:'City',mapping:'PhysicianCity',value:''},
            {section:'Physician Information',label:'State',mapping:'PhysicianState',value:''},
            {section:'Physician Information',label:'Zip Code',mapping:'PhysicianZipCode',value:''},
            {section:'Supporting Information for the Appeal',label:'Reason for appealing the denial',mapping:'ReasonForAppealingDenial',value:''},
            {section:'Supporting Information for the Appeal',label:'Clinical reasons for disagreeing with the denial',mapping:'ClinicalReasonForDisagreeingDenial',value:''},
            {section:'Supporting Information for the Appeal',label:"Rationale as to why the standard timeframe would endanger the member’s health",mapping:'Rationale',value:''},
            {section:'Supporting Information for the Appeal',label:'Diagnosis and its meaning',mapping:'DiagnosisAndMeaning',value:''},
            {section:'Supporting Information for the Appeal',label:'Additional Clinical information',mapping:'AdditionalClinicalInformation',value:''},
            ];
        return detail;
    }
    if(templateName === hcConstants.MEDICARE_PART_D){
        const detail =[ 
            {section:'1. Drug Information',label: 'Drug Name', mapping: 'DrugName', value: ''},
            {section:'1. Drug Information',label: 'Drug Dosage', mapping: 'DrugDosage1', value: ''},
            {section:'1. Drug Information',label: 'Rx Obtained', mapping: 'RxObtained1', value: ''},
            {section:'1. Drug Information',label: 'Date/Time Opened', mapping: 'DateTimeOpened1', value: ''},
            {section:'1. Drug Information',label: 'Denial Date', mapping: 'DenialDate1', value: ''},
            {section:'1. Drug Information',label: 'Date Of Fill', mapping: 'DateOfFill1', value: ''},
            {section:'1. Drug Information',label: 'Reason for Appealing Denial', mapping: 'ReasonForAppeal1', value: ''},
            {section:'1. Drug Information',label: 'Supporting Details', mapping: 'SupportingDetail1', value: ''},

            {section:'2. Drug Information',label: 'Drug Name', mapping: 'DrugName', value: ''},
            {section:'2. Drug Information',label: 'Drug Dosage', mapping: 'DrugDosage2', value: ''},
            {section:'2. Drug Information',label: 'Rx Obtained', mapping: 'RxObtained2', value: ''},
            {section:'2. Drug Information',label: 'Date/Time Opened', mapping: 'DateTimeOpened2', value: ''},
            {section:'2. Drug Information',label: 'Denial Date', mapping: 'DenialDate2', value: ''},
            {section:'2. Drug Information',label: 'Date Of Fill', mapping: 'DateOfFill2', value: ''},
            {section:'2. Drug Information',label: 'Reason for Appealing Denial', mapping: 'ReasonForAppeal2', value: ''},
            {section:'2. Drug Information',label: 'Supporting Details', mapping: 'SupportingDetail2', value: ''},

            {section:'3. Drug Information',label: 'Drug Name', mapping: 'DrugName', value: ''},
            {section:'3. Drug Information',label: 'Drug Dosage', mapping: 'DrugDosage3', value: ''},
            {section:'3. Drug Information',label: 'Rx Obtained', mapping: 'RxObtained3', value: ''},
            {section:'3. Drug Information',label: 'Date/Time Opened', mapping: 'DateTimeOpened3', value: ''},
            {section:'3. Drug Information',label: 'Denial Date', mapping: 'DenialDate3', value: ''},
            {section:'3. Drug Information',label: 'Date Of Fill', mapping: 'DateOfFill3', value: ''},
            {section:'3. Drug Information',label: 'Reason for Appealing Denial', mapping: 'ReasonForAppeal3', value: ''},
            {section:'3. Drug Information',label: 'Supporting Details', mapping: 'SupportingDetail3', value: ''},

            {section:"Prescribing Physician's Information",label: 'Physician Name', mapping: 'PhysicianName', value: ''},
            {section:"Prescribing Physician's Information",label: 'Office contact person', mapping: 'PhysicianOfficeContactPerson', value: ''},
            {section:"Prescribing Physician's Information",label: 'Address', mapping: 'PhysicianAddress', value: ''},
            {section:"Prescribing Physician's Information",label: 'City', mapping: 'PhysicianCity', value: ''},
            {section:"Prescribing Physician's Information",label: 'State', mapping: 'PhysicianState', value: ''},
            {section:"Prescribing Physician's Information",label: 'Zip Code', mapping: 'PhysicianZipCode', value: ''},
            {section:"Prescribing Physician's Information",label: 'Telephone Number', mapping: 'PhysicianTelephoneNumber', value: ''},
            {section:"Prescribing Physician's Information",label: 'EXT:', mapping: 'PhysicianExt', value: ''},
            {section:"Prescribing Physician's Information",label: 'FAX Number', mapping: 'PhysicianFaxNumber', value: ''},
            {section:"Prescribing Physician's Information",label: 'Additional Information', mapping: 'PhysicianAdditionalInfo', value: ''},
            {section:"Expedite Request",label: "Did the member or member representative state at any time that applying the 72 hour standard review timeframe may seriously jeopardize the life or health of the enrollee or the enrollee's ability to regain maximum function", mapping: 'ExpediteRequest', value: ''},
        ];
        return detail;
    }
    
    if(templateName === hcConstants.MEDICARE_GOOD_CAUSE){
        const detail =[ 
            {section:'',label: 'Did the member term with a reason code of 012 within 60 calendar days from the term date?', mapping: 'GCRTermedPlanYesNo', value: 'Yes'},
            {section:'',label: 'Member ID', mapping: 'GoodCauseMemberId', value: ''},
            {section:'',label: 'Member DOB', mapping: 'GoodCauseDOB', value: ''},
            {section:'',label: 'CRM Case number provided to member', mapping: 'GoodCauseCaseNumber', value: ''},
            {section:'',label: 'Person speaking with', mapping: 'GCRPersonName', value: ''},
            {section:'',label: 'Relationship to member', mapping: 'GCRRelationship', value: ''},
            {section:'',label: 'Person responsible for paying premium', mapping: 'GCRPremiumPaymentPerson', value: ''},
            {section:'',label: 'Callback number of person responsible for paying premium if other than member', mapping: 'GCRCallBackNumber', value: ''},
            {section:'',label: 'Choose the reason the member or person responsible for paying premium indicates that they could not pay the premium', mapping: 'GCRPremiumNonPaymentReason', value: ''},
            {section:'',label: 'Specify the nature of the Event', mapping: 'GCREventNature', value: ''},
            {section:'',label: 'Start Date Situation Occurred', mapping: 'GCRSituationStartDate', value: ''},
            {section:'',label: 'End Date Situation Occurred', mapping: 'GCRSituationEndDate', value: ''},
            {section:'',label: 'Start Date Situation Occurred', mapping: 'GCRStartDateillness', value: ''},
            {section:'',label: 'End Date Situation Occurred', mapping: 'GCREndDateillness', value: ''},
            {section:'',label: 'Start Date Situation Occurred', mapping: 'GCRStartDateEvent', value: ''},
            {section:'',label: 'Facility Type', mapping: 'GCRFacilityType', value: ''},
            {section:'',label: 'If Other, Please Provide Facility Type', mapping: 'GCROtherFacilityType', value: ''},
            {section:'',label: 'Specify the Nature of the Illness if provided by the Member', mapping: 'GCRIllnessNature', value: ''},
            {section:'',label: 'Date of Death', mapping: 'GCRDateOfDeath', value: ''},
            {section:'',label: 'Relationship to Member', mapping: 'GCRMemberRelationOpt3', value: ''},
            {section:'',label: 'Is the Member in need of Medication and has No Account Balance?', mapping: 'GCRIsMedicationNeeded', value: ''}    
        ];
        return detail;
    }

    if(templateName === hcConstants.CREDITABLE_COVERAGE){
        const detail =[ 
            {section:'Overview',label: 'Who are you Speaking With ?', mapping: 'ATVF_PersonSpeakingWith', value: ''},
            {section:'Overview',label: 'Do you have verbal consent to move forward with the call ?', mapping: 'ATVF_OtherMemberConsentMoveForwardCall', value: ''},
            {section:'Overview',label: 'First Name', mapping: 'ATVF_FirstName', value: ''},
            {section:'Overview',label: 'Last Name', mapping: 'ATVF_LastName', value: ''},
            {section:'Overview',label: 'Address', mapping: 'ATVF_Address', value: ''},
            {section:'Overview',label: 'City', mapping: 'ATVF_City', value: ''},
            {section:'Overview',label: 'State', mapping: 'ATVF_State', value: ''},
            {section:'Overview',label: 'ZipCode', mapping: 'ATVF_ZipCode', value: ''},
            {section:'Overview',label: 'Phone Number', mapping: 'ATVF_PhoneNumber', value: ''},
            {section:'Overview',label: 'Relationship To Member', mapping: 'ATVF_RelationShipToMember', value: ''},
            {section:'Overview',label: 'Does the caller understand the definition of Creditable Coverage?', mapping: 'ATVF_POAUnderstandDefOfCridtableCoverage', value: ''},
            {section:'Overview',label: 'Has it been more than 90 days since letter ME1800, ME0996, ME0883, or ME887 was sent to the member ?', mapping: 'ATVF_90DaysLetterSent', value: ''},
            {section:'Overview',label: 'Does the caller agree to the disclaimer ?', mapping: 'ATVF_FinalDisclaimer', value: ''},
            {section:'Summary of Coverages',label: 'Never had creditable coverage', mapping: 'ATVF_NeverHadCrditableCoverage', value: ''},
            {section:'Summary of Coverages',label: 'Employer/Union Coverage, including the Federal Employees Health Benefits Program (FEHBP)', mapping: 'ATVF_FEHBPCoveragePCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_FEHBPCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_FEHBPCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Medicaid, State Pharmaceutical Assistance Program (SPAP), or other State Sponsored Coverage', mapping: 'ATVF_SPAPCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_SPAPCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_SPAPCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'VA Benefits ( veterans, survivor, or dependent benefits)', mapping: 'ATVF_VACoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_VACoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_VACoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'TRICARE or Other Military Coverage', mapping: 'ATVF_TRICARECoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_TRICARECoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_TRICARECoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Medigap (Medicare Supplement) policy with Creditable prescription drug coverage', mapping: 'ATVF_MediGapCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_MediGapCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_MediGapCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Indian Health Service, a Tribe or Tribal organization, or Urban Indian Organization (I/T/U) Coverage', mapping: 'ATVF_ITUCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_ITUCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_ITUToCoverageDate', value: ''},
            {section:'Summary of Coverages',label: 'PACE Coverage (Program of All-Inclusive Care for the Elderly)', mapping: 'ATVF_PACECoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_PACECoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_PACECoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Different Source of Coverage', mapping: 'ATVF_DiffSourceOfCoverageSourceName', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_DiffSourceOfCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_DiffSourceOfCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Humana Coverage', mapping: 'ATVF_HumanaCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_HumanaCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_HumanaCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Extra Help From Medicare to pay for prescription drug coverage', mapping: 'ATVF_ExtraHelpCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_ExtraHelpCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_ExtraHelpCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Lived in an area affected by Hurricane Katrina in August 2005 and joined a Medicare prescription drug plan before Dec. 31, 2006', mapping: 'ATVF_KatrinaCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_KatrinaCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_KatrinaCoverageToDate', value: ''},
            {section:'Summary of Coverages',label: 'Puerto Rico Reforma', mapping: 'ATVF_PuertoRicoCoverageCheck', value: ''},
            {section:'Summary of Coverages',label: 'From Date', mapping: 'ATVF_PuertoRicoCoverageFromDate', value: ''},
            {section:'Summary of Coverages',label: 'To Date', mapping: 'ATVF_PuertoRicoCoverageToDate', value: ''}
        ];
        return detail;
    }

    if(templateName === uConstants.Update_Plan_Demographics){
        const detail =[ 
            {section:'Interaction and Address Update Request',label: 'Person Speaking With', mapping: 'PersonSpeakingWith', value: ''},
            {section:'Interaction and Address Update Request',label: 'Relationship to Member', mapping: 'RelationToMember', value: ''},
            {section:'Critical Information',label: 'First Name', mapping: 'FirstName', value: ''},
            {section:'Critical Information',label: 'Middle Initial', mapping: 'MiddleInitial', value: ''},
            {section:'Critical Information',label: 'Last Name', mapping: 'LastName', value: ''},
            {section:'Critical Information',label: 'Gender', mapping: 'Gender', value: ''},
            {section:'Critical Information',label: 'Birthdate', mapping: 'DateOfBirth', value: ''},
            {section:'Critical Information',label: 'Age', mapping: 'Age', value: ''},
            {section:'Contact Information',label: 'Home Email', mapping: 'HomeEmail', value: ''},
            {section:'Contact Information',label: 'Work Email', mapping: 'WorkEmail', value: ''},
            {section:'Contact Information',label: 'Home Phone', mapping: 'HomePhone', value: ''},
            {section:'Contact Information',label: 'Mobile', mapping: 'Mobile', value: ''},
            {section:'Contact Information',label: 'Work Phone', mapping: 'WorkPhone', value: ''},
            {section:'Contact Information',label: 'Work Phone Ext', mapping: 'WorkPhoneExt', value: ''},
            {section:'Residential Address',label: 'Residential Address ', mapping: 'PermanentResidentialAddress', value: ''},
            {section:'Residential Address',label: 'Residential City', mapping: 'PermanentResidentialCityName', value: ''},
            {section:'Residential Address',label: 'Residential State', mapping: 'PermanentResidentialStateCode', value: ''},
            {section:'Residential Address',label: 'Residential County', mapping: 'PermanentResidentialCountyName', value: ''},
            {section:'Residential Address',label: 'Residential Zip Code', mapping: 'PermanentResidentialZipCode', value: ''},
            {section:'Residential Address',label: 'Effective Moving Date Address', mapping: 'PermanentResidentialStartDate', value: ''},
            {section:'Residential Address',label: 'Does the member have a mailing address different from their permanent residential address?', mapping: 'DoesMemberHaveMailAddressDiffFromPermAddress', value: ''},
            {section:'Residential Address',label: 'Does the member have a temporary address?', mapping: 'DoesResMemberHaveTemporaryAddress', value: ''},
            {section:'Residential Address',label: 'Does the member want their mail sent to their temporary address?', mapping: 'DoesMemberWantMailSentToTemporaryAddress', value: ''},
            {section:'Residential Address',label: 'Does the member want their mailing address different from their residential address?', mapping: 'DoesMemberWantMailAddressDiffFromResAddress', value: ''},
            {section:'Mailing Address',label: 'Mailing Address', mapping: 'MailingAddress', value: ''},
            {section:'Mailing Address',label: 'Mailing City', mapping: 'MailingCityName', value: ''},
            {section:'Mailing Address',label: 'Mailing State', mapping: 'MailingStateCode', value: ''},
            {section:'Mailing Address',label: 'Mailing County', mapping: 'MailingCounty', value: ''},
            {section:'Mailing Address',label: 'Mailing Zip Code', mapping: 'MailingZipCode', value: ''},
            {section:'Mailing Address',label: 'Does the member have a temporary address?', mapping: 'DoesMailMemberHaveTemporaryAddress', value: ''},
            {section:'Mailing Address',label: 'Does the member have a residential address different from their mailing address provided?', mapping: 'MailDoesMemberHaveResAddressDiffFromMailAddressingZipCode', value: ''},
            {section:'Temporary Address',label: 'Temporary Address', mapping: 'TemporaryAddress', value: ''},
            {section:'Temporary Address',label: 'Temporary City', mapping: 'TemporaryCityName', value: ''},
            {section:'Temporary Address',label: 'Temporary State', mapping: 'TemporaryStateCode', value: ''},
            {section:'Temporary Address',label: 'Temporary County', mapping: 'TemporaryCountyName', value: ''},
            {section:'Temporary Address',label: 'Temporary Zip Code', mapping: 'TemporaryZipCode', value: ''},
            {section:'Temporary Address',label: 'Temporary Absence Start Date', mapping: 'TemporaryOSAStartDate', value: ''},
            {section:'Temporary Address',label: 'Temporary Absence End Date', mapping: 'TemporaryOSAEndDate', value: ''}
            ];
        return detail;
    }
	
    if(templateName === "Update Commercial Demographics"){
        const detail =[ 
            {section:'Critical Information',label: 'First Name', mapping: 'FirstName', value: ''},
            {section:'Critical Information',label: 'Middle Initial', mapping: 'MiddleInitial', value: ''},
            {section:'Critical Information',label: 'Last Name', mapping: 'LastName', value: ''},
            {section:'Critical Information',label: 'Gender', mapping: 'Gender', value: ''},
            {section:'Critical Information',label: 'Birthdate', mapping: 'DateOfBirth', value: ''},
            {section:'Critical Information',label: 'SSN', mapping: 'SSN', value: ''},
            {section:'Contact Information',label: 'Home Email', mapping: 'HomeEmail', value: ''},
            {section:'Contact Information',label: 'Work Email', mapping: 'WorkEmail', value: ''},
            {section:'Contact Information',label: 'Home Phone', mapping: 'HomePhone', value: ''},
            {section:'Contact Information',label: 'Mobile', mapping: 'Mobile', value: ''},
            {section:'Contact Information',label: 'Work Phone', mapping: 'WorkPhone', value: ''},
            {section:'Contact Information',label: 'Work Phone Ext', mapping: 'WorkPhoneExt', value: ''},
            {section:'Residential Address',label: 'Residential Address ', mapping: 'PermanentResidentialAddress', value: ''},
            {section:'Residential Address',label: 'Residential City', mapping: 'PermanentResidentialCityName', value: ''},
            {section:'Residential Address',label: 'Residential State', mapping: 'PermanentResidentialStateCode', value: ''},
            {section:'Residential Address',label: 'Residential Zip Code', mapping: 'PermanentResidentialZipCode', value: ''},
            {section:'Mailing Address',label: 'Mailing Address', mapping: 'MailingAddress', value: ''},
            {section:'Mailing Address',label: 'Mailing City', mapping: 'MailingCityName', value: ''},
            {section:'Mailing Address',label: 'Mailing State', mapping: 'MailingStateCode', value: ''},
            {section:'Mailing Address',label: 'Mailing Zip', mapping: 'MailingZipCode', value: ''}    
        ];
        return detail;
    }


    if(templateName === hcConstants.DEAA_Member_Opt_Out){
        const detail =[
            {section :'Caller Verification Information',label: 'Is the member or valid POA on the call concerning their Humana Plan?',mapping:'DEAA_MemberOrPOAOnCall',value:''},
            {section :'Caller Verification Information',label: 'Did you get member consent to move on with the call?',mapping:'DEAA_VerbalConsent',value:''},
            {section :'Members Opt Out Information',label: 'Does the member want to remain on their MA-Only plan and Opt out of the DEAA process?',mapping:'DEAA_MemberOptOutOption',value:''},
            {section :'Members Opt Out Information',label: 'Has the member already been moved to the MAPD plan?',mapping:'DEAA_MemberAlreadyMovedToMAPD',value:''},
            {section :'Members Opt Out Information',label: 'Does the member qualify for the earlier effective date?',mapping:'DEAA_EarlyEffectiveDateEligible',value:''},
            {section :'Members Opt Out Information',label: 'Does the member wish to have an earlier effective date for their prescription coverage?',mapping:'DEAA_EarlyEffectiveDate_MemberOption',value:''},
        ];
        return detail;
    }
    
       if(templateName === hcConstants.MEDICAID_PAR_PROVIDER_NOT_ACCEPTING){
        const detail =[
            {section :'',label: 'Product Type',mapping:'Product Type',value:''},
            {section :'',label: 'Member Name',mapping:'Member Name',value:''},
            {section :'',label: 'Member ID',mapping:'Member ID',value:''},
            {section :'',label: 'Is this a Provider or Group?',mapping:'Is this a Provider or Group',value:''},
            {section :'Provider Details',label: 'Name',mapping:'Provider Name',value:''},
            {section :'Provider Details',label: 'Specialty',mapping:'Provider Specialty',value:''},
            {section :'Provider Details',label: 'Tax ID',mapping:'Provider Tax ID',value:''},
            {section :'Provider Details',label: 'NPI',mapping:'Provider NPI',value:''},
            {section :'Provider Details',label: 'Street Address',mapping:'Provider Street Address',value:''},
            {section :'Provider Details',label: 'City',mapping:'Provider City',value:''},
            {section :'Provider Details',label: 'County',mapping:'Provider County',value:''},
            {section :'Provider Details',label: 'State',mapping:'Provider State',value:''},
            {section :'Provider Details',label: 'Zip Code',mapping:'Provider Zip Code',value:''},
            {section :'Provider Details',label: 'Phone Number',mapping:'Provider Phone Number',value:''},
            {section :'Provider Details',label: 'Extension',mapping:'Provider Extension',value:''},
            {section :'Provider Details',label: 'Date of Occurrence',mapping:'Date of Occurrence',value:''},
            {section :'Provider Details',label: 'Who did you speak to at the Provider office?',mapping:'Who did you speak to at the Provider office',value:''},
            {section :'Provider Details',label: 'Description of the Issue',mapping:'Description of the Issue',value:''},
            {section :'Provider Details',label: 'Is this a behavioral health provider?',mapping:'Is this a behavioral health provider',value:''},
            {section :'Group Details',label: 'Name',mapping:'Group Name',value:''},
            {section :'Group Details',label: 'Specialty',mapping:'Group Specialty',value:''},
            {section :'Group Details',label: 'Tax ID',mapping:'Group Tax ID',value:''},
            {section :'Group Details',label: 'NPI',mapping:'Group NPI',value:''},
            {section :'Group Details',label: 'Street Address',mapping:'Group Street Address',value:''},
            {section :'Group Details',label: 'City',mapping:'Group City',value:''},
            {section :'Group Details',label: 'County',mapping:'Group County',value:''},
            {section :'Group Details',label: 'State',mapping:'Group State',value:''},
            {section :'Group Details',label: 'Zip Code',mapping:'',value:''},
            {section :'Group Details',label: 'Phone Number',mapping:'Group Phone Number',value:''},
            {section :'Group Details',label: 'Extension',mapping:'Group Extension',value:''},
            {section :'Group Details',label: 'Date of Occurrence',mapping:'Date of Occurrence',value:''},
            {section :'Group Details',label: 'Who did you speak to at the Group office?',mapping:'Who did you speak to at the Group office',value:''},
            {section :'Group Details',label: 'Description of the Issue',mapping:'Description of the Issue',value:''},
            {section :'Group Details',label: 'Is this a behavioral health group?',mapping:'Is this a behavioral health group',value:''}
            
        ];
        return detail;
    }
    

    if(templateName === hcConstants.QAA_COMPLAINT){
        const detail =[
            {section :'',label: 'Process Type',mapping:'Process Type',value:''},
            {section :'',label: 'Is the Member willing to provide their QAA Complaint details verbally?',mapping:'Is the Member willing to provide their QAA Complaint details verbally?',value:''},
            {section :'',label: 'Who is submitting the complaint?',mapping:'Who is submitting the complaint?',value:''},
            {section :'',label: 'Date of the incident/service',mapping:'Date of the incident/service',value:''},
            {section :'',label: 'Setting of the incident/service',mapping:'Setting of the incident/service',value:''},
            {section :'',label: 'Complaint Details',mapping:'Complaint Details',value:''},
            {section :'',label: 'Is any part of the complaint related to an access to care issue?',mapping:'Is any part of the complaint related to accessibility care issues?',value:''},
            {section :'Provider Details',label: 'Is this for a Provider or a Facility/Group?',mapping:'Is this for a Provider or a Facility/Group?',value:''},
            {section :'Provider Details',label: 'First Name',mapping:'Provider First Name',value:''},
            {section :'Provider Details',label: 'Last Name',mapping:'Provider Last Name',value:''},
			{section :'Provider Details',label: 'Facility/ Group Name',mapping:'Facility/ Group Name',value:''},
            {section :'Provider Details',label: 'Provider/Facility/Group Tax ID',mapping:'Provider/Facility/Group Tax ID',value:''},
            {section :'Provider Details',label: 'City',mapping:'Provider City',value:''},
            {section :'Provider Details',label: 'State',mapping:'Provider State',value:''},
            {section :'Provider Details',label: 'Provider/Facility/Group NPI',mapping:'Provider/Facility/Group NPI',value:''},
            {section :'Provider Details',label: 'Street Address',mapping:'Provider Street Address',value:''},
            {section :'Provider Details',label: 'Zip Code',mapping:'Provider Zip Code',value:''}            
        ];
        return detail;
    }
}