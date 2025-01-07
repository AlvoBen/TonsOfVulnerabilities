/*******************************************************************************************************************************
Apex Class Name : GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM
Version         : 1.0
Created On      : 12/14/2021 
Function        : Lightning Constant class for Case Detail section
Test Class      : CaseCommentsDataTable_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                        12/14/2021                	Original Version
*    2.0           Vinay Lingegowda                                    05/25/2022                	Added method for case Timeline
*    3.0           M K Manoj                                           07/27/2022                 US-3522143,3495639 For passing Case Record Type to child cmp
*    4.0		Prasuna Pattabhi										07/28/2022					User Story 3518085 : View Case Logged Information from Lightning
*******************************************************************************************************************************************************************/

public with sharing class GLOBAL_CASE_DETAIL_CONSTANT_LH_HUM {
    //Added for case Comments
    Public Static final String CASECOMMENT_PHARMACY_MEMBERNOTFOUND_HUM = 'This member does not have an Order Management System (OMS) - also known as ePost/ANS  - account. The Case Comment is not saved to ePost Rx, but saved in CRM';
    Public Static final String CASECOMMENT_PHARMACY_FAULTERROR_HUM =  'The Order Management System (OMS) - also known as ePost/ANS  - is currently unavailable. The Case Comment is not saved to ePost Rx, but saved in CRM';
    Public Static final String CASECOMMENT_INSERTED = 'Comment inserted';
    Public Static final String CASECOMMENT_NOT_INSERTED = 'No Comment inserted';

    Public Static final String HUMANA_PHARMACY_SPECIALIST = 'Humana Pharmacy Specialist';
    Public Static final String CUSTOMER_CARE_SPECIALIST = 'Customer Care Specialist';
    
    Public Static final String SYSTEM_ADMIN = 'System Administrator';
    Public Static final String CUSTOMER_CARE_SUPERVISOR = 'Customer Care Supervisor';
    Public Static final String YES_SUPERVISOR = 'Yes - Supervisor';
    Public Static final String NONPHARMACY_USER_HUM = 'No';
    Public Static final String RSO_HP_PermissionSet_Name= 'CRMS_206_CCSHumanaPharmacyAccess';
    Public Static final String PDPPilot_PermissionSet_Name= 'CRMS_205_CCSPDPPharmacyPilot';

    Public Static final String HUM_HP_REC_TYPE= 'HP';
    Public Static final String MEDICARE_POLICY = 'MED';
    Public Static final string WEB_CHAT_ORIGIN = 'Web Chat';
    Public static final String LOGCODEHSP_CLASSIFICATIONTYPE = 'HP Specialty Calls';
    Public Static final String LOGCODEHSP_SERVICEMODELTYPE = 'Humana Pharmacy';
    Public Static String CLOSEDHP_MEMBERCASE_HUM = 'Closed HP Member Case';
	Public static final string HP_MEMBERCASE_HUM = 'HP Member Case';

    Public Static Set<String> WQUEUES = new Set<String>{'Provider Payment Integrity', 'Louisville Other Insurance Investigation', 'Louisville RSO Oral Grievance Oversight', 'LV Medicare Member Communications'};
    Public Static Set<String> CHATQUEUES = new Set<String>{'Humana Pharmacy Web Chat', 'HP Clinical Services Web Chat'};
    Public Static final List<String> Pharmacy_Record_Type_Name = New List<String>{'Closed HP Agent/Broker Case','Closed HP Group Case','Closed HP Member Case','Closed HP Provider Case','Closed HP Unknown Case','HP Agent/Broker Case','HP Group Case','HP Member Case','HP Provider Case','HP Unknown Case'};
    	
     Public Static final List<String> Log_Code_Pharmacy_Record_Type_Name = New List<String>{'Closed HP Member Case','HP Member Case'};
    	

    public static void onlyForCoverage() {
        // for test coverage only
    }
    Public Static final String HUM_PHARMACY_SPECIALIST= 'Humana Pharmacy Specialist';
    Public Static final String YES_SPECIALIST = 'Yes - Specialist';
    Public Static final String CLAIMS_MANUAL_ENTRY = 'Claims Manual Entry';
    Public Static final String CRM = 'CRM';
    Public Static final String CRM_LIGHTNING = 'CRM Lightning';    
}