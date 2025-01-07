/*******************************************************************************************************************************
LWC JS Name : pharmacyRightSourceIntegrationHum.js
Function    : This component is used to call RightSource Srvice. 
Modification Log: 
  Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Nirmal Garg                                           01/25/2023                 Original Version
               
*********************************************************************************************************************************/

import { LightningElement } from 'lwc';
import { GetMemberDTO } from './getMemberRequest';
import invokeRightSourceService from '@salesforce/apexContinuation/Pharmacy_LC_HUM.invokeRightSourceService';

export default class PharmacyRightSourceIntegrationHum extends LightningElement {}

const invokeGetMemberService = (memId,networkId,startdate,enddate,accountId) =>{
    let request={};
    request.GetMemberRequest = new GetMemberDTO(memId,networkId,startdate,enddate);
    return new Promise((resolve,reject)=>{
        callRightSourceService(request,'GetMember').then(result =>{
            resolve(result);
        }).catch(error =>{
            reject(error)
        })
    })
}

const callRightSourceService = (request,type)=>{
    return new Promise((resolve,reject) =>{
        invokeRightSourceService({request : JSON.stringify(request), type : type})
        .then(result =>{
            resolve(result);
        }).catch(error =>{
            reject(error);
        })
    })
}

export{invokeGetMemberService}