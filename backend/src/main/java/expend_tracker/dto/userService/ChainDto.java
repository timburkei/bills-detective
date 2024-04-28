package expend_tracker.dto.userService;

import lombok.Data;

@Data
public class ChainDto {
    private Long id;
    private String name;

    public ChainDto() {

    }

    public ChainDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}