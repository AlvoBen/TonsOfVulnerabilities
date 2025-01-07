/**************************************************************************** 
Apex Class Name  : MemberPreferencesLinkResponse_LH_HUM 
Version          : 
Created Date     : 18/01/2023 
Function         : Response helper for Encrypt Rest Service EncryptResult
Test Class       : MemberPreferencesLink_LT_HUM
****************************************************************************
Modification Log:
 *   Developer                   Code Review             Date               Description
 * --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- 
 *  Vishal Shinde                                     18/01/2023           4167387- Implementation of Links & landing pages- Person account page and Medical Plan member page - Part2
 ************************************************************************************************************************************************************************************/

public class MemberPreferencesLinkResponse_LH_HUM
{
    public static MemberPreferencesLinkResponse_LH_HUM parse(String response) 
    {
        return (MemberPreferencesLinkResponse_LH_HUM) System.JSON.deserialize(response, MemberPreferencesLinkResponse_LH_HUM.class);
    }

   public EncryptResponse EncryptResponse;
    
   public class EncryptResponse
   {
    public EncryptResult EncryptResult;
   }
    
    
    /*
     * EncryptValue: Encrypted Member Gen Key      
    */
   public class EncryptResult
    {
     public String EncryptValue;    
       
    }
 }