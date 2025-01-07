/*******************************************************************************************************************************
Apex Class Name :  CaseServiceProvider_v4_H_HUM.cls
Version: 1.0
Created On      : 10/10/2020 
Function        : Helper class to get Account Map For provider from Case service removed dependency from interction code
Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Sagar MN                                           10/10/2020                Original Version 
* Sagar MN											 10/10/2020					unknown provider issue fix - modularising the code
* Sagar MN											 03/04/2021					US 1999913 Code checkin for MF1
* Tummala Vijaya Lakshmi                             10/22/2021                 US 2696540 T1PRJ0002606/PR00094268 :  MF6 -INC1341295 - Unknown Provider case Not getting created caseserviceV4
* Pinky Vijur                                        04/14/2023                 User Story 4438160: T1PRJ0891339 - TECH - HUM-2174- CRM Service - Provider information not getting transferred from medHok to CRM
*******************************************************************************************************************************/
public class CaseServiceProvider_v4_H_HUM{
    private static List<HUMCustomSearchHelper> lstProviderAccounts;
    private static ProviderSearch_H_HUM oProvSearchObj;
    private static id sProviderRecordTypeId;
    private Map<id,Account> upsertAcctMap;
    private Boolean bErrorFlag=false;
    private static Map<String,List<ConsumerID__c>> mapAconsumerAccount = new Map<String,List<ConsumerID__c>>();
    private static Set<String> setConsumerIds = new Set <String>();
    private Set <String> setAccIds;  
    private static Set<id> accountIdSet;
    private static Map<String,Account> accountsMap = new Map<String,Account>();
    private HUMSearchComponentBaseHelperV2 baseHelperObj ;
    private Map<String,List<sObject>> AcctConsumerMap = new Map<String,List<sObject>>();
    public static Map<String,Boolean> bisMultipleAcct = new Map<String,Boolean>();
    public static Map<String,List<String>> taxIdMap = new Map<String,List<String>>();
    public static Map<String,String> NPIIDdMap = new Map<String,String>();
    public static Map<String,List<String>> mapTaxIDs = new Map<String,List<String>>();
    public static Map<String,Map<String,String>> mapReturnValues = new Map<String,Map<String,String>>();
    private static List<Account> providerAcct = new List<Account>();   
    public static Map<String,List<Account>> unknownAccountMapToUpsert = new Map<String,List<Account>>();
    private static Map<Integer,String> mapProviderId = new Map<Integer,String>();
    private static Integer iProviderKey=0;     
    private static map<String,String> requestParamMap = new map<String,String>();
    private static String sUnKnownProviderID;
    private static String sUnKnownProviderEntityType;
    private static String sUnKnownProviderIDType;
    private static String sUnKnownProviderFirstName;
    private static String sUnKnownProviderLastName;
    private static Boolean bUnKnownProviderNameCheck;
    private static Boolean bSwitch_US4438160 = false;
    
   /*
* getProviderDetails
* <p>
* This method is used to get interacting and account details
* @param CaseServiceRestInteraction_DTO_HUM, String
* @return Map<String,Account>
*/  
    public static map<String,Account> getProviderDetails(CaseServiceRestInteraction_DTO_HUM interactionWrap,String sIntercatingType){
        Boolean bswitch_US2696540 = HUMUtilityHelper.isCRMFunctionalityON('2696540');
        bSwitch_US4438160 = HUMUtilityHelper.isCRMFunctionalityON('4438160');
        Map < String, Account > accountsMap = New Map < String, Account > ();
        Map<String,Account> externalSearchReturnValue = new Map<String,Account>();        
        List<Account> lstAccount = new List<Account>();
        Account existingAccountId = new Account();       
        bUnKnownProviderNameCheck=false;
        sUnKnownProviderIDType ='';
        sUnKnownProviderFirstName='';
        sUnKnownProviderLastName='';
        
        If (bswitch_US2696540){        
        CaseServiceRestInteraction_DTO_HUM  interactionWrapObj =new CaseServiceRestInteraction_DTO_HUM();              
        interactionWrapObj = interactionWrap;       
        sUnKnownProviderID=interactionWrapObj.sIntID;
        sUnKnownProviderEntityType=interactionWrapObj.sIntType;
        sUnKnownProviderIDType=interactionWrapObj.sIntIDType;
        sUnKnownProviderFirstName=interactionWrapObj.sFirstName;
        sUnKnownProviderLastName=interactionWrapObj.sLastname;  
        requestParamMap =serviceInputMap(interactionWrapObj.sNPIID,interactionWrapObj.sTaxID,interactionWrapObj.sProviderClassification);
            If (String.isBlank(sUnKnownProviderID) &&
              sUnKnownProviderEntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE) &&
              sUnKnownProviderIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)){
              bUnKnownProviderNameCheck =true;   
            }  
        }
        
                   
        //internal search     
        if(accountsMap == Null || accountsMap.isEmpty()) 
        {           
            existingAccountId = locateProviderByParams();           
            
            if(bSwitch_US4438160 && existingAccountId!=NULL && existingAccountId.RecordType.Name.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNPROVIDER)){
                //If an unknown provider account is returned, Check EPSS if a known provider is available.
                Map<String,Account> tempAccountMap = new Map<String,Account>();
                tempAccountMap = searchInEpassService(interactionWrap,sIntercatingType);

                if(tempAccountMap.isEmpty()){
                    accountsMap.put(sIntercatingType, existingAccountId); // add the fetched account from internal search to the map
                }else{
                    accountsMap.putAll(tempAccountMap);
                }
            } else if(existingAccountId!=NULL){               
                accountsMap.put(sIntercatingType, existingAccountId); // add the fetched account from internal search to the map
            } else{
                If (!(sUnKnownProviderIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE))){      
       				 accountsMap = searchInEpassService(interactionWrap,sIntercatingType); //externAL SERVICE  
        			}
            }                 
        }

         If (accountsMap == Null || accountsMap.isEmpty()) {
           accountsMap.put(sIntercatingType,null); //if internal search also dont fetch any value		
        }
        Return accountsMap; 
    }
    
    /*
* locateProviderByParams
* <p>
* This method returns the Account Record for the respective UNknown Provider If any. 
* @param    
* @return   Account
*/    
    Public static Account locateProviderByParams()
    {
        Boolean bswitch_US2696540 = HUMUtilityHelper.isCRMFunctionalityON('2696540');
        List < Account > oProviderAccount = queryAccounts(requestParamMap);
        Account providerId;       
        String Nameconcat =sUnKnownProviderFirstName+' '+sUnKnownProviderLastName;


        If (bswitch_US2696540){
			if(bUnKnownProviderNameCheck){			
			 if(sUnKnownProviderIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)){
				for(ACCOUNT act : oProviderAccount){
					if(act.Name==Nameconcat){
						providerId=act;                    
					}                
				}
			 }
			}else{
				if(oProviderAccount!=NULL && oProviderAccount.SIZE()>0)
				{
					providerId = oProviderAccount[0];
				} 
			}
		}else{
			if(oProviderAccount!=NULL && oProviderAccount.SIZE()>0)
			{
				providerId = oProviderAccount[0];
			} 
		  }
		   
        return providerId;
    }
    
    /**
* This method queries in Salesforce Data Base.
* 
* @param    Map<String,String> 
* @return   List<Account> 
*/
    public static List<Account> queryAccounts(Map<String,String> requestDTOMap)
    {       
        String queryProviderAcct=createPlatformQieries(requestDTOMap);        
        List<Account> accountLst;
        if(String.isNotBlank(queryProviderAcct))            
        {
            accountLst = (List<Account>)Database.Query(queryProviderAcct);
        }        
        return accountLst;
    }
    /**
* Method to create queries in case of platform encryption
*
* @param    Map<String,String>  requestDTOMap
* @return   String 
*/
    public static String createPlatformQieries(Map<String,String> requestDTOMap )
    {      
        Boolean bswitch_US2696540 = HUMUtilityHelper.isCRMFunctionalityON('2696540');
        String Nameconcat =sUnKnownProviderFirstName+' '+sUnKnownProviderLastName; 

        String sPlatformSOQLQuery ='Select Id, Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
            'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, '+
            ' NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c,Enterprise_ID__c From Account Where (RecordType.DeveloperName='+'\''+String.escapeSingleQuotes(HUMConstants.HumProviderRecordType)+'\''+
            +' OR RecordType.DeveloperName ='+'\''+ String.escapeSingleQuotes(HUMConstants.HumUnknownProviderRecordType)+'\')';
        
        
        String sProviderClassification = '';
        
        if(String.isNotBlank(requestDTOMap.get(GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATION)))
        { 
            sProviderClassification  = '(Provider_Classification__c = \'' + String.escapeSingleQuotes(HUMConstants.sFacilityprovider) + '\' OR Provider_Classification__c ='+'\''+String.escapeSingleQuotes(HUMConstants.sGroupprovider)+'\''+')';
        }else{
            sProviderClassification = 'Provider_Classification__c = \'' + String.escapeSingleQuotes(HUMConstants.sIndividualprovider)+ '\'';
        }
        if(String.isNotBlank(requestDTOMap.get(HUMConstants.sNPI)))  
        {
            sPlatformSOQLQuery += ' AND NPI_ID__c = \'' +requestDTOMap.get(HUMConstants.sNPI).trim()+ '\' ';
        }
        If(String.IsNotBlank(requestDTOMap.get(HUMConstants.sTaxID)))
        {
			searchConsumerIDObject(requestDTOMap.get(HUMConstants.sTaxID));
            
            if(!mapAconsumerAccount.isEmpty() && mapAconsumerAccount!=null)
            {
                setConsumerIds = mapAconsumerAccount.keySet();                
                sPlatformSOQLQuery += ' AND ID IN : setConsumerIds ';
            }
        }
         If (bswitch_US2696540){
          if(bUnKnownProviderNameCheck){
                if(sUnKnownProviderIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)){
                sPlatformSOQLQuery += ' AND Name = \'' +Nameconcat+ '\' ';
            	}
             }
         }
        sPlatformSOQLQuery +=' AND ETL_Record_Deleted__c=false';

        //Priortizes Known Provider Accounts over Unknown Provider Accounts
        if(bSwitch_US4438160){
            sPlatformSOQLQuery +=' ORDER BY RecordType.Name ASC';
        }

        sPlatformSOQLQuery = sPlatformSOQLQuery + ' Limit ' + Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c);
        if(String.isNotBlank(requestDTOMap.get(HUMConstants.sTaxID)) && (setConsumerIds==null||setConsumerIds.isEmpty()))
        {
            sPlatformSOQLQuery='';  
        }

        return sPlatformSOQLQuery;
    }
    
    /**
* Method to Search for Consumer Id
*
* @param    String sTaxId
* @return   None 
*/  
    private static void searchConsumerIDObject(String sTaxId)
    {
        String sKey = sTaxId+'*';
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
        List<ConsumerID__c> lstTempConsumers;
        sConsumerQuery = 'FIND \'' +  string.escapeSingleQuotes(sKey)  +'\' IN ALL FIELDS RETURNING ConsumerID__c (Account__c,Consumer_ID__c  Where ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\''+' AND ETL_Record_Deleted__c = false Order by Account__c )';
        List<List<sObject>> searchList = search.query(sConsumerQuery);

        if(!searchList.isEmpty()){
            lstTempConsumers = ((List<ConsumerID__c>)searchList[0]);
        }        
        Boolean bIsMatching  = true;
        if(lstTempConsumers!=null && !lstTempConsumers.isEmpty())
        {
            For(ConsumerID__c obj : lstTempConsumers)
            {
                bIsMatching = true;
                If(!obj.Consumer_ID__c.startsWithIgnoreCase(sTaxId)) bIsMatching = false;
                If(bIsMatching)
                {           
                    lstConsumers.Add(obj);
                }
            }
        }
        if(lstConsumers!=null && !lstConsumers.isEmpty())
        { 
            prepareConsumerIDMap(lstConsumers);
        }
    } 
    
    /**
* Method to prepare ConsumerIDMap
*
* @param    List<ConsumerID__c> lstConsumers
* @return   void 
*/  
    private static void prepareConsumerIDMap(List<ConsumerID__c> lstConsumers )
    {
        List<ConsumerID__c> lstTempConsumers = new List<ConsumerID__c>();             
        if(lstConsumers != Null && !lstConsumers.IsEmpty())
        {            
            String sId = lstConsumers[0].Account__c;
            String sVal ='';
            for(Integer i =0 ;i<=lstConsumers.size()-1;i++)
            {
                if(sId == lstConsumers[i].Account__c)
                {
                    lstTempConsumers.Add(lstConsumers[i]);                   
                }
                else
                {
                    lstTempConsumers.Add(lstConsumers[i]);
                    mapAconsumerAccount.Put(sId,lstTempConsumers);                    
                    sId = lstConsumers[i].Account__c;
                    lstTempConsumers = new List<ConsumerID__c>();
                    lstTempConsumers.Add(lstConsumers[i]);
                }
              }           
        }
    }
  /*
* searchInEpassService
* <p>
* This method is used to perform search account/provider in external service
* @param CaseServiceRestInteraction_DTO_HUM, String
* @return Map<String,Account>
*/    
    public Static Map<String,Account> searchInEpassService(CaseServiceRestInteraction_DTO_HUM interactionwrapObj,String interactingType)
    {
        Map<String,Account> accountsMapRetVal = new Map<String,Account>();  
      try
        {
            oProvSearchObj = New ProviderSearch_H_HUM();
            accountIdSet = New Set<id>();
            Set<String> taxIdSet = new Set<String>();
            Http httpObj = new Http();
            List<Account> acctLstToInsert =new List<Account>();
            HttpRequest ePassReqVar=new HttpRequest();
            requestParamMap = serviceInputMap(interactionwrapObj.sNPIID,interactionwrapObj.sTaxID,interactionwrapObj.sProviderClassification);
            ePassReqVar=oProvSearchObj.getEPSSReq(requestParamMap);            
            HttpResponse ePassResponseObj=new HttpResponse();
            ePassResponseObj=httpObj.send(ePassReqVar);
            HUMSearchComponentBaseHelperV2 baseHelperObj=new HUMSearchComponentBaseHelperV2();
            if(ePassResponseObj != null && ePassResponseObj.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || ePassResponseObj.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            { 
                lstProviderAccounts = New List<HUMCustomSearchHelper> ();
                lstProviderAccounts=oProvSearchObj.parseServiceResponse(ePassResponseObj,true);
               
                if((NULL != lstProviderAccounts && !lstProviderAccounts.isEmpty() && lstProviderAccounts.size()>0))
                {
                    Account acctRec;
                    acctRec=getAccount(lstProviderAccounts[0]);        
                    
                    //Upserts Provider Account with Consumer ID Details
                    if(acctRec !=null && bSwitch_US4438160){
                        if(lstProviderAccounts[0].sTaxID != '' && lstProviderAccounts[0].sExtID != ''){
                            String sTaxIDs = lstProviderAccounts[0].sTaxID.deleteWhitespace();
                            taxIdSet.addAll(sTaxIDs.split(','));
                            mapTaxIDs.put(lstProviderAccounts[0].sExtID, new List<String>(taxIdSet));
                        }
                        Map<String,Account> accountUpsert = new Map<String,Account>();
                        accountUpsert.put(interactingType,acctRec);
                        performUpsertForKnown(accountUpsert);
                    }

                    if(acctRec !=null && acctRec.id !=null ){
                         accountsMapRetVal.put(interactingType,acctRec);
                    }                                    
                }   
            }          
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseServiceProvider_H_HUM','searchInEpassService');       
        }
        
        return accountsMapRetVal;
    }
    /**
* Method is used to make a service parameter Map
* 
* @param    string, String
* @return   Map<String,String> 
*/    
    public static Map<String,String> serviceInputMap(String sNPI, String sTaxId, String sProviderClassification)
    {
        Map<String,String> requestParamMap =new Map<String,String>();
        requestParamMap.put(HUMConstants.sTaxID, String.isBlank(sTaxId)?'':sTaxId);
        requestParamMap.put(HUMConstants.sNPI, String.isBlank(sNPI)?'':sNPI);
        requestParamMap.put(GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATION, String.isBlank(sProviderClassification)?'':sProviderClassification);
        return requestParamMap;
    }
    /**
* Method to Map Response to Account Object to make DML or to create a Account Record.
* Method Name : getAccount
* @param      : HUMCustomSearchHelper 
* @return     : Account 
*/  
    @testVisible 
    private static Account getAccount(HUMCustomSearchHelper Wrapperdata)
    {
        String specialChars = System.Label.HUMSpecialCharprovider; 
        sProviderRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get(Label.HumProviderRecordType).getRecordTypeId();        
        if(!Pattern.matches(specialChars, Wrapperdata.sLastname))
        {
            for(integer i=0; i<specialChars.split('|').size(); i++)
                Wrapperdata.sLastname=Wrapperdata.sLastname.replace(specialChars.split('|')[i], '');
        }
        if(string.isnotblank(Wrapperdata.sDba) && !Pattern.matches(specialChars, Wrapperdata.sDba))
        {
            for(integer i=0; i<specialChars.split('|').size(); i++)
                Wrapperdata.sDba=Wrapperdata.sDba.replace(specialChars.split('|')[i], '');
        }
        String sName = ((Wrapperdata.sFirstname !=NULL)? Wrapperdata.sFirstname:'') + ((Wrapperdata.sLastname !=NULL)? Wrapperdata.sLastname :'');
        Account oAcc = new Account(Name = sName, recordtypeid = ID.valueof(sProviderRecordTypeId),         
                                   NPI_ID__c = Wrapperdata.sNPI, Description = Wrapperdata.sSpeciality, 
                                   Account_External_ID__c = Wrapperdata.sExtID, BillingStreet = Wrapperdata.sAddress,
                                   BillingStatecode = Wrapperdata.sState, BillingPostalCode = Wrapperdata.sPostalCode,
                                   Billingcity = Wrapperdata.sCity, Provider_Classification__c= Wrapperdata.sClassification,
                                   Birthdate__c = changeDateStringFormat(Wrapperdata.sBirthdate),Gender__c = Wrapperdata.sGender,
                                   Taxonomy_Code__c = wrapperdata.sTaxmonycode, Enterprise_ID__c= wrapperdata.sEnterpriseID ,
                                   Degree__c=wrapperdata.sDegree, Tenant_Id__c = label.HUMExtMemAccountTenantID,
                                   Source_Platform_Code__c =wrapperdata.sPlatform,DBA__c = wrapperdata.sDba,
                                   phone =wrapperdata.sphone,Phone_Ext__c=wrapperdata.sPhoneExtn ,
                                   ShippingStatecode =Wrapperdata.sServiceState , shippingPostalCode = Wrapperdata.sServicezip ,
                                   ShippingCity= Wrapperdata.sServiceCity ,ShippingStreet = wrapperdata.sServiceaddress,
                                   Individual_Last_Name__c = Wrapperdata.sLastname, Individual_First_Name__c = Wrapperdata.sFirstname,
                                   General_Account__c =true );
        return oAcc;
    }    
    
    /**
* Method to create a map with key as externalid
* Method Name : prepareMapWithExtrnlId
* @param      : String snpiId, String sTaxId, String sExternalId, String interactingType
* @return     : void
*/
    public static void prepareMapWithExtrnlId(String snpiId, String sTaxId, String sExternalId, String interactingType)
    {
        if(String.isNotBlank(snpiId))
        {
            if(mapReturnValues.containsKey(interactingType))
            {
                mapReturnValues.get(interactingType).put(sExternalId,snpiId);
            }
            else
            {
                mapReturnValues.put(interactingType, new Map<String,String>{sExternalId=>snpiId});
            }
        }
        if(String.isNotBlank(sTaxId))
        {
            if(mapReturnValues.containsKey(interactingType))
            {
                mapReturnValues.get(interactingType).put(sExternalId,sTaxId);
            }
            else
            {
                mapReturnValues.put(interactingType, new Map<String,String>{sExternalId=>sTaxId});
            }
        }
    }
    /**
* Method is used to change the date string format from'yyyy-MM-dd' to 'MM/dd/yyyy'
*
* @param    string-sdob
* @return   string -sModifiedDOB
*/
    @testVisible
    private Static String changeDateStringFormat(String sdob)
    {
        String sModifiedDOB='';
        List<String> lstdobValues=new List<String>();
        
        if(sdob != NULL)
        {
            lstdobValues =  sdob.split(HUMConstants.sHiphan);
            if(lstdobValues.size() == HUMConstants.VALUE_THREE) sModifiedDOB=  lstdobValues[HUMConstants.VALUE_ONE]+'/'+lstdobValues[HUMConstants.VALUE_TWO]+'/'+lstdobValues[HUMConstants.VALUE_ZERO];
        }
        return sModifiedDOB;
    }
  /*
* performDMLOperationsQueuable
* <p>
* This method is used to perform create case for provider in Asynchronous mode
* @param Map<String,Account>
* @return void
*/   
    public static void performDMLOperationsQueuable(Map<String,Account> accountMapToUpsert){       
       CreateCaseAsynch_Provider_S_V4_HUM updateJob = new CreateCaseAsynch_Provider_S_V4_HUM(accountMapToUpsert);
        ID jobID = System.enqueueJob(updateJob); 
    }
   /*
* performUpsertForKnown
* <p>
* This method is used to upsert unknown account
* @param Map<String,Account>
* @return Map<String,Account>
*/          
    public static void performUpsertForKnown(Map<String,Account> accountMapToUpsertKnown){
        Map<String,Account> mapresultAccount = new Map<String,Account>();
        Map<String,Map<String,String>> mapFinalProviderData = new Map<String,Map<String,String>>();
        //ADD TRY CATCH
        try{
          if(accountMapToUpsertKnown!=null && !accountMapToUpsertKnown.isEmpty())
        {
            mapresultAccount = upsertAccountRec(accountMapToUpsertKnown);            
            if(mapresultAccount!=null && !mapresultAccount.isEmpty())
            {
                upsertconsumerIds(mapresultAccount); 
            }
        }    
        }catch(exception ex){
             HUMExceptionHelper.logErrors(ex,'CaseServiceProvider_v4_H_HUM','performUpsertForKnown');            
        }
       
    }
  
    
    /*
* upsertAccountRec
* <p>
* This method is used to upsert account records
* @param Map<String,List<Account>> accountMap
* @return Map<String,List<Account>>
*/  
    public static Map<String,Account> upsertAccountRec(Map<String,Account> accountMap)
    { 
        Map<String,Account> mapProviderAccounts = new Map<String,Account>();
        List<Account> lstOrderedAccounts = new List<Account>();        
        Boolean binteractingWithPresent = false;
        Boolean bUniqueWithAbout = false;
        String sIntWithExternalId;
        String sIntAbout='Interacting_About';
        String sIntWith='Interacting_With';
        List<Account> lstOfOrderedAccounts = new List<Account>();  
        
        if(!accountMap.isEmpty()  && accountMap.get(sIntWith)!=null)
        {
            lstorderedAccounts.add(accountMap.get(sIntWith)); 
            binteractingWithPresent = true;
            for(ACCOUNT act : lstorderedAccounts){
                if(accountMap.containsKey('Account_External_ID__c')){
                    sIntWithExternalId = lstorderedAccounts[0].Account_External_ID__c;
                }
            }
        }
        
        if(!accountMap.isEmpty() && accountMap.get(sIntAbout)!=null)
        {
            if(binteractingWithPresent && String.isNotBlank(sIntWithExternalId))  
            {  
                if(accountMap.get(sIntAbout).Account_External_ID__c == sIntWithExternalId)
                {
                    bUniqueWithAbout = true;   
                }
                else
                    lstorderedAccounts.add(accountMap.get(sIntAbout));
            }
            else{
                lstorderedAccounts.add(accountMap.get(sIntAbout));
            }   
        }
        
        lstorderedAccounts = HUMInteractionMemberDMLHelper.upsertAccountForInteractionID(lstorderedAccounts);
        
        if(!lstorderedAccounts.isEmpty() && lstorderedAccounts !=null)
        {            
            if(binteractingWithPresent)
            {               
                mapProviderAccounts.put(sIntWith,lstorderedAccounts[0]);    
            }
            if(!lstorderedAccounts.isEmpty())
            {
                if(bUniqueWithAbout)
                {
                    mapProviderAccounts.put(sIntAbout,lstorderedAccounts[0]);                      
                }
                else
                {
                    mapProviderAccounts.put(sIntAbout,lstorderedAccounts[0]);  
                }
                bUniqueWithAbout = false;               
            }
        }        
        return mapProviderAccounts;
    }
    
    /*
* upsertconsumerIds
* <p>
* This method is used to upsert consumerid records
* @param Map<String,List<Account>> accountMapToUpsert
* @return Map<String,String>
*/     
    public static Map<String,String> upsertconsumerIds(Map<String,Account> accountMapToUpsert)
    {
        try
        {
            Set<String> setUniqueExtrnalId = new Set<String>();
            Map<String,List<String>> fetchTaxIdMap = new Map<String,List<String>>();
            Map<String,List<String>> mapAllTaxIds = new Map<String,List<String>>();
            Set<ConsumerID__c> uniqueConsumerSet=new Set<ConsumerID__c>();
            for(String Interactiontype : accountMapToUpsert.keySet())
            {   
                setUniqueExtrnalId.add(accountMapToUpsert.get(Interactiontype).Account_External_ID__c);             
            }
            fetchTaxIdMap.putAll(CaseServiceProvider_v4_H_HUM.mapTaxIDs);              
            List<Account> lstAccount = [select id,Account_External_ID__c from Account where Account_External_ID__c in:setUniqueExtrnalId AND ETL_Record_Deleted__c=False];
            for(Account oAcc : lstAccount)
            {
                for(String sAcctExtId : fetchTaxIdMap.keyset())    
                {
                    if(oAcc.Account_External_ID__c == sAcctExtId)
                    {
                        mapAllTaxIds.put(oAcc.id,fetchTaxIdMap.get(sAcctExtId));
                    }
                }
            }
            
            if(!mapAllTaxIds.isEmpty() && mapAllTaxIds!=null)
            {
                for(String sAccId: mapAllTaxIds.keyset())
                {
                    for(String sTaxId:mapAllTaxIds.get(sAccId))
                    {
                        uniqueConsumerSet.add(assignConsumerIds(sTaxId,sAccId,GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID));
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
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseServiceProvider_v4_H_HUM','upsertconsumerIds');
            return null; 
        }
    }
    
    /*
* assignConsumerIds
* <p>
* This method is used to map instance for consumerid object
* @param String sTaxId, String sAccountId, string sType
* @return ConsumerID__c
*/      
    public static ConsumerID__c assignConsumerIds(String sTaxId, String sAccountId, string sType)
    {
        ConsumerID__c consumerRec = new ConsumerID__c();
        consumerRec.Account__c = sAccountId;
        consumerRec.Consumer_ID__c = sTaxId;
        consumerRec.ID_Type__c = sType;
        consumerRec.Consumer_External_ID__c = CreateCaseRest_H_HUM.getConsumerExternalID(sTaxId,sAccountId);
        return consumerRec;
    }
}