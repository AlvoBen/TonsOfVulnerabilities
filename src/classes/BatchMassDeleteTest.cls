@istest
public class BatchMassDeleteTest{

   
   
   @testSetup static void testDataSetup(){
    List<Member_Cache__c>   Mcachelist= new List<Member_Cache__c>();
    
      Member_Cache__c MCache = new Member_Cache__c ();
      MCache.Member_gen_key__c = '8105003080401';
      date datetoday=date.valueof('2018-05-14');
      MCache.Is_web_registered__c= 'true';
      insert MCache ;
   }
   
   
   
   static testMethod void testMassdelete() 
    {
         Database.QueryLocator QL;
          Database.BatchableContext BC;
          SchedulableContext SC;
        List<Member_Cache__c> MCList= new List<Member_Cache__c>();
                
            BatchMassDeleteRecs obj = new BatchMassDeleteRecs();
            obj.execute(SC); 
             try{ 
              QL=obj.start(bc);
            obj.execute(BC, MCList);
            obj.finish(BC);
        }catch(DMLException ex){
           system.assertnotEquals(ex.getMessage(), ex.getMessage()); 
          HUMExceptionHelper.logErrors(ex,'Records deletion have a problem from object','finish');
          
        }
          
    }
   
   
   
   
   
   
 
}