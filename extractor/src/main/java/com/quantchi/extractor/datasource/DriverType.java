package com.quantchi.extractor.datasource;

import com.quantchi.extractor.exception.UnSupportedJdbcTypeException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum DriverType {
    //todo 检查不同版本的服务器与驱动之间的兼容性
    MYSQL("com.mysql.cj.jdbc.Driver","jdbc:mysql://${host}:${port}/${database}", 3306),
    ORACLE("oracle.jdbc.driver.OracleDriver","jdbc:oracle:thin:@${host}:${port}:${database}", 1521),
    /**
     * jdbc连接DB2主要为两种方式
     * Type2：url形式如 ：jdbc:db2:DBName,需要依赖本地库文件。
     * Type4：url形式如：jdbc:db2://ip:port/DBName,与Type2形式的主要区别也就是多了IP/Port这种直连形式，纯java实现的，无需依赖本地库文件
     */
    DB2("com.ibm.db2.jcc.DB2Driver","jdbc:db2://${host}:${port}/${database}",50000),
    SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver","jdbc:sqlserver://${host}:${port};DatabaseName=${database}",1433),
    SYBASE("com.sybase.jdbc.SybDriver","jdbc:sybase:Tds:${host}:${port}/${database}",5000),
    //another SQLSERVER And SyBase Driver
    JTDS_SQLSERVER("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sqlserver://${host}:${posrt}/${database}", 1433),
    JTDS_SYBASE("net.sourceforge.jtds.jdbc.Driver","jdbc:jtds:sybase://${host}:${port}/${database}", 5000),
    POSTGRESQL("org.postgresql.Driver","jdbc:postgresql://${host}:${port}/${database}",5432),
    HIVE("org.apache.hive.jdbc.HiveDriver","jdbc:hive2://${host}:${port}/${database}",10000),
    IMPALA("com.cloudera.impala.jdbc41.Driver","jdbc:impala://${host}:${port}/${database}",21050),
    ODBC("sun.jdbc.odbc.JdbcOdbcDriver","jdbc:odbc:${database}",0);

    private String driver;
    private String pattern;
    private Integer port;

    DriverType(String driver, String pattern, Integer port) {
        this.driver = driver;
        this.pattern = pattern;
        this.port = port;
    }

    public String getPattern(){
        return pattern;
    }

    public String getDriver() {
        return driver;
    }

    public Integer getPort() {
        return port;
    }

    public static DriverType judgeDriver(String url){
        url = url.trim().toLowerCase();
        if (url.startsWith("jdbc")){
            Pattern jdbcPat = Pattern.compile("jdbc:(\\S+?):");
            Matcher matcher = jdbcPat.matcher(url);
            if (matcher.find()){
                String driver = matcher.group(1);
                switch (driver.toLowerCase()){
                    case "mysql":
                        return MYSQL;
                    case "oracle":
                        return ORACLE;
                    case "sybase":
                        return SYBASE;
                    case "microsoft":
                    case "sqlserver":
                        return SQLSERVER;
                    case "jtds":
                        if (url.contains("sqlserver")){
                            return JTDS_SQLSERVER;
                        } else if (url.contains("sybase")){
                            return JTDS_SYBASE;
                        }
                        break;
                    case "db2":
                        return DB2;
                    case "postgresql":
                        return POSTGRESQL;
                    case "hive":
                    case "hive2":
                        return HIVE;
                    case "impala":
                        return IMPALA;
                }
            }
        }
        throw new UnSupportedJdbcTypeException(url + " is not a valid jdbc connect string");
    }

    public static String getUrl(DriverType driver, String host, Integer port, String db){
        String url = driver.getPattern();
        url = url.replace("${host}", host);
        url = url.replace("${port}", checkPortValid(port)? port.toString(): driver.getPort().toString());
        url = url.replace("${database}", db==null?"":db);
        return url;
    }

    private static boolean checkPortValid(Integer port){
        if (port==null){return false;}
        return port>=1024 && port <= 49151;
    }
}
