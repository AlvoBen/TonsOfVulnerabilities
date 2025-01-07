/*******************************************************************************************************************************
Apex Class Name : SplunkFailureMailService_T_HUM.cls
Version         : 1.0
Created On      : 05/13/2019
Function        : Test Email Service class implemented to fetch the SiteScope monitor details from the Email and load those in to CustomerHeartbeatMetric__c Object.
Modification Log:
* Developer Name            Code Review                Date                       Description
*-------------------------------------------------------------------------------------------------------------------------------
* Praveen Kumar Parimi                                06/24/2019                 Original Version.
*********************************************************************************************************************************/
@IsTest
public class SplunkFailureMailService_T_HUM 
{    
    private static testMethod void testServiceSuccessHtml() 
    {
        String emailBody = '<b>alert-monitor: SFDC_TEST\r\nalert-id: 1234</b>\r\n';
        
        // Make mock email, only need the body field.
        Messaging.InboundEmail mockEmail = new Messaging.InboundEmail();
        mockEmail.htmlBody = emailBody;
        
        Test.startTest();        
                
        // Invoke service.
        SplunkFailureMailService_E_HUM svc = new SplunkFailureMailService_E_HUM();
        Messaging.InboundEmailResult result = svc.handleInboundEmail(mockEmail, null /* unused */);
        
        // Result should be null.
        System.assert(result != null, 'Should have result.');
        System.assertequals(result.message,'Failed to find alert-monitor and/or alert-id in email.');
        System.assert(result.success == false, 'Should be successful.');
        
        Test.stopTest();
    }
    
    private static testMethod void testServiceSuccessplaintext() 
    {
        String emailBody = 'Monitor:  1951041654:SFDC_PROD Tags:Group: CRMService Status:   SFDC_PROD_02262018/step1- Login/Status=1 Sample #: 42006 Time:     4:40 PM 4/14/19';        
        
        // Make mock email, only need the body field.
        Messaging.InboundEmail mockEmail = new Messaging.InboundEmail();
        Messaging.InboundEnvelope envelope = new Messaging.InboundEnvelope();
        mockEmail.plainTextBody = emailBody;
        
        Test.startTest();    
        
        // Invoke service.
        SplunkFailureMailService_E_HUM svc = new SplunkFailureMailService_E_HUM();        
        svc.alertMonitor = '1951041654:SFDC_PROD';
        Messaging.InboundEmailResult result = svc.handleInboundEmail(mockEmail, envelope);
        
        // Result should be null.
        System.assert(result != null, 'Should have result.');
        System.assertequals(result.message, 'Published failure result for metric: 1951041654:SFDC_PROD');
        System.assert(result.success == true, 'Should be successful.');
        
        Test.stopTest();
    }    
  
    private static testMethod void testServiceSuccessnobody() {
        String emailBody = null;
        
        // Make mock email, only need the body field.
        Messaging.InboundEmail mockEmail = new Messaging.InboundEmail();
        Messaging.InboundEnvelope envelope = new Messaging.InboundEnvelope();
        mockEmail.plainTextBody = emailBody;
        
        Test.startTest();   
        
        // Invoke service.
        SplunkFailureMailService_E_HUM svc = new SplunkFailureMailService_E_HUM();
        Messaging.InboundEmailResult result = svc.handleInboundEmail(mockEmail, envelope);
        
        // Result should be null.
        System.assert(result != null, 'Should have result.');
        System.assertequals(result.message, 'Email did not have a body.');
        System.assert(result.success == false, 'Should be successful.');
        
        Test.stopTest();
    }
    
}