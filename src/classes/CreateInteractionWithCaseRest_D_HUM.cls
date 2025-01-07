/******************************************************************************************************************
Apex class Name    : CreateInteractionWithCaseRest_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for Interaction related activities
Created On         : March 07 2019
Test Class         : CreateInteractionWithCaseRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar                           03/07/2019           Original Version
*   2.0            Lakshmi Vutukuri                              04/23/2019           Added unknown member creation part
*   3.0            Asish                                         05/06/2019           Create Case Signeture change
*   3.0            Prasanthi Kandula            355010           05/07/2019           Updated code to handle unknown Member And Provider data from service
*   5.0            Pallavi Ravishankar          354791           05/08/2019           REQ - 377486 - UpdateInteraction CaseInteraction changes
*   6.0            Pallavi Ravishankar          355713           05/13/2019           415771.0001 - Case Interacting about Population issue  
*   7.0            Prasanthi Kandula            355817           05/13/2019           REQ - 399198 - Asynchronous call for WATSON
*   8.0            Prasanthi Kandula            357233           05/24/2019           Passing parameter as true to createCases method, which creates cases asynchronously 
*   9.0            Prasanthi Kandula                             06/25/2019           Map Interaction id to case
*   10.0           Seema Kaulgi                                  07/04/2019           REQ - 403811 GBO Watson chat (Group) 
*   11.0           Asish                                         07/08/2020           Fixed the issue to create multiple case for one interctaing about 
*   12.0           Sagar MN					                     02/10/2021	          US1867250 - Added ETL Record deleted flag to queries
*   13.0           Vaishnavi Manda                               05/12/2021           US2163327 - MF6 Checkmarx scan issue fix
*   14.0           Sagar MN                                      05/21/2021           MF6 - INC1014026 - intermittent 500 internal server error from CRM service for interaction creation
*   15.0           Moshitha Gunasekaran                          07/05/2021           US2446677 - Salesforce to Softphone Case Interaction Web Services 
*   16.0           Moshitha Gunasekaran                          08/05/2021           US2457004 - Populated Authenticated_c field for Softphone interaction
*   17.0           Moshitha Gunasekaran                          10/12/2021           US2772502 - INC1404789 - PLT  : Unable to view interactions with CRM when created via Softphone
*   18.0           Moshitha Gunasekaran                          10/12/2021           US2772502 - INC1398571 - PLT  : Add switch for incidentn ticket
------------------------------------------------------------------------------------------------------------------- */
global with sharing class CreateInteractionWithCaseRest_D_HUM {

 private static final string RECORDTYPE_MEMBER = GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM;
 private static map<string, map<string, set<string>>> mapMemberDependentCode= new map<string, map<string, set<string>>>();
 private static Map<String,Account> accountMapToUpsert = new Map<String,Account>();
 private static map<string, string> mapInteractingWithAccount = new map<string, string>();
 private static map<string, string> mapInteractAboutAccount = new map<string, string>();
 private static map<string,set<string>> mapAcctIDValuesbyType = new map<string,set<string>>();
 private static string sIntAbout = 'Interacting_About';
 private static string sIntWith = 'Interacting_With';
 private static string CreateInteractionError = 'There are no fields present to create Interaction.';
 private static CreateInteractionRequest_DTO_HUM interactionRequestDTO = new CreateInteractionRequest_DTO_HUM();
 private static map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>> mapIntWithAboutUnknownCriteria = new map<string, list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>>();
 private static Map<String,Map<String,String>> mapProviderDetails = new Map<String,Map<String,String>>();
 private static Map<String,Map<String,String>> mapAgentDetails = new Map<String,Map<String,String>>();
 private static Map<String,Map<String,String>> mapUnknownmemberdetails = New Map<String,Map<String,String>>();
 private static Map<String,Map<String,String>> mapGroupDetails = new Map<String,Map<String,String>>();
 private static list<CreateCaseRequest_DTO_V4_HUM> listOfMultiCaseCreateRequest_DTO_HUM = New list<CreateCaseRequest_DTO_V4_HUM>();  
 private static map<string,map<string,string>> mapUnkwnMbrFrmRequest = New map<string,map<string,string>>();  
 private static Id interactionRecordID;
 private static map<string,string> mapRecordType = new map<string,string>
                                                 {
                                                   GLOBAL_CONSTANT_HUM.MEMBER  => GLOBAL_CONSTANT_HUM.MEMBER,
                                                   GLOBAL_CONSTANT_HUM.PROVIDER_TYPE => GLOBAL_CONSTANT_HUM.PROVIDER_TYPE,
                                                   GLOBAL_CONSTANT_HUM.CLOSEDCASE_AGENTBROKER_HUM => GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTAGENTTYPE_HUM,
                                                   GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNMEMBER_HUM => GLOBAL_CONSTANT_HUM.CASEEDIT_INT_ABOUT_TYPE_UNKNOWN_MEMBER,
                                                   GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNAGENTRECORDTYPE_HUM => GLOBAL_CONSTANT_HUM.HUMUnknownAgentIntType,
                                                   GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNPROVIDERRECORDTYPE_HUM => GLOBAL_CONSTANT_HUM.HUMUnknownProviderIntType,
                                                   GLOBAL_CONSTANT_HUM.HUMGroupIntType  => GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM ,
                                                   GLOBAL_CONSTANT_HUM.UNKNOWNACCOUNT_UNKNOWNGROUPRECORDTYPE => GLOBAL_CONSTANT_HUM.HUMUnknownGroupIntType 
                                                 };  
 
  /*
    * Method name : createInteraction
    * Description : This method is used to createinteraction records in CRM through Restservice from External System.
    * Return Type : CreateInteractionResponse_DTO_HUM
    * Parameter   : CreateInteractionRequest_DTO_HUM
    */
    public static CreateInteractionResponse_DTO_HUM createInteraction(CreateInteractionRequest_DTO_HUM request)
    {
       CreateInteractionResponse_DTO_HUM response = new CreateInteractionResponse_DTO_HUM();
        if(request == null)
        {
           response = CaseInteractionSearch_H_HUM.getErrorLogInstance(null,GLOBAL_CONSTANT_HUM.HUMCaseCreateMandatoryError,false);
        }
        else
        {  
           response = verifyAsynchronousFlag(request); 
        }
        return response;    
    }

    /*
    * Method name : createCallInteraction
    * Description : This method is used to createinteraction records in CRM through Restservice from Call.
    * Return Type : CreateInteractionResponse_DTO_HUM
    * Parameter   : CreateInteractionRequest_DTO_HUM
    */
    public static CallInteractionResponse_DTO_HUM createCallInteraction(CallInteractionRequest_DTO_HUM request)
    {
        CallInteractionResponse_DTO_HUM response = new CallInteractionResponse_DTO_HUM();
        try 
        {
            
            //If request is not retieved then error message will be sent
            if(request == null)
            {
                response.Success = false;
                response.ErrorMessage = CreateInteractionError;
            }
            else
            { 
                //Assign Interaction values in synchronous way
                Interaction__c oInteraction = new Interaction__c(); 
                if (Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable()) oInteraction.Interaction_Origin__c  = request.InteractionOrigin;
                if (Schema.sObjectType.Interaction__c.fields.UUID__c.isCreateable()) oInteraction.UUID__c = request.UUID;
                if (Schema.sObjectType.Interaction__c.fields.Call_Category__c.isCreateable())  oInteraction.Call_Category__c = request.CallCategory;
                if (Schema.sObjectType.Interaction__c.fields.Call_Category_Description__c.isCreateable()) oInteraction.Call_Category_Description__c = request.CallCategoryDescription;
                if (Schema.sObjectType.Interaction__c.fields.Caller_Name__c.isCreateable()) oInteraction.Caller_Name__c = request.InteractingFirstName + ' ' + request.InteractingLastName;
                if (Schema.sObjectType.Interaction__c.fields.Call_Data__c.isCreateable()) oInteraction.Call_Data__c = request.CallData;
                if (Schema.sObjectType.Interaction__c.fields.Authenticated__c.isCreateable()) oInteraction.Authenticated__c = request.IsAuthenticated;
                //If logged in UserId is passed then system values will be inserted with logged in userid to identify who created the interaction
                if(string.isNotBlank(request.CallUserId))
                {
                list<User> lstUsr = [Select Id,Name, Current_Queue__c,Network_User_Id__c,IsActive from User where Network_User_Id__c = :request.CallUserId AND IsActive=true];
                if(null != lstUsr && !lstUsr.isEmpty())
                    {
                        boolean bSwitch_2772502 =  HUMUtilityHelper.isCRMFunctionalityON('2772502');
                        if (Schema.sObjectType.Interaction__c.fields.CreatedById.isCreateable()) ointeraction.CreatedById = lstUsr[0].Id;
                        if(bSwitch_2772502)
                        {
                            if (Schema.sObjectType.Interaction__c.fields.OwnerId.isCreateable()) ointeraction.OwnerId = lstUsr[0].Id;
                        }
                        if (Schema.sObjectType.Interaction__c.fields.Created_By_Queue__c.isCreateable()) ointeraction.Created_By_Queue__c  = lstUsr[0].Current_Queue__c;
                        if (Schema.sObjectType.Interaction__c.fields.LastModifiedById.isCreateable()) ointeraction.LastModifiedById = lstUsr[0].Id;
                    }  
                }
                //Insert interaction values and send the response
                response = insertCallInteractionObj(oInteraction);
                //Assign Interaction With and Interaction About values in Queue to handle in asynchronous call
                if(response.Success == true)
                {   
                    CreateInteractionRest_S_HUM updateJob = new CreateInteractionRest_S_HUM (request, interactionRecordID);
                    ID jobID = System.enqueueJob(updateJob);                   
                } 
            }
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CreateInteractionWithCaseRest_D_HUM','createCallInteraction');
            response.Success = false; 
            response.ErrorMessage = ex.getMessage();
        }
        return response;    
    }


    /*
    * Method name : verifyAsynchronousFlag
    * Description : This method is used to check RequiredCasesinResponse boolean value from request. 
                    If the boolean value is true case numbers are returned in request
                    If the boolean value is false case numbers are not returned
    * Return Type : CreateInteractionResponse_DTO_HUM
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM
    */
    private static CreateInteractionResponse_DTO_HUM verifyAsynchronousFlag(CreateInteractionRequest_DTO_HUM interactionRequest)
    {
        Interaction__c oInteraction = new Interaction__c();
        CreateInteractionResponse_DTO_HUM intResponse = new CreateInteractionResponse_DTO_HUM();
        if(!interactionRequest.CreateInteractionRequest.RequiredCasesInResponse)
        {            
            oInteraction.Interaction_Origin__c  = interactionRequest.CreateInteractionRequest.InteractionOrigin;
            intResponse = insertInteractionObj(oInteraction);
            if(intResponse.CreateInteractionInfo.Success == true)
            {   
                CreateInteractionRest_S_HUM updateJob = new CreateInteractionRest_S_HUM (interactionRequest, interactionRecordID);
                ID jobID = System.enqueueJob(updateJob);                   
            }    
        } 
        else
        {
           intResponse = createInteractionRecord(interactionRequest,interactionRecordID);
        } 
        return intResponse;
    }
    
   /*
    * Method name : insertInteractionObj
    * Description : This method is used to insert interaction record when RequiredCasesinResponse boolean value is false from request. 
    * Return Type : CreateInteractionResponse_DTO_HUM
    * Parameter   : CaseServiceRestCreateRequest_DTO_HUM
    */
    private static CreateInteractionResponse_DTO_HUM insertInteractionObj(Interaction__c oInteraction)
    {
        CreateInteractionResponse_DTO_HUM intResponseObj = new CreateInteractionResponse_DTO_HUM();
        CreateInteractionResponse_DTO_HUM.CreateInteractionInfo intResponse = new CreateInteractionResponse_DTO_HUM.CreateInteractionInfo();
        String sintId = null;
                
        Database.SaveResult result = Database.insert(oInteraction, false); 
            
        if(result.isSuccess())
        {
            sintId = result.getId();                                    
            List<Interaction__c> ointReturn = new List<Interaction__c>([Select Id,Name from Interaction__c where Id = :sintId]);
            
            if(!ointReturn.isEmpty())
            {              
                intResponse.InteractionNumber = ointReturn[0].Name;
                intResponse.Success = true;
                interactionRecordID = sintId;                              
            }
        }
        else
        {               
            intResponse.Success = false;
            for(Database.Error err : result.getErrors()) 
            { 
                intResponse.ErrorCode += (String.valueOf(err.getStatusCode()) + '; ');
                intResponse.ErrorMessage += (err.getMessage() + '; ');             
            }
        }   
        intResponseObj.CreateInteractionInfo = intResponse;
        return intResponseObj;      
    }

    /*
    * Method name : insertCallInteractionObj
    * Description : This method is used to insert interaction record For Call.
    * Return Type : CallInteractionResponse_DTO_HUM
    * Parameter   : Interaction__c
    */
    private static CallInteractionResponse_DTO_HUM insertCallInteractionObj(Interaction__c oInteraction)
    {
        CallInteractionResponse_DTO_HUM intResponse = new CallInteractionResponse_DTO_HUM();
        String sintId = null;
                
        Database.SaveResult result = Database.insert(oInteraction, false); 
        //If success return the InteractionNumber    
        if(result.isSuccess())
        {
            sintId = result.getId();                                    
            List<Interaction__c> ointReturn = new List<Interaction__c>([Select Id,Name from Interaction__c where Id = :sintId]);
            
            if(null != ointReturn && !ointReturn.isEmpty())
            {              
                intResponse.InteractionNumber = ointReturn[0].Name;
                intResponse.Success = true;
                interactionRecordID = sintId;                              
            }
        }
        else
        {               
            intResponse.Success = false;
            //If error then add error message in response
            for(Database.Error err : result.getErrors()) 
            { 
                intResponse.ErrorMessage += (err.getMessage() + '; ');             
            }
        }   
        return intResponse;      
    }

   /*
    * Method name : createInteractionRecord
    * Description : This method is used to create Interaction records.       
    * Return Type : CreateInteractionResponse_DTO_HUM
    * Parameter   : CreateInteractionRequest_DTO_HUM interactionRequest, Id ointId
    */
    @TestVisible
    public static CreateInteractionResponse_DTO_HUM createInteractionRecord(CreateInteractionRequest_DTO_HUM interactionRequest, Id ointId)
    {   
        CreateInteractionResponse_DTO_HUM intResponse = new CreateInteractionResponse_DTO_HUM();
        Interaction__c oInteraction = new Interaction__c();  
        if(String.isNotBlank(ointId)) 
            oInteraction = [select Id, Name, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c FROM Interaction__c where id=:ointId]; 
       List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
        try
        {
            boolean bIsCasesRequired = interactionRequest.CreateInteractionRequest.RequiredCasesInResponse;
            oInteraction = assignInteractionValues(oInteraction,interactionRequest, lstInteractionMembers);
            intResponse = insertInteractionAndIntMembers(oInteraction, lstInteractionMembers, bIsCasesRequired);
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CreateInteractionWithCaseRest_D_HUM','createInteractionRecord');
            return null; 
        }
        return intResponse;
    }

    /*
    * Method name : createCallInteractionRecord
    * Description : This method is used to create Interaction records in asynch manner.       
    * Return Type : CallInteractionResponse_DTO_HUM
    * Parameter   : CallInteractionRequest_DTO_HUM interactionRequest, Id ointId
    */
    public static void createCallInteractionRecord(CallInteractionRequest_DTO_HUM interactionRequest, Id ointId)
    {   
        list<Interaction__c> interactionLst = new list<Interaction__c>();
        if(String.isNotBlank(ointId)) 
        {
            interactionLst = [select Id, Name, Interacting_With__c, Interacting_With_type__c, Interaction_Origin__c FROM Interaction__c where id=:ointId]; 
        }
        List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
        try
        {
            if(null != interactionLst && !interactionLst.isEmpty())
            {
                CaseInteractionSearch_H_HUM.GetCallAccountDetails(interactionRequest,interactionLst[0]);
            }
        }
        catch(exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'CreateInteractionWithCaseRest_D_HUM','createCallInteractionRecord');
        }
    }
    
    
    /*
    * Method name : assignInteractionValues
    * Description : This method is used to assign Interaction request values to insertinteraction record record.
    * Return Type : Interaction__C
    * Parameter   : CreateInteractionRequest_DTO_HUM ,List<Interaction_Member__c>
    */ 
    @TestVisible
    private static Interaction__c assignInteractionValues(Interaction__c objInteraction, CreateInteractionRequest_DTO_HUM intRequestObject, List<Interaction_Member__c> lstInteractionMembers)
    {
        if (Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isCreateable() || Schema.sObjectType.Interaction__c.fields.Interaction_Origin__c.isUpdateable())
            objInteraction.Interaction_Origin__c = intRequestObject.CreateInteractionRequest.InteractionOrigin;
        if (Schema.sObjectType.Interaction__c.fields.UUID__c.isCreateable() || Schema.sObjectType.Interaction__c.fields.UUID__c.isUpdateable())
            objInteraction.UUID__c =  intRequestObject.CreateInteractionRequest.UUID;  
        if (Schema.sObjectType.Interaction__c.fields.Created_By_Queue__c.isCreateable() || Schema.sObjectType.Interaction__c.fields.Created_By_Queue__c.isUpdateable())
            objInteraction.Created_By_Queue__c  =intRequestObject.CreateInteractionRequest.CreatedByQueue; 
        
        objInteraction  = populateInteractionDetails(objInteraction, intRequestObject,  lstInteractionMembers);
        return objInteraction;   
    }
    
    /*  
    * Method name : populateInteractionDetails
    * Description : This method is used to populate interaction with interacting with and interacting about details
    * Return Type : Interaction__c
    * Parameter   : Interaction__c, CreateInteractionRequest_DTO_HUM, List<Interaction_Member__c>
    */       
    @TestVisible
    private static Interaction__c populateInteractionDetails (Interaction__c oInteraction, CreateInteractionRequest_DTO_HUM intRequestObject, List<Interaction_Member__c> lstInteractionMembers)
    {  
       map<String, map<string,set<string>>> mapInquireAboutCriteria = new map<String, map<string,set<string>>>();
       map<String, map<string,set<string>>> mapInquireWithCriteria = new map<String, map<string,set<string>>>();       
       list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> Interactionaboutvalues=new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();
       list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> Interactionwithvalues = new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();        
       map<String,list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>> mapIntAboutCaseDetails= new map<String,list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>>(); 
       map<String,list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>> mapIntAboutCaseUnknwn= new map<String,list<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>>(); 
       Map<String,List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>> mapCaseInteractionDetails = new Map<String,List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>>();
       Boolean isUnknownProv = false;
       String sAccRecType ='';
       // Constructing map based on  Entitytype,IDvalueType and IDvalues from requested Interactingabout/Interactingwith values
       if( intRequestObject.CreateInteractionRequest.CaseInteractingAbout!= null 
           && !intRequestObject.CreateInteractionRequest.CaseInteractingAbout.isEmpty())
        {    
            Integer iCount=0;        
            for(CreateInteractionRequest_DTO_HUM.CaseInteractingAbout oIntAbout: intRequestObject.CreateInteractionRequest.CaseInteractingAbout)
            {
                if(oIntAbout.InteractingAbout!=null)  Interactionaboutvalues.add(oIntAbout.InteractingAbout);                                             
                // Map of Interaction about id to Case details
                if(intRequestObject.CreateInteractionRequest.CreateCaseDetails!=null && !intRequestObject.CreateInteractionRequest.CreateCaseDetails.isEmpty()) 
                {
                    for(CreateInteractionRequest_DTO_HUM.CreateCaseDetails oCaseDetail: intRequestObject.CreateInteractionRequest.CreateCaseDetails)
                    {
                        if(oCaseDetail.CaseIdentifier.equalsIgnoreCase(oIntAbout.CaseIdentifier) && oIntAbout.InteractingAbout!=null)
                        {               
                            if(mapIntAboutCaseDetails.containsKey(oIntAbout.InteractingAbout.ID)) mapIntAboutCaseDetails.get(oIntAbout.InteractingAbout.ID).add(oCaseDetail);
                            else mapIntAboutCaseDetails.put(oIntAbout.InteractingAbout.ID,new List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>{oCaseDetail});
                        }  
                        if(oCaseDetail.CaseIdentifier.equalsIgnoreCase(oIntAbout.CaseIdentifier) && oIntAbout.InteractingAbout!=null && String.isBlank(oIntAbout.InteractingAbout.ID))
                        { 
                            string sTempKey = 'UnknownMbr'+iCount;
                            if(mapIntAboutCaseUnknwn.containsKey(sTempKey)) mapIntAboutCaseUnknwn.get(sTempKey).add(oCaseDetail);
                            else mapIntAboutCaseUnknwn.put(sTempKey,new List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails>{oCaseDetail});
                            iCount++;
                        }                       
                    }
                }
                
                //Added to handle Unknow Prov 
                if(oIntAbout.InteractingAbout.IDType == GLOBAL_CONSTANT_HUM.UNKNOWNPROVIDER_CASE_SERVICE)
                isUnknownProv = true;
            }
            
            
            if(!isUnknownProv){
                mapInquireAboutCriteria.putAll(CaseInteractionSearch_H_HUM.createMapInquireCriteria(Interactionaboutvalues, sIntAbout));  
                mapMemberDependentCode.putAll(CaseInteractionSearch_H_HUM.getMapMemberDependentCode(Interactionaboutvalues, sIntAbout));           
            }
        }
        //For Interacting With 
        if(intRequestObject.CreateInteractionRequest.InteractingWith!=null)
        {   
            Interactionwithvalues.add(intRequestObject.CreateInteractionRequest.InteractingWith);
            mapInquireWithCriteria.putAll(CaseInteractionSearch_H_HUM.createMapInquireCriteria(Interactionwithvalues, sIntWith));
            mapMemberDependentCode.putAll(CaseInteractionSearch_H_HUM.getMapMemberDependentCode(Interactionwithvalues, sIntWith));
        }
        
        //Added to handle Unknow Prov
        if(isUnknownProv){
                mapInquireAboutCriteria.putAll(CaseInteractionSearch_H_HUM.createMapInquireCriteria(Interactionaboutvalues, sIntAbout));  
                mapMemberDependentCode.putAll(CaseInteractionSearch_H_HUM.getMapMemberDependentCode(Interactionaboutvalues, sIntAbout));           
            }
        if((mapInquireAboutCriteria != null && !mapInquireAboutCriteria.isEmpty()) || (mapInquireWithCriteria!= null && !mapInquireWithCriteria.isEmpty()))
        {
            map<string, set<string>> mapMemberIntAbt = new map<string, set<string>>();
            map<string, set<string>> mapMemberIntWith = new map<string, set<string>>();
            if(mapInquireAboutCriteria != null && !mapInquireAboutCriteria.isEmpty())
            {                   
                if(mapInquireAboutCriteria.containsKey(RECORDTYPE_MEMBER))
                {
                    mapMemberIntAbt.putAll(mapInquireAboutCriteria.get(RECORDTYPE_MEMBER));             
                    if(mapMemberIntAbt.containsKey(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM)) 
                    {              
                        mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.createMapAcctIDValuesByType(mapMemberIntAbt));
                        mapMemberIntAbt.remove(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM);  //remove EnterpriseID before querying on MemberID object
                    }  
                }
            } 
            if(mapInquireWithCriteria!= null && !mapInquireWithCriteria.isEmpty())
            {                       
                if(mapInquireWithCriteria.containsKey(RECORDTYPE_MEMBER))
                {
                    mapMemberIntWith.putAll(mapInquireWithCriteria.get(RECORDTYPE_MEMBER));
                    if(mapMemberIntWith.containsKey(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM)) 
                    {
                        mapInteractingWithAccount.putAll(CaseInteractionSearch_H_HUM.createMapAcctIDValuesByType(mapMemberIntWith));                       
                        mapMemberIntWith.remove(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM);   //remove EnterpriseID before querying on MemberID object
                    }  
                }
            }
            if((mapMemberIntAbt!=null && !mapMemberIntAbt.isEmpty()) || (mapMemberIntWith!=null && !mapMemberIntWith.isEmpty()))
            {  
                list<Member_Id__c> lstMemberRecs = CaseInteractionSearch_H_HUM.locateMembers(mapMemberIntAbt, mapMemberIntWith, mapMemberDependentCode);                   
                mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecords(lstMemberRecs, mapMemberIntAbt,mapMemberDependentCode, sIntAbout));
                mapInteractingWithAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecords(lstMemberRecs, mapMemberIntWith,mapMemberDependentCode, sIntWith));                
            }   
            //unknown account creation
            if(intRequestObject.CreateInteractionRequest.InteractingWith.EntityType != GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM ){
            
            mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(mapInteractAboutAccount, Interactionaboutvalues,sIntAbout));
            mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(mapInteractingWithAccount, InteractionWithvalues,sIntWith));
          } 
        }
        if((mapIntWithAboutUnknownCriteria.isEmpty() || (!mapIntWithAboutUnknownCriteria.isEmpty() && mapIntWithAboutUnknownCriteria.get(sIntAbout)==NULL)) && CaseInteractionSearch_H_HUM.unknwnMbrRequest!=null && !CaseInteractionSearch_H_HUM.unknwnMbrRequest.isEmpty())       {
           if(CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntAbout)!=null && !CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntAbout).isEmpty()) mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntAbout), Interactionaboutvalues,sIntAbout));
        }
        if((mapIntWithAboutUnknownCriteria.isEmpty() || (!mapIntWithAboutUnknownCriteria.isEmpty() && mapIntWithAboutUnknownCriteria.get(sIntWith)==NULL)) && CaseInteractionSearch_H_HUM.unknwnMbrRequest!=null && !CaseInteractionSearch_H_HUM.unknwnMbrRequest.isEmpty())        {   
            if(CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntWith)!=null && !CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntWith).isEmpty()) mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntWith), InteractionWithvalues,sIntWith));
        }   

        //for watson Group
       
       if((CaseInteractionSearch_H_HUM.groupaccountMapToUpsert !=null && !CaseInteractionSearch_H_HUM.groupaccountMapToUpsert.isEmpty()) || (CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert!=null && !CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert.isEmpty()))
        {           
          mapGroupDetails.putAll(CaseInteractionSearch_H_HUM.calltoUpsertGroupAccounts());
        }
       
       if(mapGroupDetails.containskey(sIntWith)) mapInteractingWithAccount.putAll(mapGroupDetails.get(sIntWith));
       if(mapGroupDetails.containskey(sIntAbout)) mapInteractAboutAccount.putAll(mapGroupDetails.get(sIntAbout));
        
       if(mapGroupDetails.size() == 0 && ((CaseInteractionSearch_H_HUM.accountMapToUpsert!=null && !CaseInteractionSearch_H_HUM.accountMapToUpsert.isEmpty()) || (CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert!=null && !CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert.isEmpty())))
        {
           mapProviderDetails.putAll(CaseInteractionSearch_H_HUM.calltoUpsertAccounts());
       }
       
       //provider
       if(mapProviderDetails.containskey(sIntWith)) mapInteractingWithAccount.putAll(mapProviderDetails.get(sIntWith));
       if(mapProviderDetails.containskey(sIntAbout)) mapInteractAboutAccount.putAll(mapProviderDetails.get(sIntAbout));
       //agent
       CreateAgentInteraction_H_HUM AgentHelper = new CreateAgentInteraction_H_HUM();
       mapAgentDetails = AgentHelper.PrepareAgentAccountMap(intRequestObject.CreateInteractionRequest);
       if(mapAgentDetails.containskey(sIntWith)) mapInteractingWithAccount.putAll(mapAgentDetails.get(sIntWith));
       if(mapAgentDetails.containskey(sIntAbout)) mapInteractAboutAccount.putAll(mapAgentDetails.get(sIntAbout));
      
      
      //unknown insertion for group 
      if( CaseInteractionSearch_H_HUM.blninsertunknowngrp == true){
      
      mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(mapInteractAboutAccount, Interactionaboutvalues,sIntAbout));
      mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(mapInteractingWithAccount, InteractionWithvalues,sIntWith));}
     
      
       //Unknown insertion
       if(mapIntWithAboutUnknownCriteria != null) mapUnknownmemberdetails = CaseInteractionSearch_H_HUM.getUknownMemberMaps(mapIntWithAboutUnknownCriteria);
       
       
       
       if(mapUnknownmemberdetails.containskey(sIntWith) && mapUnknownmemberdetails.containskey(sIntWith) != NULL) {
       mapInteractingWithAccount.putAll(mapUnknownmemberdetails.get(sIntWith));
       }
       if(mapUnknownmemberdetails.containskey(sIntAbout) && mapUnknownmemberdetails.containskey(sIntAbout) != NULL) {
       mapInteractAboutAccount.putAll(mapUnknownmemberdetails.get(sIntAbout));
       }
       
       
       if(CaseInteractionSearch_H_HUM.mapUnknwnMemberFrmService != null && !CaseInteractionSearch_H_HUM.mapUnknwnMemberFrmService.isEmpty())  mapUnkwnMbrFrmRequest.putAll(CaseInteractionSearch_H_HUM.mapUnknwnMemberFrmService); 
       set<String> setAccountId = new set<String>();   // set of account ids        
       map<id, String> mapAccountRecordtype = new map <id,String>(); // map of account ids and record type ids      
       setAccountId.addAll(mapInteractAboutAccount.Values());
       setAccountId.addAll(mapInteractingWithAccount.Values());
       if(CaseInteractionSearch_H_HUM.lstAcctId != null && !CaseInteractionSearch_H_HUM.lstAcctId.isEmpty())
           setAccountId.addAll(CaseInteractionSearch_H_HUM.lstAcctId);
       if(!CaseInteractionSearch_H_HUM.mapunknownProvider.isEmpty() && CaseInteractionSearch_H_HUM.mapunknownProvider!=null)
       {
           Set<String> setunknwnProv = new Set<String>();
           if(CaseInteractionSearch_H_HUM.mapunknownProvider.containsKey(sIntWith)){
           setunknwnProv.add(CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntWith));
           setAccountId.addAll(setunknwnProv);}
           if(CaseInteractionSearch_H_HUM.mapunknownProvider.containsKey(sIntAbout)){
           setunknwnProv.add(CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntAbout));
           setAccountId.addAll(setunknwnProv);}
           
           
      }
      if(setAccountId != null && !setAccountId.isEmpty()) {
           for(Account a: [Select Id,recordtypeId,recordtype.name from Account where id=: setAccountId AND ETL_Record_Deleted__c = false])
           {
               mapAccountRecordtype.put(a.Id, a.recordtypeId);
           }
      }
       // insertion of interacting with and interacting with members                                  
        if(!mapInteractAboutAccount.isEmpty() && mapInteractAboutAccount!=null)
        {
            set<String> setofCaseIndentifier = new set<String>();
            for(CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO oIntAbt: Interactionaboutvalues)
            {
                Interaction_Member__c oIntAbtMember = new Interaction_Member__c();                      
                if(mapInteractAboutAccount.ContainsKey(oIntAbt.ID))
                {
                    String sIntAbtAccount = mapInteractAboutAccount.get(oIntAbt.ID);  
                     if(!mapAccountRecordtype.isEmpty()){
                      sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sIntAbtAccount)).getName();  
                    }
                    
                    oIntAbtMember.Interacting_About__c = sIntAbtAccount; 
                    oIntAbtMember.Interacting_About_Type__c = mapRecordType.get(sAccRecType);  
                    if(mapIntAboutCaseDetails.containsKey(oIntAbt.ID)) //populating case details inner map
                    {                       
                        List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> templist = mapIntAboutCaseDetails.get(oIntAbt.ID);
                         
                        for(CreateInteractionRequest_DTO_HUM.CreateCaseDetails requestDTO:templist)
                        {
                            if(!setofCaseIndentifier.contains(requestDTO.CaseIdentifier)){
                                
                                setofCaseIndentifier.add(requestDTO.CaseIdentifier);
                                CreateCaseRequest_DTO_V4_HUM multiCaseCreateDTOobj =  new CreateCaseRequest_DTO_V4_HUM();
                                multiCaseCreateDTOobj.caseCreateRequest = requestDTO.CaseDetails;
                                multiCaseCreateDTOobj.sInteractingAbout = sIntAbtAccount;
                                multiCaseCreateDTOobj.sInteractingAboutType = oIntAbtMember.Interacting_About_Type__c;
                                multiCaseCreateDTOobj.sInteractingWith = assignCaseInteractingWith(multiCaseCreateDTOobj,intRequestObject);
                                multiCaseCreateDTOobj.sCaseIdentifier = requestDTO.CaseIdentifier;
                                String sIntWithType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(multiCaseCreateDTOobj.sInteractingWith)).getName();                                           
                                multiCaseCreateDTOobj.sInteractingWithType = mapRecordType.get(sIntWithType);
                                listOfMultiCaseCreateRequest_DTO_HUM.add(multiCaseCreateDTOobj);  
                            }
                           
                        }
                        if(mapCaseInteractionDetails.containskey(sIntAbtAccount)) mapCaseInteractionDetails.get(sIntAbtAccount).addall(templist);
                        else mapCaseInteractionDetails.put(sIntAbtAccount, templist);
                    }                
                }
                lstInteractionMembers.add(oIntAbtMember);
            }
        } 
        
        if(mapUnkwnMbrFrmRequest != null && !mapUnkwnMbrFrmRequest.isEmpty() && mapUnkwnMbrFrmRequest.get(sIntAbout)!=null && !mapUnkwnMbrFrmRequest.get(sIntAbout).isEmpty())
        {
            Integer iUnknwnSize = mapUnkwnMbrFrmRequest.get(sIntAbout).size();
            Integer iCount =0;
             set<String> setofCaseIndentifier = new set<String>();
            for(integer iUnk=0; iUnk<iUnknwnSize; iUnk++)
            {
                Interaction_Member__c oIntAbtMember = new Interaction_Member__c();                      
                string stemp = 'UnknownMbr'+iCount;
                String sIntAbtAccount = mapUnkwnMbrFrmRequest.get(sIntAbout).get(stemp);  
                sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sIntAbtAccount)).getName();
                oIntAbtMember.Interacting_About__c = sIntAbtAccount; 
                oIntAbtMember.Interacting_About_Type__c = mapRecordType.get(sAccRecType);
                if(mapIntAboutCaseUnknwn.containsKey(stemp)) //populating case details inner map
                {                       
                List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> templist = mapIntAboutCaseUnknwn.get(stemp);
                for(CreateInteractionRequest_DTO_HUM.CreateCaseDetails requestDTO:templist)
                {
                    if(!setofCaseIndentifier.contains(requestDTO.CaseIdentifier)){
                        setofCaseIndentifier.add(requestDTO.CaseIdentifier);
                    CreateCaseRequest_DTO_V4_HUM multiCaseCreateDTOobj =  new CreateCaseRequest_DTO_V4_HUM();
                    multiCaseCreateDTOobj.caseCreateRequest = requestDTO.CaseDetails;
                    multiCaseCreateDTOobj.sInteractingAbout = sIntAbtAccount;
                    multiCaseCreateDTOobj.sInteractingAboutType = oIntAbtMember.Interacting_About_Type__c;
		    multiCaseCreateDTOobj.sCaseIdentifier = requestDTO.CaseIdentifier;
                    multiCaseCreateDTOobj.sInteractingWith = assignCaseInteractingWith(multiCaseCreateDTOobj,intRequestObject);
                    String sIntWithType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(multiCaseCreateDTOobj.sInteractingWith)).getName();                                           
                    multiCaseCreateDTOobj.sInteractingWithType = mapRecordType.get(sIntWithType);                         
                    listOfMultiCaseCreateRequest_DTO_HUM.add(multiCaseCreateDTOobj);
                           
                    }
                    
                }
                        if(mapCaseInteractionDetails.containskey(sIntAbtAccount)) mapCaseInteractionDetails.get(sIntAbtAccount).addall(templist);
                        else mapCaseInteractionDetails.put(sIntAbtAccount, templist);
                    }   
                    lstInteractionMembers.add(oIntAbtMember);  
                    iCount++;
                    }      
                }
                

        if(!mapInteractingWithAccount.isEmpty() && mapInteractingWithAccount!=null)
        {   
            if(mapInteractingWithAccount.containsKey(intRequestObject.CreateInteractionRequest.InteractingWith.ID))
            {
                String sInteractWithID = mapInteractingWithAccount.get(intRequestObject.CreateInteractionRequest.InteractingWith.ID);
                sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sInteractWithID)).getName();
                if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()  || Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable())
                	ointeraction.Interacting_With__c = sInteractWithID;
                if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()  || Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable())
                	ointeraction.Interacting_With_type__c = mapRecordType.get(sAccRecType);
           } 
        }  
        
        //Unknown Provider scenario  
        if(CaseInteractionSearch_H_HUM.mapunknownProvider!= null && !CaseInteractionSearch_H_HUM.mapunknownProvider.isEmpty() && CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntAbout)!=null && CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntAbout)!= '')
        {
            Integer iUnknwnSize = CaseInteractionSearch_H_HUM.mapunknownProvider.size();
            Integer iCount =0;
            set<String> setofCaseIndentifier = new set<String>();
            for(integer iUnk=0; iUnk<iUnknwnSize; iUnk++)
            {
                Interaction_Member__c oIntAbtMember = new Interaction_Member__c();                      
                string stemp = 'UnknownMbr'+iCount;
                String sIntAbtAccount = CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntAbout);  
                
                
                sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sIntAbtAccount)).getName();
               
                oIntAbtMember.Interacting_About__c = sIntAbtAccount; 
                oIntAbtMember.Interacting_About_Type__c = mapRecordType.get(sAccRecType);
               
                if(mapIntAboutCaseUnknwn.containsKey(stemp)) //populating case details inner map
                {                       
                List<CreateInteractionRequest_DTO_HUM.CreateCaseDetails> templist = mapIntAboutCaseUnknwn.get(stemp);
                for(CreateInteractionRequest_DTO_HUM.CreateCaseDetails requestDTO:templist)
                {
                    if(!setofCaseIndentifier.contains(requestDTO.CaseIdentifier)){
                        CreateCaseRequest_DTO_V4_HUM multiCaseCreateDTOobj =  new CreateCaseRequest_DTO_V4_HUM();
                        multiCaseCreateDTOobj.caseCreateRequest = requestDTO.CaseDetails;
                        multiCaseCreateDTOobj.sInteractingAbout = sIntAbtAccount;
                        multiCaseCreateDTOobj.sInteractingAboutType = oIntAbtMember.Interacting_About_Type__c;
                        multiCaseCreateDTOobj.sCaseIdentifier = requestDTO.CaseIdentifier;
                        multiCaseCreateDTOobj.sInteractingWith = assignCaseInteractingWith(multiCaseCreateDTOobj,intRequestObject);
                        String sIntWithType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(multiCaseCreateDTOobj.sInteractingWith)).getName();                                           
                        multiCaseCreateDTOobj.sInteractingWithType = mapRecordType.get(sIntWithType);                                                      
                        
                        listOfMultiCaseCreateRequest_DTO_HUM.add(multiCaseCreateDTOobj);  
                             
                    }
                    
                }
                        if(mapCaseInteractionDetails.containskey(sIntAbtAccount)) mapCaseInteractionDetails.get(sIntAbtAccount).addall(templist);
                        else mapCaseInteractionDetails.put(sIntAbtAccount, templist);
                    }   
                    lstInteractionMembers.add(oIntAbtMember);  
                    iCount++;
                    }      
                }
          
        if(!CaseInteractionSearch_H_HUM.mapunknownProvider.isEmpty() && CaseInteractionSearch_H_HUM.mapunknownProvider!=null) 
        {
            
            String sInteractWithID = '';
            if(CaseInteractionSearch_H_HUM.mapunknownProvider.containsKey(sIntWith)){
            sInteractWithID = CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntWith);
            sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sInteractWithID)).getName();
            if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()  || Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable())
            ointeraction.Interacting_With__c = sInteractWithID;
            if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()  || Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable())
            ointeraction.Interacting_With_type__c = mapRecordType.get(sAccRecType); }    
        } 
        if(mapUnkwnMbrFrmRequest != null && !mapUnkwnMbrFrmRequest.isEmpty() && mapUnkwnMbrFrmRequest.get(sIntWith)!=null && !mapUnkwnMbrFrmRequest.get(sIntWith).isEmpty())
        {
    
            String sInteractWithID = mapUnkwnMbrFrmRequest.get(sIntWith).get('UnknownMbr0');
             sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sInteractWithID)).getName();
            if (Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isCreateable()  || Schema.sObjectType.Interaction__c.fields.Interacting_With__c.isUpdateable())
            ointeraction.Interacting_With__c = sInteractWithID;
            if (Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isCreateable()  || Schema.sObjectType.Interaction__c.fields.Interacting_With_type__c.isUpdateable())
            ointeraction.Interacting_With_type__c = mapRecordType.get(sAccRecType);     
        } 
        return  ointeraction;
    }

    private static string assignCaseInteractingWith(CreateCaseRequest_DTO_V4_HUM multiCaseCreateDTOobj,CreateInteractionRequest_DTO_HUM intRequestObject)
    {
        if(mapInteractingWithAccount.get(intRequestObject.CreateInteractionRequest.InteractingWith.ID) != null && String.isNotBlank(mapInteractingWithAccount.get(intRequestObject.CreateInteractionRequest.InteractingWith.ID)))
            multiCaseCreateDTOobj.sInteractingWith = mapInteractingWithAccount.get(intRequestObject.CreateInteractionRequest.InteractingWith.ID);
        if(mapUnkwnMbrFrmRequest.get(sIntWith) != null && !mapUnkwnMbrFrmRequest.get(sIntWith).isEmpty())
            multiCaseCreateDTOobj.sInteractingWith = mapUnkwnMbrFrmRequest.get(sIntWith).get('UnknownMbr0');
        if(CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntWith) !=null && String.isNotBlank(CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntWith)))
            multiCaseCreateDTOobj.sInteractingWith = CaseInteractionSearch_H_HUM.mapunknownProvider.get(sIntWith);
        return multiCaseCreateDTOobj.sInteractingWith;
    }

   /*  
    * Method name : insertInteractionAndIntMembers
    * Description : This method is used to insert interaction and interaction members
    * Return Type : CreateInteractionResponse_DTO_HUM 
    * Parameter   : interaction__c oInteraction, list<Interaction_Member__c> lstInteractionMembers, boolean bIsCasesRequired
    */    
    @TestVisible
    private static CreateInteractionResponse_DTO_HUM insertInteractionAndIntMembers(interaction__c oInteraction, list<Interaction_Member__c> lstInteractionMembers, boolean bIsCasesRequired)
    {    
        CreateInteractionResponse_DTO_HUM  intResponseObj = new CreateInteractionResponse_DTO_HUM();
        CreateInteractionResponse_DTO_HUM.CreateInteractionInfo interactionInfo = new CreateInteractionResponse_DTO_HUM.CreateInteractionInfo();       
        list<case> lstCases = new list<case>();
        Database.UpsertResult result = Database.upsert(oInteraction, false);    
        if(result.isSuccess())
        {
            String intId = result.getId(); 
            List<Interaction__c> oIntReturn = new List<Interaction__c>();  
            if(String.isNotBlank(intId) && intId!=null)
            oIntReturn = [Select Id,name from interaction__c where Id = :intId];
            Boolean bIsIntMemberError = CaseInteractionSearch_H_HUM.createInteractingMembers(lstInteractionMembers, intId);
			for(CreateCaseRequest_DTO_V4_HUM ocaseReq : listOfMultiCaseCreateRequest_DTO_HUM)
            {
                ocaseReq.sInteractionId = intId;
            }
            List<CaseDetailResponse_DTO_V4_HUM> lstCaseResponse =  CreateCaseRestBulkified_D_V4_HUM.createCases(listOfMultiCaseCreateRequest_DTO_HUM,true);
            List<String> lstCasenumbers = CaseInteractionSearch_H_HUM.createCaseInteractionRecords(lstCaseResponse, intId, lstCases);
            List<CaseDetailResponse_DTO_V4_HUM> lstCaseErrorLogs = CaseInteractionSearch_H_HUM.getCaseErrorLogs(lstCaseResponse);
            if(!oIntReturn.isEmpty())
            {                  
                interactionInfo.InteractionNumber = oIntReturn[0].Name;
                interactionInfo.Success = true;
                if(lstCasenumbers!=null && !lstCasenumbers.isEmpty() && bIsCasesRequired) 
                { 
                    interactioninfo.CreatedCases =   lstCasenumbers;  
                }
                else
                {                    
                    if(!lstCaseErrorLogs.isEmpty() && lstCaseErrorLogs!=null)
                    { 
                        interactionInfo.ErrorCode = lstCaseErrorLogs[0].ErrorCode;
                        interactionInfo.ErrorMessage = lstCaseErrorLogs[0].ErrorMessage;
                        interactionInfo.Success = false;
                   }
               }                        
           }
       }
       else
       {               
           interactionInfo.Success = false;       
           for(Database.Error err : result.getErrors()) 
           { 
               interactionInfo.ErrorCode += (String.valueOf(err.getStatusCode()) + '; ');
               interactionInfo.ErrorMessage += (err.getMessage() + '; ');             
           }                    
        }   
        intResponseObj.CreateInteractionInfo = interactionInfo;
        return intResponseObj;      
    }
}