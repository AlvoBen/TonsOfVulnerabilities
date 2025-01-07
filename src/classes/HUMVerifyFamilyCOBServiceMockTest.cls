/*****************************************************************************************
Apex class Name    : HUMVerifyFamilyCOBServiceMockTest
Version            : 1.0
Function           : Test class to generate Mock response for HUMVerifyFamilyCOBService
Created On         : 02/16/2015

Modification Log:
*   Developer                Code Review            Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Pradeep Veeravali          20976                  02/16/2015         Original Version
* Pradeep Veeravali          21106                  02/23/2015         Modified test data for COB
* Saurabh Kaple               162071               01/10/2018			REQ - 333069 - Genkey conversion- updates to person service request																																					
******************************************************************************************/

@isTest
public with sharing class HUMVerifyFamilyCOBServiceMockTest implements WebServiceMock 
{

     /*
    * Method Name   :    doInvoke
    * Description   :    This method is used to generate Mock response for HUMVerifyFamilyCOBService
    * Return Type   :    void
    * Parameters    :    Object,Object , Map<String, Object>,string,string,string,string,string,string
    */
    public void doInvoke( Object oStub, Object oRequest, Map<String, Object> mapResponse,String sEndpoint, String sSoapAction,
                          String sRequestName, String sResponseNS, String sResponseName, String sResponseType)
    {     
        if(sResponseType == 'HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element')  updateDemoGraphicsResponse(mapResponse);
        if(sResponseType == 'HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element') getLastVerifiedResponse (mapResponse);
        if(sResponseType == 'HUMVerifyFamilyCOBService.GetMemberInformationResponse_element') getMemberInfoResponse(mapResponse);
    }    
        
    /*
    * Method Name   :    updateDemoGraphicsResponse
    * Description   :    This method is used to generate Mock response for UpdateDemographics service
    * Return Type   :    void
    * Parameters    :    Map<String, Object>
    */
    public void updateDemoGraphicsResponse( Map<String, Object> mapResponse)
    {
        HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element oUpdateDemographics = new HUMVerifyFamilyCOBService.UpdateDemographicsResponse_element();
        
        string sReponseString  = '<?xml version="1.0"?>' +
                                '<EXPORT_ERROR_INFO><SEVERITY_CD>E</SEVERITY_CD><ERR_CD>91</ERR_CD>'+
                                '<ERR_DESC>Object variable or With block variable not set</ERR_DESC><MESSAGE_END>Y</MESSAGE_END>'+
                                '<MESSAGE_TYPE>C</MESSAGE_TYPE></EXPORT_ERROR_INFO>';
        
        oUpdateDemographics.UpdateDemographicsResult = sReponseString ;
        mapResponse.put('response_x', oUpdateDemographics); 
    }
       
    /*
    * Method Name   :    getLastVerifiedResponse
    * Description   :    This method is used to generate Mock response for LastVerifiedDates
    * Return Type   :    void
    * Parameters    :    Map<String, Object>
    */ 
    Public void getLastVerifiedResponse( Map<String, Object> mapResponse)
    {
        HUMVerifyFamilyCOBService.OIDemographicsOutputDTO oOIDemographics = new HUMVerifyFamilyCOBService.OIDemographicsOutputDTO();
        
        oOIDemographics.DemographicsLastVerifiedDate = Date.Today();
        oOIDemographics.DemographicsVerifiedBy = 'AXK7437';
        oOIDemographics.OIDenLastVerifiedDate = NULL;
        oOIDemographics.OIMedLastVerifiedDate = NULL;
        
        HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element oUpdateDemographics = new HUMVerifyFamilyCOBService.GetLastVerifiedDatesResponse_element();
        oUpdateDemographics.GetLastVerifiedDatesResult = oOIDemographics ;
        mapResponse.put('response_x', oUpdateDemographics); 
    }
    
     /*
    * Method Name   :    getMemberInfoResponse
    * Description   :    This method is used to generate Mock response for MemberInfo
    * Return Type   :    void
    * Parameters    :    Map<String, Object>
    */
    public void getMemberInfoResponse( Map<String, Object> mapResponse)
    {
        List<HUMVerifyFamilyCOBService.MemberCoverageDTO> lstMemberCoverageDTO = new List<HUMVerifyFamilyCOBService.MemberCoverageDTO>();
        
        HUMVerifyFamilyCOBService.MemberCoverageDTO oMemberCoverageDTO = new HUMVerifyFamilyCOBService.MemberCoverageDTO(); 
        oMemberCoverageDTO.EnterpriseId = '123456789';
        oMemberCoverageDTO.CustomerGenKey = '89';
        oMemberCoverageDTO.CustCovSeqNbr = '89';
        oMemberCoverageDTO.SrcPlatformCode = 'LV';
        HUMVerifyFamilyCOBService.ArrayOfAPTCDTO aptc = new HUMVerifyFamilyCOBService.ArrayOfAPTCDTO();
        List<HUMVerifyFamilyCOBService.APTCDTO> lstAptcDTO = new List<HUMVerifyFamilyCOBService.APTCDTO>();
        HUMVerifyFamilyCOBService.APTCDTO aptcDTO = new HUMVerifyFamilyCOBService.APTCDTO();
        aptcDTO.Amount = '12';
        aptcDTO.EffectiveDate = '01/01/2015';
        aptcDTO.EndDate = '01/01/2025';
        aptcDTO.SeqNumber = '007';        
        lstAptcDTO.add(aptcDTO);
        aptc.APTCDTO = lstAptcDTO; 
        oMemberCoverageDTO.APTC = aptc;
        HUMVerifyFamilyCOBService.AlternateCoverageIDList altCovIdList = new HUMVerifyFamilyCOBService.AlternateCoverageIDList();        
        HUMVerifyFamilyCOBService.AlternateValue altValue = new HUMVerifyFamilyCOBService.AlternateValue();
        altValue.Source = 'sourceValue';
        altValue.Type_x = 'typeValue';
        altValue.Value = 'altValue';
        List<HUMVerifyFamilyCOBService.AlternateValue> lstAltValues = new List<HUMVerifyFamilyCOBService.AlternateValue>();
        lstAltValues.add(altValue);
        HUMVerifyFamilyCOBService.ArrayOfAlternateValue arrAltValue = new HUMVerifyFamilyCOBService.ArrayOfAlternateValue();
        arrAltValue.AlternateValue = lstAltValues;
        altCovIdList.AlternateValues = arrAltValue;
        oMemberCoverageDTO.AlternateCoverageIDList = altCovIdList;
        
        HUMVerifyFamilyCOBService.COBDTO cob = new HUMVerifyFamilyCOBService.COBDTO();
        cob.CarrierName = 'Humana One';
        cob.EffectiveDate = '02/15/2015';
        cob.EndDate = '02/15/2025';
        cob.Primary = 'Y';
        List<HUMVerifyFamilyCOBService.COBDTO> lstCOB = new List<HUMVerifyFamilyCOBService.COBDTO>();
        lstCOB.add(cob);        
        HUMVerifyFamilyCOBService.ArrayOfCOBDTO arrCOB = new HUMVerifyFamilyCOBService.ArrayOfCOBDTO();
        arrCOB.COBDTO = lstCOB;
        oMemberCoverageDTO.COB = arrCOB;
        
        HUMVerifyFamilyCOBService.NetworkDTO ntDTO = new HUMVerifyFamilyCOBService.NetworkDTO();
        ntDTO.NetworkID = 'net';
        oMemberCoverageDTO.Network = ntDTO;  
                      
        HUMVerifyFamilyCOBService.MemberCoveragePropertyDTO memCovPropDTO = new HUMVerifyFamilyCOBService.MemberCoveragePropertyDTO();
        memCovPropDTO.Key = 'covKey';
        memCovPropDTO.Value = 'covValue';
        List<HUMVerifyFamilyCOBService.MemberCoveragePropertyDTO> lstMemCovPropDTO = new List<HUMVerifyFamilyCOBService.MemberCoveragePropertyDTO>();
        lstMemCovPropDTO.add(memCovPropDTO); 
        HUMVerifyFamilyCOBService.ArrayOfMemberCoveragePropertyDTO arrMemCovPropDTO = new HUMVerifyFamilyCOBService.ArrayOfMemberCoveragePropertyDTO();
        arrMemCovPropDTO.MemberCoveragePropertyDTO = lstMemCovPropDTO;
        HUMVerifyFamilyCOBService.PlanDTO plan = new  HUMVerifyFamilyCOBService.PlanDTO();
        plan.Properties = arrMemCovPropDTO;
        oMemberCoverageDTO.Properties = arrMemCovPropDTO;
        
        HUMVerifyFamilyCOBService.ProviderDTO prov = new HUMVerifyFamilyCOBService.ProviderDTO();        
        oMemberCoverageDTO.Provider = prov;
                
        HUMVerifyFamilyCOBService.ArrayOfMemberCoverageSourceDTO arrMemSrcCovDTO = new HUMVerifyFamilyCOBService.ArrayOfMemberCoverageSourceDTO();
        List<HUMVerifyFamilyCOBService.MemberCoverageSourceDTO> lstMemCovSrcDTO = new List<HUMVerifyFamilyCOBService.MemberCoverageSourceDTO>();
        HUMVerifyFamilyCOBService.MemberCoverageSourceDTO memCovSrcDTO = new HUMVerifyFamilyCOBService.MemberCoverageSourceDTO();        
        memCovSrcDTO.Key = '078648304 Test COB EE0R760801';
        memCovSrcDTO.SourceCode = '23';
        memCovSrcDTO.Source = 'MC';
        lstMemCovSrcDTO.add(memCovSrcDTO);
        arrMemSrcCovDTO.MemberCoverageSourceDTO = lstMemCovSrcDTO;
        oMemberCoverageDTO.SourceHeaderKeys = arrMemSrcCovDTO;
        
        HUMVerifyFamilyCOBService.SupplementCoverageKeysDTO supCovKeysDTO = new HUMVerifyFamilyCOBService.SupplementCoverageKeysDTO();        
        HUMVerifyFamilyCOBService.SupplementalCoverageDTO supCovDTO = new HUMVerifyFamilyCOBService.SupplementalCoverageDTO();
        supCovDTO.SupplementCoverageKeys = supCovKeysDTO;
        List<HUMVerifyFamilyCOBService.SupplementalCoverageDTO> lstSupCovDTO = new List<HUMVerifyFamilyCOBService.SupplementalCoverageDTO>();
        lstSupCovDTO.add(supCovDTO);
        HUMVerifyFamilyCOBService.ArrayOfSupplementalCoverageDTO arrSupCovDTO = new HUMVerifyFamilyCOBService.ArrayOfSupplementalCoverageDTO();
        arrSupCovDTO.SupplementalCoverageDTO = lstSupCovDTO;
        oMemberCoverageDTO.SupplementalCovgInformation = arrSupCovDTO; 
        lstMemberCoverageDTO.add(oMemberCoverageDTO);
              
        HUMVerifyFamilyCOBService.ArrayOfMemberCoverageDTO  oMembercoverage = new HUMVerifyFamilyCOBService.ArrayOfMemberCoverageDTO();
        oMembercoverage.MemberCoverageDTO = lstMemberCoverageDTO; 
               
        List<HUMVerifyFamilyCOBService.MemberDTO> lstMemberDTO = new List<HUMVerifyFamilyCOBService.MemberDTO>();
        HUMVerifyFamilyCOBService.MemberDTO oMemberDTO = new HUMVerifyFamilyCOBService.MemberDTO();
        oMemberDTO.MemberCoverage = oMembercoverage ;  
        
        HUMVerifyFamilyCOBService.AddressDTO oAddDTO = new HUMVerifyFamilyCOBService.AddressDTO();
        oMemberDTO.Address = oAddDTO;
        
        HUMVerifyFamilyCOBService.ArrayOfClinicalCaseDTO arrClinCaseDTO = new HUMVerifyFamilyCOBService.ArrayOfClinicalCaseDTO();
        List<HUMVerifyFamilyCOBService.ClinicalCaseDTO> lstClinCaseDTO = new List<HUMVerifyFamilyCOBService.ClinicalCaseDTO>();
        HUMVerifyFamilyCOBService.ClinicalCaseDTO oClinCaseDTO = new HUMVerifyFamilyCOBService.ClinicalCaseDTO();
        HUMVerifyFamilyCOBService.ArrayOfPartnerDetailDTO arrPartnerDetDTO = new HUMVerifyFamilyCOBService.ArrayOfPartnerDetailDTO();
        HUMVerifyFamilyCOBService.PartnerDetailDTO oPtDtlDTO = new HUMVerifyFamilyCOBService.PartnerDetailDTO();
        List<HUMVerifyFamilyCOBService.PartnerDetailDTO> lstptDtlDTO = new List<HUMVerifyFamilyCOBService.PartnerDetailDTO>();
        lstptDtlDTO.add(oPtDtlDTO);
        arrPartnerDetDTO.PartnerDetailDTO = lstptDtlDTO;
        oClinCaseDTO.PartnerDetail = arrPartnerDetDTO;
        lstClinCaseDTO.add(oClinCaseDTO);
        arrClinCaseDTO.ClinicalCaseDTO = lstClinCaseDTO;
        oMemberDTO.ClinicalCase = arrClinCaseDTO;            
        
        HUMVerifyFamilyCOBService.MemberPropertiesDTO oMemPropDTO = new HUMVerifyFamilyCOBService.MemberPropertiesDTO();
        oMemberDTO.MemberProperties = oMemPropDTO;
        
        HUMVerifyFamilyCOBService.ResidentialAddressDTO resAddDTO = new HUMVerifyFamilyCOBService.ResidentialAddressDTO();
        oMemberDTO.ResidentialAddress = resAddDTO;
        
        HUMVerifyFamilyCOBService.ArrayOfLongTermCareDTO arrLngTrmCrDTO = new HUMVerifyFamilyCOBService.ArrayOfLongTermCareDTO();
        List<HUMVerifyFamilyCOBService.LongTermCareDTO> lstLngTrmCrDTO = new List<HUMVerifyFamilyCOBService.LongTermCareDTO>();
        HUMVerifyFamilyCOBService.LongTermCareDTO olngTrmCrDTO = new HUMVerifyFamilyCOBService.LongTermCareDTO();
        lstLngTrmCrDTO.add(olngTrmCrDTO);
        arrLngTrmCrDTO.LongTermCareDTO = lstLngTrmCrDTO;
        oMemberDTO.LongTermCareHistory = arrLngTrmCrDTO;
        oMemberDTO.FirstName = 'Test COB';
        oMemberDTO.LastName = 'Account Last';
        lstMemberDTO.add(oMemberDTO);
        HUMVerifyFamilyCOBService.ArrayOfMemberDTO oArrayOfMember = new HUMVerifyFamilyCOBService.ArrayOfMemberDTO();
        oArrayOfMember.MemberDTO = lstMemberDTO;
        
        List<HUMVerifyFamilyCOBService.MemberDTO> lstVerifyFamily = new List<HUMVerifyFamilyCOBService.MemberDTO>();
        HUMVerifyFamilyCOBService.MemberDTO overifyFamily = new HUMVerifyFamilyCOBService.MemberDTO();
        overifyFamily.FamilyMembers = oArrayOfMember;
        lstVerifyFamily.add(oVerifyFamily);
        
        HUMVerifyFamilyCOBService.ArrayOfMemberDTO lstArrMemberDTO = new HUMVerifyFamilyCOBService.ArrayOfMemberDTO();
        lstArrMemberDTO.MemberDTO = lstVerifyFamily;
        
        HUMVerifyFamilyCOBService.GetMemberInformationResponse_element oCOBInfo = new HUMVerifyFamilyCOBService.GetMemberInformationResponse_element();
        oCOBInfo.GetMemberInformationResult = lstArrMemberDTO ;
        mapResponse.put('response_x', oCOBInfo);
    }
}