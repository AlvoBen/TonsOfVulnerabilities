/*******************************************************************************************************************************
Apex Class Name : METMilestoneTAS_DTO_HUM.cls
Version         : 1.0
Created On      : 2018
Function        : Wrapper class used in the generic ManualDeployment Method for T/A/S Milestone entries. 

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Mohammed Noor              238265                  06/01/2018                   Original Version.

*******************************************************************************************************************************/
public with sharing class METMilestoneTAS_DTO_HUM 
{
    public string sCaseType;		//Case Type
    public string sCaseSubtype;		//Case Subtype
    public string sTaskName;		//MET Task Name
    public string sActionName;		//MET Action Name
    public string sSourceName;		//MET Source Name
    public string sMilestone;		//MET Milestone
    public boolean bLaunchEmme;		//Launch EMME Flag
    public boolean bIsMilestone; 	//Is Milestone Flag
        
    public  METMilestoneTAS_DTO_HUM(string sCaseType, string sCaseSubtype, string sTaskName, string sActionName, string sSourceName, string sMilestone, boolean bLaunchEmme, boolean bIsMilestone)  
    {
        this.sCaseType = sCaseType;
        this.sCaseSubtype = sCaseSubtype;
        this.sTaskName = sTaskName;
        this.sActionName = sActionName;
        this.sSourceName = sSourceName;
        this.sMilestone = sMilestone;
        this.bLaunchEmme = bLaunchEmme;
        this.bIsMilestone = bIsMilestone;
    }      
}