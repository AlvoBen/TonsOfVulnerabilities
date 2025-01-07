/***************************************************************************************************************************************************************************
Apex Class Name  : CreateCaseAccount_D_HUM
Version          : 1.0
Created Date     : May 24 2017
Function         : This class is used to update case records asynchronously.
Test Class       : HUMCaseServiceTest 
****************************************************************************************************************************************************************************
Modification Log:  

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Sumit Sharma                 103536                      05/24/2017                  Original Version
 * Mohammed Noor                112270                      07/14/2017                  REQ - 315928 - Creditable Coverage Intake - Case Service
 * Mohammed Noor                114118                      07/26/2017                  REQ - 315928 - Creditable Coverage Intake - Case Service - Made changes to save
 *                                                                                      encrypted messages in CaseComments.
 * Moshitha Gunasekaran         211055                      04/04/2018                  REQ -359138 - TECH - SF - CA Ticket 7119893 - Made changes to accomodate 
 *                                                                                      duplicate issues and associate acccount
 * Suresh Gottipati                                         07/31/2018                  Added Encryption code for account fields                 
 * Moshitha Gunasekaran         262118                      07/25/2018                  REQ - 350472 ID Number field on the Unknown Account tab
 * Sunil Bukya                                              12/26/2018                  Removed Dpass callout code changes
 ****************************************************************************************************************************************************************************
*/

public with sharing class CreateCaseAccount_D_HUM 
{    
    //Store information related to Record type Names
    static final Map<String,String> mapRecordtypeInfo= new map<String,String>
    {'UM' => HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c
    };
    //External ID format
    static final Map<string,string> mapExternalIDFormat = new map<String,String>{'UM' => HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c};
    
    //Method to upsert account with external id
     public static Map<String,String> createAccount(HUMCreateCaseRequestHelper caseRequestObject,String sInteractingType, String sExternalId,Id caseId)
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

            if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))
            {
                if (Schema.sObjectType.Account.fields.firstName.isCreateable() || Schema.sobjectType.Account.fields.firstName.isUpdateable())
                oAccount.firstName = caseRequestObject.sInteractingAboutFirstName;
                if (Schema.sObjectType.Account.fields.lastName.isCreateable() || Schema.sobjectType.Account.fields.lastName.isUpdateable()) 
                oAccount.lastName = caseRequestObject.sInteractingAboutLastName;
                if (Schema.sObjectType.Account.fields.personmailingstreet.isCreateable() || Schema.sobjectType.Account.fields.personmailingstreet.isUpdateable())   
                oAccount.personmailingstreet = caseRequestObject.sInteractingAboutAddressLine1;
                if (Schema.sObjectType.Account.fields.BirthDate__c.isCreateable() || Schema.sobjectType.Account.fields.BirthDate__c.isUpdateable()) 
                oAccount.BirthDate__c = caseRequestObject.sInteractingAboutDOB;
                if (Schema.sObjectType.Account.fields.personmailingcity.isCreateable() || Schema.sobjectType.Account.fields.personmailingcity.isUpdateable()) 
                oAccount.personmailingcity = caseRequestObject.sInteractingAboutCity;
                if (Schema.sObjectType.Account.fields.PersonMailingStateCode.isCreateable() || Schema.sobjectType.Account.fields.PersonMailingStateCode.isUpdateable())
                oAccount.PersonMailingStateCode = caseRequestObject.sInteractingAboutState;
                if (Schema.sObjectType.Account.fields.personmailingpostalcode.isCreateable() || Schema.sobjectType.Account.fields.personmailingpostalcode.isUpdateable()) 
                oAccount.personmailingpostalcode = caseRequestObject.sInteractingAboutZipCode;
                if (Schema.sObjectType.Account.fields.Individual_First_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_First_Name__C.isUpdateable()) 
                oAccount.Individual_First_Name__C = caseRequestObject.sInteractingAboutFirstName;
                if (Schema.sObjectType.Account.fields.Individual_Last_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_Last_Name__C.isUpdateable())   
                oAccount.Individual_Last_Name__C = caseRequestObject.sInteractingAboutLastName;
                interactingId = caseRequestObject.sInteractingAboutId;
                interactingIdType = caseRequestObject.sInteractingAboutIdType;
                
            }
            else if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))
            {
                if (Schema.sObjectType.Account.fields.firstName.isCreateable() || Schema.sobjectType.Account.fields.firstName.isUpdateable())
                oAccount.firstName = caseRequestObject.sInteractingWithFirstName;
                if (Schema.sObjectType.Account.fields.lastName.isCreateable() || Schema.sobjectType.Account.fields.lastName.isUpdateable()) 
                oAccount.lastName = caseRequestObject.sInteractingWithLastName;
                if (Schema.sObjectType.Account.fields.personmailingstreet.isCreateable() || Schema.sobjectType.Account.fields.personmailingstreet.isUpdateable())   
                oAccount.personmailingstreet = caseRequestObject.sInteractingWithAddressLine1;
                if (Schema.sObjectType.Account.fields.BirthDate__c.isCreateable() || Schema.sobjectType.Account.fields.BirthDate__c.isUpdateable())
                oAccount.BirthDate__c = caseRequestObject.sInteractingWithDOB;
                if (Schema.sObjectType.Account.fields.personmailingcity.isCreateable() || Schema.sobjectType.Account.fields.personmailingcity.isUpdateable()) 
                oAccount.personmailingcity = caseRequestObject.sInteractingWithCity;
                if (Schema.sObjectType.Account.fields.PersonMailingStateCode.isCreateable() || Schema.sobjectType.Account.fields.PersonMailingStateCode.isUpdateable())
                oAccount.PersonMailingStateCode = caseRequestObject.sInteractingWithState;
                if (Schema.sObjectType.Account.fields.personmailingpostalcode.isCreateable() || Schema.sobjectType.Account.fields.personmailingpostalcode.isUpdateable()) 
                oAccount.personmailingpostalcode = caseRequestObject.sInteractingWithZipCode;
                if (Schema.sObjectType.Account.fields.Individual_First_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_First_Name__C.isUpdateable()) 
                oAccount.Individual_First_Name__C = caseRequestObject.sInteractingWithFirstName;
                if (Schema.sObjectType.Account.fields.Individual_Last_Name__C.isCreateable() || Schema.sobjectType.Account.fields.Individual_Last_Name__C.isUpdateable())
                oAccount.Individual_Last_Name__C= caseRequestObject.sInteractingWithLastName;
                interactingId = caseRequestObject.sInteractingWithId;
                interactingIdType = caseRequestObject.sInteractingWithIdType;
                
            }
         	if (Schema.sObjectType.Account.fields.General_Account__c.isCreateable() || Schema.sobjectType.Account.fields.General_Account__c.isUpdateable()) 
            oAccount.General_Account__c = True;
          	if (Schema.sObjectType.Account.fields.Tenant_Id__c.isCreateable() || Schema.sobjectType.Account.fields.Tenant_Id__c.isUpdateable())  
            oAccount.Tenant_Id__c = '00';
           	if (Schema.sObjectType.Account.fields.ETL_Record_Deleted__c.isCreateable() || Schema.sobjectType.Account.fields.ETL_Record_Deleted__c.isUpdateable())   
            oAccount.ETL_Record_Deleted__c = false; 
           	if (Schema.sObjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Account_External_ID__c.isUpdateable())  
            oAccount.Account_External_ID__c = sExternalId;                                     
            Schema.sObjectField schemaExternalId = Account.Account_External_ID__c;
            boolean bAcctExist = false;
            if(sExternalId != GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
            {
                String queryAcct = 'Select Id from Account where Account_External_ID__c = \'' + string.escapeSingleQuotes(sExternalId) + '\' LIMIT 1';
                List<Account> objAcct = (List<Account>)Database.Query(queryAcct);
                if(!objAcct.isEmpty())
                {
                    interactingAccountMap.put(sInteractingType,objAcct[0].Id);
                    insertConsumerTaxIds(interactingId,objAcct[0].Id,interactingIdType,caseRequestObject.bAsynchronousProcess);
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
              if(sExternalId == GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
              {
              	if (Schema.sObjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Account_External_ID__c.isUpdateable())  
                oAccount.Account_External_ID__c = sExternalId + sAccountID;
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
                    if(caseRequestObject.bAsynchronousProcess)
                    {
                        CreateCase_D_HUM.createCaseComment(caseRequestObject.sAccountErrMsg, caseId);
                        CreateCase_D_HUM.logError(result1.getErrors(), 'createAccount', 'Account', result1.getId());
                    }
                    else
                    {
                        throw new HUMCustomException('Issue with Unknow Member External ID Update');
                    }
                }
                
                  }
                  else
                  {
                        //Insert Consumer IDs
                        insertConsumerTaxIds(interactingId,sAccountID,interactingIdType,caseRequestObject.bAsynchronousProcess);
                  }
                    interactingAccountMap.put(sInteractingType,sAccountID);
                }
                /*If the Unknown member creation failed,
                    For Synchronous Call -> throw a custom Exception which will bubble up and will be handled in the calling Method
                    For Asynchronous Call -> Log the error in both CaseComment and ErrorLogs and continue processing. 
                */
                else
                {               
                    Boolean bMemberExist = false;
                    for(Database.Error err : result.getErrors())
                    {
                        if(String.ValueOf(err.getStatusCode()).contains('DUPLICATE_VALUE'))
                        {
                            List<string> errMessages = (err.getMessage()).split('id:');
                            if(String.isNotBlank(errMessages[1]) && ((errMessages[1].trim()).length() == 15 || (errMessages[1].trim()).length() == 18))
                            {
                                interactingAccountMap.put(sInteractingType ,String.valueOf(errMessages[1].trim()));
                                bMemberExist = true;
                                break;
                            }
                        }
                    }
                    if(!bMemberExist)
                    {
                        if(caseRequestObject.bAsynchronousProcess)
                        {
                            CreateCase_D_HUM.createCaseComment(caseRequestObject.sAccountErrMsg, caseId);
                            CreateCase_D_HUM.logError(result.getErrors(), 'createAccount', 'Account', result.getId());
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
             if(caseRequestObject.bAsynchronousProcess)
             {
                CreateCase_D_HUM.createCaseComment(ex.getMessage(),caseId);
                HUMExceptionHelper.logErrors(ex,'CreateCaseAccount_D_HUM','createAccount');  
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
      	if (Schema.sObjectType.ConsumerId__c.fields.Consumer_Id__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.Consumer_Id__c.isUpdateable())   
        objConsumer.Consumer_Id__c = sConsumerID.toUppercase();
  		if (Schema.sObjectType.ConsumerId__c.fields.ID_Type__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.ID_Type__c.isUpdateable())  
        objConsumer.ID_Type__c = idType;
       	if (Schema.sObjectType.ConsumerId__c.fields.Account__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.Account__c.isUpdateable())   
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
        if (Schema.sObjectType.ConsumerId__c.fields.Consumer_External_Id__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.Consumer_External_Id__c.isUpdateable()) 
        objConsumer.Consumer_External_Id__c = externalId;                                     
        Schema.sObjectField schemaExternalId = ConsumerId__c.Consumer_External_Id__c;
        Database.upsertResult consumerResult = Database.upsert(objConsumer,schemaExternalId, false);
        if(!consumerResult.isSuccess())
        {
            if(bAsynchProcess)
            {
                CreateCase_D_HUM.logError(consumerResult.getErrors(), 'insertConsumerTaxIds', 'ConsumerID__c', consumerResult.getId());
            }
            else
            {
                throw new HUMCustomException('Issue with Unknown Member ID Update in Consumer');
            }
        }
        
    }
}