package expend_tracker.service.matchService;

import expend_tracker.model.Chain;
import expend_tracker.repositories.ChainRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ChainMatchServiceTest {

    @InjectMocks
    private ChainMatchService chainMatchService;

    @Mock
    private ChainRepository chainRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testMatchChain() {
        String input = "Edeka Rheinhard";
        Chain chain1 = new Chain("Edeka");
        chain1.setId(1L);
        Chain chain2 = new Chain("Rewe");
        chain2.setId(2L);

        when(chainRepository.findAll())
                .thenReturn(Arrays.asList(chain1, chain2));

        Long result = chainMatchService.matchChain(input);

        assertEquals(1L, result);
    }
}