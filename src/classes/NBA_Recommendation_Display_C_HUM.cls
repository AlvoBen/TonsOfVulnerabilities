/*******************************************************************************************************************************
Apex Class Name : NBA_Recommendation_Display_C_HUM.cls
Version         : 1.0
Created On      : 10/13/2020
Function        : Class is base class for Recommendation_Display_VF_HUM used to open lightning flow from visual force page.

Modification Log:
* Developer Name            Code Review           Date                       Description
*------------------------------------------------- -----------------------------------------------------------------------------
* Suraj Patil									10/13/2020					Original version	
*****************************************************************************************************************************************/

public without sharing class NBA_Recommendation_Display_C_HUM{

    public Boolean InteractionId{get;set;} 
    public list<String> lstRecommendationActionIds{get; set;}
    public String actionIds{get; set;}
	public string sPageName{get;set;}
   
    /*
     * Method Name : NBA_Recommendation_Display_C_HUM
     * Description : Constructor for the class
     * Return type : None
     * Paramater   : None 
     */
    public NBA_Recommendation_Display_C_HUM() {
    lstRecommendationActionIds = new list<string>();     
         String sInteraction = ApexPages.currentPage().getParameters().get('InteractionId').escapeHtml4();
         InteractionId = sInteraction.equalsIgnoreCase('TRUE');
         actionIds = ApexPages.currentPage().getParameters().get('lstRecommendationActionIds').escapeHtml4();
         lstRecommendationActionIds.add(actionIds);
		 sPageName = actionIds = (ApexPages.currentPage().getParameters().get('sPageName')!=null)?ApexPages.currentPage().getParameters().get('sPageName').escapeHtml4():null;
	}
}