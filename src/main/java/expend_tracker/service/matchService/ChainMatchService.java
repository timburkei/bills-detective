package expend_tracker.service.matchService;

import expend_tracker.model.Chain;
import expend_tracker.repositories.ChainRepository;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service for matching input strings to chain names using similarity algorithms.
 */
@Service
public class ChainMatchService {
    private static final Logger log = LogManager.getLogger(ChainMatchService.class);
    private final ChainRepository chainRepository;
    private final LevenshteinDistance levenshteinDistance;
    private static final double SIMILARITY_THRESHOLD = 0.3;

    public ChainMatchService(ChainRepository chainRepository) {
        this.chainRepository = chainRepository;
        this.levenshteinDistance = new LevenshteinDistance();
    }

    /**
     * Matches an input string to the most similar chain name.
     *
     * @param input The input string to match.
     * @return The ID of the matched or newly created Chain.
     */
    public Long matchChain(String input) {
        try {
            log.info("Matching chain for input: {}", input);
            List<Chain> chains = StreamSupport.stream(chainRepository.findAll().spliterator(), false)
                    .collect(Collectors.toList());
            Chain bestMatch = findBestMatch(input, chains);

            if (bestMatch == null || calculateSimilarity(bestMatch.getName(), input) < SIMILARITY_THRESHOLD) {
                return createNewChain(input);
            }

            return bestMatch.getId();
        } catch (Exception e) {
            log.error("Error matching chain for input: {}", input, e);
            throw e;
        }
    }

    private Chain findBestMatch(String input, List<Chain> chains) {
        Chain bestMatch = null;
        double bestSimilarity = 0.0;

        for (Chain chain : chains) {
            double similarity = calculateSimilarity(chain.getName(), input);
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestMatch = chain;
            }
        }

        return bestMatch;
    }

    private double calculateSimilarity(String chainName, String input) {
        int distance = levenshteinDistance.apply(chainName, input);
        int maxLength = Math.max(chainName.length(), input.length());
        return 1 - (double) distance / maxLength;
    }

    private Long createNewChain(String name) {
        Chain newChain = new Chain(name);
        chainRepository.save(newChain);
        log.info("Created new chain with name: {}", name);
        return newChain.getId();
    }
}