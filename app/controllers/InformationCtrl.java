/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangChao 2014.8.17.
*/

package controllers;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Information;
import models.Reader;
import play.data.DynamicForm;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import repos.InformationRepository;
import services.AdminService;
import services.ReaderService;
import utils.CommonUtils;

/**
 * InformationCtrl 主要作用： 实现管理员发出公告消息进行处理，读者可以获取公告
 *   1.管理员获取所有公告消息
 *   2.管理员对公告做增删改查
 *   3.读者端首页公告显示
 *   4.公告消息对应类为 Information
 * @return
 */

@Named
@Singleton
public class InformationCtrl extends Controller {
    private final InformationRepository informationRepository;
    private final AdminService adminService;
    private final ReaderService readerService;

    @Inject
    public InformationCtrl(final InformationRepository informationRepository,
            final AdminService adminService,
            final ReaderService readerService) {
        this.informationRepository = informationRepository;
        this.adminService = adminService;
        this.readerService = readerService;
    }

    /**
     * 获取已经发送过的消息
     */
    public Result allInformations() {
        // yc add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Iterable<Information> informations = informationRepository.findAll();
        return ok(views.html.admin.allInformations.render(CommonUtils.toList(informations)));
    }

    /**
     * 提交新消息
     */
    public Result newInformation() {
        String adminName = session("adminName");
        Admin admin = adminService.findByAdminName(adminName);
        if (admin == null) {
            return redirect("/admin/login");
        }
        Form<Information> filledForm = Form.form(Information.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<Information> informations = informationRepository.findAll();
            return badRequest(views.html.admin.allInformations.render(CommonUtils
                    .toList(informations)));
        } else {
            Information information = filledForm.get();
            information.createDate = new Date();
            informationRepository.save(information);
            return redirect(routes.InformationCtrl.allInformations());
        }
    }

    /**
     * 显示消息体
     *
     * @param id
     * @return
     */
    public Result aShowInformation(Long id) {
        // yc add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        String msg = requestData.get("msg");
        Information information = informationRepository.findOne(id);
        return ok(views.html.admin.informationShow.render(information, msg));
    }

    public Result rShowInformation(Long id) {
        // 获取当前用户的个人信息
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Information information = informationRepository.findOne(id);
        return ok(views.html.reader.informationShow.render(information));
    }

    /**
     * 修改消息
     *
     * @param id
     * @return
     */
    public Result updateInformation(Long id) {
        // yc add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        DynamicForm requestData = Form.form().bindFromRequest();
        Information information = informationRepository.findOne(id);
        information.informationTitle = requestData.get("informationTitle-" + id);
        information.informationContent = requestData.get("informationContent-" + id);
        information.createDate = new Date();
        information.cardID = requestData.get("cardID-" + id);
        informationRepository.save(information);
        return redirect(routes.InformationCtrl.aShowInformation(id));
    }

    /**
     * 删除消息
     *
     * @param id
     * @return
     */
    public Result deleteInformation(Long id) {
        // yc add 查看是否登陆
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Information information = informationRepository.findOne(id);
        if (information != null) {
            informationRepository.delete(id);
        }
        return redirect(routes.InformationCtrl.allInformations());
    }
}
