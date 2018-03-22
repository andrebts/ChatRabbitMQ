package br.ufs.dcomp.server;

import java.nio.charset.Charset;

public class Server {

    public final String HOST, USER, PASSWORD, URL;
    public final int PORT;
    public final Charset CHARSET;

    public Server(String host, String user, String password, String url, int port, Charset charset) {
        this.HOST = host;
        this.USER = user;
        this.PASSWORD = password;
        this.URL = url;
        this.PORT = port;
        this.CHARSET = charset;
    }
}
