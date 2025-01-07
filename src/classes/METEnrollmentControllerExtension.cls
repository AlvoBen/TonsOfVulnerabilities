/*<!--

Class Name       : METEnrollmentControllerExtension
Version          : 1.0 
Created Date     : May 10 2017
Function         : This Class will use to display MET Enrollment secction on Case page for Medicare RecordType

Modification Log :
*  Developer Name                    Code Review               Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
* Prasad Jandhyala                    114623                 07/10/2017                  Original version
-->
*/
public with sharing class METEnrollmentControllerExtension
{
    private case objCaseRecordData;
    public string jsonMetTaskList { get; set; }
    public string jsonMetTask { get; set; }            
    
    public METEnrollmentControllerExtension(ApexPages.StandardController controller)
    {
        try
        {
            objCaseRecordData = (case)controller.getrecord();
        }
        catch(Exception Ex)
        {
            HUMExceptionHelper.logErrors(ex, 'METEnrollmentControllerExtension', 'METEnrollmentControllerExtension');
        }
        
    }
    /*
    * This method is used to populate JSON for rendering MET Tasks in MET Enrollment Section
    * @param  NA
    * @return NA
    */
    public void fetchMetTaskList()
    {
        try 
        {
            METTaskList_DTO_HUM objMETTaskList = METEnrollment_H_HUM.getTaskListDTO(objCaseRecordData.Id);
            jsonMetTaskList = JSON.serialize(objMETTaskList.listMETTaskDTO);
            jsonMetTaskList = '{"data":' + jsonMetTaskList + '}';
        }
        catch (Exception e) 
        {
            HUMExceptionHelper.logErrors(e,'METEnrollmentControllerExtension','fetchMetTaskList');
        }
    }
}