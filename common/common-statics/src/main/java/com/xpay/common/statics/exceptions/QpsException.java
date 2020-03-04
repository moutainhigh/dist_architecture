package com.xpay.common.statics.exceptions;

public class QpsException extends BizException {
    private boolean isBlock;
    private Throwable ex;

    public QpsException(){
        super();
    }

    public QpsException(Throwable ex){
        super();
        this.ex = ex;
    }

    public QpsException(boolean isBlock, Throwable ex){
        super();
        this.isBlock = isBlock;
        this.ex = ex;
    }

    public boolean getIsBlock() {
        return isBlock;
    }

    public void setIsBlock(boolean block) {
        isBlock = block;
    }

    public Throwable getEx() {
        return ex;
    }

    public void setEx(Throwable ex) {
        this.ex = ex;
    }
}
