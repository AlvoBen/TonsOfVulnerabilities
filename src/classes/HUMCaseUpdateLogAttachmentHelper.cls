/**************************************************************************** 
Apex Class Name  : HUMCaseUpdateLogAttachmentHelper
Version          : 2.0  
Created Date     : December 22 2014 
Function         : This class is used as a base class to handle the return request
Test Class       : HUMCaseUpdateLogAttachmentHelperTest               
****************************************************************************
Modification Log:

* Developer Name                    Code Review#                    Date                        Description
*------------------------------------------------------------------------------------------------------------
* Krishna Tadikonda                   20294                        12/22/2014                   Original Version
***************************************************************************************************/
global with sharing class HUMCaseUpdateLogAttachmentHelper{
    
      Webservice String sCaseNumber = '';
      Webservice String sCaseStatus = '';
      Webservice String sComment = ''; 
      Webservice String sQueueName = '';
      Webservice String sOwnerId = '';
      Webservice List<HUMCaseUpdateAttachmentDTO> lstAttachmentDTO = null ;   
      webservice HUMLogAttachmentRequestDTO attachmentDTO = null; 
}