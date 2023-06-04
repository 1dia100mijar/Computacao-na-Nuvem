Para executar o programa é necessário:

* Definir com as permissoes de  Cloud datastore Owner, Storage Admin,
  Pub/Sub Admin e VisionAI Admin a variável de sistema `GOOGLE APPLICATION CREDENTIALS`
referenciando o ficheiro json com as chave da conta de serviço com estas permissões.
* Definir a variável `apiKey` no ficheiro `LandmarkDetector` com a api key para aceder ao MapsStaticAPI
* Compilar o `jar` da aplicação através da função `install` do Maven
* Correr o ficheiro `LandmarkDetector-1.0-jar-with-dependencies.jar` abrindo um terminal na diretoria `LandmarkDetector/target`
  e executando o código
> java -jar LandmarkDetector-1.0-jar-with-dependencies.jar