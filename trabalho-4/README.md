# ShellLinux

Um interpretador de comandos (shell) implementado em Java, com suporte a comandos internos, execução de comandos externos, redirecionamento, pipes, execução em background e logging.

## Funcionalidades Implementadas

### Comandos internos
- `cd <diretório>` — Muda o diretório atual
- `pwd` — Exibe o diretório atual
- `echo <texto>` — Imprime texto na tela
- `cp <origem> <destino>` — Copia arquivos
- `mv <origem> <destino>` — Move arquivos
- `rm <arquivo>` — Remove arquivos
- `mkdir <nome>` — Cria diretório
- `rmdir <nome>` — Remove diretório vazio
- `ls` — Lista arquivos no diretório atual
- `exit` — Sai do shell

### Comandos externos
Executa qualquer comando válido do sistema Linux, por exemplo:
```bash
shell> date
shell> whoami
shell> ping google.com
```

### Redirecionamento de entrada e saída
- `>` redireciona a saída (sobrescreve):
  ```bash
  myshell> echo Olá > saida.txt
  ```
- `>>` redireciona a saída (acrescenta):
  ```bash
  myshell> echo outra linha >> saida.txt
  ```
- `<` redireciona a entrada:
  ```bash
  myshell> cat < saida.txt
  ```

### Pipes
Permite encadear comandos:
```bash
shell> cat < saida.txt | grep Olá
```

### Execução em background
Executa processos em segundo plano com `&`:
```bash
shell> ping google.com & 
```

### Expansão de caminho
- `~` é expandido para o diretório home do usuário
- `./` é expandido para o diretório atual

### Logging da sessão
Cada sessão gera um arquivo de log no formato:
```
myshell-YYYYMMDD-HHmmss.log
```
Comandos executados e mensagens importantes são gravados automaticamente.

---

## Como compilar e executar

```bash
javac Shell.java
java Shell
```

---

## Exemplos de uso

```bash
shell> echo Teste > arquivo.txt
shell> cat < arquivo.txt | grep Teste
shell> echo Adicional >> arquivo.txt
shell> ls
shell> cp arquivo.txt copia.txt
shell> rm arquivo.txt
shell> pwd
shell> cd ..
shell> mkdir nova_pasta
shell> rmdir nova_pasta
shell> ping google.com &
shell> exit
```