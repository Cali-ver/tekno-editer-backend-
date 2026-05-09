package com.canvaclone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "designs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Design {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Integer width;
    private Integer height;
    @Column(columnDefinition = "LONGTEXT")
    private String thumbnail;

    @Column(columnDefinition = "LONGTEXT")
    private String jsonData;

    @Column(name = "access_level", columnDefinition = "VARCHAR(255) DEFAULT 'ONLY_YOU'")
    @Builder.Default
    private String accessLevel = "ONLY_YOU";

    @Column(name = "is_public", columnDefinition = "BIT DEFAULT 0")
    @Builder.Default
    private boolean isPublic = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "design", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Element> elements = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
