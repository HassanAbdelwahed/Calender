package com.example.demo.service.Interface;

import com.example.demo.model.Invitation;
import com.example.demo.util.utilInterfaces.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvitationServiceInterface {

    ResponseEntity<Response<?>> acceptInvitation(long id);
    ResponseEntity<Response<?>> rejectInvitation(long id);
    ResponseEntity<Response<?>> openInvitation(long id);
    ResponseEntity<Response<?>> refuseAttendance(long id);
    ResponseEntity<List<Invitation>> getUserInvitations();
}
