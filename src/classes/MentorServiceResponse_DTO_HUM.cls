/**************************************************************************** 
Apex Class Name  : MentorServiceResponse_DTO_HUM 
Created Date     : January 31, 2019
Function         : Mentor Service response DTO class.
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Dinesh Subramaniyan                       01/31/2019                 Original Version
***************************************************************************************************/
public with sharing class MentorServiceResponse_DTO_HUM {
   
   public HasSearchResultsResponse HasSearchResultsResponse;

	public class Error {
		public String ErrorCode;
		public String ErrorMessage;
		public String Severity;
	}

	public class HasSearchResultsResponse {
		public Error Error;
		public String HasMatches;
	} 
}