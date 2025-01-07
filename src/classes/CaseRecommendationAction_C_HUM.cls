/************************************************************************************************************************************************
Apex Class Name :  CaseRecommendationAction_C_HUM 
Version          : 1.0
Created Date     : Jun 29 2020
Function         : This class is used to for providing Case Recommendation for OGO Cases.
Test Class       :
*************************************************************************************************************************************************
Modification Log:

* Modification Id           Developer Name                       Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* 1.0                       Vinay Lingegowda                     06/29/2020                 Original Version
* 1.1                       Vijaykumar                           07/24/2020                 REQ - 1275884 - trigger auto clone case for Accept Recommendation
* 1.2                       Vinay Lingegowda                     07/24/2020                 US-1278736 - PR00091574-IR-50-50 OGO-Link Original and cloned cases bi-directionally
* 1.3                       Vinay Lingegowda                     08/03/2020                 DF-1245 Fix.
* 1.4 			            Vinay Lingegowda                     08/11/2020 		        USER STORY 1376901- INC0404513 - Update set case owner as owner queue for recommendation routing
* 1.3                       Vijaykumar                           08/21/2020                 US-1293644 -PR00091574- IR 5050-OGO -Actions and routing associated on the recommendation page (Accept, Defer - re-opened cases)- (CRM)
* 1.6			            Vinay Lingegowda                     08/24/2020                 User Story 1314983: PR00091574- IR 5050-OGO -Open case in Edit mode to set complaint fields (Scrubber Action-Accept on open cases) -(CRM)
* 1.7                       Subhamay Mandal                      08/27/2020                 User Story 1293633: PR00091574-IR 5050-OGO - Recommendation actions on re-opened cases (reject )-(CRM)
* 1.8                       Subhamay Mandal                      08/27/2020                 User Story 1293633: Build Fix
* 1.9                       Subhamay Mandal                      08/27/2020                 User Story 1293633: SIT Observation
* 2.0                       Vinay Lingegowda                     11/13/2020                 USER STORY 1628353 PR00091574 - IR 5050 - OGO - Add a new View "Clarabridge Recommendations" for Re-opened cases (CRM)
* 2.1                       Vinay Lingegowda                     03/08/2021                 USER STORY 2044469 IR 5050 - OGO - Optimize Accept recommendation actions for queues with clone & link feature (CRM)
* 2.2                       Mithra Bharadwaj                                                US#2089572 SonarQube Changes
* 2.3					    Mohammed Noor                        07/01/2021          		US2273201 - Case Management Checkmarx Security violations  
****************************************************************************************************************************************************************************/

public with sharing class CaseRecommendationAction_C_HUM {

    public case oCaseRec{get;set;}
    public Task oNewTask {get;set;}
    public boolean bShowPopUp {get;set;}
    public string sPopupMsg {get;set;}
    Boolean bRecommendedOG;
    public Boolean bCloneCaseScenario{get; set;}
    private String sSetTopic = '';
    private String sErrorMessage = '';
  

    /*
    * Constructor 
    */
    public CaseRecommendationAction_C_HUM(){
        this.bShowPopUp = false;
        this.bRecommendedOG = false;
         String sCaseId = ApexPages.currentPage().getParameters().get('id').escapeHtml4();
        this.oCaseRec = CaseRecommendationAction_S_HUM.getCaseDetails(sCaseId);
        this.oNewTask = CaseRecommendationAction_S_HUM.populateTaskDetails(this.oCaseRec);
        this.bCloneCaseScenario = false;
    }
    
    /*
    * Inserts the Task and provides the Case ID for JavaScript to open Case detail page
    * 
    * @see Success: the Task Edit page opens for the new case recommendations. 
    */
    public void createRecommendation(){        
        try {
            String sSetTaskType = this.oNewTask.Type;            
            String sPreviousWorkQueueView = this.oCaseRec.Work_Queue_View_Name__c;
            Boolean bSetComplaint = false;
            String sCurrentWorkQueueView='';

            RecommendedQueueView__c csRQVDetail = UtilityHelper_H_AHT_HUM.getRecommendedQueueViewDetail(this.oCaseRec.Work_Queue_View_Name__c);
            this.bRecommendedOG = (csRQVDetail.OGO_QueueView_Name__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASE_OGO_ROG_VIEW) || 
                                   csRQVDetail.OGO_QueueView_Name__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CASE_OGO_CBR_VIEW));
            sPopupMsg = GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_DEFERRED_MSG;
            if(String.isNotBlank(sSetTaskType) && !this.bRecommendedOG) {
                
                if(sSetTaskType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASK_FEEDBACK_ACCEPT_TYPE)) {
                    if(csRQVDetail.Clone_Case__c){
                        if(Schema.SObjectType.Case.Fields.Autoroute_Status__c.isUpdateable())	this.oCaseRec.Autoroute_Status__c = GLOBAL_CONSTANT_HUM.AUTOROUTING_EVALUATE;
                        bCloneCaseScenario = true;
                    }else {
                    	bSetComplaint = true;
                    	sSetTopic = csRQVDetail != null? csRQVDetail.Set_Topic_for_Accepted__c : '';
                    	if(String.isNotBlank(sSetTopic) && sSetTopic.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM)) {
                           bSetComplaint = false;
                        }
                        if(Schema.SObjectType.Case.Fields.Autoroute_Status__c.isUpdateable())	this.oCaseRec.Autoroute_Status__c = GLOBAL_CONSTANT_HUM.ROUTED_PENDING_ACCEPT;
                    }
                
                } else if(sSetTaskType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASK_FEEDBACK_REJECT_TYPE) && Schema.SObjectType.Case.Fields.Autoroute_Status__c.isUpdateable()) {
						this.oCaseRec.Autoroute_Status__c = GLOBAL_CONSTANT_HUM.AUTOROUTING_EVALUATE;
                
					}    
				}
            

            if(!sSetTaskType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASK_FEEDBACK_DEFERRED_TYPE) ){
                if(this.bRecommendedOG) {
                    if(sSetTaskType.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.TASK_FEEDBACK_REJECT_TYPE)) {
                        CaseRecommendationAction_S_HUM.assginCaseBackToUser(this.oCaseRec);
                        sErrorMessage = CaseRecommendationAction_S_HUM.updateCaseFields(this.oCaseRec);
                        sPopupMsg = GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_ROGVIEW_REJECT_MSG;
                        this.bRecommendedOG = false;
                    }else{
                        sPopupMsg = GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_ROGVIEW_ACCEPT_MSG;
                    }
                } else {
                    CaseRecommendationAction_S_HUM.assginCaseValues(this.oCaseRec, bSetComplaint, sSetTopic);
                    sErrorMessage = CaseRecommendationAction_S_HUM.updateCaseFields(this.oCaseRec);
                    if(String.isBlank(sErrorMessage) ){
                        sCurrentWorkQueueView = [select Work_Queue_View_Name__c from case where id =:this.oCaseRec.Id].Work_Queue_View_Name__c;
                        if(String.isNotBlank(sCurrentWorkQueueView) && sCurrentWorkQueueView.equalsIgnoreCase(sPreviousWorkQueueView)){
                            sPopupMsg = GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_DEFERRED_MSG;
                        }else{
                            sPopupMsg = GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_ACCREJ_MSG + sCurrentWorkQueueView + GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_MSG;
                        }
                    }
                }
            }else{
                this.bRecommendedOG = false;
            }

            if(bCloneCaseScenario && String.isBlank(sErrorMessage)){
                sSetTopic = csRQVDetail != null? csRQVDetail.Set_Topic_for_Accepted__c : '';
                String sView = sSetTopic.contains('CIT') ? ' CIT ' :' HCPR ';
                sPopupMsg = GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_CLONEACCEPT_MSG1 + sCurrentWorkQueueView + GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_CLONEACCEPT_MSG2 + sView  + GLOBAL_CONSTANT_HUM.CASE_OGO_POPUP_MSG;
            }

            if(String.isBlank(sErrorMessage)) {
                sErrorMessage = CaseRecommendationAction_S_HUM.insertNewTask(this.oNewTask);
                if(String.isBlank(sErrorMessage)) {
                    this.bShowPopUp = true;
                } else {
                    throw new CaseRecommendationCustomException(sErrorMessage);    
                }
            } else {
                throw new CaseRecommendationCustomException(sErrorMessage);
            }

        }catch (Exception e){
            HUMExceptionHelper.logErrors(e, 'CaseRecommendationAction_C_HUM', 'createTaskError');
        }
    }
    
    /*
    * This method is used when user clicks on the Edit Button on the Case Detail VF page 
    * @param  NA
    * @return PageReference
    */
    public PageReference referEdit(){
        PageReference pageRef = null;
        Case oCloneCaseRec;
        String sCloneCaseId;
        if(bCloneCaseScenario && String.isBlank(sErrorMessage)){
            oCloneCaseRec = CaseRecommendationAction_S_HUM.cloneCaseRecord(this.oCaseRec.id, sSetTopic);
            if(oCloneCaseRec != null) {
                sCloneCaseId = oCloneCaseRec.id;
            }
        }
        
        if(bRecommendedOG){
            pageRef = new Pagereference('/'+oCaseRec.Id+'/e');
            pageRef.getParameters().put('isOGO', 'OGOA');  
        }else {
            pageRef = new Pagereference('/'+oCaseRec.Id); 
            if(sCloneCaseId != null){
                pageRef.getParameters().put('CloneCaseId', sCloneCaseId); 
            }
        }
        pageRef.setRedirect(true);
        return pageRef;
    }
    public class CaseRecommendationCustomException extends Exception { }
}