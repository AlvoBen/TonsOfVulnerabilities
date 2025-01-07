/*****************************************************************************************
Apex class Name    : GroupDetailSection_H_HUM
Version            : 1.0
Function           : Helper Class for GroupDetailSection_C_HUM
Created Date       : 22 Oct 2015

Modification Log:
*   Developer                    Code Review         Date                   Description
* -------------------------------------------------------------------------------------------------------------------------               
*  SuryaKumari Medicherla         27320              10/22/2015             Original Version
*  Santhi Mandava                 27320              10/29/2015             Fixed CC comments
*  SuryaKumari Medicherla                            11/02/2015             Code modified for REQ-213234(MTVx link in Division-Subgroup related list)
*  Santhi Mandava                                     03/12/2019             Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
*  Ajay Chakradhar                                   11/22/2021             DEF 4182 FIX - Global Constant replace for performance improvement
*************************************************************************************************************************/    
public with sharing Class GroupDetailSection_H_HUM
{
    public String sVitalityIndicator{get;set;}
    public String sSubGroupID{get;set;}
    public String sSubGroupName{get;set;}
    public String sUnitCount{get;set;}
    public String sUnit{get;set;}
    public String sEffectiveProvision{get;set;}
    public String sMTVLink{get;set;}
    Public String sYESValue = 'Y';
    Public String sNoValue = 'N';
    Public String sDayValue = 'D';
    Public String sMonthValue = 'M';
    Public String sHourValue = 'H';
    Public String sFMValue = 'FM';
    Public String sIMValue = 'IM';
    Public String sEMvalue = 'EM';
    Public String sGDvalue = 'GD';
    
    /*
    * Method Name : GroupDetailSection_H_HUM
    * Description : This method is used to intialize values to variables
    * Return type : NA
    * Parameter   : Boolean,String,String,String,String,String
    */    
    public GroupDetailSection_H_HUM(String sGroupId, Boolean bVIndicator,String sSubGrpId,String sSubGrpName,String sUnitCountValue,String sUnitValue,String sEffectiveProvisionValue)
    {
        this.sVitalityIndicator = (bVIndicator == True? sYESValue : sNoValue);
        this.sSubGroupID = sSubGrpId;
        this.sSubGroupName = sSubGrpName;
        this.sUnitCount = sUnitCountValue; 
        
        if(!String.isBlank(sUnitValue))
        {
            if(sUnitValue.equals(sDayValue))    
            {
                this.sUnit = System.Label.GroupDivison_Days_HUM;
            }
            else if(sUnitValue.equals(sMonthValue))    
            {
                this.sUnit = System.Label.GroupDivison_Months_HUM;
            }
            else if(sUnitValue.equals(sHourValue))    
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
            if(sEffectiveProvisionValue.equals(sFMValue))    
            {
                this.sEffectiveProvision = System.Label.GroupDivison_FirstOfMonth_HUM;
            }
            else if(sEffectiveProvisionValue.equals(sIMValue))    
            {
                this.sEffectiveProvision = System.Label.GroupDivison_Immediate_HUM;
            }
            else if(sEffectiveProvisionValue.equals(sEMvalue))    
            {
                this.sEffectiveProvision = System.Label.GroupDivison_EOM_HUM;
            }
            else if(sEffectiveProvisionValue.equals(sGDvalue))    
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
        
        if(!String.isBlank(sSubGrpId) && sSubGrpId.length() >= 6)
        {
            Map<String,String> mapParameters = new Map<String,String>();
            mapParameters.put('strGrpID',sGroupId);
            mapParameters.put('strDivID',sSubGrpId);
            sMTVLink = SingleSignOn_H_HUM.GetSingleSignOnUrl(Endpoint_Url_HUM__c.getInstance('MTVx').Remote_Url__c,mapParameters);
        }
        else
        {
            sMTVLink = '';    
        }
    }
}