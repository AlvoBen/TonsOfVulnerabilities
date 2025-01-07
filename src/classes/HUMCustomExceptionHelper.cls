/*******************************************************************************************************************************
Apex Class Name : HUMCustomExceptionHelper
Version         : 1.0
Created On      : June 18 2014
Function        : Exception wrapper class to store any custom Exception data

Modification Log: 
 * Developer Name            Code Review                 Date                       Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar            17152                      06/18/2014                   Original Version
 * Syed Mubarak T             171775					01/23/2018                    Error Message storing as null fix
 *******************************************************************************************************************************/



public with sharing class HUMCustomExceptionHelper{

    /*
     * Method Name : HUMCustomExceptionHelper
     * Description : Constructor
     * Return type : void
     * Parameter   : String , String , String , String 
     */
    public HUMCustomExceptionHelper(String sErrorcode, String sErrorDescription , String sEndPoint, String sExceptionType) 
    {

        this.sErrorCode =sErrorCode;
        this.sErrorDescription = sErrorDescription;
        this.sEndPoint=sEndPoint;
        this.sExceptionType=sExceptionType; 

    }

    //Exception code 
    public String sErrorCode ;
    //Exception Description
    public String sErrorDescription ;
    // External system End Point
    public String sEndPoint;
    //Exception Type - Application / Integration
    public String sExceptionType;

}