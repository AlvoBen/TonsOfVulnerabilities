/**************************************************************************** 
Apex Class Name  : HUMCaseUpdateRequestHelper
Version          : 2.0  
Created Date     : May 9 2014 
Function         : This class is used as a base class to handle the return request
Test Class       : HUMCaseUpdateRequestHelperTest                 
****************************************************************************
Modification Log:

* Developer Name                    Code Review#                    Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi                      17353                       05/24/2014                   Original Version
* Srikant Joshi                      17353                       05/27/2014                   Original Version
* Vivek Batham                       17353                       08/16/2014                   Updated according to Humana standards
* Vivek Batham                       17369                       08/19/2014                   Updated according to Humana standards
* Palak Papneja                      18313                       09/30/2014                   Updated test class
***************************************************************************************************/
global with sharing class HUMCaseUpdateRequestHelper{
    
      Webservice String sCaseNumber = '';
      Webservice String sCaseStatus = '';
      Webservice String sComment = ''; 
      Webservice String sQueueName = '';
      Webservice String sOwnerId = '';
      Webservice List<HUMCaseUpdateAttachmentDTO> lstAttachmentDTO = null ;    
}