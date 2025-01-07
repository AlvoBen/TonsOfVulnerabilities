/******************************************************************************************************************************
Apex Class Name  : Storefront_NotificationsWrapper_HUM
Version          : 1.0 
Created Date     : 03/04/2020 

Modification Log :
*    Developer             Code Review            Date                 Description
* ------------------------------------------------------------------------------------------------------------------------------                
*    Vinoth L					  				  04/14/2021	       Original Version : User Story - 196045- Identify Active Notifications 
*  	 Vinoth L                                     05/04/2021           User Story 2046674- Notification Interaction Generation
*    Vinoth L                                     05/14/2021           User Story 2071202 -CRM Storefront Notifications: Date Suppression
*    Vinoth L                                     05/14/2021           User Story 2068463 -CRM Storefront Notifications: Determine Last Notification Date
*    Vinoth L                      				  07/11/2021           User Story 1717300 -CRM Storefront Notifications: T1PRJ0001894 - MF 8 - CRM Storefront Home Page: First Onsite Visit 
*    Vinoth L                                  	  06/15/2022           User Story 3316885: T1PRJ0154546 / SF / MF9 Storefront - SNP: Action Required/Account Hover/Alert Icon
*    Vinoth L                                     11/28/2022           REQ-4012388 T1PRJ0154546 / SF/ MF9 Storefront - Ability to View First Time Visit - Currebnt CY in Storefront
*********************************************************************************************************************************/
public class Storefront_NotificationsWrapper_HUM {
@AuraEnabled public Account accountRec=new Account();
@AuraEnabled public List<Storefront_Notification__C> listOfNotificationRec=new List<Storefront_Notification__C>();
@AuraEnabled public String previousInteractionDate='';
@AuraEnabled public String previousInteractionLocation=''; 
@AuraEnabled public String interactionCateory=''; 
@AuraEnabled public String checkInType='';  
@AuraEnabled public String visitorType=''; 
@AuraEnabled public String currentLocation=''; 
@AuraEnabled public String visitorId='';
@AuraEnabled public List<Id> listOfFollowUpNotificationsId=new List<Id>(); 
@AuraEnabled public String sdohAcronym='';
@AuraEnabled public Boolean isPreviousAllVirtualInteration= False;  
@AuraEnabled public String visitorIndicator='';
@AuraEnabled public Boolean fstTimeClndrVst = False;
}