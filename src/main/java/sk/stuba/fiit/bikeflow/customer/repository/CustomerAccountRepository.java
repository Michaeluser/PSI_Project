package sk.stuba.fiit.bikeflow.customer.repository;

import sk.stuba.fiit.bikeflow.customer.domain.CustomerAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerAccountRepository extends JpaRepository<CustomerAccount, UUID> {
}
