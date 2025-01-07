/***************************************************************************************************************************************************************************
Apex Class Name  : HUMUpdateCaseServiceRequestHelper
Version          : 1.0
Created Date     : December 22 2014
Function         : This class is used as request for update case  HUMCaseService.
Test Class       : HUMUpdateCaseServiceRequestHelperTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name        Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Vishal Verma           20294                    12/22/2014                   Original Version
 ****************************************************************************************************************************************************************************/

global with sharing class HUMUpdateCaseServiceRequestHelper
{
    webservice HUMCaseUpdateLogAttachmentHelper CaseUpdateRequest = null;
}