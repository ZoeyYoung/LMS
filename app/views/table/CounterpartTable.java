package views.table;

import utils.CommonUtils;
import models.Book;
import models.Counterpart;

public class CounterpartTable {
    public Long id;
    /**
     * 书刊条码
     */
    public String counterpartCode;
    /**
     * 状态
     */
    public String status;
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
     * 登记日期
     */
    public String createDate;
    /**
     * 操作员
     */
    public String operator;

    public static CounterpartTable makeInstantce(Counterpart counterpart) {
        CounterpartTable bookTbl = new CounterpartTable();
        bookTbl.counterpartCode = counterpart.counterpartCode;
        bookTbl.status = counterpart.status.status();
        bookTbl.createDate = CommonUtils.getDateInstance(counterpart.createDate);
        bookTbl.operator = counterpart.operator;
        Book book = counterpart.book;
        bookTbl.isbn = book.isbn;
        bookTbl.bookname = book.bookname;
        bookTbl.inputCode = book.inputCode;
        bookTbl.author = book.author;
        // TODO
        // bookTbl.libraryType = book.libraryType.libraryType;
        bookTbl.categoryCode = book.categoryCode;
        bookTbl.category = book.category;
        bookTbl.publisher = book.publisher;
        bookTbl.publishAddr = book.publishAddr;
        bookTbl.publishDate = book.publishDate;
        bookTbl.price = book.price;
        return bookTbl;
    }
}
