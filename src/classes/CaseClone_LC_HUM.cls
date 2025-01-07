/***********************************************************************************************************
Apex Class Name  : CaseClone_LC_HUM
Version          : 1.0
Function         : This class is for Clone casefunctionality in lightning
Test Class       : CaseClone_LT_HUM
************************************************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gowthami Thota/MK Manoj                       09/01/2022                  Original Version
* Dinesh Subramaniyan                           02/27/2023                  US4274185- Systematically Attach Interaction to Cloned Cases
* Ajay Chakradhar                               07/27/2023                  US4854853- T1PRJ0865978 Case Management -(INC2432259) 
************************************************************************************************************/
public with sharing class CaseClone_LC_HUM {
    
    public static string sCaseNumber;
    public static string caseID;
    
    /**
    * This method is used to check the user workqueue value and clone the case 
    *
    * @param None       
    * @return  string
    */
    @AuraEnabled
    public static string cloneCase(string recordId, string interactionId)
    { 
       try
       { 
            //Get user initiating clone
            User oCurrentUser = [select Id, Name, Current_Queue__c from User where id =: Userinfo.getUserId()];
            
            //Check that the user has a work queue selected before creating the cloned case
            if(String.isBlank(oCurrentUser.Current_Queue__c)) 
            {
                return null;
            }
            else 
            {
               
            List<String> fieldsCase = new List<String>();
            List<String> lstFileds = new List<String>();
            lstFileds.addAll(HUMCaseCloneFieldsLC__c.getAll().keySet());
            String soqlQuery = 'SELECT ' ; 
            for(String field: lstFileds){
                soqlQuery +=  + field+',';
            }
          
            soqlQuery =  soqlQuery.removeEnd(',');
            soqlQuery += ' FROM Case WHERE ID = \'' + String.escapeSingleQuotes(recordId) + '\'' ;
                //Get record being cloned
                Case origCase = Database.query(soqlQuery);

                //Create clone
                Case newCase = origCase.clone();
                if(Schema.SObjectType.Case.fields.Case_Owner__c.isCreateable())	newCase.Case_Owner__c = oCurrentUser.Name;
                if(Schema.SObjectType.Case.fields.Owner_Queue__c.isCreateable()) newCase.Owner_Queue__c = oCurrentUser.Current_Queue__c;
                
                // Map of record types from Schema Describe. 
                Map<Id,String> mapRecordTypeId = new Map<Id,String>();
                Map<String,Id> mapRecordTypeName = new Map<String,Id>();
				for(RecordTypeInfo recType : Schema.SObjectType.Case.getRecordTypeInfos())
				{
				  mapRecordTypeId.put(recType.getRecordTypeId(),recType.getName());
				  mapRecordTypeName.put(recType.getName(),recType.getRecordTypeId());  
				}
             
                
                if(mapRecordTypeId.containskey(origCase.RecordTypeId) && GLOBAL_CONSTANT_HUM.CLONECLOSEDCASE_RECORDTYPEMAP_HUM.containskey(mapRecordTypeId.get(origCase.RecordTypeId)))
                {
            		String sRecordTypeName = GLOBAL_CONSTANT_HUM.CLONECLOSEDCASE_RECORDTYPEMAP_HUM.get(mapRecordTypeId.get(origCase.RecordTypeId));
            		if(mapRecordTypeName.containsKey(sRecordTypeName) && Schema.SObjectType.Case.fields.RecordTypeId.isCreateable())
            		{
            			newCase.RecordTypeId = mapRecordTypeName.get(sRecordTypeName);
            		}
                }
                
                //Commenting Changes for medicare enrollment cases, where Subtype and Record Type should also be copied over to cloned cases in lightning
                /*
                if(mapRecordTypeId.containsKey(origCase.RecordTypeId) && (mapRecordTypeId.get(origCase.RecordTypeId) == GLOBAL_CONSTANT_HUM.MEDICARE_CASE || mapRecordTypeId.get(origCase.RecordTypeId) == GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE))
                {
                    String sAccRecordTypeName = CaseDetails_LC_Hum.populateAccountRecordTypeName(origCase.AccountId);
                    if(Schema.SObjectType.Case.fields.SubType__c.isCreateable())	newCase.SubType__c = '';
                    if(Schema.SObjectType.Case.fields.RecordTypeId.isCreateable())	newCase.RecordTypeId = HUMUtilityHelper.getCaseRecordType(sAccRecordTypeName);                    
                }
                */           
               List<Work_Queue_Setup__c> lstOWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Name =: oCurrentUser.Current_Queue__c And IsActive__c = TRUE];
            
                if(!lstOWQS.isEmpty())
                {
                    Work_Queue_Setup__c oWQS = lstOWQS[0];
                    if(Schema.SObjectType.Case.fields.Service_Center__c.isCreateable())	newCase.Service_Center__c = oWQS.Service_Center__c; 
                    if(Schema.SObjectType.Case.fields.Department__c.isCreateable())	newCase.Department__c = oWQS.Department__c;           
                    if(oWQS.Classification_Type__c!=null && Schema.SObjectType.Case.fields.Classification_Type__c.isCreateable())         
                    newCase.Classification_Type__c = oWQS.Classification_Type__r.name; 
                }
                
                if(Schema.SObjectType.Case.fields.Verbal_Consent_Obtained__c.isCreateable())
                    newCase.Verbal_Consent_Obtained__c = origCase.Verbal_Consent_Obtained__c;
                  
                insert newCase;   

                sCaseNumber=newCase.CaseNumber;
                caseID=newCase.Id; 
                if(string.isNotBlank(interactionId))
                saveCaseinteractionData(interactionId, caseID);    
         return caseID;
        } 
    }
        catch(Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex,'CaseClone_LC_HUM','cloneCase');
           
           return null;
        }
        
    }
    
     /*
     * Method Name : getCurrentUserCurrentQueue
     * Description : retrieves the current user current queue 
     * Return type : String
     * Paramater   : None
     */
    @AuraEnabled
    public static String getUserCurrentQueue()
    {       
        User oCurrentUser = [select Id, Name, Current_Queue__c from User where id =: Userinfo.getUserId()];
        String currentQueue = oCurrentUser.Current_Queue__c; 
        String userName = oCurrentUser.Name;
        if(currentQueue != null){
        	return currentQueue;
        }
        return null;
    }

     /*
     * Method Name : saveCaseinteractionData
     * Description : Creates case interaction 
     * Return type : Void
     * Paramater   : Case Id and Interaction Id
     */
		public Static void saveCaseinteractionData(String interid, String caseId){
            Case_Interaction__c CaseInteraction = new Case_Interaction__c();
        if (interid !=null  && caseId!=null ){
         PharmacyCaseSave_LD_HUM.saveCaseInteractionRecord(CaseInteraction, interId, caseId);
        }
      }

}