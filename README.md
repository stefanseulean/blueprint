The communication with RabbitMQ should be through TLS. In order to achieve this, 
the certificates should be generated and asked to be signed by the Security Team.

For RabbitMQ, the certificates are generated with the following command, executed for each environment:
      ./server-csr.sh <service-name>.<region>.<env>.ts.sv   - this command will generate the csr and key
Create a ticket to sign the certificate by adding the csr as attached in the ticket

* Step 1: The client certificate will be generated with the following script:

       ./client-csr.sh <service-name>.<region>.<env>.ts.sv
     
* Step 2: Document Resolution Service will connect to RabbitMQ using a pkcs12 file that will be generated with the following command:

        openssl pkcs12 -export -inkey <service-name>.<region>.<env>.ts.sv.key 
            -in <service-name>.<region>.<env>.ts.sv.pem -out <service-name>.<region>.<env>.ts.sv.pkcs12 -caname <tradeshift-ca-name> 
            -certfile <cafile>
        
* Step 3: Transform PEM to binary DER encoded certificate :
       
       openssl x509 -outform der -in <tradeshift-root-ca>.crt -out <tradeshift-binary-root-ca>.cert
    
* Step 4: Import DER to PKCS12
      
      keytool -import -trustcacerts -alias <alias-name> \
        -file <tradeshift-binary-root-ca>.cert 
        -keystore <service-name>.<region>.<env>.ts.sv.pkcs12  \
        -storetype pkcs12
        
 #### Install Blueprint on cloud stack

 * Step 1: Make sure that kubectl is pointing to Google Kubernetes Engine by running:
      1.  `kubectl config current-context` 
      <br >
      Otherwise set the context with this command:
      2.  `kubectl config use-context <own-context>` 
 
 * Step 2: Navigate under **kubernetes/charts**: 
      1. `helm install --name <release-name> blueprint` 
      

Note: All variables under the kubernetes/deployments should be encrypted:

        docker run --rm docker.tradeshift.net/eyaml [site] [environment] -s "aPasswordToBeEncrypted".
     
