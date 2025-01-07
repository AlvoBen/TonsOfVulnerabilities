/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2DosageResponse_H_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : response helper for GetDrugDosage method in pharmacy service
Test Class       : PharmacyServiceV2DosageResponse_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
***************************************************************************************************/
public with sharing class PharmacyServiceV2DosageResponse_H_HUM 
{
	public PharmacyDrugDosageResponse_H_HUM DrugDosageResponse;
	public class PharmacyDrugDosageResponse_H_HUM 
	{
		public list<PharmacyServiceV2DosageDrugs_H_HUM> Drugs;
		public String ResponseCode = '';	
	}
	public class PharmacyServiceV2DosageDrugs_H_HUM 
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