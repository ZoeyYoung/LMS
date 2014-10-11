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

import models.enums.PointType;

@Entity
@Table(name = "Point_tab")
public class Point {
    @Id
    @GeneratedValue
    @Column(name = "PointID")
    public Long id;
    @ManyToOne
    @JoinColumn(name = "ReaderID")
    public Reader reader;
    @Column(name = "Source", nullable = false)
    public String source;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Type", nullable = false)
    public PointType ptype = PointType.BOOKBORROW;
    @Temporal(TemporalType.DATE)
    @Column(name = "CreateDate")
    public Date createDate;
    @Temporal(TemporalType.DATE)
    @Column(name = "OverdueDate")
    public Date overdueDate;
    @Column(name = "Point", nullable = false)
    public Long point;
}
