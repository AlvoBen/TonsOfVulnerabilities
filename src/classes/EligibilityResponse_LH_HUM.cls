/*****************************************************************************************************************************************************************************
    @Apex Class Name  : EligibilityResponse_LH_HUM
    @Version          : 1.0
    @Created Date     : 06/09/2022
    @Test Claa        : EligibilityResponse_LT_HUM
******************************************************************************************************************************************************************************
Modification Log:

* Developer Name           Review Number              Date                       Description
*-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Kajal Namdev                                 		  06/15/2022                 Original Version
******************************************************************************************************************************************************************************/

public class EligibilityResponse_LH_HUM {  
    public static string sClassName = 'EligibilityResponse_LH_HUM';
    string templateName;
    string caseRecordId;
    string personId;
    EligiblityResponse_DTO_HUM responseDTO; 

    /**
    * @description this class will be used to raise exception when there is logic or data issues
    */
    public class DataException extends Exception {}

    /**
    * @description this is the constructor method where assignment to private variables will happen
    * @param EligiblityResponse_DTO_HUM, String, String
    */
    public EligibilityResponse_LH_HUM(EligiblityResponse_DTO_HUM responseDTO, string templateName, string caseRecordId)
    {
        this.templateName = templateName;
        this.caseRecordId = caseRecordId;
        this.responseDTO = responseDTO;
    }
    
    /**
    * @description this method acts as the engine to start the rest of the processes
    * @param EligiblityResponse_DTO_HUM, String, String
    * @return String
    */
    public string processEligiblity()
    {
        string returnValue;
        Map<String, Object> returnMap = new Map<String, Object>();
        try {
            //0. Fetch Person Id
            this.derivePersonId();

            //1. Retrieve initial data models for Eligbility Processing
            returnMap = this.deriveEligibleModels();

            //2. Stringify the map
            //returnValue = JSON.serialize(returnMap);
        }
        catch (Exception ex) {
            //returnValue = JSON.serialize(returnMap);
            HUMExceptionHelper.logErrors(ex, sClassName, 'processEligiblity');
        }
        returnValue = JSON.serialize(returnMap);
        return returnValue;
    }

    /**
    * @description this method is used to retrieve person id
    * @param none
    * @return none
    */
    private void derivePersonId()
    {
        GenericHelper_LightningStrides_LH_HUM genericHelper = new GenericHelper_LightningStrides_LH_HUM();
        this.personId = genericHelper.fetchPersonId(this.caseRecordId);
    }

    /**
    * @description this method is used to retrieve initial data models for Eligbility Processing
    * @param none
    * @return Map<String, String>
    */
    private Map<String, Object> deriveEligibleModels()
    {
        Map<String, Object> returnMap = new Map<String, Object>();

        //1. deduce highest eligiblity record by following Active vs Future vs Termed
        HighestEligibleHeirarchy_LH_HUM heirarchyClass = 
            new HighestEligibleHeirarchy_LH_HUM(this.responseDTO, this.templateName, this.personId);
        returnMap = heirarchyClass.deduceHighestHeirarchy();

        //2. deduce the heirarchy and routing messages

        //3. deduce the record id and record collection for submissions

        return returnMap;
    }
}