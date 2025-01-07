/*******************************************************************************************************************************
Apex Class Name :  CaseServiceProvider_H_HUM.cls
Version: 1.0
Created On      : 10/17/2018 
Function        : Helper class to get Account Map For provider from Case service
Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Charan konakandla          314128                    10/17/2018                Original Version 
* Prasanthi kandula                                    4/19/2019                 REQ - 383786 - Added maps for vantage project
* Moshitha Gunasekaran                                 01/08/2021                US1534952 - INC0462428 - CRM Service - Incorrect NPI Populating on CRM Case Upon transfer from MHK / NPI Not Populating on CRM Case
* Sagar MN					                           02/10/2021	      US1867250 - Added ETL Record deleted flag to queries
*******************************************************************************************************************************/
public with sharing class CaseServiceProvider_H_HUM 
{
    private List<HUMCustomSearchHelper> lstProviderAccounts;
    private ProviderSearch_H_HUM oProvSearchObj;
    private id sProviderRecordTypeId;
    private Map<id,Account> upsertAcctMap;
    private Boolean bErrorFlag=false;
    private Map<String,List<ConsumerID__c>> mapAconsumerAccount;
    private Set<String> setConsumerIds = new Set <String>();
    private Set <String> setAccIds;
    private Map<String,String> requestParamMap;
    private Set<id> accountIdSet;
    private Map<String,Account> accountsMap = new Map<String,Account>();
    private HUMSearchComponentBaseHelperV2 baseHelperObj ;
    private Map<String,List<sObject>> AcctConsumerMap = new Map<String,List<sObject>>();
    public static Map<String,Boolean> bisMultipleAcct = new Map<String,Boolean>();
    public static Map<String,List<String>> taxIdMap = new Map<String,List<String>>();
    public static Map<String,String> NPIIDdMap = new Map<String,String>();
    public static Map<String,List<String>> mapTaxIDs = new Map<String,List<String>>();
    public static Map<String,Map<String,String>> mapReturnValues = new Map<String,Map<String,String>>();
    
    /**
    * Method to Make a callout to Epass Service and Search in Salesforce if EPASS doesnot return any records.
    * Method Name : searchInEpassService
    * @param      : String-sNPI,String-sTaxId,String-interactingType,String-providerClassification
    * @return     : Map<String,Account> - accountsMap
    */
    public Map<String,Account> searchInEpassService(CaseServiceRestInteraction_DTO_HUM interactionwrapObj,String interactingType)
    {
        try
        {
            oProvSearchObj = new ProviderSearch_H_HUM();
            accountIdSet = new Set<id>();
            Set<String> taxIdSet = new Set<String>();
            Http httpObj = new Http();
            List<Account> acctLstToInsert =new List<Account>();
            HttpRequest ePassReqVar=new HttpRequest();
            ePassReqVar=oProvSearchObj.getEPSSReq(serviceInputMap(interactionwrapObj));
            HttpResponse ePassResponseObj=new HttpResponse();
            ePassResponseObj=httpObj.send(ePassReqVar);
            HUMSearchComponentBaseHelperV2 baseHelperObj=new HUMSearchComponentBaseHelperV2();
            if(ePassResponseObj != null && ePassResponseObj.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || ePassResponseObj.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            {
               
                lstProviderAccounts = New List<HUMCustomSearchHelper> ();
                lstProviderAccounts=oProvSearchObj.parseServiceResponse(ePassResponseObj,true);
                if(NULL != lstProviderAccounts && !lstProviderAccounts.isEmpty() && lstProviderAccounts.size()>1)
                {
                    lstProviderAccounts=EvaluateProviderResponse(lstProviderAccounts,interactionwrapObj);   
                }
                if((NULL != lstProviderAccounts && !lstProviderAccounts.isEmpty() && lstProviderAccounts.size()>0))
                {
                    Account acctRec=new Account();
                    acctRec=getAccount(lstProviderAccounts[0], interactionwrapObj.sNPIID);
                    prepareMapWithExtrnlId(interactionwrapObj.sNPIID,interactionwrapObj.sTaxID,acctRec.Account_External_ID__c,interactingType);
                    accountsMap.put(interactingType,acctRec);
                    List<String> taxIdLst =new List<String>();
                    taxIdLst=((lstProviderAccounts[0].sTaxID).split(',')).size()>0?(lstProviderAccounts[0].sTaxID).split(','):new List<String>(taxIdLst);
                    for(String sTaxidStr:taxIdLst)
                    {
                        if(String.isNotBlank(sTaxidStr))
                        {
                            taxIdSet.add(sTaxidStr.trim());
                        }
                    }
                    taxIdLst =new List<String>();
                    taxIdLst.addAll(taxIdSet);
                    if(taxIdLst!=null && !taxIdLst.isEmpty())
                    {
                        taxIdMap.put(interactingType,taxIdLst);
                        mapTaxIDs.put(acctRec.Account_External_ID__c,taxIdLst);
                    }
                    NPIIDdMap.put(interactingType,acctRec.NPI_ID__c);
                    if(accountsMap!=null && !accountsMap.values().isEmpty() && accountsMap.containsKey(interactingType))
                    {
                        bisMultipleAcct.put(interactingType,false);
                    }
                }
         else if(lstProviderAccounts.isEmpty() && (String.isNotBlank(interactionwrapObj.sTaxID)||String.isNotBlank(interactionwrapObj.sNPIID)))
                {
                    accountsMap.putAll(fetchAccountRecords(interactionwrapObj.sTaxID, interactionwrapObj.sNPIID, interactingType));
                   
                    if(accountsMap!=null && !accountsMap.values().isEmpty() && accountsMap.containsKey(interactingType))
                    {
                        bisMultipleAcct.put(interactingType,false);
                    }   
                }
               
                if(accountsMap==null || accountsMap.isEmpty())
                {
                    bisMultipleAcct.put(interactingType,true);
                }
            }
            else
            {
                //This Condition is to create a Unknown Account if service callout fails.
                bisMultipleAcct.put(interactingType,true);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CaseServiceProvider_H_HUM','searchInEpassService');       
        }
       
        return accountsMap;
    }
    
    /**
    * Method to Make a Search in Salesforce if EPASS doesnot return any records.
    * Method Name : fetchAccountRecords
    * @param      : String-sTaxId,String-sNPIId,String-interactingType
    * @return     : Map<String,Account> - acctMaps
    */
    public Map<String,Account> fetchAccountRecords(String sTaxId, String sNPIId, String interactingType)
    {
        Map<String,Account> acctMap=new Map<String,Account>();
        List<Account> lstMembers = new List<Account>();
        setAccIds = new Set <String>();
        List<ConsumerID__c> lstTemp = new List<ConsumerID__c>();
        mapAconsumerAccount = new Map<string,List<ConsumerID__c>>();
        if(requestParamMap!=null && !requestParamMap.isEmpty())
        {
            String saccountQuery=createPlatformQieries(requestParamMap);
            
            if(String.isNotBlank(saccountQuery))
            {
                lstMembers = Database.Query(saccountQuery);
            }
        }
        if(lstMembers!=null && !lstMembers.isEmpty())
        {
            prepareMapWithExtrnlId(sNPIID,sTaxID,lstMembers[0].Account_External_ID__c,interactingType);
            acctMap.put(interactingType,lstMembers[0]);
        }
        For(Account obj :lstMembers )
        {
            setAccIds.Add(Obj.Id);
            mapAconsumerAccount.put(Obj.Id,lstTemp );
           
        }
        queryConsumerIDObject();
        return acctMap;
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
    * Method to Map Response to Account Object to make DML or to create a Account Record.
    * Method Name : getAccount
    * @param      : HUMCustomSearchHelper - Wrapperdata
    * @return     : Account - oAcc
    */  
    @testVisible private Account getAccount(HUMCustomSearchHelper Wrapperdata, String NPIId)
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
                                    NPI_ID__c = String.isNotBlank(NPIId)? NPIId: Wrapperdata.sNPI, Description = Wrapperdata.sSpeciality, 
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
                                   General_Account__c =true, ETL_Record_Deleted__c= false);
        return oAcc;
    }
    /**
    * Method is used to change the date string format from'yyyy-MM-dd' to 'MM/dd/yyyy'
    *
    * @param    string-sdob
    * @return   string -sModifiedDOB
    */
    @testVisible private String changeDateStringFormat(String sdob)
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
    /**
    * Method is used to make a service parameter Map
    * 
    * @param    string, String
    * @return   Map<String,String> 
    */    
    public Map<String,String> serviceInputMap(CaseServiceRestInteraction_DTO_HUM interactionwrapObj)
    {
        requestParamMap =new Map<String,String>();
        requestParamMap.put(HUMConstants.sTaxID, String.isBlank(interactionwrapObj.sTaxId)?'':interactionwrapObj.sTaxId);
        requestParamMap.put(HUMConstants.sNPI, String.isBlank(interactionwrapObj.sNPIID)?'':interactionwrapObj.sNPIID);
        requestParamMap.put('sFirstName', String.isBlank(interactionwrapObj.sFirstName)?'':interactionwrapObj.sFirstName);
        requestParamMap.put('sLastName', String.isBlank(interactionwrapObj.sLastName)?'':interactionwrapObj.sLastName);
        requestParamMap.put('sFacilityName', String.isBlank(interactionwrapObj.sAccountName)?'':interactionwrapObj.sAccountName);
        requestParamMap.put(GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATION, String.isBlank(interactionwrapObj.sProviderClassification)?'':interactionwrapObj.sProviderClassification);
        return requestParamMap;
    }
    
    /**
    * Method to create queries in case of platform encryption
    *
    * @param    Map<String,String>  requestDTOMap
    * @return   String 
    */
    public String createPlatformQieries(Map<String,String> requestDTOMap )
    {
        String sPlatformSOQLQuery ='Select Id, Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
            'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet, '+
            ' NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c From Account Where (RecordType.DeveloperName='+'\''+HUMConstants.HumProviderRecordType+'\''+
            +' OR RecordType.DeveloperName ='+'\''+ HUMConstants.HumUnknownProviderRecordType+'\')';
        
        mapAconsumerAccount = new Map<String,List<ConsumerID__c>>();
        String sProviderClassification = '';
        
        if(String.isNotEmpty(requestDTOMap.get(GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATION)))
        {
            if(String.isNotEmpty(requestDTOMap.get(GLOBAL_CONSTANT_HUM.PROVIDER_CLASSIFICATION)))
            {
                sProviderClassification  = '(Provider_Classification__c = \'' + HUMConstants.sFacilityprovider + '\' OR Provider_Classification__c ='+'\''+HUMConstants.sGroupprovider+'\''+')';
            }
            else
            {
                sProviderClassification = 'Provider_Classification__c = \'' + HUMConstants.sIndividualprovider+ '\'';
            }
        }
        if(String.isNotBlank(requestDTOMap.get(HUMConstants.sNPI)))  
        {
            sPlatformSOQLQuery += ' AND NPI_ID__c = \'' +requestDTOMap.get(HUMConstants.sNPI).trim()+ '\' '; 
        }
        If(String.IsNotBlank(requestDTOMap.get(HUMConstants.sTaxID)))
        {
           searchConsumerIDObject(requestDTOMap.get(HUMConstants.sTaxID));
            
            if(!mapAconsumerAccount.isEmpty())
            {
                setConsumerIds = mapAconsumerAccount.keySet();
                sPlatformSOQLQuery += ' AND ID IN : setConsumerIds ';
            }
        }
        sPlatformSOQLQuery+='AND ETL_Record_Deleted__c=False';
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
    private void searchConsumerIDObject(String sTaxId)
    {
        String sKey = sTaxId+'*';
        String sConsumerQuery = '';
        List<ConsumerID__c> lstConsumers = new List<ConsumerID__c>();
        List<ConsumerID__c> lstTempConsumers;
        sConsumerQuery = 'FIND \'' + sKey  +'\' IN ALL FIELDS RETURNING ConsumerID__c (Account__c,Consumer_ID__c  Where ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\''+' AND ETL_Record_Deleted__c = false Order by Account__c )';
        List<List<sObject>> searchList = search.query(sConsumerQuery);
        lstTempConsumers = ((List<ConsumerID__c>)searchList[0]);
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
    * @return   None 
    */  
    private void prepareConsumerIDMap(List<ConsumerID__c> lstConsumers )
    {
        
        if(lstConsumers != Null && !lstConsumers.IsEmpty())
        {
            List<ConsumerID__c> lstTempConsumers = new List<ConsumerID__c>();
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
                    mapAconsumerAccount.Put(sId,lstTempConsumers); 
                    sId = lstConsumers[i].Account__c;
                    lstTempConsumers = new List<ConsumerID__c>();
                    lstTempConsumers.Add(lstConsumers[i]);
                }
            }
            mapAconsumerAccount.Put(sId,lstTempConsumers); 
        }
    }
    
    /**
    * This method will be used search consumer id object
    * 
    * @param    None
    * @return   Object 
    */
    private void queryConsumerIDObject()
    {
       
        String sConsumerQuery ='';
        List<ConsumerID__c> lstConsumers ;
        sConsumerQuery = 'Select Account__c,Consumer_ID__c FROM ConsumerID__c WHERE ' ;
        sConsumerQuery += 'ID_Type__c = \'' + string.escapeSingleQuotes(Label.HumServiceTaxIDtag) + '\' AND ETL_Record_Deleted__c = false ';
        if(setAccIds!=null)
        {
            sConsumerQuery += ' AND Account__c IN : setAccIds';
        }
        sConsumerQuery = sConsumerQuery + ' Limit ' + Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c);    
        lstConsumers = Database.Query(sConsumerQuery );
        prepareConsumerIDMap(lstConsumers);
    }
    
    /**
    * This method queries in Salesforce Data Base.
    * 
    * @param    None
    * @return   Object 
    */
    public List<Account> queryAccounts(Map<String,String> requestDTOMap)
    {
        String queryProviderAcct=createPlatformQieries(requestDTOMap);
        List<Account> accountLst=new List<Account>();
        if(String.isNotBlank(queryProviderAcct))
        {
            accountLst = (List<Account>)Database.Query(queryProviderAcct);
        }
        return accountLst;
    }
    
    /**
    * This method evaluates Provider response and checks if response is matching with request values.
    * 
    * @param    List<HUMCustomSearchHelper> lstProviderAccounts,CaseServiceRestInteraction_DTO_HUM interactionwrapObj
    * @return   Object 
    */
    private List<HUMCustomSearchHelper> EvaluateProviderResponse(List<HUMCustomSearchHelper> lstProviderAccounts,CaseServiceRestInteraction_DTO_HUM interactionwrapObj)
    {
        List<HUMCustomSearchHelper> lstMatchedProviderAccounts = new List<HUMCustomSearchHelper>();
        if(lstProviderAccounts!=null && !lstProviderAccounts.isEmpty() && lstProviderAccounts.size()>1)
        {
            for(HUMCustomSearchHelper provAccount : lstProviderAccounts)
            {
                Boolean bIsNameMatching  = false;
                Boolean bIsAddressMatching  = false;
                if(String.isNotBlank(interactionwrapObj.sFirstName) && String.isNotBlank(interactionwrapObj.sLastname) 
                &&  !((interactionwrapObj.sFirstName).equalsIgnoreCase('MissingFN')) && !((interactionwrapObj.sLastname).equalsIgnoreCase('MissingLN'))) 
                {
                    if(provAccount.sFirstName.equalsIgnoreCase(interactionwrapObj.sFirstName)
                    && provAccount.sLastName.equalsIgnoreCase(interactionwrapObj.sLastname)) bIsNameMatching = true;
                }
                else if(String.isNotBlank(interactionwrapObj.sLastname) && !((interactionwrapObj.sLastname).equalsIgnoreCase('MissingLN')) &&
                String.isNotBlank(provAccount.sLastName))
                {
                    if(provAccount.sLastName.equalsIgnoreCase(interactionwrapObj.sLastname)) bIsNameMatching = true;
                }
                else if(String.isNotBlank(interactionwrapObj.sAccountName) 
                && provAccount.sLastName.equalsIgnoreCase(interactionwrapObj.sAccountName)) bIsNameMatching = true;
                if(String.isNotBlank(interactionwrapObj.sStateCode) && String.isNotBlank(interactionwrapObj.sInteractingZipCode) 
                && String.isNotBlank(provAccount.sServiceState) && String.isNotBlank(provAccount.sServicezip)
                && provAccount.sServiceState.equalsIgnoreCase(interactionwrapObj.sStateCode) && provAccount.sServicezip.equalsIgnoreCase(interactionwrapObj.sInteractingZipCode.substring(0,5)))
                {
                    bIsAddressMatching = true;
                }
                else if(String.isNotBlank(interactionwrapObj.sStateCode) && String.isNotBlank(interactionwrapObj.sInteractingZipCode) 
                && String.isNotBlank(provAccount.sState) && String.isNotBlank(provAccount.sPostalCode)
                && provAccount.sState.equalsIgnoreCase(interactionwrapObj.sStateCode) && provAccount.sPostalCode.equalsIgnoreCase(interactionwrapObj.sInteractingZipCode.substring(0,5)))
                {
                    bIsAddressMatching = true;
                }
                else if(String.isNotBlank(interactionwrapObj.sStateCode) && String.isNotBlank(interactionwrapObj.sInteractingZipCode))
                {
                    bIsAddressMatching = false;
                }
                if((bIsAddressMatching && bIsNameMatching) || bIsNameMatching)
                {           
                    lstMatchedProviderAccounts.Add(provAccount);
                }
            }
            if(lstMatchedProviderAccounts.isEmpty())
            {
                lstMatchedProviderAccounts = lstProviderAccounts;
            }
            
        }
        return lstMatchedProviderAccounts;
    }
}