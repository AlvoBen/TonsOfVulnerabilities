/*******************************************************************************************************************************
Apex Class Name : HUMMentorToSFDCHelper 
Version         : 1.2
Created On      : 6/16/2014
Function        : This class is wrapper class for obtaining payload from Mentor

Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Pranali Choudhari        17109                       06/16/2014                   Original Version
 * Pranali Choudhari        17109    					08/06/2014            		 Made changes to the comments
 ****************************************************************************************************************************/


public with sharing class HUMMentorToSFDCHelper
{
	//Variable names are defined based on the payload obtained from Mentor
    public String DocumentName;
    public String ObjectId;
    public String DocumentURL;
   
   
	/*
	 * Method Name : HUMMentorToSFDCHelper
	 * Description : Constructor of the class
	 * Return type : None
	 * Paramater   : Takes 3 parameters, Document Name, ObjectID and DocumentURL
	 */	
    public HUMMentorToSFDCHelper(String DocumentName,String ObjectId,String DocumentURL)
    {
        this.DocumentName = DocumentName;
        this.ObjectId = ObjectId;
        this.DocumentURL = DocumentURL;           
    }
}