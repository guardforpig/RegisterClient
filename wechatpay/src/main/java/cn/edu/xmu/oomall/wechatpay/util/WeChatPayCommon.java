package cn.edu.xmu.oomall.wechatpay.util;

import cn.edu.xmu.oomall.core.model.VoObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ziyi guo
 * @date 2021/12/2
 */
public class WeChatPayCommon {

    public static Object decorateReturnObject(WeChatPayReturnObject returnObject) {

        Map<String, Object> obj = new HashMap<>();
        switch (returnObject.getCode()) {
            case ORDER_NO_TEXIST:
                // 404
                obj.put("errmsg", returnObject.getErrmsg());
                return new ResponseEntity(obj, HttpStatus.NOT_FOUND);

            case SYSTEM_ERROR:
                // 500
                obj.put("errmsg", returnObject.getErrmsg());
                return new ResponseEntity(obj,HttpStatus.INTERNAL_SERVER_ERROR);

            case ORDER_CLOSED:
            case PARAM_ERROR:
                // 400
                obj.put("errmsg", returnObject.getErrmsg());
                return new ResponseEntity(obj,HttpStatus.BAD_REQUEST);

            case OUT_TRADE_NO_USED:
            case USER_ACCOUNT_ABNORMAL:
                //403
                obj.put("errmsg", returnObject.getErrmsg());
                return new ResponseEntity(obj,HttpStatus.FORBIDDEN);

            default:
                // 200
                Object data = returnObject.getData();
                obj.put("data", data);
                return new ResponseEntity(obj,HttpStatus.OK);
        }
    }

    public static WeChatPayReturnObject getRetObject(WeChatPayReturnObject<VoObject> returnObject) {
        WeChatPayReturnNo code = returnObject.getCode();
        switch (code){
            case OK:
                VoObject data = returnObject.getData();
                if (data != null){
                    Object voObj = data.createVo();
                    return new WeChatPayReturnObject(voObj);
                }else{
                    return new WeChatPayReturnObject();
                }
            default:
                return new WeChatPayReturnObject(returnObject.getCode(), returnObject.getErrmsg());
        }
    }

}
