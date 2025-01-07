/********************************************************************************************************************************
Apex Class Name  : ImageViewer_C_HUM
Version          : 1.0 
Created Date     : 03/21/2016
Test Class       : ImageViewer_T_HUM
Function         : This is used to display image viewer.
Modification Log :
* Developer                       Code review              Date                 Description
* ----------------------------------------------------------------------------------------------------------------------------------
* Santhi Mandava                  24069                    03/21/2016           Original Version
* Santhi Mandava                  27512                    05/12/2016           Added sorting feature as per REQ - 263794
* Santhi Mandava                                           07/26/2016            Implemented attachment body decryption logic.
**************************************************************************************************************************************/

public with sharing class ImageViewer_C_HUM 
{
    public static List<LoggingWrapper_DTO_HUM> lstImages{get;set;}
    
       /*
     * Method Name : loadImageViewer
     * Description : This method is used to load images in imageviewer.
     * Return type : void
     * Parameter   : NA
     */
    public void loadImageViewer()
    {
        String sCurrentPageData= ApexPages.currentPage().getParameters().get('pageIds');
        LoggingWrapper_DTO_HUM objNew = new LoggingWrapper_DTO_HUM();
        lstImages =new List<LoggingWrapper_DTO_HUM>();
        if(String.IsNotBlank(sCurrentPageData))
        {
            List<String> lstIds = (List<String>) JSON.deserialize(sCurrentPageData,List<String>.class);
            For(integer i = 0; i <= lstIds.size()-1 ; i++)
            {
                objNew = new LoggingWrapper_DTO_HUM();
                objNew.sImageString = 'data:image/png;base64,' + lstIds[i];
                lstImages.Add(objNew);                   
            }
        }
    } 
}