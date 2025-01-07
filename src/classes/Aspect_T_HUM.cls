/*****************************************************************************************
Apex class Name    : Aspect_T_HUM
Version            : 1.0
Function           : This is test class for Aspect_I_HUM
Created Date       : 17 November 2016

Modification Log:
*   Developer                           Code Review             Date                   Description
* ------------------------------------------------------------------------------------------------------------------------------                
*   Shruthi Karanth                     48462                 11/17/2016              Original version 
*   Shruthi Karanth                                           12/01/2016              Updated test class to cover calculation of APS
********************************************************************************************************************************/
@isTest
private class Aspect_T_HUM
{
   /**
    * testsetup method to create test data
    *<p>
    * @param            NULL
    * @return           void
    */
    @testSetup static void prepareTestData()
    {
        List<User> lstUser = new List<User>();
        User oUser1 = HUMTestDataHelperTest.createTestUser('User1','System Administrator');
        User oUser2 = HUMTestDataHelperTest.createTestUser('User2','ETL API Access');
        User oUser3 = HUMTestDataHelperTest.createTestUser('User3','Customer Care Specialist');
        User oUser4 = HUMTestDataHelperTest.createTestUser('User4','Customer Care Specialist');
        lstUser.add(oUser1);
        lstUser.add(oUser2);
        lstUser.add(oUser3);
        lstUser.add(oUser4);
        insert lstUser;
        
        System.assert(lstUser[0].LanguageLocaleKey == 'en_US');
        
        System.runAs (oUser1) 
        {
            List<Skill> lstSkill = [select id from skill limit 10];
            List<SkillUser> lstSkillUser = [Select SkillId, UserId FROM SkillUser LIMIT 10];
            List<SkillProfile> lstSkillProfile = [Select SkillId, ProfileId FROM SkillProfile LIMIT 10];             
            
            livechatvisitor lcvI = new livechatvisitor();
            insert lcVI;
            System.assert(lcVI.Id != null); 
                
            List<LiveChatTranscript> lstChatTrans = new List<LiveChatTranscript>();
            for(Integer i = 0; i < 100; i++)
            {      
                livechattranscript lctI1 = new livechattranscript(LIVECHATVISITORID=lcVI.Id, Secondary_Chat_ID__c ='TestSecondChat'+'_i'+i+'_i', STATUS='Completed', SkillId = lstSkill[0].Id, StartTime = system.Now().addminutes(-15),EndTime = system.Now().addminutes(-10)); 
                lstChatTrans.add(lctI1);
            }    
            for(Integer j = 0; j < 100; j++)
            {
                livechattranscript lctI2 = new livechattranscript(LIVECHATVISITORID=lcVI.Id, Secondary_Chat_ID__c ='TestSecondChat'+'_j'+j+'_j', STATUS='Missed', SkillId = lstSkill[1].Id, EndedBy = 'Visitor',StartTime = system.Now().addminutes(-10),EndTime = system.Now().addminutes(-5)); 
                lstChatTrans.add(lctI2);
            }
            for(Integer k = 0; k < 100; k++)
            {
                livechattranscript lctI3 = new livechattranscript(LIVECHATVISITORID=lcVI.Id, Secondary_Chat_ID__c ='TestSecondChat'+'_k'+k+'_k', STATUS='Missed', SkillId = lstSkill[2].Id, EndedBy = 'Agent',StartTime = system.Now().addminutes(-5),EndTime = system.Now().addminutes(-1));
                lstChatTrans.add(lctI3);
            }
            
            insert lstChatTrans;
            System.assert(lstChatTrans.size() == 300);
            
            List<LiveAgentSession> lstSession = new List<LiveAgentSession>();
            for(Integer i = 0; i < 100; i++)
            {
                LiveAgentSession oSession1 = new LiveAgentSession(TimeInOnlineStatus = 30, LoginTime = system.Now().addminutes(-15), LogoutTime = system.Now().addminutes(-10));
                if(lstSkillUser.size() > 0) oSession1.AgentId = lstSkillUser[0].UserId;
                lstSession.add(oSession1);
            }
            for(Integer j = 0; j < 100; j++)
            {
                LiveAgentSession oSession2 = new LiveAgentSession(TimeInOnlineStatus = 30, LoginTime = system.Now().addhours(-3), LogoutTime = system.Now().addhours(-2));
                if(lstSkillUser.size() > 1) 
                {
                    oSession2.AgentId = lstSkillUser[1].UserId;
                }
                else if(lstSkillUser.size() > 0) 
                {
                    oSession2.AgentId = lstSkillUser[0].UserId;
                }
                lstSession.add(oSession2);
            }
            insert lstSession;
            System.assert(lstSession.size() == 200);
            
        }
    }     
  
    /**
    * testMethodForPositive
    *<p>
    *
    * @param            NULL
    * @return           void
    */
    static testMethod void testServiceResponse()
    {       
        User oUserR = [Select id,Current_Queue__c from user where LastName = 'test User2' limit 1 ];
        
        System.runAs (oUserR) 
        {
            AspectRequest_DTO_HUM oRequest = new AspectRequest_DTO_HUM ();
            oRequest.StartDateTime = String.valueOf(system.Now().addMinutes(-30));
            oRequest.EndDateTime = String.valueOf(system.Now());
            list<AspectResponse_DTO_HUM> lstResponse = new list<AspectResponse_DTO_HUM>();
            lstResponse = Aspect_WS_HUM.getChatData(oRequest);
            
            System.assert(lstResponse.size() == 3);     
        }
    }
    
    /**
    * testMethodForSessionRecordResponse
    *<p>
    *
    * @param            NULL
    * @return           void
    */
    static testMethod void testSessionOnlykResponse()
    {       
        User oUserR = [Select id,Current_Queue__c from user where LastName = 'test User2' limit 1 ];
        
        System.runAs (oUserR) 
        {
            AspectRequest_DTO_HUM oRequest = new AspectRequest_DTO_HUM ();
            oRequest.StartDateTime = String.valueOf(system.Now().addHours(-4));
            oRequest.EndDateTime = String.valueOf(system.Now().addHours(-1));
            list<AspectResponse_DTO_HUM> lstResponse = new list<AspectResponse_DTO_HUM>();
            lstResponse = Aspect_WS_HUM.getChatData(oRequest);
            System.assert(lstResponse.size() > 0);     
        }
    }
    
    /**
    * testMethodForBlankResponse
    *<p>
    *
    * @param            NULL
    * @return           void
    */
    static testMethod void testBlankResponse()
    {       
        User oUserR = [Select id,Current_Queue__c from user where LastName = 'test User2' limit 1 ];
        
        System.runAs (oUserR) 
        {
            AspectRequest_DTO_HUM oRequest = new AspectRequest_DTO_HUM ();
            oRequest.StartDateTime = String.valueOf(system.Now());
            oRequest.EndDateTime = String.valueOf(system.Now());
            list<AspectResponse_DTO_HUM> lstResponse = new list<AspectResponse_DTO_HUM>();
            lstResponse = Aspect_WS_HUM.getChatData(oRequest);
            System.assert(lstResponse.size() == 0);     
        }
    }
}