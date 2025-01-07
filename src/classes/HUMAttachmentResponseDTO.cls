/**************************************************************************** 
Apex Class Name  : HUMAttachmentResponseDTO
Version          : 1.0  
Created Date     : May 9 2014 
Function         : This class is used as a base class to handle the return request for the class HUMCaseAttachmentInfoToCCP
Test Class       : HUMAttachmentResponseDTOTest
****************************************************************************
Modification Log:

* Developer Name                   Code Review                     Date                        Description
*------------------------------------------------------------------------------------------------------------
* Srikant Joshi/Pranali Choudary     17369                     05/24/2014                   Original Version
* Shruthi Karanth                    17844                     09/10/2014                   Made changes to naming
* Palak Papneja                      18313                     09/30/2014                   Updated test class
* Vishal Verma                       18313                     10/06/2014                   Initialize variables
***************************************************************************************************/
global class HUMAttachmentResponseDTO{
    
    //initialize variables before usage
    webservice String CaseId = null;
    webservice String AttachmentId  = null;
    webservice List<String> InteractionNumber  = null;
    webservice boolean Success = null;
    webservice String ErrorCode = null;
    webservice String ErrorMessage = null;
    
}