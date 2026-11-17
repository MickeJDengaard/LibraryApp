package dk.kea.library.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dk.kea.library.dto.ReservationResponse;
import dk.kea.library.dto.WorkIdRequest;
import dk.kea.library.model.Reservation;
import dk.kea.library.service.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<ReservationResponse> myReservations(HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        List<ReservationResponse> result = new ArrayList<>();
        for (Reservation reservation : reservationService.myReservations(userId)) {
            result.add(ReservationResponse.from(reservation));
        }
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse reserve(@RequestBody WorkIdRequest body, HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        return ReservationResponse.from(reservationService.reserve(userId, body.workId()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable long id, HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        reservationService.cancel(id, userId);
    }
}
