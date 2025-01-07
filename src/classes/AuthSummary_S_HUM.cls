/*****************************************************************************************
Apex class Name    : AuthSummary_S_HUM
Version            : 1.0
Function           : This is service layer class which is used to process clinical functionalities which includes service request 
                     preparation,response parsing and security. Security class is Helper to this Class.
Created Date       : 15 Dec 2015
Test Class         : AuthSummary_Service_T_HUM

Modification Log:
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Sruthi Adivikolanu           29042                  12/15/2015          Original Version
 *   Sruthi Adivikolanu           29646                  01/23/2016          Applied Securityon search of Authrization.
 *   Sruthi Adivikolanu           29646                  01/30/2016          Moved the Constants to the static class as per the review
 *   Manish Kumar                 30647                  03/26/2015          Modified for continuos call to serice REQ-262442
 *   Santhi Mandava                                      03/30/2016          Fixed null pointer issue.
 *   Praveen Parimi/Prasanthi     33128                  07/18/2016          Defect fix for D-00471.
 *   Harika Devi Kalidindi        103812                 06/21/2017          REQ - 320375 -Changed class from Without sharing to with sharing 
 *   Prudhvi Pamarthi             288280                 09/26/2018          REQ - 374784 Fix for CA Incident #7728789
 *   Imran                        416071                 12/04/2019          Implemented the swith condition to handle both old And New service.
 * Pradeep Kumar Yadav                                   07/22/2020          User Story 1248496: CA ticket- 9228963 - CRM Service: Collection size 1, 050 exceeds to maximum size of 1000
 * Rahul Chaurasia                                       04/06/2021          User Story 1279935: PR00094226 - SF- Auth/Referral Summary old version Decommission
 ******************************************************************************************************************************/
public with sharing class AuthSummary_S_HUM
{
    @testVisible private  String sBodyReq ='' ;
    private AuthReferral_Query_D_HUM objdatalayer = new AuthReferral_Query_D_HUM();
    private Integer count = 0;
    private Integer pageNum = 1;
    ID curentUserID = UserInfo.getUserId();
    private Account objAcct = new Account();
    AuthReferral_Security_H_HUM objAuthSecurity = new AuthReferral_Security_H_HUM();
    AuthReferral_SecurityHO_H_HUM cHomeOfficeSecurity=new AuthReferral_SecurityHO_H_HUM();
    public Boolean bViewResults = true;
    public Boolean bShowMessage ;
    private List<AuthRefferal_Wrapper_DTO_HUM> lstAuthRefferal = new List<AuthRefferal_Wrapper_DTO_HUM>();
    public Integer totalRecordFound = 0;
    
    /*
     * Method Name : AuthSummary_S_HUM
     * Description : Constructor for the class , initialize variable
     * Return type : NA
     * Parameter   : NA
     */
    public AuthSummary_S_HUM()
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
    
    public HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderClinical,string sRecId)
    {
      
      try
      {        
		sBodyReq = buildRequestv2(sRecId);
        
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
          HUMExceptionHelper.logErrors(ex, 'AuthSummary_S_HUM', 'setupRequest');
          return Null;
      }
    }
    
                            /*
    * Method Name   :  buildRequestv2
    * Description   :  This method Is used to build the required inputs need to hit the service
    * Return Type   :  String
    * Parameters    :  String(Policy Member Id)
    */
    Public string buildRequestv2(String sRecId)
    {    
        If (count != 0 && pageNum < count)  
                                        {
            pageNum++;
        }
        If (bViewResults) 
                                            {
            objAcct = objdatalayer.getMemberAccount(sRecId); 
            
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
                oPageMetaData.PageNumber = pageNum;
               
                oreq.PageMetaData = oPageMetaData;
                omain.AuthSummaryRequest = oreq;
                String jsonbody = json.serialize(omain);
                If (count != 0 && pageNum == count) 
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
    public List<AuthRefferal_Wrapper_DTO_HUM> processResponse(string response) 
    {  
        try
        {
            ClinicalAuthSummaryResponse_H_HUM objresp = new ClinicalAuthSummaryResponse_H_HUM();
            List<AuthRefferal_Wrapper_DTO_HUM> listAuthResp = new List<AuthRefferal_Wrapper_DTO_HUM>();
            objresp = (ClinicalAuthSummaryResponse_H_HUM)Json.deserialize(response,ClinicalAuthSummaryResponse_H_HUM.class);
            if(objresp.AuthSummaryResponse != Null)
            {
                listAuthResp = parseResponse(objresp);
                return listAuthResp;
            } 
            else
            {
                return listAuthResp;
            }
        }
        catch(Exception ex)
        {
          HUMExceptionHelper.logErrors(ex, 'AuthSummary_S_HUM', 'processResponse');
          return Null;
        }
    }
    /*
    * Method Name   :  parseResponse
    * Description   :  This method is used to assign the response to variables to wrapper Class
    * Return Type   :  List<AuthReferral_Wrapper_H_HUM>
    * Parameters    :  ClinicalAuthSummaryResponse_H_HUM object
    */
    Private List<AuthRefferal_Wrapper_DTO_HUM> parseResponse(ClinicalAuthSummaryResponse_H_HUM response)
    {   
        List<AuthRefferal_Wrapper_DTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_DTO_HUM>();
        ClinicalAuthSummaryResponse_H_HUM.AuthSummaryResponse oAuthSummaryResponse = response.AuthSummaryResponse ;
        if( oAuthSummaryResponse.TotalRecordsFound != null)
        {
            totalRecordFound =  Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound);
        }
        if(oAuthSummaryResponse != null && oAuthSummaryResponse.AuthSummaryList != null)
        {
            ClinicalAuthSummaryResponse_H_HUM.AuthSummaryList oauthsumlist = oAuthSummaryResponse.AuthSummaryList;
            List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstauth = oauthsumlist.AuthSummary;
            count = Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound)/Integer.valueOf(GLOBAL_CONSTANT_HUM.HTTPREQUEST_PAGESIZE_HUM);
              
            if(Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound) > Integer.valueOf(GLOBAL_CONSTANT_HUM.HTTPREQUEST_PAGESIZE_HUM) && math.mod(Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound),Integer.valueOf(GLOBAL_CONSTANT_HUM.HTTPREQUEST_PAGESIZE_HUM)) != 0)
                count++;
            if(Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound) < Integer.valueOf(GLOBAL_CONSTANT_HUM.HTTPREQUEST_PAGESIZE_HUM) || Integer.valueOf(oAuthSummaryResponse.TotalRecordsFound) == Integer.valueOf(GLOBAL_CONSTANT_HUM.HTTPREQUEST_PAGESIZE_HUM))
                bViewResults = false;
            
            lstAuthsummary = buildWrapperList(lstauth);
            lstAuthRefferal = checkSecurity(lstAuthsummary);
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
    private List<AuthRefferal_Wrapper_DTO_HUM> buildWrapperList(List<ClinicalAuthSummaryResponse_H_HUM.AuthSummary> lstauth)
    {
        List<AuthRefferal_Wrapper_DTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_DTO_HUM>();
        for(ClinicalAuthSummaryResponse_H_HUM.AuthSummary obj:lstauth )
        {
            AuthRefferal_Wrapper_DTO_HUM objwrapper = new AuthRefferal_Wrapper_DTO_HUM();
            objwrapper.sAuthorizationOrReferralNumber = obj.AuthID;
            objwrapper.sAuthorizationType = obj.AuthType;
            objwrapper.sOverallStatus = obj.AuthStatus;
            
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
            lstAuthsummary.add(objwrapper);
        }
        return lstAuthsummary;
    }
    
    /*
     * Method Name   :  checkSecurity
     * Description   :  This method is used to pass the wraper list to security class to verify whether it is accessible for user or not.
     * Return Type   :  List<AuthReferral_Wrapper_H_HUM>
     * Parameters    :  List<AuthReferral_Wrapper_H_HUM>
     */
    private List<AuthRefferal_Wrapper_DTO_HUM> checkSecurity(List<AuthRefferal_Wrapper_DTO_HUM> lstAuthsummary){
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
               objAcct = objdatalayer.getMemberAccount(sRecId);  
            }
            lstAuthorization = oAuthorizations.Authorization;            
            List<AuthRefferal_Wrapper_DTO_HUM> lstAuthsummary = new List<AuthRefferal_Wrapper_DTO_HUM>();
            AuthRefferal_Wrapper_DTO_HUM objauth = new AuthRefferal_Wrapper_DTO_HUM();
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