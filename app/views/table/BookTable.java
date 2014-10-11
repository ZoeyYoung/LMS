package views.table;

import models.Book;

public class BookTable {
    /**
     * ISBN
     */
    public String isbn;
    /**
     * 书刊名称
     */
    public String bookname;
    /**
     * 输入码
     */
    public String inputCode;
    /**
     * 著者
     */
    public String author;
    /**
     * 馆藏类型
     */
    public String libraryType;
    /**
     * 分类号
     */
    public String categoryCode;
    /**
     * 分类名称
     */
    public String category;
    /**
     * 出版单位
     */
    public String publisher;
    /**
     * 出版地
     */
    public String publishAddr;
    /**
     * 出版日期
     */
    public String publishDate;
    /**
     * 价格
     */
    public String price;
    /**
     * 借出次数
     */
    public Integer borrowedTimes = 0;
    /**
     * 借出次数
     */
    public Integer heartedTimes = 0;

    public Integer counterparts = 0;

    public static BookTable makeInstance(Book book) {
        BookTable bt = new BookTable();
        bt.isbn = book.isbn;
        bt.bookname = book.bookname;
        bt.inputCode = book.inputCode;
        bt.author = book.author;
        bt.libraryType = book.libraryType.libraryType;
        bt.categoryCode = book.categoryCode;
        bt.category = book.category;
        bt.publisher = book.publisher;
        bt.publishDate = book.publishDate;
        bt.price = book.price;
        bt.borrowedTimes = book.borrowedTimes;
        bt.heartedTimes = book.heartedTimes;
        // bt.counterparts = size;
        return bt;
    }
}
