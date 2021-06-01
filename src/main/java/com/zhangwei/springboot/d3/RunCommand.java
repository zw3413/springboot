package com.zhangwei.springboot.d3;

import com.at21.common.exception.ServiceException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunCommand {
    public static void main(String[] args) {
        RunCommand s = new RunCommand();
        s.run("ping 127.0.0.1");
    }
    public void run(String cmd) throws ServiceException{
        Runtime run =Runtime.getRuntime();
        String line = "";
        try {
            System.out.println("执行命令："+cmd);
            String[] command = {"/bin/sh", "-c", cmd};
            Process p = run.exec(command);
//            InputStream ins= p.getInputStream();
            InputStream ers= p.getErrorStream();

            byte[] b = new byte[100];
            int num = 0;
            try {
                while((num=ers.read(b))!=-1){
                    line+=new String(b , "gb2312")+"\\\n";
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            throw new ServiceException("导入命令执行异常。",e);
        }
        if(line.length()>0){
            throw new ServiceException(line);
        }
    }

}
