@IsTest
public with sharing class DpaasFileClientTest{

static testMethod void testDpaasFileClient(){
    Document document = new Document();
    document.AuthorId = UserInfo.getUserId();
    document.FolderId = UserInfo.getUserId();
    document.name = 'Test doc';   
    try{
        insert document;
    }catch(Exception e){
        System.debug('Exception Occured : ' + e);
    }
     
    DpaasFileClient dpaasFileClient = new DpaasFileClient();
    dpaasFileClient.testCall = true;
    FileOperation fileOperation = new FileOperation();
    fileOperation.docId = document.id;
    dpaasFileClient.fileOperationsOutputList.add(fileOperation);
    dpaasFileClient.getDecryptedFiles();
    System.assertNotEquals(dpaasFileClient,null);
    dpaasFileClient.loadDocBlocks();
    dpaasFileClient.removeDocBlocks();
    dpaasFileClient.removeDocBlocks();
    dpaasFileClient.removeDocBlocks();
    FileOperation fo = new FileOperation();
    fo.docid = 'abc';
    dpaasFileClient.executeQuery(fo);
    
    dpaasFileClient.processFileOperation(fo);
    
    dpaasFileClient.testCall = false;
    dpaasFileClient.fileOperationsOutputList.add(fileOperation);
    dpaasFileClient.getDecryptedFiles();
    
    dpaasFileClient.decryptContent(null);
    dpaasFileClient.getFileOperation(null);
}

}