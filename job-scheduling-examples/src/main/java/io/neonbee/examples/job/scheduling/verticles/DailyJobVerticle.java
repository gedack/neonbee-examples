package io.neonbee.examples.job.scheduling.verticles;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjuster;

import io.neonbee.NeonBeeDeployable;
import io.neonbee.data.DataContext;
import io.neonbee.job.JobSchedule;
import io.neonbee.job.JobVerticle;
import io.neonbee.logging.LoggingFacade;
import io.vertx.core.Future;

@NeonBeeDeployable // Load and register this verticle automatically
public class DailyJobVerticle extends JobVerticle {

    private static final LoggingFacade LOGGER = LoggingFacade.create();

    private static final int DELAY_WEEKDAY_IN_SECONDS = 5;

    private static final int DELAY_WEEKEND_IN_SECONDS = 15;

    private static final TemporalAdjuster TEMPORAL_ADJUSTER =
            temporal -> isWeekday() ? temporal.plus(DELAY_WEEKDAY_IN_SECONDS, ChronoUnit.SECONDS)
                    : temporal.plus(DELAY_WEEKEND_IN_SECONDS, ChronoUnit.SECONDS);

    public DailyJobVerticle() {
        super(new JobSchedule(TEMPORAL_ADJUSTER));
    }

    @Override
    public Future<Void> execute(DataContext context) {
        LOGGER.info("Executing the DailyJobVerticle");
        return Future.succeededFuture();
    }

    private static boolean isWeekday() {
        final String day = LocalDate.now(ZoneId.systemDefault()).getDayOfWeek().toString();
        switch (day) {
        case "MONDAY":
        case "TUESDAY":
        case "WEDNESDAY":
        case "THURSDAY":
        case "FRIDAY":
            LOGGER.debug("It's a weekday ({}), the interval will be {} seconds.", day, DELAY_WEEKDAY_IN_SECONDS);
            return true;
        default: // SATURDAY, SUNDAY
            LOGGER.debug("It's weekend ({}), the interval will be {} seconds.", day, DELAY_WEEKEND_IN_SECONDS);
            return false;
        }
    }

}
