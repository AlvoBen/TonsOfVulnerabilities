/************************************************************************************************************************************************************************************       
Apex Class Name  : UserVoiceIdea_C_HUM 
Created Date     : Oct 29, 2018
Function         : Uservoice Implementations
Test Class      :  UserVoiceIdea_T_HUM

**Modification Log
* Developer Name          Code Review #         Date                       Description
************************************************************************************************************************************************************************************
* Asish Behera                                 10/29/2018                REQ - 378894 User Voice Implementation (RF) 
* Prathyu Tushar           355516              05/10/2019                REQ - 383783: UserVoice Forum id for RxRF Rapid Force.
* Prathyu Tushar           357790              05/31/2019                REQ - 402631: UserVoice Forum id for RxRF Rapid Force.
* Pinky Vijur                                  01/20/2023                3215226	T1PRJ0891339 - MF 5 - TECH - Pharmville Decommissioning - Permission Set Usage
************************************************************************************************************************************************************************************/
public  with sharing class UserVoiceIdea_C_HUM {
	
	public string sforumid {get; set;}
    public UserVoiceIdea_C_HUM(){
       UserVoiceForum_HUM__c userVoiceForum = new UserVoiceForum_HUM__c();
       userVoiceForum = UserVoiceForum_HUM__c.getValues('RapidForce'); 
       sforumid = userVoiceForum !=null ?userVoiceForum.ForumId__c:'';
    	
    }
    
}