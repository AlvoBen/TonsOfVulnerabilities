/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCreateCaseResponseHelper
Version          : 1.0
Created Date     : May 23 2016
Function         : This class is used as respone for create case  CreateCase_D_HUM .
Test Class       : HUMCreateCaseResponseHelperTest
 ****************************************************************************************************************************************************************************

Modification Log:

 * Developer Name        Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Adil Mohammed          103536                    24/05/2016                           Original Version
 ****************************************************************************************************************************************************************************/

global with sharing class HUMCreateCaseResponseHelper
{
  webservice string sRecordId = '';
  Webservice Boolean Success = null;
  Webservice String ErrorCode = '';
  Webservice String ErrorMessage = '';    
}