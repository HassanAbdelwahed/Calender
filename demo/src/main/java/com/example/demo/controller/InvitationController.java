package com.example.demo.controller;


import com.example.demo.model.Invitation;
import com.example.demo.service.Interface.InvitationServiceInterface;
import com.example.demo.util.utilInterfaces.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/invitations")
public class InvitationController {

    @Autowired
    private InvitationServiceInterface invitationService;

    @PatchMapping("/accept/{id}")
    public ResponseEntity<Response<?>> acceptInvitation(@PathVariable("id") long id) {
        return invitationService.acceptInvitation(id);
    }

    @PatchMapping("/reject/{id}")
    public ResponseEntity<Response<?>> rejectInvitation(@PathVariable("id") long id) {
        return invitationService.rejectInvitation(id);
    }

    @PatchMapping("/open/{id}")
    public ResponseEntity<Response<?>> openInvitation(@PathVariable("id") long id) {
        return invitationService.openInvitation(id);
    }

    @PatchMapping("/refuse/{id}")
    public ResponseEntity<Response<?>> refuseAttendance(@PathVariable("id") long id) {
        return invitationService.refuseAttendance(id);
    }

    @GetMapping
    public ResponseEntity<List<Invitation>> getUserInvitations() {
        return invitationService.getUserInvitations();
    }
}
