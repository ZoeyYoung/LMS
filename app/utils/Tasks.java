package utils;

import org.springframework.scheduling.annotation.Scheduled;

/**
 * 完成定时任务
 * 
 * @author yangdm.fnst
 * 
 */
public class Tasks {
    @Scheduled(cron = "0 5 0 * * ?")
    public void checkBorrows() {
        System.out.println("定时任务进行中。。。");
    }
}
