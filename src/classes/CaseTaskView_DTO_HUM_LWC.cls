/************************************************************************************************************************
Apex class Name    : CaseTaskView_DTO_HUM_LWC
Version            : 1.0
Function           : Wrapper to hold the structure of data that will be disaplyed on Task List View page in lwc
Created On         : Dec 11 2021
Test Class         : Hum_CaseTasks_T_LWC

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                

*   Vijaya Lakshmi Tummala                 			12/11/2021          Original Version
***************************************************************************************************************************/
public with sharing class CaseTaskView_DTO_HUM_LWC
{
    /**
    * Constructor of the Class
    * <p>    
    * @return     NA
    * @param      String : Type of Record (Case/Task)
    * @param      String : Case or Task Id of Record (Case/Task)
    * @param      String : Case or Task Number of Record (Case/Task)
    * @param      String : Open Work Items of Record (Case/Task)
    * @param      String : Status of Record (Case/Task)
    * @param      String : Priority of Record (Case/Task)
    * @param      String : Interacting With Id for Case . For Task it lookup to the Case associated to it.
    * @param      String : Interacting With for Case . For Task it lookup to the Case associated to it.
    * @param      String : Interacting About for Case . For Task it lookup to the Case associated to it.
    * @param      String : Classification of Record (Case/Task)
    * @param      String : Intent of Record (Case/Task)
    * @param      String : Queue of Record (Case/Task)
    * @param      String : View of Record (Case/Task)
    * @param      String : Due Date of Record (Case/Task)
    * @param      Decimal : Age of Record (Case/Task)
    * @param      Decimal : Age of Case.For Task it lookup to the Case associated to it.
    * @param      Decimal : Reopen Age of Record (Case/Task)
    * @param      string : Created Date of Record (Case/Task)
    * @param      Boolean : Identifier if the due date of the record is in past.
    * @param      String : Identifier to check if the case is Assigned to a user or to Queue.
    * @param      String : Case Milestone remaining SLA value.
    * @param      String : Case Task Comment.
    */
    public  CaseTaskView_DTO_HUM_LWC(String Type ,String CaseTaskId , String CaseTaskNumbernComment , String OpenWorkItems ,String Status , String Priority , 
        String InteractingWithId , String InteractingWith , String InteractingAbout ,String Classification , String Intent , String CaseTaskQueue , 
        String CaseTaskView ,String DueDate , decimal WorkItemAge , decimal CaseAge , decimal ReopenWorkItemAge ,String receiveddate, String CreatedDate , Boolean IsDuePast , 
        String Assigned , String SortOpened, String sRemainingSLA, Boolean TransferFail, String Policy, String RecordTypeId, string Account, 
        Boolean GuidedFlag, Map<string,string> CaseData, Boolean General, Boolean Dental, Boolean Medical)
    {
        sFilterType = Type;
        sCaseTaskId = CaseTaskId;
        sCaseTaskNumber = CaseTaskNumbernComment.split('--')[0];
        sOpenWorkItems = OpenWorkItems;
        sStatus = Status;
        sPriority = Priority;
        sInteractingWithId = InteractingWithId;
        sInteractingWith = InteractingWith;
        sInteractingAbout = InteractingAbout;
        sClassification = Classification;
        sIntent = Intent;
        sCaseTaskQueue = CaseTaskQueue;
        sCaseTaskView = CaseTaskView;
        sConcatenateViewAndQueueName = sCaseTaskView+' ('+sCaseTaskQueue+')';
        dDueDate = DueDate;
        decWorkItemAge = WorkItemAge;
        decCaseAge = CaseAge;
        decReopenWorkItemAge = ReopenWorkItemAge;
        sreceiveddate = receiveddate;
        dtCreatedDate = CreatedDate;
        bIsDuePast = IsDuePast;
        sAssigned = Assigned;
        sSortOpened = SortOpened;
        this.sRemainingSLA = sRemainingSLA;
        sRecordTypeId = RecordTypeId;
        sPolicy = Policy; 
        sAccount = Account;
        if(CaseData != Null && !CaseData.isEmpty())
        {
            sClassificationType = CaseData.get('ClassificationType');
            sComplaint = CaseData.get('Complaint');
            sProduct = CaseData.get('Product');
            sCreatedQueue = CaseData.get('Queue');
            sAutoRouteStatus = CaseData.get('AutoRouteStatus');
            sOwner = CaseData.get('Owner');
            sOrigin = CaseData.get('Origin');
            sPolicyMember = CaseData.get('PolicyMember');
            sInteractingAboutType = CaseData.get('InteractingAboutType');
            sCallBenefitCategory = CaseData.get('CallBenefitCategory');
            sInteractingWithName = CaseData.get('InteractingWithName');
            sGARightsGiven = CaseData.get('GARightsGiven');
        }
        bGuidedFlag = GuidedFlag;
        bGeneral = General;
        bDental = Dental;
        bMedical = Medical;
        bTransferFail = TransferFail;
        sCaseTaskComment = CaseTaskNumbernComment.split('--')[1];
    }
    @AuraEnabled
    public String   sFilterType{get; set;}
	@AuraEnabled
    public String   sCaseTaskId{get; set;}
	@AuraEnabled	
    public String   sCaseTaskNumber {get; set;}  
	@AuraEnabled
    public String   sOpenWorkItems {get; set;}  
	@AuraEnabled
    public String   sStatus{get; set;}
	@AuraEnabled
    public String   sPriority {get; set;}
	@AuraEnabled
    public String   sInteractingWithId {get;set;}
	@AuraEnabled
    public String   sInteractingWith {get;set;}
	@AuraEnabled
    public String   sInteractingAbout {get; set;}
	@AuraEnabled
    public String   sClassification{get; set;}
	@AuraEnabled
    public String   sIntent{get; set;}
	@AuraEnabled
    public String   sCaseTaskQueue{get; set;}
	@AuraEnabled
    public String   sCaseTaskView{get; set;}
	@AuraEnabled	
    public String   dDueDate {get; set;}
	@AuraEnabled
    public decimal  decWorkItemAge {get; set;}
	@AuraEnabled
    public decimal  decCaseAge {get; set;}
	@AuraEnabled
    public decimal  decReopenWorkItemAge{get;set;}
	@AuraEnabled
    public string   dtCreatedDate{get;set;}
	@AuraEnabled
    public Boolean  bIsDuePast{get;set;}
	@AuraEnabled
    public String   sAssigned {get; set;} 
	@AuraEnabled
    public String   sSortOpened {get; set;}
	@AuraEnabled	
    public String   sConcatenateViewAndQueueName {get; set;}
	@AuraEnabled
    public String   sRemainingSLA {get; set;}
	@AuraEnabled
    public String   sreceiveddate {get; set;}
	@AuraEnabled
    public String   sOrigin{get; set;}
	@AuraEnabled	
    public String   sPolicyMember {get; set;}  
	@AuraEnabled
    public String   sRecordTypeId {get; set;}
	@AuraEnabled
    public String   sPolicy {get; set;}
	@AuraEnabled
    public String   sAccount {get; set;}
	@AuraEnabled
    public String   sClassificationType {get; set;}
	@AuraEnabled
    public String   sComplaint {get; set;}
	@AuraEnabled
    public String   sInteractingAboutType {get; set;}
	@AuraEnabled
    public String   sCallBenefitCategory{get; set;} 
	@AuraEnabled
    public String   sProduct {get; set;}
	@AuraEnabled
    public String   sCreatedQueue {get; set;}
	@AuraEnabled
    public String   sAutoRouteStatus{get; set;}
	@AuraEnabled
    public String   sOwner{get; set;}
	@AuraEnabled
    public Boolean bGuidedFlag{get; set;}
	@AuraEnabled
    public Boolean bGeneral{get; set;}
	@AuraEnabled
    public Boolean bDental{get; set;}
	@AuraEnabled
    public Boolean bMedical{get; set;}
	@AuraEnabled
    public String sInteractingWithName{get; set;}
	@AuraEnabled
    public String sGARightsGiven{get; set;}
	@AuraEnabled
    public Boolean bTransferFail{get; set;}
    @AuraEnabled
    public String sCaseTaskComment{get; set;}
}