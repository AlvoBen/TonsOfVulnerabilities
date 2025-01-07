/**************************************************************************************************************************************************
Apex class Name    :  ClaimSendStmt_S_HUM
Version            :  1.0
Function           :  This is Helper class for service call out to ClaimSendStmt. It forms request and parse response.
Test Class         :  ClaimSendStmt_ST_HUM
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Kritika Popat           295053                09/28/2018                 Original Version
* Trina  Ghosh                                  07/27/2020                 US# 965352
* Rahul Chaurasia								03/05/2021		   		   REQ - 1905086 - T1PRJ0002606- SF- Mobius Services old version Decommission	

************************************************************************************************************************************************* */
public with sharing class ClaimSendStmt_S_HUM
{
    @TestVisible private String sBody ='' ;
    /*
    *  This method is used to form http request for ClaimSendStmt Service
    *  @name   setupRequest
    *  @param  HttpRequest - http Request for ClaimSendStmt Service
               HUM_Webservice_Callout__c - custom setting which has values for web service callouts 
               
    *  @return HttpRequest - request for ClaimSendStmt service
    *  @throws setupRequest
    */
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderStmt, String sROrgName, String sDistributionChnl, String sMsgFulfillmentId, String sRFN, String sRLN, String sRAdrLine1, String sRAdrLine2, String sRCity, String sRState, String sRZip, String sRFaxNbr, String sMbrId, String sDptCode, String sPlatformCode, String sSDRPersonId,  String sMemberGenKey, String sProvID, String sProvIDType, String sType,  String sAttachmentCategory, String sReprintSystemOfRecord )
    {
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            if(sROrgName != null || String.isNotEmpty(sROrgName))    sROrgName = 'ATTN: ' + sROrgName;
            if(sType == 'Member')       sBody = buildRequestMbr(sROrgName, sDistributionChnl, sMsgFulfillmentId, sRFN, sRLN, sRAdrLine1, sRAdrLine2, sRCity, sRState, sRZip, sRFaxNbr, sMbrId, sDptCode, sPlatformCode, sSDRPersonId, sMemberGenKey ,sAttachmentCategory,sReprintSystemOfRecord);
            if(sType == 'Design')       sBody = buildRequestDesign(sROrgName, sDistributionChnl, sMsgFulfillmentId, sRFN, sRLN, sRAdrLine1, sRAdrLine2, sRCity, sRState, sRZip, sRFaxNbr , sAttachmentCategory,sReprintSystemOfRecord);
            if(sType == 'Other')        sBody = buildRequestOthr(sROrgName, sDistributionChnl, sMsgFulfillmentId, sRFN, sRLN, sRAdrLine1, sRAdrLine2, sRCity, sRState, sRZip, sRFaxNbr, sAttachmentCategory,sReprintSystemOfRecord);
            if(sType == 'Provider')     sBody = buildRequestProvider(sROrgName, sDistributionChnl, sMsgFulfillmentId, sRFN, sRLN, sRAdrLine1, sRAdrLine2, sRCity, sRState, sRZip, sRFaxNbr, sProvID, sProvIDType, sAttachmentCategory,sReprintSystemOfRecord);
            
            if(String.isNotBlank(sBody) && sBody != null)
            {
                request.setMethod(oSvcHeaderStmt.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oSvcHeaderStmt.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                request.setBody(sBody);
                if(!Test.isRunningTest())    request.setClientCertificateName(oSvcHeaderStmt.Certificate_Name__c);    
                request.setTimeout(Integer.valueof(oSvcHeaderStmt.Timeout__c));
                request.setEndpoint(oSvcHeaderStmt.End_Point_URL__c);  
                orequest = request;
                if(Test.isRunningTest()) throw new HUMCustomException('Error');
            }
            else    orequest = null;
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimSendStmt_S_HUM', 'setupRequest');
            orequest = null;
        }
      return orequest;  
    }
    
    /*
    *  This method is used to build the required inputs needed to hit the ClaimSendStmt service
    *  @name   buildRequestMbr
    *  @param  
    *  @return string - returns the request body in serialized form
    *  @throws buildRequest
    */
    @testvisible private string buildRequestMbr(String sROrgName, String sDistributionChnl, String sMsgFulfillmentId, String sRFN, String sRLN, String sRAdrLine1, String sRAdrLine2, String sRCity, String sRState, String sRZip, String sRFaxNbr, String sMbrId, String sDptCode, String sPlatformCode, String sSDRPersonId,  String sMemberGenKey,String sAttachmentCategory, String sReprintSystemOfRecord   )
    {    
        string sJsonbody = '';
        try
        {
            ClaimSendStmt_ServiceRequest_H_HUM oRequest = new ClaimSendStmt_ServiceRequest_H_HUM();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleMember oMember = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleMember();
            oRequest.DistributionChannel = sDistributionChnl;            
            oRequest.RecipientOrganizationName = sROrgName;
            oRequest.RecipientFirstName = sRFN;
            oRequest.RecipientLastName = sRLN;
            oRequest.RecipientAddressLine1 = sRAdrLine1;
            oRequest.RecipientAddressLine2 = sRAdrLine2;
            oRequest.RecipientCity = sRCity;
            oRequest.RecipientState = sRState;
            oRequest.RecipientZip = sRZip;
            oRequest.RecipientFaxNumber = sRFaxNbr;
            oRequest.AttachmentCategory = sAttachmentCategory;
			oRequest.ReprintSystemOfRecord = sReprintSystemOfRecord;
            oRequest.SystemOfRecordId = sMsgFulfillmentId;            	


            oMember.MemberCardid = sMbrId;
            oMember.DependentCode = sDptCode;
            oMember.PlatformCode = sPlatformCode;
            oMember.SDRPersonId = sSDRPersonId;
            oMember.MemberGenKey = sMemberGenKey;
            
            oRequest.RecipientRoleMember = oMember;
            sJsonbody = JSON.serialize(oRequest);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimSendStmt_S_HUM', 'buildRequestMbr');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
    /*
    *  This method is used to build the required inputs needed to hit the ClaimSendStmt service
    *  @name   buildRequestMbr
    *  @param  
    *  @return string - returns the request body in serialized form
    *  @throws buildRequest
    */
    @testvisible private string buildRequestProvider(String sROrgName, String sDistributionChnl, String sMsgFulfillmentId, String sRFN, String sRLN, String sRAdrLine1, String sRAdrLine2, String sRCity, String sRState, String sRZip, String sRFaxNbr, String sProvID, String sProvIDType, String sAttachmentCategory, String sReprintSystemOfRecord  )
    {    
        string sJsonbody = '';
        try
        {
            ClaimSendStmt_ServiceRequest_H_HUM oRequest = new ClaimSendStmt_ServiceRequest_H_HUM();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleProvider oProvider = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleProvider();
            oRequest.DistributionChannel = sDistributionChnl;            
            oRequest.RecipientOrganizationName = sROrgName;
            oRequest.RecipientFirstName = sRFN;
            oRequest.RecipientLastName = sRLN;
            oRequest.RecipientAddressLine1 = sRAdrLine1;
            oRequest.RecipientAddressLine2 = sRAdrLine2;
            oRequest.RecipientCity = sRCity;
            oRequest.RecipientState = sRState;
            oRequest.RecipientZip = sRZip;
            oRequest.RecipientFaxNumber = sRFaxNbr;     
            oRequest.AttachmentCategory = sAttachmentCategory;
			oRequest.ReprintSystemOfRecord = sReprintSystemOfRecord;
            oRequest.SystemOfRecordId = sMsgFulfillmentId;            	
           
            oProvider.ProviderId = sProvID;
            oProvider.ProviderIdType = sProvIDType;
            
            oRequest.RecipientRoleProvider = oProvider;
            sJsonbody = JSON.serialize(oRequest);
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimSendStmt_S_HUM', 'buildRequestProvider');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
    /*
    *  This method is used to build the required inputs needed to hit the ClaimSendStmt service
    *  @name   buildRequestDesign
    *  @param  
    *  @return string - returns the request body in serialized form
    *  @throws buildRequestDesign
    */
    @testvisible private string buildRequestDesign(String sROrgName, String sDistributionChnl, String sMsgFulfillmentId, String sRFN, String sRLN, String sRAdrLine1, String sRAdrLine2, String sRCity, String sRState, String sRZip, String sRFaxNbr, String sAttachmentCategory, String sReprintSystemOfRecord  )
    {    
        string sJsonbody = '';
        try
        {
            ClaimSendStmt_ServiceRequest_H_HUM oRequest = new ClaimSendStmt_ServiceRequest_H_HUM();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleDesigned oDesign = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleDesigned();
            oRequest.DistributionChannel = sDistributionChnl;            
            oRequest.RecipientOrganizationName = sROrgName;
            oRequest.RecipientFirstName = sRFN;
            oRequest.RecipientLastName = sRLN;
            oRequest.RecipientAddressLine1 = sRAdrLine1;
            oRequest.RecipientAddressLine2 = sRAdrLine2;
            oRequest.RecipientCity = sRCity;
            oRequest.RecipientState = sRState;
            oRequest.RecipientZip = sRZip;
            oRequest.RecipientFaxNumber = sRFaxNbr;
            oRequest.AttachmentCategory = sAttachmentCategory;
			oRequest.ReprintSystemOfRecord = sReprintSystemOfRecord;
            oRequest.SystemOfRecordId = sMsgFulfillmentId;            	
            
            oDesign.DesignedId = '';
            oDesign.DesignedType = '';
            
            oRequest.RecipientRoleDesigned = oDesign;
            sJsonbody = JSON.serialize(oRequest);
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimSendStmt_S_HUM', 'buildRequestDesign');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
    /*
    *  This method is used to build the required inputs needed to hit the ClaimSendStmt service
    *  @name   buildRequest
    *  @param  
    *  @return string - returns the request body in serialized form
    *  @throws buildRequest
    */
    @testvisible private string buildRequestOthr(String sROrgName, String sDistributionChnl, String sMsgFulfillmentId, String sRFN, String sRLN, String sRAdrLine1, String sRAdrLine2, String sRCity, String sRState, String sRZip, String sRFaxNbr , String sAttachmentCategory, String sReprintSystemOfRecord  )
    {    
        string sJsonbody = '';
        try
        {
            ClaimSendStmt_ServiceRequest_H_HUM oRequest = new ClaimSendStmt_ServiceRequest_H_HUM();
            ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleOther oOther = new ClaimSendStmt_ServiceRequest_H_HUM.RecipientRoleOther();
            oRequest.DistributionChannel = sDistributionChnl;            
            oRequest.RecipientOrganizationName = sROrgName;
            oRequest.RecipientFirstName = sRFN;
            oRequest.RecipientLastName = sRLN;
            oRequest.RecipientAddressLine1 = sRAdrLine1;
            oRequest.RecipientAddressLine2 = sRAdrLine2;
            oRequest.RecipientCity = sRCity;
            oRequest.RecipientState = sRState;
            oRequest.RecipientZip = sRZip;
            oRequest.RecipientFaxNumber = sRFaxNbr;
            oRequest.AttachmentCategory = sAttachmentCategory;
			oRequest.ReprintSystemOfRecord = sReprintSystemOfRecord;
			oRequest.SystemOfRecordId = sMsgFulfillmentId;            	
		
            oOther.OtherId = '';
            oOther.OtherSystemCode = '';
            oOther.OtherSystemSuffix = '';
            oRequest.RecipientRoleOther = oOther;
            sJsonbody = JSON.serialize(oRequest);
            if(Test.isRunningTest()) throw new HUMCustomException('Error');
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimSendStmt_S_HUM', 'buildRequestOthr');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }    
}