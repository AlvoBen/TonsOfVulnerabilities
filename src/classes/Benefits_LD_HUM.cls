/**************************************************************************** 
Apex Class Name  : Benefits_LD_HUM
Version          : 1.0  
Created Date     : 3/17/2022
Test class       : Benefits_LT_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Aishwarya Pawar                               3/17/2022                   Original Version - US 3017464
* Swapnali Sonawane                             3/17/2022                   US- 3017787
* Kalyani Pachpol                               3/17/2022                   US- 3149503
* Swapnali Sonawane                             08/23/2022                  US#3631288 Use purchaser plan object
* Suraj Patil                                   05/08/2023                  US#4542585 Dental Plan - Tooth History
* Anusha Chindanoor                             07/19/2023                  US: 4785118 Critical- Lightning - Benefits                 
***************************************************************************************************/
public with sharing class Benefits_LD_HUM {
    Public Static final String HYPHEN_HUM = '-';
    Public Static final String BACKSLASH_HUM = '/';   
    Public Static final String  VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';//
    //PBE
    Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String  VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
    Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
    Public Static final String  VOB_RIDERS_HUM = 'Riders';
    Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail';

    //MBE
    Public Static final String  VOB_CURRENT_HUM = 'Current';
    Public Static final String  VOB_PAST_HUM = 'Past';
    Public Static final String  VOB_FUTURE_HUM = 'Future';
    Public Static final String  VOB_GENKEY_KEYTYPE = 'ODS_GENKEY';   
    Public static final string PaidThroughDate = 'PaidThruDate';
    Public static final string MAX_NEW_DATE = '12/31/4000';
    Public static final string MAX_OLD_DATE = '12/31/9999';
    
    //billing 
    Public Static final String PAIDTHROUGHDATE_NUMBEROFTRANSACTIONS_HUM = '1';
    Public Static final String PAIDTHROUGHDATE_RATELEVEL_HUM = 'Account';
    Public Static final String PAIDTHROUGHDATE_IDTYPE_HUM = 'CONTRID';
    public static MemberPlan objMemberPlan;
    public static Policy_Plan_Details__c objPolicyPlan;

    //Mentor
    Public Static final String sMTV_Value = 'MTV';
    Public Static final String sLV_Value= 'LV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String sEM_Value= 'EM';
    Public Static final String SUBGROUP_MENTORREDIRECT_HUM = 'subgroup:';
    Public Static final String HUM_PARTD_DRUGREQUESTOR = 'CRM';
    Public Static final String sHiphan = '-';
    Public Static final String MED = 'MED';
    Public Static final String  VOB_MEDICARE ='RSO- Medicare';
    
    //Benefit Search    
    Public Static final String VOB_PBESERVICEDETAIL_HUM = 'ServicesWithPlaceOfService';
    Public Static final String  VOB_PLANCOMMENTS_HUM = 'PlanComments';
    Public Static final String VOB_PAR_DEFAULT_HUM = 'Par/Non Par/Both';
    Public Static final String  VOB_BOTH = 'Both'; 
    Public Static final String VOB_PARCODE_DEFAULT_HUM = 'Par Code';
    
    Public Static final String VOB_BENEFITSEARCH_SERVICELIST_HUM = 'Service';
    //updatebenSearchResArray
    Public Static final String VOB_PAR_YES_HUM = 'Yes';
    Public Static final String VOB_BENEFITSEARCH_INNETWORK_HUM = 'InNetwork';
    Public Static final String VOB_BENEFITSEARCH_OUTNETWORK_HUM = 'OutOfNetwork';
    Public Static final String VOB_PAR_NO_HUM = 'No';
    //getFormattedValue method
    Public Static final String VOB_ACCUMS_DOLLAR_SIGN_HUM = '$';
    Public Static final String VOB_BENEFITSEARCH_PERCENTAGE_HUM = 'Percentage';
    Public Static final String VOB_ACCUMS_PERCENTAGE_SIGN_HUM = '%';
    Public Static final String VOB_BENEFITSEARCH_DOLLAR_HUM = 'Dollar';
    
    Public Static final String  VOB_BENEFITS = 'BENEFITS';
    
    Public static Boolean bValidNodeFound = false;
    Public static PBEBenefitsSearch_DTO_HUM benefitsSearchDTOObj = new PBEBenefitsSearch_DTO_HUM();
    
    /**
    * Returns Policy_Member__C object that can then be utilized for showing Benefits Information.
    * This method returns SOQL result of policy member.
    */
    @AuraEnabled ( cacheable=true)
    public static void getPlanDetails(string memberPlanId){
    try
        {
            objMemberPlan = [Select Id,EffectiveFrom,EffectiveTo,Policy__c,Member_Coverage_Status__c, Source_Coverage_ID__c, plan.Product__r.Name, planid, Status, plan.Product__r.ProductCode, plan.Source_Major_LOB__c,plan.Major_LOB__c, Policy__r.Major_LOB_Frm__c, Policy__r.Major_LOB__c,
                Plan.Payer.Source_Platform_Code__c, Plan.Source_Cust_Cov_Key__c,Plan.EffectiveFrom, Plan.EffectiveTo,
                plan.Benefit_Coverage__c, Plan.Purchaser_Plan_External_ID__c, Member.Enterprise_Id__c, Plan.Product__r.Sold_Product_Key_Value__c,plan.Platform__c, plan.Contract_Number__c, plan.PBP_Code__c, Product__c, Product_Type__c, ASO__c,GroupNumber,Product_Type_Code__c, Exchange_type__c
                From MemberPlan where Id= :memberPlanId with USER_MODE];
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Benefits_LD_HUM', 'getPlanMember_SOQL');
            objMemberPlan = null;

        }
    }

    // returns Policy_Plan_Details__c
    public static void getPolicyPlan(string asOfDate)
    {
        try
        {
            List<Policy_Plan_Details__c> lstPolicyPlan = [Select Id , Benefit_Coverage__c, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c , Product__r.Sold_Product_Key_Value__c             
                                                       FROM Policy_Plan_Details__c WHERE Policy__c = :objMemberPlan.Policy__c AND ETL_Record_Deleted__c = false  with USER_MODE] ;
           
            if(!lstPolicyPlan.isEmpty())
            {
				if(lstPolicyPlan.size() == 1)
				{
                    objPolicyPlan =  lstPolicyPlan[0];
				}
				else 
				{
                    String asOfDateUnformatted = populateAsOfDate(asOfDate);
                    String sAsofDate = asOfDateUnformatted.split(GLOBAL_CONSTANT_HUM.HYPHEN_HUM)[1] + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + asOfDateUnformatted.split(GLOBAL_CONSTANT_HUM.HYPHEN_HUM)[2] + GLOBAL_CONSTANT_HUM.BACKSLASH_HUM + asOfDateUnformatted.split(GLOBAL_CONSTANT_HUM.HYPHEN_HUM)[0];
                    for(Policy_Plan_Details__c objPlan : lstPolicyPlan)
                    {
                        if(GLOBAL_CONSTANT_HUM.MAX_DATE_HUM == objPlan.Coverage_Plan_End_Date__c || ( GLOBAL_CONSTANT_HUM.MAX_DATE_HUM != objPlan.Coverage_Plan_End_Date__c  &&
                        (Date.parse(objPlan.Coverage_Plan_Effective_Date__c) <=  Date.Parse(sAsofDate)  && Date.parse(sAsofDate)  <= Date.parse(objPlan.Coverage_Plan_End_Date__c))))
						{
							objPolicyPlan =  objPlan;
                    }
				}
            }
			
			}
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'Benefits_LD_HUM', 'getPolicyPlan_SOQL');
            objMemberPlan =  NULL;
        }
    }

    
    /*
    * Method to get As Of Date on basis on Policy Member End Date
    * @param  String EndDate of Policy Member .
    * @return String
    */

    private static String populateAsOfDate(string refDate)
    {
		if(refDate != '' )
		{
			return refdate;
		}
		else if (objMemberPlan <> NULL && objMemberPlan.Member_Coverage_Status__c  <> NULL && objMemberPlan.Member_Coverage_Status__c  <> VOB_ACTIVEMEMBERSTATUS_HUM && objMemberPlan.EffectiveTo <> NULL)
		{
			return  string.valueOf(objMemberPlan.EffectiveTo);
		}
		else
		{
			return string.valueof(date.today());
		}
    }  
    
    /*
    * Returns ProductBusinessEntityRequestV2_H_HUM request
    * Method to prepare PBE request.
    * @return ProductBusinessEntityRequestV2_H_HUM Request structure for PBE
    */
    public static ProductBusinessEntityRequestV2_H_HUM RequestStructurePBEService(String memberPlanId, String asOfDate)
    {
        try
        {
            getPlanDetails(memberPlanId);
            getPolicyPlan(asOfDate);
            ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
            ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
            productNode.SubVersion = '3';   
            productNode.AsOfDate = populateAsOfDate(asOfDate);
            ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
            PrId.ProductId =  objPolicyPlan != null && objPolicyPlan.Product__r.Sold_Product_Key_Value__c != NULL ? objPolicyPlan.Product__r.Sold_Product_Key_Value__c :objMemberPlan.Plan.Payer.Source_Platform_Code__c;
	    if (objMemberPlan.Plan.Payer.Source_Platform_Code__c == VOB_PLATFORM_EM_HUM)
            {
                PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
            }
            else if (objMemberPlan.Plan.Payer.Source_Platform_Code__c == VOB_PLATFORM_LV_HUM)
            {
                PrId.ProductIdType = VOB_VALUEPDQ_HUM;
            }
            
            productNode.ProductIdentifier = PrId;
            ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail addDetails = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
            addDetails.MasterServicesWithPOS = 'true';
            addDetails.MasterServicesWithParCode = 'true';
            if (string.isNotBlank(objMemberPlan.Plan.Product__r.ProductCode) && objMemberPlan.Plan.Product__r.productcode.toLowerCase() == 'den')
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
            HUMExceptionHelper.logErrors(ex, 'Benefits_LD_HUM', 'RequestStructurePBEService');
            return NULL;
        }
    }
   
    
    /*
    * Returns PBEResponseV2_DTO_HUM object
    * <p>
    * Method to prepare PBE DTO to send to UI
    *
    * @param  ProductBusinessEntityResponseV2_H_HUM object.
    * @return PBEResponseV2_DTO_HUM
    */
	public static PBEResponseV2_DTO_HUM getPBEDTOData(ProductBusinessEntityResponseV2_H_HUM oResponsePBE)
    {
		PBEResponseV2_DTO_HUM PBEDtoObj = NULL;
		try
		{
			if (oResponsePBE != null)
			{
				ProductBusinessEntityResponseV2_H_HUM.GetProductResponse objGetProductResponse = oResponsePBE.GetProductResponse;
				ProductBusinessEntityResponseV2_H_HUM.ProductList objProductList = (objGetProductResponse != null)?objGetProductResponse.ProductList:null;
				ProductBusinessEntityResponseV2_H_HUM.Product objProduct =  (objProductList != null && objProductList.Product != null && objProductList.Product.size() > 0)?objProductList.Product[0]:null;
                
                if (objProduct != null)
                {
                    ProductBusinessEntityResponseV2_H_HUM.Detail objDetail = objProduct.Detail;    
                    PBEDtoObj = parsePBEData(objDetail, objProduct.MetaData.PlatformCode);
                    ProductBusinessEntityResponseV2_H_HUM.Definition objDefinition = objProduct.Definition;
                    PBEDtoObj.planOption = (objDefinition != null && string.isNotBlank(objDefinition.Description)) ?getPlanOption(objDefinition,objProduct):null;
                    // We are fetching PlanId on the basis of ProductIDType as "PdbPlanId"
                    List<ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier> lstProductIdentifier = (objProduct.Definition != null && objProduct.Definition.IdentifierList != null && objProduct.Definition.IdentifierList.ProductIdentifier != null)?objProduct.Definition.IdentifierList.ProductIdentifier:null;           
                    for(ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier objProductIdentifier : lstProductIdentifier )
                    {
                        PBEDtoObj.pbePlanId=(string.isNotBlank(objProductIdentifier.ProductIdType) && 
                                            objProductIdentifier.ProductIdType.equalsIgnoreCase('PdbPlanId'))&&
                                            string.isNotBlank(objProductIdentifier.ProductId) ? objProductIdentifier.ProductId : '';
                    }					      
                }
			}
			return PBEDtoObj;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'Benefits_LD_HUM', 'getPBEDTOData');
			return NULL;
		}
    }  
    
    public static string getPlanOption(ProductBusinessEntityResponseV2_H_HUM.Definition objDefinition,ProductBusinessEntityResponseV2_H_HUM.product objProduct)
    {
        PBEResponseV2_DTO_HUM PBEDtoObj = new PBEResponseV2_DTO_HUM();
        if(objDefinition.Description == 'DENTAL')
        {                                        
            List<ProductBusinessEntityResponseV2_H_HUM.AlternateName> lstAltName = objProduct.Definition.AlternateNameList.AlternateName;
            if(lstAltName != NULL && lstAltName.size()>0)
            {
                for(ProductBusinessEntityResponseV2_H_HUM.AlternateName objAltName : lstAltName)
                {
                    if(objAltName.NameType == 'ProductBuildName')
                    PBEDtoObj.planOption = objAltName.Name;
                }
            }
        }
        else
        {
            PBEDtoObj.planOption = objDefinition.Description;
        }
        return PBEDtoObj.planOption; 
    }
    /*
    * Returns PBEResponseV2_DTO_HUM object
    * <p>
    * Method to parse and prepare PBE DTO to send to UI
    *
    * @param  ProductBusinessEntityResponseV2_H_HUM.Detail object.
    * @return PBEResponseV2_DTO_HUM
    */
    @TestVisible
    private static PBEResponseV2_DTO_HUM parsePBEData(ProductBusinessEntityResponseV2_H_HUM.Detail objDetail, String platformCode)
    {
        PBEResponseV2_DTO_HUM PBEDtoObj = new PBEResponseV2_DTO_HUM();
        PBEDtoObj.PlatformCode = (PlatformCode != null)? platformCode:null;
        PBEDtoObj.PackageInfo = (objDetail.PackageInfo != null)?objDetail.PackageInfo:null;
        PBEDtoObj.ServiceTypeList = (objDetail.ServiceTypeList != null)?objDetail.ServiceTypeList:null;
        PBEDtoObj.ServiceCategoryList = (objDetail.ServiceCategoryList != null)?objDetail.ServiceCategoryList:null;
        PBEDtoObj.PlaceOfServiceList = (objDetail.PlaceOfServiceList != null)? objDetail.PlaceOfServiceList:null;
        PBEDtoObj.ParcodeList = (objDetail.ParcodeList != null)?objDetail.ParcodeList:null;
        PBEDtoObj.RxDetail = (objDetail.RxDetail != null)?objDetail.RxDetail:null;
        PBEDtoObj.CauseCodeList =(objDetail.CauseCodeList != null) ? objDetail.CauseCodeList:null;

        ProductBusinessEntityResponseV2_H_HUM.GeneralBenefits objGeneralBenefits = objDetail.GeneralBenefits;
        PBEDtoObj.RiderList = (objDetail.RiderList != null)? objDetail.RiderList:null;
        if (objGeneralBenefits != null)
        {
            PBEDtoObj.Deductible = (objGeneralBenefits.Deductible != null)? objGeneralBenefits.Deductible:null;
            PBEDtoObj.OutOfPocket = (objGeneralBenefits.OutOfPocket != null)?objGeneralBenefits.OutOfPocket:null;
            PBEDtoObj.MaxOutOfPocket = (objGeneralBenefits.MaxOutOfPocket != null)? objGeneralBenefits.MaxOutOfPocket:null;
            PBEDtoObj.BenefitIndicatorList = (objGeneralBenefits.BenefitIndicatorList != null)?objGeneralBenefits.BenefitIndicatorList:null;
            PBEDtoObj.IsHDHP =(string.isNotBlank(objGeneralBenefits.IsHDHP)) ? objGeneralBenefits.IsHDHP:null;
            PBEDtoObj.CoInsurance = (objGeneralBenefits.CoInsurance != null)?objGeneralBenefits.CoInsurance:null;
            PBEDtoObj.SuperMoop = (objGeneralBenefits.SuperMoop != null)?objGeneralBenefits.SuperMoop:null;
            PBEDtoObj.CopayOfficeVisit = (objGeneralBenefits.CopayOfficeVisit != null) ?objGeneralBenefits.CopayOfficeVisit:null;
            PBEDtoObj.Limitation = (objGeneralBenefits.Limitation != null)?objGeneralBenefits.Limitation:null;
        }
        return PBEDtoObj;
    }

    /*
    * Returns GBEServiceRequest_H_HUM request
    * <p>
    * Method to prepare GBEServiceRequest_H_HUM request.
    * @return GBEServiceRequest_H_HUM Request structure for PBE
    */
	
    public static GBEServiceRequest_H_HUM RequestStructureGBEService(String memberPlanId, String asOfDate)    
    {
        try
        {
            getPlanDetails(memberPlanId);
			getPolicyPlan(asOfDate);
			GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
			string refDatePopulated = populateAsOfDate(asOfDate);
			string refDateParam = refDatePopulated <> null ? refDatePopulated.split(HYPHEN_HUM)[1] + BACKSLASH_HUM + refDatePopulated.split(HYPHEN_HUM)[2] + BACKSLASH_HUM + refDatePopulated.split(HYPHEN_HUM)[0] : '';
			if (objMemberPlan != null && objMemberPlan.planid != null && objMemberPlan.Plan.Payer != null && String.isNotBlank(objMemberPlan.Plan.Payer.source_platform_code__c)&& String.isNotBlank(objMemberPlan.Plan.source_cust_cov_key__c))
            {
                GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
                grpDetailDTO.GroupId = objMemberPlan.Plan.source_cust_cov_key__c.substring(0, 6);
                grpDetailDTO.SubGroupId =(objMemberPlan.Plan.Payer.source_platform_code__c == VOB_PLATFORM_LV_HUM)? objMemberPlan.Plan.source_cust_cov_key__c.substring(9, 12):'';
                grpDetailDTO.BenefitPackageId = (objMemberPlan.Plan.Payer.source_platform_code__c == VOB_PLATFORM_LV_HUM)?'':'';
                grpDetailDTO.BenefitSequenceNumber = (objMemberPlan.Plan.Payer.source_platform_code__c == VOB_PLATFORM_LV_HUM)? objMemberPlan.Plan.source_cust_cov_key__c.substring(6, 9):'';
                
                if (objMemberPlan.Plan.Payer.source_platform_code__c == VOB_PLATFORM_EM_HUM)
                {
                    String[] str = objMemberPlan.Plan.source_cust_cov_key__c.split(' ');
                    grpDetailDTO.SubGroupId = str[0];
                    grpDetailDTO.BenefitPackageId = objPolicyPlan != NULL ? objPolicyPlan.benefit_coverage__C : objMemberPlan.plan.benefit_coverage__C;
                    grpDetailDTO.BenefitSequenceNumber = '';
                }
                grpDetailDTO.Platform = (objMemberPlan.Plan.Payer != null) ?objMemberPlan.Plan.Payer.source_platform_code__c:null;
                grpDetailDTO.StartDate = (objMemberPlan.PlanId != null && objPolicyPlan != NULL) ? objPolicyPlan.Coverage_Plan_Effective_Date__c : objMemberPlan.Plan.EffectiveFrom.format();
                grpDetailDTO.EndDate = (objMemberPlan.PlanId != null && objPolicyPlan != NULL) ? objPolicyPlan.Coverage_Plan_End_Date__c : objMemberPlan.Plan.EffectiveTo.format();
                
                grpDetailDTO.InclusionCriteria = VOB_BENEFITS;
                grpDetailDTO.AsOfDate = refDateParam;
                oRequest.GroupDetailDTO = grpDetailDTO;
			}
			return oRequest;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'Benefits_LD_HUM', 'RequestStructureGBEService');
			return NULL;
		}
    }
    
   
    /*
    * Returns none
    * <p>
    * Method to prepare DTO from GBE response.
    * @param  GBEServiceResponse_H_HUM object .
    * @return GBEResponse_DTO_HUM
    */
	
    public static GBEResponse_DTO_HUM getGBEDTOData(GBEServiceResponse_H_HUM oResponseGBE)
    {
		GBEResponse_DTO_HUM gbeDtoObj = NULL;
        try
        {
            if ( oResponseGBE != null && oResponseGBE.GroupInfo != null &&
                  oResponseGBE.GroupInfo.Benefit != null )
            {            
                gbeDtoObj = new GBEResponse_DTO_HUM();
                GBEServiceResponse_H_HUM.GroupInfo objGrpInfo = oResponseGBE.GroupInfo;                    
                gbeDtoObj.sBenefitPackageId = objGrpInfo.BenefitPackageId;
                GBEServiceResponse_H_HUM.Benefit objBenefit = objGrpInfo.Benefit;
                gbeDtoObj = createGBEBenefitDTO(gbeDtoObj, objBenefit);
            }           
            return gbeDtoObj;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Benefits_LD_HUM', 'getGBEDTOData');
            return NULL;
        }
           
    }
	
    
  
    /*
    * Returns GBEServiceResponse_H_HUM.Benefit
    * <p>
    * Method to prepare DTO from GBE Benefit response.
    * @param  GBEServiceResponse_H_HUM gbeDtoObj .
    * @param  GBEServiceResponse_H_HUM.Benefit objBenefit .
    * @return GBEResponse_DTO_HUM
    */
    public static GBEResponse_DTO_HUM createGBEBenefitDTO(GBEResponse_DTO_HUM gbeDtoObj, GBEServiceResponse_H_HUM.Benefit objBenefit)
    {
        gbeDtoObj.sEnrollmentSource = objBenefit.EnrollmentSource;
        gbeDtoObj.sHourlyReq = objBenefit.HourlyRequirement;
        gbeDtoObj.sGroupEnrollmentCount = objBenefit.GroupEnrollmentCount;
        gbeDtoObj.sMaximumDependentAge = objBenefit.MaximumDependentAge;
        gbeDtoObj.sMaximumStudentAge = objBenefit.MaximumStudentAge;
        gbeDtoObj.sBenefitPeriod = objBenefit.BenefitPeriod;
        gbeDtoObj.sCertificate = objBenefit.Certificate;
        gbeDtoObj.sNetwork = objBenefit.Network;
        gbeDtoObj.sMentalHealthVendorCode = objBenefit.MentalHealthVendorCode;
        gbeDtoObj.sLastRenewalDate = objBenefit.LastRenewalDate;
        gbeDtoObj.sBenefitNetwork = objBenefit.Network;
        gbeDtoObj.sMarketName = objBenefit.MarketName;
        gbeDtoObj.sSellingLedger = objBenefit.SellingLedger;
        gbeDtoObj.sLedgerDescription = objBenefit.SellingLedgerDescription;
        if (objBenefit.OpenEnrollmentList != null && objBenefit.OpenEnrollmentList.OpenEnrollment != null && objBenefit.OpenEnrollmentList.OpenEnrollment.size() > 0)
        {
            GBEServiceResponse_H_HUM.OpenEnrollment openEnrollObj = objBenefit.OpenEnrollmentList.OpenEnrollment[0];
            gbeDtoObj.sOpenEnrolBegindate = openEnrollObj.BeginDate;
            gbeDtoObj.sOpenEnrolEnddate = openEnrollObj.EndDate;
        }
        return gbeDtoObj;
    }
    
    /**
    * Returns MBEPlusRequest_DTO_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for MBE service .
    * @return  MBEPlusRequest_DTO_HUM      request structure of the wrapper class MBEServiceRequest_H_HUM
    */
    public static MBEPlusRequest_DTO_HUM RequestStructureMBEService(String memberPlanId, String asOfDate)
    {
        try
        {
            getPlanDetails(memberPlanId);
            MBEPlusRequest_DTO_HUM oReq = new MBEPlusRequest_DTO_HUM();
            MBEPlusRequest_DTO_HUM.GetMember getMember = new MBEPlusRequest_DTO_HUM.GetMember();
            
            MBEPlusRequest_DTO_HUM.Member   mem = new MBEPlusRequest_DTO_HUM.Member();
            MBEPlusRequest_DTO_HUM.IndicatorList indList = new MBEPlusRequest_DTO_HUM.IndicatorList();
            MBEPlusRequest_DTO_HUM.PlatformPointerList PlatformPointerList = new MBEPlusRequest_DTO_HUM.PlatformPointerList();
            mem.IndicatorList = indList;
            mem.PlatformPointerList = PlatformPointerList;
            mem.SoldProductList = createSoldProductList();
            mem.GetCriteria   = createGetCriteriaList(objMemberPlan, asOfDate);
            getMember.Member = mem;
            oReq.GetMember = getMember;
            return oReq;
            
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'Benefits_LD_HUM', 'RequestStructureMBEService');
            return null;
        }
    }


    /*
    * Method to create structure of Sold Product List that would be passed in tthe request
    * @param
    * @return MBEPlusRequest_DTO_HUM.SoldProductList Structure for Sold Product List
    */
    
    @TestVisible private static MBEPlusRequest_DTO_HUM.SoldProductList createSoldProductList()
    {
        MBEPlusRequest_DTO_HUM.SoldProductList objSoldProductList = new MBEPlusRequest_DTO_HUM.SoldProductList();
        List<MBEPlusRequest_DTO_HUM.SoldProduct> listObjSoldProduct = new List<MBEPlusRequest_DTO_HUM.SoldProduct>();
        MBEPlusRequest_DTO_HUM.SoldProduct ObjSoldProduct = new MBEPlusRequest_DTO_HUM.SoldProduct();
        MBEPlusRequest_DTO_HUM.InsuranceProduct objInsuranceProduct = new MBEPlusRequest_DTO_HUM.InsuranceProduct();
        MBEPlusRequest_DTO_HUM.SoldProductDetailList soldProDetailList = new MBEPlusRequest_DTO_HUM.SoldProductDetailList();
        MBEPlusRequest_DTO_HUM.SoldProductDetail  soldProductDetail  = new MBEPlusRequest_DTO_HUM.SoldProductDetail();
        MBEPlusRequest_DTO_HUM.InsuranceCoverage  insuranceCoverage  = new MBEPlusRequest_DTO_HUM.InsuranceCoverage();
        MBEPlusRequest_DTO_HUM.WaitingPeriodList watPeriodList = new MBEPlusRequest_DTO_HUM.WaitingPeriodList();
        MBEPlusRequest_DTO_HUM.OutOfAreaList outofAreaLst = new MBEPlusRequest_DTO_HUM.OutOfAreaList();
        MBEPlusRequest_DTO_HUM.AncillaryList oAncillaryLst = new MBEPlusRequest_DTO_HUM.AncillaryList();
        MBEPlusRequest_DTO_HUM.CostShareReductionList  costShareReductionList = new MBEPlusRequest_DTO_HUM.CostShareReductionList();
        MBEPlusRequest_DTO_HUM.EnrollmentInfo  enrollInfo = new MBEPlusRequest_DTO_HUM.EnrollmentInfo();
        MBEPlusRequest_DTO_HUM.IndicatorList indicatorList= new MBEPlusRequest_DTO_HUM.IndicatorList();
        MBEPlusRequest_DTO_HUM.PrimaryCarePhysicianList primaryCarePhyList = new MBEPlusRequest_DTO_HUM.PrimaryCarePhysicianList();
        MBEPlusRequest_DTO_HUM.Network netList = new MBEPlusRequest_DTO_HUM.Network();
        MBEPlusRequest_DTO_HUM.CoveredRolePlatformPointerList coveredRolPlatPointList = new MBEPlusRequest_DTO_HUM.CoveredRolePlatformPointerList();
        MBEPlusRequest_DTO_HUM.CoveredRoleList coverRoleList= new MBEPlusRequest_DTO_HUM.CoveredRoleList(); 
        MBEPlusRequest_DTO_HUM.CoveredRole coveredRol = new MBEPlusRequest_DTO_HUM.CoveredRole();
        MBEPlusRequest_DTO_HUM.EnhancedHealthBenefit enHealthBen= new MBEPlusRequest_DTO_HUM.EnhancedHealthBenefit();
        
        insuranceCoverage.WaitingPeriodList = watPeriodList;
        insuranceCoverage.CostShareReductionList = costShareReductionList;
        insuranceCoverage.OutOfAreaList = outofAreaLst;
        insuranceCoverage.AncillaryList = oAncillaryLst;
        insuranceCoverage.EnrollmentInfo = enrollInfo;
        insuranceCoverage.IndicatorList = indicatorList;
        insuranceCoverage.Network = netList;
        insuranceCoverage.PrimaryCarePhysicianList = primaryCarePhyList;
        insuranceCoverage.EnhancedHealthBenefit = enHealthBen;
        coveredRol.CoveredRolePlatformPointerList= coveredRolPlatPointList;
        coverRoleList.CoveredRole= coveredRol;
        insuranceCoverage.CoveredRoleList =coverRoleList;
        soldProductDetail.InsuranceCoverage = insuranceCoverage;
        soldProDetailList.SoldProductDetail = soldProductDetail;
        objInsuranceProduct.SoldProductDetailList = soldProDetailList;
        ObjSoldProduct.InsuranceProduct = objInsuranceProduct;
        listObjSoldProduct.add(objSoldProduct);
        objSoldProductList.SoldProduct = listObjSoldProduct;
        return objSoldProductList;
    }
    
    /*
    * Method to create structure of Person Criteria that would be passed in tthe request
    * @paramString : End Date of Policy member
    * @return MBEPlusRequest_DTO_HUM.GetCriteria Structure for Get Criteria List
    */
    
    private static MBEPlusRequest_DTO_HUM.GetCriteria createGetCriteriaList(MemberPlan MemPlanObj,  String asOfDate)
    {
        MBEPlusRequest_DTO_HUM.GetCriteria  getCriteriaList = new MBEPlusRequest_DTO_HUM.GetCriteria();
        MBEPlusRequest_DTO_HUM.PersonIdCriteria  personIdCriteria  = new MBEPlusRequest_DTO_HUM.PersonIdCriteria();
        personIdCriteria.PersonId = MemPlanObj.Member.Enterprise_Id__c;
        MBEPlusRequest_DTO_HUM.Period per = new MBEPlusRequest_DTO_HUM.Period();
        
        List<String> lstper = new List<String>{ VOB_CURRENT_HUM, VOB_PAST_HUM,VOB_FUTURE_HUM };
        per.Period = lstper;
        personIdCriteria.PeriodList = per;
        personIdCriteria.AsOfDate = populateAsOfDate(asOfDate);
        getCriteriaList.PersonIdCriteria  = personIdCriteria;
        return getCriteriaList;
    }
    
    
    /*
    * Returns MBEResponse_DTO_HUM object
    * <p>
    * Method to prepare MBE DTO to send to UI
    *
    * @param  MBEPlusResponse_DTO_HUM object.
    * @param  memberplan id.
    * @return MBEResponseV1_DTO_HUM*/
    
    public static MBEResponseV1_DTO_HUM getMBEDTOData(MBEPlusResponse_DTO_HUM oMBEResponse, String memberPlanId)
    {
        MBEResponseV1_DTO_HUM dtoObjClass = NULL;
        getPlanDetails(memberPlanId);
        system.debug('oMBEResponse >>>'+oMBEResponse);
        try
        {
            if ( oMBEResponse != null && oMBEResponse.GetMemberResponse != null &&  oMBEResponse.GetMemberResponse.Body != null )
            {   
                MBEPlusResponse_DTO_HUM.GetMemberResponse MBEresponse = oMBEResponse.GetMemberResponse;
                MBEPlusResponse_DTO_HUM.Body body = MBEresponse.body;
                MBEPlusResponse_DTO_HUM.Member member = Body.member;
                dtoObjClass = getMemGenKey(member);
                
                MBEPlusResponse_DTO_HUM.Body objBody = MBEresponse.Body;   
                MBEPlusResponse_DTO_HUM.SoldProductList  soldproductListObj = objBody.Member.SoldProductList;
                dtoObjClass =  (soldproductListObj != null && soldproductListObj.SoldProduct != null && soldproductListObj.SoldProduct.size() > 0)?
                               parseSolProListDTO(oMBEResponse, dtoObjClass, objMemberPlan):null;                                
            }
            return dtoObjClass;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'Benefits_LD_HUM', 'getMBEDTOData');
            return null;
        }
    }    

    
    /*
    * Returns MBEResponseV1_DTO_HUM object
    * <p>
    * Method to retrieve MemgenKey from  MBE Plus response
    *
    * @param  MBEPlusResponse_DTO_HUM.MemberMaster memb .
    * @return MBEResponseV1_DTO_HUM
    */
    @TestVisible
    private static MBEResponseV1_DTO_HUM getMemGenKey(MBEPlusResponse_DTO_HUM.Member memb)
    {
        MBEResponseV1_DTO_HUM dtoObjClass = new MBEResponseV1_DTO_HUM();
        MBEPlusResponse_DTO_HUM.SoldProductList objSoldProductList =(memb.SoldProductList!= null)? memb.SoldProductList:null;
        List<MBEPlusResponse_DTO_HUM.SoldProduct> listObjSoldProduct = (objSoldProductList.SoldProduct!= null)?objSoldProductList.SoldProduct:null;
        MBEPlusResponse_DTO_HUM.InsuranceProduct insPro = (listObjSoldProduct.size()!=0)? listObjSoldProduct[0].InsuranceProduct:null;
        MBEPlusResponse_DTO_HUM.SoldProductDetailList objSoldProductDetaillist = (insPro != null)? insPro.SoldProductDetailList:null;
        List<MBEPlusResponse_DTO_HUM.SoldProductDetail> objSoldProductDetail = objSoldProductDetaillist.SoldProductDetail;                
        if(objSoldProductDetail != null){
            For(MBEPlusResponse_DTO_HUM.SoldProductDetail inscov :objSoldProductDetail)
            {
                MBEPlusResponse_DTO_HUM.InsuranceCoverage objInsuranceCoverage = inscov.InsuranceCoverage;   
                dtoObjClass.Network = (objInsuranceCoverage.Network!= null)?objInsuranceCoverage.Network:null;    
            } 
        }      
        dtoObjClass.IndicatorList = memb.IndicatorList;
        MBEPlusResponse_DTO_HUM.PlatformPointerList  platFormObj = memb.PlatformPointerList ;
        if (platFormObj != null && platFormObj.PlatformProxyKey  != null && platFormObj.PlatformProxyKey.size() > 0)
        {
            for (MBEPlusResponse_DTO_HUM.PlatformProxyKey  memberGenKeyObj : platFormObj.PlatformProxyKey )
            {
                 dtoObjClass.memberGenKey = (memberGenKeyObj != null) && String.isNotBlank(memberGenKeyObj.KeyType) && memberGenKeyObj.KeyType == VOB_GENKEY_KEYTYPE && String.isNotBlank(memberGenKeyObj.KeyValue)? memberGenKeyObj.KeyValue:null;   
            }
        }
        return dtoObjClass;
    }

/*
    * Returns MBEResponseV1_DTO_HUM object
    * <p>
    * Method to prepare MBE Master List DTO to send to UI
    *
    * @param  List<MBEServiceResponse_H_HUM.MemberMaster> object.
    * @param  MBEResponseV1_DTO_HUM object.
    * @param  MemberPlanRecord Record.
    * @return MBEResponseV1_DTO_HUM*/
    private static MBEResponseV1_DTO_HUM parseSolProListDTO(MBEPlusResponse_DTO_HUM oResponse, MBEResponseV1_DTO_HUM dtoObjClass, MemberPlan MemPlanObj)
    {
        bValidNodeFound = false;
        MBEPlusResponse_DTO_HUM.Member mem ;
        MBEPlusResponse_DTO_HUM.InsuranceCoverage insuranceCoverage;
        MBEPlusResponse_DTO_HUM.GetMemberResponse GetMemberResponse = oResponse.GetMemberResponse;
        MBEPlusResponse_DTO_HUM.Body body = GetMemberResponse.body;
        MBEPlusResponse_DTO_HUM.Member member = Body.member;
        MBEPlusResponse_DTO_HUM.SoldProductList objSoldProductList = member.SoldProductList;
        List<MBEPlusResponse_DTO_HUM.SoldProduct> listObjSoldProduct = objSoldProductList.SoldProduct;
        MBEPlusResponse_DTO_HUM.SoldProductDetail objSoldProductDetail = new MBEPlusResponse_DTO_HUM.SoldProductDetail();
        
        if(objSoldProductList <> null && objSoldProductList.SoldProduct  <> null && objSoldProductList.SoldProduct.size() > 0) 
        {           
            for (MBEPlusResponse_DTO_HUM.SoldProduct soldpro :  objSoldProductList.SoldProduct)
            {
                MBEPlusResponse_DTO_HUM.SoldProductDetailList listsoldProductDetail = (soldpro != null && soldpro.InsuranceProduct.SoldProductDetailList  != null)? soldpro.InsuranceProduct.SoldProductDetailList:null;   
                dtoObjClass = getMemPolicyDTO(soldpro,listsoldProductDetail,dtoObjClass, MemPlanObj);
                if (bValidNodeFound) break;
            }
       }
        return dtoObjClass;
    }
    
   private static MBEResponseV1_DTO_HUM getMemPolicyDTO(MBEPlusResponse_DTO_HUM.SoldProduct soldpro ,MBEPlusResponse_DTO_HUM.SoldProductDetailList listsoldProductDetail, MBEResponseV1_DTO_HUM dtoObjClass,MemberPlan MemPlanObj)
   {
    if (listsoldProductDetail != null && listsoldProductDetail.SoldProductDetail!= null &&listsoldProductDetail.SoldProductDetail.size()> 0)
    {
        for (MBEPlusResponse_DTO_HUM.SoldProductDetail soldProductDetailtemp: soldpro.InsuranceProduct.SoldProductDetailList.SoldProductDetail)
        {
            if (soldProductDetailtemp != null && soldProductDetailtemp.InsuranceCoverage!= null && soldProductDetailtemp.InsuranceCoverage.AncillaryList != null && soldProductDetailtemp.InsuranceCoverage.AncillaryList.Ancillary.size()> 0)
            {
                for(MBEPlusResponse_DTO_HUM.Ancillary oAncillary : soldProductDetailtemp.InsuranceCoverage.AncillaryList.Ancillary)
                {
                    if(oAncillary.Name == PaidThroughDate)
                    {    
                        dtoObjClass.paidThruDate = formatDate(oAncillary.Value);
                    }
                }
            }
            
            if (isValidMBENode(soldProductDetailtemp, MemPlanObj)&&soldpro.InsuranceProduct.ProductLineCode == MemPlanObj.plan.Product__r.ProductCode)
            { 
                bValidNodeFound = true;
                dtoObjClass = (soldProductDetailtemp.InsuranceCoverage != null)? createMemPolicyDTO(soldProductDetailtemp.InsuranceCoverage, dtoObjClass):null;
                break;
            }
        }
      }   
       return dtoObjClass;
   }
    /*
    * Returns boolean object
    * <p>
    * Method to identify the correct node in the service response
    *
    * @param  MBEPlusResponse_DTO_HUM.SoldProductDetail object.
    * @param  memberPlan record.
    * @return boolean*/
    @TestVisible
     private static boolean isValidMBENode(MBEPlusResponse_DTO_HUM.SoldProductDetail soldProductDetail, MemberPlan MemPlanObj)
    {
      List<String> startDate;
      String effectiveFrom= MemPlanObj.EffectiveFrom.format();
      String effectiveTo= MemPlanObj.EffectiveTo.format();
      List<String> endDate ;
      List<String> policyEffDate ;
      List<String> policyEndDate ;
      boolean returnValue = false;
      startDate=(soldProductDetail.InsuranceCoverage.StartDate<> NULL)? soldProductDetail.InsuranceCoverage.StartDate.split('/'):null;
     
      if(soldProductDetail.InsuranceCoverage.EndDate<> NULL)
     { 
         if(soldProductDetail.InsuranceCoverage.EndDate == MAX_OLD_DATE)
       {
        string sEndDt = MAX_NEW_DATE;
        endDate = sEndDt.split('/');
       }else{endDate = soldProductDetail.InsuranceCoverage.EndDate.split('/');}
     }
      policyEffDate = (effectiveFrom<>NULL)? effectiveFrom.split('/'):null;  
      policyEndDate =( effectiveTo<>NULL) ? effectiveTo.split('/'):null;
      
      try{      
            if(((soldProductDetail != null && MemPlanObj != null && soldProductDetail.InsuranceCoverage != null && (soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == MemPlanObj.Plan.Source_Major_LOB__c ||soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == MemPlanObj.Plan.Major_LOB__c
            || soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == MemPlanObj.Policy__r.Major_LOB_Frm__c || soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == MemPlanObj.Policy__r.Major_LOB__c)) || test.isRunningTest()))
              { 
                  returnValue = (Integer.valueof(startDate[0])==Integer.valueof(policyEffDate[0])&& Integer.valueof(startDate[1])==Integer.valueof(policyEffDate[1]) && Integer.valueof(startDate[2])==Integer.valueof(policyEffDate[2])) &&
                                (Integer.valueof(endDate[0])==Integer.valueof(policyEndDate[0])&& Integer.valueof(endDate[1])==Integer.valueof(policyEndDate[1]) && Integer.valueof(endDate[2])==Integer.valueof(policyEndDate[2]))?
                                true:false;   
              }
      }catch(Exception e)
          { 
            HUMExceptionHelper.logErrors(e, 'Benefits_LD_HUM', 'isValidMBENode');
          }
      return returnValue;
    }

    /*
    * Returns MBEResponseV1_DTO_HUM object
    * <p>
    * Method to prepare MBE Policy DTO to send to UI
    *
    * @param  MBEPlusResponse_DTO_HUM.InsuranceCoverage object.
    * @param  MBEResponseV1_DTO_HUM object.
    * @return MBEResponseV1_DTO_HUM*/
    
    @TestVisible private static MBEResponseV1_DTO_HUM createMemPolicyDTO(MBEPlusResponse_DTO_HUM.InsuranceCoverage insuranceCoverage, MBEResponseV1_DTO_HUM dtoObjClass)
    {

       if (insuranceCoverage.EnrollmentInfo  != null)
        {
            dtoObjClass.EnrollmentInfo  = insuranceCoverage.EnrollmentInfo;
        }
        if (insuranceCoverage.WaitingPeriodList != null)
        {
            dtoObjClass.WaitingPeriodList = insuranceCoverage.WaitingPeriodList;
        }
        if (String.isNotBlank(insuranceCoverage.OriginalEffectiveDate))
        {
            dtoObjClass.sOriginalEffectiveDate = insuranceCoverage.OriginalEffectiveDate;
        }
        if (String.isNotBlank(insuranceCoverage.CoverageType))
        {
            dtoObjClass.sCoverageType = insuranceCoverage.CoverageType;
        }
        if (insuranceCoverage.OutOfAreaList != NULL)
        {
            dtoObjClass.OutOfAreaList = insuranceCoverage.OutOfAreaList;
        }
        if (insuranceCoverage.CostShareReductionList  != NULL)
        {
            dtoObjClass.CostShareReductionList  = insuranceCoverage.CostShareReductionList;
        }
        if (insuranceCoverage.IndicatorList != NULL)
        {
            dtoObjClass.PolicyIndicatorList = insuranceCoverage.IndicatorList;
        }
        if (insuranceCoverage.Network != NULL)
        {
            dtoObjClass.Network = insuranceCoverage.Network;
        }
        return dtoObjClass;
    }
    @testVisible public static String formatDate(String sDateInput)
    {
        String sDateOutput = '';
        List<String> lstdobValues = new List<String>();
        if (sDateInput != NULL)
        {
            lstdobValues = sDateInput.split('T');
            lstdobValues = lstdobValues[0].split('-');
            if (lstdobValues.size() == 3) sDateOutput = lstdobValues[1] + '/' + lstdobValues[2] + '/' + lstdobValues[0];
        }
        return sDateOutput;
    }

    /**
    * Returns Paid Through Date from the webservice response.
    * This method is to parse the respponse for Billing service .
    *
    * @param    BillingProfileResponseV2_DTO_HUM Webservice response
    * @return   String   Paid through date in the response
    */
    public static String ParseStructureBillingService(BillingProfileResponseV2_DTO_HUM billingProfileResponse)
    {
        String sDateOutput = '';
        if (billingProfileResponse != null && billingProfileResponse.GetBillingAccountInfoResponse != null && billingProfileResponse.GetBillingAccountInfoResponse.GetBillingAccountInfoResponseList != null && billingProfileResponse.GetBillingAccountInfoResponse.GetBillingAccountInfoResponseList.GetBillingAccountInfoResponseDetail != null)
        {
            BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseDetail billingInfo = (!billingProfileResponse.GetBillingAccountInfoResponse.GetBillingAccountInfoResponseList.GetBillingAccountInfoResponseDetail.isEmpty())?
                       billingProfileResponse.GetBillingAccountInfoResponse.GetBillingAccountInfoResponseList.GetBillingAccountInfoResponseDetail[0]:null;
            sDateOutput = (billingInfo != null && billingInfo.BillingAccountList != null && 
                          billingInfo.BillingAccountList.BillAccount != null &&
                          (!billingInfo.BillingAccountList.BillAccount.isEmpty()))? formatDate(billingInfo.BillingAccountList.BillAccount[0].BillingProfile.PaidThroughDate):null;
        }

        return sDateOutput;
    }
  /**
    * Returns BillingProfileRequestV2_DTO_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for Billing service .
    *
    * @param    String  NetworkUser Id
    * @param    String  SearchID
    * @param    String  Platform
    * @return   BillingProfileRequestV2_DTO_HUM      request structure of the wrapper class Billing Service
    */
    public static BillingProfileRequestV2_DTO_HUM RequestStructureBillingService(String sNetworkID, String sPlatform, String sSearchID, String sExchangeType, string sProductType, string sProduct)
    {
        BillingProfileRequestV2_DTO_HUM oReq = new BillingProfileRequestV2_DTO_HUM();
        
        BillingProfileRequestV2_DTO_HUM.InvoiceInfo invoiceInfo = new BillingProfileRequestV2_DTO_HUM.InvoiceInfo();
        invoiceInfo.IncludeInvoiceInformation = 'true';
        invoiceInfo.NbrOfInvoices = PAIDTHROUGHDATE_NUMBEROFTRANSACTIONS_HUM;

        BillingProfileRequestV2_DTO_HUM.PremiumInfo premiumInfo = new BillingProfileRequestV2_DTO_HUM.PremiumInfo();
        premiumInfo.IncludePremiumInformation = 'true';
        premiumInfo.RateLevel = PAIDTHROUGHDATE_RATELEVEL_HUM;

        BillingProfileRequestV2_DTO_HUM.IncludeCriteria includeCriteriaDTO = new BillingProfileRequestV2_DTO_HUM.IncludeCriteria();
        includeCriteriaDTO.GetGroupInfo = 'true';       
       

        BillingProfileRequestV2_DTO_HUM.RequestContext requestContect = new BillingProfileRequestV2_DTO_HUM.RequestContext();
        
        requestContect.CallingSystemID = 'PDCRM';
        requestContect.UserIDType = 'EXTL';
        requestContect.ProfileSequenceNbr = 0;
       
        requestContect.UserID = sNetworkID;
        requestContect.ProcessDate = datetime.now().format('yyy-MM-dd');

        BillingProfileRequestV2_DTO_HUM.SearchCriteria searchCriteria = new BillingProfileRequestV2_DTO_HUM.SearchCriteria();
        searchCriteria.PlatformCD = sPlatform;
        searchCriteria.IDType = PAIDTHROUGHDATE_IDTYPE_HUM;
        searchCriteria.SearchID = sSearchID;
        List<BillingProfileRequestV2_DTO_HUM.SearchCriteria> searchCriterias = new List<BillingProfileRequestV2_DTO_HUM.SearchCriteria>();
        searchCriterias.add(searchCriteria);

        BillingProfileRequestV2_DTO_HUM.SearchCriteriaList searchCriteriaList = new BillingProfileRequestV2_DTO_HUM.SearchCriteriaList();
        searchCriteriaList.SearchCriteria = searchCriterias;

        BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequestCriteria BillingAccountInfoRequestCriteria = new  BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequestCriteria();
        BillingAccountInfoRequestCriteria.RequestContext = requestContect;
        BillingAccountInfoRequestCriteria.SearchCriteria = searchCriteriaList;
        BillingAccountInfoRequestCriteria.IncludeCriteria = includeCriteriaDTO;

        BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequest GetBillingAccountInfoRequest = new BillingProfileRequestV2_DTO_HUM.GetBillingAccountInfoRequest();
        GetBillingAccountInfoRequest.GetBillingAccountInfoRequestCriteria = BillingAccountInfoRequestCriteria;

        oReq.GetBillingAccountInfoRequest = GetBillingAccountInfoRequest;

        return oReq;

    }

    // Mentor Browser
    public static MentorServiceRequest_DTO_HUM RequestStructureKDMService(MentorServiceRequest_DTO_HUM KDMRequestDTO,String memberPlanId)
    {
    string sContractNum;
     string sPBPCode;
     string sMedicaresegId; 
     string sQueryText = '';
     string sProduct; 
     string sProductType;
     string sPlatform; 
     string sBenCoverage;
     string sGrpNumber;  
        if(string.isNotBlank(memberPlanId))
        {
            MemberPlan objPolMem = [Select Id ,Product_Type__c,Plan.Platform__c,Product__c ,Plan.Product_Type__c,Plan.Contract_Number__c,Plan.PBP_Code__c,Plan.Benefit_coverage__c,
                                    Plan.Policy_Group_Number__c,Plan.Medicare_Segment_ID__c  From MemberPlan Where Id =:memberPlanId];
            if(objPolMem != null)
            {   
                sPlatform = objPolMem.Plan.Platform__c;
                sBenCoverage = objPolMem.Plan.Benefit_coverage__c;
                sContractNum = objPolMem.Plan.Contract_Number__c;
                sPBPCode = objPolMem.Plan.PBP_Code__c;
                sMedicaresegId = objPolMem.Plan.Medicare_Segment_ID__c;  
                sGrpNumber = objPolMem.Plan.Policy_Group_Number__c;
                sProduct = objPolMem.Product__c;
                sProductType = objPolMem.Product_Type__c;
                
                List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                                                                                                  Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :VOB_MEDICARE WITH USER_MODE];
                string strPBE= sContractNum+sHiphan+sPBPCode;
                sQueryText = (sProduct == MED) && !lstRefBusinessLineProdGroups.isEmpty()? strPBE: '';
                string strMedicaresegId = sHiphan +  sMedicaresegId;
                sQueryText += String.isNotBlank(sMedicaresegId)? strMedicaresegId: '';
                string strBenCoverage = SUBGROUP_MENTORREDIRECT_HUM + sBenCoverage;
                sQueryText = (String.isBlank(sQueryText) && ( sPlatform == sEM_Value || sPlatform == sMTV_Value) )?strBenCoverage:'';
                string strGroup = SUBGROUP_MENTORREDIRECT_HUM + sGrpNumber;
                sQueryText = (String.isBlank(sQueryText)) && (sPlatform == sLV_Value || sPlatform == sCI_Value || sPlatform == sCAS_Value)
                               && (String.isNotBlank(sGrpNumber) )? strGroup :'';
                                                
                    
                
            }
        }
        MentorServiceRequest_DTO_HUM oRequest = new MentorServiceRequest_DTO_HUM();      
        MentorServiceRequest_DTO_HUM.Request oHasSearch = new MentorServiceRequest_DTO_HUM.Request();
        oHasSearch.BenefitGridsOnly = true;
        oHasSearch.ConsumingApp = HUM_PARTD_DRUGREQUESTOR;
        oHasSearch.Query = sQueryText;
        oRequest.Request = oHasSearch;
        return oRequest;
}
    
public static Boolean ParseStructureKMDocument(MentorServiceResponse_DTO_HUM KMServiceResponse)
    {
        Boolean sKMDocuStatus = false;
        if (KMServiceResponse != null && KMServiceResponse.HasSearchResultsResponse != null)
        {
                if (KMServiceResponse.HasSearchResultsResponse != null)
                {
                     sKMDocuStatus = Boolean.valueOf(KMServiceResponse.HasSearchResultsResponse.HasMatches);
                }
            
        }

        return sKMDocuStatus;
    }

    
    
    
    /* Returns request for Benefits Search
    * <p>
    * Method to get the Benefits Search request
    *
    * @param String           Service Category Id
    * @param String           Type Of Service
    * @param String           Place of Service
    * @param String           Par /non Par
    * @param String           Platform Code
    * @param String           Product Key
    * @param String           Ref Date
    * @return                 ProductBusinessEntityRequestV2_H_HUM
    */
    public static ProductBusinessEntityRequestV2_H_HUM RequestStructureBenefitSearch(List<String> sServiceCategory, String sTypeOfService, String sPlaceOfService, String sPar, string platformCode, String productKey, string refdate,Boolean isCAS, string ADACode)
    {

        ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
        ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();

        boolean sServiceTypeCommentSwitch = false;            
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');
        productNode.SubVersion = '3';   
        productNode.AsOfDate =populateAsOfDate(refdate); 
        ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
        PrId.ProductId = productKey;
        if (platformCode == VOB_PLATFORM_EM_HUM)
        {
            PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
        }
        else if (platformCode == VOB_PLATFORM_LV_HUM)
        {
            PrId.ProductIdType = VOB_VALUEPDQ_HUM;
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
    
    
    /* Method to parse Benefit Search Result
    * <p>
    * Methd is used to Benefit Search response and returncreate DTO to show values on UI
    * @param  ProductBusinessEntityResponseV2_H_HUM   Webservice Response .
    * @param  String        PAR/NON Par value
    * @return PBEBenefitsSearch_DTO_HUM DTO to be sown on UI
    */
    public static PBEBenefitsSearch_DTO_HUM getBenefitsSearchDTO(ProductBusinessEntityResponseV2_H_HUM oResponse, string ParValue)
    {
        if (ParValue != 'Yes' && ParValue != 'No') ParValue = VOB_BOTH;
        Map<string, list<CoverageType_DTO_HUM>> inNetworkMap = new Map<string, list<CoverageType_DTO_HUM>>();
        Map<string, list<CoverageType_DTO_HUM>> outNetworkMap = new Map<string, list<CoverageType_DTO_HUM>>();
        Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork = new Map<string, list<CoverageType_DTO_HUM>>();
        Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork = new Map<string, list<CoverageType_DTO_HUM>>();
        Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork = new Map<string, List<CoverageType_DTO_HUM>>();
        Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsInNetwork = new Map<string, List<CoverageType_DTO_HUM>>();
        Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork = new Map<string, List<CoverageType_DTO_HUM>>();
        Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork = new Map<string, List<CoverageType_DTO_HUM>>();
        Map<string, List<string>> lstOfServiceComments = new Map<string, List<string>>();
        Map<string, Boolean> isCoveredServiceInNetworks = new Map<string, Boolean>();

        if (oResponse != null && oResponse.GetProductResponse != null && oResponse.GetProductResponse.ProductList != null && oResponse.GetProductResponse.ProductList.Product != null)
        {
            prepareBenefits(oResponse.GetProductResponse.ProductList.Product[0], ParValue, inNetworkMap, outNetworkMap, lstOfServiceComments, isCoveredServiceInNetworks);
        }
        if (inNetworkMap.size() > 0) prepareInNetworkResponse(inNetworkMap, finalMapofNonLimitInNetwork, lstOfLimitsInNetwork);
        if (outNetworkMap.size() > 0) prepareOutNetworkResponse(outNetworkMap, finalMapofNonLimitOutNetwork, lstOfLimitsOutNetwork);
        if (ParValue == VOB_BOTH)
        {
            finalMapofNonLimitInOutNetwork = finalMapofNonLimitInNetwork.clone();
            lstOfLimitsInOutNetwork = lstOfLimitsInNetwork.clone();
            prepareFinalMapNonLimit(finalMapofNonLimitOutNetwork, finalMapofNonLimitInOutNetwork);
            Map<string, List<CoverageType_DTO_HUM>> TempMapInOutNetwork = prepareBOTHMap(finalMapofNonLimitInOutNetwork);

            if (TempMapInOutNetwork != null && TempMapInOutNetwork.size() > 0)
            {
                finalMapofNonLimitInOutNetwork.clear();
                finalMapofNonLimitInOutNetwork = TempMapInOutNetwork.clone();
            }
            prepareLimitsNetwork(lstOfLimitsOutNetwork, lstOfLimitsInOutNetwork);
        }


        List<CoverageType_DTO_HUM> lstNewIN = new List<CoverageType_DTO_HUM>();
        List<CoverageType_DTO_HUM> lstNewOUT = new List<CoverageType_DTO_HUM>();
        for (string nodeType : finalMapofNonLimitInNetwork.keyset())
        {
            if (nodeType == 'Service')
            {
                for (CoverageType_DTO_HUM oCoverageType : finalMapofNonLimitInNetwork.get(nodeType))
                {
                    if (oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False'))
                    {
                        CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
                        covDtoObj.BenefitDescription = oCoverageType.BenefitDescription;
                        covDtoObj.placeOfServiceCode = oCoverageType.placeOfServiceCode;
                        covDtoObj.benefitType = 'Not Covered';
                        lstNewIN.add(covDtoObj);
                    }
                    else    lstNewIN.add(oCoverageType);
                }
            }
        }
        if (finalMapofNonLimitInNetwork.containsKey('Service'))    finalMapofNonLimitInNetwork.remove('Service');
        if (!lstNewIN.isEmpty())    finalMapofNonLimitInNetwork.put('Service', lstNewIN);

        for (string nodeType : finalMapofNonLimitOutNetwork.keyset())
        {
            if (nodeType == 'Service')
            {
                for (CoverageType_DTO_HUM oCoverageType : finalMapofNonLimitOutNetwork.get(nodeType))
                {
                    if (oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False'))
                    {
                        CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
                        covDtoObj.BenefitDescription = oCoverageType.BenefitDescription;
                        covDtoObj.placeOfServiceCode = oCoverageType.placeOfServiceCode;
                        covDtoObj.benefitType = 'Not Covered';
                        lstNewOUT.add(covDtoObj);
                    }
                    else    lstNewOUT.add(oCoverageType);
                }
            }
        }
        if (finalMapofNonLimitOutNetwork.containsKey('Service'))    finalMapofNonLimitOutNetwork.remove('Service');
        if (!lstNewOUT.isEmpty())    finalMapofNonLimitOutNetwork.put('Service', lstNewOUT);

        return prepareResponseStructure(isCoveredServiceInNetworks, ParValue, lstOfLimitsInNetwork, lstOfLimitsOutNetwork, lstOfLimitsInOutNetwork, finalMapofNonLimitInNetwork, finalMapofNonLimitOutNetwork, finalMapofNonLimitInOutNetwork, lstOfServiceComments);
    }
    
    
     /* Method for parsing of InNetwork and OutOfNetwork benefit type
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @return nothing
    */
    
    private Static void prepareBenefits(ProductBusinessEntityResponseV2_H_HUM.Product oProduct, String ParValue, Map<string, list<CoverageType_DTO_HUM>> inNetworkMap, Map<string, list<CoverageType_DTO_HUM>> outNetworkMap, Map<string, List<string>> lstOfServiceComments, Map<string, Boolean> isCoveredServiceInNetworks)
    {          
        if (oProduct != null && oProduct.Detail != null &&
		    oProduct.Detail.ServiceList != null && oProduct.Detail.ServiceList.Service != null && 
			oProduct.Detail.ServiceList.Service.size() > 0 )
            getBenefits (oProduct,ParValue,inNetworkMap,outNetworkMap,lstOfServiceComments,isCoveredServiceInNetworks);
    }

    private static void getBenefits(ProductBusinessEntityResponseV2_H_HUM.Product oProduct, String ParValue, Map<string, list<CoverageType_DTO_HUM>> inNetworkMap, Map<string, list<CoverageType_DTO_HUM>> outNetworkMap, Map<string, List<string>> lstOfServiceComments, Map<string, Boolean> isCoveredServiceInNetworks)
    {
        boolean sServiceTypeCommentSwitch = false; 
        //REQ - 732113 Service Type Comments ordering - Dental only
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');    
        List<ProductBusinessEntityResponseV2_H_HUM.Service> oServiceList = oProduct.Detail.ServiceList.Service;
        Boolean bPlatformLV = false;
        bPlatformLV = (oProduct.MetaData.PlatformCode == 'LV' || oProduct.MetaData.PlatformCode == 'CI' || oProduct.MetaData.PlatformCode == 'CAS')?true:false;
        for(ProductBusinessEntityResponseV2_H_HUM.Service oService :oServiceList){  
            if (oService != null && oService.BenefitList != null && oService.BenefitList.Benefit != null && oService.BenefitList.Benefit.size() > 0)
            	updatebenSearchResArray(inNetworkMap,outNetworkMap,oService.BenefitList.Benefit,ParValue,VOB_BENEFITSEARCH_SERVICELIST_HUM, isCoveredServiceInNetworks,bPlatformLV,oService.ServiceCategoryCode, oService.ServiceTypeID);
            if(oService != null && oService.CommentList != null && oService.CommentList.PlanComment != null && 
			   oService.CommentList.PlanComment.size () > 0 && sServiceTypeCommentSwitch == true)
				updatebenSearchServiceCommentsArraySubVerion3(oService.CommentList.PlanComment,lstOfServiceComments, oService.ServiceTypeID);               
			else if(oService != null && oService.CommentList != null && oService.CommentList.PlanComment != null && 
			oService.CommentList.PlanComment.size () > 0 && sServiceTypeCommentSwitch == false)
				updatebenSearchServiceCommentsArray(oService.CommentList.PlanComment,lstOfServiceComments, oService.ServiceTypeID);
        }
    }

    /* Method for parsing of InNetwork and OutOfNetwork benefit type
    * <p>
    * @param  string selected PAR value .
    * @param  string benefit type .
    * @param List<ProductBusinessEntityResponseV2_H_HUM.Benefit>
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @return nothing
    */
    @testvisible private static void updatebenSearchResArray (Map<string,list<CoverageType_DTO_HUM>> inNetworkMap,Map<string,list<CoverageType_DTO_HUM>> outNetworkMap, List<ProductBusinessEntityResponseV2_H_HUM.Benefit> BenefitLst,string parVal,string nodeTypeValue, Map<string,Boolean> isCoveredServiceInNetworks,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        Boolean inNetworkIsCovered = false;
        Boolean outNetworkIsCovered = false;
        Boolean inNetwork = false;
        Boolean outNetwork = false;
        for (Integer j = 0; j < BenefitLst.size(); j++)
        {
            if (parVal == VOB_PAR_YES_HUM || parVal == VOB_BOTH)
            {
                if (string.isNotBlank(BenefitLst[j].NetworkType) && BenefitLst[j].NetworkType == VOB_BENEFITSEARCH_INNETWORK_HUM)
                {
                    inNetwork = (string.isNotBlank(BenefitLst[j].IsCoveredService))&&(!inNetwork)?true:false;
                    inNetworkIsCovered =(string.isNotBlank(BenefitLst[j].IsCoveredService))&&(!inNetworkIsCovered && BenefitLst[j].IsCoveredService.equalsIgnoreCase('True'))?true:false;
                    
                    if (BenefitLst[j].BenefitDetailList != null && !BenefitLst[j].BenefitDetailList.BenefitDetail.isEmpty())
					{
                    	getAssignInNetworkValue(BenefitLst[j].BenefitDetailList.BenefitDetail,inNetworkMap,nodeTypeValue,parVal,bPlatformLV, ServiceCatCode,  ServiceTypeID);
					}
                }
            }
            if (parVal == VOB_PAR_NO_HUM || parVal == VOB_BOTH)
            {
                if (string.isNotBlank(BenefitLst[j].NetworkType) && BenefitLst[j].NetworkType == VOB_BENEFITSEARCH_OUTNETWORK_HUM)
                {
                    if (string.isNotBlank(BenefitLst[j].IsCoveredService))
					{
                        outNetwork = (!outNetwork) ? true:false;
                        outNetworkIsCovered = (!outNetworkIsCovered && BenefitLst[j].IsCoveredService.equalsIgnoreCase('True')) ?true:false;
					}
                    if (BenefitLst[j].BenefitDetailList != null && !BenefitLst[j].BenefitDetailList.BenefitDetail.isEmpty())
					{
                    	getAssignOutNetworkValue(BenefitLst[j].BenefitDetailList.BenefitDetail,outNetworkMap,nodeTypeValue,parVal,bPlatformLV, ServiceCatCode,  ServiceTypeID);
					}
                }
            }
        }
         
        if (inNetwork)   isCoveredServiceInNetworks.put(VOB_BENEFITSEARCH_INNETWORK_HUM, inNetworkIsCovered);
        if (outNetwork)    isCoveredServiceInNetworks.put(VOB_BENEFITSEARCH_OUTNETWORK_HUM, outNetworkIsCovered);
    }
    /* Method to show service type comments order based on PlanCommentSequenceNumber 
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.PlanComment> planCommentsLst, Map<string, List<string>> lstOfServiceComments,String ServiceTypeID  .
    * @return void
    */
    @testvisible private static void updatebenSearchServiceCommentsArraySubVerion3(List<ProductBusinessEntityResponseV2_H_HUM.PlanComment> planCommentsLst, Map<string, List<string>> lstOfServiceComments,String ServiceTypeID)
    {
        Integer sequenceNo;
        Set<Integer> sequenceSet;
        List<Integer> sequenceList;
        Map<Integer,String> commentWithSequenceMap = new Map<Integer,String>();
        
        if (planCommentsLst != null && planCommentsLst.size() > 0)
        {
            List<String> listOfComments = new List<String>();
            integer count = 0;
            for (ProductBusinessEntityResponseV2_H_HUM.PlanComment oPlanComments : planCommentsLst)
            {
                 
                count++;
                if (oPlanComments != null && string.isNotBlank(oPlanComments.Comment) && oPlanComments.PlanCommentSequenceNumber == null )
                {
					listOfComments.add(oPlanComments.Comment);
                }
                else if(oPlanComments != null && string.isNotBlank(oPlanComments.Comment) && oPlanComments.PlanCommentSequenceNumber != null){
                    sequenceNo = integer.ValueOf(oPlanComments.PlanCommentSequenceNumber + count);
                    commentWithSequenceMap.put(sequenceNo,oPlanComments.Comment);
                }
            }
            
            if(!commentWithSequenceMap.isEmpty()){
                sequenceSet = commentWithSequenceMap.keySet();
                sequenceList = new List<Integer>(sequenceSet);
                sequenceList.sort();
                //Service Type Comments in descending Order 
                for(integer i = sequenceList.size()-1; i >= 0; i--){
                    listOfComments.add(commentWithSequenceMap.get(sequenceList[i]));
                }               
            }
            
            lstOfServiceComments.put(ServiceTypeID, listOfComments);
        }
    }
     @testvisible private static void updatebenSearchServiceCommentsArray(List<ProductBusinessEntityResponseV2_H_HUM.PlanComment> planCommentsLst, Map<string, List<string>> lstOfServiceComments,String ServiceTypeID)
    {
        if (planCommentsLst != null && planCommentsLst.size() > 0)
        {
            List<String> listOfComments = new List<String>();
            for (ProductBusinessEntityResponseV2_H_HUM.PlanComment oPlanComments : planCommentsLst)
            {
                if (oPlanComments != null && string.isNotBlank(oPlanComments.Comment))
				{
                    listOfComments.add(oPlanComments.Comment);
            }
			}
            lstOfServiceComments.put(ServiceTypeID, listOfComments);
        }
    }
    
    /* Method for parsing of InNetwork and OutOfNetwork benefit type
    * <p>
    * @param  string selected PAR value .
    * @param  list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail>
    * @param  string node value .
    * @param List<ProductBusinessEntityResponseV2_H_HUM.Benefit>
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @return nothing
    */
    private static void getAssignInNetworkValue(list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail> pbeBenDtlList,Map<string,list<CoverageType_DTO_HUM>> inNetworkMap,string nodeTypeValue,string parVal,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        string tierNumber = null;
        string isCoveredBenefit = null;
        for (ProductBusinessEntityResponseV2_H_HUM.BenefitDetail pbeBenDtl : pbeBenDtlList)
        {
            tierNumber = null;
            isCoveredBenefit = null;
            tierNumber =  (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.TierNumber) && !'None'.equalsIgnoreCase(pbeBenDtl.TierNumber))?pbeBenDtl.TierNumber:null;
            isCoveredBenefit = (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.IsCoveredBenefit) && !'None'.equalsIgnoreCase(pbeBenDtl.IsCoveredBenefit))? pbeBenDtl.IsCoveredBenefit:null;
            
            if (pbeBenDtl != null && pbeBenDtl.BenefitAmountList != null && !pbeBenDtl.BenefitAmountList.BenefitAmount.isEmpty())
            {
                nodeTypeValue = (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.BenefitType))? pbeBenDtl.BenefitType + '_Service':'';
                getInNetworkValue(pbeBenDtl,bPlatformLV,inNetworkMap,nodeTypeValue,parVal,tierNumber,isCoveredBenefit, ServiceCatCode, ServiceTypeID);
            }
        }
    }
    private static void getInNetworkValue(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail pbeBenDtl,Boolean bPlatformLV,Map<string,list<CoverageType_DTO_HUM>> inNetworkMap,string nodeTypeValue,string parVal,string tierNumber,string isCoveredBenefit,String ServiceCatCode, String ServiceTypeID)
    {
        if (pbeBenDtl.PlaceOfServiceCodeList != null && pbeBenDtl.PlaceOfServiceCodeList.Code != null && pbeBenDtl.PlaceOfServiceCodeList.Code.size() > 0)
        {
            for (string placeOfServiceCodeIn : pbeBenDtl.PlaceOfServiceCodeList.Code)
            {
                if (bPlatformLV && pbeBenDtl.ParCodeIDList != null && pbeBenDtl.ParCodeIDList.Code != null && pbeBenDtl.ParCodeIDList.Code.size() > 0)
                {
                    for (string parCodeIDListIn : pbeBenDtl.ParCodeIDList.Code)
                    {                             
                        assignInNetworkValues( pbeBenDtl, pbeBenDtl.BenefitAmountList.BenefitAmount,inNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeIn,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV, ServiceCatCode,  ServiceTypeID);
                    }
                }
                else    assignInNetworkValues( pbeBenDtl, pbeBenDtl.BenefitAmountList.BenefitAmount,inNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeIn,tierNumber,isCoveredBenefit,'',bPlatformLV,ServiceCatCode, ServiceTypeID);         
            }
        }
    }
    /* Method for parsing of InNetwork and OutOfNetwork benefit type
    * <p>
    * @param  string selected PAR value .
    * @param  list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail>
    * @param  string node value .
    * @param List<ProductBusinessEntityResponseV2_H_HUM.Benefit>
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutNetwork .
    * @return nothing
    */
    private static void getAssignOutNetworkValue(list<ProductBusinessEntityResponseV2_H_HUM.BenefitDetail> pbeBenDtlList,Map<string,list<CoverageType_DTO_HUM>> outNetworkMap,string nodeTypeValue,string parVal,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        string tierNumber = null;
        string isCoveredBenefit = '';
        for (ProductBusinessEntityResponseV2_H_HUM.BenefitDetail pbeBenDtl : pbeBenDtlList)
        {
            tierNumber = null;
            isCoveredBenefit = (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.IsCoveredBenefit) && !'None'.equalsIgnoreCase(pbeBenDtl.IsCoveredBenefit))?pbeBenDtl.IsCoveredBenefit:'';
            tierNumber = (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.TierNumber) && !'None'.equalsIgnoreCase(pbeBenDtl.TierNumber))?pbeBenDtl.TierNumber:null;
            if (pbeBenDtl != null && pbeBenDtl.BenefitAmountList != null && !pbeBenDtl.BenefitAmountList.BenefitAmount.isEmpty())
            {
                nodeTypeValue = (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.BenefitType))? pbeBenDtl.BenefitType + '_Service':null;
                if (pbeBenDtl.PlaceOfServiceCodeList != null && pbeBenDtl.PlaceOfServiceCodeList.Code != null && pbeBenDtl.PlaceOfServiceCodeList.Code.size() > 0)
                {
                    getOutNetworkValue(pbeBenDtl,outNetworkMap,nodeTypeValue,tierNumber,isCoveredBenefit,parVal, bPlatformLV, ServiceCatCode,ServiceTypeID);
                }
            }
        }
    }
    private static void getOutNetworkValue(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail pbeBenDtl,Map<string,list<CoverageType_DTO_HUM>> outNetworkMap,string nodeTypeValue,string tierNumber,string isCoveredBenefit, string parVal,Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
    for (string placeOfServiceCodeOut : pbeBenDtl.PlaceOfServiceCodeList.Code)
    {
        if (bPlatformLV && pbeBenDtl.ParCodeIDList != null && pbeBenDtl.ParCodeIDList.Code != null && pbeBenDtl.ParCodeIDList.Code.size() > 0)
        {
            for (string parCodeIDListOut : pbeBenDtl.ParCodeIDList.Code)
            {
                assignOutNetworkValues(pbeBenDtl.BenefitAmountList.BenefitAmount,outNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeOut,tierNumber,isCoveredBenefit,parCodeIDListOut,bPlatformLV,ServiceCatCode,ServiceTypeId);
            }
        }
        else  assignOutNetworkValues(pbeBenDtl.BenefitAmountList.BenefitAmount,outNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeOut,tierNumber,isCoveredBenefit,'',bPlatformLV,ServiceCatCode,ServiceTypeId);
    }
}
    
    /* Method for parsing of InNetwork values and prepare In network map with both values Limits and Non Limits values
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Benefit type value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @return nothing
    */
    //REQ-410445 Add BenefitDetail node in argument to access service ada code 
    private static void assignInNetworkValues(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail benefitdetails, List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListIn, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
            prepareNodeStructureInNetwork(benefitdetails, pbeBenAmtLst,networkMap,nodeTypeValue,sPar,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV,ServiceCatCode, ServiceTypeID);
    }


    /* Method for parsing of OutOfNetwork values and prepare Out network map with both values Limits and Non Limits values
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Benefit type value .
    * @param  string tierNumber value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @return nothing
    */
    private static void assignOutNetworkValues(List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListOut, Boolean bPlatformLV, String ServiceCatCode, String ServiceTypeID)
    {
        prepareNodeStructureOutNetwork(pbeBenAmtLst,networkMap,nodeTypeValue,sPar,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListOut,bPlatformLV, ServiceCatCode,  ServiceTypeID);
    }
    
    
    /* Method for parsing of InNetwork values and prepare In network map with both values Limits and Non Limits values
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Node type value .
    * @param  string TierNumber value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @return nothing
    */
    private static void prepareNodeStructureInNetwork(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail details, List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListIn, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        String  mapAdaCode = '';
        if (details != null && details.ServiceAdaCodeList != null && details.ServiceAdaCodeList.Code != null)
        {
            for (string adaCodes : details.ServiceAdaCodeList.Code)
            {
                    mapAdaCode = adaCodes;
					if(String.isNotBlank(mapAdaCode))
						break;
            }
        }
        prepareNodeStructure(pbeBenAmtLst,networkMap,nodeTypeValue,sPar,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV,ServiceCatCode, ServiceTypeID,mapAdaCode);
    }
    private static void prepareNodeStructure(List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst, Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListIn, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID,String mapAdaCode)
    {
        
        for (Integer index = 0; index < pbeBenAmtLst.size(); index++)
        {
            CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
            covDtoObj.Networktype = 'IN';
            if (pbeBenAmtLst[index] != null && string.isNotBlank(pbeBenAmtLst[index].IsLimit))
            {
                if (pbeBenAmtLst[index].UnitDetail != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity))
				{
					covDtoObj.Par = getFormattedValue(pbeBenAmtLst[index].UnitDetail);																																													
				}
                covDtoObj.AdaCode = mapAdaCode;
                createCoverageDTO(covDtoObj, index, pbeBenAmtLst,nodeTypeValue,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV,ServiceCatCode,  ServiceTypeID);
                if (networkMap != null && networkMap.containsKey(nodeTypeValue))
                {
                    List<CoverageType_DTO_HUM> listcovType = networkMap.get(nodeTypeValue);
                    listcovType.add(covDtoObj);
                }
                else
                {
                    list<CoverageType_DTO_HUM> lstCoverages = new List<CoverageType_DTO_HUM>();
                    lstCoverages.add(covDtoObj);
                    networkMap.put(nodeTypeValue, lstCoverages);
                }
				
            }
        }
    }
    
    /* Method for parsing of outNetwork values and prepare Out network map with both values Limits and Non Limits values
    * <p>
    * @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  string selected PAR value .
    * @param  string Node type value .
    * @param  string tierNumber value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutNetwork .
    * @return nothing
    */
    private static void prepareNodeStructureOutNetwork(List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber,string isCoveredBenefit, String parCodeIDListOut, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        for (Integer index = 0; index < pbeBenAmtLst.size(); index++)
        {
            CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
            covDtoObj.Networktype = 'OUT';
            if (pbeBenAmtLst[index] != null && string.isNotBlank(pbeBenAmtLst[index].IsLimit))
            {
                if (pbeBenAmtLst[index].UnitDetail != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity))
				{
					covDtoObj.NonPar = getFormattedValue(pbeBenAmtLst[index].UnitDetail);
				}
                createCoverageDTO(covDtoObj, index, pbeBenAmtLst,nodeTypeValue,benefitsType,placeOfServiceCode,tierNumber,isCoveredBenefit,parCodeIDListOut,bPlatformLV, ServiceCatCode,  ServiceTypeID);
                if (networkMap != null && networkMap.containsKey(nodeTypeValue))
                {
                    List<CoverageType_DTO_HUM> listcovType = networkMap.get(nodeTypeValue);
                    listcovType.add(covDtoObj);
                }
                else
                {
                    list<CoverageType_DTO_HUM> lstCoverages = new List<CoverageType_DTO_HUM>();
                    lstCoverages.add(covDtoObj);
                    networkMap.put(nodeTypeValue, lstCoverages);
                }
            }
        }
    }
    
    /* Method for assigning values to DTO object
    * <p>
    * @param CoverageType_DTO_HUM object .
    * @param  Integer .
    * @param  List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  string Benefit Type .
    * @param  string tierNumber Value .
    * @return nothing
    */
    private static void createCoverageDTO(CoverageType_DTO_HUM covDtoObj , Integer index , List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , String nodeTypeValue,string benefitsType,string placeOfServiceCode,string tierNumber,String isCoveredBenefit,String parCodeIDList, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
    {
        covDtoObj.nodeType = nodeTypeValue;
        if(string.isNotBlank(ServiceCatCode)){
            covDtoObj.ServiceCatCode = ServiceCatCode;
        }
        if(string.isNotBlank(ServiceTypeID)){
            covDtoObj.ServiceTypeID = ServiceTypeID;
        }
        if (!bPlatformLV && string.isNotBlank(pbeBenAmtLst[index].BenefitDescription)){
            covDtoObj.BenefitDescription = pbeBenAmtLst[index].BenefitDescription;
        }  
		if (bPlatformLV && string.isNotBlank(parCodeIDList)){
            covDtoObj.BenefitDescription = parCodeIDList;
        }
        if (string.isNotBlank(pbeBenAmtLst[index].ProviderSubNetworkNumber)){
            covDtoObj.ProviderSubNetworkNumber = pbeBenAmtLst[index].ProviderSubNetworkNumber;
        }
        if (string.isNotBlank(pbeBenAmtLst[index].IsLimit)){
            covDtoObj.isLimit = pbeBenAmtLst[index].isLimit;
        }
        if (string.isNotBlank(pbeBenAmtLst[index].CoverageType)){
            covDtoObj.CoverageType = pbeBenAmtLst[index].CoverageType;
        }
        if (string.isNotBlank(tierNumber)){
            covDtoObj.TierNumber = TierNumber;
        }
        if (string.isNotBlank(isCoveredBenefit)){
            covDtoObj.sIsCoveredBenefit = isCoveredBenefit;
        }
        if(string.isNotBlank(benefitsType)){
            covDtoObj.benefitType = benefitsType;
        }
        covDtoObj.placeOfServiceCode = (string.isNotBlank(placeOfServiceCode)) ? placeOfServiceCode : '';
        covDtoObj.limitName = (pbeBenAmtLst[index].LimitName != 'None') ? pbeBenAmtLst[index].LimitName : pbeBenAmtLst[index].BenefitDescription;
        if (pbeBenAmtLst[index].UnitDetail != null)
        {
            String limitText ='';
            if(bPlatformLV){              
               if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityType ) && pbeBenAmtLst[index].UnitDetail.QuantityType != 'None' ){
                    limitText =   pbeBenAmtLst[index].UnitDetail.QuantityType;
               } 
               if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType) && pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType != 'None'){
                    limitText =  limitText + ' per '+ pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType;
               }              
            }
            else
            {
                if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Notes)){
                    limitText =  pbeBenAmtLst[index].UnitDetail.Notes;
                } 
            }             
            covDtoObj.limitText = limitText;
            if (string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity)){
                covDtoObj.limitAmount = getFormattedValue(pbeBenAmtLst[index].UnitDetail);
            }
            if (pbeBenAmtLst[index].UnitDetail.QuantityPeriod != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes)){
                covDtoObj.limitComment = pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes;
            }
            covDtoObj.notes = (pbeBenAmtLst[index ].UnitDetail.QuantityPeriod != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes))?pbeBenAmtLst[index].UnitDetail.Notes:'';
        }
    }
    
    /* Method for appending $ or % sign after quantity based of the UnitType
    * <p>
    * @param  ProductBusinessEntityResponseV2_H_HUM.UnitDetail .
    * @return string
    */
    private static string getFormattedValue(ProductBusinessEntityResponseV2_H_HUM.UnitDetail oUnitDetail)
    {
        if (oUnitDetail != null  && string.isNotBlank(oUnitDetail.UnitType) && oUnitDetail.UnitType == VOB_BENEFITSEARCH_DOLLAR_HUM)
		{
        	return VOB_ACCUMS_DOLLAR_SIGN_HUM + oUnitDetail.Quantity;
		}
        else if (oUnitDetail != null  && string.isNotBlank(oUnitDetail.UnitType) && oUnitDetail.UnitType == VOB_BENEFITSEARCH_PERCENTAGE_HUM)
		{
            return oUnitDetail.Quantity + VOB_ACCUMS_PERCENTAGE_SIGN_HUM;																	
		}
        else
		{
            return oUnitDetail.Quantity;
    }
	}
    
    /* Method for parsing of InNetwork response
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalInNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork Values for respective Limits .
    * @return nothing
    */
    private static void prepareInNetworkResponse(Map<string, list<CoverageType_DTO_HUM>> inNetworkMap, Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork, Map<string, list<CoverageType_DTO_HUM>> lstOfLimitsInNetwork)
    {
        CoverageType_DTO_HUM coverageDTO = NULL;
        Map<string, CoverageType_DTO_HUM>  finalInNetworkMap = new Map<string, CoverageType_DTO_HUM>();
        for (string nodeType : inNetworkMap.keyset())
        {
            if (finalInNetworkMap.containsKey(nodeType))
			{
                coverageDTO = finalInNetworkMap.get(nodeType);
			}
            else
			{
                coverageDTO = new CoverageType_DTO_HUM();
                finalInNetworkMap.put(nodeType, coverageDTO);
			}
            if (inNetworkMap.get(nodeType) != null && inNetworkMap.get(nodeType).size() > 0)
            {
                for (CoverageType_DTO_HUM oDto : inNetworkMap.get(nodeType))
                {
                    parseInNetworkMap(oDto, coverageDTO, finalMapofNonLimitInNetwork, lstOfLimitsInNetwork);
                }
            }
        }
    }
    
    /* Method for parsing of InNetwork response
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalInNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork Values for respective Limits .
    * @return nothing
    */
    private static void parseInNetworkMap(CoverageType_DTO_HUM oDto, CoverageType_DTO_HUM coverageDTO, Map<string, list<CoverageType_DTO_HUM>>finalMapofNonLimitInNetwork, Map<string, list<CoverageType_DTO_HUM>>lstOfLimitsInNetwork)
    {
        if(oDto.isLimit == 'false' || oDto.nodeType == 'Service')
        {
            coverageDTO.BenefitDescription = oDto.BenefitDescription;
            coverageDTO.Par = oDto.Par;
            coverageDTO.nodeType = oDto.nodeType;
            coverageDTO.notes = oDto.notes;
            if (finalMapofNonLimitInNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = finalMapofNonLimitInNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                finalMapofNonLimitInNetwork.put(oDto.nodeType, covType);
            }
        }
        if(oDto.isLimit == 'true')
        {
            if (lstOfLimitsInNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = lstOfLimitsInNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                lstOfLimitsInNetwork.put(oDto.nodeType, covType);
            }
        }
    }
    
    /* Method for parsing of OutOfNetwork response
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalOutOfNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork Values for respective Limits .
    * @return nothing
    */
    private static void prepareOutNetworkResponse(Map<string, list<CoverageType_DTO_HUM>>outNetworkMap, Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork, Map<string, list<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork)
    {
        CoverageType_DTO_HUM coverageDTO = NULL;
        Map<string, CoverageType_DTO_HUM> finalOutNetworkMap = new Map<string, CoverageType_DTO_HUM>();
        for (string nodeType : outNetworkMap.keyset())
        {
            if (finalOutNetworkMap.containsKey(nodeType))
			{
                coverageDTO = finalOutNetworkMap.get(nodeType);
			}
            else
			{
                coverageDTO = new CoverageType_DTO_HUM();
                finalOutNetworkMap.put(nodeType, coverageDTO);
			}
            if (outNetworkMap.get(nodeType) != null && outNetworkMap.get(nodeType).size() > 0)
            {
                for (CoverageType_DTO_HUM oDto : outNetworkMap.get(nodeType))
                {
                    parseOutNetworkMap(oDto, coverageDTO, finalMapofNonLimitOutNetwork, lstOfLimitsOutNetwork);
                }
            }
        }
    }
    
    /* Method for parsing of OutOfNetwork response
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for finalOutOfNetwork Values for Non Limits .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for OutOfNetwork Values for respective Limits .
    * @return nothing
    */
    private static void parseOutNetworkMap(CoverageType_DTO_HUM oDto, CoverageType_DTO_HUM coverageDTO, Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork, Map<string, list<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork)
    {
        if(oDto.isLimit == 'false' || oDto.nodeType =='Service')
        {
            coverageDTO.BenefitDescription = oDto.BenefitDescription;
            coverageDTO.NonPar = oDto.NonPar;
            coverageDTO.nodeType = oDto.nodeType;
            coverageDTO.notes = oDto.notes;
            if (finalMapofNonLimitOutNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = finalMapofNonLimitOutNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                finalMapofNonLimitOutNetwork.put(oDto.nodeType, covType);
            }
        }
        if(oDto.isLimit == 'true')
        {
            if (lstOfLimitsOutNetwork.containsKey(oDto.nodeType))
            {
                List<CoverageType_DTO_HUM> covType = lstOfLimitsOutNetwork.get(oDto.nodeType);
                covType.add(oDto);
            }
            else
            {
                List<CoverageType_DTO_HUM> covType = new List<CoverageType_DTO_HUM>();
                covType.add(oDto);
                lstOfLimitsOutNetwork.put(oDto.nodeType, covType);
            }
        }
    }

    /* Method to prepare final Map of Non Limit
    * <p>
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for  In and out Network Values  .
    * @return nothing
    */
    private static void prepareFinalMapNonLimit(Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork, Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork)
    {
        for (string nodeType : finalMapofNonLimitOutNetwork.keyset())
        {
            if (finalMapofNonLimitInOutNetwork.containsKey(nodeType))
            {
                List<CoverageType_DTO_HUM> temCov = finalMapofNonLimitOutNetwork.get(nodeType);
                finalMapofNonLimitInOutNetwork.get(nodeType).addAll(temCov);
            }
            else
            {
                finalMapofNonLimitInOutNetwork.put(nodeType, finalMapofNonLimitOutNetwork.get(nodeType));
            }
        }
    }
    
    /* Method to prepare map when 'Par = Both' is selected for Non limits(where isLimit = false)
    * <p>
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for  In and out Network Values  .
    * @return Map<string,List<CoverageType_DTO_HUM>> Non - limits Map with combined PAR and NonPar values
    */
    private static Map<string, List<CoverageType_DTO_HUM>> prepareBOTHMap(Map<string, List<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork)
    {
        Map<string, List<CoverageType_DTO_HUM>> TempMapInOutNetwork = new Map<string, List<CoverageType_DTO_HUM>>();
        for (string nodeType : finalMapofNonLimitInOutNetwork.keyset())
        {
            List<CoverageType_DTO_HUM> outNetworkCoverageType = new List<CoverageType_DTO_HUM>();
            List<CoverageType_DTO_HUM> inNetworkListCoverageType = new List<CoverageType_DTO_HUM>();
            for (CoverageType_DTO_HUM oCoverageType : finalMapofNonLimitInOutNetwork.get(nodeType))
            {
                CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
                covDtoObj.BenefitDescription = oCoverageType.BenefitDescription;
                covDtoObj.placeOfServiceCode = oCoverageType.placeOfServiceCode;
                covDtoObj.sIsCoveredBenefit = oCoverageType.sIsCoveredBenefit;
                covDtoObj.Par = oCoverageType.Par;
                covDtoObj.NonPar = oCoverageType.NonPar;
                covDtoObj.benefitType = 'Not Covered';

            if(string.isNotBlank(oCoverageType.Par) || oCoverageType.networkType.equalsIgnoreCase('IN'))
                {
                    if ((nodeType == 'Service') && (oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False')))    inNetworkListCoverageType.add(covDtoObj);
                    else inNetworkListCoverageType.add(oCoverageType);
                }
                else if(string.isNotBlank(oCoverageType.NonPar)||oCoverageType.networkType.equalsIgnoreCase('out'))
                {
                    if ((nodeType == 'Service') && (oCoverageType.sIsCoveredBenefit.equalsIgnoreCase('False')))    outNetworkCoverageType.add(covDtoObj);
                    else    outNetworkCoverageType.add(oCoverageType);
                }
            }
            for (CoverageType_DTO_HUM InNetCovType : inNetworkListCoverageType)
            {
                for (CoverageType_DTO_HUM OutNetCovType : outNetworkCoverageType)
                {
                        if(InNetCovType.BenefitDescription == OutNetCovType.BenefitDescription &&  InNetCovType.TierNumber == OutNetCovType.TierNumber && ((InNetCovType.benefitType != 'Limit_Service' && OutNetCovType.benefitType != 'Limit_Service' && InNetCovType.benefitType == OutNetCovType.benefitType) || (InNetCovType.limitName == OutNetCovType.limitName && InNetCovType.benefitType == 'Limit_Service' && OutNetCovType.benefitType == 'Limit_Service')))
                    {
                        if (nodeType == 'Service')
                        {
                            if (InNetCovType.sIsCoveredBenefit.equalsIgnoreCase('True') || OutNetCovType.sIsCoveredBenefit.equalsIgnoreCase('False'))
                            {
                                InNetCovType.NonPar = OutNetCovType.NonPar;
                                OutNetCovType.matched = (InNetCovType.placeOfServiceCode.equalsIgnoreCase(OutNetCovType.placeOfServiceCode))?true:false;
                            }
                            else
                            {
                                OutNetCovType.Par = InNetCovType.Par;
                                InNetCovType.matched = (InNetCovType.placeOfServiceCode.equalsIgnoreCase(OutNetCovType.placeOfServiceCode))?true:false;
                            }
                        }
                        else
                        {
                            InNetCovType.NonPar = OutNetCovType.NonPar;
                            OutNetCovType.matched = true;
                        }
                    }
                }
            }
            for (CoverageType_DTO_HUM OutNetCovType : outNetworkCoverageType)
            {
                if (OutNetCovType.matched == false) inNetworkListCoverageType.add(OutNetCovType);
            }
            TempMapInOutNetwork.put(nodeType, inNetworkListCoverageType);
        }
        return TempMapInOutNetwork;
    }
    
    /* Method to prepare map when 'Par = Both' is selected for limits
    * <p>
    * @param  object of ProductBusinessEntityResponseV2_H_HUM.Product .
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> limits for  In and out Network Values  .
    * @return nothing
    */
    private static void prepareLimitsNetwork(Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork, Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork)
    {
        for (string benefitType : lstOfLimitsOutNetwork.keyset())
        {
            if (lstOfLimitsInOutNetwork.containsKey(benefitType))
            {
                List<CoverageType_DTO_HUM> temCovLst = lstOfLimitsInOutNetwork.get(benefitType);
                temCovLst.addAll(lstOfLimitsOutNetwork.get(benefitType));
            }
            else
            {
                lstOfLimitsInOutNetwork.put(benefitType, lstOfLimitsOutNetwork.get(benefitType));
            }

        }
    }
    public static void getBenefitsPARYes(Map<string, Boolean> isCoveredServiceInNetworks,CoverageType_DTO_HUM covDtoObj,Map<string, List<string>> lstComment ,Map<string, List<CoverageType_DTO_HUM>> lstLimit,  Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork,Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInNetwork)
    {
        if ((isCoveredServiceInNetworks.size() == 1) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_INNETWORK_HUM))))
        {
            if (benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
            benefitsSearchDTOObj.LimitFalseLst.add(covDtoObj);
            benefitsSearchDTOObj.lstOfLimits = lstLimit;
            benefitsSearchDTOObj.lstOfServiceComments = lstComment;
        }
        else
        {
            for (string nodeType : finalMapofNonLimitInNetwork.keyset())
            {
                if (benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                benefitsSearchDTOObj.LimitFalseLst.addAll(finalMapofNonLimitInNetwork.get(nodeType));
            }
            benefitsSearchDTOObj.lstOfLimits = lstOfLimitsInNetwork;
        }
    }
    public static void getBenefitsPARNo(Map<string, Boolean> isCoveredServiceInNetworks,CoverageType_DTO_HUM covDtoObj,Map<string, List<string>> lstComment ,Map<string, List<CoverageType_DTO_HUM>> lstLimit,  Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork,Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsOutNetwork)
    {
        if ((isCoveredServiceInNetworks.size() == 1) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_OUTNETWORK_HUM))))
        {
            if (benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
            benefitsSearchDTOObj.LimitFalseLst.add(covDtoObj);
            benefitsSearchDTOObj.lstOfLimits = lstLimit;
            benefitsSearchDTOObj.lstOfServiceComments = lstComment;
        }
        else
        {
            for (string nodeType : finalMapofNonLimitOutNetwork.keyset())
            {
                if (benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                benefitsSearchDTOObj.LimitFalseLst.addAll(finalMapofNonLimitOutNetwork.get(nodeType));
            }
            benefitsSearchDTOObj.lstOfLimits = lstOfLimitsOutNetwork;
        }
    }
    public static void getBenefitsBoth(Map<string, Boolean> isCoveredServiceInNetworks,CoverageType_DTO_HUM covDtoObj,Map<string, List<string>> lstComment ,Map<string, List<CoverageType_DTO_HUM>> lstLimit,  Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInOutNetwork,Map<string,List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork)
    {
        if ((isCoveredServiceInNetworks.size() == 2) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_OUTNETWORK_HUM))) && (!(isCoveredServiceInNetworks.get(VOB_BENEFITSEARCH_INNETWORK_HUM))))
        {
            if (benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
            benefitsSearchDTOObj.LimitFalseLst.add(covDtoObj);
            benefitsSearchDTOObj.lstOfLimits = lstLimit;
            benefitsSearchDTOObj.lstOfServiceComments = lstComment;
        }
        else
        {
            for (string nodeType : finalMapofNonLimitInOutNetwork.keyset())
            {
                if (benefitsSearchDTOObj.LimitFalseLst == null) benefitsSearchDTOObj.LimitFalseLst = new List<CoverageType_DTO_HUM>();
                benefitsSearchDTOObj.LimitFalseLst.addAll(finalMapofNonLimitInOutNetwork.get(nodeType));
            }
            benefitsSearchDTOObj.lstOfLimits = lstOfLimitsInOutNetwork;
        }
    }

        /* Method for preparing the DTO structure and assigning the Map to DTO objects
    * <p>
    * @param  string selected PAR value .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Non Limit OutOfNetwork .
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Non Limit InNetwork
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Non Limit In and Out Network
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Limits of In Network
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Limits of Out Network
    * @param  Map<string,list<CoverageType_DTO_HUM>> for Limits of In and Out Network
    * @return PBEBenefitsSearch_DTO_HUM object
    */
    public static PBEBenefitsSearch_DTO_HUM prepareResponseStructure(Map<string, Boolean> isCoveredServiceInNetworks, String ParValue, Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsInNetwork, Map<string, List<CoverageType_DTO_HUM>>lstOfLimitsOutNetwork, Map<string, List<CoverageType_DTO_HUM>> lstOfLimitsInOutNetwork, Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitInNetwork, Map<string, list<CoverageType_DTO_HUM>> finalMapofNonLimitOutNetwork, Map<string, list<CoverageType_DTO_HUM>>finalMapofNonLimitInOutNetwork, Map<string, List<string>> lstOfServiceComments)
    {
        CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
        covDtoObj.BenefitDescription = 'Not Covered';
        Map<string, List<CoverageType_DTO_HUM>> lstLimit = new Map<string, List<CoverageType_DTO_HUM>>();
        Map<string, List<string>> lstComment = new Map<string, List<string>>();

        benefitsSearchDTOObj.lstOfServiceComments = (lstOfServiceComments != null && lstOfServiceComments.size() > 0)?lstOfServiceComments:null;

        if (ParValue == VOB_PAR_YES_HUM)
        {
            getBenefitsPARYes(isCoveredServiceInNetworks,covDtoObj,lstComment ,lstLimit,finalMapofNonLimitInNetwork,lstOfLimitsInNetwork);
        }
        else if (ParValue == VOB_PAR_NO_HUM)
        {
            getBenefitsPARNo(isCoveredServiceInNetworks,covDtoObj,lstComment ,lstLimit,finalMapofNonLimitOutNetwork,lstOfLimitsOutNetwork);
        }
        else if (ParValue == VOB_BOTH)
        {
            getBenefitsBoth(isCoveredServiceInNetworks,covDtoObj,lstComment ,lstLimit,finalMapofNonLimitInOutNetwork,lstOfLimitsInOutNetwork);
        }
        return benefitsSearchDTOObj;
    }
    /**
    * Returns ToothHistoryServiceRequest_LH_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for tooth history service .
    * @param    String  Member Id
    * @param    String  Member Dependant Code
    * @return   ToothHistoryServiceRequest_LH_HUM request structure of the tooth history service
    */
    @auraEnabled(cacheable = true continuation=true)
    public static ToothHistoryServiceRequest_LH_HUM RequestToothHistoryService(String sMemberId, String sMemberDependentCode)
    {
        ToothHistoryServiceRequest_LH_HUM oReq = new ToothHistoryServiceRequest_LH_HUM();
        ToothHistoryServiceRequest_LH_HUM.GetToothHistory oGetToothHistory = new ToothHistoryServiceRequest_LH_HUM.GetToothHistory();
        oGetToothHistory.MemberId = sMemberId;
        oGetToothHistory.MemberDependentCode = sMemberDependentCode;
        oReq.GetToothHistory = oGetToothHistory;
        return oReq;
    }
    
    /**
    * This method is to parse the respponse for tooth history service .
    *
    * @param    ToothServiceResponse_LH_HUM Webservice response
    * @return   list of ToothHistory_L_DTO_HUM
    */
    @auraEnabled(cacheable = true continuation=true)
    public static List<ToothHistory_L_DTO_HUM> ParseToothHistoryRes(ToothServiceResponse_LH_HUM Response)
    {
        List<ToothHistory_L_DTO_HUM> lstToothHistoryDTO = new List<ToothHistory_L_DTO_HUM>();
        if (Response != null && Response.GetToothHistoryResponse != null && Response.GetToothHistoryResponse.GetToothHistoryResult != null && Response.GetToothHistoryResponse.GetToothHistoryResult.ClaimHistoryResult != null)
        {
            ToothServiceResponse_LH_HUM.GetToothHistoryResponse oGetToothHistoryResponse = Response.GetToothHistoryResponse;
            ToothServiceResponse_LH_HUM.GetToothHistoryResult oGetToothHistoryResult  = oGetToothHistoryResponse.GetToothHistoryResult;
            List<ToothServiceResponse_LH_HUM.ClaimHistoryResult> lstClaimHistoryResult = oGetToothHistoryResult.ClaimHistoryResult;
            if(oGetToothHistoryResult.ErrorMessage == '' && oGetToothHistoryResult.ErrorType == '')
            {
                for(ToothServiceResponse_LH_HUM.ClaimHistoryResult objHistoryResult : lstClaimHistoryResult)
                {
                    ToothHistory_L_DTO_HUM obj = new ToothHistory_L_DTO_HUM();
                    if(String.IsNotBlank(objHistoryResult.DateOfService))
                    {
                        obj.sDateOfService = objHistoryResult.DateOfService;
                        obj.sDateOfServiceformatted = date.parse(objHistoryResult.DateOfService);
                    }
                    obj.sTooth = String.IsNotBlank(objHistoryResult.Tooth) ? objHistoryResult.Tooth : '';
                    obj.sSurfaceCode = String.IsNotBlank(objHistoryResult.SurfaceCode) ? objHistoryResult.SurfaceCode : '';
                    obj.sADACode = String.IsNotBlank(objHistoryResult.ADACode) ? objHistoryResult.ADACode : '';
                    obj.sDescription = String.IsNotBlank(objHistoryResult.Description) ? objHistoryResult.Description : '';
                    obj.sClaimNumber = String.IsNotBlank(objHistoryResult.ClaimNumber) ? objHistoryResult.ClaimNumber : '';
                    obj.sStatus = String.IsNotBlank(objHistoryResult.Status) ? objHistoryResult.Status : '';   
                    lstToothHistoryDTO.add(obj);
                }
            }else return null;
        }
        return lstToothHistoryDTO;
    }
}