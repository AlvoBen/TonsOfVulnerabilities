/*****************************************************************************************
Apex class Name    : ChangeCaseOwner_C_HUM
Version            : 1.0
Function           : Controller to VF Component:ChangeCaseOwner_CMP_HUM.
                    This class is used to drive the custom functionality of bulk transfer of Cases.
Created Date       : 22 July 2020
Test Class         : ChangeCaseOwner_T_HUM

    Modification Log
 *   Developer                       Date               Description
 * -------------------------------------------------------------------------------------------------------------------------------                
 *   Prudhvi Pamarthi                07/22/2020          Original Version
 *   Vinay Lingegowda                02/12/2021          USER STORY 1748357 PR00091574 - IR 5050 - Add a Transfer Reason in Case/Tasks page - Supervisor Bulk Transfer (CRM)
 *   Tummala Vijaya Lakshmi			 08/27/2021			 USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App
 *   Tummala Vijaya Lakshmi			 09/30/2021			 USER STORY 2578061  -T1PRJ0002980/PR00095219 - MF 5 - Contracted Protected Data for Q/V App -CPD Switch
 **************************************************************************************************************************************************************************************************************************************************************************/
public with sharing class ChangeCaseOwner_C_HUM
{
    private Set<string> setServiceCenter = new Set<string>();
    private Set<string> setDepartment = new Set<string>();
    private Integer iLimitValue = GLOBAL_CONSTANT_HUM.LIMITVALUE_HUM;
    public Map<String, Work_Queue_Setup__c> mapWorkQueueSetup{get;set;}
    public Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup{get;set;}
    private Set<String> setPharRecType = new Set<String>(GLOBAL_CONSTANT_HUM.Pharmacy_Record_Type_Name);
    private List<PermissionSetAssignment> lstRSOHPPermissionSet;    
    public Boolean bPharmacyUserSupervisor {get; set;}
    public string caseCom{Get;Set;}
    Public string sFilter{Get;Set;}
    Public Boolean bswitch_US2578074 {Get;Set;}
    /**
    * Constructor for HUMChangeCaseOwnerController
    *
    */
    public ChangeCaseOwner_C_HUM()
    {  
        bswitch_US2578074 = HUMUtilityHelper.isCRMFunctionalityON('2578074');
        mapWorkQueueSetup = New Map < String, Work_Queue_Setup__c > ([Select Name, id, Classification_Type__r.Name, Classification_Type__c, CokY__c, Department__c, DST__c, General__c, Contract_Protected_Data__c, HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c, OwnerId from Work_Queue_Setup__c 
                                                                                                       Where IsActive__c = TRUE limit :iLimitValue]);
        bTopicMan = false;
        mapServCenDeptWorkQueueSetup = new Map<String,Work_Queue_Setup__c>();
        lstRSOHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:GLOBAL_CONSTANT_HUM.RSO_HP_PermissionSet_Name LIMIT 1];
        bRSOHPFlag = false;
        bShowTopic = true;
        bPharmacyUserSupervisor = true;
        hPharmacyUser = false;
        if(Pharmacy_H_HUM.isPharmacyUser() != GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM)  hPharmacyUser = true;  
    }
    public Boolean bShowTopic{get;set;}
    public Boolean bTopicMan{get;set;}
    public String sOwnerName {get;set;}
    public Id sOwnerId {get;set;}
    public String sServiceCenter {get;set;}
    public String sDepartment {get;set;}
    public String sTopic {get;set;}  
    public String sTransferReason {get;set;}
    public String sGrpQueueName{get;set;}
    public String sSelectedWorkQueue {get;set;}
    public String sPublicGroupName{get;set;}  
    public List<String> sCenters = new List<String>{GLOBAL_CONSTANT_HUM.SERVICE_CENTER_HUMANA_PHARMACY,GLOBAL_CONSTANT_HUM.SERVICE_CENTER_RX};
    public List<String> wqueues = new List<String>{GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public List<String> wTopics = new List<String>{GLOBAL_CONSTANT_HUM.TOPIC_SPECIAL_INVESTIGATIONS,GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE,GLOBAL_CONSTANT_HUM.NONE_HUM};
    public boolean hPharmacyUser {get;set;} 
    public boolean bRSOHPFlag{get;set;}
    public String JsonMap{get;set;} 
    public Work_Queue_setup__c oWQS{get;set;}
    

    /**
    * Returns all service centers present in the system
    * <p>
    * Method to return all service centers present in the system
    *
    * @param     NULL
    * @return    List <SelectOption>                                                                                   
    */
    public List <SelectOption> getServiceCenters() 
    {
        setServiceCenter = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstServiceValues = new List<String>();
        for (String sWorkQueue : mapWorkQueueSetup.keySet()) 
        {
            if(getCaseIndicatorStatus(sWorkQueue))  
            {
              if(!hPharmacyUser)
              { 
                setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c); 
                mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));
              }
              else
              {  
                if(wqueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || sCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c))
                {
                  setServiceCenter.add(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c); 
                  mapServCenDeptWorkQueueSetup.put(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c +'#' + mapWorkQueueSetup.get(sWorkQueue).Department__c,mapWorkQueueSetup.get(sWorkQueue));
                }
              }
            }          
        }
        lstServiceValues = HUMUtilityHelper.doSort(setServiceCenter);
        for(integer i=0; i < lstServiceValues.size(); i++)
        {
            lstOptions.add(new SelectOption(lstServiceValues[i], lstServiceValues[i]));
        }
        if (!lstOptions.isEmpty()) 
        {
            lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));
        }
        else
        {
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));    
        }
        return lstOptions;
    }

    /**
    * Returns Boolean
    * <p>
    * Method to return case indicator based on queue
    *
    * @param     String     sQueuename
    * @return    Boolean                                                                                   
    */
    public boolean getCaseIndicatorStatus(String sQueueName)
    {
        Boolean bCaseIndicatorStatus;
        bCaseIndicatorStatus = False;
        If (sFilter.equalsIgnoreCase('General'))
        bCaseIndicatorStatus = mapWorkQueueSetup.get(sQueueName).General__c;
        Else
                    If (sFilter.equalsIgnoreCase('Home Office Dental'))
        bCaseIndicatorStatus = mapWorkQueueSetup.get(sQueueName).HO_Dental__c;
        Else
                        If (sFilter.equalsIgnoreCase('Home Office All Other'))
            bCaseIndicatorStatus = mapWorkQueueSetup.get(sQueueName).HO_Medical__c; 
        Else
                            If (sFilter.equalsIgnoreCase('Contract Protected Data') && (bswitch_US2578074) )
        bCaseIndicatorStatus = mapWorkQueueSetup.get(sQueueName).Contract_Protected_Data__c;
        Return bCaseIndicatorStatus;
    }

    /**
    * Returns all Departments present in the system
    * <p>
    * Method to return all departments present in the system
    *
    * @param     NULL
    * @return    List <SelectOption>                                                                                   
    */
    public list <Selectoption> getDepartments()
    {
        setDepartment = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstDepValues = new List<String>();
        for(string sWorkQueue : mapWorkQueueSetup.keySet())
        {
            if(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                    !string.isBlank(mapWorkQueueSetup.get(sWorkQueue).Department__c))
            {     
                if(getCaseIndicatorStatus(sWorkQueue))
                {
                  if(!hPharmacyUser) 
                  { 
                    setDepartment.add(mapWorkQueueSetup.get(sWorkQueue).Department__c);
                  }
                  else
                  {
                    if(wqueues.contains(mapWorkQueueSetup.get(sWorkQueue).Name) || sCenters.contains(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c))
                    {
                      setDepartment.add(mapWorkQueueSetup.get(sWorkQueue).Department__c);
                    }
                  }
                }
            }
        }
        lstDepValues = HUMUtilityHelper.doSort(setDepartment);
        for(integer i=0; i < lstDepValues.size(); i++)
        {
            lstOptions.add(new SelectOption(lstDepValues[i], lstDepValues[i]));
        }
        if (lstOptions.isEmpty()) 
        {
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));
        }
        else
        {
            lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.NONE_HUM,GLOBAL_CONSTANT_HUM.NONEDISPLAY_HUM));    
        }
        return lstOptions;
    } 

    /**
    * Returns list <Selectoption>
    * <p>
    * Method to return all topics related to work queue
    *
    * @param     NULL
    * @return    List <SelectOption>                                                                                   
    */
    public list <Selectoption> getTopics()
    {
        this.bTopicMan = false;
        String sWorkQueueSetUpName=null;
        Set<String> setTopic = new Set<String>();
        List<SelectOption> lstOptions = new List<SelectOption>();
        List<String> lstTopicValues = new List<String>();
        lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));  
        if((String.isBlank(sServiceCenter) && String.isBlank(sDepartment)) || (!sServiceCenter.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM) 
        && sDepartment.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.NONE_HUM)))   return lstOptions;
        lstOptions.remove(0);
        for(string sWorkQueue : mapWorkQueueSetup.keySet())
        {
            if(mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                    mapWorkQueueSetup.get(sWorkQueue).Department__c.equalsIgnoreCase(sDepartment))
            {
                sWorkQueueSetUpName = sWorkQueue;
                break;
            }
        }
        List<Work_Queue_Junction__c> lstWorkQueueJunction = [select Work_Queue_Setup_Name__c, Work_Queue_Topic_Name__r.Name from Work_Queue_Junction__c where 
        Work_Queue_Setup_Name__c =: sWorkQueueSetUpName and Work_Queue_Topic_Name__r.IsActive__c = true];
        if(!lstWorkQueueJunction.isEmpty())
        {
            for(Work_Queue_Junction__c oWQJ : lstWorkQueueJunction)
            {
               if(!hPharmacyUser) 
               {  
                 setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
               }
               else
               {
                 if(sCenters.contains(sServiceCenter))
                 {
                   setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                 }
                 else
                 { 
                   if(wTopics.contains(oWQJ.Work_Queue_Topic_Name__r.Name)) 
                   {
                   setTopic.add(oWQJ.Work_Queue_Topic_Name__r.Name); 
                   }
                 }
               }
            }
            lstTopicValues = HUMUtilityHelper.doSort(setTopic);
            for(integer i=0; i < lstTopicValues.size(); i++)
            {
                lstOptions.add(new SelectOption(lstTopicValues[i], lstTopicValues[i]));
            }
        }  
        if (lstOptions.isEmpty()) 
        {
            lstOptions.add(new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
            bShowTopic = true;
        }
        else
        {
            lstOptions.add(0, new SelectOption(GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM, GLOBAL_CONSTANT_HUM.CHANGECASEOWNER_SELECTTOPIC_HUM));
            bShowTopic = false;
        }          
        return lstOptions;
    }    

    /* Method to poulate queue name
    *
    * @param     NULL
    * @return    void 
    */
    public void populateQueueName()
    {
        try
        {
            oWQS = new Work_Queue_setup__c();
            if(mapServCenDeptWorkQueueSetup.containsKey(sServiceCenter + '#' + sDepartment))
            {
                oWQS = mapServCenDeptWorkQueueSetup.get(sServiceCenter + '#' + sDepartment);
                sSelectedWorkQueue = oWQS.Work_Queue_Name__c;
                sPublicGroupName= oWQS.Public_Group_Name__c;
                JsonMap = JSON.serialize(oWQS);
            }
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMChangeCaseOwnerController', 'populateQueueName');
        }        
    }
 
    /**
    * Returns list of log node
    * <p>
    * Method to create list of log notes 
    *
    * @param  NA
    * @return list<SelectOption> 
    */
    public list<SelectOption> getTransferReason()
    {
        List<SelectOption> lstTransferReason = new List<SelectOption>();
		Schema.DescribeFieldResult transferReason = case.Transfer_Reason__c.getDescribe();
		List<Schema.PicklistEntry> tr = transferReason.getPicklistValues();
		for( Schema.PicklistEntry pickListVal : tr){
			lstTransferReason.add(new SelectOption(pickListVal.getValue(), pickListVal.getValue()));
		}  
        return lstTransferReason;
    }
}