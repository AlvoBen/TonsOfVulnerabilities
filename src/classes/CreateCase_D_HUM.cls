/***************************************************************************************************************************************************************************
Apex Class Name  : CreateCase_D_HUM
Version          : 1.0
Created Date     : May 24 2016
Function         : This Webservice is used to create case records from the GCP/CCP system.
Test Class       : HUMCaseServiceTest
****************************************************************************************************************************************************************************
Modification Log:  

* Developer Name                Code Review        Date                        Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri                           05/24/2016                   Original Version
* Prasad Jandhyala                                02/28/2017                   Req 301390
* Sumit Sharma                                    05/25/2017
* Prafull Verma                  103536           06/15/2017        
* Jasmeen Shangari               105724           06/27/2017                   Defect 298665 - Complaint_Type__c is not populated
* Mohammed Noor                  112270           07/14/2017                   REQ - 315928 - Creditable Coverage Intake - Case Service 
* Mohammed Noor                  114118           07/26/2017                   REQ - 315928 - Creditable Coverage Intake - Case Service - Made changes to save
*                                                                                      encrypted messages in CaseComments. 
* Mohammed Noor                  117665           08/07/2017                   Defect 305551 - Fix for handling Unknown Member creation if only Member-ID-Base is 
*                                                                              is sent in Case Service input request.
* Mohammed Noor                  119200           08/11/2017                   Defect 309992 - Update the TRACKID on the case from Input and skip creating comments if input is empty.
* Mohammed Noor                  121719           08/21/2017                   Defect 312230 - Fix to locate the correct Known Member during Case Creation.
* Mohammed Noor                  123264           08/28/2017                   Defect 313436 - Fix handle the input ID sent by Correspondence.
* Mohammed Noor                  128981           09/12/2017                   REQ - 329040 - Ability for CRM Service to create unknown member accounts resulting from Conduent data
* Moshitha Gunasekaran           211055           04/02/2018                   To remove the SourceAPP logic and make it based on ID.
* Suresh Gottipati               280927           07/31/2018                   Added Switch between Platform and Cipher Encryption                              
* Sunil Bukya                                     08/20/2018                   REQ - 367786 - Implemented Member ID fields platform encryption changes.
* Syed Mubarak                                    10/03/2018                   Defect #385363.0001 fix 
* Sunil Bukya                                     12/26/2018                   Removed Dpass callout 
* Santhi Mandava                                  04/15/2019                   Replaced "Member_Id_Value__c" with Name    
* Vaishnavi Manda                                 05/12/2021                   US 2163327 MF6 Checkmarx scan issue fix             
**************************************************************************************************************************************************************/
global with sharing class CreateCase_D_HUM
{    
    private static final Map<String, Schema.RecordTypeInfo> rtypesCache = Schema.SObjectType.Case.getRecordTypeInfosByName();              
    public static final String ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
    
    //To store the encrypted error messages from input request
    private static string accountErrorMsg = '';
    private static string policyErrorMsg  = '';
    private static string caseCommentErrorMsg = '';
    
    //To store the Case RecordID
    private static string caseRecordID = '';
       
    /*
* Method name : createCase
* Description : This method is used to create a case records in CRM through Webservice from External System.
* Return Type : HUMCreateCaseResponseHelper
* Parameter   : HUMCreateCaseRequestHelper
*/
    
    webService static HUMCreateCaseResponseHelper createCase(HUMCreateCaseRequestHelper request)
    {
        HUMCreateCaseResponseHelper response = new HUMCreateCaseResponseHelper();
        if(request == null)
        {
            response = getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.HUMCaseCreateMandatoryError,false);
        }
        else
        {  
            response = createCaseRecord(request);      
        }
        return response;    
    }   
    
    
    /*
* Method name : createCaseRecord
* Description : This method is used to create a case records. 
*               
*               In Realtime request (Asynchronous flag is TRUE), then Case object will be created with the mandatory fields alone and the Case Number
*               will be sent in the Response. The Other Case fields such as associating Member, Policy, RecordType, Queue details will be done Asynchronously 
*               using Queueable APEX and the case will be updated. If any errors, they will be added as CaseComments in the Case created in 1st pass.
*               
*               In Near Realtime Request (Asynchronous Flag is False), the entire Case Object including the mandatory fields, Members, Policy, RecordType, Queue
*               details will be created and the Case Number will be returned.
*
*               Incase of any errors in both Realtime and NearRealtime, the corresponding error message will be returned in the response.
*               
* Return Type : HUMCreateCaseResponseHelper 
* Parameter   : HUMCreateCaseRequestHelper
*/
    @TestVisible
    private static HUMCreateCaseResponseHelper createCaseRecord(HUMCreateCaseRequestHelper caseRequest)
    {
        HUMCreateCaseResponseHelper caseResponseObject = new HUMCreateCaseResponseHelper();
        Case oCase = new Case();                                      
                        
        try
        {      
            oCase = assignCaseValues(caseRequest);
            
            if(caseRequest.bAsynchronousProcess)
            {                               
                caseResponseObject = insertCaseObj(oCase);

                if(caseResponseObject.Success == true)
                {                                                           
                    CreateCase_S_HUM updateJob = new CreateCase_S_HUM(caseRequest, caseRecordID);
                    ID jobID = System.enqueueJob(updateJob);                    
                }               
            }
            else
            {               
                oCase = populateCaseFields(caseRequest, oCase);                                                                                                                     
                caseResponseObject = insertCaseObj(oCase);
                
                //Insert the Case Comments from the Input Request
                if(!caseRequest.CaseCommentDTO.isEmpty() && caseResponseObject.Success == true)
                    createCommentsFromInput(caseRequest.CaseCommentDTO, oCase.Id);              
            }                        
        }    
        catch(Exception ex)
        {
                                   
            List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CreateCase_D_HUM','createCaseRecord');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            } 
            caseResponseObject = getErrorLogInstance('',ex.getMessage(),false);                    
        }                
        return caseResponseObject;
    }
    

    /*
* Method name : insertCaseObj
* Description : This method is used to insert the Case Object.
* Return Type : HUMCreateCaseResponseHelper
* Parameter   : Case Object
*/
    @TestVisible
    private static HUMCreateCaseResponseHelper insertCaseObj(Case oCase)
    {
        HUMCreateCaseResponseHelper caseResponse = new HUMCreateCaseResponseHelper();
        String caseId = null;
                
        Database.SaveResult sResult = Database.insert(oCase, false); 
            
        if(sResult.isSuccess())
        {
            caseId = sResult.getId();                                   
            List<Case> oCaseReturn = new List<Case>([Select Id,CaseNumber from Case where Id = :caseId]);
            
            if(!oCaseReturn.isEmpty())
            {                  
                caseResponse.sRecordId = oCaseReturn[0].CaseNumber;
                caseResponse.Success = true;
                caseRecordID = caseId;                              
            }
        }
        else
        {               
            caseResponse.Success = false;
        
            for(Database.Error err : sResult.getErrors()) 
            { 
                caseResponse.ErrorCode += (String.valueOf(err.getStatusCode()) + '; ');
                caseResponse.ErrorMessage += (err.getMessage() + '; ');             
            }
            logError(sResult.getErrors(), 'insertCaseObj', 'case', sResult.getId());                        
        }                       
        return caseResponse;      
    }

    
    /*
* Method name : assignCasevalues
* Description : This method is used to assign case request values to insert case record.
* Return Type : Case 
* Parameter   : HUMCreateCaseRequestHelper 
*/ 
    @TestVisible
    private static Case assignCaseValues(HUMCreateCaseRequestHelper caseRequestObject){
        Case objCase;     
        objCase = new Case();
        HUMCreateCaseRequestHelper objRequest = new HUMCreateCaseRequestHelper();
        
        objCase.Origin = caseRequestObject.sOrigin;
        objCase.G_A_Rights_Given__c = caseRequestObject.sGARightsGiven;
        objCase.G_A_Reason__c = caseRequestObject.sGAReason;
        objCase.Type = caseRequestObject.sType; 
        objCase.Status = caseRequestObject.sStatus;  
        objCase.Owner_Queue__c = caseRequestObject.sOwnerQueue;  
        objCase.Case_Owner__c =  caseRequestObject.sOwnerQueue;      
        objCase.Service_Center__c  = caseRequestObject.sServiceCenter;
        objCase.Department__c = caseRequestObject.sDepartment;
        objCase.Classification_Type__c = caseRequestObject.sClassificationType;
        objCase.Priority = caseRequestObject.sPriority;
        objCase.Complaint__c = caseRequestObject.sComplaint;
        objCase.Complaint_Reason__c = caseRequestObject.sComplaintReason;
        objCase.Complaint_Type__c = caseRequestObject.sComplaintType;
        objCase.Doc_Type__c = caseRequestObject.sDocType;
        objCase.DCN__c = caseRequestObject.sDCN;
        objCase.Created_By_Queue__c = caseRequestObject.sCreatedQueue; 
        objCase.LastmodifiedBy_Queue__c = caseRequestObject.sCreatedQueue; 
        objCase.Work_Queue_View_Name__c = caseRequestObject.sWorkQueueViewName; 
        objCase.Topic__c = caseRequestObject.sTopicName;
        objCase.Medicare_Track_ID__c = caseRequestObject.sExternalTrackId;      
        
        if(String.isNotBlank(caseRequestObject.sFollowupDueDate) && String.isNotEmpty(caseRequestObject.sFollowupDueDate))
        {
            objCase.Follow_up_Due_Date__c = Date.parse(caseRequestObject.sFollowupDueDate); 
        }
         List<CTCI_Junction__c> lstCTCIJunction = [SELECT Id, Classification__r.Id, Classification_Type__r.Id, Intent__r.Id FROM CTCI_Junction__c 
                                              WHERE Classification_Type__r.Name = : caseRequestObject.sClassificationType AND 
                                              Classification__r.Name = : caseRequestObject.sClassification AND 
                                              Intent__r.Name = : caseRequestObject.sIntent 
                                              AND Intent__r.IS_ACTIVE__C= true
                                              AND Classification__r.IS_ACTIVE__C=true AND CTCI_Junction__c.ISACTIVE__C=true];                                                    
        if(lstCTCIJunction.size() > 0)
        {                     
            objCase.CTCI_List__c = lstCTCIJunction[0].Id;
            objCase.CLASSIFICATION_ID__c = lstCTCIJunction[0].Classification__r.Id;
            objCase.INTENT_ID__C = lstCTCIJunction[0].Intent__r.Id;
        } 

        if(!String.isBlank(caseRequestObject.sCaseSubType))
        {
          //set the Case Subtyoe
            objCase.Subtype__c = caseRequestObject.sCaseSubType;
            
            //set the Case RecordType for Medicare cases
            objCase.RecordTypeId = getCaseRecordType(caseRequestObject,null); 
        
        }
        
        return objCase;   
    } 
    
    /*
* Method name : getErrorLogInstance
* Description : This method will be used retuen the response instance with error related fields.
* Return Type : HUMCreateCaseResponseHelper
* Parameter   : String , String , Boolean
*/
    public static HUMCreateCaseResponseHelper getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean sSuccess )
    {        
        HUMCreateCaseResponseHelper caseResponse = new HUMCreateCaseResponseHelper();        
        caseResponse.Success = sSuccess;
        caseResponse.ErrorMessage =sErrorMessage;
        caseResponse.ErrorCode = sErrorCode;        
        return caseResponse;
    }
    
    /* Leaving it commented for future if Attachments needed to be added to Case
    public class LogAttachmentWrapper {
        public List<Attachment_Log_Item__c> attachmentLogItemList;
        public Attachment_Log__c attachmentLog;
    } */
    
   /*  
* Method name : getMemberAccountMaps
* Description : This method returns the Map of Account ID for the respective MedicareID of the CaseRequest. 
*               First Member is located using the various input parameters received. If no member is located, then Unknown Member is created.
* Return Type : Map<String,String> (Key is the Interactingtype and the value is Case ID).
* Parameter   : HUMCreateCaseRequestHelper, Interaction type(String) and Case ID.    
*/
    public static Map<String,String> getMemberAccountMaps(HUMCreateCaseRequestHelper caseRequestObject, String sInteractingType,Id caseId) 
    {    
        Map<String,String> memberAccountIDMap = new Map<String,String>();
        List<Member_Id__c> objMembers = new List<Member_Id__c>();
        String sInteractingId = null;
        String sInteractingIdType = null;
        String sFirstName = '';
        String sLastName = '';
        String sBirthDate = '';
        String sStateCode = '';
        String sMemberID = '';
        
      try {  
            if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))
            {             
                sInteractingId = caseRequestObject.sInteractingWithId;
                sInteractingIdType = caseRequestObject.sInteractingWithIdType;            
                sBirthDate = caseRequestObject.sInteractingWithDOB;
                sFirstName = caseRequestObject.sInteractingWithFirstName;
                sLastName = caseRequestObject.sInteractingWithLastName;
                sStateCode = caseRequestObject.sInteractingWithState;
            }
            else if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))
            {
                sInteractingId = caseRequestObject.sInteractingAboutId;
                sInteractingIdType = caseRequestObject.sInteractingAboutIdType;
                sBirthDate = caseRequestObject.sInteractingAboutDOB;
                sFirstName = caseRequestObject.sInteractingAboutFirstName;
                sLastName = caseRequestObject.sInteractingAboutLastName;              
                sStateCode = caseRequestObject.sInteractingAboutState;                
            }
            String sAccountExternalId = '';
            Boolean bMemberExist = false;
            
            //Check if the input state code is available in the Picklist Options, else overwrite it 'OT' for 'Other State code'
            if(!String.isBlank(sStateCode) && !sStateCode.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE))
            {
                Schema.DescribeFieldResult fieldResult = Account.PERSONMAILINGSTATECODE.getDescribe();
                List<Schema.PicklistEntry> picklistentries = fieldResult.getPickListValues();
                Boolean stateFound = false;                    
                for(Schema.PicklistEntry picklistentry : picklistentries)
                {
                    if(picklistEntry.isActive() && picklistEntry.getValue().equalsIgnoreCase(sStateCode))
                    {
                        stateFound = true;
                    }       
                }
                if(!stateFound)
                { 
                    if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM)) caseRequestObject.sInteractingAboutState = GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE;
                    if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM)) caseRequestObject.sInteractingWithState = GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE;
                    sStateCode = GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE;       
                }
            }            
                        
            //For Input ID type is among MedicareID or MedicAid ID or SSN or MemberIDBase  
            if((sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM ||sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM  
            || sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM || sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM) && !String.isBlank(sInteractingId))
            {                 
                sMemberID = locateMember(sInteractingId, sInteractingIdType, sBirthDate, sFirstName, sLastName);
                                        
                if(!String.isBlank(sMemberID))
                {
                    memberAccountIDMap.put(sInteractingType ,sMemberID);
                    bMemberExist = true;
                }                                                
                else if (sInteractingType == GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM && sInteractingIdType != GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM && !String.isBlank(caseRequestObject.sMemberSSN))
                {                     
                    //Locate the account with MemberSSN sent in the input                                                               
                    sMemberID = locateMember(caseRequestObject.sMemberSSN, GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM, sBirthDate, sFirstName, sLastName);
                                
                    if(!String.isBlank(sMemberID))
                    {
                        memberAccountIDMap.put(sInteractingType ,sMemberID);
                        bMemberExist = true;
                    }                                         
                }                     
            }
            else if(sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM && String.isBlank(sInteractingId))
            {   
                
                //Locate Account using FirstName, LastName, DOB and State code from Input
                sMemberID = locateMemberByName(sFirstName, sLastName, sBirthDate, sStateCode);
                
                if(!String.isBlank(sMemberID))
                {
                    memberAccountIDMap.put(sInteractingType ,sMemberID);
                    bMemberExist = true;
                }
            }                                
            else if(sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM && !String.isBlank(sInteractingId))
            {                           
                //Locate Account using the Enterprise ID
                List<Account> oAccount = [SELECT id FROM Account WHERE Enterprise_Id__c = : sInteractingId AND ETL_Record_Deleted__c = false limit 1];
                if(!oAccount.isEmpty())
                {
                    memberAccountIDMap.put(sInteractingType, oAccount[0].Id );
                    bMemberExist = true;
                }                
            }
                                    
            //If No Member is located using input Parameters, then create Unknown Member
            if(!bMemberExist)
            {   
                     
                if(sInteractingType == GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM && !String.isBlank(caseRequestObject.sMemberSSN)) 
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SSN  + HUMUtilityHelper.getMD5Hash(caseRequestObject.sMemberSSN);   
                }
                else if(sInteractingType == GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM && sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM && !String.isBlank(sInteractingId))
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SSN + HUMUtilityHelper.getMD5Hash(sInteractingId.toUppercase());                        
                }
                else if (sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM && !String.isBlank(sInteractingId))
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEDICARE + HUMUtilityHelper.getMD5Hash(sInteractingId.toUppercase()); 
                }
                else if (sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM && !String.isBlank(sInteractingId))
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_ENTID + HUMUtilityHelper.getMD5Hash(sInteractingId.toUppercase());                
                }
                else if (sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM && !String.isBlank(sInteractingId))
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_MEMBERIDBASE + HUMUtilityHelper.getMD5Hash(sInteractingId.toUppercase());                
                }                    
                else if (!String.isBlank(sInteractingId))
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_UKEY + HUMUtilityHelper.getMD5Hash(sInteractingId.toUppercase());
                }                
                else if (sInteractingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM && String.isBlank(sInteractingId))
                {
                    sAccountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC;                       
                }                       
                memberAccountIDMap.putAll(CreateCaseAccount_D_HUM.createAccount(caseRequestObject,sInteractingType, sAccountExternalId,caseId));
              }
          } 
          catch(Exception ex)
          {                                             
             if(caseRequestObject.bAsynchronousProcess)
             {
                HUMExceptionHelper.logErrors(ex,'CreateCase_D_HUM','getMemberAccountMaps');  
             }
             else            
                throw ex;
          } 
         
         return memberAccountIDMap;        
      }
      
        /*  
* Method name : locateMemberByName
* Description : This method is used to locate the Member to be associated with the Case Record.    
* Return Type : String (Member RecordID)
* Parameter   : Input String FirstName, String Type, Input Birth Date, Input FirstName and Input LastName
*/
    @TestVisible
    private static String locateMemberByName(String sFirstName, String sLastName, String sBirthDate, String sStateCode) 
    {         
        List<Account> oAccount = new List<Account>();
        String locatedAccountID = '';
        boolean bIsMatching =false;
        String searchTerms=''; 
        if(String.isNotBlank(sFirstName)) searchTerms = sFirstName+'*';                                                                                       
        if((String.isNotBlank(sLastName) && string.isNotBlank(searchTerms)) )
            searchTerms += ' AND ' + sLastName+'*';
        else if((String.isNotBlank(sLastName) && string.isBlank(searchTerms)))
            searchTerms += sLastName+'*';
        String queryAcct ='';
        IF(string.isNotBlank(searchTerms))
        {
            queryAcct = 'FIND \''+ String.escapeSingleQuotes(searchTerms)+' \' RETURNING Account(id, RecordType.Name,Individual_First_Name__c,Individual_Last_Name__c, Birthdate__c Where ';
            if(String.isNotBlank(sStateCode)) queryAcct += ' PersonMailingStateCode = \'' +string.escapeSingleQuotes(sStateCode) + '\''+' AND ';
            if(String.isNotBlank(sBirthDate)) queryAcct += ' Birthdate__c = \'' +string.escapeSingleQuotes(sBirthDate) + '\''+' AND ';
            queryAcct += 'ETL_Record_Deleted__c = false' + ' AND RecordType.Name in ' + GLOBAL_CONSTANT_HUM.MEMBERIDBASE_CASE_RECCORDTYPE + ' ORDER BY RecordType.Name'+')';        
            
            List<List<Account>> searchList=search.query(queryAcct);
            List<Account> tempAccountList = searchList[0];
            
            If(tempAccountList != Null && !tempAccountList.isEmpty())
            {
                For(Account obj : tempAccountList)
                {
                    bIsMatching = True;
                    If(String.IsNotBlank(sFirstName))
                    {
                        If(String.IsBlank(obj.Individual_First_Name__c) || (String.IsNotBlank(obj.Individual_First_Name__c) && !obj.Individual_First_Name__c.startsWithIgnoreCase(sFirstName))) bIsMatching = false;
                    }
                    If(String.IsNotBlank(sLastName))
                    {
                        If(String.IsBlank(obj.Individual_Last_Name__c) || (String.IsNotBlank(obj.Individual_Last_Name__c) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(sLastName))) bIsMatching = false;
                    }
                    If(bIsMatching)
                    {           
                        oAccount.Add(obj);
                    }
                }
            }
        }
        else if(String.isNotBlank(sBirthDate))
        {
            queryAcct = 'SELECT id, RecordType.Name FROM Account WHERE ';
            if(String.isNotBlank(sStateCode)) queryAcct += ' PersonMailingStateCode = \'' +string.escapeSingleQuotes(sStateCode) + '\''+' AND ';
                                              queryAcct += ' Birthdate__c = \'' +string.escapeSingleQuotes(sBirthDate) + '\''+' AND ';
            queryAcct += 'ETL_Record_Deleted__c = false' + ' AND RecordType.Name in ' + GLOBAL_CONSTANT_HUM.MEMBERIDBASE_CASE_RECCORDTYPE + ' ORDER BY RecordType.Name';            

            oAccount = (List<Account>)Database.Query(queryAcct);
        }
                
        if(!oAccount.isEmpty())
        {                   
            List<Account> knownMembers = new List<Account>();
            List<Account> unknownMembers = new List<Account>();
            
            for(Account acct : oAccount)
            {
                if(acct.RecordType.Name == 'Member') knownMembers.add(acct);
                        
                if(acct.RecordType.Name == 'Unknown Member') unknownMembers.add(acct);                          
            }
            if(knownMembers.size() == 1 )
            {
                locatedAccountID = knownMembers[0].Id;              
            }
            else if(!unknownMembers.isEmpty())
            {
                locatedAccountID = unknownMembers[0].Id;
            }                   
        }
        return locatedAccountID;
    }
    
    /*  
* Method name : locateMember
* Description : This method is used to locate the Member to be associated with the Case Record.    
* Return Type : String (Member RecordID)
* Parameter   : Input MemberID, Input Type, Input Birth Date, Input FirstName and Input LastName
*/
    @TestVisible
    private static String locateMember(String sInteractingID, String sInteractingType, String sBirthDate, String sFirstName, String sLastName) 
    {                                   
        String sValue = '';  
        String locatedMemberID = '';
        List<Member_Id__c> memberList = new List<Member_Id__c>();
        String typeString = '(\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM + '\',\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM + '\',\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM + '\',\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM + '\')';
        Map<Id, Member_Id__c> uniqMemberMap = new Map<Id, Member_Id__c>();   // Map of Account ID and Member_ID__c Object. This holds the unique Members returned from the query
        String sQuery ='';
            sQuery = 'FIND \'' + sInteractingID + '*'+ '\' IN NAME FIELDS RETURNING Member_Id__c(id, Name, Type__c, Policy_Member__r.Member__c, Policy_Member__r.Member__r.name,Policy_Member__r.Member__r.firstname,' + 
                            'Policy_Member__r.Member__r.lastname, Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.PersonBirthdate WHERE ';
                            
            if(sinteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))
                sQuery += ' Type__c IN ' + typeString ;
            else
                sQuery += ' Type__c = \'' + sinteractingType + '\'';  
                                                
            
            sQuery+= ' AND Policy_Member__r.Member__r.ETL_Record_Deleted__c = false'+')';
            List<List<Member_Id__c>>searchList=search.query(sQuery);
            List<Member_Id__c> tempMemberList = searchList[0];

            If(tempMemberList != Null && !tempMemberList.isEmpty())
            {
                For(Member_Id__c obj : tempMemberList)
                {
                    If(obj.Name.startsWithIgnoreCase(sValue))
                    {
                        memberList.Add(obj);
                    }
                }
            }
                
        Map<Id, Member_Id__c> uniqMemberIdBaseMap = new Map<Id, Member_Id__c>();    
        Map<Id, Member_Id__c> uniqSSNMap = new Map<Id, Member_Id__c>();
        Map<Id, Member_Id__c> uniqMedicareIdMap = new Map<Id, Member_Id__c>();
        Map<Id, Member_Id__c> uniqMedicaidIdMap = new Map<Id, Member_Id__c>();                                                    
        
        if(!memberList.isEmpty() && memberList.size() > 1 )
        {               
            if(sInteractingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))
            {
                uniqMemberIdBaseMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM, memberList);
                uniqSSNMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM, memberList);
                uniqMedicareIdMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM, memberList);
                uniqMedicaidIdMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM, memberList);                
                
                if(!uniqMemberIdBaseMap.values().isEmpty() && uniqMemberIdBaseMap.values().size() == 1 )                                
                    locatedMemberID = uniqMemberIdBaseMap.values()[0].Policy_Member__r.Member__c;                   
                else if(!uniqMemberIdBaseMap.values().isEmpty())                    
                    locatedMemberID = findMatchingMember(uniqMemberIdBaseMap.values(), sBirthDate, sFirstName, sLastName);                      
                    
                if(String.isBlank(locatedMemberID))
                {
                    if(!uniqSSNMap.values().isEmpty() && uniqSSNMap.values().size() == 1 )                                  
                        locatedMemberID = uniqSSNMap.values()[0].Policy_Member__r.Member__c;                    
                    else if(!uniqSSNMap.values().isEmpty())                     
                        locatedMemberID = findMatchingMember(uniqSSNMap.values(), sBirthDate, sFirstName, sLastName);                                                                       
                }
                
                if(String.isBlank(locatedMemberID))
                {
                    if(!uniqMedicareIdMap.values().isEmpty() && uniqMedicareIdMap.values().size() == 1 )                                
                        locatedMemberID = uniqMedicareIdMap.values()[0].Policy_Member__r.Member__c;                     
                    else if(!uniqMedicareIdMap.values().isEmpty())                      
                        locatedMemberID = findMatchingMember(uniqMedicareIdMap.values(), sBirthDate, sFirstName, sLastName);                            
                }
                
                if(String.isBlank(locatedMemberID))
                {
                    if(!uniqMedicaidIdMap.values().isEmpty() && uniqMedicaidIdMap.values().size() == 1 )                                
                        locatedMemberID = uniqMedicaidIdMap.values()[0].Policy_Member__r.Member__c;                     
                    else if(!uniqMedicaidIdMap.values().isEmpty())                      
                        locatedMemberID = findMatchingMember(uniqMedicaidIdMap.values(), sBirthDate, sFirstName, sLastName);                        
                }                                       
            }
            else
            {
                uniqMemberMap = createUniqMemberMap(sInteractingType, memberList);
                if(!uniqMemberMap.values().isEmpty() && uniqMemberMap.values().size() == 1 )                                
                    locatedMemberID = uniqMemberMap.values()[0].Policy_Member__r.Member__c;                     
                else if(!uniqMemberMap.values().isEmpty())                      
                    locatedMemberID = findMatchingMember(uniqMemberMap.values(), sBirthDate, sFirstName, sLastName);                                                        
            }                                                                                       
        }
        else if (memberList.size() == 1) {
            locatedMemberID = memberList[0].Policy_Member__r.Member__c;
        }
        
        return locatedMemberID;
    }

/*  
* Method name : createUniqMemberMap
* Description : This method is used create unique Account Map. If the Member has more than one policy, the Relationship query on Member_ID__c can return more than one record for the same Account.
* Return Type : Map<Id, Member_Id__c>
* Parameter   : Type of ID and List of Member_Id__c 
*/
    @TestVisible
    private static Map<Id, Member_Id__c> createUniqMemberMap(String IdType, List<Member_Id__c> memberList)
    {
        Map<Id, Member_Id__c> memberMap = new Map<Id, Member_Id__c>();
        for(Member_Id__c member : memberList)
        {
            if(member.type__c.equalsIgnoreCase(IdType))
                memberMap.put(member.Policy_Member__r.Member__c, member);
        }
        return memberMap;
    }

/*  
* Method name : findMatchingMember
* Description : This method is used to match the list of Account against input FirstName, LastName and DOB
* Return Type : String (Located AccountID)
* Parameter   : List<Member_Id__c>, BirthDate, FirstName and LastName.
*/    
    @TestVisible
    private static String findMatchingMember(List<Member_Id__c> memberList, String sBirthDate, String sFirstName, String sLastName)
    {
        String matchingMemberId = '';
        for(Member_Id__c member : memberList)
        {                                                           
            Boolean bBirthDateMatch = sBirthDate.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.Birthdate__c).trim());
            Boolean bFirstNameMatch = sFirstName.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.FirstName).trim());
            Boolean bLastNameMatch = sLastName.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.LastName).trim());                                                            
            if(bFirstNameMatch && bLastNameMatch && bBirthDateMatch)
            {
                matchingMemberId = member.Policy_Member__r.Member__c;
                break;                                                                             
            }                        
        }
        return matchingMemberId;
    } 
        
                  
/*  
* Method name : updateCaseAndAccount
* Description : This method is used to update case and account record in the asynchronous call through Queuable Apex call from CreateCase_S_HUM class.
*               Member, Policy, Queue and other details are populated on the case record and case is updated. If the Case Update fails, then tries to 
*               get as much details into Case Record as possible, by updating Member, Policy and other details in separate DMLs.
* Return Type : Void
* Parameter   : HUMCreateCaseRequestHelper, Case Id 
*/
    public static void updateCaseAndAccount(HUMCreateCaseRequestHelper caseRequest, Id caseId) 
    {    
        Case oCase = new case();                
        try
        {           
            if (Schema.sObjectType.Case.fields.id.isUpdateable())                    
                oCase.id = caseId;  
                                                                        
            oCase = populateCaseFields(caseRequest, oCase);
                                                                                                                                                                                                            
            if(!caseRequest.CaseCommentDTO.isEmpty())
                createCommentsFromInput(caseRequest.CaseCommentDTO, oCase.Id);
                                                            
            Database.SaveResult sResult = database.update(oCase, false);
            
            if(!sResult.isSuccess())
            {
                //if the entire Case details update fails, try pieces of data separately sequentially and update as much possible data on the case record.
                updateCaseDetails(oCase);
                
                if(oCase.AccountID <> null)                 
                    updateMemberAndPolicy(oCase);                                                                               
            }
        } 
        catch(Exception ex)
        {               
           HUMExceptionHelper.logErrors(ex,'CreateCase_D_HUM','updateCaseAndAccount');                     
        }                   
    }
 
 /*  
* Method name : populateCaseFields
* Description : This method is used to populate case with Member, Policy, RecordType and other details.
* Return Type : Case
* Parameter   : HUMCreateCaseRequestHelper, Case 
*/       
    @TestVisible
    private static Case populateCaseFields(HUMCreateCaseRequestHelper caseRequest, case oCase) 
    {             
        List<Account> lstAccount = new List<Account>();
        String sRecordTypeInterWith;
        String sRecordTypeInterAbout;                
        Map<ID, Schema.RecordTypeInfo> recordTypeMap = Schema.SObjectType.Account.getRecordTypeInfosById();
        Map<String, HUMCaseInteractionAboutType__c> caseInterAboutTypeMap = HUMCaseInteractionAboutType__c.getAll();
        Map<String,String> memberAccountMap = new Map<String,String>();             
        
        try
        {                                                           
            //populate the errorMessage Static variables from the input request
            accountErrorMsg = caseRequest.sAccountErrMsg; 
            policyErrorMsg = caseRequest.sPolicyErrMsg;
            caseCommentErrorMsg = caseRequest.sCaseCommentErrMsg;
                        
            memberAccountMap.putAll(CreateCase_D_HUM.getMemberAccountMaps(caseRequest, GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM,oCase.Id));
                
            if(caseRequest.sInteractingAboutId != caseRequest.sInteractingWithId || (String.isBlank(caseRequest.sInteractingAboutId) && String.isBlank(caseRequest.sInteractingWithId))) 
            {                 
                memberAccountMap.putAll(CreateCase_D_HUM.getMemberAccountMaps(caseRequest, GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM ,oCase.Id));
            }           
            
            if(!memberAccountMap.values().isEmpty())
            {            
                lstAccount = [SELECT Id,RecordTypeId FROM Account WHERE Id IN :memberAccountMap.values()];
            }
            
            if(lstAccount.size() > 0)
            {
                Map<Id,Account> accountMap = new Map<Id,Account>(lstAccount);   
                if (Schema.sObjectType.Case.fields.AccountID.isUpdateable())         
                     oCase.AccountID = memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM);
            
                if(accountMap.containsKey(memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM)))
                    sRecordTypeInterAbout = recordTypeMap.get((accountMap.get(memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))).RecordTypeId).getName();

                if(caseRequest.sInteractingAboutId != caseRequest.sInteractingWithId  || (String.isBlank(caseRequest.sInteractingAboutId) && String.isBlank(caseRequest.sInteractingWithId)))
                {
                    if(accountMap.containsKey(memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM)))
                  sRecordTypeInterWith = recordTypeMap.get((accountMap.get(memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))).RecordTypeId).getName();
                }
                
                if(!String.isBlank(sRecordTypeInterAbout))    
                    if (Schema.sObjectType.Case.fields.Interacting_About_Type__c.isUpdateable())        
                        oCase.Interacting_About_Type__c = caseInterAboutTypeMap.get(sRecordTypeInterAbout).CaseInteraction_About_Type__c;
            
                if(caseRequest.sInteractingAboutId != caseRequest.sInteractingWithId || (String.isBlank(caseRequest.sInteractingAboutId) && String.isBlank(caseRequest.sInteractingWithId))) 
                {
                    if (Schema.sObjectType.Case.fields.Interacting_With__c.isUpdateable())
                         oCase.Interacting_With__c = memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM);
                    if(sRecordTypeInterWith <> null && sRecordTypeInterWith <> '')
                    {
                        if (Schema.sObjectType.Case.fields.Interacting_With_Type__c.isUpdateable())
                        oCase.Interacting_With_Type__c = caseInterAboutTypeMap.get(sRecordTypeInterWith).CaseInteraction_About_Type__c;
                    }
                }
                else 
                {
                    if (Schema.sObjectType.Case.fields.Interacting_With__c.isUpdateable())
                    oCase.Interacting_With__c = memberAccountMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM);
                    if(sRecordTypeInterAbout <> null && sRecordTypeInterAbout <> '')
                    {
                        if (Schema.sObjectType.Case.fields.Interacting_With_Type__c.isUpdateable())
                             oCase.Interacting_With_Type__c = caseInterAboutTypeMap.get(sRecordTypeInterAbout).CaseInteraction_About_Type__c;
                    }
                }                      
            }                                    
                 

        //set the Case RecordType for Medicare cases
        if (Schema.sObjectType.Case.fields.RecordTypeId.isUpdateable())
            oCase.RecordTypeId = getCaseRecordType(caseRequest, sRecordTypeInterAbout);
                                               
            //set the Policy for the Case
            if(oCase.Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.MEMBER && oCase.AccountID != Null) 
                if (Schema.sObjectType.Case.fields.Policy_Member__c.isUpdateable())             
                     oCase.Policy_Member__c = getMemberPolicy(caseRequest, oCase);                              
                       
            //set Case OwnerId
            Group[] ownerIdGroup = [select Id from Group where Name = : caseRequest.sOwnerQueue  and Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM Limit 1];
            if (ownerIdGroup.size()>0) {
                if(Schema.sObjectType.Case.fields.OwnerId.isUpdateable())
                    oCase.OwnerId = ownerIdGroup[0].Id; 
            }           
        }
        catch(exception ex)
        {           
            if(caseRequest.bAsynchronousProcess)
             {
                HUMExceptionHelper.logErrors(ex,'CreateCase_D_HUM','populateCaseFields');  
             }
            else
                throw ex;
        }                               
        return oCase;               
    }

        
    /* Method Name: CreateCaseComment
    Description: This method is used to create comment records for case
    Return Type: Nothing
    Parameter: 
    */    
    public static void createCaseComment(String ErrorMessage , ID CaseId )
    {
        CaseComment objCaseCommentErr = new CaseComment(); 
        if (Schema.sObjectType.CaseComment.fields.commentBody.isCreateable())          
            objCaseCommentErr.commentBody = ErrorMessage ;
        if (Schema.sObjectType.CaseComment.fields.ParentId.isCreateable())
            objCaseCommentErr.ParentId = caseId;
        try
        {      
            Database.SaveResult sResult = Database.insert(objCaseCommentErr, false);
            if(!sResult.isSuccess())
            {
                throw new HUMCustomException('Error in Creating Case Comment: '+sResult.getErrors()[0]);
            }        
        }
        catch(Exception ex)
        {            
            List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CreateCase_D_HUM','createCaseComment');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            }            
        } 
    }
    
    /* Method Name: getCaseRecordType
    Description: This method is used figure out the Case RecordType
    Return Type: ID
    Parameter: Input Case Request and Account RecordTypeName
    */
    @TestVisible
    private static ID getCaseRecordType(HUMCreateCaseRequestHelper caseRequest, String sRecordTypeInterAbout)
    {        
        ID CaseRecTypeID;                               
        //For Medicare Enrollment Cases - To get the list of Subtypes to identify Medicare Enrollment Cases
        Set<String> medicareCaseSubTypeList = new Set<String>();
        try
        {
            List<MedEnroll_CASE_SUBTYPE_HUM__mdt> medicareCaseSubTypes = [SELECT Id, CaseType__c, CaseSubType__c FROM MedEnroll_CASE_SUBTYPE_HUM__mdt where CaseType__c = : caseRequest.sType ];        
            if(medicareCaseSubTypes.size()>0)
            {
                medicareCaseSubTypeList.addAll(medicareCaseSubTypes[GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM].CaseSubType__c.split(','));
            }
                                                    
            //For In Progress Medicare Enrollment Cases 
            if(medicareCaseSubTypeList.contains(caseRequest.sCaseSubType) && caseRequest.sStatus == GLOBAL_CONSTANT_HUM.CASESTATUS_HUM)
            {
                CaseRecTypeID = rtypesCache.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).getRecordTypeId();            
            }
            //For Closed Medicare Enrollment Cases
            else if(medicareCaseSubTypeList.contains(caseRequest.sCaseSubType) && caseRequest.sStatus == GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)
            {
                CaseRecTypeID = rtypesCache.get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId();                
            }
            else if(!String.isBlank(sRecordTypeInterAbout))            
            {      
                CaseRecTypeID = HUMUtilityHelper.getCaseRecordType(sRecordTypeInterAbout);
            }
        }
        catch(exception ex)
        {
            if(caseRequest.bAsynchronousProcess)
            {
                HUMExceptionHelper.logErrors(ex,'CreateCase_D_HUM','getCaseRecordType');  
            }
            else
                throw ex;
        }                               
        return CaseRecTypeID; 
    } 
    
    /* Method Name: getMemberPolicy
    Description: This method is used get the Policy of the Member
    Return Type: ID
    Parameter: Input Case Request and Case Object
    */
    @TestVisible
    private static ID getMemberPolicy(HUMCreateCaseRequestHelper caseRequest, Case oCase)
    {                  
        ID memberPolicyID;
        ID activePolicyMemberId;
        ID futurePolicyMemberId;
        try
        {                                          
        if(oCase.RecordTypeId == rtypesCache.get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId() || oCase.RecordTypeId == rtypesCache.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).getRecordTypeId())
        {
            Date todayDate = System.today();
            Integer numberDaysActive = 0;
            Integer numberDaysFuture = 0;
            List<Policy_Member__c> medPolicyMember = [SELECT Id, Name, Effective_Date__c, Member__c, End_Date__c FROM Policy_Member__c 
                                                    where Member__c = :oCase.AccountID and Policy_Member__c.ETL_Record_Deleted__c= false 
                                                    and Policy__r.Major_LOB__c in :GLOBAL_CONSTANT_HUM.MEDICARE_CASE_SERVICE and Policy__r.Product__r.ProductCode ='MED'];
                    
            for(Policy_Member__c policyMember : medPolicyMember)
            {
                Date policystartDate = date.parse(policyMember.Effective_Date__c);      
                
                if(policyMember.End_Date__c == GLOBAL_CONSTANT_HUM.MAX_DATE_HUM) 
                    policyMember.End_Date__c = GLOBAL_CONSTANT_HUM.MAX_SFDC_DATE_HUM;
                
                Date policyEndDate = date.parse(policyMember.End_Date__c);       
                
                //Business Rule to identify active policy record
                if((policyStartDate <= todayDate && policyEndDate >= todayDate))
                {
                    if(policyStartDate.daysBetween(todayDate) < numberDaysActive || numberDaysActive == 0)
                    {
                        numberDaysActive = policyStartDate.daysBetween(todayDate);
                        activePolicyMemberId = policyMember.Id;
                    }
                }   
            
                //Business Rule to identify future policy  record
                if(policyStartDate >  todayDate )
                {
                    if(todayDate.daysBetween(policyStartDate) < numberDaysFuture || numberDaysFuture == 0)
                    {
                        numberDaysFuture = todayDate.daysBetween(policyStartDate);
                        futurePolicyMemberId = policyMember.Id;
                    }
                }           
            }
        
            if(activePolicyMemberId <>  null)
                memberPolicyID  = activePolicyMemberId;
            else if (futurePolicyMemberId <> null)
                memberPolicyID  = futurePolicyMemberId; 
        }
        }
        catch(exception ex)
        {
            if(caseRequest.bAsynchronousProcess)
            {
                HUMExceptionHelper.logErrors(ex,'CreateCase_D_HUM','getCaseRecordType');  
            }
            else
                throw ex;
        }                
        return memberPolicyID;
    }
    
    /* Method Name: createCommentsFromInput
    Description: This method is used to insert the caseComments receive in input request
    Return Type: void
    Parameter: Input CasecommentDTO, Case ID  */
    @TestVisible
    private static void createCommentsFromInput(List<HUMCaseCommentRequestDTO> CaseCommentDTO, ID caseId)
    {
        List<CaseComment> lstCaseComment = new List<CaseComment>();         
        for(HUMCaseCommentRequestDTO sComment : CaseCommentDTO)
        {
            if(String.isNotBlank(sComment.CommentBody))
            {            
            CaseComment objCaseComment = new CaseComment();    
            if (Schema.sObjectType.CaseComment.fields.commentBody.isCreateable())       
                objCaseComment.commentBody = sComment.CommentBody;
            if (Schema.sObjectType.CaseComment.fields.ParentId.isCreateable())
                objCaseComment.ParentId = caseId;
            lstCaseComment.add(objCaseComment);
            }
        }       
        
        if(lstCaseComment.size() > 0)
        {
            Database.SaveResult[] caseCommentSaveResultList = Database.insert(lstCaseComment,false);                
        
            Boolean bIsError = HUMExceptionHelper.processSaveResults(caseCommentSaveResultList, lstCaseComment, 'CreateCase_D_HUM', 'createCommentsFromInput', 'CaseComment');
            if(bIsError) createCaseComment(caseCommentErrorMsg,caseId);
        }
    }
    
    /* Method Name: updateMemberAndPolicy
    Description: This method is to Update Member and Policy Details sequentially in separate DMLs
    Return Type: void
    Parameter: Case Object
    */
    @TestVisible
    private static void updateMemberAndPolicy(case oCase)
    {       
        Case caseForUpdate = new Case();
        Boolean bSuccess = false;
        String errMsg = AccountErrorMsg;
        
        caseForUpdate.Id = oCase.Id;
        if (Schema.sObjectType.Case.fields.AccountID.isUpdateable())
        caseForUpdate.AccountID = oCase.AccountID;
        if (Schema.sObjectType.Case.fields.Interacting_About_Type__c.isUpdateable())
        caseForUpdate.Interacting_About_Type__c = oCase.Interacting_About_Type__c;
        if (Schema.sObjectType.Case.fields.Interacting_With__c.isUpdateable())
        caseForUpdate.Interacting_With__c = oCase.Interacting_With__c;
        if (Schema.sObjectType.Case.fields.Interacting_With_Type__c.isUpdateable())
        caseForUpdate.Interacting_With_Type__c = oCase.Interacting_With_Type__c;
         if (Schema.sObjectType.Case.fields.RecordTypeId.isUpdateable())
        caseForUpdate.RecordTypeId = oCase.RecordTypeId;        
         if (Schema.sObjectType.Case.fields.Subtype__c.isUpdateable())
        caseForUpdate.Subtype__c = oCase.Subtype__c;
        
        bSuccess = performCaseUpdate(caseForUpdate, errMsg);
        
        if(bSuccess && oCase.Policy_Member__c <> null)
        {           
            caseForUpdate = new Case();         
            errMsg = PolicyErrorMsg;            
            caseForUpdate.Id = oCase.Id;
            caseForUpdate.Policy_Member__c = oCase.Policy_Member__c;
            bSuccess = performCaseUpdate(caseForUpdate, errMsg);
        }                           
    }

    
    /* Method Name: updateCaseDetails
    Description: This method is to Update Case Details in separate DML
    Return Type: void
    Parameter: Case Object
    */
    @TestVisible
    private static void updateCaseDetails(case oCase)
    {       
        Case caseForUpdate = new Case();
        String errMsg = '';

        caseForUpdate.Id = oCase.Id; 
        if (Schema.sObjectType.Case.fields.OwnerId.isUpdateable())       
        caseForUpdate.OwnerId = oCase.OwnerId;
                
        Boolean bSuccess = performCaseUpdate(caseForUpdate, errMsg);                 
    }            
    
    /* Method Name: performCaseUpdate
    Description: This method is to Update Case record.
    Return Type: Boolean
    Parameter: Case Object and Error Message
    */
    @TestVisible
    private static Boolean performCaseUpdate(Case caseForUpdate, String errorMsg)
    {       
        Database.Error error;
        Boolean result = false;
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c > ();
        
        Database.SaveResult sResult = Database.update(caseforUpdate,false);
                
        if(sResult.isSuccess())
        {
            result = true;
        }
        else
        {    
            result = false;
            if(!String.isBlank(errorMsg))                               
            createCaseComment(errorMsg,caseForUpdate.Id);
            
            error = sResult.getErrors()[0];
                        
            listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(error.getStatusCode()),
                        Error_Message__c = error.getMessage(),
                        class_name__c = 'CreateCase_D_HUM',
                        Method_Name__c = 'performCaseUpdate',
                        Object_Type__c = 'Case',
                        Record_ID__c = caseForUpdate.Id,
                        Type__c = ERROR_APPLICATION));
                        
            HUMExceptionHelper.saveLog(listErrorLogs, false);    
        }
        return result;
    }
    
    /* Method Name: logError
    Description: This method is to Log errors.
    Return Type: void
    Parameter: List of errors, method name, Sobject type, recordID
    */
    public static void logError(List<Database.Error> errors, String methodName, String sObjectType, String recId)
    {
        Database.Error error;
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c >();
        
        for(Database.Error err : errors)
        {
            listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(err.getStatusCode()),
                        Error_Message__c = err.getMessage(),
                        class_name__c = 'CreateCase_D_HUM',
                        Method_Name__c = methodName,
                        Object_Type__c = sObjectType,
                        Record_ID__c = recId,
                        Type__c = ERROR_APPLICATION));
        }       
        HUMExceptionHelper.saveLog(listErrorLogs, false);
    }
    
}