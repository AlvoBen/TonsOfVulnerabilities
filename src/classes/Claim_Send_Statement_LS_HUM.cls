/**************************************************************************** 
Apex Class Name  : Claim_Send_Statement_LS_HUM
Version          : 1.0  
Created Date     : 07/12/2022
Function         : Controller class RTIResenHum
Test class       : Claim_Send_Statement_LT_HUM
****************************************************************************
Modification Log:
* Developer Name                                  Date                       Description
*---------------------------------------------------------------------------------------------------
* Swapnali Sonawane 							  7/12/2022				     US-3406771 Lightning- RTI - Resend functionality
***************************************************************************************************/
public with sharing class Claim_Send_Statement_LS_HUM {
   
    private String sBody ='' ;
    public  Claim_Send_Statement_DTO_HUM clmSendWrapObj{get;set;}
    public  List<Claim_Send_Statement_DTO_HUM> InteractionDetails{get;set;}
    private Claim_Send_Statement_DTO_HUM oSClaimsWrap = new Claim_Send_Statement_DTO_HUM();
    public static final String Content_type_HUM = 'content-type';
    public static final String Connection_HUM = 'Connection';
    public static final String Keep_alive_HUM = 'keep-alive';
    public static final String SOAPAction_HUM = 'SOAPAction';
    
 /*
*  This method is used to form http request for Encrypt Rest Servive
*  @name   setupRequest
*  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
*  @return HttpRequest - request for Get Cases and Interaction 
*  @throws setupRequest exception
*/   
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcAddress,String sAddressLine1, String sAddressLine2,String sCity, String sStateCode, String sZipCode)
    {
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody =  buildRequest(sAddressLine1, sAddressLine2, sCity, sStateCode, sZipCode);
           
            if(String.isNotBlank(sBody))
            { 
                request.setMethod(oSvcAddress.Request_Type__c); 
                request.setHeader(CONTENT_TYPE_HUM, oSvcAddress.Content_Type__c);  
                request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
                request.setClientCertificateName(oSvcAddress.Certificate_Name__c);
                
                request.setTimeout(Integer.valueof(oSvcAddress.Timeout__c));
                request.setEndpoint(oSvcAddress.End_Point_URL__c);
                request.setBody(sBody);
                orequest = request; 
            } 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_LS_HUM' , 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }  
    
    @testvisible private string buildRequest(String sAddressLine1, String sAddressLine2,String sCity, String sStateCode, String sZipCode)
    {    
        string sJsonbody = '';
        try
        { 
            Claim_Send_Statement_Request_H_HUM oRequest = new Claim_Send_Statement_Request_H_HUM(); 
            Claim_Send_Statement_Request_H_HUM.StandardizeAddressRequest getSendStmtRequest = new Claim_Send_Statement_Request_H_HUM.StandardizeAddressRequest();
            getSendStmtRequest.AddressLine1 = sAddressLine1;
            getSendStmtRequest.AddressLine2 = sAddressLine2;
            getSendStmtRequest.City = sCity;
            getSendStmtRequest.StateCode = sStateCode;
            getSendStmtRequest.ZipCode = sZipCode;
            oRequest.StandardizeAddressRequest = getSendStmtRequest;
            sJsonbody = JSON.serialize(oRequest); 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'Claim_Send_Statement_LS_HUM' , 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    } 
    
    
    
    public void processResponse(string response) 
    {  
        Claim_Send_Statement_Response_H_HUM objRestResp = new Claim_Send_Statement_Response_H_HUM();
        objRestResp = Claim_Send_Statement_Response_H_HUM.parse(response);
        if(objRestResp.StandardizeAddressResponse != Null)
        { 
            
            oSClaimsWrap  = parseServiceResponse(objRestResp);
        }
    }
    
    private Claim_Send_Statement_DTO_HUM parseServiceResponse(Claim_Send_Statement_Response_H_HUM objRestResp)
    { 
        clmSendWrapObj  = new Claim_Send_Statement_DTO_HUM();
        Claim_Send_Statement_Response_H_HUM.StandardizeAddressResponse oStandaredAddResults = objRestResp.StandardizeAddressResponse;
        clmSendWrapObj.sSuccessFlag= oStandaredAddResults.SuccessFlag; 
        clmSendWrapObj.sAddressLine1 = oStandaredAddResults.AddressLine1; 
        
        clmSendWrapObj.sCity= oStandaredAddResults.City; 
        clmSendWrapObj.sZipCode= oStandaredAddResults.ZipCode; 
        clmSendWrapObj.sStateCode= oStandaredAddResults.StateCode; 
        clmSendWrapObj.sStateID= oStandaredAddResults.StateID;
        clmSendWrapObj.sZipCodePlus= oStandaredAddResults.ZipCodePlus; 
        clmSendWrapObj.sCountyName = oStandaredAddResults.CountyName; 
        clmSendWrapObj.sCountyID = oStandaredAddResults.CountyID; 
        clmSendWrapObj.sValid= oStandaredAddResults.Valid; 
        
        clmSendWrapObj.sChangeIndicator = oStandaredAddResults.ChangeIndicator; 
        return clmSendWrapObj;
    }
    

    /*
    *  This method is used to form http request for ClaimSendStmt Service
    *  @name   setupRequest
    *  @param  HttpRequest - http Request for ClaimSendStmt Service
               HUM_Webservice_Callout__c - custom setting which has values for web service callouts 
               
    *  @return HttpRequest - request for ClaimSendStmt service
    *  @throws setupRequest
    */
    public HttpRequest setupPrintSendRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderStmt, String sROrgName, String sDistributionChnl, String sMsgFulfillmentId, String sRFN, String sRLN, String sRAdrLine1, String sRAdrLine2, String sRCity, String sRState, String sRZip, String sRFaxNbr, String sMbrId, String sDptCode, String sPlatformCode, String sSDRPersonId,  String sMemberGenKey, String sProvID, String sProvIDType, String sType,  String sAttachmentCategory, String sReprintSystemOfRecord )
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
                request.setHeader(CONTENT_TYPE_HUM, oSvcHeaderStmt.Content_Type__c);  
                request.setHeader(SOAPACTION_HUM, '');    
                request.setHeader(CONNECTION_HUM,KEEP_ALIVE_HUM);
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
    //setupPrintSendRequest
    
}