package io.aitos.hackathon.pojo;

import lombok.Data;

@Data
public class Ciphertext {

    private String idType;
    private String classify;
    private String name;
    private String sex;
    private String nation;
    private String birthDate;
    private String address;
    private String idnum;
    private String signingOrganization;
    private String beginTime;
    private String endTime;
    private String picture;


    @Override
    public String toString() {
        return "Ciphertext{" +
                "idType='" + idType + '\'' +
                ", classify='" + classify + '\'' +
                ", name='" + name + '\'' +
                ", sex='" + sex + '\'' +
                ", nation='" + nation + '\'' +
                ", birthDate='" + birthDate + '\'' +
                ", address='" + address + '\'' +
                ", idnum='" + idnum + '\'' +
                ", signingOrganization='" + signingOrganization + '\'' +
                ", beginTime='" + beginTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
