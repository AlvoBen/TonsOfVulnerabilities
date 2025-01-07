/*******************************************************************************************************************************
Trigger Name    : ChatterMessage_G_HUM
Created On      : 5/8/2020
Function        : Trigger execution sequence logic for ChatterMessage object.
                  
Modification Log: 
* Developer Name         Code Review              Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Joel George                                    5/8/2020                 original version
*******************************************************************************************************************************/

trigger ChatterMessage_G_HUM on ChatterMessage (before insert) {
    for (ChatterMessage objT : trigger.new) {
        objT.addError(System.Label.ChatterErrorMessage);
    }
}