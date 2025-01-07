/************************************************************************************************************************************************************************************       
Apex Class Name  : CreateCaseRest_S_V4_HUM.cls 
Created Date     : May 05, 2019
Function         : Queable class to update case and child records in asynchronoulsy
Test Class       : 

**Modification Log
* Developer Name          Code Review #         Date                       Description
* Asish Behera                                05/24/2019                  Intital Version
************************************************************************************************************************************************************************************/
public class CreateCaseRest_S_V4_HUM implements Queueable{

    map<Id,CreateCaseRequest_DTO_V4_HUM> map_CaseId_Request = new map<Id,CreateCaseRequest_DTO_V4_HUM>();
    public CreateCaseRest_S_V4_HUM(List<id> listOfcaseid, List<CreateCaseRequest_DTO_V4_HUM> listOfRequest){
        
        for(Integer i=0; i<listOfcaseid.size(); i++ ){
            map_CaseId_Request.put(listOfcaseid[i],listOfRequest[i]);
        }
    }
    
    /*  
    * Method name : execute
    * Description : This method Is used to update case records asynchronously.
    * Return Type : Void
    * Parameter   : QueueableContext    
    */
    public void execute(QueueableContext context) {        
        CreateCaseRestBulkified_D_V4_HUM.updateCase(map_CaseId_Request);
    }  
}