export class UpdatePCP_DTO_HUM {
    constructor(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue, personId, subPersonId, platform, groupId, benefitId) {
        this.timestamp = getTimeStamp();
        this.consumer = 'CRM';
        this.requestid = crypto.randomUUID();
        this.members = new Members_DTO(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue, personId, subPersonId, platform, groupId, benefitId);
    }
}

const getTimeStamp = () => {
    return new Date().toISOString().replaceAll('T', '-').replaceAll('Z', new Date().getMilliseconds()).replaceAll(':', '.').padEnd(26, '0');
}

class Members_DTO {
    constructor(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue, personId, subPersonId, platform, groupId, benefitId) {
        let tmp = [];
        tmp.push(new Member_DTO(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue, personId, subPersonId, platform, groupId, benefitId));
        return tmp;
    }
}

class Member_DTO {
    constructor(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue, personId, subPersonId, platform, groupId, benefitId) {
        this.demotype = 'Z';
        this.ownindicator = 'Y';
        this.policies = new Policies_DTO(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue);
        this.membercriterion = new MemberCriterion_DTO(personId, subPersonId, platform, groupId, benefitId);
    }
}

class MemberCriterion_DTO {
    constructor(personId, subPersonId, platform, groupId, benefitId) {
        let tmp = [];
        tmp.push(new MemberCriteria_DTO(personId, subPersonId, platform, groupId, benefitId));
        return tmp;
    }
}


class MemberCriteria_DTO {
    constructor(personId, subPersonId, platform, groupId, benefitId) {
        this.updaterequesttype = new Update_PCP_DTO();
        this.membersourcepersonid = personId;
        this.subscribersourcepersonid = subPersonId;
        this.platform = platform;
        this.groupid = groupId;
        this.benefitid = benefitId;
    }
}

class Update_PCP_DTO {
    constructor() {
        let tmp = [];
        tmp.push('PCP');
        return tmp;
    }
}

class Policies_DTO {
    constructor(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue) {
        let tmp = [];
        tmp.push(new Policy_DTO(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue));
        return tmp;
    }
}


class Policy_DTO {
    constructor(effectiveDate, pcpId, pcpName, memberDob, stateOfIssue) {
        this.effectivedate = effectiveDate;
        this.primarycarephysicians = new PrimaryCarePhysicians_DTO(pcpId, pcpName, memberDob, effectiveDate);
        this.grouppolicy = new GroupPolicy_DTO(stateOfIssue);
    }
}


class GroupPolicy_DTO {
    constructor(stateOfIssue) {
        this.stateofissue = stateOfIssue;
    }
}

class PrimaryCarePhysicians_DTO {
    constructor(pcpId, pcpName, memberDob, effectiveDate) {
        let tmp = [];
        tmp.push(new PrimaryCarePhysician_DTO(pcpId, pcpName, memberDob, effectiveDate));
        return tmp;
    }
}

class PrimaryCarePhysician_DTO {
    constructor(pcpId, pcpName, memberDob, effectiveDate) {
        this.changeindicator = 'Y';
        this.effectivedate = effectiveDate;
        this.id = pcpId;
        this.name = pcpName;
        this.memberdob = memberDob;
    }
}