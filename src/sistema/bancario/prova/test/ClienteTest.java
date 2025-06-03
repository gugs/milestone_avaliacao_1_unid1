package sistema.bancario.prova.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ClienteTest {
    private Class<?> clienteClass;
    private Class<?> contaClass;

    @BeforeEach
    public void setUp() throws Exception {
        clienteClass = Class.forName("sistema.bancario.prova.model.Cliente");
        contaClass = Class.forName("sistema.bancario.prova.model.Conta");
        assertTrue(java.io.Serializable.class.isAssignableFrom(clienteClass), "Cliente deve implementar Serializable");
    }

    @Test
    public void testClienteClassStructure() throws Exception {
        assertTrue(hasField(clienteClass, "cpf", String.class, true), "Atributo cpf (String) deve estar presente e ser privado");
        assertTrue(hasField(clienteClass, "nome", String.class, true), "Atributo nome (String) deve estar presente e ser privado");
        assertTrue(hasField(clienteClass, "contas", ArrayList.class, true), "Atributo contas (ArrayList) deve estar presente e ser privado");

        assertTrue(hasMethod(clienteClass, "adicionarConta", new Class[]{contaClass}), "Método adicionarConta deve estar presente");
        assertTrue(hasMethod(clienteClass, "removerConta", new Class[]{String.class}), "Método removerConta deve estar presente");
        assertTrue(hasMethod(clienteClass, "localizarContaPorNumero", new Class[]{String.class}), "Método localizarContaPorNumero deve estar presente");
        assertTrue(hasMethod(clienteClass, "atualizarConta", new Class[]{contaClass}), "Método atualizarConta deve estar presente");
        assertTrue(hasMethod(clienteClass, "toString", new Class[]{}), "Método toString deve estar presente");
        assertTrue(hasMethod(clienteClass, "equals", new Class[]{Object.class}), "Método equals deve estar presente");
        assertTrue(hasMethod(clienteClass, "hashCode", new Class[]{}), "Método hashCode deve estar presente");
    }

    @Test
    public void testAdicionarConta() throws Exception {
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method adicionarConta = clienteClass.getMethod("adicionarConta", contaClass);
        adicionarConta.invoke(cliente, conta);

        Field contasField = clienteClass.getDeclaredField("contas");
        contasField.setAccessible(true);
        ArrayList<?> contas = (ArrayList<?>) contasField.get(cliente);
        assertEquals(1, contas.size(), "A conta deve ser adicionada ao ArrayList");
    }

    @Test
    public void testLocalizarContaPorNumero() throws Exception {
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method adicionarConta = clienteClass.getMethod("adicionarConta", contaClass);
        adicionarConta.invoke(cliente, conta);

        Method localizarConta = clienteClass.getMethod("localizarContaPorNumero", String.class);
        Object contaLocalizada = localizarConta.invoke(cliente, "1001");
        assertNotNull(contaLocalizada, "A conta deve ser localizada por número");

        contaLocalizada = localizarConta.invoke(cliente, "9999");
        assertNull(contaLocalizada, "Deve retornar null para número de conta inexistente");
    }

    @Test
    public void testAtualizarConta() throws Exception {
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method adicionarConta = clienteClass.getMethod("adicionarConta", contaClass);
        adicionarConta.invoke(cliente, conta);

        Object novaConta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1500.00"), true, LocalDateTime.now());
        Method atualizarConta = clienteClass.getMethod("atualizarConta", contaClass);
        atualizarConta.invoke(cliente, novaConta);

        Method localizarConta = clienteClass.getMethod("localizarContaPorNumero", String.class);
        Object contaLocalizada = localizarConta.invoke(cliente, "1001");
        Field saldoField = contaClass.getDeclaredField("saldo");
        saldoField.setAccessible(true);
        BigDecimal saldo = (BigDecimal) saldoField.get(contaLocalizada);
        assertEquals(new BigDecimal("1500.00"), saldo, "O saldo da conta deve ser atualizado");
    }

    @Test
    public void testRemoverConta() throws Exception {
        Object cliente = clienteClass.getDeclaredConstructor(String.class, String.class).newInstance("12345678901", "João");
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method adicionarConta = clienteClass.getMethod("adicionarConta", contaClass);
        adicionarConta.invoke(cliente, conta);

        Method removerConta = clienteClass.getMethod("removerConta", String.class);
        removerConta.invoke(cliente, "1001");

        Field contasField = clienteClass.getDeclaredField("contas");
        contasField.setAccessible(true);
        ArrayList<?> contas = (ArrayList<?>) contasField.get(cliente);
        assertEquals(0, contas.size(), "A conta deve ser removida do ArrayList");
    }

    private boolean hasField(Class<?> clazz, String fieldName, Class<?> fieldType, boolean isPrivate) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            return field.getType().equals(fieldType) && (!isPrivate || java.lang.reflect.Modifier.isPrivate(field.getModifiers()));
        } catch (NoSuchFieldException e) {
            return false;
        }
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
