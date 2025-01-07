/*****************************************************************************************
Apex class Name    : RepeatCallerConfiguration_DTO_HUM
Version            : 1.0
Function           : DTO class used to display Repeat Caller config
Created Date       : 06/18/2020
Test Class         : MemberIcons_CT_HUM

Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                                        Original Version
 *   Amit Agarwal                                       06/25/2020        User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *   Ajay Chakradhar                                    01/06/2022        US 2997677 : CheckMarx Fix 
 * --------------------------------------------------------------------------------------------------------------------------------
*/
public with sharing class RepeatCallerConfiguration_DTO_HUM 
{
    public String sId{get; set;} 
    public String sName{get; set;}
    public String sPermissionSet{get; set;}
    public Boolean bIsActive{get; set;}
    public String sTodaysIntsGreenRange{get; set;}
    public String sTodaysIntsAmberRange{get; set;}
    public String sTodaysIntsRedRange{get; set;}
    public String sPast14DaysIntsGreenRange{get; set;}
    public String sPast14DaysIntsAmberRange{get; set;}
    public String sPast14DaysIntsRedRange{get; set;}
    public String sOpenCasesGreenRange{get; set;}
    public String sOpenCasesAmberRange{get; set;}
    public String sOpenCasesRedRange{get; set;}
    public Boolean bIsTodaysIntsVisible{get; set;}
    public Boolean bIsPast14DaysIntsVisible{get; set;}
    public Boolean bIsOpenCasesVisible{get; set;}
    public String sInteractionOrigin{get; set;}
    public String sInteractingWithType{get;set;}
    public String sGreenColorCode{get; set;}
    public String sAmberColorCode{get; set;}
    public String sRedColorCode{get; set;}
     /**
    * Assign values to variables which Is used to render the values UI page.
    * <p>
    * Constructor Method to for the class , Initialize variables
    * @ Param 		RepeatCallerSettings_HUM__c  reference
    * @ Return 		NA
    */
    public RepeatCallerConfiguration_DTO_HUM (RepeatCallerSettings_HUM__c csRCSetting) 
    {
        if(csRCSetting != null) 
        {
            this.sId = csRCSetting.Id;
            this.sName = csRCSetting.Name;
            this.sPermissionSet = csRCSetting.PermissionSet__c;
            this.bIsActive = csRCSetting.IsActive__c;
            this.sTodaysIntsGreenRange = csRCSetting.TodaysInts_Green_Range__c;
            this.sTodaysIntsAmberRange = csRCSetting.TodaysInts_Amber_Range__c;
            this.sTodaysIntsRedRange = csRCSetting.TodaysInts_RedRange__c;
            this.sPast14DaysIntsGreenRange = csRCSetting.Past14DaysInts_Green_Range__c;
            this.sPast14DaysIntsAmberRange = csRCSetting.Past14DaysInts_Amber_Range__c;
            this.sPast14DaysIntsRedRange = csRCSetting.Past14DaysInts_RedRange__c;
            this.sOpenCasesGreenRange = csRCSetting.OpenCases_Green_Range__c;
            this.sOpenCasesAmberRange = csRCSetting.OpenCases_Amber_Range__c;
            this.sOpenCasesRedRange = csRCSetting.OpenCases_RedRange__c;
            this.bIsTodaysIntsVisible = csRCSetting.IsTodaysIntsVisible__c;
            this.bIsPast14DaysIntsVisible = csRCSetting.Is14DaysIntsVisible__c;
            this.bIsOpenCasesVisible = csRCSetting.IsOpenCasesVisible__c;
            this.sInteractionOrigin = csRCSetting.Interaction_Origin__c;
            this.sInteractingWithType = csRCSetting.Interacting_With_type__c;
            this.sGreenColorCode = csRCSetting.GreenColorCode__c;
            this.sAmberColorCode = csRCSetting.AmberColorCode__c;
            this.sRedColorCode = csRCSetting.RedColorCode__c;
        }
    }
     /*
    * This method is used to get Repeat Caller Custom Settings Values to display RC attributes
    * @param  N/A
    * @return RepeatCallerSettings_HUM__c 
    */
    public RepeatCallerSettings_HUM__c getRepeatCallerCustomSettings() 
    {
        RepeatCallerSettings_HUM__c csRC = new RepeatCallerSettings_HUM__c();
        csRC.id = this.sId;
        csRC.Name = this.sName;
        csRC.PermissionSet__c = this.sPermissionSet;
        csRC.IsActive__c = this.bIsActive;
        csRC.TodaysInts_Green_Range__c = this.sTodaysIntsGreenRange;
        csRC.TodaysInts_Amber_Range__c = this.sTodaysIntsAmberRange;
        csRC.TodaysInts_RedRange__c = this.sTodaysIntsRedRange;
        csRC.Past14DaysInts_Green_Range__c = this.sPast14DaysIntsGreenRange;
        csRC.Past14DaysInts_Amber_Range__c = this.sPast14DaysIntsAmberRange;
        csRC.Past14DaysInts_RedRange__c = this.sPast14DaysIntsRedRange;
        csRC.OpenCases_Green_Range__c = this.sOpenCasesGreenRange;
        csRC.OpenCases_Amber_Range__c = this.sOpenCasesAmberRange;
        csRC.OpenCases_RedRange__c = this.sOpenCasesRedRange;
        csRC.IsTodaysIntsVisible__c = this.bIsTodaysIntsVisible;
        csRC.Is14DaysIntsVisible__c = this.bIsPast14DaysIntsVisible;
        csRC.IsOpenCasesVisible__c = this.bIsOpenCasesVisible;
        csRC.Interaction_Origin__c = this.sInteractionOrigin;
        csRC.Interacting_With_type__c = this.sInteractingWithType;
        csRC.GreenColorCode__c = this.sGreenColorCode;
        csRC.AmberColorCode__c = this.sAmberColorCode;
        csRC.RedColorCode__c = this.sRedColorCode;
        return csRC;
    }
}