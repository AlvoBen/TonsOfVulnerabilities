/*****************************************************************************************
Apex class Name    : MemberIcons_C_HUM
Version            : 1.0
Function           : Controller to VF Component: MemberIcons_CMP_HUM
                     This class is used to display the list of member icons
Created Date       : 08/13/2019
Test Class         : MemberIcons_CT_HUM

    Modification Log
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------------  
 *   Santhi Mandava                                  08/13/2019          Original Version
 *   Sagar MN                                        01/14/2019          REQ - 416054 Checmarx Sharing Vulnerability Fix
 *   Amar Gadewar                                    01/28/2020          USER STORY 729689 PR00091574 - MF 1 - User Interface to manage Icons display (CRM)
 *   Amar Gadewar                                    02/07/2020          USER STORY 774245 PR00091574 - MF 1 - Iconology - H1036 Icon (CRM)
 *   Amit Agarwal                                    06/25/2020          User Story 1186331: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (RF)
 *   Amit Agarwal                                    07/17/2020          User Story 1276140: PR00091574 - MF 1 - Quality - Ability to display Repeat Caller Icon on search page and Repeat Caller Information on the Member Account page (CRM)
 *   Suresh Kokkeragadda                             07/29/2020          User Story 1317726: PR00091574 - MF 1 - SF - TECH - On/Off Switch Cleanup 7/30 , 8/14 and 8/28 releases (CRM)
 *   Samantha Tennyson                               09/08/2020          User Story 1449973: PR00091574 - MF 1 - Iconology - FIDE Icon (CRM)
 *   Atia Uzma                                       09/11/2020          User Story 1449938: PR00091574 - MF 1 - Iconology - HIDE Icon (CRM)
 *   Raja Sekhar Reddy M V                           11/13/2020          User Story 1660597: PR00096346 - MF 1 - DEV - New Member Indicator
 *   Samantha Tennyson                               02/08/2021          Removing unused existing logic for Dual Eligible Icon
 *   Himalay Patel									 03/16/2021 		 REQ - 1711424: Display Expired and Expiring Soon Credit Card Icon (CRMS) (ID# 131)
 *   Ajay Chakradhar                           		 05/05/2021	         REQ - 2184294 - Replacing constants with Consumer Management Constants
 *   AshokKumar Nutalapati   						 07/12/2021          REQ - 1907149 T1PRJ0002517 - MF 2 - Display Icon When Pharmacy Authorization on File (RxRF) (ID# 131)
 *   Atia Uzma                                       09/21/2021          User Story 2696506: T1PRJ0001827 - MF 1 - Iconology - ID Card Request Icon (RF)
 *   Atia Uzma                                       01/18/2022          User Story 2977635: T1PRJ0314829 - MF 1 - Iconology - LI NET Icon (RF)
 *   Subhamay Mandal                                 05/10/2022          User Story 3272362: T1PRJ0314829 - MF 1 - Iconology - ID Card Request Icon (CRM)
 *   Kiran Kumar B									 07/05/2022			 User Story 3253058: T1PRJ0325139 - MF 1 - Iconology - IL MMP Icon (CRM)
 *   Anil Parvthaneni                                11/08/2022          User Story 3501670: T1PR0325139 - FR1.02 - Medicaid (MCD) New Member Icon in CRM (Classic)
 *   Kiran Kumar B									 02/23/2023			 User Story 3790703: T1PRJ0325139 - MF 1 - Iconology - Update Code Framework for IL MMP Icon (CRM) (Classic)
 ************************************************************************************************************************************************************************************/

public with sharing class MemberIcons_C_HUM
{
    public String sRecordId{get;set;}
    public String sPageName{get;set;}
    public String sBirthDate{get;set;}
    public Boolean bExpiredIcon{get;set;}
    public Boolean bExpiringSoonIcon{get;set;}
	public Boolean bPharmacyAuthIcon{get;set;}
    public String sDeceasedDate{get;set;}
    public String scurrInteractionId{get;set;}
    public MemberIcons_DTO_HUM objIcons{get;set;}
    public boolean bNewMCDMemberIconOnOffSwitch {get;set;}
    public boolean bNewMemberIconOnOffSwitch {get;set;}
    Public boolean bVeteranIconOnOffSwitch {get; set;}
    Public string sVeteranStatus{get; set;}
    Private String sConfirmedVeteran = 'confirmed-veteran';
    Private String sIdentifiedVeteran = 'identified-veteran';
    public string sStatus{get; set;}
    public Boolean bIDcard{get;set;}
    public boolean bLINETIconOnOffSwitch {get;set;}
	public Boolean bILMMPIconSwitch {get{ return HUMUtilityHelper.isCRMFunctionalityON('3253058_1');} Set;}
	
    /*
    * This method is used to fetch icons for the given page
    * @param  N/A
    * @return Void
    */
    public void getMemberIconStatus()
    {    
      try{  
        objIcons = MemberIcons_S_HUM.getPageIcons(sPageName);
        List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstFinalIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
        bNewMemberIconOnOffSwitch  = HUMUtilityHelper.isCRMFunctionalityON('1660597');
        bVeteranIconOnOffSwitch  = HUMUtilityHelper.isCRMFunctionalityON('1260069');
        bLINETIconOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('2977635'); 
        bNewMCDMemberIconOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('3501670');
        Integer iSearchIconsCount = 0;
        objIcons.bIconsPresnt = false;
        If(objIcons != Null && objIcons.lstMemberIcons != Null)
        {
            Boolean bDeceased = MemberIcons_S_HUM.getDeceasedIconStatus(sDeceasedDate);
            for(MemberIcons_DTO_HUM.Icon_DTO_HUM iconObject : objIcons.lstMemberIcons)
            {
                switch on iconObject.sIconName  
                {
                    when 'BirthDay' {
                        iconObject.bIconVisible = false;
                        if(!bDeceased) iconObject.bIconVisible = MemberIcons_S_HUM.getBirthdayIconStatus(sBirthDate);
                    }
                    when 'Deceased' {
                        iconObject.bIconVisible = bDeceased;
                    }
                    when 'NewMember' {
                        iconObject.bIconVisible = false;
                        if(!bDeceased && bNewMCDMemberIconOnOffSwitch) iconObject.bIconVisible = MemberIcons_S_HUM.getNewMemberIconStatus(sRecordId);
                    }
                    when 'GroupMedicare' {
                        if(sPageName == 'Member Account' || sPageName == 'Search') iconObject.bIconVisible = MemberIcons_S_HUM.getGroupMedicareIconStatusForAccount(sRecordId);
                        else  iconObject.bIconVisible = MemberIcons_S_HUM.getGroupMedicareIconStatusForPolMember(sRecordId);
                    }
                    when 'MedicareSupplement' {
                        if(sPageName == 'Member Account' || sPageName == 'Search') iconObject.bIconVisible = MemberIcons_S_HUM.getMedicareSupplementIconStatusForAccount(sRecordId);
                        else iconObject.bIconVisible = MemberIcons_S_HUM.getMedicareSupplementIconStatusForPolMember(sRecordId);
                    }
                    when 'H1036Contract' {
                         iconObject.bIconVisible = false;
                        if(sPageName == 'Member Account' || sPageName == 'Search') iconObject.bIconVisible = MemberIcons_S_HUM.getH1036ContractIconForAccount(sRecordId);
                        else iconObject.bIconVisible = MemberIcons_S_HUM.getH1036ContractIconForPolMember(sRecordId);
                         
                    }
                    when 'RepeatCaller' {
                        iconObject.bIconVisible = false;
                        if(sPageName == 'Member Account' || sPageName == 'Search')
                            iconObject.bIconVisible = MemberIcons_S_HUM.getRepeatCallerIconStatus(sRecordId,scurrInteractionId);
                                               
                   }
                    when 'FIDE' {
                       iconObject.bIconVisible = false;
                       if(sPageName == 'Member Account' || sPageName == 'Search') iconObject.bIconVisible = MemberIcons_S_HUM.getFIDEIconForAccount(sRecordId);
                       else iconObject.bIconVisible = MemberIcons_S_HUM.getFIDEIconForPolMember(sRecordId);
                    }
                    when 'HIDE' { 
                        iconObject.bIconVisible = false;
                        if(sPageName == 'Member Account' || sPageName == 'Search') iconObject.bIconVisible = MemberIcons_S_HUM.getHIDEIconForAccount(sRecordId);
                        else iconObject.bIconVisible = MemberIcons_S_HUM.getHIDEIconForPolMember(sRecordId);
                    }
                    when 'VeteranIcon'  {
                        iconObject.bIconVisible = false; 
                        if(sveteranStatus != null && bVeteranIconOnOffSwitch && (sVeteranStatus.equalsIgnoreCase(sConfirmedVeteran) || sVeteranStatus.equalsIgnoreCase(sIdentifiedVeteran))){
                             iconObject.bIconVisible = MemberIcons_S_HUM.getVeteranIconStatus(sRecordId);                                                            
                        }
                    }
                    when 'creditCardExpiredIcon' { 
                        iconObject.bIconVisible = bExpiredIcon;                       
                    }
                    when 'creditCardExpiringSoonIcon' { 
                        iconObject.bIconVisible = bExpiringSoonIcon;                      
                    }
					when 'PharmacyAuthorization' { 
						iconObject.bIconVisible = bPharmacyAuthIcon;                      
					}
                    when'IDCardRequest'{
                        iconObject.bIconVisible = false;
                        if(sPageName == 'Policy Member'){
                            iconObject.bIconVisible = MemberIcons_S_HUM.getIDCardIconForPolMember(sRecordId);
                            if(iconObject.bIconVisible) {
                                bIDcard = MemberIcons_S_HUM.checkIDCardIconActionalble(sRecordId);
                            }
                        }
                    }
                    when 'LINET' {
                       iconObject.bIconVisible = false;
                        if(UtilityHelper_H_AHT_HUM.isRapidForceUser() && bLINETIconOnOffSwitch){
                           if(sPageName == 'Member Account' || sPageName == 'Search') iconObject.bIconVisible = MemberIcons_S_HUM.getLINETIconForAccount(sRecordId);
                            else iconObject.bIconVisible = MemberIcons_S_HUM.getLINETIconForPolMember(sRecordId,sStatus);
                        }
                    }
                    // Iconology - IL MMP Icon
                    when 'IL_MMP' {
                       iconObject.bIconVisible = false;
                        if(bILMMPIconSwitch){
                            if(sPageName == 'Member Account' || sPageName == 'Search'){ 
                               iconObject.bIconVisible = MemberIcons_S_HUM.getILMMPIconForAccount(sRecordId); 
                            } 
                            else { 
                                iconObject.bIconVisible = MemberIcons_S_HUM.getILMMPIconForPolMember(sRecordId); 
                            }
                        }
                    }
                }
                if(iconObject.bIconVisible) lstFinalIcons.Add(iconObject);
            }
            if(!lstFinalIcons.isEmpty()) objIcons.bIconsPresnt = true;
            objIcons.lstMemberIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
            List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstTemp = MemberIcons_S_HUM.sortIcons(lstFinalIcons);
            If(sPageName == 'Search')
            {
                if(lstTemp != Null && !lstTemp.isEmpty() && lstTemp.size() > 5)
                {
                    for(integer i= 0; i < 5;i++)
                    {
                        objIcons.lstMemberIcons.Add(lstTemp[i]);
                    }
                }
                else
                {
                    objIcons.lstMemberIcons = lstTemp;
                }
            }
            else
            {
                objIcons.lstMemberIcons = lstTemp;
            }
        }
      }
      catch(Exception ex){
        HUMExceptionHelper.logErrors(ex, 'MemberIcons_C_HUM.', 'getMemberIconStatus');
      }
    }
}