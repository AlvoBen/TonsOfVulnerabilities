/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityResponseV2_T_HUM 
Version          : 1.0  
Created Date     : April 12, 2018
Function         : test class for ProductBusinessEntityResponseV2_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rakesh Patni              220187          04/12/2018                    Original Version
***************************************************************************************************/
@isTest
private Class ProductBusinessEntityResponseV2_T_HUM 
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
     * Method Name : testProductBusinessEntityResponseV2_H_HUM
     * Description : Test method for ProductBusinessEntityResponseV2_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testProductBusinessEntityResponseV2_H_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            ProductBusinessEntityResponseV2_H_HUM oResponse = new ProductBusinessEntityResponseV2_H_HUM();
            ProductBusinessEntityResponseV2_H_HUM.GetProductResponse getProRes = new ProductBusinessEntityResponseV2_H_HUM.GetProductResponse(); 
            ProductBusinessEntityResponseV2_H_HUM.ProductList proList = new ProductBusinessEntityResponseV2_H_HUM.ProductList(); 
            ProductBusinessEntityResponseV2_H_HUM.Product pro = new ProductBusinessEntityResponseV2_H_HUM.Product();
            ProductBusinessEntityResponseV2_H_HUM.Categorization cate = new ProductBusinessEntityResponseV2_H_HUM.Categorization();
            ProductBusinessEntityResponseV2_H_HUM.Category cat = new ProductBusinessEntityResponseV2_H_HUM.Category();
            ProductBusinessEntityResponseV2_H_HUM.Line line = new ProductBusinessEntityResponseV2_H_HUM.Line();
            ProductBusinessEntityResponseV2_H_HUM.Family fam = new ProductBusinessEntityResponseV2_H_HUM.Family();
            ProductBusinessEntityResponseV2_H_HUM.Metadata mData = new ProductBusinessEntityResponseV2_H_HUM.Metadata();
            ProductBusinessEntityResponseV2_H_HUM.LocationList loclst = new ProductBusinessEntityResponseV2_H_HUM.LocationList();
            ProductBusinessEntityResponseV2_H_HUM.Location loc = new ProductBusinessEntityResponseV2_H_HUM.Location();
            ProductBusinessEntityResponseV2_H_HUM.ZipList ziplst = new ProductBusinessEntityResponseV2_H_HUM.ZipList();
            ProductBusinessEntityResponseV2_H_HUM.Zip zi = new ProductBusinessEntityResponseV2_H_HUM.Zip();
            ProductBusinessEntityResponseV2_H_HUM.Country cntry = new ProductBusinessEntityResponseV2_H_HUM.Country();
            ProductBusinessEntityResponseV2_H_HUM.State sta = new ProductBusinessEntityResponseV2_H_HUM.State();
            ProductBusinessEntityResponseV2_H_HUM.County cnt = new ProductBusinessEntityResponseV2_H_HUM.County();
            ProductBusinessEntityResponseV2_H_HUM.QuoteDates quoDates = new ProductBusinessEntityResponseV2_H_HUM.QuoteDates();
            ProductBusinessEntityResponseV2_H_HUM.Definition def = new ProductBusinessEntityResponseV2_H_HUM.Definition();
            ProductBusinessEntityResponseV2_H_HUM.IdentifierList idenlst = new ProductBusinessEntityResponseV2_H_HUM.IdentifierList(); 
            ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier proIden = new ProductBusinessEntityResponseV2_H_HUM.ProductIdentifier();
            ProductBusinessEntityResponseV2_H_HUM.AlternateNameList altlst = new ProductBusinessEntityResponseV2_H_HUM.AlternateNameList();
            ProductBusinessEntityResponseV2_H_HUM.AlternateName altName = new ProductBusinessEntityResponseV2_H_HUM.AlternateName();
            ProductBusinessEntityResponseV2_H_HUM.Detail det = new ProductBusinessEntityResponseV2_H_HUM.Detail();
            ProductBusinessEntityResponseV2_H_HUM.ServiceList serlst = new ProductBusinessEntityResponseV2_H_HUM.ServiceList();
            ProductBusinessEntityResponseV2_H_HUM.ServiceCategoryList scatlst = new ProductBusinessEntityResponseV2_H_HUM.ServiceCategoryList();
            ProductBusinessEntityResponseV2_H_HUM.PlaceOfServiceList pslst = new ProductBusinessEntityResponseV2_H_HUM.PlaceOfServiceList();
            ProductBusinessEntityResponseV2_H_HUM.BenefitCodesList bencodelst = new ProductBusinessEntityResponseV2_H_HUM.BenefitCodesList();
            ProductBusinessEntityResponseV2_H_HUM.RiderList ridlst = new ProductBusinessEntityResponseV2_H_HUM.RiderList();
            ProductBusinessEntityResponseV2_H_HUM.PackageInfo pacInfo = new ProductBusinessEntityResponseV2_H_HUM.PackageInfo();
            ProductBusinessEntityResponseV2_H_HUM.EffectiveDates effDate = new ProductBusinessEntityResponseV2_H_HUM.EffectiveDates();
            ProductBusinessEntityResponseV2_H_HUM.GeneralBenefits genBen = new ProductBusinessEntityResponseV2_H_HUM.GeneralBenefits();
            ProductBusinessEntityResponseV2_H_HUM.BenefitIndicatorList biLst = new ProductBusinessEntityResponseV2_H_HUM.BenefitIndicatorList();
            ProductBusinessEntityResponseV2_H_HUM.Deductible deduc = new ProductBusinessEntityResponseV2_H_HUM.Deductible();
            ProductBusinessEntityResponseV2_H_HUM.Accumulates acc = new ProductBusinessEntityResponseV2_H_HUM.Accumulates();
            ProductBusinessEntityResponseV2_H_HUM.InNetwork inNet = new ProductBusinessEntityResponseV2_H_HUM.InNetwork();
            ProductBusinessEntityResponseV2_H_HUM.OutOfnetwork oNet = new ProductBusinessEntityResponseV2_H_HUM.OutOfnetwork();
            ProductBusinessEntityResponseV2_H_HUM.DeductibleList delist = new ProductBusinessEntityResponseV2_H_HUM.DeductibleList();
            ProductBusinessEntityResponseV2_H_HUM.Coverage cov = new ProductBusinessEntityResponseV2_H_HUM.Coverage();
            ProductBusinessEntityResponseV2_H_HUM.BenefitAmountList balst = new ProductBusinessEntityResponseV2_H_HUM.BenefitAmountList();
            ProductBusinessEntityResponseV2_H_HUM.BenefitAmount benAmt = new ProductBusinessEntityResponseV2_H_HUM.BenefitAmount();
            ProductBusinessEntityResponseV2_H_HUM.UnitDetail unit = new ProductBusinessEntityResponseV2_H_HUM.UnitDetail();
            ProductBusinessEntityResponseV2_H_HUM.QuantityPeriod quanPer = new ProductBusinessEntityResponseV2_H_HUM.QuantityPeriod();
            ProductBusinessEntityResponseV2_H_HUM.OutOfPocket outPc = new ProductBusinessEntityResponseV2_H_HUM.OutOfPocket();
            ProductBusinessEntityResponseV2_H_HUM.OutOfPocketList oplst = new ProductBusinessEntityResponseV2_H_HUM.OutOfPocketList();
            ProductBusinessEntityResponseV2_H_HUM.MaxOutOfPocketList moplst = new ProductBusinessEntityResponseV2_H_HUM.MaxOutOfPocketList();
            ProductBusinessEntityResponseV2_H_HUM.MaxOutOfPocket maxPoc = new ProductBusinessEntityResponseV2_H_HUM.MaxOutOfPocket();
            ProductBusinessEntityResponseV2_H_HUM.CoInsurance coIns = new ProductBusinessEntityResponseV2_H_HUM.CoInsurance();
            ProductBusinessEntityResponseV2_H_HUM.CoInsuranceList cilst = new ProductBusinessEntityResponseV2_H_HUM.CoInsuranceList();
            ProductBusinessEntityResponseV2_H_HUM.SuperMoop supMoop = new ProductBusinessEntityResponseV2_H_HUM.SuperMoop();
            ProductBusinessEntityResponseV2_H_HUM.SuperMoopList smlst = new ProductBusinessEntityResponseV2_H_HUM.SuperMoopList();
            ProductBusinessEntityResponseV2_H_HUM.PlanCommentList planCommList = new ProductBusinessEntityResponseV2_H_HUM.PlanCommentList();
            ProductBusinessEntityResponseV2_H_HUM.PlanComment planComm = new ProductBusinessEntityResponseV2_H_HUM.PlanComment();
            ProductBusinessEntityResponseV2_H_HUM.BenefitIndicator benInd = new ProductBusinessEntityResponseV2_H_HUM.BenefitIndicator();
            ProductBusinessEntityResponseV2_H_HUM.Rider rid = new ProductBusinessEntityResponseV2_H_HUM.Rider();
            ProductBusinessEntityResponseV2_H_HUM.BenefitCodeList benlist = new ProductBusinessEntityResponseV2_H_HUM.BenefitCodeList();
            ProductBusinessEntityResponseV2_H_HUM.BenefitCodes benCodes = new ProductBusinessEntityResponseV2_H_HUM.BenefitCodes();
            ProductBusinessEntityResponseV2_H_HUM.BenefitCodeLimit benLimit = new ProductBusinessEntityResponseV2_H_HUM.BenefitCodeLimit();
            ProductBusinessEntityResponseV2_H_HUM.BenefitCode benCode = new ProductBusinessEntityResponseV2_H_HUM.BenefitCode();
            ProductBusinessEntityResponseV2_H_HUM.RxDetail rxDet = new ProductBusinessEntityResponseV2_H_HUM.RxDetail();
            ProductBusinessEntityResponseV2_H_HUM.RxServiceList rxlst = new ProductBusinessEntityResponseV2_H_HUM.RxServiceList();
            ProductBusinessEntityResponseV2_H_HUM.RxCoverage rxCov = new ProductBusinessEntityResponseV2_H_HUM.RxCoverage();
            ProductBusinessEntityResponseV2_H_HUM.RxBenefitDetail rxBenDet = new ProductBusinessEntityResponseV2_H_HUM.RxBenefitDetail();
            ProductBusinessEntityResponseV2_H_HUM.Service ser = new  ProductBusinessEntityResponseV2_H_HUM.Service();
            ProductBusinessEntityResponseV2_H_HUM.Benefit bene = new ProductBusinessEntityResponseV2_H_HUM.Benefit();
            ProductBusinessEntityResponseV2_H_HUM.BenefitDetail bdet = new ProductBusinessEntityResponseV2_H_HUM.BenefitDetail();
            ProductBusinessEntityResponseV2_H_HUM.ServiceCategory servCat = new ProductBusinessEntityResponseV2_H_HUM.ServiceCategory();
            ProductBusinessEntityResponseV2_H_HUM.PlaceOfService pos = new ProductBusinessEntityResponseV2_H_HUM.PlaceOfService();
            ProductBusinessEntityResponseV2_H_HUM.PlaceOfServiceCodeList podId = new ProductBusinessEntityResponseV2_H_HUM.PlaceOfServiceCodeList();
            ProductBusinessEntityResponseV2_H_HUM.BenefitDetailList bendetlst = new ProductBusinessEntityResponseV2_H_HUM.BenefitDetailList();
            ProductBusinessEntityResponseV2_H_HUM.BenefitList benlst = new ProductBusinessEntityResponseV2_H_HUM.BenefitList();
            ProductBusinessEntityResponseV2_H_HUM.RxBenefitDetailList Rxbendetlst = new ProductBusinessEntityResponseV2_H_HUM.RxBenefitDetailList();
            ProductBusinessEntityResponseV2_H_HUM.LimitationList limlst = new ProductBusinessEntityResponseV2_H_HUM.LimitationList();
            ProductBusinessEntityResponseV2_H_HUM.CopayOfficeVisit cpoffvst = new ProductBusinessEntityResponseV2_H_HUM.CopayOfficeVisit();            
            ProductBusinessEntityResponseV2_H_HUM.HipaaServiceType hipa = new ProductBusinessEntityResponseV2_H_HUM.HipaaServiceType();
            ProductBusinessEntityResponseV2_H_HUM.SupplementalRider supRider = new ProductBusinessEntityResponseV2_H_HUM.SupplementalRider();
            ProductBusinessEntityResponseV2_H_HUM.ParCode parCode = new ProductBusinessEntityResponseV2_H_HUM.ParCode();
            system.debug('****'+oResponse);
            system.assert(oResponse != null);
            system.assertEquals(oResponse.GetProductResponse,null);
          Test.StopTest(); 
        }
    }  
}