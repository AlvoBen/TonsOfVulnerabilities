/**************************************************************************** 
Apex Class Name  : AVFTemplateResponse_DTO_HUM
Version          : 1.0  
Created Date     : August 01, 2016
Function         : This class is used to build Rest Service for AVF Template service
Test Class       : AVFTemplateResponse_DTO_T_HUM
****************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------
* Shruthi Karanth             	34224	                         08/01/2016                	  Original Version   
***************************************************************************************************/
public with sharing class AVFTemplateResponse_DTO_HUM 
{
    public TemplateResponse TemplateResponse;
    
    public class TemplateResponse
    {
        public ErrorList Errors = null;
    }
    
    public class ErrorList
    {
        public List<Error> Error = null;
    }
    
    public class Error
    {
        public String Code = '';
        public String Description = '';
    }     
}