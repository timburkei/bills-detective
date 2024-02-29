package expend_tracker.dto.expensesPage;

import lombok.Data;

import java.util.List;

@Data
public class ChainExpenseDto {
    private String chainName;
    private List<TagExpenseDto> tagExpenses;
}
