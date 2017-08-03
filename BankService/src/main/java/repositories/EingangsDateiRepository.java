package repositories;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;

import data.EingangsDatei;


public interface EingangsDateiRepository extends CrudRepository<EingangsDatei, Long> {
    EingangsDatei findByDateiname(String dateiname);
}
