package com.example.demo.service.Interface;

import com.example.demo.model.Invitation;
import com.example.demo.util.ResponseDataOrError;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface InvitationServiceInterface {

    ResponseEntity<ResponseDataOrError<?>> acceptInvitation(long id);
    ResponseEntity<ResponseDataOrError<?>> rejectInvitation(long id);
    ResponseEntity<ResponseDataOrError<?>> openInvitation(long id);
    ResponseEntity<ResponseDataOrError<?>> refuseAttendance(long id);
    ResponseEntity<List<Invitation>> getUserInvitations();
}
