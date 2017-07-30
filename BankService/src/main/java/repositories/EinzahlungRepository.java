package repositories;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import data.Einzahlung;


public interface EinzahlungRepository extends CrudRepository<Einzahlung, Long> {

  @Query("select count(*) from Einzahlung")
  int  anzahlDerEinzahlungen();
  
}
