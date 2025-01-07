/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2DosageRequest_H_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : request helper for GetDrugDosage method in pharmacy service
Test Class       : PharmacyServiceV2DosageRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
***************************************************************************************************/
public with sharing class PharmacyServiceV2DosageRequest_H_HUM 
{
	public PharmacyV2DrugDosageRequest_H_HUM DrugDosageRequest;
	public class PharmacyV2DrugDosageRequest_H_HUM 
	{
		public String Requestor = '';	
		public PharmacyServiceV2DrugId_H_HUM DrugId;
	}
	public class PharmacyServiceV2DrugId_H_HUM 
	{
		public String Key = '';	
		public String Value = '';
	}
}