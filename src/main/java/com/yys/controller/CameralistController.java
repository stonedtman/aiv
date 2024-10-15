package com.yys.controller;

import com.alibaba.fastjson2.JSON;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.yys.util.MinioClientProvider;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.yys.entity.AiCamera;
import com.yys.entity.CameraSector;
import com.yys.entity.Result;
import com.yys.service.CameralistService;


import org.bytedeco.opencv.opencv_videostab.IFrameSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/sterams")
@CrossOrigin
public class CameralistController {

    @Autowired
    private CameralistService cameralistService;

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Autowired
    private MinioClient minioClient;

    @GetMapping("/getvideolist")
    public String getCameralist(
            @RequestParam(value = "gId", required = false) String gId,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize
    ){
        pageNum=(pageNum-1)*pageSize;
        Result result = cameralistService.selectCameralist(gId, pageNum,pageSize);
        return JSON.toJSONString(result);
    }

    @GetMapping("/getvideolistgroup")
    public String getCameralistGroup(
            @RequestParam(value = "groupName", required = false) String groupName,
            @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize
    ){
        pageNum=(pageNum-1)*pageSize;
        Result result = cameralistService.selectCameralistGroup(groupName, pageNum,pageSize);
        return JSON.toJSONString(result);
    }

    @GetMapping("/getgroups")
    public String getCameralistgroup(){
        Result result = cameralistService.selectGroups();
        return JSON.toJSONString(result);
    }

    //更新分组名
    @GetMapping("/updateGroup")
    public String updateGroup(@RequestParam(value = "groupId", required = false)String groupId,
                              @RequestParam(value = "groupName", required = false)String groupName){
        Result result=cameralistService.updateGroup(groupId, groupName);
        return JSON.toJSONString(result);
    }

    //获取分组信息
    @GetMapping("/getGroupMsg")
    public String getGroupMsg(String groupId){
        Result result = cameralistService.selectGroupMsg(groupId);
        return JSON.toJSONString(result);
    }

    @GetMapping("/deleteCameraGroup")
    public String deleteCameraGroup(String groupId){
        Result result = cameralistService.deleteCameraGroup(groupId);
        return JSON.toJSONString(result);
    }

    @GetMapping("/deleteCameraList")
    public String deleteCameraList(String cameraId){
        Result result = cameralistService.deleteCameraList(cameraId);
        return JSON.toJSONString(result);
    }

    @GetMapping("/addgroups")
    public String getCameralistCount(@RequestParam(value = "groupName", required = false) String groupName){
        int i = cameralistService.selectGroupExists(groupName);
        if (i != 0){
            return JSON.toJSONString(Result.success(302,"分组已存在",0,null));
        }
        CameraSector cameraSector = new CameraSector();
        cameraSector.setGroupId(generateGroupId());
        cameraSector.setGroupName(groupName);
        cameraSector.setCreateTime(getnowtime());
        int count = cameralistService.insertCameralistCount(cameraSector);
        if (count == 0){
            return JSON.toJSONString(Result.success("添加失败",0,null));
        }
        return JSON.toJSONString(Result.success("添加成功",1,null));
    }

    @PostMapping("/addCamera")
    public String addCamera(@RequestBody AiCamera aiCamera) {
        int i = cameralistService.selectCameraExists(aiCamera.getCameraLocation());
        if (i != 0) {
            Result result = Result.success(302, "摄像机点位已存在", 0, null);
            return JSON.toJSONString(result);
        }
        aiCamera.setCameraProtocol(getupagreement(aiCamera.getVideoStreaming()));
        aiCamera.setCameraId(generateCameraId());
        aiCamera.setWorkingTime(getnowtime());

        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(aiCamera.getVideoStreaming())) {
            grabber.setOption("rtsp_transport", "tcp");
            grabber.setOption("stimeout", "5000000");
            grabber.start();
            int width = grabber.getImageWidth();
            int height = grabber.getImageHeight();
            if (width >= 0 && height >= 0) {
                String resolution = getResolutionDescription(width, height);
                aiCamera.setCameraResolution(resolution);
                aiCamera.setCameraStatus(1);

                // 捕获帧并上传到 MinIO
                Frame frame = grabber.grabImage();
                if (frame != null) {
                    Java2DFrameConverter converter = new Java2DFrameConverter();
                    BufferedImage bufferedImage = converter.convert(frame);
                    if (bufferedImage != null) {
                        // 上传图片到 MinIO
                        String bucketName = "camera-covers";
                        String objectName = aiCamera.getCameraId() + ".jpg";
                        uploadImageToMinio(bucketName, objectName, bufferedImage);
                        aiCamera.setCameraImg("/" +bucketName + "/" + objectName);
                    }
                }
            } else {
                aiCamera.setCameraStatus(0);
            }
            grabber.stop();
        } catch (Exception e) {
            aiCamera.setCameraStatus(0);
            return JSON.toJSONString(Result.success(500, "添加失败,无法连接摄像头，请检查摄像头状态", 0, null));
        }

        cameralistService.insertCamera(aiCamera);
        return JSON.toJSONString(Result.success("添加成功", 1, null));
    }

    private void uploadImageToMinio(String bucketName, String objectName, BufferedImage image) throws Exception {
        // 检查桶是否存在，如果不存在则创建
        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!isExist) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        // 将 BufferedImage 转换为 InputStream
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", os);
        InputStream inputStream = new ByteArrayInputStream(os.toByteArray());

        // 上传图片到 MinIO
        minioClient.putObject(
                PutObjectArgs.builder().bucket(bucketName).object(objectName)
                        .stream(inputStream, os.size(), -1)
                        .contentType("image/jpeg")
                        .build()
        );
    }

    @PostMapping("/updateCamera")
    public String updateCamera(@RequestBody AiCamera aiCamera){

        int i = cameralistService.selectCameraGroupExists(aiCamera.getCameraLocation(), String.valueOf(aiCamera.getCameraGroup()));
        if (i != 0){
            Result result = Result.success(302,"该分组内已存在摄像机点位,修改失败",0,null);
            return JSON.toJSONString(result);
        }

        String VideoStreaming = cameralistService.selectCameraStream(aiCamera.getCameraId());
        if (!VideoStreaming.equals(aiCamera.getVideoStreaming())){
            //需要拼接 不需要拼接的话需要从流里获取端口号ip地址
            if (aiCamera.getTypeInput()==0){
                aiCamera.setVideoStreaming(aiCamera.getAgreement()+"://"+
                        aiCamera.getUserName()+":"+
                        aiCamera.getPassWord()+"@"+
                        aiCamera.getIpAddress()+":"+
                        aiCamera.getCameraPort()+"/"+
                        aiCamera.getAddress());
                aiCamera.setCameraProtocol(aiCamera.getAgreement());
            } else {
                aiCamera.setCameraPort(Integer.valueOf(extractPort(aiCamera.getVideoStreaming())));
                aiCamera.setCameraProtocol(getupagreement(aiCamera.getVideoStreaming()));
                aiCamera.setIpAddress(getIpAddress(aiCamera.getVideoStreaming()));
            }

            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(aiCamera.getVideoStreaming())) {
                grabber.setOption("stimeout", "3000000");
                grabber.start();
                int width = grabber.getImageWidth();
                int height = grabber.getImageHeight();
                if (width > 0 && height > 0) {
                    String resolution = getResolutionDescription(width, height);
                    aiCamera.setCameraResolution(resolution);
                    aiCamera.setCameraStatus(1);
                } else {
                    aiCamera.setCameraStatus(0);
                }
                grabber.stop();
            } catch (Exception e) {
                aiCamera.setCameraStatus(0);
                return JSON.toJSONString(Result.success(500,"修改失败,无法连接摄像头，请检查摄像头状态",0,null));

            }
        }
        cameralistService.updateCamera(aiCamera);
        return JSON.toJSONString(Result.success("修改成功",1,null));
    }

    //主页获取摄像头状态
    @GetMapping("/getCamerastus")
    public String getCamerastus(){
        int Camerasum=cameralistService.selectAicameralist().size();
        int working=cameralistService.selectWorkingCamera();

        String rate = "";
        Map<String, Object> map = new HashMap<>();
        map.put("Camerasum", Camerasum);
        map.put("working", working);
        if (Camerasum != 0) {
            double num = ((double) working) / Camerasum;
            rate = String.format("%.2f%%", num * 100);
            map.put("rate", rate);
        } else {
            return JSON.toJSONString(Result.success("还未添加摄像头",0,map));
        }

        return JSON.toJSONString(Result.success("获取成功",1,map));

    }

    //查询摄像头信息
    @GetMapping("/selectCameraMsg")
    public String selectCameraMsg(String cameraId) {
        Result result = cameralistService.selectCameraMsg(cameraId);
        return JSON.toJSONString(result);
    }

    @GetMapping("/selectCameraLocation")
    public String selectCameraLocation(){
        Result result = cameralistService.selectCameraLocation();
        return JSON.toJSONString(result);
    }

    //更新摄像头状态
    private String generateGroupId() {
        String oldId = cameralistService.selectGroupid();
        if (oldId == null || oldId.isEmpty()) {
            return "G001";
        }
        int numericPart = Integer.parseInt(oldId.substring(1)) + 1;
        return String.format("G%03d", numericPart);
    }
    private String generateCameraId() {
        String oldId = cameralistService.selectCameraid();
        if (oldId == null || oldId.isEmpty()) {
            return "C001";
        }
        int numericPart = Integer.parseInt(oldId.substring(1)) + 1;
        return String.format("C%03d", numericPart);
    }

    private String getnowtime(){
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }

    private static String getResolutionDescription(int width, int height) {
        double aspectRatio = (double) width / height;

        if (isClose(aspectRatio, 16.0 / 9.0)) {
            if (isClose(width, 3840) && isClose(height, 2160)) return "4K";
            if (isClose(width, 1920) && isClose(height, 1080)) return "1080p";
            if (isClose(width, 1280) && isClose(height, 720)) return "720p";
            if (isClose(width, 854) && isClose(height, 480)) return "480p";
            if (isClose(width, 640) && isClose(height, 360)) return "360p";
            if (isClose(width, 426) && isClose(height, 240)) return "240p";
        } else if (isClose(aspectRatio, 4.0 / 3.0)) {
            if (isClose(width, 1440) && isClose(height, 1080)) return "1080p";
            if (isClose(width, 960) && isClose(height, 720)) return "720p";
            if (isClose(width, 640) && isClose(height, 480)) return "480p";
        } else if (isClose(aspectRatio, 2592.0 / 1944.0)) {
            return "5MP";
        } else if (isClose(aspectRatio, 2592.0 / 1458.0)) {
            return "2K (2592x1458)";
        }

        return width + "x" + height;
    }

    private static boolean isClose(double value1, double value2) {
        double tolerance = 0.01;
        return Math.abs(value1 - value2) < tolerance;
    }

    //获取端口号
    public static String extractPort(String rtspUrl) {
        int thirdColon = 0;
        int thirdSlash = 0;
        int colonCount = 0;
        int slashCount = 0;

        // 遍历字符串，找到第三个冒号和第三个斜线的位置
        for (int i = 0; i < rtspUrl.length(); i++) {
            char ch = rtspUrl.charAt(i);
            if (ch == ':') {
                colonCount++;
                if (colonCount == 3) {
                    thirdColon = i;
                }
            } else if (ch == '/') {
                slashCount++;
                if (slashCount == 3) {
                    thirdSlash = i;
                    break; // 找到第三个斜线后，可以提前退出循环
                }
            }
        }

        // 使用substring()方法获取端口号
        if (thirdColon != 0 && thirdSlash != 0) {
            // 注意，substring()的结束位置是不包含的，因此需要加1
            return rtspUrl.substring(thirdColon + 1, thirdSlash);
        } else {
            // 如果找不到第三个冒号或第三个斜线，返回空字符串或抛出异常
            return "";
        }
    }

    //获取大写
    public static String getupagreement(String originalString) {
        String firstFourChars = originalString.substring(0, 4); // 获取前四位字符
        String upperCaseFirstFour = firstFourChars.toUpperCase();
        return upperCaseFirstFour;
    }

    public static String getIpAddress(String rtspUrl) {
        String regex = "(?<=@)(.*?)(?=:)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(rtspUrl);
        if (matcher.find()) {
            String result = matcher.group();
            return result;
        } else {
           return "";
        }
    }
}
