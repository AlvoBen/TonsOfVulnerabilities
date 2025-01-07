/******************************************************************************************************************************
Apex Class Name  : CaseProcess_DTO_HUM
Version          : 1.0 
Created Date     : NOV 17 2017
Function         : Data Transfer Object class for CaseProcess

Modification Log :
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Rakesh Patni       149201              11/17/2017           Original version.
 * Moshitha Gunasekaran  348700              03/25/2019           REQ - 380742 - Ability to view Transaction Reply Report (TRR) Process data for a specific member on a Medicare case
 *********************************************************************************************************************************/
public with sharing class CaseProcess_DTO_HUM 
{
	public String ProcessType = '';
	public String CreatedDate = '';
	public String CreatedBy  = '';
	public String CreatedByQueue  = '';
	public String LastModifiedDate = '';
	public String LastModifiedBy = '';
	public String LastModifiedByQueue  = '';
	public string ProcessNumber = '';
	public string ReplyCode = '';
	public string EffectiveDate = '';
	public string ProcessedDate = '';
}