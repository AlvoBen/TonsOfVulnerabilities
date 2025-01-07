export function getModel(modelname) {
  if (modelname) {
    switch (modelname) {
      case "templateDataModel":
        return TemplateDataModel;

    }
  }
}

export const TemplateDataModel = [
  {
    Template_Field_Name__c: 'Request details',

    Screen__c: 'PCPServiceFund',

    Order__c: 10,

    label: 'Request details'
  }, {
    Template_Field_Name__c: 'Is the member an established patient with the new PCP',

    Screen__c: 'PCPServiceFund',

    Order__c: 5,

    label: 'Is the member an established patient with the new PCP'
  }, {
    Template_Field_Name__c: 'CAS ID from PAAG, applicable',

    Screen__c: 'PCPServiceFund',

    Order__c: 3,

    label: 'CAS ID from PAAG, application'
  }, {
    Template_Field_Name__c: 'Provide details for Other reason',

    Screen__c: 'PCPProviderSearch',

    Order__c: 3,
    Display : true,

    label: 'Other reason'
  }, {
    Template_Field_Name__c: 'Frozen panel only: name of the office personnel who confirmed established patien',

    Screen__c: 'PCPServiceFund',

    Order__c: 6,

    label: 'Frozen panel only: name of the office personnel who confirmed established patien'
  }, {
    Template_Field_Name__c: 'PCP number or Center number (Florida or Nevada market)',

    Screen__c: 'PCPServiceFund',

    Order__c: 1,

    label: 'PCP number or Center number (Florida or Nevada market)'
  }, {
    Template_Field_Name__c: 'Requested effective date',

    Screen__c: 'PCPServiceFund',

    Order__c: 8,

    label: 'Requested effective Date'
  }, {
    Template_Field_Name__c: "Did you receive a Service Fund edit when attempting to change the member's PCP",

    Screen__c: 'PCPProviderSearch',
    Display : false,
    Order__c: '',

    label: ''
  }, {
    Template_Field_Name__c: 'Name and information of the new Practice, member wishes to select',

    Screen__c: 'PCPProviderSearch',
    Question__c: 'Name and information of the new Practice, member wishes to select',
    Display : false,
    JoinField : 'Name',
    label: 1
  }
  , {
    Template_Field_Name__c: 'Callback number, if different than the number in the system',

    Screen__c: 'PCPServiceFund',

    Order__c: 11,

    label: 'Callback number, if different than the number in the system'
  }, {
    Template_Field_Name__c: 'Reason for change',

    Screen__c: 'PCPProviderSearch',

    Order__c: 2,
    Display : true,
    label: 'Reason for change'
  }, {
    Template_Field_Name__c: 'Name of the individual doctor',
    Screen__c: 'PCPServiceFund',
    Order__c: 4,
    label: 'Name of the individual doctor'
  }, {
    Template_Field_Name__c: 'What is the name of the physician you are currently assigned to',

    Screen__c: 'PCPProviderSearch',
    Display : true,
    Order__c: 1,

    label: 'What is the name of the physician you are currently assigned to?'
  }, {
    Template_Field_Name__c: 'Group name, if applicable',

    Screen__c: 'PCPServiceFund',

    Order__c: 2,

    label: 'Group name, if applicable'
  }, {
    Template_Field_Name__c: 'Are there any current referrals or authorizations on file',

    Screen__c: 'PCPQuestion',

    Order__c: 5,

    label: 'Are there any current referrals or authorizations on file?'
  }, {
    Template_Field_Name__c: 'Location/address of new physician',

    Screen__c: 'PCPProviderSearch',
    Display : false,
    Order__c: 4,
    JoinField : 'Address',
    label: 'Name and Address of the new Physician selected'
  }, {
    Template_Field_Name__c: 'Do you/they have any surgery scheduled with the current PCP',

    Screen__c: 'PCPQuestion',

    Order__c: 4,

    label: 'Do you/they have any surgery scheduled with the current PCP?'
  }, {
    Template_Field_Name__c: 'Are you working a call with a member',
    Type__c: 'Textarea',
    Screen__c: '',
    Question__c: 'Are you working a call with a member',
    Answer: '',
    Value__c: '',
    Order__c: '',
    Required__c: true,
    Options__c: '',
    label: 'Question 22'
  }, {
    Template_Field_Name__c: 'Who you spoke with that advised the patient is established. Include any addition',
    Screen__c: 'PCPServiceFund',
    Order__c: '7',
    label: 'Who you spoke with that advised the patient is established. Include any addition?'
  }, {
    Template_Field_Name__c: 'Are you/they currently an inpatient in a hospital',
    Screen__c: 'PCPQuestion',Order__c: '3',
    label: 'Are you/they currently an inpatient in a hospital?'
  }, {
    Template_Field_Name__c: 'Are you/they currently receiving medical treatment',
    Screen__c: 'PCPQuestion',
    Order__c: 2,
    label: 'Are you/they currently receiving medical treatment?'
  }, {
    Template_Field_Name__c: 'Does the member want to update their PCP due to an appointment with a new doctor',
    Screen__c: 'PCPQuestion',
    Order__c: '1',
    label: 'Does the member want to update their PCP due to an appointment with a new doctor?'
  }, {
    Template_Field_Name__c: 'Effective date of change',
    Screen__c: 'PCPProviderSearch',
    Order__c: 3,
    Display : true,
    label: 'Effective Date'
  },{Template_Field_Name__c : 'The error received in CI',

  Screen__c : 'PCPServiceFund',

  Order__c : 9,

  label : 'The error received in CI'
}]