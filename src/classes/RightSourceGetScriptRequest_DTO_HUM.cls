/******************************************************************************************************************
Apex class Name    : RightSourceGetScriptRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for RightSource GetScript
Created On         : December 9 2016
Test Class         : RightSource_T_HUM

Modification Log:
*   Version        Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0          Vishal Verma                                   12/09/2016             Original Version -> REQ - 291073
*    1.1          Rajesh Keswani            	93585		     05/12/2017             Offline mode including IncludeStaledata -> REQ - 313437
*    1.2          Rajesh Keswani            	112036        	 07/14/2017             adding network Id in Request
* Pinky Vijur                                       06/22/2022                   US - 2892911:T1PRJ0100059 - MF 3- TECH - SF - Update URL for HPIE Services (GdetScript)
------------------------------------------------------------------------------------------------------------------*/
public with sharing class RightSourceGetScriptRequest_DTO_HUM 
{
  public ScriptsRequest GetScriptsRequest;
  
  public class ScriptsRequest 
  {
    public String EnterprisePersonID = '';
    public string organizationPatientId = '';
    public string organization = '';
    public String ScriptKey = '';  
    public String ScriptEndDate = '';
    public String ScriptStartDate = '';
    public String IncludeStaleData = '';
    public String CustomerServiceId = '';
  }
}