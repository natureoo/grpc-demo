grpc ssl demo, 可使用haproxy route request

##使用openssl工具生成一个Root CA 私钥
openssl genrsa -out ca.key 1024

##生成Root CA证书, Comman Name 为root.com
openssl req -new -x509 -days 3650 -subj "/CN=root.com"    -key ca.key -out ca.crt

##生成server端私钥
openssl genrsa -out grpcserver.key.rsa 1024

##server端私钥必须无密码
openssl pkcs8 -topk8 -in grpcserver.key.rsa -out grpcserver.key -nocrypt

##生成server端证书csr, Comman Name 为demo.com, 该comman name不能与Root CA common name相同
openssl req -new -subj "/CN=demo.com" -key grpcserver.key -out grpcserver.csr

##生成server端证书
openssl x509 -req -sha256     -CA ca.crt -CAkey ca.key -CAcreateserial -days 3650     -in grpcserver.csr     -out grpcserver.crt


cat grpcserver.crt grpcserver.key > grpcserver.pem

##verify certs
openssl verify -verbose -CAfile ca.crt grpcserver.crt

##查看证书内容
openssl x509 -in ca.crt -text -noout
openssl x509 -in grpcserver.crt -text -noout
