/************************************************************************************************************************************************
  Apex Class Name  : Pharmacy_LH_HUM
  Version          : 1.0
  Created Date     : May 12 2022
  Function         : Helper Class for Pharmacy WorkStream. 
  Test Class       : 
  *************************************************************************************************************************************************

  Modification Log:
  * Modification Id           Developer Name             Review Number               Date                       Description
  *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Vinay Lingegowda                                    05/25/2022                	Added method for case Timeline
  *	2.0		  				  Monali Jagtap									 	  07/17/2023 					 USER STORY- 4812246 T1PRJ0891415 Platform Management-  SF - TECH - Critical- Lightning – Pharmacy
******************************************************************************************************************************************************************************/
public with sharing class Pharmacy_LH_HUM {

    Public Static final String NONPHARMACY_USER_HUM = 'No';
    Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
    Public Static final String PHARMACYSPECIALIST_USER_HUM = 'Yes - Specialist';
    Public Static final String PHARMACYSUPERVISOR_USER_HUM = 'Yes - Supervisor';
    Public Static final String PDPPilot_PermissionSet_Name= 'CRMS_205_CCSPDPPharmacyPilot';
    Public Static final String PHARMACYInsuranceData_PERMISSION_HUM = 'CRMS_301_HPInsuranceData';
    Public Static final String PHARMACYTraditionalInsuranceData_PERMISSION_HUM = 'CRMS_302_HPTraditionalInsuranceData';
    Public Static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';

    /**
    * Method is used to identify if the logged in user is a Pharmacy Specialist / Supervisor user. 
    * <p>
    * @return   String
    */
    public static String isPharmacyUser()
    {
        String sUserProfile = NONPHARMACY_USER_HUM;
        
        if([SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == PHARMACYSPECIALIST_PROFILE_HUM)  
        {   
            sUserProfile = PHARMACYSPECIALIST_USER_HUM;
        	List<String> listCSHPSupervisor = new List<String>();
        	
        	for(String cs : Pharmacy_Supervisor_HUM__c.getAll().keyset())
        	{
        		listCSHPSupervisor.add(cs);
        	}
            sUserProfile= getUserProfile_Sec(sUserProfile,listCSHPSupervisor);}
       		 return sUserProfile;	
        }
       
        public static String getUserProfile_Sec(String sUserProfile,List<String> listCSHPSupervisor){
                List<PermissionSetAssignment> lstPermissionSet=(listCSHPSupervisor != null && !listCSHPSupervisor.isEmpty())? [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name IN : listCSHPSupervisor]:null;
                sUserProfile =(listCSHPSupervisor != null && !listCSHPSupervisor.isEmpty() && NULL != lstPermissionSet && lstPermissionSet.size() > 0)?PHARMACYSUPERVISOR_USER_HUM:'';
                return sUserProfile;
          }
    }