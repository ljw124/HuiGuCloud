package com.dcdz.huigucloud.utils;

import android.content.Context;

import com.hikvision.hikfridgesdk.HikFridgeSDK;
import com.hikvision.hikfridgesdk.OnHikFridgeSdkInitCallback;
import com.hikvision.hikfridgesdk.OnImageUploadCallback;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by LJW on 2019/3/14.
 */
public class UploadImagesUtil {

    protected static Logger log = Logger.getLogger(UploadImagesUtil.class);
    private HikFridgeSDK hikFridgeSDK;
    private String devinfo = "/devinfo.txt";
    private String devid = "/dev_id";
    private String devmasterkey = "/dev_masterkey";
    Context context;

    private List<String> imageList = new ArrayList<>();// 图片路径

    public void uploadImage(Context context){
        this.context = context;
        // 一开始测试的时候SD卡的文件读写不了，所以把测试需要的文件都复制到了app内部存储路径下
        devinfo = context.getDir("hikfridge", MODE_PRIVATE) + devinfo;
        devid = context.getDir("hikfridge", MODE_PRIVATE) + devid;
        devmasterkey = context.getDir("hikfridge", MODE_PRIVATE) + devmasterkey;

        imageList.add(context.getDir("hikfridge", MODE_PRIVATE) + "/bq.jpg");
        imageList.add(context.getDir("hikfridge", MODE_PRIVATE) + "/ck03.jpg");
        imageList.add(context.getDir("hikfridge", MODE_PRIVATE) + "/ck05.jpg");
        imageList.add(context.getDir("hikfridge", MODE_PRIVATE) + "/ck10.jpg");

        copyFile();

        hikFridgeSDK = new HikFridgeSDK();
        log.info("SDK版本号:" + hikFridgeSDK.getVersion());
        /***************************注意******************************/
        // 本项目所使用的所有assets目录下的文件，是为了便于通过手机模拟
        // 测试而从冰柜的SD卡中拷贝过来的，实际使用本SDK的时候需要传入
        // 冰柜当中的真实文件
        hikFridgeSDK.init("licdev.ys7.com", 8666, devinfo, devid, devmasterkey, new OnHikFridgeSdkInitCallback() {
            @Override
            public void OnDeviceOnline() {
                // 初始化之后，需要等待设备上线之后才能进行图片上传操作
                // demo这里的{1,3,5,7}是随便写的，实际开发中要根据情况如实填写，保持cameraIndex和imageList顺序一致
                final long start = System.currentTimeMillis();
                hikFridgeSDK.uploadImages("test param", new int[]{1, 3, 5, 7}, imageList, new OnImageUploadCallback() {

                    @Override
                    public void onUploadImageSuccess(String s, List<String> list) {
                        long end = System.currentTimeMillis();
                        log.info("onUploadImageSuccess: 上传图片耗时:" + (float) (end - start) / 1000 + "秒");
                    }

                    @Override
                    public void onUploadSuccess() {
                        log.info("onUploadSuccess");
                    }

                    @Override
                    public void onUploadFail(String msg) {
                        log.info("onUploadFail: " + msg);
                    }
                });
            }

            @Override
            public void OnDeviceOffline(String msg) {
                log.info("OnDeviceOffline: " + msg);
            }


            @Override
            public void OnInitFail(String s) {
                log.info("OnInitFail: " + s);
            }
        });
    }

    /**
     * demo实现把SDK需要的初始化文件和测试图片放在了assets目录下，然后启动的时候会把
     * 这些文件复制到app的内部存储路径下，规避sd卡文件无法读写的问题
     *
     * @author Qian Sijianhao
     * @time 2018年05月11日
     */
    private void copyFile() {
        File file = context.getDir("hikfridge", MODE_PRIVATE);
        String dir = file.getPath();
        try {
            String[] assetsFiles = context.getResources().getAssets().list("");
            for (String s : assetsFiles) {
                file = new File(dir + "/" + s);
                if (!file.exists()) {
                    file.createNewFile();
                }
                InputStream is = null;
                FileOutputStream fos = null;
                byte[] buffer = new byte[2048];
                is = context.getResources().getAssets().open(s);
                fos = new FileOutputStream(file.getAbsolutePath());
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                is.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("文件复制出错：" + e.getMessage());
        }
    }

    public void finishTask(){
        hikFridgeSDK.killAllTask();
        hikFridgeSDK.fini();
    }
}
