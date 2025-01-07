@isTest
public with sharing class DpaaSSoapWebServiceTest {
    @isTest static void testDetokenize(){

        // This causes a fake response to be generated
        Test.setMock(WebServiceMock.class, new DpaaSWebServiceMockImpl());
        
        // Call the method that invokes a callout
        String sessionId = UserInfo.getSessionId();
        String serverUrl = 'server url';
         String applicationName= 'sfdc';
        String[] cipherText = new List<String> {'Cipher text'};
        DpaaSSoapWebService service = new DpaaSSoapWebService();
        DpaaSSoapWebService.DetokenizationResult[] output = new DpaaSSoapWebService.DataProtectionWebServiceImplPort().detokenize(sessionId, serverUrl,applicationName, cipherText);
        
        // Verify that a fake result is returned
        System.assertEquals('Clear Text', output[0].clearTextValue); 
    }
    
    @isTest static void testTokenize(){

        // This causes a fake response to be generated
        Test.setMock(WebServiceMock.class, new DpaaSWebServiceMockImpl());
        
        // Call the method that invokes a callout
        String sessionId = UserInfo.getSessionId();
        String serverUrl = 'server url';
        
        DpaaSSoapWebService.Field[] fields = new List<DpaaSSoapWebService.Field>();
        DpaaSSoapWebService service = new DpaaSSoapWebService();
        DpaaSSoapWebService.TokenizeFault tokenizeFault = new DpaaSSoapWebService.TokenizeFault();
        System.AssertNotEquals(tokenizeFault, null);
        DpaaSSoapWebService.LoginFault loginFault = new DpaaSSoapWebService.LoginFault();
        System.AssertNotEquals(loginFault , null);
        DpaaSSoapWebService.Fault fault = new DpaaSSoapWebService.Fault();
        System.AssertNotEquals(fault , null);
        DpaaSSoapWebService.DetokenizeFault detokenizeFault = new DpaaSSoapWebService.DetokenizeFault();
        System.AssertNotEquals(detokenizeFault , null);
        DpaaSSoapWebService.Field[] output = new DpaaSSoapWebService.DataProtectionWebServiceImplPort().tokenize(sessionId, serverUrl, fields);
        //String ss =  new DpaaSSoapWebService.DataProtectionWebServiceImplPort().getTokenizer('EncryptionScheme',sessionId, serverUrl);
        DpaaSSoapWebService.ChunkContext chunkContext=null;
        DpaaSSoapWebService.FileTokenizeDetokenizeResponse  output11 = new DpaaSSoapWebService.DataProtectionWebServiceImplPort().handleFileTokenizer('applicationName','inputByteArray','fileName','mimeType',chunkContext);
        String[] inputByteArray=null;
        DpaaSSoapWebService.ChunkFileResponse output112 = new DpaaSSoapWebService.DataProtectionWebServiceImplPort().handleChunkFile('applicationName',inputByteArray,chunkContext);
         
        //String output1112 = new DpaaSSoapWebService.DataProtectionWebServiceImplPort().getTokenizer('encryptionSchemeName',sessionId,serverUrl);
              
        DpaaSSoapWebService.GetApplicationDetailsResponse getApplicationDetailsResponse= new DpaaSSoapWebService.GetApplicationDetailsResponse ();
        DpaaSSoapWebService.GetEncryptedMailMessageResponse getEncryptedMailMessageResponse = new DpaaSSoapWebService.GetEncryptedMailMessageResponse ();
        DpaaSSoapWebService.GetEncryptedMailMessageRequest getEncryptedMailMessageRequest = new DpaaSSoapWebService.GetEncryptedMailMessageRequest();
        DpaaSSoapWebService.GetApplicationDetailsRequest getApplicationDetailsRequest  = new DpaaSSoapWebService.GetApplicationDetailsRequest ();
      
       DpaaSSoapWebService.GetApplicationDetailsResponse getApplicationDetailsResponse1=new DpaaSSoapWebService.DataProtectionWebServiceImplPort().getApplicationDetails('sfdc','sfdc');
       
       String getTokenizer='sss';
       //getTokenizer=new DpaaSSoapWebService.DataProtectionWebServiceImplPort().getTokenizer('sfdc','sessionId',serverUrl);
       DpaaSSoapWebService.GetEncryptedMailMessageResponse getEncryptedMailMessage=new DpaaSSoapWebService.DataProtectionWebServiceImplPort().getEncryptedMailMessage('mimeSubject','mimeBody','mimeAttachment','cloudService');
       
       DpaaSSoapWebService.GetTokenizerResponse gGetTokenizerResponse= new DpaaSSoapWebService.GetTokenizerResponse();
       
       DpaaSSoapWebService.GetTokenizerRequest gGetTokenizerRequest= new DpaaSSoapWebService.GetTokenizerRequest();
       DpaaSSoapWebService.GetTokenizerFault gGetTokenizerFault = new DpaaSSoapWebService.GetTokenizerFault ();
       DpaaSSoapWebService.GetEncryptedMailMessageFault gGetEncryptedMailMessageFaultt = new DpaaSSoapWebService.GetEncryptedMailMessageFault();
       
       DpaaSSoapWebService.FileTokenizeDetokenizeResponse getFileTokenizeDetokenizeResponse  = new DpaaSSoapWebService.FileTokenizeDetokenizeResponse();
       DpaaSSoapWebService.FileResponse getFileResponse  = new DpaaSSoapWebService.FileResponse();
       DpaaSSoapWebService.ChunkFileResponse getChunkFileResponse  = new DpaaSSoapWebService.ChunkFileResponse();
       DpaaSSoapWebService.ChunkContext getChunkContext  = new DpaaSSoapWebService.ChunkContext();
       DpaaSSoapWebService.FileTokenizeDetokenizeRequest getFileTokenizeDetokenizeRequest = new DpaaSSoapWebService.FileTokenizeDetokenizeRequest();
       DpaaSSoapWebService.FileTokenizeDetokenizeResponse getFileTokenizeDetokenizeResponse1  = new DpaaSSoapWebService.FileTokenizeDetokenizeResponse();
       DpaaSSoapWebService.HandleChunkFileFault getHandleChunkFileFault = new DpaaSSoapWebService.HandleChunkFileFault();
       DpaaSSoapWebService.ChunkFileRequest getChunkFileRequest  = new DpaaSSoapWebService.ChunkFileRequest();
       
       
       
       
        // Verify that a fake result is returned
        System.assertEquals('SFDC', output[0].applicationName); 
    }
}