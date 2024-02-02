package com.kws.mybatis.config;

import com.kws.annotation.EnumValueMarkerFinder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author kws
 * @date 2024-01-14 16:04
 */
@Slf4j
public class MyBatisEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

    private final Class<E> type;
    public MyBatisEnumTypeHandler(Class<E> type) {
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        try {
            Field field = EnumValueMarkerFinder.find(type);
            Object val = field.get(parameter);
            if (jdbcType == null) {
                ps.setObject(i, val);
            } else {
                ps.setObject(i, val, jdbcType.TYPE_CODE);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object s = rs.getObject(columnName);
        return findTargetEnum(s, type);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object s = rs.getObject(columnIndex);
        return findTargetEnum(s, type);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object s = cs.getObject(columnIndex);
        return findTargetEnum(s, type);
    }


    private E findTargetEnum(Object val, Class<E> type) {
        if (val == null) {
            return null;
        }
        try {
            Field field = EnumValueMarkerFinder.find(type);
            for (E enumConstant : type.getEnumConstants()) {
                Object o = field.get(enumConstant);
                if (val.equals(o)) {
                    return enumConstant;
                }
            }
        } catch (IllegalAccessException e) {
            log.error("Handle enum failed...", e);
        }
        return null;
    }

}
