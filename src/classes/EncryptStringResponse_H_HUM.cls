/*********************************************************************************************************************************************     
Apex Class Name  : EncryptStringResponse_H_HUM 
Created Date     : 11 Nov 2021
Function         : 
**Modification Log
* Developer Name          Code Review          Date                       Description
******************************************************************************************************************************************************
* Suraj Patil								   11/10/2021				  REQ - 2512130 T1PRJ0193862 - MF 01 - Ability to attach PFP email automatically to CRM case (Mbr Policy Page)
*****************************************************************************************************************************************************/
public with sharing class EncryptStringResponse_H_HUM 
{
	public EncryptStringArrayResponse EncryptStringArrayResponse;        
 
	 public class EncryptStringArrayResponse {
		public EncryptStringArrayResult EncryptStringArrayResult;
	}
	
	 public class EncryptStringArrayResult
	{
		public List<string> EncryptStringArrayValue;
	}
	
}