package config;

import com.typesafe.config.Config;
import scala.concurrent.duration.Duration;

public class AggregatorConfig {
    public final Duration timeout;

    public AggregatorConfig(Duration timeout) {
        this.timeout = timeout;
    }

    public AggregatorConfig(Config config) {
        this(Duration.fromNanos(config.getDuration("timeout").toNanos()));
    }
}
