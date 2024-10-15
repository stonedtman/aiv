package com.yys.controller;

import com.alibaba.fastjson2.JSON;
import com.yys.entity.*;
import com.yys.service.CameralistService;
import com.yys.service.CreatedetectiontaskService;
import com.yys.service.ModelPlanService;
import com.yys.service.StreamService;
import com.yys.util.textimgUtil;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/plan")
@CrossOrigin
public class ModelPlanController {

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Value("${stream.minio.imgbucket.name}")
    private String bucketName;
    @Value("${file.uploaddir}")
    private String upload_dir;
    @Value("${file.upload-img}")
    private String upload_img;

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private StreamService streamService;

    @Autowired
    private CameralistService cameraService;

    @Autowired
    private ModelPlanService modelPlanService;

    @Autowired
    private CreatedetectiontaskService createdetectiontaskService;

    @GetMapping("/getPlans")
    public String getPlans(@RequestParam(value = "scene", required = false) String scene) {
        Result rs = modelPlanService.getMainMsgList(scene);
        return JSON.toJSONString(rs);
    }

    @GetMapping("/getModelPlanbyid")
    public String getModelPlan(@RequestParam(value = "Id", required = false) Integer Id) {
        Result rs = modelPlanService.getModelPlan(Id);
        return JSON.toJSONString(rs);
    }

    @GetMapping("/getModelTypes")
    public String getModelTypes(){
        Result rs = modelPlanService.getModelTypes();
        return JSON.toJSONString(rs);
    }


    @PostMapping("/saveModelMsg")
    public String handleFileUpload(@ModelAttribute MultipartFile modelFile) throws IOException {

        if (!modelFile.getOriginalFilename().endsWith(".zip")) {
            return JSON.toJSONString(Result.success(500, "上传的文件不是zip格式，请重新上传！",1, "上传失败"));
        }
        if (modelFile.isEmpty()) {
            return JSON.toJSONString(Result.success(500, "上传的文件为空，请重新上传！",1, "上传失败"));
        }

        // 保存上传的压缩包到服务器
        Path zipFilePath = Paths.get(uploadDir, modelFile.getOriginalFilename());

        // 先检查目录是否存在，如果不存在则创建
        Files.createDirectories(zipFilePath.getParent());


        try (FileOutputStream fos = new FileOutputStream(zipFilePath.toFile())) {
            fos.write(modelFile.getBytes());
        } catch (IOException e) {
            throw new IOException("保存上传文件失败：" + e.getMessage(), e);
        }
        // 解压文件
        unzipFile(zipFilePath.toFile(), uploadDir);

        // 清理已处理的文件
        cleanupProcessedFiles(zipFilePath);

        return JSON.toJSONString(Result.success(200,"上传成功",1, "上传成功"));
    }


    /**
     * 上传图片并预测结果.
     * @param imgFile 上传的图片文件
     * @param Id 模型商城id
     * @return 处理结果
     */
    @SneakyThrows
    @PostMapping("/getImgMsg")
    public String getImgMsg(@RequestParam("file") MultipartFile imgFile,
                            @RequestParam("Id") Integer Id) {

        ModelPlan modelPlan = modelPlanService.gettheModelPlan(Id);

        Integer id = Integer.valueOf(modelPlan.getModelId());

        AiModels aiModels = createdetectiontaskService.selectModelById(id);

        String modelPath =  aiModels.getModelLocation()+"/"+aiModels.getModel();
        // 创建目标文件夹
        File folder = new File(upload_img);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        // 获取文件名
        String fileName = imgFile.getOriginalFilename();

        // 定义保存路径
        Path targetPath = Paths.get(upload_img, fileName);

        // 将文件保存到指定路径
        Files.write(targetPath, imgFile.getBytes());

        File img = new File(String.valueOf(targetPath));

        List<String> list=streamService.getimgmsg(modelPath,img);

        if (list.isEmpty()){
            return JSON.toJSONString(Result.success(200, "未检测到目标，请重新上传！",1, "解析失败，未检测到目标，请重新上传！"));
        }

        LableResult lableResult = textimgUtil.getText(list);

        return JSON.toJSONString(Result.success(200, "解析成功",1, lableResult));
    }

    //卸载商城文件
    @GetMapping("/unload")
    public String unload(@RequestParam("Id") Integer Id){

        //删除数据库记录
        ModelPlan modelPlan = modelPlanService.gettheModelPlan(Id);

        Integer id = Integer.valueOf(modelPlan.getModelId());

        AiModels aiModels = createdetectiontaskService.selectModelById(id);

        String modelPath =  aiModels.getModelLocation()+"/"+aiModels.getModel();

        //删除模型管理记录
        if (modelPlanService.deleteModelPlan(Id)==0){
            return JSON.toJSONString(Result.success(500, "删除数据库记录失败！",1, "删除失败"));
        }

        //删除模型记录
        if (createdetectiontaskService.deleteModelPlan(id)==0){
            return JSON.toJSONString(Result.success(500, "删除模型管理记录失败！",1, "删除失败"));
        }

        //删除python端文件

        if (!streamService.deleteModel(modelPath)){
            return JSON.toJSONString(Result.success(500, "删除模型失败！",1, "删除失败"));
        }


        return JSON.toJSONString(Result.success(200, "卸载成功",1, "卸载成功"));
    }

    private void unzipFile(File zipFile, String outputDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFile.toPath()))) {
            ZipEntry zipEntry;
            Integer aimodelid=null;
            JsonMsg jsonMsg=null;
            List<String> modelTypes = new ArrayList<>();
            while ((zipEntry = zis.getNextEntry()) != null) {
                String fileName = zipEntry.getName();
                Path newPath = Paths.get(outputDir, fileName);

                if (zipEntry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);

                    // 根据文件类型处理
                    if (fileName.endsWith(".pt")) {
                        aimodelid=processModelFile(newPath.toFile());
                    } else if (fileName.endsWith(".json")) {
                        jsonMsg=processJsonFile(newPath.toFile());
                    } else if (isImageFile(fileName)) {
                        modelTypes.add(processImageFile(newPath.toFile()));
                    }
                }
            }
            if(aimodelid!=null){
                //更新模型名称
                cameraService.updateModelname(aimodelid,jsonMsg.getModelName());
                //添加模型商城
                ModelPlan modelPlan = new ModelPlan();
                modelPlan.setModelId(String.valueOf(aimodelid));
                modelPlan.setModelName(jsonMsg.getModelName());
                modelPlan.setModelExplain(jsonMsg.getModelExplain());
                modelPlan.setModelType(jsonMsg.getModelTypes());
                for (String modelType : modelTypes) {
                    if (getminiomsg(modelType ).equals("img")){
                        modelPlan.setImgs("/"+modelType);
                    }else if (getminiomsg(modelType ).equals("test")){
                        modelPlan.setImgTest("/"+modelType);
                    }else if (getminiomsg(modelType ).equals("detail")) {
                        modelPlan.setImgDetail("/"+modelType);
                    }
                }
                //添加标签
                StringBuilder sb = new StringBuilder();
                List<String> list = jsonMsg.getModelTypes();
                for (String modelType : list) {
                    Integer modelTypeId =modelPlanService.getmodeltypeid(modelType);
                    if (modelTypeId == null){
                        modelPlanService.insterTypes(modelType);
                    }
                    sb.append(modelType).append(",");

                }
                // 删除最后一个逗号
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 1);
                }
                modelPlan.setScene(sb.toString());
                modelPlanService.insertModelPlan(modelPlan);

            }
        } catch (IOException e) {
            throw new IOException("解压文件失败：" + e.getMessage(), e);
        }
    }

    private boolean isImageFile(String fileName) {
        String[] imageExtensions = {"jpg", "jpeg", "png", "gif"};

        for (String ext : imageExtensions) {
            if (fileName.toLowerCase().endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    private int processModelFile(File file) {
        Path modelDir = Paths.get(uploadDir, "models");
        try {
            // 检查并创建模型目录
            if (!Files.exists(modelDir)) {
                Files.createDirectories(modelDir);
            }

            // 避免文件覆盖
            String fileName = file.getName();
            Path targetPath = modelDir.resolve(fileName);
            int i = 1;
            while (Files.exists(targetPath)) {
                String newName = fileName.substring(0, fileName.lastIndexOf('.')) + "_" + i + fileName.substring(fileName.lastIndexOf('.'));
                targetPath = modelDir.resolve(newName);
                i++;
            }
            Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // 上传文件到Python服务
            int uploadResult = streamService.processModelFile(targetPath.toFile());
            if (uploadResult == -1) {
                System.err.println("文件上传失败，终止处理。");
                return -1; // 上传失败，终止处理
            }

            // 记录在数据库
            AiModels aiModels = new AiModels();
            aiModels.setModel(fileName);
            aiModels.setModelLocation(getUploadDir(upload_dir));
            aiModels.setModelVersion("v1.0.0");
            aiModels.setModelSource(1);
            aiModels.setModelName(fileName);
            String time = getnowtime();
            aiModels.setCreateTime(time);
            aiModels.setUpdateTime(time);

            cameraService.insterModel(aiModels);

            // 删除本地文件
            Files.deleteIfExists(targetPath);
            return aiModels.getId();
        } catch (IOException e) {
            System.err.println("处理模型文件时出错: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    private JsonMsg processJsonFile(File file) throws IOException {
        // 读取JSON文件内容并打印
        String content = new String(Files.readAllBytes(file.toPath()));
        JsonMsg jsonMsg = JSON.parseObject(content, JsonMsg.class);
        return jsonMsg;
    }

    private String processImageFile(File file) throws IOException {
        // 将图片上传到MinIO
        String[] parts = file.getName().split("_");
        String folderName = parts[0];

        // 设置存储路径
        String objectName ="modelplan/"+ folderName + "/" + file.getName();
        try (FileInputStream fis = new FileInputStream(file)) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(fis, file.length(), -1)
                            .contentType("image/jpeg")
                            .build()
            );
            return bucketName + "/" +objectName;
        } catch (Exception e) {
            throw new IOException("上传到 MinIO 失败：" + e.getMessage(), e);
        }
    }

    private void cleanupProcessedFiles(Path zipFilePath) throws IOException {
        // 清理已处理的文件
        Files.walk(zipFilePath.getParent())
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private String getnowtime(){
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }

    private String getUploadDir(String path) {
        String[] parts = path.split("/");
        return parts[parts.length - 1];
    }

    private String getminiomsg(String path) {
        String[] parts = path.split("/");
        return parts[2];
    }

}
