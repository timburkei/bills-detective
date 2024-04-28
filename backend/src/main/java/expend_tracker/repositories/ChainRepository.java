package expend_tracker.repositories;

import expend_tracker.model.Chain;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChainRepository extends CrudRepository<Chain, Long> {
}
