package repos;

import javax.inject.Named;
import javax.inject.Singleton;

import models.ReaderCate;

import org.springframework.data.repository.CrudRepository;

/**
 * Provides CRUD functionality for accessing people. Spring Data auto-magically takes care of many
 * standard operations here.
 */
@Named
@Singleton
public interface ReaderCateRepository extends CrudRepository<ReaderCate, Long> {
    Iterable<ReaderCate> findByReaderCateName(String readerCateName);
}
