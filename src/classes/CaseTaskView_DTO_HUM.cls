/************************************************************************************************************************
Apex class Name    : CaseTaskView_DTO_HUM
Version            : 1.0
Function           : Wrapper to hold the structure of data that will be disaplyed on Task List View page 
Created On         : October 10 2016
Test Class         : CaseTaskView_T_HUM

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Rajesh Keswani                44250            10/10/2016          Original Version
*   Prasanthi Kandula             199807           03/15/2018          REQ - 95977 Multiple Queue, View selections on Case/Task Page
*   Subhamay Mandal                                04/24/2019          REQ - 396217 Quality - MIR - Ability for CSR to see remaining SLA field on the Case/Tasks tab (RF)
*   Sumanth Nardela                                03/12/2020          User Story - 731476 MF 9 Case & Tasks Tab - Image Received Date Column
*   Prudhvi Pamarthi                               06/12/2020          US#800050 Close or Cancel two or more cases at once with case comments within CRM Service
*   Prudhvi Pamarthi                               07/23/2020          US#1045266 Transfer two or more cases at once with case comments within CRM Service
***************************************************************************************************************************/
public with sharing class CaseTaskView_DTO_HUM 
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
    */
    public  CaseTaskView_DTO_HUM(String Type ,String CaseTaskId , String CaseTaskNumber , String OpenWorkItems ,String Status , String Priority , 
        String InteractingWithId , String InteractingWith , String InteractingAbout ,String Classification , String Intent , String CaseTaskQueue , 
        String CaseTaskView ,String DueDate , decimal WorkItemAge , decimal CaseAge , decimal ReopenWorkItemAge ,String receiveddate, String CreatedDate , Boolean IsDuePast , 
        String Assigned , String SortOpened, String sRemainingSLA, Boolean TransferFail, String Policy, String RecordTypeId, string Account, 
        Boolean GuidedFlag, Map<string,string> CaseData, Boolean General, Boolean Dental, Boolean Medical)
    {
        sFilterType = Type;
        sCaseTaskId = CaseTaskId;
        sCaseTaskNumber = CaseTaskNumber;
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
    }
    
    public String   sFilterType{get; set;} 
    public String   sCaseTaskId{get; set;} 
    public String   sCaseTaskNumber {get; set;}  
    public String   sOpenWorkItems {get; set;}  
    public String   sStatus{get; set;}
    public String   sPriority {get; set;}
    public String   sInteractingWithId {get;set;}
    public String   sInteractingWith {get;set;}
    public String   sInteractingAbout {get; set;}
    public String   sClassification{get; set;}
    public String   sIntent{get; set;}
    public String   sCaseTaskQueue{get; set;}
    public String   sCaseTaskView{get; set;}
    public String   dDueDate {get; set;}
    public decimal  decWorkItemAge {get; set;}
    public decimal  decCaseAge {get; set;}
    public decimal  decReopenWorkItemAge{get;set;}
    public string   dtCreatedDate{get;set;}
    public Boolean  bIsDuePast{get;set;}
    public String   sAssigned {get; set;} 
    public String   sSortOpened {get; set;}  
    public String   sConcatenateViewAndQueueName {get; set;}
    public String   sRemainingSLA {get; set;}
    public String   sreceiveddate {get; set;}
    public String   sOrigin{get; set;} 
    public String   sPolicyMember {get; set;}  
    public String   sRecordTypeId {get; set;}
    public String   sPolicy {get; set;}
    public String   sAccount {get; set;}
    public String   sClassificationType {get; set;}
    public String   sComplaint {get; set;}
    public String   sInteractingAboutType {get; set;}
    public String   sCallBenefitCategory{get; set;} 
    public String   sProduct {get; set;}
    public String   sCreatedQueue {get; set;}
    public String   sAutoRouteStatus{get; set;}
    public String   sOwner{get; set;}
    public Boolean bGuidedFlag{get; set;}
    public Boolean bGeneral{get; set;}
    public Boolean bDental{get; set;}
    public Boolean bMedical{get; set;}
    public String sInteractingWithName{get; set;}
    public String sGARightsGiven{get; set;}
    public Boolean bTransferFail{get; set;}
}