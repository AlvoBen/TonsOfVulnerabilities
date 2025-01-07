export class MTMRequest {
    constructor(genKey) {
        this.Identifier = 1;
        this.Value = genKey;
        this.MembershipType = 'ALL';
    }
}