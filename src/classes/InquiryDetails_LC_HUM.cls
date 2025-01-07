/*******************************************************************************************************************************
Apex Class Name : InquiryDetails_LC_HUM.cls
Version         : 1.0
Created On      : 8/27/2020
Function        : Class contains methods for services to fetch Inquiry Detail.
TestClass       : InquiryDetails_LT_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Ritik Agarwal                                       03/24/2021                 Initial version(US #)
*****************************************************************************************************************************************/
    public with sharing class InquiryDetails_LC_HUM {
            
        /**
    * <p>
    * callout for Inquiry Details service
    *
    * @param  String sContactId, String sInquiryId
    * @return Object
    */
        @AuraEnabled(continuation=true)
        public static object getInquiryDetails(String sContactId, String sInquiryId)
        {   
            return InquiryDetails_S_HUM.requestInquiryDetails(sContactId, sInquiryId);
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
        @AuraEnabled
        public static object parseInquiryDetails(List<String> labels,object state)
        {
            return InquiryDetails_S_HUM.parseInquiryDetails(labels, state);
        }
        
  /**
    * <p>
    * callout for Inquiry Notes service
    *
    * @param  String sContactId, String sInquiryId
    * @return Object
    */
        @AuraEnabled(continuation=true)
        public static object getInquiryNotes(String sContactId, String sInquiryId)
        {
            return InquiryDetails_S_HUM.requestInquiryNotes(sContactId, sInquiryId);
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
        @AuraEnabled
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
        @AuraEnabled(continuation=true)
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
        @AuraEnabled
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
        @AuraEnabled(continuation=true)
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
        @AuraEnabled
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
        @AuraEnabled(continuation=true)
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
        @AuraEnabled
        public static object parseInquiryAttachment(List<String> labels,object state)
        {   
            system.debug('attachment'+InquiryDetails_S_HUM.parseInquiryAttachment(labels, state));
            return InquiryDetails_S_HUM.parseInquiryAttachment(labels, state);
        }
        
        /**
    * <p>
    * callout for Inquiry detail Task service
    *
    * @param  String sContactId, sInquiryId 
    * @return Object
    */
        @AuraEnabled(continuation=true)
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
        @AuraEnabled
        public static object parseInquiryTaskDetail(List<string> labels,object state)
        { 
            return InquiryDetails_S_HUM.parseInquiryTaskDetails(labels, state);
        }
    }