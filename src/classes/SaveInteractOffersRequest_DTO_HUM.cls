/**
   @Author Gargey Sharma
   @name SaveInteractOffersRequest_DTO_HUM
   @CreateDate 01/04/2017
   @Description Request helper class for Save Interact Offers Service
   @Version 1.0
   @reference REQ - 291110
   @Test Class 
*/ 
/*Modification Log: 
 * Developer Name           Code Review                 Date                         Description
 *------------------------------------------------------------------------------------------------------------------------------
 * Gargey Sharma         		                      01/04/2017                     Original version
 * Gargey Sharma         		                      02/27/2017                     Added new input field EventReason
 * Gargey Sharma         		                      03/03/2017                     REQ - 304171 : Added new input fields to SessionContext
********************************************************************************************************************************/
public with sharing class SaveInteractOffersRequest_DTO_HUM 
{
	public SaveOffersRequest SaveOffersRequest = null;
	public class SaveOffersRequest 
	{
		public SessionContext SessionContext;
		public PostEventContext PostEventContext;
	}	
	
	public class SessionContext 
	{
		public string SessionID = '';
		public string InqAboutID = '';
		public string InqAboutType = '';
		public string AudienceLevel = '';
		public string QueueName = '';
		public string ClassificationType = '';
		public string InteractionPoint = '';
		public string OffersLimit = '';
		public string ConsumerReferenceId = '';
		public string OfferDeliveringConsumerID = '';
	}
	
	public class PostEventContext 
	{
		public string EventType = '';
		public string TreatmentCodes = '';
		public string EventReason = '';
	}
}