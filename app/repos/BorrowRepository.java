package repos;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Named;
import javax.inject.Singleton;

import models.Borrow;
import models.Counterpart;
import models.Reader;
import models.enums.BorrowStatus;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.fasterxml.jackson.databind.JsonNode;

@Named
@Singleton
public interface BorrowRepository extends CrudRepository<Borrow, Long> {
    Iterable<Borrow> findByReader(Reader reader);

    Iterable<Borrow> findByReaderAndCounterpartOrderByBorrowDateDesc(Reader reader,
            Counterpart counterpart);

    Iterable<Borrow> findByBorrowStatusIn(Collection<BorrowStatus> asList);

    Iterable<Borrow> findByReaderAndBorrowStatusIn(Reader reader, Collection<BorrowStatus> asList);

    Iterable<Borrow> findByReaderAndCounterpartAndBorrowStatusIn(Reader reader, Counterpart cp,
            List<BorrowStatus> asList);

    Iterable<Borrow> findByCounterpartAndBorrowStatusIn(Counterpart cp, List<BorrowStatus> asList);

    // --begin 张祥 2014-08-19
    // @Query("select * from lms_db.borrows_tab bt where time = ?1")
    // Iterable<Borrow> findByTime(Date time);
    /**
     * 通过借书时间段获取用户的借阅记录
     * 
     * @param startTime 借阅记录时间段范围的起始时间位置
     * @param endTime 借阅记录时间段范围的终止时间位置
     * @return
     */
    Iterable<Borrow> findByReaderAndBorrowDateBetween(Reader reader, Date startTime, Date endTime);

    // --end

    Iterable<Borrow> findByRemindDate(Date date);

    Iterable<Borrow> findByReaderAndBorrowDateBetweenAndBorrowStatusIn(Reader reader,
            Date timeStart, Date timeEnd, List<BorrowStatus> returnedStatus);

    Iterable<Borrow> findByBorrowDateBetween(Date timeStart, Date timeEnd);

    Iterable<Borrow> findByBorrowDateBetweenAndBorrowStatusIn(Date timeStart, Date timeEnd,
            List<BorrowStatus> overdueStatus);

    @Query("SELECT b.reader as reader, COUNT(b.reader) as borrowTimes FROM Borrow b GROUP BY b.reader Order By borrowTimes Desc")
    Iterable<Object[]> findReaderOrderByBorrowBookTimes();

}
