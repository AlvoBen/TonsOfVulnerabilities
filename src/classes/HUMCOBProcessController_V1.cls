/******************************************************************************************************************************
Apex Class Name  : HUMCOBProcessController_V1
Version          : 1.0
Created Date     : Aug 03 2020
Function         : Controller for the V1 version of the COB Template page. 
                   MBEPlus service is used to retreive the OI details and display on the COB template page.                   
Test Class       : HUMCOBProcessController_V1_Test
Modification Log :
* Developer             Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor                             08/03/2020           Original Version - Created for US825176
* Mohammed Noor                             08/14/2020           Fix Defect DF-1312 and suggestions from Business Demo 
* Mohammed Noor                             08/19/2020           US825176 - Fix Cancellation Date and New Effective date from clearing out.
* Mohammed Noor                             10/05/2020           User Story 1474107 - Fix Defect DF-1765
* Mohammed Noor                             11/13/2020			 Changes done for User Story 1654207.
* Mohammed Noor								02/09/2021			 USER STORY 1700807 - PR00093155 MF-18 Use new node OICoordinationOfBenefitList
* Mohammed Noor								03/15/2021			 USER STORY 1700807 - PR00093155 MF-18 Added On/Off Switch to include the new OICOB node in the request
* Jasmeen Shangari                          04/21/2021           Checkmarx fixes Azure ID - 2163309
*********************************************************************************************************************************/

public with sharing class HUMCOBProcessController_V1
{        
    private String sTemplateType = '';
    private list < Tabular_Question__c > lstTabularQuestions = new list< Tabular_Question__c >();
    private list< Tabular_Question__c> lstExistingOIQuestions = new list< Tabular_Question__c>();
    private list < String > lstColumnTitles = new list<string>();
    private list < HUMCaseTemplateHelper > lstTabularResponses = new list< HUMCaseTemplateHelper >();
    private list < Policy_Member__c > lstPolicyMembersForCOB = new list< Policy_Member__c >();
    private map < String, List < String >> mapPreferences = new map< String,list< string >>();
    private list < HUMTemplateCOBColumns__c > lstPreferences = new list< HUMTemplateCOBCOlumns__c >();
    private list < Question__c > lstQuestions = new List < Question__c > ();
    private boolean boolOtherCarrier= false;    
    private Map<String,String> mapDependentsFirstName = new Map<String,String>();
    private Map<String,String> mapDependentsLastName = new Map<String,String>();
    public map<String, Policy_Member__c> mapPolicyMembers = new map<string, Policy_Member__c>();
    public map<String, list<Policy_Plan_Details__c>> mapPolicyPlanDetails = new map<string, list<Policy_Plan_Details__c>>();
    public set<String> setRemainingIDs = new set<String>();
    public set<String> setReadOnlyIDs = new set<String>();    
    public map<string, string> mapRequestLabels = new map<string, string>();
    public Integer iChainingCount = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM;    

    /*
    * Method Name : HUMCOBProcessController_V1
    * Description : Initializes variables and gathers data into query variables.
    * Return type : Void
    * Paramater   : NA
    */
    public HUMCOBProcessController_V1()
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
            lstLogErrors = HUMExceptionHelper.logErrors(e,'HUMCOBProcessController_V1','HUMCOBProcessController_V1');
        }
    }
    
    public Case casObj{get;set;}
    public String sCaseId {get;set;}
    public Integer iRowNumAddOI {get;set;}
    public Integer iRowNumRemoveOI {get;set;}
    public String sColumnName {get;set;}
    public string sLoadView {get;set;}
    public Boolean boolOIResponse {get;set;}
    public map<String, list<HUMPersonServiceResponseHelper>> mapResponseWrapperMBE {get; set;}
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
    public boolean bShowExistingOITable {get; set;}
    public map < Integer, List < Tabular_Answer_Response__c >> rowdispTableMap {get;set;}
    public map < Integer, List < Tabular_Answer_Response__c >> mapOIdispTable {get;set;}
    public list < Integer > columnrows {get;set;}
    public list < Integer > columnrowsOI {get;set;}
    public String sTabId {get; set;}
    public Boolean bIsSuccess {get; set;}
    public Boolean bFamilySizeMessage {get; set;}
    public Boolean bAddOIMessage {get; set;}
	public Boolean bDetailAddOIInfo {get; set;}
    public Date dDateValue {get; set;}
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
            sCaseId = (Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam) != null) ? Apexpages.currentPage().getParameters().get(Label.HUMCOBCaseIdParam).escapehtml4() : null;
            sTabId = (Apexpages.currentPage().getParameters().containsKey(Label.HUMTabId) != null) ?  Apexpages.currentPage().getParameters().get(Label.HUMTabId).escapehtml4() : null;
            casObj = [Select Id, CaseNumber, Policy_Member__r.Member__r.FirstName , Policy_Member__r.Member__r.LastName , Policy_Member__c, AccountId, Account.Enterprise_ID__c, Account.Mbr_Gen_Key__c, Policy_Member__r.Member__c, Policy_Member__r.Member__r.Enterprise_Id__c,Policy_Member__r.Relationship__c,Policy_Member__r.Subscriber__c,
            (Select Id, Case_Action__c, Action__c From Case_Action_Associations__r) from Case where Id = : sCaseId];
            if (casObj.Case_Action_Associations__r != null && !casObj.Case_Action_Associations__r.isEmpty())
            {
                sLoadView = Label.HUMCOBView;
                boolean boolIsDataReturned = loadDisplayData(casObj.Case_Action_Associations__r[Integer.valueof(system.label.HUMConstantZero)].Id);
                if(!boolIsDataReturned){  ApexPages.addMessage(new Apexpages.Message(APEXPAGES.SEVERITY.INFO,Label.HUMCOBIncorrectCaseAction)); }
                ApexPages.addMessage(new Apexpages.Message(APEXPAGES.SEVERITY.INFO,Label.HUMCOBEditLabel));
            }
            else
            {
                loadEditView();
                retrieveCoverageDetails();
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
        HUMTemplateCOBColumns__c cobTableColumnPreferences = HUMTemplateCOBColumns__c.getValues(Label.HUMCOBTemplateLabelV1);
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
    * Method Name : retrieveCoverageDetails
    * Description : This method is used to retrieve the coverage details for all the family members to be dispalyed on COB template
    * Return type : Void
    * Paramater   : None
    */
    public void retrieveCoverageDetails()
    {
        Id polMemId = null;
        mapPolicyMembers = new map<string, Policy_Member__c>();
        mapPolicyPlanDetails = new map<string, list<Policy_Plan_Details__c>>();
        setRemainingIDs = new set<string>();
        setReadOnlyIDs = new set<string>();
        if (casObj != null)
        {                
            if(casObj.Policy_Member__r.Subscriber__c != null)   //If the Member is not the subscriber then take the subscriber's Policy_Member__c ID and get all the related policies
            {                    
                polMemId = casObj.Policy_Member__r.Subscriber__c;
            }
            else    //if the Member is Subscriber, then take Memeber's Policy_Member ID
            {                    
                polMemId = casObj.Policy_Member__c;
            }
            
            if(String.isNotBlank(polMemId))
            {                
                list<policy_member__c> lstPolicyMem = new list<policy_Member__c>();
                set<Id> setPolicyIds = new set<Id>();
                lstPolicyMem = [Select Id, Relationship__c, Name, Member__c, Member__r.Name, Member__r.FirstName , Member__r.LastName , Member__r.Enterprise_Id__c, Policy_Member_External_Id__c, Effective_Date__c, End_Date__c, 
                                           Policy__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Group_Number__c, Policy__r.Source_Cust_Cov_Key__c,
                                           (Select Id, Relationship__c, Name, Member__c, Member__r.Name, Member__r.FirstName , Member__r.LastName , Member__r.Enterprise_Id__c, Policy_Member_External_Id__c, Effective_Date__c, End_Date__c, 
                                           Policy__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Group_Number__c, Policy__r.Source_Cust_Cov_Key__c From Policy_Members__r)
                                           From Policy_Member__c where Id = : polMemId limit 1];
                                
                if(lstPolicyMem!=null && !lstPolicyMem.isEmpty())
                {                    
                    if(String.isNotBlank(lstPolicyMem[0].Member__r.Enterprise_Id__c))
                    {   
                        mapPolicyMembers.put(lstPolicyMem[0].Member__r.Enterprise_Id__c, lstPolicyMem[0]);
                        mapDependentsFirstName.put(lstPolicyMem[0].Member__r.Name,lstPolicyMem[0].Member__r.FirstName);
                        mapDependentsLastName.put(lstPolicyMem[0].Member__r.Name,lstPolicyMem[0].Member__r.LastName);
                        mapMemberInfo.put(lstPolicyMem[0].Member__r.Name,lstPolicyMem[0].Relationship__c);
                        if(lstPolicyMem[0].Policy__c !=null) { setPolicyIds.add(lstPolicyMem[0].Policy__c); }
                    }
                                                                                                                                
                    if(lstPolicyMem[0].Policy_Members__r!=null && !lstPolicyMem[0].Policy_Members__r.isEmpty())
                    {
                        /* Start of Logic -  If the family has more than 9 members and if the Case is created against any of the family member other than
                         * subscriber, then explicitly adding the Case's Member to the map, so the member will displayed in the COB Guide section.
                         */
                        if(lstPolicyMem[0].Policy_Members__r.size() >= GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH && !lstPolicyMem[0].ID.equals(casObj.Policy_Member__c))
                        {                                                       
                            map<Id, Policy_Member__c> mapTempPolicyMem = new map<Id, Policy_Member__c>();
                            mapTempPolicyMem.putAll(lstPolicyMem[0].Policy_Members__r);
                            if(mapTempPolicyMem!=null && !mapTempPolicyMem.isEmpty() && mapTempPolicyMem.containsKey(casObj.Policy_Member__c))
                            {
                                Policy_Member__c polMem = mapTempPolicyMem.get(casObj.Policy_Member__c);
                                mapPolicyMembers.put(polMem.Member__r.Enterprise_Id__c, polMem);
                                mapDependentsFirstName.put(polMem.Member__r.Name,polMem.Member__r.FirstName);
                                mapDependentsLastName.put(polMem.Member__r.Name,polMem.Member__r.LastName);
                                mapMemberInfo.put(polMem.Member__r.Name,polMem.Relationship__c);
                                if(polMem.Policy__c !=null) { setPolicyIds.add(polMem.Policy__c); }
                            }                               
                        }
                        /* End of Logic */
                                                
                        for(Policy_Member__c polMem : lstPolicyMem[0].Policy_Members__r)
                        {
                            if(String.isNotBlank(polMem.Member__r.Enterprise_Id__c))
                            {    
                                mapPolicyMembers.put(polMem.Member__r.Enterprise_Id__c, polMem);
                                mapDependentsFirstName.put(polMem.Member__r.Name,polMem.Member__r.FirstName);
                                mapDependentsLastName.put(polMem.Member__r.Name,polMem.Member__r.LastName);
                                mapMemberInfo.put(polMem.Member__r.Name,polMem.Relationship__c);
                                if(polMem.Policy__c !=null) { setPolicyIds.add(polMem.Policy__c); }
                            }
                        }
                    }                    
                }                
                if(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty()) 
                {
                    setRemainingIDs.addAll(mapPolicyMembers.keySet());
                    list<Policy_Member__c> lstPolicyMembers = mapPolicyMembers.values();
                    //adding the remaining members to be displayed as read only on the guide cob table
                    if(lstPolicyMembers.size() > GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH)  
                    {
                        for(Integer i = GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH; i<lstPolicyMembers.size(); i++)
                        {                                
                            setReadOnlyIDs.add(lstPolicyMembers[i].Member__r.Enterprise_Id__c);             
                        }
                    }
                    if(setPolicyIds!=null && !setPolicyIds.isEmpty())
                    {

                        list<Policy_Plan_Details__c> lstPolicyPlans = new list<Policy_Plan_Details__c>();
                        lstPolicyPlans = [SELECT Id, Name, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Policy__c, Product__c, Product__r.Sold_Product_Key_Value__c, Policy_Group_Number__c, Source_Cust_Cov_Key__c 
                                                          FROM Policy_Plan_Details__c Where Policy__c IN :setPolicyIds ];
                                                
                        if(lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
                        {
                            for(Policy_Plan_Details__c oPlan : lstPolicyPlans)
                            {
                                if(mapPolicyPlanDetails.containsKey(oPlan.Policy__c)) { mapPolicyPlanDetails.get(oPlan.Policy__c).add(oPlan); }
                                else { mapPolicyPlanDetails.put(oPlan.Policy__c, new list<Policy_Plan_Details__c>{oPlan}); }
                            }
                        }                                                
                    }                    
                }                                
            }
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
            if (rowdispTableMap.containsKey(Integer.valueOf(tabResp.Row_Number__c)) && rowdispTableMap.get(Integer.valueOf(tabResp.Row_Number__c)) != null)
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
        if(rowdispTableMap != null && !rowdispTableMap.isEmpty())
        {
            lstColumns.addAll(rowdispTableMap.keySet());
        }        
        if(lstColumns != null && !lstColumns.isEmpty())
        {
            for(Integer i : lstColumns)
            {                
                if (rowdispTableMap.containsKey(i) && getMemRelColIndex(rowdispTableMap.get(i))<rowdispTableMap.get(i).size())
                {
                    rowdispTableMap.get(i).remove(getMemRelColIndex(rowdispTableMap.get(i)));
                }                
            }
        }        
        return lstColumns;
    }
    
    /*
    * Method Name : addRowOnExistingOITable
    * Description : This method is used to populate the Existing OI details for Display on the page.
    * Return type : void
    * Paramater   : lstActionResponses - Tabular_Answer_Response__c containing the Existing OI information
    */
    public void addRowOnExistingOITable(list<Tabular_Answer_Response__c> lstActionResponses)
    {
        mapOIdispTable = new map<Integer, List <Tabular_Answer_Response__c>>();
        columnrowsOI = new list<Integer>();
        if(lstActionResponses!=null && !lstActionResponses.isEmpty())
        {
            for (Tabular_Answer_Response__c tabResp: lstActionResponses)
            {            
                if(mapOIdispTable.containsKey(Integer.valueOf(tabResp.Row_Number__c)))              
                    mapOIdispTable.get(Integer.valueOf(tabResp.Row_Number__c)).add(tabResp);                                
                else
                    mapOIdispTable.put(Integer.valueOf(tabResp.Row_Number__c), new List<Tabular_Answer_Response__c> {tabResp});         
            }
        }           
        if(mapOIdispTable!=null && !mapOIdispTable.isEmpty())
        {
            columnrowsOI.addAll(mapOIdispTable.keySet());
            bShowExistingOITable = true;    //set the boolean flag to display the existing OI table on the Detail page
        }              
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
		if(dispCaseResponses!=null && !dispCaseResponses.isEmpty()) { bDetailAddOIInfo = true; }
        list<Tabular_Answer_Response__c> lstActionResponses = new List<Tabular_Answer_Response__c>();
        list<Tabular_Answer_Response__c> lstExistingOIDetails = new List<Tabular_Answer_Response__c>();
        for(Tabular_Answer_Response__c tabAns : HUMProcessesHelper.getTabularAnswerResponses(caseAssociationId))
        {
            if(String.isNotBlank(tabAns.Grid_Question__r.Question__r.Question_Label__c) && tabAns.Grid_Question__r.Question__r.Question_Label__c.equalsIgnoreCase(Label.HUMCOBExistingOIInfo))
            {
               lstExistingOIDetails.add(tabAns); 
            }
            else 
            {
                if(String.isNotBlank(tabAns.Column_Title__c) && !tabAns.Column_Title__c.equalsIgnoreCase(Label.MemberFirstName) && !tabAns.Column_Title__c.equalsIgnoreCase(Label.MemberLastName) && !tabAns.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOIDentalVerifiedDate))
                { 
                	if(tabAns.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOIMedicalVerifiedDate)) tabAns.Column_Title__c = Label.HUMCOBOILastVerifiedDate;
                	lstActionResponses.add(tabAns);
                }          
            }
        }
        if(lstActionResponses!=null && !lstActionResponses.isEmpty())
        {
            list<Integer> lstColRowsFromResult = addRowonDisplayTable(lstActionResponses);
            if(lstColRowsFromResult!=null && !lstColRowsFromResult.isEmpty()){ columnrows.addAll(lstColRowsFromResult); }
        }
        if(lstExistingOIDetails!=null && !lstExistingOIDetails.isEmpty())
        {
            addRowOnExistingOITable(lstExistingOIDetails);
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
        if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBMemberLabel) && String.isNotBlank(responseRowData.Member)) {response = responseRowData.Member;}  
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBRelationshipLabel) && String.isNotBlank(responseRowData.Relationship)){ response = responseRowData.Relationship;  }
        //Assign response value for memberRelationship
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBMemberRelationshipLabel)){ response = responseRowData.memberRelationship; }
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBOICarrierName)){ response = responseRowData.OICarriername;  }
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBOIEffectiveDate) && string.isNotBlank(responseRowData.OIEffectiveDate)){
            response = responseRowData.OIEffectiveDate;
        }
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBOIEndDate) && string.isNotBlank(responseRowData.OIEndDate)){
            response = responseRowData.OIEndDate;
        }
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBOILastVerifiedDate) && string.isNotBlank(responseRowData.OILastVerifieddate)){            
            response = responseRowData.OILastVerifieddate;
        }
        else if (sColumnTitle.equalsIgnoreCase(Label.HUMCOBOIPrimary)){  
            response = responseRowData.OIPrimary;
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
    public void loadGuide()
    {        
        lstQuestions = [Select Id, Name, Question_Label__c, Parent_Question__c, Parent_Question__r.Question_Label__c, Question_Type__c, Question_Values__c, Dependent_Value__c,
        Display_Text__c, Display_Text_Area__c, Display_Checkbox__c, Display_Date__c
        from Question__c where RecordType.Name = : sTemplateType AND Active__c = true ORDER BY Sequence_Number__c ASC NULLS LAST
        ];        
        //Loads the table structure to be displayed on page.
        list<Tabular_Question__c> tempList = [select Id, Name, Column_Title__c, Question_Type__c, Question_Values__c, Question__r.Question_Label__c from Tabular_Question__c ORDER BY Sequence_Number__c ASC NULLS LAST];
        for(Tabular_Question__c tQues : tempList)
        {
            if(String.isNotBlank(tQues.Question__r.Question_Label__c) && tQues.Question__r.Question_Label__c.equalsIgnoreCase(Label.HUMCOBExistingOIInfo))
            {
                lstExistingOIQuestions.add(tQues);              
            }
            else
            {
                if(tQues.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOIMedicalVerifiedDate)) tQues.Column_Title__c = Label.HUMCOBOILastVerifiedDate;
                lstTabularQuestions.add(tQues);
            }
        }               
        mapSequenceResponses = loadGenericTemplateTable(lstTabularQuestions);        
        if (lstQuestions!=null && !lstQuestions.isEmpty())   prepareOnScreenQuestionsDisplay(lstQuestions);
        if(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty() && mapPolicyMembers.size() > GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH)
        {
            bFamilySizeMessage = true; 
        }                
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
            if (ques.Question_Label__c == Label.HUMCOBOtherInsSubscriberName && mapMemberInfo != null && !mapMemberInfo.isEmpty())
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
        
        if(mapResponseWrapperMBE!=null && !mapResponseWrapperMBE.isEmpty())
        {
            integer iRowNum = Integer.valueof(system.label.HUMConstantZero);
            iRowNum++;
            list<HUMPersonServiceResponseHelper> lstCOBDetails = new list<HUMPersonServiceResponseHelper>();
            // Loading Tabular response row.
            for (String key: mapResponseWrapperMBE.keyset())
            {
                lstCOBDetails = new list<HUMPersonServiceResponseHelper>();
                lstCOBDetails = mapResponseWrapperMBE.get(key);
                if(lstCOBDetails!=null && !lstCOBDetails.isEmpty())
                {
                    for(HUMPersonServiceResponseHelper oCOBDetails : lstCOBDetails)
                    {
                        addTabularResponses(lstTabularQuestions,key,iRowNum,oCOBDetails);
                        lstRowHolders.add(iRowNum);
                        mapReturnWrapper.put(iRowNum, lstTabularResponses);
                        //Initially designating that all the rows should not be available on AddOI,RemoveOI tables.
                        mapAddOIRowDisplayBool.put(iRowNum, false);
                        mapRemoveOIRowDisplayBool.put(iRowNum, false);
                        iRowNum++;      
                    }
                }                                         
            }
            if(setReadOnlyIDs!=null && !setReadOnlyIDs.isEmpty() && mapPolicyMembers!=null && !mapPolicyMembers.isEmpty())
            {                        
                for(String sEnterpriseID : setReadOnlyIDs)
                {                    
                    lstTabularResponses = new List < HUMCaseTemplateHelper >(); 
                    if(mapPolicyMembers.containsKey(sEnterpriseID))
                    { 
                        Policy_Member__c policyMem = mapPolicyMembers.get(sEnterpriseID);
                        for (Tabular_Question__c tabularquestion: lstTabularQuestions)
                        {
                            Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
                            oTempVar.Column_Title__c = tabularquestion.column_title__c;
                            oTempVar.Grid_Question__c = tabularQuestion.Id;
                            oTempVar.Row_Number__c = iRowNum;
                            if(oTempVar.Column_Title__c == Label.HUMCOBMemberLabel) oTempVar.Response__c = policyMem.Member__r.Name;
                            if(oTempVar.Column_Title__c == Label.HUMCOBRelationshipLabel) oTempVar.Response__c = policyMem.Relationship__c;
                            if(tabularquestion.Column_Title__c == Label.HUMCOBMemberLabel || tabularquestion.Column_Title__c == Label.HUMCOBRelationshipLabel)
                            {
                                lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, false));
                            }
                            else
                            {
                                lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, true));
                            }
                        }
                        mapReturnWrapper.put(iRowNum, lstTabularResponses);
                        mapAddOIRowDisplayBool.put(iRowNum, false);
                		mapRemoveOIRowDisplayBool.put(iRowNum, false);
                		lstRowHolders.add(iRowNum);
                        iRowNum++;                        
                    }
                }
            }
        }
        //If mapResponseWrapper doesn't contains any keys then below table should prepare.
        else { mapReturnWrapper = doPrepareEmptyTable(lstTabularQuestions); }
        lstRowHolders.sort();
        return mapReturnWrapper;
    }
    
    /*
    * Method Name : addTabularResponses
    * Description : Adds rows to tabular responses list by extracting the values from tabular question.
    * Return type : Void
    * Paramater   : list<Tabular_Question__c>, string, integer
    */
    private void addTabularResponses(list<Tabular_Question__c> lstTabularQuestions, string key, integer iRowNum, HUMPersonServiceResponseHelper oCOBDisplayData)
    {
        lstTabularResponses = new List < HUMCaseTemplateHelper > ();
        String sMemberName = '';
        for (Tabular_Question__c tabularquestion: lstTabularQuestions)
        {
            if(tabularquestion.column_title__c != Label.MemberFirstName && tabularquestion.column_title__c != Label.MemberLastName)
            {
                Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
                setResponseValue(tabularquestion.column_title__c, oCOBDisplayData, oTempVar );
                oTempVar.Column_Title__c = tabularquestion.column_title__c;
                oTempVar.Grid_Question__c = tabularQuestion.Id;
                oTempVar.Row_Number__c = iRowNum;
                lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, false));
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
                    setResponseValue(tabularquestion.column_title__c, oCOBDisplayData, oTempVar );
                    String sName = ''; 
                    if(tabularquestion.column_title__c == Label.MemberFirstName) 
                    {
                        sName = mapDependentsFirstName.get(sMemberName );
                        oTempVar.Response__c = sName;
                    }
                    if(tabularquestion.column_title__c == Label.MemberLastName) 
                    {
                        sName = mapDependentsLastName.get(sMemberName );
                        oTempVar.Response__c = sName;
                    }
                    oTempVar.Column_Title__c = tabularquestion.column_title__c;
                    oTempVar.Grid_Question__c = tabularQuestion.Id;
                    oTempVar.Row_Number__c = iRowNum;
                    lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, false));
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
    * Method Name : persistDateValue
    * Description : This method is used to persist the user selected value in Cancellation Date for RemoveOI and   
    *               New Effective Date for AddOI. A call to the controller is able retain the values in the inputField tags.
    *               Dummy assignment is done to add logic inside the method to retain the values.
    * Return type : Void
    * Paramater   : NA
    */
    public void persistDateValue()
    {
    	try
        {
            if(iRowNumRemoveOI!=null && String.isNotBlank(sColumnName) && mapSequenceResponses.containsKey(iRowNumRemoveOI))
            {	            
                for (HUMCaseTemplateHelper oWrapperRec: mapSequenceResponses.get(iRowNumRemoveOI))
                {
                    if (oWrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(sColumnName))
                    {                                                
                        if(oWrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_Cancellation_Date))	//Cancellation Date
                        {
                        	oWrapperRec.boolDisplayCancellationDate = true;
                        }
                        if(oWrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_New_Effective_Date))	//New Effective Date
                        {
                        	oWrapperRec.boolAddOITableDisplay = true;	
                        }
						break;
                    }
                }	           
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'persistDateValue');
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
    * Paramater   : Tabular_Answer_Response__c,String,String,Boolean
    */
    public HUMCaseTemplateHelper wrapTabularAnswer(Tabular_Answer_Response__c responseRow, String picklistValues, String dataType, boolean bEmptyCell)
    {
        HUMCaseTemplateHelper tempWrap = new HUMCaseTemplateHelper();
        tempWrap.responseRec = responseRow;
        tempWrap.sDatatype = datatype;
        
        if(mapPreferences.containsKey(Label.HUMCOBTemplateLabelV1))
        {
            tempWrap.boolCOBTableDisplay = assignRowRendering(mapPreferences, Label.HUMCOBTemplateLabelV1, responseRow);
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
        if(bEmptyCell!=null)
        {
            tempWrap.boolDisplayEmptyCell = bEmptyCell;
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
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'reCalculateRemoveOIDisplay');
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
            if(boolExistingResponse && oWrapperRec1.responseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_Cancellation_Date)) 
            {                
                oWrapperRec1.responseRec.date_response__c = null; 
            }
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
            if (mapSequenceResponses.containsKey(iRowNumRemoveOI) && String.isNotBlank(sColumnName))
            {
                for (HUMCaseTemplateHelper oWrapperRec: mapSequenceResponses.get(iRowNumRemoveOI))
                {
                    if(oWrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOICarrierName))
                    {
                        if(Label.HUMCOBAddOI.equalsIgnoreCase(sColumnName))
                        {                                       
                            boolOtherCarrier = String.isNotBlank(oWrapperRec.sSelectedPicklistValueAddOI) && oWrapperRec.sSelectedPicklistValueAddOI.trim().equalsIgnoreCase(Label.HUMCOBOtherCarrier) ? true : false;                                                                          
                        }
                        else if(Label.HUMCOBRemoveOI.equalsIgnoreCase(sColumnName))
                        {                                                       
                            boolOtherCarrier = String.isNotBlank(oWrapperRec.sSelectedPicklistValue) && oWrapperRec.sSelectedPicklistValue.trim().equalsIgnoreCase(Label.HUMCOBOtherCarrier) ? true : false;                                                                                                                    
                        }
                        renderOtherCarrier(iRowNumRemoveOI,boolOtherCarrier, sColumnName);
                        break;
                    }
                }
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'displayOtherCarrier');
        }
    }
    
    /*
    * Method Name : renderOtherCarrier
    * Description : This method verifies and turns on/off the OTher Carrier field on page.
    * Return type : void
    * Parameter   : integer, boolean
    */
    private void renderOtherCarrier(Integer rowNum, boolean boolOtherCarrier, String sColName)
    {
        for (HUMCaseTemplateHelper oWrapperRec1: mapSequenceResponses.get(rowNum))
        {
            if(Label.HUMCOBOtherCarrierName.equalsIgnoreCase(oWrapperRec1.responseRec.Column_Title__c))
            {
                if(Label.HUMCOBAddOI.equalsIgnoreCase(sColName))
                {
                    oWrapperRec1.boolDisplayOtherCarrierAddOI = boolOtherCarrier;
                    if(!boolOtherCarrier && String.isNotBlank(oWrapperRec1.sOtherCarrierNameAddOI))
                    {
                        oWrapperRec1.sOtherCarrierNameAddOI = null;     //clear the other carrier name if it was entered by the User
                    }
                }
                else if(Label.HUMCOBRemoveOI.equalsIgnoreCase(sColName))
                {
                    oWrapperRec1.boolDisplayOtherCarrier = boolOtherCarrier;
                    if(!boolOtherCarrier && String.isNotBlank(oWrapperRec1.responseRec.response__c))
                    {
                        oWrapperRec1.responseRec.response__c = null;    //clear the other carrier name if it was entered by the User
                    }
                }
                break;
            }
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
        integer iRowNum = Integer.valueof(system.label.HUMConstantZero);
        iRowNum++;
        list<policy_member__c> lstPolicyMem = new list<Policy_Member__c>();
        if(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty()) { lstPolicyMem = mapPolicyMembers.values(); }
        
        if(lstPolicyMem!=null && !lstPolicyMem.isEmpty())
        {
            for(Policy_Member__c  policyMem : lstPolicyMem)
            {
                lstTabularResponses = new List < HUMCaseTemplateHelper > ();
                for (Tabular_Question__c tabularquestion: tabularRecords)
                {
                    if(iRowNum <= GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH)
                    {                               
                        Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
                        oTempVar.Column_Title__c = tabularquestion.column_title__c;
                        oTempVar.Grid_Question__c = tabularQuestion.Id;
                        oTempVar.Row_Number__c = iRowNum;
                        if(oTempVar.Column_Title__c.equalsIgnoreCase(Label.HUMCOBMemberLabel)) oTempVar.Response__c = policyMem.Member__r.Name;
                        if(oTempVar.Column_Title__c.equalsIgnoreCase(Label.HUMCOBRelationshipLabel)) oTempVar.Response__c = policyMem.Relationship__c;
                        lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, false));                                                              
                    }
                    else
                    {                                                              
                        Tabular_Answer_Response__c oTempVar = new Tabular_Answer_Response__c();
                        oTempVar.Column_Title__c = tabularquestion.column_title__c;
                        oTempVar.Grid_Question__c = tabularQuestion.Id;
                        oTempVar.Row_Number__c = iRowNum;
                        if(oTempVar.Column_Title__c == Label.HUMCOBMemberLabel) oTempVar.Response__c = policyMem.Member__r.Name;
                        if(oTempVar.Column_Title__c == Label.HUMCOBRelationshipLabel) oTempVar.Response__c = policyMem.Relationship__c;
                        if(tabularquestion.Column_Title__c.equalsIgnoreCase(Label.HUMCOBMemberLabel) || tabularquestion.Column_Title__c.equalsIgnoreCase(Label.HUMCOBRelationshipLabel))
                        {
                            lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, false));
                        }
                        else
                        {
                            lstTabularResponses.add(wrapTabularAnswer(oTempVar, tabularquestion.Question_Values__c, tabularQuestion.Question_Type__c, true));
                        }                                                                                                                                            
                    }                              
                }
                lstRowHolders.add(iRowNum);
                mapReturnWrapper.put(iRowNum, lstTabularResponses);
                //Initially designating that all the rows should not be available on AddOI,RemoveOI tables.
                mapAddOIRowDisplayBool.put(iRowNum, false);
                mapRemoveOIRowDisplayBool.put(iRowNum, false);
                iRowNum++;
            }
        }                
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
                bAddOIMessage = false;
                if (mapAddOIRowDisplayBool.containsKey(iRowNumAddOI))
                {
                    mapAddOIRowDisplayBool.put(iRowNumAddOI, !(mapAddOIRowDisplayBool.get(iRowNumAddOI)));
                }
                if (mapAddOIRowDisplayBool!=null && !mapAddOIRowDisplayBool.isEmpty())
                {
                    boolDisplayQuestions = false;
                    boolRenderAddOI = false;
                    for (Integer row: mapAddOIRowDisplayBool.keySet())
                    {
                        if (mapAddOIRowDisplayBool.get(row))
                        {
                            boolDisplayQuestions = true;
                            boolRenderAddOI = true;                            
                            bAddOIMessage = true;  //set the flag to dislay the ADDOI Message
                            break;
                        }
                    }
                }
                if(!boolOIResponse && mapSequenceResponses.containsKey(iRowNumAddOI))
                {
                    for (HUMCaseTemplateHelper wrapperRec: mapSequenceResponses.get(iRowNumAddOI))
                    {                                                               
                        if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOICarrierName) && String.isNotBlank(wrapperRec.sSelectedPicklistValueAddOI))
                        {                       
                            wrapperRec.sSelectedPicklistValueAddOI = '';
                        }                                                        
                        else if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOtherCarrierName) && String.isNotBlank(wrapperRec.sOtherCarrierNameAddOI))
                        {
                            wrapperRec.sOtherCarrierNameAddOI = '';
                            wrapperRec.boolDisplayOtherCarrierAddOI = false;                                                                                                             
                        }  
                        else if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_New_Effective_Date) && wrapperRec.responseRec.date_response__c!=null)
                        {
                                wrapperRec.responseRec.date_response__c=null;
                        }                                                                           
                    }
                }                              
            }
            else if (iRowNumAddOI != NULL && sColumnName == Label.HUMCOBRemoveOI)
            {
                boolean bChecked = false;
                boolean bOICarrierExist = false;
                if (mapRemoveOIRowDisplayBool.containsKey(iRowNumAddOI))
                {
                    mapRemoveOIRowDisplayBool.put(iRowNumAddOI, !(mapRemoveOIRowDisplayBool.get(iRowNumAddOI)));
                    bChecked = mapRemoveOIRowDisplayBool.get(iRowNumAddOI);
                }
                if (mapRemoveOIRowDisplayBool!=null && !mapRemoveOIRowDisplayBool.isEmpty())
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
                if(bChecked)
                {
                    String sOtherCarrier = ''; 
                    for (HUMCaseTemplateHelper wrapperRec: mapSequenceResponses.get(iRowNumAddOI))
                    {
                        if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOICarrierName))
                        {
                            if(wrapperRec.lstPicklistValues!=null && !wrapperRec.lstPicklistValues.isEmpty() && String.isNotBlank(wrapperRec.responseRec.Response__c))
                            {
                                for(SelectOption selOpt : wrapperRec.lstPicklistValues)
                                {
                                    if(selOpt.getValue().equalsIgnoreCase(wrapperRec.responseRec.Response__c))
                                    {
                                        wrapperRec.sSelectedPicklistValue = wrapperRec.responseRec.Response__c;
                                        bOICarrierExist = true;
                                    }
                                }
                                if(!bOICarrierExist)
                                {
                                    wrapperRec.sSelectedPicklistValue = Label.HUMCOBOtherCarrier;
                                    sOtherCarrier = wrapperRec.responseRec.Response__c;
                                }
                            }
                        }
                        
                        if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOtherCarrierName) && !bOICarrierExist && String.isNotBlank(sOtherCarrier))
                        {
                            wrapperRec.responseRec.Response__c = sOtherCarrier;
                            wrapperRec.boolDisplayOtherCarrier = true;                                                                                                              
                        }                                               
                    }
                }
                else
                {
                    for (HUMCaseTemplateHelper wrapperRec: mapSequenceResponses.get(iRowNumAddOI))
                    {                     
                        if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOIShouldNeverListed) && wrapperRec.responseRec.boolean_response__c!=null)
                        {                       
                            wrapperRec.responseRec.boolean_response__c = false;
                        }                                                        
                        else if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOICarrierName) && String.isNotBlank(wrapperRec.sSelectedPicklistValue))
                        {                       
                            wrapperRec.sSelectedPicklistValue = '';
                        }                                                        
                        else if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOtherCarrierName) && String.isNotBlank(wrapperRec.responseRec.Response__c))
                        {
                            wrapperRec.responseRec.Response__c = '';
                            wrapperRec.boolDisplayOtherCarrier = false;                                                                                                             
                        }  
                        else if(wrapperRec.responseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_Cancellation_Date) && wrapperRec.responseRec.date_response__c!=null)
                        {
                            wrapperRec.responseRec.date_response__c=null;
                        }                                                                     
                    }
                }                                               
            }                        
        }
        catch (Exception e)
        {
            boolRenderAddOI = false;
            boolRenderRemoveOI = false;
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'prepareAddOITable');
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
                    if (mapSequenceResponses!=null && !mapSequenceResponses.isEmpty())
                    {
                        List < Tabular_Answer_Response__c > lstTabularResponses = new List < Tabular_Answer_Response__c > ();
                        integer iRowIndex;
                        set<Integer> setRowNums = new set<Integer>();
                        map<integer, list<HUMCaseTemplateHelper>> mapResponseCopy = new map<integer, list<HUMCaseTemplateHelper>>();
                        
                        /*Start of Logic to identify if both Add OI and Remove OI selected on the same row */
                        if(mapAddOIRowDisplayBool!=null && !mapAddOIRowDisplayBool.isEmpty()  && mapRemoveOIRowDisplayBool!=null && !mapRemoveOIRowDisplayBool.isEmpty())                        
                        {
                            for(Integer iRow : mapAddOIRowDisplayBool.keyset())
                            {
                                if(mapRemoveOIRowDisplayBool.containsKey(iRow) && mapRemoveOIRowDisplayBool.get(iRow) && mapAddOIRowDisplayBool.get(iRow))	setRowNums.add(iRow);   
                            }                                                      
                        }
                        /*End of Logic to identify if both Add OI and Remove OI selected on the same row */
                        
                        for (Integer row_num: mapSequenceResponses.keySet())
                        {                            
                            if(row_num <= GLOBAL_CONSTANT_HUM.CASE_PROVIDERSEARCH_TAXIDLENGTH)   //This is to save only the 9 rows that will be editable on the screen.
                            {                                                              
                                list<HUMCaseTemplateHelper> lstWrapperCopy = new list<HUMCaseTemplateHelper>();
                                for (HUMCaseTemplateHelper responseWrapper: mapSequenceResponses.get(row_num))
                                {                                
                                    if(Schema.sObjectType.Tabular_Answer_Response__c.fields.Action_Response__c.isCreateable()) responseWrapper.responseRec.Action_Response__c = associationRec.Id;                                     
                                    if(responseWrapper.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOILastVerifiedDate) && Schema.sObjectType.Tabular_Answer_Response__c.fields.Column_Title__c.isCreateable())  responseWrapper.responseRec.Column_Title__c = Label.HUMCOBOIMedicalVerifiedDate;                                                                                                                                                                                                        
                                    if(setRowNums!=null && !setRowNums.isEmpty() && setRowNums.contains(row_num))
                                    {                                        
                                        HUMCaseTemplateHelper oWrapCopy = createWrapperCopy(responseWrapper);
                                        if(oWrapCopy!=null) { lstWrapperCopy.add(oWrapCopy); }
                                        if(responseWrapper.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBAddOI) && Schema.sObjectType.Tabular_Answer_Response__c.fields.boolean_response__c.isCreateable()) {     responseWrapper.responseRec.boolean_response__c = false; }
                                        if(responseWrapper.responseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_New_Effective_Date) && Schema.sObjectType.Tabular_Answer_Response__c.fields.date_response__c.isCreateable()) {     responseWrapper.responseRec.date_response__c = null; }
                                        
                                    }                               
									else if(mapAddOIRowDisplayBool.containsKey(row_num) && mapAddOIRowDisplayBool.get(row_num))	
									{
										/* If only AddOI is seleted on the row, then copy the values from the new Add OI specific variables to existing variables. */                                       
										if(responseWrapper.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOICarrierName) && String.isNotBlank(responseWrapper.sSelectedPicklistValueAddOI)) { responseWrapper.sSelectedPicklistValue = responseWrapper.sSelectedPicklistValueAddOI; }
                                        if(responseWrapper.responseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOtherCarrierName) && String.isNotBlank(responseWrapper.sOtherCarrierNameAddOI) && Schema.sObjectType.Tabular_Answer_Response__c.fields.Response__c.isCreateable()) { responseWrapper.responseRec.Response__c = responseWrapper.sOtherCarrierNameAddOI; }
									}                                    
                                    if( responseWrapper.sDataType != null)
                                    {                                        										
										responseWrapper.responseRec.response__c = assignResponsesForTable(responseWrapper);
                                    }                                
                                    lstTabularResponses.add(responseWrapper.responseRec);
                                    iRowIndex = row_num;
                                }
                                if(lstWrapperCopy!=null && !lstWrapperCopy.isEmpty()) { mapResponseCopy.put(row_num, lstWrapperCopy); }
                            }                           
                        }
                        /* Start of logic to add extra ADD OI row entry for ADD OI and Remove OI done on the same row */
                        if(mapResponseCopy!=null && !mapResponseCopy.isEmpty())
                        {
                            for(Integer iRowNum : mapResponseCopy.keySet())
                            {
                                iRowIndex++;
                                for (HUMCaseTemplateHelper responseWrapper: mapResponseCopy.get(iRowNum))
                                {                                                                                                                                                                                                                                                                                                                                                                                 
                                    if( responseWrapper.sDataType != null)
                                    {
                                        responseWrapper.responseRec.response__c = assignResponsesForTable(responseWrapper);
                                    }                              
                                    if(Schema.sObjectType.Tabular_Answer_Response__c.fields.Row_Number__c.isCreateable())  responseWrapper.responseRec.Row_Number__c = iRowIndex;  
                                    lstTabularResponses.add(responseWrapper.responseRec);                                   
                                }   
                            }
                        }
                        /* End of logic to add extra ADD OI row entry for ADD OI and Remove OI done on the same row */
                         
                        /*Start of logic to save the Existing OI Information*/                                                                    
                        if(lstExistingOIQuestions!=null && !lstExistingOIQuestions.isEmpty() && mapResponseWrapperMBE!=null && !mapResponseWrapperMBE.isEmpty())
                        {                            
                            Tabular_Answer_Response__c oTabAns = new Tabular_Answer_Response__c();
                            iRowIndex++; //start from the next row number
                            for (String key: mapResponseWrapperMBE.keyset())
                            {            
                                for(HUMPersonServiceResponseHelper oCOBDetails : mapResponseWrapperMBE.get(key))
                                {
                                    for (Tabular_Question__c tabularquestion: lstExistingOIQuestions)
                                    {                                        
                                        oTabAns = new Tabular_Answer_Response__c();
                                        setResponseValue(tabularquestion.column_title__c, oCOBDetails, oTabAns );
                                        oTabAns.Column_Title__c = tabularquestion.column_title__c;
										if(Schema.sObjectType.Tabular_Answer_Response__c.fields.Grid_Question__c.isCreateable()) oTabAns.Grid_Question__c = tabularQuestion.Id;
                                        oTabAns.Action_Response__c = associationRec.Id;
                                        oTabAns.Row_Number__c = iRowIndex;                                      
                                        lstTabularResponses.add(oTabAns);
                                    }
                                    iRowIndex++;
                                }
                            }
                        }
                        /*End of logic to save the Existing OI Information*/
                        
                        if (lstTabularResponses!=null && !lstTabularResponses.isEmpty() && boolRenderMainTable){
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
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'saveAction');
        }
        return null;
    }
    
    /*
    * Method Name : createWrapperCopy
    * Description : This method creates the copy of the HUMCaseTemplateHelper wrapper object
    *               for adding extra row for both Add and Remove OI selected on the same row.
    * Return type : HUMCaseTemplateHelper
    * Paramater   : HUMCaseTemplateHelper
    */
    @TestVisible
    private HUMCaseTemplateHelper createWrapperCopy(HUMCaseTemplateHelper orignalWrapper)
    {
        HUMCaseTemplateHelper oWrapperCopy = new HUMCaseTemplateHelper();
        if(orignalWrapper!=null)
        {
            oWrapperCopy.lstPicklistValues = orignalWrapper.lstPicklistValues;
            oWrapperCopy.sSelectedPicklistValue = orignalWrapper.sSelectedPicklistValue;
            oWrapperCopy.sSelectedPicklistValueAddOI = orignalWrapper.sSelectedPicklistValueAddOI;
            oWrapperCopy.sOtherCarrierNameAddOI = orignalWrapper.sOtherCarrierNameAddOI;
            oWrapperCopy.boolCOBTableDisplay = orignalWrapper.boolCOBTableDisplay;
            oWrapperCopy.boolAddOITableDisplay = orignalWrapper.boolAddOITableDisplay;
            oWrapperCopy.boolRemoveOITableDisplay = orignalWrapper.boolRemoveOITableDisplay;
            oWrapperCopy.boolDisplayCancellationDate = orignalWrapper.boolDisplayCancellationDate;
            oWrapperCopy.boolDisplayOtherCarrier = orignalWrapper.boolDisplayOtherCarrier;
            oWrapperCopy.boolDisplayOtherCarrierAddOI = orignalWrapper.boolDisplayOtherCarrierAddOI;
            oWrapperCopy.boolDisplayEmptyCell = orignalWrapper.boolDisplayEmptyCell;
            oWrapperCopy.sDataType = orignalWrapper.sDataType;
            if(orignalWrapper.responseRec!=null)
            {
                String sResponseRec = JSON.serialize(orignalWrapper.responseRec);
                Tabular_Answer_Response__C oResponseRec = new Tabular_Answer_Response__c();
                oResponseRec = (Tabular_Answer_Response__C)System.JSON.deserialize(sResponseRec, Tabular_Answer_Response__c.class);
                if(oResponseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOIShouldNeverListed)) {      oResponseRec.boolean_Response__c = false; }
                if(oResponseRec.Column_Title__c.equalsIgnoreCase(Label.Archival_Cancellation_Date)) {     oResponseRec.date_response__c = null; }
                if(oResponseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOICarrierName)) { oWrapperCopy.sSelectedPicklistValue = orignalWrapper.sSelectedPicklistValueAddOI; }
                if(oResponseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBOtherCarrierName)) { oResponseRec.Response__c = orignalWrapper.sOtherCarrierNameAddOI; }
                if(oResponseRec.Column_Title__c.equalsIgnoreCase(Label.HUMCOBRemoveOI)) { oResponseRec.boolean_Response__c = false; }
                oWrapperCopy.responseRec = oResponseRec;
            }
        }       
        return oWrapperCopy;
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
                if(responseWrapper.boolAddOITableDisplay && mapAddOIRowDisplayBool.get(row_num))
                {
                    if(responseWrapper.sDataType == Label.HUMCOBDropDownType && responseWrapper.sSelectedPicklistValueAddOI == Label.HUMCOBNoneSelectOption)  lstBlankResponses.add(Label.HUMAddOI+row_num+' :'+responseWrapper.responseRec.Column_Title__c);
                    if(responseWrapper.sDataType == Label.HUMCOBDateQuestionType && responseWrapper.responseRec.Date_Response__c == null)  lstBlankResponses.add(Label.HUMAddOI+row_num+' :'+responseWrapper.responseRec.Column_Title__c);        
                    if(responseWrapper.responseRec.Column_Title__c == Label.HUMCOBOtherCarrierName && responseWrapper.sDataType == Label.HUMCOBTextType && String.isBlank(responseWrapper.sOtherCarrierNameAddOI) && responseWrapper.boolDisplayOtherCarrierAddOI )  lstBlankResponses.add(Label.HUMAddOI + row_num+' :'+responseWrapper.responseRec.Column_Title__c);                    
                }
                                        
                if(responseWrapper.boolRemoveOITableDisplay && mapRemoveOIRowDisplayBool.get(row_num))
                {
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
    * Method Name : responseMethodMBEPlus
    * Description : This method is used to make Continuation Callout to MBEPlus service to get the COB details
    * Return type : continuation
    * Paramater   : None
    */
    public continuation responseMethodMBEPlus()
    {                
        try
        {
            mapRequestLabels = new map<string, string>();   // map to contain the continuation callout label
			Boolean bAddOICOBNode = HUMUtilityHelper.isCRMFunctionalityON('1700807');
            if(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty() && setRemainingIDs!=null && !setRemainingIDs.isEmpty())
            {                      
                Continuation con = new Continuation(GLOBAL_CONSTANT_HUM.VOB_ServiceTimeOut_HUM);
                con.continuationMethod = GLOBAL_CONSTANT_HUM.COBCALLBACKMETHOD;
                Integer iRequestCount = GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ZERO_HUM; 
                for(String sEnterpriseID : setRemainingIDs)
                {                
                    if(iRequestCount < GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM)   //Each continuation can contain only max of 3 callouts
                    {
                        MBEPlusOIRequest_DTO_HUM oReq = new MBEPlusOIRequest_DTO_HUM();
                        oReq = HUMProcessUtilityHelper.createMBEPlusRequest(sEnterpriseID,bAddOICOBNode);
                        String sBodyJSON;
                        String strRequestLabel;
                        if(oReq!=null)
                        {
                            sBodyJSON = JSON.serialize(oReq);                                                   
                        }                    
                        if(String.isNotBlank(sBodyJSON))
                        {
                            HttpRequest mbeRequest = HUMProcessUtilityHelper.builtHttpRequest();                        
                            if(mbeRequest!=null)
                            {
                                mbeRequest.setBody(sBodyJSON);
                                strRequestLabel = con.addHttpRequest(mbeRequest);
                                mapRequestLabels.put(strRequestLabel, sEnterpriseID);
                                iRequestCount++;
                                setRemainingIDs.remove(sEnterpriseID);                       
                            }                        
                        }
                    }
                }
                return con;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'responseMethodMBEPlus');
        } 
        return null;                
    }
    
    /*
    * Method Name : mbePlusCallBack
    * Description : The callback method to process the MBEPlus Service response and chaining any futher continuation callouts
    * Return type : Object
    * Paramater   : None
    */
    public Object mbePlusCallBack() 
    {        
        list<HUMPersonServiceResponseHelper> lstCOBDetails = new list<HUMPersonServiceResponseHelper>();
        try
        {
            if(mapRequestLabels!= null && !mapRequestLabels.isEmpty())
            {
                for(String sRequestLabel : mapRequestLabels.keySet())
                {
                    lstCOBDetails = new list<HUMPersonServiceResponseHelper>();
                    HttpResponse response = Continuation.getResponse(sRequestLabel);                
                    String sResponseBody = response.getBody();                
                    if (String.isNotBlank(sResponseBody) && sResponseBody.indexOf(HUMConstants.VOB_PBE_FAULT_CODE) == -1 )    //if no error is returned in the response
                    {
                        MBEPlusOIResponse_DTO_HUM outResponse = (MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(sResponseBody, MBEPlusOIResponse_DTO_HUM.class);                                                               
                        if(outResponse != null && outResponse.GetMemberResponse != null && outResponse.GetMemberResponse.Body != null && outResponse.GetMemberResponse.Body.Member != null 
                           && String.isNotBlank(outResponse.GetMemberResponse.Body.Member.MasterId) && mapPolicyMembers.containsKey(outResponse.GetMemberResponse.Body.Member.MasterId))    
                        {
                            //call the parse method.
                            Policy_Member__c oPolicyMember = mapPolicyMembers.get(outResponse.GetMemberResponse.Body.Member.MasterId);
                            list<Policy_Plan_Details__c> lstPlans = new list<Policy_Plan_Details__c>();
                            if(mapPolicyPlanDetails!=null && !mapPolicyPlanDetails.isEmpty() && mapPolicyPlanDetails.containsKey(oPolicyMember.Policy__c)) lstPlans = mapPolicyPlanDetails.get(oPolicyMember.Policy__c);                                 
                            if(HUMUtilityHelper.isCRMFunctionalityON('1700807'))
                            {                                  
	                            list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstOICOBFromService = HUMProcessUtilityHelper.parseMBERespForOICOBDetails(outResponse, oPolicyMember, lstPlans);
	                            if(lstOICOBFromService!=null && !lstOICOBFromService.isEmpty())
	                            {
	                            	lstCOBDetails = HUMProcessUtilityHelper.prepareOICOBDetailsDTO(oPolicyMember, lstOICOBFromService);	
	                            }
	                            else
	                            {
	                            	lstCOBDetails = HUMProcessUtilityHelper.prepareEmptyCOBDetails(oPolicyMember);                            	
	                            }
                            }
                            else
                            {                                  
	                            list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBFromService = HUMProcessUtilityHelper.parseMBERespForCOBDetails(outResponse, oPolicyMember, lstPlans);
	                            if(lstCOBFromService!=null && !lstCOBFromService.isEmpty())
	                            {
	                            	lstCOBDetails = HUMProcessUtilityHelper.prepareCOBDetailsDTO(oPolicyMember, lstCOBFromService);	
	                            }
	                            else
	                            {
	                            	lstCOBDetails = HUMProcessUtilityHelper.prepareEmptyCOBDetails(oPolicyMember);                            	
	                            }                       
                            }                            
                            
                            if(lstCOBDetails!=null && !lstCOBDetails.isEmpty())                       
                            {                               
                                mapResponseWrapperMBE.put(outResponse.GetMemberResponse.Body.Member.MasterId, lstCOBDetails);                                                           
                            }
                        }
                    }
                    else
                    {
                        string sMemberKey = mapRequestLabels.get(sRequestLabel);
                        if(String.isNotBlank(sMemberKey))
                        {   
                            lstCOBDetails = HUMProcessUtilityHelper.prepareEmptyCOBDetails(mapPolicyMembers.get(sMemberKey));
                            if(lstCOBDetails!=null && !lstCOBDetails.isEmpty())
                            {
								mapResponseWrapperMBE.put(sMemberKey, lstCOBDetails);
                            }
                        }
                    }                                   
                }                                        
            }
            if(setRemainingIDs!=null && !setRemainingIDs.isEmpty())
            {           
                if(iChainingCount < GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_THREE_HUM)  //only 3 levels of Chaining is allowed in Continuation callouts
                {
                    iChainingCount++;                                
                    return responseMethodMBEPlus();   
                }            
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'mbePlusCallBack');
        }            
        return null;
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
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'cancel');
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
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'getMapSequenceResponsesSize');
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
            HUMExceptionHelper.logErrors(e, 'HUMCOBProcessController_V1', 'getRowDisplayTableMapSize');
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
        mapResponseWrapperMBE = new map<String, list<HUMPersonServiceResponseHelper>> ();
        rowdispTableMap = new Map < Integer, List < Tabular_Answer_Response__c >> ();
        boolOIResponse = false;
        boolRenderAddOI = false;
        boolRenderRemoveOI = false;
        boolRenderMainTable = false;
        boolIsServiceRequired = true;
        bShowExistingOITable = false;
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
        bFamilySizeMessage = false;
        bAddOIMessage = false;
		bDetailAddOIInfo = false;
    }
    
}