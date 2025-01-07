/*
LWC JS Name       : commonHighlightPanelIconHum.js
Function          : JS controller to commonHighlightPanelIconHum.html

Modification Log:
* Developer Name                        Date                       Description
*------------------------------------------------------------------------------------------------------------------------------------------------------------------
* Nilanjana Sanyal                     03/16/2022 				Initial version - Saperating the icon display in a common component (US-3091965, US-3091967)
* Nilanjana Sanyal                     06/16/2022               DF-5116: Lightning_US 3082787 -  Icon visibility / Plan Member Page - (...) dots are being visible for less than 4 Icons




******************************************************************************************************************************************************************** */


import { api,LightningElement, track, wire } from 'lwc';


export default class CommonHighlightPanelIconHum extends LightningElement {

//@api pageName;
@track hMemberIcons =[];
@track vMemberIcons =[];
@track indexIcon=false;
@track indexDropDown = false;
@track boolVisible = false;

    // Method to featch and display the availabe member icons
    @api loadMemberIcons(memberIconList, hLimit) {
        const me = this;
        console.log('in loadMemberIcons of commonHighlightPanelIconHum' + JSON.stringify(memberIconList));
        console.log ('Total length:' +memberIconList.length);
        console.log('Limit received for horizontal icon in common highlight panel:', hLimit);
        //console.log('page name:' , me.pageName);
        me.indexIcon = true;
        console.log('Boolean Check:', me.indexIcon); 
        
            // Splitting the list into two lists if the no of icons are more than four                                  
            if(memberIconList)
            {  
				memberIconList  = memberIconList.filter(function (icon) {
                return icon.bIconVisible == true ;
                });
				
                if (memberIconList.length > hLimit)
                {                  
                            me.hMemberIcons = memberIconList.slice(0, hLimit);
                            console.log('hMemberIcons passing:' +JSON.stringify(me.hMemberIcons));
                                                    
                            me.vMemberIcons = memberIconList.slice(hLimit, memberIconList.length);
                            console.log('vMemberIcons passing:' +JSON.stringify(me.vMemberIcons)); 
                            
                            me.indexDropDown = true;
                            console.log('Dropdown Check:', me.indexDropDown);
                                                    
                }
                // Assigning  a single list if the no of icons not more than four 
                else 
                {
                    me.hMemberIcons = memberIconList;
                    console.log('hMemberIcons passing:' +JSON.stringify(me.hMemberIcons));
                }       
            }
    }

// to load the dropdown list on mouse hover the dropdown button
    showDropDown(event)
    { 
        this.boolVisible = !this.boolVisible ? true : false;
    }
// to keep the dropdown list hidden on page load and mosue-out from the dropdown button
    hideDropDown(event)
    {
        this.boolVisible = false;
    }


}