/***************************************************************************************************************************************************************************
Apex Class Name  : CreateInteractionRest_S_HUM
Version          : 1.0
Created Date     : 05/13/2019
Function         : This class is used to create interaction records asynchronously.
Test Class       : 
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Prasanthi Kandula                                    05/13/2019                REQ - 399198 - Asynchronous call for WATSON
 * Moshitha Gunasekaran                                 07/05/2021                US2446677 - Salesforce to Softphone Case Interaction Web Services 
 **************************************************************************************************************************************************************/

public class CreateInteractionRest_S_HUM implements Queueable, Database.AllowsCallouts {
    private CreateInteractionRequest_DTO_HUM interactionRequest;
    private CallInteractionRequest_DTO_HUM callInteractionRequest;
    private ID interactionId;

    public CreateInteractionRest_S_HUM (CreateInteractionRequest_DTO_HUM interactionReqObj, ID interactionIdObj) {
        this.interactionRequest = interactionReqObj;
        this.interactionId = interactionIdObj;       
    }

    /**
	* Constructor of the class to Instantiate the variables for Call related service
	*
	* @param    CallInteractionRequest_DTO_HUM   Call Interaction Request
	* @param    Id      Interaction Record Id
	* @return   na
	*/
    public CreateInteractionRest_S_HUM (CallInteractionRequest_DTO_HUM callInteractionReqObj, ID interactionIdObj) {
        this.callInteractionRequest = callInteractionReqObj;
        this.interactionId = interactionIdObj;       
    }

    /*  
    * Method name : execute
    * Description : This method Is used to update case records asynchronously.
    * Return Type : Void
    * Parameter   : QueueableContext    
    */
    public void execute(QueueableContext context) {
        if(null != interactionRequest)
        {
            CreateInteractionWithCaseRest_D_HUM.createInteractionRecord(interactionRequest , interactionId);       
        }
        else if(null != callInteractionRequest)
        {
            CreateInteractionWithCaseRest_D_HUM.createCallInteractionRecord(callInteractionRequest , interactionId);       
        }
    }   
}