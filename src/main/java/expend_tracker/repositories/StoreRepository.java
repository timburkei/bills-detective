package expend_tracker.repositories;

import expend_tracker.model.Chain;
import expend_tracker.model.Location;
import expend_tracker.model.Store;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreRepository extends CrudRepository<Store, Long> {
    Optional<Store> findByLocationAndChain(Location location, Chain chain);
}
