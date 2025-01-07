/*******************************************************************************************************************************
Apex Class Name : CaseCommentView_C_HUM.cls
Version         : 1.0
Created On      : April 25 2017
Function        : Class contains methods to view Case Comments for Humana Pharmacy User
TestClass       : CaseCommentView_T_HUM

Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Vishal Verma               87024                   04/25/2017                   Original Version 
* Shreya Choodamani                                  05/30/2017                   Added the variable sPharmacyUser to check for profile of logged in user.
* Sharan Shanmugam									 08/09/2017					  REQ - 324814 : Save Case Comment to CRM during ePost failure
* Prathyu Tushar             210777                  04/06/2018                   As per Checkmarx REQ - 359259, checking the user permissions before deleting case comments in deleteComment() method. 
* Mithra Bharadwaj                                                          US#1522825 SonarQube Changes
*****************************************************************************************************************************************/
public with sharing class CaseCommentView_C_HUM 
{
   	
    /**
    * 
    * <p>
    * Constructor Method to for the class , Initialize variables
    */  
    public CaseCommentView_C_HUM(ApexPages.StandardController controller)
    {
    	lstCaseComment = new List<CaseCommentView_DTO_HUM>();
    	bHasComments= false;
    	idCase = (Id) controller.getRecord().Id;
    	sPharmacyUser =  Pharmacy_H_HUM.isPharmacyUser();
    	fetchCaseComments();
    }
    
    public id idCase {get;set;} 
    public List<CaseCommentView_DTO_HUM> lstCaseComment {get;set;}
    public boolean bHasComments {get;set;}
    public String sPharmacyUser{get;set;}
    
    /**
    * Method to fetch case comment for the case
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void fetchCaseComments()
    {
    	for(CaseComment comment : [Select LastModifiedDate, LastModifiedBy.Id, LastModifiedBy.Name,  CreatedDate, CreatedBy.Id, CreatedBy.Name, CommentBody From CaseComment c where ParentId = :idCase order by c.id desc])
    	{
    		CaseCommentView_DTO_HUM objComment = new CaseCommentView_DTO_HUM();
			objComment.cComment = comment;
			objComment.commentCreatedBy = comment.CreatedBy.Id;
			objComment.commentText = '<b>Created By: <a href="#dummy" onclick="openTabforUserInfo(\'' + comment.CreatedBy.Id +'\',\'' + comment.CreatedBy.Name +'\');">' + comment.CreatedBy.Name + '</a> (' + comment.CreatedDate.format() + ')  ';
			if(comment.createddate != comment.lastmodifieddate)
			{
				objComment.commentText += ' | Last Modified By: <a href="#dummy" onclick="openTabforUserInfo(\'' + comment.LastModifiedBy.Id +'\',\'' + comment.LastModifiedBy.Name +'\');">' + comment.LastModifiedBy.Name + '</a> (' + comment.LastModifiedDate.format() + ')</b><br>';
			}
			else
			{
				 objComment.commentText += '</b> <br>';
			}
			if(comment.CommentBody != NULL && comment.CommentBody.startswith('(') && comment.CommentBody.contains(')') )
			{
				List<String> lstStringComment = comment.CommentBody.split('\\)');
				if(lstStringComment != NULL && !lstStringComment.isEmpty() && lstStringComment.size() > 1)
				{
					objComment.commentText += '(<b>'+ lstStringComment[0].replace('(','')+'</b>)' + lstStringComment[1];
				}
				else
				{
					objComment.commentText += comment.CommentBody;
				}
			}
			else
			{
				objComment.commentText += comment.CommentBody;
			}
			
			lstCaseComment.add(objComment);
    	}
    	bHasComments = !lstCaseComment.isEmpty() ? true : false; 	
    }
    
    /**
    * Method to delete case comment for the case
    * <p>
    * @param  NA
    * 
    * @return void
    */
    public void deleteComment()
	{
		Id commentId = ApexPages.currentPage().getParameters().get('CommentId_d');
		if(CaseComment.sObjectType.getDescribe().isDeletable())
        {
            if(commentId != NULL)
                 delete new CaseComment(Id=string.escapeSingleQuotes(commentId)) ;    
        }
        else
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.FATAL,'Insufficient access')); 
	}

}