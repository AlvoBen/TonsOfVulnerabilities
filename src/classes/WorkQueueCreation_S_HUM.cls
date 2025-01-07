/***************************************************************************************************************************************************
Apex Class Name  : WorkQueueCreation_S_HUM
Version          : 1.0
Created Date     : 03/16/2016
Function         : Controller to create Work Queue & its related objects
Test Class       : WorkQueueCreation_T_HUM
****************************************************************************************************************************************************
Modification Log:
* Developer Name            Review Number            Date                       Description
----------------------------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya              30706              03/16/2016                    Original Version
* Sharan Shanmugam                                10/20/2016                    Defect fix #245745.0001 
* Sharan Shanmugam             47084              10/20/2016                    Defect fix #247983.0001 
* Sharan Shanmugam             50226              11/30/2106                    Implemented REQ - 290990 -  removed all lookup related fields from 
                                                                                Case/Task/Interaction/Attachment to Work Queue Setup and Queue View
* Pradeepkumar Dani            74579              03/09/2017                    Modified for the Classification_Type__c reference
* Gargey Sharma                81589              04/04/2017                    REQ - 306370 : Updates as per CTCI changes
* Suresh Sunkari		                          09/20/2018                    Modified for Req- 374839 
* Mithra Bharadwaj                                                              US# 1990288    PR00094232 - 2021 MF6 - **P9 - Security** - Sharing  Remediate Checkmarx Security Scan results 
* Subhamay Mandal              74579              04/29/2021                    User Story 2230059: T1PRJ0001827- IR 5050 - Add SLA fields in New - Work Queue Setup (CRM)
* Prudhvi Pamarthi                                05/05/2021                    US#2212581 checkmarx fix
* Bhumika Dodiya								  07/22/2021					T1PRJ0001827 - IR 5050 - Tech Debt - Clean up switch for past releases(CRM) - clean up of US2230059
* Tummala Vijay Lakshmi							  08/27/2021					USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App	
* Tummala Vijay Lakshmi							  09/13/2021					USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App	-To include contract protect data flag in security indicator modified if condition Not to have WORKQUEUE_APP_SECURITYINDICATORVALUE_HUM exception.
Tummala Vijay Lakshmi							  09/13/2021					USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App	-Security Group values general medical dental contract will be updated as per the group slected (Home office And contract protected will Not be in combination)
* Akash Surwase							          09/13/2021					        USER STORY 2768287 - DF5342 fix :T1PRJ0002980 - CR 1 - Home Office and Contract Protected Data Q/V
****************************************************************************************************************************************************/

public with sharing  class WorkQueueCreation_S_HUM 
{
    public string WORKQUEUE_APP_CLASSTYPEREQ_HUM ='Select Classification Type';
    private WorkQueueCreation_D_HUM dLayer = new WorkQueueCreation_D_HUM();
    Public Boolean bErrorQueueView = False;
    
    /**
    * <p>
    * This method is there to initialize data needed for page
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Void
    */ 
    public void construct(WorkQueueCreation_C_HUM cls) 
    {   
        populatePriorityIntegers(cls);
        cls.lstwqsetup = dLayer.getWorkQueueSetupRecords();
        //To populates the values in edit page
        if(cls.workQueueSetup.id != null && Apexpages.currentpage().getParameters().containsKey(GLOBAL_CONSTANT_HUM.ID_HUM))
        {
            cls.isEdit =true;
            cls.bEditBoolean = true;
            populateDataForEdit(cls);
            populateServiceCenterDepartmentQueueViews(cls);  
            fetchSecurityGroup(cls);   
            fetchExistingPublicGroupPGVDeveloperNames(cls);       
        }   
        else
        {
            populateDefaultPriorityValues(cls);
            populateServiceCenterDepartmentQueueViews(cls);
            fetchSecurityGroup(cls);
            fetchExistingPublicGroupPGVDeveloperNames(cls);
        }
        
    }
    
    /**
    * <p>
    * This method populates the integer values which will be used drive Work Queue Priority
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Void
    */
    private void populatePriorityIntegers(WorkQueueCreation_C_HUM cls)
    {
        for(Integer i=1;i<=22;i++)
        {
            cls.priorityIntegers.add(String.valueOf(i));
        }
    }
    
    /**
    * <p>
    * This method populates the values required for the edit page
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Void
    */ 
    private void populateDataForEdit(WorkQueueCreation_C_HUM cls)
    {
        try
        {
           cls.workQueueSetup = dLayer.wqEdit_SOQL(cls.workQueueSetup.id);
           cls.lstWqJunction = cls.workQueueSetup.Work_Queue_Juntions__r;
           if(!cls.workQueueSetup.Work_Queue_Priorities__r.isEmpty())
           {
                cls.wqFP = cls.workQueueSetup.Work_Queue_Priorities__r[0];
           }
           populateWorkQueueTopics(cls);
           populateSecurityGroupIndicators(cls);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','editPagePopulate');
        }
    }
        
    /**
    * <p>
    * This method populates the values for Security Group Indicator
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Void
    */ 
    private void populateSecurityGroupIndicators(WorkQueueCreation_C_HUM cls)
    { 
        set<String> generalIndicators = new Set<String>{
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEALLINDICATORALL_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORMEDICALPHARMACY_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORMEDICALPHARMACYGENERAL_HUM,
                                                            'Humana Contract Protected Data',
                                                            'Humana Pharmacy Home Office',
                                                            'Humana Pharmacy General'};
        set<String> medicalIndicators = new Set<String>{
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEALLINDICATORALL_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORMEDICALPHARMACY_HUM,
                                                        'Humana Contract Protected Data',
                                                            'Humana Pharmacy Home Office'};
        set<String> dentalIndicators = new Set<String>{
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEALLINDICATORALL_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM,
                                                           'Humana Contract Protected Data',
                                                            'Humana Pharmacy Home Office'};

        set<String> CPDindicator = new Set<String>{
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEALLINDICATORALL_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORDENTAL_HUM,
                                                        GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_HOMEOFFICEINDICATORMEDICALPHARMACY_HUM,                                                        
                                                            'Humana Pharmacy Home Office',
                                                            'Humana Contract Protected Data'};                                                   
        
        cls.enableGeneralIndicator = generalIndicators.contains(cls.workQueueSetup.Security_Group__c)?true:false;
        cls.enableHOMedicalIndicator =  medicalIndicators.contains(cls.workQueueSetup.Security_Group__c)?true:false;
        cls.enableHODentalIndicator =  dentalIndicators.contains(cls.workQueueSetup.Security_Group__c)?true:false;
        cls.enableContractProtectedDataIndicator =  CPDindicator.contains(cls.workQueueSetup.Security_Group__c)?true:false;
    }
    
    /**
    * <p>
    * This method populates the values for Security Group Indicator
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Void
    */ 
    private void populateWorkQueueTopics(WorkQueueCreation_C_HUM cls)
    { 
        if(!cls.lstWqJunction.isEmpty())
        {
            for(Work_Queue_Junction__c jun : cls.lstWqJunction)
            {
                Work_Queue_Topic__c wqt = new Work_Queue_Topic__c(Id = jun.Work_Queue_Topic_Name__c,Name = jun.Work_Queue_Topic_Name__r.Name);
                cls.lstTopicName.add(wqt);
                cls.uniqueTopic.add(wqt.Name);
            }
        }
    }
    
    /**
    * <p>
    * This method is used for defaulting the Priority values.   
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private void populateDefaultPriorityValues(WorkQueueCreation_C_HUM cls)
    {
        for(Integer i=1;i<=22;i++)
        {
             cls.wqFP.put(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_CRITERIA_HUM + i + GLOBAL_CONSTANT_HUM.CUSTOMOBJECT_SUFFIX_HUM,
                            String.valueOf((HUMWorkQueuePriorityCriteria__c.getInstance(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_CRITERIA_HUM + i).CriteriaValue__c))); 
             cls.wqFP.put(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PRIORITY_HUM + i + GLOBAL_CONSTANT_HUM.CUSTOMOBJECT_SUFFIX_HUM,String.valueOf(i));     
        }
        
    }
    
    /**
    * <p>
    * This method used to get the service center and Deparment which is already present and populating the picklist values using the same. 
    *
    *
    * Note : Iterating over the work queue setup records and populating service center and department in this method instead of seperate methods to avoid iteration of Work Queue Setup records
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private void populateServiceCenterDepartmentQueueViews(WorkQueueCreation_C_HUM cls)
    {
        List<work_queue_setup__c> lstWorkQueueSetupRecords = new List<work_queue_setup__c>();
        List<Queue_View__c> lstQueueViews = new List<Queue_View__c>();
        Set<String> setServiceCenters = new Set<String>();
        Set<String> setDepartments = new Set<String>();       
        lstWorkQueueSetupRecords = cls.lstwqsetup;
        for(work_queue_setup__c workQueueInstance : lstWorkQueueSetupRecords)
        {
            lstQueueViews.addAll(workQueueInstance.Queue_Views__r);
            cls.setWQnames.add(workQueueInstance.Name);
            if(!String.isBlank(workQueueInstance.Service_Center__c))
            {
                setServiceCenters.add(workQueueInstance.Service_Center__c);
            }
            if(!String.isBlank(workQueueInstance.Department__c))
            {
                setDepartments.add(workQueueInstance.Department__c);
                cls.setDepartments.add(workQueueInstance.Department__c);
            }
            if(!String.isBlank(workQueueInstance.Service_Center__c) && !String.isBlank(workQueueInstance.Department__c))
            {
                cls.setServiceCenterDepartments.add(workQueueInstance.Service_Center__c + GLOBAL_CONSTANT_HUM.HASH_HUM + workQueueInstance.Department__c); 
            }
        }
        populateServiceCenter(cls,setServiceCenters);
        populateDepartment(cls,setDepartments);
        populateUniqueQueueViews(cls,lstQueueViews);
     } 
     
    /**
    * <p>
    * This method used to populate service center picklist values. 
    *
    * @param    cls                   Instance of WorkQueueCreation_C_HUM Class
    * @param    setServiceCenters     Set of strings
    * @return   void
    */
    private void populateServiceCenter(WorkQueueCreation_C_HUM cls,Set<String>setServiceCenters)
    {
       List<String> lstServiceCenters = new List<String>(setServiceCenters);
       lstServiceCenters.sort();
       cls.lstWQSC.add(new SelectOption(' ', GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_NONEPICKLIST_HUM));
       for(String serviceCenter : lstServiceCenters)
       {
           cls.lstWQSC.add(new SelectOption(serviceCenter,serviceCenter));
       }
       cls.lstWQSC.add(new SelectOption(Label.WORKQUEUE_APP_ADDNEW_HUM, Label.WORKQUEUE_APP_ADDNEW_HUM));
    }
    
    /**
    * <p>
    * This method used to populate Deparment Picklist Values. 
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @param    setDepartments        Set of strings
    * @return   void
    */
    private void populateDepartment(WorkQueueCreation_C_HUM cls,Set<String> setDepartments)
    {
       List<String> lstDepartments = new List<String>(setDepartments); 
       lstDepartments.sort();
       cls.lstWQDept.add(new SelectOption(' ', GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_NONEPICKLIST_HUM));  
       for(String departments : lstDepartments)
       {
           cls.lstWQDept.add(new SelectOption(departments,departments));
       }
       cls.lstWQDept.add(new SelectOption(Label.WORKQUEUE_APP_ADDNEW_HUM, Label.WORKQUEUE_APP_ADDNEW_HUM));
    }
   
    /**
    * <p>
    * This method used to populate unique queue view Names. 
    *
    * @param    cls                   Instance of WorkQueueCreation_C_HUM Class
    * @param    lstQueueViews         List of Queue Views
    * @return   void
    */
    private void populateUniqueQueueViews(WorkQueueCreation_C_HUM cls,List<Queue_View__c> lstQueueViews)
    {
        for(Queue_View__c queueView : lstQueueViews)
        {
            cls.setUniqueViews.add(queueView.name);
        }
    }
         
    /**
    * <p>
    * This method used to get the security group.   
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private void fetchSecurityGroup(WorkQueueCreation_C_HUM cls)
    {
        if(cls.workQueueSetup.Security_Group__c != null)
        {
            cls.lstSecurityGroups.add(new SelectOption((String)cls.workQueueSetup.Security_Group__c,(String)cls.workQueueSetup.Security_Group__c));
        }
        else
        {
            cls.lstSecurityGroups.add(new SelectOption(GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_NONE_HUM, GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_NONEPICKLIST_HUM));
        }
        Set<String> setStrings = HUMSecurityGroupAndIndicators__c.getAll().keyset();
        List<String> lstStrings = new List<String>(setStrings);
        lstStrings.sort();
        for( String eachRec : lstStrings )
        {
            if(eachRec == (String)cls.workQueueSetup.Security_Group__c)
            { 
                continue;
            }
            else
            {
            cls.lstSecurityGroups.add(new SelectOption(eachRec, eachRec));
            }
        } 
    }
    
    /**
    * <p>
    * This method fetches the existing PGV Developer Names.  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private void fetchExistingPublicGroupPGVDeveloperNames(WorkQueueCreation_C_HUM cls)
    {
        for(Group eachRec : dLayer.getGroup_SOQL())
        {
            cls.groupDeveloperPublicGroupPGV.add(eachRec.developerName);
        }
    }
    
    /**
    * <p>
    * This method used to show security indicators on selection of security group.  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    public void enableIndicators(WorkQueueCreation_C_HUM cls)
    {
            cls.workQueueSetup.General__c = false;
            cls.workQueueSetup.HO_Medical__c = false;
            cls.workQueueSetup.HO_Dental__c = false;
            cls.workQueueSetup.Contract_Protected_Data__c = false;

        if(cls.securityGroupSelected != GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_NONE_HUM)
        {
            HUMSecurityGroupAndIndicators__c securityIndi = HUMSecurityGroupAndIndicators__c.getInstance(cls.securityGroupSelected);
            cls.enableGeneralIndicator = securityIndi.General__c;
            cls.enableHOMedicalIndicator = securityIndi.HO_Medical__c;
            cls.enableHODentalIndicator = securityIndi.HO_Dental__c;
            cls.workQueueSetup.TenantID__c = securityIndi.TenantId__c;
            cls.enableContractProtectedDataIndicator = securityIndi.Contract_Protected_Data__c;
        }
    }
  
    
    /**
    * <p>
    * This method updates the Work Queue Name based on the service center and department values.    
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    public void updateWorkQueueName(WorkQueueCreation_C_HUM cls)
    {
        cls.displayDeptInput =  isNewDepartmentSelected(cls);
        cls.displaySCInput = isNewServiceCenterSelected(cls);
        cls.workQueueSetup.Name = cls.selectedSC +' ' + cls.selectedDept;
        cls.qvNameToManager = new Map<String,String>();
        cls.qvNameToManagerName = new Map<String,String>();
        cls.mapQueueViewNameToPGVName = new Map<String,String>();
        cls.setUniqueViews = new Set<string>();
        cls.setUniquePGVs = new Set<string>();
        cls.queueViewTable = false;
        cls.queueView = new Queue_View__c();
        cls.sPGVName = '';

        //Populating PGQ Name based on the selected service center and department values
        if(cls.selectedSC != Label.WORKQUEUE_APP_ADDNEW_HUM  && cls.selectedDept != null  && cls.selectedDept != Label.WORKQUEUE_APP_ADDNEW_HUM )
        {
            cls.workQueueSetup.Name = cls.selectedSC + ' ' + cls.selectedDept;
            if(cls.workQueueSetup.Name.containsAny('/&()-')) removeSpecialCharacterWQS(cls);
            Boolean bValidWorkQueueSetupLength = cls.workQueueSetup.Name.length() > 36;
            Boolean bValidWQSLength = cls.workQueueSetup.Name.length() > 40;
            cls.workQueueSetup.Name = (bValidWQSLength ? cls.workQueueSetup.Name.substring(0,40) : cls.workQueueSetup.Name);
            cls.publicGroupRecord.Name = (bValidWorkQueueSetupLength ? cls.workQueueSetup.Name.substring(0,36) + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM :
                                         cls.selectedSC + ' '+ cls.selectedDept + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM);
            if(cls.publicGroupRecord.Name.containsAny('/&()-')) removeSpecialCharacterPGQ(cls);
            cls.workQueueSetup.Public_Group_Name__c = cls.publicGroupRecord.Name;
                 
        }
        else if(cls.newServiceCenterValue != null && cls.selectedDept != Label.WORKQUEUE_APP_ADDNEW_HUM)
        {
           cls.workQueueSetup.Name = cls.newServiceCenterValue +' ' + cls.selectedDept;
           if(cls.workQueueSetup.Name.containsAny('/&()-')) removeSpecialCharacterWQS(cls);
           Boolean bValidWorkQueueSetupLength = cls.workQueueSetup.Name.length() > 36;
           Boolean bValidWQSLength = cls.workQueueSetup.Name.length() > 40;
           cls.workQueueSetup.Name = (bValidWQSLength ? cls.workQueueSetup.Name.substring(0,40) : cls.workQueueSetup.Name);
           cls.publicGroupRecord.Name = (bValidWorkQueueSetupLength ? cls.workQueueSetup.Name.substring(0,36) + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM : 
                                            cls.newServiceCenterValue +' ' + cls.selectedDept + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM); 
           if(cls.publicGroupRecord.Name.containsAny('/&()-')) removeSpecialCharacterPGQ(cls);  
           cls.workQueueSetup.Public_Group_Name__c = cls.publicGroupRecord.Name;                             
        }
        else if(cls.newServiceCenterValue != Label.WORKQUEUE_APP_ADDNEW_HUM  && cls.newDepartmentValue != null && cls.selectedSC != Label.WORKQUEUE_APP_ADDNEW_HUM)
        {
            cls.workQueueSetup.Name = cls.selectedSC +' '+ cls.newDepartmentValue;
            if(cls.workQueueSetup.Name.containsAny('/&()-')) removeSpecialCharacterWQS(cls);
            Boolean bValidWorkQueueSetupLength = cls.workQueueSetup.Name.length() > 36;
            Boolean bValidWQSLength = cls.workQueueSetup.Name.length() > 40;
            cls.workQueueSetup.Name = (bValidWQSLength ? cls.workQueueSetup.Name.substring(0,40) : cls.workQueueSetup.Name);
            cls.publicGroupRecord.Name = (bValidWorkQueueSetupLength ? cls.workQueueSetup.Name.substring(0,36) + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM :
                                           cls.selectedSC +' '+ cls.newDepartmentValue + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM) ;
            if(cls.publicGroupRecord.Name.containsAny('/&()-')) removeSpecialCharacterPGQ(cls);   
            cls.workQueueSetup.Public_Group_Name__c = cls.publicGroupRecord.Name;                            
        }
        else if(cls.newServiceCenterValue != null  && cls.newDepartmentValue != null)
        {
            cls.workQueueSetup.Name = cls.newServiceCenterValue +' '+ cls.newDepartmentValue;
            cls.workQueueSetup.Service_Center__c = cls.newServiceCenterValue;
            cls.workQueueSetup.department__c  = cls.newDepartmentValue;
            if(cls.workQueueSetup.Name.containsAny('/&()-')) removeSpecialCharacterWQS(cls);
            Boolean bValidWorkQueueSetupLength = cls.workQueueSetup.Name.length() > 36;
            Boolean bValidWQSLength = cls.workQueueSetup.Name.length() > 40;
            cls.workQueueSetup.Name = (bValidWQSLength ? cls.workQueueSetup.Name.substring(0,40) : cls.workQueueSetup.Name);          
            cls.publicGroupRecord.Name = (bValidWorkQueueSetupLength ? cls.workQueueSetup.Name.substring(0,36) + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM : 
                                          cls.newServiceCenterValue +' '+ cls.newDepartmentValue + ' ' + GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGQ_HUM); 
            if(cls.publicGroupRecord.Name.containsAny('/&()-')) removeSpecialCharacterPGQ(cls); 
            cls.workQueueSetup.Public_Group_Name__c = cls.publicGroupRecord.Name;
        }
    }
    
    /**
    * Removes the special characters from Work Queue Setup Name and PGQ Name  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    public void removeSpecialCharacterWQS(WorkQueueCreation_C_HUM cls)
    {
        if(cls.workQueueSetup.Name.contains('/')) cls.workQueueSetup.Name = cls.workQueueSetup.Name.replace('/',' ');
        if(cls.workQueueSetup.Name.contains('(')) cls.workQueueSetup.Name = cls.workQueueSetup.Name.replace('(',' ');
        if(cls.workQueueSetup.Name.contains(')')) cls.workQueueSetup.Name = cls.workQueueSetup.Name.replace(')',' ');
        if(cls.workQueueSetup.Name.contains('&')) cls.workQueueSetup.Name = cls.workQueueSetup.Name.replace('&',' ');
        if(cls.workQueueSetup.Name.contains('-')) cls.workQueueSetup.Name = cls.workQueueSetup.Name.replace('-',' ');
    }
    
    /**
    * Removes the special characters from Work Queue Setup Name and PGQ Name  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    public void removeSpecialCharacterPGQ(WorkQueueCreation_C_HUM cls)
    {
        if(cls.publicGroupRecord.Name.contains('/')) cls.publicGroupRecord.Name = cls.publicGroupRecord.Name.replace('/',' ');
        if(cls.publicGroupRecord.Name.contains('(')) cls.publicGroupRecord.Name = cls.publicGroupRecord.Name.replace('(',' ');
        if(cls.publicGroupRecord.Name.contains(')')) cls.publicGroupRecord.Name = cls.publicGroupRecord.Name.replace(')',' ');
        if(cls.publicGroupRecord.Name.contains('&')) cls.publicGroupRecord.Name = cls.publicGroupRecord.Name.replace('&',' ');
        if(cls.publicGroupRecord.Name.contains('-')) cls.publicGroupRecord.Name = cls.publicGroupRecord.Name.replace('-',' ');
        
    }
    
    /**
    * Returns Boolean verifies whether user is adding service center
    * <p>
    * This method checks if Add new is selected as service center.  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Boolean
    */
    private Boolean isNewServiceCenterSelected(WorkQueueCreation_C_HUM cls)
    {
        return cls.selectedSC == Label.WORKQUEUE_APP_ADDNEW_HUM ? true : false;
    }
    
    /**
    * Returns Boolean verifies whether user is adding Department
    * <p>
    * This method checks if Add new is selected as department.  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private Boolean isNewDepartmentSelected(WorkQueueCreation_C_HUM cls)
    {
        return cls.selectedDept == Label.WORKQUEUE_APP_ADDNEW_HUM ? true : false;
    }
    
    /**
    * <p>
    * This method used to delete Group records. 
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    public void deletePreviouslyCreatedGroupObjects(WorkQueueCreation_C_HUM cls)
    {
        try
        {
            List<Sobject> lstGroupRecords = new List<SObject>();
            
            if(cls.publicGroupRecord.id != null) lstGroupRecords.add(cls.publicGroupRecord);
            if(cls.queueGroupRecord.id != null) lstGroupRecords.add(cls.queueGroupRecord);
            
            if(!lstGroupRecords.isEmpty())
            {
                dLayer.deleteRecords(lstGroupRecords);
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','deletePreviouslyCreatedGroupObjects');
        }
        
    }
    
    /**
    * <p>
    * This method used to ckeck if the Work Queue Setup Name, service center and departments entered are unique .
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private void getUniqueValues(WorkQueueCreation_C_HUM cls)
    {
        cls.bFlagWQ = cls.setWQnames.contains(cls.workQueueSetup.Name);
        String serviceCenterValue = (cls.selectedSC == Label.WORKQUEUE_APP_ADDNEW_HUM) ? cls.newServiceCenterValue : cls.selectedSC;
        String departmentValue = (cls.selectedDept == Label.WORKQUEUE_APP_ADDNEW_HUM) ? cls.newDepartmentValue : cls.selectedDept;
        
        if(serviceCenterValue != null && departmentValue != null)
        {
            cls.bFlagSCANDD = cls.setServiceCenterDepartments.contains(serviceCenterValue + GLOBAL_CONSTANT_HUM.HASH_HUM + departmentValue) ;
        }
        
        if(!String.isBlank(cls.newDepartmentValue))
        {
            cls.bFlagD = cls.setDepartments.contains(cls.newDepartmentValue);
        }   
    }
    
    /**
    * <p>
    * This method used to insert groups.
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   void
    */
    private void  createGroup(WorkQueueCreation_C_HUM cls)
    {
        if(cls.publicGroupRecord.Name.containsAny('/&()-')) removeSpecialCharacterPGQ(cls);
        if(cls.workQueueSetup.Name.containsAny('/&()-')) removeSpecialCharacterWQS(cls);
        cls.publicGroupRecord  = createGroupRecord(cls.publicGroupRecord.Name,'', GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM,cls);
        
        if(dLayer.insertsObject(new List<sObject>{cls.publicGroupRecord}))
        {
            cls.publicGroupCreationError =true;
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR,Label.WORKQUEUE_APP_UNIQUEPGQ_HUM, true);
        }
        else
        {
            cls.queueGroupRecord = createGroupRecord(cls.workQueueSetup.Name, '', GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM,cls);
            dLayer.insertsObject(new List<sObject>{cls.queueGroupRecord});
            cls.queueRecordList = createQueueRecord (cls.queueGroupRecord.Id);
            createGroupMembersPGQ(cls.queueGroupRecord.id, cls.publicGroupRecord.id);
            if(!cls.queueRecordList.isEmpty())
            {
                dLayer.insertsObject(new List<sObject>((List<sObject>)cls.queueRecordList));
            }
        }       
    }
    
    /**
    * Returns List<QueuesObject> List of QueueSObject records which needs to be inserted
    * <p>
    * This method used to create queue sobject record.
    *
    * @param    Id                      Group Id 
    * @return   List<QueueSObject>      Returns list of queue sObject records
    */
    private List<QueuesObject> createQueueRecord(Id groupId)
    {
        List<QueueSobject> queueList = new List<QueueSobject>();
        QueueSobject queueRecord1 = new QueueSobject();
        queueRecord1.queueid = groupId;
        queueRecord1.sobjectType = GLOBAL_CONSTANT_HUM.CASE_CONSTANT_HUM;
        queueList.add(queueRecord1);
        
        QueueSobject queueRecord2 = new QueueSobject();
        queueRecord2.queueid = groupId;
        queueRecord2.sobjectType = GLOBAL_CONSTANT_HUM.WORKQUEUESETUP_OBJECT_HUM;
        queueList.add(queueRecord2);
        
        return queueList;
    }
    
    /**
    * <p>
    * This method used to create relation between the PGQ and the Work Queue.
    *
    * @param    wqs             work Queue setup record Id
    * @oaram    publicgrpId     Public Group Id 
    * @return   void
    */
    @future
    public static void createGroupMembersPGQ(id wqs, id publicgrpId)
    {
        try
        {
            GroupMember grpMemm = new GroupMember(UserOrGroupId = publicgrpId , GroupId = wqs);
            Boolean berrorGroupMembersPGQ = WorkQueueCreation_D_HUM.insertStaticsObject(new List<sObject>{grpMemm});
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','createGroupMembersPGQ');
        }
    }
    
    /**
    * Returns Boolean Whether Priority entered by user is valid or not
    * <p>
    * This method used to verify the priority values enetred.
    *
    * @oaram    cls     Instance of class WorkQueueCreation_C_HUM   
    * @return   Boolean
    */
    private Boolean checkPriority(WorkQueueCreation_C_HUM cls)
    {
        Set<Integer> uniquePriority = new Set<Integer>();
        for(Integer i=1;i<=22;i++)
        {
            if(cls.wqFP.get( GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PRIORITY_HUM + i + GLOBAL_CONSTANT_HUM.CUSTOMOBJECT_SUFFIX_HUM) != NULL)
            {
                uniquePriority.add(Integer.valueOf(cls.wqFP.get( GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PRIORITY_HUM + i + GLOBAL_CONSTANT_HUM.CUSTOMOBJECT_SUFFIX_HUM)));
            }
            else
            {
                return true;    
            }
        }
      
        //Checking if the priority values are unique and between 1 and 22
        if(uniquePriority.size() == 22)
        {
            for(Integer i : uniquePriority)
            {
                if(i > 22 || i < 1)
                {
                    return true;
                }
            }
        }
        else
        {
            return true;
        }
        return false;
    }
    
    /**
    * <p>
    * This method used to insert groups.
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM
    * @return   void
    */
    public void  createWorkQueueGroupPGQ(WorkQueueCreation_C_HUM cls)
    {
        Savepoint sp = Database.setSavepoint();
        cls.bErrorBooleanPriority = false;
        cls.publicGroupCreationError = false;
        try
        {
            if(!cls.bEditBoolean)
            {
                getUniqueValues(cls);
            }
            validateWorkQueueSetupData(cls);
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','createWorkQueueGroupPGQ');
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_ERROROCCURED_HUM, true);
            Database.rollback(sp);
        }
        
    }
    
    /**
    * <p>
    * This method used to Update groups.
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM
    * @return   void
    */
    public void  updateWorkQueueGroupPGQ(WorkQueueCreation_C_HUM cls)
    {
        List<String> oldGroupNames = new List<String>();
        List<String> newGroupNames = new List<String>();
        Boolean bSuccess  = true;
        Work_Queue_Setup__c wsq = [Select id,name,Public_Group_Queue_Id__c from Work_Queue_Setup__c  where id =: ApexPages.currentPage().getParameters().get('id')];
        Group lstGrp = [Select id,name,developername,type from group where id =: wsq.Public_Group_Queue_Id__c];
        oldGroupNames.add(lstGrp.name);
        oldGroupNames.add(wsq.name); 
        newGroupNames.add(cls.workQueueSetup.Public_Group_Name__c);
        newGroupNames.add(cls.workQueueSetup.name);
        for(integer iCounter = 0; iCounter < oldGroupNames.size(); iCounter++ ) {
            bSuccess = UpdateGroup(oldGroupNames[iCounter], newGroupNames[iCounter]);
        }
        updateServiceCenterAndDepartment(cls);       
    }
    
    /**
     * Method Name : UpdateGroup
     * Discription : Updates the Group Object on Name, DeveloperName
     * Inputs      : sOldGroupName, sNewDeveloperName, sNewGroupName        
     * OutPut      : Boolean Values true or false based on successfull execution.                   
    */
    public static boolean UpdateGroup(String sOldGroupName, String sNewGroupName){
        boolean bSuccess = true;
        Try{
            List<Group> lstGroup = [SELECT Id, Name, DeveloperName FROM Group where name =:sOldGroupName];
            if(!lstGroup.IsEmpty()){
                for( Group oGroup : lstGroup ){
                    oGroup.Name = sNewGroupName;
                    oGroup.DeveloperName = sNewGroupName.replace(' - ',' ').replace('-','_').replace(' & ',' ').replace('&','_').replace('/','_').replace('','').replace(' ','_').replace('__','_');
                }
                update lstGroup;
            }
        }catch(exception e){
            HUMExceptionHelper.logErrors(e, 'WorkQueueCreation_S_HUM', 'UpdateGroup');
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_ERROROCCURED_HUM, true);
            bSuccess = false;
        }
        return bSuccess;
    }
    
    public void updateServiceCenterAndDepartment(WorkQueueCreation_C_HUM cls)
    {
        try{
            if(Schema.sObjectType.Work_Queue_Setup__c.fields.Service_Center__c.isUpdateable())
            cls.workQueueSetup.Service_Center__c = (cls.selectedSC == 'Add New')? cls.newServiceCenterValue : cls.selectedSC;
            if(Schema.sObjectType.Work_Queue_Setup__c.fields.Department__c.isUpdateable())
            cls.workQueueSetup.Department__c = (cls.selectedDept == 'Add New')? cls.newDepartmentValue : cls.selectedDept;
            update cls.workQueueSetup;
        }catch(exception e){
            HUMExceptionHelper.logErrors(e, 'WorkQueueCreation_S_HUM', 'updateServiceCenterAndDepartment');
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_ERROROCCURED_HUM, true);
        }
   }
        
    /**
    * <p>
    * This method used to validate work queue setup record
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM
    * @return   void
    */
    private void validateWorkQueueSetupData(WorkQueueCreation_C_HUM cls)
    {
        Boolean bcheckPriority = checkPriority(cls);
        //To check if the selected service center and department combination is unique
        if(cls.bFlagSCANDD && !cls.bEditBoolean)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_UNIQUESCDEPT_HUM, true);
            cls.publicGroupCreationError =true;
        }
        //To avoid duplicate department value
        else if(cls.bFlagD && !cls.bEditBoolean)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_UNIQUEDEPARTMENT_HUM , true);
            cls.publicGroupCreationError =true;
        }
        //To check if the Work Queue and PGQ Name are unique
        else if(cls.bFlagWQ && !cls.bEditBoolean)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_UNIQUEWORKQUEUE_HUM, true);
            cls.publicGroupCreationError =true;
        }
        //To check if Work Queue Setup Name is not blank
        else if(cls.workQueueSetup.Name == null && !cls.bEditBoolean)
        {
           HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_UNIQUEWQNAME_HUM, true);
           cls.publicGroupCreationError =true;
        }
        //To check if PGQ Name is not blank
        else if(cls.publicGroupRecord.Name == null && !cls.bEditBoolean)
        {
           HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_PGQVALUE_HUM, true);
           cls.publicGroupCreationError =true;
        }
        //To check if service center and department are not blank
        else if((cls.selectedSC ==' ' || cls.selectedDept == ' ') && !cls.bEditBoolean )
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_SCDEPTVALUE_HUM, true);
            cls.publicGroupCreationError =true;
        }  
        //To check if a security group is selected             
        else if(cls.securityGroupSelected == GLOBAL_CONSTANT_HUM.NONE && !cls.bEditBoolean)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_SECURITYGROUPVALUE_HUM, true);
            cls.publicGroupCreationError =true;
            cls.workQueueCreationError =true;
        }
        //To check if a security indicator is checked 
        else if(!cls.workQueueSetup.General__c  && !cls.workQueueSetup.HO_Medical__c  && !cls.workQueueSetup.HO_Dental__c && !cls.workQueueSetup.Contract_Protected_Data__c)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_SECURITYINDICATORVALUE_HUM, true);
            cls.workQueueCreationError = true;
            cls.publicGroupCreationError = true;
        }
        //To check if there are any error in priority values entered
        else if(bcheckPriority)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_UNIQUEWQPRIORITY_HUM, true);
            cls.publicGroupCreationError = true;
            if(!cls.bEditBoolean) 
                cls.bErrorBooleanPriority =true;
        }
        else if(string.isBlank(cls.workQueueSetup.Classification_Type__c))
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, WORKQUEUE_APP_CLASSTYPEREQ_HUM, true);
            cls.publicGroupCreationError =true;
        }       
        else if(!cls.bEditBoolean)   
        {
            createGroup(cls);  
        }        
    }
    
    /**
    * Returns Group record to be inserted
    * <p>
    * This method used to create group records.
    *
    * @param    recordName          Queue Name
    * @param    groupNameSuffix     Group Name Suffix
    * @oaram    groupType           Group Type  
    * @oaram    cls                 Instance of class WorkQueueCreation_C_HUM   
    * @return   Group
    */
    private Group createGroupRecord(String recordName, String groupNameSuffix, String groupType, WorkQueueCreation_C_HUM cls)
    {    
        String groupName ='';
        String  developerName = '';
        String queueName = recordName.trim();
        
        groupName = getGroupName(queueName, groupNameSuffix, groupType, cls);
        developerName = groupName.replace(' ','_');
        Group grp = new Group();
        grp.Name = groupName;
        grp.developerName = developerName;
        grp.type = groupType;
        grp.DoesSendEmailToMembers = false;
        if(groupType == GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM) grp.DoesIncludeBosses = false;
        return grp;
    }
    
    /**
    * Returns String Group Name
    * <p>
    * This method used to get the group name
    *
    * @param    recordName          Queue Name
    * @param    groupNameSuffix     Group Name Suffix
    * @oaram    groupType           Group Type  
    * @oaram    cls                 Instance of class WorkQueueCreation_C_HUM   
    * @return   String
    */
    private String getGroupName(String recordName, String groupNameSuffix, String groupType, WorkQueueCreation_C_HUM cls)
    {
        String groupName;
        if (groupNameSuffix == GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGV_HUM)
        {
            if(groupType == GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM)
            {
                String tempPGVName = cls.wqs.Name;
                tempPGVName = (tempPGVName.length() > 30 ? tempPGVName.subString(0,30) : tempPGVName);
                groupName = (recordName == GLOBAL_CONSTANT_HUM.HUM_ALLOTHERVIEW ?  tempPGVName +' ' + GLOBAL_CONSTANT_HUM.HUM_ALLOTHERVIEW + ' ' + 
                                                        groupNameSuffix : recordName) ;
            }
            else if(groupType == GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM)
            {
                groupName = (recordName.length() > 40 ? cls.wqs.Name + ' ' + recordName.substring(0,35) : cls.wqs.Name + ' ' + recordName);
            }
        }
        else
        {
            if(groupType == GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM || groupType == GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM)
            {
                groupName = recordName;
            }
            else if(groupType == GLOBAL_CONSTANT_HUM.GROUP_QUEUE_HUM)
            {
                if(recordName.length() > 40)
                {
                    groupName = recordName.substring(0,40);
                }
            }   
        }
        return groupName;
    }
    
    /**
    * Returns PageReference url afer creating Work Queue
    * <p>
    * This method used to create work queue record,topics, junction.  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   PageReference
    */
    public PageReference createWorkQueue(WorkQueueCreation_C_HUM cls)
    {
        try
        {
            if(!cls.bEditBoolean)   
            {
                populateWorkQueueSetupDetails(cls);
            }
            else    
            {
                populateWorkQueueSetupDetailsEdit(cls);
            }
            if(upsertWorkQueue(cls) || createWQ(cls))
            {
                deletePreviouslyCreatedGroupObjects(cls);
                dLayer.deleteRecords(new List<sObject>{cls.wqs});
                return null;
            }
            else
            {
                
                if(!cls.bEditBoolean) createGroupMembersPGQSecurity(cls.wqs.Security_Group__c, cls.publicGroupRecord.id);
                createWorkQueueTopics(cls);
                if(!cls.qvNameToManager.isEmpty()) createQueueViews(cls); 
                cls.pg = (((cls.bEditBoolean) || (cls.qvNameToManager.isEmpty() && !cls.bEditBoolean))? new PageReference('/'+cls.wqs.Id) 
                            : new PageReference(GLOBAL_CONSTANT_HUM.QUEUEVIEWITEM_VFPAGE_HUM+cls.wqs.id));
                cls.pg.setRedirect(true);
                }
        }  
        catch(Exception ex)
        {
            HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_ERROROCCURED_HUM, true);
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','createWorkQueue');
            cls.workQueueCreationError =false;
        }
        return cls.pg;  
    }
    
   /**
    * Returns Boolean after upserting Work Queue
    * <p>
    * This method used upsert Work Queue  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Boolean
    */
    private Boolean upsertWorkQueue(WorkQueueCreation_C_HUM cls)
    {
        return (dLayer.upsertWQObject(cls.wqs));
    }
    
   /**
    * Returns Boolean after validation
    * <p>
    * This method used to validate records other than group.  
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM Class
    * @return   Boolean
    */
    public Boolean createWQ(WorkQueueCreation_C_HUM cls)
    {
       if(!cls.bErrorBooleanPriority && cls.wqs.Id != null)
        {
            cls.wqPriority = createFilterPriority(cls);
            if(cls.wqPriority != null)
            {
                if(!cls.bEditBoolean) 
                  cls.wqPriority.Work_Queue_Name__c = cls.wqs.Id;
                return (dLayer.upsertWQPriorityObject(cls.wqPriority));
            }
        }
        return true;
    }
    
    /**
    * <p>
    * This method used to populate Work Queue Setup record Details while editing.
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM
    * @return   void
    */
    public void populateWorkQueueSetupDetailsEdit(WorkQueueCreation_C_HUM cls)
    {
        cls.wqs = new Work_Queue_Setup__c();
        cls.wqs.Name= cls.workQueueSetup.Name;
        cls.wqs.id= cls.workQueueSetup.id;
        cls.wqs.Queue_Description__c = cls.workQueueSetup.Queue_Description__c;
        cls.wqs.Queue_Manager__c = cls.workQueueSetup.Queue_Manager__c;
        cls.wqs.General__c = cls.workQueueSetup.General__c;
        cls.wqs.HO_Medical__c = cls.workQueueSetup.HO_Medical__c;
        cls.wqs.HO_Dental__c = cls.workQueueSetup.HO_Dental__c;
        cls.wqs.Contract_Protected_Data__c = cls.workQueueSetup.Contract_Protected_Data__c;
        cls.wqs.Security_Group__c = cls.securityGroupSelected;
        cls.wqs.Name= cls.workQueueSetup.Name;
    }
       
    /**
    * <p>
    * This method used to populate Work Queue Setup record Details.
    *
    * @param    cls     Instance of WorkQueueCreation_C_HUM
    * @return   void
    */
    public void populateWorkQueueSetupDetails(WorkQueueCreation_C_HUM cls)
    {
        cls.wqs.Service_Center__c = (cls.selectedSC == Label.WORKQUEUE_APP_ADDNEW_HUM) ? cls.newServiceCenterValue : cls.selectedSC;
        cls.wqs.Department__c = (cls.selectedDept == Label.WORKQUEUE_APP_ADDNEW_HUM) ? cls.newDepartmentValue : cls.selectedDept;
        cls.wqs.Classification_Type__c = cls.workQueueSetup.Classification_Type__c;
        cls.wqs.Security_Group__c = cls.securityGroupSelected;
        cls.wqs.OwnerId = cls.queueGroupRecord.Id;
        cls.wqs.TenantID__c = cls.workQueueSetup.TenantID__c;
        cls.wqs.IsActive__c = false;
        cls.wqs.Public_Group_Queue_Id__c = cls.publicGroupRecord.Id;
        cls.wqs.Public_Group_Name__c = cls.publicGroupRecord.Name;
        if(cls.workQueueSetup.Name.containsAny('/&()-')) removeSpecialCharacterWQS(cls);
        cls.wqs.Name= cls.workQueueSetup.Name;
        cls.wqs.id= cls.workQueueSetup.id;
        cls.wqs.Queue_Description__c = cls.workQueueSetup.Queue_Description__c;
        cls.wqs.Queue_Manager__c = cls.workQueueSetup.Queue_Manager__c;
        cls.wqs.General__c = cls.workQueueSetup.General__c;
        cls.wqs.HO_Medical__c = cls.workQueueSetup.HO_Medical__c;
        cls.wqs.HO_Dental__c = cls.workQueueSetup.HO_Dental__c;       
        cls.wqs.SLA_In_Days__c = cls.workQueueSetup.SLA_In_Days__c;
        cls.wqs.SLA_On_Case_Transfer__c = cls.workQueueSetup.SLA_On_Case_Transfer__c;
        cls.wqs.Contract_Protected_Data__c = cls.workQueueSetup.Contract_Protected_Data__c;
    }
        
    /**
    * <p>
    * This method used to create relation between the security group and the Work Queue.
    *
    * @param    sSecurity           Security Name
    * @param    publicgrpId         Public Group id
    * @return   Void
    */
    @future
    public static void createGroupMembersPGQSecurity(String sSecurity , id publicgrpId)
    {
        try
        {
            List<Group> lstGrpSec = new List<Group>();
            lstGrpSec = [Select id, Name from Group where Name =: sSecurity];
            GroupMember grpMemm = new GroupMember(UserOrGroupId = publicgrpId , GroupId = lstGrpSec[0].id);
            Boolean berrorGroupMembersPGQSecurity = WorkQueueCreation_D_HUM.insertStaticsObject(new List<sObject>{grpMemm});
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','createGroupMembersPGQSecurity');
        }
        
    }
    
    /**
    * <p>
    * This method used to create topic.
    *
    * @param    cls         WorkQueueCreation_C_HUM
    * @return   Void
    */
    private void createWorkQueueTopics(WorkQueueCreation_C_HUM cls)
    {
        List<Work_Queue_Junction__c> wqJunctions = new List<Work_Queue_Junction__c>();
        List<Work_Queue_Topic__c> wqTopicRecords = new List<Work_Queue_Topic__c>();
        wqTopicRecords = CreateWorkQueueTopic(cls);
        try
        {
            if(!cls.topicNames.isEmpty())
            {
                if(!wqTopicRecords.isEmpty())
                {
                    Boolean berrorTopic = dLayer.insertsObject((List<sObject>)wqTopicRecords);
                    wqJunctions = createWorkQueueJunction(cls.wqs.Name, cls.wqs.Id,wqTopicRecords);
                }
            }
            if(!wqJunctions.isEmpty())
            {
                Boolean berrorJunction = dLayer.insertsObject((List<sObject>)wqJunctions);
            }
            
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','createWorkQueueTopics');
        }
    } 
    
    /**
    * Returns List<Work_Queue_Topic__c> creates work queue topic
    * <p>
    * This method used to create work queue topic record.
    *
    * @param    cls                             WorkQueueCreation_C_HUM
    * @return   List<Work_Queue_Topic__c>
    */
    private List<Work_Queue_Topic__c> createWorkQueueTopic(WorkQueueCreation_C_HUM cls)
    {
        List<Work_Queue_Topic__c> wqTopicRecords = new List<Work_Queue_Topic__c>();
        if(!cls.topicNames.isEmpty())
        {
            for(String eachRec : cls.topicNames)
            {
                Work_Queue_Topic__c wqt = new Work_Queue_Topic__c();
                wqt.Name = eachRec; 
                wqTopicRecords.add(wqt);
            }
             
        }   
        return wqTopicRecords;
    }
        
    /**
    * Returns List<Work_Queue_Junction__c> creates work queue junction records
    * <p>
    * This method used to create work queue junction record.
    *
    * @param    wqsName                             Name of Work Queue
    * @param    workQueueId                         Work Queue Id
    * @param    topicsList                          List of topics
    * @return   List<Work_Queue_Junction__c>
    */
    private List<Work_Queue_Junction__c> createWorkQueueJunction(String wqsName, Id workQueueId, List<Work_Queue_Topic__c> topicsList)
    {
        List<Work_Queue_Junction__c> wqJunctions = new List<Work_Queue_Junction__c>();
        for(Work_Queue_Topic__c eachRec : topicsList)
        {
            Work_Queue_Junction__c wqj = new Work_Queue_Junction__c();
            wqj.Name = wqsName +':'+ eachRec.Name;
            wqj.Work_Queue_Setup_Name__c = workQueueId;
            wqj.Work_Queue_Topic_Name__c = eachRec.Id;
            
            wqJunctions.add(wqj);
        }
        return wqJunctions;
    }
    
    /**
    * Returns Work_Queue_Priority__c returns filter priority
    * <p>
    * This method used to create filter priority.
    *
    * @param    cls                     Instance of Class WorkQueueCreation_C_HUM
    * @return   Work_Queue_Priority__c
    */
    public Work_Queue_Priority__c createFilterPriority(WorkQueueCreation_C_HUM cls)
    {
        return cls.wqFP;
    }
    
    /**
    * <p>
    * This method used to add the topics.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    public void addTopic(WorkQueueCreation_C_HUM cls)
    {
        try
        {
            if(!String.isBlank(cls.workQueueTopic.Name))
            {
                if(cls.uniqueTopic.contains(cls.workQueueTopic.Name))
                {
                    HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_DUPLICATETOPIC_HUM, true);
                }
                else
                {
                    cls.uniqueTopic.add(cls.workQueueTopic.Name);
                    cls.topicNames.add(cls.workQueueTopic.Name);        
                }
            }
        }
        catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex,'WorkQueueCreation_S_HUM','addTopic');
        }
    }
    
    /**
    * <p>
    * This method used to remove the topics.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    public void removeTopicFromList(WorkQueueCreation_C_HUM cls)
    {
        //Removing the topic from topicNames list on click of Remove link 
        for(Integer i = 0; i < cls.topicNames.size() ; i++)
        {
            if(cls.removedTopic == cls.topicNames[i])
            {
                cls.topicNames.remove(i);
                cls.uniqueTopic.remove(cls.removedTopic);
                break;
            }
        }
    }
    
    /**
    * <p>
    * This method used to add the Queue Views.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    public void addQueueView(WorkQueueCreation_C_HUM cls)
    {
        cls.bqvAdd = true;
        User viewManagerName = new User();
        String sPGVDeveloperName = cls.sPGVName;
        if(cls.sPGVName.contains(' ')) 
            sPGVDeveloperName = cls.sPGVName.replace(' ','_');
        
        if((cls.QueueView.Name != null))
        {
            if (cls.queueView.View_Manager__c != null) viewManagerName = dLayer.getUserName(cls.queueView.View_Manager__c);
            
            if(!cls.setUniqueViews.contains(cls.QueueView.Name) && !cls.setUniquePGVs.contains(cls.sPGVName) && !cls.groupDeveloperPublicGroupPGV.Contains(sPGVDeveloperName))
            {
                cls.qvNameToManager.put(cls.queueView.Name,cls.queueView.View_Manager__c);
                cls.mapQueueViewNameToPGVName.put(cls.queueView.Name,cls.sPGVName);
                if (cls.queueView.View_Manager__c != null) 
                {
                    cls.qvNameToManagerName.put(cls.queueView.Name,viewManagerName.Name);
                }
                else 
                {
                    cls.qvNameToManagerName.put(cls.queueView.Name,' ');
                }
                cls.queueViewTable = true;
                cls.setUniqueViews.add(cls.QueueView.Name);
                cls.setUniquePGVs.add(cls.sPGVName);
                cls.queueView = new Queue_View__c();
                cls.sPGVName = '';
            }
            else
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR,Label.WORKQUEUE_APP_UNIQUEQV_HUM, true);
            }       
        }
    }
    
    /**
    * <p>
    * This method used to create Queue Views.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    private void createQueueViews(WorkQueueCreation_C_HUM cls)
    {
        List<Queue_View__c> queueViewRecords = new List<Queue_View__c>();
        List<GroupMember> lstgrpMemm = new List<GroupMember>();
        GroupMember grpMemm = new GroupMember();
        if(!cls.qvNameToManager.KeySet().isEmpty())
        {
            Map<String, Group> ViewNameToGroups = new Map<String,Group>();
           
            for(String eachViewName : cls.qvNameToManager.keySet())
            {
                String groupname = eachViewName;
                if(cls.mapQueueViewNameToPGVName.containsKey(eachViewName))
                    groupname = cls.mapQueueViewNameToPGVName.get(eachViewName);
                ViewNameToGroups.put(eachViewName, createGroupRecord(groupname,GLOBAL_CONSTANT_HUM.WORKQUEUE_APP_PGV_HUM, GLOBAL_CONSTANT_HUM.GROUP_REGULAR_HUM,cls));
            }
            Database.SaveResult[] lstSaveResultPGV = Database.insert(ViewNameToGroups.values(), false);
            HUMExceptionHelper.bShowErrorOnPage = false;
            Boolean bErrorPGV = HUMExceptionHelper.processSaveResults(lstSaveResultPGV, ViewNameToGroups.values(),'WorkQueueCreation_S_HUM', 'createQueueViews', 'Group');
            if(bErrorPGV)
            {
                HUMExceptionHelper.addMessageToPage(ApexPages.Severity.ERROR, Label.WORKQUEUE_APP_PGVUNIQUEERROR_HUM, true);
            }
            else
            {
                Set<id> groupIds = new Map<id  , Group> (ViewNameToGroups.values()).keySet();
                createGroupMembers(groupIds, cls.publicGroupRecord.id);
            }
            queueViewRecords = createQueueView(cls.wqs, cls.qvNameToManager,ViewNameToGroups);
        }
        
        if(!queueViewRecords.isEmpty())
        {
            Database.SaveResult[] lstSaveResultQueueView = Database.insert(queueViewRecords, false);
            HUMExceptionHelper.bShowErrorOnPage = false;
            bErrorQueueView = HUMExceptionHelper.processSaveResults(lstSaveResultQueueView, queueViewRecords, 'WorkQueueCreation_S_HUM', 'createQueueViews', 'Queue_View__c');
        }
    }
    
    /**
    * <p>
    * This method used to create relation between the PGV and the PGQ.
    *
    * Note : This method is static future method inorder to avoid mixed DML due to platform restriction.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    @future
    public static void createGroupMembers(Set<Id> groupIds, id publicgrpId)
    {
        List<GroupMember> newGroupMembers = new List<GroupMember>();
        for (Id groupId : groupIds) 
        {
            if(Schema.sObjectType.GroupMember.fields.GroupId.isCreateable() && Schema.sObjectType.GroupMember.fields.UserOrGroupId.isCreateable())
            newGroupMembers.add(new GroupMember(GroupId = publicgrpId, UserOrGroupId = groupId));
        }
        insert newGroupMembers;   
    }
    
    /**
    * Returns List<Queue_View__c> creates queue view records
    * <p>
    * This method used to create queue view record.
    *
    * @param    workQueueId                         Work Queue Id
    * @param    QvNamesToManagerIDs                 Map of QV Names to Manager Ids
    * @param    ViewNameToGroups                    Map of View Names and Groups
    * @return   List<Queue_View__c>
    */
    private List<Queue_View__c> createQueueView(Work_Queue_Setup__C wqs, map<String,Id> QvNamesToManagerIDs, Map<String, Group> ViewNameToGroups)
    {
        List<Queue_View__c> qvRecords = new List<Queue_View__c>();
        for(String eachRec: QvNamesToManagerIDs.KeySet())
        {
            Queue_View__c qv = new Queue_View__c();
            if(Schema.sObjectType.Queue_View__c.fields.Name.isCreateable())
            qv.Name = eachRec;
            if(Schema.sObjectType.Queue_View__c.fields.Queue_View_Name__c.isCreateable())
            qv.Queue_View_Name__c = eachRec;
            if(eachRec != GLOBAL_CONSTANT_HUM.HUM_ALLOTHERVIEW && Schema.sObjectType.Queue_View__c.fields.View_Manager__c.isCreateable()) 
            qv.View_Manager__c = QvNamesToManagerIDs.get(eachRec);
            if(Schema.sObjectType.Queue_View__c.fields.Work_Queue_Name__c.isCreateable())
            qv.Work_Queue_Name__c = wqs.id;
            if(Schema.sObjectType.Queue_View__c.fields.IsActive__c.isCreateable())
            qv.IsActive__c = false;
            if(Schema.sObjectType.Queue_View__c.fields.Public_Group_View_Id__c.isCreateable())
            qv.Public_Group_View_Id__c = ViewNameToGroups.get(eachRec).Id;
            if(Schema.sObjectType.Queue_View__c.fields.Group_Name__c.isCreateable())
            qv.Group_Name__c = ViewNameToGroups.get(eachRec).Name;
            if(Schema.sObjectType.Queue_View__c.fields.Queue_Name__c.isCreateable())
            qv.Queue_Name__c = wqs.Name;
            if(Schema.sObjectType.Queue_View__c.fields.Service_Center__c.isCreateable())
            qv.Service_Center__c = wqs.Service_Center__c;
            if(Schema.sObjectType.Queue_View__c.fields.Department__c.isCreateable())
            qv.Department__c = wqs.Department__c;
            
            qvRecords.add(qv);
        }
        return qvRecords;
    }
    
    /**
    * <p>
    * This method used to autopopulate PGV Name.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    public void updateQueueView(WorkQueueCreation_C_HUM cls)
    {
        cls.workQueueSetup.Name = (cls.workQueueSetup.Name == NULL ?'' : cls.workQueueSetup.Name);
        cls.sPGVName = cls.workQueueSetup.Name + ' ' + cls.queueView.Name;
        if(cls.sPGVName.length() > 36)
            cls.sPGVName = cls.sPGVName.subString(0,36);
        cls.sPGVName += ' ' + 'PGV';
        
    }
    
    /**
    * <p>
    * This method used to call private methods to run while test class is running.
    *
    * @param    cls     Instance of Class WorkQueueCreation_C_HUM
    * @return   void
    */
    
    public void callMethod(WorkQueueCreation_C_HUM cls)
    {
        if(Test.isRunningTest())
        {
            createQueueViews(cls);
            createWorkQueueTopic(cls);
            createWorkQueueTopics(cls);
        }
    }
}