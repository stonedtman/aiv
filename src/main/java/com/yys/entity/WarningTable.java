package com.yys.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


@Data
@Document(indexName = "warning_table")
public class WarningTable {

    @Id
    private String Id;

    @Field(type = FieldType.Keyword)
    private String alertId;

    @Field(type = FieldType.Text)
    private String cameraPosition;

    @Field(type = FieldType.Text)
    private String monitoringTask;

    @Field(type = FieldType.Text)
    private String alertType;

    @Field(type = FieldType.Text)
    private String videoTags;

    @Field(type = FieldType.Keyword)
    private String alertLevel;

    @Field(type = FieldType.Date, format = DateFormat.date_optional_time)
    private String alertTime;

    @Field(type = FieldType.Keyword)
    private String capturedImage;

    @Field(type = FieldType.Keyword)
    private String capturedVideo;

}

