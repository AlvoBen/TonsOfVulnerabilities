/******************************************************************************************************************
Apex class Name    : AspectRequest_DTO_HUM
Version            : 1.0
Function           : Wrapper for receiving request for Aspect service
Created On         : November 9 2016
Test Class         : Aspect_T_HUM

Modification Log:
*   Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*   Shruthi Karanth               48462            11/09/2016       Request Wrapper for Aspect service
* 	Akshay Pai                                     09/04/2020       REQ-1442568 TEST COVERAGE FIX
******************************************************************************************************************/

global with sharing class AspectRequest_DTO_HUM 
{
    webservice String StartDateTime = '';
    webservice String EndDateTime = '';
    webservice String OpServiceLevelThreshold = '';
}