/*******************************************************************************************************************************
Apex Class Name : CaseTransfer_LD_HUM
Version         : 1.0
Created On      : 02/24/2022
Function        : This class is for Case Transfer Feature.
Test Class      : CaseTransfer_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											02/24/2022                 Original Version
******************************************************************************************************************************************************************************/

public with sharing class CaseTransfer_LD_HUM {
    
    public static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';
    public static Integer iLimitValue = GLOBAL_CONSTANT_HUM.LIMITVALUE_HUM;

    /**
*  @Function - Method to get Case record
*  @name fetchCaseRecord
*  @param String
*  @return Case
*/
    public static Case fetchCaseRecord(String sCaseID)
    {
        return [select id,CaseNumber,Status,AccountId,Classification_Type__c,Classification_Id__c,Intent_Id__c,Type,RecordType.name,CreatedBy.IsActive,CreatedBy.id,CreatedBy.Current_Queue__c,CreatedBy.name,Member_Plan_Id__c,CTCI_List__r.Classification_Type__r.Name,
                Member_Plan_ID__r.Product_Type__c,Member_Plan_ID__r.Product__c,Member_Plan_Id__r.Plan.Product__r.Major_LOB__c,Interacting_With_Type__c,Escalation_Indicator__c,Escalation_Indicator_Date__c,
                Interacting_With_Name__c,OwnerId,Interacting_With__c,Member_Plan_ID__r.Product_Type_Code__c,Owner_Queue__c,Created_By_Queue__c,Previous_Owner_Queue__c,Origin,CTCI_List__r.Classification__r.name,CTCI_List__r.Intent__r.name,
                General_Case__c,Contract_Protected_Case__c,DST_Case__c,CTCI_List__r.Service_Model_Type__c,HO_Dental_Case__c,HO_Medical_Case__c,CoKY_Case__c,Tenant_Id__c,Interacting_About_Type__c,Priority, User__c,Transferred_From__c,Transferred_Date__c,Case_Owner__c
                from case where id=:sCaseID LIMIT 1];
    }
        /**
*  @Function - Method to get CTCI record
*  @name fetchCTCIRecord
*  @param String
*  @return Case
*/
    public static List<CTCI_Junction__c> fetchCTCIRecord(Case objCase)
    {
        List<PermissionSetAssignment> lstRSOHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:CaseEditConstants_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
        String caseClassificationType = '';
        // if logged in user has 206 permission set then the default value of classification will be Call(RSO) and HP log,service model type will be evaluated based on call(RSO)
        if(lstRSOHPPermissionSet != null && !lstRSOHPPermissionSet.isEmpty()){
            caseClassificationType = CaseEditConstants_HUM.CASE_CLASSIFICATION_TYPE_CALLRSO;
            
        }else{
            caseClassificationType = objCase.Classification_Type__c;
        }
    	return [SELECT Id,Humana_Pharmacy_Log_Code__c,Service_Model_Type__c FROM CTCI_Junction__c WHERE Classification__c = :objCase.Classification_Id__c AND Intent__c =:objCase.Intent_Id__c AND (Humana_Pharmacy_Log_Code__c !=null OR Service_Model_Type__c !=null) AND Classification_Type__r.name =:caseClassificationType];
    }
    /*
* Method Name : getCurrentUserProfileName
* Description : get current user profile name.
* Return type : String
* Parameter   : NA
*/
    public static String getCurrentUserProfileName()
    {
        Id profileId=userinfo.getProfileId();
        return [Select Id,Name from Profile where Id=:profileId].Name;
    }
 
 /**
* <p>
* Function: Method to fetch log Node Code on the basis of Classifcation Type
* @param  String, Boolean, Boolean
* @return List<String>
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
* Method to get Case record
*  @name getMapWorkQueueSetup
*  @param NA
*  @return List<Work_Queue_Setup__c>
*/
    public static List<Work_Queue_Setup__c> getMapWorkQueueSetup()
    {
        return [Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, HO_Dental__c, HO_Medical__c,Contract_Protected_Data__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId,Work_Queue_Guidance__c from Work_Queue_Setup__c 
        Where IsActive__c = TRUE limit :iLimitValue];
    }
     
    /**
*  @Function -Method to get Work Queue Junction Object
*  @name fetchWorkQueueJunction
*  @param String
*  @return List<Work_Queue_Setup__c>
*/   
    public static List<Work_Queue_Junction__c> fetchWorkQueueJunction(String sWorkQueueSetUpName)
    {
        return [select Work_Queue_Setup_Name__c,Work_Queue_Setup_Name__r.Name, Work_Queue_Topic_Name__r.Name, Work_Queue_Topic_Guidance__c from Work_Queue_Junction__c where 
        Work_Queue_Setup_Name__c =: sWorkQueueSetUpName and Work_Queue_Topic_Name__r.IsActive__c = true];
    }
    
    /**
* Method to get list of permission set
*  @name getRSOPermissionSetList
*  @param NA
*  @return List<PermissionSetAssignment>
*/
    public static List<PermissionSetAssignment> getRSOPermissionSetList()
    {
        return [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
    }
    
    public static List<CaseHistory> fetchCaseHistoryRecords(String fieldName, String caseId, Integer limitNo)
    {
        return [SELECT  NewValue, OldValue, CreatedBy.Id FROM CaseHistory where Field =:fieldName and caseid =:caseId ORDER BY CreatedDate DESC limit: limitNo];
    }
    
    public static List<User> fetchUser(String sPreviousUserId)
    {
        return [SELECT Name, Id, IsActive, Current_Queue__c FROM User WHERE Id = : sPreviousUserId];
    }
}