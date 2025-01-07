/**************************************************************************** 
Apex Class Name  : HUMInteractionLogHelper
Version          : 1.0  
Created Date     : June 22 2014 
Function         : This class is used as a base class to handle the Interaction Log
****************************************************************************
Modification Log:

* Developer Name                                                  Date                        Description
*------------------------------------------------------------------------------------------------------------
* Vivek Batham                                                  08/18/2014                   Original Version
***************************************************************************************************/
global with sharing class HUMInteractionLogHelper{ 

     Webservice String CallerName;
     Webservice String InteractingWithType;
     Webservice String InteractionOrigin; 

}