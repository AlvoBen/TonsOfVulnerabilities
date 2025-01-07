public with sharing class Release_Charts_Gantt_Helper {

  public String myId {get;set;}
  public Boolean allowView {get;set;}
  
  ApexPages.StandardController stdCont;
  
  public Release_Charts_Gantt_Helper(ApexPages.StandardController stc){
    myId = stc.getId();
    allowView = Apexpages.currentPage().getParameters().get('p') == null;
  }

  static testMethod void testController() {
    
    Release__c rel = Release1_Test_Utility.sampleProject('Basic Sample For Test' + DateTime.now().getTime());
    insert rel;
    
    ApexPages.StandardController stc = new ApexPages.StandardController(rel);
        Release_Charts_Gantt_Helper cont = new Release_Charts_Gantt_Helper(stc);
    System.assert(cont.myId != null);
    
    
  }

    
}