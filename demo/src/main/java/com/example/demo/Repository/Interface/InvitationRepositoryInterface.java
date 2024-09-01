package com.example.demo.Repository.Interface;

import com.example.demo.model.Invitation;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InvitationRepositoryInterface extends JpaRepository<Invitation, Long> {

    @Query("SELECT i FROM Invitation i " +
            "WHERE i.user = :user " +
            "AND i.seen = false" +
            "AND i.status != ACCEPTED " +
            "AND i.status != REJECTED " +
            "AND i.status != REFUSED")
    List<Invitation> findUserInvitations(@Param("user") User user);
}
