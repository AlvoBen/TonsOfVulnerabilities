/*******************************************************************************************************************************
Apex Class Name : HPPreChatResultWrapperHelper 
Version         : 1.0
Created On      : 09/26/2014
Function        : 1. This Class is wrappwe class used to wrapp response sent by HPPreChatResultWrapperHelper to Page.
Test Class 		: HUMPreChatControllerTest
Modification Log: 
 * Modification ID	Developer Name          Code Review             Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0				Sai Sree Lakshmi                              	04/11/2019                  Original Version
 * 1.1				Alvaro Madrid									07/14/2023					
 ****************************************************************************************************************************/
global class HPPreChatResultWrapperHelper
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
    @TestVisible private String sAccountName = '';
    
    //Stores boolean value to true if member not found..
	@TestVisible private boolean bIsMemberPresent = false;
	
	//Stores descrypted bot chat transcript in HPPreChatController_C_HUM.
	@TestVisible private String sDecryptTranscript = '';
	
	/**
    * This parameterized Constructor accepts Account id, INteraction Id and interaction member id
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
    */
	global HPPreChatResultWrapperHelper(String sAccId,String sIntId,String sIntMemberId , String sIntName, String  sInteractingWithType, String sCaseOrigin, String sCaseType, String sAccountName)
	{
		this.sAccId = sAccId;
		this.sIntId=sIntId;
		this.sIntMemberId=sIntMemberId;
		this.sInteractionName = sIntName;
		this.sInteractingWithType = sInteractingWithType;
        this.sCaseOrigin = sCaseOrigin;
        this.sCaseType = sCaseType;
        this.sAccountName = sAccountName;
	}
	
	/*
     * Method Name   :    HUMPreChatResultWrapperHelper()
     * Description   :    
     * return Type   :    NA
     * Parameters    :    member gen key , Session ID, sDecryptTranscript
     */
    /**
    * This parameterized Constructor accepts Decrypted member gen key and session id
    * <p>
    * wraps them under single object and returns to the calling entity.
    *
    */
	global HPPreChatResultWrapperHelper(String sDecryptMemberGenKey,String sDecryptLsessionID, Boolean bIsMemberPresent, String sDecryptTranscript)
	{
		this.sDecryptMemberGenKey = sDecryptMemberGenKey;
		this.sDecryptLsessionID = sDecryptLsessionID;
		this.bIsMemberPresent = bIsMemberPresent;
		this.sDecryptTranscript = sDecryptTranscript;
	}
}