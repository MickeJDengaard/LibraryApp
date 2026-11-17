package dk.kea.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

/**
 * BR-08: late fee.
 * daysLate = date - due. 0 kr if daysLate <= 3, otherwise 5 kr per day after
 * the first 3, capped at 200 kr.
 */
@Component
public class FeeCalculator {

    public static final int FREE_DAYS = 3;
    public static final int KR_PER_DAY = 5;
    public static final int MAX_FEE_KR = 200;

    public int feeFor(LocalDate due, LocalDate date) {
        long daysLate = ChronoUnit.DAYS.between(due, date);
        if (daysLate <= FREE_DAYS) {
            return 0;
        }
        long fee = KR_PER_DAY * (daysLate - FREE_DAYS);
        return (int) Math.min(fee, MAX_FEE_KR);
    }
}
