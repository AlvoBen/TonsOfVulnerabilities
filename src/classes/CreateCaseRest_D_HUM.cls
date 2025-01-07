/******************************************************************************************************************
Apex class Name    : CreateCaseRest_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for case related activities
Created On         : March 14 2018
Test Class         : HUMCaseServiceRestTest

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran        215336              03/14/2018          Original Version
*   1.1            Moshitha Gunasekaran        224503              05/02/2018          REQ - 350862 - Fix to process Omni Routing rules for asynchrnous call from ETL API Acess profile
*   1.2            Moshitha Gunasekaran        230876              05/19/2018          Defect fix for 358913 to associate policy for Member
*   1.3            Sunil Bukya                 263423              07/27/2018          REQ - 371288 - Implemented Account Fields platform encryption changes.
*   1.4            Sunil Bukya                                     08/17/2018          REQ - 367786 - Implemented Member ID fields platform encryption changes.
*   1.5            Moshitha Gunasekaran        292111              09/28/2018          REQ - 371378 - TECH- Case Service with Attachment Log backend mapping
*   1.6             Syed Mubarak                                   10/03/2018          Defect #385363.0001 fix 
*   1.7            Moshitha Gunasekaran        294763              10/05/2018          REQ - 375754 - TECH - SF - Textbox copy paste and validation error for unknown member 
*   1.8            Charan Konakandla           314128                  11/22/2018          REQ-377179,REQ-377180 - Implemented Provider and Group Search to populate Interaction details for a correspondance Cases
*   1.9             Joel George                 313879              12/03/2018          REQ 378820 changes
*   2.0             Melkisan Selvaraj           317009              12/06/2018          Modified conditions of Attachment log source system
*   2.1             Rakesh Patni                317871              12/10/2018           REQ-369725
*   2.3             Sunil Bukya                 321985              12/24/2018          Removed Dpass callout code changes.
*   2.4             Shailesh Mali                                   01/30/2019          REQ-380714, Implemented Attachment insert logic on case
*   2.5             Shailesh Mali                                   02/15/2019          Defect - 402416 Fixed.
*   2.6             Rakesh Patni                339813              02/26/2019          Defect-403933.0001 Fix. Allowing Provider Account map(including EPSS callout) to be created ahead of member account map.
*   2.7          Rakesh Patni               344305              03/14/2019          REQ-389695.
*   2.8             Santhi Mandava                                  04/15/2019          Replaced "Member_Id_Value__c" with Name
*   2.9             Moshitha Gunasekaran                            02/04/2020          US754949 - Ability for expanded the population of cases containing Case Initiated Date for Medicare Enrollment processing
*   3.0             Asish Behera                                    07/14/2020          Issue Attempt to null object when policy member has blank end date.
*   3.1             Sumanth Naredla                                 07/17/2020          1226831 - PR00093202 - TECH - Change Request# CR-012 - Image Gap (Long Term Solution - File To to CRM August Requirement)
*   3.2             Moshitha Gunasekaran                            01/08/2021          US1534952 - INC0462428 - CRM Service - Incorrect NPI Populating on CRM Case Upon transfer from MHK / NPI Not Populating on CRM Case
*   3.3             Sagar MN					                    02/10/2021	        US1867250 - Added ETL Record deleted flag to queries
* 	3.4			    Sagar MN										03/04/2021		    US 1999913 Code checkin for MF1
*   3.5             Vaishnavi Manda                                 05/12/2021          US 2163327 MF6 Checkmarx Scan issue fix
*   3.6             Gowthami Thota                                  08/20/2021          User Story 2449505: MF6 - Case Management Checkmarx Security violations - Case Service
------------------------------------------------------------------------------------------------------------------- ------------------------------------------------------------------------------------------*/
global with sharing class CreateCaseRest_D_HUM
{
    private static final Map<String, Schema.RecordTypeInfo> rtypesCache = Schema.SObjectType.Case.getRecordTypeInfosByName();              
    public static final String ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
    
    //To store the encrypted error messages from input request
    private static string accountErrorMsg = '';
    private static string policyErrorMsg  = '';
    private static string caseCommentErrorMsg = '';
    
    //To store the Case RecordID
    private static string caseRecordID = ''; 
    
     //Variables created for Provider Search 
    public static List<ConsumerID__c> consumerLstUpsert = new List<ConsumerID__c>();
    private static Map<String,Account> accountMapToUpsert = new Map<String,Account>();
    private static Map<String ,Account> unknownAccountMap= new Map<String ,Account>();
    public static Boolean bisUnique=false; 

    
    
    /*
    * Method name : createCase
    * Description : This method is used to create a case records in CRM through Restservice from External System.
    * Return Type : CaseServiceRestCreateResponse_DTO_HUM
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM
    */
    
    public static CaseServiceRestCreateResponse_DTO_HUM createCase(CaseServiceRestCreateRequest_DTO_HUM request)
    {
        CaseServiceRestCreateResponse_DTO_HUM response = new CaseServiceRestCreateResponse_DTO_HUM();
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
    * Return Type : CaseServiceRestCreateResponse_DTO_HUM 
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM
    */
    @TestVisible
    private static CaseServiceRestCreateResponse_DTO_HUM createCaseRecord(CaseServiceRestCreateRequest_DTO_HUM caseRequest)
    {
        CaseServiceRestCreateResponse_DTO_HUM caseResponse = new CaseServiceRestCreateResponse_DTO_HUM();
        
        Case oCase = new Case();                                      
                        
        try
        {    
            oCase = assignCaseValues(caseRequest);           
            if(caseRequest.CreateCaseRequest.AsynchronousProcess !=null && caseRequest.CreateCaseRequest.AsynchronousProcess)
            {                               
                caseResponse = insertCaseObj(oCase);

                if(caseResponse.CreateCaseInfoResponse.Success == true)
                {                                                           
                    CreateCaseRest_S_HUM updateJob = new CreateCaseRest_S_HUM(caseRequest, caseRecordID);
                    ID jobID = System.enqueueJob(updateJob);                    
                }               
            }
            else
            {               
                oCase = populateCaseFields(caseRequest, oCase);                                                                                                                       
                caseResponse = insertCaseObj(oCase);
                
                //Insert the Case Comments from the Input Request
                if(!caseRequest.CreateCaseRequest.CaseComments.CaseComment.isEmpty() && caseResponse.CreateCaseInfoResponse.Success == true && (oCase.Id!=NUll || oCase.Id!=''))
                    createCommentsFromInput(caseRequest.CreateCaseRequest.CaseComments.CaseComment, oCase.Id); 
                
                if(NULL != caseRequest.CreateCaseRequest.AttachmentLog && !caseRequest.CreateCaseRequest.AttachmentLog.isEmpty() && caseResponse.CreateCaseInfoResponse.Success == true)
                    createAttachmentFromInput(caseRequest.CreateCaseRequest.AttachmentLog, oCase.Id ,caseRequest);             
                
                if(caseRequest.CreateCaseRequest.Attachment !=null && !caseRequest.CreateCaseRequest.Attachment.isEmpty()  && caseResponse.CreateCaseInfoResponse.Success == true )
                    createAttachment(caseRequest.CreateCaseRequest.Attachment, oCase.Id ,caseRequest);
               
            }                        
        }    
        catch(Exception ex)
        {
                                   
            List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CreateCaseRest_D_HUM','createCaseRecord');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            } 
            caseResponse = getErrorLogInstance('',ex.getMessage(),false);                    
        }  
        return caseResponse;
    }
    
    /*
    * Method name : insertCaseObj
    * Description : This method is used to insert the Case Object.
    * Return Type : CaseServiceRestCreateResponse_DTO_HUM
    * Parameter   : Case Object
    */
    @TestVisible
    private static CaseServiceRestCreateResponse_DTO_HUM insertCaseObj(Case oCase)
    {
        CaseServiceRestCreateResponse_DTO_HUM caseResponseObj = new CaseServiceRestCreateResponse_DTO_HUM();
        CaseServiceRestCreateResponse_DTO_HUM.CreateCaseInfoResponseDTO caseResponse = new CaseServiceRestCreateResponse_DTO_HUM.CreateCaseInfoResponseDTO();
        String caseId = null;
                
        Database.SaveResult result = Database.insert(oCase, false); 
            
        if(result.isSuccess())
        {
            caseId = result.getId();                                    
            List<Case> oCaseReturn = new List<Case>([Select Id,CaseNumber from Case where Id = :caseId]);
            
            if(!oCaseReturn.isEmpty())
            {                  
                caseResponse.CaseNumber = oCaseReturn[0].CaseNumber;
                caseResponse.Success = true;
                caseRecordID = caseId;                              
            }
        }
        else
        {               
            caseResponse.Success = false;
        
            for(Database.Error err : result.getErrors()) 
            { 
                caseResponse.ErrorCode += (String.valueOf(err.getStatusCode()) + '; ');
                caseResponse.ErrorMessage += (err.getMessage() + '; ');             
            }
            logError(result.getErrors(), 'insertCaseObj', 'case', result.getId());                        
        }   
        caseResponseObj.CreateCaseInfoResponse = caseResponse;
        return caseResponseObj;      
    }
    
    /*
    * Method name : assignCasevalues
    * Description : This method is used to assign case request values to insert case record.
    * Return Type : Case 
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM 
    */ 
    @TestVisible
    private static Case assignCaseValues(CaseServiceRestCreateRequest_DTO_HUM caseRequestObject){
        Case objCase; 
        List<Work_Queue_Setup__c> lWQSetup = new List<Work_Queue_Setup__c>(); 
        Boolean wqFieldsEmpty = false;     
        objCase = new Case();
                
        objCase.Origin = caseRequestObject.CreateCaseRequest.Origin;
        objCase.G_A_Rights_Given__c = caseRequestObject.CreateCaseRequest.GARightsGiven;
        objCase.G_A_Reason__c = caseRequestObject.CreateCaseRequest.GAReason;
        objCase.Type = caseRequestObject.CreateCaseRequest.CaseType; 
        objCase.Status = caseRequestObject.CreateCaseRequest.CaseStatus;  
        objCase.Owner_Queue__c = caseRequestObject.CreateCaseRequest.OwnerQueue;  
        objCase.Case_Owner__c =  caseRequestObject.CreateCaseRequest.OwnerQueue;      
        objCase.Service_Center__c  = caseRequestObject.CreateCaseRequest.ServiceCenter;
        objCase.Department__c = caseRequestObject.CreateCaseRequest.Department;
        objCase.Classification_Type__c = caseRequestObject.CreateCaseRequest.ClassificationType;
        objCase.Priority = caseRequestObject.CreateCaseRequest.Priority;
        objCase.Complaint__c = caseRequestObject.CreateCaseRequest.Complaint;
        objCase.Complaint_Reason__c = caseRequestObject.CreateCaseRequest.ComplaintReason;
        objCase.Complaint_Type__c = caseRequestObject.CreateCaseRequest.ComplaintType;
        objCase.Doc_Type__c = caseRequestObject.CreateCaseRequest.DocType;
        objCase.DCN__c = caseRequestObject.CreateCaseRequest.DCN;
        objCase.DCN_Type__c = caseRequestObject.CreateCaseRequest.DCNType;
        objCase.DCN_URL__c = caseRequestObject.CreateCaseRequest.DCNURL;
        objCase.Created_By_Queue__c = caseRequestObject.CreateCaseRequest.CreatedQueue; 
        objCase.LastmodifiedBy_Queue__c = caseRequestObject.CreateCaseRequest.CreatedQueue; 
        objCase.Work_Queue_View_Name__c = caseRequestObject.CreateCaseRequest.WorkQueueViewName; 
        objCase.Topic__c = caseRequestObject.CreateCaseRequest.TopicName;
        objCase.Medicare_Track_ID__c = caseRequestObject.CreateCaseRequest.ExternalTrackId;
        objCase.Pend_Key__c = caseRequestObject.CreateCaseRequest.PendKey;
        objCase.Election_Type_Code__c = caseRequestObject.CreateCaseRequest.ElectionType;       
        if(String.isBlank(objCase.Classification_Type__c)||String.isBlank(objCase.Department__c)||String.isBlank(objCase.Service_Center__c))
        {
            wqFieldsEmpty = true;
        }
        if(String.isNotBlank(objCase.Owner_Queue__c) && wqFieldsEmpty)
        {       
            lWQSetup= [Select name,Department__c,Classification_Type__r.name,Service_Center__c from Work_Queue_Setup__c where name=:objCase.Owner_Queue__c];
        }
        if(!lWQSetup.isEmpty())
        {        
            if(String.isBlank(objCase.Classification_Type__c))
            {
                objCase.Classification_Type__c = lWQSetup[0].Classification_Type__r.name;
            }
            if(String.isBlank(objCase.Department__c))
            {
                objCase.Department__c = lWQSetup[0].Department__c;
            }
            if(String.isBlank(objCase.Service_Center__c))
            {
                 objCase.Service_Center__c = lWQSetup[0].Service_Center__c;
            }           
        }
        if(String.isNotBlank(caseRequestObject.CreateCaseRequest.FollowupDueDate) && String.isNotEmpty(caseRequestObject.CreateCaseRequest.FollowupDueDate))
        {
            objCase.Follow_up_Due_Date__c = Date.parse(caseRequestObject.CreateCaseRequest.FollowupDueDate); 
        }
        if(String.isNotBlank(caseRequestObject.CreateCaseRequest.CaseInitiatedDate))
        {
            objCase.Case_Initiated_Date__c = Date.valueOf(caseRequestObject.CreateCaseRequest.CaseInitiatedDate); 
        }
         List<CTCI_Junction__c> lstCTCIJunction = [SELECT Id, Classification__r.Id, Classification_Type__r.Id, Intent__r.Id FROM CTCI_Junction__c 
                                              WHERE Classification_Type__r.Name = : caseRequestObject.CreateCaseRequest.ClassificationType AND 
                                              Classification__r.Name = : caseRequestObject.CreateCaseRequest.Classification AND 
                                              Intent__r.Name = : caseRequestObject.CreateCaseRequest.Intent 
                                              AND Intent__r.IS_ACTIVE__C= true
                                              AND Classification__r.IS_ACTIVE__C=true AND CTCI_Junction__c.ISACTIVE__C=true];                                                    
        if(lstCTCIJunction.size() > 0)
        {                     
            objCase.CTCI_List__c = lstCTCIJunction[0].Id;
            objCase.CLASSIFICATION_ID__c = lstCTCIJunction[0].Classification__r.Id;
            objCase.INTENT_ID__C = lstCTCIJunction[0].Intent__r.Id;
        }  

        if(!String.isBlank(caseRequestObject.CreateCaseRequest.CaseSubType))
        {
            //set the Case Subtyoe
            objCase.Subtype__c = caseRequestObject.CreateCaseRequest.CaseSubType;
            
            //set the Case RecordType for Medicare cases
            objCase.RecordTypeId = getCaseRecordType(caseRequestObject,null); 
        
        }
        
        return objCase;   
    }
    
    /*
    * Method name : getErrorLogInstance
    * Description : This method will be used retuen the response instance with error related fields.
    * Return Type : CreateCaseInfoResponseDTO
    * Parameter   : String , String , Boolean
    */
    public static CaseServiceRestCreateResponse_DTO_HUM getErrorLogInstance(String sErrorCode,String sErrorMessage,Boolean sSuccess )
    {    
        CaseServiceRestCreateResponse_DTO_HUM caseResponseObj = new CaseServiceRestCreateResponse_DTO_HUM();
        CaseServiceRestCreateResponse_DTO_HUM.CreateCaseInfoResponseDTO caseResponse = new CaseServiceRestCreateResponse_DTO_HUM.CreateCaseInfoResponseDTO();
        caseResponse.Success = sSuccess;
        caseResponse.ErrorMessage =sErrorMessage;
        caseResponse.ErrorCode = sErrorCode;   
        caseResponseObj.CreateCaseInfoResponse = caseResponse;
        return caseResponseObj;
    }
    /*  
    * Method name : getMemberAccountMaps
    * Description : This method returns the Map of Account ID for the respective MedicareID of the CaseRequest. 
    *               First Member is located using the various input parameters received. If no member is located, then Unknown Member is created.
    * Return Type : Map<String,String> (Key is the Interactingtype and the value is Case ID).
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM, Interaction type(String) and Case ID.    
    */
    public static Map<String,String> getMemberAccountMaps(CaseServiceRestCreateRequest_DTO_HUM caseRequestObject, String interactingType, Id caseId ) 
    {    
        Map<String,String> memberAccountIDMap = new Map<String,String>();
        List<Member_Id__c> objMembers = new List<Member_Id__c>();
        String interactingId = null;
        String interactingIdType = null;
        String firstName = '';
        String lastName = '';
        String birthDate = '';
        String stateCode = '';
        String memberID = '';
        
      try {  
            if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))
            {             
                interactingId = caseRequestObject.CreateCaseRequest.InteractingWithId;
                interactingIdType = caseRequestObject.CreateCaseRequest.InteractingWithIdType;            
                birthDate = caseRequestObject.CreateCaseRequest.InteractingWithDOB;
                firstName = caseRequestObject.CreateCaseRequest.InteractingWithFirstName;
                lastName = caseRequestObject.CreateCaseRequest.InteractingWithLastName;
                stateCode = caseRequestObject.CreateCaseRequest.InteractingWithState;
            }
            else if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))
            {
                interactingId = caseRequestObject.CreateCaseRequest.InteractingAboutId;
                interactingIdType = caseRequestObject.CreateCaseRequest.InteractingAboutIdType;
                birthDate = caseRequestObject.CreateCaseRequest.InteractingAboutDOB;
                firstName = caseRequestObject.CreateCaseRequest.InteractingAboutFirstName;
                lastName = caseRequestObject.CreateCaseRequest.InteractingAboutLastName;              
                stateCode = caseRequestObject.CreateCaseRequest.InteractingAboutState;                
            }
            String accountExternalId = '';
            Boolean memberExist = false;
            
            //Check if the input state code is available in the Picklist Options, else overwrite it 'OT' for 'Other State code'
            if(!String.isBlank(stateCode) && !stateCode.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE))
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
                    if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM)) caseRequestObject.CreateCaseRequest.InteractingAboutState = GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE;
                    if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM)) caseRequestObject.CreateCaseRequest.InteractingWithState = GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE;
                    stateCode = GLOBAL_CONSTANT_HUM.OTHER_STATE_CODE;       
                }
            }            
                        
            //For Input ID type is among MedicareID or MedicAid ID or SSN or MemberIDBase  
            if((interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM || interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM  
            || interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM || interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM) && !String.isBlank(interactingId))
            {         
                memberID = locateMember(interactingId, interactingIdType, birthDate, firstName, lastName);
                                                
                if(!String.isBlank(memberID))
                {
                    memberAccountIDMap.put(interactingType ,memberID);
                    memberExist = true;
                }                                                
                else if (interactingType == GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM && interactingIdType != GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM && !String.isBlank(caseRequestObject.CreateCaseRequest.MemberSSN))
                {                    
                    //Locate the account with MemberSSN sent in the input                                                               
                    memberID = locateMember(caseRequestObject.CreateCaseRequest.MemberSSN, GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM, birthDate, firstName, lastName);
                                        
                    if(!String.isBlank(memberID))
                    {
                        memberAccountIDMap.put(interactingType ,memberID);
                        memberExist = true;
                    }                                         
                }                     
            }
            else if(interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM && String.isBlank(interactingId))
            {    
                //Locate Account using FirstName, LastName, DOB and State code from Input
                memberID = locateMemberByName(firstName, lastName, birthDate, stateCode);
                
                if(!String.isBlank(memberID))
                {
                    memberAccountIDMap.put(interactingType ,memberID);
                    memberExist = true;
                }
            }                                
            else if(interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM && !String.isBlank(interactingId))
            {                           
                //Locate Account using the Enterprise ID
                List<Account> oAccount = [SELECT id FROM Account WHERE Enterprise_Id__c = : interactingId AND ETL_Record_Deleted__c = false limit 1];
                if(!oAccount.isEmpty())
                {
                    memberAccountIDMap.put(interactingType, oAccount[0].Id );
                    memberExist = true;
                }                
            }
            else if(interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM && String.isBlank(interactingId) && (String.isNotBlank(firstName) || String.isNotBlank(lastName) || String.isNotBlank(birthDate) || String.isNotBlank(stateCode)))
            {    
                //Locate Account using FirstName, LastName, DOB and State code from Input
                memberID = locateMemberByName(firstName, lastName, birthDate, stateCode);
                
                if(String.isNotBlank(memberID))
                {
                    memberAccountIDMap.put(interactingType ,memberID);
                    memberExist = true;
                }
            }
                                    
            //If No Member is located using input Parameters, then create Unknown Member
            if(!memberExist)
            {               
                if(interactingType == GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM && !String.isBlank(caseRequestObject.CreateCaseRequest.MemberSSN)) 
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SSN  + HUMUtilityHelper.getMD5Hash(caseRequestObject.CreateCaseRequest.MemberSSN);   
                }
                else if(interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM && !String.isBlank(interactingId))
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
                else if ((interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM || interactingIdType == GLOBAL_CONSTANT_HUM.CASESERVICE_UNKNOWNMEMBER_HUM) && String.isBlank(interactingId))
                {
                    accountExternalId = GLOBAL_CONSTANT_HUM.ACCOUNT_EXTERNALID_SFDC;                       
                }                      
               memberAccountIDMap.putAll(CreateCaseRestAccount_D_HUM.createAccount(caseRequestObject,interactingType, accountExternalId,caseId));
              }
          } 
          catch(Exception ex)
          {                                             
             if(caseRequestObject.CreateCaseRequest.AsynchronousProcess)
             {
                HUMExceptionHelper.logErrors(ex,'CreateCaseRest_D_HUM','getMemberAccountMaps');  
             }
             else            
                throw ex;
          }    
         return memberAccountIDMap;        
    }
    /*  
    * Method name : locateProviderByParams
    * Description : This method returns the Account Record for the respective UNknown Provider If any . 
    * Return Type : Account .
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM, Interaction type(string) and Case ID.    
    */
    private static Account locateProviderByParams(string npiid, String sTaxId ,String sProviderClassificaton)
    {
        List<Account> oProviderAccount = new List<Account>();
        Account providerId = new Account();
        
        //query to search for the provider account
        Map<String ,String> requestMap=new Map<String,String>();       
       	requestMap=CaseServiceProvider_v4_H_HUM.serviceInputMap(npiid, sTaxId, sProviderClassificaton);
        oProviderAccount=new CaseServiceProvider_H_HUM().queryAccounts(requestMap);
        
        if(oProviderAccount!=null && oProviderAccount.size()>0)
        {
            providerId = oProviderAccount[0];
        }
        return providerId;
    }
    /*  
    * Method name : getProviderAccountMap
    * Description : This method returns the map of Account ID for the respective Interaction Type. 
    *               First provider is located using the various input parameters received. If no provider is located, then Unknown provider is created.
    * Return Type : map<string,string> (Key is the Interactingtype and the value is Case ID).
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM, Interaction type(string) and Case ID.    
    */  
    @testVisible
    private static map<string,string> getProviderAccountMap(CaseServiceRestCreateRequest_DTO_HUM caseRequestObject, string interactingType, Id caseId)
    {
        Map<String,String> intProviderIDMap=new Map<String,String>();
        CaseServiceRestInteraction_DTO_HUM interactionWrapObj =new CaseServiceRestInteraction_DTO_HUM();
        interactionWrapObj=CreateCaseRest_H_HUM.getInteractionWrapObj(caseRequestObject, interactingType);
        //Locate Provider Account using request parameters
        if(interactionWrapObj.sIntType!=GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)
        {
            accountMapToUpsert.putAll( new CaseServiceProvider_H_HUM().searchInEpassService(interactionWrapObj,interactingType));
            intProviderIDMap.put(interactingType, '');
        }
        if(interactionWrapObj.sIntType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE||accountMapToUpsert.isEmpty()||
          (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null && !bisUnique)||
          (CaseServiceProvider_H_HUM.bisMultipleAcct!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)))
        {
            string accountExternalId = '';
            Account existingAccountId;
            if(String.isNotBlank(interactionWrapObj.sIntID))
            {
                 existingAccountId=locateProviderByParams(interactionWrapObj.sNPIID, interactionWrapObj.sTaxID,interactionWrapObj.sProviderClassification);
            }
            Boolean isError = false;
            if((string.isBlank(interactionWrapObj.sIntID) && (interactionWrapObj.sIntType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE ))||
                accountMapToUpsert.isEmpty()||
                (!accountMapToUpsert.isEmpty() && accountMapToUpsert.get(interactingType)==null && !bisUnique )||
                (CaseServiceProvider_H_HUM.bisMultipleAcct!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)!=null && CaseServiceProvider_H_HUM.bisMultipleAcct.get(interactingType)))
            {
                unknownAccountMap.put(interactingType,CreateCaseRest_H_HUM.getUnknownAccountRecord(interactionWrapObj));
                
            }
            else if(string.isBlank(interactionWrapObj.sIntID) && (interactionWrapObj.sIntType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE ) && existingAccountId!=null)
            {
                unknownAccountMap.put(interactingType,existingAccountId);
               
            }
            else
            {
                isError = true;
            }   
            if(isError) 
            {
                CalloutException e = new CalloutException();
                e.setMessage(GLOBAL_CONSTANT_HUM.ERRORMSG_CASESERVICE);      
                throw e;
            }   
        }
        return intProviderIDMap;
    }
    
    /*  
    * Method name : getAccountMap
    * Description : This method returns Corresponding Account map based on Type of Account provided in Request i.e. (Member,Provider etc). 
    * Return Type : Map<string,string>.
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM caseRequestObject, string interactingType, Id caseId.    
    */      
    public static Map<string,string> getAccountMap(CaseServiceRestCreateRequest_DTO_HUM caseRequestObject, string interactingType, Id caseId) 
    {    
        map<string,string> intIDMap = new map<string,string>();
        String requestIntRecordType = '';
        
        if(interactingType == GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM)
        {
            requestIntRecordType = caseRequestObject.CreateCaseRequest.InteractingAboutType;
        }
        else
        {
            requestIntRecordType = caseRequestObject.CreateCaseRequest.InteractingWithType;
        }
        
        if(string.isNotBlank(requestIntRecordType))
        {
            //Member
            if(requestIntRecordType.contains('Member')) intIDMap = getMemberAccountMaps(caseRequestObject, interactingType, caseId);
            
            //Provider
            if(requestIntRecordType.contains(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE)) intIDMap = getProviderAccountMap(caseRequestObject, interactingType, caseId);
        }
        return intIDMap;     
    }
    
    /*  
    * Method name : locateMemberByName
    * Description : This method is used to locate the Member to be associated with the Case Record.    
    * Return Type : String (Member RecordID)
    * Parameter   : Input String FirstName, String Type, Input Birth Date, Input FirstName and Input LastName
    */
    @TestVisible
    private static String locateMemberByName(String firstName, String lastName, String birthDate, String stateCode) 
    {           
        List<Account> oAccount = new List<Account>();
        String locatedAccountID = '';
        boolean bIsMatching =false;
        String searchTerms=''; 
        if(String.isNotBlank(firstName)) searchTerms = firstName+'*';                                                                                       
        if((String.isNotBlank(lastName) && string.isNotBlank(searchTerms)) )
            searchTerms += ' AND ' + lastName+'*';
        else if((String.isNotBlank(lastName) && string.isBlank(searchTerms)))
            searchTerms += lastName+'*';
        String queryAcct ='';
        IF(string.isNotBlank(searchTerms))
        {
            queryAcct = 'FIND \''+ String.escapeSingleQuotes(searchTerms)+' \' RETURNING Account(id, RecordType.Name,Individual_First_Name__c,Individual_Last_Name__c, Birthdate__c Where ';
            if(String.isNotBlank(stateCode)) queryAcct += ' PersonMailingStateCode = \'' +string.escapeSingleQuotes(stateCode) + '\''+' AND ';
            if(String.isNotBlank(birthDate)) queryAcct += ' Birthdate__c = \'' +string.escapeSingleQuotes(birthDate) + '\''+' AND ';
            queryAcct += 'ETL_Record_Deleted__c = false' + ' AND RecordType.Name in ' + GLOBAL_CONSTANT_HUM.MEMBERIDBASE_CASE_RECCORDTYPE + ' ORDER BY RecordType.Name'+')';        
            
            List<List<Account>> searchList=search.query(queryAcct);
            List<Account> tempAccountList = searchList[0];
           
            If(tempAccountList != Null && !tempAccountList.isEmpty())
            {
                For(Account obj : tempAccountList)
                {
                    bIsMatching = True;
                    If(String.IsNotBlank(firstName))
                    {
                        If(String.IsBlank(obj.Individual_First_Name__c) || (String.IsNotBlank(obj.Individual_First_Name__c) && !obj.Individual_First_Name__c.startsWithIgnoreCase(firstName))) bIsMatching = false;
                    }
                    If(String.IsNotBlank(lastName))
                    {
                        If(String.IsBlank(obj.Individual_Last_Name__c) || (String.IsNotBlank(obj.Individual_Last_Name__c) && !obj.Individual_Last_Name__c.startsWithIgnoreCase(lastName))) bIsMatching = false;
                    }
                    If(bIsMatching)
                    {           
                        oAccount.Add(obj);
                    }
                }
            }
        }
        else if(String.isNotBlank(birthDate))
        {
            queryAcct = 'SELECT id, RecordType.Name FROM Account WHERE ';
            if(String.isNotBlank(stateCode)) queryAcct += ' PersonMailingStateCode = \'' +string.escapeSingleQuotes(stateCode) + '\''+' AND ';
                                             queryAcct += ' Birthdate__c = \'' +string.escapeSingleQuotes(birthDate) + '\''+' AND ';
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
    private static String locateMember(String interactingID, String interactingType, String birthDate, String firstName, String lastName) 
    {                            
        String interactingIdValue = '';             
        String locatedMemberID = '';
        List<Member_Id__c> memberList = new List<Member_Id__c>();
        String typeString = '(\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM + '\',\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM + '\',\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM + '\',\'' + GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM + '\')';
        Map<Id, Member_Id__c> uniqMemberMap = new Map<Id, Member_Id__c>();   // Map of Account ID and Member_ID__c Object. This holds the unique Members returned from the query
        String policyMemberQuery = '';
        
            policyMemberQuery = 'FIND \'' + interactingID + '*'+ '\' IN NAME FIELDS RETURNING Member_Id__c(id, Name, Type__c, Policy_Member__r.Member__c, Policy_Member__r.Member__r.name,Policy_Member__r.Member__r.firstname,' + 
                            'Policy_Member__r.Member__r.lastname, Policy_Member__r.Member__r.Birthdate__c, Policy_Member__r.Member__r.PersonBirthdate WHERE ';
                            
            if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))
                policyMemberQuery += ' Type__c IN ' + typeString ;
            else
                policyMemberQuery += ' Type__c = \'' + interactingType + '\'';  
                                                
            
            policyMemberQuery += ' AND Policy_Member__r.Member__r.ETL_Record_Deleted__c = false'+')';
            List<List<Member_Id__c>>searchList=search.query(policyMemberQuery);
            
            List<Member_Id__c> tempMemberList = searchList[0];
            If(tempMemberList != Null && !tempMemberList.isEmpty())
            {
                For(Member_Id__c obj : tempMemberList)
                {
                    If(obj.Name.startsWithIgnoreCase(interactingIdValue))
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
            if(interactingType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM))
            {
                uniqMemberIdBaseMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_MEMBERIDBASE_HUM, memberList);
                uniqSSNMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_SSN_HUM, memberList);
                uniqMedicareIdMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICAREID_HUM, memberList);
                uniqMedicaidIdMap = createUniqMemberMap(GLOBAL_CONSTANT_HUM.CASESERVICE_MEDICADEID_HUM, memberList);                
                
                if(!uniqMemberIdBaseMap.values().isEmpty() && uniqMemberIdBaseMap.values().size() == 1 )                                
                    locatedMemberID = uniqMemberIdBaseMap.values()[0].Policy_Member__r.Member__c;                  
                else if(!uniqMemberIdBaseMap.values().isEmpty())                    
                    locatedMemberID = findMatchingMember(uniqMemberIdBaseMap.values(), birthDate, firstName, lastName);                     
                    
                if(String.isBlank(locatedMemberID))
                {
                    if(!uniqSSNMap.values().isEmpty() && uniqSSNMap.values().size() == 1 )                                  
                        locatedMemberID = uniqSSNMap.values()[0].Policy_Member__r.Member__c;                    
                    else if(!uniqSSNMap.values().isEmpty())                     
                        locatedMemberID = findMatchingMember(uniqSSNMap.values(), birthDate, firstName, lastName);                                                                        
                }
                
                if(String.isBlank(locatedMemberID))
                {
                    if(!uniqMedicareIdMap.values().isEmpty() && uniqMedicareIdMap.values().size() == 1 )                                
                        locatedMemberID = uniqMedicareIdMap.values()[0].Policy_Member__r.Member__c;                     
                    else if(!uniqMedicareIdMap.values().isEmpty())                      
                        locatedMemberID = findMatchingMember(uniqMedicareIdMap.values(), birthDate, firstName, lastName);                           
                }
                
                if(String.isBlank(locatedMemberID))
                {
                    if(!uniqMedicaidIdMap.values().isEmpty() && uniqMedicaidIdMap.values().size() == 1 )                                
                        locatedMemberID = uniqMedicaidIdMap.values()[0].Policy_Member__r.Member__c;                     
                    else if(!uniqMedicaidIdMap.values().isEmpty())                      
                        locatedMemberID = findMatchingMember(uniqMedicaidIdMap.values(), birthDate, firstName, lastName);                      
                }                                       
            }
            else
            {
                uniqMemberMap = createUniqMemberMap(interactingType, memberList);
                if(!uniqMemberMap.values().isEmpty() && uniqMemberMap.values().size() == 1 )                                
                    locatedMemberID = uniqMemberMap.values()[0].Policy_Member__r.Member__c;                     
                else if(!uniqMemberMap.values().isEmpty())                      
                    locatedMemberID = findMatchingMember(uniqMemberMap.values(), birthDate, firstName, lastName);                                                      
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
    private static String findMatchingMember(List<Member_Id__c> memberList, String birthDate, String firstName, String lastName)
    {
        String matchingMemberId = '';
        for(Member_Id__c member : memberList)
        {                                                           
            Boolean birthDateMatch = birthDate.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.Birthdate__c).trim());
            Boolean firstNameMatch = firstName.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.FirstName).trim());
            Boolean lastNameMatch = lastName.trim().equalsIgnoreCase((member.Policy_Member__r.Member__r.LastName).trim());                                                          
            if(firstNameMatch && lastNameMatch && birthDateMatch)
            {
                matchingMemberId = member.Policy_Member__r.Member__c;
                break;                                                                             
            }                        
        }
        return matchingMemberId;
    }

    /*  
    * Method name : updateCaseAndAccount
    * Description : This method is used to update case and account record in the asynchronous call through Queuable Apex call from CreateCaseRest_S_HUM class.
    *               Member, Policy, Queue and other details are populated on the case record and case is updated. If the Case Update fails, then tries to 
    *               get as much details into Case Record as possible, by updating Member, Policy and other details in separate DMLs.
    * Return Type : Void
    * Parameter   : CreateCaseInfoResponseDTO, Case Id 
    */
    public static void updateCaseAndAccount(CaseServiceRestCreateRequest_DTO_HUM caseRequest, Id caseId) 
    {    
        Case oCase = new case();                
        try
        {              
                         
                oCase.id = caseId;  
                                                                        
            oCase = populateCaseFields(caseRequest, oCase);
                                                                                                                                                                                                            
            if(!caseRequest.CreateCaseRequest.CaseComments.CaseComment.isEmpty() && (oCase.Id!=NUll || oCase.Id!=''))
                createCommentsFromInput(caseRequest.CreateCaseRequest.CaseComments.CaseComment, oCase.Id);
    
            if(NULL != caseRequest.CreateCaseRequest.AttachmentLog && !caseRequest.CreateCaseRequest.AttachmentLog.isEmpty())
                createAttachmentFromInput(caseRequest.CreateCaseRequest.AttachmentLog, oCase.Id ,caseRequest);       
                
             if(caseRequest.CreateCaseRequest.Attachment !=null && !caseRequest.CreateCaseRequest.Attachment.isEmpty())
                createAttachment(caseRequest.CreateCaseRequest.Attachment, oCase.Id ,caseRequest);
             
                     
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
           HUMExceptionHelper.logErrors(ex,'CreateCaseRest_D_HUM','updateCaseAndAccount');                     
        }                   
    }

    /*  
    * Method name : populateCaseFields
    * Description : This method is used to populate case with Member, Policy, RecordType and other details.
    * Return Type : Case
    * Parameter   : CreateCaseInfoResponseDTO, Case 
    */       
    @TestVisible
    private static Case populateCaseFields(CaseServiceRestCreateRequest_DTO_HUM caseRequest, case oCase) 
    {             
        try
        {                                                           
            //populate the errorMessage Static variables from the input request
            accountErrorMsg = caseRequest.CreateCaseRequest.AccountErrMsg; 
            policyErrorMsg = caseRequest.CreateCaseRequest.PolicyErrMsg;
            caseCommentErrorMsg = caseRequest.CreateCaseRequest.CaseCommentErrMsg;
            
            oCase = populateCaseIntWithAbouts(caseRequest, oCase);
            
            //set the Case Subtyoe
            if(Schema.sobjectType.Case.fields.Subtype__c.isUpdateable())
                oCase.Subtype__c = caseRequest.CreateCaseRequest.CaseSubType;
            
            //set the Policy for the Case
            if(oCase.Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.MEMBER && oCase.AccountID != Null)  {
                if(Schema.sObjectType.Case.fields.Policy_Member__c.isUpdateable())            
                    oCase.Policy_Member__c = getMemberPolicy(caseRequest, oCase);  
            }                            
                       
            //set Case OwnerId
            if(caseRequest.CreateCaseRequest.AsynchronousProcess)
            {
               //Fix --Sagar --28/5/2021
                Case[] objCase = [Select OwnerId from Case where id = :oCase.Id Limit 1];
                
                //set Case OwnerId
                if(objCase.size()>0)
                {
                   if(String.isNotBlank(objCase[0].OwnerId))
                    {
                       //Need to check while creating case from Datapower if the OwnerId is not Datapower API and it should be Queue
                       Group[] ownerIdGroup = [select Id from Group where Id = : objCase[0].OwnerId  and Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM Limit 1];
                       if (ownerIdGroup.size()>0)
                       {
                            if(Schema.sobjectType.Case.fields.OwnerId.isUpdateable())
                                oCase.OwnerId = objCase[0].OwnerId;
                       } 
                       else
                       {
                            Group[] ownerIdGrp = [select Id from Group where Name = : caseRequest.CreateCaseRequest.OwnerQueue  and Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM Limit 1];
                            if (ownerIdGrp.size()>0) 
                            {
                                if(Schema.sobjectType.Case.fields.OwnerId.isUpdateable())
                                oCase.OwnerId = ownerIdGrp[0].Id;
                            }
                       }
                 }
                }
                else
                { 
                    Group[] ownerIdGroup = [select Id from Group where Name = : caseRequest.CreateCaseRequest.OwnerQueue  and Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM Limit 1];
                    if (ownerIdGroup.size()>0) 
                    {
                                if(Schema.sobjectType.Case.fields.OwnerId.isUpdateable())
                                oCase.OwnerId = ownerIdGroup[0].Id; 
                    } 
                }
            } 
            else
            {
                Group[] ownerIdGroup = [select Id from Group where Name = : caseRequest.CreateCaseRequest.OwnerQueue  and Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM Limit 1];
                if (ownerIdGroup.size()>0 && Schema.sobjectType.Case.fields.OwnerId.isUpdateable()) oCase.OwnerId = ownerIdGroup[0].Id; 
            }             
        }
        catch(exception ex)
        {           
            if(caseRequest.CreateCaseRequest.AsynchronousProcess)
             {
                HUMExceptionHelper.logErrors(ex,'CreateCaseRest_D_HUM','populateCaseFields');  
             }
            else
                throw ex;
        }                               
        return oCase;               
    }
    /*  
    * Method name : populateCaseIntWithAbouts
    * Description : This method is used to populate Interaction WIth and Interaction About Details for case Created.
    * Return Type : Case
    * Parameter   : CreateCaseInfoResponseDTO, Case 
    */   
    public static Case populateCaseIntWithAbouts(CaseServiceRestCreateRequest_DTO_HUM caseRequest, case oCase) 
    {
        string recordTypeInterWith;
        string recordTypeInterAbout;
        map<string,string> interactingAccountsMap = new map<string,string>(); 
        map<string, HUMCaseInteractionAboutType__c> caseInterAboutTypeMap = HUMCaseInteractionAboutType__c.getAll();  
        List<Account> lstAccount = new List<Account>();
        map<ID, Schema.RecordTypeInfo> recordTypeMap = Schema.SObjectType.Account.getRecordTypeInfosById();
        List<Id> AccIdLst=new List<Id>();
        List<Account> resultAccountLst=new List<Account>();
        //If Provider is Int With and not Int About. Passing Provider into map first to avoid Callout Exception.
        Boolean isProviderIntWithOnly = caseRequest.CreateCaseRequest.InteractingWithType.contains(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE) && !caseRequest.CreateCaseRequest.InteractingAboutType.contains(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE);
        if (isProviderIntWithOnly)
        {
            interactingAccountsMap.putAll(CreateCaseRest_D_HUM.getAccountMap(caseRequest, GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM, oCase.Id));
            if (isProviderIntWithOnly || caseRequest.CreateCaseRequest.InteractingAboutId != caseRequest.CreateCaseRequest.InteractingWithId ||
                (string.isBlank(caseRequest.CreateCaseRequest.InteractingAboutId) && string.isBlank(caseRequest.CreateCaseRequest.InteractingWithId)))
            {
                bisUnique = false;
                interactingAccountsMap.putAll(CreateCaseRest_D_HUM.getAccountMap(caseRequest, GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM, oCase.Id));
            }
            else
            {
                bisUnique = true;
            }
        }
        else 
        {
            interactingAccountsMap.putAll(CreateCaseRest_D_HUM.getAccountMap(caseRequest, GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM, oCase.Id));

            if (caseRequest.CreateCaseRequest.InteractingAboutId != caseRequest.CreateCaseRequest.InteractingWithId ||
                (string.isBlank(caseRequest.CreateCaseRequest.InteractingAboutId) && string.isBlank(caseRequest.CreateCaseRequest.InteractingWithId)))
            {
                bisUnique = false;
                interactingAccountsMap.putAll(CreateCaseRest_D_HUM.getAccountMap(caseRequest, GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM, oCase.Id));
            }
            else
            {
                bisUnique = true;
            }
        }
        if(accountMapToUpsert!=null && !accountMapToUpsert.isEmpty())
        {
            if(accountMapToUpsert!=null && !accountMapToUpsert.isEmpty())
            {
                resultAccountLst=CreateCaseRest_H_HUM.upsertAccountRec(accountMapToUpsert);
            }
            if(resultAccountLst!=null && !resultAccountLst.isEmpty())
            {
                String recType='';
                String InteractionAboutType=caseRequest.CreateCaseRequest.InteractingAboutType;
                String InteractionwithType=caseRequest.CreateCaseRequest.InteractingwithType;
                if(InteractionAboutType.contains(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE))
                {
                    recType=caseRequest.CreateCaseRequest.InteractingAboutType;
                }
                else if(InteractionwithType.contains(GLOBAL_CONSTANT_HUM.KNOWNPROVIDER_CASE_SERVICE))
                {
                    recType=caseRequest.CreateCaseRequest.InteractingwithType;
                }
                if(String.isNotBlank(recType))
                {
                    interactingAccountsMap.putAll(CreateCaseRest_H_HUM.upsertconsumerIds(interactingAccountsMap,accountMapToUpsert,recType));
                }
           } 
        }
        if(unknownAccountMap!=null && !unknownAccountMap.isEmpty())
        {
            interactingAccountsMap.putAll(CreateCaseRest_H_HUM.insertUnknownAccounts(unknownAccountMap,interactingAccountsMap));
        }
        if(!interactingAccountsMap.values().isEmpty())
        {            
            lstAccount = [SELECT Id,RecordTypeId FROM Account WHERE Id IN :interactingAccountsMap.values() and ETL_Record_Deleted__c = false];
        }
        if(lstAccount.size() > 0)
        {
            map<Id,Account> accountMap = new map<Id,Account>(lstAccount);  
            if(Schema.sobjectType.Case.fields.AccountID.isUpdateable())          
                 oCase.AccountID = interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM);
            if(accountMap.containsKey(interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM)))
                recordTypeInterAbout = recordTypeMap.get((accountMap.get(interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM))).RecordTypeId).getName();
            
            if(caseRequest.CreateCaseRequest.InteractingAboutId != caseRequest.CreateCaseRequest.InteractingWithId  || (string.isBlank(caseRequest.CreateCaseRequest.InteractingAboutId) && string.isBlank(caseRequest.CreateCaseRequest.InteractingWithId)))
            {
                if(accountMap.containsKey(interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM)))
                    recordTypeInterWith = recordTypeMap.get((accountMap.get(interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM))).RecordTypeId).getName();
            }
            if(!string.isBlank(recordTypeInterAbout))  {        
                if(Schema.sobjectType.Case.fields.Interacting_About_Type__c.isUpdateable())  
                    oCase.Interacting_About_Type__c = caseInterAboutTypeMap.get(recordTypeInterAbout).CaseInteraction_About_Type__c;
            }
            if(caseRequest.CreateCaseRequest.InteractingAboutId != caseRequest.CreateCaseRequest.InteractingWithId || (string.isBlank(caseRequest.CreateCaseRequest.InteractingAboutId) && string.isBlank(caseRequest.CreateCaseRequest.InteractingWithId))) 
            {
                if(Schema.sobjectType.Case.fields.Interacting_With__c.isUpdateable())
                     oCase.Interacting_With__c = interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTWITHTYPE_HUM);
                if(recordTypeInterWith <> null && recordTypeInterWith <> '')
                {
                    if(Schema.sobjectType.Case.fields.Interacting_With_Type__c.isUpdateable())
                        oCase.Interacting_With_Type__c = caseInterAboutTypeMap.get(recordTypeInterWith).CaseInteraction_About_Type__c;
                }
            }
            else 
            {
                if(Schema.sobjectType.Case.fields.Interacting_With__c.isUpdateable())
                oCase.Interacting_With__c = interactingAccountsMap.get(GLOBAL_CONSTANT_HUM.CASESERVICE_INTABTTYPE_HUM);
                if(String.isNotBlank(recordTypeInterAbout))
                {
                    if(Schema.sobjectType.Case.fields.Interacting_With_Type__c.isUpdateable())
                    oCase.Interacting_With_Type__c = caseInterAboutTypeMap.get(recordTypeInterAbout).CaseInteraction_About_Type__c;
                }
            }                      
        }
        //set the Case RecordType
        if(Schema.sobjectType.Case.fields.RecordTypeId.isUpdateable())
            oCase.RecordTypeId = getCaseRecordType(caseRequest, recordTypeInterAbout);
        return oCase;
    }
    
    /* Method Name: CreateCaseComment
    *
    Description: This method is used to create comment records for case
    Return Type: Nothing
    Parameter: 
    */    
    public static void createCaseComment(String ErrorMessage , ID CaseId )
    {
        CaseComment objCaseCommentErr = new CaseComment();           
        objCaseCommentErr.commentBody = ErrorMessage ;
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
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CreateCaseRest_D_HUM','createCaseComment');
            
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
    private static ID getCaseRecordType(CaseServiceRestCreateRequest_DTO_HUM caseRequest, String recordTypeInterAbout)
    {        
        ID CaseRecTypeID;                               
        //For Medicare Enrollment Cases - To get the list of Subtypes to identify Medicare Enrollment Cases
        Set<String> medicareCaseSubTypeList = new Set<String>();
        try
        {
            List<MedEnroll_CASE_SUBTYPE_HUM__mdt> medicareCaseSubTypes = [SELECT Id, CaseType__c, CaseSubType__c FROM MedEnroll_CASE_SUBTYPE_HUM__mdt where CaseType__c = : caseRequest.CreateCaseRequest.CaseType ];        
            if(medicareCaseSubTypes.size()>0)
            {
                medicareCaseSubTypeList.addAll(medicareCaseSubTypes[GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM].CaseSubType__c.split(','));
            }
                                                    
            //For In Progress Medicare Enrollment Cases 
            if(medicareCaseSubTypeList.contains(caseRequest.CreateCaseRequest.CaseSubType) && caseRequest.CreateCaseRequest.CaseStatus == GLOBAL_CONSTANT_HUM.CASESTATUS_HUM)
            {
                CaseRecTypeID = rtypesCache.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).getRecordTypeId();            
                  
            }
            //For Closed Medicare Enrollment Cases
            else if(medicareCaseSubTypeList.contains(caseRequest.CreateCaseRequest.CaseSubType) && caseRequest.CreateCaseRequest.CaseStatus == GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)
            {
                CaseRecTypeID = rtypesCache.get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId();                
            }
            else if(!String.isBlank(recordTypeInterAbout))            
            {      
                CaseRecTypeID = HUMUtilityHelper.getCaseRecordType(recordTypeInterAbout);
            }
        }
        catch(exception ex)
        {
            if(caseRequest.CreateCaseRequest.AsynchronousProcess)
            {
                HUMExceptionHelper.logErrors(ex,'CreateCaseRest_D_HUM','getCaseRecordType');  
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
    private static ID getMemberPolicy(CaseServiceRestCreateRequest_DTO_HUM caseRequest, Case oCase)
    {                  
        ID memberPolicyID;
        ID activePolicyMemberId;
        ID futurePolicyMemberId;
        try
        {                                          
            if(oCase.RecordTypeId == rtypesCache.get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId() || oCase.RecordTypeId == rtypesCache.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).getRecordTypeId() || 
                oCase.RecordTypeId == rtypesCache.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_MEMBERCASE_HUM).getRecordTypeId() || oCase.RecordTypeId == rtypesCache.get(GLOBAL_CONSTANT_HUM.HUM_CLOSEDMEMBERCASE).getRecordTypeId())
            {
                Date todayDate = System.today();
                Integer numberDaysActive = 0;
                Integer numberDaysFuture = 0;
                List<Policy_Member__c> medPolicyMember = [SELECT Id, Name, Effective_Date__c,Policy__r.Major_LOB__c,Policy__r.Product__r.ProductCode, Member__c, End_Date__c ,Group_Number__c  FROM Policy_Member__c 
                                                          WHERE Member__c = :oCase.AccountID AND Policy_Member__c.ETL_Record_Deleted__c= false ];                       
                
                for(Policy_Member__c policyMember : medPolicyMember)
                {                   
                    if(String.isNotBlank(String.valueOf(policyMember.Effective_Date__c)) && String.isNotBlank(String.valueOf(policyMember.End_Date__c)) && String.isNotBlank(caseRequest.CreateCaseRequest.PolicyMember))
                    {
                        if(caseRequest.CreateCaseRequest.EffectiveDate==policyMember.Effective_Date__c && caseRequest.CreateCaseRequest.EndDate==policyMember.End_Date__c
                               && caseRequest.CreateCaseRequest.PolicyMember.startsWithIgnoreCase(policyMember.Name))
                        {
                            memberPolicyID  = policyMember.id;  
                            break;
                        }   
                    }               
                    else if( GLOBAL_CONSTANT_HUM.MEDICARE_CASE_SERVICE.contains(String.valueOf(policyMember.Policy__r.Major_LOB__c)) && String.valueOf(policyMember.Policy__r.Product__r.ProductCode) =='MED')
                    {
                        Date policystartDate = string.isNotBlank(policyMember.Effective_Date__c) ? date.parse(policyMember.Effective_Date__c):null;      
                        if(policyMember.End_Date__c == GLOBAL_CONSTANT_HUM.MAX_DATE_HUM) 
                            policyMember.End_Date__c = GLOBAL_CONSTANT_HUM.MAX_SFDC_DATE_HUM;
                        
                        Date policyEndDate = string.isNotBlank(policyMember.End_Date__c) ?date.parse(policyMember.End_Date__c):null;      
                        
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
                        if(activePolicyMemberId <>  null)
                            memberPolicyID  = activePolicyMemberId;
                        else if (futurePolicyMemberId <> null)
                            memberPolicyID  = futurePolicyMemberId;
                    }                               
                    
                }       
                 
            }       
        }
        catch(exception ex)
        {
            if(caseRequest.CreateCaseRequest.AsynchronousProcess)
            {
                HUMExceptionHelper.logErrors(ex,'CreateCaseRest_D_HUM','getCaseRecordType');  
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
    private static void createCommentsFromInput(List<String> CaseCommentDTO, ID caseId)
    {
        List<CaseComment> lstCaseComment = new List<CaseComment>();         
        for(string sComment : CaseCommentDTO)
        {
            if(String.isNotBlank(sComment))
            {            
            CaseComment objCaseComment = new CaseComment();           
            objCaseComment.commentBody = sComment;
            if(caseId!=null) objCaseComment.ParentId = caseId;
            lstCaseComment.add(objCaseComment);
            }
        }       
        
        if(lstCaseComment.size() > 0)
        {
        //ExceptionLog Fix -Sagar
             Database.SaveResult[] caseCommentSaveResultList = Database.insert(lstCaseComment,false);                
        	for(Database.SaveResult resultCaseAttach: caseCommentSaveResultList){
                if (!resultCaseAttach.isSuccess()){                    
                  Boolean isError = HUMExceptionHelper.processSaveResults(caseCommentSaveResultList, lstCaseComment, 'CreateCaseRest_D_HUM', 'createCommentsFromInput', 'CaseComment');  
                	if(isError) createCaseComment(caseCommentErrorMsg,caseId);
                }           
        }  
        }
    }
    /* Method Name: createAttachment
    Description: This method is used to insert the attachment receive in input request
    Return Type: void
    Parameter: Input AttachmentDTO, CaseID  */  
    @TestVisible
    private static void createAttachment(List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO> AttachmentDTO, ID caseId ,CaseServiceRestCreateRequest_DTO_HUM caseRequestObject)
    {
          Attachment__c attachmentRec; 
          List<Attachment__c> lstAttachment = new  List<Attachment__c>();  
           for(CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO objAttach : AttachmentDTO){  
           
            if(!string.ISBLANK(objAttach.VersionId) && !string.ISBLANK(objAttach.DocId)){
                if(objAttach.FileName !=null){
                    attachmentRec = new Attachment__c(Name = objAttach.FileName,Related_To_Case__c = caseId,Version_Id__c = objAttach.VersionId, DocId__c = objAttach.DocId,Attachment_URL_Text__c=objAttach.AttachmentUrl);
                    lstAttachment.add(attachmentRec); 
                }
                else{
                    attachmentRec = new Attachment__c(Related_To_Case__c = caseId,Version_Id__c = objAttach.VersionId, DocId__c = objAttach.DocId,Attachment_URL_Text__c=objAttach.AttachmentUrl);
                    lstAttachment.add(attachmentRec); 
                }    
            }

        }
            if(!lstAttachment.isEmpty()){
            Database.SaveResult[] lstResultCaseAttach = Database.Insert(lstAttachment, false);  

            for(Database.SaveResult resultCaseAttach: lstResultCaseAttach){
                if (!resultCaseAttach.isSuccess()){
                    Boolean bIsError = HUMExceptionHelper.processSaveResults(lstResultCaseAttach, lstAttachment, 'CreateCaseRest_D_HUM', 'createAttachment', 'Attachment__c');
                    if(bIsError) createCaseComment(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_ERRORMSG,caseId);
                }
            }
       }
    
    }
    /* Method Name: createAttachmentFromInput
    Description: This method is used to insert the attachment receive in input request
    Return Type: void
    Parameter: Input AttachmentLogDTO, Case ID  */
    @TestVisible
    private static void createAttachmentFromInput(List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO> AttachmentDTO, ID caseId ,CaseServiceRestCreateRequest_DTO_HUM caseRequestObject)
    {
        
        List<Attachment_Log__c> lstAttachmentLog = new List<Attachment_Log__c>();
        
        for(CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO objAttach : AttachmentDTO)
        {
            
            if(String.isNotBlank(objAttach.AttachmentKey) && String.isNotBlank(objAttach.AttachmentType))  
            {  
                Attachment_Log__c objAttachmentLog = new Attachment_Log__c();  
                if(String.isNotBlank(objAttach.AttachmentDescription))
                {            
                    objAttachmentLog.Attachment_Description__c = objAttach.AttachmentDescription;
                }
                objAttachmentLog.Attachment_Key__c = objAttach.AttachmentKey;
                objAttachmentLog.Attachment_Type__c = objAttach.AttachmentType;
                objAttachmentLog.Case__c = caseId;
                //Req 378820
                objAttachmentLog.Source_System__c = objAttach.Source;

                if(String.isNotBlank(objAttach.CreatedBy))
                {
                    objAttachmentLog.Created_By__c = objAttach.CreatedBy;
                }
                else
                {
                    objAttachmentLog.Created_By__c = GLOBAL_CONSTANT_HUM.DatapowerUserName;
                }
                lstAttachmentLog.add(objAttachmentLog);
            }
        } 
        if(!lstAttachmentLog.isEmpty())
        { 
            
            Database.SaveResult[] attachmentLogSaveResultList = Database.insert(lstAttachmentLog,false);    
                        
            
            Boolean bIsError = HUMExceptionHelper.processSaveResults(attachmentLogSaveResultList, lstAttachmentLog, 'CreateCaseRest_D_HUM', 'createAttachmentFromInput', 'Attachment_Log__c');
            if(bIsError)
            { 
                createCaseComment(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_ERRORMESSAGE,caseId);
            }
            else
            {
                map<String,Id> mapattachmentLog = new map<String,Id>();
                for(Attachment_Log__c oattachLog : lstAttachmentLog)
                {
                    mapattachmentLog.put(oattachLog.Attachment_Key__c + '#' + oattachLog.Attachment_Type__c + '#' + oattachLog.Attachment_Description__c, oattachLog.Id);
                }
                
                List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{};
                lstAttachlogItems = createAtachmentLogItem(AttachmentDTO, mapattachmentLog);
                if(!lstAttachlogItems.isEmpty())
                {
                    Database.SaveResult[] lstResultAttachLogItems = Database.insert(lstAttachlogItems,false);
                    Boolean bIsErrorLogItem = HUMExceptionHelper.processSaveResults(lstResultAttachLogItems ,lstAttachlogItems ,'CreateCaseRest_D_HUM','createAttachmentFromInput','Attachment_Log_Item__c');
                    if(bIsErrorLogItem) createCaseComment(GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_ERRORMESSAGE,caseId);
                }
            }
       }
    }
    
     /*  
    * Method name : createAtachmentLogItem
    * Description : This method is used to create List of Attchment log item records.
    * Return Type : List<Attachment_Log_Item__c> 
    * Parameter   : List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentDTO> , map<String,Id>
    */
   private static List<Attachment_Log_Item__c> createAtachmentLogItem(List<CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO> AttachmentDTO, map<String,Id> mapAttachmentLog)
    {
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>();
        
        for(CaseServiceRestCreateRequest_DTO_HUM.AttachmentLogDTO oattachLog : AttachmentDTO)
        {
            if(mapAttachmentLog.containsKey(oattachLog.AttachmentKey + '#' + oattachLog.AttachmentType + '#' + oattachLog.AttachmentDescription))
            {
                for(CaseServiceRestCreateRequest_DTO_HUM.AttachmentAttributeDTO attachAttribute : oattachLog.AttachmentAttribute)
                {
                    Attachment_Log_Item__c attLogItem = new Attachment_Log_Item__c();
                    attLogItem.Name =  attachAttribute.AttachmentName;
                    if(String.isNotBlank(attachAttribute.AttachmentValue))
                    {
                        attLogItem.Value__c = attachAttribute.AttachmentValue;
                    }          
                    attLogItem.Attachment_Log__c = mapAttachmentLog.get(oattachLog.AttachmentKey + '#' + oattachLog.AttachmentType + '#' + oattachLog.AttachmentDescription);
                    lstAttachlogItems.add(attLogItem);
                }
            }
        }
        return lstAttachlogItems;
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
        if(Schema.sobjectType.Case.fields.AccountID.isUpdateable())
        caseForUpdate.AccountID = oCase.AccountID;
        if(Schema.sobjectType.Case.fields.Interacting_About_Type__c.isUpdateable())
        caseForUpdate.Interacting_About_Type__c = oCase.Interacting_About_Type__c;
        if(Schema.sobjectType.Case.fields.Interacting_With__c.isUpdateable())
        caseForUpdate.Interacting_With__c = oCase.Interacting_With__c;
        if(Schema.sobjectType.Case.fields.Interacting_With_Type__c.isUpdateable())
        caseForUpdate.Interacting_With_Type__c = oCase.Interacting_With_Type__c;
        if(Schema.sobjectType.Case.fields.RecordTypeId.isUpdateable())
        caseForUpdate.RecordTypeId = oCase.RecordTypeId;
        if(Schema.sobjectType.Case.fields.Subtype__c.isUpdateable())        
        caseForUpdate.Subtype__c = oCase.Subtype__c;
        
        bSuccess = performCaseUpdate(caseForUpdate, errMsg);
        
        if(bSuccess && oCase.Policy_Member__c <> null)
        {           
            caseForUpdate = new Case();         
            errMsg = PolicyErrorMsg;            
            caseForUpdate.Id = oCase.Id;
            if(Schema.sobjectType.Case.fields.Policy_Member__c.isUpdateable())
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
        if(Schema.sobjectType.Case.fields.OwnerId.isUpdateable())       
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
                        class_name__c = 'CreateCaseRest_D_HUM',
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
                        class_name__c = 'CreateCaseRest_D_HUM',
                        Method_Name__c = methodName,
                        Object_Type__c = sObjectType,
                        Record_ID__c = recId,
                        Type__c = ERROR_APPLICATION));
        }       
        HUMExceptionHelper.saveLog(listErrorLogs, false);
    }

}