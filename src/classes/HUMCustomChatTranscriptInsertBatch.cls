/***************************************************************************************************************************************************************************
Apex Class Name  : HUMCustomChatTranscriptInsertBatch
Version          : 1.0
Created Date     : May 26, 2015
Function         : Class to create custom chat transcript record
Test Class       : HUMCustomChatTranscriptInsertBatchTest
****************************************************************************************************************************************************************************
Modification Log:
* Developer Name            Code Review             Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Sagar Tapadia               23562                05/26/2015                  CA5027355: Live chat Transcript fix
* Sagar Tapadia               23747                06/09/2015                  QAS Error: Attempt to Dereference Null Object Fix. 
* Pradeepkumar Dani           30060                02/17/2016                  Added changes for formating the Transcript REQ - 224011 / CA 5272746
* Praveen Kumar Parimi        30060                02/18/2016                  Inserting the Encryption Call for Issue__C as part of REQ – 213235/CA 5250144 
* Pradeepkumar Dani           30060                03/03/2016                  modified as per CC review comments
* Pradeepkumar Dani           34627                08/04/2016                  Updated class with APEX standards - REQ - 270091
* Avinash Choubey                                  02/21/2017                  REQ - 301383  chaged to Query locator and added logic to create custom chat record if no custom record exist for standard chat record
* Shiva Pasumarty                                  04/03/2017                  REQ - 301383  Updated logic to by pass failures while updating a list.
* Shiva Pasumarty                                  04/28/2017                  REQ - 311381  CA Ticket - 6730699 summary:Excessive error in Error_Log__c
* Shiva Pasumarty             95223                05/23/2017                  REQ - 313481  CA Ticket# 6734451 Live chat transcript records are not getting encrypted. Updated to remove filter to check if body is empty only then consider the records to be encrypted.
* Shiva Pasumarty             98098                05/23/2017                  REQ - 313481  CA Ticket# 6734451 Added logic to capture owner Id for Completed Chats from Live Chat Transcript Events 
* Sharan Shanmugam                                 01/04/2018                  REQ - 350764  Modified logic to populate HO_Dental__c field of Humana chat from HO_Dental_Case__c field of related case
* Suresh Gottipati								   10/10/2018			       REQ - 369763 Removed encryption logic as part of platform migration
* Rajesh Keswani			 314114				   10/10/2018		     	   REQ - 377098 Update HUMCustomChatTranscriptInsertBatch to feed account information when case is not associated.
* Joel George			 						   08/06/2020		     	   Added ChatKey not null check in query , prevents duplicate chat key records
* Vishnu Pilli			 						   10/16/2021		     	   2767546: Added empty query to avoid error on false scenarios
* Alvaro Madrid                                    09/02/2022                  Updated filter in query from dateCreated using a defined date to SystemModStamp using a number days.
* Vishnu Pilli			 						   03/03/2022		     	   Adding new field on Hum Chat Transcript to Share the records SDUP
* Pavan Kumar Maddikuntla                          06/13/2023                  For Unsecure stories passing the SkillId as null
* Sivaprakash Rajendran                            07/10/2023                  US4678688-Addtional Logic to update the correct Owner for Unsecure Chat (Omni-Channel) for Transfer scenarios
***************************************************************************************************************************************************************************/
public class HUMCustomChatTranscriptInsertBatch implements Database.Batchable <sObject > , Database.AllowsCallouts, Schedulable, Database.Stateful
{
    private static final String DEFAULT_DAYS_TO_PICK = '2';
    private static final String DEFAULT_CREATED_DATE_DAYS_TO_PICK = '7';
    //Query string variable
    public String sQuery ='SELECT Id, Body, status, CaseId, Case.CoKY_Case__c, Case.General_Case__c, ownerId, Case.DST_Case__c, Case.HO_Medical_Case__c, Case.HO_Dental_Case__c, Issue__c, ChatKey, AccountId, Account.CoKY_Account__c,Account.General_Account__c,Account.Home_Office_Account__c, Account.Contract_Protected_Data__c, SupervisorTranscriptBody ,createdbyId,Chat_Type__c FROM LiveChatTranscript Where endtime!=null AND ChatKey!=null' ;
    
    /**
    * start is interface required method.
    * <p>
    * This is used to execute pre logic and get records of Standard Transcript Object.
    *
    * @param  BC                        Database.BatchableContext
    * @return void
    */
    public Database.QueryLocator start(Database.BatchableContext BC)
    {
            //  2767546: Added empty query to avoid error on false scenarios
        String strEmptyQuery = sQuery + ' LIMIT 0';
        String dayToPick = DEFAULT_DAYS_TO_PICK;
        String createdDateDaysToPick = DEFAULT_CREATED_DATE_DAYS_TO_PICK;
        //RESTRICTED_USER_ID value is org specific hence it is not considered as GLOBAL_CONTSANTS
        try
        {
            If(!String.isEmpty(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c))
            {
                Id userId = ID.valueOf(HUMConstants__c.getInstance('RESTRICTED_USER_ID').StringValue__c);
                sQuery += ' AND  OwnerId=:userId';
            }
             //Dt - Date value is set to 01/01/2017. This is used to fetch records that are created post Dt value to avoid getting inactive records in query.
             //Updated to reduce the records that will be processed to only the last updated in the LAST_N_DAYS (defined in CS CHAT_TRANSCRIPT_N_DAYS_TO_PICK)
            if (HUMConstants__c.getInstance('CHAT_TRANSCRIPT_N_DAYS_TO_PICK') != null && 
                String.isNotBlank(HUMConstants__c.getInstance('CHAT_TRANSCRIPT_N_DAYS_TO_PICK').StringValue__c)) {
                dayToPick = HUMConstants__c.getInstance('CHAT_TRANSCRIPT_N_DAYS_TO_PICK').StringValue__c;
            }
            sQuery += ' AND  SystemModStamp = LAST_N_DAYS:' + dayToPick;
            
            //Added createdDate as filter
            if (HUMConstants__c.getInstance('CHAT_TRANSCRIPT_CREATED_LAST_N_DAYS') != null && 
                String.isNotBlank(HUMConstants__c.getInstance('CHAT_TRANSCRIPT_CREATED_LAST_N_DAYS').StringValue__c)) {
                createdDateDaysToPick = HUMConstants__c.getInstance('CHAT_TRANSCRIPT_CREATED_LAST_N_DAYS').StringValue__c;
            }
            sQuery += ' AND  createdDate = LAST_N_DAYS:' + createdDateDaysToPick;
            
            System.debug('AMDev sQuery: ' + sQuery);

            List<AsyncApexJob> lstObjApexJob = [Select Id,ApexClass.Name,Status from AsyncApexJob WHERE JobType = 'BatchApex' AND Status = 'Processing' AND Id !=: BC.getJobId()];        
            Integer iJobs = 0;
            if (!lstObjApexJob.IsEmpty())
            {
                for(AsyncApexJob obj : lstObjApexJob)
                {
                    //When the job for current class is already in progress, then Batch will process empty List (lstChat)
                    if(obj.ApexClass.Name == string.valueof(this).split(':')[0])
                    {
                            //  2767546: Returning empty result
                        return Database.getQueryLocator(strEmptyQuery);
                    }
                    else
                    {
                        //Counting the other processing jobs
                        iJobs++;
                    }
                    //When more than 4 apex jobs are already in progress, then Batch will process empty List (lstChat)
                    //  2767546: Returning empty result
                    if(iJobs > 4) return Database.getQueryLocator(strEmptyQuery);                       
                }
            }
            
            return  Database.getQueryLocator(sQuery);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomChatTranscriptInsertBatch','start');
            //  2767546: Returning empty result
            return Database.getQueryLocator(strEmptyQuery);
        }     
    }
    
    /**
    * execute is interface required method.
    * <p>
    * This is used to process records.
    *
    * @param  BC                        Database.BatchableContext
    * @param  lstChatRecords            list of chat records in the scope
    * @return void
    */
    public void execute(Database.BatchableContext BC, List < sObject > lstChatRecords)
    {
        System.debug('AMDev - batch will run for lstChatRecords: ' + lstChatRecords.size());
        set < String > setChatKey = new set < String > ();
        list <String> lstissue = new list <String> ();
        //Variable for storing chat body information
        set < String > setBody = new set < String > ();
        map<Id, List<string> > mapChatBodyLines = new map<Id, List<string> >();        
        //Variable for storing Supervisor chat body information
        set < String > setSuperBody = new set < String > ();
        map<Id, List<string> > mapChatSuperBodyLines = new map<Id, List<string> >();        
        //loop Over list of Standard Transcript Records
        LiveChatTranscript objTrans;
        try
        {
            for (sObject sObj: lstChatRecords)
            {
                objTrans = (LiveChatTranscript)sObj;
                setChatKey.add(objTrans.chatKey);            
                if (objTrans.issue__c != null) lstissue.add(objTrans.issue__c); //Store issue__c 
                if (objTrans.Body != null)
                {               
                    for(string sLine : objTrans.body.split('\\>'))
                    {
                        sLine = sLine.stripHtmlTags();
                        if(string.isNotBlank(sLine))
                        {
                            mapChatBodyLines.put(objTrans.Id, addElementToMap(mapChatBodyLines, sLine, objTrans.Id)); //MAP used to find the Body Lines of the chat Transcript from Encrypted data.
                            setBody.add(sLine); //SET used to eliminate duplicate lines to improve callout efficiency 
                        }
                    }
                }
                if(objTrans.SuperVisorTranscriptBody!=null)
                {
                    for(string sLine : objTrans.SuperVisorTranscriptBody.split('\\>'))
                    {
                        sLine = sLine.stripHtmlTags();
                        if(string.isNotBlank(sLine))
                        {
                            mapChatSuperBodyLines.put(objTrans.Id, addElementToMap(mapChatSuperBodyLines, sLine, objTrans.Id));//MAP used to find the Body Lines of the chat Transcript from Encrypted data.
                            setSuperBody.add(sLine);//SET used to eliminate duplicate lines to improve callout efficiency
                        }
                    }
                }
            }
            encryptingData(lstissue, setBody, setSuperBody, setChatKey, mapChatSuperBodyLines, mapChatBodyLines, lstChatRecords);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomChatTranscriptInsertBatch','execute');
            
        }
    }
    
    /**
    * encryptingData is method called from execute method
    * <p>
    * This method is used to encrypt the data using DPASS.
    *
    * @param  lstissue                  list containing chat issue field values
    * @param  setBody                   set containing body field values
    * @param  setSuperBody              set containing Supervisorbody field values
    * @param  setChatKey                set containing chatKey field values
    * @param  mapChatSuperBodyLines     map containing Supervisorbody field separated by linebreaks values
    * @param  mapChatBodyLines          map containing body field separated by linebreaks values
    * @return void
    */
    private void encryptingData(list<String> lstissue, set<String> setBody, set<String> setSuperBody, set<String> setChatKey, map<Id, List<string>> mapChatSuperBodyLines, map<Id, List<string>> mapChatBodyLines, list<LiveChatTranscript> lstChatRecords)
    {
        Map < String, Humana_Chat_Transcript__c > mapCustomRecord = new Map < String, Humana_Chat_Transcript__c > ();
        try
        {
            if (!setChatKey.isEmpty())
            {
                //Get Custom Chat Record created when chat was accepted using Chat Key of Standard Transcript Object.
                for (Humana_Chat_Transcript__c objCustom: [Select Id, Name,CoKY__c, General__c, HO_Dental__c, OwnerId, HO_Medical__c, Chat_Key__c, Live_Chat_Id__c from Humana_Chat_Transcript__c Where Chat_Key__c IN: setChatKey order by lastmodifieddate desc])
                {
                    if(!mapCustomRecord.containskey(objCustom.Chat_Key__c))
                        mapCustomRecord.put(objCustom.Chat_Key__c, objCustom);
                }
            }
            mapEncryptingDataToCustomObject(lstChatRecords, mapCustomRecord, mapChatBodyLines, mapChatSuperBodyLines);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomChatTranscriptInsertBatch','encryptingData');
        } 
    }
    
    /**
    * encryptingData is method called from execute method
    * <p>
    * This method is used to encrypt the data using DPASS.
    *
    * @param  mapClearEncrypt                   map containing encrypted chat body field values
    * @param  mapissueEncrypt                   map containing encrypted chat issue field values
    * @param  mapSuperBodyEncrypt               map containing encrypted chat supervisorbody field values
    * @param  lstChatRecords                    list of scope chat records
    * @param  mapCustomRecord                   list of scope custom chat records
    * @param  mapChatSuperBodyLines             map containing Supervisorbody field separated by linebreaks values
    * @param  mapChatBodyLines                  map containing body field separated by linebreaks values
    * @return void
    */
    private void mapEncryptingDataToCustomObject(list<LiveChatTranscript> lstChatRecords, map<String, Humana_Chat_Transcript__c> mapCustomRecord, map<Id, List<string>> mapChatBodyLines, map<Id, List<string>> mapChatSuperBodyLines)
    {
        List < Humana_Chat_Transcript__c > lstUpdateCustomRecord = new List < Humana_Chat_Transcript__c > ();
        Map<ID, LiveChatTranscriptEvent> mapLiveChatevent = new Map<ID, LiveChatTranscriptEvent>();
        Set<ID> setStanChatWithoutCustom = new Set<Id>();
       Set<String> setCheckDuplicateLCT = new Set<String>();
        try
        {       
            for(LiveChatTranscript objLiveChat: lstChatRecords)
            {
                //US4678688-Addtional Logic to check the Owner for Unsecure Chat (Omni-Channel) for Transfer scenarios
                if((mapCustomRecord.isEmpty() || !mapCustomRecord.containsKey(objLiveChat.chatKey)) ||  
                   (objLiveChat.Chat_Type__c.contains('Unsecure'))){
                    setStanChatWithoutCustom.add(objLiveChat.Id);
                }
                
            }
                    //Map to capture all the Transcript event records for the respective Live Chat Transcript record. This will be used to capture the owner Id for the Agent who accepted the chat request.
                Map<ID,LiveChatTranscript> mapChatTransWithoutCustom = new Map<Id, LiveChatTranscript>( [select id, (SELECT Id,  AgentId, LiveChatTranscriptId FROM LiveChatTranscriptEvents where AgentId!= null order by time desc limit 1)from LiveChatTranscript where Id in :setStanChatWithoutCustom ]);
				Set<String> validChatTypeSet = new Set<String>();
				validChatTypeSet = getChatType();
           
                    // Removed line - if(!mapClearEncrypt.isEmpty) - since this will fail to capture missed chat transcript records. For Missed Chats, the body field will always be empty so this condition will fail.
                for (LiveChatTranscript objLiveChat: lstChatRecords)
                {
                    if(!setCheckDuplicateLCT.contains(objLiveChat.chatKey))
                    {
                    Humana_Chat_Transcript__c objCustom ;
                    if (!mapCustomRecord.isEmpty() && mapCustomRecord.get(objLiveChat.chatKey) != null)
                    {
                        objCustom = mapCustomRecord.get(objLiveChat.chatKey);
                    }
                    else
                    {
                        objCustom = new Humana_Chat_Transcript__c();
                        
                       if(String.isNotBlank(objLiveChat.chat_type__c) && validChatTypeSet.contains(objLiveChat.chat_type__c.toUppercase()))
                        {
                            objCustom.ownerID = objLiveChat.createdbyId;
                        }
                        else
						{
                            //Added logic to capture owner Id for Completed Chats from Live Chat Transcript Events if Humana Chat Transcript record is not created by HUMPrechatController for some reason.
                            if(!mapChatTransWithoutCustom.get(objLiveChat.id).LiveChatTranscriptEvents.isEmpty() && null != mapChatTransWithoutCustom.get(objLiveChat.id).LiveChatTranscriptEvents[0].AgentId )
                            {
                                if(objLiveChat.status == GLOBAL_CONSTANT_HUM.CHATBATCHJOB_COMPLETED_STATUS_HUM)
                                    objCustom.ownerID = mapChatTransWithoutCustom.get(objLiveChat.id).LiveChatTranscriptEvents[0].AgentId;
                            }
                            else  objCustom.ownerID = objLiveChat.ownerID;
                        }
                    }
                    if (objCustom != null)
                    {
                        if(objLiveChat.Body!=null)
                        {
                            objCustom.Body__c ='';
                            for(string sLine : mapChatBodyLines.get(objLiveChat.Id)) objCustom.Body__c += sLine + '<BR/>';
                        }
                        if (objLiveChat.CaseId != null)
                        {
                            objCustom.CoKY__c = objLiveChat.Case.CoKY_Case__c;
                            objCustom.General__c = objLiveChat.Case.General_Case__c;
                            objCustom.HO_Dental__c = objLiveChat.Case.HO_Dental_Case__c;
                            objCustom.HO_Medical__c = objLiveChat.Case.HO_Medical_Case__c;
                        }
                        else
                        {    
                            objCustom.CoKY__c = objLiveChat.Account.CoKY_Account__c;
                            objCustom.General__c = objLiveChat.Account.General_Account__c;                          
                            objCustom.HO_Medical__c = objLiveChat.Account.Home_Office_Account__c;
                            
                        }
                        objCustom.Chat_Key__c = objLiveChat.chatKey;
                        
                        if (objLiveChat.issue__c != null) objCustom.Issue__c = objLiveChat.issue__c;
                        if(objLiveChat.SupervisorTranscriptBody!=null)
                        {   
                            objCustom.SupervisorTranscriptBody__c = '';
                            for(string sLine : mapChatSuperBodyLines.get(objLiveChat.Id))objCustom.SupervisorTranscriptBody__c += sLine + '<BR/>';                                
                        }
                        objCustom.live_chat_id__C = objLiveChat.Id;   
                        objCustom.Contract_Protected_Transcript__c = objLiveChat.Account.Contract_Protected_Data__c; 
                        //1.US4678688-Addtional Logic to check the Owner for Unsecure Chat (Omni-Channel) for Transfer scenarios
                        if(objLiveChat.Chat_Type__c.contains('Unsecure')){
                            if(!mapChatTransWithoutCustom.get(objLiveChat.id).LiveChatTranscriptEvents.isEmpty() && null != mapChatTransWithoutCustom.get(objLiveChat.id).LiveChatTranscriptEvents[0].AgentId )
                            {
                                if(objLiveChat.status == GLOBAL_CONSTANT_HUM.CHATBATCHJOB_COMPLETED_STATUS_HUM)
                                    objCustom.ownerID = mapChatTransWithoutCustom.get(objLiveChat.id).LiveChatTranscriptEvents[0].AgentId;
                            }
                        }                     
                        lstUpdateCustomRecord.add(objCustom);
                        
                    }
                        setCheckDuplicateLCT.add(objLiveChat.chatKey);
                    }
                                  
                }
            
            updateTranscriptRecords(lstUpdateCustomRecord, mapCustomRecord, lstChatRecords);
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomChatTranscriptInsertBatch','mapEncryptingDataToCustomObject');
            
        }     
    }
    
    /**
    * UpdateTranscriptRecords is called from mapEncryptingDataToCustomObject method.
    * <p>
    * This is used to Update standard/custom transcript record body,supervisor body and issue
    *
    * @param  scxt      SchedulableContext
    * @return void
    */
    private void updateTranscriptRecords(list<Humana_Chat_Transcript__c> lstUpdateCustomRecord, map<String, Humana_Chat_Transcript__c> mapCustomRecord, list<LiveChatTranscript> lstChatRecords)
    {
        List < LiveChatTranscript > lstUpdateStandardRec = new List < LiveChatTranscript > ();
        map<id,LiveChatTranscript > lstDupRec = new map<id,LiveChatTranscript >();
        List < Humana_Chat_Transcript__c > lstUpdatedCustomRecord = new List < Humana_Chat_Transcript__c > ();
        set < id > setUpdateCustomRecord = new set < id > ();
        try
        {
            
            if (!lstUpdateCustomRecord.isEmpty())
            {
				Set<String> validChatTypeSet = new Set<String>();
				validChatTypeSet = getChatType();
		
                //Upsert Custom transcript record with encrypted body and supervisor body.
                Schema.SObjectField keyFieldName= Humana_Chat_Transcript__c.Fields.chat_Key__c;
                Database.UpsertResult [] lstSvr = Database.upsert(lstUpdateCustomRecord,keyFieldName, false);
                
                //Retrieve the Custom transcript record Ids once they are upserted in previous statement.
                 for (Database.UpsertResult sr : lstSvr) 
                    {
                        if (sr.isSuccess()) 
                        {
                            setUpdateCustomRecord.add(sr.getid());
                        }
                    }
                lstUpdatedCustomRecord = [Select Id, Name,CoKY__c, General__c, HO_Dental__c, OwnerId, HO_Medical__c, Chat_Key__c, Live_Chat_Id__c from Humana_Chat_Transcript__c Where Id IN: setUpdateCustomRecord];
                       
                Boolean isError = HUMExceptionHelper.processUpsertResults(lstSvr, lstUpdateCustomRecord,'HUMCustomChatTranscriptInsertBatch-1', 'execute', 'Humana_Chat_Transcript__c');
                // Store the Custom Transcript record details in a map to be used later for retrieivng Id & owner id values
                for(Humana_Chat_Transcript__c objCustom : lstUpdatedCustomRecord)
                {
                    mapCustomRecord.put(objCustom.Chat_Key__c, objCustom);
                }
                 
                for (Database.UpsertResult sr : lstSvr) 
                {
                    if (sr.isSuccess()) 
                    {
                        for (LiveChatTranscript objLive: lstChatRecords)
                        {
                            if(!lstDupRec.containskey(objLive.id))
                            {
                                if (mapCustomRecord.containsKey(objLive.chatKey))
                                {
                                    objLive.Humana_Chat_Transcript__c = mapCustomRecord.get(objLive.ChatKey).Id;
                                    objLive.Body = '';
                                    objLive.SuperVisorTranscriptBody = '';
                                    objLive.Issue__c = ''; 
                                                                       
                                    if(String.isNotBlank(objLive.chat_type__c) && validChatTypeSet.contains(objLive.chat_type__c.toUppercase()))
                                    {
                                        objLive.OwnerId = objLive.createdbyId;
                                    }
                                    else
                                    objLive.OwnerId = mapCustomRecord.get(objLive.ChatKey).OwnerId;
                                    lstUpdateStandardRec.add(objLive);
                                    lstDupRec.put(objLive.id,objLive);
                                }
                            }
                        }
                    }    
                }
                if (!lstUpdateStandardRec.isEmpty())
                {
                    //Update standard transcript object record.
                    Database.SaveResult[] lstChatSvr = Database.update(lstUpdateStandardRec, false);
                    Boolean isErrorStandard = HUMExceptionHelper.processSaveResults(lstChatSvr, lstUpdateCustomRecord, 'HUMCustomChatTranscriptInsertBatch-2', 'execute', 'Humana_Chat_Transcript__c');
                }                
            }
        }
        catch(Exception e)
        {
            HUMExceptionHelper.logErrors(e,'HUMCustomChatTranscriptInsertBatch','updateTranscriptRecords');
            
        }
    }
    
    /**
    * addElementToMap is generic method to add string element to the given map using key.
    *
    * @param  iMap           Map variable to which element needs to be added
    * @param  ele            element which needs to be added
    * @param  key            key value to find the match in the Map
    * @return list<string>
    */
    private list<string> addElementToMap(map<Id, List<string> > iMap, string ele, Id key)
    {
        List<string> lstTemp = new List<string>();
        if(iMap.containsKey(key) && iMap.get(key) <> NULL)
        {
            lstTemp = iMap.get(key);
        }
        lstTemp.add(ele);
        return lstTemp;
    }
	
	/**
    * addElementToMap is generic method to add string element to the given map using key.
    *
    * @param  iMap           Map variable to which element needs to be added
    * @param  ele            element which needs to be added
    * @param  key            key value to find the match in the Map
    * @return list<string>
    */
    private Set<String> getChatType()
    {
		Set<String> validChatTypeSet = new Set<String>();
        for(Chat_Service_Source_App__mdt sourceAppObj : [SELECT MasterLabel,ChatType__c FROM Chat_Service_Source_App__mdt where MasterLabel IN ('IVAWatson','IVANINA') ])
        {
            validChatTypeSet.addall(sourceAppObj.ChatType__c.toUppercase().split(','));
        }
        return validChatTypeSet;
    }
    
    /**
    * finish is interface required method.
    * <p>
    * This is used to execute post logic.
    *
    * @return void
    */
    public void finish(Database.BatchableContext BC)
    {  
    }
    
    /**
    * execute is Schedulable interface required method.
    * <p>
    * This is used to execute batch class
    *
    * @param  scxt      SchedulableContext
    * @return void
    */
    public void execute(SchedulableContext scxt)
    {
        //Using Type class create the instance
        Database.executeBatch((HUMCustomChatTranscriptInsertBatch)Type.forName(string.valueof(this).split(':')[0]).newInstance(), 200);
    }    
}