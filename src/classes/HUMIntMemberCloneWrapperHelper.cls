/*******************************************************************************************************************************
Apex Class Name : HUMIntMemberCloneWrapperHelper
Version         : 1.0
Created On      : 01/08/2014
Function        : This class serves as a wrapper class for storing Interacting About values to be cloned.
Test Class      : HUMInteractionCloneOverrideExtensionTest
Modification Log: 
 * Modification ID      Developer Name       Code Review                   Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * 1.0                  Shreya Choodamani          20337                 01/09/2014                Original Version(REQ-66090
 ****************************************************************************************************************************/
public with sharing class HUMIntMemberCloneWrapperHelper {

    /* 
     * Method name : HUMIntMemberCloneWrapperHelper
     * Description : This is the constructor of the class.
                     Accepts instance of Interaction_Member__c,
                     Boolean flag to show selection status.                      
     * Param       : Interaction_Member__c object
                     boolean flag to check whether value is selected or not.     
     * Returns     : void
     */
    public HUMIntMemberCloneWrapperHelper(Interaction_Member__c oIntMemberRec, boolean bIsSelected)
    {
        this.oIntMember = oIntMemberRec;
        this.bIsSelected = bIsSelected;
    }
    
    //Interaction Member records that need to be selected for cloning.
    public Interaction_Member__c oIntMember{get; set;}

    //Boolean flag depicts whether record is selected or not.
    public boolean bIsSelected{get; set;}
}