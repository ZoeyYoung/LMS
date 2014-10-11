/*
 * Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
 * @author FSNT)YangDM 2014.8.14.
 */

package controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Borrow;
import models.Counterpart;
import models.Reader;
import models.enums.BorrowStatus;
import models.enums.PointType;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.AdminService;
import services.BookService;
import services.ReaderService;
import utils.CommonUtils;
import views.table.BorrowTable;
import views.table.UserBorrowCondTable;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * BorrowCtrl 主要作用： 实现书刊借阅功能模块，对借阅相关操作做业务处理 1.管理员查看被借书刊，读者查阅自己的借阅书刊 2.读者进行还书，续借，获取借书历史记录
 * 3.管理员查询书刊借阅的情况，如逾期书刊
 * 
 * @return
 */

@Named
@Singleton
public class BorrowCtrl extends Controller {
    private final ReaderService readerService;
    private final BookService bookService;
    private final AdminService adminService;

    @Inject
    public BorrowCtrl(final ReaderService readerService, final BookService bookService,
            final AdminService adminService) {
        this.readerService = readerService;
        this.bookService = bookService;
        this.adminService = adminService;
    }

    public Result borrowForm() {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        return ok(views.html.admin.borrowForm.render(Form.form(Borrow.class)));
    }

    /**
     * 借书
     * 
     * @return
     */
    public Result borrowBook() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String counterpartCode = requestData.get("counterpartCode");
        String readerCode = requestData.get("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        Counterpart cp = bookService.findByCounterpartCode(counterpartCode);
        bookService.newBorrow(reader, cp);
        readerService.newPoint(reader, 1L, "借书", PointType.BOOKBORROW);
        Iterator<Borrow> borrows = bookService.findReaderCurrentBorrows(reader);
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        return ok(Json.toJson(bts));
    }

    /**
     * 还书
     * 
     * @return
     */
    public Result returnBook() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String counterpartCode = requestData.get("counterpartCode");
        String readerCode = requestData.get("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        Counterpart cp = bookService.findByCounterpartCode(counterpartCode);
        Borrow borrow = bookService.findByReaderAndCounterpart(reader, cp);
        if (!BorrowStatus.OVERDUE.equals(borrow.borrowStatus)) {
            readerService.newPoint(reader, 1L, "按时还书", PointType.BOOKBORROW);
        } else {
            readerService.newPoint(reader,
                    -1L * CommonUtils.getIntervalDays(new Date(), borrow.returnDate), "逾期还书",
                    PointType.BOOKBORROW);
        }
        bookService.updateBorrowStatus(reader, cp, BorrowStatus.RETURNED);
        Iterator<Borrow> borrows = bookService.findReaderCurrentBorrows(reader);
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        return ok(Json.toJson(bts));
    }

    /**
     * 续借
     * 
     * @return
     */
    public Result renewBook() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String counterpartCode = requestData.get("counterpartCode");
        String readerCode = requestData.get("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        Counterpart cp = bookService.findByCounterpartCode(counterpartCode);
        bookService.updateBorrowStatus(reader, cp, BorrowStatus.RENEW);
        Iterator<Borrow> borrows = bookService.findReaderCurrentBorrows(reader);
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        return ok(Json.toJson(bts));
    }

    /**
     * 所有借阅
     * 
     * @return
     */
    public Result aBorrows() {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        Iterator<Borrow> borrows = bookService.allBorrows();
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        UserBorrowCondTable ubc = new UserBorrowCondTable();
        return ok(views.html.admin.allBorrows.render(bts, ubc));
    }

    /**
     * 逾期借阅
     * 
     * @return
     */
    public Result oBorrows() {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        Iterator<Borrow> overdueBorrows = bookService.findOverduesBorrows();
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (overdueBorrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(overdueBorrows.next(), false));
        }
        UserBorrowCondTable ubc = new UserBorrowCondTable();
        return ok(views.html.admin.overdueBorrows.render(bts, ubc));
    }

    public Result rBorrows() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        // 获取全部的借阅记录
        Iterator<Borrow> borrows = bookService.findReaderCurrentBorrows(reader);
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        boolean overdue = false;
        while (borrows.hasNext()) {
            Borrow borrow = borrows.next();
            if (new Date().before(CommonUtils.afterDays(borrow.returnDate, 1))
                    && CommonUtils.afterDays(new Date(), 10).after(borrow.returnDate)
                    && borrow.renewTimes < reader.readerCate.reLoanTimes) {
                bts.add(BorrowTable.makeInstantce(borrow, true));
            } else {
                bts.add(BorrowTable.makeInstantce(borrow, false));
            }
            if (BorrowStatus.OVERDUE.equals(borrow.borrowStatus)) {
                overdue = true;
            }
        }
        if (overdue) {
            for (BorrowTable bt : bts) {
                bt.renewable = false;
            }
        }
        return ok(views.html.reader.borrows.render(bts));
    }

    public Result rBorrowsHistory() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Iterator<Borrow> borrows = bookService.findReaderReturedBorrows(reader);
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        UserBorrowCondTable ubc = new UserBorrowCondTable();
        return ok(views.html.reader.borrowsHistory.render(bts, ubc));
    }

    /**
     * 根据时间检索符合条件的借阅记录
     * 
     * @return
     * @throws ParseException
     */
    public Result aBorrowsBetween() throws ParseException {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        // 通过表单数据检索条件初始化
        UserBorrowCondTable ubc = new UserBorrowCondTable(requestData.get("timeStart"),
                requestData.get("timeEnd"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeStart = null;
        Date timeEnd = null;
        // 获取的借阅记录的借阅时间的范围起始和终止的
        // 若起始时间为空，则赋值2014年1月1日 0时0分0秒，为了程序能正常运作而做的方式；
        // 否则把表单数据添加 时间（0时0分0秒）转换成Date类型
        if (ubc.timeStart == null || ubc.timeStart.equals("")) {
            timeStart = sdf.parse("2014-01-01 00:00:00");
        } else {
            timeStart = sdf.parse(ubc.timeStart + " 00:00:00");
        }
        // 若终止为空，则赋值 当前系统时间；
        // 否则把表单数据添加 时间（23时59分59秒）转换成Date类型
        if (ubc.timeEnd == null || ubc.timeEnd.equals("")) {
            timeEnd = new Date();
        } else {
            timeEnd = sdf.parse(ubc.timeEnd + " 23:59:59");
        }
        Iterator<Borrow> borrows = bookService.borrowsBetween(timeStart, timeEnd).iterator();
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        return ok(views.html.admin.allBorrows.render(bts, ubc));
    }

    public Result oBorrowsBetween() throws ParseException {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        // 通过表单数据检索条件初始化
        UserBorrowCondTable ubc = new UserBorrowCondTable(requestData.get("timeStart"),
                requestData.get("timeEnd"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeStart = null;
        Date timeEnd = null;
        // 获取的借阅记录的借阅时间的范围起始和终止的
        // 若起始时间为空，则赋值2014年1月1日 0时0分0秒，为了程序能正常运作而做的方式；
        // 否则把表单数据添加 时间（0时0分0秒）转换成Date类型
        if (ubc.timeStart == null || ubc.timeStart.equals("")) {
            timeStart = sdf.parse("2014-01-01 00:00:00");
        } else {
            timeStart = sdf.parse(ubc.timeStart + " 00:00:00");
        }
        // 若终止为空，则赋值 当前系统时间；
        // 否则把表单数据添加 时间（23时59分59秒）转换成Date类型
        if (ubc.timeEnd == null || ubc.timeEnd.equals("")) {
            timeEnd = new Date();
        } else {
            timeEnd = sdf.parse(ubc.timeEnd + " 23:59:59");
        }
        Iterator<Borrow> borrows = bookService.overdueBorrowsBetween(timeStart, timeEnd).iterator();
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        return ok(views.html.admin.overdueBorrows.render(bts, ubc));
    }

    /**
     * 根据时间段检索，目前登陆的读者符合条件的借阅记录
     * 
     * @return
     * @throws ParseException
     */
    public Result rBorrowsBetween() throws ParseException {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        // 通过表单数据检索条件初始化
        UserBorrowCondTable ubc = new UserBorrowCondTable(requestData.get("timeStart"),
                requestData.get("timeEnd"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date timeStart = null;
        Date timeEnd = null;
        // 获取的借阅记录的借阅时间的范围起始和终止的
        // 若起始时间为空，则赋值2014年1月1日 0时0分0秒，为了程序能正常运作而做的方式；
        // 否则把表单数据添加 时间（0时0分0秒）转换成Date类型
        if (ubc.timeStart == null || ubc.timeStart.equals("")) {
            timeStart = sdf.parse("2014-01-01 00:00:00");
        } else {
            timeStart = sdf.parse(ubc.timeStart + " 00:00:00");
        }
        // 若终止为空，则赋值 当前系统时间；
        // 否则把表单数据添加 时间（23时59分59秒）转换成Date类型
        if (ubc.timeEnd == null || ubc.timeEnd.equals("")) {
            timeEnd = new Date();
        } else {
            timeEnd = sdf.parse(ubc.timeEnd + " 23:59:59");
        }
        Iterator<Borrow> borrows = bookService.borrowsBetween(reader, timeStart, timeEnd)
                .iterator();
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        while (borrows.hasNext()) {
            bts.add(BorrowTable.makeInstantce(borrows.next(), false));
        }
        return ok(views.html.reader.borrowsHistory.render(bts, ubc));
    }

    /**
     * 读者当前借阅书刊
     * 
     * @param readerCode
     * @return
     */
    public Result readerBorrowsCurrent(String readerCode) {
        Reader reader = readerService.findByReaderCode(readerCode);
        Iterator<Borrow> borrows = bookService.findReaderCurrentBorrows(reader);
        List<BorrowTable> bts = new ArrayList<BorrowTable>();
        int booksCount = 0;
        boolean overdue = false;
        while (borrows.hasNext()) {
            booksCount++;
            Borrow borrow = borrows.next();
            bts.add(BorrowTable.makeInstantce(borrow, false));
            if (BorrowStatus.OVERDUE.equals(borrow.borrowStatus)) {
                overdue = true;
            }
        }
        String borrowStatusMsg = "";
        if (booksCount >= reader.readerCate.limitBooksCount) {
            borrowStatusMsg += "已达借阅上线，无法借阅\n"; // TODO
        }
        if (overdue) {
            borrowStatusMsg += "存在逾期未归还书刊，无法借阅(或续借)\n"; // TODO
        }
        ObjectNode result = Json.newObject();
        result.put("borrows", Json.toJson(bts));
        result.put("borrowStatusMsg", borrowStatusMsg);
        return ok(result);
    }

    public Result counterpart(String code) {
        ObjectNode result = Json.newObject();
        Counterpart cp = bookService.findByCounterpartCode(code);
        if (cp != null) {
            result.put("counterpart", Json.toJson(cp));
            // 获取当前复本状态
            Borrow borrow = bookService.findCounterpartCurrentBorrow(cp);
            if (borrow == null) {
                result.put("borrow", "borrow");
            } else if (new Date().before(CommonUtils.afterDays(borrow.returnDate, 1))
                    && CommonUtils.afterDays(new Date(), 10).after(borrow.returnDate)
                    && borrow.renewTimes < borrow.reader.readerCate.reLoanTimes) {
                result.put("reader", Json.toJson(borrow.reader));
                result.put("borrow", "review");
            } else {
                result.put("reader", Json.toJson(borrow.reader));
                result.put("borrow", "return");
            }
        } else {
            result.put("msg", "不存在的书刊");
        }
        return ok(result);
    }

    public Result readerTopBorrowBook(Long size) {
        Iterator<Object[]> objIter = bookService.findReadersOrderByBorrowBookTimes();
        List<String> readernames = new ArrayList<String>();
        List<Long> borrowTimes = new ArrayList<Long>();
        int n = 0;
        while (objIter.hasNext() && n++ < size) {
            Object[] obj = objIter.next();
            readernames.add(((Reader) obj[0]).readerName);
            borrowTimes.add((Long) obj[1]);
        }
        ObjectNode result = Json.newObject();
        result.put("readernames", Json.toJson(readernames));
        result.put("borrowTimes", Json.toJson(borrowTimes));
        return ok(result);
    }
}
