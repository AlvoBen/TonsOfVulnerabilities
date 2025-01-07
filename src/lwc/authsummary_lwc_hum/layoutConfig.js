/*******************************************************************************************************************************
LWC JS Name : layoutconfig.js
Function    : This JS serves as helper to authsummary_Lc_Hum

Modification Log: 
Developer Name           Code Review                      Date                         Description
*--------------------------------------------------------------------------------------------------------------------------------
Rajesh Narode                                           14/07/2022                    User story 3362701 Authorization Summary table.
Raj Paliwal												06/12/2022					  US #3888622 Auth/Referral: Lightning Verification: Auth Clean up
*********************************************************************************************************************************/
export function getDetailFormLayout(){
    const detail = {
        object: { name: "Authorization/Referral Summary", icon: { size: "large", name: "standard:contact" } },
        recordDetail: {
            name: "", mapping: "Name", fields: [
                { label: "Date of Birth", bIsCustom: false, mapping: "Birthdate",value:""},
                { label: "Mailing Address", bIsCustom: true, bAddress: true, mapping: "PersonMailingAddress" ,value:""}
            ]
        }
}
return detail;
};