package com.server.global.dialect;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.StandardBasicTypes;

public class CustomMySQL8Dialect extends MySQL8Dialect {

    public CustomMySQL8Dialect() {
        super();

        registerFunction("matchVideo", new SQLFunctionTemplate(StandardBasicTypes.DOUBLE, "MATCH(?1) AGAINST (?2 IN BOOLEAN MODE)"));
    }

}
