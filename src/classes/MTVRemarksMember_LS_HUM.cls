/*******************************************************************************************************************************
Apex Class Name : MTVRemarksMember_LS_HUM
Version         : 1.0
Created On      : 06/07/2021   
Function        : This class is used to get data from  MTV member service.
Test Class      : MTVRemarksMember_LT_HUM
                 
Modification Log: 
*  Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*  Kajal Namdev                                        06/07/2021                  Original version
*******************************************************************************************************************************************************************/
public class MTVRemarksMember_LS_HUM {
	 /*
    Method Name : BuildRequestBody
    Functionality : BuildRequestBody is to create request body for  service for getting remarks from 'GetMTVRemarks' service.
    Return type : MTVRemarksServiceGetMemberRequest_H_HUM object.
    */
    public static MTVRemarksServiceGetMemberRequest_H_HUM BuildRequestBody(String idPolicyMember){
        MTVRemarksServiceGetMemberRequest_H_HUM oMTVRemarksReq = new MTVRemarksServiceGetMemberRequest_H_HUM();
        MTVRemarksServiceGetMemberRequest_H_HUM.GetMTVRemarksRequest oMTVRequest = new MTVRemarksServiceGetMemberRequest_H_HUM.GetMTVRemarksRequest();
        oMTVRequest.IdentifierId= String.isNotBlank(idPolicyMember) ? idPolicyMember.subString(0,idPolicyMember.length()-2) : '';
        oMTVRequest.SubIdentifier = String.isNotBlank(idPolicyMember) ? idPolicyMember.right(2) : '';
        oMTVRequest.IdType = GLOBAL_CONSTANT_LH_HUM.MEMBER;
        oMTVRequest.StartDate = String.valueOf(Date.today().addYears(GLOBAL_CONSTANT_LH_HUM.TwoYearOldDate));
        oMTVRequest.EndDate = String.valueOf(Date.today());
        oMTVRemarksReq.GetMTVRemarksRequest = oMTVRequest;
        return oMTVRemarksReq;
    }
    
    /*
    Method Name : setupRequest
    Functionality : setupRequest is to create request header for  service for getting remarks from 'GetMTVRemarks' service.
    Return type : HttpRequest object.
    */
    public static HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderMTVRemarks, String sRequestBody){
        oRequest.setMethod(oSvcHeaderMTVRemarks.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcHeaderMTVRemarks.End_Point_URL__c);
        if(!Test.isRunningTest()){
       		oRequest.setClientCertificateName(oSvcHeaderMTVRemarks.Certificate_Name__c);   
        }
        oRequest.setTimeout(Integer.valueof(oSvcHeaderMTVRemarks.Timeout__c));
        oRequest.setHeader(GLOBAL_CONSTANT_LH_HUM.Content_type_HUM, oSvcHeaderMTVRemarks.Content_Type__c);
        oRequest.setHeader(GLOBAL_CONSTANT_LH_HUM.SOAPAction_HUM, '');    
        oRequest.setHeader(GLOBAL_CONSTANT_LH_HUM.Connection_HUM,GLOBAL_CONSTANT_LH_HUM.Keep_alive_HUM);
        return oRequest;          
    }
    
    /*
    Method Name : processResponse
    Functionality : processResponse is to parse response from GetMTVRemarks service for getting remarks.
    Return type :  list of MTVMembers_Remarks_DTO_HUM.
    */
    public static object processResponse(List < String > labels, Object State){  
        try{
            HttpResponse response = (labels != null ) ? Continuation.getResponse(labels[0]) : null;
            MTVResponseWrapper_DTO_HUM mtvResponse = new MTVResponseWrapper_DTO_HUM();
            List<MTVMembers_Remarks_DTO_HUM> listMTVResp = new List<MTVMembers_Remarks_DTO_HUM>();
            if (response != null && !response.getBody().containsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.VOB_FAULT_CODE) && ((response.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK_HUM || response.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest())){
                MTVRemarksServiceGetMemberResponse_H_HUM objResp = new MTVRemarksServiceGetMemberResponse_H_HUM();
                objResp = (MTVRemarksServiceGetMemberResponse_H_HUM)System.Json.deserialize(response.getBody(),MTVRemarksServiceGetMemberResponse_H_HUM.class);
                if(objResp.MTVRemarksResponse != Null){
                    MTVRemarksServiceGetMemberResponse_H_HUM.MTVRemarksResponse oMTVRemarksResponse = objresp.MTVRemarksResponse;
                    MTVRemarksServiceGetMemberResponse_H_HUM.RemarkList oRemarkList = oMTVRemarksResponse.RemarkList;
                    list<MTVRemarksServiceGetMemberResponse_H_HUM.Remarks> lstRemark = new list<MTVRemarksServiceGetMemberResponse_H_HUM.Remarks>();
                    lstRemark = oRemarkList.Remarks;
                    for(MTVRemarksServiceGetMemberResponse_H_HUM.Remarks objRemarks : lstRemark){
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
            mtvResponse.mtvMemberPremarkList = listMTVResp;
            mtvResponse.isOnSwitch = true;
            return mtvResponse;
        }catch(Exception ex){
            HUMExceptionHelper.logErrors(ex, 'MTVRemarksMember_LS_HUM', 'processResponse');
            return null;
        }
    }
    
    public static object getMTVRemarkData(String memberPlanId){  
        try{
            MTVRemarksServiceGetMemberRequest_H_HUM oMTVRemarksReq = new MTVRemarksServiceGetMemberRequest_H_HUM();            
            oMTVRemarksReq = BuildRequestBody(memberPlanId);            
            HUM_Webservice_Callout__c oSvcHeaderClaimMTVRemarks = HUM_Webservice_Callout__c.getValues('GetMTVRemarks'); 
            Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClaimMTVRemarks.Timeout__c)/1000;
            Continuation con = new Continuation(iTIMEOUT_INT_SECS);
            con.continuationMethod = 'processMTVResponse';
            String sJsonRequest = JSON.Serialize(oMTVRemarksReq);
            HttpRequest oRequest = new HttpRequest();
            oRequest = setupRequest(oRequest,oSvcHeaderClaimMTVRemarks,sjsonRequest);
            String sRequest = con.addHttpRequest(oRequest);
            return con;  
        }Catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MTVRemarksMember_LS_HUM', 'getMTVRemarkData');
            return NULL;
        }
    }
}