### 1. 프로젝트 빌드
```shell
 ./gradlew build
```
### 2. 이미지 빌드
```shell
 docker buildx build -t gateway .
```
### 3. 컨테이너 실행 (윈도우 환경)
```shell
 docker run --name gateway -p 8081:8080 -v C:\properties:/share gateway
```