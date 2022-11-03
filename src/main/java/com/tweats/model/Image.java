package com.tweats.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.*;

@Entity
@Table(name = "image")
@Getter
@Setter
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

    private String name;
    @Column(name = "content_type")
    private String contentType;

    @Lob
    private byte[] data;

    private Long size;

    public Image(String name, String contentType, byte[] data, Long size) {
        this.name = name;
        this.contentType = contentType;
        this.data = data;
        this.size = size;
    }
}
