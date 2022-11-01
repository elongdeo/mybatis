package io.github.elongdeo.mybatis.constants;

public enum BaseDoPropertyEnum {
    GMT_CREATE("gmtCreate","gmt_create"),
    CREATOR("creator", "creator"),
    GMT_MODIFIED("gmtModified", "gmt_modified"),
    MODIFIER("modifier", "modifier"),
    ENABLE("enable", "is_enable");
    private String propertyName;
    private String columnName;

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    BaseDoPropertyEnum(String propertyName, String columnName) {
        this.propertyName = propertyName;
        this.columnName = columnName;
    }
}
