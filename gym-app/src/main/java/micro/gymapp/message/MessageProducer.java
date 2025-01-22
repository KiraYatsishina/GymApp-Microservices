package micro.gymapp.message;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MessageProducer {

      private static final String TRAINING_QUEUE = "training.queue";

      @Autowired
      private JmsTemplate jmsTemplate;

      @Transactional
      public void send(String trainerUsername,
                       String firstName,
                       String lastName,
                       boolean status,
                       String date,
                       int duration,
                       String actionType){
            jmsTemplate.convertAndSend(TRAINING_QUEUE, trainerUsername, new MessagePostProcessor() {
                  @Override
                  public Message postProcessMessage(Message message) throws JMSException {
                        message.setStringProperty("trainerUsername", trainerUsername);
                        message.setStringProperty("firstName", firstName);
                        message.setStringProperty("lastName", lastName);
                        message.setBooleanProperty("status", status);
                        message.setStringProperty("trainingDate", date);
                        message.setIntProperty("duration", duration);
                        message.setStringProperty("actionType", actionType);
                        return message;
                  }
            });
      }
}