package views.form;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

public class OrderForm {
    /**
     * ISBN
     */
    @Required(message = "ISBN不能为空.")
    public String isbn;
    /**
     * 书刊名称
     */
    @Required(message = "书名不能为空.")
    public String bookname;
    /**
     * 著者
     */
    public String author;
    /**
     * 译者
     */
    public String translator;
    /**
     * 分类名称
     */
    public String category;
    /**
     * 出版单位
     */
    public String publisher;
    /**
     * 价格
     */
    public String price;
    /**
     * 购买理由
     */
    @Required(message = "购买理由不能为空.")
    public String reason;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        return errors.isEmpty() ? null : errors;
    }
}
