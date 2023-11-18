package com.example.grpc;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.grpc.services.UserService;

import io.grpc.Server;
import io.grpc.ServerBuilder;

@SpringBootApplication
public class GrpcApplication {

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(GrpcApplication.class, args);

		startGrpcServer();

	}

	/**
	 /**
     * Inicializa o servidor gRPC e define a porta.
     *
	 */
	private static void startGrpcServer() throws IOException, InterruptedException {
		
		System.out.println("Iniciando o servidor GRPC...");

		int grpcPort = 9090;

		Server server = ServerBuilder.forPort(grpcPort).addService(new UserService()) 
				.build();

		server.start();
		System.out.println("O servidor GRPC foi iniciado na porta: " + grpcPort);
		server.awaitTermination();
	}

}
