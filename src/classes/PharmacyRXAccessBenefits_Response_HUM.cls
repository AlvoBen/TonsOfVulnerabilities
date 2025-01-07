/**************************************************************************** 
Apex Class Name  : PharmacyRXAccessBenefits_Response_HUM   
Version          : 1.0  
Created Date     : May 6, 2021
Function         : This Class is response class for KMDocumentServiceREST DoSearch
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Suraj Patil     				               05/06/2021                 REQ - 1003582 - 1011772 - 1694036
***************************************************************************************************/
public with sharing class PharmacyRXAccessBenefits_Response_HUM {
     
    public KMDSearchResults KMDSearchResults;
    
    public class KMDSearchResults {      
        public List<Documents> Documents;       
        public Error Error;        
        public Integer NumberRetrieved;        
        public String SearchReferenceID;       
        public Integer TotalDocumentsFound;
    }

    public class Documents {        
        public String FileType;        
        public String Href;        
        public String Title;
    }

    public class Error {       
        public String ErrorCode;       
        public String ErrorMessage;       
        public String Severity;
    }
}