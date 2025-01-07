@isTest
public with sharing class DpaasWebServiceClientTest {

@isTest static void testDpaasWebServiceClient(){
        DpaasWebServiceClient dpaasWebServiceClient = new DpaasWebServiceClient();
        dpaasWebServiceClient.loadBlocks();
        dpaasWebServiceClient.removeBlocks();
        
        dpaasWebServiceClient.createCS();
        dpaasWebServiceClient.doLogin();
        dpaasWebServiceClient.finish();
        dpaasWebServiceClient.cancel();
        
        dpaasWebServiceClient.tokenizationStep2();
          
        List<DpaasWebServiceClient.DpaasFieldVo> dpaasFieldVoList = dpaasWebServiceClient.dpaasFieldVoList;
        if(dpaasFieldVoList == null){
            dpaasFieldVoList  = new List<DpaasWebServiceClient.DpaasFieldVo>();
        }
        DpaasWebServiceClient.DpaasFieldVo dpaasFieldVo = new dpaasWebServiceClient.DpaasFieldVo('SFDC', 'Account', 'Name', 'Cipher');
        dpaasFieldVo.cipherTextValue = 'EncryptedText';
        dpaasFieldVoList.add(dpaasFieldVo);
        
        
        dpaasWebServiceClient.sid = UserInfo.getSessionId();
        dpaasWebServiceClient.sURL = 'https://login.salesforce.com';
        
        dpaasWebServiceClient.tokenizationStep2();
        dpaasWebServiceClient.detokenizationPage2();
        dpaasWebServiceClient.detokenizationCancel();
        
        dpaasWebServiceClient.detokenizationFinish();
        dpaasWebServiceClient.DpaasFieldVo dpaasFieldVo1 = new dpaasWebServiceClient.DpaasFieldVo('SFDC', 'Account', 'Name', 'Cipher');
        System.assertEquals(dpaasFieldVo1.applicationName , 'SFDC');
}

 static testMethod void testDpaaSSoapWebServiceV1(){
        DpaaSSoapWebServiceV1.ErrorMessage errorMessage = new DpaaSSoapWebServiceV1.ErrorMessage();
        errorMessage.errorCode = 'ERR_DPAAS_001';
        errorMessage.message = 'errorMessage test';
        
        List<DpaaSSoapWebServiceV1.ErrorMessage> errorMessageList = new List<DpaaSSoapWebServiceV1.ErrorMessage>();
        errorMessageList.add(errorMessage);
        DpaaSSoapWebServiceV1.ErrorMessages errorMessages = new DpaaSSoapWebServiceV1.ErrorMessages();
        errorMessages.errorMessage = errorMessageList;
        
        DpaaSSoapWebServiceV1.Address address = new DpaaSSoapWebServiceV1.Address();
        address.street = 'Krishna Nagar';
        address.city = 'Hyd';
        address.state = 'AP';
        address.country = 'INDIA';
        address.postalCode = '522426';
        
        DpaaSSoapWebServiceV1.ContactInfo contactInfo = new DpaaSSoapWebServiceV1.ContactInfo();
        contactInfo.phone = '9875287645873';
        contactInfo.mobile = '875984375932874';
        contactInfo.fax = '54798357493875';
        contactInfo.email = 'abc@gmail.com';
        contactInfo.website = 'login.salesforce.com';
        
        DpaaSSoapWebServiceV1.Person person = new DpaaSSoapWebServiceV1.Person();
        person.title = 'Mr.';
        person.firstName = 'Mr.';
        person.middleName = 'Mr.';
        person.lastName = 'Mr.';
        person.suffix = 'Mr.';
        
        DpaaSSoapWebServiceV1.Organization org = new DpaaSSoapWebServiceV1.Organization();
        org.name = 'CipherCloud';
        org.address = address;
        org.primaryContact = person;
        
        DpaaSSoapWebServiceV1.BasicAuthentication basic = new DpaaSSoapWebServiceV1.BasicAuthentication();
        basic.login = 'amar@freshstart.in';
        basic.password = 'ciphercloud@123';
        
        DpaaSSoapWebServiceV1.OpenIdAuthentication open = new DpaaSSoapWebServiceV1.OpenIdAuthentication();
        open.forFutureUse = 'Hello';
        
        List<String> ipList = new List<String>();
        ipList.add('198');
        DpaaSSoapWebServiceV1.IpAddressAuthentication ip = new DpaaSSoapWebServiceV1.IpAddressAuthentication();
        ip.ipAddress = ipList;
        
        DpaaSSoapWebServiceV1.SalesforceAuthentication sfdc = new DpaaSSoapWebServiceV1.SalesforceAuthentication();
        sfdc.sessionId = '134567';
        sfdc.serverUrl = 'https://login.salesforce.com';
        sfdc.orgId = '2345678';
        
        DpaaSSoapWebServiceV1.Credentials cred = new DpaaSSoapWebServiceV1.Credentials();
        cred.basicAuthentication = basic;
        cred.openIdAuthentication = open;
        cred.ipAddressAuthentication = ip;
        cred.salesforceAuthentication = sfdc;
        
        DpaaSSoapWebServiceV1.Parameter param = new DpaaSSoapWebServiceV1.Parameter();
        param.name = 'Cipher';
        param.value = 'Cloud';
        
        List<DpaaSSoapWebServiceV1.Parameter> paramList = new List<DpaaSSoapWebServiceV1.Parameter>();
        paramList.add(param);
        
        DpaaSSoapWebServiceV1.Factory fact = new DpaaSSoapWebServiceV1.Factory();
        fact.name = 'Amar';
        fact.className = 'Cipher';
        fact.parameter = paramList;
        
       
        
        DpaaSSoapWebServiceV1.BaseResponse baseResponse = new DpaaSSoapWebServiceV1.BaseResponse();
        baseResponse.errorMessage = errorMessageList;
        
        DpaaSSoapWebServiceV1.BaseRequest baseRequest = new DpaaSSoapWebServiceV1.BaseRequest();
        // Newly add line         
        //System.assertEquals('ERR_DPAAS_001', ''+baseResponse.errorMessage[0]); 
    }

}