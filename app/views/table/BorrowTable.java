package views.table;

import models.Borrow;
import utils.CommonUtils;

public class BorrowTable {
    /**
     * 借出日期
     */
    public String borrowDate;
    /**
     * 借出时间
     */
    public String borrowTime;
    /**
     * 应还日期
     */
    public String returnDate;
    /**
     * ISBN
     */
    public String isbn;
    /**
     * 书刊名称
     */
    public String bookname;
    /**
     * 书刊输入码
     */
    public String bookInputCode;
    /**
     * 馆藏类型
     */
    public String libraryType;
    /**
     * 书刊条码
     */
    public String counterpartCode;
    /**
     * 索书号
     */
    public String categoryCode;
    /**
     * 分类
     */
    public String category;
    /**
     * 读者姓名
     */
    public String readerName;
    /**
     * 读者输入码
     */
    public String readerInputCode;
    /**
     * 读者编码
     */
    public String readerCode;
    /**
     * 读者证卡
     */
    public String cardID;
    /**
     * 读者类别
     */
    public String readerCateName;
    /**
     * 借阅状态
     */
    public String borrowStatus;
    /**
     * 是否可续借
     */
    public boolean renewable;

    public Integer renewTimes;

    public static BorrowTable makeInstantce(Borrow borrow, boolean renewable) {
        BorrowTable bt = new BorrowTable();
        bt.borrowDate = CommonUtils.getDateInstance(borrow.borrowDate);
        bt.borrowTime = CommonUtils.getTimeInstance(borrow.borrowDate);
        bt.returnDate = CommonUtils.getDateInstance(borrow.returnDate);
        bt.isbn = borrow.counterpart.book.isbn;
        bt.bookname = borrow.counterpart.book.bookname;
        bt.bookInputCode = borrow.counterpart.book.inputCode;
        bt.libraryType = borrow.counterpart.book.libraryType.libraryType;
        bt.counterpartCode = borrow.counterpart.counterpartCode;
        bt.categoryCode = borrow.counterpart.book.categoryCode;
        bt.category = borrow.counterpart.book.category;
        bt.readerName = borrow.reader.readerName;
        bt.readerInputCode = borrow.reader.readerInputCode;
        bt.readerCode = borrow.reader.readerCode;
        bt.cardID = borrow.reader.cardID;
        bt.readerCateName = borrow.reader.readerCate.readerCateName;
        bt.borrowStatus = borrow.borrowStatus.status();
        bt.renewable = renewable;
        bt.renewTimes = borrow.renewTimes;
        return bt;
    }
}
