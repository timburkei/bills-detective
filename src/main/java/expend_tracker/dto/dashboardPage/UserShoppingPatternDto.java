package expend_tracker.dto.dashboardPage;

import lombok.Data;

@Data
public class UserShoppingPatternDto {
    private String timeGroup;
    private String weekday;
    private Long chainId;
    private String chainName;
}