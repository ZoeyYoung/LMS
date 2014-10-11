/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangChao 2014.8.10.
*/

package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "Information_tab")
public class Information {

    @Id
    @GeneratedValue
    @Column(name = "InformationID")
    public Long id;
    /**
     * 消息主题
     */
    @Column(name = "InformationTitle", unique = true, nullable = false)
    public String informationTitle;
    /**
     * 消息内容
     */
    @Lob
    @Column(name = "Content", nullable = false)
    public String informationContent;
    /**
     * 消息发送日期
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreateDate", nullable = false)
    public Date createDate;
    /**
     * 接收员工编号
     */
    @Column(name = "CardID")
    public String cardID;
}
