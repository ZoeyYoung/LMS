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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import views.form.BookForm;

/**
 * Book类，对应数据库书刊表Books_tab
 */

@Entity
@Table(name = "Books_tab")
public class Book {

    @Id
    @GeneratedValue
    @Column(name = "BookID")
    public Long id;
    /**
     * ISBN
     */
    @Column(name = "ISBN", unique = true, nullable = false)
    public String isbn;
    /**
     * 书刊名称
     */
    @Column(name = "BookName", nullable = false)
    public String bookname;
    /**
     * 输入码
     */
    @Column(name = "InputCode")
    public String inputCode;
    /**
     * 著者
     */
    @Column(name = "Author")
    public String author;
    /**
     * 馆藏类型
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "LibraryTypeID")
    public LibraryType libraryType;
    /**
     * 分类号
     */
    @Column(name = "CategoryCode")
    public String categoryCode;
    /**
     * 分类名称
     */
    @Column(name = "Category")
    public String category;
    /**
     * 出版单位
     */
    @Column(name = "Publisher")
    public String publisher;
    /**
     * 出版地
     */
    @Column(name = "PublishAddr")
    public String publishAddr;
    /**
     * 出版日期
     */
    @Column(name = "PublishDate")
    public String publishDate;
    /**
     * 价格
     */
    @Column(name = "Price")
    public String price;
    /**
     * 借出次数
     */
    @Column(name = "BorrowedTimes")
    public Integer borrowedTimes = 0;
    /**
     * 借出次数
     */
    @Column(name = "HeartedTimes")
    public Integer heartedTimes = 0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CreateDate")
    public Date createDate;

    public static Book makeInstance(BookForm formData, LibraryType lt) {
        Book book = new Book();
        book.isbn = formData.isbn;
        book.bookname = formData.bookname;
        book.inputCode = formData.inputCode;
        book.author = formData.author;
        book.libraryType = lt;
        book.categoryCode = formData.categoryCode;
        book.category = formData.category;
        book.publisher = formData.publisher;
        book.publishAddr = formData.publishAddr;
        book.publishDate = formData.publishDate;
        book.price = formData.price;
        book.createDate = new Date();
        return book;
    }

}
