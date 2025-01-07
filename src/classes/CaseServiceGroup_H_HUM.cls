/*******************************************************************************************************************************
Apex Class Name :  CaseServiceGroup_H_HUM.cls
Version: 1.0
Created On      : 10/17/2018 
Function        : Helper class to get Account Map For Group from Case service
Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------

* Seema Kaulgi                                         07/04/2019                REQ - 403811 GBO Watson chat (Group) 
* Riddhi Mittal											07/30/2020				 CCP functionality is shutting down so removing its functionality 
* Sagar MN					 	                        02/10/2021	             US1867250 - Added ETL Record deleted flag to queries
*******************************************************************************************************************************/
public with sharing class CaseServiceGroup_H_HUM 
{
    public String interactingType;
    public HUMGroupSearchWrapperHelper oSearchWrapper;    
    public static Map<String,Account> mapRecordData = new Map<String,Account>();
    public static Map<String,Map<String,String>> mapReturnValues = new Map<String,Map<String,String>>();
    private Map<String,List<ConsumerID__c>> mapAconsumerAccount;
    private Set<String> setConsumerIds = new Set <String>();
    public static Map<String,Boolean> bisMultipleAcct = new Map<String,Boolean>();         
    private List<Account> lstAccountList = new List<Account>();    
    public HUM_Webservice_Callout__c objWebserviceCallout = HUM_Webservice_Callout__c.getInstance('GroupSearchService');
    //Boolean to check if an error thrown is for Group search. This is used in Softphone call popup.
    public transient boolean bIsGroupError{get;set;}
    public static boolean bIsGroupService{get;set;}
    public static boolean blninsertunknowngrp{get;set;}
    public static boolean blnisCRMgrp{get;set;}
    //Variable to hold the web service callout Response
    public List<HUMGroupSearchServiceHelper.ResponseGroupSearch> responseDTO {get;set;} 
     Public Boolean ccpOnOff {get; set;}  
	 
      //To capture errors
    public List<Error_Log__c> lstLogErrors
    {
        get 
        {
            return lstLogErrors != null ? lstLogErrors : new List<Error_Log__c>();
        }
        set;
    } 
     
    //Boolean to indicate that the results are from external system
    public boolean bExternalResults{get;set;}
     /*  
     * Method name : queryGroupAccount
     * Description : This method will be called when integration does not return any result
                     to do an internal search within SFDC
     * @return     : Map<String,Account> - accountsMap
     * Parameter   : NA
     */
    public Map<String,Account> queryGroupAccount(CaseServiceRestInteraction_DTO_HUM interactionwrapObj,String sinteractingType)
    {  
        Map<String,Account> accountsMap = new Map<String,Account>(); 
        bExternalResults = false; 
        blnisCRMgrp = false;
        lstAccountList = new List<Account>(); 
        interactingType =  sinteractingType     ;
        ccpOnOff = false;
        // On/Off switch functionality for CCP 
         ccpOnOff= HUMUtilityHelper.isCRMFunctionalityON('1185084');
		String sQry = ''; 
        bIsGroupService = false;
        blninsertunknowngrp = false;
        oSearchWrapper = new HUMGroupSearchWrapperHelper();       
        oSearchWrapper.sSourcePlatformCode = interactionwrapObj.sPlatformCode;
        oSearchWrapper.sGroupNumber= interactionwrapObj.sGroupID;                 
        if(!String.isblank(oSearchWrapper.sGroupNumber))
        {   
             sQry = 'Select Account_External_ID__c,Id, FirstName,Home_Office_Account__c, RecordType.Name, Phone, BillingStreet, BillingCity,ETL_Record_Deleted__c,BillingState, BillingCountry, Group_Number__c,'; 
             sQry += 'Source_Platform_Code__c,UserRecordAccess.HasReadAccess, Name from Account where (RecordType.DeveloperName ='+'\''+HUMConstants.HUM_Group_Account_Record_Type+'\'';
             sQry += ' OR RecordType.DeveloperName ='+'\''+ HUMConstants.HUM_UnknownGroup_Account_Record_Type+'\''+')';                          
             sQry += ' AND ' +'Group_NuMBER__C='+'\''+ String.escapeSingleQuotes(oSearchWrapper.sGroupNumber)+'\'';
             if(!(String.isBlank(oSearchWrapper.sSourcePlatformCode))){sQry += ' AND ' +'Source_Platform_Code__c='+'\''+ String.escapeSingleQuotes(oSearchWrapper.sSourcePlatformCode) +'\'';}
             sQry +=' AND ' +'ETL_Record_Deleted__c=false';
             //Per guidelines added limit to the query to restrict results to 100 records
             sQry = sQry + ' Limit ' + Integer.valueOf(HUMSearchLimit__c.getInstance('Member_SearchLimit').Search_Limit__c);
             lstAccountList = Database.Query(sQry);
             
             
             //watson
             if(!lstAccountList.isempty()){
             
                  prepareMapWithExtrnlId(interactionwrapObj.sGroupID,lstAccountList[0].Account_External_ID__c,sinteractingType);
                  accountsMap.put(interactingType,lstAccountList[0]);
                  blnisCRMgrp = true;
                  lstAccountList = new List<Account>(); 
             }else{
                 
                 blnisCRMgrp = false;
				  if(ccpOnOff)
                 {
                 // Group number is not find in salesforce , so as per the requirement we are stop invoking the GroupSearchService. and it creates a new group account.
				
                 }
                 else
                 {
                 invokeGroupService();                
                 accountsMap = mapRecordData;
                 }
                 
                 Account acc = new Account();
                 acc = accountsMap.get(interactingType );
                 
                 
                 if(acc != NULL){ 
                     prepareMapWithExtrnlId(interactionwrapObj.sGroupID,acc.Account_External_ID__c,sinteractingType);
                     
                     insert acc;      
                     accountsMap.put(interactingType,acc);
                     bIsGroupService = true;
                 }
				 
				 
                 
                
                
             }
             
              if(accountsMap!=null && !accountsMap.values().isEmpty() && accountsMap.containsKey(interactingType))
              {
                    bisMultipleAcct.put(interactingType,false);
              }
              else{
              //handle unknown group code
                blninsertunknowngrp = true;  
              }
             
            }
    
        return accountsMap;                       
    }
    
     /* Method Name   :  invokeGroupService-     
     * Description   :   Continuation method to invoke the Group WebService callout
     * Return Type   :   Continuation     
     * Parameters    :   NA   
     */
    public void invokeGroupService()
    {  
        
        responseDTO = null;
         
        try
        {            
            Integer TIMEOUT_INT_SECS = Integer.ValueOf(objWebserviceCallout.Timeout__c)/1000;               
            
            if(objWebserviceCallout != null && objWebserviceCallout.Active__c)
            {
                HUMGroupSearchServiceHelper.RequestGroupSearch requestGroupSearch = new HUMGroupSearchServiceHelper.RequestGroupSearch();           
                requestGroupSearch.GroupID = oSearchWrapper.sGroupNumber;   
                requestGroupSearch.PlatformCode = oSearchWrapper.sSourcePlatformCode;                
                HUMGroupSearchService.Soap requestService = new HUMGroupSearchService.Soap();
                requestService.endpoint_x = objWebserviceCallout.End_Point_URL__c;                
                requestService.clientCertName_x = objWebserviceCallout.Certificate_Name__c;
                requestService.timeout_x = Integer.valueOf(objWebserviceCallout.Timeout__c);                
                responseDTO = requestService.GroupSearch(requestGroupSearch);  
                
                mapRecordData = processResponse();                   
                
            }            
        }
        catch(Exception e)
        {          
            bIsGroupError = true;
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            lstLogErrors= HUMExceptionHelper.logErrors(e,'HUMGroupSearchControllerV2','invokeGroupService');
            HUMExceptionHelper.bShowErrorOnPage = true;
        }
        
    } 
    
     /* Method Name   : processResponse     
    * Description   :   Callback method for the GroupWebService Callout
    * Return Type   :   Object  
    * Parameters    :   NA      
    */ 
    public Map<String,Account> processResponse() 
    {
        try
        {
            Map<String,Account> mapAcc = new Map<String,Account>();  
            integer iKey = 0; 
            
            if(responsedto != null && !responsedto.IsEmpty() )              
            {                        
                bExternalResults = true;                      
                for(HUMGroupSearchServiceHelper.ResponseGroupSearch extAccountRecord:responsedto)                  
                {                                          
                    mapAcc.put(interactingType,populateGroupInfoOnAccount(extAccountRecord));                                                                          
                }  
                mapRecordData = mapAcc;              
            }
            //If no search results are returned both from integration and salesforce, error message is shown to the user 
            if(mapRecordData.isEmpty())
            {
                throw new HUMCustomException(System.Label.HUMGroupSearchNoResultFound);
            }            
                
        }        
        catch(Exception e)
        {
            bIsGroupError = true;
            HUMExceptionHelper.logErrors(e, 'HUMGroupSearchControllerV2', 'processResponse'); 
            HUMExceptionHelper.bRestrictLog =false;
 
        }
  
        return mapRecordData;
    }
    
    /* Method Name   :   populateGroupInfoOnAccount-     
    * Description   :   Populates Group Information on Account Object
    * Return Type   :   Account  
    * Parameters    :   HUMIntGroupSearchServiceHelper.ResponseGroupSearch      
    */              
    private Account populateGroupInfoOnAccount(HUMGroupSearchServiceHelper.ResponseGroupSearch extAccountRecord)    
    {        
        Account acc = new Account(recordTypeId = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),HUMConstants.HUM_Group_Account_Record_Type), 
                                   Name = extAccountRecord.GroupName,    
                                   Group_Number__c = extAccountRecord.GroupID,                               
                                   Phone = extAccountRecord.Phone,                                
                                   
                                   Source_Platform_Code__c = extAccountRecord.Platform,                                                  
                                   Source_Customer_Key__c =  extAccountRecord.Numeric_GroupID,
                                   Account_External_ID__c = (extAccountRecord.Platform == HUMConstants.sFMValue)? HUMConstants.sGRPValue +HumConstants.Seperator+HUMConstants.TENANTID_GEN+HumConstants.Seperator+HUMConstants.sFMValue+HumConstants.Seperator+ extAccountRecord.CustomerGenKey : HUMConstants.sGRPValue+HumConstants.Seperator+ HUMConstants.TENANTID_GEN+HumConstants.Seperator+HUMConstants.sODSValue+HumConstants.Seperator+ extAccountRecord.CustomerGenKey,
                                   BillingStreet = extAccountRecord.Address,                                
                                   BillingStateCode = oSearchWrapper.sState == HUMConstants.HUMStateDefaultValue? '': oSearchWrapper.sState,                                
                                   General_Account__c = true, Tenant_Id__c = HUMConstants.TENANTID_GEN);                    
                                   return acc;     
    }
    
   
             
    /**
    * Method to create a map with key as externalid
    * Method Name : prepareMapWithExtrnlId
    * @param      : String snpiId, String sTaxId, String sExternalId, String interactingType
    * @return     : void
    */
    public static void prepareMapWithExtrnlId(String sGroupID, String sExternalId, String interactingType)
    {
        if(String.isNotBlank(sGroupID))
        {
            if(mapReturnValues.containsKey(interactingType))
            {
                mapReturnValues.get(interactingType).put(sExternalId,sGroupID);
            }
            else
            {
                mapReturnValues.put(interactingType, new Map<String,String>{sExternalId=>sGroupID});
            }
        }        
    }
    
}