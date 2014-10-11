/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.18.
*/

package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Order;
import models.Reader;
import models.enums.OrderStatus;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import repos.OrderRepository;
import services.AdminService;
import services.BookService;
import services.ReaderService;
import views.form.OrderForm;
import views.table.OrderTable;


/**
 * OrderCtrl 主要作用： 实现读者提交书刊购买订单，管理员对订单进行处理操作
 *   1.管理员获取所有读者提交的订单
 *   2.管理员设置图书购买情况
 *   3.读者提交购买图书订单
 *   4.读者查看自己的订单处理情况并可以取消
 * @return
 */

@Named
@Singleton
public class OrderCtrl extends Controller {
    private final OrderRepository orderRepository;
    private final BookService bookService;
    private final ReaderService readerService;
    private final AdminService adminService;

    @Inject
    public OrderCtrl(final OrderRepository orderRepository, final BookService bookService,
            final ReaderService readerService, final AdminService adminService) {
        this.orderRepository = orderRepository;
        this.bookService = bookService;
        this.readerService = readerService;
        this.adminService = adminService;
    }
 
    /**
     * 管理员获取所有订单
     * @return
     */
    public Result allOrders() {
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        final Iterator<Order> retrievedOrders = orderRepository.findAll().iterator();
        Iterator<Object[]> iter = orderRepository.findCountOrderGroupByISBN().iterator();
        List<OrderTable> ort = new ArrayList<OrderTable>();
        while (iter.hasNext()) {
            Object[] obj = iter.next();
            OrderTable order = new OrderTable();
            order.isbn = (String) obj[0];
            order.bookname = (String) obj[1];
            order.status = ((OrderStatus) obj[2]).status();
            order.orderCount = (Long) obj[3];
            ort.add(order);
        }
        return ok(views.html.admin.orders.render(toOrderTableList(retrievedOrders), ort));
    }

    /**
     * 读者查看订单
     * @return
     */
    public Result orderForm() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        return ok(views.html.reader.orderForm.render(Form.form(OrderForm.class)));
    }

    /**
     * 读者提交新订单
     * @return
     */
    public Result newOrder() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Form<OrderForm> filledForm = Form.form(OrderForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.reader.orderForm.render(filledForm));
        } else {
            Order order = Order.makeInstance(filledForm.get(), reader);
            orderRepository.save(order);
            return redirect(routes.OrderCtrl.readerOrders());
        }
    }

    public Result deleteOrder(Long id) {
        Order order = orderRepository.findOne(id);
        if (order != null) {
            orderRepository.delete(id);
        }
        return redirect(routes.OrderCtrl.readerOrders());
    }

    public Result setNoBuy(String isbn) {
        bookService.setOrderStatus(isbn, OrderStatus.NOBUY);
        return ok("");
    }

    public Result setUnBuy(String isbn) {
        bookService.setOrderStatus(isbn, OrderStatus.UNBUYED);
        return ok("");
    }

    /**
     * 
     * @param id 用户号
     * @return
     */
    public Result readerOrders() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Iterator<Order> retrievedOrders = orderRepository.findByReader(reader).iterator();
        return ok(views.html.reader.orders.render(toOrderTableList(retrievedOrders)));
    }

    private List<OrderTable> toOrderTableList(Iterator<Order> orders) {
        List<OrderTable> ots = new ArrayList<OrderTable>();
        while (orders.hasNext()) {
            ots.add(OrderTable.makeInstantce(orders.next()));
        }
        return ots;
    }
}
