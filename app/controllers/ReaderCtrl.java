/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)ZhangXiang 2014.8.19.
*/

package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Book;
import models.Borrow;
import models.Information;
import models.Reader;
import models.enums.BorrowStatus;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.AdminService;
import services.BookService;
import services.ReaderService;
import utils.CommonUtils;
import utils.MD5Utils;
import views.form.LoginForm;
import views.table.MessageTable;
import views.table.ReaderTable;

import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * ReaderCtrl 主要作用： 响应读者的相关操作，对提交的数据进行处理并实现管
 *                     理员对读者密码重置，控制跳转
 *   1.验证读者是否登录，登录能访问页面，没有登录不能访问
 *   2.读者可以修改自己的密码
 *   3.读者注销退出
 * @return
 */

@Named
@Singleton
public class ReaderCtrl extends Controller {
    private final BookService bookService;
    private final ReaderService readerService;
    private final AdminService adminService;

    @Inject
    public ReaderCtrl(final BookService bookService, final ReaderService readerService,
            final AdminService adminService) {
        this.bookService = bookService;
        this.readerService = readerService;
        this.adminService = adminService;
    }

    public Result redirect() {
        return redirect("/reader/index");
    }

    public Result index() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Iterator<Borrow> borrows = bookService.findReaderCurrentBorrows(reader);
        List<MessageTable> mtl = new ArrayList<MessageTable>();
        while (borrows.hasNext()) {
            Borrow borrow = borrows.next();
            if (BorrowStatus.OVERDUE.equals(borrow.borrowStatus)) {
                mtl.add(new MessageTable("你借的《" + borrow.counterpart.book.bookname
                        + "》已逾期，请尽快办理还书！", "danger"));
            } else if (CommonUtils.afterDays(new Date(), 10).after(borrow.returnDate)
                    && borrow.renewTimes >= reader.readerCate.reLoanTimes) {
                mtl.add(new MessageTable("你借的《" + borrow.counterpart.book.bookname + "》将于"
                        + borrow.returnDate + "过期，请尽快办理还书！", "warning"));
            } else if (CommonUtils.afterDays(new Date(), 10).after(borrow.returnDate)
                    && borrow.renewTimes < reader.readerCate.reLoanTimes) {
                mtl.add(new MessageTable("你借的《" + borrow.counterpart.book.bookname + "》将于"
                        + borrow.returnDate + "过期，请尽快办理续借或还书！", "warning"));
            }
        }
        String lastLoginDateStr = session("lastLoginDate");
        Date lastLoginDate = CommonUtils.isBlank(lastLoginDateStr) ? new Date() : CommonUtils
                .stringToDate(lastLoginDateStr);
        Iterator<Book> bookIter = bookService.findBookByCreateDateAfterLoginDate(lastLoginDate);
        while (bookIter.hasNext()) {
            mtl.add(new MessageTable("新书《" + bookIter.next().bookname + "》已上架，欢迎前来借阅！", "success"));
        }
        Iterator<Book> booksIter = bookService.findBooksOrderByHeartedTimes().iterator();
        int size = 10;
        int n = 0;
        List<Book> books = new ArrayList<Book>();
        while (booksIter.hasNext() && n++ < size) {
            books.add(booksIter.next());
        }
        Iterable<Information> infos = readerService.findInformationForReader(reader.readerCode);
        return ok(views.html.reader.index.render(mtl, books, CommonUtils.toList(infos)));
    }

    public Result loginForm() {
        return ok(views.html.reader.loginForm.render(Form.form(LoginForm.class), ""));
    }

    public Result login() {
        Form<LoginForm> filledForm = Form.form(LoginForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.reader.loginForm.render(filledForm, ""));
        } else {
            LoginForm lf = filledForm.get();
            Reader reader = readerService.findByReaderCodeAndPassword(lf.readerCode, lf.password);
            if (reader != null) {
                session("readerCode", reader.readerCode);
                session("readerName", reader.readerName);
                session("lastLoginDate", CommonUtils.dateToString(reader.lastLoginDate));
                reader.lastLoginDate = new Date();
                readerService.updateReader(reader);
            } else {
                reader = readerService.findByReaderCode(lf.readerCode);
                if (reader != null && CommonUtils.isBlank(reader.password)) {
                    readerService.resetPassword(lf.readerCode);
                    if ("000000".equals(lf.password)) {
                        session("readerCode", reader.readerCode);
                        session("readerName", reader.readerName);
                        session("lastLoginDate", CommonUtils.dateToString(reader.lastLoginDate));
                        return redirect("/reader/index");
                    } else {
                        return ok(views.html.reader.loginForm.render(filledForm, "读者编号或密码不正确"));
                    }
                } else {
                    return ok(views.html.reader.loginForm.render(filledForm, "读者编号或密码不正确"));
                }
            }
            return redirect("/reader/index");
        }
    }

    public Result logout() {
        String adminName = session("adminName");
        session().clear();
        session("adminName", adminName);
        return redirect("/reader/login");
    }

    /**
     * 个人中心，个人信息
     *
     * @param id
     * @return
     */
    public Result info() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        return ok(views.html.reader.readerInfo.render(reader));
    }

    public Result changePwdForm() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        return ok(views.html.reader.changePwd.render(reader, ""));
    }

    /**
     * 修改密码
     *
     * @return
     */
    public Result changePwd() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        String originPassword = requestData.get("originPassword");
        String password = requestData.get("password");
        String repassword = requestData.get("repassword");
        if (reader.password.equals(MD5Utils.md5(originPassword))) {
            if (password.equals(repassword)) {
                // 加密
                reader.password = MD5Utils.md5(password);
                readerService.savePassword(reader);
                return ok(views.html.reader.changePwd.render(new Reader(), "密码修改成功"));
            } else {
                return ok(views.html.reader.changePwd.render(new Reader(), "两次新密码不一致"));
            }
        } else {
            return ok(views.html.reader.changePwd.render(new Reader(),
                    "原密码不正确，不能修改密码。若忘记密码可联系管理员进行重置。"));
        }
    }

    public Result reader(String readerCode) {
        ObjectNode result = Json.newObject();
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader != null) {
            result.put("reader", Json.toJson(reader));
        } else {
            result.put("msg", "读者不存在");
        }
        return ok(result);
    }

    /**
     * 管理员获取所有读者信息
     *
     * @return
     */
    public Result allReaders() {
        // yc add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Iterator<Reader> readers = readerService.allReaders().iterator();
        List<ReaderTable> rtbls = new ArrayList<ReaderTable>();
        while (readers.hasNext()) {
            rtbls.add(ReaderTable.makeInstantce(readers.next()));
        }
        return ok(views.html.admin.allReaders.render(rtbls));
    }

    public Result resetPasswordForm() {
        // yc add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        return ok(views.html.admin.resetPwd.render(""));
    }

    /**
     * 管理员重置读者密码
     *
     * @return
     */
    public Result resetPassword() {
        DynamicForm requestData = Form.form().bindFromRequest();
        String readerCode = requestData.get("readerCode");
        Reader reader = readerService.resetPassword(readerCode);
        if (reader == null) {
            return ok(views.html.admin.resetPwd.render("用户不存在"));
        } else {
            return ok(views.html.admin.resetPwd.render("用户" + readerCode + "密码重置成功，当前密码为000000"));
        }
    }
}
