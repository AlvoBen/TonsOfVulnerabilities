/************************************************************************************************************************
Apex class Name    : ChangeCaseOwner_C_HUM_LWC
Version            : 1.0
Function           : This class is used to drive the custom functionality of bulk transfer of Cases in LWC.
Created On         : December 29 2021
Test Class         : ChangeCaseOwner_T_HUM_LWC

    Modification Log
*   Developer                   Code Review         Date                Description
* -------------------------------------------------------------------------------------------------------------------------                
*   Vijaya Lakshmi Tummala/
	Shailesh Bagade                					12/29/2021          Original Version
***************************************************************************************************************************/
public with sharing class ChangeCaseOwner_C_HUM_LWC
{
    private static Set<string> setServiceCenter = new Set<string>();
    private static Set<string> setDepartment = new Set<string>();
    private static Integer iLimitValue = HumConstants_LWC.LIMITVALUE_HUM;
    public  static Map<String, Work_Queue_Setup__c> mapWorkQueueSetup ;
    public static Map<String,Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup;
    private static Set<String> setPharRecType = new Set<String>(HumConstants_LWC.Pharmacy_Record_Type_Name);
    private static List<PermissionSetAssignment> lstRSOHPPermissionSet;    
    public static Boolean bPharmacyUserSupervisor ;
    public static string caseCom;
    public static string sFilter;
    public static Boolean bswitch_US2578074;
    /**
    * Constructor for HUMChangeCaseOwnerController
    *
    */
    
    public static void getChangeCaseOwner_C_HUM_LWC()
    {  
        
        bswitch_US2578074=HUMUtilityHelper_LWC.isCRMFunctionalityON('2578074');
        mapWorkQueueSetup = New Map < String, Work_Queue_Setup__c>([Select Name,id,Classification_Type__r.Name,Classification_Type__c, CokY__c, Department__c, DST__c, General__c, Contract_Protected_Data__c,HO_Dental__c, HO_Medical__c, Public_Group_Name__c, Queue_Manager__c, Service_Center__c, TenantID__c, Work_Queue_Name__c,OwnerId from Work_Queue_Setup__c 
                                                                                                       Where IsActive__c = TRUE limit :iLimitValue]);
        bTopicMan = false;
        mapServCenDeptWorkQueueSetup = new Map<String,Work_Queue_Setup__c>();
        lstRSOHPPermissionSet = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId=:UserInfo.getUserId() AND PermissionSet.Name=:HumConstants_LWC.RSO_HP_PermissionSet_Name LIMIT 1];
        bRSOHPFlag = false;
        bShowTopic = true;
        bPharmacyUserSupervisor = true;
        hPharmacyUser = false;
        if(HUMUtilityHelper_LWC.isPharmacyUser() != HumConstants_LWC.NONPHARMACY_USER_HUM)  hPharmacyUser = true;  
    }
    public static Boolean bShowTopic;
    public static Boolean bTopicMan;
    public static String sOwnerName;
    public static Id sOwnerId ;
    public static String sServiceCenter;
    public static String sDepartment;
    public static String sTopic;
    public static String sTransferReason;
    public static String sGrpQueueName;
    public static String sSelectedWorkQueue;
    public static String sPublicGroupName;
    public static List<String> sCenters = new List<String>{HumConstants_LWC.SERVICE_CENTER_HUMANA_PHARMACY,HumConstants_LWC.SERVICE_CENTER_RX};
    public static List<String> wqueues = new List<String>{HumConstants_LWC.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,HumConstants_LWC.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,HumConstants_LWC.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,HumConstants_LWC.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS};
    public static List<String> wTopics = new List<String>{HumConstants_LWC.TOPIC_SPECIAL_INVESTIGATIONS,HumConstants_LWC.TOPIC_ORAL_GRIEVANCE,HumConstants_LWC.NONE_HUM};
    public static boolean hPharmacyUser ;
    public static boolean bRSOHPFlag;
    public static String JsonMap;
    public static Work_Queue_setup__c oWQS;
    

    /**
    * Returns all service centers present in the system
    * <p>
    * Method to return all service centers present in the system
    *
    * @param     NULL
    * @return    List <String>                                                                                   
    */
	@AuraEnabled
    public Static List <String> getServiceCenters() 
    {
        setServiceCenter = new Set<String>();
        List<String> lstOptions = new List<String>();
        List<String> lstServiceValues = new List<String>();
        for (String sWorkQueue : mapWorkQueueSetup.keySet()) 
        {
            if(ChangeCaseOwner_C_HUM_LWC.getCaseIndicatorStatus(sWorkQueue))  
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
        lstServiceValues = HUMUtilityHelper_LWC.doSort(setServiceCenter);
        for(integer i=0; i < lstServiceValues.size(); i++)
        {
            lstOptions.add(lstServiceValues[i]);
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
    
    public static boolean getCaseIndicatorStatus(String sQueueName)
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
    * @return    List <String>                                                                                   
    */
    @AuraEnabled
    public static list <String> getDepartments()
    {
        setDepartment = new Set<String>();
        List<String> lstOptions = new List<String>();
        List<String> lstDepValues = new List<String>();
        for(string sWorkQueue : mapWorkQueueSetup.keySet())
        {
            if(sServiceCenter !=null && mapWorkQueueSetup.get(sWorkQueue).Service_Center__c.equalsIgnoreCase(sServiceCenter) &&
                    !string.isBlank(mapWorkQueueSetup.get(sWorkQueue).Department__c))
            {     
                if(ChangeCaseOwner_C_HUM_LWC.getCaseIndicatorStatus(sWorkQueue))
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
        lstDepValues = HUMUtilityHelper_LWC.doSort(setDepartment);
        for(integer i=0; i < lstDepValues.size(); i++)
        {
            lstOptions.add(lstDepValues[i]);
        }
        
         return lstOptions;
    } 

    /**
    * Returns list <String>
    * <p>
    * Method to return all topics related to work queue
    *
    * @param     NULL
    * @return    List <String>                                                                                   
    */
    @AuraEnabled
    public static list <String> getTopics()
    {
        bTopicMan = false;
        String sWorkQueueSetUpName=null;
        Set<String> setTopic = new Set<String>();
        List<String> lstOptions = new List<String>();
        List<String> lstTopicValues = new List<String>();
        lstOptions.add(HumConstants_LWC.CHANGECASEOWNER_SELECTTOPIC_HUM);  
        if((String.isBlank(sServiceCenter) && String.isBlank(sDepartment)) || (!sServiceCenter.equalsIgnoreCase(HumConstants_LWC.NONE_HUM) 
        && sDepartment.equalsIgnoreCase(HumConstants_LWC.NONE_HUM)))   return lstOptions;
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
            lstTopicValues = HUMUtilityHelper_LWC.doSort(setTopic);
            for(integer i=0; i < lstTopicValues.size(); i++)
            {
                lstOptions.add(lstTopicValues[i]);
            }
        }  
        if (lstOptions.isEmpty()) 
        {
            bShowTopic = true;
        }
        else
        {
            bShowTopic = false;
        }   
        
        return lstOptions;
    }    

    /* Method to poulate queue name
    *
    * @param     NULL
    * @return    void 
    */
    
    public static String populateQueueName()
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
            return JsonMap;
        }
        catch(Exception ex)
        {
             HUMExceptionHelper.logErrors(ex, 'HUMChangeCaseOwnerController', 'populateQueueName');
             return null;
        }        
    }
 
    /**
    * Returns list of log node
    * <p>
    * Method to create list of log notes 
    *
    * @param  NA
    * @return list<String> 
    */
    @AuraEnabled(cacheable=true)
    public static list<String> getTransferReason()
    {
        List<String> lstTransferReason = new List<String>();
		Schema.DescribeFieldResult transferReason = case.Transfer_Reason__c.getDescribe();
		List<Schema.PicklistEntry> tr = transferReason.getPicklistValues();
		for( Schema.PicklistEntry pickListVal : tr){
			lstTransferReason.add(pickListVal.getValue());
		}  
        return lstTransferReason;
    }

    @AuraEnabled
    public static picklistWrappper getPicklistValuesWrp(String sFilter, String sServiceCenter, String sDepartment, Boolean onDeptClicked)
    {
        ChangeCaseOwner_C_HUM_LWC.sFilter = sFilter;
        ChangeCaseOwner_C_HUM_LWC.sServiceCenter = sServiceCenter;
        ChangeCaseOwner_C_HUM_LWC.sDepartment = sDepartment;
        getChangeCaseOwner_C_HUM_LWC();
        picklistWrappper wrp = new picklistWrappper();
        wrp.cServiceCenter = getServiceCenters();
        wrp.cDepartment = getDepartments();
        wrp.cTopic = getTopics();
        wrp.cTransferReason = getTransferReason();
        if(onDeptClicked){
            //fill Json from populateQueueName
            wrp.JsonMap =  populateQueueName();
        }
        return wrp;
    }
    public class picklistWrappper {
        @AuraEnabled
        public List<String> cServiceCenter;
        @AuraEnabled
        public List<String> cDepartment;
        @AuraEnabled
        public List<String> cTopic;
        @AuraEnabled
        public List<String> cTransferReason;
        @AuraEnabled
        public String JsonMap; 
    }
}