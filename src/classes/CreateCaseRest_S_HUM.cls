/***************************************************************************************************************************************************************************
Apex Class Name  : CreateCaseRest_S_HUM
Version          : 1.0
Created Date     : March 15 2018
Function         : This class is used to update case records asynchronously.
Test Class       : HUMCaseServiceRestTest
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Moshitha Gunasekaran          215336                     03/15/2018                  Original Version 
 * Sunil Bukya                   280927                     09/03/2018                  Implemented Platform changes for Account fields
 * Sunil Bukya                   321985             		12/26/2018                  Removed Dpaas callout code changes
 * Shailesh Mali                    		             	03/11/2019                  Making Create case rest service from Sync to Async (Load test Issue in QA)
**************************************************************************************************************************************************************/

public class CreateCaseRest_S_HUM implements Queueable, Database.AllowsCallouts {
    private CaseServiceRestCreateRequest_DTO_HUM caseRequest;
    private ID caseId;

    public CreateCaseRest_S_HUM (CaseServiceRestCreateRequest_DTO_HUM caseRequestObj, ID caseIdObj) {
        this.caseRequest = caseRequestObj;
        this.caseId = caseIdObj;       
    }
    /*  
    * Method name : execute
    * Description : This method Is used to update case records asynchronously.
    * Return Type : Void
    * Parameter   : QueueableContext    
    */
    public void execute(QueueableContext context) {
        CreateCaseRest_D_HUM.updateCaseAndAccount(caseRequest, caseId);       
    }   
}