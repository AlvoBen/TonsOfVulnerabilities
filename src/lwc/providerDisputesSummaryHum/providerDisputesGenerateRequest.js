export class ProviderDisputesRequestDTO {
    constructor(externalId){
        let todayDate = new Date();
        let fromDate = new Date();
        fromDate.setDate(todayDate.getDate() - 548);
        this.thruDate = todayDate.toISOString().substring(0,19);
        this.member = new MemberDTO(externalId);
        this.fromDate = fromDate.toISOString().substring(0,19);
    }
}

export class MemberDTO {
    constructor(externalId){
        this.externalId = externalId;
    }
}