/**************************************************************************************************************************************
Apex Class Name : HUMVitalityResponseHelper
Version         : 1.0
Created On      : 06/24/2015
Function        : class used to have response of Vitality service
Test Class      : HUMVitalityResponseHelperTest

* Modification Log:
* Developer Name           Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Pradeep Veeravali         24953              06/24/2015                 Original Version
* ChiranjeeviRao ravuri     24953              07/09/2015                 Added child node fields of MemberGoals
* Gargey Sharma             24953              07/23/2015                 Modified Error Structure
* Gargey Sharma             25149              07/30/2015                 Moved response structure for Member Goals to a seperate class   
****************************************************************************************************************************************/

public with sharing class HUMVitalityResponseHelper
{    
    public ResponseHelper Response;
    public string sError='';    
    public string slastLoginDate='' ;
    public string swebRegistered ='';
    public string sMemGenKey='' ;        
}