package sistema.bancario.prova.test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    ClienteTest.class,
    ContaTest.class,
    PersistenciaEmArquivoTest.class
})
public class SistemaBancarioTestSuite {
}
