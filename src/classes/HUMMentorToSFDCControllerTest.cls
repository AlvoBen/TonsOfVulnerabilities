/****************************************************************************************************************************************
Apex Class Name  : HUMMentorToSFDCControllerTest
Version          : 1.3
Created Date     : June 19 2014
Function         : Test class for HUMMentorToSFDCController
 *****************************************************************************************************************************************

Modification Log:

 * Developer Name              Code Review                Date                       Description
 *----------------------------------------------------------------------------------------------------------------------------------------
 * Pranali/Krishna              17109                    06/19/2014                 Original Version
 * Pranali                      17109                    06/24/2014                 Added duplicate and closed case tests
 * Pranali                      17109                    08/05/2014                 Made changes to incorporate best practices
 * Asha Deepika Koyyana         20572                    01/28/2015                 Fixed test class failure for Defect#164139
 * Santhi Mandava                                        03/12/2019                 Removed "DpaaSWebServiceMockImpl" usage. 
 * Rakesh Patni                                          08/12/2019                 REQ-405880 - Launch Compass Functionality 
 *****************************************************************************************************************************************/

@isTest
private class HUMMentorToSFDCControllerTest
{
	/*
     * Method Name : setup
     * Description : Test method to create Test records
     * Return type : void
     * Paramater   : NA
     */
     @testSetup static void setup()
    {
        Group oNewGroup = null;
        oNewGroup = new Group(Name ='Test Business Process and Testing', Type = 'Queue');
        insert oNewGroup;     
		system.assertEquals(oNewGroup.Type,'Queue');
		
        QueueSObject oNewQueue = new QueueSObject(QueueId = oNewGroup.Id, SObjectType = 'Work_Queue_Setup__c');
        insert oNewQueue;
		system.assertEquals(oNewQueue.SObjectType,'Work_Queue_Setup__c');

        User oCCSUser = HUMTestDataHelperTest.createTestUser('QuickStartSpecialist','Customer Care Specialist');
        oCCSUser.Mentor_Sub_Function__c = 'Go365';
        oCCSUser.Current_Queue__c='Test Business Process and Testing';
        insert oCCSUser;
        system.assertEquals(oCCSUser.IsActive,true);        

        system.runAs(oCCSUser)
        {
        	List<Classification_Type__c> lstClassType= new List<Classification_Type__c>();
            
            Classification_Type__c oClassType = new Classification_Type__c();
            oClassType.Name = 'Calls';
            oClassType.Is_Active__c = true;
            oClassType.Mentor_Function__c ='Contact Center GBO';
            lstClassType.add(oClassType);         
            insert lstClassType;
            System.assert(lstClassType[0].Is_Active__c==true);
        	
            Work_Queue_Setup__c oWorkQueue = new Work_Queue_Setup__c(Name = 'Test Business Process and Testing',Classification_Type__c=oClassType.id, OwnerId = oNewGroup.Id, Service_Center__c = 'Business', Department__c = 'Process and Testing');
            insert oWorkQueue;    
            System.assertEquals(oWorkQueue.Service_Center__c,'Business');
            
        }       
    }
    /*
     * Method Name : testAttachToCase
     * Description : Test method to successfully insert article for a case which is not closed or cancelled
     * Return type : void
     * Paramater   : NA
     */
	@isTest
    static void testAttachToCase()
    {
        Case oCase = HUMTestDataHelperTest.getCase();
        oCase.Status = 'New';  
        insert oCase;
        PageReference pageRef = Page.HUMMentorToSFDC;
        Test.setCurrentPage(pageRef);       
        ApexPages.CurrentPage().getParameters().put('caseid',oCase.Id);
        ApexPages.CurrentPage().getParameters().put('casename','test name');
        Compass__c objCompass = HUMTestDataHelperTest.getCompassRecord();
        ApexPages.StandardController stdCntrl = new ApexPages.StandardController(objCompass);
        HUMMentorToSFDCController objMentorClass = new HUMMentorToSFDCController(stdCntrl);

        //Test for a article for a case which is not closed
        HUMMentorToSFDCHelper compPayload = new HUMMentorToSFDCHelper('test', '09000929806ec765', 'www.salesforce.com' );    
        String sPayload = JSON.serialize(compPayload)+'"'+oCase.Id+'"';
        test.startTest();
        String sResult = HUMMentorToSFDCController.attachToCase(sPayload,oCase.id);
        test.stopTest();
        System.assertEquals(System.Label.HUMCanvasAlertInserted, sResult);
    }

    /*
     * Method Name : testAttachToCaseDuplicate
     * Description : Test method to insert duplicate article for a case 
     * Return type : void
     * Paramater   : NA
     */
	@isTest
    static void testAttachToCaseDuplicate()
    {
        Case oCase = HUMTestDataHelperTest.getCase();  
        oCase.Status = 'New';  
        insert oCase;

        //Test for duplicate article for a case which is not closed
        HUMMentorToSFDCHelper compPayload = new HUMMentorToSFDCHelper('test', '09000929806ec765', 'www.salesforce.com' );    
        String sPayload = JSON.serialize(compPayload)+'"'+oCase.Id+'"';
        HUMMentorToSFDCController.attachToCase(sPayload,oCase.id);
        HUMMentorToSFDCHelper compPayloaddupe = new HUMMentorToSFDCHelper('test', '09000929806ec765', 'www.salesforce.com' );    
        String sPayloadDupe = JSON.serialize(compPayloaddupe)+'"'+oCase.Id+'"';
        test.startTest();       		
		String sResultString = HUMMentorToSFDCController.attachToCase(sPayloadDupe,oCase.id);  
        test.stopTest();    
        System.assertEquals(System.Label.HUMCanvasAlertDuplicate, sResultString);        
    }

    /*
     * Method Name : testAttachToCaseNoCase
     * Description : Test method to insert empty payload 
     * Return type : void
     * Paramater   : NA
     */
	@isTest
    static void testAttachToCaseNoCase()
    {
        //Test for empty payload 
        HUMMentorToSFDCHelper compPayloadNoCase = new HUMMentorToSFDCHelper('','','');
        String sPayloadNoCase = JSON.serialize(compPayloadNoCase);
        test.startTest();       
        String sResultStringNew = HUMMentorToSFDCController.attachToCase(sPayloadNoCase,UserInfo.getUserId());
        test.stopTest();        
        System.assertEquals(System.Label.HUMCanvasAlertInsertError, sResultStringNew);
    }

    /*
     * Method Name : testAttachtoClosedCase
     * Description : Test method to insert article for a closed case
     * Return type : void
     * Paramater   : NA
     */
	@isTest
    static void testAttachtoClosedCase()
    {
        Case oCase = HUMTestDataHelperTest.getCase();
        oCase.Status = 'Closed';
        insert oCase;

        //Test for article insertion for closed case
        HUMMentorToSFDCHelper compPayloadclosed = new HUMMentorToSFDCHelper('test article', '09000929806ec766', 'www.salesforce.com' );    
        String sPayload = JSON.serialize(compPayloadclosed)+'"'+oCase.Id+'"';
        test.startTest();       
        String sResult = HUMMentorToSFDCController.attachToCase(sPayload,oCase.id);
        test.stopTest();
        System.assertEquals(System.Label.HUMCanvasAlertClosedCase, sResult);
    }
	/*
     * Method Name : testAttachToQSCompass
     * Description : Test method to insert Articles to Temporary QS Attach Object
     * Return type : void
     * Paramater   : NA
     */
    @isTest
    static void testAttachToQSCompass()
    {
    	
    	User oCCSUser = [SELECT Id, Name, UserName, Current_Queue__c 
                         FROM User 
                         WHERE LastName = 'test QuickStartSpecialist'];
    	system.assertEquals(oCCSUser.Current_Queue__c,'Test Business Process and Testing');
    	system.runAs(oCCSUser)    		
    	{
	   		List<Compass_Staging__c> lstQSMentorLinks = new List<Compass_Staging__c>();
	      	lstQSMentorLinks.add(new Compass_Staging__c(name=String.valueOf(Datetime.now().getTime())+UserInfo.getUserId(),Document_ID__c='testId',Document_Name__c='testName',Document_URL__c='https://www.salesforce.com'));       	
	        insert lstQSMentorLinks;
	        system.assert(lstQSMentorLinks[0].name.length()>18);
	   		
	        PageReference pageRef = Page.HUMMentorToSFDC;
	        Test.setCurrentPage(pageRef);       
	        ApexPages.CurrentPage().getParameters().put('caseid',lstQSMentorLinks[0].name);
	        ApexPages.CurrentPage().getParameters().put('casename','');
	        ApexPages.CurrentPage().getParameters().put('classification','Transportation');
	        ApexPages.CurrentPage().getParameters().put('intent','Cancel');
	        ApexPages.CurrentPage().getParameters().put('function','Contact Center RSO');
	        ApexPages.CurrentPage().getParameters().put('subfunction','Go365');
	        ApexPages.CurrentPage().getParameters().put('pgname','QuickStart_VF_AHT_HUM');
	        Compass__c objCompass = HUMTestDataHelperTest.getCompassRecord();
	        ApexPages.StandardController stdCntrl = new ApexPages.StandardController(objCompass);
	        HUMMentorToSFDCController objMentorClass = new HUMMentorToSFDCController(stdCntrl);
	
	        //Test for a article for a case which is not closed
	        HUMMentorToSFDCHelper compPayload = new HUMMentorToSFDCHelper('test', '09000929806ec765', 'www.salesforce.com' );    
	        String sPayload = JSON.serialize(compPayload)+'"'+lstQSMentorLinks[0].name+'"';
	        test.startTest();
	        String sResult = HUMMentorToSFDCController.attachToCase(sPayload,'1001234567890127898655');
	        System.assertEquals(System.Label.HUMCanvasAlertInserted, sResult);
	        sResult = HUMMentorToSFDCController.attachToCase(sPayload,'1001234567890127898655');
	        System.assertEquals(sResult,System.Label.HUMCanvasAlertDuplicate);
	        test.stopTest();
    	}
    }
}