/**************************************************************************** 
Apex Class Name  : PolicyMemberDetail_DTO_HUM 
Version          : 1.0  
Created Date     : Aug 02, 2017
Function         : DTo For PBE service request
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
* Rajesh Keswani           118189               02/08/2017                  Original Version
***************************************************************************************************/

public with sharing class PolicyMemberDetail_DTO_HUM
{
	public string policyMemberID;
	public string policyID;
	public string status;
	public string endDate;
	public string soldProductKeyValue;
	public string groupMemberNumber;
	public string refDateFromURL;
	public string platformCode;
    public string ASOValue;
}