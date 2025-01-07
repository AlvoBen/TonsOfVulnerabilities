/*******************************************************************************************************************************
Apex Class Name : HUMSearchController 
Version         : 1.0
Created On      : 05/21/2014
Function        : This Class serves as a controller for Main search page. It coordinates data flow between 
                  main search page as well as individual search component and Interaction Log.
Test Class      : HUMSearchControllerTest
Modification Log: 
 * Developer Name           Code Review              Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Ninad Patil              17172                  05/21/2014                     Original Version
 * Chaitanya Kumar          17172                  06/06/2014                     Included InteractionWithDetails,InteractionAboutDetails and InteractionDetails method
 * Ninad Patil              17172                  06/20/2014                     Included swithTab Method to focus curor on specifc tab
 * Chaitanya Kumar          17172                  07/04/2014                     Included SoftPhoneRoutingSearch Method and relevant variables
 * Chaitanya Kumar          17172                  07/22/2014                     Included populateUnknowAccountDetails method
 * Chaitanya Kumar          17172                  07/31/2014                     Included getURLKeyValuePairs method to parse Base64 encoded url parameters
 * Chaitanya Kumar          17176                  08/20/2014                     Modified populateAgentDetails to assign value to sTaxID
 * Chaitanya Kumar          17824                  09/02/2014                     Modified getMemberDetailsFromExternalSystem,getEncryptedValuesForTaxID to add code related to upsert of Consumer Tax Ids,
 * Chaitanya Kumar          17824                  09/10/2014                     Fixed Defect 155382- Modified softPhoneRoutingSearch
 * Chaitanya Kumar          17862                  09/11/2014                     Modified getEncryptedValuesForTaxID to return Map of Normal and encrypted tax id values
 * Chaitanya Kumar          17883                  09/12/2014                     Modified populateAgentDetails to check the iAboutID and iWithID character length ,and assign values accordingly
 * Chaitanya Kumar          17999                  09/18/2014                     Modified getURLKeyValuePairs to decode UTF-8 encoded url parameter value
 * Chaitanya Kumar          18890                  10/27/2014                     Included logic in getMemberDetailsFromExternalSystem to upsert CBIS Account with Salesforce ID as External ID
 * Pranali Choudhari        18890                  10/30/2014                     Updated logic in getMemberDetailsFromExternalSystem to upsert CBIS Account with Salesforce ID as External ID
 * Sagar Tapadia            20358                  01/15/2015                     Created a boolean variable for popup and a method to close pop up
 * Adil Mohammed            20402                  01/19/2015                     REQ – 103999 : Adding suffix ID to SoftPhone call popup functionality
 * Vishal Verma             20566                  01/28/2015                     Created a new variable SEARCH_AGENT for value as 'Agent' at line 36.
 * Pradeep Veeravali        21640                  03/17/2015                     REQ - 160866 : replacing Salesforce Id with Member Id in Unknown CBIS Account External Id. Removed CBIS condition in getMemberIDAfterUpsert()
 * Ranjeeth Nagishetty      25224                  07/23/2015                     Defect 190339 : Added a call to method for encrypting the Interaction__c -> caller_name__c
 * Praveen Kumar Parimi     27530                  12/03/2015                     Removed the explict call to method for encrypting the Interaction__c -> caller_name__c and updated the code as per latest coding standards
 * Prudhvi Pamarthi         105627                 06/27/2017                     Commented Member, Group, Provider and Agent/Broker search functionality
 * Santhi Mandava           348723                 04/02/2019                     Cleanup cipher cloud references
 * Moshitha Gunasekaran                            04/20/2021                     US#2163329 - Cleanup old version reference of HumInteractionLogComponentController for checkmarx issues
 *********************************************************************************************************************************************************************************************/
public with sharing class HUMSearchController 
{ 
    //Constants related to Search
    private static final String SEARCH_MEMBER = HUMConstants.SEARCH_MEMBER;
    private static final String SEARCH_PROVIDER = HUMConstants.SEARCH_PROVIDER;
    private static final String SEARCH_GROUP = HUMConstants.SEARCH_GROUP;
    private static final String SEARCH_AGENCY = HUMConstants.SEARCH_AGENCY;
    private static final String SEARCH_AGENT = HUMConstants.SEARCH_AGENT;
    private static final String SEARCH_ENROLLMENT = HUMConstants.SEARCH_ENROLLMENT;
    //Constants related to tab Name
    private static final String TAB_NAME_MEMBER = HUMConstants.TAB_NAME_MEMBER;
    private static final String TAB_NAME_PROVIDER = HUMConstants.TAB_NAME_PROVIDER;
    private static final String TAB_NAME_GROUP = HUMConstants.TAB_NAME_GROUP;
    private static final String TAB_NAME_AGENCY = HUMConstants.TAB_NAME_AGENCY;
    private static final Map<String,String> mapAgentCodes = new Map<String,String>
                                                            {
                                                                SEARCH_AGENCY => SEARCH_AGENT,
                                                                SEARCH_PROVIDER  =>  SEARCH_PROVIDER,
                                                                SEARCH_GROUP => SEARCH_GROUP,
                                                                SEARCH_MEMBER => SEARCH_MEMBER
                                                            };
    public static boolean bPopup{get; set;}
    //Private Map to Store type of of Search for Soft Phone Routing
    private static final Map<String,String> mapSoftPhoneSearchCodes = new Map<String,String> 
    {
        'M' => SEARCH_MEMBER,'P' => SEARCH_PROVIDER,'A' => SEARCH_AGENCY,'G' => SEARCH_GROUP,
                'E' => SEARCH_GROUP,'UM' => SEARCH_MEMBER,'UP' => SEARCH_PROVIDER,'UA' => SEARCH_AGENCY,
                'UG' => SEARCH_GROUP,'UE' => SEARCH_GROUP,null=>''
    };
    //Private Map to Store codes for Tab to be focussed 
    private static final Map<String,String> mapSoftPhoneFocusCodes = new Map<String,String> 
    {
        'M' => TAB_NAME_MEMBER,'P' => TAB_NAME_PROVIDER,'A' => TAB_NAME_AGENCY,'G' => TAB_NAME_GROUP,
                'E' => TAB_NAME_GROUP,'UM' => TAB_NAME_MEMBER,'UP' => TAB_NAME_PROVIDER,'UA' => TAB_NAME_AGENCY,
                'UG' => TAB_NAME_GROUP,'UE' => TAB_NAME_GROUP,null => TAB_NAME_MEMBER
    };

    //Place holder for SoftPhone Data
    private HUMSoftPhoneRoutingHelper softPhoneWrapper = new HUMSoftPhoneRoutingHelper();

    //Storing record Id of record selected on the search page
    public string sResultRecordId = '';

    /*
     * Method Name   :   HUMSearchController
     * Description   :   Constructor - Initialize Variables 
     * Return Type   :   void
     * Parameters    :   NA
     */
    public HUMSearchController()
    {
        initializeVariables();
    }

    // Store the Individual Component References in the map
    public Map<String, HUMSearchComponentBaseHelper> searchControllerMap {get;set;}
    // Store the Base Component References in the map
    public HUMSearchComponentBaseHelper providerReference {get;set;}
    // Tab to be focussed
    public String sTabToBeFocussed{get; set;}
    //Store SoftPhone Json String
    public string transferJsonObject {get;set;}
        
    /*
     * Method Name   :   setComponentControllerMap
     * Description   :   Store all the search component references . This will be called by respective components   
     * Return Type   :   void
     * Parameters    :   String , HUMSearchComponentBaseHelper
     */
    public void setComponentControllerMap(String key, HUMSearchComponentBaseHelper compController)
    {         
        if(searchControllerMap == null) searchControllerMap = new Map<String, HUMSearchComponentBaseHelper>();
        searchControllerMap.put(key,compController); 
    }

    /*
     * Method Name   :    initializeVariables
     * Description   :    Initialize Variables
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void initializeVariables() 
    {
        sTabToBeFocussed = HUMConstants.TAB_NAME_MEMBER;
    } 

    /*
     * Method Name   :    switchTab 
     * Description   :    Set the tab ID to be focussed
     * Return Type   :    void
     * Parameters    :    NA
     */
    public void switchTab() 
    {
        sTabToBeFocussed = HUMConstants.TAB_NAME_ENROLLMENT;
    }

    /*
     * Method Name   :    getThis 
     * Description   :    Store the Current Page Instance
     * Return Type   :    HUMSearchController
     * Parameters    :    NA
     */
    public HUMSearchController getThis() 
    {
        return this;
    }
    
  
    /*
     * Method Name   :    softPhoneRoutingSearch 
     * Description   :    Do Auto search based on the parameters passed from Soft phone
     * Return Type   :    void
     * Parameters    :    NA
     */ 
    public void softPhoneRoutingSearch() 
    {
        try  
        {
            if(!String.ISBLANK(transferJsonObject)) 
            {
                //Deserialize JSon String to Key Value Pairs of URL Parameters
                Map<String,String> mapSoftPhoneParameters = getURLKeyValuePairs(transferJsonObject);                

                //Populate All Soft Phone Data  in Wrapper . If no key present in the map ,Null is assigned
                softPhoneWrapper.sIntAboutFirstName = mapSoftPhoneParameters.get(HUMConstants.INTABTFNAME);
                softPhoneWrapper.sIntAboutLastName = mapSoftPhoneParameters.get(HUMConstants.INTABTLNAME);
                softPhoneWrapper.sIntAboutID = mapSoftPhoneParameters.get(HUMConstants.INTABTID);
                softPhoneWrapper.sIntAboutDOB = mapSoftPhoneParameters.get(HUMConstants.INTABTDOB);
                softPhoneWrapper.sIntAboutType = mapSoftPhoneParameters.get(HUMConstants.INTABOUTTYPE);
                softPhoneWrapper.sIntWithFirstName = mapSoftPhoneParameters.get(HUMConstants.INTWITHFNAME);
                softPhoneWrapper.sIntWithLastName = mapSoftPhoneParameters.get(HUMConstants.INTWITHLNAME);
                softPhoneWrapper.sIntWithID = mapSoftPhoneParameters.get(HUMConstants.INTWITHID);
                softPhoneWrapper.sIntWithDOB = mapSoftPhoneParameters.get(HUMConstants.INTWITHDOB);
                softPhoneWrapper.sIntWithType = mapSoftPhoneParameters.get(HUMConstants.INTWITHTYPE);
                softPhoneWrapper.sInteractionFirstName = mapSoftPhoneParameters.get(HUMConstants.FIRSTNAME);
                softPhoneWrapper.sInteractionLastName = mapSoftPhoneParameters.get(HUMConstants.LASTNAME);
                softPhoneWrapper.sInteractionCallType = mapSoftPhoneParameters.get(HUMConstants.CALLTYPE);
                softPhoneWrapper.sInteractionUUID = mapSoftPhoneParameters.get(HUMConstants.UNIQUEID);
                softPhoneWrapper.sIntAboutSuffixId = mapSoftPhoneParameters.get(HUMConstants.INTABTIDSUFFIX);
                softPhoneWrapper.sIntWithSuffixId = mapSoftPhoneParameters.get(HUMConstants.INTWITHIDSUFFIX);

                //Route the business logic based on the URL Parameters 
                routeSearchLogic(mapSoftPhoneParameters);
                
            }
        }
        catch(Exception e) 
        {
            HUMExceptionHelper.logErrors(e, 'HUMSearchController', 'softPhoneRoutingSearch');
        }   
    }


    /*
     * Method Name   :    routeSearchLogic 
     * Description   :    Route the logic to the respective method based on the search type
     * Return Type   :    void
     * Parameters    :    NA
     */ 
    @testvisible private void routeSearchLogic(Map<String,String> mapSoftPhoneParameters)
    {

        if(!String.ISBLANK(softPhoneWrapper.sIntAboutType) || !String.ISBLANK(softPhoneWrapper.sIntWithType)) 
        {
            //Tab to be focussed
            String getTabName = !String.ISBLANK(softPhoneWrapper.sIntWithType) ? softPhoneWrapper.sIntWithType : softPhoneWrapper.sIntAboutType;
            stabTobeFocussed = !String.ISBLANK(getTabName) ? mapSoftPhoneFocusCodes.get(getTabName) : TAB_NAME_MEMBER;                
            softPhoneWrapper.sIntWithType = mapSoftPhoneSearchCodes.get(softPhoneWrapper.sIntWithType);
            softPhoneWrapper.sIntAboutType = mapSoftPhoneSearchCodes.get(softPhoneWrapper.sIntAboutType);

            //Do Auto Search based on Search Type . Exceptions are handled by this methods catch block    
            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_MEMBER) || softPhoneWrapper.sIntWithType.equals(SEARCH_MEMBER))
            {
                //populateMemberDetails(mapSoftPhoneParameters);
            }           
            if(softPhoneWrapper.sIntAboutType.equals(SEARCH_PROVIDER) || softPhoneWrapper.sIntWithType.equals(SEARCH_PROVIDER))
            { 
                //populateProviderDetails(mapSoftPhoneParameters);
            }                   
            if(softPhoneWrapper.sIntAboutType.equalsIgnoreCase(SEARCH_GROUP) || softPhoneWrapper.sIntWithType.equals(SEARCH_GROUP))
            {
               // populateGroupDetails(mapSoftPhoneParameters);
            }
            if(softPhoneWrapper.sIntAboutType.equalsIgnoreCase(SEARCH_AGENCY) || softPhoneWrapper.sIntWithType.equals(SEARCH_AGENCY) )
            {
               // populateAgentDetails(mapSoftPhoneParameters);
            }     

        }   

    }


    /*
     * Method Name   :    getURLKeyValuePairs
     * Description   :    Dissect the Query String and return key value pairs
     * Return Type   :    Map<String,String>
     * Parameters    :    String
     */ 
    public static Map<String,String> getURLKeyValuePairs(String sUrlString) 
    {
        Map<String, String> mapQueryKeyValues = new Map<String, String>();
        if(String.ISNOTBLANK(sURlString)) 
        {
            List<String>  listKeyValuePairs = sUrlString.split(HUMConstants.AMP);
            for (String key : listKeyValuePairs) 
            {
                Integer iKey = key.indexOf(HUMConstants.EQUAL);
                //In case of softphone routing URL is Base 64 encoded . In query string parameter value is undefined
                boolean isEncodedvalue = (key.substring(iKey + 1) == HUMConstants.UNDEFINED || String.ISBLANK( key.substring(iKey + 1)) ) ? true:false;
                if(isEncodedValue) 
                {
                    List<String>  lstDecodedvalues = (EncodingUtil.Base64Decode(key.substring(0, iKey))).toString().split(HUMConstants.AMP);
                    for(String decodedKey : lstDecodedvalues) 
                    {
                        Integer idecodedKey = decodedKey.indexOf(HUMConstants.EQUAL);
                        mapQueryKeyValues.put(decodedKey.substring(0, idecodedKey),EncodingUtil.URLdecode(decodedKey.substring(idecodedKey + 1),'UTF-8'));
                    }
                }
                else
                {
                    mapQueryKeyValues.put(key.substring(0, iKey),EncodingUtil.URLdecode(key.substring(iKey + 1),'UTF-8'));
                } 
            }
        }
        return mapQueryKeyValues;
    }
    
    /*
     * Method Name   :    closePopUp
     * Description   :    Close the pop up on interaction
     * Return Type   :    NA
     * Parameters    :    NA
    */
    public void closePopup() 
    {        
        try
        {  
            bPopup = false;  
        }
        catch(exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMSearchController','closePopup');
        }
    }
}