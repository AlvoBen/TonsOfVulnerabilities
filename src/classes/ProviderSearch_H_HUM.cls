/**
   @Author Gargey Sharma
   @name ProviderSearch_H_HUM
   @CreateDate 10/15/2016
   @Description Contains methods to construct request and parsing response for EPS Service
   @Version 1.0
   @reference REQ - 270080 : EPSS conversion SOAP to REST
   @Test Class ProviderSearch_T_HUM
 */ 
 /**********************************************************************************************************
Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            71723                    03/03/2017                 Included address,city parameters in EPSS request criteria.               
* Santhi Mandava            77768                    03/22/2017                 Fixed defect 265058.0001 related to provider service address.
* Santhi Mandava            80459                    03/27/2017                 Added logic to read complete zipcode in case of QAA provider search
* Tanushree Hazari          92276                    05/11/2017                 REQ-313370/ CA Ticket 6577616 CRM Service EPSS Data Error Research 
* Prudhvi Pamarthi          237056                   06/15/2018                 REQ - 363608 : EPSS - Update App Key for CRM Service.
* Praveen Kumar Parimi      262732                   07/26/2018                 REQ - 367051 aka CA Incident #7521926  Fix for Service Addresses not showing up when conducting a search in the QAA template.
* Moshitha Gunasekaran                               01/08/2021                 US1534952 - INC0462428 - CRM Service - Incorrect NPI Populating on CRM Case Upon transfer from MHK / NPI Not Populating on CRM Case
* Ajay Chakradhar                                    05/31/2021                 REQ - 1755482 - Replacing constants to improve performance
* Firoja Begam                                       07/26/2021                 INC1212787 - Provider Search : Search with TAX ID "444444444 "is throwing an error
*************************************************************************************************************************************************/
public with sharing class ProviderSearch_H_HUM {
  
    Private String PCP_APPLICATIONKEY_HUM = '28AD1338-9A7D-41DB-BB87-F9FDFEE3DA01';
    Private String SERVICEADDRESSTYPE_PROVIDERSEARCH_HUM = 'Service';
    Private String NPITAG_PROVIDERSEARCH_HUM = 'NpiID';
    Private String INDIVIDUALTAG_PROVIDERSEARCH_HUM = 'Individual';
    Private String GROUP_PROVIDERSEARCH_HUM = 'Group';
    Private String FACILITYTAG_PROVIDERSEARCH_HUM = 'Facility';
    Private String TAXIDTAG_PROVIDERSEARCH_HUM = 'TaxID';
    Private String PROVIDERUNIQUE_PROVIDERSEARCH_HUM = 'PRV';
    Private String EXTMEMACCTTENANTID_PROVIDERSEARCH_HUM = '00';
    Private String BILLINGADDRESSTYPE_PROVIDERSEARCH_HUM = 'Billing';
    Private Integer TAXIDCOUNT_PROVIDERSEARCH_HUM = 3;
    Private String RESTV1SERVICENAME_PROVIDERSEARCH_HUM = 'EPSServiceREST_V1';
  /**
    * This method is used to form HTTP request for EPS Service
    * 
    * @param   map<string, string> Map with request parameters
    * @return    HttpRequest
    */
  public HttpRequest getEPSSReq(map<string, string> mapRequest)
  {
    HUM_Webservice_Callout__c oHumEPSSservice = HUM_Webservice_Callout__c.getValues(RESTV1SERVICENAME_PROVIDERSEARCH_HUM);
    HttpRequest req = new HttpRequest();
        if(oHumEPSSservice.Active__c)  
        {
          EPSSRESTV1SearchProviderRequest_H_HUM oRequest = new EPSSRESTV1SearchProviderRequest_H_HUM();
            oRequest.SearchProviderRequest = constructRequest(mapRequest);
      req.setMethod(oHumEPSSservice.Request_Type__c);  
      req.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oHumEPSSservice.Content_Type__c);
      req.setTimeout(Integer.valueOf(oHumEPSSservice.Timeout__c));
      req.setEndpoint(oHumEPSSservice.End_Point_URL__c);      
      req.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
      if (!Test.isRunningTest() && String.isNotBlank(oHumEPSSservice.Certificate_Name__c)) 
      {
        req.setClientCertificateName(oHumEPSSservice.Certificate_Name__c);
      }
           string sbody = JSON.serialize(oRequest);
      req.setBody(sbody);
        }
        return req;
  }
  
    /**
    * This method is called to construct request for EPS Service
    * 
    * @param   map<string, string> Map with request parameters
    * @return    EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest
    */
    private EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest constructRequest(map<string, string> mapRequest)
    {
        EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest oSearchProvReq = new EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest();
        oSearchProvReq.ApplicationKey = PCP_APPLICATIONKEY_HUM;
        EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria oSearchCri = new EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria();
        
        if(String.isNotBlank(mapRequest.get('sFirstName')) && String.isNotBlank(mapRequest.get('sLastName')) 
        &&  !((mapRequest.get('sFirstName')).equalsIgnoreCase('MissingFN')) && !((mapRequest.get('sLastName')).equalsIgnoreCase('MissingLN'))) 
        {
            oSearchCri.ProviderName = mapRequest.get('sFirstName') + ' ' + mapRequest.get('sLastName');
        }
        else if(String.isNotBlank(mapRequest.get('sFacilityName'))) 
        {    
          oSearchCri.ProviderName = mapRequest.get('sFacilityName');
        }
        
        oSearchCri.Address = constructAddressRequest(mapRequest);
        oSearchCri.Identifiers = constructIdentifierRequest(mapRequest);
        oSearchCri.Filter = contructFilterRequest(mapRequest);
        
        oSearchProvReq.SearchCriteria = oSearchCri;
        return oSearchProvReq;
    }
    
    /**
    * This method is used to create request for Address DTO in EPS service
    * 
    * @param   map<string, string> Map with request parameters
    * @return    EPSSRESTV1SearchProviderRequest_H_HUM.Address
    */
    private EPSSRESTV1SearchProviderRequest_H_HUM.Address constructAddressRequest(map<string, string> mapRequest)
    {
      EPSSRESTV1SearchProviderRequest_H_HUM.Address oAddress = new EPSSRESTV1SearchProviderRequest_H_HUM.Address();
        if (String.isNotBlank(mapRequest.get('sPostalCode')) || String.isNotBlank(mapRequest.get('sState')))  
        {
            oAddress.AddressType = SERVICEADDRESSTYPE_PROVIDERSEARCH_HUM;  
        }
        if(String.isNotBlank(mapRequest.get('sState'))) 
        {
            oAddress.State = mapRequest.get('sState');  
        } 
        if(string.isNotBlank(mapRequest.get('sPostalCode'))) 
        { 
            oAddress.Zip = mapRequest.get('sPostalCode');
        }
        if(string.isNotBlank(mapRequest.get('sAddress'))) 
        { 
            oAddress.AddressLine1 = mapRequest.get('sAddress');
        }
        if(string.isNotBlank(mapRequest.get('sCity'))) 
        { 
            oAddress.City = mapRequest.get('sCity');
        }
        return oAddress;
    }
    
    /**
    * This method is used to to create request for Filter DTO in EPS service
    * 
    * @param   map<string, string> Map with request parameters
    * @return    EPSSRESTV1SearchProviderRequest_H_HUM.Filter
    */
    private EPSSRESTV1SearchProviderRequest_H_HUM.Filter contructFilterRequest(map<string, string> mapRequest)
    {
      EPSSRESTV1SearchProviderRequest_H_HUM.Filter oFilter = new EPSSRESTV1SearchProviderRequest_H_HUM.Filter();
      oFilter.ParOnly = 'false';
      oFilter.UsaStates = '';
        if(String.isNotBlank(mapRequest.get('sNPI')))
        {
            oFilter.ExactMatch = NPITAG_PROVIDERSEARCH_HUM;
        }
        if(String.isNotBlank(mapRequest.get('sFirstName')) && String.isNotBlank(mapRequest.get('sLastName')) 
        &&  !((mapRequest.get('sFirstName')).equalsIgnoreCase('MissingFN')) && !((mapRequest.get('sLastName')).equalsIgnoreCase('MissingLN'))) 
        {
            oFilter.Classification = INDIVIDUALTAG_PROVIDERSEARCH_HUM;
        }
        else if(String.isNotBlank(mapRequest.get('sFacilityName'))) 
        {    
          oFilter.Classification = GROUP_PROVIDERSEARCH_HUM + ' ' + FACILITYTAG_PROVIDERSEARCH_HUM;
        }
        if(String.isNotBlank(mapRequest.get('sSpeciality'))) 
        {
            oFilter.Specialties = mapRequest.get('sSpeciality');
        }
        return oFilter;
    }
    
    /**
    * This method is used to to create request for Identifier DTO in EPS service
    * 
    * @param   map<string, string> Map with request parameters
  * @param   EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers
    * @return    EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers
    */
    private EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers constructIdentifierRequest(map<string, string> mapRequest)
    {
      EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers oIdentifiers = new EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers();
        if(String.isBlank(mapRequest.get('sNPI')) && String.isBlank(mapRequest.get('sTaxID')) )
        {
            return oIdentifiers;
        }
        list<EPSSRESTV1SearchProviderRequest_H_HUM.Identifier> lstIden = new list<EPSSRESTV1SearchProviderRequest_H_HUM.Identifier>();
        EPSSRESTV1SearchProviderRequest_H_HUM.Identifier oIden = new EPSSRESTV1SearchProviderRequest_H_HUM.Identifier();
        if(String.isNotBlank(mapRequest.get('sNPI')))
        {
            oIden.ID = mapRequest.get('sNPI');
            lstIden.add(oIden);
            oIdentifiers.Identifiers = lstIden;
            oIdentifiers.IdentifierType = NPITAG_PROVIDERSEARCH_HUM;
        }
        else if(String.isNotBlank(mapRequest.get('sTaxID')))
        {
            oIden.ID = mapRequest.get('sTaxID');
            lstIden.add(oIden);
            oIdentifiers.Identifiers = lstIden;
            oIdentifiers.IdentifierType = TAXIDTAG_PROVIDERSEARCH_HUM;
        }
        return oIdentifiers;
    }
    
    /**
    * This method is used to parse response received from EPS service
    * 
    * @param   HttpResponse
    * @return    list<HUMCustomSearchHelper> 
    */
    public list<HUMCustomSearchHelper> parseServiceResponse(HttpResponse response,Boolean isThisForCustomSearch)
    { 
      	EPSSRESTV1SearchProviderResponse_H_HUM oResponse = (EPSSRESTV1SearchProviderResponse_H_HUM)System.JSON.deserialize(response.getbody(),EPSSRESTV1SearchProviderResponse_H_HUM.class);
        list<HUMCustomSearchHelper> lstWrpProvider = new list<HUMCustomSearchHelper>();
        EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse oSearchProvRes = new EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse();
        if(oResponse.SearchProviderResponse != null) 
        {
          oSearchProvRes = oResponse.SearchProviderResponse;
        }
        list<EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders> lstEntProv = new list<EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders>();
        lstEntProv = oSearchProvRes.EnterpriseProviders;
        if(lstEntProv != null && !lstEntProv.isEmpty())
        {
          for(EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders oEntProv : lstEntProv)
          { 
              HUMCustomSearchHelper oSearchProvWrapper = new HUMCustomSearchHelper();
              if(string.isNotBlank(OEntProv.EnterpriseID))
              { 
                  oSearchProvWrapper.sEnterpriseID = OEntProv.EnterpriseID;
                  oSearchProvWrapper.sExtID = PROVIDERUNIQUE_PROVIDERSEARCH_HUM + '|' + EXTMEMACCTTENANTID_PROVIDERSEARCH_HUM + '|' + oSearchProvWrapper.sEnterpriseID;
                  oSearchProvWrapper.sMemberId= oSearchProvWrapper.sExtID;                  

              }
              if(string.isNotBlank(OEntProv.DbaName)) oSearchProvWrapper.sDBA = OEntProv.DbaName;
              if(string.isNotBlank(OEntProv.Classification) && OEntProv.Classification.length() > 0) {oSearchProvWrapper.sClassification = OEntProv.Classification.substring(0,1);}
              EPSSRESTV1SearchProviderResponse_H_HUM.ProviderName oProvName = new EPSSRESTV1SearchProviderResponse_H_HUM.ProviderName();
              oProvName = OEntProv.ProviderName;
              if(oProvName != null)
              {
                  oSearchProvWrapper.sFirstname = oProvName.First;
                  oSearchProvWrapper.sLastname = oProvName.Last;
                  oSearchProvWrapper.sDegree = oProvName.Degree;
              }
              EPSSRESTV1SearchProviderResponse_H_HUM.UniqueID sUniqID = new EPSSRESTV1SearchProviderResponse_H_HUM.UniqueID();
              sUniqID = OEntProv.UniqueID;
              if(string.isNotBlank(sUniqID.Source)) {oSearchProvWrapper.sPlatform = sUniqID.Source; }
              EPSSRESTV1SearchProviderResponse_H_HUM.Demographics oDemographics = new EPSSRESTV1SearchProviderResponse_H_HUM.Demographics();
              oDemographics = oEntProv.Demographics;
              oSearchProvWrapper = parseServiceResponseDemographics(oSearchProvWrapper, oDemographics,isThisForCustomSearch);
              list<EPSSRESTV1SearchProviderResponse_H_HUM.Identifier> lstIdentifiers = new list<EPSSRESTV1SearchProviderResponse_H_HUM.Identifier>();
              lstIdentifiers = oEntProv.Identifiers;
              oSearchProvWrapper = parseServiceResponseIdentifiers(oSearchProvWrapper, lstIdentifiers);
              oSearchProvWrapper = parseSpecialitySourceProviderTaxonomy(oSearchProvWrapper, OEntProv);
              oSearchProvWrapper.sPend = Label.HUM_Account_Provider_Recordtype;
              lstWrpProvider.add(oSearchProvWrapper);
          }
        }
        return lstWrpProvider;
    }
    
    /**
    * This method is used to parse demographics DTO in response received from EPSS service
    * 
    * @param   HUMCustomSearchHelper
    * @param   EPSSRESTV1SearchProviderResponse_H_HUM.Demographics
    * @param   Boolean
    * @return    HUMCustomSearchHelper 
    */
    private HUMCustomSearchHelper parseServiceResponseDemographics(HUMCustomSearchHelper oSearchProvWrapper, EPSSRESTV1SearchProviderResponse_H_HUM.Demographics oDemographics,Boolean isThisForCustomSearch)
    {
        if(string.isNotBlank(oDemographics.DateOfBirth)) 
        {
          oSearchProvWrapper.sBirthdate  = oDemographics.DateOfBirth;
        }
        if(string.isNotBlank(oDemographics.Gender)) 
        {
          oSearchProvWrapper.sGender  = oDemographics.Gender;
        }
        list<EPSSRESTV1SearchProviderResponse_H_HUM.Address> lstAddress = new list<EPSSRESTV1SearchProviderResponse_H_HUM.Address>();
        lstAddress = oDemographics.Addresses;
        Boolean isServiceTypePresent = false;
        if(lstAddress != null && !lstAddress.isEmpty())
        {
            for(EPSSRESTV1SearchProviderResponse_H_HUM.Address oAddress : lstAddress)
            {
                // isThisForCustomSearch - Variable is used to validate if this method is called from custom provider search or QAA provider search
                // isServiceTypePresent - This variable is used to read only the first service  address from the address list in case of qaa address search 
                if(oAddress.AddressType == BILLINGADDRESSTYPE_PROVIDERSEARCH_HUM && (!isServiceTypePresent || isThisForCustomSearch)) 
                {
                    oSearchProvWrapper.sAddress = (string.isNotBlank(oAddress.Line1)) ? oAddress.Line1 : '';
                    
                    If(isThisForCustomSearch)
                    {
                        oSearchProvWrapper.sAddress += (string.isNotBlank(oAddress.Line2)) ? oAddress.Line2 : '';
                        oSearchProvWrapper.sPostalCode = (string.isNotBlank(oAddress.Zip)) ? oAddress.Zip.substring(0,5) : '';
                    }
                    else
                    {
                        //Reading complete zipcode in case of QAA provider search
                        oSearchProvWrapper.sPostalCode = (string.isNotBlank(oAddress.Zip)) ? oAddress.Zip : '';
                    }
                    //Adding Blank value check for StateCode to not throw key not found Exception on UI if State Value is Blank
                    if(HUMUtilityHelper.isCRMFunctionalityON('2478323')){
                     if(string.isNotBlank(oAddress.State)) 
        			 {
                      oSearchProvWrapper.sState = oAddress.State;
                     }
                    }else{
                      oSearchProvWrapper.sState = oAddress.State;
                    }
                    oSearchProvWrapper.sCity = oAddress.City;
                }
                if(oAddress.AddressType == SERVICEADDRESSTYPE_PROVIDERSEARCH_HUM && (!isServiceTypePresent || isThisForCustomSearch))
                {
                    isServiceTypePresent = true;
                    oSearchProvWrapper.sServiceAddress = (string.isNotBlank(oAddress.Line1)) ? oAddress.Line1 : '';
                    if(isThisForCustomSearch)
                    {
                        oSearchProvWrapper.sServiceAddress += (string.isNotBlank(oAddress.Line2)) ? oAddress.Line2 : '';
                        oSearchProvWrapper.sServiceZip = (string.isNotBlank(oAddress.Zip)) ? oAddress.Zip.substring(0,5): '';                        
                    }
                    else
                    {
                        //Reading complete zipcode in case of QAA provider search
                        oSearchProvWrapper.sServiceZip = (string.isNotBlank(oAddress.Zip)) ? oAddress.Zip : '';                        
                    }                   
                    oSearchProvWrapper.sServiceState = oAddress.State;
                    oSearchProvWrapper.sServiceCity = oAddress.City;                    
                }
                list<EPSSRESTV1SearchProviderResponse_H_HUM.Phone> lstPhones = new List<EPSSRESTV1SearchProviderResponse_H_HUM.Phone>();
                lstPhones = oAddress.Phones;
                if(lstPhones != null && !lstPhones.isEmpty())
                {
                    for(EPSSRESTV1SearchProviderResponse_H_HUM.Phone oPhone : lstPhones)
                    {
                        if(string.isNotBlank(oPhone.PhoneNumber) && oPhone.PhoneNumber.length() >= 10)
                        {
                            oSearchProvWrapper.sphone = '(' + oPhone.PhoneNumber.substring(0,3) + ') ' + oPhone.PhoneNumber.substring(3,6) + '-' + oPhone.PhoneNumber.substring(6,10);
                        }
                        oSearchProvWrapper.sPhoneExtn = oPhone.Extension;
                    }
                }
            }
        }
        return oSearchProvWrapper;
     }
    
    /**
    * This method is used to parse Identifier DTO in response received from EPSS service
    * 
    * @param   HUMCustomSearchHelper
    * @param   list<EPSSRESTV1SearchProviderResponse_H_HUM.Identifier>
    * @return    HUMCustomSearchHelper 
    */
     private HUMCustomSearchHelper parseServiceResponseIdentifiers(HUMCustomSearchHelper oSearchProvWrapper, list<EPSSRESTV1SearchProviderResponse_H_HUM.Identifier> lstIdentifiers)
     {
       oSearchProvWrapper.sTaxID = '';
        if(lstIdentifiers != null && !lstIdentifiers.isEmpty())
        {
            integer iTaxCount = 0;
            for(EPSSRESTV1SearchProviderResponse_H_HUM.Identifier oIden : lstIdentifiers)
            {
                list<EPSSRESTV1SearchProviderResponse_H_HUM.IdentifierDetail> lstIdenDetails = new list<EPSSRESTV1SearchProviderResponse_H_HUM.IdentifierDetail>();
                lstIdenDetails = oIden.Identifiers;
                if(lstIdenDetails !=null && !lstIdenDetails.isEmpty())
                {
                    if(oIden.IdentifierType == TAXIDTAG_PROVIDERSEARCH_HUM)
                    {
                        for(EPSSRESTV1SearchProviderResponse_H_HUM.IdentifierDetail oIdenDetail : lstIdenDetails)
                        {
                            oSearchProvWrapper.sTaxID += oIdenDetail.ID + ',';
                            iTaxcount = iTaxCount + 1;
                            if(iTaxCount == TAXIDCOUNT_PROVIDERSEARCH_HUM)
                            {
                                oSearchProvWrapper.sTaxID = oSearchProvWrapper.sTaxID+ ' ';
                                iTaxCount = 0;
                            }
                        }
                    }
                    else if(oIden.IdentifierType == NPITAG_PROVIDERSEARCH_HUM)
                    {
                        for(EPSSRESTV1SearchProviderResponse_H_HUM.IdentifierDetail oIdenDetail : lstIdenDetails)
                        {
                            oSearchProvWrapper.sNPI = oIdenDetail.ID;
                        }
                    }
                }
            }
            if(string.isNotBlank(oSearchProvWrapper.sTaxID))
            {
                oSearchProvWrapper.sTaxID = oSearchProvWrapper.sTaxID.substring(0, oSearchProvWrapper.sTaxID.length()-1);
            }
        }
        return oSearchProvWrapper;
     }
     
    /**
    * This method is used to parse Speciality and Taxonomy DTO in response received from EPSS service
    * 
    * @param   HUMCustomSearchHelper
    * @param   EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders
    * @return    HUMCustomSearchHelper 
    */
     private HUMCustomSearchHelper parseSpecialitySourceProviderTaxonomy(HUMCustomSearchHelper oSearchProvWrapper, EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders oEntProv)
     {
        list<EPSSRESTV1SearchProviderResponse_H_HUM.Specialties> lstSpecialities = new list<EPSSRESTV1SearchProviderResponse_H_HUM.Specialties>();
        lstSpecialities = oEntProv.Specialties;
        if(lstSpecialities != null && !lstSpecialities.isEmpty())
        {
            for(EPSSRESTV1SearchProviderResponse_H_HUM.Specialties oSpecialities: lstSpecialities)
            {
                oSearchProvWrapper.sSpeciality = oSpecialities.Description;
            }
        }
        
        list<EPSSRESTV1SearchProviderResponse_H_HUM.Taxonomy> lstTaxonomy = new list<EPSSRESTV1SearchProviderResponse_H_HUM.Taxonomy>();
        lstTaxonomy = oEntProv.Taxonomy;
        if(lstTaxonomy != null && !lstTaxonomy.isEmpty())
        {
            for(EPSSRESTV1SearchProviderResponse_H_HUM.Taxonomy oTaxonomy: lstTaxonomy)
            {
                oSearchProvWrapper.sTaxmonycode = oTaxonomy.Code;
            }
        }
        return oSearchProvWrapper;
     }
}