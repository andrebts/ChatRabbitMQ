package br.ufs.dcomp.chat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

import br.ufs.dcomp.chatrabbitmq.Emissor;
import br.ufs.dcomp.chatrabbitmq.Receptor;
import br.ufs.dcomp.server.ServerConnection;

public class Cliente {
    public static String PROMPT_LINE = ">> ";

    static public void main(String args[]) throws IOException, TimeoutException, InterruptedException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
        String message;

    	ServerConnection serverConnection = new ServerConnection();
        Scanner input = new Scanner(System.in);

        System.out.print("User: ");
        String user = input.nextLine();
      
        serverConnection.createUserQueue(user);
        
        // Destinatario: I(invalido) G(Grupo) P(Privado)
        char statusDestinatario = 'I';
        String destinatario = "";

        Receptor receiver = new Receptor(serverConnection.getChannel(), user, destinatario, statusDestinatario);
        Thread receiving = new Thread(receiver);
        receiving.start();

        Thread.sleep(250);

        do {
            switch (statusDestinatario) {
                case 'G':
                    System.out.print(destinatario + " (grupo)>> ");
                    break;

                case 'P':
                    System.out.print(destinatario + ">> ");
                    break;
                default:
                    if (PROMPT_LINE.equals(">> ")) {
                        System.out.print(PROMPT_LINE);
                    }

            }
            message = input.nextLine();

            switch (message.split(" ")[0]) {
                case "!addGroup":
                    serverConnection.createGroup(user, message.split(" ")[1]);
                    break;

                case "!addUser":
                    serverConnection.addUserToGroup(message.split(" ")[1], message.split(" ")[2]);
                    break;

                case "!delFromGroup":
                    serverConnection.removeUserFromGroup(message.split(" ")[1], message.split(" ")[2]);
                    break;
                    
                case "!removeGroup":
                    serverConnection.deleteGroup(message.split(" ")[1]);
                    break;

                case "!upload":
                	/*if (message.length() > 2 && ("@".equals(message.substring(0, 1)) || "#".equals(message.substring(0, 1)))) {
                        if ("#".equals(message.substring(0, 1))) {
                            statusDestinatario = 'G';
                            destinatario = message.substring(1, message.length());
                            serverConnection.queueDeclare(destinatario);
                            PROMPT_LINE = destinatario + " (grupo)";
                        } else {
                            statusDestinatario = 'P';
                            destinatario = message.substring(1, message.length());
                            serverConnection.queueDeclare(destinatario);
                            PROMPT_LINE = destinatario;
                        }

                    } else */
                	if (statusDestinatario != 'I') {
                		String path = message.split(" ")[1];
                        if (statusDestinatario == 'P') {
                            Emissor.sendFileToUser(serverConnection.getChannel(), user, destinatario, path);
                        } else {
                        	Emissor.sendFileToGroup(serverConnection.getChannel(), user, destinatario, path);
                        }
                        System.out.println("Uploading file " + path + " to " + destinatario + " ...");
                    } else {
                        statusDestinatario = 'I';
                        destinatario = "";
                        System.out.println("Informe o destinatário da mensagem!");
                    }
                	
                    break;
                    
                case "!listUsers":
                    serverConnection.listUsers();
                    break;
                
                case "!listGroup":
                    serverConnection.listGroups();
                    break;
                    
                case "!exit":
                    break;

                default:
                    if (message.length() > 2 && ("@".equals(message.substring(0, 1)) || "#".equals(message.substring(0, 1)))) {
                        if ("#".equals(message.substring(0, 1))) {
                            statusDestinatario = 'G';
                            destinatario = message.substring(1, message.length());
                            serverConnection.queueDeclare(destinatario);
                            PROMPT_LINE = destinatario + " (grupo)";
                        } else {
                            statusDestinatario = 'P';
                            destinatario = message.substring(1, message.length());
                            serverConnection.queueDeclare(destinatario);
                            PROMPT_LINE = destinatario;
                        }

                    } else if (statusDestinatario != 'I') {
                        if (statusDestinatario == 'P') {
                            Emissor.sendToUser(serverConnection.getChannel(), user, destinatario, message);
                        } else {
                        	Emissor.sendToGroup(serverConnection.getChannel(), user, destinatario, message);
                        }
                    } else {
                        statusDestinatario = 'I';
                        destinatario = "";
                        System.out.println("Informe o destinatário da mensagem!");
                    }

                    break;
            }

        } while (!message.equals("!exit"));

        input.close();
        serverConnection.close();
    }
}
