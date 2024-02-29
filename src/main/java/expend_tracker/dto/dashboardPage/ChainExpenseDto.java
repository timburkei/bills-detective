package expend_tracker.dto.dashboardPage;

import lombok.Data;
import lombok.Getter;

/**
 * Data Transfer Object representing chain expense details.
 */
@Data
public class ChainExpenseDto {

    /**
     * Chain ID.
     */
    private Long chainId;

    /**
     * Chain name.
     */
    private String chainName;


    /**
     * Total expense.
     */
    @Getter
    private Double totalExpense;

    /**
     * Default constructor.
     */
    public ChainExpenseDto(Long chainId, String chainName, Double totalExpense) {
        this.chainId = chainId;
        this.chainName = chainName;
        this.totalExpense = totalExpense;
    }


}
