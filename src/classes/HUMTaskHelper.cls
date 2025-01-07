/**************************************************************************** 
Apex Class Name  : HUMTaskHelper
Version          : 1.0  
Created Date     : August 18 2014 
Function         : This class is used as a base class to handle the Task Helper
Test Class       : HUMTaskHelperTest
****************************************************************************
Modification Log:

* Developer Name                                                  Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham                                                  08/18/2014                   Original Version
***************************************************************************************************/
global with sharing class HUMTaskHelper
{ 
      Webservice String Subject;
      Webservice Date DueDate;
      Webservice String Status;
      Webservice String Priority;
      Webservice String Description;
      Webservice String TaskNumber;  
      Webservice Integer LastModifiedByAge;
      Webservice Integer AgeSinceCreated;
      Webservice String Comments;
      Webservice String TaskOwner;
      Webservice String OwnerQueue;
      Webservice String CreatedBy;
      Webservice String CreatedByQueue;
      Webservice String lastModifiedBy;
      Webservice String lastModifiedByQueue;

}