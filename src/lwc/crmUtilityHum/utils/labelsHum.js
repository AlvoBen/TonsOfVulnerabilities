/*******************************************************************************************************************************
// Deprecated : This file is deprecated and it will be removed in the coming sprints. 
//Please use customLabelsHum to add new labels.

LWC JS Name : labelsHum.js
Function    : Labels utility

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Mohan kumar N                                   11/02/2020                 Labels utility
* Joel George									  03/05/2021				 Verify Demographics label changes
* Joel George									  03/01/2021				 Chat transcript
* Ashish Kumar                                    03/01/2021                 Added Label HUM_UserAssociated_No_Results
* Joel George									  03/05/2021				 Verify Demographics
* Ritik Agarwal									  03/07/2021				 added a labels for legacyHistory table
* Saikumar Boga                                   06/14/2021                 Changes Related to Unknown Provider Form
* Saikumar Boga                                   07/13/2021                   Chnages for Unknown Agent/Broker Form
* Kajal Namdev                                    08/16/2021                 US-2306063 Group account label update
* Ankima Srivastava                               09/30/2021                 US-2507939 No Eligibility Records Label added
* Vardhman Jain                                   10/21/2021                  US:2098890- Account Management - Group Account - Launch Member Search 
* Krishna Teja Samudrala                          10/21/2021                  US:3114073- Interaction log component related labels
* Santhi Mandava                                  07/29/2022                  US:3268999- Case templates related labels
* Ajay Chakradhar                                 09/17/2023                 US:4874911 - Lightning - Mentor Documents
*********************************************************************************************************************************/
import noPidRecordsFound from "@salesforce/label/c.memberSearchPidNoResult";
import memberSearchAtleastOneHum from '@salesforce/label/c.memberSearchAtleastOneHum';
import memberSearchCriteriaHum from '@salesforce/label/c.memberSearchCriteriaHum';
import memberSearchLastNameHum from '@salesforce/label/c.memberSearchLastNameHum';
import memberSearchSpecialCharLastNameHum from '@salesforce/label/c.memberSearchSpecialCharLastNameHum';
import memberSearchFirstNameCriteriaHum from '@salesforce/label/c.memberSearchFirstNameCriteriaHum';
import memberSearchSpecialCharFirstNameHum from '@salesforce/label/c.memberSearchSpecialCharFirstNameHum';
import memberSearchPhoneEnterHum from '@salesforce/label/c.memberSearchPhoneEnterHum';
import memberSearchZipEnterHum from '@salesforce/label/c.memberSearchZipEnterHum';
import memberSearchZipCodeCriteriaHum from '@salesforce/label/c.memberSearchZipCodeCriteriaHum';
import memberSearchIDCriteriaHum from '@salesforce/label/c.memberSearchIDCriteriaHum';
import memberSearchIDAlphaCriteriaHum from '@salesforce/label/c.memberSearchIDAlphaCriteriaHum';
import memberSearchGroupAlphaCriteriaHum from '@salesforce/label/c.memberSearchGroupAlphaCriteriaHum';
import memberSearchNoResultsHum from '@salesforce/label/c.memberSearchNoResultsHum';
import GroupSearchNoResultHum from "@salesforce/label/c.GroupSearchNoResultHum";
import groupSearchAtleastOneHum from "@salesforce/label/c.groupSearchAtleastOneHum";
import groupSearchNumberCriteriaHum from "@salesforce/label/c.groupSearchNumberCriteriaHum";
import groupSearchAlphaCriteriaHum from "@salesforce/label/c.groupSearchAlphaCriteriaHum";
import crmSearchError from "@salesforce/label/c.crmSearchError";
import crmToastError from "@salesforce/label/c.crmToastError";
import memberSearchPIDNotFound from '@salesforce/label/c.memberSearchPIDNotFound';
import HUM_Provider_Search_Fields_Not_Entered from '@salesforce/label/c.HUM_Provider_Search_Fields_Not_Entered';
import Hum_Specialty_Name from '@salesforce/label/c.Hum_Specialty_Name';
import HumErrorStateSearch from '@salesforce/label/c.HumErrorStateSearch';
import Hum_First_Name from '@salesforce/label/c.Hum_First_Name';
import HumProviderSearchNPILength from '@salesforce/label/c.HumProviderSearchNPILength';
import Hum_Zipcode_Name from '@salesforce/label/c.Hum_Zipcode_Name';
import memberSearchFirstNameHum from '@salesforce/label/c.memberSearchFirstNameHum';
import memberFirstNameSearchHum from '@salesforce/label/c.memberFirstNameSearchHum';
import memberSearchIDMandatoryHum from '@salesforce/label/c.memberSearchIDMandatoryHum';
import HumPhoneCharacterLimit from '@salesforce/label/c.HumPhoneCharacterLimit';
import HUMResetButtonLabel from '@salesforce/label/c.HUMResetButtonLabel';
import backToResultsHum from '@salesforce/label/c.backToResultsHum';
import HUMSearchButton from '@salesforce/label/c.HUMSearchButton';
import HUMGroupSearchTitle from '@salesforce/label/c.HUMGroupSearchTitle';
import HUMcreateUnknown from '@salesforce/label/c.HUMcreateUnknown';
import HUMGroupSearch from '@salesforce/label/c.HUMGroupSearch';
import HUMResults from '@salesforce/label/c.HUMResults';
import HUMSearchEnrollmentButtonLabel from '@salesforce/label/c.HUMSearchEnrollmentButtonLabel';
import HUMLaunchGCPCCP_UnknownMember from '@salesforce/label/c.HUMLaunchGCPCCP_UnknownMember';
import HumMemberSearch from '@salesforce/label/c.HumMemberSearch';
import HUM_Agency_Search_Fields_Not_Entered from '@salesforce/label/c.HUM_Agency_Search_Fields_Not_Entered';
import HumSearchAgencyNameValidation from '@salesforce/label/c.HumSearchAgencyNameValidation';
import agencySearch_NoResults_HUM from '@salesforce/label/c.agencySearch_NoResults_HUM';
import agentSearchCriteriaHum from '@salesforce/label/c.agentSearchCriteriaHum';
import HUMAgentBrokerRecordTypeName from '@salesforce/label/c.HUMAgentBrokerRecordTypeName';
import HUMProvider_Unknown_Create from '@salesforce/label/c.HUMProvider_Unknown_Create';
import HUMProvider_Unknown_Search from '@salesforce/label/c.HUMProvider_Unknown_Search';
import PROVIDERSEARCHTAXIDHELP_HUM from '@salesforce/label/c.HUMProvider_Unknown_Create';
import Hum_Provider_Search_help from '@salesforce/label/c.Hum_Provider_Search_help';
import Hum_Provider_Search from '@salesforce/label/c.HumProviderSearch';
import HumZipcodeCharacterLimit from '@salesforce/label/c.HumZipcodeCharacterLimit';
import HumSearchSuffixHelpText from '@salesforce/label/c.HumSearchSuffixHelpText';
import HUMLegacyDeletedMessage from '@salesforce/label/c.HUMLegacyDeletedMessage';
import searchResultsHelptext from '@salesforce/label/c.searchResultsHelptext';
import searchResultsMemberHelptext from '@salesforce/label/c.searchResultsMemberHelptext';
import searchResultsAgencyHelptext from '@salesforce/label/c.searchResultsAgencyHelptext';
import searchResultsProviderHelptext from '@salesforce/label/c.searchResultsProviderHelptext';
import memberSearchBirthdayCriteria from '@salesforce/label/c.memberSearchBirthdayCriteria';
import memberLastNameSearchHum from '@salesforce/label/c.memberLastNameSearchHum';
import HUMGroupSearchInternalButton from '@salesforce/label/c.HUMGroupSearchInternalButton';
import HUMGroupSearchUnknownGroupButton from '@salesforce/label/c.HUMGroupSearchUnknownGroupButton';
import Hum_memberSearchHelpText from '@salesforce/label/c.Hum_memberSearchHelpText';
import Hum_groupSearchHelpText from '@salesforce/label/c.Hum_groupSearchHelpText';
import memberSearchBirthdateLimit from '@salesforce/label/c.memberSearchBirthdateLimit';
import enrollmentSearchFormTitle from '@salesforce/label/c.EnrollmentSearchFormTitle';
import enrollmentSearchFormStartHelpText from '@salesforce/label/c.EnrollmentSearchFormStartHelpText';
import enrollmentSearchFormLastNameHelpText from '@salesforce/label/c.EnrollmentSearchFormLastNameHelpText';
import enrollmentSearchFormAdditonalFieldError from '@salesforce/label/c.EnrollmentSearchFormAdditonalFieldError';
import enrollmentSearchFormMedicareIDError from '@salesforce/label/c.EnrollmentSearchFormMedicareIDError';
import HumPhoneInvalidCharacter from '@salesforce/label/c.HumPhoneInvalidCharacter';
import Hum_Provider_results from '@salesforce/label/c.Hum_Provider_results';
import Hum_State_Name from '@salesforce/label/c.Hum_State_Name';
import HumProviderSearchTaxIDLength from '@salesforce/label/c.HumProviderSearchTaxIDLength';
import HumProviderOnlyFirstNameMsg from '@salesforce/label/c.HumProviderOnlyFirstNameMsg';
import HumProviderOnlyLastNameMsg from '@salesforce/label/c.HumProviderOnlyFirstNameMsg';
import Hum_Provider_TaxHelp from '@salesforce/label/c.Hum_Provider_TaxHelp';
import HUMViewAll from '@salesforce/label/c.HUMViewAll';
import CASE_HISTORY_POPUP from '@salesforce/label/c.CASE_HISTORY_POPUP';
import LinkCasesHelpHover_HUM from '@salesforce/label/c.LinkCasesHelpHover_HUM';
import memberDateFormatHum from '@salesforce/label/c.memberDateFormatHum';
import poaTypeHelpText from '@salesforce/label/c.poaTypeHelpText';
import AM_Search_Invalid_Name_Error from '@salesforce/label/c.AM_Search_Invalid_Name_Error';
import AM_No_Results_Found_Error from '@salesforce/label/c.AM_No_Results_Found_Error';
import Hum_NoResultsFound from '@salesforce/label/c.Hum_NoResultsFound';
import enrollmentBdayError from '@salesforce/label/c.enrollmentBdayError';
import HumStartnEndDate from '@salesforce/label/c.HumStartnEndDate';
import HumSearchEnrollmentDateValid1 from '@salesforce/label/c.HumSearchEnrollmentDateValid1';
import HUM_CaseComments_LC from '@salesforce/label/c.HUM_CaseComments_LC';
import HUM_CaseNoComments_LC from '@salesforce/label/c.HUM_CaseNoComments_LC';
import HUMNewInteraction from '@salesforce/label/c.HUMNewInteraction';
import enrollmentDateCompareMsg from '@salesforce/label/c.enrollmentDateCompareMsg';
import associatedFormsHelp from '@salesforce/label/c.associatedFormsHelp';
import AM_Medicaid_Error from '@salesforce/label/c.AM_Medicaid_Error';
import TRR_FN_LN_BD_Error from '@salesforce/label/c.TRR_FN_LN_BD_Error';
import Hum_Policy_MemberId_Help from '@salesforce/label/c.Hum_Policy_MemberId_Help';
import HUM_SSN_HelpText from '@salesforce/label/c.HUM_SSN_HelpText';
import HUM_SSN_Invalid_Message from '@salesforce/label/c.HUM_SSN_Invalid_Message';
import HUM_SSN_Invalid_Digits_Message from '@salesforce/label/c.HUM_SSN_Invalid_Digits_Message';
import HUM_Start_End_Date_Blank from '@salesforce/label/c.HUM_Start_End_Date_Blank';
import caseHistoryDateLabel from '@salesforce/label/c.caseHistoryDateLabel';
import caseHistoryDateSelect from '@salesforce/label/c.caseHistoryDateSelect';
import agentSearchStateCriteria from '@salesforce/label/c.agentSearchStateCriteria';
import HUMInqClearResults from '@salesforce/label/c.HUMInqClearResults';
import HumStatus from '@salesforce/label/c.HumStatus';
import HUMCaseProduct from '@salesforce/label/c.HUMCaseProduct';
import HUMAccoutRecordPhoneEmail from '@salesforce/label/c.AccountRecordPhoneEmail_HUM';
import HUMAccountRecordPDP from '@salesforce/label/c.AccountRecordPCP_PDP_HUM';
import memberSuffixNumericHum from '@salesforce/label/c.memberSuffixNumericHum';
import memberSuffixLimitHum from '@salesforce/label/c.memberSuffixLimitHum';
import interactionsHeadingHum from '@salesforce/label/c.interactionsHeadingHum';
import groupNumberCriteriaHum from '@salesforce/label/c.groupNumberCriteriaHum';
import AckPopUpTitle_Hum from '@salesforce/label/c.AckPopUpTitle_Hum';
import AckPupUpPassText_Hum from '@salesforce/label/c.AckPupUpPassText_Hum';
import AckPopUpQAText_HUm from '@salesforce/label/c.AckPopUpQAText_HUm';
import HUMAlertsAcknowledge from '@salesforce/label/c.HUMAlertsAcknowledge';
import HUMCancel from '@salesforce/label/c.HUMCancel';
import AckPopUpAnswerLabel_Hum from '@salesforce/label/c.AckPopUpAnswerLabel_Hum';
import AckPopUpQuestionLabel_Hum from '@salesforce/label/c.AckPopUpQuestionLabel_Hum';
import AckPopUpPassLabel_Hum from '@salesforce/label/c.AckPopUpPassLabel_Hum';
import HUMSaveBtn from '@salesforce/label/c.HUMSaveBtn';
import fieldLimitHum from '@salesforce/label/c.fieldLimitHum';
import UserPassword from '@salesforce/label/c.UserPassword';
import passwordSuccessHum from '@salesforce/label/c.passwordSuccessHum';
import passwordDeletedHum from '@salesforce/label/c.passwordDeletedHum';
import modalErrorToastHum from '@salesforce/label/c.modalErrorToastHum';
import passwordDeleteModalHum from '@salesforce/label/c.passwordDeleteModalHum';
import unsavedModalMsgHum from '@salesforce/label/c.unsavedModalMsgHum';
import HUM_UserAssociated_No_Results from '@salesforce/label/c.HUM_UserAssociated_No_Results';
import HUM_LiveChatTranscript from '@salesforce/label/c.Chat_Transcripts';
import HUM_LegacyHistoryTable from '@salesforce/label/c.LegacyHistoryTable';
import Case_History from '@salesforce/label/c.Case_History';
import Policies from '@salesforce/label/c.Policies';
import Policy from '@salesforce/label/c.Policy';
import Group_Policy from '@salesforce/label/c.Group_Policy';
import Group_Plans from '@salesforce/label/c.Group_Plans';
import Group_Case_History_Checkbox_Msg from '@salesforce/label/c.Group_Case_History_Checkbox_Msg';
import Case_History_Checkbox_Msg from '@salesforce/label/c.Case_History_Checkbox_Msg';
import inquiryDetailsHum from '@salesforce/label/c.inquiryDetailsHum';
import inquiryNotesHum from '@salesforce/label/c.inquiryNotesHum';
import inquiryAuditTrailHum from '@salesforce/label/c.inquiryAuditTrailHum';
import inquiryAttachmentsHum from '@salesforce/label/c.inquiryAttachmentsHum';
import taskListHum from '@salesforce/label/c.taskListHum';
import taskDetailsHum from '@salesforce/label/c.taskDetailsHum';
import webResponseHum from '@salesforce/label/c.webResponseHum';
import interactionTodayHum from '@salesforce/label/c.interactionTodayHum';
import interactionLastHum from '@salesforce/label/c.interactionLastHum';
import openCasesHum from '@salesforce/label/c.openCasesHum';
import HUM_TimeFrameMsge from '@salesforce/label/c.LegacyTimeFrameMsg';
import HUMSelectLabel from '@salesforce/label/c.HUMSelectLabel';
import HumNBAHelptext from '@salesforce/label/c.HumNBAHelptext';
import policyNoRecordsHum from '@salesforce/label/c.policyNoRecordsHum';
import HumSearchSuffixNumericValidation from '@salesforce/label/c.HumSearchSuffixNumericValidation';
import dropdownPlaceholderHum from '@salesforce/label/c.dropdownPlaceholderHum';
import taskNotesHum from '@salesforce/label/c.taskNotesHum';
import taskAuditTrailHum from '@salesforce/label/c.taskAuditTrailHum';
import taskAttachmentsHum from '@salesforce/label/c.taskAttachmentsHum';
import medicaid_plan_details_HUM from '@salesforce/label/c.medicaid_plan_details_HUM';
import medicaid_HUM from '@salesforce/label/c.medicaid_HUM';
import HUMAgencySearchTitle from '@salesforce/label/c.HUMAgencySearchTitle';
import HumEnrollmentSearchTitle from '@salesforce/label/c.HumEnrollmentSearchTitle';
import HumBenefitVerificationTitle from '@salesforce/label/c.HumBenefitVerificationTitle';
import currentPasswordHum from '@salesforce/label/c.currentPasswordHum';
import currentQuestionHum from '@salesforce/label/c.currentQuestionHum';
import currentAnswerHum from '@salesforce/label/c.currentAnswerHum';
import additionalPlanDetailsHUM from '@salesforce/label/c.additionalPlanDetailsHUM';
import required_msg_HUM from '@salesforce/label/c.required_msg_HUM';
import HUMUtilityCSS from '@salesforce/label/c.HUMUtilityCSS'
import HUMAgencyCCSupervisor from '@salesforce/label/c.HUMAgencyCCSupervisor';
import HumUtilityPharmacy from '@salesforce/label/c.PHARMACY_SPECIALIST_PROFILE_NAME';
import myHumana_Account from '@salesforce/label/c.myHumana_Account';
import LastLoginDate_HUM from '@salesforce/label/c.LastLoginDate_HUM';
import MyHumanaLink_Hum from '@salesforce/label/c.MyHumanaLink_Hum';
import WebEmulateLink_HUM from '@salesforce/label/c.WebEmulateLink_HUM';
import planOpenCasesHum from '@salesforce/label/c.planOpenCasesHum';
import CASE_PROVIDER_LASTNAMELENGTH_ERROR from '@salesforce/label/c.CASE_PROVIDER_LASTNAMELENGTH_ERROR';
import Hum_Unknown_Provider_Form from '@salesforce/label/c.Hum_Unknown_Provider_Form';
import Hum_Unknown_TaxNPIIDHelp from '@salesforce/label/c.Hum_Unknown_TaxNPIIDHelp';
import Hum_FirstNameError from '@salesforce/label/c.Hum_FirstNameError';
import Hum_LastNameError from '@salesforce/label/c.Hum_LastNameError';
import Hum_AccountNameError from '@salesforce/label/c.Hum_AccountNameError';
import Hum_BillingStateError from '@salesforce/label/c.Hum_BillingStateError';
import endStageRenalHum from '@salesforce/label/c.endStageRenalHum';
import lowIncomeSubsHum from '@salesforce/label/c.HUMLowIncomeSubsidy';
import longTermSupportHum from '@salesforce/label/c.longTermSupportHum';
import medicationTherapyHum from '@salesforce/label/c.MTM_ELIGIBILITY_HUM';
import specialNeedsHum from '@salesforce/label/c.HUMSpecialNeedsPlan';
import contractInfoHum from '@salesforce/label/c.contractInfoHum';
import HUMSearchEnrollmentEffDate from '@salesforce/label/c.HUMSearchEnrollmentEffDate';
import endDateHum from '@salesforce/label/c.Pharmacy_End_Date';
import levelHum from '@salesforce/label/c.levelHum';
import dualEligibleHum from '@salesforce/label/c.dualEligibleHum';
import chronicCondHum from '@salesforce/label/c.chronicCondHum';
import institutionalisedHum from '@salesforce/label/c.institutionalisedHum';
import eligibilityHum from '@salesforce/label/c.eligibilityHum';
import HUMContractId from '@salesforce/label/c.HUMContractId';
import pbpCodeHum from '@salesforce/label/c.pbpCodeHum';
import HUMSegmentCode from '@salesforce/label/c.HUMSegmentCode';
import costShareProtectionHum from '@salesforce/label/c.costShareProtectionHum';
import mtvRemarksHum from '@salesforce/label/c.mtvRemarksHum';
import HUMUnknownAgencyBrokerInfo from '@salesforce/label/c.HUMUnknownAgencyBrokerInfo';
import HumSearchLastNameCharacterValidation from '@salesforce/label/c.HumSearchLastNameCharacterValidation';
import HumUnknownAgentValidation from '@salesforce/label/c.HumUnknownAgentValidation';
import HumUnknownAgentEmailValidation from '@salesforce/label/c.HumUnknownAgentEmailValidation';
import HUMCreateUnknownAgentBroker from '@salesforce/label/c.HUMCreateUnknownAgentBroker';
import deemingPeriod_Hum from '@salesforce/label/c.deemingPeriod_Hum';
import dualStatusLevel_Hum from '@salesforce/label/c.dualStatusLevel_Hum';
import dualEligibityDetails_Hum from '@salesforce/label/c.dualEligibityDetails_Hum';
import otherInsuranceDetails_Hum from '@salesforce/label/c.otherInsuranceDetails_Hum';
import otherInsHum from '@salesforce/label/c.HUMCOBOIDisclaimer';
import dualEligHelpHum from '@salesforce/label/c.dualEligHelpHum';
import HumSearchFirstNameAlphaNumericValidation from '@salesforce/label/c.HumSearchFirstNameAlphaNumericValidation';
import HumSearchLastNameAlphaNumericValidation from '@salesforce/label/c.HumSearchLastNameAlphaNumericValidation';
import dualStatusValueHelpText_Hum from '@salesforce/label/c.dualStatusValueHelpText_Hum';
import costShareHelpText_Hum from '@salesforce/label/c.costShareHelpText_Hum';
import deemingPeriodHelpText_Hum from '@salesforce/label/c.deemingPeriodHelpText_Hum';
import vendorProgramHelpHum from '@salesforce/label/c.vendorProgramHelpHum';
import noEligibilityInfoHum from '@salesforce/label/c.NoEligibilityInfoHum';
import HUMIntNameFieldLabel from '@salesforce/label/c.HUMIntNameFieldLabel';
import HUMIntAboutFieldLabel from '@salesforce/label/c.HUMIntAboutFieldLabel';
import HUMIntOriginFieldLabel from '@salesforce/label/c.HUMIntOriginFieldLabel';
import HUMIntWithTypeFieldLabel from '@salesforce/label/c.HUMIntWithTypeFieldLabel';
import HUMIntAboutTypeFieldLabel from '@salesforce/label/c.HUMIntAboutTypeFieldLabel';
import HUMIntNumberFieldLabel from '@salesforce/label/c.HUMIntNumberFieldLabel';
import HUMIntWithFieldLabel from '@salesforce/label/c.HUMIntWithFieldLabel';
import HUMSaveNewBtn from '@salesforce/label/c.HUMSaveNewBtn';
import HUMInteractionLimitMsg from '@salesforce/label/c.HUMInteractionLimitMsg';
import UNSAVED_CHANGES_HUM from '@salesforce/label/c.UnsavedChangesMessage';
import NO_RECORDS_MESSAGE_HUM from '@salesforce/label/c.HUMNoRecords';
import NO_RECORDS_MSG_HUM from '@salesforce/label/c.HUMNo_records_to_display';
import providerCombinationError_Hum from '@salesforce/label/c.QAA_PROVIDER_COMBINATION_ERROR';
import providerFirstNameError_Hum from '@salesforce/label/c.QAA_PROVIDER_FIRSTNAME_ERROR';
import providerLastNameError_Hum from '@salesforce/label/c.QAA_PROVIDER_LASTNAME_ERROR';
import providerLastNameLengthError_Hum from '@salesforce/label/c.QAA_PROVIDER_LASTNAMELENGTH_ERROR';
import providerFacilityGroupError_Hum from '@salesforce/label/c.CASE_PROVIDER_FACILITY_ERROR';
import providerFacilityGroupLengthError_Hum from '@salesforce/label/c.CASE_PROVIDER_FACILITYLENGTH_ERROR';
import providerNPIError_Hum from '@salesforce/label/c.CASE_PROVIDER_NPI_ERROR';
import providerTaxError_Hum from '@salesforce/label/c.CASE_PROVIDER_TAXID_ERROR';
import providerZipCodeError_Hum from '@salesforce/label/c.QAA_PROVIDER_ZIPCODE_ERROR';
import providerTAXIDAndNPIIDCombinationError_Hum from '@salesforce/label/c.CASE_PROVIDER_TAXIDNPI_ERROR';
import LimitedAccessMessage from '@salesforce/label/c.LimitedAccessMessage';
import Hum_Uniqueue_ProviderSearchError from '@salesforce/label/c.Hum_Uniqueue_ProviderSearchError';

export const labels = {
	Hum_Uniqueue_ProviderSearchError,
    HUMIntNameFieldLabel,
    HUMIntWithFieldLabel,
    HUMIntAboutFieldLabel,
    HUMIntOriginFieldLabel,
    HUMIntWithTypeFieldLabel,
    HUMIntAboutTypeFieldLabel,
    HUMIntNumberFieldLabel,
    HUMSaveNewBtn,
    HUMInteractionLimitMsg,
    noEligibilityInfoHum,
    vendorProgramHelpHum,
    deemingPeriodHelpText_Hum,
    dualStatusValueHelpText_Hum,
    costShareHelpText_Hum,
    otherInsuranceDetails_Hum,
    dualEligibityDetails_Hum,
    deemingPeriod_Hum,
    dualStatusLevel_Hum,
    HUMCreateUnknownAgentBroker,
    dualEligHelpHum,
    otherInsHum,
    mtvRemarksHum,
    costShareProtectionHum,
    HUMSegmentCode,
    pbpCodeHum,
    HUMContractId,
    eligibilityHum,
    institutionalisedHum,
    chronicCondHum,
    dualEligibleHum,
    levelHum,
    endDateHum,
    HUMSearchEnrollmentEffDate,
    contractInfoHum,
    specialNeedsHum,
    medicationTherapyHum,
    longTermSupportHum,
    lowIncomeSubsHum,
    endStageRenalHum,
    planOpenCasesHum,
    HumUtilityPharmacy,
    HUMAgencyCCSupervisor,
    HUMUtilityCSS,
    required_msg_HUM,
    additionalPlanDetailsHUM,
    currentAnswerHum,
    currentQuestionHum,
    currentPasswordHum,
    medicaid_plan_details_HUM,
    medicaid_HUM,
    HumBenefitVerificationTitle,
    HumEnrollmentSearchTitle,
    HUMAgencySearchTitle,
    taskAttachmentsHum,
    taskAuditTrailHum,
    taskNotesHum,
    Policy,
    Group_Policy,
    Group_Plans,
    HumNBAHelptext,
    policyNoRecordsHum,
    HumSearchSuffixNumericValidation,
    dropdownPlaceholderHum,
    openCasesHum,
    interactionLastHum,
    interactionTodayHum,
    HUMSelectLabel,
    inquiryDetailsHum,
    inquiryNotesHum,
    inquiryAuditTrailHum,
    inquiryAttachmentsHum,
    taskListHum,
    taskDetailsHum,
    webResponseHum,
    Policies,
    Case_History,
    Case_History_Checkbox_Msg,
    Group_Case_History_Checkbox_Msg,
    unsavedModalMsgHum,
    passwordDeleteModalHum,
    modalErrorToastHum,
    passwordDeletedHum,
    passwordSuccessHum,
    UserPassword,
    fieldLimitHum,
    HUMSaveBtn,
    AckPopUpPassLabel_Hum,
    AckPopUpQuestionLabel_Hum,
    AckPopUpAnswerLabel_Hum,
    HUMCancel,
    HUMAlertsAcknowledge,
    AckPopUpQAText_HUm,
    AckPupUpPassText_Hum,
    AckPopUpTitle_Hum,
    groupNumberCriteriaHum,
    interactionsHeadingHum,
    memberSuffixLimitHum,
    memberSuffixNumericHum,
    HUMCaseProduct,
    HumStatus,
    HUMInqClearResults,
    agentSearchStateCriteria,
    caseHistoryDateSelect,
    caseHistoryDateLabel,
    HUM_Start_End_Date_Blank,
    HUM_SSN_Invalid_Digits_Message,
    HUM_SSN_Invalid_Message,
    HUM_SSN_HelpText,
    Hum_Policy_MemberId_Help,
    TRR_FN_LN_BD_Error,
    AM_Medicaid_Error,
    associatedFormsHelp,
    HUMNewInteraction,
    enrollmentDateCompareMsg,
    HumSearchEnrollmentDateValid1,
    HumStartnEndDate,
    enrollmentBdayError,
    HUM_CaseNoComments_LC,
    Hum_NoResultsFound,
    AM_No_Results_Found_Error,
    AM_Search_Invalid_Name_Error,
    memberDateFormatHum,
    CASE_HISTORY_POPUP,
    LinkCasesHelpHover_HUM,
    HUMViewAll,
    Hum_Provider_TaxHelp,
    HumProviderOnlyLastNameMsg,
    HumProviderOnlyFirstNameMsg,
    HumProviderSearchTaxIDLength,
    Hum_Provider_results,
    Hum_State_Name,
    memberSearchBirthdateLimit,
    Hum_groupSearchHelpText,
    Hum_memberSearchHelpText,
    HUMGroupSearchUnknownGroupButton,
    HUMGroupSearchInternalButton,
    memberLastNameSearchHum,
    memberSearchBirthdayCriteria,
    searchResultsHelptext,
    searchResultsMemberHelptext,
    searchResultsAgencyHelptext,
    searchResultsProviderHelptext,
    HUMLegacyDeletedMessage,
    HumSearchSuffixHelpText,
    HumZipcodeCharacterLimit,
    Hum_Provider_Search,
    Hum_Provider_Search_help,
    PROVIDERSEARCHTAXIDHELP_HUM,
    HUMProvider_Unknown_Search,
    HUMProvider_Unknown_Create,
    HumMemberSearch,
    HUMLaunchGCPCCP_UnknownMember,
    HUMSearchEnrollmentButtonLabel,
    HUMResults,
    HUMGroupSearch,
    HUMGroupSearchTitle,
    HUMcreateUnknown,
    HUMResetButtonLabel,
    HUMSearchButton,
    backToResultsHum,
    GroupSearchNoResultHum,
    groupSearchAtleastOneHum,
    groupSearchNumberCriteriaHum,
    groupSearchAlphaCriteriaHum,
    crmSearchError,
    crmToastError,
    memberSearchNoResultsHum,
    noPidRecordsFound,
    memberSearchAtleastOneHum,
    memberSearchCriteriaHum,
    memberSearchLastNameHum,
    memberSearchSpecialCharLastNameHum,
    memberSearchFirstNameCriteriaHum,
    memberSearchSpecialCharFirstNameHum,
    memberSearchPhoneEnterHum,
    memberSearchZipEnterHum,
    memberSearchZipCodeCriteriaHum,
    memberSearchIDCriteriaHum,
    memberSearchIDAlphaCriteriaHum,
    memberSearchGroupAlphaCriteriaHum,
    memberSearchPIDNotFound,
    HUM_Provider_Search_Fields_Not_Entered,
    Hum_Specialty_Name,
    HumErrorStateSearch,
    Hum_First_Name,
    HumProviderSearchNPILength,
    Hum_Zipcode_Name,
    memberSearchFirstNameHum,
    memberFirstNameSearchHum,
    memberSearchIDMandatoryHum,
    HumPhoneCharacterLimit,
    HUM_Agency_Search_Fields_Not_Entered,
    HumSearchAgencyNameValidation,
    agencySearch_NoResults_HUM,
    agentSearchCriteriaHum,
    HUMAgentBrokerRecordTypeName,
    enrollmentSearchFormTitle,
    enrollmentSearchFormStartHelpText,
    enrollmentSearchFormLastNameHelpText,
    enrollmentSearchFormAdditonalFieldError,
    enrollmentSearchFormMedicareIDError,
    HumPhoneInvalidCharacter,
    poaTypeHelpText,
    HUM_CaseComments_LC,
    HUMAccoutRecordPhoneEmail,
    HUMAccountRecordPDP,
    HUM_UserAssociated_No_Results,
    HUM_LiveChatTranscript,
    HUM_LegacyHistoryTable,
	CASE_PROVIDER_LASTNAMELENGTH_ERROR,
    Hum_Unknown_Provider_Form,
    Hum_Unknown_TaxNPIIDHelp,
    Hum_FirstNameError,
    Hum_LastNameError,
    Hum_AccountNameError,
    Hum_BillingStateError,
	HUMUnknownAgencyBrokerInfo,
    HumSearchLastNameCharacterValidation,
    HumUnknownAgentValidation,
    HumUnknownAgentEmailValidation,
	HumSearchFirstNameAlphaNumericValidation,
	HumSearchLastNameAlphaNumericValidation,
    UNSAVED_CHANGES_HUM,
    NO_RECORDS_MESSAGE_HUM,
    NO_RECORDS_MSG_HUM,
    providerCombinationError_Hum,
    providerFirstNameError_Hum,
    providerLastNameError_Hum,
    providerLastNameLengthError_Hum,
    providerFacilityGroupError_Hum,
    providerFacilityGroupLengthError_Hum,
    providerNPIError_Hum,
    providerTaxError_Hum,
    providerZipCodeError_Hum,
    providerTAXIDAndNPIIDCombinationError_Hum,
    LimitedAccessMessage,
    HUM_MessageOnFilter: 'No matching records to display',
    HUM_DateRangeMessage: 'Please enter a 24 month date range',	
    HUM_Verified_Demographics: 'Verified Demographics',	
    HUM_LVB: 'Last Verified By',	
    HUM_DSLV: 'Days Since Last Verified',	
    HUM_LVO: 'Last Verified On',	
    HUM_Verify_Demographics: 'Verify Demographics',
    HUM_OnLoad: 'onLoad',
    HUM_OnClick: 'onclickVerify',
    HUM_TimeFrameMsge,
    HUM_LegacyRefNum: 'Reference Number',
    HUM_LegacyFrom: 'From',
    HUM_LegacyTo: 'To',
    myHumana_Account,
    LastLoginDate_HUM,
    WebEmulateLink_HUM,
    MyHumanaLink_Hum,
	HUMMemberSearch:'Member Search'
}

export function getLabels() {
    return labels;
}