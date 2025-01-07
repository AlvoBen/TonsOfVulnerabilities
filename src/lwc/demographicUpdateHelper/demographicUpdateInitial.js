import { MemberCriterion, SSN } from './demographicUpdateModel';

export function initialize() {
    // this.errorTrace.stack = 'Method: initialize, file: demographicUpdateInitial.js';
    generateUpdateModel.apply(this);
    generateAVModel.apply(this);
    // generateMedicareAVFModel.apply(this);
    generateUpdateRequestModel.apply(this);
    generateMembers.apply(this);
}

export function generateUpdateModel() {
    this.updatedDataModel = this.updateDataModelClass.generateUpdateDataModel.apply(this);
}

export function generateAVModel() {
    this.addressVerificationModel = this.addressVerificationModeClass.generateAddressVerificationModel.apply(this);
}

// export function generateMedicareAVFModel() {
//     this.medicareAVFModel = this.medicareAVFModelClass.generateModel.apply(this);
// }

export function generateUpdateRequestModel() {
    this.updateRequestModel = this.updateRequestModelClass.generateRequestModel.apply(this);
}

export function generateMembers() {
    this.member = this.membersClass.generateMembers.apply(this);
    let memberCriterionClass = new MemberCriterion();
    let memberCriterionObj = memberCriterionClass.generateCriteria.apply(this);
    let ssnClass = new SSN();
    let ssnObj = ssnClass.generateSSNModel.apply(this);
    this.member.criticalbiographics.ssn = ssnObj;
    this.member.membercriterion.push(memberCriterionObj);
    this.updateRequestModel.UpdateMemberRequest.members.push(this.member);
}