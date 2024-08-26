FROM golang:1.22 as builder 

WORKDIR /app 
COPY go.mod ./

RUN go mod download 
COPY . .

RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o server .

FROM alpine:latest 
ENV token "123456"

WORKDIR /root
COPY --from=builder /app/server .

EXPOSE 8080

CMD ["sh", "-c", "./server -port 8080 -db /db -content /data -token ${token}"] 

