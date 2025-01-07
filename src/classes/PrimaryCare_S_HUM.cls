/************************************************************************************************************************************************
Apex Class Name  : PrimaryCare_S_HUM
Version          : 1.0
Created Date     : MAR 16 2016
Function         : Service Layer Class for PrimaryCare_VF_HUM Page and ... component.
Test Class       :
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Kumar Choubey      30639                   03/16/2016                   Original Version as per REQ - 244445
* Avinash Kumar Choubey      30639                   03/29/2016                   fixed Review Comments
* Rajesh Keswani		     27366 	                 05/05/2016                   Added Request preparation method for EPS Service
* Rajesh Keswani		     31032 	                 06/22/2016                   Changed older version of EPSS request classes.
* Rajesh Keswani		     32180 	                 07/06/2016                   Defect fix 232765 passing todays date in MBE input
* Gargey Sharma		     	 46856			 	     10/28/2016                   REQ - 282445 : Merged changes for new EPSS service
* Prudhvi Pamarthi           237056                  06/15/2018                   REQ - 363608 : EPSS - Update App Key for CRM Service
* Melkisan Selvaraj			 252166       	         06/19/2018  		          REQ - 366157 MBEServices replaced by MBEPlusService
* Pallavi Shewale                                    04/30/2021                   User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member
*************************************************************************************************************************************************/
public with sharing class PrimaryCare_S_HUM
{
      Public Static final String PCP_MBEPLUSSERVICENAME_HUM = 'MBEPlusService';
      Public Static final String VOB_PCP_REFDATEFORMT_HUM ='yyyy-MM-dd';  
      Public Static final String PCP_MEMCR_PERIOD_HUM = 'Current';
      Public Static final String VOB_PAST_HUM = 'Past';
      Public Static final String VOB_FUTURE_HUM = 'Future';
      Public Static final String RESTV1SERVICENAME_PROVIDERSEARCH_HUM = 'EPSServiceREST_V1';
      Public Static final String PCP_EPS_PARONLY_HUM ='false';
      Public Static final String PCP_EPS_IDENTIFIERTYPE_HUM ='NpiID';
      Public Static final String PCP_APPLICATIONKEY_HUM = '28AD1338-9A7D-41DB-BB87-F9FDFEE3DA01';//'b35bc8f5-a390-4c91-8b5b-4327708550ec';
      Public Static final String PCP_SER_CONTENTTYPE_HUM = 'content-type';
      Public Static final String PCP_SER_CONECTION_HUM = 'Connection';
      Public Static final String PCP_SER_KEEPALIVE_HUM = 'keep-alive';
      Public Static final String PCP_SER_SOAPACTION_HUM = 'SOAPAction';    
	/**
	* Returns an object of HttpRequest
	* <p>
	* Method to get the request Details from custom setting for Pcp/Pcd Table
	*
	* @param  String personID
	* @param  String refDate
	* @return  HttpRequest object
	* @see
	*/
	public static HTTPRequest  getMemberMBERequest(String personID, String refDate)
	{
        HTTPRequest request = getHTTPRequestStructure(PCP_MBEPLUSSERVICENAME_HUM);
		MBEPlusRequest_DTO_HUM oReq = new MBEPlusRequest_DTO_HUM();

		MBEPlusRequest_DTO_HUM.GetMember getMember = new MBEPlusRequest_DTO_HUM.GetMember();
		MBEPlusRequest_DTO_HUM.Member mem = new MBEPlusRequest_DTO_HUM.Member();


		MBEPlusRequest_DTO_HUM.PlatformPointerList objPlatformPointerList = new MBEPlusRequest_DTO_HUM.PlatformPointerList();
		objPlatformPointerList.PlatformProxyKey = '';
		mem.PlatformPointerList = objPlatformPointerList;

		MBEPlusRequest_DTO_HUM.IndicatorList indList = new MBEPlusRequest_DTO_HUM.IndicatorList();
		mem.IndicatorList = indList;

		MBEPlusRequest_DTO_HUM.MedicaidIdentifierList  objMedicaidIdentifierList = new MBEPlusRequest_DTO_HUM.MedicaidIdentifierList();
		objMedicaidIdentifierList.MedicaidIdentifier = '';
		mem.MedicaidIdentifierList = objMedicaidIdentifierList;

		MBEPlusRequest_DTO_HUM.MedicareIdentifierList objMedicareIdentifierList = new MBEPlusRequest_DTO_HUM.MedicareIdentifierList();
		objMedicareIdentifierList.MedicareIdentifier = '';
		mem.MedicareIdentifierList = objMedicareIdentifierList;

		MBEPlusRequest_DTO_HUM.LongTermCareEligibilityList objLongTermCareEligibilityList = new MBEPlusRequest_DTO_HUM.LongTermCareEligibilityList();
		objLongTermCareEligibilityList.LongTermCareEligibility = '';
		mem.LongTermCareEligibilityList = objLongTermCareEligibilityList;

		MBEPlusRequest_DTO_HUM.LowIncomeSubsidyLevelList objLowIncomeSubsidyLevelList = new MBEPlusRequest_DTO_HUM.LowIncomeSubsidyLevelList();
		objLowIncomeSubsidyLevelList.LowIncomeSubsidyLevel = '';
		mem.LowIncomeSubsidyLevelList = objLowIncomeSubsidyLevelList;

		MBEPlusRequest_DTO_HUM.Contact objContact = new MBEPlusRequest_DTO_HUM.Contact();
		MBEPlusRequest_DTO_HUM.ContactProfile objContactProfile = new MBEPlusRequest_DTO_HUM.ContactProfile();
		MBEPlusRequest_DTO_HUM.PhoneList objPhoneList = new MBEPlusRequest_DTO_HUM.PhoneList();
		objPhoneList.PersonPhone = '';
		MBEPlusRequest_DTO_HUM.EmailList objEmailList = new MBEPlusRequest_DTO_HUM.EmailList();
		objEmailList.PersonEmail = '';
		MBEPlusRequest_DTO_HUM.AddressList objAddressList = new MBEPlusRequest_DTO_HUM.AddressList();
		objAddressList.PersonAddress = '';
		objContactProfile.PhoneList = objPhoneList;
		objContactProfile.EmailList = objEmailList;
		objContactProfile.AddressList = objAddressList;
		objContact.ContactProfile = objContactProfile;
		mem.Contact = objContact;


		MBEPlusRequest_DTO_HUM.SoldProductList objSoldProductList = new MBEPlusRequest_DTO_HUM.SoldProductList();
		List<MBEPlusRequest_DTO_HUM.SoldProduct> listObjSoldProduct = new List<MBEPlusRequest_DTO_HUM.SoldProduct>();
		MBEPlusRequest_DTO_HUM.SoldProduct objSoldProduct = new MBEPlusRequest_DTO_HUM.SoldProduct();
		MBEPlusRequest_DTO_HUM.InsuranceProduct objInsuranceProduct = new MBEPlusRequest_DTO_HUM.InsuranceProduct();
		MBEPlusRequest_DTO_HUM.SoldProductDetailList objSoldProductDetailList = new MBEPlusRequest_DTO_HUM.SoldProductDetailList();
		MBEPlusRequest_DTO_HUM.SoldProductDetail objSoldProductDetail = new MBEPlusRequest_DTO_HUM.SoldProductDetail();
		MBEPlusRequest_DTO_HUM.InsuranceCoverage objInsuranceCoverage = new MBEPlusRequest_DTO_HUM.InsuranceCoverage();
		MBEPlusRequest_DTO_HUM.NonInsuranceCoverage objNonInsuranceCoverage = new MBEPlusRequest_DTO_HUM.NonInsuranceCoverage();

		MBEPlusRequest_DTO_HUM.ReasonList objReasonList = new MBEPlusRequest_DTO_HUM.ReasonList();
		MBEPlusRequest_DTO_HUM.OutOfAreaList outofAreaLst = new MBEPlusRequest_DTO_HUM.OutOfAreaList();
		MBEPlusRequest_DTO_HUM.CostShareReductionList  costShareReductionList = new MBEPlusRequest_DTO_HUM.CostShareReductionList();
		MBEPlusRequest_DTO_HUM.EnrollmentInfo  enrollInfo = new MBEPlusRequest_DTO_HUM.EnrollmentInfo();

		MBEPlusRequest_DTO_HUM.MemberPlatformPointerList  objMemberPlatformPointerList = new MBEPlusRequest_DTO_HUM.MemberPlatformPointerList();
		MBEPlusRequest_DTO_HUM.GroupPlatformPointerList  objGroupPlatformPointerList = new MBEPlusRequest_DTO_HUM.GroupPlatformPointerList();
		MBEPlusRequest_DTO_HUM.Network netList = new MBEPlusRequest_DTO_HUM.Network();
		MBEPlusRequest_DTO_HUM.CoveredRolePlatformPointerList coveredRolPlatPointList = new MBEPlusRequest_DTO_HUM.CoveredRolePlatformPointerList();
		MBEPlusRequest_DTO_HUM.CoveredRoleList coverRoleList = new MBEPlusRequest_DTO_HUM.CoveredRoleList();
		MBEPlusRequest_DTO_HUM.CoveredRole coveredRol = new MBEPlusRequest_DTO_HUM.CoveredRole();
		MBEPlusRequest_DTO_HUM.EnhancedHealthBenefit enHealthBen = new MBEPlusRequest_DTO_HUM.EnhancedHealthBenefit();
		MBEPlusRequest_DTO_HUM.XReferenceList  objXReferenceList = new MBEPlusRequest_DTO_HUM.XReferenceList();
		MBEPlusRequest_DTO_HUM.XReference objXReference = new MBEPlusRequest_DTO_HUM.XReference();
		MBEPlusRequest_DTO_HUM.WaitingPeriodIndicatorList objWaitingPeriodIndicatorList = new MBEPlusRequest_DTO_HUM.WaitingPeriodIndicatorList();
		MBEPlusRequest_DTO_HUM.WaitingPeriod objWaitingPeriod = new MBEPlusRequest_DTO_HUM.WaitingPeriod();
		MBEPlusRequest_DTO_HUM.WaitingPeriodList objWaitingPeriodList = new MBEPlusRequest_DTO_HUM.WaitingPeriodList();

		MBEPlusRequest_DTO_HUM.PriorCarrier objPriorCarrier = new MBEPlusRequest_DTO_HUM.PriorCarrier();

		MBEPlusRequest_DTO_HUM.CoordinationOfBenefitList objCoordBenefitList = new MBEPlusRequest_DTO_HUM.CoordinationOfBenefitList();
		MBEPlusRequest_DTO_HUM.AdvancedPremiumTaxCreditList objAdvancedPremiumTaxCreditList = new MBEPlusRequest_DTO_HUM.AdvancedPremiumTaxCreditList();
		MBEPlusRequest_DTO_HUM.PrimaryCarePhysician objPrimaryCarePhysician = new MBEPlusRequest_DTO_HUM.PrimaryCarePhysician();
		MBEPlusRequest_DTO_HUM.CostShareReduction objCostShareReduction = new MBEPlusRequest_DTO_HUM.CostShareReduction();

		MBEPlusRequest_DTO_HUM.PrimaryCarePhysicianList objPrimaryCarePhysicianList = new MBEPlusRequest_DTO_HUM.PrimaryCarePhysicianList();
		objInsuranceCoverage.PrimaryCarePhysicianList = objPrimaryCarePhysicianList;

		objPriorCarrier.IndicatorList = indList;
		enrollInfo.PriorCarrier = objPriorCarrier;

		objInsuranceCoverage.MemberPlatformPointerList = objMemberPlatformPointerList;
		objInsuranceCoverage.GroupPlatformPointerList = objGroupPlatformPointerList;
		objInsuranceCoverage.CostShareReductionList = costShareReductionList;
		objInsuranceCoverage.OutOfAreaList = outofAreaLst;
		objInsuranceCoverage.EnrollmentInfo = enrollInfo;
		objInsuranceCoverage.IndicatorList = indList;
		objInsuranceCoverage.Network = netList;
		objInsuranceCoverage.CostShareReduction = objCostShareReduction;
		objInsuranceCoverage.EnhancedHealthBenefit = enHealthBen;
		objInsuranceCoverage.PrimaryCarePhysician = objPrimaryCarePhysician;
		objInsuranceCoverage.ReasonList = objReasonList;
		objWaitingPeriod.WaitingPeriodIndicatorList = objWaitingPeriodIndicatorList;
		objWaitingPeriodList.WaitingPeriod = objWaitingPeriod;
		objInsuranceCoverage.WaitingPeriodList = objWaitingPeriodList;


		coveredRol.CoveredRolePlatformPointerList = coveredRolPlatPointList;
		coverRoleList.CoveredRole = coveredRol;
		objInsuranceCoverage.CoveredRoleList = coverRoleList;
		objXReferenceList.XReference = objXReference;
		objInsuranceCoverage.XReferenceList = objXReferenceList;
		objInsuranceCoverage.CoordinationOfBenefitList = objCoordBenefitList;
		objInsuranceCoverage.AdvancedPremiumTaxCreditList = objAdvancedPremiumTaxCreditList;


		objSoldProductDetail.InsuranceCoverage = objInsuranceCoverage;
		objSoldProductDetail.NonInsuranceCoverage = objNonInsuranceCoverage;
		objSoldProductDetailList.SoldProductDetail = objSoldProductDetail;
		objInsuranceProduct.SoldProductDetailList = objSoldProductDetailList;

		objSoldProduct.InsuranceProduct = objInsuranceProduct;
		listObjSoldProduct.add(objSoldProduct);
		objSoldProductList.SoldProduct = listObjSoldProduct;
		mem.SoldProductList = objSoldProductList;

		MBEPlusRequest_DTO_HUM.GetCriteria getCri = new MBEPlusRequest_DTO_HUM.GetCriteria();
		MBEPlusRequest_DTO_HUM.PersonIdCriteria pIdCri = new MBEPlusRequest_DTO_HUM.PersonIdCriteria();
        pIdCri.AsOfDate = Datetime.now().format(VOB_PCP_REFDATEFORMT_HUM);
		MBEPlusRequest_DTO_HUM.Period period = new MBEPlusRequest_DTO_HUM.Period();
		List<String> periodVars = new List<String>();
        periodVars.add(PCP_MEMCR_PERIOD_HUM);
        periodVars.add(VOB_PAST_HUM);
        periodVars.add(VOB_FUTURE_HUM);
		period.Period = periodVars;
		pIdCri.PeriodList = period;
		pIdCri.PersonId = personID;
		getcri.PersonIdCriteria = pIdCri;
		mem.GetCriteria = getCri;
		getMember.Member = mem;
		oReq.GetMember = getMember;
		string sbody = JSON.serialize(oReq);
		request.setBody(sbody);
		return request;
	}

	/**
	* Returns an object of HttpRequest
	* <p>
	* Method to get the request Details from custom setting for Pcp/Pcd Table
	*
	* @param  String personID
	* @return  HttpRequest object
	* @see
	*/
	public static HTTPRequest  getMemberEPSRequest(String MBENpiID)
	{
        HTTPRequest request = getHTTPRequestStructure(RESTV1SERVICENAME_PROVIDERSEARCH_HUM);
		EPSSRESTV1SearchProviderRequest_H_HUM oReq = new EPSSRESTV1SearchProviderRequest_H_HUM();
		EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest SearchProviderRequest = new EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest();
		EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria SearchCriteria = new EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria();
		EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers Identifiers = new EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers();
		EPSSRESTV1SearchProviderRequest_H_HUM.Identifier Identifier = new EPSSRESTV1SearchProviderRequest_H_HUM.Identifier();
		List<EPSSRESTV1SearchProviderRequest_H_HUM.Identifier> listOfIden = new List<EPSSRESTV1SearchProviderRequest_H_HUM.Identifier>();
		EPSSRESTV1SearchProviderRequest_H_HUM.Filter Filter = new EPSSRESTV1SearchProviderRequest_H_HUM.Filter();
        Filter.ParOnly = PCP_EPS_PARONLY_HUM;
		Identifier.ID = MBENpiID;
		listOfIden.add(Identifier);
		Identifiers.Identifiers = listOfIden;
        Identifiers.IdentifierType = PCP_EPS_IDENTIFIERTYPE_HUM;
		SearchCriteria.Identifiers = Identifiers;
		SearchCriteria.Filter = Filter;
		SearchProviderRequest.SearchCriteria = SearchCriteria;
        SearchProviderRequest.ApplicationKey = PCP_APPLICATIONKEY_HUM;
		oReq.SearchProviderRequest = SearchProviderRequest;
		string sbody = JSON.serialize(oReq);
		request.setBody(sbody);
		return request;
	}

	/**
	* Returns an object of HttpRequest
	* <p>
	* Method to get the request Details from custom setting for Pcp/Pcd Table
	*
	* @param  String Servicename
	* @return  HttpRequest object
	* @see
	*/
	public static HTTPRequest getHTTPRequestStructure(String serviceName)
	{
		HttpRequest request = new HttpRequest();
		HUM_Webservice_Callout__c objMbeService = HUM_Webservice_Callout__c.getValues(serviceName);
		if (objMbeService != null)
		{
			request.setMethod(objMbeService.Request_Type__c);
			if (!Test.isRunningTest() && String.isNotBlank(objMbeService.Certificate_Name__c))
			{
				request.setClientCertificateName(objMbeService.Certificate_Name__c);
			}
			if (String.isNotBlank(objMbeService.Content_Type__c))
			{
                request.setHeader(PCP_SER_CONTENTTYPE_HUM, objMbeService.Content_Type__c);
			}
            request.setHeader(PCP_SER_CONECTION_HUM, PCP_SER_KEEPALIVE_HUM);
			request.setEndpoint(objMbeService.End_Point_URL__c);
            request.setHeader(PCP_SER_SOAPACTION_HUM, '');
			if (objMbeService.Timeout__c != null)
			{
				request.setTimeout(Integer.valueOf(objMbeService.Timeout__c));
			}
		}
		return request;
	}
}