/**************************************************************************** 
Apex Class Name  : VeteranSaveRequest_DTO_HUM
Version          : 
Created Date     : March 04, 2021
Function         : Request DTO Wrapper for VeteransSaveService In VeteransAPI
Test Class       : 
****************************************************************************
Modification Log:
* Developer Name          Code Review #         Date                       Description
*---------------------------------------------------------------------------------------------------
*  Anjani Vaddadi                             03/04/2021                 Original Version
*************************************************************************************************** */

public class VeteranSaveRequest_DTO_HUM  
{     
    public String memberIdentityQualifier; 
    public String veteranStatus;
    public Boolean isEnrolledInVA;
    public String sourceId ;
    public String sourceSystemTimestamp;
    public String modifiedBy;
}