/**************************************************************************** 
Apex Class Name  : MentorServiceRequest_DTO_HUM 
Created Date     : January 31, 2019
Function         : Mentor Service request DTO class.
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*------------------------------------------------------------------------------------------------------------
* Dinesh Subramaniyan                       01/30/2019                 Original Version
* Suraj Patil								06/7/2021				   DF 3001 and DF 3003 in qtest
***************************************************************************************************/
public with sharing class MentorServiceRequest_DTO_HUM {

	public Request Request;

	public class Request {
		public Boolean BenefitGridsOnly;
		public String ConsumingApp;
		public String Query;
	}
}