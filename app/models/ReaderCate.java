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

import views.form.ReaderCateForm;

@Entity
@Table(name = "ReaderCate_tab")
public class ReaderCate {
    @Id
    @GeneratedValue
    @Column(name = "ReaderCateID")
    public Long id;
    @Column(name = "ReaderCateName", nullable = false)
    public String readerCateName;
    @Column(name = "LimitBooksCount", nullable = false)
    public Integer limitBooksCount;
    @Column(name = "LimitDays", nullable = false)
    public Integer limitDays;
    @Column(name = "ReLoanTimes", nullable = false)
    public Integer reLoanTimes;
    @Column(name = "ReLoanDays", nullable = false)
    public Integer reLoanDays;

    @Override
    public String toString() {
        return "ReaderCate [id=" + id + ", readerCateName=" + readerCateName + ", limitBooksCount="
                + limitBooksCount + ", limitDays=" + limitDays + ", reLoanTimes=" + reLoanTimes
                + ", reLoanDays=" + reLoanDays + "]";
    }

    public static ReaderCate makeInstance(ReaderCateForm formData) {
        ReaderCate rc = new ReaderCate();
        rc.readerCateName = formData.readerCateName;
        rc.limitBooksCount = formData.limitBooksCount;
        rc.limitDays = formData.limitDays;
        rc.reLoanDays = formData.reLoanDays;
        rc.reLoanTimes = formData.reLoanTimes;
        return rc;
    }

}
