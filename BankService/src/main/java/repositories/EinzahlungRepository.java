package repositories;
import org.springframework.data.repository.CrudRepository;

import data.Einzahlung;


public interface EinzahlungRepository extends CrudRepository<Einzahlung, Long> {

}
