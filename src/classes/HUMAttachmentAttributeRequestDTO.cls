/**************************************************************************** 
Apex Class Name  : HUMAttachmentAttributeRequestDTO
Version          : 1.0  
Created Date     : Aug 18 2014 
Function         : This class is used as a base class to handle the return request for the class HUMCaseAttachmentInfoToCCP
Test Class       : HUMAttachmentAttributeRequestDTOTest
****************************************************************************
Modification Log:

* Developer Name                    Review Number                    Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi/Pranali Choudary       17844                       05/24/2014                   Original Version
* Palak Papneja                        18313                       09/30/2014                   Updated test class
* Palak Papneja                        18313                       10/06/2014                   Made changes according to Humana standards
***************************************************************************************************/
global with sharing class HUMAttachmentAttributeRequestDTO{
    
      Webservice String AttachmentName = '';
      Webservice String AttachmentValue = '';
   
}