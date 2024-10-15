package com.yys.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.yys.config.WebSocketHandler;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class RTSPService {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private ConcurrentHashMap<String, AtomicBoolean> runningStates = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Runnable> streamingTasks = new ConcurrentHashMap<>();

    @Resource
    private WebSocketHandler webSocketHandler;

    public static class StreamException extends RuntimeException {
        public StreamException(String message) {
            super(message);
        }
    }

    public int streamVideo(String sessionId, String rtspUrl) {
        stopStreaming(sessionId);

        AtomicBoolean running = new AtomicBoolean(true);
        runningStates.put(sessionId, running);

        webSocketHandler.addCloseListener(sessionId, (String id) -> stopStreaming(id));

        Runnable streamingTask = () -> {
            try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {
                grabber.setOption("buffer_size", "1048576");
                grabber.setOption("rtsp_transport", "tcp");
                grabber.setOption("stimeout", "5000000");
                grabber.start();
                System.out.println("开始播放");

                Java2DFrameConverter converter = new Java2DFrameConverter();
                while (running.get()) {
                    Frame frame = grabber.grab();
                    if (frame == null) {
                        continue;
                    }
                    BufferedImage bufferedImage = converter.convert(frame);
                    if (bufferedImage == null) {
                        continue;
                    }
                    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                        ImageIO.write(bufferedImage, "jpg", baos);
                        byte[] frameData = baos.toByteArray();
                        sendFrameSafe(sessionId, frameData);
                    } catch (IOException e) {
                        handleException(sessionId,"WebSocket 发送时出错", e);
                    }
                }
                grabber.stop();
            } catch (Exception e) {
                handleException(sessionId,"WebSocket 发送时出错", e);
                throw new StreamException("无法连接到 RTSP 流");
            }
        };

        threadPool.submit(streamingTask);
        streamingTasks.put(sessionId, streamingTask);
        return 1;
    }

    public void stopStreaming(String sessionId) {
        Runnable streamingTask = streamingTasks.get(sessionId);
        AtomicBoolean running = runningStates.get(sessionId);

        if (streamingTask != null && running != null && running.get()) {
            running.set(false);
            streamingTask = null; // 清除任务引用，允许GC
            try {
                webSocketHandler.sendTextMessage(sessionId, "准备播放");
            } catch (Exception e) {
                handleException(sessionId,"WebSocket 发送时出错", e);
            } finally {
                runningStates.remove(sessionId);
                streamingTasks.remove(sessionId);
                webSocketHandler.removeCloseListener(sessionId);
            }
        }
    }

    private void sendFrameSafe(String sessionId, byte[] frameData) {
        try {
            WebSocketSession session = webSocketHandler.getSession(sessionId);
            if (session != null && session.isOpen()) {
                webSocketHandler.sendFrame(sessionId, frameData);
            } else {
                System.out.println("WebSocket 连接已关闭，无法发送帧数据");
                stopStreaming(sessionId);
            }
        } catch (Exception e) {
            handleException(sessionId,"WebSocket 发送时出错", e);
        }
    }

    private void handleException(String sessionId,String message, Exception e) {
        System.out.println(message + ": " + e.getMessage());
        e.printStackTrace();
        try {
            webSocketHandler.sendTextMessage(sessionId, "视频流无法打开");
        } catch (Exception ex) {
            System.err.println("发送错误信息时出错: " + ex.getMessage());
        }
    }
}

