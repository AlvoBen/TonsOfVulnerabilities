/*****************************************************************************************************
 Apex Class Name  	: BaseDataTable_C_HUM
 Version   			: 1.0 
 Created Date  		: 11/13/2017
 Function  			: Holds the logic for object permission dynamic by passing the object name from page to component
 Test Class  		:   
 
 Modification Log :
* Developer                 Code Review         Date                        Description
* -----------------------------------------------------------------------------------------------------                 
*  Pardeepkumar Dani		  149513			11/13/2017					Original Version 
*  Pallavi R                  215883            04/16/2018               REQ - 357339 - Changed class to Global to allow usage on standard layouts 
*******************************************************************************************************/

global with sharing class BaseDataTable_C_HUM
{
    public boolean canEdit {get; set;}
    public boolean canDelete {get; set;}
    public string sObjectName ;
    
    public string getSObjectName ()
    { 
    	return sObjectName; 
    }
    
    public void setSObjectName (string s)
    {
        sObjectName = s;
        canEdit     = Schema.getGlobalDescribe().get(sObjectName).getDescribe().isUpdateable();
        canDelete   = Schema.getGlobalDescribe().get(sObjectName).getDescribe().isDeletable();
    }
    
    /**
    *  To delete the record from DB
    *  @name deleterecord
    *  @param NA
    *  @return void
    *  @throws NA
    */
    @RemoteAction
    global static boolean deleteRecord(string deleteID, String objectName) 
    {
        boolean retBool = true;
        try
        {
	        IF(string.isNotBlank(deleteID))
	        {
	            string query = '';
	            query = 'SELECT id from '+objectName + ' WHERE Id=:deleteID';
	            sobject s = Database.query(query);
	            delete s;
	        }  
        }
        catch(Exception e)
        {
        	retBool = false;
        	HUMExceptionHelper.logErrors(e,'DataTable_C_HUM','deleteRecord'); 
        }
        return retBool;      
    }
}