/******************************************************************************************************************************
Apex Class Name :  CaseProcess_LC_DTO_HUM
Version: 1.0 
Created Date     :  02/01/2023
Function         : Data Transfer Object class for CaseProcess

Modification Log : 
 *    Developer          Code Review         Date                 Description
 * ------------------------------------------------------------------------------------------------------------------------------                
 *    Prasuna Pattabhi                      02/01/2023           Original version.
 *********************************************************************************************************************************/
public with sharing class CaseProcess_LC_DTO_HUM {
    
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
	public boolean isLast = false;
}