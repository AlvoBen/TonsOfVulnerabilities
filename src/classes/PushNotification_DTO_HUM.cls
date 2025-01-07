/***********************************************************************************************************************
Apex Class Name  : PushNotification_DTO_HUM
Version          : 1.0  
Created Date     : June 18, 2021
Function         : This is the DTO for the request of the Push Notification service for Coaching cases.
Test Class       : HUMFeedItemHelperTest
****************************************************************************
Modification Log:

   Developer                           Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Moshitha Gunasekaran                 06/18/2021           Original version. 
* Moshitha Gunasekaran                 07/27/2021           Made changes as per latest payload updates			
*********************************************************************************************************************************/
public with sharing class PushNotification_DTO_HUM 
{
    public string Person_Id;
    public FeedDataDTO FeedData;
	
	public class FeedDataDTO
	{
		public string FeedCommentId = '';
		public string FeedItemId = '';
	}
}