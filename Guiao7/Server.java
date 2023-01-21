import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.Arrays.asList;

class ContactManager {

    private HashMap<String, Contact> contacts;
    private ReentrantLock l = new ReentrantLock();

    public ContactManager() {
        contacts = new HashMap<>();
        // example pre-population
        update(new Contact("John", 20, 253123321, null, asList("john@mail.com")));
        update(new Contact("Alice", 30, 253987654, "CompanyInc.", asList("alice.personal@mail.com", "alice.business@mail.com")));
        update(new Contact("Bob", 40, 253123456, "Comp.Ld", asList("bob@mail.com", "bob.work@mail.com")));
    }

    public void update(Contact c) {
        l.lock();
        try{
            contacts.put(c.getName(),c);
        }
        finally {
            l.unlock();
        }
    }


    public ContactList getContacts() {
        l.lock();
        try{
            ContactList cl = new ContactList();
            cl.addAll(contacts.values());
            return cl;
        }
        finally {
            l.unlock();
        }
    }


}

class ServerWorker implements Runnable {

    private Socket socket;
    private ContactManager manager;

    public ServerWorker (Socket socket, ContactManager manager) {
        this.socket = socket;
        this.manager = manager;
    }

    @Override
    public void run() {

        try{
            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            ContactList cl = this.manager.getContacts();
            cl.serialize(out);
            out.flush();
            socket.shutdownOutput();

            while (true){
                Contact c = Contact.deserialize(in);
                if (c==null) break;
                manager.update(c);
            }

            socket.shutdownInput();
            socket.close();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }


}



public class Server {

    public static void main (String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        ContactManager manager = new ContactManager();

        while (true) {
            Socket socket = serverSocket.accept();
            Thread worker = new Thread(new ServerWorker(socket, manager));
            worker.start();
        }
    }


}
