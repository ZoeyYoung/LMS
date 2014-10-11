package views.table;

import models.Order;
import models.enums.OrderStatus;

public class OrderTable {
    public Long id;
    public String category;
    public String bookname;
    public String author;
    public String translator;
    public String publisher;
    public String isbn;
    public String price;
    public String status;
    public String readerName;
    public String reason;
    public boolean deleteable = false;
    public Long orderCount = 0L;

    public static OrderTable makeInstantce(Order order) {
        OrderTable orderTbl = new OrderTable();
        orderTbl.id = order.id;
        orderTbl.category = order.category;
        orderTbl.bookname = order.bookname;
        orderTbl.author = order.author;
        orderTbl.translator = order.translator;
        orderTbl.publisher = order.publisher;
        orderTbl.isbn = order.isbn;
        orderTbl.price = order.price;
        orderTbl.status = order.status.status();
        orderTbl.readerName = order.reader.readerName;
        orderTbl.reason = order.reason;
        if (OrderStatus.UNBUYED.equals(order.status)) {
            orderTbl.deleteable = true;
        }
        return orderTbl;
    }
}
