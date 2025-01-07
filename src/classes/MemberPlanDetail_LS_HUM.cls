/*******************************************************************************************************************************
Apex Class Name : MemberPlanDetail_LS_HUM
Version         : 1.0
Created On      : 05/01/2021
Function        : This is the service layer class for Medicaid Plan Details section on the Policy Detail page
Test Class      : MemberPlanDetail_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0                    Joel George                                            05/26/2021                Original Version
*     2.0                    Joel George                                            06/10/2021                  Rollback PBE Service
*     3.0                    Kajal Namdev                                           06/15/2021                  Added Test.isTestRunning condition
*     4.0                    Joel George                                            07/01/2021                Switch related changes
*	  5.0                    Joel George                                            07/29/2021                Switch update
*     6.0                    Kajal Namdev                                           06/21/2021                  Added Other Insurance Service
*     7.0                    Ashish Kumar                                           08/06/2021                  Added null check
*     8.0                    Joel George                                            08/10/2021                  Added CheckMarx Check
*     9.0                    Kajal Namdev                                           08/10/2021                  Replaced OR with && contion in checkDualEligibleListIsNull function
*     10.0                   Ankima Srivastava                                      09/01/2021                  Added method for Cost Share Protected Field
*     11.0                   Joel George                                            09/03/2021                  Added Date formating code
*     12.0                   Ankima Srivastava                                      09/06/2021                  Removal of cost share related method
*     13.0                   Ankima Srivastava                                      09/08/2021                  Removal of cost share related extra method
*     14.0                   Joel George		                                    10/07/2021                  Error Log Vendor Update
*     15.0                   Ankima Srivastava                                      10/25/2021                  US : 258176
*     16.0                   Vardhman Jain                                          16/06/2023                  US : 4525760
******************************************************************************************************************************************************************************/

public with sharing class MemberPlanDetail_LS_HUM {
    
     private static AdditionalResponseWrapper_DTO_HUM addVendorWrapper = new AdditionalResponseWrapper_DTO_HUM();
     private static String sNoneValue = 'NONE';  
     private static String sNullValue = 'Uninitialized';  
     private static String sLVPlatForm =  GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM;
     private static String sEMPlatForm =  GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_EM_HUM; 
     public static  Boolean switchOnOffSubGroupId = false;
     public static  Boolean switchOnOffPEOAssociate = false;
     Private static   String sDummyBenValue = 'XXXXXXXX';
     @TestVisible private static String sBodyVendor ='' ;
     Private static PurchaserPlan policyPlan = New PurchaserPlan();
    public static list<PurchaserPlan> policyPlanList = new List<PurchaserPlan>();
    @TestVisible private static MemberPlanDetail_LC_HUM oServiceVendorPolicyMem = New MemberPlanDetail_LC_HUM ();
     public static MemberPlan policyMember = New MemberPlan();
    public static String sId;
    public static String comma = ',';
    public static Boolean isCRMSGBOPermissionSetUser  = UtilityHelper_H_AHT_HUM.isUserhavingPermissionSetAccess(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CRMS_240_GBO_Access_HUM);
    public static GBEServiceResponse_H_HUM respond = New GBEServiceResponse_H_HUM();
    public static Boolean switchOnOffPEO = HUMUtilityHelper.isCRMFunctionalityON('1760349');
    public class ServiceDownException extends Exception {}
    @testVisible private static GBEServiceResponse_H_HUM.AssociationList associationDTO = NULL;
    @testVisible private static GBEServiceResponse_H_HUM.OrganizationTypeList organizationTypeDTO = NULL;
    @testVisible private static GBEServiceResponse_H_HUM.VendorProgramOfferListGroup vendorProgramGroupDTO = NULL;
    @testVisible private static GBEServiceResponse_H_HUM.VendorProgramOfferListSubGroup vendorProgramSubGroupDTO = NULL;

    public static string sRequest;
    public static Map<String,String> stateMap= new Map<String,String>();
    //====Other Insurance====
    @TestVisible private static String continuationLabel;
    public static map<String, MemberPlan> mapPolicyMembers = new map<string, MemberPlan>();
    public static map<String, list<PurchaserPlan>> mapPolicyPlanDetails = new map<string, list<PurchaserPlan>>();
    Private Static String sNumOne = '1';  
    Private Static String sNumZero = '0';
   
    
    //================================= EDI Service Start ======================================================   

    /*
     * REQ-383577 - Integration for EDI Group 
     * Method used for, to parse the EDIResponse and set the value in Map<string,List<string>> format to parse easily in remoting Callback
     * @method - parseEDIResponse 
     * @param - HUMPolicyMember_EDIResponse_DTOt
     * @return - Map<string,List<string>>
    */
    public static Map<string,List<string>> parseEDIResponse(HUMPolicyMember_EDIResponse_DTO ediResponse , string policyProduct)
    {
         
        if(ediResponse!=null){
            
            List<String> lstValuetoAdd = new List<String>();
             Map<String, List<String>> EDIlabelToValue = new MAP<String, List<String>>();
            
            for(HUMPolicyMember_EDIResponse_DTO.EdiGroupDetails e : ediResponse.GetEDIGroupInfoResponse.EdiGroupDetails )
            {

                 if(e.EDIGroup !=null){
                    lstValuetoAdd = new List<String>(); 
                    lstValuetoAdd.add(e.EDIGroup);
                    EDIlabelToValue.put('EDI',lstValuetoAdd);
                }

                if(e.Frequency !=null){
                    lstValuetoAdd = new List<String>(); 
                    lstValuetoAdd.add(e.Frequency);
                    EDIlabelToValue.put('Group Update Frequency',lstValuetoAdd);
                }

                lstValuetoAdd = new List<String>(); 
                if(String.isNotBlank(e.Day1))lstValuetoAdd.add(e.Day1);
                if(String.isNotBlank(e.Day2))lstValuetoAdd.add(e.Day2);
                if(String.isNotBlank(e.Day3))lstValuetoAdd.add(e.Day3);
                if(String.isNotBlank(e.Day4))lstValuetoAdd.add(e.Day4);
                EDIlabelToValue.put('Frequency Details',lstValuetoAdd); 

                lstValuetoAdd = new List<String>(); 
                lstValuetoAdd.add('Phone Number');
                lstValuetoAdd.add('Email');
               //if(policyProduct == 'MED')
                    //lstValuetoAdd.add('PCP Change');
                //if(policyProduct == 'DEN')
                   // lstValuetoAdd.add('PCD Change');

                EDIlabelToValue.put('Humana Can Change',lstValuetoAdd); 

                         
            }
             return EDIlabelToValue; 
        }
 
        return null;            
    }
    
    /*
     * REQ-383577 - Integration for EDI Group 
     * Method is used for to create Request for EDI 
     * @method - RequestStructureforEDI 
     * @param - groupId
     * @return - HUMPolicyMember_EDIRequest_DTO
    */
    public static HUMPolicyMember_EDIRequest_DTO RequestStructureforEDI(string groupId){
        try{
            HUMPolicyMember_EDIRequest_DTO ediRequestDTO = new HUMPolicyMember_EDIRequest_DTO();
            HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest getEDIgroup = new HUMPolicyMember_EDIRequest_DTO.GetEDIGroupInfoRequest();
            HUMPolicyMember_EDIRequest_DTO.GroupDetail grpDetail = new HUMPolicyMember_EDIRequest_DTO.GroupDetail()  ;
         
            getEDIgroup.GroupDetail = new List<HUMPolicyMember_EDIRequest_DTO.GroupDetail>();
          
            grpDetail.Group_id = groupId;
            getEDIgroup.GroupDetail.add(grpDetail);
            ediRequestDTO.GetEDIGroupInfoRequest = getEDIgroup;
            return ediRequestDTO;           
        }
        catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MemberPlanDetail_LS_HUM', 'RequestStructureforEDI');
            return Null;
        }
    }
    /*
     * Method Name   :    getEDIData
     * Description   :    This method makes call to EDI Service 
     * Return Type   :    Continuation
     * Parameters    :    sRecId
     */ 
@AuraEnabled(continuation=true)  
     public static Object getEDIData(String sRecId) 
        {  
         try{
           
				List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
				lstMemberPlan = MemberPlanDetail_LD_HUM.getMemberName(sRecId); 
				String groupNumber = MemberPlanDetail_LD_HUM.getEDIGroupNumber(lstMemberPlan);
        
                    HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues('GetEDIData');
                    Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
                    Continuation cont = new Continuation(iTIMEOUT_INT_SECS );
                    cont.continuationMethod='processResponseEDI'; //processResponseVendor
                    HttpRequest request = new HttpRequest();
            
                    if(ServiceObj !=null && ServiceObj.Active__c ){
                        request.setMethod(ServiceObj.Request_Type__c);
                        If(!Test.isRunningTest()){
                        	request.setClientCertificateName(ServiceObj.Certificate_Name__c);    
                        }
                        request.setEndpoint(ServiceObj.End_Point_URL__c);
                        request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
                        request.setBody(JSON.serialize(RequestStructureforEDI(groupNumber)));
                    }

                    sRequest = cont.addHttpRequest(request);
                    stateMap.put('sRequest',sRequest);
                    stateMap.put('groupId',groupNumber);
                    if(!lstMemberPlan.isEmpty() && lstMemberPlan[0].Product__c !=null)
                    stateMap.put('product',lstMemberPlan[0].Product__c);
                    cont.state=stateMap;
                    return cont;
                 
             }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','GetGroupInfoFromGBE');     
            } 
            AdditionalResponseWrapper_DTO_HUM addRespWrapper = new AdditionalResponseWrapper_DTO_HUM(); 
			addRespWrapper.isOnSwitch = true;           
			return addRespWrapper;
        }
    
    /*
     * Method Name   :    processResponseVendor
     * Description   :    This method process Http Response from GBE Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    @AuraEnabled
    public static object processResponseEDI(Object state)
    {   
        try 
        {
                Map<String,String> mapState=(map<String, String>)state; 
                HttpResponse sResponse = Continuation.getResponse(mapState.get('sRequest'));   
                AdditionalResponseWrapper_DTO_HUM addWrapper = new AdditionalResponseWrapper_DTO_HUM();    

                HUMPolicyMember_EDIResponse_DTO getEDIResponse = new HUMPolicyMember_EDIResponse_DTO();           
            
                if (sResponse != null && !sResponse.getBody().containsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_FAULT_CODE) && ((sResponse.getStatusCode() == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) || Test.isRunningTest()))
                {
                    getEDIResponse = (HUMPolicyMember_EDIResponse_DTO)System.JSON.deserialize(sResponse.getBody(),HUMPolicyMember_EDIResponse_DTO.class);
                    if(getEDIResponse!=null)
                        addWrapper.ediMap = parseEDIResponse(getEDIResponse,mapState.get('product'));
                    else
                        return null;
                }
                addWrapper.isOnSwitch = true;   
                return addWrapper;
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','processResponseVendor');    
        }
        return null;  
    } 
    
    //================================= EDI Service Stop ======================================================   

    //================================= Vendor Service Start ======================================================    
  /*
   * Method Name   :  returnStringDate
   * Description   :  This method is used to convert date to string
   * Return Type   :  String 
   * Parameters    : Date
   */
    public static string returnStringDate(Date dtDate)
    {
        Datetime dtDateTime = datetime.newInstance(dtDate.year(), dtDate.month(),dtDate.day());
        string strDate = dtDateTime.format('MM/dd/yyyy');
        return strDate;
        
    }
     /*
   * Method Name   :  setupRequest
   * Description   :  This method is used to form http request.
   * Return Type   :  HttpRequest 
   * Parameters    :  HttpRequest,HUM_Webservice_Callout__c,string (Policy Plan Id)
   */
    public static HttpRequest setupRequest(HttpRequest request,HUM_Webservice_Callout__c oSvcHeaderPolicy,PurchaserPlan policyPlan,MemberPlan policyMember)
    {
       HttpRequest orequest = new HttpRequest();    
        try
        {
          sBodyVendor = buildRequest(policyPlan,policyMember);
            if(String.isNotBlank(sBodyVendor))
            {
                request.setMethod(oSvcHeaderPolicy.Request_Type__c); 
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CONTENT_TYPE_HUM, oSvcHeaderPolicy.Content_Type__c); 
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.SOAPACTION_HUM, '');    
                request.setHeader(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Connection_HUM,GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.Keep_alive_HUM);
                request.setBody(sBodyVendor);
                if(!Test.isRunningTest())    
                {
                  request.setClientCertificateName(oSvcHeaderPolicy.Certificate_Name__c);
                }
                request.setTimeout(Integer.valueof(oSvcHeaderPolicy.Timeout__c));
                request.setEndpoint(oSvcHeaderPolicy.End_Point_URL__c);  
                orequest = request;
            }
            else
            {
               orequest = null;  
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM', 'setupRequest');
            orequest = null;
        }
        return orequest; 
    }
    
     /*
    *  This method is used to Process the Json response from service to Wrapper List
    *  @name   buildRequest
    *  @param  
    *  @return string - Returns the serialized body of the request
    *  @throws NA
    */ 
    @testvisible private static string buildRequest(PurchaserPlan policyPlan,MemberPlan policyMember)
    {    
        string sJsonbody = '';
        switchOnOffSubGroupId = HUMUtilityHelper.isCRMFunctionalityON('1789465');
        switchOnOffPEOAssociate = HUMUtilityHelper.isCRMFunctionalityON('1760349');
        try
        {
          GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM(); 
          GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
          //Replaced existing condition to fetch SubGroupID from 8 digits to 9 digits. For SubGroupId having more than 8 chars was not getting fetched properly. Hence replaced subString() to fetch 9 digits and for 8 chars will trim the space
          if(switchOnOffSubGroupId)
          {
           if(!String.isBlank(policyPlan.Source_Cust_Cov_Key__c))
           {
            grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6):policyMember.GroupNumber);          
            grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,9).trim() : policyPlan.Platform__c ==   sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(9,12):'');
           }
          }
          else
          {
           grpDetailDTO.GroupId = (policyPlan.Platform__c == sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,6):policyMember.GroupNumber);
           grpDetailDTO.SubGroupId = (policyPlan.Platform__c == sEMPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(0,8) : policyPlan.Platform__c ==   sLVPlatForm ? policyPlan.Source_Cust_Cov_Key__c.subString(9,12):'');  
          }
          grpDetailDTO.BenefitPackageId = policyPlan.Platform__c == sEMPlatForm ? sDummyBenValue : ''; 
          grpDetailDTO.Platform = policyPlan.Platform__c;
          grpDetailDTO.StartDate =  (policyMember.effectiveFrom <> null ? returnStringDate(policyMember.effectiveFrom) : sNoneValue);                   
          grpDetailDTO.EndDate = policyMember.EffectiveTo <> null ? returnStringDate(policyMember.EffectiveTo) : sNoneValue;
          grpDetailDTO.BenefitSequenceNumber='';
          grpDetailDTO.DivisionStartPosition ='';
          grpDetailDTO.DivisionRecordsToReturn ='';
          grpDetailDTO.InclusionCriteria = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.GROUPSTRING;
          grpDetailDTO.VendorOfferProgramGroup = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.STRING_TRUE;
          grpDetailDTO.VendorOfferProgramSubGroup = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.STRING_TRUE;
          /****Added new Request param for EM Platform only for PEO Indicator Field ****/
          if(switchOnOffPEOAssociate){
          grpDetailDTO.Association = policyPlan.Platform__c == sEMPlatForm ? GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.STRING_TRUE : '';
          }else{
          grpDetailDTO.Association = null;
          }
          oRequest.GroupDetailDTO = grpDetailDTO;
          sJsonbody = JSON.serialize(oRequest);         
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM', 'buildRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
    /*
     * Method Name   :    GetGroupInfoFromGBE
     * Description   :    This method makes call to GBE when the Member policy page is loaded 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */  
     public static Object GetGroupInfoFromGBE(String sRecId) 
        {  
         try{
                String productType;
                List<MemberPlan> memberPlan = MemberPlanDetail_LD_HUM.getMemberName(sRecId);
                if(memberPlan.size()>0){
                    for(MemberPlan memPlan : memberPlan){
                        productType = memPlan.Product__c;    
                    }
                }
				SetPolicyPlanValues(sRecId);
				if(policyPlan.id != null)
                {  
					HUM_Webservice_Callout__c ServiceObj = HUM_Webservice_Callout__c.getValues('GroupBusinessEntityService');
					Integer iTIMEOUT_INT_SECS = Integer.valueof(ServiceObj.Timeout__c)/1000;
					Continuation cont = new Continuation(iTIMEOUT_INT_SECS );
					cont.continuationMethod=GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PROCESSRESPONSE_HUM; //processResponseVendor
					HttpRequest request = new HttpRequest();
					request= setupRequest(request, ServiceObj,policyPlan,policyMember); 
					sRequest = cont.addHttpRequest(request);
					stateMap.put('sRequest',sRequest);
					stateMap.put('sPurchaserPlan',policyPlan.Id);
                    stateMap.put('sProductType',productType);
					if(policyMember !=null && policyMember.Id != null )
						stateMap.put('sMemberPlan',policyMember.Id);
					cont.state=stateMap;
					return cont;
                } 
                else
                {
                    AdditionalResponseWrapper_DTO_HUM addRespWrapper = new AdditionalResponseWrapper_DTO_HUM(); 
                    addRespWrapper.isOnSwitch = true; 
                    if(productType =='MED'){
                        addRespWrapper.showVendorSection=true;
                    }else{
                        addRespWrapper.showVendorSection=false;    
                    }          
                    return addRespWrapper;
                }
             }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','GetGroupInfoFromGBE');     
            } 
            return NULL;
        }
    /**
     * Returns NULL
     * <p>
     * get and set the record values of PolicyMember Object.
     * @return      NULL
     * @see         Record Values
     */     
     public static void SetPolicyPlanValues(String sRecId)
    {
        try{
            List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
            if(sRecId != NULL)
            {
            
            lstMemberPlan = MemberPlanDetail_LD_HUM.getMemberName(sRecId);
            policyMember = lstMemberPlan[0];
             String vendorProdType = Label.RSO_Product_Type;
             List<String> lstProdType = vendorProdType.split(comma);

             if(policyMember != NULL)
              {
                if(policyMember.Policy_Platform__c !=null && policyMember.Policy_Platform__c.equals(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_EM_HUM) && (isCRMSGBOPermissionSetUser))
                  {
                   GetPolicyPlan(policyMember);
                  }
                  else if(policyMember.Policy_Platform__c !=null && policyMember.Policy_Platform__c.equals(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM) && (isCRMSGBOPermissionSetUser))
                  {
                   if(!lstProdType.contains(policyMember.Product_Type__c))
                   {                     
                    GetPolicyPlan(policyMember);
                   }
                  }  
               }
            }
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','SetPolicyPlanValues');     
        } 
    }
    
    
    /*
     * Method Name   :    GetPolicyPlan
     * Description   :    This method get the PolicyPlanDetails  
     * Return Type   :    void
     * Parameters    :    policyMember
     */  
    
     public static void GetPolicyPlan(MemberPlan policyMember) 
        {  
         try{
             policyPlanList = MemberPlanDetail_LD_HUM.getPolicyPlanVendor(policyMember);
             if(policyPlanList != NULL && !policyPlanList.isEmpty())
             for(PurchaserPlan policyPlanDetail : policyPlanList)
                {
                 if(policyPlanDetail.payer.Group_Number__c <> null)
                  {
                   policyPlan = policyPlanDetail;
                   }
                }
             }
            catch(Exception ex)
            {    
                HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','GetPolicyPlan');     
            } 
        }
    /*
     * Method Name   :    processResponseVendor
     * Description   :    This method process Http Response from GBE Service 
     * Return Type   :    Continuation
     * Parameters    :    NA
     */
    public static object processResponseVendor(Object state)
    {  
        addVendorWrapper = new AdditionalResponseWrapper_DTO_HUM(); 
        try 
        {
			Map<String,String> mapState=(map<String, String>)state; 
			HttpResponse oResponse = Continuation.getResponse(mapState.get('sRequest'));   
			boolean isResponseValid = checkResponseValidity(oResponse);
            addVendorWrapper.isOnSwitch = true;
            String productType = mapState.get('sProductType');
            if(productType == 'MED'){
                addVendorWrapper.showVendorSection = true;
            }else{
                addVendorWrapper.showVendorSection = false;    
            }
           if(isResponseValid)
			{
                respond = (GBEServiceResponse_H_HUM)System.JSON.deserialize(oResponse.getBody(),GBEServiceResponse_H_HUM.class);
                if(respond<>null && respond.GroupInfo<>null )
                    {
                     if(respond.GroupInfo.Platform != NULL )
                     {
                      if(respond.GroupInfo.VendorProgramOfferListSubGroup<>null )
                        {
                          vendorInfoValue(respond);
                        }
                      if(switchOnOffPEO)
                        {
                          peoInfoValue(respond);
                        }  
                      }
					 if(respond.GroupInfo.BenfitSequenceNumber<>null)
					 {
                        addVendorWrapper.gbeMap.put('BSN', respond.GroupInfo.BenfitSequenceNumber);
                     }
                    }
                    return addVendorWrapper;
			}
			else
			{
			  String errorValues ='Purchaser plan id-'+mapState.get('sPurchaserPlan') +'Member plan id-'+mapState.get('sMemberPlan');
              HUMExceptionHelper.logErrors(New ServiceDownException('SERVICEERROR'+errorValues+oResponse.getBody()), 'MemberPlanDetail_LS_HUM', 'processResponseVendor');
			}
        }
        catch(Exception ex)
        {    
            HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','processResponseVendor');    
        }
        return addVendorWrapper;  
    }

    /*
     * Method Name   :  peoInfoValue()
     * Description   :  This method is used to populate peo Indicator Info 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public static void peoInfoValue(GBEServiceResponse_H_HUM resObj){
           if(resObj != NULL){
               if(resObj.GroupInfo.Platform.equals(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_EM_HUM) && resObj.GroupInfo.AssociationList != NULL)
               {
                setPeoInfoforEM(resObj);
               }else if(resObj.GroupInfo.Platform.equals(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM) && resObj.GroupInfo.OrganizationTypeList != NULL)
               {
                setPeoInfoforLV(resObj);
               }
            }
       }
    
    /*
     * Method Name   :  setPeoforEM()
     * Description   :  This method is used to populate peo Indicator Info for EM Platform 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public static void setPeoInfoforEM(GBEServiceResponse_H_HUM resObj){
          if(resObj.GroupInfo.AssociationList != NULL) associationDTO = resObj.GroupInfo.AssociationList;
                if(associationDTO != NULL && associationDTO.Association != NULL)
                 {          
                    for(GBEServiceResponse_H_HUM.Association association: associationDTO.Association)
                    {
                     /*******To verify AssociationType as PEO from AssociationList****/
                     if((!String.ISBLANK(association.AssociationType)) && (association.AssociationType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PEO)))
                       {
                         List<String> associationEndDateSplit = new List<String>();
                         List<String> associationEfffectiveDateSplit = new List<String>();
                         if(!String.isBlank(association.AssociationEndDate) && !String.isBlank(association.AssociationEffectiveDate))  
                         {              
                           associationEndDateSplit = association.AssociationEndDate.split('/');
                           associationEfffectiveDateSplit = association.AssociationEffectiveDate.split('/');
                         }
                         if(!associationEndDateSplit.isEmpty() && !associationEfffectiveDateSplit.isEmpty())
                         {
                           Date assoEndDate = Date.newInstance(Integer.valueOf(associationEndDateSplit[2]),Integer.valueOf(associationEndDateSplit[0]),Integer.valueOf(associationEndDateSplit[1]));
                           Date assoEffDate = Date.newInstance(Integer.valueOf(associationEfffectiveDateSplit[2]),Integer.valueOf(associationEfffectiveDateSplit[0]),Integer.valueOf(associationEfffectiveDateSplit[1]));
                           //Set PEO Indicator Field value to Yes/NO based on AssociationEndDate is Future Date or Past Date
                           if(assoEffDate <= system.today() && assoEndDate >= system.today())
                           { 
                                addVendorWrapper.gbeMap.put('peoIndicator', GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PEO_YES);
                                break;   
                           }else{
                                    addVendorWrapper.gbeMap.put('peoIndicator', GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PEO_NO);
                           }
                         } 
                       }else{
                                addVendorWrapper.gbeMap.put('peoIndicator', GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PEO_NO);
                       }
                    }               
                 }
       }
    
    /*
     * Method Name   :  setPeoInfoforLV()
     * Description   :  This method is used to populate peo Indicator Info for LV Platform 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
       public static void setPeoInfoforLV(GBEServiceResponse_H_HUM resObj){
          if(resObj.GroupInfo.OrganizationTypeList != NULL) organizationTypeDTO = resObj.GroupInfo.OrganizationTypeList;
             if(organizationTypeDTO != NULL && organizationTypeDTO.OrganizationType != NULL)
               {          
                 for(GBEServiceResponse_H_HUM.OrganizationType organization: organizationTypeDTO.OrganizationType)
                   {
                     if((!String.ISBLANK(organization.Code)) && (organization.Code.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PE)))
                     {
                        addVendorWrapper.gbeMap.put('peoIndicator', GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PEO_YES);
                      break;
                     }else{
                        addVendorWrapper.gbeMap.put('peoIndicator', GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.PEO_NO);
                     }
                   }                
               } 
        }

    /*
     * Method Name   :  vendorInfoValue()
     * Description   :  This is the used to populate Vendor Info 
     * Return Type   :  void
     * Parameters    :  HttpResponse Object
    */
        public static void vendorInfoValue(GBEServiceResponse_H_HUM resObj){
           if(resObj != NULL){
               if(resObj.GroupInfo.VendorProgramOfferListSubGroup != NULL) vendorProgramSubGroupDTO = resObj.GroupInfo.VendorProgramOfferListSubGroup;
                if(vendorProgramSubGroupDTO != NULL && vendorProgramSubGroupDTO.VendorProgramOffer != NULL)
                {          
                    for(GBEServiceResponse_H_HUM.VendorProgramOffer programoffer: vendorProgramSubGroupDTO.VendorProgramOffer)
                    {
                      if((!String.ISBLANK(programoffer.VendorIdentifier)) && (programoffer.VendorIdentifier.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.ACCL)))
                       {
                          if(!String.ISBLANK(programoffer.VendorIdentifierDescription))  addVendorWrapper.gbeMap.put('vendorName', programoffer.VendorIdentifierDescription);
                          if(!String.ISBLANK(programoffer.VendorBenefitCodeDescription))  addVendorWrapper.gbeMap.put('vendorProgram', programoffer.VendorBenefitCodeDescription);
                          if(!String.ISBLANK(programoffer.ProgramOfferEffectiveDate))  addVendorWrapper.gbeMap.put('programEffective', programoffer.ProgramOfferEffectiveDate);
                          if(!String.ISBLANK(programoffer.ProgramOfferEndDate)) addVendorWrapper.gbeMap.put('programEnd', programoffer.ProgramOfferEndDate);
                          break;
                       }
                    }               
                } 
            }
    }
    /*
     * Method Name   :    checkResponseValidity
     * Description   :    This method to check HttpResponseStatus 
     * Return Type   :    Boolean
     * Parameters    :    HttpResponse response
     */
    public static boolean checkResponseValidity(HttpResponse response) {
        boolean returnValue = false;
        try {
            if(response != null) {
                integer statusCode = response.getStatusCode();
                Boolean isStatusOk = (statusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_OK_HUM) ? true : (statusCode == GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HTTP_ACCEPTED_HUM) ? true : false;
                Boolean isNoFault = (response.getBody().containsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_FAULT_CODE)) ? false : true;
                returnValue = (isStatusOk && isNoFault) ? true : false;
            }
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex,'MemberPlanDetail_LS_HUM','checkResponseValidity');
            returnValue = false;
        }
        return returnValue;
    }
    //================================= Vendor Service Stop ======================================================     
    //================================= Other Insurance Service Start ====================================================== 
    /**
    * Returns an object and method calls the MBEPlus web service
    * @pram  id of the policy member in string format
    * @param  type in string format 
    * @return  object
    */
    public static Object getOtherInsuranceData(string sPolicyMemberId)
    {  
        AdditionalResponseWrapper_DTO_HUM  oPolMemberDetails = new AdditionalResponseWrapper_DTO_HUM ();
        try
        {
            List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
            lstMemberPlan =  MemberPlanDetail_LD_HUM.getMemberName(sPolicyMemberId);
            if(!lstMemberPlan.IsEmpty())
            {
                String sEnterpriseId = (lstMemberPlan[0].Member.Enterprise_Id__c != Null) ? lstMemberPlan[0].Member.Enterprise_Id__c : ''; 
                Continuation con = new Continuation(GlobalCommonConstants_HUM.SERVICETIMEOUT);
                MBEPlusOIRequest_DTO_HUM oReq = new MBEPlusOIRequest_DTO_HUM();
                oReq = createMBEPlusRequest(sEnterpriseID,HUMUtilityHelper.isCRMFunctionalityON('1707499_Switch')); // construct MBE+ Request //JOELCHECK_Shift
                String sBodyJSON;
                if(oReq!=null){
                    sBodyJSON = JSON.serialize(oReq);
                } 
                if(String.isNotBlank(sBodyJSON))
                { 
                    HttpRequest mbeRequest = HUMProcessUtilityHelper.builtHttpRequest();                        
                    if(mbeRequest!=null)
                    {
                        mbeRequest.setBody(sBodyJSON);
                        con.continuationMethod = 'processMBEOtherInsuranceResponse';
                        continuationLabel = con.addHttpRequest(mbeRequest);
                        Map<String,String> stateMapOI= new Map<String,String>();
                        String sMPId = (lstMemberPlan[0].Id != Null) ? lstMemberPlan[0].Id : '';
                        stateMapOI.put('sPolicyMember',sMPId);
                        stateMapOI.put('sRequest',continuationLabel);
                        con.state=stateMapOI;
                    }
                }
                return con;
            }
            oPolMemberDetails.isOnSwitch = true;
            return oPolMemberDetails;
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MemberPlanDetail_LS_HUM', 'getOtherInsuranceData');
        }
        oPolMemberDetails.isOnSwitch = true;
        return oPolMemberDetails;
    }
    

    /*
    *  This method is used to Parse response from MBEPlus service
    *  @name   processMBEOtherInsuranceResponse
    *  @param  List and object
    *  @return object
    */
    @testvisible
    public static object processMBEOtherInsuranceResponse(object state)
    {   
        AdditionalResponseWrapper_DTO_HUM  oPolMemberDetails = new AdditionalResponseWrapper_DTO_HUM ();
        try
        {
            Map<String,String> mapState=(map<String, String>)state; 
            HttpResponse oResponse = Continuation.getResponse(mapState.get('sRequest'));   
            String sMPID =  mapState.get('sPolicyMember');
            List<MemberPlan> oPolMemberInfo = MemberPlanDetail_LD_HUM.getMemberName(sMPID);
            retrieveCoverageDetails(sMPID); // retrive details of Policy Member
            if ((oResponse != null && !oResponse.getBody().containsIgnoreCase(GlobalCommonConstants_HUM.VOB_FAULT_CODE) && 
                (oResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_OK_HUM || oResponse.getStatusCode() == GlobalCommonConstants_HUM.HTTP_ACCEPTED_HUM )
                ) || Test.isRunningTest() )
            {
                MBEPlusOIResponse_DTO_HUM outResponse = (MBEPlusOIResponse_DTO_HUM)System.JSON.deserialize(oResponse.getbody(), MBEPlusOIResponse_DTO_HUM.class);                                                               
                if(outResponse != null && outResponse.GetMemberResponse != null && outResponse.GetMemberResponse.Body != null && 
                   outResponse.GetMemberResponse.Body.Member != null && String.isNotBlank(outResponse.GetMemberResponse.Body.Member.MasterId) && 
                   mapPolicyMembers.containsKey(outResponse.GetMemberResponse.Body.Member.MasterId))    
                {   
                    MemberPlan oPolicyMember = mapPolicyMembers.get(outResponse.GetMemberResponse.Body.Member.MasterId);
                    list<PurchaserPlan> lstPlans = new list<PurchaserPlan>();
                    if(mapPolicyPlanDetails!=null && !mapPolicyPlanDetails.isEmpty() && mapPolicyPlanDetails.containsKey(oPolicyMember.PlanId)){
                        lstPlans = mapPolicyPlanDetails.get(oPolicyMember.PlanId); 
                    } 
                    //Retrieve COB details from MBE Response
                    //Introduced new switch '1707499_Switch' to handle switch between OICoordinationOfBenefit and CoordinationOfBenefit node from MBE+ response 
                    if(HUMUtilityHelper.isCRMFunctionalityON('1707499_Switch'))
                    {
                        //Below code will parse MBE+ repsonse with new node OICoordinationOfBenefit 
                        list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstOICOBFromService = parseMBERespForOICOBDetails(outResponse, oPolicyMember, lstPlans); 
                        oPolMemberDetails.validOIWrapperList = (lstOICOBFromService!=null && !lstOICOBFromService.isEmpty() && !oPolMemberInfo[0].ETL_Record_Deleted__c) ? processOICOBData(lstOICOBFromService) : Null ;
                    }else{
                        //Below code will parse MBE+ repsonse with old node OICoordinationOfBenefit
                        list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBFromService = parseMBERespForCOBDetails(outResponse, oPolicyMember, lstPlans);     
                        oPolMemberDetails.validOIWrapperList = (lstCOBFromService!=null && !lstCOBFromService.isEmpty() && !oPolMemberInfo[0].ETL_Record_Deleted__c) ? processCOBData(lstCOBFromService) : Null ;
                    }
                    // To assign dual status indicator field 
                    oPolMemberDetails.isMemDualEligibleInLastTwelveMonth = DualEligibleDetailsHelper_H_HUM.processDualResponse(outResponse);
                    oPolMemberDetails.isDualEligibleListHasValue = checkDualEligibleListIsNull(outResponse);
                }
            }
        } 
        catch (Exception ex)
        {   
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM', 'processMBEOtherInsuranceResponse');
            return null;   
        }
        oPolMemberDetails.isOnSwitch = true;
        return oPolMemberDetails;
    }
    
    public static boolean checkDualEligibleListIsNull(MBEPlusOIResponse_DTO_HUM resvalidOI){
        try{
            MBEPlusOIResponse_DTO_HUM.GetMemberResponse oGetMemberResponse = resvalidOI.GetMemberResponse;
            MBEPlusOIResponse_DTO_HUM.Body oBody = oGetMemberResponse.Body;
            MBEPlusOIResponse_DTO_HUM.Member oMember = oBody.Member;
            boolean isDualEligibleListHasValue = false;
            if (oMember!=null && (oMember.DualStatusList != null && !oMember.DualStatusList.isEmpty()))
            {
                isDualEligibleListHasValue = true;
            }
            return isDualEligibleListHasValue;
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MemberPlanDetail_LS_HUM', 'checkDualEligibleListIsNull');
            return false;
        }
    }
    
    /**
    * This method generates list of other insurance on policy member page.
    * @Nmae     processOICOBData
    * @param    ValidOIWrapper_DTO_HUM response
    * @return   List of ValidOIWrapper_DTO_HUM 
    */
    public static list<ValidOIWrapper_DTO_HUM> processOICOBData(list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCoordinationOfBenefit)
    { 
        list<ValidOIWrapper_DTO_HUM>  lstOtherInsuranceData = new list<ValidOIWrapper_DTO_HUM>();
        for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCoordinationOfBenefit : lstCoordinationOfBenefit)
        { 
            ValidOIWrapper_DTO_HUM oOIWrap = new ValidOIWrapper_DTO_HUM();

            oOIWrap.sCarrierName = oCoordinationOfBenefit.CarrierName;
            oOIWrap.sEffectiveDate = oCoordinationOfBenefit.EffectiveDate;
            oOIWrap.sEndDate = oCoordinationOfBenefit.EndDate;
            //US2104099 - In Primary field CRM displays Y for the Primary and N for the Secondary and Blank for undefined
            if(HUMUtilityHelper.isCRMFunctionalityON('2104099')){
                oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == sNumOne) ? 'Y' : (oCoordinationOfBenefit.Primary == sNumZero) ? 'N' : '';
            }else{
                oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == Label.HUMConstOne) ? 'Y' : 'N';     
            }
            oOIWrap.sOILastVerifiedDate = oCoordinationOfBenefit.OILastVerifiedDate;
            oOIWrap.sLastUpdatedDate = oCoordinationOfBenefit.LastUpdatedDate;
                
            lstOtherInsuranceData.add(oOIWrap);
        }
        return lstOtherInsuranceData;
    }
    
    /**
    * This method generates list of other insurance on policy member page.
    * @Nmae     processCOBData
    * @param    ValidOIWrapper_DTO_HUM response
    * @return   List of ValidOIWrapper_DTO_HUM 
    */
    public static list<ValidOIWrapper_DTO_HUM> processCOBData(list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCoordinationOfBenefit)
    { 
        list<ValidOIWrapper_DTO_HUM>  lstOtherInsuranceData = new list<ValidOIWrapper_DTO_HUM>();
        for(MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit oCoordinationOfBenefit : lstCoordinationOfBenefit)
        { 
            ValidOIWrapper_DTO_HUM oOIWrap = new ValidOIWrapper_DTO_HUM();

            oOIWrap.sCarrierName = oCoordinationOfBenefit.CarrierName;
            oOIWrap.sEffectiveDate = oCoordinationOfBenefit.EffectiveDate;
            oOIWrap.sEndDate = oCoordinationOfBenefit.EndDate;
            //US2104099 - In Primary field CRM displays Y for the Primary and N for the Secondary and Blank for undefined
            if(HUMUtilityHelper.isCRMFunctionalityON('2104099')){
                oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == sNumOne) ? 'Y' : (oCoordinationOfBenefit.Primary == sNumZero) ? 'N' : '';
            }else{
                oOIWrap.sPrimary = (oCoordinationOfBenefit.Primary == Label.HUMConstOne) ? 'Y' : 'N';     
            }
            oOIWrap.sOILastVerifiedDate = oCoordinationOfBenefit.OILastVerifiedDate;
            oOIWrap.sLastUpdatedDate = oCoordinationOfBenefit.LastUpdatedDate;
                
            lstOtherInsuranceData.add(oOIWrap);
        }
        return lstOtherInsuranceData;
    }
    
    /*
    * Method Name : retrieveCoverageDetails
    * Description : This method is used to retrieve the coverage details for a PolicyMember to be dispalyed on COB template
    * Return type : Void
    * Paramater   : None
    */
   public static void retrieveCoverageDetails(String polMemId)
    {   
        try
        {  
            if(String.isNotBlank(polMemId))
            {
                list<MemberPlan> lstPolicyMem = new list<MemberPlan>();
                set<Id> setPolicyIds = new set<Id>();
                lstPolicyMem =  MemberPlanDetail_LD_HUM.getMemberName(polMemId);
   
                if(lstPolicyMem!=null && !lstPolicyMem.isEmpty() && String.isNotBlank(lstPolicyMem[0].Member.Enterprise_Id__c))
                {
                    mapPolicyMembers.put(lstPolicyMem[0].Member.Enterprise_Id__c, lstPolicyMem[0]);
                    if(lstPolicyMem[0].PlanId !=null){ 
                        setPolicyIds.add(lstPolicyMem[0].PlanId); 
                    }
                }
                
                if(mapPolicyMembers!=null && !mapPolicyMembers.isEmpty()) 
                {   
                    if(setPolicyIds!=null && !setPolicyIds.isEmpty())
                    {
                        list<PurchaserPlan> lstPolicyPlans = new list<PurchaserPlan>();
                        lstPolicyPlans = MemberPlanDetail_LD_HUM.getPurchaserPlanOtherInsurance(setPolicyIds);
                        if(lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
                        {
                            for(PurchaserPlan oPlan : lstPolicyPlans)
                            {
                                if(mapPolicyPlanDetails.containsKey(oPlan.Id)){ 
                                    mapPolicyPlanDetails.get(oPlan.Id).add(oPlan); 
                                }else{ 
                                    mapPolicyPlanDetails.put(oPlan.Id, new list<PurchaserPlan>{oPlan}); 
                                }          
                            }
                        }    
                    }    
                }
            }        
       }
       catch(Exception e)
        {   
           HUMExceptionHelper.logErrors(e, 'MemberPlanDetail_LS_HUM', 'retrieveCoverageDetails');
        } 
    }
    /*     
    * Method Name : createMBEPlusRequest     
    * Description : This method is used to Create the MBEPLus Request DTO for COB Template      
    * Return type : MBEPlusOIRequest_DTO_HUM     
    * Paramater   : sEnterpriseID - input personId for the MBEPlus request, Boolean to add OICOB node in the request     
    */
    public static MBEPlusOIRequest_DTO_HUM createMBEPlusRequest(String sEnterpriseID, Boolean bAddOICOBNode)
    {
        MBEPlusOIRequest_DTO_HUM req;
        try
        {
            if(String.isNotBlank(sEnterpriseID) && bAddOICOBNode!=null)
            {
                req = new MBEPlusOIRequest_DTO_HUM();
                MBEPlusOIRequest_DTO_HUM.GetMember getMember = new MBEPlusOIRequest_DTO_HUM.GetMember();            
                MBEPlusOIRequest_DTO_HUM.Member   mem = new MBEPlusOIRequest_DTO_HUM.Member();      
                MBEPlusOIRequest_DTO_HUM.DualStatusList objDualStatusList = new MBEPlusOIRequest_DTO_HUM.DualStatusList();
                mem.DualStatusList  = objDualStatusList;
                mem.SoldProductList = createSoldProductList(bAddOICOBNode);
                
                mem.GetCriteria = createGetCriteriaList(sEnterpriseID);        
                
                getMember.Member = mem;
                req.GetMember = getMember;
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'createMBEPlusRequest');
        }
        return req;
    }
    
    /*     
    * Method Name : createSoldProductList     
    * Description : This method is used to create the input request for fetching the COB Detals      
    * Return type : MBEPlusOIRequest_DTO_HUM.SoldProductList     
    * Paramater   : Boolean flag to add OICOB Node to request
    */
    @testVisible
    private static MBEPlusOIRequest_DTO_HUM.SoldProductList createSoldProductList(Boolean bAddOICOBNode)
    {
        /* Start of Create SOld product list */
        MBEPlusOIRequest_DTO_HUM.SoldProductList objSoldProductList = new MBEPlusOIRequest_DTO_HUM.SoldProductList();
        List<MBEPlusOIRequest_DTO_HUM.SoldProduct> listObjSoldProduct = new List<MBEPlusOIRequest_DTO_HUM.SoldProduct>();
        MBEPlusOIRequest_DTO_HUM.SoldProduct ObjSoldProduct = new MBEPlusOIRequest_DTO_HUM.SoldProduct();
        MBEPlusOIRequest_DTO_HUM.InsuranceProduct objInsuranceProduct = new MBEPlusOIRequest_DTO_HUM.InsuranceProduct();
        MBEPlusOIRequest_DTO_HUM.SoldProductDetailList soldProDetailList = new MBEPlusOIRequest_DTO_HUM.SoldProductDetailList();
        MBEPlusOIRequest_DTO_HUM.SoldProductDetail  soldProductDetail  = new MBEPlusOIRequest_DTO_HUM.SoldProductDetail();
        MBEPlusOIRequest_DTO_HUM.InsuranceCoverage  insuranceCoverage  = new MBEPlusOIRequest_DTO_HUM.InsuranceCoverage();        
        MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList coveredRolPlatPointList = new MBEPlusOIRequest_DTO_HUM.CoveredRolePlatformPointerList();
        MBEPlusOIRequest_DTO_HUM.CoveredRoleList coverRoleList= new MBEPlusOIRequest_DTO_HUM.CoveredRoleList(); 
        MBEPlusOIRequest_DTO_HUM.CoveredRole coveredRol = new MBEPlusOIRequest_DTO_HUM.CoveredRole();
        MBEPlusOIRequest_DTO_HUM.CoordinationOfBenefitList COBList = new MBEPlusOIRequest_DTO_HUM.CoordinationOfBenefitList();
        insuranceCoverage.CoordinationOfBenefitList = COBList;
        if(bAddOICOBNode!=null && bAddOICOBNode)
        {
            MBEPlusOIRequest_DTO_HUM.OICoordinationOfBenefitList OICOBList = new MBEPlusOIRequest_DTO_HUM.OICoordinationOfBenefitList();
            insuranceCoverage.OICoordinationOfBenefitList = OICOBList;
        }
        MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList grpList = new MBEPlusOIRequest_DTO_HUM.GroupPlatformPointerList();
        insuranceCoverage.GroupPlatformPointerList = grpList;

                
        coveredRol.CoveredRolePlatformPointerList= coveredRolPlatPointList;
        coverRoleList.CoveredRole= coveredRol;
        insuranceCoverage.CoveredRoleList =coverRoleList;
        soldProductDetail.InsuranceCoverage = insuranceCoverage;
        soldProDetailList.SoldProductDetail = soldProductDetail;
        objInsuranceProduct.SoldProductDetailList = soldProDetailList;      
        ObjSoldProduct.InsuranceProduct = objInsuranceProduct;
        listObjSoldProduct.add(objSoldProduct);
        objSoldProductList.SoldProduct = listObjSoldProduct;
        /* End of Create SOld product list */
        return objSoldProductList;
    }

    /*     
    * Method Name : createGetCriteriaList     
    * Description : This method is used to create the input request Criteria for MBEPlus Service      
    * Return type : MBEPlusOIRequest_DTO_HUM.GetCriteria     
    * Paramater   : sEnterpriseID - input personId for the MBEPlus request     
    */
    @testVisible
    private static MBEPlusOIRequest_DTO_HUM.GetCriteria createGetCriteriaList(String sEnterpriseID)
    {        
        /* Start of Create getCriteriaList*/
        MBEPlusOIRequest_DTO_HUM.GetCriteria  getCriteriaList = new MBEPlusOIRequest_DTO_HUM.GetCriteria();
        MBEPlusOIRequest_DTO_HUM.PersonIdCriteria  personIdCriteria  = new MBEPlusOIRequest_DTO_HUM.PersonIdCriteria();
        personIdCriteria.PersonId = sEnterpriseID;
        MBEPlusOIRequest_DTO_HUM.PeriodList periodValues = new MBEPlusOIRequest_DTO_HUM.PeriodList();         
        periodValues.Period = new list<String>{ GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_CURRENT_HUM, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PAST_HUM, GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_FUTURE_HUM };
        personIdCriteria.PeriodList = periodValues;        
        personIdCriteria.AsOfDate = DateTime.now().format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PCP_REFDATEFORMT_HUM);
        getCriteriaList.PersonIdCriteria  = personIdCriteria;
        /* End of Create getCriteriaList*/
        return getCriteriaList;
    }
    
    
    
        /*     
    * Method Name : parseMBERespForOICOBDetails     
    * Description : This method is used to fetch the COB details DTO from MBEPlus Response      
    * Return type : list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> - list of COB details wrapper
    * Paramater   : policyMemRec - Policy_Member__c record for the COB details, oMBEResponse - MBEPlus Response, list<Policy_Plan_Details__c>     
    */
    public static list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> parseMBERespForOICOBDetails(MBEPlusOIResponse_DTO_HUM oMBEResponse, MemberPlan policyMemRec, list<PurchaserPlan> lstPolicyPlans)
    {        
        list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> lstCOBDetails = new list<MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();
        map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit> mapCOBDetails = new map<String, MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit>();                         
        try
        {
            if(oMBEResponse!=null && policyMemRec!=null && oMBEResponse.GetMemberResponse != null && oMBEResponse.GetMemberResponse.Body != null && oMBEResponse.GetMemberResponse.Body.Member != null && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
            {
                MBEPlusOIResponse_DTO_HUM.Member oMemberDTO = oMBEResponse.GetMemberResponse.Body.Member;
                list<MBEPlusOIResponse_DTO_HUM.SoldProduct> lstSoldProducts = new list<MBEPlusOIResponse_DTO_HUM.SoldProduct>(); 
                if(oMemberDTO!=null && oMemberDTO.SoldProductList!=null && oMemberDTO.SoldProductList.SoldProduct!=null)
                {
                    MBEPlusOIResponse_DTO_HUM.SoldProductList oSoldProductList = oMemberDTO.SoldProductList;
                    lstSoldProducts = oSoldProductList.SoldProduct;
                }
                if(lstSoldProducts!=null && !lstSoldProducts.isEmpty())
                {
                    for(MBEPlusOIResponse_DTO_HUM.SoldProduct oSoldProduct : lstSoldProducts)
                    {                    
                        if(oSoldProduct.InsuranceProduct != null && oSoldProduct.InsuranceProduct.SoldProductDetailList != null && oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail !=null                        
                           && String.isNotBlank(oSoldProduct.InsuranceProduct.Source) && String.isNotBlank(policyMemRec.Policy_Platform__c) && oSoldProduct.InsuranceProduct.Source.equals(policyMemRec.Policy_Platform__c)
                           && String.isNotBlank(oSoldProduct.InsuranceProduct.ProductId) && checkProductIDFromCRM(oSoldProduct.InsuranceProduct.ProductId, lstPolicyPlans))
                        {                                                                                                                       
                            String sGroupIDCRM = policyMemRec.Policy_Platform__c.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM) || policyMemRec.Policy_Platform__c.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HumHPRecType) ? policyMemRec.Plan.Source_Cust_Cov_Key__c.subString(0,6) : policyMemRec.GroupNumber;  
                                                                                        
                            list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail> lstSoldProductDetail = oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail;                       
                            if(lstSoldProductDetail!=null && !lstSoldProductDetail.isEmpty())
                            {
                                for(MBEPlusOIResponse_DTO_HUM.SoldProductDetail oSoldProductDetail : lstSoldProductDetail)
                                {                                                                                       
                                    String sGroupID='';
                                    String sMemberRelationship='';
                                    String sMemberIDBase='';
                                    if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList!=null && 
                                       oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey !=null && !oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey.isEmpty())
                                    {
                                        for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey grpPlatformKey : oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey)
                                        {                                       
                                            if(String.isNotBlank(grpPlatformKey.KeyType) && grpPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CASESERVICE_ACCOUNTGROUPTYPEID))
                                            {
                                                sGroupID = grpPlatformKey.KeyValue;
                                                break;
                                            }
                                        }
                                    }
                                    if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole!=null && !oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole.isEmpty())
                                    {
                                        for(MBEPlusOIResponse_DTO_HUM.CoveredRole oCoveredRole: oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole)
                                        {
                                            if(String.isNotBlank(oCoveredRole.MemberMasterId) && oCoveredRole.MemberMasterId.equals(policyMemRec.Member.Enterprise_ID__c))
                                            {
                                                if(oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey!=null && !oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey.isEmpty())
                                                {
                                                    for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey memPlatformKey : oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey)
                                                    {
                                                        if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEMBERIDDEPENDENTCODE))
                                                        {
                                                            sMemberRelationship = memPlatformKey.KeyValue;                                 
                                                        }
                                                        if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEMBERIDBASE))
                                                        {
                                                            sMemberIDBase = memPlatformKey.KeyValue;
                                                        }
                                                    }
                                                }
                                                break; 
                                            }
                                        }
                                    }
                                    String endDate = '';
                                    String startDate = '';
                                    if(policyMemRec.EffectiveTo != null){
                                        if(policyMemRec.EffectiveTo == date.newInstance(4000,12,31)){
                                     		endDate = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DefaultEffectiveDate;     
                                        }else{
                                        	Datetime endDateTime = datetime.newInstance(policyMemRec.EffectiveTo.year(), policyMemRec.EffectiveTo.month(),policyMemRec.EffectiveTo.day());
                                        	endDate = endDateTime.format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DATEFORMAT);     
                                        }
                                    }
                                    if(policyMemRec.EffectiveFrom != null){
                                        Datetime startDateTime = datetime.newInstance(policyMemRec.EffectiveFrom.year(), policyMemRec.EffectiveFrom.month(),policyMemRec.EffectiveFrom.day());
                                        startDate = startDateTime.format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DATEFORMAT);
                                    }
                                    
                                    if(oSoldProductDetail.InsuranceCoverage != null && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.StartDate)
                                       && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.EndDate) && oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList != null
                                       && oSoldProductDetail.InsuranceCoverage.StartDate.equals(startDate) && oSoldProductDetail.InsuranceCoverage.EndDate.equals(endDate)
                                       && String.isNotBlank(sMemberIDBase) && String.isNotBlank(sMemberRelationship) && String.isNotBlank(policyMemRec.Name) && policyMemRec.Name.equalsIgnoreCase(sMemberIDBase+sMemberRelationship) 
                                       && String.isNotBlank(sGroupID) && String.isNotBlank(sGroupIDCRM) && sGroupID.equalsIgnoreCase(sGroupIDCRM) && oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit!=null
                                       && !oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit.isEmpty())
                                    {                                                                               
                                        String sMapKey = '';
                                        for(MBEPlusOIResponse_DTO_HUM.OICoordinationOfBenefit oCOBDTO : oSoldProductDetail.InsuranceCoverage.OICoordinationOfBenefitList.OICoordinationOfBenefit)
                                        {
                                            if(oCOBDTO!=null)
                                            {
                                                sMapKey = '';
                                                if(String.isNotBlank(oCOBDTO.CarrierName))  sMapKey += oCOBDTO.CarrierName;
                                                if(String.isNotBlank(oCOBDTO.EffectiveDate))    sMapKey += oCOBDTO.EffectiveDate;
                                                if(String.isNotBlank(oCOBDTO.EndDate))  sMapKey += oCOBDTO.EndDate;
                                                if(String.isNotBlank(oCOBDTO.Primary))  sMapKey += oCOBDTO.Primary;
                                                if(String.isNotBlank(oCOBDTO.OILastVerifiedDate))   sMapKey += oCOBDTO.OILastVerifiedDate;
                                                if(String.isNotBlank(oCOBDTO.LastUpdatedDate))  sMapKey += oCOBDTO.LastUpdatedDate;
                                                if(String.isNotBlank(sMapKey)) mapCOBDetails.put(sMapKey, oCOBDTO);
                                            }
                                        }                                                                                                                                                                                                       
                                    }                                
                                }
                            }                           
                        }
                    }
                }                       
            }
            if(mapCOBDetails!=null && !mapCOBDetails.isEmpty()) lstCOBDetails.addAll(mapCOBDetails.values());           
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'parseMBERespForOICOBDetails');  
        }
        return lstCOBDetails;
    }
    /*     
    * Method Name : checkProductIDFromCRM
    * Description : Method is used to match ProducId from MBE+ reponse with the ProductId in CRM.     
    * Return type : Boolean     
    * Paramater   : String, list<PurchaserPlan>  
    */
    @testVisible
    private static Boolean checkProductIDFromCRM(String sProductId, list<PurchaserPlan> lstPolicyPlans)
    {
        Boolean bProductIDMatched = false;
        if(String.isNotBlank(sProductId) && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
        {
            for(PurchaserPlan oPolicyPlan : lstPolicyPlans)
            {               
                if(oPolicyPlan.Product__c!=null && String.isNotBlank(oPolicyPlan.Product__r.Sold_Product_Key_Value__c) && sProductId.equals(oPolicyPlan.Product__r.Sold_Product_Key_Value__c))
                {
                    bProductIDMatched = true;
                    break;
                }           
            }           
        }
        return bProductIDMatched;
    }
    
        /*     
    * Method Name : parseMBERespForCOBDetails     
    * Description : This method is used to fetch the COB details DTO from MBEPlus Response      
    * Return type : list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> - list of COB details wrapper
    * Paramater   : policyMemRec - MemberPlan record for the COB details, oMBEResponse - MBEPlus Response, list<Policy_Plan_Details__c>     
    */
    public static list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> parseMBERespForCOBDetails(MBEPlusOIResponse_DTO_HUM oMBEResponse, MemberPlan policyMemRec, list<PurchaserPlan> lstPolicyPlans)
    {        
        list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCOBDetails = new list<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();
        map<String, MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> mapCOBDetails = new map<String, MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();                         
        try
        {
            if(oMBEResponse!=null && policyMemRec!=null && oMBEResponse.GetMemberResponse != null && oMBEResponse.GetMemberResponse.Body != null && oMBEResponse.GetMemberResponse.Body.Member != null && lstPolicyPlans!=null && !lstPolicyPlans.isEmpty())
            {
                MBEPlusOIResponse_DTO_HUM.Member oMemberDTO = oMBEResponse.GetMemberResponse.Body.Member;
                list<MBEPlusOIResponse_DTO_HUM.SoldProduct> lstSoldProducts = new list<MBEPlusOIResponse_DTO_HUM.SoldProduct>(); 
                if(oMemberDTO!=null && oMemberDTO.SoldProductList!=null && oMemberDTO.SoldProductList.SoldProduct!=null)
                {
                    MBEPlusOIResponse_DTO_HUM.SoldProductList oSoldProductList = oMemberDTO.SoldProductList;
                    lstSoldProducts = oSoldProductList.SoldProduct;
                }
                if(lstSoldProducts!=null && !lstSoldProducts.isEmpty())
                {
                    for(MBEPlusOIResponse_DTO_HUM.SoldProduct oSoldProduct : lstSoldProducts)
                    {                    
                        if(oSoldProduct.InsuranceProduct != null && oSoldProduct.InsuranceProduct.SoldProductDetailList != null && oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail !=null                        
                           && String.isNotBlank(oSoldProduct.InsuranceProduct.Source) && String.isNotBlank(policyMemRec.Policy_Platform__c) && oSoldProduct.InsuranceProduct.Source.equals(policyMemRec.Policy_Platform__c)
                           && String.isNotBlank(oSoldProduct.InsuranceProduct.ProductId) && checkProductIDFromCRM(oSoldProduct.InsuranceProduct.ProductId, lstPolicyPlans))
                        {                                                             
                            String sGroupIDCRM = policyMemRec.Policy_Platform__c.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.VOB_PLATFORM_LV_HUM) || policyMemRec.Policy_Platform__c.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.HumHPRecType) ? policyMemRec.Plan.Source_Cust_Cov_Key__c.subString(0,6) : policyMemRec.GroupNumber;  
                                                                                        
                            list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail> lstSoldProductDetail = oSoldProduct.InsuranceProduct.SoldProductDetailList.SoldProductDetail;                       
                            if(lstSoldProductDetail!=null && !lstSoldProductDetail.isEmpty())
                            {
                                for(MBEPlusOIResponse_DTO_HUM.SoldProductDetail oSoldProductDetail : lstSoldProductDetail)
                                {                                                                                       
                                    String sGroupID='';
                                    String sMemberRelationship='';
                                    String sMemberIDBase='';
                                    if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList!=null && 
                                       oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey !=null && !oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey.isEmpty())
                                    {
                                        for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey grpPlatformKey : oSoldProductDetail.InsuranceCoverage.GroupPlatformPointerList.PlatformProxyKey)
                                        {                                       
                                            if(String.isNotBlank(grpPlatformKey.KeyType) && grpPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.CASESERVICE_ACCOUNTGROUPTYPEID))
                                            {
                                                sGroupID = grpPlatformKey.KeyValue;
                                                break;
                                            }
                                        }
                                    }
                                    if(oSoldProductDetail.InsuranceCoverage!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList!=null && oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole!=null && !oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole.isEmpty())
                                    {
                                        for(MBEPlusOIResponse_DTO_HUM.CoveredRole oCoveredRole: oSoldProductDetail.InsuranceCoverage.CoveredRoleList.CoveredRole)
                                        {
                                            if(String.isNotBlank(oCoveredRole.MemberMasterId) && oCoveredRole.MemberMasterId.equals(policyMemRec.Member.Enterprise_ID__c))
                                            {
                                                if(oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey!=null && !oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey.isEmpty())
                                                {
                                                    for(MBEPlusOIResponse_DTO_HUM.PlatformProxyKey memPlatformKey : oCoveredRole.CoveredRolePlatformPointerList.PlatformProxyKey)
                                                    {
                                                        if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEMBERIDDEPENDENTCODE))
                                                        {
                                                            sMemberRelationship = memPlatformKey.KeyValue;                                 
                                                        }
                                                        if(String.isNotBlank(memPlatformKey.KeyType) && memPlatformKey.KeyType.equalsIgnoreCase(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.MEMBERIDBASE))
                                                        {
                                                            sMemberIDBase = memPlatformKey.KeyValue;
                                                        }
                                                    }
                                                }
                                                break; 
                                            }
                                        }
                                    }         
                                   
                                    String endDate = '';
                                    String startDate = '';
                                    if(policyMemRec.EffectiveTo != null){
                                        if(policyMemRec.EffectiveTo == date.newInstance(4000,12,31)){
                                     		endDate = GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DefaultEffectiveDate;     
                                        }else{
                                        	Datetime endDateTime = datetime.newInstance(policyMemRec.EffectiveTo.year(), policyMemRec.EffectiveTo.month(),policyMemRec.EffectiveTo.day());
                                        	endDate = endDateTime.format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DATEFORMAT);     
                                        }
                                    }
                                    if(policyMemRec.EffectiveFrom != null){
                                        Datetime startDateTime = datetime.newInstance(policyMemRec.EffectiveFrom.year(), policyMemRec.EffectiveFrom.month(),policyMemRec.EffectiveFrom.day());
                                        startDate = startDateTime.format(GLOBAL_MEMBERPLAN_CONSTANT_LH_HUM.OI_DATEFORMAT);
                                    }
                                    
                                    if(oSoldProductDetail.InsuranceCoverage != null && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.StartDate)
                                       && String.isNotBlank(oSoldProductDetail.InsuranceCoverage.EndDate) && oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList != null
                                       && oSoldProductDetail.InsuranceCoverage.StartDate.equals(startDate) && oSoldProductDetail.InsuranceCoverage.EndDate.equals(endDate)
                                       && String.isNotBlank(sGroupID) && String.isNotBlank(sGroupIDCRM) && sGroupID.equalsIgnoreCase(sGroupIDCRM)
                                       && String.isNotBlank(sMemberIDBase) && String.isNotBlank(sMemberRelationship) && String.isNotBlank(policyMemRec.Name) && policyMemRec.Name.equalsIgnoreCase(sMemberIDBase+sMemberRelationship) 
                                      && oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList.CoordinationOfBenefit!=null&&!oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList.CoordinationOfBenefit.isEmpty()   
                                      )
                                    {
                                            String sMapKey = '';
                                            List<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> oCOBDTOList = oSoldProductDetail.InsuranceCoverage.CoordinationOfBenefitList.CoordinationOfBenefit;
                                            for(MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit oCOBDTO : oCOBDTOList)
                                            {
                                                if(oCOBDTO!=null)
                                                {
                                                    sMapKey = '';
                                                    if(String.isNotBlank(oCOBDTO.CarrierName))  sMapKey += oCOBDTO.CarrierName;
                                                    if(String.isNotBlank(oCOBDTO.EffectiveDate))    sMapKey += oCOBDTO.EffectiveDate;
                                                    if(String.isNotBlank(oCOBDTO.EndDate))  sMapKey += oCOBDTO.EndDate;
                                                    if(String.isNotBlank(oCOBDTO.Primary))  sMapKey += oCOBDTO.Primary;
                                                    if(String.isNotBlank(oCOBDTO.OILastVerifiedDate))   sMapKey += oCOBDTO.OILastVerifiedDate;
                                                    if(String.isNotBlank(oCOBDTO.LastUpdatedDate))  sMapKey += oCOBDTO.LastUpdatedDate;
                                                    if(String.isNotBlank(sMapKey)) mapCOBDetails.put(sMapKey, oCOBDTO);
                                                }
                                            }
                                    }                                
                                }
                            }                           
                        }
                    }
                }                       
            }
            if(mapCOBDetails!=null && !mapCOBDetails.isEmpty()) lstCOBDetails.addAll(mapCOBDetails.values());           
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'HUMProcessUtilityHelper', 'parseMBERespForCOBDetails');    
        }
        return lstCOBDetails;
    }  

    //================================= Other Insurance Service Stop ======================================================     
    /**
    * Returns an object and method calls the MBEPlus web service
    * @pram  id of the policy member in string format
    * @param  type in string format 
    * @return  object
    */
    public static Object getDualStatusData(String memberPlanId)
    {
        AdditionalResponseWrapper_DTO_HUM returnWrapper = new AdditionalResponseWrapper_DTO_HUM();
        try
        { 
            List<MemberPlan> lstMemberPlan = new List<MemberPlan>();
            lstMemberPlan =  MemberPlanDetail_LD_HUM.getMemberName(memberPlanId);
            System.debug(lstMemberPlan);
            if(!lstMemberPlan.IsEmpty())
            {
                String sPersonId = (lstMemberPlan[0].Member.Enterprise_Id__c != Null) ? lstMemberPlan[0].Member.Enterprise_Id__c : ''; 
                HUM_Webservice_Callout__c oSvcEncrypt = HUM_Webservice_Callout__c.getValues('MBEPLUSServiceREST'); 
                Integer iTIMEOUT_INT_SECS = Integer.valueof(oSvcEncrypt.Timeout__c)/1000;
                HttpRequest request = new HttpRequest();
                request = setupDualStatusServiceRequest(request, oSvcEncrypt,sPersonId);
                Continuation con = new Continuation(iTIMEOUT_INT_SECS );
                sRequest = con.addHttpRequest(request);
                con.continuationMethod='processResponse';
                stateMap.put('sRequest',sRequest);
                con.state=stateMap;
                return con;
            }
           else
            {
                returnWrapper.isOnSwitch = true;
                return returnWrapper;          
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM', 'getDualStatusData');
            returnWrapper.isOnSwitch = true;
            return returnWrapper;         
        }    
    }
    
     /*
    *  This method is used to form http request for getTaskHierarchy Servive
    *  @name   setupRequest
    *  @param  HttpRequest - instance of HttpRequest, HUM_Webservice_Callout__c  - contains webservice callout details, 
    sCaseID-CaseId                       
    *  @return HttpRequest - request for getCaseDetail Service
    */
    public static HttpRequest setupDualStatusServiceRequest(HttpRequest request, HUM_Webservice_Callout__c oSvcEncrypt, String sPersonId)
    {
        HttpRequest orequest = new HttpRequest();    
        try
        { 
            string sBody = buildDualStatusRequest(sPersonId);
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
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM' , 'setupDualStatusServiceRequest');
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
    public static string buildDualStatusRequest(String sPersonId)
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
            HUMExceptionHelper.logErrors(ex, 'MemberPlanDetail_LS_HUM' , 'buildDualStatusRequest');
            sJsonbody =  NULL;
        }
        return sJsonbody;
    }
    
    @AuraEnabled
    public static object processResponseDualStatus(Object state)
    {
        AdditionalResponseWrapper_DTO_HUM returnWrapper = new AdditionalResponseWrapper_DTO_HUM();
        try 
        { 
            Map<String,String> mapState=(map<String, String>)state;
            if(mapState != null){
                HttpResponse sResponse = Continuation.getResponse((mapState.get('sRequest')));
                if(sResponse != null && (sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_OK_HUM || sResponse.getStatusCode() == GLOBAL_CONSTANT_HUM.HTTP_ACCEPTED_HUM && !sResponse.getbody().contains(GLOBAL_CONSTANT_HUM.FAULTCODE_ALERTS_HUM)))
                { 
                    DualEligibleDetailsResponse_H_HUM outResponse = (DualEligibleDetailsResponse_H_HUM)System.JSON.deserialize(sResponse.getbody(), DualEligibleDetailsResponse_H_HUM.class);                                             
                    if(outResponse != null && outResponse.GetMemberResponse != null && outResponse.GetMemberResponse.Body != null ){
                        DualEligibleDetails_DTO_HUM oMoopDetailParse = new DualEligibleDetails_DTO_HUM();
                        DualEligibleDetailsResponse_H_HUM.GetMemberResponse oMoopDetailResponse = outResponse.GetMemberResponse;
                        DualEligibleDetailsResponse_H_HUM.Body oMoopDetailBody = oMoopDetailResponse.Body;
                        
                        List<DualEligibleDetailsResponse_H_HUM.DualStatusList> lstDualStatusResponse = oMoopDetailBody.DualStatusList;     
                        List<DualEligibleDetailsResponse_H_HUM.DeemingPeriodList> lstDualDeemingResponse = oMoopDetailBody.DeemingPeriodList;
                        returnWrapper.lstDualStatusLevel =  buildWrapperDualsList(lstDualStatusResponse);
                        returnWrapper.lstDeemingPeriods = buildWrapperDeemingList(lstDualDeemingResponse);
                        returnWrapper.isOnSwitch = true;
                    }    
                    
                    
                }else{
                    returnWrapper.isOnSwitch = true;
                    return returnWrapper;
                }
            }
        }catch(Exception e){
            HUMExceptionHelper.logErrors(e, 'MemberPlanDetail_LS_HUM' , 'processResponseDualStatus');
            returnWrapper.isOnSwitch = true;
            return returnWrapper;
        }
        return returnWrapper;
    }
    /*
    *  This method is used to parse the Duals status List from Moop service and assign it to the wrapper class objects 
    *  @name   buildWrapperDualsList
    *  @param  List<DualEligibleDetails_DTO_HUM>  - The Response object
    *  @throws NA
    */
            
    public static List<DualEligibleDetails_DTO_HUM> buildWrapperDualsList(List<DualEligibleDetailsResponse_H_HUM.DualStatusList> lstStatus)
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
    
    public static List<DualEligibleDetails_DTO_HUM> buildWrapperDeemingList(List<DualEligibleDetailsResponse_H_HUM.DeemingPeriodList> lstDeeming)
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