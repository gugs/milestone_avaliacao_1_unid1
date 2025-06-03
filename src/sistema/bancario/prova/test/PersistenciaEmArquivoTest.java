package sistema.bancario.prova.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class PersistenciaEmArquivoTest {
    private Class<?> persistenciaClass;
    private Class<?> clienteClass;

    @BeforeEach
    public void setUp() throws Exception {
        persistenciaClass = Class.forName("sistema.bancario.prova.persistencia.PersistenciaEmArquivo");
        clienteClass = Class.forName("sistema.bancario.prova.model.Cliente");
    }
    @Test
    public void testPersistenciaClassStructure() throws Exception {
        assertTrue(hasMethod(persistenciaClass, "adicionarCliente", new Class[]{clienteClass}), "Método adicionarCliente deve estar presente");
        assertTrue(hasMethod(persistenciaClass, "removerCliente", new Class[]{String.class}), "Método removerCliente deve estar presente");
        assertTrue(hasMethod(persistenciaClass, "localizarClientePorCpf", new Class[]{String.class}), "Método localizarClientePorCpf deve estar presente");
        assertTrue(hasMethod(persistenciaClass, "atualizarCliente", new Class[]{clienteClass}), "Método atualizarCliente deve estar presente");
        assertTrue(hasMethod(persistenciaClass, "salvarEmArquivo", new Class[]{}), "Método salvarEmArquivo deve estar presente");
        assertTrue(hasMethod(persistenciaClass, "carregarDeArquivo", new Class[]{}), "Método carregarDeArquivo deve estar presente");
    }

    @Test
    public void testAdicionarCliente() throws Exception {
        Object persistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");

        Method adicionarCliente = persistenciaClass.getMethod("adicionarCliente", clienteClass);
        adicionarCliente.invoke(persistencia, cliente);

        Method localizarCliente = persistenciaClass.getMethod("localizarClientePorCpf", String.class);
        Object clienteLocalizado = localizarCliente.invoke(persistencia, "12345678901");
        assertNotNull(clienteLocalizado, "O cliente deve ser localizado por CPF após adição");
    }

    @Test
    public void testAtualizarCliente() throws Exception {
        Object persistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");

        Method adicionarCliente = persistenciaClass.getMethod("adicionarCliente", clienteClass);
        adicionarCliente.invoke(persistencia, cliente);

        Object clienteAtualizado = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João Silva");
        Method atualizarCliente = persistenciaClass.getMethod("atualizarCliente", clienteClass);
        atualizarCliente.invoke(persistencia, clienteAtualizado);

        Method localizarCliente = persistenciaClass.getMethod("localizarClientePorCpf", String.class);
        Object clienteLocalizado = localizarCliente.invoke(persistencia, "12345678901");
        Field nomeField = clienteClass.getDeclaredField("nome");
        nomeField.setAccessible(true);
        String nome = (String) nomeField.get(clienteLocalizado);
        assertEquals("João Silva", nome, "O nome do cliente deve ser atualizado");
    }

    @Test
    public void testRemoverCliente() throws Exception {
        Object persistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");

        Method adicionarCliente = persistenciaClass.getMethod("adicionarCliente", clienteClass);
        adicionarCliente.invoke(persistencia, cliente);

        Method removerCliente = persistenciaClass.getMethod("removerCliente", String.class);
        removerCliente.invoke(persistencia, "12345678901");

        Method localizarCliente = persistenciaClass.getMethod("localizarClientePorCpf", String.class);
        Object clienteLocalizado = localizarCliente.invoke(persistencia, "12345678901");
        assertNull(clienteLocalizado, "O cliente deve ser removido");
    }

    @Test
    public void testSalvarEmArquivo() throws Exception {
        Object persistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");

        Method adicionarCliente = persistenciaClass.getMethod("adicionarCliente", clienteClass);
        adicionarCliente.invoke(persistencia, cliente);

        Method salvarEmArquivo = persistenciaClass.getMethod("salvarEmArquivo");
        salvarEmArquivo.invoke(persistencia);

        File file = new File("dados");
        assertTrue(file.exists(), "O arquivo 'dados' deve ser criado após salvar");
    }

    @Test
    public void testCarregarDeArquivo() throws Exception {
        // Preparar dados no arquivo
        Object persistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");

        Method adicionarCliente = persistenciaClass.getMethod("adicionarCliente", clienteClass);
        adicionarCliente.invoke(persistencia, cliente);
        Method salvarEmArquivo = persistenciaClass.getMethod("salvarEmArquivo");
        salvarEmArquivo.invoke(persistencia);

        // Testar carregamento
        Object novaPersistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        Method carregarDeArquivo = persistenciaClass.getMethod("carregarDeArquivo");
        carregarDeArquivo.invoke(novaPersistencia);

        Method localizarCliente = persistenciaClass.getMethod("localizarClientePorCpf", String.class);
        Object clienteLocalizado = localizarCliente.invoke(novaPersistencia, "12345678901");
        assertNotNull(clienteLocalizado, "O cliente deve ser carregado do arquivo");

        // Testar carregamento com arquivo inexistente
        File file = new File("dados");
        if (file.exists()) {
            file.delete();
        }
        novaPersistencia = persistenciaClass.getDeclaredConstructor().newInstance();
        carregarDeArquivo.invoke(novaPersistencia);
        Field clientesField = persistenciaClass.getDeclaredField("clientes");
        clientesField.setAccessible(true);
        ArrayList<?> clientes = (ArrayList<?>) clientesField.get(novaPersistencia);
        assertTrue(clientes.isEmpty(), "O ArrayList de clientes deve ser vazio quando o arquivo não existe");
    }

    private boolean hasMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            clazz.getMethod(methodName, parameterTypes);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
