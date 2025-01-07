/*******************************************************************************************************************************
Apex Class Name : HUMSearchEPSSService
Version         : 1.0
Created On      : 03/06/2014
Function        : This class contains methods of WSDL Class and Main HumProviderSearchcontroller
                  It acts as an Intermediate  between HUMProviderSearchController and HUMProvider_esHumanaComEPSS-WSDL controller and used for sending the 
                  Request to EPSS and response to HUMProviderSearchController of Providersearch.
Test Class      : HUMSearchEPSSServiceTest                  
                  
Modification Log: 
* Developer Name               Code Review                         Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*Ranjeeth Nagishetty             17123                         04/01/2014                 Original Version
*Ranjeeth Nagishetty             17534                         08/20/2014                 Fixed the Defect Related to State to Defectno:149217
*Ranjeeth Nagishetty             17722                         09/01/2014                 Worked new  Change Request on tax ID
*Ranjeeth Nagishetty             17885                         09/11/2014                 Changed the Custom setting value Impact
*RanjeetN Nagishetty             18042                         09/19/2014                 Defect :152775
*Ranjeeth Nagishetty             18381                         10/08/2014                 Defect :157082
*Ranjeeth Nagishetty             19161                         11/05/2014                 Zipcode Length  should be 5 characters  as per REQ - 63682
*Ranjeeth Nagishetty             19484                         11/24/2014                 Defect :162135
*******************************************************************************************************************************/

Public with sharing class HUMSearchEPSSService
{
    public List<HUMCustomSearchHelper> lstWrpProvider{ get; set; }

    /* Method Name  :    humSearchInputs()  
    * Description   :    Forming the request to send to searchEPSS Method
    * Parameters    :    Map<String, String> 
    * Return Type   :    HUMProvider_esHumanaComEPSS.SearchRequestDto 
    */
    public HUMProvider_esHumanaComEPSS.SearchRequestDto humSearchInputs(Map<String, String> mapRequest)
    { 
        /*************************************APPLICATION KEY*******************************************************/
        HUMProvider_esHumanaComEPSS.SearchRequestDto oSearchRequestDto = new HUMProvider_esHumanaComEPSS.SearchRequestDto();
        oSearchRequestDto.ApplicationKey = Label.HumSearchEPSSServiceApplicationKey;
        HUMProvider_esHumanaComEPSS.AddressCriteriaDto oAddressCriteriaDTO = new HUMProvider_esHumanaComEPSS.AddressCriteriaDto();
        HUMProvider_esHumanaComEPSS.SearchCriteriaDto oSearchCriteriaDTO = new HUMProvider_esHumanaComEPSS.SearchCriteriaDto();
        HUMProvider_esHumanaComEPSS.FiltersDto oFilterDTO = new HUMProvider_esHumanaComEPSS.FiltersDto();
        /***********************************************************************************************************/
        
        /***********************************NPIID AND TaxID,FIRSTNAME, LASTNAME, SPECIALITY, Facility Name &Group,ADDRESS******************************************************/
        
        oSearchCriteriaDTO=humSearchNPIidTaxidInputs(oSearchCriteriaDTO,oFilterDTO,mapRequest);
        oSearchCriteriaDTO=humSearchNameSpecialityFacilityInputs(oSearchCriteriaDTO,oFilterDTO,mapRequest);
        oSearchCriteriaDTO=humSearchAddressInputs(oAddressCriteriaDTO,oSearchCriteriaDTO,oFilterDTO,mapRequest);
        
        /**********************************************************************************************************************************/
        
        oSearchRequestDto.SearchCriteria = oSearchCriteriaDTO;
        return oSearchRequestDto;
    }
    
    /*Method Name   :    humSearchNPIidTaxidInputs()  
    * Description   :    Forming the request with NPIid and Taxid to send to searchEPSS method.
    * Parameters    :    HUMProvider_esHumanaComEPSS.SearchCriteriaDto,HUMProvider_esHumanaComEPSS.FiltersDto,Map<String, String> 
    * Return Type   :    HUMProvider_esHumanaComEPSS.SearchCriteriaDto
    */
    private HUMProvider_esHumanaComEPSS.SearchCriteriaDto humSearchNPIidTaxidInputs(HUMProvider_esHumanaComEPSS.SearchCriteriaDto oSearchCriteriaDTO,
                                HUMProvider_esHumanaComEPSS.FiltersDto oFilterDTO,Map<String, String> mapRequest)
    {
            if(String.isEmpty(mapRequest.get('sNPI')) && String.isEmpty(mapRequest.get('sTaxID')) )
            {
                return oSearchCriteriaDTO;
            }
            List<HUMProvider_esHumanaComEPSS.IdentifierDto> lstIDDTO = new  List<HUMProvider_esHumanaComEPSS.IdentifierDto>();
            List<HUMProvider_esHumanaComEPSS.IdentifierListDto> lstIdLstDto = new List<HUMProvider_esHumanaComEPSS.IdentifierListDto>();
            HUMProvider_esHumanaComEPSS.IdentifierListDto oIdlstDTO = new HUMProvider_esHumanaComEPSS.IdentifierListDto();
            HUMProvider_esHumanaComEPSS.IdentifierDto oIdDTO = new HUMProvider_esHumanaComEPSS.IdentifierDto();
            
            if(!String.isEmpty(mapRequest.get('sNPI')))
            {
                oIdlstDTO.IdentifierType = Label.HumServiceNPItag;
                oIdDTO.ID = mapRequest.get('sNPI');
                oFilterDTO.ExactMatch = Label.HumServiceNPItag;
                oFilterDTO.ParOnly = false;
                lstIDDTO.add(oIdDTO);  
                oIdlstDTO.Identifiers = lstIDDTO ;
                lstIdLstDto.add(oIdlstDTO);
                oSearchCriteriaDTO .Identifiers = lstIdLstDto;
                oSearchCriteriaDTO.Filter = oFilterDTO;
            } 
            else if(!String.isEmpty(mapRequest.get('sTaxID')))
            {
                oIdlstDTO.IdentifierType = Label.HumServiceTaxIDtag;
                oIdDTO.ID = mapRequest.get('sTaxID');
                lstIDDTO.add(oIdDTO);  
                oIdlstDTO.Identifiers = lstIDDTO ;
                lstIdLstDto.add(oIdlstDTO);
                oSearchCriteriaDTO .Identifiers = lstIdLstDto;
            }
        return oSearchCriteriaDTO;
    }
    
    /*Method Name   :    humSearchNameSpecialityFacilityInputs  
    * Description   :    Forming the request with Name ,Speciality and Facility details to send to searchEPSS method.
    * Parameters    :    HUMProvider_esHumanaComEPSS.SearchCriteriaDto,HUMProvider_esHumanaComEPSS.FiltersDto,Map<String, String> 
    * Return Type   :    HUMProvider_esHumanaComEPSS.SearchCriteriaDto
    */
    private HUMProvider_esHumanaComEPSS.SearchCriteriaDto humSearchNameSpecialityFacilityInputs(HUMProvider_esHumanaComEPSS.SearchCriteriaDto oSearchCriteriaDTO,
                                HUMProvider_esHumanaComEPSS.FiltersDto oFilterDTO,Map<String, String> mapRequest)
    {
        /***********************************FIRSTNAME LASTNAME*********************************************************************************/
        if(!String.isEmpty(mapRequest.get('sFirstName')) && !String.isEmpty(mapRequest.get('sLastName')) ) 
        {
            oSearchCriteriaDTO.ProviderName = mapRequest.get('sFirstName') + ' ' + mapRequest.get('sLastName');
            oFilterDTO.Classification = Label.HumserviceIndividualtag;
            oFilterDTO.ParOnly = false;
            oSearchCriteriaDTO.Filter = oFilterDTO;
        }
        /**********************************************************************************************************************************/
        
        /****************************************SPECIALITY*******************************************************************************/
        if(!String.isEmpty(mapRequest.get('sSpeciality'))) 
        {
            oFilterDTO.Specialties=  new string[]{mapRequest.get('sSpeciality')};
            oSearchCriteriaDTO.Filter = oFilterDTO;
        }
        /*****************************************************************************************************************************/
        
        /*********************************************Facility Name &Group ************************************************************************/
        if(!String.isEmpty(mapRequest.get('sFacilityName'))) 
        {    
          oSearchCriteriaDTO.ProviderName = mapRequest.get('sFacilityName');
          oFilterDTO.Classification = Label.HumproviderGroup + ' ' + Label.HumServiceFacilitytag;
          oFilterDTO.ParOnly = false;
          oSearchCriteriaDTO.Filter = oFilterDTO;
        }
        /*****************************************************************************************************************************/
        return oSearchCriteriaDTO;
    }
    
    /*Method Name   :    humSearchAddressInputs  
    * Description   :    Forming the request with Address details to send to searchEPSS method.
    * Parameters    :    HUMProvider_esHumanaComEPSS.AddressCriteriaDto,HUMProvider_esHumanaComEPSS.SearchCriteriaDto,HUMProvider_esHumanaComEPSS.FiltersDto,Map<String, String> 
    * Return Type   :    HUMProvider_esHumanaComEPSS.SearchCriteriaDto 
    */
    private HUMProvider_esHumanaComEPSS.SearchCriteriaDto humSearchAddressInputs(HUMProvider_esHumanaComEPSS.AddressCriteriaDto oAddressCriteriaDTO,
                                HUMProvider_esHumanaComEPSS.SearchCriteriaDto oSearchCriteriaDTO,HUMProvider_esHumanaComEPSS.FiltersDto oFilterDTO,Map<String, String> mapRequest)
    {
        //To fix defect 149217, sending state value in service input filter criteria.
        if(!String.isEmpty(mapRequest.get('sState'))) 
        {
           oAddressCriteriaDTO.State = mapRequest.get('sState');
           oFilterDTO.UsaStates = new string[]{oAddressCriteriaDTO.State};
           
        }
        if(!string.isEmpty(mapRequest.get('sPostalCode'))) 
        {
           oAddressCriteriaDTO.Zip = mapRequest.get('sPostalCode');
           oFilterDTO.ExactMatch = Label.HumproviderServiceZip;
        }
        if (!String.isEmpty(mapRequest.get('sPostalCode')) || !String.isEmpty(mapRequest.get('sState')))  
        {
            oAddressCriteriaDTO.AddressType = Label.HumServiceAddresstype;
            oSearchCriteriaDTO.Address = oAddressCriteriaDTO;
        }
        return oSearchCriteriaDTO;
    }
    
    /* Method Name  :    searchEPSS   
    * Description   :    To display the records in result section sending request to EPSS.
    * Parameters    :    Map<String, String>
    * Return Type   :    List<HUMCustomSearchHelper> 
    */
    public List<HUMCustomSearchHelper> searchEPSS(Map<String, String> mapRequest)
    {
        HUMProvider_esHumanaComEPSS.SearchRequestDto oSearchRequestDto = humSearchInputs(mapRequest);
        HUM_Webservice_Callout__c oProviderSvcAuth = HUM_Webservice_Callout__c.getValues('ProviderSearchService');
        HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort oEPSSProvider = new HUMProvider_esHumanaComEPSS.EnterpriseProviderSearchServicePort();
        oEPSSProvider.clientCertName_x = oProviderSvcAuth.Certificate_Name__c; 
        oEPSSProvider.endpoint_x = oProviderSvcAuth.End_Point_URL__c;
        oEPSSProvider.timeout_x = Integer.valueOf(oProviderSvcAuth.Timeout__c);
        HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto;
        try 
        {        
            oSearchResponseDto = oEPSSProvider.SearchProvider(oSearchRequestDto);
            lstWrpProvider = HUMServiceResponse(oSearchResponseDto);
            return lstWrpProvider;        
        }
        catch(exception ex)
        {
            HUMExceptionHelper.bShowErrorOnPage = false;
            HUMExceptionHelper.bRestrictLog = true;
            HUMExceptionHelper.bShowErrorOnPage = true;
            HUMExceptionHelper.logErrors(ex, 'HUMSearchEPSSService', 'searchEPSS');
            return null;
        }
    }  
     
    /*Method Name   :    HUMServiceResponse  
    * Description   :    Parsing the Response and throws an error 
    * Parameters    :    HUMProvider_esHumanaComEPSS.SearchResponseDto
    * Return Type   :    List<HUMCustomSearchHelper>
    */
    public List<HUMCustomSearchHelper> HUMServiceResponse(HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto)
    {
        HUMProvider_esHumanaComEPSS.ResponseMessageDto oHUMRespMessageDTO = oSearchResponseDto.Message;
        
        if(oHUMRespMessageDTO.isError)  return null;
        
        lstWrpProvider = new List<HUMCustomSearchHelper>();
        HUMCustomSearchHelper oSearchWrpProvider;
        
        //Defect 152775 Error Message to Filter Criteria From service
        
        if(oHUMRespMessageDTO.IsWarning)
        {
            oSearchWrpProvider = new HUMCustomSearchHelper();
            oSearchWrpProvider.sFirstname='';
            HUMProvider_esHumanaComEPSS.WarningMessageDto[] arrWarnings=oHUMRespMessageDTO.Warnings;
            for(integer i = 0; i < arrWarnings.size(); i++)
            {
               oSearchWrpProvider.sFirstname=arrWarnings[i].Description;
            }
            if(oSearchWrpProvider.sFirstname==System.Label.HUMProviderFilterWarning)
            {
                lstWrpProvider.add(oSearchWrpProvider);
                return lstWrpProvider;
            }
            else
            {
                return null;
            }
        }
        return getProviderDetailsFromServiceResponse(oSearchResponseDto,lstWrpProvider,oSearchWrpProvider);
    }
       
   /*Method Name    :    getProviderDetailsFromServiceResponse  
    * Description   :    Parsing the Response and storing in wrapper class.
    * Parameters    :    HUMProvider_esHumanaComEPSS.SearchResponseDto,List<HUMCustomSearchHelper>,HUMCustomSearchHelper
    * Return Type   :    List<HUMCustomSearchHelper>
    */
     public List<HUMCustomSearchHelper> getProviderDetailsFromServiceResponse(HUMProvider_esHumanaComEPSS.SearchResponseDto oSearchResponseDto,
                                    List<HUMCustomSearchHelper> lstWrpProvider,HUMCustomSearchHelper oSearchWrpProvider)
     {
        HUMProvider_esHumanaComEPSS.EnterpriseProviderDto[] arrHUMEnterpriseProviders = oSearchResponseDto.EnterpriseProviders;
        
        if(arrHUMEnterpriseProviders != NULL)
        {
            for(integer i = 0; i < arrHUMEnterpriseProviders.size(); i++)
            {
                oSearchWrpProvider = new HUMCustomSearchHelper();
                if(arrHUMEnterpriseProviders[i].Demographics != NULL) 
                {
                    oSearchWrpProvider.sBirthdate = string.valueof(arrHUMEnterpriseProviders[i].Demographics.DateOfBirth);
                    oSearchWrpProvider.sGender  = arrHUMEnterpriseProviders[i].Demographics.Gender;
                    oSearchWrpProvider.sDegree = arrHUMEnterpriseProviders[i].Demographics.Degree;
                }
                
                if(arrHUMEnterpriseProviders[i].ProviderName != NULL) 
                {
                    oSearchWrpProvider.sFirstname = arrHUMEnterpriseProviders[i].ProviderName.First;
                    oSearchWrpProvider.sLastname  = arrHUMEnterpriseProviders[i].ProviderName.Last;
                }
                
                if(arrHUMEnterpriseProviders[i].DbaName != NULL)    oSearchWrpProvider.sDBA = arrHUMEnterpriseProviders[i].DbaName;
                if(arrHUMEnterpriseProviders[i].UniqueID != NULL)   oSearchWrpProvider.sPlatform = arrHUMEnterpriseProviders[i].UniqueID.Source;
                
                oSearchWrpProvider = HUMServiceResponseReadUidEidAddresses(oSearchWrpProvider,arrHUMEnterpriseProviders[i]);
                oSearchWrpProvider = HUMServiceResponseReadSpecialtiesTaxonomy(oSearchWrpProvider,arrHUMEnterpriseProviders[i]);
                oSearchWrpProvider = HUMServiceResponseAddresses(oSearchWrpProvider,arrHUMEnterpriseProviders[i]);
                
                lstWrpProvider.add(oSearchWrpProvider);
            }
        }
        return lstWrpProvider;
     }
    
    /*Method Name   :    HUMServiceResponseReadUidEidAddresses  
    * Description   :    Parsing UniqueID, and EnterpriseID response and storing in wrapper class. 
    * Parameters    :    HUMCustomSearchHelper,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto
    * Return Type   :    HUMCustomSearchHelper
    */
    public HUMCustomSearchHelper HUMServiceResponseReadUidEidAddresses(HUMCustomSearchHelper oSearchWrpProvider,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto  arrHUMEnterpriseProviders)
    {
        String stenanId = label.HUMExtMemAccountTenantID; 
        if(arrHUMEnterpriseProviders.EnterpriseID != NULL)
        { 
            oSearchWrpProvider.sExtID = String.valueOf(arrHUMEnterpriseProviders.EnterpriseID);
            oSearchWrpProvider.sEnterpriseID = oSearchWrpProvider.sExtID;
            oSearchWrpProvider.sExtID = Label.HumProviderUnique+'|'+ stenanId + '|'+oSearchWrpProvider.sExtID;
        }
        
        if(!String.isBlank(arrHUMEnterpriseProviders.Classification) && arrHUMEnterpriseProviders.Classification.length() > 0)
        oSearchWrpProvider.sClassification = arrHUMEnterpriseProviders.Classification.substring(0,1);
        return oSearchWrpProvider;
    }  
    
    /*Method Name   :    HUMServiceResponseAddresses  
    * Description   :    Parsing Address and storing in wrapper class.
    * Parameters    :    HUMCustomSearchHelper,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto.
    * Return Type   :    HUMCustomSearchHelper
    */
    public HUMCustomSearchHelper HUMServiceResponseAddresses(HUMCustomSearchHelper oSearchWrpProvider,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto  arrHUMEnterpriseProviders)
    {
         if(arrHUMEnterpriseProviders.Demographics.Addresses != NULL)
         {
            for(Integer j = 0; j < arrHUMEnterpriseProviders.Demographics.Addresses.size(); j++)
            {
                if(arrHUMEnterpriseProviders.Demographics.Addresses[j].AddressType == Label.HUMBillingAddresstype)
                {
                    oSearchWrpProvider.sAddress = (arrHUMEnterpriseProviders.Demographics.Addresses[j].Line1 != NULL) ? arrHUMEnterpriseProviders.Demographics.Addresses[j].Line1 : '';
                    oSearchWrpProvider.sAddress += (arrHUMEnterpriseProviders.Demographics.Addresses[j].Line2 != NULL) ? arrHUMEnterpriseProviders.Demographics.Addresses[j].Line2 : '';
                    oSearchWrpProvider.sState = arrHUMEnterpriseProviders.Demographics.Addresses[j].state;
                    //doing substring for the postal code to store only 5 characters
                    oSearchWrpProvider.sPostalCode = (arrHUMEnterpriseProviders.Demographics.Addresses[j].zip != NULL) ? arrHUMEnterpriseProviders.Demographics.Addresses[j].zip.substring(0,5): '';
                    oSearchWrpProvider.sCity = arrHUMEnterpriseProviders.Demographics.Addresses[j].city;
                }
                
                if(arrHUMEnterpriseProviders.Demographics.Addresses[j].AddressType == Label.HumServiceAddresstype)
                {
                    oSearchWrpProvider.sServiceaddress  = (arrHUMEnterpriseProviders.Demographics.Addresses[j].Line1 != NULL) ? arrHUMEnterpriseProviders.Demographics.Addresses[j].Line1 : '';
                    oSearchWrpProvider.sServiceaddress += (arrHUMEnterpriseProviders.Demographics.Addresses[j].Line2 != NULL) ? arrHUMEnterpriseProviders.Demographics.Addresses[j].Line2 : '';
                    oSearchWrpProvider.sServiceState = arrHUMEnterpriseProviders.Demographics.Addresses[j].state;
                    oSearchWrpProvider.sServicezip = (arrHUMEnterpriseProviders.Demographics.Addresses[j].zip != NULL) ? arrHUMEnterpriseProviders.Demographics.Addresses[j].zip.substring(0,5): '';
                    oSearchWrpProvider.sServiceCity = arrHUMEnterpriseProviders.Demographics.Addresses[j].city;
                }
                
                if(NULL != arrHUMEnterpriseProviders.Demographics.Addresses[j].Phones && 0 < arrHUMEnterpriseProviders.Demographics.Addresses[j].Phones.size())
                {
                   for(integer k = 0; K< arrHUMEnterpriseProviders.Demographics.Addresses[j].Phones.size(); K++)
                   {
                         string sphone= arrHUMEnterpriseProviders.Demographics.Addresses[j].Phones[k].Number_x;
                         if(sphone!=null && sphone.length()>=10)
                         {
                             oSearchWrpProvider.sphone = '(' + sphone.substring(0,3) + ') ' + sphone.substring(3,6) + '-' + sphone.substring(6,10);
                         }
                         oSearchWrpProvider.sPhoneExtn  = arrHUMEnterpriseProviders.Demographics.Addresses[j].Phones[k].Extension;
                   }
                }
            }
         }
        return oSearchWrpProvider;
    }
    
    /*Method Name   :    HUMServiceResponseReadSpecialtiesTaxonomy  
    * Description   :    Parsing Specialties and Taxonomy related response and storing in wrapper class. 
    * Parameters    :    HUMCustomSearchHelper,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto 
    * Return Type   :    HUMCustomSearchHelper
    */
    public HUMCustomSearchHelper HUMServiceResponseReadSpecialtiesTaxonomy(HUMCustomSearchHelper oSearchWrpProvider,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto  arrHUMEnterpriseProviders)
    {
        if(arrHUMEnterpriseProviders.Specialties != NULL)
        {
            for(Integer j = 0; j < arrHUMEnterpriseProviders.Specialties.size(); j++) 
            {
                oSearchWrpProvider.sSpeciality= arrHUMEnterpriseProviders.Specialties[j].Description;
            }
        }
        oSearchWrpProvider.sTaxID ='';
        oSearchWrpProvider=HUMServiceResponseReadIdentifiers(oSearchWrpProvider,arrHUMEnterpriseProviders);
        
        if(arrHUMEnterpriseProviders.Taxonomy != NULL)
        {
            for(Integer j = 0; j < arrHUMEnterpriseProviders.Taxonomy.size(); j++) 
            {
                oSearchWrpProvider.sTaxmonycode = arrHUMEnterpriseProviders.Taxonomy[j].Code;
            }
        }
        return oSearchWrpProvider;
    } 
    
    /*Method Name   :    HUMServiceResponseReadIdentifiers  
    * Description   :    Parsing identifiers related response and storing in wrapper class. 
    * Parameters    :    HUMCustomSearchHelper,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto 
    * Return Type   :    HUMCustomSearchHelper
    */
    private HUMCustomSearchHelper HUMServiceResponseReadIdentifiers(HUMCustomSearchHelper oSearchWrpProvider,HUMProvider_esHumanaComEPSS.EnterpriseProviderDto  arrHUMEnterpriseProviders)
    {
        if(arrHUMEnterpriseProviders.Identifiers != NULL)
        {
            integer iTaxCount=0;
            for(Integer j = 0; j < arrHUMEnterpriseProviders.Identifiers.size(); j++)
            {
                if(arrHUMEnterpriseProviders.Identifiers[j].Identifiers != NULL)
                { 
                    for(Integer k = 0 ; k < arrHUMEnterpriseProviders.Identifiers[j].Identifiers.size(); k++)
                    {
                        if(arrHUMEnterpriseProviders.Identifiers[j].IdentifierType == Label.HumServiceTaxIDtag)
                        {                          
                            oSearchWrpProvider.sTaxID += arrHUMEnterpriseProviders.Identifiers[j].Identifiers[k].ID +',';
                            iTaxCount = iTaxCount+1;
                            if(iTaxCount == Integer.ValueOf(Label.HUMTaxIDCount))
                            {
                                oSearchWrpProvider.sTaxID = oSearchWrpProvider.sTaxID+ ' ';
                                iTaxCount = 0;
                            }
                        }
                        else if(arrHUMEnterpriseProviders.Identifiers[j].IdentifierType == Label.HumServiceNPItag) 
                        {
                            oSearchWrpProvider.sNPI = arrHUMEnterpriseProviders.Identifiers[j].Identifiers[k].ID;
                        }
                    }   
                }
            }
            if(!string.isEmpty(oSearchWrpProvider.sTaxID))
            {
                oSearchWrpProvider.sTaxID=oSearchWrpProvider.sTaxID.substring(0, oSearchWrpProvider.sTaxID.length()-1);
            }
        }
        return oSearchWrpProvider;
    } 
}