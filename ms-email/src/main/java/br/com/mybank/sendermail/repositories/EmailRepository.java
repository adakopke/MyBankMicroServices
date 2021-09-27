package br.com.mybank.sendermail.repositories;

import br.com.mybank.sendermail.models.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailModel, Long> {
}
