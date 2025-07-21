package ph.edu.cspb.form137.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ph.edu.cspb.form137.model.Comment;
import java.util.List;

/**
 * Repository for {@link Comment} documents.
 */
public interface CommentRepository extends MongoRepository<Comment, String> {
    List<Comment> findByRequestIdOrderByTimestampAsc(String requestId);
}