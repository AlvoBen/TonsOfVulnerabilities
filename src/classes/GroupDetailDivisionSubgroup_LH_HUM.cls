/*******************************************************************************************************************************
Apex Class Name : GroupDetailDivisionSubgroup_LH_HUM
Version         : 1.0
Created On      : 11/03/2020
Function        : This class is the Helper class for GroupDetailDivisionSubgroup_LC_HUM
Test Class      : GroupDetailDivisionSubgroup_LT_HUM

Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Ashish Kumar                                     12/18/2020                Original Version
*    2.0           Kajal Namdev                                     05/07/2021                 Replace HUMConstants with GLOBAL_CONSTANT_LH_HUM class
*******************************************************************************************************************************************************************/

public with sharing Class GroupDetailDivisionSubgroup_LH_HUM
{
    @AuraEnabled public String sVitalityIndicator = '';
    @AuraEnabled public String sSubGroupID = '';
    @AuraEnabled public String sSubGroupName = '';
    @AuraEnabled public String sUnitCount = '';
    @AuraEnabled public String sUnit = '';
    @AuraEnabled public String sEffectiveProvision = '';
    @AuraEnabled public String sMTVLink = GLOBAL_CONSTANT_LH_HUM.sMTVLink;
    
    /*
    * Method Name : GroupDetailDivisionSubgroup_LH_HUM(Constructor)
    * Description : This method is used to intialize values to variables
    * Return type : NA
    * Parameter   : Boolean,String,String,String,String,String
    */    
    public GroupDetailDivisionSubgroup_LH_HUM(String sGroupId, Boolean bVIndicator,String sSubGrpId,String sSubGrpName,String sUnitCountValue,String sUnitValue,String sEffectiveProvisionValue)
    {
        this.sVitalityIndicator = (bVIndicator == True? GLOBAL_CONSTANT_LH_HUM.sYESValue : GLOBAL_CONSTANT_LH_HUM.sNoValue);
        this.sSubGroupID = sSubGrpId;
        this.sSubGroupName = sSubGrpName;
        this.sUnitCount = sUnitCountValue; 
        
        if(!String.isBlank(sUnitValue))
        {
            if(sUnitValue.equals(GLOBAL_CONSTANT_LH_HUM.sDayValue))    
            {
                this.sUnit = System.Label.GroupDivison_Days_HUM;
            }
            else if(sUnitValue.equals(GLOBAL_CONSTANT_LH_HUM.sMonthValue))    
            {
                this.sUnit = System.Label.GroupDivison_Months_HUM;
            }
            else if(sUnitValue.equals(GLOBAL_CONSTANT_LH_HUM.sHourValue))    
            {
                this.sUnit = System.Label.GroupDivison_Hours_HUM;
            }
            else 
            {
                this.sUnit = sUnitValue;
            }
        }
        else
        {
            this.sUnit = '';    
        }
        
        if(!String.isBlank(sEffectiveProvisionValue))
        { 
            if(sEffectiveProvisionValue.equals(GLOBAL_CONSTANT_LH_HUM.sFMValue))    
            {
                this.sEffectiveProvision = System.Label.GroupDivison_FirstOfMonth_HUM;
            }
            else if(sEffectiveProvisionValue.equals(GLOBAL_CONSTANT_LH_HUM.sIMValue))    
            {
                this.sEffectiveProvision = System.Label.GroupDivison_Immediate_HUM;
            }
            else if(sEffectiveProvisionValue.equals(GLOBAL_CONSTANT_LH_HUM.sEMvalue))    
            {
                this.sEffectiveProvision = System.Label.GroupDivison_EOM_HUM;
            }
            else if(sEffectiveProvisionValue.equals(GLOBAL_CONSTANT_LH_HUM.sGDvalue))    
            {
                this.sEffectiveProvision = System.Label.Group_Decides_Decision_HUM;
            }
            else    
            {
                this.sEffectiveProvision = sEffectiveProvisionValue; 
            }
        }
        else
        {
            this.sEffectiveProvision = '';    
        }
        
        if(!String.isBlank(sSubGrpId) && sSubGrpId.length() >= GLOBAL_CONSTANT_LH_HUM.GroupDivisonMax)
        {
            Map<String,String> mapParameters = new Map<String,String>();
            mapParameters.put('strGrpID',sGroupId);
            mapParameters.put('strDivID',sSubGrpId);
            sMTVLink += SingleSignOn_H_HUM.GetSingleSignOnUrl(Endpoint_Url_HUM__c.getInstance('MTVx').Remote_Url__c,mapParameters);
        }
        else
        {
            sMTVLink = '';    
        }
    }
}