/***************************************************************************************************************************************************************************
Apex Class Name  : HUMGetCaseServiceResponseHelper
Version          : 1.0
Created Date     : December 22 2014
Function         : This class is used as response for get case  HUMCaseService.
Test Class       : HUMGetCaseServiceResponseHelperTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name        Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma            20294                   12/22/2014                   Original Version
 ****************************************************************************************************************************************************************************/

global with sharing class HUMGetCaseServiceResponseHelper
{
    webservice Integer caseCount = 0;
    webservice List<HUMGetCaseInfoResponseHelper> caseInfoResponse = null;
}