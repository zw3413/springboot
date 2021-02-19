package com.zhangwei.springboot.d3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RunCommand {
    public static void main(String[] args) {
        RunCommand s = new RunCommand();
        s.run("ping 127.0.0.1");
    }
    public void run(String cmd){
        Runtime run =Runtime.getRuntime();
        try {
            Process p = run.exec(cmd);
            InputStream ins= p.getInputStream();
            InputStream ers= p.getErrorStream();
            new Thread(new inputStreamThread(ins)).start();
            p.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    class inputStreamThread implements Runnable{
        private InputStream ins = null;
        private BufferedReader bfr = null;
        public inputStreamThread(InputStream ins){
            this.ins = ins;
            this.bfr = new BufferedReader(new InputStreamReader(ins));
        }
        @Override
        public void run() {
            String line = null;
            byte[] b = new byte[100];
            int num = 0;
            try {
                while((num=ins.read(b))!=-1){
                    System.out.println(new String(b,"gb2312"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
