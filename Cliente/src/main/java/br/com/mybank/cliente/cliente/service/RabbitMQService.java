package br.com.mybank.cliente.cliente.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RabbitMQService {

        private final RabbitTemplate rabbitTemplate;

        private final ObjectMapper objectMapper;

        public void enviaMensagem(String nomeFila, Object mensagem){
            try {
                String mensagemJson = this.objectMapper.writeValueAsString(mensagem);
                System.out.println(mensagemJson);
                Message msgfinal = MessageBuilder.withBody(mensagemJson.getBytes()).setContentType("application/json").build();
                this.rabbitTemplate.convertAndSend(nomeFila, msgfinal);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
}
