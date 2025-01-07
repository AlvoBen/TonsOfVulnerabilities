/*******************************************************************************************************************************
Apex Class Name :  QueueViewActivateDeactivate_C_HUM
Version: 1.0
Created On      : 06/13/2016  
Function        : Controller for QueueViewActivateDeactivate_VF_HUM

Modification Log:  
* Developer Name            Code Review                Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya          	  		                06/13/2016                   Original Version
* Harshith Mandya 			 50014					11/30/2106				  	 Implemented REQ - 290990 -  removed all lookup related fields from
																				 Case/ Task / Interaction / Attachment To Work Queue Setup And Queue View
********************************************************************************************************************************/

Public with sharing Class QueueViewActivateDeactivate_C_HUM
{
	Private QueueViewActivateDeactivate_S_HUM sLayer = New QueueViewActivateDeactivate_S_HUM();  
    Public Queue_View__c queueView = New Queue_View__c();
    Public ID sWVID = NULL;
    
    /**
	   * invokes service layer for constructing the variables.
	   * <p>
	   * Constructor Method to for the class , Initialize variables
    */
    Public QueueViewActivateDeactivate_C_HUM()
    {
		init();        
    } 
    
    /**
    * 
    * <p>
    * Method to activate/de-activate queue view records
    *
    * param		NULL
    * @return   Void
    */
    Public PageReference activateDeactivate() 
    {
    	Return sLayer.activateDeactivate(this);
    }
    
    /**
    * <p>
    * Method to set all the values required 
    *
    * @param     NULL
    * @return    Void											
    */ 
    Private Void init() 
    {
        Try
        {
            sWVID = (ID)ApexPages.currentPage().getParameters().get(GLOBAL_CONSTANT_HUM.ID_HUM);
            If (sWVID!= null)
            {
                queueView = sLayer.getQueueView(this);
            }   
        }
        Catch(Exception ex)
        {
            HUMExceptionHelper.logErrors(ex, 'QueueViewActivateDeactivate_C_HUM', 'init');
            HUMExceptionHelper.bShowErrorOnPage = false;
        }
    }
}