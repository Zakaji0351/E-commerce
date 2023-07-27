package com.lzq.mall.utils;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.apache.poi.hpsf.Thumbnail;

import java.io.IOException;

public class ImageUtils {
    public static void main(String[] args) throws IOException {
        String path = "/Users/liziqi/Desktop/";
        Thumbnails.of(path + "caomei2.jpg").sourceRegion(Positions.BOTTOM_RIGHT, 50, 50)
                .size(50, 50).toFile(path + "crop.jpg");
        Thumbnails.of(path + "caomei2.jpg").scale(0.5).toFile(path + "scale1.jpg");
        Thumbnails.of(path + "caomei2.jpg").scale(1.5).toFile(path + "scale2.jpg");
        Thumbnails.of(path + "caomei2.jpg").size(500, 500).keepAspectRatio(false).toFile(path + "size1.jpg");
        Thumbnails.of(path + "caomei2.jpg").size(500, 500).keepAspectRatio(true).toFile(path + "size2.jpg");
    }
}
