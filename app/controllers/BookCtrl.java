/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangChao 2014.8.11.
*/

package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Book;
import models.BookReview;
import models.Counterpart;
import models.LibraryType;
import models.Reader;
import models.enums.OrderStatus;

import org.json.JSONException;
import org.json.JSONObject;

import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import repos.LibraryTypeRepository;
import services.AdminService;
import services.BookService;
import services.ReaderService;
import utils.CommonUtils;
import views.form.BookForm;
import views.table.BookTable;
import views.table.CounterpartTable;

import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * BookCtrl 主要作用： 响应书刊管理请求，对页面发出的书刊信息作业务处理，并跳转到相应页面
 *   1.管理员和读者都可以获取所有书刊信息
 *   2.管理员添加书刊或书刊副本，判断书刊是否存在
 *   3.管理员通过书刊ISNB号获取书刊信息
 *   4.响应管理员和读者首页的Top10被借书和被赞最多的书
 *   5.实现书刊录入页面显示所有馆藏类型
 * @return
 */

@Named
@Singleton
public class BookCtrl extends Controller {
    private final BookService bookService;
    private final LibraryTypeRepository libraryTypeRepository;
    private final AdminService adminService;
    private final ReaderService readerService;

    @Inject
    public BookCtrl(final LibraryTypeRepository libraryTypeRepository,
            final BookService bookService, final AdminService adminService,
            final ReaderService readerService) {
        this.bookService = bookService;
        this.libraryTypeRepository = libraryTypeRepository;
        this.adminService = adminService;
        this.readerService = readerService;
    }

    /**
     * 图书入库时通过ISBN号获取数据库中的图书，先做判断在联网获取数据
     *
     * @return JSON 数据
     */
    public Result isbnService(String isbn) {
        JSONObject jsonObj;
        try {
            jsonObj = CommonUtils.isbnService(isbn);
            return ok(jsonObj.toString());
        } catch (JSONException e) {
            Logger.error(e.getClass() + " " + e.getMessage());
        }
        return internalServerError("Oops");
    }

    /**
     * 通过ISBN判断书刊是否存在
     *
     * @return
     */
    public Result isBookExist(String isbn) {
        ObjectNode result = Json.newObject();
        Book book = bookService.findByIsbn(isbn);
        if (book == null) {
            result.put("isBookExist", false);
        } else {
            result.put("isBookExist", true);
        }
        return ok(result);
    }

    /**
     * 跳转到图书入库界面，并获取馆藏类型添加成下拉单选项
     *
     * @return
     */
    public Result bookForm() {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
        return ok(views.html.admin.bookForm.render(CommonUtils.toList(libraryTypes),
                Form.form(BookForm.class)));
    }

    public Result currrentCounterpart(String isbn) {
        ObjectNode result = Json.newObject();
        Book book = bookService.findByIsbn(isbn);
        if (book != null) {
            List<Counterpart> counterparts = bookService.findCounterpartsByBook(book);
            List<CounterpartTable> cTbl = new ArrayList<CounterpartTable>();
            for (Counterpart counterpart : counterparts) {
                cTbl.add(CounterpartTable.makeInstantce(counterpart));
            }
            result.put("counterparts", Json.toJson(cTbl));
            result.put("book", Json.toJson(book));
        }
        return ok(result);
    }

    /**
     * admin添加新书
     *
     * @return
     */
    public Result newBook() {
        Form<BookForm> filledForm = Form.form(BookForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
            return badRequest(views.html.admin.bookForm.render(CommonUtils.toList(libraryTypes),
                    filledForm));
        } else {
            LibraryType lt = libraryTypeRepository.findOne(filledForm.get().libraryType);
            Book book = Book.makeInstance(filledForm.get(), lt);
            Book rBook = bookService.findByIsbn(book.isbn);
            if (rBook == null) {
                rBook = bookService.newBook(book);
                bookService.setOrderStatus(rBook.isbn, OrderStatus.BUYED);
            } else {
                book.id = rBook.id;
                bookService.updateBook(book);
            }
            List<Counterpart> counterparts = bookService.findCounterpartsByBook(rBook);
            int cSize = counterparts.size();
            cSize++;
            // 设置图书入库ID
            String counterpartCode = cSize < 10 ? rBook.isbn + "00" + cSize : rBook.isbn + "0" + cSize;
            Counterpart newcounterpart = bookService.newCounterpart(rBook, counterpartCode);
            counterparts.add(newcounterpart);
            List<CounterpartTable> bookTbl = new ArrayList<CounterpartTable>();
            for (Counterpart counterpart : counterparts) {
                bookTbl.add(CounterpartTable.makeInstantce(counterpart));
            }
            return ok(Json.toJson(bookTbl));
        }
    }

    /**
     * admin修改书刊
     *
     * @return
     */
    public Result updateBook() {
        Form<BookForm> filledForm = Form.form(BookForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
            return badRequest(views.html.admin.bookForm.render(CommonUtils.toList(libraryTypes),
                    filledForm));
        } else {
            LibraryType lt = libraryTypeRepository.findOne(filledForm.get().libraryType);
            Book book = Book.makeInstance(filledForm.get(), lt);
            Book rBook = bookService.findByIsbn(book.isbn);
            if (rBook == null) {
                rBook = bookService.newBook(book);
                bookService.setOrderStatus(rBook.isbn, OrderStatus.BUYED);
            } else {
                book.id = rBook.id;
                book = bookService.updateBook(book);
            }
            List<Counterpart> counterparts = bookService.findCounterpartsByBook(rBook);
            List<CounterpartTable> bookTbl = new ArrayList<CounterpartTable>();
            for (Counterpart counterpart : counterparts) {
                bookTbl.add(CounterpartTable.makeInstantce(counterpart));
            }
            return ok(Json.toJson(bookTbl));
        }
    }

    /**
     * admin获取所有书刊
     *
     * @return
     */
    public Result aBooks() {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Iterator<Book> retrievedBooks = bookService.allBooks().iterator();
        List<BookTable> btbl = new ArrayList<BookTable>();
        while (retrievedBooks.hasNext()) {
            btbl.add(BookTable.makeInstance(retrievedBooks.next()));
        }
        Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
        return ok(views.html.admin.allBooks.render(CommonUtils.toList(libraryTypes), btbl, ""));
    }

    /**
     * reader获取所有书刊
     *
     * @return
     */
    public Result rBooks() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Iterator<Book> retrievedBooks = bookService.allBooks().iterator();
        List<BookTable> btbl = new ArrayList<BookTable>();
        while (retrievedBooks.hasNext()) {
            btbl.add(BookTable.makeInstance(retrievedBooks.next()));
        }
        Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
        return ok(views.html.reader.allBooks.render(CommonUtils.toList(libraryTypes), btbl, ""));
    }

    /**
     * reader通过ISBN获取书刊
     *
     * @return
     */
    public Result rbook(String isbn) {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Book book = bookService.findByIsbn(isbn);
        Iterable<BookReview> brs = bookService.findBookReviewsByBook(book);
        List<Counterpart> counterparts = bookService.findCounterpartsByBook(book);
        List<CounterpartTable> cTbl = new ArrayList<CounterpartTable>();
        for (Counterpart counterpart : counterparts) {
            cTbl.add(CounterpartTable.makeInstantce(counterpart));
        }
        return ok(views.html.reader.book.render(book, CommonUtils.toList(brs), cTbl));
    }

    /**
     * admin通过ISBN获取书刊
     *
     * @return
     */
    public Result abook(String isbn) {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Book book = bookService.findByIsbn(isbn);
        Iterable<BookReview> brs = bookService.findBookReviewsByBook(book);
        List<Counterpart> counterparts = bookService.findCounterpartsByBook(book);
        List<CounterpartTable> cTbl = new ArrayList<CounterpartTable>();
        for (Counterpart counterpart : counterparts) {
            cTbl.add(CounterpartTable.makeInstantce(counterpart));
        }
        return ok(views.html.admin.book.render(book, CommonUtils.toList(brs), cTbl));
    }

    /**
     * 书刊被借阅前 Top10
     *
     * @return
     */
    public Result bookTopBorrowed(Long size) {
        Iterator<Book> books = bookService.findBooksOrderByBorrowedTimes().iterator();
        List<String> booknames = new ArrayList<String>();
        List<Integer> borrowedTimes = new ArrayList<Integer>();
        List<Integer> heartedTimes = new ArrayList<Integer>();
        int n = 0;
        while (books.hasNext() && n++ < size) {
            Book book = books.next();
            booknames.add(book.bookname);
            borrowedTimes.add(book.borrowedTimes);
            heartedTimes.add(book.heartedTimes);
        }
        ObjectNode result = Json.newObject();
        result.put("booknames", Json.toJson(booknames));
        result.put("borrowedTimes", Json.toJson(borrowedTimes));
        result.put("heartedTimes", Json.toJson(heartedTimes));
        return ok(result);
    }

    /**
     * 书刊被赞 Top10
     *
     * @return
     */
    public Result bookTopHearted(Long size) {
        Iterator<Book> books = bookService.findBooksOrderByHeartedTimes().iterator();
        List<String> booknames = new ArrayList<String>();
        List<Integer> heartedTimes = new ArrayList<Integer>();
        int n = 0;
        while (books.hasNext() && n++ < size) {
            Book book = books.next();
            booknames.add(book.bookname);
            heartedTimes.add(book.heartedTimes);
        }
        ObjectNode result = Json.newObject();
        result.put("booknames", Json.toJson(booknames));
        result.put("heartedTimes", Json.toJson(heartedTimes));
        return ok(result);
    }

    public Result aSearch() {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        String keyword = requestData.get("keyword");
        Iterator<Book> retrievedBooks = bookService.allBooks().iterator();
        List<BookTable> btbl = new ArrayList<BookTable>();
        while (retrievedBooks.hasNext()) {
            btbl.add(BookTable.makeInstance(retrievedBooks.next()));
        }
        Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
        return ok(views.html.admin.allBooks.render(CommonUtils.toList(libraryTypes), btbl, keyword));
    }

    public Result rSearch() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        String keyword = requestData.get("keyword");
        Iterator<Book> retrievedBooks = bookService.allBooks().iterator();
        List<BookTable> btbl = new ArrayList<BookTable>();
        while (retrievedBooks.hasNext()) {
            btbl.add(BookTable.makeInstance(retrievedBooks.next()));
        }
        Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
        return ok(views.html.reader.allBooks
                .render(CommonUtils.toList(libraryTypes), btbl, keyword));
    }

}
