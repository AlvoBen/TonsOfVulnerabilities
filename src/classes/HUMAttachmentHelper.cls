/**************************************************************************** 
Apex Class Name  : HUMAttachmentHelper
Version          : 1.0  
Created Date     : August 18 2014 
Function         : This class is used as a base class to handle the Attachments
Test Class       : HUMAttachmentHelperTest
****************************************************************************
Modification Log:

* Developer Name        Review Number                            Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham            17369                                08/18/2014                   Original Version
* Shruthi Karanth         18313                                09/29/2014                   Upadted test class
* Palak Papneja           18313                                10/06/2014                   Made changes according to Humana standards
* Srivinas Damera                                              9/26/2018                    REQ - 374689 - Update CaseService V1 and V3 - GetCaseInfo method
***************************************************************************************************/
global with sharing class HUMAttachmentHelper
{ 
    
    Webservice String FileName = '';
    Webservice String AttachmentURL = '';
	Webservice String DocId ='';
    Webservice String VersionNumber ='';
}