/**************************************************************************************************************************************
Apex Class Name : HUMVitalityResponseHelper
Version         : 1.0
Created On      : 07/30/2015
Function        : class used to have response of Member Goals in Vitality service
Test Class      : HUMVitalityMemberGoalResponseHelperTest

* Modification Log:
* Developer Name           Code Review         Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Gargey Sharma             25149             07/30/2015                 Original Version   
****************************************************************************************************************************************/
public with sharing class HUMVitalityMemberGoalResponseHelper
{
    public String sGoalId = '';
    public String sGoalName = '';
    public String sGoalDescription = '';
    public String sGoalPointValue = '';
    public String sStatus  = '';
    public String sEffectiveFromDateTime = '';
    public String sExternalEndDateTime = '';
    public String sDaysUntillExpiration = '';
    public String sMeasurementStartDateTime = '';
    public String sMonitorEndDateTime = '';
}