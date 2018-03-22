package br.ufs.dcomp.chatrabbitmq;

import com.rabbitmq.client.*;

import br.ufs.dcomp.chat.Cliente;
import br.ufs.dcomp.utils.FileUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class Receptor implements IRabbitObject, Runnable {
	private static Channel canal;
    private static String queueName;
    private static String destinatario;
    private static char statusDestinatario;

    public Receptor(Channel channel, String queueName, String destinatario, char statusDestinatario) {
        canal = channel;
        Receptor.queueName = queueName;
        Receptor.destinatario = destinatario;
        Receptor.statusDestinatario = statusDestinatario;
    }

	public void run() {
		Consumer consumer = new DefaultConsumer(canal) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {

                MensagemProto.Mensagem message = MensagemProto.Mensagem.parseFrom(body);

                boolean show = true;

                switch (message.getSender().split("/").length) {
                    case 1:
                        if (message.getSender().split("/")[0].equals(queueName)) 
                            show = false;
                        break;
                    case 2:
                        if (message.getSender().split("/")[1].equals(queueName)) 
                            show = false;
                        break;
                }

                if (show) {
                    if (!Cliente.PROMPT_LINE.equals(">> ")) 
                        System.out.println();

                    if (!message.getContent().getType().equals(FileUtils.TYPE_NONE)) 
                    	System.out.println("File " + message.getContent().getName() + " from " + message.getSender() + " downloaded!");
                    else	
                    	System.out.println("(" + message.getDate() + " às " + message.getTime() + ") " + message.getSender() + " diz: " + message.getContent().getBody().toString(StandardCharsets.UTF_8));

                    if (!Cliente.PROMPT_LINE.equals(">> ")) {
                        System.out.print(Cliente.PROMPT_LINE + ">> ");
                    }

                }

            }
        };
        try {
            canal.basicConsume(queueName, true, consumer);
        } catch (IOException ex) {
        	System.err.println(ex.getStackTrace());
        }
	}	
}