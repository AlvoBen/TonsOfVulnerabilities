/*****************************************************************************************************
 Apex Class Name  : METMileStoneCustomLookup_C_HUM
 Version   : 1.0 
 Created Date  : 02/01/2017
 Function  : Holds the logic to display lookup page on click of lookup icon from Milestone Edit/New page.
 Test Class  :  METMileStoneCustomLookup_T_HUM
 
 Modification Log :
* Developer                 Code Review         Date                   Description
* -----------------------------------------------------------------------------------------------------                 
* Prasanthi Kandula           55297           02/01/2017              Original Version - REQ-290027 MileStone Custom Lookup Page
* Manoj Chaurasia             202594          03/29/2018              REQ - 354470 Ability to display TAS combination in CRM
* Prasuna Pattabhi                            08/11/2021              US 2567549 sf-main/src/classes/METMileStoneCustomLookup_C_HUM.cls
* Prasuna Pattabhi                            09/07/2021              Defect Fix DF 3699
*******************************************************************************************************/
public with sharing class METMileStoneCustomLookup_C_HUM
{
    @testVisible private string sCaseSubType = '';
    @testVisible private string sTaskId = '';
    @testVisible private string sActionId = '';
    @testVisible private String sSortExp = GLOBAL_CONSTANT_HUM.SORTDATE_HUM;
    @testVisible private String sSortDirection = GLOBAL_CONSTANT_HUM.Sort_Expression_DESC_HUM;
    
    /*
    * Method Name : METMileStoneCustomLookup_C_HUM
    * Description : Constructor
    * Return type : NA
    * Parameter   : NA
    */
    public METMileStoneCustomLookup_C_HUM()
    {
        sCaseSubType = ApexPages.currentPage().getParameters().get('casesubtype');
        sTaskId = ApexPages.currentPage().getParameters().get('TaskId');
        sActionId = ApexPages.currentPage().getParameters().get('ActionId');
        searchString = System.currentPageReference().getParameters().get('searchStr');
        sObjectName = ApexPages.currentPage().getParameters().get('Obj');
    }
    
    public string searchString {get; set;}
    public String sObjectName {get;set;}
    public List<sObject> lstsObject {get; set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}
    FetchDataMileStone_D_HUM ofetchData = new FetchDataMileStone_D_HUM();
    
    /*
    * Method Name : hasNext
    * Description : This property is used to get/set hasNext.
    * Return type : Boolean 
    * Parameter   : NA
    */
    public Boolean hasNext 
    {
        get 
        {
            if(stdsetcon!=null)
            {
                return stdsetcon.getHasNext();
            }
            else 
            {
                return null;
            }
        }
        set;
    }
    
    /*
    * Method Name : hasPrevious 
    * Description : This property is used to get/set hasPrevious.
    * Return type : Boolean 
    * Parameter   : NA
    */
    public Boolean hasPrevious 
    {
        get 
        {
            if(stdsetcon!=null)
            {
                return stdsetcon.getHasPrevious();
            }
            else 
            {
                return null;
            }
        }
        set;
    }
    
    /*
    * Method Name : first
    * Description : This method returns the first page of records.
    * Return type : Void
    * Parameter   : NA
    */
    public void first()
    {
        stdsetcon.first();
    }
    
    /*
    * Method Name : next
    * Description : This method returns the next page of records.
    * Return type : Void
    * Parameter   : NA
    */  
    public void next() 
    {
        stdsetcon.next();
    }
    
    /*
    * Method Name : last
    * Description : This method returns the last page of records.
    * Return type : Void
    * Parameter   : NA
    */ 
    public void last() 
    {
        stdsetcon.last();
    }
    
    /*
    * Method Name : previous
    * Description : This method returns the previous page of records.
    * Return type : Void
    * Parameter   : NA
    */
    public void previous() 
    {
        stdsetcon.previous();
    }
    
    /*
    * Method Name : sSortExpression 
    * Description : This property is used to get/set sSortExpression.
    * Return type : String
    * Parameter   : NA
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
    * Method Name : getlstWrapperData
    * Description : This method Is used to get the list of Wrapper data.
    * Return type : List<MileStoneWrappper_DTO_HUM>
    * Parameter   : NA
    */
    public List<MileStoneWrappper_DTO_HUM> getlstWrapperData() 
    {
        List<MileStoneWrappper_DTO_HUM> lstObjWrapper = new List<MileStoneWrappper_DTO_HUM>();  
        for(sObject oObj : stdsetcon.getRecords())
        {
            MileStoneWrappper_DTO_HUM oWrapper = new MileStoneWrappper_DTO_HUM();
            oWrapper.sName = (String)oObj.get('Name');   
            oWrapper.sId = (String)oObj.get('Id'); 
            if(sObjectName.equalsIgnoreCase('Task'))  
                oWrapper.sCaseSubType = (String)oObj.get('Case_Subtype__c'); 
            lstObjWrapper.add(oWrapper);
        }
        return lstObjWrapper;
    }
   
    /*
    * Method Name : fetchDataForMileStone
    * Description : This method is used to fetch the data for Milestone depending on the object the loookup icon is clicked.
    * Return type : Void
    * Parameter   : None
    */
    public void fetchDataForMileStone()
    {
        try
        {
            if(String.isNotBlank(searchString) && searchString.contains('*'))
                searchString = searchString.removeEnd('*');

            String querySearchStr;
            if(String.isNotBlank(searchString) && searchString!=null) 
                querySearchStr=string.escapesingleQuotes(searchString);
            else
                querySearchStr =ESAPI.encoder().SFDC_HTMLENCODE(searchString);

            if(sObjectName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OBJECTNAME_TASK_HUM))
            {
                lstsObject = new List<sObject>();
                lstsObject  = ofetchData.queryTaskData(sCaseSubType,querySearchStr,sSortExpression,sSortDirection);
                setStandardSet(lstsObject);
            }
            if(sObjectName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OBJECTNAME_ACTION_HUM))
            {
                lstsObject = new List<sObject>();
                lstsObject = ofetchData.queryActionDataWithoutTask(querySearchStr,sSortExpression,sSortDirection);
                setStandardSet(lstsObject);
            }
            if(sObjectName.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.OBJECTNAME_SOURCE_HUM))
            {
                lstsObject = new List<sObject>();
                lstsObject = ofetchData.querySourceDataWithoutAction(querySearchStr,sSortExpression,sSortDirection);
                setStandardSet(lstsObject);
            }
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex,'METMileStoneCustomLookup_C_HUM','fetchDataForMileStone');
        }
    }
    
    /*
    * Method Name : setStandardSet
    * Description : This method is used to set the page size to 10
    * Return type : Void
    * Parameter   : List<sObject>
    */
    public void setStandardSet(List<sObject> lstsObject)
    {
          stdsetcon = new ApexPages.StandardSetController(lstsObject);
          if(stdsetcon != Null)    
              stdsetcon.setPageSize(GLOBAL_CONSTANT_HUM.CONSTANT_10_HUM);  
    }
}