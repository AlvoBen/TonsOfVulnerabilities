/*******************************************************************************************************************************
Apex Class Name : MemberSearch_LC_HUM
Version         : 1.0
Created On      : 09/18/2020
Function        : This class is used for performing the Lightning Member Search
Test Class      : MemberSearch_LT_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Joel Oommen George                                  09/18/2020                Original Version
*    2.0           Joel Oommen George                                  10/22/2020                Code Improvement
*    3.0           Ashish Kumar                                        10/29/2020                Code Improvement
*    4.0           Ritik Agarwar/Ashish Kumar                          02/08/2021                Sorting Logic
*    5.0           Ritik Agarwar/Ashish Kumar                          03/15/2021                Added new fields on Account and MemberPlan Object
*    6.0           Ritik Agarwar/Ashish Kumar                          04/07/2021                Changed doRelationship method, added logic for accessing accounts as well
*    7.0           Kajal Namdev     
* 	 8.0		   Ashish Kumar										   05/28/2021				 Removed references of Memebr_Id__c, Policy_Member__C 
*    9.0           Ankima Srivastava								   06/11/2021				 Checkmarx fix related to with sharing
*    10.0           Pallavi Shewale                                    07/19/2021                US: 2364907- Search- Add Humana Pharmacy Account Number to the Search screen
*    11.0          Ritik Agarwal                                       08/09/2021                allow partial search if search is only basis on memberId
*    12.0          Santhi Mandava                                      09/29/2022                   US3398901- Homeoffice/CPD changes
*******************************************************************************************************************************************************************/
public without sharing class MemberSearch_LC_HUM 
{   
    public static Map<string, string> mapEncryptedResult = new Map<String,String>();
    public static Boolean memberIdSearch  = false;
    public static Boolean bIsNameSearch  = false;
    public static MemberSearchWrapper_LH_HUM oMemberSearchWrapper{get; set;}
    public static Map<Id, Boolean> mapRecordHaveAccess {get; set;}
    public static String sourcePersonId;
    public static String sEnterpriseId;
	public static MemberSearchResponseWrapper objMemSerResponse = new MemberSearchResponseWrapper();
    @TestVisible private static string sSearchQuery ='';
    @TestVisible private static string sConsumerSearchQuery   ='';
    @TestVisible private static Set<ID> setAccMemberId = new Set<ID>();
    
    private static string platformNameSearchQuery  ='';
    private static string platformSearchQuery  ='';
    private static integer iSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Member_SearchLimit;
    public static List<Account> lstFinalSearchResult = new List<Account>();
    public static Map<Id, Account> mapAccounts = new Map<Id, Account>(); 
    private static Set<ID> setAccIdForGrpNumber = new Set <ID>();
    private static Set<ID> setAccIdFormemberId = new Set <ID>();
    private static Set<ID> setAccConsumerId = new Set<ID>();
    private static integer iConsumerSearchLimit = GLOBAL_SEARCH_CONSTANT_LH_HUM.Member_SearchLimit;
    @TestVisible
    private static String sRequestLabelPID='';
       
   /*
    * Method Name   :    Constructor
    * Description   :    This method is used for initializing variables
    * Return Type   :    NA
    * Parameters    :    NA
    */
    public MemberSearch_LC_HUM()
    {        
         oMemberSearchWrapper = new MemberSearchWrapper_LH_HUM();
    }

    /*
    * Method Name   :    searchRecords
    * Description   :    This method Is used for returning Account Records Found
    * Return Type   :    List<Account>
    * Parameters    :    MemberSearchWrapper_LH_HUM
    */
    @AuraEnabled
    public static MemberSearchResponseWrapper searchRecords(MemberSearchWrapper_LH_HUM formMemberSearchWrapper) 
    {
        List<Account> lstAccounts = new List<Account>();
        Set<Id> lstPolicyIds = new Set<Id>();
        List<PurchaserPlan> lstPolicy = new List<PurchaserPlan>();  
        setAccIdForGrpNumber = new Set<ID>();
        List <MemberPlan> lstPolicyMember = new List <MemberPlan>(); 
        List<Account> lstAccountMember = new List<Account>();   
        oMemberSearchWrapper = formMemberSearchWrapper;
        memberIdSearch = false;
        mapEncryptedResult = MemberSearchUtility_LH_HUM.getQueryMapSoftphone(MemberSearch_LD_HUM.FieldCipherMaping(oMemberSearchWrapper));  
        if((!String.isBlank(oMemberSearchWrapper.sMemberId)) && String.isBlank(oMemberSearchWrapper.sGroupNumber) && String.isBlank(oMemberSearchWrapper.sFirstName) && String.isBlank(oMemberSearchWrapper.sLastName)
           && String.isBlank(oMemberSearchWrapper.sBirthdate) && String.isBlank(oMemberSearchWrapper.sPhone) && String.isBlank(oMemberSearchWrapper.sPostalCode) 
           && (String.isBlank(oMemberSearchWrapper.sState)||(oMemberSearchWrapper.sState).equalsIgnoreCase(system.label.HUMSPhoneMappingObj_None)))  
           memberIdSearch = true;   
        buildQuery();       
        if (!String.isBlank(oMemberSearchWrapper.sGroupNumber) && !memberIdSearch)
        {            
           String sQuery = 'Select id, Group_Number__c from Account Where ' + mapEncryptedResult.get(System.Label.HUMGroupNumber);
           lstAccounts = (List<Account>)MemberSearch_LD_HUM.getListOfsObjects(sQuery, NULL, NULL, NULL);           
           Set<Id> setIds = new Set<Id>();
           for(Account oacc : lstAccounts)  
           {
               setIds.add(oacc.id);
           }      
           String sQueryforPolicy = 'Select id from PurchaserPlan where PayerId in :';
           lstPolicy  = (List<PurchaserPlan>)MemberSearch_LD_HUM.getListOfsObjects(sQueryforPolicy, setIds, NULL, NULL);            
           for(PurchaserPlan oPolicy : lstPolicy)
           {
               lstPolicyIds.add(oPolicy.id);
           }
           String sQueryForMembers = 'Select MemberId FROM MemberPlan Where PlanId in :'; 
           lstPolicyMember  = (List<MemberPlan>)MemberSearch_LD_HUM.getListOfsObjects(sQueryForMembers, lstPolicyIds, NULL, NULL);                     
            if (!lstPolicyMember.isEmpty())
            {
                for (MemberPlan oPolMem :lstPolicyMember)
                {
                    setAccIdForGrpNumber.add(oPolMem.MemberId);
                }
            }           
            sSearchQuery += ' AND ID IN : setAccIdForGrpNumber ';
            platformNameSearchQuery += ' AND ID IN : setAccIdForGrpNumber ';     
        }
        if(!memberIdSearch) 
        { 
            doQuery();
        }
        else
        { 
            doRelationshipQuery();
        }               
        if(oMemberSearchWrapper.sUnknownMemCheck)
        {
            MemberSearch_LD_HUM.searchConsumer(oMemberSearchWrapper,lstFinalSearchResult,memberIdSearch,sConsumerSearchQuery,bIsNameSearch,mapEncryptedResult);
        }
        objMemSerResponse.lstAccounts = getSortedAccountList(lstFinalSearchResult);
        return objMemSerResponse;
    } 
    
    public static List<String> fetchRecordTypeIds(MemberSearchWrapper_LH_HUM oMemberSearchWrapper)
    {
        Schema.DescribeSObjectResult accountSchema = Schema.SObjectType.Account;
        Map<String,Schema.RecordTypeInfo> accountRecordTypeMap = accountSchema.getRecordTypeInfosByName();
        Schema.RecordTypeInfo oMemberRecordType =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.HumMemberRecordType);
        Schema.RecordTypeInfo oUnknownMemberRecordType =  accountRecordTypeMap.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.UNKNOWN_MEMBER);
        List<String> lstRecordTypeId = new List<String>();
        if(oMemberSearchWrapper.sUnknownMemCheck)
        {
            lstRecordTypeId = new List<String>{'\''+oUnknownMemberRecordType.getRecordTypeId()+'\''};     
        }
        else
        {  
          lstRecordTypeId = new List<String>{'\''+oMemberRecordType.getRecordTypeId()+'\'','\''+oUnknownMemberRecordType.getRecordTypeId()+'\''};     
        }
        return lstRecordTypeId;
    }
    
    /*
    * Method Name   :    buildQuery
    * Description   :    This method is used for buildQuery
    * Return Type   :    void
    * Parameters    :    NA
    */  
    @testvisible private static void buildQuery()
    {
        bIsNameSearch =false;  
        String sMemberId = oMemberSearchWrapper.sMemberId + '*';
        String sNameKey ='';
        List<String> lstRecordTypeId = new List<String>();
        
		//created a method to fetch recordTYpeids
        lstRecordTypeId= fetchRecordTypeIds(oMemberSearchWrapper);
       
        if(!memberIdSearch)
        {              
            if(string.isNotBlank(oMemberSearchWrapper.sFirstName)) 
                sNameKey = oMemberSearchWrapper.sFirstName + '*';

            if(string.isNotBlank(oMemberSearchWrapper.sLastName))
            {
                if(String.IsNotBlank(sNameKey))
                {
                    sNameKey += ' AND '+oMemberSearchWrapper.sLastName+'*'; 
                }
                else
                {
                    sNameKey = oMemberSearchWrapper.sLastName + '*';
                }
            }
            if(String.IsNotBlank(sNameKey))
            {
                platformNameSearchQuery = 'FIND \'' + String.escapeSingleQuotes(sNameKey) +'\' IN NAME FIELDS RETURNING Account(ID, Name, FirstName, LastName, middlename,'+
                                + 'Individual_First_Name__c,Individual_Last_Name__c, RecordType.Name,Suffix, Birthdate__c,Deceased_Date__c,Enterprise_ID__c, PersonEmail,PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                + 'PersonMailingPostalCode,Security_Groups__c, PersonHomePhone, Home_Office_Account__c, SSN__c,  MedicareID__c,Mbr_Gen_Key__c, MedicareID_HICN__c, PERSONID__c , ETL_Record_Deleted__c' +
                                + ' Where RecordtypeId IN ' + lstRecordTypeId ;
                
                if(!string.isBlank(oMemberSearchWrapper.sBirthdate) && NULL != mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sBirthdate))    platformNameSearchQuery += ' AND ' + mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sBirthdate);
                if(!String.isEmpty(oMemberSearchWrapper.sState) && !(oMemberSearchWrapper.sState).equalsIgnoreCase(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue))    platformNameSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(oMemberSearchWrapper.sState) + '\'';
                if(!String.isBlank(oMemberSearchWrapper.sPhone) && NULL != mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPersonHomePhone)) platformNameSearchQuery += ' AND ' + mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPersonHomePhone);
                if(!String.isBlank(oMemberSearchWrapper.sPostalCode) && NULL != mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPersonMailingPostalCode ))    platformNameSearchQuery += ' AND ' + mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPersonMailingPostalCode);
                bIsNameSearch =true;                
                 sConsumerSearchQuery =  platformNameSearchQuery;
            }
            else{
                sSearchQuery = 'SELECT ID, Name, FirstName, LastName,middlename,Individual_First_Name__c,Individual_Last_Name__c,Suffix, RecordType.Name, Birthdate__c,Deceased_Date__c, Enterprise_ID__c,PersonEmail, '+
                                       + 'PersonMailingStreet, PersonMailingCity, PersonMailingStateCode, PersonMailingState, ' +
                                       + 'PersonMailingPostalCode,Security_Groups__c, Mbr_Gen_Key__c,PersonHomePhone, UserRecordAccess.HasReadAccess, Home_Office_Account__c, ETL_Record_Deleted__c ' +
                                       + 'From Account Where RecordtypeId IN ' + lstRecordTypeId ;
                                       
                
                 if(!string.isEmpty(oMemberSearchWrapper.sFirstName) && NULL != mapEncryptedResult.get('FirstName')) sSearchQuery += ' AND ' + mapEncryptedResult.get('FirstName');
                if(!string.isEmpty(oMemberSearchWrapper.sLastName) && NULL != mapEncryptedResult.get('LastName')) sSearchQuery += ' AND ' + mapEncryptedResult.get('LastName');
                if(!string.isBlank(oMemberSearchWrapper.sBirthdate) && NULL != mapEncryptedResult.get('Birthdate__c'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('Birthdate__c');
                if(!String.isEmpty(oMemberSearchWrapper.sState) && !(oMemberSearchWrapper.sState).equalsIgnoreCase(GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue))    sSearchQuery += ' AND PersonMailingStateCode = \'' +string.escapeSingleQuotes(oMemberSearchWrapper.sState) + '\'';
                if(!String.isBlank(oMemberSearchWrapper.sPhone) && NULL != mapEncryptedResult.get('PersonHomePhone'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('PersonHomePhone');
                if(!String.isBlank(oMemberSearchWrapper.sPostalCode) && NULL != mapEncryptedResult.get('PersonMailingPostalCode'))    sSearchQuery += ' AND ' + mapEncryptedResult.get('PersonMailingPostalCode');

                sConsumerSearchQuery =  sSearchQuery;
            }
        }
         else
        {       
            buildMemberIDQueries(lstRecordTypeId);
        }       
    }
    
    /*
    * Method Name   :    doQuery
    * Description   :    This method is used for doQuery
    * Return Type   :    void
    * Parameters    :    NA
    */ 
   @TestVisible private static void doQuery()
    {
        setAccIdFormemberId = new Set<ID>();
        if (!String.isBlank(oMemberSearchWrapper.sMemberId)&&(NULL != mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sName)))
        {
            setAccIdFormemberId = MemberSearch_LD_HUM.createSearchQueryNewMemberPlan(mapEncryptedResult.get(GLOBAL_SEARCH_CONSTANT_LH_HUM.sName),oMemberSearchWrapper); 
            sSearchQuery += ' AND ID IN : setAccIdFormemberId ';
            platformNameSearchQuery += ' AND ID IN : setAccIdFormemberId ';
        }  
        getEligibleAccountList();
    }   
     /*
     * Method Name   :  getEligibleAccountList
     * Description   :  This Method is used for returning results to Member search screen
     * Return Type   :  void
     * Parameters    :  String - Sort direction value
     */
    @testvisible private static void getEligibleAccountList()
    {   
        List<Account> lstTempAccounts;
        String sQuery = '';
        if(!memberIdSearch)
        {   
            if(bIsNameSearch)
            {  
                platformNameSearchQuery += 'Order By RecordType.Name Limit 250)';//keeping to 250 as we have name filtering
                List<List<sObject>> searchList = search.query(platformNameSearchQuery);
                lstTempAccounts = ((List<Account>)searchList[0]);
                List<Account> lstMembersFinal = new List<Account>();
                Boolean bIsMatching  = true;
                String sIndFirstName ='';
                String sIndLastName = '';
                for(Account obj : lstTempAccounts)
                {
                    bIsMatching = true;
                    sIndFirstName = obj.Individual_First_Name__c;
                    sIndLastName = obj.Individual_Last_Name__c;
                    if(String.IsNotBlank(oMemberSearchWrapper.sFirstName))
                    {
                       if(String.IsBlank(sIndFirstName) || (String.IsNotBlank(sIndFirstName) && !obj.Individual_First_Name__c.startsWithIgnoreCase(oMemberSearchWrapper.sFirstName))) bIsMatching = false;
                    }
                    if(String.IsNotBlank(oMemberSearchWrapper.sLastName))
                    {
                       if(String.IsBlank(sIndLastName) || (String.IsNotBlank(sIndLastName) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(oMemberSearchWrapper.sLastName))) bIsMatching = false;
                    }
                    if(bIsMatching)
                    {                   
                        lstMembersFinal.Add(obj);
                    }
                    if(lstMembersFinal.size() == (iSearchLimit - 1 ) ) break; //break the loop if we reached UI result limit of 100.
                }
                lstFinalSearchResult = MemberSearch_LD_HUM.dataSecurity(lstMembersFinal);              
            }
             else
            {
                sSearchQuery += ' Order By RecordType.Name Limit ' + iSearchLimit;
                lstFinalSearchResult = MemberSearch_LD_HUM.dataSecurity(MemberSearch_LD_HUM.getListOfsObjects(sSearchQuery, NULL, setAccIdFormemberId, setAccIdForGrpNumber));   
            }    
        }    
    }       
        
     /*
    * Method Name   :    buildMemberIDQueries
    * Descripintion   :    This method is used to build member id queiries 
    * Return Type   :    void
    * Parameters    :    NA
    */
    @testvisible private static void buildMemberIDQueries(List<String> lstRecordTypeId)
    {	
        platformSearchQuery = MemberSearch_LD_HUM.buildMemberIdQuery(oMemberSearchWrapper,lstRecordTypeId);       
    }
    
     /*
     * Method Name   :  doRelationshipQuery
     * Description   :  This Method is  used to :
                        a. Build query for fetching Account details using only MemberID
                        b. Execute DB query                        
     * Return Type   :  void
     * Parameters    :  NA
     */
    private static void doRelationshipQuery()
    {       
        List<List<sObject>> lstReturnedResults = new List<List<sObject>>();
        lstReturnedResults =  MemberSearch_LD_HUM.getMemberIdQueryResults(platformSearchQuery);
        List<MemberPlan> lstMemPlan = New List<MemberPlan>();
        List<Account> lstAcc = New List<Account>();
        List<Account> lstAccToReturn = new List<Account>();
        mapAccounts = new Map<Id, Account>();
        if(!lstReturnedResults.IsEmpty())
        {
            lstMemPlan = ((List<MemberPlan>)lstReturnedResults[0]);
            if(lstReturnedResults.size() > 1) lstAcc = ((List<Account>)lstReturnedResults[1]);
        } 
        
        for(MemberPlan oMemPlan : lstMemPlan)
        {
            if((string.isNotBlank(oMemberSearchWrapper.sSuffix) && String.isNotBlank(oMemPlan.Member_Dependent_Code__c) && oMemPlan.Member_Dependent_Code__c == oMemberSearchWrapper.sSuffix && String.isNotBlank(oMemPlan.Member_Id_Base__c) && oMemPlan.Member_Id_Base__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId)) ||
                   (string.isBlank(oMemberSearchWrapper.sSuffix) && String.isNotBlank(oMemPlan.Member_Id_Base__c)  && oMemPlan.Member_Id_Base__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId)) || 
                  (String.isNotBlank(oMemPlan.Medicaid_Id__c) && oMemPlan.Medicaid_Id__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId) && string.isBlank(oMemberSearchWrapper.sSuffix)) || 
                  (String.isNotBlank(oMemPlan.CBIS_ALT_ID__C) && oMemPlan.CBIS_ALT_ID__C.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId) && string.isBlank(oMemberSearchWrapper.sSuffix)) ||
                  (String.isNotBlank(oMemPlan.Cert_Id__c) && oMemPlan.Cert_Id__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId) && string.isBlank(oMemberSearchWrapper.sSuffix)) ||
                  (String.isNotBlank(oMemPlan.Vitality_Entity_ID__c) && oMemPlan.Vitality_Entity_ID__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId) && string.isBlank(oMemberSearchWrapper.sSuffix)))           
            {
                mapAccounts.put(oMemPlan.MemberId, MemberSearch_LD_HUM.createAccountObj(oMemPlan));
            }
        }
        if(!(mapAccounts.size() > 0)){
            for(Account objAcc : lstAcc)
            {
               if((String.isNotBlank(objAcc.MedicareID__c) && objAcc.MedicareID__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId) && string.isBlank(oMemberSearchWrapper.sSuffix)) || 
                   (String.isNotBlank(objAcc.SSN__c) && objAcc.SSN__c.startsWithIgnoreCase(oMemberSearchWrapper.sMemberId) && string.isBlank(oMemberSearchWrapper.sSuffix)))
                {
                  lstAccToReturn.add(objAcc);  
                }
            }
        }
        
        lstFinalSearchResult = mapAccounts.size() > 0 ? MemberSearch_LD_HUM.dataSecurity(mapAccounts.values()) : MemberSearch_LD_HUM.dataSecurity(lstAccToReturn);               
    }   

      /**
    * Returns request object of continuation object
    * <p>
    * Method to invoke the MBE plus continuation request
    *
    * @return continuation object
    */
     @AuraEnabled(continuation=true cacheable=true)
    public static Continuation getInvokeMBEPlusService2(String sPIdVal)
    { 
        return MemberSearch_LS_HUM.getInvokeMBEPlusService2(sPIdVal);
    }   
    /**
    * Returns responses of continuation object
    * <p>
    * Callback Method to parse the service response
    *
    * @return Object
    */
      @AuraEnabled( continuation=true cacheable=true)
    public static Object getparseMBEPlusResponse2(List<String> labels)
    {   
        Object objectReturn;
        objectReturn = MemberSearch_LS_HUM.getparseMBEPlusResponse2(labels);
        sEnterpriseId = (String)objectReturn;        
        MemberSearch_LD_HUM.buildEnterpriseIDQuery(sEnterpriseId);        
        if(!lstFinalSearchResult.isEmpty())
		{
			objMemSerResponse.lstAccounts = getSortedAccountList(lstFinalSearchResult);
            return objMemSerResponse;
		}
        else
            return null;  
    }
    
        /**
    * Returns custom sorted List<Account> using comparable
    */
	public static List<Account> getSortedAccountList(List<Account> accList)
	{	
		List<MemberRecordSorting> wMemberSortList = new List<MemberRecordSorting>();
		MemberRecordSorting memSort;
		
		for(Account accRecord : accList)
		{	
			if(accRecord.ETL_Record_Deleted__c)
			{
			 memSort = new MemberRecordSorting('Z', accRecord);
			}
			else
			{
			 memSort = new MemberRecordSorting(accRecord.RecordType.Name, accRecord);
			}
			
		wMemberSortList.add(memSort);
		}
		wMemberSortList.sort();
		//if sorting not occurs ned to change this list into account
		List<sObject> sortedAccountList =new List<sObject>();
		for(MemberRecordSorting memSortRecord : wMemberSortList)
		{
			sortedAccountList.add(memSortRecord.oAcc);
		}
		return sortedAccountList;
	}
    
    /**
    Description: This wrapper class is used for custom sorting of Accounts based on
                 recordType name and ETL_Record_Deleted field using standard comparable 
                 interface in the order(Member --> Unknown Member --> ETL record deleted =true records)
    */
	public class MemberRecordSorting implements Comparable {
 
    public String sParamCondition;
	public sObject oAcc;
	
    public MemberRecordSorting(String paramCondition, sObject sForceObject) {
      sParamCondition = paramCondition;
	  oAcc = sForceObject;
    }
 
    public Integer compareTo(Object objToCompare) {
        MemberRecordSorting oMem = (MemberRecordSorting)objToCompare;
        if (sParamCondition == oMem.sParamCondition){
            return 0;
        }
        else if (sParamCondition > oMem.sParamCondition){
            return 1;
        }
        else{
            return -1;        
        }
    }
}
 public class MemberSearchResponseWrapper{
        @AuraEnabled
        public List<Account> lstAccounts;
        @AuraEnabled
        public Map<Id, Boolean> mapRecordAccess;
	}

   

}