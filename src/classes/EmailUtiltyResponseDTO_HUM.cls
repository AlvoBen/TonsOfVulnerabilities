/******************************************************************************************************************
Apex class Name    : EmailUtiltyResponseDTO_HUM
Version            : 1.0
Function           : Response DTO for Email Utility
Created On         : December 9 2016
Test Class         : EmailUtility_T_HUM

Modification Log:
*   Version				Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0  				Vishal Verma			                     12/09/2016             Original Version -> REQ - 291072
------------------------------------------------------------------------------------------------------------------*/
public with sharing class EmailUtiltyResponseDTO_HUM 
{
	public EmailResponse SendEmailResponse;
	
	public class EmailResponse 
	{
		public String Type;
		public String Source;
		public String Description;
	}
}