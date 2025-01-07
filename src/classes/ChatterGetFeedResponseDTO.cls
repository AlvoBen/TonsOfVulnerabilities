/******************************************************************************************************************
Apex class Name    : ChatterGetFeedResponseDTO
Version            : 1.0
Function           : This class is used as request for chatter get feed Rest Service.
Created On         : May 21 2021

Modification Log:
*   Version        Developer                   Date                Description
* -----------------------------------------------------------------------------------------------------------------
*   1.0            Moshitha Gunasekaran		   05/21/2021		   Original Version	
*   1.1			   Moshitha Gunasekaran        06/14/2021          Made changes as per latest ICD to add FeedItemId and Date functionalities				
*   1.2			   Moshitha Gunasekaran        11/09/2021          Made changes as per latest ICD to add SentBy functionalities				
------------------------------------------------------------------------------------------------------------------- */
public with sharing class ChatterGetFeedResponseDTO {
    
    public boolean Success;
    public string ErrorMessage;
    public ChatterFeedReponseDTO ChatterFeedReponse;
    public class ChatterFeedReponseDTO
    {
    	public string PersonId = '';
    	public list<ChatterFeedItemsDTO> FeedItemList;
    }
    
    public class ChatterFeedItemsDTO
    {
        public string FeedItemId = '';
        public string MessageText ='';
    	public string FeedFrom ='';
        public string FeedSentBy = '';
    	public DateTime MessageDateTime;
    	public list<ChatterFeedCommentsDTO> FeedCommentList;
    }
    
    public class ChatterFeedCommentsDTO
    {
        public string FeedCommentId = '';
    	public string MessageText ='';
    	public string FeedFrom ='';
        public string FeedSentBy = '';
    	public string MessageDateTime ='';
    }
    
}