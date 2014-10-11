package views.table;

public class UserBorrowCondTable {

    public UserBorrowCondTable() {
        // TODO Auto-generated constructor stub
        timeStart = "";
        timeEnd = "";
        bookName = "";
        borrowStatus = 0;
    }

    public UserBorrowCondTable(String timeStart, String timeEnd) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }

    public UserBorrowCondTable(String timeStart, String timeEnd, String bookName, int borrowStatus) {
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.bookName = bookName;
        this.borrowStatus = borrowStatus;
    }

    // 检索条件：借阅记录，目标时间范围的起始时间和终止时间
    public String timeStart;
    public String timeEnd;

    // 检索条件：书名
    public String bookName;

    // 检索条件：借阅状态
    public int borrowStatus;

}
