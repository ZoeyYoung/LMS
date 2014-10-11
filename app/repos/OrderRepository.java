package repos;

import java.util.Collection;

import javax.inject.Named;
import javax.inject.Singleton;

import models.Order;
import models.Reader;
import models.enums.OrderStatus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Named
@Singleton
public interface OrderRepository extends CrudRepository<Order, Long> {
    Iterable<Order> findByReader(Reader reader);

    Iterable<Order> findByIsbn(String isbn);

    Iterable<Order> findByStatusIn(Collection<OrderStatus> unhandledStatus);
    
    @Query("SELECT o.isbn as isbn, o.bookname as bookname, o.status as status, COUNT(o.isbn) as count FROM Order o GROUP BY o.isbn")
    Iterable<Object[]> findCountOrderGroupByISBN();
}
