package expend_tracker.repositories;

import expend_tracker.model.Location;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface LocationRepository extends CrudRepository<Location, Long> {
    Optional<Location> findByStreetAndNumberAndZipAndCity(String street, String number, Integer zip, String city);

}
