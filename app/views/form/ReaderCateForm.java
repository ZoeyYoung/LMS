package views.form;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

public class ReaderCateForm {
    public Long id;
    @Required(message = "读者类别名不能为空")
    public String readerCateName;
    @Required(message = "可借书数不能为空，且为数值")
    @Min(value = 0, message = "可借书数不能为负")
    public Integer limitBooksCount;
    @Required(message = "可借天数不能为空，且为数值")
    @Min(value = 0, message = "可借天数不能为负")
    public Integer limitDays;
    @Required(message = "可续借次数不能为空，且为数值")
    @Min(value = 0, message = "可续借次数不能为负")
    public Integer reLoanTimes;
    @Required(message = "每次可续借天数不能为空，且为数值")
    @Min(value = 0, message = "每次可续借天数不能为负")
    public Integer reLoanDays;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        if (errors.size() > 0) return errors;
        return null;
    }
}
