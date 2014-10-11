/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.18.
*/

package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import models.Admin;
import models.Point;
import models.Reader;
import models.enums.PointType;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import services.AdminService;
import services.ReaderService;
import utils.CommonUtils;
import views.form.PointForm;
import views.table.SumPointTable;

/**
 * PointCtrl 主要作用： 对读者使用图书馆系统积分进行处理
 *   1.管理员可以查看读者积分情况，读者可以查看自己的积分
 *   2.管理员对读者使用图书管情况进行对读者的积分管理
 * @return
 */

@Named
@Singleton
public class PointCtrl extends Controller {
    private final ReaderService readerService;
    private final AdminService adminService;

    @Inject
    public PointCtrl(final ReaderService readerService, final AdminService adminService) {
        this.readerService = readerService;
        this.adminService = adminService;
    }

    public Result aPoints() {
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Iterable<Point> points = readerService.findAllPoints();
        List<SumPointTable> sumpoints = readerService.findSumPoints();
        return ok(views.html.admin.allPoints.render(Form.form(PointForm.class),
                CommonUtils.toList(points), sumpoints));
    }

    public Result rPoints() {
        String readerCode = session("readerCode");
        Reader reader = readerService.findByReaderCode(readerCode);
        if (reader == null) {
            return redirect("/reader/login");
        }
        Iterable<Point> points = readerService.findPointsByReader(reader);
        Point point = readerService.findSumPoint(reader, PointType.exchangeStatus());
        Point totalpoint = readerService.findSumPoint(reader);
        Iterable<Point> epoints = readerService.findExchangedSumPoint(reader);
        return ok(views.html.reader.points.render(CommonUtils.toList(points), point, totalpoint,
                CommonUtils.toList(epoints)));
    }

    public Result newPoint() {
        String adminName = session("adminName");
        Admin ad = adminService.findByAdminName(adminName);
        if (ad == null) {
            return redirect("/admin/login");
        }
        Form<PointForm> filledForm = Form.form(PointForm.class).bindFromRequest();
        if (filledForm.hasErrors()) {
            Iterable<Point> points = readerService.findAllPoints();
            List<SumPointTable> sumpoints = readerService.findSumPoints();
            return badRequest(views.html.admin.allPoints.render(filledForm,
                    CommonUtils.toList(points), sumpoints));
        } else {
            PointForm pf = filledForm.get();
            Reader reader = readerService.findByReaderCode(pf.readerCode);
            if (reader != null) {
                readerService.newPoint(reader, pf.point, pf.source, pf.ptype);
            }
            return redirect(routes.PointCtrl.aPoints());
        }
    }

    public Result deletePoint(Long id) {
        Point point = readerService.findPoint(id);
        if (point != null) {
            readerService.deletePoint(id);
        }
        return redirect(routes.PointCtrl.aPoints());
    }

}
