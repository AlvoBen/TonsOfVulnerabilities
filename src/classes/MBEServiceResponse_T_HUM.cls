/**************************************************************************** 
Apex Class Name  : MBEServiceResponse_T_HUM
Version          : 1.0  
Created Date     : February 05,2016
Function         : test class for MBEServiceResponse_H_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* ChiranjeeviRao Ravuri    29768                02/05/2015                    Original Version
***************************************************************************************************/
@isTest
private Class MBEServiceResponse_T_HUM
{
    /*
    * Method Name : testSetup 
    * Description : Test method to setup common test data for all test methods     
    * Return type : void     
    * Paramater   : NA  
    */
    @testSetup 
    static void testSetup()
    {        
        HUMTestDataHelperTest.getHUMConstantsData();
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert oUser;
    }
    /*
     * Method Name : testMBEServiceResponse
     * Description : Test method for  MBEServiceResponse_H_HUM
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testMBEServiceResponse()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];                
        system.runAs(oUser)
        {
          Test.StartTest();
            MBEServiceResponse_H_HUM oResponse = new MBEServiceResponse_H_HUM();
            MBEServiceResponse_H_HUM.GetMemberResponse getMemRes = new MBEServiceResponse_H_HUM.GetMemberResponse();
            MBEServiceResponse_H_HUM.Header header = new MBEServiceResponse_H_HUM.Header();
            MBEServiceResponse_H_HUM.ResponseHeader resHeader = new MBEServiceResponse_H_HUM.ResponseHeader();
            MBEServiceResponse_H_HUM.Server server = new MBEServiceResponse_H_HUM.Server();
            MBEServiceResponse_H_HUM.Metrics metrics = new MBEServiceResponse_H_HUM.Metrics(); 
            MBEServiceResponse_H_HUM.ResponseMetaData resMetaData = new MBEServiceResponse_H_HUM.ResponseMetaData();
            MBEServiceResponse_H_HUM.DataAuthorizationConstraintList dataAuthConList = new MBEServiceResponse_H_HUM.DataAuthorizationConstraintList();
            MBEServiceResponse_H_HUM.DataAuthorizationConstraint dataAuthCon = new MBEServiceResponse_H_HUM.DataAuthorizationConstraint();
            MBEServiceResponse_H_HUM.PageMetaData pageMetaData = new MBEServiceResponse_H_HUM.PageMetaData();
            MBEServiceResponse_H_HUM.SortCriteria sortCriteria = new MBEServiceResponse_H_HUM.SortCriteria();
            MBEServiceResponse_H_HUM.FirstName firstName = new MBEServiceResponse_H_HUM.FirstName();
            MBEServiceResponse_H_HUM.LastName lastName = new MBEServiceResponse_H_HUM.LastName(); 
            MBEServiceResponse_H_HUM.Body body = new MBEServiceResponse_H_HUM.Body();
            MBEServiceResponse_H_HUM.MemberMasterList memMasterList = new MBEServiceResponse_H_HUM.MemberMasterList();
            MBEServiceResponse_H_HUM.MemberMaster memMaster = new MBEServiceResponse_H_HUM.MemberMaster(); 
            MBEServiceResponse_H_HUM.EmailList emailList = new MBEServiceResponse_H_HUM.EmailList();
            MBEServiceResponse_H_HUM.Email email = new MBEServiceResponse_H_HUM.Email();
            MBEServiceResponse_H_HUM.PhoneList phoneList = new MBEServiceResponse_H_HUM.PhoneList();
            MBEServiceResponse_H_HUM.Phone phone = new MBEServiceResponse_H_HUM.Phone();
            MBEServiceResponse_H_HUM.AddressList addList = new MBEServiceResponse_H_HUM.AddressList();
            MBEServiceResponse_H_HUM.Address add = new MBEServiceResponse_H_HUM.Address();
            MBEServiceResponse_H_HUM.GeoLocation geoLocation = new MBEServiceResponse_H_HUM.GeoLocation(); 
            MBEServiceResponse_H_HUM.Position position = new MBEServiceResponse_H_HUM.Position();
            MBEServiceResponse_H_HUM.Coordinates coordinates = new MBEServiceResponse_H_HUM.Coordinates();
            MBEServiceResponse_H_HUM.MemberMasterPlatformPointerList memMasPlatpontList = new MBEServiceResponse_H_HUM.MemberMasterPlatformPointerList(); 
            MBEServiceResponse_H_HUM.MemberMasterPlatformPointer memMasPlatPont = new MBEServiceResponse_H_HUM.MemberMasterPlatformPointer();
            MBEServiceResponse_H_HUM.MemberPlatformList memPlatList = new MBEServiceResponse_H_HUM.MemberPlatformList(); 
            MBEServiceResponse_H_HUM.MemberPlatform memPlat = new MBEServiceResponse_H_HUM.MemberPlatform(); 
            MBEServiceResponse_H_HUM.BlockCodeList blockCodeList = new MBEServiceResponse_H_HUM.BlockCodeList();
            MBEServiceResponse_H_HUM.BlockCode blockCode = new MBEServiceResponse_H_HUM.BlockCode();
            MBEServiceResponse_H_HUM.BlockOverrideList blockOvList = new MBEServiceResponse_H_HUM.BlockOverrideList();
            MBEServiceResponse_H_HUM.BlockOverride blockOv = new MBEServiceResponse_H_HUM.BlockOverride();
            MBEServiceResponse_H_HUM.LowIncomeSubsidyLevelList lowIncSubLevelList = new MBEServiceResponse_H_HUM.LowIncomeSubsidyLevelList();
            MBEServiceResponse_H_HUM.LowIncomeSubsidyLevel lowIncSubLevel = new MBEServiceResponse_H_HUM.LowIncomeSubsidyLevel();
            MBEServiceResponse_H_HUM.IndicatorList indList = new  MBEServiceResponse_H_HUM.IndicatorList();
            MBEServiceResponse_H_HUM.Indicator ind = new MBEServiceResponse_H_HUM.Indicator();
            MBEServiceResponse_H_HUM.MemberPolicyList memPolList = new MBEServiceResponse_H_HUM.MemberPolicyList(); 
            MBEServiceResponse_H_HUM.MemberPolicy memPol = new MBEServiceResponse_H_HUM.MemberPolicy(); 
            MBEServiceResponse_H_HUM.CoordinationOfBenefitList cobList = new MBEServiceResponse_H_HUM.CoordinationOfBenefitList();  
            MBEServiceResponse_H_HUM.CoordinationOfBenefit cob = new MBEServiceResponse_H_HUM.CoordinationOfBenefit(); 
            MBEServiceResponse_H_HUM.MemberPolicyRoleList memPolRoleList = new MBEServiceResponse_H_HUM.MemberPolicyRoleList(); 
            MBEServiceResponse_H_HUM.MemberPolicyRole memPloRole = new MBEServiceResponse_H_HUM.MemberPolicyRole(); 
            MBEServiceResponse_H_HUM.MemberPolicyRolePlatformPointerList mempolRolePlatPntList = new MBEServiceResponse_H_HUM.MemberPolicyRolePlatformPointerList(); 
            MBEServiceResponse_H_HUM.MemberPolicyRolePlatformPointer memPolRolePlatPnt = new MBEServiceResponse_H_HUM.MemberPolicyRolePlatformPointer(); 
            MBEServiceResponse_H_HUM.EnhancedHealthBenefit enhHealthBen = new MBEServiceResponse_H_HUM.EnhancedHealthBenefit();
            MBEServiceResponse_H_HUM.PrimaryCarePhysicianList primCarePhyList = new  MBEServiceResponse_H_HUM.PrimaryCarePhysicianList(); 
            MBEServiceResponse_H_HUM.PrimaryCarePhysician primCarePhy = new MBEServiceResponse_H_HUM.PrimaryCarePhysician();
            MBEServiceResponse_H_HUM.OptionalSupplementalBenefitList optSuppBenList = new MBEServiceResponse_H_HUM.OptionalSupplementalBenefitList(); 
            MBEServiceResponse_H_HUM.OptionalSupplementalBenefit optSuppBen = new  MBEServiceResponse_H_HUM.OptionalSupplementalBenefit();
            MBEServiceResponse_H_HUM.WaitingPeriodList watPerList = new MBEServiceResponse_H_HUM.WaitingPeriodList();
            MBEServiceResponse_H_HUM.WaitingPeriod watPer = new MBEServiceResponse_H_HUM.WaitingPeriod();
            MBEServiceResponse_H_HUM.WaitingPeriodIndicatorList watPerIndList = new MBEServiceResponse_H_HUM.WaitingPeriodIndicatorList(); 
            MBEServiceResponse_H_HUM.NetworkList netList = new MBEServiceResponse_H_HUM.NetworkList();
            MBEServiceResponse_H_HUM.Network net = new MBEServiceResponse_H_HUM.Network(); 
            MBEServiceResponse_H_HUM.NetworkDirectoryList netDirList = new MBEServiceResponse_H_HUM.NetworkDirectoryList();
            MBEServiceResponse_H_HUM.NetworkDirectory netDir = new MBEServiceResponse_H_HUM.NetworkDirectory(); 
            MBEServiceResponse_H_HUM.MemberPolicyProductPlatformPointerList memPolProPlatPntList = new MBEServiceResponse_H_HUM.MemberPolicyProductPlatformPointerList();
            MBEServiceResponse_H_HUM.MemberPolicyProductPlatformPointer memPolProPlatPnt = new MBEServiceResponse_H_HUM.MemberPolicyProductPlatformPointer();
            MBEServiceResponse_H_HUM.Product prod = new MBEServiceResponse_H_HUM.Product();
            MBEServiceResponse_H_HUM.RiderList rdList = new MBEServiceResponse_H_HUM.RiderList();
            MBEServiceResponse_H_HUM.Rider rd = new MBEServiceResponse_H_HUM.Rider();
            MBEServiceResponse_H_HUM.BenefitList benList = new MBEServiceResponse_H_HUM.BenefitList();
            MBEServiceResponse_H_HUM.Benefit ben = new MBEServiceResponse_H_HUM.Benefit();
            MBEServiceResponse_H_HUM.EligibilityBenefitCodeList elgBenCodeList = new MBEServiceResponse_H_HUM.EligibilityBenefitCodeList();
            MBEServiceResponse_H_HUM.BenefitCode benCode = new MBEServiceResponse_H_HUM.BenefitCode();
            MBEServiceResponse_H_HUM.HealthCareServicesDirectoryCodeList healthCarSerDirCodeList = new MBEServiceResponse_H_HUM.HealthCareServicesDirectoryCodeList();
            MBEServiceResponse_H_HUM.DirectoryCode dirCode = new MBEServiceResponse_H_HUM.DirectoryCode();
            MBEServiceResponse_H_HUM.XReferenceList xRefList = new MBEServiceResponse_H_HUM.XReferenceList();
            MBEServiceResponse_H_HUM.XReference xRef = new MBEServiceResponse_H_HUM.XReference();
            MBEServiceResponse_H_HUM.NameValueList nameValList = new MBEServiceResponse_H_HUM.NameValueList();
            MBEServiceResponse_H_HUM.NameValue nameValue = new MBEServiceResponse_H_HUM.NameValue();
            MBEServiceResponse_H_HUM.GroupPolicy grpPolicy = new MBEServiceResponse_H_HUM.GroupPolicy();
            MBEServiceResponse_H_HUM.PlanComment planComm = new MBEServiceResponse_H_HUM.PlanComment();
            MBEServiceResponse_H_HUM.MemberPolicyGroupPlatformPointerList memPolGrpPlatPntList = new MBEServiceResponse_H_HUM.MemberPolicyGroupPlatformPointerList();
            MBEServiceResponse_H_HUM.MemberPolicyGroupPlatformPointer memPolGrpPnt = new MBEServiceResponse_H_HUM.MemberPolicyGroupPlatformPointer();
            MBEServiceResponse_H_HUM.AdvancedPremiumTaxCreditList advPremTaxCreList = new MBEServiceResponse_H_HUM.AdvancedPremiumTaxCreditList();
            MBEServiceResponse_H_HUM.AdvancedPremiumTaxCredit advPremTaxCre = new MBEServiceResponse_H_HUM.AdvancedPremiumTaxCredit();
            MBEServiceResponse_H_HUM.MemberPolicyPlatformPointerList memPolPlatPntList = new MBEServiceResponse_H_HUM.MemberPolicyPlatformPointerList();
            MBEServiceResponse_H_HUM.MemberPolicyPlatformPointer memPolPlatPnt = new MBEServiceResponse_H_HUM.MemberPolicyPlatformPointer();
            MBEServiceResponse_H_HUM.PolicyIndicatorList polindList = new MBEServiceResponse_H_HUM.PolicyIndicatorList();    
            MBEServiceResponse_H_HUM.EnrollmentInformation  enrollInfo = new MBEServiceResponse_H_HUM.EnrollmentInformation();
            MBEServiceResponse_H_HUM.PriorCarrier  preCarrier = new MBEServiceResponse_H_HUM.PriorCarrier();
            MBEServiceResponse_H_HUM.PriorCarrierIndicatorList  priorCarrIndList = new MBEServiceResponse_H_HUM.PriorCarrierIndicatorList();
            MBEServiceResponse_H_HUM.PrimaryCarePhysicianHistory  primCarePhyHistory = new MBEServiceResponse_H_HUM.PrimaryCarePhysicianHistory();
            MBEServiceResponse_H_HUM.CSRLevel csrLevel = new MBEServiceResponse_H_HUM.CSRLevel();
            MBEServiceResponse_H_HUM.CSRLevelHistory csrLevHistory = new MBEServiceResponse_H_HUM.CSRLevelHistory();
            MBEServiceResponse_H_HUM.CostShareReduction cstShareRed = new MBEServiceResponse_H_HUM.CostShareReduction();
            MBEServiceResponse_H_HUM.OutOfAreaList outOfAreaList = new MBEServiceResponse_H_HUM.OutOfAreaList();
            MBEServiceResponse_H_HUM.OutOfArea outOfArea = new MBEServiceResponse_H_HUM.OutOfArea();
            MBEServiceResponse_H_HUM.MemberProgramList memProgList = new MBEServiceResponse_H_HUM.MemberProgramList();
            MBEServiceResponse_H_HUM.MemberProgram  memProg = new MBEServiceResponse_H_HUM.MemberProgram();
            MBEServiceResponse_H_HUM.MemberProgramRoleList  memProgRoleList = new MBEServiceResponse_H_HUM.MemberProgramRoleList();
            MBEServiceResponse_H_HUM.MemberProgramRole  memProgRole = new MBEServiceResponse_H_HUM.MemberProgramRole();
            MBEServiceResponse_H_HUM.PlatformPointerList  platPointerList = new MBEServiceResponse_H_HUM.PlatformPointerList();
            MBEServiceResponse_H_HUM.PlatformPointer  platPointer = new MBEServiceResponse_H_HUM.PlatformPointer();
            MBEServiceResponse_H_HUM.GroupProgram  grpProgram = new MBEServiceResponse_H_HUM.GroupProgram();
            MBEServiceResponse_H_HUM.MemberProgramGroupPlatformPointerList memProgGrpPlatPntList = new MBEServiceResponse_H_HUM.MemberProgramGroupPlatformPointerList();
            MBEServiceResponse_H_HUM.MemberProgramGroupPlatformPointer memProgGrpPlatPnt = new MBEServiceResponse_H_HUM.MemberProgramGroupPlatformPointer(); 
            MBEServiceResponse_H_HUM.Order order = new MBEServiceResponse_H_HUM.Order();  
            MBEServiceResponse_H_HUM.Utilization ut = new MBEServiceResponse_H_HUM.Utilization();
            MBEServiceResponse_H_HUM.MemberProgramProductPlatformPointerList memProgprodPlatPntList = new MBEServiceResponse_H_HUM.MemberProgramProductPlatformPointerList();
            MBEServiceResponse_H_HUM.MemberProgramProductPlatformPointer memProgProdPlatPnt = new  MBEServiceResponse_H_HUM.MemberProgramProductPlatformPointer();
            MBEServiceResponse_H_HUM.MemberProgramPlatformPointerList memProgPlatPntList = new MBEServiceResponse_H_HUM.MemberProgramPlatformPointerList();
            MBEServiceResponse_H_HUM.MemberProgramPlatformPointer memProgPlatPnt = new MBEServiceResponse_H_HUM.MemberProgramPlatformPointer();
            MBEServiceResponse_H_HUM.MemberProgramEndReasonList memProgEndReList = new MBEServiceResponse_H_HUM.MemberProgramEndReasonList(); 
            MBEServiceResponse_H_HUM.MemberProgramEndReason memProgEndRe = new MBEServiceResponse_H_HUM.MemberProgramEndReason();
            MBEServiceResponse_H_HUM.LongTermCareEligibilityList longTermEliList = new MBEServiceResponse_H_HUM.LongTermCareEligibilityList(); 
            MBEServiceResponse_H_HUM.LongTermCareEligibility longTermcarEli = new MBEServiceResponse_H_HUM.LongTermCareEligibility();
            MBEServiceResponse_H_HUM.MemberIdList memIdList = new MBEServiceResponse_H_HUM.MemberIdList(); 
            MBEServiceResponse_H_HUM.GroupIdList grpIdList = new MBEServiceResponse_H_HUM.GroupIdList(); 
            MBEServiceResponse_H_HUM.GroupId grpId = new MBEServiceResponse_H_HUM.GroupId();
            MBEServiceResponse_H_HUM.HcfaDualEnrolledIndicator hcfaDulEnrollInd = new MBEServiceResponse_H_HUM.HcfaDualEnrolledIndicator();
            MBEServiceResponse_H_HUM.SourcePlatform sourcePlat = new MBEServiceResponse_H_HUM.SourcePlatform(); 
            MBEServiceResponse_H_HUM.ContactProfileAdditional conProfAdd = new MBEServiceResponse_H_HUM.ContactProfileAdditional();
            MBEServiceResponse_H_HUM.EmailAdditional emailAdd = new  MBEServiceResponse_H_HUM.EmailAdditional();
            MBEServiceResponse_H_HUM.EmailAdditionalIdentifier emailAddIden = new MBEServiceResponse_H_HUM.EmailAdditionalIdentifier();
            MBEServiceResponse_H_HUM.EmailAdditionalAudit emailAddAudit = new MBEServiceResponse_H_HUM.EmailAdditionalAudit(); 
            MBEServiceResponse_H_HUM.PhoneAdditional phoneAdd = new MBEServiceResponse_H_HUM.PhoneAdditional();
            MBEServiceResponse_H_HUM.PhoneAdditionalIdentifier phoneAddIdent = new  MBEServiceResponse_H_HUM.PhoneAdditionalIdentifier(); 
            MBEServiceResponse_H_HUM.PhoneAdditionalAudit phoneAddAudit = new MBEServiceResponse_H_HUM.PhoneAdditionalAudit(); 
            MBEServiceResponse_H_HUM.AddressAdditional addAdditional = new MBEServiceResponse_H_HUM.AddressAdditional(); 
            MBEServiceResponse_H_HUM.AddressAdditionalIdentifier addAdditionalIdent = new MBEServiceResponse_H_HUM.AddressAdditionalIdentifier();
            MBEServiceResponse_H_HUM.PostalAddressAdditionalAudit postAddressAddAudit = new MBEServiceResponse_H_HUM.PostalAddressAdditionalAudit(); 
            MBEServiceResponse_H_HUM.MemberIdentity memIdent = new MBEServiceResponse_H_HUM.MemberIdentity();
            MBEServiceResponse_H_HUM.MessageList messageList = new MBEServiceResponse_H_HUM.MessageList();
            MBEServiceResponse_H_HUM.Message message = new MBEServiceResponse_H_HUM.Message();
            system.assert(oResponse!=null);
          Test.StopTest();
        }
    }  
    
}