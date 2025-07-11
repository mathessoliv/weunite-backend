# WeUnite - Backend

## Sobre o Projeto
WeUnite é uma rede social que conecta pessoas e oportunidades. Este é o backend da aplicação, desenvolvido em Java com Spring Boot.

## Tecnologias Utilizadas
- **Java 17+**
- **Spring Boot 3.x**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL** (Banco de dados)
- **Cloudinary** (Upload de imagens)
- **Maven** (Gerenciamento de dependências)

## Funcionalidades
- ✅ Autenticação de usuários (JWT)
- ✅ Cadastro e login de usuários
- ✅ Criação, edição e exclusão de posts
- ✅ Upload de imagens em posts
- ✅ Sistema de likes e comentários
- ✅ Verificação de email
- ✅ Reset de senha

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6+
- MySQL 8.0+
- Conta no Cloudinary (opcional para testes)

## Passos para execução

1. **Clone o repositório**
```bash
git clone https://github.com/mathessoliv/weunite-backend.git
cd weunite-backend
```

2. **Configure o banco de dados**
- Crie um banco PostgreSQL chamado `weunite`
- Configure as credenciais em `application.properties`

3. **Configure as variáveis de ambiente**
```properties
# application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/weunite
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha

# Cloudinary
cloudinary.url=cloudinary://api_key:api_secret@cloud_name
```

4. **Execute a aplicação**
```bash
# Com Maven
mvn spring-boot:run

# Ou via IDE
# Execute a classe WeUniteAuthApplication.java
```

5. **Acesse a API**
- Backend: `http://localhost:8080`
- Swagger (se habilitado): `http://localhost:8080/swagger-ui.html`

## Endpoints Principais

### Autenticação
- `POST /api/auth/register` - Cadastro de usuário
- `POST /api/auth/login` - Login
- `POST /api/auth/verify` - Verificação de email
- `POST /api/auth/forgot-password` - Esqueci a senha

### Posts
- `POST /api/posts/create/{userId}` - Criar post
- `GET /api/posts/get/{postId}` - Buscar post
- `PUT /api/posts/update/{userId}/{postId}` - Atualizar post
- `DELETE /api/posts/delete/{userId}/{postId}` - Deletar post

### Usuários
- `GET /api/users/{userId}` - Buscar usuário
- `PUT /api/users/{userId}` - Atualizar usuário

## Estrutura do Projeto
```
src/
├── main/
│   ├── java/
│   │   └── com/example/weuniteauth/
│   │       ├── config/          # Configurações
│   │       ├── controller/      # Controllers REST
│   │       ├── domain/          # Entidades JPA
│   │       ├── dto/             # Data Transfer Objects
│   │       ├── exceptions/      # Exceções customizadas
│   │       ├── mapper/          # Mapeadores DTO/Entity
│   │       ├── repository/      # Repositórios JPA
│   │       ├── service/         # Lógica de negócio
│   │       └── validations/     # Validações customizadas
│   └── resources/
│       ├── application.properties
│       └── templates/           # Templates de email
```

## Configuração para Desenvolvimento

### Banco de Dados
O projeto está configurado para usar PostgreSQL. Para desenvolvimento local:

```sql
CREATE DATABASE weunite;
```

## Contribuição
1. Faça fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-feature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/nova-feature`)
5. Abra um Pull Request
