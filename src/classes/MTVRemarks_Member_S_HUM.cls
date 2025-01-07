/*******************************************************************************************************************************
Apex Class Name : Public class MTVRemarks_Member_S_HUM
Version         : 1.0
Created On      : 04/05/2019   
Function        : This class is used to get data from  MTV member service.
Test Class      : MTVRemarks_Member_T_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Suraj patil                                        06/01/2019                  Original version
*  Sagar MN 										  04/21/2020				  User Story 777415 :MF 09 - MTV Member Remarks� Add Entity Remark 
*  Sagar MN                                           05/18/2020                  User Story 1098089 Add MTV Member Remarks 'SubIdentifier' Header/Column
*  Abhishek Mangutkar								  05/05/2021				  User Story 2230063 : Humana constants Cleanup Claims and MTV Claim Remarks
*  Ajay Chakradhar                                    05/05/2021	          REQ - 2184294 - Replacing constants with Consumer Management Constants
*******************************************************************************************************************************************************************/
public with sharing class MTVRemarks_Member_S_HUM
{
	Public Static final Integer HTTP_OK_HUM = 200;
	Public Static final Integer HTTP_ACCEPTED_HUM = 202;
	Public Static final String VOB_FAULT_CODE = 'faultcode';
	Public Static final String Content_type_HUM = 'content-type';
	Public Static final String MEMBER = 'Member';
	Public static final Integer iTwoYearOldDate = -2;
    /*
    Method Name : BuildRequestBody
    Functionality : BuildRequestBody is to create request body for  service for getting remarks from 'GetMTVRemarks' service.
    Return type : MTVRemarksServiceGetMemberRequest_H_HUM object.
    */
    public MTVRemarksServiceGetMemberRequest_H_HUM BuildRequestBody(String idPolicyMember)
    {
        MTVRemarksServiceGetMemberRequest_H_HUM oMTVRemarksReq = new MTVRemarksServiceGetMemberRequest_H_HUM();
        MTVRemarksServiceGetMemberRequest_H_HUM.GetMTVRemarksRequest oMTVRequest = new MTVRemarksServiceGetMemberRequest_H_HUM.GetMTVRemarksRequest();
            oMTVRequest.IdentifierId= String.isNotBlank(idPolicyMember) ? idPolicyMember.subString(0,idPolicyMember.length()-2) : '';
            oMTVRequest.SubIdentifier = String.isNotBlank(idPolicyMember) ? idPolicyMember.right(2) : '';
            oMTVRequest.IdType = MEMBER;
            oMTVRequest.StartDate = String.valueOf(Date.today().addYears(iTwoYearOldDate));
            oMTVRequest.EndDate = String.valueOf(Date.today());
            oMTVRemarksReq.GetMTVRemarksRequest = oMTVRequest;
        return oMTVRemarksReq;
    }
    
    /*
    Method Name : setupRequest
    Functionality : setupRequest is to create request header for  service for getting remarks from 'GetMTVRemarks' service.
    Return type : HttpRequest object.
    */
    public HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderMTVRemarks, String sRequestBody)
    {
        oRequest.setMethod(oSvcHeaderMTVRemarks.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcHeaderMTVRemarks.End_Point_URL__c);
        oRequest.setClientCertificateName(oSvcHeaderMTVRemarks.Certificate_Name__c);
        oRequest.setTimeout(Integer.valueof(oSvcHeaderMTVRemarks.Timeout__c));
        oRequest.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, oSvcHeaderMTVRemarks.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;          
    }
    
    /*
    Method Name : processResponse
    Functionality : processResponse is to parse response from GetMTVRemarks service for getting remarks.
    Return type :  list of MTVMembers_Remarks_DTO_HUM.
    */
    public static object processResponse(List < String > labels, Object State) 
    {  
        try
        {
            HttpResponse response = (labels != null ) ? Continuation.getResponse(labels[0]) : null;
            List<MTVMembers_Remarks_DTO_HUM> listMTVResp = new List<MTVMembers_Remarks_DTO_HUM>();
            
            if (response != null && !response.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && ((response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || response.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
            {
                MTVRemarksServiceGetMemberResponse_H_HUM objResp = new MTVRemarksServiceGetMemberResponse_H_HUM();
                objResp = (MTVRemarksServiceGetMemberResponse_H_HUM)System.Json.deserialize(response.getBody(),MTVRemarksServiceGetMemberResponse_H_HUM.class);
                if(objResp.MTVRemarksResponse != Null)
                {
                    MTVRemarksServiceGetMemberResponse_H_HUM.MTVRemarksResponse oMTVRemarksResponse = objresp.MTVRemarksResponse;
                    MTVRemarksServiceGetMemberResponse_H_HUM.RemarkList oRemarkList = oMTVRemarksResponse.RemarkList;
                    list<MTVRemarksServiceGetMemberResponse_H_HUM.Remarks> lstRemark = new list<MTVRemarksServiceGetMemberResponse_H_HUM.Remarks>();
                    lstRemark = oRemarkList.Remarks;
                    for(MTVRemarksServiceGetMemberResponse_H_HUM.Remarks objRemarks : lstRemark)
                    {
                        MTVMembers_Remarks_DTO_HUM objWrapper = new MTVMembers_Remarks_DTO_HUM();
                        objWrapper.sIdType = objRemarks.IdType;
                        objWrapper.sRemarkCode = objRemarks.RemarkType;
                        objWrapper.sIdentifierId= objRemarks.IdentifierId;
                        objWrapper.sSubIdentifier = objRemarks.SubIdentifier;
                        if(!String.isBlank(objRemarks.CreatedDate)){
                        objWrapper.sCreatedDate = date.valueOf(objRemarks.CreatedDate).format().replace('/', '-' );
                         }else{
                         objWrapper.sCreatedDate = '';   
                        }
                        objWrapper.sRemarkText = objRemarks.RemarkText;
                        objWrapper.sRemarkCategory = objRemarks.RemarkCategory;
                        objWrapper.sRemarkCount = oMTVRemarksResponse.RemarkCount;
                        listMTVResp.add(objWrapper);
                    }
                }
            } 
            return listMTVResp ;
        }
        catch(Exception ex)
        {
          HUMExceptionHelper.logErrors(ex, 'MTVRemarks_Member_S_HUM', 'processResponse');
          return null;
        }
    }
}