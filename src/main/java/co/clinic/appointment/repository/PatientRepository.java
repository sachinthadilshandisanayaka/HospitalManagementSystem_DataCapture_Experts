package co.clinic.appointment.repository;

import co.clinic.appointment.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {

    Optional<Patient> findByNationalId(String nationalId);

    Page<Patient> findByNationalIdContainingIgnoreCase(String nationalId, Pageable pageable);

    Page<Patient> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByNationalId(String nationalId);
}
