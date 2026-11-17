package dk.kea.library.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dk.kea.library.model.Loan;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    /** Active (not returned) loans of a user */
    long countByUserIdAndReturnedOnIsNull(Long userId);

    List<Loan> findByUserIdOrderByBorrowedOnDesc(Long userId);

    List<Loan> findByUserIdAndReturnedOnIsNull(Long userId);

    /** Returned loans whose fee has not been paid */
    List<Loan> findByUserIdAndReturnedOnIsNotNullAndFeePaidFalse(Long userId);

    /** Is the book currently on loan? */
    boolean existsByWorkIdAndReturnedOnIsNull(String workId);

    /** The active loan of a book, if any */
    Optional<Loan> findByWorkIdAndReturnedOnIsNull(String workId);
}
