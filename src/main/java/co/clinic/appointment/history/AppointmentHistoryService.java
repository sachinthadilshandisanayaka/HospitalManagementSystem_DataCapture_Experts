package co.clinic.appointment.history;

import co.clinic.appointment.entity.Appointment;
import co.clinic.appointment.util.AppointmentHistoryAction;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AppointmentHistoryService {

    private final AppointmentHistoryRepository historyRepository;

    public AppointmentHistoryService(AppointmentHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public void record(Appointment appointment, AppointmentHistoryAction action, String performedBy) {
        AppointmentHistory history = AppointmentHistory.builder()
                .appointmentId(appointment.getId())
                .action(action)
                .statusAfter(appointment.getStatus())
                .notes(appointment.getNotes())
                .performedBy(performedBy)
                .performedAt(Instant.now())
                .build();

        historyRepository.save(history);
    }
}
