package repos;

import javax.inject.Named;
import javax.inject.Singleton;

import models.Book;
import models.BookReview;
import models.Reader;

import org.springframework.data.repository.CrudRepository;

@Named
@Singleton
public interface BookReviewRepository extends CrudRepository<BookReview, Long> {

    Iterable<BookReview> findByReader(Reader reader);

    Iterable<BookReview> findByBook(Book book);
    
    /**
     * 查询数据库，寻找是否存在reader对book的评论
     * @param book
     * @param reader
     */
    Iterable<BookReview> findByBookAndReader(Book book,Reader reader);
}