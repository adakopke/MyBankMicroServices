package br.com.mybank.emailserverms.repositories;
import br.com.mybank.emailserverms.models.EmailModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailRepository extends JpaRepository<EmailModel, Long> {
}
