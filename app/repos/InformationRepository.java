package repos;

import models.Information;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface InformationRepository extends CrudRepository<Information, Long> {

    @Query("FROM Information i WHERE i.cardID=?1 OR i.cardID='-1' ORDER BY i.createDate Desc")
    Iterable<Information> findByCardID(String cardID);

    @Query("FROM Information i ORDER BY i.createDate Desc")
    Iterable<Information> findAllOrderByCreateDateDesc();

}
