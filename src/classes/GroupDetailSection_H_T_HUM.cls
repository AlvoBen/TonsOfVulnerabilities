/*****************************************************************************************
Apex class Name    : GroupDetailSection_H_T_HUM
Version            : 1.0
Function           : Test class for GroupDetailSection_H_HUM
Created Date       : 22 Oct 2015

Modification Log:
*   Developer                    Code Review             Date                       Description
* -------------------------------------------------------------------------------------------------------------------------               
*  SuryaKumari Medicherla         27320                 10/22/2015             Original Version
*  SuryaKumari Medicherla                               11/02/2015             Code modified for REQ-213234(MTVx link in Division-Subgroup related list)
* Santhi Mandava                                        03/12/2019             Replaced "Endpoint_Url__c" with Endpoint_Url_HUM__c.
*************************************************************************************************************************/
@isTest
private  class GroupDetailSection_H_T_HUM
{
    /*
    * Method Name : setup
    * Description : This method is used to insert the data only once and called automicticaly in all methods
    * Return type : Void
    * Paramater   : None
    */
     @testSetup static void setup() 
     {           
        //Load all Constants data        
        HUMTestDataHelperTest.getHUMConstantsData();        
        User oUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');        
        insert oUser;        
        system.assertNotEquals(oUser.id,null,'​Value not matched');
        
        System.runAS(oUser)
        {
            HUM_Webservice_Callout__c oHUMGBESservice = new HUM_Webservice_Callout__c();
            oHUMGBESservice.name='GBEService';
            oHUMGBESservice.Active__c=true;
            oHUMGBESservice.Certificate_Name__c='Certificate';
            oHUMGBESservice.End_Point_URL__c='https://int-crmservicews.humana.com/EnrollmentServices/Group_V1.0';
            oHUMGBESservice.Timeout__c='12000';
            insert oHUMGBESservice ;
            system.assert(oHUMGBESservice.id!=null);
            system.assert(oHUMGBESservice.Name =='GBEService');
            
            Endpoint_Url_HUM__c oEPURL = new Endpoint_Url_HUM__c();
            oEPURL.Name = 'MTVx';
            oEPURL.Remote_Url__c = 'http://test-hss.humana.com/MTVxAdmin/Pages/Main.aspx';
            insert oEPURL;
            system.assert(oEPURL.id!=null);
            system.assert(oEPURL.Name =='MTVx');
            
            Endpoint_Url_HUM__c oEPSSOURL = new Endpoint_Url_HUM__c();
            oEPSSOURL.Name = 'SSO_LOGINURL';
            oEPSSOURL.Remote_Url__c = 'http://test-hss.humana.com/ITSecurity/HSSOneClick_40/LaunchHSS.aspx';
            insert oEPSSOURL;
            system.assert(oEPSSOURL.id!=null);
            system.assert(oEPSSOURL.Name =='SSO_LOGINURL');
        }
    }
    
     /*
    * Method Name : testForGBESRequest
    * Description : This method tests search and creation of account and custom settings
    * Return type : Void
    * Paramater   : None
    */
    static testMethod void testForGBESRequest()
    {
        User oUser = [Select id,Name from User WHERE Lastname='test User1' LIMIT 1];
        system.assertEquals(oUser.Name ,'test User1','​Value not matched');
        
        System.runAS(oUser)
        {
            GroupDetailSection_H_HUM objHelper1 = new GroupDetailSection_H_HUM('123456',True,'12345601','ABCDE','23','D','EM');
            system.assert(objHelper1.sVitalityIndicator =='Y');
            
            GroupDetailSection_H_HUM objHelper2 = new GroupDetailSection_H_HUM('123456',True,'12345','ABCDE','23','M','FM');
            system.assert(objHelper2.sEffectiveProvision == 'First of the Month');
            
            GroupDetailSection_H_HUM objHelper3 = new GroupDetailSection_H_HUM('123456',True,'12345','ABCDE','23','H','IM');
            system.assert(objHelper3.sUnit == 'Hours');
            
            GroupDetailSection_H_HUM objHelper4 = new GroupDetailSection_H_HUM('123456',True,'12345','ABCDE','23','','');
            system.assert(objHelper4.sSubGroupName  == 'ABCDE');
            
            GroupDetailSection_H_HUM objHelper5 = new GroupDetailSection_H_HUM('123456',True,'12345','ABCDE','23','A','W');
            system.assert(objHelper5.sSubGroupID  == '12345');
            
            GroupDetailSection_H_HUM objHelper6 = new GroupDetailSection_H_HUM('123456',True,'1234501','ABCDE','23','A','GD');
            system.assert(objHelper6.sEffectiveProvision == 'Group Decides/Decision');
        }
    }
}