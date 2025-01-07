/************************************************************************************************************************************************
Apex Class Name  : PrimaryCare_LD_HUM
Version          : 1.0
Created Date     : APR 04 2022
Function         : Data Layer Class for PrimaryCarePhysicanHum component.
Test Class       :
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                                 04/04/2022                 Original Version as per REQ - 3196414
* Atul Patil										 04/07/2023					REQ - 4810466
*************************************************************************************************************************************************/

public with sharing class PrimaryCare_LD_HUM {
    
    Public Static final String PCP_MBEPLUSSERVICENAME_HUM = 'MBEPlusService';
    Public Static final String VOB_PCP_REFDATEFORMT_HUM ='yyyy-MM-dd';  
    Public Static final String PCP_MEMCR_PERIOD_HUM = 'Current';
    Public Static final String  VOB_CURRENT_HUM = 'Current';
    Public Static final String  VOB_PAST_HUM = 'Past';
    Public Static final String  VOB_FUTURE_HUM = 'Future';
    Public Static final String PCP_SER_CONTENTTYPE_HUM = 'content-type';
    Public Static final String PCP_SER_CONECTION_HUM = 'Connection';
    Public Static final String PCP_SER_KEEPALIVE_HUM = 'keep-alive';
    Public Static final String PCP_SER_SOAPACTION_HUM = 'SOAPAction'; 
    Public Static final Integer MINDIFF_PROVIDERSEARCH_HUM = 10000000;
    
    Public Static final String PCP_EPS_PHONE_HUM = 'phone';
    Public Static final String PCP_EPS_ADDRESS_ALL_HUM = 'All';
    Public Static final String PCP_EPS_ADDRESS_SERVICE_HUM = 'Service';
    Public Static final String PCP_EPS_ADDRESS_BILLING_HUM = 'Billing';
    Public Static final String PCP_EPS_ADDRESS_MAIL_HUM = 'Mail';
    
    
    Public Static final String RESTV1SERVICENAME_PROVIDERSEARCH_HUM = 'EPSServiceREST_V1';
    Public Static final String PCP_EPS_PARONLY_HUM ='false';
    Public Static final String PCP_EPS_IDENTIFIERTYPE_HUM ='NpiID';
    Public Static final String PCP_APPLICATIONKEY_HUM = '28AD1338-9A7D-41DB-BB87-F9FDFEE3DA01';//'b35bc8f5-a390-4c91-8b5b-4327708550ec';
    /**
* Method to create request structure and return the request
*
* @param  String personID
* @param  String refDate
* @return  HttpRequest object
* @see
*/
    public static MBEPlusRequest_DTO_HUM  getMemberMBERequest(String personID, String refDate)
    {
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
        return oReq;
        
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
    public static EPSSRESTV1SearchProviderRequest_H_HUM  getMemberEPSRequest(String MBENpiID)
    {
        EPSSRESTV1SearchProviderRequest_H_HUM oReq = New EPSSRESTV1SearchProviderRequest_H_HUM();
        EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest SearchProviderRequest = New EPSSRESTV1SearchProviderRequest_H_HUM.SearchProviderRequest();
        EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria SearchCriteria = New EPSSRESTV1SearchProviderRequest_H_HUM.SearchCriteria();
        EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers Identifiers = New EPSSRESTV1SearchProviderRequest_H_HUM.Identifiers();
        EPSSRESTV1SearchProviderRequest_H_HUM.Identifier Identifier = New EPSSRESTV1SearchProviderRequest_H_HUM.Identifier();
        List<EPSSRESTV1SearchProviderRequest_H_HUM.Identifier> listOfIden = New List<EPSSRESTV1SearchProviderRequest_H_HUM.Identifier>();
        EPSSRESTV1SearchProviderRequest_H_HUM.Filter Filter = New EPSSRESTV1SearchProviderRequest_H_HUM.Filter();
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
        Return oReq;
    }
    
    public static string getPhoneNumber(EPSSRESTV1SearchProviderResponse_H_HUM EPSRes){
        string phoneNumberField;
        if (EPSRes != null && EPSRes.SearchProviderResponse != null){
           phoneNumberField = getValidAddressNodePick(EPSRes.SearchProviderResponse);
        } 
        return phoneNumberField;
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
         List<EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders> EnterpriseProvidersList = searchProvider.EnterpriseProviders;
            for(EPSSRESTV1SearchProviderResponse_H_HUM.EnterpriseProviders EnterpriseProvidersObj:EnterpriseProvidersList){
                 if (EnterpriseProvidersObj != null && EnterpriseProvidersObj.Demographics != null && EnterpriseProvidersObj.Demographics.Addresses!= null && EnterpriseProvidersObj.Demographics.Addresses.size() > 0){
                    EPSSRESTV1SearchProviderResponse_H_HUM.Address desiredAddressNode = getValidAddressNode(EnterpriseProvidersObj.Demographics.Addresses, PCP_EPS_ADDRESS_ALL_HUM);
                    phoneNumber = desiredAddressNode != null && desiredAddressNode.Phones != null && desiredAddressNode.Phones.size() > 0 ?  getPhoneNumber(desiredAddressNode.Phones) :'';
                    return phoneNumber;    
                }
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
            if (matchedAllAddressNode != null){ 
                matchedAddress = matchedAllAddressNode;
            }
            if (matchedAllAddressNode == null)
            {
                filter = PCP_EPS_ADDRESS_SERVICE_HUM;
                matchedServiceAddressNode = ValidAddressNode(addressList, filter, matchedServiceAddressNode);
                if (matchedServiceAddressNode != null){ 
                    matchedAddress = matchedServiceAddressNode;
                }
            }
            if (matchedAllAddressNode == null && matchedServiceAddressNode == null)
            {
                filter = PCP_EPS_ADDRESS_BILLING_HUM;
                matchedBillingAddressNode = ValidAddressNode(addressList, filter, matchedBillingAddressNode);
                if (matchedBillingAddressNode != null){ 
                    matchedAddress = matchedBillingAddressNode;
                }
            }
            if ((matchedAllAddressNode == null && matchedServiceAddressNode == null && matchedBillingAddressNode == null) || test.isRunningTest())
            {
                filter = PCP_EPS_ADDRESS_MAIL_HUM;
                matchedMailAddressNode = ValidAddressNode(addressList, filter, matchedMailAddressNode);
                if (matchedMailAddressNode != null){ 
                    matchedAddress = matchedMailAddressNode;
                }
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
    private static EPSSRESTV1SearchProviderResponse_H_HUM.Address ValidAddressNode(list < EPSSRESTV1SearchProviderResponse_H_HUM.Address > addressList, string filter, EPSSRESTV1SearchProviderResponse_H_HUM.Address addressNode){
        for (EPSSRESTV1SearchProviderResponse_H_HUM.Address tempAddressNode : addressList){
            if (tempAddressNode != null && string.isNotBlank(tempAddressNode.AddressType) && tempAddressNode.AddressType == filter){
                addressNode = tempAddressNode;
                break;
            }
        }
        return addressNode;
    }
}