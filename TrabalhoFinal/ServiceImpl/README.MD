Para executar o programa é necessário:

* Definir com as permissoes de  Cloud datastore Owner, Storage Admin e Pub/Sub Admin a variável de sistema `GOOGLE APPLICATION CREDENTIALS`
  referenciando o ficheiro json com as chave da conta de serviço com estas permissões.
* Importar a dependência do ficheiro .jar gerado no Contrato do serviço.
* Compilar o `jar` da aplicação através da função `install` do Maven
* Correr o ficheiro `ServiceImpl-1.0-jar-with-dependencies.jar` abrindo um terminal na diretoria `ServiceImpl/target`
  e executando o código
> java -jar ServiceImpl-1.0-jar-with-dependencies.jar