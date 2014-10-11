package repos;

import javax.inject.Named;
import javax.inject.Singleton;

import models.Book;
import models.Counterpart;

import org.springframework.data.repository.CrudRepository;

@Named
@Singleton
public interface CounterpartRepository extends CrudRepository<Counterpart, Long> {
    Iterable<Counterpart> findByCounterpartCode(String counterpartCode);

    Iterable<Counterpart> findByBook(Book book);
}
