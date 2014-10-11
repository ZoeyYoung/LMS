package views.table;

import models.Reader;
import utils.CommonUtils;

public class ReaderTable {
    public Long id;
    public String readerName;
    public String readerInputCode;
    public String readerCode;
    public String cardID;
    public String readerCateName;
    public String sex;
    public String birthDate;
    public String corpName;
    public String deptName;
    public String email;
    public String regDate;
    public String operator;

    public static ReaderTable makeInstantce(Reader reader) {
        ReaderTable readerTbl = new ReaderTable();
        readerTbl.id = reader.id;
        readerTbl.readerName = reader.readerName;
        readerTbl.readerInputCode = reader.readerInputCode;
        readerTbl.readerCode = reader.readerCode;
        readerTbl.cardID = reader.cardID;
        readerTbl.readerCateName = reader.readerCate.readerCateName;
        if (reader.sex != null) {
            readerTbl.sex = reader.sex.gender();
        }
        readerTbl.birthDate = CommonUtils.getDateInstance(reader.birthDate);
        readerTbl.corpName = reader.corpName;
        readerTbl.deptName = reader.deptName;
        readerTbl.email = reader.email;
        readerTbl.regDate = CommonUtils.getDateInstance(reader.regDate);
        readerTbl.operator = reader.operator;
        return readerTbl;
    }
}
