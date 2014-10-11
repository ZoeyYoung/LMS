/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.10.
*/

package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Admin_tab")

/**
 * 管理员类，对应数据库管理员表Amin_tab
 * 
 */
public class Admin {

    @Id
    @GeneratedValue
    @Column(name = "AdminID")
    public Long id;
    /**
     * 管理员名字
     */
    @Column(name = "AdminName", unique = true, nullable = false)
    public String adminName;
    /**
     * 管理员密码
     */
    @Column(name = "AdminPassword", nullable = false)
    public String adminPassword;
    /**
     * 管理员邮件
     */
    @Column(name = "AdminMail")
    public String adminMail;
}
