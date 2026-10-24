package com.telecom.netops.ingestion;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.telecom.netops.events.TelecomKpiEvent;

@Configuration
public class KafkaErrorHandlingConfig {

	@Bean
	public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<String, TelecomKpiEvent> kafkaTemplate) {

		DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
				(ConsumerRecord<?, ?> record, Exception exception) -> new org.apache.kafka.common.TopicPartition(
						record.topic() + ".DLT", record.partition()));

		// Initial attempt + 2 retries, with a 1-second delay.
		DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 2L));

		// Validation failures should go directly to the DLT.
		errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);

		return errorHandler;
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory(
			ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
			ConsumerFactory<Object, Object> consumerFactory, DefaultErrorHandler kafkaErrorHandler) {

		ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();

		configurer.configure(factory, consumerFactory);
		factory.setCommonErrorHandler(kafkaErrorHandler);

		return factory;
	}

}
