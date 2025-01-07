/***************************************************************************************************************************************************************************
Apex Class Name  : HUMEchoServiceREST
Version          : 1.0
Created Date     : Aug 15 2018
Function         : This Rest Webservice is used to retrieve last interaction details
Test Class       : HUMEchoServiceRESTtTest
 ****************************************************************************************************************************************************************************

Modification Log:

* Developer Name             Code Review               Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor               296361                   08/15/2018                  Original Version
* Mohammed Noor               319700                   12/14/2018                  CA# 7969405 - Optimize the Query to resolve the Service time out Issue
* Jasmeen Shangari            320224                   12/17/2018                  CA# 7971998 - Code changes to do Search on InteractingAboutID if data not exist for UUID
****************************************************************************************************************************************************************************/
 
@RestResource(urlMapping='/EchoServiceREST/*')
global with sharing class HUMEchoServiceREST 
{
    
    /*
    * Method name : getInteractionDetails
    * Description : REST API method exposed to fetch Interaction details
    * Parameter   : void.
    * Return Type : void.    
    */
    @HttpPost
    global static void getInteractionDetails()
    {
        RESTRequest request = RestContext.request;                       
        HUMEchoRestRequestDTO_HUM echoRequest = new HUMEchoRestRequestDTO_HUM();
        echoRequest = (HUMEchoRestRequestDTO_HUM)System.JSON.deserialize(request.requestBody.toString(), HUMEchoRestRequestDTO_HUM.class);        
        getInteractionDetailsResponse(echoRequest);        
    }
    
    /*
    * Method name : getInteractionDetailsResponse
    * Description : This method is used to fetch the response of latest interaction details
    * Parameter   : HUMEchoRestRequestDTO_HUM (input request from service call)
    * Return Type : void.    
    */
    @testVisible
    private static void getInteractionDetailsResponse(HUMEchoRestRequestDTO_HUM echoRequest)
    {
    	HUMEchoRestResponseDTO_HUM echoResponse = new HUMEchoRestResponseDTO_HUM();
    	RESTResponse res = RESTContext.response;
		if(String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithType) && echoRequest.LastInteractionDetails.sInteractingWithType.equals(GLOBAL_CONSTANT_HUM.EMPLOYER_TYPE))
		{
		  echoRequest.LastInteractionDetails.sInteractingWithType = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM;
		}
    	echoResponse = fetchInteractionDetails(echoRequest);
        string sEchoResponse = System.JSON.serialize(echoResponse);        
	    res.statusCode = 200;
	    res.addHeader('Content-Type', 'application/json');
	    res.responseBody = Blob.valueOf(sEchoResponse);
    }
    
    /*
    * Method name : fetchInteractionDetails
    * Description : This method is used to fetch the latest interaction details
    * Parameter   : HUMEchoRestRequestDTO_HUM (input request from service call)
    * Return Type : HUMEchoRestResponseDTO_HUM (output response for the service call)    
    */
    @testVisible
    private static HUMEchoRestResponseDTO_HUM fetchInteractionDetails(HUMEchoRestRequestDTO_HUM echoRequest)
    {
        
        HUMEchoRestResponseDTO_HUM echoResponse = new HUMEchoRestResponseDTO_HUM();        
        try
        {        		        
	        if(echoRequest != null && String.isNotBlank(echoRequest.LastInteractionDetails.sUUID))
	        {
	            //fetch the interaction details based on UUID
	            echoResponse = fetchDetailsByUUID(echoRequest);
	        }
	        else if(echoRequest != null && ((String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithID) && String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithType))
	         || (String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingAboutID)&& String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingAboutType) && String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithType))))
	        {
	            //fetch the interaction details based on Enterprise ID or Account ID
	            echoResponse = fetchDetailsByMemberID(echoRequest);
	        }
	        else
	        {
	            //set error response where all/some input parameters are empty             
	            echoResponse =  createErrorResponse(null,false,System.Label.ECHOSERVICE_MISSINGINPUT_HUM);
	        }	        
        }
        catch(Exception e)
		{
			HUMExceptionHelper.logErrors(e,'HUMEchoServiceREST','fetchInteractionDetails');
			echoResponse = createErrorResponse(null,false,System.Label.ECHOSERVICE_GENERICERROR_HUM+':'+e.getMessage());			
		}
		return echoResponse;
    }
    
    /*
    * Method name : fetchDetailsByUUID
    * Description : This method is used to fetch the latest interaction details based on UUID from the input request.
    * Parameter   : HUMEchoRestRequestDTO_HUM (input request from service call)
    * Return Type : HUMEchoRestResponseDTO_HUM (output response for the service call)    
    */
    @testVisible
    private static HUMEchoRestResponseDTO_HUM fetchDetailsByUUID(HUMEchoRestRequestDTO_HUM echoRequest)
    {
        HUMEchoRestResponseDTO_HUM echoResponseObj = new HUMEchoRestResponseDTO_HUM();
        list<Interaction__c> lstInteraction;
        list<Case_Interaction__c> lstCaseInteraction;
                        
        if(String.isNotBlank(echoRequest.LastInteractionDetails.sUUID))
        {
            string sUUID = echoRequest.LastInteractionDetails.sUUID;
			date dtFromDate = null;
            date dtToDate = null;
                
            if(!String.isBlank(echoRequest.LastInteractionDetails.sStartDate))  dtFromDate = Date.parse(echoRequest.LastInteractionDetails.sStartDate); 
            if(!String.isBlank(echoRequest.LastInteractionDetails.sEndDate))  dtToDate = Date.parse(echoRequest.LastInteractionDetails.sEndDate).addDays(1);
           
            string sIntQuery = 'SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c, '+
                             'Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE UUID__c = :sUUID';   
            
            if(dtFromDate != null) sIntQuery = sIntQuery + ' AND CreatedDate >= :dtFromDate ';  // If Start date is provided 

            if(dtToDate != null) sIntQuery = sIntQuery + ' AND CreatedDate < :dtToDate ';   // If End Date is provided
                                   
            sIntQuery =  sIntQuery + ' ORDER BY CreatedDate DESC NULLS LAST Limit 1';         
            
            lstInteraction = database.query(sIntQuery);
            
            if(lstInteraction!=null && !lstInteraction.isEmpty())
            {                               
                string sInteractionID = lstInteraction[0].Id;
                                               
                //Fetch the latest case & interaction details from junction object between case and interaction                  
                string sCaseIntQuery = 'SELECT Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,'+
                'Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,'+              
                'Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c,'+
                'Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c, Case__r.LastModifiedby_Queue__c '+
                'FROM Case_Interaction__c WHERE Interaction__c = :sInteractionID';                
                
                if(dtFromDate != null) sCaseIntQuery = sCaseIntQuery + ' AND CreatedDate >= :dtFromDate ';  // If Start date is provided 

                if(dtToDate != null) sCaseIntQuery = sCaseIntQuery + ' AND CreatedDate < :dtToDate ';   // If End Date is provided
                        
                sCaseIntQuery =  sCaseIntQuery + ' ORDER BY Case__r.CreatedDate DESC NULLS LAST ';         
                
                lstCaseInteraction = database.query(sCaseIntQuery);
                
                if(lstCaseInteraction!=null && !lstCaseInteraction.isEmpty())
                {
                    echoResponseObj = setEchoOutput(lstInteraction[0], lstCaseInteraction[0]);  //create the output response based on interaction and case
                }
                else
                {
                    echoResponseObj = setEchoOutput(lstInteraction[0], null); //create the output response based only On the interaction 
                }                   
            }
            else
            {   
				echoResponseObj = fetchDetailsByMemberID(echoRequest);
            }
        }       
        return echoResponseObj;
    }
    
    /*
    * Method name : fetchDetailsByMemberID
    * Description : This method is used to fetch the latest interaction details based on ID from the input request.
    * Parameter   : HUMEchoRestRequestDTO_HUM (input request from service call)
    * Return Type : HUMEchoRestResponseDTO_HUM (output response for the service call)    
    */
    @testVisible
    private static HUMEchoRestResponseDTO_HUM fetchDetailsByMemberID(HUMEchoRestRequestDTO_HUM echoRequest)
    {
        HUMEchoRestResponseDTO_HUM echoResponseObj = new HUMEchoRestResponseDTO_HUM();
        string sAccountID = null;
        date dtFromDate = null;
        date dtToDate = null;
        List<Interaction__c> lstInteraction = new List<Interaction__c>();
        List<Interaction_Member__c> lstInteractionMem = new List<Interaction_Member__c>();
        List<Case_Interaction__c> lstCaseInteraction = new List<Case_Interaction__c>();
                
        if(!String.isBlank(echoRequest.LastInteractionDetails.sStartDate))  dtFromDate = Date.parse(echoRequest.LastInteractionDetails.sStartDate); 
        if(!String.isBlank(echoRequest.LastInteractionDetails.sEndDate))  dtToDate = Date.parse(echoRequest.LastInteractionDetails.sEndDate).addDays(1);  
        
        if(String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingAboutID)&& String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingAboutType) && String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithType))
        {
            //fetch account based on ID and Type
            sAccountID = locateAccount(echoRequest.LastInteractionDetails.sInteractingAboutID, echoRequest.LastInteractionDetails.sInteractingAboutType);
            if(String.isNotBlank(sAccountID))
            {
            	lstInteractionMem = getInteractionMemberDetails(sAccountID,echoRequest.LastInteractionDetails.sInteractingWithType,dtFromDate,dtToDate);
		        if(lstInteractionMem!= null && !lstInteractionMem.isEmpty())
		        {               
		            lstCaseInteraction = getCaseInteractionRecords(lstInteractionMem[0].Interaction__c,dtFromDate,dtToDate);
		            
		            lstInteraction = [SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c,
		                             Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE Id = :lstInteractionMem[0].Interaction__c
		                             ORDER BY CreatedDate desc NULLS Last];	
		        }
            }           
        }
        else if(String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithID) && String.isNotBlank(echoRequest.LastInteractionDetails.sInteractingWithType))
        {
            //fetch account based on ID and Type
            sAccountID = locateAccount(echoRequest.LastInteractionDetails.sInteractingWithID, echoRequest.LastInteractionDetails.sInteractingWithType);
        	if(String.isNotBlank(sAccountID))
        	{
        		lstInteraction = [SELECT Id, Name, CreatedDate, CreatedById, LastModifiedDate, LastModifiedById, Caller_Name__c, Created_By_Queue__c, Interacting_With__c, Interacting_With_type__c,
		                         Interaction_Origin__c, UUID__c, LastModifiedBy.Network_User_Id__c FROM Interaction__c WHERE Interacting_With__c =:sAccountID 
		                         ORDER BY CreatedDate desc NULLS Last];
		        if(lstInteraction!= null && !lstInteraction.isEmpty())
		        {
		        	lstCaseInteraction = getCaseInteractionRecords(lstInteraction[0].Id,dtFromDate,dtToDate);
		        }
        	}             
        }
        if(lstCaseInteraction!= null && lstInteraction!= null && lstCaseInteraction.isEmpty()&& !lstInteraction.isEmpty())
        {
            echoResponseObj = setEchoOutput(lstInteraction[0], null);
        }
        else if(lstCaseInteraction!= null && lstInteraction!= null && !lstCaseInteraction.isEmpty()&& !lstInteraction.isEmpty())
        {
            echoResponseObj = setEchoOutput(lstInteraction[0], lstCaseInteraction[0]);
        }
        else
        {
            echoResponseObj =  createErrorResponse(null,false,System.Label.ECHOSERVICE_NORECORDFOUND_HUM);
        }            
                                      
        return echoResponseObj;
    }
    
    /*
    * Method name : getInteractionMemberDetails
    * Description : This method is used to fetch the latest interaction member details based on the input parameters.
    * Parameter   : AccountID, Interaction Type, Start Date and End Date.
    * Return Type : List of Interaction_Member__C records.    
    */
    @testVisible
    private static list<Interaction_Member__C> getInteractionMemberDetails(string sAccountID,string sIntType,date dtFromDate,date dtToDate)
    {

        list<Interaction_Member__c> lstInteractionMem = new list<Interaction_Member__c>();
        
        string sIntMember = 'Select Id,Name, Interaction__r.Interacting_With_type__c,LastModifiedDate, Interacting_About__c,Interaction__r.LastModifiedDate,'+
                'Interaction__c,Interaction__r.LastModifiedBy.Name, Interaction__r.Name, Interaction__r.LastModifiedBy.Network_User_Id__c '+
                ' from Interaction_Member__c where Interaction__r.Interacting_With_type__c = :sIntType '+                                      
                ' AND Interacting_About__c =:sAccountID ';
           
        if(dtFromDate != null) sIntMember = sIntMember + ' And  Interaction__r.CreatedDate >= : dtFromDate ';  // If Start date is provided  

        if(dtToDate != null) sIntMember = sIntMember + ' And  Interaction__r.CreatedDate < : dtToDate ';   // If End Date is provided

        sIntMember =  sIntMember + 'order by Interaction__r.CreatedDate DESC NULLS LAST ';
        lstInteractionMem = database.query(sIntMember);     
        return lstInteractionMem;

    }		
    
    /*
    * Method name : getCaseInteractionRecords
    * Description : This method is used to fetch the latest case interaction member details based on the input parameters.
    * Parameter   : Interaction ID, Start Date and End Date.
    * Return Type : List of Case_Interaction__c records.    
    */
    @testVisible
    private static list<Case_Interaction__c> getCaseInteractionRecords(Id intId,date dtFromDate, date dtToDate)
    {
        list<Case_Interaction__c> lstCaseInteraction;

        //Fetch the latest case & interaction details from junction object between case and interaction                  
        string sCaseIntQuery = 'Select Id,Case__c,LastModifiedById, LastModifiedBy.Name,Case__r.LastModifiedDate,Case__r.CaseNumber,Case__r.CTCI_List__c,Case__r.CTCI_List__r.Intent__c,Case__r.CTCI_List__r.Intent__r.Name,Case__r.CTCI_List__r.Classification__r.Name,'+
                'Case__r.Service_Center__c, Case__r.Owner_Queue__c, Case__r.LastModifiedById,Case__r.CreatedById, Case__r.LastModifiedBy.Network_User_Id__c, Interaction__c,'+
                'Case__r.LastModifiedBy.Name, Case__r.CreatedBy.Name, Case__r.CreatedBy.Network_User_Id__c, Case__r.Department__c, Case__r.LastModifiedby_Queue__c '+
                'from Case_Interaction__c where Interaction__c = :  intId ';


        if(dtFromDate != null)  sCaseIntQuery = sCaseIntQuery + ' And  Case__r.CreatedDate >= : dtFromDate ';  // If Start date is provided 

        if(dtToDate != null)  sCaseIntQuery = sCaseIntQuery + ' And  Case__r.CreatedDate < : dtToDate ';  // If End Date is provided 

        sCaseIntQuery =  sCaseIntQuery + ' order by Case__r.CreatedDate DESC NULLS LAST ';         
        lstCaseInteraction = database.query(sCaseIntQuery);         
        return lstCaseInteraction;

    }
    
    /*
    * Method name : setEchoOutput
    * Description : This method is used to generate the successfull response message.
    * Parameter   : Interaction record and Case Interaction record.
    * Return Type : HUMEchoRestResponseDTO_HUM.     
    */
    @testVisible
    private static HUMEchoRestResponseDTO_HUM setEchoOutput(Interaction__c memberInt, Case_Interaction__c memberCaseInt)
    {
        HUMEchoRestResponseDTO_HUM echoResponseObj = new HUMEchoRestResponseDTO_HUM();
        HUMEchoRestResponseDTO_HUM.HUMEchoRestResponseDTO echoResponse = new HUMEchoRestResponseDTO_HUM.HUMEchoRestResponseDTO();
        if(memberCaseInt==null && memberInt!=null)
        {
            echoResponse.sInteractionID = memberInt.Name;
            echoResponse.sLastmodifiedby = memberInt.LastModifiedBy.Network_User_Id__c;
            echoResponse.sLastModifiedDateInteraction = String.valueOf(memberInt.LastModifiedDate);
            echoResponse.bIsSuccess = true;
            echoResponse.sReferenceType = System.Label.ECHOSERVICE_REFERENCETYPE_HUM;
        }
        else if(memberCaseInt!=null && memberInt!=null)
        {
            echoResponse.sCaseId = memberCaseInt.Case__r.CaseNumber;
            echoResponse.sCreatedBy = memberCaseInt.Case__r.CreatedBy.Network_User_Id__c;                       
            if(memberCaseInt.Case__r.CTCI_List__c!=null)    
            {
                if(memberCaseInt.Case__r.CTCI_List__r.Intent__c!=null)
                    echoResponse.sReason = memberCaseInt.Case__r.CTCI_List__r.Intent__r.Name;
                if(memberCaseInt.Case__r.CTCI_List__r.Classification__c!=null)      
                    echoResponse.sCategory = memberCaseInt.Case__r.CTCI_List__r.Classification__r.Name;             
            }               
            echoResponse.sInteractionID = memberInt.Name;
            echoResponse.sLastmodifiedby = memberCaseInt.Case__r.LastModifiedBy.Network_User_Id__c;
            echoResponse.sLastModifiedDateCase = String.ValueOf(memberCaseInt.Case__r.LastModifiedDate);
            echoResponse.sLastModifiedDateInteraction = String.ValueOf(memberInt.LastModifiedDate);
            echoResponse.sSite = memberCaseInt.Case__r.Service_Center__c;            
            echoResponse.sDepartment =memberCaseInt.Case__r.Department__c;
            echoResponse.sLastUpdatedTeamName = memberCaseInt.Case__r.LastModifiedby_Queue__c;
            echoResponse.sDisposition = System.Label.ECHOSERVICE_INTERFACEDISPOSITION_HUM;
            echoResponse.sReferenceType = System.Label.ECHOSERVICE_REFERENCETYPE_HUM;
            echoResponse.bIsSuccess = true;
        }
        else
        {
            echoResponseObj = createErrorResponse(null,false,System.Label.ECHOSERVICE_NORECORDFOUND_HUM);
            return echoResponseObj;
        }        
        echoResponseObj.lastInteractionDetailsResponse = echoResponse;
        return echoResponseObj;
    }
    
    /*
    * Method name : locateAccount
    * Description : Fetches the account records based on Input parameters
    * Parameter   : Account Enterprise ID and Account Record type
    * Return Type : String AccountID.
    */ 
    @testVisible
    private static string locateAccount(string sAccountID, string sAccountType)
    {
        string sAccRecordID;
        list<Account> lstAccount = new list<Account>();
        
		If(sAccountType.equals(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))
			lstAccount = [SELECT Id, Account_External_ID__c FROM Account WHERE Group_Number__c = :sAccountID AND RecordType.Name = :sAccountType limit 1];
		else		
			lstAccount = [SELECT Id, Enterprise_ID__c,Account_External_ID__c FROM Account WHERE Enterprise_ID__c = :sAccountID AND RecordType.Name = :sAccountType limit 1];
		
			
        if(lstAccount!= null && !lstAccount.isEmpty()) { sAccRecordID =  lstAccount[0].Id; }		     
		
        return sAccRecordID;
    }
    
    /*
    * Method name : createErrorResponse
    * Description : Creates the Error Response Message
    * Parameter   : Error Code, success flag and Error Message
    * Return Type : Response Message - HUMEchoRestResponseDTO_HUM
    */ 
    @testVisible 
    private static HUMEchoRestResponseDTO_HUM createErrorResponse(string sErrorCode, boolean bIsSuccess, string sErrorMessage)
    {
        HUMEchoRestResponseDTO_HUM echoResponseObj = new HUMEchoRestResponseDTO_HUM();
        HUMEchoRestResponseDTO_HUM.HUMEchoRestResponseDTO echoResponse = new HUMEchoRestResponseDTO_HUM.HUMEchoRestResponseDTO();
        echoResponse.bIsSuccess = bIsSuccess;
        echoResponse.sErrorCode = sErrorCode;
        echoResponse.sErrorMessage = sErrorMessage;
        echoResponseObj.lastInteractionDetailsResponse = echoResponse;
        return echoResponseObj;
    }
        
}