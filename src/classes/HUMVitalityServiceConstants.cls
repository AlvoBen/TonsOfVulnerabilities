/******************************************************************************************************************************
Apex Class Name  : HUMVitalityServiceConstants
Version          : 1.0 
Created Date     : July 21 2015
Function         : Constants declared here are used for the Vitality service.
Modification Log :
* Developer               Code review                   Date                 Description
* -----------------------------------------------------------------------------------------------------------------------------                
* Gargey Sharma            24953                     07/21/2015              Original version
* Harshith Mandya                                    04/21/2106              Defect # 223238.0001
*********************************************************************************************************************************/
public with sharing class HUMVitalityServiceConstants 
{
    public static final String HUMVitalityService = 'HUMVitalityService';
    public static final String HUMVitalityReqId = 'EntityId=';
    public static final String HUMVitalityResponseData = 'MemberVitalityData';
    public static final String HUMVitalityResponseMemberEntityNumber = 'MemberEntityNumber';
    public static final String HUMVitalityResponseError = 'error';
    public static final String HUMVitalityResponseErrorCode = 'error_code';
    public static final String HUMVitalityResponseErrorReason = 'error_reason';
    public static final String HUMVitalityResponseVitalityAge = 'VitalityAge';
    public static final String HUMVitalityResponseVitalityBucksBalances = 'VitalityBucksBalances';
    public static final String HUMVitalityResponseSpendableBucksBalance = 'SpendableBucksBalance';
    public static final String HUMVitalityResponseStatusBucksBalance = 'StatusBucksBalance';
    public static final String HUMVitalityResponseBuckstoNextStatus = 'BucksToNextStatus';
    public static final String HUMVitalityResponseVitalityStatuses = 'VitalityStatuses';
    public static final String HUMVitalityResponseCarryoverStatus = 'CarryoverStatus';
    public static final String HUMVitalityResponseEarnedStatus = 'EarnedStatus';
    public static final String HUMVitalityResponseHraCompleted = 'IsHraCompleted';
    public static final String HUMVitalityResponseMemberGoals = 'MemberGoals';
    public static final String HUMVitalityResponseGoalId = 'GoalID';
    public static final String HUMVitalityResponseGoalName = 'GoalName';
    public static final String HUMVitalityResponseGoalDescription = 'GoalDescription';
    public static final String HUMVitalityResponseGoalPointValue = 'GoalPointValue';
    public static final String HUMVitalityResponseStatus = 'Status';
    public static final String HUMVitalityResponseEffectiveFromDateTime = 'EffectiveFromDateTime';
    public static final String HUMVitalityResponseExternalEndDateTime = 'ExternalEndDateTime';
    public static final String HUMVitalityResponseInternalEndDateTime = 'internalEndDate';
    public static final String HUMVitalityResponseDaysUntillExpiration = 'DaysUntillExpiration';
    public static final String HUMVitalityResponseMeasurementStartDateTime = 'MeasurementStartDateTime';
    public static final String HUMVitalityResponseMonitorEndDateTime = 'MonitorEndDateTime';
    public static final String HUMVitalityResponsesMemberGoal = 'MemberGoal';
    public static final String HUMVitalityResponsesStatus = 'status';
    public static final String HUMVitalityCDATAEndTag = '%5D%5D%3E';
    public static final String HUMVitalityCDATAStartTag = '%3C%21%5BCDATA%5B';
    public static final String HUMVitalityUnicodeStandard = 'UTF-8';
    public static final String HUMVitalityErrorMessage = 'An Error Occured';
    public static final String HUMVitalityEntityID = '2510757319';
    public static final String HUMMYHumanaVitality = 'Yes';
    public static final String CONTINUATION_PROCESSRESPONSE_HUM= 'processResponse';
    public static final Integer VITALITY_DIVISION_HUM= 1000;
    public static final Integer HUMContinuationTimeout = 120;
    public static final String HUMClassNameVitalityService = 'Vitality_S_HUM';
    public static final String HUMMethodNameCopyWebRegistered = 'copyWebRegistered';
    public static final String HUMClassNameVitalityHelper = 'Vitality_H_HUM';
    public static final String HUMMethodNameSerializeEntityId = 'serializeEntityId';
    public static final String CONTINUATION_PROCESSRESPONSENEXT_HUM = 'processResponseNextData';
}