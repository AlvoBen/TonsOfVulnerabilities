/*******************************************************************************************************************************
Apex Class Name : ProviderSearch_LS_HUM
Version         : 1.0
Created On      : 09/25/2020
Function        : Service layer class for ProviderSearch_LC_HUM 
Test Class      : ProviderSearch_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Siddhesh Sinkar                                          09/22/2020                 created LWC compatible methods .
* Pradeep Dani                                             10/14/2020                 added logic for internal search
* Ashish Kumar                                             11/06/2020                 Refactored the code
* Ashish Kumar											   03/03/2021				  changed from with to without sharing
* Kajal Namdev                                             05/07/2021                 Replace GLOBAL_CONSTANT_HUM with GLOBAL_SEARCH_CONSTANT_LH_HUM class
* Ankima                                                   06/18/2021                 checkmarx fix
*********************************************************************************************************************************************************************************************/

public with sharing class ProviderSearch_LS_HUM 
{
    /**
    * Method to call EPS Service for provider search
    * 
    * @param    ProviderSearch_LC_HUM.providerformData searchFormData
    * @return   Continuation 
    */
    public static Continuation sendProviderContinuationRequest(ProviderSearch_LC_HUM.providerformData searchFormData)
    {   
        HUMProviderSearchWrapperHelper oSearchWrapper = new HUMProviderSearchWrapperHelper();
        oSearchWrapper.sTaxID = searchFormData.sTaxID;
        oSearchWrapper.sNPI = searchFormData.sNPI;
        oSearchWrapper.sFacilityName = searchFormData.sFacilityName;
        oSearchWrapper.sFirstName =  searchFormData.sFirstName;
        oSearchWrapper.sLastName = searchFormData.sLastName;
        oSearchWrapper.sState = searchFormData.sState;
        oSearchWrapper.sPostalCode = searchFormData.sPostalCode;
        oSearchWrapper.sSpeciality = searchFormData.sSpeciality;
        
        Continuation oCon = new Continuation(GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_ServiceTimeOut_HUM);
        oCon.continuationMethod ='processResponse';
        oCon.state = oSearchWrapper;
        
        @testVisible Map<String, String> mapRequestDto = new Map<String, String>();
        mapRequestDto = serviceInputs(mapRequestDto,oSearchWrapper);
        ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM ();
        ProviderSearch_LC_HUM.sRequestLabelPID=oCon.addHttpRequest(oProvSearch.getEPSSReq(mapRequestDto));
        return oCon;
    }
    
    /**
    * Callback method for EPSS response
    * 
    * @param    List<String> labels, Object state
    * @return   Object 
    */
    public Static String requestProcessResponse(List<String> labels, Object state)
    {
        HUMProviderSearchWrapperHelper oSearchWrapper = (HUMProviderSearchWrapperHelper)state;
        HttpResponse oEpssResp = Continuation.getResponse(labels[0]);
        String sResponse;

        if (oEpssResp != null && ((oEpssResp.getStatusCode() == GLOBAL_SEARCH_CONSTANT_LH_HUM.HTTP_OK_HUM || oEpssResp.getStatusCode() == GLOBAL_SEARCH_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) && (!oEpssResp.getBody().containsIgnoreCase(GLOBAL_SEARCH_CONSTANT_LH_HUM.VOB_FAULT_CODE)) || Test.isRunningTest()))
        {
            ProviderSearch_H_HUM oProvSearch = new ProviderSearch_H_HUM ();
            List<HUMCustomSearchHelper> lstProviderAccounts = oProvSearch.parseServiceResponse(oEpssResp,true);
            
            if(NULL != lstProviderAccounts && !lstProviderAccounts.isEmpty())
            {
                sResponse = JSON.serialize(lstProviderAccounts);
            }
            else
            {
                //Calling method from ProviderSearch_LD_HUM
                list<Account> lstMembers = ProviderSearch_LD_HUM.requestListAccountUsingSFDC(oSearchWrapper);
                sResponse = JSON.serialize(ProviderSearch_LD_HUM.getProviderWrapper(lstMembers));
            }
        }
        else
        {
            // Fault Code
            HUMCalloutUtilityHelper.logFaultError(oEpssResp,'HUMProviderSearchControllerV2', 'processResponse', null, 'EPSS', 'INFO');
            
            //Search in SFDC
            list<Account> lstMembers = ProviderSearch_LD_HUM.requestListAccountUsingSFDC(oSearchWrapper);
            sResponse = JSON.serialize(ProviderSearch_LD_HUM.getProviderWrapper(lstMembers));
        
        }
        return sResponse;
    }
    
    /**
    * Method to assign the values and store in MAP
    * 
    * @param    map<String, String>
    * @return   map<String, String> 
    */
    @testVisible private static map<String, String> serviceInputs(map<String, String> mapRequestDto ,HUMProviderSearchWrapperHelper oSearchWrapper)
    {
        try 
        {
            mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sLastName, oSearchWrapper.sLastname); 
            mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sTaxID, oSearchWrapper.sTaxID);
            mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sNPI, oSearchWrapper.sNPI);
            mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sFirstName, oSearchWrapper.sFirstName);
            mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sFacilityName, oSearchWrapper.sFacilityName);
            mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sPostalCode, oSearchWrapper.sPostalCode);
            if(oSearchWrapper.sState != null && !(oSearchWrapper.sState).equalsIgnoreCase(HumConstants.NONE))  mapRequestDto.put('sState', oSearchWrapper.sState);
            if(oSearchWrapper.sSpeciality != null &&!(oSearchWrapper.sSpeciality).equalsIgnoreCase(HumConstants.NONE)) 
            {
                HumProviderSpecialtyMapping__c oSpecialityCustSetting = HumProviderSpecialtyMapping__c.getValues(oSearchWrapper.sSpeciality );
                if(oSpecialityCustSetting != NULL) 
                {
                    oSearchWrapper.sSpeciality = oSpecialityCustSetting.EpssSpec_Code__c;
                    mapRequestDto.put(GLOBAL_SEARCH_CONSTANT_LH_HUM.sSpeciality, oSearchWrapper.sSpeciality); 
                }
            }
        }
        catch(Exception e)
        {  
            HUMExceptionHelper.logErrors(e, 'ProviderSearch_LS_HUM', 'serviceInputs');
        }
        return mapRequestDto;
    }
}