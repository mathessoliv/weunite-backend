# 📚 Guia Completo de Sintaxe de Testes Unitários

## 🎯 Estrutura Básica de um Teste

```java
@ExtendWith(MockitoExtension.class)      // ← Habilita o Mockito
@DisplayName("ServiceName Tests")         // ← Nome legível do teste
class ServiceNameTest {

    @Mock                                 // ← Mock de dependências
    private DependencyRepository repository;
    
    @Mock
    private DependencyMapper mapper;
    
    @InjectMocks                          // ← Injeta os mocks no service
    private ServiceName service;
    
    @BeforeEach                           // ← Executa ANTES de cada teste
    void setUp() {
        // Inicializar objetos de teste aqui
    }
    
    @Test                                 // ← Marca um método como teste
    @DisplayName("Should do something when condition")
    void testMethodName() {
        // Arrange (preparar)
        // Act (executar)
        // Assert (verificar)
        // Verify (confirmar chamadas)
    }
}
```

---

## 🔧 1. Mockando Retornos de Métodos

### Retornar um valor específico
```java
when(repository.findById(1L)).thenReturn(Optional.of(user));
```

### Retornar null
```java
when(repository.findById(999L)).thenReturn(Optional.empty());
```

### Retornar para qualquer argumento
```java
when(repository.save(any(User.class))).thenReturn(user);
```

### Retornar valores diferentes em chamadas sucessivas
```java
when(repository.count())
    .thenReturn(10L)   // primeira chamada
    .thenReturn(20L)   // segunda chamada
    .thenReturn(30L);  // terceira chamada
```

### Para métodos void (que não retornam nada)
```java
doNothing().when(repository).delete(user);
```

### Lançar exceção
```java
when(repository.findById(999L))
    .thenThrow(new UserNotFoundException());
```

---

## 🎭 2. Matchers (Argumentos Flexíveis)

### Qualquer valor de um tipo
```java
any(User.class)           // qualquer User
any(Long.class)           // qualquer Long
any(String.class)         // qualquer String
any()                     // qualquer objeto
anyLong()                 // qualquer long
anyString()               // qualquer string
anyList()                 // qualquer lista
```

### Valor específico com matcher
```java
eq(1L)                    // exatamente 1L
eq("text")                // exatamente "text"
```

### Misturando valores específicos com matchers
```java
// ❌ ERRADO
when(service.method("value", any())).thenReturn(result);

// ✅ CORRETO
when(service.method(eq("value"), any())).thenReturn(result);
```

### Verificar se é null
```java
isNull()                  // deve ser null
isNotNull()               // não pode ser null
```

---

## ✅ 3. Assertions (Verificações)

### Verificar igualdade
```java
assertEquals(expected, actual);
assertEquals(100L, result.getTotalPosts());
assertEquals("message", result.message());
```

### Verificar não-null
```java
assertNotNull(result);
assertNotNull(result.getData());
```

### Verificar null
```java
assertNull(result);
```

### Verificar booleanos
```java
assertTrue(user.isActive());
assertFalse(user.isBanned());
```

### Verificar que lança exceção
```java
assertThrows(UserNotFoundException.class, () -> 
    service.getUser(999L)
);
```

### Verificar se contém texto
```java
assertTrue(result.message().contains("sucesso"));
```

### Verificar tamanho de lista
```java
assertEquals(5, list.size());
assertTrue(list.isEmpty());
assertFalse(list.isEmpty());
```

### Comparar números com margem de erro (para doubles)
```java
assertEquals(800.0, result.getEngagementRate(), 0.01);
//                                               ↑ margem de erro
```

---

## 🔍 4. Verify (Confirmar Chamadas)

### Verificar que método foi chamado
```java
verify(repository).save(user);
```

### Verificar com argumentos específicos
```java
verify(repository).findById(1L);
verify(repository).save(any(User.class));
```

### Verificar número de vezes
```java
verify(repository, times(1)).save(user);      // 1 vez
verify(repository, times(3)).findById(anyLong()); // 3 vezes
verify(repository, never()).delete(any());     // nunca
verify(repository, atLeast(1)).save(any());    // pelo menos 1
verify(repository, atMost(3)).save(any());     // no máximo 3
```

### Verificar que NUNCA foi chamado
```java
verify(repository, never()).delete(any(User.class));
```

### Verificar ordem de chamadas
```java
InOrder inOrder = inOrder(repository);
inOrder.verify(repository).findById(1L);
inOrder.verify(repository).save(user);
```

---

## 📝 5. Padrão AAA (Arrange, Act, Assert)

```java
@Test
void testExample() {
    // ARRANGE (preparar) - configurar mocks e dados
    User user = new User();
    user.setId(1L);
    user.setName("Test");
    
    when(repository.findById(1L)).thenReturn(Optional.of(user));
    when(repository.save(any(User.class))).thenReturn(user);
    
    // ACT (executar) - chamar o método que está sendo testado
    ResponseDTO<UserDTO> result = service.updateUser(1L, updateRequest);
    
    // ASSERT (verificar) - verificar resultados
    assertNotNull(result);
    assertEquals("Usuário atualizado", result.message());
    
    // VERIFY (confirmar) - confirmar chamadas aos mocks
    verify(repository).findById(1L);
    verify(repository).save(user);
}
```

---

## 🎯 6. Testando Diferentes Cenários

### Cenário de Sucesso
```java
@Test
@DisplayName("Should create user successfully")
void createUserSuccess() {
    when(repository.existsByEmail("test@test.com")).thenReturn(false);
    when(repository.save(any(User.class))).thenReturn(user);
    
    User result = service.createUser(request);
    
    assertNotNull(result);
    verify(repository).save(any(User.class));
}
```

### Cenário de Erro
```java
@Test
@DisplayName("Should throw exception when user already exists")
void createUserAlreadyExists() {
    when(repository.existsByEmail("test@test.com")).thenReturn(true);
    
    assertThrows(UserAlreadyExistsException.class, () -> 
        service.createUser(request)
    );
    
    verify(repository, never()).save(any(User.class));
}
```

### Cenário de Validação
```java
@Test
@DisplayName("Should handle empty list")
void handleEmptyList() {
    when(repository.findAll()).thenReturn(Arrays.asList());
    
    List<User> result = service.getAllUsers();
    
    assertNotNull(result);
    assertTrue(result.isEmpty());
}
```

---

## 🧪 7. Testando Valores Calculados

```java
@Test
@DisplayName("Should calculate engagement rate correctly")
void calculateEngagementRate() {
    when(postRepository.count()).thenReturn(100L);
    when(postRepository.countTotalLikes()).thenReturn(500L);
    when(postRepository.countTotalComments()).thenReturn(300L);
    
    AdminStatsDTO result = service.getAdminStats();
    
    // Engagement = (500 + 300) / 100 * 100 = 800%
    assertEquals(800.0, result.engagementRate(), 0.01);
}
```

---

## 📊 8. Testando Listas e Coleções

```java
@Test
@DisplayName("Should return list with 3 items")
void returnListWithThreeItems() {
    List<User> users = Arrays.asList(user1, user2, user3);
    
    when(repository.findAll()).thenReturn(users);
    
    List<UserDTO> result = service.getAllUsers();
    
    assertNotNull(result);
    assertEquals(3, result.size());
    
    // Verificar cada item
    result.forEach(user -> {
        assertNotNull(user.getId());
        assertNotNull(user.getUsername());
    });
}
```

---

## ⏰ 9. Testando Datas e Timestamps

```java
@Test
@DisplayName("Should set createdAt timestamp")
void setCreatedAtTimestamp() {
    Instant before = Instant.now();
    
    User result = service.createUser(request);
    
    Instant after = Instant.now();
    
    assertNotNull(result.getCreatedAt());
    assertTrue(result.getCreatedAt().isAfter(before));
    assertTrue(result.getCreatedAt().isBefore(after));
}
```

---

## 🎨 10. Dicas de Boas Práticas

### ✅ DO (Faça)
```java
// Nome descritivo
@DisplayName("Should create user when email is unique")

// Um conceito por teste
@Test
void createUserSuccess() {
    // Testa apenas criação
}

// Mock apenas o necessário
when(repository.save(any(User.class))).thenReturn(user);
```

### ❌ DON'T (Não faça)
```java
// Nome ruim
@Test
void test1() { }

// Testar múltiplos conceitos
@Test
void testEverything() {
    // Testa create, update, delete tudo junto
}

// Mockar demais
when(repository.findById(anyLong())).thenReturn(...);
when(repository.findByEmail(anyString())).thenReturn(...);
// ... 20 mocks ...
```

---

## 🚀 11. Template Completo para Copiar

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("MyService Tests")
class MyServiceTest {

    @Mock
    private MyRepository myRepository;
    
    @Mock
    private MyMapper myMapper;
    
    @InjectMocks
    private MyService myService;
    
    private MyEntity testEntity;
    private MyDTO testDTO;
    
    @BeforeEach
    void setUp() {
        testEntity = new MyEntity();
        testEntity.setId(1L);
        testEntity.setName("Test");
        
        testDTO = new MyDTO("1", "Test");
    }
    
    @Test
    @DisplayName("Should do something successfully")
    void doSomethingSuccess() {
        // Arrange
        when(myRepository.findById(1L)).thenReturn(Optional.of(testEntity));
        when(myMapper.toDTO(any(MyEntity.class))).thenReturn(testDTO);
        
        // Act
        MyDTO result = myService.doSomething(1L);
        
        // Assert
        assertNotNull(result);
        assertEquals("Test", result.getName());
        
        // Verify
        verify(myRepository).findById(1L);
        verify(myMapper).toDTO(testEntity);
    }
    
    @Test
    @DisplayName("Should throw exception when not found")
    void doSomethingNotFound() {
        // Arrange
        when(myRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(NotFoundException.class, () -> 
            myService.doSomething(999L)
        );
        
        // Verify
        verify(myRepository).findById(999L);
        verify(myRepository, never()).save(any());
    }
}
```

---

## 💡 Resumo Rápido

| O que fazer | Sintaxe |
|-------------|---------|
| Mockar retorno | `when(method()).thenReturn(value)` |
| Qualquer argumento | `any(Type.class)` ou `anyLong()` |
| Verificar igualdade | `assertEquals(expected, actual)` |
| Verificar não-null | `assertNotNull(value)` |
| Verificar exceção | `assertThrows(Exception.class, () -> method())` |
| Confirmar chamada | `verify(mock).method(args)` |
| Confirmar nunca chamado | `verify(mock, never()).method()` |
| Método void | `doNothing().when(mock).voidMethod()` |

---

Use este guia como referência sempre que precisar criar testes! 🚀

