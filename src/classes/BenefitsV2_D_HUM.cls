/*******************************************************************************************************************************
Apex Class Name :  BenefitsV2_D_HUM.cls
Version: 1.0
Created On      : 04/11/2018
Function        : Data Class for BenefitsV2_C_HUM.

Modification Log:
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Pradeepkumar Dani         29802                     02/08/2016                  Original Version
* Avinash Choubey           30467                     02/26/2016                  Added getAccumsCurrentData() method
* Pradeepkumar Dani         30467                     03/08/2016                  Changes for REQ - 237281
* Pradeepkumar Dani         30467                     03/17/2016                  Modified as per review comments
* Vishal Verma              30727                     03/30/2016                  Defect: 221163.0001 - Added code to handle exception
* Rajesh Keswani            24929                     03/31/2016                  Defect: 221233.0001 - Added AsOfDate field in request of GBE
* Pradeepkumar Dani         24929                     04/13/2016                  Added Product node in teh MBE request Defect#221236
* Rajesh Keswani            26063                     04/22/2016                  Added PBE DTO method
* Rajesh Keswani            25869                     04/22/2016                  Added MBE DTO method
* Rajesh Keswani            26120                     04/27/2016                  Added GBE DTO method
* Pradeepkumar Dani         26125                     04/27/2016                  Added variables for Benefit summary section
* Pradeepkumar Dani         26247                     04/28/2016                  Added defect#224489 fix
* Vishal Verma              26316                     04/29/2016                  Added Mapping for Benefit Summary Second Pass
* Vishal Verma              26826                     04/29/2016                  REQ 184827 : Method to call PBE Servie from Benefits Search
* Pradeepkumar Dani         26877                     05/03/2016                  Added GeneralBenefits to the PBE request
* Vishal Verma              27384                     05/12/2016                  Defect : 225825.0001 - Updated the mapping of GBE service request for SubgroupId and BenefitsSequenceNumber
* Shreya Choodamani         27205                     05/09/2016                  REQ 262414 : Added variable for rendering the Mentor Link on Benefit Search Component
* Avinash Kumar Choubey     27205                     05/09/2016                  extarct OOP and BenefitIndicator from PBE response for REQ 237280
* Pradeepkumar Dani         27609                     05/18/2016                  Added asOfDate logic to MBE, PBE and GBE
* Rajesh Keswani            27613                     05/17/2016                  Policy Rider changes.
* Rajesh Keswani            27613                     05/19/2016                  Added method to get for platform value
* Vishal Verma              28671                     06/01/2016                  Defect# 228424 - Modified Render condition on OSB/MSB Link
* Avinash Kumar Choubey     29936                     06/01/2016                  extarct Coinsurance, CoPayOfficeVisit, SuperMoop from PBE response for REQ 269949
* Avinash Kumar Choubey     29936                     06/11/2016                  Extarct RxDetail from PBE response for Defect 229256,229257
* Vishal Verma              30776                     06/10/2016                  REQ:269910 - Update Mapping for PBE and GBE
* Rajesh Keswani            30776                     06/09/2016                  REQ: 224050 - Implemented Benefit Search logic
* Rajesh Keswani            31517                     06/30/2016                  Defect# 231798 changed logic for LimitName field
* Pradeepkumar Dani         31811                     07/04/2016                  Added changes for standard values for logging - Defect#230623.0001
* Rajesh Keswani            31996                     07/06/2016                  changed logic for Place of service field
* Pradeepkumar Dani         32373,32703               07/11/2016                  Updated/Removed standard values for logging & Created By updated to map USERID - Defect#232935.000
* Rajesh Keswani            33540                     07/21/2016                  Defect Fix 234757.0001
* Rajesh Keswani            33842                     07/27/2016                  REQ - 277160 -  appending TierNumber In description field
* Rajesh Keswani            33923                     07/28/2016                  REQ - 269935 -  Added Service Comments for the new child table
* Apoorv Jain                                         08/25/2016                  REQ - 269936 : VOB - Mentor Link for All segments
* Rajesh Keswani            39527                     09/15/2016                  Implemented REQ - 282318 adding Notes column in benefits search table.
* Praveen Kumar Parimi      56654                     01/12/2017                  Implemented REQ - 290282 Removal of Tier from the benefit description.
* Praveen Kumar Parimi      58990                     01/27/2017                  Implemented fix for #257266.0001 .
* Gargey Sharma             82304                     04/06/2017                  REQ - 306338: Updated as per CTCI data model changes
* Kritika Popat             110227                    06/29/2017                  REQ - 299434: Updated to handle when network type is not covered
* Prasanthi Kandula         110227                    07/12/2017                  REQ - 322564: Benefit search- updated logic to handle Par/Non Par selection if isCoveredService network type is returned false from PBE
* Kritika Popat             112848                    07/25/2017                  REQ - 320577 Blended coverage
* Prasanthi Kandula         112848                    07/25/2017                  REQ - 320577 Blended coverage
* Rajesh Keswani            109089                    07/24/2017                  REQ - 299431 Making Place of Service drop down dynamic
* Avinash Choubey           109914                    07/24/2017                  REQ - 300751 Making Par Code” drop down dynamic(for CAS Members) and Sorted
* Santhi Mandava            85904                     04/20/2017                  Implemented requirement 300121 - Merged Rolledback code
* Kritika Popat             121425                    08/21/2017                  REQ - 326924 MF2 changes to accommodate MF8 service work (Defect 305971.0001)
* Prasanthi Kandula         121425                    08/21/2017                  REQ - 326924 MF2 changes to accommodate MF8 service work (Defect 305971.0001)
* Rajesh keswani            129626                    09/18/2017                  REQ - 329669; changing plan option mapping to defination description.
* Raja Sekhar Reddy M V     132082                    09/22/2017                  REQ - 319111 Added code for sending MemberPolicyRolePlatformPointerList.
* Sumit Pharande            162950                    01/09/2018                  Upgraded to MBE Plus
* Melkisan Selvaraj         220187                    04/11/2018                  Cloned from BenefitsV1_VF_HUM for REQ - 360140
* Rajesh Keswani                                      04/18/2018                  REQ - 360142; Update Benefits Service and Data Layer classes for PBE Second call
* Rajesh Keswani   			229751	      			  05/15/2018                  REQ - 361224; Adding ADA code in Benefits search section
* Vishal Verma				229766        			  05/16/2018                  REQ - 355782 : Add Dental Paid Thru Date to Benefits and Accums Summary section  
* Sharan Shanmugam                                    05/16/2018                  REQ - 357347 : Missing Elements – Benefit Summary – Medical Member 
* Joel George										  07/02/2018				  REQ 366156
* Joel George				269661					  08/03/2018				  Changes made as part of REQ 367753, 362122 & 363574 
* Anjali Kumar                                        08/09/2018                  REQ 363570 - Update benefit search filter for both/default
* Lakshmi Madduri			268842					  8/14/2018					  REQ - 363357 Added causecodeList
* Melkisan Selvaraj			271010					  08/06/2018				  REQ - 362101 To auto populate benefits grid
* Anjali Kumar										  09/07/2018			      REQ 374811 - Display only ADA Code dropdown options
* Prudhvi Pamarthi                                    02/18/2019                  REQ - 361155 - Not allowing case creation on Legacy Deleted policies
* Dinesh Subramaniyan				       			  04/18/2018		  	      REQ - 396416 - Modify logic for CAS benefit grids to auto-populate.
* Praveen Kumar Parimi      353724                    04/29/2019                  REQ - 396387 aka CA 7947277 Fixing the Plan option for Dental policies.
* Deepankar Binjola         48282                     09/19/2019                 REQ - 402985 Service Catalog IMS form to prepopulate with the plan information
* Sumit Pharande                                      02/10/2019                 REQ - 722934  [SF] - [CRM Service Error Trending (SFDC)]
* Yogesh Gupta										  12/03/2019				 REQ - 410445 Azure - 731139 Add Ada code column on UI for Dental policy
* Shailesh Mali										  02/18/2020				 REQ - 732113 Service Type Comments ordering - Dental only
* Suraj Patil                                         05/04/2020                  REQ - 737881 - MF 4 - Add Dental Paid Thru Date for HPS Members
* Sayali Gavane									 	  11/25/2020				 REQ - 1457838 - PR00095581 - MF 53 - Out of Area Indicator for CRM
* Prasanthi Kandula                                   03/25/2021                 US:1437221 Displaying Paid Thru Date for DEN and MED Benefit Summary     
* Rahul Chaurasia									  04/19/2021				 1895456 - T1PRJ0002736 - 2021 MF6  - CR AEP readiness - Low Security Issues 
* Suraj Patil										  05/07/2021				  DF 3003 and DF 3001 in Qtest
* Suraj Patil                              		      05/31/2021			      USER STORY 1867803 T1PRJ0003278/PR00095917 - MF01 - Tooth History Data Fields
*********************************************************************************************************************************/
public with sharing class BenefitsV2_D_HUM
{
Public Static final String HYPHEN_HUM = '-';
    Public Static final String BACKSLASH_HUM = '/';
    Public Static final String MAX_DATE_HUM = '12/31/9999';
    Public Static final String  VOB_CURRENT_HUM = 'Current';
    Public Static final String  VOB_PAST_HUM = 'Past';
    Public Static final String  VOB_FUTURE_HUM = 'Future';
    Public Static final String  VOB_PLATFORM_EM_HUM = 'EM';
    Public Static final String  VOB_PLATFORM_LV_HUM = 'LV';
    Public Static final String  VOB_BENEFITPACKAGEID_HUM = 'BenefitPackageId';
    Public Static final String  VOB_VALUEPDQ_HUM = 'PDQ';
    Public Static final String  VOB_MASTERSERVICES_HUM = 'MasterServices';
    Public Static final String  VOB_GENERALBENEFITS_HUM = 'GeneralBenefits';
    Public Static final String  VOB_RIDERS_HUM = 'Riders';
    Public Static final String  VOB_BOTH = 'Both'; 
    Public Static final String VOB_PAR_NO_HUM = 'No';
    Public Static final String  VOB_RXDETAIL_HUM = 'RxDetail'; 
    Public Static final String  VOB_BENEFITS = 'BENEFITS';
    Public Static final String  VOB_GENKEY_KEYTYPE = 'ODS_GENKEY'; 
    Public Static final String  VOB_PLATFORM_MTV_HUM = 'MTV';
    Public Static final String  VOB_PLATFORM_CI_HUM = 'CI';
    Public Static final String  VOB_PLATFORM_CAS_HUM = 'CAS';
    Public Static final String VOB_PBESERVICEDETAIL_HUM = 'ServicesWithPlaceOfService';
    Public Static final String  VOB_PLANCOMMENTS_HUM = 'PlanComments';
    Public Static final String VOB_PAR_DEFAULT_HUM = 'Par/Non Par/Both';
    Public Static final String VOB_PARCODE_DEFAULT_HUM = 'Par Code';
    Public Static final String  VOB_ACTIVEMEMBERSTATUS_HUM = 'Active';
    Public Static final String VOB_BENEFITSEARCH_SERVICELIST_HUM = 'Service';
    Public Static final String VOB_PAR_YES_HUM = 'Yes';
    Public Static final String VOB_BENEFITSEARCH_INNETWORK_HUM = 'InNetwork';
    Public Static final String VOB_BENEFITSEARCH_OUTNETWORK_HUM = 'OutOfNetwork';
    Public Static final String VOB_ACCUMS_DOLLAR_SIGN_HUM = '$';
    Public Static final String VOB_BENEFITSEARCH_PERCENTAGE_HUM = 'Percentage';
    Public Static final String VOB_ACCUMS_PERCENTAGE_SIGN_HUM = '%';
    Public Static final String PAIDTHROUGHDATE_RATELEVEL_HUM = 'Account';
    Public Static final String PAIDTHROUGHDATE_TRANSACTIONTYPE_HUM = 'p';
    Public Static final String PAIDTHROUGHDATE_CALLINGSYSTEMID_HUM = 'CRMS';
    Public Static final String PAIDTHROUGHDATE_USERIDTYPE_HUM = 'INTL';
    Public Static final String PAIDTHROUGHDATE_IDTYPE_HUM = 'CONTRID';
    Public Static final String PAIDTHROUGHDATE_NUMBEROFTRANSACTIONS_HUM = '1';
    Public Static final String  VOB_MEDICARE ='RSO- Medicare';
    Public Static final String sHiphan = '-';
    Public Static final String sMTV_Value = 'MTV';
    Public Static final String sLV_Value= 'LV';
    Public Static final String sCI_Value = 'CI';
    Public Static final String sCAS_Value = 'CAS';
    Public Static final String sEM_Value= 'EM';
    Public Static final String SUBGROUP_MENTORREDIRECT_HUM = 'subgroup:';
    Public Static final String HUM_PARTD_DRUGREQUESTOR = 'CRM';
    Public Static final String MED = 'MED';
    public static final string PaidThroughDate = 'PaidThruDate';
    Public Static final String VOB_BENEFITSEARCH_DOLLAR_HUM = 'Dollar';
    
 	/**
	* Returns Policy_Member__C object that can then be utilized for showing Benefits Information.    *
	* This method returns SOQL result of policy member.
	*
	* @param    benefitsCls         instance of the Controller class.
	* @return  Policy_Member__c    return the policy member object 
	*/
    public Policy_Member__C getPolicyMember_SOQL(BenefitsV2_C_HUM benefitsCls, string policyMemberId)
	{
		try
		{
			
			return[Select Id,ETL_Record_Deleted__c, Source_Coverage_ID__c, Policy__r.Product__r.Name, Name, Policy__c, Status__c, Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode, Policy__r.Major_LOB_Frm__c,Policy__r.Major_LOB__c, Member__r.Enterprise_Id__c,
				Policy__r.Group_Name__r.Source_Platform_Code__c, Policy__r.Source_Cust_Cov_Key__c, Policy__r.Coverage_Plan_Effective_Date__c, Policy__r.Coverage_Plan_End_Date__c,
				Policy__r.Benefit_Coverage__c, Policy__r.Policy_External_ID__c, Policy__r.Platform__c, Policy__r.Product__r.Sold_Product_Key_Value__c, Policy_Platform__c, Policy__r.Contract_Number__c, Policy__r.PBP_Code__c, Product__c, Product_Type__c, Member__r.personmailingpostalcode, ASO__c,Group_Number__c,Product_Type_Code__c, Exchange_type__c
				From Policy_Member__c Where Id = :PolicyMemberID];
		}
		catch (Exception ex)
		{
			//As DML is not currently allowed , set the boolean flag to stop insertion 
			HUMExceptionHelper.bRestrictLog = true;
			benefitsCls.lstLogErrors = HUMExceptionHelper.logErrors(ex, 'BenefitsV2_D_HUM', 'getPolicyMember_SOQL');
			return null;
		}
	}

	/**
	* Returns Policy_Plan_Details__c object that can then be utilized for showing Benefits Information.  *
	* This method returns SOQL result of policy member.
	*
	* @param   benefitsCls               instance of the Controller class.
	* @return  Policy_Plan_Details__c    return the policy plan detail object
	*/
	public Policy_Plan_Details__c getPolicyPlan_SOQL(BenefitsV2_C_HUM benefitsCls)
	{
		try
		{
			List<Policy_Plan_Details__c> objPolicyPlan = [Select Id, Benefit_Coverage__c, Coverage_Plan_Effective_Date__c, Coverage_Plan_End_Date__c, Product__r.Sold_Product_Key_Value__c
				FROM Policy_Plan_Details__c WHERE Policy__c = :benefitsCls.objPolMem.Policy__c AND ETL_Record_Deleted__c = false];
			if (!objPolicyPlan.isEmpty())
			{
				if (objPolicyPlan.size() == 1)
				{
					return objPolicyPlan[0];
				}
				else
				{
					String asOfDateUnformatted = populateAsOfDate(benefitsCls.objPolMem);
					
					String sAsofDate = asOfDateUnformatted.split(HYPHEN_HUM)[1] + BACKSLASH_HUM + asOfDateUnformatted.split(HYPHEN_HUM)[2] + BACKSLASH_HUM + asOfDateUnformatted.split(HYPHEN_HUM)[0];
					for (Policy_Plan_Details__c objPlan : objPolicyPlan)
					{
						if (MAX_DATE_HUM == objPlan.Coverage_Plan_End_Date__c || (MAX_DATE_HUM != objPlan.Coverage_Plan_End_Date__c &&
							(Date.parse(objPlan.Coverage_Plan_Effective_Date__c) <= Date.Parse(sAsofDate) && Date.parse(sAsofDate) <= Date.parse(objPlan.Coverage_Plan_End_Date__c))))
						{
							return objPlan;
						}
					}
				}

			}
		}
		catch (Exception ex)
		{
			//As DML is not currently allowed , set the boolean flag to stop insertion 
			HUMExceptionHelper.bRestrictLog = true;
			benefitsCls.lstLogErrors = HUMExceptionHelper.logErrors(ex, 'BenefitsV2_D_HUM', 'getPolicyPlan_SOQL');
		}
		return null;
	}

    /**
    * Returns MBEPlusRequest_DTO_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for MBE service .
    *
    * @param    PolMemobj   an policy member record object derived from SOQL results.
    * @return  MBEPlusRequest_DTO_HUM      request structure of the wrapper class MBEServiceRequest_H_HUM
    */
    public static MBEPlusRequest_DTO_HUM RequestStructureMBEService(Policy_Member__c PolMemobj)
    {
        try
        {
            MBEPlusRequest_DTO_HUM oReq = new MBEPlusRequest_DTO_HUM();
            MBEPlusRequest_DTO_HUM.GetMember getMember = new MBEPlusRequest_DTO_HUM.GetMember();
            
            MBEPlusRequest_DTO_HUM.Member   mem = new MBEPlusRequest_DTO_HUM.Member();
            MBEPlusRequest_DTO_HUM.IndicatorList indList = new MBEPlusRequest_DTO_HUM.IndicatorList();
            MBEPlusRequest_DTO_HUM.PlatformPointerList PlatformPointerList = new MBEPlusRequest_DTO_HUM.PlatformPointerList();
            mem.IndicatorList = indList;
            mem.PlatformPointerList = PlatformPointerList;
			mem.SoldProductList = createSoldProductList();
            mem.GetCriteria   = createGetCriteriaList(PolMemobj);
            getMember.Member = mem;
            oReq.GetMember = getMember;
            return oReq;
            
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'BenefitsV2_D_HUM', 'RequestStructureMBEService');
            return null;
        }
    }

    /*
    * Method to create structure of Person Criteria that would be passed in tthe request
    * @paramString : End Date of Policy member
    * @return MBEPlusRequest_DTO_HUM.GetCriteria Structure for Get Criteria List
    */
    
    private static MBEPlusRequest_DTO_HUM.GetCriteria createGetCriteriaList(Policy_Member__c PolMemobj)
    {
        MBEPlusRequest_DTO_HUM.GetCriteria  getCriteriaList = new MBEPlusRequest_DTO_HUM.GetCriteria();
        MBEPlusRequest_DTO_HUM.PersonIdCriteria  personIdCriteria  = new MBEPlusRequest_DTO_HUM.PersonIdCriteria();
        personIdCriteria.PersonId = PolMemobj.Member__r.Enterprise_Id__c;
        MBEPlusRequest_DTO_HUM.Period per = new MBEPlusRequest_DTO_HUM.Period();
        
	List<String> lstper = new List<String>{ VOB_CURRENT_HUM, VOB_PAST_HUM,VOB_FUTURE_HUM };
        per.Period = lstper;
        personIdCriteria.PeriodList = per;
        personIdCriteria.AsOfDate = populateAsOfDate(PolMemobj);
        getCriteriaList.PersonIdCriteria  = personIdCriteria;
        return getCriteriaList;
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
    

	/**
	* Returns GBEServiceRequest_H_HUM object that can then be utilized for sending the webservice request.
	* This method is to create the request Structure for GBE service .
	*
	* @param    PolMemobj   an policy member record object derived from SOQL results.
	* @param    string  refdate.
	* @return  GBEServiceRequest_H_HUM      request structure of the wrapper class GBEServiceRequest_H_HUM
	*/
	public static GBEServiceRequest_H_HUM RequestStructureGBEService(Policy_Member__c PolMemobj, Policy_Plan_Details__c polplan, string refDate)
	{
		try
		{
			GBEServiceRequest_H_HUM request = new GBEServiceRequest_H_HUM();
			string refDatePopulated = populateAsOfDate(PolMemobj);
			string refDateParam = refDatePopulated <> null ? refDatePopulated.split(HYPHEN_HUM)[1] + BACKSLASH_HUM + refDatePopulated.split(HYPHEN_HUM)[2] + BACKSLASH_HUM + refDatePopulated.split(HYPHEN_HUM)[0] : '';
			if (PolMemobj != null && PolMemobj.policy__c != null && PolMemobj.policy__r.Group_Name__c != null && String.isNotBlank(PolMemobj.policy__r.Group_Name__r.source_platform_code__c))
			{
				request = prepareGBERequest(PolMemobj, polplan, refDateParam);
			}
			return request;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'BenefitsV2_D_HUM', 'RequestStructureGBEService');
			return NULL;
		}
	}

	/*
	* Returns ProductBusinessEntityRequestV2_H_HUM request
	* <p>
	* Method to prepare PBE request
	*
	* @param  cInstance BenefitsV2_C_HUM Class Instance .
	* @return ProductBusinessEntityRequestV2_H_HUM Request structure for PBE
	*/
	public static ProductBusinessEntityRequestV2_H_HUM RequestStructurePBEService(BenefitsV2_C_HUM cInstance)
	{
		try
		{
			ProductBusinessEntityRequestV2_H_HUM oRequest = new ProductBusinessEntityRequestV2_H_HUM();
			ProductBusinessEntityRequestV2_H_HUM.GetProduct productNode = new ProductBusinessEntityRequestV2_H_HUM.GetProduct();
            
			boolean sServiceTypeCommentSwitch = false;            
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');
			
			//newly added for  PBE18.08
            if(sServiceTypeCommentSwitch == true)
                productNode.SubVersion = '3';
            else
                productNode.SubVersion = '2';
				
			productNode.AsOfDate = populateAsOfDate(cInstance.objPolMem);
			ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier PrId = new ProductBusinessEntityRequestV2_H_HUM.ProductIdentifier();
			PrId.ProductId = (cInstance.objPolPlan.Product__r.Sold_Product_Key_Value__c != NULL ? cInstance.objPolPlan.Product__r.Sold_Product_Key_Value__c :
				cInstance.objPolMem.Policy__r.Product__r.Sold_Product_Key_Value__c);
			if (cInstance.objPolMem.policy__r.Group_Name__r.Source_Platform_Code__c == VOB_PLATFORM_EM_HUM)
			{
				PrId.ProductIdType = VOB_BENEFITPACKAGEID_HUM;
			}
			else if (cInstance.objPolMem.policy__r.Group_Name__r.Source_Platform_Code__c == VOB_PLATFORM_LV_HUM)
			{
				PrId.ProductIdType = VOB_VALUEPDQ_HUM;
			}
			productNode.ProductIdentifier = PrId;
			ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail addDetails = new ProductBusinessEntityRequestV2_H_HUM.IncludeAdditionalDetail();
			addDetails.MasterServicesWithPOS = 'true';
			addDetails.MasterServicesWithParCode = 'true';
			if (string.isNotBlank(cInstance.objPolMem.Policy__r.Product__r.productcode) && cInstance.objPolMem.Policy__r.Product__r.productcode.toLowerCase() == 'den')
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
			HUMExceptionHelper.logErrors(ex, 'BenefitsV2_D_HUM', 'RequestStructurePBEService');
			return NULL;
		}
	}

	/**
	* Returns request structure with request body
	* <p>
	* Method to prepare GBE request
	*
	* @param  Policy_Member__c record .
	* @param  String As of Date .
	* @return GBEServiceRequest_H_HUM
	*/
	@TestVisible private static GBEServiceRequest_H_HUM prepareGBERequest(policy_member__c PolMemobj, Policy_Plan_Details__c popPlan, String AsOfDateValue)
	{
		GBEServiceRequest_H_HUM oRequest = new GBEServiceRequest_H_HUM();
		try
		{
			if (PolMemobj != null && PolMemobj.policy__c != null && String.isNotBlank(PolMemobj.policy__r.source_cust_cov_key__c))
			{
				GBEServiceRequest_H_HUM.GroupDetailDTO grpDetailDTO = new GBEServiceRequest_H_HUM.GroupDetailDTO();
				grpDetailDTO.GroupId = PolMemobj.policy__r.source_cust_cov_key__c.substring(0, 6);
				if (PolMemobj.policy__r.Group_Name__r.source_platform_code__c == VOB_PLATFORM_LV_HUM)
				{
					grpDetailDTO.SubGroupId = PolMemobj.policy__r.source_cust_cov_key__c.substring(9, 12);
					grpDetailDTO.BenefitPackageId = '';
					grpDetailDTO.BenefitSequenceNumber = PolMemobj.policy__r.source_cust_cov_key__c.substring(6, 9);
				}
				else if (PolMemobj.policy__r.Group_Name__r.source_platform_code__c == VOB_PLATFORM_EM_HUM)
				{
					String[] str = PolMemobj.policy__r.source_cust_cov_key__c.split(' ');
					grpDetailDTO.SubGroupId = str[0];
					grpDetailDTO.BenefitPackageId = popPlan != NULL ? popPlan.benefit_coverage__C : PolMemobj.policy__r.benefit_coverage__C;
					grpDetailDTO.BenefitSequenceNumber = '';
				}
				if (PolMemobj.policy__r.Group_Name__c != null) grpDetailDTO.Platform = PolMemobj.policy__r.Group_Name__r.source_platform_code__c;
				if (PolMemobj.policy__c != null)    grpDetailDTO.StartDate = popPlan != NULL ? popPlan.Coverage_Plan_Effective_Date__c : PolMemobj.policy__r.Coverage_Plan_Effective_Date__c;
				if (PolMemobj.policy__c != null)    grpDetailDTO.EndDate = popPlan != NULL ? popPlan.Coverage_Plan_End_Date__c : PolMemobj.policy__r.Coverage_Plan_End_Date__c;

				grpDetailDTO.InclusionCriteria = VOB_BENEFITS;
				grpDetailDTO.AsOfDate = AsOfDateValue;
				oRequest.GroupDetailDTO = grpDetailDTO;
			}
			return oRequest;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_D_HUM', 'prepareGBERequest');
			return Null;
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
				if (objGetProductResponse != null)
				{
					ProductBusinessEntityResponseV2_H_HUM.ProductList objProductList = objGetProductResponse.ProductList;
					if (objProductList != null && objProductList.Product != null && objProductList.Product.size() > 0)
					{
						ProductBusinessEntityResponseV2_H_HUM.Product objProduct = objProductList.Product[0];
						if (objProduct != null)
						{
							ProductBusinessEntityResponseV2_H_HUM.Detail objDetail = objProduct.Detail;
							if (objProduct != null)
							{
								PBEDtoObj = parsePBEData(objDetail, objProduct.MetaData.PlatformCode);
								ProductBusinessEntityResponseV2_H_HUM.Definition objDefinition = objProduct.Definition;
								if(objDefinition != null && string.isNotBlank(objDefinition.Description)) 
                                {
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
                               }
							   // We are fetching PlanId on the basis of ProductIDType as "PdbPlanId"
							   If(objProduct.Definition != null && objProduct.Definition.IdentifierList != null && objProduct.Definition.IdentifierList.ProductIdentifier != null)
                               {
									List<ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier> lstProductIdentifier = objProduct.Definition.IdentifierList.ProductIdentifier;           
									for(ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier objProductIdentifier : lstProductIdentifier )
									{
									   if(string.isNotBlank(objProductIdentifier.ProductIdType) && objProductIdentifier.ProductIdType.equalsIgnoreCase('PdbPlanId'))
										PBEDtoObj.pbePlanId= string.isNotBlank(objProductIdentifier.ProductId) ? objProductIdentifier.ProductId : '';
									}
								}							   
							}
						}
					}
				}
			}
			return PBEDtoObj;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_D_HUM', 'getPBEDTOData');
			return NULL;
		}
	}

	/*
	* Returns PBEResponseV2_DTO_HUM object
	* <p>
	* Method to parse and prepare PBE DTO to send to UI
	*
	* @param  ProductBusinessEntityResponseV2_H_HUM.Detail object.
	* @return PBEResponseV2_DTO_HUM
	*/
	private static PBEResponseV2_DTO_HUM parsePBEData(ProductBusinessEntityResponseV2_H_HUM.Detail objDetail, String platformCode)
	{
		PBEResponseV2_DTO_HUM PBEDtoObj = new PBEResponseV2_DTO_HUM();
		if (PlatformCode != null)  PBEDtoObj.PlatformCode = platformCode;
		if (objDetail.PackageInfo != null) PBEDtoObj.PackageInfo = objDetail.PackageInfo;
		if (objDetail.ServiceTypeList != null) PBEDtoObj.ServiceTypeList = objDetail.ServiceTypeList;
		if (objDetail.ServiceCategoryList != null) PBEDtoObj.ServiceCategoryList = objDetail.ServiceCategoryList;
		if (objDetail.PlaceOfServiceList != null) PBEDtoObj.PlaceOfServiceList = objDetail.PlaceOfServiceList;
		if (objDetail.ParcodeList != null) PBEDtoObj.ParcodeList = objDetail.ParcodeList;
		if (objDetail.RxDetail != null) PBEDtoObj.RxDetail = objDetail.RxDetail;
		if (objDetail.CauseCodeList != null) PBEDtoObj.CauseCodeList = objDetail.CauseCodeList;

		ProductBusinessEntityResponseV2_H_HUM.GeneralBenefits objGeneralBenefits = objDetail.GeneralBenefits;
		if (objDetail.RiderList != null) PBEDtoObj.RiderList = objDetail.RiderList;
		if (objGeneralBenefits != null)
		{
			if (objGeneralBenefits.Deductible != null) PBEDtoObj.Deductible = objGeneralBenefits.Deductible;
			if (objGeneralBenefits.OutOfPocket != null) PBEDtoObj.OutOfPocket = objGeneralBenefits.OutOfPocket;
			if (objGeneralBenefits.MaxOutOfPocket != null) PBEDtoObj.MaxOutOfPocket = objGeneralBenefits.MaxOutOfPocket;
			if (objGeneralBenefits.BenefitIndicatorList != null) PBEDtoObj.BenefitIndicatorList = objGeneralBenefits.BenefitIndicatorList;
			if (string.isNotBlank(objGeneralBenefits.IsHDHP)) PBEDtoObj.IsHDHP = objGeneralBenefits.IsHDHP;
			if (objGeneralBenefits.CoInsurance != null) PBEDtoObj.CoInsurance = objGeneralBenefits.CoInsurance;
			if (objGeneralBenefits.SuperMoop != null) PBEDtoObj.SuperMoop = objGeneralBenefits.SuperMoop;
			if (objGeneralBenefits.CopayOfficeVisit != null) PBEDtoObj.CopayOfficeVisit = objGeneralBenefits.CopayOfficeVisit;
			if (objGeneralBenefits.Limitation != null) PBEDtoObj.Limitation = objGeneralBenefits.Limitation;
		}
		return PBEDtoObj;
	}

    /*
    * Returns MBEResponse_DTO_HUM object
    * <p>
    * Method to prepare MBE DTO to send to UI
    *
    * @param  MBEPlusResponse_DTO_HUM object.
    * @param  policy_member__c Record.
    * @return MBEResponseV1_DTO_HUM
    */
    public static MBEResponseV1_DTO_HUM getMBEDTOData(MBEPlusResponse_DTO_HUM oMBEResponse, Policy_Member__c policyObject, BenefitsV2_C_HUM benefitsCls)
    {
        MBEResponseV1_DTO_HUM dtoObjClass = NULL;
        try
        {
            if (oMBEResponse != null)
            {
                MBEPlusResponse_DTO_HUM.GetMemberResponse MBEresponse = oMBEResponse.GetMemberResponse;
                MBEPlusResponse_DTO_HUM.Body body = MBEresponse.body;
                MBEPlusResponse_DTO_HUM.Member member = Body.member;
                dtoObjClass = getMemGenKey(member);
                if (MBEresponse != null)
                {
                    MBEPlusResponse_DTO_HUM.Body objBody = MBEresponse.Body;
                    if (objBody != null)
                    {
                       MBEPlusResponse_DTO_HUM.SoldProductList  soldproductListObj = objBody.Member.SoldProductList;
                       if (soldproductListObj != null && soldproductListObj.SoldProduct != null && soldproductListObj.SoldProduct.size() > 0)
                        {
                            dtoObjClass = parseSolProListDTO(oMBEResponse, dtoObjClass, policyObject, benefitsCls);
                        }
                    }
                }
            }
            return dtoObjClass;
        }
        catch (Exception e)
        {
            HUMExceptionHelper.logErrors(e, 'BenefitsV2_D_HUM', 'getMBEDTOData');
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
    private static MBEResponseV1_DTO_HUM getMemGenKey(MBEPlusResponse_DTO_HUM.Member memb)
    {
        MBEResponseV1_DTO_HUM dtoObjClass = new MBEResponseV1_DTO_HUM();
        if(memb.SoldProductList!= null){
            MBEPlusResponse_DTO_HUM.SoldProductList objSoldProductList = memb.SoldProductList;
            if(objSoldProductList.SoldProduct!= null) {
                List<MBEPlusResponse_DTO_HUM.SoldProduct> listObjSoldProduct =  objSoldProductList.SoldProduct;
                if(listObjSoldProduct.size()!=0){
                    MBEPlusResponse_DTO_HUM.InsuranceProduct insPro =  listObjSoldProduct[0].InsuranceProduct;
                    if(insPro != null){
                        MBEPlusResponse_DTO_HUM.SoldProductDetailList objSoldProductDetaillist =  insPro.SoldProductDetailList;
                        List<MBEPlusResponse_DTO_HUM.SoldProductDetail> objSoldProductDetail = objSoldProductDetaillist.SoldProductDetail;
                        
                       For(MBEPlusResponse_DTO_HUM.SoldProductDetail inscov :objSoldProductDetail)
                        {
                            MBEPlusResponse_DTO_HUM.InsuranceCoverage objInsuranceCoverage = inscov.InsuranceCoverage;
                       
                            if(objInsuranceCoverage.Network!= null)
                            {
                                dtoObjClass.Network = objInsuranceCoverage.Network;
                            }    
                        } 
                    }
                }
            }
		}
        dtoObjClass.IndicatorList = memb.IndicatorList;
        MBEPlusResponse_DTO_HUM.PlatformPointerList  platFormObj = memb.PlatformPointerList ;
         if (platFormObj != null && platFormObj.PlatformProxyKey  != null && platFormObj.PlatformProxyKey.size() > 0)
        {
            for (MBEPlusResponse_DTO_HUM.PlatformProxyKey  memberGenKeyObj : platFormObj.PlatformProxyKey )
            {
                
		if (memberGenKeyObj != null && String.isNotBlank(memberGenKeyObj.KeyType) && memberGenKeyObj.KeyType == VOB_GENKEY_KEYTYPE && String.isNotBlank(memberGenKeyObj.KeyValue))
                {
                    dtoObjClass.memberGenKey = memberGenKeyObj.KeyValue;
                }
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
    * @param  policy_member__c Record.
    * @return MBEResponseV1_DTO_HUM
    */
     private static MBEResponseV1_DTO_HUM parseSolProListDTO(MBEPlusResponse_DTO_HUM oResponse, MBEResponseV1_DTO_HUM dtoObjClass, Policy_Member__c policyObject, BenefitsV2_C_HUM benefitsCls)
    {
        Boolean bValidNodeFound = false;
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
            if (soldpro != null && soldpro.InsuranceProduct.SoldProductDetailList  != null)
            {
                MBEPlusResponse_DTO_HUM.SoldProductDetailList listsoldProductDetail = soldpro.InsuranceProduct.SoldProductDetailList;
                if (listsoldProductDetail != null && listsoldProductDetail.SoldProductDetail!= null &&listsoldProductDetail.SoldProductDetail.size()> 0)
                {
                    for (MBEPlusResponse_DTO_HUM.SoldProductDetail  soldProductDetailtemp: soldpro.InsuranceProduct.SoldProductDetailList.SoldProductDetail)
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
						
                        if (isValidMBENode(soldProductDetailtemp, policyObject)&&soldpro.InsuranceProduct.ProductLineCode == policyObject.Policy__r.Product__r.productCode)
                        { 
                            bValidNodeFound = true;
                            if (soldProductDetailtemp.InsuranceCoverage != null)
                            {
                                dtoObjClass = createMemPolicyDTO(soldProductDetailtemp.InsuranceCoverage, dtoObjClass);
                            }
                            break;
                        }
                    }
                }
            }
            
            if (bValidNodeFound)
            {       
                break;
            }
        }
   }
        return dtoObjClass;
    }
     
    /*
    * Returns MBEResponseV1_DTO_HUM object
    * <p>
    * Method to prepare MBE Policy DTO to send to UI
    *
    * @param  MBEPlusResponse_DTO_HUM.InsuranceCoverage object.
    * @param  MBEResponseV1_DTO_HUM object.
    * @return MBEResponseV1_DTO_HUM
    */
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


    /*
    * Returns boolean object
    * <p>
    * Method to identify the correct node in the service response
    *
    * @param  MBEPlusResponse_DTO_HUM.SoldProductDetail object.
    * @param  policy_member__c Record.
    * @return boolean
    */
     private static boolean isValidMBENode(MBEPlusResponse_DTO_HUM.SoldProductDetail soldProductDetail, policy_member__c polMemObj)
    {
      List<String> startDate;
      if(soldProductDetail.InsuranceCoverage.StartDate<> NULL)
     {
        startDate= soldProductDetail.InsuranceCoverage.StartDate.split('/');
     }
      List<String> endDate ;
      if(soldProductDetail.InsuranceCoverage.EndDate<> NULL)
     { 
       endDate = soldProductDetail.InsuranceCoverage.EndDate.split('/');
     }
      List<String> policyEffDate ;
      if(polMemObj.Effective_Date__c<>NULL)
      {
         policyEffDate = polMemObj.Effective_Date__c.split('/');
      }
     List<String> policyEndDate ;
      if( polMemObj.End_Date__c<>NULL) 
      {
         policyEndDate = polMemObj.End_Date__c.split('/');
      }
      boolean returnValue = false;
   
      try{      
         
             if(((soldProductDetail != null && polMemObj != null && soldProductDetail.InsuranceCoverage != null && (soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == polMemObj.Policy__r.Major_LOB_Frm__c ||soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == polMemObj.Policy__r.Major_LOB__c)  ) || test.isRunningTest()))
              {
                 if(Integer.valueof(startDate[0])==Integer.valueof(policyEffDate[0])&& Integer.valueof(startDate[1])==Integer.valueof(policyEffDate[1]) && Integer.valueof(startDate[2])==Integer.valueof(policyEffDate[2]))
                  {
                    if(Integer.valueof(endDate[0])==Integer.valueof(policyEndDate[0])&& Integer.valueof(endDate[1])==Integer.valueof(policyEndDate[1]) && Integer.valueof(endDate[2])==Integer.valueof(policyEndDate[2]))
                     {
                       returnValue = true;
                     }
          
                  }
              }
      }catch(Exception e)
          { 
            HUMExceptionHelper.logErrors(e, 'BenefitsV2_D_HUM', 'isValidMBENode');
          }
      
      return returnValue;
     
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
			if (oResponseGBE != null)
			{
				gbeDtoObj = new GBEResponse_DTO_HUM();
				GBEServiceResponse_H_HUM.GroupInfo objGrpInfo = oResponseGBE.GroupInfo;
				if (objGrpInfo != null)
				{
					gbeDtoObj.sBenefitPackageId = objGrpInfo.BenefitPackageId;

					GBEServiceResponse_H_HUM.Benefit objBenefit = objGrpInfo.Benefit;
					if (objBenefit != null)
					{
						gbeDtoObj = createGBEBenefitDTO(gbeDtoObj, objBenefit);
					}
				}
			}
			return gbeDtoObj;
		}
		catch (Exception e)
		{
			HUMExceptionHelper.logErrors(e, 'BenefitsV2_D_HUM', 'getGBEDTOData');
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
		
        //newly added for  PBE18.08
        if(sServiceTypeCommentSwitch == true)
            productNode.SubVersion = '3';
        else
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

	/*
	* Method to get As Of Date on basis on Policy Member End Date
	* @param  String EndDate of Policy Member .
	* @return String
	*/
	private static String populateAsOfDate(Policy_Member__c  objPolMember)
	{
		//Date Formatting Criteria
		if (ApexPages.currentpage().getParameters().containsKey('refdate') && ApexPages.currentpage().getParameters().get('refdate') != '')
		{
			string s = ApexPages.currentpage().getParameters().get('refdate');
			return s.substring(4, 8) + HYPHEN_HUM + s.substring(0, 2) + HYPHEN_HUM + s.substring(2, 4);
		}
		else if (objPolMember <> NULL && objPolMember.status__c <> NULL && objPolMember.Status__c <> VOB_ACTIVEMEMBERSTATUS_HUM && objPolMember.End_Date__c <> NULL)
		{
			return objPolMember.End_Date__c.split(BACKSLASH_HUM)[2] + HYPHEN_HUM + objPolMember.End_Date__c.split(BACKSLASH_HUM)[0] + HYPHEN_HUM + objPolMember.End_Date__c.split(BACKSLASH_HUM)[1];
		}
		else
		{
			return string.valueof(date.today());
		}
	}

	/* Returns string.
	* <p>
	* Method to get platform value of the policy member
	*
	* @param String           Instance of policy_member__c
	* @return
	*/
	public string getPlatformValueForPolicyRider(policy_member__c objPolMem)
	{
		string platformValue = '';
		if (objPolMem != null && string.isNotBlank(objPolMem.Policy_Platform__c))
		{
			if (objPolMem.Policy_Platform__c == VOB_PLATFORM_LV_HUM || objPolMem.Policy_Platform__c == VOB_PLATFORM_CI_HUM || objPolMem.Policy_Platform__c == VOB_PLATFORM_CAS_HUM)
			{
				platformValue = VOB_PLATFORM_CAS_HUM;
			}
			else if (objPolMem.Policy_Platform__c == VOB_PLATFORM_EM_HUM || objPolMem.Policy_Platform__c == VOB_PLATFORM_MTV_HUM)
			{
				platformValue = VOB_PLATFORM_MTV_HUM;
			}
		}
		return platformValue;
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
		boolean sServiceTypeCommentSwitch = false;            
        sServiceTypeCommentSwitch = HUMUtilityHelper.isCRMFunctionalityON('732113');
			
		if (oProduct != null && oProduct.Detail != null)
		{
			if (oProduct.Detail.ServiceList != null && oProduct.Detail.ServiceList.Service != null && oProduct.Detail.ServiceList.Service.size() > 0)
			{
				List<ProductBusinessEntityResponseV2_H_HUM.Service> oServiceList = oProduct.Detail.ServiceList.Service;

				Boolean bPlatformLV = false;
				if (oProduct.MetaData.PlatformCode == 'LV' || oProduct.MetaData.PlatformCode == 'CI' || oProduct.MetaData.PlatformCode == 'CAS')    bPlatformLV = true;

				for(ProductBusinessEntityResponseV2_H_HUM.Service oService :oServiceList){	
					if (oService != null && oService.BenefitList != null && oService.BenefitList.Benefit != null && oService.BenefitList.Benefit.size() > 0)
					{
	                    updatebenSearchResArray(inNetworkMap,outNetworkMap,oService.BenefitList.Benefit,ParValue,VOB_BENEFITSEARCH_SERVICELIST_HUM, isCoveredServiceInNetworks,bPlatformLV,oService.ServiceCategoryCode, oService.ServiceTypeID);
	                }
	                if(oService != null && oService.CommentList != null && oService.CommentList.PlanComment != null && oService.CommentList.PlanComment.size () > 0)
	                {
	                    if(sServiceTypeCommentSwitch == true)
                        	updatebenSearchServiceCommentsArraySubVerion3(oService.CommentList.PlanComment,lstOfServiceComments, oService.ServiceTypeID);
                        else
                            updatebenSearchServiceCommentsArray(oService.CommentList.PlanComment,lstOfServiceComments, oService.ServiceTypeID);
                    }
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

	/* Method to prepare fianl Map of Non Limit
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
								if (InNetCovType.placeOfServiceCode.equalsIgnoreCase(OutNetCovType.placeOfServiceCode))    OutNetCovType.matched = true;
							}
							else
							{
								OutNetCovType.Par = InNetCovType.Par;
								if (InNetCovType.placeOfServiceCode.equalsIgnoreCase(OutNetCovType.placeOfServiceCode))    InNetCovType.matched = true;
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
		PBEBenefitsSearch_DTO_HUM benefitsSearchDTOObj = new PBEBenefitsSearch_DTO_HUM();
		CoverageType_DTO_HUM covDtoObj = new CoverageType_DTO_HUM();
		covDtoObj.BenefitDescription = 'Not Covered';
		Map<string, List<CoverageType_DTO_HUM>> lstLimit = new Map<string, List<CoverageType_DTO_HUM>>();
		Map<string, List<string>> lstComment = new Map<string, List<string>>();

		if (lstOfServiceComments != null && lstOfServiceComments.size() > 0) benefitsSearchDTOObj.lstOfServiceComments = lstOfServiceComments;

		if (ParValue == VOB_PAR_YES_HUM)
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
		else if (ParValue == VOB_PAR_NO_HUM)
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
		else if (ParValue == VOB_BOTH)
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
		return benefitsSearchDTOObj;
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
					if (string.isNotBlank(BenefitLst[j].IsCoveredService))
					{
						if (!inNetwork)    inNetwork = true;
						if (!inNetworkIsCovered && BenefitLst[j].IsCoveredService.equalsIgnoreCase('True'))    inNetworkIsCovered = true;
					}
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
						if (!outNetwork)    outNetwork = true;
						if (!outNetworkIsCovered && BenefitLst[j].IsCoveredService.equalsIgnoreCase('True'))    outNetworkIsCovered = true;
					}
					if (BenefitLst[j].BenefitDetailList != null && !BenefitLst[j].BenefitDetailList.BenefitDetail.isEmpty())
					{
                        getAssignOutNetworkValue(BenefitLst[j].BenefitDetailList.BenefitDetail,outNetworkMap,nodeTypeValue,parVal,bPlatformLV, ServiceCatCode,  ServiceTypeID);
					}
				}
			}
		}
		if (inNetwork)    isCoveredServiceInNetworks.put(VOB_BENEFITSEARCH_INNETWORK_HUM, inNetworkIsCovered);
		if (outNetwork)    isCoveredServiceInNetworks.put(VOB_BENEFITSEARCH_OUTNETWORK_HUM, outNetworkIsCovered);
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
		 if(string.isNotBlank(ServiceCatCode))  covDtoObj.ServiceCatCode = ServiceCatCode;
		 if(string.isNotBlank(ServiceTypeID))  covDtoObj.ServiceTypeID = ServiceTypeID;
		if (!bPlatformLV && string.isNotBlank(pbeBenAmtLst[index].BenefitDescription))  covDtoObj.BenefitDescription = pbeBenAmtLst[index].BenefitDescription;
		if (bPlatformLV && string.isNotBlank(parCodeIDList))  covDtoObj.BenefitDescription = parCodeIDList;
		if (string.isNotBlank(pbeBenAmtLst[index].ProviderSubNetworkNumber))  covDtoObj.ProviderSubNetworkNumber = pbeBenAmtLst[index].ProviderSubNetworkNumber;
		if (string.isNotBlank(pbeBenAmtLst[index].IsLimit))  covDtoObj.isLimit = pbeBenAmtLst[index].isLimit;
		if (string.isNotBlank(pbeBenAmtLst[index].CoverageType)) covDtoObj.CoverageType = pbeBenAmtLst[index].CoverageType;
		if (string.isNotBlank(tierNumber)) covDtoObj.TierNumber = TierNumber;
		if (string.isNotBlank(isCoveredBenefit)) covDtoObj.sIsCoveredBenefit = isCoveredBenefit;
		if (string.isNotBlank(benefitsType)) covDtoObj.benefitType = benefitsType;
		covDtoObj.placeOfServiceCode = (string.isNotBlank(placeOfServiceCode)) ? placeOfServiceCode : '';
		covDtoObj.limitName = (pbeBenAmtLst[index].LimitName != 'None') ? pbeBenAmtLst[index].LimitName : pbeBenAmtLst[index].BenefitDescription;
		if (pbeBenAmtLst[index].UnitDetail != null)
		{
             String limitText ='';
              if(bPlatformLV)
              {
              
               if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityType ) && pbeBenAmtLst[index].UnitDetail.QuantityType != 'None' ) limitText =   pbeBenAmtLst[index].UnitDetail.QuantityType;
               if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType) && pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType != 'None') limitText =  limitText + ' per '+ pbeBenAmtLst[index].UnitDetail.TimePeriodQualifierType;
              
              }
              else
              {
                 if(string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Notes)) limitText =  pbeBenAmtLst[index].UnitDetail.Notes;
              }
           covDtoObj.limitText = limitText;
			if (string.isNotBlank(pbeBenAmtLst[index].UnitDetail.Quantity)) covDtoObj.limitAmount = getFormattedValue(pbeBenAmtLst[index].UnitDetail);
			if (pbeBenAmtLst[index].UnitDetail.QuantityPeriod != null && string.isNotBlank(pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes))
				covDtoObj.limitComment = pbeBenAmtLst[index].UnitDetail.QuantityPeriod.Notes;
			covDtoObj.notes = pbeBenAmtLst[index].UnitDetail.Notes;
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

	/* Method for parsing of InNetwork values and prepare In network map with both values Limits and Non Limits values
	* <p>
	* @param List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> .
	* @param  string selected PAR value .
	* @param  string Node type value .
	* @param  string TierNumber value .
	* @param  Map<string,list<CoverageType_DTO_HUM>> for InNetwork .
	* @return nothing
	*/
    //REQ-410445 Add BenefitDetail node to access service ada code 
    private static void prepareNodeStructureInNetwork(ProductBusinessEntityResponseV2_H_HUM.BenefitDetail details, List<ProductBusinessEntityResponseV2_H_HUM.BenefitAmount> pbeBenAmtLst , Map<string,list<CoverageType_DTO_HUM>> networkMap , String nodeTypeValue , String sPar,string benefitsType,string placeOfServiceCode,string tierNumber, String isCoveredBenefit, String parCodeIDListIn, Boolean bPlatformLV,String ServiceCatCode, String ServiceTypeID)
	{
		//REQ-410445 Add Ada code in wrapper
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
				//Add in wrapper
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
			if (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.TierNumber) && !'None'.equalsIgnoreCase(pbeBenDtl.TierNumber))
			{
				tierNumber = pbeBenDtl.TierNumber;
			}
			if (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.IsCoveredBenefit) && !'None'.equalsIgnoreCase(pbeBenDtl.IsCoveredBenefit))
			{
				isCoveredBenefit = pbeBenDtl.IsCoveredBenefit;
			}
			if (pbeBenDtl != null && pbeBenDtl.BenefitAmountList != null && !pbeBenDtl.BenefitAmountList.BenefitAmount.isEmpty())
			{
				if (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.BenefitType)) nodeTypeValue = pbeBenDtl.BenefitType + '_Service';
				if (pbeBenDtl.PlaceOfServiceCodeList != null && pbeBenDtl.PlaceOfServiceCodeList.Code != null && pbeBenDtl.PlaceOfServiceCodeList.Code.size() > 0)
				{
					for (string placeOfServiceCodeIn : pbeBenDtl.PlaceOfServiceCodeList.Code)
					{
						if (bPlatformLV && pbeBenDtl.ParCodeIDList != null && pbeBenDtl.ParCodeIDList.Code != null && pbeBenDtl.ParCodeIDList.Code.size() > 0)
						{
							for (string parCodeIDListIn : pbeBenDtl.ParCodeIDList.Code)
							{
                                 //Add detail node to acess ada code REQ-410445 , same for else case also                                
								assignInNetworkValues( pbeBenDtl, pbeBenDtl.BenefitAmountList.BenefitAmount,inNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeIn,tierNumber,isCoveredBenefit,parCodeIDListIn,bPlatformLV, ServiceCatCode,  ServiceTypeID);
							}
						}
                        else    assignInNetworkValues( pbeBenDtl, pbeBenDtl.BenefitAmountList.BenefitAmount,inNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeIn,tierNumber,isCoveredBenefit,'',bPlatformLV,ServiceCatCode, ServiceTypeID);         
					}
				}
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
			if (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.IsCoveredBenefit) && !'None'.equalsIgnoreCase(pbeBenDtl.IsCoveredBenefit))
			{
				isCoveredBenefit = pbeBenDtl.IsCoveredBenefit;
			}
			if (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.TierNumber) && !'None'.equalsIgnoreCase(pbeBenDtl.TierNumber))
			{
				tierNumber = pbeBenDtl.TierNumber;
			}
			if (pbeBenDtl != null && pbeBenDtl.BenefitAmountList != null && !pbeBenDtl.BenefitAmountList.BenefitAmount.isEmpty())
			{
				if (pbeBenDtl != null && string.isNotBlank(pbeBenDtl.BenefitType)) nodeTypeValue = pbeBenDtl.BenefitType + '_Service';
				if (pbeBenDtl.PlaceOfServiceCodeList != null && pbeBenDtl.PlaceOfServiceCodeList.Code != null && pbeBenDtl.PlaceOfServiceCodeList.Code.size() > 0)
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
                        else    assignOutNetworkValues(pbeBenDtl.BenefitAmountList.BenefitAmount,outNetworkMap,'Service',parVal,nodeTypeValue,placeOfServiceCodeOut,tierNumber,isCoveredBenefit,'',bPlatformLV,ServiceCatCode,ServiceTypeId);
					}
				}
			}
		}
	}

	/* Method for retrieving case records
	* <p>
	* @param  String account id
	* @param  string sortExpression .
	* @param  string sortdirection
	* @return List<Case>
	*/
	public static List<Case> retrieveAccountCases(String accId, String sortExpression, String sortDirection)
	{
		List<Case> lstCase = NULL;
		Set<Case> setFinalCase = new Set<Case>();

		String sQuery = 'Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name,Product__c,Interacting_With__r.Name,' +
			'Interacting_With_Type__c,Interacting_About__c,Status,Priority,CreatedDate,ClosedDate,CreatedBy.Name,Created_By_Queue__c from' +
			' Case where ';
		sQuery += 'AccountId =: accId';
		sQuery += ' AND Status != \'Cancelled\'';
		if (String.isNotBlank(sortExpression) && String.isNotBlank(sortDirection)) {
			sQuery += ' ORDER BY ' + sortExpression + ' ' + sortDirection + ' NULLS LAST';
		}
		return Database.query(sQuery);
	}

	/**
	* Returns BillingProfileRequestV2_DTO_HUM object that can then be utilized for sending the webservice request.
	* This method is to create the request Structure for Billing service .
	*
	* @param    String 	NetworkUser Id
	* @param    String 	SearchID
	* @param    String 	Platform
	* @return   BillingProfileRequestV2_DTO_HUM      request structure of the wrapper class Billing Service
	*/
	public static BillingProfileRequestV2_DTO_HUM RequestStructureBillingService(String sNetworkID, String sPlatform, String sSearchID, String sExchangeType, string sProductType, string sProduct, Boolean bSwitchForNewPBS)
	{
		BillingProfileRequestV2_DTO_HUM oReq = new BillingProfileRequestV2_DTO_HUM();
        Boolean bcallNewPBSService = false;
        if(bSwitchForNewPBS)
        {
            Set<String> setNonCommDental = new Set<String>{'OHBD','POSD','OPPD','SUPPD','OCHD','SUPD'};
            if((sPlatform.equalsIgnoreCase('MTV') && !sExchangeType.equalsIgnoreCase('Federal') && !setNonCommDental.contains(sProductType) && sProduct.equalsIgnoreCase('Dental')) || sProduct.equalsIgnoreCase('Medical'))
                bcallNewPBSService = true;
        }
		BillingProfileRequestV2_DTO_HUM.InvoiceInfo invoiceInfo = new BillingProfileRequestV2_DTO_HUM.InvoiceInfo();
		invoiceInfo.IncludeInvoiceInformation = 'true';
		invoiceInfo.NbrOfInvoices = PAIDTHROUGHDATE_NUMBEROFTRANSACTIONS_HUM;

		BillingProfileRequestV2_DTO_HUM.PremiumInfo premiumInfo = new BillingProfileRequestV2_DTO_HUM.PremiumInfo();
		premiumInfo.IncludePremiumInformation = 'true';
		premiumInfo.RateLevel = PAIDTHROUGHDATE_RATELEVEL_HUM;

		BillingProfileRequestV2_DTO_HUM.IncludeCriteria includeCriteriaDTO = new BillingProfileRequestV2_DTO_HUM.IncludeCriteria();
       if(bcallNewPBSService && bSwitchForNewPBS)
            includeCriteriaDTO.GetGroupInfo = 'true';       
        else {
            includeCriteriaDTO.PaymentOption = 'true';
            includeCriteriaDTO.PayorInfo = 'true';
            includeCriteriaDTO.CollectionInfo = 'true';
            includeCriteriaDTO.BillingMode = 'true';
            includeCriteriaDTO.BillingFrequency = 'true';
            includeCriteriaDTO.BalanceInfo = 'true';
            includeCriteriaDTO.InvoiceInfo = invoiceInfo;
            includeCriteriaDTO.PremiumInfo = premiumInfo;
            includeCriteriaDTO.TransactionInfo = 'true';
            includeCriteriaDTO.TransactionType = PAIDTHROUGHDATE_TRANSACTIONTYPE_HUM;
            includeCriteriaDTO.NbrOfTransactions = PAIDTHROUGHDATE_NUMBEROFTRANSACTIONS_HUM;
        }


		BillingProfileRequestV2_DTO_HUM.RequestContext requestContect = new BillingProfileRequestV2_DTO_HUM.RequestContext();
        if(bcallNewPBSService && bSwitchForNewPBS) {
            requestContect.CallingSystemID = 'PDCRM';
            requestContect.UserIDType = 'EXTL';
            requestContect.ProfileSequenceNbr = 0;
        }
        else {		
            requestContect.CallingSystemID = PAIDTHROUGHDATE_CALLINGSYSTEMID_HUM;
            requestContect.UserIDType = PAIDTHROUGHDATE_USERIDTYPE_HUM;
        }
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
			if (!billingProfileResponse.GetBillingAccountInfoResponse.GetBillingAccountInfoResponseList.GetBillingAccountInfoResponseDetail.isEmpty())
			{
				BillingProfileResponseV2_DTO_HUM.GetBillingAccountInfoResponseDetail billingInfo = billingProfileResponse.GetBillingAccountInfoResponse.GetBillingAccountInfoResponseList.GetBillingAccountInfoResponseDetail[0];
				if (billingInfo != null && billingInfo.BillingAccountList != null && billingInfo.BillingAccountList.BillAccount != null)
				{
					if (!billingInfo.BillingAccountList.BillAccount.isEmpty()) sDateOutput = formatDate(billingInfo.BillingAccountList.BillAccount[0].BillingProfile.PaidThroughDate);
				}
			}
		}

		return sDateOutput;
	}

	/**
	* Formats Paid Through Date from the webservice response.
	* This method is to format the Paidthrough date .
	*
	* @param    String   Webservice response from Paidthrough date
	* @return   String   Paid through date in MM-DD-YYYY
	*/
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

	
	@testVisible public static MentorServiceRequest_DTO_HUM RequestStructureKDMService(MentorServiceRequest_DTO_HUM KDMRequestDTO)
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
    	if(ApexPages.currentpage().getParameters().containsKey('Id'))
        {
            String sPolMemId = ApexPages.currentpage().getParameters().get('Id');
            if(NULL!= sPolMemId)
        	{
	            Policy_Member__c objPolMem = [Select Id ,Product__c ,Policy_Platform__c,Product_Type__c,Policy__r.Contract_Number__c,Policy__r.PBP_Code__c,Policy__r.Benefit_coverage__c,
	                                         Policy__r.Group_Number__c,Policy__r.Medicare_Segment_ID__c From Policy_Member__c Where Id =: ApexPages.currentpage().getParameters().get('Id')];
	            if(objPolMem != null)
	            {	
	                sPlatform = objPolMem.Policy_Platform__c;
	                sBenCoverage = objPolMem.Policy__r.Benefit_coverage__c;
	                sContractNum = objPolMem.Policy__r.Contract_Number__c;
	                sPBPCode = objPolMem.Policy__r.PBP_Code__c;
	                sMedicaresegId = objPolMem.Policy__r.Medicare_Segment_ID__c;  
	                sGrpNumber = objPolMem.Policy__r.Group_Number__c;
	                sProduct = objPolMem.Product__c;
	                sProductType = objPolMem.Product_Type__c;
	               
	               List<Reference_Business_Line_Product_Grouping__c> lstRefBusinessLineProdGroups = [SELECT Business_Description__c  FROM Reference_Business_Line_Product_Grouping__c 
                                                                                              Where ProductCode__c = :sProduct And  Major_LOB__c = : sProductType And Business_Description__c = :VOB_MEDICARE];
                    if(sProduct	 == MED && !lstRefBusinessLineProdGroups.isEmpty())
		         	{
			            sQueryText = sContractNum+sHiphan+sPBPCode;
		                if(String.isNotBlank(sMedicaresegId)) sQueryText += sHiphan +  sMedicaresegId;
		            }
		            else 
		            {
		                if( sPlatform == sEM_Value || sPlatform == sMTV_Value )
				        {
				            sQueryText = SUBGROUP_MENTORREDIRECT_HUM + sBenCoverage;
				        } 
				        else if( sPlatform == sLV_Value || sPlatform == sCI_Value || sPlatform == sCAS_Value )
				        {
				            if(String.isNotBlank(sGrpNumber))
				            {
				                sQueryText = SUBGROUP_MENTORREDIRECT_HUM + sGrpNumber;
				            }
				        } 

		            }
	           	 }
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
	
	 /**
    * Returns ToothHistoryServiceRequest_H_HUM object that can then be utilized for sending the webservice request.
    * This method is to create the request Structure for tooth history service .
    *
    * @param    String  NetworkUser Id
    * @param    String  SearchID
    * @param    String  Platform
    * @return   BillingProfileRequestV2_DTO_HUM      request structure of the wrapper class Billing Service
    */
    public static ToothHistoryServiceRequest_H_HUM RequestToothHistoryService(String sMemberId, String sMemberDependentCode)
    {
        ToothHistoryServiceRequest_H_HUM oReq = new ToothHistoryServiceRequest_H_HUM();
        ToothHistoryServiceRequest_H_HUM.GetToothHistory oGetToothHistory = new ToothHistoryServiceRequest_H_HUM.GetToothHistory();
        oGetToothHistory.MemberId = sMemberId;
        oGetToothHistory.MemberDependentCode = sMemberDependentCode;
        oReq.GetToothHistory = oGetToothHistory;
        return oReq;
    }
    
    /**
    * Returns Paid Through Date from the webservice response.
    * This method is to parse the respponse for Billing service .
    *
    * @param    BillingProfileResponseV2_DTO_HUM Webservice response
    * @return   String   Paid through date in the response
    */
    public static List<ToothHistory_DTO_HUM> ParseToothHistoryRes(ToothServiceResponse_H_HUM Response)
    {
        List<ToothHistory_DTO_HUM> lstToothHistoryDTO = new List<ToothHistory_DTO_HUM>();
        if (Response != null && Response.GetToothHistoryResponse != null && Response.GetToothHistoryResponse.GetToothHistoryResult != null && Response.GetToothHistoryResponse.GetToothHistoryResult.ClaimHistoryResult != null)
        {
            ToothServiceResponse_H_HUM.GetToothHistoryResponse oGetToothHistoryResponse = Response.GetToothHistoryResponse;
            ToothServiceResponse_H_HUM.GetToothHistoryResult oGetToothHistoryResult  = oGetToothHistoryResponse.GetToothHistoryResult;
            List<ToothServiceResponse_H_HUM.ClaimHistoryResult> lstClaimHistoryResult = oGetToothHistoryResult.ClaimHistoryResult;
            if(oGetToothHistoryResult.ErrorMessage == '' && oGetToothHistoryResult.ErrorType == '')
            {
                for(ToothServiceResponse_H_HUM.ClaimHistoryResult objHistoryResult : lstClaimHistoryResult)
                {
                    ToothHistory_DTO_HUM obj = new ToothHistory_DTO_HUM();
                    obj.sDateOfService = String.IsNotBlank(objHistoryResult.DateOfService) ? objHistoryResult.DateOfService : '';
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