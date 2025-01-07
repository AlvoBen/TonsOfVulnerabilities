/*******************************************************************************************************************************
Apex Class Name : HUMAccountDetailPageController 
Version         : 1.0
Created On      : 07/24/2014
Function        : To display limited records to User(s) who don't have access to this account record.
                  The class is made as 'without sharing' since we are conditionally 
                  overriding security to retrieve the account details, but displaying necessary information 
                  based on security.
TEst class      : HUMAccountDetailPageControllerTest
                  
Modification Log: 
* Developer Name                Code Review          Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Manish Kumar                  17122                      07/24/2014            Original Version
* Pallavi R                     165514                     12/22/2017            REQ - 350418 -  MA Post Search Functionality Resolution
* Pallavi R                                                06/06/2018            REQ - 363592 - Rollback Resolution Search tab Changes 
* Ankit Avula                                              06/18/2021            US 2360351:INC1132612 - User has limited access, the group page says "Person Account" instead of "Business Account"
* Kiran Bhuvanagiri										   11/05/2021			 US 2791814: Account pages of all types (Change to PROD)
* Vardhman Jain                                            11/11/2021               Added switch on Account and Policy page for Coveo
*******************************************************************************************************************************/
public without sharing class HUMAccountDetailPageController
{
    public boolean switchOnOffBusinessAccountTitle {get;set;}
    public boolean bShowUnifiedSearch{get;set;}
    public String interactionNumber {get;set;}
    public String interactionWithType {get;set;}
    public String interactionAbout {get;set;}
    public String interactWith {get;set;}
      
  /*
   * Method Name   :    HumAccountDetailPageController - Constructor with StandardController parameter
   * Description   :    Used to initialize few variables
   * Return Type   :    NA
   * Parameters    :    NA
   */
    public HUMAccountDetailPageController()
    {
        //As per the US 2360351 Added the switch logic.
        switchOnOffBusinessAccountTitle = HUMUtilityHelper.isCRMFunctionalityON('2360351');
        String sIntId = Apexpages.currentpage().getparameters().get('interactionId');
        getUnifiedSearchDeatils(sIntId);
    }
                
    public Account acc
    { 
        get{
            String id =  ApexPages.currentPage().getParameters().get('id');
            if(!String.isBlank(id) && id.startsWith('001') && id.length() >= 15)    acc = [SELECT Name, Phone, Industry, firstName, Salutation, LastName, Birthdate__c, PersonEmail, PersonMailingState, 
                                                    PersonMailingPostalCode, PersonHomePhone, RecordType.Name, Security_Groups__c 
                                                    FROM Account WHERE Id = : id.subString(0, 15)];
            return acc;
    } private set; }
    /*
	* Method Name   :  getUnifiedSearchDeatils
	* Description   :  This method checks the conditions for the Unified search button display in Account Detail page
	* Parameters    :Interaction id
	*/
	 @testvisible private void getUnifiedSearchDeatils(string sIntId) { 
        string sBrowserContext = ApexPages.currentPage().getHeaders().get('USER-AGENT');

        if(sBrowserContext.contains(ConsumerManagementConstants_HUM.sChromeBrowser) || sBrowserContext.contains(ConsumerManagementConstants_HUM.sEdgeBrowser)){
        bShowUnifiedSearch = HUMUtilityHelper.isCRMFunctionalityON('ConsumerMgmtUnifiedSearchSwitch') && UnifiedSearchUtility_H_HUM.isUnifiedSearchEligible(); 
        if(bShowUnifiedSearch){
			getInteractiondetails(sIntId);
            } 
        }
        
    }
    @testvisible private void getInteractiondetails(string sIntId){
        if(sIntId!= null){									   
		Interaction_Member__c [] interactionMember =[SELECT Id,Interaction__r.name,Interaction__r.Interacting_With_type__c,
                                                      Interacting_About_type__c,Interaction__r.Interacting_With__c,
													  Interaction__r.Interacting_With__r.Name
													  FROM Interaction_Member__c 
                                                      Where interaction__c =:sIntId];								   
        if ( interactionMember != null && !interactionMember.isEmpty())
		{
            interactionNumber = interactionMember[0].Interaction__r.name;
            interactionWithType = interactionMember[0].Interaction__r.Interacting_With_type__c;
            interactionAbout = interactionMember[0].Interacting_About_type__c;
            interactWith = interactionMember[0].Interaction__r.Interacting_With__r.Name;
        }
        }
    }
}