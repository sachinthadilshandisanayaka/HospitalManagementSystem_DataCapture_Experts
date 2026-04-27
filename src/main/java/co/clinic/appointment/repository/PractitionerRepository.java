package co.clinic.appointment.repository;

import co.clinic.appointment.entity.Practitioner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PractitionerRepository extends MongoRepository<Practitioner, String> {

    Optional<Practitioner> findByRegistrationNo(String registrationNo);

    boolean existsByRegistrationNo(String registrationNo);

    Page<Practitioner> findBySpecialtyIgnoreCase(String specialty, Pageable pageable);
}
