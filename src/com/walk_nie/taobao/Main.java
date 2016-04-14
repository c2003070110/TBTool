package com.walk_nie.taobao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.util.TaobaoUtil;


public class Main {

    /**
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        // TODO 自動生成されたメソッド・スタブ
        //String allLine = FileUtils.readFileToString(new File("C:/tmp/txt.txt"));
        //TaobaoUtil.readBaobeiIn(allLine);
        List<String> list = FileUtils.readLines(new File("./res/all.csv"),"UTF-8");
        List<String> newList = new ArrayList<String>();
        for (String str : list) {
            if(str.startsWith("\"")){
                newList.add(str);
            }else{
                if(newList.isEmpty())continue;
                int pos = newList.size()-1;
                String newStr = newList.get(pos) + str;
                newList.set(pos, newStr);
            }
        }
        List<BaobeiPublishObject> bbList = Lists.newArrayList();
        for (String str : newList) {
            BaobeiPublishObject bb = TaobaoUtil.readBaobeiIn(str);
            if(bb != null){
                bbList.add(bb);
            }
        }
        for (BaobeiPublishObject bb : bbList) {
            String fmt = "title=%s;price=%s;productId=%s";
            System.out.println(String.format(fmt, bb.title,bb.price,bb.outer_id));
            
        }
    }

}
