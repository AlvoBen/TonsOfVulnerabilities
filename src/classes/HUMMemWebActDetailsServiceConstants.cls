/******************************************************************************************************************************
Apex Class Name  : HUMMemWebActDetailsServiceConstants
Version          : 1.0 
Created Date     : July 23 2015
Function         : Constants declared here are used for the Member Web Activity Details service.
Modification Log :
* Developer               Code review                   Date                 Description
* -----------------------------------------------------------------------------------------------------------------------------                
* Gargey Sharma              24997                   07/23/2015              Original version
* Moshitha Gunasekaran      186811                   02/20/2018              REQ – 355804 - Fix for CA Ticket 7366220  
*********************************************************************************************************************************/
public with sharing class HUMMemWebActDetailsServiceConstants 
{
    public static final String HUMMemberWebActivityDetailsService = 'HUMMemberWebActivityDetailsService';
    public static final String HUMMemberWebActivityDetailsPersonID = 'PersonId=';
    public static final String HUMVitalityEntityID = 'VitalityEntityID';
    public static final String HUMMemberVitalityIndicator = 'MemberVitalityIndicator';
    public static final String HUMMemberGenkey = 'MemberGenkey';
    public static final String HUMLastLoginDateTime = 'LastLoginDateTime';
    public static final String HUMIsWebRegistered = 'IsWebRegistered';
    public static final String HUMErrorMemberWebActivity = 'ErrorMemberWebActivity';
    public static final String HUMError = 'An Error Occured.';
    public static final String HUMNoInputs = 'No Inputs found.';
    public static final String HUMWEBActivityID = '1005871665';

    public static final String CONTINUATION_PROCESSRESPONSE_HUM= 'processContinuation';
    public static final String IsWebRegistered_HUM = 'IsWebRegistered'; 
    public static final String LastLoginDateTime_HUM = 'LastLoginDateTime';
    public static final String MemberGenKey_HUM = 'MemberGenKey'; 
    public static final String PolicyVitalityFalse_HUM = 'PolicyVitalityFalse';
    public static final String PolicyVitalityTrue_HUM = 'PolicyVitalityTrue';
    public static final String VitalityEntityID_HUM = 'VitalityEntityID'; 
    public static final String EffectiveDate_HUM = 'EffectiveDate'; 
    public static final String EndDate_HUM = 'EndDate';  
    public static final String MajorLineOfBusiness_HUM = 'MLOB';
    public static final String ProductLine_HUM = 'productLineCd';
    public static final String ServiceIDType_HUM  = 'IDType';
    public static final String ServiceVitalityFlag_HUM = 'VitalityFlag';
    public static final String ServiceIDValue_HUM = 'IDValue';
    public static final String ServiceStatus_HUM = 'ServiceStatus';
    public static final String ServiceStatusFull_HUM = 'FullService';
    public static final String ServiceStatusNo_HUM = 'NoService';
    public static final String ServiceExpired_HUM = 'Expired';
    public static final String ServicePersonIDInputValue_HUM  ='PERSONID';
    public static final String ServiceVitalityFlagValueYes_HUM = 'Y';
    public static final String ServiceVitalityFlagValueNo_HUM =  'N';
    public static final String ServiceGenInputValue_HUM =  'GENKEY';
	public static final String ServiceCacheGenInputValue_HUM =  'CACHEGENKEY';
    public static final String TimeValue_HUM =  ' 10:00 AM';
    public static final String DateFormat_HUM=  'y-MM-dd';
}