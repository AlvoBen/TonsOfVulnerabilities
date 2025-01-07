public class HoverController {

    public HoverController() {
findObj();
    }


   




   
    public boolean policyPage{get;set;}
    public boolean policyMemberPage{get;set;}
       
   
   string  urlId = ApexPages.CurrentPage().getParameters().get('id');
    String ObjName;
    public Policy__c lp {get;set;}
    public Policy_Member__c lpm {get;set;}
    
    public String policyMemberField {get;set;}
        public String strProduct {get;set;}
            public String strRecordId {get;set;}
            public String strLabelValue{get;set;}


    public Map <String, Schema.SObjectType> schemaMap = Schema.getGlobalDescribe();
    

  
 
    Public void findObj(){
          

           if(urlId != null)   
            {        
                String myIdPrefix = String.valueOf(urlID).substring(0,3); //get just the prefix
                
                for(Schema.SObjectType stype : schemaMap.values())       
                    {         
                       Schema.DescribeSObjectResult r = stype.getDescribe();           
                       String prefix = r.getKeyPrefix();      
                       
                       if(prefix!=null && prefix.equals(myIdPrefix))           
                            {
                                ObjName=r.getName();    
                                break;           
                            }       
                    } 
                    
                    
                    if(ObjName == 'Policy__c')
                    {
                    
                           
                           lp = [Select Id,Product__r.Product_Type__c, product__r.name from Policy__c where id =: urlId limit 1];
                            policyMemberField =  lp.Product__r.Product_Type__c;
                            strProduct =lp.product__r.name;
                    strRecordId = lp.id;
                    strLabelValue = 'Product Type Code';
                    }
                    
                    
                    else                 
                           if(ObjName  == 'Policy_Member__c')
                           {
                           
                               
                                
                                
                                lpm = [Select id, Product__c , Product_Type__c from Policy_Member__c where id =: urlId limit 1];
                                                  strLabelValue = 'Product Type';  
                                strProduct = lpm.Product__c;
                                  strRecordId = lpm.id;
                                policyMemberField =lpm.Product_Type__c;
                               
                                
                              }
                                   
                           
                    
                    
                        
             }
             
      } 
      
      
      
      
      

      
      
       


}