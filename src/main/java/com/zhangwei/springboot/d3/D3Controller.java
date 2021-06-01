package com.zhangwei.springboot.d3;

import com.at21.common.entity.JsonResult;

import com.at21.common.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

//@CrossOrigin(allowCredentials="true",maxAge = 3600, value="*")
@RestController
@RequestMapping("/d3")
@ConfigurationProperties(prefix = "d3")
public class D3Controller {

    @Autowired
    private D3Service d3Service;

    @Autowired
    private D3Dao d3Dao;

    @Value("${d3.kml_root}")
    String kml_root;

    SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");


    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public JsonResult upload(@RequestParam("file") MultipartFile uploadFile, HttpServletRequest req) {
        try {
            String fileName, fileType, filePath, id;

            String rootPath;
            if (kml_root != null && kml_root.length() > 0) {
                rootPath = kml_root;
            } else {
                rootPath = req.getSession().getServletContext().getRealPath("/uploadFile/");
            }

            String dateFormat = sdf.format(new Date());
            File folder = new File(rootPath + dateFormat);
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }
            fileName = uploadFile.getOriginalFilename();
            fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toUpperCase();
            id = UUID.randomUUID().toString().replace("-", "");
            String saveName = id + "." + fileType;
            filePath = rootPath + dateFormat + File.separator + saveName;

            uploadFile.transferTo(new File(folder, saveName));

            String username = null;
            try {
                User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                username = user.getUsername(); //get logged in username
            } catch (Exception e) {
                //e.printStackTrace();
            }
            //保存文件信息
            d3Dao.saveKmlFileInfo(id, fileName, fileType, filePath, username);

            /**
             * 解析KML/KMZ文件需要服务器安装ogr2ogr工具，在mac系统下安装gdal并将其bin路径配置到path中即可
             */
            String pgHost = "localhost";
            String pgUsername = "postgres";
            String pgPassword = "postgres";
            String dbName = "springboot";
            String cmd = "ogr2ogr -f \"PostgreSQL\" PG:\"host=" + pgHost + " user=" + pgUsername + " dbname=" + dbName + " password=" + pgPassword + "\" " + filePath + " ";
            String tableName = "";
            if ("KML".equals(fileType)) {
                tableName = "d3_temp_kml";
            } else if ("GEOJSON".equals(fileType)) {
                tableName = "d3_temp_geojson";
            }else if("SHP".equals(fileType)){
                tableName="d3_temp_shapefile";
            }
            try {
                //cmd += "-nln " + tableName + " -nlt PROMOTE_TO_MULTI -append";//指定导入的表名
                cmd += "-nln " + tableName ;//指定导入的表名
                RunCommand runCommand = new RunCommand();
                runCommand.run(cmd);
              //  d3Dao.updateGeometryImportFileInfo(tableName, id, username);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            return new JsonResult(e);
        }
        return new JsonResult();
    }

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public JsonResult list() {

        List<Map<String, Object>> list = d3Dao.list();

        return new JsonResult(list);
    }


    @RequestMapping(value = "/group", method = RequestMethod.GET)
    public JsonResult group() {

        List<Map<String, Object>> groups = new ArrayList<>();

        List<String> groupNames = d3Dao.getGroupNames();
        for (String groupName : groupNames) {
            List<Map<String, Object>> groupFiles = d3Dao.getFilesByGroupName(groupName);
            Map<String, Object> map = new HashMap<>();
            map.put("children", groupFiles);
            map.put("id", groupName);
            map.put("name", groupName);
            groups.add(map);
        }
        return new JsonResult(groups);
    }

    @RequestMapping(value = "/del", method = RequestMethod.POST)
    public JsonResult del(@RequestBody List<String> ids) {

        for (String id : ids) {
            d3Service.deleteFile(id);

        }
        return new JsonResult();
    }

    @RequestMapping(value = "/data/kml/{id}", method = RequestMethod.GET)
    public void dataKml(@PathVariable("id") String id, HttpServletResponse response) {

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;
        OutputStream outputStream = null;
        try {
            //获取文件流
            Map<String, Object> fileInfo = d3Dao.getFileInfo(id);

            String filePath = (String) fileInfo.get("filepath");
            String fileType = (String) fileInfo.get("filetype");
            File file = new File(filePath);

            //将文件流通过response写出
            if (file.exists()) {
                // 写明要下载的文件的大小
                response.setContentLength((int) file.length());
                response.setHeader("Content-Disposition", "attachment;filename="
                        + id + "." + fileType);// 设置在下载框默认显示的文件名
                response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
                // 读出文件到response
                // 这里是先需要把要把文件内容先读到缓冲区
                // 再把缓冲区的内容写到response的输出流供用户下载
                fileInputStream = new FileInputStream(file);
                bufferedInputStream = new BufferedInputStream(
                        fileInputStream);
                byte[] b = new byte[bufferedInputStream.available()];
                bufferedInputStream.read(b);
                outputStream = response.getOutputStream();
                outputStream.write(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (bufferedInputStream != null) {
                    bufferedInputStream.close();
                }
                if (outputStream != null) {
                    outputStream.flush();
                    outputStream.close();
                }
            } catch (Exception e) {

            }
        }
    }

}