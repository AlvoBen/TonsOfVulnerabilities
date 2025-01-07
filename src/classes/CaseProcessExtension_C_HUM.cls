/**
   @Author      : Prasad Jandhyala
   @name        : CaseProcessExtension_C_HUM
   @CreateDate  : 11/02/2017
   @Description : This class serves as controller for CaseProcessDataTable_CMP_HUM component
   @Version     : 1.0
   @reference   : CaseProcessDataTable_CMP_HUM
   
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Prasad Jandhyala         149201                      11/02/2017                   Original version   
 * Pradeepkumar Dani        149201                      11/17/2017                   re-factoring
 * Prasad Jandhyala         165081                      01/04/2018                   Fix for Defect 335626.0001
 * Bharath Rajappan         204270                      03/26/2018                   Defect Fix - remove unsaved template data before populate on process section
 * Bharath Rajappan         204793                      03/27/2018                   REQ - 359255 - Resolve issue reported in CA Ticket # 7435297
  * Vijaya Koriprolu                  11/11/2018            						 REQ - 371427- Logging for Provide claims
 * Moshitha Gunasekaran     348700                            03/25/2019             REQ - 380742 - Ability to view Transaction Reply Report (TRR) Process data for a specific member on a Medicare case
 * Santhosh Ganji                                       04/19/2019                   REQ - 386935 - Case Detail Restrictions (MED vs non-MED cases)—READ ONLY features
 * Santhosh Ganji                                       05/24/2019                   REQ - 400147 - Humana Pharmacy Web Chat users Access to cases
 * Moshitha Gunasekaran                                 02/04/2020                   US752867 - New TRR Templates Process Section
 * Jasmeen Shangari                                     04/30/2020                   Fix for defect 642
 * Mohammed Noor                                 		08/03/2020           		 US825176 - Route to new version of COB page based on the ON/OFF switch
 * Mohammed Noor                       					02/01/2021					 US1921246-PR94254-MF4-AEP Performance - SF Limits - Sync CPU Time Limit Optimization
 * Prudhvi Pamarthi                                     06/17/2021                   US#1529368 Adding Success/Fail Message within Title of Case Process/Attachment
 * Prudhvi Pamarthi                                     07/29/2021                   Rollback of US#1529368 Adding Success/Fail Message within Title of Case Process/Attachment
 * Gowthami Thota									    08/27/2021					 US - 2543348 - T1PRJ0002606/PR00094268  - CR01 CSI 1922  - Performance improvement of Case detail and Edit page
 * Prudhvi Pamarthi                                     09/20/2021                   US#2556003 Adding Success/Fail Message within Title of Case Process/Attachment
 * Prasuna Pattabhi                                     10/29/2021                  Rollback US#2556003 Adding Success/Fail Message within Title of Case Process/Attachment
 * Ajay Chakradhar                                      05/19/2022                   US#2556003 Adding Success/Fail Message within Title of Case Process/Attachment
 ********************************************************************************************************************************/
Public With sharing Class CaseProcessExtension_C_HUM 
{
    Public String columnName{ Get; Set; }
    Public String columnHeader{ Get; Set; }
    public string trrColumnName{ get; Set; }
    public string trrColumnHeader{ get; Set; }
    public string caseOrigin {get;set;}
    public boolean bCOBTemplateSwitch {get; set;}    
    Public Static final Set<String> demographicTemplates = New Set<String>{'Contact Demographic Update - Commercial','Critical Demographic Update - Commercial','Contact Demographic Update - Medicare','Critical Demographic Update - Medicare','Address Update - Medicare','Address Update - Medicaid'};    
    
    /**
    *  Constructor for the class
    *  @name CaseProcessExtension_C_HUM
    *  @param NA
    *  @return void
    *  @throws NA
    */ 
    public CaseProcessExtension_C_HUM()
    {
        Id caseIdRecord =  ApexPages.currentPage().getParameters().get('Id');
        list<Case> lstCaseRec = null;
        if(caseIdRecord != null)
            lstCaseRec = [SELECT Origin FROM Case WHERE ID =:caseIdRecord Limit 1];
        //Retrieve Origin and store in variable to check on component and display sorting based on that
        if(lstCaseRec != null && !lstCaseRec.isEmpty() && string.isNotBlank(lstCaseRec[0].Origin))
        {
            caseOrigin = lstCaseRec[0].Origin;
        }
        //Made code changes to check if Origin is TRR then different columns to be displayed for Process Section
        if(string.isNotBlank(caseOrigin) && (caseOrigin.equals(CaseDetailConstants_HUM.TRR_ORIGIN)))
        {
            trrColumnName = json.serialize(new list<String>{ 'ProcessNumber', 'ProcessType' ,'CreatedDate', 'CreatedBy' ,'ReplyCode','EffectiveDate', 'ProcessedDate'});
            trrColumnHeader = json.serialize(new list<String>{ 'Process Number', 'Process Type' ,'Created Date', 'Created By' ,'Reply Code','Effective Date', 'Processing Date Stamp' });
        } 
        
        columnName = json.serialize(new list<String>{ 'ProcessType' ,'CreatedDate', 'CreatedBy' ,'LastModifiedDate','LastModifiedBy'});
        columnHeader = json.serialize(new list<String>{ 'Process Type' ,'Created Date', 'Created By' ,'Last Modified Date','Last Modified By' });
        
        bCOBTemplateSwitch = HUMUtilityHelper.isCRMFunctionalityON('825176') ? true : false;
        
    }
    
    /**
    * This Remote Method is invoked from Component and generates list of Case Articles associated to the case.
    * 
    * @param    sCaseId                             Case record ID
    * @return                                       List of Case Process records
    */
    @RemoteAction
    public static list <CaseProcess_DTO_HUM> getJSONData(String sCaseId) {
        return getWrapperClass(sCaseId);
    }       
    
    /**
    * This Remote Method is invoked from Component and generates list of TRR Case Process associated to the case.
    * 
    * @param    sCaseId                             Case record ID
    * @return                                       List of Case Process records
    */
    @RemoteAction
    public static list <CaseProcess_DTO_HUM> getTRRJSONData(String sCaseId) {
        return getTRRData(sCaseId);
    }
    
     /**
    * This method generates list of Case Processes associated to the case.
    * 
    * @param    sCaseId                             Case record ID
    * @return   list<CaseArticle_DTO_HUM>           List of Case Process records
    */
    public static list<CaseProcess_DTO_HUM> getWrapperClass(Id sCaseId)
    {
        Set<String> wqueues = new Set<String>{CaseDetailConstants_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,CaseDetailConstants_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,CaseDetailConstants_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,CaseDetailConstants_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
        Set<String> chatQueues = new Set<String>(CaseDetailConstants_HUM.WEB_CHAT_WQUEUES);
        String sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
        Boolean bdisableLink = false;
        Boolean bInvalidTemplate = false;
        Boolean b1921246_A = HUMUtilityHelper.isCRMFunctionalityON('1921246_A');
        //This will remove the unsaved template data and helps rep to launch template form again.
        if(b1921246_A)
        {
            bInvalidTemplate = HUMValidSubmissionHelper.deleteInValidSubmissionAsynch(sCaseId); 
        }
        else
        {
            HUMValidSubmissionHelper.deleteInValidSubmission(sCaseId);  
        }                
                        
        Case oCase = [SELECT Id, Case_Owner__c, Owner_Queue__c, Origin, RecordType.name,Created_By_Queue__c, Policy_Member__c, Policy_Member__r.Product__c,
                     (SELECT LastModifiedDate, LastModifiedBy.Name, LastModifiedById, CreatedDate, CreatedBy.Name, CreatedById, Case__c, Case_Action__r.Name, Case_Action__r.CreatedBy.Name,Case_Action__r.LastModifiedBy.Name,Case_Action__c, Action__c
                      FROM Case_Action_Associations__r ORDER BY Id DESC)
                      FROM Case WHERE id = :sCaseId];
                      
        User uUser = [SELECT Id, Current_Queue__c FROM User WHERE Id = :UserInfo.getUserId()];
         
        list<CaseProcess_DTO_HUM> lstCaseArt = new list<CaseProcess_DTO_HUM>();
        Integer iOldProcessCount = 0; 
        if(oCase !=null)
        {
            if(oCase.Recordtype.name.containsIgnoreCase(CaseDetailConstants_HUM.HumHPRecType) || (oCase.Policy_Member__c !=null && oCase.Policy_Member__r.Product__c == CaseDetailConstants_HUM.MEDICARE_POLICY) 
              || wqueues.contains(oCase.Owner_Queue__c) || wqueues.contains(oCase.Created_By_Queue__c) || (chatQueues.contains(oCase.Owner_Queue__c) && oCase.Origin == CaseDetailConstants_HUM.WEB_CHAT_ORIGIN))
            {
                bdisableLink = true;
            }
              
            if(oCase.Case_Action_Associations__r!=null && !oCase.Case_Action_Associations__r.isEmpty())
            {
                iOldProcessCount = oCase.Case_Action_Associations__r.size();
                for (Case_Action_Association__c oCa : oCase.Case_Action_Associations__r)
                {
                    CaseProcess_DTO_HUM OcaseArt = new CaseProcess_DTO_HUM();           
                    OcaseArt.LastModifiedBy = oCa.LastModifiedBy.Name + CaseDetailConstants_HUM.sSrcUp + oCa.LastModifiedBy.Id; 
                    String createdDate = oCa.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                    OcaseArt.CreatedDate = createdDate;
                    String lastModifiedDate = oCa.LastModifiedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                    OcaseArt.LastModifiedDate = lastModifiedDate;
                    String caseProcessName =  oCa.Case_Action__r.Name;
                    if(sPharmacyUser != CaseDetailConstants_HUM.NONPHARMACY_USER_HUM && !bdisableLink)
                    {
                       OcaseArt.ProcessType = caseProcessName;
                    }
                    else
                    {
                       OcaseArt.ProcessType = caseProcessName + CaseDetailConstants_HUM.sProcessCaseIdURLParam + sCaseId +',' + caseProcessName ; 
                    }
                    OcaseArt.CreatedBy = oCa.CreatedBy.Name + CaseDetailConstants_HUM.sSrcUp + oCa.CreatedBy.Id   ;            
                    if (oCase.Case_Owner__c.equalsIgnoreCase(oCa.Case_Action__r.CreatedBy.Name))
                    {
                        OcaseArt.CreatedByQueue = oCase.Owner_Queue__c;
                    }
                    else
                    {
                        OcaseArt.CreatedByQueue = uUser.Current_Queue__c;
                    }
                    if (oCase.Case_Owner__c.equalsIgnoreCase(oCa.Case_Action__r.LastModifiedBy.Name))
                    {
                        OcaseArt.LastModifiedByQueue = oCase.Owner_Queue__c;
                    }
                    else
                    {
                        OcaseArt.LastModifiedByQueue = uUser.Current_Queue__c;
                    }
                    lstCaseArt.add(Ocaseart);
                }
            }
        }   
                                                                      
            if(!bInvalidTemplate)
        {
            Boolean bStmtTemplates = false;
	        String sServiceResponse = 'serviceresponse';
            Boolean benableProcessName;
            list<CaseProcess_DTO_HUM> lstCaseTemplates = new list<CaseProcess_DTO_HUM>();
            set<String> setStmtTemplates = new set<String>{'Send Claim Statement: Member','Send Claim Statement: Provider','Send Print Item: Member'};
            list<Template_Submission_Owner__c> lstSubOwner = [SELECT (SELECT Template__r.Description__c, LastModifiedDate, LastModifiedBy.Name, LastModifiedById, 
                                                            CreatedDate, CreatedBy.Name, CreatedById,Template__r.Unique_Template_Name__c FROM template_submissions__r) 
                                                            FROM template_submission_owner__c WHERE Object_Owner_ID__c =:sCaseId];
            if(lstSubOwner!=null && !lstSubOwner.isEmpty())
            {
                list<Template_Submission_Data__c> lstTempData = [SELECT Id, Template_Field__c, Template_Submission__c, Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c, value__C FROM Template_Submission_Data__c WHERE 
                                                                Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c =:sCaseId and Template_Field__r.name =:sServiceResponse];
                Map<String, string> mapTempDataValue = new Map<String, String>();
                if(lstTempData != null && lstTempData.size()>0)
                {
                    
                    for(Template_Submission_Data__c tempData : lstTempData)
                    {
                       
                        mapTempDataValue.put(tempData.Template_Submission__c, tempData.value__C);
                        
                    }
                }
		        for(Template_Submission_Owner__c oSubOwner : lstSubOwner)
                {
                    if(oSubOwner.template_submissions__r!=null && !oSubOwner.template_submissions__r.isEmpty())
                    {
                        for(Template_Submission__c oTempSub : oSubOwner.template_submissions__r)
                        {
                            if(CaseDetailConstants_HUM.TRR_TEMPLATE_NAME.equalsIgnoreCase(oTempSub.Template__r.Unique_Template_Name__c))	continue;	//Skip TRR Templates as they will be in different section.
							if(setStmtTemplates.contains(oTempSub.Template__r.Description__c))	bStmtTemplates = true;
							CaseProcess_DTO_HUM OcaseArt = new CaseProcess_DTO_HUM();                                      
		                    OcaseArt.LastModifiedBy = oTempSub.LastModifiedBy.Name+ CaseDetailConstants_HUM.sSrcUp + oTempSub.LastModifiedById;
                            String createdDate = oTempSub.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                            OcaseArt.CreatedDate = createdDate;
                            String lastModifiedDate = oTempSub.LastModifiedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat);
                            OcaseArt.LastModifiedDate = lastModifiedDate;
                            if(sPharmacyUser != CaseDetailConstants_HUM.NONPHARMACY_USER_HUM && !bdisableLink)
                            {
                               OcaseArt.ProcessType = oTempSub.Template__r.Description__c;
                            }
                            else
                            {
			                    benableProcessName = HUMUtilityHelper.isCRMFunctionalityON('2556003');
                                if(demographicTemplates.contains(oTempSub.Template__r.Description__c) && benableProcessName)
                                {
                                   string successVal = 'Success';
                                   string failVal = 'Fail';
                                   if(mapTempDataValue!=null && !mapTempDataValue.isEmpty() && mapTempDataValue.containsKey(oTempSub.id))
                                    {
                                    string val = mapTempDataValue.get(oTempSub.id);
                                    if(!string.isBlank(val) && (val == 'Success' || val == 'Warning'))
                                        OcaseArt.ProcessType = oTempSub.Template__r.Description__c+' - '+successVal+ CaseDetailConstants_HUM.sProcessCaseIdURLParam + sCaseId +',' + oTempSub.Template__r.Description__c+',' + oTempSub.Id;
                                    else
                                        OcaseArt.ProcessType = oTempSub.Template__r.Description__c+' - '+failVal+ CaseDetailConstants_HUM.sProcessCaseIdURLParam + sCaseId +',' + oTempSub.Template__r.Description__c+',' + oTempSub.Id;    
                                    }
                                    else
                                        OcaseArt.ProcessType = oTempSub.Template__r.Description__c + CaseDetailConstants_HUM.sProcessCaseIdURLParam + sCaseId +',' + oTempSub.Template__r.Description__c+',' + oTempSub.Id;
                                }
                                else
                                   OcaseArt.ProcessType = oTempSub.Template__r.Description__c + CaseDetailConstants_HUM.sProcessCaseIdURLParam + sCaseId +',' + oTempSub.Template__r.Description__c+',' + oTempSub.Id;
                            }
                            OcaseArt.CreatedBy = oTempSub.CreatedBy.Name+ CaseDetailConstants_HUM.sSrcUp + oTempSub.CreatedById;
                            if (oCase.Case_Owner__c.equalsIgnoreCase(oTempSub.CreatedBy.Name))
                            {
                                OcaseArt.CreatedByQueue = oCase.Owner_Queue__c;
                            }
                            else
                            {
                                OcaseArt.CreatedByQueue = uUser.Current_Queue__c;
                            }
                            if(oCase.Case_Owner__c.equalsIgnoreCase(oTempSub.LastModifiedBy.Name))
                            {
                                OcaseArt.LastModifiedByQueue = oCase.Owner_Queue__c;
                            }
                            else
                            {
                                OcaseArt.LastModifiedByQueue = uUser.Current_Queue__c;
                            }
                            lstCaseTemplates.add(OcaseArt);                         
                        }
                    }
                }
                if(lstCaseTemplates!=null && !lstCaseTemplates.isEmpty() && (iOldProcessCount <= 0 || bStmtTemplates))
                {
                    lstCaseArt.addAll(lstCaseTemplates);
                }
            }
        }
        return lstCaseArt;
    }
                
     /**
    * This method generates list of TRRCase Processes associated to the case.
    * 
    * @param    sCaseId                             Case record ID
    * @return   list<CaseArticle_DTO_HUM>           List of Case Process records
    */
    public static list<CaseProcess_DTO_HUM> getTRRData(Id sCaseId)
    {
        //This will remove the unsaved template data and helps rep to launch template form again.
        HUMValidSubmissionHelper.deleteInValidSubmission(sCaseId); 
        
        ID currentUser = UserInfo.getUserId();
        Case oCase = [SELECT Id, Case_Owner__c, Owner_Queue__c, Origin, RecordType.name,Created_By_Queue__c, Policy_Member__c, Policy_Member__r.Product__c FROM Case WHERE id = :sCaseId];
        User uUser = [SELECT Id, Current_Queue__c FROM User WHERE Id = :currentUser]; 
        list<CaseProcess_DTO_HUM>  lstCaseArt = new list<CaseProcess_DTO_HUM>();
        
        //Made code changes to check if Origin is TRR then different columns to be displayed for Process Section
        if(string.isNotBlank(oCase.Origin) && (oCase.Origin.equals(CaseDetailConstants_HUM.TRR_ORIGIN)))
        {
            list<string> lstTemplateFields = new List<string>{CaseDetailConstants_HUM.TRR_FIELD_REPLYCD, CaseDetailConstants_HUM.TRR_FIELD_EFFDATE, CaseDetailConstants_HUM.TRR_FIELD_PROCDATSTMP};
            list<Template_Submission_Data__c> lstTempData = [SELECT Id, Name, Template_Field__c, Template_Submission__c, 
            Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c, Template_Submission__r.Submission_Owner__r.Object_Owner_Type__c, Name__c, 
            Value__c, Template_Submission__r.CreatedDate, Template_Submission__r.CreatedBy.Name, Template_Submission__r.CreatedById, 
            Template_Submission__r.Template__r.Description__c, Template_Submission__r.Name FROM Template_Submission_Data__c WHERE 
            Template_Submission__r.Submission_Owner__r.Object_Owner_ID__c = :sCaseId AND Template_Submission__r.Template__r.Unique_Template_Name__c = :CaseDetailConstants_HUM.TRR_TEMPLATE_NAME
            AND Name__c IN :lstTemplateFields];
            
            //Retrieve all Templates and its data field and put it in map based on Template Submission   
            if(lstTempData != null && !lstTempData.isEmpty())
            {
                Map<String, list<Template_Submission_Data__c>> mapTempDataBySubmission = new Map<String, list<Template_Submission_Data__c>>();
                for(Template_Submission_Data__c tempData : lstTempData)
                {
                    if(mapTempDataBySubmission!=null && !mapTempDataBySubmission.isEmpty() &&
                       mapTempDataBySubmission.containsKey(tempData.Template_Submission__c))
                    {
                        mapTempDataBySubmission.get(tempData.Template_Submission__c).add(tempData); 
                    }
                    else
                    {
                        mapTempDataBySubmission.put(tempData.Template_Submission__c, new List<Template_Submission_Data__c>{tempData});
                    }
                }
                //Construct the ProcessSection data based on each TemplateSubmission retrieved from database
                if(!mapTempDataBySubmission.isEmpty())
                {
                    for(string submission : mapTempDataBySubmission.keySet())
                    {
                        CaseProcess_DTO_HUM oCaseArt = new CaseProcess_DTO_HUM();
                        list<Template_Submission_Data__c> lstTempSubData = mapTempDataBySubmission.get(submission);
                        if(lstTempSubData !=null && !lstTempSubData.isEmpty())
                        {
                            oCaseArt.CreatedDate = String.ValueOf(lstTempSubData[0].Template_Submission__r.CreatedDate.format(CaseDetailConstants_HUM.HUMDateTimeFormat));
                            oCaseArt.ProcessType = lstTempSubData[0].Template_Submission__r.Template__r.Description__c;
                            oCaseArt.CreatedBy = lstTempSubData[0].Template_Submission__r.CreatedBy.Name+ CaseDetailConstants_HUM.sSrcUp + lstTempSubData[0].Template_Submission__r.CreatedById;
                            oCaseArt.ProcessNumber = lstTempSubData[0].Template_Submission__r.Name+ CaseDetailConstants_HUM.sTRRProcessCaseIdURLParam + sCaseId +',' + lstTempSubData[0].Template_Submission__r.Template__r.Description__c +',' + lstTempSubData[0].Template_Submission__r.Name;
                            string processedDate = '';
                            string processedTime = '';
                            for(Template_Submission_Data__c tempData : lstTempSubData)
                            {
                                if(tempData.Name__c.equals(CaseDetailConstants_HUM.TRR_FIELD_REPLYCD))
                                {
                                    oCaseArt.ReplyCode = tempData.Value__c;
                                }
                                else if(tempData.Name__c.equals(CaseDetailConstants_HUM.TRR_FIELD_EFFDATE))
                                {
                                    if(string.isNotBlank(tempData.Value__c) && tempData.Value__c.length() == 10)
                                    {
                                        oCaseArt.EffectiveDate = tempData.Value__c.substring(5,7) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(8,10) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(0,4);
                                    }
                                    else
                                    {
                                        oCaseArt.EffectiveDate = tempData.Value__c;
                                    }   
                                }
                                else if(tempData.Name__c.equals(CaseDetailConstants_HUM.TRR_FIELD_PROCDATSTMP))
                                {
                                    if(string.isNotBlank(tempData.Value__c) && tempData.Value__c.length() == 10)
                                    {
                                        oCaseArt.ProcessedDate = tempData.Value__c.substring(5,7) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(8,10) + CaseDetailConstants_HUM.BACKSLASH_HUM + tempData.Value__c.substring(0,4);
                                    }
                                    else
                                    {
                                        oCaseArt.ProcessedDate = tempData.Value__c;
                                    }
                                }
                            }
                            lstCaseArt.add(oCaseArt);
                        }
                    }
                }
            }
        }
        return lstCaseArt;
    }
}