/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.10.
*/

package models;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import models.enums.BorrowStatus;

/**
 * Borrow类对应数据借书表Borrows_tab
 * 
 */
@Entity
@Table(name = "Borrows_tab")
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "BorrowID")
    public Long id;
    @ManyToOne(cascade = { CascadeType.MERGE })
    @JoinColumn(name = "CounterpartID")
    public Counterpart counterpart;
    @ManyToOne
    @JoinColumn(name = "ReaderID")
    public Reader reader;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "BorrowDate", updatable = false)
    public Date borrowDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "ReturnDate")
    public Date returnDate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "BorrowStatus")
    public BorrowStatus borrowStatus = BorrowStatus.UNRETURNED;
    /**
     * 已续借次数
     */
    @Column(name = "RenewTimes")
    public int renewTimes = 0;
    /**
     * 提醒日期
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "RemindDate")
    public Date remindDate;
}
