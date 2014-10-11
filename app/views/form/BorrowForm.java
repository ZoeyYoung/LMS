package views.form;

import java.util.ArrayList;
import java.util.List;

import play.data.validation.ValidationError;

public class BorrowForm {
    /**
     * 书刊条码
     */
    public String counterpartCode;

    public String readerCode;

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (counterpartCode == null || counterpartCode.length() == 0) {
            errors.add(new ValidationError("counterpartCode", "No counterpartCode was given."));
        }
        if (errors.size() > 0) return errors;
        return null;
    }
}
