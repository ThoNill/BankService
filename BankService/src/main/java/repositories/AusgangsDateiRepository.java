package repositories;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ausgang.AusgangsDatei;


public interface AusgangsDateiRepository extends CrudRepository<AusgangsDatei, Long> {

}
