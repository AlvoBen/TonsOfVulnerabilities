/*******************************************************************************************************************************************************
Apex Class Name  : METTask_LDTO_HUM
Version          : 1.0
Function         : This class is wrapper class to serialize and show Datatable on METEnrollment Section on Case Edit Page
Modification Log :

* Developer Name            Review Number           Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Pooja Kumbhar                             	  12/22/2022                  Original version
************************************************************************************************************************************************/

public class METTaskList_LDTO_HUM {
    public List<METTask_LDTO_HUM> listMETTaskDTO;
    
    public class METTask_LDTO_HUM{
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
    public string isSourceEMMEMap {get;set;}
        
        public METTask_LDTO_HUM()
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
      isSourceEMMEMap = '';  
        }
        
        public METTask_LDTO_HUM(Task taskObj) {
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
    
    public METTaskList_LDTO_HUM(List<Task> taskList) {
        METTask_LDTO_HUM METTaskDTOInstance;
        List<METTask_LDTO_HUM> metTaskDTOList = new List<METTask_LDTO_HUM>();
        for(Task taskInstance: taskList)
        {
            METTaskDTOInstance = new METTask_LDTO_HUM(taskInstance);
            metTaskDTOList.add(METTaskDTOInstance);
        }
        this.listMETTaskDTO = metTaskDTOList;
    }
    
    public METTaskList_LDTO_HUM() { }
}