/*****************************************************************************************************
 Apex Class Name  : METMileStoneDetail_C_HUM
 Version   : 1.0 
 Created Date  : 12/20/2016
 Function  : Holds the logic to display MileStone New/Edit page
 Test Class  :  METMileStoneDetail_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                   Description
* -----------------------------------------------------------------------------------------------------                 
* Prasanthi Kandula          55297             12/20/2016              Original Version - REQ - 290027 MileStone Edit/New page
* Manoj Chaurasia            202594            03/29/2018              REQ - 354470 Ability to display TAS combination in CRM
* Manoj Chaurasia            214472            04/13/2018              REQ - 358347: Defect#354614.0001- Added Constants for Default Milestones ID as '9999' 
* Pooja Kumbhar								   02/24/2021			   REQ - 1990246 - InputText Ignoring FLS Remediation
* Pooja Kumbhar								   05/05/2021			   REQ - 2254207 - case management checkkmarx remediation
* Mithra Bharadwaj                             02/09/2021              US#1522825 SonarQube Changes
*******************************************************************************************************/

public with sharing class METMileStoneDetail_C_HUM
{
    @testVisible private String sSortExp = GLOBAL_CONSTANT_HUM.SORTDATE_HUM;
    @testVisible private String sSortDirection = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
    
    /**
    *  Constructor
    *  @name METMileStoneDetail_C_HUM
    *  @param ApexPages.StandardController
    *  @return NA
    *  @throws NA
    */
    public METMileStoneDetail_C_HUM(ApexPages.StandardController controller)
    {
        sMileStoneID = ApexPages.currentPage().getParameters().get('id');
        oMileStone = (MET_Milestone__c)controller.getRecord();
        if(String.isNotBlank(sMileStoneID))
        {
            oMileStone = oFetchMileStoneData.queryMileStoneData(string.escapeSingleQuotes(sMileStoneID));
            if(Schema.SObjectType.MET_Milestone__c.fields.Task__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Task__c.isUpdateable()) sTaskName = oMileStone.Task__r.Name; 
            if(Schema.SObjectType.MET_Milestone__c.fields.Action__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Action__c.isUpdateable()) sActionName = oMileStone.Action__r.Name; 
            if(Schema.SObjectType.MET_Milestone__c.fields.Source_Name__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Source_Name__c.isUpdateable()) sSourceName = oMileStone.Source_Name__r.Name; 
            if(Schema.SObjectType.MET_Milestone__c.fields.Case_Type__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Case_Type__c.isUpdateable()) sCaseTypeName = oMileStone.Case_Type__c; 
            if(Schema.SObjectType.MET_Milestone__c.fields.Case_Subtype__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Case_Subtype__c.isUpdateable()) sCaseSubtypeName = oMileStone.Case_Subtype__c; 
           
        }
        else
        {
            oMileStone = new MET_Milestone__c();
			oMileStone.Name = GLOBAL_CONSTANT_HUM.METMILESTONE_DEFAULT_ID;
        }
    }
    
    public MET_Milestone__c oMileStone{get; set;}
    public string sMileStoneID {get;set;}
    public string sTaskName {get; set;}
    public string sActionName {get; set;}
    public string sSourceName {get;set;}
    public string sCaseTypeName {get; set;}
    public string sCaseSubtypeName {get;set;}
    public ID iTaskId{get;set;}
    public ID iActionId{get;set;}
    public ID iSourceId{get;set;}
    FetchDataMileStone_D_HUM oFetchMileStoneData = new FetchDataMileStone_D_HUM();
   
   
   /*
     * Method Name : getTaskId
     * Description : This method is used to get/set the Task ID
     * Return type : pageReference 
     
     */
       public pageReference getTaskId()
       {
           if(Schema.SObjectType.MET_Milestone__c.fields.Task__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Task__c.isUpdateable()) oMileStone.Task__c = iTaskId; 
           return null;
       }
       
       /*
     * Method Name : getActionId
     * Description : This method is used to get/set the Action ID
     * Return type : pageReference 
     
     */
       public pageReference getActionId()
       {
           if(Schema.SObjectType.MET_Milestone__c.fields.Action__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Action__c.isUpdateable()) oMileStone.Action__c = iActionId; 
           return null;
       }
       
       /*
     * Method Name : getSourceId
     * Description : This method is used to get/set the Source ID
     * Return type : pageReference 
     
     */
       public pageReference getSourceId()
       {
           if(Schema.SObjectType.MET_Milestone__c.fields.Source_Name__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Source_Name__c.isUpdateable()) oMileStone.Source_Name__c = iSourceId; 
           return null;
       }
       
    
     /*
     * Method Name : sSortExpression 
     * Description : This method is used to get/set the Sort Expression
     * Return type : Void
     * Parameter   : String
     */
    public String sSortExpression 
    {
        get 
        {
            return sSortExp;
        }
        set 
        {
            if (value == sSortExp)
            {
                sSortDirection = (sSortDirection == GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM) ? GLOBAL_CONSTANT_HUM.Sort_Expression_ASC_HUM : GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
            }
            else
            {
                sSortDirection = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
            }
            sSortExp = value;
        }
    }
    
    /*
     * Method Name : getSortDirection
     * Description : This method is used to get the Sort direction.
     * Return type : Void
     * Parameter   : String
     */
    public String getSortDirection()
    {
        if (String.isBlank(sSortDirection))
        {
            return GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
        }
        else
        {
            return sSortDirection;
        }
    }
    
    /*
     * Method Name : setSortDirection
     * Description : This method is used to set the Sort direction.
     * Return type : Void
     * Parameter   : String
     */
    public void setSortDirection(String value)
    {
        sSortDirection = value;
    }
    
    /*
    * Method Name : Save
    * Description : This method is called on click of Save button on MileStone New/Edit page.
    * Return type : NA
    * Parameter   : NA
    */
    public PageReference Save() 
    {
        try 
        {
            MET_Milestone__c oMetMileStone = new MET_Milestone__c();
            oMetMileStone = insertMileStone();
            if(oMetMileStone != null && oMetMileStone.Id != null)
            {
                Pagereference pg = new Pagereference(GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + oMetMileStone.Id);
                pg.setRedirect(true);   
                return pg;
            }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'METMileStoneDetail_C_HUM', 'Save');
            return null;
        }
    }
    
    /*
    * Method Name : insertMileStone
    * Description : Generic method to upsert the Milestone record. This method is called on both Save, Save & New buttons
    * Return type : MET_Milestone__c
    * Parameter   : NA
    */
    public MET_Milestone__c insertMileStone()
    {
        Boolean bTaskFlag = true;
        Boolean bActionFlag = true;
        Boolean bSourceFlag = true;
        Boolean bReturnFlag = true;
        Boolean bCaseTypeFlag = true;
        Boolean bCaseSubtypeFlag = true;
        Boolean bVerifyMileStoneId = pattern.matches(GLOBAL_CONSTANT_HUM.MILESTONEID_VALIDATION_HUM,oMileStone.Name);
        if(!bVerifyMileStoneId)
        {
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VERIFY_MILESTONEID_HUM);
            ApexPages.addMessage(myMsg);
            bReturnFlag = false;
        }
        if(String.isBlank(sCaseTypeName) && String.isBlank(oMileStone.Case_Type__c)){
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATECASETYPE_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bCaseTypeFlag = false;
        }
        if(String.isBlank(sCaseSubtypeName) && String.isBlank(oMileStone.Case_Subtype__c)){
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATECASESUBTYPE_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bCaseSubtypeFlag = false;
        }
        if(String.isBlank(sTaskName)){
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATETASKNAME_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bTaskFlag = false;
        }
        else if(String.isNotBlank(sTaskName)&& String.isBlank(oMileStone.Task__c))
        {
            bTaskFlag =  callTaskData();
        }
        
        if(String.isBlank(sActionName)){
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATEACTIONNAME_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bActionFlag = false;
        }
        else if(String.isNotBlank(sActionName) && String.isBlank(oMileStone.Action__c))
        {
            bActionFlag = callActionData();
        }
        
        if(String.isBlank(sSourceName)){
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATESOURCENAME_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bSourceFlag = false;
        }
        else if(String.isNotEmpty(sSourceName) && String.isBlank(oMileStone.Source_Name__c))
        {
            bSourceFlag = callSourceData();
        }
        
        
        if(!(bReturnFlag && bCaseTypeFlag && bCaseSubtypeFlag && bTaskFlag && bActionFlag && bSourceFlag ))
        {
            return null;
        }
        else
        {
            try{
                upsert oMileStone;
                return oMileStone;
            }
            catch(Exception ex)
            {
                return null;
            }
        }
    }
    
    /*
    * Method Name : callTaskData
    * Description : This method validates the Task Name entered on Milestone page. If the user manually enters an invalid task name an error will be displayed.
    * Return type : boolean
    * Parameter   : NA
    */
    private boolean callTaskData()
    {
        Boolean bReturnFlag;
        List<MET_Task__c> lstTaskData = new List<MET_Task__c>();
        lstTaskData = oFetchMileStoneData.queryTaskData(oMileStone.Case_SubType__c, sTaskName,sSortExpression,sSortDirection);
        if(lstTaskData.isEmpty() || lstTaskData == null )
        {
            sTaskName = '';
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATETASKNAME_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bReturnFlag = false;
        }
        else
        {
            if(lstTaskData.size()==GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM)
            {
                if(Schema.SObjectType.MET_Milestone__c.fields.Task__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Task__c.isUpdateable()) oMileStone.Task__c = lstTaskData[0].id; 
                sTaskName = lstTaskData[0].Name;
                bReturnFlag = true;
            }
            else
            {
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.DUPLICATETASKS_MILESTONE_HUM);
                ApexPages.addMessage(myMsg);
                bReturnFlag = false;
            }
        } 
        return bReturnFlag;
    }
  
    /*
    * Method Name : callActionData
    * Description : If the action name is not blank, this method queries the Action records from MET Action object
    * Return type : boolean
    * Parameter   : NA
    */  
  private boolean callActionData()
    {
        List<sObject> lstsObject = new List<sObject>();
        if(String.isNotBlank(sActionName))
        {
          List<MET_Action__c > lstAction = new List<MET_Action__c>();
          lstsObject = new List<sObject>();
          lstsObject = oFetchMileStoneData.queryActionDataWithoutTask(sActionName,sSortExpression,sSortDirection);
        }
        return validateMETAction(lstsObject);
    }
    
    /*
    * Method Name : validateMETAction
    * Description : This method validates the Action Name entered on Milestone page. If the user manually enters an invalid action name an error will be displayed.
    * Return type : boolean
    * Parameter   : List<sObject> lstsObject
    */
    private boolean validateMETAction(List<sObject> lstsObject)
    {
        Boolean bActionReturnFlag;
        if(lstsObject.isEmpty() || lstsObject==null)
            {
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATEACTIONNAME_MILESTONE_HUM);
                ApexPages.addMessage(myMsg);
                bActionReturnFlag = false;
            }
        else
        {
            if(lstsObject .size()==GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM)
            {
                if(Schema.SObjectType.MET_Milestone__c.fields.Action__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Action__c.isUpdateable()) oMileStone.Action__c = lstsObject[0].Id; 
                    sActionName = String.ValueOf(lstsObject [0].get('Name'));
                bActionReturnFlag = true;
            }
            else
            {
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.DUPLICATEACTIONS_MILESTONE_HUM);
                ApexPages.addMessage(myMsg);
                bActionReturnFlag = false; 
            }
        }  
        return bActionReturnFlag;
    }
    
    /*
    * Method Name : callSourceData
    * Description : If the source name is not blank, this method queries the Source records from MET Source object
    * Return type : boolean
    * Parameter   : NA
    */ 
    private boolean callSourceData()
    {
        List<sObject> lstsObject = new List<sObject>();
        if(String.isNotBlank(sSourceName))
        {
            lstsObject = new List<sObject>();
            lstsObject = oFetchMileStoneData.querySourceDataWithoutAction(sSourceName,sSortExpression,sSortDirection);
        }
        return validateMETSource(lstsObject);
    }
    
    /*
    * Method Name : validateMETSource
    * Description : This method validates the Source Name entered on Milestone page. If the user manually enters an invalid source name an error will be displayed.
    * Return type : boolean
    * Parameter   : List<sObject> lstsObject
    */
    private boolean validateMETSource(List<sObject> lstsObject)
    {
       Boolean bSourceReturnFlag;
        if(lstsObject.isEmpty() || lstsObject==NULL)
        {
            ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.VALIDATESOURCENAME_MILESTONE_HUM);
            ApexPages.addMessage(myMsg);
            bSourceReturnFlag= false;
        }
        else
        {
            if(lstsObject .size()==GLOBAL_CONSTANT_HUM.NUMBER_CONSTANT_ONE_HUM)
            {
                if(Schema.SObjectType.MET_Milestone__c.fields.Source_Name__c.isCreateable() && Schema.SObjectType.MET_Milestone__c.fields.Source_Name__c.isUpdateable()) oMileStone.Source_Name__c = lstsObject [0].id; 
                sSourceName = String.valueOf(lstsObject [0].get('Name'));
                bSourceReturnFlag= true;
            }
            else
            {
                ApexPages.Message myMsg = new ApexPages.Message(ApexPages.Severity.Error,Label.DUPLICATESOURCES_MILESTONE_HUM);
                ApexPages.addMessage(myMsg);
                bSourceReturnFlag= false; 
            }
        }  
        return bSourceReturnFlag;
    }
    
    /*
    * Method Name : Cancel
    * Description : This method is called when user clicks Cancel button on MileStone New/Edit page.
    * Return type : NA
    * Parameter   : NA
    */
    public PageReference Cancel() 
    {
        Pagereference oPgRef;
        if(String.isNotBlank(sMileStoneID)) 
            oPgRef = new Pagereference('/'+string.escapeSingleQuotes(sMileStoneID));
        else
        {
            Schema.DescribeSObjectResult oObjResult = MET_Milestone__c.sObjectType.getDescribe();
            String sKeyPrefix = oObjResult .getKeyPrefix();
            oPgRef = new Pagereference('/'+sKeyPrefix);
        }
        oPgRef.setRedirect(true);
        return oPgRef;
    }
    
    /*
    * Method Name : SaveAndNew
    * Description : This method saves the record and open a new Milestone edit page
    * Return type : NA
    * Parameter   : NA
    */
    public PageReference SaveAndNew() 
    {
        try
        {
            MET_Milestone__c oMetMileStone = new MET_Milestone__c();
            oMetMileStone = insertMileStone();
            if(oMetMileStone != null && String.isNotBlank(oMileStone.id))
            {
                Pagereference oPgRef = new Pagereference(GLOBAL_CONSTANT_HUM.MILESTONEDETAIL_VFPAGE_HUM);
                oPgRef.setRedirect(true);
                return oPgRef;
            }
            return null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'METMileStoneDetail_C_HUM', 'SaveAndNew');
            return null;
        }
    }
}