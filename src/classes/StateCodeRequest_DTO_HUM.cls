/*
Class Name       : StateCodeRequest_DTO_HUM
Created Date     : 08/31/2021
Function         : DTO Request EnterpriseAddress Service for GetCountyDetails call
Modification Log :
*Version  Developer          Code Review         Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------
*1.0	  Ankit Avula						     08/31/2021			  US2365934: T1PRJ0002855/PR00094919 - Account Management - Unknown Records - Prefill State Dropdown when user Inputs Zip, Create Unknown Member, Provider, and Group
-------------------------------------------------------------------------------------------------------------------------------*/

public class StateCodeRequest_DTO_HUM {

	public GetCountyDetailsRequest GetCountyDetailsRequest;

	public class GetCountyDetailsRequest {
		public String ZipCode;
	}

}