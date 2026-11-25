package com.shopflow.catalog.config;

import com.shopflow.catalog.model.Product;
import com.shopflow.catalog.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Charge 10 produits au démarrage si la table est vide (consigne 6 du TP).
 * Désactivé pendant les tests grâce au profil.
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ProductRepository repository;

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        List<Product> products = List.of(
                product("KB-MECA-01", "Clavier mécanique", "Switchs rouges, rétroéclairage blanc", "89.90"),
                product("MS-ERGO-02", "Souris ergonomique", "Sans fil, 6 boutons", "49.90"),
                product("SC-27-4K", "Écran 27 pouces 4K", "Dalle IPS, 60 Hz", "349.00"),
                product("HS-BT-03", "Casque Bluetooth", "Réduction de bruit active", "129.00"),
                product("WC-HD-04", "Webcam Full HD", "1080p, micro intégré", "59.90"),
                product("SSD-1TB-05", "SSD NVMe 1 To", "PCIe 4.0", "79.90"),
                product("HUB-USBC-06", "Hub USB-C 7 en 1", "HDMI, USB 3.0, lecteur SD", "39.90"),
                product("LAP-15-07", "Ordinateur portable 15 pouces", "16 Go RAM, 512 Go SSD", "1199.00"),
                product("DESK-LMP-08", "Lampe de bureau LED", "Intensité réglable", "29.90"),
                product("CHR-ERG-09", "Chaise ergonomique", "Soutien lombaire réglable", "259.00")
        );
        repository.saveAll(products);
        log.info("{} produits chargés dans catalog_db", products.size());
    }

    private static Product product(String sku, String name, String description, String price) {
        return Product.builder()
                .sku(sku)
                .name(name)
                .description(description)
                .price(new BigDecimal(price))
                .build();
    }
}
