package com.projectfkklp.saristorepos.enums;

public enum UserStatus {
    ACTIVE(
        1,
        "Active",
        "Enter",
        "Leave",
        "Leaving %s",
        "Are you sure you want to leave this store?"
    ),
    INVITED(
            2,
            "Invited",
            "Accept",
            "Decline",
            "Declining Invitation from %s",
            "Are you sure you want to to decline the invitivation?"
    ),
    REQUESTED(
            3,
            "Requested",
            null,
            "Cancel",
            "Canceling Request to Join %s",
            "Are you sure you want to cancel your request?"
    );

    public final int value;
    public final String label;

    public final String positiveAction;

    public final String negativeAction;
    public final String confirmationTitle;
    public final String confirmationMessage;

    UserStatus(
        int value,
        String label,
        String positiveAction,
        String negativeAction,
        String confirmationTitle,
        String confirmationMessage
    ) {
        this.value = value;
        this.label = label;
        this.positiveAction = positiveAction;
        this.negativeAction = negativeAction;
        this.confirmationTitle=confirmationTitle;
        this.confirmationMessage=confirmationMessage;
    }
}
