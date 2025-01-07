/*******************************************************************************************************************************
Apex Class Name : HUMSoftPhoneRoutingHelper 
Version         : 1.0 
Created On      : 09/11/2014
Function        : This Class holds as a Place holder wrapper class to store Values passed on through URL
Test Class      : HUMSoftPhoneRoutingHelperTest
Modification Log: 
 * Developer Name           Code Review              Date                       Description
 *-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * Chaitanya Kumar           17824                  09/11/2014                   Original Version
 * Adil Mohammed             20402                  01/19/2015                   REQ – 103999 : Adding suffix ID to SoftPhone call popup functionality
 * Prafull Verma             84636                  04/05/2017                  REQ-306322 : SF - TECH - Softphone routing optimization
 * Isha Gupta                                       03/29/2021                  Added variables for IVR data to be stored in interaction object
 *Navajit Sarkar/Sahil Verma 	07/12/2021					User story -2287945 Added  variables for Authentication Indicator to be stored in interaction object
 * Sahil Verma                                      09/02/2021                   US-2230000 - Variable added to fetch the interaction Id of auto interaction created
  *********************************************************************************************************************************************************************************************/

public with sharing class HUMSoftPhoneRoutingHelper { 
    
    public String sIntAboutFirstName;
    public String sIntAboutLastName;
    public String sIntAboutID;
    public String sIntAboutDOB;
    public String sIntAboutType;
    public String sIntWithFirstName;
    public String sIntWithLastName;
    public String sIntWithID;
    public String sIntWithDOB;
    public String sIntWithType;
    public String sInteractionFirstName;
    public String sInteractionLastName;
    public String sInteractionCallType;
    public String sInteractionUUID;
    public String sIntAboutSuffixId;
    public String sIntWithSuffixId;
    public boolean sAuthIndicator; //US#2287945 added for storing authentication Indicator value
    public String sInteractionId;
    public String sSurveyId; 
    public String sCallCatDesc;
    public string sCallCategory;
    public Map<String, String> mapfieldCipherMapping ;
    public Map<String,String> mapEncryptedValues;
    public String autoInteractionId;

}