package com.walk_nie.taobao.eizo;

import org.jsoup.helper.StringUtil;

import com.walk_nie.taobao.util.TaobaoUtil;
import com.walk_nie.util.NieConfig;

public class EizoUtil {

	public static String convertToCNYWithEmsFee(EizoProductObject item) {
		double curencyRate = Double.parseDouble(NieConfig.getConfig("eizo.currency.rate"));
		double benefitRate = Double.parseDouble(NieConfig.getConfig("eizo.benefit.rate"));
		String priceStr = item.priceJPY;
		try {
			int price = Integer.parseInt(priceStr);
			//long priceTax = Math.round(price * 1.08);
			int emsFee = TaobaoUtil.getEmsFee(item.weight + item.weightExtra);
			double priceCNY = (price + emsFee) * curencyRate;
			priceCNY = priceCNY + priceCNY * benefitRate;
			return String.valueOf(Math.round(priceCNY));
		} catch (Exception ex) {
			ex.printStackTrace();
			return "XXXXXX";
		}
	}
	public static String getPictureRootFolder() {

		return NieConfig.getConfig("eizo.out.root.folder") + "/" + NieConfig.getConfig("eizo.picture.folder");
	}

    public static String composeBaoyouMiaoshu() {
        StringBuffer detailSB = new StringBuffer();
        detailSB.append("<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">各位亲们</h3>");
        detailSB.append("<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
        detailSB.append("<p style=\"text-indent:2.0em;\">100%正品，日货料好质高，低调奢华，性价比高！</p>");
        detailSB.append("<p style=\"text-indent:2.0em;\"><span style=\";color:red;font-weight:bold\">下单后</span>才采购!<span style=\";color:red;font-weight:bold\">不是现货！不能当天发货！</span></p>");
        detailSB.append("<p style=\"text-indent:2.0em;\">如不幸被海关查到，由买家报关通关！<span style=\";color:red;font-weight:bold\">关税买家承担！</span></p>");
        detailSB.append("<p style=\"text-indent:2.0em;\"><span style=\";color:red;font-weight:bold\">不能报关的买家，请不要下单！！！！</span></p>");
        detailSB.append("</div>");
        return detailSB.toString();
    }

	public static String composeProductInfoMiaoshu(EizoProductObject item) {
		StringBuffer detailSB = new StringBuffer();
		if (StringUtil.isBlank(item.detailScreenShotPicFile) && StringUtil.isBlank(item.specScreenShotPicFile)) {

			return detailSB.toString();
		}
		detailSB.append(
				"<h3 style=\"background:#ff8f2d repeat-x 0 0;border:1.0px solid #e19d63;border-bottom:1.0px solid #d07428;padding:3.0px 0 0 10.0px;height:26.0px;color:#ffffff;font-size:large;\">宝贝说明</h3>");
		detailSB.append(
				"<div style=\"background:#f8f9fb repeat-x top;border:1.0px solid #b0bec7;padding:10.0px;font-size:large;font-family:simsun;\">");
		if (!StringUtil.isBlank(item.detailScreenShotPicFile)) {
			detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
					+ item.detailScreenShotPicFile + "\"/></p>");
		}
		if (!StringUtil.isBlank(item.specScreenShotPicFile)) {
			detailSB.append("<p><img style=\"border:#666666 2px solid;padding:2px;width:650px;\" src=\"FILE:///"
					+ item.specScreenShotPicFile + "\"/></p>");
		}
		detailSB.append("</div>");
		return detailSB.toString();
	}

}
