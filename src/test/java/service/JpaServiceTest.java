package service;

import domain.*;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class JpaServiceTest {

    private static final String PERSISTENCE_UNIT_NAME = "runner";

    private EntityManagerFactory factory;

    @BeforeEach
    void cleanDatabase() {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }

    @AfterEach
    void closeFactory() {
        factory.close();
    }

    @Test
    void finding_all_runners_works() {
        var service = new JpaService(factory);
        var runners = List.of(
                new Runner(null, "Alfred", Gender.MALE, List.of()),
                new Runner(null, "Bernd", Gender.MALE, List.of()),
                new Runner(null, "Christina", Gender.FEMALE, List.of())
        );
        var savedRunners = runners
                .stream()
                .map(service::save)
                .collect(Collectors.toList());

        var allSaved = service.findAll();
        assertThat(allSaved)
                .containsExactlyInAnyOrderElementsOf(savedRunners);
    }

    @Nested
    class SavingRunner {

        @Test
        void works() {
            var service = new JpaService(factory);
            var runner = new Runner(null, "name", Gender.MALE, List.of());

            var saved = service.save(runner);

            assertThat(saved)
                    .extracting(Runner::getId)
                    .isNotNull();
            var queried = service.findById(saved.getId());
            assertThat(queried)
                    .get()
                    .isEqualTo(saved);
        }

        @Test
        void updates_existing() {
            var service = new JpaService(factory);
            var runner = new Runner("name", Gender.MALE);
            var saved = service.save(runner);

            saved.setGender(Gender.DIVERSE);
            service.save(saved);

            var queried = service.findById(saved.getId());
            assertThat(queried)
                    .get()
                    .isEqualTo(saved);
        }
    }

    @Nested
    class SavingRun {

        @Test
        void also_saves_runner() {
            var service = new JpaService(factory);
            var runner = new Runner(null, "name", Gender.MALE, List.of());
            var run = new Run(null, LocalDate.of(2020, 1, 1), 1.0, 1, runner);

            var saved = service.save(run);

            var savedRunner = service.findById(saved.getRunner().getId());
            assertThat(savedRunner)
                    .get()
                    .isEqualTo(saved.getRunner());
        }
    }

    @Nested
    class FindingById {

        @Test
        void loads_all_runs() {
            var service = new JpaService(factory);
            var runner = new Runner(null, "name", Gender.MALE, List.of());
            var savedRunner = service.save(runner);
            var run = new Run(null, LocalDate.of(2020, 1, 1), 1.0, 1, savedRunner);
            service.save(run);

            var queried = service.findById(savedRunner.getId());

            assertThat(queried)
                    .get()
                    .isEqualTo(savedRunner)
                    .extracting(Runner::getRuns)
                    .asList()
                    .isNotEmpty();
        }

        @Test
        void returns_empty_for_unknown_id() {
            var service = new JpaService(factory);

            var queried = service.findById(404);
            assertThat(queried)
                    .isEmpty();
        }
    }

    @Nested
    class GettingAverageSpeed {

        @Test
        void works() {
            var service = new JpaService(factory);
            var runner = new Runner(null, "name", Gender.MALE, List.of());
            var savedRunner = service.save(runner);
            var runs = List.of(
                    new Run(null, LocalDate.now(), 1.0, 20, savedRunner),
                    new Run(null, LocalDate.now(), 2.0, 10, savedRunner)
            );
            runs.forEach(service::save);

            var speed = service.getAverageSpeed(savedRunner);

            assertThat(speed.getAsDouble())
                    .isCloseTo(6.0, Offset.offset(1e-7));
        }

        @Test
        void returns_empty_without_runs() {
            var service = new JpaService(factory);
            var runner = new Runner(null, "name", Gender.MALE, List.of());
            var savedRunner = service.save(runner);

            var speed = service.getAverageSpeed(savedRunner);

            assertThat(speed)
                    .isEmpty();
        }
    }

    @Nested
    class GetsAllRunnersWithFinishedMarathon {

        @Test
        void works() {
            var service = new JpaService(factory);
            var runners = List.of(
                    new Runner(null, "2 Marathon finishes", Gender.DIVERSE, List.of()),
                    new Runner(null, "1 Marathon finish", Gender.FEMALE, List.of()),
                    new Runner(null, "No Marathon finish", Gender.MALE, List.of())
            );
            var savedRunners = runners
                    .stream()
                    .map(service::save)
                    .collect(Collectors.toList());
            var runs = List.of(
                    new Run(null, LocalDate.now(), 43.0, 100, savedRunners.get(0)),
                    new Run(null, LocalDate.now(), 42.195, 500, savedRunners.get(0)),
                    new Run(null, LocalDate.now(), 42.195, 500, savedRunners.get(1)),
                    new Run(null, LocalDate.now(), 42.0, 10, savedRunners.get(2))
            );
            runs.forEach(service::save);

            var finishers = service.getAllRunnersWithFinishedMarathon();

            assertThat(finishers)
                    .containsExactlyInAnyOrderElementsOf(savedRunners.subList(0, 2));
        }
    }
}