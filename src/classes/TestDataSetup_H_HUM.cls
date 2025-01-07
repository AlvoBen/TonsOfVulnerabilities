/**********************************************************************************************************************
Apex Class   : TestDataSetup_H_HUM
Description  : Utility class for creating test data that can be used in Test Classes across the Application.
               This class is made public since it is referenced by other classes.
Created By   : Santhi Mandava
Created On   : 23/10/2015

Modification Log :
-----------------------------------------------------------------------------------------------------------------------
* Developer             Code Review            Date                    Description
* --------------------------------------------------------------------------------------------------------------------
* Santhi Mandava        27372                  10/23/2015              Original version
* Santhi Mandava        27518                  11/03/2015              Added test data to cover single sign-on code.
* Harika Devi Kalidindi 112000                 07/20/2017              REQ - 323435 :Added Policy_Group_Number__c field value while inserting policy
***********************************************************************************************************************/
@isTest
public class TestDataSetup_H_HUM
{
    public static void setupCustomLinksData()
    {
        Id grpRecordTypeId = Schema.SObjectType.Account.getRecordTypeInfosByName().get('Group').getRecordTypeId();
        
        Account acc = HUMTestDataHelperTest.getAccount();
        acc.name = 'test';
        acc.Source_Platform_Code__c ='EM';
        acc.Source_Customer_Key__c = 'XY';
        acc.Recordtypeid = grpRecordTypeId ;
        insert acc;
        system.assertNotEquals(Null,acc.id);
        system.assertequals(grpRecordTypeId ,acc.Recordtypeid);
        
        Policy__c objPolicy = new Policy__c();
        objPolicy.Group_Name__c = acc.Id;
        objPolicy.Policy_Group_Number__c='11111';
        insert objPolicy;
        system.assertequals(acc.Id ,objPolicy.Group_Name__c  );
        
        Policy_Member__c objPmemb = new Policy_Member__c();
        objPmemb.Policy__c = objPolicy.Id;
        objPmemb.Name = 'HR0234';
        insert objPmemb;
        system.assertequals(objPolicy.Id ,objPmemb.Policy__c );
        
        Link_Layout__c objlnklayout = new Link_Layout__c();
        objlnklayout.Target_Name__c = 'Policy_Member__c';
        objlnklayout.Target_Type__c = 'sObject';
        objlnklayout.Active__c = true;
        insert objlnklayout;
        system.assertNotEquals(Null,objlnklayout.id);
        system.assertequals('Policy_Member__c',objlnklayout.Target_Name__c);
        
        Link_Layout_Section__c lnklayoutsec1 = new Link_Layout_Section__c();
        lnklayoutsec1.Index__c = 1;
        lnklayoutsec1.Link_Layout__c = objlnklayout.id;
        lnklayoutsec1.Section_Name__c = 'Employer';
        insert lnklayoutsec1;
        system.assertNotEquals(Null,lnklayoutsec1.id);
        system.assertequals('Employer',lnklayoutsec1.Section_Name__c);
        
        Link_Layout_Section__c lnklayoutsec2 = new Link_Layout_Section__c();
        lnklayoutsec2.Index__c = 2;
        lnklayoutsec2.Link_Layout__c = objlnklayout.id;
        lnklayoutsec2.Section_Name__c = 'Billing';
        insert lnklayoutsec2;
        system.assertNotEquals(Null,lnklayoutsec2.id);
        system.assertequals('Billing',lnklayoutsec2.Section_Name__c);
        
        Link__c objlnk = new Link__c();
        objlnk.Active__c = true;
        objlnk.Index__c = 1;
        objlnk.Layout_Section__c = lnklayoutsec1.Id;
        objlnk.Link_Action__c = 'Basic';
        objlnk.Link_Label__c = 'Google';
        objlnk.Target_Type__c = 'Window';
        objlnk.Url__c = 'https://www.google.com';
        objlnk.RequireSSO__c =true;
        insert objlnk;
        system.assertNotEquals(Null,objlnk.id);
        system.assertequals('https://www.google.com',objlnk.Url__c );
        
        Link__c objlnkQS = new Link__c();
        objlnkQS.Active__c = true;
        objlnkQS.Index__c = 2;
        objlnkQS.Layout_Section__c = lnklayoutsec2.Id;
        objlnkQS.Link_Action__c = 'Query String';
        objlnkQS.Link_Label__c = 'GroupContact';
        objlnkQS.Target_Type__c = 'Subtab';
        objlnkQS.Url__c = '/apex/HumGroupContact?';
        insert objlnkQS;
        system.assertNotEquals(Null,objlnkQS.id);
        system.assertequals('Query String',objlnkQS.Link_Action__c );
        
        Link__c objlnkQS1 = new Link__c();
        objlnkQS1.Active__c = true;
        objlnkQS1.Index__c = 3;
        objlnkQS1.Layout_Section__c = lnklayoutsec2.Id;
        objlnkQS1.Link_Action__c = 'Query String';
        objlnkQS1.Link_Label__c = 'SPIDAR';
        objlnkQS1.Target_Type__c = 'Tab';
        objlnkQS1.Url__c = 'https://associates.humana.com/SPIDAR/viewer.asp?';
        objlnkQS1.RequireSSO__c =true;
        insert objlnkQS1;
        system.assertNotEquals(Null,objlnkQS1.id);
        system.assertequals('Tab',objlnkQS1.Target_Type__c);
        
        Link__c objlnkQS2 = new Link__c();
        objlnkQS2.Active__c = true;
        objlnkQS2.Index__c = 4;
        objlnkQS2.Layout_Section__c = lnklayoutsec2.Id;
        objlnkQS2.Link_Action__c = 'Query String';
        objlnkQS2.Link_Label__c = 'SPIDAR1';
        objlnkQS2.Target_Type__c = 'Tab';
        objlnkQS2.Url__c = 'https://associates.humana.com/SPIDAR/viewer.asp';
        objlnkQS2.RequireSSO__c =true;
        insert objlnkQS2;
        system.assertNotEquals(Null,objlnkQS2.id);
        system.assertequals('Tab',objlnkQS2.Target_Type__c);
        
        Link__c objlnkp = new Link__c();
        objlnkp.Active__c = true;
        objlnkp.Index__c = 5;
        objlnkp.Layout_Section__c = lnklayoutsec1.Id;
        objlnkp.Link_Action__c = 'Post';
        objlnkp.Link_Label__c = 'Googlea';
        objlnkp.Target_Type__c = 'Window';
        objlnkp.Url__c = 'https://www.google.com';
        insert objlnkp;
        system.assertNotEquals(Null,objlnkp.id);
        system.assertequals('https://www.google.com',objlnkp.Url__c );
        
        Link__c objlnkpsign = new Link__c();
        objlnkpsign.Active__c = true;
        objlnkpsign.Index__c = 6;
        objlnkpsign.Layout_Section__c = lnklayoutsec1.Id;
        objlnkpsign.Link_Action__c = 'Post';
        objlnkpsign.Link_Label__c = 'Googlesign';
        objlnkpsign.Target_Type__c = 'Window';
        objlnkpsign.Url__c = 'https://www.google.com';
        objlnkpsign.RequireSSO__c =true;
        insert objlnkpsign ;
        system.assertNotEquals(Null,objlnkpsign.id);
        system.assertequals('https://www.google.com',objlnkpsign.Url__c );
        
        Link__c objlnkBasic = new Link__c();
        objlnkBasic.Active__c = true;
        objlnkBasic.Index__c = 1;
        objlnkBasic.Layout_Section__c = lnklayoutsec1.Id;
        objlnkBasic.Link_Action__c = 'Basic';
        objlnkBasic.Link_Label__c = 'Google';
        objlnkBasic.Target_Type__c = 'Window';
        objlnkBasic.Url__c = 'https://www.google.com';
        insert objlnkBasic;
        system.assertNotEquals(Null,objlnkBasic.id);
        system.assertequals('https://www.google.com',objlnkBasic.Url__c );
        
        Link_Parameter__c lnkparam = new Link_Parameter__c();
        lnkparam.Link__c = objlnkQS.Id;
        lnkparam.Variable_Name__c = 'Id';
        lnkparam.Variable_Source__c = 'Id';
        lnkparam.Variable_Type__c = 'Database Field';
        insert lnkparam;
        system.assertNotEquals(Null,lnkparam.id);
        system.assertequals(objlnkQS.Id,lnkparam.Link__c);
        
        Link_Parameter__c lnkparam1 = new Link_Parameter__c();
        lnkparam1.Link__c = objlnkQS1.Id;
        lnkparam1.IsConditional__c =true;
        lnkparam1.Variable_Name__c = 'srcPC';
        lnkparam1.Variable_Source__c = 'Policy__r.Group_Name__r.Source_Platform_Code__c';
        lnkparam1.Variable_Type__c = 'Database Field';
        insert lnkparam1;
        system.assertNotEquals(Null,lnkparam1.id);
        system.assertequals(objlnkQS1.Id,lnkparam1.Link__c);
        
        Link_Parameter__c lnkparam2 = new Link_Parameter__c();
        lnkparam2.Link__c = objlnkQS1.Id;
        lnkparam2.Variable_Name__c = 'srcCK';
        lnkparam2.IsConditional__c =true;
        lnkparam2.Variable_Source__c = 'Policy__r.Group_Name__r.Source_Customer_Key__c';
        lnkparam2.Variable_Type__c = 'Database Field';
        insert lnkparam2;
        system.assertNotEquals(Null,lnkparam2.id);
        system.assertequals(objlnkQS1.Id,lnkparam2.Link__c);
         
        Link_Parameter__c lnkparam3 = new Link_Parameter__c();
        lnkparam3.Link__c = objlnkQS1.Id;
        lnkparam3.Variable_Name__c = 'target';
        lnkparam3.Variable_Source__c = 'Policy';
        lnkparam3.Variable_Type__c = 'Static';
        insert lnkparam3;
        system.assertNotEquals(Null,lnkparam3.id);
        system.assertequals(objlnkQS1.Id,lnkparam3.Link__c);
         
        Link_Conditional_Parameter__c lnkCondParam = new Link_Conditional_Parameter__c();     
        lnkCondParam.Conditional_Check__c = 'EM';
        lnkCondParam.Conditional_Type__c = 'IF';
        lnkCondParam.Conditional_Value__c = 'E';
        lnkCondParam.Link_Parameter__c = lnkparam1.id;
        insert lnkCondParam;
        system.assertNotEquals(Null,lnkCondParam.id);
        system.assertequals(lnkparam1.id,lnkCondParam.Link_Parameter__c );
        
        Link_Conditional_Parameter__c lnkCondParam1 = new Link_Conditional_Parameter__c();     
        lnkCondParam1.Conditional_Check__c = 'SC';
        lnkCondParam1.Conditional_Type__c = 'IF';
        lnkCondParam1.Conditional_Value__c = 'S';
        lnkCondParam1.Link_Parameter__c = lnkparam2.id;
        insert lnkCondParam1;
        system.assertNotEquals(Null,lnkCondParam1.id);
        system.assertequals(lnkparam2.id,lnkCondParam1.Link_Parameter__c );
        
        Link_Conditional_Parameter__c lnkCondParam2 = new Link_Conditional_Parameter__c();     
        lnkCondParam2.Conditional_Check__c = 'AB';
        lnkCondParam2.Conditional_Type__c = 'ELSE';
        lnkCondParam2.Conditional_Value__c = 'AB';
        lnkCondParam2.Link_Parameter__c = lnkparam2.id;
        insert lnkCondParam2;
        system.assertNotEquals(Null,lnkCondParam2.id);
        system.assertequals(lnkparam2.id,lnkCondParam2.Link_Parameter__c );
        
        Link_Criteria__c lnkcrt = new Link_Criteria__c();
        lnkcrt.Criteria_Source__c = 'Name';
        lnkcrt.Criteria_Type__c = 'Database Field';
        lnkcrt.Expected_Value__c = objPmemb.name;
        lnkcrt.Link__c = objlnkQS.Id;
        insert lnkcrt;
        system.assertNotEquals(Null,lnkcrt.id);
        system.assertequals('HR0234',lnkcrt.Expected_Value__c);
        
        Link_Criteria__c lnkcrt1 = new Link_Criteria__c();
        lnkcrt1.Criteria_Source__c = 'Name';
        lnkcrt1.Criteria_Type__c = 'User Profile';
        lnkcrt1.Expected_Value__c = 'Developer';
        lnkcrt1.Link__c = objlnkQS1.Id;
        insert lnkcrt1;
        system.assertNotEquals(Null,lnkcrt1.id);
        system.assertequals('Developer',lnkcrt1.Expected_Value__c);
        
        Link_Criteria__c lnkcrt2 = new Link_Criteria__c();
        lnkcrt2.Criteria_Source__c = 'Name';
        lnkcrt2.Criteria_Type__c = 'Database Object';
        lnkcrt2.Expected_Value__c = 'Policy_Member__c';
        lnkcrt2.Link__c = objlnkQS1.Id;
        insert lnkcrt2;
        system.assertNotEquals(Null,lnkcrt2.id);
        system.assertequals('Policy_Member__c',lnkcrt2.Expected_Value__c);
        
        List<Link_Criteria__c> lstCtri = [Select Name,Link__c  from Link_Criteria__c order by Link__c];
        String strExpre='';
        
        For(Link_Criteria__c obj : lstCtri)
        {
            if(obj.Link__c == objlnkQS1.Id)
            {
                strExpre = strExpre +' AND '+obj.Name;
            }
            else
            {
                objlnkQS.Expression__c =  obj.Name;
            }
        }
        strExpre = strExpre.substring(4);
        objlnkQS1.Expression__c =  strExpre;
        update objlnkQS;
        update objlnkQS1;
        system.assertNotEquals(null,objlnkQS.Expression__c);
        system.assertEquals(strExpre,objlnkQS1.Expression__c);
    }
}