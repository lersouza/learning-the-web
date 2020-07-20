# Warmup

> Em computação, aplicação web designa, de forma geral, sistemas de informática projetados para utilização através de um navegador, através da internet ou aplicativos desenvolvidos utilizando tecnologias web HTML, JavaScript e CSS. Pode ser executado a partir de um servidor HTTP (Web Host) ou localmente, no dispositivo do usuário. *(by Wikipedia)*

Em geral, aplicações web são software distribuídos (executados em diferentes máquinas) que usam o protocolo HTTP (ou similares) para trocar mensagens.

## Por que o protocolo?

Respondendo com muita simplicidade (e humildade), porque deseja-se estabelecer uma linguagem comum entre as partes (lembre-se, estamos falando de um sistema distribuído) para que eles possam fazer requisições e atender à estas requisições, explorando características conhecidas para todas as partes.

Em geral, os protocolos e padrões da web são expressos por meio de RFC (*Request for comments*) e coordenatos pelo IETF (*Internet Engineering Task Force*). Algumas RFCs importantes, relacionadas à Web:

- [HTTP, versão 1.1](https://tools.ietf.org/html/rfc2616): a versão mais utilizada, atualmente, do protocolo HTTP

- [HTTP, versão 2](https://tools.ietf.org/html/rfc2616): versão mais nova do protocolo

- [HTML](https://tools.ietf.org/html/rfc1866): A linguagem de marcação usada, comumente, em páginas da Web

- [JSON](https://www.rfc-editor.org/rfc/rfc8259.txt): Linguagem para representação e intercâmbio de dados

Também existem consórcios de empresas, como o [W3C](https://www.w3.org/) (*World Wide Web Consortium*), que participam ativamente no desenvolvimento de tecnologias e padrões.

## Cliente Servidor

A Web, em geral (não a Internet), trabalha numa arquitetura cliente-servidor. Existem aplicações ativas, aguardando por uma solicitação e aplicações que realizam solicitações em nome de alguma pessoa ou interesse. No caso do HTTP, toda interação é realizada com base no formato Requisição e Resposta.

O HTTP, em particular, é um protocolo que usa o protocolo TCP como meio conexão. As aplicações clientes estabelecem uma conexão TCP com a aplicação servidora e enviam mensagens seguindo a convenção estabelecida pelo HTTP.

Vamos brincar um pouco com algumas ferramentas para ver como um browser realiza opera quando digitamos, por exemplo, http://www.example.org:

### Resolução de DNS e Porta

A primeira nota importante é que, para nos comunicarmos com outros computador, precisamos de seu endereço IP e uma porta que representa a aplicação remota que desejo acessar. Quando um usuário digita, no browser, http://www.example.org/, duas coisas importantes ocorrem:

*Com qual computador a conexão TCP deve ser estabelecida?*

O Browser precisa conhecer qual o endereço IP associado ao nome "amigável" www.example.org. Para isso, ele realiza um *query* DNS (resolução de nomes). Podemos simular essa *query* através do utilitário `nslookup`:

```
nslookup www.example.org
```

*Em qual porta lógica?*

Todo o paradigma de conexão envolve o endereço e a porta lógica. Isto permite que vários programas no mesmo computador identificado por um endereço IP possam atender a solicitações de clientes: um na porta 80, outro na 443, outro na 8080 e assim por diante.

Por convenção, o início da URL indica o protocolo que se deseja usar para a comunicação (no caso, *http*). Também por convenção, quando a porta não é especificada, como em http://www.example.org:80, utiliza-se a porta 80 para o esquema http, 443 para o esquema https, 22 para o esquema ssh, etc.

### Estabelecendo a coenxão TCP

Uma vez identificado o verdadeiro endereço do servidor e a porta na qual devemos nos conectar, o browser (ou aplicativo cliente, em geral), deve estabelecer uma conexão TCP com este servidor. Os sistemas operacionas fornecem uma implementação deste protocolo, portanto, tudo que o browser deve fazer é usar a API disponível para tal. Podemos realizar uma conexão diretamente através do aplicativo **telnet**, especificando o endereço IP e porta obtidos anteriormente:

```
telnet 93.184.216.34 80
```

Ao realizar este comando, o seguinte é exibido no shell:

```
Trying 93.184.216.34...
Connected to 93.184.216.34.
Escape character is '^]'.
```

Isto indica que a conexão foi estabelecida com sucesso. Tudo o que for digitado na sequência será enviado, via rede, para o servidor.

### Comunicação HTTP

Agora, com uma conexão estabelecida, o broswer pode começar a enviar mensagens, solicitando recursos para o servidor. Para HTTP, a estrutura básica da mensagem é a seguinte:

```
METHOD PATH PROTOCOL_VERSION
HEADER_KEY: HEADER_VALUE
HEADER_KEY: HEADER_VALUE

Body Content (Se o cabeçalho Content-Length for especificado)

```
Note que a mensagem se encerra com uma linha em branco. Um exemplo:

```http
GET / HTTP/1.1
Host: www.example.org

```

Se usarmos este exemplo, na conexão aberta, teremos uma resposta do servidor:

```http
HTTP/1.1 200 OK
Age: 359205
Cache-Control: max-age=604800
Content-Type: text/html; charset=UTF-8
Date: Mon, 20 Jul 2020 18:54:21 GMT
Etag: "3147526947+ident"
Expires: Mon, 27 Jul 2020 18:54:21 GMT
Last-Modified: Thu, 17 Oct 2019 07:18:26 GMT
Server: ECS (mic/9A9E)
Vary: Accept-Encoding
X-Cache: HIT
Content-Length: 1256

<!doctype html>
<html>
<head>
    <title>Example Domain</title>

    <meta charset="utf-8" />
    <meta http-equiv="Content-type" content="text/html; charset=utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <style type="text/css">

....
```

A resposta tem o format:

```http
PROTOCOL_VERSION STATUS_CODE STATUS_MESSAGE
Header1: Value1
Header2: Value2

Body Content
```

Na RFC do HTTP, cada código de resposta tem um propósito bem definido. o Range 200 - 299 representa resposta do tipo Sucesso.

### Carregando o HTML

Uma vez que o servidor responda o HTML (no nosso caso), o browser processa seu conteúdo, criando elementos visuais relacionados às *tags*.

## Utilizando Sniffers

Existem algumas aplicações que funcionam como "espiões" do tráfego entre clientes e servidor. Um exemplo é o [Wireshark](https://www.wireshark.org/). Ele observa as mensagens que estão sendo enviadas e recebidas pela placa de rede. Deste modo, caso a requisição seja aberta, pode-se inspecionar o conteúdo das mensagens HTTP deste modo.