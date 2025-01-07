/*******************************************************************************************************************************
Apex Class Name : CaseTransferSave_LD_HUM
Version         : 1.0
Created On      : 03/02/2022
Function        : This class is for save functionality of change case owner
Test Class      : 

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											03/02/2022                 Original Version and Implemented onload functionlity
*     2.0 					 Vijaya Lakshmi Tummala 							    02/02/2023                 US 4128928 T1PRJ0170850-  Lightning- Escalation Indicator on Case Transfer Page
******************************************************************************************************************************************************************************/

public with sharing class CaseTransferSave_LD_HUM {
    
    public static CaseTransferWrapper wCaseTranfer;
    
    //Boolean variables
    public static boolean bSwitch_1414643 = HUMUtilityHelper.isCRMFunctionalityON('1414643');
    public static boolean bisErrorExist;
    public static boolean bNoMHKCase = false;
    public static boolean bCheckMediCC= true;
    public static boolean bMedicareCalls= false;
    public static boolean bQMSFlag= false;
    public static boolean bisQMSErrorExist= false;
    public static boolean bCase= false;
    public static boolean bNoQMSFlag= false;
    
    //collection Variables
    public static Map < String, Work_Queue_Setup__c > mapServCenDeptWorkQueueSetup = new Map < String, Work_Queue_Setup__c > ();
    
    //String variables
    public static String sSelectedWorkQueue;
    public static String sPublicGroupName;
    public static String sGrpQueueName;
    public static String ownerId;
    public static String ownerName;
    
    /*
* Method Name : calEscalationIndDate
* Description : This method will calculate escalation indicator date
* Return type : void
* Parameter   : case
*/
public static void calEscalationIndDate(Case objCase){
    if(objCase.Id !=null){
        
    Case caseRec=[select id, Escalation_Indicator__c,Escalation_Indicator_Date__c from Case where id=:objCase.Id limit 1];
      
    if(schema.SobjectType.case.fields.Escalation_Indicator_Date__c.isUpdateable())
                 {
                    if((objCase.Escalation_Indicator__c !=null) && string.isNotBlank(objCase.Escalation_Indicator__c) && caseRec.Escalation_Indicator__c !=objCase.Escalation_Indicator__c){
                        String escIndDate = !string.isBlank(caseRec.Escalation_Indicator_Date__c) ? caseRec.Escalation_Indicator_Date__c : '' ;
                        objCase.Escalation_Indicator_Date__c = escIndDate +'\n'+system.now().format(CONSTANTS_CASE_HUM.DATE_Format) + ' - '+objCase.Escalation_Indicator__c;
                        
                    }
                     
                 }
    } 
    update  objCase;
}


    /**
     * 	@Function -Method to perform case tranfer operations
     *  @name saveCaseTransfer
     *  @param String caseTranferInput
     *  @return Object
     */
    @AuraEnabled
    public static Object saveCaseTransfer(String caseTranferInput)
    {
        try{
            //Getting case, caseComment, OwnerId, WQS unique map from LWC
            wCaseTranfer = (CaseTransferWrapper) JSON.deserialize(caseTranferInput, CaseTransferWrapper.class);            
            Case oCaseRec = wCaseTranfer.oCaseRec;
            String sCommentBody = wCaseTranfer.caseComment;
            mapServCenDeptWorkQueueSetup = wCaseTranfer.mapServCenDeptWorkQueueSetup;
            ownerId= wCaseTranfer.ownerId;
            ownerName= wCaseTranfer.ownerName;

            if(string.isNotBlank(oCaseRec.Escalation_Indicator__c)){
                calEscalationIndDate(oCaseRec);
                }
            
            if(bMedicareCalls){
                //TODO : logic for Medicare care in future sprints 
            }
            if(bSwitch_1414643 && string.isNotBlank(oCaseRec.Service_Center__c) && oCaseRec.Service_Center__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Service_Center_Provider) 
               && string.isNotBlank(oCaseRec.Department__c) && oCaseRec.Department__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK) 
               && string.isNotBlank(oCaseRec.Type) && oCaseRec.Type.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE) && string.isBlank(sCommentBody) )
            {
                List<CaseComment> lstCaseComments = [SELECT Id,CommentBody, CreatedById,CreatedBy.Name,CreatedDate, ParentId FROM CaseComment where ParentId =: oCaseRec.Id and CreatedBy.Name !=: GLOBAL_CONSTANT_HUM.DatapowerUserName];
                if(lstCaseComments == null || (lstCaseComments != null && lstCaseComments.isEmpty()))
                {	
                    bisErrorExist = true;
                    bNoMHKCase = true;
                }    
            }
            
            //US#1083065 When Case Type other than MHK Task is transferred to 'Correspondence Task To MHK' Department and 'Provider' Service Center then error message will be displayed
            if(bSwitch_1414643 && string.isNotBlank(oCaseRec.Service_Center__c) && oCaseRec.Service_Center__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Service_Center_Provider)
               && string.isNotBlank(oCaseRec.Department__c) && oCaseRec.Department__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK) 
               && ((string.isNotBlank(oCaseRec.Type) && !oCaseRec.Type.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE)) || string.isBlank(oCaseRec.Type)))
            {
                bisErrorExist = true;
                bNoMHKCase = true;
            }
            
            if(bSwitch_1414643)
            {
                if (NULL != oCaseRec && !bNoMHKCase)
                {
                    assignFixedValuesToCase();   
                }    
            }
            else
            {
                assignFixedValuesToCase();   
            } 
            
            if(bCheckMediCC)
            {
                if(String.isBlank(oCaseRec.User__c))
                {	
                    if(String.isNotBlank(sSelectedWorkQueue) && ( sSelectedWorkQueue?.equals(System.label.CHANGECASEOWNER_QMSQUEUE_HUM)|| sSelectedWorkQueue?.equals(System.label.CHANGECASEOWNER_MARKETHOQMSQUEUE_HUM)))
                    {
                        List<Case_Action_Association__c> lstCaseAssosciation = [Select Id,Case_Action__r.Name,(Select Process_Question__c,Process_Question_Response__c from Case_Action_Responses__r) from Case_Action_Association__c Where Case_Action__r.Name='QAA' and Case__c =: oCaseRec.Id order by LastModifiedDate DESC LIMIT 1];
                        //Check if a QAA Process is present
                        if(!lstCaseAssosciation.isEmpty())
                        {
                            bQMSFlag = true;
                        }
                        else
                        {
                            bCase = true;
                            bisQMSErrorExist = true;
                        }
                    }
                }
            }
            else
            {
                //TODO : logic for Medicare care in future sprints 
            }
            
            if(oCaseRec.Service_Center__c == Label.Service_Center_Louisville && (oCaseRec.Department__c == Label.Department_Grievance_and_Appeals || 
                 oCaseRec.Department__c ==  Label.Department_GA_Correspondence_Screening))
            {   
                if(Label.Case_Transfer_Service_Flag == '1' && bCheckMediCC)
                {
                    bNoQMSFlag = true;
                }    
            }
            
            //Upsertion of case
            if((NULL != oCaseRec && !bCase) || (NULL != oCaseRec && bNoQMSFlag) || (bSwitch_1414643 && NULL != oCaseRec && !bNoMHKCase) )
            {
                Lib_Security_LH_HUm.assertUpsert(oCaseRec);
                upsert oCaseRec;
                return oCaseRec;
            }
         return 'NOT_SUCCESS';
        }
        catch(DMLException ex){
            HUMExceptionHelper.logErrors(ex, 'CaseTransferSave_LD_HUM', 'saveCaseTransfer');
           	throw Lib_Security_LH_HUm.createException('Update/Insert Failed',ex);
        }
    }   
      /**
     * 	@Function -Method to assign values to case record fields depending upon different conditions
     *  @name assignFixedValuesToCase
     *  @param NA
     *  @return void
     */
    public static void assignFixedValuesToCase()
    {	
        Case oCaseRec = wCaseTranfer.oCaseRec;
        
        if(String.isBlank(oCaseRec.User__c))
        {
            Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
            if(mapServCenDeptWorkQueueSetup.containsKey(oCaseRec.Service_Center__c +'#' + oCaseRec.Department__c))
            {
                oWQS = mapServCenDeptWorkQueueSetup.get(oCaseRec.Service_Center__c + '#' + oCaseRec.Department__c);
                sSelectedWorkQueue = oWQS.Work_Queue_Name__c;
                sPublicGroupName= oWQS.Public_Group_Name__c;
            }
            
            sGrpQueueName = sSelectedWorkQueue?.replace('_',' ');
            Group oGrp = NULL;  
            oCaseRec.Classification_Type__c = oWQS.Classification_Type__r.Name;
            oCaseRec.Owner_Queue__c = sGrpQueueName; 
            
            if(oCaseRec.topic__c.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM))
            {
                oCaseRec.topic__c = ''; 
            }
            if(String.isBlank(ownerId) && NULL != oCaseRec)
            {
                if (NULL != oWQS.OwnerId)  
                {
                    oCaseRec.Case_Owner__c = sGrpQueueName;
                    oCaseRec.OwnerId = oWQS.OwnerId;
                }
            }
            else if(!String.isBlank(ownerId) && NULL != oCaseRec)
            {   
                oCaseRec.OwnerId = ownerId;
                oCaseRec.Case_Owner__c = ownerName;
            }        
        }
        else
        {
            oCaseRec.User__c = oCaseRec.User__c;
            oCaseRec.Status ='Pending - Transfer'; 
            oCaseRec.Transferred_From__c=oCaseRec.OwnerId;
            oCaseRec.Transferred_Date__c= System.Now(); 
        }  
    }
    
    //Wrapper that is being sent to UI
    public with sharing class CaseTransferWrapper {
        
        //Case Record
        public Case oCaseRec;
        public String caseComment;
        public String ownerId;
        public String ownerName;
        public Map <String, Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup;
        
        public CaseTransferWrapper(){}
    }
}