package oop.fun.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: frandy
 * Date: 2021/4/22
 * Time: 下午3:25
 */
@Setter
@Getter
public class SubBank  {
    private String province;
    private String city;
    private String bankId;
    private String bankName;
    private String subBankName;
    //联行号
    private String unionBankNo;
}
