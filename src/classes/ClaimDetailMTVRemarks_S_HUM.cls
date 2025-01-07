/**************************************************************************** 
Apex Class Name  : ClaimDetailMTVRemarks_S_HUM 
Version          : 1.0  
Created Date     : March 31, 2016
Function         : Service that returns MTV Claim Remarks data
Test Class       : ClaimDetailMTVRemarksService_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Muralidhar Kollu                            03/31/2016                    Original Version
* Samruddhi Rathi	                          05/07/2021         REQ - 2230063 Humana Constant CleanUp
* Apurva Urkude	                          	10/20/2021         REQ - 2132792
* Nirmal Garg	                          	10/25/2021         Removing claim remark special chars
***************************************************************************************************/
public with sharing class ClaimDetailMTVRemarks_S_HUM
{	
	Public Static final String Content_type_HUM = 'content-type';
    /*
    * Returns an object 
    * <p>
    * This method is used to assign values to the request helper class
    * @param  String,String
    * @return MTVRemarksServiceGetClaimsRequest_H_HUM 
    */    
    public MTVRemarksServiceGetClaimsRequest_H_HUM assignValuesToHelper(Boolean bSecondCall, String sClaimNumber, String sCreationDate, String sCreationTime, String sSurrogateId, String sStartRemarkSequenceNumber )
    {
        MTVRemarksServiceGetClaimsRequest_H_HUM oMTVRemarksReq = new MTVRemarksServiceGetClaimsRequest_H_HUM();
        oMTVRemarksReq.ClaimNumber = sClaimNumber;
        if(!bSecondCall)
        {
            
            oMTVRemarksReq.CreationDate = '';
            oMTVRemarksReq.CreationTime  = '';
            oMTVRemarksReq.SurrogateId = '';
            oMTVRemarksReq.StartRemarkSequenceNumber = '';        
        }
        else
        {
            oMTVRemarksReq.CreationDate = sCreationDate;
            oMTVRemarksReq.CreationTime  = sCreationTime;
            oMTVRemarksReq.SurrogateId = sSurrogateId;
            oMTVRemarksReq.StartRemarkSequenceNumber = sStartRemarkSequenceNumber;              
        }
        return oMTVRemarksReq;
    }
    
    /*
     * Method Name   :  setupRequest
     * Description   :  This method is used to form http request.
     * Return Type   :  HttpRequest 
     * Parameters    :  HttpRequest, HUM_Webservice_Callout__c, String 
     */    
    public HttpRequest setupRequest(HttpRequest oRequest,HUM_Webservice_Callout__c oSvcHeaderMTVRemarks, String sRequestBody)
    {
        oRequest.setMethod(oSvcHeaderMTVRemarks.Request_Type__c);
        oRequest.setBody(sRequestBody);
        oRequest.setEndpoint(oSvcHeaderMTVRemarks.End_Point_URL__c);
        if(!Test.isRunningTest())    
        {
           oRequest.setClientCertificateName(oSvcHeaderMTVRemarks.Certificate_Name__c);
        }
        oRequest.setTimeout(Integer.valueof(oSvcHeaderMTVRemarks.Timeout__c));
        oRequest.setHeader(Content_type_HUM, oSvcHeaderMTVRemarks.Content_Type__c);
        oRequest.setHeader('SOAPAction', '');    
        oRequest.setHeader('Connection','keep-alive');
        return oRequest;          
    }
    
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to Process the Json response from service to Wrapper List
    * Return Type   :  List<ClaimDetailMTVRemarks_DTO_HUM>
    * Parameters    :  string(Json response)
    */
    public List<ClaimDetailMTVRemarks_DTO_HUM> processResponse(string response) 
    {  
        try
        {
            MTVRemarksServiceGetClaimsResponse_H_HUM objResp = new MTVRemarksServiceGetClaimsResponse_H_HUM ();
            List<ClaimDetailMTVRemarks_DTO_HUM> listMTVResp = new List<ClaimDetailMTVRemarks_DTO_HUM>();
            objResp = (MTVRemarksServiceGetClaimsResponse_H_HUM)System.Json.deserialize(response,MTVRemarksServiceGetClaimsResponse_H_HUM.class);
            if(objResp.MTVRemarksResponse != Null)
            {
                
                MTVRemarksServiceGetClaimsResponse_H_HUM.RemarksList lstRemarks = objresp.MTVRemarksResponse.RemarksList;
                for(MTVRemarksServiceGetClaimsResponse_H_HUM.Remarks obj:lstRemarks.Remarks)
                {
                    ClaimDetailMTVRemarks_DTO_HUM objWrapper = new ClaimDetailMTVRemarks_DTO_HUM();
                  
                    objWrapper.sCreationDate = objresp.MTVRemarksResponse.CreationDate;
                    objWrapper.sCreationTime = objresp.MTVRemarksResponse.CreationTime;
                    objWrapper.sSurrogateId = objresp.MTVRemarksResponse.SurrogateId;
                    objWrapper.sStartRemarkSequenceNumber = objresp.MTVRemarksResponse.StartRemarkSequenceNumber;                  
                    objWrapper.sPagingInd = objresp.MTVRemarksResponse.PagingInd;
                    objWrapper.sRemarkEntityType = obj.EntityType;
                    objWrapper.sRemarkIdentifier = obj.Identifier;
                    objWrapper.sRemarkSequenceNumber = obj.RemarkSequenceNumber;
                    objWrapper.sRemarkType = obj.RemarkType;
                    objWrapper.sRemarkCategory = obj.RemarkCategory;
                    objWrapper.sRemarkCreationTimestamp = obj.CreationTimestamp;
                    objWrapper.sRemarkText = obj.RemarkText;
                    objWrapper.sRemarkExtendedText = obj.RemarkText;
                    objWrapper.sRemarkSurrogateId = obj.SurrogateId;
                    listMTVResp.add(objWrapper);
                }
                return listMTVResp ;
            } 
            else    return listMTVResp ;
        }
        catch(Exception ex)
        {
          HUMExceptionHelper.logErrors(ex, 'ClaimDetailMTVRemarks_S_HUM', 'processResponse');
          return null;
        }
    }
  /*
    * Method Name   :  processResponse
    * Description   :  This method is used to Process the Json response from service to Wrapper List
    * Return Type   :  List<ClaimDetailMTVRemarks_DTO_HUM>
    * Parameters    :  string(Json response)
    */
    public List<ClaimDetailMTVRemarks_DTO_HUM> processResponseNew(string response)  
    {  
        try
        {
            MTVRemarksGetClaimsResponse_H_HUM_V1 objResp = new MTVRemarksGetClaimsResponse_H_HUM_V1 ();
            List<ClaimDetailMTVRemarks_DTO_HUM> listMTVResp = new List<ClaimDetailMTVRemarks_DTO_HUM>();
            objResp = (MTVRemarksGetClaimsResponse_H_HUM_V1)System.Json.deserialize(response,MTVRemarksGetClaimsResponse_H_HUM_V1.class);
            if(objResp.ClaimsRemarksResponse != Null)
            {
                
                MTVRemarksGetClaimsResponse_H_HUM_V1.RemarksList lstRemarks = objresp.ClaimsRemarksResponse.RemarksList;
                for(MTVRemarksGetClaimsResponse_H_HUM_V1.Remarks obj:lstRemarks.Remarks)
                {
                     ClaimDetailMTVRemarks_DTO_HUM objWrapper = new ClaimDetailMTVRemarks_DTO_HUM();                                      
                    objWrapper.sRemarkEntityType = obj.EntityType;
                    objWrapper.sRemarkIdentifier = obj.ClaimNumber;
                    objWrapper.sRemarkType = obj.RemarkTypeCode;
                    objWrapper.sRemarkCategory = obj.RemarkCategory;
                    objWrapper.sRemarkCreationTimestamp = '';
                    objWrapper.sRemarkText = string.isnotblank(obj.RemarkText)?obj.RemarkText.replace('~ /','\n')
                        .replace(':~',' : ').replace('~}', ')').replace('{','(').replace('**','\n').replace('+',' ')
                        .replace('~}',')').replace('/',' ').replace('}',')')
                        : obj.RemarkText ;   
                   	objWrapper.sRemarkCreationTimestamp = formatRemarkDate(obj.RemarkPlatformCreationDate);
					objWrapper.sRemarkExtendedText = string.isnotblank(obj.RemarkText)?obj.RemarkText.replace('~ /','\n')
                        .replace(':~',' : ').replace('~}', ')').replace('{','(').replace('**','\n').replace('+',' ')
                        .replace('~}',')').replace('/',' ').replace('}',')')
                        : obj.RemarkText ; 
                    listMTVResp.add(objWrapper);
                }
                return listMTVResp ;
            } 
            else    return listMTVResp ;                                                    
          
        }
        catch(Exception ex)
        {
          HUMExceptionHelper.logErrors(ex, 'ClaimDetailMTVRemarks_S_HUM', 'processResponse');
          return null;
        }
    }
	
	private string formatRemarkDate(string inputDate){
        string outputdate = '';
        try{
            if(string.isNotBlank(inputDate) && inputDate.contains('-')){
                string[] sTempdate = inputDate.split('-');
                outputdate =  (sTempdate[1] != null ? sTempdate[1] : '') + '/' + 
                            (sTempdate[2] != null ? sTempdate[2] : '') + '/' + 
                    (sTempdate[0] != null ? sTempdate[0]
                            : '') + ' ' +  (sTempdate[3] != null ? sTempdate[3]
                            : '');
            }
            return outputdate;
        }
        catch(Exception ex){
            return outputdate;
        }
    }
}