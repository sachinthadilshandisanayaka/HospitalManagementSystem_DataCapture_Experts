package co.clinic.appointment.history;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentHistoryRepository extends MongoRepository<AppointmentHistory, String> {

    List<AppointmentHistory> findByAppointmentIdOrderByPerformedAtAsc(String appointmentId);
}
