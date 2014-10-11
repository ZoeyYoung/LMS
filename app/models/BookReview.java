/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangDM 2014.8.10.
*/

package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * BookReview类对应数据库书评表BookReview_tab
 * 
 */
@Entity
@Table(name = "BookReview_tab")
public class BookReview {
    @Id
    @GeneratedValue
    @Column(name = "ReviewID")
    public Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "BookID")
    public Book book;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ReaderID")
    public Reader reader;
    @Lob
    @Column(name = "Review")
    public String review;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreateDate")
    public Date createDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UpdateDate")
    public Date updateDate;
    @Column(name = "Star")
    public boolean star;
}
