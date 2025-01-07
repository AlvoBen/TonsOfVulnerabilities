/*****************************************************************************************
Apex class Name    : MemberIcons_LC_HUM
Version            : 1.0
Function           : controller for displaying Icons along with Open Cases and Interaction Count
Created Date       : 03/12/2021
Test Class         : MemberIcons_LT_HUM
Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
*Ankima Srivastava										 03/12/2021				  initial version
*Ashish Kumar                                            04/23/2021               Added changes related to Icons on Policy Detail page
*Ankima Srivastava                                       06/04/2021               Added changes related to Medicare Suppliment Icon
*Ankima Srivastava                                       07/07/2021               Added changes related to Other Insurance Icon
*Ankima Srivastava                                       07/12/2021               Defect Fix for Medicare Suppliment icon
*Ankima Srivastava                                       07/14/2021               Rollback - Defect Fix for Medicare Suppliment icon
*Ankima Srivastava                                       08/09/2021               Defect Fix for Medicare Suppliment icon
*Ankima Srivastava                                       08/11/2021               Legacy Delete Icon Update
*Abhishek Mangutkar										 08/09/2021				  Added changes related to ICons on Humana Pharmacy page REQ_2526741
*Abhishek Mangutkar										 09/20/2021				  Added changes related to ICons on Humana Pharmacy page REQ_2527039_2527065
*Firoja Begam										     11/02/2021				  US:2468373 made OtherInsurance Icon visible
*Jonathan Dickinson										 10/28/2021				  Added changes related to ICons on Humana Pharmacy page REQ_2674165
*visweswararao j                                        05/31/2022               US2746576:Account Management - New Classic Icon Implementations - Veteran Icon
*Anil Parvthaneni                                       11/08/2022               User Story 3501670: T1PR0325139 - FR1.02 - Medicaid (MCD) New Member Icon in CRM (Classic)
*Muthukumar												01/05/2023				 US 4077850 Dual Eligible Icon/Plan Member page (Enabler)
*Deepakkumar Khandelwal								    02/02/2023				 US-4137330: T1PRJ0865978 - MF 24067 /  4097490 -C13, Lightning - CORE - Match Icons to Plans 1
*Deepakkumar Khandelwal									 06/30/2023				  US_4742449_ T1PRJ0865978 - INC2384724/Contact Handling Alert Icon is displayed even when there are no contact handling alerts for that member.
*Deepakkumar Khandelwal									 07/10/2023				  US_4816948 : Dev 3/Veteran Icon & New Member icon is not always showing in CRM Lightning (even when displaying in classic)
****************************************************************************************************************/
public with sharing class MemberIcons_LC_HUM
{
    public static MemberIcons_DTO_HUM objIcons{get;set;}
    public static boolean bNewMemberIconOnOffSwitch {get;set;}
     public static boolean bNewMCDMemberIconOnOffSwitch {get;set;}
    
    /*
* This method is used to fetch icons for the given page
* @param  N/A
* @return Void
*/
    @AuraEnabled
    public static MemberIcons_DTO_HUM getMemberIconStatus(String sPageName,string sRecordId)
    {  
        String scurrInteractionId='';
        String sDeceasedDate='';
        String sBirthDate='';
		String sStatus='';
        Boolean bLINETIconOnOffSwitch =false;  
        Boolean bIDcard =false; 
        Boolean bILMMPIconSwitch = false;
		Boolean bVeteranIconOnOffSwitch =false; 
		try{
			if(sRecordId!=null && sPageName!='Policy Member'){
				Account AccRec = MemberIcons_LS_HUM.getBirthDescDate(sRecordId);
				 if(AccRec!=null){
					sBirthDate = AccRec.Birthdate__c;
					sDeceasedDate = AccRec.Deceased_Date__c;
				 }
			}
			objIcons = MemberIcons_LS_HUM.getPageIcons(sPageName);
			List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstFinalIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
			List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstDeceasedFinalIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
			List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstNormalFinalIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
			bNewMemberIconOnOffSwitch  = HUMUtilityHelper.isCRMFunctionalityON('1660597');
			bNewMCDMemberIconOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('3501670');
			bLINETIconOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('2977635');  
			bILMMPIconSwitch = HUMUtilityHelper.isCRMFunctionalityON('3253058_1'); 
			bVeteranIconOnOffSwitch = HUMUtilityHelper.isCRMFunctionalityON('1260069'); 
			Integer iSearchIconsCount = 0;
			objIcons.bIconsPresnt = false;
			If(objIcons != Null && objIcons.lstMemberIcons != Null)
			{	
				Boolean bDeceased = MemberIcons_LS_HUM.getDeceasedIconStatus(sDeceasedDate);
				for(MemberIcons_DTO_HUM.Icon_DTO_HUM iconObject : objIcons.lstMemberIcons)
				{
					if(iconObject.sDocumentId!=''){
						String docUrl = URL.getSalesforceBaseURL().toExternalForm()+'/servlet/servlet.FileDownload?file='+iconObject.sDocumentId;
						iconObject.sDocumentId = docUrl;
					}
					switch on iconObject.sIconName  
					{
						when 'BirthDay' {
							iconObject.bIconVisible = false;
							if(!bDeceased) iconObject.bIconVisible = MemberIcons_LS_HUM.getBirthdayIconStatus(sBirthDate);
						}
						when 'Deceased' {
							iconObject.bIconVisible = bDeceased;
						}
						when 'NewMember' {
							iconObject.bIconVisible = false;
							if(!bDeceased  && bNewMCDMemberIconOnOffSwitch) iconObject.bIconVisible = MemberIcons_LS_HUM.getNewMemberIconStatus(sRecordId);
						}
						when 'GroupMedicare' {
							if(sPageName == 'Member Account' || sPageName == 'Search'){
								iconObject.bIconVisible = MemberIcons_LS_HUM.getGroupMedicareIconStatusForAccount(sRecordId);
							}
							else {
								iconObject.bIconVisible = MemberIcons_LS_HUM.getGroupMedicareIconStatusForPolMember(sRecordId);
							} 

						}
						when 'MedicareSupplement' {
						   if(sPageName == 'Member Account' || sPageName == 'Search'){
								iconObject.bIconVisible = MemberIcons_LS_HUM.getMedicareSupplementIconStatusForAccount(sRecordId);
							}
							else{
								iconObject.bIconVisible = MemberIcons_LS_HUM.getMedicareSupplementIconStatusForPolMember(sRecordId);
							}
						}
						when 'H1036Contract' {
							iconObject.bIconVisible = false;
							if(sPageName == 'Member Account' || sPageName == 'Search'){
								iconObject.bIconVisible = MemberIcons_LS_HUM.getH1036ContractIconForAccount(sRecordId);
							}
							else{
								iconObject.bIconVisible = MemberIcons_LS_HUM.getH1036ContractIconForPolMember(sRecordId);
							}
						}
						when 'RepeatCaller' {
							iconObject.bIconVisible = false;
							if(sPageName == 'Member Account' || sPageName == 'Search' || sPageName == 'Humana Pharmacy'){
								iconObject.bIconVisible = MemberIcons_LS_HUM.getRepeatCallerIconStatus(sRecordId,scurrInteractionId);
							}
							else
							{
								iconObject.bIconVisible=false;  
							}
						}
						when 'ContactHandlingAlert'{
							iconObject.bIconVisible = true;     
						}
						when 'VeteranIcon'{ 
							if(bVeteranIconOnOffSwitch){
							iconObject.bIconVisible = MemberIcons_LS_HUM.getVeteranIconStatus(sRecordId) ;
							}
						}
						when 'OtherInsurance'{
							iconObject.bIconVisible = true;    
						}
						when 'FIDE' {
							iconObject.bIconVisible = false;
							if(sPageName == 'Member Account' || sPageName == 'Search'){
							iconObject.bIconVisible = MemberIcons_LS_HUM.getFIDEIconForAccount(sRecordId);
							}
							else{
								iconObject.bIconVisible = MemberIcons_LS_HUM.getFIDEIconForPolMember(sRecordId);
							}
						}
						when 'HIDE' { 
							iconObject.bIconVisible = false;
							if(sPageName == 'Member Account' || sPageName == 'Search'){
							iconObject.bIconVisible = MemberIcons_LS_HUM.getHIDEIconForAccount(sRecordId);
							}
							else{
								iconObject.bIconVisible = MemberIcons_LS_HUM.getHIDEIconForPolMember(sRecordId);
							}
						}
						when 'DualEligiblity' {
							iconObject.bIconVisible=false;
							if(sPageName == 'Policy Member'){
								iconObject.bIconVisible=true;
							}
						}
						when 'creditCardExpiringSoonIcon' {
							iconObject.bIconVisible = false;
							if(sPageName == 'Humana Pharmacy')
							{
								iconObject.bIconVisible = true;
							}
						}
						when 'LINET' {
						   iconObject.bIconVisible = false;
							if(bLINETIconOnOffSwitch ){   
								if(sPageName == 'Member Account' || sPageName == 'Search') {
								   iconObject.bIconVisible = MemberIcons_LS_HUM.getLINETIconForAccount(sRecordId); 
								}
								else{
									iconObject.bIconVisible = MemberIcons_LS_HUM.getLINETIconForPolMember(sRecordId,sStatus);
									 
								}
							}
						}                        
					 
						 when 'IL_MMP' {    
						   iconObject.bIconVisible = false; 
							if(bILMMPIconSwitch){
								if(sPageName == 'Member Account' || sPageName == 'Search'){
								   iconObject.bIconVisible = MemberIcons_LS_HUM.getILMMPIconForAccount(sRecordId);
								} 
								else{ 
									iconObject.bIconVisible = MemberIcons_LS_HUM.getILMMPIconForPolMember(sRecordId);
								}
							}
						}                                  
						 when'IDCardRequest'{
							iconObject.bIconVisible = false;
							if(sPageName == 'Policy Member'){
								iconObject.bIconVisible = MemberIcons_LS_HUM.getIDCardIconForPolMember(sRecordId);
								if(iconObject.bIconVisible) {
									bIDcard = MemberIcons_LS_HUM.checkIDCardIconActionalble(sRecordId);
								}
							}
						}
						when 'creditCardExpiredIcon' {
							iconObject.bIconVisible = false;
							if(sPageName == 'Humana Pharmacy')
							{
								iconObject.bIconVisible = true;
							}
						} 
					}
					if(iconObject.bIconVisible) {
						if(bDeceased && iconObject.sIconName=='Deceased'){
							lstDeceasedFinalIcons.Add(iconObject);
						}else{
							lstNormalFinalIcons.Add(iconObject);
						}
					}
				}
				  if(bDeceased){
					lstFinalIcons.Addall(lstDeceasedFinalIcons);
				}else{
					lstFinalIcons.Addall(lstNormalFinalIcons);
					
				}
				if(!lstFinalIcons.isEmpty()) objIcons.bIconsPresnt = true;
				objIcons.lstMemberIcons = new List<MemberIcons_DTO_HUM.Icon_DTO_HUM>();
				List<MemberIcons_DTO_HUM.Icon_DTO_HUM> lstTemp = MemberIcons_LS_HUM.sortIcons(lstFinalIcons);
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
		catch(Exception e)
        { 
            HUMExceptionHelper.logErrors(e,'MemberIcons_LC_HUM','getMemberIconStatus');
            return null;
        }
        return objIcons;
    }
    
    @AuraEnabled
    public static RepeatCallerInformation_DTO_HUM getInteractionAndCase(String sPageName,String AccountId,String MemberPlanID){
       try{
			String InteractionId ='';
			RepeatCallerInformation_DTO_HUM objRepeatCallerInfo = MemberIcons_LS_HUM.getRepeatCallerInformation(sPageName,MemberPlanID,AccountId,InteractionId,true); 
			return objRepeatCallerInfo;
		}
	    catch(Exception e)
        { 
            HUMExceptionHelper.logErrors(e,'MemberIcons_LC_HUM','getInteractionAndCase');
            return null;
        }
   }
   
	/*
	*This method is used to dynamically retrieve the URL of the expired and expiring soon credit card icons
	*/
	@AuraEnabled
	public static String getExpiredAndExpiringSoonIconURLs(String sPageName, String iconType)
	{  
		try{
				objIcons = MemberIcons_LS_HUM.getPageIcons(sPageName);
				objIcons.bIconsPresnt = false;
				If(objIcons != Null && objIcons.lstMemberIcons != Null)
				{   
					for(MemberIcons_DTO_HUM.Icon_DTO_HUM iconObject : objIcons.lstMemberIcons)
					{
						if(iconObject.sDocumentId!=''){
							String docUrl = URL.getSalesforceBaseURL().toExternalForm()+'/servlet/servlet.FileDownload?file='+iconObject.sDocumentId;
							iconObject.sDocumentId = docUrl;
						}

						if (iconType == 'expired' && sPageName == 'Humana Pharmacy' && iconObject.sIconName == 'creditCardExpiredIcon') {
							return iconObject.sDocumentId;
						} else if (iconType == 'expiring' && sPageName == 'Humana Pharmacy' && iconObject.sIconName == 'creditCardExpiringSoonIcon'){
							return iconObject.sDocumentId;
						}
					}
				}
		}
		catch(Exception e)
        { 
            HUMExceptionHelper.logErrors(e,'MemberIcons_LC_HUM','getExpiredAndExpiringSoonIconURLs');
            
        }
		return '';
	}
}