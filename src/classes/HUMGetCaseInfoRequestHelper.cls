/**************************************************************************** 
Apex Class Name  : HUMGetCaseInfoRequestHelper
Version          : 1.0  
Created Date     : May 24 2014 
Function         : This class is used as a base class to handle the return request.
                    without sharing as service is exposed to external systems
Test Class       : HUMGetCaseInfoRequestHelperTest
****************************************************************************
Modification Log:

* Developer Name               Code Review                          Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi                17353                                 05/24/2014                    Original Version
* Srikant Joshi                17369                                 08/18/2014                    Made changes according to Humana standards
* Shruthi Karanth              18313                                 09/29/2014                    Updated test class
***************************************************************************************************/
global with sharing class HUMGetCaseInfoRequestHelper
{ 
    
    //Declare variables before usage
    Webservice String InteractionId = '';
    Webservice String CaseId = '';
    Webservice List<HUMEntityIdentifierHelper> InquireForCriteria = null;
    Webservice List<HUMEntityIdentifierHelper> InquireAboutCriteria = null;
    Webservice String StartDate = '';    
    Webservice String EndDate = '';
    Webservice String InqForAboutMatchRule = '';
    Webservice String UserId = '';
    Webservice Boolean AttachmentFlag = null;   
    Webservice Boolean AttachmentLogFlag = null;
    Webservice Boolean InteractionFlag = null;
    Webservice Boolean CaseCommentFlag = null;
    Webservice Boolean TaskFlag = null;
 
   
}