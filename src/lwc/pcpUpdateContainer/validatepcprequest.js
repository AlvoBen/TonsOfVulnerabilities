export class ValidateEligibilityRequest_DTO_HUM {
    constructor(platform, stateofissue, groupid) {
        this.timestamp = getTimeStamp();
        this.consumer = 'CRM';
        this.requestid = crypto.randomUUID();
        this.members = new Members_DTO_HUM(platform, stateofissue, groupid);
    }
}

const getTimeStamp = () => {
    return new Date().toISOString().replaceAll('T', '-').replaceAll('Z', new Date().getMilliseconds()).replaceAll(':', '.').padEnd(26, '0');
}

class Members_DTO_HUM {
    constructor(platform, stateofissue, groupid) {
        let tmp = [];
        tmp.push(new Member_DTO_HUM(platform, stateofissue, groupid));
        return tmp;
    }
}


class Member_DTO_HUM {
    constructor(platform, stateofissue, groupid) {
        this.membercriterion = new MemberCriterion_DTO_HUM(platform, stateofissue, groupid);
    }
}

class MemberCriterion_DTO_HUM {
    constructor(platform, stateofissue, groupid) {
        let tmp = [];
        tmp.push(new MemberCriteria_DTO_HUM(platform, stateofissue, groupid));
        return tmp;
    }
}

class MemberCriteria_DTO_HUM {
    constructor(platform, stateofissue, groupid) {
        this.platform = platform;
        this.demographicType = 'PCP';
        this.pcprequired = 'true';
        this.stateofissue = stateofissue;
        this.groupid = groupid;
    }
}