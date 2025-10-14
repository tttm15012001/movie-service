package com.ryanmovie.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ryanmovie.dto.response.CastResponseDto;
import com.ryanmovie.dto.response.MovieResponseDto;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_CAST;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TABLE_CAST)
public class CastModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "birthday")
    private Date birthday;

    @ManyToMany(mappedBy = "cast")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<MovieModel> movies = new HashSet<>();

    public CastResponseDto toCastResponseDto() {
        return CastResponseDto.builder()
                .id(this.getId())
                .name(this.getName())
                .nationality(this.getNationality())
                .birthday(this.getBirthday())
                .build();
    }
}
