/*******************************************************************************************************************************
Apex Class Name : MemberSearch_LD_HUM
Version         : 1.0
Created On      : 10/28/2020
Function        : This class is Data Layer Class for MemberSearch_LC_HUM
Test Class      : MemberSearch_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* Ashish Kumar										 				10/29/2020					 	Original Version
* Kajal Namdev                                                      05/07/2021                     Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
* Ashish Kumar										 				05/28/2021					 	Removed references of Memebr_Id__c, Policy_Member__C 
* Pallavi Shewale                                    				07/19/2021                      US: 2364907- Search- Add Humana Pharmacy Account Number to the Search screen
* Ashish Kumar/Ritik										 	    07/22/2021                      Fixed Member Search issue code changes related to US: 2364907- Added Enterprise_ID__c in SOSL				   
* Ashish Kumar                                                      01/10/2021                      Changing constants reference
* Santhi Mandava                                                    09/29/2022                       US3398901- Homeoffice/CPD changes
* Santhi Mandava								                    07/17/2023			            User Story 4782867:Search page results incorrectly displaying
*******************************************************************************************************************************************************************/

public without sharing class MemberSearch_LD_HUM 
{
	private static integer iSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Member_SearchLimit;
    private static integer iConsumerSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Member_SearchLimit;
    Public static Integer limitValue = Integer.valueOf(system.label.iPaginator);
	/*
    * Method Name   :    FieldCipherMaping
    * Description   :    This method is used for FieldCipherMaping
    * Return Type   :    Map<String, String>
    * Parameters    :    MemberSearchWrapper_LH_HUM
    */    
     public static Map<String, String> FieldCipherMaping(MemberSearchWrapper_LH_HUM oMemberSearchWrapperwithCipher)
     {
        Map<String, String> accFieldCipher = new Map<String,String>();                
       
        if(!String.isEmpty(oMemberSearchWrapperwithCipher.sFirstName)) 
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.OBJECT_ACCOUNT_API_FIRSTNAME, oMemberSearchWrapperwithCipher.sFirstName);
                                
        if(!String.isEmpty(oMemberSearchWrapperwithCipher.sLastName)) 
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.OBJECT_ACCOUNT_API_LASTNAME, oMemberSearchWrapperwithCipher.sLastName);
        
        if(!String.isEmpty(oMemberSearchWrapperwithCipher.sBirthdate)) 
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAccountBirthDate, oMemberSearchWrapperwithCipher.sBirthdate);
        
        if(!String.isEmpty(oMemberSearchWrapperwithCipher.sMemberId))
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMName, oMemberSearchWrapperwithCipher.sMemberId);
        
        if(!String.isEmpty(oMemberSearchWrapperwithCipher.sMemberId))
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMConsumerTaxID, oMemberSearchWrapperwithCipher.sMemberId);                        
    
        if(!String.isBlank(oMemberSearchWrapperwithCipher.sPhone)) 
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPersonHomePhone , oMemberSearchWrapperwithCipher.sPhone);
              
        if(!String.isBlank(oMemberSearchWrapperwithCipher.sPostalCode))
            accFieldCipher.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPersonMailingPostalCode , oMemberSearchWrapperwithCipher.sPostalCode);           
        
        if (!String.isBlank(oMemberSearchWrapperwithCipher.sGroupNumber))
            accFieldCipher.put(System.Label.HUMGroupNumber, oMemberSearchWrapperwithCipher.sGroupNumber);                              
        return accFieldCipher;
     }
	 
	 /* 
     * Method Name   :  filterSOSLResults
     * Description   :  This Method is used to filter member id SOSL query results.
     * Return Type   :  List<ConsumerID__c>
     * Parameters    :  List<ConsumerID__c>
     */
   public static List<ConsumerID__c> filterSOSLResultsConsumer(List<ConsumerID__c> lstConsumers,MemberSearchWrapper_LH_HUM oMemberSearchWrapper)
    {
        List<ConsumerID__c> lstConsumersFinal = new List<ConsumerID__c>();
        for(ConsumerID__c obj : lstConsumers)
        {
            String sConsId = obj.Consumer_ID__c;
            If(sConsId.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId))
            {           
                lstConsumersFinal.Add(obj);
            }
        }
        return lstConsumersFinal;
    }
	
	/*
     * Method Name   :  createAccountObjConsumer
     * Description   :  This Method is used to create the Account Objects instances
     * Return Type   :  Account
     * Parameters    :  ConsumerID__c Object instance
     */
    public static Account createAccountObjConsumer(ConsumerID__c consumerObj)
    {
        Account acctObj = new Account();        
        acctObj.ID = consumerObj.Account__r.ID;
        acctObj.Name = consumerObj.Account__r.Name;
        acctObj.FirstName = consumerObj.Account__r.FirstName;
        acctObj.LastName = consumerObj.Account__r.LastName;
        acctObj.Middlename = consumerObj.Account__r.Middlename;
        acctObj.Suffix = consumerObj.Account__r.Suffix;  
        acctObj.Individual_First_Name__c = consumerObj.Account__r.Individual_First_Name__c;
        acctObj.Individual_Last_Name__c = consumerObj.Account__r.Individual_Last_Name__c;
        acctObj.RecordTypeId = consumerObj.Account__r.RecordTypeId;
        acctObj.Birthdate__c = consumerObj.Account__r.Birthdate__c;
        acctObj.Deceased_Date__c = consumerObj.Account__r.Deceased_Date__c ;
        acctObj.PersonEmail = consumerObj.Account__r.PersonEmail;
        acctObj.PersonMailingStreet = consumerObj.Account__r.PersonMailingStreet;
        acctObj.PersonMailingCity = consumerObj.Account__r.PersonMailingCity;
        acctObj.PersonMailingStateCode = consumerObj.Account__r.PersonMailingStateCode;
        acctObj.PersonMailingState = consumerObj.Account__r.PersonMailingState;
        acctObj.PersonMailingPostalCode = consumerObj.Account__r.PersonMailingPostalCode;
        acctObj.PersonHomePhone = consumerObj.Account__r.PersonHomePhone;        
        acctObj.Home_Office_Account__c = consumerObj.Account__r.Home_Office_Account__c;
        acctObj.ETL_Record_Deleted__c = consumerObj.Account__r.ETL_Record_Deleted__c;
        acctObj.Enterprise_ID__c = consumerObj.Account__r.Enterprise_ID__c;
        RecordType recType = new RecordType();
        recType.id = consumerObj.Account__r.RecordTypeId;
        rectype.name = consumerObj.Account__r.RecordType.Name;
        acctObj.recordtype = recType;         
        return acctObj;
    }
    /*
     * Method Name   :  createSearchQueryConsumer
     * Description   :  This Method is called from dataSearch() when ID field value is entered on screen.
     * Return Type   :  Set of accounts ID
     * Parameters    :  Consumer Id
     */
    public static Set<ID> createSearchQueryConsumer(String sConsumerId,MemberSearchWrapper_LH_HUM oMemberSearchWrapper)
    {
        List<ConsumerID__c> lstConsumerId = new List<ConsumerID__c>();    
        String sConsId = oMemberSearchWrapper.sMemberId + '*';
        String sConsIdQuery = 'FIND \'' + sConsId + '\' IN ALL FIELDS RETURNING ConsumerID__c(Consumer_ID__c,Account__c ,Account__r.ID, Account__r.ETL_Record_Deleted__c Where Account__r.ETL_Record_Deleted__c = false Limit ' + iConsumerSearchLimit + ' )';
        List<List<sObject>> searchList = search.query(sConsIdQuery);
        lstConsumerId = ((List<ConsumerID__c>)searchList[0]);
        lstConsumerId = filterSOSLResultsConsumer(lstConsumerId,oMemberSearchWrapper);
   		Set<ID> setAccConsumerId = new Set<Id>();
        for (ConsumerID__c oConsumerId: lstConsumerId)
        {
            setAccConsumerId.add(oConsumerId.Account__r.Id);
        }  
        return setAccConsumerId;
    }
	
	 /*
    * Method Name   :    checkResultSizeLimit
    * Description   :    This method is used for checking maximum/minimum search result size 
                         in member search page   
    * Return Type   :    void
    * Parameters    :    NA
    */
    public static void checkResultSizeLimit(List<Account> lstFinalSearchResult)
    {   
        try
        {          
            if(lstFinalSearchResult.size() == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMConstantZero ) 
            { 
                throw new HUMCustomException(system.Label.HUMMedicareSearchCriteriaViolation); 
            }
            else if(lstFinalSearchResult.size() == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMConstantZero )
                throw new HUMCustomException(System.Label.HUMMemberSearchCriteriaViolation);

        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberSearch_LC_HUM', 'checkResultSizeLimit');
            return;
        }
    
    }
	/*
     * Method Name   :  getSOSLResults
     * Description   :  This Method is used to extract data based on the given query.
     * Return Type   :  List<Member_Id__c>
     * Parameters    :  String 
     */
      public static List<List<sObject>> getMemberIdQueryResults(String searchQuery)
    {       
        List<MemberPlan> lstMembers = new List<MemberPlan>();
        List<List<sObject>> searchList = search.query(searchQuery); 
        lstMembers = ((List<MemberPlan>)searchList[0]);      
        return searchList ;
    }
    

      public static Set<Id> getMemberIdQueryResults2NewMemberPlan(String searchQuery,MemberSearchWrapper_LH_HUM oMemberSearchWrapper)
    {
        List<List<sObject>> searchList = search.query(searchQuery);        
        return filterSOSLResultsNewMemberPlan(searchList,oMemberSearchWrapper) ;
    }
  
	/*
     * Method Name   :  filterSOSLResultsNewMemberPlan
     * Description   :  This Method is used to filter member id SOSL query results.
     * Return Type   :  Set<Id>
     * Parameters    :  List<List<sObject>>
     */
    public static Set<Id> filterSOSLResultsNewMemberPlan(List<List<sObject>> lstMembers,MemberSearchWrapper_LH_HUM oMemberSearchWrapper)
    {
        Set<Id> setAccMemberId= new Set<Id>();
		String sMemberId=oMemberSearchWrapper.sMemberId;
        for(MemberPlan obj : (List<MemberPlan>)lstMembers[0])
        {
            If((String.IsNotBlank(obj.CBIS_ALT_ID__C) && obj.CBIS_ALT_ID__C.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.CERT_ID__C) && obj.CERT_ID__C.startsWithIgnoreCase(sMemberId))
              	||(String.IsNotBlank(obj.CLIENTID__C) && obj.CLIENTID__C.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.CONTRACTID__C) && obj.CONTRACTID__C.startsWithIgnoreCase(sMemberId))
                ||(String.IsNotBlank(obj.CONTRACTID_ETL__C) && obj.CONTRACTID_ETL__C.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.EXCH_MBR_ID__C) && obj.EXCH_MBR_ID__C.startsWithIgnoreCase(sMemberId))
                ||(String.IsNotBlank(obj.MEMBER_ID_BASE__C) && obj.MEMBER_ID_BASE__C.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.TPA_ID__C) && obj.TPA_ID__C.startsWithIgnoreCase(sMemberId))
              	||(String.IsNotBlank(obj.VITALITY_ENTITY_ID__C) && obj.VITALITY_ENTITY_ID__C.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.MEDICAID_ID__C) && obj.MEDICAID_ID__C.startsWithIgnoreCase(sMemberId)))
            { 	
                if((String.isBlank(oMemberSearchWrapper.sSuffix) || (String.isNotBlank(oMemberSearchWrapper.sSuffix) && obj.Member_Dependent_Code__c==oMemberSearchWrapper.sSuffix)) && obj.ETL_Record_Deleted__c==false)
                {
                    setAccMemberId.add(obj.memberId);
                }
            }
        }
        
        for(Account obj : (List<Account>)lstMembers[1])
           {
                if((String.IsNotBlank(obj.SSN__C) && obj.SSN__C.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.MedicareID__c) && obj.MedicareID__c.startsWithIgnoreCase(sMemberId))
                   ||(String.IsNotBlank(obj.MedicareID_HICN__c) && obj.MedicareID_HICN__c.startsWithIgnoreCase(sMemberId))||(String.IsNotBlank(obj.PERSONID__c) && obj.PERSONID__c.startsWithIgnoreCase(sMemberId)))
                {
                	setAccMemberId.add(obj.Id);
                }
           }
        
        return setAccMemberId;
    }
    
    /*
     * Method Name   :  createAccoutObj
     * Description   :  This Method is used to create the Account Objects instances
     * Return Type   :  Account
     * Parameters    :  Member_Id__c Object instance
     */
    public static Account createAccountObj(MemberPlan memObj) //PeerReview
    {
        Account acctObj = new Account();        
        acctObj.ID = memObj.memberId;
        acctObj.Name = memObj.member.Name;
        acctObj.FirstName = memObj.member.FirstName;
        acctObj.LastName = memObj.member.LastName;
        acctObj.middlename = memObj.member.middlename;
        acctObj.Suffix = memObj.member.Suffix;
        acctObj.Individual_First_Name__c = memObj.member.Individual_First_Name__c;
        acctObj.Individual_Last_Name__c = memObj.member.Individual_Last_Name__c;
        acctObj.RecordTypeId = memObj.member.RecordTypeId;
        acctObj.Birthdate__c = memObj.member.Birthdate__c;
        acctObj.Deceased_Date__c = memObj.member.Deceased_Date__c ;
        acctObj.PersonEmail = memObj.member.PersonEmail;
        acctObj.PersonMailingStreet = memObj.member.PersonMailingStreet;
        acctObj.PersonMailingCity = memObj.member.PersonMailingCity;
        acctObj.PersonMailingStateCode = memObj.member.PersonMailingStateCode;
        acctObj.PersonMailingState = memObj.member.PersonMailingState;
        acctObj.PersonMailingPostalCode = memObj.member.PersonMailingPostalCode;
        acctObj.PersonHomePhone = memObj.member.PersonHomePhone;        
        acctObj.Home_Office_Account__c = memObj.member.Home_Office_Account__c;
        acctObj.ETL_Record_Deleted__c = memObj.member.ETL_Record_Deleted__c;
        acctObj.Enterprise_ID__c = memObj.member.Enterprise_ID__c;
        RecordType recType = new RecordType();
        recType.id = memObj.member.RecordTypeId;
        rectype.name = memObj.member.RecordType.Name;
        acctObj.recordtype = recType;
        acctObj.Mbr_Gen_Key__c = memObj.member.Mbr_Gen_Key__c;        
        return acctObj;
    }

    /*
     * Method Name   :  createSearchQuery
    *  Description   :  This Method is for creating a common query
     * Return Type   :  String
     * Parameters    :  MemberSearchWrapper_LH_HUM
     */
         public static String buildMemberIdQuery(MemberSearchWrapper_LH_HUM oMemberSearchWrapper, List<String> lstRecordTypeId)
    {	
        String memberQuery='';
        String sMemberId = oMemberSearchWrapper.sMemberId + '*';
        memberQuery = 'FIND \'' + String.escapeSingleQuotes(sMemberId) + '\' IN ALL FIELDS '+
            'RETURNING MemberPlan(Id,Name,CBIS_ALT_ID__C,CERT_ID__C,ContractID__c,CLIENTID__C,CONTRACTID_ETL__C,EXCH_MBR_ID__C,TPA_ID__C,VITALITY_ENTITY_ID__C,Member_Dependent_Code__c,Member_Id_Base__c,member.Name,member.FirstName,member.LastName,member.middlename,member.Suffix,'+
            'member.Individual_First_Name__c,member.Individual_Last_Name__c,member.RecordTypeId,member.Birthdate__c,member.Deceased_Date__c ,'+
            'member.PersonEmail,member.PersonMailingStreet,member.PersonMailingCity, Medicaid_Id__c ,member.Mbr_Gen_Key__c,member.PersonMailingStateCode,member.PersonMailingState,'+
            'member.PersonMailingPostalCode,member.PersonHomePhone,member.Home_Office_Account__c,member.ETL_Record_Deleted__c, member.SSN__c, member.MedicareID__c,member.MedicareID_HICN__c, member.PERSONID__c, '+
            'member.RecordType.Name,ETL_Record_Deleted__c, member.Enterprise_ID__c Limit 101),'+
            'Account(Id,Name, FirstName, LastName, middlename, Individual_First_Name__c,Individual_Last_Name__c,'+
            'RecordType.Name, RecordType.Id,Birthdate__c, PersonEmail, PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, '+
            'PersonMailingState,Security_Groups__c, PersonMailingPostalCode, PersonHomePhone, Home_Office_Account__c, SSN__c, Mbr_Gen_Key__c, MedicareID__c, MedicareID_HICN__c, PERSONID__c, ETL_Record_Deleted__c, RecordTypeId '+
            'where RecordTypeId IN ' + lstRecordTypeId;                        
        memberQuery += ' Limit ' + iSearchLimit + ')'; 
        
        return memberQuery;
    }
    
        /* 
     * Method Name   :  createSearchQueryNewMemberPlan
     * Description   :  This Method is called from dataSearch() when ID field value is entered on screen.
     * Return Type   :  Set of accounts ID
     * Parameters    :  Member Id
     */
    public static Set<ID> createSearchQueryNewMemberPlan(String sMemId,MemberSearchWrapper_LH_HUM oMemberSearchWrapper)
    {	
        Set<ID> setAccMemberId = new Set<ID>();
        //fetching recordTypeIds
        List<String> lstRecordTypeIds=MemberSearch_LC_HUM.fetchRecordTypeIds(oMemberSearchWrapper);
       	//building query
        String sMemIdQueryNew= buildMemberIdQuery(oMemberSearchWrapper,lstRecordTypeIds);
        setAccMemberId = getMemberIdQueryResults2NewMemberPlan(sMemIdQueryNew,oMemberSearchWrapper);
        return setAccMemberId;
    }    
    
	/*
     * Method Name   :    getListOfsObjects 
     * Description   :    This method accepts String parameters related to SOQL query.
                          It aslo accepts set of Accoun ids, member Ids, Group number.
                          It Queries and returns result in the form of sObject.
     * Return Type   :    List<sObject>
     * Parameters    :    String sSoqlQuery (Query string) 
                          Set <ID> setAccIds 
                          Set <ID> setAccIdForMemberId
                          set <ID> setAccIdForGrpNumber
     */
    public static List<sObject> getListOfsObjects(string sSoqlQuery, 
            Set <ID> setAccId, 
            Set <ID> setAccIdForMemberId, 
            set <ID> setAccIdForGrpNumber)
    {   
        if(setAccId != NULL)
        {
            sSoqlQuery +=' setAccId';
            if(sSoqlQuery.containsIgnoreCase('MemberPlan')) 
            {               
                sSoqlQuery +=  ' order by MemberId desc NULLS LAST Limit ' + GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HumListsizeLimit;               
            }
        }
        return Database.Query(sSoqlQuery);
    } 
	 /*
    * Method Name   :    dataSecurity
    * Description   :    This Method is for Security purpose. Used to display fewer records to External users
    * Return Type   :    void
    * Parameters    :    List<Account>
    */
    public static List<Account> dataSecurity(List<Account> lstMemberAccount)
    {
        List <ID> lstAccountList = new List<ID>();
        Map<Id, Boolean> mapRecordHaveAccess = new Map<Id, Boolean>();
        List<AccountComparisionHelper_H_HUM> lstAccountsToSort = new List<AccountComparisionHelper_H_HUM>();
        for(Integer i = 0 ; i < lstMemberAccount.size() ; i++)
        {
            lstAccountList.add(lstMemberAccount.get(i).Id);
            lstAccountsToSort.Add(New AccountComparisionHelper_H_HUM(lstMemberAccount[i]));
        }
        List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : lstAccountList];
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            mapRecordHaveAccess.put(userAccess.RECORDID, userAccess.HASREADACCESS);
        }  
		MemberSearch_LC_HUM.objMemSerResponse.mapRecordAccess =  mapRecordHaveAccess;
        List<Account> lstFinalSearchResult = new List<Account>();
        lstFinalSearchResult.addAll(lstMemberAccount); 
		return lstFinalSearchResult;
    } 
	/*
        * Method Name   :    buildEnterpriseIDQuery
        * Description   :    This method is used to get Records based on enterprise ID 
        * Return Type   :    void
        * Parameters    :    NA
        */   
    public static void buildEnterpriseIDQuery(String enterpriseID)
    {
        try
        {               
            List<Account> accountList=new List<Account>();
            if(enterpriseID!=null && String.isNotBlank(enterpriseID))
            {  
                accountList = [SELECT ID,Name,FirstName, LastName, Middlename,Suffix, Individual_First_Name__c,Individual_Last_Name__c, Birthdate__c,Deceased_Date__c,PersonEmail,PersonMailingStreet,
                               PersonMailingCity, PersonMailingStateCode, PersonMailingState, PersonMailingPostalCode,PersonHomePhone,RecordType.Name, 
                               ETL_Record_Deleted__c,Enterprise_ID__c, Home_Office_Account__c, UserRecordAccess.HasReadAccess FROM Account WHERE Enterprise_ID__c =:enterpriseID LIMIT :limitValue];
                if(accountList!=null &&!accountList.isEmpty())
                {
                    MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(accountList);
                }
            }
        }
        catch(Exception ex)
        {
              HUMExceptionHelper.logErrors(ex, 'MemberSearch_LC_HUM', 'buildEnterpriseIDQuery');
        } 
    }
    
    /*
     * Method Name   :  SearchConsumer
     * Description   :  This Method is called for Manual Search from UI(both for Search button click and hit of Enter key) on complete of Search Manual method    
     * Return Type   :  void
     * Parameters    :  NA
     */
	public static void SearchConsumer(MemberSearchWrapper_LH_HUM oMemberSearchWrapper, List<Account> lstFinalSearchResult,boolean memberIdSearch,string sConsumerSearchQuery,boolean bIsNameSearch,Map<string, string> mapEncryptedResult)
	{
	   try
	   {
		   if(oMemberSearchWrapper.sUnknownMemCheck)
		   {
			   if(NULL != lstFinalSearchResult && !lstFinalSearchResult.isEmpty())
			   {
				   iConsumerSearchLimit = iConsumerSearchLimit - lstFinalSearchResult.size();
			   }              
			   sConsumerSearchQuery = BuildQueryConsumer(memberIdSearch,oMemberSearchWrapper,bIsNameSearch,sConsumerSearchQuery,iConsumerSearchLimit,mapEncryptedResult);
			   List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
			   if(memberIdSearch)
			   {
				   List<List<sObject>> searchList = search.query(sConsumerSearchQuery);
				   lstConsumers = ((List<ConsumerID__c>)searchList[0]);
				   lstConsumers = filterSOSLResultsConsumer(lstConsumers,oMemberSearchWrapper);    
				   if(lstConsumers.isEmpty())
				   {
					   if(NULL != lstFinalSearchResult && !lstFinalSearchResult.isEmpty())
					   {
						   MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(lstFinalSearchResult);     
					   }     
				   }
				   else
				   {
					   for (ConsumerID__c oConsumerId: lstConsumers)
					   {                                       
						   MemberSearch_LC_HUM.mapAccounts.put(oConsumerId.ID, createAccountObjConsumer(oConsumerId));               
					   }
					   MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(MemberSearch_LC_HUM.mapAccounts.values());
				   }  
			   } 
		   }
		   else
		   {
			   MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(lstFinalSearchResult);
		   }  
		   checkResultSizeLimit(MemberSearch_LC_HUM.lstFinalSearchResult);     
	   }
	   catch(Exception ex)
	   {
		   HUMExceptionHelper.logErrors(ex, 'MemberSearch_LC_HUM', 'searchConsumer');
	   } 
	} 
	 /*
    * Method Name   :  buildQueryConsumer
    * Description   :  This Method is used to :
    *                  a. Search Name 
    *                  b.  Build search query for consumer
    * Return Type   :  void
    * Parameters    :  NA
    */ 
    @TestVisible private static string BuildQueryConsumer(boolean memberIdSearch,MemberSearchWrapper_LH_HUM oMemberSearchWrapper,boolean bIsNameSearch,string sConsumerSearchQuery,integer iConsumerSearchLimit,Map<string, string> mapEncryptedResult)
    {
       if(!memberIdSearch)
       { 
           if (!String.isBlank(oMemberSearchWrapper.sMemberId)&&(NULL != mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sConsumerId )))
           {   
               Set<ID> setAccIdFormemberId = new Set<ID>();
               setAccIdFormemberId = MemberSearch_LD_HUM.createSearchQueryConsumer(mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sConsumerId ),oMemberSearchWrapper);                               
               List<Account> lstConsumerDetails = null;
               if(bIsNameSearch)
               {   
                   sConsumerSearchQuery += ' AND ID IN : setAccIdFormemberId )';
                   List<List<sObject>> searchList = search.query(sConsumerSearchQuery);
                   lstConsumerDetails = ((List<Account>)searchList[0]); 
               }
               else
               {   
                   sConsumerSearchQuery += ' AND ID IN : setAccIdFormemberId ';
                   lstConsumerDetails = (List<Account>)MemberSearch_LD_HUM.getListOfsObjects(sConsumerSearchQuery, NULL, setAccIdFormemberId, NULL);   
               }
               if(null != lstConsumerDetails && !lstConsumerDetails.isEmpty())
               {
                   MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(lstConsumerDetails);
               }
               else
               {
                   MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(MemberSearch_LC_HUM.lstFinalSearchResult);
               }
           }
           else
           {   
               MemberSearch_LC_HUM.lstFinalSearchResult = dataSecurity(MemberSearch_LC_HUM.lstFinalSearchResult);
           }
       }
       else
       {  
           String sConsId = oMemberSearchWrapper.sMemberId + '*';
           sConsumerSearchQuery = 'FIND \'' + sConsId + '\' IN ALL FIELDS RETURNING ConsumerID__c(Consumer_ID__c, Account__r.ID, Account__r.Name, Account__r.FirstName, Account__r.LastName,Account__r.Suffix, Account__r.Middlename,'
                       + 'Account__r.Individual_First_Name__c, Account__r.Individual_Last_Name__c,Account__r.RecordTypeId, Account__r.RecordType.Name, Account__r.Birthdate__c, Account__r.Deceased_Date__c, Account__r.PersonEmail, Account__r.PersonMailingStreet, '
                       + 'Account__r.PersonMailingCity, Account__r.PersonMailingStateCode, Account__r.PersonMailingState, Account__r.PersonMailingPostalCode, Account__r.PersonHomePhone, '
                       + 'Account__r.Home_Office_Account__c, Account__r.ETL_Record_Deleted__c Where Account__r.ETL_Record_Deleted__c = false and ID_Type__C in (\'MedicareID\',\'Medicaid-Id\', \'HumanaId\', \'SSN\', \'CBIS ALT ID\') Limit ' + iConsumerSearchLimit +')';
           
       }
	   return sConsumerSearchQuery;
    }
}