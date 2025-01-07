/******************************************************************************************************************
Apex class Name    : CreateCaseRequest_H_HUM
Version            : 1.0
Function           : This class is used as request  for ineteracting with/About 
Created On         : July 31 2019
Test Class         : 

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Asish Behera                                                       
*   1.1            Aissh Behera                                   08/15/2019           unknownmember scenario added
*   1.2            Aissh Behera                                   08/21/2019           Group scenario handled
*   1.3            Aissh Behera                                   08/21/2019           Group-provider issue and exception strategy
*   1.4            Aissh Behera                                   08/28/2019           Enterprise person id issue fix
*   1.5            Aissh Behera                                   05/28/2020           DOB formated to MM/DD/YYYY format.
*   1.6            Moshitha Gunasekaran                           01/18/2021           US1534952 - INC0462428 - CRM Service - Incorrect NPI Populating on CRM Case Upon transfer from MHK / NPI Not Populating on CRM Case
*   2.0			   Sagar MN									  	  10/10/2020		   Updating code for uknown provider fix - MF1
* 	2.1			   Sagar MN										  03/04/2021		   US 1999913 Code checkin for MF1
*   2.2            Vaishnavi Manda                                03/25/2021           INC0934323 - Handled member scenario for member-id-base if dependent code is not passed
*   2.3            Vaishnavi Manda                                05/12/2021           US 2163327 - MF6 Checkmarx scan issue Fix
*   2.4            Sagar MN                                       05/20/2021           INC1060956 Added Meicaid-Id Id Type for Member Case Creation V4
*   2.5            Tummala Vijaya Lakshmi                         10/22/2021           US 2696540 T1PRJ0002606/PR00094268 :  MF6 -INC1341295 - Unknown Provider case Not getting created caseserviceV4
------------------------------------------------------------------------------------------------------------------- */
public With Sharing class CreateCaseRequest_H_HUM {
    
    private static Map<String,Account> accountMapToUpsertProv = new Map<String,Account>();
    private static final string RECORDTYPE_MEMBER = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM;
    private static Map<String,Account> accountMapToUpsertGroup = new Map<String,Account>();
    public static Map<String,List<Account>> unknownAccountMapToUpsert = new Map<String,List<Account>>(); 
    public static Map<String,List<Account>> accountMapToUpsert = new Map<String,List<Account>>();  
    public static Map<String,List<Account>> groupaccountMapToUpsert = new Map<String,List<Account>>();  
    private static Map<Integer,String> mapProviderId = new Map<Integer,String>();
    private static List<Account> providerAcct = new List<Account>();
    public static Map<String,String> mapunknownProvider = new map<string,string>();
    private static Integer iProviderKey=0; 
    private static Integer iGroupKey=0;
    private static String sIntercatingType;
    private static Map<String,Map<String,String>> mapProviderDetails = new Map<String,Map<String,String>>();
    private static map<string, string> mapInteractAboutAccount;
    private static map<string, Account> mapInteractAbtAccnt;
    private static map<String, map<string,set<string>>> mapInquireAboutCriteria = new map<String, map<string,set<string>>>();
    private static map<string, map<string, set<string>>> mapMemberDependentCode= new map<string, map<string, set<string>>>();
    private static Map<String,Map<String,String>> mapUnknownmemberdetails = New Map<String,Map<String,String>>();
    private static map<string,List<InteractingAccount_DTO_HUM>> mapIntWithAboutUnknownCriteria = new map<string, list<InteractingAccount_DTO_HUM>>();
    public static boolean blninsertunknowngrp = true;  
    public static Map<String,String> mapunknownGroup = new map<string,string>();
    public static map<string,map<string,string>> unknwnMbrRequest = new map<string,map<string,string>>();
    private static Map<String,Map<String,String>> mapGroupDetails = new Map<String,Map<String,String>>();
    private static List<Account> GroupAcct = new List<Account>();
    private static Map<Integer,String> mapGroupId = new Map<Integer,String>();
    Static final Map<String,String> mapRecordtypeInfo= New map<String,String>
    {'UM' => HUMConstants__c.getInstance('UNKNOWN_MEMBER').Stringvalue__c
        };
            public static List<String> lstAcctId = new List<String>();
     public static List<Account> lstAccounttId = new List<Account>();
    public static map<String,map<String,String>> mapUnknwnMemberFrmService = new map<String,map<String,String>>();
    private static Map<Integer,String> mapAgentId = new Map<Integer,String>();     
    public with sharing Class CreateUnknownMembers_DTO_HUM
    {        
        public String MemberInteractingId{get;set;}
        public String AccId{get;set;} 
        public String MemberInteractingIdType{get;set;}
        public String AccntextrnlID{get;set;}
        
    }
   /*
* getInteractingAccountDetails_V4
* <p>
* This method is used to get the Interacting details for Agent,Provider,Member & Group
* @param    InteractingAccount_DTO_HUM ,String 
* @return   map<String,Account>                  
*/  
    public static map<String,Account> getInteractingAccountDetails_V4(InteractingAccount_DTO_HUM intAccWIthAbout, String sIntercatingType){
        
        try{
            map<String,Account> map_Id_AccId  = new map<String,Account>();
            map<String,String> map_String_Id_AccId  = new map<String,String>();
             List<Account> lstAccount = new List<Account>();
	    map<String,String> map_Unknown_Id_AccId  = new map<String,String>();
            
            sIntercatingType =  sIntercatingType;       
            //varibales to hold request based on IDType.
            InteractingAccount_DTO_HUM providerAccountDTO = new InteractingAccount_DTO_HUM();
            InteractingAccount_DTO_HUM agentAccountDTO = new InteractingAccount_DTO_HUM();	       
            InteractingAccount_DTO_HUM unknownProviderIntAcc = new InteractingAccount_DTO_HUM();
            InteractingAccount_DTO_HUM unknownAgentIntAcc = new InteractingAccount_DTO_HUM();
            InteractingAccount_DTO_HUM unknownGroupIntAcc = new InteractingAccount_DTO_HUM();
            InteractingAccount_DTO_HUM memberAccountDTO = new InteractingAccount_DTO_HUM();
            List<InteractingAccount_DTO_HUM> listGroupIntAcc = new List<InteractingAccount_DTO_HUM>();
            List<InteractingAccount_DTO_HUM> listMemberIntAcc = new List<InteractingAccount_DTO_HUM>();
            List<InteractingAccount_DTO_HUM> listUnknownICreateCaseRequest_H_HUMntAcc = new List<InteractingAccount_DTO_HUM>();
	    List<InteractingAccount_DTO_HUM> listUnknownIntAcc = new List<InteractingAccount_DTO_HUM>();
	    
            boolean bswitch_US2696540=HUMUtilityHelper.isCRMFunctionalityON('2696540');
            // for known provider/agent scenarios
            if(String.isNotBlank(intAccWIthAbout.ID)){                
                if(intAccWIthAbout.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM)){						
                    listMemberIntAcc.add(intAccWIthAbout);				
                }else if(intAccWIthAbout.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM) && !intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)){
                    providerAccountDTO = intAccWIthAbout;
                }else if(intAccWIthAbout.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM) && (!intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.HUMUnknownAgentIntType) || !intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CLOSEDCASE_UNKNOWNAGENTBROKER_HUM))){
                    agentAccountDTO = intAccWIthAbout;
                }else if(intAccWIthAbout.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM) && intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID))
                    listGroupIntAcc.add(intAccWIthAbout);
            }else{ 
                if(string.isNotBlank(intAccWIthAbout.EntityType) && String.isBlank(intAccWIthAbout.ID) && string.isNotBlank(intAccWIthAbout.IDType) && intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM))
                     {
                          
                            unknwnMbrRequest.put(sIntercatingType, new map<string,string>{intAccWIthAbout.IDType=>intAccWIthAbout.ID});
                         listUnknownIntAcc.add(intAccWIthAbout);
                     }
            }
 
            if(String.isNotBlank(intAccWIthAbout.IDType)){
                if(intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)){                           
                    unknownProviderIntAcc = intAccWIthAbout;
                }                
            }           
            
            //Provider scenarios 
            if(providerAccountDTO!=NULL && providerAccountDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTPROVIDERTYPE_HUM))
            {                
                map_Id_AccId = new map<String,Account>();
                map_Id_AccId = getProviderRecordId_V4(providerAccountDTO,sIntercatingType);               
                return map_Id_AccId;
            }
            
            //Agent Sceanrios
            if(agentAccountDTO!=NULL && agentAccountDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM))
            {                
                map_Id_AccId = new map<String,Account>();
                map_Id_AccId = getAgentRecordId_V4(agentAccountDTO,sIntercatingType);
                return map_Id_AccId;             
            }
            
            //Group scenarios 
            if(!listGroupIntAcc.Isempty()){
                map_String_Id_AccId = getGroupRecordId(listGroupIntAcc,sIntercatingType);                    
                //---->Added by Sagar 3/3/21 
                List<String> sLstAccId = new List<String>(); 
                List<Account> accountList;
                for(String srval:map_String_Id_AccId.keySet()){               
                    sLstAccId.add(map_String_Id_AccId.get(srval));                  
                }
                
                String sQueryval = 'Select Id, Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
                    'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet,'+
                    'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c,Enterprise_ID__c From Account Where Id = '+'\''+sLstAccId[0]+'\'';
                
                if(String.isNotBlank(sQueryval))            
                {
                    accountList = (List<Account>)Database.Query(sQueryval);
                }
                    map_Id_AccId.put(sIntercatingType,accountList[0]);

                //--->end
            }
            //member scenarios 
            if(listMemberIntAcc!=null && !listMemberIntAcc.isEmpty()){            
                map_String_Id_AccId = getmemberRecordId(listMemberIntAcc,sIntercatingType);            
                //---->Added by Sagar 3/3/21 
                List<String> sListAccId = new List<String>();
                List<Account> accountLst;
                for(String sr:map_String_Id_AccId.keySet()){               
                    sListAccId.add(map_String_Id_AccId.get(sr));                   
                }
                
                String sQry = 'Select Id, Name, RecordType.Name, Individual_First_Name__c,Description,Primary_Specialty__c,'+
                    'BillingPostalCode, Individual_Last_Name__c, Account_External_ID__c, BillingState,BillingStateCode, BillingStreet,'+
                    'NPI_ID__c, RecordTypeid,ETL_Record_Deleted__c,Enterprise_ID__c From Account Where Id = '+'\''+sListAccId[0]+'\'';
                
                if(String.isNotBlank(sQry))            
                {
                    accountLst = (List<Account>)Database.Query(sQry);
                }    
                    map_Id_AccId.put(sIntercatingType,accountLst[0]);
                    
                //--->end
            }
            
            //Unknown Member scenarios --Prod issue Fix
            //INC1341295
            if(!unknwnMbrRequest.Isempty() && (unknwnMbrRequest!=null)){
                    
            map_Unknown_Id_AccId = new map<String,string>();            
            mapInteractAbtAccnt = new map<string, Account>();
            mapIntWithAboutUnknownCriteria.putall(createUnknownAccWrapper(unknwnMbrRequest.get(sIntercatingType), listUnknownIntAcc,sIntercatingType));
           
            if(mapIntWithAboutUnknownCriteria != null) mapUnknownmemberdetails = getUknownMemberMaps(mapIntWithAboutUnknownCriteria,sIntercatingType);
            if(mapUnknownmemberdetails.get(sIntercatingType).isEmpty()){                
                map_Id_AccId.put(sIntercatingType,lstAccounttId[0]);
                
            }else{
                if(mapUnknownmemberdetails.containskey(sIntercatingType)) mapInteractAboutAccount.putAll(mapUnknownmemberdetails.get(sIntercatingType));            	
            	map_Id_AccId.putAll(mapInteractAbtAccnt);
                
                }
            }
           	 //unknown provider scenarios
            if(bswitch_US2696540){
                if(unknownProviderIntAcc!= NULL){    
                    if(intAccWIthAbout.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)){ 
             		map_Id_AccId = getProviderRecordId_V4(unknownProviderIntAcc,sIntercatingType);               
            	 }
            }
            }
            
            
            return map_Id_AccId;
        }catch(Exception ex)
        {
            throw ex;
        }              
    }
    
   
/*
* getGroupRecordId
* <p>
* This method is used to get the Group account details
* @param    List<InteractingAccount_DTO_HUM> ,String 
* @return   map<String,String>                  
*/        
    public static map<String,String> getGroupRecordId( List<InteractingAccount_DTO_HUM> listGroupIntAcc,String sIntercatingType){
       
        try{
            mapInteractAboutAccount = new map<string, string>();          
            for(InteractingAccount_DTO_HUM groupRecord:listGroupIntAcc){
                getGroupAccountMap(groupRecord,sIntercatingType);
            }
            
            //for watson Group-           
            if((CreateCaseRequest_H_HUM.groupaccountMapToUpsert !=null && !CreateCaseRequest_H_HUM.groupaccountMapToUpsert.isEmpty()) || (CreateCaseRequest_H_HUM.unknownAccountMapToUpsert!=null && !CreateCaseRequest_H_HUM.unknownAccountMapToUpsert.isEmpty()))
            {           
                mapGroupDetails.putAll(calltoUpsertGroupAccounts());
            }         
            if(mapGroupDetails.containskey(sIntercatingType)) mapInteractAboutAccount.putAll(mapGroupDetails.get(sIntercatingType));
            map<String,string> mapGroupid_AccountId =  mapGroupDetails.get(sIntercatingType);          
            return mapGroupid_AccountId;    
        }catch(Exception ex){
            throw ex;
        }
        
    }
   /*
* getGroupRecordId
* <p>
* This method is used to get the MemberRecordID details
* @param    List<InteractingAccount_DTO_HUM> ,String 
* @return   map<String,String>                  
*/       
    public static map<String,String> getmemberRecordId( List<InteractingAccount_DTO_HUM> listMemberIntAcc,String sIntercatingType){
        try{
            map<string, map<string,set<string>>> mapIDValue =new map<string, map<string,set<string>>>();        
            map<string, set<string>> mapMemberIDValues = new map<string, set<string>>();
            map<string, string> mapGrpPlatformCode = new map<string, string>();
            map<String,String> map_Id_AccountId = new map<String,String>(); 
            
            map<string, set<string>> mapMemberIntAbt = new map<string, set<string>>(); 
            mapInteractAboutAccount = new map<string, string>();
            
            for(InteractingAccount_DTO_HUM intAcc :listMemberIntAcc){
             if(intAcc.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWN_MEMBER_MEDICAIDID))  intAcc.IDType = GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM;
                // all member types - MedicareID, SSN, Medicaid, Enterprise,member id              
                if(mapMemberIDValues.containsKey(intAcc.IdType))
                { mapMemberIDValues.get(intAcc.IdType).add(intAcc.ID.toUppercase());} 
                else 
                { mapMemberIDValues.put(intAcc.IdType,new set<string>{intAcc.ID.toUppercase()});}
            }
            
            if(mapMemberIDValues !=null){
                mapIDValue.put(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM, mapMemberIDValues);            
                mapInquireAboutCriteria.putAll(mapIDValue);          
                mapMemberDependentCode.putAll(getMapMemberDependentCode(listMemberIntAcc,sIntercatingType));
                
            }
            
            if(mapInquireAboutCriteria != null && !mapInquireAboutCriteria.isEmpty())
            {                   
                if(mapInquireAboutCriteria.containsKey(RECORDTYPE_MEMBER))
                {
                    mapMemberIntAbt.putAll(mapInquireAboutCriteria.get(RECORDTYPE_MEMBER)); 
                    
                    if(mapMemberIntAbt.containsKey(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM)) 
                    {              
                        mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.createMapAcctIDValuesByType(mapMemberIntAbt));
                    }  
                }
            }
            
            if(mapMemberIntAbt!=null && !mapMemberIntAbt.isEmpty()) 
            {  
                list<Member_Id__c> lstMemberRecs = CaseInteractionSearch_H_HUM.locateMembers(mapMemberIntAbt, null, mapMemberDependentCode); 
                
                if(!lstMemberRecs.isEmpty()){
                    //if dependent code is not passed and Id type is member-id-base
                    if((mapMemberDependentCode == null ||  mapMemberDependentCode.isEmpty()) && GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM.equalsIgnoreCase(listMemberIntAcc[0].IDType)){
                        mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecordsWithName(lstMemberRecs, mapMemberIntAbt, sIntercatingType,listMemberIntAcc));
                    }else{
                   	 	mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecords(lstMemberRecs, mapMemberIntAbt,mapMemberDependentCode, sIntercatingType));
                    }
                }  
                
                if(mapInteractAboutAccount !=null && !mapInteractAboutAccount.isEmpty()){
                    map_Id_AccountId.putAll(mapInteractAboutAccount);
                } else{
                    // when ID dont present in CRM.                   
                    map_Id_AccountId = new map<String,String>();
                    mapIntWithAboutUnknownCriteria.putall(createUnknownAccWrapper(mapInteractAboutAccount, listMemberIntAcc,sIntercatingType));               
                    if(mapIntWithAboutUnknownCriteria != null) mapUnknownmemberdetails = getUknownMemberMaps(mapIntWithAboutUnknownCriteria,sIntercatingType);                  
                    if(mapUnknownmemberdetails.containskey(sIntercatingType)) mapInteractAboutAccount.putAll(mapUnknownmemberdetails.get(sIntercatingType));
                    
                    map_Id_AccountId.putAll(mapInteractAboutAccount);
                }                  
            }  
            
            return map_Id_AccountId;
        }catch(Exception ex){
            throw ex;
        }
    }
    
    public static map<String,String> getProviderRecordId( List<InteractingAccount_DTO_HUM> listProviderIntAcc,String sIntercatingType){
        try{
            mapInteractAboutAccount = new map<string, string>();
            map<String,String> map_Id_AccountId = new map<String,String>();
            for(InteractingAccount_DTO_HUM intercatingAcc : listProviderIntAcc){
                if(intercatingAcc.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE) || intercatingAcc.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID) || intercatingAcc.IDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TAXIDTAG_PROVIDERSEARCH_HUM)){
                    getProviderAccountMap(intercatingAcc,sIntercatingType);
                } 
                
            }
            
            if( (accountMapToUpsert!=null && !accountMapToUpsert.isEmpty()) || (unknownAccountMapToUpsert!=null && !unknownAccountMapToUpsert.isEmpty()))
            {
                mapProviderDetails.putAll(calltoUpsertAccounts());               
            }
            //known provider
            if(mapProviderDetails !=null && mapProviderDetails.containskey(sIntercatingType)){
                mapInteractAboutAccount.putAll(mapProviderDetails.get(sIntercatingType));
            } 
            else if(mapunknownProvider !=null){
                mapInteractAboutAccount.putAll(mapunknownProvider);
            }
            return mapInteractAboutAccount;
        }catch(Exception ex){
            throw ex;
        }
        
    }
    /*
* getInteractionWrapObj
* <p>
*  Method is used to get values of particular Interaction type.
* @param    CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO  interactionWithAbout, string interactingType
* @return   CaseServiceRestInteraction_DTO_HUM
*/ 
    public static CaseServiceRestInteraction_DTO_HUM getInteractionWrapObj(InteractingAccount_DTO_HUM interactingAcc,String sIntercatingType)
    {
        CaseServiceRestInteraction_DTO_HUM  interactionWrapObj =new CaseServiceRestInteraction_DTO_HUM();              
        interactionWrapObj.sIntID = interactingAcc.ID;
        interactionWrapObj.sIntIDType = interactingAcc.IDType;
        interactionWrapObj.sIntType = interactingAcc.EntityType;
        interactionWrapObj.sAccountName = interactingAcc.AccountName;
        interactionWrapObj.sFirstName = interactingAcc.FirstName;
        interactionWrapObj.sLastname = interactingAcc.LastName;
        interactionWrapObj.sBillingState = interactingAcc.State;
        interactionWrapObj.sStateCode = CaseInteractionSearch_H_HUM.evaluateStateCode(interactingAcc.State);
        interactionWrapObj.sProviderClassification = interactingAcc.ProviderClassification;     
        interactionWrapObj.sInteractingZipCode = interactingAcc.ZipCode;
        interactionWrapObj.sInteractingHomePhone = interactingAcc.HomePhone;
        interactionWrapObj.sInteractingEmailID = interactingAcc.EmailID;
        interactionWrapObj.sPlatformCode = interactingAcc.PlatformCode;
        if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_TAXID)
        {
            interactionWrapObj.sTaxId = interactingAcc.ID;
        }
        else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_NPIID)
        {                           
            interactionWrapObj.sNPIID = interactingAcc.ID;
        }
        else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.INTERACTIONTYPE_AGENTID)
        {
            interactionWrapObj.sAgentId = interactingAcc.ID;
        }
        else if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID)
        {
            interactionWrapObj.sGroupID = interactingAcc.ID;
            
        }
        return interactionWrapObj;
    }
    
    /*
* getProviderAccountMap
* <p>
* This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
* Accounts in the final results Map. 
* @param    CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO ointeractionwithAbt, string interactingType
* @return   Void                  
*/
    public static void getProviderAccountMap(InteractingAccount_DTO_HUM interactingAcc,String sIntercatingType)
    {
        
        try
        {
            CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM();
            interactionWrapObj = getInteractionWrapObj(interactingAcc, sIntercatingType);
            
            accountMapToUpsertProv = new Map<String,Account>();
            
            if(interactionWrapObj.sIntIDType != GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)
            {
                if(accountMapToUpsert.containsKey(sIntercatingType))
                {
                    accountMapToUpsertProv = new CaseServiceProvider_H_HUM().searchInEpassService(interactionWrapObj,sIntercatingType);
                    
                    if(!accountMapToUpsertProv.isEmpty()) 
                    { 
                        accountMapToUpsert.get(sIntercatingType).add(accountMapToUpsertProv.values());
                    }
                    
                }
                else
                {
                    List<Account> lstAccount = new List<Account>();
                    accountMapToUpsertProv.putAll( new CaseServiceProvider_H_HUM().searchInEpassService(interactionWrapObj,sIntercatingType));
                    
                    if(accountMapToUpsertProv.get(sIntercatingType)!=null)
                    {
                        lstAccount.add(accountMapToUpsertProv.get(sIntercatingType));
                        accountMapToUpsert.put(sIntercatingType,lstAccount);
                        
                    }
                }
            }
            
            if(interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE||accountMapToUpsert.isEmpty()||
               (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(sIntercatingType)==null)||
               (CaseServiceProvider_H_HUM.bisMultipleAcct!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(sIntercatingType)!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(sIntercatingType)))
            {
                prepareUnknownAccts(interactionWrapObj,sIntercatingType);    
            }
            
            if(providerAcct!=null && !providerAcct.isEmpty()) {
                upsert providerAcct;
                mapunknownProvider.put(sIntercatingType,providerAcct[0].id);          
            }            
        }
        catch(Exception ex){
            throw ex;
        }        
    } 
    
    /*
* prepareUnknownAccts
* <p>
* This method is used to prepare the unknown accounts for those service did not return response/internal accounts does not exist 
* @param    CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType
* @return   Void                  
*/
    private static void prepareUnknownAccts(CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType)
    {
        
        Account existingAccountId;
        Boolean bunknwnProvider = false;
        if(interactionWrapObj.sIntIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE) && String.isBlank(interactionWrapObj.sIntID))
        {
            Account oAcct = new Account();
            bunknwnProvider = true;
            oAcct = CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj);
            providerAcct.add(oAcct);         
        }
        if(String.isNotBlank(interactionWrapObj.sIntID))
        {
            //Moving all wrapper object for Provider search to pass in EPSS service
            existingAccountId=CaseInteractionSearch_H_HUM.locateProviderByParams(interactionWrapObj);         
        }
        Boolean isError = false;
        if(!bunknwnProvider && ((string.isBlank(interactionWrapObj.sIntID) && (interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE ))||
                                accountMapToUpsert.isEmpty()||
                                (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null)||
                                (CaseServiceProvider_H_HUM.bisMultipleAcct!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType))))
        {           
            if(unknownAccountMapToUpsert.containsKey(interactingType))
            {
                unknownAccountMapToUpsert.get(interactingType).add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                mapProviderId.put(iProviderKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
            else
            {
                List<Account> lstAccount = new List<Account>();
                lstAccount.add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                unknownAccountMapToUpsert.put(interactingType,lstAccount);
                mapProviderId.put(iProviderKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
                
            }
        }
        else
        {
            isError = true;
        }  
        iProviderKey++;
        
    }
    
    /*
* calltoUpsertAccounts
* <p>
* This method is used to upsert accounts
* @param   
* @return   Map<String,Map<String,String>> key is Interacting With/About and value will be(Key(Tax/Npi Id) value(salesforce account id))
*/
    public static Map<String,Map<String,String>> calltoUpsertAccounts()
    {
        try
        {
            Map<String,List<Account>> mapresultAccount = new Map<String,List<Account>>();
            Map<String,Map<String,String>> mapFinalProviderData = new Map<String,Map<String,String>>();
            
            if(accountMapToUpsert!=null && !accountMapToUpsert.isEmpty())
            {
                mapresultAccount = CaseInteractionSearch_H_HUM.upsertAccountRec(accountMapToUpsert);
                
                if(mapresultAccount!=null && !mapresultAccount.isEmpty())
                {
                    CaseInteractionSearch_H_HUM.upsertconsumerIds(accountMapToUpsert); 
                }
            } 
            Map<String,Map<String,String>> mapUnknownAccts = new Map<String,Map<String,String>>();
            if(unknownAccountMapToUpsert!=null && !unknownAccountMapToUpsert.isEmpty())
            {
                mapUnknownAccts = CaseInteractionSearch_H_HUM.insertUnknownAccounts(mapProviderId,unknownAccountMapToUpsert); 
                
            }
            mapFinalProviderData = CaseInteractionSearch_H_HUM.prepareFinalMap(mapUnknownAccts,mapresultAccount);
            
            return mapFinalProviderData;
        }
        catch(Exception ex){
            throw ex;
        }
    }
    
    //memeber
    /**
* getMapMemberDependentCode
* <p>
* Method is used to create Map for Inquire about and Inquire With criteria Member Dependent code
* @param     list<CreateInteractionRequest_DTO_HUM.EntityIdentifierDTO> request
* @return    map<string, map<string,set<string>>> map of interacting type to the member id and dependent code values                    
*/     
    @TestVisible
    public static map<string, map<string,set<string>>> getMapMemberDependentCode(list<InteractingAccount_DTO_HUM> request, string sIntType)
    { 
        
        map<string,set<string>> mapDependentCode = new map<string, set<string>>();
        map<string, map<string, set<string>>> mapMemberDependentCode = new map<string, map<string, set<string>>>();
        for(InteractingAccount_DTO_HUM intWithAboutDTO : request)
        {
            
            if(intWithAboutDTO != null && string.isNotBlank(intWithAboutDTO.EntityType) && String.isNotBlank(intWithAboutDTO.ID) && string.isNotBlank(intWithAboutDTO.IDType))                
            { 
                // memberID__C object population check and Checking for IdValueType as'Member-Id-Base'
                if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) && (intWithAboutDTO.IdType.equals(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM)))
                {
                    if(string.isNotBlank(intWithAboutDTO.MemberIdDependentCode)) // map of membr id base, member dependent code
                    {
                        
                        if(mapDependentCode.containsKey(intWithAboutDTO.ID.toUppercase())) 
                        {    mapDependentCode.get(intWithAboutDTO.ID.toUppercase()).add(intWithAboutDTO.MemberIdDependentCode); 
                         
                        }
                        
                        else
                        {    mapDependentCode.put(intWithAboutDTO.ID.toUppercase(),new set<string>{intWithAboutDTO.MemberIdDependentCode});    }
                    } 
                    
                }           
            }   
        }
        if(mapDependentCode!=null && !mapDependentCode.isEmpty())
        {
            mapMemberDependentCode.put(sIntType, mapDependentCode);
        }
        
        return mapMemberDependentCode;        
    }
    
    /*
* createUnknownAccWrapper
* <p>
* This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
* Accounts in the final results Map. 
* @param    lstAccountRecs(List of Accounts), map<string,set<string>>(Input Interacting About/With values),sIntType (Interaction type)   
* @return   Void                  
*/
    @TestVisible
    public static map<string,List<InteractingAccount_DTO_HUM>> createUnknownAccWrapper(map<string, string> mapIntWithAbtAccount,list<InteractingAccount_DTO_HUM> lstIntWithAbt, String sIntType )
    {
        map<string,List<InteractingAccount_DTO_HUM>> mapIntWithAboutUnknownCriteria = new map<string,List<InteractingAccount_DTO_HUM>>();
        for(InteractingAccount_DTO_HUM oIntWithAbt: lstIntWithAbt)
        {
            if(!mapIntWithAbtAccount.containsKey(oIntWithAbt.ID))
            {
                if(oIntWithAbt.entityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
                {
                    if(mapIntWithAboutUnknownCriteria.containsKey(sIntType))
                    {mapIntWithAboutUnknownCriteria.get(sIntType).add(oIntWithAbt);}
                    else{mapIntWithAboutUnknownCriteria.put(sIntType,new list<InteractingAccount_DTO_HUM>{oIntWithAbt});}
                }
                //watson
                if(oIntWithAbt.entityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM ))
                {
                    if(mapIntWithAboutUnknownCriteria.containsKey(sIntType))
                    {mapIntWithAboutUnknownCriteria.get(sIntType).add(oIntWithAbt);}
                    else{mapIntWithAboutUnknownCriteria.put(sIntType,new list<InteractingAccount_DTO_HUM>{oIntWithAbt});}
                }
            }
        }
        return mapIntWithAboutUnknownCriteria ;
    }
    
    /*
* Method name : getUknownMemberMaps 
* Description :Thi method is to call create unknown member accounts method and return the final map
* Return Type : map<String,map<String,String>>
* Parameter   : map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>>
*/
    Public static map<String,map<String,String>> getUknownMemberMaps(map<string,List<InteractingAccount_DTO_HUM>> mapIntWithAboutUnknownCriteria,String sInteractingType)
    {
        
        map<String,String> unknownAccountWithMap = new map<String,String>();
        map<String,String> unknownAccountAboutMap = new map<String,String>();//final map returning for unknown account creation
        map<String,map<String,String>> FinalUnknownAccountMap = new Map<String,map<String,String>>();
        list<InteractingAccount_DTO_HUM> InteractionAboutvaluesforUnknown = new list<InteractingAccount_DTO_HUM>();
        if(!mapIntWithAboutUnknownCriteria.isEmpty()&& mapIntWithAboutUnknownCriteria.containsKey(sInteractingType))
        {   
            InteractionAboutvaluesforUnknown.addAll(mapIntWithAboutUnknownCriteria.get(sInteractingType));
            unknownAccountAboutMap = createUnknownAccounts (InteractionAboutvaluesforUnknown,sInteractingType);
        }
        if(unknownAccountWithMap!= null) FinalUnknownAccountMap.put(sInteractingType,unknownAccountAboutMap); 
        
        return FinalUnknownAccountMap; 
    }
    
    /*
* Method name : createUnknownAccounts 
* Description :Thi method is to create unknown member accounts
* Return Type :map<String,String>
* Parameter   : list<InteractingAccount_DTO_HUM> , interactingType
*/ 
    Private static map<String,String> createUnknownAccounts (list<InteractingAccount_DTO_HUM> request, string sInteractingType)
    {
        map<String,String> mapUnknownmemAccts = new map<String,String>();   
        ID recordTypeID = mapRecordtypeInfo.containsKey('UM') ? HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),mapRecordtypeInfo.get('UM')) : null;
        ID GrouprecordTypeID = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Unknown Group').getRecordTypeId();
        List<Account> lstunknwnAccFrmReq = new List<Account>();
        list<CreateUnknownMembers_DTO_HUM> lstunknownmemberDTO = new list<CreateUnknownMembers_DTO_HUM>();
        list<Account> lstunknwnAccounts= new list<Account>();
        Schema.sObjectField schemaExternalId = Account.Account_External_ID__c;
        //looping through the list of wrapper ,checking entity type and creating  account
        for(InteractingAccount_DTO_HUM intWithAboutDTO : request)
        {
            Account oAccount = new Account();
            if (Schema.sObjectType.Account.fields.recordTypeID.isCreateable() || Schema.sObjectType.Account.fields.recordTypeID.isUpdateable())
                oAccount.recordTypeID=recordTypeID;
            String interactingId = '';
            String interactingIdType = '';  
            if(intWithAboutDTO != null && string.isNotBlank(intWithAboutDTO.EntityType) && string.isNotBlank(intWithAboutDTO.IDType) 
               && (intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM) || (intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM))) )               
            {            
                if(intWithAboutDTO.EntityType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM)) oAccount.recordTypeID=GrouprecordTypeID; 
                interactingId = intWithAboutDTO.ID;
                if (Schema.sObjectType.Account.fields.firstName.isCreateable() || Schema.sObjectType.Account.fields.firstName.isUpdateable())
                     oAccount.firstName = intWithAboutDTO.FirstName;
                if (Schema.sObjectType.Account.fields.lastName.isCreateable() || Schema.sObjectType.Account.fields.lastName.isUpdateable())
                    oAccount.lastName = intWithAboutDTO.LastName;
                if (Schema.sObjectType.Account.fields.personmailingstreet.isCreateable() || Schema.sObjectType.Account.fields.personmailingstreet.isUpdateable())
                    oAccount.personmailingstreet = intWithAboutDTO.AddressLine1;
                if (Schema.sObjectType.Account.fields.BirthDate__c.isCreateable() || Schema.sObjectType.Account.fields.BirthDate__c.isUpdateable())
                    oAccount.BirthDate__c = String.isNotBlank(intWithAboutDTO.DOB)?HUMUtilityHelper.convertDateformat(intWithAboutDTO.DOB):'';
                if (Schema.sObjectType.Account.fields.personmailingcity.isCreateable() || Schema.sObjectType.Account.fields.personmailingcity.isUpdateable())
                    oAccount.personmailingcity = intWithAboutDTO.City;
                if (Schema.sObjectType.Account.fields.PersonMailingStateCode.isCreateable() || Schema.sObjectType.Account.fields.PersonMailingStateCode.isUpdateable())
                    oAccount.PersonMailingStateCode = intWithAboutDTO.State;
                if (Schema.sObjectType.Account.fields.personmailingpostalcode.isCreateable() || Schema.sObjectType.Account.fields.personmailingpostalcode.isUpdateable())
                    oAccount.personmailingpostalcode = intWithAboutDTO.ZipCode;
                if (Schema.sObjectType.Account.fields.General_Account__c.isCreateable() || Schema.sObjectType.Account.fields.General_Account__c.isUpdateable())
                    oAccount.General_Account__c = True;
                if (Schema.sObjectType.Account.fields.Tenant_Id__c.isCreateable() || Schema.sObjectType.Account.fields.Tenant_Id__c.isUpdateable())
                    oAccount.Tenant_Id__c = '00';
                if (Schema.sObjectType.Account.fields.ETL_Record_Deleted__c.isCreateable() || Schema.sObjectType.Account.fields.ETL_Record_Deleted__c.isUpdateable())
                    oAccount.ETL_Record_Deleted__c = false;
                interactingIdType = intWithAboutDTO.IDType;
                String accountExternalId = '';
               
                if(interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM && !String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SSN + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                        
                }
                else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM && !String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICARE + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase()); 
                }
                else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM && !String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_ENTID + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                
                }
                else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM && !String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEMBERIDBASE + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                
                }
                else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM && !String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICAID + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());                
                }                    
                else if (!String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_UKEY + HUMUtilityHelper.getMD5Hash(interactingId.toUppercase());
                }                
                else if (interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM ) 
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC;                       
                }
                else if ((interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM) && String.isBlank(interactingId))
                {
                    accountExternalId = '';                       
                }
                if (Schema.sObjectType.Account.fields.Account_External_ID__c.isCreateable() || Schema.sObjectType.Account.fields.Account_External_ID__c.isUpdateable())
                    oAccount.Account_External_ID__c = accountExternalId ;
                boolean bAcctExist = false;
                if(accountExternalId != GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC && String.isNotBlank(accountExternalId))
                {
                    String queryAcct = 'Select Id from Account where Account_External_ID__c = \'' + string.escapeSingleQuotes(accountExternalId ) + '\' LIMIT 1';
                    List<Account> objAcct = (List<Account>)Database.Query(queryAcct);
                    if(!objAcct.isEmpty() && objAcct[0].Id != null && String.isNotBlank(objAcct[0].Id))
                    { 
                        CreateUnknownMembers_DTO_HUM unknownmemberDTO = new CreateUnknownMembers_DTO_HUM();   
                        unknownmemberDTO.MemberInteractingId = interactingId;
                        unknownmemberDTO.AccId = objAcct[0].Id;
                        unknownmemberDTO.MemberInteractingIdType = interactingIdType;
                        unknownmemberDTO.AccntextrnlID = accountExternalId ;
                        lstunknownmemberDTO.add(unknownmemberDTO);
                        mapUnknownmemAccts.put(interactingId,objAcct[0].Id);
                        bAcctExist = true;
                    }
                }
                if (!bAcctExist)
                {   
                    CreateUnknownMembers_DTO_HUM unknownmemberDTO = new CreateUnknownMembers_DTO_HUM();
                    lstunknwnAccounts.add(oAccount);
                    if(string.isNotBlank(interactingId) && !interactingIdType.equalsIgnoreCase('UnknownMember'))
                    {
                        unknownmemberDTO.MemberInteractingId = interactingId;
                        unknownmemberDTO.MemberInteractingIdType = interactingIdType;
                        unknownmemberDTO.AccntextrnlID = accountExternalId ;
                        lstunknownmemberDTO.add(unknownmemberDTO);
                    }
                }
            }
        }
        
        Database.upsertResult[] result = Database.upsert(lstunknwnAccounts,false); 
        
        if(lstunknwnAccounts !=null && !lstunknwnAccounts.isEmpty())
        {
            Integer iCount=0;
            for(Account accountRec : lstunknwnAccounts)
            {
                if(String.isBlank(accountRec.Account_External_Id__c) && accountRec.id!=null) 
                {
                    accountRec.Account_External_Id__c = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC + accountRec.id;
                    lstunknwnAccFrmReq.add(accountRec); 
                    lstAcctId.add(accountRec.id);
                    lstAccounttId.add(accountRec); //prod fix
                    if(mapUnknwnMemberFrmService.get(sInteractingType) != null &&!mapUnknwnMemberFrmService.get(sInteractingType).isEmpty())
                        mapUnknwnMemberFrmService.get(sInteractingType).put('UnknownMbr'+iCount,accountRec.Id); 
                    else {
                        mapUnknwnMemberFrmService.put(sInteractingType,new map<string,string>{'UnknownMbr'+iCount=>accountRec.Id});
                    }
                }
            }
        }
        if(lstunknwnAccFrmReq != null && !lstunknwnAccFrmReq.isEmpty())
        {
            list<Database.saveResult> saveResult = Database.update(lstunknwnAccFrmReq, false);
        }
        for(Account oAcc : lstunknwnAccounts)
        {
            for (CreateUnknownMembers_DTO_HUM unknownDTOlst : lstunknownmemberDTO)
            {
                if (unknownDTOlst.AccntextrnlID  == oAcc.Account_External_ID__c) 
                    unknownDTOlst.AccId = oAcc.Id  ;
                mapUnknownmemAccts.put(unknownDTOlst.MemberInteractingId,unknownDTOlst.AccId);
            }
        }
        if(lstunknownmemberDTO!=null)
            insertConsumerTaxIds(lstunknownmemberDTO);
        return mapUnknownmemAccts;
        
    } 
    
    /*
* Method Name   :    insertConsumerTaxIds
* Description   :    Insert the Consumer TAXIDs based on the Account ID
* Return Type   :    List<ConsumerId__c>
* Parameters    :    list<CreateUnknownMembers_DTO_HUM>
*/
    @testVisible 
    private static void insertConsumerTaxIds(list<CreateUnknownMembers_DTO_HUM> insertconsumerlst)
    {   
        try
        {
            Boolean bAsynchProcess = false;
            list<ConsumerId__c> objConsumerlst = new list<ConsumerId__c>();
            for(CreateUnknownMembers_DTO_HUM consumerlstDTO: insertconsumerlst)
            {
                ConsumerId__c objConsumer = new ConsumerId__c();
                String accountExternalId = HUMConstants__c.getInstance('UNKNOWN_MEMBER_EXTID').Stringvalue__c;
                if(String.isNotBlank(consumerlstDTO.MemberInteractingIdType) && consumerlstDTO.MemberInteractingIdType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))
                {
                    consumerlstDTO.MemberInteractingIdType = GLOBAL_CONSTANT_HUM.CASESERVICE_HUMANAID_HUM;
                }
                if (Schema.sObjectType.ConsumerId__c.fields.Consumer_Id__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.Consumer_Id__c.isUpdateable())
                    objConsumer.Consumer_Id__c = consumerlstDTO.MemberInteractingId.toUppercase();
                if (Schema.sObjectType.ConsumerId__c.fields.ID_Type__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.ID_Type__c.isUpdateable())
                    objConsumer.ID_Type__c = consumerlstDTO.MemberInteractingIdType;
                if (Schema.sObjectType.ConsumerId__c.fields.Account__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.Account__c.isUpdateable())
                    objConsumer.Account__c = consumerlstDTO.AccId;
                String sPipeCharacter = HUMConstants__c.getInstance('PIPE_CHARACTER').StringValue__c;
                String externalId = '';
                if(consumerlstDTO.MemberInteractingIdType== GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM)
                {
                    externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_SSN + consumerlstDTO.AccId;
                }
                else if(consumerlstDTO.MemberInteractingIdType== GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM)
                {
                    externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICARE + consumerlstDTO.AccId;
                }
                else if(consumerlstDTO.MemberInteractingIdType== GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM)
                {
                    externalId = GLOBAL_CONSTANT_HUM.CONSUMER_EXTERNALID_MEDICAID + consumerlstDTO.AccId;
                }
                else
                {
                    externalId = accountExternalId + consumerlstDTO.MemberInteractingIdType+ sPipeCharacter + consumerlstDTO.AccId;   
                }
                if (Schema.sObjectType.ConsumerId__c.fields.Consumer_External_Id__c.isCreateable() || Schema.sObjectType.ConsumerId__c.fields.Consumer_External_Id__c.isUpdateable())
                    objConsumer.Consumer_External_Id__c = externalId;                                     
                objConsumerlst.add(objConsumer);
            }
            Schema.sObjectField schemaExternalId = ConsumerId__c.Consumer_External_Id__c;
            Database.upsertResult[] consumerResult = Database.upsert(objConsumerlst,schemaExternalId, false);
            
            for (Database.upsertResult ur : consumerResult )
            {
                if(!ur.isSuccess())
                {
                    if(bAsynchProcess)
                    {
                        CreateCaseRest_D_HUM.logError(ur.getErrors(), 'insertConsumerTaxIds', 'ConsumerID__c', ur.getId());
                    }
                    else
                    {
                        throw new HUMCustomException('Issue with Unknown Member ID Update in Consumer');
                    }
                } 
            }
        }
        catch(Exception ex){
            throw ex;
        }
    } 
    
    /*
* getGroupAccountMap
* <p>
* This method is used to match the Accounts retrieved against the input Interacting About and Interaction with values. Also populate the matching 
* Accounts in the final results Map. 
* @param    InteractingAccount_DTO_HUM ointeractionAcc, string interactingType
* @return   Void                  
*/
    public static void getGroupAccountMap(InteractingAccount_DTO_HUM ointeractionAcc, string interactingType)
    {
        try
        {
            CaseServiceRestInteraction_DTO_HUM interactionWrapObj  = new CaseServiceRestInteraction_DTO_HUM();
            interactionWrapObj = getInteractionWrapObj(ointeractionAcc, interactingType);
            accountMapToUpsertGroup = new Map<String,Account>();
            
            if(interactionWrapObj.sIntIDType != GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE)
            {
                if(groupaccountMapToUpsert.containsKey(interactingType))
                {
                    
                    accountMapToUpsertGroup = new CaseServiceGroup_H_HUM().queryGroupAccount(interactionWrapObj,interactingType);
                    if(!accountMapToUpsertGroup.isEmpty()) 
                    { 
                        
                        groupaccountMapToUpsert.get(interactingType).add(accountMapToUpsertGroup.values());
                    }
                }
                else
                {
                    
                    List<Account> lstAccount = new List<Account>();
                    accountMapToUpsertGroup.putAll( new CaseServiceGroup_H_HUM().queryGroupAccount(interactionWrapObj,interactingType));
                    if(CaseServiceGroup_H_HUM.bIsGroupService == true) blninsertunknowngrp = false;
                    if(accountMapToUpsertGroup.get(interactingType)!=null)
                    {                        
                        lstAccount.add(accountMapToUpsertGroup.get(interactingType));
                        groupaccountMapToUpsert.put(interactingType,lstAccount);
                        
                    }
                }
            }
            
            if( groupaccountMapToUpsert.isEmpty()||
               (!groupaccountMapToUpsert.isEmpty() && groupaccountMapToUpsert.get(interactingType)==null)||
               (CaseServiceGroup_H_HUM.bisMultipleAcct!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType)))
            {
                
                prepareUnknownGroupAccts(interactionWrapObj,interactingType);    
            }
            
            if(GroupAcct !=null && !GroupAcct.isEmpty()) {
                upsert GroupAcct;
                mapunknownGroup.put(interactingType,GroupAcct[0].id);
            }
        }
        catch(Exception ex){
            throw ex;
        }
    }
    /*
* prepareUnknownGroupAccts
* <p>
* This method is used to prepare the unknown accounts for those service did not return response/internal accounts does not exist 
* @param    CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType
* @return   Void                  
*/
    private static void prepareUnknownGroupAccts(CaseServiceRestInteraction_DTO_HUM interactionWrapObj,String interactingType)
    {
        
        Account existingAccountId;
        Boolean bunknwnGroup = false;
        if(interactionWrapObj.sIntIDType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPEID) && String.isBlank(interactionWrapObj.sIntID))
        {
            Account oAcct = new Account();
            bunknwnGroup = true;            
            oAcct = CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj);            
            GroupAcct.add(oAcct);            
        }
        if(String.isNotBlank(interactionWrapObj.sIntID))
        {
            //Moving all wrapper object for Provider search to pass in EPSS service
            existingAccountId=CaseInteractionSearch_H_HUM.locateProviderByParams(interactionWrapObj);
        }
        Boolean isError = false;
        if(!bunknwnGroup && ((string.isBlank(interactionWrapObj.sIntID) && (interactionWrapObj.sIntIDType == GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE))||
                             accountMapToUpsert.isEmpty()||
                             (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null)||
                             (CaseServiceGroup_H_HUM.bisMultipleAcct!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceGroup_H_HUM.bisMultipleAcct.get(interactingType))))
        {
            if(unknownAccountMapToUpsert.containsKey(interactingType))
            {
                unknownAccountMapToUpsert.get(interactingType).add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                mapGroupId.put(iGroupKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
            else
            {
                List<Account> lstAccount = new List<Account>();
                lstAccount.add(CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                unknownAccountMapToUpsert.put(interactingType,lstAccount);
                
                mapGroupId.put(iGroupKey,interactionWrapObj.sIntID+'-'+interactingType+'-'+interactionWrapObj.sIntIDType);
            }
        }
        else
        {
            isError = true;
        }  
        iGroupKey++;
    }
    
    /*
* calltoUpsertGroupAccounts
* <p>
* This method is used to upsert accounts
* @param   
* @return   Map<String,Map<String,String>> key is Interacting With/About and value(salesforce account id))
*/
    public static Map<String,Map<String,String>> calltoUpsertGroupAccounts() 
    {
        try
        {
            Map<String,List<Account>> mapGroupresultAccount = new Map<String,List<Account>>();
            Map<String,Map<String,String>> mapFinalGroupData = new Map<String,Map<String,String>>(); 
            Map<String,Map<String,String>> mapUnknownAccts = new Map<String,Map<String,String>>();
            
            if(groupaccountMapToUpsert !=null && !groupaccountMapToUpsert.isEmpty())
            {               
                mapGroupresultAccount = groupaccountMapToUpsert;               
            }            
            
            if(unknownAccountMapToUpsert!=null && !unknownAccountMapToUpsert.isEmpty())
            {
                mapUnknownAccts = CaseInteractionSearch_H_HUM.insertUnknownAccounts(mapGroupId,unknownAccountMapToUpsert); 
            }
            
            mapFinalGroupData = CaseInteractionSearch_H_HUM.prepareFinalMap(mapUnknownAccts,mapGroupresultAccount);
            
            return mapFinalGroupData ;
        }
        catch(Exception ex){
            throw ex;
        }
    }
    
    /*
* getProviderRecordId_V4
* <p>
* This method is used to get provider details from the interaction details
* @param   InteractingAccount_DTO_HUM , String
* @return   map<String,Account>
*/  
    public static map<String,Account> getProviderRecordId_V4(InteractingAccount_DTO_HUM listProviderIntAcc,String sIntercatingType){
        
        map<String,Account> retVal = new map<String,Account>();
        map<String,Account> retMapVal = new map<String,Account>();
        String sReturnValAbout='Interacting_About';
        String sReturnValWith='Interacting_With';
        CaseServiceRestInteraction_DTO_HUM interactionWrapObject  = new CaseServiceRestInteraction_DTO_HUM(); 
        Account oAcct = new Account();
        List<Account> lstAccount = new List<Account>();
        
        interactionWrapObject = getInteractionWrapObj(listProviderIntAcc, sIntercatingType);
        retVal=CaseServiceProvider_v4_H_HUM.getProviderDetails(interactionWrapObject, sIntercatingType);       
        if(retVal.get(sIntercatingType)==null){
            retMapVal.put(sIntercatingType,null);
        }else{
            retMapVal = retVal;
        }       
        
        return retMapVal;
    }
 
        /*
* getAgentRecordId_V4
* <p>
* This method is used to get provider details from the interaction details
* @param   InteractingAccount_DTO_HUM , String
* @return   map<String,Account>
*/  
    public static map<String,Account> getAgentRecordId_V4(InteractingAccount_DTO_HUM listAgentIntAcc,String sIntercatingType){
        
        map<String,Account> retVal = new map<String,Account>();
        map<String,Account> retMapVal = new map<String,Account>();
        String sReturnValAbout='Interacting_About';
        String sReturnValWith='Interacting_With';
        CaseServiceRestInteraction_DTO_HUM interactionWrapObjt  = new CaseServiceRestInteraction_DTO_HUM(); 
        Account oAct = new Account();
        List<Account> lstOfAccount = new List<Account>();
        
        retVal=CaseServiceAgent_v4_H_HUM.getAgentDetails(listAgentIntAcc, sIntercatingType);
        
        if(retVal.get(sIntercatingType)==null){         
            retMapVal.put(sIntercatingType,null);     
        }else{
            retMapVal = retVal;
        }
        return retMapVal;
    } 
}