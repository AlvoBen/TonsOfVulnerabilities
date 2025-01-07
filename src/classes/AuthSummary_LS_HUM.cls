/*Apex class Name  : AuthSummary_LS_HUM
Version            : 1.0
Function           : It is service class
Created Date       : 14/07/2022 
Test Class         : Authsummary_LT_HUM
*/
/*******************************************************************************************
Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
* Rajesh Narode                                         14/07/2022         User story 3362694 Authorization Summary table
* Anuradha Gajbhe                                       28/07/2022         DF-5462 Fix
* Anuradha Gajbhe                                       05/12/2022         User story 3778026 - Auth/Referral: Lightning Build: Home Office Auths/referrals.
*********************************************************************************************************************************/
public with sharing class AuthSummary_LS_HUM {
   @testVisible private  String sBodyReq ='' ;
    private AuthReferral_Query_LD_HUM objdatalayer = new AuthReferral_Query_LD_HUM();
    private Integer count = 0;
    private Integer pageNum = 1;
    ID curentUserID = UserInfo.getUserId();
    public Account objAcct =  new Account();
    AuthReferral_Security_LH_HUM objAuthSecurity = new AuthReferral_Security_LH_HUM();
    AuthReferral_SecurityHO_LH_HUM cHomeOfficeSecurity=new AuthReferral_SecurityHO_LH_HUM();
    public Boolean bViewResults = true;
    public Boolean bShowMessage ;
    private List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthRefferal = new List<AuthRefferal_Wrapper_LDTO_HUM>();
    public Integer totalRecordFound = 0;
    public static string memPlanId{get;set;}
    /*
     * Method Name : AuthSummary_S_HUM
     * Description : Constructor for the class , initialize variable
     * Return type : NA
     * Parameter   : NA
     */
    public AuthSummary_LS_HUM()
    {
        pageNum = 1;
        count = 0;
        bViewResults = true;
        totalRecordFound = 0;
    }
    /*
     * Method Name   :  setupRequest
     * Description   :  This method is used to form http request.
     * Return Type   :  HttpRequest 
     * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Member Id)
     */
    
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderClinical,string sRecId, Integer sPageNum)
    {
      
      try
      { 
          memPlanId = sRecId;
          sBodyReq = buildRequestv2(sRecId, sPageNum);
          if(Null != sBodyReq)
          {
              request.setMethod(oSvcHeaderClinical.Request_Type__c);
              request.setBody(sBodyReq );
              request.setEndpoint(oSvcHeaderClinical.End_Point_URL__c);
              request.setClientCertificateName(oSvcHeaderClinical.Certificate_Name__c);
              request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderClinical.Content_Type__c);
              return request;
          }
          else
          {
              return Null;  
          }
      }
      catch(Exception ex)
      {
          HUMExceptionHelper.logErrors(ex, 'AuthSummary_LS_HUM', 'setupRequest');
          return Null;
      }
    }
    
    /* Addded by Sathish for Auth/Reff GOTO
     * Method Name   :  setupGoButtonRequest
     * Description   :  This method is used to form http request.
     * Return Type   :  HttpRequest 
     * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Member Id)
     */
    
    public HttpRequest setupGoButtonRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderGoAuth,string sAuthNumber)
    {
      
      try
      { 
        sBodyReq = buildRequestGo(sAuthNumber);
          if(Null != sBodyReq)
          {
              request.setMethod(oSvcHeaderGoAuth.Request_Type__c);
              request.setBody(sBodyReq );
              request.setEndpoint(oSvcHeaderGoAuth.End_Point_URL__c);
              request.setClientCertificateName(oSvcHeaderGoAuth.Certificate_Name__c);
              request.setHeader(Label.HUMServiceTesterController_content_type, oSvcHeaderGoAuth.Content_Type__c);
              return request;
          }
          else
          {
              return Null;  
          }
      }
      catch(Exception ex)
      {
          HUMExceptionHelper.logErrors(ex, 'AuthSummary_S_HUM', 'setupGoButtonRequest');
         return Null;
      }
    }
    
    /*
    * Method Name   :  buildRequestGo
    * Description   :  This method Is used to build the required inputs need to hit the service
    * Return Type   :  String
    * Parameters    :  String(Auth Number)
   */
    Public string buildRequestGo(String sAuthNumber)
    {    
      
       string sJsonBodyGo = '';
        try
        {
            if(String.isNotBlank(sAuthNumber))
            { 
                ClinicalAuthSummaryRequest_H_HUM_V2 omain = New ClinicalAuthSummaryRequest_H_HUM_V2();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest oreq = New ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory oauthcategory = New ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData  oPageMetaData = New ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData();
                //oreq.AuthID = sAuthNumber;
                oreq.PageMetaData = oPageMetaData;
                omain.AuthSummaryRequest = oreq;
                sJsonBodyGo = json.serialize(omain);
                bViewResults = False;
                return sJsonBodyGo ;                
            }
            else
            {
             return null;
            }
      }
      catch(Exception ex)
      {
          HUMExceptionHelper.logErrors(ex, 'AuthSummary_S_HUM', 'setupGoButtonRequest');
          return Null;
      }
     
    } 
    
                            /*
    * Method Name   :  buildRequestv2
    * Description   :  This method Is used to build the required inputs need to hit the service
    * Return Type   :  String
    * Parameters    :  String(Policy Member Id)
    */
    Public string buildRequestv2(String sRecId, Integer sPageNum)
    {      
        If (bViewResults) 
                                            {
            objAcct = AuthReferral_Query_LD_HUM.getMemberAccount(sRecId);                                       
            If (objAcct.Enterprise_ID__c != Null && objAcct.Birthdate__c != Null) 
                                                    {
                ClinicalAuthSummaryRequest_H_HUM_V2 omain = New ClinicalAuthSummaryRequest_H_HUM_V2();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest oreq = New ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalAuthSummaryRequest();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory oauthcategory = New ClinicalAuthSummaryRequest_H_HUM_V2.ClincalAuthcategory();
                ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData  oPageMetaData = New ClinicalAuthSummaryRequest_H_HUM_V2.ClinicalPageMetaData();
                oreq.MemberId = objAcct.Enterprise_ID__c;
                oreq.MemberIdType = 'SdrPersonID'; 
                oreq.DateOfBirth = objAcct.Birthdate__c;
                oreq.IncludeCDRAuth = 'false';
                oauthcategory.AuthCategory = 'All';
                oreq.AuthFilter = oauthcategory;
                oPageMetaData.PageSize = 50;
                                oPageMetaData.PageNumber = sPageNum;
               
                oreq.PageMetaData = oPageMetaData;
                omain.AuthSummaryRequest = oreq;
                String jsonbody = json.serialize(omain);
                                bViewResults = False;
                return jsonbody;                
            }
            else
            {
             return null;
            }
          }
          else
          {
            return null;
          }
    }
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to Process the Json response from service to Wrapper List
    * Return Type   :  List<AuthReferral_Wrapper_H_HUM>
    * Parameters    :  string(Json response)
    */
    public List<AuthRefferal_Wrapper_LDTO_HUM> processResponse(string response, string sRecId) 
    {  
        try
        {
            ClinicalAuthSummaryResponse_H_HUM objresp = new ClinicalAuthSummaryResponse_H_HUM();
            List<AuthRefferal_Wrapper_LDTO_HUM> listAuthResp = new List<AuthRefferal_Wrapper_LDTO_HUM>();
            objresp = (ClinicalAuthSummaryResponse_H_HUM)Json.deserialize(response,ClinicalAuthSummaryResponse_H_HUM.class);
            if(objresp.AuthSummaryResponse != Null)
            {
                listAuthResp = parseResponse(objresp, sRecId);
                return listAuthResp;
            } 
            else
            {
                return listAuthResp;
            }
        }
        catch(Exception ex)
        {
          HUMExceptionHelper.logErrors(ex, 'AuthSummary_LS_HUM', 'processResponse');
          return Null;
        }
    }
    /*
    * Method Name   :  parseResponse
    * Description   :  This method is used to assign the response to variables to wrapper Class
    * Return Type   :  List<AuthReferral_Wrapper_H_HUM>
    * Parameters    :  ClinicalAuthSummaryResponse_H_HUM object
    */
    Private List<AuthRefferal_Wrapper_LDTO_HUM> parseResponse(ClinicalAuthSummaryResponse_H_HUM response, string sRecId)
    {   
        List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_LDTO_HUM>();
        ClinicalAuthSummaryResponse_H_HUM.AuthSummaryResponse oAuthSummaryResponse = response.AuthSummaryResponse ;
        if( oAuthSummaryResponse.TotalRecordsFound != null)
        {
            totalRecordFound =  Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound); 
        }
        if(oAuthSummaryResponse != null && oAuthSummaryResponse.AuthSummaryList != null)
        {
            ClinicalAuthSummaryResponse_H_HUM.AuthSummaryList oauthsumlist = oAuthSummaryResponse.AuthSummaryList;
            List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstauth = oauthsumlist.AuthSummary;
            
            lstAuthsummary = buildWrapperList(lstauth,totalRecordFound, sRecId);
            lstAuthRefferal = checkSecurity(lstAuthsummary, sRecId);
            return lstAuthRefferal;
        }
        else
        {
            return null;
        }
    }
    /*
    * Method Name   :  buildWrapperList
    * Description   :  This method is used to assign the response to variables to wrapper Class
    * Return Type   :  List<AuthReferral_Wrapper_H_HUM>
    * Parameters    :  List<ClinicalAuthSummaryResponse_H_HUM>
    */
    private List<AuthRefferal_Wrapper_LDTO_HUM> buildWrapperList(List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstauth, Integer totalRecordFound, string sRecId)
    {
        List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_LDTO_HUM>();
        for(ClinicalAuthSummaryResponse_H_HUM.AuthSummary obj:lstauth )
        {
            AuthRefferal_Wrapper_LDTO_HUM objwrapper = new AuthRefferal_Wrapper_LDTO_HUM();
            objwrapper.sAuthorizationOrReferralNumber = obj.AuthID;
            objwrapper.sAuthorizationType = obj.AuthType;
            objwrapper.sOverallStatus = obj.AuthStatus;
            objwrapper.Totalcount = totalRecordFound;
            
            if(obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_BHINP_HUM || obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_INP_HUM)
            {
               if(! String.isBlank(obj.InpatientDetails.AdmissionDate))     objwrapper.sAdmFirstDay = formateDate(obj.InpatientDetails.AdmissionDate);
               
               if(! String.isBlank(obj.InpatientDetails.DischargeDate))     objwrapper.sDischargeLastDay = formateDate(obj.InpatientDetails.DischargeDate);
               
               if(! String.isBlank(obj.InpatientDetails.AdmissionType))     objwrapper.sServiceType = obj.InpatientDetails.AdmissionType;
            }
            if(obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_OUP_HUM || obj.AuthType== GLOBAL_CONSTANT_HUM.AUTHTYPE_BHOUP_HUM)
            {
               if(! String.isBlank(obj.OutpatientDetails.FirstDay))       objwrapper.sAdmFirstDay = formateDate(obj.OutpatientDetails.FirstDay);
               
               if(! String.isBlank(obj.OutpatientDetails.LastDay))        objwrapper.sDischargeLastDay = formateDate(obj.OutpatientDetails.LastDay);
               
               if(! String.isBlank(obj.OutpatientDetails.ServiceType))    objwrapper.sServiceType = obj.OutpatientDetails.ServiceType;
            }
            objwrapper.sFacility = obj.FacilityProviderName;
            objwrapper.sRequestingrovider = obj.RequestingProviderName;
            objwrapper.sTreatingProvider = obj.TreatingProviderName;
            objwrapper.sGroupId = obj.GroupId;
            objwrapper.bIsAccessible  = true;
            objwrapper = updateUrl(objwrapper, sRecId);
            lstAuthsummary.add(objwrapper);
        }
        return lstAuthsummary;
    }
    
   public AuthRefferal_Wrapper_LDTO_HUM updateUrl(AuthRefferal_Wrapper_LDTO_HUM authRefUrl, String sRecId){
        
        try{      
                                memPlanId = sRecId;
            if(authRefUrl != null){
                String authType = authRefUrl.sAuthorizationType;
                                String authStatus = authRefUrl.sOverallStatus;
                String authTypeJS;
                boolean bOnSearch = false;
                if(authType.equals('Inpatient')) {
                        authTypeJS = 'Auth:' + authRefUrl.sAuthorizationOrReferralNumber;
                } else if(authType.equals('Outpatient')) {
                        authTypeJS = 'Auth/Referral:' + authRefUrl.sAuthorizationOrReferralNumber; 
                }
                authRefUrl.sAtuhRefUrl = '/lightning/n/authsummary_medical_authorization_detail?'+
                                        '&AuthId='+authRefUrl.sAuthorizationOrReferralNumber+
                                        '&search='+bOnSearch+
                                        '&type=Authorizations'+
                                        '&subtype='+authTypeJS+
                                                              '&Authtype='+authType+
                                                              '&AuthStatus='+authStatus;
            }            
        }catch(Exception ex){
            return null;
        }
        return authRefUrl;
    }
    /*
     * Method Name   :  checkSecurity
     * Description   :  This method is used to pass the wraper list to security class to verify whether it is accessible for user or not.
     * Return Type   :  List<AuthReferral_Wrapper_H_HUM>
     * Parameters    :  List<AuthReferral_Wrapper_H_HUM>
     */
    private List<AuthRefferal_Wrapper_LDTO_HUM> checkSecurity(List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary, ID sRecId){
            if(sRecId != null && string.isNotBlank(sRecId))
				{
				objAcct = AuthReferral_Query_LD_HUM.getMemberAccount(sRecId);  
                }
        return cHomeOfficeSecurity.getFilteredAuths(lstAuthsummary,curentUserID,objAcct.Id);
    }
    
    /*
     * Method Name   :  formateDate
     * Description   :  This method is used to formate date with time stamp to dd/mm/yyyy 
     * Return Type   :  string 
     * Parameters    :  string 
     */
    public string formateDate(string sdateTime)
    {
        string[] sArrayDate = (sdateTime.substringBefore('T')).split('-');
        string sDate = sArrayDate[1]+'/'+sArrayDate[2]+'/'+sArrayDate[0];
        return sDate  ;
    }
    
    /*
     * Method Name   :  checkAuthSecurity
     * Description   :  This method is used to check the security on search of Authorization.
     * Return Type   :  Boolean 
     * Parameters    :  ClinicalAuthDetailsResponse_H_HUM 
     */
                
    public Boolean checkAuthSecurity(ClinicalAuthDetailsResponse_H_HUM oResponse,String sRecId)
    {
        Boolean bIsAccible = true;
        ClinicalAuthDetailsResponse_H_HUM.AuthorizationsResponse oAuthResponse = oResponse.AuthorizationsResponse;
        ClinicalAuthDetailsResponse_H_HUM.Authorizations oAuthorizations = oAuthResponse.Authorizations;
        List<ClinicalAuthDetailsResponse_H_HUM.Authorization> lstAuthorization = new List<ClinicalAuthDetailsResponse_H_HUM.Authorization>();
        
        if(oAuthorizations != null)
        {
            if(sRecId != null && string.isNotBlank(sRecId))
            {
               objAcct = AuthReferral_Query_LD_HUM.getMemberAccount(sRecId);  
            }
            lstAuthorization = oAuthorizations.Authorization;            
            List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_LDTO_HUM>();
            AuthRefferal_Wrapper_LDTO_HUM objauth = new AuthRefferal_Wrapper_LDTO_HUM();
            objauth.sAuthorizationOrReferralNumber = lstAuthorization[0].AuthID;
            objauth.sGroupId = lstAuthorization[0].GroupId;
            objauth.bIsAccessible = true;
            lstAuthsummary.add(objauth);
            lstAuthsummary = cHomeOfficeSecurity.getFilteredAuths(lstAuthsummary,curentUserID,objAcct.Id);
            if(lstAuthsummary!= null && !lstAuthsummary.isEmpty())
            {
                if(lstAuthsummary[0].bIsAccessible)
                {
                    bIsAccible = false;  
                }
                else
                {
                    bIsAccible = true;
                }
            }
        }
        return bIsAccible;
    }
}