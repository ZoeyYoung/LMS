package views.form;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.Constraints.Required;
import play.data.validation.ValidationError;

public class AdminLoginForm {
    @Required(message = "* 帐号不能为空")
    public String adminName;
    @Required(message = "* 密码不能为空")
    public String adminPassword;
    
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (errors.size() > 0) return errors;
        return null;
    }
}
