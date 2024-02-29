package expend_tracker.dto.dashboardPage;

import lombok.Data;


import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Data Transfer Object representing monthly expense details.
 */
@Data
public class MonthlyExpenseDto {

        /**
         * Month.
         */
        private YearMonth month;

        /**
         * Total expense.
         */
        private BigDecimal totalAmount;


        /**
         * Default constructor.
         */
        public MonthlyExpenseDto(YearMonth month, BigDecimal totalAmount) {
                this.month = month;
                this.totalAmount = totalAmount;
        }

}
