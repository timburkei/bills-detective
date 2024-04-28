package expend_tracker.service.matchService;

import expend_tracker.model.Product;
import expend_tracker.model.Tag;
import expend_tracker.repositories.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ProductMatchServiceTest {

    @InjectMocks
    private ProductMatchService productMatchService;

    @Mock
    private ProductRepository productRepository;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindMostSimilarProductIdWithBioApfel() {
        String input = "Bio Apfel";
        Tag mockTag = new Tag("Obst");
        Product product1 = new Product("Apfel", mockTag);
        product1.setId(1L);
        Product product2 = new Product("Gurke", mockTag);
        product2.setId(2L);

        when(productRepository.findAll())
                .thenReturn(Arrays.asList(product1, product2));

        Long result = productMatchService.findMostSimilarProductId(input);

        assertEquals(1L, result);
    }
}