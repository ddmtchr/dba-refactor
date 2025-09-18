package com.ddmtchr.dbarefactor.entity;

import com.ddmtchr.dbarefactor.security.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

@Entity
@Table(name = "estate")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Check(constraints = "name <> ''")
    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(nullable = false)
    private Long price;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
