package com.bialas.robert.devicemanager.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@EqualsAndHashCode
@Builder
@Table
public class Brand {

    @Id
    private String name;

    @Transient
    @OneToMany(mappedBy = "brand",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    private List<Device> devices;
}
