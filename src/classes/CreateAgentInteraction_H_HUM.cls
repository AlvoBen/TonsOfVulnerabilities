/******************************************************************************************************************
Apex class Name    : CreateAgentInteraction_H_HUM
Version            : 1.0
Function           : This is a Helper class to get Account Map For agent from Case service
Created On         : Jan 20 2019
Test Class         : CreateAgentInteraction_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Lakshmi Vutukuri               353269          04/12/2019           Original Version
*   2.0            Seema Kaulgi                                   07/04/2019           REQ - 403811 GBO Watson chat (Group)- Agent to Agent scenario fix 
*   3.0            Sagar MN                                       05/04/2020           UserStory : 1028676 Creation of unknown agent while service down
*   4.0			   Jasmeen Shangari                               02/22/2021           CheckMarx fix -update class with sharing
*   5.0           Moshitha Gunasekaran                            07/05/2021           US2446677 - Salesforce to Softphone Case Interaction Web Services 
*                 Mayur Pardeshi                                  07/05/2023		   User Story 4599393: T1PRJ0036776: Ability To Create Interactions in Genesys when calls are placed to Producer Services
------------------------------------------------------------------------------------------------------------------- */
public with sharing class CreateAgentInteraction_H_HUM
{
    private HUMAgencySearchService.ProducerSearchResult objServiceResponse;
    Map<Integer,Account> mapAccountList = new Map<Integer,Account>();
    Map<string,Account> mapCallAccountToUpsert = new Map<string,Account>();
    Map<string,Account> mapCallFinalAccount = new Map<string,Account>();
    Map<String,Map<String,String>> mapFinal = new Map<String,Map<String,String>>();
    Map<Integer,CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> mapObjectList = new Map<Integer,CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
    Map<String,Map<String,String>> mapReturning = new Map<String,Map<String,String>>();
    public HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('AgencySearchService');
    Private List<Account> lstKnownAccounts = new List<Account>(); 
    Private List<Account> lstUnKnownAccounts = new List<Account>();
    //adding static variable for US4599393 development(US4599393:T1PRJ0036776:Ability To Create Interactions in Genesys when calls are placed to Producer Services).
    Public Static final Map<string,Account> mapAccountToUpsert = new Map<string,Account>(); 
    /*
     * Method Name : PrepareAgentAccountMap
     * Description : This method is used to create agent accounts
     * Return type : Map<String,Map<String,String>>
     * Paramater   : CreateInteractionRequest_DTO_HUM 
     */
    @TestVisible 
    public Map<String,Map<String,String>> PrepareAgentAccountMap(CreateInteractionRequest_DTO_HUM.CreateInteractionRequest AgentObjRequest)
    {
        
       If(AgentObjRequest.InteractingWith.EntityType == 'Agent' )
       {   
           SearchAgentExternal(0,AgentObjRequest.InteractingWith.ID,AgentObjRequest.InteractingWith);
       }
       for(Integer i = 0;i <= AgentObjRequest.CaseInteractingAbout.size()-1; i++)
       {
           
           If(AgentObjRequest.CaseInteractingAbout[i].InteractingAbout.EntityType == 'Agent' )
           {   
               SearchAgentExternal(i+1,AgentObjRequest.CaseInteractingAbout[i].InteractingAbout.ID,AgentObjRequest.CaseInteractingAbout[i].InteractingAbout);
           }
       }
        SearchAgentInternal();
        mapReturning =  CreateAccountRecords(AgentObjRequest); 
        return mapReturning;
    } 
    
    /*
     * Method Name : PrepareCallAgentAccountMap
     * Description : This method is used to create agent accounts for service called by calls
     * Paramater   : accountId 
     */
    public void PrepareCallAgentAccountMap(string accountId)
    {
        //External service is called to fetch the records
        SearchAgentExternal(0, accountId, null);
        //If record is available then stored in map for upsert the record in account later once all service call is done
        if(null != mapAccountList.get(0))
        {
            mapCallAccountToUpsert.put(accountId,mapAccountList.get(0));
            mapAccountToUpsert.put(accountId,mapAccountList.get(0));
        }
        else
        {
            //If record is not available then internal call is amde to query SFDC object for getting agent records
            String sQry = 'Select Individual_Last_Name__c, Individual_First_Name__c, Name, BillingStreet, BillingCity, BillingState, BillingCountry, BillingAddress,';
            sQry += 'RecordType.Name, RecordType.DeveloperName, Phone, Agent_ID__c,Agent_Type__c,Id,Account_External_ID__c ,BillingPostalCode,ETL_Record_Deleted__c,(Select Consumer_ID__c from ConsumerIDs__r)'; 
            sQry += 'from Account where (RecordType.DeveloperName=' + '\'' + HUMConstants.HUM_Agency_Account_Record_Type + '\''+')';
            sQry += ' AND Agent_ID__c =  : accountId  ';          
            sQry += ' Limit ' +20;
            
            List<Account> lstAccounts = Database.Query(sQry);
            if(lstAccounts != null && !lstAccounts.isEmpty())
            {
                for(Account ObjAcc : lstAccounts )
                {
                    //AccountId is matched with AgentID to find exact records
                    if(accountId == objAcc.Agent_ID__c)
                    {
                        mapCallFinalAccount.put(accountId,ObjAcc);
                        mapAccountToUpsert.put(accountId,ObjAcc);
                        break;
                    }
                }
            }
            
        }
    } 
    
    /*
     * Method Name : SearchAgentExternal
     * Description :This method is used for serching external service 
     * Return type : NA
     * Paramater   : Index,AgentID,CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO
     */
    @TestVisible
    Public Void SearchAgentExternal(Integer iIndex,String sAgentId,CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO objWithAbout)
    {  
        HUMAgencySearchService.ProducerSearchResult objResponse = new HUMAgencySearchService.ProducerSearchResult();
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
                objAcc = mapAccountfields(objResponse.ProducerList.Producer[0]);
                mapAccountList.put(iIndex,objAcc);
                lstKnownAccounts.Add(objAcc);
                mapObjectList.put(iIndex,Null);
            }
            else
            {  
                mapAccountList.put(iIndex,Null);
                mapObjectList.put(iIndex,objWithAbout);
            }
        }
        catch(Exception ex)
        {
            mapAccountList.put(iIndex,Null);
            mapObjectList.put(iIndex,objWithAbout);
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
        CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO objReqs;

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
    Public Account CreateUnknownAgent(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO objReqs)
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
     * Method Name : mapAccountfields
     * Description : This method is used to map the external callout results to account object
     * Return type : Account
     * Paramater   : HUMAgencySearchService.Producer
     */
    @TestVisible
    public Account mapAccountfields(HUMAgencySearchService.Producer agencyInfo)
    {
        try
        {
            Date dHireDate =  ((agencyInfo.HireDate != null) && (agencyInfo.HireDate.year() > HUMConstants.HUMMaxDateYear) 
                                            || (agencyInfo.HireDate.year() < HUMConstants.HUMMinDateYear)) ? null : agencyInfo.HireDate.date();                
            Date dBaseDate =  ((agencyInfo.BaseDate != null) && (agencyInfo.BaseDate.year() > HUMConstants.HUMMaxDateYear) 
                                            || (agencyInfo.BaseDate.year() < HUMConstants.HUMMinDateYear)) ? null : agencyInfo.BaseDate.date();
            
            String sName = String.isBlank(agencyInfo.Name)? agencyInfo.FirstName + ' '+ agencyInfo.LastName : agencyInfo.Name;
            Account acc = populateAgencyInfoOnAccount(agencyInfo);           
            acc.Producer_Base_Date__c = dBaseDate;
            acc.Producer_Hire_Date__c = dHireDate;  
            acc.Name = sName;                
            return acc; 
        }
        catch(Exception e)
        {            
            HUMExceptionHelper.logErrors(e,'','mapAccountfields');
            return null;
        }                       
    }
    /*
     * Method Name : populateAgencyInfoOnAccount
     * Description : This method is used to map the external callout results to account object
     * Return type : Account
     * Paramater   : HUMAgencySearchService.Producer
     */
    @TestVisible
    private Account populateAgencyInfoOnAccount(HUMAgencySearchService.Producer agencyInfo)
    {
        Account acc = new Account(recordTypeId = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),HUMConstants.HUMAgentBrokerRecordTypeName),
                                Individual_First_Name__c = agencyInfo.FirstName,Individual_Last_Name__c = agencyInfo.LastName,
                                Agent_ID__c = agencyInfo.SysAssgnNbr,
                                Agent_Type__c = agencyInfo.ProducerType == HUMConstants.HUMAgencySearchAgentTypeValue2?agencyInfo.ProducerType:HUMConstants.HUMAgencySearchAgentTypeValue3,
                                Phone = agencyInfo.PrimaryPhone,
                                Fax = agencyInfo.FaxNbr,Work_Email__c = agencyInfo.EmailId,
                                Producer_Termed__c = agencyInfo.IsTermed,
                                DBA__c = agencyInfo.Dba,
                                Gender__c = agencyInfo.Gender,Producer_Status__c = agencyInfo.Status,
                                Birthdate__c = agencyInfo.DateOfBirth != null ? String.ValueOf(agencyInfo.DateOfBirth.format('MM/dd/yyyy')) : null,
                                Producer_Status_Date__c = agencyInfo.StatusDate.date(),
                                Name_Suffix__c = agencyInfo.SuffixName,Middle_Name__c = agencyInfo.MiddleInitialName,
                                Enterprise_ID__c = agencyInfo.SysAssgnNbr,                    
                                Account_External_ID__c = HUMConstants.EXT_ID_AGENT+ agencyInfo.SysAssgnNbr, 
                                BillingStreet = agencyInfo.BusinessAddress.AddressLine1, BillingCity = agencyInfo.BusinessAddress.CityName,
                                BillingPostalCode = agencyInfo.BusinessAddress.ZipCode,
                                ShippingStreet = agencyInfo.ShippingAddress.AddressLine1, ShippingCity = agencyInfo.ShippingAddress.CityName,
                                ShippingPostalCode = agencyInfo.ShippingAddress.ZipCode, ShippingStateCode = agencyInfo.ShippingAddress.StateCode,
                                BillingStateCode = agencyInfo.StateCode, General_Account__c = true, Tenant_Id__c = HUMConstants.TENANTID_GEN);
         
         return acc;
    }

    /*
     * Method Name : CreateCallAccountRecords
     * Description : This method is used to create agent accounts for service returned records
     * returntype   : map<String,Account> 
     */
    public map<String,Account> CreateCallAccountRecords()
    {   
        List<Account> lstIntWithAccounts = new List<Account>();
        //If upsert record is stored in map then upsert and send the information
        if(null != mapCallAccountToUpsert && !mapCallAccountToUpsert.isEmpty())
        {
            List<Account> lstAccounts = mapCallAccountToUpsert.values();
            lstIntWithAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstAccounts);
            if(null!= lstIntWithAccounts && !lstIntWithAccounts.isEmpty())
            {
                for(Account acct :lstIntWithAccounts)
                {
                    mapCallFinalAccount.put(acct.Agent_ID__c,acct);
                }
            }
        }
        return mapCallFinalAccount;
    }
     /*
     * Method Name : CreateAccountRecords
     * Description : This method is used for Upserting account records and adding it to map 
     * Return type : Map<String,Map<String,String>>
     * Paramater   : CreateInteractionRequest_DTO_HUM.CreateInteractionRequest
     */
     @TestVisible
     private Map<String,Map<String,String>> CreateAccountRecords(CreateInteractionRequest_DTO_HUM.CreateInteractionRequest AgentObjRequest)
     {   
        List<Account> lstIntWithAccounts = new List<Account>();
        List<Account> lstIntAboutAccounts = new List<Account>();
        List<Account> lstTempIntWithAccounts = new List<Account>();
        List<Account> lstTempIntAboutAccounts = new List<Account>();
        List<Account> lstAccounts = mapAccountList.values();
        List <Account> upsertlstAccounts = new List <Account>();
        
        If(lstKnownAccounts != Null  && !lstKnownAccounts.isEmpty())
        {           
           if(lstKnownAccounts.size() <= 2){
               if(lstKnownAccounts[0] != NULL){
               lstTempIntWithAccounts.add(lstKnownAccounts[0]);
               
               lstIntWithAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstTempIntWithAccounts);
               
               } 
               if(lstKnownAccounts.size() > 1){         
                   if(lstKnownAccounts[1] != NULL){
                   
                   lstTempIntAboutAccounts.add(lstKnownAccounts[1]);
                   lstIntAboutAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstTempIntAboutAccounts);}
               }
                
           }
           else{
           for(Integer i = 0;i <= AgentObjRequest.CaseInteractingAbout.size()-1; i++)
           {
               
               If(AgentObjRequest.CaseInteractingAbout[i].InteractingAbout.EntityType == 'Agent' )
               {   
                   
                   lstTempIntWithAccounts.add(lstKnownAccounts[i]);
                   lstIntWithAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstTempIntWithAccounts);
                   lstTempIntWithAccounts = new List<Account>();
                   if(lstKnownAccounts[i+1] != NULL){
                       lstTempIntAboutAccounts.add(lstKnownAccounts[i+1]);
                       lstIntAboutAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstTempIntAboutAccounts);
                       lstTempIntAboutAccounts = new List<Account>();
                   }
                   //SearchAgentExternal(i+1,AgentObjRequest.CaseInteractingAbout[i].InteractingAbout.ID,AgentObjRequest.CaseInteractingAbout[i].InteractingAbout);
               }
           }
           
           }
        }
        If(lstKnownAccounts == Null  && lstKnownAccounts.isEmpty())
        {
            lstIntAboutAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstTempIntAboutAccounts);
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
                lstToUpsert.Add(accountRec);
            }
        }
        If(!lstToUpsert.isEmpty()) lstToUpsert = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstToUpsert);
        Map<String, String> WithAboutFinalMap;
        Integer iIndex = 0;
        If(AgentObjRequest.InteractingWith.EntityType == 'Agent' )
        {       
            WithAboutFinalMap = new Map<String,String>();
            WithAboutFinalMap.put(AgentObjRequest.InteractingWith.ID, lstAccounts[iIndex].Id);
            mapFinal.put('Interacting_With',WithAboutFinalMap);
            iIndex = iIndex + 1;
        }
        for(Integer i = 0;i <= AgentObjRequest.CaseInteractingAbout.size()-1; i++)
        {
            If(AgentObjRequest.CaseInteractingAbout[i].InteractingAbout.EntityType == 'Agent' )
            {
                WithAboutFinalMap = mapFinal.get('Interacting_About');
                If(WithAboutFinalMap == Null) WithAboutFinalMap = new Map<String,String>();
                WithAboutFinalMap.put(AgentObjRequest.CaseInteractingAbout[i].InteractingAbout.ID,lstAccounts[iIndex].Id);
                mapFinal.put('Interacting_About',WithAboutFinalMap);
                iIndex = iIndex + 1;
            }
        }
         
         return mapFinal;
     }
}