/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)ZhangXiang 2014.8.10.
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

import models.enums.Gender;

@Entity
@Table(name = "Readers_tab")
public class Reader {
    @Id
    @GeneratedValue
    @Column(name = "ReaderID")
    public Long id;
    @Column(name = "ReaderName", nullable = false)
    public String readerName;
    @Column(name = "ReaderInputCode")
    public String readerInputCode;
    @Column(name = "ReaderCode", nullable = false)
    public String readerCode;
    @Column(name = "CardID")
    public String cardID;
    @ManyToOne
    @JoinColumn(name = "ReaderCateID")
    public ReaderCate readerCate;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "Sex")
    public Gender sex;
    @Column(name = "BirthDate")
    public Date birthDate;
    @Column(name = "CorpName")
    public String corpName;
    @Column(name = "DeptName")
    public String deptName;
    @Column(name = "IDCard")
    public String idCard;
    @Column(name = "WorkCard")
    public String workCard;
    @Column(name = "WorkPhone")
    public String workPhone;
    @Column(name = "HomePhone")
    public String homePhone;
    @Column(name = "MobilePhone")
    public String mobilePhone;
    @Column(name = "EMail")
    public String email;
    @Column(name = "Address")
    public String address;
    @Column(name = "Memo")
    public String memo;
    @Column(name = "RegDate")
    public Date regDate;
    @Column(name = "Operator")
    public String operator;
    @Column(name = "Password")
    public String password = "000000";
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LastLoginDate")
    public Date lastLoginDate;
}
