/************************************************************************************************************************************************
Apex Class Name  : DevelopmentTaskController_C_HUM
Version          : 1.0
Created Date     : 2018
Function         : It fetches data for populating burndown chart from requirement and development history objects.
Test Class       : 
*************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-------------------------------------------------------------------------------------------------------------------------------------------------
* Santhosh Ganji                                       11/01/2018                   Original Version
*************************************************************************************************************************************************/
public with sharing class DevlopmentTaskController_C_HUM {
  
    public string SprintId{get;set;}
    public Sprint__c SprintData {get;set;}
    Public List<Requirement__c> SprintReqIds = new List<Requirement__c>();
    Public List<Story_Tasks__c> SprintDevTasks = new List<Story_Tasks__c>();
    Public List<Id> Ids = new List<Id>();
    Public List<Story_Tasks__History> TasksHistory = new List<Story_Tasks__History>();
    Public List<DevlopmentTaskController_DTO_HUM> DailyBurnDownData{get;set;}
        
    public Integer numberOfDays {get;set;}
    public Decimal LoggedHrsPerDay {get;set;}
    public Decimal TotalTasksRemainingHrs {get;set;}
    Public Decimal TotalEstHrs{get;set;}
    Public Decimal TotalHrs{get;set;}
    public Decimal ChartDataPoints {get;set;}
    public Integer SprintDays{get;set;}
    public Integer Hours{get;set;}
    public boolean weekendflag {get;set;}
    public Decimal TotalTaskHours{get;set;}
        
    public DevlopmentTaskController_C_HUM(ApexPages.StandardController controller)
    { 
      weekendflag = true;
      Hours = 8;
    }
    
    public List<DevlopmentTaskController_DTO_HUM> getListWrapper(){
        
        SprintId = ApexPages.currentPage().getParameters().get('Id');
        
        if(SprintId != null)
             SprintData = [SELECT Id, Name,Sprint_Start_Date__c,Sprint_End_Date__c,Total_Story_Points__c,Total_Tasks_Logged_Hours__c,Total_Tasks_Remaining_Hours__c,Team__c,Sprint_Day__c FROM Sprint__c where Id = :SprintId limit 1];
        
        if(SprintData.Name != null || SprintData.Name != '')
               SprintReqIds = [SELECT Id FROM Requirement__c where Assigned_Sprint__c =:SprintData.Id];
       
        if(SprintReqIds.size() > 0 && !SprintReqIds.isEmpty())
        {
          
          for(Requirement__c Req:SprintReqIds)
           {        
             Ids.Add(Req.Id);
           }
          
          SprintDevTasks = [SELECT Id, Name, Estimate_Hours__c, Logged_Hours__c, Requirement__c, CreatedDate FROM Story_Tasks__c where Requirement__c IN:Ids];
        
        }
        
        Date SprintstartDate = SprintData.Sprint_Start_Date__c;
        Date startDate = SprintData.Sprint_Start_Date__c;
        Date EndDate = SprintData.Sprint_End_Date__c;
        Date CurrentDate= System.today();
        numberOfDays = startDate.daysBetween(EndDate); 
        TotalTaskHours = 0; 
        
        if(numberOfDays != null && numberOfDays > 0 && numberOfDays <= 7)
            {                 
                SprintDays = 5;
            }
            else if(numberOfDays != null && numberOfDays > 0 && numberOfDays > 7){
                 SprintDays = 10;
            }    
        
        Ids = new List<Id>();
        if(SprintDevTasks.size() > 0 && !SprintDevTasks.isEmpty())
        {
        
          for(Story_Tasks__c DevTasks:SprintDevTasks)
          {        
             TotalTaskHours = TotalTaskHours + DevTasks.Estimate_Hours__c;
             Ids.Add(DevTasks.Id);
          }
        
          TasksHistory = [SELECT Id, ParentId, CreatedDate, Field, OldValue, NewValue FROM Story_Tasks__History where (Field = 'Logged_Hours__c' OR Field = 'Remaining_Hours__c') AND ParentId=: Ids];
        
        }
        
        TotalHrs = TotalTaskHours;
        TotalTasksRemainingHrs = TotalTaskHours;
        
        
        DailyBurnDownData = new List<DevlopmentTaskController_DTO_HUM>();
        
        for(Integer Start = 1;startDate <= CurrentDate && Start <= SprintDays ;Start++)
        {   
            DevlopmentTaskController_DTO_HUM BurnDownData = new DevlopmentTaskController_DTO_HUM();  
            LoggedHrsPerDay = 0;
            
            TotalEstHrs= TotalTaskHours;
            
            if(TotalHrs  != null && TotalEstHrs != null)
            {
              if(SprintDays != null && SprintDays > 0)
              {                 
                TotalEstHrs = TotalEstHrs - (start-1)*(TotalEstHrs/SprintDays);
                ChartDataPoints = TotalHrs - TotalEstHrs; 
              }                  
            }
            
           if(Start > 1)
           { 
            if(TasksHistory.size() > 0 && !TasksHistory.isEmpty())
            {
             for(Story_Tasks__History Tasks:TasksHistory)
             {
               if(Tasks.CreatedDate > startDate && Tasks.CreatedDate < (startDate.addDays(1)))
               {
                 if(Tasks.Field == 'Logged_Hours__c')
                 { 
                   if(Tasks.OldValue == null)
                   {
                      LoggedHrsPerDay = LoggedHrsPerDay + (Integer.valueof(Tasks.NewValue) - 0);
                      
                   }
                   else if(Tasks.OldValue != null && Tasks.NewValue == null)
                   {
                      LoggedHrsPerDay = LoggedHrsPerDay + (0 - Integer.valueof(Tasks.OldValue));
                      
                   }
                   else{
                      LoggedHrsPerDay = LoggedHrsPerDay + (Integer.valueof(Tasks.NewValue) - Integer.valueof(Tasks.OldValue));
                      
                   }
                 }
               }
             }
            }
            else if(TasksHistory.isEmpty() && TasksHistory.size() == 0)
            {
               for(Story_Tasks__c story : SprintDevTasks)
               {
                  if(story.CreatedDate > startDate && story.CreatedDate < (startDate.addDays(1)))
                  {
                    if(story.Logged_Hours__c != null)
                    {
                      LoggedHrsPerDay = LoggedHrsPerDay + story.Logged_Hours__c;
                    }
                  }
               }
             }
           }
           
          
          Datetime newdate = Datetime.newInstance(startDate,Time.newInstance(0, 0, 0, 0));
            
          if(start >= 2 && ((newdate.format('E') == 'Sat')||(newdate.format('E') == 'Sun')))  
           {  
              start--;
              weekendflag = false;
           }else
             {
              weekendflag = true;
             }
           
         
         if(weekendflag){
            
            
            TotalTasksRemainingHrs = TotalTasksRemainingHrs - LoggedHrsPerDay;
            
            BurnDownData.Days = 'Day ' + Start;
            BurnDownData.IdealHrs = TotalEstHrs;
            BurnDownData.LoggedHrs = LoggedHrsPerDay;
            BurnDownData.RemainingHrs = TotalTasksRemainingHrs;
            BurnDownData.ChartDataPoints = ChartDataPoints;  
            DailyBurnDownData.Add(BurnDownData);
         }  
            startDate = startDate.addDays(1);
       }
            return DailyBurnDownData;   
    }
    
}