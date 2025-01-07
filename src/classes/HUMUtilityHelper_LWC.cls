/************************************************************************************************************************
Apex class Name    : HUMUtilityHelper_LWC
Version            : 1.0
Function           : Helper Class contains generic methods used in Hum_CaseTasks_LWC.
Created On         : December 29 2021
Test Class         : HUMUtilityHelper_T_LWC

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala                 			12/29/2021          Original Version
*   Muthukumar                                      04/04/2023          CIM Switch Implementation
*   Prasuna Pattabhi                                10/10/2023          Removed Cacheable
*   Prasuna Pattabhi                                10/10/2023          Added Cacheable
***************************************************************************************************************************/

Public with sharing Class HUMUtilityHelper_LWC {

    /**
     * Check CRM Functionality ON/OFF switch (CRMFunctionality_ONOFF_Switch__c)
     * @param String - story number
     * @return Boolean
     */
    public static Boolean isCRMFunctionalityON(String sStoryNumber) {
        Boolean bCRMFunctionalityON = false;
        try {
            if(String.isNotBlank(sStoryNumber)) {
                CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues(sStoryNumber);
                if(csCRMOnOffSwitch != null) {
                    bCRMFunctionalityON = csCRMOnOffSwitch.IsON__c;                    
                }
            }
        } catch(Exception ex) {
            bCRMFunctionalityON = false;
        }
        return bCRMFunctionalityON;
    }
 /**
     * Check CRM Functionality ON/OFF switch (CRMFunctionality_ONOFF_Switch__c)
     * @param String - story number
     * @return Boolean
     */
    @AuraEnabled(cacheable = true)
    public static Map<String,Boolean> isCRMFunctionalityONJS(List<String> sStoryNumber) {
        //Boolean bCRMFunctionalityON = false;
        Map<String,Boolean> mapOfSwitch = new Map<String,Boolean>();
        try {
        
            for(String strNum : sStoryNumber){
                CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues(strNum);
                if(csCRMOnOffSwitch != null) {
                    mapOfSwitch.put(csCRMOnOffSwitch.Name,csCRMOnOffSwitch.IsON__c);                   
            }
        }
        
        } catch(Exception ex) {
            
            mapOfSwitch.put('false',false);
            }
        return mapOfSwitch;
    }

     /*
     * Method Name   :  doSort
     * Description   :  This method is sort the set of String values and returns sorted list values
     * Return Type   :  List<String>
     * Parameters    :  Set<String>
     */
    public static List<String> doSort(Set<String> setValues)
    {
        List <String> lstOptions = new List <String>();
        lstOptions.addAll(setValues);
        for(integer iCounter = 0; iCounter < lstOptions.size(); iCounter++)
        {
            lstOptions[iCounter] = lstOptions[iCounter].toUpperCase() + lstOptions[iCounter];
        }
        lstOptions.sort();
        for(integer iCounter = 0; iCounter < lstOptions.size(); iCounter++)
        {
            lstOptions[iCounter] = lstOptions[iCounter].substring( lstOptions[iCounter].length() / 2 );
        }
        return lstOptions;
    }

    /**
    * Method is used to identify if the logged in user is a Pharmacy Specialist / Supervisor user. 
    * <p>
    * @return   String
    */
    public static String isPharmacyUser()
    {
        String sUserProfile = HumConstants_LWC.NONPHARMACY_USER_HUM;
        
        if([SELECT Id, Name FROM Profile WHERE Id =:UserInfo.getProfileId()].Name == HumConstants_LWC.PHARMACYSPECIALIST_PROFILE_HUM)  
        //if([SELECT Id, Name FROM Profile WHERE Id ='00eF0000000zVEQIA2'].Name == HumConstants_LWC.PHARMACYSPECIALIST_PROFILE_HUM)  
        {
            sUserProfile = HumConstants_LWC.PHARMACYSPECIALIST_USER_HUM;
        	List<String> listCSHPSupervisor = new List<String>();
        	
        	for(String cs : Pharmacy_Supervisor_HUM__c.getAll().keyset())
        	{
        		listCSHPSupervisor.add(cs);
        	}
        	if(listCSHPSupervisor != null && !listCSHPSupervisor.isEmpty())
        	{
		        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name IN : listCSHPSupervisor];
                //List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = '0052I00000HMX9oQAH' AND PermissionSet.Name IN : listCSHPSupervisor];
		        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
		        {
		            sUserProfile = HumConstants_LWC.PHARMACYSUPERVISOR_USER_HUM;
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
        List<PermissionSetAssignment> lstPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId = :UserInfo.getUserId() AND PermissionSet.Name =:HumConstants_LWC.PDPPilot_PermissionSet_Name];
        if (NULL != lstPermissionSet && lstPermissionSet.size() > 0)
        {
            return true;
        }
        return false;
    }
	
     /**
    * Check user is having permission set access or not
    * @param String PermissionSetName
    * @returnParam Boolean bHasPermission
    */
    public static boolean isUserhavingPermissionSetAccess(String sPermissionSetName){
        boolean bHasPermission = false;
        try{
            List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND PermissionSet.Name = :sPermissionSetName];
            if(null != lstPMAssign && !lstPMAssign.isEmpty()) bHasPermission = true;
            if(Test.isRunningTest()){throw new NullPointerException();}
        }catch(exception ex){
            bHasPermission = false;
            HUMExceptionHelper.logErrors(ex, 'HUMUtilityHelper_LWC', 'isUserHavingPharmvilleAccess');
        }
        return bHasPermission;
    }
    
    /**
    * Method is used to identify if the logged in user is a RSO User having access to  Medicare Customer Service Access. 
    * <p>
    * @return   boolean
    */
    public static boolean isUserHavingMedicareCustomerServiceAccess()
    {
        
        boolean bMedicareCustomerServiceAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : HumConstants_LWC.MEDICARECALLS_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bMedicareCustomerServiceAccess = true;
        
        
        
        return bMedicareCustomerServiceAccess;
    }
    
    /**
    * Method is used to identify if the logged in user is a MIR Pilot User having access to CRMS_1100_MemberIssueResolution Permission Set. 
    * <p>
    * @return   boolean
    */
    public static boolean isMIRPilotUser()
    {
        boolean bMIRPilotPermissionAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : HumConstants_LWC.MIR_PERMISSIONSET_HUM];
        //List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= '0050G00000CQdTtQAL' AND PermissionSet.Name = : HumConstants_LWC.MIR_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bMIRPilotPermissionAccess = true;

        return bMIRPilotPermissionAccess;
    }
    /**
    * Display minute value in day/hour format
    * param caseMilestone
    * return String 
    */
    public static String formatMinutes(CaseMilestone caseMilestone) {
        String sTimeSpentOnCase = '';
        
        if(caseMilestone != null) {
            Integer iTimeSpentOnCase = 0;
            Boolean bIsViolated = caseMilestone.isviolated;
            
            String sCaseTime = bIsViolated ? caseMilestone.TimeSinceTargetInMins : caseMilestone.TimeRemainingInMins;
            if(String.isNotBlank(sCaseTime)) {
                iTimeSpentOnCase = Integer.valueOf(sCaseTime.substringBefore(':'));             
            }
            
            if(iTimeSpentOnCase != 0) {
                Integer days = iTimeSpentOnCase / 60 / 24 ;
                Integer hours = (iTimeSpentOnCase - days * 60 * 24) / 60 ;
                
                sTimeSpentOnCase = (bIsViolated ? '-':'') + (days+'d '+hours+'h');
            }
        }
        return sTimeSpentOnCase;
    }
    
    public static User fetchUserDetails()
    {
    	return [Select Current_Queue__c, Mentor_Sub_Function__c from User where ID=:UserInfo.getUserId()];
    }
    
    /* Method Name   :	fetchUserMentorFunction
     * Description   :  This method is to fetch Current User Mentor Function
     * Return Type   :  String - Mentor Function
     * Parameters    :  Current User 
    */
    public static String fetchUserMentorFunction(User currentloggedInUser)
    {
    	List<Work_Queue_Setup__c> lWorkQ = [SELECT Id, Classification_Type__c, Classification_Type__r.Mentor_Function__c FROM Work_Queue_Setup__c WHERE Name =: currentloggedInUser.Current_Queue__c];
    	String sFunction='';
    	if(!lWorkQ.isEmpty())
        {	
        	sFunction = lWorkQ[0].Classification_Type__r.Mentor_Function__c;	        	
        }		
		return sFunction;
    }
    
    /* Method Name   :	getlstSubFunctions
     * Description   :  This method is to fetch Sub Functions for the current Logged in user
     * Return Type   :  List<SelectOption>
     * Parameters    :  Current User Mentor Function, Current User, None String(either None Or Blank)  
    */
    public static List<String> getlstSubFunctions(String sMentorFunction, User currentloggedInUser, String nonetype)
    {
       
	 	List<String> lSubFunc = new List<String>();		
    	List<string> lstSubFunctions = new List<string>();
		if(!String.isBlank(sMentorFunction))
		{			
			if(!String.isBlank(currentloggedInUser.Mentor_Sub_Function__c))
			{
				lstSubFunctions.add(currentloggedInUser.Mentor_Sub_Function__c);
			}
			if(sMentorFunction != HumConstants_LWC.HUMDefaultMentorFunction)
			{
				lstSubFunctions.add(nonetype);
			}
			
			List<MentorSubFunction__mdt> lstMetorsSubFunctions = [Select label from MentorSubFunction__mdt where Mentor_Function__c=:sMentorFunction ORDER BY label];								
			for(MentorSubFunction__mdt subFunc : lstMetorsSubFunctions)
			{
				if(subFunc.label!=currentloggedInUser.Mentor_Sub_Function__c)
				{
					lstSubFunctions.add(subFunc.label.trim());
				}
			}
			
			//sort the subfunction list 
			lstSubFunctions.sort();
			for(String subFunc: lstSubFunctions)
			{
				lSubFunc.add(subFunc);
			}
			
		}
    	
		return lSubFunc;
    }
    
}