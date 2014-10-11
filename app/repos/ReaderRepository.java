package repos;

import javax.inject.Named;
import javax.inject.Singleton;

import models.Reader;
import models.ReaderCate;

import org.springframework.data.repository.CrudRepository;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many
 * standard operations here.
 */
@Named
@Singleton
public interface ReaderRepository extends CrudRepository<Reader, Long> {
    Iterable<Reader> findByReaderCode(String readerCode);

    Iterable<Reader> findByReaderCate(ReaderCate readerCate);

    Iterable<Reader> findByReaderCodeAndPassword(String readerCode, String pwd);
}
