/***************************************************************************************************************************************************************************
Apex Class Name  : UpdateInteractionRest_S_HUM
Version          : 1.0
Created Date     : 05/13/2019
Function         : This class is used to update interaction records asynchronously.
Test Class       : 
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Prasanthi Kandula                                    05/13/2019                REQ - 399198 - Asynchronous call for WATSON
 **************************************************************************************************************************************************************/

public class UpdateInteractionRest_S_HUM implements Queueable, Database.AllowsCallouts {
    private UpdateInteractionRequest_DTO_HUM interactionRequest;
    private string interactionId;

    public UpdateInteractionRest_S_HUM (UpdateInteractionRequest_DTO_HUM interactionReqObj, String interactionIdObj) {
        this.interactionRequest = interactionReqObj;
        this.interactionId = interactionIdObj;       
    }
    /*  
    * Method name : execute
    * Description : This method Is used to update case records asynchronously.
    * Return Type : Void
    * Parameter   : QueueableContext    
    */
    public void execute(QueueableContext context) {
        UpdateInteractionWithCaseRest_D_HUM.updateInteractionRecord(interactionRequest , interactionId);       
    }   
}