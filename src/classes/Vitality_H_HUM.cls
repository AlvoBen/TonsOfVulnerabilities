public with sharing class Vitality_H_HUM {

    public static string serializeEntityId(VitalityEntity_DTO_HUM oSerializable){
            
        string sSerialized = '';
            
        if(oSerializable.EntityId !=null && !string.isBlank(oSerializable.EntityId)){       
            return JSON.serialize(oSerializable);
        } else return sSerialized;
    }
}