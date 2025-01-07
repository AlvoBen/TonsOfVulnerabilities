/**************************************************************************************************************************************
Apex Class Name : HUMIntGroupSearchServiceTest
Version         : 1.0
Created On      : 08/05/2014
Function        : This class mocks the response of HUMIntGroupSearchService

* Modification Log:
* Developer Name          Code Review                   Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth           17172                      08/05/2014                 Original Version
* Chaitanya Kumar           18626                      10/17/2014                 Removed code related to Group Search as Group search doesnt use External results
* Reena Rajan               22348                      04/07/2015                 Added code block for responseType -HUMIntGroupSearchService.GroupSearchResponse_element for External Group Search for REQ-160910(line#43 to line#54)                                  
****************************************************************************************************************************************/

@isTest
global class HUMIntGroupSearchServiceTest implements WebServiceMock {
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
            
                          
                if(responseType == 'HUMIntGroupSearchService.GetGBEInfoResponse_element')
                {
                    HUMIntGroupSearchService.GetGBEInfoResponse_element respElement = 
                    new HUMIntGroupSearchService.GetGBEInfoResponse_element();
                    HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO grpInfo = new HUMIntGroupSearchServiceHelper.ResponseGroupExchangeDTO();
                    HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO lstGrpResp = new HUMIntGroupSearchServiceHelper.ArrayOfGroupExchangeInfoDetailDTO();
                    HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO grpResp = new HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO();
                    grpResp.IsHumanaOne = true;
                    lstGrpResp.GroupExchangeInfoDetailDTO = new List<HUMIntGroupSearchServiceHelper.GroupExchangeInfoDetailDTO>();
                    lstGrpResp.GroupExchangeInfoDetailDTO.add(grpResp);
                    grpInfo.GroupExchangeInfoDetailList = lstGrpResp;
                    respElement.GetGBEInfoResult = grpInfo;
                    response.put('response_x', respElement);
                }
                if(responseType == 'HUMIntGroupSearchService.GroupSearchResponse_element')
                {
                     HUMIntGroupSearchService.GroupSearch_element request_x = new HUMIntGroupSearchService.GroupSearch_element();
                     HUMIntGroupSearchService.GroupSearchResponse_element response_x = new HUMIntGroupSearchService.GroupSearchResponse_element();
                     HUMIntGroupSearchServiceHelper.ResponseGroupSearch groupSearchResponse = new HUMIntGroupSearchServiceHelper.ResponseGroupSearch();
                     List<HUMIntGroupSearchServiceHelper.ResponseGroupSearch> lstgroupSearchResponse  = new List<HUMIntGroupSearchServiceHelper.ResponseGroupSearch>(); 
                     groupSearchResponse.GroupID = 'CD066801';
                     groupSearchResponse.GroupName = 'mockGroup';
                     lstgroupSearchResponse.add(groupSearchResponse);
                     response_x.GroupSearchResult = lstgroupSearchResponse;
                     response.put('response_x', response_x);                   
                }
            
   }
}