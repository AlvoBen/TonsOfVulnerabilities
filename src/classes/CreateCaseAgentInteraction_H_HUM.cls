/******************************************************************************************************************
Apex class Name    : CreateCaseAgentInteraction_H_HUM
Version            : 1.0
Function           : This is a Helper class to get Account Map For agent from create Case service
Created On         : Jan 20 2019
Test Class         : CreateAgentInteraction_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Asish Behera                                  08/03/2019           Original Version
*   2.0            Sagar MN                                      05/04/2020          UserStory : 1028676 Creation of unknown agent while service down
*   3.0            Saikumar Boga                                 02/24/2021           US2009196 - PR00094232 - Build Checkmarx - SF - TECH
------------------------------------------------------------------------------------------------------------------- */
Public with sharing Class CreateCaseAgentInteraction_H_HUM {
	
    private HUMAgencySearchService.ProducerSearchResult objServiceResponse;
    Map<Integer,Account> mapAccountList = new Map<Integer,Account>();
    public HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('AgencySearchService');
    Private List<Account> lstKnownAccounts = new List<Account>(); 
    Private List<Account> lstUnKnownAccounts = new List<Account>(); 
    Map<Integer,InteractingAccount_DTO_HUM> mapObjectList = new Map<Integer,InteractingAccount_DTO_HUM>();    
    Map<String,String> mapReturning = new Map<String,String>();    
    
    /*
     * Method Name : PrepareAgentAccountMap
     * Description : This method is used to create agent accounts
     * Return type : Map<String,Map<String,String>>
     * Paramater   : CreateInteractionRequest_DTO_HUM 
     */
    @TestVisible 
    public Map<String,String> PrepareAgentAccountMap(List<InteractingAccount_DTO_HUM> ListAgentObjRequest)
    {
        
       for(Integer i = 0;i <= ListAgentObjRequest.size()-1; i++)
       {
               SearchAgentExternal(i+1,ListAgentObjRequest[i].ID,ListAgentObjRequest[i]);           
       }
        SearchAgentInternal();
        mapReturning =  CreateAccountRecords(ListAgentObjRequest); 
        return mapReturning;
    }  
    /*
     * Method Name : SearchAgentExternal
     * Description :This method is used for serching external service 
     * Return type : NA
     * Paramater   : Index,AgentID,CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO
     */
    @TestVisible
    Public Void SearchAgentExternal(Integer iIndex,String sAgentId,InteractingAccount_DTO_HUM objWithAcc)
    {  
        
        HUMAgencySearchService.ProducerSearchResult objResponse = new HUMAgencySearchService.ProducerSearchResult();
        CreateAgentInteraction_H_HUM agentIntObj = new CreateAgentInteraction_H_HUM();
        Try
        {
           
            HUMAgencySearchService.BasicHttpBinding_IProducer objService = new HUMAgencySearchService.BasicHttpBinding_IProducer();
            HUMAgencySearchService.Producer AgentObjRequest = new HUMAgencySearchService.Producer();
            AgentObjRequest.SysAssgnNbr = sAgentId;
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
                mapAccountList.put(iIndex,objAcc);
                lstKnownAccounts.Add(objAcc);
                mapObjectList.put(iIndex,Null);
            }
            else
            {  
                mapAccountList.put(iIndex,Null);
                mapObjectList.put(iIndex,objWithAcc);
            }
        }
        catch(Exception ex)
        {
            mapAccountList.put(iIndex,Null);
            mapObjectList.put(iIndex,objWithAcc);
            HUMExceptionHelper.logErrors(ex,'','SearchAgentExternal 1');
        }
        
    }
    
    /*
     * Method Name : SearchAgentInternal
     * Description : This method is used to Search Internally with AgentID
     * Return type : NA
     * Paramater   : NA
     */
     @TestVisible
    Public Void SearchAgentInternal()
    { 
       
        List<String> lstAgentIds = new List<String>();
        Account objUnknown ;
        InteractingAccount_DTO_HUM objReqs;

        For(Integer iKey :mapObjectList.keySet())
        {
            objReqs = mapObjectList.get(iKey);
            If(objReqs != Null)
            { 
                lstAgentIds.Add(objReqs.ID);
            }
        }        
        If(lstAgentIds != Null && !lstAgentIds.isEmpty())
        {
            String sQry = 'Select Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry, BillingAddress,';
            sQry += 'RecordType.Name, Phone, Agent_ID__c,Agent_Type__c,Id,Account_External_ID__c ,BillingPostalCode,ETL_Record_Deleted__c,(Select Consumer_ID__c from ConsumerIDs__r)'; 
            sQry += 'from Account where (RecordType.DeveloperName=' + '\'' + HUMConstants.HUM_Agency_Account_Record_Type + '\'';
                        sQry += ' OR RecordType.DeveloperName ='+'\''+ HUMConstants.HUM_UnknownAgency_Account_Record_Type+'\''+')';           
            
            
            sQry += ' AND Agent_ID__c IN  : lstAgentIds  ';           
            sQry += ' Limit ' +100;         
            List<Account> lstAccounts = Database.Query(sQry);            
            String sAgentID = '';
            Boolean bFoundAccount = false;            
            For(Integer i : mapObjectList.keySet())
            {
                objReqs = mapObjectList.get(i);
                IF(objReqs != Null)
                {
                    sAgentID = objReqs.ID;                    
                    bFoundAccount = false;
                    If(lstAccounts != Null && !lstAccounts.isEmpty())
                    {
                        For(Account ObjAcc : lstAccounts )
                        {
                            If(sAgentID == objAcc.Agent_ID__c)
                            {
                                mapAccountList.put(i,ObjAcc);
                                bFoundAccount = true;
                                break;
                            }
                        }
                        if(!bFoundAccount)
                        {
                            objUnknown = CreateUnknownAgent(objReqs);                            
                            mapAccountList.put(i,objUnknown );   
                            lstUnKnownAccounts.Add(objUnknown);
                        }
                    }
                    else
                    {
                        objUnknown = CreateUnknownAgent(objReqs);                         
                        mapAccountList.put(i,objUnknown );
                        lstUnKnownAccounts.Add(objUnknown);
                        
                    }
                }    
            }
            
        }
    }
    
    /*
     * Method Name : CreateUnknownAgent
     * Description : This method is used to create unknown accounts if there are no results from external service and Internal search
     * Return type : Account
     * Paramater   : CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO
     */
    @TestVisible
    Public Account CreateUnknownAgent(InteractingAccount_DTO_HUM objReqs)
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
     private Map<String,String> CreateAccountRecords(List<InteractingAccount_DTO_HUM> ListAgentObjRequest)
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
           
            if(accountRec.Account_External_Id__c == null && accountRec.id != null)
            {
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
         
         
         
                       
        return WithAboutFinalMap;
     }
}