/**************************************************************************************************************************************
Apex Class Name : HUMIntEESLegacySearchServiceTest
Version         : 1.0
Created On      : 08/05/2014
Function        : This class mocks the response of HUMIntEESLegacySearchService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth                                     08/05/2014                 Original Version
****************************************************************************************************************************************/

@isTest
global class  HUMIntEESLegacySearchServiceTest implements WebServiceMock{

    global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType) {
           HUMIntEESLegacySearchService.GetPlatformMembersResponse_element respElement = new HUMIntEESLegacySearchService.GetPlatformMembersResponse_element ();
           String sResp = '<Return SessionID="5561184479" Version="2.23.0.0" Duration="1328" TimeStamp="05/15/2014 09:05:35:570" Environment="QA">';
 		   sResp += '<PlatformMembers><PlatformMember><FirstName>JOSEPH</FirstName> <LastName>ADAMOWICZ</LastName><PID>0338730300212608</PID>'; 
 		   sResp += '<Family><PlatformMember><FirstName>JOSEPH</FirstName> <SSN>111585060</SSN><MemberContracts>';
 		   sResp += '<MemberContract><ContractID>700800508</ContractID> <EffectiveDate>02/01/2013</EffectiveDate><EndDate>NONE</EndDate>';
 		   sResp += '<Group><GroupID>553121</GroupID><GroupName>HUMANAONE TX IMM PHPR</GroupName></Group><Coverages>';
 		   sResp += '<PlatformMemberCoverage><LineOfBusiness>IPP</LineOfBusiness><SrcPlatformCode>EM</SrcPlatformCode>';
 		   sResp += '<CategoryDesciption>MEDICAL</CategoryDesciption><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate>'; 
 		   sResp += '<MemberCoverageEndDate>NONE</MemberCoverageEndDate> </PlatformMemberCoverage><PlatformMemberCoverage>';
 		   sResp += '<LineOfBusiness>IPB</LineOfBusiness><Category>DEN</Category><SrcPlatformCode>EM</SrcPlatformCode><CategoryDesciption>DENTALAL</CategoryDesciption>';
 		   sResp += '<CoverageTier>FAM</CoverageTier><MemberCoverageEffectiveDate>02/01/2013</MemberCoverageEffectiveDate></PlatformMemberCoverage>';
 		   sResp += '</Coverages></MemberContract></MemberContracts></PlatformMember></Family></PlatformMember></PlatformMembers></Return>';
           respElement.GetPlatformMembersResult = sResp;
           response.put('response_x', respElement);
   }
}