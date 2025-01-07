/**************************************************************************** 
Apex Class Name  : HUMAttachmentHelper
Version          : 1.0  
Created Date     : June 22 2014 
Function         : This class is used as a base class to handle the Attachment Logs
Test Class       : HUMAttachmentItemHelperTest
****************************************************************************
Modification Log:

* Developer Name       Review Number                            Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham          17369                                08/18/2014                   Original Version
* Palak Papneja         18313                                09/30/2014                   Updated test class
* Palak Papneja         18313                                10/06/2014                   Made changes according to Humana standards
* Shruthi Karanth				30552                 							 06/15/2016					  REQ - 269656 - Added logging parameters : UserSelected and UserSelectedExt
***************************************************************************************************/
global with sharing class HUMAttachmentItemHelper{ 

    
    Webservice String LoggedBy = '';
    Webservice String LoggedDateTime = '';
    Webservice String AttachmentDescription = '';
    Webservice String AttachmentKey = '';      
    Webservice String AttachmentType = '';
    Webservice String UserSelected = '';
    Webservice String UserSelectedExtn = ''; 
    Webservice List<HUMAttachmentItemAttributeHelper> AttachmentAttribute = null; 
    

}