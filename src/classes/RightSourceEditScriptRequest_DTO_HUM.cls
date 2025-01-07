/******************************************************************************************************************
Apex class Name    : RightSourceEditScriptRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource EditScriptRequest
Created On         : March 15 2017
Test Class         : RightSource_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0         Sumit Pharande                                03/15/2017             Original Version -> REQ - 303405
*    1.1         Rajesh Keswani            		112036         07/14/2017             Adding network Id in Request
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceEditScriptRequest_DTO_HUM 
{
  public EditScriptRequest EditScriptRequest;
  
  public class EditScriptRequest
  {
    public String ScriptKey ='';  
    public String AutoRefill = '';
    public string CustomerServiceId = '';
  }
}