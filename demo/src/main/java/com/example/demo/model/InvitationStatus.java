package com.example.demo.model;

public enum InvitationStatus {
    ACCEPTED,   // invitation Accepted by the invitee
    REJECTED,   // invitation rejected by the invitee
    AVAILABLE,  // can be accepted
    CONFLICTED, // Not allowed to accept or Reject because there is conflict with other appointment
    TIMEOVER,   // Not allowed to accept or Reject because time of meeting is over
    CANCELED,    // meeting canceled by the owner so Invitation is canceled also.
    REFUSED,     // before meeting start, user can refuse to attend ==> just after ACCEPTED STATUS
}
