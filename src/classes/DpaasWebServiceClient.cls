public with sharing class DpaasWebServiceClient{
    public List<DpaasFieldVo> dpaasFieldVoList{get;set;}
    public DpaasFieldVo dpaasFieldVo{get;set;}
    public List<DpaasFieldVo> dpaasEncryptedFieldVoList{get;set;}
    public List<DpaasFieldVo> dpaasDecryptedFieldVoList{get;set;}
    public DpaaSSoapWebService.DataProtectionWebServiceImplPort dpaasSoapWebService{get;set;}
    public DpaaSSoapWebService.DetokenizationResult[] detokenizationResults{get;set;}
    public DpaaSSoapWebService.Field[] tokenizationResult{get;set;}
    public String error{get; set;}
    public String status{get;set;}    
    public Boolean isError {get; set;}  
    public Boolean render {get;set;}
    public String resultStatus{get;set;}
    public List<String> errors{get;set;}
    public List<DpaaSSoapWebService.Field> resultedFields{get;set;}
    public List<DpaaSSoapWebService.DetokenizationResult> detokenizedTokens{get;set;}
    public String sid{get;set;}
    public String sURL{get;set;}
    public String appName {get;set;}
    
    public DpaasWebServiceClient(){
        //dpaasSoapWebService = new DpaaSSoapWebService.DataProtectionWebServiceImplPort();
        dpaasFieldVo = new DpaasFieldVo();
        dpaasFieldVoList = new List<DpaasFieldVo>();
        dpaasEncryptedFieldVoList = new List<DpaasFieldVo>();
        dpaasDecryptedFieldVoList = new List<DpaasFieldVo>();
        DpaasFieldVo dpaasFieldVo = new DpaasFieldVo();
        this.dpaasFieldVoList.add(dpaasFieldVo);
        resultedFields = new List<DpaaSSoapWebService.Field>();
        detokenizedTokens = new List<DpaaSSoapWebService.DetokenizationResult>();
        status = '';
        isError = false;
        render = true;
    }
    
    public void loadBlocks(){
        DpaasFieldVo dpaasFieldVo = new DpaasFieldVo();
        this.dpaasFieldVoList.add(dpaasFieldVo);
    }
    
    public void removeBlocks() {
        if(this.dpaasFieldVoList.size()>1){
            this.dpaasFieldVoList.remove(this.dpaasFieldVoList.size()-1);
        }
    }
    
    public PageReference tokenize(){
        if(dpaasFieldVoList != null && dpaasFieldVoList.size() > 0){
            errors = new List<String>();
            boolean foundError = false;
            Integer index = 0;
            DpaaSSoapWebService.Field field;
            String sessionId;
            String serverUrl;
            String errorCode;
            List<DpaaSSoapWebServiceV1.ErrorMessage> errMessages = new List<DpaaSSoapWebServiceV1.ErrorMessage>();
           try{
               DpaaSSoapWebService.Field[] fields = new DpaaSSoapWebService.Field[dpaasFieldVoList.size()];
               for(DpaasFieldVo dpaasFieldVo:dpaasFieldVoList){
                   field = new DpaaSSoapWebService.Field();
                   if(dpaasFieldVo != null){
                   if(dpaasFieldVoList.size() == 1){
                   if(dpaasFieldVo.applicationName == null || dpaasFieldVo.applicationName == ''){
                       error = 'Application Name should not be empty';
                       foundError = true;
                       errors.add(error);
                   } 
                   if(dpaasFieldVo.entityName == null || dpaasFieldVo.entityName == ''){
                       error = 'Entity Name should not be empty';
                       foundError = true;
                       errors.add(error);
                   } 
                   if(dpaasFieldVo.fieldName == null || dpaasFieldVo.fieldName == ''){
                       error = 'Field Name should not be empty';
                       foundError = true;
                       errors.add(error);
                   } 
                   if(dpaasFieldVo.clearTextValue == null || dpaasFieldVo.clearTextValue == ''){
                       error = 'ClearText Value should not be empty';
                       foundError = true;
                       errors.add(error);
                   }
                   } 
                   if(!foundError) {
                       //sessionId = dpaasFieldVo.apiSessionId;
                       sessionId = sid;
                       //resultStatus = 'Hello THis Is Session ID: '+sessionId;
                       //serverUrl = dpaasFieldVo.apiServerURL;
                       serverUrl = sURL;
                       field.applicationName = dpaasFieldVo.applicationName;
                       field.entityName = dpaasFieldVo.entityName;
                       field.fieldName = dpaasFieldVo.fieldName;
                       field.clearTextValue = dpaasFieldVo.clearTextValue;
                   }
                    fields[index] = field;
                    if(foundError && dpaasFieldVoList.size() == 1){
                        return Page.dpaasTokenizationStep1;
                    }
                    foundError = false;
                    ++index;
                  }
                }
               
                tokenizationResult = dpaasSoapWebService.tokenize(sessionId, serverUrl, fields);
                if(tokenizationResult != null && tokenizationResult.size() > 0){
                
                   // DpaaSSoapWebService.Field[] aa = tokenizationResult ;
                
                    for(Integer i=0;i<tokenizationResult.size();i++){
                        if(tokenizationResult[i].cipherTextValue != null)
                        tokenizationResult[i].cipherTextValue = (tokenizationResult[i].cipherTextValue).substring(9,(tokenizationResult[i].cipherTextValue.length()-3));
                         //System.debug('gogati'+(aa[i].cipherTextValue).substring(9,(aa[i].cipherTextValue.length()-3))) ;
                    }
                   // tokenizationResult = aa;
                    resultedFields.addAll(tokenizationResult);
                }
                return Page.dpaasTokenizationStep2;
              
         } catch(Exception e){
             render = false;
             isError = true;
             status += e.getMessage();
         }
      }
      return Page.dpaasTokenizationStep2;
    }
        
    public PageReference detokenize(){
       if(dpaasFieldVoList != null && dpaasFieldVoList.size() > 0){
           boolean foundError = false;
           String sessionId;
           String serverUrl;
           String applicationName ;
           Integer index = 0;
           try{ 
           String[] cipherTextValues = new String[dpaasFieldVoList.size()];
           for(DpaasFieldVo dpaasFieldVo:dpaasFieldVoList){
               if(dpaasFieldVo != null){
                   if(dpaasFieldVo.cipherTextValue == null || dpaasFieldVo.cipherTextValue == ''){
                       error = 'CipherText Value should not be an empty';
                       foundError = true;
                   } else {
                       //sessionId = dpaasFieldVo.apiSessionId;
                       //serverUrl = dpaasFieldVo.apiServerURL;
                       sessionId = sid;
                       serverUrl = sURL;
                       applicationName = appName;
                   }
                   if(foundError){
                       if(dpaasFieldVoList.size() == 1){
                           return Page.dpaasDetokenizationStep1;
                       }
                       foundError = false;
                   }
                   cipherTextValues[index] = dpaasFieldVo.cipherTextValue;
                   ++index;
               }
           }
          

         detokenizationResults = dpaasSoapWebService.detokenize(sessionId, serverUrl,applicationName, cipherTextValues);
          
           if(detokenizationResults != null && detokenizationResults.size() > 0){
               detokenizedTokens.addAll(detokenizationResults);
               return Page.dpaasDetokenizationStep2;
            }
         } catch(Exception e) {
             render = false;
             isError = true;
             status = e.getMessage();
             error = status ;
             foundError = true;
           
         }   
       }
       return Page.dpaasDetokenizationStep1;
    }
    
     public void createCS(){
       if(Endpoint_Url__c.getInstance() == null){
            upsert new Endpoint_Url__c (name='Cipher Cloud Url',Remote_Url__c='http://ciphercloud.net/ws'); 
       }
       dpaasSoapWebService = new DpaaSSoapWebService.DataProtectionWebServiceImplPort();
    }        
    
    public PageReference doLogin(){
        return null;
    }
    
    public PageReference finish(){
        PageReference finish = new PageReference('/apex/dpaasTokenizationStep1');
        finish.setRedirect(true);
        return finish;
    }
   public PageReference cancel(){
        PageReference cancel = new PageReference('/apex/dpaasTokenizationStep1');
        cancel.setRedirect(true);
        return cancel;
    }
    
    public PageReference tokenizationStep2(){
        return tokenize();
     }
     
     public PageReference detokenizationPage2(){
         return detokenize();
      }
     
     public PageReference detokenizationCancel(){
         PageReference cancel = new PageReference('/apex/dpaasDetokenizationStep1');
         cancel.setRedirect(true);
         return cancel;
     }
     
     public PageReference detokenizationFinish(){
         PageReference finish = new PageReference('/apex/dpaasDetokenizationStep1');
         finish.setRedirect(true);
         return finish;
     }
    public with sharing class DpaasFieldVo{
        public String applicationName{get;set;}
        public String entityName{get;set;}
        public String fieldName{get;set;}
        public String clearTextValue{get;set;}
        public String cipherTextValue{get;set;}
        public String apiSessionId{get;set;}
        public String apiServerURL{get;set;}
        //public DpaaSSoapWebServiceV1.ErrorMessage[] errorMessages{get;set;}
        //public Integer errorMessagesSize{get;set;}
        
        public DpaasFieldVo(){}
        
        public DpaasFieldVo(String argApplicationName, String argEntityName, String argFieldName, String argClearTextValue){
            this.applicationName = argApplicationName;
            this.entityName = argEntityName;
            this.fieldName = argFieldName;
            this.clearTextValue = argClearTextValue;
            //this.errorMessages = new DpaaSSoapWebServiceV1.ErrorMessage[errorMessagesSize];
        }
    }
    
}