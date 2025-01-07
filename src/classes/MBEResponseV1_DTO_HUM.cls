/**************************************************************************** 
Apex Class Name :  MBEResponseV1_DTO_HUM   
Version: 1.0  
Created Date     : 
Function         : Wrapper class for MBE Service 
****************************************************************************
Modification Log: 
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------

* Sumit Pharande                             01/09/2018                 Upgraded to MBE Plus
* Suraj Patil                                05/04/2020                 REQ - 737881 - MF 4 - Add Dental Paid Thru Date for HPS Members
* Sai Kumar Boga                             11/10/2020                 REQ - 1567028  CALL_MF03_FR02: KY MMC 2021(Added per CR17)_CRM service must create a NATIVE AMERICAN indicator
* Ashish Kumar								 05/03/2021					Made 'Code' parameter as @AuraEnabled for lightning functionality
***************************************************************************************************/

Public Class MBEResponseV1_DTO_HUM
{ 
    Public String sOriginalEffectiveDate ='';
    Public String memberGenKey = '';
    Public MBEPlusResponse_DTO_HUM.EnrollmentInfo EnrollmentInfo = NULL;
    Public MBEPlusResponse_DTO_HUM.WaitingPeriodList WaitingPeriodList = NULL;
    Public MBEPlusResponse_DTO_HUM.IndicatorList IndicatorList = NULL;
    Public MBEPlusResponse_DTO_HUM.IndicatorList PolicyIndicatorList = NULL;
   
    //Benefit Summary Variables
    Public MBEPlusResponse_DTO_HUM.OutOfAreaList OutOfAreaList = NULL;
    Public MBEPlusResponse_DTO_HUM.Network Network= NULL;
    Public MBEPlusResponse_DTO_HUM.SoldProductDetail  SoldProductDetail  = NULL;
    Public String sCoverageType = '';
    public string paidThruDate = '';

    //Cost share level variable
    Public MBEPlusResponse_DTO_HUM.CostShareReductionList   CostShareReductionList   = NULL;
    //Race Code Values
    @AuraEnabled Public String Code{Get;Set;}
    Public String Description{Get;Set;}

}