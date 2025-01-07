/*
Modification Log: 
* Developer Name                       	Date                        Description
* Nirmal Garg                   		02/27/2023                 Initial Version
*------------------------------------------------------------------------------------------------------------------------------
--*/
import { LightningElement } from 'lwc';
import isHomeOfficeUser from '@salesforce/apex/CheckUserHOAccess_LC_Hum.isHomeOfficeUser';

const checkHomeOfficeAccess =(gpname) =>{
    return new Promise((resolve,reject)=>{
        isHomeOfficeUser({groupname : gpname}).then(result =>{
            resolve(result);
        }).catch(error =>{
            console.log(error);
            reject(false);
        })
    })
}



export{checkHomeOfficeAccess}