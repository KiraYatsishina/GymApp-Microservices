package micro.gymapp.unit.message;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import micro.gymapp.message.MessageProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;

import static org.mockito.Mockito.*;

class MessageProducerTest {

      private static final String TRAINING_QUEUE = "training.queue";

      @InjectMocks
      private MessageProducer messageProducer;

      @Mock
      private JmsTemplate jmsTemplate;

      @Mock
      private Message message;

      @Captor
      private ArgumentCaptor<MessagePostProcessor> messagePostProcessorCaptor;

      @BeforeEach
      void setUp() {
            MockitoAnnotations.openMocks(this);
      }

      @Test
      void testSend_withValidInputs() throws JMSException {
            String trainerUsername = "john_doe";
            String firstname = "john";
            String lastname = "doe";
            boolean status = true;
            String trainingDate = "2025-01-13";
            int duration = 60;
            String actionType = "ADD";

            messageProducer.send(trainerUsername, firstname, lastname, status, trainingDate, duration, actionType);

            verify(jmsTemplate, times(1)).convertAndSend(eq(TRAINING_QUEUE), eq(trainerUsername), messagePostProcessorCaptor.capture());

            MessagePostProcessor postProcessor = messagePostProcessorCaptor.getValue();
            postProcessor.postProcessMessage(message);

            verify(message).setStringProperty("trainerUsername", trainerUsername);
            verify(message).setStringProperty("trainingDate", trainingDate);
            verify(message).setIntProperty("duration", duration);
            verify(message).setStringProperty("actionType", actionType);
      }

      @Test
      void testSend_whenJMSExceptionOccurs() throws JMSException {
            String trainerUsername = "john_doe";
            String trainingDate = "2025-01-13";
            int duration = 60;
            String firstname = "john";
            String lastname = "doe";
            boolean status = true;
            String actionType = "ADD";

            doThrow(new JMSException("Error processing message")).when(message).setStringProperty(anyString(), anyString());

            messageProducer.send(trainerUsername, firstname, lastname, status, trainingDate, duration, actionType);

            verify(jmsTemplate, times(1)).convertAndSend(eq(TRAINING_QUEUE), eq(trainerUsername), any(MessagePostProcessor.class));
      }
}
