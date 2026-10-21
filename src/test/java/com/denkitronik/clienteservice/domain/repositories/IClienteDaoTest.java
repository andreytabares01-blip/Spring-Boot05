package com.denkitronik.clienteservice.domain.repositories;

import com.denkitronik.clienteservice.domain.entities.Cliente;
import com.denkitronik.clienteservice.domain.entities.Region;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// Imports para Spring Boot 4.x
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@DisplayName("Integration tests (data slice) — IClienteDao con H2")
class IClienteDaoTest {

    @Autowired
    private TestEntityManager em;     // para preparar datos de prueba

    @Autowired
    private IClienteDao clienteDao;   // el repositorio que estamos probando

    private Region region;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Preparamos datos con TestEntityManager
        region = new Region();
        region.setNombre("Asia");
        em.persist(region);

        cliente = new Cliente();
        cliente.setNombre("Grace");
        cliente.setApellido("Hopper");
        cliente.setEmail("grace@navy.mil");
        cliente.setRegion(region);
        em.persist(cliente);

        em.flush();  // fuerza el INSERT antes de que los tests lean datos
    }

    // ==========================================
    // TESTS DEL CODELAB
    // ==========================================

    @Test
    @DisplayName("findAllRegiones — devuelve las regiones persistidas")
    void findAllRegiones_debeRetornarListaDeRegiones() {
        // Act
        List<Region> regiones = clienteDao.findAllRegiones();

        // Assert
        assertThat(regiones).hasSize(1);
        assertThat(regiones.get(0).getNombre()).isEqualTo("Asia");
    }

    @Test
    @DisplayName("deleteById — elimina el cliente verificado con em.find")
    void deleteById_debeEliminarElCliente() {
        Long id = cliente.getId();

        clienteDao.deleteById(id);
        em.flush();

        // Verificamos directamente en el contexto de persistencia
        assertThat(em.find(Cliente.class, id)).isNull();
    }

    // ==========================================
    // TESTS EXTRA PARA COMPLETAR LOS 6
    // ==========================================

    @Test
    @DisplayName("save — guarda un cliente correctamente")
    void save_debeGuardarCliente() {
        Cliente nuevo = new Cliente();
        nuevo.setNombre("Alan");
        nuevo.setApellido("Turing");
        nuevo.setEmail("alan@bletchley.uk");
        nuevo.setRegion(region);

        Cliente guardado = clienteDao.save(nuevo);

        assertThat(guardado.getId()).isNotNull();
        assertThat(guardado.getEmail()).isEqualTo("alan@bletchley.uk");
    }

    @Test
    @DisplayName("findById — busca un cliente existente")
    void findById_debeRetornarCliente() {
        Optional<Cliente> encontrado = clienteDao.findById(cliente.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Grace");
    }

    @Test
    @DisplayName("existsById — verifica si un cliente existe")
    void existsById_debeRetornarTrueParaClienteExistente() {
        boolean existe = clienteDao.existsById(cliente.getId());
        boolean noExiste = clienteDao.existsById(999L);

        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }

    @Test
    @DisplayName("delete — elimina un cliente pasando la entidad")
    void delete_debeEliminarEntidad() {
        clienteDao.delete(cliente);
        em.flush();

        assertThat(em.find(Cliente.class, cliente.getId())).isNull();
    }
}
