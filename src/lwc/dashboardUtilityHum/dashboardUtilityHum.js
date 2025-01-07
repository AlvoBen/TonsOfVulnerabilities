/*
LWC Name        : dashboardUtilityHum.js
Function        : dashboardUtilityHum used to show additional columns

Modification Log:
* Developer Name                  Date                          Description
*------------------------------------------------------------------------------------------------------------------------------
* Shailesh B                      07/03/2022                    Original Version 
* Gowthami Thota                  09/14/2023                    User Story 4828062: T1PRJ0865978 - C06, Case Management: Pharmacy - Cases/Tasks- Mirror Customer Care Profile Table - (Lightning)
* Nirmal Garg                     10/10/2023                    Switch Added
****************************************************************************************************************************/
import { LightningElement } from 'lwc';
import hasCRMS_206_CCSHumanaPharmacyAccess from '@salesforce/customPermission/CRMS_206_CCSHumanaPharmacyAccess';
import hasCRMS_684_Medicare_Customer_Service_Access from '@salesforce/customPermission/CRMS_684_Medicare_Customer_Service_Access';
import hasCRMS_685_PCC_Customer_Service_Access from '@salesforce/customPermission/CRMS_685_PCC_Customer_Service_Access';
import hasCRMS_240_GBO_Segment_Service_Access from '@salesforce/customPermission/CRMS_240_GBO_Segment_Service_Access';
import hasCRMS_205_CCSPDPPharmacyPilot from '@salesforce/customPermission/CRMS_205_CCSPDPPharmacyPilot';
import hasCRMS_302_HPTraditionalInsuranceData from '@salesforce/customPermission/CRMS_302_HPTraditionalInsuranceData';
import hasCRMS_301_HPInsuranceData from '@salesforce/customPermission/CRMS_301_HPInsuranceData';
import hasCRMS_300_HP_Supervisor_Custom from '@salesforce/customPermission/CRMS_300_HP_Supervisor_Custom';
import { getCustomSettingValue } from 'c/genericCustomSettingValueHum';
/*This method is used to get the user group settings.
Parms:
* @param {*} oThis           controller this operator to set the track variable
*/
export const getUserGroup = function () {
    


    let oUserGroup = {};
    if (hasCRMS_206_CCSHumanaPharmacyAccess || hasCRMS_684_Medicare_Customer_Service_Access) {
        oUserGroup.bRcc = true;
    }
    if (hasCRMS_685_PCC_Customer_Service_Access) {
        oUserGroup.bProvider = true;    //provider
    }
    if (hasCRMS_240_GBO_Segment_Service_Access) {
        oUserGroup.bGbo = true;
    }
    if (hasCRMS_205_CCSPDPPharmacyPilot || hasCRMS_302_HPTraditionalInsuranceData || hasCRMS_301_HPInsuranceData || hasCRMS_300_HP_Supervisor_Custom) {
        oUserGroup.bPharmacy = true; //
    }
    if ((!oUserGroup.bRcc && !oUserGroup.bProvider && !oUserGroup.bGbo && !oUserGroup.bPharmacy) || Object.keys(oUserGroup).length > 1) {
        oUserGroup.bGeneral = true;
    }
    return oUserGroup;
}

function getColumns(prf, oUserGroup, bSwitch) {
    let profileColumns = {};
    if(prf === 'Customer Care Supervisor'){
        if(oUserGroup.bGeneral === true){
            profileColumns = sGeneralColumnsSUP();
        }
        else if(oUserGroup.bPharmacy === true){
            //need information
            profileColumns = sPharmacyColumnsSUP(bSwitch);
        }
        else if(oUserGroup.bProvider === true){
            profileColumns = sPCCColumnsSUP();
        }
        else if(oUserGroup.bRcc === true){
            profileColumns = sRCCColumnsSUP();
        }
        else if(oUserGroup.bGbo === true ){
            profileColumns = sGBOColumnsSUP();
        }

    }
    else if(prf ==='Customer Care Specialist' || prf === 'Humana Pharmacy Specialist'){
        if(oUserGroup.bGeneral === true){
            profileColumns = sGeneralColumnsCSR();
        }
        else if(oUserGroup.bGbo === true ){
            profileColumns = sGBOColumnsCSR();
        }
        else if(oUserGroup.bPharmacy === true){
            //300 for supervisor
            if(hasCRMS_300_HP_Supervisor_Custom){
                profileColumns = sPharmacyColumnsSUP(bSwitch);
            }
            else{
                profileColumns = sPharmacyColumnsCSR(bSwitch);
            }
        }
        else if(oUserGroup.bProvider === true){
            profileColumns = sPCCColumnsCSR();
        }
        else if(oUserGroup.bRcc === true){
            profileColumns = sRCCColumnsCSR();
        }
    }
    return profileColumns;
}


export const getTotalColumns = function (prf) {
    return new Promise((resolve, reject) => {
        try {
            const oUserGroup = getUserGroup();
            let profileColumns = {};
            if (oUserGroup.bPharmacy === true) {
                getCustomSettingValue('Switch', '4828062').then(result => {
                    if (result && result?.IsON__c === true) {
                        profileColumns = getColumns(prf, oUserGroup, true);
                        resolve(profileColumns);
                    } else {
                        profileColumns = getColumns(prf, oUserGroup, false);
                        resolve(profileColumns);
                    }
                }).catch(error => {
                    console.log(error);
                    profileColumns = getColumns(false);
                });
            } else {
                profileColumns = getColumns(prf, oUserGroup, false);
                resolve(profileColumns);
            }
        }
        catch (error) {
            console.log(error);
            reject(error);
        }        
    })    
}


export const sGBOColumnsCSR = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } },  }, 
                                { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true },  { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true,  wrapText: true }, 
                                { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true },  { label: 'Item Age', fieldName: 'decWorkItemAge', type: 'text', sortable: true,  wrapText: true  },
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } },  wrapText: true, initialWidth : 10  }, 
                                { label: 'Open Work Tasks', fieldName: 'sOpenWorkItems', type: 'text', sortable: true, initialWidth : 145 },{ label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true,  wrapText: true  },
                                { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true,  wrapText: true,typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true,typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  }];

    oColumns.optColumns = [{ label: 'Select All', value: 'selectall' }, { label: 'Classification', value: 'sClassification' }, { label: 'Intent', value: 'sIntent'},
                                { label: 'View', value: 'sCaseTaskView', cellAttributes: { class: { fieldName: 'viewColor' } }},  { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, { label: 'Image Received Date', value: 'sreceiveddate', initialWidth : 130 }, 
                                { label: 'Date/Time Opened', value: 'dtCreatedDate' } ];          
    return oColumns;
}
export const sGBOColumnsSUP = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } }  }, 
                                { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true  },  { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true, wrapText: true, tooltip: { fieldName: 'sStatus' } }, 
                                { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true  },  { label: 'Item Age', fieldName: 'decWorkItemAge', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth : 10  }, 
                                { label: 'Open Work Tasks', fieldName: 'sOpenWorkItems', type: 'text', sortable: true, wrapText: true, initialWidth : 145  },{ label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  }];

    oColumns.optColumns = [{ label: 'Select All', value: 'selectall' }, { label: 'Classification', value: 'sClassification' }, { label: 'Intent', value: 'sIntent'},
                                { label: 'Owner', value: 'sOwner' },   { label: 'View', value: 'sCaseTaskView', cellAttributes: { class: { fieldName: 'viewColor' } }},  { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, 
                                { label: 'Image Received Date', value: 'sreceiveddate', initialWidth : 130 },  { label: 'Date/Time Opened', value: 'dtCreatedDate' } ];          
    return oColumns;
}
export const sPCCColumnsCSR = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } }  }, 
                                { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true, wrapText: true, tooltip: { fieldName: 'sStatus' }  },                             
                                { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true,  wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  }, 
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth : 10  },                          
                                { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true  },{ label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true  }, 
                                { label: 'Item Age', fieldName: 'decWorkItemAge', type: 'text', sortable: true, wrapText: true  }, { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true  }];
 
    oColumns.optColumns =   [{ label: 'Select All', value: 'selectall' }, { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth : 145 }, { label: 'Due Date', value: 'dDueDate'}, 
                                { label: 'View', value: 'sCaseTaskView', cellAttributes: { class: { fieldName: 'viewColor' } }}, { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' },  { label: 'Image Received Date', value: 'sreceiveddate', initialWidth : 130 },                
                                { label: 'Date/Time Opened', value: 'dtCreatedDate' } ];
                                          
    return oColumns;
}
export const sPCCColumnsSUP = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } } }, 
                                { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true, wrapText: true  },                             
                                { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } } },
                                { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true ,typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } } }, 
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true , initialWidth : 10 },                          
                                { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true  },{ label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true  }, 
                                { label: 'Item Age', fieldName: 'decWorkItemAge', type: 'text', sortable: true, wrapText: true  }, { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true  }];

    oColumns.optColumns =   [{ label: 'Select All', value: 'selectall' }, { label: 'Owner', value: 'sOwner'}, { label: 'Due Date', value: 'dDueDate'}, 
                            { label: 'View', value: 'sCaseTaskView', cellAttributes: { class: { fieldName: 'viewColor' } }},{ label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth : 145 },{ label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, 
                            { label: 'Image Received Date', value: 'sreceiveddate', initialWidth : 130 }, { label: 'Date/Time Opened', value: 'dtCreatedDate' } ];
                                                            
    return oColumns;
}
export const sRCCColumnsCSR = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } } }, 
                                { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true, wrapText: true  },                             
                                { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true,  wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true,typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  },{ label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true  }, { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true  },
                                { label: 'View', fieldName: 'sCaseTaskView', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'viewColor' } }, wrapText: true  },
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth : 10  },                          
                                { label: 'Date/Time Opened', fieldName: 'dtCreatedDate',  type: 'text', sortable: true, wrapText: true  }];
                               

    oColumns.optColumns =   [{ label: 'Select All', value: 'selectall' },{ label: 'Priority', value: 'sPriority'},{ label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth : 145 },
                                { label: 'Due Date', value: 'dDueDate'},{ label: 'Item Age', value: 'decWorkItemAge' }, { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, 
                                { label: 'Image Received Date', value: 'sreceiveddate', initialWidth : 130 }];
                                                            
    return oColumns;
}

export const sRCCColumnsSUP = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } } }, 
                                { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true, wrapText: true,typeAttributes: { tooltip: { fieldName: 'sStatus' } }},                             
                                { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true,  wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', wrapText: true , type: 'url', sortable: true, wrapText: true,typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  },{ label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true  }, { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true  },
                                { label: 'View', fieldName: 'sCaseTaskView', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'viewColor' } }, wrapText: true  },
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, initialWidth : 10, wrapText: true  },                          
                                { label: 'Date/Time Opened', fieldName: 'dtCreatedDate',  type: 'text', sortable: true, wrapText: true  }];
                               

    oColumns.optColumns =   [{ label: 'Select All', value: 'selectall' },{ label: 'Priority', value: 'sPriority'},{ label: 'Owner', value: 'sOwner'},
                                { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth : 145 },                
                                { label: 'Due Date', value: 'dDueDate'},{ label: 'Item Age', value: 'decWorkItemAge' }, { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, 
                                { label: 'Image Received Date', value: 'sreceiveddate', initialWidth : 130 }];
                                                            
    return oColumns;
}
export const sGeneralColumnsCSR = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' },  tooltip: { fieldName: 'sCaseComment' } } }, 
                                { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true, wrapText: true  },   { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true ,
                                typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  },
                                { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true  }, { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Item Age', fieldName: 'decWorkItemAge', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth : 10  },                          
                                { label: 'Date/Time Opened', fieldName: 'dtCreatedDate',  type: 'text', sortable: true, wrapText: true  }];


    oColumns.optColumns =   [{ label: 'Select All', value: 'selectall' }, { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth : 145 },  
                                { label: 'Priority', value: 'sPriority'}, { label: 'Interacting With', value: 'sInteractingWithIdUrl', type: 'url', typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'View', value: 'sCaseTaskView', cellAttributes: { class: { fieldName: 'viewColor' } }},{ label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, 
                                { label: 'Image Received Date', value: 'sreceiveddate',initialWidth : 130 }];

                       
    return oColumns;
}

export const sGeneralColumnsSUP = function () {
    let oColumns = {};
    oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' }, tooltip: { fieldName: 'sCaseComment' } } }, 
                                { label: 'Status', fieldName: 'sStatus' , type: 'text', sortable: true },   { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true,typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } }  },
                                { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true  }, { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true },
                                { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Item Age', fieldName: 'decWorkItemAge', type: 'text', sortable: true, wrapText: true  },
                                { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth : 10  },                          
                                { label: 'Date/Time Opened', fieldName: 'dtCreatedDate',  type: 'text', sortable: true, wrapText: true  }];


    oColumns.optColumns =   [{ label: 'Select All', value: 'selectall' }, { label: 'Open Work Tasks', value: 'sOpenWorkItems',initialWidth : 145 },  
                                { label: 'Priority', value: 'sPriority'},
                                { label: 'Interacting With', value: 'sInteractingWithIdUrl', type: 'url', typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } }  },
                                { label: 'Owner', value: 'sOwner'},
                                { label: 'View', value: 'sCaseTaskView', cellAttributes: { class: { fieldName: 'viewColor' } }},{ label: 'Reopen Item Age', value: 'decReopenWorkItemAge' }, 
                                { label: 'Image Received Date', value: 'sreceiveddate',initialWidth : 130 }];            
    return oColumns;
}

export const sPharmacyColumnsCSR = function (bSwitch) {
    let oColumns = {};
    if (bSwitch) {
        oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' }, tooltip: { fieldName: 'sCaseComment' } } },
        { label: 'Status', value: 'sStatus' },
        { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } } },
        { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true },
        { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true },
        { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true },
        { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true },
        { label: 'Item Age', value: 'decWorkItemAge' },
        { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth: 10 },
        { label: 'Date/Time Opened', value: 'dtCreatedDate' }];
        oColumns.optColumns = [{ label: 'Select All', value: 'selectall' }, { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth: 145 },
        { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true },
        { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } } },
        { label: 'View', fieldName: 'sCaseTaskView', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'viewColor' } }, wrapText: true },
        { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' },
        { label: 'Image Received Date', value: 'sreceiveddate', initialWidth: 130 }];
    } else {
        oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' }, tooltip: { fieldName: 'sCaseComment' } } },
        { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true },
        { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } } },
        { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } } },
        { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true },
        { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true }, { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true },
        { label: 'View', fieldName: 'sCaseTaskView', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'viewColor' } }, wrapText: true },
        { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true },
        { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth: 10 }];

        oColumns.optColumns = [{ label: 'Select All', value: 'selectall' },
        { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth: 145 },
        { label: 'Status', value: 'sStatus' }, { label: 'Item Age', value: 'decWorkItemAge' },
        { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' },
        { label: 'Image Received Date', value: 'sreceiveddate', initialWidth: 130 },
        { label: 'Date/Time Opened', value: 'dtCreatedDate' }];
    }
    return oColumns;
}

export const sPharmacyColumnsSUP = function (bSwitch) {
    let oColumns = {};
    if (bSwitch) {
        oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' }, tooltip: { fieldName: 'sCaseComment' } } },
        { label: 'Status', value: 'sStatus' },
        { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } } },
        { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } } },
        { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true },
        { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true },
        { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true },
        { label: 'View', fieldName: 'sCaseTaskView', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'viewColor' } }, wrapText: true },
        { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth: 10 },
        { label: 'Date/Time Opened', value: 'dtCreatedDate' }];

        oColumns.optColumns = [{ label: 'Select All', value: 'selectall' },
        { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true },
        { label: 'Owner', value: 'sOwner' },
        { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth: 145 },
        { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true },
        { label: 'Item Age', value: 'decWorkItemAge' }, { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' },
        { label: 'Image Received Date', value: 'sreceiveddate', initialWidth: 130 }];
    } else {
        oColumns.staticColumns = [{ label: 'Case/Task ID', fieldName: 'sCaseTaskUrl', type: 'url', sortable: true, typeAttributes: { label: { fieldName: 'sCaseTaskNumber' }, tooltip: { fieldName: 'sCaseComment' } } },
        { label: 'Priority', fieldName: 'sPriority', type: 'text', sortable: true, wrapText: true },
        { label: 'Interacting With', fieldName: 'sInteractingWithIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingWith' }, tooltip: { fieldName: 'sInteractingWith' } } },
        { label: 'Interacting About', fieldName: 'sInteractingAboutIdUrl', type: 'url', sortable: true, wrapText: true, typeAttributes: { label: { fieldName: 'sInteractingAbout' }, tooltip: { fieldName: 'sInteractingAbout' } } },
        { label: 'Classification', fieldName: 'sClassification', type: 'text', sortable: true, wrapText: true },
        { label: 'Intent', fieldName: 'sIntent', type: 'text', sortable: true, wrapText: true }, { label: 'Owner Queue', fieldName: 'sCaseTaskQueue', type: 'text', sortable: true, wrapText: true },
        { label: 'View', fieldName: 'sCaseTaskView', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'viewColor' } }, wrapText: true },
        { label: 'Due Date', fieldName: 'dDueDate', type: 'text', sortable: true, wrapText: true },
        { label: 'Case Age', fieldName: 'decCaseAge', type: 'text', sortable: true, cellAttributes: { class: { fieldName: 'accountColor' } }, wrapText: true, initialWidth: 10 }];

        oColumns.optColumns = [{ label: 'Select All', value: 'selectall' }, { label: 'Open Work Tasks', value: 'sOpenWorkItems', initialWidth: 145 },
        { label: 'Status', value: 'sStatus' }, { label: 'Owner', value: 'sOwner' },
        { label: 'Item Age', value: 'decWorkItemAge' }, { label: 'Reopen Item Age', value: 'decReopenWorkItemAge' },
        { label: 'Image Received Date', value: 'sreceiveddate', initialWidth: 130 }, { label: 'Date/Time Opened', value: 'dtCreatedDate' }];
    }
    return oColumns;
}