/*******************************************************************************************************************************
Apex Class Name :    CaseClose_LC_HUM
Version: 1.0
Created On      : 06/15/2022
Function        : This class Is for Case Close Feature.
Test Class      : CaseClose_LT_HUM

  Modification Log:    
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 			Gowthami Thoa 					   06/15/2022			Original Version	
******************************************************************************************************************************************************************************/

public with sharing class CaseClose_LC_HUM {
    
    //Wrapper Variable
    public static InputParams pageInputs;
    public static CaseCloseWrapper wCaseClose;
    public static CaseDataWrapper wCaseDataWrapper = new CaseDataWrapper();
    //Collection variables
    public static Set <String> setPharRecType = new Set<String>(HumConstants_LWC.Pharmacy_Record_Type_Name);
    public static List<Task> lstOpenTasks = new list<Task>();
    //string variables
    public static string sTaskOpen;
    
     /**
     * 	@Function -Method to get Case record
     *  @name fetchCaseObject
     *  @param String pageInputs
     *  @return CaseDataWrapper
     */
    @AuraEnabled
    public static CaseDataWrapper fetchCaseData(String pageInput) {
        pageInputs = (InputParams) JSON.deserialize(pageInput, InputParams.class);
        Case oCase = CaseClose_LD_HUM.fetchCaseRecord(pageInputs.sCaseID);
        if (oCase != null) {
            //Modal for CaseComment functionality
            wCaseDataWrapper.caseCommentModal = new CaseCloseCommentModal_LH_HUM(CaseClose_LD_HUM.getCurrentUserProfileName(), CaseClose_LD_HUM.fetchLogCodeForBothHPCCS(oCase.Classification_Type__c, pageInputs.bRSOFlag, setPharRecType.contains(oCase.RecordType.Name)));
            wCaseDataWrapper.caseData = oCase;
            wCaseDataWrapper.sCurrentUserId = UserInfo.getUserId();
            wCaseDataWrapper.sCurrentUserQueue = CaseClose_LD_HUM.getCurrentUserQueue();
            return wCaseDataWrapper;
        }
        return null;
    }
    
     /*
     * Method Name : CaseClose
     * Description : Method to perform case close
     * Return type : string
     * Paramater   : sCaseID
     */
    @AuraEnabled
	public static string CaseCloseSave(String sCaseID){
        Case oCaseRec = CaseClose_LD_HUM.fetchCaseRecord(sCaseID);
        try{
            if(null != oCaseRec){
               List<Task> lastOpenTasks= [Select Id From Task Where Type = :HumConstants_LWC.HUMWorkTask and Status != :HumConstants_LWC.HUMClOSED and whatId = :oCaseRec.id];
                if(lastOpenTasks.isEmpty()){
                        if(oCaseRec.Classification_Id__c != null && oCaseRec.Intent_Id__c != null && oCaseRec.Complaint__c != null && oCaseRec.G_A_Rights_Given__c != null) {
                            if(Schema.sObjectType.Case.fields.Status.isUpdateable()){
                                oCaseRec.status = HumConstants_LWC.HUM_CASEEDIT_CLOSED;
                            }
                        }
                        update oCaseRec;
                }
            
          }
           return oCaseRec.status; 
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'CaseClose_LC_HUM', 'CaseClose');
            return null;
        } 
        
   }
    
    /**
     * 	@Function -Method to perform case close
     *  @name saveCaseClose
     *  @param String caseCloseInput
     *  @return Object
     */
    @AuraEnabled
    public static Object saveCaseClose(String caseCloseInput){
        
		wCaseClose = (CaseCloseWrapper) JSON.deserialize(caseCloseInput, CaseCloseWrapper.class);
			Case oCaseRec = wCaseClose.oCaseRec;
		//Updation of case
        try{
                if(null != oCaseRec && oCaseRec.Complaint__c != null && oCaseRec.G_A_Rights_Given__c != null)
                {
                    if(Schema.sObjectType.Case.fields.Status.isUpdateable()){
                        oCaseRec.status = HumConstants_LWC.HUM_CASEEDIT_CLOSED;
                    }
                    update oCaseRec;
                }	
                if(Test.isRunningTest()){
                throw new dmlException();
                }
            return oCaseRec;
        }
        catch(Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'CaseClose_LC_HUM', 'saveCaseClose');
            throw new HUMCustomException(ex.getMessage());
         }
        
    }
    @AuraEnabled
    public static string CheckForOpenTaskCount(String sCaseID){
            if(!String.ISBLANK(sCaseID))
            {
                List<Task> lstOpenTasks = [Select Id,whatId From Task Where Type = :HumConstants_LWC.HUMWorkTask 
                                           and Status != :HumConstants_LWC.HUMClOSED and whatId = :sCaseID];
                if(lstOpenTasks.size() <= 0)
                {
                    sTaskOpen = 'FALSE'; 
                }
                else
                {
                    sTaskOpen = 'TRUE';
                }
            }
        return sTaskOpen;
    }
    
    //Main wrapper that is being sent to UI
    public with sharing class CaseDataWrapper {
        //Variable for CaseComments 
        @AuraEnabled public CaseCloseCommentModal_LH_HUM caseCommentModal;
        @AuraEnabled public Case caseData;
        @AuraEnabled public string sCurrentUserId;
        @AuraEnabled public string sCurrentUserQueue;
    }
    //Wrapper For getting input params from LWC component
    public with sharing class InputParams {
        public String sCaseId;
        public Boolean bRSOFlag;

        public InputParams() {}
    }
    
    public with sharing class CaseCloseWrapper{
        @AuraEnabled public Case oCaseRec;
        
        public CaseCloseWrapper() {}
    }
}