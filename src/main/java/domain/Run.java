package domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.Objects;

@NoArgsConstructor
@Setter
@Getter
@Entity
public class Run {

    public Run(Long id, LocalDate date, Double distanceInKm, Integer minutes, Runner runner) {
        this.id = id;
        this.date = date;
        this.distanceInKm = distanceInKm;
        this.minutes = minutes;
        this.runner = runner;
    }

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate date;

    @Positive
    private Double distanceInKm;

    @Positive
    private Integer minutes;

    @ManyToOne
    private Runner runner;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Run run = (Run) o;
        return id != null && Objects.equals(id, run.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
