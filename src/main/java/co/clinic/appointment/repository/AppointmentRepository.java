package co.clinic.appointment.repository;

import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.util.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {

    // Overlap detection for conflict rule
    @Query("{ 'practitionerId': ?0, 'status': 'BOOKED', 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 } }")
    List<Appointment> findOverlapping(String practitionerId, Instant newStart, Instant newEnd);

    // Filter: practitionerId + date range + pagination
    @Query("{ 'practitionerId': ?0, 'startTime': { $gte: ?1, $lt: ?2 } }")
    Page<Appointment> findByPractitionerIdAndDateRange(
            String practitionerId, Instant dayStart, Instant dayEnd, Pageable pageable);

    // Filter: practitionerId only + pagination
    Page<Appointment> findByPractitionerId(String practitionerId, Pageable pageable);

    // Filter: date range only (all practitioners) + pagination
    @Query("{ 'startTime': { $gte: ?0, $lt: ?1 } }")
    Page<Appointment> findByDateRange(Instant dayStart, Instant dayEnd, Pageable pageable);

    // Filter: status only + pagination
    Page<Appointment> findByStatus(AppointmentStatus status, Pageable pageable);

    // Filter: practitionerId + status + pagination
    Page<Appointment> findByPractitionerIdAndStatus(
            String practitionerId, AppointmentStatus status, Pageable pageable);
}
