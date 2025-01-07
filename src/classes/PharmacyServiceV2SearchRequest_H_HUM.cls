/**************************************************************************** 
Apex Class Name  : PharmacyServiceV2SearchRequest_H_HUM 
Version          : 1.0  
Created Date     : October 29, 2015
Function         : request helper for pharmacy service
Test Class       : PharmacyServiceV2SearchRequest_T_HUM
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Gargey Sharma		        27449             10/29/2015                    Original Version
***************************************************************************************************/
public with sharing class PharmacyServiceV2SearchRequest_H_HUM 
{
	public PharmacyV2DrugSearchRequest_H_HUM DrugSearchRequest;
	public class PharmacyV2DrugSearchRequest_H_HUM 
	{
		public String Requestor = '';	
		public String SearchKey = '';	
		public String SearchValue = '';
	}
}