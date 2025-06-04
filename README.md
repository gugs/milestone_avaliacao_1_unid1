
# Atividade de Avaliação da Disciplina Programação Orientada a Objetos: Sistema Bancário Simplificado

## Objetivo

Desenvolver um sistema bancário simplificado em Java, implementando as classes `Cliente`, `Conta`, e `PersistenciaEmArquivo` no pacote `sistema.bancario`. As classes devem seguir os conceitos de programação orientada a objetos abordados na unidade, incluindo encapsulamento, associação entre classes, e persistência de dados. A atividade será avaliada por meio de testes automatizados fornecidos (usando JUnit 5), que verificarão tanto a estrutura quanto o comportamento funcional das classes implementadas.

## Contexto

O sistema deve gerenciar clientes e suas contas bancárias, permitindo operações como depósito, saque, transferência, ativação/desativação de contas, e gerenciamento de clientes com persistência em arquivo. As classes devem ser implementadas no pacote `sistema.bancario`, e os testes JUnit fornecidos, no pacote `sistema.bancario.test`, (`ClienteTest.java`, `ContaTest.java`, `PersistenciaEmArquivoTest.java`, e `SistemaBancarioTestSuite.java`) serão usados para validar as implementações.

## Requisitos Gerais

- **Pacote**: Todas as classes devem ser implementadas no pacote `sistema.bancario.model`.
- **Serializable**: As classes `Cliente` e `Conta` devem implementar a interface `java.io.Serializable`.
- **Encapsulamento**: Todos os atributos devem ser privados, com *getters* e *setters* apropriados.
- **Tipos Específicos**:
  - Use `java.math.BigDecimal` para valores monetários (como saldo).
  - Use `java.time.LocalDateTime` para datas (como data de abertura da conta).
  - Use `java.util.ArrayList` para coleções (como a lista de contas de um cliente).
- **Validações**: Implemente as validações especificadas para cada método, lançando exceções quando necessário (por exemplo, `IllegalStateException` ou `IllegalArgumentException`).
- **Testes**: Os testes JUnit fornecidos no pacote `banco.test` verificarão a estrutura (atributos e métodos) e o comportamento funcional. Execute a suíte `SistemaBancarioTestSuite.java` para validar sua implementação.

## Especificações das Classes

### 1. Classe `Cliente`

A classe `Cliente` representa um cliente do banco, que pode possuir várias contas.

#### Atributos

- `cpf`: `String`, privado, representa o CPF do cliente.
- `nome`: `String`, privado, representa o nome do cliente.
- `contas`: `ArrayList<Conta>`, privado, representa a lista de contas do cliente.

#### Construtores

- **Construtor padrão**: Inicializa o `ArrayList` de contas como vazio.
- **Construtor com argumentos**: Recebe `cpf` e `nome`, inicializando o `ArrayList` de contas como vazio.

#### Métodos

- **Getters e Setters**: Para `cpf`, `nome`, e `contas`, todos devem estar privados. O *getter* de `contas` deve retornar uma cópia do `ArrayList` para proteger o encapsulamento.
- `adicionarConta(Conta conta)`: Adiciona uma conta ao `ArrayList` se ela não existir (verifique pela existência do objeto). Não permita adicionar `null`.
- `removerConta(String numero)`: Remove a conta, se ela existir.
- `localizarContaPorNumero(String numeroConta)`: Retorna a conta com o número especificado e demais atributos ou `null` se não encontrada.
- `atualizarConta(Conta novaConta)`: Atualiza a conta existente com o mesmo número da `novaConta`, substituindo-a no `ArrayList`.
- `toString()`: Retorna uma representação em texto do cliente (ex.: `Cliente [cpf=12345678901, nome=João, contas=2]`).
- `equals(Object o)`: Compara dois clientes pelo `cpf`. Dois clientes com o mesmo CPF são considerados iguais.
- `hashCode()`: Gera um código hash baseado no `cpf`.

#### Observações

- O `ArrayList` de contas deve ser encapsulado, evitando modificações diretas.
- Implemente a interface `Serializable` com um `serialVersionUID` (ex.: `private static final long serialVersionUID = 1L;`).

### 2. Classe `Conta`

A classe `Conta` representa uma conta bancária associada a um cliente.

#### Atributos

- `numeroConta`: `String`, privado, representa o número único da conta.
- `saldo`: `BigDecimal`, privado, representa o saldo da conta.
- `status`: `boolean`, privado, indica se a conta está ativa (`true`) ou desativada (`false`).
- `dataAbertura`: `LocalDateTime`, privado, representa a data de abertura da conta.

#### Construtores

- **Construtor padrão**: Inicializa o saldo como `BigDecimal.ZERO`, o status como `true`, e a data de abertura como a data atual (`LocalDateTime.now()`).
- **Construtor com argumentos**: Recebe numeroConta, saldo, status, e dataAbertura. Se saldo ou dataAbertura forem null, use BigDecimal.ZERO e LocalDateTime.now(), respectivamente.

#### Métodos

- **Getters e Setters**: Para todos os atributos.
- `depositar(BigDecimal quantia)`: Adiciona a quantia ao saldo, se:
  - A conta estiver ativa (`status` é `true`).
  - A quantia for maior que zero.
  - Caso contrário, lance `IllegalStateException` (conta desativada) ou `IllegalArgumentException` (quantia inválida).
- `sacar(BigDecimal quantia)`: Subtrai a quantia do saldo, se:
  - A conta estiver ativa.
  - A quantia for maior que zero.
  - O saldo for suficiente.
  - Caso contrário, lance `IllegalStateException` (conta desativada ou saldo insuficiente) ou `IllegalArgumentException` (quantia inválida).
- `transferir(Conta destino, BigDecimal quantia)`: Transfere a quantia para a conta destino, se:
  - Ambas as contas (origem e destino) estiverem ativas.
  - A quantia for maior que zero.
  - O saldo da conta de origem for suficiente.
  - Caso contrário, lance `IllegalStateException` ou `IllegalArgumentException`.
- `ativarConta()`: Define o status como `true`.
- `desativarConta()`: Define o status como `false`.
- `toString()`: Retorna uma representação em texto da conta (ex.: `Conta [numeroConta=1001, saldo=1000.00, status=true, dataAbertura=2025-05-26T10:00:00]`).
- `equals(Object o)`: Compara duas contas pelo `numeroConta`. Contas com o mesmo número são consideradas iguais.
- `hashCode()`: Gera um código hash baseado no `numeroConta`.

#### Observações

- Implemente a interface `Serializable` com um `serialVersionUID`.
- Todas as operações financeiras (`depositar`, `sacar`, `transferir`) devem respeitar as validações especificadas.

### 3. Classe `PersistenciaEmArquivo`

A classe `PersistenciaEmArquivo` gerencia a persistência de clientes em um arquivo chamado `dados`, o qual deverá ser criado na raiz do projeto.

#### Atributos

- `clientes`: `ArrayList<Cliente>`, privado, armazena a lista de clientes.
- `ARQUIVO`: `String`, constante estática com o valor `"dados"`, representa o nome do arquivo de persistência.

#### Construtores

- **Construtor padrão**: Inicializa o `ArrayList` de clientes como vazio.

#### Métodos

- `adicionarCliente(Cliente cliente)`: Adiciona um cliente ao `ArrayList` se ele não existir (verifique pelo CPF). Não permita adicionar `null`.
- `removerCliente(String cpf)`: Remove o cliente com o CPF especificado, se ele existir.
- `localizarClientePorCpf(String cpf)`: Retorna o cliente com o CPF especificado ou `null` se não encontrado.
- `atualizarCliente(Cliente novoCliente)`: Atualiza o cliente existente com o mesmo CPF, substituindo-o no `ArrayList`.
- `salvarEmArquivo()`: Salva o `ArrayList` de clientes no arquivo `dados` usando `ObjectOutputStream`. Trate possíveis exceções de I/O.
- `carregarDeArquivo()`: Carrega o `ArrayList` de clientes do arquivo `dados` usando `ObjectInputStream`. Se o arquivo não existir ou houver erro, inicialize o `ArrayList` como vazio.

#### Observações

- Use `ObjectOutputStream` e `ObjectInputStream` para serialização.
- Trate exceções de I/O e `ClassNotFoundException` adequadamente.

## Instruções para Implementação

1. **Estrutura do Projeto**:

   - Importe o projeto Java no Eclipse chamado `milestone_avaliacao_1_und1`.
   - Crie o pacote `sistema.bancario.prova.model` e implemente as classes `Cliente.java` e `Conta.java`.
   - Crie o pacote `sistema.bancario.prova.persistencia` e implemente a classe `PersistenciaEmArquivo.java` dentro dele.
   - O pacote `sistema.bancario.prova.test` já contém os testes JUnit (`ClienteTest.java`, `ContaTest.java`, `PersistenciaEmArquivoTest.java`, e `SistemaBancarioTestSuite.java`), que você deve usar para validar sua implementação.

2. **Configuração do Ambiente**:

   - Use Java 8 ou superior.
   - Adicione a biblioteca JUnit 5 ao projeto:
     - No Eclipse, clique com o botão direito no projeto, selecione `Build Path > Add Libraries > JUnit > JUnit 5`.
   - Certifique-se de que não há erros de compilação antes de executar os testes.

3. **Execução dos Testes**:

   - Execute a suíte de testes clicando com o botão direito em `SistemaBancarioTestSuite.java` e selecionando `Run As > JUnit Test`.
   - Os testes verificarão:
     - A estrutura das classes (atributos, métodos, modificadores, e implementação de `Serializable`).
     - O comportamento funcional dos métodos, incluindo validações (ex.: conta ativa, quantia positiva, saldo suficiente).
     - A persistência correta no arquivo `dados`.
   - Analise os resultados dos testes para corrigir eventuais erros na implementação.

4. **Dicas**:

   - Implemente os métodos `equals` e `hashCode` de forma consistente, usando `Objects.equals` e `Objects.hash` para simplificar.
   - Use `BigDecimal` para cálculos financeiros, evitando tipos como `double` devido a imprecisões.
   - Teste incrementalmente, implementando e validando uma classe de cada vez com os testes correspondentes.
   - Certifique-se de que o `ArrayList` de contas em `Cliente` e de clientes em `PersistenciaEmArquivo` seja protegido contra modificações externas.

## Critérios de Avaliação

- **Correção**: Os testes JUnit devem passar sem erros, indicando que a estrutura e o comportamento das classes estão corretos.
- **Encapsulamento**: Atributos devem ser privados, com acesso controlado por *getters*/*setters*.
- **Validações**: Os métodos devem implementar todas as validações especificadas, lançando exceções apropriadas.
- **Persistência**: O arquivo `dados` deve ser criado e lido corretamente, mantendo a integridade dos dados.
- **Boas Práticas**: Código limpo, com nomes claros, comentários quando necessário, e tratamento adequado de exceções.
- **Nota da Avaliação:** A nota da avaliação dar-se-á por meio do computo percentual da atividade. 

## Entrega

- A entrega deverá ocorrer ao término da avaliação em sala de aula. 

**Boa sorte!!**
