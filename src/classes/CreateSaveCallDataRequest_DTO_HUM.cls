/*******************************************************************************************************************************************************
Apex Class Name  : CreateSaveCallDataRequest_DTO_HUM
Version          : 1.0
Created Date     : 03/15/2021
Function         : This DTO class is used to generate saveCallData service Request Body
*****************************************************************************************************************************************************

Modification Log:
* Developer Name            		 Review Number            Date                       Description
-----------------------------------------------------------------------------------------------------------------------------------------------------
* Arpit Jain/Navajit Sarkar                              03/15/2021                 Original Version
* Arpit Jain/Navajit Sarkar                              03/18/2021                 Added methods to get service bodies
* Arpit Jain/Shreya Agarwal                              07/12/2021                 US-2357061 : IVR Call End or Transfer related method added to get saveCallData service request body
**********************************************************************************************************************************************/
public class CreateSaveCallDataRequest_DTO_HUM 
{	
    public  saveCallDataDTO SaveCallDataRequest;    
    public class saveCallDataDTO
    {
        public String  Application = '';
        public String  UUID = '';
        public DTO CallDataValues;
    }

    public class DTO
    {
        public List<CallDataValuesDTO> CallDataValuesList;
    }
    
    public Class CallDataValuesDTO
    {
        public String DataName='';
        public String DataValue='';
    }

    /*
    * Method name : requestStructuresaveCallData  
    * Description : This method will form the requestbody for SaveCallData service
    * Parameter   :Map<String,String> saveCallDataMapping,Map<String,String> StateDataMapping,String UUID
    * Return : CreateSaveCallDataRequest_DTO_HUM
    */
    public static CreateSaveCallDataRequest_DTO_HUM requestStructuresaveCallData(Map<String,String> saveCallDataMapping,Map<String,String> StateDataMapping,String UUID)
    {
        CreateSaveCallDataRequest_DTO_HUM oRequest = New CreateSaveCallDataRequest_DTO_HUM();
        CreateSaveCallDataRequest_DTO_HUM.saveCallDataDTO saveRequest = New CreateSaveCallDataRequest_DTO_HUM.saveCallDataDTO();        
        CreateSaveCallDataRequest_DTO_HUM.DTO dtoObj = New CreateSaveCallDataRequest_DTO_HUM.DTO();
        List<CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO> CallDataList = new List<CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO>();
        Map<String,String> CallDataListMap = new Map<String,String>();
        
        if(saveCallDataMapping.size()>0)
        {
            for(String key: saveCallDataMapping.KeySet())
            {
                CallDataListMap.put(key,saveCallDataMapping.get(key));
            }
        }
        if(StateDataMapping.size()>0)
        {
            for(String key: StateDataMapping.KeySet())
            {
                CallDataListMap.put(key,(String)StateDataMapping.get(key));
            }
        }
        
        for(String key: CallDataListMap.keySet())
        {
            CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO callDataDTO = New CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO(); 
            callDataDTO.DataName=key;
            callDataDTO.DataValue=CallDataListMap.get(key);
            CallDataList.add(callDataDTO); 
        }        	
        dtoObj.CallDataValuesList = CallDataList;
        saveRequest.Application = '';
        saveRequest.UUID = UUID;
        saveRequest.CallDataValues = dtoObj;
        oRequest.SaveCallDataRequest=saveRequest;   
        return oRequest;
    }

    /**
    * Returns CreateSaveCallDataRequest_DTO_HUM
    * <p>
    * Method to form the requestbody for SaveCallData service for lightning
    *
    * @param  Map < String, String > disconnectCallDatamapping ,Map < String, String > saveCallDataMappingss,String UUID
    * @return CreateSaveCallDataRequest_DTO_HUM
    */
    public static CreateSaveCallDataRequest_DTO_HUM requestStructuresaveCallDatalightning(Map <String, String> disconnectCallDatamapping, Map <String, String> saveCallDataMappingss,String UUID) 
    {
        CreateSaveCallDataRequest_DTO_HUM oRequest = New CreateSaveCallDataRequest_DTO_HUM();
        CreateSaveCallDataRequest_DTO_HUM.saveCallDataDTO saveRequest = New CreateSaveCallDataRequest_DTO_HUM.saveCallDataDTO();
        CreateSaveCallDataRequest_DTO_HUM.DTO dtoObj = New CreateSaveCallDataRequest_DTO_HUM.DTO();
        List < CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO > CallDataList = new List < CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO > ();
        Map < String, String > callDataListMap = new Map < String, String > ();
        if (saveCallDataMappingss.size() > 0)
        {
            for (String key: saveCallDataMappingss.keySet()) callDataListMap.put(key, saveCallDataMappingss.get(key));
        } 
        if(disconnectCallDatamapping.size() > 0)
        {
            if (disconnectCallDatamapping.get(PhoneBookConstants_H_HUM.isTransferredCall) == 'N')
            {
                callDataListMap.put(PhoneBookConstants_H_HUM.CALL_TYPE, disconnectCallDatamapping.get(PhoneBookConstants_H_HUM.CALL_TYPE));
                callDataListMap.put(PhoneBookConstants_H_HUM.MODULE_NAME, disconnectCallDatamapping.get(PhoneBookConstants_H_HUM.MODULE_NAME));
                callDataListMap.put(PhoneBookConstants_H_HUM.AGENT_CALL_START_TIME, disconnectCallDatamapping.get(PhoneBookConstants_H_HUM.AGENT_CALL_START_TIME));
            }
            if (disconnectCallDatamapping.get(PhoneBookConstants_H_HUM.isConsultBlindClicked) == 'Y') 
            {
                callDataListMap.put(PhoneBookConstants_H_HUM.TRANSFER_ETIM, disconnectCallDatamapping.get(PhoneBookConstants_H_HUM.AGENT_CALL_END_TIME));
            }
        }
        for (String key: callDataListMap.keySet()) 
        {
            CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO callDataDTO = New CreateSaveCallDataRequest_DTO_HUM.CallDataValuesDTO();
            callDataDTO.DataName = key;
            callDataDTO.DataValue = callDataListMap.get(key);
            CallDataList.add(callDataDTO);
        }
        dtoObj.CallDataValuesList = CallDataList;
        saveRequest.Application = '';
        saveRequest.UUID = UUID;
        saveRequest.CallDataValues = dtoObj;
        oRequest.SaveCallDataRequest = saveRequest;
        return oRequest;
    }
}