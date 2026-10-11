package com.talenthub.talenthub.user.model;

public enum Role {
    TALENT(true, false),
    ORG_REP(true, true),
    MENTOR(false, false),
    JUDGE(false, false),
    SPONSOR_REP(true, true),
    ADMIN(false, false);

    // attributes or fields
    private final boolean selfRegistrable;
    private final boolean requiresApproval;

    // constructor
    Role(boolean selfRegistrable, boolean requiresApproval) {
        this.selfRegistrable = selfRegistrable;
        this.requiresApproval = requiresApproval;
    }

    // methods - which checks users self registration and approval
    // NOTE:this project rule in here: organization and sponsor are self registrable  and requires Approval to register 
    // and Mentors, judges and admins can’t self-register because an admin creates them
    
    public boolean isSelfRegistrable() {
        return selfRegistrable;
    }

    public boolean requiresApproval() {
        return requiresApproval;
    }
}