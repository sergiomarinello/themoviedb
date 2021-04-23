## Desafio
O objetivo é construir a primeira versão de um app que lista os filmes mais populares
do momento.

### Funcionalidades
* Listar os filmes mais populares do momento (com paginação);
* Pesquisar por um filme digitando seu nome em um campo de pesquisa;
* Permitir a navegação para os detalhes de cada filme listado;
* Listar os filmes marcados como favoritos pelo usuário, um filme pode ser
marcado como favorito tanto na listagem quanto nos detalhes;
* Persistir no dispositivo os filmes favoritos para que possam ser acessados em
modo offline;

### API
Para desenvolver o seu app de filmes, você vai precisar se cadastrar no TheMoviesDB 
(https://www.themoviedb.org) e obter uma chave de acesso à API.

### Interface
#### Lista de Filmes
* Listagem dos filmes (poster do filme, o título e quais informações ache necessário);
* Botão para marcar como favorito nas células;
* Barra de pesquisa para buscar um filme pelo nome;
* Interface de lista vazia em caso de erro ou sem internet;
#### Detalhes do Filme
* Botão de favorito;
* Poster do filme em tamanho maior;
* Descrição (se houver);
* Orçamento e bilheteria;
* Qualquer outra informação que você ache legal :)
#### Favoritos
* Listagem dos filmes marcados como favoritos pelo usuário;
* Interface de lista vazia, caso não haja um filme favorito;

## Architecture decisions and libraries used.
1. The app was developed using MVVM architecture in mind, which is widely used for its decoupled properties and easy maintenance/scalability.
2. Dependency injection with Koin was used because is very lightweight and has a quick setup.
3. Remote data were fetched with the use of Retrofit as it provides a quick way to do so and it's widely used today for REST API access.
4. Pagination is supported by TMDB and that helps with load speed.
5. Local data were fetched with the use of Room that abstracts the access to SQL database and can be easily combine with Model structure.
6. Roboeletric used to abstract the android components such as context, allowing to testing the local access.
7. Glide was used to load the images from the TMDB server.
8. Mockito, Google's Truth and Mockito-Kotlin was used to create unit tests.
9. Enabled R8 that provides encryption, and store the TMDB key value in build/gradle to secure the value inside it.
10. detekt and ktlint for static code analysis that brings quality for the code.
11. Espresso to do automated testing UI that brings more reliability to the code.

## Observation
* Needs a valid key from the TMDB and MUST BE replace the field API_KEY_VALUE by its string value in build/gradle
