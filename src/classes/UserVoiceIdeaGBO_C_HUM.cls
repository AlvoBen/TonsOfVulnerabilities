/************************************************************************************************************************************************************************************       
Apex Class Name  : UserVoiceIdeaGBO_C_HUM 
Created Date     : April 1, 2019
Function         : Uservoice Implementations
Test Class      :  UserVoiceIdeaGBO_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
 * Sumanth/Riddhi           349066             04/01/2019               REQ - 389690 User Story - Implement UserVoice in GBO Rapid Force App (GBORF) (ID# 35) 
************************************************************************************************************************************************************************************/
public  with sharing class UserVoiceIdeaGBO_C_HUM {
  
  public string sforumid {get; set;}
  public UserVoiceIdeaGBO_C_HUM(){
  UserVoiceForum_HUM__c userVoiceForum = UserVoiceForum_HUM__c.getValues('GBORapidForce');
  sforumid = userVoiceForum !=null ?userVoiceForum.ForumId__c:'';
      
    }
    
}