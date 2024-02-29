package expend_tracker.service.matchService;

import org.apache.commons.text.similarity.LevenshteinDistance;
import expend_tracker.model.Product;
import expend_tracker.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Service
public class ProductMatchService {
    private static final Logger log = LogManager.getLogger(ProductMatchService.class);
    private static final double SIMILARITY_THRESHOLD = 0.38;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Finds the most similar product ID based on the given search term.
     * Uses Levenshtein Distance for fuzzy matching.
     *
     * @param searchTerm The search term to match.
     * @return The ID of the most similar product, or null if no match is found.
     */
    public Long findMostSimilarProductId(String searchTerm) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int bestMatchDistance = Integer.MAX_VALUE;
        Long mostSimilarProductId = null;
        String searchTermLowerCase = searchTerm.toLowerCase();

        try {
            log.info("Finding most similar product for term: {}", searchTermLowerCase);
            List<Product> allProducts = productRepository.findAll();
            for (Product product : allProducts) {
                String productNameLowerCase = product.getName().toLowerCase();
                int distance = levenshteinDistance.apply(productNameLowerCase, searchTermLowerCase);
                double similarity = calculateSimilarity(distance, productNameLowerCase, searchTermLowerCase);

                if (similarity > SIMILARITY_THRESHOLD && distance < bestMatchDistance) {
                    bestMatchDistance = distance;
                    mostSimilarProductId = product.getId();
                }
            }
        } catch (Exception e) {
            log.error("Error finding similar product for term: {}", searchTermLowerCase, e);
            return null;
        }

        return mostSimilarProductId;
    }

    /**
     * Calculates the similarity between two strings based on Levenshtein distance.
     *
     * @param distance    The Levenshtein distance between the two strings.
     * @param firstString The first string.
     * @param secondString The second string.
     * @return The similarity ratio.
     */
    private double calculateSimilarity(int distance, String firstString, String secondString) {
        int maxLength = Math.max(firstString.length(), secondString.length());
        return 1.0 - (double) distance / maxLength;
    }
}