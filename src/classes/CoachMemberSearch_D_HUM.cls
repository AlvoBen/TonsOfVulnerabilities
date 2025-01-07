/*******************************************************************************************************************************
Apex Class Name : CoachMemberSearch_D_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : This class is Data Layer Class for CoachMemberSearch_S_HUM
Test Class      : CoachMemberSearch_T_HUM
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0               Jasmeen Shangari                                 03/08/2021                 Original Version
* 1.1               Jasmeen Shangari                                 04/20/2021                Fix for Defect-2851
*******************************************************************************************************************************************************************/

public with sharing class CoachMemberSearch_D_HUM 
{
    private static integer iSearchLimit = HUMConstants.Member_SearchLimit;
    private static integer iConsumerSearchLimit = HUMConstants.Member_SearchLimit;
    /*
    * Method Name   :    FieldCipherMaping
    * Description   :    This method is used for FieldCipherMaping
    * Return Type   :    Map<String, String>
    * Parameters    :    CoachMemberSearchWrapper_DTO_HUM
    */    
     public static Map<String, String> FieldCipherMaping(CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapperwithCipher)
     {
        Map<String, String> accFieldCipher = new Map<String,String>();                
       
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sFirstName)) 
            accFieldCipher.put(HUMConstants.OBJECT_ACCOUNT_API_FIRSTNAME, oMemberSearchWrapperwithCipher.sFirstName);
                                
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sLastName)) 
            accFieldCipher.put(HUMConstants.OBJECT_ACCOUNT_API_LASTNAME, oMemberSearchWrapperwithCipher.sLastName);
        
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sBirthdate)) 
            accFieldCipher.put(HUMConstants.HUMAccountBirthDate, oMemberSearchWrapperwithCipher.sBirthdate);
        
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sMemberId))
            accFieldCipher.put(HUMConstants.HUMName, oMemberSearchWrapperwithCipher.sMemberId);
        
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sMemberId))
            accFieldCipher.put(HUMConstants.HUMConsumerTaxID, oMemberSearchWrapperwithCipher.sMemberId);                        
    
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sPhone)) 
            accFieldCipher.put(GLOBAL_CONSTANT_CH_HUM.sPersonHomePhone , oMemberSearchWrapperwithCipher.sPhone);
              
        if(String.isNotBlank(oMemberSearchWrapperwithCipher.sPostalCode))
            accFieldCipher.put(GLOBAL_CONSTANT_CH_HUM.sPersonMailingPostalCode , oMemberSearchWrapperwithCipher.sPostalCode);           
        
        if (String.isNotBlank(oMemberSearchWrapperwithCipher.sGroupNumber))
            accFieldCipher.put(System.Label.HUMGroupNumber, oMemberSearchWrapperwithCipher.sGroupNumber);                              
        return accFieldCipher;
     }
     
     /* 
     * Method Name   :  filterSOSLResults
     * Description   :  This Method is used to filter member id SOSL query results.
     * Return Type   :  List<ConsumerID__c>
     * Parameters    :  List<ConsumerID__c>
     */
   public static List<ConsumerID__c> filterSOSLResultsConsumer(List<ConsumerID__c> lstConsumers,CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapper)
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
     * Parameters    :  Consumer Id, CoachMemberSearchWrapper_DTO_HUM
     */
    public static Set<ID> createSearchQueryConsumer(String sConsumerId,CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapper)
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
            if(lstFinalSearchResult.size() == HUMConstants.HUMConstantZero ) 
            { 
                throw new HUMCustomException(system.Label.HUMMedicareSearchCriteriaViolation); 
            }
            else if(lstFinalSearchResult.size() == HUMConstants.HUMConstantZero )
                throw new HUMCustomException(System.Label.HUMMemberSearchCriteriaViolation);

        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'CoachMemberSearch_D_HUM', 'checkResultSizeLimit');
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
    
    
      public static List<Member_Id__c> getMemberIdQueryResults2(String searchQuery,String sMemberId)
    {
        List<Member_Id__c> lstMembers = new List<Member_Id__c>();
        List<List<sObject>> searchList = search.query(searchQuery);        
        lstMembers = ((List<Member_Id__c>)searchList[0]);   
        lstMembers  = filterSOSLResults(lstMembers,sMemberId);
        return lstMembers ;

    }
    
    /*
     * Method Name   :  filterSOSLResults
     * Description   :  This Method is used to filter member id SOSL query results.
     * Return Type   :  List<Member_Id__c>
     * Parameters    :  List<Member_Id__c>
     */
    public static List<Member_Id__c> filterSOSLResults(List<Member_Id__c> lstMembers,String sMemberId)
    {
        List<Member_Id__c> lstMembersFinal = new List<Member_Id__c>();
        For(Member_Id__c obj : lstMembers)
        {
            String sMemId = obj.Name;
            If(String.IsNotBlank(sMemId) && sMemId.startsWithIgnoreCase(sMemberId))
            {           
                lstMembersFinal.Add(obj);
            }
        }
        return lstMembersFinal;
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
        RecordType recType = new RecordType();
        recType.id = memObj.member.RecordTypeId;
        rectype.name = memObj.member.RecordType.Name;
        acctObj.recordtype = recType;          
        return acctObj;
    }
    /*
     * Method Name   :  createSearchQuery
     * Description   :  This Method is called from dataSearch() when ID field value is entered on screen.
     * Return Type   :  Set of accounts ID
     * Parameters    :  Member Id
     */
    public static Set<ID> createSearchQuery(String sMemId,CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapper)
    {
        List<Member_Id__c> lstMemberId = new List<Member_Id__c>();
        String sMemIdQuery = '';  
        String sMemberId = oMemberSearchWrapper.sMemberId + '*';
        sMemIdQuery = 'FIND \'' + sMemberId + '\' IN NAME FIELDS RETURNING Member_Id__c(Name,Member_Dependent_Code__c ,Policy_Member__r.Member__r.Id, Policy_Member__r.ETL_Record_Deleted__c Where Policy_Member__r.ETL_Record_Deleted__c = false ';
        if(String.isNotBlank(oMemberSearchWrapper.sSuffix))
        {
            sMemIdQuery += ' AND Member_Dependent_Code__c = \'' + oMemberSearchWrapper.sSuffix+ '\' Limit ' + iSearchLimit+')';
        }
        else
        {
            sMemIdQuery += ' Limit ' + iSearchLimit+')';
        }
        lstMemberId = getMemberIdQueryResults2(sMemIdQuery,oMemberSearchWrapper.sMemberId);
        Set<ID> setAccMemberId = new Set<ID>();
        for (Member_Id__c oMemId: lstMemberId)
        {
            setAccMemberId.add(oMemId.Policy_Member__r.Member__r.Id);
        }
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
                sSoqlQuery +=  ' order by MemberId desc NULLS LAST Limit ' +HUMCOnstants.HumListsizeLimit;               
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
        List<AccountComparisionHelper_CH_HUM> lstAccountsToSort = new List<AccountComparisionHelper_CH_HUM>();
        for(Integer i = 0 ; i < lstMemberAccount.size() ; i++)
        {
            lstAccountList.add(lstMemberAccount.get(i).Id);
            lstAccountsToSort.Add(New AccountComparisionHelper_CH_HUM(lstMemberAccount[i]));
        }
        List <UserRecordAccess> lstUserRecordAcess = [SELECT RECORDID, HASREADACCESS FROM UserRecordAccess WHERE USERID =: UserInfo.getUserID() AND RECORDID IN : lstAccountList];
        for(UserRecordAccess userAccess : lstUserRecordAcess)
        {
            mapRecordHaveAccess.put(userAccess.RECORDID, userAccess.HASREADACCESS);
        }            
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
                               ETL_Record_Deleted__c,Enterprise_ID__c, Home_Office_Account__c, UserRecordAccess.HasReadAccess FROM Account WHERE Enterprise_ID__c =:enterpriseID];
                if(accountList!=null &&!accountList.isEmpty())
                {
                    CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(accountList);
                }
            }
        }
        catch(Exception ex)
        {
              HUMExceptionHelper.logErrors(ex, 'CoachMemberSearch_D_HUM', 'buildEnterpriseIDQuery');
        } 
    }
    
    /*
     * Method Name   :  SearchConsumer
     * Description   :  This Method is called for Manual Search from UI(both for Search button click and hit of Enter key) on complete of Search Manual method    
     * Return Type   :  void
     * Parameters    :  NA
     */
    public static void SearchConsumer(CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapper, List<Account> lstFinalSearchResult,boolean memberIdSearch,string sConsumerSearchQuery,boolean bIsNameSearch,Map<string, string> mapEncryptedResult)
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
                           CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(lstFinalSearchResult);     
                       }     
                   }
                   else
                   {
                       for (ConsumerID__c oConsumerId: lstConsumers)
                       {                                       
                           CoachMemberSearch_C_HUM.mapAccounts.put(oConsumerId.ID, createAccountObjConsumer(oConsumerId));               
                       }
                       CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(CoachMemberSearch_C_HUM.mapAccounts.values());
                   }  
               } 
           }
           else
           {
               CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(lstFinalSearchResult);
           }  
           checkResultSizeLimit(CoachMemberSearch_C_HUM.lstFinalSearchResult);     
       }
       catch(Exception ex)
       {
           HUMExceptionHelper.logErrors(ex, 'CoachMemberSearch_D_HUM', 'searchConsumer');
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
    @TestVisible private static string BuildQueryConsumer(boolean memberIdSearch,CoachMemberSearchWrapper_DTO_HUM oMemberSearchWrapper,boolean bIsNameSearch,string sConsumerSearchQuery,integer iConsumerSearchLimit,Map<string, string> mapEncryptedResult)
    {
       if(!memberIdSearch)
       { 
           if (String.isNotBlank(oMemberSearchWrapper.sMemberId)&&(NULL != mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sConsumerId )))
           {   
               Set<ID> setAccIdFormemberId = new Set<ID>();
               setAccIdFormemberId = createSearchQueryConsumer(mapEncryptedResult.get(GLOBAL_CONSTANT_CH_HUM.sConsumerId ),oMemberSearchWrapper);                               
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
                   lstConsumerDetails = (List<Account>)getListOfsObjects(sConsumerSearchQuery, NULL, setAccIdFormemberId, NULL);   
               }
               if(null != lstConsumerDetails && !lstConsumerDetails.isEmpty())
               {
                   CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(lstConsumerDetails);
               }
               else
               {
                   CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(CoachMemberSearch_C_HUM.lstFinalSearchResult);
               }
           }
           else
           {   
               CoachMemberSearch_C_HUM.lstFinalSearchResult = dataSecurity(CoachMemberSearch_C_HUM.lstFinalSearchResult);
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