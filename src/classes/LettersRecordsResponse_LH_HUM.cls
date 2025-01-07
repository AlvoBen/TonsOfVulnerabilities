/***************************************************************************************************************** 
Apex Class Name  : LettersRecordsResponse_LH_HUM
Version          : 1.0  
Created Date     : 
Function         : Response class for Communication record service
Test Class       : 
*******************************************************************************************************************
Modification Log:

* Developer Name              Code Review #                       Date                        Description
*------------------------------------------------------------------------------------------------------------------
*******************************************************************************************************************/
public with sharing class LettersRecordsResponse_LH_HUM
{
    public LettersResponse LettersResponse;
    public class LettersResponse
    {
         public LettersRecordsList LettersRecordsList;   
    }
    public class LettersRecordsList
    {
        public List<LettersRecords> LettersRecords = null;
        public integer TotalRecordsFound;
    }
    
    public class LettersRecords
    {
        Public String LetterType = '';
        Public String AssociatedAuthID = '';  
        Public String AssociatedCaseID = '';
        Public String CreatedBy = '';
        Public String CreatedDate = ''; 
        Public String TemplateName = ''; 
        Public String Status = '';    
        Public String FullfillmentStatus = '';  
        Public String P2POfferedinLetter = '';  
        Public String LetterName = '';  
        Public String VoidRemarks = '';
        Public String LastModifiedBy = ''; 
        Public DateTime LastModifiedDate;
        Public String FilePath= '';  
		public string LastModifiedDateFormated = '';
    }   
}