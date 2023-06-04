Depois de desenvolver o projeto localmente foi feito os seguintes passos:

* Correr a função package do Mavem para criar o jar
* Aberto um terminal na pasta do projeto e usado o seguinte comando:
  * > gcloud functions deploy LookupFunction --allow-unauthenticated --entry-point=cn2223tf.Entrypoint --runtime=java11 --trigger-http --region=us-central1 --service-account=cn2223tf-lookup@cn2223-t2-g06.iam.gserviceaccount.com --max-instances=3 --project=cn2223-t2-g06
