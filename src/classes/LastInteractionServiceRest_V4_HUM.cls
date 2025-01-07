/***************************************************************************************************************************************************************************
Apex Class Name  : LastInteractionServiceRest_V4_HUM
Version          : 1.0
Created Date     : Aug 27 2019
Function         : This Rest Webservice helper class is used to retrieve last interaction details
Test Class       : 
****************************************************************************************************************************************************************************

Modification Log:

* Developer Name             Code Review               Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Sagar MN                                        08/27/2019                  Original Version
* Sagar MN                                        01/13/2021                  Fetch Interaction based on only Ineracting With Details.
* Sagar MN                                        07/02/2021                  Updated code to Match V3 Scenario for member type
****************************************************************************************************************************************************************************/ 
Public with sharing class LastInteractionServiceRest_V4_HUM {
    
    /*
  * Method name : fetchInteractionDetails
  * Description : This method is used to fetch the latest interaction details
  * Parameter   : LastInteractionRequest_DTO_HUM (input request from service call)
  * Return Type : LastInteractionResponse_DTO_HUM (output response for the service call)    
  */
    Public static LastInteractionResponse_DTO_HUM fetchInteractionDetails(LastInteractionRequest_DTO_HUM lastInteractionRequest)
    {
        LastInteractionResponse_DTO_HUM lastInteractionResponse = new LastInteractionResponse_DTO_HUM();
        try
        {   
            if(lastInteractionRequest != null)
            {               
                if(String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.UUID))
                {                    
                    lastInteractionResponse = fetchDetailsByUUID(lastInteractionRequest);                
                }                
                else if(((String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.Id)
                          && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.EntityType)
                          && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.IdType))
                          && (String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.Id)
                          && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.EntityType)
                          && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.IdType)))
                          && String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.UUID))                                                        
                {    
                    lastInteractionResponse = fetchDetailsByInteractionDetails(lastInteractionRequest);                
                }
                else if((String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.Id))
                          && (String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.EntityType))
                          && (String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.IdType)))
        {
                     lastInteractionResponse = fetchDetailsByInteractionDetails(lastInteractionRequest);    
                }
                else
                {     
                     lastInteractionResponse =  createErrorResponse(GLOBAL_CONSTANT_HUM.Error_Code_LastInteractionInfo,false,System.Label.ECHOSERVICE_MISSINGINPUT_HUM);
                }     
            }                    
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'LastInteractionServiceRest_V4_HUM','fetchInteractionDetails');
            lastInteractionResponse = createErrorResponse(GLOBAL_CONSTANT_HUM.Error_Code_LastInteractionInfo,false,GLOBAL_CONSTANT_HUM.Error_Message_LastInteractionInfo);      
        }        
        return lastInteractionResponse;
    }
    
    /*
  * Method name : fetchDetailsByUUID
  * Description : This method is used to fetch the latest interaction details based on UUID from the input request.
  * Parameter   : LastInteractionRequest_DTO_HUM (input request from service call)
  * Return Type : LastInteractionResponse_DTO_HUM (output response for the service call)    
  */ 
    @TestVisible
    private static LastInteractionResponse_DTO_HUM fetchDetailsByUUID(LastInteractionRequest_DTO_HUM lastInteractionRequest)
    {
        LastInteractionResponse_DTO_HUM lastInteractResponseObj = new LastInteractionResponse_DTO_HUM();
        list<Interaction__c> lstInteraction;
        list<Case_Interaction__c> lstCaseInteraction;        
        
        if(String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.UUID))
        {
            string UUID = lastInteractionRequest.LastInteractionDetailRequest.UUID;
            date dtFromDate = null;
            date dtToDate = null;
            
          
            string sIntQuery = 'SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c,'+
                'Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE UUID__c =:UUID';   
            
       sIntQuery =  sIntQuery + ' ORDER BY CreatedDate DESC NULLS LAST LIMIT 1 ';         
            
            lstInteraction = database.query(sIntQuery);
            
            if(!lstInteraction.isEmpty())
            {                               
                string sInteractionID = lstInteraction[0].Id;
                lstCaseInteraction = caseIntQuery(sInteractionID);                
              
                if(!lstCaseInteraction.isEmpty())
                {
                    lastInteractResponseObj = setLastInteractionDetails(lstInteraction[0], lstCaseInteraction[0]); 
                }
                else
                {
                    lastInteractResponseObj = setLastInteractionDetails(lstInteraction[0], null);  
                }                   
            }
            else
            {   
                lastInteractResponseObj = fetchDetailsByInteractionDetails(lastInteractionRequest);
            }
        }        
        return lastInteractResponseObj;   
    }
    
    /*
  * Method name : caseIntQuery
  * Description : This method is used to fetch the latest interaction details based on InteractionID.
  * Parameter   : String sInteractionID
  * Return Type : list<Case_Interaction__c>   
  */  
    Public static list<Case_Interaction__c> caseIntQuery(string sInteractionID){
        
       list<Case_Interaction__c> lstCaseIntRetVal = new list<Case_Interaction__c>(); 
       if(String.isNotBlank(sInteractionID)){ 
                    
                    string sCaseIntQuery = 'SELECT Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,'+
                        'Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,Case__r.Created_By_Queue__c,'+              
                        'Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c,'+
                        'Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c, Case__r.LastModifiedby_Queue__c , Interaction__c '+
                        'FROM Case_Interaction__c Where Interaction__c =:sInteractionID AND Interaction__r.Interacting_With__r.ETL_Record_Deleted__c = false';               
                 
                    sCaseIntQuery =  sCaseIntQuery + ' ORDER BY Case__r.CreatedDate DESC NULLS LAST LIMIT 1 ';         
                    
                    lstCaseIntRetVal = database.query(sCaseIntQuery); 
       }
        Return lstCaseIntRetVal;
    }
    
    /*
  * Method name : fetchDetailsByInteractionDetails
  * Description : This method is used to fetch the latest interaction details based on Interaction from the input request.
  * Parameter   : LastInteractionRequest_DTO_HUM (input request from service call)
  * Return Type : LastInteractionResponse_DTO_HUM (output response for the service call)    
  */
    @TestVisible
    private static LastInteractionResponse_DTO_HUM fetchDetailsByInteractionDetails(LastInteractionRequest_DTO_HUM lastInteractionRequest)
    {
        LastInteractionResponse_DTO_HUM lstIntResponseObj = new LastInteractionResponse_DTO_HUM();
        string sInteractionAboutAccountID;
        string sInteractionWithAccountID;
        date dtFromDate = null;
        date dtToDate = null;
        List<Interaction__c> lstInteraction = new List<Interaction__c>();
        List<Interaction_Member__c> lstInteractionMem = new List<Interaction_Member__c>();
        List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();
        
        if(!String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.StartDate))  dtFromDate = Date.parse(lastInteractionRequest.LastInteractionDetailRequest.StartDate); 
        if(!String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.EndDate))  dtToDate = Date.parse(lastInteractionRequest.LastInteractionDetailRequest.EndDate).addDays(1);  
        
     if((String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.Id) && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.IdType) && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.EntityType))&& 
	 String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.Id) && !String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.IdType) && !String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.EntityType)){
        sInteractionAboutAccountID = locateAccount(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.Id, lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.IdType , lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.EntityType, lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.MemberIdDependentCode);

	if(String.isNotBlank(sInteractionAboutAccountID)){
		
		 lstInteractionMem = getInteractionDetails(sInteractionAboutAccountID,lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.EntityType,dtFromDate,dtToDate) ;
	}
	
	if(lstInteractionMem!=null && !lstInteractionMem.isEmpty()){
		lstCaseInteraction = getCaseInteractionRecords(lstInteractionMem[0].Interaction__c,dtFromDate,dtToDate);
	
	 lstInteraction = [SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c,
                                  Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE Id = :lstInteractionMem[0].Interaction__c AND Interacting_With__r.ETL_Record_Deleted__c = false
                                  ORDER BY CreatedDate desc NULLS Last Limit 1];
	}	 
	}else if(String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.Id) && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.IdType) && String.isNotBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.EntityType) && String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.Id) && String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.IdType) && String.isBlank(lastInteractionRequest.LastInteractionDetailRequest.InteractingAboutCriteria.EntityType))
	{
		 sInteractionWithAccountID = locateAccount(lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.Id, lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.IdType, lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.EntityType, lastInteractionRequest.LastInteractionDetailRequest.InteractingWithCriteria.MemberIdDependentCode);
		if(String.isNotBlank(sInteractionWithAccountID))
        {
            lstInteraction = getInteractionWithDetails(sInteractionWithAccountID,dtFromDate,dtToDate);
        }
         if(!lstInteraction.isEmpty())
            {              
                lstCaseInteraction = getCaseInteractionRecords(lstInteraction[0].Id,dtFromDate,dtToDate);                   
            }
    }
           
        
        if(lstCaseInteraction!= NULL && lstCaseInteraction.isEmpty() && !lstInteraction.isEmpty() && lstInteraction!=NULL)
        {
            lstIntResponseObj = setLastInteractionDetails(lstInteraction[0], null);
        }
        else if(lstCaseInteraction!= NULL && !lstCaseInteraction.isEmpty() && !lstInteraction.isEmpty() && lstInteraction!=NULL)
        {
            lstIntResponseObj = setLastInteractionDetails(lstInteraction[0], lstCaseInteraction[0]);
        }
        else
        {
            lstIntResponseObj =  createErrorResponse(GLOBAL_CONSTANT_HUM.Error_Code_LastInteractionInfo,false,System.Label.ECHOSERVICE_NORECORDFOUND_HUM);
        } 
        return lstIntResponseObj;
    }
    
   /*
  * Method name : getLstOfInteraction
  * Description : This method is used to fetch the latest interaction details based on Interaction from the input Param.
  * Parameter   : List<Interaction_Member__c>
  * Return Type : List<Interaction__c>    
  */ 
    Public Static List<Interaction__c> getLstOfInteraction(List<Interaction_Member__c> lstInteractionMem){
       List<Interaction__c> lstOfInteraction = new List<Interaction__c>();
        
       lstOfInteraction = [SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c,
                                  Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE Id = :lstInteractionMem[0].Interaction__c AND Interacting_With__r.ETL_Record_Deleted__c = false
                                  ORDER BY CreatedDate desc NULLS Last Limit 1];
        Return lstOfInteraction;
    }
    
    /*
  * Method name : locateAccount
  * Description : Fetches the account records based on Input parameters
  * Parameter   : Account Enterprise ID and Account Record type
  * Return Type : String AccountID.
  */ 
    @TestVisible
    private static string locateAccount(string sAccountID, string sAccountType , String sEntityType, String memberDependentCode)
    {
        string sAccRecordID='';
        list<Account> lstAccount = new list<Account>();
        List<Policy_Member__c> memberIDs = new List<Policy_Member__c>();
      	List<Member_ID__c> memberID = new List<Member_ID__c>();
        
        if(sEntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
        {            
            if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_GROUPNUM) && sAccountID!='') 
            {
                lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Group_Number__c = :sAccountID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];     
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ACCOUNT_GROUP_CUSTGENKEY) && sAccountID!='')
            {
                lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Source_Customer_Key__c, Account_External_ID__c FROM Account WHERE Enterprise_ID__c = :sAccountID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];        
            }            
        }
        else if(sEntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
        { 
             if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ACCOUNT_ENTERPRISEID) && sAccountID!='') 
            { 
                lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Enterprise_ID__c = :sAccountID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];     
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MEMBERID_HUM) && sAccountID!='')
            { 
                if(memberDependentCode!='')
                {
                    sAccountID = sAccountID + memberDependentCode;
                }
               
                   memberIDs  = [SELECT Member__c, Id, Name FROM Policy_Member__c where Name =:sAccountID AND ETL_Record_Deleted__c = false limit 1];
                 
                if(!memberIDs.isEmpty()){
                   String smemberID = memberIDs[0].Member__c;  
                    if(String.isNotBlank(smemberID)){
                        lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Id =:smemberID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];                          
                    }
                }           
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM) && sAccountID!='')
            {
                 memberID = [SELECT Id,Name, policy_Member__r.Member__c,Type__c,Member_ID_Value__c FROM Member_ID__c WHERE Type__c =:GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM AND Member_ID_Value__c=:sAccountID AND policy_Member__r.ETL_Record_Deleted__c = false Limit 1];            
                
                if(!memberID.isEmpty()){
                 String ssnMemberID = memberID[0].policy_Member__r.Member__c;
                 if(String.isNotBlank(ssnmemberID)){
                         lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE ID =:ssnMemberID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];      
                    }            
                }                                  
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM) && sAccountID!='')
            {
                   memberID = [SELECT Id,Name, policy_Member__r.Member__c,Type__c,Member_ID_Value__c FROM Member_ID__c WHERE Type__c =:GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM AND Member_ID_Value__c=:sAccountID AND policy_Member__r.ETL_Record_Deleted__c = false Limit 1];            
                
                if(!memberID.isEmpty()){
                    String medicareMemberID = memberID[0].policy_Member__r.Member__c;
                	if(String.isNotBlank(medicareMemberID)){                   
                     lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE ID =:medicareMemberID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];     
                	}   
                }
            }
        }            
        else If(sEntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
        {
            if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ACCOUNT_AGENT_AGENTID) && sAccountID!='')
            {             
             		lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Agent_ID__c = :sAccountID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM AND ETL_Record_Deleted__c = false limit 1];                     	
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID) && sAccountID!='')
            {              
              	List<ConsumerID__c> taxIDlst = [SELECT ID_Type__c, Id, Name, Account__c ,Consumer_ID__c FROM ConsumerID__c where ID_Type__c=:GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID and Consumer_ID__c =:sAccountID AND Account__r.ETL_Record_Deleted__c = false Order By CreatedDate desc limit 1];  
                
                if(!taxIDlst.isEmpty()){                                 
                    lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Id =: taxIDlst[0].Account__c and recordtype.Name = :GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM AND ETL_Record_Deleted__c = false limit 1];                            
                }
             }            
           }
        else If(sEntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
        {
            if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID) && sAccountID!='')
            {                
                lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE NPI_ID__c = :sAccountID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];           
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.ACCOUNT_PROVIDER_PROVIDERID) && sAccountID!='')
            {
                lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Enterprise_ID__c = :sAccountID and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];         
            }
            else if(sAccountType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM) && sAccountID!='')
            {   
                List<ConsumerID__c> taxIDs = [SELECT ID_Type__c, Id, Name, Account__c ,Consumer_ID__c FROM ConsumerID__c where ID_Type__c=:GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM and Consumer_ID__c =:sAccountID AND Account__r.ETL_Record_Deleted__c = false Order By CreatedDate desc limit 1];  
                
                if(!taxIDs.isEmpty()){                                 
                    lstAccount = [SELECT Id, Agent_ID__c, NPI_ID__c, Group_Number__c, Enterprise_ID__c, recordtype.Name, Source_Platform_Code__c, Account_External_ID__c FROM Account WHERE Id =: taxIDs[0].Account__c and recordtype.Name = :GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM AND ETL_Record_Deleted__c = false limit 1];                            
                }                
            }            
        }    
        
        if(lstAccount!= null && !lstAccount.isEmpty())
        {
            sAccRecordID =  lstAccount[0].Id; 
        }
        
        return sAccRecordID;  
    }
    
    /*
  * Method name : getCaseInteractionRecords
  * Description : This method is used to fetch the latest case interaction member details based on the input parameters.
  * Parameter   : Interaction ID, Start Date and End Date.
  * Return Type : List of Case_Interaction__c records.    
  */
    @TestVisible
    private static list<Case_Interaction__c> getCaseInteractionRecords(Id intId,date dtFromDate, date dtToDate)
    {
        list<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();        
                        
        string sCaseIntQuery = 'Select Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,'+
            'Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c, Interaction__c,'+
            'Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c,Case__r.Created_By_Queue__c, Case__r.LastModifiedby_Queue__c '+
            'from Case_Interaction__c where Interaction__c = :intId ';   
			
		if(dtFromDate != null)  sCaseIntQuery = sCaseIntQuery + ' And  Case__r.CreatedDate >= : dtFromDate ';  // If Start date is provided 

        if(dtToDate != null)  sCaseIntQuery = sCaseIntQuery + ' And  Case__r.CreatedDate < : dtToDate ';  // If End Date is provided 
			
        
        sCaseIntQuery =  sCaseIntQuery + ' order by Case__r.CreatedDate DESC NULLS LAST LIMIT 1 ';   
       
        lstCaseInteraction = database.query(sCaseIntQuery);            
        return lstCaseInteraction;
    }
     /*
  * Method name : getCaseInteractionRecords
  * Description : This method is used to fetch the latest case interaction member details based on the input parameters.
  * Parameter   : Interaction ID, Start Date and End Date.
  * Return Type : List of Case_Interaction__c records.    
  */
    @TestVisible
    private static list<Case_Interaction__c> getCaseInteractionRecords(Id intId,date dtFromDate, date dtToDate, Id accId)
    {
        list<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();        
                        
        string sCaseIntQuery = 'Select Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,'+
            'Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c, Interaction__c,'+
            'Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c,Case__r.Created_By_Queue__c, Case__r.LastModifiedby_Queue__c '+
            'from Case_Interaction__c where Interaction__c = :intId  and Case__r.AccountId =: accId';        
        
        sCaseIntQuery =  sCaseIntQuery + ' order by Case__r.CreatedDate DESC NULLS LAST LIMIT 1 ';   
       
        lstCaseInteraction = database.query(sCaseIntQuery);            
        return lstCaseInteraction;
    }
    
    /*
  * Method name : setLastInteractionDetails
  * Description : This method is used to generate the successfull response message.
  * Parameter   : Interaction record and Case Interaction record.
  * Return Type : LstIntResponseObj.     
  */
    @TestVisible
    private static LastInteractionResponse_DTO_HUM setLastInteractionDetails(Interaction__c intId, Case_Interaction__c caseIntID)
    {
        LastInteractionResponse_DTO_HUM LstIntResponseObj = new LastInteractionResponse_DTO_HUM();
        LastInteractionResponse_DTO_HUM.LastInteractionDetailsResponse LstIntResponse = new LastInteractionResponse_DTO_HUM.LastInteractionDetailsResponse();
        User usr = [Select Id,Name From User where Id =:intId.CreatedByID];
        if(caseIntID==null && intId!=null)
        {
            LstIntResponse.InteractionID = intId.Name;           
            LstIntResponse.CreatedBy = usr.Name;
            LstIntResponse.LastUpdated = String.valueOf(intId.LastModifiedDate);
            LstIntResponse.bIsSuccess = true;
            LstIntResponse.ReferenceType = System.Label.ECHOSERVICE_REFERENCETYPE_HUM;
            LstIntResponse.AgentId = intId.LastModifiedBy.Network_User_Id__c;
        }
        else if(caseIntID!=null && intId!=null)
        {
            LstIntResponse.ContactId = ''; 
            LstIntResponse.InquiryId = '';  
            LstIntResponse.AgentId = intId.LastModifiedBy.Network_User_Id__c;
            LstIntResponse.LastUpdated = String.valueOf(intId.LastModifiedDate);
            LstIntResponse.CreatedBy = caseIntID.Case__r.CreatedBy.Name; 
            LstIntResponse.CreatedTeamId = caseIntID.Case__r.Created_By_Queue__c ;
            if(caseIntID.Case__r.CTCI_List__c!=null)    
            {
                if(caseIntID.Case__r.CTCI_List__r.Intent__c!=null)
                    LstIntResponse.Reason = caseIntID.Case__r.CTCI_List__r.Intent__r.Name;
                if(caseIntID.Case__r.CTCI_List__r.Classification__c!=null)      
                    LstIntResponse.Category = caseIntID.Case__r.CTCI_List__r.Classification__r.Name;             
            } 
            LstIntResponse.Disposition = System.Label.ECHOSERVICE_INTERFACEDISPOSITION_HUM;
            LstIntResponse.Site = caseIntID.Case__r.Service_Center__c; 
            LstIntResponse.Department = caseIntID.Case__r.Department__c;
            LstIntResponse.TeamName = caseIntID.Case__r.LastModifiedby_Queue__c;            
            LstIntResponse.CaseId = caseIntID.Case__r.CaseNumber;
            LstIntResponse.InteractionID = intId.Name;
            LstIntResponse.ReferenceType = System.Label.ECHOSERVICE_REFERENCETYPE_HUM; 
            LstIntResponse.bIsSuccess = true;
        }
        else
        {
            LstIntResponseObj = createErrorResponse(GLOBAL_CONSTANT_HUM.Error_Code_LastInteractionInfo,false,System.Label.ECHOSERVICE_NORECORDFOUND_HUM);
            return LstIntResponseObj;
        }        
        LstIntResponseObj.lastInteractionDetailsResponse = LstIntResponse;
        return LstIntResponseObj;
    }
    
    /*
  * Method name : getInteractionDetails
  * Description : This method is used to fetch the latest interaction member details based on the input parameters.
  * Parameter   : InteractionAboutAccountID, InteractionWithAccountId, Start Date and End Date.
  * Return Type : List of Interaction_Member__C records.    
  */
    @TestVisible
    private static list<Interaction_Member__C> getInteractionDetails(string sInteractionAboutAccountID, string sInteractionWithAccountID, date dtFromDate,date dtToDate)
    {
        list<Interaction_Member__c> lstInteractionMem = new list<Interaction_Member__c>();
        
        string sIntMember = 'Select Id,Name, Interaction__r.Interacting_With_type__c,LastModifiedDate, Interacting_About__c,Interaction__r.LastModifiedDate,'+
            'Interaction__c,Interaction__r.LastModifiedBy.Name, Interaction__r.Name, Interaction__r.LastModifiedBy.Network_User_Id__c '+
            'from Interaction_Member__c where Interacting_About__c = :sInteractionAboutAccountID '+                                      
            'AND Interaction__r.Interacting_With_type__c =:sInteractionWithAccountID'; 
        
        if(dtFromDate != null) sIntMember = sIntMember + ' And Interaction__r.CreatedDate >= :dtFromDate ';
        
    if(dtToDate != null) sIntMember = sIntMember + ' And Interaction__r.CreatedDate <= :dtToDate '; 
        
        sIntMember =  sIntMember + ' order by Interaction__r.CreatedDate DESC NULLS LAST LIMIt 1 ';
        
        lstInteractionMem = database.query(sIntMember);
       
        return lstInteractionMem;
    }
  
   /*
  * Method name : getInteractionWithDetails
  * Description : This method is used to fetch the latest interaction member details based on the input parameters - interacting with.
  * Parameter   : InteractionWithAccountId, Start Date and End Date.
  * Return Type : List of Interaction_Member__C records.    
  */
    @TestVisible
    private static list<Interaction__c> getInteractionWithDetails(string sInteractionWithAccountID, date dtFromDate,date dtToDate)
    {
        list<Interaction__c> lstInteractionMem = new list<Interaction__c>();
       
        string sIntMember = 'SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, '+            
            'Interacting_With_type__c, Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c '+
            'FROM Interaction__c where Interacting_With__c =:sInteractionWithAccountID'; 
        
         if(dtFromDate != null) sIntMember = sIntMember + ' And CreatedDate >= :dtFromDate ';
        
    	if(dtToDate != null) sIntMember = sIntMember + ' And CreatedDate <= :dtToDate '; 
        
        sIntMember =  sIntMember + ' order by CreatedDate DESC NULLS LAST LIMIT 1';
        lstInteractionMem = database.query(sIntMember);        
        return lstInteractionMem;
    }
    /*
  * Method name : createErrorResponse
  * Description : Creates the Error Response Message
  * Parameter   : Error Code, success flag and Error Message
  * Return Type : Response Message - LastInteractionResponse_DTO_HUM
  */ 
    @TestVisible
    private static LastInteractionResponse_DTO_HUM createErrorResponse(string sErrorCode, boolean bIsSuccess, string sErrorMessage)
    {
        LastInteractionResponse_DTO_HUM lastInteractionResponseObj = new LastInteractionResponse_DTO_HUM();
        LastInteractionResponse_DTO_HUM.LastInteractionDetailsResponse errResponse = new LastInteractionResponse_DTO_HUM.LastInteractionDetailsResponse();
        errResponse.bIsSuccess = bIsSuccess;
        errResponse.sErrorCode = sErrorCode;
        errResponse.sErrorMessage = sErrorMessage;
        lastInteractionResponseObj.lastInteractionDetailsResponse = errResponse;
        return lastInteractionResponseObj;
    }   
    
}