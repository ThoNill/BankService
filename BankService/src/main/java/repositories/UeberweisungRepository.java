package repositories;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import ausgang.AusgangsDatei;
import ausgang.Ueberweisung;


public interface UeberweisungRepository extends CrudRepository<Ueberweisung, Long> {

    
    
  @Query("select count(*) from Ueberweisung")
  int  anzahlDerEinzahlungen();
  
  @Query("select u from Ueberweisung u where datei is null ")
  List<Ueberweisung> ohneDateinummer();

  @Query("update Ueberweisung set datei = null where datei = ? ")
  int dieDateinummerEntfernen(AusgangsDatei datei);

}
