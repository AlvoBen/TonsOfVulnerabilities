/************************************************************************************************************************************************
Apex Class Name  : DevelopmentTaskController_DTO_HUM
Version          : 1.0
Created Date     : 2018
Function         : Wrap class for DevelopmentTaskController_C_HUM.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Santhosh Ganji                                       11/01/2018                   Original Version
*************************************************************************************************************************************************/
Public class DevlopmentTaskController_DTO_HUM
{
  Public String Days{get;set;} 
  Public Decimal IdealHrs{get;set;}  
  Public Decimal LoggedHrs{get;set;} 
  Public Decimal RemainingHrs{get;set;}
  Public Decimal ChartDataPoints {get;set;}
 
}