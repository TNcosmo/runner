package domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Runner {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Runner runner = (Runner) o;
        return id != null && Objects.equals(id, runner.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Runner(String name, Gender gender) {
        this.name = name;
        this.gender = gender;
    }

    public Runner(Long id, String name, Gender gender, List<Run> runs) {
        this.id = id;
        this.name = name;
        this.gender = gender;
    }

    @Id
    @GeneratedValue
    private Long id;

    @Size(max = 30)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    private Gender gender;


    @OneToMany(mappedBy = "runner")
    private List<Run> runs = new ArrayList<>();

}
