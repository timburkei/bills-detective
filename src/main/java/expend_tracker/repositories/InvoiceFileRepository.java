package expend_tracker.repositories;

import expend_tracker.model.InvoiceFile;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceFileRepository extends CrudRepository<InvoiceFile, Long> {

}
