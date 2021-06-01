package com.zhangwei.springboot.d3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Map;

@Service
public class D3Service {
    @Autowired
    private D3Dao d3Dao;

    public void deleteFile(String id) {
        try {
            Map<String, Object> map = d3Dao.getFileInfo(id);
            if (map != null) {
                String filePath = (String) map.get("filepath");
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        d3Dao.del(id);
    }
}
