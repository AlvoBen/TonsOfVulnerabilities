/************************************************************************************************************************
Apex class Name    : CaseTaskView_H_HUM_LWC
Version            : 1.0
Function           : Helper Class for Hum_CaseTasks_LWC
Created On         : Dec 12 2021
Test Class         : Hum_CaseTasks_T_LWC

Modification Log
*   Developer                        Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala           12/29/2021          Original Version
***************************************************************************************************************************/
public with sharing class CaseTaskView_H_HUM_LWC 
{
    /*
    * This method get medicare comments for cases
    * @param  NA
    * @return String
    */
    Public static Map<id,list<CaseCommentType_Junction__c>> getCommentsMap(list<String> mapOfUnassignedRecords,map<Id, CaseComment> mapMediCC)
    {
        list<CaseCommentType_Junction__c> lstCCTJunc = [SELECT Id, Name, CaseCommentType__r.Description__c, CaseCommentType__r.Name, CaseComment__c FROM CaseCommentType_Junction__c WHERE CaseComment__c IN :mapMediCC.keyset() order by createdDate ASC];
        Map<id,list<CaseCommentType_Junction__c>> mapMediCCTemp = new Map<id,list<CaseCommentType_Junction__c>>();
        Map<id,list<CaseCommentType_Junction__c>> caseCCTJunc = new Map<id,list<CaseCommentType_Junction__c>>();
        if(mapMediCC.keyset() != null && !mapMediCC.keyset().isEmpty() && lstCCTJunc != null && !lstCCTJunc.isEmpty())
        {
            for(id medcom : mapMediCC.keyset())
            {
              list<CaseCommentType_Junction__c> listccJun = new list<CaseCommentType_Junction__c>();
              
              for(CaseCommentType_Junction__c ccJun :lstCCTJunc)
              {
              if(ccJun.CaseComment__c == medcom && ccJun.CaseCommentType__r.Name == HumConstants_LWC.MEDICARECALLS_RESO_HUM)
              listccJun.add(ccJun);
              }
              if(listccJun != null && !listccJun.isEmpty())
              mapMediCCTemp.put(Id.valueOf(medcom),listccJun);
            }
        }
        if(mapMediCCTemp.keyset() != null && !mapMediCCTemp.keyset().isEmpty())
        {
            for(String s : mapOfUnassignedRecords)
            {
               list<CaseCommentType_Junction__c> listccJunTemp = new list<CaseCommentType_Junction__c>();
               for(string medcom : mapMediCCTemp.keyset())
               {
                    if(mapMediCC.get(Id.valueOf(medcom)) !=null)
                    {
                       CaseComment c = mapMediCC.get(Id.valueOf(medcom));
                       if(c.parentid == s)
                       {
                         if(mapMediCCTemp.get(Id.valueOf(medcom)) != null && !mapMediCCTemp.get(Id.valueOf(medcom)).isEmpty())
                         {
                           listccJunTemp.addall(mapMediCCTemp.get(Id.valueOf(medcom)));
                         }
                       }
                     }
               }
               if(listccJunTemp != null && listccJunTemp.size()>0)
               caseCCTJunc.put(Id.valueOf(s),listccJunTemp);  
            }
        }
        return caseCCTJunc;
    }
    
    /*
    * This method checks for last inserted medicare comment
    * @param  NA
    * @return String
    */
    Public static String getMedCom(list<CaseCommentType_Junction__c> lstCCTJunc, map<Id, CaseComment> mapMediCC)
    {
        integer i =0;
        String sMedicareResInit = '';
        datetime create;
        for (CaseCommentType_Junction__c oCCTJ : lstCCTJunc)
        {
            if (oCCTJ.CaseCommentType__r.Name == HumConstants_LWC.MEDICARECALLS_RESO_HUM && mapMediCC.containsKey(Id.valueOf(oCCTJ.CaseComment__c)))
            {
            if(i==0)
            {
                create = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).createddate;
                sMedicareResInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
            }
            else if(mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).createddate>create)
            {
                create=mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).createddate;
                sMedicareResInit = mapMediCC.get(Id.valueOf(oCCTJ.CaseComment__c)).CommentBody;
            }
            }
            i = i+1;
        }
        return sMedicareResInit;
    }
    
    /*
    * This method checks CustomerServiceAccess for user
    * @param   caserecord origin and permission set list
    * @return  true Or false
    */
    public static Boolean validateMedicareCustomerServiceAccess(String sOrigin, list<PermissionSetAssignment> pCheck)
    {
        Boolean checkMedicareCustomerServiceAccess = false;
        try
        {
            String UserId = Userinfo.getUserId();
            if(pCheck.size()>0 && sOrigin == HumConstants_LWC.MEDICARECALLS_CASEORIGIN_HUM)
                checkMedicareCustomerServiceAccess = true;    
            else { checkMedicareCustomerServiceAccess = false; }
        }
        catch (Exception e)
        {
            checkMedicareCustomerServiceAccess = false;
        }
        return checkMedicareCustomerServiceAccess;
    }
    
    /*
    * This method checks for QAA process attached to the case record
    * @param   caserecord id
    * @return  true Or false
    */
    public static boolean checkProcess(id CaseId, List<Case_Action_Association__c> lstCaseAction)
    {
        Boolean bSuccess = false;
        try{
            List<Case_Action_Association__c> lstCaseActionAssociation = new List<Case_Action_Association__c>();
            if(lstCaseAction != Null && !lstCaseAction.isEmpty())
            {
                for(Case_Action_Association__c caseAction: lstCaseAction)
                {
                    if(caseAction.Case__c == CaseId)
                    {
                    lstCaseActionAssociation.add(caseAction);
                    break;
                    }
                }   
            }
            bSuccess =  (lstCaseActionAssociation != Null && !lstCaseActionAssociation.IsEmpty())? true : false ;
        }catch(exception ex){
            HUMExceptionHelper.logErrors(ex,'CaseTaskView_H_HUM_LWC', 'checkProcess');
        }
        return bSuccess;
    }
    
    /*
    * This method checks whether selected policy is Medicare policy based on SegmentType
    * @param  NA
    * @return Boolean
    */
    public static Boolean checkProductSegmentType(Policy__c policy)
    {
        Boolean segmentTypeFlag = false;

        if (policy.Product__r.Segment_Type__c != null && policy.Product__r.Segment_Type__c != '')
        {
            if (policy.Major_Lob__c == HumConstants_LWC.MAJOR_LOB_MCD  && policy.Product__r.Segment_Type__c == HumConstants_LWC.SEGMENT_TYPE_MCD)
            {
                segmentTypeFlag = true;
            }
            else if (policy.Major_Lob__c == HumConstants_LWC.MAJOR_LOB_MER && policy.Product__r.Segment_Type__c == HumConstants_LWC.SEGMENT_TYPE_MCR)
            {
                segmentTypeFlag = true;
            }
        }
        return segmentTypeFlag;
    }
    
    /*
    * This method checks whether case has creditable cverage attached
    * @param  NA
    * @return Boolean
    */
    public static Boolean disableChangeOwnerCreditableCoverage(List <Template_Submission_Data__c> flowValue,string caseowner, Boolean bGuidedFlag, String status, string s,boolean bEditCheck)
    {
        List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
        if(flowValue != Null && !flowValue.isEmpty())
        {
            for(Template_Submission_Data__c t: flowValue)
            {
                if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.ATTESTATION_TEMPLATE_NAME)
                flowLst.add(t);
            }   
        }
        if(bEditCheck) return (flowLst.size()>0 && caseowner == HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER && bGuidedFlag == true && status == HumConstants_LWC.CASE_STATUS_PENDING_RESPONSE) ? true : false;  
        else return (flowLst.size()>0 && status == HumConstants_LWC.CASE_STATUS_PENDING_RESPONSE && bGuidedFlag == true) ? true : false;                          
    }
    
    /*
    * This method checks whether case has MedExpedited attached
    * @param  NA
    * @return Boolean
    */
    public static Boolean isMedExpProcessAttachedExp(Set<String> grpLst, List <Template_Submission_Data__c> flowValue,string ownerQueue, String autoRouteStatus, String status, string s,boolean bEditCheck)
    {
        List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
        Boolean bQueueUser = true;
        if(grpLst != Null && !grpLst.isEmpty())
        {
            if(grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ) && grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVE))
                bQueueUser = false;
            else
                bQueueUser = true;
        }
        boolean bFlag = false;
        if(bEditCheck)
        {
            if (autoRouteStatus == HumConstants_LWC.autoRouteStatus_EvalExp || (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening))
            {
                bFlag = True;
            }
        }
        else
        {
            if (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening)
            {
                bFlag = True;
            }
        }
                           
        if(flowValue != null && !flowValue.isEmpty())
        {
            for(Template_Submission_Data__c t: flowValue)
            {
                if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.MedExpTEMPLATEUNIQUEID)
                flowLst.add(t);
            }   
        }
        return (flowLst.size()>0  && bQueueUser && status != 'Closed' && bFlag) ? true : false;
     }
     
    /*
    * This method checks whether case has PartD attached
    * @param  NA
    * @return Boolean
    */
    public static Boolean isMedExpProcessAttachedPartD(Set<String> grpLst, List <Template_Submission_Data__c> flowValue,string ownerQueue, String autoRouteStatus, String status, string s,boolean bEditCheck)
    {
        List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
        Boolean bQueueUser = true;
        if(grpLst != null && !grpLst.isEmpty())
        {
            if(grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ) && grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVP))
                bQueueUser = false;
            else
                bQueueUser = true;
        }
        boolean bFlag = false;
        if(bEditCheck)
        {
            If (autoRouteStatus == HumConstants_LWC.autoRouteStatus_EvalPartD || (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening)){
                bFlag = True;
            }
        }
        else
        {
            If (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening){
                bFlag = True;
            }
        }                       
        if(flowValue != null && !flowValue.isEmpty())
        {
        for(Template_Submission_Data__c t: flowValue)
        {
            if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.PartDTEMPLATEUNIQUEID)
                flowLst.add(t);
            }   
        }
        return (flowLst.size()>0 && bQueueUser  && status != 'Closed' && bFlag) ? true : false;
    }
    
    /*
    * This method checks whether case has PCP attached
    * @param  NA
    * @return Boolean
    */
    public static Boolean isPCPProcessAttached(Set<String> grpLst, List <Template_Submission_Data__c> flowValue,string ownerQueue, String autoRouteStatus, String status, string s,boolean bEditCheck)
    {
        List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
        boolean bQueueUser = true;
        if(grpLst != null && !grpLst.isEmpty())
        {
            if(grpLst.contains(HumConstants_LWC.PCPGROUPNAME))
            bQueueUser = false;
        else
            bQueueUser = true;
        }
        boolean bFlag = false;
        if(bEditCheck)
        {
            If (autoRouteStatus == HumConstants_LWC.AUTOROUTING_PCP){
                bFlag = True;
             }     
        }
        else
        {
            If (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research){
                bFlag = True;
             }   
        }
       
        if(flowValue != null && !flowValue.isEmpty())
        {
            for(Template_Submission_Data__c t: flowValue)
            {
                if(bEditCheck)
                {
                    if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.PCPTEMPLATEUNIQUEID && t.Template_Field__r.Name == HumConstants_LWC.SERVICEFUNDCHECK)
                    flowLst.add(t);    
                }
                else
                {
                    if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.PCPTEMPLATEUNIQUEID)
                    flowLst.add(t);
                }
            }   
        }
        if(bEditCheck) return (flowLst.size()>0 && flowLst[0].Value__c == 'Yes' && bQueueUser  && status != 'Closed' && bFlag) ? true : false;
        else return (flowLst.size()>0 && bQueueUser  && status != 'Closed' && bFlag) ? true : false;
       
    }
    
    /*
    * This method checks whether case has GoodCause
    * @param  NA
    * @return Boolean
    */
    public static Boolean isMedExpProcessAttachedGoodCause(Set<String> grpLst, List <Template_Submission_Data__c> flowValue,string ownerQueue, String autoRouteStatus, String status, string s,boolean bEditCheck)
    {
        List <Template_Submission_Data__c> flowLst = new List<Template_Submission_Data__c>();
        flowLst = new List<Template_Submission_Data__c>();
        boolean bQueueUser = true;
        if(grpLst != null && !grpLst.isEmpty())
        {
            if(grpLst.contains(HumConstants_LWC.GOODCAUSEGROUPNAME))
            bQueueUser = false;
        else
            bQueueUser = true;
        }
        boolean bFlag = false;
        if(bEditCheck)
        {
            If ((autoRouteStatus == HumConstants_LWC.AUTOROUTING_GOODCAUSE || autoRouteStatus == HumConstants_LWC.AUTOROUTING_GOODCAUSETIE)|| (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.GOODCAUSEQUEUE)){
                bFlag = True;
            }
        }
        else
        {
            If (autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && ownerQueue == HumConstants_LWC.GOODCAUSEQUEUE){
                bFlag = True;
            }
        }
       
        if(flowValue != null && !flowValue.isEmpty())
        {
            for(Template_Submission_Data__c t: flowValue)
            {
                if(bEditCheck)
                {
                    if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.GOODCAUSEUNIQUEID && t.Template_Field__r.Name== HumConstants_LWC.GCRTERMEDPLAN)
                    flowLst.add(t);
                }
                else
                {
                    if(t.Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c == s && t.Template_Submission__r.Template__r.Unique_Template_Name__c == HumConstants_LWC.GOODCAUSEUNIQUEID)
                    flowLst.add(t);
                }
            }   
        }
        if(bEditCheck) return (flowLst.size()>0 && flowLst[0].Value__c == 'Yes' && bQueueUser  && status != 'Closed' && bFlag) ? true : false;
        else return (flowLst.size()>0 && bQueueUser  && status != 'Closed' && bFlag) ? true : false;
    }
    
    /*
    * This method checks whether case can be edited from detail page
    * @param  NA
    * @return Boolean
    */
    public static Boolean checkDetailPageClose(List <Template_Submission_Data__c> flowValue,string caseowner, Boolean bGuidedFlag,Set<String> grpLst,string ownerQueue, String autoRouteStatus, String status, string s, string caseRecordTypeName)
    {
        Boolean disableCaseOwner = false;
        Boolean disableCaseAfterExp = false;
        Boolean disableCaseAfterPart = false;
        Boolean disableCaseAfterPCP = false;
        Boolean disableCaseAfterGoodCause = false;

        if(caseowner  == HumConstants_LWC.CASE_RSO_ENROLLMENT_OWNER)
        {
            disableCaseOwner = disableChangeOwnerCreditableCoverage(flowValue,caseowner,bGuidedFlag,status,s,false);
        }
        
        if(caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_MEMBERCASE_HUM) && autoRouteStatus == HumConstants_LWC.HUM_COMPLETED && disableCaseOwner==false)
        {
            //disableCaseAfterExp
            if(ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening && !(grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ) && grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVE)))
            disableCaseAfterExp=isMedExpProcessAttachedExp(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,false);
            
            //disableCaseAfterPart
            if(ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening && !(grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ) && grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVP)))
            {
                disableCaseAfterPart = isMedExpProcessAttachedPartD(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,false);
            }

            //disableCaseAfterPCP
            if(ownerQueue == HumConstants_LWC.WORK_QUEUE_PCP_Assignment_Research  && !grpLst.contains(HumConstants_LWC.PCPGROUPNAME))
            {
                disableCaseAfterPCP = isPCPProcessAttached(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,false);
            }
            
            
            //disableCaseAfterGoodCause
            if(ownerQueue == HumConstants_LWC.GOODCAUSEQUEUE && !grpLst.contains(HumConstants_LWC.GOODCAUSEGROUPNAME))
            {
                disableCaseAfterGoodCause = isMedExpProcessAttachedGoodCause(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,false);
            }
        }
        return (disableCaseOwner||disableCaseAfterExp||disableCaseAfterPart||disableCaseAfterPCP||disableCaseAfterGoodCause);

    }
    
     /*
    * This method checks whether case can be edited from Edit page
    * @param  NA
    * @return Boolean
    */
    public static Boolean checkEditPageClose(List <Template_Submission_Data__c> flowValue,string caseowner, Boolean bGuidedFlag,Set<String> grpLst,string ownerQueue, String autoRouteStatus, String status, string s)
    {
        Boolean disableCaseAfterExp = false;
        Boolean disableCaseAfterPart = false;
        Boolean disableCaseAfterGoodCause = false;
    
        //disableCaseAfterExp
        if((autoRouteStatus == HumConstants_LWC.autoRouteStatus_EvalExp || ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening) && !(grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ) && grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVE)))
        disableCaseAfterExp=isMedExpProcessAttachedExp(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,true);
        
        //disableCaseAfterPart
        if((ownerQueue == HumConstants_LWC.WORK_QUEUE_LV_Screening || autoRouteStatus == HumConstants_LWC.autoRouteStatus_EvalPartD) && !(grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGQ) && grpLst.contains(HumConstants_LWC.Group_LV_Correspondence_Screening_PGVP)))
        {
            disableCaseAfterPart = isMedExpProcessAttachedPartD(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,true);
        }
        
        //disableCaseAfterGoodCause
        if(ownerQueue == HumConstants_LWC.GOODCAUSEQUEUE && !grpLst.contains(HumConstants_LWC.GOODCAUSEGROUPNAME))
        {
            disableCaseAfterGoodCause = isMedExpProcessAttachedGoodCause(grpLst,flowValue,ownerQueue,autoRouteStatus,status,s,true);
        }
        
        return (disableCaseAfterExp||disableCaseAfterPart||disableCaseAfterGoodCause);
    }
    
    /*
    * This method saves the case comment record
    * @param   NA
    * @return  void
    */
    @future
    public static void addCaseComment(string comment, map<string,Boolean> mapCaseComment, List<string> lstIds)
    {
        list<CaseComment> commentList = new list<CaseComment>();
        list<CaseComment> commentListInserted = new list<CaseComment>();
        list<Id> resolutionCase= new list<Id>();
        Database.SaveResult[] updatedCommentList;
        list<CaseCommentType__c> lstCCT = [SELECT Id FROM CaseCommentType__c WHERE Name = :HumConstants_LWC.MEDICARECALLS_RESO_HUM];
        
        if(lstIds != null && !lstIds.isEmpty())
        {
            for(String s : lstIds)
            {
                CaseComment caseCommentRec = new CaseComment();
                caseCommentRec.ParentId = s;
                if(mapCaseComment.containsKey(s) && mapCaseComment.get(s))
                {
                    caseCommentRec.CommentBody = HumConstants_LWC.Resolution_Comment+comment;
                    resolutionCase.add(s);
                }
                else
                    caseCommentRec.CommentBody = comment;
                commentList.add(caseCommentRec);
             }
         }
         if(commentList != null && !commentList.isEmpty())
         updatedCommentList = Database.insert(commentList, false);
         list<string> lstCommentIds = new list<string>();
         List<CaseCommentType_Junction__c> lstCCTJuninsert = new List<CaseCommentType_Junction__c>();
         List<casecomment> lstCCTJunToInsert = new List<casecomment>();
         if(updatedCommentList != null && !updatedCommentList.isEmpty())
         {  
            
            for(Database.SaveResult sr : updatedCommentList)
            {
                if(sr.isSuccess())
                {
                    lstCommentIds.add(sr.getId());
                }
            }
         }
         if(lstCommentIds != null && !lstCommentIds.isEmpty())
         {
             commentListInserted = [Select ParentId from CaseComment where id in :lstCommentIds];
             if(commentListInserted != null && !commentListInserted.isEmpty())
             for(casecomment cm: commentListInserted)
             {
                if(resolutionCase.contains(cm.ParentId)) 
                {
                  lstCCTJunToInsert.add(cm);
                }
             }
         }
         lstCCTJuninsert.addall(AssigncaseCommentTypeJunction(lstCCTJunToInsert,lstCCT));
         if(lstCCTJuninsert!=null && lstCCTJuninsert.size()>0) Database.insert(lstCCTJuninsert, false);
     }
     
     /*
     * This method is used to create CaseCommentType_Junction records
     * @param  CaseComment
     * @param  string
     * @return void 
     */
    public static List<CaseCommentType_Junction__c> AssigncaseCommentTypeJunction(list<CaseComment> oCaseCom,list<CaseCommentType__c> lstCCT)
    {
        List<CaseCommentType_Junction__c> lstCCTJun = new List<CaseCommentType_Junction__c>();
        if(lstCCT != null && !lstCCT.isEmpty())
        {  
           for(casecomment cm : oCaseCom)
           {
            CaseCommentType_Junction__c oCCTJunc = new CaseCommentType_Junction__c();
            oCCTJunc.CaseCommentType__c = lstCCT[0].Id;
            oCCTJunc.CaseComment__c = cm.Id;
            lstCCTJun.add(oCCTJunc);
           }
        }
        return lstCCTJun;
    }
    
    /*
    * <p>
    * Method to check the record type of case and setting flags which will be used for rendering
    * @param  case record type, interacting about type
    * @Return Boolean
    */
    public static Boolean checkRecordType(String caseRecordTypeName, String sInteractingAboutType)
    {
        Boolean bGAndASectionRender = false;
        if(caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_AGENTBROKERCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_GROUPCASE_HUM) || caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_UNKNOWNCASE_HUM) || (caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.MEDICARE_CASE) && sInteractingAboutType.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_UNKNOWNMEMBER_HUM )))
        {
            bGAndASectionRender = false;
        }
        else if(caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_PROVIDERCASE_HUM))
        {
            bGAndASectionRender = true;
        }
        else if(caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.CHANGECASEOWNER_MEMBERCASE_HUM) || (caseRecordTypeName.equalsIgnoreCase(HumConstants_LWC.MEDICARE_CASE) && sInteractingAboutType.equalsIgnoreCase(HumConstants_LWC.MEMBER_RECORDTYPE_HUM )))
        {
            bGAndASectionRender = true;
        }
        return bGAndASectionRender;
    }
    
   
}