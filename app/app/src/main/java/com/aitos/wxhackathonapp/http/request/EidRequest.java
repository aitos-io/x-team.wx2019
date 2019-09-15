package com.aitos.wxhackathonapp.http.request;

public class EidRequest {
    private ReqHeader header;
    private ReqContent content;

    public ReqHeader getHeader() {
        return header;
    }

    public void setHeader(ReqHeader header) {
        this.header = header;
    }

    public ReqContent getContent() {
        return content;
    }

    public void setContent(ReqContent content) {
        this.content = content;
    }
}