/*
*******************************************************************************************************************************
File Name        : crmretail_labels.js
Version          : 1.0 
Created Date     : 07/19/2022
Function         : JS to hold custom labels and constants of interactions datatable
Modification Log :
* Developer                Date                  Description
*******************************************************************************************************************************
* Lakshmi Madduri      	  07/19/2022            Original Version
* Navajit Sarkar          08/17/2022            REQ-3510484 T1PRJ0154546 / SF / MF9 Storefront: Recommend Events (Story 1)
* Vinoth L                09/06/2022            DF-6112 - 09/23 - Engagement Prediction Hover Fix
* Mohamed Thameem         01/05/2023            User Story 2792916: T1PRJ0154546 / SF / MF9 Storefront Home Page: Visitor Interactions Search
* Sahil Verma             02/01/2023            User Story 4231796: T1PRJ0154546 / SF / MF9 Storefront - Ability to View Deceased Information on Member Account
* Vinoth L                04/26/2023            User Story 4566290: T1PRJ0154546 / SF / MF3 Storefront: Display MyHumana Enrollment & Go365 Enrollment - Backend Logic
* Mohamed Thameem         22/06/2023            US4736604 - T1PRJ0154546 / SF / MF9 Storefront - AOR UI Component Upgrade[Tech]
*/
import PRIORITY_ICON from '@salesforce/label/c.CRMRetail_priority_icon';
import BIRTHDAY from '@salesforce/label/c.CRM_Retail_Birthday';
import MONTH_COUNT from '@salesforce/label/c.CRM_Retail_Month_Count';
import DAYS_COUNT from '@salesforce/label/c.CRM_Retail_Days_Count';
import END_DURATION from '@salesforce/label/c.CRM_Retail_Icon_Display_End_Duration';
import START_DURATION from '@salesforce/label/c.CRM_Retail_Icon_Display_Start_Duration';
import BIRTHDAY_ICON from '@salesforce/label/c.CRM_Retail_Birthday_Icon';
import EMPLOYEE_ORGANiZATION_ICON from '@salesforce/label/c.Employee_Organization_Icon';
import ONSITE from '@salesforce/label/c.Icon_Label_For_Onsite_Interaction';
import EMPLOYEE_ASSET_ICON from '@salesforce/label/c.Employee_Asset_Icon';
import VIRTUAL from '@salesforce/label/c.Icon_Label_For_Virtual_Interaction';
import CHECKIN_ICON from '@salesforce/label/c.CRMRetail_Check_Icon_Name';
import BLOCK_VISITOR from '@salesforce/label/c.CRMRetail_BlockVisitor_Icon_Name';
import INACTIVE_MEMBER from '@salesforce/label/c.CRMRetail_InactiveMember_Label';
import FIRST_TIME_VISITOR from '@salesforce/label/c.CRM_Retail_First_Time_Visitor';
import NEW_MEMBER from '@salesforce/label/c.CRM_Retail_New_Member';
import VETERAN from '@salesforce/label/c.CRM_Retail_Veteran';
import AGING_IN from '@salesforce/label/c.CRM_Retail_Aging_In';
import SDOH from '@salesforce/label/c.CRM_Retail_SDoH';
import INDICATORS_APPLY from '@salesforce/label/c.CRM_Retail_Indicators_Apply';
import ENGAGEMENT from '@salesforce/label/c.CRMRetail_Engagement';
import INDICATORS_NOTAPPLIED from '@salesforce/label/c.CRM_Retail_Indicators_Not_Applied';
import VISITOR_CHECKIN_FIRSTTIME from '@salesforce/label/c.CRM_Retail_Visitor_Check_In_1st_Time';
import VISITOR_CHECKIN from '@salesforce/label/c.CRM_Retail_Visitor_Check_In';
import VISITOR_RECORDTYPE from '@salesforce/label/c.CRM_Retail_Visitor_Record_Type';
import NOT_GO365_ELIGIBLE from '@salesforce/label/c.CRM_Retail_Not_Go365_Eligible';
import GO365_ELIGIBLE from '@salesforce/label/c.CRM_Retail_Go365_Eligibility';
import RELEASE_DATE from '@salesforce/label/c.CRMRetail_Release_Date';
import SCHEDULED_VIRTUAL_ICON from '@salesforce/label/c.Icon_Label_For_ScheduledVirtual_Interaction';
import SCHEDULED_ONSITE_ICON from '@salesforce/label/c.Icon_Label_For_ScheduledOnsite_Interaction';
import VIDEO_ICON from '@salesforce/label/c.Utility_Video_Icon';
import VIDEO_ICON_LABEL from '@salesforce/label/c.Icon_Label_For_Video_Icon';
import SUCCESS_TEXT from '@salesforce/label/c.CRM_Retail_Waiver_Success_Text';
import WAIVER_EXPIRATION_SUCCESS from '@salesforce/label/c.CRM_Retail_Waiver_Functionality_Expiration_Success';
import WAIVER_FAILURE from '@salesforce/label/c.CRM_Retail_Waiver_Failure_Text';
import WAIVER_EXPIRATION_ERROR from '@salesforce/label/c.CRM_Retail_Waiver_Expiration_Functionality_Error';
import SIGNED_ERROR from '@salesforce/label/c.CRM_Retail_Signed_Error_text';
import DELETE_INTERACTION from '@salesforce/label/c.CRMRetail_SureToDelete_HUM';
import DELETE_HE_INTERACTION from '@salesforce/label/c.CRMRetail_SureToDelete_HE_HUM';
import SCH_INTERACTION_CREATED from '@salesforce/label/c.CRMRETAIL_SCH_INTERACTION_CREATED';
import VISITOR_CHECKINS from '@salesforce/label/c.CRMRetail_Visitor_CheckIns';
import ONSITE_INTERACTIONS from '@salesforce/label/c.CRMRetail_Onsite_Interactions';
import VIRTUAL_INTERACTIONS from '@salesforce/label/c.CRMRetail_VirtualInteractions';
import CONFIRM_DELETE_INTERACTION from '@salesforce/label/c.CRMRetail_Confirm_Delete_Interaction';
import ERROR_VARIANT from '@salesforce/label/c.CRMRetail_Error_variant';
import SUCCESS_VARIANT from '@salesforce/label/c.CRMRetail_Success_Variant';
import UNEXPECTED_ERROR from '@salesforce/label/c.CRMRetail_Checkin_Error_Message';
import ERROR from '@salesforce/label/c.CRMRetail_Error_HUM';
import INTERACTIONS_CREATED from '@salesforce/label/c.CRMRetail_Interaactions_Created';
import DUPLICATE_INTERACTIONS from '@salesforce/label/c.CRMRetail_DuplicateInteractions';
import INTERACTIONS_DELETED from '@salesforce/label/c.CRMRetail_InteractionsDeleted';
import HEALTH_EDUCATOR from '@salesforce/label/c.CRMRETAIL_HEALTH_EDUCATOR_HUM';
import SNP_TEXT from '@salesforce/label/c.CRMRetail_SNP_Text';
import CRMRetail_Interactions_PageName from '@salesforce/label/c.CRMRetail_Interactions_PageName';
import CACHE_SETUP_ERROR_TEXT from '@salesforce/label/c.CRMRetail_CacheSetup_ErrorText';
import OOO_TRACKING_RECORD_SUCCESS from '@salesforce/label/c.CRM_Retail_One_On_One_Tracking_Create_Message';
import CRMRetail_RecommendationHeading_HUM from '@salesforce/label/c.CRMRetail_RecommendationHeading_HUM';
import CRMRetail_RecommendationOptionText_HUM from '@salesforce/label/c.CRMRetail_RecommendationOptionText_HUM';
import CRMRetail_RecommendationFollowupText_HUM from '@salesforce/label/c.CRMRetail_RecommendationFollowupText_HUM';
import CRMRetail_NoRecommendationText_HUM from '@salesforce/label/c.CRMRetail_NoRecommendationText_HUM';
import CRMRetail_RecommendationSubmitButton_HUM from '@salesforce/label/c.CRMRetail_RecommendationSubmitButton_HUM';
import CRMRetail_FollowUp from '@salesforce/label/c.CRMRetail_FollowUp';
import CRMRetail_FollowUp_BtnLabel from '@salesforce/label/c.CRMRetail_FollowUp_BtnLabel';
import CRMRetail_UpcomingEventsMessage_HUM from '@salesforce/label/c.CRMRetail_UpcomingEventsMessage_HUM';
import CRMRetail_RecommendEventsNote_HUM from '@salesforce/label/c.CRMRetail_RecommendEventsNote_HUM';
import CRMRetail_RecommendationSButtonIcon_HUM from '@salesforce/label/c.CRMRetail_RecommendationSButtonIcon_HUM';
import CRMRetail_Warning_Text from '@salesforce/label/c.CRMRetail_Warning_Text';
import ENGAGEMENT_PREDICTION_TEXT from '@salesforce/label/c.CRMRetail_Engagement_Prediction_Key';
import CRMRetail_Interaction_Reason from '@salesforce/label/c.CRMRetail_Interaction_Reason';
import CRMRetail_IconError from '@salesforce/label/c.CRMRetail_Deceased_Icon_Error';
import CRMRetail_Error_Loading_Icon from '@salesforce/label/c.CRMRetail_Error_Loading_Deceased_Icon';
import CRM_Retail_Go365_Eligibility from '@salesforce/label/c.CRM_Retail_Go365_Eligibility';
import CRMRetail_LastLogin_DateTime from '@salesforce/label/c.CRMRetail_LastLogin_DateTime';
import CRMRetail_MyHumanaKey from '@salesforce/label/c.CRMRetail_MyHumanaKey';
import CRMRetail_Error_Label from '@salesforce/label/c.CRMRetail_Error_Label';
import CRMRetail_Member from '@salesforce/label/c.CRMRetail_Member';

export const labels = {
    PRIORITY_ICON,
    BIRTHDAY,
    MONTH_COUNT,
    DAYS_COUNT,
    END_DURATION,
    START_DURATION,
    BIRTHDAY_ICON,
    EMPLOYEE_ORGANiZATION_ICON,
    ONSITE,
    EMPLOYEE_ASSET_ICON,
    VIRTUAL,
    CHECKIN_ICON,
    BLOCK_VISITOR,
    INACTIVE_MEMBER,
    FIRST_TIME_VISITOR,
    NEW_MEMBER,
    VETERAN,
    AGING_IN,
    SDOH,
    INDICATORS_APPLY,
    ENGAGEMENT,
    INDICATORS_NOTAPPLIED,
    VISITOR_CHECKIN_FIRSTTIME,
    VISITOR_CHECKIN,
    VISITOR_RECORDTYPE,
    NOT_GO365_ELIGIBLE,
    GO365_ELIGIBLE,
    RELEASE_DATE,
    SCHEDULED_VIRTUAL_ICON,
    SCHEDULED_ONSITE_ICON,
    VIDEO_ICON,
    VIDEO_ICON_LABEL,
    SUCCESS_TEXT,
    WAIVER_EXPIRATION_SUCCESS,
    WAIVER_FAILURE,
    WAIVER_EXPIRATION_ERROR,
    SIGNED_ERROR,
    DELETE_HE_INTERACTION,
    DELETE_INTERACTION,
    SCH_INTERACTION_CREATED,
    VISITOR_CHECKINS,
    ONSITE_INTERACTIONS,
    VIRTUAL_INTERACTIONS,
    CONFIRM_DELETE_INTERACTION,
    ERROR_VARIANT,
    SUCCESS_VARIANT,
    UNEXPECTED_ERROR,
    ERROR,
    INTERACTIONS_CREATED,
    DUPLICATE_INTERACTIONS,
    INTERACTIONS_DELETED,
    HEALTH_EDUCATOR,
    SNP_TEXT,
    CRMRetail_Interactions_PageName,
    CACHE_SETUP_ERROR_TEXT,
    OOO_TRACKING_RECORD_SUCCESS,
    CRMRetail_RecommendationHeading_HUM,
    CRMRetail_RecommendationOptionText_HUM,
    CRMRetail_RecommendationFollowupText_HUM,
    CRMRetail_NoRecommendationText_HUM,
    CRMRetail_RecommendationSubmitButton_HUM,
    CRMRetail_FollowUp,
    CRMRetail_UpcomingEventsMessage_HUM,
    CRMRetail_RecommendEventsNote_HUM,
    CRMRetail_RecommendationSButtonIcon_HUM,
    CRMRetail_Warning_Text,
    CRMRetail_FollowUp_BtnLabel,
    ENGAGEMENT_PREDICTION_TEXT,
    CRMRetail_Interaction_Reason,
    CRMRetail_IconError,
    CRMRetail_Error_Loading_Icon,
    CRM_Retail_Go365_Eligibility,
    CRMRetail_LastLogin_DateTime,
    CRMRetail_MyHumanaKey,
    CRMRetail_Error_Label,
    CRMRetail_Member
}
export const interactionConstants = {
    ACTION:'action',
    REQ_PERMSN_SET:"Required Permission Set",
    CHECKIN:'checkin',
    CHECKINBTN:'checkinbtn',
    ONSITE:'onsite',
    ONSITEBTN:'onsitebtn',
    VIRTUAL:'virtual',
    VIRTUALBTN:'virtualbtn',
    FETCH_SELECTED_EVENTS:'fetchSelectedEvents',
    SHOW_NOTIFICATION:'showNotification',
    EDIT_INTERACTIONS:'editInteractions',
    DELETE_INTERACTIONS:'deleteInteractions',
    HEALTH_EDUCATOR:'healthEducator',
    ATTENDED:'attended',
    DISMISSIBLE:'dismissible',
    SCHEDULED:'scheduled',
    SCHEDULEDVIRTUAL:'scheduledVirtual',
    SCHEDLEDONSITE:'scheduledOnsite',
    RELOAD_INTERACTIONS:'reloadinteractions',
    INTERACTIONS:'Interactions',
    SHOWRECOMMENDATIONS:'showRecommendations',
    STRING_CHECKINSUCCESS : 'checkInSuccess',
    STRING_DUPLICATEINTERACTION : 'DuplicateInteraction',
    STRING_GENERATEERRORMESSAGE : 'generateErrorMessage',
    STRING_VIRTUALEVENT_FOUND:'virtualEventFound',
    STRING_GENERATEERRORMESSAGE : 'generateErrorMessage',
    FOLLOWUP_TASK: 'followUpTask',
    STICKY:'sticky',
    ERROR_UPPERCASE :'ERROR',
    SUCCESS_UPPERCASE :'SUCCESS',
    NAME : 'Name',
    DATA : 'Data',
    STATE : 'State',
    AORNAME : 'AORName',
    AORNUM : 'AORNum',
    NA : 'N/A'

}
export function getLabels(){
    return labels;
}