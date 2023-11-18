package com.example.grpcclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.grpcclient.gerenatedsource.UserGrpc;
import com.example.grpcclient.gerenatedsource.UserGrpc.UserStub;
import com.example.grpcclient.gerenatedsource.UserOuterClass.APIResponse;
import com.example.grpcclient.gerenatedsource.UserOuterClass.LoginRequest;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class GrpcClientApplication {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) {
        SpringApplication.run(GrpcClientApplication.class, args);

        // Criar e inicializar o canal gRPC
        ManagedChannel channel = createChannel();

        // Criar um cliente gRPC
        GrpcClient grpcClient = new GrpcClient(channel);

        // Realizar chamadas assíncronas do método de login e medir o tempo
        long startTime = System.currentTimeMillis();

        // Realizar as chamadas
        grpcClient.performMultipleLogins();

        // Aguardar a conclusão de todas as chamadas antes de imprimir o tempo total
        grpcClient.awaitCompletion();
        long endTime = System.currentTimeMillis();
        long totalTimeInSeconds = (endTime - startTime) / 1000;
        System.out.println("Tempo total gasto: " + totalTimeInSeconds + " segundos");

        // Fechar o canal gRPC
        channel.shutdown();
    }

    private static ManagedChannel createChannel() {
        return ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT).usePlaintext().build();
    }
}

class GrpcClient {

    private static final int NUM_REQUESTS = 100000;

    private final UserStub userStub;
    private final CountDownLatch latch;
    private final AtomicInteger requestCount;

    public GrpcClient(ManagedChannel channel) {
        this.userStub = UserGrpc.newStub(channel);
        this.latch = new CountDownLatch(NUM_REQUESTS);
        this.requestCount = new AtomicInteger(0);
    }

    public void performMultipleLogins() {
        System.out.println("Realizando " + NUM_REQUESTS + " chamadas assíncronas do método de login");

        for (int i = 0; i < NUM_REQUESTS; i++) {
            final int index = i;
            userStub.login(createLoginRequest("teste", "teste"), new StreamObserver<APIResponse>() {
                @Override
                public void onNext(APIResponse loginResponse) {
                    handleLoginResponse(index, loginResponse);
                }

                @Override
                public void onError(Throwable t) {
                    handleError(t);
                }

                @Override
                public void onCompleted() {
                    latch.countDown();
                }
            });
        }
    }

    public void awaitCompletion() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleLoginResponse(int index, APIResponse loginResponse) {
        System.out.println("Recebida resposta " + index + " de login: " + loginResponse);
        requestCount.incrementAndGet();

        // Verificar se todas as chamadas foram concluídas antes de imprimir o número total
        if (requestCount.get() == NUM_REQUESTS) {
            System.out.println("Chamadas de login concluídas. Número total de requisições: " + requestCount.get());
        }
    }

    private void handleError(Throwable t) {
        System.err.println("Erro durante o login: " + t.getMessage());
        latch.countDown();
    }

    private LoginRequest createLoginRequest(String username, String password) {
        return LoginRequest.newBuilder().setUsername(username).setPassword(password).build();
    }
}
