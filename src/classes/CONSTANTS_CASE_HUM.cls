/*******************************************************************************************************************************
Apex Class Name : CONSTANTS_CASE_HUM
Version         : 1.0
Created On      : 12/10/2021
Function        : This class is used for serve the purpose of constants related to case in lightning

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ritik Agarwal                                    12/06/2021               Original Version
*    2.0           Prasuna Pattabhi                                    12/05/2022              Build Fix 
*    3.0		   Jasmeen Shangari									12/05/2022		         US-4016899, Creditable coverage Process changes to launch Case Edit
* Vijaya Lakshmi Tummala                 	 10/17/2022     Original Version-Case Attachment -Feature-3033033
* 															UserStory -3775271 Creating Case Attachments in Lightning
*    4.0          Nirmal garg			02/07/2023							Invoice request autorouse fix
* 	 5.0		  Gowthami Thota		02/28/2023			Added constants for Case Interactions
*****************************************************************************************************************************************/
public with Sharing class CONSTANTS_CASE_HUM {
    
    Public Static final String OBJECT_ACCOUNT = 'Account';
    Public Static final String OBJECT_MEMBERPLAN = 'MemberPlan';
    Public Static final String PDP_Pilot_Pharmacy_Work_Queues = 'PDP Pilot Humana Pharmacy Calls';	
    public static final string HP_MEMBERCASE_HUM = 'HP Member Case';
    Public Static final String CASE_ORIGIN='Inbound Call';
    public Static final String CASE_STATUS='In Progress';
    public Static final String CASE_TYPE='Customer Inquiry';
    public Static final String AUTOROUTING_PARTD = 'Evaluate Part D';
    public Static Final String AUTOROUTING_COMPLETED = 'Completed';
    public Static final String AUTOROUTING_ExpeditedAppeal = 'Evaluate Expedited Appeal';
    public Static final String AUTOROUTING_PCP = 'PCP Reassignment';
    public Static final String AUTOROUTING_GOODCAUSE = 'Evaluate Good Cause';
    public Static final String AUTOROUTING_GOODCAUSETIE = 'Termed in Error';
    Public Static Final String AUTOROUTING_INVOICEREQUEST = 'Invoice Requests';
    Public Static Final String AUTOROUTING_EVALUATE = 'Evaluate';
    Public Static final String ATTESTATION_TEMPLATE_NAME = 'ATVF007';
    Public Static final String CASE_RSO_ENROLLMENT_OWNER = 'Louisville RSO Enrollment';
    Public Static final String CASE_STATUS_PENDING_RESPONSE = 'Pending - Response';   
    public static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};    
    public static final String RSO_HP_PermissionSet_Name= 'CRMS_206_CCSHumanaPharmacyAccess';  
    public static final String NONPHARMACY_USER_HUM = 'No';  	
    public static final String sEmpty = '';
     Public Static final String EHUB_URL_DOCID = '/apex/EhubDocumentViewer_VF_HUM?DocId=';
     Public Static final String EHUB_URL_VID = '&VId=';
     public static final string sAttURL = '/servlet/servlet.FileDownload?file=';
     public static final string sAttLightURL = '/sfc/servlet.shepherd/version/download/';
     public static final string HUMDateTimeFormat = 'MM/dd/yyyy hh:mm a';
     public static final string WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY = 'Provider Payment Integrity';
     public static final string WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION = 'Louisville Other Insurance Investigation';
     public static final string WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT = 'Louisville RSO Oral Grievance Oversight';
     public static final string WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS = 'LV Medicare Member Communications';
     Public Static final List<String> WEB_CHAT_WQUEUES = New List<String>{'Humana Pharmacy Web Chat','HP Clinical Services Web Chat'};
     Public Static final String HumHPRecType = 'HP';
     public static final string WEB_CHAT_ORIGIN = 'Web Chat'; 
     Public Static final String MEDICARE_POLICY = 'MED';
     Public Static final Integer EHUB_Limit = 26214400;
     public static final string DATE_Format = 'MM/dd/yyyy hh:mm:ss a';
     Public Static final String ImmediateLeader = 'Immediate Leader';
     Public Static final String SecondaryLeader = 'Secondary Leader';
     Public Static final String ClosedHPAgent = 'Closed HP Agent/Broker Case';
     Public Static final String sSrcUp = ',srcUp,';
     Public Static final String sForwardSlash = '/';
}