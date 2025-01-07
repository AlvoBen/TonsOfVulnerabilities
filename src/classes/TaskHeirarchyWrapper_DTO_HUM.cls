/************************************************************************************************************************************************
Apex Class Name  : TaskHeirarchyWrapper_DTO_HUM
Version          : 1.0
Function         : Wrapper class for task heirarchy table to send the data to the callback function.
Test Class       : TaskeHeirarchyCase_T_HUM
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Avinash Choubey                                   09/15/2016                    Original Version
*************************************************************************************************************************************************/

global class TaskHeirarchyWrapper_DTO_HUM
    { 
        
        public String TaskNumber ='';
        public String TaskType ='';
        public String  Duedate ='';
        public String Status ='';
        public String Priority ='';
        public Integer TaskAge = 0 ;
        public String DateTimeOpened ='';
        public String DateTimeClosed ='';
        public String TaskOwner   ='';
        public String OwnerQueue  ='';
        public String taskID  ='';
        public Integer taskLevel  = 0 ;
        public Integer parentSequence  = 0;
        public String CredentialingTask ='';
        public String TaskCompletionDate = '';
        
    /**
    * Returns None
    * <p>
    * Constructor for the class , Initialize variables
    * 
    * @param  ID sTaskID
    * @param  Integer iTaskLevel
    * @param  Integer iParentSequence.
    * @return  None
    * @see
    */
        public TaskHeirarchyWrapper_DTO_HUM(ID sTaskID,Integer iTaskLevel,Integer iParentSequence )
        {
          
           this.taskLevel = iTaskLevel;
           this.taskId = sTaskID;
           this.parentSequence =iParentSequence;
           
        }
        
    }