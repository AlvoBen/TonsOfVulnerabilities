/*****************************************************************************************
Apex class Name    : DualEligibleDetails_S_HUM
Version            : 1.0
Function           : This is the Service class for GetDualInfo Service 
Created Date       : 05/18/2019 
Test Class         : DualEligibleDetails_S_T_HUM

Modification Log:
*   Developer                   Code Review             Date                Description
* ------------------------------------------------------------------------------------------------------------------------------   
*  Pavan Karnam                                    05/18/2019          Controller Class for Dual/Moop Project.   
*********************************************************************************************************************************/ 


public with sharing class DualEligibleDetails_S_HUM{
    
    @TestVisible private String sBody ='' ;
    public DualEligibleDetails_DTO_HUM lstDualDeemingDetails = new DualEligibleDetails_DTO_HUM();
    public List<DualEligibleDetails_DTO_HUM> lstDualEligibleStatusVals{get;set;}
    public List<DualEligibleDetails_DTO_HUM> lstDualEligibleDeemingVals{get;set;}
    
    /*
*  This method is used to form http request for getTaskHierarchy Servive
*  @name   setupRequest
*  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
sCaseID-CaseId                       
*  @return HttpRequest - request for getCaseDetail Service
*/
    public HttpRequest setupRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcEncrypt, String sPersonId)
    {
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            sBody = buildRequest(sPersonId);
            if(String.isNotBlank(sBody))
            { 
                request.setMethod(oSvcEncrypt.Request_Type__c); 
                request.setHeader(GLOBAL_CONSTANT_HUM.CONTENT_TYPE_HUM, oSvcEncrypt.Content_Type__c);  
                request.setHeader(GLOBAL_CONSTANT_HUM.CONNECTION_HUM,GLOBAL_CONSTANT_HUM.KEEP_ALIVE_HUM);
                if(!Test.isRunningTest())    
                {
                    request.setClientCertificateName(oSvcEncrypt.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcEncrypt.Timeout__c));
                request.setEndpoint(oSvcEncrypt.End_Point_URL__c);
                request.setBody(sBody);
                orequest = request; 
            } 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'DualEligibleDetails_S_HUM' , 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }  
    
    
    /*
*  This method is used to build Json request for getCaseDetail Service
*  @name   buildRequest
*  @param  String sCaseID- CaseId
*  @return string - Returns the request with scaseId in JSON format
*  @throws NA
*/ 
    @testvisible private string buildRequest(String sPersonId)
    {    
        string sJsonbody = '';
        try
        { 
            DualEligibleDetailsRequest_H_HUM oRequest = new DualEligibleDetailsRequest_H_HUM(); 
            DualEligibleDetailsRequest_H_HUM.GetMember oGetDualMem = new DualEligibleDetailsRequest_H_HUM.GetMember();
            DualEligibleDetailsRequest_H_HUM.Member oGetMem = new DualEligibleDetailsRequest_H_HUM.Member();
            DualEligibleDetailsRequest_H_HUM.DualStatusList oGetStatus = new DualEligibleDetailsRequest_H_HUM.DualStatusList();            
            DualEligibleDetailsRequest_H_HUM.DeemingPeriodList oGetDeeming = new DualEligibleDetailsRequest_H_HUM.DeemingPeriodList();
            DualEligibleDetailsRequest_H_HUM.GetCriteria oGetCriteria = new DualEligibleDetailsRequest_H_HUM.GetCriteria();
            DualEligibleDetailsRequest_H_HUM.PersonIdCriteria oGetPerCriteria = new DualEligibleDetailsRequest_H_HUM.PersonIdCriteria();
            DualEligibleDetailsRequest_H_HUM.PeriodList oGetPeriodList = new DualEligibleDetailsRequest_H_HUM.PeriodList();
            oGetPerCriteria.PersonId = sPersonId;
            oRequest.GetMember = oGetDualMem;
            oRequest.GetMember.Member = oGetMem; 
            oRequest.GetMember.Member.DualStatusList = oGetStatus;  
            oRequest.GetMember.Member.DeemingPeriodList = oGetDeeming;
            oRequest.GetMember.Member.GetCriteria = oGetCriteria;
            oRequest.GetMember.Member.GetCriteria.PersonIdCriteria = oGetPerCriteria;
            oRequest.GetMember.Member.GetCriteria.PersonIdCriteria.PeriodList = oGetPeriodList;     
            sJsonbody = JSON.serialize(oRequest); 
        }
        catch(Exception ex)
        { 
            HUMExceptionHelper.logErrors(ex, 'DualEligibleDetails_S_HUM' , 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    } 
    
    
    /*
*  This method is used to Process the Json response from getCaseDetail service to Object
*  @name   processResponse
*  @param  string(Json response) - Body of the response in JSON format
*  @return void
*  @throws NA 
*/
    
    public DualEligibleDetails_DTO_HUM processResponse(string response) 
    {  
        DualEligibleDetailsResponse_H_HUM  oMoopResponse = new DualEligibleDetailsResponse_H_HUM();
        if(response!=null)
        { 
            oMoopResponse = DualEligibleDetailsResponse_H_HUM.parse(response);
        }
        if(oMoopResponse != null && oMoopResponse.GetMemberResponse != Null)
        { 
            lstDualDeemingDetails = parseServiceResponse(oMoopResponse);
        } 
        return lstDualDeemingDetails;
    }
    
    /*
*  This method is used to return wrapper list for Dual Status Details
*  @name   DualStatus
*  @return List<DualEligibleDetails_DTO_HUM>
*  @throws NA*/
    
    public List<DualEligibleDetails_DTO_HUM>  DualStatus()
    { 
        return lstDualEligibleStatusVals;
    }
    
     /*
*  This method is used to return wrapper list for Linked Cases
*  @name   DeemingPeriod
*  @return List<DualEligibleDetails_DTO_HUM>
*  @throws NA*/
    
    public List<DualEligibleDetails_DTO_HUM>  DeemingPeriod()
    { 
        return lstDualEligibleDeemingVals;
    }
    
    
    /*
*  This method is used to parse the getCaseDetail service reponse 
*  @name   parseServiceResponse
*  @param  DualEligibleDetailsResponse_H_HUM - The Response object
*  @throws NA
*/
    private DualEligibleDetails_DTO_HUM parseServiceResponse(DualEligibleDetailsResponse_H_HUM oMoopResponse)
    { 
        DualEligibleDetails_DTO_HUM oMoopDetailParse = new DualEligibleDetails_DTO_HUM();
        DualEligibleDetailsResponse_H_HUM.GetMemberResponse oMoopDetailResponse = oMoopResponse.GetMemberResponse;
        DualEligibleDetailsResponse_H_HUM.Body oMoopDetailBody = oMoopDetailResponse.Body;
        
        List<DualEligibleDetailsResponse_H_HUM.DualStatusList> lstDualStatusResponse = oMoopDetailBody.DualStatusList;     
        List<DualEligibleDetailsResponse_H_HUM.DeemingPeriodList> lstDualDeemingResponse = oMoopDetailBody.DeemingPeriodList;
        lstDualEligibleStatusVals =  buildWrapperDualsList(lstDualStatusResponse);
        lstDualEligibleDeemingVals = buildWrapperDeemingList(lstDualDeemingResponse);
        return oMoopDetailParse;
        
    }
    
    /*
*  This method is used to parse the Duals status List from Moop service and assign it to the wrapper class objects 
*  @name   buildWrapperDualsList
*  @param  List<DualEligibleDetails_DTO_HUM>  - The Response object
*  @throws NA
*/
    
    public List<DualEligibleDetails_DTO_HUM> buildWrapperDualsList(List<DualEligibleDetailsResponse_H_HUM.DualStatusList> lstStatus)
    {
        List<DualEligibleDetails_DTO_HUM>  lstStatusWrapper = new List<DualEligibleDetails_DTO_HUM>();
        for(DualEligibleDetailsResponse_H_HUM.DualStatusList oDualStatus : lstStatus)
        { 
            DualEligibleDetails_DTO_HUM objWrapper = new DualEligibleDetails_DTO_HUM();
            if(oDualStatus.EffectiveDate != null) objWrapper.sEffectiveDate =  Date.valueOf(oDualStatus.EffectiveDate).format();
            if(oDualStatus.EndDate != null) objWrapper.sEndDate =  Date.valueOf(oDualStatus.EndDate).format();
            objWrapper.sValue = oDualStatus.Value;
            if(oDualStatus.Indicator == 'true'){ objWrapper.sIndicator = 'Y'; } else {objWrapper.sIndicator = 'N';}     
            lstStatusWrapper.add(objWrapper);
        }
        return lstStatusWrapper;  
    }
    
    
    /*
*  This method is used to parse the Duals status List from Moop service and assign it to the wrapper class objects 
*  @name   buildWrapperDeemingList
*  @param  List<DualEligibleDetails_DTO_HUM>  - The Response object
*  @throws NA
*/
    
    public List<DualEligibleDetails_DTO_HUM> buildWrapperDeemingList(List<DualEligibleDetailsResponse_H_HUM.DeemingPeriodList> lstDeeming)
    {
        List<DualEligibleDetails_DTO_HUM>  lstDeemingWrapper = new List<DualEligibleDetails_DTO_HUM>();
        for(DualEligibleDetailsResponse_H_HUM.DeemingPeriodList oDeemingStatus : lstDeeming)
        { 
            DualEligibleDetails_DTO_HUM objWrapperDeeming = new DualEligibleDetails_DTO_HUM();
            if(oDeemingStatus.EffectiveDate != null) objWrapperDeeming.sEffectiveDateDeeming =  Date.valueOf(oDeemingStatus.EffectiveDate).format();
            if(oDeemingStatus.EndDate != null) objWrapperDeeming.sEndDateDeeming =  Date.valueOf(oDeemingStatus.EndDate).format();        
            if(oDeemingStatus.Indicator == 'true'){ objWrapperDeeming.sIndicatorDeeming = 'Y'; } else {objWrapperDeeming.sIndicatorDeeming = 'N';}        
            lstDeemingWrapper.add(objWrapperDeeming);
        }
        return lstDeemingWrapper;  
    }
}