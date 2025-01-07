/******************************************************************************************************************
Apex class Name    : CreateCaseRestBulkified_D_V4_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for case related activities
Created On         : March 2019
Test Class         : CreateCaseRestBulkified_T_V4_HUM
**Modification Log
* Developer Name          Code Review #         Date                       Description
* Sagar MN                                      04/24/2019                 Initial Version
* Sagar MN                                      04/24/2019                 REQ-383786 Build Failure
* Asish Behera                                  05/06/2019                 REQ-374762 Create and Interaction case in M service in Aynch
* Sagar MN                                      05/07/2019                 REQ-374762 Asynch process code review chnages.
* Asish Behera                                  05/24/2019                 Defect 418284.0001 fix performance Improvement
* Asish Behera                                  06/04/2019                 REQ - 402755 create closed case as part of service.
* Asish Behera                                  06/06/2019                 REQ - 402755 Link Policymember to case
* Asish Behera                                  06/06/2019                 REQ - 403626  - RecordType map constant Added
* Seema Kaulgi                                  07/04/2019                 REQ - 403811 GBO Watson chat (Group)
* Sagar MN                                      08/08/2019                 Added Parse date function for follow up due date 
* Asish Behera                                  08/24/2019                 medicare default policy linking added.
<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Releases/Release_2019_11_22
* Suraj Patil                                   10/03/2019        		  REQ - 409544 allow consumer to send Int With type=Internal
========================================================================
* Deepankar Binjola								10/04/2019				   REQ - 410518	SF Story to allow case assignment to the agent directly
>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Features/Lego/Feature_2019_11_22_410518_1
* Moshitha Gunasekaran                          02/04/2020                 US754949 - Ability for expanded the population of cases containing Case Initiated Date for Medicare Enrollment processing
* Jasmeen Shangari                              04/29/2020                 Log error in Catch block to resolve Checkmarx error
* Asish Behera                                  05/19/2020                 PR00093202_1080503 Med policy is linked instead of Den issue fix
* Asish Behera                                  05/28/2020                 Invalid date exception for followupdate field handled.
* Sumanth Naredla                               06/30/2020                 1226831 - PR00093202 - TECH - Change Request# CR-012 - Image Gap (Long Term Solution - File To to CRM August Requirement)
* Asish Behera                                  07/08/2020                 Fixed the issue to create multiple case for one interctaing about.
* Asish Behera                                  07/14/2020                 Issue Attempt to null object when policy member has blank end date.
* Asish Behera                                  07/14/2020                 Case Recordtype isssue fix
* Asish Behera                                  08/17/2020                 HP Case Recordtype  DF-1331 fix
* Asish Behera                                  08/22/2020                 HP Case Recordtype Issue Fix
* Mohammed Noor                                 09/01/2020                 US1428444 - PR00088383 - MF6 - Changes done to save the DocumentSource and AlternateDocId on Attachment object.
* Jayesh Solanki                                05/14/2020                 User Story 2271553: T1PRJ0001827 - MF 1 - Quality - Add "Care Manager" as a new Interacting with type value (CRM)
* Vaishnavi Manda                               05/12/2021                 US 2163327 - MF6 CheckMarx scan issue fix
* Sagar MN                                      06/01/2021                 Fix for PolicyMember Display on Case Detail Page 
* Moshitha Gunasekaran                          12/01/2021                 Fix for Classification and Intent without Classification Type 
------------------------------------------------------------------------------------------*/
global with sharing class CreateCaseRestBulkified_D_V4_HUM {

    private static final Map<String, Schema.RecordTypeInfo> rtypesCache = Schema.SObjectType.Case.getRecordTypeInfosByName();              
    public static final String ERROR_APPLICATION = HUMConstants__c.getInstance('ERROR_APPLICATION') != null ? HUMConstants__c.getInstance('ERROR_APPLICATION').StringValue__c : '';
    public static List<CaseDetailResponse_DTO_V4_HUM> caseResponse = new List<CaseDetailResponse_DTO_V4_HUM>();
    private static Boolean isDefaultMedPolicy;
    private Static List<Id> listOfCaseids = new List<Id>();
    //constant map to store all the Account to case recordType map.
    private static final map<String,String> map_AccountRecordType_CaseRecordType = new map<String,String>{'Agent'=> 'Agent/Broker Case','Group'=>'Group Case','Provider'=>'Provider Case','Unknown-Agent'=>'Unknown Case','Unknown-Group'=>'Unknown Case','Unknown-Provider'=>'Unknown Case','Unknown-Member'=>'Unknown Case','Member'=>'Member Case'};
    
    /**
    * Method name : createCases
    * Description : This method is used to create a case records in CRM through Restservice from External System.
    * Return Type : List<CaseDetailResponse_DTO_V4_HUM>
    * Parameter   : list<CreateCaseRequest_DTO_V4_HUM> request, Boolean asynch
    */    
    public static List<CaseDetailResponse_DTO_V4_HUM> createCases(list<CreateCaseRequest_DTO_V4_HUM> request, Boolean bAsynch){             
        
        List<CaseDetailResponse_DTO_V4_HUM> response;       
        
        if(request == null)
        {
            response = getErrorLogInstances(null);
        }       
        else{
            response = createCaseRecords(request, bAsynch);            
        }
        
        return response;    
    }
    
     /*
    * Method name : createCaseRecords
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
    * Return Type : List<CaseDetailResponse_DTO_V4_HUM> 
    * Parameter   : list<CreateCaseRequest_DTO_V4_HUM> request
    */    
    private static List<CaseDetailResponse_DTO_V4_HUM> createCaseRecords(list<CreateCaseRequest_DTO_V4_HUM> request, Boolean bAsynch)
    {                  
        try{                                                 
            Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>> caseCommentsMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>();
            List<CaseDetailRequest_DTO_V4_HUM.CaseComment> caseCommentList = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
            Map<Id,List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>> caseAttachmentLogMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>>();
            List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog> caseAttachmentLogList = new List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>();
            Map<Id,List<CaseDetailRequest_DTO_V4_HUM.Attachment>> caseAttachmentMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.Attachment>>();
            List<CaseDetailRequest_DTO_V4_HUM.Attachment> caseAttachmentList = new List<CaseDetailRequest_DTO_V4_HUM.Attachment>();              
            Map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM> mapOfAssignedCase = new Map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM>();  
            Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM> mapOfAssignedCaseDetail = new Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM>(); 
            List<Case> caseListSync = new List<Case>();
            
            //invoke method to populate case required info
             mapOfAssignedCase = populateCaseRequiredFields(request);
            
            if(!mapOfAssignedCase.isEmpty()){ 
                for(CreateMultipleCaseWrapper oCaseWrap : mapOfAssignedCase.KeySet()){                     
                    CaseDetailRequest_DTO_V4_HUM requestObj = mapOfAssignedCase.get(oCaseWrap).caseCreateRequest;                 
                    caseCommentList=requestObj.CaseComments;                              
                    caseAttachmentLogList=requestObj.AttachmentLog;
                    caseAttachmentList=requestObj.Attachment;
                    
                }
                //populate rest of case fields
                mapOfAssignedCaseDetail = assignCaseValues(mapOfAssignedCase); 
                // inserting case records 
                for(CreateMultipleCaseWrapper caseWrap : mapOfAssignedCaseDetail.keySet()){
                	caseListSync.add(caseWrap.caseRecord);
                }
				if(!caseListSync.isEmpty()){
					caseResponse = insertCaseObjs(caseListSync);
				}
                
                if(bAsynch){
                     CreateCaseRest_S_V4_HUM updateJob = new CreateCaseRest_S_V4_HUM(listOfCaseids,mapOfAssignedCase.values());
                     ID jobID = System.enqueueJob(updateJob);    
                }
                else{
                     for(CaseDetailResponse_DTO_V4_HUM caseResponseObj : caseResponse){               
                        if(caseResponseObj.Success == true && caseResponseObj.CaseId!=''){                                           
                            if(!caseCommentList.isEmpty()){               
                             caseCommentsMap.put(caseResponseObj.CaseId,caseCommentList);                                                                                              
                            }                 
                            if(!caseAttachmentLogList.isEmpty()){                            
                                caseAttachmentLogMap.put(caseResponseObj.CaseId,caseAttachmentLogList);                            
                            }                 
                            if(!caseAttachmentList.isEmpty()){                             
                                caseAttachmentMap.put(caseResponseObj.CaseId,caseAttachmentList);                          
                            }                                                       
                        }                                    
                    }  
                    createCaseComments(caseCommentsMap);                    
                    createAttachmentFromInputs(caseAttachmentLogMap);                    
                    createAttachments(caseAttachmentMap);
                }
            
            }
           
        }catch(Exception ex){
            List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex,'CreateCaseRestBulkified_D_V4_HUM','createCaseRecords');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            } 
            List<String> listOfError = new List<String>();
            listOfError.add(ex.getMessage());
            caseResponse = getErrorLogInstances(listOfError);
            
        }        
        return caseResponse;
    }
    
    /**
    * Method name : populateCaseRequiredFields
    * Description : This method is used to create a case records Asynchronously.
    * Return Type : Map<case,CreateCaseRequest_DTO_V4_HUM>
    * Parameter   : list<CreateCaseRequest_DTO_V4_HUM> caseRequestObject
    */   
    public static Map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM> populateCaseRequiredFields(list<CreateCaseRequest_DTO_V4_HUM> caseRequestObject){        
        
        Map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM> mapOfCaseRequests = new   Map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM>();
        Case objCase; 
        CreateMultipleCaseWrapper caseWrap;
        CaseDetailRequest_DTO_V4_HUM objCaseDetail;
        for(Integer i=0;i< caseRequestObject.size();i++){
            caseWrap = new CreateMultipleCaseWrapper();
            objCaseDetail = caseRequestObject[i].caseCreateRequest;                
            objCase = new Case();                
            if(Schema.sObjectType.Case.fields.Origin.isCreateable())             
                objCase.Origin = objCaseDetail.Origin;
            if(Schema.sObjectType.Case.fields.Status.isCreateable()) 
                objCase.Status = objCaseDetail.CaseStatus;
            if(Schema.sObjectType.Case.fields.AccountId.isCreateable())             
                objCase.AccountId = caseRequestObject[i].sInteractingAbout;
            if(Schema.sObjectType.Case.fields.Interacting_About_Type__c.isCreateable())             
                objCase.Interacting_About_Type__c = caseRequestObject[i].sInteractingAboutType; 
            if(Schema.sObjectType.Case.fields.Interacting_With_Type__c.isCreateable())             
                objCase.Interacting_With_Type__c = caseRequestObject[i].sInteractingWithType;
            //If Internal Type is Internal then putting Interacting with name as it do not have account for rest of the account putting value in Interacting with field
            if(objCase.Interacting_With_Type__c == GLOBAL_CONSTANT_HUM.sInternalAccountType || objCase.Interacting_With_Type__c == 'Care Manager') {
                 if(Schema.sObjectType.Case.fields.Interacting_With_Name__c.isCreateable())
			        objCase.Interacting_With_Name__c = caseRequestObject[i].sInteractingWithName;
            }
            else {
                if(Schema.sObjectType.Case.fields.Interacting_With__c.isCreateable())
                    objCase.Interacting_With__c = caseRequestObject[i].sInteractingWith;
            }
			
            //interaction Id population
            if(Schema.sObjectType.Case.fields.INTERACTION_ID__C.isCreateable()) 
                objCase.INTERACTION_ID__C  = caseRequestObject[i].sInteractionId;
            //record type population.
            if(CreateCaseRestBulkified_D_V4_HUM.map_AccountRecordType_CaseRecordType.containsKey(caseRequestObject[i].sInteractingAboutType)){
            	String ScaseRecordtype = CreateCaseRestBulkified_D_V4_HUM.map_AccountRecordType_CaseRecordType.get(caseRequestObject[i].sInteractingAboutType);
                Map<String,String> mapCaseRecordTypes = GLOBAL_CONSTANT_HUM.CASE_RECORD_TYPES;
                if(GLOBAL_CONSTANT_HUM.HPClassificationTypeset.contains(objCaseDetail.ClassificationType)) {
                	String sCaseRecName = mapCaseRecordTypes.get(ScaseRecordtype);
                    objCase.recordTypeid = Schema.SObjectType.Case.getRecordTypeInfosByName().get(sCaseRecName).getRecordTypeId();
            	}else{
                    if(Schema.sObjectType.Case.fields.recordTypeid.isCreateable())
                        objCase.recordTypeid = Schema.SObjectType.Case.getRecordTypeInfosByName().get(CreateCaseRestBulkified_D_V4_HUM.map_AccountRecordType_CaseRecordType.get(caseRequestObject[i].sInteractingAboutType)).getRecordTypeId(); 
                }             
            }
            //populating Createmultiplecase wrapper
            caseWrap.sCaseIdentifier =  caseRequestObject[i].sCaseIdentifier;
            caseWrap.caseRecord = objCase;
            mapOfCaseRequests.put(caseWrap,caseRequestObject[i]);
        }
        return mapOfCaseRequests;
    }
    
    /*
    * Method name : assignCaseValues
    * Description : This method Is used to assign values.
    * Return Type : Map<case,CaseDetailRequest_DTO_V4_HUM>
    * Parameter   : Map<Case,CreateCaseRequest_DTO_V4_HUM> mapOfCreateCaseRequests
    */
    private static Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM> assignCaseValues( Map<CreateMultipleCaseWrapper,CreateCaseRequest_DTO_V4_HUM> mapOfCreateCaseRequests){ 
        
        list<CreateCaseRequest_DTO_V4_HUM> caseRequestObject = mapOfCreateCaseRequests.values();
        Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM> mapOfCase_CaseDetail = new   Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM>();
        Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM> mapOfCase_CaseDetail_ForPolicyMember = new Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM>();       
        Set<String> setOfCreatedQueue = new Set<String>();
        Set<String> setOfClassification = new Set<String>();
        Set<String> setOfClassificationType = new Set<String>();
        Set<String> setOfDefaultCreatedQueue = new Set<String>();
        Set<String> setOfIntent = new Set<String>();
        Map<String,Work_Queue_Setup__c> mapWorkQueueSetup = new Map<String,Work_Queue_Setup__c>();
        Map<String,string> mapQueueClassType = new Map<String,string>();
        Map<String,CTCI_Junction__c> mapCTCI = new Map<String,CTCI_Junction__c>();
        Map<String,Id> mapRecordType = new Map<String,Id>();
        List<CaseDetailRequest_DTO_V4_HUM> requestList = new List<CaseDetailRequest_DTO_V4_HUM>();
        Case objCase;              
        String sCTCIKey = '';
        Set<String> caseOfOwnerIds = new Set<String>();
        Map<String,Group> mapGroupIds = new Map<String,Group>();
        Map<Id,Id> map_Accountid_PolicyMember = new Map<Id,Id>(); 
        Boolean getPolicyDetails=false;
        Set<String> networkIds = new set<String>();
        map<String,User> mapNetworkIds = new map<String,User>();
		Set<String> dupNetworkIds = new set<String>();
        String ObjcaseDetailCTCIkey = '';
        boolean bSwitch_2854267_1 =  HUMUtilityHelper.isCRMFunctionalityON('2854267_1');
        if(!caseRequestObject.isEmpty()){
            for(CreateCaseRequest_DTO_V4_HUM caseDetail: caseRequestObject){    
                CaseDetailRequest_DTO_V4_HUM requestDTO = caseDetail.caseCreateRequest;
                setOfCreatedQueue.add(requestDTO.CreatedQueue);
                setOfClassification.add(requestDTO.Classification);
                setOfClassificationType.add(requestDTO.ClassificationType);
                setOfIntent.add(requestDTO.Intent); 
                requestList.add(requestDTO);  
                caseOfOwnerIds.add(requestDTO.OwnerQueue);
                if(!String.isEmpty(requestDTO.CaseOwnerId)){
                    networkIds.add(requestDTO.CaseOwnerId);
                }
                if(bSwitch_2854267_1 && String.isNotBlank(requestDTO.Classification) && String.isNotBlank(requestDTO.Intent) && string.isBlank(requestDTO.ClassificationType))
                {
                    setOfDefaultCreatedQueue.add(requestDTO.CreatedQueue);
                }
            }
            if(bSwitch_2854267_1)
            {
                if(!setOfDefaultCreatedQueue.isEmpty())
                {
                    for(Work_Queue_Setup__c workQueue : [SELECT name,Department__c,Classification_Type__r.name,Service_Center__c FROM Work_Queue_Setup__c WHERE name IN:setOfDefaultCreatedQueue]){                
                        setOfClassificationType.add(workQueue.Classification_Type__r.name);  
                        mapQueueClassType.put(workQueue.name,workQueue.Classification_Type__r.name);               
                    }
                }
            }
            for(Work_Queue_Setup__c workQueue : [SELECT name,Department__c,Classification_Type__r.name,Service_Center__c FROM Work_Queue_Setup__c WHERE name IN:setOfCreatedQueue]){                
                mapWorkQueueSetup.put(workQueue.name,workQueue);                 
            }   
            
            for(CTCI_Junction__c ctciObj : [SELECT Id, Classification__r.Id, Classification_Type__r.Id, Intent__r.Id, Intent__r.Name, Classification_Type__r.Name, Classification__r.Name FROM CTCI_Junction__c 
                                            WHERE Classification_Type__r.Name IN : setOfClassificationType AND 
                                            Classification__r.Name IN : setOfClassification AND 
                                            Intent__r.Name IN : setOfIntent 
                                            AND Intent__r.IS_ACTIVE__C= true
                                            AND Classification__r.IS_ACTIVE__C=true AND CTCI_Junction__c.ISACTIVE__C=true]){                                                             
                                                sCTCIkey = ctciObj.Classification_Type__r.Name+GLOBAL_CONSTANT_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR+ctciObj.Classification__r.Name+GLOBAL_CONSTANT_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR+ctciObj.Intent__r.Name;
                                                mapCTCI.put(sCTCIkey,ctciObj); 
                                                                                
                                            }
            if(!caseOfOwnerIds.isEmpty()){
                for(Group groupObj : [select Id,Name from Group where Name IN : caseOfOwnerIds AND Type = : GLOBAL_CONSTANT_HUM.TASKLISTVIEW_QUEUE_HUM]){
                    mapGroupIds.put(groupObj.Name,groupObj);
                }
            }  
            if(networkIds.Size()> 0){
                For(User usr : [Select Id,Name,Network_User_Id__c,IsActive from User where Network_User_Id__c IN :networkIds AND IsActive=true])
                {
                    if(usr.Id!=null){
                        // We are checking whethere mapNetworkIds already has networkID in it, If it already exist we will remove the existing Id from mapNetworkIds
                        if(mapNetworkIds.containsKey(usr.Network_User_Id__c))
						{
							mapNetworkIds.remove(usr.Network_User_Id__c);
					        dupNetworkIds.add(usr.Network_User_Id__c);
						}
						else if(!dupNetworkIds.contains(usr.Network_User_Id__c))
						{
							mapNetworkIds.put(usr.Network_User_Id__c, usr);	
						}
                    }
                }  
            }
            CreateMultipleCaseWrapper caseWrapKey;
            for(CreateMultipleCaseWrapper caseWrap : mapOfCreateCaseRequests.KeySet()){ 
                caseWrapKey = new CreateMultipleCaseWrapper();
                caseWrapKey.sCaseIdentifier = caseWrap.sCaseIdentifier;
                objCase = caseWrap.caseRecord;
                CaseDetailRequest_DTO_V4_HUM objCaseDetail = mapOfCreateCaseRequests.get(caseWrap).caseCreateRequest;
                    if(Schema.sObjectType.Case.fields.G_A_Rights_Given__c.isCreateable())
                    	objCase.G_A_Rights_Given__c = objCaseDetail.GARightsGiven;
                	if(Schema.sObjectType.Case.fields.G_A_Reason__c.isCreateable())
                   	 	objCase.G_A_Reason__c = objCaseDetail.GAReason; 
                	if(Schema.sObjectType.Case.fields.Type.isCreateable())
                    	objCase.Type = objCaseDetail.CaseType;               
                     objCase.Status = objCaseDetail.CaseStatus;    
                    if(Schema.sObjectType.Case.fields.Owner_Queue__c.isCreateable())            
                        objCase.Owner_Queue__c = objCaseDetail.OwnerQueue;                
                    objCase.Case_Owner__c =  objCaseDetail.OwnerQueue;                
                    if(Schema.sObjectType.Case.fields.Service_Center__c.isCreateable())   
                    	objCase.Service_Center__c  = objCaseDetail.ServiceCenter;
                	if(Schema.sObjectType.Case.fields.Department__c.isCreateable())     
                    	objCase.Department__c = objCaseDetail.Department;   
                	if(Schema.sObjectType.Case.fields.Classification_Type__c.isCreateable())     
                    	objCase.Classification_Type__c = objCaseDetail.ClassificationType;                
                	if(Schema.sObjectType.Case.fields.Priority.isCreateable())     
                    	objCase.Priority = objCaseDetail.Priority;  
               	 	if(Schema.sObjectType.Case.fields.Complaint__c.isCreateable())     
                    	objCase.Complaint__c = objCaseDetail.Complaint;       
                	if(Schema.sObjectType.Case.fields.Complaint_Reason__c.isCreateable())     
                    	objCase.Complaint_Reason__c = objCaseDetail.ComplaintReason;
                	if(Schema.sObjectType.Case.fields.Complaint_Type__c.isCreateable())     
                    	objCase.Complaint_Type__c = objCaseDetail.ComplaintType; 
                	if(Schema.sObjectType.Case.fields.Doc_Type__c.isCreateable())     
                    	objCase.Doc_Type__c = objCaseDetail.DocType;  
                	if(Schema.sObjectType.Case.fields.DCN__c.isCreateable())     
                    	objCase.DCN__c = objCaseDetail.DCN;
                	if(Schema.sObjectType.Case.fields.DCN_Type__c.isCreateable())     
                    	objCase.DCN_Type__c = objCaseDetail.DCNType;
                	if(Schema.sObjectType.Case.fields.DCN_URL__c.isCreateable())     
                    	objCase.DCN_URL__c = objCaseDetail.DCNURL;     
                	if(Schema.sObjectType.Case.fields.Created_By_Queue__c.isCreateable())     
                    	objCase.Created_By_Queue__c = objCaseDetail.CreatedQueue;
                	if(Schema.sObjectType.Case.fields.LastmodifiedBy_Queue__c.isCreateable())     
                    	objCase.LastmodifiedBy_Queue__c = objCaseDetail.CreatedQueue;
                	if(Schema.sObjectType.Case.fields.Work_Queue_View_Name__c.isCreateable())     
                    	objCase.Work_Queue_View_Name__c = objCaseDetail.WorkQueueViewName;   
                	if(Schema.sObjectType.Case.fields.Topic__c.isCreateable())     
                    	objCase.Topic__c = objCaseDetail.TopicName;  
                	if(Schema.sObjectType.Case.fields.Medicare_Track_ID__c.isCreateable())     
                    	objCase.Medicare_Track_ID__c = objCaseDetail.ExternalTrackId; 
                	if(Schema.sObjectType.Case.fields.Pend_Key__c.isCreateable())     
                    	objCase.Pend_Key__c = objCaseDetail.PendKey;    
                	if(Schema.sObjectType.Case.fields.Election_Type_Code__c.isCreateable())     
                    	objCase.Election_Type_Code__c = objCaseDetail.ElectionType;               
               
                if(bSwitch_2854267_1 && string.isBlank(objCaseDetail.ClassificationType) && mapQueueClassType != null && !mapQueueClassType.isEmpty()
                    && mapQueueClassType.containsKey(objCaseDetail.CreatedQueue))
                {
                    ObjcaseDetailCTCIkey = mapQueueClassType.get(objCaseDetail.CreatedQueue)+GLOBAL_CONSTANT_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR+objCaseDetail.Classification+GLOBAL_CONSTANT_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR+objCaseDetail.Intent;
                }
                else
                {
                    ObjcaseDetailCTCIkey = objCaseDetail.ClassificationType+GLOBAL_CONSTANT_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR+objCaseDetail.Classification+GLOBAL_CONSTANT_HUM.CASEEDIT_CLASSIFICATION_SEPERATOR+objCaseDetail.Intent;             
                }
                
                if(mapCTCI != null && mapCTCI.containsKey(ObjcaseDetailCTCIkey))
                {    
                    CTCI_Junction__c ctciObjTemp = mapCTCI.get(ObjcaseDetailCTCIkey);               
                        if(Schema.sObjectType.Case.fields.CTCI_List__c.isCreateable())     
                        	objCase.CTCI_List__c = ctciObjTemp.Id;
                    	if(Schema.sObjectType.Case.fields.CLASSIFICATION_ID__c.isCreateable())     
                        	objCase.CLASSIFICATION_ID__c =ctciObjTemp.Classification__r.Id;
                    	if(Schema.sObjectType.Case.fields.INTENT_ID__C.isCreateable())      
                        	objCase.INTENT_ID__C = ctciObjTemp.Intent__r.Id;
                } 
                if(!mapWorkQueueSetup.values().isEmpty())
                {                      
                    if(String.isNotBlank(objCase.Case_Owner__c))
                    {
                        objCase.Owner_Queue__c = mapWorkQueueSetup.get(objCase.Case_Owner__c).name; 
                        if(Schema.sObjectType.Case.fields.Case_Owner__c.isCreateable())  
                            objCase.Case_Owner__c =mapWorkQueueSetup.get(objCase.Case_Owner__c).name;                        
                    }
                    //if(String.isBlank(objCase.Classification_Type__c))
                   // {                        
                        objCase.Classification_Type__c = mapWorkQueueSetup.get(objCase.Case_Owner__c).Classification_Type__r.name;
                    //}
                    if(String.isBlank(objCase.Department__c))
                    {
                        objCase.Department__c =mapWorkQueueSetup.get(objCase.Case_Owner__c).Department__c;
                    }
                    if(String.isBlank(objCase.Service_Center__c))
                    {
                        objCase.Service_Center__c = mapWorkQueueSetup.get(objCase.Case_Owner__c).Service_Center__c;
                    }                        
                }
                if(String.isNotBlank(objCaseDetail.FollowupDueDate) && String.isNotEmpty(objCaseDetail.FollowupDueDate))
                {   
                     if(Schema.sObjectType.Case.fields.Follow_up_Due_Date__c.isCreateable())
                        objCase.Follow_up_Due_Date__c = Date.valueOf(objCaseDetail.FollowupDueDate);
                }
                if(String.isNotBlank(objCaseDetail.CaseInitiatedDate))
		        {
                    if(Schema.sObjectType.Case.fields.Case_Initiated_Date__c.isCreateable())
		                objCase.Case_Initiated_Date__c = Date.valueOf(objCaseDetail.CaseInitiatedDate); 
		        }     
                if(String.isNotBlank(objCaseDetail.CaseSubType))
                {                    
                     if(Schema.sObjectType.Case.fields.Subtype__c.isCreateable())
                         objCase.Subtype__c = objCaseDetail.CaseSubType;                                      
                    mapRecordType = getCaseRecordTypes(requestList,null);             
                    Id idRecord = mapRecordType.get(objCaseDetail.CaseSubType);                    
                    objCase.RecordTypeId = idRecord !=null?idRecord:objCase.RecordTypeId;                
                }  
                if(mapGroupIds!= null && mapGroupIds.containsKey(objCaseDetail.OwnerQueue)){
                    if(Schema.sObjectType.Case.fields.OwnerId.isCreateable())
                        objCase.OwnerId = mapGroupIds.get(objCaseDetail.OwnerQueue).Id;                                                                
                }           
                // We have added third condition to check whether if multiple cases are coming in caserequest we have to bulkify it
                if(mapNetworkIds!=null && mapNetworkIds.size()> 0 && mapNetworkIds.containsKey(objCaseDetail.CaseOwnerId))
                { 
                    objCase.Case_Owner__c = mapNetworkIds.get(objCaseDetail.CaseOwnerId).Name;    
                    objCase.OwnerId = mapNetworkIds.get(objCaseDetail.CaseOwnerId).Id;  
                }
                isDefaultMedPolicy = objCaseDetail.MedicareDefaultPolicy;  
                caseWrapKey.caseRecord = objCase;
                mapOfCase_CaseDetail_ForPolicyMember.put(caseWrapKey,objCaseDetail);                
                //Check for policy details from request
               if(objCaseDetail.MedicareDefaultPolicy || (string.isNotBlank(objCaseDetail.PolicyMember)&& string.isNotBlank(objCaseDetail.EffectiveDate)&& string.isNotBlank(objCaseDetail.EndDate) && string.isNotBlank(objCaseDetail.PolicyGroupID)))
               {
                    getPolicyDetails = true;
                }                            
            } 
        
            // logic to populate policy member
            map_Accountid_PolicyMember = getMemberPolicys(mapOfCase_CaseDetail_ForPolicyMember);
            for(CreateMultipleCaseWrapper caseWrap : mapOfCase_CaseDetail_ForPolicyMember.keySet()){
                    CaseDetailRequest_DTO_V4_HUM objCaseDetail = mapOfCase_CaseDetail_ForPolicyMember.get(caseWrap);
                                         
                    if(!map_Accountid_PolicyMember.isEmpty()){
                        if(map_Accountid_PolicyMember.containsKey(caseWrap.caseRecord.AccountId)){
                            if(getPolicyDetails){
                               caseWrap.caseRecord.Policy_Member__c = map_Accountid_PolicyMember.get(caseWrap.caseRecord.AccountId);  
                            }else{
                                if(Schema.sObjectType.Case.fields.Policy_Member__c.isCreateable())
                                    caseWrap.caseRecord.Policy_Member__c=null; 
                            }
                        }
                    }
                      
                    mapOfCase_CaseDetail.put(caseWrap,objCaseDetail);   
            }  
                     
        }        
        return mapOfCase_CaseDetail;
    }
       
    /*  
    * Method name : updateCase
    * Description : This method is used to update case and account record in the asynchronous call through Queuable Apex call from CreateCaseRest_S_HUM class.
    *               Member, Policy, Queue and other details are populated on the case record and case is updated. If the Case Update fails, then tries to 
    *               get as much details into Case Record as possible, by updating Member, Policy and other details in separate DMLs.
    * Return Type : Void
    * Parameter   : map<Id,CreateCaseRequest_DTO_V4_HUM> caseIDRequestMap
    */
    public static void updateCase(map<Id,CreateCaseRequest_DTO_V4_HUM> caseIDRequestMap) 
    {     
        Map<Case,CreateCaseRequest_DTO_V4_HUM> caseRequestMap = new Map<Case,CreateCaseRequest_DTO_V4_HUM>();
        Map<Id,List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>> caseIdAttachmentlogMap= new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>>();
        Map<Id,List<CaseDetailRequest_DTO_V4_HUM.Attachment>> caseIdAttachmentMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.Attachment>>();
        Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>> caseIdCommentsMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>();        
        Set<Case> cset = new Set<Case>();
        List<Case> updateMemberPolicyList = new List<Case>();        
        Map<Case,CaseDetailRequest_DTO_V4_HUM> map_Case_CaseDetail = new  Map<Case,CaseDetailRequest_DTO_V4_HUM>();       
        
        try{
            
            for(String caseId: caseIDRequestMap.keySet()) {                   
                caseIdAttachmentlogMap.put(caseId,caseIDRequestMap.get(caseId).caseCreateRequest.AttachmentLog);
                caseIdAttachmentMap.put(caseId,caseIDRequestMap.get(caseId).caseCreateRequest.Attachment);
                caseIdCommentsMap.put(caseId,caseIDRequestMap.get(caseId).caseCreateRequest.CaseComments);
                caseRequestMap.put(new case(Id = caseId),caseIDRequestMap.get(caseId));             
            }     
            if(!caseIdAttachmentlogMap.isEmpty()){createAttachmentFromInputs(caseIdAttachmentlogMap);}            
            if(!caseIdAttachmentMap.isEmpty()){createAttachments(caseIdAttachmentMap);}            
            if(!caseIdCommentsMap.isEmpty()){createCaseComments(caseIdCommentsMap);}            
            
        }catch(Exception ex)
        {               
            HUMExceptionHelper.logErrors(ex,'CreateCaseRestBulkified_D_V4_HUM','updateCase');                     
        }            
    }
    
     /*
    * Method name : insertCaseObjs
    * Description : This method is used to insert the Case Object.
    * Return Type : list<CaseDetailResponse_DTO_V4_HUM>
    * Parameter   : List<Case> oCase
    */
    private static list<CaseDetailResponse_DTO_V4_HUM> insertCaseObjs(List<Case> oCase)  
    {                 
        Set<Id> successIds = new Set<Id>();               
        List<CaseDetailResponse_DTO_V4_HUM> createDTOList = new List<CaseDetailResponse_DTO_V4_HUM>();                          
        List<Database.Error> errors  = new List<Database.Error>();  
        Map<Id,list<Database.Error>> mapOfErrors = new Map<Id,List<Database.Error>>();
        
        Database.SaveResult[] result = Database.insert(oCase, false);      
        for (Database.SaveResult sr : result) {          
            if (sr.isSuccess()) {                
                successIds.add(sr.getId()); 
                
            }else{
                for(Database.Error err : sr.getErrors()) {                   
                    mapOfErrors.put(sr.getId(),errors);
                }
            }
        } 
        
        if(!mapOfErrors.isEmpty()){           
            logErrors(mapOfErrors,'insertCaseObjs','Case');
        }
        if(!successIds.isEmpty()){
            
            for(Case oCaseObj : [SELECT Id, CaseNumber FROM Case WHERE Id IN : successIds order by casenumber asc]){                
                CaseDetailResponse_DTO_V4_HUM createDTO = new CaseDetailResponse_DTO_V4_HUM();                
                createDTO.CaseNumber = oCaseObj.CaseNumber;
                createDTO.CaseId = oCaseObj.Id;
                createDTO.Success = true;                
                createDTOList.add(createDTO); 
                listOfCaseids.add(oCaseObj.Id);             
            }            
        }                     
        return createDTOList;
    }
    
     /* Method Name: CreateCaseComments
    *Description: This method is used to create comment records for list of cases
    *Return Type: Void
    *Parameter: Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>> caseIdCommentsMap
    */    
    public static void createCaseComments(Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>> caseIdCommentsMap)
    {  
        
        List<CaseComment> caseCommentsList = new List<CaseComment>();
        List<Database.Error> ErrList = new List<Database.Error>();
        List<Database.Error> errors  = new List<Database.Error>();  
        Map<Id,list<Database.Error>> mapOfErrors = new Map<Id,List<Database.Error>>();  
        
        try
        { 
            for (Id caseId : caseIdCommentsMap.keySet()) {
                for(CaseDetailRequest_DTO_V4_HUM.CaseComment caseComments : caseIdCommentsMap.get(caseId))
                {        
                    CaseComment objCaseComment = new CaseComment();
                        if(Schema.sObjectType.CaseComment.fields.commentBody.isCreateable())
                    		objCaseComment.commentBody = caseComments.CommentBody;
                    	if(Schema.sObjectType.CaseComment.fields.ParentId.isCreateable())
                        	objCaseComment.ParentId = caseId;
                    caseCommentsList.add(objCaseComment);
                }
                
            }             
            Database.SaveResult[] sResult = Database.insert(caseCommentsList, false);
            List<Id> SuccessIds = new List<Id>();
            
            for(Database.SaveResult sr : sResult){
                if (sr.isSuccess()) {   
                    SuccessIds.add(sr.getId());                    
                }else{
                    for(Database.Error err : sr.getErrors()) {                   
                        mapOfErrors.put(sr.getId(),errors);
                    }
                }                  
            }
            
            if(!mapOfErrors.isEmpty()){           
                logErrors(mapOfErrors,'createCaseComments','CaseComment');
            }
            if(!ErrList.isEmpty())
            {
                throw new HUMCustomException('Error in Creating Case Comments: '+ErrList);
            }        
        }
        catch(Exception ex)
        {            
            List<Error_Log__c> errorList = new List<Error_Log__c>(); 
            Error_log__c error = HUMExceptionHelper.logOtherExceptions(ex, 'CreateCaseRestBulkified_D_V4_HUM','createCaseComments');
            
            if(error!=null) 
            { 
                errorList.add(error);
                HUMExceptionHelper.saveLog(errorList, false);
            }            
        } 
    }

     /*  
    * Method name : createAttachmentFromInputs
    * Description : createAttachmentFromInputs
    * Return Type : void
    * Parameter   : Map<Id,List<CaseServiceRestCreateInteraction_DTO_HUM.AttachmentLog>> caseIdAttachmentlogMap
    */ 
    private static void createAttachmentFromInputs(Map<Id,List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>> caseIdAttachmentlogMap)
    {        
        List<Attachment_Log__c> lstAttachmentLog = new List<Attachment_Log__c>();
        List<Id> cseId = new List<Id>();
        Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>> caseIdCommentsMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>();
        List<CaseDetailRequest_DTO_V4_HUM.CaseComment> errMsg = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();
        List<Database.Error> errors  = new List<Database.Error>();  
        Map<Id,list<Database.Error>> mapOfErrors = new Map<Id,List<Database.Error>>();
        
        for(Id caseId: caseIdAttachmentlogMap.KeySet())
        {
            cseId.add(caseId);
            for(CaseDetailRequest_DTO_V4_HUM.AttachmentLog objAttach : caseIdAttachmentlogMap.get(caseId))
            {
                
                if(String.isNotBlank(objAttach.AttachmentKey) && String.isNotBlank(objAttach.AttachmentType))  
                {  
                    Attachment_Log__c objAttachmentLog = new Attachment_Log__c();  
                    if(String.isNotBlank(objAttach.AttachmentDescription))
                    {            
                        if(Schema.sObjectType.Attachment_Log__c.fields.Attachment_Description__c.isCreateable())
                            objAttachmentLog.Attachment_Description__c = objAttach.AttachmentDescription;
                    }
                        if(Schema.sObjectType.Attachment_Log__c.fields.Attachment_Key__c.isCreateable())
                        	objAttachmentLog.Attachment_Key__c = objAttach.AttachmentKey;
                        if(Schema.sObjectType.Attachment_Log__c.fields.Attachment_Type__c.isCreateable())
                            objAttachmentLog.Attachment_Type__c = objAttach.AttachmentType;
                        if(Schema.sObjectType.Attachment_Log__c.fields.Case__c.isCreateable())
                            objAttachmentLog.Case__c = caseId;
                        if(Schema.sObjectType.Attachment_Log__c.fields.Source_System__c.isCreateable()) 
                            objAttachmentLog.Source_System__c = objAttach.Source;
                    
                    if(String.isNotBlank(objAttach.CreatedBy))
                    {
                        if(Schema.sObjectType.Attachment_Log__c.fields.Created_By__c.isCreateable())
                            objAttachmentLog.Created_By__c = objAttach.CreatedBy;
                    }
                    else
                    {
                        objAttachmentLog.Created_By__c = GLOBAL_CONSTANT_HUM.DatapowerUserName;
                        
                    }
                    lstAttachmentLog.add(objAttachmentLog);                    
                }
            } 
        }
        if(!lstAttachmentLog.isEmpty())
        { 
            List<Id> SuccessIds= new List<Id>();
            Database.SaveResult[] attachmentLogSaveResultList = Database.insert(lstAttachmentLog,false);    
            for(Database.SaveResult sr : attachmentLogSaveResultList){
                if (sr.isSuccess()) {   
                    SuccessIds.add(sr.getId());                    
                }else{
                    for(Database.Error err : sr.getErrors()) {                   
                        mapOfErrors.put(sr.getId(),errors);
                    }
                }
            }
            if(!mapOfErrors.isEmpty()){           
                logErrors(mapOfErrors,'createAttachmentFromInputs','Attachment_Log__c');
            }
            Boolean bIsError = HUMExceptionHelper.processSaveResults(attachmentLogSaveResultList, lstAttachmentLog, 'CreateCaseRestBulkified_D_V4_HUM', 'createAttachmentFromInput', 'Attachment_Log__c');
            if(bIsError)
            {                          
                for(CaseDetailRequest_DTO_V4_HUM.CaseComment er: errMsg){
                    er.CommentBody=GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_ERRORMESSAGE;
                    errMsg.add(er);
                } 
                for(ID cid : cseId){                
                    caseIdCommentsMap.put(cid,errMsg);              
                }
                createCaseComments(caseIdCommentsMap);
            }
            else
            {
                map<String,Id> mapattachmentLog = new map<String,Id>();
                for(Attachment_Log__c oattachLog : lstAttachmentLog)
                {
                    mapattachmentLog.put(oattachLog.Attachment_Key__c + '#' + oattachLog.Attachment_Type__c + '#' + oattachLog.Attachment_Description__c, oattachLog.Id);
                }
                
                List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>{};
                    lstAttachlogItems = createAtachmentLogItems(caseIdAttachmentlogMap, mapattachmentLog);
                if(!lstAttachlogItems.isEmpty())
                {
                    Database.SaveResult[] lstResultAttachLogItems = Database.insert(lstAttachlogItems,false);
                    Boolean bIsErrorLogItem = HUMExceptionHelper.processSaveResults(lstResultAttachLogItems ,lstAttachlogItems ,'CreateCaseRest_D_HUM','createAttachmentFromInput','Attachment_Log_Item__c');
                    if(bIsErrorLogItem) createCaseComments(caseIdCommentsMap);
                }
            }
            
        }
    }
    
     /*  
    * Method name : createAtachmentLogItems
    * Description : createAtachment Log
    * Return Type : List<Attachment_Log_Item__c>
    * Parameter   : Map<Id,List<CaseServiceRestCreateInteraction_DTO_HUM.AttachmentLog>> caseIdAttachmentlogMap, map<String,Id> mapAttachmentLog
    */ 
    private static List<Attachment_Log_Item__c> createAtachmentLogItems(Map<Id,List<CaseDetailRequest_DTO_V4_HUM.AttachmentLog>> caseIdAttachmentlogMap, map<String,Id> mapAttachmentLog)
    {
        List<Attachment_Log_Item__c> lstAttachlogItems = new List<Attachment_Log_Item__c>();
        
        for(String caseId: caseIdAttachmentlogMap.KeySet())
        {
            for(CaseDetailRequest_DTO_V4_HUM.AttachmentLog oattachLog : caseIdAttachmentlogMap.get(caseId))
            {
                if(mapAttachmentLog.containsKey(oattachLog.AttachmentKey + '#' + oattachLog.AttachmentType + '#' + oattachLog.AttachmentDescription))
                {
                    for(CaseDetailRequest_DTO_V4_HUM.AttachmentAttribute attachAttribute : oattachLog.AttachmentAttribute)
                    {
                        Attachment_Log_Item__c attLogItem = new Attachment_Log_Item__c();
                        if(Schema.sObjectType.Attachment_Log_Item__c.fields.Name.isCreateable())
                            attLogItem.Name =  attachAttribute.AttachmentName;
                        if(String.isNotBlank(attachAttribute.AttachmentValue))
                        {
                            if(Schema.sObjectType.Attachment_Log_Item__c.fields.Value__c.isCreateable())
                                attLogItem.Value__c = attachAttribute.AttachmentValue;
                        }          
                        if(Schema.sObjectType.Attachment_Log_Item__c.fields.Attachment_Log__c.isCreateable())
                            attLogItem.Attachment_Log__c = mapAttachmentLog.get(oattachLog.AttachmentKey + '#' + oattachLog.AttachmentType + '#' + oattachLog.AttachmentDescription);
                        lstAttachlogItems.add(attLogItem);
                    }
                }
            }
        }
        return lstAttachlogItems;
    }
    
     /*  
    * Method name : createAttachments
    * Description : create attachemnts
    * Return Type : void
    * Parameter   : Map<Id,List<CaseServiceRestCreateInteraction_DTO_HUM.Attachment>> 
    */
    private static void createAttachments(Map<Id,List<CaseDetailRequest_DTO_V4_HUM.Attachment>> caseIdAttachmentMap)
    {
        Attachment__c attachmentRec; 
        List<Id> cseId = new List<Id>();
        Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>> caseIdCommentsMap = new Map<Id,List<CaseDetailRequest_DTO_V4_HUM.CaseComment>>();
        List<CaseDetailRequest_DTO_V4_HUM.CaseComment> errMsg = new List<CaseDetailRequest_DTO_V4_HUM.CaseComment>();       
        List<Attachment__c> lstAttachment = new  List<Attachment__c>();
        List<Database.Error> errors  = new List<Database.Error>();  
        Map<Id,list<Database.Error>> mapOfErrors = new Map<Id,List<Database.Error>>();        
        
        for(String caseId: caseIdAttachmentMap.KeySet())
        {
            for(CaseDetailRequest_DTO_V4_HUM.Attachment objAttach : caseIdAttachmentMap.get(caseId))
            {
                if(string.ISNOTBLANK(objAttach.AttachmentUrl) || string.ISNOTBLANK(objAttach.VersionNumber) && string.ISNOTBLANK(objAttach.DocId))
                {
                    if(objAttach.FileName !=null)
                    {
                        attachmentRec = new Attachment__c(Name = objAttach.FileName,Related_To_Case__c = caseId,Version_Id__c = objAttach.VersionNumber,
                                                          DocId__c = objAttach.DocId,Attachment_URL_Text__c=objAttach.AttachmentUrl,Document_Source__c=objAttach.DocumentSource,AlternateDocId__c=objAttach.AlternateDocId);
                        lstAttachment.add(attachmentRec); 
                    }
                    else{
                        attachmentRec = new Attachment__c(Related_To_Case__c = caseId,Version_Id__c = objAttach.VersionNumber, DocId__c = objAttach.DocId,
                                                          Attachment_URL_Text__c=objAttach.AttachmentUrl,Document_Source__c=objAttach.DocumentSource,AlternateDocId__c=objAttach.AlternateDocId);
                        lstAttachment.add(attachmentRec); 
                    }                   
                }
            }
        }   
        
        if(!lstAttachment.isEmpty()){
            
            Database.SaveResult[] lstResultCaseAttach = Database.Insert(lstAttachment, false); 
            List<Id> SuccessIds= new List<Id>();            
            for(Database.SaveResult sr : lstResultCaseAttach){
                if (sr.isSuccess()) {   
                    SuccessIds.add(sr.getId());                    
                }else{
                    for(Database.Error err : sr.getErrors()) {                   
                        mapOfErrors.put(sr.getId(),errors);
                    }
                }
            }
            if(!mapOfErrors.isEmpty()){           
                logErrors(mapOfErrors,'createAttachments','Attachment__c');
            }
            Boolean bIsError = HUMExceptionHelper.processSaveResults(lstResultCaseAttach, lstAttachment,'CreateCaseRestBulkified_D_V4_HUM', 'createAttachment', 'Attachment__c');
            if(bIsError){
                
                for(CaseDetailRequest_DTO_V4_HUM.CaseComment er: errMsg){
                    er.CommentBody=GLOBAL_CONSTANT_HUM.CASESERVICE_ATTACHMENT_ERRORMESSAGE;
                    errMsg.add(er);
                }             
                
                for(ID cid : cseId){
                    caseIdCommentsMap.put(cid,errMsg);              
                }                
                createCaseComments(caseIdCommentsMap);
            }
        }        
    }
    
      /*
    * Method name : getErrorLogInstances
    * Description : This method will be used return the response instance with error related fields.
    * Return Type : List<CaseDetailResponse_DTO_V4_HUM>
    * Parameter   : List<String> sErrorMessage
    */
    public static List<CaseDetailResponse_DTO_V4_HUM> getErrorLogInstances(List<String> sErrorMessage)
    {          
        CaseDetailResponse_DTO_V4_HUM createResObj;
        List<CaseDetailResponse_DTO_V4_HUM> listResponse = new List<CaseDetailResponse_DTO_V4_HUM>();
        if(sErrorMessage !=null && !sErrorMessage.isEmpty()){  
            createResObj = new CaseDetailResponse_DTO_V4_HUM();                          
            createResObj.Success= False;
            createResObj.ErrorMessage = GLOBAL_CONSTANT_HUM.Error_Message_UpdateCasev4; 
            createResObj.ErrorCode = '400';  
            listResponse.add(createResObj);            
        }       
        
        return listResponse;
    }       
    
    /* Method Name: getCaseRecordTypes
    *Description: This method is used figure out the Case RecordType
    *Return Type: Map<String,Id>
    *Parameter: Input Case Request and Account RecordTypeName
    */
    private static Map<String,Id> getCaseRecordTypes(List<CaseDetailRequest_DTO_V4_HUM> requestList,String recordTypeInteractionAbout)
    {      
        ID CaseRecTypeID;                              
        Set<String> medicareCaseSubTypeList = new Set<String>();
        Map<String,Id> mapRetRecordId = new Map<String,Id>();
        List<MedEnroll_CASE_SUBTYPE_HUM__mdt> medicareCaseSubTypes;
        List<String> caseReqValues = new List<String>();
        
        if(!requestList.isEmpty()){        
            for(CaseDetailRequest_DTO_V4_HUM caseRequest : requestList){
                caseReqValues.add(caseRequest.CaseType);
            }
            
            medicareCaseSubTypes = [SELECT Id, CaseType__c, CaseSubType__c FROM MedEnroll_CASE_SUBTYPE_HUM__mdt where CaseType__c IN :caseReqValues];                               
            
            for(CaseDetailRequest_DTO_V4_HUM caseRequest : requestList){                 
                if(medicareCaseSubTypes.size()>0)
                {
                    medicareCaseSubTypeList.addAll(medicareCaseSubTypes[GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM].CaseSubType__c.split(','));
                }
                //For In Progress Medicare Enrollment Cases 
                if(medicareCaseSubTypeList.contains(caseRequest.CaseSubType) && caseRequest.CaseStatus == GLOBAL_CONSTANT_HUM.CASESTATUS_HUM)
                {
                    CaseRecTypeID = rtypesCache.get(GLOBAL_CONSTANT_HUM.MEDICARE_CASE).getRecordTypeId();            
                }
                //For Closed Medicare Enrollment Cases
                else if(medicareCaseSubTypeList.contains(caseRequest.CaseSubType) && caseRequest.CaseStatus == GLOBAL_CONSTANT_HUM.CASEEDIT_CLOSED_HUM)
                {
                    CaseRecTypeID = rtypesCache.get(GLOBAL_CONSTANT_HUM.CLOSED_MEDICARE_CASE).getRecordTypeId();                
                }
                else if(!String.isBlank(recordTypeInteractionAbout))            
                {      
                    CaseRecTypeID = HUMUtilityHelper.getCaseRecordType(recordTypeInteractionAbout);
                }       
                mapRetRecordId.put(caseRequest.CaseSubType,CaseRecTypeID);
            }       
        }        
        return mapRetRecordId; 
    } 
    
    /* Method Name: logErrors
    *Description: This method is to Log errors.
    *Return Type: void
    *Parameter: Map<Id,list<Database.Error>> mapOfErrors, String methodName, String sObjectType
    */
    public static void logErrors(Map<Id,list<Database.Error>> mapOfErrors, String methodName, String sObjectType)
    {
        Database.Error error;
        List < Error_Log__c > listErrorLogs = new List < Error_Log__c >();
        
        for(Id errId : mapOfErrors.Keyset()){                
            for(Database.Error err : mapOfErrors.get(errId))
            {
                listErrorLogs.add(new Error_Log__c(Error_Code__c = String.valueOf(err.getStatusCode()),
                                                   Error_Message__c = err.getMessage(),
                                                   class_name__c = 'CreateCaseRestBulkified_D_V4_HUM',
                                                   Method_Name__c = methodName,
                                                   Object_Type__c = sObjectType,
                                                   Record_ID__c = errId,
                                                   Type__c = ERROR_APPLICATION));
            }  
        }
        HUMExceptionHelper.saveLog(listErrorLogs, false);
    }
    
     /* Method Name: getMemberPolicys
    *Description: This method is used get the Policy of the Member
    *Return Type: Map<Id,Id>
    *Parameter: Map<Case,CaseDetailRequest_DTO_V4_HUM> sRequest
    */    
    private static Map<Id,Id> getMemberPolicys(Map<CreateMultipleCaseWrapper,CaseDetailRequest_DTO_V4_HUM> map_Case_CaseDetailRequest){
        Map<Id,Id> policyMemberMap = new Map<Id,Id>();
        Set<Id> setOfAccount = new Set<Id>();
        ID activePolicyMemberId;
        ID futurePolicyMemberId;
        Date todayDate = System.today();
        Integer numberDaysActive = 0;
        Integer numberDaysFuture = 0;
        
        if(!map_Case_CaseDetailRequest.isEmpty()){
            for(CreateMultipleCaseWrapper oCaseWrap : map_Case_CaseDetailRequest.keySet()){
                       setOfAccount.add(oCaseWrap.caseRecord.AccountId);
            }
            if(!setOfAccount.isEmpty()){
                for(Policy_Member__c policyMember : [SELECT Id, Name, Effective_Date__c,Policy__r.Major_LOB__c,Policy__r.Product__r.ProductCode, Member__c, End_Date__c,
                                                     Group_Number__c,Policy__r.Group_Name__r.Source_Customer_Key__c
                                                     FROM Policy_Member__c 
                                                     WHERE Member__c = :setOfAccount AND Policy_Member__c.ETL_Record_Deleted__c= false]){
                                                     
                     for(CaseDetailRequest_DTO_V4_HUM caseRequest : map_Case_CaseDetailRequest.Values()){     
                         if(policyMember.Effective_Date__c!=Null && policyMember.End_Date__c!=Null && caseRequest.PolicyMember!=Null &&
                            caseRequest.EffectiveDate==String.valueOf(policyMember.Effective_Date__c) && caseRequest.EndDate==String.valueOf(policyMember.End_Date__c) && caseRequest.PolicyMember.equals(policyMember.Name) && caseRequest.PolicyGroupID.equals(policyMember.Policy__r.Group_Name__r.Source_Customer_Key__c))
                         {                          
                             policyMemberMap.put(policyMember.Member__c,policyMember.Id);                                
                             
                         }else if(isDefaultMedPolicy && GLOBAL_CONSTANT_HUM.MEDICARE_CASE_SERVICE.contains(policyMember.Policy__r.Major_LOB__c) && policyMember.Policy__r.Product__r.ProductCode ==GLOBAL_CONSTANT_HUM.MED)
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
                             if(activePolicyMemberId <>  null){
                                 policyMemberMap.put(policyMember.Member__c,activePolicyMemberId);                                    
                             }
                             else if (futurePolicyMemberId <> null){
                                 policyMemberMap.put(policyMember.Member__c,futurePolicyMemberId);                                    
                             }
                             
                         }
                         break;         
                     }
                                                     }               
            }
        }       
        return policyMemberMap;
    }
    
}