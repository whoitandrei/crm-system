package ru.shift.zverev.crm_system.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.shift.zverev.crm_system.model.Seller;
import ru.shift.zverev.crm_system.model.Transaction;
import ru.shift.zverev.crm_system.repository.SellerRepository;
import ru.shift.zverev.crm_system.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final SellerRepository sellerRepository;
    private final TransactionRepository transactionRepository;

    @Autowired
    public DataInitializer(SellerRepository sellerRepository, TransactionRepository transactionRepository) {
        this.sellerRepository = sellerRepository;
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Seller seller1 = new Seller("Иван Иванов", LocalDateTime.now().minusMonths(6));
        seller1.setContactInfo("ivan@example.com");
        
        Seller seller2 = new Seller("Мария Петрова", LocalDateTime.now().minusMonths(3));
        seller2.setContactInfo("maria@example.com");
        
        Seller seller3 = new Seller("Петр Сидоров", LocalDateTime.now().minusMonths(1));
        seller3.setContactInfo("petr@example.com");

        seller1 = sellerRepository.save(seller1);
        seller2 = sellerRepository.save(seller2);
        seller3 = sellerRepository.save(seller3);

        Transaction transaction1 = new Transaction(seller1, new BigDecimal("150.00"), 
                Transaction.PaymentType.CARD, LocalDateTime.now().minusDays(10));
        
        Transaction transaction2 = new Transaction(seller1, new BigDecimal("250.50"), 
                Transaction.PaymentType.CASH, LocalDateTime.now().minusDays(5));

        Transaction transaction3 = new Transaction(seller1, new BigDecimal("250.50"),
                Transaction.PaymentType.CASH, LocalDateTime.now().minusDays(30));
        Transaction transaction4 = new Transaction(seller1, new BigDecimal("250.50"),
                Transaction.PaymentType.CASH, LocalDateTime.now().minusDays(31));
        Transaction transaction5 = new Transaction(seller1, new BigDecimal("250.50"),
                Transaction.PaymentType.CASH, LocalDateTime.now().minusDays(32));
        
        Transaction transaction6 = new Transaction(seller2, new BigDecimal("75.25"),
                Transaction.PaymentType.CARD, LocalDateTime.now().minusDays(3));
        
        Transaction transaction7 = new Transaction(seller2, new BigDecimal("300.00"),
                Transaction.PaymentType.CARD, LocalDateTime.now().minusDays(1));
        
        Transaction transaction8 = new Transaction(seller3, new BigDecimal("45.50"),
                Transaction.PaymentType.CASH, LocalDateTime.now().minusHours(2));

        transactionRepository.save(transaction1);
        transactionRepository.save(transaction2);
        transactionRepository.save(transaction3);
        transactionRepository.save(transaction4);
        transactionRepository.save(transaction5);
        transactionRepository.save(transaction6);
        transactionRepository.save(transaction7);
        transactionRepository.save(transaction8);

        System.out.println("Тестовые данные загружены:");
        System.out.println("- Создано продавцов: " + sellerRepository.count());
        System.out.println("- Создано транзакций: " + transactionRepository.count());
    }
}
