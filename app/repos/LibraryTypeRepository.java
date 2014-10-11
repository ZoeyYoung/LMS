package repos;

import javax.inject.Named;
import javax.inject.Singleton;

import models.LibraryType;

import org.springframework.data.repository.CrudRepository;

@Named
@Singleton
public interface LibraryTypeRepository extends CrudRepository<LibraryType, Long> {

    Iterable<LibraryType> findByLibraryType(String name);

}
