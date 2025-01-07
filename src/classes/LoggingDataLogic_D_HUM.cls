/*******************************************************************************************************************************************************
Apex Class Name  : LoggingDataLogic_D_HUM
Version          : 1.0
Created Date     : 02/26/2016
Function         : This is data layer class used for logging functionality
Test Class       : Logging_D_T_HUM
*****************************************************************************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    02/26/2016                 Original Version
* Santhi Mandava            30761                    04/01/2016                 Fixed defect 221489.0001. Updated line 28.
* Santhi Mandava            24308                    04/05/2016                 Modified line 29 to exclude CCP/GCP related attachments.
* Santhi Mandava            25231                    04/19/2016                 Updated "retrieveCases" method to fix defect 223172
* Santhi Mandava            25389                    04/20/2016                 updated SOQL query(Line 115) to fix interacting with issue(defect 223338.0001)
* Santhi Mandava            27512                    05/12/2016                 Removed order by in "getAttachmentDetails" method.
* Avinash Choubey           33332                    07/20/2016                 Chnages made as part of REQ- 270351
* Santhi Mandava            33921                    07/27/2016                 Created attachment sub type retrieval logic.
* Santhi Mandava            35034                    08/10/2016                 Fixed defect 230816.0001.
* Manish Kumar              36590                    08/24/2016                 Worked on REQ#275929 for DPaaS error handling.
* Santhi Mandava            40488                    09/20/2016                 Removed "\ " from user selected value before comparision.
* Manish Kumar              59048                    01/27/2017                 Worked on Defect#243750, REQ#296700. Incorporated changes for Datatable
                                                                                in "Existing case" scenario.
* Pradeepkumar Dani         81946                    04/04/2017                 CTCI Data model update REQ - 306337
* Harika Devi Kalidindi     88181                    04/28/2017                 CA Ticket# 6688666 : Displaying UserName instead of network Id in CreatedBy column of Logged Information section 
* Anurag Shah               235263                   05/23/2018          	    Reverting the Resolution changes.
* Pradeep Dani				268872                   08/09/2017                 Logging Regression Defect 368854.0001
* Santhi Mandava                                     10/17/2018                 Implemented platform migration changes 
* Prathyu Tushar            356946                   05/22/2019                 Defect# 417759.0001, Added Methods to retrive Open Pharmacy Record Type cases of account.
* Prathyu Tushar		        					 07/17/2019				    REQ - 39602, added logic to get the case records in ID-27 logging component.
* Santhosh Ganji                                     02/01/2020                 User Story 729006 - Logging Component pages- hiding READ Only Cases, when the HP user selects the 'Existing Case' radio button.
* Ashok Kumar Nutalapati                        	 06/22/2020           		REQ - 1000087 Log 'Invoice Request' Guided Process to Case (RxRF)
* Lakshmi Madduri                                    04/20/2021                 US-1911605 SonarQube Fixes 
* Lakshmi Madduri                                    05/05/2021                 2235919 - Separate constant class for logging framework
* Ravikumar Subbaiyan/Luke Cecil                     09/28/2021                 2676424: LoggingProcessLogic_S_HUM.cls vulnerabilities
**********************************************************************************************************************************************/

public with sharing class LoggingDataLogic_D_HUM
{
     /*
    * Method Name : getLogDetails
    * Description : This method is used to retrieve attachment log records based on case id.
    * Return type : List<Attachment_Log__c>
    * Parameter   : String,String,String
    */
    public static List<Attachment_Log__c> getLogDetails(String sCaseId,String sortExpression, String sortDirection)
    {
        String sSourceSystem = 'CRM';
        String sQuery = 'Select Id,Attachment_Sub_type__c,Created_By__c,CreatedBy.Name,CreatedDate,CreatedByQueue__c from '+
                    'Attachment_Log__c where isDeleted__c = false AND Case__c =: sCaseId AND Source_System__c =:sSourceSystem';

        if(String.isNotBlank(sortExpression) && String.isNotBlank(sortDirection)) {
            sQuery += ' ORDER BY ' + sortExpression + ' ' + sortDirection + ' NULLS LAST';
        }
        sQuery = String.escapeSingleQuotes(sQuery);
        return Database.query(sQuery);
    }
    
    /*
    * Method Name : deleteLogDetails
    * Description : This method is used to delete attachment log record based on id.
    * Return type : Void
    * Parameter   : String
    */
    public static void deleteLogDetails(String sAttchId)
    {
        Attachment_Log__c objDeletedRecord = [Select Id,IsDeleted__c from Attachment_Log__c where Id=:sAttchId];
        If(objDeletedRecord  != null)
        {
            objDeletedRecord.IsDeleted__c = true;
            update objDeletedRecord;
        }
    }
    
    
    /*
    * Method Name : retrieveCases
    * Description : This method is used to retrieve cases based on account id.
    * Return type : List<Case>
    * Parameter   : String,String,String
    */
    public static List<Case> retrieveCases(String accId,String sType,String sUniqueVal, String sortExpression, String sortDirection)
    {
        map<Id, Case> mapCase = NULL;
        List<ID> lstCaseId = new List<ID>();
        Set<Case> setFinalCase = new Set<Case>();
       
        mapCase = retrieveAccountCasesIds(accId);
        String sValu = ''; 
        String sExtnValu = '';       
        if(mapCase != null && !mapCase.isEmpty())
        {
            List<Attachment_Log__c> lstCaseRecordsInAttachmentLog = [Select Id,Attachment_Sub_type__c,Case__c,User_Selected_Value__c,User_Selected_Value_EXT__c,Attachment_Type__c From Attachment_Log__c Where Case__c IN : mapCase.keySet()];
           
            if(String.isNotBlank(sType)) 
            {
                for(Attachment_Log__c attachLogrec : lstCaseRecordsInAttachmentLog) 
                {
                    if(String.isNotBlank(attachLogrec.User_Selected_Value__c)) sValu = attachLogrec.User_Selected_Value__c.remove('\\ ');
                    if(String.isNotBlank(attachLogrec.User_Selected_Value_EXT__c)) sExtnValu = attachLogrec.User_Selected_Value_EXT__c.remove('\\ ');
                   
                    if(attachLogrec.Case__c != null && ((String.isNotBlank(attachLogrec.Attachment_Sub_type__c) && attachLogrec.Attachment_Sub_type__c == sType) ||
                            ((String.isNotBlank(attachLogrec.User_Selected_Value__c) && sValu.contains(sUniqueVal)) ||
                             (String.isNotBlank(attachLogrec.User_Selected_Value_EXT__c ) && sExtnValu.contains(sUniqueVal)))))
                    {
                        setFinalCase.add(mapCase.get(attachLogrec.Case__c));
                    }
                }
            }
        }
        return new List<Case>(setFinalCase);
    }
 
    
    /*
    * Method Name : retrieveAccountCasesIds
    * Description : This method is used to retrieve case Ids based on account id.
    * Return type : List<Case>
    * Parameter   : String
    */
    public static map<Id, Case> retrieveAccountCasesIds(String accId)
    {
        map<Id, Case> mapCase = new map<Id, Case>();       
        if(String.isNotBlank(accId) && accId instanceof Id)
        {
            mapCase = new map<Id, Case>([Select Id, CaseNumber From Case Where AccountId =: accId AND Status != 'Cancelled' ORDER BY CreatedDate DESC NULLS LAST Limit 10000]);
        } 
        return mapCase;
    }
    
    /*
    * Method Name : getCaseCount
    * Description : This method is used to retrieve case countof a particular account in given date range
    * Return type : Integer
    * Parameter   : String, DateTime, DateTime
    */
    public static Integer getCaseCount(String accId, DateTime lastDate, DateTime todayDate)
    {
        Integer caseCount = 0;
        
        if(String.isNotBlank(accId) && accId instanceof Id)
            caseCount = [Select count() From Case Where AccountId =: accId AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate AND Status != 'Cancelled'];
            
        return caseCount;
    }
    
    /*
    * Method Name : retrieveAccountCases
    * Description : This method is used to retrieve cases based on account id, startDate, endDate OR CaseNumber
    * Return type : List<Case>
    * Parameter   : String,DateTime,DateTime,String,Integer
    */
    public static List<Case> retrieveAccountCases(String accId, DateTime lastDate, DateTime todayDate, String caseNumberToSearch, Integer limitVal)
    {
        List<Case> lstCase = new List<Case>();
        Set<String> HPRecType = new Set<String>(LOGGING_CONSTANT_HUM.LIST_PHARMACY_CASE_RECORD_TYPES);
        Set<String> wQueues = new Set<String>(LOGGING_CONSTANT_HUM.LIST_PHARMACY_WORK_QUEUES);
        Set<String> chatQueues = new Set<String>(LOGGING_CONSTANT_HUM.LIST_PHARMACY_WEB_CHAT_WQUEUES);
        Boolean bPharmacyUser = (Pharmacy_H_HUM.isPharmacyUser() != LOGGING_CONSTANT_HUM.STRING_NO);
        
        String sHPLogQuery = 'Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name, Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
                                       'Interacting_About__c,Status,Priority,CreatedDate, ClosedDate,CreatedBy.Name,Created_By_Queue__c,Owner_Queue__c From Case Where AccountId =: accId';
        sHPLogQuery += ' AND ((RecordType.Name in :HPRecType) OR (Product__c = \'MED\' AND (Created_By_Queue__c in :wQueues OR Owner_Queue__c in :wQueues ))';
			//Defect 458100.0001 Fix - MED Group cases with HP Queue should visible in case history table
		sHPLogQuery += ' OR ((Product__c = NULL AND Group_Number_for_policy__c != NULL AND Policy__r.Product__r.Name != NULL AND Policy__r.Product__r.Name = \'MED\') AND (Created_By_Queue__c in :wQueues OR Owner_Queue__c in :wQueues ))';
        sHPLogQuery += ' OR ((Product__c = \'MED\' OR Product__c != \'MED\' OR Product__c = NULL) AND Owner_Queue__c in :chatQueues AND Origin = \'Web Chat\'))';
		
        if(String.isNotBlank(accId) && accId instanceof Id)
        {
            if(String.isNotBlank(caseNumberToSearch))
            {
				if(!bPharmacyUser)
                {
                
					lstCase = [Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name, Product__c,Interacting_With__r.Name,Interacting_With_Type__c,
								Interacting_About__c,Status,Priority,CreatedDate, ClosedDate,CreatedBy.Name,Created_By_Queue__c From Case Where 
								  CaseNumber =: caseNumberToSearch AND AccountId =: accId ];
				}
				else
				{
                   sHPLogQuery = sHPLogQuery + ' AND CaseNumber =: caseNumberToSearch';
                   lstCase = database.query(sHPLogQuery);
                }   
            }
            else
            {
				if(!bPharmacyUser)
                {
					if(limitVal == null)
					{
						lstCase = [Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name, Product__c,Interacting_With__r.Name,Interacting_With_Type__c,
									Interacting_About__c,Status,Priority,CreatedDate, ClosedDate,CreatedBy.Name,Created_By_Queue__c From Case Where 
									AccountId =: accId AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate AND 
									Status != 'Cancelled' ORDER BY CreatedDate DESC NULLS LAST];
					}
					else
					{
						lstCase = [Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name, Product__c,Interacting_With__r.Name,Interacting_With_Type__c,
									Interacting_About__c,Status,Priority,CreatedDate, ClosedDate,CreatedBy.Name,Created_By_Queue__c From Case Where 
									AccountId =: accId AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate AND 
									Status != 'Cancelled' ORDER BY CreatedDate DESC NULLS LAST Limit : limitVal];
					}
				}
				else
                {
                    if(limitVal == null)
                    {
                        sHPLogQuery = sHPLogQuery + ' AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate AND Status != \'Cancelled\' ORDER BY CreatedDate DESC NULLS LAST';
                    }
                    else
                    {
                        sHPLogQuery = sHPLogQuery + ' AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate AND Status != \'Cancelled\' ORDER BY CreatedDate DESC NULLS LAST Limit : limitVal';
                    }
					
					lstCase = database.query(sHPLogQuery);
                }
            }
        }
        
        return lstCase;
    }
    
    /*
    * Method Name : getAccountEnterpriseID
    * Description : This method is used to get account's enterprise id.
    * Return type : String
    * Parameter   : String
    */
    public static String getAccountEnterpriseID(String accId)
    {
        String sKeyValue= '';
        List<Account> lstAcc = [Select Id, Enterprise_ID__c From Account Where Id =: accId];
        
        If(lstAcc != Null && !lstAcc.isEmpty())
        {
            sKeyValue = lstAcc[0].Enterprise_ID__c;
        }
        return sKeyValue;
    } 
    
    /*
    * Method Name : getAccountIdFromPolicy
    * Description : This method is used to get account id based on policy id.
    * Return type : String
    * Parameter   : String
    */
    public static String getAccountIdFromPolicy(String sObjId)
    {
        String accId = '';
        Policy__c objPol = [Select Group_Name__c from Policy__c where Id =:sObjId];
        
        If(objPol != Null)
        {
            accId = objPol.Group_Name__c;
        }
        return accId;
    } 
    
    /*
    * Method Name : getAccountIdFromPolicyMember
    * Description : This method is used to get account id based on policy member id.
    * Return type : String
    * Parameter   : String
    */
    public static String getAccountIdFromPolicyMember(String sObjId)
    {
        String accId = '';
        Policy_Member__c objPolMember = [Select Member__c from Policy_Member__c where Id =:sObjId] ;
        
        If(objPolMember != Null)
        {
            accId = objPolMember.Member__c ;
        }
        return accId;
    }
    
    /*
    * Method Name : getCaseNumber
    * Description : This method is used to get case number based on case id.
    * Return type : String
    * Parameter   : String
    */
    public static String getCaseNumber(String sCaseId)
    {
        String sCaseNumber ='';
        Case objCase =  [Select CaseNumber from Case where Id=:sCaseId];
        If(objCase != Null)
        {
            sCaseNumber = objCase.CaseNumber;
        }
        return sCaseNumber;
    }
    
    /*
    * Method Name : getCaseDetails
    * Description : This method is used to get case details like case owner,created by queue ect.
    * Return type : Case
    * Parameter   : String
    */
    public static Case getCaseDetails(String sSelecCaseId)
    {
        return [Select Case_Owner__c,Owner_Queue__c from Case where Id =:sSelecCaseId];
    }
    
    /*
    * Method Name : getUserCurrentQueue
    * Description : This method is used to get logged in users current queue value.
    * Return type : String
    * Parameter   : NA
    */
    public static String getUserCurrentQueue()
    {
        String sUserQueue = '';
        User ObjUser = [Select Current_Queue__c from User where Id =:UserInfo.getUserId()];
        If(ObjUser != null) sUserQueue = ObjUser.Current_Queue__c;
        return sUserQueue;
    }
    
    /*
    * Method Name : getAttachmentDetails
    * Description : This method is used to get attachment details
    * Return type : List<Attachment>
    * Parameter   : String, Boolean
    */
    public static List<Attachment> getAttachmentDetails(String sAttachLogId)
    {
        List<Attachment> lstobj = new List<Attachment>();
        lstobj  = [SELECT Id,CreatedBy.Name,CreatedDate,ContentType,Body,IsEncrypted FROM Attachment Where ParentId =:sAttachLogId];
        return lstobj ;
    }
    
    /*
    * Method Name : insertAttachementLogRecord
    * Description : This method is used to insert attachement log record.
    * Return type : Id
    * Parameter   : Attachment_Log__c 
    */
    public static Id insertAttachementLogRecord(Attachment_Log__c objAttachLog)
    {    
        if(Schema.sObjectType.Attachment_Log__c.isCreateable()){
			
			insert objAttachLog;
		
		}
        return objAttachLog.Id;
    }
    
    /*
    * Method Name : insertAttachementRecord
    * Description : This method is used to insert attachement record.
    * Return type : Void
    * Parameter   : Attachment 
    */
    public static void insertAttachementRecord(Attachment attachRec)
    {
        insert attachRec;
    }
    
    /*
    * Method Name : getUser
    * Description : This method is used to get user details
    * Return type : User
    * Parameter   : Id
    */
    public static User getUser(Id userId)
    {
        if(String.isNotBlank(userId))    return [Select Id, Network_User_Id__c From User Where Id =: userId];
        return NULL;
    }
    
    /*
    * Method Name : getAttachmentRecord
    * Description : This method is used to get Attachment Log details
    * Return type : List<Attachment_Log__c>
    * Parameter   : Id
    */
    public static List<Attachment_Log__c> getAttachmentRecord(Id attachId)
    {
        if(String.isNotBlank(attachId))    return [Select Id,Attachment_Sub_type__c, User_Selected_Value__c, User_Selected_Value_EXT__c From Attachment_Log__c Where Id =: attachId];
        return new List<Attachment_Log__c>();
    }
    
    /*
    * Method Name : updateAttachmentRecord
    * Description : This method is used to update Attachment_Log details
    * Return type : void
    * Parameter   : List<Attachment_Log__c>
    */
    public static void updateAttachmentRecord(List<Attachment_Log__c> recToUpdate)
    {
        if(recToUpdate != null)    update recToUpdate;
    }
    
    /*
    * Method Name : getUserQueue
    * Description : This method is used to get User's current queue
    * Return type : string
    * Parameter   : Id
    */
    public static String getUserQueue(Id userId)
    {
        if(String.isNotBlank(userId))    return [Select Current_Queue__c From User Where Id =: userId].Current_Queue__c;
        return '';
    }
    
    /*
    * Method Name : getErrorLogObject
    * Description : This method is used to get error log record base don id
    * Return type : Error_Log__c
    * Parameter   : Id
    */
    public static Error_Log__c getErrorLogObject(Id errId)
    {
       Error_Log__c errorLog = [Select Name, Error_Message__c from Error_Log__c where Id =: errId];
       return errorLog;
    }

    /*
    * Method Name : retrievePharmacyOrderCases
    * Description : This method is used to retrieve cases based on policy member id, startDate, endDate OR CaseNumber
    * Return type : List<Case>
    * Parameter   : String,DateTime,DateTime,String,Integer
    */
    public static List<Case> retrievePharmacyOrderCases(String accId, DateTime lastDate, DateTime todayDate, String caseNumberToSearch, Integer limitVal)
    {
        
        Id userProfileId = userinfo.getProfileId();
		Id CCSProfileId = [Select id from Profile where Name =: LOGGING_CONSTANT_HUM.STRING_CUSTOMER_CARE_SPECIALIST].id;
		Id CCSuprProfileId = [Select id from Profile where Name = :LOGGING_CONSTANT_HUM.STRING_CUSTOMER_CARE_SUPERVISOR].id;
		Id HPSProfileId = [Select id from Profile where Name = :LOGGING_CONSTANT_HUM.STRING_HUMANA_PHARMACY_SPECIALIST].id;
        List<Case> lstCase = new List<Case>();
        Set<String> setPharRecType = new Set<String>(LOGGING_CONSTANT_HUM.LIST_PHARMACY_CASE_RECORD_TYPES);
        Set<String> setOwnerQueue = new Set<String>(LOGGING_CONSTANT_HUM.LIST_PHARMACY_WORK_QUEUES);
        Set<String> setOwnerQueueWebChat = new Set<String>(LOGGING_CONSTANT_HUM.LIST_PHARMACY_WEB_CHAT_WQUEUES);
        
        
        if(String.isNotBlank(accId) && accId instanceof Id)
        {
            String soqlQuery = 'Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name, Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
                               'Interacting_About__c,Status,Priority,CreatedDate, ClosedDate,CreatedBy.Name,Created_By_Queue__c From Case where AccountId =: accId AND Status != \'Cancelled\'';
            
            if(HPSProfileId != NULL && userProfileId == HPSProfileId){
               soqlQuery += ' AND ((Product__c = \'MED\' AND RecordType.Name in :setPharRecType)';
               soqlQuery += ' OR (Product__c = \'MED\' AND RecordType.Name not in :setPharRecType AND Owner_Queue__c in :setOwnerQueue)';
               soqlQuery += ' OR (Product__c = \'MED\' AND RecordType.Name not in :setPharRecType AND Owner_Queue__c in :setOwnerQueueWebChat AND Origin = \'Web Chat\')';
               soqlQuery += ' OR (Product__c != \'MED\' AND Owner_Queue__c in :setOwnerQueueWebChat AND Origin = \'Web Chat\')'; 
               soqlQuery += ' OR (Product__c = NULL AND RecordType.Name in :setPharRecType)';
               soqlQuery += ' OR (Product__c = NULL AND RecordType.Name not in :setPharRecType AND Owner_Queue__c in :setOwnerQueueWebChat AND Origin = \'Web Chat\'))';
                
            }    
       
            if(String.isNotBlank(caseNumberToSearch))
            {
                soqlQuery += ' AND CaseNumber =: caseNumberToSearch AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate';
            }
            else
            {
                if(limitVal == null)
                {
                    soqlQuery += ' AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate ORDER BY CreatedDate DESC NULLS LAST';
                }
                else
                {
                    soqlQuery += ' AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate ORDER BY CreatedDate DESC NULLS LAST Limit : limitVal';
                }
            }
            lstCase = database.query(soqlQuery);
        }
        
        return lstCase;
    }
	
	/*
    * Method Name : retrievePharmacyInvoiceCases
    * Description : This method is used to retrieve cases based on policy member id, startDate, endDate OR CaseNumber
    * Return type : List<Case>
    * Parameter   : String,DateTime,DateTime,String,Integer
    */
    public static List<Case> retrievePharmacyInvoiceCases(String accId, String sObjectId, DateTime lastDate, DateTime todayDate, String caseNumberToSearch, Integer limitVal)
    {
        Id userId = userinfo.getUserId();
        List<Case> lstCase = new List<Case>();        
        
        if(String.isNotBlank(accId) && accId instanceof Id)
        {
            String soqlQuery = 'Select Id,CaseNumber,Origin,Type,CTCI_List__r.Classification__c,CTCI_List__r.Classification__r.Name,CTCI_List__r.Intent__c,CTCI_List__r.Intent__r.Name, Product__c,Interacting_With__r.Name,Interacting_With_Type__c,'+
                               'Interacting_About__c,Status,Priority,CreatedDate, ClosedDate,CreatedBy.Name,Created_By_Queue__c From Case where AccountId =: accId AND Policy_Member__c =: sObjectId AND Status != \'Cancelled\' AND Status != \'Closed\' AND OwnerId = :userId';

            if(String.isNotBlank(caseNumberToSearch))
            {
                soqlQuery += ' AND CaseNumber =: caseNumberToSearch AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate';
            }
            else
            {
                if(limitVal == null)
                {
                    soqlQuery += ' AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate ORDER BY CreatedDate DESC NULLS LAST';
                }
                else
                {
                    soqlQuery += ' AND CreatedDate >=: lastDate AND CreatedDate <=: todayDate ORDER BY CreatedDate DESC NULLS LAST Limit : limitVal';
                }
            }
            lstCase = database.query(soqlQuery);
        }
        
        return lstCase;
    }
}