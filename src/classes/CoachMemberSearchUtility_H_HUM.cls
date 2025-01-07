/*******************************************************************************************************************************
Apex Class Name : CoachMemberSearchUtility_H_HUM
Version         : 1.0
Created On      : 03/08/2021
Function        : This class is used for performing the Member Search for Wellness Coaching APp
Test Class      : 
                 
Modification Log: 
* Version          Developer Name             Code Review              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------
*    1.0           Jasmeen Shangari                                   03/08/2021                Original Version
*******************************************************************************************************************************************************************/
public with sharing class CoachMemberSearchUtility_H_HUM 
{
    
    /*
    * Method is used to identify if the logged in user is a RSO User having access to  Medicare Customer Service Access. 
    * <p>
    * @return   boolean
    */
    public static boolean isUserHavingMedicareCustomerServiceAccess()
    {
        
        boolean bMedicareCustomerServiceAccess = false;
        String sUserId = UserInfo.getUserId();
        
        List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :sUserId AND PermissionSet.Name = : GLOBAL_CONSTANT_HUM.MEDICARECALLS_PERMISSIONSET_HUM];
        
        if(null != lstPMAssign && !lstPMAssign.isEmpty()) bMedicareCustomerServiceAccess = true;

        return bMedicareCustomerServiceAccess;
    }
    
    /*
    * Check user is having permission set access or not
    * @param String PermissionSetName
    * @returnParam Boolean bHasPermission
    */
    public static boolean isUserhavingPermissionSetAccess(String sPermissionSetName){
        boolean bHasPermission = false;
        try{
            List<PermissionSetAssignment> lstPMAssign = [SELECT PermissionSetId FROM PermissionSetAssignment WHERE AssigneeId= :UserInfo.getUserId() AND PermissionSet.Name = :sPermissionSetName];
            if(null != lstPMAssign && !lstPMAssign.isEmpty()) bHasPermission = true;
        }catch(exception ex){
            bHasPermission = false;
            HUMExceptionHelper.logErrors(ex, 'CoachMemberSearchUtility__HUM', 'isUserHavingPharmvilleAccess');
        }
        return bHasPermission;
    }
    /*  
     * Method name : deduceOSB
     * Description : This method deduces whether to show OSB Product Type or Not      
     * Return Type : boolean
     * Parameter :   Osb Switch On and Inidicator Value
     */
      public static Boolean deduceOSB(Boolean isOSBOn, string osbIndicatorValue) {
        string globalOSBIndicatorValue = GLOBAL_CONSTANT_CH_HUM.OSB_Indicator_value;
        Boolean canProceed = false;
        
        switch on string.valueof(isOSBOn) {
            when 'true' {
                canProceed = true;
            }
            when else {
                canProceed = (osbIndicatorValue != globalOSBIndicatorValue) ? true : false;
            }
        }
        
        return canProceed;
    }
        /**
     * Check CRM Functionality ON/OFF switch (CRMFunctionality_ONOFF_Switch__c)
     * @param String - story number
     * @return Boolean
     */
    public static Boolean isCRMFunctionalityON(String sStoryNumber) {
        Boolean bCRMFunctionalityON = false;
        try {
            if(String.isNotBlank(sStoryNumber)) {
                CRMFunctionality_ONOFF_Switch__c csCRMOnOffSwitch = CRMFunctionality_ONOFF_Switch__c.getValues(sStoryNumber);
                if(csCRMOnOffSwitch != null) {
                    bCRMFunctionalityON = csCRMOnOffSwitch.IsON__c;
                }
            }
        } catch(Exception ex) {
            bCRMFunctionalityON = false;
        }
        return bCRMFunctionalityON;
    }

     /*
     * Method Name   :    validateInputDataCombinationsMedicare
     * Description   :    This method is used to validate combinational search in Member screen on Case edit Acount Search
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM , ClickSource
     */
    public static boolean validateInputDataCombinationsMedicare(MemberSearchWrapper_CH_HUM oSearchWrapper , Boolean clicksource)
    {
        if(!memberSearchFieldsEnteredMedicare(oSearchWrapper))    return false;
        
        // When ID  OR ( First Name AND Last Name ) OR Email combinations are given
        if(!String.isBlank(oSearchWrapper.sMemberId) || (!String.isBlank(oSearchWrapper.sMemberId) && !String.isBlank(oSearchWrapper.sSuffix)) 
           || ((!(String.isBlank(oSearchWrapper.sFirstName)))&& (!(String.isBlank(oSearchWrapper.sLastName)))))
        {
            return true;
        }
        else
        {
            // When Only Birth Date given OR if we enter (First Name OR Last Name ) with Birth Date given
            if(!errorBirthDateSearchMedicare(oSearchWrapper)) return false;
            
            // When Only Zip code given OR if we enter (First Name OR Last Name ) with Zip code given
            if(!errorZipCodeSearchMedicare(oSearchWrapper))   return false;
            
            // When Only State given OR if we enter (First Name OR Last Name ) with State given
            if(!errorStateSearchMedicare(oSearchWrapper)) return false;
            
            //When only First Name or Last Name is given
            if(!errorFirstLastNameSearchMedicare(oSearchWrapper)) return false;
            
            //When only suffix is given
            if(!validateSuffixSearch(oSearchWrapper)) return false;
        }
        return false;
    }

     /*
     * Method Name   :    validateInputDataCombinations
     * Description   :    This method is used to validate combinational search in Member screen
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean validateInputDataCombinations(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(!memberSearchFieldsEntered(oSearchWrapper))    return false;
        
        // When ID OR Group Number OR ( First Name AND Last Name ) OR Phone number OR Email combinations are given
        if(!String.isBlank(oSearchWrapper.sMemberId) || (!String.isBlank(oSearchWrapper.sMemberId) && !String.isBlank(oSearchWrapper.sSuffix))|| (!(String.isBlank(oSearchWrapper.sGroupNumber))) ||(!(String.isBlank(oSearchWrapper.sPhone)))  
           || ((!(String.isBlank(oSearchWrapper.sFirstName)))&& (!(String.isBlank(oSearchWrapper.sLastName)))) || (!String.isBlank(oSearchWrapper.sPID)))
        {
            return true;
        }
        else
        {
            // When Only Birth Date given OR if we enter (First Name OR Last Name ) with Birth Date given
            if(!errorBirthDateSearch(oSearchWrapper)) return false;
            
            // When Only Zip code given OR if we enter (First Name OR Last Name ) with Zip code given
            if(!errorZipCodeSearch(oSearchWrapper))   return false;
            
            // When Only State given OR if we enter (First Name OR Last Name ) with State given
            if(!errorStateSearch(oSearchWrapper)) return false;
            
            //When only First Name or Last Name is given
            if(!errorFirstLastNameSearch(oSearchWrapper)) return false;
            
            //When only suffix is given
            if(!validateSuffixSearch(oSearchWrapper)) return false;
        }
        return false;
    }

    
    /* Method Name   :  getTokeizeQueryMapSoftphone
     * Description   :    This method is used to return query when member search will driven by Softphone
     * Return Type   :    Map<String, String>
     * Parameters    :    Map<String, String>   
    */
    public static Map<String, String> getQueryMapSoftphone(Map<String,String> oSearchWrapperwithCipher)
    {
        Map<String, String> mapFieldValueResult = new Map<String, String>();
        String sValue='';
        if(NULL != oSearchWrapperwithCipher)
        {
            for ( string fieldName: oSearchWrapperwithCipher.keyset())
            {
                if(!String.isEmpty(oSearchWrapperwithCipher.get(fieldName)))
                {
                    sValue = oSearchWrapperwithCipher.get(fieldName);
                    if(fieldName.equalsIgnoreCase(System.Label.HUMAccountLastName) || fieldName.equalsIgnoreCase(System.Label.HUMAccountFirstName) || fieldName.equalsIgnoreCase(System.Label.HUMAccountName))
                    {
                        mapFieldValueResult.put(fieldName, '('+HUMStringHelper.parseSearchStringWithORs(fieldName, 'LIKE', NULL, 'OR', sValue, true)+')'); 
                    }
                    else
                    {
                        mapFieldValueResult.put(fieldName, '('+HUMStringHelper.parseSearchStringWithORs(fieldName, '=', NULL, 'OR', sValue , true)+')');
                    }
                }   
            }
        }
        system.debug('mapFieldValueResult in LH'+mapFieldValueResult);
        return mapFieldValueResult;
    }


        /*
     * Method Name   :    memberSearchFieldsEnteredMedicare
     * Description   :    This method is used to validate if any field is entered or not - Member search
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean memberSearchFieldsEnteredMedicare(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if((String.isBlank(oSearchWrapper.sBirthdate)) && (String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId))
             && (oSearchWrapper.sState == HUMConstants.HUMStateDefaultValue)
            && (String.isBlank(oSearchWrapper.sPostalCode)) && (String.isBlank(oSearchWrapper.sSuffix))&& (String.isBlank(oSearchWrapper.sPID)))
        {
            throw new HUMCustomException(HUMConstants.HUM_Member_Search_Fields_Not_Entered);
        }
        return true;
    }

       
    /*
     * Method Name   :    errorBirthDateSearchMedicare
     * Description   :    This method is used to validate if Birth date field is filled or not
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorBirthDateSearchMedicare(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sBirthdate))
        {
            if((String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId)))
            {
                throw new HUMCustomException(HUMConstants.HumErrorBirthDateSearchMedicare);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HUMConstants.HumErrorBirthDateSearchCombination);
            }
        }
        return true;
    }
   
    
    /*
     * Method Name   :    errorZipCodeSearchMedicare
     * Description   :    This method is used to validate if zipcode field is filled or not for Acccount edit on CaseEdit for Medicare Case
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorZipCodeSearchMedicare(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sPostalCode))
        {
            if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId))
            {
                throw new HUMCustomException(HUMConstants.HumErrorZipCodeSearchMedicare);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HUMConstants.HumErrorPostalCodeSearchCombination);
            }
        }
        return true;
    }
    
       
    /*
     * Method Name   :    errorStateSearchMedicare
     * Description   :    This method is used to validate if state field is filled or not for Acccount edit on CaseEdit for Medicare Case
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorStateSearchMedicare(MemberSearchWrapper_CH_HUM oSearchWrapper)
   {
        if(String.isNotBlank(oSearchWrapper.sState))
        {    
            if(!(oSearchWrapper.sState.equalsIgnoreCase(HUMConstants.HUMStateDefaultValue)))
            {
                if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId))
                {
                    throw new HUMCustomException(HUMConstants.HumErrorStateSearchMedicare);
                }
                else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
                {
                    throw new HUMCustomException(HUMConstants.HumErrorStateSearchCombination);
                }
            }
        }
        return true;
    }
    
       
    /*
     * Method Name   :    errorFirstLastNameSearchMedicare
     * Description   :    This method is used to validate if first/last field is filled or not for Acccount edit on CaseEdit for Medicare Case
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorFirstLastNameSearchMedicare(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        //When only First Name is given
        if(!String.isBlank(oSearchWrapper.sFirstName) && (String.isBlank(oSearchWrapper.sLastName) 
                                                          && oSearchWrapper.sState.equalsIgnoreCase(HUMConstants.HUMStateDefaultValue)
                                                          && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HUMConstants.HumErrorFirstNameSearchMedicare);
        }
        //When only Last Name is given
        else if(!String.isBlank(oSearchWrapper.sLastName) && (String.isBlank(oSearchWrapper.sFirstName)
                                                              && oSearchWrapper.sState.equalsIgnoreCase(HUMConstants.HUMStateDefaultValue)
                                                              && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HUMConstants.HumErrorLastNameSearchMedicare);
        }
        return true;
    }
        /*
     * Method Name   :    validateSuffixSearch
     * Description   :    This method is used to validate member id and suffix combination.
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean validateSuffixSearch(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sSuffix) && String.isBlank(oSearchWrapper.sMemberId))
        {
            throw new HUMCustomException(HUMConstants.HumSearchSuffixMemberIdValidation);
        }
        return true;
    }

    /*
     * Method Name   :    memberSearchFieldsEntered
     * Description   :    This method is used to validate if any field is entered or not - Member search
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean memberSearchFieldsEntered(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if((String.isBlank(oSearchWrapper.sBirthdate)) && (String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId))
            && (String.isBlank(oSearchWrapper.sGroupNumber)) && (oSearchWrapper.sState == HUMConstants.HUMStateDefaultValue) && (String.isBlank(oSearchWrapper.sPhone)) 
            && (String.isBlank(oSearchWrapper.sPostalCode)) && (String.isBlank(oSearchWrapper.sSuffix)))
        {
            throw new HUMCustomException(HUMConstants.HUM_Member_Search_Fields_Not_Entered);
        }
        return true;
    }

      
    /*
     * Method Name   :    errorBirthDateSearch
     * Description   :    This method is used to validate if Birth date field is filled or not
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorBirthDateSearch(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sBirthdate))
        {
            if((String.isBlank(oSearchWrapper.sLastName)) && (String.isBlank(oSearchWrapper.sFirstName)) && (String.isBlank(oSearchWrapper.sMemberId))
               && (String.isBlank(oSearchWrapper.sGroupNumber)) && (String.isBlank(oSearchWrapper.sPhone)))
            {
                throw new HUMCustomException(HUMConstants.HumErrorBirthDateSearch);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HUMConstants.HumErrorBirthDateSearchCombination);
            }
        }
        return true;
    }

     
    /*
     * Method Name   :    errorZipCodeSearch
     * Description   :    This method is used to validate if zipcode field is filled or not
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorZipCodeSearch(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(!String.isBlank(oSearchWrapper.sPostalCode))
        {
            if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId)
               && String.isBlank(oSearchWrapper.sGroupNumber) && String.isBlank(oSearchWrapper.sPhone))
            {
                throw new HUMCustomException(HUMConstants.HumErrorZipCodeSearch);
            }
            else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
            {
                throw new HUMCustomException(HUMConstants.HumErrorPostalCodeSearchCombination);
            }
        }
        return true;
    }

       
    /*
     * Method Name   :    errorStateSearch
     * Description   :    This method is used to validate if state field is filled or not
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorStateSearch(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        if(String.isNotBlank(oSearchWrapper.sState))
        {    
            if(!(oSearchWrapper.sState.equalsIgnoreCase(HUMConstants.HUMStateDefaultValue)))
            {
                if(String.isBlank(oSearchWrapper.sLastName) && String.isBlank(oSearchWrapper.sFirstName) && String.isBlank(oSearchWrapper.sMemberId)
                   && String.isBlank(oSearchWrapper.sGroupNumber) && String.isBlank(oSearchWrapper.sPhone))
                {
                    throw new HUMCustomException(HUMConstants.HumErrorStateSearch);
                }
                else if(String.isBlank(oSearchWrapper.sFirstName) || String.isBlank(oSearchWrapper.sLastName))
                {
                    throw new HUMCustomException(HUMConstants.HumErrorStateSearchCombination);
                }
            }
        }
        return true;
    }
    /*
     * Method Name   :    errorFirstLastNameSearch
     * Description   :    This method is used to validate if first/last field is filled or not
     * Return Type   :    boolean
     * Parameters    :    MemberSearchWrapper_CH_HUM
     */
    public static boolean errorFirstLastNameSearch(MemberSearchWrapper_CH_HUM oSearchWrapper)
    {
        //When only First Name is given
        if(!String.isBlank(oSearchWrapper.sFirstName) && (String.isBlank(oSearchWrapper.sLastName) 
                                                          && String.isBlank(oSearchWrapper.sGroupNumber) && oSearchWrapper.sState.equalsIgnoreCase(HUMConstants.HUMStateDefaultValue) && String.isBlank(oSearchWrapper.sPhone)
                                                          && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HUMConstants.HumErrorFirstNameSearch);
        }
        //When only Last Name is given
        else if(!String.isBlank(oSearchWrapper.sLastName) && (String.isBlank(oSearchWrapper.sFirstName)
                                                              && String.isBlank(oSearchWrapper.sGroupNumber) && oSearchWrapper.sState.equalsIgnoreCase(HUMConstants.HUMStateDefaultValue) && String.isBlank(oSearchWrapper.sPhone)
                                                              && String.isBlank(oSearchWrapper.sMemberId) && String.isBlank(oSearchWrapper.sBirthdate) && String.isBlank(oSearchWrapper.sPostalCode)))
        {
            throw new HUMCustomException(HUMConstants.HumErrorLastNameSearch);
        }
        return true;
    }

}