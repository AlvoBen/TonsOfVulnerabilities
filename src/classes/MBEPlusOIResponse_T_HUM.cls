/*****************************************************************************************************************************************************************************
    @Apex Class Name  : MBEPlusOIResponse_T_HUM
    @Version          : 1.0
    @Created Date     : June 05, 2020
    @Function         : Test class for MBEPlusOIResponse_DTO_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Keerti Durbha                                    06/05/2020                    Original Version
* Ajay Chakradhar                                  10/05/2020                    US 1467120 Test Coverage
******************************************************************************************************************************************************************************/
@isTest
private class MBEPlusOIResponse_T_HUM{
     /*
    * Method to test MBEPlusOI Response structure
    * @param        None
    * @return       None
    */
    Private Static testmethod void TestMBEPlusOIResponseDTO()
    {
       	User usr = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert usr; 
        system.assertEquals(usr.Lastname == 'test User1',true); 
        system.runAs(usr)
        {
            Test.startTest();
    
    		MBEPlusOIResponse_DTO_HUM  objResponse = new MBEPlusOIResponse_DTO_HUM();
               
            MBEPlusOIResponse_DTO_HUM.GetMemberResponse objGetMemberResponse = new MBEPlusOIResponse_DTO_HUM.GetMemberResponse();
            MBEPlusOIResponse_DTO_HUM.Header objHeader = new MBEPlusOIResponse_DTO_HUM.Header();
            MBEPlusOIResponse_DTO_HUM.ResponseHeader objResponseHeader = new MBEPlusOIResponse_DTO_HUM.ResponseHeader();
            MBEPlusOIResponse_DTO_HUM.ResponseMetaData objResponseMetaData = new MBEPlusOIResponse_DTO_HUM.ResponseMetaData();
            MBEPlusOIResponse_DTO_HUM.DataAuthorizationConstraintList objDataAuthorizationConstraintList = new MBEPlusOIResponse_DTO_HUM.DataAuthorizationConstraintList();
            List<MBEPlusOIResponse_DTO_HUM.DataAuthorizationConstraint> lstDataAuthorizationConstraint = new List<MBEPlusOIResponse_DTO_HUM.DataAuthorizationConstraint>();
            MBEPlusOIResponse_DTO_HUM.DataAuthorizationConstraint objDataAuthorizationConstraint = new MBEPlusOIResponse_DTO_HUM.DataAuthorizationConstraint();
            	objDataAuthorizationConstraint.ConstraintType = '';
            	objDataAuthorizationConstraint.Tenant = '';
            	objDataAuthorizationConstraint.Source = '';	
            	lstDataAuthorizationConstraint.add(objDataAuthorizationConstraint);
            	objDataAuthorizationConstraintList.DataAuthorizationConstraint = lstDataAuthorizationConstraint;
            	
            	objResponseMetaData.DataAuthorizationConstraintList = objDataAuthorizationConstraintList;
            	objResponseMetaData.ClientSessionID = '';
            	objResponseMetaData.SessionID ='';
            	objResponseMetaData.Status ='';
            
            MBEPlusOIResponse_DTO_HUM.Metrics objMetrics = new MBEPlusOIResponse_DTO_HUM.Metrics();
            	objMetrics.ResponseTimeStamp = '';
		 		objMetrics.RequestTimeStamp = '';
		 		objMetrics.Duration = '';    
			MBEPlusOIResponse_DTO_HUM.Server objServer = new MBEPlusOIResponse_DTO_HUM.Server();	
            	objServer.Name = '';
				
            	objResponseHeader.ResponseMetaData = objResponseMetaData;
                objResponseHeader.Environment = '';
                objResponseHeader.Metrics = objMetrics;
                objResponseHeader.Server = objServer;
                objResponseHeader.Version = '';
            	objHeader.ResponseHeader = objResponseHeader;
            	
            MBEPlusOIResponse_DTO_HUM.Body objBody = new MBEPlusOIResponse_DTO_HUM.Body();
			MBEPlusOIResponse_DTO_HUM.Member objMember = new MBEPlusOIResponse_DTO_HUM.Member();
            	objMember.DateOfBirth ='07/01/1959';
                objMember.FirstName = 'JOVETHSY';
                objMember.Gender = 'M';
                objMember.LastName ='MONTAGNE';
                objMember.MasterId = '10408199XX';
                objMember.MiddleInitial ='H';
                objMember.Ssn ='1389334XX';
            
            MBEPlusOIResponse_DTO_HUM.Contact objContact = new MBEPlusOIResponse_DTO_HUM.Contact();
            MBEPlusOIResponse_DTO_HUM.ContactProfile objContactProfile = new MBEPlusOIResponse_DTO_HUM.ContactProfile();
            MBEPlusOIResponse_DTO_HUM.EmailList objEmailList = new MBEPlusOIResponse_DTO_HUM.EmailList();
            MBEPlusOIResponse_DTO_HUM.PersonEmail objPersonEmail = new MBEPlusOIResponse_DTO_HUM.PersonEmail();
            List<MBEPlusOIResponse_DTO_HUM.PersonEmail> lstPersonEmail = new List<MBEPlusOIResponse_DTO_HUM.PersonEmail>();
                objPersonEmail.Address ='';
                objPersonEmail.Type_Z ='';
            	lstPersonEmail.add(objPersonEmail);
            objEmailList.PersonEmail = lstPersonEmail;
            objContactProfile.EmailList = objEmailList;
            
            MBEPlusOIResponse_DTO_HUM.PhoneList objPhoneList = new MBEPlusOIResponse_DTO_HUM.PhoneList();
            MBEPlusOIResponse_DTO_HUM.PersonPhone objPersonPhone = new MBEPlusOIResponse_DTO_HUM.PersonPhone();
            List<MBEPlusOIResponse_DTO_HUM.PersonPhone> lstPersonPhone = new List<MBEPlusOIResponse_DTO_HUM.PersonPhone>();
                objPersonPhone.PhoneNumber ='';
                objPersonPhone.Type_Z ='';
            	lstPersonPhone.add(objPersonPhone);
            objPhoneList.PersonPhone = lstPersonPhone;
            objContactProfile.PhoneList = objPhoneList;
            
            MBEPlusOIResponse_DTO_HUM.AddressList objAddressList = new MBEPlusOIResponse_DTO_HUM.AddressList();
            MBEPlusOIResponse_DTO_HUM.PersonAddress objPersonAddress = new MBEPlusOIResponse_DTO_HUM.PersonAddress();
            List<MBEPlusOIResponse_DTO_HUM.PersonAddress> lstPersonAddress = new List<MBEPlusOIResponse_DTO_HUM.PersonAddress>();
            	objPersonAddress.AddressLine1='Test Address Line';
                objPersonAddress.City='test City';
                objPersonAddress.County='test country';
                objPersonAddress.CountyCode='country code';
                objPersonAddress.DeliveryCode='Delivery code';
                objPersonAddress.EndDate='EndDate';
                objPersonAddress.FIPSCountryCode='Testcod';
                objPersonAddress.FIPSStateCode='Test State code';
                objPersonAddress.ForeignPostalCode='Foregin Postal code';
                objPersonAddress.StartDate='Test start Date';
                objPersonAddress.StateCode='Test State code';
                objPersonAddress.Type_Z='Test typ';
                objPersonAddress.ZipCode='Ip code';
                objPersonAddress.ZipPlus= 'ZIP'; 
        
            MBEPlusOIResponse_DTO_HUM.GeoLocation objGeoLocation=new MBEPlusOIResponse_DTO_HUM.GeoLocation();
            MBEPlusOIResponse_DTO_HUM.Position objPosition=new MBEPlusOIResponse_DTO_HUM.Position();
            MBEPlusOIResponse_DTO_HUM.Coordinates objCoordinates=New MBEPlusOIResponse_DTO_HUM.Coordinates();
                objCoordinates.Latitude='34.56';
                objCoordinates.Longitude='45.67.78';
            	objPosition.AcquiredTime='test Time';
        		objPosition.Coordinates=objCoordinates;
        		objGeoLocation.Position=objPosition;
            	objPersonAddress.GeoLocation=objGeoLocation;
            
            MBEPlusOIResponse_DTO_HUM.PersonIdentifier objPersonIdentifier = new MBEPlusOIResponse_DTO_HUM.PersonIdentifier();
            MBEPlusOIResponse_DTO_HUM.PlatformProxyKey objPlatformProxyKey =new MBEPlusOIResponse_DTO_HUM.PlatformProxyKey();
                objPlatformProxyKey.Application='Test Application';
                objPlatformProxyKey.KeyType='Test KeyType';
                objPlatformProxyKey.KeyValue='Test Keyvalue';
                objPlatformProxyKey.Source='Test Sourse';
        		objPersonIdentifier.PlatformProxyKey = objPlatformProxyKey;
             	objPersonAddress.PersonIdentifier = objPersonIdentifier;
            lstPersonAddress.add(objPersonAddress);
			objAddressList.PersonAddress = lstPersonAddress;
            objContactProfile.AddressList = objAddressList;
            objContact.ContactProfile = objContactProfile;
            objMember.Contact = objContact; 
            
            /* Medicareidentifierlist*/
        	MBEPlusOIResponse_DTO_HUM.MedicareIdentifierList MedicareIdentifierList=new MBEPlusOIResponse_DTO_HUM.MedicareIdentifierList();
        	List<MBEPlusOIResponse_DTO_HUM.MedicareIdentifier> MedicareIdentifiers=new List<MBEPlusOIResponse_DTO_HUM.MedicareIdentifier>();
            MBEPlusOIResponse_DTO_HUM.MedicareIdentifier MedicareIdentifier=new  MBEPlusOIResponse_DTO_HUM.MedicareIdentifier(); 
            	MedicareIdentifier.MedicareId='test Medicareid';
            	MedicareIdentifiers.add(MedicareIdentifier);
            	MedicareIdentifierList.MedicareIdentifier=MedicareIdentifiers;
            objMember.MedicareIdentifierList = MedicareIdentifierList;
            
            MBEPlusOIResponse_DTO_HUM.PlatformPointerList  objPlatformPointerList = new MBEPlusOIResponse_DTO_HUM.PlatformPointerList();
         	MBEPlusOIResponse_DTO_HUM.PlatformProxyKey objPlatformProxyKeyy = new MBEPlusOIResponse_DTO_HUM.PlatformProxyKey();
                 objPlatformProxyKeyy.Application = 'test';
                 objPlatformProxyKeyy.KeyType = 'test';
                 objPlatformProxyKeyy.KeyValue = 'test';
                 objPlatformProxyKeyy.Source  = 'test';
                
            List<MBEPlusOIResponse_DTO_HUM.PlatformProxyKey> listPlatformProxyKey = new List<MBEPlusOIResponse_DTO_HUM.PlatformProxyKey>();
             	listPlatformProxyKey.add(objPlatformProxyKeyy);
            objPlatformPointerList.PlatformProxyKey = listPlatformProxyKey ;
            objMember.PlatformPointerList =  objPlatformPointerList;   
            
            MBEPlusOIResponse_DTO_HUM.Indicator  objIndicator = new MBEPlusOIResponse_DTO_HUM.Indicator();
                objIndicator.Name = 'test';
                objIndicator.Value = 'test';
            List<MBEPlusOIResponse_DTO_HUM.Indicator> listIndicator = new List<MBEPlusOIResponse_DTO_HUM.Indicator>();
            	listIndicator.add(objIndicator);
            MBEPlusOIResponse_DTO_HUM.IndicatorList objIndicatorList = new MBEPlusOIResponse_DTO_HUM.IndicatorList();
            	objIndicatorList.Indicator = listIndicator ;
            objMember.IndicatorList = objIndicatorList;
                
            MBEPlusOIResponse_DTO_HUM.DualStatus objDualStatus = new MBEPlusOIResponse_DTO_HUM.DualStatus();
            List<MBEPlusOIResponse_DTO_HUM.DualStatus> lstDualStatus = new List<MBEPlusOIResponse_DTO_HUM.DualStatus>();
                objDualStatus.Code ='';
                objDualStatus.EffectiveDate ='';
                objDualStatus.EndDate = '';
                objDualStatus.Indicator = True;
                objDualStatus.Value ='';
                lstDualStatus.add(objDualStatus);
            objMember.DualStatusList = lstDualStatus;
            
            MBEPlusOIResponse_DTO_HUM.MessageList objMessageList = new MBEPlusOIResponse_DTO_HUM.MessageList();
            MBEPlusOIResponse_DTO_HUM.Message objMessage = new MBEPlusOIResponse_DTO_HUM.Message();
            List<MBEPlusOIResponse_DTO_HUM.Message> lstMessage = new List<MBEPlusOIResponse_DTO_HUM.Message>();
            	objMessage.Code ='';
            	objMessage.Description ='';
            	objMessage.Source ='';
            	objMessage.Type_Z ='';
            	lstMessage.add(objMessage);
            objMessageList.Message = lstMessage;
            
            MBEPlusOIResponse_DTO_HUM.SoldProductList objSoldProductList = new MBEPlusOIResponse_DTO_HUM.SoldProductList();
            
            MBEPlusOIResponse_DTO_HUM.SoldProduct objSoldProduct = new MBEPlusOIResponse_DTO_HUM.SoldProduct();
            List<MBEPlusOIResponse_DTO_HUM.SoldProduct> lstSoldProduct = new  list<MBEPlusOIResponse_DTO_HUM.SoldProduct>();
            
            MBEPlusOIResponse_DTO_HUM.InsuranceProduct objInsuranceProduct = new MBEPlusOIResponse_DTO_HUM.InsuranceProduct();
            
            MBEPlusOIResponse_DTO_HUM.SoldProductDetailList objSoldProductDetailList = new MBEPlusOIResponse_DTO_HUM.SoldProductDetailList();
                
            MBEPlusOIResponse_DTO_HUM.SoldProductDetail objSoldProductDetail = new MBEPlusOIResponse_DTO_HUM.SoldProductDetail();
            list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail> lstSoldProductDetail = new list<MBEPlusOIResponse_DTO_HUM.SoldProductDetail>();
           
            MBEPlusOIResponse_DTO_HUM.InsuranceCoverage objInsuranceCoverage = new MBEPlusOIResponse_DTO_HUM.InsuranceCoverage();
            MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefitList objCoordinationOfBenefitList = new MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefitList();
            MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit objCoordinationOfBenefit = new MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit();
            List<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit> lstCoordinationOfBenefit = new List<MBEPlusOIResponse_DTO_HUM.CoordinationOfBenefit>();
            	objCoordinationOfBenefit.CarrierName = 'Medicare';
                objCoordinationOfBenefit.EffectiveDate = '04/01/2020';
                objCoordinationOfBenefit.EndDate = '12/31/9999';
                objCoordinationOfBenefit.LastUpdatedDate = '12/31/9999';
                objCoordinationOfBenefit.OILastVerifiedDate = '12/31/9999';
                objCoordinationOfBenefit.Primary = 'Y';
                lstCoordinationOfBenefit.add(objCoordinationOfBenefit);    
     			objCoordinationOfBenefitList.CoordinationOfBenefit = lstCoordinationOfBenefit;
            
                objInsuranceCoverage.CoordinationOfBenefitList = objCoordinationOfBenefitList;
                objInsuranceCoverage.ContractId ='';
                objInsuranceCoverage.CoverageType ='';
                objInsuranceCoverage.EndDate = '';
                objInsuranceCoverage.HcfaEnrollmentSourceCode = '';
                objInsuranceCoverage.OperationalMajorLineofBusiness = '';
                objInsuranceCoverage.PolicySequenceNumber = '';
                objInsuranceCoverage.PolicyType = '';
                objInsuranceCoverage.StartDate = '';
            	objInsuranceCoverage.LongTermCare = '';
            	objInsuranceCoverage.OriginalEffectiveDate = '';
            
            /* CoveredRoleListcovereage*/
            MBEPlusOIResponse_DTO_HUM.CoveredRoleList objCoveredRoleList=new MBEPlusOIResponse_DTO_HUM.CoveredRoleList();
            list<MBEPlusOIResponse_DTO_HUM.CoveredRole> lstCoveredRole =new list<MBEPlusOIResponse_DTO_HUM.CoveredRole>();
            MBEPlusOIResponse_DTO_HUM.CoveredRole objCoveredRole=new MBEPlusOIResponse_DTO_HUM.CoveredRole();
                objCoveredRole.CoverageKey='Test Key';
                objCoveredRole.EndDate='test Enddate';
                objCoveredRole.Hipaa='testHippa';
            	objCoveredRole.ProductRoleType = 'testProductRoleType';
                objCoveredRole.StartDate='test Startdate';
                objCoveredRole.MemberMasterId='test Memberid';
                objCoveredRole.RelationshipToSubscriber='test Suscriber';
            	objCoveredRole.CoveredRolePlatformPointerList = objPlatformPointerList;
                lstCoveredRole.add(objCoveredRole);
            objCoveredRoleList.CoveredRole =  lstCoveredRole;
            objInsuranceCoverage.CoveredRoleList = objCoveredRoleList;
            
            /*Reasoncoverage*/
            MBEPlusOIResponse_DTO_HUM.ReasonList objReasonList=new MBEPlusOIResponse_DTO_HUM.ReasonList();
            list<MBEPlusOIResponse_DTO_HUM.Reason> lstReason=new list<MBEPlusOIResponse_DTO_HUM.Reason>();
            MBEPlusOIResponse_DTO_HUM.Reason objReason=new MBEPlusOIResponse_DTO_HUM.Reason();
                objReason.Code='Test Code';
                objReason.Description='Test Description';
                objReason.Type_Z='Test Type';
                lstReason.add(objReason);
            objReasonList.Reason = lstReason;
            objInsuranceCoverage.ReasonList = objReasonList;
            objInsuranceCoverage.GroupPlatformPointerList = objPlatformPointerList;
            objInsuranceCoverage.IndicatorList = objIndicatorList;
            
            /* Network coverage*/
            MBEPlusOIResponse_DTO_HUM.Network objNetwork = new MBEPlusOIResponse_DTO_HUM.Network();
                objNetwork.CASD1Network='testCASD1';
                objNetwork.CASD2Network='testCASD1' ;
                objNetwork.CASH1Network='testCASH1';
                objNetwork.CASH2Network='testCASSH2';
                objNetwork.NetworkId='testnetworkid';
                
            list<MBEPlusOIResponse_DTO_HUM.NetworkDirectory> lstNetworkDirectory=new list<MBEPlusOIResponse_DTO_HUM.NetworkDirectory>();
            MBEPlusOIResponse_DTO_HUM.NetworkDirectory objNetworkDirectory=new MBEPlusOIResponse_DTO_HUM.NetworkDirectory();
                objNetworkDirectory.Primary ='test Primary';
                objNetworkDirectory.DirectoryId ='test DirectoryId';
                objNetworkDirectory.DisplayName ='test DisplayName';
                objNetworkDirectory.FormNumber ='test FormNumber';
                lstNetworkDirectory.add(objNetworkDirectory);
            MBEPlusOIResponse_DTO_HUM.NetworkDirectoryList  NetworkDirectoryList=new MBEPlusOIResponse_DTO_HUM.NetworkDirectoryList();
                NetworkDirectoryList.NetworkDirectory = lstNetworkDirectory;
                objNetwork.NetworkDirectoryList = NetworkDirectoryList;
            objInsuranceCoverage.Network = objNetwork;
                
            /* PrimaryCare Physician*/
            list<MBEPlusOIResponse_DTO_HUM.PrimaryCarePhysician> lstPrimaryCarePhysician=new List<MBEPlusOIResponse_DTO_HUM.PrimaryCarePhysician>();
            MBEPlusOIResponse_DTO_HUM.PrimaryCarePhysician objPrimaryCarePhysician=new MBEPlusOIResponse_DTO_HUM.PrimaryCarePhysician();
                objPrimaryCarePhysician.EffectiveDate='test Date';
                objPrimaryCarePhysician.EndDate='TestEndDate';
                objPrimaryCarePhysician.GrouperName='Test GroupName';
            	objPrimaryCarePhysician.Npi='test npi';
                objPrimaryCarePhysician.Name='test name';
                objPrimaryCarePhysician.SourceGrouperNumber='Group Number';
                objPrimaryCarePhysician.SourceProviderKey='Key';
                lstPrimaryCarePhysician.add(objPrimaryCarePhysician);
            
            /* PrimaryCare Physicianlist*/
            MBEPlusOIResponse_DTO_HUM.PrimaryCarePhysicianList objPrimaryCarePhysicianList=new MBEPlusOIResponse_DTO_HUM.PrimaryCarePhysicianList();
                objPrimaryCarePhysicianList.PrimaryCarePhysician = lstPrimaryCarePhysician;
            objInsuranceCoverage.PrimaryCarePhysician = objPrimaryCarePhysician;
            objInsuranceCoverage.PrimaryCarePhysicianList =  objPrimaryCarePhysicianList;
                
            MBEPlusOIResponse_DTO_HUM.EnrollmentInfo objEnrollmentInfo = new MBEPlusOIResponse_DTO_HUM.EnrollmentInfo();
                 objEnrollmentInfo.DateofHire = 'test';  
                 objEnrollmentInfo.TimelyIndicator = 'test';    
             MBEPlusOIResponse_DTO_HUM.PriorCarrier objPriorCarrier= new MBEPlusOIResponse_DTO_HUM.PriorCarrier();
                objPriorCarrier.IndicatorList = objIndicatorList ;     
                objEnrollmentInfo.PriorCarrier = objPriorCarrier  ;
             objInsuranceCoverage.EnrollmentInfo = objEnrollmentInfo;
             
             MBEPlusOIResponse_DTO_HUM.Hipaa objHipaa = new MBEPlusOIResponse_DTO_HUM.Hipaa();
                objHipaa.CoverageType = 'test';
                objHipaa.MajorLineOfBusinessCode = 'test';
                objHipaa.MajorLineOfBusinessDesc = 'test';
                objHipaa.RelationshipCode = 'test';
                objHipaa.RelationshipDesc = 'test';
                
                objSoldProductDetail.InsuranceCoverage = objInsuranceCoverage;
                objSoldProductDetailList.SoldProductDetail = lstSoldProductDetail;
            
                objInsuranceProduct.AlternateDescription = '';
                objInsuranceProduct.Description = '';
            	objInsuranceProduct.DeductibleTypeCode = '';
                objInsuranceProduct.IsCarryOverCredit = '';
                objInsuranceProduct.IsCustomerCarveOut = '';
                objInsuranceProduct.IsCustomPlan = '';
                objInsuranceProduct.IsFedQualified = '';
                objInsuranceProduct.IsHdhp = '';
                objInsuranceProduct.IsPca = '';
                objInsuranceProduct.IsPcpRequired = '';
                objInsuranceProduct.IsRxMedicalIntegratedDeductible = '';
                objInsuranceProduct.IsRxSpeciality ='';
                objInsuranceProduct.MajorLineOfBusinessCode = '';
                objInsuranceProduct.MajorLineOfBusinessDescription = '';
                objInsuranceProduct.ProductEndDate = '';
                objInsuranceProduct.ProductId = '';
                objInsuranceProduct.ProductLineCode = '';
                objInsuranceProduct.ProductLineDescription = '';
                objInsuranceProduct.ProductStartDate = '';
                objInsuranceProduct.ProductTypeCode = '';
                objInsuranceProduct.SegmentType = '';
                objInsuranceProduct.SoldProductDetailList = objSoldProductDetailList;
                objInsuranceProduct.Source = '';
                
                objSoldProduct.InsuranceProduct = objInsuranceProduct;
            
            MBEPlusOIResponse_DTO_HUM.NonInsuranceProduct objNonInsuranceProduct = new MBEPlusOIResponse_DTO_HUM.NonInsuranceProduct();
            	objNonInsuranceProduct.Description = 'test d';
                objNonInsuranceProduct.OfferId = 'test d';
                objNonInsuranceProduct.ProductEndDate = 'test d';
                objNonInsuranceProduct.ProductId = 'test d';
                objNonInsuranceProduct.ProductLineCode = 'test d';
                objNonInsuranceProduct.ProductOptionKey = 'test d';
                objNonInsuranceProduct.ProductStartDate = 'test d';
                objNonInsuranceProduct.Source = 'test d';
            
            MBEPlusOIResponse_DTO_HUM.NonInsuranceCoverage objNonInsuranceCoverage = new MBEPlusOIResponse_DTO_HUM.NonInsuranceCoverage();
            	objNonInsuranceCoverage.EndDate = 'test d';
                objNonInsuranceCoverage.OptOut ='test d';
                objNonInsuranceCoverage.StartDate ='test d';
                objNonInsuranceCoverage.VitalityContractId = 'test d';
            	objNonInsuranceCoverage.CoveredRoleList = objCoveredRoleList;
            	objNonInsuranceCoverage.GroupPlatformPointerList = objPlatformPointerList;
            
            MBEPlusOIResponse_DTO_HUM.ParentProductInfo objParentProductInfo = new MBEPlusOIResponse_DTO_HUM.ParentProductInfo();
            	objParentProductInfo.CoverageId ='test';
                objParentProductInfo.IsNonInsurance ='test';
                objParentProductInfo.PlatformCode ='test';
                objParentProductInfo.ProductStartDate ='test';
                objParentProductInfo.ProductID ='test';
                objNonInsuranceCoverage.ParentProductInfo = objParentProductInfo;
            
            /* XReferenceList*/
        	MBEPlusOIResponse_DTO_HUM.XReferenceList XReferenceList=new MBEPlusOIResponse_DTO_HUM.XReferenceList();
            list<MBEPlusOIResponse_DTO_HUM.NameValueList> NameValueLists=new list<MBEPlusOIResponse_DTO_HUM.NameValueList>();
        		MBEPlusOIResponse_DTO_HUM.NameValueList NameValueList=New MBEPlusOIResponse_DTO_HUM.NameValueList(); 
        		NameValueLists.add(NameValueList);
   			list<MBEPlusOIResponse_DTO_HUM.XReference> XReferences=new list<MBEPlusOIResponse_DTO_HUM.XReference>();
        	MBEPlusOIResponse_DTO_HUM.XReference XReference=new MBEPlusOIResponse_DTO_HUM.XReference();
                XReference.KeyType='test key';
                XReference.Name='test name';
                XReference.NameValueList=NameValueLists;
                XReferences.add(XReference);
          	XReferenceList.XReference=XReferences;
        	objNonInsuranceCoverage.XReferenceList =XReferenceList;
            
            objSoldProductDetail.NonInsuranceCoverage = objNonInsuranceCoverage;
            objNonInsuranceProduct.SoldProductDetailList = objSoldProductDetailList;
            objSoldProduct.NonInsuranceProduct = objNonInsuranceProduct;
            lstSoldProduct.add(objSoldProduct);
            objSoldProductList.SoldProduct = lstSoldProduct;
            objMember.SoldProductList = objSoldProductList;
            objBody.Member = objMember;
            objBody.MessageList = objMessageList;
            objGetMemberResponse.Body = objBody;
            objGetMemberResponse.Header = objHeader;
            objResponse.GetMemberResponse = objGetMemberResponse;
            system.assertNotEquals(null, objResponse);
            Test.stopTest();    
        }
    }
}