# WoW Character Interaction

API REST em Java e Spring Boot para consultar personagens de World of Warcraft e conversar com eles. O PostgreSQL armazena personagens e contas, o Flyway aplica as migrações e o Gemini gera respostas com base nos dados e na história do personagem.

## Execução local

É preciso ter JDK 17, PostgreSQL com um banco já criado e uma chave da API Gemini. O Maven Wrapper baixa a versão do Maven usada pelo projeto.

Forneça estas variáveis ao processo antes de iniciar a aplicação.

| Variável | Uso |
| --- | --- |
| `PGHOST` | Host do PostgreSQL |
| `PGPORT` | Porta do PostgreSQL. Padrão `5432` |
| `PGDATABASE` | Nome do banco |
| `PGUSER` | Usuário do banco, com permissão para executar as migrações |
| `PGPASSWORD` | Senha do banco |
| `GEMINI_API_KEY` | Chave para chamadas ao Gemini |
| `GEMINI_MODEL` | Identificador de um modelo com suporte a `generateContent`. Padrão `gemini-3.5-flash` |
| `JWT_SECRET_KEY` | Segredo de assinatura dos tokens |
| `PASS_CRYPT_SECRET` | Segredo usado na derivação dos hashes das senhas |

Forneça os segredos pelo ambiente da aplicação ou pelo gerenciador de segredos do deploy. Preserve o `PASS_CRYPT_SECRET` de uma instalação existente para que as senhas já gravadas continuem válidas. O perfil `prod` contém referências a variáveis de ambiente e a configuração local com valores reais deve ficar fora do Git.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

No Windows, use `mvnw.cmd` no lugar de `./mvnw`. A aplicação inicia na porta `8080` por padrão e aplica as migrações automaticamente. A interface Swagger fica em [localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) e a especificação OpenAPI em [localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

## Endpoints

O prefixo é `/api/v1`. As rotas com corpo aceitam JSON, XML e YAML em `Content-Type` e `Accept`, usando `application/json`, `application/xml` e `application/x-yaml`.

| Método | Rota | Autorização |
| --- | --- | --- |
| POST | `/users/create` | Pública |
| POST | `/auth/signin` | Pública |
| PUT | `/auth/refresh/{username}` | Token de renovação da conta indicada |
| PATCH | `/users/update/{username}/full-name` | Token de acesso do titular |
| PATCH | `/users/update/{username}/password` | Token de acesso do titular |
| GET | `/character/find-all` | Token de acesso |
| GET | `/character/find/{id}` | Token de acesso |
| POST | `/character/ask/{id}` | Token de acesso |
| POST | `/character/create` | Token de acesso com `ADMIN` ou `MANAGER` |
| PUT | `/character/update/{id}` | Token de acesso com `ADMIN` ou `MANAGER` |
| DELETE | `/character/delete/{id}` | Token de acesso com `ADMIN` ou `MANAGER` |

O cadastro recebe `user_name`, `full_name` e `password`. O login recebe `username` e `password` e devolve `accessToken` e `refreshToken`. Envie o token da rota no cabeçalho `Authorization: Bearer <token>`. Nas rotas de usuário e renovação, use o nome da conta no segmento `{username}`. Os dois endpoints de alteração de conta recebem o novo valor como texto no corpo da requisição, sem objeto envolvente.

```bash
API=http://localhost:8080/api/v1

curl -sS "$API/users/create" \
  -H 'Content-Type: application/json' \
  -d '{"user_name":"explorador","full_name":"Explorador de Azeroth","password":"senha-de-exemplo"}'

curl -sS "$API/auth/signin" \
  -H 'Content-Type: application/json' \
  -d '{"username":"explorador","password":"senha-de-exemplo"}'
```

Copie o `accessToken` para a variável `ACCESS_TOKEN`. Consulte os personagens para escolher um ID e envie a pergunta no campo `question`. O corpo da resposta traz o campo `answer`.

```bash
curl -sS "$API/character/find-all" \
  -H "Authorization: Bearer $ACCESS_TOKEN"

curl -sS "$API/character/ask/1" \
  -H "Authorization: Bearer $ACCESS_TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"question":"O que você sabe sobre Azeroth?"}'
```

Nas rotas de autenticação e conversa, `400` indica entrada inválida, `401` indica credenciais ou token inválidos, `403` indica falta de permissão, `404` indica recurso inexistente e `502` indica falha ou ausência de resposta do Gemini. A exclusão de um personagem retorna `204`.

## Testes

Com o Docker ativo, execute a suíte completa. O Testcontainers inicia PostgreSQL 16.3, e a suíte substitui as chamadas ao Gemini por respostas controladas.

```bash
./mvnw clean test
```

Para rodar a mesma suíte com um PostgreSQL local, crie um banco exclusivo para testes e forneça `TEST_POSTGRES_URL` como URL JDBC, `TEST_POSTGRES_USER` e, se houver senha, `TEST_POSTGRES_PASSWORD`. As migrações de teste criam uma conta de fixture nesse banco. O comando abaixo executa apenas testes unitários e dispensa PostgreSQL e Docker.

```bash
./mvnw -Dtest=MapperTests,WowCharacterServiceImplTest,AskCharacterUseCaseTest,GeminiChatApiTest,JwtTokenProviderTest test
```
