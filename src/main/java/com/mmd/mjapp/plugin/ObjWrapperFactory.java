package com.mmd.mjapp.plugin;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * Created by Administrator on 2017/7/13.
 */
public class ObjWrapperFactory implements ObjectWrapperFactory {

    @Override
    public boolean hasWrapperFor(Object o) {
        return o != null && o.getClass().getPackage().getName().contains("com.mmd.mjapp.model");
    }

    @Override
    public ObjectWrapper getWrapperFor(MetaObject metaObject, Object o) {
        return new CusObjWrapper(metaObject, o);
    }
}
