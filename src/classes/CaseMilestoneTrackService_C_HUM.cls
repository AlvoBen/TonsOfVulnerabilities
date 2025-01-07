/*******************************************************************************************************************************
Apex Class Name : CaseMilestoneTrackService_C_HUM
Version         : 1.0
Created On      : 2017
Function        : Class contains method for services to post Milestone information.
test Class      : CaseMilestoneTrackService_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Pradeepkumar Dani          114278                  07/10/2017                 Original Version: REQ - 315927
* Pradeepkumar Dani                                  08/11/2017                 Updated for Correspondence related changes.
* Syed Mubarak               221369                  04/27/2018                 REQ - 360058 Writing Stop Milestone
* Mohammed Noor                                      05/10/2018                 REQ - 360058 - BAM Milestone changes
* Mohammed Noor              232736                  05/22/2018                 REQ - 362097 - Ability for CRM to send milestone values to BAM
*                                                                               upon subtype creation and change, as well as supporting TAS entries.
* Mohammed Noor              238897                  06/04/2018                 Fix Defect# 360701.0001. Changes done to use Inbound Call Origin 
*                                                                               for all the changes happening with in CRM.
* Mohammed Noor              254494                  07/09/2018                 Fix the issue with Bulk Record Inserts from ETL.
* Syed Mubarak										07/26/2018					Fix to send the previous subtype for stop milestone and TAS added with previous subtype
********************************************************************************************************************************/
public class CaseMilestoneTrackService_C_HUM 
{
    public class CaseMilestoneParams
    {
        @InvocableVariable(required=true)
        public Id CaseId;
        
        @InvocableVariable(required=false)
        public String previousCaseSubType;
        
        @InvocableVariable(required=false)
        public Boolean isNewCase;
    }
	
	public static string sSTOPMileStone = '898';
	
		
    @InvocableMethod public static void invokeTrackService(CaseMilestoneParams[] CaseParams)
    {
        try
        {
            List<Id> caseIdsList = new List<Id>();
            Map<Id,String> CaseSubtypeList = new Map<Id,String>();
            Map<Id, Case> mapCases = new Map<Id, Case>();
  			Map<String, List<Enrollment_Track_History__c>> mapHistoryMilestones = new Map<String, List<Enrollment_Track_History__c>>();
  			          
            for(CaseMilestoneParams cmp: CaseParams)
            {
                caseIdsList.add(cmp.CaseId);
                CaseSubtypeList.put(cmp.CaseId,cmp.previousCaseSubType);                
            }
                        
            list<case> lstCases = [SELECT Id, SubType__c, Origin, Doc_Type__c, CaseNumber, Type, Policy_Member__c, Medicare_Track_ID__c, CreatedDate FROM Case WHERE ID = :caseIdsList];
            if(lstCases != null && !lstCases.isEmpty())
            {
            	mapCases.putAll(lstCases);            	
            }
            mapHistoryMilestones = getCaseMilestoneHistoryRecs(mapCases.keySet());
                        
            map<Id, set<MET_Milestone__c>> mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();            
            
            //Map of Milestones for new Case Creation
            map<string, set<MET_Milestone__c>> mapMilestone = new map<string, set<MET_Milestone__c>>();
                        
            //Maps for the Update of the case
            map<String, Case_Milestone__c> mapStartMilestone = new map<String, Case_Milestone__c>();		//map of Start Milestones by Subtype
            map<String, Case_Milestone__c> mapContMilestone = new map<String, Case_Milestone__c>();		//map of Continuity Milestones by Subtype
            
            
            //Get the Milestone entries from Custom Setting and populate the required Maps.
            for (Case_Milestone__c cm : Case_Milestone__c.getall().values())
            {
                set<MET_Milestone__c> setMilestones = new set<MET_Milestone__c>();  
                string key = (cm.Sub_Type__c + cm.Origin__c).toLowerCase();
                if(mapMilestone.containsKey(key)) setMilestones = mapMilestone.get(key);
                setMilestones.add(new MET_Milestone__c(Name = cm.Milestone__c));
                mapMilestone.put(key, setMilestones);
             	
				if(cm.Origin__c == GLOBAL_CONSTANT_HUM.MEDICARECALLS_CASEORIGIN_HUM)
	            {
					if(cm.Is_Start_Milestone__c)
					{             		
						mapStartMilestone.put(cm.Sub_Type__c.toLowerCase(), cm);
					}
					else
					{             	
						mapContMilestone.put(cm.Sub_Type__c.toLowerCase(), cm);
					}             	                 
				}
            }

            for(CaseMilestoneParams cmp: CaseParams)
            {
            	Case cs = mapCases.get(cmp.CaseId);
            	string key;
            	            	
            	if(cmp.isNewCase)	//For New Case Insert, select Milestone based on Case Subtype and Origin.
            	{
            		key = (cs.SubType__c + cs.Origin).toLowerCase();	// Case Subtype and Origin is the key for new case
            		if (mapMilestone.containsKey(key))
					{						
						mapCaseMilestones.put(cs.Id, new set<MET_Milestone__c>(mapMilestone.get(key)));
					}
            	} 
            	else	//Update Case Logic
            	{
            		Boolean bAddStopMS = false;		//Flag to identify if Stop Milestone needs to be written
            		Boolean bDiffCategory = false;	//Flag to identify the Activity Category
            		key = (cs.SubType__c).toLowerCase();     //For update case use Subtype only as the key
            		set<MET_Milestone__c> writeMilestones = new set<MET_Milestone__c>();
            		if(mapStartMilestone.containsKey(key))
            		{            			
            			Case_Milestone__c caseMilestone = mapStartMilestone.get(key);
            			            			            		            			            			            			            			
            			if(mapHistoryMilestones.containsKey(cs.Id))  //check if any milestones already written for the case
            			{            				
            				if(String.isNotBlank(caseMilestone.Activity_Category__c))
            				{
            					//check if the previous written start milestone is in the same category
            					if(!getPreviousMSCategory(mapHistoryMilestones.get(cs.Id), caseMilestone.Activity_Category__c))
            					{					
            						bDiffCategory = true;
            					}
	            			}
	            			else bDiffCategory = true;	
            				
            				if(!checkLastMilestoneForStop(mapHistoryMilestones.get(cs.Id)))    //check if the last milestone written on the case was a stop milestone '898'
            				{            					
            					bAddStopMS = true;
            				}
            			}
            			
            			if(bAddStopMS && bDiffCategory) 
            			{
            				writeMilestones.add(new MET_Milestone__c(Name = sSTOPMileStone,Case_Subtype__c = CaseSubtypeList.get(cs.Id)));
            			}            			            			            			             			            		            			
            			//Add the new start milestone for the new subtype            		
            			writeMilestones.add(new MET_Milestone__c(Name = caseMilestone.Milestone__c));
            		}
					else
					{						
						if (mapContMilestone.containsKey(key))
						{
							Case_Milestone__c caseMilestone = mapContMilestone.get(key);
																												
							if(mapHistoryMilestones.containsKey(cs.Id))  //check if any milestones already written for the case
	            			{            				
								//check if the same Continuity milestone had already been written as the recent Milestone on the Case
								if(!checkContinuityMSWritten(mapHistoryMilestones.get(cs.Id),caseMilestone.Milestone__c))
								{
									//Add the new Continuity Milestone for the new Subtype									
									writeMilestones.add(new MET_Milestone__c(Name = caseMilestone.Milestone__c));
								}	        				  				
    	        			}
    	        			else
    	        			{
    	        				writeMilestones.add(new MET_Milestone__c(Name = caseMilestone.Milestone__c));
    	        			}																					
						}		
					}
                    if(!writeMilestones.isEmpty())
                    {
                    	mapCaseMilestones.put(cs.Id,writeMilestones);
                    }                    
            	}            	
            }                                                                 
            if (!mapCaseMilestones.isEmpty())
            {
                System.enqueueJob(new TrackService_Q_HUM(mapCaseMilestones));
            }
        }
        catch (Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'CaseMilestoneTrackService_C_HUM', 'invokeTrackService');
        }
    }
	
	/**
    * This method is called from the Case Edit save logic to get the list of Milestones to be written for the case    
    *
    * @param    case Instance,Previous subtype value, set of MET_Milestone__c for the new MET T/A/S created on the previous subtype 
    * @return   set<MET_Milestone__c> - List of Milestones for the case record.
    */
    public static set<MET_Milestone__c> getUICaseMilestone(Case caseRec, String sPrevSubtype, set<MET_Milestone__c> prevSubtypeMS)
    {
    	set<MET_Milestone__c> writeMilestones = new set<MET_Milestone__c>();
    	
    	Map<String, List<Enrollment_Track_History__c>> mapHistoryMilestones = new Map<String, List<Enrollment_Track_History__c>>();
    	
    	//Map of Milestones for new Case Creation
        map<string, set<MET_Milestone__c>> mapMilestone = new map<string, set<MET_Milestone__c>>();
                    
        //Maps for the Update of the case
        map<String, Case_Milestone__c> mapStartMilestone = new map<String, Case_Milestone__c>();		//map of Start Milestones by Subtype
        map<String, Case_Milestone__c> mapContMilestone = new map<String, Case_Milestone__c>();		//map of Continuity Milestones by Subtype
        
        
        //Get the Milestone entries from Custom Setting and populate the required Maps.
        for (Case_Milestone__c cm : Case_Milestone__c.getall().values())
        {
            set<MET_Milestone__c> setMilestones = new set<MET_Milestone__c>();  
            string key = (cm.Sub_Type__c + cm.Origin__c).toLowerCase();
            if(mapMilestone.containsKey(key)) setMilestones = mapMilestone.get(key);
            setMilestones.add(new MET_Milestone__c(Name = cm.Milestone__c));
            mapMilestone.put(key, setMilestones);
         	
         	if(cm.Origin__c == GLOBAL_CONSTANT_HUM.MEDICARECALLS_CASEORIGIN_HUM)
         	{
				if(cm.Is_Start_Milestone__c)
				{             		
					mapStartMilestone.put(cm.Sub_Type__c.toLowerCase(), cm);
				}
				else
				{             	
					mapContMilestone.put(cm.Sub_Type__c.toLowerCase(), cm);
				}             	               
			}
        }
    	
    	mapHistoryMilestones = getCaseMilestoneHistoryRecs(new set<Id>{caseRec.Id});
    	    	    	
    	
		String key = (caseRec.SubType__c).toLowerCase();     //For update case use Subtype only as the key
		
		Boolean bLastMS = true;
		Boolean bAddStopMS = false;		//Flag to identify if Stop Milestone needs to be written
		Boolean bDiffCategory = false;	//Flag to identify the Activity Category
		
		if(mapStartMilestone.containsKey(key))
		{        
			Case_Milestone__c caseMilestone = mapStartMilestone.get(key);
			
			//check if the last T/A/S entry of the previous subtype tasks in 898
			if(prevSubtypeMS!= null && !prevSubtypeMS.isEmpty())
			{
				String sLastMS = new List<MET_Milestone__c>(prevSubtypeMS).get(prevSubtypeMS.size()-1).Name;           	
				if(sLastMS.equals(sSTOPMileStone))
				{
					bLastMS = false;
				}           			
			}           		
			if(bLastMS && mapHistoryMilestones.containsKey(caseRec.Id))  //check if any milestones already written for the case
			{            				
				if(String.isNotBlank(caseMilestone.Activity_Category__c))
				{
					//check if the previous written start milestone is in the same category
					if(!getPreviousMSCategory(mapHistoryMilestones.get(caseRec.Id), caseMilestone.Activity_Category__c))
					{					
						bDiffCategory = true;
					}
				}
				else bDiffCategory = true;	
				
				if(!checkLastMilestoneForStop(mapHistoryMilestones.get(caseRec.Id)))    //check if the last milestone written on the case was a stop milestone '898'
				{            					
					bAddStopMS = true;
				}
			}
			
			if(bAddStopMS && bDiffCategory) 
			{
				writeMilestones.add(new MET_Milestone__c(Name = sSTOPMileStone ,Case_Subtype__c = sPrevSubtype));
			}            			            			            			             			            		            			
			//Add the new start milestone for the new subtype            		
			writeMilestones.add(new MET_Milestone__c(Name = caseMilestone.Milestone__c));    			       		           		           		
		}  
		else if (mapContMilestone.containsKey(key))
		{           		
			Case_Milestone__c caseMilestone = mapContMilestone.get(key);
																											
			if(mapHistoryMilestones.containsKey(caseRec.Id))  //check if any milestones already written for the case
			{            				
				//check if the same Continuity milestone had already been written as the recent Milestone on the Case
				if(!checkContinuityMSWritten(mapHistoryMilestones.get(caseRec.Id),caseMilestone.Milestone__c))
				{
					//Add the new Continuity Milestone for the new Subtype									
					writeMilestones.add(new MET_Milestone__c(Name = caseMilestone.Milestone__c));
				}	        				  				
			}
			else
			{
				writeMilestones.add(new MET_Milestone__c(Name = caseMilestone.Milestone__c));
			}    																								
		}			             	         			
			
    	return writeMilestones;
    }
    
	@TestVisible
	/**
    * This method is to get all the Milestone History Records for cases. The records are retrived in the descending order of created date.
    * The records are also pushed into the list in the descending order of created date. 
    *
    * @param    Set<Id> caseRecs
    * @return   Map<String, List<Enrollment_Track_History__c>> (map of List<Enrollment_Track_History__c> by Case IDs)
    */	
	private static Map<String, List<Enrollment_Track_History__c>> getCaseMilestoneHistoryRecs(set<Id> caseRecs)	
	{
		List<Enrollment_Track_History__c> lstTrackHis = new List<Enrollment_Track_History__c>();
						
		Map<String, List<Enrollment_Track_History__c>> mapHistoryMilestones = new Map<String, List<Enrollment_Track_History__c>>();
		
		lstTrackHis = [SELECT Id, Name, Case__c, Source_App__c, Recipient_App__c, Source_Track_Id__c, Recipient_Track_ID__c, Member_ID__c, Status__c, Milestone_Id__c, Milestone_MET_Id__c, CreatedDate
					   FROM Enrollment_Track_History__c WHERE case__c IN :caseRecs AND Status__c = 'Success' ORDER BY CreatedDate desc];
		if(!lstTrackHis.isEmpty())
		{
			for(Enrollment_Track_History__c HisRec : lstTrackHis)
			{
				List<Enrollment_Track_History__c> lstTrackHisByCase = new List<Enrollment_Track_History__c>();
				if(mapHistoryMilestones.containsKey(HisRec.Case__c))
				{
					lstTrackHisByCase = mapHistoryMilestones.get(HisRec.Case__c);
				}
				lstTrackHisByCase.add(HisRec);
				mapHistoryMilestones.put(HisRec.Case__c, lstTrackHisByCase);
			}
		}
		return mapHistoryMilestones;
	}
	
	@TestVisible
	/**
    * This method is to verify if the new Milestone and last written Case Milestone is of same category
    * 
    * @param    List<Enrollment_Track_History__c> caseMilestones and String newMilestoneCategory (new Milestone Category)
    * @return   Boolean (True if milestone already written else False)
    */
	private static Boolean getPreviousMSCategory(List<Enrollment_Track_History__c> caseMilestones, String newMilestoneCategory)
	{
		Boolean bPrevMSCategoryResult = false;
		
		for(Enrollment_Track_History__c histMilestone : caseMilestones)
		{
			if(histMilestone.Milestone_MET_Id__c != null || histMilestone.Milestone_MET_Id__c == null && (histMilestone.Milestone_Id__c).equals(sSTOPMileStone))
			{ 
				continue;
			}
			else
			{
				Map<String, Case_Milestone__c> mapMilestoneEntry = new Map<String, Case_Milestone__c>();
				for (Case_Milestone__c caseMS : Case_Milestone__c.getall().values())
				{
					mapMilestoneEntry.put(caseMS.Milestone__c, caseMS);
				}
				
				if(mapMilestoneEntry.containsKey(histMilestone.Milestone_Id__c))
				{
					String sPrevActivityCategory = mapMilestoneEntry.get(histMilestone.Milestone_Id__c).Activity_Category__c;
					if(String.isBlank(sPrevActivityCategory))
					{
						bPrevMSCategoryResult = false;
					}
					else
					{
					bPrevMSCategoryResult = newMilestoneCategory.equals(mapMilestoneEntry.get(histMilestone.Milestone_Id__c).Activity_Category__c) ? true : false;
					}
				}
				break;
			}									
		}
		return bPrevMSCategoryResult;
	}
	
	
	@TestVisible
	/**
    * This method is to verify if the stop milestone has already been written on the case.
    * 
    * @param    List<Enrollment_Track_History__c> caseMilestones   - List of Track Histories of the Case
    * @return   Boolean (True if stop milestone already written else False)
    */
	private static Boolean checkLastMilestoneForStop(List<Enrollment_Track_History__c> caseMilestones)
	{
		Boolean bChkResult = false;		
		for(Enrollment_Track_History__c histMilestone : caseMilestones)
		{
			if(histMilestone.Status__c == 'Success')
			{
				bChkResult = (histMilestone.Milestone_Id__c).equals(sSTOPMileStone) ? true : false ;
				break;				
			}
		}					
		return bChkResult;
	}
	
	@TestVisible
	/**
    * This method is to verify if the same Continuity milestone has already been written on the case.
    * 
    * @param    List<Enrollment_Track_History__c> caseMilestones   - List of Track Histories of the Case, New Continuity Milestone
    * @return   Boolean (True if same continuity milestone has already been written else False)
    */
	private static Boolean checkContinuityMSWritten(List<Enrollment_Track_History__c> caseMilestones, String newMilestone)
	{
		Boolean bValResult = false;			
		for(Enrollment_Track_History__c histMilestone : caseMilestones)
		{
			if(histMilestone.Milestone_MET_Id__c != null || histMilestone.Milestone_MET_Id__c == null && (histMilestone.Milestone_Id__c).equals(sSTOPMileStone))
			{ 
				continue;
			}
			else
			{
				bValResult = (newMilestone.equals(histMilestone.Milestone_Id__c)) ? true : false;
				break;
			}
		}
		return bValResult;
	}     
}