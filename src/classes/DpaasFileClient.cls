public with sharing class DpaasFileClient {
     public List<FileOperation> fileOperationsOutputList {get;set;}
     public List<FileOperation> downloadedOperationsOutputList {get;set;}
     public transient String result{get;set;}
     public RetrieveSid retrieveSid {get;set;}
     public DpaaSSoapWebService.DataProtectionWebServiceImplPort dpaasSoapWebService{get;set;}
     public Boolean processZip {get;set;}
     public Boolean testCall {get;set;}
     public String error{get; set;}          
     public String appName {get;set;}
     
    Integer count = 0;
    
    public DpaasFileClient(){
        testCall = false;
        fileOperationsOutputList = new List<FileOperation>();
        downloadedOperationsOutputList = new List<FileOperation>();
        retrieveSid = new RetrieveSid();
        dpaasSoapWebService = new DpaaSSoapWebService.DataProtectionWebServiceImplPort();
        dpaasSoapWebService.timeout_x = 120000;
        processZip = false;
    }
    
    public void getDecryptedFiles(){
        try{
                this.downloadedOperationsOutputList.clear();
                Boolean processedFiles = false;
                result = '';
                Map<String,Object> resultSets = new Map<String,Object>(); 
                Map<String,Object> results = new Map<String,Object>();
                List<String> keys = new List<String>();
                Integer i = 0;
                Map<String,SObject> docIdVsSobjectMap = new Map<String,SObject>();
                Map<String,String> docIdVsDecryptedContentMap = new Map<String,String>();
                
                
                          
                for(FileOperation fo : fileOperationsOutputList ){
                    if(fo != null && fo.docId != null){
                        this.downloadedOperationsOutputList.add(fo);
                           
                       List<SObject> docSObject = executeQuery(fo);
                       if(docSObject != null && docSObject.size() > 0){
                           docIdVsSobjectMap.put(fo.docId,docSObject.get(0));
                       } 
                       else{
                       fo.status='File Not Found';
                       }
                    }
                
                }
                
               
                
                if(docIdVsSobjectMap != null && docIdVsSobjectMap.size() > 0){
                    docIdVsDecryptedContentMap = getDecryptSobjectContent(docIdVsSobjectMap);
                }
               
                if(docIdVsDecryptedContentMap != null && docIdVsDecryptedContentMap.size() > 0){
                for(FileOperation fo : fileOperationsOutputList ){
                   if(fo != null){
                       SObject sObj = docIdVsSobjectMap.get(fo.docId); 
                       if(sObj != null){
                           Map<String,Object> docResults = new Map<String,Object>();
                           //FileOperation processedFileOperation = processFileOperation(fo);
                           FileOperation processedFileOperation = getFileOperation(sObj);
                           String docId = processedFileOperation.docId;
                           fo.docId = docId ;
                           if(docId.length() > 15){
                               docId = docId.substring(0,15);
                           }
                           fo.name = processedFileOperation.name;
                           fo.folderid = processedFileOperation.folderid;
                           fo.type = processedFileOperation.type;
                           fo.status = 'File Found';
                           
                          
                           
                           docResults.put('docId',fo.docId );
                           //docResults.put('fileName',fo.name + '.'  + fo.type );
                           
                            if((fo.name).contains('.'))
                            {
                                docResults.put('fileName',fo.name );
                            }else
                            {
                                docResults.put('fileName',fo.name + '.' + fo.type );
                            }

                           docResults.put('docBody',docIdVsDecryptedContentMap.get(docId));
                           System.debug('Doc ID: '+docId);
                           System.debug('Decrypted............' + docIdVsDecryptedContentMap);
                           processedFiles = true;
                           results.put(fo.docId + ':docResult',docResults);
                           keys.add(fo.docId + ':docResult');   
                       }
                   }            
                }
                }
                
                if(processedFiles){
                    processZip = true;
                }
                resultSets.put('downloadedDocs',results);
                resultSets.put('keys',keys);                  
                result = JSON.serialize(resultSets);    
                
                this.fileOperationsOutputList.clear();
                FileOperation fileOperation = new FileOperation();
                this.fileOperationsOutputList.add(fileOperation);
                
                
                   
            }catch(Exception e){
                //  error = ' Exceptoin while making webservice calls : ' + e;
                       
                System.debug('Exceptoin while making webservice calls : ' + e);
                Map<String,Object> resultSets = new Map<String,Object>(); 
                resultSets.put('exceptionOccured',e.getMessage()); 
                error = e.getMessage();
                result = JSON.serialize(resultSets);
            }
    }                 
    
    public FileOperation processFileOperation(FileOperation argFileOperation){
        FileOperation fileOperation = new FileOperation();
        if(argFileOperation != null){
            fileOperation.body = EncodingUtil.base64Encode(Blob.valueOf(argFileOperation.docId));
        }
        
        return fileOperation;
    } 
    
     public FileOperation getFileOperation(SObject argSobject){
        if(argSobject != null){
            FileOperation fileOperation = new FileOperation();
            String docId = String.valueOf(argSobject.get('Id'));
            fileOperation.docId = docId;
            
            String folderId = String.valueOf(argSobject.get('FolderId'));
            fileOperation.folderId = folderId;
            
            //Blob bodyBlob = (Blob)argSobject.get('Body');
            //String body = EncodingUtil.base64Encode(bodyBlob);
            //fileOperation.body = body;
            
            String contentType = String.valueOf(argSobject.get('ContentType'));
            fileOperation.contentType = contentType;
            
            String type = String.valueOf(argSobject.get('Type'));
            fileOperation.type = type;
             
            String name = String.valueOf(argSobject.get('Name'));
            fileOperation.name = name;
            
            fileOperation.apiSessionId = retrieveSid.sessionId;
            fileOperation.apiServerURL = retrieveSid.serverUrl;
           
            
            return fileOperation;
        }
        return null;
    }
    
     
    public List<SObject> executeQuery(FileOperation argFileOperation){
        try{
        System.debug('Entered into execute query method...');
        if(argFileOperation != null && argFileOperation.docId !=null && argFileOperation.docId != ''){
            String query = 'Select Id, FolderId, Name, ContentType, Type, Body FROM Document WHERE Id = \'' + argFileOperation.docId +'\'' ;
            System.debug('Query: ' + query);
            argFileOperation.processedFile = true;
            return Database.query(query);
        }
        }catch(Exception e){
            argFileOperation.status = 'File Not Found';
            argFileOperation.processedFile = false;
        }
        return null;
    }
    
     private Map<String,String> getDecryptSobjectContent(Map<String,SObject> argDocIdVsSobjectMap){
         Map<String,String> docIdVsDecryptedContentMap = new Map<String,String>();
         DpaaSSoapWebService.FileOptions fileOptions = null;
       
         if(argDocIdVsSobjectMap != null && argDocIdVsSobjectMap.size() > 0){
            fileOptions = new DpaaSSoapWebService.FileOptions();    
            fileOptions.base64Content = new List<String>();            
            List<String> base64Tokens = new List<String>();
                   
            for (String docId : argDocIdVsSobjectMap.keySet()){
                SObject sobj = argDocIdVsSobjectMap.get(docId);
                Blob bodyBlob = (Blob)sobj.get('Body');
                String body = EncodingUtil.base64Encode(bodyBlob);           
                base64Tokens.add(body);            
            }
          
            fileOptions.base64Content.addAll(base64Tokens);
            List<String> decryptedTokens = decryptContent(fileOptions);
            fileOptions.base64Content = null;
         
            if(decryptedTokens != null && decryptedTokens.size() > 0 && argDocIdVsSobjectMap.size() == decryptedTokens.size()){
          
                Integer i = 0;
                for (String docId : argDocIdVsSobjectMap.keySet()){
                //System.debug('Decrypted Token: '+ decryptedTokens.get(i));
                    docIdVsDecryptedContentMap.put(docId,decryptedTokens.get(i));
                    i++;
                }
            }            
        }
          
        fileOptions = null;
        return docIdVsDecryptedContentMap;
    } 
     
    public List<String> decryptContent(DpaaSSoapWebService.FileOptions argFileOptions){
        List<String> decryptedTokens = null;                
        if(argFileOptions != null && !testCall){
      
        String applicationName=appName;
        argFileOptions.operation='DECRYPT';
           
            DpaaSSoapWebService.FileResponse responseOptionFile = dpaasSoapWebService.handleFile( retrieveSid.sessionId,retrieveSid.serverUrl,argFileOptions,applicationName);
            DpaaSSoapWebService.FileOptions responseOption =responseOptionFile.fileOptions;
           
            if(responseOption != null){
                decryptedTokens = new List<String>(); 
                for(String decryptedBase64Token : responseOption.base64Content){
                    System.debug('decryptedBase64Token :'+ decryptedBase64Token);
                    decryptedTokens.add(decryptedBase64Token);
                }
                return decryptedTokens;
            }
        } else if(testCall){
            decryptedTokens = new List<String>(); 
            decryptedTokens.add('Test Doc');              
        }
        return decryptedTokens; 
    }                  
                       
     public PageReference loadDocBlocks() {
        FileOperation fileOperation = new FileOperation();
        this.fileOperationsOutputList.add(fileOperation);
        return null;
    }
    
     public void removeDocBlocks() {
        if(this.fileOperationsOutputList.size() > 1){
            this.fileOperationsOutputList.remove(this.fileOperationsOutputList.size() - 1);
        }else {
            this.fileOperationsOutputList.remove(this.fileOperationsOutputList.size() - 1);
            FileOperation fileOperation = new FileOperation();
            this.fileOperationsOutputList.add(fileOperation);        
        }
    }
      
}