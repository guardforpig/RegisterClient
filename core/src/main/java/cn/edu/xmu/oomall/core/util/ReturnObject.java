package cn.edu.xmu.oomall.core.util;

import cn.edu.xmu.privilegegateway.annotation.util.InternalReturnObject;
import lombok.Getter;

/**
 * 返回对象
 * @author Ming Qiu
 **/
@Getter
public class ReturnObject<T> {

    /**
     * 错误号
     */
    ReturnNo code = ReturnNo.OK;

    /**
     * 自定义的错误码
     */
    String errmsg = null;

    /**
     * 返回值
     */
    private T data = null;

    /**
     * 默认构造函数，错误码为OK
     */
    public ReturnObject() {
    }

    /**
     * 带值构造函数
     * @param data 返回值
     */
    public ReturnObject(T data) {
        this();
        this.data = data;
    }

    /**
     * 有错误码的构造函数
     * @param code 错误码
     */
    public ReturnObject(ReturnNo code) {
        this.code = code;
    }

    /**
     * 有错误码和自定义message的构造函数
     * @param code 错误码
     * @param errmsg 自定义message
     */
    public ReturnObject(ReturnNo code, String errmsg) {
        this(code);
        this.errmsg = errmsg;
    }

    /**
     * 有错误码，自定义message和值的构造函数
     * @param code 错误码
     * @param data 返回值
     */
    public ReturnObject(ReturnNo code, T data) {
        this(code);
        this.data = data;
    }

    /**
     * 有错误码，自定义message和值的构造函数
     * @param code 错误码
     * @param errmsg 自定义message
     * @param data 返回值
     */
    public ReturnObject(ReturnNo code, String errmsg, T data) {
        this(code, errmsg);
        this.data = data;
    }


    /**
     * 错误信息
     * @return 错误信息
     */
    public String getErrmsg() {
        if (null != this.errmsg) {
            return this.errmsg;
        }else{
            return this.code.getMessage();
        }
    }
    public ReturnObject(InternalReturnObject<T> internalReturnObject){
        this.code=ReturnNo.getReturnNoByCode(internalReturnObject.getErrno());
        this.errmsg=internalReturnObject.getErrmsg();
        this.data=internalReturnObject.getData();
    }
}
