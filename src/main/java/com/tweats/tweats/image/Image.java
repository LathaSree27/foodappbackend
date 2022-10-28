package com.tweats.tweats.image;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(generator =  "uuid")
    @GenericGenerator(name = "uuid" , strategy = "uuid2")
    private String id;

    private String name;

    private String contentType;

    @Lob
    private byte[] data;

    private Long size;

    public Image() {
    }

    public Image(String name, String contentType, byte[] data, Long size) {
        this.name = name;
        this.contentType = contentType;
        this.data = data;
        this.size = size;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getData() {
        return data;
    }

    public Long getSize() {
        return size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
