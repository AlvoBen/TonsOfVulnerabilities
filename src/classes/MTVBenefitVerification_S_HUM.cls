/*******************************************************************************************************************************
Apex Class Name :  MTVBenefitVerification_S_HUM.cls
Version         : 1.0
Created On      : 11/12/2019
Function        : Service Class for MTVBenefitVerification_C_HUM
Test Class      : MTVBenefitVerification_T_HUM

Modification Log: 
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula                                  11/12/2019                  Original Version
* Abhishek Mangutkar								 05/05/2021					 User Story 2230063 : Humana constants Cleanup Claims and MTV Claim Remarks  
* Ajay Chakradhar                                    05/31/2021                  REQ - 1755482 - Replacing constants to improve performance
*********************************************************************************************************************************/
public with sharing class MTVBenefitVerification_S_HUM
{
    public PBEServiceTypeV2DTO_HUM ServiceTypeDTO;
	Public Static final String  VOB_BOTH = 'Both'; 
	Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';	
	Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
	Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
	Public Static final String VOB_PBESERVICEDETAIL_HUM = 'ServicesWithPlaceOfService';
	Public Static final String  VOB_PLANCOMMENTS_HUM = 'PlanComments';
	Public Static final String VOB_PAR_DEFAULT_HUM = 'Par/Non Par/Both';
	Public Static final String VOB_PARCODE_DEFAULT_HUM = 'Par Code';
	Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
	Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
	Public Static final String  VOB_RIDERS_HUM = 'Riders';
	Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail'; 
	
   /*
    * Method Name   :  builtHttpRequest
    * Description   :  This Method is used to build HttpRequest
    * Return Type   :  HttpRequest
    * Parameters    :  HUM_Webservice_Callout__c
    */    
    public static  HttpRequest builtHttpRequest(HUM_Webservice_Callout__c ServiceObj)
    {
        HttpRequest request = new HttpRequest();
        try
        {
            if (ServiceObj != null && ServiceObj.Active__c)
            {
                request.setMethod(ServiceObj.Request_Type__c);
                if (!Test.isRunningTest() && String.isNotBlank(ServiceObj.Certificate_Name__c)) request.setClientCertificateName(ServiceObj.Certificate_Name__c);
                if (String.isNotBlank(ServiceObj.Content_Type__c)) request.setHeader(GlobalCommonConstants_HUM.CONTENT_TYPE_HUM, ServiceObj.Content_Type__c);
                request.setHeader(GlobalCommonConstants_HUM.CONNECTION_HUM, GlobalCommonConstants_HUM.KEEP_ALIVE_HUM);
                request.setEndpoint(ServiceObj.End_Point_URL__c);
                request.setHeader(GlobalCommonConstants_HUM.SOAPACTION_HUM, '');
                if (ServiceObj.Timeout__c != null) request.setTimeout(Integer.valueOf(ServiceObj.Timeout__c));
            }
            return request;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'MTVBenefitVerification_S_HUM', 'builtHttpRequest');
            return Null;
        }
    }

     /*
    * Method Name   :  RequestStructurePBEService
    * Description   :  This Method is used to prepare request for PBE service
    * Return Type   :  ProductBusinessEntityRequestV2_H_HUM
    * Parameters    :  String    As Of Date 
    * Parameters    :  String    MTV Benefit Package Id
    */      
    public  ProductBusinessEntityRequestV2_H_HUM RequestStructurePBEService(string sAsOfDate, string sMTVBenefitPkgId)
    {
        try
        {
            ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
            ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
            productNode.SubVersion = '2';
            String sBenefitAsofDate = sAsOfDate.split('/')[2] + '-' + sAsOfDate.split('/')[0] + '-' + sAsOfDate.split('/')[1];
            productNode.AsOfDate = sBenefitAsofDate; 
            ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
            PrId.ProductId = sMTVBenefitPkgId; 
            PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
            productNode.ProductIdentifier = PrId;
            ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail addDetails = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
            addDetails.MasterServicesWithPOS = 'true';
            addDetails.MasterServicesWithParCode = 'true';
            addDetails.MasterServicesWithAdaCode = 'true';
            productNode.IncludeAdditionalDetail = addDetails;
            ProductBusinessEntityRequestV2_H_HUM.IncludeList IncNode = new ProductBusinessEntityRequestV2_H_HUM.IncludeList();
            list<String> lstIncludeTypes = new list<String>{ VOB_GENERALBENEFITS_HUM, VOB_MASTERSERVICES_HUM, VOB_RIDERS_HUM, VOB_RXDETAIL_HUM };
            IncNode.IncludeType = lstIncludeTypes;
            productNode.IncludeList = IncNode;
            ProductBusinessEntityRequestV2_H_HUM.ICDCodeList icdCode = new ProductBusinessEntityRequestV2_H_HUM.ICDCodeList();
            icdCode.ICDCode = new List<string>();
            icdCode.ICDCode.add('?');
            productNode.ICDCodeList = icdCode;
            oRequest.GetProduct = productNode;
            return oRequest;
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'MTVBenefitVerification_S_HUM', 'RequestStructurePBEService');
            return NULL;
        }
    }

   /*
    * Method Name   :  serializePBEResponse
    * Description   :  This Method is used to serialize the response received from PBE service
    * Return Type   :  string
    * Parameters    :  MTVBenefitVerification_C_HUM instance of benefit class 
    * Parameters    :  PBEResponseV2_DTO_HUM DTOobj
    */    
    public string serializePBEResponse(MTVBenefitVerification_C_HUM benefitsCls,PBEResponseV2_DTO_HUM DTOobj)
    {
        string vPBEServiceRes;
        Map<String, String> mapServiceCategoryName;
        List<ProductBusinessEntityResponseV2_H_HUM.ServiceType> sameServiceCategoryTypeList;
        Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>> mapServiceCategory;
        mapServiceCategory = new Map<string, List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>>();
        mapServiceCategoryName = new Map<String,String>();
        if (DTOobj != null && DTOobj.ServiceCategoryList != null && DTOobj.ServiceCategoryList.ServiceCategory != null && DTOobj.ServiceCategoryList.ServiceCategory.size() > 0 && DTOobj.ServiceTypeList != null && DTOobj.ServiceTypeList.ServiceType != null)
        {
            String sCodeVal;
            for (ProductBusinessEntityResponseV2_H_HUM.ServiceCategory serviceCatObj : DTOobj.ServiceCategoryList.ServiceCategory)
            {
                sameServiceCategoryTypeList = new List<ProductBusinessEntityResponseV2_H_HUM.ServiceType>();
                if(serviceCatObj.CodeList.Code !=null) {
                    sCodeVal = '';
                    for(String sCode : serviceCatObj.CodeList.Code)    {
                        if(sCodeVal != '')    {
                            sCodeVal = sCodeVal + ','+sCode;
                        }
                        else    {
                            sCodeVal = sCode;
                        }
                    }
                }
                for (ProductBusinessEntityResponseV2_H_HUM.ServiceType serviceTypeObj : DTOobj.ServiceTypeList.ServiceType)
                {
                    if (serviceCatObj.CodeList.Code.contains(serviceTypeObj.ServiceCategoryCode)) {
                        sameServiceCategoryTypeList.add(serviceTypeObj);
                    }
                }
                mapServiceCategory.put(sCodeVal, sameServiceCategoryTypeList);
                mapServiceCategoryName.put(serviceCatObj.Name,sCodeVal);
            }    
        }
        if (mapServiceCategory != null && mapServiceCategory.size() > 0)
        {
            ServiceTypeDTO = new PBEServiceTypeV2DTO_HUM();
            ServiceTypeDTO.mapServiceCatServicetype = mapServiceCategory;
            ServiceTypeDTO.mapServiceCatCode = mapServiceCategoryName;
            benefitsCls.ServiceTypeDTOStr = JSON.serialize(ServiceTypeDTO).escapeEcmaScript();
        }
        vPBEServiceRes = JSON.serialize(DTOobj);
        if (string.isNotBlank(vPBEServiceRes)) vPBEServiceRes = vPBEServiceRes.escapeEcmaScript();
        return vPBEServiceRes;
    }
    
   /*
    * Method Name   :  RequestStructureBenefitSearch
    * Description   :  This Method is used to frame request for benefit search
    * Return Type   :  ProductBusinessEntityRequestV2_H_HUM
    * Parameter : String    Service Category Id
    * Parameter : String    Type Of Service
    * Parameter : String    Place of Service
    * Parameter : String    Par /non Par
    * Parameter : String    Platform Code
    * Parameter : String    Product Key
    * Parameter : String    Ref Date
    * Parameter : Boolean   is CAS
    * Parameter : String    ADA Code
    */  
    public static ProductBusinessEntityRequestV2_H_HUM RequestStructureBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate,Boolean isCAS, string ADACode)
    {

        ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
        ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
        productNode.SubVersion = '2';
        productNode.AsOfDate = refdate.substring(6, 10) + '-' + refdate.substring(0, 2) + '-' + refdate.substring(3, 5);
        ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
        PrId.ProductId = productKey;
        if (platformCode == VOB_PLATFORM_EM_HUM)
        {
            PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
        }
        else if (platformCode == VOB_PLATFORM_LV_HUM)
        {
            PrId.ProductIdType = GLOBAL_CONSTANT_HUM.VOB_VALUEPDQ_HUM;
        }
        productNode.ProductIdentifier = PrId;
        ProductBusinessEntityRequestV2_H_HUM.IncludeList IncNode = new ProductBusinessEntityRequestV2_H_HUM.IncludeList();
        list<String> lstIncludeTypes = new list<String>{ VOB_PBESERVICEDETAIL_HUM,VOB_PLANCOMMENTS_HUM };
        IncNode.IncludeType = lstIncludeTypes;
        productNode.IncludeList = IncNode;

        ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail oIncAddDtl = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
        oIncAddDtl.NonCoveredBenefit = 'true';
        oIncAddDtl.MasterServicesWithAdaCode = 'true';
        productNode.IncludeAdditionalDetail = oIncAddDtl;

        ProductBusinessEntityRequestV2_H_HUM.ICDCodeList icdCode = new ProductBusinessEntityRequestV2_H_HUM.ICDCodeList();
        icdCode.ICDCode = new List<string>();
        icdCode.ICDCode.add('?');
        productNode.ICDCodeList = icdCode;
        ProductBusinessEntityRequestV2_H_HUM.ServiceDetail serviceDetail = new ProductBusinessEntityRequestV2_H_HUM.ServiceDetail();
        serviceDetail.ServiceCategoryCode = sServiceCategory;
        serviceDetail.ServiceTypeId = sTypeOfService;
        serviceDetail.PlaceOfServiceCode = sPlaceOfService;
        serviceDetail.AdaCode = '';
        if (!isCAS)
        {
            serviceDetail.Par = (String.isNotblank(sPar) && sPar != VOB_PAR_DEFAULT_HUM) ? sPar : VOB_BOTH;
        }
        else
        {
            serviceDetail.Par = VOB_BOTH;
            serviceDetail.ParCode = (String.isNotblank(sPar) && sPar != VOB_PARCODE_DEFAULT_HUM) ? sPar : '';
        }
        productNode.ServiceDetail = serviceDetail;
        oRequest.GetProduct = productNode;
        return oRequest;
    }
}