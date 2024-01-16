package com.projectfkklp.saristorepos.enums;

public enum UserStatus {
    ACTIVE(
        1,
        "Active",
        "Enter",
        "Leave",
        "Leaving %s",
        "Are you sure you want to leave this store?",
        1,
        "Change Role",
        "Kick",
        "Kicking %s",
        "Are you sure you want to kick %s?"
    ),
    INVITED(
        2,
        "Invited",
        "Accept",
        "Decline",
        "Declining Invitation from %s",
        "Are you sure you want to decline the invitation?",
        3,
        null,
        "Cancel",
        "Canceling Invitation to %s",
        "Are you sure you want to cancel your invitation to %s?"
    ),
    REQUESTED(
        3,
        "Requested",
        null,
        "Cancel",
        "Canceling Request to Join %s",
        "Are you sure you want to cancel your request?",
        2,
        "Accept",
        "Decline",
        "Declining Request from %s",
        "Are you sure you want to decline request from %s?"
    );

    public final int value;
    public final String label;

    public final String positiveAction;

    public final String negativeAction;
    public final String confirmationTitle;
    public final String confirmationMessage;
    public final int orderInStoreProfile;
    public final String storePositiveAction;
    public final String storeNegativeAction;
    public final String storeConfirmationTitle;
    public final String storeConfirmationMessage;

    UserStatus(
        int value,
        String label,
        String positiveAction,
        String negativeAction,
        String confirmationTitle,
        String confirmationMessage,
        int orderInStoreProfile,
        String storePositiveAction,
        String storeNegativeAction,
        String storeConfirmationTitle,
        String storeConfirmationMessage
    ) {
        this.value = value;
        this.label = label;
        this.positiveAction = positiveAction;
        this.negativeAction = negativeAction;
        this.confirmationTitle=confirmationTitle;
        this.confirmationMessage=confirmationMessage;
        this.orderInStoreProfile = orderInStoreProfile;
        this.storePositiveAction = storePositiveAction;
        this.storeNegativeAction = storeNegativeAction;
        this.storeConfirmationTitle = storeConfirmationTitle;
        this.storeConfirmationMessage = storeConfirmationMessage;
    }
}
