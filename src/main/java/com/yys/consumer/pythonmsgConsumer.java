package com.yys.consumer;

import com.alibaba.fastjson2.JSON;
import com.yys.config.RabbitMQconfig;
import com.yys.config.WebSocketDataHandler;
import com.yys.entity.DetectionTask;
import com.yys.entity.RabbitMsg;
import com.yys.entity.WarningTable;
import com.yys.service.DetectionTaskService;
import com.yys.service.RadisService;
import com.yys.service.WarningTableService;
import com.yys.util.ImageClassificationUtil;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Component
@RabbitListener(queues = {RabbitMQconfig.QUEUE_NAME})
public class pythonmsgConsumer {

    @Autowired
    WebSocketDataHandler webSocketDataHandler;

    @Autowired
    private WarningTableService warningTableService;

    @Autowired
    private DetectionTaskService detectionTaskService;

    @Autowired
    private RadisService radisCounterService;

    @Autowired
    private ImageClassificationUtil minioImageClassificationUtil;

    @RabbitHandler
    public void process(byte[] message) throws InterruptedException {

        try {
            // 将字节数组转换为字符串
            String jsonString = new String(message, StandardCharsets.UTF_8);
            // 将JSON字符串转换为对象
            RabbitMsg rabbitMsg = JSON.parseObject(jsonString, RabbitMsg.class);

            // 获取模型名称
            String model = detectionTaskService.modelName(rabbitMsg.getModel().get(0));

            // 获取任务信息
            DetectionTask detectionTask = detectionTaskService.selectDetectionType(rabbitMsg.getTaskId());

            // 创建警告表
            WarningTable warningTable = new WarningTable();
            warningTable.setId(getCurrentTimestampUsingInstant());
            warningTable.setCameraPosition(detectionTask.getCameraPosition());
            warningTable.setMonitoringTask(rabbitMsg.getTaskId());
            warningTable.setAlertType(model);
            warningTable.setAlertLevel(detectionTask.getAlertLevel());
            warningTable.setCapturedVideo(rabbitMsg.getVideoPath());
            warningTable.setCapturedImage(rabbitMsg.getImgPath());
            warningTable.setAlertTime(rabbitMsg.getTime());

            // 调用minioImageClassificationUtil从图片中获取到标签信息
//            warningTable.setVideoTags(minioImageClassificationUtil.classifyImage(rabbitMsg.getImgPath()));
            /**
             *  暂时不需要实时推送数据到前端
             webSocketDataHandler.sendMessageToAll(JSON.toJSONString(warningTable));
             **/

            //Radis计数器
            radisCounterService.incrementCounter();

            //保存数据到数据库
            warningTableService.saveWarningTable(warningTable);



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getCurrentTimestampUsingInstant() {
        return String.valueOf(Instant.now().toEpochMilli());
    }
}
