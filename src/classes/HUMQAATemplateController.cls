/******************************************************************************************************************************
Apex Class Name  : HUMQAATemplateController 
Version          : 4.0 
Created Date     : JUL 18 2014
Function         : Controller to handle display of QAA Questions and responses.                   
Test Class       : HUMQAATemplateControllerTest
Modification Log :
 *   Modification ID     Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *           1.0         Vivek Batham         17353           07/18/2014           Original version.
 *           1.1         Shruthi Karanth      17353           08/16/2014           Incorporated code review comments
 *           2.0         Krishna Tadikonda    17542           08/20/2014           Defects fixed.Incorporated code review comments
 *           3.0         Pradeep Raju         18067           09/22/2014           Page displayable error message is modified by modifying the custom label and referring the same in line#331 on HUMQAATemplateController  
 *           4.0         Shreya Choodamani    18878           10/30/2014           Updated the SOQL in the function prepareOnScreenQuestionsDisplay to fix the issue related to defect#: 159662.0001
 *           5.0         Santhi Mandava                       05/19/2015           Changed casObj type from private to public.
 *           6.0         Praveen Kumar Parimi                 06/17/2015           Added call to HUMEncryptObjectFieldsUtility to get the Case_Action_Response__c Encrypted at Line#331 , null check at Line#:392
 *           7.0         Santhi Mandava       71723           01/03/2017           Written logic to exclude enhanced QAA template questions.
 *           8.0         Santhi Mandava       87365           04/26/2017           Moved"QAATemplate_C_HUM" class code to this class. Enhanced QAA page will be visible to all users.
 *           9.0         Prudhvi Pamarthi     171453          01/22/2018           REQ - 328929 Fix for HUMExceptionHelper: DML 
 *           10.0        Santhi Mandava       214342          04/11/2018           REQ - 359924 Removed encryption logic.
 *           11.0        Praveen Kumar Parimi 262732          07/26/2018           REQ - 367051 aka CA Incident #7521926  Fix for Service Addresses not showing up when conducting a search in the QAA template.
 *           12.0        Prudhvi Pamarthi                     02/24/2020           US#849244 CA Ticket 8963843 Fix - QAA Process-Edit button still appears when Case Status equals closed
 *           13.0        Kavya Bondada                        04/14/2021           User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM)
 * 			 14.0		 Jasmeen Shangari                     04/21/2021           Checkmarx fixes Azure ID - 2163309
 *           15.0        Kavya Bondada                        05/06/2021           User Story 2187149: T1PRJ0001827 - MF 1 - Quality - Auto route QAA cases to the G&A team (CRM) Fixes
 *			 16.0		 Kavya Bondada                        05/20/2021           User Story 2271571: T1PRJ0001827 - MF 1 - AHT/Quality - Ability to default "Is the Member willing to provide their QAA Complaint details verbally?" to Yes in the QAA template (CRM)
 *           17.0        Amar Gadewar                         06/17/2021           User Story 2389201: T1PRJ0001827 - MF 1 - Quality - Ability to exclude FL LTC QAA Complaint cases from auto routing process (CRM)
 *           18.0        Samantha Tennyson                    06/22/2021           User Story 2393164: T1PRJ0001827 - MF 1 - SF - TECH - On/Off Switch Cleanup for 6/11/21, 6/25/21, 7/9/21 and 7/23/21 releases (CRM)
*********************************************************************************************************************************/
 
 public with sharing class HUMQAATemplateController 
 { 
    private list<Question__c> lstQuestions = new List<Question__c>();
    private map<String,Map<String,List<String>>> mapMasterConditions = new Map<String,Map<String,List<String>>>();
    private string caseId = '';
    private List<String> lstResponsesValidation = new list<string>();
    @testVisible Private Map<String, String> mapRequestDto;
    @testVisible Private string sEPSSReq = '';
    private Set<String> setProviderList = new Set<String>();
    @testVisible Private HUMCaseActionQUestionHelper objParentQuestion;
    @testVisible Private Map<String,HUMCaseActionQUestionHelper> mapProviderQuestions;
    @testVisible Private Set<String> setDropdownQuestionsList = new Set<String>();
    @testVisible Private Map<String,String> mapExtProviderDropdownsData;
    @testVisible Private list<HUMCaseActionQUestionHelper> lstWrapperQuestions = new List<HUMCaseActionQUestionHelper>();
    
     /**
    *  Description Initializes variables and gathers data into query variables.
    *  @name QAATemplate_C_HUM
    *  @param NA
    *  @return Void
    *  @throws NA
    */
    public HUMQAATemplateController()
    {     
        //Initializing     
        lstLogErrors = new List<Error_Log__c>();       
        lstVisibleQuestionsList = new List<HUMCaseActionQUestionHelper>();
        dispCaseResponses = new List<Case_Action_Response__c>();
        quesResponseMap = new Map<String, Case_Action_Response__c>();   
        bIsSuccess = false;
        lstResponsesValidation = new List<String>();
        prepareProviderSet();
        isProviderDetailsPresent = false;  
        bIsProviderSelected = false;
        mapProviderQuestions = new Map<String,HUMCaseActionQUestionHelper>();
        mapExtProviderDropdownsData = new Map<String,String>();
        bAutoRouteQAA = false;
        bStopAutopopulateYesNo = true;
        
        try
        { 
            initLoadEdit();    
        }
        catch(Exception e)
        {
            //As DML is not allowed inside constructor,stop the DML operation by this boolean
            HUMExceptionHelper.bRestrictLog = true;
            //store the list of exceptions captured
            lstLogErrors = HUMExceptionHelper.logErrors(e,'QAATemplate_C_HUM','QAATemplate_C_HUM');
        }                  
    }
    public static Boolean bStopAutopopulateYesNo;
    
    public Case casObj{get;set;}
    public Boolean isDisabled{get;set;}
    public List<HUMCaseActionQUestionHelper> lstVisibleQuestionsList{get;set;}
    public list<Case_Action_Response__c> dispCaseResponses{get; set;}   
    public map<String, Case_Action_Response__c> quesResponseMap {get; set;} 
    public string sTemplateType{get;set;}
    public String loadView{get; set;}
    public String sTabId {get; set;}
    public Boolean bIsSuccess {get; set;}
    public List<HUMCustomSearchHelper> lstProviderAccounts{get;set;}
    public Boolean isProviderDetailsPresent{get;set;}
    public Boolean bSizeException{get;set;}
    public Boolean isErrorExist{get;set;}
    Public Boolean bIsProviderSelected{get;set;}
    Public list<HUMCaseActionQUestionHelper> lstDropDownWrapperQuestions{get;set;}
    public Boolean bAutoRouteQAA {get; set;}
    public List<Error_Log__c> lstLogErrors
      {
        get
        {
          return lstLogErrors;
        }
        set;
      }

    
     /**
    *  Description Assigning variables based on page paramenters and generating basic queries needed.
    *  @name initLoadEdit
    *  @param NA
    *  @return Void
    *  @throws NA
    */
    public void initLoadEdit()
    {
        caseId = (Apexpages.currentPage().getParameters().containsKey(Label.HUMCOBCaseIdParam)  && Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam) != null) ? Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam) : null;        
        If(caseId != null && caseId instanceof Id)
        {
            casObj = [Select Id,Created_By_Queue__c ,Classification_Type__c,Autoroute_Status__c,Policy_Member__r.Product_Type__c,Policy_Member__r.Segment_Indicator__c,Policy_Member__r.Issue_State__c,CaseNumber,Status, Policy_Member__c,AccountId, Account.Enterprise_ID__c, Account.Name, Policy_Member__r.Name, Policy_Member__r.Member__c,
                      Policy_Member__r.Member__r.Name,Policy_Member__r.Group_Number__c, (Select Id, Case_Action__c, Action__c From Case_Action_Associations__r)  
                      from Case where Id =: caseId];
            if(casObj.Status == 'Closed' || casObj.Status == 'Cancelled')
                isDisabled = true;
            if(Apexpages.currentPage() != null && 
               Apexpages.currentPage().getParameters().containsKey(Label.HUMCOBProcessType) && 
               Apexpages.currentPage().getParameters().get(Label.HUMCOBProcessType) != null)
            {
                sTabId = Apexpages.currentPage().getParameters().containsKey(Label.HUMTabId) != null ?  Apexpages.currentPage().getParameters().get(Label.HUMTabId) : null;  
                sTemplateType = Apexpages.currentPage().getParameters().get(Label.HUMCOBProcessType) ;  
                if(casObj.Case_Action_Associations__r != null && casObj.Case_Action_Associations__r.size() > 0)
                {
                    loadView = Label.HUMPreXViewTypeView;
                    loadDisplayData(casObj.Case_Action_Associations__r[0].Id);
                    bIsProviderSelected = false;
                }
                else
                {
                    loadView = Label.HUMPreXViewTypeEdit;               
                    loadGuide();
                }
            } 
            List<CTCI_Junction__c> lstCTCIs = [SELECT Id, Name, Classification__r.name, Classification_Type__r.Name, Intent__r.Name, Classification__c, Classification_Type__c, Intent__c, IsActive__c FROM CTCI_Junction__c where Classification__r.name=: CaseAutoRouteConstant_C_HUM.sQAACalssification and Intent__r.Name=: CaseAutoRouteConstant_C_HUM.sQAAIntent and Classification_Type__r.name = :casObj.Classification_Type__c  and IsActive__c=TRUE]; 
            bAutoRouteQAA = (null!= lstCTCIs  && !lstCTCIs.isEmpty()) && (casObj.Autoroute_Status__c !='Completed') && UtilityHelper_H_AHT_HUM.isQAAAutoRoute(casObj);
        }  
    }

    /**
    *  Description This method retrieves the questions for the respective guide selected and loads the questions onto screen.
    *  @name loadGuide
    *  @param List<Case> lstNewCaseList -List of Case object being modified
    *  @return Void
    *  @throws NA
    */
    @TestVisible Private void loadGuide()
    {
        lstQuestions = [Select Id,Name,Question_Label__c,Parent_Question__c,Parent_Question__r.Question_Label__c,Question_Type__c,Question_Values__c,Dependent_Value__c, 
                        Display_Text__c,Display_Text_Area__c,Display_Checkbox__c,Display_Date__c, Sequence_Number__c, Help_Text__c 
                        from Question__c where Actions__r.Name =: sTemplateType AND Active__c = true ORDER BY Sequence_Number__c ASC NULLS LAST];
        if(lstQuestions.size() > 0)
            prepareOnScreenQuestionsDisplay(null);   
    }

    /**
    *  Description This method retrieves the questions for the respective guide selected and loads the questions onto screen.
    *  @name prepareOnScreenQuestionsDisplay
    *  @param List < Question__c >, Map< String, Case_Action_Response__c>
    *  @return Void
    *  @throws NA
    */
    @TestVisible Private void prepareOnScreenQuestionsDisplay(Map<String, Case_Action_Response__c> quesRespMap)
    {   
        HUMCaseActionQUestionHelper wrapQues = new HUMCaseActionQUestionHelper();           
        lstWrapperQuestions = new List<HUMCaseActionQUestionHelper>(); 
        lstDropDownWrapperQuestions = new List<HUMCaseActionQUestionHelper>(); 
                 
        if(quesRespMap != null && quesRespMap.size() > 0)
        {               
            lstQuestions = [Select Id,Name,Question_Label__c,Parent_Question__c,Parent_Question__r.Question_Label__c,Question_Type__c,Question_Values__c,Dependent_Value__c, 
                            Display_Text__c,Display_Text_Area__c,Display_Checkbox__c,Display_Date__c, Sequence_Number__c, Help_Text__c 
                            from Question__c where Actions__r.Name =: sTemplateType AND Active__c = true ORDER BY Sequence_Number__c ASC NULLS LAST];
        }
        //To store the parent responses needed to render the child.
        mapMasterConditions = new Map<String,Map<String,List<String>>>();
        for(Question__c ques : lstQuestions)
        {
            If(!ques.Question_Label__c.contains(GLOBAL_CONSTANT_HUM.CASE_PROVIDERNAME ))
            {
                wrapQues = new HUMCaseActionQUestionHelper();  
                getMasterConditions(ques);
                prepareWrapper(ques, wrapQues, quesRespMap);                 
                
                wrapQues = addClassName(wrapQues,ques);
                
                lstWrapperQuestions.add(wrapQues);
                if(ques.Question_Label__c == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERZIPCODE_HUM )
                {
                   createQuestionForCommandButtons(ques);
                }
            }
        }
        reCalculatePageQuestions();
    }
    
    /**
    *  Description This method gets all the conditions based on the master field responses.
    *  @name getMasterConditions
    *  @param Question__c
    *  @return Void
    *  @throws NA
    */
    private void getMasterConditions(Question__c ques)
    {
        if(ques.Parent_Question__c != NULL && ques.Parent_Question__r.Question_Label__c != NULL && ques.Dependent_Value__c != NULL)
        {
            List < String > sDependentValues = ques.Dependent_Value__c.split(';');
            Map < String, List < String >> mapInnerValue = new Map < String, List < String >> ();
            mapInnerValue.put(ques.Parent_Question__r.Question_Label__c, sDependentValues);
            mapMasterConditions.put(ques.Question_Label__c, mapInnerValue);
        }        
    }

    /**
    *  Description Checks the responses of the parent questions and reloads the valid child questions.
    *  @name reCalculatePageQuestions
    *  @param NA
    *  @return Void
    *  @throws NA
    */
    Public void reCalculatePageQuestions()
    {
        try
        {
            Map<String,String> mapCurrentResponses = new map<String,String>();
            String sCurrentParentValue = '';
            lstVisibleQuestionsList = new List<HUMCaseActionQUestionHelper>();
            for(HUMCaseActionQUestionHelper ques: lstWrapperQuestions)
            {
                if(ques.QuestionRec.Question_Label__c == '')
                {
                    ques.boolDisplayQuestion = false;
                    sCurrentParentValue = mapCurrentResponses.get(GLOBAL_CONSTANT_HUM.CASE_PROVIDERVERBALCOMPLAINT);
                    If(sCurrentParentValue == ques.QuestionRec.Dependent_Value__c)
                    {
                        ques.boolDisplayQuestion = true;
                        lstVisibleQuestionsList.Add(ques);
                    }
                }
                else
                {
                    if(ques.QuestionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_PROVIDERVERBALCOMPLAINT )
                    {
                        objParentQuestion = ques;
                        objParentQuestion.sClassName = GLOBAL_CONSTANT_HUM.CASE_PROVIDERPARENTQUESTIONCLASS ;
                    }
                    mapCurrentResponses.put(ques.QuestionRec.Question_Label__c,ques.sSelectedDropDownOption);
                    if(ques.QuestionRec.Parent_Question__c != NULL)
                    {
                        If(ques.QuestionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_PROVIDERDBA )
                        {
                            ques.boolDisplayQuestion = false;
                        }
                        else
                        {
                            //Checks if the Parent Question has the selected value to render the Question on page.
                            assignDisplayFlag(ques,mapCurrentResponses);   
                        }
                    }
                    else if(ques.boolDisplayQuestion)
                    {
                        lstVisibleQuestionsList.Add(ques);
                    }
                }
            }
            setProviderFieldsVisibility();
        }
        catch(Exception e)
        {
            //Restrict insert of error Logs
            HUMExceptionHelper.bRestrictLog=true;
            lstLogErrors = HUMExceptionHelper.logErrors(e, 'QAATemplate_C_HUM', 'reCalculatePageQuestions');
        }
    }       
    
    /**
    *  Description Checks and assigns if the Parent Question has the selected value to render the Question on page.
    *  @name assignDisplayFlag
    *  @param HUMCaseActionQuestionHelper,map<string, string>
    *  @return void
    *  @throws NA
    */
    private void assignDisplayFlag(HUMCaseActionQuestionHelper ques, map<string, string> mapCurrentResponses)
    {
        if(ques != null && ques.QuestionRec!= null && ques.QuestionRec.Question_Label__c != null && mapMasterConditions.containsKey(ques.QuestionRec.Question_Label__c) && 
        mapMasterConditions.get(ques.QuestionRec.Question_Label__c).containsKey(ques.QuestionRec.Parent_Question__r.Question_Label__c))
        {
            List<String> lstMatchingResponsesFromMasterMap = mapMasterConditions.get(ques.QuestionRec.Question_Label__c).get(ques.QuestionRec.Parent_Question__r.Question_Label__c);
            Map<String,List<String>> sMatchingQuestion = mapMasterConditions.get(ques.QuestionRec.Question_Label__c);
            String sCurrentParentValue = null;
            
            sCurrentParentValue = mapCurrentResponses.get(ques.QuestionRec.Parent_Question__r.Question_Label__c);
            ques.boolDisplayQuestion = false;         
            for(String value:lstMatchingResponsesFromMasterMap)
            {
                if(sCurrentParentValue == value)
                {
                    ques.boolDisplayQuestion = true;
                    lstVisibleQuestionsList.Add(ques);
                    break;
                }
            }
        } 
    }
        
    /**
    *  Description Creates the wrapper
    *  @name prepareWrapper
    *  @param Question__c, HUMCaseActionQuestionHelper, Map<String, Case_Action_Response__c>
    *  @return Void
    *  @throws NA
    */
    private void prepareWrapper(Question__c question,  HUMCaseActionQuestionHelper wrapQues, Map<String, Case_Action_Response__c> quesRespMap){
        
        wrapQues.questionRec = question;
        if(question.Question_Values__c !=NULL)
        {
            If(question.Question_Label__c != GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERFACILITYQUESTION )
            {
                wrapQues.lstDropDownOptions.add(new SelectOption(Label.HUMCOBNoneSelectOption,Label.HUMCOBNoneSelectOption));
            }
            for(String DropDownValue: question.Question_Values__c.split(';'))
            {
                If(question.Question_Label__c == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM)
                {
                    String[] selectArr = DropDownValue.split('-');
                    String selectValue= selectArr[0].trim();
                    String selectLabel = selectArr[1].trim();
                    wrapQues.lstDropDownOptions.add(new SelectOption(selectValue, DropDownValue));
                }
                else
                {
                    wrapQues.lstDropDownOptions.add(new SelectOption(DropDownValue,DropDownValue));
                }
            }
            If(question.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERFACILITYQUESTION )
            {
                If(quesRespMap != null && quesRespMap.get(question.Question_Label__c) != null)
                {
                    wrapQues.sSelectedDropDownOption = quesRespMap.get(question.Question_Label__c).Process_Question_Response__c;
                }
                else
                {
                    wrapQues.sSelectedDropDownOption = GLOBAL_CONSTANT_HUM.PROVIDER_TYPE;
                }
            } 
            else
            {
                wrapQues.sSelectedDropDownOption = (quesRespMap != null && quesRespMap.get(question.Question_Label__c) != null) ? quesRespMap.get(question.Question_Label__c).Process_Question_Response__c  :Label.HUMPreXQuestionResponseNone;
            }  
        }  
        if(question.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_PROVIDERWHOISSUBMITTING )
        {
            If(casObj != null && casObj.Account != null && casObj.Account.Name != null)
            {
                question.Display_text__c = casObj.Account.Name;
            }
        }
        if(bStopAutopopulateYesNo && question.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_PROVIDERVERBALCOMPLAINT )
        {
            wrapQues.sSelectedDropDownOption = 'Yes';
        }
        // Only Independent Questions are rendered onto page. Dependent Questions are by default not rendered onto page. Code below:
        if(question.Parent_Question__c == NULL){  wrapQues.boolDisplayQuestion = true; }
        else{  wrapQues.boolDisplayQuestion = false; }
        if(quesRespMap != null)
        {    
           populateExistingQuestions(question, quesRespMap);
        }          
    }       
        
    /**
    *  Description This method Saves the QAA information and the Questions answered on QAA section.
    *  @name saveAction
    *  @param NA
    *  @return Void
    *  @throws NA
    */
    public PageReference saveAction()
    {
        try
        {                     
            bStopAutopopulateYesNo = false;
            lstResponsesValidation = validateQuestions(lstWrapperQuestions);
            List<Case_Action_Response__c> lstResponses = new List<Case_Action_Response__c>();
            if(lstResponsesValidation.size() == 0 && (bIsProviderSelected || objParentQuestion.sSelectedDropDownOption == GLOBAL_CONSTANT_HUM.VOB_PAR_NO_HUM))
            {
                if(casObj.Id != null)
                {
                    Case_Action__c activeAction = [Select Id from Case_Action__c where Name=:  sTemplateType and Active__c = true LIMIT 1];
                    Case_Action_Association__c associationRec = new Case_Action_Association__c();
                    if(quesResponseMap != null && quesResponseMap.size() > 0)
                    {
                        List<Case_Action_Response__c> casRespList = quesResponseMap.values();
						if(Case_Action_Association__c.sObjectType.getDescribe().isAccessible()) associationRec.Id = casRespList[0].Action__c;
                    }
                    else
                    {
                        if(Schema.sObjectType.Case_Action_Association__c.fields.Case__c.isCreateable()) associationRec.Case__c = casObj.Id;
                        if(Schema.sObjectType.Case_Action_Association__c.fields.Case_Action__c.isCreateable()) associationRec.Case_Action__c = activeAction.Id; 
                    }
                    list<Case_Action_Association__c> lstCaseAccAssn = new list<Case_Action_Association__c>();
                    lstCaseAccAssn.add(associationRec);
                    //Saves Responses records.                                    
                    processWrapperQuestions(lstWrapperQuestions, quesResponseMap, lstResponses, associationRec.Id);                    
                    List<Database.upsertResult> lstSvr = Database.upsert(lstCaseAccAssn,false);
                    boolean isErrorAssn =HUMExceptionHelper.processUpsertResults(lstSvr ,lstCaseAccAssn ,
                                        'HUMQAATemplateController','saveAction','Case_Action_Association__c'); 
                    for(case_Action_REsponse__c csr:lstresponses)
                    {
                        csr.Action__c=associationRec.id;
                    }                                                         
                    List<Database.upsertResult> lstSvrResponses = Database.upsert(lstResponses,false);
                    boolean isErrorResponses =HUMExceptionHelper.processUpsertResults(lstSvrResponses ,lstResponses ,
                                              'HUMQAATemplateController','saveAction','Case_Action_Response__c'); 
                    loadDisplayData(associationRec.Id); 
                    loadView = Label.HUMPreXViewTypeView; 
                    
                    bIsSuccess = true;
                    if(sTabId == null)
                    {
                        PageReference pgRef =  new ApexPages.Standardcontroller(new Case(Id=casObj.Id)).view();
                        pgRef.setRedirect(true);
                        return pgRef;
                    }
                }   
                else
                {
                    ApexPages.addMessage(new ApexPages.message(APEXPAGES.SEVERITY.ERROR,Label.HUMCOBCaseUnavailableErrorMsg));  
                    bIsSuccess = false;
                    isErrorExist = true;
                    bSizeException = false;
                }   
            }
            else
            {
                bIsSuccess = false;
                isErrorExist = true;
                bSizeException = false;
                //plce holder to dsplay error messages if no value is provided.
                String errorMsg = Label.HUMQAAMissingResponses;
                for(String msg:lstResponsesValidation)
                {
                    errorMsg = errorMsg + msg +'<br/>';
                }
                if(!bIsProviderSelected) errorMsg = errorMsg + 'Please search for and select a provider before saving' +'<br/>';
                ApexPages.addMessage(new ApexPages.message(APEXPAGES.SEVERITY.ERROR,errorMsg)); 
            }     
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'QAATemplate_C_HUM', 'saveAction');
        }
        return null;
    } 
    
    /**
    *  Description This method is used to process Wrapper Questions.
    *  @name processWrapperQuestions
    *  @param NA
    *  @return Void
    *  @throws NA
    */
    private void processWrapperQuestions(List<HUMCaseActionQUestionHelper> lstWrapperQuestions, Map<String, Case_Action_Response__c> quesResponseMap, List<Case_Action_Response__c> lstResponses, Id associationRecId)
    {
        for(HUMCaseActionQUestionHelper question: lstWrapperQuestions)
        {            
            If(question.questionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFIRSTNAME_HUM  || question.questionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERLASTNAME_HUM  
            || question.questionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERGROUPNAME_HUM )
            {
                If(!question.boolDisplayQuestion && quesResponseMap.get(question.questionRec.Question_Label__c) != null)
                {
                    question.questionRec.Display_Text__c = '';
                    lstResponses.add(addQuestionRes(question,quesResponseMap,associationRecId));
                }
            }
            if(objParentQuestion.sSelectedDropDownOption == GLOBAL_CONSTANT_HUM.VOB_PAR_YES_HUM && question.questionRec.Question_Label__c == GLOBAL_CONSTANT_HUM.CASE_PROVIDERDBA)          
            {
                lstResponses.add(addQuestionRes(question,quesResponseMap,associationRecId));
            }
            if(question.boolDisplayQuestion && String.IsNotBlank(question.questionRec.Question_Label__c))
            {
                lstResponses.add(addQuestionRes(question,quesResponseMap,associationRecId));
            }
        }
    }
    
    /**
    *  Description This method is used to create case action response object
    *  @name addQuestionRes
    *  @param HUMCaseActionQUestionHelper,Map<String, Case_Action_Response__c>, Id
    *  @return Case_Action_Response__c 
    *  @throws NA
    */
    private Case_Action_Response__c addQuestionRes(HUMCaseActionQUestionHelper question,Map<String, Case_Action_Response__c> quesResponseMap,Id associationRecId)
    {
        Case_Action_Response__c response = new Case_Action_Response__c();
        if(quesResponseMap != null && Case_Action_Response__c.sObjectType.getDescribe().isAccessible())
        {
            response.Id = quesResponseMap.get(question.questionRec.Question_Label__c) != null ? quesResponseMap.get(question.questionRec.Question_Label__c).Id : null;
        }
        if(Schema.sObjectType.Case_Action_Response__c.fields.Action__c.isCreateable() && Schema.sObjectType.Case_Action_Response__c.fields.Action__c.isUpdateable()) response.Action__c = associationRecId;
        if(Schema.sObjectType.Case_Action_Response__c.fields.Process_Question__c.isCreateable() && Schema.sObjectType.Case_Action_Response__c.fields.Process_Question__c.isUpdateable()) response.Process_Question__c = question.questionRec.Question_Label__c;
        
        string responseValue = HUMProcessesHelper.processQuestionResponse(question);
        
        if(Schema.sObjectType.Case_Action_Response__c.fields.Process_Question_Response__c.isCreateable() && Schema.sObjectType.Case_Action_Response__c.fields.Process_Question_Response__c.isUpdateable()) response.Process_Question_Response__c = responseValue;
        if(Schema.sObjectType.Case_Action_Response__c.fields.case_Question__c.isCreateable() && Schema.sObjectType.Case_Action_Response__c.fields.case_Question__c.isUpdateable()) response.case_Question__c = question.questionRec.Id;
        return response ;
    }
    
    /**
    *  Description This method is used to perform validations on Questions.
    *  @name validateQuestions
    *  @param List<HUMCaseActionQUestionHelper>
    *  @return List<String> 
    *  @throws NA
    */
    private List<String> validateQuestions(List<HUMCaseActionQUestionHelper> lstWrapQuestions)
    {        
        List<String> lstBlankResponses = new List<String>();        
        for(HUMCaseActionQUestionHelper question: lstWrapQuestions)
        {
            If(!setProviderList.Contains(question.questionRec.Question_Label__c))
            {
                if(question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType) 
                {
                    question.questionRec.Display_Date__c = Null;
                    If(question.questionRec.Display_Text__c != null) question.questionRec.Display_Date__c = HUMProcessesHelper.convertStringtoDate(question.questionRec.Display_Text__c); 
                }
                if(question.boolDisplayQuestion) 
                {   
                    if(question.questionRec.Question_Type__c == Label.HUMCOBDropDownType && question.sSelectedDropDownOption == Label.HUMCOBNoneSelectOption) lstBlankResponses.add(question.questionRec.Question_Label__c);
                    if(question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType && question.questionRec.Display_Date__c == NULL) 
                    {
                        lstBlankResponses.add(question.questionRec.Question_Label__c);
                    }
                    if(question.questionRec.Question_Type__c == Label.HUMCOBTextType && question.questionRec.Display_text__c == NULL) lstBlankResponses.add(question.questionRec.Question_Label__c);                            
                    if(question.questionRec.Question_Type__c == Label.HUMCOBTextAreaType && string.isEmpty(question.questionRec.Display_text_area__c)) lstBlankResponses.add(question.questionRec.Question_Label__c); 
                    if(question.questionRec.Question_Label__c == Label.HUMQAAComplaintDetailsLabel && !string.isEmpty(question.questionRec.Display_text_area__c) && question.questionRec.Display_Text_Area__c.length() > 32000) lstBlankResponses.add(Label.HUMQAAComplaintLimitError); 

                    if((question.questionRec.Question_Label__c == Label.HUMQAADateofIncidence) && question.questionRec.Display_Date__c > System.today())
                    {
                        lstBlankResponses.add(Label.HUMQAAInvalidFutureDate);
                    }
                    if((question.questionRec.Question_Label__c == Label.HUMQAADateofIncidence) && question.questionRec.Display_Date__c == null)
                    {
                        lstBlankResponses.add(Label.HUMActionInvalidText+question.questionRec.Question_Label__c+Label.HUMActionDateFormat);
                    }                          
                }
            }
        }
        return lstBlankResponses;
    }    
    
    /**
    *  Description This method is used to populate the existing QAA information saved on a Case.
    *  @name populateExistingQuestions
    *  @param Question__c ,Map<String, Case_Action_Response__c>
    *  @return Void
    *  @throws NA
    */
    private void populateExistingQuestions(Question__c question, Map<String, Case_Action_Response__c> qRespMap)
    {
        if(qRespMap.get(question.Question_Label__c) != null)
        {
            if( question.Question_Type__c == Label.HUMCOBCheckboxType) question.Display_Checkbox__c = qRespMap.get(question.Question_Label__c).Process_Question_Response__c == 'true'? true : false;
            if( question.Question_Type__c == Label.HUMCOBDateQuestionType) question.Display_text__c = qRespMap.get(question.Question_Label__c).Process_Question_Response__c;
            if( question.Question_Type__c == Label.HUMCOBTextAreaType) question.Display_text_area__c = qRespMap.get(question.Question_Label__c).Process_Question_Response__c;
            if( question.Question_Type__c == Label.HUMCOBTextType) question.Display_text__c = qRespMap.get(question.Question_Label__c).Process_Question_Response__c;
            if( question.Question_Type__c == Label.HUMCOBOutputTextType) question.Display_text__c = qRespMap.get(question.Question_Label__c).Process_Question_Response__c;
        }
    }

    /**
    *  Description Loads the Case Action data to be displayed on the Action page, If any data exists.
    *  @name loadDisplayData
    *  @param Id 
    *  @return Void
    *  @throws NA
    */
    public void loadDisplayData(Id caseAssociationId)
    {
        dispCaseResponses = new List<Case_Action_Response__c>();
        for(Case_Action_Response__c  casAct : [Select Process_Question__c,  Process_Question_Response__c, Name, Id, Case_Question__c, Action__c, Case_Question__r.Display_Text_Area__c, Case_Question__r.Help_Text__c,  
                                               Case_Question__r.Question_Type__c, Case_Question__r.Question_Label__c, Case_Question__r.Display_Text__c, Case_Question__r.Display_Date__c, Case_Question__r.Sequence_Number__c 
                                               From Case_Action_Response__c where Action__c =: caseAssociationId AND Action__r.Case_Action__r.Name =:sTemplateType ORDER BY Case_Question__r.Sequence_Number__c ASC NULLS LAST])
        {
            dispCaseResponses.add(casAct);  
            quesResponseMap.put(casAct.Process_Question__c, casAct);
        }
        prepareOnScreenQuestionsDisplay(quesResponseMap);
    }

    /**
    *  Description Edits the Case Action data to be displayed on the Action page, If any data exists.
    *  @name editAction
    *  @param NA
    *  @return PageReference 
    *  @throws NA
    */
    public PageReference editAction()
    {
        try
        {
            bStopAutopopulateYesNo = false;
            String sWrittenComplaintAns = Apexpages.currentPage().getParameters().get('sWrittenComplaintVal');
            prepareOnScreenQuestionsDisplay(quesResponseMap);
            loadView = Label.HUMPreXViewTypeEdit;
            If(sWrittenComplaintAns  == 'Yes') bIsProviderSelected = true;
            return null;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'QAATemplate_C_HUM', 'editAction');
            return null;
        }
    }

    /**
    *  Description Redirects the user back to the Case Page where the navigation to this page happened.
    *  @name cancel
    *  @param NA
    *  @return PageReference 
    *  @throws NA
    */
    public PageReference cancel()
    {
        if(sTabId == null && caseId != null && caseId instanceof Id)
        {
            PageReference pgRef = new PageReference('/'+caseId);
            pgRef.setRedirect(true);
            return pgRef;
        }
        return null;
    } 
   
    /**
    *  This method is used to set Provider First Name,Provider Last Name and Provider Facility/Group Name 
        fields visibility based on the answer selected for "Is this for a Provider or a Facility/Group?" question.
    *  @name setProviderFieldsVisibility
    *  @param NA
    *  @return NA
    *  @throws NA
    */
    @TestVisible Private void setProviderFieldsVisibility()
    {
        Map<String,String> mapCurrentResponses = new map<String,String>();
        String sCurrentParentValue = '';
        HUMCaseActionQUestionHelper objParent = mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERFACILITYQUESTION);
        
        if (objParent != null) {
            If(objParent.boolDisplayQuestion && objParent.sSelectedDropDownOption  == GLOBAL_CONSTANT_HUM.PROVIDER_TYPE)
            {
                mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFIRSTNAME_HUM).boolDisplayQuestion = true;
                mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERLASTNAME_HUM).boolDisplayQuestion = true;
                mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERGROUPNAME_HUM).boolDisplayQuestion = false;
            }
            else if(objParent.boolDisplayQuestion && objParent.sSelectedDropDownOption  == GLOBAL_CONSTANT_HUM.CASE_FACILITYGROUPTYPE)
            {
                mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFIRSTNAME_HUM).boolDisplayQuestion = false;
                mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERLASTNAME_HUM).boolDisplayQuestion = false;
                mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERGROUPNAME_HUM).boolDisplayQuestion = true;
            }
        }        
    }
    
    /**
    *  This method is used to call EPSS service with the given search criteria
    *  @name searchProvider
    *  @param NA
    *  @return Continuation 
    *  @throws NA
    */
    public Continuation searchProvider()
    {
        isErrorExist = false;
        isProviderDetailsPresent = false;
        Continuation oCon = new Continuation(GLOBAL_CONSTANT_HUM.VOB_ServiceTimeOut_HUM);
        try 
        {
            HUMProviderSearchWrapperHelper oSearchWrapper = QAAProviderSearch_H_HUM.prepareProviderSearchWrapper(mapProviderQuestions);
            System.debug('oSearchWrapper -------'+oSearchWrapper );
            if(QAAProviderSearch_H_HUM.validateProviderSearchRules(oSearchWrapper))
            {
                ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
                oCon.continuationMethod = 'processResponse';
                mapRequestDto = QAAProviderSearch_H_HUM.getserviceInputMap(oSearchWrapper);
                sEPSSReq = oCon.addHttpRequest(oProvSearch.getEPSSReq(mapRequestDto));
            }
            else
            {
                oCon = null;
                isErrorExist = true;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'QAATemplate_C_HUM', 'searchProvider');
        }
        return oCon ;
    } 
    
    /**
    *  This method is used to parse EPSS service response.
    *  @name processResponse
    *  @param NA
    *  @return object 
    *  @throws NA
    */
    public object processResponse()
    {
        bSizeException = false;
        Try
        {
            HttpResponse response = Continuation.getResponse(sEPSSReq);
            if(response != null && response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM)
            {               
                parseServiceResponse(response);
            }
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'QAATemplate_C_HUM', 'processResponse');
        }
        return null;
    }
    
    /**
    *  Description This method is used to parse service response.
    *  @name parseServiceResponse
    *  @param HttpResponse 
    *  @return Void
    *  @throws NA
    */
    @testVisible Private Void parseServiceResponse(HttpResponse response)
    {
        ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM();
        List<HUMCustomSearchHelper> lsttempProviderAccounts = oProvSearch.parseServiceResponse(response,false);
        lstProviderAccounts = new List<HUMCustomSearchHelper>();
        lstProviderAccounts.AddAll(lsttempProviderAccounts);
        
        if(lstProviderAccounts != null && lstProviderAccounts.size()>0)
        {
            for(integer i=0; i<lstProviderAccounts.size(); i++)
            {
               if(lstProviderAccounts[i].sServiceAddress != null && lstProviderAccounts[i].sServiceZip != null && lstProviderAccounts[i].sServiceState != null && lstProviderAccounts[i].sServiceCity != null)
               {
                   lstProviderAccounts[i].sAddress =  lstProviderAccounts[i].sServiceAddress;
                   lstProviderAccounts[i].sPostalCode =  lstProviderAccounts[i].sServiceZip;
                   lstProviderAccounts[i].sCity =  lstProviderAccounts[i].sServiceCity;
                   lstProviderAccounts[i].sState =  lstProviderAccounts[i].sServiceState;
               }
            }
        }
		
        isProviderDetailsPresent = true;

        If(lstProviderAccounts != null && lstProviderAccounts.size()>100)
        {
            bSizeException = true;
            lstProviderAccounts = new List<HUMCustomSearchHelper>();
            isErrorExist = false;
            throw new HUMCustomException(Label.CASE_PROVIDER_LIMIT_ERROR);
        }
    }
    
    /**
    *  This method is used to populate selected provider row details in provider fields.
    *  @name populateProviderDetails
    *  @param NA
    *  @return NA
    *  @throws NA
    */
    public void populateProviderDetails()
    {
        String sProvDetails = Apexpages.currentPage().getParameters().get('providerData');

        List<String> lstProvData = sProvDetails.split('####');
        mapProviderQuestions = QAAProviderSearch_H_HUM.populateProviderDetails(mapProviderQuestions,lstProvData);
        bSizeException = false;
        bIsProviderSelected = true;
        Try
        {
            if(String.isBlank(mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERNPI_HUM).questionRec.Display_text__c) && String.isBlank(mapProviderQuestions.get(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFACILITYTAXID_HUM).questionRec.Display_text__c))
            {
                throw new HUMCustomException(Label.CASE_PROVIDER_TAXIDNPI_ERROR);
            }
        }
        Catch(Exception ex)
        {
            bSizeException = true;
            isProviderDetailsPresent = true;
            isErrorExist = false;
            HUMExceptionHelper.logErrors(ex, 'QAATemplate_C_HUM', 'populateProviderDetails');
        }
    }
     
     /**
    *  This method is used to clear details in provider fields.
    *  @name clearProviderDetails
    *  @param NA
    *  @return NA
    *  @throws NA
    */
     public void clearProviderDetails()
    {
        mapProviderQuestions= QAAProviderSearch_H_HUM.clearProviderDetails(mapProviderQuestions);
        bIsProviderSelected = false;
        isProviderDetailsPresent = false;
        lstProviderAccounts = new List<HUMCustomSearchHelper>();
    }
    
    /**
    *  This method is used to prepare a set with all provider related field labels.
    *  @name prepareProviderSet
    *  @param NA
    *  @return NA
    *  @throws NA
    */
    private void prepareProviderSet()
    {        
        setProviderList  = QAAProviderSearch_H_HUM.getProviderQuestions();
        setDropdownQuestionsList = new Set<String>{GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERFACILITY ,GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_QMSPROVIDERSTATE_HUM ,GLOBAL_CONSTANT_HUM.CASE_QAATEMPLATE_PROVIDERACCESSIBILITYQUESTION };
    }
    
    /**
    *  This method is used to add custom class name for all questions.
    *  @name addClassName
    *  @param HUMCaseActionQUestionHelper ,Question__c 
    *  @return HUMCaseActionQUestionHelper 
    *  @throws NA
    */
    Public HUMCaseActionQUestionHelper addClassName(HUMCaseActionQUestionHelper wrapQues,Question__c ques)
    {
        wrapQues.sClassName = GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_QAACLASS ;
        if(setProviderList.contains(ques.Question_Label__c)) 
        {
            wrapQues.sClassName = 'Provider_' + (ques.Question_Label__c.replaceAll( '\\s+', '')).replaceAll( '/', '');
            mapProviderQuestions.put(ques.Question_Label__c,wrapQues);
        }
        If(setDropdownQuestionsList.contains(ques.Question_Label__c))
        {
            HUMCaseActionQUestionHelper providerDropdownVal = new HUMCaseActionQUestionHelper(); 
            providerDropdownVal.sClassName = 'Provider_DD_' + (ques.Question_Label__c.replaceAll( '\\s+', '')).replaceAll( '/', '');
            providerDropdownVal.questionRec.Question_Label__c = ques.Question_Label__c;
            providerDropdownVal.questionRec.Display_text__c  = wrapQues.sSelectedDropDownOption;
            lstDropDownWrapperQuestions.Add(providerDropdownVal);
        }
        return wrapQues;
    }
    
    /**
    *  This method is used to create a dummy question to display "Clear" and "Search" buttons under provider zip code field.
    *  @name createQuestionForCommandButtons
    *  @param Question__c 
    *  @return NA
    *  @throws NA
    */
    Private Void  createQuestionForCommandButtons(Question__c ques)
    {
        HUMCaseActionQUestionHelper wrapQuesCmd = new HUMCaseActionQUestionHelper();
        wrapQuesCmd.questionRec.Question_Label__c = '';
        wrapQuesCmd.questionRec.Parent_Question__c = ques.Parent_Question__c;
        wrapQuesCmd.questionRec.Dependent_Value__c = ques.Dependent_Value__c;
        wrapQuesCmd.boolDisplayQuestion = false;
        lstWrapperQuestions.add(wrapQuesCmd );
    }
    
    /**
    *  This method is used to read decrypted dropdown question values in view mode.
    *  @name getProviderDetailsFromPage
    *  @param NA
    *  @return NA
    *  @throws NA
    */
    Public Void getProviderDetailsFromPage()
    {
        lstWrapperQuestions = QAAProviderSearch_H_HUM.getProviderDetailsFromPage(lstWrapperQuestions,mapProviderQuestions);
    } 
 }