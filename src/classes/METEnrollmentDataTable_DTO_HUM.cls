/*****************************************************************************************************
Apex Class Name : METEnrollmentDataTable_DTO_HUM
Version   		: 1.0 
Created Date  	: 11/30/2017
Function  		: DTO structure to hold MET Tasks
Test Class  	: METEnrollmentDataTable_T_HUM

Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
* Rakesh Patni				153362			11/30/2017					  Original Version
* Rakesh Patni				158049			12/18/2017					  Updates as per REQ - 349584
*******************************************************************************************************/
public with sharing class METEnrollmentDataTable_DTO_HUM
{
	public string sTask = '';
	public string sAction = '';
	public string sSource = '';
	public string sStatus = '';
	public string sCreatedDate = '';
	public string sCreatedBy = '';
	public string sCreatedByQ = '';
	public string sLastModDate = '';
	public string sLastModifiedBy = '';
	public string sLastModByQ = '';
	public string sCancelledByDate = '';
	public string sCancelledBy = '';
	public string sCancelledByQ = '';
	public string sColSort = '';
	public METEnrollmentDataTable_DTO_HUM()
    {}
}