package br.ufs.dcomp.chatrabbitmq;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.GregorianCalendar;

import javax.activation.MimeType;

import org.json.JSONObject;

import com.google.protobuf.ByteString;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import br.ufs.dcomp.chatrabbitmq.MensagemProto.Mensagem;
import br.ufs.dcomp.chatrabbitmq.MensagemProto.Mensagem.Conteudo;
import br.ufs.dcomp.utils.FileUtils;
import br.ufs.dcomp.utils.VariaveisGlobais;

public class Emissor implements IRabbitObject{
	private static GregorianCalendar calendario = new GregorianCalendar();
   
    private static String getDate() {
    	String date = Integer.toString(calendario.get(GregorianCalendar.DAY_OF_MONTH)) + "/" + 
        		Integer.toString(calendario.get(GregorianCalendar.MONTH)) + "/" + 
        		Integer.toString(calendario.get(GregorianCalendar.YEAR));
    	return date;
    }
    
    private static String getTime() {
    	String time = Integer.toString(calendario.get(GregorianCalendar.HOUR_OF_DAY)) + ":" + 
        		Integer.toString(calendario.get(GregorianCalendar.MINUTE));
    	return time;
    }

    private static Mensagem formatMsg(String message, String user, String group) {    	
    	MensagemProto.Mensagem mensagem = MensagemProto.Mensagem.newBuilder()
    	    .setSender(user)
    	    .setDate(getDate())
    	    .setTime(getTime())
    	    .setGroup(group)
    	    .setContent(
    	    		Conteudo.newBuilder()
    	    		.setType(FileUtils.TYPE_NONE)
    	    		.setBody(ByteString.copyFrom(message, StandardCharsets.UTF_8))
    	    		.build())
    	    .build();

        return mensagem;
    }
    
    private static Mensagem formatMsgFile(String path, String user, String group) throws IOException{    	
    	MensagemProto.Mensagem mensagem = MensagemProto.Mensagem.newBuilder()
		    .setSender(user)
		    .setDate(getDate())
		    .setTime(getTime())
		    .setGroup(group)
		    .setContent(
		    		Conteudo.newBuilder()
		    		.setType(FileUtils.TYPE_TEXT)
		    		.setName(path)
		    		.setBody(ByteString.copyFrom(FileUtils.read(path)))
		    		.build())
		    .build();

        return mensagem;
    }

    public static void sendToUser(Channel channel, String sender, String destination, String message) throws UnsupportedEncodingException, IOException {
        Mensagem msg = formatMsg(message, sender, "");
        channel.basicPublish("", destination, null, msg.toByteArray());
    }

    public static void sendToGroup(Channel channel, String sender, String group, String message) throws UnsupportedEncodingException, IOException {
    	Mensagem msg = formatMsg(message, sender, group + "/");
        channel.basicPublish(group, "", null, msg.toByteArray());
    }
    
    public static void sendFileToUser(final Channel channel, final String sender, final String destination, final String path) throws UnsupportedEncodingException, IOException {
    	Thread t1 = new Thread(new Runnable() {
    	    public void run()
    	    {
    	        try {
        	    	Mensagem msg = formatMsgFile(path, sender, "");
					channel.basicPublish("", destination, null, msg.toByteArray());
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("Ocorreu um erro no envio do arquivo " + path + " para " + destination);
				}
    	    }});  
    	    t1.start();    	
    }

    public static void sendFileToGroup(final Channel channel, final String sender, final String group, final String path) throws UnsupportedEncodingException, IOException {
    	Thread t1 = new Thread(new Runnable() {
    	    public void run()
    	    {
    	        try {
        	    	Mensagem msg = formatMsgFile(path, sender, group + "/");

    	        	if (msg != null) {
    	        		channel.basicPublish(group + VariaveisGlobais.fileQueuSufix, "", null, msg.toByteArray());
    	        		System.out.println("File " + path + " is now available to " + sender + "!\n");
    	        	}
    	        	else
    	        		System.err.println("Não foi possivel carregar o arquivo");
				} catch (IOException e) {
					System.err.println("Ocorreu um erro no envio do arquivo " + path + " para " + group);
				}
    	    }});  
    	    t1.start();   
    }
}