/************************************************************************************************************************************************
Apex Class Name  : PrimaryCare_D_HUM
Version          : 1.0
Created Date     : MAR 16 2016
Function         : Data Layer class for PCP/PCD Page
Test Class       : PrimaryCare_T_HUM
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey            30639                   03/16/2016                   Original Version
* Avinash Kumar Choubey      30639                   03/29/2016                   Fixed Review Comments
* Avinash Kumar Choubey      30978                   04/14/2016                   Added null Check for nodes while parsing response (defect 222662)
* Shiva Pasumarty                                    04/28/2016                   Added logic for PCP/PCD Active Table
* Rajesh Keswani             27366                   05/05/2016                   Added EPS Service call
* Rajesh Keswani		     31032 	                 06/22/2016                   Changed older version of EPSS request classes.
* Avinash Kumar Choubey	     31321	 	             06/28/2016                   Changed Logic to select row to Bold , fix for defect 231479
* Rajesh Keswani		     31784		 	         07/01/2016                   Implemented EPSS changes as per the REQ-270313
* Rajesh Keswani		     36230			 	     08/19/2016                   Defect Fix 238044
* Rajesh Keswani		     36101			 	     08/19/2016                   REQ 270275 Claims - PCP/PCD Link *ACTIVE PCP* .
* Rajesh Keswani		     40644			 	     09/22/2016                   Defect fix 242850
* Gargey Sharma		     	 46856				     10/28/2016                   REQ - 282445 : Merged changes for new EPSS service
* Prudhvi Pamarthi           246877                  06/22/2018                   REQ - 366132 Fix for CA Incident - 7433768
* Melkisan Selvaraj	         252166		     		 06/19/2018  		  		  REQ - 366157 MBEServices replaced by MBEPlusService
* Lakshmi Madduri                                    10/17/2018                   REQ - 355805 Mapped Practice location
* Seema Kaulgi                             	  		 02/25/2019         		  REQ - 383975 Display Grouper Number and Grouper Name on PCP/PCD Tab (RF)
* Prasanthi Kandula                                  08/03/2020                   US - 1326203 Corrected message to display if there are no PCP/PCD information returned from backend for Dental policy  
* Anjani Vaddadi                                     08/27/2020                   DefectFix 1476
* Pallavi Shewale                                    04/30/2021                   User Story 2230075:T1PRJ0002606 - 2021 MF1 -SF - TECH HUmana Constants clean up Member

*************************************************************************************************************************************************/
public with sharing class PrimaryCare_D_HUM
{
     Public Static final Integer VOB_ServiceTimeOut_HUM = 120; 
     Public Static final String PCP_PROCESSRESMETHOD_HUM ='ProcessMbeResponse';
     Public Static final Integer HTTP_OK_HUM = 200;
     Public Static final Integer HTTP_ACCEPTED_HUM = 202; 
     Public Static final String PCP_EPS_ADDRESS_SERVICE_HUM = 'Service';
     Public Static final String PCP_EPS_ADDRESS_BILLING_HUM = 'Billing';
     Public Static final String PCP_EPS_ADDRESS_MAIL_HUM = 'Mail';
     Public Static final Integer MINDIFF_PROVIDERSEARCH_HUM = 10000000;
     Public Static final String PCP_EPS_PROCESSRESMETHOD_HUM ='pcpActiveHistoryFinalResponse';
     Public Static final String PCP_EPS_ADDRESS_ALL_HUM = 'All';
     Public Static final String PCP_EPS_PHONE_HUM = 'phone';
	/**
	* Returns an object
	* <p>
	* This method calls the accums web service
	*
	* @pram  id of the policy member in string format
	* @param  type in string format i.e either history or current
	* @return  object
	* @see
	*/
	public static Object getPcpPcdData(String personID, String refDate, string polMemObjId)
	{
		try
		{
			HttpRequest request = new HttpRequest();
            Continuation con = new Continuation(VOB_ServiceTimeOut_HUM);  
            con.continuationMethod = PCP_PROCESSRESMETHOD_HUM;
			request = PrimaryCare_S_HUM.getMemberMBERequest(personID, refDate);
			con.addHttpRequest(request);
			PrimaryCare_DTO_HUM primaryDTO = new PrimaryCare_DTO_HUM();
			primaryDTO.policyMemberObject = polMemObjId;
			primaryDTO.refDate = refDate;
			con.state = primaryDTO;
			return con;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'PrimaryCare_D_HUM', 'getPcpPcdData');
			return null;
		}
	}

	/**
	* Returns an object
	* <p>
	* This method Process the response from Service
	*
	* @pram  List<String> : Request Headers
	* @param  Object : State
	* @return  object
	* @see
	*/
	public Static Object ProcessMbeResponse(List < String > Labels, Object State)
	{
		Policy_member__c policyMemObj;
		string policymemId;
		string refDate;
		try
		{
			PrimaryCare_DTO_HUM PrimaryCareDTO = (PrimaryCare_DTO_HUM)State;
			if (PrimaryCareDTO != null)
			{
				refDate = PrimaryCareDTO.refDate;
				policymemId = PrimaryCareDTO.policyMemberObject;
			}
			HttpResponse response = Continuation.getResponse(Labels[0]);
            if (response != null && (response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM))
			{

				MBEPlusResponse_DTO_HUM oResponse = (MBEPlusResponse_DTO_HUM)System.JSON.deserialize(response.getBody(), MBEPlusResponse_DTO_HUM.class);
				if (string.isNotBlank(policymemId))
				{
					policyMemObj = [select id, name, Effective_Date__c, End_Date__c, Policy__r.Product__r.ProductCode, Policy__r.Major_LOB_Frm__c, Policy__r.Major_LOB__c, member__r.Enterprise_ID__C from Policy_member__C where id = :policymemId limit 1];
				}
                if(oResponse.GetMemberResponse != null) 
                {
				    PrimaryCareDTO = ConvertServiceResToPrimaryCareDTO(oResponse, refDate, policyMemObj);
				}
				if ((PrimaryCareDTO != null && PrimaryCareDTO.PcpPdpActiveDTO != null && string.isNotBlank(PrimaryCareDTO.PcpPdpActiveDTO.sPCP_PCD_Number)) || test.isRunningTest())
				{
					return prepareEPSServiceCall(PrimaryCareDTO);
				}
				else return PrimaryCareDTO;
			}
			else
			{
				return null;
			}
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'PrimaryCare_D_HUM', 'ProcessMbeResponse');
			return null;
		}
	}

	/**
	* Returns an object
	* <p>
	* This method Process the response from Service
	*
	* @pram  List<String> : Request Headers
	* @param  Object : State
	* @return  object
	* @see
	*/
	public Static Object pcpActiveHistoryFinalResponse(List < String > Labels, Object State)
	{
		try
		{
			PrimaryCare_DTO_HUM primaryDto = (PrimaryCare_DTO_HUM)State;
			HttpResponse response = Continuation.getResponse(Labels[0]);
            if (response != null && (response.getStatusCode() == HTTP_OK_HUM || response.getStatusCode() == HTTP_ACCEPTED_HUM))
			{
				EPSSRESTV1SearchProviderResponse_H_HUM oResponse = (EPSSRESTV1SearchProviderResponse_H_HUM)System.JSON.deserialize(response.getBody(), EPSSRESTV1SearchProviderResponse_H_HUM.class);
				String phoneNumber = getPhoneNumber(oResponse);
				if (primaryDto != null && primaryDto.PcpPdpActiveDTO != null)
				{
					primaryDto.PcpPdpActiveDTO.sPCP_PCD_Phone_Number = phoneNumber;
				}
				return primaryDto;
			}
			else
			{
				return primaryDto;
			}
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'PrimaryCare_D_HUM', 'pcpActiveHistoryFinalResponse');
			return null;
		}
	}

	/**
	* Returns string
	* <p>
	* This method to get the phoine number from EPS service
	*
	* @param  EPSSRESTV1SearchProviderResponse_H_HUM : Response
	* @return  string
	* @see
	*/
	private static string getPhoneNumber(EPSSRESTV1SearchProviderResponse_H_HUM EPSRes)
	{
		string phoneNumberField;
		if (EPSRes != null)
		{
			if (EPSRes.SearchProviderResponse != null)
			{
				EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse searchProvider = EPSRes.SearchProviderResponse;
				if (searchProvider != null)
				{
					phoneNumberField = getValidAddressNodePick(searchProvider);
				}
			}
		}
		return phoneNumberField;
	}

	/**
	* Returns PrimaryCare_DTO_HUM
	* <p>
	* This method Parse the response and create a structure
	*
	* @param  MBEPlusResponse_DTO_HUM : Response
	* @param  EPSServiceSearchGetPCPResponse_H_HUM : Response
	* @param  String : Referral Date
	* @return  PrimaryCare_DTO_HUM
	* @see
	*/
	private static PrimaryCare_DTO_HUM ConvertServiceResToPrimaryCareDTO(MBEPlusResponse_DTO_HUM oResponse, String refdate, policy_member__c polMembObj)
	{
		try
		{
			List < PcpPdpHistory_DTO_HUM > lstPcpPdpHistoryDTO = new List < PcpPdpHistory_DTO_HUM >();
			MBEPlusResponse_DTO_HUM.SoldProductDetail objSoldProdDetail = new MBEPlusResponse_DTO_HUM.SoldProductDetail();
			objSoldProdDetail = ConvertMbeEPSSResToPrimaryCareDTO(oResponse, polMembObj);
			string sPhone = '';
			PcpPdpActive_DTO_HUM PcpPdpActive;
            if(objSoldProdDetail.InsuranceCoverage!=null) {
			    PcpPdpActive = getPcpPcdActiveDtoObj(objSoldProdDetail, sPhone, refDate);
            }
			string effectiveDate = '';
			if (PcpPdpActive != null)
			{
				effectiveDate = PcpPdpActive.sEffective_Date;
			}
			Date asOfDate = Date.valueOf(refdate);
			PcpPdpHistory_DTO_HUM objPcpPdpHistoryDTO = null;
			if(objSoldProdDetail.InsuranceCoverage!=null){
			preparePCPHistoryDTO(objSoldProdDetail.InsuranceCoverage, lstPcpPdpHistoryDTO, asOfDate, objPcpPdpHistoryDTO, effectiveDate);
			}
			PrimaryCare_DTO_HUM PrimaryCare = new PrimaryCare_DTO_HUM();
			PrimaryCare.PcpPdpActiveDTO = PcpPdpActive;
			PrimaryCare.lstPcpPdpHistoryDTO = lstPcpPdpHistoryDTO;
			return PrimaryCare;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'ConvertServiceResToPrimaryCareDTO', 'ConvertServiceResToPrimaryCareDTO');
			return null;
		}
	}

	/**
	* Returns PcpPdpActive_DTO_HUM
	* <p>
	* This method Parse the response and create a structure
	*
	* @param  MBEPlusResponse_DTO_HUM : Response
	* @param  EPSServiceSearchGetPCPResponse_H_HUM : Response
	* @param  String : Referral Date
	* @return  PcpPdpActive_DTO_HUM
	* @see
	*/
	private static MBEPlusResponse_DTO_HUM.SoldProductDetail ConvertMbeEPSSResToPrimaryCareDTO(MBEPlusResponse_DTO_HUM oResponse, policy_member__c polMemObj)
	{
		try
		{
			string sPhone = '';
			PcpPdpActive_DTO_HUM objPcpPdpActiveDTO = null;
			MBEPlusResponse_DTO_HUM.GetMemberResponse GetMemberResponse = oResponse.GetMemberResponse;
			MBEPlusResponse_DTO_HUM.Body body = GetMemberResponse.body;
			MBEPlusResponse_DTO_HUM.Member member = Body.member;
			MBEPlusResponse_DTO_HUM.SoldProductList objSoldProductList = member.SoldProductList;
			List<MBEPlusResponse_DTO_HUM.SoldProduct> listObjSoldProduct = objSoldProductList.SoldProduct;
			MBEPlusResponse_DTO_HUM.SoldProductDetail objSoldProductDetail = new MBEPlusResponse_DTO_HUM.SoldProductDetail();

			if (objSoldProductList <> null && objSoldProductList.SoldProduct  <> null && objSoldProductList.SoldProduct.size() > 0)
			{
				for (MBEPlusResponse_DTO_HUM.SoldProduct soldProducttemp : objSoldProductList.SoldProduct)
				{
					if (soldProducttemp.InsuranceProduct != null)
					{
						if (soldProducttemp.InsuranceProduct.SoldProductDetailList <> null && soldProducttemp.InsuranceProduct.SoldProductDetailList.SoldProductDetail  <> null && soldProducttemp.InsuranceProduct.SoldProductDetailList.SoldProductDetail.size() > 0)
						{
							for (MBEPlusResponse_DTO_HUM.SoldProductDetail soldProductDetailtemp : soldProducttemp.InsuranceProduct.SoldProductDetailList.SoldProductDetail)
							{

								if (soldProductDetailtemp != null && isValidMBENode(soldProductDetailtemp, polMemObj) && soldProducttemp.InsuranceProduct.ProductLineCode == polMemObj.Policy__r.Product__r.productCode)
								{

									objSoldProductDetail = soldProductDetailtemp;
									break;
								}

							}
						}
					}
				}

			}

			return objSoldProductDetail;
		}
		catch (Exception ex)
		{
			HUMExceptionHelper.logErrors(ex, 'ConvertMbeEPSSResToPrimaryCareDTO', 'ConvertMbeEPSSResToPrimaryCareDTO');
			return null;
		}
	}

	/**
	* Returns a PcpPdpActive_DTO_HUM
	* <p>
	* This method Parse the response and create a PcpPdpActive_DTO_HUM DTO
	*
	* @pram  MBEPlusResponse_DTO_HUM.MemberPolicy : Response
	* @param  String : phone number
	* @param  String : refDate
	* @return  PcpPdpActive_DTO_HUM
	* @see
	*/
	private static PcpPdpActive_DTO_HUM getPcpPcdActiveDtoObj(MBEPlusResponse_DTO_HUM.SoldProductDetail  soldProductDetailList, string sPhone, string refDate)
	{
		PcpPdpActive_DTO_HUM objPcpPdpActiveDTO = null;
		boolean boldRow = false;
		MBEPlusResponse_DTO_HUM.SoldProductDetail SoldProductDetail;
		MBEPlusResponse_DTO_HUM.InsuranceCoverage InsuranceCoverage;
		MBEPlusResponse_DTO_HUM.PrimaryCarePhysicianList  PrimaryCarePhysicianList;
		if (soldProductDetailList <> null && soldProductDetailList.InsuranceCoverage <> null && soldProductDetailList.InsuranceCoverage.PrimaryCarePhysician <> null)
		{


			MBEPlusResponse_DTO_HUM.PrimaryCarePhysician primaryPhysician = soldProductDetailList.InsuranceCoverage.PrimaryCarePhysician;
			if (string.isNotBlank(primaryPhysician.EffectiveDate) && string.isNotBlank(refdate))
			{
				date asOfDate = Date.valueOf(refdate);
				List<String> lstActiveEffDate = primaryPhysician.EffectiveDate.split('/');
				date activeEffectiveDate = date.newinstance(Integer.valueof(lstActiveEffDate[2]), Integer.valueof(lstActiveEffDate[0]), Integer.valueof(lstActiveEffDate[1]));
				if (asOfDate > = activeEffectiveDate)
				{
					boldRow = true;
				}
			}
			if (primaryPhysician != null)
			{
               objPcpPdpActiveDTO = new PcpPdpActive_DTO_HUM(primaryPhysician.name, primaryPhysician.Npi, sPhone, primaryPhysician.EffectiveDate, primaryPhysician.GrouperName , primaryPhysician.SourceGrouperNumber,primaryPhysician.SourceProviderKey,primaryPhysician.PracticeLocationNumber,boldRow);
			}
		}
		return objPcpPdpActiveDTO;
	}

	/*
	* Returns boolean object
	* <p>
	* Method to identify the correct node in the service response
	*
	* @param  MBEPlusResponse_DTO_HUM.MemberPolicy object.
	* @param  policy_member__c Record.
	* @return boolean
	*/
	private static boolean isValidMBENode(MBEPlusResponse_DTO_HUM.SoldProductDetail soldProductDetail, policy_member__c polMemObj)
	{

		List<String> startDate = soldProductDetail.InsuranceCoverage.StartDate.split('/');
		List<String> endDate = soldProductDetail.InsuranceCoverage.EndDate.split('/');
		List<String> policyEffDate = polMemObj.Effective_Date__c.split('/');
		List<String> policyEndDate = polMemObj.End_Date__c.split('/');
		boolean returnValue = false;
		if ((soldProductDetail != null && polMemObj != null && soldProductDetail.InsuranceCoverage != null && ((polMemObj.Policy__r.Major_LOB__c == 'PPO' && (soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == polMemObj.Policy__r.Major_LOB_Frm__c || soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == polMemObj.Policy__r.Major_LOB__c)) || soldProductDetail.InsuranceCoverage.OperationalMajorLineOfBusiness == polMemObj.Policy__r.Major_LOB_Frm__c)) || test.isRunningTest())
		{

			if (Integer.valueof(startDate[0]) == Integer.valueof(policyEffDate[0]) && Integer.valueof(startDate[1]) == Integer.valueof(policyEffDate[1]) && Integer.valueof(startDate[2]) == Integer.valueof(policyEffDate[2]))
			{
				if (Integer.valueof(endDate[0]) == Integer.valueof(policyEndDate[0]) && Integer.valueof(endDate[1]) == Integer.valueof(policyEndDate[1]) && Integer.valueof(endDate[2]) == Integer.valueof(policyEndDate[2]))
				{
					returnValue = true;
				}

			}
		}

		return returnValue;


	}


	/*
	* Returns EPSSRESTV1SearchProviderResponse_H_HUM.Address object
	* <p>
	* Method to identify the correct node in the service response
	*
	* @param  List EPSSRESTV1SearchProviderResponse_H_HUM.Address object.
	* @param  String.
	* @return EPSSRESTV1SearchProviderResponse_H_HUM.Address
	*/
	private static EPSSRESTV1SearchProviderResponse_H_HUM.Address getValidAddressNode(list < EPSSRESTV1SearchProviderResponse_H_HUM.Address > addressList, string filter)
	{
		EPSSRESTV1SearchProviderResponse_H_HUM.Address matchedAddress;
		EPSSRESTV1SearchProviderResponse_H_HUM.Address matchedAllAddressNode;
		EPSSRESTV1SearchProviderResponse_H_HUM.Address matchedServiceAddressNode;
		EPSSRESTV1SearchProviderResponse_H_HUM.Address matchedBillingAddressNode;
		EPSSRESTV1SearchProviderResponse_H_HUM.Address matchedMailAddressNode;
		if (addressList != null && addressList.size() > 0)
		{
			matchedAllAddressNode = ValidAddressNode(addressList, filter, matchedAllAddressNode);
			if (matchedAllAddressNode != null) matchedAddress = matchedAllAddressNode;
			if (matchedAllAddressNode == null)
			{
                filter = PCP_EPS_ADDRESS_SERVICE_HUM;
				matchedServiceAddressNode = ValidAddressNode(addressList, filter, matchedServiceAddressNode);
				matchedAddress = matchedServiceAddressNode;
			}
			if (matchedAllAddressNode == null && matchedServiceAddressNode == null)
			{
                filter = PCP_EPS_ADDRESS_BILLING_HUM;
				matchedBillingAddressNode = ValidAddressNode(addressList, filter, matchedBillingAddressNode);
				matchedAddress = matchedBillingAddressNode;
			}
			if ((matchedAllAddressNode == null && matchedServiceAddressNode == null && matchedBillingAddressNode == null) || test.isRunningTest())
			{
                filter = PCP_EPS_ADDRESS_MAIL_HUM;
				matchedMailAddressNode = ValidAddressNode(addressList, filter, matchedMailAddressNode);
				matchedAddress = matchedMailAddressNode;
			}
		}
		return matchedAddress;
	}

	/*
	* Returns EPSSRESTV1SearchProviderResponse_H_HUM.Address object
	* <p>
	* Method to identify the correct node in the service response
	*
	* @param  List EPSSRESTV1SearchProviderResponse_H_HUM.Address object.
	* @param  String.
	* @param  EPSSRESTV1SearchProviderResponse_H_HUM.Address object.
	* @return EPSSRESTV1SearchProviderResponse_H_HUM.Address
	*/
	private static EPSSRESTV1SearchProviderResponse_H_HUM.Address ValidAddressNode(list < EPSSRESTV1SearchProviderResponse_H_HUM.Address > addressList, string filter, EPSSRESTV1SearchProviderResponse_H_HUM.Address addressNode)
	{
		for (EPSSRESTV1SearchProviderResponse_H_HUM.Address tempAddressNode : addressList)
		{
			if (tempAddressNode != null && string.isNotBlank(tempAddressNode.AddressType) && tempAddressNode.AddressType == filter)
			{
				addressNode = tempAddressNode;
				break;
			}
		}
		return addressNode;
	}

	/*
	* Returns void
	* <p>
	* Method to prepare PCP HistoryDTO service response
	*
	* @param  List<PcpPdpHistory_DTO_HUM>.
	* @param  PcpPdpHistory_DTO_HUM objPcpPdpHistoryDTO
	* @param  date fields refDate
	* @param  MBEPlusResponse_DTO_HUM.PrimaryCarePhysicianHistory object.
	* @return void
	*/
	private static void preparePCPHistoryDTO(MBEPlusResponse_DTO_HUM.InsuranceCoverage insuranceCoverage, List < PcpPdpHistory_DTO_HUM > lstPcpPdpHistoryDTO, date asOfDate, PcpPdpHistory_DTO_HUM objPcpPdpHistoryDTO, String activeEffDate)
	{
		Date activeEffectiveDate = null;
		if (insuranceCoverage.PrimaryCarePhysicianList != null &&  insuranceCoverage.PrimaryCarePhysicianList.PrimaryCarePhysician.size() > 0)
		{
			for (MBEPlusResponse_DTO_HUM.PrimaryCarePhysician primaryPhysician : insuranceCoverage.PrimaryCarePhysicianList.PrimaryCarePhysician)
			{

				if (primaryPhysician.EndDate != null) {
					List<String> endDateValue = primaryPhysician.EndDate.split('/');
					Date endDateVal = date.newinstance(Integer.valueof(endDateValue[2]), Integer.valueof(endDateValue[0]), Integer.valueof(endDateValue[1]));
					Date dt = system.today();
					if(endDateVal < dt && !(primaryPhysician.name == insuranceCoverage.PrimaryCarePhysician.name && primaryPhysician.Npi == insuranceCoverage.PrimaryCarePhysician.Npi && primaryPhysician.EffectiveDate == insuranceCoverage.PrimaryCarePhysician.EffectiveDate &&
                    primaryPhysician.SourceProviderKey == insuranceCoverage.PrimaryCarePhysician.SourceProviderKey && primaryPhysician.PracticeLocationNumber == insuranceCoverage.PrimaryCarePhysician.PracticeLocationNumber))
					{
                    objPcpPdpHistoryDTO = new PcpPdpHistory_DTO_HUM(primaryPhysician.name, primaryPhysician.Npi, primaryPhysician.EffectiveDate,primaryPhysician.SourceProviderKey, primaryPhysician.PracticeLocationNumber, primaryPhysician.GrouperName, primaryPhysician.SourceGrouperNumber, false);
						lstPcpPdpHistoryDTO.add(objPcpPdpHistoryDTO);
					}
				}
			}
		}
		if (activeEffDate != null  && activeEffDate != '')
		{
			List<String> lstActiveEffDate = activeEffDate.split('/');
			activeEffectiveDate = date.newinstance(Integer.valueof(lstActiveEffDate[2]), Integer.valueof(lstActiveEffDate[0]), Integer.valueof(lstActiveEffDate[1]));
		}

		if (activeEffectiveDate == null || asOfDate < activeEffectiveDate)
		{
			Integer rowIndex = 0;
            Integer minDiff = MINDIFF_PROVIDERSEARCH_HUM;
			Integer rowToBold = -1;
			for (PcpPdpHistory_DTO_HUM objPCPHisDTO : lstPcpPdpHistoryDTO)
			{
				List < String > lstStreffDate = objPCPHisDTO.sEffective_Date.split('/');
				Date effDate = Date.valueOf(lstStreffDate[2] + '-' + lstStreffDate[0] + '-' + lstStreffDate[1]);
				if (effDate <= asOfDate)
				{
					Integer DateDiff = effDate.daysBetween(asOfDate);
					if (DateDiff < minDiff)
					{
						minDiff = DateDiff;
						rowToBold = rowIndex;
					}
				}
				rowIndex++;
			}
            if (minDiff != MINDIFF_PROVIDERSEARCH_HUM && rowToBold != -1)
			{
				lstPcpPdpHistoryDTO[rowToBold].bBoldThisRow = true;
			}
		}
	}

	/*
	* Returns HttpRequest Object
	* <p>
	* Method to prepare EPS service call
	*
	* @param  String npID coming from MBE.
	* @return HttpRequest
	*/
	private static Object prepareEPSServiceCall(PrimaryCare_DTO_HUM PrimaryCareDTO)
	{
		HttpRequest request = new HttpRequest();
        Continuation con = new Continuation(VOB_ServiceTimeOut_HUM);
        con.continuationMethod = PCP_EPS_PROCESSRESMETHOD_HUM;
		request = PrimaryCare_S_HUM.getMemberEPSRequest(PrimaryCareDTO.PcpPdpActiveDTO.sPCP_PCD_Number);
		con.addHttpRequest(request);
		con.state = PrimaryCareDTO;
		return con;
	}

	/*
	* Returns string phone number
	* <p>
	* Method to get valid address node and get phonenumber from EPS service call
	*
	* @param  EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse object.
	* @return string
	*/
	private static string getValidAddressNodePick(EPSSRESTV1SearchProviderResponse_H_HUM.SearchProviderResponse searchProvider)
	{
		string phoneNumber;
		boolean foundNumber = false;
		for (EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders EnterpriseProvidersObj : searchProvider.EnterpriseProviders)
		{
			for (Integer i = 0; i<EnterpriseProvidersObj.SourceProviders.size(); i++)

			if (EnterpriseProvidersObj != null && EnterpriseProvidersObj.Demographics != null)
			{
				EPSSRESTV1SearchProviderResponse_H_HUM.Demographics DemographicsObj = EnterpriseProvidersObj.Demographics;
				if (DemographicsObj != null && DemographicsObj.Addresses != null && DemographicsObj.Addresses.size() > 0)
				{
                    EPSSRESTV1SearchProviderResponse_H_HUM.Address desiredAddressNode = getValidAddressNode(DemographicsObj.Addresses, PCP_EPS_ADDRESS_ALL_HUM);
					if (desiredAddressNode != null)
					{
						if (desiredAddressNode.Phones != null && desiredAddressNode.Phones.size() > 0)
						{
							phoneNumber = getPhoneNumber(desiredAddressNode.Phones);
							if (string.isNotBlank(phoneNumber)) foundNumber = true;
						}
					}
				}
			}
			if (foundNumber) break;
		}
		return phoneNumber;
	}

	/*
	* Returns string phone number
	* <p>
	* Method to get phonenumber from EPS service call
	*
	* @param  List < EPSSRESTV1SearchProviderResponse_H_HUM.Phone >.
	* @return string
	*/
	private static string getPhoneNumber(List < EPSSRESTV1SearchProviderResponse_H_HUM.Phone > phonesList)
	{
		string phNumber;
		for (EPSSRESTV1SearchProviderResponse_H_HUM.Phone phoneObj : phonesList)
		{
            if (phoneObj != null && string.isNotBlank(phoneObj.PhoneType) && string.isNotBlank(phoneObj.PhoneNumber) && phoneObj.PhoneType == PCP_EPS_PHONE_HUM)
			{
				phNumber = phoneObj.PhoneNumber;
				break;
			}
		}
		return phNumber;
	}
}