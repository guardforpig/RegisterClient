package cn.edu.xmu.oomall.alipay.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;

/**
 * 用于将json转为vo对象
 */
public class GetJsonInstance {

    public static Object getInstance(String jsonString, Class boClass) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Object bo = objectMapper.readValue(jsonString, boClass);
            return bo;
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    /**
     * json 转 vo
     * json中命名为下划线
     * 对象中为驼峰
     * @param jsonString
     * @return
     */
    public static Object getInstance1(String jsonString,Class voClass)
    {
        try {
            Object newVo = voClass.getDeclaredConstructor().newInstance();
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(jsonString);
            Field[] voFields = voClass.getDeclaredFields();
            for(Field voField : voFields)
            {
                String fieldName=voField.getName();
                StringBuffer stringBuffer = new StringBuffer();
                for (int i = 0; i < fieldName.length(); i++) {
                    //如果为驼峰，在其前面加入下划线，并将其转为lowercase
                    if(Character.isUpperCase(jsonString.charAt(i)))
                    {
                        stringBuffer.append('_');
                        stringBuffer.append(Character.toLowerCase(jsonString.charAt(i)));
                    }
                    else {
                        stringBuffer.append(jsonString.charAt(i));
                    }
                }
                String newFieldName=stringBuffer.toString();
                Object value = null;
                try{
                    if("Long".equals(voField.getType().getSimpleName()))
                    {
                        value = Long.valueOf(root.get(newFieldName).asText());
                    }
                    else{
                        value = root.get(newFieldName).asText();
                    }
                }catch (Exception e)
                {
                    //在json中找不到
                    voField.set(newVo,null);
                    continue;
                }
                voField.set(newVo,value);
            }
            return newVo;
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
