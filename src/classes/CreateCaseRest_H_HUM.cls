/*******************************************************************************************************************************
Apex Class Name :  CreateCaseRest_H_HUM.cls
Version: 1.0
Created On      : 11/14/2018 
Function        : Helper class for CreateCaseRest_D_HUM.cls
Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Charan konakandla         314128                     11/14/2018                   Original Version
* Seema Kaulgi                                         07/04/2019                   REQ - 403811 GBO Watson chat (Group)
* Asish Behera                                         03/13/2020                   Watson unknown Provider Issue
* Sagar MN					                           02/10/2021	                US1867250 - Added ETL Record deleted flag to queries
* Sagar MN											   03/04/2021		    		US 1999913 Code checkin for MF1
* Vaishnavi Manda									   05/12/2021					US 2163327 - MF6 Checkmarx scan issue fix
********************************************************************************************************************************/
public with sharing class CreateCaseRest_H_HUM 
{

	/**
	* Method is used to insert UnknownAccounts
	* 
	* @param    Map<String,Account> acctMapToInsert,Map<String,String> memAcctMap
	* @return   Map<String,String 
	*/    
	public static Map<String,String> insertUnknownAccounts(Map<String,Account> acctMapToInsert,Map<String,String> memAcctMap)
	{
			List<Account> lstAccts = new List<Account>();
			Boolean isExistingAccount = false;
			if(acctMapToInsert.values() != null && !acctMapToInsert.values().isEmpty())
			{
				for(Account accountRec:acctMapToInsert.values())
				{
					if(null==accountRec.id)
					{
						lstAccts.add(accountRec);
					}
				}
			}
			
			Boolean isError=false;
			List<ConsumerID__c> consumerlist = new List<ConsumerID__c>();
			list<Database.saveResult> saveResult;
			if(lstAccts!=null && !lstAccts.isEmpty())
			{
				 saveResult = Database.insert(lstAccts, false);
			}
			isError = HUMExceptionHelper.processSaveResults(saveResult, lstAccts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
			if(!isError)
			{
			    for(Account accountRec:lstAccts)
			    {
			    	if(accountRec.Account_External_Id__c == null && accountRec.id!=null )
			    	{
			    		accountRec.Account_External_Id__c = HUMConstants__c.getInstance('UNKNOWN_PROVIDER_EXTID').Stringvalue__c + accountRec.id;
			    	}
			    }
			    saveResult = Database.update(lstAccts, false);
				isError = HUMExceptionHelper.processSaveResults(saveResult, lstAccts, 'HUMUnknownAccountController', 'processSaveResults', 'Account');
			    if(!isError || isExistingAccount)
			    {
			             if((lstAccts!=null && !lstAccts.isEmpty())||isExistingAccount)
						 {
						 	for(String sInteractionType:acctMapToInsert.keySet())
						 	{
						 		 memAcctMap.put(sInteractionType,acctMapToInsert.get(sInteractionType).id);
						 	}
						 }                      
			    }
			}
			if(isError)
			{
    			throw new HUMCustomException(GLOBAL_CONSTANT_HUM.ERRORMSG_CASESERVICE);
			}
		    return memAcctMap;
		}
	
    /**
	* Method is used to  upsert Account Records
	* 
	* @param    Map<String,Account> accountMap
	* @return   List<Account>
	*/   
	public static List<Account> upsertAccountRec(Map<String,Account> accountMap)
	{
		List<Account> uniqueAccountLst=new List<Account>();
		Set<Account> acctSet = new Set<Account>();
		acctSet.addAll(accountMap.values());
		uniqueAccountLst.addAll(acctSet);
		uniqueAccountLst = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(uniqueAccountLst);
	    return uniqueAccountLst;
	}
	
    /**
	* Method is used to upsert consumer Ids records
	* 
	* @param    Map<String,Account> accountMap
	* @return   List<Account>
	*/
	public static Map<String,String> upsertconsumerIds( Map<String,String> memberAcctMap , Map<String,Account> accountMap, String rectype)
	{
		try
		{
		    String sCommonAcctId='';
			String sIntTypeCommon='';
			Map<String,List<String>> fetchTaxIdMap=new Map<String,List<String>>();
		 	Set<ConsumerID__c> uniqueConsumerSet=new Set<ConsumerID__c>();
		 	for(String Interactiontype:accountMap.keySet())
		 	{
				if(accountMap.get(Interactiontype).id!=null)
				{
					if(String.isBlank(sCommonAcctId))
					{
						sCommonAcctId=accountMap.get(Interactiontype).id;
					}
					if(String.isBlank(sIntTypeCommon)){
						sIntTypeCommon= Interactiontype;
					}
				}
				if(rectype.contains(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE))
		 		{
			 		if(CaseServiceProvider_H_HUM.taxIdMap.get(Interactiontype)!=null && !CaseServiceProvider_H_HUM.taxIdMap.get(Interactiontype).isEmpty())
			 		{
			 			fetchTaxIdMap.putAll(CaseServiceProvider_H_HUM.taxIdMap);
			 		}
			 		else if(CreateCaseRest_D_HUM.bisUnique!=null && CreateCaseRest_D_HUM.bisUnique && NULL==CaseServiceProvider_H_HUM.taxIdMap.get(Interactiontype)&& String.isNotBlank(sIntTypeCommon) )
			 		{
			 			fetchTaxIdMap.put(sIntTypeCommon,CaseServiceProvider_H_HUM.taxIdMap.get(sIntTypeCommon));
			 		}
		 			if(fetchTaxIdMap!=null && !fetchTaxIdMap.isEmpty() && fetchTaxIdMap.get(Interactiontype)!=null && fetchTaxIdMap.get(Interactiontype).size()>0)
		 			{
		 				for(String sTaxId :fetchTaxIdMap.get(Interactiontype))
				 		{
				 			if(accountMap.get(Interactiontype).id!=null && String.isNotBlank(sTaxId))
					 		{
					 		    uniqueConsumerSet.add(insertConsumerIds(sTaxId,accountMap.get(Interactiontype).id,GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID));	
					 			memberAcctMap.put(Interactiontype,accountMap.get(Interactiontype).id);	
					 		}
					 		else if(accountMap.get(Interactiontype).id==null)
					 		{
					 			uniqueConsumerSet.add(insertConsumerIds(sTaxId,sCommonAcctId,GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID));	
					 			memberAcctMap.put(Interactiontype,sCommonAcctId);	
					 		}
				 		}
		 			}
		 			else if(accountMap.get(Interactiontype).id!=null && (fetchTaxIdMap.get(Interactiontype)==null||fetchTaxIdMap.get(Interactiontype).isEmpty()))
		 			{
		 				memberAcctMap.put(Interactiontype,accountMap.get(Interactiontype).id);
		 			}
		 		}
		 	}
	 		if(uniqueConsumerSet!=null)
	 		{
	 			CreateCaseRest_D_HUM.consumerLstUpsert.addAll(uniqueConsumerSet);
	 		}
		 	if(CreateCaseRest_D_HUM.consumerLstUpsert!=null && !CreateCaseRest_D_HUM.consumerLstUpsert.isEmpty())
		 	{
		 		HUMInteractionMemberDmlHelper.upsertConsumerIDofAccount(CreateCaseRest_D_HUM.consumerLstUpsert);
		 	}
		 	return memberAcctMap;
		}
		catch(Exception ex)
		{
	 		HUMExceptionHelper.logErrors(ex,'CaseServiceRest_H_HUM','upsertconsumerIds');
	 		return null; 
		}
	}
	
	/**
	* Method is used to insert consumer Ids records
	* 
	* @param    Map<String,Account> accountMap
	* @return   List<Account>
	*/
	public static ConsumerID__c insertConsumerIds(String sTaxId, String AccountId, string sType)
	{
		ConsumerID__c consumerRec = new ConsumerID__c();
		consumerRec.Account__c = AccountId;
		consumerRec.Consumer_ID__c = sTaxId;
		consumerRec.ID_Type__c = sType;
		consumerRec.Consumer_External_ID__c = getConsumerExternalID(sTaxId,AccountId);
		return consumerRec;
	}
	
	/**
    * Method to Search for Consumer Id
    * @param    String sConsumerTaxId ,String accID
    * @return   None 
    */	
    public static String getConsumerExternalID(String sConsumerTaxId , String accID)
    {
        String sFinalExternalID='';
        if(String.isNotBlank(sConsumerTaxId) &&  String.isNotBlank(accID))
        {   
            List<Account> oAccount =[Select Enterprise_id__c,RecordType.Name,Agent_id__c from Account where Id=:accID AND ETL_Record_Deleted__c=False];
            if(oAccount!=null && oAccount.size()>0)
            {           
                String pipeCharacter=   HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
                String sExternalID='';
                String sMD5String='';
                String sMD5ID ='';
                String sTaxID = HUMConstants__c.getInstance('CONSUMERID_TAXID').StringValue__c;

                if(oAccount[0].RecordType.Name==HUMConstants__c.getInstance('PROVIDER_RECORDTYPE').StringValue__c || oAccount[0].RecordType.Name==HUMConstants__c.getInstance('UNKNOWN_PROVIDER_RECORDTYPE').StringValue__c)
                {
                    sMD5String=HUMConstants__c.getInstance('CONSUMER_EXTID_PROVIDER').StringValue__c;
                    sExternalId=HUMConstants__c.getInstance('CONSUMERID_EXTID_ID_PROVIDER').StringValue__c;
                    sMD5ID=oAccount[0].Enterprise_id__c;
                }
                String sFinalMD5String = sMD5String;
                //Custom setting has a place holder in the form of ###-Replace this with Tax ID
                sFinalMD5String = sFinalMD5String.replace('###',sConsumerTaxId);
                sFinalExternalID =  sExternalID + pipeCharacter + HUMUtilityHelper.getMD5Hash(sFinalMD5String+sMD5ID) + pipeCharacter + sMD5ID;
          } 
         }
         return sFinalExternalID;
        }
		
	/**
	* Method is used toget values of particular Interaction type.
	* 
	* @param    CaseServiceRestCreateRequest_DTO_HUM caseRequestObject, string interactingType
	* @return   CaseServiceRestInteraction_DTO_HUM
	*/
	public static CaseServiceRestInteraction_DTO_HUM getInteractionWrapObj(CaseServiceRestCreateRequest_DTO_HUM caseRequestObject, string interactingType)
	{
		CaseServiceRestInteraction_DTO_HUM	interactionWrapObj =new CaseServiceRestInteraction_DTO_HUM();
		if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))
        {             
                interactionWrapObj.sIntID = caseRequestObject.CreateCaseRequest.InteractingWithId;
                interactionWrapObj.sIntIDType = caseRequestObject.CreateCaseRequest.InteractingWithIdType;
                interactionWrapObj.sIntType = caseRequestObject.CreateCaseRequest.InteractingWithType;
                interactionWrapObj.sAccountName = caseRequestObject.CreateCaseRequest.InteractingWithName;
                interactionWrapObj.sFirstName = caseRequestObject.CreateCaseRequest.InteractingWithFirstName;
                interactionWrapObj.sLastname = caseRequestObject.CreateCaseRequest.InteractingWithLastName;
                interactionWrapObj.sBillingState = caseRequestObject.CreateCaseRequest.InteractingWithState;
                interactionWrapObj.sStateCode = evaluateStateCode(caseRequestObject.CreateCaseRequest.InteractingWithState);
                interactionWrapObj.sProviderClassification = caseRequestObject.CreateCaseRequest.InteractingWithProviderClassification;
                interactionWrapObj.sgrpNum =  caseRequestObject.CreateCaseRequest.InteractingWithId;
                interactionWrapObj.sInteractingZipCode = caseRequestObject.CreateCaseRequest.InteractingWithZipCode;
                interactionWrapObj.sInteractingHomePhone = caseRequestObject.CreateCaseRequest.InteractingWithHomePhone;
                interactionWrapObj.sInteractingEmailID = caseRequestObject.CreateCaseRequest.InteractingWithEmailID;
			if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID)
			{
			 	interactionWrapObj.sTaxId = caseRequestObject.CreateCaseRequest.InteractingWithID;
			}
			else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID)
			{							
				interactionWrapObj.sNPIID = caseRequestObject.CreateCaseRequest.InteractingWithId;
			}
			else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_AGENTID)
			{
				interactionWrapObj.sAgentId = caseRequestObject.CreateCaseRequest.InteractingWithId;
			}
	    }
        else if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))
        {
                interactionWrapObj.sIntID = caseRequestObject.CreateCaseRequest.InteractingAboutId;
                interactionWrapObj.sIntIDType = caseRequestObject.CreateCaseRequest.InteractingAboutIdType;
                interactionWrapObj.sIntType = caseRequestObject.CreateCaseRequest.InteractingAboutType;
                interactionWrapObj.sAccountName = caseRequestObject.CreateCaseRequest.InteractingAboutName;
                interactionWrapObj.sFirstName = caseRequestObject.CreateCaseRequest.InteractingAboutFirstName;
                interactionWrapObj.sLastname = caseRequestObject.CreateCaseRequest.InteractingAboutLastName;
                interactionWrapObj.sBillingState = caseRequestObject.CreateCaseRequest.InteractingAboutState;
                interactionWrapObj.sStateCode=evaluateStateCode(caseRequestObject.CreateCaseRequest.InteractingAboutState);
                interactionWrapObj.sProviderClassification= caseRequestObject.CreateCaseRequest.InteractingAboutProviderClassification;
                interactionWrapObj.sgrpNum =  caseRequestObject.CreateCaseRequest.InteractingAboutId;
                interactionWrapObj.sInteractingZipCode=caseRequestObject.CreateCaseRequest.InteractingAboutZipCode;
                interactionWrapObj.sInteractingHomePhone=caseRequestObject.CreateCaseRequest.InteractingAboutHomePhone;
                interactionWrapObj.sInteractingEmailID=caseRequestObject.CreateCaseRequest.InteractingAboutEmailID;
			if(interactionWrapObj.sIntIDType==GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID)
			{
			 	interactionWrapObj.sTaxId = caseRequestObject.CreateCaseRequest.InteractingAboutId;
			}
			else if(interactionWrapObj.sIntIDType==GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID)
			{							
				interactionWrapObj.sNPIID= caseRequestObject.CreateCaseRequest.InteractingAboutId;
			}
			else if(interactionWrapObj.sIntIDType==GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_AGENTID)
			{
				interactionWrapObj.sAgentId= caseRequestObject.CreateCaseRequest.InteractingAboutId;
			}
		 }
				return interactionWrapObj;
	}
	
	/**
	* Method is used to get unknown account mapping.
	* 
	* @param    CaseServiceRestInteraction_DTO_HUM interactionWrapObj
	* @return   Account
	*/
	public static Account getUnknownAccountRecord(CaseServiceRestInteraction_DTO_HUM interactionWrapObj)
	{
		Account oUnknownAcc = new Account();
		Boolean bIsNotIndividual=false;
		//watson
        If(InteractionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID)
        {    
            
            if(Schema.sObjectType.Account.fields.BillingStateCode.isCreateable() ||	Schema.sobjectType.Account.fields.BillingStateCode.isUpdateable())
                oUnknownAcc.BillingStateCode = InteractionWrapObj.sBillingState;
            if(Schema.sObjectType.Account.fields.BillingStreet.isCreateable() || Schema.sobjectType.Account.fields.BillingStreet.isUpdateable())
                oUnknownAcc.BillingStreet = '';
            if(Schema.sObjectType.Account.fields.BillingCity.isCreateable() || Schema.sobjectType.Account.fields.BillingCity.isUpdateable())
                oUnknownAcc.BillingCity = '';
            if(Schema.sObjectType.Account.fields.BillingCountryCode.isCreateable() || Schema.sobjectType.Account.fields.BillingCountryCode.isUpdateable())
                oUnknownAcc.BillingCountryCode = GLOBAL_CONSTANT_HUM.BILLING_COUNTRY_CODE;
            if(Schema.sObjectType.Account.fields.BillingPostalCode.isCreateable() || Schema.sobjectType.Account.fields.BillingPostalCode.isUpdateable())
                oUnknownAcc.BillingPostalCode = '';
            if(Schema.sObjectType.Account.fields.Phone.isCreateable() || Schema.sobjectType.Account.fields.Phone.isUpdateable())
                oUnknownAcc.Phone = InteractionWrapObj.sInteractingHomePhone;
            if(Schema.sObjectType.Account.fields.Work_Email__c.isCreateable() || Schema.sobjectType.Account.fields.Work_Email__c.isUpdateable())
                oUnknownAcc.Work_Email__c = InteractionWrapObj.sInteractingEmailID;
            if(Schema.sObjectType.Account.fields.Source_Platform_Code__c.isCreateable() || Schema.sobjectType.Account.fields.Source_Platform_Code__c.isUpdateable())
                oUnknownAcc.Source_Platform_Code__c = InteractionWrapObj.sPlatformCode;
            if(Schema.sObjectType.Account.fields.Individual_Last_Name__c.isCreateable() ||Schema.sobjectType.Account.fields.Individual_Last_Name__c.isUpdateable() )
                oUnknownAcc.Individual_Last_Name__c = InteractionWrapObj.sLastname;
            if(Schema.sObjectType.Account.fields.Individual_first_Name__c.isCreateable() || Schema.sobjectType.Account.fields.Individual_first_Name__c.isUpdateable())
                oUnknownAcc.Individual_first_Name__c = InteractionWrapObj.sFirstName;
           
            if(!bIsNotIndividual && String.isNotBlank(InteractionWrapObj.sAccountName) && InteractionWrapObj.sAccountName !=  GLOBAL_CONSTANT_HUM.MISSINGNAME_UNKNOWNACCOUNT)
            {
				if(Schema.sObjectType.Account.fields.Name.isCreateable() || Schema.sobjectType.Account.fields.Name.isUpdateable())
                	oUnknownAcc.Name = InteractionWrapObj.sAccountName;
            }
            else if(!bIsNotIndividual && (String.isNotBlank(InteractionWrapObj.sLastname)||String.isNotBlank(InteractionWrapObj.sFirstName)))
            {
               oUnknownAcc.Name = InteractionWrapObj.sFirstName + GLOBAL_CONSTANT_HUM.SPACE_CONSATNT + InteractionWrapObj.sLastname;
           }
            if(String.isBlank(InteractionWrapObj.sAccountName) && String.isNotBlank(InteractionWrapObj.sLastname) && String.isNotBlank(InteractionWrapObj.sFirstName))
            {
               // oUnknownAcc.Name =  GLOBAL_CONSTANT_HUM.MISSINGNAME_UNKNOWNACCT ;
               oUnknownAcc.Name = InteractionWrapObj.sFirstName + GLOBAL_CONSTANT_HUM.SPACE_CONSATNT + InteractionWrapObj.sLastname;
            }
                if(Schema.sObjectType.Account.fields.Description.isCreateable() || Schema.sobjectType.Account.fields.Description.isUpdateable())
                    oUnknownAcc.Description= '';
                if(Schema.sObjectType.Account.fields.RecordTypeId.isCreateable() || Schema.sobjectType.Account.fields.RecordTypeId.isUpdateable())
                    oUnknownAcc.RecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE).getRecordTypeId();
                if(Schema.sObjectType.Account.fields.Group_Number__c.isCreateable() || Schema.sobjectType.Account.fields.Group_Number__c.isUpdateable())
                    oUnknownAcc.Group_Number__c = InteractionWrapObj.sGroupID;
                if(Schema.sObjectType.Account.fields.General_Account__c.isCreateable() || Schema.sobjectType.Account.fields.General_Account__c.isUpdateable())
                    oUnknownAcc.General_Account__c = true;
                if(Schema.sObjectType.Account.fields.Tenant_Id__c.isCreateable() || Schema.sobjectType.Account.fields.Tenant_Id__c.isUpdateable())
                    oUnknownAcc.Tenant_Id__c = label.HUMExtMemAccountTenantID;
            
        }
        //Handling Unknown Provider
		// Added by asish, if TAXID and NPIID not present should be considered as Unknown Provider
        else if(InteractionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE || InteractionWrapObj.sIntIDType == 'NPIID' ||
				InteractionWrapObj.sIntIDType == 'TaxID'){
            if(GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATIONMAP.containsKey(InteractionWrapObj.sProviderClassification))
            {
				 if(Schema.sObjectType.Account.fields.Provider_Classification__C.isCreateable() || Schema.sobjectType.Account.fields.Provider_Classification__C.isUpdateable()) 
                	oUnknownAcc.Provider_Classification__C = GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATIONMAP.get(InteractionWrapObj.sProviderClassification);
				if(Schema.sObjectType.Account.fields.Enterprise_ID__c.isCreateable() || Schema.sobjectType.Account.fields.Enterprise_ID__c.isUpdateable()) 
					oUnknownAcc.Enterprise_ID__c='';
            }
                oUnknownAcc.RecordTypeId = Schema.getGlobalDescribe().get(GLOBAL_CONSTANT_HUM.CASESERVICE_OBJECT_ACCOUNT).getDescribe().getRecordTypeInfosByName().get(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNPROVIDER).getRecordTypeId();      
         
            if(String.isNotBlank(InteractionWrapObj.sProviderClassification) && !(InteractionWrapObj.sProviderClassification).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INDIVIDUAL_PROVIDER_CLASSIFICATION))
            {
                bIsNotIndividual = true;
            }
            
            oUnknownAcc.BillingStateCode = InteractionWrapObj.sBillingState;
            oUnknownAcc.BillingStreet = '';
            oUnknownAcc.BillingCity = '';
            oUnknownAcc.BillingCountryCode = GLOBAL_CONSTANT_HUM.BILLING_COUNTRY_CODE;
            oUnknownAcc.BillingPostalCode = '';
            oUnknownAcc.Phone = InteractionWrapObj.sInteractingHomePhone;
            oUnknownAcc.Work_Email__c = InteractionWrapObj.sInteractingEmailID;
			if(Schema.sObjectType.Account.fields.NPI_ID__c.isCreateable() || Schema.sobjectType.Account.fields.NPI_ID__c.isUpdateable()) 
            	oUnknownAcc.NPI_ID__c = InteractionWrapObj.sNPIID;
            oUnknownAcc.Individual_Last_Name__c = InteractionWrapObj.sLastname;
            oUnknownAcc.Individual_first_Name__c = InteractionWrapObj.sFirstName;
        if(String.isNotBlank(InteractionWrapObj.sProviderClassification) && !(InteractionWrapObj.sProviderClassification).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INDIVIDUAL_PROVIDER_CLASSIFICATION))
        {
            bIsNotIndividual = true;
        }
        
        if(bIsNotIndividual && String.isNotBlank(InteractionWrapObj.sAccountName))
        {
            oUnknownAcc.Name = InteractionWrapObj.sAccountName;
        }
        else if(!bIsNotIndividual && (String.isNotBlank(InteractionWrapObj.sLastname)||String.isNotBlank(InteractionWrapObj.sFirstName)))
        {
            oUnknownAcc.Name = InteractionWrapObj.sFirstName + GLOBAL_CONSTANT_HUM.SPACE_CONSATNT + InteractionWrapObj.sLastname;
        }
        if(String.isBlank(InteractionWrapObj.sAccountName) && String.isNotBlank(InteractionWrapObj.sLastname) && String.isNotBlank(InteractionWrapObj.sFirstName))
        {
            oUnknownAcc.Name =  GLOBAL_CONSTANT_HUM.MISSINGNAME_UNKNOWNACCT ;
        }
            oUnknownAcc.Description= '';                 
            oUnknownAcc.General_Account__c = true;
            oUnknownAcc.Tenant_Id__c = label.HUMExtMemAccountTenantID;
        }
        else if(InteractionWrapObj.sIntIDType != GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE){       
            oUnknownAcc.BillingStateCode = InteractionWrapObj.sBillingState;
            oUnknownAcc.BillingStreet = '';
            oUnknownAcc.BillingCity = '';
            oUnknownAcc.BillingCountryCode = GLOBAL_CONSTANT_HUM.BILLING_COUNTRY_CODE;
            oUnknownAcc.BillingPostalCode = '';
            oUnknownAcc.Phone = InteractionWrapObj.sInteractingHomePhone;
            oUnknownAcc.Work_Email__c = InteractionWrapObj.sInteractingEmailID;
            oUnknownAcc.NPI_ID__c = InteractionWrapObj.sNPIID;
            oUnknownAcc.Individual_Last_Name__c = InteractionWrapObj.sLastname;
            oUnknownAcc.Individual_first_Name__c = InteractionWrapObj.sFirstName;
        if(String.isNotBlank(InteractionWrapObj.sProviderClassification) && !(InteractionWrapObj.sProviderClassification).equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INDIVIDUAL_PROVIDER_CLASSIFICATION))
        {
            bIsNotIndividual = true;
        }
        
        if(bIsNotIndividual && String.isNotBlank(InteractionWrapObj.sAccountName))
        {
            oUnknownAcc.Name = InteractionWrapObj.sAccountName;
        }
        else if(!bIsNotIndividual && (String.isNotBlank(InteractionWrapObj.sLastname)||String.isNotBlank(InteractionWrapObj.sFirstName)))
        {
            oUnknownAcc.Name = InteractionWrapObj.sFirstName + GLOBAL_CONSTANT_HUM.SPACE_CONSATNT + InteractionWrapObj.sLastname;
        }
        if(String.isBlank(InteractionWrapObj.sAccountName) && String.isNotBlank(InteractionWrapObj.sLastname) && String.isNotBlank(InteractionWrapObj.sFirstName))
        {
            //oUnknownAcc.Name =  GLOBAL_CONSTANT_HUM.MISSINGNAME_UNKNOWNACCT ;
            oUnknownAcc.Name = InteractionWrapObj.sFirstName + GLOBAL_CONSTANT_HUM.SPACE_CONSATNT + InteractionWrapObj.sLastname;
        }
        oUnknownAcc.Description= '';
        oUnknownAcc.Agent_ID__c = InteractionWrapObj.sAgentId;
        
        
        oUnknownAcc.General_Account__c = true;
        oUnknownAcc.Tenant_Id__c = label.HUMExtMemAccountTenantID;
    }
        
        return oUnknownAcc;
    }
        
    /**
    * Method is used to evaluate StateCode.
    * 
    * @param    string stateCode
    * @return   string
    */
    private static string evaluateStateCode(string stateCode)
    {
    	//Check if the input state code is available in the Picklist Options, else overwrite it 'OT' for 'Other State code'
        if(!string.isBlank(stateCode) && !stateCode.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE))
        {
            Schema.DescribeFieldResult fieldResult = Account.PERSONMAILINGSTATECODE.getDescribe();
            List<Schema.PicklistEntry> picklistentries = fieldResult.getPickListValues();
            Boolean stateFound = false;                    
            for(Schema.PicklistEntry picklistentry : picklistentries)
            {
                if(picklistEntry.isActive() && picklistEntry.getValue().equalsIgnoreCase(stateCode))
                {
                    stateFound = true;
                }       
            }
            if(!stateFound)
            { 
                stateCode = GLOBAL_CONSTANT_HUM.CASESERVICE_OTHER_STATE_CODE;       
            }
        }
        return stateCode;
    }
}