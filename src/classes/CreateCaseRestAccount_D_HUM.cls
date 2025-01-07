/***************************************************************************************************************************************************************************
Apex Class Name  : CreateCaseRestAccount_D_HUM
Version          : 1.0
Created Date     : March 15 2018
Function         : This class is used to update case records asynchronously.
Test Class       :  HUMCaseServiceRestTest
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran           215336                    03/15/2018                  Original Version
 * Moshitha Gunasekaran           230876                    05/19/2018                  Defect fix for 358913 to associate policy for Member
 * Sunil Bukya                    263423                    07/27/2018                  REQ - 371288 - Implemented Account Fields platform encryption changes.
 * Moshitha Gunasekaran           262118                    07/25/2018                  REQ - 350472 ID Number field on the Unknown Account tab
 * Sunil Bukya                    321985                    12/26/2018                  Removed Dpaas callout code changes
 ****************************************************************************************************************************************************************************
*/

public with sharing class CreateCaseRestAccount_D_HUM 
{    
    //Store information related to Record type Names
    static final Map<String,String> mapRecordtypeInfo= new map<String,String>
    {'UM' => HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c
    };
    //External ID format
    static final Map<string,string> mapExternalIDFormat = new map<String,String>{'UM' => HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c};
    //Method to upsert account with external id
     public static Map<String,String> createAccount(CaseServiceRestCreateRequest_DTO_HUM caseRequestObject,String interactingType, String externalId,Id caseId)
     {
        Map<String,String> interactingAccountMap = new Map<String,String>();
        ID recordTypeID = mapRecordtypeInfo.containsKey('UM') ? HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),mapRecordtypeInfo.get('UM')) : null;
        
       try
       {
            Account oAccount = new Account();
            if (Schema.sObjectType.Account.fields.recordTypeID.isCreateable() || Schema.sobjectType.Account.fields.recordTypeID.isUpdateable())
            oAccount.recordTypeID=recordTypeID;
            String interactingId = '';
            String interactingIdType = '';     

            if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))
            {
                if (Schema.sObjectType.Account.fields.firstName.isCreateable() || Schema.sobjectType.Account.fields.firstName.isUpdateable())
                oAccount.firstName = caseRequestObject.CreateCaseRequest.InteractingAboutFirstName;
                if (Schema.sObjectType.Account.fields.lastName.isCreateable()|| Schema.sobjectType.Account.fields.lastName.isUpdateable())
                oAccount.lastName = caseRequestObject.CreateCaseRequest.InteractingAboutLastName;
                if (Schema.sObjectType.Account.fields.personmailingstreet.isCreateable() || Schema.sobjectType.Account.fields.personmailingstreet.isUpdateable())
                oAccount.personmailingstreet = caseRequestObject.CreateCaseRequest.InteractingAboutAddressLine1;
                if (Schema.sObjectType.Account.fields.BirthDate__c.isCreateable() || Schema.sobjectType.Account.fields.BirthDate__c.isUpdateable())
                oAccount.BirthDate__c = caseRequestObject.CreateCaseRequest.InteractingAboutDOB;
                if (Schema.sObjectType.Account.fields.personmailingcity.isCreateable() || Schema.sobjectType.Account.fields.personmailingcity.isUpdateable())
                oAccount.personmailingcity = caseRequestObject.CreateCaseRequest.InteractingAboutCity;
                if (Schema.sObjectType.Account.fields.PersonMailingStateCode.isCreateable() || Schema.sobjectType.Account.fields.PersonMailingStateCode.isUpdateable())
                oAccount.PersonMailingStateCode = caseRequestObject.CreateCaseRequest.InteractingAboutState;
                 if (Schema.sObjectType.Account.fields.personmailingpostalcode.isCreateable() || Schema.sobjectType.Account.fields.personmailingpostalcode.isUpdateable())
                oAccount.personmailingpostalcode = caseRequestObject.CreateCaseRequest.InteractingAboutZipCode;
                if (Schema.sObjectType.Account.fields.Individual_First_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_First_Name__C.isUpdateable())
                oAccount.Individual_First_Name__C = caseRequestObject.CreateCaseRequest.InteractingAboutFirstName;
                if (Schema.sObjectType.Account.fields.Individual_Last_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_Last_Name__C.isUpdateable())
                oAccount.Individual_Last_Name__C = caseRequestObject.CreateCaseRequest.InteractingAboutLastName;
                interactingId = caseRequestObject.CreateCaseRequest.InteractingAboutId;
                interactingIdType = caseRequestObject.CreateCaseRequest.InteractingAboutIdType;
            }
            else if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))
            {
                if (Schema.sObjectType.Account.fields.firstName.isCreateable() || Schema.sobjectType.Account.fields.firstName.isUpdateable())
                oAccount.firstName = caseRequestObject.CreateCaseRequest.InteractingWithFirstName;
                if (Schema.sObjectType.Account.fields.lastName.isCreateable()|| Schema.sobjectType.Account.fields.lastName.isUpdateable()) 
                oAccount.lastName = caseRequestObject.CreateCaseRequest.InteractingWithLastName;
                if (Schema.sObjectType.Account.fields.personmailingstreet.isCreateable() || Schema.sobjectType.Account.fields.personmailingstreet.isUpdateable())
                oAccount.personmailingstreet = caseRequestObject.CreateCaseRequest.InteractingWithAddressLine1 ;
                if (Schema.sObjectType.Account.fields.BirthDate__c.isCreateable() || Schema.sobjectType.Account.fields.BirthDate__c.isUpdateable())
                oAccount.BirthDate__c = caseRequestObject.CreateCaseRequest.InteractingWithDOB;
                if (Schema.sObjectType.Account.fields.personmailingcity.isCreateable() || Schema.sobjectType.Account.fields.personmailingcity.isUpdateable())
                oAccount.personmailingcity = caseRequestObject.CreateCaseRequest.InteractingWithCity;
                if (Schema.sObjectType.Account.fields.PersonMailingStateCode.isCreateable() || Schema.sobjectType.Account.fields.PersonMailingStateCode.isUpdateable())
                oAccount.PersonMailingStateCode = caseRequestObject.CreateCaseRequest.InteractingWithState;
                if (Schema.sObjectType.Account.fields.personmailingpostalcode.isCreateable() || Schema.sobjectType.Account.fields.personmailingpostalcode.isUpdateable())
                oAccount.personmailingpostalcode = caseRequestObject.CreateCaseRequest.InteractingWithZipCode;
                if (Schema.sObjectType.Account.fields.Individual_First_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_First_Name__C.isUpdateable())
                oAccount.Individual_First_Name__C = caseRequestObject.CreateCaseRequest.InteractingWithFirstName;
                if (Schema.sObjectType.Account.fields.Individual_Last_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_Last_Name__C.isUpdateable())  
                oAccount.Individual_Last_Name__C = caseRequestObject.CreateCaseRequest.InteractingWithLastName;
                interactingId = caseRequestObject.CreateCaseRequest.InteractingWithId;
                interactingIdType = caseRequestObject.CreateCaseRequest.InteractingWithIdType;
            }
      		if (Schema.sObjectType.Account.fields.General_Account__c.isCreateable() || Schema.sobjectType.Account.fields.General_Account__c.isUpdateable())
            oAccount.General_Account__c = True;
       		if (Schema.sObjectType.Account.fields.Tenant_Id__c.isCreateable() || Schema.sobjectType.Account.fields.Tenant_Id__c.isUpdateable())
            oAccount.Tenant_Id__c = '00';
           	if (Schema.sObjectType.Account.fields.ETL_Record_Deleted__c.isCreateable() || Schema.sobjectType.Account.fields.ETL_Record_Deleted__c.isUpdateable())
            oAccount.ETL_Record_Deleted__c = false;  
      		if (Schema.sObjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Account_External_ID__c.isUpdateable())
            oAccount.Account_External_ID__c = externalId;                                     
            Schema.sObjectField schemaExternalId = Account.Account_External_ID__c;
            boolean bAcctExist = false;
            if(externalId != GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
            {
                String queryAcct = 'Select Id from Account where Account_External_ID__c = \'' + string.escapeSingleQuotes(externalId) + '\' LIMIT 1';
                List<Account> objAcct = (List<Account>)Database.Query(queryAcct);
                if(!objAcct.isEmpty() && objAcct[0].Id != null && String.isNotBlank(objAcct[0].Id))
                {
                    interactingAccountMap.put(interactingType,objAcct[0].Id);
                    insertConsumerTaxIds(interactingId,objAcct[0].Id,interactingIdType,caseRequestObject.CreateCaseRequest.AsynchronousProcess);
                    bAcctExist = true;
                }
            }
            if (!bAcctExist)
            {
                Database.upsertResult result = Database.upsert(oAccount,schemaExternalId,false); 
            
                //If the Unknown member creation was successful, continue with Account External ID 
                if(result.isSuccess())
                {
                  String sAccountID = result.getId();                
                  if(externalId == GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
                  {
                	if (Schema.sObjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Account_External_ID__c.isUpdateable())
                    oAccount.Account_External_ID__c = externalId + sAccountID;
                   	if (Schema.sObjectType.Account.fields.ID.isCreateable() || Schema.sobjectType.Account.fields.ID.isUpdateable())
                    oAccount.ID = sAccountID ;                        
                    Schema.sObjectField schemaAccountId = Account.ID;                 
                
                    Database.upsertResult result1 = Database.upsert(oAccount,schemaAccountId,false);
                
                    /*If the Unknown member creation failed,
                    For Synchronous Call -> throw a custom Exception which will bubble up and will be handled in the calling Method
                    For Asynchronous Call -> Log the error in both CaseComment and ErrorLogs and continue processing. 
                    */
                    if(!result1.isSuccess())
                    {                   
                        if(caseRequestObject.CreateCaseRequest.AsynchronousProcess)
                        {
                            CreateCaseRest_D_HUM.createCaseComment(caseRequestObject.CreateCaseRequest.AccountErrMsg, caseId);
                            CreateCaseRest_D_HUM.logError(result1.getErrors(), 'createAccount', 'Account', result1.getId());
                        }
                        else
                        {
                            throw new HUMCustomException('Issue with Unknown Member External ID Update');
                        }
                    }
                
                  }
                  else
                  {
                        //Insert Consumer IDs
                        insertConsumerTaxIds(interactingId,sAccountID,interactingIdType,caseRequestObject.CreateCaseRequest.AsynchronousProcess);
                  }
                    interactingAccountMap.put(interactingType,sAccountID);
                }
                /*If the Unknown member creation failed,
                    For Synchronous Call -> throw a custom Exception which will bubble up and will be handled in the calling Method
                    For Asynchronous Call -> Log the error in both CaseComment and ErrorLogs and continue processing. 
                */
                else
                {
                    Boolean memberExist = false;
                    for(Database.Error err : result.getErrors())
                    {
                        if(String.ValueOf(err.getStatusCode()).contains('DUPLICATE_VALUE'))
                        {
                            List<string> errMessages = (err.getMessage()).split('id:');
                            if(String.isNotBlank(errMessages[1]) && ((errMessages[1].trim()).length() == 15 || (errMessages[1].trim()).length() == 18))
                            {
                                interactingAccountMap.put(interactingType ,String.valueOf(errMessages[1].trim()));
                                memberExist = true;
                                break;
                            }
                        }
                    }
                    if(!memberExist)
                    {               
                        if(caseRequestObject.CreateCaseRequest.AsynchronousProcess)
                        {
                            CreateCaseRest_D_HUM.createCaseComment(caseRequestObject.CreateCaseRequest.AccountErrMsg, caseId);
                            CreateCaseRest_D_HUM.logError(result.getErrors(), 'createAccount', 'Account', result.getId());
                        }
                        else
                        {
                            throw new HUMCustomException('Issue with Unknown Member Creation');
                        } 
                    }                              
                }
             } 
         }
         catch(Exception ex)
         {                          
             /* For Synchronous Call -> throw a custom Exception which will bubble up and will be handled in the calling Method
                For Asynchronous Call -> Log the error in both CaseComment and ErrorLogs and continue processing.  
             */ 
             if(caseRequestObject.CreateCaseRequest.AsynchronousProcess)
             {
                CreateCaseRest_D_HUM.createCaseComment(ex.getMessage(),caseId);
                HUMExceptionHelper.logErrors(ex,'CreateCaseRestAccount_D_HUM','createAccount');  
             }             
             else
             {
                throw ex;               
             }                 
         }
        return interactingAccountMap;
    }

    /*
    * Method Name   :    insertConsumerTaxIds
    * Description   :    Insert the Consumer TAXIDs based on the Account ID
    * Return Type   :    List<ConsumerId__c>
    * Parameters    :    String, ID
    */
    @testVisible private static void insertConsumerTaxIds(String sConsumerID,ID accID, String idType, Boolean bAsynchProcess)
    {
        ConsumerId__c objConsumer = new ConsumerId__c();
        String accountExternalId = HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c;
        if(String.isNotBlank(idType) && idType.equalsIgnoreCase('Member-Id-Base'))
        {
            idType = 'HumanaId';
        }
      	if (Schema.sObjectType.ConsumerId__c.fields.Consumer_Id__c.isCreateable() || Schema.sobjectType.ConsumerId__c.fields.Consumer_Id__c.isUpdateable())
        objConsumer.Consumer_Id__c = sConsumerID.toUppercase();
     	if (Schema.sObjectType.ConsumerId__c.fields.ID_Type__c.isCreateable() || Schema.sobjectType.ConsumerId__c.fields.ID_Type__c.isUpdateable())
        objConsumer.ID_Type__c = idType;
      	if (Schema.sObjectType.ConsumerId__c.fields.Account__c.isCreateable() || Schema.sobjectType.ConsumerId__c.fields.Account__c.isUpdateable())
        objConsumer.Account__c = accId;
        String sPipeCharacter = HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
        String externalId = '';
        if(idType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM)
        {
            externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_SSN + accId;
        }
        else if(idType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM)
        {
            externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICARE + accId;
        }
        else if(idType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM)
        {
            externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICAID + accId;
        }
        else
        {
            externalId = accountExternalId + idType + sPipeCharacter + accId;   
        }
        if (Schema.sObjectType.ConsumerId__c.fields.Consumer_External_Id__c.isCreateable())
        objConsumer.Consumer_External_Id__c = externalId;                                     
        Schema.sObjectField schemaExternalId = ConsumerId__c.Consumer_External_Id__c;
        Database.upsertResult consumerResult = Database.upsert(objConsumer,schemaExternalId, false);
        if(!consumerResult.isSuccess())
        {
            if(bAsynchProcess)
            {
                CreateCaseRest_D_HUM.logError(consumerResult.getErrors(), 'insertConsumerTaxIds', 'ConsumerID__c', consumerResult.getId());
            }
            else
            {
                throw new HUMCustomException('Issue with Unknown Member ID Update in Consumer');
            }
        }
    }
}