package views.form;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

public class LoginForm {
    @Required(message = "* 读者编号不能为空")
    public String readerCode;
    @Required(message = "* 密码不能为空")
    public String password;
    
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();

        if (errors.size() > 0) return errors;
        return null;
    }
}
