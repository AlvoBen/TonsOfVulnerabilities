/**************************************************************************** 
Apex Class Name  : PBEResponse_DTO_HUM   
Version          : 1.0  
Created Date     : April 12, 2016
Function         : Wrapper class for PBE Service 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh keswani                             04/12/2016                 Original Version
* Aviansh Choubey         VS-27193           04/26/2016                 Added new property BenefitIndicatorList req 237280
* Avinash Kumar Choubey                      06/01/2016                 Added new property CoInsurance, SuperMoop, CopayOfficeVisit from PBE response for REQ 269949
* Avinash Kumar Choubey   29936              06/11/2016                 Added new property RxDetail from PBE response for Defect# 229256, 229257
* Avinash Choubey           109914           07/24/2017                 REQ - 300751 Making Par Code” drop down dynamic(for CAS Members) and Sorted
* Rajesh keswani          129626             09/18/2017                 REQ - 329669; changing plan option mapping to defination description.
* Vijaya Koriprolu        138809             10/07/2017                 REQ-304935 : VOB Prefill the Reimbursement tool with Provider Contract Number
***************************************************************************************************/
public class PBEResponse_DTO_HUM {
    
    public string IsHDHP ='';
    public ProductBusinessEntityResponse_H_HUM.Deductible Deductible = NULL;
    public ProductBusinessEntityResponse_H_HUM.MaxOutOfPocket MaxOutOfPocket = NULL;
    public ProductBusinessEntityResponse_H_HUM.OutOfPocket OutOfPocket = NULL;
    public ProductBusinessEntityResponse_H_HUM.RiderList RiderList = NULL;
    public ProductBusinessEntityResponse_H_HUM.PackageInfo PackageInfo = NULL;
    public ProductBusinessEntityResponse_H_HUM.PlaceOfServiceList PlaceOfServiceList = NULL;
    public ProductBusinessEntityResponse_H_HUM.ServiceCategoryList ServiceCategoryList = NULL;
    public ProductBusinessEntityResponse_H_HUM.ServiceTypeList ServiceTypeList = NULL;
    public ProductBusinessEntityResponse_H_HUM.BenefitIndicatorList BenefitIndicatorList = NULL;
    public ProductBusinessEntityResponse_H_HUM.CoInsurance CoInsurance = NULL;
    public ProductBusinessEntityResponse_H_HUM.SuperMoop SuperMoop = NULL;
    public ProductBusinessEntityResponse_H_HUM.CopayOfficeVisit CopayOfficeVisit = NULL;
    public ProductBusinessEntityResponse_H_HUM.RxDetail RxDetail = NULL;
    public ProductBusinessEntityResponse_H_HUM.Limitation Limitation = NULL;
    public ProductBusinessEntityResponse_H_HUM.ParcodeList ParcodeList = NULL;
    public String PlatformCode = '';
    public string planOption = '';
    public String AlternateReimburseId = '';
}