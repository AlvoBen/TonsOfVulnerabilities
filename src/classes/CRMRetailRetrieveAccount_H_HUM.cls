/***********************************************************************************************************************************
* Apex Class Name  : CRMRetailRetrieveAccount_H_HUM 
* Version          : 1.0
* Created Date     : 28th May, 2021
* Function         : Handler class for handling and preparing response.
* Test Class       : CRMRetailRetrieveAccount_T_HUM
****************************************************************************
* Developer Name                             Code Review #           Date                 Description
*---------------------------------------------------------------------------------------------------------------------------------
* Ananya Singh                                  			       5/28/2021              Original Version  2322478
* Ananya                			  		 		     		   9/13/2021              CRUD FLS FIX 
* Ekta									  						   01/07/2022		      REQ:2824434 - Remove Case Sensitivity
**********************************************************************************************************************************/

public with sharing class CRMRetailRetrieveAccount_H_HUM{
    private static CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM response = new CRMRetailVNCResponse_DTO_HUM.CRMRetailRetrieveAccountResponse_DTO_HUM(); 
    private static CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM request = new CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM(); 
    
    /**
	* CRMRetailRetrieveAccount_H_HUM Method constructor.
	* <p>
	* This method is used to call the extractAccountDetails method.
	*
	* @param  		oRequest           Deserialized request                     		
	* @return		 NA
	*/
	
    public CRMRetailRetrieveAccount_H_HUM (CRMRetailVNCRequest_DTO_HUM.CRMRetailRetrieveAccountRequest_DTO_HUM oRequest)
    {
        if(oRequest != null)
        {
            request = oRequest;             
            extractAccountDetails();
        }
        else
        {
			createResponse(false,null, false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG);
        }
        prepareResponse();
    }
    
    
	/**
	* extractAccountDetails Method.
	* <p>
	* This method is used for retrieving the data for preparing response.
	*
	* @param  		 NA                               		
	* @return		 NA
	*/
    
    public void extractAccountDetails()
    {
        try
        {   
            String memberId = '';
            List<Account> lstAccount = new List<Account>();
            List<Account> lstAcc = new List<Account>();
			List<Account> accSearchResult = new List<Account>();
            Set<Id> setOfEmail = new Set<Id>();
            Set<Id> setOfAccIdsToLoop = new Set<Id>();
            Set<Id> setOfZipCode = new Set<Id>();
            Set<Id> setOfVisitorAccountId = new Set<Id>();
            ID accountId;
            Map<Id,Account> mapOfIdWithAccount = new Map<Id,Account>();
            
            lstAccount = [Select Id, FirstName, LastName, Birthdate__c, PersonEmail, PersonMailingPostalCode, PersonMailingCity, PersonMailingState, PersonMailingStreet, CRMRetailNotificationOptOut__c, PersonHomePhone, PersonOtherPhone, Gender__c,RecordType.Name FROM Account WHERE Id =: request.accountId.trim()];
            
            if(lstAccount != null && !lstAccount.isEmpty())
            {
                
                if(lstAccount[0].RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER)
                {
                    List<Member_Id__c> listMember = [Select Id, name, Policy_Member__r.Member__r.Id from Member_Id__c where Policy_Member__r.Member__r.Id =: lstAccount[0].Id AND Policy_Member__c != null AND Policy_Member__r.Member__c != null AND Policy_Member__r.Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.ETL_Record_Deleted__c = false AND Policy_Member__r.Status__c =: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACTIVE AND Policy_Member__r.Member__r.RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER LIMIT 1];
                    
                    if(listMember != null && !listMember.isEmpty())
                    {
                        memberId = listMember[0].name;
                        
						createResponse(true, lstAccount[0], true, memberId, '');
                    }
                    else
                    {
                        Boolean Switch_2824434 = HUMUtilityHelper.isCRMFunctionalityON('2824434');
                        if(Switch_2824434){
                            String eQuery = lstAccount[0].firstName.trim() + ' AND ' + lstAccount[0].lastName.trim();   
                            Id vistorRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId(); 
                            String accQuery = 'FIND \'' + eQuery +'\' IN NAME FIELDS RETURNING Account(Id,FirstName, LastName, Birthdate__c, GCM_Visitor_Barcode__c, PersonEmail, PersonMailingPostalCode, PersonMailingCity, PersonMailingState, PersonMailingStreet,CRMRetailNotificationOptOut__c, PersonHomePhone, PersonOtherPhone, Gender__c,RecordType.Name '+
                                + ' Where RecordTypeId = \'' + vistorRecordTypeId + '\')';
                            List<List<Sobject>> lstResult = Search.query(accQuery);
                            accSearchResult = (List<Account>) lstResult[0];
                        }
                        else{
                             accSearchResult = [Select Id,FirstName, LastName, Birthdate__c, GCM_Visitor_Barcode__c, PersonEmail, PersonMailingPostalCode, PersonMailingCity, PersonMailingState, PersonMailingStreet,CRMRetailNotificationOptOut__c, PersonHomePhone, PersonOtherPhone, Gender__c,RecordType.Name FROM Account WHERE RecordType.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR AND FirstName =: lstAccount[0].firstName AND LastName =: lstAccount[0].lastName];
                        }
                        
                        for (Account accVal : accSearchResult){
                            if (accVal.FirstName == lstAccount[0].firstName.trim() && accVal.LastName == lstAccount[0].lastName.trim() && accVal.Birthdate__c == lstAccount[0].Birthdate__c)
                            {
                                lstAcc.add(accVal);
                            }
                        }
                        
                        if(lstAcc != null && !lstAcc.isEmpty())
                        {
                            
                            if(lstAcc.size() == 1)
                            {
                                if (String.isBlank(lstAcc[0].PersonEmail) || String.isBlank(lstAccount[0].PersonEmail))
								{
                                    createUpdateAccount(lstAcc[0],lstAccount[0]);
								}
								else 
								{
									if(String.isNotBlank(lstAcc[0].PersonEmail) && String.isNotBlank(lstAccount[0].PersonEmail) && lstAcc[0].PersonEmail == lstAccount[0].PersonEmail)
									{
                                        createUpdateAccount(lstAcc[0],lstAccount[0]);
									}
									else
									{
                                        createUpdateAccount(null,lstAccount[0]);
									}
								}
                            }
                            else if(lstAcc.size() > 1)
                            {
                                for(Account objAcc : lstAcc)
                                {
                                    mapOfIdWithAccount.put(objAcc.Id,objAcc);
                                    setOfVisitorAccountId.add(objAcc.Id);
                                }
                                
								if(lstAccount[0].PersonEmail != null)
								{
                                    for(Account obj : lstAcc)
									{
										if(lstAccount[0].PersonEmail == obj.PersonEmail)
										{
                                            setOfEmail.add(obj.Id);
										}
									}
								}
								
								if(setOfEmail.size() == 1)
								{
                                    accountId = new list<Id> (setOfEmail)[0];
				
									createUpdateAccount(mapOfIdWithAccount.get(accountId), lstAccount[0]);
								}
								else
								{
                                    setOfAccIdsToLoop = setOfEmail.size() > 0 ? setOfEmail : setOfVisitorAccountId;
									if(lstAccount[0].PersonMailingPostalCode != null)
									{
										for(String str : setOfAccIdsToLoop)
										{	
											if(lstAccount[0].PersonMailingPostalCode == mapOfIdWithAccount.get(str).PersonMailingPostalCode)
											{
                                                setOfZipCode.add(str);
											}
										}
									}
												
									if(setOfZipCode.size() == 1)
									{
										accountId = new List<Id> (setOfZipCode)[0];

										createUpdateAccount(mapOfIdWithAccount.get(accountId), lstAccount[0]);												
									}
									else
									{	
										setOfAccIdsToLoop = setOfZipCode.size() > 0 ? setOfZipCode : setOfEmail.size() > 0 ? setOfEmail : setOfVisitorAccountId;
									
										List<Storefront_Interaction__c> listOfInteraction = [Select id, Account__c from Storefront_Interaction__c where Account__c IN : setOfAccIdsToLoop order by CreatedDate desc limit 1];
										
										if(listOfInteraction!= null  && listOfInteraction.size() == 1)
										{
											accountId = listOfInteraction[0].Account__c;
											
											createUpdateAccount(mapOfIdWithAccount.get(accountId), lstAccount[0]);
										   
										}
										else 
										{
											accountId = setOfZipCode.size() > 1 ? new List<Id>(setOfZipCode)[0] : setOfEmail.size() > 1 ? new List<Id>(setOfEmail)[0] : new List<Id>(setOfVisitorAccountId)[0];
											
											createUpdateAccount(mapOfIdWithAccount.get(accountId), lstAccount[0]);
											
										}	
										
									}
								} 
                            }
                        }
                        else 
                        {
                            createUpdateAccount(null,lstAccount[0]);
							return;
                        }
                    }    
                    
                }
                else if(lstAccount[0].RecordType.Name == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR)
                {
                   createResponse(true, lstAccount[0], false, '', '');
				   return;
                }
            }
            else
            {
                createResponse(false, null, false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_NO_MATCHING_ACCOUNT);
				return;
            }
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false, null, false ,'', error);
            HUMExceptionHelper.logErrors(e,'CRMRetailRetrieveAccount_H_HUM','createAccount');
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
	* createUpdateAccount Method.
	* <p>
	* This method is used to create and update the account.
	*
	* @param  		oAcc
	* @param  		accToRefer
	* @return 		NA                 
	*/	
    
	public void createUpdateAccount(Account oAcc, Account accToRefer)
    {
        try
        {
            if(oAcc != null)
            {
                List<Account> listAcc = new List<Account>();
				if(Schema.sObjectType.Account.isUpdateable())
                {
					if(String.isNotBlank(accToRefer.PersonMailingStreet) && oAcc.PersonMailingStreet != accToRefer.PersonMailingStreet)
					{
						oAcc.PersonMailingStreet = accToRefer.PersonMailingStreet;
					}
					
					if(String.isNotBlank(accToRefer.CRMRetailNotificationOptOut__c) && oAcc.CRMRetailNotificationOptOut__c != accToRefer.CRMRetailNotificationOptOut__c)
					{
						oAcc.CRMRetailNotificationOptOut__c = accToRefer.CRMRetailNotificationOptOut__c;
					}
					
					if(String.isNotBlank(accToRefer.PersonHomePhone) && oAcc.PersonHomePhone != accToRefer.PersonHomePhone)
					{
						oAcc.PersonHomePhone = accToRefer.PersonHomePhone;
					}
					
					if(String.isNotBlank(accToRefer.PersonOtherPhone) && oAcc.PersonOtherPhone != accToRefer.PersonOtherPhone)
					{
						oAcc.PersonOtherPhone = accToRefer.PersonOtherPhone;
					}
					
					if(String.isNotBlank(accToRefer.PersonEmail) && oAcc.PersonEmail != accToRefer.PersonEmail)
					{
						oAcc.PersonEmail = accToRefer.PersonEmail;
					}
				
					listAcc.add(oAcc);
               
                    SObjectAccessDecision decision = Security.stripInaccessible(AccessType.UPDATABLE, listAcc);
					Set<String> removedFields =  decision.getRemovedFields().get('Account');
                    if(removedFields == null || removedFields.isEmpty())
                    {
                        update listAcc;
						createResponse(true, listAcc[0], false, '','');
                        return;
                    }
                    else
                    {
                        HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT + ' - ' + removedFields,'CRMRetailRetrieveAccount_H_HUM','createUpdateAccount','',''); 
                        createResponse(false, null, false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT + ' - ' + removedFields);	
						return;
                    }
                }
                else
                {
                    HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT,'CRMRetailRetrieveAccount_H_HUM','createUpdateAccount','',''); 
                    createResponse(false, null, false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_UPDATE_EVENT_TYPE + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT);	
                    return;
                }
            }
            else
            {
				
                if(Schema.sObjectType.Account.isCreateable())
                {
					Id recordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_RETAIL_VISITOR).getRecordTypeId();
					List<Account> lstAccCreate = new List<Account>();
					Account objAccount = new Account();
					objAccount.GCM_Visitor_Barcode__c = request.sourceSystem.trim();
					objAccount.LastName = accToRefer.LastName;
					objAccount.FirstName = accToRefer.FirstName;
					
					if(String.isNotBlank(accToRefer.Birthdate__c))
					{
						objAccount.Birthdate__c = accToRefer.Birthdate__c;
					}
						
					if(String.isNotBlank(accToRefer.PersonMailingPostalCode))
					{
						objAccount.PersonMailingPostalCode = accToRefer.PersonMailingPostalCode;
					}
					
					if(String.isNotBlank(accToRefer.PersonEmail))
					{
						objAccount.PersonEmail = accToRefer.PersonEmail;
					}
					
					if(accToRefer.Gender__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENDER_MALE || accToRefer.Gender__c == GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENDER_FEMALE)
					{
						objAccount.Gender__c = accToRefer.Gender__c;
					}
					else
					{
						objAccount.Gender__c = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_GENDER;
					}
					
					if(String.isNotBlank(accToRefer.PersonMailingState))
					{
						objAccount.PersonMailingState = accToRefer.PersonMailingState;
					}
					
					if(String.isNotBlank(accToRefer.PersonMailingStreet))
					{
						objAccount.PersonMailingStreet = accToRefer.PersonMailingStreet;
					}
					
					if(String.isNotBlank(accToRefer.PersonMailingCity))
					{
						objAccount.PersonMailingCity = accToRefer.PersonMailingCity;
					}
					
					if(String.isNotBlank(accToRefer.PersonOtherPhone))
					{
						objAccount.PersonOtherPhone = accToRefer.PersonOtherPhone;
					}
					
					if(String.isNotBlank(accToRefer.PersonHomePhone))
					{
						objAccount.PersonHomePhone = accToRefer.PersonHomePhone;
					}
					
					if(String.isNotBlank(accToRefer.CRMRetailNotificationOptOut__c))
					{
						objAccount.CRMRetailNotificationOptOut__c = accToRefer.CRMRetailNotificationOptOut__c;
					}
					
					if(recordTypeId != null) 
					{
						objAccount.RecordTypeId = recordTypeId;
					}
					
					lstAccCreate.add(objAccount);
                	SObjectAccessDecision decision = Security.stripInaccessible(AccessType.CREATABLE, lstAccCreate);
                    Set<String> removedFields =  decision.getRemovedFields().get('Account');
                    if(removedFields == null || removedFields.isEmpty())
                    {
                        insert lstAccCreate;
                        createResponse(true, lstAccCreate[0], false,'', '');	
                        return;
                    }
                    else
                    {
                        HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT + ' - ' + removedFields,'CRMRetailRetrieveAccount_H_HUM','createUpdateAccount','',''); 
                        createResponse(false, null, false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENTFIELD_ACCESS + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT + ' - ' + removedFields);	
                        return;
                    }
                }
                else
                {
                    HUMExceptionHelper.logError(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT,'CRMRetailRetrieveAccount_H_HUM','createUpdateAccount','',''); 
                    createResponse(false, null, false,'', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSUFFICIENT_ACCESS + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_INSERT_EVENT_TYPE + ' ' + GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ACCOUNT);	
                	return;
                }
            }
        }
        catch(Exception e)
        {
            String error = CRMRetailVNC_Helper_HUM.getError(e.getMessage());
            createResponse(false, null, false, '', GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_ERROR_MSG + error);
            HUMExceptionHelper.logErrors(e,'CRMRetailRetrieveAccount_H_HUM','createUpdateAccount');          
        }
    }
    
    /**
	* createResponse Method.
	* <p>
	* This method is used for creating the response.
	*
	* @param  		 isSuccess             Boolean
	* @param         lstAccount            List
	* @param         isMember              Boolean
	* @param         memberId              String
	* @param         errorMessage          String
	* @return		 NA
	*/

	public void createResponse(Boolean isSuccess, Account oAccount, Boolean isMember, String memberId, String errorMessage)
	{
		response.isSuccess = isSuccess;
		response.errorMessage = errorMessage;
		response.isMember = isMember;
		response.memberId = memberId;

		if(oAccount != null)
		{
            response.accountId = oAccount.Id;
			response.firstName = oAccount.firstName;
			response.lastName = oAccount.lastName;
			response.dateOfBirth = oAccount.Birthdate__c;
			response.emailId = oAccount.PersonEmail;
			response.zipCode = oAccount.PersonMailingPostalCode;
			response.city = oAccount.PersonMailingCity;
			response.state = oAccount.PersonMailingState;
		}
	}
}