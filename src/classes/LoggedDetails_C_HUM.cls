/*************************************************************************************************
Apex Class Name  : LoggedDetails_C_HUM
Version          : 1.0
Created Date     : 02/22/2016
Function         : This is controller class for visual force component 'LoggedDetails_CMP_HUM'.
                    This is used to display attachment log records on UI.
Test Class       : LoggedDetails_T_HUM
***************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
---------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    02/22/2016                 Original Version
* Santhi Mandava                                     04/05/2016                 Fixed defect 221550.0001. Modified line 21.
* Shreya Choodamani         90487                    04/28/2017                 REQ#: 311397 Updated the code to mimic the behavior for HP Supervisor And Specialist(Added Variable sPharmacyUser)
* Lakshmi Madduri                                    05/07/2021                 2235919 - Separate constant class for logging framework
****************************************************************************************************/

public with sharing class LoggedDetails_C_HUM
{
    @testVisible private String sSortExp = LOGGING_CONSTANT_HUM.STRING_CREATEDDATE;
    @testVisible private String sSortDirection = LOGGING_CONSTANT_HUM.STRING_DESC;
    @testVisible private List<Attachment_Log__c> lstFinalLogReocrds;
    
    public String sCaseId{get;set;}
    public ApexPages.StandardSetController stdsetcon {get;set;}
    
    public integer iConstantValue
    {
        get
        {
          iConstantValue = 100;
          return iConstantValue;
        }
        set;
    }
    
    public Boolean hasNext 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getHasNext();}
            else {return null;}
        }
        set;
    }
    
    //Boolean to check if there are more records before the present displaying records  
    public Boolean hasPrevious 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getHasPrevious();}
            else {return null;}
        }
        set;
    }
    
    //Page number of the current displaying records  
    public Integer pageNumber 
    {
        get 
        {
            if(stdsetcon!=null){return stdsetcon.getPageNumber();}
            else{ return null;}
        }
        set;
    }

    Public String sPharmacyUser
   	{
   		get
   		{
   			sPharmacyUser = Pharmacy_H_HUM.isPharmacyUser();
   			Return sPharmacyUser;
   		}
   		set;
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
     * Method Name : sSortExpression
     * Description : This is uesd to get/set sort expression.
     * Return type : Void
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
                sSortDirection = (sSortDirection == LOGGING_CONSTANT_HUM.STRING_DESC) ? LOGGING_CONSTANT_HUM.STRING_ASC : LOGGING_CONSTANT_HUM.STRING_DESC;
            }
            else{sSortDirection = LOGGING_CONSTANT_HUM.STRING_DESC;}
            sSortExp = value;
        }
    }
    
    /*
     * Method Name : lstLogReocrds
     * Description : This is uesd to get/set log records.
     * Return type : Void
     * Parameter   : NA
     */
    public List<Attachment_Log__c> lstLogReocrds 
    {
        get
        {
            if (stdsetcon != null)
            {
                return (List <Attachment_Log__c> ) stdsetcon.getRecords();
            }
            else
            {
               retrieveLogDetails();
               return (List <Attachment_Log__c> ) stdsetcon.getRecords();
            }
        }
        set;
    }
    
    /*
    * Method Name : retrieveLogDetails
    * Description : This method is used to retrieve attachment log custom object records.
    * Return type : void
    * Parameter   : NA
    */
    public void retrieveLogDetails()
    {
        lstFinalLogReocrds = LoggingDataLogic_D_HUM.getLogDetails(sCaseId,sSortExpression, sSortDirection);
        stdsetcon = new ApexPages.StandardSetController(lstFinalLogReocrds );
        if(stdsetcon != Null)    stdsetcon.setPageSize(LOGGING_CONSTANT_HUM.INTEGER_5);
    }
    
    /*
    * Method Name : deleteLogAttachments
    * Description : This method is used to delete attachment log record.
    * Return type : void
    * Parameter   : NA
    */
    public void deleteLogAttachments()
    {
       string sAttchId =  ApexPages.CurrentPage().getParameters().get('attchmentId').escapeHTML4();
       If(!String.IsBlank(sAttchId))
       {
           LoggingDataLogic_D_HUM.deleteLogDetails(sAttchId);
       }
    }
    
     /*
     * Method Name : getSortDirection
     * Description : This method is used to get the sortdirection.
     * Return type : String
     * Parameter   : NA
     */
    public String getSortDirection()
    {
        if (String.isBlank(sSortDirection))
        {
            return LOGGING_CONSTANT_HUM.STRING_DESC;
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
     * Method Name : sortResultPage
     * Description : This method is used to sort the case records.
     * Return type : Void
     * Parameter   : NA
     */
    public void sortResultPage()
    {
        try
        {
            lstFinalLogReocrds = LoggingDataLogic_D_HUM.getLogDetails(sCaseId, sSortExpression, sSortDirection);
            lstLogReocrds = lstFinalLogReocrds ;
            stdsetcon = new ApexPages.StandardSetController(lstFinalLogReocrds);
            if(stdsetcon != Null)    stdsetcon.setPageSize(LOGGING_CONSTANT_HUM.INTEGER_5);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'LoggedDetails_C_HUM','sortResultPage');
        }
    }
}