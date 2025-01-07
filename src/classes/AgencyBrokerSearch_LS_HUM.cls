/*******************************************************************************************************************************
Apex Class Name : AgencyBrokerSearch_LS_HUM
Version         : 1.0
Created On      : 11/2/2020
Function        : This class is used to fetch results from Web Service Callout
Test Class      : 

Modification Log: 
 * Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*  1.0                        Rahul Krishan                                          09/28/2020                Original Version
*  2.0                        Ashish Kumar                                           11/24/2020                Refactored the code
*  3.0                        Kajal Namdev                                           05/07/2021                Replace HUMConstants with GLOBAL_SEARCH_CONSTANT_LH_HUM class
*  4.0                        Kajal Namdev                                           11/16/2021                coverage increase
**********************************************************************************************************************************/

public class AgencyBrokerSearch_LS_HUM {

    @testvisible private static String sClassName ='AgencyBrokerSearch_LS_HUM';
    @testvisible private static String sMethodName = ''; 
    @testvisible private static String continuationLabel;
    @testVisible private static transient boolean bIsAgencyError{get;set;}
    @testvisible private static List<Account> lstSObjects = new List<Account>();
    
    /*  
     * Method name : createAccountList
     * Description : This method will be called response is havingaccount data
     * Return Type : Object
     * Parameter   : HUMAgencySearchService.ArrayOfProducer 
     */
    
    public static AgencyBrokerSearch_LC_HUM.wrapperclass  createAccountList(HUMAgencySearchService.ArrayOfProducer agencyInfoList,AgencyBrokerSearch_DTO_HUM oSearchWrapper)
    {
        List<AgencyBrokerSearch_LC_HUM.wrapperclass> lstAgencyWrapper = new List<AgencyBrokerSearch_LC_HUM.wrapperclass>();
        try
        {
            sMethodName = 'createAccountList';
            Integer i=0;               
            Map<String,Account> mapAcc = new Map<String,Account>();
            Map<String,Account> mapofOrginAcc = new Map<String,Account>();
            Map<Integer,List<ConsumerID__c>> mapConsumerID = new Map<Integer,List<ConsumerID__c>>();
            HUMAgencySearchService.Producer agencyInfo = null;  
            Account acc = null;
            Map<Integer,String> mapConsumerTaxIds = new Map<Integer,String>();  
            for(integer j = 0; j < agencyInfoList.Producer.size(); j++)
            {
                agencyInfo =  agencyInfoList.Producer[j]; 
                if(((oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue1 
                        && agencyInfo.ProducerType != GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2)
                        || oSearchWrapper.sAgentType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2)
                        && ((String.isBlank(oSearchWrapper.sState) || oSearchWrapper.sState == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMStateDefaultValue) ||agencyInfo.StateCode == oSearchWrapper.sState))
                {
                    acc = mapAccountfields(agencyInfo);
                    if(acc != null)
                    {                               
                        mapofOrginAcc.put(agencyInfo.SysAssgnNbr,acc);
                        mapAcc.put(agencyInfo.TaxNbr,acc);
                        i++;
                    }
                }
            }
            List<String> lstTaxIds = new List<String>();
            String strextId;
            Map<String,List<string>> mapOfaccIdTaxIds = new Map<String,List<string>>();
            for(String str : mapAcc.keyset())
            {             
                strextId = mapAcc.get(str).Account_External_ID__c.replace('&#124;','|');
                lstTaxIds = mapOfaccIdTaxIds.get(strextId);
                if(lstTaxIds == null || lstTaxIds.isEmpty())
                {
                    mapOfaccIdTaxIds.put(strextId,new List<String>{str});
                }                 
                else {
                    lstTaxIds.add(str);
                }    
            }
            AgencyBrokerSearch_LC_HUM.wrapperclass objWrapperclass = new AgencyBrokerSearch_LC_HUM.wrapperclass();
            objWrapperclass.lstSObjects = mapofOrginAcc.values();
            objWrapperclass.sTaxIds = JSON.serialize(mapOfaccIdTaxIds);
            return objWrapperclass;
        }
        catch(Exception e)
        {            
            bIsAgencyError = true;
            HUMExceptionHelper.logErrors(e,sClassName,sMethodName);
            return null;
        } 
    }

    /*Method Name   :    mapAccountfields -     
     * Description   :   Creates an account object by mapping fields from integration result
     * Return Type   :   Account  
     * Parameters    :   HUMIntAgencySearchService.Producer     
     */
    
    public static Account mapAccountfields(HUMAgencySearchService.Producer agencyInfo)
    {
            Date dHireDate =  ((agencyInfo.HireDate != null) && (agencyInfo.HireDate.year() > GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMMaxDateYear) 
                                            || (agencyInfo.HireDate.year() < GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMMinDateYear)) ? null : agencyInfo.HireDate.date();                
            Date dBaseDate =  ((agencyInfo.BaseDate != null) && (agencyInfo.BaseDate.year() > GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMMaxDateYear) 
                                            || (agencyInfo.BaseDate.year() < GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMMinDateYear)) ? null : agencyInfo.BaseDate.date();
            
            String sName = String.isBlank(agencyInfo.Name)? agencyInfo.FirstName + ' '+ agencyInfo.LastName : agencyInfo.Name;
            Account acc = populateAgencyInfoOnAccount(agencyInfo);           
            acc.Producer_Base_Date__c = dBaseDate;
            acc.Producer_Hire_Date__c = dHireDate;  
            acc.Name = sName;                
            return acc;                     
    }
    
    /*Method Name   :   populateAgencyInfoOnAccount -     
    * Description   :   Populates Agency Information on Account Object
    * Return Type   :   Account  
    * Parameters    :   HUMIntAgencySearchService.Producer      
    */
    
 @testVisible private static Account populateAgencyInfoOnAccount(HUMAgencySearchService.Producer agencyInfo)
    {
        Account acc = new Account(recordTypeId = HUMUtilityHelper.getRecordTypeID(Account.getsobjectType(),GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgentBrokerRecordTypeName),
                                Individual_First_Name__c = agencyInfo.FirstName,Individual_Last_Name__c = agencyInfo.LastName,
                                Agent_ID__c = agencyInfo.SysAssgnNbr,
                                Agent_Type__c = agencyInfo.ProducerType == GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue2?agencyInfo.ProducerType:GLOBAL_SEARCH_CONSTANT_LH_HUM.HUMAgencySearchAgentTypeValue3,
                                Phone = agencyInfo.PrimaryPhone,
                                Fax = agencyInfo.FaxNbr,Work_Email__c = agencyInfo.EmailId,
                                Producer_Termed__c = agencyInfo.IsTermed,
                                DBA__c = agencyInfo.Dba,
                                Gender__c = agencyInfo.Gender,Producer_Status__c = agencyInfo.Status,
                                Birthdate__c = agencyInfo.DateOfBirth != null ? String.ValueOf(agencyInfo.DateOfBirth.format('MM/dd/yyyy')) : null,
                                Producer_Status_Date__c = agencyInfo.StatusDate.date(),
                                Name_Suffix__c = agencyInfo.SuffixName,Middle_Name__c = agencyInfo.MiddleInitialName,
                                Enterprise_ID__c = agencyInfo.SysAssgnNbr,                    
                                Account_External_ID__c = GLOBAL_SEARCH_CONSTANT_LH_HUM.EXT_ID_AGENT+ agencyInfo.SysAssgnNbr,                                 
                                BillingStreet = agencyInfo.BusinessAddress.AddressLine1, 
                                BillingCity = agencyInfo.BusinessAddress.CityName,
                                BillingPostalCode = agencyInfo.BusinessAddress.ZipCode,
                                ShippingStreet = agencyInfo.ShippingAddress.AddressLine1, ShippingCity = agencyInfo.ShippingAddress.CityName,
                                ShippingPostalCode = agencyInfo.ShippingAddress.ZipCode, ShippingStateCode = agencyInfo.ShippingAddress.StateCode,
                                BillingStateCode = agencyInfo.StateCode,General_Account__c = true, Tenant_Id__c = GLOBAL_SEARCH_CONSTANT_LH_HUM.TENANTID_GEN); 
         return acc;
     }
    
}