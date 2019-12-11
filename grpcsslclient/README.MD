##先要生成私钥
openssl genrsa -out client.key 2048

##生成请求文件
openssl req -new -key client.key -out client.csr

##发给ca签名
openssl x509 -req -days 3650 -in client.csr -signkey client.key -out client.crt

##crt to pem
openssl x509 -in client.crt -out client.pem -outform PEM