package org.ethanyuen.springboot.module;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.XmlUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.ethanyuen.springboot.annotation.SysLog;
import org.ethanyuen.springboot.bean.File;
import org.ethanyuen.springboot.bean.form.FileFormParam;
import org.ethanyuen.springboot.bean.query.FileQueryParam;
import org.ethanyuen.springboot.enums.BusinessType;
import org.ethanyuen.springboot.enums.Filetype;
import org.ethanyuen.springboot.utilbean.BaseCondition;
import org.ethanyuen.springboot.utilbean.Result;
import org.nutz.dao.Dao;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.upload.UploadAdaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.xpath.XPathConstants;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("file")
@Api(tags ="文件管理")
public class FileModule extends BaseModule<File> {
    public static  String PROFILEROOT ;
    static {
        ClassPathResource resource = new ClassPathResource("logback-spring.xml");
        Document document = null;
        try {
            document = XmlUtil.readXML(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Object fileRootPath = XmlUtil.getByXPath("//property[@name='fileRootPath']/attribute::value", document, XPathConstants.STRING);
        PROFILEROOT= (String) fileRootPath;
    }
    @Autowired
    Dao dao;
    @RequestMapping(method = RequestMethod.POST)
    @AdaptBy(type = UploadAdaptor.class, args = {"${app.root}/upload"})
    @SaCheckPermission("file:upload")
    @ApiOperation("上传文件")
    @SysLog(type = BusinessType.ADD)
    public Result uploadFile(FileFormParam fileInfo, @RequestParam("file") MultipartFile[] files) {
        Result result = new Result();
        if (files != null) {
            for (MultipartFile file : files) {
               /* String filePath = uploadFile(file, fileInfo.getFiletype());
                if (!StrUtil.isBlank(filePath)) {

                    fileInfo.setFilePath(filePath);
                    dao.insert(fileInfo);
                }*/
            }
        }
        return result.setOk();
    }

    @RequestMapping
    @SaCheckPermission("file:delete")
    @ApiOperation("删除文件")
    @SysLog(type = BusinessType.DELETE)
    public Result deleteFile(String ids) {
        return deleteEntities(ids);
    }

    @Ok("raw")
    @RequestMapping
    @SaCheckPermission("file:downloadFileByPath")
    @ApiOperation("根据路径下载文件")
    public Object downloadFileByPath(String filePath) {
        java.io.File file = new java.io.File(PROFILEROOT + filePath);
        return file;
    }
    @RequestMapping
    @SaCheckPermission("file:getFiles")
    @ApiOperation(value = "获取文件")
    public Result<File> getFiles(FileQueryParam condition, BaseCondition baseCondition) {
      return getEntities(condition,baseCondition);
    }
    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    public static String uploadFile(MultipartFile file, Filetype filetype) {
//        java.io.File tmpfile =new java.io.File(file.getInputStream() );
        if (filetype==null) {
            filetype= Filetype.OTHER;
        }
        String suffixRoot = filetype.name() + "/";
        return processFile(file, suffixRoot);
    }
    public static String uploadFile(MultipartFile file, String field) {
//        java.io.File tmpfile = file.getFile();
        if (StrUtil.isBlank(field)) {
            field="OTHER";
        }
        String suffixRoot = field + "/";
        return processFile(file, suffixRoot);
    }

    private static String processFile(MultipartFile tmpfile, String suffixRoot) {
        String uploadPath = PROFILEROOT + suffixRoot;
        java.io.File filedir = new java.io.File(uploadPath);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }
        String ext = FileUtil.getSuffix(tmpfile.getOriginalFilename());
        String fileName = UUID.randomUUID() + ext;
        String u_name = uploadPath + fileName;
        java.io.File tofile = new java.io.File(u_name);
        /*Files.copy(tmpfile, tofile);
        tmpfile.delete();*/
        return suffixRoot + fileName;
    }

}
