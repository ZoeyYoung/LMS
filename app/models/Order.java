/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.10.
*/

package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.enums.OrderStatus;
import views.form.OrderForm;

@Entity
@Table(name = "Orders_tab")
public class Order {
    @Id
    @GeneratedValue
    @Column(name = "OrderID")
    public Long id;
    /**
     * ISBN
     */
    @Column(name = "ISBN", nullable = false)
    public String isbn;
    /**
     * 书刊名称
     */
    @Column(name = "BookName", nullable = false)
    public String bookname;
    /**
     * 著者
     */
    @Column(name = "Author")
    public String author;
    /**
     * 译者
     */
    @Column(name = "Translator")
    public String translator;
    /**
     * 分类名称
     */
    @Column(name = "Category")
    public String category;
    /**
     * 出版单位
     */
    @Column(name = "Publisher")
    public String publisher;
    /**
     * 价格
     */
    @Column(name = "Price")
    public String price;
    /**
     * 状态: 未购入、已购入、暂不购入
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Status")
    public OrderStatus status = OrderStatus.UNBUYED;
    /**
     * 提出者
     */
    @ManyToOne
    @JoinColumn(name = "ReaderID")
    public Reader reader;
    /**
     * 购买理由
     */
    @Column(name = "Reason")
    public String reason;

    public static Order makeInstance(OrderForm formData, Reader reader) {
        Order order = new Order();
        order.isbn = formData.isbn;
        order.bookname = formData.bookname;
        order.author = formData.author;
        order.translator = formData.translator;
        order.category = formData.category;
        order.publisher = formData.publisher;
        order.price = formData.price;
        order.status = OrderStatus.UNBUYED;
        order.reader = reader;
        order.reason = formData.reason;
        return order;
    }
}
