/**********************************************************************************************************************************************
Apex Class Name  : HUMInquiryHistoryHelper
Version          : 1.0
Created Date     : May 9 2014
Function         : Invoke the Inquiry history webservice
Test Class       : HUMInquiryHistoryHelperTest 
Modification Log: 
 * Developer Name         Code Review                    Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Srikant Joshi            17175                       05/09/2014                   Original Version
 * Srikant Joshi            17175                       07/21/2014                   Added additional filtering logic       
 * Vishal Verma             17783                       09/02/2014                   Sorting functinality removed from the class. 
 * Shruthi Karanth          18232                       09/28/2014                   Added space between first name and last name 
 * Shruthi Karanth          18232                       09/29/2014                   Updated test class.     
 * Shruthi Karanth          18313                       09/30/2014                   Breaking functions in to smaller methods. 
 * Vishal Verma             18521                       10/07/2014                   Added helper methods that is called form HUMInquiryHistoryController And HUMInquiryHistoryPMController.
 * Shruthi Karanth          18888                       10/21/2014                   Changed the custom label of Agent record type name to fix defect 153495.0001    
 * Vishal Verma             18888                       10/29/2014                   Added a security check for dental and medical access on Policy Member before showing data on the page. 
 * Vishal Verma             18994                       11/04/2014                   Added code for fetching Inquiry History from the Min Start Date.     
 * Vishal Verma             19131                       11/10/2014                   Added code for security check for Home Office All.
 * Vishal Verma             20677                       02/02/2015                   Defect : 169366.0001 Modified constructInquiryHistoryWrapper method to add a condition 
                                                                                     If we have to search in Date and Reference Id.
 * Vishal Verma             20947                       02/16/2015                   Defect 172363.0001 , Created a new method buildQuery for Querying on different objects and storing the values. 
                                                                                     These values will be used at multiple places in the class, so for avoiding the same query to run multiple times 
                                                                                     we have made this method.  
 * Vishal Verma             21169                       02/17/2015                   Defect : 170956.0001 - Changed custom labels to custom settings
 * Vishal Verma             21409                       03/10/2015                   Changed custom settings back to custom labels as a new user story is created for this change.  
 * Vishal Verma             22273                       04/03/2015                   Req  - 166509 -1.Added Date , Reference Id filters in the webservice Request .
                                                                                                    2.Changed Custom labels , Constants to Custom Settings
                                                                                                    3.Refactored code with a more scalable approach and removed unused code. 
                                                                                     This class is called from another class , so the Exception handling is done in the parent class.
                                                                                     Moved the webservice logic to HUMInquiryHistoryServiceHelper Class                                                               
 * Prudhvi Pamarthi         297150                      10/16/2018                   REQ - 377195 Fix for CA Ticket 7742421 - Added warning msg for invalid Inquiry ID                                                                                    
 **********************************************************************************************************************************************/
public with sharing class HUMInquiryHistoryHelper
{
    /*
     * Method Name : displayErrorMessage
     * Description : This function checks whether generic error message is displayed on the page or not
     * Return type : boolean
     * Paramater   : None
     */
    public static boolean displayErrorMessage()
    {
        if(ApexPages.getMessages() != null && !ApexPages.getMessages().isEmpty())
        {
            for(Apexpages.Message msg: ApexPages.getMessages())
            {
                if (msg.getDetail().contains(system.label.HUMInquiryHistoryGenericError)) 
                {
                     return  false ; // Return false if the generic message is displayed on page
                }
            }
        }
        return true;
    }
    
    /*
     * Method Name : constructMap
     * Description : This method is used to sort the list and create map of page number with list of HUMInquiryHistoryWrapperHelper
     * Return type : map<Integer,List<HUMInquiryHistoryWrapperHelper>>
     * Paramater   : List<HUMInquiryHistoryWrapperHelper> , Integer , String , String
     */
    public static map<Integer,List<HUMInquiryHistoryWrapperHelper>> constructMap(List<HUMInquiryHistoryWrapperHelper> lstInqHis , Integer iSize , String sSortExpression , String sSortDirection )
    {
        map<Integer,List<HUMInquiryHistoryWrapperHelper>> mapInquiryHistory = new map<Integer,List<HUMInquiryHistoryWrapperHelper>>();
        if(lstInqHis != null && !lstInqHis.isEmpty())
        {
            HUMInquiryHistoryWrapperHelper.sSortExpression = sSortExpression; // Assigning values to HUMInquiryHistoryWrapperHelper for Sorting
            HUMInquiryHistoryWrapperHelper.sSortDirection = sSortDirection; // Assigning values to HUMInquiryHistoryWrapperHelper for Sorting
            lstInqHis.sort();
            integer iKey = Integer.valueOf(HUMConstants__c.getValues('HUMConstantOne').IntegerValue__c); // variable to set key to map
            for(HUMInquiryHistoryWrapperHelper inq : lstInqHis) // Iterate over the list to create a map
            {
                if(mapInquiryHistory.containskey(iKey) && !mapInquiryHistory.get(iKey).isEmpty())
                {
                    List<HUMInquiryHistoryWrapperHelper> inqL = new List<HUMInquiryHistoryWrapperHelper>();
                    inqL = mapInquiryHistory.get(iKey);
                    inqL.add(inq);
                    mapInquiryHistory.put(iKey,inqL);
                }
                else 
                {
                    mapInquiryHistory.put(iKey,new List<HUMInquiryHistoryWrapperHelper> {inq});
                }
                if(mapInquiryHistory.containskey(iKey) && !mapInquiryHistory.get(iKey).isEmpty()  && mapInquiryHistory.get(iKey).size() == iSize){iKey++;} // Increase the keyset when the values count reaches iSize
            }
        }
        return mapInquiryHistory; // return map
    }
    
    /*
     * Method Name : hasRecord
     * Description : This method is used to check if the map contains the passed integer
     * Return type : boolean
     * Paramater   : Integer , map<Integer,List<HUMInquiryHistoryWrapperHelper>>  
     */
    public static boolean hasRecord(Integer iPgNumb , map<Integer,List<HUMInquiryHistoryWrapperHelper>> mapInquiryHistory)
    {
        return(mapInquiryHistory != null && !mapInquiryHistory.isEmpty() && mapInquiryHistory.containskey(iPgNumb));
    }
    
    /*
     * Method Name : addError
     * Description : This method is used to Add Error Message
     * Return type : Void
     * Paramater   : String, String , String  
     */
    public static void addError(String sFromDate , String sToDate , String sReferenceId )
    {
        if(String.isNotBlank(sFromDate) && String.isNotBlank(sToDate) && HUMInquiryHistoryHelper.displayErrorMessage()) // Condition where from date and to date are provided from page
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HumCCPGCPContactNoResultFormatError)); // Error message for no data in the date range
        }
        if(String.isNotBlank(sReferenceId) && HUMInquiryHistoryHelper.displayErrorMessage()) // Reference Id provided from page
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, Label.HumCCPGCPContactRefrenceFormatError+ sReferenceID)); // Error message for no data for reference Id
        }
    }
    
    /*
     * Method Name : validateRequest
     * Description : This method is used to validate required params
     * Return type : boolean
     * Paramater   : String, String , String 
     */
    public static boolean validateRequest(String sFromDate , String sToDate , String sReferenceId )
    {
        if((String.isNotBlank(sFromDate) && String.isNOtBlank(sToDate)) || String.isNotBlank(sReferenceId)) // Required parameters , either dates or reference id needs to be passed when invoking the service
        {
            try
            {
                if(((String.isNotBlank(sFromDate) || String.isNOtBlank(sToDate)) &&  date.parse(sFromDate) > date.parse(sToDate)) || (String.isNotBlank(sReferenceId) && !sReferenceId.isNumeric()))
                {
                    if((String.isNotBlank(sFromDate) || String.isNOtBlank(sToDate)) &&  date.parse(sFromDate) > date.parse(sToDate)) // Check if From Date is greater than to date
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, system.label.HUMDateValidation)); // Error Message for From Date is Greater than to Date
                    }
                    if(String.isNotBlank(sReferenceId) && !sReferenceId.isNumeric()) // Check if Ref ID contains any characters other than numbers
                    {
                        ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, system.label.HUMRefIdValidation)); // Error Message for letters in REF ID
                    }
                    return false;
                }                
            }
            catch(TypeException ex) // To capture type exceptions , e.g. Improper dates 
            {
                ApexPages.addMessages(ex); // Show the type exception on page
                return false;
            }
        }
        else
        {
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.INFO, system.label.HUMInqHistReqFields)); // Error message when required params are not passed
            return false;
        }
        return true;
    }
}