public with sharing class EditUseCaseExtension {
     Use_Case__c usecaseobj;
     public EditUseCaseExtension (ApexPages.StandardController controller) {
              usecaseobj=(Use_Case__c)controller.getRecord();
    }
  
  //No of Steps
     public Boolean noStepCount{ get; set; } 
    
    
    //--------------------- TestCase Add-------------------------------------------
    //Getter/Setter for Test Case valueshttps://na6.salesforce.com/s.gif
     Use_Case__c usecase = new Use_Case__c();
     public Use_Case__c getusecase()
     {
     return usecase ;
     }
     public void setusecase(Use_Case__c usecaseacc)
     {
     usecase.Name = usecaseacc.Name;
/*     usecase.Type__c = usecaseacc.Type__c;
     usecase.Status__c  = usecaseacc.Status__c ;
     usecase.Assigned_To__c = usecaseacc.Assigned_To__c;*/
//     usecase.Use_Case__c = usecase.Use_Case__c;
     }
    
  
  //--------------------- TestCaseStep Add/Remove -------------------------------------------
  @TestVisible
    List<useCaseWrapper> usecasestepList = new List<useCaseWrapper>();
    
    List<Use_Case_Step__c> selectedusecasesteps = new List<Use_Case_Step__c>();
        
    public List<useCaseWrapper> getUseCaseSteps()
    {   
        usecasestepList.clear();
        for(Use_Case_Step__c a : [select Id , Step_Number__c ,Step_Description__c ,Expected_Result__c ,Use_Case__c from Use_Case_Step__c where  
            Use_Case__c= :usecaseobj.Id order by Step_Number__c])
        usecasestepList.add(new useCaseWrapper(a));
        return usecasestepList;
    }
    
    public PageReference getSelected()
    {
        selectedusecasesteps.clear();
        for(useCaseWrapper useCsWrTemp : usecasestepList )
        if(useCsWrTemp.selected == true)
        selectedusecasesteps.add(useCsWrTemp.useCsWr);
        return null;
    }
    
    public List<Use_Case_Step__c> GetSelectedAccounts()
    {
        if(selectedusecasesteps.size()>0)
        return selectedusecasesteps;
        else
        return null;
    }    
    
    public class useCaseWrapper
    {
        public Use_Case_Step__c useCsWr{get; set;}
        public Boolean selected {get; set;}
        public useCaseWrapper(Use_Case_Step__c a)
        {
            useCsWr = a;
            selected = false;
        }
    }
    
    //------------------------------- Add new UseCaseStep Record ----------------------------------------------------
    //Getter/Setter for Use Step values
     Use_Case_Step__c obj = new Use_Case_Step__c();
     public Use_Case_Step__c getobj()
     {
     return obj;
     }
     public void setobj(Use_Case_Step__c objcc)
     {
     obj.Step_Number__c = objcc.Step_Number__c;
     obj.Step_Description__c = objcc.Step_Description__c;
     obj.Expected_Result__c = objcc.Expected_Result__c;
     }
  
    
    
    public PageReference AddUseCaseStep() {
    
         if(obj.Step_Number__c==0)
          {
           ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,'Step Number should not be Zero.'));
           return null;
         }
       
        else if(DuplicateStepNumber_AddClick())
        {
         ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,'Step Number already Exists.'));
         return null;
        }
        else
        {
            Use_Case_Step__c newcasecasestep = new Use_Case_Step__c();
            newcasecasestep.Step_Number__c = obj.Step_Number__c;
            newcasecasestep.Step_Description__c = obj.Step_Description__c;
            newcasecasestep.Expected_Result__c = obj.Expected_Result__c;
            newcasecasestep.Use_Case__c= usecaseobj.Id;
            if(Use_Case_Step__c.SObjectType.getDescribe().isCreateable())
                insert newcasecasestep;
            obj.Step_Number__c = obj.Step_Number__c!=null?obj.Step_Number__c+1:1;
            obj.Step_Description__c = null;
            obj.Expected_Result__c = null;
            return null;
        }
    }
    //Function : Cancel Button Click
    public PageReference AddStep_Cancel() {
    obj.Step_Number__c = null;
    obj.Step_Description__c = null;
    obj.Expected_Result__c = null;
    return null;
    }
    
    //--------------------------------- Remove Selected UseCaseStep Record ---------------------------------------
        
     List <Use_Case_Step__c>  removeUseCaseStepList = new List <Use_Case_Step__c> ();
     List <Use_Case_Step__c>  tempUseCaseStepList = new List <Use_Case_Step__c> ();
     
     
     public pagereference removeUseCaseStep() {
       System.debug('--Remove Called.--');
       for(useCaseWrapper useCsWrTemp : usecasestepList)
        if(useCsWrTemp.selected == true)
        removeUseCaseStepList.add(useCsWrTemp.useCsWr);
        if(removeUseCaseStepList.size()==usecasestepList.size())
        {
        ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,'Atleast One Step should be associated with Test Case.'));
        removeUseCaseStepList.clear();
        return null;
        }
        else
        {
        if (Use_Case_Step__c.sObjectType.getDescribe().isDeletable())
        {
            Delete  removeUseCaseStepList;
        }
        removeUseCaseStepList.clear();
        return null;
        }
        
         }
        
        
     //--------------------------------- Update Selected TestCaseStep Record ---------------------------------------
    
     List <Use_Case_Step__c> updatedTestStepList = new List <Use_Case_Step__c> ();
     public PageReference updateUseCaseStep() {
     if(ZeroStepNumber())
     {
     ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,'Step Value should not be Zero.'));
     return null;
     }
     else if (DuplicateStepNumber())
     {
     ApexPages.addmessage(new ApexPages.message(ApexPages.severity.ERROR,'Step Value should not be Duplicate.'));
     return null;
     }
     else
     { 
     System.debug('usecasestepList-->>'+usecasestepList);
     for(useCaseWrapper useCsWrTemp : usecasestepList)
        if(useCsWrTemp.selected == true)
        updatedTestStepList.add(useCsWrTemp.useCsWr);
        Update updatedTestStepList;
        System.debug('updatedTestStepList-->>'+updatedTestStepList);
        updatedTestStepList.clear();
        return null;
     }
}

//--------------------------------- Supporting Methods --------------------------------
    Boolean flag = false ;
    List <Use_Case_Step__c> tempList = new List <Use_Case_Step__c>();
    
    
    Public boolean ZeroStepNumber()
    {
    for(useCaseWrapper useCsWrTemp : usecasestepList)
    if(useCsWrTemp.useCsWr.step_Number__c == 0)
    {
    flag = true;
    break;
    }
    tempList.clear(); 
    return flag;
    }
   
   
   Public boolean DuplicateStepNumber()
    {
    for(useCaseWrapper useCsWrTemp : usecasestepList)
        tempList.add(useCsWrTemp.useCsWr);
    flag=false;
    for(Integer i = 0 ; i< tempList.size();i++)
    {
    for(Integer j = i+1 ; j< tempList.size();j++)
     {
         if(tempList[i].step_number__c==tempList[j].step_number__c)
         flag = true;
         break;
     }
    }
   tempList.clear();
    return flag;
   }
   
   
    Public boolean DuplicateStepNumber_AddClick()
    {
    flag=false;
    for(useCaseWrapper useCsWrTemp : usecasestepList)
        tempList.add(useCsWrTemp.useCsWr);
    system.debug('eeeeeeeeeeeeeeeeeeeee>' +tempList);
    for(Integer i = 0 ; i< tempList.size();i++)
    {
        system.debug('cccccccccccccccccc>' +tempList[i].step_number__c);
        system.debug('ddddddddddddddddddddddd>' +obj.step_number__c);
        if(tempList[i].step_number__c==obj.step_number__c)
         {
         flag = true;
         break;
         }
     }
    tempList.clear(); 
    return flag;
    }
  
   
}