/**************************************************************************** 
Apex Class Name  : HUMAttachmentRequestDTO
Version          : 1.0  
Created Date     : May 9 2014 
Function         : This class is used as a base class to handle the return request for the class HUMCaseAttachmentInfoToCCP
Test Class       : HUMAttachmentRequestDTOTest
****************************************************************************
Modification Log:

* Developer Name                          Review Number            Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi/Pranali Choudary          17844                   05/24/2014                   Original Version
* Palak Papneja                           18313                   09/30/2014                   Updated test class
* Vishal Verma                            18313                   10/06/2014                   Initializing variables
***************************************************************************************************/
global with sharing class HUMAttachmentRequestDTO{    
        
      Webservice List<HUMAttachmentAttributeRequestDTO> AttachmentAttribute = null; // this variable will hold mutiple refrences to the AttachmentAttribute class    
      Webservice String CreatedBy = '';
      Webservice String CaseId = '';
      Webservice String AttachmentDescription = '';
      Webservice String AttachmentKey = '';
      Webservice String AttachmentType = '';     

}