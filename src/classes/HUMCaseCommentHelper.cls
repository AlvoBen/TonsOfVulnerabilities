/**************************************************************************** 
Apex Class Name  : HUMCaseCommentHelper
Version          : 1.0  
Created Date     : August 18 2014 
Function         : This class is used as a base class to handle the CaseComment Helper
Test Class       : HUMCaseCommentHelperTest
****************************************************************************
Modification Log:

* Developer Name            Review Number                        Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham               17369                             08/18/2014                   Original Version
* Vivek Batham               17395                             08/20/2014                   Made changes according to Humana standards
* Vishal Verma               18313                             10/06/2014                   Initializing variables
***************************************************************************************************/
global with sharing class HUMCaseCommentHelper
{ 
      Webservice String CommentBody = '';
      Webservice String CreatedById = '';
      Webservice String CreatedByName = '';
      Webservice String CreatedDate = '';
      Webservice String LastModifiedDate = '';
      Webservice String LastModifiedById = '';

}