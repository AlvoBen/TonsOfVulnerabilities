/*****************************************************************************************
Apex class Name    : AuthSummary_LC_HUM
Version            : 1.0
Function           : Controller class for Auth suammary referral page
Created Date       : 14/07/2022
Test Class         : Authsummary_LT_HUM 
*******************************************************************************************
Modification Log:
*   Developer                   Code Review             Date               Description
* ------------------------------------------------------------------------------------------------------------------------------   
* Rajesh Narode                                        14/07/2022         User story 3362694 Authorization Summary table
* Kalyani Pachpol                                      15/07/2022         US - 3442787
* Sagar Gulleve                                        19/07/2022         US - 3388026 T1PRJ0193102- MF 3223149 CRM Service Benefits 2022-  Auth/Referral: Lightning Build of Details - GO TO 
* Anuradha Gajbhe                                      28/07/2022         DF-5462 Fix
* Aishwarya Pawar                                      10/31/2022         REQ-  3819554
* Anuradha Gajbhe                                      05/12/2022         User story 3778026 - Auth/Referral: Lightning Build: Home Office Auths/referrals.
* Raj Paliwal										   03/03/2023		  User story 4003693 - Auth/Referral: Lightning Verification: Toast/Error/Info Messages.
* Sagar G                                              18/08/2023         User story 4762116: T1PRJ0891415 - MF4439757 - Test Class Code Coverage
*********************************************************************************************************************************/

public with sharing class AuthSummary_LC_HUM {
    public static AuthSummary_LS_HUM oServiceAuth = new AuthSummary_LS_HUM(); 
    public static String sRequest ='';
    public static List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthResponse = new List<AuthRefferal_Wrapper_LDTO_HUM>();
    Public Static final Integer HTTP_OK_HUM = 200;
    Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
    public static string sEnterpriseID;
    public static string sDateOfBirth;
    public static Boolean bIsError{get;set;}
    public static Boolean bShowError{get;set;}
    public static Boolean bShowMessage{get;set;}
    public static Boolean bShowResults{get;set;}
    public static Boolean bShowServiceError{get;set;}
    public static String sRecId {get;set;} 
    public static List<AuthRefferal_Wrapper_LDTO_HUM> lstAuth;
	public static List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthHO;
    public static String sUserinputAuthId {get;set;}
    public static Boolean bTotalRecordFlag {get;set;}
    public static String sKMDocRequest = '';
    public Static String sAuthNbr{get;set;}
    public Static Boolean bClaimExists{get;set;}
    public Static String sMsgOnGo{get;set;}
    public Static String sSeverityOnGo{get;set;}
    public Static Boolean bDuplicate{get;set;}
    @TestVisible private static String continuationLabel;
    @TestVisible private static String continuationState;

     /*
    * Method Name   :  initiateRequest
    * Description   :  This method is used to Call the Service while loading the page
    * Return Type   :  continuation (Object of type Continuation)
    * Parameters    :  NA (Pass parameter - sRecId from page, record ID of member plan object)
    */
    @AuraEnabled(Continuation=true)
    public static Object initiateRequest(String sRecordId, Integer sPageNum)
    {
        Try
        {
            Map<String, String> DataTemplate = new Map<String, String>();
            sRequest = '';
            sRecId = sRecordId;
            if(String.isNotBlank(sRecId)){
                DataTemplate.put('MemPlanRecId',sRecordId);
                HttpRequest request = new HttpRequest();
                Integer iTIMEOUT_INT_SECS;               
            
                HUM_Webservice_Callout__c  oSvcHeaderClinicalnew= HUM_Webservice_Callout__c.getValues('Clinical_AuthSummary_V2'); 
                iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderClinicalnew.Timeout__c)/1000;
                request = oServiceAuth.setupRequest(request,oSvcHeaderClinicalnew,sRecId,sPageNum);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                con.state = DataTemplate;
                sRequest = con.addHttpRequest(request);             
                con.continuationMethod='processResponse';
                
                return con;
                }
            else{
                return Null;
            }
        }
        catch(Exception ex)
        {
            bIsError = true;
            bShowServiceError = true;
            HUMExceptionHelper.logErrors(ex, 'Authsummary_c_Hum', 'initiaterequest');
            return System.Label.AuthSummary_Service_Error;
        }
    }
    
  
    /*
    * Method Name   :  processResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled(cacheable=true)
    public static Object processResponse(List<string> labels,Object state)
    {
        try
        {
            Object authData =  state; 
            Map<String, Object> authDataObj = (Map<String, Object>) JSON.deserializeUntyped(JSON.serialize(authData));
            string sRecId = string.valueOf(authDataObj.get('MemPlanRecId'));
            HttpResponse response = Continuation.getResponse(labels[0]);
            return  responseParse(response, sRecId);
            
        }
        catch(Exception ex)
        {
            bIsError = true;
            bShowServiceError = true;
            HUMExceptionHelper.logErrors(ex, 'Authsummary_c_Hum', 'processResponse');
            return System.Label.AuthSummary_Service_Error;
        }
    }

    /*
     * Method Name   :  responseParse
     * Description   :  This method is used to check response
     * Return Type   :  void 
     * Parameters    :  HttpResponse obj
     */
   // @AuraEnabled
    public static Object responseParse(HttpResponse sResponse, String sRecId)
    {
        Integer statusCode = sResponse.getStatusCode();
        if (statusCode == 200 || statusCode == 202){
            lstAuthResponse = oServiceAuth.processResponse(sResponse.getbody(), sRecId);
            if(bTotalRecordFlag== null && lstAuthResponse != null && lstAuthResponse.size() > 0 && oServiceAuth.totalRecordFound != null)    
            {  
               if(oServiceAuth.totalRecordFound <= Integer.ValueOf('1000'))
                {
                    bShowResults = oServiceAuth.bviewresults;
                     return finalAuthsummarylist(lstAuthResponse);
              }
                else
                {   
                    bIsError = true;
                    bShowResults = false;
                    bShowError = true;
                    return null;
                }
            } 
            else if(bTotalRecordFlag == false  && lstAuthResponse != null && !(lstAuthResponse).isEmpty())
            {
                bShowResults = oServiceAuth.bviewresults;
                 return finalAuthsummarylist(lstAuthResponse);
            }
            else
            {
                bIsError = true;
                bShowResults = false;
                bShowError = true;
                return null;
            }
        }
        else
        {
            bIsError = true;
            bShowResults = false; 
            bShowServiceError = true;
            
            return System.Label.AuthSummary_Service_Error;
        }
    }

    /*
    * Method Name   :  finalAuthsummarylist
    * Description   :  This method is used to form the wrapper list that needs to be display on UI. 
    * Return Type   :  void 
    * Parameters    :  List<AuthReferral_Wrapper_H_HUM>
    */
     @AuraEnabled
    public static Object finalAuthsummarylist(List<AuthRefferal_Wrapper_LDTO_HUM> lstAuthResponse)
    {
        bShowMessage = false;
		Boolean bHomeOficeMessage=false;
		string sWarningMsg = '';
		String lstAuthResp='';
		String lstAuthRespHO='';
        lstAuth = new List<AuthRefferal_Wrapper_LDTO_HUM>();
		lstAuthHO = new List<AuthRefferal_Wrapper_LDTO_HUM>();
        if(lstAuthResponse != null && !lstAuthResponse.isEmpty())
        {
           for(AuthRefferal_Wrapper_LDTO_HUM obj:lstAuthResponse)
           {
                if(obj.bIsAccessible){                   
                   lstAuth.add(obj);
                }
                else if(! obj.bIsAccessible){
                    bIsError = true;
                    bShowMessage = true;
					bHomeOficeMessage = true;
                    sWarningMsg = System.Label.AuthSummary_Security_Info;
					lstAuthHO.add(obj);
                }
           }
             
        }
        else
        {
            bIsError = true;
            bShowError = false;
            lstAuth= null;
			lstAuthHO = null;			
        }
        lstAuthResp = JSON.serialize(lstAuth);
		lstAuthRespHO = JSON.serialize(lstAuthHO);
        if(bHomeOficeMessage){
		   lstAuthResp = '{"Data":'+ lstAuthResp +',' + '"HOData":'+ lstAuthRespHO + ',' +'"HOMsg":' + sWarningMsg + '}';
        }
        return lstAuthResp;
    }
     
       /*
    * Method Name: getMemberAccount
    * Description: This method will query Member account fields required for service as input
    * Parameter: sRecId the String which holds Record id of the current Policy Member
    * Return Type: Member Account
    */
    @AuraEnabled
    public Static Account getMemberAccount(String sRecId)
    {
        if(string.isNotBlank(sRecId))
        {
           return AuthReferral_Query_LD_HUM.getMemberAccount(sRecId);
        }
        else
        {
            return Null;
        }
    }

                @AuraEnabled (cacheable=true)
                public static string getFlowTemplate(){
        
                                try {
                                    String TN ='';
                                                List<Template__c> templateNumber = [SELECT NAME FROM Template__c WHERE Description__c = 'Authorization/Referral Requirements' Limit 1];
            if(templateNumber != null && templateNumber.size() > 0){
                                                                TN = templateNumber[0].name;
                                                                return TN;
                                                }
        return TN;
                                } catch (Exception e) {
                                HUMExceptionHelper.logErrors(e, 'AuthSummary_LC_HUM', 'getFlowTemplate');
                                                throw new AuraHandledException(e.getMessage());
                                }
                }
                
                @AuraEnabled (cacheable=true)
                public static MemberPlan getPlanDetails(string memberPlanId){
                try
                                {
            if(string.isNotBlank(memberPlanId)){
                return [Select Id,Name,Member.Mbr_Gen_Key__c,Product_Type__c From memberPlan where Id= :memberPlanId];
            }else{
                return null;
            }
                                }
                                catch (Exception ex)
                                {
                                                HUMExceptionHelper.logErrors(ex, 'AuthSummary_LC_HUM', 'getPlanDetails');
            return null;
                                }
    }
    /*
     * Method Name   :  searchAuth
     * Description   :  This method is used for searching Auths
     * Return Type   :  NA
     * Parameters    :  NA
     */
    @AuraEnabled(continuation=true)
    public Static object searchAuth(string sAuthNumber, string sRecId) 
    {
        try
        { 
            sAuthNbr = sAuthNumber;
            String sGoRequest = '';
            bDuplicate = false;
            bClaimExists = false;
            bShowServiceError = false;
            bShowError = false;
            if(String.isNotBlank(sAuthNumber))
            {
                HUM_Webservice_Callout__c oSvcHeaderGoClaims = HUM_Webservice_Callout__c.getValues('Clinical_AuthSummary_V2'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcHeaderGoClaims.Timeout__c)/1000;
                HttpRequest requestGo = new HttpRequest();
                requestGo = oServiceAuth.setupGoButtonRequest(requestGo, oSvcHeaderGoClaims, sAuthNumber);
                Continuation conGo = new Continuation(iTIMEOUT_INT_SECS );
                                conGo.state = sRecId;
                sGoRequest = conGo.addHttpRequest(requestGo);
                conGo.continuationMethod = 'processGoButtonResponse';
                if(Test.isRunningTest()){
                 continuationLabel = conGo.addHttpRequest(requestGo);
                continuationState = sRecId;
                }
                return conGo;
            }
            else return Null;
        }
        catch(Exception ex)
        {
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo = 'Error';
            HUMExceptionHelper.logErrors(ex, 'AuthSummary_LC_HUM', 'searchAuth');
            return NULL;
        }   
    }
   
    /*
    * Method Name   :  processGoButtonResponse
    * Description   :  This method is used to get response from service.
    * Return Type   :  void 
    * Parameters    :  NA
    */
    @AuraEnabled
    public Static AuthRefferal_Wrapper_LDTO_HUM processGoButtonResponse(List<String> labels ,Object State)
    {
        try 
        {
            sRecId = string.valueof(state);
            HttpResponse responseGo = Continuation.getResponse(labels[0]);
            AuthRefferal_Wrapper_LDTO_HUM claimgotoResponse = responseparseGoButton(responseGO, sAuthNbr, sRecId);
            return claimgotoResponse;
        }
        catch(Exception ex)
        {
            bClaimExists = true; 
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo = 'Error';
            HUMExceptionHelper.logErrors(ex, 'AuthSummary_LC_HUM', 'processGoButtonResponse');
        }
        return null;
    }
    
    /*
     * Method Name   :  responseparseGoButton
     * Description   :  This method is used to check response
     * Return Type   :  AuthRefferal_Wrapper_LDTO_HUM 
     * Parameters    :  HttpResponse obj
     */
    @testvisible
    private Static AuthRefferal_Wrapper_LDTO_HUM responseparseGoButton(HttpResponse sResponseGo, String sAuthNbr, string sRecId )
    {
        Integer statusCode = sResponseGo.getStatusCode();
        if (statusCode == HTTP_OK_HUM || statusCode == HTTP_ACCEPTED_HUM)
        {
            List<AuthRefferal_Wrapper_LDTO_HUM> templstClaimResponse = oServiceAuth.processResponse(sResponseGo.getbody(), sRecId);
            List<AuthRefferal_Wrapper_LDTO_HUM> templstClaimResponseMember = new List<AuthRefferal_Wrapper_LDTO_HUM>();
            templstClaimResponseMember = templstClaimResponse ;
            return templstClaimResponseMember[0];
        }
        else
        {
            bClaimExists = true;
            sMsgOnGo = Label.ClaimsSummary_Service_Error;
            sSeverityOnGo  = 'Error';
        }
        return null;
    }
	
	/*    
     * Method Name   :  authDetailscheckRequest
     * Description   :  This method is used to Check the users access for auth id
     * Return Type   :  String
     * Parameters    :  String GroupId, string RecId 
     */
    @AuraEnabled(continuation=true)
    public static String authDetailscheckRequest(String GroupId, string RecId ) 
    {
        AuthReferral_Query_LD_HUM objdatalayer = new AuthReferral_Query_LD_HUM();
        AuthReferral_SecurityHO_LH_HUM cHomeOfficeSecurity= new AuthReferral_SecurityHO_LH_HUM();
        Account objAcct = new Account();
        Boolean bAuthIdExistsdet;
        ID sRecId =RecId ;
        String sGroupId = GroupId; 
        if(sGroupId != null && string.isNotEmpty(sGroupId))
        {
            if(sRecId != null && string.isNotBlank(sRecId))
            {
               objAcct = getMemberAccount(sRecId);  
            }
        }
        bAuthIdExistsdet = cHomeOfficeSecurity.filteredAuthsCheck(sGroupId,UserInfo.getUserId(),objAcct.Id);
        
        if (bAuthIdExistsdet)
        {
            return System.Label.AuthSummary_Security_Info;
        }
        return null;

    }

    /*End of class */
}