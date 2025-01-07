/**************************************************************************************************************************************************************************************
Apex Class Name  : lwcApexController
Version          : 1.0
Created Date     : June 22 2021
Function         : Controller of Unknown Account- Member . Read the parameter values and assign to the respective fields on Account
Test Class       : lwcApexControllerTest
 **************************************************************************************************************************************************************************************
Modification Log:
 * Developer Name           Code Review                    Date                       Description
 *-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Anil Kumar                              07/21/2021                    Original Version
 * Sravankumar Ch					  	   07/24/2023				 User Story 4891049: T1PRJ0865978 - INC2449472 Unknown member isn't capturing inputted information correctly
 * Santhi Mandava                          03/06/2024                 User Story 5312358: T1PRJ0891742 - C01- Consumer Mgt - Ltng- VIT Remediation Search pages and unknown Group Creation    
 */

 public with sharing class lwcApexController {
    static final Map<String,String> mapRecordtypeInfo= new map<String,String>
   {
       'M' => HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c
   };
   static final map<string,string> mapExternalIDFormat = new map<String,String>{'A' => HUMConstants__c.getInstance('UNKNOWN_AGENT_EXTID').Stringvalue__c,'G' => HUMConstants__c.getInstance('UNKNOWN_GROUP_EXTID').Stringvalue__c,'P' => HUMConstants__c.getInstance('UNKNOWN_PROVIDER_EXTID').Stringvalue__c,'M' => HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c};
   //Store Information related to URL Mapping
   @AuraEnabled
   public static Map<String,Object> insertAccountMethod(Account accountObj,String IDType, String IDNumber,String fnm , String lnm,String wemail, String myemail, String bdate,String mstreet,String mcity,String streetchangee, String mphone, String mstate , string mpostalcode){
       try {
           String consumerID=IDType;
             List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
             String sEncryptedTaxId = '';
             Map<String,Object> resMap = new Map<String,Object>();
             Boolean isDuplicate=false;
            String staxId;
            set<String> setConsumerID = new Set<String>{'M'};	
             account acc=new account();
             acc.FirstName =fnm;
          acc.LastName =lnm;
          acc.Individual_First_Name__c =fnm;
           acc.Individual_Last_Name__c =lnm; 
          acc.PersonMailingStreet =mstreet; 
        //  acc.PersonMailingStreet =streetchangee;  
           acc.PersonMailingCity =mcity;
        //  acc.PersonMailingState =mstate;
          acc.PersonMailingStateCode =mstate;
           acc.Work_Email__c =wemail;
           acc.PersonEmail =myemail;
         // acc.PersonMailingPostalCode =accountObj.PersonMailingPostalCode;
             acc.PersonMailingPostalCode =mpostalcode;
           acc.Birthdate__c =bdate;
           acc.PersonHomePhone=mphone;
                      acc.General_Account__c = true;
                     acc.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c;
          ID recordTypeID = mapRecordtypeInfo.containsKey('M') ? HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),mapRecordtypeInfo.get('M')) : null;
                       if(recordTypeID != null)
               {
                   if(Schema.sobjectType.account.fields.recordTypeID.isCreateable() || Schema.sobjectType.account.fields.recordTypeID.isUpdateable())//*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
                  acc.recordTypeID=recordTypeID;
               }
            if('M' == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
       {
           sTaxId = IDNumber;
       }
            if(!String.isBlank(sTaxId) && setConsumerID.contains('M'))
       {
               sEncryptedTaxId = sTaxId;
               if('M' == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
               {
                   String sConsumerSearchQuery =   'SELECT Id, Name, Consumer_ID__c, ID_Type__c FROM ConsumerID__c where ID_Type__c = \'' + string.escapeSingleQuotes(consumerID) + '\' and ';
                   sConsumerSearchQuery += 'Consumer_ID__c = \'' + string.escapeSingleQuotes(sEncryptedTaxId) + '\'';
                   lstConsumers = (List<ConsumerID__c>)Database.Query(sConsumerSearchQuery);
                   for(ConsumerID__c obj:(List<ConsumerID__c>)Database.Query(sConsumerSearchQuery)){
                       isDuplicate =true;
                       break;
                   }

               }
       }
            if(!isDuplicate)      
      {             
           List<Account> lstAccounts  = new  List<Account>{acc};
           boolean isError = false;
           string accountExternalId = '';
           if('M' == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
           {
               if(mapExternalIDFormat.containsKey('M'))
               {
                   if(null != consumerID)
                   {               
                       if(consumerID == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM)
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SSN + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());                        
                       }
                       else if (consumerID == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM)
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICARE + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase()); 
                       }
                       else if (consumerID == GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM)
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_ENTID + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());                
                       }
                       else if (consumerID == GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM )
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEMBERIDBASE + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());                
                       }
                       else if (consumerID == GLOBAL_CONSTANT_HUM.CASESERVICE_CBISALTID_HUM)
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_CBISALTID + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());
                       } 
                       else if (consumerID == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM)
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICAID + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());
                       }                   
                       else if (!String.isBlank(idNumber))
                       {
                           accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_UKEY + HUMUtilityHelper.getMD5Hash(idNumber.toUpperCase());
                       }                
                   }
                   else
                   {
                       accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC;
                   }
               }
           }
           if(String.isNotBlank(accountExternalId) && accountExternalId != GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
           {
                          //  if(Schema.sobjectType.Account.isAccessible()){  //*validate user permissions to access an object before reading or modifying it against the FLS (Field Level Security) schema
             if(Schema.sobjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Account_External_ID__c.isUpdateable())
               lstAccounts[0].Account_External_ID__c = accountExternalId;  
               //} 
               Schema.sObjectField schemaExternalId = Schema.getGlobalDescribe().get('Account').getDescribe().fields.getMap().get('Account_External_ID__c');
               List<Database.upsertResult> upsertResults = Database.upsert(lstAccounts,schemaExternalId, false);
               isError = HUMExceptionHelper.processUpsertResults(upsertResults, lstAccounts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
           }
           else
           {
               Database.saveResult[] saveResults = Database.insert(lstAccounts, false);
               isError = HUMExceptionHelper.processSaveResults(saveResults, lstAccounts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
           }
           //In case of error set the flag
           if(isError)
           {
               //Reset the ids in case of Error
               for(Account oAccount: lstAccounts)
               {
                   oAccount.id = null;
               }
           }
           else
           {
               List<User> usr = [Select id, username from User where username LIKE: system.label.HUMUnknownAccountOwner];
               //Populate External ID
               if(mapExternalIDFormat.containsKey('M'))
               {
                   if(String.isBlank(accountExternalId) || accountExternalId == GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
                   {
                       for(Account oAcc: lstAccounts)
                       {
                           if(accountExternalId == GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
                           {               
                               accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC + oAcc.id;
                           }
                           else
                           {
                               accountExternalId = mapExternalIDFormat.get('M') + oAcc.id;
                           }
                           oAcc.Account_External_Id__c = accountExternalId;
                           if(!usr.isEmpty() && String.isBlank(sEncryptedTaxId)) oAcc.OwnerId = usr[0].id;
                       }
                       
                       //Update the result
                       List<Database.saveResult> saveResults = Database.update(lstAccounts,false);
                       isError = HUMExceptionHelper.processSaveResults(saveResults,lstAccounts,'HUMUnknownAccountController','processSaveResults','Account');
                   }
                   if(isError)
                   {
                   }
                   else
                   {
                       if(String.isNotBlank(sEncryptedTaxId) && setConsumerID.contains('M'))
                       {
                           //Insert Consumer IDs
                           List<ConsumerID__c> lstConsumerIds = insertConsumerTaxIds(sEncryptedTaxId,lstAccounts[0].id,consumerID);                           
                           for(Account oAcc: lstAccounts)
                           {
                               //Assign the owner the record to Humana API Only
                               if(!usr.isEmpty()) oAcc.OwnerId = usr[0].id;
                           }
                           
                           //Update the result
                           List<Database.saveResult> saveResults = Database.update(lstAccounts,false);                                               
                       }
                   }
               }
           }
       }else{

       }
       resMap.put('isDuplicate',isDuplicate);
       resMap.put('Account',acc);
             return resMap;
       } catch (Exception exp) {
           throw new AuraHandledException(exp.getMessage());
       }
   }
    @AuraEnabled(cacheable=true)
  public static List<Account> getAccountMethod(String accid){
       Try {
      
           List<account> accountlist=new list<account>();
                accountlist= [Select ID, Name, FirstName, LastName,Individual_First_Name__c,Individual_Last_Name__c,Suffix, RecordType.Name,PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState,PersonMailingPostalCode, PersonHomePhone, UserRecordAccess.HasReadAccess,Home_Office_Account__c,Birthdate__c, PersonEmail,ETL_Record_Deleted__c from Account Where Id=:accid ORDER BY CreatedDate DESC ];
        //  return [select Id, Name, Type, Rating,
           //    Phone, Website, AnnualRevenue from Account ORDER By CreatedDate DESC limit 1];
           return accountlist;
       } catch (Exception exp) {
           throw new AuraHandledException(exp.getMessage());
       }
   }
   public static List<Account> getAccountList() {
       return [SELECT Id, Name, Type, Rating,
               Phone, Website, AnnualRevenue
           FROM Account];
   }
      /*
   * Method Name   :    insertConsumerTaxIds
   * Description   :    Insertthe Consumer TAXIDs based on the Account ID
   * Return Type   :    List<ConsumerId__c>
   * Parameters    :    String, ID
   */
   @testVisible private Static List<ConsumerID__c> insertConsumerTaxIds(String sConsumerID,ID accID, String consumerIDType)
   {
       List<ConsumerId__c> lstConsumerRecords = new List<ConsumerId__c>();
       String sExternalID = mapExternalIDFormat.containsKey('M') ? mapExternalIDFormat.get('M'):'';
       String sTaxID = '';
       if('M' == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
       {
           sTaxID = consumerIDType;
           
       }
       String sPipeCharacter = HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
       
       if(!String.isBlank(sExternalID))
       {
           lstConsumerRecords.add(new ConsumerID__c(Consumer_Id__c = sConsumerID,Account__c = accId ,ID_Type__c = sTaxID));
           Database.saveResult[] saveResults = Database.insert(lstConsumerRecords, false);
           boolean isError = HUMExceptionHelper.processSaveResults(saveResults, lstConsumerRecords, 'HUMUnknownAccountController', 'insertConsumerTaxIds', 'ConsumerId__c');
           if(!isError)
           {
               //Populate ID
               for(ConsumerId__c consumerId: lstConsumerRecords)
               {
                  if('M' == HUMConstants__c.getInstance('UNKNOWN_MEMBER_NOTATION').Stringvalue__c)
                   {
                       if(sTaxID == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM)
                       {
                           consumerId.Consumer_External_Id__c = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_SSN + accId;
                       }
                       else if(sTaxID == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM)
                       {
                           consumerId.Consumer_External_Id__c = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICARE + accId;
                       }
                       else if(sTaxID == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM)
                       {
                           consumerId.Consumer_External_Id__c = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICAID + accId;
                       }
                       else
                       {
                           consumerId.Consumer_External_Id__c = sExternalID + sTaxID + sPipeCharacter +accId;  
                       }
                   }
               }
               saveResults = Database.update(lstConsumerRecords, false);
               isError = HUMExceptionHelper.processSaveResults(saveResults, lstConsumerRecords, 'HUMUnknownAccountController', 'insertConsumerTaxIds', 'ConsumerId__c');
               if(!isError) return lstConsumerRecords;
           }
       }
       return null;
   }
  
}