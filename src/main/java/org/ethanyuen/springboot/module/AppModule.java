package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.extra.qrcode.QrCodeUtil;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.bean.App;
import org.ethanyuen.springboot.bean.BaseEntity;
import org.ethanyuen.springboot.bean.form.AppFormParam;
import org.ethanyuen.springboot.bean.query.AppQueryParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.Result;
import org.ethanyuen.springboot.utils.SQLUtil;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.upload.UploadAdaptor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@Api(tags = "app管理")
@RestController
@RequestMapping("app")
public class AppModule extends BaseModule<App> {

    @RequestMapping
    @SaCheckPermission("app:setApp")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("设置app")
    public Result setApp(AppFormParam app) {
        return setEntity(app);
    }

    @RequestMapping
    @SaCheckPermission("app:deleteApp")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除app")
    public Result deleteApp(String ids) {
        return deleteEntities(ids);
    }
    @ApiOperation("获取app列表")
    @RequestMapping
    @SaCheckPermission("app:getApp")
    public Result<App> getApp(AppQueryParam condition, BaseCondition baseCondition) {
        return getEntities(condition, baseCondition);
    }

    @RequestMapping(method = RequestMethod.POST)
    @SaCheckPermission("app:uploadFile")
    @SysLog(type = BusinessType.SET)
    @ApiOperation("上传app文件")
    @AdaptBy(type = UploadAdaptor.class)
    public Result uploadFile(@Param("file")MultipartFile file, Long id,String fileField) {
        return uploadSingleFile(id,file,fileField);
    }

    @RequestMapping
    @SaCheckPermission("app:deleteFile")
    @SysLog(type = BusinessType.DELETE)
    @ApiOperation("删除app文件")
    @ApiOperationSupport(includeParameters = {BaseEntity.Fields.id,"fileField"})
    public Result deleteFile(Long id,String fileField) {
        return deleteSingleFile(id,fileField);
    }


    @RequestMapping
    @ApiOperation("检查app是否需要更新")
    public Result checkAppUpdate(String appId, Long versionCode) {
        Result result = new Result();
        App app = dao.fetch(App.class, SQLUtil.baseCnd().and("appId", "=", appId));
        if (app == null) {
            result.setBad("该app数据不存在");
        } else {
            if (versionCode < app.getVersionCode()) {
                result.setObj(app);
                result.setOk();
            } else {
                result.setBad("当前已是最新版本");
            }
        }
        return result;
    }

    @SneakyThrows
    @RequestMapping(method = RequestMethod.POST)
    @SaCheckPermission("app:decodeQrCode")
    @ApiOperation("解析二维码")
    public Result decodeQrCode(@RequestParam("file") MultipartFile file) {
        Result re = new Result();
        re.setObj(QrCodeUtil.decode(file.getInputStream()));
        return re.setOk();
    }

}
