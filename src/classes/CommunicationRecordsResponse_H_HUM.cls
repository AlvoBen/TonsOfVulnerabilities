/***************************************************************************************************************** 
Apex Class Name  : CommunicationRecordsResponse_H_HUM
Version          : 1.0  
Created Date     : December 03, 2019
Function         : Response class for Communication record service
Test Class       : 
*******************************************************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------------
* Prasanthi Kandula                                             12/03/2019                   Original Version 
*******************************************************************************************************************/
public with sharing class CommunicationRecordsResponse_H_HUM
{
    public CommunicationRecordsResponse CommunicationRecordsResponse;
    public class CommunicationRecordsResponse
    {
         public CommunicationRecordsList CommunicationRecordsList;   
    }
    public class CommunicationRecordsList
    {
        public List<CommunicationRecords> CommunicationRecords = null;
        public integer TotalRecordsFound;
   }
   public class CommunicationRecords
   {
        public string RecordType = '';
        public string CreatedDate;
        public string CreatedBy = '';
        public DateTime LastModifiedDate;        
        public string LastModifiedBy = '';
        public string Department = '';
        public string CommunicationType = '';
        public string ContactMethod = '';
        public string ContactType =  '';
        public string ContactAttemptNo = '';
        public List<string> ContactResult;
        public string ContactResultOther = '';
        public string Notes = '';
        public string Details = '';
		public string LastModifiedDateFormated = '';
   }
   
}