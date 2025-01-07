global with sharing class FileOperation{

    public String docId{get;set;}
    public String folderId{get;set;}
    public String name{get;set;}
    public String contentType{get;set;}
    public String type{get;set;}
    public String body{get;set;}
    //public String title{get;set;}
    public String apiSessionId{get;set;}
    public String apiServerURL{get;set;}
    public boolean selected {get; set;}
    public String status {get;set;}
    public Boolean processedFile {get;set;}
    
    public FileOperation(){
    this.selected = false;
    }
    
}