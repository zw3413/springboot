package com.zhangwei.springboot.d3;

import com.alibaba.fastjson.JSONReader;
import com.at21.common.entity.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@RequestMapping("/d3/data")
public class D3EDataController {

    @Autowired
    private D3DataDao d3DataDao;


    SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDD");


    @RequestMapping(value = "/element", method = RequestMethod.PUT)

    public JsonResult putElement(@RequestBody Map<String, Object> params) {

        String id = (String) params.get("id");
        String name = (String) params.get("name");
        String geojson = (String) params.get("geojson");
        String type = (String) params.get("type");
        String style = null;
        if(params.get("style")!=null){
            style = (String) params.get("style");
        }

        d3DataDao.insert(id, name, geojson, type , style);

        return new JsonResult();
    }

    @RequestMapping(value = "/element", method = RequestMethod.POST)
    public JsonResult postElement(@RequestBody Map<String, Object> params) {


        String id = (String) params.get("id");
        String geojson = null, name = null, type = null, group=null , style=null;
        if (params.get("name") != null)
            name = (String) params.get("name");
        if (params.get("geojson") != null)
            geojson = (String) params.get("geojson");
        if (params.get("type") != null)
            type = (String) params.get("type");
        if (params.get("group") != null)
            group = (String) params.get("group");
        if (params.get("style") != null)
            style = (String) params.get("style");

        if (name != null && name.length() > 0)
            d3DataDao.updateNameById(id, name);
        if (geojson != null && geojson.length() > 0)
            d3DataDao.updateGeometryById(id, geojson);
        if (type != null && type.length() > 0)
            d3DataDao.updateTypeById(id, type);
        if (group != null && group.length() > 0)
            d3DataDao.updateGroupById(id, group);
        if (style != null && style.length() > 0)
            d3DataDao.updateStyleById(id, style);

        return new JsonResult();
    }


    @RequestMapping(value = "/element", method = RequestMethod.DELETE)
    public JsonResult dropElement(@RequestBody Map<String, Object> params) {
        String id = (String) params.get("id");

        d3DataDao.delete(id);

        return new JsonResult();
    }

    @RequestMapping(value = "/element/tree", method = RequestMethod.GET)
    public JsonResult elementTree() {

        //List<Map<String,Object>> tree = new ArrayList<>();
        List<Map<String,Object>> groups = d3DataDao.getGroups();

//
//
        if(groups==null ||groups.size()==0){
            Map<String,Object> group = new HashMap<>();
            group.put("name","default");
            groups.add(group);
        }

        for (Map<String,Object> group : groups) {
            String name =(String)group.get("name");
            List<Map<String, Object>> elements = d3DataDao.getElementsByGroupName(name);
            group.put("children", elements);
//            map.put("id", groupName);
//            map.put("name", groupName);
//            tree.add(map);
        }

        return new JsonResult(groups);
    }

    @RequestMapping(value = "/element/group", method = RequestMethod.GET)
    public JsonResult getGroups() {

        List<Map<String,Object>> groups =  d3DataDao.getGroups();
        List<String> groupNames = new ArrayList<>();
        for(Map<String,Object> map : groups){
            groupNames.add((String)map.get("name"));
        }
//        if (groupNames != null) {
//            String m =null;
//            for (String groupName : groupNames) {
//                if ("其他".equals(groupName)) {
//                    m = groupName;
//                }
//            }
//            groupNames.remove(m);
//        }
        return new JsonResult(groupNames);
    }

    @RequestMapping(value="/element/group", method = RequestMethod.PUT)
    public JsonResult createGroup(@RequestBody Map<String, Object> params){
        String id = (String)params.get("id");
        String name = (String)params.get("name");
        Integer order = (Integer)params.get("order");
        Integer checked= (Integer)params.get("checked");
        Integer open = (Integer)params.get("open");

        d3DataDao.insertGroup(id,name,order,checked,open);

        return new JsonResult();
    }
    @RequestMapping(value = "/element/group", method = RequestMethod.DELETE)
    public JsonResult dropGroup(@RequestBody Map<String, Object> params) {
        String id = (String) params.get("id");

        d3DataDao.deleteGroup(id);

        return new JsonResult();
    }

    @RequestMapping(value = "/element/{id}", method = RequestMethod.GET)
    public JsonResult getElementJSONById(@PathVariable("id") String id) {

        String geoJSON = d3DataDao.getGeoJSONById(id);

        Map<String, Object> geometryInfo = d3DataDao.getGeometryInfoById(id);

        if(geometryInfo!=null){
            geometryInfo.put("geoJson", geoJSON);
            return new JsonResult(geometryInfo);
        }else{
            return new JsonResult();
        }
    }

    @RequestMapping(value = "/element/geojson/{id}", method = RequestMethod.GET)
    public void getElementGeoJSONById(@PathVariable("id") String id, HttpServletResponse response) {

        String geoJSON = d3DataDao.getGeoJSONById(id);
        if (geoJSON != null && geoJSON.length() > 0) {
            String fileType = "json";
            // 写明要下载的文件的大小
            //response.setContentLength((int) file.length());
            response.setHeader("Content-Disposition", "attachment;filename="
                    + id + "." + fileType);// 设置在下载框默认显示的文件名
            response.setContentType("application/octet-stream");// 指明response的返回对象是文件流
            // 读出文件到response
            // 这里是先需要把要把文件内容先读到缓冲区
            // 再把缓冲区的内容写到response的输出流供用户下载

//            bufferedInputStream.read(b);
//            outputStream = response.getOutputStream();
            OutputStream outputStream = null;
            try {
                outputStream = response.getOutputStream();
                outputStream.write(geoJSON.getBytes());
            } catch (Exception e) {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }
}