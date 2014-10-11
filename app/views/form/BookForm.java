package views.form;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.ValidationError;

public class BookForm {
    public Long id;
    /**
     * ISBN
     */
    public String isbn;
    /**
     * 书刊名称
     */
    public String bookname;
    /**
     * 输入码
     */
    public String inputCode;
    /**
     * 著者
     */
    public String author;
    /**
     * 馆藏类型
     */
    public Long libraryType;
    /**
     * 分类号
     */
    public String categoryCode;
    /**
     * 分类名称
     */
    public String category;
    /**
     * 出版单位
     */
    public String publisher;
    /**
     * 出版地
     */
    public String publishAddr;
    /**
     * 出版日期
     */
    public String publishDate;
    /**
     * 书刊定价
     */
    public String price;
    /**
     * 主题词
     */
    public String subjectTerm;
    /**
     * 丛编名
     */
    public String subseriesName;
    /**
     * 出版次数
     */
    public String publishTimes;
    /**
     * 书刊语言
     */
    public String bookLanguage;
    /**
     * 载体形态
     */
    public String entiretyShape;
    /**
     * 书刊附件
     */
    public String bookAccessory;
    /**
     * CN刊号
     */
    public String CNID;
    /**
     * 出版周期
     */
    public String publishPeriod;
    /**
     * 主办单位
     */
    public String hostUnit;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (isbn == null || isbn.length() == 0) {
            errors.add(new ValidationError("isbn", "ISBN不能为空."));
        }
        if (errors.size() > 0) return errors;
        return null;
    }
}
