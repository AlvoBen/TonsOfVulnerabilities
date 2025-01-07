/*******************************************************************************************************************************
Apex Trigger Name : TriggerAvoidRecursion_H_HUM 
Version           : 1.0
Created On        : 02/22/2017
Function          : This class is used to control trigger running recursively
                
Modification Log: 
* Developer Name           Code Reveiw                 Date                       Description
*------------------------------------------------------------------------------------------------------------------------------
* Harshith Mandya                                   02/20/2017                  Original Version
* Santhi Mandava                                    09/30/2020                  Added new variable 'bFutureLogicRan'
*******************************************************************************************************************************/

public class TriggerAvoidRecursion_H_HUM {
    
    public static boolean run = true;
    
    public static boolean bFutureLogicRan = false;
}