/**
   @Author 		: Pradeepkumar Dani
   @name 		: CaseAttachments_C_HUM
   @CreateDate 	: 10/30/2017
   @Description : This class serves as DTO wrapper.
   @Version 	: 1.0
   @reference 	: CaseAttachments_C_HUM, CaseAttachments_T_HUM
   
Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pradeepkumar Dani          142531                 10/30/2017                   	Original version   
********************************************************************************************************************************/
public with sharing class CaseAttach_DTO_HUM 
{
	public string Id = '';
	public string fileName = '';
	public string createdDate = '';
	public string createdBy = '';
	public string createdByQueue = '';
}