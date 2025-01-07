/**
 * @File Name          : DemographicUpdate_S_HUM.cls
 * @Description        : Service class to generate HttpRequest for MBE Plus call
 * @Author             : Chetan Sindhe
 * @Group              : Team Thunder
 * @Last Modified On   : 7/2/2020, 2:31:30 PM
 * @Modification Log   : 
 * Ver       Date            Author      		    Modification
 * 1.0    5/11/2020        Chetan Sindhe           Initial Version
**/
public with sharing class DemographicUpdate_S_HUM {
    public static string sClassName = 'DemographicUpdate_S_HUM';

    /*
    * Method Name: getMemberMBERequest
    * Description: method is used to generate HttpRequest for MBE Plus call w.r. to request DTO
    * Parameter: Enterprise Id from Account for Member
    * Return Type: HTTPRequest
    */
    public static HTTPRequest getMemberMBERequest(string personId) {
        HttpRequest request = new HttpRequest();
        try {
            request = generateHTTPRequestStructure(GLOBAL_CONSTANT_HUM.PCP_MBEPLUSSERVICENAME_HUM);
            if(request != null) {
                DemographicMBEPlusRequest_DTO_HUM dtoRequest = 
                    new DemographicMBEPlusRequest_DTO_HUM();
                DemographicMBEPlusRequest_DTO_HUM.GetMember dtoGetMember =
                    new DemographicMBEPlusRequest_DTO_HUM.GetMember();
                DemographicMBEPlusRequest_DTO_HUM.Member dtoMember = 
                    new DemographicMBEPlusRequest_DTO_HUM.Member();
                DemographicMBEPlusRequest_DTO_HUM.Contact dtoContact = 
                    new DemographicMBEPlusRequest_DTO_HUM.Contact();
                DemographicMBEPlusRequest_DTO_HUM.ContactProfile dtoContactProfile = 
                    new DemographicMBEPlusRequest_DTO_HUM.ContactProfile();
                DemographicMBEPlusRequest_DTO_HUM.PhoneList dtoPhoneList = 
                    new DemographicMBEPlusRequest_DTO_HUM.PhoneList();
                 DemographicMBEPlusRequest_DTO_HUM.AddressList dtoAddressList = 
                    new DemographicMBEPlusRequest_DTO_HUM.AddressList();
                 DemographicMBEPlusRequest_DTO_HUM.EmailList dtoEmailList = 
                    new DemographicMBEPlusRequest_DTO_HUM.EmailList();
                DemographicMBEPlusRequest_DTO_HUM.GetCriteria dtoGetCriteria = 
                    new DemographicMBEPlusRequest_DTO_HUM.GetCriteria();
                DemographicMBEPlusRequest_DTO_HUM.PersonIdCriteria dtoPersonIdCriteria = 
                    new DemographicMBEPlusRequest_DTO_HUM.PersonIdCriteria();
                DemographicMBEPlusRequest_DTO_HUM.Period dtoPeriod = 
                    new DemographicMBEPlusRequest_DTO_HUM.Period();
                DemographicMBEPlusRequest_DTO_HUM.PlatformPointerList dtoPlatformPointerList = 
                    new DemographicMBEPlusRequest_DTO_HUM.PlatformPointerList();
                DemographicMBEPlusRequest_DTO_HUM.SoldProductList dtoSoldProductList = 
                    new DemographicMBEPlusRequest_DTO_HUM.SoldProductList();
                DemographicMBEPlusRequest_DTO_HUM.CoveredRole dtoCoveredRole = 
                    new DemographicMBEPlusRequest_DTO_HUM.CoveredRole();
                DemographicMBEPlusRequest_DTO_HUM.CoveredRoleList dtoCoveredRoleLIst = 
                    new DemographicMBEPlusRequest_DTO_HUM.CoveredRoleList();
                DemographicMBEPlusRequest_DTO_HUM.CoveredRolePlatformPointerList dtoCovRolPlatPointList = 
                    new DemographicMBEPlusRequest_DTO_HUM.CoveredRolePlatformPointerList();
                DemographicMBEPlusRequest_DTO_HUM.SoldProduct dtoSoldProduct = 
                    new DemographicMBEPlusRequest_DTO_HUM.SoldProduct();
                DemographicMBEPlusRequest_DTO_HUM.SoldProductDetail_IP dtoSoldProductDetailIP = 
                    new DemographicMBEPlusRequest_DTO_HUM.SoldProductDetail_IP();
                DemographicMBEPlusRequest_DTO_HUM.SoldProductDetailList_IP dtoSoldProductDetailListIP = 
                    new DemographicMBEPlusRequest_DTO_HUM.SoldProductDetailList_IP();
                DemographicMBEPlusRequest_DTO_HUM.SoldProductDetail_NIP dtoSoldProductDetailNIP = 
                    new DemographicMBEPlusRequest_DTO_HUM.SoldProductDetail_NIP();
                DemographicMBEPlusRequest_DTO_HUM.SoldProductDetailList_NIP dtoSoldProductDetailListNIP = 
                    new DemographicMBEPlusRequest_DTO_HUM.SoldProductDetailList_NIP();
                DemographicMBEPlusRequest_DTO_HUM.InsuranceProduct dtoInsuranceProduct = 
                    new DemographicMBEPlusRequest_DTO_HUM.InsuranceProduct();
                DemographicMBEPlusRequest_DTO_HUM.NonInsuranceProduct dtoNonInsuraneProduct = 
                    new DemographicMBEPlusRequest_DTO_HUM.NonInsuranceProduct();
                DemographicMBEPlusRequest_DTO_HUM.InsuranceCoverage dtoInsuranceCoverage = 
                    new DemographicMBEPlusRequest_DTO_HUM.InsuranceCoverage();
                DemographicMBEPlusRequest_DTO_HUM.NonInsuranceCoverage dtoNonInsuranceCoverage = 
                    new DemographicMBEPlusRequest_DTO_HUM.NonInsuranceCoverage();
                DemographicMBEPlusRequest_DTO_HUM.MemberPlatformPointerList dtoMemberPlatPointList = 
                    new DemographicMBEPlusRequest_DTO_HUM.MemberPlatformPointerList();
                DemographicMBEPlusRequest_DTO_HUM.GroupPlatformPointerList dtoGroupPlatPointList = 
                    new DemographicMBEPlusRequest_DTO_HUM.GroupPlatformPointerList();
                DemographicMBEPlusRequest_DTO_HUM.PlatformPointerList dtoPlatPointList = 
                    new DemographicMBEPlusRequest_DTO_HUM.PlatformPointerList();
                
                dtoGroupPlatPointList.PlatformProxyKey = '';
                dtoMemberPlatPointList.PlatformProxyKey = '';
                dtoCovRolPlatPointList.PlatformProxyKey = '';
                dtoCoveredRole.CoveredRolePlatformPointerList = dtoCovRolPlatPointList;
                dtoCoveredRoleLIst.CoveredRole = dtoCoveredRole;
                dtoInsuranceCoverage.CoveredRoleList = dtoCoveredRoleLIst;
                dtoInsuranceCoverage.GroupPlatformPointerList = dtoGroupPlatPointList;
                dtoInsuranceCoverage.MemberPlatformPointerList = dtoMemberPlatPointList;
                dtoNonInsuranceCoverage.CoveredRoleList = dtoCoveredRoleLIst;
                dtoNonInsuranceCoverage.GroupPlatformPointerList = dtoGroupPlatPointList;
                dtoNonInsuranceCoverage.MemberPlatformPointerList = dtoMemberPlatPointList;
                dtoSoldProductDetailNIP.NonInsuranceCoverage = dtoNonInsuranceCoverage;
                dtoSoldProductDetailIP.InsuranceCoverage = dtoInsuranceCoverage;
                dtoSoldProductDetailListIP.SoldProductDetail = dtoSoldProductDetailIP;
                dtoSoldProductDetailListNIP.SoldProductDetail = dtoSoldProductDetailNIP;
                dtoInsuranceProduct.SoldProductDetailList = dtoSoldProductDetailListIP;
                dtoNonInsuraneProduct.SoldProductDetailList = dtoSoldProductDetailListNIP;
                dtoSoldProduct.InsuranceProduct = dtoInsuranceProduct;
                dtoSoldProduct.NonInsuranceProduct = dtoNonInsuraneProduct;

                List<DemographicMBEPlusRequest_DTO_HUM.SoldProduct> dtoSoldProductLst = 
                    new List<DemographicMBEPlusRequest_DTO_HUM.SoldProduct>();
                dtoSoldProductLst.add(dtoSoldProduct);
                dtoSoldProductList.SoldProduct = dtoSoldProductLst;
                dtoMember.SoldProductList = dtoSoldProductList;

                dtoPlatPointList.PlatformProxyKey = '';
                dtoMember.PlatformPointerList = dtoPlatPointList;
                
                List<String> periodList = new List<String>();
                periodList.add(GLOBAL_CONSTANT_HUM.PCP_MEMCR_PERIOD_HUM);
                periodList.add(GLOBAL_CONSTANT_HUM.VOB_PAST_HUM);
                periodList.add(GLOBAL_CONSTANT_HUM.VOB_FUTURE_HUM);
                dtoPeriod.Period = periodList;

                dtoPersonIdCriteria.AsOfDate = 
                    Datetime.now().format(GLOBAL_CONSTANT_HUM.VOB_PCP_REFDATEFORMT_HUM);
                dtoPersonIdCriteria.PeriodList = dtoPeriod;
                dtoPersonIdCriteria.PersonId = personId;

                dtoGetCriteria.PersonIdCriteria = dtoPersonIdCriteria;

                dtoContactProfile.AddressList = dtoAddressList;
                dtoContactProfile.EmailList = dtoEmailList;
                dtoContactProfile.PhoneList = dtoPhoneList;
                
                dtoContact.ContactProfile = dtoContactProfile;

                dtoMember.Contact = dtoContact;

                dtoMember.GetCriteria = dtoGetCriteria;

                dtoGetMember.Member = dtoMember;

                dtoRequest.GetMember = dtoGetMember;

                String requestBody = JSON.serialize(dtoRequest);
                request.setBody(requestBody);
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'getMemberMBERequest');
            return null;
        }
    }

    /*
    * Method Name: generateHTTPRequestStructure
    * Description: method is used to generate HttpRequest for MBE Plus call w.r. to request DTO
    * Parameter: Enterprise Id from Account for Member
    * Return Type: HTTPRequest
    */
    private static HTTPRequest generateHTTPRequestStructure(string serviceName) {
        HttpRequest request = new HttpRequest();
        HUM_Webservice_Callout__c objMBEService;
        try {
            objMBEService = HUM_Webservice_Callout__c.getValues(serviceName);
            if(objMBEService != null) {
                request.setMethod(objMBEService.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(objMbeService.Certificate_Name__c))
                {
                    request.setClientCertificateName(objMbeService.Certificate_Name__c);
                }
                if (String.isNotBlank(objMbeService.Content_Type__c))
                {
                    request.setHeader(GLOBAL_CONSTANT_HUM.PCP_SER_CONTENTTYPE_HUM, 
                    objMbeService.Content_Type__c);
                }
                request.setHeader(GLOBAL_CONSTANT_HUM.PCP_SER_CONECTION_HUM, 
                GLOBAL_CONSTANT_HUM.PCP_SER_KEEPALIVE_HUM);
                request.setEndpoint(objMbeService.End_Point_URL__c);
                request.setHeader(GLOBAL_CONSTANT_HUM.PCP_SER_SOAPACTION_HUM, '');
                if(objMbeService.Timeout__c != null) {
                    request.setTimeout(Integer.valueOf(objMbeService.Timeout__c));
                }
            }
            return request;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, sClassName, 'generateHTTPRequestStructure');
            return null;
        }
    }
}