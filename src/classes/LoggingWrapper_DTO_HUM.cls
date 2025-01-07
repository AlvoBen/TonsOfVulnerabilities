/**************************************************************************************
Apex Class Name  : LoggingWrapper_DTO_HUM
Version          : 1.0
Created Date     : 03/24/2016
Function         : This is wrapper class for logging functionality.
                    This is used to send details from one file to other.
Test Class       : LoggingWrapper_T_HUM
**************************************************************************************

Modification Log:

* Developer Name            Review Number            Date                       Description
----------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava            24069                    03/24/2016                 Original Version
* Santhi Mandava            26846                    05/04/2016                 Added SubType property
* Santhi Mandava            27512                    05/12/2016                 Added properties to support image viewer sorting
* Manish Kumar              32175                    07/25/2016                 Added properties to support encryption for Description field
* Manish Kumar                                       08/15/2016                 Worked on REQ#279125 for Attachment_Key encryption
* Anurag Shah               201051                   03/13/20118          		REQ - 354520:: SF - USER - Research/Attachments - MA Post Claims Summary.
* Anurag Shah               235263                   05/23/2018          		Reverting the Resolution changes.
*********************************************************************************************************************/

public class LoggingWrapper_DTO_HUM
{
    public String sImageString{get;set;}
    public String sAttachmentName{get;set;}
    public String sCreatedBy{get;set;}
    public String sCreatedByQueue{get;set;}   
    public String sAttachId{get;set;}
    public String sAttachCreatedDate{get;set;}
    public String sSelectedCaseId{get;set;}
    public String sAccountId{get;set;}
    public String sAuthId{get;set;}
    public String sType{get;set;}
    public String sSubType{get;set;}
    public String sJsonMap{get;set;}
    public String sJsonStandardMap{get;set;}
    public String keyDescValue{get;set;}
    public String keyValue{get;set;}
    public String sAttachmentSubType{get;set;}
}