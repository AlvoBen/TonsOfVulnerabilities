/**************************************************************************************************************************************
Apex Class Name : HumIntTrackEnrollmentServiceTest
Version         : 1.0
Created On      : 08/05/2014
Function        : This class mocks the response of HumIntTrackEnrollmentService

* Modification Log:
* Developer Name           Code Review                  Date                       Description
*---------------------------------------------------------------------------------------------------------------------------------------
* Shruthi Karanth            17165                     08/05/2014                 Original Version
****************************************************************************************************************************************/

@isTest
global class HumIntTrackEnrollmentServiceTest implements WebServiceMock{

   global void doInvoke(
           Object stub,
           Object request,
           Map<String, Object> response,
           String endpoint,
           String soapAction,
           String requestName,
           String responseNS,
           String responseName,
           String responseType) {

            HUMIntTrackEnrollmentResponseHelper.ResponseComplexType respComplex = new HUMIntTrackEnrollmentResponseHelper.ResponseComplexType();
            HUMIntTrackEnrollmentResponseHelper.TrackList TrackListResponse = new HUMIntTrackEnrollmentResponseHelper.TrackList();
            List<HUMIntTrackEnrollmentResponseHelper.Track> lstTrack = new List<HUMIntTrackEnrollmentResponseHelper.Track>();
            HUMIntTrackEnrollmentResponseHelper.Track respElement = new HUMIntTrackEnrollmentResponseHelper.Track();
            respElement.Source_Track_Id = ' T1234';
            respElement.Recipient_Track_Id = ' Res T1234';
            respElement.Source_System_Cd = 'Source 1234';
            respElement.Source_Id_Type_Cd = ' Source Type 1234';
            respElement.Recipient_Id_Type_Cd  = 'Res Id 1234';
            respElement.Track_Date= null;
            respElement.Track_Time = null;
            respElement.Status_Cd = ' Test';
            respElement.Trans_Rec_Type_Cd  = 'Test';
            respElement.Last_Name = ' Patchy';
            respElement.First_Name = ' Panda';
            respElement.Birth_Date = null;
            respElement.Zip_Cd = ' 30324';
            respElement.Group_Id = ' T1234';
            respElement.Category_Id = ' T1234';
            respElement.Line_Of_Coverage = ' T1234';
            respElement.Coverage_Type_Cd  = ' T1234';
            respElement.Pend_Key = ' T1234';
            lstTrack.add(respElement);
            TrackListResponse.Track = lstTrack;
            respComplex.TrackList = TrackListResponse;
            response.put('response_x', respComplex);
            
   }
    
}