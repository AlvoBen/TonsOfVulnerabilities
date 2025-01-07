/*******************************************************************************************************************************
Apex Class Name : PolicyMemberDetail_D_HUM.cls
Version         : 1.0
Created On      : 2017
Function        : data layer for PolicyMemberDetail_VF_HUM Page

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------

* Rajesh Keswani             118189                   07/31/2017                 REQ - 305662  Original version
* Anjali Kumar                                        08/28/2018                 REQ - 373406 Replace PBE1.2 with PBE1.3 in Policy Member Detail Page
* Firoja Begam                                        10/28/2020                 US-1279281- PR00095366 - MF 1 - new fields in policy details section (member policy page)
* Firoja Begam                                        11/4/2020                  1661723 -- PR00095366 Accolade - SF - TECH - Vendor details display only for GBO products
* Ajay Chakradhar                           		  05/05/2021	             REQ - 2184294 - Replacing constants with Consumer Management Constants
*****************************************************************************************************************************************/
public with sharing class PolicyMemberDetail_D_HUM
{
    Private Static String HYPHEN_HUM = '-';
    Private Static String BACKSLASH_HUM = '/';

    /*
     * Returns ProductBusinessEntityRequest_H_HUM request
     * <p>
     * Method to prepare PBE request
     *
     * @param  PBERequestDTO PolicyMemberDetail_DTO_HUM Class Instance .
     * @return ProductBusinessEntityRequest_H_HUM Request structure for PBE
     */
    public static ProductBusinessEntityRequestV2_H_HUM RequestStructurePBEService(PolicyMemberDetail_DTO_HUM PBERequestDTO)
    {   
        Policy_Plan_Details__c policyPlanObject = getPolicyPlan_SOQL(PBERequestDTO.policyID, PBERequestDTO.status, PBERequestDTO.endDate, PBERequestDTO.refDateFromURL);
        ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
        ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
        productNode.AsOfDate = populateAsOfDate(PBERequestDTO.status, PBERequestDTO.endDate, PBERequestDTO.refDateFromURL);
        ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
        if(policyPlanObject != null && string.isNotBlank(policyPlanObject.Product__r.Sold_Product_Key_Value__c)) PrId.ProductId = policyPlanObject.Product__r.Sold_Product_Key_Value__c;
        else PrId.ProductId = PBERequestDTO.soldProductKeyValue;
        
         if (PBERequestDTO.platformCode == ConsumerManagementConstants_HUM.VOB_PLATFORM_EM_HUM)
         {
            PrId.ProductIdType = ConsumerManagementConstants_HUM.VOB_BENEFITPACKAGEID_HUM;
         }
         else if (PBERequestDTO.platformCode == ConsumerManagementConstants_HUM.VOB_PLATFORM_LV_HUM)
         {
           PrId.ProductIdType = ConsumerManagementConstants_HUM.VOB_VALUEPDQ_HUM;
         }
         
        productNode.ProductIdentifier = PrId;
        ProductBusinessEntityRequestV2_H_HUM.IncludeList IncNode = new ProductBusinessEntityRequestV2_H_HUM.IncludeList();
        IncNode.IncludeType = new List<string>();
		IncNode.IncludeType.add('SupplementalRider');
        productNode.IncludeList = IncNode;
        ProductBusinessEntityRequestV2_H_HUM.ICDCodeList icdCode = new ProductBusinessEntityRequestV2_H_HUM.ICDCodeList();
		icdCode.ICDCode = new List<string>();
		icdCode.ICDCode.add('?');
		productNode.ICDCodeList = icdCode;
        oRequest.GetProduct = productNode;
        return oRequest;
    }

    /*
     * Method to get As Of Date on basis on Policy Member End Date
     * @param  String EndDate of Policy Member .
     * @return String
     */
    private static String populateAsOfDate(string status, string endDate, string refDate)
    {
        //Date Formatting Criteria
        
        if (string.isNotBlank(refDate))
        {
            string s = refDate;
            return s.substring(4, 8) + HYPHEN_HUM + s.substring(0, 2) + HYPHEN_HUM + s.substring(2, 4);
        }
        else if (status != NULL && status != ConsumerManagementConstants_HUM.VOB_ACTIVEMEMBERSTATUS_HUM && endDate != NULL)
        {
            return endDate.split(BACKSLASH_HUM)[2] + HYPHEN_HUM + endDate.split(BACKSLASH_HUM)[0] + HYPHEN_HUM + endDate.split(BACKSLASH_HUM)[1];
        }
        else
        {
            return string.valueof(date.today());
        }
    }

    /**
     * Returns Policy_Plan_Details__c object that can then be utilized for showing Benefits Information.     *
     * This method returns SOQL result of policy member.
     *
     * @param   policyId string
     * @param   status  string
     * @param   endDate string
     * @return  Policy_Plan_Details__c    return the policy plan detail object
     */
    public static Policy_Plan_Details__c getPolicyPlan_SOQL(string policyId, string status, string endDate, string refDate)
    {

        List < Policy_Plan_Details__c > objPolicyPlan = [Select Id, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Product__r.Sold_Product_Key_Value__c
            FROM Policy_Plan_Details__c WHERE Policy__c =: policyId AND ETL_Record_Deleted__c = false];
        if (!objPolicyPlan.isEmpty())
        {
            if (objPolicyPlan.size() == 1)
            {
                return objPolicyPlan[0];
            }
            else
            {
                String asOfDateUnformatted = populateAsOfDate(status, endDate, refDate);
                String sAsofDate = asOfDateUnformatted.split(HYPHEN_HUM)[1] + BACKSLASH_HUM + asOfDateUnformatted.split(HYPHEN_HUM)[2] + BACKSLASH_HUM + asOfDateUnformatted.split(HYPHEN_HUM)[0];
                for (Policy_Plan_Details__c objPlan: objPolicyPlan)
                {
                    if (ConsumerManagementConstants_HUM.MAX_DATE_HUM == objPlan.Coverage_Plan_End_Date__c || (ConsumerManagementConstants_HUM.MAX_DATE_HUM != objPlan.Coverage_Plan_End_Date__c &&
                            (Date.parse(objPlan.Coverage_Plan_Effective_Date__c) <= Date.Parse(sAsofDate) && Date.parse(sAsofDate) <= Date.parse(objPlan.Coverage_Plan_End_Date__c))))
                    {
                        return objPlan;
                    }
                }
            }

        }
        return null;
    }

    /*
     * Method to PBE response from service
     * @param  oResponsePBE instance of  ProductBusinessEntityResponse_H_HUM .
     * @return Map<string,string>
     */
    public static Map<string,string> parsePBE(ProductBusinessEntityResponseV2_H_HUM oResponsePBE)
    {
        string PbeResponse;
        Map<string,string> PbeMap;
        if (oResponsePBE != null && oResponsePBE.GetProductResponse != null)
        {
            ProductBusinessEntityResponseV2_H_HUM.GetProductResponse objGetProductResponse = oResponsePBE.GetProductResponse;
            if (objGetProductResponse != null)
            {
                ProductBusinessEntityResponseV2_H_HUM.ProductList objProductList = objGetProductResponse.ProductList;
                if (objProductList != null && objProductList.Product != null && objProductList.Product.size() > 0)
                {
                    ProductBusinessEntityResponseV2_H_HUM.Product objProduct = objProductList.Product[0];
                    if (objProduct != null)
                    {
                        ProductBusinessEntityResponseV2_H_HUM.Detail objDetail = objProduct.Detail;
                        if (objDetail != null)
                        {
                            ProductBusinessEntityResponseV2_H_HUM.SupplementalRiderList supplementalRiderListObj = objDetail.SupplementalRiderList;
                            if (supplementalRiderListObj != null && supplementalRiderListObj.SupplementalRider != null && supplementalRiderListObj.SupplementalRider.size() > 0)
                            {
                                ProductBusinessEntityResponseV2_H_HUM.SupplementalRider supplementRiderObject = supplementalRiderListObj.SupplementalRider[0];
                                PbeMap = new Map<string,string>();
                                if (string.isNotBlank(supplementRiderObject.IsCarveOut))
                                {
                                     if('true'.equalsIgnoreCase(supplementRiderObject.IsCarveOut)) PbeMap.put('isCarvedOut','Y');
                                     else PbeMap.put('isCarvedOut','N');
                                }
                                if (string.isNotBlank(supplementRiderObject.IsIntegratedBenefit) && string.isNotBlank(supplementRiderObject.IsCarveOut) )
                                {
                                     if('true'.equalsIgnoreCase(supplementRiderObject.IsIntegratedBenefit)) PbeMap.put('MedRX','Y');
                                     else PbeMap.put('MedRX','N');
                                }
                                if (string.isNotBlank(supplementRiderObject.VendorId) && string.isNotBlank(supplementRiderObject.IsCarveOut))
                                {
                                     PbeMap.put('carvedOutTo',supplementRiderObject.VendorId);
                                }
                            }

                        }
                    }
                }
            }
        }
        return PbeMap;
    }
     /*
    *  This method  is used to return details of a policy member
    *  @name   getPolicyMemberVendor
    *  @param  ID - Record ID of Policy Member 
    *  @return List<Policy_Member__c> -  List of Policy member records
    *  @throws getPolicyMemberVendor
    */
    public static Policy_Member__c getPolicyMemberVendor(ID sRecId)
    {   
        Policy_Member__c lstpolmem = new Policy_Member__c();
        try
        {
            if(sRecId != null && String.isNotBlank(sRecId))
            {
                lstpolmem = [select id, Effective_Date__c, End_Date__c, Product__c, Product_Type__c, Policy_Platform__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Policy__r.Platform__c, Policy__r.Source_Cust_Cov_Key__c,
                             Group_Number__c,Policy__r.Last_GBE_Call_Date__c,Policy__c,Policy__r.Group_Name__c from Policy_Member__c where id=:sRecId];
            }
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PolicyMemberDetail_D_HUM', 'getPolicyMemberVendor');
        }
        return lstpolmem;
    } 
     /*
    *  This method  is used to return details of a policy PLAN
    *  @name   getPolicyPlanVendor
    *  @param  Policy Member 
    *  @return List<Policy_Plan_Details__c> -  List of Policy pLAN records
    *  @throws getPolicyPlanVendor
    */
    public static List<Policy_Plan_Details__c> getPolicyPlanVendor(Policy_Member__c policyMember)
    {   
        List<Policy_Plan_Details__c> lstpolplan = new List<Policy_Plan_Details__c>();
        try
        {
            if(policyMember != null)
            {
                lstpolplan = [select id, Exchange__c, Business_Segment__c, Exchange_Indicator__c, Exchange_Type__c, Metallic_Tier__c, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Product__r.Sold_Product_Key_Value__c, Policy__r.Group_Name__r.Source_Customer_Key__c, Platform__c,Source_Cust_Cov_Key__c,
                              Group_Number__c, Policy__r.Last_GBE_Call_Date__c, Policy__c,Policy__r.Group_Name__c, Last_GBE_Call_Date__c from Policy_Plan_Details__c where Policy__c =: policyMember.policy__c and ETL_Record_Deleted__c=false ];
            }
        }  
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'PolicyMemberDetail_D_HUM', 'getPolicyPlanVendor');
        }
        return lstpolplan;
    } 
}