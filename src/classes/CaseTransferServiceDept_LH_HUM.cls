/*******************************************************************************************************************************
Apex Class Name : CaseTransferServiceDept_LH_HUM
Version         : 1.0
Created On      : 02/24/2022
Function        : Modal for service center, department, Topic
Test Class      : CaseTransfer_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											02/24/2022                 Original Version
*     2.0					 Pooja Kumbhar											02/03/2023		   US 4180008 - T1PRJ0170850- Lightning- Case management- Change Case Owner Missing Functionalities					
*     3.0					 Pooja Kumbhar											02/13/2023         DF 7152 Defect Fix
*********************************************************************************************************************************/
public with sharing class CaseTransferServiceDept_LH_HUM {
   
    //Collection Variables
    @AuraEnabled public Map<String, List<String>> lstServiceCenter;
    @AuraEnabled public Map<String,Work_Queue_Setup__c> mapUniqueServCenDeptQueueSetup;
    @AuraEnabled public List<classificationIntentModel.OptionModel> lstTransferReason;
    @AuraEnabled public List<classificationIntentModel.OptionModel> lstTopics;
    @AuraEnabled public List<classificationIntentModel.OptionModel> lstPriority;
    @AuraEnabled public List<classificationIntentModel.OptionModel> lstEscIndicator;

    //String variables
    @AuraEnabled public String defaultTransfer = GLOBAL_CONSTANT_HUM.TRANSFER_PER_PROCESS;
    @AuraEnabled public String defaultService;
    @AuraEnabled public String defaultDepartment;
    @AuraEnabled public String defaultTopic = 'Select a Topic';
    @AuraEnabled public String defaultEscIndicator = '--None--';
    @AuraEnabled public String ownerObjName;
    @AuraEnabled public String currentUserID;
    
    //Will work only when reassign to creator and sender is clicked
    @AuraEnabled public String reassignCaseOwner='';
    @AuraEnabled public String reassignCaseId;
    
    @AuraEnabled public Boolean  isCreatorDisabled;
    @AuraEnabled public Boolean  isSenderDisabled;
	@AuraEnabled public Boolean  isSecuritySenderDisabled;

    @AuraEnabled public Boolean bIsETLUser;
    @AuraEnabled public Boolean bcaseTransferAssistInfo;
    
    //Boolean variables
    public boolean bSwitch_1414643 = HUMUtilityHelper.isCRMFunctionalityON('1414643');
    
        /**
* Parameterized constructor which take different parameters and populates values in the variables. It also assigns default values of Transfer, service center and department fields
*  @param Case, Map<String, List<String>>, Map<String,Work_Queue_Setup__c>,List<String>
*  @return CaseDataWrapper
*/
    public CaseTransferServiceDept_LH_HUM(Case oCaseRecord,Map<String, List<String>> lstServiceCenter, List<String> lstTransferReason)
    {
        this.lstServiceCenter=lstServiceCenter;
        this.lstTransferReason=(List<classificationIntentModel.OptionModel>) prepareComboBoxValues(lstTransferReason);
        populateDefaultServiceDept(oCaseRecord);
        
    }
        /**
* 	This method will prepopulate service center and department based on specific conditions
*  @name populateDefaultServiceDept
*  @param Case 
*  @return void
*/
    public void populateDefaultServiceDept(Case oCaseRecord)
    {
       	 if(this.bSwitch_1414643 && string.isNotBlank(oCaseRecord.Type) && oCaseRecord.Type.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE))
       	{
            this.defaultService = GLOBAL_CONSTANT_HUM.Service_Center_Provider;
            this.defaultDepartment = GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK;
        }
    }
    
/**
* 	This method will convert a list of string into key value pair for usage as comboBox on LWC
*  @name transferComboBox
*  @param List<String> 
*  @return Object
*/
    public Object prepareComboBoxValues(List<String> lstOfStrings)
    {	
        classificationIntentModel classificatioModel= new classificationIntentModel();
        return classificatioModel.collectionToOption(lstOfStrings);
    }
}