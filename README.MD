

##--------server端-----------------------
##使用openssl工具生成一个Root CA 私钥
openssl genrsa -out ca.key 2048

##生成Root CA证书, Comman Name 为root.com
openssl req -new -x509 -days 3650 -subj "/CN=root.com"    -key ca.key -out ca.crt

##生成server端私钥
openssl genrsa -out grpcserver.key.rsa 2048

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


##--------client端-----------------------

##使用openssl工具生成一个client Root CA 私钥
openssl genrsa -out client-ca.key 2048

##生成client Root CA证书, Comman Name 为clientroot.com
openssl req -new -x509 -days 3650 -subj "/CN=clientroot.com"    -key client-ca.key -out client-ca.crt

##生成client端私钥
openssl genrsa -out grpcclient.key.rsa 2048

##client端私钥必须无密码
openssl pkcs8 -topk8 -in grpcclient.key.rsa -out grpcclient.key -nocrypt

##生成client端证书csr, Comman Name clientdemo.com, 该comman name不能与Root CA common name相同
openssl req -new -subj "/CN=clientdemo.com" -key grpcclient.key -out grpcclient.csr

##生成client端证书
openssl x509 -req -sha256     -CA client-ca.crt -CAkey client-ca.key -CAcreateserial -days 3650     -in grpcclient.csr     -out grpcclient.crt


cat grpcclient.crt grpcclient.key > grpcclient.pem


##verify certs
openssl verify -verbose -CAfile client-ca.crt grpcclient.crt



##ecdsa
openssl ecparam -genkey -name prime256v1 -out nginx02-orderer.key


openssl req -new -SHA256 -subj "/C=US/ST=California/L=San Francisco/CN=orderer.example.com" -key nginx02-orderer.key -nodes -out nginx02-orderer.csr


openssl req -in nginx02-orderer.csr -noout -text

openssl x509 -req -SHA256  -days 3650 -in nginx02-orderer.csr -CA tlsca.example.com-cert.pem -CAkey a2b5b526ffa0bc893ef07aca51fa7b915577bcdde9b770b29934676d12507c09_sk -CAcreateserial -out nginx02-orderer.crt



openssl x509 -req -SHA256 -extfile v3.ext -days 3650 -in nginx01-peer02.csr -CA tlsca.org1.example.com-cert.pem -CAkey 666612008420107322b873f3f9ee9cf9ceae036ad88070846503c2c42ca245bb_sk -CAcreateserial -out nginx01-peer02-v3.crt
