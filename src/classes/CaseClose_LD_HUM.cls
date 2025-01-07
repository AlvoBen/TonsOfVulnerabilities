/*******************************************************************************************************************************
Apex Class Name :    CaseClose_LD_HUM
Version: 1.0
Created On      : 06/15/2022
Function        : This class Is for Case Close Feature.
Test Class      : CaseClose_LT_HUM

        Modification Log:    
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Gowthami 											                				Original Version
*	  1.1					 Nirmal Garg											09/01/2023				    4908765 Changes
******************************************************************************************************************************************************************************/


Public with sharing class CaseClose_LD_HUM {
    public static final String CASE_CLASSIFICATION_TYPE_CALLRSO = 'Calls (RSO)';

/**
*  @Function - Method to get Case record
*  @name fetchCaseRecord
*  @param String
*  @return Case
*/
    public static Case fetchCaseRecord(String sCaseID)
    {
        try{
        return [select id,CaseNumber,Status,AccountId,Account.Enterprise_Id__c,Classification_Type__c,Classification_Id__c,Intent_Id__c,G_A_Rights_Given__c,Complaint__c,Type,RecordType.name,CreatedBy.IsActive,CreatedBy.id,CreatedBy.Current_Queue__c,CreatedBy.name,Member_Plan_Id__c,CTCI_List__r.Classification_Type__r.Name,
                Member_Plan_ID__r.Product_Type__c,Member_Plan_ID__r.Product__c,Member_Plan_ID__r.Issue_State__c,Member_Plan_Id__r.Plan.Product__r.Major_LOB__c,
                Interacting_With_Name__c,OwnerId,Interacting_With__c,Member_Plan_ID__r.Product_Type_Code__c,Owner_Queue__c,Created_By_Queue__c,Previous_Owner_Queue__c,Origin,CTCI_List__r.Classification__r.name,CTCI_List__r.Intent__r.name,
                General_Case__c,Contract_Protected_Case__c,DST_Case__c,CTCI_List__r.Service_Model_Type__c,HO_Dental_Case__c,HO_Medical_Case__c,CoKY_Case__c,Tenant_Id__c,Interacting_About_Type__c,Priority, User__c,Transferred_From__c,Transferred_Date__c,Case_Owner__c
                from case where id=:sCaseID LIMIT 1];
        }
        catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'CaseClose_LD_HUM', 'fetchCaseRecord');
            return null;
        }
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
    
    public static String getCurrentUserQueue()
    {
        Id userId = UserInfo.getUserId();
        return [Select Id,Name,Current_Queue__c from User where Id=:userId].Current_Queue__c;
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
}