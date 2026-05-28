package com.canvaclone.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "elements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Element {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;

    @Column(columnDefinition = "TEXT")
    private String properties;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "design_id", nullable = false)
    private Design design;

    private Integer orderIndex;
}
