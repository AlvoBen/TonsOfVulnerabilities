/********************************************************************************************************************************************************
Apex Class Name  : CaseAutoRouteConstant_C_HUM
Version          : 1.0 
Created Date     : MAR 16 2021
Test Class       : CaseAutoRouteConstant_T_HUM
Function         : Constants declaration here that are going to be used across the org for Case Auto Routing.
Modification Log :       
* Developer                 Date                 Description
* -------------------------------------------------------------------------------------------------------------------------------------------------------               
* Kavya Bondada             04/14/2021           User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
* Amar Gadewar              05/21/2021           User Story 1874589: T1PRJ0001827 - MF 1 - AHT - "Medicaid PAR Provider Not Accepting" template auto routing (CRM)
* Sunil Kumar Reddy Y       06/17/2021           User Story 2369660: T1PRJ0001827 - MF 1 - Update logic to make 'Medicaid PAR Provider Not Accepting' Template required on the case (CRM)
* Amar Gadewar              06/17/2021           User Story 2389201: T1PRJ0001827 - MF 1 - Quality - Ability to exclude FL LTC QAA Complaint cases from auto routing process (CRM)
* Sunil Kumar Reddy Y       06/30/2021           User Story 2393208: T1PRJ0001827 - MF 1 - Quality - 'Medicaid PAR Provider Not Accepting' Template Routing Rules Update (CRM)
* Amar Gadewar              07/02/2021           User Story 2393211: T1PRJ0001827 - MF 1 - Quality - Update logic to make 'Medicaid PAR Provider Not Accepting' Template required on the case (CRM)
* Kiran Kumar B				10/07/2022			 User Story 3627766: T1PRJ0054938 - Auto-route the Medicaid PAR Provider Not Accepting template to the appropriate team for IL-LTSS, OH and LA
* Bhumika S                 02/10/2023           User Story 3621739: T1PRJ0393258- Complaint Routing - iZone 903- CRM FL MCD QAA Routing Changes (Classic)
* Pooja Kumbhar	            06/29/2023           US4626269 - T1PRJ0865978 - C06- Case Management - OI : T1PRJ0865978 - C06, Case Management, OI (Medicare and Medicaid Other Insurance )Template, Auto Set CI's and Disable Case Edit Page buttons
* Santhi Mandava            01/03/2024           User Story 5243585: T1PRJ1132745 - MF28328 - C06; Case Management - Classic & Lightning - Oklahoma MCD Update Medicaid PAR Provider Not Accepting Routing rules (MCD OK MBRCALLS 1.1) 
*******************************************************************************************************************************************************/

public class CaseAutoRouteConstant_C_HUM {

     //-----------------------US-2187149----------------------------------------//
    public static final List<String> LST_MEDICAREPLANS = new List<String> {'Individual-Medicare','LargeGroup-Medicare','SmallGroup-Medicare'};
    public static final String MEDICAIDPLAN =  'Individual-Medicaid';
    public static final String PRODUCT_TYPE_MCD = 'MCD';
    public static final List<String> LST_MEDICAID_STATES = new List<String> {'KY','IL','FL'};
    public static final List<String> LST_MCDSTATES_KYIL = new List<String> {'KY','IL'};
    public static final String POLICY_MEMBER_ISSUE_STATE_FL = 'FL';
    
    public static final string AUTOROUTING_MEDICARE  = 'Evaluate Medicare';
    public static final string AUTOROUTING_KY_IL_MCD = 'Evaluate KY IL MCD';
    public static final string AUTOROUTING_FL_MCD  = 'Evaluate FL MCD';

    public static  final String sQAACalssification = 'Complaints or Compliments';
    public static final String sQAAIntent = 'QAA';
    public static final String COMPLAINT_NO ='No';
    public static final String GA_NO ='No';

    public static final  Map<String,String> QAAGroupPlanValue = new Map<String,String>{'Medicare' => 'LV GA and Correspondence Screening PGQ', 'KY' => 'Green Bay Grievance and Appeals PGQ','IL' => 'Green Bay Grievance and Appeals PGQ', 'FL' => 'LV RSO Oral Grievance Oversight PGQ'};
    public static final List<String> QAAQueues = new List<String>{'LV G and A and Correspondence Screening', 'Green Bay Grievance and Appeals', 'Louisville RSO Oral Grievance Oversight'}; 
    public static final List<String> QAAGroupNames = new List<String>{'LV GA and Correspondence Screening PGQ','Green Bay Grievance and Appeals PGQ','LV RSO Oral Grievance Oversight PGQ'};

    public static final List<String> lstFLLTCQAAIgnoreGroupNumbers = new List<String>{'X8535','X8536','X9389','X8965','Y1023','X9253','X9243','X8966','X9242','X9247','Y1024'};
    //-----------------------US-2187149----------------------------------------//
    
    //Medicaid PAR Provider Not Accepting
    public static final String sMedicaidPARProviderClassification  = 'Advanced Resolution (ART)';
    public static final String sMedicaidPARProviderIntent = 'Par Provider Not Accepting';
    public static final string AUTOROUTING_MEDICAID_PAR_PROVIDER  = 'Evaluate PAR';
    public static final string AUTOROUTING_MEDICAID_PAR_PROVIDER_KY  = 'Evaluate PAR KY';
	public static final string AUTOROUTING_MEDICAID_PAR_PROVIDER_IL  = 'Evaluate PAR IL';

    public static final string MEDICAID_PAR_PROVIDER_UNIQUEID = 'deeef952-9f63-49ce-97d0-c7eb3204b519';
    
    public static final String MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE = 'Market National Network Ops';
    public static final String MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP = 'Market National Network Ops PGQ';

    public static final String MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_KY = 'Provider Engagement East Region Medicaid';
    public static final String MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_KY = 'Provider Engagement Est Reg Medicaid PGQ';
	
	public static final String MEDICAID_PAR_PROVIDER_AUTOROUTE_QUEUE_IL = 'Provider Contracting Medicaid LTSS'; 
    public static final String MEDICAID_PAR_PROVIDER_AUTOROUTE_GROUP_IL = 'Provider Contracting Medicaid LTSS PGQ';

    public static final List<String> LST_MEDICAIDPAR_FIELDS = new List<String> {'Provider State','Group State','Is this a behavioral health group','Is this a behavioral health provider'};
    public static final List<String> LST_MEDICAIDPAR_STATEFIELDS = new List<String> {'Provider State','Group State'};
    public static final List<String> LST_MEDICAIDPAR_BEHAVIORAL_FIELDS = new List<String> {'Is this a behavioral health group','Is this a behavioral health provider'};
    public static final List<String> LST_MEDICAIDPAR_STATES_VALUES = new List<String> {'KY','IL','FL','SC','IL-LTSS','LA','OH'};
    public static final String MEDICAIDPAR_PROGROUPST_IL = 'IL';
    public static final String MEDICAIDPAR_PROGROUPST_FL = 'FL';
    public static final String MEDICAIDPAR_PROGROUPST_KY = 'KY';
    public static final String MEDICAIDPAR_PROGROUPST_SC = 'SC';
	public static final String MEDICAIDPAR_PROGROUPST_IL_LTSS = 'IL-LTSS';
    public static final String MEDICAIDPAR_PROGROUPST_LA = 'LA';
    public static final String MEDICAIDPAR_PROGROUPST_OH = 'OH';
    public static final String MEDICAIDPAR_PROGROUPST_OK = 'OK';
    public static final String String_Yes = 'Yes';


    public static final String MEDICAIDPAR_PROGROUPST_ILTOPIC = 'Illinois';
    public static final String MEDICAIDPAR_PROGROUPST_FLTOPIC = 'Florida Medicaid Complaints';
    public static final String MEDICAIDPAR_PROGROUPST_KYTOPIC = 'Kentucky';
    public static final String MEDICAIDPAR_PROGROUPST_SCTOPIC_Behavioral_Health = 'Humana Behavioral Health';
    public static final String MEDICAIDPAR_PROGROUPST_SCTOPIC_South_Carolina = 'South Carolina';
	public static final String MEDICAIDPAR_PROGROUPST_OHTOPIC = 'Ohio';
    public static final String MEDICAIDPAR_PROGROUPST_LATOPIC = 'Louisiana';
    public static final String MEDICAIDPAR_PROGROUPST_OKTOPIC = 'Oklahoma';
    //Medicaid PAR Provider Not Accepting

    public static final String CLASSIFICATION_DEMOGRAPHIC = 'Demographics';
    public static final String INTENT_CHANGE = 'Change';
    Public Static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';

    // Medicare and Medicaid OI template
    public static final String MEDICAIRE_OI_TEMPLATE = '9FD171F0-D1F0';		
    public static final List<String> MEDICAIRE_OI_FIELD = new List<String> {'OIUpdateType'};
    public static final List<String> LST_MEDICAIRE_OI_INTENT_VALUES = new List<String> {'Medical Update/Dispute','RX Update/Dispute','RX 090 Denial','OI Prescription Drug Letter','Newborn'};
    public static final String OI_AUTOROUTING_MEDICALUPDATE = 'Medical Update';
    public static final String OI_AUTOROUTING_RXMCDONLY = 'RX Update MCD only';
    public static final String OI_AUTOROUTING_RXMESPDP = 'RX Update MES PDP';
    public static final String OI_AUTOROUTING_RX090DENIAL= 'RX 090 Denial';
    public static final String OI_AUTOROUTING_OIPRESCRIPTION = 'OI Prescription Drug Letter';
    public static final String OI_AUTOROUTING_NEWBORN = 'Newborn';
}