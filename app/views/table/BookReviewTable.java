package views.table;

import models.BookReview;

public class BookReviewTable {
    /**
     * ISBN
     */
    public String isbn;
    /**
     * 书刊名称
     */
    public String bookname;
    /**
     * 读者姓名
     */
    public String readerName;
    /**
     * 书评
     */
    public String review;
    /**
     * 是否赞
     */
    public boolean star;

    public static BookReviewTable makeInstantce(BookReview bookReview) {
        BookReviewTable bt = new BookReviewTable();
        bt.isbn = bookReview.book.isbn;
        bt.bookname = bookReview.book.bookname;
        bt.readerName = bookReview.reader.readerName;
        bt.review = bookReview.review;
        bt.star = bookReview.star;
        return bt;
    }
}
