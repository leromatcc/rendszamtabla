# Camera
Esse projeto realiza o acesso a uma Camera IP e grava a imagem na pasta "capturas"
A pasta "capturas" deve ser criada previamente.

# Funcionamento 
A classe App.java contém a inicialização de um timer e realiza a captura de imagem a cada 5 segundos
A classe Camera.java contém as definições de acesso, é necessário informar o host, usuário e senha para acessar a camera
A classe AcessarCameraTemporizado.java implementa uma thread com um temporizador para disparar acessos a camera

# Como testar
A edição da classe App.java, informando os dados de acesso a camera permite utilizar com qualquer camera ip que disponibilize um URL de acesso a imagens e utilize HTTP Basic authentication

# Leitura de uma URL e salvar em arquivo
# Objetivo
Acessar uma camera IP, obter a imagem de um quadro e salvar o arquivo da imagem.

#Arquitetura
Camera IP conectada a um Roteador
Programa roda em um computador com acesso ao Roteador
Camera IP pede autenticação basica HTTP

#Solução Desenvolvida
Programa ConnectToUrlUsingBasicAuthentication.java 
- acessa a url da camera ip
- autentica na camera ip
- recebe/le a stream de dados disponibilizada pela camera
- grava em um arquivo jpg essa stream de dados
