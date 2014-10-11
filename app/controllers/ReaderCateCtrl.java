/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.19.
*/

package controllers;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Reader;
import models.ReaderCate;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import repos.ReaderCateRepository;
import services.AdminService;
import services.ReaderService;
import utils.CommonUtils;
import views.form.ReaderCateForm;


/**
 * PointCtrl 主要作用： 对读者权限进行修改
 *   1.管理员查看读者读者权限
 *   2.管理员对读者权限进行增删改
 * @return
 */

@Named
@Singleton
public class ReaderCateCtrl extends Controller {
    private final ReaderCateRepository readerCateRepository;
    private final ReaderService readerService;
    private final AdminService adminService;

    @Inject
    public ReaderCateCtrl(final ReaderCateRepository readerCateRepository,
            final ReaderService readerService, final AdminService adminService) {
        this.readerCateRepository = readerCateRepository;
        this.readerService = readerService;
        this.adminService = adminService;
    }

    public Result readerCates() {
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
        Iterable<ReaderCate> retrievedReaderCate = readerCateRepository.findAll();
        return ok(views.html.admin.readerCates.render(
                CommonUtils.toList(retrievedReaderCate),
                Form.form(ReaderCateForm.class), message));
    }

    public Result newReaderCate() {
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Form<ReaderCateForm> filledForm = Form.form(ReaderCateForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<ReaderCate> retrievedReaderCate = readerCateRepository.findAll();
            return badRequest(views.html.admin.readerCates.render(
                    CommonUtils.toList(retrievedReaderCate), filledForm, ""));
        } else {
            readerCateRepository.save(ReaderCate.makeInstance(filledForm.get()));
            return redirect(routes.ReaderCateCtrl.readerCates());
        }
    }

    public Result updateReaderCate(Long id) {
        DynamicForm requestData = Form.form().bindFromRequest();
        ReaderCate retrievedReaderCate = readerCateRepository.findOne(id);
        retrievedReaderCate.readerCateName = requestData.get("readerCateName-" + id);
        retrievedReaderCate.limitBooksCount = Integer.parseInt(requestData.get("limitBooksCount-"
                + id));
        retrievedReaderCate.limitDays = Integer.parseInt(requestData.get("limitDays-" + id));
        retrievedReaderCate.reLoanDays = Integer.parseInt(requestData.get("reLoanDays-" + id));
        retrievedReaderCate.reLoanTimes = Integer.parseInt(requestData.get("reLoanTimes-" + id));
        readerCateRepository.save(retrievedReaderCate);
        return redirect(routes.ReaderCateCtrl.readerCates());
    }

    public Result deleteReaderCate(Long id) {
        ReaderCate rc = readerCateRepository.findOne(id);
        Iterator<Reader> readers = readerService.findByReaderCate(rc).iterator();
        while (readers.hasNext()) {
            flash("failed", "该用户类别下存在用户，无法删除");
            return redirect(routes.ReaderCateCtrl.readerCates());
        }
        readerCateRepository.delete(id);
        return redirect(routes.ReaderCateCtrl.readerCates());
    }

}
