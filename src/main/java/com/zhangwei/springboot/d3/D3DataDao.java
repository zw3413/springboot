package com.zhangwei.springboot.d3;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.postgresql.util.PSQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class D3DataDao {

    @Autowired
    JdbcTemplate jdbcTemplate;


    public void insert(String id, String name, String geojson, String type, String style) {

        String sql = "insert into d3_geometry(id,name,type,geometry,createtime, updatetime, style)" +
                " values(?,?,?, ST_GeomFromGeojson(?) , now(), now() , ?)";

        System.out.println(geojson);
        jdbcTemplate.update(sql, new Object[]{id, name, type, geojson, style});

    }


    public void updateNameById(String id, String name) {
        String sql = " update d3_geometry set name = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{name, id});
    }
    public void updateGeometryById(String id, String geojson) {
        String sql = " update d3_geometry set geometry = ST_GeomFromGeojson(?) where id = ?";
        jdbcTemplate.update(sql, new Object[]{geojson, id});
    }
    public void updateTypeById(String id, String type) {
        String sql = " update d3_geometry set type = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{type, id});
    }
    public void updateGroupById(String id, String group) {
        String sql = " update d3_geometry set groupname = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{group, id});
    }
    public void updateStyleById(String id, String style){
        String sql = " update d3_geometry set style = ? where id = ?";
        jdbcTemplate.update(sql, new Object[]{style, id});
    }

    public void delete(String id) {
        String sql = " delete from d3_geometry where id = ?";
        jdbcTemplate.update(sql, new Object[]{id});
    }

    public List<Map<String, Object>> list() {
        return null;
    }

    public List<Map<String,Object>> getGroups() {
//        String sql = "select " +
//                " case when groupname is null then '其他' else groupname end " +
//                "           as groupname" +
//                "             from (select distinct groupname from d3_geometry ) a";
//        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
//        List<String> list = new ArrayList<>();
//        for (Map<String, Object> m : result) {
//            list.add((String) m.get("groupname"));
//        }

        String sql1="select id, name, grouporder as \"order\" , open, checked from d3_geometry_group order by grouporder";
        List<Map<String,Object>> result =jdbcTemplate.queryForList(sql1);
        return result;
    }

    public List<Map<String, Object>> getElementsByGroupName(String groupName) {

        if (groupName ==null || "其他".equals(groupName) ||  "default".equals(groupName)  ) {
            String sql = "select * from d3_geometry where groupname = ? or groupname is null or groupname = ''";
            return jdbcTemplate.queryForList(sql, new Object[]{groupName});
        } else {
            String sql = "select id, name  from d3_geometry where groupname = ? ";
            return jdbcTemplate.queryForList(sql, new Object[]{groupName});
        }

    }

    public String getGeoJSONById(String id) {

        String sql = "select ST_AsGeoJSON(geometry) from d3_geometry where id = ?";
        try {
            Object result = jdbcTemplate.queryForObject(sql, new Object[]{id}, String.class);
            String geoJSON = (String)result;
            return geoJSON;
        }catch (EmptyResultDataAccessException e){
            return null;
        }

    }

    public Map<String, Object> getGeometryInfoById(String id) {
        try {
            String sql = "select id,name,description, style from d3_geometry where id = ?";
            return jdbcTemplate.queryForMap(sql, new Object[]{id});
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }


    public void insertGroup(String id, String name, Integer order, Integer checked, Integer open) {
        if(id!=null && name!=null){
            if(order==null) order=0;
            if(checked==null) checked=0;
            if(open==null) open=0;
            String sql = "insert into d3_geometry_group(id,name,grouporder,checked,open) " +
                    " values(?,?,?,?,?) ";
            //System.out.println(sql);
            try {
                jdbcTemplate.update(sql, new Object[]{id, name, order, checked, open});
            }catch (Exception e){
                e.printStackTrace();
            }
            //jdbcTemplate.execute(sql);
        }
    }

    public void deleteGroup(String id) {
        String sql = "delete from d3_geometry_group where id = ?";
        jdbcTemplate.update(sql, new Object[]{id});
    }
}
