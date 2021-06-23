package sourcefx.config;

import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Configuration
public class JacksonConfiguration {
	private static final String DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
		return builder -> {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
			DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
			builder.timeZone(TimeZone.getDefault());
			builder.deserializers(new LocalDateDeserializer(dateFormatter));
			builder.deserializers(new LocalDateTimeDeserializer(dateTimeFormatter));
			builder.serializers(new LocalDateSerializer(dateFormatter));
			builder.serializers(new LocalDateTimeSerializer(dateTimeFormatter));
		};
	}
}
