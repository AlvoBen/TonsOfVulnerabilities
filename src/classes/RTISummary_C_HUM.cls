/**************************************************************************************************************************************************
Apex class Name    :  RTISummary_C_HUM
Version            :  1.0
Function           :  This is controller for RTI Page

   Modification Log:
    
 *   Developer                   Code Review             Date               Description
 * ------------------------------------------------------------------------------------------------------------------------
 *  Kritika Popat                                        06/26/2017         Original version
 *  Kritika Popat                                        05/15/2018         REQ - 357638 Collapse Panel for Provider
 *  Satyam Malviya                                       12/03/2018         NBA MF-4 GUidance Alert
 *  Sumit Pharande									     2/2/2019           Provider RTI Panel fix
 *  Sumit Pharande			 							                    REQ-386780 -Removed the 18 to 15 Digit Id conversion
 *  Anjani Vaddadi                                       4/09/2020          REQ-845546-RTI Panel
 *  Muralidhar Kollu                                     08/05/2020          REQ-836091-remove Informational message for HO/General member
 * Trina Ghosh                                        02/09/2021            US:1669848 - Low Security Issues Fix
*****************************************************************************************************************************/
public with sharing class RTISummary_C_HUM
{
    @TestVisible private String sId{get;set;}
    public Boolean bSecurity{get;private set;}
    public Integer iInteracting{get;private set;}
    //check to display RTI section on right side panel or not    
    public String sAccountAlertId{get;set;}
    public String URLString {get;set;}    
    public String bServiceFlag{get;set;}       
    public String bCollapsedSidePanel{get;set;} 
    public String bRTIcheck{get;set;}
    public String sAccountId{Get;Set;}   
    public Boolean NBANewAlertEngineSwitch{get;private set;}

    Public RTISummary_C_HUM()
    {
		NBANewAlertEngineSwitch = HUMUtilityHelper.isCRMFunctionalityON('1041229');
        Boolean bcheckHOuser = True;
        ClaimsSummaryHOCheck_C_HUM_V2 ObjClaimsSummaryHOCheck_C_HUM_V2 = New ClaimsSummaryHOCheck_C_HUM_V2();
        bSecurity = True;
        iInteracting = 9000;
        sId = ApexPages.currentPage().getParameters().get('Id').escapeHtml4();  
        sAccountId =  ApexPages.currentPage().getParameters().get('Id'); 
        bRTIcheck = ApexPages.currentPage().getParameters().get('RTIFlag');
        bServiceFlag = ApexPages.currentPage().getParameters().get('ServiceFlag');
        bCollapsedSidePanel =  ApexPages.currentPage().getParameters().get('CollapsedSidePanel');        
        Account objAcnt = RTISummary_D_HUM.getAccountDetails(sId);  
        String sUserId = UserInfo.getUserId(); 
        bcheckHOuser = ObjClaimsSummaryHOCheck_C_HUM_V2.isNonHoUser(sUserId);
        if (String.isNotBlank(sUserId)) 
        {
            List<Interaction__c> lstInteraction = [Select Id, CreatedbyID, CreatedDate, Interacting_With_type__c from Interaction__c where Interacting_With_type__c = 'Provider' and CreatedDate >= :Datetime.now().addMinutes(-15) and CreatedbyId =: sUserId  limit 10];
            if(!lstInteraction.isEmpty())
            {
                Set<Id> setID = (new Map<Id,Interaction__c>(lstInteraction)).keySet();
                List<Interaction_Member__c> lstIntrcMember = [Select Id, CreatedbyID, CreatedDate, Interaction__c, Interacting_About__c from Interaction_Member__c  where CreatedDate >= :Datetime.now().addMinutes(-15) and CreatedbyId =: sUserId and Interaction__c =: setID and Interacting_About__c =: objAcnt.Id limit 10];
                if(!lstIntrcMember.isEmpty())    
                {
                    bSecurity = false;
                    iInteracting = 1000;
					bCollapsedSidePanel='true';
                }
            }   
        }
    }
    
    /*
     * Method Name   :    getVerifyAccountType
     * Description   :    Send the boolean value to display the ButtonSection on vf page 
     * Return Type   :    boolean
     * Parameters    :    NA
     */
     public Boolean getVerifyAccountType()
     {
         Boolean isDisplay = true;
         try
            {
                 //sId = ApexPages.currentPage().getParameters().get('id');
                 List<Account> accnts = [Select id,Name,RecordType.Name from Account where id =:sAccountId limit 1 ];
                 if(!(accnts.isEmpty()))
                 {
                    for(Account accnt : accnts)
                        {
                             isDisplay = (accnt.RecordType.Name == 'Member' || accnt.RecordType.Name == 'Unknown Member' );
                             
                       }
                 }
           }
         catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'RTISummary_C_HUM', 'getVerifyAccountType');
        }
         return  isDisplay;
     }
}