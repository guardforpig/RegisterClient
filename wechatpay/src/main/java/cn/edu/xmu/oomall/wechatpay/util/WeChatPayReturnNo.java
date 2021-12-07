package cn.edu.xmu.oomall.wechatpay.util;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
public enum WeChatPayReturnNo {

    //200
    OK("成功"),

    //400
    ORDER_CLOSED("订单已关闭"),
    ORDER_PAID("订单已支付"),
    PARAM_ERROR("参数错误"),

    //403
    OUT_TRADE_NO_USED("商户订单号重复"),
    USER_ACCOUNT_ABNORMAL("退款请求失败"),

    //404
    RESOURCE_NOT_EXISTS("查询的资源不存在"),

    //500
    SYSTEM_ERROR("系统错误");

    private String message;

    WeChatPayReturnNo(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }
}
