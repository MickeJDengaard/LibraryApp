package dk.kea.library.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Open Library work ID, e.g. OL45883W */
    @Column(nullable = false, length = 20)
    private String workId;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false)
    private LocalDate borrowedOn;

    @Column(nullable = false)
    private LocalDate dueOn;

    @Column(nullable = false)
    private int renewCount = 0;

    /** null while the loan is active */
    private LocalDate returnedOn;

    @Column(nullable = false)
    private int feeKr = 0;

    @Column(nullable = false)
    private boolean feePaid = false;

    public Loan() {
        // for JPA
    }

    public Loan(User user, String workId, String title, LocalDate borrowedOn, LocalDate dueOn) {
        this.user = user;
        this.workId = workId;
        this.title = title;
        this.borrowedOn = borrowedOn;
        this.dueOn = dueOn;
    }

    public boolean isActive() {
        return returnedOn == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public String getWorkId() {
        return workId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getBorrowedOn() {
        return borrowedOn;
    }

    public LocalDate getDueOn() {
        return dueOn;
    }

    public void setDueOn(LocalDate dueOn) {
        this.dueOn = dueOn;
    }

    public int getRenewCount() {
        return renewCount;
    }

    public void setRenewCount(int renewCount) {
        this.renewCount = renewCount;
    }

    public LocalDate getReturnedOn() {
        return returnedOn;
    }

    public void setReturnedOn(LocalDate returnedOn) {
        this.returnedOn = returnedOn;
    }

    public int getFeeKr() {
        return feeKr;
    }

    public void setFeeKr(int feeKr) {
        this.feeKr = feeKr;
    }

    public boolean isFeePaid() {
        return feePaid;
    }

    public void setFeePaid(boolean feePaid) {
        this.feePaid = feePaid;
    }
}
