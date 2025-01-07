import caseUnsavedChangesMsg from '@salesforce/label/c.unsavedModalMsgHum';
import HUMNewCase from '@salesforce/label/c.HUMNewCase';
import HUMRelatedAccounts from '@salesforce/label/c.Related_Accounts';
import HUMSystemInformation from '@salesforce/label/c.HUMSystemInfo';
import HUMPharmacyLogCode from '@salesforce/label/c.HUMPharmacyLogCode';
import LogCode from '@salesforce/label/c.LogCode';
import HumComments from '@salesforce/label/c.HumComments';
import HumCommentsLimit from '@salesforce/label/c.HumCommentsLimit';
import HUMCaseComments from '@salesforce/label/c.HUMCaseComments';
import HUMNewComment from '@salesforce/label/c.HUMNewComment';
import HumUtilityPharmacy from '@salesforce/label/c.PHARMACY_SPECIALIST_PROFILE_NAME';
import HUMCommentSaveToast from '@salesforce/label/c.HUMCommentSaveToast';
import HUMCommentDeleteToast from '@salesforce/label/c.HUMCommentDeleteToast';
import CIError_ChangeCase_HUM from '@salesforce/label/c.CIError_ChangeCase_HUM';
import HUMCaseTransferError from '@salesforce/label/c.HUMCaseTransferError';
import HUMSelectInteractionWith from '@salesforce/label/c.HUMSelectInteractionWith';
import CHANGECASEOWNER_CASETRANSFERASSSIT_INFO_HUM from '@salesforce/label/c.CHANGECASEOWNER_CASETRANSFERASSSIT_INFO_HUM';
import Case_Transfer_Service_Flag from '@salesforce/label/c.Case_Transfer_Service_Flag';
import HumCaseCloseOwnerError from '@salesforce/label/c.HumCaseCloseOwnerError';
import HumCaseCloseWorkTaskError from '@salesforce/label/c.HumCaseCloseWorkTaskError';
import HUMDissatisfyDisagreeMsg from '@salesforce/label/c.QUICKSTART_DISSATISFY_AND_DISAGREES_HUM';
import HUMDissatisfyMsg from '@salesforce/label/c.QUICKSTART_CALLER_DISSATISFY_HUM';
import HUMGandareasonMsg from '@salesforce/label/c.QUICKSTART_GANDAREASON_HUM';
import HUMDisagreeMsg from '@salesforce/label/c.QUICKSTART_CALLER_DISAGREES_HUM';
import HUMCompReasnCompTypeMsg from '@salesforce/label/c.QUICKSTART_COMPLAINTREASON_AND_COMPLAINTTYPE_HUM';
import HUMCompTypeMsg from '@salesforce/label/c.QUICKSTART_COMPLAINT_TYPE_HUM';
import HUMCompReasnMsg from '@salesforce/label/c.QUICKSTART_COMPLAINTREASON_MISSING_HUM';
import HUMMedicarePartcComplaintMsg from '@salesforce/label/c.MedicarePartCPartD_Complaint_Validation_HUM';
import Qs_CICERROR_Hum from '@salesforce/label/c.Qs_CICERROR_Hum'
import Qs_CCValidation_Hum from '@salesforce/label/c.Qs_CCValidation_Hum'
import Qs_placeholder_CEO_Hum from '@salesforce/label/c.Qs_placeholder_CEO_Hum'
import Qs_placeholder_AT_Hum from '@salesforce/label/c.Qs_placeholder_AT_Hum'
import Qs_placeholder_AR_Hum from '@salesforce/label/c.Qs_placeholder_AR_Hum'
import Complaint_must_be_selected_No from '@salesforce/label/c.Complaint_must_be_selected_No'
import Complaint_Cant_be_marked_Medicaid_PAR_Provider from '@salesforce/label/c.Complaint_Cant_be_marked_Medicaid_PAR_Provider'
import AutoRouteToastMessage from '@salesforce/label/c.AutoRouteToastMessage';
import EditAutoRouteToastMessage from '@salesforce/label/c.EditAutoRouteToastMessage';

const caseLabels = {
    HumUtilityPharmacy,
    caseUnsavedChangesMsg,
    HUMNewCase,
    HUMRelatedAccounts,
    HUMSystemInformation,
    HUMPharmacyLogCode,
    LogCode,
    HumComments,
    HumCommentsLimit,
    HUMCaseComments,
    HUMNewComment,
    HUMCommentSaveToast,
    HUMCommentDeleteToast,
    CIError_ChangeCase_HUM,
    HUMCaseTransferError,
    HUMSelectInteractionWith,
    CHANGECASEOWNER_CASETRANSFERASSSIT_INFO_HUM,
    Case_Transfer_Service_Flag,
    HumCaseCloseOwnerError,
   HumCaseCloseWorkTaskError,
    HUMDissatisfyDisagreeMsg,
    HUMDissatisfyMsg,
    HUMGandareasonMsg,
    HUMDisagreeMsg,
    HUMCompReasnCompTypeMsg,
    HUMCompTypeMsg,
    HUMCompReasnMsg,
    HUMMedicarePartcComplaintMsg,
    HumCaseCloseWorkTaskError,
    Qs_placeholder_CEO_Hum,
    Qs_placeholder_AT_Hum,
    Qs_placeholder_AR_Hum,
    Qs_CICERROR_Hum,
    Qs_CCValidation_Hum,
    Complaint_must_be_selected_No,
    Complaint_Cant_be_marked_Medicaid_PAR_Provider,
    AutoRouteToastMessage,
    EditAutoRouteToastMessage
};
export function getCaseLabels() {
    return caseLabels;
}