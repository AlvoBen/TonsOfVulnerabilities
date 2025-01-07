/*****************************************************************************************
Apex class Name    : HUMVerifyFamilyWSDLMockImplHelper
Version            : 1.0
Function           : Test class to generate fake response 
Created On         : 08/05/2014

Modification Log:
*   Developer                Code Review            Date               Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Manish Kumar                17076                 08/05/2014         Original Version
* Asha Deepika Koyyana        18289                 10/01/2014         Defect fix defect #155408
******************************************************************************************/

@isTest
public with sharing class HUMVerifyFamilyWSDLMockImplHelper implements WebServiceMock 
{
    /*
    * Method Name   :    doInvoke
    * Description   :    This method is used to generate fake response for HUMVerifyFamily service
    * Return Type   :    void
    * Parameters    :    Object,Object , Map<String, Object>,string,string,string,string,string,string
    */
    public void doInvoke( Object oStub, Object oRequest, Map<String, Object> mapResponse,String sEndpoint, String sSoapAction,
                          String sRequestName, String sResponseNS, String sResponseName, String sResponseType)
    {     
        if(sResponseType == 'HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element')  updateDemoGraphicsResponse(mapResponse);
        if(sResponseType == 'HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element') getLastVerifiedResponse (mapResponse);
        if(sResponseType == 'HUMVerifyFamilyWSDL.GetMemberInformationResponse_element') getMemberInfoResponse(mapResponse);
    }    
        
    /*
    * Method Name   :    updateDemoGraphicsResponse
    * Description   :    This method is used to generate fake response for UpdateDemographics service
    * Return Type   :    void
    * Parameters    :    Map<String, Object>
    */
    public void updateDemoGraphicsResponse( Map<String, Object> mapResponse)
    {
        HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element oUpdateDemographics = new HUMVerifyFamilyWSDL.UpdateDemographicsResponse_element();
        
        string sReponseString  = '<?xml version="1.0"?>' +
                                '<EXPORT_ERROR_INFO><SEVERITY_CD>E</SEVERITY_CD><ERR_CD>91</ERR_CD>'+
                                '<ERR_DESC>Object variable or With block variable not set</ERR_DESC><MESSAGE_END>Y</MESSAGE_END>'+
                                '<MESSAGE_TYPE>C</MESSAGE_TYPE></EXPORT_ERROR_INFO>';
        
        oUpdateDemographics.UpdateDemographicsResult = sReponseString ;
        mapResponse.put('response_x', oUpdateDemographics); 
    }
       
    /*
    * Method Name   :    getLastVerifiedResponse
    * Description   :    This method is used to generate fake response for LastVerifiedDates
    * Return Type   :    void
    * Parameters    :    Map<String, Object>
    */ 
    Public void getLastVerifiedResponse( Map<String, Object> mapResponse)
    {
        HUMVerifyFamilyDatacontract.OIDemographicsOutputDTO oOIDemographics = new HUMVerifyFamilyDatacontract.OIDemographicsOutputDTO();
        
        oOIDemographics.DemographicsLastVerifiedDate = Date.Today();
        oOIDemographics.DemographicsVerifiedBy = 'AXK7437';
        oOIDemographics.OIDenLastVerifiedDate = NULL;
        oOIDemographics.OIMedLastVerifiedDate = NULL;
        
        HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element oUpdateDemographics = new HUMVerifyFamilyWSDL.GetLastVerifiedDatesResponse_element();
        oUpdateDemographics.GetLastVerifiedDatesResult = oOIDemographics ;
        mapResponse.put('response_x', oUpdateDemographics); 
    }
        
    /*
    * Method Name   :    getMemberInfoResponse
    * Description   :    This method is used to generate fake response for MemberInfo
    * Return Type   :    void
    * Parameters    :    Map<String, Object>
    */
    public void getMemberInfoResponse( Map<String, Object> mapResponse)
    {
        List<HUMVerifyFamilyDatacontract.MemberCoverageDTO> lstMemberCoverageDTO = new List<HUMVerifyFamilyDatacontract.MemberCoverageDTO>();
        
        HUMVerifyFamilyDatacontract.MemberCoverageDTO oMemberCoverageDTO = new HUMVerifyFamilyDatacontract.MemberCoverageDTO(); 
        oMemberCoverageDTO.CustomerGenKey = '89';
        oMemberCoverageDTO.CustCovSeqNbr = '89';
        lstMemberCoverageDTO.add(oMemberCoverageDTO);
              
        HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageDTO  oMembercoverage = new HUMVerifyFamilyDatacontract.ArrayOfMemberCoverageDTO();
        oMembercoverage.MemberCoverageDTO = lstMemberCoverageDTO; 
               
        List<HUMVerifyFamilyDatacontract.MemberDTO> lstMemberDTO = new List<HUMVerifyFamilyDatacontract.MemberDTO>();
        HUMVerifyFamilyDatacontract.MemberDTO oMemberDTO = new HUMVerifyFamilyDatacontract.MemberDTO();
        oMemberDTO.MemberCoverage = oMembercoverage ;  
        lstMemberDTO.add(oMemberDTO );
        
        HUMVerifyFamilyDatacontract.ArrayOfMemberDTO oArrayOfMember = new HUMVerifyFamilyDatacontract.ArrayOfMemberDTO();
        oArrayOfMember.MemberDTO = lstMemberDTO ;
        
        List<HUMVerifyFamilyDatacontract.MemberDTO> lstVerifyFamily = new List<HUMVerifyFamilyDatacontract.MemberDTO>();
        HUMVerifyFamilyDatacontract.MemberDTO overifyFamily = new HUMVerifyFamilyDatacontract.MemberDTO();
        overifyFamily.FamilyMembers = oArrayOfMember;
        lstVerifyFamily.add(oVerifyFamily);
        
        HUMVerifyFamilyDatacontract.ArrayOfMemberDTO lstArrMemberDTO = new HUMVerifyFamilyDatacontract.ArrayOfMemberDTO();
        lstArrMemberDTO.MemberDTO = lstVerifyFamily;
        
        HUMVerifyFamilyWSDL.GetMemberInformationResponse_element oUpdateDemographics = new HUMVerifyFamilyWSDL.GetMemberInformationResponse_element();
        oUpdateDemographics.GetMemberInformationResult = lstArrMemberDTO ;
        mapResponse.put('response_x', oUpdateDemographics);
    }
}