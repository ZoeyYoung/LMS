package views.form;

import play.data.validation.Constraints.Required;

public class PointForm {
    @Required(message = "* 读者编号不能为空")
    public String readerCode;
    @Required(message = "* 积分类型不能为空")
    public String ptype;
    @Required(message = "* 来源不能为空")
    public String source;
    @Required(message = "* 积分不能为空")
    public Long point;

}
