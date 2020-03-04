package com.xpay.common.statics.enums.message;

public enum EmailFromEnum {
    ALIYUN_JOINPAY("joinpay@aliyun.com", "阿里云公用"),

    ;
    private String account;
    private String desc;

    private EmailFromEnum(String account, String desc){
        this.account = account;
        this.desc = desc;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static EmailFromEnum getEnum(String account){
        EmailFromEnum[] values = EmailFromEnum.values();
        for(int i=0; i<values.length; i++){
            if(values[i].getAccount().equals(account)){
                return values[i];
            }
        }
        return null;
    }
}
