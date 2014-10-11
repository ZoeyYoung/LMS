package repos;

import java.util.Date;

import javax.inject.Named;
import javax.inject.Singleton;

import models.Book;
import models.LibraryType;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Named
@Singleton
public interface BookRepository extends CrudRepository<Book, Long> {

    Iterable<Book> findByIsbn(String isbn);

    @Query("FROM Book b ORDER BY b.borrowedTimes Desc")
    Iterable<Book> findOrderByBorrowedTimesDesc();

    @Query("FROM Book b ORDER BY b.heartedTimes Desc")
    Iterable<Book> findOrderByHeartedTimesDesc();

    Iterable<Book> findByCreateDateAfter(Date date);

    Iterable<Book> findByLibraryType(LibraryType lt);

}