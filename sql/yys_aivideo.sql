/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80036 (8.0.36)
 Source Host           : localhost:3306
 Source Schema         : yys_aivideo

 Target Server Type    : MySQL
 Target Server Version : 80036 (8.0.36)
 File Encoding         : 65001

 Date: 20/09/2024 11:16:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ai_camera
-- ----------------------------
DROP TABLE IF EXISTS `ai_camera`;
CREATE TABLE `ai_camera`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `camera_id` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `camera_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `camera_group` int NULL DEFAULT NULL,
  `camera_resolution` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `camera_protocol` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `camera_status` int NULL DEFAULT NULL,
  `working_time` date NULL DEFAULT NULL,
  `last_time` datetime NULL DEFAULT NULL,
  `ip_address` varchar(15) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `camera_port` int NULL DEFAULT NULL,
  `video_streaming` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `type_input` int NULL DEFAULT NULL,
  `camera_img` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_camera
-- ----------------------------

-- ----------------------------
-- Table structure for ai_model
-- ----------------------------
DROP TABLE IF EXISTS `ai_model`;
CREATE TABLE `ai_model`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '模型id',
  `model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `model_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `model_source` int NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  `model_location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  `model_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_model
-- ----------------------------
INSERT INTO `ai_model` VALUES (1, 'SafeHat.pt', '佩戴安全帽', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (2, 'FallDetect.pt', '人体跌倒识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (3, 'Ebike.pt', '电动车监测', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (4, 'Truck.pt', '大货车监测', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (5, 'Door.pt', '房门关开监测', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (6, 'unFire.pt', '灭火器正常', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (11, 'GroundWater.pt', '地面积水', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (12, 'Mask.pt', '口罩识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (13, 'SafetyGloves.pt', '安全手套识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (15, 'Smartphone.pt', '玩手机识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (16, 'Sleeping.pt', '睡觉识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (17, 'Smoking.pt', '吸烟监测', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (20, 'NMotor.pt', '非机动车识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (22, 'Occupation.pt', '占道经营识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (26, 'Guarding.pt', '智能电网守卫', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (27, 'PetLeash.pt', '宠物牵绳识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (28, 'Wheelchair.pt', '轮椅识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (29, 'Stroller.pt', '婴儿车识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (30, 'Traffic.pt', '交通事故识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');
INSERT INTO `ai_model` VALUES (35, 'Facial.pt', '表情识别', 0, '2024-08-01 00:00:00', 'weights', '2024-08-01 00:00:00', 'v1.0.0');

-- ----------------------------
-- Table structure for ai_model_type
-- ----------------------------
DROP TABLE IF EXISTS `ai_model_type`;
CREATE TABLE `ai_model_type`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `model_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_model_type
-- ----------------------------
INSERT INTO `ai_model_type` VALUES (1, '智慧工地');
INSERT INTO `ai_model_type` VALUES (2, '公安行业');
INSERT INTO `ai_model_type` VALUES (3, '智慧医院');
INSERT INTO `ai_model_type` VALUES (4, '地铁站');
INSERT INTO `ai_model_type` VALUES (5, '交通枢纽');
INSERT INTO `ai_model_type` VALUES (6, '城市道路');
INSERT INTO `ai_model_type` VALUES (7, '社区');
INSERT INTO `ai_model_type` VALUES (8, '园区');
INSERT INTO `ai_model_type` VALUES (9, '城市治理');
INSERT INTO `ai_model_type` VALUES (10, '交通治理');
INSERT INTO `ai_model_type` VALUES (11, '平安校园');
INSERT INTO `ai_model_type` VALUES (12, '智慧零售');
INSERT INTO `ai_model_type` VALUES (13, '小区');
INSERT INTO `ai_model_type` VALUES (14, '写字楼');
INSERT INTO `ai_model_type` VALUES (15, '消防行业');
INSERT INTO `ai_model_type` VALUES (16, '购物中心');
INSERT INTO `ai_model_type` VALUES (17, '车站');
INSERT INTO `ai_model_type` VALUES (18, '化工生产');
INSERT INTO `ai_model_type` VALUES (19, '智慧电网');
INSERT INTO `ai_model_type` VALUES (20, '监控室');
INSERT INTO `ai_model_type` VALUES (21, '后厨');
INSERT INTO `ai_model_type` VALUES (22, '火车站');
INSERT INTO `ai_model_type` VALUES (23, '娱乐场所');
INSERT INTO `ai_model_type` VALUES (24, '机场');
INSERT INTO `ai_model_type` VALUES (25, '电焊车间');

-- ----------------------------
-- Table structure for ai_modeltotype
-- ----------------------------
DROP TABLE IF EXISTS `ai_modeltotype`;
CREATE TABLE `ai_modeltotype`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `model_id` int NULL DEFAULT NULL,
  `model_type_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 167 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_modeltotype
-- ----------------------------
INSERT INTO `ai_modeltotype` VALUES (71, 1, 1);
INSERT INTO `ai_modeltotype` VALUES (72, 2, 2);
INSERT INTO `ai_modeltotype` VALUES (73, 2, 3);
INSERT INTO `ai_modeltotype` VALUES (74, 2, 4);
INSERT INTO `ai_modeltotype` VALUES (75, 2, 5);
INSERT INTO `ai_modeltotype` VALUES (76, 2, 6);
INSERT INTO `ai_modeltotype` VALUES (77, 2, 7);
INSERT INTO `ai_modeltotype` VALUES (78, 2, 8);
INSERT INTO `ai_modeltotype` VALUES (79, 3, 6);
INSERT INTO `ai_modeltotype` VALUES (80, 3, 7);
INSERT INTO `ai_modeltotype` VALUES (81, 4, 1);
INSERT INTO `ai_modeltotype` VALUES (82, 4, 9);
INSERT INTO `ai_modeltotype` VALUES (83, 4, 10);
INSERT INTO `ai_modeltotype` VALUES (84, 4, 5);
INSERT INTO `ai_modeltotype` VALUES (85, 4, 6);
INSERT INTO `ai_modeltotype` VALUES (86, 5, 1);
INSERT INTO `ai_modeltotype` VALUES (87, 5, 11);
INSERT INTO `ai_modeltotype` VALUES (88, 5, 12);
INSERT INTO `ai_modeltotype` VALUES (89, 5, 13);
INSERT INTO `ai_modeltotype` VALUES (90, 5, 14);
INSERT INTO `ai_modeltotype` VALUES (91, 6, 11);
INSERT INTO `ai_modeltotype` VALUES (92, 6, 15);
INSERT INTO `ai_modeltotype` VALUES (93, 6, 13);
INSERT INTO `ai_modeltotype` VALUES (94, 6, 16);
INSERT INTO `ai_modeltotype` VALUES (95, 6, 17);
INSERT INTO `ai_modeltotype` VALUES (96, 6, 14);
INSERT INTO `ai_modeltotype` VALUES (97, 7, 9);
INSERT INTO `ai_modeltotype` VALUES (98, 7, 13);
INSERT INTO `ai_modeltotype` VALUES (99, 7, 14);
INSERT INTO `ai_modeltotype` VALUES (100, 7, 8);
INSERT INTO `ai_modeltotype` VALUES (101, 8, 11);
INSERT INTO `ai_modeltotype` VALUES (102, 8, 2);
INSERT INTO `ai_modeltotype` VALUES (103, 8, 15);
INSERT INTO `ai_modeltotype` VALUES (104, 8, 13);
INSERT INTO `ai_modeltotype` VALUES (105, 8, 14);
INSERT INTO `ai_modeltotype` VALUES (106, 8, 8);
INSERT INTO `ai_modeltotype` VALUES (107, 8, 18);
INSERT INTO `ai_modeltotype` VALUES (108, 9, 11);
INSERT INTO `ai_modeltotype` VALUES (109, 9, 2);
INSERT INTO `ai_modeltotype` VALUES (110, 9, 15);
INSERT INTO `ai_modeltotype` VALUES (111, 9, 13);
INSERT INTO `ai_modeltotype` VALUES (112, 9, 14);
INSERT INTO `ai_modeltotype` VALUES (113, 10, 19);
INSERT INTO `ai_modeltotype` VALUES (114, 10, 18);
INSERT INTO `ai_modeltotype` VALUES (115, 11, 6);
INSERT INTO `ai_modeltotype` VALUES (116, 11, 7);
INSERT INTO `ai_modeltotype` VALUES (117, 12, 9);
INSERT INTO `ai_modeltotype` VALUES (118, 13, 18);
INSERT INTO `ai_modeltotype` VALUES (119, 14, 20);
INSERT INTO `ai_modeltotype` VALUES (120, 15, 20);
INSERT INTO `ai_modeltotype` VALUES (121, 15, 21);
INSERT INTO `ai_modeltotype` VALUES (122, 16, 20);
INSERT INTO `ai_modeltotype` VALUES (123, 17, 11);
INSERT INTO `ai_modeltotype` VALUES (124, 17, 3);
INSERT INTO `ai_modeltotype` VALUES (125, 17, 12);
INSERT INTO `ai_modeltotype` VALUES (126, 17, 22);
INSERT INTO `ai_modeltotype` VALUES (127, 17, 20);
INSERT INTO `ai_modeltotype` VALUES (128, 18, 20);
INSERT INTO `ai_modeltotype` VALUES (129, 19, 9);
INSERT INTO `ai_modeltotype` VALUES (130, 20, 10);
INSERT INTO `ai_modeltotype` VALUES (131, 21, 11);
INSERT INTO `ai_modeltotype` VALUES (132, 21, 2);
INSERT INTO `ai_modeltotype` VALUES (133, 21, 23);
INSERT INTO `ai_modeltotype` VALUES (134, 22, 9);
INSERT INTO `ai_modeltotype` VALUES (135, 23, 12);
INSERT INTO `ai_modeltotype` VALUES (136, 23, 22);
INSERT INTO `ai_modeltotype` VALUES (137, 23, 20);
INSERT INTO `ai_modeltotype` VALUES (138, 24, 22);
INSERT INTO `ai_modeltotype` VALUES (139, 24, 17);
INSERT INTO `ai_modeltotype` VALUES (140, 25, 19);
INSERT INTO `ai_modeltotype` VALUES (141, 26, 19);
INSERT INTO `ai_modeltotype` VALUES (142, 27, 7);
INSERT INTO `ai_modeltotype` VALUES (143, 28, 3);
INSERT INTO `ai_modeltotype` VALUES (144, 28, 24);
INSERT INTO `ai_modeltotype` VALUES (145, 29, 16);
INSERT INTO `ai_modeltotype` VALUES (146, 29, 24);
INSERT INTO `ai_modeltotype` VALUES (147, 30, 5);
INSERT INTO `ai_modeltotype` VALUES (148, 31, 21);
INSERT INTO `ai_modeltotype` VALUES (149, 32, 4);
INSERT INTO `ai_modeltotype` VALUES (150, 32, 17);
INSERT INTO `ai_modeltotype` VALUES (151, 32, 25);
INSERT INTO `ai_modeltotype` VALUES (152, 33, 9);
INSERT INTO `ai_modeltotype` VALUES (153, 33, 10);
INSERT INTO `ai_modeltotype` VALUES (154, 33, 5);
INSERT INTO `ai_modeltotype` VALUES (155, 34, 9);
INSERT INTO `ai_modeltotype` VALUES (156, 34, 10);
INSERT INTO `ai_modeltotype` VALUES (157, 35, 2);
INSERT INTO `ai_modeltotype` VALUES (158, 35, 20);
INSERT INTO `ai_modeltotype` VALUES (159, 36, 9);
INSERT INTO `ai_modeltotype` VALUES (160, 36, 2);
INSERT INTO `ai_modeltotype` VALUES (161, 36, 15);
INSERT INTO `ai_modeltotype` VALUES (162, 36, 6);
INSERT INTO `ai_modeltotype` VALUES (163, 36, 8);
INSERT INTO `ai_modeltotype` VALUES (164, 37, 9);
INSERT INTO `ai_modeltotype` VALUES (165, 37, 2);
INSERT INTO `ai_modeltotype` VALUES (166, 37, 10);

-- ----------------------------
-- Table structure for ai_user
-- ----------------------------
DROP TABLE IF EXISTS `ai_user`;
CREATE TABLE `ai_user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_pwd` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_emial` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `permissions` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of ai_user
-- ----------------------------
INSERT INTO `ai_user` VALUES (1, 'admin', 'Stonedt,123', NULL, '0');
INSERT INTO `ai_user` VALUES (2, 'user', 'user', NULL, '1');

-- ----------------------------
-- Table structure for camera_sector
-- ----------------------------
DROP TABLE IF EXISTS `camera_sector`;
CREATE TABLE `camera_sector`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_id` char(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `group_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of camera_sector
-- ----------------------------

-- ----------------------------
-- Table structure for detection_task
-- ----------------------------
DROP TABLE IF EXISTS `detection_task`;
CREATE TABLE `detection_task`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `task_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务ID',
  `task_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '任务名称',
  `camera_position` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '摄像头位置',
  `algorithm_model` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '算法模型',
  `task_description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '任务描述',
  `priority` int NULL DEFAULT NULL COMMENT '任务优先级',
  `alert_method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '告警方式',
  `alert_level` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '告警严重程度',
  `notification_email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子邮件地址',
  `target_count_threshold` int NULL DEFAULT NULL COMMENT '触发告警的目标数量阈值',
  `target_dwell_time` int NULL DEFAULT NULL COMMENT '目标停留的最短时间，用于告警触发',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `start_time` datetime NULL DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime NULL DEFAULT NULL COMMENT '结束时间',
  `status` int NULL DEFAULT NULL COMMENT '任务当前的状态',
  `task_tagging` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '任务标识',
  `ids` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '多选模型id',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '检测任务表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of detection_task
-- ----------------------------

-- ----------------------------
-- Table structure for model_plan
-- ----------------------------
DROP TABLE IF EXISTS `model_plan`;
CREATE TABLE `model_plan`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `model_id` int NULL DEFAULT NULL COMMENT '模型id',
  `model_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '模型名称',
  `model_explain` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '模型描述',
  `imgs` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '概念图',
  `img_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '缩略图',
  `img_test` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '测试图',
  `test_result` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '测试结果',
  `scene` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '应用场景标签',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 79 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of model_plan
-- ----------------------------
INSERT INTO `model_plan` VALUES (1, 1, '佩戴安全帽', '自动识别建筑工地等场景的现场作业人员的安全帽佩戴情况,常见红白蓝黄4种颜色安全帽均能识别', '/file/%E4%BD%A9%E6%88%B4%E5%AE%89%E5%85%A8%E5%B8%BD.jpg', '/file/detail/%E4%BD%A9%E6%88%B4%E5%AE%89%E5%85%A8%E5%B8%BD.jpg', '/file/test/safeHat.png', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [877.0, 56.0, 1106.0, 580.0], \"label\": \"person\", \"score\": \"0.63\"}, {\"xyxy\": [901.0, 169.0, 1094.0, 442.0], \"label\": \"vest\", \"score\": \"0.68\"}, {\"xyxy\": [404.0, 164.0, 582.0, 372.0], \"label\": \"vest\", \"score\": \"0.80\"}, {\"xyxy\": [399.0, 73.0, 590.0, 385.0], \"label\": \"person\", \"score\": \"0.84\"}, {\"xyxy\": [666.0, 177.0, 827.0, 384.0], \"label\": \"vest\", \"score\": \"0.84\"}, {\"xyxy\": [422.0, 76.0, 517.0, 146.0], \"label\": \"helmet\", \"score\": \"0.86\"}, {\"xyxy\": [52.0, 26.0, 227.0, 125.0], \"label\": \"helmet\", \"score\": \"0.87\"}, {\"xyxy\": [656.0, 67.0, 835.0, 408.0], \"label\": \"person\", \"score\": \"0.87\"}, {\"xyxy\": [8.0, 30.0, 307.0, 642.0], \"label\": \"person\", \"score\": \"0.87\"}, {\"xyxy\": [664.0, 63.0, 792.0, 140.0], \"label\": \"helmet\", \"score\": \"0.90\"}, {\"xyxy\": [9.0, 184.0, 276.0, 565.0], \"label\": \"vest\", \"score\": \"0.92\"}, {\"xyxy\": [883.0, 45.0, 1006.0, 155.0], \"label\": \"helmet\", \"score\": \"0.94\"}]}}', '智慧工地');
INSERT INTO `model_plan` VALUES (2, 2, '人体跌倒识别', '自动识别如地铁手扶梯/楼梯、老幼活动区等公共场所人员摔倒行为，准确率高于90%', '/file/%E4%BA%BA%E4%BD%93%E8%B7%8C%E5%80%92%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E4%BA%BA%E4%BD%93%E8%B7%8C%E5%80%92%E8%AF%86%E5%88%AB.jpg', '/file/test/FallDetect.png', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [191.0, 202.0, 809.0, 521.0], \"label\": \"Fall-Detected\", \"score\": \"0.64\"}]}}', '公安行业,智慧医院,地铁站,交通枢纽,城市道路,社区,园区');
INSERT INTO `model_plan` VALUES (3, 3, '电动车监测', '自动识别电动车进电梯、电动车骑行或停放等行为，同时能够区分自行车、手推车等其他目标', '/file/%E7%94%B5%E5%8A%A8%E8%BD%A6%E7%9B%91%E6%B5%8B.jpg', '/file/detail/%E7%94%B5%E5%8A%A8%E8%BD%A6%E8%AF%86%E5%88%AB.jpg', '/file/test/Ebike.png', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [176.0, 27.0, 559.0, 690.0], \"label\": \"E-bike\", \"score\": \"0.82\"}]}}', '城市道路,社区');
INSERT INTO `model_plan` VALUES (4, 4, '大货车监测', '自动识别和检测限定区域内是否有渣土车、大货车出现，以极大地提高城市管理的效率和响应速度，同时对于环境保护和交通安全也有着积极的影响。', '/file/%E5%A4%A7%E8%B4%A7%E8%BD%A6%E7%9B%91%E6%B5%8B.jpg', '/file/detail/%E5%A4%A7%E8%B4%A7%E8%BD%A6%E7%9B%91%E6%B5%8B.jpg', '/file/test/Truck.png', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [7.0, 142.0, 765.0, 673.0], \"label\": \"truck\", \"score\": \"0.96\"}]}}', '智慧工地,城市治理,交通治理,交通枢纽,城市道路');
INSERT INTO `model_plan` VALUES (5, 5, '房门关开监测', '在社区、写字楼和学校等场所，应用智能识别技术自动监测电梯机房、配电房和单元门的关闭状态，确保安全并及时警报门的异常敞开情况。', '/file/%E6%88%BF%E9%97%A8%E5%BC%80%E5%85%B3%E7%9B%91%E6%B5%8B.jpg', '/file/detail/%E6%88%BF%E9%97%A8%E5%BC%80%E5%85%B3%E6%A3%80%E6%B5%8B.jpg', '/file/test/Door.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [214.0, 161.0, 561.0, 555.0], \"label\": \"semi\", \"score\": \"0.82\"}]}}', '智慧工地,平安校园,智慧零售,小区,写字楼');
INSERT INTO `model_plan` VALUES (6, 6, '灭火器正常', '在小区、学校、写字楼和商超等公共场所，通过智能监控系统识别并确保灭火器是否按规范正确摆放在指定区域，以提高紧急情况下的应对效率和安全性。', '/file/%E7%81%AD%E7%81%AB%E5%99%A8.jpg', '/file/detail/%E7%81%AD%E7%81%AB%E5%99%A8%E8%AF%86%E5%88%AB.jpg', '/file/test/灭火器正常样本.jpg', '{\"msg\":\"success\",\"code\":200,\"data\":{\"image\":null,\"coordinate\":[{\"score\":\"0.97\",\"label\":\"fire_extinguisher\",\"xyxy\":[1,30,223,370]},{\"score\":\"0.97\",\"label\":\"fire_extinguisher\",\"xyxy\":[371,72,431,272]},{\"score\":\"0.98\",\"label\":\"fire_extinguisher\",\"xyxy\":[306,63,381,292]},{\"score\":\"0.98\",\"label\":\"fire_extinguisher\",\"xyxy\":[227,50,316,327]}]}}', '平安校园,消防行业,小区,购物中心,车站,写字楼');
INSERT INTO `model_plan` VALUES (11, 11, '地面积水', '在建筑工地和城市道路等关键区域，部署积水识别系统以实时监测雨后或洪水导致的路面积水状况，并在积水达到一定程度时立即发出警报，以便及时采取应对措施。', '/file/%E5%9C%B0%E9%9D%A2%E7%A7%AF%E6%B0%B4.jpg', '/file/detail/%E8%B7%AF%E9%9D%A2%E7%A7%AF%E6%B0%B4%E7%9B%91%E6%B5%8B.jpg', '/file/test/GroundWater.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [139.0, 141.0, 557.0, 427.0], \"label\": \"water\", \"score\": \"0.55\"}]}}', NULL);
INSERT INTO `model_plan` VALUES (12, 12, '口罩识别', '对如医院、后厨等对卫生要求较高的人员工作场所，有效监测工作人员口罩佩戴情况，检测到未佩戴者立即标注并进行提醒', '/file/%E5%8F%A3%E7%BD%A9%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E5%8F%A3%E7%BD%A9%E8%AF%86%E5%88%AB.jpg', '/file/test/Mask.png', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [114.0, 139.0, 413.0, 494.0], \"label\": \"mask\", \"score\": \"0.59\"}]}}', NULL);
INSERT INTO `model_plan` VALUES (13, 13, '安全手套识别', '在能源和电力园区等关键作业环境中，利用深度学习算法对作业人员进行智能监控，以确保他们在操作带电设备时正确穿戴绝缘手套，一旦发现违规未穿戴情况，系统将立即发出警报，提醒安全监管人员及时介入，保障作业安全。', '/file/%E5%AE%89%E5%85%A8%E6%89%8B%E5%A5%97%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E5%AE%89%E5%85%A8%E6%89%8B%E5%A5%97%E4%BD%A9%E6%88%B4.jpg', '/file/test/SafetyGloves.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [314.0, 150.0, 524.0, 335.0], \"label\": \"gloved\", \"score\": \"0.96\"}]}}', '化工生产');
INSERT INTO `model_plan` VALUES (15, 15, '玩手机识别', '实时检测在特定场合下是否有人分心玩手机，该技术在学校、工作场所，一旦系统识别到玩手机的行为，能够及时发出警示，以减少分心行为对安全和效率的影响。', '/file/%E7%8E%A9%E6%89%8B%E6%9C%BA%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E7%8E%A9%E6%89%8B%E6%9C%BA%E8%AF%86%E5%88%AB.jpg', '/file/test/Smartphone.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [3.0, 188.0, 119.0, 478.0], \"label\": \"0\", \"score\": \"0.94\"}]}}', '监控室,后厨');
INSERT INTO `model_plan` VALUES (16, 16, '睡觉识别', '智能监测技术检测员工在岗期间的睡眠行为，确保工作效率和职业安全，一旦系统发现有员工在工作岗位上睡觉，将及时发出提醒，促进健康的工作习惯和提高整体工作氛围。', '/file/%E7%9D%A1%E8%A7%89%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E7%9D%A1%E8%A7%89%E8%AF%86%E5%88%AB.jpg', '/file/test/Sleeping.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [466.0, 357.0, 545.0, 437.0], \"label\": \"sleep\", \"score\": \"0.94\"}]}}', '监控室');
INSERT INTO `model_plan` VALUES (17, 17, '吸烟监测', '在加油站、后厨以及公共场所等对卫生和安全要求较高的区域，实施抽烟识别系统进行实时监控，确保及时检测并防范潜在的火灾风险和卫生问题。', '/file/%E5%90%B8%E7%83%9F%E6%A3%80%E6%B5%8B.jpg', '/file/detail/%E5%90%B8%E7%83%9F%E8%AF%86%E5%88%AB.jpg', '/file/test/Smoking.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [401.0, 155.0, 465.0, 221.0], \"label\": \"smoking\", \"score\": \"0.76\"}]}}', '平安校园,智慧医院,智慧零售,火车站,监控室');
INSERT INTO `model_plan` VALUES (20, 20, '非机动车识别', '在城市道路、工业园区和社区等区域，通过非机动车识别技术对误入机动车道的三轮车、自行车、电动车、板车和摩托车等进行有效监测和识别，及时反馈信息，以保障交通安全和顺畅。', '/file/%E9%9D%9E%E6%9C%BA%E5%8A%A8%E8%BD%A6%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E9%9D%9E%E6%9C%BA%E5%8A%A8%E8%BD%A6%E8%AF%86%E5%88%AB.jpg', '/file/test/NMotor.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [362.0, 118.0, 453.0, 378.0], \"label\": \"People\", \"score\": \"0.87\"}, {\"xyxy\": [226.0, 105.0, 826.0, 458.0], \"label\": \"Tricycle\", \"score\": \"0.92\"}]}}', '交通治理');
INSERT INTO `model_plan` VALUES (22, 22, '占道经营识别', '城市管理和小区路口通过占道经营识别技术对商家违规占用公共道路进行经营活动的行为进行有效监测和检测，以维护交通秩序和市容整洁。', '/file/%E5%8D%A0%E9%81%93%E7%BB%8F%E8%90%A5%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E5%8D%A0%E9%81%93%E7%BB%8F%E8%90%A5%E8%AF%86%E5%88%AB.jpg', '/file/test/Occupation.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [468.0, 160.0, 814.0, 439.0], \"label\": \"Fixed\", \"score\": \"0.51\"}]}}', '城市治理');
INSERT INTO `model_plan` VALUES (26, 26, '智能电网守卫', '防止鸟类、老鼠、蛇类和风筝等对设施的潜在威胁，能够自动检测异常行为，提高电网监控的效率和准确性，同时减少人力巡检的需求。', '/file/%E6%99%BA%E8%83%BD%E7%94%B5%E7%BD%91.jpg', '/file/detail/%E6%99%BA%E8%83%BD%E7%94%B5%E7%BD%91%E8%AF%86%E5%88%AB.jpg', '/file/test/智能电网守卫样本.png', '{\"msg\":\"success\",\"code\":200,\"data\":{\"image\":null,\"coordinate\":[{\"score\":\"0.64\",\"label\":\"kite\",\"xyxy\":[185.0,105.0,224.0,224.0]}]}}', '智慧电网');
INSERT INTO `model_plan` VALUES (27, 27, '宠物牵绳识别', '宠物牵绳识别技术能够自动检测社区中宠物是否佩戴牵绳，有助于提升社区安全和管理效率，同时促进宠物主人负责任的养宠行为。', '/file/%E5%8A%A8%E7%89%A9%E7%89%B5%E7%BB%B3%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E5%AE%A0%E7%89%A9%E7%89%B5%E7%BB%B3%E6%A3%80%E6%B5%8B.jpg', '/file/test/PetLeash.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [183.0, 267.0, 553.0, 594.0], \"label\": \"dog-walking\", \"score\": \"0.54\"}]}}', '社区');
INSERT INTO `model_plan` VALUES (28, 28, '轮椅识别', '要用于医院、公共场所、地铁站、商场等扶梯场景，自动识别轮椅，及时反馈给工作人员，协助运营管理人员第一时间发现并处理可能的危险情况，确保安全与便利。', '/file/%E8%BD%AE%E6%A4%85%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E8%BD%AE%E6%A4%85%E8%AF%86%E5%88%AB.jpg', '/file/test/Wheelchair.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [422.0, 340.0, 607.0, 491.0], \"label\": \"wheelchairs\", \"score\": \"0.87\"}, {\"xyxy\": [480.0, 79.0, 610.0, 184.0], \"label\": \"wheelchairs\", \"score\": \"0.90\"}]}}', '智慧医院,机场');
INSERT INTO `model_plan` VALUES (29, 29, '婴儿车识别', '要用于车站、机场、地铁站、商场等扶梯场景,自动识别婴儿车,及时反馈给工作人员,协助运营管理人员第一时间发现危险情况。', '/file/%E6%89%B6%E6%A2%AF%E5%A9%B4%E5%84%BF%E8%BD%A6.jpg', '/file/detail/%E6%89%B6%E6%A2%AF%E5%A9%B4%E5%84%BF%E8%BD%A6%E8%AF%86%E5%88%AB.jpg', '/file/test/Stroller.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [175.0, 180.0, 292.0, 390.0], \"label\": \"carrito bebe\", \"score\": \"0.97\"}]}}', '购物中心,机场');
INSERT INTO `model_plan` VALUES (30, 30, '交通事故识别', '交通事故识别是利用先进的监控技术和数据分析，对道路上发生的事故进行快速检测和分类。这有助于提高应急响应速度，优化交通流量，保障道路安全。', '/file/%E4%BA%A4%E9%80%9A%E4%BA%8B%E6%95%85%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E4%BA%A4%E9%80%9A%E4%BA%8B%E6%95%85%E8%AF%86%E5%88%AB.jpg', '/file/test/Traffic.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [148.0, 69.0, 463.0, 246.0], \"label\": \"Accident\", \"score\": \"0.85\"}]}}', '交通枢纽');
INSERT INTO `model_plan` VALUES (35, 35, '表情识别', '对图片或动态视频进行人脸检测,定位并标记边框,依据眼、口、鼻轮廓等关键点信息识别个人五官动作及形态,输出表情结果。', '/file/%E8%A1%A8%E6%83%85%E8%AF%86%E5%88%AB.jpg', '/file/detail/%E8%A1%A8%E6%83%85%E8%AF%86%E5%88%AB.jpg', '/file/test/Facial.jpg', '{\"msg\": \"success\", \"code\": 200, \"data\": {\"image\": null, \"coordinate\": [{\"xyxy\": [11.0, 63.0, 390.0, 512.0], \"label\": \"anger\", \"score\": \"0.64\"}]}}', '公安行业,监控室');

SET FOREIGN_KEY_CHECKS = 1;
