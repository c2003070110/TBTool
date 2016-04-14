package com.walk_nie.taobao.kakaku;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.List;

import org.apache.http.client.utils.DateUtils;

import com.beust.jcommander.internal.Lists;
import com.walk_nie.taobao.object.BaobeiPublishObject;
import com.walk_nie.taobao.support.BaseBaobeiProducer;
import com.walk_nie.taobao.util.BaobeiUtil;
import com.walk_nie.taobao.util.TaobaoUtil;

public abstract class KakakuBaobeiProceducer extends BaseBaobeiProducer{

	private String scanUrlsFile = "";
	
	private String publishedBaobeiFile = "";
	private String outputFile = "";
	protected List<String> publishedItems = Lists.newArrayList();
	
	protected BaobeiPublishObject baobeiTemplate = new BaobeiPublishObject();
	
	protected String outputPicFolder = "";


    public void process() {
        BufferedWriter priceBw = null;
        try {
            System.out.println("-------- START --------");
            KakakuBaobeiParser parser = (KakakuBaobeiParser)getParser();
            parser.setScanUrlsFile(this.scanUrlsFile);
            parser.setToUpdateBaobeiList(this.toUpdatebaobeiList);
                
            List<KakakuObject> itemIdList = parser.parse();
            if (itemIdList.isEmpty()){
                System.out.println("-------- FINISH--------");
                return;
            }

            String outFilePathPrice = String.format(outputFile, DateUtils
                    .formatDate(Calendar.getInstance().getTime(),
                            "yyyy_MM_dd_HH_mm_ss"));
            File csvFile = new File(outFilePathPrice);
            String picFolder = TaobaoUtil.getPictureFolder(csvFile);
            priceBw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(csvFile), "UTF-16"));

            priceBw.write(TaobaoUtil.composeTaobaoHeaderLine());

            for (KakakuObject obj : itemIdList) {
                // TODO
                downloadPicture(obj, picFolder);
                writeOut(priceBw, obj);
            }

            System.out.println("-------- FINISH--------");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (priceBw != null)
                try {
                    priceBw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    private void downloadPicture(KakakuObject obj, String picFolder) {
        // TODO 自動生成されたメソッド・スタブ
    }

    private void writeOut(BufferedWriter priceBw, KakakuObject item)
            throws Exception {
        priceBw.write(composeBaobeiLine(item));
        priceBw.flush();
    }

    protected String composeBaobeiLine(KakakuObject item) throws Exception {
        BaobeiPublishObject obj = new BaobeiPublishObject();
        if (toUpdatebaobeiList != null && toUpdatebaobeiList.isEmpty()) {
            obj = getBeforeObject(toUpdatebaobeiList, item);
        } else {
            BaobeiUtil.setBaobeiCommonInfo(obj);
        }

        // 宝贝名称
        composeBaobeiTitle(obj,item);
        // 宝贝价格
        composeBaobeiPrice(obj,item);
        // 宝贝数量
        obj.num = "9999";
        // 宝贝描述
        composeBaobeiMiaoshu(obj,item);
         //宝贝属性
        composeBaobeiCateProps(obj,item);
        // 销售属性组合
        composeBaobeiSkuProps(obj,item);
        // 商家编码
        composeBaobeiOutId(obj,item);
        // 销售属性别名
        composeBaobeiPropAlias(obj,item);
        // 商品条形码
        composeBaobeiBarCode(obj,item);
        // 宝贝卖点
        composeBaobeiMaidian(obj,item);
        // 图片状态
        composeBaobeiPictureStatus(obj,item);
        // 新图片
        composeBaobeiPicture(obj,item);

        return TaobaoUtil.composeTaobaoLine(obj);
    }

	private BaobeiPublishObject getBeforeObject(List<BaobeiPublishObject> toUpdatebaobeiList2,
            KakakuObject item) {
	    for(BaobeiPublishObject baobei:toUpdatebaobeiList2){
	        if(baobei.outer_id.equals(item.id)){
	            return baobei;
	        }
	    }
        return null;
    }

    public KakakuBaobeiProceducer setScanUrlsFile(String scanUrlsFile) {
		this.scanUrlsFile = scanUrlsFile;
		return this;
	}

	public KakakuBaobeiProceducer setPublishedBaobeiFile(
			String publishedBaobeiFile) {
		this.publishedBaobeiFile = publishedBaobeiFile;
		return this;
	}

	public KakakuBaobeiProceducer setOutputFile(String outputFile) {
		this.outputFile = outputFile;
		return this;
	}

	protected abstract void composeBaobeiTitle(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiPrice(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiMiaoshu(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiCateProps(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiSkuProps(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiOutId(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiPropAlias(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiBarCode(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiMaidian(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiPictureStatus(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);
    
    protected abstract void composeBaobeiPicture(BaobeiPublishObject baobeiObj, KakakuObject kakakuObj);

//	protected abstract String getExtraBaobeiMiaoshu(KakakuObject obj)
//			throws IOException;
//
//	protected abstract void parsePictureFromMaker(KakakuObject obj)
//			throws ClientProtocolException, IOException;
	
}
