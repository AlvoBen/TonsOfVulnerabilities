/**************************************************************************** 
Apex Class Name  : HUMCaseUpdateAttachmentDTO
Version          : 2.0  
Created Date     : May 9 2014 
Function         : This class is used as a base class to handle the return request
                  Used without sharing as the service is exposed to external system
Test Class       : HUMCaseUpdateAttachmentDTOTest                 
****************************************************************************
Modification Log:

* Developer Name       Code Review #                                Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi              17369                                05/24/2014                   Original Version
* Palak Papneja              18313                                09/30/2014                   Updated test class
* Palak Papneja              18313                                10/06/2014                   Initialized variables
***************************************************************************************************/
global with sharing class HUMCaseUpdateAttachmentDTO{
      
    Webservice String sFileName = '';
    Webservice String sAttachmentUrl = ''; 
}