/*******************************************************************************************************************************************************
Apex Class Name  : METTask_DTO_HUM
Version          : 1.0
Created Date     : 06/01/2017
Function         : This class is wrapper class to serialize and show Datatable on METEnrollment Section on Case Edit Page
Test Class       : METTask_DTO_T_HUM
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Prasad Jandhyala             114623                 07/10/2017                  Original version
* Mohammed Noor                                       09/28/2017                  REQ - 329043 - Added variable for Row Number and default it to Zero.
* Mohammed Noor                142298                 10/26/2017                  REQ - 335062 - CR 5317 Custom Policy Member Search & MET Section Save Order
************************************************************************************************************************************************/
public class METTaskList_DTO_HUM {
    public List<METTask_DTO_HUM> listMETTaskDTO;
    
    public class METTask_DTO_HUM {
        public String sCreatedByName { get; set; }
        public String sCreatedByDate { get; set; }
        public String sCreatedById {get;set;}
        
        public String sLastModifiedByName { get; set; }
        public String sLastModifiedByDate{get; set;}
        public String sLastModifiedbyId {get; set;}
        
        public String sCancelledByName { get; set; }
        public String sCancelledByDate {get; set;}
        public String sCancelledById {get;set;}
        
        public String sTask { get; set; }        
        public String sAction { get; set; }
        public String sSource { get; set; }
        public String sStatus { get; set; }
        public String sTaskId { get; set; }
		public Integer sRowNum { get; set; }
		public String sCreatedDate_Sort {get; set;}
        
        public METTask_DTO_HUM()
        {
            sCreatedByName='';
            sCreatedByDate='';
            sCreatedById='';
            
            sLastModifiedByName='';
            sLastModifiedByDate='';
            sLastModifiedbyId='';
            
            sCancelledByDate ='';
            sCancelledByDate ='';
            sCancelledById ='';
            
            sTask= '';
            sAction= '';
            sSource= '';
            sStatus='';
            sTaskId='';
			sRowNum=0;
			sCreatedDate_Sort = '';			
        }
        
        public METTask_DTO_HUM(Task taskObj) {
            this.sCreatedByName = taskObj.createdby.name;
            this.sCreatedByDate = String.valueOf(DateTime.valueOf(taskObj.createddate));
            this.sCreatedById = taskObj.createdby.id;

            this.sLastModifiedByName = taskObj.Lastmodifiedby.name;
            this.sLastModifiedByDate= String.valueOf(DateTime.valueOf(taskObj.lastmodifieddate));
            this.sLastModifiedbyId = taskObj.Lastmodifiedby.id;
            
            this.sCancelledByName = !String.ISBLANK(taskObj.MET_CancelledByUser__r.name)?taskObj.MET_CancelledByUser__r.name:'';
            this.sCancelledByDate = !String.ISBLANK(String.valueOf(taskObj.MET_CancelledBydate__c))?String.valueOf(DateTime.valueOf(taskObj.MET_CancelledBydate__c)):'';
            this.sCancelledById = taskObj.MET_CancelledByUser__r.id;
            
            this.sTask = taskObj.MET_Task__r.Name;
            this.sAction = taskObj.MET_Action__r.Name;
            this.sSource = taskObj.MET_Source__r.Name;
            this.sStatus = taskObj.Status ;
            this.sTaskId = taskObj.id;
			this.sRowNum = 0;
			this.sCreatedDate_Sort = taskObj.CreatedDate_Sort__c;
        }
    }
    
    public METTaskList_DTO_HUM(List<Task> taskList) {
        METTask_DTO_HUM METTaskDTOInstance;
        List<METTask_DTO_HUM> metTaskDTOList = new List<METTask_DTO_HUM>();
        for(Task taskInstance: taskList)
        {
            METTaskDTOInstance = new METTask_DTO_HUM(taskInstance);
            metTaskDTOList.add(METTaskDTOInstance);
        }
        this.listMETTaskDTO = metTaskDTOList;
    }
    
    public METTaskList_DTO_HUM() { }
}