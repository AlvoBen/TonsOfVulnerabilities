/**************************************************************************** 
Apex Class Name  : ClaimPaymentDetails_S_HUM 
Version          : 1.0  
Created Date     : April 22, 2016
Function         : Service that returns Claim Payment details data
Test Class       : ClaimPaymentDetails_ST_HUM 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------
* Muralidhar Kollu        26844               04/22/2016                    Original Version
* Pallavi Ravishankar     29226               06/03/2016                    Added logic for REQ 263142
* Santhi Mandava          29879               06/11/2016                    Implemented standard map changes for claim logging
* Muralidhar Kollu        31408               06/28/2016                    REQ - 249360 - change to Payment Type ID
* Prasanthi Kandula       36268               08/22/2016                    REQ - 277892 Defect 235497: Logic for Displaying Payment Information
* Muralidhar Kollu        41379               09/27/2016                    REQ - 284941 Defect 242718: Update Mapping of Payment Method ID Field
* Santhi Mandava                              03/12/2019                    Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
**************************************************************************************************************************************************/
public with sharing class ClaimPaymentDetails_S_HUM
{
    
    /*
     * Method Name   :  setupRequest
     * Description   :  This method is used to form http request.
     * Return Type   :  HttpRequest 
     * Parameters    :  HttpRequest, HUM_Webservice_Callout__c, String 
     */    
    public HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderPayments, String sRequestBody)
    {
        oRequest.setMethod(oSvcHeaderPayments.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcHeaderPayments.End_Point_URL__c);
         if(!Test.isRunningTest())    
        {
           oRequest.setClientCertificateName(oSvcHeaderPayments.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oSvcHeaderPayments.Timeout__c));
        oRequest.setHeader(GLOBAL_CONSTANT_HUM.Content_type_HUM, oSvcHeaderPayments.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;          
    }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to Process the Json response from service to Wrapper List
    * Return Type   :  List<ClaimsPaymentDetails_DTO_HUM>
    * Parameters    :  string(Json response)
    */
    public List<ClaimsPaymentDetails_DTO_HUM> processResponse(string response)
    {
        try
        {
            
            Endpoint_Url_HUM__c oSsoLogin = Endpoint_Url_HUM__c.getInstance('SSO_LOGINURL');
            Endpoint_Url_HUM__c oChk = Endpoint_Url_HUM__c.getInstance('CheckStatusToolLink');            
           
            ClaimsServicePaymentDetailResponse_H_HUM objResp = new ClaimsServicePaymentDetailResponse_H_HUM();
            List<ClaimsPaymentDetails_DTO_HUM> listPaymentResp = new List<ClaimsPaymentDetails_DTO_HUM>();
            objResp = (ClaimsServicePaymentDetailResponse_H_HUM)System.Json.deserialize(response,ClaimsServicePaymentDetailResponse_H_HUM.class);
            if(objResp.GetEhub835XmlResponse != Null && objResp.GetEhub835XmlResponse.GetEhub835XmlResult != Null && objResp.GetEhub835XmlResponse.GetEhub835XmlResult.Transaction_835  != Null)
            {
                List<ClaimsServicePaymentDetailResponse_H_HUM.Transaction_835> lstPayments = objresp.GetEhub835XmlResponse.GetEhub835XmlResult.Transaction_835;

                for(ClaimsServicePaymentDetailResponse_H_HUM.Transaction_835 obj :lstPayments)
                {
                    ClaimsPaymentDetails_DTO_HUM objWrapper = new ClaimsPaymentDetails_DTO_HUM();
                    objWrapper.sCheckNumber    =    obj.CheckNumber;
                    objWrapper.sPayeeName      =    obj.PayeeName;
                    objWrapper.sPayeeAddress   =    obj.PayeeAddress;
                    objWrapper.sCheckDate      =    obj.CheckDate;
                    objWrapper.sPaymentType    =    obj.PaymentType;
                    objWrapper.sPaymentAmount  =    obj.PaymentAmount;
                    objWrapper.sPayeeID        =    obj.PayeeID;
                    objWrapper.sPaymentIssueDate=   obj.PaymentIssueDate;
                    objWrapper.sPaymentStatus  =    obj.PaymentStatus;
                    objWrapper.sPaymentStatusDate = obj.PaymentStatusDate;
                    objWrapper.bPaymentInfoMsg = false;
                    objWrapper.sPaymentTypeID  =    obj.CheckNumber;                                         
                    
                    if(obj.PaymentType=='CHK')
                    {
                        objWrapper.sPaymentUrl = oSsoLogin.Remote_Url__c +'?TARGET='+ oChk.Remote_Url__c;  
                    }
                    if(obj.ClaimDetails != null)
                    {
                        List<ClaimsServicePaymentDetailResponse_H_HUM.ClaimDetail> lstclaims = obj.ClaimDetails.ClaimDetail; 
                        List<ClaimsPaymentDetails_DTO_HUM.ClaimDetail> listPaymentClaimsResp = new List<ClaimsPaymentDetails_DTO_HUM.ClaimDetail>();                               
                        for(ClaimsServicePaymentDetailResponse_H_HUM.ClaimDetail objClaim : lstclaims )
                        {
                            ClaimsPaymentDetails_DTO_HUM.ClaimDetail cDetail = new ClaimsPaymentDetails_DTO_HUM.ClaimDetail();
                            cDetail.ClaimNumber = objClaim.ClaimNumber;
                            cDetail.ClaimPaymentAmount = objClaim.ClaimPaymentAmount;
                            listPaymentClaimsResp.add(cDetail);
                        }
                           
                        if(listPaymentClaimsResp != null)
                        {
                            objWrapper.ClaimDetails = new ClaimsPaymentDetails_DTO_HUM.ClaimDetails();
                            objWrapper.ClaimDetails.ClaimDetail = listPaymentClaimsResp;                        
                        }
                    }
                    listPaymentResp.add(objWrapper);
                                                       
                }
                return listPaymentResp;
            } 
            else
            {
                 return listPaymentResp;
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'ClaimPaymentDetails_S_HUM', 'processResponse');
            return Null;
        }
    }
}