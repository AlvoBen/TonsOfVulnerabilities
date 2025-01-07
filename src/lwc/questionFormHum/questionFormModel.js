/*******************************************************************************************************************************
LWC JS Name : questionFormModel.js
Function    : This JS serves as config for password form 

Modification Log: 
Developer Name           Code Review                      Date                       Description
*--------------------------------------------------------------------------------------------------------------------------------
* Supriya Shastri                                         03/12/2020                 initial version
*********************************************************************************************************************************/
export const editForm =[
   {
      labelName: 'New Password',
      labelFor: 'new-password',
      fieldName: 'password',
      variant: 'label-hidden',
      fieldId: 'new-password',
      value: '', 
      showValidation: false,
      errorMsg: 'Please enter a password',
      errorClass: 'new-fields error-highlights slds-p-top_x-small'
   },{
      labelName: 'New Question',
      labelFor: 'new-quest',
      fieldName: 'question',
      variant: 'label-hidden',
      fieldId: 'new-quest',
      value: '',
      showValidation: false,
      errorMsg: 'Please enter a question',
      errorClass: 'new-fields error-highlights slds-p-top_x-small'
   },{
      labelName: 'New Answer',
      labelFor: 'new-ans',
      fieldName: 'answer',
      variant: 'label-hidden',
      fieldId: 'new-ans',
      value: '',
      showValidation: false,
      errorMsg: 'Please enter an answer',
      errorClass: 'new-fields error-highlights slds-p-top_x-small'
   }];