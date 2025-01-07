/*******************************************************************************************************************************
LWC JS Name : labelsHum.js
Function    : Labels utility

Modification Log: 
Developer Name           Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Jasmeen Shangari                              03/10/2021                 Labels utility
* Prudhvi Pamarthi                              05/22/2021                 US#1800508
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
import HumNBAHelptext from '@salesforce/label/c.HumNBAHelptext';
import HUMAccoutRecordPhoneEmail from '@salesforce/label/c.AccountRecordPhoneEmail_HUM';
import HUMAccountRecordPDP from '@salesforce/label/c.AccountRecordPCP_PDP_HUM';
import policyNoRecordsHum from '@salesforce/label/c.policyNoRecordsHum';
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
import currentPasswordHum from '@salesforce/label/c.currentPasswordHum';
import currentQuestionHum from '@salesforce/label/c.currentQuestionHum';
import currentAnswerHum from '@salesforce/label/c.currentAnswerHum';
import viewAlertHum from '@salesforce/label/c.viewAlertHum';

export function getLabels() {
    let label = {
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
        currentAnswerHum,
        currentQuestionHum,
        currentPasswordHum,
        viewAlertHum,
        groupNumberCriteriaHum,
        interactionsHeadingHum,
        policyNoRecordsHum,
        HumNBAHelptext,
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
        HUM_UserAssociated_No_Results
    }
    return label;
}