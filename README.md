# Rest Api Currency-Exchange-Rates
Api Rest para consultar las divisas entre las monedas de dos paises, a partir de consultar por el codigo de pais

# CONSTRUCCION Y EJECUCION DE ESTE PROYECTO
1. Sistema operativo Ubuntu 14.0
2. Java jdk 1.7.0_65
3. Servidor Tomcat 7.0.27
4. Es necesario añadir un certificado al almacen de claves de la JVM para acceder al sitio: https://restcountries.eu/
5. Puede descargar el certificado desde el navegador o usar el fichero .cer que esta en la ruta de resources del proyecto
6. Para instalar el certificado ejecute el siguiente comando: keytool -import -alias [unalias] -file path/cert.cer -keystore path/jdk/../certs -storepass changeit
7. Building con Maven 4.0.0
8. El despliegue del fichero .war, esta destinado a la ruta: /opt/tomcat/webapps/
9. Copiar el fichero .war en la ruta webapps del servidor tomcat y arrancar el servidor
10. Ejecute en un navegador o cliente Rest Api la siguiente request: http://localhost:8090/cer-services/rest/json/foreignexchange/countries?countrySource=esp&countryTarget=gbr
11. La respuesta esperada es Json : {"currencySource":"EUR","currencyTarget":"GBP","rate":"0.8426","status_code":"200","status_text":"OK"}




