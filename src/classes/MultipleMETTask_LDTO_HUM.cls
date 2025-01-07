/************************************************************************************************************************************************
  Apex Class Name  : MultipleMETTask_LDTO_HUM
  Version          : 1.0
  Function         : DTO Class for Multiple MET Task fetching multiple Milestones records
  Test Class       : METEnrollment_LT_HUM
  *************************************************************************************************************************************************
  Modification Log:
  *Developer Name             Review Number               Date                       Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
  *Pooja Kumbhar                                       12/22/2022                  Original Version : 
 ******************************************************************************************************************************************************************************/

public with sharing class MultipleMETTask_LDTO_HUM implements Comparable {
        public String sTaskId{get; set;}
        public String sTaskName{get; set;}
        public String sActionId{get; set;}
        public String sActionName{get; set;}
        public String sSourceId{get; set;}
        public String sSourceName{get; set;}
        public Boolean bLaunchEMME {get; set;}
		public Integer iOrder {get; set;}

        public MultipleMETTask_LDTO_HUM(MET_Milestone__c oMETMilestone) {
            this.sTaskId = oMETMilestone.Task__c;
            this.sTaskName = oMETMilestone.Task__r.name;
            this.sActionId = oMETMilestone.Action__c;
            this.sActionName = oMETMilestone.Action__r.name;
            this.sSourceId = oMETMilestone.Source_Name__c;
            this.sSourceName = oMETMilestone.Source_Name__r.name;
            this.iOrder = Integer.valueof(oMETMilestone.Met_Entries_Assoc_Order__c);
            this.bLaunchEMME = oMETMilestone.Source_Name__r.Launch_EMME__c;
        }
		
		
		public Integer compareTo(Object compareTo) {
			MultipleMETTask_LDTO_HUM compareToMileStone = (MultipleMETTask_LDTO_HUM)compareTo;
			if (this.iOrder == compareToMileStone.iOrder) return 0;
			if (this.iOrder > compareToMileStone.iOrder) return 1;
			return -1;
    
		}
    }