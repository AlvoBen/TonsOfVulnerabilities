/******************************************************************************************************************************
Apex Class Name  : HUMCOBProcessController
Version          : 1.0
Created Date     : JUL 02 2014
Function         : Controller to handle display of COB Questions and COB Tabular Questions.
                   Addind/Removing OI Data is presented on the page.
                   Saves COB Data along with table data.
Test Class       : HUMCOBProcessControllerTest
Modification Log :
*   Modification ID     Developer           Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*           1.0         Krishna Tadikonda     17352            07/02/2014           Original Version
*           2.0         Krishna Tadikonda     17353            07/17/2014           Added Dynamic Questions Display logic.
*           3.0         Krishna Tadikonda     17542            07/28/2014           Added OI tables on the page.
*           4.0         Vivek Batham          17588            08/01/2014           Added View logic for COB Data.
*           5.0         Vivek Batham          17929            09/17/2014           Validation added to make sure future date not entered for DOB
*           6.0         Krishna Tadikonda     18313            09/25/2014           Defect fixes for error messages.
*           7.0         Palak Papneja         18313            10/06/2014           Shortened the code as per the review
*           8.0         Krishna Tadikonda     18878            10/30/2014           Implemented CR-51 to show the subscriber Policy Members, if dependent is selected.
*           9.0         Asha Deepika Koyyana  19409            11/14/2014           To implement Req-70318, added displayOtherCarrier method.
*          10.0         Asha Deepika Koyyana  19843            12/10/2014           Fixed defect #16431
*          11.0         Asha Deepika Koyyana  19843            12/15/2014           Fixec CC #19843 review comments
*          12.0         Pradeep Veeravali     20976            02/15/2015           added extra column to Tabular Answer Response to save relationshipCode coming from Person Service
*          13.0         Santhi Mandava                         05/19/2015           Changed casObj type from private to public.
*          14.0         Rakesh Patni          23763            06/03/2015           Fixed defect #185301. Removed duplicate line at line #916
*          15.0         Praveen Kumar Parimi                   06/17/2015           Added call to HUMEncryptObjectFieldsUtility in insertCaseActionResponses method to get the Case_Action_Response__c Encrypted.
*          16.0         Manish Kumar          24291            06/25/2015           Fixed the code for defecft#188338.0001. Inserting Case_Action_Association record for vefified check box.
                                                                                    Line#797 - 803
*          17.0         Vishal Verma          24506            06/30/2015           CA Ticket - 5105763 - Added Two Tabular Question and two Question to capture responses. (Line NUmber - 477 to 494 And 843 to 864 )
                                                                                    These questions will not be shown on the UI.
*          18.0         Praveen Kumar Parimi  24582            07/07/2015           Moved insert associationRec outside the loop.
*          19.0         Saurabh Kaple         162071           01/10/2018           REQ - 333069 - Genkey conversion- updates to person service request                                                                                                                                                 
*          20.0         Santhi Mandava        214342           04/11/2018           REQ - 359924 Removed encryption logic.
*		   20.1			Santhi Mandava						   01/24/2019			Removed all dpaas calls
*          20.2         Mohammed Noor						   08/03/2020           US825176 PR00093155 - MF 18 - Changes to skip new Tabular Question entries from this COB Template page with Person Service.  
* 		   20.3		    Jasmeen Shangari                       04/21/2021           Checkmarx fixes Azure ID - 2163309
*********************************************************************************************************************************/

public with sharing class HUMCOBProcessController
{
    
    private String sTemplateType = '';
    private list < Tabular_Question__c > lstTabularQuestions = new list< Tabular_Question__c >();
    private list < String > lstColumnTitles = new list<string>();
    private list < HUMCaseTemplateHelper > lstTabularResponses = new list< HUMCaseTemplateHelper >();
    private list < Policy_Member__c > lstPolicyMembersForCOB = new list< Policy_Member__c >();
    private map < String, List < String >> mapPreferences = new map< String,list< string >>();
    private list < HUMTemplateCOBColumns__c > lstPreferences = new list< HUMTemplateCOBCOlumns__c >();
    private list < Question__c > lstQuestions = new List < Question__c > ();
    private map<string,string> mapClearVsCipher = new map<String,string>();
    private static boolean boolCipherCall = false;
    private boolean boolOtherCarrier= false;
    
    private Map<String,String> mapDependentsFirstName = new Map<String,String>();
    private Map<String,String> mapDependentsLastName = new Map<String,String>();
    /*
    * Method Name : HUMCOBProcessController
    * Description : Initializes variables and gathers data into query variables.
    * Return type : Void
    * Paramater   : NA
    */
    public HUMCOBProcessController()
    {
        try
        {
            initializeProperties();
            initLoadEdit();
        }
        catch(Exception e)
        {
            //As DML is not allowed inside constructor,stop the DML operation by this boolean
            HUMExceptionHelper.bRestrictLog = true;
            //store the list of exceptions captured
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMCOBProcessController','HUMCOBProcessController');
        }
    }
    
    public Case casObj{get;set;}
    public String sCaseId {get;set;}
    public Integer iRowNumAddOI {get;set;}
    public Integer iRowNumRemoveOI {get;set;}
    public String sColumnName {get;set;}
    public string sLoadView {get;set;}
    public Boolean boolOIResponse {get;set;}
    public map < String, HUMPersonServiceResponseHelper > mapResponseWrapper {get;set;}
    public list < HUMCaseActionQuestionHelper > lstWrapperQuestions {get;set;}
    public map < String, Map < String, List < String >>> mapMasterConditions {get;set;}
    public map < String, String > mapMemberInfo {get;set;}
    public list < Case_Action_Response__c > dispCaseResponses {get;set;}
    public map < Integer, Boolean > mapAddOIRowDisplayBool {get;set;}
    public map < Integer, Boolean > mapRemoveOIRowDisplayBool {get;set;}
    public map < Integer, List < HUMCaseTemplateHelper >> mapSequenceResponses {get;set;}
    public list < Integer > lstRowHolders {get;set;}
    public boolean boolRenderAddOI {get;set;}
    public boolean boolRenderRemoveOI {get;set;}
    public boolean boolRenderMainTable {get;set;}
    public Boolean boolDisplayQuestions {get;set;}
    public boolean boolIsServiceRequired {get;set;}
    public map < Integer, List < Tabular_Answer_Response__c >> rowdispTableMap {get;set;}
    public list < Integer > columnrows {get;set;}
    public String sTabId {get; set;}
    public Boolean bIsSuccess {get; set;}
    public List<Error_Log__c> lstLogErrors
    {
        get
        {
            if(lstLogErrors==null){return new List<Error_Log__c>();}
            else{return lstLogErrors;}
        }
        set;
    }
    
    /*
    * Method Name : initLoadEdit
    * Description : assigning variables based on page paramenters and generating basic queries needed.
    * Return type : Void
    * Paramater   : NA
    */
    public void initLoadEdit()
    {
        if (Apexpages.currentPage().getParameters().containsKey(Label.HUMCOBProcessType) && Apexpages.currentPage().getParameters().get(Label.HUMCOBProcessType) != null)
        {
            sTemplateType = Apexpages.currentPage().getParameters().get(Label.HUMCOBProcessType);
            boolDisplayQuestions = false;
            sCaseId = (Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam) != null) ? Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam) : null;
            sTabId = Apexpages.currentPage().getParameters().containsKey(Label.HUMTabId) != null ?  Apexpages.currentPage().getParameters().get(Label.HUMTabId).escapehtml4() : null;
            casObj = [Select Id, CaseNumber, Policy_Member__r.Member__r.FirstName , Policy_Member__r.Member__r.LastName , Policy_Member__c, AccountId, Account.Enterprise_ID__c, Account.Mbr_Gen_Key__c, Policy_Member__r.Member__c, Policy_Member__r.Member__r.Enterprise_Id__c,Policy_Member__r.Relationship__c,Policy_Member__r.Subscriber__c,
            (Select Id, Case_Action__c, Action__c From Case_Action_Associations__r) from Case where Id = : sCaseId];
            if (casObj.Case_Action_Associations__r.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
            {
                sLoadView = Label.HUMCOBView;
                boolean boolIsDataReturned = loadDisplayData(casObj.Case_Action_Associations__r[Integer.valueof(system.label.HUMConstantZero)].Id);
                if(!boolIsDataReturned){  ApexPages.addMessage(new Apexpages.Message(APEXPAGES.SEVERITY.INFO,Label.HUMCOBIncorrectCaseAction)); }
                ApexPages.addMessage(new Apexpages.Message(APEXPAGES.SEVERITY.INFO,Label.HUMCOBEditLabel));
            }
            else
            {
                loadEditView();
            }
        }
    }
    
    /*
    * Method Name : loadEditView
    * Description : Loads the Case Action Guide in Edit Mode for the user to enter values.
    * Return type : Void
    * Paramater   : NA
    */
    public void loadEditView()
    {
        sLoadView = Label.HUMCOBEdit;
        //Retrieving table preferences in CustomSettings.
        HUMTemplateCOBColumns__c addOIColumnPreferences = HUMTemplateCOBColumns__c.getValues(Label.HUMCOBAddOI);
        HUMTemplateCOBColumns__c removeOIColumnPreferences = HUMTemplateCOBColumns__c.getValues(Label.HUMCOBRemoveOI);
        HUMTemplateCOBColumns__c cobTableColumnPreferences = HUMTemplateCOBColumns__c.getValues(Label.HUMCOBTableName);
        lstPreferences.add(addOIColumnPreferences);
        lstPreferences.add(removeOIColumnPreferences);
        lstPreferences.add(cobTableColumnPreferences);
        mapPreferences = new Map < String, List < String >> ();
        for (HUMTemplateCOBColumns__c pref: lstPreferences)
        {
            List < String > lstPrefColumnNames = new List < String > ();
            for (String splitPref: pref.Column_Titles__c.split(','))
            {
                lstPrefColumnNames.add(splitPref);
            }
            mapPreferences.put(pref.Name, lstPrefColumnNames);
        }
        if (ApexPages.currentPage().getParameters().containsKey(Label.HUMCOBProcessType))
        {
            boolDisplayQuestions = !(sTemplatetype == Label.HUMCOBTemplateLabel);
            boolRenderMainTable = (sTemplatetype == Label.HUMCOBTemplateLabel);
        }
    }
    
    /*
    * Method Name : addRowonDisplayTable
    * Description : This method accepts the stored action responses and places in a map in an order.
    * Return type : list<Integer>
    * Parameter   : list<Tabular_Answer_Response__c>
    */
    public list<Integer> addRowonDisplayTable(list<Tabular_Answer_Response__c> lstActionResponses)
    {
        List < Tabular_Answer_Response__c > lstTempTable = new List < Tabular_Answer_Response__c > ();
        list < Integer > lstColumns = new list < Integer >();
        for (Tabular_Answer_Response__c tabResp: lstActionResponses)
        {
            if (rowdispTableMap.get(Integer.valueOf(tabResp.Row_Number__c)) != null)
            {
                lstTempTable = rowdispTableMap.get(Integer.valueOf(tabResp.Row_Number__c));
                lstTempTable.add(tabResp);
                rowdispTableMap.put(Integer.valueOf(tabResp.Row_Number__c), lstTempTable);
            }
            else
            {
                lstTempTable = new List < Tabular_Answer_Response__c > ();
                lstTempTable.add(tabResp);
                rowdispTableMap.put(Integer.valueOf(tabResp.Row_Number__c), lstTempTable);
            }
        }
        if(rowdispTableMap.keySet().size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
        {
            lstColumns.addAll(rowdispTableMap.keySet());
        }
        if(lstColumns.size()>HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
        {
            for(Integer i : lstColumns)
            {
                if (rowdispTableMap.keyset().contains(i) && getMemRelColIndex(rowdispTableMap.get(i))<rowdispTableMap.get(i).size())
                {
                    rowdispTableMap.get(i).remove(getMemRelColIndex(rowdispTableMap.get(i)));
                }
            }
        }
        return lstColumns;
    }
    
    /*
    * Method Name : getMemRelColIndex
    * Description : This method is used to remove the MemberRelationship label from map.
    * Return type : Integer
    * Parameter   : list<Tabular_Answer_Response__c>
    */
    private static Integer getMemRelColIndex(List<Tabular_Answer_Response__c>  lsttab)
    {
        Integer index = Integer.valueof(system.label.HUMConstantZero);
        for(Tabular_Answer_Response__c tabres : lsttab )
        {
            if(tabres.Column_Title__c ==Label.HUMCOBMemberRelationshipLabel)
            {
                break;
            }
            else
            {
                index++;
            }
        }
        return index;
    }
    
    /*
    * Method Name : loadDisplayData
    * Description : Loads the Case Action data to be displayed on the Action page, If any data exists.
    * Return type : Void
    * Paramater   : Id
    */
    public boolean loadDisplayData(Id caseAssociationId)
    {
        rowdispTableMap = new Map < Integer, List < Tabular_Answer_Response__c >> ();
        for (Case_Action_Response__c casAct: HUMProcessesHelper.getCaseActionResponses(caseAssociationId, sTemplateType))
        {
            if(casAct.Process_Question__c != Label.OIPolicySubsFName && casAct.Process_Question__c != Label.OIPolicySubsLName) dispCaseResponses.add(casAct);
        }
        list<Tabular_Answer_Response__c> lstActionResponses = new List<Tabular_Answer_Response__c>();
        for(Tabular_Answer_Response__c tabAns : HUMProcessesHelper.getTabularAnswerResponses(caseAssociationId))
        {
            if(!Label.HUMCOBExistingOIInfo.equalsIgnoreCase(tabAns.Grid_Question__r.Question__r.Question_Label__c) && tabAns.Column_Title__c != Label.MemberFirstName && tabAns.Column_Title__c != Label.MemberLastName) lstActionResponses.add(tabAns);
        }
        if(lstActionResponses.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
        {
            list<Integer> lstColRowsFromResult = addRowonDisplayTable(lstActionResponses);
            if(lstColRowsFromResult.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c){ columnrows.addAll(lstColRowsFromResult); }
        }
        return (lstActionResponses.size()>HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c);
    }
    
    /*
    * Method Name : setResponseValue
    * Description : This method retrieves the data from the response and assigns it to the rows of table.
    * Return type : Tabular_Answer_Response__c
    * Paramater   : string, HUMPersonServiceResponseHelper
    */
    private Tabular_Answer_Response__c setResponseValue(string sColumnTitle, HUMPersonServiceResponseHelper responseRowData, Tabular_Answer_Response__c responseRec)
    {
        string response = '';
        if (sColumnTitle.contains(Label.HUMCOBMemberLabel)) response = responseRowData.Member;
        if (sColumnTitle.contains(Label.HUMCOBRelationshipLabel)){ response = responseRowData.Relationship;  }
        //Assign response value for memberRelationship
        if (sColumnTitle.contains(Label.HUMCOBMemberRelationshipLabel)){ response = responseRowData.memberRelationship; }
        if (sColumnTitle.contains(Label.HUMCOBOICarrierName)){ response = responseRowData.OICarriername;  }
        if (sColumnTitle.contains(Label.HUMCOBOIEffectiveDate) && !string.isBlank(responseRowData.OIEffectiveDate)){
            response = responseRowData.OIEffectiveDate;
        }
        if (sColumnTitle.contains(Label.HUMCOBOIEndDate) && !string.isBlank(responseRowData.OIEndDate)){
            response = responseRowData.OIEndDate;
        }
        if (sColumnTitle.contains(Label.HUMCOBOIMedicalVerifiedDate) && !string.isBlank(responseRowData.OIMedicalLastVerifieddate)){
            response = responseRowData.OIMedicalLastVerifieddate;
        }
        if (sColumnTitle.contains(Label.HUMCOBOIDentalVerifiedDate) && !string.isBlank(responseRowData.OIDentalLastVerifieddate)){
            response = responseRowData.OIDentalLastVerifieddate;
        }
        if (sColumnTitle.contains(Label.HUMCOBOIPrimary))
        {  response = responseRowData.OIPrimary;
            responseRec.boolean_response__c = responseRowData.OIPrimary == 'true';
        }
        responseRec.Response__c = response;
        return responseRec;
    }
    
    /*
    * Method Name : loadGuide
    * Description : This method retrieves the questions for the respective guide selected and loads the questions onto screen.
    * Return type : Void
    * Parameter   : NA
    */
    private void loadGuide()
    {
        lstQuestions = [Select Id, Name, Question_Label__c, Parent_Question__c, Parent_Question__r.Question_Label__c, Question_Type__c, Question_Values__c, Dependent_Value__c,
        Display_Text__c, Display_Text_Area__c, Display_Checkbox__c, Display_Date__c
        from Question__c where RecordType.Name = : sTemplateType AND Active__c = true ORDER BY Sequence_Number__c ASC NULLS LAST
        ];
        //Loads the table structure to be displayed on page.
        list<Tabular_Question__c> tempTabularQuestions = [select Id, Name, Column_Title__c, Question_Type__c, Question_Values__c, Question__r.Question_Label__c from Tabular_Question__c ORDER BY Sequence_Number__c ASC NULLS LAST];
        for(Tabular_Question__c tQues : tempTabularQuestions)
        {
            if(!Label.HUMCOBExistingOIInfo.equalsIgnoreCase(tQues.Question__r.Question_Label__c)) lstTabularQuestions.add(tQues);
        }
        mapSequenceResponses = loadGenericTemplateTable(lstTabularQuestions);
        if (lstQuestions.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)   prepareOnScreenQuestionsDisplay(lstQuestions);
    }
    
    /*
    * Method Name : prepareOnScreenQuestionsDisplay
    * Description : This method restructures the AddOI,RemoveOI tables for COB based on the checkbox selected for AddOI,RemoveOI.
    * Return type : Void
    * Paramater   : List < Question__c >
    */
    public void prepareOnScreenQuestionsDisplay(List < Question__c > lstQuestions)
    {
        //Maintains Questions Sequence-wise.
        Set < Integer > setSequences = new Set < Integer > ();
        Map < Integer, List < Question__c >> mapOrderedQuestions = new Map < Integer, List < Question__c >> ();
        //To store the parent responses needed to render the child.
        mapMasterConditions = new Map < String, Map < String, List < String >>> ();
        for (Question__c ques: lstQuestions)
        {
            HUMCaseActionQuestionHelper wrapQues = new HUMCaseActionQuestionHelper();
            wrapQues.questionRec = ques;
            if (ques.Parent_Question__c != NULL && ques.Parent_Question__r.Question_Label__c != NULL && ques.Dependent_Value__c != NULL)
            {
                getMasterConditions(ques);
            }
            if (ques.Question_Values__c != NULL && ques.Question_Label__c != Label.HUMCOBOtherInsSubscriberName)
            {
                wrapQues.lstDropDownOptions.add(new SelectOption(Label.HUMCOBNoneSelectOption,Label.HUMCOBNoneSelectOption));
                for (String DropDownValue: ques.Question_Values__c.split(';'))
                {
                    wrapQues.lstDropDownOptions.add(new SelectOption(DropDownValue, DropDownValue));
                }
                wrapQues.sSelectedDropDownOption = Label.HUMCOBNoneSelectOption;
            }
            if (ques.Question_Label__c == Label.HUMCOBOtherInsSubscriberName && mapMemberInfo.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
            {
                getSubscriberNames(wrapQues);
            }
            // Only Independent Questions are rendered onto page. Dependent Questions are by default not rendered onto page. Code below:
            if (ques.Parent_Question__c == NULL)
            {
                wrapQues.boolDisplayQuestion = true;
            }
            else
            {
                wrapQues.boolDisplayQuestion = false;
            }
            //
            if(ques.Question_Label__c == Label.OIPolicySubsFName || ques.Question_Label__c == Label.OIPolicySubsLName )
            {
                wrapQues.boolDisplayQuestion = false;
            }
            lstWrapperQuestions.add(wrapQues);
        }
        reCalculatePageQuestions();
    }
    
    /*
    * Method Name : getMasterConditions
    * Description : This method gets all the conditions based on the master field responses.
    * Return type : boolean
    * Paramater   : Question
    */
    private boolean getMasterConditions(Question__c ques)
    {
        List < String > sDependentValues = new List < String > ();
        sDependentValues = ques.Dependent_Value__c.split(';');
        Map < String, List < String >> mapInnerValue = new Map < String, List < String >> ();
        mapInnerValue.put(ques.Parent_Question__r.Question_Label__c, sDependentValues);
        mapMasterConditions.put(ques.Question_Label__c, mapInnerValue);
        return true;
    }
    
    /*
    * Method Name : getSubscriberNames
    * Description : This method adds the valid subscriber names to the Other Insurance Subscriber dropdown
    * Return type : void
    * Paramater   : HUMCaseActionQuestionHelper
    */
    public void getSubscriberNames(HUMCaseActionQuestionHelper wrapQues)
    {
        wrapQues.lstDropDownOptions.add(new SelectOption(Label.HUMCOBNoneSelectOption,Label.HUMCOBNoneSelectOption));
        wrapQues.sSelectedDropDownOption = Label.HUMCOBNoneSelectOption;
        list<string> sMemNames = new list<string>();
        for (String memberKey: mapMemberInfo.keyset())
        {
            wrapQues.lstDropDownOptions.add(new SelectOption(memberKey, memberKey));
        }
    }
    
    /*
    * Method Name : loadGenericTemplateTable
    * Description : This method retrieves the column titles and construct an empty table to display onto page.
    * Return type : Void
    * Parameter   : NA
    */
    private map<integer, list< HUMCaseTemplateHelper >> loadGenericTemplateTable(list< tabular_Question__c > lstTabularQuestions)
    {
        map<integer, list< HUMCaseTemplateHelper >> mapReturnWrapper = new map<integer, list < HUMCaseTemplateHelper >>();
        
        lstColumnTitles = new List < String > ();
        Integer iRepetitions = Integer.valueof(system.label.HUMConstantZero);
        if (sTemplateType == Label.HUMCOBTemplateLabel && lstPolicyMembersForCOB.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
        {
            // repetitions = size + 1
            iRepetitions = lstPolicyMembersForCOB.size();
            iRepetitions++;
        }
        integer iRowNum = Integer.valueof(system.label.HUMConstantZero);
        iRowNum++;
        // Loading Tabular response row.
        for (String key: mapResponseWrapper.keyset())
        {
            
            addTabularResponses(lstTabularQuestions,key,iRowNum);
            lstRowHolders.add(iRowNum);
            mapReturnWrapper.put(iRowNum, lstTabularResponses);
            //Initially designating that all the rows should not be available on AddOI,RemoveOI tables.
            mapAddOIRowDisplayBool.put(iRowNum, false);
            mapRemoveOIRowDisplayBool.put(iRowNum, false);
            iRowNum++;
        }
        //If mapResponseWrapper doesn't contains any keys then below table should prepare.
        if(mapResponseWrapper.keySet().size() == Integer.valueof(system.label.HUMConstantZero)){ mapReturnWrapper = doPrepareEmptyTable(lstTabularQuestions); }
        lstRowHolders.sort();
        return mapReturnWrapper;
    }
    
    /*
    * Method Name : addTabularResponses
    * Description : Adds rows to tabular responses list by extracting the values from tabular question.
    * Return type : Void
    * Paramater   : list<Tabular_Question__c>, string, integer
    */
    private void addTabularResponses(list<Tabular_Question__c> lstTabularQuestions, string key, integer iRowNum)
    {
        lstTabularResponses = new List < HUMCaseTemplateHelper > ();
        String sMemberName = '';
        for (Tabular_Question__c tabularquestion: lstTabularQuestions)
        {
            if(tabularquestion.column_title__c != Label.MemberFirstName && tabularquestion.column_title__c != Label.MemberLastName)
            {
                Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
                setResponseValue(tabularquestion.column_title__c, mapResponseWrapper.get(key), oTempVar );
                oTempVar.Column_Title__c = tabularquestion.column_title__c;
                oTempVar.Grid_Question__c = tabularQuestion.Id;
                oTempVar.Row_Number__c = iRowNum;
                lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c));
                lstColumnTitles.add(tabularquestion.column_title__c);
                if(tabularquestion.column_title__c == 'Member') { sMemberName = oTempVar.Response__c;}
            }
        }
       
        if(mapDependentsFirstName.containskey(sMemberName) || mapDependentsLastName.containskey(sMemberName))
        {
            for (Tabular_Question__c tabularquestion: lstTabularQuestions)
            {
                if(tabularquestion.column_title__c == Label.MemberFirstName ||  tabularquestion.column_title__c == Label.MemberLastName)
                {
                    Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
                    setResponseValue(tabularquestion.column_title__c, mapResponseWrapper.get(key), oTempVar );
                    String sName = ''; 
                    if(tabularquestion.column_title__c == Label.MemberFirstName) 
                    {
                        sName = mapDependentsFirstName.get(sMemberName );
                        if(Schema.sObjectType.Tabular_Answer_Response__c.fields.Response__c.isCreateable()) oTempVar.Response__c = sName;
                    }
                    if(tabularquestion.column_title__c == Label.MemberLastName) 
                    {
                        sName = mapDependentsLastName.get(sMemberName );
                        oTempVar.Response__c = sName;
                    }
                    oTempVar.Column_Title__c = tabularquestion.column_title__c;
                    oTempVar.Grid_Question__c = tabularQuestion.Id;
                    oTempVar.Row_Number__c = iRowNum;
                    lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c));
                    lstColumnTitles.add(tabularquestion.column_title__c);
                }
            }
        }
    }
    
    /*
    * Method Name : reCalculatePageQuestions
    * Description : Checks the responses of the parent questions and reloads the valid child questions.
    * Return type : Void
    * Paramater   : NA
    */
    public void reCalculatePageQuestions()
    {
        map < String, HUMCaseActionQuestionHelper > mapCurrentResponses = new map < String, HUMCaseActionQuestionHelper > ();
        string sMemberName;
        for (HUMCaseActionQuestionHelper ques: lstWrapperQuestions)
        {
            mapCurrentResponses.put(ques.QuestionRec.Question_Label__c, ques);
            if (ques.QuestionRec.Question_Label__c == Label.HUMCOBOtherInsSubscriberName){  sMemberName = ques.sSelectedDropDownOption;     }
            assignDisplayValues(ques,sMemberName);
            //Checks if the Parent Question has the selected value to render the Question on page.
            if (ques.QuestionRec.Parent_Question__c != NULL &&
                (mapMasterConditions.containsKey(ques.QuestionRec.Question_Label__c) &&
                mapMasterConditions.get(ques.QuestionRec.Question_Label__c).containsKey(ques.QuestionRec.Parent_Question__r.Question_Label__c)))
            {
                List < String > lstMatchingResponsesFromMasterMap = mapMasterConditions.get(ques.QuestionRec.Question_Label__c).get(ques.QuestionRec.Parent_Question__r.Question_Label__c);
                Map < String, List < String >> sMatchingQuestion = mapMasterConditions.get(ques.QuestionRec.Question_Label__c);
                String sCurrentParentValue;
                sCurrentParentValue = mapCurrentResponses.get(ques.QuestionRec.Parent_Question__r.Question_Label__c).sSelectedDropDownOption;
                ques.boolDisplayQuestion = false;
                for (String value: lstMatchingResponsesFromMasterMap)
                {
                    if(mapCurrentResponses.containsKey(ques.QuestionRec.Parent_Question__r.Question_Label__c))
                    if (sCurrentParentValue == value && mapCurrentResponses.containsKey(ques.QuestionRec.Parent_Question__r.Question_Label__c) && mapCurrentResponses.get(ques.QuestionRec.Parent_Question__r.Question_Label__c).boolDisplayQuestion)
                    {
                        ques.boolDisplayQuestion = true;
                        break;
                    }
                    else
                    {
                        ques.boolDisplayQuestion = false;
                    }
                }
            }
        }
    }
    
    /*
    * Method Name : assignDisplayValues
    * Description : Assigns relationship response value based on selected Member.
    * Return type : Void
    * Paramater   : HUMCaseActionQuestionHelper, String.
    */
    private void assignDisplayValues(HUMCaseActionQuestionHelper ques, string sMemberName)
    {
        if (ques.QuestionRec.Question_Label__c == Label.HUMCOBRelationshipLabel)
        {
            ques.QuestionRec.Display_text__c = null;
            ques.sValue = null;
        }
        if (!string.isBlank(sMemberName) && mapMemberInfo.ContainsKey(sMemberName) && ques.QuestionRec.Question_Label__c == Label.HUMCOBRelationshipLabel)
        {
            ques.QuestionRec.Display_text__c = mapMemberInfo.get(sMemberName);
            ques.sValue =  mapMemberInfo.get(sMemberName);
        }
        else if(ques.QuestionRec.Question_Label__c == Label.HUMCOBRelationshipLabel && mapMemberInfo.containsKey(sMemberName))
        {
            ques.QuestionRec.Display_text__c = mapMemberInfo.get(sMemberName);
            ques.sValue =  mapMemberInfo.get(sMemberName);
        }
    }
    
    /*
    * Method Name : wrapTabularAnswer
    * Description : This method sets the data visibility among COB,Add OI, Remove OI tables.
    * Return type : HUMCaseTemplateHelper
    * Paramater   : Tabular_Answer_Response__c,String,String
    */
    public HUMCaseTemplateHelper wrapTabularAnswer(Tabular_Answer_Response__c responseRow, String picklistValues, String dataType)
    {
        HUMCaseTemplateHelper tempWrap = new HUMCaseTemplateHelper();
        tempWrap.responseRec = responseRow;
        tempWrap.sDatatype = datatype;
        
        if(mapPreferences.containsKey(Label.HUMCOBTableName))
        {
            tempWrap.boolCOBTableDisplay = assignRowRendering(mapPreferences, Label.HUMCOBTableName, responseRow);
        }
        if(mapPreferences.containsKey(Label.HUMCOBAddOI))
        {
            tempWrap.boolAddOITableDisplay = assignRowRendering(mapPreferences, Label.HUMCOBAddOI, responseRow);
        }
        if(mapPreferences.containsKey(Label.HUMCOBRemoveOI))
        {
            tempWrap.boolRemoveOITableDisplay = assignRowRendering(mapPreferences, Label.HUMCOBRemoveOI, responseRow);
        }
        
        if (datatype == Label.HUMCOBDropDownType && !string.isBlank(picklistValues))
        {
            tempWrap.lstPicklistValues.add(new SelectOption(Label.HUMCOBNoneSelectOption, Label.HUMCOBNoneSelectOption));
            tempWrap.sSelectedPicklistValue = Label.HUMCOBNoneSelectOption;
            tempWrap.lstPicklistValues.addAll(HUMProcessesHelper.splitPicklistValues(picklistValues));
        }
        return tempWrap;
    }
    
    /*
    * Method Name : assignRowRendering
    * Description : This method sets the data visibility among COB,Add OI, Remove OI tables.
    * Return type : HUMCaseTemplateHelper
    * Paramater   : Map<String, List<String>>,String,Tabular_Answer_Response__c
    */
    private Boolean assignRowRendering(Map<String, List<String>> mapPref,  String tableName, Tabular_Answer_Response__c respRow){
        
        for (String title: mapPref.get(tableName))
        {
            if (respRow.Column_Title__c.equalsIgnoreCase(title))
            {
                return true;
            }
        }
        return false;
    }
    
    /*
    * Method Name : reCalculateRemoveOIDisplay
    * Description : This method recalculates the display of Cancellation Date field on Remove OI Table based on
    *               the 'OI Should Never Have Been Listed' field value.
    * Return type : Void
    * Paramater   : NA
    */
    public void reCalculateRemoveOIDisplay()
    {
        try
        {
            if (mapSequenceResponses.containsKey(iRowNumRemoveOI)) 
            {
                for (HUMCaseTemplateHelper oWrapperRec: mapSequenceResponses.get(iRowNumRemoveOI))
                {
                    if (oWrapperRec.responseRec.Column_Title__c == Label.HUMCOBOIShouldNeverListed)
                    {
                        renderCancellationDate(iRowNumRemoveOI,oWrapperRec.responseRec.boolean_response__c);
                    }
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'reCalculateRemoveOIDisplay');
        }
    }
    
    /*
    * Method Name : renderCancellationDate
    * Description : This method verifies and turns on/off the cancellation date field on page.
    * Return type : void
    * Parameter   : integer, boolean
    */
    private void renderCancellationDate(Integer rowNum, boolean boolExistingResponse)
    {
        for (HUMCaseTemplateHelper oWrapperRec1: mapSequenceResponses.get(rowNum))
        {
            oWrapperRec1.boolDisplayCancellationDate = !boolExistingResponse;
        }
    }
    
    /*
    * Method Name : displayOtherCarrier
    * Description : This method used to display Other Carrier name on Add OI/Remove OI Table based on
    *               selection of Other Carrier name in OI carrier name picklist
    * Return type : Void
    * Paramater   : NA
    */
    public void displayOtherCarrier()
    {
        try
        {
            if (mapSequenceResponses.containsKey(iRowNumRemoveOI))
            {
                for (HUMCaseTemplateHelper oWrapperRec: mapSequenceResponses.get(iRowNumRemoveOI))
                {
                    if(oWrapperRec.sSelectedPicklistValue != NULL)
                    {
                        if(oWrapperRec.sSelectedPicklistValue.trim().equals(Label.HUMCOBOtherCarrier))
                        {
                            boolOtherCarrier = true;
                        }
                        else
                        {
                            boolOtherCarrier = false;
                        }
                        renderOtherCarrier(iRowNumRemoveOI,boolOtherCarrier);
                    }
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'displayOtherCarrier');
        }
    }
    
    /*
    * Method Name : renderOtherCarrier
    * Description : This method verifies and turns on/off the OTher Carrier field on page.
    * Return type : void
    * Parameter   : integer, boolean
    */
    private void renderOtherCarrier(Integer rowNum, boolean boolOtherCarrier)
    {
        for (HUMCaseTemplateHelper oWrapperRec1: mapSequenceResponses.get(rowNum))
        {
            oWrapperRec1.boolDisplayOtherCarrier = boolOtherCarrier;
        }
    }
    
    /*
    * Method Name : doPrepareEmptyTable
    * Description : This method constructs and returns OI table using tabular questions from the query.
    * Return type : map<Integer, list< HUMCaseTemplateHelper >>
    * Parameter   : list<Tabular_Question__c>
    */
    public map<Integer, list< HUMCaseTemplateHelper >> doPrepareEmptyTable(list<Tabular_Question__c> tabularRecords)
    {
        Map<Integer,list< HUMCaseTemplateHelper >> mapReturnWrapper = new Map<Integer, list< HUMCaseTemplateHelper >>();
        lstTabularResponses = new List < HUMCaseTemplateHelper > ();
        integer iRowNum = Integer.valueof(system.label.HUMConstantZero);
        iRowNum++;
        list<policy_member__c> lstPolicyMem = [select id, Member__r.Name , Relationship__c from Policy_member__c where id in: lstPolicyMembersForCOB];
        for (Tabular_Question__c tabularquestion: tabularRecords)
        {
            Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
            oTempVar.Column_Title__c = tabularquestion.column_title__c;
            oTempVar.Grid_Question__c = tabularQuestion.Id;
            oTempVar.Row_Number__c = iRowNum;
            if(oTempVar.Column_Title__c == Label.HUMCOBMemberLabel && lstPolicyMem.size() > iRowNum) oTempVar.Response__c = lstPolicyMem[iRowNum].Member__r.Name;
            if(oTempVar.Column_Title__c == Label.HUMCOBRelationshipLabel&& lstPolicyMem.size() > iRowNum) oTempVar.Response__c = lstPolicyMem[iRowNum].Relationship__c;
            lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c));
            lstColumnTitles.add(tabularquestion.column_title__c);
        }
        lstRowHolders.add(iRowNum);
        mapReturnWrapper.put(iRowNum, lstTabularResponses);
        //Initially designating that all the rows should not be available on AddOI,RemoveOI tables.
        mapAddOIRowDisplayBool.put(iRowNum, false);
        mapRemoveOIRowDisplayBool.put(iRowNum, false);
        iRowNum++;
        return mapReturnWrapper;
    }
    
    
    /*
    * Method Name : prepareAddOITable
    * Description : This method restructures the AddOI,RemoveOI tables for COB based on the checkbox selected for AddOI,RemoveOI.
    * Return type : Void
    * Paramater   : NA
    */
    public void prepareAddOITable()
    {
        Boolean tempBool = !boolOIResponse;
        try
        {
            if (iRowNumAddOI != NULL && sColumnName == Label.HUMCOBAddOI)
            {
                if (mapAddOIRowDisplayBool.containsKey(iRowNumAddOI))
                {
                    mapAddOIRowDisplayBool.put(iRowNumAddOI, !(mapAddOIRowDisplayBool.get(iRowNumAddOI)));
                }
                if (mapAddOIRowDisplayBool.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
                {
                    boolDisplayQuestions = false;
                    boolRenderAddOI = false;
                    for (Integer row: mapAddOIRowDisplayBool.keySet())
                    {
                        if (mapAddOIRowDisplayBool.get(row))
                        {
                            boolDisplayQuestions = true;
                            boolRenderAddOI = true;
                            break;
                        }
                    }
                }
                
            }
            else if (iRowNumAddOI != NULL && sColumnName == Label.HUMCOBRemoveOI)
            {
                if (mapRemoveOIRowDisplayBool.containsKey(iRowNumAddOI))
                {
                    mapRemoveOIRowDisplayBool.put(iRowNumAddOI, !(mapRemoveOIRowDisplayBool.get(iRowNumAddOI)));
                }
                if (mapRemoveOIRowDisplayBool.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
                {
                    boolRenderRemoveOI = false;
                    for (Integer row: mapRemoveOIRowDisplayBool.keySet())
                    {
                        if (mapRemoveOIRowDisplayBool.get(row))
                        {
                            boolRenderRemoveOI = true;
                            break;
                        }
                    }
                }
            }
            for (HUMCaseTemplateHelper wrapperRec: mapSequenceResponses.get(iRowNumAddOI))
            {
                if (wrapperRec.responseRec.Column_Title__c == System.Label.HUMCOBVerified)
                {
                    wrapperRec.responseRec.Boolean_response__c = true;
                }
            }
        }
        catch (Exception e)
        {
            boolRenderAddOI = false;
            boolRenderRemoveOI = false;
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'prepareAddOITable');
        }
    }
    
    /*
    * Method Name : insertCaseActionResponses
    * Description : Inserts CaseActionAssociation record and Case Action Responses.
    * Return type : Case_Action_Association__c
    * Paramater   : NA
    */
    private Case_Action_Association__c insertCaseActionResponses()
    {
        Case_Action__c activeAction = [Select Id from Case_Action__c where Name = : sTemplateType and Active__c = true LIMIT 1];
        List < Case_Action_Response__c > lstResponses = new List < Case_Action_Response__c > ();
        Case_Action_Association__c associationRec = new Case_Action_Association__c();
        if(Schema.sObjectType.Case_Action_Association__c.fields.Case__c.isCreateable()) associationRec.Case__c = sCaseId;
        if(Schema.sObjectType.Case_Action_Association__c.fields.Case_Action__c.isCreateable()) associationRec.Case_Action__c = activeAction.Id;		
        
        String sSubscriberName = '';
        if (boolDisplayQuestions)
        {
            for (HUMCaseActionQuestionHelper question: lstWrapperQuestions)
            {
                if (question.boolDisplayQuestion && question.questionRec.Question_Label__c != Label.OIPolicySubsFName && question.questionRec.Question_Label__c != Label.OIPolicySubsLName)
                {
                    Case_Action_Response__c response = new Case_Action_Response__c();
					if(Schema.sObjectType.Case_Action_Response__c.fields.Process_Question__c.isCreateable()) response.Process_Question__c = question.questionRec.Question_Label__c;					
                    string responseValue = HUMProcessesHelper.processQuestionResponse(question);
                    if(responseValue != '' && Schema.sObjectType.Case_Action_Response__c.fields.Process_Question_Response__c.isCreateable()) { response.Process_Question_Response__c = responseValue; }
                    if(Schema.sObjectType.Case_Action_Response__c.fields.case_Question__c.isCreateable()) response.case_Question__c = question.questionRec.Id;					
                    lstResponses.add(response);
                    if(question.questionRec.Question_Label__c == Label.HUMCOBOtherInsSubscriberName) sSubscriberName = response.Process_Question_Response__c;
                }
            }
            if(mapDependentsFirstName.containskey(sSubscriberName) || mapDependentsLastName.containskey(sSubscriberName))
            {
                for (Question__c question: lstQuestions)
                {
                    if( mapDependentsFirstName.containskey(sSubscriberName) && question.Question_Label__c == Label.OIPolicySubsFName )
                    {
                        Case_Action_Response__c response = new Case_Action_Response__c();
                        response.Process_Question__c = question.Question_Label__c;
                        String sName = mapDependentsFirstName.get(sSubscriberName);
                        response.Process_Question_Response__c = sName;
                        response.case_Question__c = question.Id;
                        lstResponses.add(response);
                    }
                    if( mapDependentsLastName.containskey(sSubscriberName) && question.Question_Label__c == Label.OIPolicySubsLName)
                    {
                        Case_Action_Response__c response = new Case_Action_Response__c();
                        response.Process_Question__c = question.Question_Label__c;
                        String sName = mapDependentsLastName.get(sSubscriberName);
                        response.Process_Question_Response__c = sName;
                        response.case_Question__c = question.Id;
                        lstResponses.add(response);
                    }
                }
            }
        }
        
        insert associationRec;
        // assigning association record to all case action response records
        if(boolDisplayQuestions)
        {
            for(Case_Action_Response__c response : lstResponses )
                {
                    response.Action__c = associationRec.Id;    
                }
        insert lstResponses;
        }       
        
        return associationRec;
    }
    
    /*
    * Method Name : saveAction
    * Description : This method Saves the OI table information and the Questions answered on COB section.
    * Return type : Void
    * Paramater   : NA
    */
    public PageReference saveAction()
    {
        try
        {
            List < String > lstBlankResponses = new List < String > ();
            lstBlankResponses = validateQuestions(lstWrapperQuestions);
            
            if (lstBlankResponses.size() == Integer.valueof(system.label.HUMConstantZero))
            {
                if (casObj.Id != null)
                {
                    Case_Action_Association__c associationRec = insertCaseActionResponses();
                    //Saves Tabular response records.
                    if (mapSequenceResponses.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
                    {
                        List < Tabular_Answer_Response__c > lstTabularResponses = new List < Tabular_Answer_Response__c > ();
                        for (Integer row_num: mapSequenceResponses.keySet())
                        {
                            for (HUMCaseTemplateHelper responseWrapper: mapSequenceResponses.get(row_num))
                            {
								if(Schema.sObjectType.Tabular_Answer_Response__c.fields.Action_Response__c.isCreateable()) responseWrapper.responseRec.Action_Response__c = associationRec.Id;                                     								
                                if( responseWrapper.sDataType != null)
                                {
                                    responseWrapper.responseRec.response__c = assignResponsesForTable(responseWrapper);
                                }
                                lstTabularResponses.add(responseWrapper.responseRec);
                            }
                        }
                        if (lstTabularResponses.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c && boolRenderMainTable){
                            insert lstTabularResponses;
                            bIsSuccess = true;
                            if(sTabId == null){
                                PageReference pgRef =  new ApexPages.Standardcontroller(new Case(Id=casObj.Id)).view();
                                pgRef.setRedirect(true);
                                return pgRef;
                            }
                        }
                    }
                    loadDisplayData(associationRec.Id);
                    sLoadView = Label.HUMCOBView;
                }
                else
                {
                    bIsSuccess = false;
                    ApexPages.addMessage(new ApexPages.message(APEXPAGES.SEVERITY.ERROR, Label.HUMCOBCaseUnavailableErrorMsg));
                }
            }
            else
            {
                bIsSuccess = false;
                //place holder to dsplay error messages if no value is provided.
                String errorMsg = Label.HUMCOBMissingResponses;
                for (String msg: lstBlankResponses)
                {
                    errorMsg = errorMsg + msg + '<br/>';
                }
                throw new HUMCustomException(errorMsg);
            }
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'saveAction');
        }
        return null;
    }
    
    /*
    * Method Name : assignResponsesForTable
    * Description : Copies the text value of data or checkbox into text resposne field, since the text response field is picked up by informatica job.
    * Return type : string
    * Paramater   : HUMCaseTemplateHelper
    */
    private string assignResponsesForTable(HUMCaseTemplateHelper responseWrapper)
    {
        string sReturnResponse = null;
        
        if(responseWrapper.sDatatype == Label.HUMCOBCheckboxType)
        {
            sReturnResponse = string.valueOf(responseWrapper.responseRec.boolean_Response__c);
        }
        if(responseWrapper.sDatatype == Label.HUMCOBDropDownType)
        {
            sReturnResponse = responseWrapper.sSelectedPicklistValue;
        }
        if(responseWrapper.sDatatype == Label.HUMCOBTextType)
        {
            sReturnResponse = responseWrapper.responseRec.Response__c;
        }
        if(responseWrapper.sDatatype == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.date_Response__c != null)
        {
            Date dResponse = responseWrapper.responseRec.date_Response__c;
            sReturnResponse = dResponse.month()+'/'+dResponse.Day()+'/'+dResponse.year();
        }
        return sReturnResponse;
    }
    
    /*
    * Method Name : validateQuestions
    * Description : verifies the responses on page for any left over fields and adds them to list and returns their names.
    * Return type : list<string>
    * Paramater   : list<HUMCaseActionQuestionHelper>
    */
    private list<string> validateQuestions(list<HUMCaseActionQUestionHelper> lstWrapQuestions)
    {
        list<string> lstBlankResponses = new list<string>();
        for (Integer row_num: mapSequenceResponses.keySet())
        {
            for (HUMCaseTemplateHelper responseWrapper: mapSequenceResponses.get(row_num))
            {
                if(responseWrapper.boolAddOITableDisplay && mapAddOIRowDisplayBool.get(row_num)){
                    if (responseWrapper.sDataType == Label.HUMCOBDropDownType && responseWrapper.sSelectedPicklistValue == Label.HUMCOBNoneSelectOption)  lstBlankResponses.add(Label.HUMAddOI+row_num+' :'+responseWrapper.responseRec.Column_Title__c);
                    if (responseWrapper.sDataType == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.Date_Response__c == null)  lstBlankResponses.add(Label.HUMAddOI+row_num+' :'+responseWrapper.responseRec.Column_Title__c);
        
                    if (responseWrapper.responseRec.Column_Title__c == Label.HUMCOBOtherCarrierName && responseWrapper.sDataType == Label.HUMCOBTextType && responseWrapper.responseRec.response__c == null && responseWrapper.boolDisplayOtherCarrier )  lstBlankResponses.add(Label.HUMAddOI + row_num+' :'+responseWrapper.responseRec.Column_Title__c);}if(responseWrapper.boolRemoveOITableDisplay && mapRemoveOIRowDisplayBool.get(row_num)){
                                        }if(responseWrapper.boolRemoveOITableDisplay && mapRemoveOIRowDisplayBool.get(row_num)){
            if (responseWrapper.sDataType == Label.HUMCOBDropDownType && responseWrapper.sSelectedPicklistValue == Label.HUMCOBNoneSelectOption)  lstBlankResponses.add(Label.HUMRemoveOI+row_num+' :'+responseWrapper.responseRec.Column_Title__c);
                    if (responseWrapper.sDataType == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.Date_Response__c == null && responseWrapper.boolDisplayCancellationDate)  lstBlankResponses.add(Label.HUMRemoveOI+row_num+' :'+responseWrapper.responseRec.Column_Title__c);
                    if (responseWrapper.responseRec.Column_Title__c == Label.HUMCOBOtherCarrierName && responseWrapper.sDataType == Label.HUMCOBTextType && responseWrapper.responseRec.response__c == null && responseWrapper.boolDisplayOtherCarrier )  lstBlankResponses.add(Label.HUMRemoveOI + row_num+' :'+responseWrapper.responseRec.Column_Title__c);
                }
            }
        }
        
        
        for (HUMCaseActionQuestionHelper question: lstWrapperQuestions)
        {
            if(question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType && question.sValue != null)
            {
                question.questionRec.Display_Date__c = HUMProcessesHelper.convertStringtoDate(question.sValue);
            }
            if (boolDisplayQuestions && question.boolDisplayQuestion)
            {
                if (question.questionRec.Question_Label__c == Label.HUMCOBSubscriberDOB && question.questionRec.Display_Date__c != null && question.questionRec.Display_Date__c > system.today())  lstBlankResponses.add(Label.HUMProcessDOBError);
                if (question.questionRec.Question_Type__c == Label.HUMCOBDropDownType && question.sSelectedDropDownOption == Label.HUMCOBNoneSelectOption && question.questionRec.Question_Label__c != Label.HUMCOBOtherInsSubscriberName)  lstBlankResponses.add(question.questionRec.Question_Label__c);
                if (question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType && string.isBlank(question.sValue))  lstBlankResponses.add(question.questionRec.Question_Label__c);
                if (question.questionRec.Question_Type__c == Label.HUMCOBDateQuestionType && !string.isBlank(question.sValue) && question.questionRec.Display_Date__c == null)  lstBlankResponses.add(Label.HUMActionInvalidText+' '+question.questionRec.Question_Label__c+'. '+Label.HUMActionDateFormat);
                if (question.questionRec.Question_Type__c == Label.HUMCOBTextType && string.isBlank(question.sValue)){lstBlankResponses.add(question.questionRec.Question_Label__c);}
                if (question.questionRec.Question_Type__c == Label.HUMCOBTextAreaType && string.isBlank(question.questionRec.Display_text_area__c)) lstBlankResponses.add(question.questionRec.Question_Label__c);
            }
        }
        return lstBlankResponses;
    }
    
    /*
    * Method Name : personResponseMethod
    * Description : Calls the PersonService and gets the OI information by passing the Enterprise Id.
    * Return type : PageReference
    * Paramater   : NA
    */
    public void personResponseMethod()
    {
        try
        {
            String sEnterpriseId = '';
            String sMemGenKey = '';
            if (casObj != null)
            {
                sEnterpriseId = String.valueOf(casObj.Account.Enterprise_ID__c);
                sMemGenKey = String.valueOf(casObj.Account.Mbr_Gen_Key__c);
                list<policy_member__c> lstPolicyMem = new list<policy_Member__c>();
                Id polMemId = null;
                if(casObj.Policy_Member__r.Subscriber__c != null){ polMemId = casObj.Policy_Member__r.Subscriber__c; }
                else{ polMemId = casObj.Policy_Member__c; }
                lstPolicyMem = [Select Relationship__c, Name, Member__c, Id, Member__r.Name, Member__r.FirstName , Member__r.LastName , Policy_Member_External_Id__c, (Select Id, Name, Member__r.FirstName , Member__r.LastName ,Relationship__c, Member__c, Member__r.Name, Policy_Member_External_Id__c From Policy_Members__r)
                From Policy_Member__c where Id = : polMemId];
                map<string,string> mapMemberRelationship = new map<string,string>();
                for (Policy_Member__c polMember:lstPolicyMem)
                {
                    mapDependentsFirstName.put(polMember.Member__r.Name,polMember.Member__r.FirstName);
                    mapDependentsLastName.put(polMember.Member__r.Name,polMember.Member__r.LastName);
                    lstPolicyMembersForCOB.add(polMember);
                    if (polMember.Policy_Members__r.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c)
                    {
                        lstPolicyMembersForCOB.addAll(polMember.Policy_Members__r);
                        for (Policy_Member__c member: polMember.Policy_Members__r)
                        {
                            mapDependentsFirstName.put(member.Member__r.Name,member.Member__r.FirstName);
                            mapDependentsLastName.put(member.Member__r.Name,member.Member__r.LastName);
                            mapMemberRelationship.put(member.Member__r.Name,member.Relationship__c);
                            mapMemberInfo.put(member.Member__r.Name,member.Relationship__c);   
                        }
                    }
                    mapMemberRelationship.put(polMember.Member__r.Name,polMember.Relationship__c);
                    mapMemberInfo.put(polMember.Member__r.Name,polMember.Relationship__c); 
                }
            }
            if (lstPolicyMembersForCOB.size() > HUMConstants__c.getValues(system.label.HUMLISTSIZEZERO).IntegerValue__c && !String.isBlank(sEnterpriseId))
            {
                try
                {
                    if (boolIsServiceRequired)
                    {
                        mapResponseWrapper = HUMProcessUtilityHelper.generatePersonServiceResponse(lstPolicyMembersForCOB, sEnterpriseId , sMemGenKey);
                    }
                }
                catch (Exception ex)
                {
                    HUMExceptionHelper.logErrors(ex, 'HUMCOBProcessController', 'personResponseMethod');
                    mapSequenceResponses.clear();
                }
            }
            loadGuide();
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'personResponseMethod');
        }
    }
    
    /*
    * Method Name : cancel
    * Description : Redirects the user back to the Case Page where the navigation to this page happened.
    * Return type : PageReference
    * Paramater   : NA
    */
    public PageReference cancel()
    {
        try
        {
            if(sTabId == null)
            {
                PageReference pgRef =  new ApexPages.Standardcontroller(new Case(Id=sCaseId)).view();
                pgRef.setRedirect(true);
                return pgRef;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'cancel');
        }
        return null;
    }
    
    /*
    * Method Name : getMapSequenceResponsesSize
    * Description : To calculate Map Sequence Response size
    * Return type : Integer
    * Paramater   : NA
    */
    public Integer getMapSequenceResponsesSize()
    {
        try
        {
            return mapSequenceResponses.size();
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'getMapSequenceResponsesSize');
            return Integer.valueof(system.label.HUMConstantZero);
        }
    }
    
    /*
    * Method Name : getRowDispTableMapSize
    * Description : Redirects the user back to the Case Page where the navigation to this page happened.
    * Return type : PageReference
    * Paramater   : NA
    */
    public Integer getRowDispTableMapSize()
    {
        try
        {
            return rowDispTableMap.size();
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController', 'getRowDisplayTableMapSize');
            return Integer.valueof(system.label.HUMConstantZero);
        }
    }
    
    /*
    * Method Name : initialize
    * Description : Initializes all the parameters
    * Return type :
    * Paramater   : NA
    */
    public void initializeProperties()
    {
        sCaseId = '';
        iRowNumRemoveOI = Integer.valueof(system.label.HUMConstantZero);
        mapResponseWrapper = new Map < String, HUMPersonServiceResponseHelper > ();
        rowdispTableMap = new Map < Integer, List < Tabular_Answer_Response__c >> ();
        boolOIResponse = false;
        boolRenderAddOI = false;
        boolRenderRemoveOI = false;
        boolRenderMainTable = false;
        boolIsServiceRequired = true;
        lstPreferences = new List < HUMTemplateCOBColumns__c > ();
        lstQuestions = new List < Question__c > ();
        lstWrapperQuestions = new List < HUMCaseActionQuestionHelper > ();
        lstTabularQuestions = new List < Tabular_Question__c > ();
        lstColumnTitles = new List < String > ();
        lstTabularResponses = new List < HUMCaseTemplateHelper > ();
        mapAddOIRowDisplayBool = new Map < Integer, Boolean > ();
        mapRemoveOIRowDisplayBool = new Map < Integer, Boolean > ();
        mapAddOIRowDisplayBool = new Map < Integer, Boolean > ();
        mapRemoveOIRowDisplayBool = new Map < Integer, Boolean > ();
        mapMasterConditions = new Map < String, Map < String, List < String >>> ();
        mapPreferences = new Map < String, List < String >> ();
        mapMemberInfo = new Map < String, String > ();
        lstPolicyMembersForCOB = new List < Policy_Member__c > ();
        lstRowHolders = new List < Integer > ();
        mapSequenceResponses = new Map < Integer, List < HUMCaseTemplateHelper >> ();
        boolDisplayQuestions = true;
        dispCaseResponses = new List < Case_Action_Response__c > ();
        columnrows = new List < Integer > ();
        bIsSuccess = false;
    }
}