/*******************************************************************************************************************************
Apex Class Name : HUMPreChatResultWrapperHelper 
Version         : 1.0
Created On      : 09/26/2014
Function        : 1. This Class is wrappwe class used to wrapp response sent by HUMPreChatController to Page.
Test Class 		: HUMPreChatControllerTest
Modification Log: 
 * Modification ID	Developer Name          Code Review               Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0				Ninad Patil              18240                  09/26/2014                  Original Version(REQ-01964)
 * 1.1				Ninad Patil				 18240  				09/29/2014				    Fixed Internal code reveiw comment.
 * 1.2              Ninad Patil				 18975				    10/30/2014              	Done changes for CR50
 * 1.3				Pradeepkumar Dani		 34678 		 			08/04/2016			   		Updated class with APEX standards - REQ - 270091
 * 1.4              Vishal Verma             99430                  06/06/2017                  REQ - 320399 : CA 6197943 -WebChat/Survey Monkey missing Interaction Id fix.
 * 1.5              Vandana Chaudhari  		 220488	 	            04/09/2018               	REQ - 355781 CR788: Web Chat Auto Create Case (CMO CR5548) 
 * 1.6        		Vandana Chaudhari     				 	        07/20/2018         	 		REQ - 370601 CA Ticket 7646745 Fix: Web Chat Auto Create Case
 * 1.7        		Akshay Pai     				 	        		07/16/2020         	 		REQ - 1292117 IVA NINA - Member Secure chat changes
 * 1.8        		Vishnu Pilli     				 	        	07/29/2021         	 		MF-22 Accolade Impact Member changes
 * 1.9              Shivani Shrivastav                              08/22/2022                  US3750632 - Unsecure IVA Transcript Displays in Live Chat
 ****************************************************************************************************************************/
global class HUMPreChatResultWrapperHelper
{
	//Stores Account Id being decrypted in HUMPreChatController.
	@TestVisible private String sAccId = '';
	
	//Stores Interaction Created in HUMPreChatController.
	@TestVisible private String sIntId = '';
	
	//Stores Interaction Member ID created in HUMPreChatController.
	@TestVisible private String sIntMemberId = '';
	
	//Stores Interaction Member ID created in HUMPreChatController.
	@TestVisible private String sInteractionName = '';
	
    //Stores Interaction MemberGenKey created in HUMPreChatController.
    @TestVisible private String sDecryptMemberGenKey = '';
	//Stores Interaction Member ID created in HUMPreChatController.
	@TestVisible private String sDecryptLsessionID = '';
	
	//Stores case field values created in HUMPreChatController.
    @TestVisible private String sInteractingWithType = '';
    @TestVisible private String sCaseOrigin = '';
    @TestVisible private String sCaseType = '';
    
	//Stores classification id found in HUMPreChatController.
    @TestVisible private String sClassification = '';
    
    //Stores intent id found in HUMPreChatController.
    @TestVisible private String sIntent = '';
	
	//Stores CTCI junction id found in HUMPreChatController.
	@TestVisible private String sCTCI = '';
	
	//Stores policy member id found in HUMPreChatController.
    @TestVisible private String sPolicyNumber = '';
    
    //Stores descrypted bot chat transcript in HUMPreChatController.
    @TestVisible private String sDecryptTranscript = '';

    //Stores descrypted Accolade Id in HUMPreChatController.
    @TestVisible private String sDecryptAcclId = '';

    //Stores descrypted Accolade Benefit Code in HUMPreChatController.
    @TestVisible private String sDecryptAcclBenefitCode = '';

    //Stores descrypted Member ID in HUMPreChatController.
    @TestVisible private String sDecryptMemberId = '';

    //Stores descrypted Dependent Code in HUMPreChatController.
    @TestVisible private String sDecryptDependentCode = '';
            
    /**
    * This parameterized Constructor accepts Account id, Interaction Id, interaction member id,Interaction name, Interacting with type,Case Origin,Case Type, Classification Id,Intent Id,Policy Member Id,CTCI junction object Id
    * <p>
    * wraps them under single object and returns to the calling entity.
    *
    * @param 	sAccId			Account Id
    * @param 	sIntId			Interaction ID
    * @param 	sIntMemberId	Interaction member ID	
    * @param 	sIntName	    Interaction Name
	* @param 	sInteractingWithType	    Interacting With Type	
	* @param 	sCaseOrigin	    Case Origin	
	* @param 	sCaseType	    Case Type	
	* @param 	sClassification	    Classification ID
	* @param 	sIntent	    	Intent ID	
	* @param 	sPolicyNumber	Policy Member ID
	* @param 	sCTCI	    	CTCI junction object ID
    */      
     global HUMPreChatResultWrapperHelper(String sAccId,String sIntId,String sIntMemberId , String sIntName, String  sInteractingWithType, String sCaseOrigin, String sCaseType, String sClassification, String sIntent,String sPolicyNumber,String sCTCI)
    {
        this.sAccId = sAccId;
        this.sIntId=sIntId;
        this.sIntMemberId=sIntMemberId;
        this.sInteractionName = sIntName;
        this.sInteractingWithType = sInteractingWithType;
        this.sCaseOrigin = sCaseOrigin;
        this.sCaseType = sCaseType;
        this.sClassification = sClassification;
        this.sIntent = sIntent;
        this.sPolicyNumber = sPolicyNumber;
		this.sCTCI = sCTCI;
    }
    
    /**
    * This parameterized Constructor accepts Decrypted session id and Decrypted IVA chat Transcript
    * <p>
    * wraps them under single object and returns to the calling entity.
    *
    * @param    sDecryptMemberGenKey        Decrypted MemberGenKey
    * @param    sDecryptLsessionID          Decrypted session Id
    * @param    sDecryptTranscript          Decrypted IVA chat Transcript
    */
     global HUMPreChatResultWrapperHelper(String sDecryptMemberGenKey,String sDecryptLsessionID, String sDecryptTranscript)
    {
        this.sDecryptMemberGenKey = sDecryptMemberGenKey;
        this.sDecryptLsessionID=sDecryptLsessionID;
        this.sDecryptTranscript=sDecryptTranscript;
    }
    
    /**
    * This parameterized Constructor accepts Decrypted session id and Decrypted IVA chat Transcript
    * <p>
    * wraps them under single object and returns to the calling entity.
    *
    * @param    sDecryptMemberGenKey        Decrypted MemberGenKey
    * @param    sDecryptLsessionID          Decrypted session Id
    * @param    sDecryptTranscript          Decrypted IVA chat Transcript
    */
    global HUMPreChatResultWrapperHelper(String sDecryptMemberGenKey,String sDecryptLsessionID, String sDecryptTranscript, String sDecryptAcclId, String sDecryptAcclBenefitCode, String sDecryptMemberId, String sDecryptDependentCode)
    {
        this.sDecryptMemberGenKey = sDecryptMemberGenKey;
        this.sDecryptLsessionID=sDecryptLsessionID;
        this.sDecryptTranscript=sDecryptTranscript;
        this.sDecryptAcclId = sDecryptAcclId;
        this.sDecryptAcclBenefitCode = sDecryptAcclBenefitCode;
        this.sDecryptMemberId = sDecryptMemberId;
        this.sDecryptDependentCode = sDecryptDependentCode;
    }
    
    /**
    * This parameterized Constructor accepts Decrypted Bot Transcript for Unsecure chat
    * <p>
    * wraps them under single object and returns to the calling entity.
    *
    * @param    sDecryptTranscript          Decrypted Bot Transcript for Unsecure chat
    */
    global HUMPreChatResultWrapperHelper(String sDecryptTranscript)
    {
        this.sDecryptTranscript=sDecryptTranscript;
    }
}