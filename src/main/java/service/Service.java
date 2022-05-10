package service;

import domain.Run;
import domain.Runner;

import java.util.*;

public interface Service {

    double MARATHON_DISTANCE = 42.195;

    List<Runner> findAll();

    /**
     * Computes the average speed across all runs. Not to be confused by the average of the speed per run.
     *
     * @param runner the runner
     * @return the speed wrapped in an Optional; empty without any runs
     */
    OptionalDouble getAverageSpeed(Runner runner);

    /**
     * Returns the runner with the given id including all associated runs.
     *
     * @param id the id
     * @return the runner wrapped in an Optional; empty for unknown id
     */
    Optional<Runner> findById(long id);

    /**
     * Returns all runners who have at least one run covering at least the Marathon distance.
     *
     * @return qualifying runners
     */
    Set<Runner> getAllRunnersWithFinishedMarathon();

    Runner save(Runner runner);

    Run save(Run run);
}
