/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityResponse_T_HUM 
Version          : 1.0  
Created Date     : February 05, 2016
Function         : test class for ProductBusinessEntityResponse_H_HUM  
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma             29829             02/05/2016                    Original Version
* Gargey Sharma             30736             03/31/2016                    Updated as per changes in REQ - 263787
* Gargey Sharma             30736             04/05/2016                    Updated as per changes in ICD
* Adil Mohammed             31466             05/18/2016                    Deleted placeOfServiceCode
***************************************************************************************************/
@isTest
private Class ProductBusinessEntityResponse_T_HUM 
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
     * Method Name : testProductBusinessEntityResponse_H_HUM
     * Description : Test method for ProductBusinessEntityResponse_H_HUM 
     * Return type : NA
     * Paramater   : NA
     */
    static testMethod void testProductBusinessEntityResponse_H_HUM()
    {            
        User oUser = [select Id,Name from User where name = 'test User1' limit 1];               
        system.runAs(oUser)
        {
          Test.StartTest();
            ProductBusinessEntityResponse_H_HUM oResponse = new ProductBusinessEntityResponse_H_HUM();
            ProductBusinessEntityResponse_H_HUM.GetProductResponse getProRes = new ProductBusinessEntityResponse_H_HUM.GetProductResponse(); 
            ProductBusinessEntityResponse_H_HUM.ProductList proList = new ProductBusinessEntityResponse_H_HUM.ProductList(); 
            ProductBusinessEntityResponse_H_HUM.Product pro = new ProductBusinessEntityResponse_H_HUM.Product();
            ProductBusinessEntityResponse_H_HUM.Categorization cate = new ProductBusinessEntityResponse_H_HUM.Categorization();
            ProductBusinessEntityResponse_H_HUM.Category cat = new ProductBusinessEntityResponse_H_HUM.Category();
            ProductBusinessEntityResponse_H_HUM.Line line = new ProductBusinessEntityResponse_H_HUM.Line();
            ProductBusinessEntityResponse_H_HUM.Family fam = new ProductBusinessEntityResponse_H_HUM.Family();
            ProductBusinessEntityResponse_H_HUM.Metadata mData = new ProductBusinessEntityResponse_H_HUM.Metadata();
            ProductBusinessEntityResponse_H_HUM.LocationList loclst = new ProductBusinessEntityResponse_H_HUM.LocationList();
            ProductBusinessEntityResponse_H_HUM.Location loc = new ProductBusinessEntityResponse_H_HUM.Location();
            ProductBusinessEntityResponse_H_HUM.ZipList ziplst = new ProductBusinessEntityResponse_H_HUM.ZipList();
            ProductBusinessEntityResponse_H_HUM.Zip zi = new ProductBusinessEntityResponse_H_HUM.Zip();
            ProductBusinessEntityResponse_H_HUM.Country cntry = new ProductBusinessEntityResponse_H_HUM.Country();
            ProductBusinessEntityResponse_H_HUM.State sta = new ProductBusinessEntityResponse_H_HUM.State();
            ProductBusinessEntityResponse_H_HUM.County cnt = new ProductBusinessEntityResponse_H_HUM.County();
            ProductBusinessEntityResponse_H_HUM.QuoteDates quoDates = new ProductBusinessEntityResponse_H_HUM.QuoteDates();
            ProductBusinessEntityResponse_H_HUM.Definition def = new ProductBusinessEntityResponse_H_HUM.Definition();
            ProductBusinessEntityResponse_H_HUM.IdentifierList idenlst = new ProductBusinessEntityResponse_H_HUM.IdentifierList(); 
            ProductBusinessEntityResponse_H_HUM.ProductIdentifier proIden = new ProductBusinessEntityResponse_H_HUM.ProductIdentifier();
            ProductBusinessEntityResponse_H_HUM.AlternateNameList altlst = new ProductBusinessEntityResponse_H_HUM.AlternateNameList();
            ProductBusinessEntityResponse_H_HUM.AlternateName altName = new ProductBusinessEntityResponse_H_HUM.AlternateName();
            ProductBusinessEntityResponse_H_HUM.Detail det = new ProductBusinessEntityResponse_H_HUM.Detail();
            /*For now, renaming InsuranceProductDetail to Detail, as service is still in development phase and is returning details only for InsuranceProductDetail.
            ProductBusinessEntityResponse_H_HUM.NIProductDetail niProDet = new ProductBusinessEntityResponse_H_HUM.NIProductDetail();
            ProductBusinessEntityResponse_H_HUM.LegalEntity legEnt = new ProductBusinessEntityResponse_H_HUM.LegalEntity();
            ProductBusinessEntityResponse_H_HUM.InsuranceProductDetail insPro = new ProductBusinessEntityResponse_H_HUM.InsuranceProductDetail();*/
            ProductBusinessEntityResponse_H_HUM.ServiceList serlst = new ProductBusinessEntityResponse_H_HUM.ServiceList();
            ProductBusinessEntityResponse_H_HUM.ServiceCategoryList scatlst = new ProductBusinessEntityResponse_H_HUM.ServiceCategoryList();
            ProductBusinessEntityResponse_H_HUM.PlaceOfServiceList pslst = new ProductBusinessEntityResponse_H_HUM.PlaceOfServiceList();
            ProductBusinessEntityResponse_H_HUM.BenefitCodesList bencodelst = new ProductBusinessEntityResponse_H_HUM.BenefitCodesList();
            ProductBusinessEntityResponse_H_HUM.ServiceAreaSubNetworkList subnetlst = new ProductBusinessEntityResponse_H_HUM.ServiceAreaSubNetworkList();
            ProductBusinessEntityResponse_H_HUM.ServiceAreaSubNetwork subnet = new ProductBusinessEntityResponse_H_HUM.ServiceAreaSubNetwork();
            ProductBusinessEntityResponse_H_HUM.RiderList ridlst = new ProductBusinessEntityResponse_H_HUM.RiderList();
            ProductBusinessEntityResponse_H_HUM.PackageInfo pacInfo = new ProductBusinessEntityResponse_H_HUM.PackageInfo();
            ProductBusinessEntityResponse_H_HUM.EffectiveDates effDate = new ProductBusinessEntityResponse_H_HUM.EffectiveDates();
            ProductBusinessEntityResponse_H_HUM.GeneralBenefits genBen = new ProductBusinessEntityResponse_H_HUM.GeneralBenefits();
            ProductBusinessEntityResponse_H_HUM.BenefitIndicatorList biLst = new ProductBusinessEntityResponse_H_HUM.BenefitIndicatorList();
            ProductBusinessEntityResponse_H_HUM.Deductible deduc = new ProductBusinessEntityResponse_H_HUM.Deductible();
            ProductBusinessEntityResponse_H_HUM.Accumulates acc = new ProductBusinessEntityResponse_H_HUM.Accumulates();
            ProductBusinessEntityResponse_H_HUM.InNetwork inNet = new ProductBusinessEntityResponse_H_HUM.InNetwork();
            ProductBusinessEntityResponse_H_HUM.OutOfnetwork oNet = new ProductBusinessEntityResponse_H_HUM.OutOfnetwork();
            ProductBusinessEntityResponse_H_HUM.DeductibleList delist = new ProductBusinessEntityResponse_H_HUM.DeductibleList();
            ProductBusinessEntityResponse_H_HUM.Coverage cov = new ProductBusinessEntityResponse_H_HUM.Coverage();
            ProductBusinessEntityResponse_H_HUM.BenefitAmountList balst = new ProductBusinessEntityResponse_H_HUM.BenefitAmountList();
            ProductBusinessEntityResponse_H_HUM.BenefitAmount benAmt = new ProductBusinessEntityResponse_H_HUM.BenefitAmount();
            ProductBusinessEntityResponse_H_HUM.UnitDetail unit = new ProductBusinessEntityResponse_H_HUM.UnitDetail();
            ProductBusinessEntityResponse_H_HUM.QuantityPeriod quanPer = new ProductBusinessEntityResponse_H_HUM.QuantityPeriod();
            ProductBusinessEntityResponse_H_HUM.OutOfPocket outPc = new ProductBusinessEntityResponse_H_HUM.OutOfPocket();
            ProductBusinessEntityResponse_H_HUM.OutOfPocketList oplst = new ProductBusinessEntityResponse_H_HUM.OutOfPocketList();
            ProductBusinessEntityResponse_H_HUM.MaxOutOfPocketList moplst = new ProductBusinessEntityResponse_H_HUM.MaxOutOfPocketList();
            ProductBusinessEntityResponse_H_HUM.MaxOutOfPocket maxPoc = new ProductBusinessEntityResponse_H_HUM.MaxOutOfPocket();
            ProductBusinessEntityResponse_H_HUM.CoInsurance coIns = new ProductBusinessEntityResponse_H_HUM.CoInsurance();
            ProductBusinessEntityResponse_H_HUM.CoInsuranceList cilst = new ProductBusinessEntityResponse_H_HUM.CoInsuranceList();
            ProductBusinessEntityResponse_H_HUM.SuperMoop supMoop = new ProductBusinessEntityResponse_H_HUM.SuperMoop();
            ProductBusinessEntityResponse_H_HUM.SuperMoopList smlst = new ProductBusinessEntityResponse_H_HUM.SuperMoopList();
            ProductBusinessEntityResponse_H_HUM.PlanCommentList planCommList = new ProductBusinessEntityResponse_H_HUM.PlanCommentList();
            ProductBusinessEntityResponse_H_HUM.PlanComment planComm = new ProductBusinessEntityResponse_H_HUM.PlanComment();
            ProductBusinessEntityResponse_H_HUM.BenefitIndicator benInd = new ProductBusinessEntityResponse_H_HUM.BenefitIndicator();
            ProductBusinessEntityResponse_H_HUM.Rider rid = new ProductBusinessEntityResponse_H_HUM.Rider();
            ProductBusinessEntityResponse_H_HUM.ServiceAreaList salst = new ProductBusinessEntityResponse_H_HUM.ServiceAreaList();
            ProductBusinessEntityResponse_H_HUM.ServiceArea serArea = new ProductBusinessEntityResponse_H_HUM.ServiceArea();
            ProductBusinessEntityResponse_H_HUM.BenefitCodeList benlist = new ProductBusinessEntityResponse_H_HUM.BenefitCodeList();
            ProductBusinessEntityResponse_H_HUM.BenefitCodes benCodes = new ProductBusinessEntityResponse_H_HUM.BenefitCodes();
            ProductBusinessEntityResponse_H_HUM.BenefitCodeLimit benLimit = new ProductBusinessEntityResponse_H_HUM.BenefitCodeLimit();
            ProductBusinessEntityResponse_H_HUM.BenefitCode benCode = new ProductBusinessEntityResponse_H_HUM.BenefitCode();
            ProductBusinessEntityResponse_H_HUM.RxDetail rxDet = new ProductBusinessEntityResponse_H_HUM.RxDetail();
            ProductBusinessEntityResponse_H_HUM.RxServiceList rxlst = new ProductBusinessEntityResponse_H_HUM.RxServiceList();
            ProductBusinessEntityResponse_H_HUM.RxCoverage rxCov = new ProductBusinessEntityResponse_H_HUM.RxCoverage();
            ProductBusinessEntityResponse_H_HUM.RxBenefitDetail rxBenDet = new ProductBusinessEntityResponse_H_HUM.RxBenefitDetail();
            ProductBusinessEntityResponse_H_HUM.Service ser = new  ProductBusinessEntityResponse_H_HUM.Service();
            ProductBusinessEntityResponse_H_HUM.Benefit bene = new ProductBusinessEntityResponse_H_HUM.Benefit();
            ProductBusinessEntityResponse_H_HUM.BenefitDetail bdet = new ProductBusinessEntityResponse_H_HUM.BenefitDetail();
            ProductBusinessEntityResponse_H_HUM.ServiceCategory servCat = new ProductBusinessEntityResponse_H_HUM.ServiceCategory();
            ProductBusinessEntityResponse_H_HUM.PlaceOfService pos = new ProductBusinessEntityResponse_H_HUM.PlaceOfService();
            ProductBusinessEntityResponse_H_HUM.PlaceOfServiceCodeList podId = new ProductBusinessEntityResponse_H_HUM.PlaceOfServiceCodeList();
            ProductBusinessEntityResponse_H_HUM.BenefitDetailList bendetlst = new ProductBusinessEntityResponse_H_HUM.BenefitDetailList();
            ProductBusinessEntityResponse_H_HUM.BenefitList benlst = new ProductBusinessEntityResponse_H_HUM.BenefitList();
            ProductBusinessEntityResponse_H_HUM.RxBenefitDetailList Rxbendetlst = new ProductBusinessEntityResponse_H_HUM.RxBenefitDetailList();
            ProductBusinessEntityResponse_H_HUM.LimitationList limlst = new ProductBusinessEntityResponse_H_HUM.LimitationList();
            ProductBusinessEntityResponse_H_HUM.CopayOfficeVisit cpoffvst = new ProductBusinessEntityResponse_H_HUM.CopayOfficeVisit();
            system.assert(oResponse != null);
          Test.StopTest(); 
        }
    }  
}