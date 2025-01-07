/***************************************************************************************************************************************************************************
Apex Class Name  : CreateCase_S_HUM
Version          : 1.0
Created Date     : May 24 2017
Function         : This class is used to update case records asynchronously.
Test Class       : HUMCaseServiceTest
****************************************************************************************************************************************************************************
Modification Log:

 * Developer Name                Code Review                Date                        Description
 *---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Sumit Sharma                  103536                    05/24/2017                  Original Version 
 * Sunil Bukya                   280927                    09/03/2018                  Implemented Platform changes for Account fields
 * Sunil Bukya                   321985               	   12/26/2018                  Removed Dpaas callout code changes
 **************************************************************************************************************************************************************/

public class CreateCase_S_HUM implements Queueable{
    private HUMCreateCaseRequestHelper caseRequest;
    private ID caseId;

    public CreateCase_S_HUM (HUMCreateCaseRequestHelper caseRequestObj, ID caseIdObj) {
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
        CreateCase_D_HUM.updateCaseAndAccount(caseRequest, caseId);       
    }   
}