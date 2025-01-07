/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2SearchResponse_H_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : response helper for pharmacy service
Test Class       : PharmacyServiceV2SearchResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
* Shruthi Karanth			27819			  11/16/2015					Updated the response structure as per ICD . Defect 206691.0001 
***************************************************************************************************/
public with sharing class PharmacyServiceV2SearchResponse_H_HUM 
{
	public PharmacySearchDrugResponse_H_HUM SearchDrugResponse;
	public class PharmacySearchDrugResponse_H_HUM 
	{
		public list<PharmacySearchV2DrugSearch_H_HUM> Drugs;
		public String ResponseCode = '';	
	}
	
	public class PharmacySearchV2DrugSearch_H_HUM 
	{
		public String AvailableAtRightSource = '';
		public String BrandGeneric = '';
		public String CdlData = '';	
		public String ClinicalInformation = '';	
		public String CoverageInformation = '';	
		public String DisplayAttributes = '';	
		public String DrugFormDisplayText = '';	
		public String DrugName = '';	
		public String DrugStrength = '';	
		public String FormularyId = '';	
		public String GenericName = '';	
		public String LabelName = '';	
		public String MedId = '';
		public String MiscAttributes = '';
		public String Ndc = '';
		public String OtcIndicator = '';	
		public String PriceAttributes = '';	
		public String RequestedId = '';	
	}
}