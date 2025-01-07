/**************************************************************************** 
Apex Class Name  : GBEResponse_DTO_HUM   
Version          : 1.0  
Created Date     : April 7, 2016
Function         : Wrapper class for GBE Service 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Rajesh keswani          26247              04/07/2016                 Original Version
* Pradeepkumar Dani       26247              04/25/2016                 Added variables for Benefit summary section
* Vishal Verma			  26316				 04/25/2016					Added Mapping for Benefit Summary Second Pass
* Shreya Choodamani                          05/03/2018                 Added the Legal Entity and Group Id variable as part of REQ#: 361851
***************************************************************************************************/
global class GBEResponse_DTO_HUM 
{
	global String sGroupEnrollmentCount {get;set;}
	global String sEnrollmentSource {get;set;}
	global String sHourlyReq {get;set;}
	global String sOpenEnrolBegindate {get;set;}
	global String sOpenEnrolEnddate {get;set;}
	global String sMaximumDependentAge {get;set;}
	global String sMaximumStudentAge {get;set;}
	
	//Benefit Summary variables
	global String sBenefitPeriod {get;set;}
	global String sCertificate {get;set;}
	global String sBenefitPackageId {get;set;}
	global String sNetwork {get;set;}
	global String sMentalHealthVendorCode {get;set;}
	global String sLastRenewalDate {get;set;}	
	global String sBenefitNetwork{ get; set; }
	global String sMarketName{ get; set; }
	global String sSellingLedger{ get; set; }
	global String sLedgerDescription{ get; set; }
	global string sLegalEntity{get; set;}
	global string sGroupId{get; set;}
}