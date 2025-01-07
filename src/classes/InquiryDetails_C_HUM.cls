/*******************************************************************************************************************************
Apex Class Name : InquiryDetails_C_HUM.cls
Version         : 1.0
Created On      : 8/27/2020
Function        : Class contains methods for services to fetch Inquiry Detail.
TestClass       : InquiryDetails_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Sayali Nemade 							   09/05/2020 				 REQ - 1318083 MF 3 - Archived CCP Inquiry Detail Page - High Level (Overall Look)
* Riya Agarwal							       09/07/2020                REQ - 1338123 - PR00090723 - MF 3 - CCP GCP Archived Inquiry Details Page - Task List Section  
* Sayali Nemade 					   		   09/12/2020                REQ - 1138575 Logging Component for Inquiry Details
* Sayali Gavane  	    	    				02/01/2021				 REQ- 1674034 PR00094226 - MF 4 - SF - [ECH Sonar Cube Code Fixes   	    	    
*****************************************************************************************************************************************/
public with sharing class InquiryDetails_C_HUM
{
    public String sContactId{get;set;}
    public String sInquiryId{get;set;}
    public String sContactInquiry{get; set;}
	public String sAttachment_FMUrl{get;set;}
    public String sAttachment_ILUrl{get;set;}
    public String sAttachment_MRUrl{get; set;}
	public Map<String, Map<String, Object>> standardMapForLogging = new Map<String, Map<String, Object>>();
    public String standardMapvalue{ get; set; }{standardMapvalue = ''; }
    public String sAccName{get;set;}
    public String sMemberId{get;set;}
	public List<Common_Custom_Setting__mdt > lstAttachUrl = new List<Common_Custom_Setting__mdt>();
    
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public InquiryDetails_C_HUM()
    { 
       sContactId = ApexPages.currentpage().getParameters().get('RefId');  
       sInquiryId = ApexPages.currentpage().getParameters().get('InquiryId');  
	   sContactInquiry = ApexPages.currentpage().getParameters().get('RefInqId'); 
	   sAttachment_FMUrl = GLOBAL_CONSTANT_HUM.Attachment_FMUrl;	   
       sAttachment_MRUrl = GLOBAL_CONSTANT_HUM.Attachment_MRUrl;
	   lstAttachUrl = [SELECT id ,URL__c,URL1__c,Password__c,DeveloperName FROM Common_Custom_Setting__mdt WHERE DeveloperName = 'ILUrl'];        
        if(lstAttachUrl.size()>0)
        {
            String sPwd = lstAttachUrl[0].Password__c;
            Blob blobPwd= EncodingUtil.base64Decode(sPwd);
            String decodedPwd = blobPwd.toString();            
            sAttachment_ILUrl = lstAttachUrl[0].URL__c+decodedPwd+lstAttachUrl[0].URL1__c;
        }
	    sMemberId = ApexPages.currentpage().getParameters().get('sMemberId'); 
        sAccName = ApexPages.currentpage().getParameters().get('sAccName');
		//To Send Parameters to Logging Standard Value Field
	    standardMapForLogging.put('RefId', new Map<String, Object>{'Reference ID' => sContactId});
		standardMapForLogging.put('InquiryId', new Map<String, Object>{'Inquiry ID' => sInquiryId});
		standardMapForLogging.put('sAccName', new Map<String, Object>{'Name' => sAccName});
		standardMapForLogging.put('sMemberId', new Map<String, Object>{'Member ID' => sMemberId});
		standardMapvalue = JSON.serialize(standardMapForLogging);
    } 
    
     /**
     * <p>
     * callout for Inquiry Details service
     *
     * @param  String sContactId, String sInquiryId
     * @return Object
     */
    @RemoteAction
    public static object getInquiryDetails(String sContactId, String sInquiryId)
    {
		return InquiryDetails_S_HUM.requestInquiryDetails(sContactId, sInquiryId);
    }
    
     /**
     * <p>
     * callout for Inquiry Notes service
     *
     * @param  String sContactId, String sInquiryId
     * @return Object
     */
    @RemoteAction
    public static object getInquiryNotes(String sContactId, String sInquiryId)
    {
        return InquiryDetails_S_HUM.requestInquiryNotes(sContactId, sInquiryId);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse Inquiry Details Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return String
     */
    public static object parseInquiryDetails(List<String> labels,object state)
    {
        return InquiryDetails_S_HUM.parseInquiryDetails(labels, state);
    }
    
    /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse Inquiry Notes Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return String
     */
    public static object parseInquiryNotes(List<String> labels,object state)
    {
        return InquiryDetails_S_HUM.parseInquiryNotes(labels, state);
    }
	
     /**
     * <p>
     * callout for Inquiry detail attachment service
     *
     * @param  String sContactId, sInquiryId
     * @return Object
     */
     @RemoteAction
    public static object getInquiryAudit(String sContactId, String sInquiryId )
    {      
		return InquiryDetails_S_HUM.requestInquiryAudit(sContactId, sInquiryId);
    }
	
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse Inquiry Detail Audit Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return String
     */
    public static object parseInquiryAudit(List<String> labels,object state)
    {        
        return InquiryDetails_S_HUM.parseInquiryAudit(labels, state);
    }
	
     /**
     * <p>
     * callout for Inquiry Web Response service
     *
     * @param  String sContactId, sInquiryId
     * @return Object
     */
     @RemoteAction
    public static object getInquiryWebResponse(String sContactId, String sInquiryId )
    {    
		return InquiryDetails_S_HUM.requestInquiryWebResponse(sContactId, sInquiryId);
    }
	
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse Inquiry web response Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return String
     */
    public static object parseInquiryWebResponse(List<String> labels,object state)
    {        
        return InquiryDetails_S_HUM.parseInquiryWebResponse(labels, state);
    }
	
	/**
     * <p>
     * callout for Inquiry detail attachment service
     *
     * @param  String sContactId, sInquiryId 
     * @return Object
     */
    @RemoteAction
    public static object getInquiryAttachment(String sContactId, String sInquiryId )
    {    
		return InquiryDetails_S_HUM.requestInquiryAttachment(sContactId, sInquiryId);
    }
	
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse Inquiry Attachment Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return String
     */
    public static object parseInquiryAttachment(List<String> labels,object state)
    {
        return InquiryDetails_S_HUM.parseInquiryAttachment(labels, state);
    }
	
	/**
     * <p>
     * callout for Inquiry detail Task service
     *
     * @param  String sContactId, sInquiryId 
     * @return Object
     */
     @RemoteAction
    public static object getInquiryTaskDetails(String sContactId, String sInquiryId )
    {      
        return InquiryDetails_S_HUM.requestInquiryTaskDetails(sContactId, sInquiryId);
    }
     /**
     * Returns responses of continuation object
     * <p>
     * Callback Method to parse InquiryDetail Task Service
     *
     * @param  List<String> List of Continuation Labels
     * @param  Object State of Continuation
     * @return string
     */
    public static object parseInquiryTaskDetail(List<string> labels,object state)
    { 
        return InquiryDetails_S_HUM.parseInquiryTaskDetails(labels, state);
    }
}