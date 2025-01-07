/******************************************************************************************************************
Apex class Name    : UpdateInteractionWithCaseRest_D_HUM
Version            : 1.0
Function           : This class is for Data Access Layer for for Interaction Update related activities
Created On         : May 2nd, 2019
Test Class         : UpdateInteractionWithCaseRest_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Pallavi Ravishankar         354971            05/02/2019           Original Version
*   2.0            Prasanthi Kandula                             05/13/2019           REQ - 399198 - Asynchronous call for WATSON
*   3.0            Pallavi Ravishankar                           05/15/2019           Added logic to handle unknown member logic
*   4.0            Prasanti                                      06/25/2019           Intercation Id parameter mapping.
*   5.0            Seema Kaulgi                                  07/30/2019           REQ - 403811 GBO Watson chat (Group)
*   6.0            Asish Behera                                  07/23/2020           Case create DTO change incorporated 
------------------------------------------------------------------------------------------------------------------- */
global with sharing class UpdateInteractionWithCaseRest_D_HUM {

   private static string sIntAbout ='Interacting_About';
   private static map<string, map<string, set<string>>> mapMemberDependentCode= new map<string, map<string, set<string>>>();
   private static map<string, string> mapInteractAboutAccount = new map<string, string>();
   private static map<string,List<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>> mapIntWithAboutUnknownCriteria = new map<string, list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>>();   
   private static Map<String,Map<String,String>> mapUnknownmemberdetails = new Map<String,Map<String,String>>();
   private static Map<String,Map<String,String>> mapProviderDetails = new Map<String,Map<String,String>>();
   private static Map<String,Map<String,String>> mapGroupDetails = new Map<String,Map<String,String>>();
   private static list<CreateCaseRequest_DTO_V4_HUM> listOfMultiCaseCreateRequest_DTO_HUM = new list<CreateCaseRequest_DTO_V4_HUM>(); 
   private static map<string,map<string,string>> mapUnkwnMbrFrmRequest = new map<string,map<string,string>>();   
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
    * Method name : updateCaseAndAttachment
    * Description : This method will be used by external system to update case and attachment.
    * Return Type : UpdateInteractionRequest_DTO_HUM
    * Parameter   : UpdateInteractionResponse_DTO_HUM
    */  
   public static UpdateInteractionResponse_DTO_HUM updateInteraction(UpdateInteractionRequest_DTO_HUM request)
   {
        UpdateInteractionResponse_DTO_HUM response = new UpdateInteractionResponse_DTO_HUM();
        response = verifyAsynchronousFlag(request);  
        return response;
   }
   
   /*
    * Method name : verifyAsynchronousFlag
    * Description : This method is used to check RequiredCasesinResponse boolean value from request. 
                    If the boolean value is true case numbers are returned in request
                    If the boolean value is false case numbers are not returned
    * Return Type : UpdateInteractionResponse_DTO_HUM
    * Parameter   : UpdateInteractionRequest_DTO_HUM
    */
    private static UpdateInteractionResponse_DTO_HUM verifyAsynchronousFlag(UpdateInteractionRequest_DTO_HUM interactionRequest)
    {
        Interaction__c oInteraction = new Interaction__c();
        String sIntNumber = interactionRequest.UpdateInteractionRequest.InteractionNumber;
        UpdateInteractionResponse_DTO_HUM intResponse = new UpdateInteractionResponse_DTO_HUM();
        UpdateInteractionResponse_DTO_HUM.UpdateInteractionInfo interactionInfo = new UpdateInteractionResponse_DTO_HUM.UpdateInteractionInfo();    
        if(!interactionRequest.UpdateInteractionRequest.RequiredCasesInResponse)
        {  
            if(interactionRequest.UpdateInteractionRequest.InteractionNumber!=null && String.IsNotBlank(interactionRequest.UpdateInteractionRequest.InteractionNumber))
           {
               
               UpdateInteractionRest_S_HUM updateJob = new UpdateInteractionRest_S_HUM (interactionRequest, sIntNumber);
               ID jobID = System.enqueueJob(updateJob); 
               interactionInfo.InteractionNumber = sIntNumber;
               interactionInfo.Success=true;  
               intResponse.UpdateInteractionResponse = interactionInfo;                     
            }    
        } 
        else
        {
           intResponse = updateInteractionRecord(interactionRequest,sIntNumber);
        } 
        return intResponse;
    }

    /*
    * Method name : updateInteractionRecord
    * Description : This method is used to create Interaction records.       
    * Return Type : UpdateInteractionResponse_DTO_HUM
    * Parameter   : UpdateInteractionRequest_DTO_HUM intRequest,String sIntNumber
    */
    public static UpdateInteractionResponse_DTO_HUM updateInteractionRecord(UpdateInteractionRequest_DTO_HUM intRequest,String sIntNumber)
    {   
       UpdateInteractionResponse_DTO_HUM intResponse = new UpdateInteractionResponse_DTO_HUM();
       Interaction__c oInteraction = new Interaction__c();  
       List<Interaction_Member__c> lstInteractionMembers = new List<Interaction_Member__c>();
       List<String> lstCaseNumbers = new list<String>();
       list<case> lstCases = new list<case>();
       if(intRequest.UpdateInteractionRequest.InteractionNumber!=null && String.IsNotBlank(intRequest.UpdateInteractionRequest.InteractionNumber))
       {
         try
         {           
            boolean bIsCasesRequired = intRequest.UpdateInteractionRequest.RequiredCasesInResponse;
            list<UpdateInteractionRequest_DTO_HUM.CaseInteraction> lstCaseInt = intRequest.UpdateInteractionRequest.CaseInteractions;
            if(lstCaseInt!=null && !lstCaseInt.IsEmpty())
             {
               for(UpdateInteractionRequest_DTO_HUM.CaseInteraction oCaseInt : lstCaseInt)
               {
                 lstCaseNumbers.add(oCaseInt.CaseNumber);
               }
             }
            if(lstCaseNumbers!=null && !lstCaseNumbers.isEmpty()){ lstCases = [select Id, CaseNumber from Case where caseNumber IN: lstCaseNumbers]; }            
            List<Interaction__c> oIntList = [SELECT Id, Name, Interaction_Origin__c, Interacting_With__c,Interacting_With_type__c from Interaction__c where Name=:sIntNumber];
            if(oIntList!=null && (!oIntList.isEmpty())) oInteraction = oIntList[0];
            oInteraction.Interaction_Origin__c = intRequest.UpdateInteractionRequest.InteractionOrigin;
            oInteraction = populateInteractionDetails(oInteraction, intRequest, lstInteractionMembers);
            intResponse = insertInteractionAndIntMembers(oInteraction, lstInteractionMembers, bIsCasesRequired,lstCases);
         }
         catch(exception ex)
         {
            HUMExceptionHelper.logErrors(ex,'updateInteractionWithCaseRest_D_HUM','updateInteractionRecord');
            return null; 
         }
       
       }
        return intResponse;
    }
    
 
    /*  
    * Method name : populateInteractionDetails
    * Description : This method is used to populate interaction with interacting with and interacting about details
    * Return Type : Interaction__c
    * Parameter   : Interaction__c, UpdateInteractionRequest_DTO_HUM, List<Interaction_Member__c>
    */       
    @TestVisible
    private static Interaction__c populateInteractionDetails (Interaction__c oInteraction, UpdateInteractionRequest_DTO_HUM intRequestObject, List<Interaction_Member__c> lstInteractionMembers)
    { 
        map<String, map<string,set<string>>> mapInquireAboutCriteria = new map<String, map<string,set<string>>>();
        list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO> Interactionaboutvalues=new list<CreateInteractionRequest_DTO_HUM.InteractingWithAboutDTO>();     
        map<String,list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>> mapIntAboutCaseDetails= new map<String,list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>>(); 
        Map<String,List<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>> mapCaseInteractionDetails = new Map<String,List<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>>();
        map<String,list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>> mapIntAboutCaseUnknwn= new map<String,list<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>>(); 
       
        // Constructing map based on  Entitytype,IDvalueType and IDvalues from requested Interacting about values
        if( intRequestObject.UpdateInteractionRequest.CaseInteractingAbout!= null 
           && !intRequestObject.UpdateInteractionRequest.CaseInteractingAbout.isEmpty())
        {  
           Integer iCount=0;
           for(UpdateInteractionRequest_DTO_HUM.CaseInteractingAbout oIntAbout: intRequestObject.UpdateInteractionRequest.CaseInteractingAbout)
           {
              if(oIntAbout.InteractingAbout!=null) Interactionaboutvalues.add(oIntAbout.InteractingAbout);    
              
              // Map of Interaction about id to Case details 
              if(intRequestObject.UpdateInteractionRequest.CreateCaseDetails!=null && !intRequestObject.UpdateInteractionRequest.CreateCaseDetails.isEmpty()) 
               {
                 for(UpdateInteractionRequest_DTO_HUM.CreateCaseDetails oCaseDetail: intRequestObject.UpdateInteractionRequest.CreateCaseDetails)
                  {
                   if(oCaseDetail.CaseIdentifier.equalsIgnoreCase(oIntAbout.CaseIdentifier) && oIntAbout.InteractingAbout!=null)
                     { 
                      if(mapIntAboutCaseDetails.containsKey(oIntAbout.InteractingAbout.ID)) mapIntAboutCaseDetails.get(oIntAbout.InteractingAbout.ID).add(oCaseDetail);
                      else mapIntAboutCaseDetails.put(oIntAbout.InteractingAbout.ID,new List<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>{oCaseDetail});
                     }
                     if(oCaseDetail.CaseIdentifier.equalsIgnoreCase(oIntAbout.CaseIdentifier) && oIntAbout.InteractingAbout!=null && String.isBlank(oIntAbout.InteractingAbout.ID))
                        { 
                            string sTempKey = 'UnknownMbr'+iCount;
                            if(mapIntAboutCaseUnknwn.containsKey(sTempKey)) mapIntAboutCaseUnknwn.get(sTempKey).add(oCaseDetail);
                            else mapIntAboutCaseUnknwn.put(sTempKey,new List<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails>{oCaseDetail});
                            iCount++;
                        } 
                  }               
               }
           }
          mapInquireAboutCriteria.putAll(CaseInteractionSearch_H_HUM.createMapInquireCriteria(Interactionaboutvalues, sIntAbout));  
          mapMemberDependentCode.putAll(CaseInteractionSearch_H_HUM.getMapMemberDependentCode(Interactionaboutvalues, sIntAbout));       
        }
        
        if(mapInquireAboutCriteria != null && !mapInquireAboutCriteria.isEmpty())
        {
           map<string, set<string>> mapMemberIntAbt = new map<string, set<string>>();
           if(mapInquireAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
            {
              mapMemberIntAbt.putAll(mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM));
              if(mapMemberIntAbt.containsKey(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM)) 
                {
                  mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.createMapAcctIDValuesByType(mapMemberIntAbt));
                  mapMemberIntAbt.remove(GLOBAL_CONSTANT_HUM.CASESERVICE_ENTPERSONID_HUM);  //remove EnterpriseID before querying on MemberID object
                }                    
            }
            
            if(mapMemberIntAbt!=null && !mapMemberIntAbt.isEmpty())
            {
              list<Member_Id__c> lstMemberRecs = CaseInteractionSearch_H_HUM.locateMembers(mapMemberIntAbt, null, mapMemberDependentCode);                   
              mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecords(lstMemberRecs, mapMemberIntAbt,mapMemberDependentCode, sIntAbout));                
              
            } 
            
            //watson - START
             map<string, set<string>> mapGroupIntAbt = new map<string, set<string>>();
            if(mapInquireAboutCriteria.containsKey(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPE))
            {
              mapGroupIntAbt.putAll(mapInquireAboutCriteria.get(GLOBAL_CONSTANT_HUM.CASESERVICE_ACCOUNTGROUPTYPE));
            }
            if(mapGroupIntAbt!=null && !mapGroupIntAbt.isEmpty())
            {
              list<Member_Id__c> lstMemberRecs = CaseInteractionSearch_H_HUM.locateMembers(mapMemberIntAbt, null, mapMemberDependentCode);                   
              mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecords(lstMemberRecs, mapMemberIntAbt,mapMemberDependentCode, sIntAbout));                
            }
            if(mapGroupIntAbt!=null && !mapGroupIntAbt.isEmpty())
            {
              list<Member_Id__c> lstMemberRecs = CaseInteractionSearch_H_HUM.locateMembers(mapGroupIntAbt, null, mapMemberDependentCode);                   
              mapInteractAboutAccount.putAll(CaseInteractionSearch_H_HUM.getMatchingRecords(lstMemberRecs, mapGroupIntAbt,mapMemberDependentCode, sIntAbout));                
              
            } 
            // watson - ENDS
                       
            // unknown member map population                       
            mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(mapInteractAboutAccount, Interactionaboutvalues,sIntAbout));                      
        }
        
        if((mapIntWithAboutUnknownCriteria.isEmpty() || (!mapIntWithAboutUnknownCriteria.isEmpty() && mapIntWithAboutUnknownCriteria.get(sIntAbout)==NULL)) && CaseInteractionSearch_H_HUM.unknwnMbrRequest!=null && !CaseInteractionSearch_H_HUM.unknwnMbrRequest.isEmpty())       {
           if(CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntAbout)!=null && !CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntAbout).isEmpty()) mapIntWithAboutUnknownCriteria.putall(CaseInteractionSearch_H_HUM.createUnknownAccWrapper(CaseInteractionSearch_H_HUM.unknwnMbrRequest.get(sIntAbout), Interactionaboutvalues,sIntAbout));
        }
        if((CaseInteractionSearch_H_HUM.accountMapToUpsert!=null && !CaseInteractionSearch_H_HUM.accountMapToUpsert.isEmpty()) || (CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert!=null && !CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert.isEmpty()))
        {
           mapProviderDetails.putAll(CaseInteractionSearch_H_HUM.calltoUpsertAccounts());
        }
        //provider
        if(mapProviderDetails.containskey(sIntAbout)) mapInteractAboutAccount.putAll(mapProviderDetails.get(sIntAbout));       
        
        //Group
        if((CaseInteractionSearch_H_HUM.groupaccountMapToUpsert !=null && !CaseInteractionSearch_H_HUM.groupaccountMapToUpsert.isEmpty()) || (CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert!=null && !CaseInteractionSearch_H_HUM.unknownAccountMapToUpsert.isEmpty()))
        {
           mapGroupDetails.putAll(CaseInteractionSearch_H_HUM.calltoUpsertGroupAccounts());
        }
        
        if(mapGroupDetails.containskey(sIntAbout)) mapInteractAboutAccount.putAll(mapGroupDetails.get(sIntAbout)); 
        
        //unknown account creation
        if(mapIntWithAboutUnknownCriteria != null) mapUnknownMemberDetails = CaseInteractionSearch_H_HUM.getUknownMemberMaps(mapIntWithAboutUnknownCriteria);
        if(mapUnknownMemberDetails.containsKey(sIntAbout)) mapInteractAboutAccount.putAll(mapUnknownMemberDetails.get(sIntAbout));
        if(CaseInteractionSearch_H_HUM.mapUnknwnMemberFrmService != null && !CaseInteractionSearch_H_HUM.mapUnknwnMemberFrmService.isEmpty())  mapUnkwnMbrFrmRequest.putAll(CaseInteractionSearch_H_HUM.mapUnknwnMemberFrmService); 
       
        set<String> setAccountId = new set<String>();   // set of account ids        
        map<id, String> mapAccountRecordtype = new map <id,String>(); // map of account ids and record type ids      
        setAccountId.addAll(mapInteractAboutAccount.Values());
        
        if(CaseInteractionSearch_H_HUM.lstAcctId != null && !CaseInteractionSearch_H_HUM.lstAcctId.isEmpty())
           setAccountId.addAll(CaseInteractionSearch_H_HUM.lstAcctId);
      
        for(Account a: [Select Id,recordtypeId,recordtype.name from Account where id=: setAccountId])
        {
           mapAccountRecordtype.put(a.Id, a.recordtypeId);
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
                  String sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sIntAbtAccount)).getName();
                  oIntAbtMember.Interacting_About__c = sIntAbtAccount;  
                  oIntAbtMember.Interacting_About_Type__c = mapRecordType.get(sAccRecType); 
                  if(mapIntAboutCaseDetails.containsKey(oIntAbt.ID)) //populating case details inner map
                    { 
                      List<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails> templist = mapIntAboutCaseDetails.get(oIntAbt.ID);
                      for(UpdateInteractionRequest_DTO_HUM.CreateCaseDetails requestDTO:templist)
                        {
                           if(!setofCaseIndentifier.contains(requestDTO.CaseIdentifier)){
                                CreateCaseRequest_DTO_V4_HUM multiCaseCreateDTOobj =  new CreateCaseRequest_DTO_V4_HUM();
                              multiCaseCreateDTOobj.caseCreateRequest = requestDTO.CaseDetails;
                                multiCaseCreateDTOobj.sCaseIdentifier = requestDTO.CaseIdentifier;
                              multiCaseCreateDTOobj.sInteractingAbout = sIntAbtAccount;
                              multiCaseCreateDTOobj.sInteractingAboutType = oIntAbtMember.Interacting_About_Type__c;
                              multiCaseCreateDTOobj.sInteractingWith = oInteraction.Interacting_with__c;
                              multiCaseCreateDTOobj.sInteractingWithType = oInteraction.Interacting_With_type__c;
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
                String sAccRecType = Schema.SObjectType.Account.getRecordTypeInfosById().get(mapAccountRecordtype.get(sIntAbtAccount)).getName();
                oIntAbtMember.Interacting_About__c = sIntAbtAccount; 
                oIntAbtMember.Interacting_About_Type__c = mapRecordType.get(sAccRecType);
                if(mapIntAboutCaseUnknwn.containsKey(stemp)) //populating case details inner map
                {  
                
                List<UpdateInteractionRequest_DTO_HUM.CreateCaseDetails> templist = mapIntAboutCaseUnknwn.get(stemp);
                for(UpdateInteractionRequest_DTO_HUM.CreateCaseDetails requestDTO:templist)
                {
                     if(!setofCaseIndentifier.contains(requestDTO.CaseIdentifier)){
                        setofCaseIndentifier.add(requestDTO.CaseIdentifier);
                        CreateCaseRequest_DTO_V4_HUM multiCaseCreateDTOobj =  new CreateCaseRequest_DTO_V4_HUM();
                        multiCaseCreateDTOobj.caseCreateRequest = requestDTO.CaseDetails;
                        multiCaseCreateDTOobj.sInteractingAbout = sIntAbtAccount;
                        multiCaseCreateDTOobj.sInteractingAboutType = oIntAbtMember.Interacting_About_Type__c;
                        multiCaseCreateDTOobj.sInteractingWith = oInteraction.Interacting_with__c;
                        multiCaseCreateDTOobj.sInteractingWithType = oInteraction.Interacting_With_type__c;
                        multiCaseCreateDTOobj.sCaseIdentifier = requestDTO.CaseIdentifier;
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
        
       return oInteraction; 
     }
       
   /*  
    * Method name : insertInteractionAndIntMembers
    * Description : This method is used to insert interaction and interaction members
    * Return Type :  
    * Parameter   : interaction__c oInteraction, list<Interaction_Member__c> lstInteractionMembers, boolean bIsCasesRequired
    */    
    @TestVisible
    private static UpdateInteractionResponse_DTO_HUM insertInteractionAndIntMembers(interaction__c oInteraction, list<Interaction_Member__c> lstInteractionMembers, boolean bIsCasesRequired, list<case> lstCases)
    {  
     UpdateInteractionResponse_DTO_HUM  intResponseObj = new UpdateInteractionResponse_DTO_HUM();
     UpdateInteractionResponse_DTO_HUM.UpdateInteractionInfo interactionInfo = new UpdateInteractionResponse_DTO_HUM.UpdateInteractionInfo();             
          String intId = oInteraction.Id;  
          Boolean bIsIntMemberError = CaseInteractionSearch_H_HUM.createInteractingMembers(lstInteractionMembers, intId);
          for(CreateCaseRequest_DTO_V4_HUM ocaseReq : listOfMultiCaseCreateRequest_DTO_HUM)
          {
              ocaseReq.sInteractionId = intId;
          }
          List<CaseDetailResponse_DTO_V4_HUM> lstCaseResponse =  CreateCaseRestBulkified_D_V4_HUM.createCases(listOfMultiCaseCreateRequest_DTO_HUM,false);
          List<String> lstCasenumbers = CaseInteractionSearch_H_HUM.createCaseInteractionRecords(lstCaseResponse, intId, lstCases);
          List<CaseDetailResponse_DTO_V4_HUM> lstCaseErrorLogs = CaseInteractionSearch_H_HUM.getCaseErrorLogs(lstCaseResponse);
          if(!bIsIntMemberError && lstCaseErrorLogs.isEmpty())
          {
            interactionInfo.InteractionNumber = oInteraction.Name;
            interactionInfo.Success=true;
            if(lstCasenumbers!=null && !lstCasenumbers.isEmpty() && bIsCasesRequired) 
                { 
                    interactioninfo.CreatedCases =   lstCasenumbers;  
                }
          }
          else
          {            
            if(!lstCaseErrorLogs.isEmpty() && lstCaseErrorLogs!=null)
                { 
                   interactionInfo.ErrorCode = lstCaseErrorLogs[0].ErrorCode;
                   interactionInfo.ErrorMessage = lstCaseErrorLogs[0].ErrorMessage;
                   interactionInfo.Success = false;
                }
             if(bIsIntMemberError)
                {
                  interactionInfo.ErrorMessage ='Interaction Member creation failed';
                  interactionInfo.Success = false;                                 
                }      
                    
          }
        intResponseObj.UpdateInteractionResponse = interactionInfo;     
       return intResponseObj;
    }
}