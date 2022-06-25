package org.ethanyuen.springboot.quartz.job;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import org.ethanyuen.springboot.module.FileModule;
import org.ethanyuen.springboot.utils.QiNiuFileUpUtil;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.time.LocalDateTime;
@Component
public class BackupDBJob {
    @Value("${spring.datasource.username}")
    String username;
    @Value("${spring.datasource.password}")
    String password;
    @Value("${mysqlpath}")
    String mysqlRoot;
    @Value("${db-name}")
    String databaseName;
    @Value("${backup-table-patten}")
    String regex;
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.driver-class-name}")
    String driver;
    @Scheduled(cron = "0 0/30 * * * ?")
    @SneakyThrows
    @ApiOperation("定时任务:每半小时备份数据库")
    public void execute(){
        LocalDateTime now = LocalDateTime.now();
        String prefix = now.getDayOfWeek().getValue() + "-" + now.getHour() + "-" + now.getMinute()+ databaseName;
        String fileName= prefix  + ".sql";
        Class.forName(driver).newInstance();
        Connection connection = DriverManager.getConnection(url, username, password);
        DatabaseMetaData dbMetaData = connection.getMetaData();
        ResultSet rs = dbMetaData.getTables(databaseName, null, null,new String[] { "TABLE" });
        StringBuilder stringBuilder = new StringBuilder();
        while (rs.next()) {
            String table_name = rs.getString("TABLE_NAME");
            if ( ReUtil.isMatch(regex, table_name)) {
                stringBuilder.append(table_name).append(" ");
            }
        }
        String tableName =stringBuilder.toString();
        String sqlpath = FileModule.PROFILEROOT+"backup/db";// 备份出来的sql地址
        File file = new File(sqlpath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String cmdStr = StrUtil.format("{}mysqldump --opt -h 127.0.0.1 --user={} --password={} --result-file={}/{} --default-character-set=utf8 {} {}", mysqlRoot, username, password, sqlpath, fileName, databaseName, tableName);
        Runtime cmd = Runtime.getRuntime();
        cmd.exec(cmdStr);
        if (StrUtil.isNotBlank(QiNiuFileUpUtil.ACCESS_KEY)&&StrUtil.isNotBlank(QiNiuFileUpUtil.SECRET_KEY)) {
            Thread.sleep(5 * 60 * 1000);
            String zipName=prefix+".zip";
            String parent = FileUtil.getParent(FileModule.PROFILEROOT, 1);
            String zipDir = parent + "/backup/"+databaseName+"/";
            ZipUtil.zip(FileModule.PROFILEROOT, zipDir + zipName);
            Thread.sleep(5 * 60 * 1000);
            new QiNiuFileUpUtil().upload(zipDir,zipName);
        }

    }
}
