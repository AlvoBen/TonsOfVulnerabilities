/******************************************************************************************************************************
Apex Class Name  : CRM_Retail_CustomDashboard_C_HUM
Version          : 1.0 
Created Date     : 2/15/2022
Function         : To fetch data for custom storefront dashboard  
Test Class       : 

Modification Log :
*   Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*  Lakshmi Madduri                              2/17/2022          User Story 3104178 - T1PRJ0154546 MF9 Storefront Modernization - Create New Dashboards Tab
*  Lakshmi Madduri                              8/1/2022           Modernized home page changes
*  Mohamed Thameem                              11/20/2022         REQ- 3866581 Modernizing Dashboard
*  Mohamed Thameem                              07/07/2023         REQ- 4832304 First Time Visitor Report
*********************************************************************************************************************************/

public with sharing class CRM_Retail_Custom_Dashboard_C_HUM 
{
    public String sLocation{get;set;}
    public String sSelectedLocation{get;set;}
    public static String sFilter{get;set;}
    public static boolean bDisableLoc{get;set;}
    public static String sFrequentVisitors{get;set;}
    public static String sLastRefreshed{get;set;}
    public static String sFilterForHealthEducator{get;set;}
    public static String sReportNamesAndIds{get;set;}
    public static String sTaskDetails{get;set;}
    public static List<String> lstReports{get;set;} 
    public static Boolean bFunctionalitySwitch{get;set;}
    public static boolean bNoHomeLoc{get;set;}
    public static String fromDate{get;set;}
    public static String toDate{get;set;}
    public static String topEventData{get;set;}
    public static boolean topEventsSwitch{get;set;}
    
    public void callActionMethod()
    {
        try
        {
            bFunctionalitySwitch = HUMUtilityHelper.isCRMFunctionalityON('3104178');
            topEventsSwitch = HUMUtilityHelper.isCRMFunctionalityON('4832304');
            if(bFunctionalitySwitch){
                sSelectedLocation = getHomeLocation();
                lstReports = new List<String>();
                List<PermissionSetAssignment> lstAdmin = [select id,PermissionSet.Name from permissionsetassignment where AssigneeId =: Userinfo.getUserId() and (PermissionSet.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_400SUPERADMIN OR PermissionSet.Name =: GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_BUSINESSADMIN)];
                if(lstAdmin.size()>0){
                    bDisableLoc = false;
                    lstReports.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.lstAdminReports);
                    string LastMonth=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LASTMONTH;
                    sFilter = '['+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER+String.escapeSingleQuotes(sSelectedLocation)+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER_DEFAULT+LastMonth+'\'}]';
                    sFilterForHealthEducator = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_HEALTHEDUCATORFILTER+String.escapeSingleQuotes(sSelectedLocation)+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_JSONENDSTRING;
                    sTaskDetails = completedTasksAndContactAttempts(sSelectedLocation,true,fromDate,toDate);
                   	if(topEventsSwitch)
                    {
                        topEventData = fetchTopEvents(sSelectedLocation, '', '');
                    }
                }
                else{
                    bDisableLoc = true;
                    String sTodayDate=String.valueOf(System.today());
                    String sDate = sTodayDate.split('-')[1]+'/'+sTodayDate.split('-')[2]+'/'+sTodayDate.split('-')[0];
                    lstReports.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.lstServiceAssReports);            
                    sFilter = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SERVICEASSREPORTFILTER+String.escapeSingleQuotes(sSelectedLocation)+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_JSONENDSTRING+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SERVICEASSREPORTFILTER_1 ;       
                    sFrequentVisitors = String.escapeSinglequotes(retrieveWeeklyFrequentVisitors(sSelectedLocation,'',''));
                }
                fetchReportIds(bDisableLoc);               
                sLastRefreshed = System.now().format();
            }  
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CUSTOMDASHBOARDCLASSNAME, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CALLACTIONMETHOD);
        }                
    }
    
    /*
    * Method Name   :    fetchReportIds
    * Description   :    This method fetches all the view reports
    * Return Type   :    None
    * Parameters    :    Boolean
    */
    public void fetchReportIds(boolean bDisableLoc)
    {
        List<String> lstViewReports = new List<String>();
        if(bDisableLoc){
            lstViewReports.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.lstServiceAssViewReports);
        }
        else{
            lstViewReports.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.lstAdminViewReports);
        }
        Map<String,Id> mapOfReportNamesAndIds = new Map<String,Id>();
        for(Report oReport : [SELECT Id, DeveloperName FROM Report where DeveloperName=:lstViewReports]){
            mapOfReportNamesAndIds.put(oReport.DeveloperName,oReport.Id);
        }
        sReportNamesAndIds = JSON.serialize(mapOfReportNamesAndIds); 
    }
    
    /*
    * Method Name   :    setReportFilter
    * Description   :    This method sets the report filters
    * Return Type   :    None
    * Parameters    :    None
    */
    public void setReportFilter()
    {
        try
        {
            lstReports = new List<String>();
            if(String.isNotBlank(sLocation) && sLocation !=GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_UNDEFINED){
                if(!bDisableLoc){
                    lstReports.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.lstAdminReports);                  
                    sFilter = '['+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER+String.escapeSingleQuotes(sLocation)+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_JSONENDSTRING+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER_1+fromDate+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER_2+toDate+'\'}]';
                    sFilterForHealthEducator = '['+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_HEALTHEDUCATORFILTER+String.escapeSingleQuotes(sLocation)+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_JSONENDSTRING+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_HEALTHEDUCATORFILTER_1+fromDate+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_HEALTHEDUCATORFILTER_2+toDate+'\'}]';  
                    sTaskDetails = completedTasksAndContactAttempts(sSelectedLocation,false,fromDate,toDate);
                }
                else{
                    lstReports.addAll(GLOBAL_CONSTANT_STOREFRONT_HUM.lstServiceAssReports);
                    sFilter = GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SERVICEASSREPORTFILTER+String.escapeSingleQuotes(sLocation)+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_JSONENDSTRING+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER_1+fromDate+GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ADMINREPORTFILTER_2+toDate+'\'}]' ;       
                }     
                sLastRefreshed = System.now().format();
            }
            else{
                sFilter ='';
                sFilterForHealthEducator='';
                sTaskDetails='';
                sLastRefreshed = System.now().format();
            }
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CUSTOMDASHBOARDCLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_SETREPORTFILTER);
        }
    }
    
    /*
    * Method Name   :    fetchVisitorsAndTasks
    * Description   :    This method retrieves frequent visitors or completed tasks based on PS
    * Return Type   :    String
    * Parameters    :    String sLoc,boolean isServiceAss
    */
    @RemoteAction
    public static String fetchVisitorsAndTasks(String sLoc,String isServiceAss,String fromDate, String endDate)
    {
        try
        {
            String sResult='';
            if(isServiceAss=='true'){
                sResult = retrieveWeeklyFrequentVisitors(sLoc,String.escapeSingleQuotes(fromDate),String.escapeSingleQuotes(endDate));
            }
            else{
                sResult = completedTasksAndContactAttempts(sLoc,false,String.escapeSingleQuotes(fromDate),String.escapeSingleQuotes(endDate));
            }
            return sResult;
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CUSTOMDASHBOARDCLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_FETCHVISITORSANDTASKS);
            return null;
        }
    }
    
    /*
    * Method Name   :    fetchTopEvents
    * Description   :    This method retrieves top Five Events with first time checkin
    * Return Type   :    String
    * Parameters    :    String sLoc,String fromDate, String endDate
    */
    @RemoteAction
    public static String fetchTopEvents(String sLoc,String fromDate, String endDate)
    {
        try
        {            
            map<id,Storefront_I_Reason__c> mapOfReason = new map<id,Storefront_I_Reason__c>();
            List<object> returnData = new List<object>();
            
            Date fromDateDT = String.isBlank(fromDate)?  system.today().addmonths(-1).toStartOfMonth() : Date.valueOf(String.escapeSingleQuotes(fromDate));
            Date toDateDT = String.isBlank(endDate)?  system.today().toStartOfMonth().adddays(-1) : Date.valueOf(String.escapeSingleQuotes(endDate));
            
            for(AggregateResult aggr : [select count(id) total, reason__c, max(reason__r.name) Name from storefront_interaction__c where First_time_visitor__c = true AND location__r.name = :sLoc AND Day_Only(Interaction_Date__c) >= :fromDateDT AND Day_Only(Interaction_Date__c) <= :toDateDT group by reason__c order by count(id) desc limit :GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_INTEGER_TOPEVENTS_COUNT])
            {
                returnData.add(aggr);
            }
            return JSON.serialize(returnData);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CUSTOMDASHBOARDCLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_FETCHVISITORSANDTASKS);
            return null;
        }
    }
    
    /*
    * Method Name   :    getHomeLocation
    * Description   :    This method retrieves home location of the user
    * Return Type   :    String
    * Parameters    :    None
    */
    public static String getHomeLocation()
    {
        String sHomeLocation;
        String sPageName = ApexPages.currentPage().getUrl();
        if(String.isNotBlank(sPageName) && sPageName.containsIgnoreCase(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_HOMEDASHBOARD_PAGENAME)){
            sHomeLocation = (String)Cache.Session.get(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_CURRENTLOCATION);
            sHomeLocation = sHomeLocation.split('-')[1];
            return sHomeLocation;
        }
        sHomeLocation = GenericHelper_CRMRetail_H_HUM.getHomeLocation();
        if(String.isBlank(sHomeLocation)){
            bNoHomeLoc=true;
            sHomeLocation =  GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_LOUISVILLE;
        }        
        return sHomeLocation;        
    }
    
    /*
    * Method Name   :    getLocations
    * Description   :    This method retrieves all the active home locations
    * Return Type   :    List<SelectOption>
    * Parameters    :    None
    */
    public List<SelectOption> getLocations() 
    {	
        List<SelectOption> stList = new List<SelectOption>();
        try{
            for(Storefront_Location__c loc : [SELECT Id, Name FROM Storefront_Location__c where Inactive_indicator__c != True ORDER BY NAME ASC]){
                stList.add(new SelectOption(loc.Name,loc.Name));
            } 
        }
        catch(Exception e) {
            HUMExceptionHelper.logErrors(e, GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CUSTOMDASHBOARDCLASSNAME,GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_GETLOCATIONS);
        }
        return stList;
    }
    
    /*
    * Method Name   :    completedTasksAndContactAttempts
    * Description   :    This method retrieves all the completed tasks of last quarter
    * Return Type   :    String
    * Parameters    :    String
    */
    public static String completedTasksAndContactAttempts(String sLocation,Boolean isFromOnLoad,string fromDate,string toDate)
    {
        Map<String,Integer> mapOfTskCountAndContactAttempts = new Map<String,Integer>();
        Id taskRecordTypeId = Schema.SObjectType.Task.getRecordTypeInfosByName().get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRM_RETAIL_TASK).getRecordTypeId();
        AggregateResult arr;
        if(isFromOnLoad)
        {
            arr = [SELECT COUNT(Id),SUM(Contact_Attempts__c) FROM Task where recordtypeId =: taskRecordTypeId and CRM_Retail_Location__r.Name =:sLocation and status=: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS and CompletedDateTime=LAST_MONTH];
        }
        else
        {
            Datetime fromDateDT = Date.valueOf(String.escapeSingleQuotes(fromDate));
            Datetime toDateDT = Date.valueOf(String.escapeSingleQuotes(toDate));
            toDateDT = toDateDT.addDays(1).addMinutes(-1);
            arr = [SELECT COUNT(Id),SUM(Contact_Attempts__c) FROM Task where recordtypeId =: taskRecordTypeId and CRM_Retail_Location__r.Name =:sLocation and status=: GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAIL_TASK_COMPLETED_STATUS and CompletedDateTime >= :fromDateDT and CompletedDateTime <= :toDateDT ];
        } 
        if(arr != null){
            mapOfTskCountAndContactAttempts.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_TASKCOUNT,(Integer)arr.get('expr0'));
            Integer contactAttempts = (arr.get('expr1') != null) ?  Integer.valueOf(arr.get('expr1')) : 0;
            mapOfTskCountAndContactAttempts.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CONTACTATTEMPTS,contactAttempts);
        }
        return JSON.serialize(mapOfTskCountAndContactAttempts);
    }
    
    /*
* Method Name   :    retrieveWeeklyFrequentVisitors
* Description   :    This method retrieves all the frequent visitors for last week
* Return Type   :    String
* Parameters    :    String
*/
    public static String retrieveWeeklyFrequentVisitors(String sLoc,String fromDate, String endDate)
    {
        List<Id> lstMembers = new List<Id>();
        List<Id> lstNonMembers = new List<Id>();
        List<Id> lstMergeNonMemandMem = new List<Id>();
        
        List<String> lstMemStrings = new List<String>();
        List<String> lstNonMemStrings = new List<String>();
        Map<String,List<String>> mapOfFinalResult = new Map<String,List<String>>();
        Integer i=1;
        Integer j=1;
        Set<String> setOfCheckins = new Set<String>{GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_VISITOR_CHECKIN,GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_FIRSTSTIME_CHECKIN};
            List<AggregateResult> lstAggregateResult = new List<AggregateResult>();
        if(String.isBlank(fromDate)){
            lstAggregateResult = [select count(id),Account__c,CRM_Retail_Interaction_Visitor_Type__c from Storefront_Interaction__c where location__r.name=:sLoc and interaction_date__c = TODAY and Reason__r.Name IN: setOfCheckins group by Account__c,CRM_Retail_Interaction_Visitor_Type__c HAVING count(id) >  1 order by count(id) desc];
        }
        else{
            Date fromDateFormat = date.valueOf(fromDate);
            Date endDateFormat = date.valueOf(endDate);
            lstAggregateResult = [select count(id),Account__c,CRM_Retail_Interaction_Visitor_Type__c from Storefront_Interaction__c where location__r.name=:sLoc and interaction_date__c >= :fromDateFormat and interaction_date__c <= :endDateFormat and Reason__r.Name IN: setOfCheckins group by Account__c,CRM_Retail_Interaction_Visitor_Type__c HAVING count(id) >  1 order by count(id) desc];
        }
        for(AggregateResult arr:lstAggregateResult){
            if(arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAILVISITORTYPE) == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER && i<=5){
                lstMembers.add((Id)arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ACCOUNTFIELD));
                i++;
            }
            else if(arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_CRMRETAILVISITORTYPE) == GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_NON_MEMBER && j<=5){
                lstNonMembers.add((Id)arr.get(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_ACCOUNTFIELD));
                j++;
            }
            if(i==5 && j==5){
                break;
            }
        }
        if(!lstMembers.isEmpty() || !lstNonMembers.isEMpty()){
            lstMergeNonMemandMem.addAll(lstMembers);
            lstMergeNonMemandMem.addAll(lstNonMembers);
        }
        Map<Id,Account> mapOfAcc = new Map<Id,Account>([Select id,name from account where Id=:lstMergeNonMemandMem]);
        if(mapOfAcc != null){
            for(Id memId : lstMembers){
                lstMemStrings.add(memId+'_'+mapOfAcc.get(memId).Name);
            }
            for(Id nonMemId : lstNonMembers){
                lstNonMemStrings.add(nonMemId+'_'+mapOfAcc.get(nonMemId).Name);
            }
        }
        mapOfFinalResult.put(GLOBAL_CONSTANT_STOREFRONT_HUM.CRMRETAIL_MEMBER,lstMemStrings);
        mapOfFinalResult.put(GLOBAL_CONSTANT_STOREFRONT_HUM.STRING_NONMEMBER,lstNonMemStrings);
        return JSON.serialize(mapOfFinalResult);
    }
}