/***************************************************************************************************************************************************************************
Apex Class Name  : HUMGetCaseServiceRequestHelper
Version          : 1.0
Created Date     : December 22 2014
Function         : This class is used as request for get case  HUMCaseService.
Test Class       : HUMGetCaseServiceRequestHelperTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name        Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma           20294                    12/22/2014                   Original Version
 ****************************************************************************************************************************************************************************/

global with sharing class HUMGetCaseServiceRequestHelper
{
    webservice HUMGetCaseInfoRequestHelper caseInfoRequest = null;
    webservice List<HUMEntityIdentifierHelper> IntAboutCaseAvailabilityCheck = null;
    webservice boolean getCaseInfo = false;
    
}