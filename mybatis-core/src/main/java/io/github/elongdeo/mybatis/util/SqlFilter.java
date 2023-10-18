package io.github.elongdeo.mybatis.util;

public class SqlFilter {
    private static final int SIZE = 128;
    private static final CharMasks MASKS = new CharMasks(128) {
        {
            this.addCharToMasks("\\\u0000\n\r'\"");
        }
    };
    private static final boolean[] ESCAPE_CMD_MASKS;
    private static final char BOUND = '\u001a';
    private static final String ESCAPED_BOUND = "0x1a";
    private static final CharMasks FILTER_ORDER_BY_MASKS;

    public static String trimSqlStatically(String columnName) {
        if (columnName != null && !columnName.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int lastPos = 0;

            for(int i = 0; i < columnName.length(); ++i) {
                char c = columnName.charAt(i);
                if (c >= 128 || !FILTER_ORDER_BY_MASKS.getMasks()[c]) {
                    sb.append(columnName, lastPos, i);
                    lastPos = i + 1;
                }
            }

            return sb.append(columnName.substring(lastPos)).toString();
        } else {
            return columnName;
        }
    }

    public static String filterStatically(String sql) {
        if (sql != null && !sql.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            int lastPos = 0;

            for(int i = 0; i < sql.length(); ++i) {
                char c = sql.charAt(i);
                if (c < 128 && ESCAPE_CMD_MASKS[c]) {
                    sb.append(sql, lastPos, i);
                    sb.append('\\').append(c);
                    lastPos = i + 1;
                } else if (c == 26) {
                    sb.append(sql, lastPos, i).append("0x1a");
                    lastPos = i + 1;
                }
            }

            return sb.append(sql.substring(lastPos)).toString();
        } else {
            return sql;
        }
    }

    static {
        ESCAPE_CMD_MASKS = MASKS.getMasks();
        FILTER_ORDER_BY_MASKS = new CharMasks(128) {
            {
                int i;
                for(i = 48; i < 58; ++i) {
                    this.addCharToMasks((char)i);
                }

                for(i = 65; i < 91; ++i) {
                    this.addCharToMasks((char)i);
                }

                for(i = 97; i < 123; ++i) {
                    this.addCharToMasks((char)i);
                }

                this.addCharToMasks("_-.");
            }
        };
    }

    public static void main(String[] args) {
        String input = "'order by count(1) desc; select * from table'";
        String output = SqlFilter.filterStatically(input);
        System.out.println(output);
        output = SqlFilter.trimSqlStatically(input);
        System.out.println(output);
    }
}