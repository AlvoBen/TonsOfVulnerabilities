/***************************************************************************************
Apex Class Name  : HUMMemberEligibiltyResponseHelper
Version          : 1.0
Created Date     : August 22 2014
Function         : Webservice class is used to build response for member eligibilty service
Test Class       : HUMMemberEligibiltyResponseHelperTest
 ****************************************************************************************
Modification Log:
 * Developer Name            Review Number                 Date                       Description
 *------------------------------------------------------------------------------------------------------------
 *Vishal Verma                 17677                   08/22/2014                   Original Version
 *Ajay Thimmasamudram          18335                   10/03/2014                   Changed the Naming Convertion of Class Name
 ***************************************************************************************************/
global with sharing class HUMMemberEligibiltyResponseHelper
{
    webservice String sErrorCode = '';
    webservice String sErrorMessage = '';
    webservice boolean bMemberEligibleForChat = false;
}