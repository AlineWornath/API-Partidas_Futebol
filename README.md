# ⚽ API de Partidas de Futebol

Esta aplicação permite gerenciar clubes, estádios e partidas de futebol, incluindo estatísticas e buscas avançadas.

## Funcionalidades Principais

- **Clubes:**  
  - Cadastro (`POST /clubs`)
  - Edição (`PUT /clubs/{id}`)
  - Inativação (DELETE lógico) (`DELETE /clubs/{id}`)
  - Busca por ID e listagem com filtros por nome, estado (sigla UF), e status ativo/inativo
  - Paginação e ordenação

- **Estádios:**  
  - Cadastro e edição
  - Busca por ID e listagem com paginação/ordenação

- **Partidas:**  
  - Cadastro (`POST /matches`) entre clubes existentes, informando estádio e data/hora
  - Edição, exclusão e busca por ID
  - Listagem de partidas com filtros por clube, estádio, mandante/visitante, goleadas (diferença ≥ 3 gols), com paginação/ordenação
  
- **Buscas avançadas:**  
  - Retrospecto geral do clube
  - Retrospecto do clube contra adversários
  - Confrontos diretos (head-to-head)
  - Rankings: jogos, vitórias, gols e pontos

## Tecnologias Usadas

- Java 21
- Spring Boot
- Spring Data JPA
- Swagger/OpenAPI (documentação)
- JUnit/Mockito (testes)
- Banco relacional (MySQL, H2 suportado em dev)
- Maven
---

## Instalação & Execução

```bash
git clone https://github.com/AlineWornath/API-Partidas_Futebol.git
cd API-Partidas_Futebol
```

**Configure as credenciais do banco:**

Crie o arquivo src/main/resources/env.properties (NÃO versionado no Git) com:

```
db.username=SEU_USUARIO_DO_BANCO
db.password=SUA_SENHA_DO_BANCO
```

O arquivo application.properties já está preparado para ler estas variáveis.

**Configure seu banco MySQL:**

**Host:** localhost

**Porta:** 3306

**Database:** soccer_matches

**User/password:** conforme acima


**Rode o projeto:**

```
./mvnw spring-boot:run
```
 ou
```
./gradlew bootRun
```

## Acesse a documentação:

http://localhost:8080/swagger-ui.html



## Exemplo de Cadastro de Partida

O campo de data/hora segue o padrão ISO: yyyy-MM-dd'T'HH:mm

<pre><code>
{
  "homeClubId": 1,
  "awayClubId": 3,
  "homeGoals": 2,
  "awayGoals": 1,
  "stadiumId": 1,
  "matchDatetime": "2023-06-15T20:30"
}
</code></pre>

## Filtros Avançados — Exemplos de Endpoint

**Listar clubes ativos do RJ:**
GET /clubs?stateCode=RJ&active=true&sort=name,asc

**Filtrar partidas consideradas goleada:**
GET /matches?filter=ROUT

**Buscar head-to-head entre dois clubes:**
GET /clubs/1/head-to-head/3

**Ranking por pontos:**
GET /clubs/ranking?rankingOrderEnum=POINTS

## Testes

Execute todos os testes automatizados:
```
./mvnw test
```

**Observações**

Utilize os exemplos e coleções no Swagger UI para testar rapidamente a API.

Feito por Aline Cristina Wornath.
