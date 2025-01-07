/***********************************************************************************************************************
Apex Class Name  : DEAA_ZAPIServiceResponse_DTO_HUM
Version          : 1.0  
Created Date     : November 19, 2019
Function         : This is the DTO for the response of the zAPI service for DEAA template.
Test Class       : 
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Mohammed Noor                                                  11/19/2019                 Original Version 
***********************************************************************************************************************/
public with sharing class DEAA_ZAPIServiceResponse_DTO_HUM 
{
	Public SavePartDOptOutResponse SavePartDOptOutResponse;
    public class SavePartDOptOutResponse 
    {
        public String CaseNumber='';
        public String Type='';
        public String Code='';
        public String Description='';
    }    
}