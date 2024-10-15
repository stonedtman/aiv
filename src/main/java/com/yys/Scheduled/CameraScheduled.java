package com.yys.Scheduled;

import com.yys.entity.AiCamera;
import com.yys.service.CameralistService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
public class CameraScheduled implements AutoCloseable {

    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Autowired
    private CameralistService cameralistService;

    @Autowired
    private MinioClient minioClient;
    /**
     * 定期同步每日数据到数据库
     * 通过使用定时任务注解 @Scheduled，确保该方法每两小时执行一次
     * 该方法的目的是从摄像头列表服务中获取所有AI摄像头信息，并为每个摄像头启动一个检查和同步任务
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void syncDailyDataToDatabase() {
        // 从摄像头列表服务中获取所有AI摄像头信息
        List<AiCamera> aiCameras = cameralistService.selectAicameralist();

        // 对摄像头列表进行并行流处理，为每个摄像头启动一个检查和同步的异步任务
        aiCameras.parallelStream().forEach(aiCamera -> {
            // 提交一个异步任务来检查和同步指定的AI摄像头数据
            executorService.submit(() -> checkAndSyncCamera(aiCamera));
            
        });
    }

    private void checkAndSyncCamera(AiCamera aiCamera) {
         try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(aiCamera.getVideoStreaming())) {
             grabber.setOption("rtsp_transport", "tcp");
            grabber.setOption("stimeout", "5000000");
            grabber.start();
            // 捕获帧
            Frame frame = grabber.grabImage();
            if (frame != null) {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bufferedImage = converter.convert(frame);
                if (bufferedImage != null) {
                    // 上传图片到 MinIO
                    String bucketName = "camera-covers";
                    String objectName = aiCamera.getCameraId() + ".jpg";
                    uploadImageToMinio(bucketName, objectName, bufferedImage);

                    // 更新摄像头的图像路径
                    aiCamera.setCameraImg("/" + bucketName + "/" + objectName);
                    cameralistService.updateCameraImg(aiCamera.getCameraId(), "/" + bucketName + "/" + objectName);
                }
            }


            grabber.stop();
            cameralistService.updateCamerStats(aiCamera.getCameraId(), 1);
        } catch (Exception e) {
            if (e instanceof FFmpegFrameGrabber.Exception) {
                cameralistService.updateCamerStats(aiCamera.getCameraId(), 0);
            } else {
                System.err.println("发生意外错误: " + e.getMessage());
            }
        }
    }
    @Override
    public void close() throws Exception {
        executorService.shutdown();
        while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
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

}

