package com.walk_nie.taobao.asics.shoes;

import java.io.File;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.walk_nie.taobao.util.TaobaoUtil;


public class AsicsShoesUtil {
    public static String rootPathName = "out/Asics/";

    public static void downloadPicture(AsicsShoesObject goods, String outFilePathPrice)
            throws ClientProtocolException, IOException {

        String seperator = TaobaoUtil.FILE_NAME_SEPERATOR; 
        int idx = 0;
        for (String picUrl : goods.picUrlList) {
            String picName = goods.kataban + seperator + (idx++);
            File downloadFile = TaobaoUtil.downloadPicture(outFilePathPrice, picUrl, picName);
            goods.picLocalFileNameList.add(downloadFile.getAbsolutePath());
            goods.picNameList.add(picName);
        }
    }
}
