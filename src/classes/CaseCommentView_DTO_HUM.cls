/*******************************************************************************************************************************
Apex Class Name : CaseCommentView_DTO_HUM.cls
Version         : 1.0
Created On      : April 25 2017
Function        : Class contains Case Comment Structure
TestClass       : CaseCommentView_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma                 87024                  04/25/2017                   Original Version 
* Shreya Choodamani            97817                  05/30/2017                   Added the variable createdBy for REQ#: 308323.
*****************************************************************************************************************************************/
public with sharing class CaseCommentView_DTO_HUM 
{
		public CaseComment cComment {Get; Set;}
		Public String commentText {Get; Set;}
        Public String commentCreatedBy{Get;Set;}
}