/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.10.
*/

package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.enums.CounterpartStatus;

@Entity
@Table(name = "Counterpart_tab")
public class Counterpart {
    @Id
    @GeneratedValue
    @Column(name = "CounterpartID")
    public Long id;
    /**
     * 书刊
     */
    @ManyToOne
    @JoinColumn(name = "BookID")
    public Book book;
    /**
     * 书刊条码（自编号）
     */
    @Column(name = "CounterpartCode", nullable = false)
    public String counterpartCode;
    /**
     * 状态
     */
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "CounterpartStatus")
    public CounterpartStatus status = CounterpartStatus.OFFLOAD;
    /**
     * 登记日期
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "CreateDate")
    public Date createDate;
    /**
     * 操作员
     */
    @Column(name = "Operator")
    public String operator = "admin";

    @Override
    public String toString() {
        return "Counterpart [id=" + id + ", book=" + book.id + ", counterpartCode="
                + counterpartCode + ", status=" + status + ", createDate=" + createDate
                + ", operator=" + operator + "]";
    }
}
