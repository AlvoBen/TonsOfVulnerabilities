/*******************************************************************************************************************************
Apex Class Name :  UnknownProvider_LC_HUM
Version: 1.0
Created On      : 06/14/2021
Function        : This Class serves as a controller for Unknwon provider Account Create page. 
Test Class      : UnknownProvider_LT_HUM
        Modification Log
Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Saikumar Boga                                          06/14/2021                Intial Version
* Saikumar Boga                                          07/13/2021                Changes for Unknown Agent/Broker
* Sai Kumar Boga                                         09/02/2021                US 2654138 Checkmark scan issue with sharing 
* Vardhman Jain                                          08/01/2023                US 4832740 T1PRJ0865978 - MF26459 - Consumer/Error when creating Interaction With Unknown Provider About Member
* Santhi Mandava                                         11/04/2023                US4970205 - Unknown agent creation - Switch implemented
*********************************************************************************************************************************************************************************************/



Public with sharing Class UnknownProvider_LC_HUM {
  @AuraEnabled
   Public Static Boolean createAccountConsumerRecord(Account newRecord){
       
       Try{ 
       CRMFunctionality_ONOFF_Switch__c objSwitch = CRMFunctionality_ONOFF_Switch__c.getValues('5161867');
       Account acc = New Account();
       acc.Individual_First_Name__c = newRecord.Individual_First_Name__c ; 
       acc.Individual_Last_Name__c = newRecord.Individual_Last_Name__c ; 
       acc.Provider_Classification__c = newRecord.Provider_Classification__c ;
       acc.Agent_ID__c = newRecord.Agent_ID__c ; 
       acc.Agent_Type__c  = newRecord.Agent_Type__c ;
       acc.Name=newRecord.Name;
       acc.NPI_ID__c = newRecord.NPI_ID__c ;
       acc.BillingCity = newRecord.BillingCity ; 
       acc.BillingStateCode = newRecord.BillingStateCode ; 
       acc.BillingStreet = newRecord.BillingStreet ; 
       acc.BillingPostalCode = newRecord.BillingPostalCode ; 
       acc.Phone = newRecord.Phone ; 
       acc.Work_Email__c = newRecord.Work_Email__c ;
       acc.Description = newRecord.Description ;
       if(objSwitch?.IsON__c == true){
         acc.General_Account__c = true;
         acc.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c;
       }
           If (String.isNotBlank(newRecord.Provider_Classification__c))
           {
               
                  acc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HUM_LaunchGCPCCP_UNKNOWNPROVIDER).getRecordTypeId();
           } Else If(String.isNotBlank(newRecord.Agent_Type__c))
           {
                  acc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HUM_LaunchGCPCCP_UNKNOWAGENTBROKER).getRecordTypeId();
               
           }
       Database.insert(acc);
            system.debug('acc'+acc);
            If (String.isNotBlank(newRecord.Agent_Type__c)){
            acc.Account_External_ID__c =  HUMConstants__c.getInstance('UNKNOWN_AGENT_EXTID').Stringvalue__c + acc.id;
                
                }
		    If (String.isNotBlank(newRecord.Provider_Classification__c)){
                acc.Account_External_ID__c =  HUMConstants__c.getInstance('UNKNOWN_PROVIDER_EXTID').Stringvalue__c + acc.id;           
             }
           Database.update(acc);
           
    
       List<ConsumerId__c> lstConsumerRecrds = New List<ConsumerId__c>();
             String sExternalID = '';

       If (acc.RecordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HUM_LaunchGCPCCP_UNKNOWNPROVIDER).getRecordTypeId())
             {
            sExternalID = HUMConstants__c.getInstance('UNKNOWN_PROVIDER_EXTID').Stringvalue__c;
           
             } Else If(acc.RecordTypeId == Schema.SObjectType.Account.getRecordTypeInfosByName().get(HUMConstants.HUM_LaunchGCPCCP_UNKNOWAGENTBROKER).getRecordTypeId())
             {
               sExternalID = HUMConstants__c.getInstance('UNKNOWN_AGENT_EXTID').Stringvalue__c;
             }
           
           
            String sTaxID = '';
            sTaxID = HUMConstants__c.getInstance('CONSUMERID_TAXID').StringValue__c;
            String sPipeCharacter = HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;


       If (String.isNotBlank(newRecord.HealthCloudGA__TaxId__c))
           {
              lstConsumerRecrds.add(New ConsumerID__c(Consumer_Id__c = newRecord.HealthCloudGA__TaxId__c,Account__c = acc.Id,ID_Type__c = sTaxID,ETL_Record_Deleted__c = false));
           }
               Database.saveResult[] saveResults = Database.insert(lstConsumerRecrds, false);
           
            For (ConsumerId__c consumerId: lstConsumerRecrds)
                 {
                     {
                        consumerId.Consumer_External_Id__c = sExternalID + sTaxID + sPipeCharacter + consumerId.id;
                      }
                  }
           
                        saveResults = Database.update(lstConsumerRecrds, false);
        }
  
      Catch(Exception ex)
       {	
           HUMExceptionHelper.logErrors(ex, 'UnknownProvider_LC_HUM', 'createAccountConsumerRecord');
       }
       
    Return True;
}
   
}