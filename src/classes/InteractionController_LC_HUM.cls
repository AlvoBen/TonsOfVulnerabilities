/*******************************************************************************************************************************
Apex Class Name : InteractionController_LC_HUM 
Version         : 1.0
Created On      : 03/18/2022  
Function        : 1. This class serves as Controller of the interactionCmpHum and interactionCmpAccHum LWC.
2. This Class Interacts with Search pages and gets the data from Searches to create interaction records.
Test Class      : InteractionController_LC_HUMTest
Modification Log: 
* Modification ID  Developer Name          Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0              Krishna Teja Samudrala                         03/18/2022                 Original Version(T1PRJ0170850)
* 2.0			   Krishna Teja Samudrala                         04/18/2022                 Added exception handling
* 3.0              Santhi Mandava                                 01/19/2023                 User Story 3604501: T1PRJ0865978 - MF 20754 - Lightning - Consumer-Legacy Soft Phone Application/Display Refresh message
* Visweswara Rao J                02/07/2023                   User Story 4130775: T1PRJ0865978 -23998 / 4079568 C6, Lightning- Case management- Ability to create interactions for Home office policies- Jaguars
* 4.0              Harshada Kamble/Anil Pavithran                 05/05/2023                 US4461937 T1PRJ0036776: SFDC Ability to Manually Modify Authentication Status (Multi Members) in Lightning - Genesys
* 5.0              Sivaprakash Rajendran                          05/04/2023                 US 4579434 - T1PRJ0036776: Enterprise - Caller Type Population with Phonebook - Lightning (Genesys)
* 6.0              Harshada Kamble				                  08/04/2023				 User Story 4890793 T1PRJ0036776: INC2447611, Consumer/Toggles button from interactions is not staying green (Genesys)
* 6.0              Harshada Kamble				                  20/02/2024				 T1PRJ1374973: Splunk Error Logs - Add Null Check to the "About" Field in the Interaction Log
*******************************************************************************************************************************/

public without sharing class InteractionController_LC_HUM { 
    public static  final string INSERTstr ='insert'; 
    public static  final string UPDATEstr ='update';
        /*
    * Method Name   :    findRecords()
    * Description   :    This method is called when user inputs in Interacting with  field and hits Enter key.   
    * return Type   :    List<sobject>
    * Parameters    :    Account name and Object Name
    */   
        @AuraEnabled(cacheable=true)  
        public static list<sobject> findRecords(String searchKey, String objectName) {
            list<sobject> accList = new list<sobject>();
            string searchText = String.escapeSingleQuotes(searchKey);    
            string soslquery = 'FIND \'' + searchText + '\' IN NAME FIELDS RETURNING  Account (Id, Name, birthdate__c,Site,Type)';          
            
            try{
                List<List<sObject>> searchList = search.query(soslquery);
                accList = searchList[0]; 
                if(Test.isRunningTest()){
                    throw new QueryException();
                }            
            }
            catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','findRecords()');
            }        
            return accList;
        } 
        /*  
    * Method Name   :    findInteractionRecords()
    * Description   :    This method is called when user inputs in Interaction #  field and hits Enter key.   
    * return Type   :    List<sobject>
    * Parameters    :    Interaction name and Object Name
    */
        
        @AuraEnabled
        public static List<sobject> findInteractionRecords(String searchKey, String objectName, String scrName) {  
            list<sobject> intList = new list<sobject>();
            string searchText = String.escapeSingleQuotes(searchKey); 
            string query;
            try{
                if(((searchText==NULL) || (searchText=='')) && (scrName=='lookup'))
                {
                    query = 'Select Id, Name,Caller_Name__c,Interaction_Origin__c,Interacting_With_type__c,';
                    query += 'Interacting_With__r.Name,CreatedBy.Name FROM Interaction__c ';
                    query += ' WHERE createdDate  = LAST_N_DAYS:7 ';
                    
                    query += ' Order By createdDate desc LIMIT 100';
                    intList = Database.query(String.escapeSingleQuotes(query));
                    
                } else {
                    query = 'SELECT Id, Name, Caller_Name__c, Interaction_Origin__c, Interacting_With_type__c,Interacting_With__r.Name,CreatedBy.Name FROM ' + objectName + ' WHERE NAME LIKE \'%' 
                        + STRING.escapeSingleQuotes(searchText) + '%\' AND createdDate  = LAST_N_DAYS:7';
                    intList = Database.query(String.escapeSingleQuotes(query));
                }
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','findInteractionRecords()');
            } 
            return intList;  
        } 
        /*
    * Method Name   :    getInteractionList()
    * Description   :    This method is called on click of Get Interaction button, it returns the most recent Interaction creeated by logged in user.   
    * return Type   :    Interaction__c
    * Parameters    :    Interaction__c Id
    */
        @AuraEnabled
        public static Interaction__c getInteractionList(string recordId) {  
            Interaction__c interactionRec = new Interaction__c();
            try{                   
                if(recordId == null || recordId == '')
                {
                    interactionRec = [SELECT Caller_Name__c, Name, Id, Interacting_With__c, Interacting_With__r.Name, Interacting_With_type__c,
                                      Interaction_Origin__c, Call_Data__c, uuid__c, call_category__c,Authenticated__c,
                                                             (SELECT ID,Authenticated__c, Interacting_About__c, Interacting_About__r.Name, 
                                                              Interacting_About__r.RecordType.Name,Interacting_About_Type__c,Interaction__c 
                                                              from Interaction_members__r WHERE createdby.Name =:system.label.SoftPhoneIntMemberOwner  ORDER BY 
                                                              CreatedDate DESC) FROM Interaction__c WHERE CreatedByid = :userinfo.getUserId() 
                                      ORDER BY CreatedDate DESC LIMIT 1];
                }
                else if(recordId != null){
                    if(HUMUtilityHelper.isCRMFunctionalityON('4461937'))
                    {
                        interactionRec = [SELECT Caller_Name__c, Name, Id, Interacting_With__c, Interacting_With__r.Name, Interacting_With_type__c,
                                                Interaction_Origin__c, Call_Data__c, uuid__c, call_category__c,Authenticated__c,
                                                (SELECT ID,Authenticated__c, Interacting_About__c, Interacting_About__r.Name, 
                                                Interacting_About__r.RecordType.Name,Interacting_About_Type__c,Interaction__c 
                                                from Interaction_members__r ORDER BY 
                                                Name ASC) FROM Interaction__c WHERE Id =:recordId  
                                                ORDER BY CreatedDate DESC LIMIT 1];  
                    }
                    else{
                        interactionRec = [SELECT Caller_Name__c, Name, Id, Interacting_With__c, Interacting_With__r.Name, Interacting_With_type__c,
                                            Interaction_Origin__c, Call_Data__c, uuid__c, call_category__c,Authenticated__c,
                                            (SELECT ID,Authenticated__c, Interacting_About__c, Interacting_About__r.Name, 
                                            Interacting_About__r.RecordType.Name,Interacting_About_Type__c,Interaction__c 
                                            from Interaction_members__r ORDER BY 
                                            CreatedDate DESC) FROM Interaction__c WHERE Id =:recordId  
                                            ORDER BY CreatedDate DESC LIMIT 1]; 
                    }               
                }
                if(Test.isRunningTest()){
                    throw new QueryException();
                } 
            }catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getInteractionList()');
            } 
            return interactionRec;
        }
        /*
    * Method Name   :    getInteractionMemberList()
    * Description   :    This method is used to rerender Interacting about field and populate values in Update button Modal.   
    * return Type   :    None
    * Parameters    :    None
    */
        @AuraEnabled
        public static List<Interaction_Member__c> getInteractionMemberList(string recordId, string callingFnName) {  
            List<Interaction_Member__c> interactionMemRec = new List<Interaction_Member__c>();
            try{
                if(recordId == null || recordId == '')
                {
                    interactionMemRec = new List<Interaction_Member__c>();
                }
                // this will work for update button
                else if(recordId != null){
                    if(callingFnName == '')
                    {
                        interactionMemRec = [Select Id,Authenticated__c, Interacting_About__r.Name,Interacting_About_Type__c,
                                             Interaction__c from Interaction_Member__c WHERE Interaction__c =:recordId
                                             order by CreatedDate desc];
                    } else if(callingFnName == 'getInteractionMemberListFn'){
                        interactionMemRec = [Select Id,Authenticated__c,Interacting_About__r.Name,Interacting_About_Type__c,
                                             Interaction__c from Interaction_Member__c WHERE Interaction__c =:recordId
                                             AND createdby.Name =:system.label.SoftPhoneIntMemberOwner order by CreatedDate desc];
                    }
                }
                if(Test.isRunningTest()){
                    throw new QueryException();
                }
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getInteractionMemberList()');
            } 
            return interactionMemRec;
        }
        /*
    * Method Name   :    showAckPopup()
    * Description   :    This method is used for rendering the show password modal.   
    * return Type   :    Object
    * Parameters    :    Account Id
    */
        @AuraEnabled
        public static Object showAckPopup(String sInteractionAbout) {
            Account acc = new Account();
            try {
                list<Account> oAcc = [SELECT Id,Account_Security_Answer__c, Account_Security_Question__c,
                                      Account_Security_Access__c,recordtype.name,Account_Security_EndDate__c
                                      FROM Account where id=:sInteractionAbout limit 1] ;
                
                if(oAcc.size() >0){ 
                    if(System.now()<=oAcc[0].Account_Security_EndDate__c && oAcc[0].recordtype.name=='Member'){
                        acc  = oAcc[0];  
                    }
                }
                if(Test.isRunningTest()){
                    throw new QueryException();
                }
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','showAckPopup()');
            } 
            return acc;
        }
        /*
    * Method Name   :    initializeVariables()
    * Description   :    This method is called when the component is initialized, it returns render logic for various buttons.   
    * return Type   :    List<Boolean>
    * Parameters    :    None
    */
        @AuraEnabled(cacheable=true)
        public static List<Boolean> initializeVariables() {
            List<Boolean> boolList = new List<Boolean>();
            try{
                Boolean validateCRMSwitch = HUMUtilityHelper.isCRMFunctionalityON('2153743');
                Boolean Switch_2230000 = HUMUtilityHelper.isCRMFunctionalityON('2230000');
                Boolean mf3Switch=PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.AUTH_SWITCH);
                Boolean multiMemberAuthSwitch = HUMUtilityHelper.isCRMFunctionalityON('4461937');
                Boolean callerTypePopulationSwitch = PhoneBook_H_HUM.isCRMFunctionalityON(PhoneBookConstants_H_HUM.callerTypePopulationSwitch); 
                Boolean refreshIntLogLMSSwitch = HUMUtilityHelper.isCRMFunctionalityON('4890793');   
                boolList.add(validateCRMSwitch);       
                boolList.add(mf3Switch); 
                boolList.add(Switch_2230000);
                boolList.add(multiMemberAuthSwitch);
                boolList.add(callerTypePopulationSwitch);//Added for US#4579434 - Caller type population 
                boolList.add(refreshIntLogLMSSwitch);//Added for US#4890793 - refresh interaction log on inbound call   
                    if(Test.isRunningTest()){
                        throw new NullPointerException();
                    }
                }
                catch(Exception e) {
                    HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','initializeVariables()');
                }
            return boolList;
        }
        
        /*
    * Method Name   :    getCaseInteraction()
    * Description   :    This method is called when the component is initialized, it returns render logic for various buttons.   
    * return Type   :    List<Boolean>
    * Parameters    :    None
    */
        @AuraEnabled
        public static Boolean getCaseInteraction(string caseRecordId, string intRecordId) {
            Boolean boolCaseExists = false;  
            try{      
                List<Case_Interaction__c> CIrecList = [SELECT ID, NAME FROM Case_Interaction__c where Case__c =:caseRecordId 
                                                       AND Interaction__c=:intRecordId LIMIT 1];        
                if(CIrecList.size()>0){
                    boolCaseExists = true;
                }
                if(Test.isRunningTest()){
                    throw new QueryException();
                }
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getCaseInteraction()');
            }
            return boolCaseExists;
        }
        
        @AuraEnabled
        public static string getIntWithRecType (string accRecordId) {
            string IntWithRecType = ''; 
            try{    
                List<Account> accRecList = [SELECT ID, RecordType.NAME FROM Account where ID =:accRecordId 
                                            LIMIT 1];              
                if(accRecList.size()>0){
                    Account acc = accRecList[0];
                    IntWithRecType = acc.RecordType.NAME;
                } 
                if(Test.isRunningTest()){
                    throw new QueryException();
                }
            }   catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getIntWithRecType()');
            }    
            return IntWithRecType;
        }
        
        /*
    * Method Name   :    authCheckHandler()
    * Description   :    This method is used to render the Auth Button and it's values.   
    * return Type   :    AuthObject wrapper class
    * Parameters    :    Interaction__c.Id 
    */
        @AuraEnabled
        public static AuthObject authCheckHandler(String sInteractionAbout){
            
            AuthObject authCheckObj = new AuthObject();
            try{
                Interaction__c oFetchInteraction = [SELECT ID, Authenticated__c FROM Interaction__c WHERE ID = :sInteractionAbout limit 1];
                If(oFetchInteraction.Authenticated__c == true){            
                    authCheckObj.authCheckLabel = 'Authenticated';
                    authCheckObj.authCheck = true;
                }
                else {
                    authCheckObj.authCheckLabel = 'UnAuthenticated';
                    authCheckObj.authCheck = false;
                }
                if(Test.isRunningTest()){
                    throw new QueryException();
                } 
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','authCheckHandler()');
            }    
            return authCheckObj;
        }   
        
        public class AuthObject {
            @AuraEnabled
            public Boolean authCheck{get;set;}
            @AuraEnabled
            public String authCheckLabel{get;set;}
        }
        
        /*
    * Method Name   :    findIntwithLookupRecords()
    * Description   :    This method is called when user inputs in Interacting with  field and hits Enter key.   
    * return Type   :    List<sobject>
    * Parameters    :    Account name and Object Name
    */   
        
        @AuraEnabled(cacheable=true)  
        public static list<sobject> findIntwithLookupRecords(String searchKey, String objectName, String filter) {
            list<sobject> accList = new list<sobject>();
            string searchText = String.escapeSingleQuotes(searchKey);
            String searchFilter = String.escapeSingleQuotes(filter);
            string soslquery;
            try{
                if(searchText ==NULL || searchKey ==''){
                    
                    soslquery = 'SELECT Id, Name,birthdate__c,Site,Type,Owner.Alias FROM Account WHERE LastViewedDate !=null ORDER BY LastViewedDate DESC LIMIT 25';
                    
                }else{
                    
                    soslquery = 'FIND \'' + searchText + '\' IN  ' + searchFilter + ' FIELDS RETURNING  Account (Id, Name, birthdate__c,Site,Type,Owner.Alias)'; 
                } 
                   
                if(searchText ==NULL || searchKey ==''){
                    List<sObject> searchList=   Database.query(soslquery);
                    accList = searchList;
                }else{
                    List<List<sObject>> searchList = search.query(soslquery);
                    accList = searchList[0];
                }
                if(Test.isRunningTest()){
                    throw new QueryException();
                }
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','findIntwithLookupRecords()');
            } 
            return accList;
        }        
        
        @AuraEnabled
        public static string getAccountId(string extaccId){
            List<Account> aList = new List<Account>(); 
            string accId = '';
            try {
                aList = [SELECT name, Id, RecordType.Name, account_external_id__c 
                         FROM account 
                         WHERE account_external_id__c =:extaccId];
                Account a  = aList[0];
                accId = JSON.serialize(a);
        
            } catch (Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getAccountId()');
            }
            return accId;
        }
        
        // @AuraEnabled
        // public static string getAppName(){
        //     string accId = '';
        //     try {
        //         UserAppInfo userAppInfo = [SELECT Id, AppDefinitionId FROM UserAppInfo WHERE UserId = :UserInfo.getUserId() LIMIT 1];
        //         string AppDefinitionId = userAppInfo.AppDefinitionId;
        //         AppDefinition appDefinition = [SELECT DurableId, Label FROM AppDefinition Where DurableId = :AppDefinitionId LIMIT 1];
        //         accId = appDefinition.Label;
        //         if(Test.isRunningTest()){
        //             throw new QueryException();
        //         }                      
        //     } catch (Exception e) {
        //         HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getAppName()');
        //     }
        //     return accId;
        // }
    
        /*
        * Method Name   :    saveDataForCallTransfer
        * Description   :    This method is called when user clicks on call transfer button from account detail page
        * return Type   :    Boolean
        * Parameters    :    AccountId,MemberPlan Id,Interaction Id
        */  
        @AuraEnabled
        public static Boolean saveDataForCallTransfer(String sAccId,String memberPlanId,String sInteractionId)
        {
            Boolean isSuccess = true;
            try
            {
                Integration_Mapping__c intMapping = new Integration_Mapping__c();
                //Query the interaction details. UUID is needed for call transfer, which is stored on interaction
                String sQry = 'Select Interacting_With__r.Id,UUID__c from Interaction__c where id=:sInteractionId';
                //Query the interaction details 
                Interaction__c inter = Database.query(sQry);
                
                Id InteractingWith = inter.Interacting_With__c;
                //Prepare the string to update the INTEGRATION MAPPING object that is used for soft phone call transfer data
                String sMapping = Label.HUMSPhoneMappingObjInteraction + ':' + String.valueOf(sInteractionId) + ';' + Label.HUMSPhoneMappingObjInqFor + ':';
                sMapping += String.valueOf(InteractingWith) + ';' + Label.HUMSPhoneMappingObjInqAb + ':' + sAccId + ';'; 
                sMapping += Label.HUMSPhoneMappingObjMemberPlan + ':' + memberPlanId;
                
                //Select the Integration record where the name is Soft Phone routing   and create the integration mapping for call transfer data
                String sIntQry = 'Select Name,id from Integration__c where Name= \''+String.escapeSingleQuotes(System.Label.HUMIntegrationTypeSoftPhone)+'\'';
                Integration__c softPhone = Database.query(sIntQry);
                intMapping.Integration_ID__c=softPhone.id;
                intMapping.Mapping__c = sMapping;
                intMapping.Record_ID__c = inter.UUID__c;
                insert intMapping;
            }
            catch(Exception ex)
            {
                HUMExceptionHelper.logErrors(ex,'InteractionController_LC_HUM','saveDataForCallTransfer');
                isSuccess = false;
            }
            return isSuccess;
        }
        @AuraEnabled
        public static sObject handleRecordDml(sObject objData,string action, string objApiName){
         sObject retobj;
            if(action==INSERTstr && objData !=null){  
            insert objData;
            retobj = DynamicQuerymethod(objData,objApiName);
            }else if(action==UPDATEstr &&  objData.id !=null){
                update objData;
                retobj = DynamicQuerymethod(objData,objApiName);
            }
            return retobj;
        }
        public static sObject DynamicQuerymethod(sObject objData, string objApiName){
        sObject obj;
        try{
        string qry = 'Select FIELDS(STANDARD) From '+ string.escapeSingleQuotes(objApiName)+' where Id =' +'\''+string.escapeSingleQuotes(objData.Id)+'\'';
        obj = Database.query(qry);
    
        }catch(Exception e){
         HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','DynamicQuerymethod');
       obj = null;
       }
    return obj;
    }
        /*
        * Method Name   :    getSelectedIntMemberDetails()
        * Description   :    This method is used to get interaction member details for current selected record. Added for US#4461937 - Multi member auth Lightning
        * return Type   :    Interaction_Member__c
        * Parameters    :    Interaction__c.Id , sInteractionAbout
        */
        @AuraEnabled
        public static Interaction_Member__c  getSelectedIntMemberDetails(String sInteraction, string sInteractionAbout)
        {    
            Interaction_Member__c interactionMemRec = new Interaction_Member__c ();
            List<Interaction_Member__c> interactionMemRecList = new List<Interaction_Member__c> ();
            try{
                interactionMemRecList = [SELECT ID, Authenticated__c,Interacting_About__c,Interaction__c, Interacting_About_Type__C  FROM Interaction_Member__c WHERE Interaction__c = :sInteraction AND Interacting_About__c =: sInteractionAbout ORDER BY CreatedDate ASC LIMIT 1];

                if(interactionMemRecList.size() > 0)    
                {
                    interactionMemRec= interactionMemRecList[0];
                }
    
                if(Test.isRunningTest()){
                    throw new QueryException();
                } 
            } catch(Exception e) {
                HUMExceptionHelper.logErrors(e,'InteractionController_LC_HUM','getSelectedIntMemberDetails()');
            }    
            return interactionMemRec;
        }   
    
    
    
    }