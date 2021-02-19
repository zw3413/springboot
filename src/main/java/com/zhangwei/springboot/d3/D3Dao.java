package com.zhangwei.springboot.d3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class D3Dao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void saveKmlFileInfo(String id, String fileName, String fileType, String filePath, String username) {

        String sql = "insert into d3_kml_file (id, filename, filetype, filepath,createuser, createtime) " +
                "values (?,?,?,?,?,now())";
        jdbcTemplate.update(sql, new Object[]{id, fileName, fileType, filePath, username});

    }

    public List<Map<String, Object>> list() {
        String sql = "select * from d3_kml_file";
        return jdbcTemplate.queryForList(sql);
    }

    public int del(String id) {
        String sql = "delete from d3_kml_file where id = ?";
        return jdbcTemplate.update(sql, new Object[]{id});
    }

    public List<Map<String, Object>> group() {
        String sql = "select * from d3_kml_file group by groupname";
        return jdbcTemplate.queryForList(sql);
    }

    public List<String> getGroupNames() {
        String sql = "select " +
                " case when groupname is null then 'KMZ/KML' else groupname end " +
                "           as groupname" +
                "             from (select distinct groupname from d3_kml_file ) a";
        List<Map<String,Object>> result = jdbcTemplate.queryForList(sql);
        List<String> list = new ArrayList<>();
        for(Map<String,Object> m : result){
            list.add((String)m.get("groupname"));
        }
        return list;
    }

    public List<Map<String, Object>> getFilesByGroupName(String groupName) {

        if(groupName!=null && !"KMZ/KML".equals(groupName)){

            String sql = "select * from d3_kml_file where groupname = ?";
            return jdbcTemplate.queryForList(sql,new Object[]{groupName});
        }else{
            String sql = "select id, filename as name  from d3_kml_file where groupname = '' or groupname is null";
            return jdbcTemplate.queryForList(sql);
        }

    }

    public Map<String, Object> getFileInfo(String id) {
        String sql ="select * from d3_kml_file where id = ?";
        return jdbcTemplate.queryForMap(sql, new Object[]{id});
    }
}
