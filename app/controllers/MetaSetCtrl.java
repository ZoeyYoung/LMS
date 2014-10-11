/*
 * Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
 * @author FSNT)YangChao 2014.8.18.
 */
package controllers;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.LibraryType;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import repos.LibraryTypeRepository;
import services.AdminService;
import services.BookService;
import utils.CommonUtils;

/**
 * MetaCtrl 主要作用： 实现管理员对馆藏类型元数据处理，辅助书刊录入 1.管理员获取所有元数据 2.管理员对元数据做增删改 3.元数据对应类为 LibraryType
 * 
 * @return
 */

@Named
@Singleton
public class MetaSetCtrl extends Controller {
    private final BookService bookService;
    private final LibraryTypeRepository libraryTypeRepository;
    private final AdminService adminService;

    @Inject
    public MetaSetCtrl(final BookService bookService,
            final LibraryTypeRepository libraryTypeRepository, final AdminService adminService) {
        this.bookService = bookService;
        this.libraryTypeRepository = libraryTypeRepository;
        this.adminService = adminService;
    }

    public Result allMetas() {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        String message = flash("failed");
        if (message == null) {
            message = "";
        }
        Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
        return ok(views.html.admin.allMetas.render(CommonUtils.toList(libraryTypes), message));
    }

    public Result newMeta() {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Form<LibraryType> filledForm = Form.form(LibraryType.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<LibraryType> libraryTypes = libraryTypeRepository.findAll();
            return badRequest(views.html.admin.allMetas
                    .render(CommonUtils.toList(libraryTypes), ""));
        } else {
            libraryTypeRepository.save(filledForm.get());
            return redirect(routes.MetaSetCtrl.allMetas());
        }
    }

    public Result updateMeta(Long id) {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        LibraryType library = libraryTypeRepository.findOne(id);
        library.libraryType = requestData.get("libraryType-" + id);
        libraryTypeRepository.save(library);
        return redirect(routes.MetaSetCtrl.allMetas());
    }

    public Result deleteMeta(Long id) {
        // add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        boolean flag = bookService.deleteLibraryType(id);
        if (!flag) {
            flash("failed", "该馆藏书刊无法删除(存在书刊或已被删除)");
            return redirect(routes.MetaSetCtrl.allMetas());
        }
        return redirect(routes.MetaSetCtrl.allMetas());
    }

    public Result checkMeta(String name) {
        Iterator<LibraryType> iter = libraryTypeRepository.findByLibraryType(name).iterator();
        while (iter.hasNext()) {
            return ok("true");
        }
        return ok("false");
    }
}
