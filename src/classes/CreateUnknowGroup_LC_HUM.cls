/*******************************************************************************************************************************
Component Name : CreateUnknowGroup_LC_HUM
Version        : 1.0
Created On     : 6/23/2021
Function       : This component is for creating Unknow Group account functionality on Group Search tab
Test Class     : CreateUnknowGroup_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------------
	1.0			   Surendra Vemulapalli								06/23/2021					US1464387- Creating Unknown Group Account (Health Cloud Lightning App)
**************************************************************************************************************************************/


public without sharing class CreateUnknowGroup_LC_HUM {

    public static UnknowGroupCreateWrapper_LH_HUM unKnowGroupCreateInputWraper;
   

    @AuraEnabled
    public static List<Account> createUnknowGroup(string unKnowGroupCreateInputWraper)
    {  
      	system.debug(unKnowGroupCreateInputWraper);
     	UnknowGroupCreateWrapper_LH_HUM acctw = (UnknowGroupCreateWrapper_LH_HUM)JSON.deserialize(unKnowGroupCreateInputWraper, UnknowGroupCreateWrapper_LH_HUM.class);
        list<Account> lstAccount = new list<Account>();
          
        Account acct = new Account();
         
          if(!String.isblank(acctw.unAccountName )) acct.Name = acctw.unAccountName;
          if(!String.isblank(acctw.unTaxId )) acct.HealthCloudGA__TaxId__c = acctw.unTaxId;
          if(!String.isblank(acctw.unBillingCity )) acct.BillingCity = acctw.unBillingCity;
          if(!String.isblank(acctw.unWorkEmail )) acct.Work_Email__c = acctw.unWorkEmail;
          if(!String.isblank(acctw.unGroupNumber )) acct.Group_Number__c = acctw.unGroupNumber;
          if(!String.isblank(acctw.unBillingStreet )) acct.BillingStreet = acctw.unBillingStreet;
     	  if(!String.isblank(acctw.unZipCode )) acct.BillingPostalCode = acctw.unZipCode;
      	  if(!String.isblank(acctw.unPhone )) acct.Phone = acctw.unPhone;
     	  if(!String.isblank(acctw.unState )) acct.BillingState = acctw.unState;
          acct.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
         
         if(Schema.sobjectType.account.isAccessible()){  
                  //   if(Schema.sobjectType.account.fields.General_Account__c.isCreateable() || Schema.sobjectType.account.fields.General_Account__c.isUpdateable()) 
                       acct.General_Account__c = true;
                  //   if(Schema.sobjectType.account.fields.Tenant_Id__c.isCreateable() || Schema.sobjectType.account.fields.Tenant_Id__c.isUpdateable())
                      acct.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c;
         }  
    	insert acct;
      
       lstAccount.add(acct);
       string accountExternalId = '';
           
                List<User> usr = [Select id, username from User where username LIKE: system.label.HUMUnknownAccountOwner];
                //Populate External ID
                        for(Account oAcc: lstAccount)
                        {
                            if(accountExternalId == GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC)
                            {               
                                accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC + oAcc.id;  
                                system.debug('accountExternalId in External ID:'+accountExternalId);
                            }
                            else
                            {
                                accountExternalId = HUMConstants__c.getInstance('UNKNOWN_GROUP_EXTID').Stringvalue__c + oAcc.id;
                                system.debug('accountExternalId in External ID else:'+accountExternalId);
                            }
                            oAcc.Account_External_Id__c = accountExternalId;
                            if(!usr.isEmpty())
                            {
                                oAcc.OwnerId = usr[0].id;
                            }
                        }
                        
                        system.debug('Update Account');
                        List<Database.saveResult> saveResults = Database.update(lstAccount,false);
                    
             return lstAccount;
    }
}