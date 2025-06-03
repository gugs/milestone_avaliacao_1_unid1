package sistema.bancario.prova.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaTest {
    private Class<?> contaClass;

    @BeforeEach
    public void setUp() throws Exception {
        contaClass = Class.forName("sistema.bancario.prova.model.Conta");
        assertTrue(java.io.Serializable.class.isAssignableFrom(contaClass), "Conta deve implementar Serializable");
    }

    @Test
    public void testContaClassStructure() throws Exception {
        assertTrue(hasField(contaClass, "numeroConta", String.class, true), "Atributo numeroConta (String) deve estar presente e ser privado");
        assertTrue(hasField(contaClass, "saldo", BigDecimal.class, true), "Atributo saldo (BigDecimal) deve estar presente e ser privado");
        assertTrue(hasField(contaClass, "status", boolean.class, true), "Atributo status (boolean) deve estar presente e ser privado");
        assertTrue(hasField(contaClass, "dataAbertura", LocalDateTime.class, true), "Atributo dataAbertura (LocalDateTime) deve estar presente e ser privado");

        assertTrue(hasMethod(contaClass, "sacar", new Class[]{BigDecimal.class}), "Método sacar deve estar presente");
        assertTrue(hasMethod(contaClass, "depositar", new Class[]{BigDecimal.class}), "Método depositar deve estar presente");
        assertTrue(hasMethod(contaClass, "transferir", new Class[]{contaClass, BigDecimal.class}), "Método transferir deve estar presente");
        assertTrue(hasMethod(contaClass, "ativarConta", new Class[]{}), "Método ativarConta deve estar presente");
        assertTrue(hasMethod(contaClass, "desativarConta", new Class[]{}), "Método desativarConta deve estar presente");
        assertTrue(hasMethod(contaClass, "toString", new Class[]{}), "Método toString deve estar presente");
        assertTrue(hasMethod(contaClass, "equals", new Class[]{Object.class}), "Método equals deve estar presente");
        assertTrue(hasMethod(contaClass, "hashCode", new Class[]{}), "Método hashCode deve estar presente");
    }

    @Test
    public void testDepositar() throws Exception {
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method depositar = contaClass.getMethod("depositar", BigDecimal.class);
        depositar.invoke(conta, new BigDecimal("500.00"));

        Field saldoField = contaClass.getDeclaredField("saldo");
        saldoField.setAccessible(true);
        BigDecimal saldo = (BigDecimal) saldoField.get(conta);
        assertEquals(new BigDecimal("1500.00"), saldo, "O saldo deve aumentar após depósito");

        try {
            depositar.invoke(conta, new BigDecimal("-100.00"));
            fail("Depósito com valor negativo não deve ser permitido");
        } catch (Exception e) {
            // Esperado
        }
    }

    @Test
    public void testSacar() throws Exception {
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method sacar = contaClass.getMethod("sacar", BigDecimal.class);
        sacar.invoke(conta, new BigDecimal("300.00"));

        Field saldoField = contaClass.getDeclaredField("saldo");
        saldoField.setAccessible(true);
        BigDecimal saldo = (BigDecimal) saldoField.get(conta);
        assertEquals(new BigDecimal("700.00"), saldo, "O saldo deve diminuir após saque");

        try {
            sacar.invoke(conta, new BigDecimal("-100.00"));
            fail("Saque com valor negativo não deve ser permitido");
        } catch (Exception e) {
            // Esperado
        }

        try {
            sacar.invoke(conta, new BigDecimal("1000.00"));
            fail("Saque com saldo insuficiente não deve ser permitido");
        } catch (Exception e) {
            // Esperado
        }
    }

    @Test
    public void testTransferir() throws Exception {
        Object contaOrigem = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());
        Object contaDestino = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1002", new BigDecimal("0.00"), true, LocalDateTime.now());

        Method transferir = contaClass.getMethod("transferir", contaClass, BigDecimal.class);
        transferir.invoke(contaOrigem, contaDestino, new BigDecimal("200.00"));

        Field saldoField = contaClass.getDeclaredField("saldo");
        saldoField.setAccessible(true);
        BigDecimal saldoOrigem = (BigDecimal) saldoField.get(contaOrigem);
        BigDecimal saldoDestino = (BigDecimal) saldoField.get(contaDestino);
        assertEquals(new BigDecimal("800.00"), saldoOrigem, "O saldo da conta de origem deve diminuir após transferência");
        assertEquals(new BigDecimal("200.00"), saldoDestino, "O saldo da conta de destino deve aumentar após transferência");

        try {
            transferir.invoke(contaOrigem, contaDestino, new BigDecimal("-100.00"));
            fail("Transferência com valor negativo não deve ser permitida");
        } catch (Exception e) {
            // Esperado
        }

        try {
            transferir.invoke(contaOrigem, contaDestino, new BigDecimal("1000.00"));
            fail("Transferência com saldo insuficiente não deve ser permitida");
        } catch (Exception e) {
            // Esperado
        }
    }

    @Test
    public void testDesativarConta() throws Exception {
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), true, LocalDateTime.now());

        Method desativarConta = contaClass.getMethod("desativarConta");
        desativarConta.invoke(conta);

        Field statusField = contaClass.getDeclaredField("status");
        statusField.setAccessible(true);
        boolean status = (boolean) statusField.get(conta);
        assertFalse(status, "A conta deve estar desativada");

        Method depositar = contaClass.getMethod("depositar", BigDecimal.class);
        try {
            depositar.invoke(conta, new BigDecimal("100.00"));
            fail("Operação em conta desativada não deve ser permitida");
        } catch (Exception e) {
            // Esperado
        }
    }

    @Test
    public void testAtivarConta() throws Exception {
        Object conta = contaClass.getDeclaredConstructor(String.class, BigDecimal.class, boolean.class, LocalDateTime.class)
                .newInstance("1001", new BigDecimal("1000.00"), false, LocalDateTime.now());

        Method ativarConta = contaClass.getMethod("ativarConta");
        ativarConta.invoke(conta);

        Field statusField = contaClass.getDeclaredField("status");
        statusField.setAccessible(true);
        boolean status = (boolean) statusField.get(conta);
        assertTrue(status, "A conta deve estar ativada");

        Method depositar = contaClass.getMethod("depositar", BigDecimal.class);
        depositar.invoke(conta, new BigDecimal("100.00"));
        Field saldoField = contaClass.getDeclaredField("saldo");
        saldoField.setAccessible(true);
        BigDecimal saldo = (BigDecimal) saldoField.get(conta);
        assertEquals(new BigDecimal("1100.00"), saldo, "Depósito deve ser permitido após ativação");
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