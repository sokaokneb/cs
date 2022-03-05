package hu.benkoa.cs.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventStartEndTimeRepository extends CrudRepository<EventStartEndTime, String> {
}
