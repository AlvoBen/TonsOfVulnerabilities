/*****************************************************************************************
Apex class Name    : Pharmacy_HPIE_LT_HUM
Version            : 1.0
Function           : Test class for Pharmacy_HPIE_LC_HUM.
Created Date       : 02/26/2024
Modification Log: 
Developer Name           Code Review                       Date                       Description 
*--------------------------------------------------------------------------------------------------------------------------------
* Jonathan Dickinson                     				02/26/2022                  initial version
****************************************************************************************************************/

@isTest
public class Pharmacy_HPIE_LT_HUM { 
    @testSetup 
    static void setUpTestData(){
        User objUser = HUMTestDataHelperTest.createTestUser('User1','Customer Care Specialist');
        insert objUser;
    }

    static testMethod void testGetFinanceDetails(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getFinanceDetails('1012829915', 'nxg6699', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }
    static testMethod void testGetDemographicsDetails(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getDemographicsDetails('1012829915', 'nxg6699', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }
    static testMethod void testGetProfileDetails(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getProfileDetails('1012829915', 'nxg6699', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }
    static testMethod void testGetPlanDetails(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getPlanDetails('1012829915', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }
    static testMethod void testGetPreferences(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getPreferences('1012829915', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }
    static testMethod void testGetPrescriptions(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
			Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getPrescriptions('1012829915', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30', '2022-08-16', '2024-02-15'), null);
            Test.stopTest();
        }
    }    

    static testMethod void testUpdatePrescriptions(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
			Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updatePrescription('447522033', '1012829915', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30', true, true), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdateCapType(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateCapType('1012829915', 'SSA6903', '2021-10-22T13:21:39.035+05:30', 'HUMANA', 'E'), null);
            Test.stopTest();
        }
    }  

    static testMethod void testGetHealthHistory(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getHealthHistoryDetails('1012829915', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }

    static testMethod void testGetOrderSummary(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getOrderSummary('1012829915', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30', '2023-11-16', '2024-02-14'), null);
            Test.stopTest();
        }
    }

    static testMethod void testAddFills(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.createFill('{"sourceApplication":"CRM","userId":"JMD7035","requestTime":"2024-02-26T19:51:53.926Z","organizationPatientId":"1067742709","organization":"HUMANA","Fills":[{"prescriptionKey":"433453869"}]}'), null);
            Test.stopTest();
        }
    }

    static testMethod void testGetOrderDetail(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getOrderDetails('1019838720', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30', '495862782'), null);
            Test.stopTest();
        }
    }

    static testMethod void testGetEvents(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getEvents('1019838720', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30', '495862782'), null);
            Test.stopTest();
        }
    }

    static testMethod void testGetTasks(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getTasks('1019838720', 'JMD7035', 'HUMANA', '2021-10-22T13:21:39.035+05:30'), null);
            Test.stopTest();
        }
    }

    static testMethod void testGetPatientLogNotes(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getPatientLognotes('1019838720', 'JMD7035', 'HUMANA', '2023-07-21', '2024-02-19'), null);
            Test.stopTest();
        }
    }

    static testMethod void testAddPatientLogNotes(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.addLogNote('1019838720', 'JMD7035', 'HUMANA', 'CCALL', 'TEST NOTE'), null);
            Test.stopTest();
        }
    }
    
    static testMethod void testGetFamilyLogNotes(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.getFamilyLognotes('1019838720', 'JMD7035', 'HUMANA', '2023-07-20', '2024-02-19', '11126354'), null);
            Test.stopTest();
        }
    }

    static testMethod void testAddFamilyLogNotes(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.addFamilyLogNote('{ "UserId": "SSA6903", "organizationPatientId": "1019838720", "organization": "HUMANA", "accountId": "11126354", "notes": [ { "noteCode": "CCALL", "logNote": "Note"} ] }'), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdatePhone(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updatePrimaryPhone('1019838720', 'JMD7035', '2021-10-22T13:21:39.035+05:30', 'HUMANA', '5025801222'), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdateAlternatePhone(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateAlternatePhone('1019838720', 'JMD7035', '2021-10-22T13:21:39.035+05:30', 'HUMANA', '5025801222'), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdateEmail(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateEmail('1019838720', 'JMD7035', '2021-10-22T13:21:39.035+05:30', 'HUMANA', 'mark_test3@gmail.com'), null);
            Test.stopTest();
        }
    }

    static testMethod void testAddFinance(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            Pharmacy_HPIE_LC_HUM.AddFinanceRequest req = new Pharmacy_HPIE_LC_HUM.AddFinanceRequest();
            req.userId = 'AAA0000';
            req.requestTime = '2023-09-11T11:20:48.247Z';
            req.organization = 'HUMANA';
            req.organizationPatientId = '1019838720';
            req.cardKey = -1;
            req.cardTypeCode = '1';
            req.cardTypeDesc = 'VISA';
            req.expMonth = 3;
            req.expYear = 2025;
            req.firstName = 'test';
            req.middleName = '';
            req.lastName = 'test';
            req.active = true;
            req.autoCharge = false;
            req.spendingAccount = false;
            req.tokenKey = '9928796248339755';
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.addFinance(req), null);
            Test.stopTest();
        }
    }
    static testMethod void testUpdateFinance(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            Pharmacy_HPIE_LC_HUM.UpdateFinanceRequest req = new Pharmacy_HPIE_LC_HUM.UpdateFinanceRequest();
            req.userId = 'AAA0000';
            req.requestTime = '2023-08-25T16:45:51.894Z';
            req.organizationPatientId = '1019838720';
            req.organization = 'HUMANA';
            req.cardKey = 11977879;
            req.cardTypeCode = '2';
            req.cardTypeDesc = 'MASTER CARD';
            req.expMonth = 1;
            req.expYear = 28;
            req.firstName = 'TASHA';
            req.lastName = 'CAFOUREK';
            req.active = true;
            req.autoCharge = false;
            req.spendingAccount = false;
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateFinance(req), null);
            Test.stopTest();
        }
    }

    static testMethod void testOneTimePayment(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            Pharmacy_HPIE_LC_HUM.AddPaymentRequest req = new Pharmacy_HPIE_LC_HUM.AddPaymentRequest();
            req.userId = 'AAA0000';
            req.requestTime = '2023-08-25T16:45:51.894Z';
            req.organizationPatientId = '1019838720';
            req.organization = 'HUMANA';
            req.key = 11977879;
            req.amount = 20;
            req.z0date = '2023-12-02';
            req.authorized = true;
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.addOneTimePayment(req), null);
            Test.stopTest();
        }
    }

    static testMethod void testAddAddress(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.addNewAddress('{"organizationPatientId":"1091252368","organization":"HUMANA","requestTime":"2024-02-26T22:08:26.855Z","userId":"JMD7035","code":"11","description":"SHIPPING","addressLine1":"500 W MAIN ST","addressLine2":"","city":"LOUISVILLE","stateCode":"KY","zipCode":"40202","overrideReason":"MANUAL ADDRESS - ASSOCIATE USPS/UPS VALIDATED","overrideReasonCode":1,"uspsValidated":true,"active":true,"key":-1}'), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdateAddress(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateAddress('{"organizationPatientId":"1091252368","organization":"HUMANA","requestTime":"2024-02-26T22:29:13.826Z","userId":"JMD7035","addresses":[{"code":"11","description":"SHIPPING","addressLine1":"66000 TRAVIS RUN","addressLine2":"","city":"ZEPHYRHILLS","stateCode":"FL","zipCode":"33542","overrideReason":"MANUAL ADDRESS - ASSOCIATE USPS/UPS VALIDATED","overrideReasonCode":1,"uspsValidated":false,"active":true,"key":37758009}]}'), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdateMemberConsent(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateMemberConsent('1091252368', 'JMD7035', '2024-02-26T22:29:13.826Z', 'HUMANA', '2024-01-19', true), null);
            Test.stopTest();
        }
    }

    static testMethod void testUpdateOrder(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.updateOrder('{ "sourceApplication": "CRM", "userId": "JMD7035", "requestTime": "2024-02-26T22:37:32.730Z", "organizationPatientId": "1091252368", "organization": "HUMANA", "order": { "orderId": "609101042", "pharmacyNPI": "", "cancelOrder": false, "cancelReasonCode": 0, "releaseDate": "", "billing": { "address": { "key": 13215922 }, "payment": { "method": { "code": 2, "description": "PAYMENT CARD" }, "date": "2024-02-26", "card": { "key": 9976415 } } }, "shipping": { "method": { "code": 1 }, "priority": true, "address": { "key": 37758009 } }, "lines": [ {"copay":{"consent": true}, "memberConsent": true, "product": { "prescription": { "prescriptionKey": "480532883" } } }, {"copay":{"consent": true}, "memberConsent": true, "product": { "prescription": { "prescriptionKey": "480532884" } } } ] } }'), null);
            Test.stopTest();
        }
    }

    static testMethod void testReleaseOrder(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.releaseOrder('{"sourceApplication":"CRM","organization":"HUMANA","userId":"JMD7035","requestTime":"2024-02-26T23:16:18.810Z","organizationPatientId":"1019838720","order":{"orderId":"609101042","billing":{"payment":{"card":{"key":9976415}}},"lines":[{"copay":{"consent":true},"product":{"prescription":{"prescriptionKey":"480532883"}}},{"copay":{"consent":true},"product":{"prescription":{"prescriptionKey":"480532884"}}}]}}', true), null);
            Test.stopTest();
        }
    }

    static testMethod void testCreateOrder(){
        User testUser = [Select Id,Name from User where LastName='test User1'];
        System.runAs(testUser)
        {
            Test.startTest();
            Test.setMock(HttpCalloutMock.class, new Pharmacy_HPIEMockTest_LT_HUM());
            System.assertNotEquals(Pharmacy_HPIE_LC_HUM.createOrder('{"sourceApplication": "CRM", "userId": "ssa6903", "requestTime": "2023-08-24T09:54:18.228Z", "organizationPatientId": "1029718080", "organization": "HUMANA", "pharmacyNPI": "1710927462", "order": { "type": { "code":29 }, "billing": { "address": { "key": 16475195 }, "payment": { "method": { "code": 2, "description": "CASH" }, "date": "2023-08-23", "card": { "key": 8723871, "amount": 790 } } }, "shipping": { "method": { "code": 1 }, "priority": true, "address": { "key": 16475195 } }, "lines": [ { "product": { "prescription": { "prescriptionKey": "466720928", "fillNumber":1 } } } ] } }'),null);
            Test.stopTest();
        }
    }

}