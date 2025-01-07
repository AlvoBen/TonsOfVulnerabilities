/******************************************************************************************************************
Apex class Name    : CaseServiceAgent_v4_H_HUM
Version            : 1.0
Function           : This is a Helper class to get Account Map For agent from create Case service
Created On         : Oct 21 2020
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0           Sagar MN		                                 10/21/2020           Original Version
* 	1.1			  Sagar MN										 03/04/2021		      US 1999913 Code checkin for MF1
------------------------------------------------------------------------------------------------------------------- */
public class CaseServiceAgent_v4_H_HUM{

    public static HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('AgencySearchService');
    public static Map<String,Account> mapAccountList = new Map<String,Account>(); // main list to add accounts from ext/int search
    Private static List<Account> lstKnownAccounts = new List<Account>(); 
    Private static List<Account> lstUnKnownAccounts = new List<Account>();  
        
       /**
     * get interacting type and account details method
     * 
     * */
    public static map<String,Account> getAgentDetails(InteractingAccount_DTO_HUM listAgentIntAcc,String sIntercatingType){
    List<CaseServiceRestInteraction_DTO_HUM> interactionWrapObj  = new List<CaseServiceRestInteraction_DTO_HUM>();
    Map<String,Account> accReturnVal = new Map<String,Account>(); 
    Account retAccVal = new Account();
        CaseServiceRestInteraction_DTO_HUM interactionWrap = NEW CaseServiceRestInteraction_DTO_HUM();
        interactionWrap = CreateCaseRequest_H_HUM.getInteractionWrapObj(listAgentIntAcc, sIntercatingType);//get interaction details
        interactionWrapObj.add(interactionWrap); //get the interaction details         
        //do the external callout
       accReturnVal = SearchAgentExternal(listAgentIntAcc,sIntercatingType);       
        if(accReturnVal.get(sIntercatingType)==null){
            //do internal search           
           accReturnVal= SearchAgentInternal(listAgentIntAcc,sIntercatingType); 
            if(accReturnVal.isEmpty()){
                retAccVal = CreateUnknownAgent_V4(listAgentIntAcc);              	           
                mapAccountList.put(Null,retAccVal);
                accReturnVal= mapAccountList;
            }
        }
        
    return accReturnVal;
    }
    
    
    Public static Map<String,Account> SearchAgentExternal(InteractingAccount_DTO_HUM listAgentIntAcc,String sIntercatingType){
     HUMAgencySearchService.ProducerSearchResult objResponse = new HUMAgencySearchService.ProducerSearchResult();
        CreateAgentInteraction_H_HUM agentIntObj = new CreateAgentInteraction_H_HUM();
        Try
        {
           
            HUMAgencySearchService.BasicHttpBinding_IProducer objService = new HUMAgencySearchService.BasicHttpBinding_IProducer();
            HUMAgencySearchService.Producer AgentObjRequest = new HUMAgencySearchService.Producer();
            AgentObjRequest.SysAssgnNbr = listAgentIntAcc.ID;
            AgentObjRequest.ProducerType = 'All';
            objService.endpoint_x = objWebserviceCallout.End_Point_URL__c;
            objService.clientCertName_x = objWebserviceCallout.Certificate_Name__c;
            objService.timeout_x = Integer.valueOf(objWebserviceCallout.Timeout__c);
            objResponse = objService.SearchProducer(AgentObjRequest ,false);            
            If(objResponse != Null && objResponse.ProducerList != Null && objResponse.ProducerList.Producer != Null &&
            !objResponse.ProducerList.Producer.isEmpty() )
            {                
                Account objAcc = new Account();
                objAcc = agentIntObj.mapAccountfields(objResponse.ProducerList.Producer[0]);                 
                mapAccountList.put(sIntercatingType,objAcc);                
               // lstKnownAccounts.Add(objAcc);
               // mapObjectList.put(Null);
            }
            else
            {  
                mapAccountList.put(sIntercatingType,Null);
               // mapObjectList.put(listAgentIntAcc);
            }
            
        }
        catch(Exception ex)
        {
            mapAccountList.put(sIntercatingType,Null);
           // mapObjectList.put(listAgentIntAcc);
            HUMExceptionHelper.logErrors(ex,'','SearchAgentExternal 1');
            
        }          
        return mapAccountList;
    }
    
    Public static Map<String,Account> SearchAgentInternal(InteractingAccount_DTO_HUM listAgentIntAcc,String sIntercatingType)
    { 
        List<String> lstAgentIds = new List<String>();       
        try{
        If(listAgentIntAcc!= Null)
        {
             lstAgentIds.add(listAgentIntAcc.ID);
        }
        if(lstAgentIds.size()>0){
            String sQry = 'Select Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry, BillingAddress,';
            sQry += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,Account_External_ID__c ,BillingPostalCode,ETL_Record_Deleted__c,(Select Consumer_ID__c from ConsumerIDs__r)'; 
            sQry += 'from Account where (RecordType.DeveloperName=' + '\'' + HUMConstants.HUM_Agency_Account_Record_Type + '\'';
                        sQry += ' OR RecordType.DeveloperName ='+'\''+ HUMConstants.HUM_UnknownAgency_Account_Record_Type+'\''+')';           
            
            
            sQry += ' AND Agent_ID__c IN  : lstAgentIds  ';  
            sQry += ' AND ETL_Record_Deleted__c = false  ';
            sQry += ' Limit ' +1;    
            
            Account[] lstAccount = Database.Query(sQry);
			
            IF(lstAccount.SIZE()>0){ 
             if(listAgentIntAcc.ID == lstAccount[0].Agent_ID__c){
                 mapAccountList.put(sIntercatingType,lstAccount[0]);
                 lstUnKnownAccounts.Add(lstAccount[0]);
            }   
            }else{
               mapAccountList.put(sIntercatingType,null); 
            }
            
        }else{
            mapAccountList.put(sIntercatingType,null);
        }
    }catch(Exception ex)
        {
            mapAccountList.put(sIntercatingType,Null);
           // mapObjectList.put(listAgentIntAcc);
            HUMExceptionHelper.logErrors(ex,'','SearchAgentInternal');
            
        }  
       
        return mapAccountList; 
    }
    
    Public static Account CreateUnknownAgent_V4(InteractingAccount_DTO_HUM objReqs)
    {
       
        Account objAcc = new Account();
        objAcc.General_Account__c = true;
        objAcc.Tenant_Id__c = HUMConstants__c.getInstance('TENANTID_GEN').StringValue__c;
        Id recordTypeID = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),'Unknown Agent/Broker') ;
        objAcc.recordTypeID = recordTypeID;
        objAcc.Agent_ID__c = objReqs.ID; 
        objAcc.Agent_Type__C = 'Agency';
        objAcc.Name =  (String.isNotBlank(objReqs.AccountName)) ? objReqs.AccountName : objReqs.FirstName + ' ' +objReqs.LastName;
        objAcc.BillingStateCode = objReqs.State ;
        objAcc.BillingCity= objReqs.city;
        objAcc.BillingPostalCode= objReqs.ZipCode;
        objAcc.Phone =objReqs.HomePhone;
        objAcc.Work_Email__c = objReqs.EmailID;
        objAcc.Individual_Last_Name__c = objReqs.LastName;
        objAcc.Individual_first_Name__c = objReqs.FirstName;
        objAcc.BillingStreet =objReqs.AddressLine1+objReqs.AddressLine2;
       
        return objAcc;
    }
 
     /*
     * Method Name : CreateAccountRecords
     * Description : This method is used for Upserting account records and adding it to map 
     * Return type : Map<String,Map<String,String>>
     * Paramater   : CreateInteractionRequest_DTO_HUM.CreateInteractionRequest
     */
     @TestVisible
     private static void CreateAccountRecords(List<InteractingAccount_DTO_HUM> ListAgentObjRequest)
     {  
        
        List<Account> lstAccounts = mapAccountList.values();
        List <Account> upsertlstAccounts = new List <Account>();
        
        If(lstKnownAccounts != Null  && !lstKnownAccounts.isEmpty())
        {
           lstKnownAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstKnownAccounts);
           
           
        }
        
        If(lstUnKnownAccounts != Null  && !lstUnKnownAccounts .isEmpty())
        {
           lstUnKnownAccounts = HUMInteractionMemberDMLHelper.insertAccountForInteractionID(lstUnKnownAccounts);
           
        }
        List<Account> lstToUpsert = new List<Account>();         
        for(Account accountRec: lstAccounts)
        {
            if(accountRec.Account_External_ID__c == lstAccounts[0].Account_External_ID__c){
                
            //}
            //if(accountRec.Account_External_Id__c == null && accountRec.id != null)
            //{
                accountRec.Account_External_Id__c = GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNAGENTEXTID_HUM + accountRec.id;
               
            }
            lstToUpsert.Add(accountRec);
            
        }
         Map<String, String> WithAboutFinalMap;
        
         If(!lstToUpsert.isEmpty()){
             lstToUpsert = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstToUpsert);
            
             WithAboutFinalMap = new Map<String,String>();         	
         	 for(Integer i = 0;i < ListAgentObjRequest.size(); i++){
				WithAboutFinalMap.put(ListAgentObjRequest[i].ID, lstAccounts[i].Id);
         	 }  
         } 
         If(!lstKnownAccounts.isEmpty()){
             lstToUpsert = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstKnownAccounts);
				
             WithAboutFinalMap = new Map<String,String>();         	
			 for(Integer i = 0;i < ListAgentObjRequest.size(); i++){
				WithAboutFinalMap.put(ListAgentObjRequest[i].ID, lstKnownAccounts[i].Id);
         	}  
         }    
        
                     
       // return WithAboutFinalMap;
     }
    
      //@future method to do the dml operations for inserting provider accounts
    //Future methods do not support parameter type of Map<String,List<Account>>
    
    public static void performDMLOperationsQueuable(Map<String,Account> accountMapToUpsert){
        
       CreateCaseAsynch_Agent_S_V4_HUM updateJob = new CreateCaseAsynch_Agent_S_V4_HUM(accountMapToUpsert);
        ID jobID = System.enqueueJob(updateJob); 
    }
    
    public static void performUpsertForKnown(Map<String,Account> accountMapToUpsertKnown){
        Map<String,Account> mapresultAccount = new Map<String,Account>();
        Map<String,Map<String,String>> mapFinalProviderData = new Map<String,Map<String,String>>();
        
        if(accountMapToUpsertKnown!=null && !accountMapToUpsertKnown.isEmpty())
        {
            //commented by sagar ----12/1/2020
        //    mapresultAccount = CaseServiceProvider_v4_H_HUM.upsertAccountRec(accountMapToUpsertKnown);         
        //    if(mapresultAccount!=null && !mapresultAccount.isEmpty())
        //    {
        //        CaseServiceProvider_v4_H_HUM.upsertconsumerIds(mapresultAccount); 
        //   }
        } 
    }
   
    
}