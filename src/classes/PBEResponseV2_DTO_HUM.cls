/**************************************************************************** 
Apex Class Name  : ProductBusinessEntityRequestV2_H_HUM
Version          : 1.0  
Created Date     : April 18,2018
Function         : request helper for Product Business Entity Service 1.3
Test Class       : BenefitsV2_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rajesh Keswani            220187         18/04/2018                   Original Version
* Lakshmi Madduri 							08/08/2017					Added cause code List
* Deepankar Binjola         48282           09/19/2019                 REQ - 402985 Service Catalog IMS form to prepopulate with the plan information
***************************************************************************************************/
public class PBEResponseV2_DTO_HUM {
    
    public string IsHDHP ='';
    public ProductBusinessEntityResponseV2_H_HUM.Deductible Deductible = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.MaxOutOfPocket MaxOutOfPocket = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.OutOfPocket OutOfPocket = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.RiderList RiderList = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.PackageInfo PackageInfo = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.PlaceOfServiceList PlaceOfServiceList = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.ServiceCategoryList ServiceCategoryList = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.ServiceTypeList ServiceTypeList = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.BenefitIndicatorList BenefitIndicatorList = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.CoInsurance CoInsurance = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.SuperMoop SuperMoop = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.CopayOfficeVisit CopayOfficeVisit = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.RxDetail RxDetail = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.Limitation Limitation = NULL;
    public ProductBusinessEntityResponseV2_H_HUM.ParcodeList ParcodeList = NULL;
	public ProductBusinessEntityResponseV2_H_HUM.CauseCodeList CauseCodeList = NULL;
    public String PlatformCode = '';
    public string planOption = '';
    public String AlternateReimburseId = '';
	public string pbePlanId = '';
}