package services;

import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Book;
import models.BookReview;
import models.Borrow;
import models.Counterpart;
import models.LibraryType;
import models.Order;
import models.Reader;
import models.enums.BorrowStatus;
import models.enums.CounterpartStatus;
import models.enums.OrderStatus;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

import repos.BookRepository;
import repos.BookReviewRepository;
import repos.BorrowRepository;
import repos.CounterpartRepository;
import repos.LibraryTypeRepository;
import repos.OrderRepository;
import utils.CommonUtils;

@Named
@Singleton
@Service(value = "bookService")
public class BookService {
    @Inject
    private OrderRepository orderRepository;
    @Inject
    private BookRepository bookRepository;
    @Inject
    private BorrowRepository borrowRepository;
    @Inject
    private CounterpartRepository counterpartRepository;
    @Inject
    private LibraryTypeRepository libraryTypeRepository;
    @Inject
    private BookReviewRepository bookReviewRepository;

    /**
     * 书刊入库
     *
     * @param book
     * @return
     */
    public Book newBook(Book book) {
        Iterator<Book> bIter = bookRepository.findByIsbn(book.isbn).iterator();
        while (bIter.hasNext()) {
            return bIter.next();
        }
        return bookRepository.save(book);
    }

    public Book updateBook(Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        bookRepository.delete(id);
    }

    public Iterable<Book> allBooks() {
        return bookRepository.findAll();
    }

    public Book findByBookId(Long id) {
        return bookRepository.findOne(id);
    }

    public Book findByIsbn(String isbn) {
        Iterator<Book> books = bookRepository.findByIsbn(isbn).iterator();
        while (books.hasNext()) {
            return books.next();
        }
        return null;
    }

    /**
     * 书刊复本
     *
     * @param book
     * @param counterpartCode
     * @return
     */
    public Counterpart newCounterpart(Book book, String counterpartCode) {
        book = newBook(book);
        Iterator<Counterpart> cIter = counterpartRepository.findByCounterpartCode(counterpartCode)
                .iterator();
        while (cIter.hasNext()) {
            return cIter.next();
        }
        Counterpart cp = new Counterpart();
        cp.book = book;
        cp.counterpartCode = counterpartCode;
        cp.createDate = new Date();
        return counterpartRepository.save(cp);
    }

    public Counterpart findByCounterpartCode(String counterpartCode) {
        Iterator<Counterpart> cIter = counterpartRepository.findByCounterpartCode(counterpartCode)
                .iterator();
        while (cIter.hasNext()) {
            return cIter.next();
        }
        return null;
    }

    public List<Counterpart> findCounterpartsByBook(Book book) {
        return CommonUtils.toList(counterpartRepository.findByBook(book));
    }

    /**
     *
     * @param reader
     * @param counterpart
     * @return
     */
    public Borrow newBorrow(Reader reader, Counterpart counterpart) {
        Borrow borrow = new Borrow();
        borrow.counterpart = counterpart;
        borrow.reader = reader;
        borrow.borrowDate = new Date();
        borrow.returnDate = CommonUtils.afterDays(borrow.borrowDate, reader.readerCate.limitDays - 1);
        borrow.remindDate = CommonUtils.afterDays(borrow.returnDate, -4);
        // 将复本状态设置为借出
        borrow.counterpart.status = CounterpartStatus.ONLOAD;
        counterpart.status = CounterpartStatus.ONLOAD;
        counterpartRepository.save(counterpart);
        Book book = counterpart.book;
        book.borrowedTimes += 1;
        bookRepository.save(book);
        return borrowRepository.save(borrow);
    }

    public Borrow updateBorrowRemindDate(Borrow borrow) {
        // 对于逾期的书更新借阅状态为已逾期
        if (CommonUtils.getDateInstance(borrow.remindDate).equals(
                CommonUtils.getDateInstance(borrow.returnDate))) {
            borrow.remindDate = CommonUtils.afterDays(borrow.remindDate, 1);
        } else if (borrow.remindDate.after(borrow.returnDate)) {
            borrow.borrowStatus = BorrowStatus.OVERDUE;
            borrow.remindDate = CommonUtils.afterDays(borrow.remindDate, 5);
        } else {
            borrow.remindDate = CommonUtils.afterDays(borrow.remindDate, 4);
        }
        return borrowRepository.save(borrow);
    }

    public Borrow updateBorrowStatus(Reader reader, Counterpart counterpart, BorrowStatus bs) {
        Borrow borrow = findByReaderAndCounterpart(reader, counterpart);
        borrow.borrowStatus = bs;
        // 如果是续借
        if (BorrowStatus.RENEW.equals(bs) && borrow.renewTimes < reader.readerCate.reLoanTimes) {
            borrow.returnDate = CommonUtils.afterDays(borrow.returnDate,
                    reader.readerCate.reLoanDays);
            borrow.renewTimes += 1;
            borrow.remindDate = CommonUtils.afterDays(borrow.returnDate, -5);
        } else if (BorrowStatus.RETURNED.equals(bs)) {
            // 将复本状态设置为已归还
            borrow.counterpart.status = CounterpartStatus.OFFLOAD;
            counterpart.status = CounterpartStatus.OFFLOAD;
            borrow.remindDate = CommonUtils.afterDays(borrow.borrowDate, -1);
            counterpartRepository.save(counterpart);
        }
        return borrowRepository.save(borrow);
    }

    /**
     * 通过借阅者获取所有的借阅记录
     *
     * @param reader
     * @return
     */
    public Iterator<Borrow> findByReader(Reader reader) {
        return borrowRepository.findByReader(reader).iterator();
    }

    /**
     * 保存用户的书评
     *
     * @param br
     */
    public void updateOrSaveBookReview(BookReview br) {
        bookReviewRepository.save(br);
    }

    /**
     * 查询数据库，寻找是否存在reader对book的评论
     *
     * @param book
     * @param reader
     * @return
     */
    public BookReview findByBookAndReader(Book book, Reader reader) {
        Iterator<BookReview> iter = bookReviewRepository.findByBookAndReader(book, reader)
                .iterator();
        while (iter.hasNext()) {
            return iter.next();
        }
        return null;
    }

    public Iterable<BookReview> findBookReviewsByBook(Book book) {
        return bookReviewRepository.findByBook(book);
    }

    public Borrow findByReaderAndCounterpart(Reader reader, Counterpart counterpart) {
        Iterator<Borrow> bIter = borrowRepository.findByReaderAndCounterpartOrderByBorrowDateDesc(
                reader, counterpart).iterator();
        while (bIter.hasNext()) {
            return bIter.next();
        }
        return null;
    }

    public Iterator<Borrow> allBorrows() {
        return borrowRepository.findAll().iterator();
    }

    private Iterator<Borrow> findByBorrowStatusIn(Collection<BorrowStatus> asList) {
        return borrowRepository.findByBorrowStatusIn(asList).iterator();
    }

    public Iterator<Borrow> findOverduesBorrows() {
        return findByBorrowStatusIn(BorrowStatus.overdueStatus());
    }

    public Iterator<Borrow> findBorrowsByRemindDate(Date date) {
        return borrowRepository.findByRemindDate(date).iterator();
    }

    private Iterator<Borrow> findByReaderAndBorrowStatusIn(Reader reader,
            Collection<BorrowStatus> asList) {
        return borrowRepository.findByReaderAndBorrowStatusIn(reader, asList).iterator();
    }

    public Iterator<Borrow> findReaderCurrentBorrows(Reader reader) {
        return findByReaderAndBorrowStatusIn(reader, BorrowStatus.unreturnedStatus());
    }

    private Borrow findByReaderAndCounterpartAndBorrowStatusIn(Reader reader, Counterpart cp,
            List<BorrowStatus> asList) {
        Iterator<Borrow> bIter = borrowRepository.findByReaderAndCounterpartAndBorrowStatusIn(
                reader, cp, asList).iterator();
        while (bIter.hasNext()) {
            return bIter.next();
        }
        return null;
    }

    public Borrow findReaderCurrentBorrow(Reader reader, Counterpart cp) {
        return findByReaderAndCounterpartAndBorrowStatusIn(reader, cp,
                BorrowStatus.unreturnedStatus());
    }

    public Iterator<Borrow> findReaderReturedBorrows(Reader reader) {
        return findByReaderAndBorrowStatusIn(reader, BorrowStatus.returnedStatus());
    }

    /**
     * 用户通过条件查询
     *
     * @param reader
     * @param timeStart
     * @param timeEnd
     * @return
     * @throws ParseException
     */
    public List<Borrow> borrowsBetween(Reader reader, Date timeStart, Date timeEnd)
            throws ParseException {
        // 日期类型的做法
        return CommonUtils.toList(borrowRepository
                .findByReaderAndBorrowDateBetweenAndBorrowStatusIn(reader, timeStart, timeEnd,
                        BorrowStatus.returnedStatus()));
    }

    public List<Borrow> borrowsBetween(Date timeStart, Date timeEnd) throws ParseException {
        // 日期类型的做法
        return CommonUtils.toList(borrowRepository.findByBorrowDateBetween(timeStart, timeEnd));
    }

    public List<Borrow> overdueBorrowsBetween(Date timeStart, Date timeEnd) throws ParseException {
        // 日期类型的做法
        return CommonUtils.toList(borrowRepository.findByBorrowDateBetweenAndBorrowStatusIn(
                timeStart, timeEnd, BorrowStatus.overdueStatus()));
    }

    private Borrow findByCounterpartAndBorrowStatusIn(Counterpart cp, List<BorrowStatus> asList) {
        Iterator<Borrow> bIter = borrowRepository.findByCounterpartAndBorrowStatusIn(cp, asList)
                .iterator();
        while (bIter.hasNext()) {
            return bIter.next();
        }
        return null;
    }

    public Borrow findCounterpartCurrentBorrow(Counterpart cp) {
        return findByCounterpartAndBorrowStatusIn(cp, BorrowStatus.unreturnedStatus());
    }

    public Iterable<BookReview> findBookReviewByReader(Reader reader) {
        return bookReviewRepository.findByReader(reader);
    }

    public void setOrderStatus(String isbn, OrderStatus orderStatus) {
        Iterator<Order> retrievedOrder = orderRepository.findByIsbn(isbn).iterator();
        while (retrievedOrder.hasNext()) {
            Order order = retrievedOrder.next();
            order.status = orderStatus;
            orderRepository.save(order);
        }
    }

    public Iterable<Book> findBooksOrderByBorrowedTimes() {
        return bookRepository.findOrderByBorrowedTimesDesc();
    }

    public Iterable<Book> findBooksOrderByHeartedTimes() {
        return bookRepository.findOrderByHeartedTimesDesc();
    }

    public Iterator<Book> findBookByCreateDateAfterLoginDate(Date lastLoginDate) {
        if (lastLoginDate == null) {
            return bookRepository.findByCreateDateAfter(new Date()).iterator();
        }
        return bookRepository.findByCreateDateAfter(CommonUtils.afterDays(lastLoginDate, -5))
                .iterator();
    }

    public boolean deleteLibraryType(Long id) {
        LibraryType library = libraryTypeRepository.findOne(id);
        if (library == null) {
            return false;
        }
        Iterator<Book> books = bookRepository.findByLibraryType(library).iterator();
        while (books.hasNext()) {
            return false;
        }
        libraryTypeRepository.delete(id);
        return true;
    }

    public Iterator<Object[]> findReadersOrderByBorrowBookTimes() {
        return borrowRepository.findReaderOrderByBorrowBookTimes().iterator();
    }

}
