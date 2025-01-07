/*******************************************************************************************************************************
Apex Class Name : Public class RecommendationInteraction_C_HUM  
Version         : 1.0
Created On      : 11/04/2020 
Function        : This class is used to get recommendation for interaction
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Suraj patil                                        11/04/2020                 Original Version
*    1.0           Sayali Nemade                                      01/29/2021                 Checkmarx Issues Resolved - Changed without sharing to with sharing 
*    1.1           Jayesh Solanki                                     05/14/2020                 User Story 2271553: T1PRJ0001827 - MF 1 - Quality - Add "Care Manager" as a new Interacting with type value (CRM)
*******************************************************************************************************************************************************************/
global with sharing class RecommendationInteraction_C_HUM 
{
    public String columnName {get; private set;}
    public String columnHeader{get; private set;}
    public String interactionID{get;
                              set {
                                    if(interactionID==Null)
                                    {
                                        interactionID = value;
                                        IsGetOfferActivityServiceCall();
                                    }
                                  }  
                             }
    public boolean bWithinDateRange{get;set;}
    public boolean bSingleInteractionMember{get;set;}
    public boolean bValidAccountType{get;set;}
    public boolean sNBASwitch {get;set;}

    global RecommendationInteraction_C_HUM ()
    {    
        columnName = json.serialize(new List<String>{'sAlertName','sActionTaken','sTermNotDeliveredReason','sActionDateTime','sAssociateID','bQuestions'});
        columnHeader = json.serialize(new List<String>{'Alert Name','Action','Not Delivered /Term Reason','Action taken Date/Time','Associate ID' ,'Question'});
    }
    
    global RecommendationInteraction_C_HUM (ApexPages.StandardController stdController)
    {    
        sNBASwitch = HUMUtilityHelper.isCRMFunctionalityON('1041229');
    }
    
    @RemoteAction
    global static List<RecommendationInteractionDTO_HUM> getRecommendationInfo(String InteractionId)
    {
        List<Recommendation_Action__c> listRecommendationAction = [SELECT Id, Recommendation__r.Name, Alert_Name__c , 
        Action__c, Action_Reason__c, Action_DateTime__c,Question_1_Response__c, Question_2_Response__c, Question_3_Response__c, 
        Question_4_Response__c, Free_Text_Response1__c, Free_Text_Response2__c, Interaction__c, 
        Question_1__c, Question_2__c, Question_3__c, Question_4__c,
        CreatedById, Recommendation_Detail__c FROM Recommendation_Action__c where Interaction__c =: InteractionId];
        
        User objUser = [SELECT Id, Network_User_Id__c FROM User where id =: listRecommendationAction[0].CreatedById];
        
        List<RecommendationInteractionDTO_HUM> listGuidanceAlertTableDTO;
        if(listRecommendationAction.size() > 0){
            listGuidanceAlertTableDTO = new List<RecommendationInteractionDTO_HUM>();
            List<string> lstQue;
            List<string> lstAns;
                    
            for(Recommendation_Action__c obj : listRecommendationAction ){
                RecommendationInteractionDTO_HUM objGuidanceAlertTableDTO = new RecommendationInteractionDTO_HUM();
                lstQue = New List<string>(); 
                lstAns = New List<string>();
                
                objGuidanceAlertTableDTO.sAlertName = String.IsNotBlank(obj.Recommendation__r.Name) ? obj.Recommendation__r.Name : '';
                objGuidanceAlertTableDTO.sActionTaken = String.IsNotBlank(obj.Action__c) ? obj.Action__c : '';
                objGuidanceAlertTableDTO.sTermNotDeliveredReason = String.IsNotBlank(obj.Action_Reason__c) ? obj.Action_Reason__c : '';
                objGuidanceAlertTableDTO.sActionDateTime = obj.Action_DateTime__c != null ? obj.Action_DateTime__c.format('yyyy/MM/dd hh:mm:ss') : '';
                objGuidanceAlertTableDTO.sAssociateID = objUser.Network_User_Id__c;
                
                if(String.IsNotBlank(obj.Question_1__c)){
                    lstQue.add(obj.Question_1__c);
                    if(String.IsNotBlank(obj.Question_1_Response__c)) lstAns.add(obj.Question_1_Response__c); else lstAns.add(' ');
                }
                if(String.IsNotBlank(obj.Question_2__c)){
                    lstQue.add(obj.Question_2__c);
                    if(String.IsNotBlank(obj.Question_2_Response__c)) lstAns.add(obj.Question_2_Response__c); else lstAns.add(' ');
                }
                if(String.IsNotBlank(obj.Question_3__c)){
                    lstQue.add(obj.Question_3__c);
                    if(String.IsNotBlank(obj.Question_3_Response__c)) lstAns.add(obj.Question_3_Response__c); else lstAns.add(' ');
                }
                if(String.IsNotBlank(obj.Question_4__c)){
                    lstQue.add(obj.Question_4__c);
                    if(String.IsNotBlank(obj.Question_4_Response__c)) lstAns.add(obj.Question_4_Response__c); else lstAns.add(' ');
                }
                
                if(lstQue != null) objGuidanceAlertTableDTO.lstQuestion = lstQue;
                if(lstAns != null) objGuidanceAlertTableDTO.lstAnswer = lstAns;
                if(lstQue.size() > 0)
                    objGuidanceAlertTableDTO.bQuestions = true;
                else
                objGuidanceAlertTableDTO.bQuestions = false;
                listGuidanceAlertTableDTO.add(objGuidanceAlertTableDTO);
            }
        }
        return listGuidanceAlertTableDTO;
    }
    
    /*
    Method Name : IsGetOfferActivityServiceCall
    Functionality : IsGetOfferActivityServiceCall is check if interaction is not 18 month old and Interaction having only one interaction member.
    Return type : void
    */
    public void IsGetOfferActivityServiceCall()
    {
        //Need to retrive the total number of Interaction members againt an interactin And also check the Interaction created date And guidance alert limit for queue
        List<Interaction_Member__c> lstInteractionMember = [SELECT Id, Interacting_About_Type__c, Interaction__r.CreatedDate, Interaction__r.Interacting_With_type__c, Interaction__r.Created_By_Queue__c from Interaction_Member__c where Interaction__c =: interactionID];
        if(!lstInteractionMember.isEmpty())
        {
            set<string> interactingWithTypeSet = new Set<String>{'Agent', 'Government', 'Group', 'Internal', 'Care Manager', 'Member', 'Member Representative or Caregiver','Other','Pharmacy','Provider','Unknown-Agent','Unknown-Group','Unknown-Member','Unknown-Provider'};
            //Check if Interaction created queue guidance alert limit is 0 if it is more than zero then only service call happens otherwise empty table will load. 
            //Becasue for queues having limit 0 we do not show alerts on account page. But still Service call happenes from Contact Handeling alerts section and CCBA team returns those records on Interaction Page.
            //Check the interacting about and with type is member or unknown member or care giver, If agent calls in and and inquire about member in that we will not call history service for that interaction.

            if(interactingWithTypeSet.contains(lstInteractionMember[0].Interaction__r.Interacting_With_type__c) && (lstInteractionMember[0].Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTGROUPTYPE_HUM || lstInteractionMember[0].Interacting_About_Type__c == GLOBAL_CONSTANT_HUM.CASEINFOSERVICE_ACCOUNTMEMBERTYPE_HUM))
            {
                //Yes, the interacting about and with type is member/ unknown member /or care giver
                bValidAccountType = true;
                Date sInteractionOldDate = date.valueOf(Date.today()).addMonths(GLOBAL_CONSTANT_HUM.IDCARDMGT_FROMDATEPERIOD_HUM);
                Date sInteractionCreatedDate = date.valueof(lstInteractionMember[0].Interaction__r.CreatedDate);   
                if(sInteractionCreatedDate >= sInteractionOldDate)
                {
                    //Yes, the interaction date is less than 18 months   
                    bWithinDateRange = true;
                    if(lstInteractionMember.size() == 1)
                    {
                        //Service needs to be called only for an interaction with single interaction member
                        bSingleInteractionMember = true;
                    }
                    else
                    {
                        // For Multiple interaction members, display a information message on screen
                        // Guidance Alerts are not available when more than one member is on an interaction.
                        bSingleInteractionMember = false;
                    }
                }
                else
                {   //Interaction greater than the specified date range should display a warning message
                    //Only alerts within the last 18 months appear in CRM Service
                    bWithinDateRange = false;
                }
            }
            else
            {
            //No, the interacting about and with type is not member/ unknown member
            bValidAccountType = false;
            }
        }
    }
}