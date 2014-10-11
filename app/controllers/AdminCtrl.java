/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.10.
*/

package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Information;
import models.Order;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.AdminService;
import services.ReaderService;
import utils.CommonUtils;
import views.form.AdminLoginForm;

/**
 * AdminCtrl 主要作用： 响应管理员页面请求，对管理员请求做业务处理，判断跳转到相应页面
 *   1.当管理员需要使用系统，需要登陆才能实现相关操作
 *   2.管理员可以获取当前所有管理员信息
 *   3.admin管理员可以删改其他管理员
 *   4.ReaderService和AdminService是为管理员服务的类，通过他们操作数据库
 */
@Named
@Singleton
public class AdminCtrl extends Controller {
    private final ReaderService readerService;
    private final AdminService adminService;

    @Inject
    public AdminCtrl(final ReaderService readerService, final AdminService adminService) {
        this.readerService = readerService;
        this.adminService = adminService;
    }

    /**
     * 管理员首页
     *
     * @return
     */
    public Result index() {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        Iterator<Order> oIter = adminService.findOrdersUnHandled();
        Long unhandledOrderSize = 0L;
        while (oIter.hasNext()) {
            oIter.next();
            unhandledOrderSize++;
        }
        Iterator<Information> infoIter = readerService.findInformations().iterator();
        int n = 0;
        List<Information> infos = new ArrayList<Information>();
        while (infoIter.hasNext() && n++ < 10) {
            infos.add(infoIter.next());
        }
        return ok(views.html.admin.index.render(unhandledOrderSize, infos));
    }

    public Result loginForm() {
        return ok(views.html.admin.loginForm.render(Form.form(AdminLoginForm.class), ""));
    }

    /**
     * 管理员登陆
     *
     * @return
     */
    public Result login() {
        Form<AdminLoginForm> filledForm = Form.form(AdminLoginForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            return badRequest(views.html.admin.loginForm.render(filledForm, ""));
        } else {
            AdminLoginForm lf = filledForm.get();
            Admin admin = adminService.findByAdminNameAndAdminPassword(lf.adminName,
                    lf.adminPassword);
            if (admin == null && "admin".equals(lf.adminName)) {
                // 预设帐户
                admin = adminService.findByAdminName("admin");
                // 首次登录
                if (admin == null && "000000".equals(lf.adminPassword)) {
                    admin = new Admin();
                    admin.adminName = "admin";
                    admin.adminPassword = "000000";
                    admin.adminMail = "fake@email.com";
                    adminService.save(admin);
                    session("adminName", admin.adminName);
                } else {
                    return ok(views.html.admin.loginForm.render(filledForm, "管理员帐号或密码不正确"));
                }
            } else if (admin != null) {
                session("adminName", admin.adminName);
            } else {
                return ok(views.html.admin.loginForm.render(filledForm, "管理员帐号或密码不正确"));
            }
            return redirect("/admin/index");
        }
    }

    /**
     * 管理员退出登陆
     *
     * @return
     */
    public Result logout() {
        session().clear();
        return redirect("/admin/login");
    }

    public Result chart() {
        return ok(views.html.admin.charts.render());
    }

    /**
     * 管理员属性设置
     *
     * allAmins() 获取所有管理员信息 @return newAdmin() 创建新管理员 @return updateAdmin(Long) 修改管理员信息 @return
     * deleteAdmin(Long) 删除管理员 @return
     *
     */
    public Result allAdmins() {
      // 查看是否登陆
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        Iterable<Admin> admins = adminService.findAll();
        return ok(views.html.admin.allAdmins.render(CommonUtils.toList(admins)));
    }

    public Result checkAdmin(String name) {
        Admin admin = adminService.findByAdminName(name);
        if (admin == null) {
            return ok("false");
        } else {
            return ok("true");
        }
    }

    public Result newAdmin() {
      // 查看是否登陆
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        Form<Admin> filledForm = Form.form(Admin.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<Admin> admins = adminService.findAll();
            return badRequest(views.html.admin.allAdmins.render(CommonUtils.toList(admins)));
        } else {
            adminService.save(filledForm.get());
            return redirect(routes.AdminCtrl.allAdmins());
        }
    }

    public Result updateAdmin(Long id) {
        // 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        Admin admin = adminService.findOne(id);
        admin.adminName = requestData.get("adminName-" + id);
        admin.adminPassword = requestData.get("adminPassword-" + id);
        admin.adminMail = requestData.get("adminMail-" + id);
        adminService.save(admin);
        return redirect(routes.AdminCtrl.allAdmins());
    }

    public Result deleteAdmin(Long id) {
       // 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Admin admin = adminService.findOne(id);
        if (admin != null) {
            adminService.delete(id);
        }
        return redirect(routes.AdminCtrl.allAdmins());
    }

}
