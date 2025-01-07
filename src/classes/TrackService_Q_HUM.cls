/******************************************************************************************************************
Apex class Name    : TrackService_Q_HUM
Version            : 1.0
Function           : Queueable Apex Class to process multiple milestone call the Track Service
Created On         : July 12,2017
Test Class         : TrackService_T_HUM

Modification Log:
*  Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------
*  Lalit Jain                  114278            19/07/2017         Original version: REQ - 321922
*  Pradeepkumar Dani		   114278			 26/07/2017			Case Milestone related changes: REQ - 315927
*  Pradeepkumar Dani		   131258			 09/21/2017			Multiple task milestone enhancement.
*  Mohammed Noor		       350978  			 04/12/2019			Changes to support TRR Process BAM tracking
*  Mohammed Noor                                 02/19/2020         REQ - 410421 - Changes done for DEAA auto closure functionality.
------------------------------------------------------------------------------------------------------------------- */
public class TrackService_Q_HUM implements Queueable, Database.AllowsCallouts 
{
	private String sCaseID{get;Set;}	
	private String sTaskID{get;Set;}	
	private map<Id, set<MET_Milestone__c>> mapCaseMilestones {get;Set;}
	private list<Id> lstTaskIds{ get; Set; }
	private map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory	{get; set;}
	private set<Id> setCaseIDs {get; set;}	//This variable is added specifically for DEAA cases. Do not use in future for other cases. 
	
	/**
	* Constructor of the class to Instantiate the variables.
	*
	* @param    lstTaskIds   List of Task Record Ids
	* @param    sCaseID      Case Record Id
	* @return   na
	*/
	public TrackService_Q_HUM(list<Id> lstTaskIds, String sCaseID)
	{
		this.lstTaskIds = lstTaskIds;
		this.sCaseID = sCaseID;
	}

    /**
    * Constructor of the class to Instantiate the variables.
    * 
    * @param    sTaskID      Task Record Id
    * @param    sCaseID      Case Record Id 
	* @return   na
    */
	public TrackService_Q_HUM(String sTaskID, String sCaseID)
	{
		this.sTaskID = sTaskID;
		this.sCaseID = sCaseID;
		this.lstTaskIds = new list<Id>{sTaskID};
	}
	
	/**
    * Constructor of the class to Instantiate the variables.
    * 
    * @param    mapCaseMilestones   Map of case and its milestones
	* @return   na
    */
	public TrackService_Q_HUM(map<Id, set<MET_Milestone__c>> mapCaseMilestones)
    {
		this.mapCaseMilestones = mapCaseMilestones;
	}
	
	/**
    * Constructor of the class to Instantiate the variables.
    * 
    * @param    mapCaseEnrollHistory   Map of case and its Enrollment History Recs by SourceID
	* @return   na
    */
	public TrackService_Q_HUM(map<string, map<string, set<Enrollment_Track_History__c>>> mapCaseEnrollHistory)
    {
		this.mapCaseEnrollHistory = mapCaseEnrollHistory;
	}
	
	/**
    * Constructor of the class to Instantiate the variables.
    * 
    * @param    setCaseIDs   set of CaseIDs 
	* @return   NA
    */
	public TrackService_Q_HUM(set<Id> setCaseIDs)
    {
		this.setCaseIDs = setCaseIDs;
	}
	
    /**
    * This method will be called when this class will be called using System.enqueueJob method
    * 
    * @param    context          QueueableContext instance
	* @return   na
    */
	public void execute(QueueableContext context) 
	{		
		if(sCaseID <> null)
		{
			mapCaseMilestones = new map<Id, set<MET_Milestone__c>>();
			mapCaseMilestones.put(sCaseID, null);
		}	
		if(mapCaseEnrollHistory==null && setCaseIDs == null)
		{
			TrackService_H_HUM oTrackSerHelpClass = new TrackService_H_HUM(lstTaskIds, mapCaseMilestones);
			oTrackSerHelpClass.callTrackServiceBasedOnTaskOrMstoneSet();
		}
		else if(mapCaseEnrollHistory!=null && setCaseIDs == null)   //For TRR Process
		{
			TrackService_H_HUM oTrackSerHelpClass = new TrackService_H_HUM(mapCaseEnrollHistory);
			oTrackSerHelpClass.callTrackServiceForTRRProcess();
		}
		else if(setCaseIDs!=null)	//For DEAA Cases
		{
			TrackService_H_HUM oTrackSerHelpClass = new TrackService_H_HUM(setCaseIDs);
			oTrackSerHelpClass.processDEAACasesBulk();
		}
	}
}