/**************************************************************************** 
Apex Class Name  : HUMSearchHelper
Version          : 1.0  
Created Date     : June 4 2014 
Function         : It will used as a Base class for the response structure of HUMSoftPhoneCallTransferService
Test Class       : HUMSearchHelperTest
 * Developer Name           Code Review                  Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Vegendla       17176                       06/04/2014              Original Version
 * Shruthi Karanth          17176                       07/12/2014              Added method for creating request instance for search webservice callouts.
 * Shreya Choodamani        18797                       10/13/2014              WSDL generated class updated as per the updated WSDL for 1411.
 * Shruthi Karanth          18840                       10/29/2014              Commented out 14.11 code
 * Pranali Choudhari        18890                       10/30/2014              Added code for CBIS
 * Riddhi Mittal 										07/30/2020				CCP is shutting down
 ******************************************************************************************************************************************************************************/ 

public with sharing class HUMSearchHelper
{
  Public static Boolean ccpOnOff {get; set;}  
    /*
     * Method Name   :    getStandardSetObjects 
     * Description   :    This is a method accepts String parameters related to SOQL query.
                          It Constructs the query, runs it and returns the result in the form of standard set controller.   
     * return Type   :    ApexPages.StandardSetController 
     * Parameters    :    String selectQuery  
                          String sObjectType
                          String whereClause
                          String orderBy
                          String sortDirection 
                          Integer limitSize 
                          set<ID> setOneQueryIds  
                          set<ID> setTwoQueryIds
     */
    public static ApexPages.StandardSetController getStandardSetObjects(String selectQuery , String sObjectType , 
            String whereClause, String orderBy , String sortDirection , 
            Integer limitSize, set<ID> setOneQueryIds , 
            set<ID> setTwoQueryIds)
    {
        //Return List of Sobjects which will be later Casted by Calling Class
        ApexPages.StandardSetController sObjectList;

        //Construct query
        String soqlQuery =selectQuery + ' from ' + sObjectType  ;
        soqlQuery = (!String.isBlank(whereClause))? soqlQuery + ' Where ' + String.escapeSingleQuotes(whereClause) :soqlQuery;
        soqlQuery =(!String.isBlank(orderBy))? soqlQuery + ' order By ' + orderBy :soqlQuery;
        soqlQuery =(!String.isBlank(sortDirection))? soqlQuery + ' ' + sortDirection  :soqlQuery;
        soqlQuery =(limitSize!=null)? soqlQuery +  ' limit '+ limitSize:soqlQuery + limits.getLimitQueryRows();
      
        //Running Query
        sObjectList=new ApexPages.StandardSetController(Database.getQueryLocator(soqlQuery)); 

        return sObjectList;
    }

    /*
     * Method Name   :    getStandardSetObjects 
     * Description   :    This method accepts String query and returns query results in the form of standard set controller.   
     * return Type   :    ApexPages.StandardSetController 
     * Parameters    :    String soqlQuery
     */
    public static ApexPages.StandardSetController getStandardSetObjects(string soqlQuery)
    {
        return  new ApexPages.StandardSetController(Database.getQueryLocator(soqlQuery)); 
    }

    /*
     * Method Name   :    getListOfRecords 
     * Description   :    This method accepts Query string and runs it. 
                          returns list of sObjects to the caling function.  
     * return Type   :    List<Sobject>
     * Parameters    :    String soqlQuery
     */
    public static List<Sobject> getListOfRecords (string soqlQuery)
    {
        return Database.query(soqlQuery); 
    }

    /*
     * Method Name   :    getListOfRecords 
     * Description   :    This is a method accepts String parameters related to SOQL query.
                          It Constructs the query, runs it and returns the result in the form of standard set controller. 
     * return Type   :    List<Sobject>
     * Parameters    :    String selectQuery  
                          String sObjectType
                          String whereClause
                          String orderBy
                          String sortDirection 
                          Integer limitSize 
                          set<ID> setOneQueryIds  
                          set<ID> setTwoQueryIds
     */
    public static List<Sobject> getListOfRecords (String selectQuery , String sObjectType , 
            String whereClause, String orderBy , 
            String sortDirection , Integer limitSize, 
            set<ID> setOneQueryIds , set<ID> setTwoQueryIds)
    {
        //Construct query
        String soqlQuery =selectQuery + ' from ' + sObjectType  ;
        soqlQuery =(!String.isBlank(whereClause))? soqlQuery + ' Where ' + String.escapeSingleQuotes(whereClause) :soqlQuery;
        soqlQuery =(!String.isBlank(orderBy))? soqlQuery + ' order By ' + orderBy :soqlQuery;
        soqlQuery =(!String.isBlank(sortDirection))? soqlQuery + sortDirection  :soqlQuery;
        soqlQuery =(limitSize!=null)? soqlQuery +  ' limit '+ limitSize:soqlQuery + limits.getLimitQueryRows();

        return Database.query(soqlQuery);
    }

    /*
     * Method Name   :    getListOfRecords 
     * Description   :    This is a method accepts String parameters related to SOQL query.
                          It aslo accepts set of Accoun ids, member Ids, Group number.
                          It Queries and returns result in the form of standard set controller.
     * return Type   :    ApexPages.StandardSetController
     * Parameters    :    String soqlQuery (Query string) 
                          Set <ID> setAccIds 
                          Set <ID> setAccIdForMemberId
                          set <ID> setAccIdForGrpNumber
     */
    public static ApexPages.StandardSetController getListOfRecords (string soqlQuery, 
            Set <ID> setAccIds, 
            Set <ID> setAccIdForMemberId, 
            set <ID> setAccIdForGrpNumber)
    {
        try
        {
            return  new ApexPages.StandardSetController(Database.getQueryLocator(soqlQuery)); 
        }    
        catch(Exception e)
        {
            //Paramters - excetion , classname , methodname
            HUMExceptionHelper.logErrors(e,'HUMSearchFieldsWrapper' ,'getListOfRecords');
        } 
        return null;
    }

    /*
     * Method Name   :    getStateCodes 
     * Description   :    This is a method accepts picklist value for state from picklist.
                          It searches for its corresponding state code in custom setting and returns the value.
     * return Type   :    String
     * Parameters    :    String statePicklistVal 
     */
    public static String getStateCodes(String statePicklistVal)
    {
        if(!(String.isBlank(statePicklistVal)))
        {
            //2-digit Sate code is sent in the request. The State - State Code mapping is maintained in the below custom setting
            HUMMember_search_state_mapping__c StateCustSetting = HUMMember_search_state_mapping__c.getValues(statePicklistVal);

            if(StateCustSetting != null)
            { 
                return StateCustSetting.State_Code__c;
            }
            else
            { 
                return '';
            }
        }
        else 
        {
            return '';
        }
    }

    /*
     * Method Name   :    getState 
     * Description   :    This is a method accepts 2 digit state code value.
                          It searches corresponding state value in the custom setting and returns it.
     * return Type   :    String 
     * Parameters    :    String 
     */
    public static String getState(String sStateCode)
    {
        String sState = '';
        if(!(String.isBlank(sStateCode)))
        {
            //2-digit Sate code is sent in the request. The State - State Code mapping is maintained in the below custom setting
            List <HUMMember_search_state_mapping__c> StateCustSetting = HUMMember_search_state_mapping__c.getAll().values();
            for(Integer i=0; i<StateCustSetting.size(); i++)
            {
                if(sStateCode == StateCustSetting[i].State_Code__c)
                {
                    sState = StateCustSetting[i].State__c;
                    break;  
                }
            }
            if(sState != null)
            { 
                return sState;
            }
            else
            { 
                return '';
            }
        }
        else
        {
            return '';
        }
    }

    /*
     * Method Name   :    getServiceRequestInstance 
     * Description   :    This method is creating the request instance for  web service callouts.
                          Depending upon context of search(Member/Provider/Group/Agent etc) proper parameters
                          such as end point URL, certificate are picked from custom settings.
     * return Type   :    Object
     * Parameters    :    String serviceNameInCustomSetting (Name of custom setting to be refered)
                          String serviceRequestStructure  (service structure to be called.)
     */
    public static Object getServiceRequestInstance(String serviceNameInCustomSetting, String serviceRequestStructure)
    { 
	   ccpOnOff = false;
        // On/Off switch functionality for CCP 
       ccpOnOff= HUMUtilityHelper.isCRMFunctionalityON('1185084');
        try
        {
            Object requestMsg;
            HUM_Webservice_Callout__c svcHeaders = HUM_Webservice_Callout__c.getValues(serviceNameInCustomSetting); 
            if(svcHeaders != null)
            {  
			   if(ccpOnOff == false)
			   {
                if (serviceNameInCustomSetting.equals('GroupSearchService') && svcHeaders.Active__c)
                {
                    requestMsg = new HUMIntGroupSearchService.soap(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));
                }
			   }
                if (serviceNameInCustomSetting.equals('EESLegacySearchService') && svcHeaders.Active__c)
                {
                    requestMsg = new HUMIntEESLegacySearchService.PlatformMemberWebServiceSoap(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));
                }
                if (serviceNameInCustomSetting.equals('H1ExchangeService') && svcHeaders.Active__c)
                {
                    requestMsg = new HumH1ExchangeService.AM65AM65Port(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));
                }
                if (serviceNameInCustomSetting.equals('TrackEnrollmentService') && svcHeaders.Active__c)
                {
                    requestMsg = new HUMIntTrackEnrollmentService.BasicHttpBinding_ITwoWayAsync(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));
                }
                if (serviceNameInCustomSetting.equals('AEHistoryService') && svcHeaders.Active__c)
                {
                    requestMsg = new HUMIntAEHistoryService.BasicHttpBinding_ITwoWayAsync(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));
                }
                if (serviceNameInCustomSetting.equals('OHBPService') && svcHeaders.Active__c)
                {
                    requestMsg = new HUMOHBPService.soap(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));
                }
                if (serviceNameInCustomSetting.equals('HUMCbisSearchEnrollment') && svcHeaders.Active__c)
                {                       
                    requestMsg = new HUMIntCbisService.EndPoint_GetPendHistoryAndTermsDetails(svcHeaders.End_Point_URL__c,svcHeaders.Certificate_Name__c,Integer.valueOf(svcHeaders.Timeout__c));                       
                }
                return requestMsg;
            }
            else
            {
                return null;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchHelper', 'getServiceRequestInstance');
            return null;
        }
    }
}