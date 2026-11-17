package dk.kea.library.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import dk.kea.library.dto.LoanResponse;
import dk.kea.library.dto.WorkIdRequest;
import dk.kea.library.model.Loan;
import dk.kea.library.service.LoanService;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanResponse> myLoans(HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        List<LoanResponse> result = new ArrayList<>();
        for (Loan loan : loanService.myLoans(userId)) {
            result.add(toResponse(loan));
        }
        return result;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse borrow(@RequestBody WorkIdRequest body, HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        return toResponse(loanService.borrow(userId, body.workId()));
    }

    @PostMapping("/{id}/renew")
    public LoanResponse renew(@PathVariable long id, HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        return toResponse(loanService.renew(id, userId));
    }

    @PostMapping("/{id}/return")
    public LoanResponse returnLoan(@PathVariable long id, HttpSession session) {
        long userId = SessionUser.requireUserId(session);
        return toResponse(loanService.returnLoan(id, userId));
    }

    private LoanResponse toResponse(Loan loan) {
        return LoanResponse.from(loan, loanService.feeSoFar(loan));
    }
}
