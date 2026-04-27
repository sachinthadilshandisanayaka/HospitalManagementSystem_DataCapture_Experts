package co.clinic.appointment.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationStrategyFactory {

    private static final Logger log = LoggerFactory.getLogger(NotificationStrategyFactory.class);

    private final Map<String, NotificationStrategy> strategyMap;

    public NotificationStrategyFactory(List<NotificationStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(NotificationStrategy::getChannel, Function.identity()));
        log.info("Registered notification channels: {}", strategyMap.keySet());
    }

    public NotificationStrategy getStrategy(String channel) {
        NotificationStrategy strategy = strategyMap.get(channel.toUpperCase());
        if (strategy == null) {
            log.warn("No strategy for channel '{}', falling back to CONSOLE", channel);
            return strategyMap.get("CONSOLE");
        }
        return strategy;
    }

    public void notifyAll(NotificationEvent event) {
        strategyMap.values().forEach(strategy -> strategy.send(event));
    }
}
