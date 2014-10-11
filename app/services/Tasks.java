package services;

import java.util.Date;
import java.util.Iterator;

import javax.inject.Inject;

import models.Borrow;

import org.springframework.scheduling.annotation.Scheduled;

import play.Logger;
import services.BookService;
import utils.CommonUtils;

/**
 * 完成定时任务
 *
 * @author yangdm.fnst
 *
 */
public class Tasks {
    @Inject
    BookService bookService;

    @Scheduled(cron = "1 0 0 * * ?")
    public void checkBorrows() {
        Logger.info("========== 定时任务开始 " + new Date() + "==========");
        Iterator<Borrow> borrows = bookService.findBorrowsByRemindDate(new Date());
        while (borrows.hasNext()) {
            Borrow borrow = borrows.next();
            borrow = bookService.updateBorrowRemindDate(borrow);
            Logger.info("========== Mail To " + borrow.reader.email + " ==========");
            Logger.info("读者：" + borrow.reader.readerCode + " " + borrow.reader.readerName);
            Logger.info("您有书刊：");
            Logger.info("ISBN：" + borrow.counterpart.book.isbn);
            Logger.info("书刊名称：" + borrow.counterpart.book.bookname);
            switch (borrow.borrowStatus) {
                case UNRETURNED:
                    Logger.info("即将到期，请在到期前归还或续借");
                    break;
                case OVERDUE:
                    Logger.info("已逾期，请尽快归还");
                    break;
                default:
                    break;
            }
            Logger.info("应还日期：" + CommonUtils.getDateInstance(borrow.returnDate));
            Logger.info("当前日期：" + CommonUtils.getDateInstance(new Date()));
            Logger.info("========== Mail Send Out ==========");
        }
        Logger.info("========== 定时任务结束 " + new Date() + "==========");
    }
}
