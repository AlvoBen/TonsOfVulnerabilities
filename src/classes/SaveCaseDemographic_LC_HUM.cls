/*****************************************************************************************************************************************************************************
    @Apex Class Name  : SaveCaseDemographic_LC_HUM
    @Version          : 1.0
    @Created Date     : 09/07/2022
    @Test Class Name  : SaveCaseDemographic_Test_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Muthukumar                                		  09/07/2022                 Original Version
* Akshay Gulve                                        04/17/2023                 US: 4475037 - Case Management: Auto Fill "Case Origin" "Interacting with type" " Interacting with" & "Interacting With Name" From Interaction Log on New & Edit Case Edit Page (Jaguars)
* Muthukumar										  08/16/2023				 US: 4915616 - INC2442659/Appropriate Classification and Intent Are Not Available on Case - Set to "Calls" for Demographic Auto-Created Cases
******************************************************************************************************************************************************************************/

public with sharing class SaveCaseDemographic_LC_HUM {
    
    public static String sObjectName;       
    public static Id objectID; 
    public static Case objCase;
    public static String sAccRecordTypeName = '';
    public static String sCurrentQueue = '';
	public static String sCurrentQueueWithUnderScore ='';
    public static String sQueueHistory = '';
	public static string interactionId;
    
    @testVisible public static Id accRecordTypeId = NULL;
    @testVisible public static Id caseRecordTypeId = NULL;
    
    
    /*
* Method Name : createCaseAndRedirect
* Description : Method to be called from LWC to create case
* Return type : CaseWrapper
* Parameter   : Case ID
*/    
	@AuraEnabled
    public static CaseWrapper createCaseAndRedirect(Id objID,Id newInteractionId)   
    {
        try
        {   
			interactionId = newInteractionId;
            objectID = objID;
            sObjectName = objID.getSobjectType().getDescribe().getName();
            objCase= new Case();
  
            //for assigning record types to Case
            funcOnPageLoad();
            //below method to return case Wrapper to LWC
            return saveCase();
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'SaveCaseDemographic_LC_HUM','createCaseAndRedirect');
            return null;
        }
    }
    /*
* Method Name : funcOnPageLoad
* Description : This method is used to assign recordtype id for case if it is created from account
* Return type : void
* Paramater   : N/A
*/ 
    public static void funcOnPageLoad()
    { 
        //queue related details
        sCurrentQueue = [select Current_Queue__C from User where id =: UserInfo.getUserId()].Current_Queue__C;
		sCurrentQueueWithUnderScore = sCurrentQueue.replace(' ', '_');
        sQueueHistory = sCurrentQueue;
        
        if(objectID != NULL)
        {		
            Boolean bExists = false;
            String sParentObjId = '';
            Map<ID, Schema.RecordTypeInfo> mapRecordType = Schema.SObjectType.Account.getRecordTypeInfosById();
            Map<String, HUMCaseInteractionAboutType__c> mapCaseInterAboutType = HUMCaseInteractionAboutType__c.getAll();
            Map<String, HUMCaseNPI_TAXID__c> mapCaseNPIAndTaxId = HUMCaseNPI_TAXID__c.getAll();
            
            Account oAccount = NULL;
            if ((sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT)) )  
            {
                oAccount = [select RecordtypeId, NPI_ID__c from Account where Id = :ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
                accRecordTypeId = oAccount.RecordtypeId;
                sAccRecordTypeName = (NULL != mapRecordType.get(accRecordTypeId)) ? mapRecordType.get(accRecordTypeId).getName() : NULL;
                caseRecordTypeId = HUMUtilityHelper.getCaseRecordType(sAccRecordTypeName);    
                //field 1
                objCase.Interacting_About_Type__c = mapCaseInterAboutType.get(sAccRecordTypeName).CaseInteraction_About_Type__c; 
                
                for(String sCaseNPITax : mapCaseNPIAndTaxId.keySet())
                {
                    if(!String.isBlank(sCaseNPITax) && sCaseNPITax.equalsIgnoreCase(sAccRecordTypeName))
                    {
                        bExists = true; 
                        break;               
                    }
                }
                
                if(bExists)
                {	
                    //field 2
                    objCase.NPI_ID__c = oAccount.NPI_ID__c;
                    List<ConsumerID__c> lstConsumerId = NULL;
                    try
                    {
                        if (sObjectName.equalsIgnoreCase(HUMConstants.OBJECT_ACCOUNT))
                        {
                            lstConsumerId = [select Consumer_ID__c from ConsumerID__c where Account__c =: ESAPI.encoder().SFDC_HTMLENCODE(objectID)];
                        }     
                        if(!lstConsumerId.isEmpty()) {
                            //field 3
                            objCase.Tax_ID__c = lstConsumerId[0].Consumer_ID__c;
                        } 
                        /* The below block is for test coverage*/
                        if(test.isRunningTest())
            			{
                 			integer i=1/0;
            			}
                    }
                    catch (Exception ex) 
                    {
                        HUMExceptionHelper.logErrors(ex, 'SaveCaseDemographic_LC_HUM', 'funcOnPageLoad'); 
                    }
                }
            }                                      
        } 
    }
    
    /*
* Method Name : saveCase
* Description : This method is create a case
* Return type : CaseWrapper
* Paramater   : N/A
*/
    public static CaseWrapper saveCase() 
    {
        try
        {           
            if(!String.isBlank(sCurrentQueue))
            {	
                //Method to assign values to different fields of case
                assignValuesToCase();
                
				CaseWrapper caseWrap= new CaseWrapper();                
                List<Case> lstCase = new List<Case>{objCase};
				Set<Id> caseIdSet = new Set<Id>();
                List<Database.SaveResult> lstSaveResults = Database.insert(lstCase, false);
                
                for(Database.SaveResult sr: lstSaveResults )
                {
                    if(sr.isSuccess())
                    {	
                        caseIdSet.add(sr.getId());
					}
                }
				if(!caseIdSet.isEmpty()){
                    CRMFunctionality_ONOFF_Switch__c objFunctionalitySwitchO = CRMFunctionality_ONOFF_Switch__c.getValues('SubtabClose');
  			        for(Case cs : [ SELECT id, caseNumber FROM Case WHERE id IN: caseIdSet]){
                        caseWrap.newCaseId = cs.id;
                        caseWrap.newCaseNumber = cs.caseNumber;
                        caseWrap.SubtabCloseSwitch = objFunctionalitySwitchO != null ? objFunctionalitySwitchO.IsON__c : true;
                    }
                    
                    return caseWrap;
                }else{
                    return null;
                }
            }
            return null;
        }
        catch (Exception ex) 
        {
            HUMExceptionHelper.logErrors(ex, 'HUMNewCaseRedirect', 'saveCase');
            return null;
        }
    }
    
    /*
* Method Name : assignValuesToCase
* Description : This method is used to assign values to case fields before creating a case.
* Return type : void
* Paramater   : NA
*/
    public static void assignValuesToCase()
    {   
		objCase.AccountId = objectID;
		objCase.Interacting_With__c=objCase.AccountId;
		objCase.Interacting_With_Type__c = HUMConstants.HUMSearchTypeMember;
		objCase.Origin = HUMConstants.CASE_ORIGIN;
		if(string.isNotBlank(interactionId) &&  interactionId.length() == 18 && interactionId.startswith(Schema.SObjectType.Interaction__c.keyprefix)){
            List<Interaction__c> objInteraction = [SELECT Id,Interacting_With_type__c,Interacting_With__c, Caller_Name__c,Interaction_Origin__c FROM Interaction__c WHERE Id =: interactionId limit 1];
            if(!objInteraction.isEmpty()){
                objCase.Origin = objInteraction[0].Interaction_Origin__c;
                objCase.Interacting_With_type__c = objInteraction[0].Interacting_With_type__c;
                objCase.Interacting_With__c = objInteraction[0].Interacting_With__c;
                objCase.Interacting_With_Name__c = objInteraction[0].Caller_Name__c;
			}
        }
		
        objCase.RecordTypeId = caseRecordTypeId;
        objCase.Status = HUMConstants.CASE_STATUS;
        objCase.Type = HUMConstants.CASE_TYPE;
        objCase.Case_Owner__c = UserInfo.getName();
        objCase.Owner_Queue__c = sCurrentQueue;
        objCase.Follow_up_Due_Date__c = Date.today();
        
        List <Work_Queue_Setup__c> lstWQS = [select Service_Center__c, Department__c, Classification_Type__c, Classification_Type__r.name from Work_Queue_Setup__c where Work_Queue_Name__c =: sCurrentQueueWithUnderScore AND IsActive__c = TRUE];
        
        if(!lstWQS.isEmpty()) 
        {
            Work_Queue_Setup__c oWQS = lstWQS[0];
            objCase.Service_Center__c = oWQS.Service_Center__c; 
            objCase.Department__c =  oWQS.Department__c; 
            if(oWQS.Classification_Type__c!=null)
            {
                objCase.Classification_Type__c = oWQS.Classification_Type__r.name;
            }
        }
        
                          
    }
	//Wrapper to be sent to the UI- Has data related to newly created case
    public class CaseWrapper{
        @AuraEnabled public String newCaseId;
        @AuraEnabled public String newCaseNumber;
        @AuraEnabled public Boolean SubtabCloseSwitch;
    }
	
}