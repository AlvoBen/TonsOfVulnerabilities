/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRegisterAccount_H_HUM 
* Version          : 1.0
* Created Date     : 25th May, 2021
* Function         : Handler Class for Register Account web services
* Test Class       : 
**************************************************************************** 
* Modification Log:
* Developer Name                   Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ekta                			  		 				 5/25/2021             Original Version 2322470
* Ananya                			  		 		     9/13/2021             CRUD FLS FIX 
* Ekta									                 01/07/2022		       REQ:2824434 - Remove Case Sensitivity
* Ekta	                			  		 		     02/07/2021             Storefront VNC: Member Secondary Confirmation
**********************************************************************************************************************************/
public  with sharing class CRMRetailRegisterAccount_H_HUM 
{
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRegisterAccountResponse_DTO_HUM(); 
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM(); 
    private static String memberId = ''; 
    private static String eQuery = '';
    private static Boolean Switch_2824434 = HUMUtilityHelper.isCRMFunctionalityON('2824434');
    /**
	* CRMRetailRegisterAccount_H_HUM Method constructor.
	* <p>
	* This method is used to call the handlerequest method.
	*
	* @param  		oRequest                             Deserialized request                     		
	* @return		 NA
	*/
    public CRMRetailRegisterAccount_H_HUM(CRMRetailVNCRequest_DTO_HUM.CRMRetailRegisterAccountRequest_DTO_HUM oRequest)
    {  
        if(oRequest != null)
        {
            request = oRequest;  
            handleRequest();
        }        
        else
        {
            createResponse(false,'', false, false , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
            prepareResponse();
        }
    }
    /**
	* handleRequest Method.
	* <p>
	* This method is called from constructor and it handle the request received.
	*
	* @param  			NA                                 
	* @return 			NA                 
	*/
    public void handleRequest()
    {
        extractDetails();
        extractAccountDetails();
        prepareResponse();
    } 
    /**
	* validateRequest Method.
	* <p>
	* This method is used to validate the request.
	*
	* @param  			NA                                 
	* @return 			NA
	*/
    public void extractDetails()
    {	
        if(String.IsNotBlank(request.memberId))
        {
            memberId = request.memberId.trim();               
        }
        if(String.IsNotBlank(request.firstName) && String.IsNotBlank(request.lastName) && String.IsNotBlank(request.dateOfBirth))
        {			
            if(Switch_2824434)
            	eQuery = request.firstName.trim() + ' AND ' + request.lastName.trim();	
            else
                eQuery = ' Where FirstName = \'' + request.firstName.trim() +'\' AND LastName = \'' + request.lastName.trim()+'\' AND (RecordType.Name = \''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER+'\' OR RecordType.Name = \''+GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR+'\' ) AND ETL_Record_Deleted__c = false';				
        }
    }
    /**
	* extractAccountDetails Method .
	* <p>
	* This method is used to extract the account details and prepare response. 
	*
	* @param  NA                                 
	* @return NA                
	*/
    public void extractAccountDetails()
    {
        try
        {			
            Id accountId;             
            List<Account> listOfAccount = new List<Account>();            
            List<Account> accSearchResult = new List<Account>();            
            Set<Id> setOfMemberAccountId= new Set<Id>(); 
            Set<Id> setOfVisitorAccountId= new Set<Id>(); 
            Set<Id> setOfActivePolicyMember = new Set<Id>();
            Map<Id,Account> mapOfAccount = new Map<Id,Account>();
            
            if (String.IsNotBlank(memberId))
            { 
                Boolean Switch_2691993 = HUMUtilityHelper.isCRMFunctionalityON('2691993');  
                if(Switch_2691993){
                    List<Member_Id__c> lstMember = new List<Member_Id__c>();
                    List<Member_Id__c> lstFltrMembers = new List<Member_Id__c>();
                    String memQuery = 'FIND \'' + memberId.trim() +'\' IN NAME FIELDS RETURNING Member_Id__c(Id, name, Policy_Member__r.Member__r.Id,  Policy_Member__r.Member__r.LastName,Policy_Member__r.Member__r.PersonMailingPostalCode,Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.PersonEmail ' +
                        + ' Where Policy_Member__c != null AND Policy_Member__r.Member__c != null AND Policy_Member__r.Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.ETL_Record_Deleted__c = false ' +
                        + 'AND Policy_Member__r.Status__c = \''+ GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE +'\' AND Policy_Member__r.Member__r.RecordType.Name = \'' + GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER +'\')';
                   
                    List<List<Sobject>> lstMemberResult = Search.query(memQuery);
                    lstMember = (List<Member_Id__c>) lstMemberResult[0];
                    
                    for(Member_Id__c objMember : lstMember)
                    {
                        if(objMember.Policy_Member__r.Member__r.LastName == request.lastName.trim() && objMember.Policy_Member__r.Member__r.PersonMailingPostalCode == request.zipCode.trim() && objMember.Policy_Member__r.Member__r.Birthdate__c == request.dateOfBirth.trim())
                        {
                            lstFltrMembers.add(objMember);
                        }
                    }
                    
                    if(lstFltrMembers != null && !lstFltrMembers.isEmpty()){
                        if(lstFltrMembers.size() == 1){
                            accountId = lstFltrMembers[0].Policy_Member__r.Member__r.Id;                            
                        }
                        else{   
                            for(Member_Id__c memId : lstFltrMembers){
                                if(String.isNotBlank(memId.Policy_Member__r.Member__r.PersonEmail) && String.isNotBlank(request.emailId) && memId.Policy_Member__r.Member__r.PersonEmail.trim() == request.emailId.trim()){
                                    accountId = memId.Policy_Member__r.Member__r.Id;
                                    break;
                                }                                
                            } 
                            if(accountId == null)
                            {
                                accountId = lstFltrMembers[0].Policy_Member__r.Member__r.Id;
                                createResponse(true, accountId, false, true, '');
                                return;
                            }
                        }
                    }
                }
                else{
                    for(Member_Id__c memId : [Select Id, name, Policy_Member__r.Member__r.Id from Member_Id__c where name =: memberId AND Policy_Member__c != null AND Policy_Member__r.Member__c != null AND Policy_Member__r.Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.Status__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE AND Policy_Member__r.Member__r.RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER LIMIT 1])
                    {
                        accountId = memId.Policy_Member__r.Member__r.Id;
                    }                
                }
                
                if(accountId != null)
                {
                    createResponse(true, accountId, false, true, '');
                    return;
                }
            }

            if(accountId == null && String.IsNotBlank(eQuery))
            {
                if(Switch_2824434){
                    Id vistorRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId(); 
                   
                    Id memberRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER).getRecordTypeId(); 
                   
                    Set<String> setRecordtypeIds = new Set<String>();
                    setRecordtypeIds.add(vistorRecordTypeId);
                    setRecordtypeIds.add(memberRecordTypeId);
                    String accQuery = 'FIND \'' + eQuery +'\' IN NAME FIELDS RETURNING Account(ID, RecordType.Name, PersonEmail, GCM_Visitor_Barcode__c, PersonMailingPostalCode , Birthdate__c, Name,FirstName,LastName '+
                        + ' Where RecordTypeId IN: setRecordtypeIds AND ETL_Record_Deleted__c = false )';
                                   
                    List<List<Sobject>> lstResult = Search.query(accQuery);
                    accSearchResult = (List<Account>) lstResult[0];
                }
                else{
                    String accQuery = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_EQUERY + eQuery;
                    accSearchResult = Database.query(accQuery);
                }
                
                for (Account accVal : accSearchResult)
                {
                    if (accVal.firstname == request.firstName.trim() && accVal.LastName == request.lastName.trim() && accVal.Birthdate__c == request.dateOfBirth.trim())
                    {
                        listOfAccount.add(accVal);
                    }
                }
                
                if(listOfAccount != null && !listOfAccount.isEmpty())
                {	
                    for(Account objAcc : listOfAccount)
                    {	
                        mapOfAccount.put(objAcc.Id, objAcc); 							
                        
                        if(objAcc.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                            setOfMemberAccountId.add(objAcc.Id); 
                        
                        if(objAcc.RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR)
                            setOfVisitorAccountId.add(objAcc.Id);
                    } 
                    
                    if(setOfMemberAccountId != null && !setOfMemberAccountId.isEmpty())
                    {
                        List<Policy_Member__c> listPolicyMember = [SELECT Id, Member__r.PersonEmail, Member__r.Id FROM Policy_Member__c WHERE Member__r.Id IN: setOfMemberAccountId AND Member__r.ETL_Record_Deleted__c = false AND ETL_Record_Deleted__c = false AND Status__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE];
                        if (listPolicyMember != null && !listPolicyMember.isEmpty())
                        {
                            if(listPolicyMember.size() == 1)
                            {
                                if(String.isNotBlank(request.emailId) && String.isNotBlank(listPolicyMember[0].Member__r.PersonEmail))
                                {
                                    if(listPolicyMember[0].Member__r.PersonEmail == request.emailId.trim())
                                    {
                                        accountId = listPolicyMember[0].Member__r.Id;
                                        createResponse(true, accountId, false, true,'');
                                        return;
                                    }
                                }
                                else
                                {
                                    accountId = listPolicyMember[0].Member__r.Id;
                                    createResponse(true, accountId, false, true,'');
                                    return;
                                }
                            }
                            
                            if(listPolicyMember.size() > 1)
                            {                                    
                                boolean isEmailMatch = True;
                                for(Policy_Member__c objPolicy : listPolicyMember)                                   
                                {   
                                    if(String.isNotBlank(request.emailId) && String.isNotBlank(objPolicy.Member__r.PersonEmail))
                                    {
                                        if (objPolicy.Member__r.PersonEmail == request.emailId.trim())
                                        {
                                            accountId = objPolicy.Member__r.Id; 
                                            createResponse(true, accountId, false, true,'');
                                            return;
                                        }
                                        else
                                        {
                                            isEmailMatch = false;
                                            continue;
                                        }
                                    }		
                                }
                                
                                if (isEmailMatch && accountId ==  null)
                                {
                                    accountId = listPolicyMember[0].Member__r.Id;
                                    createResponse(true, accountId, false, true,'');
                                    return;
                                }
                            }
                        }
                    }
                    
                    if(accountId == null && setOfVisitorAccountId != null && !setOfVisitorAccountId.isEmpty())
                    {
                        Set<Id> setOfVisitorSameEmail = new Set<Id>();
                        Set<Id> setOfVisitorSameZipCode = new Set<Id>();
                        Set<Id> setForInteraction = new Set<Id>();
                        Set<Id> setOfAccIdsToLoop = new Set<Id>();
                        
                        if(setOfVisitorAccountId.size() == 1 )
                        {
                            String firstAccountId = new List<Id> (setOfVisitorAccountId)[0];
                            if(String.isNotBlank(request.emailId) && String.isNotBlank(mapOfAccount.get(firstAccountId).personEmail))
                            {
                                if(mapOfAccount.get(firstAccountId).personEmail == request.emailId.trim())
                                {
                                    accountID = firstAccountId;
                                    createResponse(true, accountId, false, false,'');
                                    return;
                                }
                            }
                            else
                            {
                                accountID = firstAccountId;
                                createResponse(true, accountId, false, false,'');
                                return;
                            }							
                        }
                        
                        if(setOfVisitorAccountId.size() > 1)
                        {
                            for(Id accId : setOfVisitorAccountId)
                            {	
                                if(String.isNotBlank(request.emailId) && mapOfAccount.get(accId).personEmail != null && mapOfAccount.get(accId).personEmail == request.emailId.trim()) 
                                    setOfVisitorSameEmail.add(accId); 
                            }
                            if(setOfVisitorSameEmail.size() == 1)
                            {	
                                accountID = new List<Id> (setOfVisitorSameEmail)[0];
                                createResponse(true, accountId, false, false,'');
                                return;
                            }							
                            else{
                                setOfAccIdsToLoop = setOfVisitorSameEmail.size() > 0 ? setOfVisitorSameEmail : setOfVisitorAccountId;
                                for(Id accId : setOfAccIdsToLoop)
                                {  
                                    if(String.isNotBlank(request.zipCode.trim()))
                                    { 
                                        if(mapOfAccount.get(accId).PersonMailingPostalCode == request.zipCode.trim())                                       
                                            setOfVisitorSameZipCode.add(accId);
                                    }
                                }
                                
                                if(setOfVisitorSameZipCode.size() == 1)
                                {   
                                    accountId = new List<Id> (setOfVisitorSameZipCode) [0];
                                    createResponse(true, accountId, false, false,'');
                                    return;
                                }								
                                else
                                {
                                    setOfAccIdsToLoop = setOfVisitorSameZipCode.size() > 0 ? setOfVisitorSameZipCode : setOfVisitorAccountId;
                                    List<Storefront_Interaction__c> listOfInteraction = [Select id, Account__c from Storefront_Interaction__c where Account__c IN : setOfAccIdsToLoop order by CreatedDate desc limit 1];
                                    if(listOfInteraction != null && listOfInteraction.size() == 1)
                                        accountId = listOfInteraction[0].Account__c;
                                    else
                                        accountId = setOfVisitorSameZipCode.size() > 1 ? new List<Id>(setOfVisitorSameZipCode)[0] : setOfVisitorSameEmail.size() > 1 ? new List<Id>(setOfVisitorSameEmail)[0] : new List<Id>(setOfVisitorAccountId)[0];                                      
                                    
                                    createResponse(true, accountId, false, false,'');
                                } 
                            }	
                        }
                    }
                    
                    if(accountId == null)
                    {
                        createAccount();  
                    }
                }
                else
                {
                    createAccount();
                }
            }
            else
            {
                createResponse(false,'', false, false , GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENERIC_ERROR_MESSAGE);
            }
        }		
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false,'', false, false ,error);
            HUMExceptionHelper.logErrors(e,'CRMRetailRegisterAccount_H_HUM','extractDetails');            
        }
    }
    /**
	* createAccount Method .
	* <p>
	* This method is used to create a Visitor Account if no member is  found.
	*
	*
	* @param  NA                                    
	* @return NA                 
	*/
    public void createAccount()
    {
        try
        {
            Id recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
            
            if(Schema.sObjectType.Account.isCreateable())
            {
                Integer externalIdLength = Account.Account_External_ID__c.getDescribe().getLength();
                List<Account> lstAcc = new List<Account>();
                Account objAccount = new Account();
                objAccount.FirstName = request.firstName.trim();
                objAccount.LastName = request.lastName.trim();
                objAccount.Birthdate__c = request.dateOfBirth.trim();
                objAccount.PersonMailingPostalCode = request.zipCode.trim();
                objAccount.PersonEmail = String.isNotBlank(request.emailId) ? request.emailId.trim() : '';
                objAccount.Gender__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENDER;
                objAccount.GCM_Visitor_Barcode__c = request.sourceSystem.trim();
                objAccount.PersonMailingCity = String.isNotBlank(request.city) ? request.city.trim() : '';
                objAccount.PersonMailingState = String.isNotBlank(request.state) ? request.state.trim() : '';
                objAccount.Account_External_ID__c = String.isNotBlank(request.externalId) ? request.externalId.left(externalIdLength) : '';
                
                if(recordTypeId != null)
                {
                    objAccount.RecordTypeId = recordTypeId;
                }
                
				lstAcc.add(objAccount);
                SObjectAccessDecision decision = Security.stripInaccessible(AccessType.CREATABLE, lstAcc);
                Set<String> removedFields =  decision.getRemovedFields().get('Account');
                if(removedFields == null || removedFields.isEmpty())
                {
                    insert lstAcc;
                    createResponse(true, lstAcc[0].Id, true, false,'');
                    return;
                } 
                else
                {
                    HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT + ' - ' + removedFields,'CRMRetailRegisterAccount_H_HUM','createUpdateAccount','',''); 
                    createResponse(false, '', false, false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT + ' - ' + removedFields); 	
                    return;
                } 
            }
            else
            {	
                HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT,'CRMRetailRegisterAccount_H_HUM','createUpdateAccount','',''); 
                createResponse(false, '', false, false,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT); 	
                return;
            } 
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false,'', false, false ,error);
            HUMExceptionHelper.logErrors(e,'CRMRetailRegisterAccount_H_HUM','createAccount');          
        }
    }
    /**
	* prepareResponse Method.
	* <p>
	* This method is used to prepare the response.
	*
	* @param  		NA                                 
	* @return 		NA                 
	*/
    public void prepareResponse()
    { 
        String createResponse = System.JSON.serialize(response);
        RestResponse res = RestContext.response;
        res.statusCode = GLOBAL_CONSTANT_STOREFRONT_HUM.INT_CRMRETAIL_STATUSCODE;
        res.addHeader(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_CONTENTTYPE, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_APPLICATION_JSON);
        res.responseBody = Blob.valueOf(createResponse);
    }
    /**
	* createResponse Method.
	* <p>
	* This method is used to create the response which will be sent on the service call.
	*
	* @param  		isSuccess 				boolean variable which holds the success or failure of the request 
	* @param  		accountId 				accountID                                
	* @param  		isNewAccount 			boolean variable which holds whether the account is a new one or an existing one                                
	* @param  		isMemberAccount 		boolean variable which holds whether the account is a member one or a visitor one                                
	* @param  		errorMsg 				error message                                
	* @return 		NA                 
	*/
    public void createResponse(Boolean isSuccess, String accountId, Boolean isNewAccount, Boolean isMemberAccount, String errorMsg)
    {
        response.isSuccess = isSuccess;
        response.accountId = accountId;
        response.isNew = isNewAccount;
        response.isMember = isMemberAccount;
        
        if(String.isNotBlank(errorMsg))
        {
            response.errorMessage = errorMsg;
        }
    }
}