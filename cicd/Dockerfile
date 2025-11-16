FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# 复制 Gradle 相关文件
COPY gradle gradle
COPY build.gradle settings.gradle gradlew ./
# 解决 gradlew 在 Linux 中不可执行/行尾为 CRLF 的问题
RUN chmod +x gradlew && sed -i 's/\r$//' gradlew
# 使用独立的 Gradle 缓存目录，便于后续复用与加速
ENV GRADLE_USER_HOME=/workspace/.gradle
COPY libs libs

# 复制源代码
COPY src src

# 构建应用（禁用守护进程，减少网络问题影响）
RUN ./gradlew build -x test --no-daemon
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

FROM eclipse-temurin:17-jre-alpine
VOLUME /tmp

# 添加应用用户
RUN addgroup -S spring && adduser -S spring -G spring

# 复制依赖
COPY --from=build /workspace/app/build/libs/*.jar app.jar

# 设置环境变量
ENV JAVA_OPTS=""

# 设置用户
USER spring:spring

# 启动命令
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]