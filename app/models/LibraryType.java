/*
* Copyright(c) FUJITSU LIMITED 2014. All Rights Reserved.
* @author FSNT)YangChao 2014.8.10.
*/

package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "LibraryType_tab")
public class LibraryType {
    @Id
    @GeneratedValue
    @Column(name = "LibraryTypeID")
    public Long id;
    /**
     * 馆藏类型名
     */
    @Column(name = "LibraryType", nullable = false)
    public String libraryType;
}
