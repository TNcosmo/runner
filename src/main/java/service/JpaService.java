package service;

import domain.Run;
import domain.Runner;


import jakarta.persistence.*;

import java.util.*;

public class JpaService implements Service {

    private final EntityManager manager;

    public JpaService(EntityManagerFactory entityManagerFactory) {
        this.manager = entityManagerFactory.createEntityManager();
    }


    @Override
    public List<Runner> findAll() {
        try {
            return manager.createQuery("select runner from Runner runner", Runner.class).getResultList();
        } finally {
            manager.clear();
        }
    }

    @Override
    public OptionalDouble getAverageSpeed(Runner runner) {
        try {
            Double avgSpeed = (Double) manager.createQuery("""
                            select sum(run.distanceInKm)/sum(run.minutes)*60
                             from Run run
                             where run.runner = :runner""")
                    .setParameter("runner", runner)
                    .getSingleResult();
            if (avgSpeed == null) return OptionalDouble.empty();
            else return OptionalDouble.of(avgSpeed);
        } finally {
            manager.clear();
        }
    }

    @Override
    public Optional<Runner> findById(long id) {
        try {
            return Optional.ofNullable(manager.createQuery("""
                    select runner 
                    from Runner runner 
                    left join fetch runner.runs 
                    where runner.id = :id
                    """, Runner.class).setParameter("id", id).getSingleResult());

        } catch (NoResultException e
        ) {
            return Optional.empty();
        } finally {
            manager.clear();

        }
    }

    @Override
    public Set<Runner> getAllRunnersWithFinishedMarathon() {
        double marathonLength = 42.195;
        try {
            return new HashSet<>(manager.
                    createQuery("""
                            select distinct run.runner
                             from Run run
                              where run.distanceInKm >= :marathonLength""", Runner.class)
                    .setParameter("marathonLength", marathonLength)
                    .getResultList());
        } finally {
            manager.clear();
        }
    }

    @Override
    public Runner save(Runner runner) {
        try {
            manager.getTransaction().begin();
            if (runner.getId() == null) manager.persist(runner);
            else {
                runner = manager.merge(runner);
            }
            manager.getTransaction().commit();
            return runner;
        } finally {
            manager.clear();
        }
    }

    @Override
    public Run save(Run run) {
        var transaction = manager.getTransaction();
        transaction.begin();

        run.setRunner(manager.merge(run.getRunner()));
        //manager.clear();
        manager.persist(run);
        transaction.commit();
        manager.clear();
        return run;
    }
}
