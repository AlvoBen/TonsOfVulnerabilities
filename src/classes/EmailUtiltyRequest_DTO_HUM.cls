/******************************************************************************************************************
Apex class Name    : EmailUtiltyRequest_DTO_HUM
Version            : 1.0
Function           : Request DTO for Email Utility
Created On         : December 9 2016
Test Class         : EmailUtility_T_HUM

Modification Log:
*   Version				Developer                   Code Review         Date                Description
* -----------------------------------------------------------------------------------------------------------------                
*    1.0  				Vishal Verma			                     12/09/2016             Original Version -> REQ - 291072
------------------------------------------------------------------------------------------------------------------*/
public with sharing  class EmailUtiltyRequest_DTO_HUM 
{
	public EmailRequest SendEmailRequest;
	
	public class EmailRequest
	{
		public String FromAddress;	
		public SendToDetail SendTo;
		public CcToDetail CcTo;
		public BccToDetail BccTo;
		public String Subject;	
		public String Content;	
		public AttachmentsDetail Attachments;
		public String Priority;	
		public String IsHTMLContent;
	}
	
	public class SendToDetail
	{
		public List<String> MailTo;
	}
	
	public class CcToDetail 
	{
		public List<String> CcToAddress;
	}
	
	public class BccToDetail 
	{
		public List<String> BccToAddress;
	}
	
	public class AttachmentsDetail 
	{
		public List<FileAttachmentDetail> FileAttachment;
	}
	
	public class FileAttachmentDetail 
	{
		public String FileData;	
		public String FilenameWithExtension;	
	}
}