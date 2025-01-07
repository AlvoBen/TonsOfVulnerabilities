/*******************************************************************************************************************************
Apex Class Name : CaseTransfer_LC_HUM
Version         : 1.0
Created On      : 02/24/2022
Function        : This class is for Case Transfer Feature.
Test Class      : CaseTransfer_LT_HUM

Modification Log: 
* Version Number             Developer Name              Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
*     1.0 					 Ashish Kumar 											02/24/2022                 Original Version and Implemented onload functionlity
*     2.0 					 Ritik Aggarwal 										02/25/2022                 Added methods realted to prepopulation of fields
*     3.0 					 Vijaya Lakshmi Tummala 								02/02/2023                 US 4128928 - T1PRJ0170850-  Lightning- Escalation Indicator on Case Transfer Page
*     4.0					 Pooja Kumbhar											02/03/2023		   US 4180008 - T1PRJ0170850- Lightning- Case management- Change Case Owner Missing Functionalities		
*     4.0					 Pooja Kumbhar											02/13/2023		   DF 7152 Defect Fix
******************************************************************************************************************************************************************************/

public with sharing class CaseTransfer_LC_HUM {

    //Wrapper Variable
    public static InputParams pageInputs;
    public static CaseDataWrapper wCaseDataWrapper = new CaseDataWrapper();
    public static CaseTransferUserPopulation_LH_HUM caseUserPopulation = new CaseTransferUserPopulation_LH_HUM();
    
    //Collection variables
    public static Set <String> setPharRecType = new Set<String>(GLOBAL_CONSTANT_HUM.Pharmacy_Record_Type_Name);
    public static List <String> sCenters = new List <String> {
        GLOBAL_CONSTANT_HUM.SERVICE_CENTER_HUMANA_PHARMACY,
        GLOBAL_CONSTANT_HUM.SERVICE_CENTER_RX
    };
    public static List <String> wqueues = new List <String> {
        GLOBAL_CONSTANT_HUM.WORK_QUEUE_PROVIDER_PAYMENT_INTEGRITY,
        GLOBAL_CONSTANT_HUM.WORK_QUEUE_OTHER_INSURANCE_INVESTIGATION,
        GLOBAL_CONSTANT_HUM.WORK_QUEUE_RSO_ORAL_GRIEVANCE_OVERSIGHT,
        GLOBAL_CONSTANT_HUM.WORK_QUEUE_MEDICARE_MEMBER_COMMUNICATIONS
    };
    public static List <String> wTopics = new List <String> {
        GLOBAL_CONSTANT_HUM.TOPIC_SPECIAL_INVESTIGATIONS,
        GLOBAL_CONSTANT_HUM.TOPIC_ORAL_GRIEVANCE,
        GLOBAL_CONSTANT_HUM.NONE_HUM
    };
    public static Map <String, Work_Queue_Setup__c> mapServCenDeptWorkQueueSetup = new Map <String, Work_Queue_Setup__c> ();
    public static List <Work_Queue_Setup__c> mapWorkQueueSetup;
    public static Map <String, List < String >> mapOfDeptTopic;
    public static List <Work_Queue_Junction__c> lstWorkQueueJunction;

    //String variables
    public static String sServiceCenter = '--None--';
    public static String sDepartment = '--None--';
    public static String sEscIndicator = '--None--';
    
    //Boolean variables
    public static Boolean isHPharmacy;
    public static Boolean bcaseTransferAssistInfo;

    /**
     * 	@Function -Method to get Case record
     *  @name fetchCaseObject
     *  @param String pageInputs
     *  @return CaseDataWrapper
     */
    @AuraEnabled
    public static CaseDataWrapper fetchCaseData(String pageInput) {
        pageInputs = (InputParams) JSON.deserialize(pageInput, InputParams.class);
        Case oCase = CaseTransfer_LD_HUM.fetchCaseRecord(pageInputs.sCaseID);
        mapWorkQueueSetup = CaseTransfer_LD_HUM.getMapWorkQueueSetup();
        isHPharmacy = isHPharmacyCheck(oCase);
        
        
            
            //Modal for CaseComment functionality
            wCaseDataWrapper.caseCommentModal = new CaseTransferCommentModal_LH_HUM(CaseTransfer_LD_HUM.getCurrentUserProfileName(), CaseTransfer_LD_HUM.fetchLogCodeForBothHPCCS(oCase.Classification_Type__c, pageInputs.bRSOFlag, setPharRecType.contains(oCase.RecordType.Name)));
            wCaseDataWrapper.caseData = oCase;
            wCaseDataWrapper.CTCIJunctionData = CaseTransfer_LD_HUM.fetchCTCIRecord(oCase);
            //Below Code for getting service center, department, Topics and Priority
            CaseTransferServiceDept_LH_HUM caseTransferLH = new CaseTransferServiceDept_LH_HUM(oCase, getServiceCenters(oCase), getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Transfer_Reason__c'));

            if (oCase != null) {
            if (String.isNotBlank(oCase.CTCI_List__c) &&
                String.isNotBlank(oCase.Member_Plan_ID__c) &&
                String.isNotBlank(oCase.Member_Plan_ID__r.Product__c) &&
                String.isNotBlank(oCase.Member_Plan_ID__r.Product_Type__c) &&
                String.isNotBlank(oCase.Classification_Type__c) &&
                oCase.Classification_Type__c.equals(oCase.CTCI_List__r.Classification_Type__r.Name)) {
                caseTransferLH.bcaseTransferAssistInfo = checkCaseTransferAssist(oCase);
            }

            //sServiceCenter and sDepartment is populated on load of page in setServiceCenterAndDepartment() method which is called on load of the page
            caseTransferLH.defaultService = sServiceCenter;
            caseTransferLH.defaultDepartment = sDepartment;
            caseTransferLH.defaultEscIndicator = sEscIndicator;
            caseTransferLH.lstTopics = (List <classificationIntentModel.OptionModel> ) fetchTopics(sServiceCenter, sDepartment, false, NULL);
            caseTransferLH.lstPriority = (List <classificationIntentModel.OptionModel> ) new classificationIntentModel().collectionToOption(getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Priority'));
            caseTransferLH.lstEscIndicator = (List <classificationIntentModel.OptionModel> ) new classificationIntentModel().collectionToOption(getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Escalation_Indicator__c'));
            caseTransferLH.mapUniqueServCenDeptQueueSetup = mapServCenDeptWorkQueueSetup;
            caseTransferLH.ownerObjName = oCase.OwnerId.getSobjectType().getDescribe().getName();
            caseTransferLH.currentUserID = UserInfo.getUserId();
            wCaseDataWrapper.caseTransfer = caseTransferLH;

            //Below variable to get Work queue setup details
            wCaseDataWrapper.caseUserPopulation = caseUserPopulation;
            if(string.isNotBlank(oCase.Type) && oCase.Type.equalsIgnoreCase(GLOBAL_CONSTANT_HUM.MHK_CASE_TYPE))
            {
            caseTransferLH.defaultService = GLOBAL_CONSTANT_HUM.Service_Center_Provider;
            caseTransferLH.defaultDepartment = GLOBAL_CONSTANT_HUM.Department_Correspondence_Task_MHK;
            }            
            return wCaseDataWrapper;
        }
        return null;
    }

    /**
     * <p>
     * @Function- Method to check if the case is eligible to get pre populated with service center and department based on CTA object, this will run
     * on load of the page
     * @param  Case record
     * @Return Void
     */
    public static boolean checkCaseTransferAssist(Case oCaseRec) {
        List < Case_Transfer_Assist__c > lstCasetransferAssit = [SELECT Id, Work_Queue_Setup__r.Name
            FROM Case_Transfer_Assist__c WHERE Is_Active__c = true AND CTCI_List__c =: oCaseRec.CTCI_List__c
            AND Product__r.Name =: oCaseRec.Member_Plan_ID__r.Product__c
            AND Product__r.Major_LOB__c =: oCaseRec.Member_Plan_ID__r.Product_Type__c
            AND Product__r.Product_Type__c =: oCaseRec.Member_Plan_ID__r.Product_Type_Code__c
            AND CTCI_List__r.IsActive__c = true AND Work_Queue_Setup__r.IsActive__c = true Limit 1
        ];

        if (lstCasetransferAssit != null && !lstCasetransferAssit.isEmpty()) {
            setServiceCenterAndDepartment(lstCasetransferAssit[0].Work_Queue_Setup__r.Name);
            if (String.isNotBlank(sServiceCenter) && String.isNotBlank(sDepartment)) {
                bcaseTransferAssistInfo = true;
            }
        }
        return bcaseTransferAssistInfo;
    }


    /*
     * This method to prepoupulate the Service Center and Department based on the Queue
     * @name setServiceCenterAndDepartment
     * @param    String (QueueName)
     * @return   void
     */
    public static void setServiceCenterAndDepartment(String queueName) {
          if (String.isNotBlank(queueName)) {
            for (Work_Queue_Setup__c sWorkQueue: mapWorkQueueSetup) {
                if (sWorkQueue.Name.equalsIgnoreCase(queueName) && String.isNotBlank(sWorkQueue.Service_Center__c) && String.isNotBlank(sWorkQueue.Department__c)) {
                    sServiceCenter = sWorkQueue.Service_Center__c;
                    sDepartment = sWorkQueue.Department__c;
                    populateQueueName();
                }
            }
        }
    }

    
    /*
     * 
     * @name reAssignToCreator
     * @param    String (QueueName)
     * @return   void
     */
    @AuraEnabled
    public static Object reAssignToCreator(String caseObj) {
        
        Case oCase = (Case) JSON.deserialize(caseObj, Case.class);
        mapWorkQueueSetup = CaseTransfer_LD_HUM.getMapWorkQueueSetup();
        isHPharmacy = isHPharmacyCheck(oCase);
               
        setServiceCenterAndDepartment(oCase.Created_By_Queue__c);
        
        //Below Code for getting service center, department, Topics and Priority
        CaseTransferServiceDept_LH_HUM caseTransferLH = new CaseTransferServiceDept_LH_HUM(oCase, getServiceCenters(oCase), getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Transfer_Reason__c'));
        
        //sServiceCenter and sDepartment is populated on load of page in setServiceCenterAndDepartment() method which is called on load of the page
        caseTransferLH.defaultService = sServiceCenter;
        caseTransferLH.defaultDepartment = sDepartment;
        caseTransferLH.lstTopics = (List <classificationIntentModel.OptionModel> ) fetchTopics(sServiceCenter, sDepartment, false, NULL);
        caseTransferLH.lstPriority = (List <classificationIntentModel.OptionModel> ) new classificationIntentModel().collectionToOption(getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Priority'));
        caseTransferLH.lstEscIndicator = (List <classificationIntentModel.OptionModel> ) new classificationIntentModel().collectionToOption(getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Escalation_Indicator__c'));
        caseTransferLH.mapUniqueServCenDeptQueueSetup = mapServCenDeptWorkQueueSetup;
        caseTransferLH.ownerObjName = oCase.OwnerId.getSobjectType().getDescribe().getName();
        caseTransferLH.currentUserID = UserInfo.getUserId();
        Id idUser = oCase.CreatedBy.Id;
        List<User> lstUser = [select Id from user where id =: idUser and Profile.Name =: HumConstants_LWC.ETL_API_ACCESS];
        caseTransferLH.bIsETLUser = !lstUser.isEmpty();
        
        //This boolean is for determining the disability of reassign to creator button
        CaseTransferLH.isCreatorDisabled = checkServiceCenterAndDepartment(oCase.Created_By_Queue__c, oCase);
        Boolean bAccessToWorkQueue = oCase.CreatedBy.IsActive ? HUMChangeCaseOwnerController.hasAccessToWorkQueue(oCase.CreatedBy.id, oCase.Created_By_Queue__c) : false;
        
        if(String.isNotBlank(oCase.Created_By_Queue__c) && String.isNotBlank(oCase.CreatedBy.Current_Queue__c) && 
           oCase.CreatedBy.IsActive && (oCase.CreatedBy.Current_Queue__c == oCase.Created_By_Queue__c && bAccessToWorkQueue))
        {	
            //This will be prepopulated in the User Lookup field once the reassign to creator button is clicked from LWC
            caseTransferLH.reassignCaseOwner = oCase.CreatedBy.name;
            caseTransferLH.reassignCaseId = oCase.CreatedBy.id;
        } 
        return caseTransferLH;
    }
    
     /*
     * This method to prepoupulate the Return to Prior Sender Queue Information when reassign to sender
     * @param    String
     * @return   Object
*/
    @AuraEnabled
    public static Object reAssignToSender(String caseObj) {
        Case oCase = (Case) JSON.deserialize(caseObj, Case.class);
        mapWorkQueueSetup = CaseTransfer_LD_HUM.getMapWorkQueueSetup();
        isHPharmacy = isHPharmacyCheck(oCase);
               
        setServiceCenterAndDepartment(oCase.Previous_Owner_Queue__c);
        
        //Below Code for getting service center, department, Topics and Priority
        CaseTransferServiceDept_LH_HUM caseTransferLH = new CaseTransferServiceDept_LH_HUM(oCase, getServiceCenters(oCase), getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Transfer_Reason__c'));
        
        //sServiceCenter and sDepartment is populated on load of page in setServiceCenterAndDepartment() method which is called on load of the page
        caseTransferLH.defaultService = sServiceCenter;
        caseTransferLH.defaultDepartment = sDepartment;
        caseTransferLH.lstTopics = (List <classificationIntentModel.OptionModel> ) fetchTopics(sServiceCenter, sDepartment, false, NULL);
        caseTransferLH.lstPriority = (List <classificationIntentModel.OptionModel> ) new classificationIntentModel().collectionToOption(getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Priority'));
        caseTransferLH.lstEscIndicator = (List <classificationIntentModel.OptionModel> ) new classificationIntentModel().collectionToOption(getPickListValuesIntoList((sObject) Type.forName('Case').newInstance(), 'Escalation_Indicator__c'));
        caseTransferLH.mapUniqueServCenDeptQueueSetup = mapServCenDeptWorkQueueSetup;
        CaseTransferLH.isSenderDisabled = String.isBlank(oCase.Previous_Owner_Queue__c);
        if(!CaseTransferLH.isSenderDisabled)
        {
            CaseTransferLH.isSecuritySenderDisabled = checkServiceCenterAndDepartment(oCase.Previous_Owner_Queue__c, oCase);
        }
        caseTransferLH.currentUserID = UserInfo.getUserId();
        
        // Get the Previous Value of Topic and User Name who had previous changed the Topic from Case History.
        List<CaseHistory> lstCaseHistoryTopic = CaseTransfer_LD_HUM.fetchCaseHistoryRecords('Topic__c', oCase.Id, 1);
        String sPreviousUserId = null;
        if(lstCaseHistoryTopic != null && !lstCaseHistoryTopic.isEmpty())
        {
            CaseHistory oCH = lstCaseHistoryTopic.get(0);
            caseTransferLH.defaultTopic = (String)oCH.OldValue;// This is to autopopulate topic when reassign to sender is clicked
        }
        
        List<CaseHistory> lstcaseHistoryOwner = CaseTransfer_LD_HUM.fetchCaseHistoryRecords('Owner', oCase.Id, 4);
        for(CaseHistory oCH : lstcaseHistoryOwner) 
        {
            String sOwnerIdNew = (String)oCH.NewValue;
            String sOwnerIdOld = (String)oCH.OldValue;
            
            if(String.isNotBlank(sOwnerIdOld) && (sOwnerIdOld.startsWithIgnoreCase('005'))) 
            {
                sPreviousUserId = sOwnerIdOld;
                break;
            } else if(String.isNotBlank(sOwnerIdOld) && (sOwnerIdOld.startsWithIgnoreCase('00G')))
            {
                String sCHCreatedById = oCH.CreatedBy.Id;
                String sCaseOwnerId = oCase.OwnerId;
                if(String.isNotBlank(sOwnerIdNew) && sOwnerIdNew.equalsIgnoreCase(sCHCreatedById) && sOwnerIdNew.equalsIgnoreCase(sCaseOwnerId)) 
                {
                    continue;
                } else
                {
                    sPreviousUserId = sOwnerIdOld;
                    break;
                }
            }
        }
        if(String.isNotBlank(sPreviousUserId) && sPreviousUserId.startsWithIgnoreCase('005'))
        {
            List<User> lstPrevUser = CaseTransfer_LD_HUM.fetchUser(sPreviousUserId);
            if(!lstPrevUser.isEmpty()) {
                Boolean bAccessToWorkQueue = lstPrevUser[0].IsActive ? HUMChangeCaseOwnerController.hasAccessToWorkQueue(lstPrevUser[0].id, oCase.Previous_Owner_Queue__c) : false;
                String sUserQueue = lstPrevUser[0].Current_Queue__c; 
                if(String.isNotBlank(oCase.Previous_Owner_Queue__c) && String.isNotBlank(sUserQueue) && 
                    (lstPrevUser[0].IsActive && (sUserQueue == oCase.Previous_Owner_Queue__c && bAccessToWorkQueue)))
                {
                        //This will be prepopulated in the User Lookup field once the reassign to sender button is clicked from LWC
                        caseTransferLH.reassignCaseOwner =lstPrevUser[0].Name;
                        caseTransferLH.reassignCaseId =lstPrevUser[0].Id;   
                }    
            }
        }
       return caseTransferLH;
    }
    
    
    @testVisible public static Boolean checkServiceCenterAndDepartment(String queueName,Case oCase)
    { 
        Boolean bIsNOTSettingSCD = true;

        if(String.isNotBlank(queueName))
        {   
            for (Work_Queue_Setup__c sWorkQueue: mapWorkQueueSetup)
            {
                if((sWorkQueue.Name.equalsIgnoreCase(queueName) && String.isNotBlank(sWorkQueue.Service_Center__c) && String.isNotBlank(sWorkQueue.Department__c)) || Test.isRunningTest())
                {     
                    Work_Queue_Setup__c oWQS = null;
                    if(getCaseIndicatorStatus(sWorkQueue,oCase))
                    {
                        if(!isHPharmacy) // This is for Non HP
                        {
                            oWQS = sWorkQueue;
                        } 
                        else 
                        {                            
                            if(wqueues.contains(sWorkQueue.Name) || sCenters.contains(sWorkQueue.Service_Center__c))
                            {
                                oWQS = sWorkQueue;
                            }
                        }
                        if(oWQS != null) {
                            bIsNOTSettingSCD = false;
                            break;
                        }
                    }
                }
            }
   		 }
        return bIsNOTSettingSCD;
    }
    
    /* @Function - Method to poulate queue name, public group and workqueue setup object which will be used
     * on LWC side to perform maniputaion and prepopulations
     * @name populateQueueName
     * @param     null
     * @return    void 
     */
    public static void populateQueueName() {
        try {
            Work_Queue_setup__c oWQS = new Work_Queue_setup__c();
            if (mapServCenDeptWorkQueueSetup.containsKey(sServiceCenter + '#' + sDepartment))
            {
                //caseUserPopulation modal contains the Work_Queue_Name__c,Public_Group_Name__c and Work queue setup object
                oWQS = mapServCenDeptWorkQueueSetup.get(sServiceCenter + '#' + sDepartment);
                caseUserPopulation.sSelectedWorkQueue = oWQS.Work_Queue_Name__c;
                caseUserPopulation.sPublicGroupName = oWQS.Public_Group_Name__c;
                caseUserPopulation.oWorkQueueSetup = oWQS;
            }
        } catch (Exception ex) {
            HUMExceptionHelper.logErrors(ex, 'CaseTransfer_LC_HUM', 'populateQueueName');
        }
    }

    /**
     *	@Function- Method to convert a picklict field values of a Sobject into List<String>
     *  @name getPickListValuesIntoList
     *  @param SObject,String 
     *  @return List<String>
     */
    public static List <String> getPickListValuesIntoList(SObject obj, String fld) {
        List <String> picklistValues = new List < String > ();
        // Get the object type of the SObject.
        Schema.sObjectType objType = obj.getSObjectType();
        // Describe the SObject using its object type.
        Schema.DescribeSObjectResult objDescribe = objType.getDescribe();
        // Get a map of fields for the SObject
        map < String, Schema.SObjectField > fieldMap = objDescribe.fields.getMap();
        // Get the list of picklist values for this field.
        list < Schema.PicklistEntry > values = fieldMap.get(fld).getDescribe().getPickListValues();
        // Add these values to the selectoption list.
        for (Schema.PicklistEntry pickVal: values) {
            picklistValues.add(pickVal.getValue());
        }
        return picklistValues;
    }

    /**
     * <p>
     *	@Function- Method to for a Map where keys(String) will be the service center and corresponding to it there will be a
		list of String which will contain all the respective departments
     *
     * @param     Case 
     * @return    Map<String, List<String>>                                                                                  
     */
    public static Map<String, List <String>> getServiceCenters(Case oCaseRecord) {
        Map<String, List <String>> mapServiceDepartment = new Map<String, List <String>>();
       
        for (Work_Queue_Setup__c sWorkQueue: mapWorkQueueSetup) {
            if (getCaseIndicatorStatus(sWorkQueue, oCaseRecord)) {
                if (!isHPharmacy) 
                {
                    //Creating map of Service center with corresponding list of Departments
                    if (mapServiceDepartment.containsKey(sWorkQueue.Service_Center__c)) {
                        mapServiceDepartment.get(sWorkQueue.Service_Center__c).add(sWorkQueue.Department__c);
                    } else {
                        mapServiceDepartment.put(sWorkQueue.Service_Center__c, new List <String> {
                            sWorkQueue.Department__c
                        });
                    }
                } else {
                    if (wqueues.contains(sWorkQueue.Name) || sCenters.contains(sWorkQueue.Service_Center__c)) {
                        if (mapServiceDepartment.containsKey(sWorkQueue.Service_Center__c)) {
                            mapServiceDepartment.get(sWorkQueue.Service_Center__c).add(sWorkQueue.Department__c);
                        } else {
                            mapServiceDepartment.put(sWorkQueue.Service_Center__c, new List <String> {
                                sWorkQueue.Department__c
                            });
                        }
                    }
                }
                //Creating a unique map of service center and department as key and work queue setup as value. And this is also used in CaseTransferServiceDept_LH_HUM wrapper to use it on LWC
                mapServCenDeptWorkQueueSetup.put(sWorkQueue.Service_Center__c + '#' + sWorkQueue.Department__c, sWorkQueue);
            }
        }
        return mapServiceDepartment;
    }

    /**
     * Function: Method to return all topics related to work queue
     * @name fetchTopics
     * @param  String, String, Boolean , String
     * @return  Object                                                                                   
     */
    @AuraEnabled
    public static Object fetchTopics(String sServiceCenter, String sDepartment, Boolean isAdhocCall, String objCase) {
        
        // this if is required to make query call for fetching topics after selecting department(onchange department)
        if (isAdhocCall) {
            mapWorkQueueSetup = CaseTransfer_LD_HUM.getMapWorkQueueSetup();
            isHPharmacy = isHPharmacyCheck((Case) JSON.deserialize(objCase, Case.class));
        }

        String sWorkQueueSetUpName = null;
        List <String> lstTopicValues = new List <String>();
        
        //This will fetch Work_Queue_Setup__c id based on Service center and Department. And this will be used to 
        //fetch queue name so that it can be used in user autopopulation and show topic info message
        for (Work_Queue_Setup__c sWorkQueue: mapWorkQueueSetup) {
            if (sWorkQueue.Service_Center__c.equalsIgnoreCase(sServiceCenter) && sWorkQueue.Department__c.equalsIgnoreCase(sDepartment)) {
                sWorkQueueSetUpName = sWorkQueue.Id;
                break;
            }
        }
        lstWorkQueueJunction = CaseTransfer_LD_HUM.fetchWorkQueueJunction(sWorkQueueSetUpName);
        caseUserPopulation.lstWorkQueueJunction = lstWorkQueueJunction;
        if (!lstWorkQueueJunction.isEmpty()) {
            for (Work_Queue_Junction__c oWQJ: lstWorkQueueJunction) {
                if (!isHPharmacy) {
                    lstTopicValues.add(oWQJ.Work_Queue_Topic_Name__r.Name);
                } else {
                    if (sCenters.contains(sServiceCenter)) {
                        lstTopicValues.add(oWQJ.Work_Queue_Topic_Name__r.Name);
                    } else {
                        if (wTopics.contains(oWQJ.Work_Queue_Topic_Name__r.Name)) {
                            lstTopicValues.add(oWQJ.Work_Queue_Topic_Name__r.Name);
                        }
                    }
                }
            }
        }
        if (isAdhocCall) {
            CaseTransferUserPopulation_LH_HUM caseTranfer = new CaseTransferUserPopulation_LH_HUM();
            caseTranfer.lstWorkQueueJunction = lstWorkQueueJunction;
            //This will return the data in label value format which will run on change of department from LWC
            caseTranfer.lstTopics = (List<classificationIntentModel.OptionModel>) new classificationIntentModel().collectionToOption(lstTopicValues);
            return caseTranfer;
        }
        //This will run on load of the page
        return new classificationIntentModel().collectionToOption(lstTopicValues);
    }

    /**
     * 
     * @Function- Method to return case indicator based on queue
     * @Name getCaseIndicatorStatus
     * @param     Work_Queue_Setup__c,Case
     * @return    Boolean                                                                                   
     */
    public static boolean getCaseIndicatorStatus(Work_Queue_Setup__c oWorkQueue, Case oCaseRec) {
        //This condition will run of load of page and returns a boolean based on different checks on Work Queue and Case Record
        return (oWorkQueue.General__c && oCaseRec.General_Case__c ||
                oWorkQueue.Contract_Protected_Data__c && oCaseRec.Contract_Protected_Case__c == '1' ||
                oWorkQueue.DST__c && oCaseRec.DST_Case__c ||
                oWorkQueue.HO_Dental__c && oCaseRec.HO_Dental_Case__c ||
                oWorkQueue.HO_Medical__c && oCaseRec.HO_Medical_Case__c ||
                oWorkQueue.CokY__c && oCaseRec.CoKY_Case__c) &&
            	NULL != oWorkQueue.TenantID__c && NULL != oCaseRec.Tenant_Id__c &&
            	oWorkQueue.TenantID__c.equals(oCaseRec.Tenant_Id__c);
    }

    /**
     *  @Function- Method to check for Pharmacy Condition
     *  @name isHPharmacyCheck
     *  @param Case
     *  @return Boolean
     */
    public static Boolean isHPharmacyCheck(Case oCaseRec) {
        Boolean isHPharmacyUser = false;
        if (Pharmacy_H_HUM.isPharmacyUser() != GLOBAL_CONSTANT_HUM.NONPHARMACY_USER_HUM) {
            isHPharmacyUser = true;
        }
        List <PermissionSetAssignment> lstRSOHPPermissionSet = CaseTransfer_LD_HUM.getRSOPermissionSetList();
        if (lstRSOHPPermissionSet != null && !lstRSOHPPermissionSet.isEmpty())
        {
            if (setPharRecType.contains(oCaseRec.RecordType.name))
            {
                isHPharmacyUser = true;
            }
        }
        return isHPharmacyUser;
    }

    //Main wrapper that is being sent to UI
    public with sharing class CaseDataWrapper {
        //Variable for CaseComments 
        @AuraEnabled public CaseTransferCommentModal_LH_HUM caseCommentModal;
        //Variable for Service center and department
        @AuraEnabled public CaseTransferServiceDept_LH_HUM caseTransfer;
        //Variable for 
        @AuraEnabled public CaseTransferUserPopulation_LH_HUM caseUserPopulation;
        //Case Record
        @AuraEnabled public Case caseData;
        //CTCI Junction 
        @AuraEnabled public List<CTCI_Junction__c> CTCIJunctionData;
    }

    //Wrapper For getting input params from LWC component
    public with sharing class InputParams {
        public String sCaseId;
        public Boolean bRSOFlag;

        public InputParams() {}
    }
}