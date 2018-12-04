package com.mmd.mjapp.plugin;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;

import java.util.Objects;


public class CusObjWrapper extends BeanWrapper {

    public CusObjWrapper(MetaObject metaObject, Object object) {
        super(metaObject, object);
    }


    @Override
    public void set(PropertyTokenizer prop, Object value) {
        if (Objects.equals(value, null)) {
            value = "";
        }
        super.set(prop, value);
    }
}
