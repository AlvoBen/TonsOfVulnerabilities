/**********************************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryController
Version          : 1.0
Created Date     : May 9 2014
Function         : Here all the methods for the integration of Inquiry History will be stored
Test Class       : HUMInquiryHistoryControllerTest
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Srikant Joshi             17175                      05/09/2014                   Original Version
 * Srikant Joshi             17175                      07/09/2014                   Changed input request
 * Srikant Joshi             17175                      07/21/2014                   Added logic for family members
 * Shruthi Karanth           17783                      09/09/2014                   Refactored sorting functinality
 * Vishal Verma              18313                      10/06/2014                   Moved reusable codes to HUMInquiryHistoryHelper
 * Vishal Verma              18600                      10/08/2014                   Added null checks before calling helper method
 * Vishal Verma              18888                      10/29/2014                   Added a check before displaying no records error message on page
 * Vishal Verma              18994                      11/05/2014                   Calling the filter date method on click of Retrieve Legacy Contact History button
 * Vishal Verma              20531                      01/15/2015                   Defect fix: 168370 , When clean filter button is cliked then only those leagcy records 
                                                                                     are displayed which are created between last 90 days
 * Vishal Verma              22273                      04/03/2015                   Req  - 166509 -1.Added Date , Reference Id filters as mandatory parameters before invoking the webservice .
                                                                                                    2.Changed Custom labels , Constants to Custom Settings
                                                                                                    3.Refactored code with a more scalable approach and removed unused code.                                                              
 * Sruthi Adivikolanu        28156                      11/20/2015                   Req - 198629 - HUMInquiryHistoryController constructor has been changed to get Account details from policy when it is viewing from Policy
 * Sharan Shanmugam          34890                      08/09/2016                   Added sSelectList variable to make it compatible for Continuation implementations
 * Avinash Choubey           43368                      10/14/2016                  added null check on Constructor to fix Defect# 242220.0001  
 * Prudhvi Pamarthi          182139                     02/15/2018                   REQ - 350422 & REQ -  CA 7110510 & CA Ticket #7205895
 * Prudhvi Pamarthi                                     06/04/2021                   US2270790 Global Constant Class Changes
 *********************************************************************************************************************************************/
public with sharing class HUMInquiryHistoryController
{
    private list<HUMInquiryHistoryWrapperHelper> lstInquiryHistorylist = new list<HUMInquiryHistoryWrapperHelper>(); 
    private map<Integer,list<HUMInquiryHistoryWrapperHelper>> mapInquiryHistory = new map<Integer,list<HUMInquiryHistoryWrapperHelper>>();  
    private Account oAccount = new Account(); 
    private integer iPageNumber = 0 ; 
    private string sPolId = null;
    Public Static final String sDateTimeOpened = 'DateTimeOpened';
    Public Static final String sViewAll = 'bViewAll';
    Public Static final String sRefId = 'refId';
    Public Static final String sfDate= 'fDate';
    Public Static final String stDate = 'tDate' ;
    Public Static final Integer sfDateVal = 90; 
    
    /*
     * Method Name : HUMRetrieveInquiryHistory
     * Description : Constructor for the class
     * Return type : None
     * Paramater   : NA
     */
    public HUMInquiryHistoryController() 
    {
        if (ApexPages.CurrentPage().getParameters().containskey('Id'))
        {
            sRecId = ApexPages.CurrentPage().getParameters().get('Id');
        }
        if(String.isNotBlank(sRecId)) 
        {
            List<Account>	lstAccount;
            if(String.valueof(sRecId).startswith(Schema.SObjectType.Account.keyprefix))
            {
                sPolId = null;
                lstAccount = [Select Id, recordType.Name, Enterprise_ID__c ,  Mbr_Gen_Key__c from Account where Id = : sRecId]; 
            }
            else if(String.valueof(sRecId).startswith(Schema.SObjectType.Policy_member__c.keyprefix))
            {
                sPolId = sRecId;
                Policy_Member__c oPolicyMember = [Select Id,Member__c from Policy_Member__c where Id = : sRecId]; 
                if(oPolicyMember.Member__c != null) {lstAccount = [Select Id, recordType.Name, Enterprise_ID__c ,  Mbr_Gen_Key__c from Account where Id = : oPolicyMember.Member__c];}
            }
            else if(String.valueof(sRecId).startswith(Schema.SObjectType.Policy__c.keyprefix))
            {
                sPolId = null;
                lstAccount = [Select Id, recordType.Name, Enterprise_ID__c ,  Mbr_Gen_Key__c from Account where Id In (Select Group_Name__c from Policy__c where Id =: sRecId )];
            }
            if(null != lstAccount && lstAccount.size() > 0)
            {
                oAccount = lstAccount[0];
            }
            iRespSize = 0; 
            bFamilyMem = false;
            bShowTable = false;
            bViewAll = false;
            lstReqInquiryHistorylist = new list <HUMInquiryHistoryWrapperHelper> ();
            sSortExpression = sDateTimeOpened ; 
            sSortDirection = 'desc'; 
            initFilter(); 
            viewAllAction();
        }
        sGCPUrl =  HUM_Webservice_Callout__c.getInstance('HUMLaunchGCP').End_Point_URL__c; 
    }
    
    public integer iTableSize = Integer.valueof(HUMConstants__c.getValues('ListSize').IntegerValue__c);
   
    public string sRecId {get;set;} 
    
    public Integer iRespSize {get;set;} 
    
    public String sGCPUrl{get;set;} 
    
    // Wrapper list to hold the values which are displayed on UI
    public list <HUMInquiryHistoryWrapperHelper> lstReqInquiryHistorylist {get;set;} 
    
    public static String sSortExpression {get;set;} 
    public static String sSortDirection {get;set;}
    public string sFromDate {get;set;} 
    public string sToDate {get;set;} 
    public string sReferenceId {get;set;} 
    //Used as value for select list which is mandatory for apex continuation
    public string sSelectList {get;set;}
    
    public boolean bFamilyMem {get;set;} 
    public boolean bViewAll {get;set;} 
    public boolean bShowTable {get;set;} 
    
    // boolean variable to check if the list has next values
    public boolean bHasNext  
    {
        get { return HUMInquiryHistoryHelper.hasRecord(iPageNumber + 1, mapInquiryHistory);}
        set;
    }
   
    // boolean variable to check if the list has Previous values
    public boolean bHasPrevious  
    {
        get { return HUMInquiryHistoryHelper.hasRecord(iPageNumber - 1, mapInquiryHistory);}
        set;
    }
    
    /*
     * Method Name : viewAllAction
     * Description : Action method called on load of page to assign values to variables from the URL
     * Return type : Void
     * Paramater   : None 
     */
    public void viewAllAction()
    {
        // View All values
        if (ApexPages.CurrentPage().getParameters().containskey(sViewAll) && 
            ApexPages.CurrentPage().getParameters().get(sViewAll) == GlobalCommonConstants_HUM.STRING_YES )
        {
            bViewAll = true;
            if (ApexPages.CurrentPage().getParameters().containskey(sRefId))
            {
                sReferenceId  = ApexPages.CurrentPage().getParameters().get(sRefId);
            }
            if (ApexPages.CurrentPage().getParameters().containskey('bFamilyCheck'))
            {
              string FamilyMem = ApexPages.CurrentPage().getParameters().get('bFamilyCheck');
              if (FamilyMem != null && FamilyMem == 'true')
              {
                  bFamilyMem = true;
              }
               else
              {
                   bFamilyMem = false;    
              }

            }
            if (ApexPages.CurrentPage().getParameters().containskey(sfDate))
            {
                sFromDate  = ApexPages.CurrentPage().getParameters().get(sfDate);
            }
            if (ApexPages.CurrentPage().getParameters().containskey(stDate))
            {
                sToDate  = ApexPages.CurrentPage().getParameters().get(stDate);
            }
            retrieveLegacyData();
        }
    }

    /*
     * Method Name : initFilter
     * Description : Initialize the filter values
     * Return type : Void
     * Paramater   : None 
     */
    private void initFilter()
    {
        sFromDate = String.Valueof((system.today() - sfDateVal).format()); 
        sToDate = String.Valueof((system.today()).format()); 
        sReferenceId = ''; 
        iPageNumber = 0; 
    }
    
    /*
     * Method Name : clearFilter
     * Description : Initialize the filter values and then calls the service to get the data
     * Return type : Void
     * Paramater   : None 
     */
    public void clearFilter()
    {
        bFamilyMem = false;
        initFilter(); 
        retrieveLegacyData(); 
    }
    
    /*
     * Method Name : retrieveLegacyData
     * Description : Calls the webservice and then displays the data on page
     * Return type : Void
     * Paramater   : None 
     */
    public void retrieveLegacyData()
    {
        try
        {
            bShowTable = false; 
            if(HUMInquiryHistoryHelper.validateRequest(sFromDate, sToDate, sReferenceId))
            {
                HUMInquiryHistoryServiceHelper inqHis = new HUMInquiryHistoryServiceHelper(); 
                
                lstInquiryHistorylist = inqHis.initiateInquiryHistoryService(oAccount,bFamilyMem,sPolId,sFromDate,sToDate,sReferenceId); 
                if(lstInquiryHistorylist != null && !lstInquiryHistorylist.isEmpty())
                {
                    iRespSize = lstInquiryHistorylist.size();
                    bShowTable = true;
                    iTableSize = bViewAll ? iRespSize : iTableSize; 
                    sortData(); 
                }
                else 
                {
                    HUMInquiryHistoryHelper.addError(sFromDate, sToDate, sReferenceId);
                }
            }
        }
        catch(Exception e) 
        {
            if(bViewAll)
            {
                HUMExceptionHelper.bRestrictLog = true; // Exception not inserted when calling from constructor.
                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.Error, Label.HUMInquiryHistoryGenericError)); 
            }
            HUMExceptionHelper.logErrors(e,'HUMInquiryHistoryController','retrieveLegacyData');
        }
    }
    
    /*
     * Method Name : next
     * Description : Method to get the next values 
     * Return type : Void
     * Paramater   : None 
     */
    public void next() 
    {
        populateInqHistory(iPageNumber + 1);
    }
    
    /*
     * Method Name : previous
     * Description : Method to get the previous values 
     * Return type : Void
     * Paramater   : None 
     */
    public void previous() 
    {
        populateInqHistory(iPageNumber - 1);
    }
    
    /*
     * Method Name : first
     * Description : Method to get the first values 
     * Return type : Void
     * Paramater   : None 
     */
    public void first() 
    {
        populateInqHistory(1);
    }
    
    /*
     * Method Name : last
     * Description : Method to get the last values 
     * Return type : Void
     * Paramater   : None 
     */
    public void last() 
    {
        populateInqHistory(mapInquiryHistory.size());
    }
    
    /*
     * Method Name : populateInqHistory
     * Description : Gets the value from page and assign it to the list which is displayed on UI
     * Return type : Void
     * Paramater   : Integer 
     */
    private void populateInqHistory(Integer iPgNum)
    {
        if(mapInquiryHistory != null && !mapInquiryHistory.isEmpty() && mapInquiryHistory.containskey(iPgNum))
        {
            iPageNumber = iPgNum;
            lstReqInquiryHistorylist =  mapInquiryHistory.get(iPageNumber);
        }
    }
    
    /*
     * Method Name : sortData
     * Description : Sort the list displayed on page
     * Return type : Void
     * Paramater   : None 
     */
    public void sortData() 
    {
        try
        {
            mapInquiryHistory = HUMInquiryHistoryHelper.constructMap(lstInquiryHistorylist,iTableSize,sSortExpression,sSortDirection); // Construct the map of Inquiry History with Page Numbers
            iPageNumber = 0;
            next();
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e,'HUMInquiryHistoryController','SortData');
        }
    }
}