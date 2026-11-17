package dk.kea.library.dto;

import java.time.LocalDate;

import dk.kea.library.model.Loan;

/** feeSoFar is the final fee for a returned loan, else the fee if returned today. */
public record LoanResponse(Long id,
                           String workId,
                           String title,
                           LocalDate borrowedOn,
                           LocalDate dueOn,
                           int renewCount,
                           LocalDate returnedOn,
                           int feeSoFar,
                           boolean active) {

    public static LoanResponse from(Loan loan, int feeSoFar) {
        return new LoanResponse(loan.getId(), loan.getWorkId(), loan.getTitle(), loan.getBorrowedOn(),
                loan.getDueOn(), loan.getRenewCount(), loan.getReturnedOn(), feeSoFar, loan.isActive());
    }
}
