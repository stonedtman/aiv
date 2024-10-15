package com.yys.util;

import com.yys.entity.Labels;
import com.yys.entity.LableResult;

import java.util.ArrayList;
import java.util.List;

public class textimgUtil {
    public static LableResult getText(List<String> list) {

        List<String> tags = new ArrayList<>();

        List<String> colors = new ArrayList<>();

        List<Labels> labels = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Integer index=null;
            // 使用空格进行分割
            String[] parts = list.get(i).split(",");

            if (tags.indexOf(parts[0])<0){
                tags.add(parts[0]);
                colors.add(RandomColor.randomColor());
            }

            String tagname = parts[0];
            double x_center = Double.parseDouble(parts[1]);
            double y_center = Double.parseDouble(parts[2]);
            double width = Double.parseDouble(parts[3]);
            double height = Double.parseDouble(parts[4]);
            double confidence = Double.parseDouble(parts[5]);

            Labels label = new Labels();
            label.setX(x_center - width / 2);
            label.setY(y_center - height / 2);
            label.setWidth(width);
            label.setHeight(height);
            label.setEnLabel(tagname);
            label.setZhLabel(tagname);
            label.setConfidence(confidence);
            label.setBgColor(colors.get(tags.indexOf(parts[0])));
            labels.add(label);
        }

        LableResult labelResult = new LableResult();
        labelResult.setId((int) (Math.random() * 1000));
        labelResult.setLabels(labels);
        labelResult.setWidth(640);
        labelResult.setHeight(640);

        return labelResult;
    }

    static class RandomColor {
        public static String randomColor() {
            int r = (int) (Math.random() * 256);
            int g = (int) (Math.random() * 256);
            int b = (int) (Math.random() * 256);
            return String.format("#%02X%02X%02X", r, g, b);
        }
    }
}
