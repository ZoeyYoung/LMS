package repos;

import java.util.Collection;

import models.Point;
import models.Reader;
import models.enums.PointType;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface PointRepository extends CrudRepository<Point, Long> {
    Iterable<Point> findByReaderOrderByCreateDateDesc(Reader reader);

    @Query(value = "SELECT p1.reader AS reader, p1.point as point, p2.point as totalpoint FROM "
            + "(SELECT p1.readerId AS reader, SUM(p1.point) AS point FROM Point_tab p1 GROUP BY p1.readerId) p1 LEFT JOIN "
            + "(SELECT p2.readerId AS reader, SUM(p2.point) AS point FROM Point_tab p2 WHERE p2.type IN (?1, ?2) GROUP BY p2.readerId) p2 "
            + "ON p1.reader=p2.reader", nativeQuery = true)
    Iterable<Object[]> findSumPointGroupByReader(int type, int type2);

    @Query("SELECT p.reader as reader, SUM(p.point) as point FROM Point p WHERE p.reader=?1 AND p.ptype IN (?2) GROUP BY p.reader")
    Iterable<Object[]> findSumPointGroupByReader(Reader reader, Collection<PointType> list);

    @Query("SELECT p.reader as reader, SUM(p.point) as point FROM Point p WHERE p.reader=?1 GROUP BY p.reader")
    Iterable<Object[]> findSumPointGroupByReader(Reader reader);

    Iterable<Point> findByReaderAndPtypeIn(Reader reader, Collection<PointType> list);
    
    @Query("FROM Point p ORDER BY p.createDate Desc")
    Iterable<Point> findOrderByCreateDateDesc();
}
