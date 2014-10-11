/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.18.
*/

package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Book;
import models.BookReview;
import models.Reader;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.BookService;
import services.ReaderService;
import views.table.BookReviewTable;

import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * BookReviewCtrl 主要作用： 该类主要实现对书评操作的响应
 *   1.查看当前在读书刊的书评
 *   2.保存书评，对该书进行评价点赞
 * @return
 */

@Named
@Singleton
public class BookReviewCtrl extends Controller {
    private final BookService bookService;
    private final ReaderService readerService;

    @Inject
    public BookReviewCtrl(final BookService bookService, final ReaderService readerService) {
        this.bookService = bookService;
        this.readerService = readerService;
    }

    public Result readerBookReviews() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Iterator<BookReview> bookReviews = bookService.findBookReviewByReader(reader).iterator();
        List<BookReviewTable> brtList = new ArrayList<BookReviewTable>();
        while (bookReviews.hasNext()) {
            brtList.add(BookReviewTable.makeInstantce(bookReviews.next()));
        }
        return ok(views.html.reader.bookReviews.render(brtList));
    }

    /**
     * 通过ISBN获取书信息，并跳转到写书评的页面
     *
     * @param isbn
     * @return
     */
    public Result readerBookReview(String isbn) {
        // 获取当前用户的个人信息
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Book book = bookService.findByIsbn(isbn);
        BookReview br = bookService.findByBookAndReader(book, reader);
        if (br == null) {
            br = new BookReview();
        }
        return ok(views.html.reader.bookReview.render(book, br));
    }

    /**
     * 获取前台的书评表单，并存入数据库
     *
     * @return
     */
    public Result saveBookReview() {
        // 获取当前用户的个人信息
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        String isbn = requestData.get("isbn");
        String review = requestData.get("bookReview");
        // 通过ＩＳＢＮ获取书刊信息
        Book book = bookService.findByIsbn(isbn);
        BookReview br = bookService.findByBookAndReader(book, reader);
        if (br == null) {
            br = new BookReview();
            br.book = book;
            br.reader = reader;
            br.star = false;
            br.createDate = new Date();
        }
        br.updateDate = new Date();
        br.review = review;
        bookService.updateOrSaveBookReview(br);
        return redirect("/reader/borrows");
    }

    public Result setBookHeart(String isbn) {
        // 获取当前用户的个人信息
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        // 通过ＩＳＢＮ获取书刊信息
        Book book = bookService.findByIsbn(isbn);
        BookReview br = bookService.findByBookAndReader(book, reader);
        if (br == null) {
            br = new BookReview();
            br.book = book;
            br.reader = reader;
            br.star = false;
            br.review = "";
            br.createDate = new Date();
            br.updateDate = new Date();
        }
        if (br.star) {
            book.heartedTimes -= 1;
        } else {
            book.heartedTimes += 1;
        }
        bookService.updateBook(book);
        br.star = !br.star;
        bookService.updateOrSaveBookReview(br);
        ObjectNode result = Json.newObject();
        result.put("hearted", br.star ? "true" : "false");
        result.put("heartedTimes", book.heartedTimes);
        return ok(result);
    }

}
