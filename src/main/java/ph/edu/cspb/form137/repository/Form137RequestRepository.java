package ph.edu.cspb.form137.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ph.edu.cspb.form137.model.Form137Request;
import java.util.Optional;

/**
 * Repository for {@link Form137Request} documents.
 */
public interface Form137RequestRepository extends MongoRepository<Form137Request, String> {
    Optional<Form137Request> findByTicketNumber(String ticketNumber);
}
