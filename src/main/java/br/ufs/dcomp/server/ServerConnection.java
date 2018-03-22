package br.ufs.dcomp.server;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ExchangeInfo;
import com.rabbitmq.http.client.domain.QueueInfo;
import com.rabbitmq.http.client.domain.UserInfo;

import br.ufs.dcomp.utils.VariaveisGlobais;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ServerConnection {
    static Connection CONN;
    public static Channel CHANNEL;
    public Client Client;
    //public final String URI = "amqp://uualtybn:91LUxmLOnXYsIUkg5oTR4TuWbmwVArIu@spider.rmq.cloudamqp.com/uualtybn";
    
    public ServerConnection() throws IOException, TimeoutException, KeyManagementException, NoSuchAlgorithmException, URISyntaxException {
    	String host = "35.161.28.111";
    	String user = "chatserver4285";
    	String pass = "P@sW0@djdfm563";

    	ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host);
		factory.setPort(5672);// 
		factory.setUsername("chatserver4285");
		factory.setPassword("P@sW0@djdfm563");
		ServerConnection.CONN = factory.newConnection();
		ServerConnection.CHANNEL = ServerConnection.CONN.createChannel();
		
    	Client = new Client("http://" + host + ":15672/api/", user, pass);
    }

    public Channel getChannel() {
        return CHANNEL;
    }

    public void queueDeclare(String queueName) throws IOException {
        CHANNEL.queueDeclare(queueName, false, false, false, null);
    }

    public void createUserQueue(String userName) throws IOException {
        CHANNEL.queueDeclare(userName, false, false, false, null);
        CHANNEL.queueDeclare(userName + VariaveisGlobais.fileQueuSufix, false, false, false, null);
    }

    public void createGroup(String userName, String groupName) throws IOException {
        CHANNEL.exchangeDeclare(groupName, "fanout");
        CHANNEL.exchangeDeclare(groupName + VariaveisGlobais.fileQueuSufix, "fanout");
        CHANNEL.queueBind(userName, groupName, "");
        CHANNEL.queueBind(userName, groupName + VariaveisGlobais.fileQueuSufix, "");
        System.out.println("Grupo " + groupName + " criado");
    }
    
    public void addUserToGroup(String userName, String groupName) throws IOException {
        CHANNEL.queueBind(userName, groupName, "");
        CHANNEL.queueBind(userName, groupName + VariaveisGlobais.fileQueuSufix, "");
        System.out.println(userName + " foi adicionado ao grupo " + groupName);
    }

    public void removeUserFromGroup(String userName, String groupName) throws IOException {
        CHANNEL.queueUnbind(userName, groupName, "");
        CHANNEL.queueUnbind(userName + VariaveisGlobais.fileQueuSufix, groupName, "");
        System.out.println(userName + " foi removido do grupo " + groupName);
    }
    
    public void deleteGroup(String groupName) throws IOException {
        CHANNEL.exchangeDelete(groupName);
        CHANNEL.exchangeDelete(groupName + VariaveisGlobais.fileQueuSufix);
        System.out.println("O grupo " + groupName + " foi detelado");
    }
    
    public void listUsers() throws IOException {
    	List<QueueInfo> queues = Client.getQueues();
    	String usr = "";
    	for (int i = 0; i < queues.size() - 1; i++) {
    		if (!queues.get(i).getName().contains(VariaveisGlobais.fileQueuSufix) && queues.get(i).getName().length() > 0) {
        		usr += queues.get(i).getName() + ", ";
			}
		}
    	
		if (!queues.get(queues.size() - 1).getName().contains(".") && queues.get(queues.size() - 1).getName().length() > 0) {
			usr += queues.get(queues.size() - 1).getName();
		}
    	
        System.out.println(usr);
    }
    
    public void listGroups() throws IOException {
    	List<ExchangeInfo> exchanges = Client.getExchanges();
    	String exs = "";
    	for (int i = 0; i < exchanges.size() - 1; i++) {
    		if (!exchanges.get(i).getName().contains(".") && exchanges.get(i).getName().length() > 0) {
        		exs += exchanges.get(i).getName() + ", ";
			}
		}
    	
		if (!exchanges.get(exchanges.size() - 1).getName().contains(".") && exchanges.get(exchanges.size() - 1).getName().length() > 0) 
			exs += exchanges.get(exchanges.size() - 1).getName();
    	
        System.out.println(exs);
    }

    public void close() throws IOException, TimeoutException {
        CHANNEL.close();
        CONN.close();
    }
}
