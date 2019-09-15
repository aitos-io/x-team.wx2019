package com.aitos.wxhackathonapp.http.response;

public class EidResponse {
    private EidRespHeader header;
    private EidRespContent content;

    public EidRespHeader getHeader() {
        return header;
    }

    public void setHeader(EidRespHeader header) {
        this.header = header;
    }

    public EidRespContent getContent() {
        return content;
    }

    public void setContent(EidRespContent content) {
        this.content = content;
    }
}