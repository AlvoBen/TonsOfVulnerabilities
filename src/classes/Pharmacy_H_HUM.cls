/************************************************************************************************************************************************
  Apex Class Name  : Pharmacy_H_HUM
  Version          : 1.0
  Created Date     : APRIL 13 2017
  Function         : Helper Class for Pharmacy WorkStream. 
  Test Class       : 
  *************************************************************************************************************************************************

  Modification Log:
  * Modification Id           Developer Name             Review Number               Date                       Description
  *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  * 1.0                       Vishal Verma                 90076                   04/13/2017                  Original Version
  * 1.1                       Vishal Verma                 114200                  07/27/2017                  REQ - 323542 : Exception handling  
  * 1.2						  Sharan Shanmugam									   08/09/2017				   REQ - 324814 : Save Case Comment to CRM during ePost failure  
  * 1.3						  Vishal Verma   			   150268				   11/22/2017				   REQ - 348197 : Added a method to determine Policy member access based on logged in user 
  * 1.4                       Anurag Shah                  267634                  07/20/2018                  REQ - 371243 : Added a method to determine PDP Pilot access based on logged in user 
  * 1.5						  Vijay Gurusamy   			   272865				   08/03/2018				   REQ - 367015 : Added a method to determine if logged in user is part of HP Insurance data permission set 
  * 1.6                       Santhosh Ganji               272865                  31/01/2019                  REQ - 378996 : Added a method to determine if logged in user Is part of HP Traditional Insurance data permission set
  * 1.8                       Santhosh Ganji                                       02/13/2019                  REQ - 379032 : View all policies to Pharmacy Profile users on Person Account Page
  * 1.9                       Prasanthi Kandula                                    10/24/2019                  Defect Fix 443806.0001 HP comments section not rendering correctly
  * 2.0                       Santhosh Ganji                                       02/01/2020                  Userstory 750125 Incident 8213853 Fix.
  * 2.1 					  Kiran Kumar										   03/23/2020				   User Story - 809656: Add Save & Transfer Button on Case Edit Page
  * 2.2                       Apurva Urkude                                        04/26/2021                  REQ:2164003 T1PRJ0002736 - 2021 MF6- TECH - SF  - CheckMarx Fix Part 2 - Pharmacy pages
  * 2.3 					  Kiran Kumar										   06/07/2021				   User Story - 2293376: Checkmarx Scan Fixes (RxRF)
  * 2.4                       Pinky Vijur                                          06/23/2021                  USER Story -2361306 - T1PRJ0002736 - 2021 MF6 - SF - TECH- CheckMarx Vulnerabilities Fix - Benefits And Pharmacy
  * Lavanya Rao				 							10/11/2021                 User Story 2731234: T1PRJ0002606- 2021 MF6 - Swat-SF-Tech-HumConstant and Global Constant Fix
  * Akash Surwase                                                                  08/17/2022                  USER STORY 2819667 : T1PRJ0100059- MF 3 FR 10 - Case Creation (HP)
******************************************************************************************************************************************************************************/
public with sharing class Pharmacy_H_HUM 
{
Public Static final String NONPHARMACY_USER_HUM = 'No';
Public Static final String PHARMACYSPECIALIST_PROFILE_HUM = 'Humana Pharmacy Specialist';
Public static final string sCustomerCareSuperProfile = 'Customer Care Supervisor';
Public Static final String PHARMACYSPECIALIST_USER_HUM = 'Yes - Specialist';
Public Static final String PHARMACYSUPERVISOR_USER_HUM = 'Yes - Supervisor';
Public Static final String PDPPilot_PermissionSet_Name= 'CRMS_205_CCSPDPPharmacyPilot';
Public Static final String PHARMACYInsuranceData_PERMISSION_HUM = 'CRMS_301_HPInsuranceData';
Public Static final String PHARMACYTraditionalInsuranceData_PERMISSION_HUM = 'CRMS_302_HPTraditionalInsuranceData';
Public Static final String Pharmacy_Object_HUM = 'Account';	
Public Static final String Pharmacy_IntegrationError_HUM = 'Integration Error';
Public Static final String Pharmacy_Fatal_HUM = 'Fatal';  
Public Static final Map<Integer,String> Pharmacy_Error_Status_Codes_HUM = New Map<Integer,String>{
        2000 => 'The timeout was reached, and the server didn’t get a chance to respond',
        2001 => 'There was a connection failure',
        2002 => 'Exceptions occurred',
        2003 => 'The response hasn’t arrived',
        2004 => 'The response size is too large'};
Public Static final String ADDRERROR = 'ADDR12006';
Public Static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';
Public Static final String MEDICARE_POLICY = 'MED';
Public Static final String NON_HUMANA_POLICY = 'TRA';
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
        	if(listCSHPSupervisor != null && !listCSHPSupervisor.isEmpty())
        	{
		        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name IN : listCSHPSupervisor];
		        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
		        {
		            sUserProfile = PHARMACYSUPERVISOR_USER_HUM;
		        }
        	}
        }
        return sUserProfile;
    }
    
    /*
    **
    * Method is used to identify PDP User Access. 
    * <p>
    * @return   boolean 
    */
    public static boolean isPDPPilotUser()
    {
        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name =:PDPPilot_PermissionSet_Name];
        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
        {
            return true;
        }
        return false;
    }
	
	/* Method is used to identify Customer Care Supervisor. 
    * <p>
    * @return   boolean 
    */
    public static boolean isCCSupervisor()
    {
        if([SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == sCustomerCareSuperProfile)  
        {
            return true;
        }
        return false;
    }
    
	
	/**
	* Method is used to identify if the logged in user is part of CRMS_301_HPInsuranceData permission set.
	* <p>
	* @return   Boolean
	*/
	public static boolean isHPInsurancePSUser()
	{
		boolean isHPInsurancePSUser = false;

		List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = :PHARMACYInsuranceData_PERMISSION_HUM];

		if (lstPermissionSet != NULL && lstPermissionSet.size() > 0)
		{
			isHPInsurancePSUser = true;
		}

		return isHPInsurancePSUser;
	}
	/**
    * Method is used to identify if the logged in user is part of CRMS_302_HPTraditionalInsuranceData permission set.
    * <p>
    * @return   Boolean
    */
    public static boolean isHPTraditionalInsurancePSUser()
    {
        boolean isHPTraditionalInsurancePSUser = false;

        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name = :PHARMACYTraditionalInsuranceData_PERMISSION_HUM];

        if (lstPermissionSet != NULL && lstPermissionSet.size() > 0)
        {
            isHPTraditionalInsurancePSUser = true;
        }

        return isHPTraditionalInsurancePSUser;
    }
    /**
	* <p>
	* Method to populate value in the hidden variable
	*
	* @param  HttpResponse http response
	* @param  String Class  Name
	* @param  String Method Name
	* @param  String Record Id
	* @return Pagereference
	*/
    public static String logHpFaultError(HttpResponse respFaultCode , String sClassName , String sMethodName , String sRecordId)
    {
    	try
    	{			
			Error_Log__c objErrorLog = new Error_Log__c();
			objErrorLog.Class_Name__c = sClassName;
    		objErrorLog.Method_Name__c = sMethodName;
    		objErrorLog.Record_Id__c = sRecordId;
    		objErrorLog.Object_Type__c = Pharmacy_Object_HUM;
    		objErrorLog.Request_Sent__c = true;
    		objErrorLog.Severity__c = Pharmacy_Fatal_HUM;
    		objErrorLog.Type__c = Pharmacy_IntegrationError_HUM;
    		if(respFaultCode != null)
    		{
    			objErrorLog.Status__c = String.valueof(respFaultCode.getStatusCode());
    			if(respFaultCode.getBody() != null && !String.isBlank(respFaultCode.getBody()))
    			{
	    			String sJsonResponse = respFaultCode.getBody();
	    			sJsonResponse = sJsonResponse.replace('-','_');
	    			FaultResponse_DTO_HUM faultResponse = new FaultResponse_DTO_HUM();
	    			faultResponse = (FaultResponse_DTO_HUM)System.JSON.deserialize(sJsonResponse,FaultResponse_DTO_HUM.class);
	    			if(faultResponse.Fault != null && faultResponse.Fault.detail != null)
	    			{
	    				if(faultResponse.Fault.detail.errorInfo != null)
	    				{	
	    					objErrorLog.Error_Code__c = faultResponse.Fault.detail.errorInfo.error_code;
		    				objErrorLog.Error_Message__c = faultResponse.Fault.detail.errorInfo.error_message;
	    				}
	    				if(faultResponse.Fault.detail.transactionInfo != null)
	    				{
		    				objErrorLog.Integration_Point__c = faultResponse.Fault.detail.transactionInfo.URL_in;
		    				objErrorLog.Operation__c = faultResponse.Fault.detail.transactionInfo.transaction_id;
		    			}
	    			}
    			}
    			else
    			{
    				objErrorLog.Error_Message__c = Pharmacy_Error_Status_Codes_HUM.containskey(respFaultCode.getStatusCode()) ? Pharmacy_Error_Status_Codes_HUM.get(respFaultCode.getStatusCode()) : '';
    			}
    				
    		}
    	   if(!objErrorLog.Error_Message__c.contains(ADDRERROR) && Error_Log__c.SObjectType.getDescribe().isCreateable() && Schema.sObjectType.Error_Log__c.fields.Class_Name__c.isCreateable()&& Schema.sObjectType.Error_Log__c.fields.Method_Name__c.isCreateable()&&Schema.sObjectType.Error_Log__c.fields.Record_Id__c.isCreateable()&&Schema.sObjectType.Error_Log__c.fields.Object_Type__c.isCreateable()&&Schema.sObjectType.Error_Log__c.fields.Request_Sent__c.isCreateable()&& Schema.sObjectType.Error_Log__c.fields.Severity__c.isCreateable()&&Error_Log__c.SObjectType.getDescribe().isCreateable() && Schema.sObjectType.Error_Log__c.fields.Class_Name__c.isCreateable()&& Schema.sObjectType.Error_Log__c.fields.Method_Name__c.isCreateable()&&Schema.sObjectType.Error_Log__c.fields.Record_Id__c.isCreateable()&&Schema.sObjectType.Error_Log__c.fields.Object_Type__c.isCreateable()&&Schema.sObjectType.Error_Log__c.fields.Type__c.isCreateable() )
           {
            insert objErrorLog;
           }
    		return objErrorLog.Error_Message__c;
    	}	
    	catch(Exception ex)
    	{
    		HUMExceptionHelper.logErrors(ex,'Pharmacy_H_HUM', 'logHpFaultError');
    		return '';
    	}
    }
	
	/**
	* <p>
	* Method to fetch log Node Code on the basis of Classifcation Type
	*
	* @param  String Classification Type
	* @return List<String> List Log Code
	*/
    public static List<String> fetchLogCode(String sClassifcationType)
    {
    	List<String> lstLogNode = new List<String>();
    	
		if(String.isNotBlank(sClassifcationType)){
            List<Humana_Pharmacy_Log_Code_HUM__mdt> logcodeList = [Select id ,Log_Code__c from Humana_Pharmacy_Log_Code_HUM__mdt where Classification_Type__c = :sClassifcationType order by Log_Code__c];
            for(Humana_Pharmacy_Log_Code_HUM__mdt sLogNode:logcodeList){
                if(String.isNotBlank(sLogNode.Log_Code__c)){
                    lstLogNode.add(sLogNode.Log_Code__c);   
                }
            }
        }
    	return lstLogNode;
    }
    
    /**
    * <p>
    * Method to fetch log Node Code on the basis of Classifcation Type
    *
    * @param  String Classification Type
    * @return List<String> List Log Code
    */
    public static List<String> fetchLogCodeForBothHPCCS(String sClassifcationType, boolean bRSOFlag, boolean bHPRT)
    {
        List<String> lstLogNode = new List<String>();
        if(!String.isBlank(sClassifcationType))
        {
            if((!bRSOFlag && !sClassifcationType.equalsIgnoreCase(CASE_CLASSIFICATION_TYPE_CALLRSO)) || bHPRT)
            {
                for(Humana_Pharmacy_Log_Code_HUM__mdt sLogNode : [Select id , Log_Code__c from Humana_Pharmacy_Log_Code_HUM__mdt where Classification_Type__c = :sClassifcationType  order by Log_Code__c])
                {
                    lstLogNode.add(sLogNode.Log_Code__c);   
                }
            }
            if(bRSOFlag)            
            {
                for(Humana_Pharmacy_Log_Code_HUM__mdt sLogNode : [Select id , Log_Code__c from Humana_Pharmacy_Log_Code_HUM__mdt where Classification_Type__c = :sClassifcationType order by Log_Code__c])
                {
                    lstLogNode.add(sLogNode.Log_Code__c);   
                }
            }
        }
        return lstLogNode;
    }        

    /**
	* <p>
	* Method to determine Poicy member access based on the logged in user
	*
	* @param  String Product Type
	* @param  String User Profile
	* @return Boolean Access
	*/
    public static boolean determinePolicyMemberAccess(String sProductTYpe ,String sPharmacyUser)
    {
    	return (sPharmacyUser == NONPHARMACY_USER_HUM || 
                (sPharmacyUser != NONPHARMACY_USER_HUM && sProductTYpe== MEDICARE_POLICY)
               || sProductTYpe == NON_HUMANA_POLICY);// agnostic customer

    }
	
	/**
    * <p>
    * Method to determine Poicy member access on AccountPage based on the logged in user
    *
    * @param  String Product Type
    * @param  String User Profile
    * @return Boolean Access
    */
    public static boolean determineAccountPageMemberAccess(String sProductTYpe ,String sPharmacyUser)
    {
        return (sPharmacyUser == NONPHARMACY_USER_HUM || sPharmacyUser != NONPHARMACY_USER_HUM);
    }
}