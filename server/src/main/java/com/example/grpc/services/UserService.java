package com.example.grpc.services;

import com.example.grpc.gerenatedsource.UserGrpc.UserImplBase;
import com.example.grpc.gerenatedsource.UserOuterClass.APIResponse;
import com.example.grpc.gerenatedsource.UserOuterClass.Empty;
import com.example.grpc.gerenatedsource.UserOuterClass.LoginRequest;

import io.grpc.stub.StreamObserver;

/**
 * A classe UserService é uma implementação do serviço gRPC definido no arquivo
 * .proto. Ela estende a classe base gerada automaticamente UserImplBase,
 * fornecendo a lógica para lidar com solicitações gRPC relacionadas à
 * autenticação do usuário.
 */
public class UserService extends UserImplBase {

	/**
	 * Ponto de extremidade para login de usuário, recebe a solicitação de login do
	 * cliente.
	 *
	 * @param request          O objeto LoginRequest contendo as credenciais de
	 *                         login do cliente.
	 * @param responseObserver O StreamObserver para enviar a APIResponse de volta
	 *                         ao cliente.
	 */
	@Override
	public void login(LoginRequest request, StreamObserver<APIResponse> responseObserver) {
		System.out.println("Dentro do login");

		// Extraindo atributos do objeto LoginRequest
		String username = request.getUsername();
		String password = request.getPassword();

		// Construindo o objeto APIResponse para enviar de volta ao cliente
		APIResponse.Builder response = APIResponse.newBuilder();

		// Exemplo básico de lógica de negócios antes de enviar a resposta
		if (username.equals(password)) {
			// Configurando dados de resposta para um login bem-sucedido
			response.setResponseCode(0).setResponsemessage("LOGIN FEITO COM SUCESSO");
		} else {
			// Configurando dados de resposta para uma senha inválida
			response.setResponseCode(100).setResponsemessage("SENHA INVÁLIDA");
		}

		// Enviando a resposta para o cliente usando responseObserver
		responseObserver.onNext(response.build());

		// Fechando a conexão entre o servidor e o cliente
		responseObserver.onCompleted();
	}

	@Override
	public void logout(Empty request, StreamObserver<APIResponse> responseObserver) {
		// TODO: Implementar lógica de logout, se necessário
		super.logout(request, responseObserver);
	}
}
