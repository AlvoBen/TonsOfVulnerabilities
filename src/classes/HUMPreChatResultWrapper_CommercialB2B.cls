/*******************************************************************************************************************************
Apex Class Name : HUMPreChatResultWrapper_CommercialB2B 
Version         : 1.0
Created On      : 09/02/2020
Function        : 1. This Class is wrappwe class used to wrapp response sent by HUMPreChatController_CommercialB2B to Page.
Test Class 		: HUMPreChatController_CommercialB2BTest
Modification Log: 
* Modification ID	Developer Name          Code Review               Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* 1.0				Akshay Pai     				 	        		09/02/2020	                Original Version(REQ-1133858)
****************************************************************************************************************************/
global class HUMPreChatResultWrapper_CommercialB2B
{
    //Stores Account Id being decrypted in HUMPreChatController_CommercialB2B.
    @TestVisible private String sAccId = '';
    
    //Stores Interaction Created in HUMPreChatController_CommercialB2B.
    @TestVisible private String sIntId = '';
    
    //Stores Interaction Member ID created in HUMPreChatController_CommercialB2B.
    @TestVisible private String sIntMemberId = '';
    
    //Stores Interaction Member ID created in HUMPreChatController_CommercialB2B.
    @TestVisible private String sInteractionName = '';
    	
    //Stores Interaction Member ID created in HUMPreChatController_CommercialB2B.
    @TestVisible private String sDecryptLsessionID = '';
    
    //Stores case field values created in HUMPreChatController_CommercialB2B.
    @TestVisible private String sInteractingWithType = '';
    @TestVisible private String sCaseOrigin = '';
    @TestVisible private String sCaseType = '';
    @TestVisible private String sCaseRecordTypeID = '';
    
    //Stores classification id found in HUMPreChatController_CommercialB2B.
    @TestVisible private String sClassification = '';
    
    //Stores intent id found in HUMPreChatController_CommercialB2B.
    @TestVisible private String sIntent = '';
    
    //Stores CTCI junction id found in HUMPreChatController_CommercialB2B.
    @TestVisible private String sCTCI = '';
    
    //Stores policy member id found in HUMPreChatController_CommercialB2B.
    @TestVisible private String sPolicyNumber = '';
    
    //Stores descrypted bot chat transcript in HUMPreChatController_CommercialB2B.
    @TestVisible private String sDecryptTranscript = '';
    
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
    * @param 	caseRecordTypeID	    	Record Type ID of new case
	* @param    sDecryptLsessionID          Decrypted session Id
    * @param    sDecryptTranscript          Decrypted IVA chat Transcript
    */      
    global HUMPreChatResultWrapper_CommercialB2B(String sAccId,String sIntId,String sIntMemberId , String sIntName, String  sInteractingWithType, String sCaseOrigin, String sCaseType, String sClassification, String sIntent,String sPolicyNumber,String sCTCI,String sCaseRecordTypeID,String sDecryptLsessionID, String sDecryptTranscript)
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
        this.sCaseRecordTypeID = sCaseRecordTypeID;
		this.sDecryptLsessionID=sDecryptLsessionID;
        this.sDecryptTranscript=sDecryptTranscript;
    }
}