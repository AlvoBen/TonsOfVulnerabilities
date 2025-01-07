/*******************************************************************************************************************************
Apex Class Name : GroupDetailDivisionSubgroup_LD_HUM
Version         : 1.0
Created On      : 11/03/2020
Function        : This class is the data layer class for GroupDetailDivisionSubgroup_LC_HUM
Test Class      : GroupDetailDivisionSubgroup_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     12/18/2020                    Original Version
*    2.0           Ashish Kumar                                     02/20/2021                    Added method for querying account
*    3.0           Kajal Namdev                                     05/07/2021                    Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*    4.0           Kajal Namdev                                     09/29/2021          	      Added limitOne in query
*******************************************************************************************************************************************************************/
public with sharing Class GroupDetailDivisionSubgroup_LD_HUM 
{
     /*
    * Method Name   :  formRequestBody()
    * Description   :  This method is used to form the request body
    * Return Type   :  String
    * Parameters    :  String, String
    */
    @testVisible public static string formRequestBody(String sStartPosition,String sRecordsToreturn, Account objGrpAccount) 
    {
        GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
        GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
        string sformBody = '';
        grpDetailDTO.GroupId = objGrpAccount.Source_Customer_Key__c;
        
        if(!String.isBlank(objGrpAccount.Source_Platform_Code__c))
        {
            if(objGrpAccount.Source_Platform_Code__c.equalsIgnoreCase(GLOBAL_CONSTANT_LH_HUM.sLV_Value))
            {            
                grpDetailDTO.SubGroupId = GLOBAL_CONSTANT_LH_HUM.sZeroZeroOne;
                grpDetailDTO.BenefitPackageId = '';
            }
            else
            {            
                grpDetailDTO.SubGroupId = objGrpAccount.Source_Customer_Key__c+ GLOBAL_CONSTANT_LH_HUM.sZeroOne;
                grpDetailDTO.BenefitPackageId = GLOBAL_CONSTANT_LH_HUM.sDummyBenValue;
            }
        }            
        grpDetailDTO.Platform = objGrpAccount.Source_Platform_Code__c;                     
        grpDetailDTO.BenefitSequenceNumber= '';
        grpDetailDTO.InclusionCriteria = HumConstants.GROUPSTRING;
        grpDetailDTO.DivisionStartPosition = sStartPosition;            
        grpDetailDTO.DivisionRecordsToReturn = sRecordsToreturn;            
        oRequest.GroupDetailDTO = grpDetailDTO;                    
        sformBody = JSON.serialize(oRequest);             
        return sformBody;
    }
       
    /*
    * Method Name   :  setuprequestToGBE
    * Description   :  This method is used to create request DTO and to call the GBE Service
    * Return Type   :  HttpRequest
    * Parameters    :  HUM_Webservice_Callout__c,String
    */    
    public static HttpRequest setuprequestToGBE(HUM_Webservice_Callout__c oSvcHeaderGBEService, string sRequestBody)
    {
        HttpRequest request = new HttpRequest();
        if(!String.isBlank(sRequestBody)&&(oSvcHeaderGBEService!=null)&&(oSvcHeaderGBEService.Active__c))
        {
            request.setMethod(oSvcHeaderGBEService.Request_Type__c);
            request.setBody(sRequestBody);
            
            if(!Test.isRunningTest() && String.isNotBlank(oSvcHeaderGBEService.Certificate_Name__c))
            {
                request.setClientCertificateName(oSvcHeaderGBEService.Certificate_Name__c); 
            }                    
            if(String.isNotBlank(oSvcHeaderGBEService.Content_Type__c))
            {
                request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderGBEService.Content_Type__c);
            }
            request.setHeader(Label.HUMServiceTesterControllerConnection,Label.HUMServiceTesterController_keep_alive);
            request.setHeader(Label.HUMServiceTesterControllerSOAPAction,'');
            request.setEndpoint(oSvcHeaderGBEService.End_Point_URL__c);            
            return request;
        }
        else return null;
    }
    
    public static Account getAccount(String sAccId)
    {
        return [SELECT Source_Customer_Key__c, Source_Platform_Code__c, Group_Number__c FROM Account where id =: sAccId LIMIT 1];

    }
    /*
    * Method Name   :    checkResponse() 
    * Description   :    Method to check the Response from processResponse().
    * return Type   :    GBEServiceResponse_H_HUM
    * Parameters    :    HttpResponse
    */   
    public static GBEServiceResponse_H_HUM checkResponse(HttpResponse response)
    {  
        GBEServiceResponse_H_HUM oResponse;
        if(response.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK || response.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED) 
        {   
            oResponse = (GBEServiceResponse_H_HUM)System.JSON.deserialize(response.getbody(),GBEServiceResponse_H_HUM.class);            
        }              
        return oResponse;         
    } 
    
    /*
    * Method Name   :  formRequestBody()
    * Description   :  This method is used to form the request body
    * Return Type   :  String
    * Parameters    :  String, String
    */
    @testVisible public static string formEDIDataRequestBody(Account objGrpAccount) 
    {
        HUMPolicyMember_EDIRequest_DTO oEdiRequestDTO = new HUMPolicyMember_EDIRequest_DTO();
        HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest oGetEDIgroup = new HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest();
        HUMPolicyMember_EDIRequest_DTO.GroupDetail oGrpDetail = new HUMPolicyMember_EDIRequest_DTO.GroupDetail()  ;
        oGetEDIgroup.GroupDetail = new List<HUMPolicyMember_EDIRequest_DTO.GroupDetail>();
        oGrpDetail.Group_id = string.isNotBlank(objGrpAccount.Source_Customer_Key__c) ? (objGrpAccount.Source_Customer_Key__c).substring(0,6) : objGrpAccount.Group_Number__c;
        oGetEDIgroup.GroupDetail.add(oGrpDetail);
        oEdiRequestDTO.GetEDIGroupInfoRequest = oGetEDIgroup;
        return JSON.serialize(oEdiRequestDTO); 
    }
    
    /*
    * Method Name   :  setuprequestToGBE
    * Description   :  This method is used to create request DTO and to call the GBE Service
    * Return Type   :  HttpRequest
    * Parameters    :  HUM_Webservice_Callout__c,String
    */    
    public static HttpRequest setupEDIDataRequest(HUM_Webservice_Callout__c oSvcHeaderGBEService, string sRequestBody)
    {
        HttpRequest oRequest = new HttpRequest();
        if(oSvcHeaderGBEService != null)
        {           
            if(oSvcHeaderGBEService != null && oSvcHeaderGBEService.Active__c )
            {
                oRequest.setMethod(oSvcHeaderGBEService.Request_Type__c);
                if(!Test.isRunningTest())oRequest.setClientCertificateName(oSvcHeaderGBEService.Certificate_Name__c);
                oRequest.setEndpoint(oSvcHeaderGBEService.End_Point_URL__c);
                oRequest.setTimeout(Integer.valueOf(oSvcHeaderGBEService.Timeout__c));
                oRequest.setBody(sRequestBody);
            }
        }
        return oRequest;
    }
    
    /*
    * Method Name   :    checkEDIResponse() 
    * Description   :    Method to check the Response from processResponse().
    * return Type   :    EDIResponse_DTO_HUM
    * Parameters    :    HttpResponse
    */   
    public static EDIResponse_DTO_HUM.EdiGroupDetails getEDIResponse(HttpResponse response)
    {  
        EDIResponse_DTO_HUM oResEDI = NULL;
        EDIResponse_DTO_HUM.EdiGroupDetails oEditGroupDetails = NULL;
                
        if(response.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_OK || response.getStatusCode() == GLOBAL_CONSTANT_LH_HUM.HTTP_ACCEPTED) 
        {   
            oResEDI = (EDIResponse_DTO_HUM)System.JSON.deserialize(response.getbody(),EDIResponse_DTO_HUM.class);            
        }   
        
        if(oResEDI!=null &&  oResEDI.GetEDIGroupInfoResponse !=null && oResEDI.GetEDIGroupInfoResponse.EdiGroupDetails !=null && oResEDI.GetEDIGroupInfoResponse.EdiGroupDetails.size() >0){
            oEditGroupDetails = oResEDI.GetEDIGroupInfoResponse.EdiGroupDetails[0];
        }
        return oEditGroupDetails;               
    } 
}