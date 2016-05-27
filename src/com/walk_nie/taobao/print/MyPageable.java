package com.walk_nie.taobao.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.List;

public class MyPageable implements Pageable {

    protected int labelType = 0;
    protected List<PrintInfoObject> toPrintList = null;
    protected PageFormat pageFormat = null;
    
    public MyPageable(List<PrintInfoObject> toPrintList, PageFormat pageFormat, int labelType) {
        this.toPrintList = toPrintList;
        this.pageFormat = pageFormat;
        this.labelType = labelType;
    }
    @Override
    public int getNumberOfPages() {
        return toPrintList.size();
    }

    @Override
    public PageFormat getPageFormat(int pageIndex) throws IndexOutOfBoundsException {
        return pageFormat;
    }

    @Override
    public Printable getPrintable(int pageIndex) throws IndexOutOfBoundsException {
        PrintInfoObject printInfo = toPrintList.get(pageIndex);
        System.out.println("[Printing]" + printInfo.receiverName + " " + printInfo.receiverAddress1
                + printInfo.receiverAddress2 + printInfo.receiverAddress3)
                ;
        if (labelType == PrintUtil.LABEL_TYPE_EMS) {
            return new PrintableForEMS(printInfo);
        } else if (labelType == PrintUtil.LABEL_TYPE_POSTAL) {
            return new PrintableForSAL(printInfo);
        } else {
            return null;
        }
    }
}
